package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Perimeter extends AbstractEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();

    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }

    if (arg1.isAST() && arg1.isBuiltInFunction()) {
      IAST geoForm = (IAST) arg1;
      int headID = arg1.headID();
      if (headID >= 0) {
        switch (headID) {
          case ID.Circle:
          case ID.Line:
            // 1D curves have length, but no 2D perimeter
            return S.Undefined;
          case ID.Disk:
            return disk(geoForm, engine);
          case ID.Rectangle:
            return rectangle(geoForm, engine);
          case ID.Triangle:
            return triangle(geoForm, engine);
          case ID.Polygon:
            return polygon(geoForm, engine);
          case ID.Ellipsoid:
            return ellipsoid(geoForm, engine);
        }
      }
    }
    return F.NIL;
  }

  private static IExpr disk(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 0) {
      return F.C2Pi;
    }
    if (geoForm.argSize() >= 1 && geoForm.arg1().isList2()) {
      IExpr t1 = F.C0;
      IExpr t2 = F.C2Pi;
      IExpr r1 = F.C1;
      IExpr r2 = F.C1;
      if (geoForm.argSize() >= 2) {
        if (geoForm.arg2().isList2()) {
          r1 = geoForm.arg2().first();
          r2 = geoForm.arg2().second();
        } else if (!geoForm.arg2().isList()) {
          r1 = geoForm.arg2();
          r2 = r1;
        } else {
          return F.NIL;
        }
      }

      if (geoForm.argSize() == 3) {
        if (geoForm.arg3().isList2()) {
          t1 = geoForm.arg3().first();
          t2 = geoForm.arg3().second();
          return engine.evaluate(
              // [$ (r2*(-EllipticE(t1, 1 - r1^2/r2^2) + EllipticE(t2, 1 - r1^2/r2^2)) +
              // Sqrt(r1^2*Cos(t1)^2 + r2^2*Sin(t1)^2) +
              // Sqrt(r1^2*Cos(t2)^2 + r2^2*Sin(t2)^2))*UnitStep(2*Pi - Abs(-t1 + t2)) +
              // 4*r2*EllipticE(1 - r1^2/r2^2)*UnitStep(-2*Pi + Abs(-t1 + t2)) $]
              F.Plus(
                  F.Times(
                      F.Plus(
                          F.Times(
                              r2, F
                                  .Plus(
                                      F.Negate(F.EllipticE(t1,
                                          F.Plus(F.C1,
                                              F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2))))),
                                      F.EllipticE(t2,
                                          F.Plus(F.C1,
                                              F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2)))))),
                          F.Sqrt(F.Plus(F.Times(F.Sqr(r1), F.Sqr(F.Cos(t1))),
                              F.Times(F.Sqr(r2), F.Sqr(F.Sin(t1))))),
                          F.Sqrt(F.Plus(F.Times(F.Sqr(r1), F.Sqr(F.Cos(t2))),
                              F.Times(F.Sqr(r2), F.Sqr(F.Sin(t2)))))),
                      F.UnitStep(F.Subtract(F.C2Pi, F.Abs(F.Plus(F.Negate(t1), t2))))),
                  F.Times(F.C4, r2,
                      F.EllipticE(F.Plus(F.C1, F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2)))),
                      F.UnitStep(F.Plus(F.CN2Pi, F.Abs(F.Plus(F.Negate(t1), t2)))))) // $$;
          );
        } else {
          return F.NIL;
        }
      }
      if (geoForm.argSize() > 3) {
        return F.NIL;
      }

      return engine.evaluate(
          F.Times(F.C4, r2, F.EllipticE(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2))))));
    }
    return F.NIL;
  }

  private static IExpr rectangle(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 0) {
      return F.C4;
    }
    IExpr a, b, c, d;
    if (geoForm.argSize() >= 1 && geoForm.arg1().isList2()) {
      a = geoForm.arg1().first();
      b = geoForm.arg1().second();
      if (geoForm.argSize() == 1) {
        c = a.plus(F.C1);
        d = b.plus(F.C1);
      } else if (geoForm.argSize() == 2 && geoForm.arg2().isList2()) {
        c = geoForm.arg2().first();
        d = geoForm.arg2().second();
      } else {
        return F.NIL;
      }
      return engine.evaluate(
          F.Times(F.C2, F.Plus(F.Abs(F.Plus(F.Negate(a), c)), F.Abs(F.Plus(F.Negate(b), d)))));
    }
    return F.NIL;
  }

  private static IExpr triangle(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 0) {
      // Default Triangle: {{0, 0}, {1, 0}, {0, 1}}
      return F.Plus(F.C2, F.CSqrt2);
    }
    if (geoForm.argSize() == 1 && geoForm.arg1().isList3()) {
      IAST list3 = (IAST) geoForm.arg1();
      if (list3.arg1().isList2() && list3.arg2().isList2() && list3.arg3().isList2()) {
        IAST l1 = (IAST) list3.arg1();
        IAST l2 = (IAST) list3.arg2();
        IAST l3 = (IAST) list3.arg3();
        IExpr a = l1.arg1();
        IExpr b = l1.arg2();
        IExpr c = l2.arg1();
        IExpr d = l2.arg2();
        IExpr e = l3.arg1();
        IExpr f = l3.arg2();
        // Sqrt((a-c)^2+(b-d)^2)+Sqrt((a-e)^2+(b-f)^2)+Sqrt((c-e)^2+(d-f)^2)
        return engine
            .evaluate(F.Plus(F.Sqrt(F.Plus(F.Sqr(F.Subtract(a, c)), F.Sqr(F.Subtract(b, d)))),
                F.Sqrt(F.Plus(F.Sqr(F.Subtract(a, e)), F.Sqr(F.Subtract(b, f)))),
                F.Sqrt(F.Plus(F.Sqr(F.Subtract(c, e)), F.Sqr(F.Subtract(d, f))))));
      }
      if (list3.arg1().isList3() && list3.arg2().isList3() && list3.arg3().isList3()) {
        IAST l1 = (IAST) list3.arg1();
        IAST l2 = (IAST) list3.arg2();
        IAST l3 = (IAST) list3.arg3();
        IExpr a = l1.arg1();
        IExpr b = l1.arg2();
        IExpr c = l1.arg3();
        IExpr d = l2.arg1();
        IExpr e = l2.arg2();
        IExpr f = l2.arg3();
        IExpr g = l3.arg1();
        IExpr h = l3.arg2();
        IExpr i = l3.arg3();
        // Sqrt((a-d)^2+(b-e)^2+(c-f)^2)+Sqrt((a-g)^2+(b-h)^2+(c-i)^2)+Sqrt((d-g)^2+(e-h)^2+(f-i)^2)
        return engine.evaluate(F.Plus(
            F.Sqrt(
                F.Plus(F.Sqr(F.Subtract(a, d)), F.Sqr(F.Subtract(b, e)), F.Sqr(F.Subtract(c, f)))),
            F.Sqrt(
                F.Plus(F.Sqr(F.Subtract(a, g)), F.Sqr(F.Subtract(b, h)), F.Sqr(F.Subtract(c, i)))),
            F.Sqrt(F.Plus(F.Sqr(F.Subtract(d, g)), F.Sqr(F.Subtract(e, h)),
                F.Sqr(F.Subtract(f, i))))));
      }
    }
    return F.NIL;
  }

  private static IExpr polygon(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 1 && geoForm.arg1().isList()) {
      IAST pts = (IAST) geoForm.arg1();
      if (pts.argSize() >= 2) {
        IExpr len = F.C0;
        for (int i = 1; i <= pts.argSize(); i++) {
          // Evaluates pairwise distances around the sequence of polygon vertices
          IExpr diff = engine.evaluate(F.Subtract(pts.get(i), pts.get(i % pts.argSize() + 1)));
          IExpr dist = engine.evaluate(F.Sqrt(F.Total(F.Sqr(diff))));
          len = engine.evaluate(F.Plus(len, dist));
        }
        return len;
      }
    }
    return F.NIL;
  }

  private static IExpr ellipsoid(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 2) {
      IExpr center = geoForm.arg1();
      IExpr radii = geoForm.arg2();
      if (center.isList() && radii.isList()) {
        if (center.argSize() == 2 && radii.argSize() == 2) {
          IExpr r1 = ((IAST) radii).arg1();
          IExpr r2 = ((IAST) radii).arg2();
          IExpr m = engine.evaluate(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2))));
          return engine.evaluate(F.Times(F.C4, r2, F.EllipticE(m)));
        }
        // The perimeter is undefined for a 3D volume or higher
        return S.Undefined;
      }
    }
    return F.NIL;
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
