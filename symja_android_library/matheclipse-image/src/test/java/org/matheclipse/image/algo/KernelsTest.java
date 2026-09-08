package org.matheclipse.image.algo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import boofcv.struct.convolve.Kernel2D_F32;

/** The three spellings of a neighbourhood all have to arrive as the same array. */
public class KernelsTest {

  static {
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void aRadiusMeansABoxOfTwiceThatPlusOne() {
    assertArrayEquals(new int[] {2, 2}, Kernels.toRadii(F.ZZ(2)));
    double[][] matrix = Kernels.toMatrix(F.ZZ(2));
    assertEquals(5, matrix.length);
    assertEquals(5, matrix[0].length);
    assertEquals(25.0, Kernels.sum(matrix), 0.0);
  }

  @Test
  public void aPairOfRadiiIsRectangular() {
    assertArrayEquals(new int[] {3, 1}, Kernels.toRadii(F.list(F.C3, F.C1)));
    double[][] matrix = Kernels.toMatrix(F.list(F.C3, F.C1));
    assertEquals(3, matrix.length);
    assertEquals(7, matrix[0].length);
  }

  @Test
  public void aMatrixIsTakenAsGiven() {
    double[][] matrix = Kernels.toMatrix(F.matrix((i, j) -> F.ZZ(i + j), 2, 3));
    assertEquals(2, matrix.length);
    assertEquals(3, matrix[0].length);
    assertEquals(0.0, matrix[0][0], 0.0);
    assertEquals(3.0, matrix[1][2], 0.0);
  }

  @Test
  public void aStructuringElementIsTrueWhereTheMatrixIsNotZero() {
    boolean[][] mask = Kernels.structuringElement(F.matrix((i, j) -> i == j ? F.C1 : F.C0, 2, 2));
    assertTrue(mask[0][0]);
    assertTrue(mask[1][1]);
    assertTrue(!mask[0][1]);
  }

  @Test
  public void nonsenseIsRejectedRatherThanGuessed() {
    assertNull(Kernels.toRadii(F.CN1));
    assertNull(Kernels.toMatrix(F.CN1));
    assertNull(Kernels.toMatrix(F.x));
  }

  @Test
  public void aConvolutionKernelHasToBeSquareAndOdd() {
    Kernel2D_F32 kernel = Kernels.toKernel2D(Kernels.box(1, 1));
    assertEquals(3, kernel.getWidth());
    assertEquals(9.0f, kernel.computeSum(), 0.0f);

    assertThrows(IllegalArgumentException.class, () -> Kernels.toKernel2D(Kernels.box(2, 1)));
  }
}
