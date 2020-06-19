package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class DensityHistogramExample extends BasePlotExample {

	@Override
	public String exampleFunction() {
		return "DensityHistogram( {{1, 1}, {2, 2}, {3, 3}, None, {3, 3}, {5, 5}, f(), {2, 2}, {1, 1}, foo, {2, 2}, {3, 3}} )";
		// return "DensityHistogram( RandomVariate(NormalDistribution(0, 1), {200,2}) )";
	}

	public static void main(String[] args) {
		DensityHistogramExample p = new DensityHistogramExample();
		p.generateHTML();
	}

}
