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
    IExpr arg1 = ast.arg1();
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    arg1 = MeshFunctions.normalizeRegion(arg1);
    if (MeshFunctions.isMeshRegion(arg1)) {
      // a mesh region is built from finitely many explicit coordinates
      return S.True;
    }
    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
          case ID.Line:
          case ID.Circle:
          case ID.Disk:
          case ID.DiskSegment:
          case ID.Annulus:
          case ID.Rectangle:
          case ID.Cuboid:
          case ID.Triangle:
          case ID.Polygon:
          case ID.Ball:
          case ID.Sphere:
          case ID.Ellipsoid:
          case ID.Cylinder:
          case ID.Cone:
          case ID.Simplex:
          case ID.Parallelepiped:
          case ID.Parallelogram:
          case ID.Tetrahedron:
          case ID.Cube:
          case ID.Octahedron:
          case ID.Dodecahedron:
          case ID.Icosahedron:
          case ID.Torus:
          case ID.FilledTorus:
          case ID.SphericalShell:
          case ID.CapsuleShape:
          case ID.StadiumShape:
          case ID.EmptyRegion:
            return S.True;
          case ID.HalfLine:
          case ID.InfiniteLine:
          case ID.HalfPlane:
          case ID.InfinitePlane:
          case ID.HalfSpace:
          case ID.FullRegion:
            return S.False;
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
