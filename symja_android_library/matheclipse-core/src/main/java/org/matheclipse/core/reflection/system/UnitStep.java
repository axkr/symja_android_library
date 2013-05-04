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
		if (ast.size() == 2) {
			IExpr arg1 = ast.get(1);
			if (arg1.isSignedNumber()) {
				ISignedNumber isn = (ISignedNumber)arg1;
				return F.integer(isn.complexSign() < 0 ? 0 : 1);
			}
		}
		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.ORDERLESS);
	}

}
