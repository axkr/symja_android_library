package org.matheclipse.core.system;

import junit.framework.TestCase;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

public class BasicPatternPropertiesTestCase extends TestCase {
	private Parser fParser;

	protected static boolean DEBUG = true;

	public BasicPatternPropertiesTestCase(String name) {
		super(name);
	}

	public void checkPriority(String patternString, String priority) {
		try {
			EvalEngine engine = EvalEngine.get();
			ASTNode node = fParser.parse(patternString);
			IExpr pat = new AST2Expr(false, engine).convert(node);

			PatternMatcher matcher = new PatternMatcher(pat);
			assertEquals(Integer.toString(matcher.getLHSPriority()), priority);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(0, priority);
		}
	}

	public void comparePriority(String patternString1, String patternString2, int result) {
		try {
			EvalEngine engine = EvalEngine.get();
			ASTNode node = fParser.parse(patternString1);
			IExpr pat1 = new AST2Expr(false, engine).convert(node);
			PatternMatcher matcher1 = new PatternMatcher(pat1);

			node = fParser.parse(patternString2);
			IExpr pat2 = new AST2Expr(false, engine).convert(node);
			PatternMatcher matcher2 = new PatternMatcher(pat2);

			assertEquals(matcher1.equivalentTo(matcher2), result);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(Integer.MAX_VALUE, result);
		}
	}

	public void testSimplePriority() {
		// the space between "x_" and "." operator is needed:
		checkPriority("x_ . y_", "2147483586");
		checkPriority("x_+y_", "2147483586");
		checkPriority("f[x_]", "2147483592");
		checkPriority("f[x__]", "2147483597");
		checkPriority("f[x_,y_]", "2147483586");
		checkPriority("g[x_,y_]", "2147483586");
		checkPriority("g[x_,42, y_]", "2147483537");
//		checkPriority("x_ . y_", "1073741762");
//		checkPriority("x_+y_", "1073741762");
//		checkPriority("f[x_]", "1073741768");
//		checkPriority("f[x__]", "1073741773");
//		checkPriority("f[x_,y_]", "1073741762");
//		checkPriority("g[x_,y_]", "1073741762");
//		checkPriority("g[x_,42, y_]", "1073741713");
	}

	public void testCompareTo() {
		comparePriority("f[a]", "f[x_]", -1);
		comparePriority("f[a,b,c,d]", "f[x_]", -1);
		comparePriority("f[x_]", "f[x__]", -1);
		comparePriority("f[x_,y_,z_]", "f[x__]", -1);
	}

	/**
	 * The JUnit setup method
	 */
	@Override
	protected void setUp() {
		try {
			// setup the evaluation engine (and bind to current thread)
			EvalEngine engine = new EvalEngine(); // EvalEngine.get();
			EvalEngine.set(engine);
			engine.setSessionID("BasicPatternPropertiesTestCase");
			engine.setRecursionLimit(256);
			engine.setIterationLimit(1024 * 1024);
			fParser = new Parser();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
