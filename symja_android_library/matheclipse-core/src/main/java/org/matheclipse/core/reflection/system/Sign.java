package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Gets the sign value of a number. See <a href="http://en.wikipedia.org/wiki/Sign_function">Wikipedia - Sign function</a>
 * 
 */
public class Sign extends AbstractEvaluator {

	public Sign() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isIndeterminate()) {
			return F.Indeterminate;
		}
		if (arg1.isDirectedInfinity()) {
			IAST directedInfininty = (IAST) arg1;
			if (directedInfininty.isComplexInfinity()) {
				return F.Indeterminate;
			}
			if (directedInfininty.size() == 2) {
				return F.Sign(directedInfininty.arg1());
			}
		}

		if (AbstractAssumptions.assumeNegative(arg1)) {
			return F.CN1;
		}
		if (AbstractAssumptions.assumePositive(arg1)) {
			return F.C1;
		}

		INumber number = arg1.evalNumber();
		if (number != null) {
			return numberSign(number);
		}
		return F.NIL;
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
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}

}
