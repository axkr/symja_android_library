package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import com.google.common.base.Predicate;

/**
 * Returns <code>True</code>, if the given expression is an number object
 * 
 */
public class NumberQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static NumberQ CONST = new NumberQ();

	public NumberQ() {
	}

	/**
	 * Returns <code>True</code> if the 1st argument is a number; <code>False</code> otherwise
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		IExpr arg1 = F.eval(ast.arg1());
		if (arg1.isDirectedInfinity()) {  
			return F.False;
		}
		return F.bool(arg1.isNumber());
	}

	@Override
	public boolean apply(final IExpr expr) {
		return expr.isNumber();
	}
}
