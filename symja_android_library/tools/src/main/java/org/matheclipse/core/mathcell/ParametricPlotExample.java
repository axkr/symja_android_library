package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class ParametricPlotExample extends BasePlotExample {

	@Override
	public String exampleFunction() {
		return "ParametricPlot({Sin(t), Cos(t^2)}, {t, 0, 2*Pi})";
	}

	public static void main(String[] args) {
		ParametricPlotExample p = new ParametricPlotExample();
		p.generateHTML();
	}

}
