package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.math.MathException;

/**
 * TODO implement &quot;TimeConstrained&quot; mode
 * 
 */
public class TimeConstrained extends AbstractCoreFunctionEvaluator {

	public TimeConstrained() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		IExpr arg2 = engine.evaluate(ast.arg2());
		long seconds = 0L;
		try {
			if (arg2.isSignedNumber()) {
				seconds = ((ISignedNumber) arg2).toLong();
			} else {
				engine.printMessage("TimeConstrained: " + ast.arg2().toString() + " is not a Java long value.");
				return null;
			}
		} catch (ArithmeticException ae) {
			engine.printMessage("TimeConstrained: " + ast.arg2().toString() + " is not a Java long value.");
			return null;
		}
		// TODO implement &quot;TimeConstrained&quot; mode
		try {
			return engine.evaluate(ast.arg1());
		} catch (final MathException e) {
			throw e;
		} catch (final Throwable th) {
			if (ast.size() == 4) {
				return ast.arg3();
			}
		}
		return F.Aborted;
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
