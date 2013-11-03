package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Sow extends AbstractCoreFunctionEvaluator {

	public final double DEFAULT_CHOP_DELTA = 10E-10;

	public Sow() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		EvalEngine engine = EvalEngine.get();
		IAST reapList = engine.getReapList();
		IExpr expr = engine.evaluate(ast.arg1());
		if (reapList != null) {
			reapList.add(expr);
		}
		return expr;
	} 

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
