package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * Norm of a given argument
 */
public class Norm extends AbstractEvaluator {

	public Norm() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		IExpr arg1 = ast.arg1();

		int dim = arg1.isVector();
		if (dim > (-1)) {
			if (ast.size() == 3) {
				IExpr arg2 = ast.arg2();
				if (arg2.isInfinity()) {
					return ((IAST) arg1).map(F.Max, Functors.replaceAll(F.Abs(F.Null), F.Null));
				} else {
					if (arg2.isSymbol() || arg2.isSignedNumber()) {
						return F.Power(((IAST) arg1).map(F.Plus, Functors.replaceAll(F.Power(F.Abs(F.Null), arg2), F.Null)),
								arg2.inverse());
					}
				}
				return null;
			}
			return F.Sqrt(((IAST) arg1).map(F.Plus, Functors.replaceAll(F.Sqr(F.Abs(F.Null)), F.Null)));
		}
		if (arg1.isNumber()) {
			if (ast.size() == 3) {
				IExpr arg2 = ast.arg2();
				return null;
			}
			// absolute Value of a number
			return ((INumber) arg1).eabs();
		}
		if (arg1.isNumericFunction()) {
			if (ast.size() == 3) {
				IExpr arg2 = ast.arg2();
				return null;
			}
			// absolute Value
			return F.Abs(arg1);
		}
		return null;
	}

}
