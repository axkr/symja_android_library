package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class Plot3DExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    // return "Plot3D(ArcTan(x, y), {x, -7, 7}, {y, -10, 10})";
    return "Plot3D(Sin(x*y), {x,0, 5}, {y, 0, 5}, ColorFunction->\"Rainbow\")";
    // return "Manipulate(Plot3D(Sin(a*x*y), {x, -1.5, 1.5}, {y, -1.5, 1.5}), {a,1,5})";
    // return "Manipulate(Plot3D(Sin(a*x*y), {x,-1.5, 1.5}, {y, -1.5, 1.5},
    // ColorFunction->\"Rainbow\"), {a,1,5})";
  }

  public static void main(String[] args) {
    Plot3DExample p = new Plot3DExample();
    p.generateHTML();
  }
}
