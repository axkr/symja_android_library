package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * TriangleCenter(tri)
 * TriangleCenter(tri, type)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the coordinates of the specified type of center for the triangle <code>tri</code>.
 * </p>
 * </blockquote>
 */
public class TriangleCenter extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST points = TriangleGeometry.vertices(ast.arg1(), engine);
    if (points.isNIL()) {
      return F.NIL;
    }

    if (ast.isAST1()) {
      return TriangleGeometry.center("Centroid", F.NIL, points, 2, engine);
    }

    IExpr arg2 = ast.arg2();
    TriangleGeometry.Spec spec = TriangleGeometry.spec(arg2, points);
    if (spec == null || !TriangleGeometry.isCenterType(spec.type)) {
      // `1` is not a valid `2` specification.
      return Errors.printMessage(S.TriangleCenter, "bspec", F.List(arg2, F.stringx("center")),
          engine);
    }
    if (spec.vertex == TriangleGeometry.INVALID_VERTEX) {
      // `1` is not a valid `2` specification.
      return Errors.printMessage(S.TriangleCenter, "bspec",
          F.List(spec.vertexSpec, F.stringx("vertex")), engine);
    }

    if (spec.vertex == TriangleGeometry.ALL_VERTICES) {
      IASTAppendable result = F.ListAlloc(3);
      for (int i = 1; i <= 3; i++) {
        IExpr center = TriangleGeometry.center(spec.type, spec.center, points, i, engine);
        if (center.isNIL()) {
          return F.NIL;
        }
        result.append(center);
      }
      return result;
    }
    return TriangleGeometry.center(spec.type, spec.center, points, spec.vertex, engine);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
