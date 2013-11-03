package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>1</code> if the 1st argument evaluates to <code>True</code>; returns <code>0</code> if the 1st argument evaluates
 * to <code>False</code>; and <code>null</code> otherwise.
 */
public class Boole extends AbstractFunctionEvaluator {

	public Boole() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		 
		if (ast.arg1().isSymbol()) {
			if (ast.arg1().isTrue()) {
				return F.C1;
			}
			if (ast.arg1().isFalse()) {
				return F.C0;
			}
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
