package org.matheclipse.image.algo;

import java.awt.image.BufferedImage;
import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The samples of an image as machine reals on the intensity scale: <code>0.0</code> is black and
 * <code>1.0</code> full intensity, whatever the type the image stores its samples in.
 *
 * <p>
 * The samples are not clipped. An image of reals may hold samples outside of
 * <code>0.0 ... 1.0</code> - <code>Image[{{0, 128}}]</code> holds 128 - and arithmetic, negation,
 * adjustment and the filters work on those values; only the picture which is shown, and a
 * conversion to an integer type, clip them. This is the store every operation which computes new
 * samples reads and writes, rather than the 8 bit bitmap the image is shown from.
 *
 * <p>
 * The samples are kept interleaved, row by row from the top: channel <code>c</code> of the pixel
 * in column <code>x</code> and row <code>y</code> is at <code>(y * width + x) * channels + c</code>.
 */
public final class Raster {

  public final int width;
  public final int height;
  public final int channels;
  private final double[] samples;

  public Raster(int width, int height, int channels) {
    this(width, height, channels, new double[width * height * channels]);
  }

  private Raster(int width, int height, int channels, double[] samples) {
    this.width = width;
    this.height = height;
    this.channels = channels;
    this.samples = samples;
  }

  /** The largest sample of an image type: what full intensity is stored as. */
  public static double maxOf(String type) {
    if (Pixels.BIT.equals(type)) {
      return 1.0;
    }
    if (Pixels.BYTE.equals(type)) {
      return 255.0;
    }
    if (Pixels.BIT16.equals(type)) {
      return 65535.0;
    }
    return 1.0;
  }

  /** Whether the samples of an image type are integers which a sample has to be clipped to. */
  public static boolean isIntegerType(String type) {
    return Pixels.BIT.equals(type) || Pixels.BYTE.equals(type) || Pixels.BIT16.equals(type);
  }

  /**
   * The samples of an interleaved pixel matrix stored in <code>type</code>.
   *
   * @return <code>null</code> if <code>matrix</code> is no rectangular matrix of real samples
   */
  public static Raster fromMatrix(IAST matrix, String type) {
    if (matrix.argSize() == 0 || !matrix.arg1().isList() || matrix.arg1().argSize() == 0) {
      return null;
    }
    int height = matrix.argSize();
    int width = matrix.arg1().argSize();
    IExpr first = ((IAST) matrix.arg1()).arg1();
    int channels = first.isList() ? first.argSize() : 1;
    double max = maxOf(type);
    Raster raster = new Raster(width, height, channels);
    for (int y = 0; y < height; y++) {
      IExpr row = matrix.get(y + 1);
      if (!row.isList() || row.argSize() != width) {
        return null;
      }
      for (int x = 0; x < width; x++) {
        IExpr pixel = ((IAST) row).get(x + 1);
        if (channels == 1 && !pixel.isList()) {
          double value = pixel.evalfNaN();
          if (Double.isNaN(value)) {
            return null;
          }
          raster.set(x, y, 0, value / max);
          continue;
        }
        if (!pixel.isList() || pixel.argSize() != channels) {
          return null;
        }
        for (int c = 0; c < channels; c++) {
          double value = ((IAST) pixel).get(c + 1).evalfNaN();
          if (Double.isNaN(value)) {
            return null;
          }
          raster.set(x, y, c, value / max);
        }
      }
    }
    return raster;
  }

  /** The samples of the 8 bit picture an image is shown from. */
  public static Raster fromBitmap(BufferedImage image) {
    int channels = Boof.channels(image);
    Raster raster = new Raster(image.getWidth(), image.getHeight(), channels);
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        float[] values = Pixels.pixel(image, x, y, channels);
        for (int c = 0; c < channels; c++) {
          raster.set(x, y, c, values[c] / 255.0);
        }
      }
    }
    return raster;
  }

  /**
   * The raster of the samples <code>[channel][row][column]</code>.
   *
   * @return <code>null</code> if they are no rectangular array
   */
  public static Raster fromChannels(double[][][] channels) {
    if (channels.length == 0 || channels[0].length == 0 || channels[0][0].length == 0) {
      return null;
    }
    int height = channels[0].length;
    int width = channels[0][0].length;
    Raster raster = new Raster(width, height, channels.length);
    for (int c = 0; c < channels.length; c++) {
      if (channels[c].length != height) {
        return null;
      }
      for (int y = 0; y < height; y++) {
        if (channels[c][y].length != width) {
          return null;
        }
        for (int x = 0; x < width; x++) {
          raster.set(x, y, c, channels[c][y][x]);
        }
      }
    }
    return raster;
  }

  /** The samples as <code>[channel][row][column]</code>. */
  public double[][][] toChannels() {
    double[][][] result = new double[channels][height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int c = 0; c < channels; c++) {
          result[c][y][x] = get(x, y, c);
        }
      }
    }
    return result;
  }

  public double get(int x, int y, int channel) {
    return samples[(y * width + x) * channels + channel];
  }

  public void set(int x, int y, int channel, double value) {
    samples[(y * width + x) * channels + channel] = value;
  }

  /** The channels which carry colour or intensity, without the alpha channel. */
  public int colorChannels() {
    return channels == 4 || channels == 2 ? channels - 1 : channels;
  }

  /** A raster of the same shape, all samples zero. */
  public Raster blank() {
    return new Raster(width, height, channels);
  }

  public Raster copy() {
    return new Raster(width, height, channels, samples.clone());
  }

  /** <code>operator</code> applied to every colour sample; the alpha channel stays. */
  public Raster mapColor(DoubleUnaryOperator operator) {
    Raster result = copy();
    int colorChannels = colorChannels();
    for (int i = 0; i < samples.length; i += channels) {
      for (int c = 0; c < colorChannels; c++) {
        result.samples[i + c] = operator.applyAsDouble(samples[i + c]);
      }
    }
    return result;
  }

  /**
   * Order two rasters by their shape - width, height, channels - and then sample by sample, row by
   * row from the top.
   */
  public int compareTo(Raster other) {
    int cmp = Integer.compare(width, other.width);
    if (cmp != 0) {
      return cmp;
    }
    cmp = Integer.compare(height, other.height);
    if (cmp != 0) {
      return cmp;
    }
    cmp = Integer.compare(channels, other.channels);
    if (cmp != 0) {
      return cmp;
    }
    for (int i = 0; i < samples.length; i++) {
      cmp = Double.compare(samples[i], other.samples[i]);
      if (cmp != 0) {
        return cmp;
      }
    }
    return 0;
  }

  /**
   * The samples rounded to single precision, which is what an image of type <code>"Real32"</code>
   * holds: two such images whose samples differ by less than that are the same image.
   */
  public Raster toSinglePrecision() {
    double[] rounded = new double[samples.length];
    for (int i = 0; i < samples.length; i++) {
      rounded[i] = (float) samples[i];
    }
    return new Raster(width, height, channels, rounded);
  }

  /** A hash of the shape and the samples, equal for two rasters {@link #compareTo} finds equal. */
  public int samplesHashCode() {
    return 31 * (31 * (31 * width + height) + channels) + java.util.Arrays.hashCode(samples);
  }

  /** The smallest and the largest colour sample. */
  public double[] colorRange() {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    int colorChannels = colorChannels();
    for (int i = 0; i < samples.length; i += channels) {
      for (int c = 0; c < colorChannels; c++) {
        min = Math.min(min, samples[i + c]);
        max = Math.max(max, samples[i + c]);
      }
    }
    return new double[] {min, max};
  }

  /**
   * The interleaved pixel matrix of the samples stored in <code>type</code>: a real type takes the
   * samples as they are, an integer type scales, rounds and clips them to its range, see
   * {@link #level(double, double)}.
   */
  public IAST toMatrix(String type) {
    double max = maxOf(type);
    boolean integer = isIntegerType(type);
    IASTAppendable rows = F.ListAlloc(height);
    for (int y = 0; y < height; y++) {
      IASTAppendable row = F.ListAlloc(width);
      for (int x = 0; x < width; x++) {
        if (channels == 1) {
          row.append(stored(get(x, y, 0), max, integer));
          continue;
        }
        IASTAppendable pixel = F.ListAlloc(channels);
        for (int c = 0; c < channels; c++) {
          pixel.append(stored(get(x, y, c), max, integer));
        }
        row.append(pixel);
      }
      rows.append(row);
    }
    return rows;
  }

  /** One sample as an image of the type stores it. */
  private static IExpr stored(double value, double max, boolean integer) {
    if (!integer) {
      return F.num(value);
    }
    return F.ZZ(level(value, max));
  }

  /**
   * The integer an image of an integer type stores a sample as: clipped to <code>0 ... 1</code>
   * and rounded half to even on the scale of the type, except that a black and white image is white
   * from <code>0.5</code> on, as reading the picture back gives it.
   *
   * @param max the largest sample of the type, see {@link #maxOf(String)}
   */
  public static int level(double value, double max) {
    double clipped = Math.max(0.0, Math.min(1.0, value));
    if (max == 1.0) {
      return clipped >= 0.5 ? 1 : 0;
    }
    return (int) Math.rint(clipped * max);
  }
}
