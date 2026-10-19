package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.RegionPrimitives;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>RegionIntersection(reg1, reg2, ...)</code> - the points which lie in every one of the
 * regions.
 *
 * <p>
 * The intersection is worked out where it is certainly known - regions that are the same, an empty
 * region among them, boxes, concentric balls, formula regions over the same variables - and is
 * otherwise left standing as it was written. That is not a failure: the unevaluated form is itself
 * a region, and {@link RegionMember} reads it as the conjunction of its parts, so
 * <code>RegionMember(RegionIntersection(d1, d2), p)</code> answers whether <code>p</code> lies in
 * both whether or not the two disks can be combined into one shape.
 *
 * <p>
 * Every part must live in the same space, as the reference implementation requires: an intersection
 * of a disk and a ball is no region at all, and stays unevaluated.
 */
public class RegionIntersection extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 0) {
      return F.NIL;
    }
    List<IExpr> regions = new ArrayList<IExpr>();
    flatten(ast, regions);

    IExpr intervals = intervals(regions, engine);
    if (intervals.isPresent()) {
      return intervals;
    }

    int embeddingDimension =
        BooleanRegions.embeddingDimension(S.RegionIntersection, regions, engine);
    if (embeddingDimension < 1) {
      return F.NIL;
    }
    IExpr empty = F.unaryAST1(S.EmptyRegion, F.ZZ(embeddingDimension));
    for (int i = 0; i < regions.size(); i++) {
      IExpr region = regions.get(i);
      if (region.isAST(S.EmptyRegion, 2)) {
        // nothing lies in the empty region, so nothing lies in the intersection
        return empty;
      }
      if (region.isAST(S.FullRegion, 2)) {
        // everything lies in the full region, so it constrains nothing
        regions.remove(i--);
        continue;
      }
      for (int j = 0; j < i; j++) {
        if (regions.get(j).equals(region)) {
          // a region intersected with itself is itself
          regions.remove(i--);
          break;
        }
      }
    }
    if (regions.isEmpty()) {
      return F.unaryAST1(S.FullRegion, F.ZZ(embeddingDimension));
    }
    if (regions.size() == 1) {
      return regions.get(0);
    }

    IExpr combined = boxes(regions, embeddingDimension, empty);
    if (combined.isNIL()) {
      combined = segments(regions, empty, engine);
    }
    if (combined.isNIL()) {
      combined = concentricBalls(regions);
    }
    if (combined.isNIL()) {
      combined = formulaRegions(regions);
    }
    if (combined.isNIL() && apart(regions, engine)) {
      combined = empty;
    }
    if (combined.isPresent()) {
      return combined;
    }
    // nothing here can be written as one shape, so the intersection is carried as what it is:
    // the regions, and the condition a point has to meet to lie in all of them
    return booleanRegion(regions);
  }

  /**
   * <code>BooleanRegion(#1 && #2 && ... &, {reg1, reg2, ...})</code> - the form the reference
   * implementation gives an intersection it cannot draw as a single shape.
   */
  private static IExpr booleanRegion(List<IExpr> regions) {
    return BooleanRegions.of(regions, bodies -> BooleanRegions.junction(S.And, bodies));
  }

  /** Intervals of the line, which have an intersection of their own. */
  private static IExpr intervals(List<IExpr> regions, EvalEngine engine) {
    for (IExpr region : regions) {
      if (!region.isAST(S.Interval)) {
        return F.NIL;
      }
    }
    return engine.evaluate(F.ast(regions.toArray(new IExpr[0]), S.IntervalIntersection));
  }

  /**
   * Two lines <code>Line({p1, p2, ...})</code> of real points: the points where their segments
   * cross, or the empty region if they miss each other. Segments which overlap along a common line
   * are left to the other methods.
   */
  private static IExpr segments(List<IExpr> regions, IExpr empty, EvalEngine engine) {
    if (regions.size() != 2) {
      return F.NIL;
    }
    IAST[] lines = new IAST[2];
    int dimension = -1;
    for (int i = 0; i < 2; i++) {
      IExpr line = regions.get(i);
      if (!line.isAST(S.Line, 2) || !line.first().isList() || line.first().argSize() < 2) {
        return F.NIL;
      }
      IAST points = (IAST) line.first();
      for (IExpr point : points) {
        if (!point.isList() || !((IAST) point).forAll(x -> x.isReal())) {
          return F.NIL;
        }
        if (dimension < 0) {
          dimension = point.argSize();
        } else if (point.argSize() != dimension) {
          return F.NIL;
        }
      }
      lines[i] = points;
    }
    if (lines[0].argSize() == 2 && lines[1].argSize() == 2) {
      IExpr point = segmentPair((IAST) lines[0].arg1(), (IAST) lines[0].arg2(),
          (IAST) lines[1].arg1(), (IAST) lines[1].arg2(), engine);
      if (point == null || point.isNIL()) {
        return point == null ? empty : F.NIL;
      }
      return F.unaryAST1(S.Point, point);
    }
    List<IExpr> found = new java.util.ArrayList<IExpr>();
    double[][] first = machinePoints(lines[0]);
    double[][] second = machinePoints(lines[1]);
    for (int i = 1; i < lines[0].argSize(); i++) {
      engine.checkTimeLimit();
      for (int j = 1; j < lines[1].argSize(); j++) {
        if (apartSegments(first[i - 1], first[i], second[j - 1], second[j])) {
          // their bounding boxes don't meet
          continue;
        }
        IExpr point = segmentPair((IAST) lines[0].get(i), (IAST) lines[0].get(i + 1),
            (IAST) lines[1].get(j), (IAST) lines[1].get(j + 1), engine);
        if (point == null) {
          continue;
        }
        if (point.isNIL()) {
          return F.NIL;
        }
        // a crossing at a corner of a line is found in both of its segments
        if (!found.stream().anyMatch(p -> S.Equal.ofQ(engine, p, point))) {
          found.add(point);
        }
      }
    }
    if (found.isEmpty()) {
      return empty;
    }
    if (found.size() == 1) {
      return F.unaryAST1(S.Point, found.get(0));
    }
    IASTAppendable list = F.ListAlloc(found);
    return F.unaryAST1(S.Point, engine.evaluate(F.Sort(list)));
  }

  /** The points of a line as machine numbers. */
  private static double[][] machinePoints(IAST points) {
    double[][] result = new double[points.argSize()][];
    for (int i = 1; i < points.size(); i++) {
      IAST point = (IAST) points.get(i);
      result[i - 1] = new double[point.argSize()];
      for (int d = 1; d < point.size(); d++) {
        result[i - 1][d - 1] = point.get(d).evalfNaN();
      }
    }
    return result;
  }

  /**
   * Whether the bounding boxes of the segments <code>p1 q1</code> and <code>p2 q2</code> lie apart
   * in some coordinate by more than the rounding of machine numbers.
   */
  private static boolean apartSegments(double[] p1, double[] q1, double[] p2, double[] q2) {
    for (int d = 0; d < p1.length; d++) {
      double lo1 = Math.min(p1[d], q1[d]);
      double hi1 = Math.max(p1[d], q1[d]);
      double lo2 = Math.min(p2[d], q2[d]);
      double hi2 = Math.max(p2[d], q2[d]);
      double slack = 1.0e-9 * (1.0 + Math.max(Math.abs(hi1), Math.abs(hi2)));
      if (hi1 + slack < lo2 || hi2 + slack < lo1) {
        return true;
      }
    }
    return false;
  }

  /**
   * The point where the segment from <code>p1</code> to <code>q1</code> meets the one from
   * <code>p2</code> to <code>q2</code>.
   *
   * @return the point, {@code null} if they don't meet, or {@link F#NIL} if they overlap along a
   *         common line
   */
  private static IExpr segmentPair(IAST p1, IAST q1, IAST p2, IAST q2, EvalEngine engine) {
    IExpr d1 = engine.evaluate(F.Subtract(q1, p1));
    IExpr d2 = engine.evaluate(F.Subtract(q2, p2));
    IExpr w = engine.evaluate(F.Subtract(p2, p1));
    // p1 + s d1 == p2 + t d2 in the least squares sense: the normal equations of s and t
    IExpr a11 = engine.evaluate(F.Dot(d1, d1));
    IExpr a12 = engine.evaluate(F.Dot(d1, d2));
    IExpr a22 = engine.evaluate(F.Dot(d2, d2));
    IExpr b1 = engine.evaluate(F.Dot(w, d1));
    IExpr b2 = engine.evaluate(F.Dot(w, d2));
    IExpr det = engine.evaluate(F.Subtract(F.Sqr(a12), F.Times(a11, a22)));
    if (!det.isReal() || a11.isZero() || a22.isZero()) {
      return F.NIL;
    }
    if (det.isZero()) {
      // parallel segments meet only if they lie on one line
      IExpr offset = engine.evaluate(F.Subtract(F.Times(F.Dot(w, w), a11), F.Sqr(b1)));
      return offset.isReal() && !offset.isZero() ? null : F.NIL;
    }
    // a11 s - a12 t == b1, a12 s - a22 t == b2
    IExpr s = engine.evaluate(F.Divide(F.Subtract(F.Times(a12, b2), F.Times(a22, b1)), det));
    IExpr t = engine.evaluate(F.Divide(F.Subtract(F.Times(a11, b2), F.Times(a12, b1)), det));
    if (!s.isReal() || !t.isReal()) {
      return F.NIL;
    }
    IExpr point = engine.evaluate(F.Plus(p1, F.Times(s, d1)));
    IExpr other = engine.evaluate(F.Plus(p2, F.Times(t, d2)));
    boolean inside = S.LessEqual.ofQ(engine, F.C0, s, F.C1) && S.LessEqual.ofQ(engine, F.C0, t, F.C1);
    if (!inside || !S.Equal.ofQ(engine, point, other)) {
      // they miss each other, or are skew lines in space
      return null;
    }
    return point;
  }

  /**
   * Whether two of the regions lie apart from one another, which makes the intersection empty.
   *
   * <p>
   * It is read off the boxes the regions are bounded by: regions whose bounds do not overlap in
   * some coordinate cannot share a point. Bounds that do overlap say nothing either way - two
   * disks may still miss each other - so this only ever finds an emptiness, never rules one out.
   */
  private static boolean apart(List<IExpr> regions, EvalEngine engine) {
    double[][][] bounds = new double[regions.size()][][];
    for (int i = 0; i < regions.size(); i++) {
      IExpr box = engine.evaluate(F.unaryAST1(S.RegionBounds, regions.get(i)));
      if (!box.isListOfLists()) {
        return false;
      }
      IAST boxList = (IAST) box;
      bounds[i] = new double[boxList.argSize()][2];
      for (int j = 1; j <= boxList.argSize(); j++) {
        IExpr range = boxList.get(j);
        if (!range.isList2()) {
          return false;
        }
        bounds[i][j - 1][0] = range.first().evalfNaN();
        bounds[i][j - 1][1] = range.second().evalfNaN();
        if (!Double.isFinite(bounds[i][j - 1][0]) || !Double.isFinite(bounds[i][j - 1][1])) {
          return false;
        }
      }
    }
    for (int i = 0; i < bounds.length; i++) {
      for (int j = i + 1; j < bounds.length; j++) {
        if (bounds[i].length != bounds[j].length) {
          return false;
        }
        for (int k = 0; k < bounds[i].length; k++) {
          if (bounds[i][k][1] < bounds[j][k][0] || bounds[j][k][1] < bounds[i][k][0]) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * The regions to intersect, with a nested <code>RegionIntersection</code> taken apart and a
   * <code>Region</code> wrapper removed.
   *
   * @return whether anything was unwrapped
   */
  private static boolean flatten(IAST ast, List<IExpr> regions) {
    boolean changed = false;
    for (int i = 1; i < ast.size(); i++) {
      IExpr region = ast.get(i);
      if (region.isAST(S.Region, 2)) {
        region = region.first();
        changed = true;
      }
      if (region.isAST(S.RegionIntersection)) {
        // intersecting is associative, so the parts of a part are parts
        changed |= flatten((IAST) region, regions);
        changed = true;
        continue;
      }
      if (BooleanRegions.isJunction(region, S.And)) {
        // an intersection that was already carried as a BooleanRegion asks for all of its parts,
        // so it is taken apart here rather than nested
        flatten((IAST) region.second(), regions);
        changed = true;
        continue;
      }
      regions.add(region);
    }
    return changed;
  }

  /**
   * The overlap of axis aligned boxes, which is the box between the largest of their lower corners
   * and the smallest of their upper corners - or nothing, when those cross over in any coordinate.
   *
   * <p>
   * Only numeric corners are combined. A symbolic one would need <code>Max</code> and
   * <code>Min</code> expressions as corners, and there would be no telling whether the result is
   * empty.
   */
  private static IExpr boxes(List<IExpr> regions, int embeddingDimension, IExpr empty) {
    double[] lower = new double[embeddingDimension];
    double[] upper = new double[embeddingDimension];
    // the corner as it was written, so that exact input keeps exact corners
    IExpr[] lowerExpr = new IExpr[embeddingDimension];
    IExpr[] upperExpr = new IExpr[embeddingDimension];
    java.util.Arrays.fill(lower, Double.NEGATIVE_INFINITY);
    java.util.Arrays.fill(upper, Double.POSITIVE_INFINITY);
    // a box keeps the head it was written with: a Cuboid of the plane stays a Cuboid
    IExpr head = regions.get(0).head();
    for (IExpr region : regions) {
      if (!region.isAST(S.Rectangle) && !region.isAST(S.Cuboid)) {
        return F.NIL;
      }
      IAST corners = RegionPrimitives.boxCorners((IAST) region);
      if (corners.isNIL() || corners.arg1().argSize() != embeddingDimension) {
        return F.NIL;
      }
      for (int i = 1; i <= embeddingDimension; i++) {
        IExpr lowEntry = ((IAST) corners.arg1()).get(i);
        IExpr highEntry = ((IAST) corners.arg2()).get(i);
        double low = lowEntry.evalfNaN();
        double high = highEntry.evalfNaN();
        if (!Double.isFinite(low) || !Double.isFinite(high)) {
          return F.NIL;
        }
        if (low > high) {
          // a box written from its far corner back to its near one
          IExpr swap = lowEntry;
          lowEntry = highEntry;
          highEntry = swap;
          double swapValue = low;
          low = high;
          high = swapValue;
        }
        if (low > lower[i - 1]) {
          lower[i - 1] = low;
          lowerExpr[i - 1] = lowEntry;
        }
        if (high < upper[i - 1]) {
          upper[i - 1] = high;
          upperExpr[i - 1] = highEntry;
        }
      }
    }
    IASTAppendable lowerCorner = F.ListAlloc(embeddingDimension);
    IASTAppendable upperCorner = F.ListAlloc(embeddingDimension);
    for (int i = 0; i < embeddingDimension; i++) {
      if (lower[i] > upper[i]) {
        // the boxes do not overlap in this coordinate, so they do not overlap at all
        return empty;
      }
      lowerCorner.append(lowerExpr[i]);
      upperCorner.append(upperExpr[i]);
    }
    return F.binaryAST2(head, lowerCorner, upperCorner);
  }

  /** Balls or disks about one centre: the smallest of them lies inside all the others. */
  private static IExpr concentricBalls(List<IExpr> regions) {
    IExpr centre = F.NIL;
    IExpr smallest = F.NIL;
    double smallestRadius = Double.POSITIVE_INFINITY;
    for (IExpr region : regions) {
      if (!region.isAST(S.Disk, 3) && !region.isAST(S.Ball, 3)) {
        return F.NIL;
      }
      IAST ball = (IAST) region;
      if (centre.isNIL()) {
        centre = ball.arg1();
      } else if (!centre.equals(ball.arg1())) {
        return F.NIL;
      }
      double radius = ball.arg2().evalfNaN();
      if (!Double.isFinite(radius) || radius < 0.0) {
        return F.NIL;
      }
      if (radius < smallestRadius) {
        smallestRadius = radius;
        smallest = region;
      }
    }
    return smallest;
  }

  /**
   * Formula regions over the same variables, which is the one region their conditions both hold in
   * - the shape the reference implementation gives for an intersection of
   * <code>ImplicitRegion</code>s.
   */
  private static IExpr formulaRegions(List<IExpr> regions) {
    IExpr variables = F.NIL;
    IASTAppendable conditions = F.ast(S.And, regions.size());
    for (IExpr region : regions) {
      if (!region.isAST(S.ImplicitRegion, 3)) {
        return F.NIL;
      }
      IAST implicit = (IAST) region;
      if (variables.isNIL()) {
        variables = implicit.arg2();
      } else if (!variables.equals(implicit.arg2())) {
        // different variables, or the same ones bounded differently: not one formula
        return F.NIL;
      }
      conditions.append(implicit.arg1());
    }
    return F.binaryAST2(S.ImplicitRegion, conditions, variables);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }
}
