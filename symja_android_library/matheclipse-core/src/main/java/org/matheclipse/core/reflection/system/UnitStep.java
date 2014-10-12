package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 */
public class UnitStep implements INumeric, IFunctionEvaluator {

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

	public IExpr evaluate(final IAST ast) {
		int size = ast.size();
		if (size > 1) {
			for (int i = 1; i < size; i++) {
				IExpr temp = ast.get(i);
				if (temp.isSignedNumber()) {
					if (((ISignedNumber) temp).complexSign() < 0) {
						return F.C0;
					} else {
						continue;
					}
				}
				if (temp.isNumericFunction()) {
					IExpr num = F.evaln(temp);
					if (num.isSignedNumber()) {
						if (((ISignedNumber) num).complexSign() < 0) {
							return F.C0;
						} else {
							continue;
						}
					}
				}
				return null;
			}
		}
		return F.C1;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE);
	}
}
