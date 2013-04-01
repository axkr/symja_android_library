package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class RandomReal extends AbstractFunctionEvaluator {

	public RandomReal() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 1) {
			// RandomReal[] gives a double value between 0.0 and 1.0
			double r = Math.random();
			return F.num(r);
		}

		return null;
	}

}
