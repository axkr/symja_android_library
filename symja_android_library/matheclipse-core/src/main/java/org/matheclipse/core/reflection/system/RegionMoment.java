package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.builtin.RegionPrimitives;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * RegionMoment(region, {p1, p2, ..., pn})
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the polynomial moment <code>Integrate(x1^p1 * ... * xn^pn)</code> of the
 * <code>region</code>. The moment is not normalized by the region measure.
 * </p>
 * </blockquote>
 */
public class RegionMoment extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    arg1 = MeshFunctions.normalizeRegion(arg1);
    if (!arg1.isAST()) {
      return F.NIL;
    }
    int[] exponents = toExponents(ast.arg2());
    if (exponents == null) {
      return F.NIL;
    }
    return RegionPrimitives.regionMoment((IAST) arg1, exponents, engine);
  }

  /** A list of non-negative machine integers. */
  static int[] toExponents(IExpr expr) {
    if (!expr.isList() || expr.argSize() == 0) {
      return null;
    }
    IAST list = (IAST) expr;
    int[] exponents = new int[list.argSize()];
    for (int i = 1; i <= list.argSize(); i++) {
      int exponent = list.get(i).toIntDefault();
      if (exponent < 0) {
        return null;
      }
      exponents[i - 1] = exponent;
    }
    return exponents;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
