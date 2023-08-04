package org.matheclipse.core.reflection.system;

import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>FindMaximum(f, {x, xstart})
 * </code>
 * </pre>
 *
 * <p>
 * searches for a local numerical maximum of <code>f</code> for the variable <code>x</code> and the
 * start value <code>xstart</code>.
 * </p>
 *
 * <pre>
 * <code>FindMaximum(f, {x, xstart}, Method-&gt;method_name)
 * </code>
 * </pre>
 *
 * <p>
 * searches for a local numerical maximum of <code>f</code> for the variable <code>x</code> and the
 * start value <code>xstart</code>, with one of the following method names:
 * </p>
 *
 * <pre>
 * <code>FindMaximum(f, {{x, xstart},{y, ystart},...})
 * </code>
 * </pre>
 *
 * <p>
 * searches for a local numerical maximum of the multivariate function <code>f</code> for the
 * variables <code>x, y,...</code> and the corresponding start values
 * <code>xstart, ystart,...</code>.
 * </p>
 *
 * <p>
 * See
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Mathematical_optimization">Wikipedia - Mathematical
 * optimization</a></li>
 * </ul>
 * <h4>Powell</h4>
 * <p>
 * Implements the Powell optimizer.
 * </p>
 * <p>
 * This is the default method, if no <code>method_name</code> is given.
 * </p>
 * <h4>ConjugateGradient</h4>
 * <p>
 * Implements the ConjugateGradient optimizer.<br />
 * This is a derivative based method and the functions must be symbolically differentiatable.
 * </p>
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; FindMaximum(Sin(x), {x, 0.5})
 * {1.0,{x-&gt;1.5708}}
 * </code>
 * </pre>
 */
public class FindMaximum extends FindMinimum {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    GoalType goalType = GoalType.MAXIMIZE;
    try {
      return findExtremum(ast, engine, goalType);
    } catch (MathIllegalStateException miae) {
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(miae.getMessage())),
          engine);
    } catch (MathRuntimeException mre) {
      Errors.printMessage(ast.topHead(), "error", F.list(F.$str(mre.getMessage())), engine);
      return F.CEmptyList;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
