package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Fourier-Motzkin elimination over the reals.
 *
 * <p>
 * The last test is a generated oracle: random linear formulas are reduced and the result is
 * compared, point by point, with the original formula on a grid of rational points. The oracle
 * doesn't call the eliminator to compute its expected answer.
 */
public class RationalQETest {

  @BeforeAll
  public static void awaitSymbolTable() throws InterruptedException {
    F.await();
  }

  private static final ISymbol[] VARIABLES = {S.x, S.y, S.z, S.a};

  private static final IExpr[] POINTS = {F.CN2, F.QQ(-3, 2), F.CN1, F.CN1D2, F.C0, F.C1D3, F.C1D2,
      F.C1, F.QQ(3, 2), F.C2, F.QQ(7, 3)};

  private static IExpr reduce(IExpr condition, ISymbol... targets) {
    Formula lowered = Lowering.lower(condition);
    assertNotNull(lowered, "expected to lower: " + condition);
    List<Variable> variables = new ArrayList<Variable>();
    for (ISymbol target : targets) {
      variables.add(Variable.free(target));
    }
    return RationalQE.reduce(lowered, variables, false);
  }

  @Test
  public void testTriangle() {
    IExpr reduced = reduce(F.And(F.Less(F.Plus(S.x, S.y), F.C1), F.Greater(S.x, F.C0),
        F.Greater(S.y, F.C0)), S.x, S.y);
    assertEquals("x>0&&x<1&&y>0&&y<1-x", EvalEngine.get().evaluate(reduced).toString());
  }

  @Test
  public void testInfeasible() {
    IExpr reduced = reduce(F.And(F.Greater(S.x, F.C1), F.Less(S.x, F.C0), F.Greater(S.y, F.C0)),
        S.x, S.y);
    assertEquals(S.False, reduced);
  }

  @Test
  public void testExistsProjects() {
    // Exists(y, x < y < 1) is x < 1
    IExpr reduced =
        reduce(F.Exists(S.y, F.And(F.Less(S.x, S.y), F.Less(S.y, F.C1))), S.x);
    assertEquals("x<1", EvalEngine.get().evaluate(reduced).toString());
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "fixed seed for a reproducible fuzz loop")
  public void testGeneratedFormulasAgreePointwise() {
    Random random = new Random(20260922L);
    EvalEngine engine = EvalEngine.get();
    int decided = 0;
    for (int n = 0; n < 150; n++) {
      int variableCount = 2 + random.nextInt(3);
      IExpr formula = randomFormula(random, variableCount);
      ISymbol[] targets = variableCount == 2 ? new ISymbol[] {S.x, S.y}
          : new ISymbol[] {S.x, S.y, S.z};
      IExpr reduced = reduce(formula, targets);
      if (reduced.isNIL()) {
        continue;
      }
      decided++;
      IExpr result = engine.evaluate(reduced);
      for (int p = 0; p < 40; p++) {
        IASTAppendable rules = F.ListAlloc(VARIABLES.length);
        for (ISymbol variable : VARIABLES) {
          rules.append(F.Rule(variable, POINTS[random.nextInt(POINTS.length)]));
        }
        IExpr expected = engine.evaluate(F.ReplaceAll(formula, rules));
        IExpr actual = engine.evaluate(F.ReplaceAll(result, rules));
        assertEquals(expected, actual,
            () -> formula + " reduced to " + result + " disagrees at " + rules);
      }
    }
    assertTrue(decided > 120, "only " + decided + " formulas were decided");
  }

  private static IExpr randomFormula(Random random, int variableCount) {
    int atoms = 1 + random.nextInt(4);
    IExpr formula = randomAtom(random, variableCount);
    for (int i = 1; i < atoms; i++) {
      IExpr atom = randomAtom(random, variableCount);
      formula = random.nextInt(4) == 0 ? F.Or(formula, atom) : F.And(formula, atom);
    }
    return formula;
  }

  private static IExpr randomAtom(Random random, int variableCount) {
    IASTAppendable sum = F.PlusAlloc(variableCount + 1);
    for (int i = 0; i < variableCount; i++) {
      sum.append(F.Times(F.ZZ(random.nextInt(7) - 3), VARIABLES[i]));
    }
    sum.append(F.ZZ(random.nextInt(9) - 4));
    IExpr term = EvalEngine.get().evaluate(sum);
    IAST[] relations = {F.Less(term, F.C0), F.LessEqual(term, F.C0), F.Greater(term, F.C0),
        F.GreaterEqual(term, F.C0), F.Equal(term, F.C0), F.Unequal(term, F.C0)};
    return relations[random.nextInt(random.nextInt(4) == 0 ? 6 : 4)];
  }
}
