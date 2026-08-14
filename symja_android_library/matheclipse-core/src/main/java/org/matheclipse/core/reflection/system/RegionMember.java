package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.builtin.RegionPrimitives;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class RegionMember extends AbstractFunctionEvaluator {
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() != 2)
      return F.NIL;
    IExpr arg1 = ast.arg1();
    IExpr p = ast.arg2();

    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    arg1 = MeshFunctions.normalizeRegion(arg1);
    if (MeshFunctions.isMeshRegion(arg1)) {
      int embeddingDimension = MeshFunctions.embeddingDimension((IAST) arg1);
      if (embeddingDimension == 3) {
        return MeshFunctions.member3D((IAST) arg1, p, engine);
      }
      if (embeddingDimension == 2) {
        return MeshFunctions.member2D((IAST) arg1, p, engine);
      }
    }

    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Disk:
          case ID.Ball:
            return engine.evaluate(
                F.LessEqual(SignedRegionDistance.ballDistance(reg, p, engine, true), F.C0));
          case ID.Circle:
          case ID.Sphere:
            return engine
                .evaluate(F.Equal(SignedRegionDistance.ballDistance(reg, p, engine, false), F.C0));
          case ID.Rectangle:
          case ID.Cuboid:
            return engine.evaluate(
                F.LessEqual(SignedRegionDistance.boxDistance(reg, p, engine, true), F.C0));
          case ID.Triangle:
            return engine.evaluate(
                F.LessEqual(SignedRegionDistance.triangleDistance(reg, p, engine, true), F.C0));
          case ID.Polygon:
            return polygonMember(reg, p, engine);
          case ID.HalfSpace:
            return halfSpaceMember(reg, p, engine);
          case ID.StadiumShape:
            return stadiumShapeMember(reg, p, engine);
        }
      }
    }
    return F.NIL;
  }

  /** A point of the half-space <code>n.x &lt;= c</code>. */
  private IExpr halfSpaceMember(IAST reg, IExpr p, EvalEngine engine) {
    RegionPrimitives.HalfSpaceSpec spec = RegionPrimitives.parseHalfSpace(reg, engine);
    if (spec == null || !p.isList() || p.argSize() != spec.normal.argSize()) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(F.LessEqual(F.Dot(spec.normal, p), spec.offset));
    // for symbolic coordinates the inequality can't be decided
    return result.isTrue() || result.isFalse() ? result : F.NIL;
  }

  /** All points which are no further than <code>r</code> away from the axis segment. */
  private IExpr stadiumShapeMember(IAST reg, IExpr p, EvalEngine engine) {
    RegionPrimitives.StadiumSpec spec = RegionPrimitives.parseStadiumShape(reg);
    if (spec == null || !p.isList2()) {
      return F.NIL;
    }
    IExpr distance =
        SignedRegionDistance.distanceToSegment(p, (IAST) spec.p1, (IAST) spec.p2, engine);
    if (!distance.isPresent()) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(F.LessEqual(distance, spec.radius));
    return result.isTrue() || result.isFalse() ? result : F.NIL;
  }

  private IExpr polygonMember(IAST reg, IExpr p, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList() && p.isList2()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() >= 3) {
        IExpr px = ((IAST) p).arg1();
        IExpr py = ((IAST) p).arg2();

        int wn = 0;
        for (int i = 1; i <= pts.argSize(); i++) {
          IAST v1 = (IAST) pts.get(i);
          IAST v2 = (IAST) pts.get(i % pts.argSize() + 1);

          IExpr dist = SignedRegionDistance.distanceToSegment(p, v1, v2, engine);
          if (engine.evaluate(F.Equal(dist, F.C0)).isTrue()) {
            return S.True;
          }

          IExpr v1y = v1.arg2();
          IExpr v2y = v2.arg2();
          IExpr v1x = v1.arg1();
          IExpr v2x = v2.arg1();

          IExpr isLeft =
              engine.evaluate(F.Subtract(F.Times(F.Subtract(v2x, v1x), F.Subtract(py, v1y)),
                  F.Times(F.Subtract(px, v1x), F.Subtract(v2y, v1y))));

          IExpr c1 = engine.evaluate(F.LessEqual(v1y, py));
          IExpr c2 = engine.evaluate(F.Greater(v2y, py));

          if (c1.isTrue()) {
            if (c2.isTrue() && engine.evaluate(F.Greater(isLeft, F.C0)).isTrue()) {
              wn++;
            }
          } else if (c1.isFalse()) {
            if (engine.evaluate(F.LessEqual(v2y, py)).isTrue()
                && engine.evaluate(F.Less(isLeft, F.C0)).isTrue()) {
              wn--;
            }
          } else {
            return F.NIL;
          }
        }
        return wn != 0 ? S.True : S.False;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
