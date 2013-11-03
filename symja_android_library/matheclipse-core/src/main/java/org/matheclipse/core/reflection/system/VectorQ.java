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
 * Returns <code>True</code> if the 1st argument is a vector;
 * <code>False</code> otherwise
 */
public class VectorQ extends AbstractFunctionEvaluator {

	public VectorQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		return F.bool(ast.arg1().isVector() != (-1));
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
