package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Power series expansion with Taylor formula
 */
public class Taylor extends AbstractFunctionEvaluator {
	public Taylor() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST2() && (ast.arg2().isVector() == 3)) {

			IAST list = (IAST) ast.arg2();
			final int upperLimit = Validate.checkIntType(list, 3, 0);
			if (upperLimit < 0) {
				return F.NIL;
			}
			IASTAppendable fadd = F.PlusAlloc(upperLimit+2);
			fadd.append(F.ReplaceAll(ast.arg1(), F.Rule(list.arg1(), list.arg2())));
			IExpr temp = ast.arg1();
			IExpr factor = null;
			for (int i = 1; i <= upperLimit; i++) {
				temp = F.D(temp, list.arg1());
				factor = F.Times(F.Power(F.Factorial(F.integer(i)), F.CN1), F.Power(F.Plus(list.arg1(), F.Times(F.CN1, list.arg2())), F
						.integer(i)));
				fadd.append(F.Times(F.ReplaceAll(temp, F.Rule(list.arg1(), list.arg2())), factor));
			}
			return fadd;
 
		}
		return F.NIL;
	}
}
