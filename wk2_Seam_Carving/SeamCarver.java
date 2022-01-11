import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private Picture picture;
    private int width, height;
    private int[][] pictureArray; // [row][col]
    // private double[][] energy; // [row][col]

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        this.picture = duplicatePicture(picture);
        resetPicture();
    }

    private Picture duplicatePicture(Picture that) {
        Picture d = new Picture(that.width(), that.height());
        for (int col = 0; col < that.width(); col++)
            for (int row = 0; row < that.height(); row++) {
                d.setRGB(col, row, that.getRGB(col, row));
            }
        return d;
    }

    private void resetPicture() {
        width = picture.width();
        height = picture.height();
        pictureArray = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pictureArray[row][col] = picture.getRGB(col, row);
                // System.out.printf("%h ", pictureArray[row][col]);
            }
            // System.out.println();
        }
    }

    // current picture
    public Picture picture() {
        return duplicatePicture(picture);
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1)
            throw new IllegalArgumentException();
        // return energy[y][x];
        return getenergy(x, y);
    }

    private double getenergy(int col, int row) {
        if (col == 0 || col == width - 1 || row == 0 || row == height - 1) return 1000;
        return Math.sqrt(deltax(pictureArray[row][col + 1], pictureArray[row][col - 1]) + deltax(
                pictureArray[row + 1][col], pictureArray[row - 1][col]));
    }

    private int deltax(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = (rgb1 >> 0) & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = (rgb2 >> 0) & 0xFF;
        int xr = Math.abs(r2 - r1);
        int xg = Math.abs(g2 - g1);
        int xb = Math.abs(b2 - b1);
        return xr * xr + xg * xg + xb * xb;
    }


    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return findShortestPath(false);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findShortestPath(true);
    }

    private int[] findShortestPath(boolean vertical) {
  /*
        double[][] energy = new double[height][width];
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++) {
                energy[row][col] = getenergy(col, row);
            }

   */
        int h, w;
        if (vertical) {
            w = width;
            h = height;
        }
        else {
            w = height;
            h = width;
        }
        double[][] e = new double[height][width];
        int[][] s = new int[height][width];
        int[] shortestPath = new int[h];
        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                e[j][i] = Double.POSITIVE_INFINITY;
                s[j][i] = -1;
            }
        for (int j = 0; j < w; j++)
            if (vertical) e[0][j] = 1000;
            else e[j][0] = 1000;
        for (int y = 0; y < h - 1; y++)
            for (int x = 0; x < w; x++) {
                for (int r = Math.max(x - 1, 0); r <= Math.min(x + 1, w - 1); r++) {
                    if (vertical) {
                        if (e[y + 1][x] > e[y][r] + energy(x, y + 1)) {
                            e[y + 1][x] = e[y][r] + energy(x, y + 1);
                            s[y + 1][x] = r;
                        }
                    }
                    else {
                        if (e[x][y + 1] > e[r][y] + energy(y + 1, x)) {
                            e[x][y + 1] = e[r][y] + energy(y + 1, x);
                            s[x][y + 1] = r;
                        }
                    }
                }
            }
        double minEnergy = Double.POSITIVE_INFINITY;
        int shortestIndex = -1;
        for (int i = 0; i < w; i++)
            if (vertical) {
                if (e[h - 1][i] < minEnergy) {
                    minEnergy = e[h - 1][i];
                    shortestIndex = i;
                }
            }
            else {
                if (e[i][h - 1] < minEnergy) {
                    minEnergy = e[i][h - 1];
                    shortestIndex = i;
                }
            }
        shortestPath[h - 1] = shortestIndex;
        for (int j = h - 1; j > 0; j--)
            if (vertical) shortestPath[j - 1] = s[j][shortestPath[j]];
            else shortestPath[j - 1] = s[shortestPath[j]][j];
        return shortestPath;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkSeam(seam, true);
        removeSeam(seam, true);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkSeam(seam, false);
        removeSeam(seam, false);
    }

    private void checkSeam(int[] seam, boolean vertical) {
        int row, col;
        if (!vertical) {
            row = width;
            col = height;
        }
        else {
            row = height;
            col = width;
        }
        if (seam == null || seam.length != row) throw new IllegalArgumentException();
        if (col <= 1) throw new IllegalArgumentException();
        for (int i = 0; i < row - 1; i++)
            if (seam[i] < 0 || seam[i] > col - 1 || Math.abs(seam[i] - seam[i + 1]) > 1)
                throw new IllegalArgumentException();
        if (seam[row - 1] < 0 || seam[row - 1] > col - 1)
            throw new IllegalArgumentException();
    }

    private void removeSeam(int[] seam, boolean vertical) {
        int length;
        if (vertical) {
            length = height;
        }
        else {
            // create rotate picture array
            pictureArray = new int[width][height];
            for (int row = 0; row < width; row++) {
                for (int col = 0; col < height; col++) {
                    pictureArray[row][col] = picture.getRGB(row, height - 1 - col);
                    //    System.out.printf("%h ", pictureArray[row][col]);
                }
                // System.out.println();
            }
            length = width;
            for (int i = 0; i < seam.length; i++)
                seam[i] = height - 1 - seam[i];
        }
        // remove seam[] vertically
        int lineLength = pictureArray[0].length;
        for (int i = 0; i < length; i++) {
            if (seam[i] == 0)
                System.arraycopy(pictureArray[i], 1, pictureArray[i], 0,
                                 lineLength - 1);
            else if (seam[i] == lineLength - 1)
                System.arraycopy(pictureArray[i], 0, pictureArray[i], 0,
                                 lineLength - 1);
            else {
                System.arraycopy(pictureArray[i], 0, pictureArray[i], 0, seam[i]);
                System.arraycopy(pictureArray[i], seam[i] + 1, pictureArray[i], seam[i],
                                 lineLength - 1 - seam[i]);
            }
        }
        if (vertical) {
            // create picture object
            picture = new Picture(width - 1, height);
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width - 1; col++) {
                    picture.setRGB(col, row, pictureArray[row][col]);
                    //   System.out.printf("%h ", pictureArray[col][row]);
                }
            }
        }
        else {
            // create rotate picture object
            picture = new Picture(width, height - 1);
            for (int row = 0; row < height - 1; row++) {
                for (int col = 0; col < width; col++) {
                    picture.setRGB(col, row, pictureArray[col][height - 2 - row]);
                    //   System.out.printf("%h ", pictureArray[col][row]);
                }
            }
        }
        resetPicture();
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver s = new SeamCarver(picture);
        SeamCarver s2 = new SeamCarver(picture);

        for (int row = 0; row < s.height; row++) {
            for (int col = 0; col < s.width; col++)
                System.out.printf("%6h ", s.picture.getRGB(col, row));
            System.out.println();
        }
        System.out.println(" ^^^^^^^^^ Orignal^^^^^^^^^^");

        int[] seam = { 3, 4, 3, 2, 1 };
        s.removeVerticalSeam(seam);
        for (int y = 0; y < s.height; y++) {
            for (int x = 0; x < s.width; x++)
                System.out.printf("%h ", s.picture.getRGB(x, y));
            System.out.println();
        }
        System.out.println(" ^^^^^^^^^^^^^^ Remove Vertical ^^^^^^^");

        int[] seam1 = { 3, 4, 3, 2, 2, 1 };
        s2.removeHorizontalSeam(seam1);
        for (int y = 0; y < s2.height; y++) {
            for (int x = 0; x < s2.width; x++)
                System.out.printf("%h ", s2.picture.getRGB(x, y));
            System.out.println();
        }
        System.out.println(" ^^^^^^^^^ Remove Horizontal ^^^^^^^^^^");
    }
}
