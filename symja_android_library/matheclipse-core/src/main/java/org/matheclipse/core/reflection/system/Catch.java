package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Catch implements IFunctionEvaluator {

	public Catch() {
		super();
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		try {
			return F.eval(ast.get(1));
		} catch (final ThrowException e) {
			return e.getValue();
		}
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
