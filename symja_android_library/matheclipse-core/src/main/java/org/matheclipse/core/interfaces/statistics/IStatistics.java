package org.matheclipse.core.interfaces.statistics;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Any distribution for which an analytic expression of the variance exists should implement
 * {@link IStatistics}.
 *
 * <p>
 * The function is used in {@link Expectation} to provide the variance of a given
 * {@link IDistribution}.
 */
public interface IStatistics {
  /** @return mean of distribution */
  IExpr mean(IAST dist);

  /** @return variance of distribution */
  IExpr variance(IAST distribution);

  /** @return skewness of distribution */
  IExpr skewness(IAST distribution);

  /** @return standard deviation of distribution */
  default IExpr standardDeviation(IAST distribution) {
    IExpr variance = variance(distribution);
    return variance.isPresent() ? F.Sqrt(variance) : F.NIL;
  }
}