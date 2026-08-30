package org.matheclipse.image.algo;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import boofcv.struct.convolve.Kernel2D_F32;

/**
 * Turns the mask and kernel arguments the image built-ins accept into plain Java arrays.
 *
 * <p>
 * Spells a neighbourhood three ways, and every morphological and filtering function accepts all
 * three: a radius <code>r</code>, a pair of radii <code>{rx, ry}</code>, or an explicit matrix. The
 * matrix case also covers <code>DiskMatrix</code>, <code>BoxMatrix</code>,
 * <code>CrossMatrix</code>, <code>DiamondMatrix</code> and <code>GaussianMatrix</code> without any
 * special handling here, because those are ordinary <code>matheclipse-core</code> built-ins that
 * have already evaluated to a matrix by the time an argument reaches this class.
 *
 * <p>
 * A radius <code>r</code> means a <code>(2r+1) x (2r+1)</code> neighbourhood, matching
 * <code>BoxMatrix[r]</code>.
 */
public final class Kernels {

  private Kernels() {}

  /**
   * The neighbourhood <code>spec</code> describes, as a rectangular <code>double[][]</code> in row
   * major order.
   *
   * @return <code>null</code> if <code>spec</code> is neither a radius, a pair of radii nor a
   *         numeric matrix
   */
  public static double[][] toMatrix(IExpr spec) {
    int[] dimensions = spec.isMatrix();
    if (dimensions != null) {
      return matrixOf((IAST) spec, dimensions[0], dimensions[1]);
    }
    int[] radii = toRadii(spec);
    return radii == null ? null : box(radii[0], radii[1]);
  }

  /**
   * The radii <code>{rx, ry}</code> that <code>spec</code> describes.
   *
   * @return <code>null</code> if <code>spec</code> is not a non negative integer or a pair of them
   */
  public static int[] toRadii(IExpr spec) {
    if (spec.isList()) {
      IAST list = (IAST) spec;
      if (list.argSize() == 2) {
        int rx = list.arg1().toIntDefault();
        int ry = list.arg2().toIntDefault();
        if (rx >= 0 && ry >= 0 && rx != Config.INVALID_INT && ry != Config.INVALID_INT) {
          return new int[] {rx, ry};
        }
      }
      return null;
    }
    int radius = spec.toIntDefault();
    if (radius >= 0 && radius != Config.INVALID_INT) {
      return new int[] {radius, radius};
    }
    return null;
  }

  /**
   * The neighbourhood <code>spec</code> describes as a mask: a cell is <code>true</code> where the
   * matrix is non zero.
   *
   * @return <code>null</code> if <code>spec</code> does not describe a neighbourhood
   */
  public static boolean[][] structuringElement(IExpr spec) {
    double[][] matrix = toMatrix(spec);
    if (matrix == null) {
      return null;
    }
    boolean[][] mask = new boolean[matrix.length][matrix[0].length];
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        mask[i][j] = matrix[i][j] != 0.0;
      }
    }
    return mask;
  }

  /** A <code>(2*radiusX+1) x (2*radiusY+1)</code> block of ones. */
  public static double[][] box(int radiusX, int radiusY) {
    double[][] matrix = new double[2 * radiusY + 1][2 * radiusX + 1];
    for (int i = 0; i < matrix.length; i++) {
      java.util.Arrays.fill(matrix[i], 1.0);
    }
    return matrix;
  }

  /**
   * A BoofCV convolution kernel over <code>matrix</code>, which has to be square and of odd width -
   * BoofCV has no representation for anything else.
   *
   * @throws IllegalArgumentException if <code>matrix</code> is not square with an odd width
   */
  public static Kernel2D_F32 toKernel2D(double[][] matrix) {
    int height = matrix.length;
    int width = matrix[0].length;
    if (width != height || (width & 1) == 0) {
      throw new IllegalArgumentException(
          "Kernels: a convolution kernel has to be square and of odd width, got " + height + "x"
              + width);
    }
    float[] data = new float[width * height];
    int index = 0;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        data[index++] = (float) matrix[i][j];
      }
    }
    return new Kernel2D_F32(width, data);
  }

  /** The sum of every cell, for normalizing a kernel. */
  public static double sum(double[][] matrix) {
    double total = 0.0;
    for (double[] row : matrix) {
      for (double value : row) {
        total += value;
      }
    }
    return total;
  }

  private static double[][] matrixOf(IAST matrix, int rows, int columns) {
    double[][] result = new double[rows][columns];
    for (int i = 0; i < rows; i++) {
      IAST row = (IAST) matrix.get(i + 1);
      for (int j = 0; j < columns; j++) {
        double value = row.get(j + 1).evalfNaN();
        if (Double.isNaN(value)) {
          return null;
        }
        result[i][j] = value;
      }
    }
    return result;
  }
}
