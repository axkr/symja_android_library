package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 */
public class PowerMod extends AbstractFunctionEvaluator {

	public PowerMod() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		for (int i = 1; i < ast.size(); i++) {
			if (!(ast.get(i).isInteger())) {
				return F.NIL;
			}
		}
		IInteger arg1 = (IInteger) ast.get(1);
		IInteger arg2 = (IInteger) ast.get(2);
		IInteger arg3 = (IInteger) ast.get(3);
		try {
			if (arg2.isMinusOne()) {
				return arg1.modInverse(arg3);
			}
			return arg1.modPow(arg2, arg3);
		} catch (ArithmeticException ae) {
			if (Config.SHOW_STACKTRACE) {
				ae.printStackTrace();
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}
