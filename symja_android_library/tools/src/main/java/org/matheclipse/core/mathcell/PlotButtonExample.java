package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class PlotButtonExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Manipulate(Plot(f(x), {x, 0, 2*Pi}), {f, {Sin, Cos, Tan, Cot}})";
  }

  public static void main(String[] args) {
    PlotButtonExample p = new PlotButtonExample();
    p.generateHTML();
  }
}
