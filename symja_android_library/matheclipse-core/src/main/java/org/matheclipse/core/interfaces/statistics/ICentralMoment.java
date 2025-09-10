package org.matheclipse.core.interfaces.statistics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** central moment function */
public interface ICentralMoment extends IDistribution {
  IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine);

  IExpr kurtosis(IAST dist, EvalEngine engine);
}
