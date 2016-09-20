package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.PolynomialsUtils;

public class ChebyshevT extends AbstractFunctionEvaluator {

	public ChebyshevT() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		IExpr arg1 = ast.arg1();
		IExpr arg2 = ast.arg2();
		if (arg1.isInteger()) {
			int degree = -1;
			try {
				degree = ((ISignedNumber) arg1).toInt();
			} catch (Exception ex) {
				return F.NIL;
			}
			if (degree < 0) {
				degree *= -1;
			}
			return PolynomialsUtils.createChebyshevPolynomial(degree, ast.arg2());
		}
		if (arg2.isZero()) {
			return F.Cos(F.Times(F.C1D2, F.Pi, arg1));
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
