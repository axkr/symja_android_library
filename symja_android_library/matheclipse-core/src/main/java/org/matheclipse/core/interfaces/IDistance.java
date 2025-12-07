package org.matheclipse.core.interfaces;

import org.hipparchus.clustering.distance.DistanceMeasure;
import org.matheclipse.core.eval.EvalEngine;

public interface IDistance extends DistanceMeasure {

  IExpr distance(IExpr a, IExpr b, EvalEngine engine);

  IExpr numericFunctionDistance(IExpr a, IExpr b, EvalEngine engine);

  IExpr scalarDistance(INumber a, INumber b, EvalEngine engine);

}
