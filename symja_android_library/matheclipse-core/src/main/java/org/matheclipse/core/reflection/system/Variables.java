package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Determine the variable symbols of an expression
 */
public class Variables extends AbstractFunctionEvaluator {

	public Variables() {
	}

	/**
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		ExprVariables eVar = new ExprVariables(ast.get(1));
		return eVar.getVarList();
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
