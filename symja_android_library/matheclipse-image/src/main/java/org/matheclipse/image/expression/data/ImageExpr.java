package org.matheclipse.image.expression.data;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.graphics.SVGGraphics3D;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.reflection.system.Export;
import org.matheclipse.core.tensor.img.ColorFormat;
import org.matheclipse.core.tensor.img.ImageFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Represent a BufferedImage by the PNG byte array.
 *
 */
final public class ImageExpr extends DataExpr<byte[]> {

  private static final long serialVersionUID = 6782059289137844206L;

  private final IAST matrix;

  private transient SoftReference<BufferedImage> buffer;

  /**
   * Represent a BufferedImage by the PNG byte array.
   * 
   * @param buffer buffered image which was drawn by a java method
   * @param matrix
   */
  public ImageExpr(final BufferedImage buffer, IAST matrix) {
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
    return new ImageExpr(newBufferedImage, matrix);
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

  public static ImageExpr toImageExpr(IAST imageData) {
    return toImageExpr(imageData, "Automatic");
  }

  /**
   * Create an <code>ImageExpr</code> from the image data with a specific ColorSpace.
   */
  public static ImageExpr toImageExpr(IAST imageData, String colorSpace) {
    if (imageData.isGraphicsObject()) {
      int rowSize = 600;
      int colSize = 400;
      SVGGraphics converter = new SVGGraphics(rowSize, colSize);
      String svgContent = converter.toSVG(imageData);
      BufferedImage bufferedImage = SVG2BufferedImage.createBufferedImage(svgContent);
      if (bufferedImage != null) {
        return new ImageExpr(bufferedImage, null);
      }
      return null;
    }
    if (imageData.isAST(S.Graphics3D)) {
      int rowSize = 600;
      int colSize = 400;
      String svgContent = SVGGraphics3D.toSVG(imageData);
      BufferedImage bufferedImage = SVG2BufferedImage.createBufferedImage(svgContent);
      if (bufferedImage != null) {
        return new ImageExpr(bufferedImage, null);
      }
      return null;
    }

    IntArrayList dimensions = LinearAlgebraUtil.dimensions(imageData);
    if (dimensions != null) {
      int rows = dimensions.getInt(0);
      int cols = dimensions.getInt(1);

      // Handle Color Space Conversions manually if not RGB
      if ("HSB".equalsIgnoreCase(colorSpace) && dimensions.size() == 3) {
        return createFromHSB(imageData, rows, cols);
      } else if ("CMYK".equalsIgnoreCase(colorSpace) && dimensions.size() == 3) {
        return createFromCMYK(imageData, rows, cols);
      } else if ("Grayscale".equalsIgnoreCase(colorSpace)) {
        // Force Grayscale interpretation
        return createGrayscale(imageData, rows, cols);
      }

      // Default RGB / Automatic Handling
      BufferedImage bufferedImage = null;
      if (dimensions.size() == 2) {
        bufferedImage = ImageFormat.toIntARGB(imageData);
      } else if (dimensions.size() == 3) {
        bufferedImage = ImageFormat.toIntFormat(imageData, BufferedImage.TYPE_INT_ARGB);
      }

      if (bufferedImage != null) {
        return new ImageExpr(bufferedImage, imageData);
      }
    }
    return null;
  }

  /**
   * Converts HSB data {{{h,s,b},...},...} to RGB BufferedImage
   */
  private static ImageExpr createFromHSB(IAST data, int rows, int cols) {
    BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < rows; y++) {
      IAST row = (IAST) data.get(y + 1);
      for (int x = 0; x < cols; x++) {
        IAST pixel = (IAST) row.get(x + 1);
        float h = (float) pixel.get(1).evalDouble();
        float s = (float) pixel.get(2).evalDouble();
        float b = (float) pixel.get(3).evalDouble();
        // Optional: Alpha

        // Java HSBtoRGB expects 0..1
        int rgb = Color.HSBtoRGB(h, s, b);
        image.setRGB(x, y, rgb);
      }
    }
    return new ImageExpr(image, data);
  }

  /**
   * Converts CMYK data {{{c,m,y,k},...},...} to RGB BufferedImage Using simple subtraction method:
   * R = (1-C)*(1-K), etc.
   */
  private static ImageExpr createFromCMYK(IAST data, int rows, int cols) {
    BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < rows; y++) {
      IAST row = (IAST) data.get(y + 1);
      for (int x = 0; x < cols; x++) {
        IAST pixel = (IAST) row.get(x + 1);
        double c = pixel.get(1).evalDouble();
        double m = pixel.get(2).evalDouble();
        double yellow = pixel.get(3).evalDouble();
        double k = pixel.get(4).evalDouble();

        double r = (1.0 - c) * (1.0 - k);
        double g = (1.0 - m) * (1.0 - k);
        double b = (1.0 - yellow) * (1.0 - k);

        int ir = (int) (r * 255);
        int ig = (int) (g * 255);
        int ib = (int) (b * 255);
        int rgb = (255 << 24) | (ir << 16) | (ig << 8) | ib;

        image.setRGB(x, y, rgb);
      }
    }
    return new ImageExpr(image, data);
  }

  /**
   * Forces data to be interpreted as Grayscale. Useful if data is {rows, cols} or {rows, cols, 1}
   */
  private static ImageExpr createGrayscale(IAST data, int rows, int cols) {
    BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_GRAY);
    for (int y = 0; y < rows; y++) {
      IAST row = (IAST) data.get(y + 1);
      for (int x = 0; x < cols; x++) {
        IExpr valExpr = row.get(x + 1);
        double val;
        if (valExpr.isList()) {
          // Handle {0.5} case
          val = ((IAST) valExpr).arg1().evalDouble();
        } else {
          val = valExpr.evalDouble();
        }
        int gray = (int) (val * 255);
        if (gray > 255)
          gray = 255;
        if (gray < 0)
          gray = 0;

        // Set RGB (Gray) - standard packing
        int rgb = (255 << 24) | (gray << 16) | (gray << 8) | gray;
        image.setRGB(x, y, rgb);
      }
    }
    return new ImageExpr(image, data);
  }

  private static ImageExpr createColoredImageExpr(BufferedImage bufferedImage, int rowSize,
      int colSize) {
    IAST byteMatrix = ImageFormat.from(bufferedImage);
    IAST resultMatrix = F.matrix((i, j) -> {
      IAST part = (IAST) byteMatrix.getPart(i + 1, j + 1);
      RGBColor color = ColorFormat.toColor(part);
      float[] floatValues = color.getColorComponents(null);
      return F.List(F.num(floatValues[0]), F.num(floatValues[1]), F.num(floatValues[2]));
    }, rowSize, colSize);
    Extension format = Extension.PNG;
    try (OutputStream outputStream = new ByteArrayOutputStream()) {
      Export.exportImage(outputStream, resultMatrix, format);
      return new ImageExpr(bufferedImage, resultMatrix);
    } catch (IOException e) {
      // e.printStackTrace();
    }
    return null;
  }

  private static ImageExpr createImageExpr(BufferedImage buffer, int rowSize, int colSize) {
    final IAST byteMatrix = ImageFormat.from(buffer);
    IAST resultMatrix = F.matrix(
        (i, j) -> F.num((byteMatrix.getPart(i + 1, j + 1).evalf()) / 255.0), rowSize, colSize);
    Extension format = Extension.PNG;
    try (OutputStream outputStream = new ByteArrayOutputStream()) {
      Export.exportImage(outputStream, resultMatrix, format);
      return new ImageExpr(buffer, resultMatrix);
    } catch (IOException e) {
      // e.printStackTrace();
    }
    return null;
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
      return Errors.templateRender(html, argsToRender);
    } catch (IOException ioex) {
      return "IOException";
    }
  }
}
