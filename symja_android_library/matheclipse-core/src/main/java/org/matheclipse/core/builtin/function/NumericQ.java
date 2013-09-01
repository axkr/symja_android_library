package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Predicate;

/**
 * Returns <code>True</code>, if the given expression is a numeric function or value.
 * 
 */
public class NumericQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {

	/**
	 * Constructor for the unary predicate
	 */
	public final static NumericQ CONST = new NumericQ();

	public NumericQ() {
	}

	@Override
	public boolean apply(IExpr arg) {
		return arg.isNumericFunction();
	}

	/**
	 * Returns <code>True</code> if the first argument is a numeric object; <code>False</code> otherwise
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		IExpr arg1 = F.eval(ast.get(1));
		return F.bool(apply(arg1));
	}

	@Override
	public void setUp(ISymbol symbol) throws SyntaxError {
	}
}
