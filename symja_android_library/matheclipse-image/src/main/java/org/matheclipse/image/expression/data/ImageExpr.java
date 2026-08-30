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
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.graphics.SVGGraphics3D;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Represent a BufferedImage by the PNG byte array.
 *
 */
final public class ImageExpr extends DataExpr<byte[]> {

  private static final long serialVersionUID = 6782059289137844206L;

  private final IAST matrix;

  private final ImageOptions options;

  private transient SoftReference<BufferedImage> buffer;

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
    super(S.Image, null);
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
    super(S.Image, original.fData);
    this.buffer = original.buffer;
    this.matrix = matrix;
    this.options = options;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ImageExpr) {
      return java.util.Arrays.equals(fData, ((ImageExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 3761 : 3761 + java.util.Arrays.hashCode(fData);
  }

  public IAST getMatrix() {
    return matrix;
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
    return new ImageExpr(newBufferedImage, matrix, options);
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
   * samples had to be interpreted through a colour space, so only a matrix that still describes the
   * pixels is kept for <code>ImageData</code> to hand back losslessly - see
   * {@link Pixels#describesPixels(String, int)}.
   *
   * @return <code>null</code> if <code>imageData</code> is neither
   */
  public static ImageExpr toImageExpr(IAST imageData, ImageOptions options) {
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
    BufferedImage bufferedImage = Pixels.toBufferedImage(samples, space,
        Pixels.scaleOf(Pixels.imageTypeOf(imageData)));
    if (bufferedImage == null) {
      return null;
    }
    if (!Pixels.describesPixels(space, samples.channels())) {
      return new ImageExpr(bufferedImage, null, options);
    }
    // mark it for the matrix layout of OutputForm, so that ImageData prints one row per line
    imageData.isMatrix(true);
    return new ImageExpr(bufferedImage, imageData, options);
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
      String html = JSBuilder.IMAGE_TEMPLATE;
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
