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

  /**
   * The <code>n</code>-th raw moment <code>E[X^n]</code> of the distribution.
   *
   * <p>
   * The default derives the first three raw moments from {@link #mean(IAST)},
   * {@link #variance(IAST)} and {@link #skewness(IAST)}, so a distribution only has to override
   * this for a general <code>n</code> or for a form which is simpler than the generic one.
   *
   * @return {@link F#NIL} if no closed form is implemented
   */
  default IExpr moment(IAST distribution, IExpr n) {
    switch (n.toIntDefault()) {
      case 1:
        return mean(distribution);
      case 2: {
        IExpr mean = mean(distribution);
        IExpr variance = variance(distribution);
        if (mean.isPresent() && variance.isPresent()) {
          // E[X^2] == Variance + Mean^2
          return F.Plus(variance, F.Sqr(mean));
        }
        break;
      }
      case 3: {
        IExpr mean = mean(distribution);
        IExpr variance = variance(distribution);
        IExpr skewness = skewness(distribution);
        if (mean.isPresent() && variance.isPresent() && skewness.isPresent()) {
          // E[X^3] == Skewness*Variance^(3/2) + 3*Mean*Variance + Mean^3
          return F.Plus(F.Times(skewness, F.Power(variance, F.QQ(3L, 2L))),
              F.Times(F.C3, mean, variance), F.Power(mean, F.C3));
        }
        break;
      }
      default:
        break;
    }
    return F.NIL;
  }

  /** @return standard deviation of distribution */
  default IExpr standardDeviation(IAST distribution) {
    IExpr variance = variance(distribution);
    return variance.isPresent() ? F.Sqrt(variance) : F.NIL;
  }
}
