package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class FormulaExample extends BasePlotExample {

	@Override
	public String exampleFunction() {
		return "Manipulate(Factor(x^n + 1), {n, 1, 5, 1})";
	}

	public static void main(String[] args) {
		FormulaExample p = new FormulaExample();
		p.generateHTML();
	}
 
}
