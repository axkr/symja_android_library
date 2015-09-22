package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

public class Divisible extends AbstractFunctionEvaluator {

	public Divisible() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isList()) {
			// thread over first list
			return ((IAST) ast.arg1()).mapAt(F.List(), ast, 1);
		}

		IExpr result = F.eval(F.Divide(ast.arg1(), ast.arg2()));
		if (result.isNumber()) {
			if (result.isComplex()) {
				IComplex comp = (IComplex) result;
				// NOTE: compare with operator == because of possible null values
				if (isSignedNumberDivisible(comp.getRe()) == F.True && isSignedNumberDivisible(comp.getIm()) == F.True) {
					return F.True;
				}
				return F.False;
			}
			if (result.isSignedNumber()) {
				return isSignedNumberDivisible((ISignedNumber) result);
			}
			return F.False;
		}
		return null;
	}

	/**
	 * Return F.True or F.False if result is divisible. Return <code>null</code>, if the result could not be determined.
	 * 
	 * @param result
	 * @return
	 */
	private IExpr isSignedNumberDivisible(ISignedNumber result) {
		if (result.isInteger()) {
			return F.True;
		}
		if (result.isNumIntValue()) {
			// return F.True;
			try {
				result.toLong();
				return F.True;
			} catch (ArithmeticException ae) {
				return null;
			}
		}
		return F.False;
	}

}
