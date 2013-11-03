package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import com.google.common.base.Predicate;

/**
 * Returns <code>True</code>, if the given expression is bound to a value.
 * 
 */
public class ValueQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static ValueQ CONST = new ValueQ();

	public ValueQ() {
	}

	/**
	 * Returns <code>True</code> if the 1st argument is an atomic object; <code>False</code> otherwise
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		return F.bool(apply(ast.arg1()));
	}

	public boolean apply(final IExpr expr) {
		return expr.isValue();
	}

	@Override
	public IExpr numericEval(IAST ast) {
		return evaluate(ast);
	}

}
