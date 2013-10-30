package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * TODO implement &quot;Unique&quot; mode
 * 
 */
public class Unique extends AbstractCoreFunctionEvaluator {

	public Unique() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 1, 2);

		final EvalEngine engine = EvalEngine.get();
		final int moduleCounter = engine.incModuleCounter();
		if (ast.size() == 2) {
			if (ast.get(1).isSymbol()) {
				final String varAppend = ast.get(1).toString() + "$" + moduleCounter;
				return F.$s(varAppend);
			} else if (ast.get(1) instanceof IStringX) {
				// TODO start counter by 1....
				final String varAppend = ast.get(1).toString() + moduleCounter;
				return F.$s(varAppend);
			}
		}
		final String varAppend = "$" + moduleCounter;
		return F.$s(varAppend);
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
