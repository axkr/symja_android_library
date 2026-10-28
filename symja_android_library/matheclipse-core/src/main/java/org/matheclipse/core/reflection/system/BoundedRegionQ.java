package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.RegionPrimitives;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * BoundedRegionQ(region)
 * </pre>
 *
 * <blockquote>
 * <p>
 * returns <code>True</code> if <code>region</code> can be included in a box with finite ranges.
 * </p>
 * </blockquote>
 */
public class BoundedRegionQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = RegionPrimitives.normalizedRegion(ast.arg1());
    if (MeshFunctions.isMeshRegion(arg1)) {
      // a mesh region is built from finitely many explicit coordinates
      return S.True;
    }
    IExpr head = arg1.head();
    if (arg1.isAST() && head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.HalfLine:
        case ID.InfiniteLine:
        case ID.HalfPlane:
        case ID.InfinitePlane:
        case ID.HalfSpace:
        case ID.FullRegion:
          return S.False;
      }
    }
    // every other region primitive is bounded
    return RegionQ.isRegion(arg1) ? S.True : F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
