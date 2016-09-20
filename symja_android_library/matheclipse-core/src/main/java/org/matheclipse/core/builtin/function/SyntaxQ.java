package org.matheclipse.core.builtin.function;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;

/**
 * Returns <code>True</code>, if the given expression is a string which has the
 * correct syntax
 * 
 */
public class SyntaxQ extends AbstractCorePredicateEvaluator implements Predicate<String> {

	public SyntaxQ() {
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		if (!(arg1 instanceof IStringX)) {
			return false;
		}
		return test(arg1.toString());
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

	@Override
	public boolean test(final String str) {
		return ExprParser.test(str);
	}

}
