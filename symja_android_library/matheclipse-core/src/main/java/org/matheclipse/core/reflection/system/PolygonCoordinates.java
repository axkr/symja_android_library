package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * PolygonCoordinates(poly)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the list of the coordinates of the polygon <code>poly</code>.
 * </p>
 * </blockquote>
 */
public class PolygonCoordinates extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST points = PolygonGeometry.vertices(ast.arg1(), engine);
    if (points.isNIL()) {
      return F.NIL;
    }
    return PolygonGeometry.sortedCoordinates(points);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
