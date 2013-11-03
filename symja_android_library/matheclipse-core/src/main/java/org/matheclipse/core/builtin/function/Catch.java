package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Catch  extends AbstractCoreFunctionEvaluator {

	public Catch() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		try {
			return F.eval(ast.arg1());
		} catch (final ThrowException e) {
			return e.getValue();
		}
	} 

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
