package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class ListPlotExample extends BasePlotExample {

	@Override
	public String exampleFunction() {

		// return "ListPlot(Prime(Range(25)))";
		// return "ListPlot(Table({Sin(t), Cos(t)}, {t, 100}))";
		// return "Manipulate(ListPlot(Table({Sin(t*b), Cos(t*a)}, {t, 100})), {a,1,4,1}, {b,1,12})";
		return "ListPlot(<|2 -> 1, 3 -> 2, 5 -> 3, 7 -> 4, 11 -> 5, 13 -> 6|>)";
	}

	public static void main(String[] args) {
		ListPlotExample p = new ListPlotExample();
		p.generateHTML();
	}

}
