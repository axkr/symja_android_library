package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.basic.Config;
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
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);

		for (int i = 1; i < ast.size(); i++) {
			if (!(ast.get(i).isInteger())) {
				return null;
			}
		}
		try {
			BigInteger bigResult = powerMod(((IInteger) ast.arg1()).getBigNumerator(), ((IInteger) ast.arg2()).getBigNumerator(),
					((IInteger) ast.arg3()).getBigNumerator());
			return F.integer(bigResult);
		} catch (ArithmeticException ae) {
			if (Config.SHOW_STACKTRACE) {
				ae.printStackTrace();
			}
		}
		return null;
	}

	public static BigInteger powerMod(BigInteger a, BigInteger b, BigInteger m) throws ArithmeticException {
		return a.modPow(b, m);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}
