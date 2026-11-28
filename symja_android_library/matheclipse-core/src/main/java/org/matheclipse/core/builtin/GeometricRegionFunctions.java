package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.RegionDimension;

/**
 * Geometric constructions and region functions: <code>Hyperplane</code>,
 * <code>ConicHullRegion</code>, <code>TransformedRegion</code>, <code>Midpoint</code>,
 * <code>PerpendicularBisector</code>, <code>DihedralAngle</code>, <code>SolidAngle</code>,
 * <code>CircumscribedBall</code>, <code>RegionConvert</code>, <code>RegionDisjoint</code>,
 * <code>ConvexPolygonQ</code>, <code>SimplePolygonQ</code>, <code>ConnectedMeshComponents</code>,
 * <code>TriangulateMesh</code>, <code>CoordinateTransform</code> and
 * <code>TransformedField</code>.
 *
 * <p>
 * <code>Hyperplane</code>, <code>ConicHullRegion</code> and a <code>TransformedRegion</code> that
 * stays unevaluated are rewritten into regions the other region functions know by
 * {@link #primitiveForm(IAST, EvalEngine)}.
 */
public final class GeometricRegionFunctions {

  /** The most point subsets {@link CircumscribedBall} tries with exact arithmetic. */
  private static final int MAX_EXACT_SUBSETS = 20000;

  /** The most point subsets {@link CircumscribedBall} tries with machine numbers. */
  private static final long MAX_NUMERIC_SUBSETS = 2000000L;

  /** The most triangles {@link TriangulateMesh} creates when it refines a mesh. */
  private static final int MAX_TRIANGLES = 50000;

  private static class Initializer {

    private static void init() {
      S.Hyperplane.setEvaluator(new Hyperplane());
      S.ConicHullRegion.setEvaluator(new ConicHullRegion());
      S.TransformedRegion.setEvaluator(new TransformedRegion());
      S.Midpoint.setEvaluator(new Midpoint());
      S.PerpendicularBisector.setEvaluator(new PerpendicularBisector());
      S.DihedralAngle.setEvaluator(new DihedralAngle());
      S.SolidAngle.setEvaluator(new SolidAngle());
      S.CircumscribedBall.setEvaluator(new CircumscribedBall());
      S.RegionConvert.setEvaluator(new RegionConvert());
      S.RegionDisjoint.setEvaluator(new RegionDisjoint());
      S.ConvexPolygonQ.setEvaluator(new PolygonTest(true));
      S.SimplePolygonQ.setEvaluator(new PolygonTest(false));
      S.ConnectedMeshComponents.setEvaluator(new ConnectedMeshComponents());
      S.TriangulateMesh.setEvaluator(new TriangulateMesh());
      S.CoordinateTransform.setEvaluator(new CoordinateTransform());
      S.TransformedField.setEvaluator(new TransformedField());
    }
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  /** The formal variables <code>{x, y, z}</code> for a region in up to 3 dimensions. */
  private static IAST formalVariables(int n) {
    ISymbol[] formal = {S.x, S.y, S.z};
    if (n >= 1 && n <= 3) {
      return F.mapRange(0, n, i -> formal[i]);
    }
    return F.mapRange(0, n, i -> F.Dummy("x" + (i + 1)));
  }

  /** The two points of <code>{p, q}</code> or <code>Line({p, q})</code>, or <code>null</code>. */
  private static IAST segment(IExpr arg) {
    IExpr points = arg.isAST(S.Line, 2) ? arg.first() : arg;
    int[] dims = points.isMatrix(false);
    if (dims == null || dims[0] != 2 || dims[1] < 1) {
      return null;
    }
    return (IAST) points.normal(false);
  }

  private static boolean isZeroVector(IExpr v, EvalEngine engine) {
    IExpr test = engine.evaluate(F.Dot(v, v));
    return test.isZero();
  }

  /** The zero vector of length <code>n</code>. */
  private static IAST zeros(int n) {
    return F.constantArray(F.C0, n);
  }

  /**
   * The vertices of a polygon <code>Polygon({p1, p2, ...})</code>, of the index form
   * <code>Polygon({p1, ...}, {i1, ...})</code> (also with the one index cycle
   * <code>{{i1, ...}}</code>) or of a <code>Triangle({p1, p2, p3})</code>.
   */
  private static @AllowNIL IAST polygonVertices(IExpr region) {
    IExpr polygon = region;
    if (region.isAST(S.Polygon, 3)) {
      IExpr indices = region.second();
      if (indices.isList() && indices.argSize() == 1 && indices.first().isList()) {
        polygon = F.binaryAST2(S.Polygon, region.first(), indices.first());
      }
      polygon = MeshFunctions.normalizeRegion(polygon);
    }
    if ((polygon.isAST(S.Polygon, 2) || polygon.isAST(S.Triangle, 2))
        && polygon.first().isListOfLists()) {
      IAST points = (IAST) polygon.first().normal(false);
      if (points.argSize() > 0 && points.arg1().isVector() > 0
          && points.isMatrix(false) != null) {
        if (polygon.isAST(S.Triangle) && points.argSize() != 3) {
          return F.NIL;
        }
        return points;
      }
    }
    return F.NIL;
  }

  // ---------------------------------------------------------------------------------------------
  // affine transformations
  // ---------------------------------------------------------------------------------------------

  /** An affine map <code>x -> a.x + b</code> taken from a <code>TransformationFunction</code>. */
  private static final class Affine {
    final IAST a;
    final IAST b;
    final int n;

    Affine(IAST a, IAST b, int n) {
      this.a = a;
      this.b = b;
      this.n = n;
    }

    IExpr apply(IExpr p, EvalEngine engine) {
      return engine.evaluate(F.Plus(F.Dot(a, p), b));
    }

    IExpr linear(IExpr v, EvalEngine engine) {
      return engine.evaluate(F.Dot(a, v));
    }

    boolean isDiagonal() {
      for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= n; j++) {
          if (i != j && !a.getPart(i, j).isZero()) {
            return false;
          }
        }
      }
      return true;
    }

    /**
     * Test if every row and every column of the linear part has exactly one entry which isn't
     * <code>0</code>: the map sends axis parallel boxes to axis parallel boxes.
     */
    boolean isMonomial() {
      for (int i = 1; i <= n; i++) {
        int rowCount = 0;
        int columnCount = 0;
        for (int j = 1; j <= n; j++) {
          if (!a.getPart(i, j).isZero()) {
            rowCount++;
          }
          if (!a.getPart(j, i).isZero()) {
            columnCount++;
          }
        }
        if (rowCount != 1 || columnCount != 1) {
          return false;
        }
      }
      return true;
    }

    /** The determinant of the linear part, evaluated. */
    IExpr det(EvalEngine engine) {
      return engine.evaluate(F.Det(a));
    }
  }

  /**
   * The affine map of <code>TransformationFunction(m)</code> if the last row of the homogeneous
   * matrix <code>m</code> is <code>{0, ..., 0, 1}</code>.
   */
  private static Affine affine(IExpr f) {
    if (!f.isAST(S.TransformationFunction, 2)) {
      return null;
    }
    int[] dims = f.first().isMatrix(false);
    if (dims == null || dims[0] != dims[1] || dims[0] < 2) {
      return null;
    }
    IAST m = (IAST) f.first().normal(false);
    int n = dims[0] - 1;
    IAST lastRow = (IAST) m.get(n + 1);
    for (int j = 1; j <= n; j++) {
      if (!lastRow.get(j).isZero()) {
        return null;
      }
    }
    if (!lastRow.get(n + 1).isOne()) {
      return null;
    }
    IASTAppendable a = F.ListAlloc(n);
    IASTAppendable b = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      IAST row = (IAST) m.get(i);
      a.append(row.copyFrom(1, n + 1));
      b.append(row.get(n + 1));
    }
    return new Affine(a, b, n);
  }

  /** Map a point or a (nested) list of points of dimension <code>n</code>. */
  private static @AllowNIL IExpr mapPoints(IExpr e, Affine t, EvalEngine engine) {
    if (e.isVector() == t.n) {
      return t.apply(e.normal(false), engine);
    }
    if (e.isList() && e.argSize() > 0) {
      IAST list = (IAST) e;
      IASTAppendable result = F.ListAlloc(list.argSize());
      for (IExpr element : list) {
        IExpr mapped = mapPoints(element, t, engine);
        if (mapped.isNIL()) {
          return F.NIL;
        }
        result.append(mapped);
      }
      return result;
    }
    return F.NIL;
  }

  /** Map a list of direction vectors of dimension <code>n</code> by the linear part. */
  private static @AllowNIL IExpr mapVectors(IExpr e, Affine t, EvalEngine engine) {
    if (!e.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) e;
    IASTAppendable result = F.ListAlloc(list.argSize());
    for (IExpr v : list) {
      if (v.isVector() != t.n) {
        return F.NIL;
      }
      result.append(t.linear(v, engine));
    }
    return result;
  }

  /**
   * The image of a region under an affine map as a region of the same kind, or {@link F#NIL} if
   * the image isn't such a region.
   */
  private static @AllowNIL IExpr transformRegion(IAST reg, Affine t, EvalEngine engine) {
    if (!reg.head().isBuiltInSymbol()) {
      return F.NIL;
    }
    int n = t.n;
    IBuiltInSymbol head = (IBuiltInSymbol) reg.head();
    switch (head.ordinal()) {
      case ID.Point:
      case ID.Line:
      case ID.Polygon:
      case ID.Triangle:
      case ID.Simplex:
      case ID.Tetrahedron:
      case ID.Hexahedron:
      case ID.Prism:
      case ID.Pyramid: {
        if (reg.argSize() < 1) {
          return F.NIL;
        }
        IExpr points = mapPoints(reg.arg1(), t, engine);
        return points.isPresent() ? reg.setAtCopy(1, points) : F.NIL;
      }
      case ID.InfiniteLine:
      case ID.HalfLine:
        if (reg.argSize() == 1) {
          IExpr points = mapPoints(reg.arg1(), t, engine);
          return points.isPresent() ? reg.setAtCopy(1, points) : F.NIL;
        }
        if (reg.argSize() == 2 && reg.arg1().isVector() == n && reg.arg2().isVector() == n) {
          return F.binaryAST2(head, t.apply(reg.arg1(), engine), t.linear(reg.arg2(), engine));
        }
        return F.NIL;
      case ID.InfinitePlane:
      case ID.Parallelogram:
      case ID.Parallelepiped:
        if (reg.argSize() == 1 && head == S.InfinitePlane) {
          IExpr points = mapPoints(reg.arg1(), t, engine);
          return points.isPresent() ? reg.setAtCopy(1, points) : F.NIL;
        }
        if (reg.argSize() == 2 && reg.arg1().isVector() == n) {
          IExpr vectors = mapVectors(reg.arg2(), t, engine);
          return vectors.isPresent() ? F.binaryAST2(head, t.apply(reg.arg1(), engine), vectors)
              : F.NIL;
        }
        return F.NIL;
      case ID.Rectangle:
      case ID.Cuboid:
        return transformBox(reg, t, engine);
      case ID.Disk:
      case ID.Ball:
      case ID.Circle:
      case ID.Sphere:
        return transformBall(reg, t, engine);
      case ID.Ellipsoid:
        if (reg.argSize() == 2 && reg.arg1().isVector() == n && reg.arg2().isVector() == n
            && t.isDiagonal()) {
          IAST radii = (IAST) reg.arg2().normal(false);
          return F.binaryAST2(S.Ellipsoid, t.apply(reg.arg1(), engine),
              engine.evaluate(F.mapRange(1, n + 1,
                  i -> F.Times(F.Abs(t.a.getPart(i, i)), radii.get(i)))));
        }
        return F.NIL;
      case ID.HalfSpace:
      case ID.Hyperplane:
        return transformHalfSpace(reg, t, engine);
      default:
        return F.NIL;
    }
  }

  /**
   * <code>Rectangle</code> and <code>Cuboid</code>: a box again under axis scalings, axis
   * permutations and reflections (like a rotation by a multiple of <code>Pi/2</code>), otherwise a
   * parallelogram.
   */
  private static @AllowNIL IExpr transformBox(IAST reg, Affine t, EvalEngine engine) {
    int n = t.n;
    if ((reg.isAST(S.Rectangle) && n != 2) || reg.argSize() > 2) {
      return F.NIL;
    }
    IExpr lo = reg.argSize() >= 1 ? reg.arg1() : zeros(n);
    if (lo.isVector() != n) {
      return F.NIL;
    }
    lo = lo.normal(false);
    IExpr hi = reg.argSize() == 2 ? reg.arg2() : engine.evaluate(F.Plus(lo, F.C1));
    if (hi.isVector() != n) {
      return F.NIL;
    }
    hi = hi.normal(false);
    IExpr p = t.apply(lo, engine);
    IExpr q = t.apply(hi, engine);
    if (t.isMonomial()) {
      // the images of two opposite corners span the image box
      IAST pl = (IAST) p;
      IAST ql = (IAST) q;
      IAST min = (IAST) engine
          .evaluate(F.mapRange(1, n + 1, i -> F.Min(pl.get(i), ql.get(i))));
      IAST max = (IAST) engine
          .evaluate(F.mapRange(1, n + 1, i -> F.Max(pl.get(i), ql.get(i))));
      return F.binaryAST2(reg.head(), min, max);
    }
    final IAST loList = (IAST) lo;
    final IAST hiList = (IAST) hi;
    IASTAppendable edges = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      final int column = i;
      IExpr edge = F.mapRange(1, n + 1,
          j -> F.Times(t.a.getPart(j, column), F.Subtract(hiList.get(column), loList.get(column))));
      edges.append(engine.evaluate(edge));
    }
    return F.binaryAST2(n == 2 ? S.Parallelogram : S.Parallelepiped, p, edges);
  }

  /** Disks, balls, circles and spheres under a similarity or an axis scaling. */
  private static @AllowNIL IExpr transformBall(IAST reg, Affine t, EvalEngine engine) {
    int n = t.n;
    if (reg.argSize() > 2) {
      return F.NIL;
    }
    IExpr center = reg.argSize() >= 1 ? reg.arg1() : zeros(n);
    if (center.isVector() != n) {
      return F.NIL;
    }
    IExpr radius = reg.argSize() == 2 ? reg.arg2() : F.C1;
    IExpr c = t.apply(center.normal(false), engine);
    boolean planar = reg.isAST(S.Disk) || reg.isAST(S.Circle);
    if (radius.isList()) {
      if (planar && radius.isVector() == n && t.isDiagonal()) {
        IAST radii = (IAST) radius.normal(false);
        return F.binaryAST2(reg.head(), c, engine.evaluate(
            F.mapRange(1, n + 1, i -> F.Times(F.Abs(t.a.getPart(i, i)), radii.get(i)))));
      }
      return F.NIL;
    }
    IExpr scale = similarityScale(t, engine);
    if (scale.isPresent()) {
      return F.binaryAST2(reg.head(), c, engine.evaluate(F.Times(scale, radius)));
    }
    if (t.isDiagonal() && (planar || reg.isAST(S.Ball))) {
      IAST radii = (IAST) engine
          .evaluate(F.mapRange(1, n + 1, i -> F.Times(F.Abs(t.a.getPart(i, i)), radius)));
      return F.binaryAST2(planar ? reg.head() : S.Ellipsoid, c, radii);
    }
    return F.NIL;
  }

  /**
   * The factor <code>s</code> if the linear part is <code>s</code> times an orthogonal matrix.
   */
  private static @AllowNIL IExpr similarityScale(Affine t, EvalEngine engine) {
    IExpr ata = engine.evaluate(F.Simplify(F.Dot(F.Transpose(t.a), t.a)));
    if (!ata.isList()) {
      return F.NIL;
    }
    IExpr s2 = ((IAST) ata).getPart(1, 1);
    for (int i = 1; i <= t.n; i++) {
      for (int j = 1; j <= t.n; j++) {
        IExpr entry = ((IAST) ata).getPart(i, j);
        IExpr expected = i == j ? s2 : F.C0;
        if (!engine.evaluate(F.PossibleZeroQ(F.Subtract(entry, expected))).isTrue()) {
          return F.NIL;
        }
      }
    }
    IExpr scale = engine.evaluate(F.Sqrt(s2));
    return scale.isZero() ? F.NIL : scale;
  }

  /** <code>HalfSpace(n, c)</code>, <code>Hyperplane(n, c)</code> and their point forms. */
  private static @AllowNIL IExpr transformHalfSpace(IAST reg, Affine t, EvalEngine engine) {
    int n = t.n;
    if (reg.argSize() < 1 || reg.argSize() > 2 || reg.arg1().isVector() != n) {
      return F.NIL;
    }
    IExpr det = t.det(engine);
    if (det.isZero() || !det.isNumericFunction(true)) {
      return F.NIL;
    }
    IExpr normal = engine.evaluate(F.Dot(F.Transpose(F.Inverse(t.a)), reg.arg1()));
    if (reg.argSize() == 1) {
      return F.binaryAST2(reg.head(), normal, engine.evaluate(F.Dot(normal, t.b)));
    }
    if (reg.arg2().isVector() == n) {
      return F.binaryAST2(reg.head(), normal, t.apply(reg.arg2().normal(false), engine));
    }
    if (reg.arg2().isList()) {
      return F.NIL;
    }
    // the image of the boundary point c*n/(n.n) on the normal through the origin
    IExpr nn = engine.evaluate(F.Dot(reg.arg1(), reg.arg1()));
    if (nn.isZero()) {
      return F.NIL;
    }
    IExpr point = engine.evaluate(F.Times(F.Divide(reg.arg2(), nn), reg.arg1()));
    return F.binaryAST2(reg.head(), normal, t.apply(point, engine));
  }

  // ---------------------------------------------------------------------------------------------
  // primitive forms for the region functions
  // ---------------------------------------------------------------------------------------------

  /**
   * A region the region functions know for <code>Hyperplane</code>, <code>ConicHullRegion</code>
   * and an unevaluated <code>TransformedRegion</code>.
   *
   * @return {@link F#NIL} if <code>region</code> is none of these or has no such form
   */
  public static @AllowNIL IExpr primitiveForm(IAST region, EvalEngine engine) {
    if (region.isAST(S.Hyperplane)) {
      return hyperplaneForm(region, engine);
    }
    if (region.isAST(S.ConicHullRegion)) {
      return conicHullForm(region, engine);
    }
    if (region.isAST(S.TransformedRegion, 3)) {
      return transformedForm(region, engine);
    }
    return F.NIL;
  }

  /**
   * <code>Hyperplane(n, c)</code> is a point on the line, an <code>InfiniteLine</code> in the
   * plane, an <code>InfinitePlane</code> in space and an <code>ImplicitRegion</code> above.
   */
  private static @AllowNIL IExpr hyperplaneForm(IAST h, EvalEngine engine) {
    if (h.argSize() < 1 || h.argSize() > 2) {
      return F.NIL;
    }
    int n = h.arg1().isVector();
    if (n <= 0) {
      return F.NIL;
    }
    IAST normal = (IAST) h.arg1().normal(false);
    IExpr offset;
    if (h.argSize() == 1) {
      offset = F.C0;
    } else if (h.arg2().isVector() == n) {
      offset = engine.evaluate(F.Dot(normal, h.arg2()));
    } else if (h.arg2().isList()) {
      return F.NIL;
    } else {
      offset = h.arg2();
    }
    IExpr nn = engine.evaluate(F.Dot(normal, normal));
    if (nn.isZero()) {
      return F.NIL;
    }
    IExpr point = engine.evaluate(F.Times(F.Divide(offset, nn), normal));
    switch (n) {
      case 1:
        return F.Point(point);
      case 2:
        return F.binaryAST2(S.InfiniteLine, point,
            engine.evaluate(F.List(F.Negate(normal.arg2()), normal.arg1())));
      case 3: {
        int axis = 1;
        for (int i = 1; i <= 3; i++) {
          if (normal.get(i).isZero()) {
            axis = i;
            break;
          }
        }
        final int a = axis;
        IAST unit = F.mapRange(1, 4, i -> i == a ? F.C1 : F.C0);
        IExpr u = engine.evaluate(F.Cross(normal, unit));
        IExpr v = engine.evaluate(F.Cross(normal, u));
        return F.binaryAST2(S.InfinitePlane, point, F.List(u, v));
      }
      default: {
        IAST vars = formalVariables(n);
        return F.binaryAST2(S.ImplicitRegion, F.Equal(F.Dot(normal, vars), offset), vars);
      }
    }
  }

  /**
   * The affine hull of the points: a point, an <code>InfiniteLine</code>, an
   * <code>InfinitePlane</code> in space or the <code>FullRegion</code>.
   *
   * @return {@link F#NIL} if the rank of the points isn't decidable or the hull has none of these
   *         forms
   */
  private static @AllowNIL IExpr affineHull(IExpr pointList, EvalEngine engine) {
    int[] dims = pointList.isMatrix(false);
    if (dims == null || dims[0] < 1) {
      return F.NIL;
    }
    IAST points = (IAST) pointList.normal(false);
    if (!points.isNumericFunction(true)) {
      // the rank of symbolic points depends on the values of the symbols
      return F.NIL;
    }
    int n = dims[1];
    IExpr p = points.arg1();
    if (points.argSize() == 1) {
      return F.Point(p);
    }
    IAST directions = (IAST) engine
        .evaluate(F.mapRange(2, points.size(), i -> F.Subtract(points.get(i), points.arg1())));
    int rank = engine.evaluate(F.unaryAST1(S.MatrixRank, directions)).toIntDefault();
    if (rank < 0) {
      return F.NIL;
    }
    if (rank == 0) {
      return F.Point(p);
    }
    if (rank == n) {
      return F.unaryAST1(S.FullRegion, F.ZZ(n));
    }
    // independent directions, in the order of the points
    IASTAppendable basis = F.ListAlloc(rank);
    for (IExpr direction : directions) {
      IASTAppendable candidate = basis.copyAppendable();
      candidate.append(direction);
      int r = engine.evaluate(F.unaryAST1(S.MatrixRank, candidate)).toIntDefault();
      if (r == candidate.argSize()) {
        basis = candidate;
        if (r == rank) {
          break;
        }
      }
    }
    if (rank == 1) {
      return F.binaryAST2(S.InfiniteLine, p, basis.arg1());
    }
    if (rank == 2 && n == 3) {
      return F.binaryAST2(S.InfinitePlane, p, basis);
    }
    return F.NIL;
  }

  /**
   * <code>ConicHullRegion({p}, {v1, ...})</code>: a point, a <code>HalfLine</code>, or for as many
   * independent generators as dimensions the <code>ImplicitRegion</code> of the nonnegative
   * coefficients. <code>ConicHullRegion({p1, p2, ...})</code> is the affine hull of the points.
   */
  private static @AllowNIL IExpr conicHullForm(IAST c, EvalEngine engine) {
    IExpr apex;
    IAST generators;
    int n;
    if (c.argSize() == 1) {
      return affineHull(c.arg1(), engine);
    } else if (c.argSize() == 2) {
      int[] dims = c.arg1().isMatrix(false);
      if (dims == null || dims[0] != 1) {
        return F.NIL;
      }
      n = dims[1];
      apex = ((IAST) c.arg1().normal(false)).arg1();
      if (!c.arg2().isList()) {
        return F.NIL;
      }
      if (c.arg2().argSize() > 0) {
        int[] vdims = c.arg2().isMatrix(false);
        if (vdims == null || vdims[1] != n) {
          return F.NIL;
        }
      }
      generators = (IAST) c.arg2().normal(false);
    } else {
      return F.NIL;
    }
    int k = generators.argSize();
    if (k == 0) {
      return F.Point(apex);
    }
    if (k == 1) {
      return F.binaryAST2(S.HalfLine, apex, generators.arg1());
    }
    if (k == n) {
      IExpr det = engine.evaluate(F.Det(generators));
      if (det.isZero() || !det.isNumericFunction(true)) {
        return F.NIL;
      }
      IAST vars = formalVariables(n);
      IExpr coefficients = engine.evaluate(
          F.Dot(F.Inverse(F.Transpose(generators)), F.Subtract(vars, apex)));
      if (!coefficients.isList()) {
        return F.NIL;
      }
      IAST coefficientList = (IAST) coefficients;
      IASTAppendable and = F.ast(S.And, k);
      for (IExpr coefficient : coefficientList) {
        and.append(F.GreaterEqual(coefficient, F.C0));
      }
      return F.binaryAST2(S.ImplicitRegion, and, vars);
    }
    return F.NIL;
  }

  /**
   * An unevaluated <code>TransformedRegion(reg, f)</code> with an invertible affine map is the
   * <code>ImplicitRegion</code> of the points whose preimage lies in <code>reg</code>.
   */
  private static @AllowNIL IExpr transformedForm(IAST tr, EvalEngine engine) {
    Affine t = affine(tr.arg2());
    if (t == null) {
      return F.NIL;
    }
    IExpr det = t.det(engine);
    if (det.isZero() || !det.isNumericFunction(true)) {
      return F.NIL;
    }
    IAST vars = formalVariables(t.n);
    IExpr preimage = engine.evaluate(F.Dot(F.Inverse(t.a), F.Subtract(vars, t.b)));
    IExpr condition = engine.evaluate(F.RegionMember(tr.arg1(), preimage));
    if (condition.isAST(S.RegionMember) || condition.has(S.RegionMember, true)) {
      return F.NIL;
    }
    return F.binaryAST2(S.ImplicitRegion, condition, vars);
  }

  /**
   * The measure of <code>TransformedRegion(reg, f)</code> for an affine map <code>f</code> and a
   * full dimensional region: the measure of <code>reg</code> times the absolute value of the
   * determinant.
   *
   * @return {@link F#NIL} if the region isn't such a transformed region
   */
  public static @AllowNIL IExpr transformedMeasure(IExpr region, EvalEngine engine) {
    if (!region.isAST(S.TransformedRegion, 3)) {
      return F.NIL;
    }
    Affine t = affine(region.second());
    if (t == null) {
      return F.NIL;
    }
    IExpr inner = region.first();
    int dimension = RegionDimension.getRegionDimension(
        org.matheclipse.core.eval.util.RegionPrimitives.unwrapRegion(inner));
    if (dimension != t.n) {
      return F.NIL;
    }
    IExpr measure = engine.evaluate(F.unaryAST1(S.RegionMeasure, inner));
    if (measure.isAST(S.RegionMeasure)) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(measure, F.Abs(t.det(engine))));
  }

  // ---------------------------------------------------------------------------------------------
  // evaluators
  // ---------------------------------------------------------------------------------------------

  /** <code>Hyperplane(n)</code> is <code>Hyperplane(n, 0)</code>. */
  private static final class Hyperplane extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() && ast.arg1().isVector() > 0) {
        return F.binaryAST2(S.Hyperplane, ast.arg1(), F.C0);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>ConicHullRegion({p1, p2, ...})</code> whose affine hull is the whole space is
   * <code>FullRegion(n)</code>; otherwise the region stays as it is.
   */
  private static final class ConicHullRegion extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr hull = affineHull(ast.arg1(), engine);
        if (hull.isAST(S.FullRegion, 2)) {
          return hull;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>TransformedRegion(reg, f)</code> - the image of <code>reg</code> under <code>f</code>.
   * An affine <code>TransformationFunction</code> maps point based regions to regions of the same
   * kind, boxes to boxes or parallelograms, balls under similarities to balls; two nested affine
   * transformations are combined.
   */
  private static final class TransformedRegion extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr reg = ast.arg1();
      IExpr f = ast.arg2();
      if (reg.isAST(S.Region, 2)) {
        reg = reg.first();
      }
      Affine outer = affine(f);
      Affine inner = reg.isAST(S.TransformedRegion, 3) ? affine(reg.second()) : null;
      if (outer != null && inner != null && outer.n == inner.n) {
        IExpr m = engine.evaluate(F.Dot(f.first(), reg.second().first()));
        return F.binaryAST2(S.TransformedRegion, reg.first(),
            F.unaryAST1(S.TransformationFunction, m));
      }
      Affine t = affine(f);
      if (t == null || !reg.isAST()) {
        return F.NIL;
      }
      return transformRegion((IAST) reg, t, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /** <code>Midpoint({p, q})</code> and <code>Midpoint(Line({p, q}))</code>. */
  private static final class Midpoint extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST points = segment(ast.arg1());
      if (points == null) {
        return F.NIL;
      }
      return engine.evaluate(F.Times(F.C1D2, F.Plus(points.arg1(), points.arg2())));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>PerpendicularBisector({p, q})</code> - in the plane the <code>InfiniteLine</code> through
   * the midpoint with the direction <code>q - p</code> turned clockwise, in higher dimensions the
   * <code>Hyperplane</code> with the normal <code>q - p</code> through the midpoint.
   */
  private static final class PerpendicularBisector extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST points = segment(ast.arg1());
      if (points == null || points.arg1().argSize() < 2) {
        return F.NIL;
      }
      IExpr mid = engine.evaluate(F.Times(F.C1D2, F.Plus(points.arg1(), points.arg2())));
      IExpr d = engine.evaluate(F.Subtract(points.arg2(), points.arg1()));
      if (isZeroVector(d, engine)) {
        return F.NIL;
      }
      if (d.argSize() == 2) {
        return F.binaryAST2(S.InfiniteLine, mid,
            engine.evaluate(F.List(d.second(), F.Negate(d.first()))));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>DihedralAngle({p1, p2}, {u, v})</code> - the angle between the half-planes bounded by the
   * line through <code>p1</code> and <code>p2</code> in the directions <code>u</code> and
   * <code>v</code>: the angle between the components of <code>u</code> and <code>v</code>
   * perpendicular to the edge.
   */
  private static final class DihedralAngle extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST edge = segment(ast.arg1());
      IAST directions = segment(ast.arg2());
      if (edge == null || directions == null) {
        return F.NIL;
      }
      int n = edge.arg1().argSize();
      if (directions.arg1().argSize() != n || n < 2) {
        return F.NIL;
      }
      IExpr e = engine.evaluate(F.Subtract(edge.arg2(), edge.arg1()));
      if (isZeroVector(e, engine)) {
        return F.NIL;
      }
      IExpr ee = engine.evaluate(F.Dot(e, e));
      IExpr u = engine.evaluate(F.Subtract(directions.arg1(),
          F.Times(F.Divide(F.Dot(directions.arg1(), e), ee), e)));
      IExpr v = engine.evaluate(F.Subtract(directions.arg2(),
          F.Times(F.Divide(F.Dot(directions.arg2(), e), ee), e)));
      if (isZeroVector(u, engine) || isZeroVector(v, engine)) {
        return F.NIL;
      }
      return engine.evaluate(F.binaryAST2(S.VectorAngle, u, v));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>SolidAngle(p, {q1, ..., qn})</code> - the solid angle at <code>p</code> of the cone
   * spanned by the vectors to the points <code>qi</code>: the plane angle in 2 dimensions, the
   * Van Oosterom-Strackee formula for 3 vectors in space.
   */
  private static final class SolidAngle extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = ast.arg1().isVector();
      int[] dims = ast.arg2().isMatrix(false);
      if (n < 2 || dims == null || dims[1] != n || dims[0] != n) {
        return F.NIL;
      }
      IExpr p = ast.arg1().normal(false);
      IAST points = (IAST) ast.arg2().normal(false);
      IAST vectors = (IAST) engine
          .evaluate(F.mapRange(1, points.size(), i -> F.Subtract(points.get(i), p)));
      for (IExpr v : vectors) {
        if (isZeroVector(v, engine)) {
          return F.NIL;
        }
      }
      if (n == 2) {
        return engine.evaluate(F.binaryAST2(S.VectorAngle, vectors.arg1(), vectors.arg2()));
      }
      if (n == 3) {
        IExpr a = vectors.arg1();
        IExpr b = vectors.arg2();
        IExpr c = vectors.arg3();
        IExpr numerator = F.Abs(F.Det(vectors));
        IExpr denominator = F.Plus(F.Times(F.Norm(a), F.Norm(b), F.Norm(c)),
            F.Times(F.Dot(a, b), F.Norm(c)), F.Times(F.Dot(a, c), F.Norm(b)),
            F.Times(F.Dot(b, c), F.Norm(a)));
        IExpr den = engine.evaluate(denominator);
        IExpr num = engine.evaluate(numerator);
        if (num.isZero()) {
          return F.C0;
        }
        return engine.evaluate(F.Times(F.C2, F.ArcTan(den, num)));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>CircumscribedBall({p1, p2, ...})</code> - the ball of minimal radius which encloses the
   * points (or the vertices of a point based region). The ball is the smallest one through a subset
   * of at most <code>n + 1</code> points that encloses all points.
   */
  private static final class CircumscribedBall extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST() && !arg1.isList() && arg1.argSize() >= 1 && arg1.first().isList()) {
        if (arg1.isAST(S.Point) || arg1.isAST(S.Line) || arg1.isAST(S.Polygon)
            || arg1.isAST(S.Triangle) || arg1.isAST(S.Simplex) || arg1.isAST(S.Tetrahedron)) {
          IExpr vertices = arg1.first();
          arg1 = vertices.isVector() > 0 ? F.list(vertices) : vertices;
        }
      }
      int[] dims = arg1.isMatrix(false);
      if (dims == null || dims[0] < 1 || dims[1] < 1) {
        return F.NIL;
      }
      IAST points = (IAST) arg1.normal(false);
      int n = dims[1];
      boolean exact = true;
      for (IExpr point : points) {
        for (IExpr x : (IAST) point) {
          if (!x.isNumericFunction(true)) {
            return F.NIL;
          }
          if (x.isInexactNumber()) {
            exact = false;
          }
        }
      }
      if (points.argSize() == 1) {
        return F.binaryAST2(S.Ball, points.arg1(), exact ? F.C0 : F.CD0);
      }
      if (!exact) {
        return numericBall(points, n, engine);
      }
      IExpr ball = welzlBall(points, n, engine);
      return ball.isPresent() ? ball : exactBall(points, n, engine);
    }

    /** Thrown when a step of Welzl's algorithm can't be decided or the support is degenerate. */
    private static final class Undecided extends RuntimeException {
      private static final long serialVersionUID = 1L;

      Undecided() {
        super(null, null, false, false);
      }
    }

    /**
     * Welzl's algorithm with exact arithmetic: the smallest ball of the points with the support
     * points on its boundary, in a fixed pseudo random order of the points.
     *
     * @return {@link F#NIL} if a comparison isn't decidable or a support set is degenerate
     */
    private static @AllowNIL IExpr welzlBall(IAST points, int n, EvalEngine engine) {
      List<IExpr> order = new ArrayList<>(points.argSize());
      for (IExpr p : points) {
        order.add(p);
      }
      java.util.Collections.shuffle(order, new java.util.Random(points.argSize()));
      try {
        IExpr[] ball = welzl(order, order.size(), new ArrayList<>(), n, engine);
        if (ball == null) {
          return F.NIL;
        }
        return F.binaryAST2(S.Ball, ball[0], engine.evaluate(F.Sqrt(ball[1])));
      } catch (Undecided e) {
        return F.NIL;
      }
    }

    /** The smallest ball of the first <code>k</code> points with <code>support</code> on it. */
    private static IExpr[] welzl(List<IExpr> points, int k, List<IExpr> support, int n,
        EvalEngine engine) {
      IExpr[] ball = supportBall(support, engine);
      if (support.size() == n + 1) {
        return ball;
      }
      for (int i = 0; i < k; i++) {
        engine.checkTimeLimit();
        IExpr p = points.get(i);
        if (ball == null || !inside(p, ball, engine)) {
          support.add(p);
          ball = welzl(points, i, support, n, engine);
          support.remove(support.size() - 1);
        }
      }
      return ball;
    }

    /** The smallest ball with the support points on its boundary, <code>{center, r^2}</code>. */
    private static IExpr[] supportBall(List<IExpr> support, EvalEngine engine) {
      if (support.isEmpty()) {
        return null;
      }
      if (support.size() == 1) {
        return new IExpr[] {support.get(0), F.C0};
      }
      IExpr center = circumcenter(support.toArray(new IExpr[0]), engine);
      if (center.isNIL()) {
        throw new Undecided();
      }
      return new IExpr[] {center, squaredDistance(support.get(0), center, engine)};
    }

    private static boolean inside(IExpr p, IExpr[] ball, EvalEngine engine) {
      IExpr test = engine.evaluate(F.LessEqual(squaredDistance(p, ball[0], engine), ball[1]));
      if (test.isTrue()) {
        return true;
      }
      if (test.isFalse()) {
        return false;
      }
      throw new Undecided();
    }

    private static boolean nextCombination(int[] idx, int k) {
      int m = idx.length;
      int i = m - 1;
      while (i >= 0 && idx[i] == k - m + i) {
        i--;
      }
      if (i < 0) {
        return false;
      }
      idx[i]++;
      for (int j = i + 1; j < m; j++) {
        idx[j] = idx[j - 1] + 1;
      }
      return true;
    }

    private static @AllowNIL IExpr exactBall(IAST points, int n, EvalEngine engine) {
      int k = points.argSize();
      IExpr bestCenter = F.NIL;
      IExpr bestR2 = F.NIL;
      int count = 0;
      for (int m = 2; m <= Math.min(k, n + 1); m++) {
        int[] idx = new int[m];
        for (int i = 0; i < m; i++) {
          idx[i] = i;
        }
        do {
          engine.checkTimeLimit();
          if (++count > MAX_EXACT_SUBSETS) {
            return F.NIL;
          }
          IExpr[] selected = new IExpr[m];
          for (int i = 0; i < m; i++) {
            selected[i] = points.get(idx[i] + 1);
          }
          IExpr center = circumcenter(selected, engine);
          if (center.isNIL()) {
            continue;
          }
          IExpr r2 = squaredDistance(selected[0], center, engine);
          if (bestR2.isPresent()) {
            IExpr smaller = engine.evaluate(F.Less(r2, bestR2));
            if (smaller.isFalse()) {
              continue;
            }
            if (!smaller.isTrue()) {
              return F.NIL;
            }
          }
          int encloses = enclosesAll(points, center, r2, engine);
          if (encloses < 0) {
            return F.NIL;
          }
          if (encloses > 0) {
            bestCenter = center;
            bestR2 = r2;
          }
        } while (nextCombination(idx, k));
      }
      if (bestCenter.isNIL()) {
        return F.NIL;
      }
      return F.binaryAST2(S.Ball, bestCenter, engine.evaluate(F.Sqrt(bestR2)));
    }

    private static IExpr squaredDistance(IExpr p, IExpr q, EvalEngine engine) {
      IExpr d = F.Subtract(p, q);
      return engine.evaluate(F.Expand(F.Dot(d, d)));
    }

    /** 1 if all points lie in the ball, 0 if one does not, -1 if that isn't decidable. */
    private static int enclosesAll(IAST points, IExpr center, IExpr r2, EvalEngine engine) {
      for (IExpr p : points) {
        IExpr inside = engine.evaluate(F.LessEqual(squaredDistance(p, center, engine), r2));
        if (inside.isFalse()) {
          return 0;
        }
        if (!inside.isTrue()) {
          return -1;
        }
      }
      return 1;
    }

    /**
     * The center of the smallest ball through the points: the point of their affine hull at equal
     * distance from all of them.
     */
    private static @AllowNIL IExpr circumcenter(IExpr[] selected, EvalEngine engine) {
      int m = selected.length;
      if (m == 2) {
        return engine.evaluate(F.Times(F.C1D2, F.Plus(selected[0], selected[1])));
      }
      IExpr[] w = new IExpr[m - 1];
      for (int j = 1; j < m; j++) {
        w[j - 1] = engine.evaluate(F.Subtract(selected[j], selected[0]));
      }
      IAST gram = F.matrix((i, j) -> engine.evaluate(F.Dot(w[i], w[j])), m - 1, m - 1);
      IExpr det = engine.evaluate(F.Det(gram));
      if (det.isZero()) {
        return F.NIL;
      }
      IAST rhs = F.mapRange(0, m - 1, i -> F.Times(F.C1D2, F.Dot(w[i], w[i])));
      IExpr lambda = engine.evaluate(F.LinearSolve(gram, rhs));
      if (!lambda.isList() || lambda.argSize() != m - 1) {
        return F.NIL;
      }
      IASTAppendable sum = F.PlusAlloc(m);
      sum.append(selected[0]);
      for (int j = 0; j < m - 1; j++) {
        sum.append(F.Times(lambda.getAt(j + 1), w[j]));
      }
      return engine.evaluate(sum);
    }

    private static @AllowNIL IExpr numericBall(IAST points, int n, EvalEngine engine) {
      int k = points.argSize();
      double[][] p = new double[k][n];
      try {
        for (int i = 0; i < k; i++) {
          IAST point = (IAST) points.get(i + 1);
          for (int j = 0; j < n; j++) {
            p[i][j] = point.get(j + 1).evalf();
          }
        }
      } catch (ArgumentTypeException | ArithmeticException e) {
        return F.NIL;
      }
      double bestR2 = Double.POSITIVE_INFINITY;
      double[] bestCenter = null;
      long count = 0;
      for (int m = 2; m <= Math.min(k, n + 1); m++) {
        int[] idx = new int[m];
        for (int i = 0; i < m; i++) {
          idx[i] = i;
        }
        do {
          if ((++count & 0xFFF) == 0) {
            engine.checkTimeLimit();
          }
          if (count > MAX_NUMERIC_SUBSETS) {
            return F.NIL;
          }
          double[] center = numericCircumcenter(p, idx, n);
          if (center == null) {
            continue;
          }
          double r2 = squared(p[idx[0]], center);
          if (r2 >= bestR2) {
            continue;
          }
          double tolerance = 1.0e-10 * Math.max(r2, 1.0e-300);
          boolean all = true;
          for (int i = 0; i < k; i++) {
            if (squared(p[i], center) > r2 + tolerance) {
              all = false;
              break;
            }
          }
          if (all) {
            bestR2 = r2;
            bestCenter = center;
          }
        } while (nextCombination(idx, k));
      }
      if (bestCenter == null) {
        return F.NIL;
      }
      final double[] c = bestCenter;
      return F.binaryAST2(S.Ball, F.mapRange(0, n, i -> F.num(c[i])), F.num(Math.sqrt(bestR2)));
    }

    private static double squared(double[] a, double[] b) {
      double sum = 0.0;
      for (int i = 0; i < a.length; i++) {
        double d = a[i] - b[i];
        sum += d * d;
      }
      return sum;
    }

    private static double[] numericCircumcenter(double[][] p, int[] idx, int n) {
      int m = idx.length - 1;
      double[] p0 = p[idx[0]];
      double[][] w = new double[m][n];
      for (int j = 0; j < m; j++) {
        for (int i = 0; i < n; i++) {
          w[j][i] = p[idx[j + 1]][i] - p0[i];
        }
      }
      double[][] g = new double[m][m + 1];
      double scale = 0.0;
      for (int i = 0; i < m; i++) {
        for (int j = 0; j < m; j++) {
          double dot = 0.0;
          for (int l = 0; l < n; l++) {
            dot += w[i][l] * w[j][l];
          }
          g[i][j] = dot;
          scale = Math.max(scale, Math.abs(dot));
        }
        g[i][m] = 0.5 * g[i][i];
      }
      // Gaussian elimination with partial pivoting
      for (int col = 0; col < m; col++) {
        int pivot = col;
        for (int r = col + 1; r < m; r++) {
          if (Math.abs(g[r][col]) > Math.abs(g[pivot][col])) {
            pivot = r;
          }
        }
        if (Math.abs(g[pivot][col]) <= 1.0e-12 * scale) {
          return null;
        }
        double[] tmp = g[col];
        g[col] = g[pivot];
        g[pivot] = tmp;
        for (int r = 0; r < m; r++) {
          if (r != col) {
            double factor = g[r][col] / g[col][col];
            for (int c = col; c <= m; c++) {
              g[r][c] -= factor * g[col][c];
            }
          }
        }
      }
      double[] center = p0.clone();
      for (int j = 0; j < m; j++) {
        double lambda = g[j][m] / g[j][j];
        for (int i = 0; i < n; i++) {
          center[i] += lambda * w[j][i];
        }
      }
      return center;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>RegionConvert(reg, "Implicit")</code> - the <code>ImplicitRegion</code> of the condition
   * <code>RegionMember(reg, {x, y, ...})</code> in the formal variables, with the denominators of
   * polynomial conditions cleared and bounds written as chained inequalities;
   * <code>RegionConvert(reg, "Discretized")</code> is <code>DiscretizeRegion(reg)</code>.
   */
  private static final class RegionConvert extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr reg = ast.arg1();
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      String form = ast.arg2().toString();
      switch (form) {
        case "Implicit":
          return implicit(reg, engine);
        case "Discretized":
          return F.unaryAST1(S.DiscretizeRegion, reg);
        default:
          return F.NIL;
      }
    }

    private static @AllowNIL IExpr implicit(IExpr reg, EvalEngine engine) {
      if (reg.isAST(S.ImplicitRegion, 3)) {
        return reg;
      }
      int n = engine.evaluate(F.unaryAST1(S.RegionEmbeddingDimension, reg)).toIntDefault();
      if (n < 1) {
        return F.NIL;
      }
      IAST vars = formalVariables(n);
      if (reg.isAST(S.Hyperplane) && reg.first().isVector() == n) {
        IExpr offset = reg.argSize() == 1 ? F.C0
            : reg.second().isVector() == n ? F.Dot(reg.first(), reg.second()) : reg.second();
        IExpr equation = engine.evaluate(F.Equal(F.Dot(reg.first(), vars), offset));
        return F.binaryAST2(S.ImplicitRegion, engine.evaluate(tidy(equation, engine)), vars);
      }
      IExpr condition = engine.evaluate(F.RegionMember(reg, vars));
      if (condition.isAST(S.RegionMember) || condition.has(S.RegionMember, true)) {
        return F.NIL;
      }
      condition = engine.evaluate(tidy(condition, engine));
      return F.binaryAST2(S.ImplicitRegion, condition, vars);
    }

    /** Chain <code>a<=x && x<=b</code> and clear denominators in polynomial relations. */
    private static IExpr tidy(IExpr condition, EvalEngine engine) {
      if (condition.isAnd()) {
        IAST and = (IAST) condition;
        IASTAppendable result = F.ast(S.And, and.argSize());
        for (int i = 1; i < and.size(); i++) {
          IExpr current = and.get(i);
          if (i + 1 < and.size()) {
            IExpr chained = chain(current, and.get(i + 1));
            if (chained.isPresent()) {
              result.append(clearDenominators(chained, engine));
              i++;
              continue;
            }
          }
          result.append(clearDenominators(current, engine));
        }
        return result.oneIdentity1();
      }
      return clearDenominators(condition, engine);
    }

    /**
     * The least common multiple of the denominators of the coefficients of the expanded
     * polynomial, or <code>null</code> if a coefficient isn't rational.
     */
    private static IInteger denominatorLCM(IExpr polynomial, EvalEngine engine) {
      if (polynomial.isRational()) {
        return ((IRational) polynomial).denominator();
      }
      IExpr expanded = engine.evaluate(F.Expand(polynomial));
      IInteger lcm = F.C1;
      IAST terms = expanded.isPlus() ? (IAST) expanded : F.list(expanded);
      for (IExpr term : terms) {
        IExpr coefficient = F.C1;
        if (term.isTimes() && term.first().isNumber()) {
          coefficient = term.first();
        } else if (term.isNumber()) {
          coefficient = term;
        }
        if (!coefficient.isRational()) {
          return null;
        }
        lcm = lcm.lcm(((IRational) coefficient).denominator());
      }
      return lcm;
    }

    /** <code>a<=x</code> and <code>x<=b</code> (or with <code><</code>) to <code>a<=x<=b</code>. */
    private static @AllowNIL IExpr chain(IExpr lower, IExpr upper) {
      boolean lowerRelation = lower.isAST(S.LessEqual, 3) || lower.isAST(S.Less, 3);
      boolean upperRelation = upper.isAST(S.LessEqual, 3) || upper.isAST(S.Less, 3);
      if (lowerRelation && upperRelation && lower.second().equals(upper.first())
          && lower.first().isNumericFunction(true) && upper.second().isNumericFunction(true)) {
        return F.Inequality(lower.first(), lower.head(), lower.second(), upper.head(),
            upper.second());
      }
      return F.NIL;
    }

    /**
     * A relation between a polynomial with rational coefficients and a rational number, or a
     * chain <code>a <= p <= b</code> with rational bounds: all sides multiplied by the least common
     * multiple of the denominators.
     */
    private static IExpr clearDenominators(IExpr relation, EvalEngine engine) {
      if (relation.isAST(S.Inequality, 6)) {
        IAST chain = (IAST) relation;
        if (!chain.arg1().isRational() || !chain.get(5).isRational()) {
          return relation;
        }
        IInteger lcm = denominatorLCM(chain.arg3(), engine);
        if (lcm == null) {
          return relation;
        }
        lcm = lcm.lcm(((IRational) chain.arg1()).denominator())
            .lcm(((IRational) chain.get(5)).denominator());
        if (lcm.isOne()) {
          return relation;
        }
        return F.Inequality(engine.evaluate(F.Times(lcm, chain.arg1())), chain.arg2(),
            engine.evaluate(F.Expand(F.Times(lcm, chain.arg3()))), chain.arg4(),
            engine.evaluate(F.Times(lcm, chain.get(5))));
      }
      if (!relation.isAST(S.LessEqual, 3) && !relation.isAST(S.Less, 3)
          && !relation.isAST(S.Equal, 3) && !relation.isAST(S.GreaterEqual, 3)
          && !relation.isAST(S.Greater, 3)) {
        return relation;
      }
      IExpr lhs = relation.first();
      IExpr rhs = relation.second();
      if (lhs.isRational() == rhs.isRational()) {
        return relation;
      }
      IExpr number = lhs.isRational() ? lhs : rhs;
      IExpr polynomial = lhs.isRational() ? rhs : lhs;
      IInteger lcm = denominatorLCM(polynomial, engine);
      if (lcm == null) {
        return relation;
      }
      lcm = lcm.lcm(((IRational) number).denominator());
      if (lcm.isOne()) {
        return relation;
      }
      return F.binaryAST2(relation.head(), engine.evaluate(F.Expand(F.Times(lcm, lhs))),
          engine.evaluate(F.Expand(F.Times(lcm, rhs))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>RegionDisjoint(reg1, reg2, ...)</code> - <code>True</code> if no two of the regions have
   * a common point. Solid balls and boxes are compared directly, a point by membership, other
   * regions by <code>RegionIntersection</code>.
   */
  private static final class RegionDisjoint extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 2) {
        // no two regions that could meet
        return S.True;
      }
      for (int i = 1; i < ast.size(); i++) {
        for (int j = i + 1; j < ast.size(); j++) {
          engine.checkTimeLimit();
          IExpr disjoint = disjoint(ast.get(i), ast.get(j), engine);
          if (disjoint.isFalse()) {
            return S.False;
          }
          if (!disjoint.isTrue()) {
            return F.NIL;
          }
        }
      }
      return S.True;
    }

    private static IExpr[] ball(IExpr reg) {
      if ((reg.isAST(S.Disk) || reg.isAST(S.Ball)) && reg.argSize() >= 1 && reg.argSize() <= 2
          && reg.first().isVector() > 0) {
        IExpr r = reg.argSize() == 2 ? reg.second() : F.C1;
        if (!r.isList()) {
          return new IExpr[] {reg.first().normal(false), r};
        }
      }
      return null;
    }

    private static IAST[] box(IExpr reg) {
      if ((reg.isAST(S.Rectangle) || reg.isAST(S.Cuboid)) && reg.argSize() == 2
          && reg.first().isVector() > 0 && reg.first().isVector() == reg.second().isVector()) {
        return new IAST[] {(IAST) reg.first().normal(false), (IAST) reg.second().normal(false)};
      }
      return null;
    }

    private static @AllowNIL IExpr disjoint(IExpr a, IExpr b, EvalEngine engine) {
      a = org.matheclipse.core.eval.util.RegionPrimitives.normalizedRegion(a);
      b = org.matheclipse.core.eval.util.RegionPrimitives.normalizedRegion(b);
      if (a.isAST(S.Point, 2) && a.first().isVector() > 0) {
        return engine.evaluate(F.Not(F.RegionMember(b, a.first())));
      }
      if (b.isAST(S.Point, 2) && b.first().isVector() > 0) {
        return engine.evaluate(F.Not(F.RegionMember(a, b.first())));
      }
      IExpr[] ballA = ball(a);
      IExpr[] ballB = ball(b);
      if (ballA != null && ballB != null) {
        IExpr d = F.Subtract(ballA[0], ballB[0]);
        return engine
            .evaluate(F.Greater(F.Dot(d, d), F.Sqr(F.Plus(ballA[1], ballB[1]))));
      }
      IAST[] boxA = box(a);
      IAST[] boxB = box(b);
      if (boxA != null && boxB != null && boxA[0].argSize() == boxB[0].argSize()) {
        boolean undecided = false;
        for (int i = 1; i <= boxA[0].argSize(); i++) {
          IExpr apart = engine.evaluate(F.Or(F.Less(boxA[1].get(i), boxB[0].get(i)),
              F.Less(boxB[1].get(i), boxA[0].get(i))));
          if (apart.isTrue()) {
            return S.True;
          }
          if (!apart.isFalse()) {
            undecided = true;
          }
        }
        return undecided ? F.NIL : S.False;
      }
      if (ballA != null && boxB != null || ballB != null && boxA != null) {
        IExpr[] theBall = ballA != null ? ballA : ballB;
        IExpr theBox = ballA != null ? b : a;
        return engine
            .evaluate(F.Greater(F.binaryAST2(S.RegionDistance, theBox, theBall[0]), theBall[1]));
      }
      IExpr intersection = engine.evaluate(F.binaryAST2(S.RegionIntersection, a, b));
      if (intersection.isAST(S.EmptyRegion)) {
        return S.True;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // planar polygons
  // ---------------------------------------------------------------------------------------------

  /** Planar vertex coordinates, either exact rational numbers or machine numbers. */
  private abstract static class PlanarCoords {
    abstract int size();

    /** The sign of the cross product of <code>b - a</code> and <code>c - a</code>. */
    abstract int orient(int a, int b, int c);

    /** The sign of the dot product of <code>b - a</code> and <code>c - b</code>. */
    abstract int dotSign(int a, int b, int c);

    /** Test if <code>p</code> lies in the bounding box of <code>a</code> and <code>b</code>. */
    abstract boolean inBox(int a, int b, int p);

    abstract boolean same(int a, int b);

    /** Area of the triangle as machine number. */
    abstract double x(int i);

    abstract double y(int i);

    /** The sign of the signed area of the polygon through all vertices. */
    int areaSign() {
      int n = size();
      double area = 0.0;
      for (int i = 0; i < n; i++) {
        int j = (i + 1) % n;
        area += x(i) * y(j) - x(j) * y(i);
      }
      return area > 0 ? 1 : area < 0 ? -1 : 0;
    }
  }

  private static final class RationalCoords extends PlanarCoords {
    final IRational[] xs;
    final IRational[] ys;

    RationalCoords(IRational[] xs, IRational[] ys) {
      this.xs = xs;
      this.ys = ys;
    }

    @Override
    int size() {
      return xs.length;
    }

    @Override
    int orient(int a, int b, int c) {
      IRational value = xs[b].subtract(xs[a]).multiply(ys[c].subtract(ys[a]))
          .subtract(ys[b].subtract(ys[a]).multiply(xs[c].subtract(xs[a])));
      return value.complexSign();
    }

    @Override
    int dotSign(int a, int b, int c) {
      IRational value = xs[b].subtract(xs[a]).multiply(xs[c].subtract(xs[b]))
          .add(ys[b].subtract(ys[a]).multiply(ys[c].subtract(ys[b])));
      return value.complexSign();
    }

    @Override
    boolean inBox(int a, int b, int p) {
      return xs[p].subtract(xs[a]).multiply(xs[p].subtract(xs[b])).complexSign() <= 0
          && ys[p].subtract(ys[a]).multiply(ys[p].subtract(ys[b])).complexSign() <= 0;
    }

    @Override
    boolean same(int a, int b) {
      return xs[a].subtract(xs[b]).isZero() && ys[a].subtract(ys[b]).isZero();
    }

    @Override
    int areaSign() {
      int n = size();
      IRational area = F.C0;
      for (int i = 0; i < n; i++) {
        int j = (i + 1) % n;
        area = area.add(xs[i].multiply(ys[j]).subtract(xs[j].multiply(ys[i])));
      }
      return area.complexSign();
    }

    @Override
    double x(int i) {
      return xs[i].doubleValue();
    }

    @Override
    double y(int i) {
      return ys[i].doubleValue();
    }
  }

  private static final class DoubleCoords extends PlanarCoords {
    final double[] xs;
    final double[] ys;
    /** The tolerance of products of two coordinate differences. */
    final double eps;
    /** The tolerance of coordinate differences. */
    final double lengthEps;

    DoubleCoords(double[] xs, double[] ys) {
      this.xs = xs;
      this.ys = ys;
      double scale = 0.0;
      for (int i = 0; i < xs.length; i++) {
        scale = Math.max(scale, Math.max(Math.abs(xs[i]), Math.abs(ys[i])));
      }
      eps = 1.0e-12 * Math.max(scale * scale, Double.MIN_NORMAL);
      lengthEps = 1.0e-12 * Math.max(scale, Double.MIN_NORMAL);
    }

    private int sign(double value) {
      return value > eps ? 1 : value < -eps ? -1 : 0;
    }

    @Override
    int size() {
      return xs.length;
    }

    @Override
    int orient(int a, int b, int c) {
      return sign((xs[b] - xs[a]) * (ys[c] - ys[a]) - (ys[b] - ys[a]) * (xs[c] - xs[a]));
    }

    @Override
    int dotSign(int a, int b, int c) {
      return sign((xs[b] - xs[a]) * (xs[c] - xs[b]) + (ys[b] - ys[a]) * (ys[c] - ys[b]));
    }

    @Override
    boolean inBox(int a, int b, int p) {
      return sign((xs[p] - xs[a]) * (xs[p] - xs[b])) <= 0
          && sign((ys[p] - ys[a]) * (ys[p] - ys[b])) <= 0;
    }

    @Override
    boolean same(int a, int b) {
      return Math.abs(xs[a] - xs[b]) + Math.abs(ys[a] - ys[b]) <= lengthEps;
    }

    @Override
    double x(int i) {
      return xs[i];
    }

    @Override
    double y(int i) {
      return ys[i];
    }
  }

  /**
   * The planar coordinates of the vertices: 2 dimensional points as they are, planar 3 dimensional
   * points projected along the coordinate axis closest to the normal.
   *
   * @return <code>null</code> if the vertices aren't numeric or not in a plane
   */
  private static PlanarCoords planarCoords(IAST vertices) {
    int k = vertices.argSize();
    if (k < 1) {
      return null;
    }
    int n = vertices.arg1().argSize();
    if (n != 2 && n != 3) {
      return null;
    }
    boolean rational = true;
    for (IExpr p : vertices) {
      if (p.argSize() != n) {
        return null;
      }
      for (IExpr x : (IAST) p) {
        if (!x.isRational()) {
          rational = false;
          if (!x.isNumericFunction(true)) {
            return null;
          }
        }
      }
    }
    if (rational) {
      IRational[][] c = new IRational[n][k];
      for (int i = 0; i < k; i++) {
        for (int j = 0; j < n; j++) {
          c[j][i] = (IRational) vertices.get(i + 1).getAt(j + 1);
        }
      }
      if (n == 2) {
        return new RationalCoords(c[0], c[1]);
      }
      IRational[] normal = new IRational[] {F.C0, F.C0, F.C0};
      for (int i = 0; i < k; i++) {
        int l = (i + 1) % k;
        for (int axis = 0; axis < 3; axis++) {
          int u = (axis + 1) % 3;
          int v = (axis + 2) % 3;
          normal[axis] =
              normal[axis].add(c[u][i].subtract(c[u][l]).multiply(c[v][i].add(c[v][l])));
        }
      }
      int drop = 0;
      for (int axis = 1; axis < 3; axis++) {
        if (normal[axis].abs().subtract(normal[drop].abs()).complexSign() > 0) {
          drop = axis;
        }
      }
      if (normal[drop].isZero()) {
        return null;
      }
      for (int i = 1; i < k; i++) {
        IRational dot = F.C0;
        for (int axis = 0; axis < 3; axis++) {
          dot = dot.add(c[axis][i].subtract(c[axis][0]).multiply(normal[axis]));
        }
        if (!dot.isZero()) {
          return null;
        }
      }
      return new RationalCoords(c[(drop + 1) % 3], c[(drop + 2) % 3]);
    }
    double[][] c = new double[n][k];
    try {
      for (int i = 0; i < k; i++) {
        for (int j = 0; j < n; j++) {
          c[j][i] = vertices.get(i + 1).getAt(j + 1).evalf();
        }
      }
    } catch (ArgumentTypeException | ArithmeticException e) {
      return null;
    }
    if (n == 2) {
      return new DoubleCoords(c[0], c[1]);
    }
    double[] normal = new double[3];
    double scale = 0.0;
    for (int i = 0; i < k; i++) {
      int l = (i + 1) % k;
      for (int axis = 0; axis < 3; axis++) {
        int u = (axis + 1) % 3;
        int v = (axis + 2) % 3;
        normal[axis] += (c[u][i] - c[u][l]) * (c[v][i] + c[v][l]);
        scale = Math.max(scale, Math.abs(c[axis][i]));
      }
    }
    int drop = 0;
    for (int axis = 1; axis < 3; axis++) {
      if (Math.abs(normal[axis]) > Math.abs(normal[drop])) {
        drop = axis;
      }
    }
    double length = Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
    if (length <= 1.0e-12 * Math.max(scale * scale, Double.MIN_NORMAL)) {
      return null;
    }
    for (int i = 1; i < k; i++) {
      double dot = 0.0;
      for (int axis = 0; axis < 3; axis++) {
        dot += (c[axis][i] - c[axis][0]) * normal[axis];
      }
      if (Math.abs(dot) > 1.0e-9 * length * Math.max(scale, 1.0)) {
        return null;
      }
    }
    return new DoubleCoords(c[(drop + 1) % 3], c[(drop + 2) % 3]);
  }

  /** Test if the closed segments <code>ab</code> and <code>cd</code> have a common point. */
  private static boolean segmentsIntersect(PlanarCoords p, int a, int b, int c, int d) {
    int o1 = p.orient(a, b, c);
    int o2 = p.orient(a, b, d);
    int o3 = p.orient(c, d, a);
    int o4 = p.orient(c, d, b);
    if (o1 * o2 < 0 && o3 * o4 < 0) {
      return true;
    }
    return (o1 == 0 && p.inBox(a, b, c)) || (o2 == 0 && p.inBox(a, b, d))
        || (o3 == 0 && p.inBox(c, d, a)) || (o4 == 0 && p.inBox(c, d, b));
  }

  /**
   * Test if the closed polygon through the vertices doesn't touch or cross itself: no repeated
   * vertex, no two edges which are not neighbours meet, and no edge folds back on the previous
   * one.
   */
  private static boolean isSimple(PlanarCoords p, EvalEngine engine) {
    int n = p.size();
    if (n < 3) {
      return false;
    }
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        if (p.same(i, j)) {
          return false;
        }
      }
    }
    for (int i = 0; i < n; i++) {
      int i1 = (i + 1) % n;
      int i2 = (i + 2) % n;
      if (p.orient(i, i1, i2) == 0 && p.dotSign(i, i1, i2) < 0) {
        return false;
      }
      if (n > 3) {
        engine.checkTimeLimit();
      }
      for (int j = i + 2; j < n; j++) {
        int j1 = (j + 1) % n;
        if (j1 == i) {
          continue;
        }
        if (segmentsIntersect(p, i, i1, j, j1)) {
          return false;
        }
      }
    }
    return true;
  }

  /** Test if all turns along the polygon have the same orientation. */
  private static boolean sameTurns(PlanarCoords p) {
    int n = p.size();
    boolean positive = false;
    boolean negative = false;
    for (int i = 0; i < n; i++) {
      int turn = p.orient(i, (i + 1) % n, (i + 2) % n);
      if (turn > 0) {
        positive = true;
      } else if (turn < 0) {
        negative = true;
      }
    }
    return !(positive && negative) && (positive || negative);
  }

  /**
   * <code>ConvexPolygonQ(poly)</code> and <code>SimplePolygonQ(poly)</code> for a polygon with
   * numeric vertices in the plane (or in a plane in space).
   */
  private static final class PolygonTest extends AbstractFunctionEvaluator {
    private final boolean convex;

    PolygonTest(boolean convex) {
      this.convex = convex;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST vertices = polygonVertices(ast.arg1());
      if (vertices.isNIL()) {
        return S.False;
      }
      if (vertices.argSize() == 3 && !vertices.isNumericFunction(true)) {
        // a triangle with symbolic corners is taken as a proper triangle
        return S.True;
      }
      PlanarCoords p = planarCoords(vertices);
      if (p == null || !isSimple(p, engine)) {
        return S.False;
      }
      return F.booleSymbol(!convex || sameTurns(p));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // meshes
  // ---------------------------------------------------------------------------------------------

  /** Union-find on vertex indices. */
  private static final class UnionFind {
    final int[] parent;

    UnionFind(int n) {
      parent = new int[n];
      for (int i = 0; i < n; i++) {
        parent[i] = i;
      }
    }

    int find(int i) {
      while (parent[i] != i) {
        parent[i] = parent[parent[i]];
        i = parent[i];
      }
      return i;
    }

    void union(int a, int b) {
      int ra = find(a);
      int rb = find(b);
      if (ra != rb) {
        parent[Math.max(ra, rb)] = Math.min(ra, rb);
      }
    }
  }

  /** A mesh cell: its head and its 1-based vertex indices. */
  private static final class Cell {
    final IBuiltInSymbol head;
    final int[] indices;

    Cell(IBuiltInSymbol head, int[] indices) {
      this.head = head;
      this.indices = indices;
    }
  }

  /**
   * The cells of a mesh region in the order they are given; a compact list of index lists is split
   * into its cells.
   *
   * @return <code>null</code> if a cell can't be parsed
   */
  private static List<Cell> parseCells(IAST mesh) {
    List<Cell> cells = new ArrayList<>();
    int numberOfCoordinates = mesh.arg1().argSize();
    for (int i = 2; i < mesh.size(); i++) {
      IExpr arg = mesh.get(i);
      if (arg.isRuleAST()) {
        break;
      }
      IAST list = arg.isList() ? (IAST) arg : F.list(arg);
      for (IExpr cell : list) {
        if (!cell.isAST1() || !cell.head().isBuiltInSymbol() || !cell.first().isList()) {
          return null;
        }
        IBuiltInSymbol head = (IBuiltInSymbol) cell.head();
        IAST indices = (IAST) cell.first();
        IAST groups = indices.argSize() > 0 && indices.arg1().isList() ? indices : F.list(indices);
        for (IExpr group : groups) {
          if (!group.isList()) {
            return null;
          }
          int[] idx = new int[group.argSize()];
          for (int k = 0; k < idx.length; k++) {
            idx[k] = group.getAt(k + 1).toIntDefault();
            if (idx[k] < 1 || idx[k] > numberOfCoordinates) {
              return null;
            }
          }
          if (head == S.Line && idx.length > 2) {
            // a walk Line({1,2,3,1}) is one edge per step
            for (int k = 0; k + 1 < idx.length; k++) {
              cells.add(new Cell(head, new int[] {idx[k], idx[k + 1]}));
            }
          } else {
            cells.add(new Cell(head, idx));
          }
        }
      }
    }
    return cells;
  }

  /**
   * Cells with renumbered vertices as arguments of a mesh region: the cells of one head in the
   * compact form <code>Head({{...}, ...})</code>.
   */
  private static IAST cellArguments(List<Cell> cells, Map<Integer, Integer> renumber) {
    Map<IBuiltInSymbol, IASTAppendable> byHead = new java.util.LinkedHashMap<>();
    for (Cell cell : cells) {
      IASTAppendable group = byHead.computeIfAbsent(cell.head, h -> F.ListAlloc());
      group.append(F.mapRange(0, cell.indices.length,
          k -> F.ZZ(renumber.get(cell.indices[k]))));
    }
    IASTAppendable result = F.ListAlloc(byHead.size());
    for (Map.Entry<IBuiltInSymbol, IASTAppendable> entry : byHead.entrySet()) {
      IASTAppendable group = entry.getValue();
      result.append(F.unaryAST1(entry.getKey(), group.argSize() == 1 ? group.arg1() : group));
    }
    return result;
  }

  /** The old-to-new vertex numbers in the order the vertices appear in the cells. */
  private static Map<Integer, Integer> renumbering(List<Cell> cells, IASTAppendable coordinates,
      IAST allCoordinates) {
    Map<Integer, Integer> renumber = new HashMap<>();
    for (Cell cell : cells) {
      for (int index : cell.indices) {
        if (!renumber.containsKey(index)) {
          renumber.put(index, renumber.size() + 1);
          coordinates.append(allCoordinates.get(index));
        }
      }
    }
    return renumber;
  }

  /**
   * <code>ConnectedMeshComponents(mesh)</code> - the connected pieces of a mesh region: for a
   * <code>MeshRegion</code> the classes of cells joined through shared vertices, for a two
   * dimensional <code>BoundaryMeshRegion</code> each outer boundary curve with the holes directly
   * inside it.
   */
  private static final class ConnectedMeshComponents extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!MeshFunctions.isMeshRegion(arg1)) {
        return F.NIL;
      }
      IAST mesh = (IAST) arg1;
      List<Cell> cells = parseCells(mesh);
      if (cells == null || cells.isEmpty()) {
        return F.NIL;
      }
      IAST coordinates = (IAST) mesh.arg1();
      UnionFind uf = new UnionFind(coordinates.argSize() + 1);
      for (Cell cell : cells) {
        for (int k = 1; k < cell.indices.length; k++) {
          uf.union(cell.indices[0], cell.indices[k]);
        }
      }
      Map<Integer, List<Cell>> components = new java.util.LinkedHashMap<>();
      for (Cell cell : cells) {
        components.computeIfAbsent(uf.find(cell.indices[0]), r -> new ArrayList<>()).add(cell);
      }
      if (mesh.isAST(S.BoundaryMeshRegion)) {
        if (components.size() == 1) {
          return F.list(mesh);
        }
        if (MeshFunctions.embeddingDimension(mesh) != 2) {
          return F.NIL;
        }
        return boundaryComponents(new ArrayList<>(components.values()), coordinates);
      }
      IASTAppendable result = F.ListAlloc(components.size());
      for (List<Cell> component : components.values()) {
        IASTAppendable newCoordinates = F.ListAlloc();
        Map<Integer, Integer> renumber = renumbering(component, newCoordinates, coordinates);
        IASTAppendable region = F.ast(S.MeshRegion);
        region.append(newCoordinates);
        region.append(cellArguments(component, renumber));
        result.append(region);
      }
      return result;
    }

    /** Split the boundary curves of a planar boundary mesh into outer curves with their holes. */
    private static @AllowNIL IExpr boundaryComponents(List<List<Cell>> loops, IAST coordinates) {
      int m = loops.size();
      List<double[][]> polygons = new ArrayList<>();
      List<List<Integer>> orders = new ArrayList<>();
      for (List<Cell> loop : loops) {
        List<Integer> order = loopOrder(loop);
        double[][] polygon = order == null ? null : loopPolygon(order, coordinates);
        if (polygon == null) {
          return F.NIL;
        }
        orders.add(order);
        polygons.add(polygon);
      }
      int[] depth = new int[m];
      boolean[][] inside = new boolean[m][m];
      for (int i = 0; i < m; i++) {
        double[] probe = polygons.get(i)[0];
        for (int j = 0; j < m; j++) {
          if (i != j && contains(polygons.get(j), probe[0], probe[1])) {
            inside[i][j] = true;
            depth[i]++;
          }
        }
      }
      IASTAppendable result = F.ListAlloc();
      for (int outer = 0; outer < m; outer++) {
        if (depth[outer] % 2 != 0) {
          continue;
        }
        List<List<Integer>> component = new ArrayList<>();
        component.add(orders.get(outer));
        for (int hole = 0; hole < m; hole++) {
          if (depth[hole] == depth[outer] + 1 && inside[hole][outer]) {
            component.add(orders.get(hole));
          }
        }
        IASTAppendable newCoordinates = F.ListAlloc();
        Map<Integer, Integer> renumber = new HashMap<>();
        IASTAppendable walks = F.ListAlloc(component.size());
        for (List<Integer> order : component) {
          IASTAppendable walk = F.ListAlloc(order.size() + 1);
          for (int index : order) {
            Integer number = renumber.get(index);
            if (number == null) {
              number = renumber.size() + 1;
              renumber.put(index, number);
              newCoordinates.append(coordinates.get(index));
            }
            walk.append(F.ZZ(number));
          }
          walk.append(walk.arg1());
          walks.append(F.Line(walk));
        }
        result.append(F.binaryAST2(S.BoundaryMeshRegion, newCoordinates,
            walks.argSize() == 1 ? walks.arg1() : walks));
      }
      return result;
    }

    /** The vertex indices of a closed boundary curve given by its edges, in order. */
    private static List<Integer> loopOrder(List<Cell> loop) {
      Map<Integer, List<Integer>> neighbours = new HashMap<>();
      for (Cell cell : loop) {
        if (cell.indices.length != 2) {
          return null;
        }
        neighbours.computeIfAbsent(cell.indices[0], k -> new ArrayList<>()).add(cell.indices[1]);
        neighbours.computeIfAbsent(cell.indices[1], k -> new ArrayList<>()).add(cell.indices[0]);
      }
      int start = loop.get(0).indices[0];
      List<Integer> order = new ArrayList<>();
      int previous = -1;
      int current = start;
      do {
        order.add(current);
        List<Integer> next = neighbours.get(current);
        if (next == null || next.size() != 2) {
          return null;
        }
        int following = next.get(0) != previous ? next.get(0) : next.get(1);
        previous = current;
        current = following;
      } while (current != start && order.size() <= loop.size());
      if (current != start) {
        return null;
      }
      return order;
    }

    /** The coordinates of the vertices of a boundary curve as machine numbers. */
    private static double[][] loopPolygon(List<Integer> order, IAST coordinates) {
      double[][] polygon = new double[order.size()][2];
      try {
        for (int i = 0; i < order.size(); i++) {
          IExpr point = coordinates.get(order.get(i));
          polygon[i][0] = point.first().evalf();
          polygon[i][1] = point.second().evalf();
        }
      } catch (ArgumentTypeException | ArithmeticException e) {
        return null;
      }
      return polygon;
    }

    /** Even-odd test of a point against a polygon. */
    private static boolean contains(double[][] polygon, double x, double y) {
      boolean in = false;
      for (int i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
        double xi = polygon[i][0];
        double yi = polygon[i][1];
        double xj = polygon[j][0];
        double yj = polygon[j][1];
        if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
          in = !in;
        }
      }
      return in;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>TriangulateMesh(reg)</code> - a <code>MeshRegion</code> of triangles which covers a
   * simple polygon, a planar boundary mesh with one boundary curve or the polygons of a planar
   * mesh, by ear clipping. With <code>MaxCellMeasure -> a</code> (or
   * <code>{"Area" -> a}</code>, <code>{"Length" -> l}</code>) all triangles are split into four
   * until they are small enough.
   */
  private static final class TriangulateMesh extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr reg = ast.arg1();
      double maxArea = Double.POSITIVE_INFINITY;
      double maxLength = Double.POSITIVE_INFINITY;
      for (int i = 2; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (!option.isRuleAST() || !option.first().equals(S.MaxCellMeasure)) {
          return F.NIL;
        }
        IExpr value = option.second();
        IAST specs = value.isList() ? (IAST) value : F.list(F.Rule(F.stringx("Area"), value));
        for (IExpr spec : specs) {
          if (!spec.isRuleAST() || !spec.first().isString() || !spec.second().isReal()) {
            if (spec.isRuleAST() && spec.second().equals(S.Automatic)) {
              continue;
            }
            return F.NIL;
          }
          double limit = spec.second().evalf();
          if (limit <= 0.0) {
            return F.NIL;
          }
          String kind = spec.first().toString();
          if (kind.equals("Area")) {
            maxArea = limit;
          } else if (kind.equals("Length")) {
            maxLength = limit;
          } else {
            return F.NIL;
          }
        }
      }
      IAST coordinates;
      List<int[]> polygons = new ArrayList<>();
      if (MeshFunctions.isMeshRegion(reg)) {
        IAST mesh = (IAST) reg;
        if (MeshFunctions.embeddingDimension(mesh) != 2) {
          return F.NIL;
        }
        coordinates = (IAST) mesh.arg1();
        if (mesh.isAST(S.BoundaryMeshRegion)) {
          IAST cycle = MeshFunctions.boundaryCycle(mesh);
          if (cycle.isNIL()) {
            return F.NIL;
          }
          polygons.add(indices(cycle));
        } else {
          List<Cell> cells = parseCells(mesh);
          if (cells == null) {
            return F.NIL;
          }
          for (Cell cell : cells) {
            if (cell.head == S.Polygon) {
              polygons.add(cell.indices);
            }
          }
          if (polygons.isEmpty()) {
            return F.NIL;
          }
        }
      } else {
        IAST vertices = polygonVertices(reg);
        if (vertices.isNIL() || vertices.arg1().argSize() != 2) {
          return F.NIL;
        }
        coordinates = vertices;
        int[] cycle = new int[vertices.argSize()];
        for (int i = 0; i < cycle.length; i++) {
          cycle[i] = i + 1;
        }
        polygons.add(cycle);
      }
      List<int[]> triangles = new ArrayList<>();
      for (int[] polygon : polygons) {
        IASTAppendable points = F.ListAlloc(polygon.length);
        for (int index : polygon) {
          points.append(coordinates.get(index));
        }
        PlanarCoords p = planarCoords(points);
        if (p == null || !isSimple(p, engine)) {
          return F.NIL;
        }
        List<int[]> local = earClip(p, engine);
        if (local == null) {
          return F.NIL;
        }
        for (int[] t : local) {
          triangles.add(new int[] {polygon[t[0]], polygon[t[1]], polygon[t[2]]});
        }
      }
      IASTAppendable allCoordinates = F.ListAlloc(coordinates.argSize());
      allCoordinates.appendArgs(coordinates);
      if (maxArea < Double.POSITIVE_INFINITY || maxLength < Double.POSITIVE_INFINITY) {
        triangles = refine(allCoordinates, triangles, maxArea, maxLength, engine);
        if (triangles == null) {
          return F.NIL;
        }
      }
      IASTAppendable cells = F.ListAlloc(triangles.size());
      for (int[] t : triangles) {
        cells.append(F.list(F.ZZ(t[0]), F.ZZ(t[1]), F.ZZ(t[2])));
      }
      return F.binaryAST2(S.MeshRegion, allCoordinates, F.list(F.Polygon(cells)));
    }

    private static int[] indices(IAST list) {
      int[] result = new int[list.argSize()];
      for (int i = 0; i < result.length; i++) {
        result[i] = list.get(i + 1).toIntDefault();
      }
      return result;
    }

    /**
     * Ear clipping of a simple polygon. The triangles are counterclockwise and given by the
     * 0-based vertex positions.
     */
    private static List<int[]> earClip(PlanarCoords p, EvalEngine engine) {
      int n = p.size();
      List<Integer> remaining = new ArrayList<>(n);
      int orientation = p.areaSign();
      if (orientation == 0) {
        return null;
      }
      for (int i = 0; i < n; i++) {
        remaining.add(orientation > 0 ? i : n - 1 - i);
      }
      List<int[]> triangles = new ArrayList<>(n - 2);
      while (remaining.size() > 3) {
        engine.checkTimeLimit();
        int m = remaining.size();
        boolean clipped = false;
        for (int i = 0; i < m; i++) {
          int a = remaining.get((i + m - 1) % m);
          int b = remaining.get(i);
          int c = remaining.get((i + 1) % m);
          if (p.orient(a, b, c) <= 0) {
            continue;
          }
          boolean ear = true;
          for (int other : remaining) {
            if (other == a || other == b || other == c) {
              continue;
            }
            if (p.orient(a, b, other) >= 0 && p.orient(b, c, other) >= 0
                && p.orient(c, a, other) >= 0) {
              ear = false;
              break;
            }
          }
          if (ear) {
            triangles.add(new int[] {a, b, c});
            remaining.remove(i);
            clipped = true;
            break;
          }
        }
        if (!clipped) {
          // only straight angles are left: drop a vertex on a straight edge
          for (int i = 0; i < m; i++) {
            int a = remaining.get((i + m - 1) % m);
            int b = remaining.get(i);
            int c = remaining.get((i + 1) % m);
            if (p.orient(a, b, c) == 0) {
              remaining.remove(i);
              clipped = true;
              break;
            }
          }
          if (!clipped) {
            return null;
          }
        }
      }
      if (p.orient(remaining.get(0), remaining.get(1), remaining.get(2)) != 0) {
        triangles.add(new int[] {remaining.get(0), remaining.get(1), remaining.get(2)});
      }
      return triangles;
    }

    /** Split every triangle into four at the edge midpoints until all are small enough. */
    private static List<int[]> refine(IASTAppendable coordinates, List<int[]> triangles,
        double maxArea, double maxLength, EvalEngine engine) {
      List<double[]> points = new ArrayList<>();
      try {
        for (IExpr point : coordinates) {
          points.add(new double[] {point.first().evalf(), point.second().evalf()});
        }
      } catch (ArgumentTypeException | ArithmeticException e) {
        return null;
      }
      while (true) {
        boolean small = true;
        for (int[] t : triangles) {
          double[] a = points.get(t[0] - 1);
          double[] b = points.get(t[1] - 1);
          double[] c = points.get(t[2] - 1);
          double area =
              Math.abs((b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0])) / 2.0;
          double longest = Math.max(Math.hypot(b[0] - a[0], b[1] - a[1]),
              Math.max(Math.hypot(c[0] - b[0], c[1] - b[1]), Math.hypot(a[0] - c[0], a[1] - c[1])));
          if (area > maxArea || longest > maxLength) {
            small = false;
            break;
          }
        }
        if (small) {
          return triangles;
        }
        if (4L * triangles.size() > MAX_TRIANGLES) {
          return null;
        }
        Map<Long, Integer> midpoints = new HashMap<>();
        List<int[]> refined = new ArrayList<>(4 * triangles.size());
        for (int[] t : triangles) {
          engine.checkTimeLimit();
          int ab = midpoint(t[0], t[1], midpoints, coordinates, points, engine);
          int bc = midpoint(t[1], t[2], midpoints, coordinates, points, engine);
          int ca = midpoint(t[2], t[0], midpoints, coordinates, points, engine);
          refined.add(new int[] {t[0], ab, ca});
          refined.add(new int[] {ab, t[1], bc});
          refined.add(new int[] {ca, bc, t[2]});
          refined.add(new int[] {ab, bc, ca});
        }
        triangles = refined;
      }
    }

    private static int midpoint(int i, int j, Map<Long, Integer> midpoints,
        IASTAppendable coordinates, List<double[]> points, EvalEngine engine) {
      long key = Math.min(i, j) * 4294967296L + Math.max(i, j);
      Integer index = midpoints.get(key);
      if (index != null) {
        return index;
      }
      coordinates.append(engine.evaluate(
          F.Times(F.C1D2, F.Plus(coordinates.get(i), coordinates.get(j)))));
      double[] a = points.get(i - 1);
      double[] b = points.get(j - 1);
      points.add(new double[] {(a[0] + b[0]) / 2.0, (a[1] + b[1]) / 2.0});
      int result = coordinates.argSize();
      midpoints.put(key, result);
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // coordinate charts
  // ---------------------------------------------------------------------------------------------

  /**
   * <code>CoordinateTransform("from" -> "to", p)</code> - the coordinates of the point
   * <code>p</code> (or of each point of a list) in the target chart.
   */
  private static final class CoordinateTransform extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr t = ast.arg1();
      IExpr p = ast.arg2();
      if (p.isVector() > 0) {
        return mapping(t, p.normal(false), engine);
      }
      if (p.isMatrix(false) != null) {
        IAST points = (IAST) p.normal(false);
        IASTAppendable result = F.ListAlloc(points.argSize());
        for (IExpr point : points) {
          IExpr mapped = mapping(t, point, engine);
          if (mapped.isNIL()) {
            return F.NIL;
          }
          result.append(mapped);
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /** The coordinates of <code>p</code> under the chart transformation <code>t</code>. */
  private static @AllowNIL IExpr mapping(IExpr t, IExpr p, EvalEngine engine) {
    if (t.isRuleAST() && t.first().equals(t.second())) {
      return p;
    }
    if (t.isRuleAST() && t.first().isString() && t.second().isString() && p.isList3()) {
      String from = t.first().toString();
      String to = t.second().toString();
      if (from.equals("Spherical") && to.equals("Cylindrical")) {
        // the azimuth is shared, the distance from the axis and the height are direct
        return engine.evaluate(F.list(F.Times(p.first(), F.Sin(p.second())), p.getAt(3),
            F.Times(p.first(), F.Cos(p.second()))));
      }
      if (from.equals("Cylindrical") && to.equals("Spherical")) {
        return engine.evaluate(F.list(F.Sqrt(F.Plus(F.Sqr(p.first()), F.Sqr(p.getAt(3)))),
            F.ArcTan(p.getAt(3), p.first()), p.second()));
      }
    }
    IExpr value = engine
        .evaluate(F.ternaryAST3(S.CoordinateTransformData, t, F.stringx("Mapping"), p));
    if (value.isAST(S.CoordinateTransformData) || !value.isList()) {
      if (t.isRuleAST() && t.first().isString() && t.second().isString()
          && !t.first().toString().equals("Cartesian")
          && !t.second().toString().equals("Cartesian")) {
        // go through the Cartesian coordinates
        IExpr cartesian =
            mapping(F.Rule(t.first(), F.stringx("Cartesian")), p, engine);
        if (cartesian.isPresent()) {
          return mapping(F.Rule(F.stringx("Cartesian"), t.second()), cartesian, engine);
        }
      }
      return F.NIL;
    }
    return value;
  }

  /**
   * The rotation from the orthonormal frame of a chart to the Cartesian axes in the chart's
   * coordinates <code>vars</code>.
   */
  private static @AllowNIL IAST frameRotation(String chart, IAST vars) {
    int n = vars.argSize();
    switch (chart) {
      case "Cartesian":
        return F.matrix((i, j) -> i == j ? F.C1 : F.C0, n, n);
      case "Polar":
        if (n == 2) {
          IExpr t = vars.arg2();
          return F.list(F.list(F.Cos(t), F.Negate(F.Sin(t))), F.list(F.Sin(t), F.Cos(t)));
        }
        return F.NIL;
      case "Cylindrical":
        if (n == 3) {
          IExpr t = vars.arg2();
          return F.list(F.list(F.Cos(t), F.Negate(F.Sin(t)), F.C0),
              F.list(F.Sin(t), F.Cos(t), F.C0), F.list(F.C0, F.C0, F.C1));
        }
        return F.NIL;
      case "Spherical":
        if (n == 3) {
          IExpr t = vars.arg2();
          IExpr p = vars.arg3();
          return F.list(F.list(F.Times(F.Sin(t), F.Cos(p)), F.Times(F.Cos(t), F.Cos(p)),
              F.Negate(F.Sin(p))),
              F.list(F.Times(F.Sin(t), F.Sin(p)), F.Times(F.Cos(t), F.Sin(p)), F.Cos(p)),
              F.list(F.Cos(t), F.Negate(F.Sin(t)), F.C0));
        }
        return F.NIL;
      default:
        return F.NIL;
    }
  }

  /** <code>Cos(ArcTan(a, b))</code>, <code>Sin(ArcTan(a, b))</code> as quotients. */
  private static @AllowNIL IExpr arcTanQuotients(IExpr expr) {
    return expr.replaceAll(e -> {
      if ((e.isCos() || e.isSin() || e.isTan()) && e.first().isAST(S.ArcTan, 3)) {
        IExpr a = e.first().first();
        IExpr b = e.first().second();
        IExpr r = F.Sqrt(F.Plus(F.Sqr(a), F.Sqr(b)));
        if (e.isCos()) {
          return F.Divide(a, r);
        }
        if (e.isSin()) {
          return F.Divide(b, r);
        }
        return F.Divide(b, a);
      }
      return F.NIL;
    }).orElse(expr);
  }

  /**
   * <code>TransformedField("from" -> "to", f, {x1, ...} -> {y1, ...})</code> - a scalar field
   * written in the new coordinates; a vector field (components in the orthonormal frame of the
   * chart) rotated into the frame of the new chart.
   */
  private static final class TransformedField extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr t = ast.arg1();
      IExpr f = ast.arg2();
      IExpr variables = ast.arg3();
      if (!t.isRuleAST() || !t.first().isString() || !t.second().isString()
          || !variables.isRuleAST() || !variables.first().isList()
          || !variables.second().isList()) {
        return F.NIL;
      }
      IAST oldVars = (IAST) variables.first();
      IAST newVars = (IAST) variables.second();
      int n = oldVars.argSize();
      if (n == 0 || newVars.argSize() != n) {
        return F.NIL;
      }
      for (int i = 1; i <= n; i++) {
        if (!oldVars.get(i).isSymbol() || !newVars.get(i).isSymbol()) {
          return F.NIL;
        }
      }
      String from = t.first().toString();
      String to = t.second().toString();
      IExpr inverse = mapping(F.Rule(t.second(), t.first()), newVars, engine);
      if (inverse.isNIL() || inverse.argSize() != n) {
        return F.NIL;
      }
      final IAST oldInNew = (IAST) inverse;
      java.util.function.Function<IExpr, IExpr> substitute = e -> {
        int position = oldVars.indexOf(e);
        return position > 0 ? oldInNew.get(position) : F.NIL;
      };
      IExpr result;
      if (f.isList()) {
        if (f.argSize() != n) {
          return F.NIL;
        }
        IAST fromFrame = frameRotation(from, oldVars);
        IAST toFrame = frameRotation(to, newVars);
        if (fromFrame.isNIL() || toFrame.isNIL()) {
          return F.NIL;
        }
        IExpr cartesian = engine.evaluate(F.Dot(fromFrame, f));
        cartesian = engine.evaluate(cartesian.replaceAll(substitute).orElse(cartesian));
        result = engine.evaluate(F.Dot(F.Transpose(toFrame), cartesian));
      } else {
        result = engine.evaluate(f.replaceAll(substitute).orElse(f));
      }
      result = engine.evaluate(arcTanQuotients(result));
      return engine.evaluate(F.Simplify(result));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private GeometricRegionFunctions() {}
}
