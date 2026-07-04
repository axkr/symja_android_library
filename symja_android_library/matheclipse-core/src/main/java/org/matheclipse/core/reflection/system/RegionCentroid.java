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

public class RegionCentroid extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();

    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }

    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
          case ID.Disk:
          case ID.Circle:
          case ID.Ball:
          case ID.Sphere:
          case ID.Ellipsoid:
            return centerCentroid(reg, engine);
          case ID.Rectangle:
          case ID.Cuboid:
            return boxCentroid(reg, engine);
          case ID.Triangle:
          case ID.Simplex:
          case ID.Tetrahedron:
            return meanCentroid(reg, engine);
          case ID.Line:
            return lineCentroid(reg, engine);
          case ID.Polygon:
            return polygonCentroid(reg, engine);
        }
      }
    }
    return F.NIL;
  }

  private IExpr centerCentroid(IAST reg, EvalEngine engine) {
    IExpr head = reg.head();
    if (head == S.Point) {
      if (reg.argSize() == 1) {
        IExpr p = reg.arg1();
        if (p.isList() && p.argSize() > 0 && ((IAST) p).arg1().isList()) {
          return engine.evaluate(F.Mean(p));
        }
        return p;
      }
    } else {
      if (reg.argSize() == 0) {
        if (head == S.Disk || head == S.Circle) {
          return F.CListC0C0;
        } else {
          return F.List(F.C0, F.C0, F.C0);
        }
      } else if (reg.argSize() >= 1) {
        if (reg.arg1().isList() || reg.arg1().isSymbol()) {
          return reg.arg1();
        }
      }
    }
    return F.NIL;
  }

  private IExpr boxCentroid(IAST reg, EvalEngine engine) {
    IExpr head = reg.head();
    int dim = (head == S.Rectangle) ? 2 : 3;
    if (reg.argSize() == 0) {
      return (dim == 2) ? F.List(F.C1D2, F.C1D2) : F.List(F.C1D2, F.C1D2, F.C1D2);
    } else if (reg.argSize() == 1) {
      if (reg.arg1().isList()) {
        IAST p = (IAST) reg.arg1();
        IASTAppendable res = F.ListAlloc(p.argSize());
        for (int i = 1; i <= p.argSize(); i++) {
          res.append(F.Plus(p.get(i), F.C1D2));
        }
        return engine.evaluate(res);
      }
    } else if (reg.argSize() == 2) {
      if (reg.arg1().isList() && reg.arg2().isList()) {
        return engine.evaluate(F.Times(F.C1D2, F.Plus(reg.arg1(), reg.arg2())));
      }
    }
    return F.NIL;
  }

  private IExpr meanCentroid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 0) {
      if (reg.head() == S.Triangle) {
        return F.List(F.QQ(1, 3), F.QQ(1, 3));
      }
    } else if (reg.argSize() == 1) {
      if (reg.arg1().isList()) {
        return engine.evaluate(F.Mean(reg.arg1()));
      }
    }
    return F.NIL;
  }

  private IExpr lineCentroid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() == 2) {
        return engine.evaluate(F.Mean(pts));
      } else if (pts.argSize() > 2) {
        // Weighted average for polyline
        IExpr totalLen = F.C0;
        IASTAppendable sum = F.PlusAlloc(pts.argSize());
        sum.append(F.C0);
        for (int i = 1; i < pts.argSize(); i++) {
          IExpr p1 = pts.get(i);
          IExpr p2 = pts.get(i + 1);
          IExpr len = engine.evaluate(F.Norm(F.Subtract(p1, p2)));
          IExpr mid = engine.evaluate(F.Times(F.C1D2, F.Plus(p1, p2)));
          totalLen = engine.evaluate(F.Plus(totalLen, len));
          sum.append(F.Times(len, mid));
        }
        sum.remove(1);
        return engine.evaluate(F.Divide(F.Plus(sum), totalLen));
      }
    }
    return F.NIL;
  }

  private IExpr polygonCentroid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      int n = pts.argSize();
      if (n >= 3) {
        // Exact area-weighted formula (Shoelace method) for 2D Polygons
        if (pts.arg1().isList2()) {
          IASTAppendable sum = F.PlusAlloc(n);
          IExpr areaSum = F.C0;
          for (int i = 1; i <= n; i++) {
            IExpr p1 = pts.get(i);
            IExpr p2 = pts.get(i % n + 1);
            IExpr x1 = ((IAST) p1).arg1();
            IExpr y1 = ((IAST) p1).arg2();
            IExpr x2 = ((IAST) p2).arg1();
            IExpr y2 = ((IAST) p2).arg2();
            IExpr cross = engine.evaluate(F.Subtract(F.Times(x1, y2), F.Times(x2, y1)));
            areaSum = engine.evaluate(F.Plus(areaSum, cross));
            sum.append(F.Times(cross, F.Plus(p1, p2)));
          }
          IExpr factor = engine.evaluate(F.Times(F.C3, areaSum));
          if (!factor.isZero()) {
            return engine.evaluate(F.Divide(sum, factor));
          }
        }
        // Fallback to mean of vertices
        return engine.evaluate(F.Mean(pts));
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}