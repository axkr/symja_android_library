package org.matheclipse.core.sympy.ntheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Tests for {@link ResidueNTheory#sqrtMod(IInteger, IInteger)} and
 * {@link ResidueNTheory#nthRootMod(IInteger, IInteger, IInteger)}, exercising prime, prime-power
 * and composite moduli.
 */
public class ResidueNTheoryTest extends ExprEvaluatorTestCase {

  /** Verifies that {@code root^n mod m == a}. */
  private static void assertIsRoot(IExpr root, long a, long n, long m) {
    assertTrue(root.isInteger(), () -> "expected integer root, got " + root);
    BigInteger r = ((IInteger) root).toBigNumerator();
    BigInteger pow = r.modPow(BigInteger.valueOf(n), BigInteger.valueOf(m));
    assertEquals(BigInteger.valueOf(a).mod(BigInteger.valueOf(m)), pow);
  }

  @Test
  public void testSqrtMod_compositeReportedCase() {
    // PowerMod(100, 1/2, 17 * 19 * 23) == 10
    IExpr r = ResidueNTheory.sqrtMod(F.ZZ(100), F.ZZ(17 * 19 * 23));
    assertEquals(F.ZZ(10), r);
  }

  @Test
  public void testSqrtMod_primeRegression() {
    // 2 is a QR mod 7: 3^2 = 9 ≡ 2 (mod 7), so roots are {3, 4}, smallest is 3.
    IExpr r = ResidueNTheory.sqrtMod(F.ZZ(2), F.ZZ(7));
    assertEquals(F.ZZ(3), r);
  }

  @Test
  public void testSqrtMod_noSolutionPrime() {
    // 3 is not a QR mod 7.
    assertFalse(ResidueNTheory.sqrtMod(F.ZZ(3), F.ZZ(7)).isPresent());
  }

  @Test
  public void testSqrtMod_oddPrimePower() {
    // Roots of x^2 = 6 (mod 25): 9 and 16.
    IExpr r = ResidueNTheory.sqrtMod(F.ZZ(6), F.ZZ(25));
    assertEquals(F.ZZ(9), r);
    assertIsRoot(r, 6, 2, 25);
  }

  @Test
  public void testSqrtMod_oddPrimePowerHigher() {
    // Roots of x^2 = 7 (mod 27): 13 and 14.
    IExpr r = ResidueNTheory.sqrtMod(F.ZZ(7), F.ZZ(27));
    assertEquals(F.ZZ(13), r);
    assertIsRoot(r, 7, 2, 27);
  }

  @Test
  public void testSqrtMod_powerOfTwo() {
    // a ≡ 1 (mod 8) is required for k >= 3. a = 17 ≡ 1 mod 8, mod 32.
    IExpr r = ResidueNTheory.sqrtMod(F.ZZ(17), F.ZZ(32));
    assertTrue(r.isInteger());
    assertIsRoot(r, 17, 2, 32);
  }

  @Test
  public void testSqrtMod_compositeSquareFreeRoundtrip() {
    // m = 3 * 5 * 11 = 165; pick a square 49 (= 7^2). Expect 7 (smallest root).
    IExpr r = ResidueNTheory.sqrtMod(F.ZZ(49), F.ZZ(165));
    assertEquals(F.ZZ(7), r);
    assertIsRoot(r, 49, 2, 165);
  }

  @Test
  public void testSqrtMod_compositeNoSolution() {
    // 3 is not a QR mod 7 → no solution mod 7*11 either.
    assertFalse(ResidueNTheory.sqrtMod(F.ZZ(3), F.ZZ(7 * 11)).isPresent());
  }

  @Test
  public void testSqrtMod_zeroResidue() {
    // x^2 = 0 (mod 12) → smallest non-negative root is 0.
    IExpr r = ResidueNTheory.sqrtMod(F.C0, F.ZZ(12));
    assertEquals(F.C0, r);
  }

  // -------------------------------------------------------------------------
  // nthRootMod
  // -------------------------------------------------------------------------

  @Test
  public void testNthRootMod_primeFastPath() {
    IExpr r = ResidueNTheory.nthRootMod(F.ZZ(6), F.ZZ(3), F.ZZ(7));
    assertEquals(F.ZZ(3), r);
    assertIsRoot(r, 6, 3, 7);
  }

  @Test
  public void testNthRootMod_compositeCoprime() {
    // x^3 = 8 (mod 35): 2^3 = 8, and gcd(3, phi(5)) = 1, gcd(3, phi(7)) = 3.
    IExpr r = ResidueNTheory.nthRootMod(F.ZZ(8), F.ZZ(3), F.ZZ(35));
    assertTrue(r.isInteger());
    assertIsRoot(r, 8, 3, 35);
  }

  @Test
  public void testNthRootMod_primePowerCoprime() {
    // x^3 = 8 (mod 25): gcd(3, 20) = 1, so unique cube root via modular inverse.
    IExpr r = ResidueNTheory.nthRootMod(F.ZZ(8), F.ZZ(3), F.ZZ(25));
    assertTrue(r.isInteger());
    assertIsRoot(r, 8, 3, 25);
  }
}
