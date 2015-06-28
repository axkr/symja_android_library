package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the numerator part of an expression
 * 
 * See <a href="http://en.wikipedia.org/wiki/Fraction_(mathematics)">Wikipedia: Fraction (mathematics)</a>
 * 
 * @see org.matheclipse.core.reflection.system.Denominator
 */
public class Numerator implements IFunctionEvaluator {

	public Numerator() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg = ast.arg1();
		if (arg.isRational()) {
			return ((IRational) arg).getNumerator();
		}
		IExpr[] parts = Apart.getFractionalParts(arg);
		if (parts == null) {
			return arg;
		}
		return parts[0];
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
