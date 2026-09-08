package org.matheclipse.core.numbertheory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class LLLTest {

  private static BigInteger[][] matrix(long[][] values) {
    BigInteger[][] result = new BigInteger[values.length][];
    for (int i = 0; i < values.length; i++) {
      result[i] = new BigInteger[values[i].length];
      for (int j = 0; j < values[i].length; j++) {
        result[i][j] = BigInteger.valueOf(values[i][j]);
      }
    }
    return result;
  }

  /** The squared euclidean length of the longest row. */
  private static BigInteger maxNormSquared(BigInteger[][] rows) {
    BigInteger max = BigInteger.ZERO;
    for (BigInteger[] row : rows) {
      BigInteger norm = LLL.normSquared(row);
      if (norm.compareTo(max) > 0) {
        max = norm;
      }
    }
    return max;
  }

  /**
   * Two bases generate the same lattice exactly when their row Hermite normal forms agree.
   */
  private static void assertSameLattice(BigInteger[][] expected, BigInteger[][] actual) {
    BigInteger[][] h1 = LLL.hermiteNormalForm(expected);
    BigInteger[][] h2 = LLL.hermiteNormalForm(actual);
    int rank1 = rank(h1);
    int rank2 = rank(h2);
    assertEquals(rank1, rank2, "different rank");
    for (int i = 0; i < rank1; i++) {
      assertArrayEquals(h1[i], h2[i], "row " + i + " of the Hermite normal form differs");
    }
  }

  private static int rank(BigInteger[][] hnf) {
    int rank = 0;
    for (BigInteger[] row : hnf) {
      for (BigInteger value : row) {
        if (value.signum() != 0) {
          rank++;
          break;
        }
      }
    }
    return rank;
  }

  @Test
  public void testSmallBasis() {
    BigInteger[][] reduced = LLL.reduce(matrix(new long[][] {{1, 2}, {3, 4}}));
    assertArrayEquals(matrix(new long[][] {{1, 0}, {0, 2}}), reduced);
  }

  @Test
  public void testClassicExample() {
    BigInteger[][] reduced = LLL.reduce(matrix(new long[][] {{1, 1, 1}, {-1, 0, 2}, {3, 5, 6}}));
    assertArrayEquals(matrix(new long[][] {{0, 1, 0}, {1, 0, 1}, {-1, 0, 2}}), reduced);
  }

  @Test
  public void testZeroRowsAreDropped() {
    assertEquals(0, LLL.reduce(matrix(new long[][] {{0, 0}, {0, 0}})).length);
    assertArrayEquals(matrix(new long[][] {{1, 0}}),
        LLL.reduce(matrix(new long[][] {{0, 0}, {1, 0}})));
    assertEquals(0, LLL.reduce(new BigInteger[0][]).length);
  }

  @Test
  public void testDependentRows() {
    // the second row is twice the first one
    assertArrayEquals(matrix(new long[][] {{1, 2}}),
        LLL.reduce(matrix(new long[][] {{1, 2}, {2, 4}})));
    // three rows spanning all of Z^2
    BigInteger[][] input = matrix(new long[][] {{1, 2}, {2, 4}, {3, 5}});
    BigInteger[][] reduced = LLL.reduce(input);
    assertEquals(2, reduced.length);
    assertSameLattice(input, reduced);
  }

  @Test
  public void testInputIsNotModified() {
    BigInteger[][] input = matrix(new long[][] {{201, 37}, {1648, 297}});
    BigInteger[][] copy = matrix(new long[][] {{201, 37}, {1648, 297}});
    LLL.reduce(input);
    assertArrayEquals(copy, input);
  }

  @Test
  public void testRaggedRowsAreRejected() {
    assertThrows(IllegalArgumentException.class,
        () -> LLL.reduce(new BigInteger[][] {{BigInteger.ONE, BigInteger.TWO}, {BigInteger.ONE}}));
  }

  @Test
  public void testDeltaOutOfRange() {
    BigInteger[][] input = matrix(new long[][] {{1, 2}, {3, 4}});
    assertThrows(IllegalArgumentException.class, () -> LLL.reduce(input, 1, 1));
    assertThrows(IllegalArgumentException.class, () -> LLL.reduce(input, 1, 4));
  }

  /**
   * On random input the reduction must preserve the lattice and must not make the basis longer.
   */
  @Test
  public void testRandomBasesPreserveTheLattice() {
    Random random = new Random(42);
    for (int round = 0; round < 40; round++) {
      int n = 2 + random.nextInt(4);
      long[][] values = new long[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          values[i][j] = random.nextInt(2001) - 1000;
        }
      }
      BigInteger[][] input = matrix(values);
      BigInteger[][] reduced = LLL.reduce(input);
      if (reduced.length == 0) {
        continue;
      }
      assertSameLattice(input, reduced);
      assertTrue(maxNormSquared(reduced).compareTo(maxNormSquared(input)) <= 0,
          "the reduced basis is longer than the input basis");
    }
  }
}
