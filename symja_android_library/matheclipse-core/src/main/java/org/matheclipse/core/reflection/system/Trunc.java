package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Trunc extends AbstractFunctionEvaluator {

	public Trunc() {
	}

	final static String[] RULES = {
		"Trunc[Pi]=3",
		"Trunc[E]=2",
		"Trunc[x_NumberQ*y_] := -Trunc[-x*y] /; SignCmp[x]<0"};

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		if (ast.get(1).isSignedNumber()) {
			final ISignedNumber signedNumber = (ISignedNumber) ast.get(1);
			if ((signedNumber).isNegative()) {
				return (signedNumber).ceil();
			} else {
				return (signedNumber).floor();
			}
		}

		return null;
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	@Override
	public String[] getRules() {
		return RULES;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}

}
