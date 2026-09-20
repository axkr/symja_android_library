package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
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
      if (MeshFunctions.isMeshRegion(reg)) {
        // a mesh region is full dimensional
        return MeshFunctions.embeddingDimension((IAST) reg);
      }
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
            return 0;
          case ID.Line:
          case ID.Circle:
          case ID.HalfLine:
          case ID.InfiniteLine:
          case ID.Interval:
            return 1;
          case ID.Triangle:
          case ID.Polygon:
          case ID.Disk:
          case ID.Rectangle:
          case ID.Annulus:
          case ID.Torus:
          case ID.Parallelogram:
          case ID.RegularPolygon:
          case ID.HalfPlane:
          case ID.InfinitePlane:
          case ID.StadiumShape:
            return 2;
          case ID.DiskSegment:
            // DiskSegment(c, r, {theta1, theta2})
            return ast.argSize() == 3 ? 2 : -1;
          case ID.FilledTorus:
          case ID.SphericalShell:
            return 3;
          case ID.CapsuleShape:
            // a capsule is full dimensional - in the plane it is a stadium
            return RegionEmbeddingDimension.getEmbeddingDimension(ast);
          case ID.HalfSpace:
          case ID.FullRegion:
            // a half-space and the whole space are full dimensional
            return RegionEmbeddingDimension.getEmbeddingDimension(ast);
          case ID.ParametricRegion:
            // ParametricRegion({x1,...}, {params}) is swept out by its parameters
            return ast.argSize() == 2 && ast.arg2().isList() ? ast.arg2().argSize() : -1;
          case ID.Cylinder:
          case ID.Cone:
          case ID.Tetrahedron:
          case ID.Cube:
          case ID.Octahedron:
          case ID.Dodecahedron:
          case ID.Icosahedron:
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
          case ID.ImplicitRegion:
            return implicitRegionDimension(ast);
          case ID.BooleanRegion:
            return booleanRegionDimension(ast);
        }
      }
    }
    return -1;
  }

  /**
   * The dimension of <code>ImplicitRegion(condition, {x, y, ...})</code>: an equation cuts one
   * dimension away, inequalities cut none.
   *
   * @return <code>-1</code> for a condition with more than one equation, whose independence is not
   *         decided here - <code>x + y == 1 && 2*x + 2*y == 2</code> is one plane written twice
   */
  private static int implicitRegionDimension(IAST ast) {
    if (ast.argSize() != 2 || !ast.arg2().isList()) {
      return -1;
    }
    int embeddingDimension = ast.arg2().argSize();
    IExpr condition = ast.arg1();
    IAST parts = condition.isAnd() ? (IAST) condition : F.unaryAST1(S.And, condition);
    int equations = 0;
    for (int i = 1; i < parts.size(); i++) {
      IExpr part = parts.get(i);
      if (part.isAST(S.Equal, 3)) {
        equations++;
      } else if (!part.isAST(S.Less) && !part.isAST(S.LessEqual) && !part.isAST(S.Greater)
          && !part.isAST(S.GreaterEqual) && !part.isAST(S.Inequality) && !part.isTrue()) {
        // an Or, an Unequal or anything else may describe a region of any dimension at all
        return -1;
      }
    }
    if (equations > 1 || equations > embeddingDimension) {
      return -1;
    }
    return embeddingDimension - equations;
  }

  /**
   * The dimension of a <code>BooleanRegion</code>, which is the dimension of what its parts leave
   * of each other.
   *
   * <p>
   * Asking for all of the parts at once leaves the thinnest of them, as long as only that one is
   * thinner than the space it lives in: a box cut by a plane is the plane's two dimensions, and two
   * solids meet in a solid. Asking for either of them leaves the widest, which is what a union
   * always is.
   *
   * @return <code>-1</code> when the parts are combined in any other way, when one of their
   *         dimensions is unknown, or when more than one part is thinner than the space - two
   *         planes of the space may meet in a line, in a plane, or not at all
   */
  private static int booleanRegionDimension(IAST ast) {
    if (ast.argSize() != 2 || !ast.arg2().isList() || !ast.arg1().isAST(S.Function, 2)) {
      return -1;
    }
    IAST parts = (IAST) ast.arg2();
    IExpr function = ast.arg1().first();
    boolean all = function.isAnd();
    if (!all && !function.isOr()) {
      return -1;
    }
    IAST combination = (IAST) function;
    if (combination.argSize() != parts.argSize()) {
      return -1;
    }
    for (int i = 1; i <= combination.argSize(); i++) {
      if (!combination.get(i).isAST(S.Slot, 2) || combination.get(i).first().toIntDefault() != i) {
        return -1;
      }
    }
    int embeddingDimension = RegionEmbeddingDimension.getEmbeddingDimension(ast);
    int smallest = Integer.MAX_VALUE;
    int largest = -1;
    int thin = 0;
    for (int i = 1; i <= parts.argSize(); i++) {
      int dimension = getRegionDimension(parts.get(i));
      if (dimension < 0) {
        return -1;
      }
      smallest = Math.min(smallest, dimension);
      largest = Math.max(largest, dimension);
      if (dimension < embeddingDimension) {
        thin++;
      }
    }
    if (largest < 0) {
      return -1;
    }
    if (!all) {
      return largest;
    }
    return thin > 1 ? -1 : smallest;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
