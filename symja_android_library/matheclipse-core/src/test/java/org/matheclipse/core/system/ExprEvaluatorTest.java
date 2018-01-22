package org.matheclipse.core.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import junit.framework.TestCase;

public class ExprEvaluatorTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// wait for initializing of Integrate() rules:
		F.join();
	}

	public void testStringEval001() {
		EvalEngine engine = new EvalEngine(true);
		ExprEvaluator eval = new ExprEvaluator(engine, true, 20);

		String str = "sin(x)";
		IExpr e = eval.eval(str);
		int i = 100;
		eval.defineVariable("x", (double) i);
		double result = e.evalDouble();
		assertEquals(-0.5063656411097588, result, 0E-10);
	}

}
