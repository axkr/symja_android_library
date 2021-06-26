package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class ComplexPlot3DExample extends BasePlotExample {
  @Override
  public String exampleFunction() {
    //	  return "ComplexPlot3D(InverseHaversine(z),  {z, -1/3-I, 1/3+I}) ";
    return "ComplexPlot3D(Haversine(z),  {z, -2*Pi - 2*I, 2*Pi + 2*I} ) ";
    //    return "ComplexPlot3D(StruveH(2, z), {z, -5-5*I,5+5*I}, PlotRange->{-5.0,10.0}) ";
    //    		return "ComplexPlot3D((z^2 + 1)/(z^2 - 1),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3}) ";
    // 	  return "ComplexPlot3D((z^4-1)/(z^2),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,10}) ";  //
  }

  public static void main(String[] args) {
    ComplexPlot3DExample p = new ComplexPlot3DExample();
    p.generateHTML();
  }
}
