package org.matheclipse.core.eval.util;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * An unbounded line or plane <code>base + s*v1 (+ t*v2)</code> in a space of any dimension, read
 * from <code>InfiniteLine</code> or <code>InfinitePlane</code>, with the orthogonal projection onto
 * it and the points where a line or a segment meets it.
 *
 * <p>
 * All arithmetic is done on the expressions as written, so exact coordinates give exact results.
 * The nearest point and the distance are converted with <code>N</code> if one of the coordinates
 * is a machine number. A crossing point is <code>a + t*d</code> evaluated as it stands, so a
 * coordinate which no machine number enters stays exact.
 */
public final class AffineFlat {

  /** Relative size below which an inexact Gram determinant or residual counts as zero. */
  private static final double MACHINE_TOLERANCE = 1.0e-12;

  /** A point of the flat. */
  public final IAST base;

  /**
   * The vectors spanning the flat: one for a line, two for a plane, fewer for the span of a
   * degenerate one.
   */
  private final IAST[] directions;

  /** The entries of the Gram matrix of the directions. */
  private final IExpr[][] gram;

  /** The determinant of the Gram matrix, a nonzero number. */
  private final IExpr gramDeterminant;

  /**
   * A normal vector if the flat is a hyperplane of the plane or of space (a line in two, a plane in
   * three dimensions), otherwise <code>null</code>.
   */
  private final IAST normal;

  /** <code>normal . base</code>, the right hand side of the hyperplane equation. */
  private final IExpr offset;

  /**
   * A zero direction of a line or plane read by {@link #parseSpan(IExpr, EvalEngine)} which has no
   * nonzero direction, whose distances are indeterminate, otherwise <code>null</code>.
   */
  private final IAST zeroDirection;

  /** The dimension of the surrounding space. */
  public final int dimension;

  /** Whether one of the coordinates is a machine number. */
  public final boolean inexact;

  private AffineFlat(IAST base, IAST[] directions, IExpr[][] gram, IExpr gramDeterminant,
      boolean inexact, IAST zeroDirection, EvalEngine engine) {
    this.base = base;
    this.zeroDirection = zeroDirection;
    this.directions = directions;
    this.gram = gram;
    this.gramDeterminant = gramDeterminant;
    this.dimension = base.argSize();
    this.inexact = inexact;
    IAST n = null;
    if (dimension == 2 && directions.length == 1) {
      IAST v = directions[0];
      n = F.list(engine.evaluate(F.Negate(v.arg2())), v.arg1());
    } else if (dimension == 3 && directions.length == 2) {
      IExpr cross = engine.evaluate(F.Cross(directions[0], directions[1]));
      n = cross.isList() && cross.argSize() == 3 ? (IAST) cross : null;
    }
    this.normal = n;
    this.offset = n == null ? F.NIL : normalDot(n, base, engine);
  }

  /**
   * The number of directions spanning the flat: <code>1</code> for a line, <code>2</code> for a
   * plane.
   */
  public int flatDimension() {
    return directions.length;
  }

  /** The <code>i</code>-th vector spanning the flat, counted from <code>0</code>. */
  public IAST direction(int i) {
    return directions[i];
  }

  /**
   * Read <code>InfiniteLine({p1, p2})</code>, <code>InfiniteLine(p, v)</code>,
   * <code>InfinitePlane({p1, p2, p3})</code> and <code>InfinitePlane(p, {v1, v2})</code>.
   *
   * @return <code>null</code> if <code>region</code> is none of these forms or is degenerate
   *         (coincident points, a zero direction, parallel directions, or a Gram determinant which
   *         can't be decided to be nonzero)
   */
  public static AffineFlat parse(IExpr region, EvalEngine engine) {
    return parse(region, false, engine);
  }

  /**
   * Like {@link #parse(IExpr, EvalEngine)}, but a degenerate line or plane is read as the span of
   * what it is made of: a zero direction is dropped, and so is a second direction parallel to the
   * first. A degenerate line is then its base point, a degenerate plane a line or a point.
   */
  public static AffineFlat parseSpan(IExpr region, EvalEngine engine) {
    return parse(region, true, engine);
  }

  private static AffineFlat parse(IExpr region, boolean span, EvalEngine engine) {
    IAST base;
    IAST[] directions;
    if (region.isAST(S.InfiniteLine, 2)) {
      IExpr points = region.first();
      if (!points.isListOfLists() || points.argSize() != 2) {
        return null;
      }
      base = (IAST) points.first();
      directions = new IAST[] {difference(points.second(), base, engine)};
    } else if (region.isAST(S.InfiniteLine, 3)) {
      if (!region.first().isList() || region.first().isListOfLists()
          || !region.second().isList()) {
        return null;
      }
      base = (IAST) region.first();
      directions = new IAST[] {(IAST) region.second()};
    } else if (region.isAST(S.InfinitePlane, 2)) {
      IExpr points = region.first();
      if (!points.isListOfLists() || points.argSize() != 3) {
        return null;
      }
      base = (IAST) points.first();
      directions = new IAST[] {difference(points.second(), base, engine),
          difference(points.getAt(3), base, engine)};
    } else if (region.isAST(S.InfinitePlane, 3)) {
      IExpr vectors = region.second();
      if (!region.first().isList() || region.first().isListOfLists()
          || !vectors.isListOfLists() || vectors.argSize() != 2) {
        return null;
      }
      base = (IAST) region.first();
      directions = new IAST[] {(IAST) vectors.first(), (IAST) vectors.second()};
    } else {
      return null;
    }
    int dimension = base.argSize();
    // the flat must fit into the space around it
    if (dimension < directions.length) {
      return null;
    }
    boolean[] inexact = new boolean[1];
    if (!isPoint(base, dimension, inexact)) {
      return null;
    }
    for (IAST direction : directions) {
      if (direction == null || !isPoint(direction, dimension, inexact)) {
        return null;
      }
    }
    // the Gram matrix of all directions, whose leading minors over the kept directions decide
    // whether the next direction is independent of them
    IExpr[][] all = gram(directions, engine);
    int[] kept = new int[directions.length];
    int keptCount = 0;
    IExpr determinant = F.C1;
    for (int i = 0; i < directions.length; i++) {
      IExpr minor;
      double scale;
      if (keptCount == 0) {
        minor = all[i][i];
        scale = all[i][i].evalfNaN();
      } else {
        int j = kept[0];
        minor = engine.evaluate(F.Subtract(F.Times(all[j][j], all[i][i]), F.Sqr(all[j][i])));
        scale = all[j][j].evalfNaN() * all[i][i].evalfNaN();
      }
      Boolean independent = independent(minor, scale);
      if (independent == null) {
        return null;
      }
      if (!independent) {
        if (!span) {
          return null;
        }
        continue;
      }
      kept[keptCount++] = i;
      determinant = minor;
    }
    // a line or plane through a point with only zero directions has no direction to measure along
    IAST zeroDirection = keptCount == 0 ? directions[0] : null;
    IAST[] keptDirections = new IAST[keptCount];
    IExpr[][] gram = new IExpr[keptCount][keptCount];
    for (int i = 0; i < keptCount; i++) {
      keptDirections[i] = directions[kept[i]];
      for (int j = 0; j < keptCount; j++) {
        gram[i][j] = all[kept[i]][kept[j]];
      }
    }
    return new AffineFlat(base, keptDirections, gram, determinant, inexact[0], zeroDirection,
        engine);
  }

  private static IExpr[][] gram(IAST[] directions, EvalEngine engine) {
    int n = directions.length;
    IExpr[][] gram = new IExpr[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = i; j < n; j++) {
        gram[i][j] = engine.evaluate(F.Dot(directions[i], directions[j]));
        gram[j][i] = gram[i][j];
      }
    }
    return gram;
  }

  /**
   * Whether a direction is independent of the ones before it, decided from the leading minor of
   * the Gram matrix it adds.
   *
   * @param scale the product of the diagonal entries of that minor, the size a machine number
   *        minor is compared with
   * @return <code>null</code> if the minor isn't a number
   */
  private static Boolean independent(IExpr minor, double scale) {
    if (!minor.isNumber()) {
      return null;
    }
    if (minor.isZero()) {
      return Boolean.FALSE;
    }
    if (minor.isInexactNumber()) {
      return Math.abs(minor.evalfNaN()) > MACHINE_TOLERANCE * scale;
    }
    return Boolean.TRUE;
  }

  /**
   * Whether <code>point</code> is a list of <code>dimension</code> coordinates, which may be
   * symbolic but not lists or non-real numbers.
   *
   * @param inexact the first entry is set to <code>true</code> if one of them contains a machine
   *        number
   */
  public static boolean isPoint(IExpr point, int dimension, boolean[] inexact) {
    if (!point.isList() || point.argSize() != dimension) {
      return false;
    }
    for (int i = 1; i <= dimension; i++) {
      IExpr x = point.getAt(i);
      if (x.isList() || (x.isNumber() && !x.isReal())) {
        return false;
      }
      if (x.has(y -> y.isInexactNumber(), false)) {
        inexact[0] = true;
      }
    }
    return true;
  }

  /**
   * Whether <code>point</code> is a list of <code>dimension</code> real numeric values.
   *
   * @param inexact the first entry is set to <code>true</code> if one of them is a machine number
   */
  public static boolean isRealPoint(IExpr point, int dimension, boolean[] inexact) {
    if (!isPoint(point, dimension, inexact)) {
      return false;
    }
    return ((IAST) point).forAll(x -> x.isReal() || (x.isNumericFunction() && x.isRealResult()));
  }

  /** Whether all coordinates of the flat are real numeric values. */
  public boolean isReal() {
    boolean[] ignored = new boolean[1];
    if (!isRealPoint(base, dimension, ignored)) {
      return false;
    }
    for (IAST direction : directions) {
      if (!isRealPoint(direction, dimension, ignored)) {
        return false;
      }
    }
    return true;
  }

  private static IAST difference(IExpr p, IAST q, EvalEngine engine) {
    if (!p.isList() || p.argSize() != q.argSize()) {
      return null;
    }
    IExpr d = engine.evaluate(F.Subtract(p, q));
    return d.isList() ? (IAST) d : null;
  }

  /** The component of <code>vector</code> in the span of the directions. */
  private IExpr linearProjection(IExpr vector, EvalEngine engine) {
    switch (directions.length) {
      case 0:
        return engine.evaluate(F.Times(F.C0, vector));
      case 1: {
        IExpr c = F.Divide(F.Dot(vector, directions[0]), gramDeterminant);
        return engine.evaluate(F.Times(c, directions[0]));
      }
      default: {
        IExpr b1 = engine.evaluate(F.Dot(vector, directions[0]));
        IExpr b2 = engine.evaluate(F.Dot(vector, directions[1]));
        // Cramer's rule for the Gram system G.c == {b1, b2}
        IExpr c1 = F.Divide(F.Subtract(F.Times(gram[1][1], b1), F.Times(gram[0][1], b2)),
            gramDeterminant);
        IExpr c2 = F.Divide(F.Subtract(F.Times(gram[0][0], b2), F.Times(gram[0][1], b1)),
            gramDeterminant);
        return engine.evaluate(F.Plus(F.Times(c1, directions[0]), F.Times(c2, directions[1])));
      }
    }
  }

  /** The component of <code>vector</code> orthogonal to the flat. */
  private IExpr linearResidual(IExpr vector, EvalEngine engine) {
    return engine.evaluate(F.Subtract(vector, linearProjection(vector, engine)));
  }

  /**
   * The point of the flat nearest to <code>point</code>, its orthogonal projection, or the list of
   * nearest points to a list of points.
   *
   * @return <code>F.NIL</code> if <code>point</code> isn't a point or a list of points of the
   *         flat's dimension
   */
  public IExpr nearest(IExpr point, EvalEngine engine) {
    return mapPoints(point, false, engine);
  }

  /**
   * The distance from <code>point</code> to the flat, or the list of distances of a list of points.
   *
   * @return <code>F.NIL</code> if <code>point</code> isn't a point or a list of points of the
   *         flat's dimension
   */
  public IExpr distance(IExpr point, EvalEngine engine) {
    return mapPoints(point, true, engine);
  }

  private IExpr mapPoints(IExpr point, boolean distance, EvalEngine engine) {
    if (point.isListOfLists()) {
      IASTAppendable result = F.ListAlloc(point.argSize());
      for (int i = 1; i <= point.argSize(); i++) {
        IExpr value = nearestOrDistance(point.getAt(i), distance, engine);
        if (value.isNIL()) {
          return F.NIL;
        }
        result.append(value);
      }
      return result;
    }
    return nearestOrDistance(point, distance, engine);
  }

  private IExpr nearestOrDistance(IExpr point, boolean distance, EvalEngine engine) {
    boolean[] inexactPoint = new boolean[] {inexact};
    if (!isPoint(point, dimension, inexactPoint)) {
      return F.NIL;
    }
    if (zeroDirection != null) {
      // the parameter of the projection is 0/0, which reports the division and gives
      // Indeterminate
      return engine.evaluate(F.Divide(F.Dot(F.Subtract(point, base), zeroDirection),
          F.Dot(zeroDirection, zeroDirection)));
    }
    IExpr result = project(point, engine);
    if (distance) {
      IExpr difference = engine.evaluate(F.Subtract(point, result));
      // for machine numbers the square root of the sum of squares, rounded once
      result = inexactPoint[0] ? engine.evaluate(F.Sqrt(F.N(F.Dot(difference, difference))))
          : engine.evaluate(F.Norm(difference));
    }
    return inexactPoint[0] ? engine.evaluate(F.N(result)) : result;
  }

  private IExpr project(IExpr point, EvalEngine engine) {
    IExpr offsetVector = engine.evaluate(F.Subtract(point, base));
    return engine.evaluate(F.Plus(base, linearProjection(offsetVector, engine)));
  }

  /** Whether <code>point</code> lies on the flat. */
  private boolean contains(IExpr point, EvalEngine engine) {
    return S.Equal.ofQ(engine, point, project(point, engine));
  }

  /** What a line or segment has in common with the flat. */
  public enum Crossing {
    /** No common point. */
    NONE,
    /** One common point. */
    POINT,
    /** The line or segment lies in the flat. */
    CONTAINED,
    /** Not decidable from the coordinates. */
    UNKNOWN
  }

  /**
   * The point where <code>start + t*direction</code> meets the flat, for any real <code>t</code>
   * or, for a segment, <code>0 &lt;= t &lt;= 1</code>.
   *
   * @param found receives the common point if there is exactly one
   */
  public Crossing crossing(IExpr start, IExpr direction, boolean segment, EvalEngine engine,
      IExpr[] found) {
    IExpr t;
    if (normal != null) {
      // a hyperplane n.x == c: t == (c - n.start) / (n.direction)
      IExpr denominator = normalDot(normal, direction, engine);
      if (!denominator.isReal()) {
        return Crossing.UNKNOWN;
      }
      if (isZero(denominator, normal, direction, engine)) {
        return contains(start, engine) ? Crossing.CONTAINED : Crossing.NONE;
      }
      t = engine.evaluate(
          F.Divide(F.Subtract(offset, normalDot(normal, start, engine)), denominator));
    } else {
      // the distance vector from the flat is affine in t: rStart + t*rDirection
      IExpr rStart = engine.evaluate(F.Subtract(start, project(start, engine)));
      IExpr rDirection = linearResidual(direction, engine);
      IExpr squared = engine.evaluate(F.Dot(rDirection, rDirection));
      if (!squared.isReal()) {
        return Crossing.UNKNOWN;
      }
      boolean parallel = squared.isZero();
      if (!parallel && squared.isInexactNumber()) {
        double length = engine.evaluate(F.Dot(direction, direction)).evalfNaN();
        parallel = !(squared.evalfNaN() > MACHINE_TOLERANCE * length);
      }
      if (parallel) {
        return contains(start, engine) ? Crossing.CONTAINED : Crossing.NONE;
      }
      t = engine.evaluate(F.Negate(F.Divide(F.Dot(rStart, rDirection), squared)));
    }
    if (!t.isReal()) {
      return Crossing.UNKNOWN;
    }
    if (segment) {
      if (t.isInexactNumber()) {
        // a crossing at an end of the segment may be computed just outside of it
        double value = t.evalfNaN();
        if (value < 0.0 && value >= -MACHINE_TOLERANCE) {
          t = F.CD0;
        } else if (value > 1.0 && value <= 1.0 + MACHINE_TOLERANCE) {
          t = F.CD1;
        }
      }
      if (!S.LessEqual.ofQ(engine, F.C0, t, F.C1)) {
        return Crossing.NONE;
      }
    }
    IExpr point = engine.evaluate(F.Plus(start, F.Times(t, direction)));
    if (normal == null && !contains(point, engine)) {
      // a line which passes the flat at a distance, possible in more than two dimensions
      return Crossing.NONE;
    }
    found[0] = point;
    return Crossing.POINT;
  }

  /**
   * <code>n.v</code> with the terms of the exactly zero components of <code>n</code> left out, so
   * that a machine number in such a coordinate of <code>v</code> doesn't make the product inexact.
   */
  private static IExpr normalDot(IAST n, IExpr v, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(n.argSize());
    for (int i = 1; i <= n.argSize(); i++) {
      IExpr component = n.get(i);
      if (component.isZero() && !component.isInexactNumber()) {
        continue;
      }
      sum.append(F.Times(component, v.getAt(i)));
    }
    return engine.evaluate(sum.oneIdentity0());
  }

  /** Whether <code>point</code> lies on the segment <code>{a, b}</code>. */
  private static boolean onSegment(IExpr point, IAST segment, EvalEngine engine) {
    IExpr a = segment.arg1();
    IExpr d = engine.evaluate(F.Subtract(segment.arg2(), a));
    IExpr squared = engine.evaluate(F.Dot(d, d));
    if (squared.isZero()) {
      return S.Equal.ofQ(engine, point, a);
    }
    IExpr t = engine.evaluate(F.Divide(F.Dot(F.Subtract(point, a), d), squared));
    return t.isReal() && S.LessEqual.ofQ(engine, F.C0, t, F.C1)
        && S.Equal.ofQ(engine, point, F.Plus(a, F.Times(t, d)));
  }

  /**
   * Whether the product <code>u.v</code> is zero, for machine numbers relative to the lengths of
   * <code>u</code> and <code>v</code>.
   */
  private static boolean isZero(IExpr dot, IExpr u, IExpr v, EvalEngine engine) {
    if (dot.isZero()) {
      return true;
    }
    if (!dot.isInexactNumber()) {
      return false;
    }
    double scale = Math.sqrt(engine.evaluate(F.Dot(u, u)).evalfNaN()
        * engine.evaluate(F.Dot(v, v)).evalfNaN());
    return !(Math.abs(dot.evalfNaN()) > MACHINE_TOLERANCE * scale);
  }

  /**
   * What the polyline through <code>points</code> has in common with the flat: the points where
   * it crosses, in the order of its segments and a corner found in two adjacent segments only
   * once, as <code>Point({p1, p2, ...})</code>; <code>polyline</code> itself if all of it lies in
   * the flat; <code>Line({{a1, b1}, {a2, b2}, ...})</code>, the segments lying in the flat in the
   * order of the polyline, if every other crossing point lies on one of them; or
   * <code>empty</code>.
   *
   * @return <code>F.NIL</code> if a crossing point lies apart from the segments in the flat or a
   *         crossing can't be decided
   */
  public IExpr polylineIntersection(IExpr polyline, IAST points, IExpr empty,
      EvalEngine engine) {
    List<IExpr> result = new ArrayList<IExpr>();
    IExpr[] found = new IExpr[1];
    IASTAppendable contained = F.ListAlloc();
    // the segment the last crossing point was found in
    int lastSegment = -1;
    for (int i = 1; i < points.argSize(); i++) {
      engine.checkTimeLimit();
      IExpr start = points.get(i);
      IExpr direction = engine.evaluate(F.Subtract(points.get(i + 1), start));
      switch (crossing(start, direction, true, engine, found)) {
        case NONE:
          continue;
        case POINT:
          IExpr point = found[0];
          // a crossing at the corner shared with the previous segment is found in both
          if (lastSegment != i - 1
              || !S.Equal.ofQ(engine, result.get(result.size() - 1), point)) {
            result.add(point);
          }
          lastSegment = i;
          continue;
        case CONTAINED:
          contained.append(F.list(start, points.get(i + 1)));
          continue;
        default:
          return F.NIL;
      }
    }
    if (contained.argSize() > 0) {
      if (contained.argSize() == points.argSize() - 1) {
        return polyline;
      }
      for (IExpr point : result) {
        if (!contained.exists(segment -> onSegment(point, (IAST) segment, engine))) {
          // isolated crossing points next to segments in the flat
          return F.NIL;
        }
      }
      return F.unaryAST1(S.Line, contained);
    }
    if (result.isEmpty()) {
      return empty;
    }
    return F.unaryAST1(S.Point, F.ListAlloc(result));
  }

  /**
   * What the line <code>line</code> has in common with the flat: <code>Point(p)</code> for one
   * crossing point, <code>line</code> itself if it lies in the flat, or <code>empty</code>.
   *
   * @return <code>F.NIL</code> if it can't be decided
   */
  public IExpr lineIntersection(IExpr line, AffineFlat parsedLine, IExpr empty,
      EvalEngine engine) {
    IExpr[] found = new IExpr[1];
    switch (crossing(parsedLine.base, parsedLine.directions[0], false, engine, found)) {
      case NONE:
        return empty;
      case POINT:
        return F.unaryAST1(S.Point, found[0]);
      case CONTAINED:
        return line;
      default:
        return F.NIL;
    }
  }

  /**
   * The line two planes of space have in common, as <code>InfiniteLine(p, d)</code> with the
   * direction <code>d = n1 x n2</code> and <code>p</code> the point of the line nearest to the
   * origin; <code>plane</code> if they are the same plane; or <code>empty</code> for parallel
   * planes.
   *
   * @return <code>F.NIL</code> if one of them is no plane of space or it can't be decided
   */
  public IExpr planeIntersection(IExpr plane, AffineFlat other, IExpr empty, EvalEngine engine) {
    if (normal == null || other.normal == null || directions.length != 2
        || other.directions.length != 2) {
      return F.NIL;
    }
    IExpr d = engine.evaluate(F.Cross(normal, other.normal));
    IExpr squared = engine.evaluate(F.Dot(d, d));
    if (!d.isList() || !squared.isReal()) {
      return F.NIL;
    }
    boolean parallel = squared.isZero();
    if (!parallel && squared.isInexactNumber()) {
      double scale = engine.evaluate(F.Dot(normal, normal)).evalfNaN()
          * engine.evaluate(F.Dot(other.normal, other.normal)).evalfNaN();
      parallel = !(squared.evalfNaN() > MACHINE_TOLERANCE * scale);
    }
    if (parallel) {
      return contains(other.base, engine) ? plane : empty;
    }
    // h1 (n2 x d) + h2 (d x n1), divided by |d|^2, lies on both planes and is orthogonal to d
    IExpr p = engine.evaluate(F.Divide(F.Plus(F.Times(offset, F.Cross(other.normal, d)),
        F.Times(other.offset, F.Cross(d, normal))), squared));
    return F.binaryAST2(S.InfiniteLine, p, d);
  }
}
