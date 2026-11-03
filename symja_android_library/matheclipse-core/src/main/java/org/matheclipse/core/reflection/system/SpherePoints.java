package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * SpherePoints(n)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives <code>n</code> approximately uniformly distributed points on the unit sphere.
 * </p>
 * </blockquote>
 *
 * <p>
 * The points lie on the golden spiral: the <code>k</code>-th point has the height
 * <code>z = (2*k - n - 1) / (n + 2)</code> and the longitude <code>k</code> times the golden angle.
 * The counts which have a known optimal arrangement (<code>2..12, 14, 16, ..., 60</code>) stay
 * unevaluated.
 * </p>
 */
public class SpherePoints extends AbstractFunctionEvaluator {
  /** The largest number of points which is generated. */
  private static final int MAX_POINTS = 1_000_000;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    final int n = ast.arg1().toIntDefault();
    if (n < 1 || n > MAX_POINTS || hasOptimalArrangement(n)) {
      return F.NIL;
    }
    if (n == 1) {
      return F.list(F.list(F.CD1, F.CD0, F.CD0));
    }
    final double golden = (1.0 + Math.sqrt(5.0)) / 2.0;
    final double angle = 2.0 * Math.PI * (1.0 - 1.0 / golden);
    IASTAppendable result = F.ListAlloc(n);
    for (int k = 1; k <= n; k++) {
      if ((k & 0xFFF) == 0) {
        engine.checkTimeLimit();
      }
      // the exact rational height, rounded once
      final double z = (double) (2L * k - n - 1) / (double) (n + 2L);
      final double r = Math.sqrt(1.0 - z * z);
      final double t = k * angle;
      result.append(F.list(F.num(r * Math.cos(t)), F.num(r * Math.sin(t)), F.num(z)));
    }
    return result;
  }

  private static boolean hasOptimalArrangement(int n) {
    if (n >= 2 && n <= 12) {
      return true;
    }
    switch (n) {
      case 14:
      case 16:
      case 18:
      case 20:
      case 22:
      case 24:
      case 30:
      case 32:
      case 40:
      case 55:
      case 60:
        return true;
      default:
        return false;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
