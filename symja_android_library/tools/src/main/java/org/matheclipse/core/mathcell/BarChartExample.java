package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class BarChartExample extends BasePlotExample {

	@Override
	public String exampleFunction() {  
		return "BarChart({1, -2, 3}, BarOrigin -> Right)";
	}

	public static void main(String[] args) {
		BarChartExample p = new BarChartExample();
		p.generateHTML();
	}

}
