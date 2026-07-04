package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
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
        IExpr head = reg.head();
        int dim = (head == S.Disk || head == S.Circle) ? 2 : 3;
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
        IExpr head = reg.head();
        int dim = (head == S.Rectangle) ? 2 : 3;
        IExpr min = F.NIL;
        IExpr max = F.NIL;

        if (reg.argSize() == 0) {
          min = (dim == 2) ? F.List(F.C0, F.C0) : F.List(F.C0, F.C0, F.C0);
          max = (dim == 2) ? F.List(F.C1, F.C1) : F.List(F.C1, F.C1, F.C1);
        } else if (reg.argSize() == 1) {
          IExpr pt = reg.arg1();
          if (pt.isList()) {
            min = pt;
            IAST ptAST = (IAST) pt;
            IASTAppendable maxList = F.ListAlloc(ptAST.argSize());
            for (int i = 1; i <= ptAST.argSize(); i++) {
              maxList.append(engine.evaluate(F.Plus(ptAST.get(i), F.C1)));
            }
            max = maxList;
          }
        } else if (reg.argSize() >= 2) {
          min = reg.arg1();
          max = reg.arg2();
        }

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
            IExpr bestPoint = F.NIL;
            IExpr minDist2 = F.NIL;
            for (int i = 1; i < pts.argSize(); i++) {
              IExpr v = pts.get(i);
              IExpr w = pts.get(i + 1);
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
        }
        return F.NIL;
    }

      private IExpr polygonNearest(IAST reg, IExpr p, EvalEngine engine) {
        IExpr isMem = engine.evaluate(F.RegionMember(reg, p));
        if (isMem.isTrue()) {
          return p;
        }

        IAST pts = extractPolygonPoints(reg);
        if (pts != null && pts.argSize() >= 3) {
          int n = pts.argSize();
          IExpr bestPoint = F.NIL;
          IExpr minDist2 = F.NIL;
          for (int i = 1; i <= n; i++) {
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