package org.matheclipse.core.interfaces.statistics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Cumulative distribution function
 *
 */
public interface ICovariance extends IDistribution {
  public IExpr covariance(IAST dist, EvalEngine engine);
}
