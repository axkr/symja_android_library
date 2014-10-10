package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 */
public class UnitStep implements IFunctionEvaluator {

	public UnitStep() {
	}

	public IExpr evaluate(final IAST ast) {
		int size = ast.size();
		if (size > 1) {
			for (int i = 1; i < size; i++) {
				if (ast.get(i).isSignedNumber()) {
					if (((ISignedNumber) ast.get(i)).complexSign() < 0) {
						return F.C0;
					} else {
						continue;
					}
				}
				if (ast.get(i).isNumericFunction()) {
					IExpr num = F.evaln(ast.get(i));
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
