package org.matheclipse.core.reflection.system;

import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.TubeRings;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>KnotData(knot, "property")</code> - properties of the knots of the Rolfsen table.
 *
 * <p>
 * A knot is named by its entry <code>{n, k}</code> in the table of prime knots - the
 * <code>k</code>-th knot with <code>n</code> crossings, up to ten crossings, <code>{0, 1}</code>
 * being the unknot - by the standard name of one of the famous ones (<code>"Trefoil"</code>,
 * <code>"FigureEight"</code>, ...), or as a torus knot <code>{"TorusKnot", {p, q}}</code>, which
 * winds <code>p</code> times round a torus's axis and <code>q</code> times through its hole
 * (<code>p</code> and <code>q</code> coprime, or the curve closes up as several loops).
 *
 * <p>
 * What follows from the name alone - the crossing number, the Alexander-Briggs notation, the names
 * - is known for every knot of the table. A space curve, and the tube drawn round it, is known for
 * the trefoil, whose classic <code>{Sin(t) + 2 Sin(2 t), Cos(t) - 2 Cos(2 t), -Sin(3 t)}</code> is
 * the reference implementation's too, and for the torus knots, as the textbook parametrization on
 * a torus of major radius 2 and tube radius 1. The reference implementation's curves for the other
 * knots are interpolated from curated data, which is not bundled.
 *
 * <p>
 * An unknown knot is reported with <code>notent</code> and an unknown property with
 * <code>notprop</code>; both leave the call unevaluated, which is how {@link EntityValue} tells
 * them apart.
 */
public class KnotData extends AbstractEvaluator {
  /** The entity type these knots belong to: <code>Entity("Knot", name)</code>. */
  public static final String KNOT = "Knot";

  /** How many prime knots the table lists for each crossing number, the unknot included. */
  private static final int[][] TABLE_COUNTS =
      {{0, 1}, {3, 1}, {4, 1}, {5, 2}, {6, 3}, {7, 7}, {8, 21}, {9, 49}, {10, 165}};

  /** The knots with a standard name: the name, the table entry and the descriptive name. */
  private static final Object[][] NAMED_KNOTS = { //
      {"Unknot", 0, 1, "unknot"}, //
      {"Trefoil", 3, 1, "trefoil"}, //
      {"FigureEight", 4, 1, "figure eight knot"}, //
      {"SolomonSeal", 5, 1, "Solomon seal knot"}, //
      {"Stevedore", 6, 1, "Stevedore knot"}, //
      {"PerkoPair", 10, 161, "Perko pair"}};

  /** The table knots which are torus knots: <code>{n, k, p, q}</code>. */
  private static final int[][] TABLE_TORUS_KNOTS =
      {{3, 1, 2, 3}, {5, 1, 2, 5}, {7, 1, 2, 7}, {8, 19, 3, 4}, {9, 1, 2, 9}, {10, 124, 3, 5}};

  /** The properties a knot answers for, in the order <code>KnotData("Properties")</code> gives. */
  private static final String[] PROPERTIES = {"AlexanderBriggsList", "AlexanderBriggsNotation",
      "CrossingNumber", "ImageData", "Name", "SpaceCurve", "StandardName"};

  /** How many rings round the curve the tube of a knot is built from. */
  private static final int RINGS = 96;

  /** How many points each ring has. */
  private static final int RING_SIDES = 16;

  /** The radius of the tube, against the torus of major radius 2 the curve winds round. */
  private static final double TUBE_RADIUS = 0.25;

  /**
   * A knot: the table entry <code>{n, k}</code>, or <code>n = -1</code> for a torus knot off the
   * table; <code>p</code> and <code>q</code> are its torus winding numbers, or <code>0</code> when
   * it is no torus knot.
   */
  private static final class Knot {
    final int n;
    final int k;
    final int p;
    final int q;

    Knot(int n, int k, int p, int q) {
      this.n = n;
      this.k = k;
      this.p = p;
      this.q = q;
    }

    boolean inTable() {
      return n >= 0;
    }

    boolean isTrefoil() {
      return n == 3 && k == 1;
    }

    boolean hasCurve() {
      return isTrefoil() || p > 0;
    }

    Object[] named() {
      for (Object[] named : NAMED_KNOTS) {
        if ((Integer) named[1] == n && (Integer) named[2] == k) {
          return named;
        }
      }
      return null;
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST0()) {
      IASTAppendable names = F.ListAlloc(NAMED_KNOTS.length);
      for (Object[] named : NAMED_KNOTS) {
        names.append(F.stringx((String) named[0]));
      }
      return names;
    }
    if (ast.isAST1() && ast.arg1() == S.All) {
      IASTAppendable table = F.ListAlloc(250);
      for (int[] count : TABLE_COUNTS) {
        for (int k = 1; k <= count[1]; k++) {
          table.append(F.list(F.ZZ(count[0]), F.ZZ(k)));
        }
      }
      return table;
    }
    IExpr spec = ast.arg1();
    if (!spec.isList()) {
      spec = Entities.nameOf(spec, KNOT);
    }
    if (ast.isAST1() && spec.isString() && "Properties".equals(spec.toString())) {
      return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
    }
    Knot knot = resolve(spec);
    if (knot == null) {
      if (spec.isString() || spec.isList()) {
        // `1` is not a known entity, class or tag for `2`. Use `2`[] for a list of entities.
        Errors.printMessage(S.KnotData, "notent", F.List(spec, S.KnotData), engine);
      }
      return F.NIL;
    }
    if (ast.isAST1()) {
      return knot.hasCurve() ? picture(knot) : F.NIL;
    }
    IExpr propertySpec = Entities.propertyOf(ast.arg2(), KNOT);
    if (!propertySpec.isString()) {
      return F.NIL;
    }
    switch (propertySpec.toString()) {
      case "AlexanderBriggsList":
        return knot.inTable() ? F.list(F.ZZ(knot.n), F.ZZ(knot.k))
            : F.Missing(S.NotApplicable);
      case "AlexanderBriggsNotation":
        return knot.inTable() ? F.Subscript(F.ZZ(knot.n), F.ZZ(knot.k))
            : F.Missing(S.NotApplicable);
      case "CrossingNumber":
        // a theorem for torus knots: the smaller of p (q - 1) and q (p - 1)
        return knot.inTable() ? F.ZZ(knot.n)
            : F.ZZ(Math.min(knot.p * (knot.q - 1), knot.q * (knot.p - 1)));
      case "StandardName": {
        if (!knot.inTable()) {
          return F.list(F.stringx("TorusKnot"), F.list(F.ZZ(knot.p), F.ZZ(knot.q)));
        }
        Object[] named = knot.named();
        return named != null ? F.stringx((String) named[0])
            : F.list(F.stringx("Knot"), F.list(F.ZZ(knot.n), F.ZZ(knot.k)));
      }
      case "Name": {
        if (!knot.inTable()) {
          return F.stringx("(" + knot.p + "," + knot.q + ")-torus knot");
        }
        Object[] named = knot.named();
        return F.stringx(named != null ? (String) named[3] : "knot " + knot.n + "-" + knot.k);
      }
      case "SpaceCurve":
        return knot.hasCurve() ? spaceCurve(knot) : F.NIL;
      case "ImageData":
        return knot.hasCurve() ? imageData(knot) : F.NIL;
      default:
        // `1` is not a known property or size specification for `2`.
        return Errors.printMessage(S.KnotData, "notprop", F.List(propertySpec, S.KnotData),
            engine);
    }
  }

  /** The knot a standard name, a table entry or a torus knot specification stands for. */
  private static Knot resolve(IExpr spec) {
    if (spec.isString()) {
      String name = spec.toString();
      for (Object[] named : NAMED_KNOTS) {
        if (named[0].equals(name)) {
          return tableKnot((Integer) named[1], (Integer) named[2]);
        }
      }
      return null;
    }
    if (!spec.isList2()) {
      return null;
    }
    if (spec.first().isInteger() && spec.second().isInteger()) {
      int n = spec.first().toIntDefault();
      int k = spec.second().toIntDefault();
      for (int[] count : TABLE_COUNTS) {
        if (count[0] == n && k >= 1 && k <= count[1]) {
          return tableKnot(n, k);
        }
      }
      return null;
    }
    if (spec.first().isString() && "TorusKnot".equals(spec.first().toString())
        && spec.second().isList2()) {
      int p = spec.second().first().toIntDefault();
      int q = spec.second().second().toIntDefault();
      if (p < 1 || q < 1 || gcd(p, q) != 1) {
        // not coprime, the curve is several loops and no knot
        return null;
      }
      return new Knot(-1, 0, p, q);
    }
    return null;
  }

  private static Knot tableKnot(int n, int k) {
    for (int[] torus : TABLE_TORUS_KNOTS) {
      if (torus[0] == n && torus[1] == k) {
        return new Knot(n, k, torus[2], torus[3]);
      }
    }
    return new Knot(n, k, 0, 0);
  }

  private static int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
  }

  /** The space curve as a pure function, <code>{x(#1), y(#1), z(#1)}&</code>, one turn to 2 Pi. */
  private static IExpr spaceCurve(Knot knot) {
    IExpr t = F.Slot1;
    if (knot.isTrefoil()) {
      return F.Function(F.list(F.Plus(F.Sin(t), F.Times(F.C2, F.Sin(F.Times(F.C2, t)))),
          F.Subtract(F.Cos(t), F.Times(F.C2, F.Cos(F.Times(F.C2, t)))),
          F.Negate(F.Sin(F.Times(F.C3, t)))));
    }
    IExpr ring = F.Plus(F.C2, F.Cos(F.Times(F.ZZ(knot.q), t)));
    return F.Function(F.list(F.Times(ring, F.Cos(F.Times(F.ZZ(knot.p), t))),
        F.Times(ring, F.Sin(F.Times(F.ZZ(knot.p), t))), F.Sin(F.Times(F.ZZ(knot.q), t))));
  }

  /** The point of the space curve at <code>t</code>, the same curve {@link #spaceCurve} gives. */
  private static double[] curvePoint(Knot knot, double t) {
    if (knot.isTrefoil()) {
      return new double[] {Math.sin(t) + 2.0 * Math.sin(2.0 * t),
          Math.cos(t) - 2.0 * Math.cos(2.0 * t), -Math.sin(3.0 * t)};
    }
    double ring = 2.0 + Math.cos(knot.q * t);
    return new double[] {ring * Math.cos(knot.p * t), ring * Math.sin(knot.p * t),
        Math.sin(knot.q * t)};
  }

  /** The knot drawn as its tube, the surface of {@link #imageData(Knot)}. */
  private static IExpr picture(Knot knot) {
    return F.Graphics3D(F.List(F.EdgeForm(S.None), imageData(knot).first()),
        F.Rule(S.Boxed, S.False));
  }

  /**
   * <code>{GraphicsComplex(points, Polygon(quads), VertexNormals -> normals)}</code>: the tube round
   * the space curve, as one closed surface. The points go ring by ring, each ring centred on the
   * curve at <code>t = 2 Pi k / 96</code>.
   */
  private static IAST imageData(Knot knot) {
    double[][] path = new double[RINGS][];
    for (int k = 0; k < RINGS; k++) {
      path[k] = curvePoint(knot, 2.0 * Math.PI * k / RINGS);
    }
    TubeRings.Rings rings = TubeRings.of(path, TUBE_RADIUS, RING_SIDES, true);
    IASTAppendable points = F.ListAlloc(RINGS * RING_SIDES);
    IASTAppendable normals = F.ListAlloc(RINGS * RING_SIDES);
    for (int i = 0; i < RINGS; i++) {
      for (int j = 0; j < RING_SIDES; j++) {
        double[] point = rings.points[i][j];
        double[] normal = rings.normals[i][j];
        points.append(F.List(F.num(point[0]), F.num(point[1]), F.num(point[2])));
        normals.append(F.List(F.num(normal[0]), F.num(normal[1]), F.num(normal[2])));
      }
    }
    IASTAppendable quads = F.ListAlloc(RINGS * RING_SIDES);
    for (int i = 0; i < RINGS; i++) {
      int next = (i + 1) % RINGS;
      for (int j = 0; j < RING_SIDES; j++) {
        int around = (j + 1) % RING_SIDES;
        quads.append(F.List(F.ZZ(i * RING_SIDES + j + 1), F.ZZ(next * RING_SIDES + j + 1),
            F.ZZ(next * RING_SIDES + around + 1), F.ZZ(i * RING_SIDES + around + 1)));
      }
    }
    return F.List(F.ternaryAST3(S.GraphicsComplex, points, F.unaryAST1(S.Polygon, quads),
        F.Rule(S.VertexNormals, normals)));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_0_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
