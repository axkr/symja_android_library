package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.external.fastutil.ints.IntArrayList;
import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.builtin.RegionPrimitives;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>RegionMember(region, point)</code> - test whether a point lies in a region.
 *
 * <p>
 * Every region head contributes a <em>condition</em> rather than a decision. For a numeric point
 * the condition collapses to <code>True</code> or <code>False</code> when it is evaluated; for a
 * point with symbolic coordinates the condition itself is the result, which is what
 * <code>RegionMember(Disk(), {x,y})</code> is expected to give.
 */
public class RegionMember extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      // RegionMember(region) is the membership test as a function object
      return engine.evaluate(F.RegionMemberFunction(ast.arg1()));
    }
    if (ast.argSize() != 2) {
      return F.NIL;
    }
    IExpr arg1 = ast.arg1();
    IExpr point = ast.arg2();

    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    arg1 = MeshFunctions.normalizeRegion(arg1);
    if (MeshFunctions.isMeshRegion(arg1)) {
      int embeddingDimension = MeshFunctions.embeddingDimension((IAST) arg1);
      if (embeddingDimension == 3) {
        return MeshFunctions.member3D((IAST) arg1, point, engine);
      }
      if (embeddingDimension == 2) {
        return MeshFunctions.member2D((IAST) arg1, point, engine);
      }
    }

    if (arg1.isAST()) {
      IExpr condition = memberCondition((IAST) arg1, point, engine);
      if (condition.isPresent()) {
        return engine.evaluate(condition);
      }
    }
    return F.NIL;
  }

  /**
   * The membership condition of a region primitive as a - not yet evaluated - boolean expression.
   *
   * @return {@link F#NIL} if <code>reg</code> is not a supported region or if <code>point</code>
   *         does not have the coordinate dimension of the region
   */
  private static IExpr memberCondition(IAST reg, IExpr point, EvalEngine engine) {
    int headID = reg.headID();
    if (headID < 0) {
      return F.NIL;
    }
    switch (headID) {
      case ID.EmptyRegion:
        return reg.argSize() == 1 ? S.False : F.NIL;
      case ID.FullRegion:
        return reg.argSize() == 1 ? S.True : F.NIL;
      case ID.Point:
        return pointMember(reg, point, engine);
      case ID.Interval:
        return intervalMember(reg, point);
      case ID.Line:
        return lineMember(reg, point, engine);
      case ID.InfiniteLine:
      case ID.HalfLine:
        return unboundedLineMember(reg, point, engine);
      case ID.Disk:
      case ID.Ball:
        return ballMember(reg, point, engine, false);
      case ID.Circle:
      case ID.Sphere:
        return ballMember(reg, point, engine, true);
      case ID.Ellipsoid:
        return ellipsoidMember(reg, point, engine);
      case ID.Annulus:
        return annulusMember(reg, point, engine);
      case ID.Rectangle:
      case ID.Cuboid:
        return boxMember(reg, point);
      case ID.Triangle:
      case ID.Simplex:
        return simplexMember(reg, point, engine);
      case ID.Parallelogram:
      case ID.Parallelepiped:
        return parallelepipedMember(reg, point, engine);
      case ID.Polygon:
        return polygonMember(reg, point, engine);
      case ID.HalfSpace:
      case ID.HalfPlane:
        return halfSpaceMember(reg, point, engine);
      case ID.StadiumShape: {
        RegionPrimitives.StadiumSpec spec = RegionPrimitives.parseStadiumShape(reg);
        return spec == null ? F.NIL : tubeMember(spec.p1, spec.p2, spec.radius, point, engine);
      }
      case ID.CapsuleShape: {
        RegionPrimitives.CapsuleSpec spec = RegionPrimitives.parseCapsuleShape(reg);
        return spec == null ? F.NIL : tubeMember(spec.p1, spec.p2, spec.radius, point, engine);
      }
      case ID.SphericalShell:
        return sphericalShellMember(reg, point, engine);
      case ID.Cylinder:
        return cylinderMember(reg, point, engine, false);
      case ID.Cone:
        return cylinderMember(reg, point, engine, true);
      case ID.ImplicitRegion:
        return implicitRegionMember(reg, point, engine);
      case ID.RegionUnion:
      case ID.RegionIntersection:
      case ID.RegionDifference:
      case ID.RegionSymmetricDifference:
        return combinedRegionMember(reg, point, engine);
    }
    return F.NIL;
  }

  /**
   * <code>ImplicitRegion(cond, {x, y, ...})</code> is defined by the condition itself, so the
   * membership test is the condition with the coordinates of the point substituted for the
   * variables. A variable may be given as <code>{x, xmin, xmax}</code>, which adds its bounds to
   * the condition.
   *
   * @return {@link F#NIL} if the arguments are not a condition and a list of variables which
   *         matches the length of <code>point</code>
   */
  private static IExpr implicitRegionMember(IAST reg, IExpr point, EvalEngine engine) {
    if (reg.argSize() != 2 || !reg.arg2().isList()) {
      return F.NIL;
    }
    IAST variables = (IAST) reg.arg2();
    IAST p = coordinates(point, variables.argSize());
    if (p.isNIL()) {
      return F.NIL;
    }
    IASTAppendable rules = F.ListAlloc(variables.argSize());
    IASTAppendable bounds = F.ast(S.And, variables.argSize() + 1);
    for (int i = 1; i <= variables.argSize(); i++) {
      IExpr variable = variables.get(i);
      if (variable.isList3()) {
        // {x, xmin, xmax} restricts the variable to a range
        IAST bounded = (IAST) variable;
        variable = bounded.arg1();
        bounds.append(F.LessEqual(bounded.arg2(), p.get(i)));
        bounds.append(F.LessEqual(p.get(i), bounded.arg3()));
      }
      if (!variable.isSymbol()) {
        return F.NIL;
      }
      rules.append(F.Rule(variable, p.get(i)));
    }
    IExpr condition = F.subst(reg.arg1(), rules);
    return bounds.argSize() == 0 ? condition : F.And(condition, bounds);
  }

  /**
   * The membership condition of a Boolean combination of regions is the same combination of the
   * membership conditions of its parts.
   *
   * @return {@link F#NIL} if one of the parts has no membership condition
   */
  private static IExpr combinedRegionMember(IAST reg, IExpr point, EvalEngine engine) {
    if (reg.argSize() < 1) {
      return F.NIL;
    }
    IExpr[] parts = new IExpr[reg.argSize()];
    for (int i = 1; i <= reg.argSize(); i++) {
      IExpr part = reg.get(i);
      if (!part.isAST()) {
        return F.NIL;
      }
      IExpr condition = memberCondition((IAST) part, point, engine);
      if (condition.isNIL()) {
        return F.NIL;
      }
      parts[i - 1] = condition;
    }
    switch (reg.headID()) {
      case ID.RegionUnion:
        return F.ast(parts, S.Or);
      case ID.RegionIntersection:
        return F.ast(parts, S.And);
      case ID.RegionSymmetricDifference:
        // the points which lie in an odd number of the regions
        return F.ast(parts, S.Xor);
      case ID.RegionDifference: {
        // the points of the first region which lie in none of the others
        IASTAppendable and = F.ast(S.And, parts.length);
        and.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
          and.append(F.Not(parts[i]));
        }
        return and;
      }
    }
    return F.NIL;
  }

  /** The coordinates of <code>point</code>, if it is a vector of the expected length. */
  private static IAST coordinates(IExpr point, int dimension) {
    if (point.isList() && point.argSize() == dimension && !((IAST) point).arg1().isList()) {
      return (IAST) point;
    }
    return F.NIL;
  }

  /** <code>And(x1 == p1, ..., xn == pn)</code> - the point is one of the given points. */
  private static IExpr pointMember(IAST reg, IExpr point, EvalEngine engine) {
    if (reg.argSize() != 1 || !reg.arg1().isList()) {
      return F.NIL;
    }
    IAST p = (IAST) reg.arg1();
    if (p.argSize() > 0 && p.arg1().isList()) {
      // Point({p1, p2, ...}) is a set of points
      IASTAppendable or = F.ast(S.Or, p.argSize());
      for (int i = 1; i <= p.argSize(); i++) {
        IExpr equal = equalCoordinates(p.get(i), point);
        if (equal.isNIL()) {
          return F.NIL;
        }
        or.append(equal);
      }
      return or;
    }
    return equalCoordinates(p, point);
  }

  /** <code>And(a1 == b1, ..., an == bn)</code> for two coordinate vectors of equal length. */
  private static IExpr equalCoordinates(IExpr a, IExpr b) {
    if (!a.isList() || !b.isList() || a.argSize() != b.argSize() || a.argSize() == 0) {
      return F.NIL;
    }
    IAST left = (IAST) a;
    IAST right = (IAST) b;
    IASTAppendable and = F.ast(S.And, left.argSize());
    for (int i = 1; i <= left.argSize(); i++) {
      and.append(F.Equal(right.get(i), left.get(i)));
    }
    return and;
  }

  /** <code>min &lt;= x &lt;= max</code> for the one dimensional <code>Interval({min,max})</code>. */
  private static IExpr intervalMember(IAST reg, IExpr point) {
    if (reg.argSize() != 1 || !reg.arg1().isList2()) {
      return F.NIL;
    }
    IAST bounds = (IAST) reg.arg1();
    IExpr x = point;
    if (point.isList()) {
      IAST p = coordinates(point, 1);
      if (p.isNIL()) {
        return F.NIL;
      }
      x = p.arg1();
    }
    return F.And(F.LessEqual(bounds.arg1(), x), F.LessEqual(x, bounds.arg2()));
  }

  /**
   * The point lies on one of the segments of the polyline: it is collinear with the segment and its
   * projection parameter stays between the two end points.
   */
  private static IExpr lineMember(IAST reg, IExpr point, EvalEngine engine) {
    if (reg.argSize() != 1 || !reg.arg1().isListOfLists()) {
      return F.NIL;
    }
    IAST pts = (IAST) reg.arg1();
    if (pts.argSize() < 2 || coordinates(point, pts.arg1().argSize()).isNIL()) {
      return F.NIL;
    }
    IASTAppendable or = F.ast(S.Or, pts.argSize() - 1);
    for (int i = 1; i < pts.argSize(); i++) {
      IExpr segment = segmentMember(pts.get(i), pts.get(i + 1), point, engine);
      if (segment.isNIL()) {
        return F.NIL;
      }
      or.append(segment);
    }
    return or.argSize() == 1 ? or.arg1() : or;
  }

  /** The point lies on the straight segment from <code>p1</code> to <code>p2</code>. */
  private static IExpr segmentMember(IExpr p1, IExpr p2, IExpr point, EvalEngine engine) {
    IExpr direction = engine.evaluate(F.Subtract(p2, p1));
    IExpr offset = engine.evaluate(F.Subtract(point, p1));
    if (!direction.isList() || !offset.isList()) {
      return F.NIL;
    }
    IExpr collinear = collinearCondition((IAST) offset, (IAST) direction, engine);
    if (collinear.isNIL()) {
      return F.NIL;
    }
    IExpr lengthSquared = engine.evaluate(F.Total(F.Sqr(direction)));
    if (lengthSquared.isZero()) {
      return F.NIL;
    }
    IExpr projection = engine.evaluate(F.Expand(F.Dot(offset, direction)));
    return F.And(collinear, F.LessEqual(F.C0, projection),
        F.LessEqual(projection, lengthSquared));
  }

  /**
   * A point of an <code>InfiniteLine({p1,p2})</code> or <code>HalfLine({p1,p2})</code> is
   * <code>p1 + t*(p2-p1)</code>. It lies on the line when the vector to it is parallel to the
   * direction, and on the half line when in addition <code>t &gt;= 0</code>.
   */
  private static IExpr unboundedLineMember(IAST reg, IExpr point, EvalEngine engine) {
    if (reg.argSize() < 1 || !reg.arg1().isListOfLists() || reg.arg1().argSize() != 2) {
      return F.NIL;
    }
    IAST pts = (IAST) reg.arg1();
    IExpr p1 = pts.arg1();
    IExpr p2 = pts.arg2();
    int dimension = p1.argSize();
    if (coordinates(point, dimension).isNIL() || p2.argSize() != dimension) {
      return F.NIL;
    }
    IExpr direction = engine.evaluate(F.Subtract(p2, p1));
    IExpr offset = engine.evaluate(F.Subtract(point, p1));
    if (!direction.isList() || !offset.isList() || offset.argSize() != dimension) {
      return F.NIL;
    }
    IExpr collinear = collinearCondition((IAST) offset, (IAST) direction, engine);
    if (collinear.isNIL()) {
      return F.NIL;
    }
    if (reg.isAST(S.InfiniteLine)) {
      return collinear;
    }
    // a half line starts at p1 and runs towards p2, so the projection must not be negative
    return F.And(collinear, F.GreaterEqual(F.Dot(offset, direction), F.C0));
  }

  /**
   * Two vectors are parallel when every <code>2x2</code> minor of the matrix they span vanishes.
   * Stating it that way keeps the condition polynomial instead of dividing by the length of the
   * direction.
   *
   * @return {@link F#NIL} if the two vectors do not have the same length
   */
  private static IExpr collinearCondition(IAST offset, IAST direction, EvalEngine engine) {
    int dimension = offset.argSize();
    if (direction.argSize() != dimension || dimension < 2) {
      return F.NIL;
    }
    IASTAppendable and = F.ast(S.And, dimension);
    for (int i = 1; i < dimension; i++) {
      for (int j = i + 1; j <= dimension; j++) {
        // stated as an equation between the two products rather than as "minor == 0", so that
        // the condition of a line through the origin reads x == y instead of x - y == 0
        IExpr left = engine.evaluate(F.Expand(F.Times(offset.get(i), direction.get(j))));
        IExpr right = engine.evaluate(F.Expand(F.Times(offset.get(j), direction.get(i))));
        and.append(F.Equal(left, right));
      }
    }
    return and;
  }

  /**
   * <code>Sum((xi-ci)^2) &lt;= r^2</code> for a ball and <code>== r^2</code> for the sphere which
   * bounds it. With one radius per axis the region is an ellipse and the condition becomes
   * <code>Sum((xi-ci)^2/ri^2) &lt;= 1</code>.
   *
   * <p>
   * Comparing the squared distances avoids the square root of <code>Norm</code>, which would wrap
   * every symbolic coordinate in <code>Abs</code>.
   */
  private static IExpr ballMember(IAST reg, IExpr point, EvalEngine engine, boolean boundaryOnly) {
    int dimension = reg.isAST(S.Disk) || reg.isAST(S.Circle) ? 2 : 3;
    IExpr center = RegionPrimitives.constantVector(F.C0, dimension);
    IExpr radius = F.C1;
    if (reg.argSize() >= 1) {
      if (!reg.arg1().isList() || ((IAST) reg.arg1()).arg1().isList()) {
        return F.NIL;
      }
      center = reg.arg1();
      dimension = center.argSize();
      if (reg.argSize() >= 2) {
        radius = reg.arg2();
      }
    }
    IExpr sector = F.NIL;
    if (reg.argSize() == 3) {
      if (dimension != 2) {
        // only a sector in the plane is supported
        return F.NIL;
      }
      // the center of a disk sector belongs to it, the center of an arc does not
      sector = sectorCondition(center, reg.arg3(), point, engine, !boundaryOnly);
      if (sector.isNIL()) {
        return F.NIL;
      }
    } else if (reg.argSize() > 3) {
      return F.NIL;
    }
    IAST p = coordinates(point, dimension);
    if (p.isNIL()) {
      return F.NIL;
    }
    IAST c = (IAST) center;
    IASTAppendable sum = F.PlusAlloc(dimension);
    if (radius.isList()) {
      if (radius.argSize() != dimension) {
        return F.NIL;
      }
      IAST radii = (IAST) radius;
      for (int i = 1; i <= dimension; i++) {
        sum.append(F.Divide(F.Sqr(F.Subtract(p.get(i), c.get(i))), F.Sqr(radii.get(i))));
      }
      return withSector(boundaryOnly ? F.Equal(sum, F.C1) : F.LessEqual(sum, F.C1), sector);
    }
    for (int i = 1; i <= dimension; i++) {
      sum.append(F.Sqr(F.Subtract(p.get(i), c.get(i))));
    }
    IExpr radiusSquared = F.Sqr(radius);
    return withSector(
        boundaryOnly ? F.Equal(sum, radiusSquared) : F.LessEqual(sum, radiusSquared), sector);
  }

  /** Combine the radial condition with the angular condition of a sector, if there is one. */
  private static IExpr withSector(IExpr radial, IExpr sector) {
    return sector.isPresent() ? F.And(radial, sector) : radial;
  }

  /**
   * The angular condition of a sector which runs counter-clockwise from <code>t1</code> to
   * <code>t2</code>. The polar angle of the point is measured relative to <code>t1</code> and
   * wrapped into one turn, so that a range which crosses the negative x axis is handled and a
   * range of a full turn or more covers everything.
   *
   * @param includeCenter the center of the region satisfies the condition even though it has no
   *        well defined polar angle
   * @return {@link F#NIL} if <code>angleRange</code> is not a pair of angles
   */
  private static IExpr sectorCondition(IExpr center, IExpr angleRange, IExpr point,
      EvalEngine engine, boolean includeCenter) {
    if (!angleRange.isList2() || !center.isList2() || coordinates(point, 2).isNIL()) {
      return F.NIL;
    }
    IExpr t1 = ((IAST) angleRange).arg1();
    IExpr t2 = ((IAST) angleRange).arg2();
    IExpr extent = engine.evaluate(F.Subtract(t2, t1));
    if (engine.evaluate(F.GreaterEqual(extent, F.C2Pi)).isTrue()) {
      // a full turn or more covers every angle
      return S.True;
    }
    if (engine.evaluate(F.Less(extent, F.C0)).isTrue()) {
      return S.False;
    }
    IAST c = (IAST) center;
    IAST p = (IAST) point;
    IExpr dx = engine.evaluate(F.Subtract(p.arg1(), c.arg1()));
    IExpr dy = engine.evaluate(F.Subtract(p.arg2(), c.arg2()));
    IExpr angle = F.Mod(F.Subtract(F.ArcTan(dx, dy), t1), F.C2Pi);
    IExpr inSector = F.LessEqual(angle, extent);
    if (!includeCenter) {
      return inSector;
    }
    // ArcTan(0,0) has no value, so the center is admitted explicitly
    return F.Or(F.And(F.Equal(dx, F.C0), F.Equal(dy, F.C0)), inSector);
  }

  /** <code>Sum((xi-ci)^2/ri^2) &lt;= 1</code> for an axis aligned ellipsoid. */
  private static IExpr ellipsoidMember(IAST reg, IExpr point, EvalEngine engine) {
    if (reg.argSize() != 2 || !reg.arg1().isList() || !reg.arg2().isList()
        || reg.arg1().argSize() != reg.arg2().argSize()) {
      return F.NIL;
    }
    IAST center = (IAST) reg.arg1();
    IAST radii = (IAST) reg.arg2();
    IAST p = coordinates(point, center.argSize());
    if (p.isNIL()) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(center.argSize());
    for (int i = 1; i <= center.argSize(); i++) {
      sum.append(F.Divide(F.Sqr(F.Subtract(p.get(i), center.get(i))), F.Sqr(radii.get(i))));
    }
    return F.LessEqual(sum, F.C1);
  }

  /** <code>rInner^2 &lt;= |p-c|^2 &lt;= rOuter^2</code>. */
  private static IExpr annulusMember(IAST reg, IExpr point, EvalEngine engine) {
    RegionPrimitives.AnnulusSpec spec = RegionPrimitives.parseAnnulus(reg, engine);
    if (spec == null) {
      return F.NIL;
    }
    IAST p = coordinates(point, spec.center.argSize());
    if (p.isNIL()) {
      return F.NIL;
    }
    IExpr distanceSquared = engine.evaluate(F.Total(F.Sqr(F.Subtract(point, spec.center))));
    IExpr radial = F.And(F.LessEqual(F.Sqr(spec.innerRadius), distanceSquared),
        F.LessEqual(distanceSquared, F.Sqr(spec.outerRadius)));
    if (spec.isFull()) {
      return radial;
    }
    // an annulus sector never reaches its center, so the center is not admitted separately
    IExpr sector = sectorCondition(spec.center, reg.arg3(), point, engine, false);
    return sector.isNIL() ? F.NIL : F.And(radial, sector);
  }

  /** <code>And(lower_i &lt;= x_i &lt;= upper_i)</code> for an axis aligned box. */
  private static IExpr boxMember(IAST reg, IExpr point) {
    IAST corners = RegionPrimitives.boxCorners(reg);
    if (corners.isNIL()) {
      return F.NIL;
    }
    IAST lower = (IAST) corners.arg1();
    IAST upper = (IAST) corners.arg2();
    IAST p = coordinates(point, lower.argSize());
    if (p.isNIL()) {
      return F.NIL;
    }
    IASTAppendable and = F.ast(S.And, lower.argSize());
    for (int i = 1; i <= lower.argSize(); i++) {
      and.append(F.And(F.LessEqual(lower.get(i), p.get(i)), F.LessEqual(p.get(i), upper.get(i))));
    }
    return and;
  }

  /**
   * The barycentric coordinates of the point with respect to the corners of a simplex must all be
   * non-negative and must not sum to more than one.
   */
  private static IExpr simplexMember(IAST reg, IExpr point, EvalEngine engine) {
    IAST vertices = reg.isAST(S.Triangle) ? trianglePoints(reg) //
        : RegionPrimitives.verticesOfSimplex(reg);
    if (vertices.isNIL() || vertices.argSize() < 2 || !vertices.arg1().isList()) {
      return F.NIL;
    }
    int dimension = vertices.arg1().argSize();
    if (coordinates(point, dimension).isNIL()) {
      return F.NIL;
    }
    if (vertices.argSize() != dimension + 1) {
      // a simplex which does not fill its embedding space - the linear system is not square
      return F.NIL;
    }
    IExpr lambda = barycentricCoordinates(vertices, point, engine);
    if (lambda.isNIL()) {
      return F.NIL;
    }
    IAST coefficients = (IAST) lambda;
    IASTAppendable and = F.ast(S.And, coefficients.argSize() + 1);
    for (int i = 1; i <= coefficients.argSize(); i++) {
      and.append(F.GreaterEqual(coefficients.get(i), F.C0));
    }
    and.append(F.LessEqual(F.Total(coefficients), F.C1));
    return and;
  }

  /** Every coefficient of the point in the basis of the spanning vectors lies in [0,1]. */
  private static IExpr parallelepipedMember(IAST reg, IExpr point, EvalEngine engine) {
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
    int dimension = base.argSize();
    if (coordinates(point, dimension).isNIL() || vectors.argSize() != dimension) {
      return F.NIL;
    }
    IExpr solution = engine.evaluate(
        F.LinearSolve(F.Transpose(vectors), F.Subtract(point, base)));
    if (!solution.isList() || solution.argSize() != dimension) {
      return F.NIL;
    }
    IAST coefficients = (IAST) solution;
    IASTAppendable and = F.ast(S.And, dimension);
    for (int i = 1; i <= dimension; i++) {
      and.append(
          F.And(F.LessEqual(F.C0, coefficients.get(i)), F.LessEqual(coefficients.get(i), F.C1)));
    }
    return and;
  }

  /**
   * Solve <code>point = v0 + Sum(lambda_i * (v_i - v0))</code> for the barycentric coefficients
   * <code>lambda_i</code> of a full dimensional simplex.
   *
   * @return {@link F#NIL} if the corner points are degenerate
   */
  private static IExpr barycentricCoordinates(IAST vertices, IExpr point, EvalEngine engine) {
    IExpr v0 = vertices.arg1();
    IASTAppendable edges = F.ListAlloc(vertices.argSize() - 1);
    for (int i = 2; i <= vertices.argSize(); i++) {
      edges.append(F.Subtract(vertices.get(i), v0));
    }
    IExpr solution =
        engine.evaluate(F.LinearSolve(F.Transpose(edges), F.Subtract(point, v0)));
    return solution.isList() && solution.argSize() == vertices.argSize() - 1 ? solution : F.NIL;
  }

  /** The three corner points of a <code>Triangle</code>. */
  private static IAST trianglePoints(IAST reg) {
    if (reg.argSize() == 0) {
      return F.list(F.CListC0C0, F.List(F.C1, F.C0), F.List(F.C0, F.C1));
    }
    if (reg.argSize() == 1 && reg.arg1().isList3() && ((IAST) reg.arg1()).arg1().isList()) {
      return (IAST) reg.arg1();
    }
    return F.NIL;
  }

  /** A point of the half-space <code>n.x &lt;= c</code>. */
  private static IExpr halfSpaceMember(IAST reg, IExpr point, EvalEngine engine) {
    RegionPrimitives.HalfSpaceSpec spec = RegionPrimitives.parseHalfSpace(reg, engine);
    if (spec == null || coordinates(point, spec.normal.argSize()).isNIL()) {
      return F.NIL;
    }
    return F.LessEqual(F.Dot(spec.normal, point), spec.offset);
  }

  /** All points which are no further than <code>radius</code> away from the axis segment. */
  private static IExpr tubeMember(IExpr p1, IExpr p2, IExpr radius, IExpr point,
      EvalEngine engine) {
    if (!p1.isList() || coordinates(point, p1.argSize()).isNIL()) {
      return F.NIL;
    }
    IExpr distance =
        SignedRegionDistance.distanceToSegment(point, (IAST) p1, (IAST) p2, engine);
    return distance.isNIL() ? F.NIL : F.LessEqual(distance, radius);
  }

  /** <code>rInner &lt;= |p-c| &lt;= rOuter</code> for the shell between two concentric spheres. */
  private static IExpr sphericalShellMember(IAST reg, IExpr point, EvalEngine engine) {
    RegionPrimitives.ShellSpec spec = RegionPrimitives.parseSphericalShell(reg);
    if (spec == null || !spec.center.isList()
        || coordinates(point, spec.center.argSize()).isNIL()) {
      return F.NIL;
    }
    IExpr distanceSquared = F.Total(F.Sqr(F.Subtract(point, spec.center)));
    return F.And(F.LessEqual(F.Sqr(spec.rInner), distanceSquared),
        F.LessEqual(distanceSquared, F.Sqr(spec.rOuter)));
  }

  /**
   * A point of a cylinder projects onto the axis between the two end caps and is no further than
   * the radius away from it. For a cone the admissible radius shrinks linearly to zero at the apex.
   */
  private static IExpr cylinderMember(IAST reg, IExpr point, EvalEngine engine, boolean cone) {
    RegionPrimitives.AxisSpec spec = RegionPrimitives.parseAxisRegion(reg);
    if (spec == null || !spec.base.isList()
        || coordinates(point, spec.base.argSize()).isNIL()) {
      return F.NIL;
    }
    IExpr axis = engine.evaluate(F.Subtract(spec.tip, spec.base));
    IExpr lengthSquared = engine.evaluate(F.Total(F.Sqr(axis)));
    if (lengthSquared.isZero()) {
      return F.NIL;
    }
    IExpr offset = engine.evaluate(F.Subtract(point, spec.base));
    if (!offset.isList()) {
      return F.NIL;
    }
    // the relative position along the axis, running from 0 at the base to 1 at the tip
    IExpr t = engine.evaluate(F.Divide(F.Dot(offset, axis), lengthSquared));
    // the squared distance from the axis is |offset|^2 - t^2*|axis|^2
    IExpr radialSquared = engine
        .evaluate(F.Expand(F.Subtract(F.Total(F.Sqr(offset)), F.Times(F.Sqr(t), lengthSquared))));
    IExpr allowedRadius =
        cone ? engine.evaluate(F.Expand(F.Times(spec.radius, F.Subtract(F.C1, t)))) : spec.radius;
    return F.And(F.LessEqual(F.C0, t), F.LessEqual(t, F.C1),
        F.LessEqual(radialSquared, F.Sqr(allowedRadius)));
  }

  private static IExpr polygonMember(IAST reg, IExpr point, EvalEngine engine) {
    IExpr winding = windingNumberMember(reg, point, engine);
    if (winding.isPresent()) {
      return winding;
    }
    // the winding number could not be decided - fall back to a condition on the outline
    return polygonCondition(reg, point, engine);
  }

  /**
   * The membership condition of a polygon in the plane. A convex outline is the intersection of
   * the half planes of its edges; a non convex outline is cut into ear triangles and the point has
   * to lie in one of them.
   *
   * @return {@link F#NIL} if the outline is not a plane polygon of at least three corners
   */
  private static IExpr polygonCondition(IAST reg, IExpr point, EvalEngine engine) {
    if (reg.argSize() != 1 || !reg.arg1().isListOfLists() || coordinates(point, 2).isNIL()) {
      return F.NIL;
    }
    IAST pts = (IAST) reg.arg1();
    if (pts.argSize() < 3 || pts.arg1().argSize() != 2) {
      return F.NIL;
    }
    for (int i = 1; i <= pts.argSize(); i++) {
      if (!pts.get(i).isList2()) {
        return F.NIL;
      }
    }
    int orientation = convexOrientation(pts, engine);
    if (orientation != 0) {
      IASTAppendable and = F.ast(S.And, pts.argSize());
      for (int i = 1; i <= pts.argSize(); i++) {
        IExpr side = sideCondition(pts.get(i), pts.get(i % pts.argSize() + 1), point, orientation,
            engine);
        // collinear edges bound the same half plane - list it once
        if (!and.contains(side)) {
          and.append(side);
        }
      }
      return and.argSize() == 1 ? and.arg1() : and;
    }
    int[][] triangles = earClip(pts, engine);
    if (triangles == null) {
      return F.NIL;
    }
    IASTAppendable or = F.ast(S.Or, triangles.length);
    for (int[] triangle : triangles) {
      IExpr condition = triangleCondition(pts.get(triangle[0]), pts.get(triangle[1]),
          pts.get(triangle[2]), point, engine);
      if (condition.isNIL()) {
        return F.NIL;
      }
      or.append(condition);
    }
    return or.argSize() == 1 ? or.arg1() : or;
  }

  /**
   * The turning direction of a convex outline.
   *
   * @return <code>1</code> for a counter-clockwise and <code>-1</code> for a clockwise convex
   *         outline, <code>0</code> if the outline is not convex or if a turn cannot be decided
   */
  private static int convexOrientation(IAST pts, EvalEngine engine) {
    int n = pts.argSize();
    int orientation = 0;
    for (int i = 1; i <= n; i++) {
      IExpr turn = crossProduct2D(pts.get(i), pts.get(i % n + 1), pts.get((i + 1) % n + 1), engine);
      int sign = signOf(turn, engine);
      if (sign == 0) {
        // a straight or undecidable turn does not fix the orientation
        if (!turn.isZero()) {
          return 0;
        }
        continue;
      }
      if (orientation == 0) {
        orientation = sign;
      } else if (orientation != sign) {
        return 0;
      }
    }
    return orientation;
  }

  /** <code>(b-a) x (c-a)</code> - twice the signed area of the triangle <code>a b c</code>. */
  private static IExpr crossProduct2D(IExpr a, IExpr b, IExpr c, EvalEngine engine) {
    IAST pa = (IAST) a;
    IAST pb = (IAST) b;
    IAST pc = (IAST) c;
    return engine.evaluate(F.Expand(F.Subtract(//
        F.Times(F.Subtract(pb.arg1(), pa.arg1()), F.Subtract(pc.arg2(), pa.arg2())), //
        F.Times(F.Subtract(pb.arg2(), pa.arg2()), F.Subtract(pc.arg1(), pa.arg1())))));
  }

  /**
   * @return <code>1</code> if <code>expr</code> is positive, <code>-1</code> if it is negative and
   *         <code>0</code> if the sign cannot be decided
   */
  private static int signOf(IExpr expr, EvalEngine engine) {
    if (engine.evaluate(F.Greater(expr, F.C0)).isTrue()) {
      return 1;
    }
    if (engine.evaluate(F.Less(expr, F.C0)).isTrue()) {
      return -1;
    }
    return 0;
  }

  /** The point is on the inner side of the directed edge from <code>a</code> to <code>b</code>. */
  private static IExpr sideCondition(IExpr a, IExpr b, IExpr point, int orientation,
      EvalEngine engine) {
    IAST pa = (IAST) a;
    IAST pb = (IAST) b;
    IAST p = (IAST) point;
    IExpr cross = engine.evaluate(F.Expand(F.Subtract(//
        F.Times(F.Subtract(pb.arg1(), pa.arg1()), F.Subtract(p.arg2(), pa.arg2())), //
        F.Times(F.Subtract(pb.arg2(), pa.arg2()), F.Subtract(p.arg1(), pa.arg1())))));
    return orientation > 0 ? F.GreaterEqual(cross, F.C0) : F.LessEqual(cross, F.C0);
  }

  /** The point lies in the triangle <code>a b c</code>, boundary included. */
  private static IExpr triangleCondition(IExpr a, IExpr b, IExpr c, IExpr point,
      EvalEngine engine) {
    int orientation = signOf(crossProduct2D(a, b, c, engine), engine);
    if (orientation == 0) {
      // a degenerate ear encloses no area
      return F.NIL;
    }
    return F.And(sideCondition(a, b, point, orientation, engine),
        sideCondition(b, c, point, orientation, engine),
        sideCondition(c, a, point, orientation, engine));
  }

  /**
   * Cut a simple polygon into triangles by repeatedly clipping an ear - a corner whose diagonal
   * stays inside the outline and which contains no other corner.
   *
   * @return the corner indices of the triangles, or <code>null</code> if the outline could not be
   *         triangulated because a turn or a containment could not be decided
   */
  private static int[][] earClip(IAST pts, EvalEngine engine) {
    int n = pts.argSize();
    if (n > 256) {
      // the quadratic ear search would build an unusably large condition
      return null;
    }
    IntArrayList remaining = new IntArrayList(n);
    for (int i = 1; i <= n; i++) {
      remaining.add(i);
    }
    // the sign of the total signed area is the turning direction of the outline
    IASTAppendable areaSum = F.PlusAlloc(n);
    for (int i = 1; i <= n; i++) {
      IAST p1 = (IAST) pts.get(i);
      IAST p2 = (IAST) pts.get(i % n + 1);
      areaSum.append(F.Subtract(F.Times(p1.arg1(), p2.arg2()), F.Times(p2.arg1(), p1.arg2())));
    }
    int outline = signOf(engine.evaluate(areaSum), engine);
    if (outline == 0) {
      return null;
    }
    List<int[]> triangles = new ArrayList<int[]>(n - 2);
    int guard = 0;
    while (remaining.size() > 3 && guard++ < n * n) {
      boolean clipped = false;
      for (int k = 0; k < remaining.size(); k++) {
        int size = remaining.size();
        int prev = remaining.getInt((k + size - 1) % size);
        int curr = remaining.getInt(k);
        int next = remaining.getInt((k + 1) % size);
        if (signOf(crossProduct2D(pts.get(prev), pts.get(curr), pts.get(next), engine),
            engine) != outline) {
          // a reflex or undecidable corner is not an ear
          continue;
        }
        if (containsOtherCorner(pts, remaining, prev, curr, next, outline, engine)) {
          continue;
        }
        triangles.add(new int[] {prev, curr, next});
        remaining.removeInt(k);
        clipped = true;
        break;
      }
      if (!clipped) {
        return null;
      }
    }
    if (remaining.size() != 3) {
      return null;
    }
    triangles
        .add(new int[] {remaining.getInt(0), remaining.getInt(1), remaining.getInt(2)});
    return triangles.toArray(new int[triangles.size()][]);
  }

  /** One of the remaining corners lies inside the candidate ear, so it must not be clipped. */
  private static boolean containsOtherCorner(IAST pts, IntArrayList remaining, int prev, int curr,
      int next, int outline, EvalEngine engine) {
    for (int i = 0; i < remaining.size(); i++) {
      int index = remaining.getInt(i);
      if (index == prev || index == curr || index == next) {
        continue;
      }
      IExpr corner = pts.get(index);
      if (signOf(crossProduct2D(pts.get(prev), pts.get(curr), corner, engine),
          engine) == outline
          && signOf(crossProduct2D(pts.get(curr), pts.get(next), corner, engine),
              engine) == outline
          && signOf(crossProduct2D(pts.get(next), pts.get(prev), corner, engine),
              engine) == outline) {
        return true;
      }
    }
    return false;
  }

  /**
   * The winding number test for a polygon in the plane. It needs a decidable comparison for every
   * edge and therefore only answers for a point with numeric coordinates.
   */
  private static IExpr windingNumberMember(IAST reg, IExpr point, EvalEngine engine) {
    if (reg.argSize() == 1 && reg.arg1().isList() && point.isList2()) {
      IAST pts = (IAST) reg.arg1();
      if (pts.argSize() >= 3) {
        IExpr px = ((IAST) point).arg1();
        IExpr py = ((IAST) point).arg2();

        int wn = 0;
        for (int i = 1; i <= pts.argSize(); i++) {
          IAST v1 = (IAST) pts.get(i);
          IAST v2 = (IAST) pts.get(i % pts.argSize() + 1);

          IExpr dist = SignedRegionDistance.distanceToSegment(point, v1, v2, engine);
          if (engine.evaluate(F.Equal(dist, F.C0)).isTrue()) {
            return S.True;
          }

          IExpr v1y = v1.arg2();
          IExpr v2y = v2.arg2();
          IExpr v1x = v1.arg1();
          IExpr v2x = v2.arg1();

          IExpr isLeft =
              engine.evaluate(F.Subtract(F.Times(F.Subtract(v2x, v1x), F.Subtract(py, v1y)),
                  F.Times(F.Subtract(px, v1x), F.Subtract(v2y, v1y))));

          IExpr c1 = engine.evaluate(F.LessEqual(v1y, py));
          IExpr c2 = engine.evaluate(F.Greater(v2y, py));

          if (c1.isTrue()) {
            if (c2.isTrue() && engine.evaluate(F.Greater(isLeft, F.C0)).isTrue()) {
              wn++;
            }
          } else if (c1.isFalse()) {
            if (engine.evaluate(F.LessEqual(v2y, py)).isTrue()
                && engine.evaluate(F.Less(isLeft, F.C0)).isTrue()) {
              wn--;
            }
          } else {
            return F.NIL;
          }
        }
        return wn != 0 ? S.True : S.False;
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
