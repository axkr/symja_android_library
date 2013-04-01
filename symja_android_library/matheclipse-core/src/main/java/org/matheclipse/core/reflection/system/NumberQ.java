package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

/**
 * Returns <code>True</code>, if the given expression is an number object
 * 
 */
public class NumberQ extends AbstractFunctionEvaluator implements Predicate<IExpr> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static NumberQ CONST = new NumberQ();

	public NumberQ() {
	}

	/**
	 * Returns <code>True</code> if the 1st argument is a number;
	 * <code>False</code> otherwise
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		return F.bool(ast.get(1).isNumber());
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	public boolean apply(final IExpr expr) {
		return expr.isNumber();
	}
}
