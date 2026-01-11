package org.matheclipse.core.reflection.system;

import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
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
    return F.subst(arg1, normal(heads));
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
