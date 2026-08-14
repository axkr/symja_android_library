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
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the angle at <code>p</code> in the triangle with the corner points <code>q1</code>,
 * <code>p</code> and <code>q2</code>, in the range <code>0</code> to <code>Pi</code>, or the
 * counterclockwise angle between the half-lines from <code>p</code> through <code>q1</code> and
 * <code>q2</code>.
 * </p>
 * </blockquote>
 */
public class PlanarAngle extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isRuleAST()) {
      // PlanarAngle(p -> {q1, q2}) is the counterclockwise angle between the two half-lines
      IExpr p = arg1.first();
      IExpr points = arg1.second();
      if (!isPoint(p) || !points.isList2()) {
        return F.NIL;
      }
      return angle(p, ((IAST) points).arg1(), ((IAST) points).arg2(), true, engine);
    }
    if (arg1.isList3()) {
      // PlanarAngle({q1, p, q2}) is the interior angle at the middle point p
      IAST list = (IAST) arg1;
      return angle(list.arg2(), list.arg1(), list.arg3(), false, engine);
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
   * @param counterclockwise if <code>true</code> the angle is measured counterclockwise from
   *        <code>q1</code> to <code>q2</code> and lies in the range <code>0</code> to
   *        <code>2*Pi</code>, otherwise it is the unoriented angle in the range <code>0</code> to
   *        <code>Pi</code>
   */
  private static IExpr angle(IExpr p, IExpr q1, IExpr q2, boolean counterclockwise,
      EvalEngine engine) {
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
    if (!counterclockwise) {
      return result;
    }

    // the cross product decides on which side of q1 the ray through q2 lies
    IExpr cross = engine.evaluate(F.Subtract(//
        F.Times(((IAST) u).arg1(), ((IAST) v).arg2()), //
        F.Times(((IAST) u).arg2(), ((IAST) v).arg1())));
    if (cross.isNegativeResult()) {
      return engine.evaluate(F.Subtract(F.C2Pi, result));
    }
    // for a cross product of unknown sign the generic counterclockwise value is used
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
