package org.matheclipse.core.interfaces.statistics;

import org.hipparchus.distribution.RealDistribution;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Any distribution for which an analytic expression of the mean exists should implement
 * {@link IDistribution}.
 *
 * <p>
 * The function is used in {@link S#Expectation} to provide the mean of a given
 * {@link IDistribution}.
 */
public interface IDistribution {
  default RealDistribution dist() {
    return null;
  }

  /**
   * Test if the parameters are consistent.
   *
   * @param distribution
   * @return the distribution or otherwise {@link F#NIL} if the parameters are not consistent
   */
  default IAST checkParameters(IAST distribution) {
    return distribution;
  }

  IExpr mean(IAST distribution);

  IExpr median(IAST distribution);
}
