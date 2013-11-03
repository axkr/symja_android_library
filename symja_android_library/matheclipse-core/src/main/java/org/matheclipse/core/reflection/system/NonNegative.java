package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class NonNegative implements IFunctionEvaluator {

	public NonNegative() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		
		IExpr arg1 = ast.arg1();
		if (arg1.isSignedNumber()) {
			if (!((ISignedNumber) arg1).isNegative()) {
				return F.True;
			}
			return F.False;
		}
		if (arg1.isNumber()) {
			return F.False;
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
