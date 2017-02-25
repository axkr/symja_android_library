package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class KroneckerDelta extends AbstractEvaluator {

	public KroneckerDelta() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size > 1) {
			IExpr arg1 = ast.arg1();
			INumber temp = arg1.evalNumber();
			if (temp != null) {
				if (size == 2) {
					if (temp.isZero()) {
						return F.C1;
					}
					if (temp.isNumber()) {
						return F.C0;
					}
					return F.NIL;
				}
				arg1 = temp;
				for (int i = 2; i < size; i++) {
					IExpr expr = ast.get(i);
					temp = expr.evalNumber();
					if (temp != null&&temp.equals(arg1)) {
						continue;
					}
					return F.C0;
				}
				return F.C1;
			}

		}
		return F.NIL;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
