package org.matheclipse.core.polynomials;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Exact isolation of the real roots of a square-free integer polynomial. */
public class RealRootIsolationTest {

  /** The coefficients of <code>x^i</code> at index <code>i</code>. */
  private static BigInteger[] poly(long... coefficients) {
    BigInteger[] result = new BigInteger[coefficients.length];
    for (int i = 0; i < coefficients.length; i++) {
      result[i] = BigInteger.valueOf(coefficients[i]);
    }
    return result;
  }

  /** The product of <code>x - root</code> over the roots. */
  private static BigInteger[] fromRoots(long... roots) {
    BigInteger[] result = {BigInteger.ONE};
    for (long root : roots) {
      BigInteger[] next = new BigInteger[result.length + 1];
      java.util.Arrays.fill(next, BigInteger.ZERO);
      for (int i = 0; i < result.length; i++) {
        next[i + 1] = next[i + 1].add(result[i]);
        next[i] = next[i].subtract(result[i].multiply(BigInteger.valueOf(root)));
      }
      result = next;
    }
    return result;
  }

  @Test
  public void integerRootsAreFoundInOrder() {
    assertArrayEquals(new double[] {1.0, 2.0, 3.0},
        RealRootIsolation.realRoots(fromRoots(1, 2, 3)), 1e-15);
    assertArrayEquals(new double[] {-7.0, -1.0, 0.0, 4.0, 1000.0},
        RealRootIsolation.realRoots(fromRoots(1000, -1, 0, 4, -7)), 1e-12);
  }

  @Test
  public void irrationalRootsAreRefinedToMachinePrecision() {
    // x^2 - 2
    assertArrayEquals(new double[] {-Math.sqrt(2.0), Math.sqrt(2.0)},
        RealRootIsolation.realRoots(poly(-2, 0, 1)), 1e-15);
    // x^5 - x - 1 has one real root
    double[] roots = RealRootIsolation.realRoots(poly(-1, -1, 0, 0, 0, 1));
    assertEquals(1, roots.length);
    assertEquals(1.1673039782614187, roots[0], 1e-15);
  }

  @Test
  public void noRealRoot() {
    assertEquals(0, RealRootIsolation.realRoots(poly(1, 0, 1)).length);
    assertEquals(0, RealRootIsolation.realRoots(poly(5)).length);
  }

  @Test
  public void closeRootsAreSeparated() {
    // (x^2 - 2) * (10^6 * x^2 - 2000001): roots Sqrt(2) and Sqrt(2.000001)
    BigInteger[] p = poly(4000002, 0, -4000001, 0, 1000000);
    List<RealRootIsolation.RootInterval> intervals = RealRootIsolation.isolate(p);
    assertEquals(4, intervals.size());
    double[] roots = RealRootIsolation.realRoots(p);
    assertEquals(Math.sqrt(2.0), roots[2], 1e-15);
    assertEquals(Math.sqrt(2.000001), roots[3], 1e-15);
    assertTrue(roots[2] < roots[3]);
  }

  @Test
  public void evenDegreeWithTwoRealRootsAtHighDegree() {
    // x^40 - 3*x^7 + x - 5: a numeric root finder with a tolerance loses one of the two
    BigInteger[] p = new BigInteger[41];
    java.util.Arrays.fill(p, BigInteger.ZERO);
    p[0] = BigInteger.valueOf(-5);
    p[1] = BigInteger.ONE;
    p[7] = BigInteger.valueOf(-3);
    p[40] = BigInteger.ONE;
    double[] roots = RealRootIsolation.realRoots(p);
    assertEquals(2, roots.length);
    assertEquals(-1.023183268482096, roots[0], 1e-14);
    assertEquals(1.054297372306026, roots[1], 1e-14);
  }

  @Test
  public void aMultipleRootIsGivenUp() {
    // (x^2 - 2)^2 is not square-free: its double roots cannot be separated
    assertNull(RealRootIsolation.isolate(poly(4, 0, -4, 0, 1)));
    assertNull(RealRootIsolation.isolate(poly(0)));
  }
}
