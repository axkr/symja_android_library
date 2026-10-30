package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * PlanarAngle({q1, p, q2})
 *
 * PlanarAngle(p -&gt; {q1, q2})
 *
 * PlanarAngle(..., kind)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the angle at <code>p</code> in the triangle with the corner points <code>q1</code>,
 * <code>p</code> and <code>q2</code>, in the range <code>0</code> to <code>Pi</code>, or the
 * counterclockwise angle between the half-lines from <code>p</code> through <code>q1</code> and
 * <code>q2</code>.
 * </p>
 * <p>
 * The <code>kind</code> <code>"Interior"</code> is the angle in the range <code>0</code> to
 * <code>Pi</code>, <code>"Exterior"</code> is <code>Pi</code> minus it, and
 * <code>"Counterclockwise"</code> and <code>"Clockwise"</code> turn from the half-line through
 * <code>q1</code> to the one through <code>q2</code> in that direction.
 * </p>
 * </blockquote>
 */
public class PlanarAngle extends AbstractFunctionEvaluator {

  private static final int INTERIOR = 0;
  private static final int EXTERIOR = 1;
  private static final int COUNTERCLOCKWISE = 2;
  private static final int CLOCKWISE = 3;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int kind = -1;
    if (ast.isAST2()) {
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      switch (ast.arg2().toString()) {
        case "Interior":
          kind = INTERIOR;
          break;
        case "Exterior":
          kind = EXTERIOR;
          break;
        case "Counterclockwise":
          kind = COUNTERCLOCKWISE;
          break;
        case "Clockwise":
          kind = CLOCKWISE;
          break;
        default:
          return F.NIL;
      }
    }
    if (arg1.isRuleAST()) {
      // PlanarAngle(p -> {q1, q2}) is the counterclockwise angle between the two half-lines
      IExpr p = arg1.first();
      IExpr points = arg1.second();
      if (!isPoint(p) || !points.isList2()) {
        return F.NIL;
      }
      return angle(p, ((IAST) points).arg1(), ((IAST) points).arg2(),
          kind < 0 ? COUNTERCLOCKWISE : kind, engine);
    }
    if (arg1.isList3()) {
      // PlanarAngle({q1, p, q2}) is the interior angle at the middle point p
      IAST list = (IAST) arg1;
      return angle(list.arg2(), list.arg1(), list.arg3(), kind < 0 ? INTERIOR : kind, engine);
    }
    return F.NIL;
  }

  /** Only the two dimensional case is supported. */
  private static boolean isPoint(IExpr expr) {
    return expr.isList2() && !expr.first().isList();
  }

  /**
   * The angle between the vectors <code>q1-p</code> and <code>q2-p</code>.
   *
   * @param kind {@link #INTERIOR} for the unoriented angle in the range <code>0</code> to
   *        <code>Pi</code>, {@link #EXTERIOR} for <code>Pi</code> minus it, or
   *        {@link #COUNTERCLOCKWISE} and {@link #CLOCKWISE} for the angle turning from
   *        <code>q1</code> to <code>q2</code> in that direction, in the range <code>0</code> to
   *        <code>2*Pi</code>
   */
  private static IExpr angle(IExpr p, IExpr q1, IExpr q2, int kind, EvalEngine engine) {
    if (!isPoint(p) || !isPoint(q1) || !isPoint(q2)) {
      return F.NIL;
    }
    IExpr u = engine.evaluate(F.Subtract(q1, p));
    IExpr v = engine.evaluate(F.Subtract(q2, p));
    IExpr normU = engine.evaluate(F.Norm(u));
    IExpr normV = engine.evaluate(F.Norm(v));
    if (normU.isZero() || normV.isZero()) {
      // the angle at a degenerate corner is not defined
      return S.Indeterminate;
    }

    IExpr cosine = engine.evaluate(F.Divide(F.Dot(u, v), F.Times(normU, normV)));
    IExpr result = engine.evaluate(F.ArcCos(cosine));
    if (kind == INTERIOR) {
      return result;
    }
    if (kind == EXTERIOR) {
      return engine.evaluate(F.Subtract(S.Pi, result));
    }

    // the cross product decides on which side of q1 the ray through q2 lies
    IExpr cross = engine.evaluate(F.Subtract(//
        F.Times(((IAST) u).arg1(), ((IAST) v).arg2()), //
        F.Times(((IAST) u).arg2(), ((IAST) v).arg1())));
    // for a cross product of unknown sign the generic counterclockwise value is used
    boolean reflex = kind == COUNTERCLOCKWISE ? cross.isNegativeResult() : !cross.isNegativeResult();
    if (kind == CLOCKWISE && (cross.isZero() || result.isZero())) {
      // the two rays are collinear: no turn or a half turn either way
      reflex = false;
    }
    if (reflex) {
      return engine.evaluate(F.Subtract(F.C2Pi, result));
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
