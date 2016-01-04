package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the first argument is an integer; <code>False</code> otherwise
 */
public class IntegerQ extends AbstractCoreFunctionEvaluator {

	public IntegerQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		final IExpr temp = engine.evaluate(ast.arg1());
		if (temp.isList()) {
			// thread over list
			return ((IAST) temp).mapAt(F.IntegerQ(null),1);
		}
		return F.bool(temp.isInteger());
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
