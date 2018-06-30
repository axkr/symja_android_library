package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * HeavisideTheta function returns <code>1</code> for all x greater than
 * <code>0</code> and <code>0</code> for all x less than <code>0</code>,
 */
public class HeavisideTheta extends AbstractEvaluator {

	public HeavisideTheta() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size > 1) {
			for (int i = 1; i < size; i++) {
				IExpr expr = ast.get(i);
				ISignedNumber temp = expr.evalReal();
				if (temp != null) {
					if (temp.sign() < 0) {
						return F.C0;
					} else if (temp.sign() > 0) {
						continue;
					}
				} else {
					if (expr.isNegativeResult()) {
						return F.C0;
					}
					if (expr.isPositiveResult()) {
						continue;
					}
					if (expr.isNegativeInfinity()) {
						return F.C0;
					}
					if (expr.isInfinity()) {
						continue;
					}
				}
				return F.NIL;
			}
		}
		return F.C1;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
