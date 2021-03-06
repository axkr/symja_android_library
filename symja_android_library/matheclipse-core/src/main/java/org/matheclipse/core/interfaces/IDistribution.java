package org.matheclipse.core.interfaces;

import org.hipparchus.distribution.RealDistribution;

/**
 * Any distribution for which an analytic expression of the mean exists should implement {@link
 * IDistribution}.
 *
 * <p>The function is used in {@link Expectation} to provide the mean of a given {@link
 * IDistribution}.
 */
public interface IDistribution {
  default RealDistribution dist() {
    return null;
  }

  IExpr mean(IAST distribution);

  IExpr median(IAST distribution);
}
