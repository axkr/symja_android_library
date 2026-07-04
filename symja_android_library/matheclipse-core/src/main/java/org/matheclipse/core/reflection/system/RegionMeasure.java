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


public class RegionMeasure extends AbstractFunctionEvaluator {

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
            return pointMeasure(reg, engine);
          case ID.Line:
            return lineMeasure(reg, engine);
          case ID.Circle:
            return circleMeasure(reg, engine);
          case ID.Disk:
            return diskMeasure(reg, engine);
          case ID.Rectangle:
          case ID.Cuboid:
            return boxMeasure(reg, engine);
          case ID.Triangle:
            if (reg.argSize() == 0)
              return F.C1D2;
            IExpr area = S.Area.funEval(engine, reg);
            return area.isPresent() ? area : F.NIL;
          case ID.Polygon:
            return polygonMeasure(reg, engine);
          case ID.Ball:
            return ballMeasure(reg, engine);
          case ID.Sphere:
            return sphereMeasure(reg, engine);
          case ID.Ellipsoid:
            return ellipsoidMeasure(reg, engine);
          case ID.Cylinder:
            return cylinderMeasure(reg, engine);
          case ID.Cone:
            return coneMeasure(reg, engine);
          case ID.Simplex:
          case ID.Tetrahedron:
            return simplexMeasure(reg, engine);
          case ID.Parallelepiped:
            return parallelepipedMeasure(reg, engine);
        }
      }
    }
    return F.NIL;
  }

  private IExpr pointMeasure(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1) {
      IExpr p = reg.arg1();
      // Point[{{...}, {...}}] -> count points
      if (p.isList() && p.argSize() > 0 && ((IAST) p).arg1().isList()) {
        return F.ZZ(p.argSize());
      } else {
        return F.C1;
      }
    }
    return F.NIL;
  }

  private IExpr lineMeasure(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() >= 2) {
        IExpr len = F.C0;
        for (int i = 1; i < pts.argSize(); i++) {
          len = engine.evaluate(F.Plus(len, F.Norm(F.Subtract(pts.get(i), pts.get(i + 1)))));
        }
        return len;
      }
    }
    return F.NIL;
  }

  private IExpr circleMeasure(IAST reg, EvalEngine engine) {
    IExpr r = F.C1;
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }
    if (r.isList2()) {
      IExpr r1 = ((IAST) r).arg1();
      IExpr r2 = ((IAST) r).arg2();
      // Perimeter of an ellipse
      return engine.evaluate(
          F.Times(F.C4, r2, F.EllipticE(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2))))));
    }
    return engine.evaluate(F.Times(F.C2, S.Pi, r));
  }

  private IExpr diskMeasure(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 0) {
      return S.Pi;
    }
    IExpr r = F.C1;
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }
    if (r.isList2()) {
      IExpr r1 = ((IAST) r).arg1();
      IExpr r2 = ((IAST) r).arg2();
      return engine.evaluate(F.Times(S.Pi, r1, r2));
    }
    return engine.evaluate(F.Times(S.Pi, F.Sqr(r)));
  }

  private IExpr boxMeasure(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 0) {
      return F.C1;
    } else if (reg.argSize() == 1) {
      if (reg.arg1().isList()) {
        return F.C1;
      }
    } else if (reg.argSize() == 2) {
      if (reg.arg1().isList() && reg.arg2().isList()) {
        IAST p1 = (IAST) reg.arg1();
        IAST p2 = (IAST) reg.arg2();
        if (p1.argSize() == p2.argSize()) {
          IExpr prod = F.C1;
          for (int i = 1; i <= p1.argSize(); i++) {
            prod = F.Times(prod, F.Subtract(p2.get(i), p1.get(i)));
          }
          return engine.evaluate(F.Abs(prod));
        }
      }
    }
    return F.NIL;
  }

  private IExpr polygonMeasure(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      int n = pts.argSize();
      if (n >= 3) {
        // Exact Shoelace area formula
        IASTAppendable sum = F.PlusAlloc(n);
        for (int i = 1; i <= n; i++) {
          IExpr p1 = pts.get(i);
          IExpr p2 = pts.get(i % n + 1);
          if (p1.isList2() && p2.isList2()) {
            IExpr x1 = ((IAST) p1).arg1();
            IExpr y1 = ((IAST) p1).arg2();
            IExpr x2 = ((IAST) p2).arg1();
            IExpr y2 = ((IAST) p2).arg2();
            sum.append(F.Subtract(F.Times(x1, y2), F.Times(x2, y1)));
          } else {
            return F.NIL;
          }
        }
        return engine.evaluate(F.Times(F.C1D2, F.Abs(sum)));
      }
    }
    return F.NIL;
  }

  private IExpr ballMeasure(IAST reg, EvalEngine engine) {
    int dim = 3;
    IExpr r = F.C1;
    if (reg.argSize() >= 1) {
      IExpr c = reg.arg1();
      if (c.isList()) {
        dim = c.argSize();
      }
    }
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }

    // Exact N-dimensional volume formulation: V = (Pi^(dim/2) / Gamma(dim/2 + 1)) * r^dim
    IExpr piPow = F.Power(S.Pi, F.QQ(dim, 2));
    IExpr rPow = F.Power(r, F.ZZ(dim));
    IExpr gamma = F.Gamma(F.Plus(F.QQ(dim, 2), F.C1));
    return engine.evaluate(F.Divide(F.Times(piPow, rPow), gamma));
  }

  private IExpr sphereMeasure(IAST reg, EvalEngine engine) {
    int dim = 3;
    IExpr r = F.C1;
    if (reg.argSize() >= 1) {
      IExpr c = reg.arg1();
      if (c.isList()) {
        dim = c.argSize();
      }
    }
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }

    // Exact N-dimensional surface formulation: A = 2 * Pi^(dim/2) / Gamma(dim/2) * r^(dim-1)
    IExpr piPow = F.Power(S.Pi, F.QQ(dim, 2));
    IExpr rPow = F.Power(r, F.ZZ(dim - 1));
    IExpr gamma = F.Gamma(F.QQ(dim, 2));
    return engine.evaluate(F.Divide(F.Times(F.C2, piPow, rPow), gamma));
  }

  private IExpr ellipsoidMeasure(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 2) {
      IExpr c = reg.arg1();
      IExpr radii = reg.arg2();
      if (c.isList() && radii.isList() && c.argSize() == radii.argSize()) {
        int dim = c.argSize();
        IExpr piPow = F.Power(S.Pi, F.QQ(dim, 2));
        IExpr gamma = F.Gamma(F.Plus(F.QQ(dim, 2), F.C1));
        IExpr prod = F.C1;
        IAST rList = (IAST) radii;
        for (int i = 1; i <= rList.argSize(); i++) {
          prod = F.Times(prod, rList.get(i));
        }
        return engine.evaluate(F.Divide(F.Times(piPow, prod), gamma));
      }
    }
    return F.NIL;
  }

  private IExpr cylinderMeasure(IAST reg, EvalEngine engine) {
    IExpr p1 = F.List(F.C0, F.C0, F.CN1);
    IExpr p2 = F.List(F.C0, F.C0, F.C1);
    IExpr r = F.C1;
    if (reg.argSize() >= 1) {
      if (reg.arg1().isList2()) {
        IAST pts = (IAST) reg.arg1();
        p1 = pts.arg1();
        p2 = pts.arg2();
      }
    }
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }
    IExpr dist2 = engine.evaluate(F.Total(F.Sqr(F.Subtract(p1, p2))));
    return engine.evaluate(F.Times(S.Pi, F.Sqr(r), F.Sqrt(dist2)));
  }

  private IExpr coneMeasure(IAST reg, EvalEngine engine) {
    IExpr cyl = cylinderMeasure(reg, engine);
    if (cyl.isPresent()) {
      return engine.evaluate(F.Times(F.C1D3, cyl));
    }
    return F.NIL;
  }

  private IExpr simplexMeasure(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      int n = pts.argSize() - 1;
      if (n > 0) {
        IExpr p0 = pts.arg1();
        IASTAppendable mat = F.ListAlloc(n);
        for (int i = 2; i <= pts.argSize(); i++) {
          mat.append(engine.evaluate(F.Subtract(pts.get(i), p0)));
        }
        IExpr det = engine.evaluate(F.Det(mat));
        IExpr fact = engine.evaluate(F.Factorial(F.ZZ(n)));
        return engine.evaluate(F.Divide(F.Abs(det), fact));
      }
    }
    return F.NIL;
  }

  private IExpr parallelepipedMeasure(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 2 && reg.arg2().isList()) {
      IAST vecs = (IAST) reg.arg2();
      IExpr det = engine.evaluate(F.Det(vecs));
      return engine.evaluate(F.Abs(det));
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}