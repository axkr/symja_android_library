package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is a list expression; <code>False</code> otherwise
 */
public class ListQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static ListQ CONST = new ListQ();

	public ListQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		final IExpr temp = F.eval(ast.arg1());
		return F.bool(apply(temp));
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

	public boolean apply(final IExpr expr) {
		return expr.isList();
	}
}
