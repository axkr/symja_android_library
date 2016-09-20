package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 * <p>
 * See the online Symja function reference: <a href="https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/ClearAll">ClearAll</a>
 * </p>
 */
public class ClearAll extends AbstractCoreFunctionEvaluator {

	public ClearAll() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		ISymbol symbol = Validate.checkSymbolType(ast, 1);
		symbol.clearAll(engine);
		return F.Null;
	} 

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
