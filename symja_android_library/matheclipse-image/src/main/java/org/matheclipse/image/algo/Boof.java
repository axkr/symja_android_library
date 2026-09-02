package org.matheclipse.image.algo;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.matheclipse.image.expression.data.ImageExpr;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.Planar;

/**
 * The single bridge between Symja's {@link ImageExpr} and BoofCV's image types.
 *
 * <p>
 * Every image processing built-in follows the same three steps: convert the {@link ImageExpr} to a
 * {@link GrayF32} or a {@link Planar} of them, run a BoofCV operation, convert the result back.
 * Keeping the two conversions in one class is what makes the two invariants below checkable.
 *
 * <p>
 * <b>Sample range.</b> All BoofCV images produced here hold samples in <code>0.0 ... 255.0</code>,
 * not <code>0.0 ... 1.0</code>. BoofCV's own operations - <code>GThresholdImageOps.computeOtsu</code>
 * and everything in <code>EnhanceImageOps</code> in particular - are written against that range,
 * while Symja's <code>ImageData</code> reports <code>0.0 ... 1.0</code>. The scaling happens at the
 * <code>ImageData</code> boundary, not here.
 *
 * <p>
 * <b>Band order.</b> A {@link Planar} produced here has band 0 = red, 1 = green, 2 = blue and, when
 * the source has an alpha channel, 3 = alpha. That is the order the pixel accessors below use, so
 * it does not depend on the underlying {@link BufferedImage} raster layout.
 *
 * <p>
 * <b>Why not <code>ConvertBufferedImage</code>.</b> BoofCV ships that exact conversion in
 * <code>boofcv-io</code>, but depending on that artifact pulls <code>boofcv-geo</code>,
 * <code>boofcv-sfm</code>, <code>boofcv-recognition</code>, <code>boofcv-reconstruction</code> and
 * snakeyaml onto the classpath for one class. This does the same job over the accessors in
 * {@link #argb(BufferedImage, int, int)}, which additionally get {@link BufferedImage#TYPE_BYTE_GRAY}
 * right - see there.
 */
public final class Boof {

  /** The largest sample value of the BoofCV images produced by this class. */
  public static final float MAX_VALUE = 255.0f;

  private Boof() {}

  // ---------------------------------------------------------------- queries

  /**
   * The number of colour channels of <code>image</code> the way <code>ImageChannels</code> counts
   * them: 1 for greyscale, 2 for greyscale with alpha, 3 for RGB, 4 for RGB with alpha.
   */
  public static int channels(BufferedImage image) {
    boolean alpha = image.getColorModel().hasAlpha();
    return isColor(image) ? (alpha ? 4 : 3) : (alpha ? 2 : 1);
  }

  /** Whether <code>image</code> carries colour rather than a single intensity per pixel. */
  public static boolean isColor(BufferedImage image) {
    return image.getRaster().getNumBands() >= 3;
  }

  /** Whether <code>image</code> carries an alpha channel. */
  public static boolean hasAlpha(BufferedImage image) {
    return image.getColorModel().hasAlpha();
  }

  /**
   * One pixel of <code>image</code> as packed <code>0xAARRGGBB</code>.
   *
   * <p>
   * This is not {@link BufferedImage#getRGB(int, int)}. For {@link BufferedImage#TYPE_BYTE_GRAY} and
   * {@link BufferedImage#TYPE_USHORT_GRAY} the JDK stores samples in a <em>linear</em> grey colour
   * space, so <code>getRGB</code> gamma-converts them to sRGB on the way out - sample 128 comes back
   * as 186. Writing with <code>setRGB</code> applies the inverse, so a
   * <code>setRGB</code>/<code>getRGB</code> pair round-trips and the conversion stays invisible
   * until greyscale pixels are compared against the numbers that went in. Reading the raster sample
   * directly is what keeps <code>ImageData[Image[data, ColorSpace -&gt; "Grayscale"]] == data</code>.
   */
  public static int argb(BufferedImage image, int x, int y) {
    int type = image.getType();
    if (type == BufferedImage.TYPE_BYTE_GRAY || type == BufferedImage.TYPE_USHORT_GRAY) {
      WritableRaster raster = image.getRaster();
      int gray = raster.getSample(x, y, 0);
      if (type == BufferedImage.TYPE_USHORT_GRAY) {
        gray = (gray * 255) / 65535;
      }
      return 0xFF000000 | (gray << 16) | (gray << 8) | gray;
    }
    return image.getRGB(x, y);
  }

  /** The intensity of one pixel in <code>0.0 ... 255.0</code>, using the ITU-R BT.601 luma weights. */
  public static float intensity(BufferedImage image, int x, int y) {
    int argb = argb(image, x, y);
    int r = (argb >> 16) & 0xFF;
    int g = (argb >> 8) & 0xFF;
    int b = argb & 0xFF;
    if (r == g && g == b) {
      return r;
    }
    return 0.299f * r + 0.587f * g + 0.114f * b;
  }

  // ------------------------------------------------- ImageExpr -> BoofCV

  /** Convert to a single channel image, reducing colour to intensity. */
  public static GrayF32 grayF32(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    GrayF32 out = new GrayF32(width, height);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        out.unsafe_set(x, y, intensity(image, x, y));
      }
    }
    return out;
  }

  /** Convert to a single channel 8 bit image, reducing colour to intensity. */
  public static GrayU8 grayU8(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    GrayU8 out = new GrayU8(width, height);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        out.unsafe_set(x, y, Math.round(intensity(image, x, y)));
      }
    }
    return out;
  }

  /**
   * Convert to a multi band image with bands red, green, blue and - when <code>image</code> has one
   * - alpha.
   */
  public static Planar<GrayF32> planarF32(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    boolean alpha = hasAlpha(image);
    int bands = alpha ? 4 : 3;
    Planar<GrayF32> out = new Planar<GrayF32>(GrayF32.class, width, height, bands);
    GrayF32 red = out.getBand(0);
    GrayF32 green = out.getBand(1);
    GrayF32 blue = out.getBand(2);
    GrayF32 transparency = alpha ? out.getBand(3) : null;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int argb = argb(image, x, y);
        red.unsafe_set(x, y, (argb >> 16) & 0xFF);
        green.unsafe_set(x, y, (argb >> 8) & 0xFF);
        blue.unsafe_set(x, y, argb & 0xFF);
        if (transparency != null) {
          transparency.unsafe_set(x, y, (argb >>> 24) & 0xFF);
        }
      }
    }
    return out;
  }

  /**
   * Convert to whatever shape preserves the channels of <code>image</code>: a {@link GrayF32} for a
   * greyscale image, a {@link Planar} of {@link GrayF32} otherwise.
   */
  public static ImageBase<?> anyF32(BufferedImage image) {
    return isColor(image) ? planarF32(image) : grayF32(image);
  }

  // ------------------------------------------------- BoofCV -> ImageExpr

  /** Convert back to a greyscale {@link BufferedImage}, clamping samples into <code>0 ... 255</code>. */
  public static BufferedImage toBufferedImage(GrayF32 image) {
    BufferedImage out =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster raster = out.getRaster();
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        raster.setSample(x, y, 0, clamp(image.unsafe_get(x, y)));
      }
    }
    return out;
  }

  /** Convert back to a greyscale {@link BufferedImage}. */
  public static BufferedImage toBufferedImage(GrayU8 image) {
    BufferedImage out =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster raster = out.getRaster();
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        raster.setSample(x, y, 0, image.unsafe_get(x, y) & 0xFF);
      }
    }
    return out;
  }

  /**
   * Convert back to an RGB {@link BufferedImage}. A fourth band is written as alpha; a single band
   * is treated as greyscale.
   */
  public static BufferedImage toBufferedImage(Planar<GrayF32> image) {
    int bands = image.getNumBands();
    if (bands == 1) {
      return toBufferedImage(image.getBand(0));
    }
    boolean alpha = bands >= 4;
    BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(),
        alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    GrayF32 red = image.getBand(0);
    GrayF32 green = image.getBand(1);
    GrayF32 blue = image.getBand(bands >= 3 ? 2 : 1);
    GrayF32 transparency = alpha ? image.getBand(3) : null;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int a = transparency == null ? 0xFF : clamp(transparency.unsafe_get(x, y));
        int argb = (a << 24) //
            | (clamp(red.unsafe_get(x, y)) << 16) //
            | (clamp(green.unsafe_get(x, y)) << 8) //
            | clamp(blue.unsafe_get(x, y));
        out.setRGB(x, y, argb);
      }
    }
    return out;
  }

  /** Convert back whatever {@link #anyF32(BufferedImage)} produced. */
  @SuppressWarnings("unchecked")
  public static BufferedImage toBufferedImage(ImageBase<?> image) {
    if (image instanceof GrayF32) {
      return toBufferedImage((GrayF32) image);
    }
    if (image instanceof GrayU8) {
      return toBufferedImage((GrayU8) image);
    }
    if (image instanceof Planar) {
      Planar<?> planar = (Planar<?>) image;
      if (planar.getBandType() == GrayF32.class) {
        return toBufferedImage((Planar<GrayF32>) planar);
      }
    }
    throw new IllegalArgumentException(
        "Boof: unsupported image type " + image.getClass().getSimpleName());
  }

  /**
   * Wrap a BoofCV image as an {@link ImageExpr}.
   *
   * <p>
   * The pixel matrix of the result is deliberately <code>null</code>. {@link ImageExpr} keeps the
   * exact <code>IAST</code> an image was built from so that <code>ImageData</code> can hand back
   * lossless values, and that matrix describes the <em>input</em> of an operation, never its output.
   * Carrying it over would make <code>ImageData[Blur[image]]</code> report the unblurred pixels.
   */
  public static ImageExpr toImageExpr(ImageBase<?> image) {
    return new ImageExpr(toBufferedImage(image), null);
  }

  /** Wrap a {@link BufferedImage} as an {@link ImageExpr}, for the same reason without a matrix. */
  public static ImageExpr toImageExpr(BufferedImage image) {
    return new ImageExpr(image, null);
  }

  private static int clamp(float value) {
    int rounded = Math.round(value);
    if (rounded < 0) {
      return 0;
    }
    return rounded > 255 ? 255 : rounded;
  }
}
