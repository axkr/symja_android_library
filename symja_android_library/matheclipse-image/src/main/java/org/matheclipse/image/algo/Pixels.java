package org.matheclipse.image.algo;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.external.fastutil.ints.IntArrayList;

/**
 * The conversion between a pixel matrix written as an <code>IAST</code> and a
 * {@link BufferedImage}.
 *
 * <p>
 * <b>The scale depends on the data.</b> <code>Image[{{0.5}}]</code> and <code>Image[{{128}}]</code>
 * are the same picture: real samples run <code>0.0 ... 1.0</code>, integer samples run
 * <code>0 ... 255</code>, and integer data whose samples are all 0 or 1 is a bilevel image where 1
 * means white. That is what {@link #imageTypeOf(IAST)} decides, and getting it wrong is what made
 * <code>Image[{{{1.0, 0.0, 0.0}}}]</code> come out black rather than red.
 *
 * <p>
 * <b>Channels are counted from the data.</b> A matrix of scalars is greyscale, a matrix of triples
 * is RGB, a matrix of quadruples is RGB with alpha. The {@link BufferedImage} type follows from
 * that - greyscale becomes <code>TYPE_BYTE_GRAY</code>, RGB becomes <code>TYPE_INT_RGB</code> and
 * only RGBA becomes <code>TYPE_INT_ARGB</code> - so that <code>ImageChannels</code> can answer from
 * the bitmap alone.
 *
 * <p>
 * <b>The channels can be laid out two ways.</b> Interleaved data - the default, and what
 * <code>Interleaving -&gt; True</code> asks for - is indexed <code>{row, column, channel}</code>,
 * so the samples of one pixel sit next to each other. Planar data, which is what
 * <code>Interleaving -&gt; False</code> asks for, is indexed <code>{channel, row, column}</code>,
 * so each channel is a separate matrix. {@link Samples} hides the difference from everything that
 * reads pixels, which is why the two layouts share one set of conversions rather than having a copy
 * each. A matrix of scalars is one greyscale plane whichever way it is read, so only rank 3 data
 * ever needs the planar reader.
 *
 * <p>
 * All pixel reads go through {@link Boof#argb(BufferedImage, int, int)}; see there for why
 * <code>getRGB</code> is not good enough.
 */
public final class Pixels {

  /** Samples are 0 or 1. */
  public static final String BIT = "Bit";
  /** Samples are integers 0 ... 255. */
  public static final String BYTE = "Byte";
  /** Samples are integers 0 ... 65535. */
  public static final String BIT16 = "Bit16";
  /** Samples are reals 0.0 ... 1.0, the default of <code>ImageData</code>. */
  public static final String REAL32 = "Real32";
  /** Samples are reals 0.0 ... 1.0. */
  public static final String REAL64 = "Real64";

  private Pixels() {}

  // -------------------------------------------------------------- IAST -> image

  /**
   * The samples of a pixel matrix, whichever order that matrix stores them in.
   *
   * <p>
   * The samples are handed out one at a time and unconverted, because the scale they are on is not
   * a property of the layout: {@link #scaleOf(String)} decides that from
   * {@link #imageTypeOf(IAST)}, which reads the data as a whole.
   */
  public interface Samples {

    int width();

    int height();

    int channels();

    /**
     * The raw sample of channel <code>channel</code> at pixel <code>(x, y)</code>, counting
     * channels from 0 and pixels from the top left corner.
     */
    IExpr at(int channel, int x, int y);
  }

  /**
   * Read <code>data</code> as a pixel matrix in the given layout.
   *
   * @param interleaved <code>true</code> for <code>{row, column, channel}</code> data,
   *        <code>false</code> for <code>{channel, row, column}</code> data
   * @return <code>null</code> if <code>data</code> is not a rectangular matrix of samples that
   *         describes an image in that layout
   */
  public static Samples samplesOf(IAST data, boolean interleaved) {
    IntArrayList dimensions = LinearAlgebraUtil.dimensions(data);
    if (dimensions == null || dimensions.size() < 2 || dimensions.size() > 3) {
      return null;
    }
    // a matrix of scalars is one greyscale plane whichever way it is read
    boolean planar = !interleaved && dimensions.size() == 3;
    int channels;
    int height;
    int width;
    if (planar) {
      channels = dimensions.getInt(0);
      height = dimensions.getInt(1);
      width = dimensions.getInt(2);
    } else {
      height = dimensions.getInt(0);
      width = dimensions.getInt(1);
      channels = dimensions.size() == 2 ? 1 : dimensions.getInt(2);
    }
    if (height <= 0 || width <= 0) {
      return null;
    }
    if (channels != 1 && channels != 3 && channels != 4) {
      // greyscale with an alpha channel has no BufferedImage type of its own and no colour space
      // name in ImageColorSpace, so it is rejected rather than silently turned into RGB
      return null;
    }
    return planar //
        ? new PlanarSamples(data, width, height, channels)
        : new InterleavedSamples(data, width, height, channels);
  }

  /**
   * Build the image that <code>Image[data]</code> describes, reading <code>data</code> as
   * interleaved.
   *
   * @param colorSpace the <code>ColorSpace</code> option, or <code>null</code> to take the colour
   *        space from the shape of the data
   * @return <code>null</code> if <code>data</code> is not a rectangular matrix of samples
   */
  public static BufferedImage toBufferedImage(IAST data, String colorSpace) {
    return toBufferedImage(data, colorSpace, true);
  }

  /**
   * Build the image that <code>Image[data]</code> describes.
   *
   * @param colorSpace the <code>ColorSpace</code> option, or <code>null</code> to take the colour
   *        space from the shape of the data
   * @param interleaved how to read <code>data</code>; see {@link #samplesOf(IAST, boolean)}
   * @return <code>null</code> if <code>data</code> is not a rectangular matrix of samples
   */
  public static BufferedImage toBufferedImage(IAST data, String colorSpace, boolean interleaved) {
    Samples samples = samplesOf(data, interleaved);
    if (samples == null) {
      return null;
    }
    return toBufferedImage(samples, colorSpace, scaleOf(imageTypeOf(data)));
  }

  /**
   * Build the image that a pixel matrix already read into {@link Samples} describes.
   *
   * <p>
   * A caller that has to know something about the shape of the data as well - how many channels it
   * had, say - reads it once with {@link #samplesOf(IAST, boolean)} and comes here, rather than
   * walking the matrix a second time.
   *
   * @param scale what a sample has to be multiplied by to become a 0 ... 255 value, from
   *        {@link #scaleOf(String)}
   */
  public static BufferedImage toBufferedImage(Samples samples, String colorSpace, double scale) {
    boolean forceGray =
        "Grayscale".equalsIgnoreCase(colorSpace) || "Gray".equalsIgnoreCase(colorSpace);
    if (samples.channels() == 1 || forceGray) {
      return grayImage(samples, scale);
    }
    return colorImage(samples, scale, "HSB".equalsIgnoreCase(colorSpace),
        "CMYK".equalsIgnoreCase(colorSpace));
  }

  /**
   * Whether the matrix an image was built from still describes the image's pixels, so that
   * <code>ImageData</code> can hand it straight back rather than reading the bitmap.
   *
   * <p>
   * It does when the samples went in untouched. <code>"RGB"</code> and <code>Automatic</code> leave
   * them alone; <code>"HSB"</code> and <code>"CMYK"</code> mean something else came out.
   * <code>"Grayscale"</code> is the one that depends on the data: it leaves a single intensity
   * alone but weighs three colour channels into one, and the three channel matrix is then not what
   * the image holds.
   *
   * @param colorSpace the <code>ColorSpace</code> name, or <code>null</code> for
   *        <code>Automatic</code>
   * @param dataChannels how many channels the source matrix had
   */
  public static boolean describesPixels(String colorSpace, int dataChannels) {
    if (colorSpace == null || "RGB".equalsIgnoreCase(colorSpace)) {
      return true;
    }
    if ("Grayscale".equalsIgnoreCase(colorSpace) || "Gray".equalsIgnoreCase(colorSpace)) {
      return dataChannels == 1;
    }
    return false;
  }

  /**
   * The image type that <code>data</code> describes: <code>"Bit"</code> when every sample is 0 or
   * 1, <code>"Byte"</code> for any other integer data, <code>"Real32"</code> as soon as one sample
   * is not an integer.
   *
   * <p>
   * This walks every sample of <code>data</code> and never asks what any of them means, so it reads
   * interleaved and planar data alike.
   */
  public static String imageTypeOf(IAST data) {
    boolean bilevel = true;
    for (int i = 1; i < data.size(); i++) {
      IExpr row = data.get(i);
      if (!row.isList()) {
        return REAL32;
      }
      IAST rowList = (IAST) row;
      for (int j = 1; j < rowList.size(); j++) {
        IExpr cell = rowList.get(j);
        if (cell.isList()) {
          IAST channels = (IAST) cell;
          for (int k = 1; k < channels.size(); k++) {
            IExpr sample = channels.get(k);
            if (!sample.isInteger()) {
              return REAL32;
            }
            bilevel = bilevel && (sample.isZero() || sample.isOne());
          }
        } else {
          if (!cell.isInteger()) {
            return REAL32;
          }
          bilevel = bilevel && (cell.isZero() || cell.isOne());
        }
      }
    }
    return bilevel ? BIT : BYTE;
  }

  /** Whether <code>type</code> names one of the five sample types. */
  public static boolean isImageType(String type) {
    return BIT.equals(type) || BYTE.equals(type) || BIT16.equals(type) || REAL32.equals(type)
        || REAL64.equals(type);
  }

  /**
   * Whether samples of <code>storedType</code> already are what <code>requestedType</code> asks
   * for. <code>Real32</code> and <code>Real64</code> are different names for the same
   * <code>0.0 ... 1.0</code> scale, so neither has to be converted into the other.
   */
  public static boolean sameScale(String storedType, String requestedType) {
    if (storedType.equals(requestedType)) {
      return true;
    }
    boolean storedIsReal = REAL32.equals(storedType) || REAL64.equals(storedType);
    boolean requestedIsReal = REAL32.equals(requestedType) || REAL64.equals(requestedType);
    return storedIsReal && requestedIsReal;
  }

  /** Whether <code>colorSpace</code> names a space that image data can be read in. */
  public static boolean isColorSpace(String colorSpace) {
    return "RGB".equalsIgnoreCase(colorSpace) //
        || "Grayscale".equalsIgnoreCase(colorSpace) //
        || "Gray".equalsIgnoreCase(colorSpace) //
        || "HSB".equalsIgnoreCase(colorSpace) //
        || "CMYK".equalsIgnoreCase(colorSpace);
  }

  /**
   * Whether a colour space can read data with this many channels.
   *
   * <p>
   * Greyscale takes anything, because it weighs whatever channels there are into one intensity. The
   * others each expect the channels they are named for, and reading four samples as
   * <code>"HSB"</code> or three as <code>"CMYK"</code> would quietly drop or invent one.
   */
  public static boolean colorSpaceFits(String colorSpace, int channels) {
    if ("Grayscale".equalsIgnoreCase(colorSpace) || "Gray".equalsIgnoreCase(colorSpace)) {
      return true;
    }
    if ("RGB".equalsIgnoreCase(colorSpace)) {
      return channels == 3 || channels == 4;
    }
    if ("HSB".equalsIgnoreCase(colorSpace)) {
      return channels == 3;
    }
    if ("CMYK".equalsIgnoreCase(colorSpace)) {
      return channels == 4;
    }
    return false;
  }

  /** How much a sample of the given type has to be multiplied by to become a 0 ... 255 value. */
  public static double scaleOf(String imageType) {
    if (BYTE.equals(imageType)) {
      return 1.0;
    }
    if (BIT16.equals(imageType)) {
      return 255.0 / 65535.0;
    }
    // Bit, Real32 and Real64 all have 1.0 as their brightest sample
    return 255.0;
  }

  // -------------------------------------------------------------- image -> IAST

  /**
   * The samples of <code>image</code> as <code>ImageData</code> reports them by default: a matrix
   * of scalars for a greyscale image, a matrix of channel lists otherwise.
   */
  public static IAST toData(BufferedImage image, String type) {
    return toData(image, type, true, false);
  }

  /**
   * The samples of <code>image</code> as <code>ImageData</code> reports them.
   *
   * @param interleaved <code>true</code> for a <code>{row, column, channel}</code> result - a
   *        matrix of scalars where the image has one channel - and <code>false</code> for one
   *        matrix per channel
   * @param dataReversed <code>true</code> to hand back the rows bottom to top
   */
  public static IAST toData(BufferedImage image, String type, boolean interleaved,
      boolean dataReversed) {
    int width = image.getWidth();
    int height = image.getHeight();
    final int channels = emittedChannels(Boof.channels(image));
    if (!interleaved) {
      IASTAppendable planes = F.ListAlloc(channels);
      for (int channel = 0; channel < channels; channel++) {
        final int c = channel;
        planes.append(rows(width, height, dataReversed,
            (x, y) -> sample(channelValue(Boof.argb(image, x, y), c, channels), type)));
      }
      return planes;
    }
    return rows(width, height, dataReversed, (x, y) -> {
      int argb = Boof.argb(image, x, y);
      if (channels == 1) {
        return sample(channelValue(argb, 0, channels), type);
      }
      IASTAppendable pixel = F.ListAlloc(channels);
      for (int channel = 0; channel < channels; channel++) {
        pixel.append(sample(channelValue(argb, channel, channels), type));
      }
      return pixel;
    });
  }

  /** One 0 ... 255 sample in the requested image type. */
  public static IExpr sample(int value, String type) {
    if (BYTE.equals(type)) {
      return F.ZZ(value);
    }
    if (BIT.equals(type)) {
      return value >= 128 ? F.C1 : F.C0;
    }
    if (BIT16.equals(type)) {
      return F.ZZ((value * 65535) / 255);
    }
    return F.num(value / 255.0);
  }

  /**
   * The channel values of one pixel in <code>0.0 ... 255.0</code>, of length {@link Boof#channels}.
   */
  public static float[] pixel(BufferedImage image, int x, int y, int channels) {
    int argb = Boof.argb(image, x, y);
    int emitted = emittedChannels(channels);
    float[] values = new float[emitted];
    for (int channel = 0; channel < emitted; channel++) {
      values[channel] = channelValue(argb, channel, emitted);
    }
    return values;
  }

  /**
   * Build an image from channel values in <code>0.0 ... 255.0</code>, supplied pixel by pixel. The
   * length of the array the supplier returns fixes the number of channels.
   */
  public static BufferedImage fromPixels(int width, int height, int channels,
      PixelSupplier supplier) {
    if (channels == 1) {
      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      WritableRaster raster = image.getRaster();
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          raster.setSample(x, y, 0, clamp(supplier.valueAt(x, y)[0]));
        }
      }
      return image;
    }
    BufferedImage image = new BufferedImage(width, height,
        channels == 4 ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float[] values = supplier.valueAt(x, y);
        int alpha = channels == 4 ? clamp(values[3]) : 0xFF;
        image.setRGB(x, y,
            (alpha << 24) | (clamp(values[0]) << 16) | (clamp(values[1]) << 8) | clamp(values[2]));
      }
    }
    return image;
  }

  /** The channel values of one pixel, in <code>0.0 ... 255.0</code>. */
  @FunctionalInterface
  public interface PixelSupplier {
    float[] valueAt(int x, int y);
  }

  /** The colour space name <code>ImageColorSpace</code> reports. */
  public static String colorSpaceOf(BufferedImage image) {
    return Boof.isColor(image) ? "RGB" : "Grayscale";
  }

  // ------------------------------------------------------------------- internals

  /** What one pixel of the result contributes: a sample, or the list of its channels. */
  @FunctionalInterface
  private interface CellSupplier {
    IExpr valueAt(int x, int y);
  }

  /**
   * How many channels a pixel is reported as, for an image whose raster has <code>channels</code>
   * bands.
   *
   * <p>
   * A {@link BufferedImage} can carry greyscale with an alpha channel and which
   * <code>Image[data]</code> therefore refuses to build. One can still be read from a file, and it
   * is reported as the three RGB channels its argb pixels already give rather than as two channels
   * nothing downstream would know how to read.
   */
  private static int emittedChannels(int channels) {
    if (channels == 1) {
      return 1;
    }
    return channels == 4 ? 4 : 3;
  }

  /** The rows of one matrix of the result, top to bottom unless the rows are reversed. */
  private static IASTAppendable rows(int width, int height, boolean dataReversed,
      CellSupplier cell) {
    IASTAppendable rows = F.ListAlloc(height);
    for (int row = 0; row < height; row++) {
      int y = dataReversed ? height - 1 - row : row;
      IASTAppendable line = F.ListAlloc(width);
      for (int x = 0; x < width; x++) {
        line.append(cell.valueAt(x, y));
      }
      rows.append(line);
    }
    return rows;
  }

  /**
   * The 0 ... 255 value of one channel of an argb pixel, in the channel order
   * <code>ImageData</code> reports: red, green, blue, alpha, or the single grey sample.
   *
   * @param emitted the channel count {@link #emittedChannels(int)} decided, not the raster's
   */
  private static int channelValue(int argb, int channel, int emitted) {
    if (emitted == 1) {
      return argb & 0xFF;
    }
    switch (channel) {
      case 0:
        return (argb >> 16) & 0xFF;
      case 1:
        return (argb >> 8) & 0xFF;
      case 2:
        return argb & 0xFF;
      default:
        return (argb >>> 24) & 0xFF;
    }
  }

  private static BufferedImage grayImage(Samples samples, double scale) {
    int width = samples.width();
    int height = samples.height();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster raster = image.getRaster();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        raster.setSample(x, y, 0, clamp((float) (intensityOf(samples, x, y) * scale)));
      }
    }
    return image;
  }

  private static BufferedImage colorImage(Samples samples, double scale, boolean hsb,
      boolean cmyk) {
    int width = samples.width();
    int height = samples.height();
    BufferedImage image = new BufferedImage(width, height,
        samples.channels() == 4 && !cmyk ? BufferedImage.TYPE_INT_ARGB
            : BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        image.setRGB(x, y, argbOf(samples, x, y, scale, hsb, cmyk));
      }
    }
    return image;
  }

  private static int argbOf(Samples samples, int x, int y, double scale, boolean hsb,
      boolean cmyk) {
    if (hsb) {
      // Color.HSBtoRGB wants 0.0 ... 1.0, so the samples are read on their own scale
      float hue = (float) (samples.at(0, x, y).evalfNaN() * scale / 255.0);
      float saturation = (float) (samples.at(1, x, y).evalfNaN() * scale / 255.0);
      float brightness = (float) (samples.at(2, x, y).evalfNaN() * scale / 255.0);
      return java.awt.Color.HSBtoRGB(hue, saturation, brightness) | 0xFF000000;
    }
    if (cmyk && samples.channels() == 4) {
      double cyan = samples.at(0, x, y).evalfNaN() * scale / 255.0;
      double magenta = samples.at(1, x, y).evalfNaN() * scale / 255.0;
      double yellow = samples.at(2, x, y).evalfNaN() * scale / 255.0;
      double black = samples.at(3, x, y).evalfNaN() * scale / 255.0;
      return 0xFF000000 //
          | (clamp((float) (255.0 * (1.0 - cyan) * (1.0 - black))) << 16) //
          | (clamp((float) (255.0 * (1.0 - magenta) * (1.0 - black))) << 8) //
          | clamp((float) (255.0 * (1.0 - yellow) * (1.0 - black)));
    }
    int alpha = samples.channels() == 4 //
        ? clamp((float) (samples.at(3, x, y).evalfNaN() * scale))
        : 0xFF;
    return (alpha << 24) //
        | (clamp((float) (samples.at(0, x, y).evalfNaN() * scale)) << 16) //
        | (clamp((float) (samples.at(1, x, y).evalfNaN() * scale)) << 8) //
        | clamp((float) (samples.at(2, x, y).evalfNaN() * scale));
  }

  /**
   * The one intensity a pixel comes down to, weighing the colour channels where there are three.
   */
  private static double intensityOf(Samples samples, int x, int y) {
    if (samples.channels() == 1) {
      return samples.at(0, x, y).evalfNaN();
    }
    return 0.299 * samples.at(0, x, y).evalfNaN() //
        + 0.587 * samples.at(1, x, y).evalfNaN() //
        + 0.114 * samples.at(2, x, y).evalfNaN();
  }

  private static int clamp(float value) {
    int rounded = Math.round(value);
    if (rounded < 0) {
      return 0;
    }
    return rounded > 255 ? 255 : rounded;
  }

  /** <code>{row, column, channel}</code> data, where one pixel's samples sit next to each other. */
  private static final class InterleavedSamples implements Samples {
    private final IAST data;
    private final int width;
    private final int height;
    private final int channels;

    InterleavedSamples(IAST data, int width, int height, int channels) {
      this.data = data;
      this.width = width;
      this.height = height;
      this.channels = channels;
    }

    @Override
    public int width() {
      return width;
    }

    @Override
    public int height() {
      return height;
    }

    @Override
    public int channels() {
      return channels;
    }

    @Override
    public IExpr at(int channel, int x, int y) {
      IExpr cell = ((IAST) data.get(y + 1)).get(x + 1);
      // a one channel image may be written either as a matrix of scalars or as a matrix of one
      // element lists, and both mean the same picture
      return cell.isList() ? ((IAST) cell).get(channel + 1) : cell;
    }
  }

  /** <code>{channel, row, column}</code> data, where each channel is a matrix of its own. */
  private static final class PlanarSamples implements Samples {
    private final IAST data;
    private final int width;
    private final int height;
    private final int channels;

    PlanarSamples(IAST data, int width, int height, int channels) {
      this.data = data;
      this.width = width;
      this.height = height;
      this.channels = channels;
    }

    @Override
    public int width() {
      return width;
    }

    @Override
    public int height() {
      return height;
    }

    @Override
    public int channels() {
      return channels;
    }

    @Override
    public IExpr at(int channel, int x, int y) {
      return ((IAST) ((IAST) data.get(channel + 1)).get(y + 1)).get(x + 1);
    }
  }
}
