package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * Tests for the Java port of the
 * <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class PatternsTest extends AbstractTestCase {
	public PatternsTest(String name) {
		super(name);
	}

	@Override
	public void check(String evalString, String expectedResult) {
		check(fScriptEngine, evalString, expectedResult, -1);
	}

	public void testPriority001() {

		IASTAppendable ast1 = ast(f);
		ast1.append(a_);
		IASTAppendable ast2 = ast(f);
		ast2.append(Times(a_, x_));
		PatternMatcher pm1 = new PatternMatcher(ast1);
		PatternMatcher pm2 = new PatternMatcher(ast2);
		int cpr = pm1.equivalentTo(pm2);
		assertEquals(cpr, 1);

	}

	public void testPriority002() {

		IASTAppendable ast1 =  ast(f);
		ast1.append(Times(a, x)); 
		IASTAppendable ast2 = ast(f);
		ast2.append(Times(a_, x_));
		PatternMatcher pm1 = new PatternMatcher(ast1);
		PatternMatcher pm2 = new PatternMatcher(ast2);
		int cpr = pm1.equivalentTo(pm2);
		assertEquals(cpr, -1);

	}
	
}
