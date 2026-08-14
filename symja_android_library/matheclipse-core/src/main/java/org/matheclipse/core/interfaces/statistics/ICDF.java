package org.matheclipse.core.interfaces.statistics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Cumulative distribution function
 *
 */
public interface ICDF extends IDistribution {
  static final IExpr CDF_NUMERIC_THRESHOLD = F.num(1e-14);

  public IExpr cdf(IAST dist, IExpr x, EvalEngine engine);

  public IExpr inverseCDF(IAST dist, IExpr x, EvalEngine engine);

  /**
   * The survival function <code>1 - CDF(dist, x)</code>.
   *
   * <p>
   * Implement this only if the distribution has a closed form which is simpler than the literal
   * <code>1 - CDF(dist, x)</code>; otherwise
   * {@link org.matheclipse.core.builtin.StatisticsFunctions} falls back to that difference.
   *
   * @return {@link F#NIL} if no dedicated closed form is available
   */
  default IExpr survivalFunction(IAST dist, IExpr x, EvalEngine engine) {
    return F.NIL;
  }

}
