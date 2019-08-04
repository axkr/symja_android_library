package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.Derivative;
import static org.matheclipse.core.expression.F.Pattern;
import static org.matheclipse.core.expression.F.Symbol;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.ast;
import static org.matheclipse.core.expression.F.f;
import static org.matheclipse.core.expression.F.function;
import static org.matheclipse.core.expression.F.n;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;

import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
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

		IASTAppendable ast1 = ast(f);
		ast1.append(Times(a, x));
		IASTAppendable ast2 = ast(f);
		ast2.append(Times(a_, x_));
		PatternMatcher pm1 = new PatternMatcher(ast1);
		PatternMatcher pm2 = new PatternMatcher(ast2);
		int cpr = pm1.equivalentTo(pm2);
		assertEquals(cpr, -1);

	}

	public void testComplicatedPatternRule() {
		IExpr expr = F.Integrate(F.unaryAST1(F.unaryAST1(F.Derivative(F.n_), F.f_), F.x_), F.x_Symbol);
		assertEquals("Integrate[Derivative[n_][f_][x_],x_Symbol]", expr.toString());
		boolean isComplicated = RulesData.isComplicatedPatternRule(expr);
		assertTrue(isComplicated);
	}
}
