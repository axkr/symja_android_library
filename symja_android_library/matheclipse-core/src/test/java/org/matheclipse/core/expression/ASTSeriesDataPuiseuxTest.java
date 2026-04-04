package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

class ASTSeriesDataPuiseuxTest {

  @BeforeAll
  static void setUp() {
    F.initSymja();
  }

  private EvalEngine engine() {
    return EvalEngine.get();
  }

  @Test
  void testPuiseuxDenominatorPreservedAfterConstruction() {
    // SeriesData[x, 0, {1, 2, 3}, 0, 6, 2] represents 1 + 2*x^(1/2) + 3*x
    ASTSeriesData sd = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2, F.C3), 0, 6, 2);
    assertEquals(2, sd.puiseuxDenominator());
    assertEquals(0, sd.minExponent());
    assertEquals(6, sd.truncateOrder());
  }

  @Test
  void testPuiseuxPlusPS_sameDenominator() {
    // s1 = 1 + x^(1/2), s2 = 2 + 3*x^(1/2)
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C1), 0, 4, 2);
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, F.list(F.C2, F.C3), 0, 4, 2);

    ASTSeriesData sum = s1.plusPS(s2);
    assertEquals(2, sum.puiseuxDenominator());
    // coefficient at index 0 => 1+2=3
    assertEquals(F.C3, sum.coefficient(0));
    // coefficient at index 1 => 1+3=4
    assertEquals(F.ZZ(4), sum.coefficient(1));
  }

  @Test
  void testPuiseuxPlusPS_differentDenominators() {
    // s1 denominator=2: coeff at 0 => 1 (i.e. constant term)
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, F.list(F.C1), 0, 4, 2);
    // s2 denominator=3: coeff at 0 => 2 (i.e. constant term)
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, F.list(F.C2), 0, 6, 3);

    ASTSeriesData sum = s1.plusPS(s2);
    // LCM(2,3) = 6
    assertEquals(6, sum.puiseuxDenominator());
    // constant term at scaled index 0 => 1+2=3
    assertEquals(F.C3, sum.coefficient(0));
  }

  @Test
  void testPuiseuxSubtractPS() {
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, F.list(F.C5, F.C3), 0, 4, 2);
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, F.list(F.C2, F.C1), 0, 4, 2);

    ASTSeriesData diff = s1.subtractPS(s2);
    assertEquals(2, diff.puiseuxDenominator());
    assertEquals(F.C3, diff.coefficient(0)); // 5-2
    assertEquals(F.C2, diff.coefficient(1)); // 3-1
  }

  @Test
  void testPuiseuxTimesPS() {
    // s1 = 1 + x^(1/2), s2 = 1 + x^(1/2)
    // (1+x^(1/2))^2 = 1 + 2*x^(1/2) + x
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C1), 0, 4, 2);
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C1), 0, 4, 2);

    ASTSeriesData product = s1.timesPS(s2);
    assertEquals(2, product.puiseuxDenominator());
    // index 0 => 1*1 = 1
    assertEquals(F.C1, product.coefficient(0));
    // index 1 => 1*1 + 1*1 = 2
    assertEquals(F.C2, product.coefficient(1));
    // index 2 => 1*1 = 1
    assertEquals(F.C1, product.coefficient(2));
  }

  @Test
  void testPuiseuxTimesPS_differentDenominators() {
    // s1 den=2, s2 den=3 => result den=6
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, F.list(F.C2), 0, 4, 2);
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, F.list(F.C3), 0, 6, 3);

    ASTSeriesData product = s1.timesPS(s2);
    assertEquals(6, product.puiseuxDenominator());
    // constant * constant => 6
    assertEquals(F.ZZ(6), product.coefficient(0));
  }

  @Test
  void testPuiseuxNegate() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2), 0, 4, 2);
    ASTSeriesData neg = s.negate();
    assertEquals(2, neg.puiseuxDenominator());
    assertEquals(F.CN1, neg.coefficient(0));
    assertEquals(F.CN2, neg.coefficient(1));
  }

  @Test
  void testPuiseuxDerivative() {
    // s = 1 + 2*x^(1/2) + 3*x => coeffs at indices 0,1,2 with den=2
    // d/dx(1) = 0
    // d/dx(2*x^(1/2)) = 2*(1/2)*x^(-1/2) = x^(-1/2)
    // d/dx(3*x) = 3*x^(1/2) [in puiseux index: 3 at index 1, with den=2 exponent = 1/2]
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2, F.C3), 0, 6, 2);
    ASTSeriesData ds = s.derive(F.x);
    assertEquals(2, ds.puiseuxDenominator());
    // The derivative shifts indices by -puiseuxDenominator = -2
    // Original index 1 (x^(1/2)): coeff 2 * (1/2) = 1 at new index -1
    assertEquals(F.C1, ds.coefficient(-1));
    // Original index 2 (x^1): coeff 3 * (2/2) = 3 at new index 0
    assertEquals(F.C3, ds.coefficient(0));
  }

  @Test
  void testPuiseuxIntegrate() {
    // s = x^(-1/2) at index -1 with den=2 => integrate => 2*x^(1/2) at index 1
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 4, 2);
    s.setCoeff(-1, F.C1);

    ASTSeriesData integral = s.integrate(F.x);
    assertNotNull(integral);
    assertEquals(2, integral.puiseuxDenominator());
    // index -1 + 2 = 1, multiplier = 2/(-1+2) = 2/1 = 2
    assertEquals(F.C2, integral.coefficient(1));
  }

  @Test
  void testPuiseuxNormal() {
    // 1 + 2*x^(1/2) with den=2
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2), 0, 4, 2);
    IExpr normalForm = s.normal(false);
    // Should produce Plus(Times(1, Power(x, 0)), Times(2, Power(x, 1/2)))
    assertNotNull(normalForm);
    assertTrue(normalForm.isPlus() || normalForm.isTimes() || normalForm.isAtom());
  }

  @Test
  void testPuiseuxPowerSeries_squareRoot() {
    // s = 1 + x (den=1), raise to power 1/2
    // (1+x)^(1/2) = 1 + (1/2)x - (1/8)x^2 + ...
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C1), 0, 6, 1);
    IExpr result = s.powerSeries(F.QQ(1, 2), engine());
    assertInstanceOf(ASTSeriesData.class, result);
    ASTSeriesData ps = (ASTSeriesData) result;
    // denominator should be 1*2 = 2
    assertEquals(2, ps.puiseuxDenominator());
    // Leading coefficient (constant) should be 1
    assertEquals(F.C1, ps.coefficient(0));
  }

  @Test
  void testPuiseuxPowerSeries_integerPower() {
    // s = 1 + x^(1/2) (den=2), raise to power 2
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C1), 0, 6, 2);
    IExpr result = s.powerSeries(F.QQ(2, 1), engine());
    assertInstanceOf(ASTSeriesData.class, result);
    ASTSeriesData ps = (ASTSeriesData) result;
    // (1+x^(1/2))^2 = 1 + 2*x^(1/2) + x
    assertEquals(F.C1, ps.coefficient(0));
  }

  @Test
  void testPuiseuxInverse() {
    // s = 2 + x^(1/2) (den=2)
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C2, F.C1), 0, 6, 2);
    ASTSeriesData inv = s.inverse();
    assertEquals(2, inv.puiseuxDenominator());
    // Leading coeff of inverse should be 1/2
    assertEquals(F.C1D2, inv.coefficient(0));
  }

  @Test
  void testPuiseuxInverse_leadingNonZeroNotAtZero() {
    // s = 0 + x^(1/2) (leading at index 1, den=2)
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, 0, 6, 2);
    s.setCoeff(1, F.C1);

    ASTSeriesData inv = s.inverse();
    assertEquals(2, inv.puiseuxDenominator());
    // inverse of x^(1/2) should have leading term at index -1
    assertEquals(F.C1, inv.coefficient(-1));
  }

  @Test
  void testIsInvertible_puiseux_leadingAtZero() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C3, F.C1), 0, 4, 2);
    assertTrue(s.isNonZero());
  }

  @Test
  void testIsInvertible_puiseux_leadingNotAtZero() {
    // Only x^(1/2) term, no constant
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, 0, 4, 2);
    s.setCoeff(1, F.C1);
    assertTrue(s.isNonZero());
  }

  @Test
  void testPuiseuxCopy() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2, F.C3), 0, 6, 3);
    ASTSeriesData c = s.copy();
    assertEquals(s.puiseuxDenominator(), c.puiseuxDenominator());
    assertEquals(s.minExponent(), c.minExponent());
    assertEquals(s.truncateOrder(), c.truncateOrder());
    // Modify copy, original unchanged
    c.setCoeff(0, F.C0);
    assertEquals(F.C1, s.coefficient(0));
  }

  @Test
  void testPuiseuxEquals() {
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2), 0, 4, 2);
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2), 0, 4, 2);
    ASTSeriesData s3 = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2), 0, 4, 3);

    assertEquals(s1, s2);
    assertNotEquals(s1, s3, "Different puiseuxDenominator should not be equal");
  }

  @Test
  void testPuiseuxArg6() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1), 0, 4, 5);
    assertEquals(F.ZZ(5), s.arg6());
    assertEquals(F.ZZ(5), s.get(6));
  }

  @Test
  void testPuiseuxShift() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2), 0, 4, 2);
    ASTSeriesData shifted = s.shift(2);
    assertEquals(2, shifted.puiseuxDenominator());
    assertEquals(F.C1, shifted.coefficient(2));
    assertEquals(F.C2, shifted.coefficient(3));
    assertEquals(F.C0, shifted.coefficient(0));
  }

  @Test
  void testPuiseuxIsOrder() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, 0, 4, 2);
    assertTrue(s.isOrder());

    s.setCoeff(1, F.C1);
    assertFalse(s.isOrder());
  }

  @Test
  void testPuiseuxIsProbableZero() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, 0, 4, 3);
    assertTrue(s.isProbableZero());

    s.setCoeff(2, F.C5);
    assertFalse(s.isProbableZero());
  }

  @Test
  void testPuiseuxCoefficientList_nonZeroExpansionPoint() {
    // This exercises the branch where expansionPoint is not zero
    ASTSeriesData s = new ASTSeriesData(F.x, F.C1, F.list(F.C1, F.C2), 0, 4, 2);
    IAST cl = s.coefficientList();
    assertNotNull(cl);
  }

  @Test
  void testPuiseuxSqrPS() {
    // (1 + x^(1/2))^2 via sqrPS
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C1), 0, 4, 2);
    ASTSeriesData sq = s.sqrPS();
    assertEquals(2, sq.puiseuxDenominator());
    assertEquals(F.C1, sq.coefficient(0)); // 1*1
    assertEquals(F.C2, sq.coefficient(1)); // 1*1 + 1*1
    assertEquals(F.C1, sq.coefficient(2)); // 1*1
  }

  @Test
  void testPuiseuxPlusScalar() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2), 0, 4, 2);
    ASTSeriesData result = s.plus(F.C3);
    assertEquals(2, result.puiseuxDenominator());
    assertEquals(F.ZZ(4), result.coefficient(0)); // 1+3
    assertEquals(F.C2, result.coefficient(1)); // unchanged
  }

  @Test
  void testPuiseuxTimesScalar() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C2, F.C3), 0, 4, 2);
    ASTSeriesData result = s.times(F.C2);
    assertEquals(2, result.puiseuxDenominator());
    assertEquals(F.ZZ(4), result.coefficient(0));
    assertEquals(F.ZZ(6), result.coefficient(1));
  }

  @Test
  void testPuiseuxSubtractScalar() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C5, F.C2), 0, 4, 2);
    ASTSeriesData result = s.subtract(F.C3);
    assertEquals(2, result.puiseuxDenominator());
    assertEquals(F.C2, result.coefficient(0)); // 5-3
    assertEquals(F.C2, result.coefficient(1)); // unchanged
  }

  @Test
  void testPuiseuxToSeriesData() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1, F.C2, F.C3), 0, 6, 2);
    IAST sd = s.toSeriesData();
    // arg6 should be the puiseux denominator
    assertEquals(F.ZZ(2), sd.get(6));
    assertEquals(S.SeriesData, sd.head());
  }

  @Test
  void testPuiseuxFullFormString() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, F.list(F.C1), 0, 4, 2);
    String ff = s.fullFormString();
    assertTrue(ff.contains("SeriesData("));
    assertTrue(ff.endsWith("2)"));
  }

  @Test
  void testPuiseuxCompareTo_differentDenominators() {
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, F.list(F.C1), 0, 4, 2);
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, F.list(F.C1), 0, 4, 3);
    assertNotEquals(0, s1.compareTo(s2));
  }
}
