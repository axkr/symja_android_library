package org.matheclipse.core.reflection.system;

import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>KnotData(knot, "property")</code> - properties of a torus knot.
 *
 * <p>
 * A torus knot lies on the surface of a torus, winding <code>p</code> times round its axis and
 * <code>q</code> times through its hole; <code>p</code> and <code>q</code> have to be coprime, or
 * the curve closes up as several loops rather than one knot. The named knots here are all torus
 * knots, and every other one is reached as <code>{"TorusKnot", {p, q}}</code>.
 *
 * <p>
 * The space curve is the textbook parametrization on a torus of major radius 2 and tube radius 1,
 * <code>{(2 + cos(q t)) cos(p t), (2 + cos(q t)) sin(p t), sin(q t)}</code>. It is the same knot as
 * the reference implementation's, not the same curve: those coefficients are its own.
 *
 * <p>
 * A knot or a property this table does not know leaves the call unevaluated, which is how
 * {@link EntityValue} tells the two apart; an unknown property also says so.
 */
public class KnotData extends AbstractEvaluator {

  /** The entity type these knots belong to: <code>Entity("Knot", name)</code>. */
  public static final String KNOT = "Knot";

  /** The named knots: the name, its aliases, the Alexander-Briggs notation, and p and q. */
  private static final Object[][] KNOTS = { //
      {"CinquefoilKnot", new String[] {"Cinquefoil", "SolomonsSealKnot", "5_1"}, "5_1", 2, 5},
      {"SeptafoilKnot", new String[] {"Septafoil", "7_1"}, "7_1", 2, 7},
      {"Trefoil", new String[] {"TrefoilKnot", "3_1"}, "3_1", 2, 3}};

  /** The properties a knot answers for, in the order <code>KnotData("Properties")</code> gives. */
  private static final String[] PROPERTIES =
      {"AlexanderBriggsNotation", "CrossingNumber", "SpaceCurve"};

  /** How many points of the curve the picture of a knot joins. */
  private static final int PICTURE_POINTS = 300;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST0() || (ast.isAST1() && ast.arg1() == S.All)) {
      IASTAppendable names = F.ListAlloc(KNOTS.length);
      for (Object[] knot : KNOTS) {
        names.append(F.stringx((String) knot[0]));
      }
      return names;
    }
    IExpr spec = ast.arg1();
    if (!spec.isList()) {
      spec = Entities.nameOf(spec, KNOT);
    }
    if (ast.isAST1() && spec.isString() && "Properties".equals(spec.toString())) {
      return strings(PROPERTIES);
    }
    Object[] knot = resolve(spec);
    if (knot == null) {
      return F.NIL;
    }
    int p = (Integer) knot[3];
    int q = (Integer) knot[4];
    if (ast.isAST1()) {
      return picture(p, q, engine);
    }
    IExpr propertySpec = Entities.propertyOf(ast.arg2(), KNOT);
    if (!propertySpec.isString()) {
      return F.NIL;
    }
    switch (propertySpec.toString()) {
      case "AlexanderBriggsNotation":
        return knot[2] == null ? F.Missing(S.NotAvailable) : F.stringx((String) knot[2]);
      case "CrossingNumber":
        // a theorem for torus knots: the smaller of p (q - 1) and q (p - 1)
        return F.ZZ(Math.min(p * (q - 1), q * (p - 1)));
      case "SpaceCurve":
        return spaceCurve(p, q);
      default:
        // `1` is not a known property or size specification for `2`.
        return Errors.printMessage(S.KnotData, "notprop", F.List(propertySpec, S.KnotData),
            engine);
    }
  }

  /**
   * The knot a name or a <code>{"TorusKnot", {p, q}}</code> specification stands for, as
   * <code>{name, aliases, notation, p, q}</code>, or <code>null</code>.
   */
  private static Object[] resolve(IExpr spec) {
    if (spec.isString()) {
      String name = spec.toString();
      for (Object[] knot : KNOTS) {
        if (knot[0].equals(name)) {
          return knot;
        }
        for (String alias : (String[]) knot[1]) {
          if (alias.equals(name)) {
            return knot;
          }
        }
      }
      return null;
    }
    if (spec.isList2() && spec.first().isString() && "TorusKnot".equals(spec.first().toString())
        && spec.second().isList2()) {
      int p = spec.second().first().toIntDefault();
      int q = spec.second().second().toIntDefault();
      if (p < 1 || q < 1 || gcd(p, q) != 1) {
        // not coprime, the curve is several loops and no knot
        return null;
      }
      for (Object[] knot : KNOTS) {
        if ((Integer) knot[3] == p && (Integer) knot[4] == q) {
          return knot;
        }
      }
      return new Object[] {null, new String[0], null, p, q};
    }
    return null;
  }

  private static int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
  }

  /** <code>Function({t}, {x(t), y(t), z(t)})</code>, one turn of the knot as t runs to 2 Pi. */
  private static IExpr spaceCurve(int p, int q) {
    ISymbol t = F.Dummy("t");
    IExpr ring = F.Plus(F.C2, F.Cos(F.Times(F.ZZ(q), t)));
    IAST curve = F.List(F.Times(ring, F.Cos(F.Times(F.ZZ(p), t))),
        F.Times(ring, F.Sin(F.Times(F.ZZ(p), t))), F.Sin(F.Times(F.ZZ(q), t)));
    return F.Function(F.List(t), curve);
  }

  /** The knot drawn as a tube along its curve. */
  private static IExpr picture(int p, int q, EvalEngine engine) {
    IASTAppendable points = F.ListAlloc(PICTURE_POINTS + 1);
    for (int i = 0; i <= PICTURE_POINTS; i++) {
      double t = 2.0 * Math.PI * i / PICTURE_POINTS;
      double ring = 2.0 + Math.cos(q * t);
      points.append(F.List(F.num(ring * Math.cos(p * t)), F.num(ring * Math.sin(p * t)),
          F.num(Math.sin(q * t))));
    }
    return F.Graphics3D(F.List(F.binaryAST2(S.Tube, points, F.num(0.25))),
        F.Rule(S.Boxed, S.False));
  }

  private static IAST strings(String[] names) {
    return F.mapRange(0, names.length, i -> F.stringx(names[i]));
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
