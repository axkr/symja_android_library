package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * 
 */
public class JavaFormTestCase extends AbstractTestCase {
	public JavaFormTestCase(String name) {
		super(name);
	}

	public void testJavaForm001() {
		// don't distinguish between lower- and uppercase identifiers
		Config.PARSER_USE_LOWERCASE_SYMBOLS = true;
		EvalUtilities util = new EvalUtilities(false, true);

		IAST function = Sinc(Times(CI, CInfinity));

		IExpr result = PatternMatcher.evalLeftHandSide(function, EvalEngine.get());
		assertEquals(result.internalFormString(true, -1), "Sinc(DirectedInfinity(CI))");

		result = util.evaluate(function);
		assertEquals(result.internalFormString(true, -1), "oo");
	}
	
	public void testJavaForm002() {
		// don't distinguish between lower- and uppercase identifiers
		Config.PARSER_USE_LOWERCASE_SYMBOLS = true;
		EvalUtilities util = new EvalUtilities(false, true);

		IAST function = Sinc(Times(CI, CInfinity));

		IExpr result = PatternMatcher.evalLeftHandSide(function, EvalEngine.get());
		assertEquals(result.internalJavaString(true, -1,false,true), "F.Sinc(F.DirectedInfinity(F.CI))");

		result = util.evaluate(function);
		assertEquals(result.internalJavaString(true, -1,false,true), "F.oo");
	}

}
