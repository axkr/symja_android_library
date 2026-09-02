package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
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
    if (!axis.arg1().isList() || !((IAST) axis.arg1()).arg1().isList()) {
      // a capsule of any embedding dimension - in the plane it is a stadium
      int dimension = axis.arg1().argSize();
      if (dimension < 2 || !isCoordinateVector(axis.arg1(), dimension)
          || !isCoordinateVector(axis.arg2(), dimension)) {
        return null;
      }
      return new CapsuleSpec(axis.arg1(), axis.arg2(), reg.arg2());
    }
    return null;
  }

  /** The embedding dimension of a parsed capsule. */
  public static int capsuleDimension(CapsuleSpec spec) {
    return spec.p1.argSize();
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
    if (spec == null || capsuleDimension(spec) != 3) {
      // the cylinder plus two hemispherical caps is the three dimensional formula
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

  // ---------------------------------------------------------------- parametrized surfaces

  /** A parameter range <code>{u, umin, umax}</code> of a parametrized surface. */
  public static final class ParameterRange {
    public final IExpr variable;
    public final IExpr min;
    public final IExpr max;

    private ParameterRange(IExpr variable, IExpr min, IExpr max) {
      this.variable = variable;
      this.min = min;
      this.max = max;
    }
  }

  /**
   * Parse a <code>{u, umin, umax}</code> iterator of a parametrized surface.
   *
   * @return <code>null</code> if <code>expr</code> is not such a triple with a symbol in front
   */
  public static ParameterRange parseParameterRange(IExpr expr) {
    if (!expr.isList3()) {
      return null;
    }
    IAST range = (IAST) expr;
    if (!range.arg1().isSymbol()) {
      return null;
    }
    return new ParameterRange(range.arg1(), range.arg2(), range.arg3());
  }

  /**
   * The coordinate functions of a parametrized surface. A single scalar <code>x</code> describes
   * the graph <code>{s, t, x}</code> over the two parameters.
   *
   * @return {@link F#NIL} if <code>expr</code> is a list which is not a vector of coordinates
   */
  public static IAST surfaceCoordinates(IExpr expr, ParameterRange s, ParameterRange t) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.argSize() < 2 || list.arg1().isList()) {
        return F.NIL;
      }
      return list;
    }
    return F.list(s.variable, t.variable, expr);
  }

  /**
   * The area element <code>Sqrt(E*G - F^2)</code> of the first fundamental form, where
   * <code>E = rs.rs</code>, <code>F = rs.rt</code> and <code>G = rt.rt</code>. This is the norm of
   * the cross product in three dimensions and generalizes it to any number of coordinates.
   */
  public static IExpr surfaceAreaElement(IAST coordinates, ParameterRange s, ParameterRange t,
      EvalEngine engine) {
    IExpr rs = engine.evaluate(F.D(coordinates, s.variable));
    IExpr rt = engine.evaluate(F.D(coordinates, t.variable));
    if (!rs.isList() || !rt.isList()) {
      return F.NIL;
    }
    IExpr e = F.Dot(rs, rs);
    IExpr f = F.Dot(rs, rt);
    IExpr g = F.Dot(rt, rt);
    return reduceOnDomain(F.Sqrt(F.Subtract(F.Times(e, g), F.Sqr(f))), engine, s, t);
  }

  /**
   * Simplify a square root which the integration has to see in its reduced form, under the
   * assumption that the parameters stay inside their ranges.
   *
   * <p>
   * The area element and the speed of a curve are square roots of a sum of squares, and the
   * {@link S#Simplify} half is what turns the raw first fundamental form of the unit sphere into
   * the <code>Sqrt(Sin(u)^2)</code> that <code>Integrate</code> can work with at all. Restricting
   * the parameters to the rectangle which is integrated over then reduces that to
   * <code>Sin(u)</code>, which is valid because the integral only ever evaluates the element
   * there.
   *
   * <p>
   * The refinement used to be load bearing: <code>Integrate(Sqrt(Sin(u)^2), {u,0,Pi})</code>
   * answered <code>0</code> instead of <code>2</code>, so an unreduced element gave a wrong area.
   * That defect is fixed (the limit machinery now reads <code>Sqrt(f^2)</code> as
   * <code>Abs(f)</code> and the range is split at the sign changes), so this is a simplification
   * aid rather than a correctness workaround.
   */
  private static IExpr reduceOnDomain(IExpr element, EvalEngine engine, ParameterRange... ranges) {
    IASTAppendable bounds = F.ast(S.And, 2 * ranges.length);
    for (ParameterRange range : ranges) {
      bounds.append(F.LessEqual(range.min, range.variable));
      bounds.append(F.LessEqual(range.variable, range.max));
    }
    // Refine has the HoldAll attribute, and an expression which has already been evaluated carries
    // a flag which stops it from being rewritten a second time. So the unevaluated Simplify(...)
    // tree is handed over as a whole and the simplification runs *under* the assumptions - passing
    // an already evaluated element here silently returns it unchanged.
    IExpr refined = engine.evaluate(F.Refine(F.Simplify(element), bounds));
    return refined.isPresent() ? refined : engine.evaluate(F.Simplify(element));
  }

  /**
   * The arc length <code>Integrate(Norm(D(curve, u)), {u, umin, umax})</code> of one coordinate
   * curve of a parametrized surface.
   */
  public static IExpr curveArcLength(IAST coordinates, ParameterRange along, ParameterRange fixed,
      IExpr fixedValue, boolean numeric, EvalEngine engine) {
    IExpr curve = engine.evaluate(F.subst(coordinates, F.Rule(fixed.variable, fixedValue)));
    IExpr derivative = engine.evaluate(F.D(curve, along.variable));
    if (!derivative.isList()) {
      return F.NIL;
    }
    IExpr speed = reduceOnDomain(F.Sqrt(F.Total(F.Sqr(derivative))), engine, along);
    return integrateAlong(speed, along, numeric);
  }

  // ---------------------------------------------------------------- measure options

  /**
   * The option symbols which <code>Area</code>, <code>Perimeter</code> and <code>Volume</code>
   * accept, in the order in which they are handed to the evaluator.
   */
  public static final IBuiltInSymbol[] MEASURE_OPTION_KEYS = new IBuiltInSymbol[] {//
      S.AccuracyGoal, S.Assumptions, S.GenerateConditions, //
      S.PerformanceGoal, S.PrecisionGoal, S.WorkingPrecision};

  /** The default values of {@link #MEASURE_OPTION_KEYS}. */
  public static final IExpr[] MEASURE_OPTION_DEFAULTS = new IExpr[] {//
      F.CInfinity, S.$Assumptions, S.Automatic, //
      S.$PerformanceGoal, S.Automatic, S.Automatic};

  /** The index of <code>AccuracyGoal</code> in {@link #MEASURE_OPTION_KEYS}. */
  public static final int MEASURE_OPTION_ACCURACY_GOAL = 0;

  /** The index of <code>Assumptions</code> in {@link #MEASURE_OPTION_KEYS}. */
  public static final int MEASURE_OPTION_ASSUMPTIONS = 1;

  /** The index of <code>PrecisionGoal</code> in {@link #MEASURE_OPTION_KEYS}. */
  public static final int MEASURE_OPTION_PRECISION_GOAL = 4;

  /** The index of <code>WorkingPrecision</code> in {@link #MEASURE_OPTION_KEYS}. */
  public static final int MEASURE_OPTION_WORKING_PRECISION = 5;

  /** The value of one option, or {@link F#NIL} if the option was not given. */
  private static IExpr option(IExpr[] options, int index) {
    if (options == null || options.length <= index || options[index] == null) {
      return F.NIL;
    }
    return options[index];
  }

  /**
   * One of the numerical goals asks for a number rather than for a closed form, so an integral
   * which a measure has to evaluate should be handed to <code>NIntegrate</code> instead of to
   * <code>Integrate</code>.
   *
   * <p>
   * This only concerns the parametrized forms of <code>Area</code> and <code>Perimeter</code> -
   * the closed form measures of the region primitives never integrate, which is why the same
   * options have no effect on them.
   */
  public static boolean requestsNumericIntegration(IExpr[] options) {
    return isNumericGoal(option(options, MEASURE_OPTION_WORKING_PRECISION))
        || isNumericGoal(option(options, MEASURE_OPTION_PRECISION_GOAL))
        || isNumericGoal(option(options, MEASURE_OPTION_ACCURACY_GOAL));
  }

  /** <code>MachinePrecision</code> or a positive number of digits. */
  private static boolean isNumericGoal(IExpr value) {
    if (value.isNIL() || value == S.Automatic || value.isInfinity()) {
      return false;
    }
    return value == S.MachinePrecision || value.toIntDefault() > 0;
  }

  /**
   * Integrate over the rectangle which the two parameter ranges span, symbolically or - if one of
   * the numerical goals asks for it - numerically.
   */
  public static IExpr integrateOverRectangle(IExpr integrand, ParameterRange s, ParameterRange t,
      boolean numeric, EvalEngine engine) {
    IAST sIterator = F.list(s.variable, s.min, s.max);
    IAST tIterator = F.list(t.variable, t.min, t.max);
    // built explicitly: the three argument F.NIntegrate(f, x, optionRule) convenience overload
    // names its last parameter for an option rule, not for a second iterator
    return engine.evaluate(F.ast(new IExpr[] {integrand, sIterator, tIterator},
        numeric ? S.NIntegrate : S.Integrate));
  }

  /** Integrate along one parameter range, symbolically or numerically. */
  public static IExpr integrateAlong(IExpr integrand, ParameterRange along, boolean numeric) {
    IAST iterator = F.list(along.variable, along.min, along.max);
    return F.ast(new IExpr[] {integrand, iterator}, numeric ? S.NIntegrate : S.Integrate);
  }

  /**
   * Apply the options which change the value of a region measure.
   *
   * <p>
   * <code>Assumptions</code> is used to {@link S#Refine} the symbolic result, so that
   * <code>Area(Rectangle({0,0},{a,b}), Assumptions -> a>0 && b>0)</code> gives <code>a*b</code>
   * instead of <code>Abs(a*b)</code>. <code>WorkingPrecision</code> evaluates the result
   * numerically. The remaining options only steer the numerical integration which Symja does not
   * use for the closed form measures, so they are accepted and ignored.
   *
   * @param result the measure which was computed without looking at the options
   */
  public static IExpr applyMeasureOptions(IExpr result, IExpr[] options, EvalEngine engine) {
    if (result.isNIL() || options == null) {
      return result;
    }
    IExpr value = result;
    IExpr assumptions = options.length > MEASURE_OPTION_ASSUMPTIONS //
        ? options[MEASURE_OPTION_ASSUMPTIONS]
        : F.NIL;
    if (assumptions != null && assumptions.isPresent() && assumptions.isAST()) {
      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        IAssumptions newAssumptions = Assumptions.getInstance(assumptions);
        if (newAssumptions != null) {
          engine.setAssumptions(newAssumptions);
          value = engine.evaluate(F.Refine(value, assumptions));
        }
      } finally {
        engine.setAssumptions(oldAssumptions);
      }
    }
    IExpr workingPrecision = options.length > MEASURE_OPTION_WORKING_PRECISION //
        ? options[MEASURE_OPTION_WORKING_PRECISION]
        : F.NIL;
    if (workingPrecision != null && workingPrecision.isPresent()) {
      if (workingPrecision == S.MachinePrecision) {
        value = engine.evaluate(F.N(value));
      } else {
        int precision = workingPrecision.toIntDefault();
        if (precision > 0) {
          value = engine.evaluate(F.N(value, F.ZZ(precision)));
        }
      }
    }
    return value;
  }

  // ---------------------------------------------------------------- dimensional dispatch

  /** The region has exactly the dimension which was asked for. */
  public static final int DIMENSION_MATCHES = 1;

  /** The dimension of the region could not be determined. */
  public static final int DIMENSION_UNKNOWN = 0;

  /** The region is of a different dimension than the one which was asked for. */
  public static final int DIMENSION_DIFFERS = -1;

  /**
   * Compare the {@link org.matheclipse.core.reflection.system.RegionDimension} of a region with the
   * dimension a measure function asks for.
   *
   * <p>
   * <code>Area</code>, <code>Perimeter</code> and <code>Volume</code> are only defined for regions
   * of one particular dimension and give <code>Undefined</code> for every other region. Deciding
   * that centrally means a head which has no closed form measure still answers
   * <code>Undefined</code> instead of staying unevaluated.
   *
   * @return {@link #DIMENSION_MATCHES}, {@link #DIMENSION_UNKNOWN} or {@link #DIMENSION_DIFFERS}
   */
  public static int dimensionMatch(IExpr region, int wantedDimension) {
    int dimension =
        org.matheclipse.core.reflection.system.RegionDimension.getRegionDimension(region);
    if (dimension < 0) {
      return DIMENSION_UNKNOWN;
    }
    return dimension == wantedDimension ? DIMENSION_MATCHES : DIMENSION_DIFFERS;
  }

  /**
   * The number of points of an <code>EmptyRegion(n)</code> is zero, so every measure of it is zero.
   *
   * @return {@link F#NIL} if <code>reg</code> is not an <code>EmptyRegion(n)</code>
   */
  public static IExpr emptyRegionMeasure(IAST reg) {
    if (reg.isAST(S.EmptyRegion, 2) && reg.arg1().toIntDefault() > 0) {
      return F.C0;
    }
    return F.NIL;
  }

  // ---------------------------------------------------------------- axis aligned boxes

  /**
   * The two opposite corners of an axis aligned box.
   *
   * <pre>
   * Rectangle()          {0,0} .. {1,1}
   * Cuboid()             {0,0,0} .. {1,1,1}
   * Rectangle(p)         p .. p+1 in every coordinate
   * Rectangle(p1, p2)
   * </pre>
   *
   * @return <code>{lower, upper}</code> or {@link F#NIL} if the arguments don't match one of the
   *         forms above
   */
  public static IAST boxCorners(IAST reg) {
    int n = reg.head() == S.Rectangle ? 2 : 3;
    if (reg.argSize() == 0) {
      return F.list(constantVector(F.C0, n), constantVector(F.C1, n));
    }
    if (reg.argSize() == 1 && reg.arg1().isList() && reg.arg1().argSize() > 0) {
      IAST lower = (IAST) reg.arg1();
      IASTAppendable upper = F.ListAlloc(lower.argSize());
      for (int i = 1; i <= lower.argSize(); i++) {
        upper.append(F.Plus(lower.get(i), F.C1));
      }
      return F.list(lower, upper);
    }
    if (reg.argSize() == 2 && reg.arg1().isList() && reg.arg2().isList()
        && reg.arg1().argSize() == reg.arg2().argSize() && reg.arg1().argSize() > 0) {
      return F.list(reg.arg1(), reg.arg2());
    }
    return F.NIL;
  }

  // ---------------------------------------------------------------- Cone / Cylinder

  /**
   * The axis and the radius of a <code>Cone</code> or <code>Cylinder</code>. For a cone
   * <code>base</code> is the center of the base disk and <code>tip</code> the apex.
   */
  public static final class AxisSpec {
    public final IExpr base;
    public final IExpr tip;
    public final IExpr radius;

    private AxisSpec(IExpr base, IExpr tip, IExpr radius) {
      this.base = base;
      this.tip = tip;
      this.radius = radius;
    }
  }

  /**
   * Parse <code>Cone()</code> / <code>Cylinder()</code>, which are equivalent to
   * <code>Cone({{0,0,-1},{0,0,1}}, 1)</code>, and <code>Cone({p1, p2}, r)</code>.
   *
   * @return <code>null</code> if the arguments don't match one of those forms
   */
  public static AxisSpec parseAxisRegion(IAST reg) {
    IExpr base = F.List(F.C0, F.C0, F.CN1);
    IExpr tip = F.List(F.C0, F.C0, F.C1);
    IExpr radius = F.C1;
    if (reg.argSize() >= 1) {
      if (!reg.arg1().isList2() || !((IAST) reg.arg1()).arg1().isList()) {
        return null;
      }
      IAST points = (IAST) reg.arg1();
      base = points.arg1();
      tip = points.arg2();
      if (base.argSize() != tip.argSize()) {
        return null;
      }
    }
    if (reg.argSize() >= 2) {
      radius = reg.arg2();
    }
    if (reg.argSize() > 2) {
      return null;
    }
    return new AxisSpec(base, tip, radius);
  }

  // ---------------------------------------------------------------- annulus

  /** The center and the inner/outer radius of an <code>Annulus</code>. */
  public static final class AnnulusSpec {
    public final IExpr center;
    public final IExpr innerRadius;
    public final IExpr outerRadius;
    /** The angular extent in radians - <code>2*Pi</code> for a full annulus. */
    public final IExpr angle;

    private AnnulusSpec(IExpr center, IExpr innerRadius, IExpr outerRadius, IExpr angle) {
      this.center = center;
      this.innerRadius = innerRadius;
      this.outerRadius = outerRadius;
      this.angle = angle;
    }

    /** The annulus covers the full <code>2*Pi</code> turn. */
    public boolean isFull() {
      return angle.equals(F.C2Pi);
    }
  }

  /**
   * Parse the argument forms of an annulus.
   *
   * <pre>
   * Annulus()                            center {0,0}, radii {1,2}
   * Annulus(c, {rin, rout})
   * Annulus(c, {rin, rout}, {t1, t2})    only the sector between the two angles
   * </pre>
   *
   * @return <code>null</code> if the arguments don't match one of the forms above
   */
  public static AnnulusSpec parseAnnulus(IAST reg, EvalEngine engine) {
    IExpr center = F.CListC0C0;
    IExpr innerRadius = F.C1;
    IExpr outerRadius = F.C2;
    IExpr angle = F.C2Pi;
    if (reg.argSize() >= 1) {
      if (!reg.arg1().isList() || reg.arg1().argSize() < 2) {
        return null;
      }
      center = reg.arg1();
    }
    if (reg.argSize() >= 2) {
      if (!reg.arg2().isList2()) {
        return null;
      }
      innerRadius = reg.arg2().first();
      outerRadius = reg.arg2().second();
    }
    if (reg.argSize() == 3) {
      if (!reg.arg3().isList2()) {
        return null;
      }
      angle = engine.evaluate(F.Abs(F.Subtract(reg.arg3().second(), reg.arg3().first())));
    }
    if (reg.argSize() > 3) {
      return null;
    }
    return new AnnulusSpec(center, innerRadius, outerRadius, angle);
  }

  // ---------------------------------------------------------------- regular polygon

  /** The center, the circumradius and the number of vertices of a <code>RegularPolygon</code>. */
  public static final class RegularPolygonSpec {
    public final IExpr center;
    public final IExpr radius;
    public final IExpr n;

    private RegularPolygonSpec(IExpr center, IExpr radius, IExpr n) {
      this.center = center;
      this.radius = radius;
      this.n = n;
    }
  }

  /**
   * Parse the argument forms of a regular polygon.
   *
   * <pre>
   * RegularPolygon(n)                 center {0,0}, circumradius 1
   * RegularPolygon(r, n)
   * RegularPolygon({r, theta}, n)     rotated by theta
   * RegularPolygon(c, r, n)
   * RegularPolygon(c, {r, theta}, n)
   * </pre>
   *
   * The rotation angle does not change any of the measures, so it is accepted and ignored here.
   *
   * @return <code>null</code> if the arguments don't match one of the forms above
   */
  public static RegularPolygonSpec parseRegularPolygon(IAST reg) {
    IExpr center = F.CListC0C0;
    IExpr radius = F.C1;
    IExpr n;
    switch (reg.argSize()) {
      case 1:
        n = reg.arg1();
        break;
      case 2:
        radius = radiusOfRegularPolygon(reg.arg1());
        n = reg.arg2();
        break;
      case 3:
        center = reg.arg1();
        radius = radiusOfRegularPolygon(reg.arg2());
        n = reg.arg3();
        break;
      default:
        return null;
    }
    if (radius.isNIL()) {
      return null;
    }
    return new RegularPolygonSpec(center, radius, n);
  }

  /** The circumradius of a regular polygon, given either plainly or as <code>{r, theta}</code>. */
  private static IExpr radiusOfRegularPolygon(IExpr expr) {
    if (expr.isList2()) {
      return ((IAST) expr).arg1();
    }
    return expr.isList() ? F.NIL : expr;
  }

  // ---------------------------------------------------------------- polygons in space

  /**
   * The area of a planar polygon, which may be embedded in a space of any dimension. In the plane
   * this is the shoelace formula, in higher dimensions the norm of one half of the sum of the
   * successive cross products - which is the same value for a planar polygon.
   *
   * @return {@link F#NIL} if <code>points</code> is not a list of at least three coordinate vectors
   *         of equal length
   */
  public static IExpr polygonArea(IAST points, EvalEngine engine) {
    int n = points.argSize();
    if (n < 3 || !points.arg1().isList()) {
      return F.NIL;
    }
    int embeddingDimension = points.arg1().argSize();
    for (int i = 1; i <= n; i++) {
      if (!points.get(i).isList() || points.get(i).argSize() != embeddingDimension) {
        return F.NIL;
      }
    }
    if (embeddingDimension == 2) {
      // the shoelace formula gives an exact rational area for rational coordinates
      IASTAppendable sum = F.PlusAlloc(n);
      for (int i = 1; i <= n; i++) {
        IAST p1 = (IAST) points.get(i);
        IAST p2 = (IAST) points.get(i % n + 1);
        sum.append(F.Subtract(F.Times(p1.arg1(), p2.arg2()), F.Times(p2.arg1(), p1.arg2())));
      }
      return engine.evaluate(F.Times(F.C1D2, F.Abs(sum)));
    }
    if (embeddingDimension == 3) {
      // 1/2 * Norm(Sum(Cross(p_i, p_i+1)))
      IASTAppendable x = F.PlusAlloc(n);
      IASTAppendable y = F.PlusAlloc(n);
      IASTAppendable z = F.PlusAlloc(n);
      for (int i = 1; i <= n; i++) {
        IAST p1 = (IAST) points.get(i);
        IAST p2 = (IAST) points.get(i % n + 1);
        x.append(F.Subtract(F.Times(p1.arg2(), p2.arg3()), F.Times(p1.arg3(), p2.arg2())));
        y.append(F.Subtract(F.Times(p1.arg3(), p2.arg1()), F.Times(p1.arg1(), p2.arg3())));
        z.append(F.Subtract(F.Times(p1.arg1(), p2.arg2()), F.Times(p1.arg2(), p2.arg1())));
      }
      return engine.evaluate(F.Times(F.C1D2, F.Norm(F.list(x, y, z))));
    }
    return F.NIL;
  }

  /**
   * The sum of the distances between the successive vertices of a closed polygon.
   *
   * @return {@link F#NIL} if <code>points</code> is not a list of at least two coordinate vectors
   */
  public static IExpr polygonPerimeter(IAST points, EvalEngine engine) {
    int n = points.argSize();
    if (n < 2 || !points.arg1().isList()) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(n);
    for (int i = 1; i <= n; i++) {
      sum.append(F.Norm(F.Subtract(points.get(i), points.get(i % n + 1))));
    }
    return engine.evaluate(sum);
  }

  /**
   * The corner points of a region which is given either as an explicit list of vertices or - for
   * <code>Simplex(n)</code> and the box shaped regions - by a defining set of numbers.
   *
   * @return {@link F#NIL} if the vertices of <code>reg</code> are not available
   */
  public static IAST verticesOfSimplex(IAST reg) {
    if (reg.argSize() == 1) {
      if (reg.arg1().isList() && reg.arg1().argSize() > 0 && ((IAST) reg.arg1()).arg1().isList()) {
        return (IAST) reg.arg1();
      }
      int n = reg.arg1().toIntDefault();
      if (n > 0) {
        // the standard simplex: the origin and the n unit vectors of the n dimensional space
        IASTAppendable vertices = F.ListAlloc(n + 1);
        vertices.append(constantVector(F.C0, n));
        for (int i = 1; i <= n; i++) {
          IASTAppendable unit = F.ListAlloc(n);
          for (int j = 1; j <= n; j++) {
            unit.append(i == j ? F.C1 : F.C0);
          }
          vertices.append(unit);
        }
        return vertices;
      }
    } else if (reg.argSize() == 0) {
      // Simplex() is the standard triangle in the plane
      return F.list(F.CListC0C0, F.List(F.C1, F.C0), F.List(F.C0, F.C1));
    }
    return F.NIL;
  }
}
