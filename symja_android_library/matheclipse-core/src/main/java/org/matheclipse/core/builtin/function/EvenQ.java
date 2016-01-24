package org.matheclipse.core.builtin.function;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is an even integer number; <code>False</code> otherwise
 */
public class EvenQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static EvenQ CONST = new EvenQ();

	public EvenQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		final IExpr temp = engine.evaluate(ast.arg1());
		if (temp.isList()) {
			// thread over list
			return ((IAST) temp).mapAt(F.EvenQ(null), 1);
		}
		return F.bool(test(temp));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	@Override
	public boolean test(final IExpr expr) {
		return (expr.isInteger()) && ((IInteger) expr).isEven();
	}
}
