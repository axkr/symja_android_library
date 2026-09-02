package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.builtin.RegionPrimitives;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class RegionCentroid extends AbstractFunctionOptionEvaluator {

  /**
   * <code>RegionCentroid</code> only accepts <code>Assumptions</code>, so the option array is
   * padded to the layout which {@link RegionPrimitives#applyMeasureOptions} expects.
   */
  private static final IBuiltInSymbol[] OPTION_KEYS = new IBuiltInSymbol[] {S.Assumptions};

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    IExpr[] measureOptions =
        new IExpr[RegionPrimitives.MEASURE_OPTION_KEYS.length];
    if (options != null && options.length > 0) {
      measureOptions[RegionPrimitives.MEASURE_OPTION_ASSUMPTIONS] = options[0];
    }
    return RegionPrimitives.applyMeasureOptions(centroid(ast, engine), measureOptions, engine);
  }

  /** The centroid of the region, computed without looking at the options. */
  private IExpr centroid(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();

    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    arg1 = MeshFunctions.normalizeRegion(arg1);
    if (MeshFunctions.isMeshRegion(arg1)) {
      int embeddingDimension = MeshFunctions.embeddingDimension((IAST) arg1);
      if (embeddingDimension == 3) {
        return MeshFunctions.centroid3D((IAST) arg1, engine);
      }
      if (embeddingDimension == 2) {
        return MeshFunctions.centroid2D((IAST) arg1, engine);
      }
    }

    if (arg1.isAST()) {
      IAST reg = (IAST) arg1;
      IExpr head = reg.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Point:
          case ID.Disk:
          case ID.Circle:
          case ID.Ball:
          case ID.Sphere:
          case ID.Ellipsoid:
          case ID.Annulus:
            return centerCentroid(reg, engine);
          case ID.RegularPolygon:
            return regularPolygonCentroid(reg);
          case ID.Rectangle:
          case ID.Cuboid:
            return boxCentroid(reg, engine);
          case ID.Triangle:
            return meanCentroid(reg, engine);
          case ID.Simplex:
            return simplexCentroid(reg, engine);
          case ID.Cylinder:
            return cylinderCentroid(reg, engine);
          case ID.Cone:
            return coneCentroid(reg, engine);
          case ID.Parallelepiped:
            return parallelepipedCentroid(reg, engine);
          case ID.EmptyRegion:
            // the empty region has no points, so there is no center of mass to report
            return reg.argSize() == 1 && reg.arg1().toIntDefault() > 0 ? F.CEmptyList : F.NIL;
          case ID.Interval:
            return intervalCentroid(reg, engine);
          case ID.Tetrahedron:
            if (RegionPrimitives.isCornerPointsForm(reg)) {
              return meanCentroid(reg, engine);
            }
            return platonicSolidCentroid(reg);
          case ID.Cube:
          case ID.Octahedron:
          case ID.Dodecahedron:
          case ID.Icosahedron:
            return platonicSolidCentroid(reg);
          case ID.Line:
            return lineCentroid(reg, engine);
          case ID.Polygon:
            return polygonCentroid(reg, engine);
          case ID.Torus:
          case ID.FilledTorus: {
            RegionPrimitives.TorusSpec torus = RegionPrimitives.parseTorus(reg, engine);
            return torus == null ? F.NIL : torus.center;
          }
          case ID.SphericalShell: {
            RegionPrimitives.ShellSpec shell = RegionPrimitives.parseSphericalShell(reg);
            return shell == null ? F.NIL : shell.center;
          }
          case ID.CapsuleShape: {
            RegionPrimitives.CapsuleSpec capsule = RegionPrimitives.parseCapsuleShape(reg);
            return capsule == null ? F.NIL
                : RegionPrimitives.midpoint(capsule.p1, capsule.p2, engine);
          }
          case ID.StadiumShape: {
            RegionPrimitives.StadiumSpec stadium = RegionPrimitives.parseStadiumShape(reg);
            return stadium == null ? F.NIL
                : RegionPrimitives.midpoint(stadium.p1, stadium.p2, engine);
          }
          case ID.DiskSegment:
            return diskSegmentCentroid(reg, engine);
          case ID.HalfPlane:
          case ID.InfinitePlane:
          case ID.InfiniteLine:
          case ID.HalfLine:
          case ID.FullRegion:
          case ID.HalfSpace: {
            // an unbounded region has no centroid
            int embeddingDim = RegionEmbeddingDimension.getEmbeddingDimension(reg);
            return embeddingDim < 1 ? F.NIL : RegionPrimitives.indeterminateCentroid(embeddingDim);
          }
          case ID.Parallelogram: {
            RegionPrimitives.ParallelogramSpec spec = RegionPrimitives.parseParallelogram(reg);
            if (spec == null) {
              return F.NIL;
            }
            return engine.evaluate(F.Plus(spec.base,
                F.Times(F.C1D2, F.Plus(spec.vectors.arg1(), spec.vectors.arg2()))));
          }
        }
      }
    }
    return F.NIL;
  }

  private IExpr centerCentroid(IAST reg, EvalEngine engine) {
    IExpr head = reg.head();
    if (head == S.Point) {
      if (reg.argSize() == 1) {
        IExpr p = reg.arg1();
        if (p.isList() && p.argSize() > 0 && ((IAST) p).arg1().isList()) {
          return engine.evaluate(F.Mean(p));
        }
        return p;
      }
    } else {
      if (reg.argSize() == 0) {
        if (head == S.Disk || head == S.Circle || head == S.Annulus) {
          return F.CListC0C0;
        } else {
          return F.List(F.C0, F.C0, F.C0);
        }
      } else if (reg.argSize() >= 1) {
        if (reg.arg1().isList() || reg.arg1().isSymbol()) {
          return reg.arg1();
        }
      }
    }
    return F.NIL;
  }

  private IExpr boxCentroid(IAST reg, EvalEngine engine) {
    IExpr head = reg.head();
    int dim = (head == S.Rectangle) ? 2 : 3;
    if (reg.argSize() == 0) {
      return (dim == 2) ? F.List(F.C1D2, F.C1D2) : F.List(F.C1D2, F.C1D2, F.C1D2);
    } else if (reg.argSize() == 1) {
      if (reg.arg1().isList()) {
        IAST p = (IAST) reg.arg1();
        IASTAppendable res = F.ListAlloc(p.argSize());
        for (int i = 1; i <= p.argSize(); i++) {
          res.append(F.Plus(p.get(i), F.C1D2));
        }
        return engine.evaluate(res);
      }
    } else if (reg.argSize() == 2) {
      if (reg.arg1().isList() && reg.arg2().isList()) {
        return engine.evaluate(F.Times(F.C1D2, F.Plus(reg.arg1(), reg.arg2())));
      }
    }
    return F.NIL;
  }

  /**
   * The centroid of a circular segment lies on the angle bisector, at the distance
   * <code>4*Sin(theta/2)^3 / (3*(theta - Sin(theta)))</code> from the center of the unit disk. For
   * an elliptical segment the offset is scaled by the two semi axes.
   */
  private IExpr diskSegmentCentroid(IAST reg, EvalEngine engine) {
    RegionPrimitives.DiskSegmentSpec spec = RegionPrimitives.parseDiskSegment(reg, engine);
    if (spec == null) {
      return F.NIL;
    }
    if (spec.angle.isNegativeResult()) {
      return S.Undefined;
    }
    IExpr denominator = engine.evaluate(F.Subtract(spec.angle, F.Sin(spec.angle)));
    if (denominator.isZero()) {
      return F.NIL;
    }
    IExpr distance = engine.evaluate(F.Divide(//
        F.Times(F.C4, F.Power(F.Sin(F.Times(F.C1D2, spec.angle)), F.C3)), //
        F.Times(F.C3, denominator)));
    IExpr bisector = engine.evaluate(F.Times(F.C1D2, F.Plus(spec.theta1, spec.theta2)));
    IAST offset = F.List(//
        F.Times(spec.rx, distance, F.Cos(bisector)), //
        F.Times(spec.ry, distance, F.Sin(bisector)));
    return engine.evaluate(F.Plus(spec.center, offset));
  }

  /** The midpoint of the one dimensional <code>Interval({min,max})</code>. */
  private IExpr intervalCentroid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList2()) {
      IAST bounds = (IAST) reg.arg1();
      return F.list(engine.evaluate(F.Times(F.C1D2, F.Plus(bounds.arg1(), bounds.arg2()))));
    }
    return F.NIL;
  }

  /** A regular polygon is centered on its circumcircle. */
  private IExpr regularPolygonCentroid(IAST reg) {
    RegionPrimitives.RegularPolygonSpec spec = RegionPrimitives.parseRegularPolygon(reg);
    return spec == null ? F.NIL : spec.center;
  }

  /** The centroid of a simplex is the mean of its corner points. */
  private IExpr simplexCentroid(IAST reg, EvalEngine engine) {
    IAST vertices = RegionPrimitives.verticesOfSimplex(reg);
    return vertices.isNIL() ? F.NIL : engine.evaluate(F.Mean(vertices));
  }

  /** The centroid of a cylinder is the midpoint of its axis. */
  private IExpr cylinderCentroid(IAST reg, EvalEngine engine) {
    RegionPrimitives.AxisSpec spec = RegionPrimitives.parseAxisRegion(reg);
    return spec == null ? F.NIL : RegionPrimitives.midpoint(spec.base, spec.tip, engine);
  }

  /**
   * The centroid of a solid cone lies on the axis, one quarter of the way from the base disk to the
   * apex: <code>(3*base + tip)/4</code>.
   */
  private IExpr coneCentroid(IAST reg, EvalEngine engine) {
    RegionPrimitives.AxisSpec spec = RegionPrimitives.parseAxisRegion(reg);
    if (spec == null) {
      return F.NIL;
    }
    return engine
        .evaluate(F.Times(F.C1D4, F.Plus(F.Times(F.C3, spec.base), spec.tip)));
  }

  /** The centroid of a parallelepiped is its base point plus half of every spanning vector. */
  private IExpr parallelepipedCentroid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 2 && reg.arg1().isList() && reg.arg2().isListOfLists()) {
      IAST vectors = (IAST) reg.arg2();
      IASTAppendable sum = F.PlusAlloc(vectors.argSize() + 1);
      sum.append(reg.arg1());
      for (int i = 1; i <= vectors.argSize(); i++) {
        sum.append(F.Times(F.C1D2, vectors.get(i)));
      }
      return engine.evaluate(sum);
    }
    return F.NIL;
  }

  private IExpr platonicSolidCentroid(IAST reg) {
    RegionPrimitives.SolidSpec spec = RegionPrimitives.parsePlatonicSolid(reg);
    return spec == null ? F.NIL : spec.center;
  }

  private IExpr meanCentroid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 0) {
      if (reg.head() == S.Triangle) {
        return F.List(F.QQ(1, 3), F.QQ(1, 3));
      }
    } else if (reg.argSize() == 1) {
      if (reg.arg1().isList()) {
        return engine.evaluate(F.Mean(reg.arg1()));
      }
    }
    return F.NIL;
  }

  private IExpr lineCentroid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() == 2) {
        return engine.evaluate(F.Mean(pts));
      } else if (pts.argSize() > 2) {
        // Weighted average for polyline
        IExpr totalLen = F.C0;
        IASTAppendable sum = F.PlusAlloc(pts.argSize());
        sum.append(F.C0);
        for (int i = 1; i < pts.argSize(); i++) {
          IExpr p1 = pts.get(i);
          IExpr p2 = pts.get(i + 1);
          IExpr len = engine.evaluate(F.Norm(F.Subtract(p1, p2)));
          IExpr mid = engine.evaluate(F.Times(F.C1D2, F.Plus(p1, p2)));
          totalLen = engine.evaluate(F.Plus(totalLen, len));
          sum.append(F.Times(len, mid));
        }
        sum.remove(1);
        return engine.evaluate(F.Divide(F.Plus(sum), totalLen));
      }
    }
    return F.NIL;
  }

  private IExpr polygonCentroid(IAST reg, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList()) {
      IAST pts = (IAST) reg.arg1();
      int n = pts.argSize();
      if (n >= 3) {
        // Exact area-weighted formula (Shoelace method) for 2D Polygons
        if (pts.arg1().isList2()) {
          IASTAppendable sum = F.PlusAlloc(n);
          IExpr areaSum = F.C0;
          for (int i = 1; i <= n; i++) {
            IExpr p1 = pts.get(i);
            IExpr p2 = pts.get(i % n + 1);
            IExpr x1 = ((IAST) p1).arg1();
            IExpr y1 = ((IAST) p1).arg2();
            IExpr x2 = ((IAST) p2).arg1();
            IExpr y2 = ((IAST) p2).arg2();
            IExpr cross = engine.evaluate(F.Subtract(F.Times(x1, y2), F.Times(x2, y1)));
            areaSum = engine.evaluate(F.Plus(areaSum, cross));
            sum.append(F.Times(cross, F.Plus(p1, p2)));
          }
          IExpr factor = engine.evaluate(F.Times(F.C3, areaSum));
          if (!factor.isZero()) {
            return engine.evaluate(F.Divide(sum, factor));
          }
        }
        if (pts.arg1().isList3()) {
          return spatialPolygonCentroid(pts, engine);
        }
        // Fallback to mean of vertices
        return engine.evaluate(F.Mean(pts));
      }
    }
    return F.NIL;
  }

  /**
   * The centroid of a planar polygon which is embedded in three dimensions. The polygon is
   * triangulated as a fan around its first vertex and the triangle centroids are weighted by the
   * signed triangle areas, so that a non convex outline is handled as well.
   *
   * <p>
   * The weights are projected onto the un-normalized polygon normal <code>N</code>. Its length is
   * the same factor in every weight and cancels in the final quotient, so <code>N</code> does not
   * have to be normalized.
   */
  private IExpr spatialPolygonCentroid(IAST pts, EvalEngine engine) {
    int n = pts.argSize();
    IExpr normal = F.C0;
    IASTAppendable normalSum = F.PlusAlloc(n);
    for (int i = 1; i <= n; i++) {
      normalSum.append(F.Cross(pts.get(i), pts.get(i % n + 1)));
    }
    normal = engine.evaluate(normalSum);

    IExpr p1 = pts.arg1();
    IASTAppendable weightedSum = F.PlusAlloc(n);
    IASTAppendable weightSum = F.PlusAlloc(n);
    for (int i = 2; i < n; i++) {
      IExpr pi = pts.get(i);
      IExpr pNext = pts.get(i + 1);
      IExpr weight = engine.evaluate(
          F.Dot(F.Cross(F.Subtract(pi, p1), F.Subtract(pNext, p1)), normal));
      weightedSum.append(F.Times(weight, F.Plus(p1, pi, pNext)));
      weightSum.append(weight);
    }
    IExpr denominator = engine.evaluate(F.Times(F.C3, weightSum));
    if (denominator.isZero()) {
      // a degenerate outline which encloses no area
      return F.NIL;
    }
    return engine.evaluate(F.Divide(weightedSum, denominator));
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, OPTION_KEYS, new IExpr[] {S.$Assumptions});
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
