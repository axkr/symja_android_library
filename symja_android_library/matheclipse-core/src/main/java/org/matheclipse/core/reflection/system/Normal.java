package org.matheclipse.core.reflection.system;

import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre>
 * Normal(series)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * converts a <code>series</code> expression into a standard expression.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))
 * 1/x-x-4*x^2-17*x^3-88*x^4-549*x^5
 * </pre>
 */
public final class Normal extends AbstractFunctionEvaluator {
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST heads = F.CEmptyList;
    if (ast.isAST2()) {
      heads = ast.arg2().makeList();
    }
    final IExpr arg1 = ast.arg1();
    if (arg1.isAST(S.RootSum, 3) && (heads.isAST0() || heads.exists(y -> y.equals(S.RootSum)))) {
      // RootSum(f, form) stays inert during automatic evaluation whenever the summand doesn't
      // reduce to a rational function; Normal(...) is the explicit request to sum over the roots.
      IExpr expanded = RootSum.expandOverRoots((IAST) arg1, engine);
      if (expanded.isPresent()) {
        return expanded;
      }
    }
    if (arg1.isAST(S.QuantityArray, 3)
        && (heads.isAST0() || heads.exists(y -> y.equals(S.QuantityArray)))) {
      IExpr expanded =
          org.matheclipse.core.builtin.QuantityFunctions.quantityArrayNormal((IAST) arg1);
      if (expanded.isPresent()) {
        return expanded;
      }
    }
    IExpr normal = F.NIL;
    if (heads.isAST0()) {
      normal = arg1.normal(true);
    } else {
      if (heads.exists(y -> y.equals(arg1.head()))) {
        normal = arg1.normal(true);
      }
    }
    if (normal.isPresent()) {
      return normal;
    }
    return arg1;
  }

  private Function<IExpr, IExpr> normal(final IAST heads) {
    return x -> {
      final int size = heads.size();
      if (size == 1) {
        return x.normal(true);
      }
      final IExpr head = x.head();
      if (heads.exists(y -> y.equals(head))) {
        return x.normal(true);
      }
      return F.NIL;
    };
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
