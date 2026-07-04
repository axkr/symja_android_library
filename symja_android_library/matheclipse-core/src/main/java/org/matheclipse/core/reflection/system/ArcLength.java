package org.matheclipse.core.reflection.system;

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
 * ArcLength(curve)
 * ArcLength(curve, {t, tmin, tmax})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the arc length of the specified region or parametric curve.
 * </p>
 * </blockquote>
 */
public class ArcLength extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
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
            case ID.Circle:
              return circleArcLength(reg, engine);
            case ID.Line:
              return lineArcLength(reg, engine);
            case ID.HalfLine:
            case ID.InfiniteLine:
              return S.Infinity;
            case ID.Point:
            case ID.Polygon:
            case ID.Disk:
            case ID.Triangle:
            case ID.Ellipsoid:
            case ID.Rectangle:
            case ID.Ball:
            case ID.Cuboid:
            case ID.Cylinder:
            case ID.Cone:
            case ID.Sphere:
              // Multi-dimensional filled solids and points have no 1D curve length
              return S.Undefined;
          }
        }
      }
    } else if (ast.argSize() == 2) {
      IExpr curve = ast.arg1();
      IExpr iter = ast.arg2();

      // Parametric curve: ArcLength({x(t), y(t), ...}, {t, tmin, tmax})
      if (iter.isList3()) {
        IAST iterator = (IAST) iter;
        IExpr t = iterator.arg1();
        IExpr tmin = iterator.arg2();
        IExpr tmax = iterator.arg3();

        // 1. Compute the derivative with respect to t
        IExpr dCurve = engine.evaluate(F.D(curve, t));
        IExpr integrand;

        // 2. Build the integrand: Sqrt(Sum(d/dt^2))
        if (curve.isList()) {
          integrand = engine.evaluate(F.Sqrt(F.Total(F.Power(dCurve, F.C2))));
        } else {
          // Scalar function fallback: Sqrt(1 + f'(t)^2)
          integrand = engine.evaluate(F.Sqrt(F.Plus(F.C1, F.Power(dCurve, F.C2))));
        }

        // 3. Simplify the integrand to resolve fundamental trigonometric identities
        // e.g., Sqrt(Cos(t)^2 + Sin(t)^2) -> 1
        integrand = engine.evaluate(F.Simplify(integrand));

        // 4. Integrate the speed over the interval
        return engine.evaluate(F.Integrate(integrand, F.List(t, tmin, tmax)));
      }
    }
    return F.NIL;
  }

  private IExpr circleArcLength(IAST reg, EvalEngine engine) {
    IExpr r = F.C1;
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }

    if (reg.argSize() >= 3) {
      IExpr theta = reg.arg3();
      if (theta.isList2()) {
        IExpr t1 = ((IAST) theta).arg1();
        IExpr t2 = ((IAST) theta).arg2();

        // Construct the piecewise bounds using UnitStep
        IExpr dtAbs = engine.evaluate(F.Abs(F.Subtract(t2, t1)));
        IExpr step1 = engine.evaluate(F.UnitStep(F.Subtract(F.Times(F.C2, S.Pi), dtAbs)));
        IExpr step2 = engine.evaluate(F.UnitStep(F.Plus(F.Times(F.CN2, S.Pi), dtAbs)));

        // Length of a standard circular arc
        if (!r.isList()) {
          IExpr partialArc = engine.evaluate(F.Times(r, dtAbs));
          IExpr fullPerimeter = engine.evaluate(F.Times(F.C2, S.Pi, r));

          return engine.evaluate(F.Plus(F.Times(partialArc, step1), F.Times(fullPerimeter, step2)));
        } else {
          // Elliptical arcs fallback to exact elliptic integral differences
          IExpr r1 = ((IAST) r).arg1();
          IExpr r2 = ((IAST) r).arg2();
          IExpr m = engine.evaluate(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2))));

          IExpr diffE = engine.evaluate(F.Subtract(F.EllipticE(t2, m), F.EllipticE(t1, m)));
          IExpr partialArc = engine.evaluate(F.Times(r2, diffE));
          IExpr fullPerimeter = engine.evaluate(F.Times(F.C4, r2, F.EllipticE(m)));

          return engine.evaluate(F.Plus(F.Times(partialArc, step1), F.Times(fullPerimeter, step2)));
        }
      }
        }

    if (r.isList2()) {
      // Full perimeter of an ellipse
      IExpr r1 = ((IAST) r).arg1();
      IExpr r2 = ((IAST) r).arg2();
      return engine.evaluate(
          F.Times(F.C4, r2, F.EllipticE(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2))))));
        }

    // Full perimeter of a standard circle
    return engine.evaluate(F.Times(F.C2, S.Pi, r));
    }

  private IExpr lineArcLength(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() >= 2) {
        IExpr len = F.C0;
        for (int i = 1; i < pts.argSize(); i++) {
          IExpr diff = engine.evaluate(F.Subtract(pts.get(i), pts.get(i + 1)));
          IExpr dist = engine.evaluate(F.Sqrt(F.Total(F.Sqr(diff))));
          len = engine.evaluate(F.Plus(len, dist));
        }
        return len;
      }
        }
    return F.NIL;
    }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
    }
}