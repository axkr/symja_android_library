package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class MatrixPlotExample extends BasePlotExample {

	@Override
	public String exampleFunction() { 
		return "MatrixPlot[{{4, 2, 1}, {3, 0, -2}, {0, 0, -1}}]";
	}

	public static void main(String[] args) {
		MatrixPlotExample p = new MatrixPlotExample();
		p.generateHTML();
	}

}
