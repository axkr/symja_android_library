package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * 
 */
public class JavaFormTestCases extends AbstractTestCase {
	public JavaFormTestCases(String name) {
		super(name);
	}

	public void testJavaForm001() {
		// don't distinguish between lower- and uppercase identifiers
		Config.PARSER_USE_LOWERCASE_SYMBOLS = true;
		EvalUtilities util = new EvalUtilities(false, true);

		IAST function = Sinc(Times(CI, CInfinity));

		IExpr result = PatternMatcher.evalLeftHandSide(function);
		assertEquals(result.internalFormString(true, -1), "Sinc(DirectedInfinity(CI))");

		result = util.evaluate(function);
		assertEquals(result.internalFormString(true, -1), "CInfinity");
	}

}
