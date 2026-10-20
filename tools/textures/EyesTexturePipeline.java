import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/** Offline asset build tool. Does not change renderer geometry, camera math, or application source. */
public final class EyesTexturePipeline {
    private static final String BASE = "https://eyes.nasa.gov/assets/static/maps/";
    private static final int WIDTH = 2048;
    private static final HttpClient HTTP = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(20)).build();
    private static final List<Job> JOBS = jobs();
    private record Job(String body, String layer, String filename, int size, boolean mask) {}

    public static void main(String[] args) throws Exception {
        CubeProjectionChecks.run();
        Path root = Path.of(args.length == 0 ? "artifacts/nasa-eyes" : args[0]);
        Path originals = root.resolve("cube-faces");
        Path output = root.resolve("eyes-v1");
        Files.createDirectories(originals);
        Files.createDirectories(output);
        List<String> sources = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        List<BufferedImage> previews = new ArrayList<>();
        for (Job job : JOBS) process(job, originals, output, sources, hashes, previews);
        Files.write(output.resolve("SHA256SUMS.txt"), hashes, StandardCharsets.UTF_8);
        Files.write(output.resolve("SOURCES.tsv"), sources, StandardCharsets.UTF_8);
        ImageIO.write(contactSheet(previews), "png", root.resolve("contact-sheet.png").toFile());
        System.out.println("COMPLETE: 12 maps, 2048x1024, source/hash manifests, contact sheet");
    }

    private static List<Job> jobs() {
        List<Job> jobs = new ArrayList<>();
        for (String body : List.of("sun", "mercury", "venus", "earth", "moon", "mars",
                "jupiter", "saturn", "uranus", "neptune")) {
            String filename = body.equals("earth") ? "earth_day.jpg" : body + ".jpg";
            int size = body.equals("uranus") || body.equals("neptune") ? 256 : 512;
            jobs.add(new Job(body, "color", filename, size, false));
        }
        jobs.add(new Job("earth", "night", "earth_night.jpg", 512, false));
        jobs.add(new Job("earth", "cloud", "earth_clouds.jpg", 512, true));
        return jobs;
    }

    private static void process(Job job, Path originals, Path output, List<String> sources,
            List<String> hashes, List<BufferedImage> previews) throws Exception {
        BufferedImage[] faces = new BufferedImage[6];
        for (int face = 0; face < 6; face++) faces[face] = source(job, face, originals, sources);
        if (job.mask()) describeAlpha(faces);
        BufferedImage result = new CubeProjection(faces).render(WIDTH, job.mask());
        CubeProjectionChecks.validateMap(job.filename(), result);
        Path destination = output.resolve(job.filename());
        writeJpeg(result, destination);
        BufferedImage decoded = ImageIO.read(destination.toFile());
        CubeProjectionChecks.validateMap(job.filename(), decoded);
        hashes.add(sha256(Files.readAllBytes(destination)) + "  " + job.filename());
        previews.add(decoded);
        System.out.printf("READY %s 2048x1024 %d bytes%n", job.filename(), Files.size(destination));
    }

    private static BufferedImage source(Job job, int face, Path originals, List<String> sources)
            throws Exception {
        String extension = job.body().equals("jupiter") ? "jpg" : "png";
        String name = job.layer() + "_" + job.size() + "_" + face + "." + extension;
        String url = BASE + job.body() + "/" + name;
        Path directory = originals.resolve(job.body());
        Files.createDirectories(directory);
        Path file = directory.resolve(name);
        if (!Files.exists(file)) download(url, file);
        BufferedImage image = ImageIO.read(file.toFile());
        if (image == null || image.getWidth() != job.size() || image.getHeight() != job.size()) {
            throw new IOException("Invalid cube face: " + file);
        }
        sources.add(job.filename() + "\t" + face + "\t" + url + "\t"
            + sha256(Files.readAllBytes(file)));
        return image;
    }

    private static void download(String url, Path file) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(90)).GET().build();
        HttpResponse<byte[]> response = HTTP.send(request, HttpResponse.BodyHandlers.ofByteArray());
        String type = response.headers().firstValue("content-type").orElse("");
        if (response.statusCode() != 200 || !type.startsWith("image/")) {
            throw new IOException("Invalid response: " + response.statusCode() + " " + url);
        }
        try (var input = new java.io.ByteArrayInputStream(response.body())) {
            if (ImageIO.read(input) == null) throw new IOException("Image decode failed: " + url);
        }
        Files.write(file, response.body());
    }

    private static void writeJpeg(BufferedImage image, Path destination) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        try (ImageOutputStream stream = ImageIO.createImageOutputStream(destination.toFile())) {
            writer.setOutput(stream);
            ImageWriteParam parameters = writer.getDefaultWriteParam();
            parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            parameters.setCompressionQuality(0.94f);
            writer.write(null, new IIOImage(image, null, null), parameters);
        } finally {
            writer.dispose();
        }
    }

    private static String sha256(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    private static void describeAlpha(BufferedImage[] faces) {
        int min = 255;
        int max = 0;
        for (BufferedImage face : faces) {
            for (int y = 0; y < face.getHeight(); y++) {
                for (int x = 0; x < face.getWidth(); x++) {
                    int alpha = face.getRGB(x, y) >>> 24;
                    min = Math.min(min, alpha);
                    max = Math.max(max, alpha);
                }
            }
        }
        System.out.printf("CLOUD source alpha range=%d..%d; output RGB=alpha*luminance%n", min, max);
    }

    private static BufferedImage contactSheet(List<BufferedImage> maps) {
        BufferedImage sheet = new BufferedImage(1280, 576, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = sheet.createGraphics();
        graphics.setColor(new Color(10, 14, 24));
        graphics.fillRect(0, 0, sheet.getWidth(), sheet.getHeight());
        for (int i = 0; i < maps.size(); i++) {
            int x = i % 4 * 320;
            int y = i / 4 * 192;
            graphics.setColor(Color.WHITE);
            graphics.drawString(JOBS.get(i).filename(), x + 8, y + 19);
            graphics.drawImage(maps.get(i), x, y + 28, 320, 160, null);
        }
        graphics.dispose();
        return sheet;
    }
}
