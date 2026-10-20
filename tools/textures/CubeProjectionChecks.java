import java.awt.image.BufferedImage;

/** Analytic direction, seam, polar, cloud-channel, and geographic orientation checks. */
final class CubeProjectionChecks {
    static void run() {
        BufferedImage[] faces = analyticFaces(32);
        CubeProjection projection = new CubeProjection(faces);
        checkCardinalFaces();
        for (double latitude : new double[] {-89.9, -60, -25, 0, 35, 60, 89.9}) {
            for (double longitude = -180; longitude <= 180; longitude += 15) {
                double[] direction = CubeProjection.direction(Math.toRadians(latitude),
                    Math.toRadians(longitude));
                int actual = projection.sample(direction, false);
                assertColorClose(actual, encode(direction), 3, "Analytic longitude/latitude sample");
            }
        }
        for (double latitude : new double[] {-85, -45, 0, 45, 85}) {
            double radians = Math.toRadians(latitude);
            int left = projection.sample(CubeProjection.direction(radians, -Math.PI), false);
            int right = projection.sample(CubeProjection.direction(radians, Math.PI), false);
            assertColorClose(left, right, 1, "Antimeridian seam");
        }
        for (double angle : new double[] {-135, -45, 45, 135}) {
            int left = projection.sample(CubeProjection.direction(0, Math.toRadians(angle - 0.0001)), false);
            int right = projection.sample(CubeProjection.direction(0, Math.toRadians(angle + 0.0001)), false);
            assertColorClose(left, right, 1, "Cube-face seam");
        }
        checkCloudMask();
        System.out.println("CHECKS PASSED: 175 analytic samples, axes, poles, face/UV seams, cloud alpha");
    }

    private static BufferedImage[] analyticFaces(int size) {
        BufferedImage[] result = new BufferedImage[6];
        for (int face = 0; face < 6; face++) {
            result[face] = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    double u = 2 * (x + 0.5) / size - 1;
                    double v = 1 - 2 * (y + 0.5) / size;
                    double[] direction = new double[3];
                    for (int axis = 0; axis < 3; axis++) {
                        direction[axis] = CubeProjection.FRAMES[face][2][axis]
                            + CubeProjection.FRAMES[face][0][axis] * u
                            + CubeProjection.FRAMES[face][1][axis] * v;
                    }
                    result[face].setRGB(x, y, encode(direction));
                }
            }
        }
        return result;
    }

    private static int encode(double[] direction) {
        double length = Math.sqrt(direction[0] * direction[0]
            + direction[1] * direction[1] + direction[2] * direction[2]);
        int value = 0;
        for (int axis = 0; axis < 3; axis++) {
            value |= (int) Math.round(127.5 * (direction[axis] / length + 1)) << (16 - axis * 8);
        }
        return value;
    }

    private static void checkCardinalFaces() {
        double[][] axes = {{1, 0, 0}, {0, 1, 0}, {-1, 0, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
        for (int face = 0; face < 6; face++) {
            if (CubeProjection.faceIndex(axes[face]) != face) {
                throw new AssertionError("NASA Eyes cube face order changed");
            }
        }
    }

    private static void assertColorClose(int actual, int expected, int tolerance, String message) {
        for (int shift : new int[] {16, 8, 0}) {
            if (Math.abs((actual >> shift & 255) - (expected >> shift & 255)) > tolerance) {
                throw new AssertionError(message + ": " + Integer.toHexString(actual)
                    + " != " + Integer.toHexString(expected));
            }
        }
    }

    private static void checkCloudMask() {
        BufferedImage[] faces = new BufferedImage[6];
        for (int i = 0; i < 6; i++) {
            faces[i] = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 2; x++) faces[i].setRGB(x, y, 0x80ffffff);
            }
        }
        int mask = new CubeProjection(faces).sample(new double[] {1, 0, 0}, true);
        assertColorClose(mask, 0x808080, 0, "White half-alpha clouds must become half-gray coverage");
    }

    static void validateMap(String filename, BufferedImage image) {
        if (image == null || image.getWidth() != 2048 || image.getHeight() != 1024) {
            throw new AssertionError("Invalid output dimensions: " + filename);
        }
        if (filename.equals("earth_day.jpg")) validateEarth(image);
        if (filename.equals("earth_clouds.jpg")) validateCloud(image);
    }

    private static void validateEarth(BufferedImage image) {
        assertLand(image, 30, 0, true, "Sahara north of equator");
        assertLand(image, -30, 0, false, "South Atlantic south of equator");
        assertLand(image, -25, 135, true, "Australia east longitude");
        assertLand(image, -25, -135, false, "Pacific west longitude");
        System.out.println("EARTH CHECK: north/south and east/west geographic anchors passed");
    }

    private static void assertLand(BufferedImage image, double lat, double lon, boolean land, String name) {
        int x = (int) ((lon + 180) / 360 * image.getWidth());
        int y = (int) ((90 - lat) / 180 * image.getHeight());
        int color = image.getRGB(x, y);
        int red = color >> 16 & 255;
        int blue = color & 255;
        if ((land && red <= blue) || (!land && blue <= red)) {
            throw new AssertionError("Geographic orientation failed at " + name);
        }
    }

    private static void validateCloud(BufferedImage image) {
        int min = 255;
        int max = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getRGB(x, y);
                int value = color & 255;
                if (value != (color >> 8 & 255) || value != (color >> 16 & 255)) {
                    throw new AssertionError("Cloud coverage must be RGB grayscale for shader .r sampling");
                }
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
        }
        if (min >= 30 || max <= 200) throw new AssertionError("Cloud mask lacks clear or opaque regions");
        System.out.printf("CLOUD CHECK: clear/opaque coverage %d..%d%n", min, max);
    }
}
