package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.RegionPrimitives;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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


  /** How close to the plane a corner has to be to count as lying on it. */
  private static final double PLANE_TOLERANCE = 1.0e-12;

  /**
   * The area of a <code>BooleanRegion</code> that is a flat cross section: a plane, together with
   * the solids that cut it down.
   *
   * <p>
   * A solid and a plane meet in a flat cross section - the shape a saw leaves - which has an area
   * although neither of the two regions has one. That is a region an intersection cannot draw as a
   * single shape, so it arrives here as <code>BooleanRegion(#1 && #2 &, {solid, plane})</code>: a
   * cube cut by <code>x + y == 1</code> is a rectangle, a ball cut by a plane is a disk, and a ball
   * and a cube together leave the part of that disk lying inside that rectangle. A plane that
   * misses the solid cuts nothing.
   *
   * <p>
   * Anything else - two solids of the same kind, no plane at all, a plane that is not flat in the
   * coordinates - is left alone.
   */
  private static IExpr booleanRegion(IAST region, EvalEngine engine) {
    List<IExpr> parts = new ArrayList<IExpr>();
    if (!conjunctionParts(region, parts)) {
      return F.NIL;
    }
    double[][] box = null;
    double[] plane = null;
    double[] ball = null;
    for (int i = 0; i < parts.size(); i++) {
      IExpr part = parts.get(i);
      double[][] nextBox = box(part);
      if (nextBox != null) {
        if (box != null) {
          return F.NIL;
        }
        box = nextBox;
        continue;
      }
      double[] nextPlane = plane(part, engine);
      if (nextPlane != null) {
        if (plane != null) {
          return F.NIL;
        }
        plane = nextPlane;
        continue;
      }
      double[] nextBall = ball(part);
      if (nextBall != null) {
        if (ball != null) {
          return F.NIL;
        }
        ball = nextBall;
        continue;
      }
      return F.NIL;
    }
    if (plane == null || (box == null && ball == null)) {
      // without a plane the parts meet in a solid, which has a volume and no area; without a solid
      // the plane itself is unbounded
      return F.NIL;
    }
    double area = crossSectionArea(box, ball, plane);
    // a plane which misses the solid leaves no region to measure; the call stays as it is
    return area > 0.0 ? F.num(area) : F.NIL;
  }

  /**
   * The parts of a conjunction of <code>BooleanRegion</code>s, a nested one taken apart -
   * intersecting a region with an intersection asks for all of them at once.
   *
   * @return whether the region is such a conjunction
   */
  private static boolean conjunctionParts(IExpr region, List<IExpr> parts) {
    if (!region.isAST(S.BooleanRegion, 3) || !region.second().isList()) {
      return false;
    }
    IAST list = (IAST) region.second();
    if (!isConjunction(region.first(), list.argSize())) {
      return false;
    }
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr part = list.get(i);
      if (part.isAST(S.BooleanRegion)) {
        if (!conjunctionParts(part, parts)) {
          return false;
        }
      } else {
        parts.add(part);
      }
    }
    return true;
  }

  /** Whether the function of a <code>BooleanRegion</code> asks for all of its parts at once. */
  private static boolean isConjunction(IExpr function, int partCount) {
    if (!function.isAST(S.Function, 2) || !function.first().isAST(S.And)) {
      return false;
    }
    IAST and = (IAST) function.first();
    if (and.argSize() != partCount) {
      return false;
    }
    for (int i = 1; i <= and.argSize(); i++) {
      if (!and.get(i).isAST(S.Slot, 2) || and.get(i).first().toIntDefault() != i) {
        return false;
      }
    }
    return true;
  }

  /**
   * An axis aligned box of the space as its lower and upper corner, or <code>null</code>.
   * <code>Cube(c, a)</code> is the box of edge <code>a</code> about <code>c</code>.
   */
  private static double[][] box(IExpr region) {
    if (region.isAST(S.Cuboid)) {
      IAST corners = RegionPrimitives.boxCorners((IAST) region);
      if (corners.isNIL() || corners.arg1().argSize() != 3) {
        return null;
      }
      return corners(((IAST) corners.arg1()), ((IAST) corners.arg2()));
    }
    if (!region.isAST(S.Cube)) {
      return null;
    }
    IAST cube = (IAST) region;
    IExpr centre = cube.argSize() >= 1 ? cube.arg1() : F.List(F.C0, F.C0, F.C0);
    if (!centre.isList() || centre.argSize() != 3) {
      return null;
    }
    double edge = cube.argSize() >= 2 ? cube.arg2().evalfNaN() : 1.0;
    if (!Double.isFinite(edge) || edge <= 0.0) {
      return null;
    }
    double[][] box = new double[2][3];
    for (int i = 0; i < 3; i++) {
      double middle = ((IAST) centre).get(i + 1).evalfNaN();
      if (!Double.isFinite(middle)) {
        return null;
      }
      box[0][i] = middle - edge / 2.0;
      box[1][i] = middle + edge / 2.0;
    }
    return box;
  }

  private static double[][] corners(IAST lower, IAST upper) {
    double[][] box = new double[2][3];
    for (int i = 0; i < 3; i++) {
      double low = lower.get(i + 1).evalfNaN();
      double high = upper.get(i + 1).evalfNaN();
      if (!Double.isFinite(low) || !Double.isFinite(high)) {
        return null;
      }
      box[0][i] = Math.min(low, high);
      box[1][i] = Math.max(low, high);
    }
    return box;
  }

  /**
   * A ball of the space as <code>{x, y, z, radius}</code>, or <code>null</code>. A
   * <code>Sphere</code> is none: its surface meets a plane in a circle, which is a curve and has no
   * area.
   */
  private static double[] ball(IExpr region) {
    if (!region.isAST(S.Ball)) {
      return null;
    }
    IAST region1 = (IAST) region;
    IExpr centre = region1.argSize() >= 1 ? region1.arg1() : F.List(F.C0, F.C0, F.C0);
    if (!centre.isList() || centre.argSize() != 3) {
      return null;
    }
    double[] ball = new double[4];
    for (int i = 0; i < 3; i++) {
      ball[i] = ((IAST) centre).get(i + 1).evalfNaN();
      if (!Double.isFinite(ball[i])) {
        return null;
      }
    }
    ball[3] = region1.argSize() >= 2 ? region1.arg2().evalfNaN() : 1.0;
    if (!Double.isFinite(ball[3]) || ball[3] < 0.0) {
      return null;
    }
    return ball;
  }

  /**
   * A plane <code>ImplicitRegion(lhs == rhs, {x, y, z})</code> as <code>{a, b, c, d}</code> with
   * <code>a x + b y + c z == d</code>, or <code>null</code> when the equation is not flat.
   */
  private static double[] plane(IExpr region, EvalEngine engine) {
    if (!region.isAST(S.ImplicitRegion, 3) || !region.first().isAST(S.Equal, 3)
        || !region.second().isList() || region.second().argSize() != 3) {
      return null;
    }
    IAST variables = (IAST) region.second();
    for (int i = 1; i <= 3; i++) {
      if (!variables.get(i).isVariable()) {
        return null;
      }
    }
    IAST equation = (IAST) region.first();
    IExpr form = engine.evaluate(F.Subtract(equation.arg1(), equation.arg2()));
    double[] plane = new double[4];
    IASTAppendable origin = F.ListAlloc(3);
    for (int i = 1; i <= 3; i++) {
      // the coefficient of each coordinate, which has to be a number for the equation to be a
      // plane rather than a curved surface
      IExpr slope = engine.evaluate(F.D(form, variables.get(i)));
      plane[i - 1] = slope.evalfNaN();
      if (!Double.isFinite(plane[i - 1]) || !slope.isNumber()) {
        return null;
      }
      origin.append(F.Rule(variables.get(i), F.C0));
    }
    if (plane[0] == 0.0 && plane[1] == 0.0 && plane[2] == 0.0) {
      return null;
    }
    double constant = engine.evaluate(F.subst(form, origin)).evalfNaN();
    if (!Double.isFinite(constant)) {
      return null;
    }
    plane[3] = -constant;
    return plane;
  }

  /**
   * The area of the cross section the plane is cut down to by the box and the ball, either of
   * which may be missing.
   *
   * <p>
   * Everything is measured in the plane's own two directions, where the box leaves a polygon and
   * the ball a disk, so that the two shapes can be cut against each other.
   */
  private static double crossSectionArea(double[][] box, double[] ball, double[] plane) {
    double length =
        Math.sqrt(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);
    double[] normal = {plane[0] / length, plane[1] / length, plane[2] / length};
    // the point of the plane closest to the origin, which serves as the origin of its coordinates
    double offset = plane[3] / length;
    double[] origin = {normal[0] * offset, normal[1] * offset, normal[2] * offset};
    double[][] basis = basis(normal);

    double[] disk = null;
    if (ball != null) {
      double distance =
          ball[0] * normal[0] + ball[1] * normal[1] + ball[2] * normal[2] - offset;
      double radius = ball[3] * ball[3] - distance * distance;
      if (radius <= 0.0) {
        // the plane misses the ball, or touches it in the single point where it is tangent
        return 0.0;
      }
      double[] middle = new double[3];
      for (int i = 0; i < 3; i++) {
        middle[i] = ball[i] - distance * normal[i];
      }
      double[] flat = project(middle, origin, basis);
      disk = new double[] {flat[0], flat[1], Math.sqrt(radius)};
    }
    if (box == null) {
      return Math.PI * disk[2] * disk[2];
    }
    List<double[]> corners = crossSectionCorners(box, plane, origin, basis);
    if (corners.size() < 3) {
      // the plane misses the box, touches it at a corner, or grazes one edge: nothing is cut
      return 0.0;
    }
    sortAround(corners);
    return disk == null ? polygonArea(corners) : diskPolygonArea(disk, corners);
  }

  /**
   * The corners of the polygon the plane cuts out of the box, in the plane's own coordinates.
   *
   * <p>
   * Every edge of the box that crosses the plane gives one corner.
   */
  private static List<double[]> crossSectionCorners(double[][] box, double[] plane,
      double[] origin, double[][] basis) {
    List<double[]> corners = new ArrayList<double[]>();
    for (int axis = 0; axis < 3; axis++) {
      for (int first = 0; first < 2; first++) {
        for (int second = 0; second < 2; second++) {
          double[] from = new double[3];
          double[] to = new double[3];
          for (int i = 0; i < 3; i++) {
            int which = i == axis ? 0 : i == (axis + 1) % 3 ? first : second;
            from[i] = box[which][i];
            to[i] = box[which][i];
          }
          from[axis] = box[0][axis];
          to[axis] = box[1][axis];
          double[] crossing = crossing(from, to, plane);
          if (crossing != null) {
            add(corners, project(crossing, origin, basis));
          }
        }
      }
    }
    return corners;
  }

  /** Where the segment meets the plane, or <code>null</code> when it does not cross it. */
  private static double[] crossing(double[] from, double[] to, double[] plane) {
    double atFrom = plane[0] * from[0] + plane[1] * from[1] + plane[2] * from[2] - plane[3];
    double atTo = plane[0] * to[0] + plane[1] * to[1] + plane[2] * to[2] - plane[3];
    if (Math.abs(atFrom) <= PLANE_TOLERANCE) {
      return from;
    }
    if (Math.abs(atTo) <= PLANE_TOLERANCE) {
      return to;
    }
    if (atFrom * atTo > 0.0) {
      return null;
    }
    double t = atFrom / (atFrom - atTo);
    double[] point = new double[3];
    for (int i = 0; i < 3; i++) {
      point[i] = from[i] + t * (to[i] - from[i]);
    }
    return point;
  }

  /** Add a corner, unless the same one is there already - box edges share their ends. */
  private static void add(List<double[]> corners, double[] point) {
    for (double[] corner : corners) {
      if (Math.abs(corner[0] - point[0]) <= 1.0e-9 && Math.abs(corner[1] - point[1]) <= 1.0e-9) {
        return;
      }
    }
    corners.add(point);
  }

  /**
   * Put the corners in order around their middle. An edge list alone does not say which corner
   * follows which, and a polygon whose corners are in the wrong order is a different shape.
   */
  private static void sortAround(List<double[]> corners) {
    double[] middle = new double[2];
    for (double[] corner : corners) {
      middle[0] += corner[0] / corners.size();
      middle[1] += corner[1] / corners.size();
    }
    corners.sort((a, b) -> Double.compare(Math.atan2(a[1] - middle[1], a[0] - middle[0]),
        Math.atan2(b[1] - middle[1], b[0] - middle[0])));
  }

  /** The area of a polygon given by its corners in order. */
  private static double polygonArea(List<double[]> corners) {
    double sum = 0.0;
    for (int i = 0; i < corners.size(); i++) {
      sum += cross2(corners.get(i), corners.get((i + 1) % corners.size()));
    }
    return Math.abs(sum) / 2.0;
  }

  /**
   * The area of the part of a disk that lies inside a polygon.
   *
   * <p>
   * The polygon is cut into triangles from the middle of the disk, and each triangle contributes
   * the part of itself that is inside the circle: a straight piece where the triangle's far side
   * runs inside the circle, and a circular wedge where it runs outside. The pieces are signed, so
   * that the ones a triangle reaching around the middle counts twice cancel again.
   */
  private static double diskPolygonArea(double[] disk, List<double[]> corners) {
    double area = 0.0;
    for (int i = 0; i < corners.size(); i++) {
      double[] from = relative(corners.get(i), disk);
      double[] to = relative(corners.get((i + 1) % corners.size()), disk);
      area += insideCircle(from, to, disk[2]);
    }
    return Math.abs(area);
  }

  private static double[] relative(double[] point, double[] disk) {
    return new double[] {point[0] - disk[0], point[1] - disk[1]};
  }

  /** The signed area of the part of the triangle <code>(0, from, to)</code> inside the circle. */
  private static double insideCircle(double[] from, double[] to, double radius) {
    double[] step = {to[0] - from[0], to[1] - from[1]};
    double along = step[0] * step[0] + step[1] * step[1];
    if (along <= 0.0) {
      return 0.0;
    }
    double half = from[0] * step[0] + from[1] * step[1];
    double outside = from[0] * from[0] + from[1] * from[1] - radius * radius;
    double discriminant = half * half - along * outside;
    if (discriminant <= 0.0) {
      return sector(from, to, radius);
    }
    double root = Math.sqrt(discriminant);
    double enter = (-half - root) / along;
    double leave = (-half + root) / along;
    if (enter >= 1.0 || leave <= 0.0) {
      // the side's own line meets the circle, but the side itself stays outside of it
      return sector(from, to, radius);
    }
    enter = Math.max(enter, 0.0);
    leave = Math.min(leave, 1.0);
    double[] in = {from[0] + enter * step[0], from[1] + enter * step[1]};
    double[] out = {from[0] + leave * step[0], from[1] + leave * step[1]};
    return sector(from, in, radius) + cross2(in, out) / 2.0 + sector(out, to, radius);
  }

  /** The signed area of the circular wedge between two directions. */
  private static double sector(double[] from, double[] to, double radius) {
    return 0.5 * radius * radius
        * Math.atan2(cross2(from, to), from[0] * to[0] + from[1] * to[1]);
  }

  private static double cross2(double[] a, double[] b) {
    return a[0] * b[1] - a[1] * b[0];
  }

  /** Two directions at right angles within the plane of the normal. */
  private static double[][] basis(double[] normal) {
    double[] first = Math.abs(normal[0]) < 0.9 ? new double[] {1, 0, 0} : new double[] {0, 1, 0};
    double[] u = cross(normal, first);
    normalize(u);
    double[] v = cross(normal, u);
    normalize(v);
    return new double[][] {u, v};
  }

  /** A point of the plane in the plane's own coordinates. */
  private static double[] project(double[] point, double[] origin, double[][] basis) {
    double[] flat = new double[2];
    for (int i = 0; i < 3; i++) {
      flat[0] += (point[i] - origin[i]) * basis[0][i];
      flat[1] += (point[i] - origin[i]) * basis[1][i];
    }
    return flat;
  }

  private static double[] cross(double[] a, double[] b) {
    return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]};
  }

  private static void normalize(double[] vector) {
    double length = Math.sqrt(
        vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
    if (length > 0.0) {
      for (int i = 0; i < 3; i++) {
        vector[i] /= length;
      }
    }
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
      case ID.BooleanRegion:
        return booleanRegion(geoForm, engine);
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

  /**
   * The area of a <code>Polygon</code> in any of its forms: a single boundary
   * <code>Polygon({p1, ..., pn})</code>, several disjoint components
   * <code>Polygon({poly1, poly2, ...})</code>, or components with holes cut out of them,
   * <code>Polygon(outer -> holes)</code>.
   */
  private static IExpr polygon(IAST geoForm, EvalEngine engine) {
    if (geoForm.argSize() != 1) {
      return F.NIL;
    }
    IExpr spec = geoForm.arg1();
    if (spec.isRuleAST()) {
      IExpr outer = boundaryArea(spec.first(), engine);
      IExpr holes = boundaryArea(spec.second(), engine);
      if (outer.isNIL() || holes.isNIL()) {
        return F.NIL;
      }
      return engine.evaluate(F.Subtract(outer, holes));
    }
    return boundaryArea(spec, engine);
  }

  /**
   * The area enclosed by one boundary, or the summed area of a list of boundaries.
   *
   * <p>
   * A point list and a list of point lists are told apart by looking one level down rather than by
   * counting: <code>{{1,1},{3,1},{3,3}}</code> is one triangle, while
   * <code>{{{1,1},{3,1},{3,3}}}</code> is a list holding one. Both spellings occur in the same
   * position - a single hole may be given bare or wrapped in a list.
   */
  private static IExpr boundaryArea(IExpr spec, EvalEngine engine) {
    if (!spec.isListOfLists()) {
      return F.NIL;
    }
    IAST list = (IAST) spec;
    if (!list.arg1().isListOfLists()) {
      return RegionPrimitives.polygonArea(list, engine);
    }
    IASTAppendable sum = F.PlusAlloc(list.argSize());
    for (int i = 1; i < list.size(); i++) {
      IExpr part = boundaryArea(list.get(i), engine);
      if (part.isNIL()) {
        return F.NIL;
      }
      sum.append(part);
    }
    return engine.evaluate(sum);
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
