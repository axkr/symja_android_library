package org.matheclipse.image.algo;

import java.awt.image.BufferedImage;

/**
 * Resampling: everything that reads an image at positions that are not its own pixel centres.
 *
 * <p>
 * All of it goes through {@link Pixels#pixel} and {@link Pixels#fromPixels} rather than through
 * {@link java.awt.Graphics2D}. Drawing a {@link BufferedImage#TYPE_BYTE_GRAY} through Graphics2D
 * runs its linear grey samples through the sRGB conversion described in
 * {@link Boof#argb(BufferedImage, int, int)}, so a rotation would quietly brighten a greyscale
 * image. Sampling the raster directly keeps the samples where they were.
 */
public final class Geometry {

  private Geometry() {}

  /** How a sample outside the image is filled in. */
  public interface Background {
    /** The channel values, on the 0 ... 255 scale, of everything outside the image. */
    float[] outside(int channels);
  }

  /** Transparent where the image has an alpha channel, black where it has not. */
  public static Background transparentOrBlack() {
    return channels -> new float[channels];
  }

  /** Transparent where the image has an alpha channel, white where it has not. */
  public static Background transparentOrWhite() {
    return channels -> {
      float[] values = new float[channels];
      java.util.Arrays.fill(values, 255.0f);
      if (channels == 4) {
        values[3] = 0.0f;
      }
      return values;
    };
  }

  /** A fixed colour, given as RGBA in <code>0.0 ... 1.0</code>. */
  public static Background color(float[] rgba) {
    return channels -> {
      if (channels == 1) {
        return new float[] {(0.299f * rgba[0] + 0.587f * rgba[1] + 0.114f * rgba[2]) * 255.0f};
      }
      float[] values = new float[channels];
      for (int c = 0; c < Math.min(3, channels); c++) {
        values[c] = rgba[c] * 255.0f;
      }
      if (channels == 4) {
        values[3] = rgba.length >= 4 ? rgba[3] * 255.0f : 255.0f;
      }
      return values;
    };
  }

  /**
   * The samples of <code>image</code> at a position given in pixels, with <code>0, 0</code> at the
   * centre of the top left pixel.
   *
   * @param bilinear interpolate between the four surrounding pixels rather than taking the nearest
   */
  public static float[] sample(BufferedImage image, int channels, double x, double y,
      boolean bilinear, Background background) {
    int width = image.getWidth();
    int height = image.getHeight();
    if (!bilinear) {
      int column = (int) Math.round(x);
      int row = (int) Math.round(y);
      if (column < 0 || row < 0 || column >= width || row >= height) {
        return background.outside(channels);
      }
      return Pixels.pixel(image, column, row, channels);
    }

    int left = (int) Math.floor(x);
    int top = (int) Math.floor(y);
    double fractionX = x - left;
    double fractionY = y - top;
    float[] result = new float[channels];
    for (int dy = 0; dy <= 1; dy++) {
      for (int dx = 0; dx <= 1; dx++) {
        double weight = (dx == 0 ? 1.0 - fractionX : fractionX) //
            * (dy == 0 ? 1.0 - fractionY : fractionY);
        if (weight == 0.0) {
          continue;
        }
        int column = left + dx;
        int row = top + dy;
        float[] values = (column < 0 || row < 0 || column >= width || row >= height) //
            ? background.outside(channels)
            : Pixels.pixel(image, column, row, channels);
        for (int c = 0; c < channels; c++) {
          result[c] += (float) (weight * values[c]);
        }
      }
    }
    return result;
  }

  /** Resample to a new size. Shrinking averages the samples that fall into each output pixel. */
  public static BufferedImage resize(BufferedImage image, int width, int height, boolean smooth) {
    int channels = Boof.channels(image);
    int sourceWidth = image.getWidth();
    int sourceHeight = image.getHeight();
    double scaleX = (double) sourceWidth / width;
    double scaleY = (double) sourceHeight / height;
    Background background = transparentOrWhite();

    if (smooth && (scaleX > 1.0 || scaleY > 1.0)) {
      // shrinking: average the block of source pixels behind each output pixel, which is what keeps
      // a downscale from turning into aliased noise
      return Pixels.fromPixels(width, height, channels, (x, y) -> {
        int fromX = (int) Math.floor(x * scaleX);
        int toX = Math.max(fromX + 1, (int) Math.ceil((x + 1) * scaleX));
        int fromY = (int) Math.floor(y * scaleY);
        int toY = Math.max(fromY + 1, (int) Math.ceil((y + 1) * scaleY));
        float[] total = new float[channels];
        int count = 0;
        for (int row = fromY; row < Math.min(toY, sourceHeight); row++) {
          for (int column = fromX; column < Math.min(toX, sourceWidth); column++) {
            float[] values = Pixels.pixel(image, column, row, channels);
            for (int c = 0; c < channels; c++) {
              total[c] += values[c];
            }
            count++;
          }
        }
        if (count == 0) {
          return background.outside(channels);
        }
        for (int c = 0; c < channels; c++) {
          total[c] /= count;
        }
        return total;
      });
    }

    return Pixels.fromPixels(width, height, channels, (x, y) -> sample(image, channels,
        (x + 0.5) * scaleX - 0.5, (y + 0.5) * scaleY - 0.5, smooth, background));
  }

  /** Rotate by a whole number of quarter turns counterclockwise, without resampling. */
  public static BufferedImage rotateQuarters(BufferedImage image, int quarters) {
    int turns = ((quarters % 4) + 4) % 4;
    int channels = Boof.channels(image);
    int width = image.getWidth();
    int height = image.getHeight();
    switch (turns) {
      case 1:
        return Pixels.fromPixels(height, width, channels,
            (x, y) -> Pixels.pixel(image, width - 1 - y, x, channels));
      case 2:
        return Pixels.fromPixels(width, height, channels,
            (x, y) -> Pixels.pixel(image, width - 1 - x, height - 1 - y, channels));
      case 3:
        return Pixels.fromPixels(height, width, channels,
            (x, y) -> Pixels.pixel(image, y, height - 1 - x, channels));
      default:
        return Pixels.fromPixels(width, height, channels,
            (x, y) -> Pixels.pixel(image, x, y, channels));
    }
  }

  /**
   * Rotate counterclockwise by <code>radians</code> about the centre.
   *
   * @param width the output size, or <code>-1</code> for the size that fits the whole rotated image
   */
  public static BufferedImage rotate(BufferedImage image, double radians, int width, int height,
      Background background) {
    int sourceWidth = image.getWidth();
    int sourceHeight = image.getHeight();
    double cos = Math.cos(radians);
    double sin = Math.sin(radians);
    int outputWidth = width;
    int outputHeight = height;
    if (outputWidth < 0 || outputHeight < 0) {
      outputWidth = (int) Math.ceil(Math.abs(sourceWidth * cos) + Math.abs(sourceHeight * sin));
      outputHeight = (int) Math.ceil(Math.abs(sourceWidth * sin) + Math.abs(sourceHeight * cos));
    }
    int channels = Boof.channels(image);
    double centreX = (outputWidth - 1) / 2.0;
    double centreY = (outputHeight - 1) / 2.0;
    double sourceCentreX = (sourceWidth - 1) / 2.0;
    double sourceCentreY = (sourceHeight - 1) / 2.0;

    return Pixels.fromPixels(outputWidth, outputHeight, channels, (x, y) -> {
      double dx = x - centreX;
      // rows grow downwards, so a counterclockwise rotation on screen is clockwise on the raster
      double dy = centreY - y;
      double sourceX = sourceCentreX + dx * cos + dy * sin;
      double sourceY = sourceCentreY - (-dx * sin + dy * cos);
      return sample(image, channels, sourceX, sourceY, true, background);
    });
  }

  /** Mirror horizontally, vertically, or both. */
  public static BufferedImage flip(BufferedImage image, boolean horizontal, boolean vertical) {
    int channels = Boof.channels(image);
    int width = image.getWidth();
    int height = image.getHeight();
    return Pixels.fromPixels(width, height, channels, (x, y) -> Pixels.pixel(image,
        horizontal ? width - 1 - x : x, vertical ? height - 1 - y : y, channels));
  }

  /** Reflect about a diagonal, swapping rows and columns. */
  public static BufferedImage transpose(BufferedImage image, boolean antiDiagonal) {
    int channels = Boof.channels(image);
    int width = image.getWidth();
    int height = image.getHeight();
    return Pixels.fromPixels(height, width, channels, (x, y) -> antiDiagonal //
        ? Pixels.pixel(image, width - 1 - y, height - 1 - x, channels)
        : Pixels.pixel(image, y, x, channels));
  }

  /**
   * The inverse of a 3x3 homogeneous transformation matrix.
   *
   * @return <code>null</code> if the matrix is singular
   */
  public static double[][] invert(double[][] matrix) {
    double a = matrix[0][0];
    double b = matrix[0][1];
    double c = matrix[0][2];
    double d = matrix[1][0];
    double e = matrix[1][1];
    double f = matrix[1][2];
    double g = matrix[2][0];
    double h = matrix[2][1];
    double i = matrix[2][2];
    double determinant = a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
    if (determinant == 0.0 || !Double.isFinite(determinant)) {
      return null;
    }
    return new double[][] { //
        {(e * i - f * h) / determinant, (c * h - b * i) / determinant,
            (b * f - c * e) / determinant},
        {(f * g - d * i) / determinant, (a * i - c * g) / determinant,
            (c * d - a * f) / determinant},
        {(d * h - e * g) / determinant, (b * g - a * h) / determinant,
            (a * e - b * d) / determinant}};
  }

  /** Apply a homogeneous 3x3 matrix to a point. */
  public static double[] apply(double[][] matrix, double x, double y) {
    double w = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2];
    if (w == 0.0) {
      return null;
    }
    return new double[] {(matrix[0][0] * x + matrix[0][1] * y + matrix[0][2]) / w,
        (matrix[1][0] * x + matrix[1][1] * y + matrix[1][2]) / w};
  }
}
