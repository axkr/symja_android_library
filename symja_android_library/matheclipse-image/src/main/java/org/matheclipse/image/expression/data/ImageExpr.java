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
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JSBuilder;
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

  /**
   * Create an <code>ImageExpr</code> from the image data.
   * 
   * @param imageData
   * @return <code>null</code>, if the <code>ImageExpr</code> cannot be created fom the given data.
   */
  public static ImageExpr toImageExpr(IAST imageData) {
    IntArrayList dimensions = LinearAlgebra.dimensions(imageData);
    if (dimensions != null) {
      if (dimensions.size() == 2) {
        BufferedImage buffer = ImageFormat.toIntARGB(imageData);
        if (buffer != null) {
          final IAST byteMatrix = ImageFormat.from(buffer);
          IAST resultMatrix =
              F.matrix((i, j) -> F.num((byteMatrix.getPart(i + 1, j + 1).evalDouble()) / 255.0),
                  dimensions.getInt(0), dimensions.getInt(1));
          Extension format = Extension.PNG;
          try (OutputStream outputStream = new ByteArrayOutputStream()) {
            Export.exportImage(outputStream, resultMatrix, format);
            return new ImageExpr(buffer, resultMatrix);
          } catch (IOException e) {
            // e.printStackTrace();
          }
        }
      } else if (dimensions.size() == 3) {
        BufferedImage buffer = ImageFormat.toIntFormat(imageData, BufferedImage.TYPE_INT_ARGB);
        if (buffer != null) {
          IAST byteMatrix = ImageFormat.from(buffer);
          IAST resultMatrix = F.matrix((i, j) -> {
            IAST part = (IAST) byteMatrix.getPart(i + 1, j + 1);
            RGBColor color = ColorFormat.toColor(part);
            float[] floatValues = color.getColorComponents(null);
            return F.List(F.num(floatValues[0]), F.num(floatValues[1]), F.num(floatValues[2]));
          }, dimensions.getInt(0), dimensions.getInt(1));
          Extension format = Extension.PNG;
          try (OutputStream outputStream = new ByteArrayOutputStream()) {
            Export.exportImage(outputStream, resultMatrix, format);
            return new ImageExpr(buffer, resultMatrix);
          } catch (IOException e) {
            // e.printStackTrace();
          }
        }
      }
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
      return IOFunctions.templateRender(html, argsToRender);
    } catch (IOException ioex) {
      return "IOException";
    }
  }
}
