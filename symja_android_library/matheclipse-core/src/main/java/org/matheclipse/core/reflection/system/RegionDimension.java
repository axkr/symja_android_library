package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
    IAST variables = (IAST) ast.arg2();
    int embeddingDimension = variables.argSize();
    IExpr condition = ast.arg1();
    IAST parts = condition.isAnd() ? (IAST) condition : F.unaryAST1(S.And, condition);
    IASTAppendable equations = F.ListAlloc(parts.argSize());
    for (int i = 1; i < parts.size(); i++) {
      IExpr part = parts.get(i);
      if (part.isAST(S.Equal, 3)) {
        equations.append(F.Subtract(part.first(), part.second()));
      } else if (!part.isAST(S.Less) && !part.isAST(S.LessEqual) && !part.isAST(S.Greater)
          && !part.isAST(S.GreaterEqual) && !part.isAST(S.Inequality) && !part.isTrue()) {
        // an Or, an Unequal or anything else may describe a region of any dimension at all
        return -1;
      }
    }
    if (equations.argSize() == 0) {
      return embeddingDimension;
    }
    if (equations.argSize() == 1) {
      return embeddingDimension - 1;
    }
    // several equations only cut away as many dimensions as they are independent:
    // x + y == 1 && 2*x + 2*y == 2 is one plane written twice
    int rank = equationRank(equations, variables);
    if (rank < 0 || rank > embeddingDimension) {
      return -1;
    }
    return embeddingDimension - rank;
  }

  /**
   * How many dimensions a system of equations cuts away, which is the rank of the system.
   *
   * @return <code>-1</code> when an equation is not linear in the variables, or when the system
   *         has no solution at all - an empty region, whose dimension is not decided here
   */
  private static int equationRank(IAST equations, IAST variables) {
    EvalEngine engine = EvalEngine.get();
    IASTAppendable origin = F.ListAlloc(variables.argSize());
    for (int i = 1; i <= variables.argSize(); i++) {
      if (!variables.get(i).isVariable()) {
        return -1;
      }
      origin.append(F.Rule(variables.get(i), F.C0));
    }
    IASTAppendable matrix = F.ListAlloc(equations.argSize());
    IASTAppendable extended = F.ListAlloc(equations.argSize());
    for (int i = 1; i <= equations.argSize(); i++) {
      IExpr equation = engine.evaluate(equations.get(i));
      IASTAppendable row = F.ListAlloc(variables.argSize());
      for (int j = 1; j <= variables.argSize(); j++) {
        IExpr coefficient = engine.evaluate(F.D(equation, variables.get(j)));
        if (!isFreeOfAll(coefficient, variables)) {
          // a coefficient that still holds a variable makes the equation a curved surface, and
          // how many dimensions curved surfaces cut away together is not decided here
          return -1;
        }
        row.append(coefficient);
      }
      IExpr constant = engine.evaluate(F.subst(equation, origin));
      if (!isFreeOfAll(constant, variables)) {
        return -1;
      }
      matrix.append(row);
      extended.append(row.appendClone(constant));
    }
    int rank = engine.evaluate(F.MatrixRank(matrix)).toIntDefault();
    int extendedRank = engine.evaluate(F.MatrixRank(extended)).toIntDefault();
    if (rank < 0 || extendedRank < 0) {
      return -1;
    }
    // a system whose extended matrix has the larger rank contradicts itself and describes nothing
    return extendedRank > rank ? -1 : rank;
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
    if (all) {
      parts = mergeImplicitRegions(parts);
    }
    int embeddingDimension = RegionEmbeddingDimension.getEmbeddingDimension(ast);
    if (embeddingDimension < 1) {
      // parts of different spaces are no region at all, and have no dimension either
      return -1;
    }
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

  /**
   * The parts with the <code>ImplicitRegion</code>s among them over one and the same variables
   * merged into a single one of all their conditions.
   *
   * <p>
   * Asking for two of them at once is asking for one region of both conditions, and only then can
   * the equations be counted together: two planes of the space meet in a line, and the same plane
   * written twice is still a plane.
   */
  private static IAST mergeImplicitRegions(IAST parts) {
    IExpr variables = F.NIL;
    int count = 0;
    for (int i = 1; i <= parts.argSize(); i++) {
      IExpr part = parts.get(i);
      if (part.isAST(S.ImplicitRegion, 3) && part.second().isList()) {
        if (variables.isNIL()) {
          variables = part.second();
        } else if (!variables.equals(part.second())) {
          // over different variables they are not one condition, and are left as they are
          return parts;
        }
        count++;
      }
    }
    if (count < 2) {
      return parts;
    }
    IASTAppendable merged = F.ListAlloc(parts.argSize());
    IASTAppendable conditions = F.ast(S.And, count);
    for (int i = 1; i <= parts.argSize(); i++) {
      IExpr part = parts.get(i);
      if (part.isAST(S.ImplicitRegion, 3)) {
        conditions.append(part.first());
      } else {
        merged.append(part);
      }
    }
    merged.append(F.binaryAST2(S.ImplicitRegion, conditions, variables));
    return merged;
  }

  /**
   * Whether the expression holds none of the variables. <code>IExpr#isFree(IExpr)</code> takes its
   * argument as one pattern, so a list of variables has to be asked for one at a time.
   */
  private static boolean isFreeOfAll(IExpr expr, IAST variables) {
    for (int i = 1; i <= variables.argSize(); i++) {
      if (!expr.isFree(variables.get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
