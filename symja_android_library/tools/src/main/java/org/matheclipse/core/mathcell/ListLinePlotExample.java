package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class ListLinePlotExample  extends BasePlotExample {

	@Override
	public String exampleFunction() {
		return "Manipulate(ListLinePlot(Table({Sin(t), Cos(t*a)}, {t, 50})), {a,1,4,1})";
	}

	public static void main(String[] args) {
		ListLinePlotExample p = new ListLinePlotExample();
		p.generateHTML();
	}
}
