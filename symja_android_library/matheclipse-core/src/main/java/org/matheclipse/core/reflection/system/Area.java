package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Area extends AbstractEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST() && arg1.isBuiltInFunction()) {
      IAST geoForm = (IAST) ast.arg1();
      int headID = arg1.headID();
      if (headID >= 0) {
        switch (headID) {
          case ID.Circle:
            return S.Undefined;
          case ID.Disk:
            return disk(geoForm);
          case ID.Rectangle:
            return rectangle(geoForm);
          case ID.Triangle:
            return triangle(geoForm);
          case ID.Polygon:
            return polygon(geoForm);
          case ID.Ellipsoid:
            return ellipsoid(geoForm);
          case ID.RegularPolygon:
            return regularPolygon(geoForm, engine);
          case ID.Sphere:
            return sphere(geoForm, engine);
        }
      }
    }
    return F.NIL;
  }

  private static IExpr disk(IAST geoForm) {
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
          return
          // [$ (r1*r2*Min(Pi, Abs(-t1+t2)/2)) $]
          F.Times(r1, r2, F.Min(S.Pi, F.Times(F.C1D2, F.Abs(F.Plus(F.Negate(t1), t2))))); // $$;
        } else {
          return F.NIL;
        }

      }
      if (geoForm.argSize() > 3) {
        return F.NIL;
      }
      return F.Times(S.Pi, r1, r2);
    } else if (geoForm.argSize() == 0) {
      return S.Pi;
    }
    return F.NIL;
  }

  private static IExpr rectangle(IAST geoForm) {
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
      return F.Abs(F.Times(F.Plus(F.Negate(a), c), F.Plus(F.Negate(b), d)));
    } else if (geoForm.argSize() == 0) {
      return F.C1;
    }
    return F.NIL;
  }

  private static IExpr triangle(IAST geoForm) {
    if (geoForm.argSize() >= 1 && geoForm.arg1().isList3()) {
      IAST list3 = (IAST) geoForm.arg1();
      if (geoForm.arg1().isListOfPoints(2)) {
        IExpr a1 = list3.arg1().first();
        IExpr a2 = list3.arg1().second();
        IExpr b1 = list3.arg2().first();
        IExpr b2 = list3.arg2().second();
        IExpr c1 = list3.arg3().first();
        IExpr c2 = list3.arg3().second();
        // (1/2)*Abs((-a2)*b1+a1*b2 +a2*c1-b2*c1-a1*c2+b1*c2)
        return F.Times(F.C1D2, F.Abs(F.Plus(F.Times(F.CN1, a2, b1), F.Times(a1, b2),
            F.Times(a2, c1), F.Times(F.CN1, b2, c1), F.Times(F.CN1, a1, c2), F.Times(b1, c2))));
      }
      if (list3.isListOfPoints(3)) {
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
        // Sqrt((e*g+b*d-a*e-b*g+a*h-d*h)^2+(f*g+c*d-a*f-c*g+a*i-d*i)^2+(f*h+c*e-b*f-c*h+b*i-e*i)^2)/2
        return F.Times(F.C1D2,
            F.Sqrt(F.Plus(
                F.Sqr(F.Plus(F.Times(e, g), F.Times(b, d), F.Times(F.CN1, a, e),
                    F.Times(F.CN1, b, g), F.Times(a, h), F.Times(F.CN1, d, h))),
                F.Sqr(F.Plus(F.Times(f, g), F.Times(c, d), F.Times(F.CN1, a, f),
                    F.Times(F.CN1, c, g), F.Times(a, i), F.Times(F.CN1, d, i))),
                F.Sqr(F.Plus(F.Times(f, h), F.Times(c, e), F.Times(F.CN1, b, f),
                    F.Times(F.CN1, c, h), F.Times(b, i), F.Times(F.CN1, e, i))))));
      }
    } else if (geoForm.argSize() == 0) {
      return F.C1D2;
    }
    return F.NIL;
  }

  private static IExpr polygon(IAST geoForm) {
    if (geoForm.argSize() == 1 && geoForm.arg1().isList()) {
      IAST pts = (IAST) geoForm.arg1();
      int n = pts.argSize();
      if (n >= 3) {
        // Exact Shoelace area formula (Surveyor's formula) for 2D Polygons
        IASTAppendable sum = F.PlusAlloc(n);
        for (int i = 1; i <= n; i++) {
          IExpr p1 = pts.get(i);
          IExpr p2 = pts.get(i % n + 1); // Loops back to the first point at the end
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
        return F.Times(F.C1D2, F.Abs(sum));
      }
    }
    return F.NIL;
  }

  private static IExpr ellipsoid(IAST geoForm) {
    if (geoForm.argSize() == 2) {
      IExpr center = geoForm.arg1();
      IExpr radii = geoForm.arg2();
      if (center.isList() && radii.isList() && center.argSize() == radii.argSize()) {
        if (center.argSize() == 2) {
          IExpr r1 = ((IAST) radii).arg1();
          IExpr r2 = ((IAST) radii).arg2();
          return F.Times(S.Pi, r1, r2);
        } else {
          return S.Undefined;
        }
      }
    }
    return F.NIL;
  }

  private static IExpr regularPolygon(IAST geoForm, EvalEngine engine) {
    IExpr nExpr = F.NIL;
    IExpr rExpr = F.C1; // default radius
    if (geoForm.argSize() == 1) {
      nExpr = geoForm.arg1();
    } else if (geoForm.argSize() == 2) {
      IExpr arg1 = geoForm.arg1();
      if (arg1.isList2()) {
        rExpr = ((IAST) arg1).arg1();
      } else {
        rExpr = arg1;
      }
      nExpr = geoForm.arg2();
    } else if (geoForm.argSize() == 3) {
      IExpr arg2 = geoForm.arg2();
      if (arg2.isList2()) {
        rExpr = ((IAST) arg2).arg1();
      } else {
        rExpr = arg2;
      }
      nExpr = geoForm.arg3();
    }
    if (nExpr.isPresent()) {
      // n/2 * Sin(2 Pi / n) * r^2
      return engine.evaluate(F.Times(F.Times(F.C1D2, nExpr),
          F.Sin(F.Times(F.C2Pi, F.Power(nExpr, F.CN1))), F.Sqr(rExpr)));
    }
    return F.NIL;
  }

  private static IExpr sphere(IAST geoForm, EvalEngine engine) {
    IExpr r = F.C1;
    if (geoForm.argSize() >= 1) {
      IExpr c = geoForm.arg1();
      // A 2-Sphere embedded in dimensions other than 3 doesn't typically have a 2D measure in WL
      // default Area calls
      if (c.isList() && c.argSize() != 3) {
        return S.Undefined;
      }
    }
    if (geoForm.argSize() >= 2) {
      r = geoForm.arg2();
    }
    return engine.evaluate(F.Times(F.C4, S.Pi, F.Sqr(r)));
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