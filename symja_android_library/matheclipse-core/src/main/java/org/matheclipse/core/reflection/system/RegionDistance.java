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
 * RegionDistance(region, point)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the shortest distance from point to the region.
 * </p>
 * </blockquote>
 */
public class RegionDistance extends AbstractFunctionEvaluator {
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() != 2)
      return F.NIL;
    IExpr arg1 = ast.arg1();
    IExpr p = ast.arg2();

    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }

    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
            return pointDistance(reg, p, engine);
          case ID.Line:
            return lineDistance(reg, p, engine);
          case ID.Circle:
          case ID.Sphere:
          case ID.Disk:
          case ID.Ball:
            return SignedRegionDistance.ballDistance(reg, p, engine, false);
          case ID.Rectangle:
          case ID.Cuboid:
            return SignedRegionDistance.boxDistance(reg, p, engine, false);
          case ID.Triangle:
            return SignedRegionDistance.triangleDistance(reg, p, engine, false);
        }
      }
    }
    return F.NIL;
  }

  private IExpr pointDistance(IAST reg, IExpr p, EvalEngine engine) {
    if (reg.argSize() == 1) {
      IExpr pt = reg.arg1();
      if (pt.isList() && pt.argSize() > 0) {
        if (((IAST) pt).arg1().isList()) {
          IAST pts = (IAST) pt;
          IExpr minDist = engine.evaluate(F.Norm(F.Subtract(p, pts.arg1())));
          for (int i = 2; i <= pts.argSize(); i++) {
            minDist = engine.evaluate(F.Min(minDist, F.Norm(F.Subtract(p, pts.get(i)))));
          }
          return minDist;
        } else {
          return engine.evaluate(F.Norm(F.Subtract(p, pt)));
        }
      }
    }
    return F.NIL;
  }

  private IExpr lineDistance(IAST reg, IExpr p, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() >= 2) {
        IExpr minDist = SignedRegionDistance.distanceToSegment(p, pts.arg1(), pts.arg2(), engine);
        for (int i = 2; i < pts.argSize(); i++) {
          minDist = engine.evaluate(F.Min(minDist,
              SignedRegionDistance.distanceToSegment(p, pts.get(i), pts.get(i + 1), engine)));
        }
        return minDist;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}