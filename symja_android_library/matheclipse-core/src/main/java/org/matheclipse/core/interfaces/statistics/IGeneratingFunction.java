package org.matheclipse.core.interfaces.statistics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Generating functions of a probability distribution.
 *
 * <p>
 * Discrete distributions implement {@link #pgf(IAST, IExpr, EvalEngine)} - the probability
 * (factorial moment) generating function <code>E[z^X]</code>. Continuous distributions implement
 * {@link #mgf(IAST, IExpr, EvalEngine)} - the moment generating function <code>E[E^(t*X)]</code>.
 * {@link #cf(IAST, IExpr, EvalEngine)} is only overridden by distributions whose characteristic
 * function has a closed form although the moment generating function does not exist (e.g.
 * <code>CauchyDistribution</code>, <code>StudentTDistribution</code>).
 *
 * <p>
 * All conversions between the generating functions (and the fallbacks for missing implementations)
 * are performed by the <code>MomentGeneratingFunction</code>, <code>CharacteristicFunction</code>,
 * <code>FactorialMomentGeneratingFunction</code>, <code>CentralMomentGeneratingFunction</code> and
 * <code>CumulantGeneratingFunction</code> evaluators in <code>StatisticalMomentFunctions</code> -
 * the interface methods must not call each other.
 *
 * <p>
 * The returned expressions are formal closed forms without convergence conditions.
 */
public interface IGeneratingFunction extends IDistribution {

  /**
   * Moment generating function <code>E[E^(t*X)]</code> of the distribution.
   *
   * @return {@link F#NIL} if no closed form is implemented
   */
  default IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
    return F.NIL;
  }

  /**
   * Probability (factorial moment) generating function <code>E[z^X]</code> of the distribution.
   *
   * @return {@link F#NIL} if no closed form is implemented
   */
  default IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
    return F.NIL;
  }

  /**
   * Characteristic function <code>E[E^(I*t*X)]</code>. Only implemented by distributions where the
   * characteristic function exists although the moment generating function does not; for all other
   * distributions the evaluator derives it from {@link #mgf(IAST, IExpr, EvalEngine)} or
   * {@link #pgf(IAST, IExpr, EvalEngine)}.
   *
   * @return {@link F#NIL} if no dedicated closed form is implemented
   */
  default IExpr cf(IAST dist, IExpr t, EvalEngine engine) {
    return F.NIL;
  }
}
