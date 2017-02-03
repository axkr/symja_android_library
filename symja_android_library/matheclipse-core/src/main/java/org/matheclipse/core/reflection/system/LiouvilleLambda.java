package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 */
public class LiouvilleLambda extends AbstractFunctionEvaluator {

	public LiouvilleLambda() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isOne()) {
			return F.C1;
		}
		if (arg1.isInteger() && arg1.isPositive()) {
			IExpr expr = engine.evaluate(F.FactorInteger(arg1));
			if (expr.isList()) {
				IAST list = (IAST) expr;
				int result = 1;
				IInteger temp;
				for (int i = 1; i < list.size(); i++) {
					temp = (IInteger) list.get(i).getAt(2);
					if (temp.isOdd()) {
						if (result == -1) {
							result = 1;
						} else {
							result = -1;
						}
					}
				}
				if (result == -1) {
					return F.CN1;
				}
				return F.C1;
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}
