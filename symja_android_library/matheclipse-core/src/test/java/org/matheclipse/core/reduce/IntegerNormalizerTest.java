package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** Integer normal form: strictness, denominators and the greatest common divisor. */
public class IntegerNormalizerTest {

  @BeforeAll
  public static void awaitSymbolTable() throws InterruptedException {
    F.await();
  }

  private static final IExpr X = S.x;

  private static Formula normalize(IExpr expr, boolean keepEqualities) {
    Formula lowered = Lowering.lower(expr);
    assertNotNull(lowered, "expected to lower: " + expr);
    return IntegerNormalizer.normalize(lowered, keepEqualities);
  }

  private static BigInteger constantOf(Formula formula) {
    return formula.atom().term().constant().numerator().toBigNumerator();
  }

  private static BigInteger coefficientOf(Formula formula) {
    return formula.atom().term().coefficient(Variable.free(S.x)).numerator().toBigNumerator();
  }

  @Test
  public void testStrictInequalityShiftsByOne() {
    // x < 3 is x <= 2, written x - 2 <= 0
    Formula formula = normalize(F.Less(X, F.C3), false);
    assertEquals(Relation.LESS_EQUAL, formula.atom().relation());
    assertEquals(BigInteger.ONE, coefficientOf(formula));
    assertEquals(BigInteger.valueOf(-2), constantOf(formula));
    // x > 3 is -x + 4 <= 0
    Formula greater = normalize(F.Greater(X, F.C3), false);
    assertEquals(BigInteger.valueOf(-1), coefficientOf(greater));
    assertEquals(BigInteger.valueOf(4), constantOf(greater));
  }

  @Test
  public void testCoefficientGcdTightensTheBound() {
    // 2 x <= 5 is x <= 2 over the integers
    Formula formula = normalize(F.LessEqual(F.Times(F.C2, X), F.C5), false);
    assertEquals(BigInteger.ONE, coefficientOf(formula));
    assertEquals(BigInteger.valueOf(-2), constantOf(formula));
    // 2 x >= 5 is x >= 3
    Formula greater = normalize(F.GreaterEqual(F.Times(F.C2, X), F.C5), false);
    assertEquals(BigInteger.valueOf(-1), coefficientOf(greater));
    assertEquals(BigInteger.valueOf(3), constantOf(greater));
  }

  @Test
  public void testRationalCoefficientsAreCleared() {
    // x/2 <= 3/4 clears to 2x - 3 <= 0 and then tightens by gcd 2 to x <= 1
    Formula formula = normalize(F.LessEqual(F.Divide(X, F.C2), F.QQ(3, 4)), false);
    assertTrue(formula.atom().term().isIntegral());
    assertEquals(BigInteger.ONE, coefficientOf(formula));
    assertEquals(BigInteger.valueOf(-1), constantOf(formula));
  }

  @Test
  public void testUnsatisfiableEqualityIsFalse() {
    // 2 x == 3 has no integer solution
    assertEquals(Formula.FALSE, normalize(F.Equal(F.Times(F.C2, X), F.C3), true));
    assertEquals(Formula.FALSE, normalize(F.Equal(F.Times(F.C2, X), F.C3), false));
    // 2 x == 4 keeps its equality and divides through
    Formula formula = normalize(F.Equal(F.Times(F.C2, X), F.C4), true);
    assertEquals(Relation.EQUAL, formula.atom().relation());
    assertEquals(BigInteger.ONE, coefficientOf(formula));
    assertEquals(BigInteger.valueOf(-2), constantOf(formula));
  }

  @Test
  public void testEqualitySplitsWhenEqualitiesAreNotKept() {
    Formula formula = normalize(F.Equal(X, F.C2), false);
    assertEquals(Formula.Kind.AND, formula.kind());
    assertEquals(2, formula.children().size());
  }

  @Test
  public void testCongruenceIsReducedToItsPrimitiveResidue() {
    // Mod(6 x + 4, 10) == 0 is x == 1 (mod 5)
    Formula formula = normalize(F.Equal(F.Mod(F.Plus(F.Times(F.C6, X), F.C4), F.C10), F.C0), false);
    assertTrue(formula.atom().isDivides());
    assertEquals(BigInteger.valueOf(5), formula.atom().modulus());
    assertEquals(BigInteger.ONE, coefficientOf(formula));
    assertEquals(BigInteger.valueOf(4), constantOf(formula));
  }

  @Test
  public void testInconsistentCongruenceIsDecided() {
    // Mod(2 x + 1, 4) == 0 is unsatisfiable: an odd number is never divisible by 4
    assertEquals(Formula.FALSE,
        normalize(F.Equal(F.Mod(F.Plus(F.Times(F.C2, X), F.C1), F.C4), F.C0), false));
    // and its negation is a tautology
    assertEquals(Formula.TRUE,
        normalize(F.Unequal(F.Mod(F.Plus(F.Times(F.C2, X), F.C1), F.C4), F.C0), false));
  }

  @Test
  public void testModulusOneIsATautology() {
    assertEquals(Formula.TRUE, normalize(F.Divisible(X, F.C1), false));
  }
}
