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
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>Area(region)</code> - the two dimensional measure of a region.
 *
 * <p>
 * The area of a region whose {@link RegionDimension} is not two is <code>Undefined</code>. That
 * test is done centrally in {@link #evaluate(IAST, EvalEngine)}, so a head which has no closed form
 * area still answers <code>Undefined</code> rather than staying unevaluated.
 */
public class Area extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    return RegionPrimitives.applyMeasureOptions(measure(ast, argSize, options, engine), options,
        engine);
  }

  /** The area of the region, computed without looking at the options. */
  private static IExpr measure(final IAST ast, int argSize, IExpr[] options,
      EvalEngine engine) {
    // argSize is the number of positional arguments - the trailing options are already stripped
    if (argSize >= 3) {
      return parametricArea(ast, argSize, options, engine);
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
    if (MeshFunctions.isMeshRegion(arg1) && MeshFunctions.embeddingDimension((IAST) arg1) == 2) {
      return MeshFunctions.area2D((IAST) arg1, engine);
    }

    if (arg1.isAST() && arg1.isBuiltInFunction()) {
      IAST geoForm = (IAST) arg1;
      if (RegionPrimitives.dimensionMatch(geoForm, 2) == RegionPrimitives.DIMENSION_DIFFERS) {
        // a region of dimension zero, one, three or higher has no area
        return S.Undefined;
      }
      return area(geoForm, engine);
    }
    return F.NIL;
  }

  /**
   * <code>Area({x1,...,xn}, {s,smin,smax}, {t,tmin,tmax})</code> - the area of a parametrized
   * surface, which is the integral of the area element of the first fundamental form over the
   * parameter rectangle. A part of the surface which the parametrization covers more than once is
   * counted as often as it is covered.
   *
   * @return {@link F#NIL} if the arguments are not a coordinate vector and two parameter ranges
   */
  private static IExpr parametricArea(IAST ast, int argSize, IExpr[] options,
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
    IExpr element = RegionPrimitives.surfaceAreaElement(coordinates, s, t, engine);
    if (element.isNIL()) {
      return F.NIL;
    }
    return RegionPrimitives.integrateOverRectangle(element, s, t,
        RegionPrimitives.requestsNumericIntegration(options), engine);
  }

  /**
   * The closed form area of a two dimensional region primitive.
   *
   * @return {@link F#NIL} if <code>geoForm</code> is not a supported region
   */
  private static IExpr area(IAST geoForm, EvalEngine engine) {
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
        return ellipsoid(geoForm);
      case ID.RegularPolygon:
        return regularPolygon(geoForm, engine);
      case ID.Sphere:
        return sphere(geoForm, engine);
      case ID.Torus:
        return torus(geoForm, engine);
      case ID.DiskSegment:
        return diskSegment(geoForm, engine);
      case ID.StadiumShape:
        return stadiumShape(geoForm, engine);
      case ID.CapsuleShape:
        // in the plane a capsule is a stadium
        return capsuleShape(geoForm, engine);
      case ID.EmptyRegion:
        return RegionPrimitives.emptyRegionMeasure(geoForm);
      case ID.Parallelogram: {
        RegionPrimitives.ParallelogramSpec spec = RegionPrimitives.parseParallelogram(geoForm);
        return spec == null ? F.NIL : RegionPrimitives.parallelogramArea(spec, engine);
      }
      case ID.HalfPlane:
      case ID.InfinitePlane:
      case ID.HalfSpace:
      case ID.FullRegion:
        // an unbounded two dimensional region has infinite area
        return F.CInfinity;
    }
    return F.NIL;
  }

  /**
   * The area <code>rx*ry*(theta - Sin(theta))/2</code> which the chord cuts off the disk. For an
   * angle running backwards the segment is not defined.
   */
  private static IExpr diskSegment(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.DiskSegmentSpec spec = RegionPrimitives.parseDiskSegment(geoForm, engine);
    if (spec == null) {
      return F.NIL;
    }
    if (spec.angle.isNegativeResult()) {
      return S.Undefined;
    }
    return engine.evaluate(F.Times(F.C1D2, spec.rx, spec.ry, //
        F.Subtract(spec.angle, F.Sin(spec.angle))));
  }

  /** The rectangle <code>2*r*d</code> plus the two semicircular caps <code>Pi*r^2</code>. */
  private static IExpr stadiumShape(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.StadiumSpec spec = RegionPrimitives.parseStadiumShape(geoForm);
    if (spec == null) {
      return F.NIL;
    }
    IExpr d = RegionPrimitives.distance(spec.p1, spec.p2, engine);
    return engine.evaluate(F.Plus(//
        F.Times(F.C2, spec.radius, d), //
        F.Times(S.Pi, F.Sqr(spec.radius))));
  }

  /** A planar capsule is a stadium: the rectangle plus the two semicircular caps. */
  private static IExpr capsuleShape(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.CapsuleSpec spec = RegionPrimitives.parseCapsuleShape(geoForm);
    if (spec == null) {
      return F.NIL;
    }
    IExpr d = RegionPrimitives.distance(spec.p1, spec.p2, engine);
    return engine.evaluate(F.Plus(//
        F.Times(F.C2, spec.radius, d), //
        F.Times(S.Pi, F.Sqr(spec.radius))));
  }

  /**
   * The area of the sector of an annulus is <code>theta/2*(rOuter^2 - rInner^2)</code>, which is
   * <code>Pi*(rOuter^2 - rInner^2)</code> for the full annulus.
   */
  private static IExpr annulus(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.AnnulusSpec spec = RegionPrimitives.parseAnnulus(geoForm, engine);
    if (spec == null) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.C1D2, spec.angle, //
        F.Subtract(F.Sqr(spec.outerRadius), F.Sqr(spec.innerRadius))));
  }

  /** The surface of a torus is <code>4*Pi^2*major*minor</code>. */
  private static IExpr torus(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.TorusSpec spec = RegionPrimitives.parseTorus(geoForm, engine);
    if (spec == null) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.C4, F.Sqr(S.Pi), spec.major, spec.minor));
  }

  /** A two dimensional simplex is the triangle through its three corner points. */
  private static IExpr simplex(IAST geoForm, EvalEngine engine) {
    IAST vertices = RegionPrimitives.verticesOfSimplex(geoForm);
    return vertices.isNIL() ? F.NIL : RegionPrimitives.polygonArea(vertices, engine);
  }

  private static IExpr disk(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 0) {
      return S.Pi;
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
          return
          // [$ (r1*r2*Min(Pi, Abs(-t1+t2)/2)) $]
          F.Times(r1, r2, F.Min(S.Pi, F.Times(F.C1D2, F.Abs(F.Plus(F.Negate(t1), t2))))); // $$;
        }
        return F.NIL;
      }
      if (geoForm.argSize() > 3) {
        return F.NIL;
      }
      return F.Times(S.Pi, r1, r2);
    }
    return F.NIL;
  }

  /** The product of the side lengths of an axis aligned box. */
  private static IExpr rectangle(IAST geoForm, EvalEngine engine) {
    IAST corners = RegionPrimitives.boxCorners(geoForm);
    if (corners.isNIL()) {
      return F.NIL;
    }
    IAST lower = (IAST) corners.arg1();
    IAST upper = (IAST) corners.arg2();
    return engine.evaluate(F.Abs(F.Times(F.Subtract(upper.arg1(), lower.arg1()),
        F.Subtract(upper.arg2(), lower.arg2()))));
  }

  private static IExpr triangle(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 0) {
      // the default triangle {{0,0},{1,0},{0,1}}
      return F.C1D2;
    }
    if (geoForm.argSize() == 1 && geoForm.arg1().isList3()) {
      return RegionPrimitives.polygonArea((IAST) geoForm.arg1(), engine);
    }
    return F.NIL;
  }

  private static IExpr polygon(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() == 1 && geoForm.arg1().isList()) {
      return RegionPrimitives.polygonArea((IAST) geoForm.arg1(), engine);
    }
    return F.NIL;
  }

  private static IExpr ellipsoid(IAST geoForm) {
    if (geoForm.argSize() == 2) {
      IExpr center = geoForm.arg1();
      IExpr radii = geoForm.arg2();
      if (center.isList() && radii.isList() && center.argSize() == radii.argSize()
          && center.argSize() == 2) {
        return F.Times(S.Pi, ((IAST) radii).arg1(), ((IAST) radii).arg2());
      }
    }
    return F.NIL;
  }

  /** <code>n/2 * Sin(2*Pi/n) * r^2</code> for the regular <code>n</code>-gon of circumradius r. */
  private static IExpr regularPolygon(IAST geoForm, EvalEngine engine) {
    RegionPrimitives.RegularPolygonSpec spec = RegionPrimitives.parseRegularPolygon(geoForm);
    if (spec == null) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.C1D2, spec.n, //
        F.Sin(F.Times(F.C2Pi, F.Power(spec.n, F.CN1))), F.Sqr(spec.radius)));
  }

  /** The surface <code>4*Pi*r^2</code> of the two dimensional sphere in three dimensions. */
  private static IExpr sphere(IAST geoForm, EvalEngine engine) {
    IExpr r = geoForm.argSize() >= 2 ? geoForm.arg2() : F.C1;
    return engine.evaluate(F.Times(F.C4, S.Pi, F.Sqr(r)));
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
    // Area(reg) and Area({x1,...,xn}, {s,smin,smax}, {t,tmin,tmax}, chart)
    return ARGS_1_4;
  }
}
