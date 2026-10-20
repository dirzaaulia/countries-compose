import java.awt.image.BufferedImage;

/** Reprojects NASA Eyes' Z-up cube frames into Countries' north-up, 0-degree-centered UVs. */
final class CubeProjection {
    static final int[][][] FRAMES = {
        {{0, 1, 0}, {0, 0, 1}, {1, 0, 0}},
        {{-1, 0, 0}, {0, 0, 1}, {0, 1, 0}},
        {{0, -1, 0}, {0, 0, 1}, {-1, 0, 0}},
        {{1, 0, 0}, {0, 0, 1}, {0, -1, 0}},
        {{0, 1, 0}, {-1, 0, 0}, {0, 0, 1}},
        {{0, 1, 0}, {1, 0, 0}, {0, 0, -1}},
    };
    private final BufferedImage[] faces;
    private final int size;

    CubeProjection(BufferedImage[] faces) {
        if (faces.length != 6) throw new IllegalArgumentException("Six cube faces required");
        this.faces = faces;
        size = faces[0].getWidth();
        for (BufferedImage face : faces) {
            if (face.getWidth() != size || face.getHeight() != size) {
                throw new IllegalArgumentException("Cube faces must be equally sized squares");
            }
        }
    }

    BufferedImage render(int width, boolean cloudMask) {
        BufferedImage output = new BufferedImage(width, width / 2, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < output.getHeight(); y++) {
            double latitude = Math.PI / 2 - (y + 0.5) * Math.PI / output.getHeight();
            for (int x = 0; x < width; x++) {
                double longitude = (x + 0.5) * 2 * Math.PI / width - Math.PI;
                double[] direction = direction(latitude, longitude);
                output.setRGB(x, y, sample(direction, cloudMask));
            }
        }
        return output;
    }

    static double[] direction(double latitude, double longitude) {
        return new double[] {
            Math.cos(latitude) * Math.cos(longitude),
            Math.cos(latitude) * Math.sin(longitude),
            Math.sin(latitude),
        };
    }

    static int faceIndex(double[] direction) {
        double max = -1;
        int result = 0;
        for (int face = 0; face < 6; face++) {
            double component = dot(FRAMES[face][2], direction);
            if (component >= max) {
                max = component;
                result = face;
            }
        }
        return result;
    }

    static double dot(int[] axis, double[] direction) {
        return axis[0] * direction[0] + axis[1] * direction[1] + axis[2] * direction[2];
    }

    private double[] imagePosition(int face, double[] direction) {
        double denominator = dot(FRAMES[face][2], direction);
        double u = dot(FRAMES[face][0], direction) / denominator;
        double v = dot(FRAMES[face][1], direction) / denominator;
        return new double[] {(u + 1) * size / 2 - 0.5, (1 - v) * size / 2 - 0.5};
    }

    int sample(double[] direction, boolean cloudMask) {
        int face = faceIndex(direction);
        double[] position = imagePosition(face, direction);
        int x = (int) Math.floor(position[0]);
        int y = (int) Math.floor(position[1]);
        double dx = position[0] - x;
        double dy = position[1] - y;
        int[] pixels = {tap(face, x, y), tap(face, x + 1, y),
            tap(face, x, y + 1), tap(face, x + 1, y + 1)};
        double[] weights = {(1 - dx) * (1 - dy), dx * (1 - dy), (1 - dx) * dy, dx * dy};
        if (cloudMask) return mask(pixels, weights);
        int result = 0;
        for (int shift : new int[] {16, 8, 0}) {
            double channel = 0;
            for (int i = 0; i < 4; i++) channel += (pixels[i] >> shift & 255) * weights[i];
            result |= (int) Math.round(channel) << shift;
        }
        return result;
    }

    private int tap(int face, int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) return faces[face].getRGB(x, y);
        double u = 2 * (x + 0.5) / size - 1;
        double v = 1 - 2 * (y + 0.5) / size;
        double[] direction = new double[3];
        for (int axis = 0; axis < 3; axis++) {
            direction[axis] = FRAMES[face][2][axis]
                + FRAMES[face][0][axis] * u + FRAMES[face][1][axis] * v;
        }
        int neighbor = faceIndex(direction);
        double[] position = imagePosition(neighbor, direction);
        int nx = Math.max(0, Math.min(size - 1, (int) Math.round(position[0])));
        int ny = Math.max(0, Math.min(size - 1, (int) Math.round(position[1])));
        return faces[neighbor].getRGB(nx, ny);
    }

    private static int mask(int[] pixels, double[] weights) {
        double value = 0;
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            double luminance = 0.2126 * (pixel >> 16 & 255)
                + 0.7152 * (pixel >> 8 & 255) + 0.0722 * (pixel & 255);
            value += luminance * (pixel >>> 24) / 255.0 * weights[i];
        }
        int gray = (int) Math.round(value);
        return gray << 16 | gray << 8 | gray;
    }
}
