package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Reap extends AbstractCoreFunctionEvaluator {

	public Reap() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IAST oldList = engine.getReapList();
		try {
			IAST result = F.List();
			IAST reapList = F.ListAlloc(10);
			engine.setReapList(reapList);
			IExpr expr = engine.evaluate(ast.arg1());
			result.append(expr);
			if (reapList.isAST0()) {
				result.append(F.List());
			} else {
				result.append(F.List(reapList));
			}
			return result;
		} finally {
			engine.setReapList(oldList);
		}

	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}
