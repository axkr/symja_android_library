package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Argument parsing and closed form measures for the geometric region primitives which are shared
 * between {@link org.matheclipse.core.reflection.system.Volume},
 * {@link org.matheclipse.core.reflection.system.SurfaceArea},
 * {@link org.matheclipse.core.reflection.system.RegionMeasure} and the other <code>Region*</code>
 * functions.
 */
public class RegionPrimitives {

  private RegionPrimitives() {}

  // ---------------------------------------------------------------- platonic solids

  /**
   * The center point and the edge length of one of the five platonic solids
   * <code>Tetrahedron, Cube, Octahedron, Dodecahedron, Icosahedron</code>.
   */
  public static final class SolidSpec {
    public final IExpr center;
    public final IExpr edge;

    private SolidSpec(IExpr center, IExpr edge) {
      this.center = center;
      this.edge = edge;
    }
  }

  private static boolean isCoordinateVector(IExpr expr, int size) {
    return expr.isList() && expr.argSize() == size && !((IAST) expr).arg1().isList();
  }

  /**
   * Parse the argument forms of a platonic solid.
   *
   * <pre>
   * X()                 center {0,0,0}, edge length 1
   * X(l)                edge length l
   * X({x,y,z})          center {x,y,z}, edge length 1
   * X({theta,phi})      rotated, center {0,0,0}, edge length 1
   * X(c, l)             center c, edge length l
   * X({theta,phi}, l)   rotated, edge length l
   * X({theta,phi}, c, l)
   * </pre>
   *
   * The <code>{theta,phi}</code> orientation does not change any of the measures, so it is accepted
   * and ignored here.
   *
   * @return <code>null</code> if the arguments don't match one of the forms above or if the edge
   *         length is negative
   */
  public static SolidSpec parsePlatonicSolid(IAST reg) {
    IExpr center = F.List(F.C0, F.C0, F.C0);
    IExpr edge = F.C1;
    switch (reg.argSize()) {
      case 0:
        break;
      case 1: {
        IExpr arg1 = reg.arg1();
        if (isCoordinateVector(arg1, 3)) {
          center = arg1;
        } else if (isCoordinateVector(arg1, 2)) {
          // {theta,phi} orientation
        } else if (arg1.isList()) {
          return null;
        } else {
          edge = arg1;
        }
        break;
      }
      case 2: {
        IExpr arg1 = reg.arg1();
        if (isCoordinateVector(arg1, 3)) {
          center = arg1;
        } else if (!isCoordinateVector(arg1, 2)) {
          return null;
        }
        edge = reg.arg2();
        if (edge.isList()) {
          return null;
        }
        break;
      }
      case 3: {
        if (!isCoordinateVector(reg.arg1(), 2) || !isCoordinateVector(reg.arg2(), 3)) {
          return null;
        }
        center = reg.arg2();
        edge = reg.arg3();
        if (edge.isList()) {
          return null;
        }
        break;
      }
      default:
        return null;
    }
    if (edge.isNegativeResult()) {
      return null;
    }
    return new SolidSpec(center, edge);
  }

  /**
   * <code>Tetrahedron({p1,p2,p3,p4})</code> is the general tetrahedron through four corner points,
   * not the regular solid.
   */
  public static boolean isCornerPointsForm(IAST reg) {
    return reg.argSize() == 1 && reg.arg1().isListOfLists();
  }

  /** The volume of a platonic solid with the given edge length. */
  public static IExpr platonicVolume(int ordinal, IExpr edge, EvalEngine engine) {
    IExpr cube = F.Power(edge, F.C3);
    switch (ordinal) {
      case ID.Tetrahedron:
        // a^3 / (6*Sqrt(2))
        return engine.evaluate(F.Divide(cube, F.Times(F.C6, F.Sqrt(F.C2))));
      case ID.Cube:
        return engine.evaluate(cube);
      case ID.Octahedron:
        // Sqrt(2)/3 * a^3
        return engine.evaluate(F.Times(F.C1D3, F.Sqrt(F.C2), cube));
      case ID.Dodecahedron:
        // (15+7*Sqrt(5))/4 * a^3
        return engine
            .evaluate(F.Times(F.C1D4, F.Plus(F.ZZ(15), F.Times(F.C7, F.Sqrt(F.C5))), cube));
      case ID.Icosahedron:
        // 5*(3+Sqrt(5))/12 * a^3
        return engine.evaluate(F.Times(F.QQ(5, 12), F.Plus(F.C3, F.Sqrt(F.C5)), cube));
    }
    return F.NIL;
  }

  /** The surface area of a platonic solid with the given edge length. */
  public static IExpr platonicSurfaceArea(int ordinal, IExpr edge, EvalEngine engine) {
    IExpr sqr = F.Sqr(edge);
    switch (ordinal) {
      case ID.Tetrahedron:
        return engine.evaluate(F.Times(F.Sqrt(F.C3), sqr));
      case ID.Cube:
        return engine.evaluate(F.Times(F.C6, sqr));
      case ID.Octahedron:
        return engine.evaluate(F.Times(F.C2, F.Sqrt(F.C3), sqr));
      case ID.Dodecahedron:
        // 3*Sqrt(5*(5+2*Sqrt(5))) * a^2
        return engine.evaluate(
            F.Times(F.C3, F.Sqrt(F.Times(F.C5, F.Plus(F.C5, F.Times(F.C2, F.Sqrt(F.C5))))), sqr));
      case ID.Icosahedron:
        return engine.evaluate(F.Times(F.C5, F.Sqrt(F.C3), sqr));
    }
    return F.NIL;
  }

  /** <code>true</code> for the heads of the five platonic solids. */
  public static boolean isPlatonicSolid(IExpr head) {
    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Tetrahedron:
        case ID.Cube:
        case ID.Octahedron:
        case ID.Dodecahedron:
        case ID.Icosahedron:
          return true;
      }
    }
    return false;
  }

  // ---------------------------------------------------------------- Torus / FilledTorus

  /**
   * A <code>Torus</code> or <code>FilledTorus</code>. <code>Torus(c, {rInner, rOuter})</code> is
   * given by its inner and outer radius; <code>major</code> is the radius of the center circle and
   * <code>minor</code> the radius of the tube.
   */
  public static final class TorusSpec {
    public final IExpr center;
    public final IExpr major;
    public final IExpr minor;

    private TorusSpec(IExpr center, IExpr major, IExpr minor) {
      this.center = center;
      this.major = major;
      this.minor = minor;
    }
  }

  /**
   * Parse <code>Torus()</code>, which is equivalent to <code>Torus({0,0,0}, {1/2, 1})</code>, and
   * <code>Torus(c, {rInner, rOuter})</code>.
   *
   * @return <code>null</code> if the arguments don't match one of those forms
   */
  public static TorusSpec parseTorus(IAST reg, EvalEngine engine) {
    IExpr center = F.List(F.C0, F.C0, F.C0);
    IExpr rInner = F.C1D2;
    IExpr rOuter = F.C1;
    if (reg.argSize() == 2) {
      if (!isCoordinateVector(reg.arg1(), 3) || !reg.arg2().isList2()) {
        return null;
      }
      center = reg.arg1();
      rInner = ((IAST) reg.arg2()).arg1();
      rOuter = ((IAST) reg.arg2()).arg2();
    } else if (reg.argSize() != 0) {
      return null;
    }
    IExpr major = engine.evaluate(F.Times(F.C1D2, F.Plus(rOuter, rInner)));
    IExpr minor = engine.evaluate(F.Times(F.C1D2, F.Subtract(rOuter, rInner)));
    return new TorusSpec(center, major, minor);
  }

  // ---------------------------------------------------------------- Parallelogram

  /** The base point and the two direction vectors of a <code>Parallelogram</code>. */
  public static final class ParallelogramSpec {
    public final IExpr base;
    public final IAST vectors;

    private ParallelogramSpec(IExpr base, IAST vectors) {
      this.base = base;
      this.vectors = vectors;
    }
  }

  /**
   * Parse <code>Parallelogram()</code>, which is equivalent to
   * <code>Parallelogram({0,0}, {{1,0},{1,1}})</code>, and <code>Parallelogram(p, {v1, v2})</code>.
   *
   * @return <code>null</code> if the arguments don't match one of those forms
   */
  public static ParallelogramSpec parseParallelogram(IAST reg) {
    if (reg.argSize() == 0) {
      return new ParallelogramSpec(F.List(F.C0, F.C0),
          F.list(F.List(F.C1, F.C0), F.List(F.C1, F.C1)));
    }
    if (reg.argSize() == 2 && reg.arg1().isList() && reg.arg2().isList2()
        && reg.arg2().isListOfLists()) {
      return new ParallelogramSpec(reg.arg1(), (IAST) reg.arg2());
    }
    return null;
  }

  /**
   * The area of the parallelogram spanned by the direction vectors. For vectors embedded in a
   * higher dimensional space this is the square root of the Gram determinant.
   */
  public static IExpr parallelogramArea(ParallelogramSpec spec, EvalEngine engine) {
    IAST v = spec.vectors;
    int embeddingDim = v.arg1().argSize();
    if (embeddingDim == 2) {
      return engine.evaluate(F.Abs(F.Det(v)));
    }
    // Sqrt(Det(v.Transpose(v)))
    return engine.evaluate(F.Sqrt(F.Det(F.Dot(v, F.Transpose(v)))));
  }

  // ---------------------------------------------------------------- SphericalShell

  /** The center point and the inner and outer radius of a <code>SphericalShell</code>. */
  public static final class ShellSpec {
    public final IExpr center;
    public final IExpr rInner;
    public final IExpr rOuter;
    /** <code>true</code> if both radii are explicit real numbers. */
    public final boolean numericRadii;

    private ShellSpec(IExpr center, IExpr rInner, IExpr rOuter, boolean numericRadii) {
      this.center = center;
      this.rInner = rInner;
      this.rOuter = rOuter;
      this.numericRadii = numericRadii;
    }
  }

  /**
   * Bring <code>SphericalShell</code> into the standard form
   * <code>SphericalShell(c, {rInner, rOuter})</code>.
   *
   * <pre>
   * SphericalShell()                  SphericalShell({0,0,0}, {1/2, 1})
   * SphericalShell(r)                 SphericalShell({0,0,0}, {r/2, r})
   * SphericalShell({rin,rout})        SphericalShell({0,0,0}, {rin, rout})
   * </pre>
   *
   * @return {@link F#NIL} if <code>reg</code> already is in standard form or doesn't match one of
   *         the forms above
   */
  public static IExpr sphericalShellStandardForm(IAST reg) {
    IExpr origin = F.List(F.C0, F.C0, F.C0);
    switch (reg.argSize()) {
      case 0:
        return F.binaryAST2(S.SphericalShell, origin, F.List(F.C1D2, F.C1));
      case 1: {
        IExpr arg1 = reg.arg1();
        if (isCoordinateVector(arg1, 2)) {
          return F.binaryAST2(S.SphericalShell, origin, arg1);
        }
        if (!arg1.isList()) {
          return F.binaryAST2(S.SphericalShell, origin, F.List(F.Times(F.C1D2, arg1), arg1));
        }
        return F.NIL;
      }
      default:
        return F.NIL;
    }
  }

  /**
   * Parse the standard form <code>SphericalShell(c, {rInner, rOuter})</code>. Numeric radii are
   * sorted, so that <code>{5, 2}</code> describes the same region as <code>{2, 5}</code>.
   *
   * @return <code>null</code> if <code>reg</code> is not in standard form
   */
  public static ShellSpec parseSphericalShell(IAST reg) {
    if (reg.argSize() != 2 || !isCoordinateVector(reg.arg1(), 3) || !reg.arg2().isList2()) {
      return null;
    }
    IAST radii = (IAST) reg.arg2();
    IExpr rInner = radii.arg1();
    IExpr rOuter = radii.arg2();
    boolean numeric = rInner.isRealResult() && rInner.isNumber() //
        && rOuter.isRealResult() && rOuter.isNumber();
    if (numeric && rInner.greaterThan(rOuter).isTrue()) {
      IExpr swap = rInner;
      rInner = rOuter;
      rOuter = swap;
    }
    return new ShellSpec(reg.arg1(), rInner, rOuter, numeric);
  }

  // ---------------------------------------------------------------- CapsuleShape

  /** The two end points of the axis segment and the radius of a <code>CapsuleShape</code>. */
  public static final class CapsuleSpec {
    public final IExpr p1;
    public final IExpr p2;
    public final IExpr radius;

    private CapsuleSpec(IExpr p1, IExpr p2, IExpr radius) {
      this.p1 = p1;
      this.p2 = p2;
      this.radius = radius;
    }
  }

  /**
   * Bring <code>CapsuleShape</code> into the standard form <code>CapsuleShape({p1, p2}, r)</code>.
   *
   * <pre>
   * CapsuleShape()      CapsuleShape({{-1,0,0}, {1,0,0}}, 1)
   * CapsuleShape(r)     CapsuleShape({{-1,0,0}, {1,0,0}}, r)
   * </pre>
   */
  public static IExpr capsuleShapeStandardForm(IAST reg) {
    IAST defaultAxis = F.list(F.List(F.CN1, F.C0, F.C0), F.List(F.C1, F.C0, F.C0));
    switch (reg.argSize()) {
      case 0:
        return F.binaryAST2(S.CapsuleShape, defaultAxis, F.C1);
      case 1:
        if (!reg.arg1().isList()) {
          return F.binaryAST2(S.CapsuleShape, defaultAxis, reg.arg1());
        }
        return F.NIL;
      default:
        return F.NIL;
    }
  }

  /**
   * Parse the standard form <code>CapsuleShape({p1, p2}, r)</code>. Only the three dimensional
   * capsule is supported.
   *
   * @return <code>null</code> if <code>reg</code> is not a three dimensional capsule
   */
  public static CapsuleSpec parseCapsuleShape(IAST reg) {
    if (reg.argSize() != 2 || !reg.arg1().isList2()) {
      return null;
    }
    IAST axis = (IAST) reg.arg1();
    if (!isCoordinateVector(axis.arg1(), 3) || !isCoordinateVector(axis.arg2(), 3)) {
      return null;
    }
    return new CapsuleSpec(axis.arg1(), axis.arg2(), reg.arg2());
  }

  /** <code>4*Pi*(rOuter^3 - rInner^3)/3</code> */
  public static IExpr sphericalShellVolume(IAST reg, EvalEngine engine) {
    ShellSpec spec = parseSphericalShell(reg);
    if (spec == null) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.QQ(4, 3), S.Pi,
        F.Subtract(F.Power(spec.rOuter, F.C3), F.Power(spec.rInner, F.C3))));
  }

  /** <code>Pi*r^2*h + 4*Pi*r^3/3</code> for the cylinder and the two hemispherical caps. */
  public static IExpr capsuleShapeVolume(IAST reg, EvalEngine engine) {
    CapsuleSpec spec = parseCapsuleShape(reg);
    if (spec == null) {
      return F.NIL;
    }
    IExpr h = distance(spec.p1, spec.p2, engine);
    return engine.evaluate(F.Plus(//
        F.Times(S.Pi, F.Sqr(spec.radius), h), //
        F.Times(F.QQ(4, 3), S.Pi, F.Power(spec.radius, F.C3))));
  }

  // ---------------------------------------------------------------- StadiumShape

  /** The two end points of the axis segment and the radius of a <code>StadiumShape</code>. */
  public static final class StadiumSpec {
    public final IExpr p1;
    public final IExpr p2;
    public final IExpr radius;

    private StadiumSpec(IExpr p1, IExpr p2, IExpr radius) {
      this.p1 = p1;
      this.p2 = p2;
      this.radius = radius;
    }
  }

  /**
   * Bring <code>StadiumShape</code> into the standard form <code>StadiumShape({p1, p2}, r)</code>.
   *
   * <pre>
   * StadiumShape()      StadiumShape({{-1,0}, {1,0}}, 1)
   * StadiumShape(r)     StadiumShape({{-1,0}, {1,0}}, r)
   * </pre>
   */
  public static IExpr stadiumShapeStandardForm(IAST reg) {
    IAST defaultAxis = F.list(F.List(F.CN1, F.C0), F.List(F.C1, F.C0));
    switch (reg.argSize()) {
      case 0:
        return F.binaryAST2(S.StadiumShape, defaultAxis, F.C1);
      case 1:
        if (!reg.arg1().isList()) {
          return F.binaryAST2(S.StadiumShape, defaultAxis, reg.arg1());
        }
        return F.NIL;
      default:
        return F.NIL;
    }
  }

  /**
   * Parse the standard form <code>StadiumShape({p1, p2}, r)</code>.
   *
   * @return <code>null</code> if <code>reg</code> is not in standard form
   */
  public static StadiumSpec parseStadiumShape(IAST reg) {
    if (reg.argSize() != 2 || !reg.arg1().isList2() || !reg.arg1().isListOfLists()) {
      return null;
    }
    IAST axis = (IAST) reg.arg1();
    return new StadiumSpec(axis.arg1(), axis.arg2(), reg.arg2());
  }

  // ---------------------------------------------------------------- HalfSpace

  /** The normal vector and the right hand side of the inequality <code>n.x &lt;= c</code>. */
  public static final class HalfSpaceSpec {
    public final IAST normal;
    public final IExpr offset;

    private HalfSpaceSpec(IAST normal, IExpr offset) {
      this.normal = normal;
      this.offset = offset;
    }
  }

  /**
   * Parse <code>HalfSpace(n, c)</code>, the points <code>x</code> with <code>n.x &lt;= c</code>,
   * and <code>HalfSpace(n, p)</code> for a point <code>p</code> on the bounding hyperplane, which
   * is the same as <code>n.x &lt;= n.p</code>.
   *
   * @return <code>null</code> if the arguments don't match one of those forms
   */
  public static HalfSpaceSpec parseHalfSpace(IAST reg, EvalEngine engine) {
    if (reg.argSize() != 2 || !reg.arg1().isList() || reg.arg1().isListOfLists()) {
      return null;
    }
    IAST normal = (IAST) reg.arg1();
    IExpr offset = reg.arg2();
    if (offset.isList()) {
      if (offset.argSize() != normal.argSize()) {
        return null;
      }
      offset = engine.evaluate(F.Dot(normal, offset));
    }
    return new HalfSpaceSpec(normal, offset);
  }

  // ---------------------------------------------------------------- DiskSegment

  /**
   * The center, the two semi axes and the two angles of a <code>DiskSegment</code>. The segment is
   * cut off by the chord between the two boundary points at <code>theta1</code> and
   * <code>theta2</code>.
   */
  public static final class DiskSegmentSpec {
    public final IExpr center;
    public final IExpr rx;
    public final IExpr ry;
    public final IExpr theta1;
    public final IExpr theta2;
    /** <code>theta2 - theta1</code> */
    public final IExpr angle;

    private DiskSegmentSpec(IExpr center, IExpr rx, IExpr ry, IExpr theta1, IExpr theta2,
        IExpr angle) {
      this.center = center;
      this.rx = rx;
      this.ry = ry;
      this.theta1 = theta1;
      this.theta2 = theta2;
      this.angle = angle;
    }

    /** <code>true</code> for a circular, i.e. non elliptical, segment. */
    public boolean isCircular() {
      return rx.equals(ry);
    }
  }

  /**
   * Parse <code>DiskSegment(c, r, {theta1, theta2})</code> and the elliptical
   * <code>DiskSegment(c, {rx, ry}, {theta1, theta2})</code>.
   *
   * @return <code>null</code> if the arguments don't match one of those forms
   */
  public static DiskSegmentSpec parseDiskSegment(IAST reg, EvalEngine engine) {
    if (reg.argSize() != 3 || !isCoordinateVector(reg.arg1(), 2) || !reg.arg3().isList2()) {
      return null;
    }
    IExpr rx;
    IExpr ry;
    if (reg.arg2().isList2()) {
      rx = ((IAST) reg.arg2()).arg1();
      ry = ((IAST) reg.arg2()).arg2();
    } else if (!reg.arg2().isList()) {
      rx = reg.arg2();
      ry = rx;
    } else {
      return null;
    }
    IAST angles = (IAST) reg.arg3();
    IExpr angle = engine.evaluate(F.Subtract(angles.arg2(), angles.arg1()));
    return new DiskSegmentSpec(reg.arg1(), rx, ry, angles.arg1(), angles.arg2(), angle);
  }

  // ---------------------------------------------------------------- region moments

  /**
   * The raw polynomial moment <code>Integrate(x1^p1 * ... * xn^pn)</code> over the region, without
   * normalizing by the region measure.
   *
   * @return {@link F#NIL} if the moment of <code>reg</code> is not supported
   */
  public static IExpr regionMoment(IAST reg, int[] exponents, EvalEngine engine) {
    IExpr head = reg.head();
    if (!head.isBuiltInSymbol()) {
      return F.NIL;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Rectangle:
      case ID.Cuboid:
        return boxMoment(reg, exponents, engine);
      case ID.Disk:
      case ID.Ball:
        return ballMoment(reg, exponents, engine);
      case ID.Triangle:
        return triangleRegionMoment(reg, exponents, engine);
      case ID.Polygon:
        return polygonMoment(reg, exponents, engine);
    }
    return F.NIL;
  }

  /**
   * The moment of an axis aligned box is the product of the one dimensional moments
   * <code>(upper^(p+1) - lower^(p+1))/(p+1)</code>.
   */
  private static IExpr boxMoment(IAST reg, int[] exponents, EvalEngine engine) {
    int n = reg.head() == S.Rectangle ? 2 : 3;
    IAST lower;
    IAST upper;
    if (reg.argSize() == 0) {
      lower = constantVector(F.C0, n);
      upper = constantVector(F.C1, n);
    } else if (reg.argSize() == 1 && reg.arg1().isList()) {
      lower = (IAST) reg.arg1();
      IASTAppendable up = F.ListAlloc(lower.argSize());
      for (int i = 1; i <= lower.argSize(); i++) {
        up.append(F.Plus(lower.get(i), F.C1));
      }
      upper = up;
    } else if (reg.argSize() == 2 && reg.arg1().isList() && reg.arg2().isList()) {
      lower = (IAST) reg.arg1();
      upper = (IAST) reg.arg2();
    } else {
      return F.NIL;
    }
    if (lower.argSize() != exponents.length || upper.argSize() != exponents.length) {
      return F.NIL;
    }
    IExpr result = F.C1;
    for (int i = 0; i < exponents.length; i++) {
      IInteger power = F.ZZ(exponents[i] + 1);
      result = F.Times(result, F.Divide(
          F.Subtract(F.Power(upper.get(i + 1), power), F.Power(lower.get(i + 1), power)), power));
    }
    return engine.evaluate(result);
  }

  /**
   * <code>Integrate(u1^k1 * ... * un^kn)</code> over the unit ball. The integral vanishes as soon
   * as one exponent is odd, otherwise it is
   * <code>Product(Gamma((ki+1)/2)) / Gamma(1 + (Sum(ki) + n)/2)</code>.
   */
  private static IExpr unitBallMoment(int[] k, EvalEngine engine) {
    int sum = 0;
    for (int ki : k) {
      if ((ki & 1) == 1) {
        return F.C0;
      }
      sum += ki;
    }
    IExpr numerator = F.C1;
    for (int ki : k) {
      numerator = F.Times(numerator, F.Gamma(F.QQ(ki + 1, 2)));
    }
    return engine.evaluate(F.Divide(numerator, F.Gamma(F.Plus(F.C1, F.QQ(sum + k.length, 2)))));
  }

  /**
   * The moment of a <code>Disk</code> or <code>Ball</code>, which is obtained from the unit ball
   * moments by substituting <code>xi -> ci + ri*ui</code>.
   */
  private static IExpr ballMoment(IAST reg, int[] exponents, EvalEngine engine) {
    int n = exponents.length;
    IAST center;
    if (reg.argSize() == 0) {
      center = constantVector(F.C0, n);
    } else if (reg.arg1().isList() && reg.arg1().argSize() == n) {
      center = (IAST) reg.arg1();
    } else {
      return F.NIL;
    }
    IAST radii;
    if (reg.argSize() >= 2) {
      if (reg.arg2().isList()) {
        if (reg.arg2().argSize() != n) {
          return F.NIL;
        }
        radii = (IAST) reg.arg2();
      } else {
        radii = constantVector(reg.arg2(), n);
      }
    } else {
      radii = constantVector(F.C1, n);
    }

    // the substitution contributes the Jacobian Product(ri)
    IExpr jacobian = F.C1;
    for (int i = 1; i <= n; i++) {
      jacobian = F.Times(jacobian, radii.get(i));
    }

    IASTAppendable sum = F.PlusAlloc(8);
    int[] index = new int[n];
    while (true) {
      IExpr term = unitBallMoment(index, engine);
      if (!term.isZero()) {
        for (int i = 0; i < n; i++) {
          term = F.Times(term, //
              F.ZZ(binomial(exponents[i], index[i])), //
              intPower(center.get(i + 1), exponents[i] - index[i]), //
              intPower(radii.get(i + 1), index[i]));
        }
        sum.append(term);
      }
      if (!increment(index, exponents)) {
        break;
      }
    }
    return engine.evaluate(F.Times(jacobian, sum));
  }

  /** Increment the multi-index <code>index</code>, bounded component wise by <code>max</code>. */
  private static boolean increment(int[] index, int[] max) {
    for (int i = index.length - 1; i >= 0; i--) {
      if (index[i] < max[i]) {
        index[i]++;
        return true;
      }
      index[i] = 0;
    }
    return false;
  }

  /**
   * <code>base^exponent</code> for a non-negative machine integer exponent. In contrast to
   * {@link F#Power(IExpr, IExpr)} this gives <code>1</code> for <code>0^0</code>, which is the
   * right value for an empty product in the expansions above.
   */
  private static IExpr intPower(IExpr base, int exponent) {
    if (exponent == 0) {
      return F.C1;
    }
    if (exponent == 1) {
      return base;
    }
    return F.Power(base, F.ZZ(exponent));
  }

  private static long binomial(int n, int k) {
    if (k < 0 || k > n) {
      return 0L;
    }
    long result = 1L;
    for (int i = 1; i <= k; i++) {
      result = result * (n - k + i) / i;
    }
    return result;
  }

  private static long factorial(int n) {
    long result = 1L;
    for (int i = 2; i <= n; i++) {
      result *= i;
    }
    return result;
  }

  private static IExpr triangleRegionMoment(IAST reg, int[] exponents, EvalEngine engine) {
    if (exponents.length != 2) {
      return F.NIL;
    }
    IAST points;
    if (reg.argSize() == 0) {
      points = F.list(F.List(F.C0, F.C0), F.List(F.C1, F.C0), F.List(F.C0, F.C1));
    } else if (reg.argSize() == 1 && reg.arg1().isListOfLists() && reg.arg1().argSize() == 3) {
      points = (IAST) reg.arg1();
    } else {
      return F.NIL;
    }
    IExpr moment = triangleMoment(points.arg1(), points.arg2(), points.arg3(), exponents, engine);
    if (!moment.isPresent()) {
      return F.NIL;
    }
    // a single triangle has no orientation, so the unsigned area is used
    IExpr det = triangleDet(points.arg1(), points.arg2(), points.arg3(), engine);
    return engine.evaluate(F.Times(F.Abs(det), moment));
  }

  /**
   * A simple polygon is decomposed into a triangle fan around its first corner point. The signed
   * determinants make the parts outside of the polygon cancel out, so this works for non convex
   * polygons too.
   */
  private static IExpr polygonMoment(IAST reg, int[] exponents, EvalEngine engine) {
    if (exponents.length != 2 || reg.argSize() != 1 || !reg.arg1().isListOfLists()) {
      return F.NIL;
    }
    IAST points = (IAST) reg.arg1();
    if (points.argSize() < 3) {
      return F.NIL;
    }
    IExpr p1 = points.arg1();
    IASTAppendable sum = F.PlusAlloc(points.argSize());
    IASTAppendable areaSum = F.PlusAlloc(points.argSize());
    for (int i = 2; i < points.argSize(); i++) {
      IExpr p2 = points.get(i);
      IExpr p3 = points.get(i + 1);
      IExpr moment = triangleMoment(p1, p2, p3, exponents, engine);
      if (!moment.isPresent()) {
        return F.NIL;
      }
      IExpr det = triangleDet(p1, p2, p3, engine);
      sum.append(F.Times(det, moment));
      areaSum.append(det);
    }
    IExpr result = engine.evaluate(sum);
    // clockwise corner points give a negative orientation
    if (engine.evaluate(areaSum).isNegativeResult()) {
      result = engine.evaluate(F.Negate(result));
    }
    return result;
  }

  /** <code>Det({p2-p1, p3-p1})</code>, twice the signed area of the triangle. */
  private static IExpr triangleDet(IExpr p1, IExpr p2, IExpr p3, EvalEngine engine) {
    return engine.evaluate(F.Det(F.List(F.Subtract(p2, p1), F.Subtract(p3, p1))));
  }

  /**
   * <code>Integrate(x^p * y^q)</code> over the triangle <code>p1, p2, p3</code>, divided by the
   * determinant <code>Det({p2-p1, p3-p1})</code>. Substituting
   * <code>x -> x1 + u*(x2-x1) + v*(x3-x1)</code> maps the triangle onto the unit triangle, where
   * <code>Integrate(u^a * v^b) == a! * b! / (a+b+2)!</code>.
   */
  private static IExpr triangleMoment(IExpr p1, IExpr p2, IExpr p3, int[] exponents,
      EvalEngine engine) {
    if (!p1.isList2() || !p2.isList2() || !p3.isList2()) {
      return F.NIL;
    }
    IExpr x1 = ((IAST) p1).arg1();
    IExpr y1 = ((IAST) p1).arg2();
    IExpr ax = F.Subtract(((IAST) p2).arg1(), x1);
    IExpr bx = F.Subtract(((IAST) p3).arg1(), x1);
    IExpr ay = F.Subtract(((IAST) p2).arg2(), y1);
    IExpr by = F.Subtract(((IAST) p3).arg2(), y1);

    int p = exponents[0];
    int q = exponents[1];
    IASTAppendable sum = F.PlusAlloc(16);
    for (int a = 0; a <= p; a++) {
      for (int b = 0; a + b <= p; b++) {
        long cx = factorial(p) / (factorial(p - a - b) * factorial(a) * factorial(b));
        IExpr termX = F.Times(F.ZZ(cx), intPower(x1, p - a - b), intPower(ax, a), intPower(bx, b));
        for (int c = 0; c <= q; c++) {
          for (int d = 0; c + d <= q; d++) {
            long cy = factorial(q) / (factorial(q - c - d) * factorial(c) * factorial(d));
            IExpr termY =
                F.Times(F.ZZ(cy), intPower(y1, q - c - d), intPower(ay, c), intPower(by, d));
            // Integrate(u^(a+c) * v^(b+d)) over the unit triangle
            long integral = factorial(a + c) * factorial(b + d);
            sum.append(F.Times(termX, termY, F.QQ(integral, factorial(a + c + b + d + 2))));
          }
        }
      }
    }
    return engine.evaluate(sum);
  }

  /**
   * The moment <code>Integrate((x1-c1)^p1 * ... * (xn-cn)^pn)</code> around the point
   * <code>c</code>, expanded into the raw moments of the region.
   */
  public static IExpr shiftedMoment(IAST reg, int[] exponents, IAST center, EvalEngine engine) {
    int n = exponents.length;
    if (center.argSize() != n) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(8);
    int[] index = new int[n];
    while (true) {
      IExpr moment = regionMoment(reg, index, engine);
      if (!moment.isPresent()) {
        return F.NIL;
      }
      if (!moment.isZero()) {
        IExpr term = moment;
        for (int i = 0; i < n; i++) {
          term = F.Times(term, F.ZZ(binomial(exponents[i], index[i])),
              intPower(F.Negate(center.get(i + 1)), exponents[i] - index[i]));
        }
        sum.append(term);
      }
      if (!increment(index, exponents)) {
        break;
      }
    }
    return engine.evaluate(sum);
  }

  // ---------------------------------------------------------------- helpers

  /** The euclidean distance between two coordinate vectors. */
  public static IExpr distance(IExpr p1, IExpr p2, EvalEngine engine) {
    return engine.evaluate(F.Norm(F.Subtract(p1, p2)));
  }

  /** A list of <code>n</code> copies of <code>expr</code>. */
  public static IAST constantVector(IExpr expr, int n) {
    IASTAppendable result = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      result.append(expr);
    }
    return result;
  }

  /** The midpoint of two coordinate vectors. */
  public static IExpr midpoint(IExpr p1, IExpr p2, EvalEngine engine) {
    return engine.evaluate(F.Times(F.C1D2, F.Plus(p1, p2)));
  }

  /**
   * <code>Indeterminate</code> repeated <code>n</code> times - the centroid of an unbounded region.
   */
  public static IAST indeterminateCentroid(int n) {
    return constantVector(S.Indeterminate, n);
  }
}
