package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * {@link IExpr#splitFreeFactors(IExpr)}, {@link IExpr#isPolynomialShape(Predicate)},
 * {@link IAST#chainToAnd()} and {@link IInteger#perfectPower()}.
 */
class ExpressionHelperMethodsTest {

  @Test
  void testSplitFreeFactors() {
    // Times(3, x, Sin(y), x^2): the factors free of x keep their order
    IExpr product = F.Times(F.C3, F.x, F.Sin(F.y), F.Power(F.x, F.C2));
    IExpr[] split = product.splitFreeFactors(F.x);
    assertEquals(F.Times(F.C3, F.Sin(F.y)), split[0]);
    assertEquals(F.Times(F.x, F.Power(F.x, F.C2)), split[1]);
    // a single factor is not unwrapped into Times
    split = F.Times(F.C5, F.Cos(F.x)).splitFreeFactors(F.x);
    assertArrayEquals(new IExpr[] {F.C5, F.Cos(F.x)}, split);
    // no Times: the whole expression is either free or not
    assertArrayEquals(new IExpr[] {F.Sin(F.y), F.C1}, F.Sin(F.y).splitFreeFactors(F.x));
    assertArrayEquals(new IExpr[] {F.C1, F.Sin(F.x)}, F.Sin(F.x).splitFreeFactors(F.x));
    // heads count: f(y) isn't free of f
    split = F.Times(F.unaryAST1(F.f, F.y), F.z).splitFreeFactors(F.f);
    assertArrayEquals(new IExpr[] {F.z, F.unaryAST1(F.f, F.y)}, split);
  }

  @Test
  void testIsPolynomialShape() {
    Predicate<IExpr> symbolsAndIntegers = x -> x.isSymbol() || x.isInteger();
    assertTrue(F.Plus(F.Times(F.C7, F.x), F.Power(F.y, F.C3), F.CN1)
        .isPolynomialShape(symbolsAndIntegers));
    assertTrue(F.Power(F.Plus(F.x, F.C1), F.C4).isPolynomialShape(symbolsAndIntegers));
    assertFalse(F.Power(F.x, F.CN1).isPolynomialShape(symbolsAndIntegers));
    assertFalse(F.Power(F.x, F.C1D2).isPolynomialShape(symbolsAndIntegers));
    assertFalse(F.Plus(F.x, F.Sin(F.y)).isPolynomialShape(symbolsAndIntegers));
    // the atoms decide: a fraction is no integer
    assertFalse(F.Times(F.C1D2, F.x).isPolynomialShape(symbolsAndIntegers));
    assertTrue(F.Times(F.C1D2, F.x).isPolynomialShape(x -> x.isSymbol() || x.isRational()));
  }

  @Test
  void testChainToAnd() {
    IAST chain = F.ternaryAST3(S.Less, F.C0, F.x, F.C2);
    assertEquals(F.And(F.Less(F.C0, F.x), F.Less(F.x, F.C2)), chain.chainToAnd());
    IAST four = F.quaternary(S.GreaterEqual, F.a, F.b, F.c, F.d);
    assertEquals(F.And(F.GreaterEqual(F.a, F.b), F.GreaterEqual(F.b, F.c),
        F.GreaterEqual(F.c, F.d)), four.chainToAnd());
    // a binary relation is its own single link
    assertEquals(F.And(F.Equal(F.x, F.y)), F.Equal(F.x, F.y).chainToAnd());
    assertEquals(0, F.unaryAST1(S.Less, F.x).chainToAnd().argSize());
  }

  @Test
  void testPerfectPower() {
    assertArrayEquals(new IInteger[] {F.C2, F.C4}, F.ZZ(16).perfectPower());
    assertArrayEquals(new IInteger[] {F.ZZ(6), F.C4}, F.ZZ(1296).perfectPower());
    // the largest exponent: 2^6*3^6 is 6^6, not 36^3 or 216^2
    assertArrayEquals(new IInteger[] {F.ZZ(6), F.ZZ(6)}, F.ZZ(46656).perfectPower());
    assertArrayEquals(new IInteger[] {F.C3, F.ZZ(41)},
        F.ZZ(3).powerRational(41).perfectPower());
    assertNull(F.ZZ(12).perfectPower());
    assertNull(F.ZZ(72).perfectPower());
    assertNull(F.C1.perfectPower());
    assertNull(F.C0.perfectPower());
    assertNull(F.ZZ(-8).perfectPower());
  }
}
