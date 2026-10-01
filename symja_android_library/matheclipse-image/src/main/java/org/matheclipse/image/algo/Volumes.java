package org.matheclipse.image.algo;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * The samples of a 3D image - a volume - as <code>[slice][row][column]</code>, and what is done
 * with them: the filters of a block around a voxel, the morphological ones, and the picture a
 * volume is shown as.
 *
 * <p>
 * The filters follow the ones of a 2D image with one more direction. The block and the
 * morphological filters use the part of the block which lies inside of the volume; the Gaussian
 * filter continues the volume with the voxels of its border.
 */
public final class Volumes {

  private Volumes() {}

  /** The kinds of {@link #block(double[][][], int, int)}. */
  public static final int MIN = 0;
  public static final int MAX = 1;
  public static final int MEAN = 2;
  public static final int MEDIAN = 3;

  private static double[][][] like(double[][][] volume) {
    return new double[volume.length][volume[0].length][volume[0][0].length];
  }

  /**
   * The smallest or the largest sample of every line of <code>2 * radius + 1</code> voxels along
   * one direction: <code>0</code> the slices, <code>1</code> the rows, <code>2</code> the columns.
   */
  private static double[][][] extremeAlong(double[][][] volume, int axis, int radius,
      boolean largest) {
    final int depth = volume.length;
    final int height = volume[0].length;
    final int width = volume[0][0].length;
    final int length = axis == 0 ? depth : axis == 1 ? height : width;
    double[][][] result = like(volume);
    for (int z = 0; z < depth; z++) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int position = axis == 0 ? z : axis == 1 ? y : x;
          int from = Math.max(0, position - radius);
          int to = Math.min(length - 1, position + radius);
          double value = largest ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
          for (int i = from; i <= to; i++) {
            double sample =
                axis == 0 ? volume[i][y][x] : axis == 1 ? volume[z][i][x] : volume[z][y][i];
            value = largest ? Math.max(value, sample) : Math.min(value, sample);
          }
          result[z][y][x] = value;
        }
      }
    }
    return result;
  }

  /** The sum of every line of <code>2 * radius + 1</code> voxels along one direction. */
  private static double[][][] sumAlong(double[][][] volume, int axis, int radius) {
    final int depth = volume.length;
    final int height = volume[0].length;
    final int width = volume[0][0].length;
    final int length = axis == 0 ? depth : axis == 1 ? height : width;
    double[][][] result = like(volume);
    for (int z = 0; z < depth; z++) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int position = axis == 0 ? z : axis == 1 ? y : x;
          int from = Math.max(0, position - radius);
          int to = Math.min(length - 1, position + radius);
          double sum = 0.0;
          for (int i = from; i <= to; i++) {
            sum += axis == 0 ? volume[i][y][x] : axis == 1 ? volume[z][i][x] : volume[z][y][i];
          }
          result[z][y][x] = sum;
        }
      }
    }
    return result;
  }

  private static int inside(int position, int radius, int length) {
    return Math.min(length - 1, position + radius) - Math.max(0, position - radius) + 1;
  }

  /**
   * The smallest, the largest, the mean or the median sample of the cube of
   * <code>2 * radius + 1</code> voxels around every voxel - of the part of the cube which lies
   * inside of the volume. The median of an even number of samples is the upper one of the two in
   * the middle.
   *
   * @param kind {@link #MIN}, {@link #MAX}, {@link #MEAN} or {@link #MEDIAN}
   */
  public static double[][][] block(double[][][] volume, int radius, int kind) {
    if (kind == MIN || kind == MAX) {
      // the extreme of a cube is the extreme of the extremes of its lines
      double[][][] result = volume;
      for (int axis = 0; axis < 3; axis++) {
        result = extremeAlong(result, axis, radius, kind == MAX);
      }
      return result;
    }
    final int depth = volume.length;
    final int height = volume[0].length;
    final int width = volume[0][0].length;
    if (kind == MEAN) {
      double[][][] result = volume;
      for (int axis = 0; axis < 3; axis++) {
        result = sumAlong(result, axis, radius);
      }
      for (int z = 0; z < depth; z++) {
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            result[z][y][x] /= (double) inside(z, radius, depth) * inside(y, radius, height)
                * inside(x, radius, width);
          }
        }
      }
      return result;
    }
    final int side = 2 * radius + 1;
    double[] samples = new double[side * side * side];
    double[][][] result = like(volume);
    for (int z = 0; z < depth; z++) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int count = 0;
          for (int k = Math.max(0, z - radius); k <= Math.min(depth - 1, z + radius); k++) {
            for (int j = Math.max(0, y - radius); j <= Math.min(height - 1, y + radius); j++) {
              for (int i = Math.max(0, x - radius); i <= Math.min(width - 1, x + radius); i++) {
                samples[count++] = volume[k][j][i];
              }
            }
          }
          Arrays.sort(samples, 0, count);
          result[z][y][x] = samples[count / 2];
        }
      }
    }
    return result;
  }

  /**
   * The largest (a dilation) or the smallest (an erosion) sample under a structuring element,
   * where the element <code>element[a][b][c]</code> lies on the voxel
   * <code>(z + a - cz, y + b - cy, x + c - cx)</code> and the centre is the element of the index
   * <code>size / 2</code>. The part of the element outside of the volume doesn't count.
   */
  public static double[][][] extreme(double[][][] volume, boolean[][][] element,
      boolean largest) {
    final int depth = volume.length;
    final int height = volume[0].length;
    final int width = volume[0][0].length;
    final int cz = element.length / 2;
    final int cy = element[0].length / 2;
    final int cx = element[0][0].length / 2;
    double[][][] result = like(volume);
    for (int z = 0; z < depth; z++) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double value = largest ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
          for (int a = 0; a < element.length; a++) {
            int k = z + a - cz;
            if (k < 0 || k >= depth) {
              continue;
            }
            for (int b = 0; b < element[0].length; b++) {
              int j = y + b - cy;
              if (j < 0 || j >= height) {
                continue;
              }
              for (int c = 0; c < element[0][0].length; c++) {
                int i = x + c - cx;
                if (element[a][b][c] && i >= 0 && i < width) {
                  double sample = volume[k][j][i];
                  value = largest ? Math.max(value, sample) : Math.min(value, sample);
                }
              }
            }
          }
          // no element of the structuring element lies on the volume
          result[z][y][x] = Double.isInfinite(value) ? volume[z][y][x] : value;
        }
      }
    }
    return result;
  }

  /**
   * The volume correlated with the kernel along each of its three directions; beyond its border
   * the volume continues with the voxels of the border.
   */
  public static double[][][] separable(double[][][] volume, double[] kernel) {
    final int depth = volume.length;
    final int height = volume[0].length;
    final int width = volume[0][0].length;
    final int radius = kernel.length / 2;
    double[][][] source = volume;
    for (int axis = 0; axis < 3; axis++) {
      final int length = axis == 0 ? depth : axis == 1 ? height : width;
      double[][][] result = like(volume);
      for (int z = 0; z < depth; z++) {
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            int position = axis == 0 ? z : axis == 1 ? y : x;
            double sum = 0.0;
            for (int k = -radius; k <= radius; k++) {
              int i = Math.min(length - 1, Math.max(0, position + k));
              sum += kernel[k + radius]
                  * (axis == 0 ? source[i][y][x] : axis == 1 ? source[z][i][x] : source[z][y][i]);
            }
            result[z][y][x] = sum;
          }
        }
      }
      source = result;
    }
    return source;
  }

  // ------------------------------------------------------------------ the picture of a volume

  /** The direction a volume is looked at from, the default view point of a 3D graphics. */
  private static final double[] VIEW_POINT = {1.3, -2.4, 2.0};

  /** The longer side of the picture of a volume, in pixels, if the volume is small enough. */
  private static final int PICTURE_SIZE = 360;

  private static double[] normalize(double[] v) {
    double norm = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    return new double[] {v[0] / norm, v[1] / norm, v[2] / norm};
  }

  private static double[] cross(double[] a, double[] b) {
    return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]};
  }

  /**
   * The picture a volume is shown as: its brightest voxel along every line of sight, seen from the
   * default view point of a 3D graphics, a little darker the farther away it is, inside of the
   * edges of its bounding box.
   *
   * <p>
   * The first slice is the top of the volume and the first row its back, the way a
   * <code>Raster3DBox</code> places them.
   *
   * @param channels the samples <code>[channel][slice][row][column]</code> in
   *        <code>0.0 ... 1.0</code>; one channel is grey, three or four are red, green and blue
   */
  public static BufferedImage picture(double[][][][] channels) {
    final int depth = channels[0].length;
    final int height = channels[0][0].length;
    final int width = channels[0][0][0].length;
    final boolean color = channels.length >= 3;

    final double[] toEye = normalize(VIEW_POINT);
    final double[] right = normalize(cross(new double[] {0.0, 0.0, 1.0}, toEye));
    final double[] up = cross(toEye, right);

    // the corners of the box, relative to its centre, give the size of the picture
    double halfU = 0.0;
    double halfV = 0.0;
    double halfW = 0.0;
    for (int corner = 0; corner < 8; corner++) {
      double x = ((corner & 1) == 0 ? -0.5 : 0.5) * width;
      double y = ((corner & 2) == 0 ? -0.5 : 0.5) * height;
      double z = ((corner & 4) == 0 ? -0.5 : 0.5) * depth;
      halfU = Math.max(halfU, Math.abs(x * right[0] + y * right[1] + z * right[2]));
      halfV = Math.max(halfV, Math.abs(x * up[0] + y * up[1] + z * up[2]));
      halfW = Math.max(halfW, Math.abs(x * toEye[0] + y * toEye[1] + z * toEye[2]));
    }
    final double scale =
        Math.max(1.0, Math.min(8.0, Math.floor(PICTURE_SIZE / (2.0 * Math.max(halfU, halfV)))));
    final int margin = 4;
    final int pictureWidth = (int) Math.ceil(2.0 * halfU * scale) + 2 * margin;
    final int pictureHeight = (int) Math.ceil(2.0 * halfV * scale) + 2 * margin;
    BufferedImage picture =
        new BufferedImage(pictureWidth, pictureHeight, BufferedImage.TYPE_INT_RGB);

    // the edges of the bounding box
    java.awt.Graphics2D graphics = picture.createGraphics();
    try {
      graphics.setColor(new java.awt.Color(96, 96, 96));
      for (int a = 0; a < 8; a++) {
        for (int bit = 1; bit <= 4; bit <<= 1) {
          if ((a & bit) != 0) {
            continue;
          }
          int b = a | bit;
          double[] p = new double[4];
          for (int end = 0; end < 2; end++) {
            int corner = end == 0 ? a : b;
            double x = ((corner & 1) == 0 ? -0.5 : 0.5) * width;
            double y = ((corner & 2) == 0 ? -0.5 : 0.5) * height;
            double z = ((corner & 4) == 0 ? -0.5 : 0.5) * depth;
            p[2 * end] = pictureWidth / 2.0 + (x * right[0] + y * right[1] + z * right[2]) * scale;
            p[2 * end + 1] = pictureHeight / 2.0 - (x * up[0] + y * up[1] + z * up[2]) * scale;
          }
          graphics.drawLine((int) Math.round(p[0]), (int) Math.round(p[1]),
              (int) Math.round(p[2]), (int) Math.round(p[3]));
        }
      }
    } finally {
      graphics.dispose();
    }

    final double step = 0.5;
    final int steps = (int) Math.ceil(2.0 * halfW / step) + 1;
    double[] brightest = new double[color ? 3 : 1];
    for (int py = 0; py < pictureHeight; py++) {
      double v = (pictureHeight / 2.0 - (py + 0.5)) / scale;
      for (int px = 0; px < pictureWidth; px++) {
        double u = ((px + 0.5) - pictureWidth / 2.0) / scale;
        Arrays.fill(brightest, 0.0);
        for (int i = 0; i < steps; i++) {
          double w = -halfW + i * step;
          // the point of the line of sight, in the coordinates of the box
          double x = u * right[0] + v * up[0] + w * toEye[0] + width / 2.0;
          double y = u * right[1] + v * up[1] + w * toEye[1] + height / 2.0;
          double z = u * right[2] + v * up[2] + w * toEye[2] + depth / 2.0;
          int column = (int) Math.floor(x);
          // the first row is the back and the first slice the top
          int row = height - 1 - (int) Math.floor(y);
          int slice = depth - 1 - (int) Math.floor(z);
          if (column < 0 || column >= width || row < 0 || row >= height || slice < 0
              || slice >= depth) {
            continue;
          }
          double near = 0.55 + 0.45 * (w + halfW) / (2.0 * halfW);
          for (int c = 0; c < brightest.length; c++) {
            brightest[c] = Math.max(brightest[c], channels[c][slice][row][column] * near);
          }
        }
        int red = level(brightest[0]);
        int green = color ? level(brightest[1]) : red;
        int blue = color ? level(brightest[2]) : red;
        if (red > 0 || green > 0 || blue > 0) {
          int edge = picture.getRGB(px, py);
          red = Math.max(red, (edge >> 16) & 0xFF);
          green = Math.max(green, (edge >> 8) & 0xFF);
          blue = Math.max(blue, edge & 0xFF);
          picture.setRGB(px, py, (red << 16) | (green << 8) | blue);
        }
      }
    }
    return picture;
  }

  private static int level(double sample) {
    return (int) Math.max(0L, Math.min(255L, Math.round(sample * 255.0)));
  }
}
