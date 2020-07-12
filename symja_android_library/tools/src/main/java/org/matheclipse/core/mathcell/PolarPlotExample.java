package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class PolarPlotExample extends BasePlotExample {

	@Override
	public String exampleFunction() {
		return "PolarPlot({t,Sin(2*t),Cos(t)}, {t, 0, 4*Pi} )";
		// return "PolarPlot(1 + 1/10*Sin(10*t), {t, 0, 2*Pi})";
		// return "PolarPlot(1-Cos(t), {t, 0, 2*Pi})";
	}

	public static void main(String[] args) {
		PolarPlotExample p = new PolarPlotExample();
		p.generateHTML();
	}

}
