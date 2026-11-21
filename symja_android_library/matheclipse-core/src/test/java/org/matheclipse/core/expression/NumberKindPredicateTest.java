package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.expression.data.TimeObjectExpr;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The type predicates {@link IExpr#isDoubleNumber()}, {@link IExpr#isNonFiniteDoubleNumber()},
 * {@link IExpr#isArbitraryPrecisionNumber()} and {@link IExpr#isDateOrTimeObject()}.
 */
class NumberKindPredicateTest {

  private static final IExpr MACHINE_REAL = Num.valueOf(-2.75);
  private static final IExpr MACHINE_COMPLEX = ComplexNum.valueOf(0.5, -3.0);
  private static final IExpr LONG_LITERAL = new NumStr("0.12345678901234567890123");
  private static final IExpr APFLOAT = ApfloatNum.valueOf(new Apfloat("7.25", 30));
  private static final IExpr APCOMPLEX =
      ApcomplexNum.valueOf(new Apcomplex(new Apfloat("1.5", 30), new Apfloat("-4", 30)));

  @Test
  void testDoubleNumber() {
    assertTrue(MACHINE_REAL.isDoubleNumber());
    assertTrue(MACHINE_COMPLEX.isDoubleNumber());
    // a literal with more digits than a machine number is still double based
    assertTrue(LONG_LITERAL.isDoubleNumber());
    assertFalse(LONG_LITERAL.isMachineNumber());
    assertTrue(Num.valueOf(Double.NEGATIVE_INFINITY).isDoubleNumber());
    assertFalse(APFLOAT.isDoubleNumber());
    assertFalse(APCOMPLEX.isDoubleNumber());
    assertFalse(F.QQ(3, 7).isDoubleNumber());
    assertFalse(F.CI.isDoubleNumber());
    assertFalse(F.x.isDoubleNumber());
  }

  @Test
  void testNonFiniteDoubleNumber() {
    assertFalse(MACHINE_REAL.isNonFiniteDoubleNumber());
    assertFalse(MACHINE_COMPLEX.isNonFiniteDoubleNumber());
    assertTrue(Num.valueOf(Double.POSITIVE_INFINITY).isNonFiniteDoubleNumber());
    assertTrue(Num.valueOf(Double.NaN).isNonFiniteDoubleNumber());
    // either part of a complex number counts
    assertTrue(ComplexNum.valueOf(Double.NEGATIVE_INFINITY, 1.0).isNonFiniteDoubleNumber());
    assertTrue(ComplexNum.valueOf(1.0, Double.POSITIVE_INFINITY).isNonFiniteDoubleNumber());
    assertTrue(ComplexNum.valueOf(2.0, Double.NaN).isNonFiniteDoubleNumber());
    assertFalse(APFLOAT.isNonFiniteDoubleNumber());
    assertFalse(F.CInfinity.isNonFiniteDoubleNumber());
  }

  @Test
  void testArbitraryPrecisionNumber() {
    assertTrue(APFLOAT.isArbitraryPrecisionNumber());
    assertTrue(APCOMPLEX.isArbitraryPrecisionNumber());
    assertFalse(MACHINE_REAL.isArbitraryPrecisionNumber());
    assertFalse(MACHINE_COMPLEX.isArbitraryPrecisionNumber());
    assertFalse(LONG_LITERAL.isArbitraryPrecisionNumber());
    assertFalse(F.ZZ(12).isArbitraryPrecisionNumber());
    assertFalse(F.list(APFLOAT).isArbitraryPrecisionNumber());
  }

  @Test
  void testDateOrTimeObject() {
    assertTrue(DateObjectExpr.newInstance(LocalDateTime.of(2031, 4, 9, 6, 30)).isDateOrTimeObject());
    assertTrue(TimeObjectExpr.newInstance(LocalTime.of(17, 5, 12)).isDateOrTimeObject());
    assertFalse(F.stringx("2031-04-09").isDateOrTimeObject());
    assertFalse(F.list(F.ZZ(2031), F.C4, F.ZZ(9)).isDateOrTimeObject());
  }
}
