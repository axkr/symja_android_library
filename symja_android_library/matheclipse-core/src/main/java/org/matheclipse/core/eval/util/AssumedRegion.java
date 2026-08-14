package org.matheclipse.core.eval.util;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Bound an expression over the region which the assumptions describe.
 *
 * <p>
 * This eliminates the quantifier of a statement like &ldquo;<code>q(v) &lt; b</code> for every
 * <code>v</code> of the assumed region&rdquo; by computing the range of <code>q</code> over that
 * region and comparing the range against <code>b</code>. The range is returned as an
 * {@link S#IntervalData}, so every caller which can already interpret an interval &ndash; the
 * relational operators, {@link S#Positive}, {@link S#Sign}, {@link S#Abs}, ... &ndash; profits from
 * it without further work.
 *
 * <p>
 * Bounding each variable on its own is not enough for this, because it loses the correlation
 * between them: the assumption <code>x^2+y^2 &lt;= 1</code> gives <code>x</code> and <code>y</code>
 * in <code>[-1,1]</code>, and the resulting bound <code>(x-1)^2+(y-2)^2 &gt;= 1</code> is far below
 * the true minimum <code>6-2*Sqrt(5)</code>. The region has to be treated as a whole.
 *
 * <p>
 * <b>Implemented case.</b> A target which is a scaled squared distance
 * <code>alpha*|v-p|^2 + k</code> and a region which is a ball <code>|v-m|^2 &lt;= r^2</code>. Both
 * are recognized by {@link QuadraticForm#identityFactor()}. The extrema then only depend on the
 * distance <code>|p-m|</code> of the two centers, because the nearest and the farthest point of a
 * ball lie on the line through both centers:
 *
 * <pre>
 * dmin == Max(0, |p-m| - r)      dmax == |p-m| + r
 * </pre>
 *
 * <p>
 * Further cases can be added to {@link #rangeOf(IExpr, EvalEngine)} without touching the callers.
 * An ellipsoid region and a general quadratic target need the trust region subproblem, i.e. an
 * eigendecomposition of the quadratic part together with a root of the secular equation.
 */
public final class AssumedRegion {

  /** Maximum number of variables for which a region is built. */
  private static final int MAX_VARIABLES = 4;

  /**
   * A ball <code>|v-center|^2 &lt;= radiusSquared</code>.
   */
  private static final class Ball {
    final IExpr[] center;
    final IExpr radiusSquared;

    Ball(IExpr[] center, IExpr radiusSquared) {
      this.center = center;
      this.radiusSquared = radiusSquared;
    }
  }

  /**
   * Read a ball from an assumption <code>g(v) &lt;= upperBound</code> in the given variables.
   *
   * <p>
   * With <code>g(v) == beta*|v-m|^2 + k</code> and <code>beta &gt; 0</code> the assumption is the
   * ball <code>|v-m|^2 &lt;= (upperBound-k)/beta</code>.
   *
   * @return <code>null</code> if the assumption doesn't describe a ball in the
   *         <code>variables</code>
   */
  private static Ball ballOf(IExpr key, IAST variables, EvalEngine engine) {
    IAST interval = engine.getAssumptions().intervalData(key);
    if (interval.isNIL() || interval.argSize() != 1) {
      return null;
    }
    IExpr subInterval = interval.first();
    if (!subInterval.isList4()) {
      return null;
    }
    IExpr upperBound = ((IAST) subInterval).arg4();
    if (!upperBound.isNumber()) {
      return null;
    }
    QuadraticForm form = QuadraticForm.of(key, variables, engine);
    if (form == null) {
      return null;
    }
    IExpr beta = form.identityFactor();
    if (beta.isNIL() || !beta.isPositiveResult()) {
      return null;
    }
    IExpr radiusSquared =
        engine.evaluate(F.Divide(F.Subtract(upperBound, form.offset(beta, engine)), beta));
    if (!radiusSquared.isNonNegativeResult()) {
      // an empty or undetermined region
      return null;
    }
    return new Ball(form.center(beta, engine), radiusSquared);
  }

  /**
   * Compute an interval enclosure of <code>expr</code> over the region which the assumptions
   * describe.
   *
   * @param expr
   * @param engine
   * @return an {@link S#IntervalData} enclosure or {@link F#NIL} if no bound could be determined
   */
  public static IAST rangeOf(IExpr expr, EvalEngine engine) {
    IAssumptions assumptions = engine.getAssumptions();
    if (assumptions == null) {
      return F.NIL;
    }
    IAST relationalKeys = assumptions.relationalKeys();
    if (relationalKeys.argSize() == 0) {
      return F.NIL;
    }
    IAST variables = new VariablesSet(expr).getVarList();
    final int n = variables.argSize();
    if (n < 1 || n > MAX_VARIABLES) {
      return F.NIL;
    }
    QuadraticForm target = QuadraticForm.of(expr, variables, engine);
    if (target == null) {
      return F.NIL;
    }
    IExpr alpha = target.identityFactor();
    if (alpha.isNIL()) {
      return F.NIL;
    }
    for (int i = 1; i < relationalKeys.size(); i++) {
      IExpr key = relationalKeys.get(i);
      if (key.equals(expr) || !variables.equals(new VariablesSet(key).getVarList())) {
        // the region has to constrain exactly the variables of the target
        continue;
      }
      Ball ball = ballOf(key, variables, engine);
      if (ball != null) {
        IAST range = rangeOverBall(target, alpha, ball, engine);
        if (range.isPresent()) {
          return range;
        }
      }
    }
    return F.NIL;
  }

  /**
   * The range of the scaled squared distance <code>alpha*|v-p|^2 + k</code> over the
   * <code>ball</code>.
   */
  private static IAST rangeOverBall(QuadraticForm target, IExpr alpha, Ball ball,
      EvalEngine engine) {
    final int n = target.size();
    final IExpr[] center = target.center(alpha, engine);
    IASTAppendable centerDistanceSquared = F.PlusAlloc(n);
    for (int i = 0; i < n; i++) {
      centerDistanceSquared.append(F.Sqr(F.Subtract(center[i], ball.center[i])));
    }
    IExpr centerDistance = engine.evaluate(F.Sqrt(centerDistanceSquared));
    IExpr radius = engine.evaluate(F.Sqrt(ball.radiusSquared));
    // the nearest and the farthest point of a ball lie on the line through both centers
    IExpr minDistance = engine.evaluate(F.Max(F.C0, F.Subtract(centerDistance, radius)));
    IExpr maxDistance = engine.evaluate(F.Plus(centerDistance, radius));

    IExpr offset = target.offset(alpha, engine);
    IExpr atMinDistance = engine.evaluate(F.Plus(F.Times(alpha, F.Sqr(minDistance)), offset));
    IExpr atMaxDistance = engine.evaluate(F.Plus(F.Times(alpha, F.Sqr(maxDistance)), offset));
    if (!atMinDistance.isRealResult() || !atMaxDistance.isRealResult()) {
      return F.NIL;
    }
    // a negative alpha turns the nearest point into the maximum
    IExpr min = alpha.isNegativeResult() ? atMaxDistance : atMinDistance;
    IExpr max = alpha.isNegativeResult() ? atMinDistance : atMaxDistance;
    return IntervalDataSym.close(min, max);
  }

  private AssumedRegion() {}
}
