package org.matheclipse.image.expression.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.HtmlTemplates;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.core.graphics.SVGGraphics3D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.algo.Raster;

/**
 * Represent a BufferedImage by the PNG byte array.
 *
 */
final public class ImageExpr extends DataExpr<byte[]> {

  private static final long serialVersionUID = 6782059289137844206L;

  private final IAST matrix;

  private final ImageOptions options;

  /**
   * The sample type <code>Image(data, type)</code> stated, or <code>null</code> to read it off the
   * matrix: <code>Image({{0,1}}, "Byte")</code> is a Byte image although its samples look bilevel.
   */
  private final String sampleType;

  private transient SoftReference<BufferedImage> buffer;

  /** The samples once {@link #raster()} has read them. */
  private transient SoftReference<Raster> rasterCache;

  /**
   * Represent a BufferedImage by the PNG byte array.
   *
   * @param buffer buffered image which was drawn by a java method
   * @param matrix
   */
  public ImageExpr(final BufferedImage buffer, IAST matrix) {
    this(buffer, matrix, ImageOptions.DEFAULT);
  }

  /**
   * Represent a BufferedImage by the PNG byte array, remembering the options it was built with.
   *
   * @param buffer buffered image which was drawn by a java method
   * @param matrix the pixel matrix the image was built from, or <code>null</code> where there is
   *        none or where it no longer describes the image's pixels
   * @param options never <code>null</code>; use {@link ImageOptions#DEFAULT}
   */
  public ImageExpr(final BufferedImage buffer, IAST matrix, ImageOptions options) {
    this(buffer, matrix, options, null);
  }

  /**
   * Represent a BufferedImage by the PNG byte array, remembering the options and the stated sample
   * type.
   *
   * @param sampleType one of the five image types, or <code>null</code> to read it off the matrix
   */
  public ImageExpr(final BufferedImage buffer, IAST matrix, ImageOptions options,
      String sampleType) {
    super(S.Image, null);
    this.sampleType = sampleType;
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final OutputStream b64 = Base64.getEncoder().wrap(outputStream)) {
      ImageIO.write(buffer, "png", b64);
      fData = outputStream.toByteArray();
    } catch (IOException ioex) {
      throw new IllegalArgumentException("ImageExpr: conversion to byte buffer not possible");
    }
    this.buffer = new SoftReference(buffer);
    this.matrix = matrix;
    this.options = options;
  }

  /**
   * Share the encoded bytes of an image that is already built, which is what makes
   * {@link #withOptions(ImageOptions)} free rather than another trip through the png encoder.
   */
  private ImageExpr(ImageExpr original, IAST matrix, ImageOptions options) {
    this(original, matrix, options, original.sampleType);
  }

  private ImageExpr(ImageExpr original, IAST matrix, ImageOptions options, String sampleType) {
    super(S.Image, original.fData);
    this.sampleType = sampleType;
    this.buffer = original.buffer;
    this.matrix = matrix;
    this.options = options;
  }

  /**
   * Two images are equal when they have the same type and the same samples. The picture they are
   * shown from is not compared, since samples which differ by less than an 8 bit step draw the same
   * picture, and neither are the options: how an image is displayed, or which layout its data was
   * written in, does not make it another image.
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ImageExpr) {
      return compareImage((ImageExpr) obj) == 0;
    }
    return false;
  }

  @Override
  public int hashCode() {
    Raster raster = comparedRaster();
    if (raster == null) {
      return (fData == null) ? 3761 : 3761 + java.util.Arrays.hashCode(fData);
    }
    return 3761 + 31 * sampleType().hashCode() + raster.samplesHashCode();
  }

  /**
   * The samples two images are compared by: those of a <code>"Real32"</code> image in single
   * precision, so that the image its <code>InputForm</code> reads back as is equal to it.
   */
  private Raster comparedRaster() {
    Raster raster = raster();
    return raster != null && Pixels.REAL32.equals(sampleType()) ? raster.toSinglePrecision()
        : raster;
  }

  /**
   * Images are ordered by their size and channels, then sample by sample and last by their type, so
   * that <code>Union</code> and <code>Sort</code> keep two different images apart and agree with
   * {@link #equals(Object)}.
   */
  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof ImageExpr) {
      return compareImage((ImageExpr) expr);
    }
    return super.compareTo(expr);
  }

  private int compareImage(ImageExpr other) {
    if (this == other) {
      return 0;
    }
    Raster raster = comparedRaster();
    Raster otherRaster = other.comparedRaster();
    if (raster == null || otherRaster == null) {
      if (raster != otherRaster) {
        return raster == null ? -1 : 1;
      }
      return java.util.Arrays.compare(fData, other.fData);
    }
    int cmp = raster.compareTo(otherRaster);
    if (cmp != 0) {
      return cmp;
    }
    return sampleType().compareTo(other.sampleType());
  }

  /**
   * <code>Interleaving -&gt; None</code> for an image of one channel, else whether the image is
   * written pixel by pixel; and <code>ColorSpace</code>. Asked for by name, every option of the
   * image is there to be picked, at its default where nothing else was given.
   */
  @Override
  public IAST options(boolean all) {
    Raster raster = raster();
    IExpr interleaving = raster != null && raster.channels == 1 ? S.None
        : F.booleSymbol(options.interleaved());
    if (all) {
      return options.toRules().map(rule -> rule.first() == S.Interleaving //
          ? F.Rule(S.Interleaving, interleaving)
          : rule);
    }
    org.matheclipse.core.interfaces.IASTAppendable result = F.ListAlloc(4);
    result.append(F.Rule(S.ColorSpace, options.colorSpace()));
    result.append(F.Rule(S.Interleaving, interleaving));
    if (!options.imageSize().isAutomatic()) {
      result.append(F.Rule(S.ImageSize, options.imageSize()));
    }
    if (!options.magnification().isAutomatic()) {
      result.append(F.Rule(S.Magnification, options.magnification()));
    }
    return result;
  }

  /**
   * <code>image1 + image2 + x</code>, <code>x * image</code>, <code>image ^ x</code>: sample by
   * sample arithmetic of images of one size and real numbers - see
   * {@link org.matheclipse.image.builtin.ImageArithmeticFunctions#arithmetic(IAST)}.
   */
  @Override
  public IExpr arithmetic(IAST ast, org.matheclipse.core.eval.EvalEngine engine) {
    return org.matheclipse.image.builtin.ImageArithmeticFunctions.arithmetic(ast);
  }

  public IAST getMatrix() {
    return matrix;
  }

  /**
   * The samples of this image on the intensity scale: the exact samples of the matrix it was built
   * from where it kept one, otherwise those of the 8 bit picture it is shown from. Samples of an
   * image of reals outside of <code>0.0 ... 1.0</code> are kept.
   *
   * <p>
   * The raster is read once and shared by the callers: it must not be changed, only copied.
   */
  public Raster raster() {
    Raster cached = rasterCache == null ? null : rasterCache.get();
    if (cached != null) {
      return cached;
    }
    Raster raster = null;
    if (matrix != null) {
      IAST interleaved = options.interleaved() ? matrix : Pixels.relayout(matrix, true);
      if (interleaved != null) {
        raster = Raster.fromMatrix(interleaved, sampleType());
      }
    }
    if (raster == null) {
      BufferedImage image = getBufferedImage();
      if (image == null) {
        return null;
      }
      raster = Raster.fromBitmap(image);
    }
    rasterCache = new SoftReference<Raster>(raster);
    return raster;
  }

  /**
   * The image of the samples of <code>raster</code>, stored in <code>type</code>: a real type
   * keeps them as they are, an integer type rounds and clips them.
   *
   * @param options the options of the image it was computed from
   * @return <code>null</code> if the samples describe no image
   */
  public static ImageExpr fromRaster(Raster raster, ImageOptions options, String type) {
    ImageOptions resultOptions = options.withInterleaved(true);
    if (!Pixels.describesPixels(resultOptions.colorSpaceName(), raster.channels)) {
      // the samples are red, green and blue whatever space the source was read in; reading them
      // in that space again would convert them twice
      resultOptions = resultOptions.withColorSpace(S.Automatic);
    }
    boolean rgbSamples = colorSpaceOf(resultOptions, raster.channels) == null;
    if ((Pixels.BYTE.equals(type) || Pixels.BIT.equals(type)) && raster.channels != 2
        && rgbSamples) {
      // 8 bits hold these samples exactly, so the picture is written directly, without a matrix
      final double max = Raster.maxOf(type);
      final double toByte = 255.0 / max;
      BufferedImage bitmap = Pixels.fromPixels(raster.width, raster.height, raster.channels,
          (x, y) -> {
            float[] values = new float[raster.channels];
            for (int c = 0; c < raster.channels; c++) {
              values[c] = (float) (Raster.level(raster.get(x, y, c), max) * toByte);
            }
            return values;
          });
      return new ImageExpr(bitmap, null, resultOptions, type);
    }
    IAST data = raster.toMatrix(type);
    return toImageExpr(data, resultOptions, type);
  }

  /**
   * The image type: the one <code>Image(data, type)</code> stated, else the type of the matrix it
   * was built from, else <code>"Byte"</code> for a picture stored as an 8 bit bitmap.
   */
  public String sampleType() {
    if (sampleType != null) {
      return sampleType;
    }
    // a matrix written without a type is one of reals
    return matrix == null ? Pixels.BYTE : Pixels.REAL32;
  }

  /**
   * The colour space other than RGB and greyscale the samples of this image are in:
   * <code>"HSB"</code> or <code>"CMYK"</code>, or <code>null</code> for an image of red, green and
   * blue samples or of grey levels. The picture it is shown from is RGB in any case.
   */
  public String storedColorSpace() {
    if (options.colorSpaceName() == null || matrix == null) {
      return null;
    }
    Raster samples = raster();
    return samples == null ? null : colorSpaceOf(options, samples.channels);
  }

  /** <code>"HSB"</code> or <code>"CMYK"</code> if the options name it for that many channels. */
  private static String colorSpaceOf(ImageOptions options, int channels) {
    String name = options.colorSpaceName();
    if ("HSB".equalsIgnoreCase(name) && channels == 3) {
      return "HSB";
    }
    if ("CMYK".equalsIgnoreCase(name) && channels == 4) {
      return "CMYK";
    }
    return null;
  }

  /** The options this image carries; never <code>null</code>. */
  public ImageOptions getOptions() {
    return options;
  }

  /**
   * The same picture with different options. The pixels are untouched, so this shares the encoded
   * bytes rather than drawing the image again.
   */
  public ImageExpr withOptions(ImageOptions newOptions) {
    return newOptions.equals(options) ? this : new ImageExpr(this, matrix, newOptions);
  }

  /**
   * The same picture with a different record of the data it came from - the samples written on the
   * scale <code>Image[data, type]</code> asked for, say. The pixels are untouched, so this shares
   * the encoded bytes too.
   */
  public ImageExpr withMatrix(IAST newMatrix) {
    return newMatrix == matrix ? this : new ImageExpr(this, newMatrix, options);
  }

  /** The same picture with a different record of its data and the sample type it is written in. */
  public ImageExpr withMatrix(IAST newMatrix, String newSampleType) {
    return new ImageExpr(this, newMatrix, options, newSampleType);
  }

  @Override
  public int hierarchy() {
    return IMAGEID;
  }

  @Override
  public IExpr copy() {
    BufferedImage oldBufferedImage = getBufferedImage();
    BufferedImage newBufferedImage = new BufferedImage(oldBufferedImage.getWidth(),
        oldBufferedImage.getHeight(), oldBufferedImage.getType());
    newBufferedImage.setData(oldBufferedImage.getRaster());
    // the copy keeps the stated sample type: a copy of Image({{0,1}}, "Byte") is "Byte", not "Bit"
    return new ImageExpr(newBufferedImage, matrix, options, sampleType);
  }

  public BufferedImage getBufferedImage() {
    BufferedImage bufferedImage = buffer.get();
    if (bufferedImage == null && fData != null) {
      try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fData)) {
        bufferedImage = ImageIO.read(inputStream);
        buffer = new SoftReference(bufferedImage);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return bufferedImage;
  }

  /**
   * <code>Image[data, "type", options]</code>: the pixel matrix it was built from, or the pixels
   * read back from the picture, its image type, and the options that differ from the defaults.
   * Evaluating it builds the same image again.
   *
   * <p>
   * This is what a front end receives: the WLJS notebook reads an <code>Image</code>'s pixels and
   * type from its arguments, and could not texture a plot with, or show, the summary string an
   * image otherwise writes itself as.
   */
  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    String type = sampleType();
    IExpr data = matrix;
    if (data == null) {
      BufferedImage image = getBufferedImage();
      if (image == null) {
        return nilIfUnevaluated ? F.NIL : F.List();
      }
      data = Pixels.toData(image, type, options.interleaved(), false);
    }
    org.matheclipse.core.interfaces.IASTAppendable result = F.ast(S.Image);
    result.append(data);
    result.append(F.stringx(type));
    if (!options.interleaved()) {
      result.append(F.Rule(S.Interleaving, S.False));
    }
    if (!options.colorSpace().isAutomatic()) {
      result.append(F.Rule(S.ColorSpace, options.colorSpace()));
    }
    if (!options.imageSize().isAutomatic()) {
      result.append(F.Rule(S.ImageSize, options.imageSize()));
    }
    if (!options.magnification().isAutomatic()) {
      result.append(F.Rule(S.Magnification, options.magnification()));
    }
    return result;
  }

  /**
   * <code>Image[NumericArray[data, arrayType], type, ColorSpace -&gt; space,
   * Interleaving -&gt; ...]</code>: the form <code>InputForm</code> writes an image in, which reads back as an equal image. The
   * samples are written on the scale of the type, those of a <code>"Real32"</code> image in single
   * precision, and in the layout the image stores them in; <code>Interleaving -&gt; None</code>
   * says that an image has one channel.
   */
  @Override
  public IAST fullForm() {
    Raster raster = raster();
    if (raster == null) {
      return F.NIL;
    }
    String type = sampleType();
    IAST data = (Pixels.REAL32.equals(type) ? raster.toSinglePrecision() : raster).toMatrix(type);
    IExpr interleaving = S.None;
    if (raster.channels > 1) {
      interleaving = F.booleSymbol(options.interleaved());
      if (!options.interleaved()) {
        data = Pixels.relayout(data, false);
        if (data == null) {
          return F.NIL;
        }
      }
    }
    org.matheclipse.core.interfaces.IASTAppendable result = F.ast(S.Image, 4);
    result.append(F.binaryAST2(S.NumericArray, data, F.stringx(numericArrayType(type))));
    result.append(F.stringx(type));
    result.append(F.Rule(S.ColorSpace, options.colorSpace()));
    result.append(F.Rule(S.Interleaving, interleaving));
    return result;
  }

  /** The element type of the <code>NumericArray</code> that holds the samples of an image type. */
  private static String numericArrayType(String type) {
    if (Pixels.BIT.equals(type)) {
      return "Integer8";
    }
    if (Pixels.BYTE.equals(type)) {
      return "UnsignedInteger8";
    }
    if (Pixels.BIT16.equals(type)) {
      return "UnsignedInteger16";
    }
    return Pixels.REAL64.equals(type) ? "Real64" : "Real32";
  }

  /**
   * <code>FullForm</code> is not the form {@link #fullForm()} gives, which is written for
   * <code>InputForm</code> only, but the pixel matrix of {@link #normal(boolean)}.
   */
  @Override
  public String fullFormString() {
    return normal(false).fullFormString();
  }

  @Override
  public String toString() {
    BufferedImage buf = getBufferedImage();
    return "Image(Dimensions: " + buf.getWidth() + "," + buf.getHeight() + " Transparency: "
        + buf.getTransparency() + ")";
  }

  public String toBase64EncodedString() {
    return new String(fData, StandardCharsets.UTF_8);
  }

  /** Create an <code>ImageExpr</code> from a pixel matrix or a graphics object. */
  public static ImageExpr toImageExpr(IAST imageData) {
    return toImageExpr(imageData, ImageOptions.DEFAULT);
  }

  /**
   * Create an <code>ImageExpr</code> from a pixel matrix, reading the samples in the given colour
   * space.
   *
   * @param colorSpace the <code>ColorSpace</code> option value, or <code>"Automatic"</code>
   */
  public static ImageExpr toImageExpr(IAST imageData, String colorSpace) {
    return toImageExpr(imageData,
        ImageOptions.DEFAULT.withColorSpace(colorSpace == null ? S.Automatic : //
            F.stringx(colorSpace)));
  }

  /**
   * Create an <code>ImageExpr</code> from a pixel matrix, a <code>Graphics</code> or a
   * <code>Graphics3D</code> object.
   *
   * <p>
   * A rasterized graphics object has no pixel matrix to remember, and neither has an image whose
   * colour samples were weighed into grey levels, so only a matrix that still describes the pixels
   * is kept for <code>ImageData</code> to hand back losslessly - see
   * {@link Pixels#describesPixels(String, int)}. An <code>"HSB"</code> or <code>"CMYK"</code>
   * image keeps its samples in that space and is shown from their RGB picture.
   *
   * @return <code>null</code> if <code>imageData</code> is neither
   */
  public static ImageExpr toImageExpr(IAST imageData, ImageOptions options) {
    return toImageExpr(imageData, options, null);
  }

  /**
   * Like {@link #toImageExpr(IAST, ImageOptions)}, with the samples read on the scale of the stated
   * <code>sampleType</code> - <code>null</code> reads the type off the data.
   */
  public static ImageExpr toImageExpr(IAST imageData, ImageOptions options, String sampleType) {
    if (imageData.isGraphicsObject()) {
      int rowSize = 600;
      int colSize = 400;
      SVGGraphics converter = new SVGGraphics(rowSize, colSize);
      String svgContent = converter.toSVG(imageData);
      BufferedImage bufferedImage = SVG2BufferedImage.createBufferedImage(svgContent);
      if (bufferedImage != null) {
        return new ImageExpr(bufferedImage, null, options);
      }
      return null;
    }
    if (imageData.isAST(S.Graphics3D)) {
      int rowSize = 600;
      int colSize = 400;
      String svgContent = SVGGraphics3D.toSVG(imageData);
      BufferedImage bufferedImage = SVG2BufferedImage.createBufferedImage(svgContent);
      if (bufferedImage != null) {
        return new ImageExpr(bufferedImage, null, options);
      }
      return null;
    }

    String space = options.colorSpaceName();
    // the shape of the data is read once here, because how many channels it had is what decides
    // whether the matrix is worth keeping
    Pixels.Samples samples = Pixels.samplesOf(imageData, options.interleaved());
    if (samples == null) {
      return null;
    }
    String type = sampleType != null ? sampleType : Pixels.REAL32;
    BufferedImage bufferedImage = Pixels.toBufferedImage(samples, space, Pixels.scaleOf(type));
    if (bufferedImage == null) {
      return null;
    }
    if (!Pixels.describesPixels(space, samples.channels())) {
      return new ImageExpr(bufferedImage, null, options, sampleType);
    }
    if (Pixels.REAL32.equals(type) || Pixels.REAL64.equals(type)) {
      // Image({{1, 0, 0.5}}) is an image of reals, and ImageData gives {{1., 0., 0.5}}
      imageData = Pixels.realSamples(imageData);
    }
    // mark it for the matrix layout of OutputForm, so that ImageData prints one row per line
    imageData.isMatrix(true);
    return new ImageExpr(bufferedImage, imageData, options, sampleType);
  }

  /**
   * The <code>style</code> attribute that shows the image at the size its options ask for, or the
   * empty string where they ask for nothing and it is shown pixel for pixel.
   */
  private String styleAttribute(BufferedImage image) {
    double[] size = options.displaySize(image.getWidth(), image.getHeight());
    if (size == null) {
      return "";
    }
    return " style=\"width:" + cssPixels(size[0]) + "px; height:" + cssPixels(size[1]) + "px\"";
  }

  /** A css length, never rounded away to nothing. */
  private static String cssPixels(double value) {
    return Long.toString(Math.max(1L, Math.round(value)));
  }

  @Override

  public String toHTML() {
    BufferedImage bImage = getBufferedImage();
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final OutputStream b64 = Base64.getEncoder().wrap(outputStream)) {
      ImageIO.write(bImage, "png", b64);
      String html = HtmlTemplates.IMAGE_TEMPLATE;
      String[] argsToRender = new String[3];
      argsToRender[0] = outputStream.toString();
      argsToRender[1] = styleAttribute(bImage);
      argsToRender[2] = "";
      return Errors.templateRender(html, argsToRender);
    } catch (IOException ioex) {
      return "IOException";
    }
  }
}
