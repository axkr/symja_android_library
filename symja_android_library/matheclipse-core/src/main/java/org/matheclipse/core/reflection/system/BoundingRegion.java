package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * BoundingRegion[{pt1, pt2, ...}]
 * </p>
 * <p>
 * Gives the smallest axis-aligned bounding box of a point list. Evaluates to Rectangle in 2D, or
 * Cuboid in 1D and >= 3D.
 * </p>
 */
public class BoundingRegion extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        IAST points = (IAST) arg1;
        if (points.isEmptyList()) {
          // Structurally-invalid input: an empty list
          // engine.printMessage("BoundingRegion", "regl", F.List(arg1));
          return F.NIL;
        }

        // Get the dimension from the first point
        IExpr firstPoint = points.arg1();
        if (!firstPoint.isList()) {
          return F.NIL;
        }

        IAST firstList = (IAST) firstPoint;
        int dimension = firstList.argSize();
        if (dimension == 0) {
          return F.NIL;
        }

        // Check that all points are lists of the exact same dimension
        for (int i = 1; i <= points.argSize(); i++) {
          IExpr point = points.get(i);
          if (!point.isList()) {
            return F.NIL;
          }
          if (((IAST) point).argSize() != dimension) {
            return F.NIL;
          }
        }

        IASTAppendable mins = F.ListAlloc(dimension);
        IASTAppendable maxs = F.ListAlloc(dimension);

        for (int d = 1; d <= dimension; d++) {
          // For each coordinate d, compute Min and Max over all points
          IASTAppendable minAST = F.ast(S.Min);
          IASTAppendable maxAST = F.ast(S.Max);
          for (int i = 1; i <= points.argSize(); i++) {
            IAST pt = (IAST) points.get(i);
            minAST.append(pt.get(d));
            maxAST.append(pt.get(d));
          }

          // Evaluate Min and Max to simplify exact values or symbolic coords
          mins.append(engine.evaluate(minAST));
          maxs.append(engine.evaluate(maxAST));
        }

        // 2D points give a Rectangle; 1D and >=3D give a Cuboid
        if (dimension == 2) {
          IASTAppendable rect = F.ast(S.Rectangle);
          rect.append(mins);
          rect.append(maxs);
          return rect;
        } else {
          IASTAppendable cuboid = F.ast(S.Cuboid);
          cuboid.append(mins);
          cuboid.append(maxs);
          return cuboid;
        }
      }
    }

    // Two-argument forms (e.g., named-methods "MinDisk") or invalid arguments stay unevaluated
    return F.NIL;
  }
}