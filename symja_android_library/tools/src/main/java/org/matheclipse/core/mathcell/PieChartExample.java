package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class PieChartExample extends BasePlotExample {

	@Override
	public String exampleFunction() { 
		return "PieChart({25, 33, 33,10})";
	}

	public static void main(String[] args) {
		PieChartExample p = new PieChartExample();
		p.generateHTML();
	}

}
