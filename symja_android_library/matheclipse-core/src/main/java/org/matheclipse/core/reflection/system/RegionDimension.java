package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class RegionDimension extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }

    int dim = getRegionDimension(arg1);
    if (dim >= 0) {
      return F.ZZ(dim);
    }
    return F.NIL;
  }

  public static int getRegionDimension(IExpr reg) {
    if (reg.isAST()) {
      IAST ast = (IAST) reg;
      IExpr head = ast.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
            return 0;
          case ID.Line:
          case ID.Circle:
          case ID.HalfLine:
          case ID.InfiniteLine:
            return 1;
          case ID.Triangle:
          case ID.Polygon:
          case ID.Disk:
          case ID.Rectangle:
          case ID.Annulus:
            return 2;
          case ID.Cylinder:
          case ID.Cone:
            return 3;
          case ID.Sphere: {
            // Sphere is the (n-1)-dimensional surface of an n-ball
            int embDim = RegionEmbeddingDimension.getEmbeddingDimension(ast);
            return embDim > 0 ? embDim - 1 : -1;
          }
          case ID.Ball:
          case ID.Cuboid:
          case ID.Ellipsoid:
            return RegionEmbeddingDimension.getEmbeddingDimension(ast);
          case ID.Simplex:
            if (ast.argSize() == 0) {
              return 2;
            }
            if (ast.arg1().isInteger()) {
              return ast.arg1().toIntDefault();
            } else if (ast.arg1().isList()) {
              return ast.arg1().argSize() - 1;
            }
            return -1;
          case ID.Parallelepiped:
            if (ast.argSize() >= 2 && ast.arg2().isList()) {
              return ast.arg2().argSize();
            }
            return -1;
        }
      }
    }
    return -1;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}