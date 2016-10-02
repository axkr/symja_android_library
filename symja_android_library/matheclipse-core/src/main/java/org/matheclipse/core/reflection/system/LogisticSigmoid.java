package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Logistic function
 * 
 * See <a href="https://en.wikipedia.org/wiki/Logistic_function">Wikipedia:
 * Logistic function</a>
 * 
 */
public class LogisticSigmoid extends AbstractEvaluator {

	public LogisticSigmoid() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		if (ast.arg1().isNumber()) {
			return F.Power(F.Plus(F.C1, F.Power(F.E, F.Times(F.CN1, ast.arg1()))), F.CN1);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}

}
