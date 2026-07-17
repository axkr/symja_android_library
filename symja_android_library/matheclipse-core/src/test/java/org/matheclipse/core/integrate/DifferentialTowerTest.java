package org.matheclipse.core.integrate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Unit tests for {@link DifferentialTower}: the tower's chain-rule derivation operator must agree
 * with the engine's real derivative once the monomial symbols are substituted back.
 */
public class DifferentialTowerTest extends ExprEvaluatorTestCase {

  /**
   * Build the tower of {@code expr}, apply {@link DifferentialTower#derivation} to its tower form,
   * substitute the monomial symbols back, and assert the result equals {@code D(expr, x)}.
   */
  private void assertTowerDerivation(String expr) {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");
    IExpr e = engine.parse(expr);
    DifferentialTower tower = DifferentialTower.build(e, x, engine);
    IExpr d = tower.derivation(tower.towerForm(), engine);
    IExpr back = engine.evaluate(tower.toExpression(d));
    IExpr real = engine.evaluate(F.D(e, x));
    IExpr diff = F.Subtract(back, real);
    // Zero up to symbolic identities (e.g. 1 + Tan^2 = Sec^2 needs TrigToExp) or a numeric check.
    boolean zero = engine.evaluate(F.Together(diff)).isZero()
        || engine.evaluate(F.Simplify(diff)).isZero()
        || engine.evaluate(F.Simplify(F.TrigToExp(diff))).isZero() || numericZero(diff, x, engine);
    assertTrue(zero,
        expr + ": tower derivation " + back + " != D[expr] = " + real + " (diff " + diff + ")");
  }

  private boolean numericZero(IExpr expr, IExpr x, EvalEngine engine) {
    for (double pt : new double[] {0.3, 0.7, 1.1}) {
      IExpr v =
          engine.evaluate(F.Less(F.Abs(F.ReplaceAll(expr, F.Rule(x, F.num(pt)))), F.num(1.0e-8)));
      if (!v.isTrue()) {
        return false;
      }
    }
    return true;
  }

  @Test
  public void primitiveMonomial() {
    assertTowerDerivation("Log(x)^2"); // t = Log(x), D(t) = 1/x
    assertTowerDerivation("x*Log(x) + Log(x)^3");
    assertTowerDerivation("Log(1 + x^2)");
  }

  @Test
  public void hyperexponentialMonomial() {
    assertTowerDerivation("E^(x^2)"); // t = E^(x^2), D(t) = 2*x*t
    assertTowerDerivation("x^2*E^x + E^x");
  }

  @Test
  public void hypertangentMonomial() {
    assertTowerDerivation("Tan(x)^2"); // t = Tan(x), D(t) = 1 + t^2
  }

  @Test
  public void twoMonomials() {
    assertTowerDerivation("Log(x)*E^x"); // t1 = Log(x), t2 = E^x
  }
}
