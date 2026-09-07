package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITensorAccess;
import org.matheclipse.core.tensor.opt.qh3.ConvexHull3D;
import org.matheclipse.external.fastutil.ints.IntArrayList;

public class ComputationalGeometryFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AASTriangle.setEvaluator(new AASTriangle());
      S.ASATriangle.setEvaluator(new ASATriangle());
      S.SASTriangle.setEvaluator(new SASTriangle());
      S.SSSTriangle.setEvaluator(new SSSTriangle());


      S.ConvexHull.setEvaluator(new ConvexHull());
      S.ConvexHullMesh.setEvaluator(new ConvexHullMesh());
      S.ConvexHullRegion.setEvaluator(new ConvexHullRegion());
      S.ConvexRegionQ.setEvaluator(new ConvexRegionQ());
      S.RegionBoundary.setEvaluator(new RegionBoundary());
      S.CircularArcThrough.setEvaluator(new CircularArcThrough());
      S.CapsuleShape.setEvaluator(new CapsuleShape());
      S.HalfSpace.setEvaluator(new HalfSpace());
      S.SphericalShell.setEvaluator(new SphericalShell());
      S.StadiumShape.setEvaluator(new StadiumShape());
      S.CollinearPoints.setEvaluator(new CollinearPoints());
      S.CoordinateBoundingBox.setEvaluator(new CoordinateBoundingBox());
      S.CoordinateBounds.setEvaluator(new CoordinateBounds());
      S.CoplanarPoints.setEvaluator(new CoplanarPoints());

      S.GeometricTest.setEvaluator(new GeometricTest());
      S.ConvexPolyhedronQ.setEvaluator(new ConvexPolyhedronQ());
      S.RegionProduct.setEvaluator(new RegionProduct());
      S.Circumsphere.setEvaluator(new Circumsphere());
      S.PolyhedronData.setEvaluator(new PolyhedronData());

      S.VectorGreater.setEvaluator(new VectorGreater());
      S.VectorGreaterEqual.setEvaluator(new VectorGreaterEqual());
      S.VectorLess.setEvaluator(new VectorLess());
      S.VectorLessEqual.setEvaluator(new VectorLessEqual());
    }
  }

  private static class AASTriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      IAST angleSum = F.Plus(a, b);
      if (a.isNegativeResult() || a.isZero()) {
        // The angle `1` should be a positive number less than `2`.
        return Errors.printMessage(S.AASTriangle, "npa", F.List(a, S.Pi), engine);
      }
      if (b.isNegativeResult() || b.isZero()) {
        // The angle `1` should be a positive number less than `2`.
        return Errors.printMessage(S.AASTriangle, "npa", F.List(b, S.Pi), engine);
      }
      if (angleSum.greaterEqualThan(S.Pi).isTrue()) {
        // The sum of angles `1` and `2` should be less than `3`.
        return Errors.printMessage(S.AASTriangle, "asm", F.List(a, b, S.Pi), engine);
      }
      return F.Triangle(//
          F.List(F.CListC0C0, //
              F.List(F.Times(c, F.Csc(a), F.Sin(angleSum)), F.C0), //
              F.List(F.Times(c, F.Cot(a), F.Sin(b)), F.Times(c, F.Sin(b)))) //
      );
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }


  private static class ASATriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      IExpr angleSum = engine.evaluate(F.Plus(a, c));
      if (angleSum.greaterEqualThan(S.Pi).isTrue()) {
        // The sum of angles `1` and `2` should be less than `3`.
        return Errors.printMessage(S.ASATriangle, "asm", F.List(a, c, S.Pi), engine);
      }
      // Triangle({{0,0}, {b,0}, {b*Cos(a)*Csc(a+c)*Sin(c), b*Csc(a+c)*Sin(a)*Sin(c)}})
      return F.Triangle(F.list(//
          F.CListC0C0, //
          F.list(b, F.C0), //
          F.list(F.Times(b, F.Cos(a), F.Csc(angleSum), F.Sin(c)), //
              F.Times(b, F.Csc(angleSum), F.Sin(a), F.Sin(c)))));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  private static class SASTriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      if (a.isNegativeResult() || a.isZero()) {
        // The triangle side `1`should be a positive number.
        return Errors.printMessage(S.SASTriangle, "nps", F.List(a), engine);
      }
      if (c.isNegativeResult() || c.isZero()) {
        // The triangle side `1`should be a positive number.
        return Errors.printMessage(S.SASTriangle, "nps", F.List(c), engine);
      }
      if (b.greaterEqualThan(S.Pi).isTrue()) {
        // The angle `1` should be a positive number less than `2`
        return Errors.printMessage(S.SASTriangle, "npa", F.List(b, S.Pi), engine);
      }
      IAST plus = F.Plus(F.Sqr(a), F.Sqr(c), F.Times(F.CN2, a, c, F.Cos(b)));
      IExpr sqrtNumerator = F.Power(plus, F.C1D2);
      IExpr sqrtDenominator = F.Power(plus, F.CN1D2);
      // Triangle({{0, 0}, {Sqrt(a^2+c^2-2*a*c*Cos(b)), 0},
      // {(c^2-a*c*Cos(b))/Sqrt(a^2+c^2-2*a*c*Cos(b)),
      // (a*c*Sin(b))/Sqrt(a^2+c^2-2*a*c*Cos(b))}})
      return F.Triangle(F.list(//
          F.CListC0C0, //
          F.list(sqrtNumerator, F.C0), //
          F.list(F.Times(F.Plus(F.Sqr(c), F.Times(F.CN1, a, c, F.Cos(b))), sqrtDenominator), //
              F.Times(a, c, sqrtDenominator, F.Sin(b)))));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  /**
   * <code>CapsuleShape()</code> and <code>CapsuleShape(r)</code> evaluate to the standard form
   * <code>CapsuleShape({p1, p2}, r)</code>.
   */
  private static class CapsuleShape extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return RegionPrimitives.capsuleShapeStandardForm(ast);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /**
   * <code>HalfSpace(n)</code> evaluates to <code>HalfSpace(n, 0)</code>, the points <code>x</code>
   * with <code>n.x &lt;= 0</code>.
   */
  private static class HalfSpace extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1 && ast.arg1().isList() && !ast.arg1().isListOfLists()) {
        return F.binaryAST2(S.HalfSpace, ast.arg1(), F.C0);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>SphericalShell()</code>, <code>SphericalShell(r)</code> and
   * <code>SphericalShell({rInner, rOuter})</code> evaluate to the standard form
   * <code>SphericalShell(c, {rInner, rOuter})</code>.
   */
  private static class SphericalShell extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return RegionPrimitives.sphericalShellStandardForm(ast);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
    }
  }

  /**
   * <code>StadiumShape()</code> and <code>StadiumShape(r)</code> evaluate to the standard form
   * <code>StadiumShape({p1, p2}, r)</code>.
   */
  private static class StadiumShape extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return RegionPrimitives.stadiumShapeStandardForm(ast);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static class SSSTriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      if (a.isNegativeResult() || a.isZero()) {
        // The triangle side `1`should be a positive number.
        return Errors.printMessage(S.SSSTriangle, "nps", F.List(a), engine);
      }
      if (b.isNegativeResult() || b.isZero()) {
        // The triangle side `1`should be a positive number.
        return Errors.printMessage(S.SSSTriangle, "nps", F.List(b), engine);
      }
      if (c.isNegativeResult() || c.isZero()) {
        // The triangle side `1`should be a positive number.
        return Errors.printMessage(S.SSSTriangle, "nps", F.List(c), engine);
      }
      return F.Triangle(F.list(//
          F.CListC0C0, //
          F.list(c, F.C0), //
          F.list(//
              F.Times(F.Plus(F.Negate(F.Sqr(a)), F.Sqr(b), F.Sqr(c)),
                  F.Power(F.Times(F.C2, c), F.CN1)),
              F.Times(F.Power(F.Times(F.C2, c), F.CN1), F.Sqrt(F.Times(F.Plus(a, b, F.Negate(c)),
                  F.Plus(a, F.Negate(b), c), F.Plus(F.Negate(a), b, c), F.Plus(a, b, c))))) //
      ));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  /**
   * Twice the signed area of the triangle <code>(p1, p2, p3)</code>: the three points are a
   * counter-clockwise turn if the result is positive, a clockwise turn if it's negative and
   * co-linear if it's zero.
   *
   * @return <code>Det2D[p2 - p1, p3 - p1]</code>
   */
  private static IExpr counterClockwise(IAST p1, IAST p2, IAST p3) {
    IExpr ux = F.eval(p2.arg1().subtract(p1.arg1()));
    IExpr uy = F.eval(p2.arg2().subtract(p1.arg2()));
    IExpr vx = F.eval(p3.arg1().subtract(p1.arg1()));
    IExpr vy = F.eval(p3.arg2().subtract(p1.arg2()));
    return F.eval(ux.times(vy).subtract(uy.times(vx)));
  }

  /** The squared euclidean distance between two 2D points. */
  private static IExpr squaredDistance(IAST p1, IAST p2) {
    IExpr dx = F.eval(p2.arg1().subtract(p1.arg1()));
    IExpr dy = F.eval(p2.arg2().subtract(p1.arg2()));
    return F.eval(dx.times(dx).plus(dy.times(dy)));
  }

  /** The scalar product of the vectors <code>p2 - p1</code> and <code>p3 - p1</code>. */
  private static IExpr scalarProduct(IAST p1, IAST p2, IAST p3) {
    IExpr ux = F.eval(p2.arg1().subtract(p1.arg1()));
    IExpr uy = F.eval(p2.arg2().subtract(p1.arg2()));
    IExpr vx = F.eval(p3.arg1().subtract(p1.arg1()));
    IExpr vy = F.eval(p3.arg2().subtract(p1.arg2()));
    return F.eval(ux.times(vx).plus(uy.times(vy)));
  }

  /**
   * The center of the circle through the given 2D points: the midpoint if the points define a
   * diameter, otherwise the circumcenter of the first three points which aren't on one line.
   *
   * @param listOfPoints a list of 2D points
   * @return {@link F#NIL} if the points don't define a circle
   */
  private static IAST circleCenter(IAST listOfPoints, EvalEngine engine) {
    IntArrayList candidates = distinctPointIndices(listOfPoints);
    if (candidates.size() < 2) {
      return F.NIL;
    }
    IAST p1 = (IAST) listOfPoints.get(candidates.getInt(0));
    IAST p2 = (IAST) listOfPoints.get(candidates.getInt(1));
    if (candidates.size() == 2) {
      // the two points define the diameter of the circle
      return F.list(F.eval(F.C1D2.times(p1.arg1().plus(p2.arg1()))), //
          F.eval(F.C1D2.times(p1.arg2().plus(p2.arg2()))));
    }
    for (int i = 2; i < candidates.size(); i++) {
      IAST p3 = (IAST) listOfPoints.get(candidates.getInt(i));
      if (!isPossibleZero(counterClockwise(p1, p2, p3), engine)) {
        return circumcenter(p1, p2, p3);
      }
    }
    // all points are on one line
    return F.NIL;
  }

  /** The center of the circle through the three 2D points <code>p1, p2, p3</code>. */
  private static IAST circumcenter(IAST p1, IAST p2, IAST p3) {
    IExpr x1 = p1.arg1();
    IExpr y1 = p1.arg2();
    IExpr x2 = p2.arg1();
    IExpr y2 = p2.arg2();
    IExpr x3 = p3.arg1();
    IExpr y3 = p3.arg2();
    // the squared distances of the points from the origin
    IExpr n1 = x1.times(x1).plus(y1.times(y1));
    IExpr n2 = x2.times(x2).plus(y2.times(y2));
    IExpr n3 = x3.times(x3).plus(y3.times(y3));
    // expand the coordinates, so that they cancel out if the center is a simple point
    IExpr d = F.eval(F.Expand(F.C2.times(counterClockwise(p1, p2, p3))));
    IExpr cx = F.eval(F.Expand(
        n1.times(y2.subtract(y3)).plus(n2.times(y3.subtract(y1))).plus(n3.times(y1.subtract(y2)))));
    IExpr cy = F.eval(F.Expand(
        n1.times(x3.subtract(x2)).plus(n2.times(x1.subtract(x3))).plus(n3.times(x2.subtract(x1)))));
    return F.list(F.eval(cx.divide(d)), F.eval(cy.divide(d)));
  }

  /** Test if <code>expr</code> is zero, also if that requires a symbolic simplification. */
  private static boolean isPossibleZero(IExpr expr, EvalEngine engine) {
    if (expr.isZero()) {
      return true;
    }
    if (expr.isNumber()) {
      return false;
    }
    return S.PossibleZeroQ.of(engine, expr).isTrue();
  }

  /**
   * Test if all elements of <code>listOfPoints</code> are points with the same number of
   * coordinates, which must be <code>2</code> or <code>3</code>.
   *
   * @param listOfPoints a list of points
   */
  private static boolean isPointList(IAST listOfPoints) {
    int dimension = -1;
    for (int i = 1; i < listOfPoints.size(); i++) {
      IExpr point = listOfPoints.get(i);
      if (!point.isList2() && !point.isList3()) {
        return false;
      }
      if (i == 1) {
        dimension = ((IAST) point).argSize();
      } else if (((IAST) point).argSize() != dimension) {
        return false;
      }
    }
    return true;
  }

  /**
   * Test if the three dimensional points <code>p1, p2, p3</code> are on one line, because the cross
   * product of the vectors <code>p2-p1</code> and <code>p3-p1</code> is the zero vector.
   */
  private static boolean isCollinear3D(IAST p1, IAST p2, IAST p3) {
    IExpr[] cross = crossProduct(difference(p2, p1), difference(p3, p1));
    return cross[0].isZero() && cross[1].isZero() && cross[2].isZero();
  }

  /** Test if every coordinate of every point is a real number. */
  private static boolean isRealPointList(IAST listOfPoints) {
    for (int i = 1; i < listOfPoints.size(); i++) {
      IAST point = (IAST) listOfPoints.get(i);
      for (int j = 1; j < point.size(); j++) {
        if (!point.get(j).isReal()) {
          return false;
        }
      }
    }
    return true;
  }

  /** Test if every coordinate of every point is an exact number. */
  private static boolean isExactPointList(IAST listOfPoints) {
    for (int i = 1; i < listOfPoints.size(); i++) {
      IAST point = (IAST) listOfPoints.get(i);
      for (int j = 1; j < point.size(); j++) {
        if (!point.get(j).isExactNumber()) {
          return false;
        }
      }
    }
    return true;
  }

  /** The indices of the points which occur for the first time in <code>listOfPoints</code>. */
  private static IntArrayList distinctPointIndices(IAST listOfPoints) {
    Map<IExpr, Integer> distinct = new LinkedHashMap<IExpr, Integer>();
    for (int i = 1; i < listOfPoints.size(); i++) {
      distinct.putIfAbsent(listOfPoints.get(i), i);
    }
    return new IntArrayList(distinct.values());
  }

  /**
   * The indices of the convex hull of a list of one dimensional points.
   *
   * @return the indices of the minimum and the maximum point or <code>null</code> if all points are
   *         equal
   */
  private static int[] hullIndices1D(IAST listOfPoints) {
    IntArrayList candidates = distinctPointIndices(listOfPoints);
    if (candidates.size() < 2) {
      return null;
    }
    int minimum = candidates.getInt(0);
    int maximum = candidates.getInt(0);
    for (int i = 1; i < candidates.size(); i++) {
      int index = candidates.getInt(i);
      IExpr coordinate = ((IAST) listOfPoints.get(index)).arg1();
      if (coordinate.compareTo(((IAST) listOfPoints.get(minimum)).arg1()) < 0) {
        minimum = index;
      }
      if (coordinate.compareTo(((IAST) listOfPoints.get(maximum)).arg1()) > 0) {
        maximum = index;
      }
    }
    return new int[] {minimum, maximum};
  }

  /**
   * The convex hull of a list of two dimensional points with a Graham scan.
   *
   * @param listOfPoints a list of 2D points
   * @param allPoints if <code>true</code> the points which lie on a hull edge are part of the
   *        result, if <code>false</code> only the corners of the hull are returned
   * @return the 1-based indices of the hull points in counter-clockwise order, or <code>null</code>
   *         if the points aren't affinely independent (less than three distinct points or all
   *         points co-linear)
   */
  public static int[] hullIndices2D(IAST listOfPoints, boolean allPoints) {
    if (!isRealPointList(listOfPoints)) {
      return null;
    }
    IntArrayList candidates = distinctPointIndices(listOfPoints);
    if (candidates.size() < 3) {
      return null;
    }
    // the point with minimal y coordinate (minimal x coordinate breaks a tie) is a hull point and
    // every other point is seen from it under an angle in the range [0, Pi)
    int pivotIndex = candidates.getInt(0);
    IAST pivot = (IAST) listOfPoints.get(pivotIndex);
    for (int i = 1; i < candidates.size(); i++) {
      IAST point = (IAST) listOfPoints.get(candidates.getInt(i));
      int cmp = point.arg2().compareTo(pivot.arg2());
      if (cmp < 0 || (cmp == 0 && point.arg1().compareTo(pivot.arg1()) < 0)) {
        pivotIndex = candidates.getInt(i);
        pivot = point;
      }
    }

    final IAST pivotPoint = pivot;
    List<Integer> sorted = new ArrayList<Integer>(candidates.size());
    for (int i = 0; i < candidates.size(); i++) {
      if (candidates.getInt(i) != pivotIndex) {
        sorted.add(candidates.getInt(i));
      }
    }
    // sort by the polar angle around the pivot, the nearer point wins a tie
    Collections.sort(sorted, new Comparator<Integer>() {
      @Override
      public int compare(Integer index1, Integer index2) {
        IAST point1 = (IAST) listOfPoints.get(index1);
        IAST point2 = (IAST) listOfPoints.get(index2);
        IExpr ccw = counterClockwise(pivotPoint, point1, point2);
        if (ccw.isPositive()) {
          return -1;
        }
        if (ccw.isNegative()) {
          return 1;
        }
        return squaredDistance(pivotPoint, point1).compareTo(squaredDistance(pivotPoint, point2));
      }
    });

    IntArrayList stack = new IntArrayList(sorted.size() + 1);
    stack.add(pivotIndex);
    for (int i = 0; i < sorted.size(); i++) {
      int index = sorted.get(i);
      IAST point = (IAST) listOfPoints.get(index);
      while (stack.size() >= 2) {
        IAST last = (IAST) listOfPoints.get(stack.getInt(stack.size() - 1));
        IAST beforeLast = (IAST) listOfPoints.get(stack.getInt(stack.size() - 2));
        if (counterClockwise(beforeLast, last, point).isPositive()) {
          break;
        }
        stack.removeInt(stack.size() - 1);
      }
      stack.add(index);
    }
    if (stack.size() < 3) {
      // all points are co-linear - there's no two dimensional hull
      return null;
    }
    if (allPoints) {
      stack = insertEdgePoints(listOfPoints, stack, candidates);
    }
    return stack.toIntArray();
  }

  /**
   * Insert the points which lie on a hull edge between the corners of the hull, ordered along the
   * boundary.
   */
  private static IntArrayList insertEdgePoints(IAST listOfPoints, IntArrayList hull,
      IntArrayList candidates) {
    IntArrayList result = new IntArrayList(candidates.size());
    for (int i = 0; i < hull.size(); i++) {
      int fromIndex = hull.getInt(i);
      int toIndex = hull.getInt(i + 1 == hull.size() ? 0 : i + 1);
      IAST from = (IAST) listOfPoints.get(fromIndex);
      IAST to = (IAST) listOfPoints.get(toIndex);
      result.add(fromIndex);

      List<Integer> onEdge = new ArrayList<Integer>();
      IExpr edgeLength = squaredDistance(from, to);
      for (int j = 0; j < candidates.size(); j++) {
        int index = candidates.getInt(j);
        if (hull.contains(index)) {
          continue;
        }
        IAST point = (IAST) listOfPoints.get(index);
        if (!counterClockwise(from, to, point).isZero()) {
          continue;
        }
        IExpr position = scalarProduct(from, to, point);
        if (position.isPositive() && position.compareTo(edgeLength) < 0) {
          onEdge.add(index);
        }
      }
      if (!onEdge.isEmpty()) {
        final IAST edgeStart = from;
        Collections.sort(onEdge, new Comparator<Integer>() {
          @Override
          public int compare(Integer index1, Integer index2) {
            return squaredDistance(edgeStart, (IAST) listOfPoints.get(index1))
                .compareTo(squaredDistance(edgeStart, (IAST) listOfPoints.get(index2)));
          }
        });
        result.addAll(onEdge);
      }
    }
    return result;
  }

  /**
   * Build the <code>BoundaryMeshRegion</code> of a convex hull.
   *
   * <p>
   * The hull vertices are listed in the order in which they appear in the input, the boundary cells
   * index into that list. If any input coordinate is inexact all coordinates are converted to
   * machine precision numbers, otherwise the exact coordinates are kept and the region carries
   * <code>WorkingPrecision -&gt; Infinity</code>.
   *
   * @param listOfPoints the original input points
   * @param vertexIndices the 1-based indices of the hull vertices, in ascending order
   * @param cells the boundary cells, indexing into <code>vertexIndices</code>
   */
  private static IExpr boundaryMeshRegion(IAST listOfPoints, int[] vertexIndices, IAST cells,
      EvalEngine engine) {
    IASTAppendable coordinates = F.ListAlloc(vertexIndices.length);
    for (int i = 0; i < vertexIndices.length; i++) {
      coordinates.append(listOfPoints.get(vertexIndices[i]));
    }
    boolean exact = isExactPointList(listOfPoints);
    IExpr vertices = exact ? coordinates : engine.evaluate(F.N(coordinates));

    IASTAppendable result = F.ast(S.BoundaryMeshRegion, 4);
    result.append(vertices);
    result.append(cells);
    result.append(F.Rule(S.Method, F.list(F.Rule(F.stringx("SeparateBoundaries"), S.False))));
    if (exact) {
      result.append(F.Rule(S.WorkingPrecision, F.CInfinity));
    }
    return result;
  }

  /**
   * Map the 1-based indices of the input points to the 1-based positions of the hull vertices.
   *
   * @param vertexIndices the hull vertex indices in ascending order
   * @param numberOfPoints the number of input points
   */
  private static int[] positionsOfVertices(int[] vertexIndices, int numberOfPoints) {
    int[] positions = new int[numberOfPoints + 1];
    for (int i = 0; i < vertexIndices.length; i++) {
      positions[vertexIndices[i]] = i + 1;
    }
    return positions;
  }

  /** The convex hull of a list of one dimensional points as a <code>BoundaryMeshRegion</code>. */
  private static IExpr convexHullMesh1D(IAST listOfPoints, EvalEngine engine) {
    int[] hull = hullIndices1D(listOfPoints);
    if (hull == null) {
      return F.NIL;
    }
    int[] vertexIndices = hull.clone();
    Arrays.sort(vertexIndices);
    int[] positions = positionsOfVertices(vertexIndices, listOfPoints.argSize());
    IAST cells = F.list(F.Point(F.list(F.list(F.ZZ(positions[hull[0]])), //
        F.list(F.ZZ(positions[hull[1]])))));
    return boundaryMeshRegion(listOfPoints, vertexIndices, cells, engine);
  }

  /**
   * The convex hull of a list of two dimensional points as a <code>BoundaryMeshRegion</code> with
   * <code>Line</code> boundary cells. The boundary walk starts at the first hull vertex and runs
   * counter-clockwise.
   */
  private static IExpr convexHullMesh2D(IAST listOfPoints, EvalEngine engine) {
    int[] cycle = hullIndices2D(listOfPoints, false);
    if (cycle == null) {
      return F.NIL;
    }
    int[] vertexIndices = cycle.clone();
    Arrays.sort(vertexIndices);
    int[] positions = positionsOfVertices(vertexIndices, listOfPoints.argSize());

    // rotate the counter-clockwise walk so that it starts at the first hull vertex
    int start = 0;
    for (int i = 0; i < cycle.length; i++) {
      if (positions[cycle[i]] == 1) {
        start = i;
        break;
      }
    }
    IASTAppendable edges = F.ListAlloc(cycle.length);
    for (int i = 0; i < cycle.length; i++) {
      int from = positions[cycle[(start + i) % cycle.length]];
      int to = positions[cycle[(start + i + 1) % cycle.length]];
      edges.append(F.list(F.ZZ(from), F.ZZ(to)));
    }
    return boundaryMeshRegion(listOfPoints, vertexIndices, F.list(F.Line(edges)), engine);
  }

  /**
   * The faces of a three dimensional hull, renumbered to the positions of the hull vertices and
   * brought into a canonical order: every face keeps its counter-clockwise orientation but starts
   * at its smallest vertex, and the faces are sorted by their vertex indices.
   *
   * @param hullFaces the zero based faces as returned by {@link ConvexHull3D#getFaces()}
   * @param vertexIndices the 1-based input index of every hull vertex
   * @param positions maps an input index to the position of that vertex in the result
   */
  private static IAST canonicalFaces(int[][] hullFaces, int[] vertexIndices, int[] positions) {
    int[][] faces = new int[hullFaces.length][];
    for (int i = 0; i < hullFaces.length; i++) {
      int[] hullFace = hullFaces[i];
      int[] face = new int[hullFace.length];
      int minimum = 0;
      for (int j = 0; j < hullFace.length; j++) {
        face[j] = positions[vertexIndices[hullFace[j]]];
        if (face[j] < face[minimum]) {
          minimum = j;
        }
      }
      // rotate the face so that it starts at its smallest vertex
      int[] rotated = new int[face.length];
      for (int j = 0; j < face.length; j++) {
        rotated[j] = face[(minimum + j) % face.length];
      }
      faces[i] = rotated;
    }
    Arrays.sort(faces, new Comparator<int[]>() {
      @Override
      public int compare(int[] face1, int[] face2) {
        for (int i = 0; i < face1.length && i < face2.length; i++) {
          if (face1[i] != face2[i]) {
            return face1[i] < face2[i] ? -1 : 1;
          }
        }
        return face1.length - face2.length;
      }
    });
    IASTAppendable result = F.ListAlloc(faces.length);
    for (int i = 0; i < faces.length; i++) {
      IASTAppendable face = F.ListAlloc(faces[i].length);
      for (int j = 0; j < faces[i].length; j++) {
        face.append(F.ZZ(faces[i][j]));
      }
      result.append(face);
    }
    return result;
  }

  /**
   * The convex hull of a list of three dimensional points as a <code>BoundaryMeshRegion</code> with
   * <code>Polygon</code> boundary cells.
   *
   * <p>
   * Co-planar facets are merged by the QuickHull algorithm, so a cube results in six quadrilaterals
   * and not in twelve triangles. Every face is listed counter-clockwise seen from outside the hull
   * and starts at its smallest vertex; the faces are sorted by their vertex indices.
   */
  private static IExpr convexHullMesh3D(IAST listOfPoints, EvalEngine engine) {
    ConvexHull3D hull = new ConvexHull3D();
    hull.build(listOfPoints);
    int[] hullVertices = hull.getVertexPointIndices();
    int[][] hullFaces = hull.getFaces();
    if (hullVertices.length < 4 || hullFaces.length < 4) {
      return F.NIL;
    }
    int[] vertexIndices = new int[hullVertices.length];
    for (int i = 0; i < hullVertices.length; i++) {
      vertexIndices[i] = hullVertices[i] + 1;
    }
    int[] sortedVertexIndices = vertexIndices.clone();
    Arrays.sort(sortedVertexIndices);
    int[] positions = positionsOfVertices(sortedVertexIndices, listOfPoints.argSize());

    IAST polygons = canonicalFaces(hullFaces, vertexIndices, positions);
    return boundaryMeshRegion(listOfPoints, sortedVertexIndices, F.list(F.Polygon(polygons)),
        engine);
  }

  /**
   * The two lexicographically extreme points of a set of co-linear points.
   *
   * @return the indices of the smallest and the largest point
   */
  private static int[] extremeIndices(IAST listOfPoints, IntArrayList candidates) {
    int minimum = candidates.getInt(0);
    int maximum = candidates.getInt(0);
    for (int i = 1; i < candidates.size(); i++) {
      int index = candidates.getInt(i);
      if (comparePoints((IAST) listOfPoints.get(index), (IAST) listOfPoints.get(minimum)) < 0) {
        minimum = index;
      }
      if (comparePoints((IAST) listOfPoints.get(index), (IAST) listOfPoints.get(maximum)) > 0) {
        maximum = index;
      }
    }
    return new int[] {minimum, maximum};
  }

  /** Compare two points coordinate by coordinate. */
  private static int comparePoints(IAST point1, IAST point2) {
    for (int i = 1; i < point1.size() && i < point2.size(); i++) {
      int cmp = point1.get(i).compareTo(point2.get(i));
      if (cmp != 0) {
        return cmp;
      }
    }
    return 0;
  }

  /**
   * Project co-planar three dimensional points into the plane they span.
   *
   * @return a list of 2D points in the same order as the input, or {@link F#NIL} if the points
   *         aren't co-planar or span less than a plane
   */
  private static IAST projectToPlane(IAST listOfPoints, IntArrayList candidates) {
    if (candidates.size() < 3) {
      return F.NIL;
    }
    IAST origin = (IAST) listOfPoints.get(candidates.getInt(0));
    IExpr[] u = null;
    IExpr[] w = null;
    for (int i = 1; i < candidates.size(); i++) {
      IExpr[] direction = difference((IAST) listOfPoints.get(candidates.getInt(i)), origin);
      if (u == null) {
        u = direction;
        continue;
      }
      IExpr[] cross = crossProduct(u, direction);
      if (!(cross[0].isZero() && cross[1].isZero() && cross[2].isZero())) {
        w = direction;
        break;
      }
    }
    if (u == null || w == null) {
      return F.NIL;
    }
    // Gram-Schmidt: the second basis vector is orthogonal to the first one
    IExpr uu = F.eval(dotProduct(u, u));
    IExpr wu = F.eval(dotProduct(w, u));
    IExpr[] e2 = new IExpr[3];
    for (int i = 0; i < 3; i++) {
      e2[i] = F.eval(w[i].subtract(F.Divide(wu, uu).times(u[i])));
    }
    IASTAppendable result = F.ListAlloc(listOfPoints.argSize());
    for (int i = 1; i < listOfPoints.size(); i++) {
      IExpr[] direction = difference((IAST) listOfPoints.get(i), origin);
      result.append(F.list(F.eval(dotProduct(direction, u)), F.eval(dotProduct(direction, e2))));
    }
    return result;
  }

  private static IExpr[] difference(IAST point, IAST origin) {
    return new IExpr[] {F.eval(point.arg1().subtract(origin.arg1())), //
        F.eval(point.arg2().subtract(origin.arg2())), //
        F.eval(point.arg3().subtract(origin.arg3()))};
  }

  private static IExpr[] crossProduct(IExpr[] u, IExpr[] v) {
    return new IExpr[] {F.eval(u[1].times(v[2]).subtract(u[2].times(v[1]))), //
        F.eval(u[2].times(v[0]).subtract(u[0].times(v[2]))), //
        F.eval(u[0].times(v[1]).subtract(u[1].times(v[0])))};
  }

  private static IExpr dotProduct(IExpr[] u, IExpr[] v) {
    return u[0].times(v[0]).plus(u[1].times(v[1])).plus(u[2].times(v[2]));
  }

  /** The points of a list selected by their 1-based indices. */
  private static IAST selectPoints(IAST listOfPoints, int[] indices) {
    IASTAppendable result = F.ListAlloc(indices.length);
    for (int i = 0; i < indices.length; i++) {
      result.append(listOfPoints.get(indices[i]));
    }
    return result;
  }

  /**
   * The convex hull as <code>Polygon({p1,...}, {1,...,n})</code>: the hull corners ordered along
   * the counter-clockwise boundary walk, which starts at the corner that appears first in the
   * input.
   */
  private static IExpr hullPolygon(IAST listOfPoints, int[] cycle) {
    int start = 0;
    for (int i = 1; i < cycle.length; i++) {
      if (cycle[i] < cycle[start]) {
        start = i;
      }
    }
    int[] walk = new int[cycle.length];
    IASTAppendable indices = F.ListAlloc(cycle.length);
    for (int i = 0; i < cycle.length; i++) {
      walk[i] = cycle[(start + i) % cycle.length];
      indices.append(F.ZZ(i + 1));
    }
    return F.binaryAST2(S.Polygon, selectPoints(listOfPoints, walk), indices);
  }

  /**
   * The convex hull of a list of points as a geometric region. In contrast to
   * <code>ConvexHullMesh</code> this also handles the degenerate cases where the hull isn't full
   * dimensional.
   */
  /**
   * A region which is convex by construction and therefore already its own convex hull. Only the
   * smooth bodies are listed here - a polytope is rebuilt from its corner points instead, so that
   * redundant vertices are dropped and the result is a canonical <code>Polygon</code> or
   * <code>Polyhedron</code>.
   */
  private static boolean isConvexRegion(IAST reg) {
    int headID = reg.headID();
    switch (headID) {
      case ID.Ball:
      case ID.Ellipsoid:
        return true;
      case ID.Disk:
        // a full disk or ellipse is convex, a sector of it is not
        return reg.argSize() <= 2;
      case ID.HalfSpace:
      case ID.HalfPlane:
      case ID.InfinitePlane:
      case ID.FullRegion:
        return true;
      default:
        return false;
    }
  }

  /**
   * The corner points of a polytope, which the convex hull is then computed from.
   *
   * @return {@link F#NIL} if the corner points of <code>reg</code> are not available
   */
  private static IAST regionVertices(IAST reg) {
    int headID = reg.headID();
    switch (headID) {
      case ID.Point:
      case ID.Line:
      case ID.Polygon:
        if (reg.argSize() == 1 && reg.arg1().isListOfLists()) {
          return (IAST) reg.arg1();
        }
        if (reg.isAST(S.Point, 2) && reg.arg1().isList()) {
          // a single point
          return F.list(reg.arg1());
        }
        return F.NIL;
      case ID.Triangle:
        if (reg.argSize() == 0) {
          return F.list(F.CListC0C0, F.List(F.C1, F.C0), F.List(F.C0, F.C1));
        }
        return reg.argSize() == 1 && reg.arg1().isListOfLists() ? (IAST) reg.arg1() : F.NIL;
      case ID.Simplex:
        return RegionPrimitives.verticesOfSimplex(reg);
      case ID.Rectangle:
      case ID.Cuboid:
        return boxVertices(reg);
      case ID.Parallelogram:
      case ID.Parallelepiped:
        return parallelepipedVertices(reg);
      case ID.Polyhedron:
        return reg.argSize() == 2 && reg.arg1().isListOfLists() ? (IAST) reg.arg1() : F.NIL;
      default:
        return F.NIL;
    }
  }

  /** The <code>2^n</code> corners of an axis aligned box. */
  private static IAST boxVertices(IAST reg) {
    IAST corners = RegionPrimitives.boxCorners(reg);
    if (corners.isNIL()) {
      return F.NIL;
    }
    IAST lower = (IAST) corners.arg1();
    IAST upper = (IAST) corners.arg2();
    int n = lower.argSize();
    if (n > 20) {
      // 2^n corners - refuse to build a list which cannot be handled anyway
      return F.NIL;
    }
    int count = 1 << n;
    IASTAppendable vertices = F.ListAlloc(count);
    for (int mask = 0; mask < count; mask++) {
      IASTAppendable corner = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        corner.append(((mask >> (i - 1)) & 1) == 0 ? lower.get(i) : upper.get(i));
      }
      vertices.append(corner);
    }
    return vertices;
  }

  /** The <code>2^n</code> corners of a parallelepiped: the base plus every subset sum. */
  private static IAST parallelepipedVertices(IAST reg) {
    IExpr base;
    IAST vectors;
    if (reg.isAST(S.Parallelogram)) {
      RegionPrimitives.ParallelogramSpec spec = RegionPrimitives.parseParallelogram(reg);
      if (spec == null) {
        return F.NIL;
      }
      base = spec.base;
      vectors = spec.vectors;
    } else if (reg.argSize() == 2 && reg.arg1().isList() && reg.arg2().isListOfLists()) {
      base = reg.arg1();
      vectors = (IAST) reg.arg2();
    } else {
      return F.NIL;
    }
    int n = vectors.argSize();
    if (n < 1 || n > 20) {
      return F.NIL;
    }
    EvalEngine engine = EvalEngine.get();
    int count = 1 << n;
    IASTAppendable vertices = F.ListAlloc(count);
    for (int mask = 0; mask < count; mask++) {
      IASTAppendable sum = F.PlusAlloc(n + 1);
      sum.append(base);
      for (int i = 1; i <= n; i++) {
        if (((mask >> (i - 1)) & 1) != 0) {
          sum.append(vectors.get(i));
        }
      }
      IExpr corner = engine.evaluate(sum);
      if (!corner.isList()) {
        return F.NIL;
      }
      vertices.append(corner);
    }
    return vertices;
  }

  private static class ConvexHullRegion extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      // Unwrap Region display wrapper if present
      if (arg1.isAST(S.Region, 1)) {
        arg1 = arg1.first();
      }
      if (MeshFunctions.isMeshRegion(arg1)) {
        arg1 = MeshFunctions.meshCoordinates((IAST) arg1);
      }
      if (!arg1.isListOfLists() && arg1.isAST()) {
        // ConvexHullRegion(region): a convex region is its own hull, a polytope is the hull of its
        // corner points
        IAST reg = (IAST) arg1;
        if (isConvexRegion(reg)) {
          return reg;
        }
        IAST vertices = regionVertices(reg);
        if (vertices.isNIL()) {
          return F.NIL;
        }
        arg1 = vertices;
      }
      if (!arg1.isListOfLists()) {
        return F.NIL;
      }
      IAST listOfPoints = (IAST) arg1;
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(listOfPoints);
      if (dimensions.size() != 2) {
        return F.NIL;
      }
      if (!isRealPointList(listOfPoints)) {
        return F.NIL;
      }
      IntArrayList candidates = distinctPointIndices(listOfPoints);
      if (candidates.size() == 1) {
        return F.Point((IAST) listOfPoints.get(candidates.getInt(0)));
      }
      switch (dimensions.getInt(1)) {
        case 1: {
          // the hull of a set of points on a line is the segment between the two extremes
          int[] extremes = extremeIndices(listOfPoints, candidates);
          return F.Line(selectPoints(listOfPoints, extremes));
        }
        case 2: {
          int[] hull = hullIndices2D(listOfPoints, false);
          if (hull == null) {
            // all points are co-linear
            return F.Line(selectPoints(listOfPoints, extremeIndices(listOfPoints, candidates)));
          }
          return hullPolygon(listOfPoints, hull);
        }
        case 3: {
          try {
            return polyhedron(listOfPoints, engine);
          } catch (IllegalArgumentException iae) {
            // the points are co-planar or co-linear
          }
          IAST projected = projectToPlane(listOfPoints, candidates);
          if (projected.isNIL()) {
            return F.Line(selectPoints(listOfPoints, extremeIndices(listOfPoints, candidates)));
          }
          int[] hull = hullIndices2D(projected, false);
          if (hull == null) {
            return F.Line(selectPoints(listOfPoints, extremeIndices(listOfPoints, candidates)));
          }
          return hullPolygon(listOfPoints, hull);
        }
      }
      return F.NIL;
    }

    /** The three dimensional hull as <code>Polyhedron({p1,...}, {{i,j,k},...})</code>. */
    private static IExpr polyhedron(IAST listOfPoints, EvalEngine engine) {
      ConvexHull3D hull = new ConvexHull3D();
      hull.build(listOfPoints);
      int[] hullVertices = hull.getVertexPointIndices();
      int[][] hullFaces = hull.getFaces();
      if (hullVertices.length < 4 || hullFaces.length < 4) {
        throw new IllegalArgumentException("degenerate hull");
      }
      // the vertices are listed in the order in which they appear in the input
      int[] vertexIndices = new int[hullVertices.length];
      for (int i = 0; i < hullVertices.length; i++) {
        vertexIndices[i] = hullVertices[i] + 1;
      }
      int[] sortedVertexIndices = vertexIndices.clone();
      Arrays.sort(sortedVertexIndices);
      int[] positions = positionsOfVertices(sortedVertexIndices, listOfPoints.argSize());

      IAST faces = canonicalFaces(hullFaces, vertexIndices, positions);
      return F.binaryAST2(S.Polyhedron, selectPoints(listOfPoints, sortedVertexIndices), faces);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * The planar convex hull as a list of point indices in counter-clockwise order. This is the
   * function of the legacy <code>ComputationalGeometry</code> package.
   */
  private static class ConvexHull extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isListOfLists()) {
        return F.NIL;
      }
      IAST listOfPoints = (IAST) arg1;
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(listOfPoints);
      if (dimensions.size() != 2 || dimensions.getInt(1) != 2) {
        return F.NIL;
      }
      boolean allPoints = !options[0].isFalse();
      int[] hull = hullIndices2D(listOfPoints, allPoints);
      if (hull == null) {
        // `1` should be a list of `2` or more affinely independent points.
        return Errors.printMessage(ast.topHead(), "affind", F.List(listOfPoints, F.C3), engine);
      }
      IASTAppendable result = F.ListAlloc(hull.length);
      for (int i = 0; i < hull.length; i++) {
        result.append(F.ZZ(hull[i]));
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.AllPoints, S.True);
    }
  }

  /** Test if a region is convex. */
  private static class ConvexRegionQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Region, 1)) {
        arg1 = arg1.first();
      }
      if (MeshFunctions.isMeshRegion(arg1)) {
        return MeshFunctions.convexQ((IAST) arg1, engine);
      }
      arg1 = MeshFunctions.normalizeRegion(arg1);
      if (arg1.isAST()) {
        IAST region = (IAST) arg1;
        IExpr head = region.head();
        if (head.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.Point:
            case ID.Line:
            case ID.HalfLine:
            case ID.InfiniteLine:
            case ID.Triangle:
            case ID.Rectangle:
            case ID.Cuboid:
            case ID.Disk:
            case ID.Ball:
            case ID.Ellipsoid:
            case ID.Simplex:
            case ID.Tetrahedron:
            case ID.Parallelepiped:
            case ID.HalfPlane:
            case ID.HalfSpace:
            case ID.Cone:
            case ID.Cylinder:
              return S.True;
            case ID.Annulus:
              return S.False;
            case ID.Polygon:
              if (region.argSize() == 1 && region.arg1().isListOfLists()) {
                return MeshFunctions.convexPointCycleQ((IAST) region.arg1(), engine);
              }
              return F.NIL;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** The boundary of a region. */
  private static class RegionBoundary extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Region, 1)) {
        arg1 = arg1.first();
      }
      if (MeshFunctions.isMeshRegion(arg1)) {
        return MeshFunctions.regionBoundary((IAST) arg1);
      }
      arg1 = MeshFunctions.normalizeRegion(arg1);
      if (arg1.isAST()) {
        IAST region = (IAST) arg1;
        IExpr head = region.head();
        if (head.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.Polygon:
            case ID.Triangle:
              if (region.argSize() == 1 && region.arg1().isListOfLists()) {
                IAST points = (IAST) region.arg1();
                IASTAppendable closed = F.ListAlloc(points.argSize() + 1);
                closed.appendArgs(points);
                closed.append(points.arg1());
                return F.Line(closed);
              }
              return F.NIL;
            case ID.Disk:
              return region.setAtCopy(0, S.Circle);
            case ID.Ball:
              return region.setAtCopy(0, S.Sphere);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ConvexHullMesh extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (MeshFunctions.isMeshRegion(arg1)) {
        arg1 = MeshFunctions.meshCoordinates((IAST) arg1);
      }
      if (!arg1.isListOfLists()) {
        return F.NIL;
      }
      IAST listOfPoints = (IAST) arg1;
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(listOfPoints);
      if (dimensions.size() != 2) {
        return F.NIL;
      }
      try {
        int embeddingDimension = dimensions.getInt(1);
        switch (embeddingDimension) {
          case 1: {
            IExpr mesh = convexHullMesh1D(listOfPoints, engine);
            if (mesh.isPresent()) {
              return mesh;
            }
            // `1` should be a list of `2` or more affinely independent points.
            return Errors.printMessage(ast.topHead(), "affind", F.List(listOfPoints, F.C2), engine);
          }
          case 2: {
            IExpr mesh = convexHullMesh2D(listOfPoints, engine);
            if (mesh.isPresent()) {
              return mesh;
            }
            // `1` should be a list of `2` or more affinely independent points.
            return Errors.printMessage(ast.topHead(), "affind", F.List(listOfPoints, F.C3), engine);
          }
          case 3: {
            IExpr mesh = convexHullMesh3D(listOfPoints, engine);
            if (mesh.isPresent()) {
              return mesh;
            }
            // `1` should be a list of `2` or more affinely independent points.
            return Errors.printMessage(ast.topHead(), "affind", F.List(listOfPoints, F.C4), engine);
          }
        }
      } catch (IllegalArgumentException iae) {
        // the points are coincident, co-linear or co-planar
        return Errors.printMessage(ast.topHead(), "affind",
            F.List(listOfPoints, F.ZZ(dimensions.getInt(1) + 1)), engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class CoordinateBoundingBox extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isListOfLists()) {
        IAST listOfPoints = (IAST) ast.arg1();
        if (listOfPoints.argSize() > 0) {
          IAST points0 = (IAST) listOfPoints.arg1();
          int dim = points0.argSize();
          if (dim > 0) {
            IASTAppendable minList = F.ListAlloc(dim);
            IASTAppendable maxList = F.ListAlloc(dim);

            for (int i = 1; i <= dim; i++) {
              minList.append(F.ast(S.Min, listOfPoints.argSize()));
              maxList.append(F.ast(S.Max, listOfPoints.argSize()));
            }

            for (int i = 1; i <= listOfPoints.argSize(); i++) {
              IExpr ptExpr = listOfPoints.get(i);
              if (!ptExpr.isList()) {
                return F.NIL;
              }
              IAST pt = (IAST) ptExpr;
              if (pt.argSize() != dim) {
                return F.NIL;
              }
              for (int j = 1; j <= dim; j++) {
                ((IASTAppendable) minList.get(j)).append(pt.get(j));
                ((IASTAppendable) maxList.get(j)).append(pt.get(j));
              }
            }

            // evaluate the Min and Max calculations
            for (int j = 1; j <= dim; j++) {
              minList.set(j, engine.evaluate(minList.get(j)));
              maxList.set(j, engine.evaluate(maxList.get(j)));
            }

            if (ast.isAST1()) {
              return F.List(minList, maxList);
            }

            if (ast.isAST2()) {
              IExpr padArg = ast.arg2();
              boolean isScaled = false;
              if (padArg.isAST(S.Scaled, 2)) {
                isScaled = true;
                padArg = padArg.first();
              }

              IASTAppendable finalMinList = F.ListAlloc(dim);
              IASTAppendable finalMaxList = F.ListAlloc(dim);

              for (int j = 1; j <= dim; j++) {
                IExpr minPart = minList.get(j);
                IExpr maxPart = maxList.get(j);
                IExpr padMin = F.C0;
                IExpr padMax = F.C0;

                if (padArg.isList()) {
                  IAST padList = (IAST) padArg;
                  // Handle different padding lengths if provided as lists
                  if (padList.argSize() != dim) {
                    return F.NIL;
                  }
                  IExpr p = padList.get(j);
                  if (p.isList2()) {
                    padMin = ((IAST) p).arg1();
                    padMax = ((IAST) p).arg2();
                  } else {
                    padMin = p;
                    padMax = p;
                  }
                } else {
                  padMin = padArg;
                  padMax = padArg;
                }

                IExpr newMin;
                IExpr newMax;

                // Scale calculations identically to maintain formula AST matches in tests
                if (isScaled) {
                  IExpr diffMin = engine.evaluate(F.Subtract(minPart, maxPart));
                  IExpr diffMax = engine.evaluate(F.Subtract(maxPart, minPart));
                  newMin = engine.evaluate(F.Plus(minPart, F.Times(padMin, diffMin)));
                  newMax = engine.evaluate(F.Plus(maxPart, F.Times(padMax, diffMax)));
                } else {
                  newMin = engine.evaluate(F.Subtract(minPart, padMin));
                  newMax = engine.evaluate(F.Plus(maxPart, padMax));
                }

                finalMinList.append(newMin);
                finalMaxList.append(newMax);
              }

              return F.List(finalMinList, finalMaxList);
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class CoordinateBounds extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isListOfLists()) {
        IAST listOfPoints = (IAST) arg1;
        if (listOfPoints.argSize() > 0) {
          // CoordinateBounds(pts, pad) behaves identically to Transpose(CoordinateBoundingBox(pts,
          // pad))
          IExpr bbox;
          if (ast.isAST1()) {
            bbox = F.CoordinateBoundingBox.funEval(engine, listOfPoints);
          } else if (ast.isAST2()) {
            bbox = F.CoordinateBoundingBox.funEval(engine, listOfPoints, ast.arg2());
          } else {
            return F.NIL;
          }

          if (bbox.isList()) {
            return S.Transpose.funEval(engine, bbox);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CoplanarPoints({{x1,y1,z1},{x2,y2,z2},{x3,y3,z3},{a,b,c},...})
   * </code>
   * </pre>
   *
   * <p>
   * returns true if the point <code>{a,b,c]</code> is on the plane defined by the first three
   * points <code>{x1,y1,z1},{x2,y2,z2},{x3,y3,z3}</code>.
   *
   * <p>
   * The plane is defined by the first three points of the list which don't lie on one line. A list
   * of less than four points and a list of points on one line are coplanar.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Coplanarity">Wikipedia - Coplanarity</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; CoplanarPoints( {{3,2,-5}, {-1,4,-3}, {-3,8,-5}, {-3,2,1}})
   * True
   *
   * &gt;&gt; CoplanarPoints( {{0,-1,-1}, {4,5,1}, {3,9,4}, {-4,4,3}})
   * False
   * </code>
   * </pre>
   */
  private static class CoplanarPoints extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        IAST listOfPoints = (IAST) ast.arg1();
        if (listOfPoints.argSize() == 0) {
          return F.NIL;
        }
        if (listOfPoints.argSize() <= 3) {
          // less than four points are always coplanar
          return isPointList(listOfPoints) ? S.True : F.NIL;
        }
        if (listOfPoints.arg1().isList2()) {
          // all 2D points lie on the same plane
          for (int i = 2; i < listOfPoints.size(); i++) {
            if (listOfPoints.get(i).isList2()) {
              continue;
            } else {
              // `1` should be a non-empty list of points.
              return Errors.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
            }
          }
          return S.True;
        } else if (listOfPoints.argSize() > 3 && listOfPoints.arg1().isList3()
            && listOfPoints.arg2().isList3() && listOfPoints.arg3().isList3()) {

          for (int i = 4; i < listOfPoints.size(); i++) {
            if (!listOfPoints.get(i).isList3()) {
              // `1` should be a non-empty list of points.
              return Errors.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
            }
          }
          return coplanarPoints(listOfPoints, engine);
        }
      }

      return F.NIL;
    }

    /**
     * Test if all points of <code>listOfPoints</code> are on the plane defined by the first three
     * points of the list which don't lie on one line.
     *
     * @param listOfPoints a list of at least four points with 3 coordinates each
     * @param engine
     */
    private static IExpr coplanarPoints(IAST listOfPoints, EvalEngine engine) {
      IAST p1 = (IAST) listOfPoints.arg1();
      // the first point which is different from p1 spans a line together with p1
      int lineIndex = -1;
      for (int i = 2; i < listOfPoints.size(); i++) {
        if (!listOfPoints.get(i).equals(p1)) {
          lineIndex = i;
          break;
        }
      }
      if (lineIndex < 0) {
        // all points are identical
        return S.True;
      }
      IAST p2 = (IAST) listOfPoints.get(lineIndex);
      // the first point which isn't on this line spans the plane together with p1 and p2
      int planeIndex = -1;
      for (int i = lineIndex + 1; i < listOfPoints.size(); i++) {
        if (!isCollinear3D(p1, p2, (IAST) listOfPoints.get(i))) {
          planeIndex = i;
          break;
        }
      }
      if (planeIndex < 0) {
        // all points are on one line
        return S.True;
      }
      IAST p3 = (IAST) listOfPoints.get(planeIndex);

      // equation of plane is: a*x + b*y + c*z = 0
      IASTAppendable result = F.ast(S.And, listOfPoints.argSize() - 3);
      for (int i = 2; i < listOfPoints.size(); i++) {
        if (i == lineIndex || i == planeIndex) {
          continue;
        }
        IAST p4 = (IAST) listOfPoints.get(i);
        IExpr temp = coplanarPoints3D(p1, p2, p3, p4, engine);
        if (temp.isNIL()) {
          return F.NIL;
        }
        result.append(temp);
      }
      if (result.argSize() == 1) {
        return result.arg1();
      }
      return result;
    }

    /**
     * Gives <code>true</code>, if the point <code>p4</code> is on the plane defined through <code>
     * p1,p2,p3</code>. Return an equation equal to <code>0</code> for symbolic parameterized
     * points.
     *
     * @param p1 a point with 3 elements in list form (x,y,z coordinates)
     * @param p2 a point with 3 elements in list form (x,y,z coordinates)
     * @param p3 a point with 3 elements in list form (x,y,z coordinates)
     * @param p4 a point with 3 elements in list form (x,y,z coordinates)
     * @param engine
     */
    private static IExpr coplanarPoints3D(IAST p1, IAST p2, IAST p3, IAST p4, EvalEngine engine) {
      IExpr x1 = p1.arg1();
      IExpr y1 = p1.arg2();
      IExpr z1 = p1.arg3();
      IExpr x2 = p2.arg1();
      IExpr y2 = p2.arg2();
      IExpr z2 = p2.arg3();
      IExpr x3 = p3.arg1();
      IExpr y3 = p3.arg2();
      IExpr z3 = p3.arg3();

      IExpr a1 = z1.subtract(z2);
      IExpr b1 = y2.subtract(y1);
      IExpr a22 = x1.subtract(x3);
      IExpr b2 = y3.subtract(y1);
      IExpr b22 = y1.subtract(y3);
      IExpr c1 = x1.subtract(x2);
      IExpr c2 = z3.subtract(z1);

      IExpr px = p4.arg1();
      IExpr py = p4.arg2();
      IExpr pz = p4.arg3();

      IExpr times1 = S.Times.of(engine, a1,
          F.Plus(F.Times(py, a22), F.Times(x3, y1), F.Times(-1, x1, y3), F.Times(px, b2)));
      IExpr times2 = S.Times.of(engine, b1,
          F.Plus(F.Times(pz, a22), F.Times(x3, z1), F.Times(-1, x1, z3), F.Times(px, c2)));

      IExpr times3 = S.Times.of(engine, c1,
          F.Plus(F.Times(pz, b22), F.Times(y3, z1), F.Times(-1, y1, z3), F.Times(py, c2)));
      IExpr calc = F.Plus.of(engine, times1, times2, times3);

      if (calc.isZero()) {
        return S.True;
      } else if (calc.isNumber()) {
        return S.False;
      }
      if (calc.isPlusTimesPower()) {
        calc = engine.evaluate(F.Equal(F.Factor(calc), F.C0));
      }
      return calc;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>CollinearPoints({{x1,y1},{x2,y2},{a,b},...})
   * </code>
   * </pre>
   *
   * <p>
   * returns true if the point <code>{a,b]</code> is on the line defined by the first two points
   * <code>{x1,y1},{x2,y2}</code>.
   *
   * <pre>
   * <code>CollinearPoints({{x1,y1,z1},{x2,y2,z2},{a,b,c},...})
   * </code>
   * </pre>
   *
   * <p>
   * returns true if the point <code>{a,b,c]</code> is on the line defined by the first two points
   * <code>{x1,y1,z1},{x2,y2,z2}</code>.
   *
   * <p>
   * The line is defined by the first two <i>different</i> points of the list. A list of less than
   * three points and a list of identical points are collinear.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Collinearity">Wikipedia - Collinearity</a>
   * <li><a href="https://youtu.be/UDt9M8_zxlw">Youtube - Collinear Points in 3D (Ch1 Pr18)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; CollinearPoints({{1,2,3}, {3,8,1}, {7,20,-3}})
   * True
   * </code>
   * </pre>
   */
  private static class CollinearPoints extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        IAST listOfPoints = (IAST) ast.arg1();
        if (listOfPoints.argSize() > 0 && listOfPoints.argSize() <= 2) {
          // less than three points are always collinear
          return isPointList(listOfPoints) ? S.True : F.NIL;
        }
        if (listOfPoints.argSize() > 2 && listOfPoints.arg1().isList2()
            && listOfPoints.arg2().isList2()) {

          for (int i = 3; i < listOfPoints.size(); i++) {
            if (!listOfPoints.get(i).isList2()) {
              // `1` should be a non-empty list of points.
              return Errors.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
            }
          }
          return collinearPoints(listOfPoints, 2, engine);
        } else if (listOfPoints.argSize() > 2 && listOfPoints.arg1().isList3()
            && listOfPoints.arg2().isList3() && listOfPoints.arg3().isList3()) {

          for (int i = 4; i < listOfPoints.size(); i++) {
            if (!listOfPoints.get(i).isList3()) {
              // `1` should be a non-empty list of points.
              return Errors.printMessage(ast.topHead(), "pts", F.list(listOfPoints), engine);
            }
          }
          return collinearPoints(listOfPoints, 3, engine);
        }
      }

      return F.NIL;
    }

    /**
     * Test if all points of <code>listOfPoints</code> are on the line defined by the first two
     * different points of the list.
     *
     * @param listOfPoints a list of at least three points, which all have <code>dimension</code>
     *        coordinates
     * @param dimension the number of coordinates of each point; <code>2</code> or <code>3</code>
     * @param engine
     */
    private static IExpr collinearPoints(IAST listOfPoints, int dimension, EvalEngine engine) {
      IAST p1 = (IAST) listOfPoints.arg1();
      // the first point which is different from p1 defines the line together with p1
      int lineIndex = -1;
      for (int i = 2; i < listOfPoints.size(); i++) {
        if (!listOfPoints.get(i).equals(p1)) {
          lineIndex = i;
          break;
        }
      }
      if (lineIndex < 0) {
        // all points are identical
        return S.True;
      }
      IAST p2 = (IAST) listOfPoints.get(lineIndex);

      IASTAppendable result = F.ast(S.And, listOfPoints.argSize() - 2);
      for (int i = 2; i < listOfPoints.size(); i++) {
        if (i == lineIndex) {
          continue;
        }
        IAST p3 = (IAST) listOfPoints.get(i);
        IExpr temp = dimension == 2 //
            ? collinearPoints2D(p1, p2, p3, engine)
            : collinearPoints3D(p1, p2, p3, engine);
        if (temp.isNIL()) {
          return F.NIL;
        }
        result.append(temp);
      }
      if (result.argSize() == 1) {
        return result.arg1();
      }
      return result;
    }

    private static IExpr collinearPoints2D(IAST p1, IAST p2, IAST p3, EvalEngine engine) {
      IExpr x1 = p1.arg1();
      IExpr y1 = p1.arg2();
      IExpr x2 = p2.arg1();
      IExpr y2 = p2.arg2();

      IExpr px = p3.arg1();
      IExpr py = p3.arg2();

      IAST plus1 = F.Plus(px.times(y2.subtract(y1)), x2.times(y1));
      IAST plus2 = F.Plus(py.times(x2.subtract(x1)), x1.times(y2));
      IExpr calc = F.Subtract.of(engine, plus1, plus2);
      if (calc.isZero()) {
        return S.True;
      } else if (calc.isNumber()) {
        return S.False;
      }
      if (calc.isPlusTimesPower()) {
        calc = engine.evaluate(F.Equal(plus1, plus2));
      }
      return calc;
    }

    private static IExpr collinearPoints3D(IAST p1, IAST p2, IAST p3, EvalEngine engine) {
      IExpr x1 = p1.arg1();
      IExpr y1 = p1.arg2();
      IExpr z1 = p1.arg3();
      IExpr x2 = p2.arg1();
      IExpr y2 = p2.arg2();
      IExpr z2 = p2.arg3();
      IExpr x3 = p3.arg1();
      IExpr y3 = p3.arg2();
      IExpr z3 = p3.arg3();

      // vector p2-p1
      IExpr x21 = x2.subtract(x1);
      IExpr y21 = y2.subtract(y1);
      IExpr z21 = z2.subtract(z1);
      if (x21.isZero() && y21.isZero() && z21.isZero()) {
        // p1 and p2 are identical - they don't define a line, so every point is collinear with them
        return S.True;
      }
      // vector p3-p1
      IExpr x31 = x3.subtract(x1);
      IExpr y31 = y3.subtract(y1);
      IExpr z31 = z3.subtract(z1);
      // factors
      IExpr fx = F.C0;
      IExpr fy = F.C0;
      IExpr fz = F.C0;
      IASTAppendable equalAST = F.ast(S.Equal, 3);
      if (!x21.isZero()) {
        fx = x31.divide(x21);
        equalAST.append(fx);
      } else if (!x31.isZero()) {
        return S.False;
      }
      if (!y21.isZero()) {
        fy = y31.divide(y21);
        equalAST.append(fy);
      } else if (!y31.isZero()) {
        return S.False;
      }
      if (!z21.isZero()) {
        fz = z31.divide(z21);
        equalAST.append(fz);
      } else if (!z31.isZero()) {
        return S.False;
      }
      IExpr calc = engine.evaluate(equalAST);
      if (calc.isTrue()) {
        return S.True;
      } else if (calc.isFalse()) {
        return S.False;
      } else if (fx.isNumber() && fy.isNumber() && fz.isNumber()) {
        return S.False;
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  /**
   *
   *
   * <pre>
   * <code>CircularArcThrough({{x1,y1},{x2,y2},...})
   * </code>
   * </pre>
   *
   * <p>
   * returns the circular arc through the 2D points <code>{x1,y1},{x2,y2},...</code> as a
   * <code>Circle(center, radius, {startAngle, endAngle})</code> object.
   *
   * <pre>
   * <code>CircularArcThrough({{x1,y1},{x2,y2},...}, center)
   * </code>
   * </pre>
   *
   * <p>
   * returns the circular arc through the points around the given <code>center</code>.
   *
   * <pre>
   * <code>CircularArcThrough({{x1,y1},{x2,y2},...}, center, radius)
   * </code>
   * </pre>
   *
   * <p>
   * returns the circular arc through the points around the given <code>center</code> with the given
   * <code>radius</code>.
   *
   * <p>
   * Two points define the diameter of the circle, three or more points define the circle through
   * them. The arc starts at the smallest and ends at the largest angle of the points, measured
   * counter-clockwise from the center. If the points aren't on a common circle or don't define one,
   * the expression is returned unevaluated.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Circular_arc">Wikipedia - Circular arc</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; CircularArcThrough({{1,0}, {0,1}, {-1,0}})
   * Circle({0,0},1,{0,Pi})
   *
   * &gt;&gt; CircularArcThrough({{3,0}, {0,3}}, {0,0})
   * Circle({0,0},3,{0,Pi/2})
   * </code>
   * </pre>
   */
  private static class CircularArcThrough extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      IAST listOfPoints = (IAST) ast.arg1();
      if (listOfPoints.argSize() < 2) {
        return F.NIL;
      }
      for (int i = 1; i < listOfPoints.size(); i++) {
        if (!listOfPoints.get(i).isList2()) {
          return F.NIL;
        }
      }

      IAST center;
      if (ast.argSize() >= 2 && ast.arg2() != S.Automatic) {
        if (!ast.arg2().isList2()) {
          return F.NIL;
        }
        center = (IAST) ast.arg2();
      } else {
        center = circleCenter(listOfPoints, engine);
        if (center.isNIL()) {
          return F.NIL;
        }
      }

      // all points must have the same distance from the center
      IExpr squaredRadius = squaredDistance(center, (IAST) listOfPoints.arg1());
      if (isPossibleZero(squaredRadius, engine)) {
        return F.NIL;
      }
      for (int i = 2; i < listOfPoints.size(); i++) {
        IExpr squared = squaredDistance(center, (IAST) listOfPoints.get(i));
        if (!isPossibleZero(F.eval(squared.subtract(squaredRadius)), engine)) {
          return F.NIL;
        }
      }
      if (ast.argSize() == 3) {
        IExpr radius = ast.arg3();
        if (radius.isNegativeResult()
            || !isPossibleZero(F.eval(radius.times(radius).subtract(squaredRadius)), engine)) {
          return F.NIL;
        }
      }

      // the arc reaches from the smallest to the largest angle of the points
      IExpr startAngle = F.NIL;
      IExpr endAngle = F.NIL;
      double minimum = Double.POSITIVE_INFINITY;
      double maximum = Double.NEGATIVE_INFINITY;
      for (int i = 1; i < listOfPoints.size(); i++) {
        IAST point = (IAST) listOfPoints.get(i);
        IExpr dx = F.eval(point.arg1().subtract(center.arg1()));
        IExpr dy = F.eval(point.arg2().subtract(center.arg2()));
        if (!dx.isNumericFunction() || !dy.isNumericFunction()) {
          return F.NIL;
        }
        IExpr angle = F.eval(F.ArcTan(dx, dy));
        // the angle itself can be an exact expression which isn't machine-sized numeric
        double value = Math.atan2(engine.evalDouble(dy, null, Double.NaN),
            engine.evalDouble(dx, null, Double.NaN));
        if (Double.isNaN(value)) {
          return F.NIL;
        }
        if (value < 0.0) {
          // normalize the angle to the range 0 <= angle < 2*Pi
          angle = F.eval(F.Plus(angle, F.C2Pi));
          value += 2.0 * Math.PI;
        }
        if (value < minimum) {
          minimum = value;
          startAngle = angle;
        }
        if (value > maximum) {
          maximum = value;
          endAngle = angle;
        }
      }
      return F.Circle(center, F.eval(F.Sqrt(squaredRadius)), F.list(startAngle, endAngle));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

  }

  private static class VectorGreater extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList2()) {
        IAST listOfVectors = (IAST) arg1;
        IExpr arg11 = listOfVectors.arg1();
        IExpr arg12 = listOfVectors.arg2();
        return compareRecursive(arg11, arg12, engine);
      }
      return F.NIL;
    }


    private IExpr compareRecursive(IExpr arg11, IExpr arg12, EvalEngine engine) {
      int n1 = -1;
      int n2 = -1;
      ITensorAccess v1 = F.NIL;
      ITensorAccess v2 = F.NIL;
      if (arg11 instanceof ITensorAccess) {
        v1 = (ITensorAccess) arg11;
        IntArrayList dim1 = LinearAlgebraUtil.dimensions(v1, S.List);
        if (dim1.size() < 1) {
          return F.NIL;
        }
        n1 = dim1.getInt(0);
      }
      if (arg12 instanceof ITensorAccess) {
        v2 = (ITensorAccess) arg12;
        IntArrayList dim2 = LinearAlgebraUtil.dimensions(v2, S.List);
        if (dim2.size() < 1) {
          return F.NIL;
        }
        n2 = dim2.getInt(0);
        if (n1 > 0 && n1 != n2) {
          return S.False;
        }
      }
      if (arg11.isReal()) {
        if (arg12.isReal()) {
          v1 = F.List(arg11);
          v2 = F.List(arg12);
        } else {
          if (n2 > 0) {
            v1 = F.constantArray(arg11, n2);
          }
        }
      } else {
        if (arg12.isReal()) {
          if (n1 > 0) {
            v2 = F.constantArray(arg12, n1);
          }
        }
      }
      if (v1.isPresent() && v2.isPresent()) {
        for (int i = 1; i < v1.size(); i++) {
          IExpr subV1 = v1.get(i);
          IExpr subV2 = v2.get(i);
          if (subV1 instanceof ITensorAccess || subV2 instanceof ITensorAccess) {
            final IExpr compareResult = compareRecursive(subV1, subV2, engine);
            if (compareResult.isPresent()) {
              if (compareResult.isTrue()) {
                continue;
              } else if (compareResult.isFalse()) {
                return S.False;
              }
            }
            // undecidable
            return F.NIL;
          }
          IExpr a1 = v1.get(i);
          IExpr a2 = v2.get(i);

          final IExpr compareResult = compare(a1, a2, engine);
          if (compareResult.isPresent()) {
            if (compareResult.isTrue()) {
              continue;
            } else if (compareResult.isFalse()) {
              return S.False;
            }
          }
          // undecidable
          return F.NIL;
        }
        return S.True;
      }
      return F.NIL;
    }


    protected IExpr compare(IExpr v1, IExpr v2, EvalEngine engine) {
      return S.Greater.ofNIL(engine, v1, v2);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class VectorGreaterEqual extends VectorGreater {

    @Override
    protected IExpr compare(IExpr v1, IExpr v2, EvalEngine engine) {
      return S.GreaterEqual.ofNIL(engine, v1, v2);
    }

  }

  private static class VectorLess extends VectorGreater {

    @Override
    protected IExpr compare(IExpr v1, IExpr v2, EvalEngine engine) {
      return S.Less.ofNIL(engine, v1, v2);
    }

  }

  private static class VectorLessEqual extends VectorGreater {

    @Override
    protected IExpr compare(IExpr v1, IExpr v2, EvalEngine engine) {
      return S.LessEqual.ofNIL(engine, v1, v2);
    }

  }

  /**
   * <code>GeometricTest(objects, "property"...)</code> - test one or more geometric properties of
   * <code>objects</code>. All the requested properties have to hold.
   *
   * <p>
   * The shape of <code>objects</code> depends on the property, not on the argument count: a point
   * list for <code>"Collinear"</code>, a single region for <code>"Convex"</code>,
   * <code>"Regular"</code> and <code>"Rectangle"</code>, and a list of exactly two objects for the
   * relational properties <code>"Parallel"</code>, <code>"Perpendicular"</code>,
   * <code>"Similar"</code> and <code>"Congruent"</code>.
   */
  private static class GeometricTest extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr objects = ast.arg1();
      IASTAppendable result = F.ast(S.And, ast.argSize() - 1);
      for (int i = 2; i < ast.size(); i++) {
        if (!ast.get(i).isString()) {
          return F.NIL;
        }
        IExpr test = test(objects, ast.get(i).toString(), engine);
        if (test.isNIL()) {
          // an unsupported property leaves the whole expression unevaluated rather than silently
          // dropping a conjunct
          return F.NIL;
        }
        result.append(test);
      }
      if (result.argSize() == 1) {
        return result.arg1();
      }
      return engine.evaluate(result);
    }

    private static IExpr test(IExpr objects, String property, EvalEngine engine) {
      switch (property) {
        case "Collinear":
          return S.CollinearPoints.ofNIL(engine, objects);
        case "Convex":
          return S.ConvexRegionQ.ofNIL(engine, objects);
        case "Regular":
          return regularQ(vertices(objects), engine);
        case "Rectangle":
          return rectangleQ(vertices(objects), engine);
        case "Parallel":
        case "Perpendicular":
          return directionTest(objects, "Parallel".equals(property), engine);
        case "Similar":
        case "Congruent":
          return shapeTest(objects, "Congruent".equals(property), engine);
        default:
          return F.NIL;
      }
    }

    /**
     * The corner points of a region, or the list itself if it already is a list of points.
     *
     * @return {@link F#NIL} if no point list can be read off
     */
    private static IAST vertices(IExpr object) {
      if (object.isAST(S.Polygon, 2) || object.isAST(S.Triangle, 2) || object.isAST(S.Line, 2)
          || object.isAST(S.InfiniteLine, 2) || object.isAST(S.HalfLine, 2)) {
        IExpr points = object.first();
        return points.isListOfLists() ? (IAST) points : F.NIL;
      }
      return object.isListOfLists() ? (IAST) object : F.NIL;
    }

    /** Equilateral and equiangular: equal side lengths alone would also admit a rhombus. */
    private static IExpr regularQ(IAST corners, EvalEngine engine) {
      if (corners.isNIL() || corners.argSize() < 3) {
        return F.NIL;
      }
      IAST sides = squaredSides(corners, engine);
      IAST radii = squaredCircumradii(corners, engine);
      if (sides.isNIL() || radii.isNIL()) {
        return F.NIL;
      }
      return allEqual(sides, engine).isTrue() && allEqual(radii, engine).isTrue() //
          ? S.True
          : S.False;
    }

    /** Four corners meeting at four right angles. */
    private static IExpr rectangleQ(IAST corners, EvalEngine engine) {
      if (corners.isNIL() || corners.argSize() != 4) {
        return corners.isNIL() ? F.NIL : S.False;
      }
      for (int i = 1; i <= 4; i++) {
        IAST previous = (IAST) corners.get(i == 1 ? 4 : i - 1);
        IAST corner = (IAST) corners.get(i);
        IAST next = (IAST) corners.get(i == 4 ? 1 : i + 1);
        IExpr dot = dot(difference(previous, corner, engine), difference(next, corner, engine),
            engine);
        if (dot.isNIL()) {
          return F.NIL;
        }
        if (!dot.isZero()) {
          return S.False;
        }
      }
      return S.True;
    }

    /** Parallel or perpendicular directions of two lines. */
    private static IExpr directionTest(IExpr objects, boolean parallel, EvalEngine engine) {
      IAST pair = objectPair(objects);
      if (pair.isNIL()) {
        return F.NIL;
      }
      IAST first = vertices(pair.arg1());
      IAST second = vertices(pair.arg2());
      if (first.isNIL() || second.isNIL() || first.argSize() < 2 || second.argSize() < 2) {
        return F.NIL;
      }
      IAST d1 = direction(first, engine);
      IAST d2 = direction(second, engine);
      if (d1.isNIL() || d2.isNIL() || d1.argSize() != d2.argSize()) {
        return F.NIL;
      }
      // Perpendicular is the vanishing dot product; parallel is the vanishing cross product, which
      // in the plane is the single determinant below.
      IExpr measure;
      if (parallel) {
        if (d1.argSize() != 2) {
          return F.NIL;
        }
        measure = engine.evaluate(F.Subtract(F.Times(d1.arg1(), d2.arg2()), //
            F.Times(d1.arg2(), d2.arg1())));
      } else {
        measure = dot(d1, d2, engine);
      }
      return measure.isNIL() ? F.NIL : (measure.isZero() ? S.True : S.False);
    }

    /** Equal side lengths (congruent) or proportional side lengths (similar). */
    private static IExpr shapeTest(IExpr objects, boolean congruent, EvalEngine engine) {
      IAST pair = objectPair(objects);
      if (pair.isNIL()) {
        return F.NIL;
      }
      IAST first = vertices(pair.arg1());
      IAST second = vertices(pair.arg2());
      if (first.isNIL() || second.isNIL() || first.argSize() != second.argSize()) {
        return first.isNIL() || second.isNIL() ? F.NIL : S.False;
      }
      double[] a = sortedSideLengths(first, engine);
      double[] b = sortedSideLengths(second, engine);
      if (a == null || b == null) {
        return F.NIL;
      }
      if (congruent) {
        for (int i = 0; i < a.length; i++) {
          if (!isClose(a[i], b[i])) {
            return S.False;
          }
        }
        return S.True;
      }
      // Similar: one common ratio across every corresponding pair of sides.
      if (b[0] == 0.0) {
        return S.False;
      }
      double ratio = a[0] / b[0];
      for (int i = 1; i < a.length; i++) {
        if (b[i] == 0.0 || !isClose(a[i] / b[i], ratio)) {
          return S.False;
        }
      }
      return S.True;
    }

    private static boolean isClose(double x, double y) {
      return Math.abs(x - y) <= 1.0e-10 * Math.max(1.0, Math.max(Math.abs(x), Math.abs(y)));
    }

    /** A list of exactly two geometric objects, as the relational properties expect. */
    private static IAST objectPair(IExpr objects) {
      return objects.isList2() ? (IAST) objects : F.NIL;
    }

    private static IAST direction(IAST points, EvalEngine engine) {
      return difference((IAST) points.arg2(), (IAST) points.arg1(), engine);
    }

    private static IAST difference(IAST p, IAST q, EvalEngine engine) {
      if (p.argSize() != q.argSize()) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(p.argSize());
      for (int i = 1; i < p.size(); i++) {
        result.append(engine.evaluate(F.Subtract(p.get(i), q.get(i))));
      }
      return result;
    }

    private static IExpr dot(IAST u, IAST v, EvalEngine engine) {
      if (u.isNIL() || v.isNIL() || u.argSize() != v.argSize()) {
        return F.NIL;
      }
      IASTAppendable sum = F.ast(S.Plus, u.argSize());
      for (int i = 1; i < u.size(); i++) {
        sum.append(F.Times(u.get(i), v.get(i)));
      }
      return engine.evaluate(sum);
    }

    /** Squared lengths of the closed edge cycle, so no square roots enter the comparison. */
    private static IAST squaredSides(IAST corners, EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(corners.argSize());
      for (int i = 1; i < corners.size(); i++) {
        if (!corners.get(i).isList()) {
          return F.NIL;
        }
        IAST from = (IAST) corners.get(i);
        IAST to = (IAST) corners.get(i == corners.argSize() ? 1 : i + 1);
        IAST edge = difference(to, from, engine);
        IExpr squared = dot(edge, edge, engine);
        if (squared.isNIL()) {
          return F.NIL;
        }
        result.append(squared);
      }
      return result;
    }

    /** Squared distances from the centroid; equal for a regular polygon. */
    private static IAST squaredCircumradii(IAST corners, EvalEngine engine) {
      int n = corners.argSize();
      if (!corners.arg1().isList()) {
        return F.NIL;
      }
      int dimension = ((IAST) corners.arg1()).argSize();
      IASTAppendable centroid = F.ListAlloc(dimension);
      for (int c = 1; c <= dimension; c++) {
        IASTAppendable sum = F.ast(S.Plus, n);
        for (int i = 1; i < corners.size(); i++) {
          if (!corners.get(i).isList() || ((IAST) corners.get(i)).argSize() != dimension) {
            return F.NIL;
          }
          sum.append(((IAST) corners.get(i)).get(c));
        }
        centroid.append(engine.evaluate(F.Divide(sum, F.ZZ(n))));
      }
      IASTAppendable result = F.ListAlloc(n);
      for (int i = 1; i < corners.size(); i++) {
        IAST radius = difference((IAST) corners.get(i), centroid, engine);
        IExpr squared = dot(radius, radius, engine);
        if (squared.isNIL()) {
          return F.NIL;
        }
        result.append(squared);
      }
      return result;
    }

    private static IExpr allEqual(IAST values, EvalEngine engine) {
      for (int i = 2; i < values.size(); i++) {
        IExpr equal = engine.evaluate(F.Equal(values.arg1(), values.get(i)));
        if (!equal.isTrue()) {
          return S.False;
        }
      }
      return S.True;
    }

    /**
     * Side lengths in ascending order, so two shapes can be compared without knowing which corner
     * matches which. Returns {@code null} when a length is not a real number.
     */
    private static double[] sortedSideLengths(IAST corners, EvalEngine engine) {
      IAST squared = squaredSides(corners, engine);
      if (squared.isNIL()) {
        return null;
      }
      double[] lengths = new double[squared.argSize()];
      for (int i = 1; i < squared.size(); i++) {
        IExpr value = engine.evalN(squared.get(i));
        if (!value.isReal()) {
          return null;
        }
        lengths[i - 1] = Math.sqrt(value.evalf());
      }
      Arrays.sort(lengths);
      return lengths;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }
  }

  /**
   * <code>ConvexPolyhedronQ(region)</code> - test whether <code>region</code> is a convex
   * polyhedron.
   *
   * <p>
   * Two conditions, and both matter: the region has to be a <i>polyhedron</i> - bounded by flat
   * faces, in three dimensions - and it has to be convex. A <code>Ball</code> is convex but has no
   * faces, and <code>Simplex(2)</code> is a convex triangle but lives in the plane; both are
   * <code>False</code>.
   */
  private static class ConvexPolyhedronQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Region, 1)) {
        arg1 = arg1.first();
      }
      arg1 = MeshFunctions.normalizeRegion(arg1);
      if (!arg1.isAST()) {
        return F.NIL;
      }
      IAST region = (IAST) arg1;
      if (!region.head().isBuiltInSymbol()) {
        return F.NIL;
      }
      switch (((IBuiltInSymbol) region.head()).ordinal()) {
        case ID.Cube:
        case ID.Hexahedron:
        case ID.Tetrahedron:
        case ID.Octahedron:
        case ID.Dodecahedron:
        case ID.Icosahedron:
        case ID.Cuboid:
        case ID.Parallelepiped:
          return S.True;
        case ID.Simplex:
          // Simplex(n) is n-dimensional; only the 3-simplex is a polyhedron.
          return region.isAST1() ? F.bool(region.arg1().equals(F.C3)) : F.NIL;
        case ID.Prism:
          return prismConvexQ(region, engine);
        case ID.Ball:
        case ID.Sphere:
        case ID.Ellipsoid:
        case ID.Cylinder:
        case ID.Cone:
        case ID.Disk:
        case ID.Circle:
        case ID.Rectangle:
        case ID.Triangle:
        case ID.Polygon:
          // curved, or not three-dimensional
          return S.False;
        default:
          return F.NIL;
      }
    }

    /**
     * A <code>Prism</code> of <code>2n</code> corners is the solid between two <code>n</code>-gons.
     * It is convex exactly when the base is convex and the top is the base shifted by one constant
     * vector; any other top makes the lateral faces non-planar.
     */
    private static IExpr prismConvexQ(IAST prism, EvalEngine engine) {
      if (!prism.isAST1() || !prism.arg1().isListOfLists()) {
        return F.NIL;
      }
      IAST corners = (IAST) prism.arg1();
      int size = corners.argSize();
      if (size < 6 || size % 2 != 0) {
        return F.NIL;
      }
      int n = size / 2;
      IASTAppendable base = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        base.append(corners.get(i));
      }
      // Every top corner has to sit at the same offset from its base corner.
      IExpr offset = engine.evaluate(F.Subtract(corners.get(n + 1), corners.arg1()));
      for (int i = 2; i <= n; i++) {
        IExpr other = engine.evaluate(F.Subtract(corners.get(n + i), corners.get(i)));
        if (!engine.evaluate(F.Equal(offset, other)).isTrue()) {
          return S.False;
        }
      }
      IExpr convexBase = base.arg1().isList2() //
          ? MeshFunctions.convexPointCycleQ(base, engine)
          : convexPlanarCycleQ(base, engine);
      return convexBase.isTrue() ? S.True : (convexBase.isFalse() ? S.False : F.NIL);
    }

    /**
     * Convexity of a planar point cycle given in 3D. {@link MeshFunctions#convexPointCycleQ} only
     * accepts 2D points, but the base face of a prism is embedded in space, so the turn direction
     * is a cross product rather than a scalar: the polygon is convex when every turn points the
     * same way, i.e. no two cross products oppose each other.
     */
    private static IExpr convexPlanarCycleQ(IAST points, EvalEngine engine) {
      int size = points.argSize();
      if (size < 3) {
        return F.NIL;
      }
      IAST reference = F.NIL;
      for (int i = 1; i <= size; i++) {
        IExpr a = points.get(i);
        IExpr b = points.get(i % size + 1);
        IExpr c = points.get((i + 1) % size + 1);
        if (!a.isList3() || !b.isList3() || !c.isList3()) {
          return F.NIL;
        }
        IAST turn = cross(difference((IAST) b, (IAST) a, engine),
            difference((IAST) c, (IAST) b, engine), engine);
        if (turn.isNIL()) {
          return F.NIL;
        }
        IExpr norm = dot(turn, turn, engine);
        if (norm.isNIL()) {
          return F.NIL;
        }
        if (norm.isZero()) {
          // three collinear corners bend neither way
          continue;
        }
        if (reference.isNIL()) {
          reference = turn;
          continue;
        }
        IExpr orientation = dot(turn, reference, engine);
        if (orientation.isNIL()) {
          return F.NIL;
        }
        if (orientation.isNegativeResult()) {
          return S.False;
        }
        if (!orientation.isPositiveResult()) {
          return F.NIL;
        }
      }
      return reference.isNIL() ? F.NIL : S.True;
    }

    private static IAST cross(IAST u, IAST v, EvalEngine engine) {
      if (u.isNIL() || v.isNIL() || u.argSize() != 3 || v.argSize() != 3) {
        return F.NIL;
      }
      return F.List( //
          engine.evaluate(
              F.Subtract(F.Times(u.arg2(), v.arg3()), F.Times(u.arg3(), v.arg2()))), //
          engine.evaluate(
              F.Subtract(F.Times(u.arg3(), v.arg1()), F.Times(u.arg1(), v.arg3()))), //
          engine.evaluate(
              F.Subtract(F.Times(u.arg1(), v.arg2()), F.Times(u.arg2(), v.arg1()))));
    }

    private static IAST difference(IAST p, IAST q, EvalEngine engine) {
      if (p.argSize() != q.argSize()) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(p.argSize());
      for (int i = 1; i < p.size(); i++) {
        result.append(engine.evaluate(F.Subtract(p.get(i), q.get(i))));
      }
      return result;
    }

    private static IExpr dot(IAST u, IAST v, EvalEngine engine) {
      if (u.isNIL() || v.isNIL() || u.argSize() != v.argSize()) {
        return F.NIL;
      }
      IASTAppendable sum = F.ast(S.Plus, u.argSize());
      for (int i = 1; i < u.size(); i++) {
        sum.append(F.Times(u.get(i), v.get(i)));
      }
      return engine.evaluate(sum);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>RegionProduct(r1, r2)</code> - the Cartesian product of two regions.
   *
   * <p>
   * Only products that have a name of their own are built; anything else stays unevaluated rather
   * than inventing a representation for it.
   */
  private static class RegionProduct extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr first = MeshFunctions.normalizeRegion(ast.arg1());
      IExpr second = MeshFunctions.normalizeRegion(ast.arg2());

      IAST firstExtent = lineExtent(first);
      IAST secondExtent = lineExtent(second);

      // an interval times an interval is an axis-aligned rectangle
      if (firstExtent.isPresent() && secondExtent.isPresent()) {
        return F.binaryAST2(S.Rectangle,
            F.List(firstExtent.arg1(), secondExtent.arg1()), //
            F.List(firstExtent.arg2(), secondExtent.arg2()));
      }
      // a planar region extruded along an interval
      if (secondExtent.isPresent()) {
        return extrude(first, secondExtent.arg1(), secondExtent.arg2(), engine);
      }
      return F.NIL;
    }

    /**
     * The endpoints of a one-dimensional region. Both <code>Interval({a, b})</code> and a
     * <code>Line</code> between two 1-coordinate points denote the same segment.
     */
    private static IAST lineExtent(IExpr region) {
      if (region.isAST(S.Interval, 2) && region.first().isList2()) {
        return (IAST) region.first();
      }
      if (region.isAST(S.Line, 2) && region.first().isListOfLists()) {
        IAST points = (IAST) region.first();
        if (points.argSize() == 2 && points.arg1().isList1() && points.arg2().isList1()) {
          return F.List(points.arg1().first(), points.arg2().first());
        }
      }
      return F.NIL;
    }

    /** Sweep a planar region from height {@code low} to {@code high}. */
    private static IExpr extrude(IExpr base, IExpr low, IExpr high, EvalEngine engine) {
      if (base.isAST(S.Disk, 2) || base.isAST(S.Disk, 3)) {
        IExpr center = base.first();
        if (!center.isList2()) {
          return F.NIL;
        }
        IExpr radius = base.isAST(S.Disk, 3) ? base.second() : F.C1;
        return F.binaryAST2(S.Cylinder, F.List( //
            F.List(center.first(), center.second(), low), //
            F.List(center.first(), center.second(), high)), radius);
      }
      IAST corners = polygonCorners(base);
      if (corners.isPresent()) {
        IASTAppendable prism = F.ListAlloc(corners.argSize() * 2);
        appendLifted(prism, corners, low);
        appendLifted(prism, corners, high);
        return F.unaryAST1(S.Prism, prism);
      }
      return F.NIL;
    }

    private static void appendLifted(IASTAppendable result, IAST corners, IExpr height) {
      for (int i = 1; i < corners.size(); i++) {
        IAST point = (IAST) corners.get(i);
        result.append(F.List(point.arg1(), point.arg2(), height));
      }
    }

    /** The 2D corner cycle of a polygonal region. {@code Triangle()} is the standard triangle. */
    private static IAST polygonCorners(IExpr region) {
      if (region.isAST(S.Triangle, 1)) {
        return F.List(F.List(F.C0, F.C0), F.List(F.C1, F.C0), F.List(F.C0, F.C1));
      }
      if (region.isAST(S.Triangle, 2) || region.isAST(S.Polygon, 2)) {
        IExpr points = region.first();
        if (points.isListOfLists() && ((IAST) points).forAll(p -> p.isList2())) {
          return (IAST) points;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>Circumsphere(points)</code> - the sphere through <code>n + 1</code> points in
   * <code>n</code> dimensions.
   *
   * <p>
   * The result is a <code>Sphere</code> in every dimension, so the circumcircle of three points in
   * the plane also comes back as <code>Sphere</code>.
   */
  private static class Circumsphere extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isListOfLists()) {
        return F.NIL;
      }
      IAST points = (IAST) ast.arg1();
      int count = points.argSize();
      if (count < 2 || !points.arg1().isList()) {
        return F.NIL;
      }
      int dimension = ((IAST) points.arg1()).argSize();
      if (count != dimension + 1) {
        return F.NIL;
      }
      for (int i = 1; i < points.size(); i++) {
        if (!points.get(i).isList() || ((IAST) points.get(i)).argSize() != dimension) {
          return F.NIL;
        }
      }
      // Every point is equidistant from the centre x, so subtracting the equation for p0 from the
      // one for pi clears the quadratic term and leaves n linear equations:
      // 2*(pi - p0).x == |pi|^2 - |p0|^2
      IAST first = (IAST) points.arg1();
      IExpr firstNorm = squaredNorm(first, engine);
      IASTAppendable matrix = F.ListAlloc(dimension);
      IASTAppendable rhs = F.ListAlloc(dimension);
      for (int i = 2; i < points.size(); i++) {
        IAST point = (IAST) points.get(i);
        IASTAppendable row = F.ListAlloc(dimension);
        for (int c = 1; c <= dimension; c++) {
          row.append(F.Times(F.C2, F.Subtract(point.get(c), first.get(c))));
        }
        matrix.append(row);
        rhs.append(F.Subtract(squaredNorm(point, engine), firstNorm));
      }
      IExpr center = S.LinearSolve.ofNIL(engine, matrix, rhs);
      if (center.isNIL() || !center.isList() || ((IAST) center).argSize() != dimension) {
        // degenerate: the points are collinear / coplanar and have no circumsphere
        return F.NIL;
      }
      IExpr radius = engine.evaluate(F.Simplify(F.EuclideanDistance(center, first)));
      return F.binaryAST2(S.Sphere, center, radius);
    }

    private static IExpr squaredNorm(IAST point, EvalEngine engine) {
      IASTAppendable sum = F.ast(S.Plus, point.argSize());
      for (int i = 1; i < point.size(); i++) {
        sum.append(F.Sqr(point.get(i)));
      }
      return engine.evaluate(sum);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>PolyhedronData("name", "property")</code> - properties of a named polyhedron.
   *
   * <p>
   * The combinatorics are <b>derived</b>, not tabulated: vertices come from their coordinate
   * definition, edges are the shortest vertex pairs, and faces follow from those. Only the scalar
   * properties that have no such derivation (exact volumes, circumradii) are listed, and a property
   * that has not been entered returns <code>Missing(NotAvailable)</code> rather than a guess.
   */
  private static class PolyhedronData extends AbstractEvaluator {

    private static final double GOLDEN = (1.0 + Math.sqrt(5.0)) / 2.0;

    /** Squared-distance slack when deciding which vertices are nearest neighbours. */
    private static final double TOLERANCE = 1.0e-6;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST2() || !ast.arg1().isString() || !ast.arg2().isString()) {
        return F.NIL;
      }
      String name = ast.arg1().toString();
      String property = ast.arg2().toString();
      double[][] vertices = vertices(name);
      switch (property) {
        case "Volume":
          return volume(name);
        case "Circumradius":
          return circumradius(name);
        default:
          break;
      }
      if (vertices == null) {
        return F.NIL;
      }
      List<int[]> faces = faces(name, vertices);
      if (faces == null) {
        return F.Missing(S.NotAvailable);
      }
      switch (property) {
        case "VertexCount":
          return F.ZZ(vertices.length);
        case "FaceCount":
          return F.ZZ(faces.size());
        case "EdgeCount":
          return F.ZZ(edges(vertices).size());
        case "FaceIndices":
          return faceIndices(faces);
        case "Faces":
          return F.binaryAST2(S.GraphicsComplex, coordinates(vertices),
              F.unaryAST1(S.Polygon, faceIndices(faces)));
        default:
          return F.Missing(S.NotAvailable);
      }
    }

    private static IExpr volume(String name) {
      switch (name) {
        case "Icosidodecahedron":
          // (45 + 17*Sqrt(5))/6
          return F.Divide(F.Plus(F.ZZ(45), F.Times(F.ZZ(17), F.Sqrt(F.C5))), F.C6);
        default:
          return F.Missing(S.NotAvailable);
      }
    }

    private static IExpr circumradius(String name) {
      switch (name) {
        case "RhombicTriacontahedron":
          // Its vertices lie on two different spheres, so no single circumradius exists.
          return F.Missing(S.NotApplicable);
        default:
          return F.Missing(S.NotAvailable);
      }
    }

    /** Vertex coordinates, or {@code null} when the solid is not known here. */
    private static double[][] vertices(String name) {
      switch (name) {
        case "Icosahedron":
          return icosahedron();
        case "TruncatedIcosahedron":
          return truncatedIcosahedron();
        default:
          return null;
      }
    }

    /** The cyclic permutations of {@code (0, +-1, +-golden)}. */
    private static double[][] icosahedron() {
      double[][] result = new double[12][];
      int k = 0;
      for (int s1 = 1; s1 >= -1; s1 -= 2) {
        for (int s2 = 1; s2 >= -1; s2 -= 2) {
          result[k++] = new double[] {0, s1, s2 * GOLDEN};
          result[k++] = new double[] {s1, s2 * GOLDEN, 0};
          result[k++] = new double[] {s2 * GOLDEN, 0, s1};
        }
      }
      return result;
    }

    /** Each icosahedron edge is cut at its two third-points, giving 60 vertices. */
    private static double[][] truncatedIcosahedron() {
      double[][] seed = icosahedron();
      List<int[]> seedEdges = edges(seed);
      double[][] result = new double[seedEdges.size() * 2][];
      int k = 0;
      for (int[] edge : seedEdges) {
        result[k++] = along(seed[edge[0]], seed[edge[1]], 1.0 / 3.0);
        result[k++] = along(seed[edge[0]], seed[edge[1]], 2.0 / 3.0);
      }
      return result;
    }

    private static double[] along(double[] from, double[] to, double t) {
      double[] result = new double[from.length];
      for (int i = 0; i < from.length; i++) {
        result[i] = from[i] + (to[i] - from[i]) * t;
      }
      return result;
    }

    /** Vertex pairs at the (equal) shortest distance. */
    private static List<int[]> edges(double[][] vertices) {
      double shortest = Double.MAX_VALUE;
      for (int i = 0; i < vertices.length; i++) {
        for (int j = i + 1; j < vertices.length; j++) {
          shortest = Math.min(shortest, squaredDistance(vertices[i], vertices[j]));
        }
      }
      List<int[]> result = new ArrayList<int[]>();
      for (int i = 0; i < vertices.length; i++) {
        for (int j = i + 1; j < vertices.length; j++) {
          if (squaredDistance(vertices[i], vertices[j]) - shortest < TOLERANCE) {
            result.add(new int[] {i, j});
          }
        }
      }
      return result;
    }

    /**
     * Faces as 0-based vertex index cycles, or {@code null} when they are not derived here.
     *
     * <p>
     * The truncated icosahedron's faces come from the solid it was cut from: one pentagon around
     * each of the 12 original vertices and one hexagon in each of the 20 original faces. Pentagons
     * are emitted first, which is the order Mathematica reports.
     */
    private static List<int[]> faces(String name, double[][] vertices) {
      if ("Icosahedron".equals(name)) {
        return triangularFaces(vertices);
      }
      if ("TruncatedIcosahedron".equals(name)) {
        double[][] seed = icosahedron();
        List<int[]> result = new ArrayList<int[]>();
        for (double[] corner : seed) {
          result.add(cycleAround(vertices, corner, 5));
        }
        for (int[] face : triangularFaces(seed)) {
          double[] centre = centroid(seed, face);
          result.add(cycleAround(vertices, centre, 6));
        }
        return result;
      }
      return null;
    }

    /** For a deltahedron: every mutually adjacent triple of vertices is a face. */
    private static List<int[]> triangularFaces(double[][] vertices) {
      boolean[][] adjacent = new boolean[vertices.length][vertices.length];
      for (int[] edge : edges(vertices)) {
        adjacent[edge[0]][edge[1]] = true;
        adjacent[edge[1]][edge[0]] = true;
      }
      List<int[]> result = new ArrayList<int[]>();
      for (int i = 0; i < vertices.length; i++) {
        for (int j = i + 1; j < vertices.length; j++) {
          for (int k = j + 1; k < vertices.length; k++) {
            if (adjacent[i][j] && adjacent[i][k] && adjacent[j][k]) {
              result.add(new int[] {i, j, k});
            }
          }
        }
      }
      return result;
    }

    /** The {@code expected} vertices nearest {@code centre}, ordered around it. */
    private static int[] cycleAround(double[][] vertices, double[] centre, int expected) {
      Integer[] order = new Integer[vertices.length];
      for (int i = 0; i < vertices.length; i++) {
        order[i] = Integer.valueOf(i);
      }
      Arrays.sort(order, Comparator.comparingDouble(i -> squaredDistance(vertices[i], centre)));
      int[] face = new int[expected];
      for (int i = 0; i < expected; i++) {
        face[i] = order[i].intValue();
      }
      return sortAroundCentre(vertices, face, centre);
    }

    /**
     * Order a face's vertices by their angle in the face plane, so the result is a boundary cycle
     * rather than an arbitrary set.
     */
    private static int[] sortAroundCentre(double[][] vertices, int[] face, double[] centre) {
      double[] normal = centre.clone();
      double[] first = subtract(vertices[face[0]], centre);
      double[] axis2 = cross(normal, first);
      Integer[] boxed = new Integer[face.length];
      for (int i = 0; i < face.length; i++) {
        boxed[i] = Integer.valueOf(face[i]);
      }
      Arrays.sort(boxed, Comparator.comparingDouble(v -> {
        double[] radial = subtract(vertices[v.intValue()], centre);
        return Math.atan2(dotProduct(radial, axis2), dotProduct(radial, first));
      }));
      int[] result = new int[face.length];
      for (int i = 0; i < face.length; i++) {
        result[i] = boxed[i].intValue();
      }
      return result;
    }

    private static double[] centroid(double[][] vertices, int[] face) {
      double[] result = new double[vertices[face[0]].length];
      for (int index : face) {
        for (int c = 0; c < result.length; c++) {
          result[c] += vertices[index][c] / face.length;
        }
      }
      return result;
    }

    private static double[] subtract(double[] u, double[] v) {
      double[] result = new double[u.length];
      for (int i = 0; i < u.length; i++) {
        result[i] = u[i] - v[i];
      }
      return result;
    }

    private static double[] cross(double[] u, double[] v) {
      return new double[] {u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2],
          u[0] * v[1] - u[1] * v[0]};
    }

    private static double dotProduct(double[] u, double[] v) {
      double result = 0;
      for (int i = 0; i < u.length; i++) {
        result += u[i] * v[i];
      }
      return result;
    }

    private static double squaredDistance(double[] u, double[] v) {
      double result = 0;
      for (int i = 0; i < u.length; i++) {
        double d = u[i] - v[i];
        result += d * d;
      }
      return result;
    }

    /** Face vertex indices, 1-based as Mathematica reports them. */
    private static IAST faceIndices(List<int[]> faces) {
      IASTAppendable result = F.ListAlloc(faces.size());
      for (int[] face : faces) {
        IASTAppendable indices = F.ListAlloc(face.length);
        for (int index : face) {
          indices.append(F.ZZ(index + 1));
        }
        result.append(indices);
      }
      return result;
    }

    private static IAST coordinates(double[][] vertices) {
      IASTAppendable result = F.ListAlloc(vertices.length);
      for (double[] vertex : vertices) {
        IASTAppendable point = F.ListAlloc(vertex.length);
        for (double coordinate : vertex) {
          point.append(F.num(coordinate));
        }
        result.append(point);
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ComputationalGeometryFunctions() {}
}
