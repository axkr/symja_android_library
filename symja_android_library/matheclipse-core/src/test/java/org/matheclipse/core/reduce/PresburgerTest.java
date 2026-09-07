package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Cooper elimination.
 *
 * <p>
 * Besides the named cases, the last test is a generated oracle: bounded quantified formulas are
 * eliminated and then compared, point by point, against a brute force evaluation of the original.
 * The oracle deliberately does not call the eliminator to compute its expected answer.
 */
public class PresburgerTest {

  @BeforeAll
  public static void awaitSymbolTable() throws InterruptedException {
    F.await();
  }

  private static final IExpr X = S.x;
  private static final IExpr Y = S.y;
  private static final IExpr A = S.a;

  private static Formula eliminate(IExpr expr) {
    Formula lowered = Lowering.lower(expr);
    assertNotNull(lowered, "expected to lower: " + expr);
    return Presburger.eliminateQuantifiers(lowered);
  }

  private static void decides(IExpr expr, Formula expected) {
    assertEquals(expected, eliminate(expr), expr.toString());
  }

  @Test
  public void testEqualityAndStrictnessAreExact() {
    // 2 y == 1 has no integer solution, 2 y == 4 has one
    decides(F.Exists(Y, F.Equal(F.Times(F.C2, Y), F.C1)), Formula.FALSE);
    decides(F.Exists(Y, F.Equal(F.Times(F.C2, Y), F.C4)), Formula.TRUE);
    // no integer lies strictly between 3 and 4, but 3 itself satisfies the closed interval
    decides(F.Exists(Y, F.And(F.Greater(Y, F.C3), F.Less(Y, F.C4))), Formula.FALSE);
    decides(F.Exists(Y, F.And(F.GreaterEqual(Y, F.C3), F.LessEqual(Y, F.C3))), Formula.TRUE);
  }

  @Test
  public void testUnboundedFormulasAreDecidedWithoutSearching() {
    // there is an even integer above any bound, and one below it in every residue class
    decides(F.Exists(Y, F.And(F.Greater(Y, A), F.Equal(F.Mod(Y, F.C2), F.C0))), Formula.TRUE);
    decides(F.Exists(Y, F.And(F.Less(Y, A), F.Equal(F.Mod(Y, F.C3), F.C1))), Formula.TRUE);
  }

  @Test
  public void testInconsistentCongruencesAreFalse() {
    // 4 | y - 1 makes y odd, 6 | y - 2 makes y even
    decides(F.Exists(Y,
        F.And(F.Equal(F.Mod(Y, F.C4), F.C1), F.Equal(F.Mod(Y, F.C6), F.C2))), Formula.FALSE);
    // the same moduli with compatible residues do have a solution
    decides(F.Exists(Y,
        F.And(F.Equal(F.Mod(Y, F.C4), F.C1), F.Equal(F.Mod(Y, F.C6), F.C3))), Formula.TRUE);
  }

  @Test
  public void testUniversalDualityAndAlternation() {
    decides(F.ForAll(Y, F.Or(F.LessEqual(Y, A), F.Greater(Y, A))), Formula.TRUE);
    // every integer has a larger one, but none is larger than all
    decides(F.ForAll(X, F.Exists(Y, F.Greater(Y, X))), Formula.TRUE);
    decides(F.Exists(X, F.ForAll(Y, F.LessEqual(Y, X))), Formula.FALSE);
  }

  @Test
  public void testProjectionOfAnEqualityKeepsTheParityCondition() {
    Formula result = eliminate(F.Exists(Y, F.Equal(X, F.Plus(F.Times(F.C2, Y), F.C1))));
    assertNotNull(result);
    assertFalse(result.containsQuantifier());
    // the bound variable is gone and what remains constrains x to be odd
    assertEquals(Formula.Kind.ATOM, result.kind());
    assertTrue(result.atom().isDivides());
    assertEquals(BigInteger.TWO, result.atom().modulus());
    Variable free = Variable.free(S.x);
    for (int value = -4; value <= 4; value++) {
      Map<Variable, BigInteger> point = new TreeMap<Variable, BigInteger>();
      point.put(free, BigInteger.valueOf(value));
      assertEquals(Math.floorMod(value, 2) == 1, evaluate(result, point), "x = " + value);
    }
  }

  @Test
  public void testEliminationOrderIsIndependentOfTheInputOrder() {
    IExpr body = F.And(F.GreaterEqual(X, A), F.LessEqual(X, S.b),
        F.Equal(F.Mod(X, F.ZZ(11)), F.C0), F.Equal(F.Mod(Y, F.C2), F.C0));
    Formula first = eliminate(F.Exists(F.List(X, Y), body));
    Formula permuted = eliminate(F.Exists(F.List(Y, X), body));
    assertNotNull(first);
    assertEquals(first, permuted);

    // the cheaper variable is the one with the smaller period
    Formula lowered = IntegerNormalizer.normalize(Lowering.lower(body), false);
    Variable boundX = null;
    Variable boundY = null;
    for (Variable variable : lowered.allVariables()) {
      if (variable.symbol() == S.x) {
        boundX = variable;
      } else if (variable.symbol() == S.y) {
        boundY = variable;
      }
    }
    assertNotNull(boundX);
    assertNotNull(boundY);
    TreeSet<Variable> candidates = new TreeSet<Variable>();
    candidates.add(boundX);
    candidates.add(boundY);
    assertEquals(boundY, Presburger.chooseEliminationVariable(lowered, candidates));
    assertTrue(Presburger.eliminationCost(lowered, boundY)
        .compareTo(Presburger.eliminationCost(lowered, boundX)) < 0);
  }

  @Test
  public void testRepeatedSubformulasUseTheMemo() {
    // lowering allocates a fresh binder for every quantifier it reads, so the same source text
    // twice is deliberately two different formulas. Sharing is what a repeated subformula gets,
    // and it is what Cooper's own instances get, so the test shares one lowered subformula.
    Formula repeated = Lowering.lower(F.Exists(Y, F.Equal(F.Mod(Y, F.C2), F.C1)));
    assertNotNull(repeated);
    Formula source = Formula.or(Formula.and(repeated, Lowering.lower(F.Less(A, F.C0))),
        Formula.and(repeated, Lowering.lower(F.GreaterEqual(A, F.C0))));
    Formula normalized = IntegerNormalizer.normalize(source, false);
    assertNotNull(normalized);
    FormulaMemo memo = new FormulaMemo();
    Formula result = Presburger.eliminateRecursive(normalized, memo, Presburger.DEFAULT_BUDGET);
    assertNotNull(result);
    assertFalse(result.containsQuantifier());
    assertTrue(memo.hits() > 0, "the repeated subformula must be eliminated only once");
  }

  @Test
  public void testBudgetDeclinesInsteadOfExploding() {
    // a period of 9699690 residues is far beyond any budget: the engine declines
    IExpr wide = F.Exists(Y, F.And(F.Equal(F.Mod(Y, F.ZZ(9699690)), F.C1), F.Greater(Y, X)));
    Formula lowered = Lowering.lower(wide);
    assertNotNull(lowered);
    assertNull(Presburger.eliminateQuantifiers(lowered));
    // and a small budget declines a formula the default budget decides
    IExpr modest = F.Exists(Y, F.And(F.Equal(F.Mod(Y, F.C10), F.C1), F.Greater(Y, X)));
    assertNotNull(Presburger.eliminateQuantifiers(Lowering.lower(modest)));
    assertNull(Presburger.eliminateQuantifiers(Lowering.lower(modest), 4));
  }

  // ------------------------------------------------------------- the oracle

  /**
   * Brute force truth of a formula under an assignment. A quantifier is evaluated over
   * <code>[-6, 6]</code>, which is sound for the generated formulas because their quantified
   * variable is explicitly bounded to <code>[-3, 3]</code>.
   */
  private static boolean evaluate(Formula formula, Map<Variable, BigInteger> point) {
    switch (formula.kind()) {
      case TRUE:
        return true;
      case FALSE:
        return false;
      case ATOM:
        return evaluateAtom(formula.atom(), point);
      case NOT:
        return !evaluate(formula.children().get(0), point);
      case AND:
        for (Formula child : formula.children()) {
          if (!evaluate(child, point)) {
            return false;
          }
        }
        return true;
      case OR:
        for (Formula child : formula.children()) {
          if (evaluate(child, point)) {
            return true;
          }
        }
        return false;
      default: {
        Variable variable = formula.boundVariables().get(0);
        boolean forAll = formula.kind() == Formula.Kind.FORALL;
        for (int value = -6; value <= 6; value++) {
          point.put(variable, BigInteger.valueOf(value));
          boolean holds = evaluate(formula.body(), point);
          point.remove(variable);
          if (holds != forAll) {
            return !forAll;
          }
        }
        return forAll;
      }
    }
  }

  private static boolean evaluateAtom(Atom atom, Map<Variable, BigInteger> point) {
    BigInteger value = atom.term().constant().numerator().toBigNumerator();
    BigInteger denominator = atom.term().constant().denominator().toBigNumerator();
    for (Map.Entry<Variable, org.matheclipse.core.interfaces.IRational> entry : atom.term()
        .coefficients().entrySet()) {
      BigInteger assigned = point.get(entry.getKey());
      assertNotNull(assigned, "unassigned variable " + entry.getKey());
      value = value.add(entry.getValue().numerator().toBigNumerator().multiply(assigned));
    }
    if (!denominator.equals(BigInteger.ONE)) {
      throw new IllegalStateException("the generated atoms are integral");
    }
    if (atom.isDivides()) {
      boolean divides = IntegerMath.euclideanMod(value, atom.modulus()).signum() == 0;
      return atom.isNegated() ? !divides : divides;
    }
    int sign = value.signum();
    switch (atom.relation()) {
      case EQUAL:
        return sign == 0;
      case NOT_EQUAL:
        return sign != 0;
      case LESS:
        return sign < 0;
      case LESS_EQUAL:
        return sign <= 0;
      case GREATER:
        return sign > 0;
      default:
        return sign >= 0;
    }
  }

  @Test
  public void testGeneratedFormulasAgreeWithExhaustiveEvaluation() {
    final int cases = Integer.getInteger("symja.presburger.cases",
        Config.EXPENSIVE_JUNIT_TESTS ? 25000 : 2000).intValue();
    Random random = new Random(0x5eed3000L);
    Variable free = Variable.free(S.x);
    for (int index = 0; index < cases; index++) {
      Variable bound = Variable.bound(S.y, index);
      int atomCount = 1 + random.nextInt(4);
      List<Formula> atoms = new ArrayList<Formula>(atomCount);
      for (int i = 0; i < atomCount; i++) {
        atoms.add(generateAtom(random, bound, free));
      }
      Formula body = atoms.get(0);
      for (int i = 1; i < atoms.size(); i++) {
        body = random.nextBoolean() ? Formula.and(body, atoms.get(i))
            : Formula.or(body, atoms.get(i));
      }
      boolean universal = random.nextBoolean();
      Formula original = bounded(body, bound, universal).normalized();

      Formula eliminated = Presburger.eliminateQuantifiers(original);
      assertNotNull(eliminated, "elimination declined case " + index + ": " + original);
      assertFalse(eliminated.containsQuantifier(),
          "a quantifier survived case " + index + ": " + eliminated);
      assertFalse(eliminated.containsVariable(bound),
          "the bound variable survived case " + index + ": " + eliminated);

      for (int value = -2; value <= 2; value++) {
        Map<Variable, BigInteger> point = new TreeMap<Variable, BigInteger>();
        point.put(free, BigInteger.valueOf(value));
        boolean before = evaluate(original, point);
        boolean after = evaluate(eliminated, point);
        assertEquals(before, after,
            "case " + index + " at x = " + value + "\n  original:   " + original
                + "\n  eliminated: " + eliminated);
      }
    }
  }

  private static Formula generateAtom(Random random, Variable bound, Variable free) {
    AffineTerm term = AffineTerm.variable(bound).scale(F.ZZ(random.nextInt(9) - 4))
        .add(AffineTerm.variable(free).scale(F.ZZ(random.nextInt(9) - 4)))
        .add(AffineTerm.integer(BigInteger.valueOf(random.nextInt(15) - 7)));
    int kind = random.nextInt(8);
    if (kind < 6) {
      Relation relation = Relation.values()[kind];
      return Formula.atom(Atom.relation(relation, term));
    }
    BigInteger modulus = BigInteger.valueOf(2 + random.nextInt(4));
    return Formula.atom(Atom.divides(modulus, term, kind == 7));
  }

  /** Bound the quantified variable to <code>[-3, 3]</code> so brute force can decide the input. */
  private static Formula bounded(Formula body, Variable variable, boolean universal) {
    AffineTerm lower = AffineTerm.variable(variable).add(AffineTerm.integer(BigInteger.valueOf(3)));
    AffineTerm upper =
        AffineTerm.variable(variable).subtract(AffineTerm.integer(BigInteger.valueOf(3)));
    List<Variable> variables = new ArrayList<Variable>(1);
    variables.add(variable);
    if (universal) {
      return Formula.quantified(Formula.Kind.FORALL, variables,
          Formula.or(Formula.atom(Atom.relation(Relation.LESS, lower)),
              Formula.atom(Atom.relation(Relation.GREATER, upper)), body));
    }
    return Formula.quantified(Formula.Kind.EXISTS, variables,
        Formula.and(Formula.atom(Atom.relation(Relation.GREATER_EQUAL, lower)),
            Formula.atom(Atom.relation(Relation.LESS_EQUAL, upper)), body));
  }
}
