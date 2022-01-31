package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class PiecewisePlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Plot(x^2+Piecewise({{x, 0 < x < 1}, {x^3, 1 < x < 2}}), {x, -5, 5})";
    // return "Plot( Piecewise({{x^2, x < 0}, {x, x >= 0&&x<1},{Cos(x-1), x >= 1}}) , {x, -2, 12})";
  }

  public static void main(String[] args) {
    PiecewisePlotExample p = new PiecewisePlotExample();
    p.generateHTML();
  }
}
