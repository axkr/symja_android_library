package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * DiracDelta function returns <code>0</code> for all x != 0
 */
public class DiracDelta extends AbstractEvaluator {

	public DiracDelta() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size > 1) {
			for (int i = 1; i < size; i++) {
				IExpr expr = ast.get(i);
				ISignedNumber temp = expr.evalSignedNumber();
				if (temp != null) {
					if (temp.isZero()) {
						return F.NIL;
					}
					continue;
				} 
				return F.NIL;
			}
		}
		return F.C0;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
