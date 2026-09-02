package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.builtin.RegionPrimitives;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>Perimeter(region)</code> - the arc length of the boundary of a two dimensional region.
 *
 * <p>
 * The perimeter of a region whose {@link RegionDimension} is not two is <code>Undefined</code>.
 * That test is done centrally in {@link #evaluate(IAST, EvalEngine)}, so a head which has no closed
 * form perimeter still answers <code>Undefined</code> rather than staying unevaluated.
 */
public class Perimeter extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    return RegionPrimitives.applyMeasureOptions(measure(ast, argSize, options, engine), options,
        engine);
  }

  /** The perimeter of the region, computed without looking at the options. */
  private static IExpr measure(final IAST ast, int argSize, IExpr[] options,
      EvalEngine engine) {
    // argSize is the number of positional arguments - the trailing options are already stripped
    if (argSize >= 3) {
      return parametricPerimeter(ast, argSize, options, engine);
    }
    if (argSize != 1) {
      return F.NIL;
    }
    IExpr arg1 = ast.arg1();

    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    arg1 = MeshFunctions.normalizeRegion(arg1);

    if (arg1.isAST() && arg1.isBuiltInFunction()) {
      IAST geoForm = (IAST) arg1;
      if (RegionPrimitives.dimensionMatch(geoForm, 2) == RegionPrimitives.DIMENSION_DIFFERS) {
        // the perimeter of a region of dimension zero, one, three or higher is undefined
        return S.Undefined;
      }
      return perimeter(geoForm, engine);
    }
    return F.NIL;
  }

  /**
   * <code>Perimeter({x1,x2}, {s,smin,smax}, {t,tmin,tmax})</code> - the perimeter of a parametrized
   * region, which is the total arc length of the image of the four edges of the parameter
   * rectangle.
   *
   * @return {@link F#NIL} if the arguments are not a coordinate vector and two parameter ranges
   */
  private static IExpr parametricPerimeter(IAST ast, int argSize, IExpr[] options,
      EvalEngine engine) {
    if (argSize > 3) {
      // the coordinate chart variant is not supported
      return F.NIL;
    }
    RegionPrimitives.ParameterRange s = RegionPrimitives.parseParameterRange(ast.arg2());
    RegionPrimitives.ParameterRange t = RegionPrimitives.parseParameterRange(ast.arg3());
    if (s == null || t == null || s.variable.equals(t.variable)) {
      return F.NIL;
    }
    IAST coordinates = RegionPrimitives.surfaceCoordinates(ast.arg1(), s, t);
    if (coordinates.isNIL()) {
      return F.NIL;
    }
    boolean numeric = RegionPrimitives.requestsNumericIntegration(options);
    // the four edges of the parameter rectangle, each traversed once
    IExpr[] edges = new IExpr[] {//
        RegionPrimitives.curveArcLength(coordinates, s, t, t.min, numeric, engine), //
        RegionPrimitives.curveArcLength(coordinates, t, s, s.max, numeric, engine), //
        RegionPrimitives.curveArcLength(coordinates, s, t, t.max, numeric, engine), //
        RegionPrimitives.curveArcLength(coordinates, t, s, s.min, numeric, engine)};
    IASTAppendable sum = F.PlusAlloc(4);
    for (IExpr edge : edges) {
      if (edge.isNIL()) {
        return F.NIL;
      }
      sum.append(edge);
    }
    return engine.evaluate(sum);
  }

  /**
   * The closed form perimeter of a two dimensional region primitive.
   *
   * @return {@link F#NIL} if <code>geoForm</code> is not a supported region
   */
  private static IExpr perimeter(IAST geoForm, EvalEngine engine) {
    int headID = geoForm.headID();
    if (headID < 0) {
      return F.NIL;
    }
    switch (headID) {
      case ID.Disk:
      case ID.Ball:
        // in the plane a ball is a disk
        return disk(geoForm, engine);
      case ID.Rectangle:
      case ID.Cuboid:
        // in the plane a cuboid is a rectangle
        return rectangle(geoForm, engine);
      case ID.Triangle:
        return triangle(geoForm, engine);
      case ID.Polygon:
        return polygon(geoForm, engine);
      case ID.Simplex:
        return simplex(geoForm, engine);
      case ID.Annulus:
        return annulus(geoForm, engine);
      case ID.Ellipsoid:
        return ellipsoid(geoForm, engine);
      case ID.RegularPolygon:
        return regularPolygon(geoForm, engine);
      case ID.DiskSegment:
        return diskSegment(geoForm, engine);
      case ID.StadiumShape:
        return stadiumShape(geoForm, engine);
      case ID.CapsuleShape:
        // in the plane a capsule is a stadium
        return capsuleShape(geoForm, engine);
      case ID.Parallelogram:
        return parallelogram(geoForm, engine);
      case ID.EmptyRegion:
        return RegionPrimitives.emptyRegionMeasure(geoForm);
      case ID.HalfPlane:
      case ID.InfinitePlane:
      case ID.HalfSpace:
      case ID.FullRegion:
        // an unbounded two dimensional region has an infinite perimeter
        return F.CInfinity;
      case ID.Sphere:
      case ID.Torus:
        // A smooth closed surface bounds nothing, so it falls back to the topological boundary in
        // the surrounding space, which for a set with empty interior is the set itself - and the
        // arc length of a two dimensional set is infinite.
        //
        // This is a property of the *closed surface*, not of the embedding dimension: a flat
        // Triangle or Polygon in space is a polytope whose boundary is its combinatorial edge
        // cycle, so it keeps the finite perimeter computed above. Both readings are confirmed
        // against Wolfram, so do not "fix" one of them into the other.
        return F.CInfinity;
    }
    return F.NIL;
  }

  /**
   * The chord <code>2*r*Sin(theta/2)</code> plus the arc <code>r*theta</code>. The perimeter of an
   * elliptical segment has no closed form.
   */
  private static IExpr diskSegment(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.DiskSegmentSpec spec = RegionPrimitives.parseDiskSegment(geoForm, engine);
    if (spec == null || !spec.isCircular()) {
      return F.NIL;
    }
    if (spec.angle.isNegativeResult()) {
      return S.Undefined;
    }
    return engine.evaluate(F.Plus(//
        F.Times(F.C2, spec.rx, F.Sin(F.Times(F.C1D2, spec.angle))), //
        F.Times(spec.rx, spec.angle)));
  }

  /** The two straight sides <code>2*d</code> plus the two semicircular caps <code>2*Pi*r</code>. */
  private static IExpr stadiumShape(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.StadiumSpec spec = RegionPrimitives.parseStadiumShape(geoForm);
    if (spec == null) {
      return F.NIL;
    }
    IExpr d = RegionPrimitives.distance(spec.p1, spec.p2, engine);
    return engine.evaluate(F.Plus(F.Times(F.C2, d), F.Times(F.C2, S.Pi, spec.radius)));
  }

  /** A planar capsule is a stadium: two straight sides plus two semicircular caps. */
  private static IExpr capsuleShape(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.CapsuleSpec spec = RegionPrimitives.parseCapsuleShape(geoForm);
    if (spec == null) {
      return F.NIL;
    }
    IExpr d = RegionPrimitives.distance(spec.p1, spec.p2, engine);
    return engine.evaluate(F.Plus(F.Times(F.C2, d), F.Times(F.C2, S.Pi, spec.radius)));
  }

  /**
   * The boundary of a full annulus is the pair of circles <code>2*Pi*(rInner + rOuter)</code>. The
   * boundary of a sector adds the two radial cuts instead of closing the circles.
   */
  private static IExpr annulus(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.AnnulusSpec spec = RegionPrimitives.parseAnnulus(geoForm, engine);
    if (spec == null) {
      return F.NIL;
    }
    if (spec.isFull()) {
      return engine.evaluate(F.Times(F.C2Pi, F.Plus(spec.innerRadius, spec.outerRadius)));
    }
    // the two arcs plus the two radial segments
    return engine.evaluate(F.Plus(//
        F.Times(spec.angle, F.Plus(spec.innerRadius, spec.outerRadius)), //
        F.Times(F.C2, F.Subtract(spec.outerRadius, spec.innerRadius))));
  }

  /** The two straight sides of the parallelogram, each of them twice. */
  private static IExpr parallelogram(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.ParallelogramSpec spec = RegionPrimitives.parseParallelogram(geoForm);
    if (spec == null) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.C2, //
        F.Plus(F.Norm(spec.vectors.arg1()), F.Norm(spec.vectors.arg2()))));
  }

  /** A two dimensional simplex is the triangle through its three corner points. */
  private static IExpr simplex(IAST geoForm, EvalEngine engine) {
    IAST vertices = RegionPrimitives.verticesOfSimplex(geoForm);
    return vertices.isNIL() ? F.NIL : RegionPrimitives.polygonPerimeter(vertices, engine);
  }

  /** <code>2*n*r*Sin(Pi/n)</code> for the regular <code>n</code>-gon of circumradius r. */
  private static IExpr regularPolygon(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.RegularPolygonSpec spec = RegionPrimitives.parseRegularPolygon(geoForm);
    if (spec == null) {
      return F.NIL;
    }
    return engine.evaluate(
        F.Times(F.C2, spec.n, spec.radius, F.Sin(F.Times(S.Pi, F.Power(spec.n, F.CN1)))));
  }

  private static IExpr disk(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 0) {
      return F.C2Pi;
    }
    if (geoForm.arg1().isList()) {
      IExpr r1 = F.C1;
      IExpr r2 = F.C1;
      if (geoForm.argSize() >= 2) {
        if (geoForm.arg2().isList2()) {
          r1 = geoForm.arg2().first();
          r2 = geoForm.arg2().second();
        } else if (!geoForm.arg2().isList()) {
          r1 = geoForm.arg2();
          r2 = r1;
        } else {
          return F.NIL;
        }
      }

      if (geoForm.argSize() == 3) {
        if (geoForm.arg3().isList2()) {
          IExpr t1 = geoForm.arg3().first();
          IExpr t2 = geoForm.arg3().second();
          return engine.evaluate(
              // [$ (r2*(-EllipticE(t1, 1 - r1^2/r2^2) + EllipticE(t2, 1 - r1^2/r2^2)) +
              // Sqrt(r1^2*Cos(t1)^2 + r2^2*Sin(t1)^2) +
              // Sqrt(r1^2*Cos(t2)^2 + r2^2*Sin(t2)^2))*UnitStep(2*Pi - Abs(-t1 + t2)) +
              // 4*r2*EllipticE(1 - r1^2/r2^2)*UnitStep(-2*Pi + Abs(-t1 + t2)) $]
              F.Plus(
                  F.Times(
                      F.Plus(
                          F.Times(
                              r2, F
                                  .Plus(
                                      F.Negate(F.EllipticE(t1,
                                          F.Plus(F.C1,
                                              F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2))))),
                                      F.EllipticE(t2,
                                          F.Plus(F.C1,
                                              F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2)))))),
                          F.Sqrt(F.Plus(F.Times(F.Sqr(r1), F.Sqr(F.Cos(t1))),
                              F.Times(F.Sqr(r2), F.Sqr(F.Sin(t1))))),
                          F.Sqrt(F.Plus(F.Times(F.Sqr(r1), F.Sqr(F.Cos(t2))),
                              F.Times(F.Sqr(r2), F.Sqr(F.Sin(t2)))))),
                      F.UnitStep(F.Subtract(F.C2Pi, F.Abs(F.Plus(F.Negate(t1), t2))))),
                  F.Times(F.C4, r2,
                      F.EllipticE(F.Plus(F.C1, F.Times(F.CN1, F.Sqr(r1), F.Power(r2, F.CN2)))),
                      F.UnitStep(F.Plus(F.CN2Pi, F.Abs(F.Plus(F.Negate(t1), t2)))))) // $$;
          );
        }
        return F.NIL;
      }
      if (geoForm.argSize() > 3) {
        return F.NIL;
      }

      return engine.evaluate(
          F.Times(F.C4, r2, F.EllipticE(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2))))));
    }
    return F.NIL;
  }

  /** Twice the sum of the side lengths of an axis aligned box. */
  private static IExpr rectangle(IAST geoForm, EvalEngine engine) {
    IAST corners = RegionPrimitives.boxCorners(geoForm);
    if (corners.isNIL()) {
      return F.NIL;
    }
    IAST lower = (IAST) corners.arg1();
    IAST upper = (IAST) corners.arg2();
    return engine.evaluate(F.Times(F.C2, //
        F.Plus(F.Abs(F.Subtract(upper.arg1(), lower.arg1())), //
            F.Abs(F.Subtract(upper.arg2(), lower.arg2())))));
  }

  private static IExpr triangle(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 0) {
      // the default triangle {{0,0},{1,0},{0,1}}
      return F.Plus(F.C2, F.CSqrt2);
    }
    if (geoForm.argSize() == 1 && geoForm.arg1().isList3()) {
      return RegionPrimitives.polygonPerimeter((IAST) geoForm.arg1(), engine);
    }
    return F.NIL;
  }

  private static IExpr polygon(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 1 && geoForm.arg1().isList()) {
      return RegionPrimitives.polygonPerimeter((IAST) geoForm.arg1(), engine);
    }
    return F.NIL;
  }

  private static IExpr ellipsoid(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 2) {
      IExpr center = geoForm.arg1();
      IExpr radii = geoForm.arg2();
      if (center.isList() && radii.isList() && center.argSize() == 2 && radii.argSize() == 2) {
        IExpr r1 = ((IAST) radii).arg1();
        IExpr r2 = ((IAST) radii).arg2();
        IExpr m = engine.evaluate(F.Subtract(F.C1, F.Divide(F.Sqr(r1), F.Sqr(r2))));
        return engine.evaluate(F.Times(F.C4, r2, F.EllipticE(m)));
      }
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, RegionPrimitives.MEASURE_OPTION_KEYS,
        RegionPrimitives.MEASURE_OPTION_DEFAULTS);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // Perimeter(reg) and Perimeter({x1,x2}, {s,smin,smax}, {t,tmin,tmax}, chart)
    return ARGS_1_4;
  }
}
