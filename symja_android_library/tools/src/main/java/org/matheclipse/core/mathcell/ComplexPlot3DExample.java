package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class ComplexPlot3DExample extends BasePlotExample {
	@Override
	public String exampleFunction() {
		return "ComplexPlot3D(Gamma(z), {z, -4.999-4.9999*I,4.9999+4.9999*I}, PlotRange->{0,8.0}) ";
//		return "ComplexPlot3D((z^2 + 1)/(z^2 - 1),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3}) ";
	}

	public static void main(String[] args) {
		ComplexPlot3DExample p = new ComplexPlot3DExample();
		p.generateHTML();
	}
}
