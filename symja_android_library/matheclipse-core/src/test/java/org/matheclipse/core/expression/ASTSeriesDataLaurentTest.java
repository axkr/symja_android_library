package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;

class ASTSeriesDataLaurentTest {

  private EvalEngine engine;

  @BeforeAll
  static void initSymja() {
    F.initSymbols();
  }

  @BeforeEach
  void setUp() {
    engine = EvalEngine.get();
  }

  // --- Construction with negative minExponent ---

  @Test
  void testLaurentSeriesConstruction() {
    // Series: 1/x + 2 + 3*x, i.e. coefficients at indices -1, 0, 1
    ASTSeriesData series = new ASTSeriesData(F.x, F.C0, -1, 4, 1);
    series.setCoeff(-1, F.C1);
    series.setCoeff(0, F.C2);
    series.setCoeff(1, F.C3);

    assertEquals(-1, series.minExponent());
    assertEquals(F.C1, series.coefficient(-1));
    assertEquals(F.C2, series.coefficient(0));
    assertEquals(F.C3, series.coefficient(1));
  }

  @Test
  void testCoefficientOutOfBoundsReturnsZero() {
    ASTSeriesData series = new ASTSeriesData(F.x, F.C0, -2, 3, 1);
    series.setCoeff(-1, F.C5);

    // Below minExponent
    assertEquals(F.C0, series.coefficient(-5));
    // Above exponentBound
    assertEquals(F.C0, series.coefficient(10));
  }

  // --- Arithmetic: plus ---

  @Test
  void testPlusLaurentSeries() {
    // s1 = 1/x^2 + 1/x
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, -2, 3, 1);
    s1.setCoeff(-2, F.C1);
    s1.setCoeff(-1, F.C1);

    // s2 = -1/x^2 + 3
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, -2, 3, 1);
    s2.setCoeff(-2, F.CN1);
    s2.setCoeff(0, F.C3);

    ASTSeriesData sum = s1.plusPS(s2);

    // 1/x^2 - 1/x^2 = 0 at index -2
    assertEquals(F.C0, sum.coefficient(-2));
    // 1/x at index -1
    assertEquals(F.C1, sum.coefficient(-1));
    // 3 at index 0
    assertEquals(F.C3, sum.coefficient(0));
  }

  @Test
  void testPlusScalarToLaurentSeries() {
    // s = 2/x
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s.setCoeff(-1, F.C2);

    ASTSeriesData result = s.plus(F.C5);

    // The pole term should be unchanged
    assertEquals(F.C2, result.coefficient(-1));
    // Constant term should now be 5
    assertEquals(F.C5, result.coefficient(0));
  }

  // --- Arithmetic: subtract ---

  @Test
  void testSubtractLaurentSeries() {
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s1.setCoeff(-1, F.C3);
    s1.setCoeff(0, F.C1);

    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s2.setCoeff(-1, F.C3);
    s2.setCoeff(0, F.C4);

    ASTSeriesData diff = s1.subtractPS(s2);

    assertEquals(F.C0, diff.coefficient(-1));
    // 1 - 4 = -3
    assertEquals(F.CN3, diff.coefficient(0));
  }

  // --- Arithmetic: times ---

  @Test
  void testTimesLaurentSeries() {
    // s1 = 1/x, s2 = x => product should be 1 (constant)
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s1.setCoeff(-1, F.C1);

    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, 1, 4, 1);
    s2.setCoeff(1, F.C1);

    ASTSeriesData product = s1.timesPS(s2);

    assertEquals(F.C1, product.coefficient(0));
    // No pole terms
    assertEquals(F.C0, product.coefficient(-1));
    assertEquals(F.C0, product.coefficient(1));
  }

  @Test
  void testTimesScalarLaurent() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -2, 2, 1);
    s.setCoeff(-2, F.C3);
    s.setCoeff(0, F.C1);

    ASTSeriesData result = s.times(F.C2);

    assertEquals(F.C6, result.coefficient(-2));
    assertEquals(F.C2, result.coefficient(0));
  }

  // --- Arithmetic: negate ---

  @Test
  void testNegateLaurentSeries() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 2, 1);
    s.setCoeff(-1, F.C3);
    s.setCoeff(0, F.CN2);

    ASTSeriesData neg = s.negate();

    assertEquals(F.CN3, neg.coefficient(-1));
    assertEquals(F.C2, neg.coefficient(0));
  }

  // --- Inverse ---

  @Test
  void testInverseOfSimplePole() {
    // s = 1/x => inverse should be x (leading exponent flips sign)
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s.setCoeff(-1, F.C1);

    ASTSeriesData inv = s.inverse();

    // The leading term of inverse of 1/x is x, i.e. coefficient 1 at index 1
    assertEquals(F.C1, inv.coefficient(1));
    assertEquals(F.C0, inv.coefficient(0));
  }

  @Test
  void testInverseOfConstantSeries() {
    // s = 2 + 0*x + ... => inverse = 1/2
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, 0, 4, 1);
    s.setCoeff(0, F.C2);

    ASTSeriesData inv = s.inverse();

    assertEquals(F.C1D2, inv.coefficient(0));
  }

  @Test
  void testInverseOfZeroSeriesThrows() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, 0, 4, 1);
    // All zero => should throw
    assertThrows(Exception.class, s::inverse);
  }

  // --- Division ---

  @Test
  void testDivideLaurentSeries() {
    // s1 = 1/x^2, s2 = 1/x => s1/s2 = 1/x
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, -2, 4, 1);
    s1.setCoeff(-2, F.C1);

    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, -1, 4, 1);
    s2.setCoeff(-1, F.C1);

    ASTSeriesData quotient = s1.dividePS(s2);

    assertEquals(F.C1, quotient.coefficient(-1));
    assertEquals(F.C0, quotient.coefficient(0));
  }

  @Test
  void testDivideByZeroSeriesThrows() {
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s1.setCoeff(-1, F.C1);

    ASTSeriesData zero = new ASTSeriesData(F.x, F.C0, 0, 3, 1);

    assertThrows(ArithmeticException.class, () -> s1.dividePS(zero));
  }

  // --- Derivative ---

  @Test
  void testDeriveLaurentSeries() {
    // s = 1/x => d/dx(1/x) = -1/x^2
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 4, 1);
    s.setCoeff(-1, F.C1);

    ASTSeriesData ds = s.derive(F.x);

    // coefficient at -2 should be -1 (derivative of x^{-1} is -x^{-2})
    assertEquals(F.CN1, ds.coefficient(-2));
    assertEquals(F.C0, ds.coefficient(-1));
  }

  @Test
  void testDeriveDoublePole() {
    // s = 3/x^2 => d/dx = -6/x^3
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -2, 4, 1);
    s.setCoeff(-2, F.C3);

    ASTSeriesData ds = s.derive(F.x);

    // coefficient at -3: 3 * (-2) = -6
    assertEquals(F.ZZ(-6), ds.coefficient(-3));
  }

  // --- Integration ---

  @Test
  void testIntegrateLaurentSeries() {
    // s = -1/x^2 => integral = 1/x
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -2, 4, 1);
    s.setCoeff(-2, F.CN1);

    ASTSeriesData intS = s.integrate(F.x);

    // integral of -x^{-2} is x^{-1}, coefficient 1 at index -1
    assertEquals(F.C1, intS.coefficient(-1));
  }

  @Test
  void testIntegrateSkipsLogTerm() {
    // s = 1/x => integral has log(x) term at index 0 which is skipped (i + den == 0)
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 4, 1);
    s.setCoeff(-1, F.C1);
    s.setCoeff(0, F.C2);

    ASTSeriesData intS = s.integrate(F.x);

    // The 1/x term (index -1) cannot be integrated to a polynomial term;
    // index 0 = -1 + 1, skipped because i + puiseuxDenominator == 0
    assertEquals(F.C0, intS.coefficient(0));
    // constant 2 integrates to 2*x at index 1
    assertEquals(F.C2, intS.coefficient(1));
  }

  // --- Shift ---

  @Test
  void testShiftLaurentSeries() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s.setCoeff(-1, F.C5);
    s.setCoeff(0, F.C2);

    ASTSeriesData shifted = s.shift(2);

    assertEquals(F.C5, shifted.coefficient(1));
    assertEquals(F.C2, shifted.coefficient(2));
    assertEquals(F.C0, shifted.coefficient(-1));
  }

  // --- Puiseux (fractional exponents) with negative terms ---

  @Test
  void testPuiseuxLaurentSeries() {
    // denominator=2 means exponents are multiples of 1/2
    // index -1 => exponent -1/2
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 5, 2);
    s.setCoeff(-1, F.C1);
    s.setCoeff(0, F.C2);

    assertEquals(2, s.puiseuxDenominator());
    assertEquals(F.C1, s.coefficient(-1));
    assertEquals(F.C2, s.coefficient(0));
  }

  // --- isOrder / isProbableZero / isNonZero ---

  @Test
  void testIsOrderLaurent() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -2, 3, 1);
    // No coefficients set => all zero => isOrder
    assertTrue(s.isOrder());
    assertFalse(s.isNonZero());
    assertTrue(s.isProbableZero());
  }

  @Test
  void testIsNonZeroLaurent() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -2, 3, 1);
    s.setCoeff(-2, F.C1);

    assertTrue(s.isNonZero());
    assertFalse(s.isOrder());
    assertFalse(s.isProbableZero());
  }

  // --- normal() for Laurent series ---

  @Test
  void testNormalLaurentSeries() {
    // s = 3/x + 2 => normal should produce 3*x^(-1) + 2*x^0
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s.setCoeff(-1, F.C3);
    s.setCoeff(0, F.C2);

    IExpr normalExpr = s.normal(false);
    assertNotNull(normalExpr);
    assertTrue(normalExpr.isPlus() || normalExpr.isTimes() || normalExpr.isPresent());

    // Evaluate the normal form at x=1 => 3 + 2 = 5
    IExpr atOne = engine.evaluate(F.subst(normalExpr, F.x, F.C1));
    assertEquals(F.C5, atOne);
  }

  // --- equals / hashCode ---

  @Test
  void testEqualsLaurentSeries() {
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s1.setCoeff(-1, F.C2);

    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s2.setCoeff(-1, F.C2);

    assertEquals(s1, s2);
    assertEquals(s1.hashCode(), s2.hashCode());
  }

  @Test
  void testNotEqualsLaurentSeriesDifferentCoeff() {
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s1.setCoeff(-1, F.C2);

    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s2.setCoeff(-1, F.C3);

    assertNotEquals(s1, s2);
  }

  // --- copy / clone ---

  @Test
  void testCopyLaurentSeries() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -2, 3, 1);
    s.setCoeff(-2, F.C7);
    s.setCoeff(0, F.C1);

    ASTSeriesData copy = s.copy();
    assertEquals(s, copy);

    // Mutating copy should not affect original
    copy.setCoeff(-2, F.C0);
    assertEquals(F.C7, s.coefficient(-2));
  }

  // --- powerSeries with Laurent input ---

  @Test
  void testPowerSeriesSquareOfLaurent() {
    // s = 1/x => s^2 = 1/x^2
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 4, 1);
    s.setCoeff(-1, F.C1);

    IExpr result = s.powerSeries(F.C2, engine);
    assertTrue(result instanceof ASTSeriesData);

    ASTSeriesData sq = (ASTSeriesData) result;
    assertEquals(F.C1, sq.coefficient(-2));
    assertEquals(F.C0, sq.coefficient(-1));
    assertEquals(F.C0, sq.coefficient(0));
  }

  // --- setZero edge case ---

  @Test
  void testSetZeroUpdatesMinExponent() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -2, 4, 1);
    s.setCoeff(-2, F.C1);
    s.setCoeff(-1, F.C2);
    s.setCoeff(0, F.C3);

    assertEquals(-2, s.minExponent());

    s.setZero(-2);
    assertEquals(-1, s.minExponent());
    assertEquals(F.C0, s.coefficient(-2));
  }

  @Test
  void testSetZeroAllTermsMakesEmpty() {
    ASTSeriesData s = new ASTSeriesData(F.x, F.C0, -1, 3, 1);
    s.setCoeff(-1, F.C1);

    s.setZero(-1);
    assertTrue(s.isProbableZero());
  }

  // --- Composition with Laurent series ---

  @Test
  void testComposeWithMatchingExpansionPoint() {
    // s1 = 1 + x (expansion around 0)
    ASTSeriesData s1 = new ASTSeriesData(F.x, F.C0, 0, 4, 1);
    s1.setCoeff(0, F.C1);
    s1.setCoeff(1, F.C1);

    // s2 = x (identity), coefficient(0)=0=expansionPoint of s1
    ASTSeriesData s2 = new ASTSeriesData(F.x, F.C0, 0, 4, 1);
    s2.setCoeff(1, F.C1);

    ASTSeriesData composed = s1.compose(s2);
    assertNotNull(composed);
    assertEquals(F.C1, composed.coefficient(0));
    assertEquals(F.C1, composed.coefficient(1));
  }

  // --- seriesDataRecursive for 1/x ---

  @Test
  void testSeriesDataRecursiveForOneOverX() {
    // Series[1/x, {x, 0, 3}] should produce a Laurent series with 1/x term
    IExpr expr = F.Power(F.x, F.CN1);
    ASTSeriesData result = ASTSeriesData.seriesDataRecursive(expr, F.x, F.C0, 3, engine);

    assertNotNull(result, "Series expansion of 1/x should not be null");
    assertTrue(result.minExponent() <= -1, "minExponent should be <= -1 for 1/x");
    assertEquals(F.C1, result.coefficient(-1));
  }

  // --- seriesDataRecursive for 1/x^2 ---

  @Test
  void testSeriesDataRecursiveForOneOverXSquared() {
    IExpr expr = F.Power(F.x, F.ZZ(-2));
    ASTSeriesData result = ASTSeriesData.seriesDataRecursive(expr, F.x, F.C0, 3, engine);

    assertNotNull(result, "Series expansion of 1/x^2 should not be null");
    assertTrue(result.minExponent() <= -2, "minExponent should be <= -2 for 1/x^2");
    assertEquals(F.C1, result.coefficient(-2));
  }
}
