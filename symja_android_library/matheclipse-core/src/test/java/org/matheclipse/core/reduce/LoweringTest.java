package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Lowering acceptance and rejection matrix. The expressions are built with the {@link F} factories
 * rather than parsed, so that the test does not depend on the parser's symbol table configuration.
 */
public class LoweringTest {

  @BeforeAll
  public static void awaitSymbolTable() throws InterruptedException {
    F.await();
  }

  private static final IExpr X = S.x;
  private static final IExpr Y = S.y;
  private static final IExpr A = S.a;

  private static Formula lower(IExpr expr) {
    return Lowering.lower(expr);
  }

  private static void accepts(IExpr expr) {
    assertNotNull(lower(expr), "expected to lower: " + expr);
  }

  private static void rejects(IExpr expr) {
    assertNull(lower(expr), "expected to be out of grammar: " + expr);
  }

  // ------------------------------------------------------------ acceptance

  @Test
  public void testAffineTermsAreAccepted() {
    accepts(F.Equal(F.Plus(F.Times(F.C2, X), F.Times(F.C3, Y)), F.C1));
    accepts(F.Equal(X, F.QQ(1, 3)));
    accepts(F.Equal(F.Subtract(X, Y), F.C0));
    accepts(F.Less(F.Times(F.CN1, X), F.C5));
    // a power with exponent 1 or 0 is still affine
    accepts(F.Equal(F.Power(X, F.C1), F.C2));
    accepts(F.Equal(F.Power(X, F.C0), F.C1));
  }

  @Test
  public void testAllRelationsAndChains() {
    accepts(F.Equal(X, F.C1));
    accepts(F.Unequal(X, F.C1));
    accepts(F.Less(X, F.C1));
    accepts(F.LessEqual(X, F.C1));
    accepts(F.Greater(X, F.C1));
    accepts(F.GreaterEqual(X, F.C1));
    // 0 <= x <= 3 as a chained relation
    Formula chain = lower(F.LessEqual(F.C0, X, F.C3));
    assertNotNull(chain);
    assertEquals(Formula.Kind.AND, chain.kind());
    assertEquals(2, chain.children().size());
    // Unequal(x, y, z) constrains all three pairs
    Formula pairwise = lower(F.ast(new IExpr[] {X, Y, S.z}, S.Unequal));
    assertNotNull(pairwise);
    assertEquals(3, pairwise.children().size());
  }

  @Test
  public void testInequalityChain() {
    IExpr inequality = F.ast(new IExpr[] {F.C0, S.LessEqual, X, S.Less, F.C3}, S.Inequality);
    Formula formula = lower(inequality);
    assertNotNull(formula);
    assertEquals(Formula.Kind.AND, formula.kind());
  }

  @Test
  public void testBooleanConnectives() {
    accepts(F.And(F.Less(X, F.C1), F.Greater(X, F.CN1)));
    accepts(F.Or(F.Less(X, F.C0), F.Greater(X, F.C1)));
    accepts(F.Not(F.Less(X, F.C1)));
    accepts(F.ast(new IExpr[] {F.Less(X, F.C0), F.Greater(X, F.C1)}, S.Xor));
    accepts(F.ast(new IExpr[] {F.Less(X, F.C0), F.Greater(X, F.C1)}, S.Nand));
    accepts(F.ast(new IExpr[] {F.Less(X, F.C0), F.Greater(X, F.C1)}, S.Nor));
    accepts(F.Implies(F.Less(X, F.C0), F.Less(X, F.C1)));
    accepts(F.Equivalent(F.Less(X, F.C0), F.Less(X, F.C1)));
    // a list of relations is the system of all of them
    accepts(F.List(F.Equal(X, F.C1), F.Greater(Y, F.C0)));
  }

  @Test
  public void testNotOfAnAtomBecomesTheNegatedAtom() {
    Formula formula = lower(F.Not(F.Less(X, F.C1)));
    assertNotNull(formula);
    assertEquals(Formula.Kind.ATOM, formula.kind());
    assertEquals(Relation.GREATER_EQUAL, formula.atom().relation());
  }

  @Test
  public void testCongruenceSurfaceForms() {
    // Mod(x, 3) == 1 is the divisibility 3 | (x - 1)
    Formula formula = lower(F.Equal(F.Mod(X, F.C3), F.C1));
    assertNotNull(formula);
    assertEquals(Formula.Kind.ATOM, formula.kind());
    assertTrue(formula.atom().isDivides());
    assertEquals(BigInteger.valueOf(3), formula.atom().modulus());
    assertFalse(formula.atom().isNegated());
    // the residue may be written on either side
    assertNotNull(lower(F.Equal(F.C1, F.Mod(X, F.C3))));
    // and the negation is the negated atom
    Formula negated = lower(F.Unequal(F.Mod(X, F.C3), F.C1));
    assertNotNull(negated);
    assertTrue(negated.atom().isNegated());
    // Divisible is the same atom with residue zero
    Formula divisible = lower(F.Divisible(X, F.C3));
    assertNotNull(divisible);
    assertTrue(divisible.atom().isDivides());
  }

  @Test
  public void testUnreachableResidueDecidesTheAtom() {
    // Mod(x, 6) is always in [0, 6), so it never equals 7
    assertEquals(Formula.FALSE, lower(F.Equal(F.Mod(X, F.C6), F.C7)));
    assertEquals(Formula.TRUE, lower(F.Unequal(F.Mod(X, F.C6), F.C7)));
    assertEquals(Formula.FALSE, lower(F.Equal(F.Mod(X, F.C6), F.CN1)));
    // a non integer residue is unreachable too
    assertEquals(Formula.FALSE, lower(F.Equal(F.Mod(X, F.C6), F.QQ(1, 2))));
  }

  @Test
  public void testQuantifiers() {
    Formula formula = lower(F.Exists(Y, F.Equal(X, F.Plus(F.Times(F.C2, Y), F.C1))));
    assertNotNull(formula);
    assertEquals(Formula.Kind.EXISTS, formula.kind());
    assertEquals(1, formula.boundVariables().size());
    assertFalse(formula.boundVariables().get(0).isFree());
    // the free variable of the body stays free
    assertEquals(1, formula.freeVariables().size());
    assertEquals(Variable.free(S.x), formula.freeVariables().iterator().next());

    accepts(F.ForAll(F.List(X, Y), F.GreaterEqual(F.Plus(X, Y), F.C0)));
    // the three argument forms carry their condition
    accepts(F.Exists(Y, F.Greater(Y, F.C0), F.Equal(X, Y)));
    accepts(F.ForAll(Y, F.Greater(Y, F.C0), F.Greater(F.Plus(X, Y), F.C0)));
  }

  @Test
  public void testNestedBindersCannotCapture() {
    // Exists(x, Exists(x, ...)) : the two x are different variables
    Formula formula =
        lower(F.Exists(X, F.And(F.Greater(X, F.C0), F.Exists(X, F.Less(X, F.C0)))));
    assertNotNull(formula);
    assertTrue(formula.freeVariables().isEmpty());
    assertEquals(2, formula.allVariables().size());
  }

  @Test
  public void testElementOfAnIntegerSuperDomainIsATautology() {
    assertEquals(Formula.TRUE, lower(F.Element(X, S.Integers)));
    assertEquals(Formula.TRUE, lower(F.Element(X, S.Reals)));
    assertEquals(Formula.TRUE, lower(F.Element(X, S.Rationals)));
    // Primes is not expressible as a linear condition
    rejects(F.Element(X, S.Primes));
  }

  @Test
  public void testParametersStayFree() {
    Formula formula = lower(F.Greater(X, A));
    assertNotNull(formula);
    assertEquals(2, formula.freeVariables().size());
  }

  // ------------------------------------------------------------- rejection

  @Test
  public void testNonLinearIsRejected() {
    rejects(F.Equal(F.Times(X, Y), F.C1));
    rejects(F.Less(F.Power(X, F.C2), F.C3));
    rejects(F.Equal(F.Divide(X, Y), F.C1));
    rejects(F.Equal(F.Power(X, F.CN1), F.C1));
  }

  @Test
  public void testInexactAndTranscendentalAreRejected() {
    rejects(F.Less(X, F.num(1.5)));
    rejects(F.Less(F.Sin(X), F.C1));
    rejects(F.Greater(X, S.Pi));
  }

  @Test
  public void testMalformedCongruencesAreRejected() {
    // a non positive modulus has a different sign convention
    rejects(F.Equal(F.Mod(X, F.CN3), F.C1));
    rejects(F.Equal(F.Mod(X, F.C0), F.C0));
    // Mod outside an equality is out of the grammar
    rejects(F.Less(F.Mod(X, F.C3), F.C2));
    // a non integral dividend cannot be a divisibility atom
    rejects(F.Divisible(F.Divide(X, F.C2), F.C3));
    rejects(F.Divisible(X, F.C0));
  }

  @Test
  public void testMalformedQuantifiersAreRejected() {
    rejects(F.Exists(F.List(X, X), F.Equal(X, F.C1)));
    rejects(F.Exists(F.C2, F.Equal(X, F.C1)));
  }

  @Test
  public void testRequestValidatesTheVariableSpecification() {
    assertNotNull(Lowering.request(F.Equal(X, F.C1), F.List(X), IntegerDomain.INTEGERS));
    // a duplicate target, a non symbol target and an empty list are all rejected
    assertNull(Lowering.request(F.Equal(X, F.C1), F.List(X, X), IntegerDomain.INTEGERS));
    assertNull(Lowering.request(F.Equal(X, F.C1), F.List(F.C2), IntegerDomain.INTEGERS));
    assertNull(Lowering.request(F.Equal(X, F.C1), F.List(), IntegerDomain.INTEGERS));
    assertNull(Lowering.request(F.Equal(X, F.C1), F.List(X), null));
  }
}
