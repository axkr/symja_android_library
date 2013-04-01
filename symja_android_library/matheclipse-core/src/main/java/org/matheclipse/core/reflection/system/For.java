package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * For[] loop
 * 
 * Example: For[$j = 1, $j <= 10, $j++, Print[$j]]
 * 
 */
public class For implements IFunctionEvaluator {

	public For() {
		super();
	}

	public IExpr evaluate(final IAST functionList) {
		final EvalEngine engine = EvalEngine.get();

		IExpr temp = F.Null;
		if (functionList.size() == 5) {
			engine.evaluate(functionList.get(1));
			while (true) {
				try {
					if (!engine.evaluate(functionList.get(2)).equals(F.True)) {
						return temp;
					}
					temp = engine.evaluate(functionList.get(4));
				} catch (final BreakException e) {
					return F.Null;
				} catch (final ContinueException e) {
					continue;
				} finally {
					engine.evaluate(functionList.get(3));
				}
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
