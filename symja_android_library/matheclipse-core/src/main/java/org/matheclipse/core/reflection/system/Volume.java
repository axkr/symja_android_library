package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class Volume extends AbstractFunctionEvaluator {

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
          case ID.Cuboid:
            return cuboid(reg, engine);
          case ID.Cylinder:
            return cylinder(reg, engine, false);
          case ID.Cone:
            return cylinder(reg, engine, true);
          case ID.Ball:
            return ball(reg, engine);
          case ID.Ellipsoid:
            return ellipsoid(reg, engine);
          case ID.Tetrahedron:
          case ID.Simplex:
            return simplex(reg, engine);
          case ID.Parallelepiped:
            return parallelepiped(reg, engine);
          case ID.Point:
          case ID.Line:
          case ID.Circle:
          case ID.Disk:
          case ID.Rectangle:
          case ID.Triangle:
          case ID.Polygon:
          case ID.Sphere: // Sphere is a 2D surface; its 3-volume is Undefined
            return S.Undefined;
        }
      }
    }
    return F.NIL;
  }

  private IExpr cuboid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 0) {
      return F.C1; // Default Cuboid() is the unit hypercube
    } else if (reg.argSize() == 1) {
      IExpr p = reg.arg1();
      if (p.isList()) {
        if (p.argSize() == 3)
          return F.C1;
        return S.Undefined; // 2D cuboids have no 3-Volume
      }
    } else if (reg.argSize() == 2) {
      IExpr p1 = reg.arg1();
      IExpr p2 = reg.arg2();
      if (p1.isList() && p2.isList()) {
        if (p1.argSize() == 3 && p2.argSize() == 3) {
          IExpr dx = F.Subtract(((IAST) p2).arg1(), ((IAST) p1).arg1());
          IExpr dy = F.Subtract(((IAST) p2).arg2(), ((IAST) p1).arg2());
          IExpr dz = F.Subtract(((IAST) p2).arg3(), ((IAST) p1).arg3());
          return engine.evaluate(F.Abs(F.Times(dx, dy, dz)));
        }
        return S.Undefined;
      }
        }
    return F.NIL;
  }

  private IExpr cylinder(IAST reg, EvalEngine engine, boolean isCone) {
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
    IExpr vol = engine.evaluate(F.Times(S.Pi, F.Sqr(r), F.Sqrt(dist2)));

    if (isCone) {
      vol = engine.evaluate(F.Times(F.C1D3, vol));
        }
    return vol;
  }

  private IExpr ball(IAST reg, EvalEngine engine) {
    IExpr c = F.List(F.C0, F.C0, F.C0);
    IExpr r = F.C1;

    if (reg.argSize() >= 1) {
      c = reg.arg1();
      if (c.isList() && c.argSize() != 3) {
        return S.Undefined;
      }
    }
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }

    return engine.evaluate(F.Times(F.QQ(4, 3), S.Pi, F.Power(r, F.C3)));
  }

  private IExpr ellipsoid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 2) {
      IExpr c = reg.arg1();
      IExpr radii = reg.arg2();
      if (c.isList() && radii.isList()) {
        if (c.argSize() == 3 && radii.argSize() == 3) {
          return engine.evaluate(F.Times(F.QQ(4, 3), S.Pi, ((IAST) radii).arg1(),
              ((IAST) radii).arg2(), ((IAST) radii).arg3()));
        }
        return S.Undefined;
      }
    }
    return F.NIL;
    }

  private IExpr simplex(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() == 4) { // 3D Simplex / Tetrahedron
        IExpr p1 = pts.arg1();
        IExpr p2 = pts.arg2();
        IExpr p3 = pts.arg3();
        IExpr p4 = pts.arg4();

        if (p1.isList() && p1.argSize() == 3) {
          IAST mat = F.List(engine.evaluate(F.Subtract(p2, p1)),
              engine.evaluate(F.Subtract(p3, p1)), engine.evaluate(F.Subtract(p4, p1)));
          return engine.evaluate(F.Times(F.QQ(1, 6), F.Abs(F.Det(mat))));
        }
        return S.Undefined;
      } else {
        return S.Undefined;
      }
    }
    return F.NIL;
  }

  private IExpr parallelepiped(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 2) {
      IExpr base = reg.arg1();
      IExpr vecs = reg.arg2();
      if (base.isList() && vecs.isList()) {
        if (base.argSize() == 3 && vecs.argSize() == 3) {
          return engine.evaluate(F.Abs(F.Det(vecs)));
        }
        return S.Undefined;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}