package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>===</code> operator implementation.
 * 
 */
public class SameQ extends AbstractFunctionEvaluator {
	public SameQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() > 1) {
			for (int i = 2; i < ast.size(); i++) {
				if (!ast.get(i - 1).isSame(ast.get(i))) {
					return F.False;
				}
			}
			return F.True;
		}

		return F.False;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.FLAT|ISymbol.NHOLDALL);
	}
}
