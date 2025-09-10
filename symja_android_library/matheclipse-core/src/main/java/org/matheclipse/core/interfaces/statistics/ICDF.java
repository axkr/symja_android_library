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

}
