package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * ShortestCurveDistance(region, p1, p2)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the shortest curve distance between p1 and p2 within the region.
 * </p>
 * </blockquote>
 */
public class ShortestCurveDistance extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr p1 = ast.arg2();
    IExpr p2 = ast.arg3();

    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }

    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();

      // Strict membership validation: Only return NIL if it explicitly evaluates to False
      IExpr mem1 = engine.evaluate(F.RegionMember(reg, p1));
      IExpr mem2 = engine.evaluate(F.RegionMember(reg, p2));
      if (mem1.isFalse() || mem2.isFalse()) {
        return F.NIL;
      }

      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Circle:
          case ID.Sphere:
            return circleSphereDistance(reg, p1, p2, engine);
          case ID.Disk:
          case ID.Ball:
          case ID.Rectangle:
          case ID.Cuboid:
          case ID.Triangle:
          case ID.Polygon:
          case ID.Simplex:
          case ID.Tetrahedron:
            return euclideanDistance(p1, p2, engine);
          case ID.Line:
            return lineDistance(reg, p1, p2, engine);
        }
      }
    }
    return F.NIL;
  }

  private IExpr circleSphereDistance(IAST reg, IExpr p1, IExpr p2, EvalEngine engine) {
    IExpr head = reg.head();
    int dim = (head == S.Circle) ? 2 : 3;
    IExpr c = F.NIL;
    IExpr r = F.C1;

    if (reg.argSize() == 0) {
      c = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
    } else if (reg.argSize() >= 1) {
      c = reg.arg1();
      if (reg.argSize() >= 2) {
        r = reg.arg2();
      }
    }

    if (c.isList()) {
      IExpr v1 = engine.evaluate(F.Subtract(p1, c));
      IExpr v2 = engine.evaluate(F.Subtract(p2, c));

      // Great Circle Geodesic Formula: r * ArcCos[ (v1 . v2) / r^2 ]
      IExpr dot = engine.evaluate(F.Dot(v1, v2));
      IExpr cosAngle = engine.evaluate(F.Divide(dot, F.Sqr(r)));
      return engine.evaluate(F.Times(r, F.ArcCos(cosAngle)));
    }

    return F.NIL;
  }

  private IExpr euclideanDistance(IExpr p1, IExpr p2, EvalEngine engine) {
    // Safe Euclidean norm without injecting Abs() layers into exact symbolic coordinates
    return engine.evaluate(F.Sqrt(F.Total(F.Sqr(F.Subtract(p1, p2)))));
  }

  private IExpr lineDistance(IAST reg, IExpr p1, IExpr p2, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() >= 2) {

        // Map out all valid point intersections along the polyline path
        List<IExpr> s1List = getParameters(pts, p1, engine);
        List<IExpr> s2List = getParameters(pts, p2, engine);

        if (s1List.isEmpty() || s2List.isEmpty()) {
          return F.NIL; // Point definitively not on line segments
        }

        IExpr minDist = F.NIL;
        for (IExpr s1 : s1List) {
          for (IExpr s2 : s2List) {
            IExpr dist = engine.evaluate(F.Abs(F.Subtract(s1, s2)));
            if (minDist.isNIL()) {
              minDist = dist;
            } else {
              minDist = engine.evaluate(F.Min(minDist, dist));
            }
          }
        }

        // Path-Wrap: Check if the polyline is a closed loop and take the shorter way around
        IExpr firstPt = pts.arg1();
        IExpr lastPt = pts.last();
        IExpr isClosed = engine.evaluate(F.PossibleZeroQ(F.Subtract(firstPt, lastPt)));

        if (isClosed.isTrue()) {
          IExpr totalLength = F.C0;
          for (int i = 1; i < pts.argSize(); i++) {
            IExpr segmentLen = engine.evaluate(F.Norm(F.Subtract(pts.get(i), pts.get(i + 1))));
            totalLength = engine.evaluate(F.Plus(totalLength, segmentLen));
          }
          for (IExpr s1 : s1List) {
            for (IExpr s2 : s2List) {
              IExpr dist = engine.evaluate(F.Abs(F.Subtract(s1, s2)));
              IExpr wrappedDist = engine.evaluate(F.Abs(F.Subtract(totalLength, dist)));
              minDist = engine.evaluate(F.Min(minDist, wrappedDist));
            }
          }
        }

        // Force MachinePrecision conversion for mesh curve distances to mirror WL semantics
        if (minDist.isPresent()) {
          return engine.evaluate(F.N(minDist));
        }
        return minDist;
      }
    }
    return F.NIL;
  }

  /**
   * Finds the accumulative arc-length parameter 's' corresponding to a point 'p' along the
   * polyline.
   */
  private List<IExpr> getParameters(IAST pts, IExpr p, EvalEngine engine) {
    List<IExpr> params = new ArrayList<>();
    IExpr currentLength = F.C0;

    for (int i = 1; i < pts.argSize(); i++) {
      IExpr a = pts.get(i);
      IExpr b = pts.get(i + 1);

      // A point exactly resides on the line segment if: Norm(p - a) + Norm(p - b) == Norm(a -
      // b)
      IExpr distA = engine.evaluate(F.Norm(F.Subtract(p, a)));
      IExpr distB = engine.evaluate(F.Norm(F.Subtract(p, b)));
      IExpr distAB = engine.evaluate(F.Norm(F.Subtract(a, b)));

      IExpr diff = engine.evaluate(F.Simplify(F.Subtract(F.Plus(distA, distB), distAB)));

      if (diff.isZero() || engine.evaluate(F.PossibleZeroQ(diff)).isTrue()) {
        params.add(engine.evaluate(F.Plus(currentLength, distA)));
      }

      currentLength = engine.evaluate(F.Plus(currentLength, distAB));
    }
    return params;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }
}
