package org.matheclipse.core.reflection.system;

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
 * SurfaceArea(region)
 * </pre>
 *
 * <blockquote>
 * <p>
 * returns the surface area of the three dimensional <code>region</code>.
 * </p>
 * </blockquote>
 */
public class SurfaceArea extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
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
          case ID.Ball:
          case ID.Sphere:
            return ball(reg, engine);
          case ID.Cuboid:
            return cuboid(reg, engine);
          case ID.Cube:
          case ID.Octahedron:
          case ID.Dodecahedron:
          case ID.Icosahedron:
            return platonicSolid(reg, engine);
          case ID.Tetrahedron:
            if (RegionPrimitives.isCornerPointsForm(reg)) {
              return F.NIL;
            }
            return platonicSolid(reg, engine);
          case ID.Cylinder:
            return cylinder(reg, engine, false);
          case ID.Cone:
            return cylinder(reg, engine, true);
          case ID.SphericalShell:
            return sphericalShell(reg, engine);
          case ID.CapsuleShape:
            return capsuleShape(reg, engine);
          case ID.BoundaryMeshRegion:
            return boundaryMesh(reg);
        }
      }
    }
    return F.NIL;
  }

  /**
   * The area of the boundary of a solid mesh, the sum of the areas of its faces - a machine number,
   * as in the reference implementation. A mesh in the plane bounds an area, which has no surface.
   */
  private static IExpr boundaryMesh(IAST reg) {
    if (reg.argSize() < 2 || !reg.arg1().isListOfLists()) {
      return F.NIL;
    }
    IAST coordinates = (IAST) reg.arg1();
    int dimension = coordinates.arg1().argSize();
    if (dimension == 2) {
      return S.Undefined;
    }
    if (dimension != 3) {
      return F.NIL;
    }
    double[][] points = new double[coordinates.argSize()][];
    for (int i = 1; i <= coordinates.argSize(); i++) {
      IExpr point = coordinates.get(i);
      if (!point.isList() || point.argSize() != 3) {
        return F.NIL;
      }
      points[i - 1] = new double[] {point.first().evalfNaN(), point.second().evalfNaN(),
          point.get(3).evalfNaN()};
    }
    IAST cells = reg.arg2().isList() ? (IAST) reg.arg2() : F.List(reg.arg2());
    double area = 0.0;
    for (IExpr cell : cells) {
      if (!cell.isAST(S.Polygon, 2) || !cell.first().isList()) {
        return F.NIL;
      }
      IAST faces = (IAST) cell.first();
      if (!faces.isListOfLists()) {
        faces = F.List(faces);
      }
      for (IExpr face : faces) {
        double faceArea = faceArea((IAST) face, points);
        if (Double.isNaN(faceArea)) {
          return F.NIL;
        }
        area += faceArea;
      }
    }
    return Double.isFinite(area) ? F.num(area) : F.NIL;
  }

  /** The area of a flat face given by the indices of its corners, by Newell's formula. */
  private static double faceArea(IAST face, double[][] points) {
    double[] normal = new double[3];
    for (int i = 1; i <= face.argSize(); i++) {
      int from = face.get(i).toIntDefault();
      int to = face.get(i % face.argSize() + 1).toIntDefault();
      if (from < 1 || from > points.length || to < 1 || to > points.length) {
        return Double.NaN;
      }
      double[] a = points[from - 1];
      double[] b = points[to - 1];
      normal[0] += a[1] * b[2] - a[2] * b[1];
      normal[1] += a[2] * b[0] - a[0] * b[2];
      normal[2] += a[0] * b[1] - a[1] * b[0];
    }
    return 0.5 * Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
  }

  private IExpr platonicSolid(IAST reg, EvalEngine engine) {
    RegionPrimitives.SolidSpec spec = RegionPrimitives.parsePlatonicSolid(reg);
    if (spec == null) {
      return F.NIL;
    }
    return RegionPrimitives.platonicSurfaceArea(((IBuiltInSymbol) reg.head()).ordinal(), spec.edge,
        engine);
  }

  /** <code>4*Pi*r^2</code> for a three dimensional ball or sphere. */
  private IExpr ball(IAST reg, EvalEngine engine) {
    IExpr r = F.C1;
    if (reg.argSize() >= 1 && reg.arg1().isList() && reg.arg1().argSize() != 3) {
      return S.Undefined;
    }
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }
    return engine.evaluate(F.Times(F.C4, S.Pi, F.Sqr(r)));
  }

  private IExpr cuboid(IAST reg, EvalEngine engine) {
    IExpr dx = F.C1;
    IExpr dy = F.C1;
    IExpr dz = F.C1;
    if (reg.argSize() == 1 && reg.arg1().isList3()) {
      // Cuboid(p) is the unit cube with lower corner p
    } else if (reg.argSize() == 2) {
      if (!reg.arg1().isList3() || !reg.arg2().isList3()) {
        return S.Undefined;
      }
      IAST p1 = (IAST) reg.arg1();
      IAST p2 = (IAST) reg.arg2();
      dx = F.Abs(F.Subtract(p2.arg1(), p1.arg1()));
      dy = F.Abs(F.Subtract(p2.arg2(), p1.arg2()));
      dz = F.Abs(F.Subtract(p2.arg3(), p1.arg3()));
    } else if (reg.argSize() > 2) {
      return F.NIL;
    }
    return engine
        .evaluate(F.Times(F.C2, F.Plus(F.Times(dx, dy), F.Times(dy, dz), F.Times(dx, dz))));
  }

  private IExpr cylinder(IAST reg, EvalEngine engine, boolean isCone) {
    IExpr p1 = F.List(F.C0, F.C0, F.CN1);
    IExpr p2 = F.List(F.C0, F.C0, F.C1);
    IExpr r = F.C1;
    if (reg.argSize() >= 1 && reg.arg1().isList2()) {
      IAST pts = (IAST) reg.arg1();
      p1 = pts.arg1();
      p2 = pts.arg2();
    }
    if (reg.argSize() >= 2) {
      r = reg.arg2();
    }
    IExpr h = RegionPrimitives.distance(p1, p2, engine);
    if (isCone) {
      // Pi*r*(r + Sqrt(r^2+h^2))
      return engine.evaluate(F.Times(S.Pi, r, F.Plus(r, F.Sqrt(F.Plus(F.Sqr(r), F.Sqr(h))))));
    }
    // 2*Pi*r*h + 2*Pi*r^2
    return engine.evaluate(F.Plus(F.Times(F.C2, S.Pi, r, h), F.Times(F.C2, S.Pi, F.Sqr(r))));
  }

  /** The inner and the outer sphere both contribute: <code>4*Pi*(rInner^2 + rOuter^2)</code>. */
  private IExpr sphericalShell(IAST reg, EvalEngine engine) {
    RegionPrimitives.ShellSpec spec = RegionPrimitives.parseSphericalShell(reg);
    if (spec == null || !spec.numericRadii) {
      // for symbolic radii it is unknown whether the inner radius is 0, in which case the shell
      // degenerates to a ball and the inner sphere doesn't contribute any surface
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.C4, S.Pi, F.Plus(F.Sqr(spec.rInner), F.Sqr(spec.rOuter))));
  }

  /** <code>2*Pi*r*h + 4*Pi*r^2</code> for the cylinder and the two hemispherical caps. */
  private IExpr capsuleShape(IAST reg, EvalEngine engine) {
    RegionPrimitives.CapsuleSpec spec = RegionPrimitives.parseCapsuleShape(reg);
    if (spec == null || RegionPrimitives.capsuleDimension(spec) != 3) {
      return F.NIL;
    }
    IExpr h = RegionPrimitives.distance(spec.p1, spec.p2, engine);
    return engine.evaluate(
        F.Plus(F.Times(F.C2, S.Pi, spec.radius, h), F.Times(F.C4, S.Pi, F.Sqr(spec.radius))));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
