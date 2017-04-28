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
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 1, 2);

		final int moduleCounter = engine.incModuleCounter();
		if (ast.isAST1()) {
			if (ast.arg1().isSymbol()) {
				final String varAppend = ast.arg1().toString() + "$" + moduleCounter;
				return F.userSymbol(varAppend, engine);
			} else if (ast.arg1() instanceof IStringX) {
				// TODO start counter by 1....
				final String varAppend = ast.arg1().toString() + moduleCounter;
				return F.userSymbol(varAppend, engine);
			}
		}
		final String varAppend = "$" + moduleCounter;
		return F.userSymbol(varAppend, engine);
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
