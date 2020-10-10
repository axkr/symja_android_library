package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class ContourPlotExample extends BasePlotExample {

	@Override
	public String exampleFunction() { 
		return "ContourPlot(Cos(x) + Cos(y), {x, 0, 4*Pi}, {y, 0, 4*Pi})";
	}

	public static void main(String[] args) {
		ContourPlotExample p = new ContourPlotExample();
		p.generateHTML();
	}
}
