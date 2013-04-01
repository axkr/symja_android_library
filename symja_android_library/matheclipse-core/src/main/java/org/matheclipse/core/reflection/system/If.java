package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class If implements IFunctionEvaluator {

	public If() {
	}

	public IExpr evaluate(final IAST functionList) {
		final EvalEngine engine = EvalEngine.get();
		if ((functionList.size() <= 5) && (functionList.size() >= 3)) {
			final IExpr temp = engine.evaluate(functionList.get(1));

			if (temp.equals(F.False)) {
				if (functionList.size() >= 4) {
					return functionList.get(3);
				}

				return F.Null;
			}

			if (temp.equals(F.True)) {
				return functionList.get(2);
			}

			if (functionList.size() == 5) {
				return functionList.get(4);
			}
		}

		return F.Null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
