package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 */
public class UnitStep extends AbstractEvaluator implements INumeric {

	public UnitStep() {
	}

	@Override
	public double evalReal(double[] stack, int top, int size) {
		for (int i = top - size + 1; i < top + 1; i++) {
			if (stack[i] < 0.0) {
				return 0.0;
			}
		}
		return 1.0;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size > 1) {
			for (int i = 1; i < size; i++) {
				ISignedNumber temp = ast.get(i).evalSignedNumber();
				if (temp != null) {
					if (temp.complexSign() < 0) {
						return F.C0;
					} else {
						continue;
					}
				}
				return F.NIL;
			}
		}
		return F.C1;
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
