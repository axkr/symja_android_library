package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.RegionPrimitives;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class RegionNearest extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      return engine.evaluate(F.RegionNearestFunction(ast.arg1()));
    }
    if (ast.argSize() != 2) {
      return F.NIL;
    }

    IExpr arg1 = ast.arg1();
    IExpr p = ast.arg2();

    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    if (arg1.isAST(S.ImplicitRegion, 3)) {
      return implicitNearest((IAST) arg1, p, engine);
    }
    arg1 = MeshFunctions.normalizeRegion(arg1);

    if (arg1.isAST() && p.isList()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();

      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
            return pointNearest(reg, p, engine);
          case ID.Line:
            return lineNearest(reg, p, engine);
          case ID.Circle:
          case ID.Sphere:
            return ballNearest(reg, p, engine, true);
          case ID.Disk:
          case ID.Ball:
            return ballNearest(reg, p, engine, false);
          case ID.Rectangle:
          case ID.Cuboid:
            return boxNearest(reg, p, engine);
          case ID.Triangle:
          case ID.Polygon:
            return polygonNearest(reg, p, engine);
        }
      }
    }
    return F.NIL;
  }

  /**
   * The point of <code>ImplicitRegion(cond, {x1, x2, ...})</code> nearest to a point of machine
   * numbers: the point itself if it meets the condition, otherwise the numeric minimum of the
   * squared distance under the condition.
   *
   * @return <code>F.NIL</code> if the point is not one of machine numbers or the minimum isn't found
   */
  static IExpr implicitNearest(IAST region, IExpr p, EvalEngine engine) {
    IExpr variables = region.arg2();
    if (!variables.isList() || !p.isList() || p.argSize() != variables.argSize()
        || !((IAST) p).forAll(x -> x.isReal()) || !((IAST) p).exists(x -> x.isInexactNumber())
        || !((IAST) variables).forAll(x -> x.isVariable())) {
      return F.NIL;
    }
    IExpr condition = region.arg1();
    IASTAppendable rules = F.ListAlloc(p.argSize());
    for (int i = 1; i <= p.argSize(); i++) {
      rules.append(F.Rule(variables.getAt(i), p.getAt(i)));
    }
    if (engine.evalTrue(F.subst(condition, rules))) {
      return p;
    }
    IExpr distance = F.Total(F.Sqr(F.Subtract(variables, p)));
    IExpr minimum = engine.evalQuiet(F.NMinimize(F.list(distance, condition), variables));
    if (!minimum.isList2() || !minimum.second().isListOfRules()) {
      return F.NIL;
    }
    IExpr nearest = engine.evaluate(F.subst(variables, (IAST) minimum.second()));
    if (!nearest.isList() || !((IAST) nearest).forAll(x -> x.isReal())) {
      return F.NIL;
    }
    return polishOnBoundary(condition, (IAST) variables, p, (IAST) nearest, engine);
  }

  /**
   * The nearest point found by the numeric search, refined by Newton's method on the conditions of
   * a minimum on the boundary <code>g == 0</code> of a single comparison <code>lhs op rhs</code>:
   * <code>x - p == lambda grad(g)</code>. The search point is kept if the refinement fails or moves
   * away from it.
   */
  private static IExpr polishOnBoundary(IExpr condition, IAST variables, IExpr p, IAST nearest,
      EvalEngine engine) {
    if (!condition.isAST(S.Equal, 3) && !condition.isAST(S.LessEqual, 3)
        && !condition.isAST(S.Less, 3) && !condition.isAST(S.GreaterEqual, 3)
        && !condition.isAST(S.Greater, 3)) {
      return nearest;
    }
    IExpr g = F.Subtract(condition.first(), condition.second());
    IExpr lambda = F.Dummy("lambda");
    IASTAppendable rules = F.ListAlloc(variables.argSize());
    for (int i = 1; i <= variables.argSize(); i++) {
      rules.append(F.Rule(variables.get(i), nearest.get(i)));
    }
    IExpr gradient = engine.evaluate(F.D(g, F.List(variables)));
    IExpr gradientAt = engine.evaluate(F.subst(gradient, rules));
    IExpr norm2 = engine.evaluate(F.Dot(gradientAt, gradientAt));
    if (!norm2.isReal() || norm2.isZero()) {
      return nearest;
    }
    IExpr lambda0 =
        engine.evaluate(F.Divide(F.Dot(F.Subtract(nearest, p), gradientAt), norm2));
    IASTAppendable equations = F.ListAlloc(variables.argSize() + 1);
    IASTAppendable specs = F.ListAlloc(variables.argSize() + 1);
    for (int i = 1; i <= variables.argSize(); i++) {
      equations.append(F.Equal(
          F.Subtract(F.Subtract(variables.get(i), p.getAt(i)), F.Times(lambda, gradient.getAt(i))),
          F.C0));
      specs.append(F.List(variables.get(i), nearest.get(i)));
    }
    equations.append(F.Equal(g, F.C0));
    specs.append(F.List(lambda, lambda0));
    IASTAppendable findRoot = F.ast(S.FindRoot, 2);
    findRoot.append(equations);
    findRoot.append(specs);
    IExpr root = engine.evalQuiet(findRoot);
    if (!root.isListOfRules()) {
      return nearest;
    }
    IExpr polished = engine.evaluate(F.subst(variables, (IAST) root));
    if (!polished.isList() || !((IAST) polished).forAll(x -> x.isReal())) {
      return nearest;
    }
    double moved = engine.evaluate(F.Norm(F.Subtract(polished, nearest))).evalfNaN();
    return moved < 1.0e-4 ? polished : nearest;
  }

  private IExpr pointNearest(IAST reg, IExpr p, EvalEngine engine) {
    if (reg.argSize() == 1) {
      IExpr pt = reg.arg1();
      if (pt.isList() && pt.argSize() > 0) {
        if (((IAST) pt).arg1().isList()) {
          IAST pts = (IAST) pt;
          IExpr bestPoint = F.NIL;
          IExpr minDist2 = F.NIL;
          for (int i = 1; i <= pts.argSize(); i++) {
            IExpr currentPt = pts.get(i);
            IExpr dist2 = engine.evaluate(F.Total(F.Sqr(F.Subtract(p, currentPt))));
            if (minDist2.isNIL()) {
              minDist2 = dist2;
              bestPoint = currentPt;
            } else {
              IExpr isLess = engine.evaluate(F.Less(dist2, minDist2));
              if (isLess.isTrue()) {
                minDist2 = dist2;
                bestPoint = currentPt;
              }
            }
          }
          return bestPoint;
        } else {
          return pt;
        }
      }
    }
    return F.NIL;
  }

  private IExpr ballNearest(IAST reg, IExpr p, EvalEngine engine, boolean isBoundaryOnly) {
    RegionPrimitives.BallSpec ball = RegionPrimitives.parseBall(reg);
    IExpr c = ball.center;
    IExpr r = ball.radius;

    if (r.isList()) {
      return F.NIL;
    }

    if (c.isList() && c.argSize() == ((IAST) p).argSize()) {
      IExpr v = engine.evaluate(F.Subtract(p, c));
      IExpr dist2 = engine.evaluate(F.Total(F.Sqr(v)));

      if (!isBoundaryOnly) {
        IExpr isInside = engine.evaluate(F.LessEqual(dist2, F.Sqr(r)));
        if (isInside.isTrue()) {
          return p;
        }
      }

      if (dist2.isZero()) {
        return F.NIL;
      }

      IExpr dist = engine.evaluate(F.Sqrt(dist2));
      IExpr scale = engine.evaluate(F.Divide(r, dist));
      return engine.evaluate(F.Plus(c, F.Times(scale, v)));
    }
    return F.NIL;
  }

  private IExpr boxNearest(IAST reg, IExpr p, EvalEngine engine) {
    IExpr[] corners = SignedRegionDistance.boxCorners(reg, engine);
    IExpr min = corners[0];
    IExpr max = corners[1];

    if (min.isList() && max.isList() && p.argSize() == ((IAST) min).argSize()) {
      IAST pL = (IAST) p;
      IAST minL = (IAST) min;
      IAST maxL = (IAST) max;

      IASTAppendable res = F.ListAlloc(pL.argSize());
      for (int i = 1; i <= pL.argSize(); i++) {
        IExpr clamped = engine.evaluate(F.Max(minL.get(i), F.Min(maxL.get(i), pL.get(i))));
        res.append(clamped);
      }
      return res;
    }
    return F.NIL;
  }

  private IExpr lineNearest(IAST reg, IExpr p, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() >= 2) {
        return nearestOnPolyline(pts, false, p, engine);
      }
    }
    return F.NIL;
  }

  /**
   * The closest projection of <code>p</code> onto the segments between consecutive points of
   * <code>pts</code>.
   *
   * @param pts the vertices
   * @param closed if <code>true</code> the segment from the last back to the first point is
   *        included (a polygon boundary)
   * @param p the point
   * @param engine the evaluation engine
   * @return the nearest point
   */
  private IExpr nearestOnPolyline(IAST pts, boolean closed, IExpr p, EvalEngine engine) {
    int n = pts.argSize();
    int segments = closed ? n : n - 1;
    IExpr bestPoint = F.NIL;
    IExpr minDist2 = F.NIL;
    for (int i = 1; i <= segments; i++) {
      IExpr v = pts.get(i);
      IExpr w = pts.get(i % n + 1);
      IExpr proj = closestPointOnSegment(p, v, w, engine);
      IExpr dist2 = engine.evaluate(F.Total(F.Sqr(F.Subtract(p, proj))));
      if (minDist2.isNIL()) {
        minDist2 = dist2;
        bestPoint = proj;
      } else {
        IExpr isLess = engine.evaluate(F.Less(dist2, minDist2));
        if (isLess.isTrue()) {
          minDist2 = dist2;
          bestPoint = proj;
        }
      }
    }
    return bestPoint;
  }

  private IExpr polygonNearest(IAST reg, IExpr p, EvalEngine engine) {
    IExpr isMem = engine.evaluate(F.RegionMember(reg, p));
    if (isMem.isTrue()) {
      return p;
    }

    IAST pts = extractPolygonPoints(reg);
    if (pts != null && pts.argSize() >= 3) {
      return nearestOnPolyline(pts, true, p, engine);
    }
    return F.NIL;
  }

  private IAST extractPolygonPoints(IAST reg) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      return (IAST) reg.arg1();
    }
    if (reg.head() == S.Triangle && reg.argSize() == 0) {
      return F.List(F.List(F.C0, F.C0), F.List(F.C1, F.C0), F.List(F.C0, F.C1));
    }
    return null;
  }

  private IExpr closestPointOnSegment(IExpr p, IExpr v, IExpr w, EvalEngine engine) {
    IExpr diff = engine.evaluate(F.Subtract(w, v));
    IExpr l2 = engine.evaluate(F.Total(F.Sqr(diff)));
    if (l2.isZero())
      return v;

    IExpr dot = engine.evaluate(F.Dot(F.Subtract(p, v), diff));
    IExpr t = engine.evaluate(F.Max(F.C0, F.Min(F.C1, F.Divide(dot, l2))));

    return engine.evaluate(F.Plus(v, F.Times(t, diff)));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
