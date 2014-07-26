package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.function.NumericQ;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Gets the sign value of a number. See <a
 * href="http://en.wikipedia.org/wiki/Sign_function">Wikipedia - Sign
 * function</a>
 * 
 */
public class Sign implements IFunctionEvaluator {

	public Sign() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		ISignedNumber signedNumber = NumericQ.getSignedNumberNumericQ(arg1);
		if (signedNumber != null) {
			return numberSign(signedNumber);
		}
		return null;
	}

	public IExpr numberSign(INumber arg1) {
		if (arg1.isSignedNumber()) {
			final int signum = ((ISignedNumber) arg1).sign();
			return F.integer(signum);
		} else if (arg1.isComplex()) {
			IComplex c = (IComplex) arg1;
			return F.Times(c, F.Power(c.eabs(), F.CN1));
		} else if (arg1.isComplexNumeric()) {
			IComplexNum c = (IComplexNum) arg1;
			return c.divide(F.num(c.dabs()));
		}
		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
