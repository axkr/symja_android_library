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
import org.matheclipse.core.interfaces.IRational;

/**
 * {@link IExpr#splitFreeFactors(IExpr)}, {@link IExpr#isPolynomialShape(Predicate)},
 * {@link IExpr#isRationalFunctionShape(Predicate)}, {@link IExpr#hasRadicalIn(IExpr)},
 * {@link IExpr#hasEvenRootIn(IExpr)}, {@link IExpr#isFreeOfInfinityOrIndeterminate()},
 * {@link IExpr#isHalfIntegerPower()}, {@link IExpr#isRationalRadical()},
 * {@link IExpr#powerExponentOf(IExpr)}, the type predicates of {@link #testTypePredicates()},
 * {@link IAST#chainToAnd()}, {@link IAST#ruleValue(IExpr)},
 * {@link IInteger#perfectPower()} and {@link IRational#modularResidue(IInteger)}.
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
    // small primes: the multiplicities 4 and 2 of 2 and 1031 allow a square only
    assertArrayEquals(new IInteger[] {F.ZZ(4124), F.C2}, F.ZZ(17007376).perfectPower());
    // a prime above the small primes, alone and together with a small one
    IInteger large = F.ZZ(1000003);
    assertArrayEquals(new IInteger[] {large, F.C3}, large.powerRational(3).perfectPower());
    assertArrayEquals(new IInteger[] {F.ZZ(4000012), F.C3},
        F.ZZ(64).multiply(large.powerRational(3)).perfectPower());
    assertNull(F.C4.multiply(large).perfectPower());
  }

  @Test
  void testIsRationalFunctionShape() {
    Predicate<IExpr> freeOfXOrX = e -> e.isFree(F.x, true) || e.equals(F.x);
    // negative integer powers are allowed, unlike for the polynomial shape
    IExpr quotient = F.Times(F.Plus(F.x, F.C1), F.Power(F.Plus(F.Sqr(F.x), F.a), F.CN2));
    assertTrue(quotient.isRationalFunctionShape(freeOfXOrX));
    assertFalse(quotient.isPolynomialShape(freeOfXOrX));
    assertFalse(F.Power(F.x, F.C1D2).isRationalFunctionShape(freeOfXOrX));
    assertFalse(F.Plus(F.x, F.Sin(F.x)).isRationalFunctionShape(freeOfXOrX));
    // an accepted atom is accepted whole: Sqrt(a) is free of x
    assertTrue(F.Times(F.Sqrt(F.a), F.Power(F.x, F.CN1)).isRationalFunctionShape(freeOfXOrX));
  }

  @Test
  void testHasRadicalIn() {
    assertTrue(F.Plus(F.C1, F.Sqrt(F.x)).hasRadicalIn(F.x));
    assertTrue(F.Sin(F.Power(F.Plus(F.x, F.C1), F.QQ(2, 3))).hasRadicalIn(F.x));
    assertFalse(F.Times(F.Sqrt(F.C2), F.x).hasRadicalIn(F.x));
    assertFalse(F.Power(F.x, F.C3).hasRadicalIn(F.x));
    assertFalse(F.Power(F.x, F.y).hasRadicalIn(F.x));
  }

  @Test
  void testHasEvenRootIn() {
    assertTrue(F.Sqrt(F.x).hasEvenRootIn(F.x));
    assertTrue(F.Plus(F.C1, F.Power(F.x, F.QQ(3, 4))).hasEvenRootIn(F.x));
    // an odd root has no branch cut on the real line
    assertFalse(F.Power(F.x, F.QQ(1, 3)).hasEvenRootIn(F.x));
    assertFalse(F.Times(F.Sqrt(F.y), F.x).hasEvenRootIn(F.x));
  }

  @Test
  void testModularResidue() {
    IInteger seven = F.ZZ(7);
    assertEquals(F.C3, F.ZZ(10).modularResidue(seven));
    assertEquals(F.C4, F.ZZ(-3).modularResidue(seven));
    // 1/3 == 5 mod 7, because 3*5 == 15 == 1 mod 7
    assertEquals(F.C5, F.QQ(1, 3).modularResidue(seven));
    // -2/5 == -2*3 == 1 mod 7, because 5*3 == 1 mod 7
    assertEquals(F.C1, F.QQ(-2, 5).modularResidue(seven));
    // a composite modulus: 1/3 mod 10 is 7, 1/4 mod 10 doesn't exist
    assertEquals(F.ZZ(7), F.QQ(1, 3).modularResidue(F.ZZ(10)));
    assertNull(F.QQ(1, 4).modularResidue(F.ZZ(10)));
    assertNull(F.QQ(2, 7).modularResidue(seven));
  }

  @Test
  void testIsFreeOfInfinityOrIndeterminate() {
    assertTrue(F.Plus(F.x, F.Sin(F.C2)).isFreeOfInfinityOrIndeterminate());
    assertFalse(F.CInfinity.isFreeOfInfinityOrIndeterminate());
    assertFalse(F.Times(F.C3, F.CComplexInfinity).isFreeOfInfinityOrIndeterminate());
    assertFalse(F.list(F.C1, S.Indeterminate).isFreeOfInfinityOrIndeterminate());
    // a machine NaN counts as indeterminate
    assertFalse(F.Plus(F.x, Num.valueOf(Double.NaN)).isFreeOfInfinityOrIndeterminate());
  }

  @Test
  void testIsHalfIntegerPowerAndIsRationalRadical() {
    assertTrue(F.Sqrt(F.x).isHalfIntegerPower());
    assertTrue(F.Power(F.y, F.QQ(-3, 2)).isHalfIntegerPower());
    assertFalse(F.Power(F.x, F.QQ(1, 3)).isHalfIntegerPower());
    assertFalse(F.Power(F.x, F.C2).isHalfIntegerPower());
    assertTrue(F.Sqrt(F.C2).isRationalRadical());
    assertTrue(F.Power(F.QQ(3, 5), F.QQ(-1, 3)).isRationalRadical());
    assertFalse(F.Sqrt(F.x).isRationalRadical());
    assertFalse(F.Power(F.C2, F.C3).isRationalRadical());
  }

  @Test
  void testPowerExponentOf() {
    assertEquals(F.C1, F.x.powerExponentOf(F.x));
    assertEquals(F.CN3, F.Power(F.x, F.CN3).powerExponentOf(F.x));
    assertEquals(F.Plus(F.a, F.C1), F.Power(F.x, F.Plus(F.a, F.C1)).powerExponentOf(F.x));
    // an exponent depending on x, another base, or a free expression give NIL
    assertTrue(F.Power(F.x, F.x).powerExponentOf(F.x).isNIL());
    assertTrue(F.Power(F.y, F.C2).powerExponentOf(F.x).isNIL());
    assertTrue(F.C5.powerExponentOf(F.x).isNIL());
  }

  @Test
  void testRuleValue() {
    IAST rules = F.list(F.Rule(F.x, F.C1),
        new IExpr[] {F.RuleDelayed(F.y, F.C2), F.Rule(F.y, F.C3), F.Rule(F.x, F.C4)});
    // the first Rule wins, a RuleDelayed isn't looked at
    assertEquals(F.C1, rules.ruleValue(F.x));
    assertEquals(F.C3, rules.ruleValue(F.y));
    assertTrue(rules.ruleValue(F.z).isNIL());
  }

  /**
   * isCircularFunction, isNegativeNumberPower, isEvenIntegerPower, isInactiveFunction,
   * isUserSymbolFunction and isRuleOrNonEmptyListOfRules.
   */
  @Test
  void testTypePredicates() {
    assertTrue(F.Sec(F.x).isCircularFunction());
    assertFalse(F.Sinh(F.x).isCircularFunction());
    assertFalse(F.binaryAST2(S.Sin, F.x, F.y).isCircularFunction());

    assertTrue(F.Power(F.x, F.CN1).isNegativeNumberPower());
    assertTrue(F.Power(F.x, F.num(-0.5)).isNegativeNumberPower());
    assertFalse(F.Power(F.x, F.Negate(F.y)).isNegativeNumberPower());

    assertTrue(F.Power(F.x, F.C4).isEvenIntegerPower());
    assertTrue(F.Power(F.x, F.ZZ(-2)).isEvenIntegerPower());
    assertFalse(F.Power(F.x, F.C3).isEvenIntegerPower());
    assertFalse(F.Power(F.x, F.C1D2).isEvenIntegerPower());

    IExpr inactive = F.binaryAST2(F.Inactive(S.Integrate), F.Sin(F.x), F.x);
    assertTrue(inactive.isInactiveFunction(S.Integrate));
    assertFalse(inactive.isInactiveFunction(S.Sum));
    assertFalse(F.Integrate(F.Sin(F.x), F.x).isInactiveFunction(S.Integrate));

    assertTrue(F.unaryAST1(F.f, F.x).isUserSymbolFunction());
    assertFalse(F.Sin(F.x).isUserSymbolFunction());
    assertFalse(F.f.isUserSymbolFunction());

    assertTrue(F.Rule(F.a, F.C1).isRuleOrNonEmptyListOfRules());
    assertTrue(F.list(F.Rule(F.a, F.C1), F.RuleDelayed(F.b, F.C2)).isRuleOrNonEmptyListOfRules());
    assertFalse(F.CEmptyList.isRuleOrNonEmptyListOfRules());
    assertFalse(F.list(F.Rule(F.a, F.C1), F.C2).isRuleOrNonEmptyListOfRules());
  }

  @Test
  void testVariableExponents() {
    assertTrue(F.Plus(F.C1, F.Power(F.C2, F.x)).hasVariableExponent(F.x));
    assertTrue(F.Sin(F.Power(F.x, F.x)).hasVariableExponent(F.x));
    assertFalse(F.Power(F.x, F.C3).hasVariableExponent(F.x));

    assertTrue(F.Power(F.C2, F.x).isExponentialIn(F.x));
    assertTrue(F.Power(F.E, F.Sqr(F.x)).isExponentialIn(F.x));
    // the base has to be free of x
    assertFalse(F.Power(F.x, F.x).isExponentialIn(F.x));
    assertFalse(F.Power(F.C2, F.y).isExponentialIn(F.x));
  }
}
