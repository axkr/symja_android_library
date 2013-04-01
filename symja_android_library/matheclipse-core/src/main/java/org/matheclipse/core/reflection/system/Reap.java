package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Reap extends AbstractFunctionEvaluator {
	public final double DEFAULT_CHOP_DELTA = 10E-10;

	public Reap() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		EvalEngine engine = EvalEngine.get();
		IAST oldList = engine.getReapList();
		try {
			IAST result = F.List();
			IAST reapList = F.List();
			engine.setReapList(reapList);
			IExpr expr = engine.evaluate(ast.get(1));
			result.add(expr);
			if (reapList.size() == 1) {
				result.add(F.List());
			} else {
				result.add(F.List(reapList));
			}
			return result;
		} finally {
			engine.setReapList(oldList);
		}

	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
	}
}
