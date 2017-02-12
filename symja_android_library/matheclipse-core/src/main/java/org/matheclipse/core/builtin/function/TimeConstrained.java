package org.matheclipse.core.builtin.function;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.math.MathException;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

/**
 * TODO implement &quot;TimeConstrained&quot; mode
 * 
 */
public class TimeConstrained extends AbstractCoreFunctionEvaluator {

	private static class EvalCallable implements Callable<IExpr> {
		private final EvalEngine fEngine;
		private final IExpr fExpr;

		public EvalCallable(IExpr expr, EvalEngine engine) {
			fExpr = expr;
			fEngine = engine;
		}

		@Override
		public IExpr call() throws Exception {
			// TODO Auto-generated method stub
			return fEngine.evaluate(fExpr);
		}

	}
	
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
				return F.NIL;
			}
		} catch (ArithmeticException ae) {
			engine.printMessage("TimeConstrained: " + ast.arg2().toString() + " is not a Java long value.");
			return F.NIL;
		}

		if (Config.JAS_NO_THREADS) {
			// no thread can be spawned
			try {
				return engine.evaluate(ast.arg1());
			} catch (final MathException e) {
				throw e;
			} catch (final Throwable th) {
				if (ast.isAST3()) {
					return ast.arg3();
				}
			}
			return F.Aborted;
		} else {
			TimeLimiter timeLimiter = new SimpleTimeLimiter();
			Callable<IExpr> work = new EvalCallable(ast.arg1(), engine);

			try {
				return timeLimiter.callWithTimeout(work, seconds, TimeUnit.SECONDS, true);
			} catch (java.util.concurrent.TimeoutException e) {
				if (ast.isAST3()) {
					return ast.arg3();
				}
				return F.Aborted;
			} catch (com.google.common.util.concurrent.UncheckedTimeoutException e) {
				if (ast.isAST3()) {
					return ast.arg3();
				}
				return F.Aborted;
			} catch (Exception e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
				return F.Null;
			}

		}

	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
