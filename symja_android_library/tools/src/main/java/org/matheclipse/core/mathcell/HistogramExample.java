package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class HistogramExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Histogram({1, 2, 3, None, 3, 5, f(), 2, 1, 5,4,3,2,foo, 2, 3})";
    // return "Histogram(RandomVariate(NormalDistribution(0, 1), 200))";
  }

  public static void main(String[] args) {
    HistogramExample p = new HistogramExample();
    p.generateHTML();
  }
}
