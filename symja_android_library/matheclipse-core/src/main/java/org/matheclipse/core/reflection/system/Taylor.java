package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Power series expansion with Taylor formula
 */
public class Taylor extends AbstractFunctionEvaluator {
	public Taylor() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 3 && (ast.get(2).isVector() == 3)) {

			IAST list = (IAST) ast.get(2);
			final int lowerLimit = Validate.checkIntType(list, 2, Integer.MIN_VALUE);
			if (lowerLimit != 0) {
				// TODO support other cases than 0
				return null;
			}
			final int upperLimit = Validate.checkIntType(list, 3, Integer.MIN_VALUE);
			if (upperLimit < 0) {
				return null;
			}
			IAST fadd = F.Plus();
			fadd.add(F.ReplaceAll(ast.get(1), F.Rule(list.get(1), list.get(2))));
			IExpr temp = ast.get(1);
			IExpr factor = null;
			for (int i = 1; i <= upperLimit; i++) {
				temp = F.D(temp, list.get(1));
				factor = F.Times(F.Power(F.Factorial(F.integer(i)), F.CN1), F.Power(F.Plus(list.get(1), F.Times(F.CN1, list.get(2))), F
						.integer(i)));
				fadd.add(F.Times(F.ReplaceAll(temp, F.Rule(list.get(1), list.get(2))), factor));
			}
			return fadd;

		}
		return null;
	}
}
