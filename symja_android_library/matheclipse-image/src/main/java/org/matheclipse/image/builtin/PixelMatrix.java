package org.matheclipse.image.builtin;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * The matrix an image was built from, moved along with the pixels.
 *
 * <p>
 * An image is shown from an 8 bit bitmap, but an image built from a matrix keeps that matrix, and
 * <code>ImageData</code> hands it back. An operation which only moves pixels around - a reflection,
 * a padding, a crop - can move the entries of the matrix the same way, and the image it returns
 * then still has the samples and the type of the one it was given, instead of the samples rounded
 * to <code>1/255</code>.
 */
final class PixelMatrix {

  /** Where the pixel of the result at column <code>x</code> and row <code>y</code> comes from. */
  @FunctionalInterface
  interface Source {
    /** @return <code>null</code> if the result can't be built from the matrix */
    IExpr at(int x, int y);
  }

  /**
   * The matrix of the image, one entry - a sample or the list of the channels - per pixel. An
   * image which stores its channels as planes gets them written pixel by pixel.
   *
   * @return <code>null</code> if <code>arg</code> is no image which kept such a matrix
   */
  static IAST of(IExpr arg) {
    if (!(arg instanceof ImageExpr)) {
      return null;
    }
    ImageExpr image = (ImageExpr) arg;
    IAST matrix = image.getMatrix();
    if (matrix != null && !image.getOptions().interleaved()) {
      matrix = Pixels.relayout(matrix, true);
    }
    if (matrix == null || matrix.argSize() == 0 || !matrix.arg1().isList()
        || matrix.arg1().argSize() == 0) {
      return null;
    }
    final int columns = matrix.arg1().argSize();
    for (int i = 1; i <= matrix.argSize(); i++) {
      if (!matrix.get(i).isList() || matrix.get(i).argSize() != columns) {
        return null;
      }
    }
    return matrix;
  }

  static int width(IAST matrix) {
    return matrix.arg1().argSize();
  }

  static int height(IAST matrix) {
    return matrix.argSize();
  }

  /** The pixel at column <code>x</code> and row <code>y</code>, both counted from 0. */
  static IExpr pixel(IAST matrix, int x, int y) {
    return ((IAST) matrix.get(y + 1)).get(x + 1);
  }

  /** The number of channels of a pixel. */
  static int channels(IAST matrix) {
    IExpr pixel = pixel(matrix, 0, 0);
    return pixel.isList() ? pixel.argSize() : 1;
  }

  /**
   * An image of the given size with the options and the type of <code>source</code>, built from
   * the pixels <code>from</code> supplies.
   *
   * @return <code>null</code> if a pixel is missing or the matrix describes no image
   */
  static IExpr image(IExpr source, int width, int height, Source from) {
    if (width <= 0 || height <= 0) {
      return null;
    }
    IASTAppendable rows = F.ListAlloc(height);
    for (int y = 0; y < height; y++) {
      IASTAppendable row = F.ListAlloc(width);
      for (int x = 0; x < width; x++) {
        IExpr pixel = from.at(x, y);
        if (pixel == null) {
          return null;
        }
        row.append(pixel);
      }
      rows.append(row);
    }
    ImageExpr image = (ImageExpr) source;
    return ImageExpr.toImageExpr(rows, image.getOptions().withInterleaved(true),
        image.sampleType());
  }

  /**
   * The pixel of one colour in an image of the given type and number of channels.
   *
   * @param rgba the colour, <code>{r, g, b, a}</code> in <code>0.0 ... 1.0</code>
   * @return <code>null</code> if the image has one channel and the colour is no grey
   */
  static IExpr constant(double[] rgba, int channels, String type) {
    if (channels == 1) {
      return rgba[0] == rgba[1] && rgba[1] == rgba[2] ? sample(rgba[0], type) : null;
    }
    if (channels != 3 && channels != 4) {
      return null;
    }
    IASTAppendable pixel = F.ListAlloc(channels);
    for (int c = 0; c < channels; c++) {
      pixel.append(sample(rgba[c], type));
    }
    return pixel;
  }

  /** A sample given in <code>0.0 ... 1.0</code> on the scale of the image type. */
  static IExpr sample(double value, String type) {
    if (Pixels.BYTE.equals(type)) {
      return F.ZZ(Math.round(value * 255.0));
    }
    if (Pixels.BIT16.equals(type)) {
      return F.ZZ(Math.round(value * 65535.0));
    }
    if (Pixels.BIT.equals(type)) {
      return value >= 0.5 ? F.C1 : F.C0;
    }
    return F.num(value);
  }

  private PixelMatrix() {}
}
