package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class RegionEmbeddingDimension extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }

    int dim = getEmbeddingDimension(arg1);
    if (dim >= 0) {
      return F.ZZ(dim);
    }
    return F.NIL;
  }

  public static int getEmbeddingDimension(IExpr reg) {
    if (reg.isAST()) {
      IAST ast = (IAST) reg;
      IExpr head = ast.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
          case ID.Line:
          case ID.Triangle:
          case ID.Polygon:
          case ID.HalfLine:
          case ID.InfiniteLine:
            if (ast.argSize() == 0) {
              return 2;
            }
            return firstVectorSize(ast.arg1());
          case ID.Simplex:
            if (ast.argSize() == 0) {
              return 2;
            }
            if (ast.arg1().isInteger()) {
              return ast.arg1().toIntDefault();
            }
            return firstVectorSize(ast.arg1());
          case ID.Parallelepiped:
            if (ast.argSize() == 0) {
              return -1;
            }
            return firstVectorSize(ast.arg1());
          case ID.Disk:
          case ID.Circle:
          case ID.Rectangle:
          case ID.Annulus:
            if (ast.argSize() == 0) {
              return 2;
            }
            if (ast.arg1().isList()) {
              return ast.arg1().argSize();
            }
            return -1;
          case ID.Ball:
          case ID.Sphere:
          case ID.Cuboid:
          case ID.Ellipsoid:
            if (ast.argSize() == 0) {
              return 3;
            }
            if (ast.arg1().isList()) {
              return ast.arg1().argSize();
            }
            return -1;
          case ID.Cylinder:
          case ID.Cone:
            if (ast.argSize() == 0) {
              return 3;
            }
            if (ast.arg1().isList()) {
              return firstVectorSize(ast.arg1());
            }
            return -1;
        }
      }
    }
    return -1;
  }

  /**
   * Recursively walks into lists until it finds a flat vector (list of non-lists) to determine the
   * mathematical coordinate dimension.
   */
  private static int firstVectorSize(IExpr expr) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.argSize() > 0) {
        if (!list.arg1().isList()) {
          return list.argSize();
        } else {
          return firstVectorSize(list.arg1());
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