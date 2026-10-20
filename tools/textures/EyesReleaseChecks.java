import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import javax.imageio.ImageIO;

/** Read-only verification of the exact published bytes, browser CORS, and cache policy. */
public final class EyesReleaseChecks {
    private static final HttpClient HTTP = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15)).build();
    private static final String[] MIRRORS = {
        "https://files.fc.dirzaaulia.com/countries/textures/eyes-v1/",
        "https://files.168.110.196.30.sslip.io/countries/textures/eyes-v1/"
    };

    public static void main(String[] args) throws Exception {
        Path release = Path.of(args.length == 0 ? "artifacts/nasa-eyes/eyes-v1" : args[0]);
        for (String mirror : MIRRORS) {
            for (String entry : Files.readAllLines(release.resolve("SHA256SUMS.txt"))) {
                if (entry.isBlank()) continue;
                String[] parts = entry.trim().split("\\s+", 2);
                checkMap(mirror, parts[1], parts[0]);
            }
            checkPreflight(mirror + "earth_day.jpg");
        }
        System.out.println("COMPLETE: both mirrors, 24 image hashes/dimensions, CORS/cache, OPTIONS");
    }

    private static void checkMap(String mirror, String filename, String expected) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(mirror + filename))
            .timeout(Duration.ofSeconds(60)).header("Origin", "https://countries.example").GET().build();
        HttpResponse<byte[]> response = HTTP.send(request, HttpResponse.BodyHandlers.ofByteArray());
        require(response.statusCode() == 200, "HTTP " + response.statusCode() + ": " + filename);
        require(header(response, "content-type").startsWith("image/jpeg"), "JPEG MIME: " + filename);
        require(header(response, "access-control-allow-origin").equals("*"), "CORS: " + filename);
        String cache = header(response, "cache-control");
        require(cache.contains("immutable") && cache.contains("31536000"), "Cache: " + filename);
        String actual = HexFormat.of().formatHex(
            MessageDigest.getInstance("SHA-256").digest(response.body()));
        require(actual.equals(expected), "Hash mismatch: " + filename);
        try (var bytes = new ByteArrayInputStream(response.body())) {
            var image = ImageIO.read(bytes);
            require(image != null && image.getWidth() == 2048 && image.getHeight() == 1024,
                "Decode/dimensions: " + filename);
        }
        System.out.println("PASS " + mirror + filename + " " + actual);
    }

    private static void checkPreflight(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(20)).header("Origin", "https://countries.example")
            .header("Access-Control-Request-Method", "GET")
            .header("Access-Control-Request-Headers", "Range")
            .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build();
        var response = HTTP.send(request, HttpResponse.BodyHandlers.discarding());
        require(response.statusCode() == 204, "OPTIONS status: " + url);
        require(header(response, "access-control-allow-origin").equals("*"), "OPTIONS CORS");
        require(header(response, "access-control-allow-methods").contains("GET"), "OPTIONS methods");
        require(header(response, "access-control-allow-headers").contains("Range"), "OPTIONS Range");
    }

    private static String header(HttpResponse<?> response, String name) {
        return response.headers().firstValue(name).orElse("");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
