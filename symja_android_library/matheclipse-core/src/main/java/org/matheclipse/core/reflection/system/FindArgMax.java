package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * <code>FindArgMax(f, {x, xstart})
 * </code>
 * </pre>
 *
 * <p>
 * gives the position <code>{x0}</code> of a local numerical maximum of <code>f</code>, searched for
 * from the start value <code>xstart</code>.
 * </p>
 *
 * <pre>
 * <code>FindArgMax(f, {{x, xstart},{y, ystart},...})
 * </code>
 * </pre>
 *
 * <p>
 * gives the position <code>{x0, y0, ...}</code> of a local numerical maximum of the multivariate
 * function <code>f</code>.
 * </p>
 *
 * <p>
 * The arguments, constraints and options are the ones of {@link FindMaximum}; the result is the
 * list of the values of the rules which {@link FindMaximum} gives.
 * </p>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; FindArgMax(4*x - x^2, {x, 0})
 * {2.0}
 * </code>
 * </pre>
 */
public class FindArgMax extends FindMaximum {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    return argumentPart(super.evaluate(ast, argSize, options, engine, originalAST));
  }
}
