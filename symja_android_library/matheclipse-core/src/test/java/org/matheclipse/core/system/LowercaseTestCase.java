package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Tests system.reflection classes
 */
public class LowercaseTestCase extends AbstractTestCase {

	public LowercaseTestCase(String name) {
		super(name);
	}

	// @Override
	// public void check(String evalString, String expectedResult) {
	// // fScriptEngine.put("RELAXED_SYNTAX", Boolean.TRUE);
	// super.check(evalString, expectedResult);
	// }

	public void test001() {
		// syntax error in relaxed mode
		// check("Sin[x]", "");
		check("f[[1,2]]", "(f[[1,2]])");
		check("-cos(x)", "-Cos(x)");
		check("expand((a+b)^3)", "a^3+3*a^2*b+3*a*b^2+b^3");
		check("expand((a+b)^8)", "a^8+8*a^7*b+28*a^6*b^2+56*a^5*b^3+70*a^4*b^4+56*a^3*b^5+28*a^2*b^6+8*a*b^7+b^8");
		check("expand((a+b+c)^3)", "a^3+3*a^2*b+3*a*b^2+b^3+3*a^2*c+6*a*b*c+3*b^2*c+3*a*c^2+3*b*c^2+c^3");
		// check(
		// "ExpandAll(Plus( Times(Times(-4, a, Power(c, 3)), Power(Plus(Power(b,
		// 2), Times(-2, a, c)), 2), Power(Plus(Power(b, 2), Times(-2, a, c)),
		// 2), Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2,
		// a, c)), Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2),
		// Times(-2, a, c)), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6),
		// c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32,
		// Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4),
		// Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c,
		// 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8, a,
		// Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8,
		// a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8,
		// a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d)))), Times(Times(3, a, b, d,
		// Plus(Times(Power(b, 4), c), Times(-3, a, Power(b, 3), d))),
		// Power(Plus(Power(b, 2), Times(-2, a, c)), 2), Plus(Power(b, 2),
		// Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b,
		// 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a, c)),
		// Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c), Times(24,
		// Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a, 3),
		// Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d)))), Times(Times(3, a, b, d, Plus(Times(-2, a, Power(b, 2),
		// Power(c, 2)), Times(6, Power(a, 2), b, c, d))), Power(Plus(Power(b,
		// 2), Times(-2, a, c)), 2), Plus(Power(b, 2), Times(-2, a, c)),
		// Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a,
		// c)), Plus(Power(b, 2), Times(-2, a, c)), Times(Plus(Power(b, 8),
		// Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d)))), Times(Times(-1, c,
		// Plus(Times(Power(b, 4), c), Times(-3, a, Power(b, 3), d))),
		// Power(Plus(Power(b, 2), Times(-2, a, c)), 2), Power(Plus(Power(b, 2),
		// Times(-2, a, c)), 2), Plus(Power(b, 2), Times(-2, a, c)),
		// Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a,
		// c)), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c), Times(24,
		// Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a, 3),
		// Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d)))), Times(Times(-1, c, Plus(Times(-2, a, Power(b, 2),
		// Power(c, 2)), Times(6, Power(a, 2), b, c, d))), Power(Plus(Power(b,
		// 2), Times(-2, a, c)), 2), Power(Plus(Power(b, 2), Times(-2, a, c)),
		// 2), Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2,
		// a, c)), Plus(Power(b, 2), Times(-2, a, c)), Times(Plus(Power(b, 8),
		// Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d)))), Times(Times(-6, a,
		// Power(b, 3), c, d), Power(Plus(Power(b, 2), Times(-2, a, c)), 2),
		// Power(Plus(Power(b, 2), Times(-2, a, c)), 2), Plus(Power(b, 2),
		// Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b,
		// 2), Times(-2, a, c)), Times(Plus(Power(b, 8), Times(-8, a, Power(b,
		// 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32,
		// Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4),
		// Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c,
		// 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8, a,
		// Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8,
		// a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8,
		// a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d)))), Times(Times(12, Power(a, 2), b,
		// Power(c, 2), d), Power(Plus(Power(b, 2), Times(-2, a, c)), 2),
		// Power(Plus(Power(b, 2), Times(-2, a, c)), 2), Plus(Power(b, 2),
		// Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b,
		// 2), Times(-2, a, c)), Times(Plus(Power(b, 8), Times(-8, a, Power(b,
		// 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32,
		// Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4),
		// Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c,
		// 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8, a,
		// Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8,
		// a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b, 8), Times(-8,
		// a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4), Power(c, 2)),
		// Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)), Times(16, Power(a,
		// 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c), Times(4, a,
		// Power(c, 2)), Times(-3, a, b, d)))), Times(Times(2, c, Plus(Power(b,
		// 2), Times(-2, a, c)), Plus(Times(4, Power(b, 9), d), Times(-42, a,
		// Power(b, 7), c, d), Times(162, Power(a, 2), Power(b, 5), Power(c, 2),
		// d), Times(-272, Power(a, 3), Power(b, 3), Power(c, 3), d), Times(168,
		// Power(a, 4), b, Power(c, 4), d), Times(18, Power(a, 2), Power(b, 6),
		// Power(d, 2)), Times(-126, Power(a, 3), Power(b, 4), c, Power(d, 2)),
		// Times(288, Power(a, 4), Power(b, 2), Power(c, 2), Power(d, 2)),
		// Times(-216, Power(a, 5), Power(c, 3), Power(d, 2))), Power(b, 2)),
		// Power(Plus(Power(b, 2), Times(-2, a, c)), 2), Power(Plus(Power(b, 2),
		// Times(-2, a, c)), 2), Plus(Power(b, 2), Times(-2, a, c)),
		// Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a,
		// c)), Plus(Power(b, 2), Times(-2, a, c)), Times(Plus(Power(b, 8),
		// Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d)))), Times(Times(-4, a,
		// Plus(Power(b, 2), Times(-2, a, c)), Plus(Times(4, Power(b, 9), d),
		// Times(-42, a, Power(b, 7), c, d), Times(162, Power(a, 2), Power(b,
		// 5), Power(c, 2), d), Times(-272, Power(a, 3), Power(b, 3), Power(c,
		// 3), d), Times(168, Power(a, 4), b, Power(c, 4), d), Times(18,
		// Power(a, 2), Power(b, 6), Power(d, 2)), Times(-126, Power(a, 3),
		// Power(b, 4), c, Power(d, 2)), Times(288, Power(a, 4), Power(b, 2),
		// Power(c, 2), Power(d, 2)), Times(-216, Power(a, 5), Power(c, 3),
		// Power(d, 2))), Power(c, 2)), Power(Plus(Power(b, 2), Times(-2, a,
		// c)), 2), Power(Plus(Power(b, 2), Times(-2, a, c)), 2), Plus(Power(b,
		// 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a, c)),
		// Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a,
		// c)), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c), Times(24,
		// Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a, 3),
		// Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d)))), Times(Times(-1, Plus(Times(Power(b, 4), c), Times(-3, a,
		// Power(b, 3), d)), Plus(Times(4, Power(b, 9), d), Times(-42, a,
		// Power(b, 7), c, d), Times(162, Power(a, 2), Power(b, 5), Power(c, 2),
		// d), Times(-272, Power(a, 3), Power(b, 3), Power(c, 3), d), Times(168,
		// Power(a, 4), b, Power(c, 4), d), Times(18, Power(a, 2), Power(b, 6),
		// Power(d, 2)), Times(-126, Power(a, 3), Power(b, 4), c, Power(d, 2)),
		// Times(288, Power(a, 4), Power(b, 2), Power(c, 2), Power(d, 2)),
		// Times(-216, Power(a, 5), Power(c, 3), Power(d, 2)))),
		// Power(Plus(Power(b, 2), Times(-2, a, c)), 2), Power(Plus(Power(b, 2),
		// Times(-2, a, c)), 2), Plus(Power(b, 2), Times(-2, a, c)),
		// Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a,
		// c)), Plus(Power(b, 2), Times(-2, a, c)), Times(Plus(Power(b, 8),
		// Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d))), Times(Plus(Power(b,
		// 8), Times(-8, a, Power(b, 6), c), Times(24, Power(a, 2), Power(b, 4),
		// Power(c, 2)), Times(-32, Power(a, 3), Power(b, 2), Power(c, 3)),
		// Times(16, Power(a, 4), Power(c, 4))), Plus(Times(-1, Power(b, 2), c),
		// Times(4, a, Power(c, 2)), Times(-3, a, b, d)))), Times(Times(-1,
		// Plus(Times(-2, a, Power(b, 2), Power(c, 2)), Times(6, Power(a, 2), b,
		// c, d)), Plus(Times(4, Power(b, 9), d), Times(-42, a, Power(b, 7), c,
		// d), Times(162, Power(a, 2), Power(b, 5), Power(c, 2), d), Times(-272,
		// Power(a, 3), Power(b, 3), Power(c, 3), d), Times(168, Power(a, 4), b,
		// Power(c, 4), d), Times(18, Power(a, 2), Power(b, 6), Power(d, 2)),
		// Times(-126, Power(a, 3), Power(b, 4), c, Power(d, 2)), Times(288,
		// Power(a, 4), Power(b, 2), Power(c, 2), Power(d, 2)), Times(-216,
		// Power(a, 5), Power(c, 3), Power(d, 2)))), Power(Plus(Power(b, 2),
		// Times(-2, a, c)), 2), Power(Plus(Power(b, 2), Times(-2, a, c)), 2),
		// Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2, a,
		// c)), Plus(Power(b, 2), Times(-2, a, c)), Plus(Power(b, 2), Times(-2,
		// a, c)), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d))), Times(Plus(Power(b, 8), Times(-8, a, Power(b, 6), c),
		// Times(24, Power(a, 2), Power(b, 4), Power(c, 2)), Times(-32, Power(a,
		// 3), Power(b, 2), Power(c, 3)), Times(16, Power(a, 4), Power(c, 4))),
		// Plus(Times(-1, Power(b, 2), c), Times(4, a, Power(c, 2)), Times(-3,
		// a, b, d)))))\n"
		// + ")", "");
	}

	public void testAbs() {
		check("Abs(-x)", "Abs(x)");
		check("Abs(Conjugate(z))", "Abs(z)");
		check("Abs(3*a*b*c)", "3*Abs(a*b*c)");
		// check("Abs(x^(-3))", "1/Abs(x)^3");

		check("Abs((1+I)/Sqrt(2))", "1");
		check("Abs(0)", "0");
		check("Abs(10/3)", "10/3");
		check("Abs(-10/3)", "10/3");
		check("Abs(Indeterminate)", "Indeterminate");
		check("Abs(Infinity)", "Infinity");
		check("Abs(-1*Infinity)", "Infinity");
		check("Abs(ComplexInfinity)", "Infinity");
		check("Abs(I*Infinity)", "Infinity");
		check("Abs(Sqrt(Pi))", "Sqrt(Pi)");
		check("Abs(-3*Sqrt(Pi))", "3*Sqrt(Pi)");
	}

	public void testAbsArg() {
		check("AbsArg(z)", "{Abs(z),Arg(z)}");
		check("AbsArg(2*z)", "{2*Abs(z),Arg(2*z)}");
		check("AbsArg({a, {b, c}})", "{{Abs(a),Arg(a)},{{Abs(b),Arg(b)},{Abs(c),Arg(c)}}}");
		check("AbsArg({{1, -1, 0}, {0, 1}})", "{{{1,0},{1,Pi},{0,0}},{{0,0},{1,0}}}");

		check("AbsArg(Gamma(-1/2))", "{2*Sqrt(Pi),Pi}");

		check("AbsArg({1, I, 0})", "{{1,0},{1,Pi/2},{0,0}}");
		check("AbsArg(z) /. z -> {1, I, 0}", "{{1,1,0},{0,Pi/2,0}}");
	}

	public void testAllTrue() {
		check("AllTrue({1, 2, 3, 4, 5, 6}, EvenQ)", "False");
		check("AllTrue({2, 4, 6, 8}, EvenQ)", "True");
		check("AllTrue({2, 6, x, 4, y}, # < 10 &)", "x<10&&y<10");
		check("AllTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", "False");
		check("AllTrue(f(1, 7, 3), OddQ)", "True");
	}

	public void testAnd() {
		check("And()", "True");
		check("And(4)", "4");
		check("2 > 1 && Pi > 3", "True");
		check("a && b && ! c", "a&&b&&!c");
		check("x + 2 y == 3 && 4 x + 5 y == 6", "x+2*y==3&&4*x+5*y==6");
		check("FullForm( And(x, And(y, z)) )", "\"And(x, y, z)\"");
		check("And(x, True, z)", "x&&z");
		check("And(x, False, z)", "False");
		check("BooleanConvert(! (a && b))", "!a||!b");
		check("BooleanConvert(! (a || b || c))", "!a&&!b&&!c");
	}

	public void testAnyTrue() {
		check("AnyTrue({1, 2, 3, 4, 5, 6}, EvenQ)", "True");
		check("AnyTrue({1, 3, 5}, EvenQ)", "False");
		check("AnyTrue({12, 16, x, 14, y}, # < 10 &)", "x<10||y<10");
		check("AnyTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", "False");
		check("AnyTrue(f(2, 7, 6), OddQ)", "True");
	}

	public void testApart() {
		check("Apart(1/((1 + x)*(5 + x)))", "1/(4+4*x)+1/(-20-4*x)");
		check("Apart(1 < (x + 1)/(x - 1) < 2)", "1<1+2/(-1+x)<2");
	}

	public void testAppendTo() {
		check("$l = {1, 2, 4, 9};appendto($l, 16)", "{1,2,4,9,16}");
		check("$l = {1, 2, 4, 9};appendto($l, 16);$l", "{1,2,4,9,16}");
	}

	public void testApply() {
		check("f @@ {{a, b}, {c}, d}", "f({a,b},{c},d)");
		check("apply(head, {3,4,5})", "Symbol");
		check("apply(f, a)", "a");
		check("apply(f, {a, \"string\", 3}, {-1})", "{a,\"string\",3}");
		check("table(i0^j, ##) & @@ {{i0, 3}, {j, 4}}", "{{1,1,1,1},{2,4,8,16},{3,9,27,81}}");

		check("apply(f, {{a, b, c}, {d, e}})", "f({a,b,c},{d,e})");
		check("apply(f, {{a, b, c}, {d, e}}, {1})", "{f(a,b,c),f(d,e)}");
		check("apply(f, {{a, b, c}, {d, e}}, {0, 1})", "f(f(a,b,c),f(d,e))");
		// Apply down to level 2 (excluding level 0):
		check("apply(f, {{{{{a}}}}}, 2)", "{f(f({{a}}))}");

		check("apply(f, {{{{{a}}}}}, {0, 2})", "f(f(f({{a}})))");
		check("apply(f, {{{{{a}}}}}, Infinity)", "{f(f(f(f(a))))}");
		check("apply(f, {{{{{a}}}}}, {0, Infinity})", "f(f(f(f(f(a)))))");

		check("apply(f, {{{{{a}}}}}, -1)", "{f(f(f(f(a))))}");
		check("apply(f, {{{{{a}}}}}, -2)", "{f(f(f(f(a))))}");
		check("apply(f, {{{{{a}}}}}, -3)", "{f(f(f({a})))}");

		check("apply(f, {{{{{a}}}}}, {2, -3})", "{{f(f({a}))}}");
		check("apply(f, h0(h1(h2(h3(h4(a))))), {2, -3})", "h0(h1(f(f(h4(a)))))");

		check("apply(f, p(x)[q(y)], {1})", "p(x)[f(y)]");
		check("apply(f, p(x)[q(y)], {1}, heads -> true)", "f(x)[f(y)]");

		// check("","");
		// check("","");
	}

	public void testArcCos() {
		check("arccos(-11)", "-Pi+ArcCos(11)");
		check("arccos(-x)", "-Pi+ArcCos(x)");
		check("D(ArcCos(x),x)", "-1/Sqrt(1-x^2)");
		check("diff(ArcCos(x),x)", "-1/Sqrt(1-x^2)");
	}

	public void testArcCosh() {
		check("ArcCosh(0)", "I*1/2*Pi");
		check("ArcCosh(-x)", "ArcCosh(-x)");
		check("D(ArcCosh(x),x)", "1/Sqrt(-1+x^2)");
		check("ArcCosh(-Infinity)", "ArcCosh(-Infinity)");
	}

	public void testArcCot() {
		check("arccot(complexinfinity)", "0");
		check("arccot(0)", "Pi/2");
		check("arccot(-11)", "-Pi+ArcCot(11)");
		check("arccot(-x)", "-Pi+ArcCot(x)");
		check("D(ArcCot(x),x)", "1/(-1-x^2)");
	}

	public void testArcCoth() {
		check("ArcCoth(0)", "I*1/2*Pi");
		check("ArcCoth(-x)", "-ArcCoth(x)");
		check("ArcCoth(-1)", "-Infinity");
		check("D(ArcCoth(x),x)", "1/(1-x^2)");
	}

	public void testArcCsc() {
		check("arccsc(0)", "ComplexInfinity");
		check("arccsc(-x)", "-ArcCsc(x)");
		check("D(ArcCsc(x),x)", "-1/(x^2*Sqrt(1-1/x^2))");
	}

	public void testArcCsch() {
		check("ArcCsch(-Infinity)", "0");
		check("arccsch(0)", "ComplexInfinity");
		check("arccsch(-x)", "-ArcCsch(x)");
		check("diff(ArcCsch(x),x)", "-1/(Sqrt(1+x^2)*Abs(x))");
	}

	public void testArcSec() {
		check("ArcSec(0)", "ComplexInfinity");
		check("ArcSec(-x)", "ArcSec(-x)");
		check("diff(ArcSec(x),x)", "1/(x^2*Sqrt(1-1/x^2))");
	}

	public void testArcSech() {
		check("ArcSech(0)", "Infinity");
		check("ArcSech(1)", "0");
		check("ArcSech(-x)", "ArcSech(-x)");
		check("ArcSech(-2)", "I*2/3*Pi");
		check("D(ArcSech(x),x)", "-1/(x*Sqrt(1-x^2))");
	}

	public void testArcSin() {
		check("arcsin(-11)", "-ArcSin(11)");
		check("arcsin(-x)", "-ArcSin(x)");
		check("diff(ArcSin(x),x)", "1/Sqrt(1-x^2)");
	}

	public void testArcSinh() {
		// check("ArcSinh(0)", "0");
		// check("ArcSinh(-x)", "-ArcSinh(x)");
		check("diff(ArcSinh(x),x)", "1/Sqrt(1+x^2)");
	}

	public void testArcTan() {
		check("Abs( ArcTan(ComplexInfinity) )", "Pi/2");
		check("arctan(infinity)", "Pi/2");
		check("arctan(1)", "Pi/4");
		check("arctan(-11)", "-ArcTan(11)");
		check("arctan(-x)", "-ArcTan(x)");
		check("arctan(1,1)", "Pi/4");
		check("arctan(-1,-1)", "-3/4*Pi");
		check("arctan(1.0,1.0)", "0.7853981633974483");
		check("N(1/4*pi)", "0.7853981633974483");
		check("D(ArcTan(x),x)", "1/(1+x^2)");
	}

	public void testArcTanh() {
		check("ArcTanh(0)", "0");

		check("ArcTanh(Infinity)", "-I*1/2*Pi");
		check("ArcTanh(I*Infinity)", "I*1/2*Pi");
		check("ArcTanh(ComplexInfinity)", "Pi/2");

		check("ArcTanh(-x)", "-ArcTanh(x)");
		check("D(ArcTanh(x),x)", "1/(1-x^2)");
	}

	public void testArg() {
		check("Arg(Pi)", "0");
		check("Arg(-Pi*E)", "Pi");
		check("Arg(1.3)", "0");
		check("Arg(0)", "0");
		check("Arg(1)", "0");
		check("Arg(-1)", "Pi");
		check("Arg(I)", "Pi/2");
		check("Arg(1+I)", "Pi/4");
		check("Arg(-I)", "-Pi/2");
		check("Arg(-2*Sqrt(Pi))", "Pi");
		check("Arg(Indeterminate)", "Indeterminate");
		check("Arg(0)", "0");
		check("Arg(10/3)", "0");
		check("Arg(-10/3)", "Pi");
		check("Arg(I*Infinity)", "Pi/2");
		check("Arg(-I*Infinity)", "-Pi/2");
		check("Arg(ComplexInfinity)", "Interval({-Pi,Pi})");
	}

	public void testArray() {
		check("Array(f, 4)", "{f(1),f(2),f(3),f(4)}");
		check("Array(f, {2, 3})", "{{f(1,1),f(1,2),f(1,3)},{f(2,1),f(2,2),f(2,3)}}");
		check("Array(f, {2, 3}, {4, 6})", "{{f(4,6),f(4,7),f(4,8)},{f(5,6),f(5,7),f(5,8)}}");
	}

	public void testArrayDepth() {
		check("ArrayDepth({{1, 2}, {3, 4}})", "2");
		check("ArrayDepth({1, 2, 3, 4})", "1");
		check("ArrayDepth({{a, b}, {c}})", "1");
		check("ArrayDepth(f(f(a, b), f(c, d)))", "2");
		check("ArrayDepth(Array(a, {4, 5, 2}))", "3");
	}

	public void testArrayQ() {
		check("ArrayQ({1, 2, 3, 4})", "True");
		check("ArrayQ({1, 2, {3}, 4})", "False");
		check("ArrayQ({{1, 2}, {3}})", "False");
		check("ArrayQ({{1, 2}, {3, 4}})", "True");
		check("ArrayQ({1, 2, 3, 4}, 2)", "False");
		check("ArrayQ({{1, 2}, {3, 4}},2)", "True");
		check("ArrayQ({1, 2, 3, x}, 1, NumericQ)", "False");
		check("ArrayQ({1, 2, 3, 4}, 1, NumericQ)", "True");
		check("ArrayQ[{{{E, 1}, {Pi, 2}}, {{Sin(1), Cos(2)}, {Sinh(1), Cosh(1)}}}, _, NumericQ]", "True");
		check("ArrayQ({1, 2., E, Pi + I}, 1)", "True");
		check("ArrayQ({{1,2},{3,4}},2,NumericQ)", "True");
	}

	public void testAttributes() {
		check("Attributes(Plus)", "{Flat,Listable,OneIdentity,Orderless,NumericFunction}");
	}

	public void testBernoulliB() {
		check("BernoulliB(2)", "1/6");
		check("Table(BernoulliB(k), {k, 0, 10})", "{1,-1/2,1/6,0,-1/30,0,1/42,0,-1/30,0,5/66}");
	}

	public void testBesselJ() {
		check("BesselJ(1.0, -3.0)", "BesselJ(1.0,-3.0)");
		check("BesselJ(0.0, 0.0)", "1.0");
		check("BesselJ(4.0, 0.0)", "-0.3971498098638474");
		// commons math: Bessel function of order 0 cannot be computed for x =
		// -3
		check("BesselJ(-3.0, 0.0)", "BesselJ(-3.0,0.0)");

		check("BesselJ(-3, 0)", "0");
		check("BesselJ(0, 0)", "1");
		check("BesselJ(4, 0)", "0");
	}

	public void testBinomial() {
		check("Binomial(n0, 2)", "1/2*n0*(-1+n0)");

		check("Binomial(k/3, k)", "Binomial(k/3,k)");
		check("Binomial(0, 0)", "1");
		check("Binomial(1000, 500)",
				"2702882409454365695156146936259752754961520084465482870073928751066254287055221\\\n"
						+ "9389861248392450237016536260608502154610480220975005067991754989421969951847542\\\n"
						+ "3665484263751733356162464079737887344364574161119497604571044985756287880514600\\\n"
						+ "994219426752366915856603136862602484428109296905863799821216320");
		check("Binomial(5, 3)", "10");
		check("Binomial(n0, n0)", "1");
		check("Binomial(n0, 0)", "1");
		check("Binomial(n0, n0-1)", "n0");
		check("Binomial(n0, 1)", "n0");
		check("Binomial(n0, 2)", "1/2*n0*(-1+n0)");
		check("Binomial(n0, 3)", "1/6*n0*(-2+n0)*(-1+n0)");
		// check("Binomial(-3, -5)", "0");

		check("Binomial(2+k, k)", "Binomial(2+k,k)");
		check("Binomial(k, 2)", "1/2*k*(-1+k)");
		check("Binomial(k, 5)", "1/120*k*(-4+k)*(-3+k)*(-2+k)*(-1+k)");
		check("Binomial(k, 6)", "Binomial(k,6)");
	}

	public void testBoole() {
		check("{Boole(False), Boole(True)}", "{0,1}");
		check("Boole({True, False, True, True, False})", "{1,0,1,1,0}");
		check("Boole({a, False, b, True, f()})", "{Boole(a),0,Boole(b),1,Boole(f())}");
	}

	public void testBooleanConvert() {
		check("BooleanConvert(Xor(x,y))", "x&&!y||!x&&y");
	}

	public void testBooleanMinimize() {
		check("BooleanMinimize(a && b || ! a && b)", "b");
	}

	public void testBooleanQ() {
		check("BooleanQ(True)", "True");
		check("BooleanQ(False)", "True");
		check("BooleanQ(f(x))", "False");
	}

	public void testBooleanTable() {
		check("BooleanTable(p || q, {p, q})", "{True,True,True,False}");
	}

	public void testBooleanVariables() {
		check("BooleanVariables(a || ! b && b)", "{a,b}");
		check("BooleanVariables(Xor(a, And(b, Or(c, d))))", "{a,b,c,d}");
		check("BooleanVariables(a && b || ! a && b)", "{a,b}");

		check("BooleanVariables(a + b*c)", "{}");
	}

	public void testCancel() {
		check("Cancel((x - a)/(x^2 - a^2) == 0 && (x^2 - 2*x + 1)/(x - 1) >= 0)", "1/(a+x)==0&&x>=1");
		check("9+3*x+x^2", "9+3*x+x^2");
		check("(9+3*x+x^2)*(3+x)^(-1)", "(9+3*x+x^2)/(3+x)");
		check("1+(9+3*x+x^2)*(3+x)^(-1)+x+(x+y)^(-1)", "1+x+(9+3*x+x^2)/(3+x)+1/(x+y)");

		check("Cancel(x / x ^ 2)", "1/x");
		check("Cancel(f(x) / x + x * f(x) / x ^ 2)", "(2*f(x))/x");
		check("Cancel(x / x ^ 2 + y / y ^ 2)", "1/x+1/y");
		check("Cancel((x^2 - 1)/(x - 1))", "1+x");
		check("Cancel((x - y)/(x^2 - y^2) + (x^3 - 27)/(x^2 - 9) + (x^3 + 1)/(x^2 - x + 1))",
				"1+x+(9+3*x+x^2)/(3+x)+1/(x+y)");
		check("cancel((x - 1)/(x^2 - 1) + (x - 2)/(x^2 - 4))", "1/(1+x)+1/(2+x)");
		check("together((x - 1)/(x^2 - 1) + (x - 2)/(x^2 - 4))", "(3+2*x)/(2+3*x+x^2)");

	}

	public void testCarmichaelLambda() {
		check("CarmichaelLambda(0)", "0");
		check("CarmichaelLambda(1)", "1");
		check("CarmichaelLambda(2)", "1");
		check("CarmichaelLambda(10)", "4");
		check("CarmichaelLambda(15)", "4");
		check("CarmichaelLambda(11)", "10");
		check("CarmichaelLambda(50)", "20");
	}

	public void testCases() {
		check("Cases(_Complex)[{1, 2I, 3, 4-I, 5}]", "{I*2,4-I}");
		check("Cases({x, a, b, x, c}, Except(x))", "{a,b,c}");
		check("Cases({a, 0, b, 1, c, 2, 3}, Except(1, _Integer))", "{0,2,3}");
		check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer)", "{1,1,2,3,9}");
		check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -1)", "{1,1,2,3,8,9,10}");
		check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -2)", "{}");
		check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, {0,4})", "{1,1,2,3,8,9,10}");

		check("Cases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x)", "{a,10}");
		check("Cases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x, -2)", "{a,8,10}");

		check("Cases({3, -4, 5, -2}, x_ /; x < 0)", "{-4,-2}");
		check("Cases({3, 4, x, x^2, x^3}, x^_)", "{x^2,x^3}");
		check("Cases({3, 4, x, x^2, x^3}, x^n_ -> n)", "{2,3}");
		check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {_, _})", "{{1,2},{5,4},{3,3}}");
		check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {a_, b_} -> a + b)", "{3,9,6}");
		check("Cases(Sqrt(Range(100)), _Integer, {1}, 3)", "{1,2,3}");
	}

	public void testCatch() {
		check("Catch(Scan(If(IntegerQ(#1),Null,Throw(False))&,{2,3});True)", "True");
		check("Catch(Scan(If(IntegerQ(#1),Null,Throw(False))&,{b+a});True)", "False");
		check("Catch(Scan(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", "6");
		check("Catch(a; b; Throw(c); d; e)", "c");
		check("$f(x_) := If(x > 10, Throw(overflow), x!);Catch($f(2) + $f(11))", "overflow");
		check("$f(x_) := If(x > 10, Throw(overflow), x!);Catch($f(2) + $f(3))", "8");
		check("catch(do(If(i0! > 10^10, throw(i0)), {i0, 100}))", "14");
		check("Catch(If(# < 0, Throw(#)) & /@ {1, 2, 0, -1, 5, 6})", "-1");
		check("Catch(a^2 + b^2 + c^2 /. b :> Throw(bbb))", "bbb");
		check("Catch({Catch({a, Throw(b), c}), d, e})", "{b,d,e}");
		check("Catch(Throw /@ {a, b, c})", "a");
		check("$f(x_) := (If(x < 0, Throw(\"negative\")); Sqrt(x));Catch(Sum($f(i0), {i0, 5, -5, -1}))",
				"\"negative\"");
		// check("$lst={0,v1,n1};\n" +
		// " Catch(\n" +
		// " {Map(Function($lst=False;\n" +
		// " If($lst===False,Throw(False),$lst((1)))),\n" +
		// " u),$lst((2)),$lst((3))})","");
	}

	public void testCatenate() {
		check("Catenate({{1,2,3},{a,b,c},{4,5,6}})", "{1,2,3,a,b,c,4,5,6}");
	}

	public void testCeiling() {
		check("Ceiling(1.5)", "2");
		check("Ceiling(1.5 + 2.7 I)", "2+I*3");
	}

	public void testCentralMoment() {
		check("CentralMoment({1.1, 1.2, 1.4, 2.1, 2.4}, 4)", "0.10084512");
	}

	public void testCharacteristicPolynomial() {
		check("CharacteristicPolynomial({{a, b}, {c, d}}, x)", "-b*c+a*d-a*x-d*x+x^2");
		check("CharacteristicPolynomial({{1, 1, 1}, {1, 1/2, 1/3}, {1, 2, 3}},x)", "-1/3-7/3*x+9/2*x^2-x^3");
		check("CharacteristicPolynomial(N({{1, 1, 1}, {1, 1/2, 1/3}, {1, 2, 3}}),x)",
				"-0.33333333333333304-2.3333333333333335*x+4.5*x^2.0-x^3.0");
		check("CharacteristicPolynomial({{1, 2 I}, {3 + 4 I, 5}}, z)", "13-I*6-6*z+z^2");
	}

	public void testChebyshevT() {
		check("ChebyshevT(Indeterminate,0)", "Indeterminate");

		check("ChebyshevT(n,0)", "Cos(1/2*n*Pi)");
		check("ChebyshevT({0,1,2,3,4}, x)", "{1,x,-1+2*x^2,-3*x+4*x^3,1-8*x^2+8*x^4}");
		check("ChebyshevT({0,-1,-2,-3,-4}, x)", "{1,x,-1+2*x^2,-3*x+4*x^3,1-8*x^2+8*x^4}");
		check("ChebyshevT(10, x)", "-1+50*x^2-400*x^4+1120*x^6-1280*x^8+512*x^10");
	}

	public void testChebyshevU() {
		check("ChebyshevU(n, 1)", "1+n");
		check("ChebyshevU({0,1,2,3,4,5}, x)", "{1,2*x,-1+4*x^2,-4*x+8*x^3,1-12*x^2+16*x^4,6*x-32*x^3+32*x^5}");
		check("ChebyshevU(0, x)", "1");
		check("ChebyshevU(1, x)", "2*x");
		check("ChebyshevU(10, x)", "-1+60*x^2-560*x^4+1792*x^6-2304*x^8+1024*x^10");
	}

	public void testChineseRemainder() {
		check("ChineseRemainder({23},{17})", "6");
		check("ChineseRemainder({91},{25})", "16");
		check("ChineseRemainder({913},{25})", "13");
		check("ChineseRemainder({3,4},{4,5})", "19");

		check("ChineseRemainder({1, 2}, {6, 10})", "ChineseRemainder({1,2},{6,10})");
	}

	public void testCoefficient() {
		// check("Apply(Plus,((Coefficient(x*(b+a),x,#1)*x^#1)&))", "");

		check("Coefficient(Sin(a)^3*#1 + b*y + c, #1)", "Sin(a)^3");
		check("Coefficient((#1 + 2)^2 + (#1 + 3)^3, #1, 0)", "31");
		check("Coefficient(42*#1^2+y^3*#1^2+(#1 + 2)^2*(#1 + 2)^2,#1,2)", "66+y^3");
		check("Coefficient(#1,#1,1)", "1");
		check("Coefficient(#1^2,#1,2)", "1");

		check("Coefficient(Null,x,0)", "");
		check("Coefficient(Null,x)", "0");

		check("Coefficient(Sin(x^2),x^2)", "0");

		check("Coefficient(Sin(x^2)^2,Sin(x^2),2)", "1");
		check("Coefficient(2*Sin(x^2)^3,Sin(x^2),3)", "2");
		check("Coefficient(f(x)+2*Sin(x^2)^3,Sin(x^2),3)", "2");
		check("Coefficient(f(x^2)+2*f(x^2)^3,f(x^2),3)", "2");
		check("ExpandAll((x + y) (x + 2 y) (3 x + 4 y + 5))", "5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
		check("Coefficient(Sin(x^2),Sin(x^2))", "1");
		check("Coefficient(x*(b+a),x,1)*x^1", "(a+b)*x");
		check("Coefficient((x + 1)^3, x, 2)", "3");
		check("Coefficient(a x + b y + c, x)", "a");
		check("Coefficient(Sin(a)^3*x + b*y + c, x)", "Sin(a)^3");
		check("Coefficient(Sin(a*x)^3*x + b*y + c, x)", "Sin(a*x)^3");
		check("Coefficient((x + 2)^2 + (x + 3)^3, x, 0)", "31");
		check("Coefficient(v,x,1)", "0");
		check("Coefficient(42,x,0)", "42");
		check("Coefficient(42*a,x,0)", "42*a");
		check("Coefficient(x,x,1)", "1");
		check("Coefficient(x^2,x,2)", "1");
		check("Coefficient(42*x^2+y^3*x^2+(x + 2)^2*(x + 2)^2,x,2)", "66+y^3");
		check("Coefficient(2*x*a,x,1)", "2*a");
		check("Coefficient(2*x*a,x,2)", "0");
		check("Coefficient(2*x*a,x,3)", "0");
		check("Coefficient(2*x*a,x,4)", "0");
		check("Coefficient(2*x^2*a+x,x,1)", "1");
		check("Coefficient(2*x^2*a,x,2)", "2*a");
		check("Coefficient(2*x^3*a,x,3)", "2*a");
		check("Coefficient(2*x^4*a,x,4)", "2*a");
		check("Coefficient(0,x,0)", "0");

		// allow multinomials
		check("ExpandAll((x + y)^4)", "x^4+4*x^3*y+6*x^2*y^2+4*x*y^3+y^4");
		check("Coefficient((x + y)^4, x y^3)", "4");
		check("Coefficient((x + y)^4,  y^4)", "1");
		check("Coefficient((x + y)^4,  y,4)", "1");

		check("Expand((x + y)*(x + 2*y)*(3*x + 4*y + 5))", "5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
		check("ExpandAll((x + y)*(x + 2*y)*(3*x + 4*y + 5))", "5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
		check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x^2*y)", "13");
		check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x*y^2)", "18");
		check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x*y)", "15");
		check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), y^3)", "8");
	}

	public void testCoefficientList() {
		check("CoefficientList(0, x)", "{}");
		check("CoefficientList((x+3)^5, x)", "{243,405,270,90,15,1}");
		check("CoefficientList(1 + 6 x - x^4, x)", "{1,6,0,0,-1}");
		check("CoefficientList((1 + x)^10 , x)", "{1,10,45,120,210,252,210,120,45,10,1}");
		check("CoefficientList(a*42*x^3+12*b*x+c*4, x)", "{4*c,12*b,0,42*a}");
		check("CoefficientList((1.0 + x)^10 , x)", "{1.0,10.0,45.0,120.0,210.0,252.0,210.0,120.0,45.0,10.0,1}");
	}

	public void testCoefficientRules() {
		check("CoefficientRules((x + y)^3)", "{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");
		check("CoefficientRules( a x y^2 + b x^2 z, {x, y, z}, \"DegreeReverseLexicographic\")",
				"{{1,2,0}->a,{2,0,1}->b}");

		check("CoefficientRules((x + y)^3)", "{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");
		// check("CoefficientRules(x^2 y^2 + x^3, {x, y})", "{x^3,x^2*y^2}");
		// check("CoefficientRules(x^2 y^2 + x^3, {x,
		// y},\"DegreeLexicographic\")", "{x^2*y^2,x^3}");
		check("CoefficientRules((x + 1)^5, x, Modulus -> 2)", "{{5}->1,{4}->1,{1}->1,{0}->1}");

		check("CoefficientRules(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z})",
				"{{5,4,2}->-10,{2,5,3}->7,{2,1,5}->-10,{1,5,4}->-7,{1,4,3}->6,{1,3,3}->6,{1,2,1}->\n"
						+ "3,{0,4,1}->1,{0,2,1}->-7,{0,0,5}->2}");

		check("CoefficientRules(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"NegativeLexicographic\")",
				"{{0,0,5}->2,{0,2,1}->-7,{0,4,1}->1,{1,2,1}->3,{1,3,3}->6,{1,4,3}->6,{1,5,4}->-7,{\n"
						+ "2,1,5}->-10,{2,5,3}->7,{5,4,2}->-10}");
		check("CoefficientRules(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"DegreeLexicographic\")",
				"{{5,4,2}->-10,{2,5,3}->7,{1,5,4}->-7,{2,1,5}->-10,{1,4,3}->6,{1,3,3}->6,{0,4,1}->\n"
						+ "1,{0,0,5}->2,{1,2,1}->3,{0,2,1}->-7}");
		check("CoefficientRules(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"NegativeDegreeReverseLexicographic\")",
				"{{0,2,1}->-7,{1,2,1}->3,{0,4,1}->1,{0,0,5}->2,{1,3,3}->6,{1,4,3}->6,{2,1,5}->-10,{\n"
						+ "2,5,3}->7,{1,5,4}->-7,{5,4,2}->-10}");
		check("CoefficientRules(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 + 6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"DegreeReverseLexicographic\")",
				"{{5,4,2}->-10,{2,5,3}->7,{1,5,4}->-7,{1,4,3}->6,{2,1,5}->-10,{1,3,3}->6,{0,4,1}->\n"
						+ "1,{0,0,5}->2,{1,2,1}->3,{0,2,1}->-7}");
		check("CoefficientRules(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 + 6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"NegativeDegreeLexicographic\")",
				"{{0,2,1}->-7,{1,2,1}->3,{0,4,1}->1,{0,0,5}->2,{1,3,3}->6,{2,1,5}->-10,{1,4,3}->6,{\n"
						+ "2,5,3}->7,{1,5,4}->-7,{5,4,2}->-10}");
	}

	public void testCollect() {
		// check("Collect(D(f(Sqrt(x^2 + 1)), {x, 3}), Derivative(_)[f][_],
		// Together)", "");
		check("x*(4*a^3+12*a^2+12*a+4)+x^4+(4*a+4)*x^3+(6*a^2+12*a+6)*x^2+a^4+4*a^3+6*a^2+4*a+1",
				"1+4*a+6*a^2+4*a^3+a^4+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4+x*(4+12*a+12*a^2+4*a^3)");
		check("x+x^4", "x+x^4");
		check("Collect(a, x)", "a");
		check("Collect(a*y, {x,y})", "a*y");
		check("Collect(42*a, {x,y})", "42*a");
		check("Collect(a Sqrt(x) + Sqrt(x) + x^(2/3) - c*x + 3*x - 2*b*x^(2/3) + 5, x)",
				"5+x*(3-c)+(1+a)*Sqrt(x)+(1-2*b)*x^(2/3)");
		check("Collect(3 b x + x, x)", "x*(1+3*b)");
		check("Collect(a x^4 + b x^4 + 2 a^2 x - 3 b x + x - 7, x)", "-7+x*(1+2*a^2-3*b)+(a+b)*x^4");
		check("Collect((1 + a + x)^4, x)",
				"1+4*a+6*a^2+4*a^3+a^4+x*(4+12*a+12*a^2+4*a^3)+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4");
		check("Collect((1 + a + x)^4, x, Simplify)", "4*x*(1+a)^3+(1+a)^4+6*(1+a)^2*x^2+(4+4*a)*x^3+x^4");

		check("Collect(a x + b y + c x, x)", "(a+c)*x+b*y");
		check("Collect((x + y + z + 1)^4, {x, y})",
				"1+(6+6*y^2+12*z+y*(12+12*z)+6*z^2)*x^2+(4+4*y+4*z)*x^3+x^4+4*y+6*y^2+4*y^3+y^4+4*z+\n"
						+ "12*y*z+12*y^2*z+4*y^3*z+x*(4+(12+12*z)*y^2+4*y^3+12*z+y*(12+24*z+12*z^2)+12*z^2+\n"
						+ "4*z^3)+6*z^2+12*y*z^2+6*y^2*z^2+4*z^3+4*y*z^3+z^4");
	}

	public void testCommonest() {
		check("Commonest({b, a, c, 2, a, b, 1, 2}, 4)", "{b,a,2,c}");
		check("Commonest({b, a, c, 2, a, b, 1, 2})", "{b,a,2}");
		check("Commonest({1, 2, 2, 3, 3, 3, 4})", "{3}");
	}

	public void testComplexExpand() {
		check("ComplexExpand(Cos(x+I*y))", "Cos(x)*Cosh(y)+I*Sin(x)*Sinh(y)");
		check("ComplexExpand(Sin(x+I*y))", "Cosh(y)*Sin(x)+I*Cos(x)*Sinh(y)");
		check("ComplexExpand(Cot(x+I*y))", "-Sin(2*x)/(-Cosh(2*y)+Cos(2*x))+(I*Sinh(2*y))/(-Cosh(2*y)+Cos(2*x))");
		check("ComplexExpand(Csc(x+I*y))",
				"(-2*Cosh(y)*Sin(x))/(-Cosh(2*y)+Cos(2*x))+(I*2*Cos(x)*Sinh(y))/(-Cosh(2*y)+Cos(2*x))");
		check("ComplexExpand(Sec(x+I*y))",
				"(2*Cos(x)*Cosh(y))/(Cos(2*x)+Cosh(2*y))+(I*2*Sin(x)*Sinh(y))/(Cos(2*x)+Cosh(2*y))");
		check("ComplexExpand(Tan(x+I*y))", "Sin(2*x)/(Cos(2*x)+Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)+Cosh(2*y))");
		check("ComplexExpand(Cos(x))", "Cos(x)");
		check("ComplexExpand(Sin(x))", "Sin(x)");
		check("ComplexExpand(Cot(x))", "-Sin(2*x)/(-1+Cos(2*x))");
		check("ComplexExpand(Csc(x))", "(-2*Sin(x))/(-1+Cos(2*x))");
		check("ComplexExpand(Sec(x))", "(2*Cos(x))/(1+Cos(2*x))");
		check("ComplexExpand(Tan(x))", "Sin(2*x)/(1+Cos(2*x))");
	}

	public void testCompoundExpression() {
		check("Catch[$a = 2; Throw[$a]; $a = 5]", "2");
	}

	public void testConjugate() {
		check("Conjugate(1-I)", "1+I");
		check("Conjugate(1+I)", "1-I");
		check("Conjugate(Conjugate(x))", "x");
		check("Conjugate(3*a*z)", "3*Conjugate(a*z)");
		check("Conjugate(E^z)", "E^Conjugate(z)");
		check("Conjugate(Pi)", "Pi");
		check("Conjugate(0)", "0");
		check("Conjugate(I)", "-I");
		check("Conjugate(Indeterminate)", "Indeterminate");
		check("Conjugate(Infinity)", "Infinity");
		check("Conjugate(-Infinity)", "-Infinity");
		check("Conjugate(ComplexInfinity)", "ComplexInfinity");
		check("Conjugate(Transpose({{1,2+I,3},{4,5-I,6},{7,8,9}}))", "{{1,4,7},{2-I,5+I,8},{3,6,9}}");
		check("Conjugate(Zeta(x))", "Zeta(Conjugate(x))");
		check("Conjugate(Zeta(11,7))", "Zeta(11,7)");
	}

	public void testConjugateTranspose() {
		check("ConjugateTranspose({{1,2+I,3},{4,5-I,6},{7,8,9}})", "{{1,4,7},\n" + " {2-I,5+I,8},\n" + " {3,6,9}}");
		check("ConjugateTranspose(N({{1,2+I,3},{4,5-I,6},{7,8,9}}))",
				"{{1.0,4.0,7.0},\n" + " {2.0+I*(-1.0),5.0+I*1.0,8.0},\n" + " {3.0,6.0,9.0}}");

		check("ConjugateTranspose({{1, 2 I, 3}, {3 + 4 I, 5, I}})", "{{1,3-I*4},\n" + " {-I*2,5},\n" + " {3,-I}}");
	}

	public void testConstantArray() {
		check("ConstantArray(c, 10)", "{c,c,c,c,c,c,c,c,c,c}");
		check("ConstantArray(c, {3, 4})", "{{c,c,c,c},{c,c,c,c},{c,c,c,c}}");
	}

	public void testContinuedFraction() {
		check("ContinuedFraction(Pi,10)", "{3,7,15,1,292,1,1,1,2,1}");
		check("ContinuedFraction(47/17)", "{2,1,3,4}");
		check("ContinuedFraction(Sqrt(13),20)", "{3,1,1,1,1,6,1,1,1,1,6,1,1,1,1,6,1,1,1,1}");
	}

	public void testCoprimeQ() {
		check("CoprimeQ(8,9,11)", "True");
		check("CoprimeQ[{1, 2, 3, 4, 5}, 6]", "{True,False,False,False,True}");
	}

	public void testCos() {
		check("Cos(z+1/2*Pi)", "-Sin(z)");
		check("Cos(Pi)", "-1");
		check("Cos(z+Pi)", "-Cos(z)");
		check("Cos(z+42*Pi)", "Cos(z)");
		check("Cos(x+y+z+43*Pi)", "-Cos(x+y+z)");
		check("Cos(z+42*a*Pi)", "Cos(42*a*Pi+z)");
	}

	public void testCosh() {
		check("Cosh(0)", "1");
		check("Cosh(1/6*Pi*I)", "Sqrt(3)/2");
		check("Cosh(Infinity)", "Infinity");
		check("Cosh(ComplexInfinity)", "Indeterminate");
	}

	public void testCosineDistance() {
		check("CosineDistance({a, b}, {x, y})", "1-(a*x+b*y)/(Sqrt(Abs(a)^2+Abs(b)^2)*Sqrt(Abs(x)^2+Abs(y)^2))");
		check("CosineDistance({a, b, c}, {x, y, z})",
				"1-(a*x+b*y+c*z)/(Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)*Sqrt(Abs(x)^2+Abs(y)^2+Abs(z)^\n" + "2))");
	}

	public void testCosIntegral() {
		check("CosIntegral(2.8)", "0.18648838964317638");
	}

	public void testCot() {
		check("Cot(z+1/2*Pi)", "-Tan(z)");
		check("Cot(Pi)", "ComplexInfinity");
		check("Cot(z+Pi)", "Cot(z)");
		check("Cot(z+42*Pi)", "Cot(z)");
		check("Cot(x+y+z+43*Pi)", "Cot(x+y+z)");
		check("Cot(z+42*a*Pi)", "Cot(42*a*Pi+z)");
	}

	public void testCount() {
		check("count({a, b, a, a, b, c, b}, b)", "3");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b)", "3");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, infinity)", "4");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, {1,2})", "3");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, {1,3})", "4");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, 3)", "4");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, {3})", "1");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, {-1})", "4");
		check("count({3, 4, x, x^2, x^3}, x^_)", "2");
	}

	public void testCovariance() {
		check("Covariance({a, b, c,d,e}, {x, y, z,v,w})",
				"1/20*(-(a+b+c-4*d+e)*Conjugate(v)-(a+b+c+d-4*e)*Conjugate(w)-(-4*a+b+c+d+e)*Conjugate(x)-(a\n"
						+ "-4*b+c+d+e)*Conjugate(y)-(a+b-4*c+d+e)*Conjugate(z))");
		check("Covariance({a, b, c}, {x, y, z})",
				"1/6*(-(-2*a+b+c)*Conjugate(x)-(a-2*b+c)*Conjugate(y)-(a+b-2*c)*Conjugate(z))");
		check("Covariance({a, b}, {x, y})", "1/2*(a-b)*(-Conjugate(y)+Conjugate(x))");
		check("Covariance({1.5, 3, 5, 10}, {2, 1.25, 15, 8})", "11.260416666666666");
	}

	public void testCross() {
		check("Cross({a, b, c}, {x, y, z})", "{-c*y+b*z,c*x-a*z,-b*x+a*y}");
		check("Cross({x, y})", "{-y,x}");

		check("Cross({1,2,3},{1,1/2,1/3})", "{-5/6,8/3,-3/2}");
		check("Cross(N({1,2,3}),N({1,1/2,1/3}))", "{-0.8333333333333334,2.6666666666666665,-1.5}");
	}

	public void testCsc() {
		check("Csc(0)", "ComplexInfinity");
		check("Csc(2/5*Pi)", "Sqrt(2-2/Sqrt(5))");
		check("Csc(23/12*Pi)", "-2*Sqrt(2+Sqrt(3))");
		check("Csc(z+1/2*Pi)", "Sec(z)");
		check("Csc(Pi)", "ComplexInfinity");
		check("Csc(z+Pi)", "-Csc(z)");
		check("Csc(z+42*Pi)", "Csc(z)");
		check("Csc(x+y+z+43*Pi)", "-Csc(x+y+z)");
		check("Csc(z+42*a*Pi)", "Csc(42*a*Pi+z)");
	}

	public void testCsch() {
		check("Csch(-x)", "-Csch(x)");
		check("Csch(1.8)", "0.3398846914154937");
		check("D(Csch(x),x)", "-Coth(x)*Csch(x)");
	}

	public void testCurl() {
		check("Curl({f[x, y, z], g[x, y, z], h[x, y, z]}, {x, y, z})",
				"{-D(g(x,y,z),z)+D(h(x,y,z),y),-D(h(x,y,z),x)+D(f(x,y,z),z),-D(f(x,y,z),y)+D(g(x,y,z),x)}");
	}

	public void testD() {
		// Koepf Seite 40-43
		check("D(Sum(k*x^k, {k,0,10}),x)", "1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9");
		check("D((x^2+3)*(3*x+2),x)", "2*x*(2+3*x)+3*(3+x^2)");
		check("D(Sin(x^2),x)", "2*x*Cos(x^2)");
		check("D((1+x^2)^Sin(x),x)", "(Cos(x)*Log(1+x^2)+(2*x*Sin(x))/(1+x^2))*(1+x^2)^Sin(x)");
		check("D(Exp(x),x)", "E^x");
		check("D((x^2+3)/(3*x+2),x)", "(-3*(3+x^2))/(2+3*x)^2+(2*x)/(2+3*x)");

		// others -----
		check("D(InverseErf(x),x)", "1/2*E^InverseErf(x)^2*Sqrt(Pi)");

		check("D(f(Sqrt(x^2 + 1)), {x, 3})",
				"(3*x^3*f'(Sqrt(1+x^2)))/(1+x^2)^(5/2)+(-3*x*f'(Sqrt(1+x^2)))/(1+x^2)^(3/2)+(-3*x^\n"
						+ "3*f''(Sqrt(1+x^2)))/(1+x^2)^2+(3*x*f''(Sqrt(1+x^2)))/(1+x^2)+(x^3*Derivative(3)[f][Sqrt(\n"
						+ "1+x^2)])/(1+x^2)^(3/2)");
	}

	public void testDegree() {
		check("Round(Pi/Degree^2)", "10313");
		check("Pi/4 < 60 Degree < Pi", "True");
		check("FullSimplify(Pi/Degree)", "180");
	}

	public void testDelete() {
		check("Delete({a, b, c, d}, 3)", "{a,b,d}");
		check("Delete({a, b, c, d}, -2)", "{a,b,d}");
	}

	public void testDeleteCases() {
		check("Sqrt(Range(10))", "{1,Sqrt(2),Sqrt(3),2,Sqrt(5),Sqrt(6),Sqrt(7),2*Sqrt(2),3,Sqrt(10)}");

		check("DeleteCases(Sqrt(Range(10)), _Integer, {1}, 3)",
				"{Sqrt(2),Sqrt(3),Sqrt(5),Sqrt(6),Sqrt(7),2*Sqrt(2),Sqrt(10)}");

		check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer)", "{f(a),y,f(8),f(10)}");
		check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -1)", "{f(a),y,f(),f()}");
		check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -2)", "{1,1,f(a),2,3,y,f(8),9,f(10)}");
		check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, {0,4})", "{f(a),y,f(),f()}");

		check("DeleteCases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x)",
				"{1,1,f(a),2,3,y,g(c,f(8)),9,f(10)}");
		check("DeleteCases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x, -2)",
				"{1,1,f(a),2,3,y,g(c,f(8)),9,f(10)}");

		check("DeleteCases({1, 1, x, 2, 3, y, 9, y}, _Integer)", "{x,y,y}");

		//
		// check("Cases({3, -4, 5, -2}, x_ /; x < 0)", "{-4,-2}");
		// check("Cases({3, 4, x, x^2, x^3}, x^_)", "{x^2,x^3}");
		// check("Cases({3, 4, x, x^2, x^3}, x^n_ -> n)", "{2,3}");
		// check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {_, _})",
		// "{{1,2},{5,4},{3,3}}");
		// check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {a_, b_} -> a
		// + b)", "{3,9,6}");

	}

	public void testDeleteDuplicates() {
		check("DeleteDuplicates({1, 7, 8, 4, 3, 4, 1, 9, 9, 2, 1})", "{1,7,8,4,3,9,2}");
		check("DeleteDuplicates({3,2,1,2,3,4}, Less)", "{3,2,1}");
		check("DeleteDuplicates({3,2,1,2,3,4}, Greater)", "{3,3,4}");
		check("DeleteDuplicates({})", "{}");
	}

	public void testDenominator() {
		check("Denominator(Csc(x))", "1");
		check("Denominator(Csc(x), Trig->True)", "Sin(x)");
		check("Denominator(Csc(x)^4)", "1");
		check("Denominator(Csc(x)^4, Trig->True)", "Sin(x)^4");
		check("Denominator(42*Csc(x))", "1");
		check("Denominator(42*Csc(x), Trig->True)", "Sin(x)");
		check("Denominator(42*Csc(x)^3)", "1");
		check("Denominator(42*Csc(x)^3, Trig->True)", "Sin(x)^3");
		check("Denominator(E^(-x)*x^(1/2))", "E^x");

		check("Denominator(Sec(x))", "1");
		check("Denominator(Tan(x))", "1");
		check("Denominator(Tan(x), Trig->True)", "Cos(x)");
	}

	public void testDepth() {
		check("Depth(a)", "1");
		check("Depth(g(a))", "2");
		check("Depth({{{a}, b}})", "4");
	}

	public void testDerivative() {
		check("D(f(a,b),b)", "Derivative(0,1)[f][a,b]");
		check("D(f(a,b),x)", "0");
		check("g(u0_,u1_):=D(f(u0,u1),u1);g(a,b)", "Derivative(0,1)[f][a,b]");
		check("Derivative(1)[ArcCoth]", "1/(1-#1^2)&");
		check("y''", "Derivative(2)[y]");
		check("y''(x)", "y''(x)");
		check("y''''(x)", "Derivative(4)[y][x]");

		check("x*x^a", "x^(1+a)");
		check("x/x^(1-x)", "x^x");
		check("Derivative(0,1)[BesselJ][a, x]", "1/2*(-BesselJ(1+a,x)+BesselJ(-1+a,x))");
		check("Derivative(1,0)[Power][x, 4]", "4*x^3");
		check("Derivative(1,0)[Power][x, y]", "y/x^(1-y)");
		check("Derivative(1,1)[Power][x, 4]", "x^3+4*x^3*Log(x)");
		check("Derivative(1,1)[Power][x, y]", "x^(-1+y)+(y*Log(x))/x^(1-y)");
		check("Derivative(0,1)[Power][a, x]", "a^x*Log(a)");
		check("Derivative(1,1)[Power][a, x]", "a^(-1+x)+(x*Log(a))/a^(1-x)");
		check("Derivative(1,1)[Power][x, x]", "x^(-1+x)+x^x*Log(x)");

		check("Hold((-1)*Sin(#)&[x])", "-Sin(#1)&[x]");
		check("Hold(Derivative(1)[Cos][x])", "Cos'(x)");
		check("Derivative(1)[Cos][x]", "-Sin(x)");
		check("Derivative(1)[Sin][x]", "Cos(x)");
		check("Derivative(4)[Cos][x]", "Cos(x)");
		check("Derivative(1)[Tan]", "Sec(#1)^2&");
		check("Derivative(2)[Tan]", "2*Sec(#1)^2*Tan(#1)&");
		check("Derivative(4)[Log][x]", "-6/x^4");
		check("Derivative(2)[ArcSin][x]", "x/(1-x^2)^(3/2)");
	}

	public void testDesignMatrix() {
		check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, x, x)", "{{1,2},{1,3},{1,5},{1,7}}");
		check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, f(x), x)", "{{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}");
	}

	public void testDet() {
		check("Det({{a11, a12},{a21,a22}})", "-a12*a21+a11*a22");
		check("Det({{a,b,c},{d,e,f},{h,i,j}})", "-c*e*h+b*f*h+c*d*i-a*f*i-b*d*j+a*e*j");
	}

	public void testDiceDissimilarity() {
		check("DiceDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", "3/7");
		check("DiceDissimilarity({True, False, True}, {True, True, False})", "1/2");
		check("DiceDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", "0");
		check("DiceDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", "1");
	}

	public void testDigitQ() {
		check("DigitQ(\"1234\")", "True");
		check("DigitQ(\".\")", "False");
	}

	public void testDimensions() {
		check("Dimensions({a, b})", "{2}");
		check("Dimensions({{a, b, c}, {d, e, f}})", "{2,3}");
		check("Dimensions({{a, b, c}, {d, e}, {f}})", "{3}");
		check("Dimensions({{{{a, b}}}})", "{1,1,1,2}");
		check("Dimensions({{{{a, b}}}}, 2)", "{1,1}");
		check("Dimensions(f(f(x, y), f(a, b), f(s, t)))", "{3,2}");
		check("Dimensions(f(g(x, y), g(a, b), g(s, t)))", "{3}");
		check("Dimensions(Array(a, {2, 1, 4, 3}))", "{2,1,4,3}");
	}


	public void testDiracDelta() {
		check("DiracDelta(0)", "DiracDelta(0)");
		check("DiracDelta(42)", "0");
		check("DiracDelta(-1)", "0");
		check("DiracDelta(-42)", "0");
		check("DiracDelta({1.6, 1.6000000000000000000000000})", "{0,0}");
		check("DiracDelta({-1, 0, 1})", "{0,DiracDelta(0),0}");
		check("DiracDelta(1, 2, 3)", "0");
	}
	
	public void testDirectedInfinity() {
		check("DirectedInfinity(Indeterminate)", "ComplexInfinity");
		check("ComplexInfinity+b", "ComplexInfinity");
		// Power()
		check("0^(-1)", "ComplexInfinity");
		check("{Exp(Infinity), Exp(-Infinity)}", "{Infinity,0}");
		check("1^Infinity", "Indeterminate");
		check("1^(-Infinity)", "Indeterminate");
		check("1^ComplexInfinity", "Indeterminate");

		// Times()
		check("DirectedInfinity(x)*DirectedInfinity(y)", "DirectedInfinity(x*y)");
		check("Table(DirectedInfinity(i), {i, {1, -1, I, -I}})", "{Infinity,-Infinity,I*Infinity,-I*Infinity}");
		check("(1 + I) Infinity", "DirectedInfinity((1+I)/Sqrt(2))");
		check("{DirectedInfinity[], DirectedInfinity[Indeterminate]}", "{ComplexInfinity,ComplexInfinity}");
		check("Infinity/Infinity", "Indeterminate");
		check("3*DirectedInfinity(z)", "DirectedInfinity(z)");
		check("I DirectedInfinity(z)", "DirectedInfinity(I*z)");

		// Plus()

		check("1+1", "2");
		check("1+Infinity", "Infinity");
		check("1-Infinity", "-Infinity");
		check("Infinity+Infinity", "Infinity");
		check("-Infinity-Infinity", "-Infinity");
		check("Infinity-Infinity", "Indeterminate");
		check("1+Indeterminate", "Indeterminate");
		check("0+ComplexInfinity", "ComplexInfinity");
		check("ComplexInfinity+ComplexInfinity", "Indeterminate");
		check("ComplexInfinity+Indeterminate", "Indeterminate");
		check("1+ComplexInfinity", "ComplexInfinity");
		check("DirectedInfinity(x) + DirectedInfinity(y)", "DirectedInfinity(x)+DirectedInfinity(y)");
		check("DirectedInfinity(x) + DirectedInfinity(y) /. {x -> 1, y -> -1}", "Indeterminate");
	}

	public void testDiscriminant() {
		check("Discriminant(x^10 - 5 x^7 - 3 x + 9, x)", "177945374758153510836");

		check("Resultant(f+g*x+h*x^2,g+2*h*x, x)", "-g^2*h+4*f*h^2");
		check("Discriminant(x^(1/2), x)",
				"The function: Discriminant(Sqrt(x),x) has wrong argument Sqrt(x) at position:1:\n"
						+ "Polynomial expected!");

		check("Discriminant(f+g*x+h*x^2, x)", "g^2-4*f*h");
		check("Discriminant(a*x^2 + b*x + c, x)", "b^2-4*a*c");
		check("Discriminant(x^10 - 5 x^7 - 3 x + 9, x)", "177945374758153510836");
		check("Discriminant(a x^3 + b x^2 + c x + g, x)", "b^2*c^2-4*a*c^3-4*b^3*g+18*a*b*c*g-27*a^2*g^2");
		check("Discriminant(a x^4 + b x^3 + c x^2 + d x + e, x)",
				"b^2*c^2*d^2-4*a*c^3*d^2-4*b^3*d^3+18*a*b*c*d^3-27*a^2*d^4+16*a*c^3*e-4*b^2*c^3*e+\n"
						+ "18*b^3*c*d*e-80*a*b*c^2*d*e-6*a*b^2*d^2*e+144*a^2*c*d^2*e-27*b^4*e^2+144*a*b^2*c*e^\n"
						+ "2-128*a^2*c^2*e^2-192*a^2*b*d*e^2+256*a^3*e^3");
	}

	public void testDistribute() {
		check("Distribute((a + b).(x + y + z))", "a.x+a.y+a.z+b.x+b.y+b.z");
		check("Distribute(f(a + b, c + d + e))", "f(a,c)+f(a,d)+f(a,e)+f(b,c)+f(b,d)+f(b,e)");
		check("Distribute(f(g(a, b), g(c, d, e)), g)", "g(f(a,c),f(a,d),f(a,e),f(b,c),f(b,d),f(b,e))");
		check("Distribute((a + b + c)*(u + v), Plus, Times)", "a*u+b*u+c*u+a*v+b*v+c*v");
		check("Distribute({{a, b}, {x, y, z}, {s, t}}, List)",
				"{{a,x,s},{a,x,t},{a,y,s},{a,y,t},{a,z,s},{a,z,t},{b,x,s},{b,x,t},{b,y,s},{b,y,t},{b,z,s},{b,z,t}}");
		check("Distribute((x y z)^n0, Times)", "x^n0*y^n0*z^n0");
		check("Distribute(And(Or(a, b, c), Or(u, v)), Or, And)", "a&&u||a&&v||b&&u||b&&v||c&&u||c&&v");
		check("Distribute((a + b).(x + y + z))", "a.x+a.y+a.z+b.x+b.y+b.z");
		check("Distribute(f(g(a, b), g(c, d, e)), g, f, gp, fp)",
				"gp(fp(a,c),fp(a,d),fp(a,e),fp(b,c),fp(b,d),fp(b,e))");
		check("Distribute[Factor[x^6 - 1], Plus, Times, List, Times]",
				"{-1,x,-x^2,-x,x^2,-x^3,-x^2,x^3,-x^4,-x,x^2,-x^3,-x^2,x^3,-x^4,-x^3,x^4,-x^5,x,-x^\n"
						+ "2,x^3,x^2,-x^3,x^4,x^3,-x^4,x^5,x^2,-x^3,x^4,x^3,-x^4,x^5,x^4,-x^5,x^6}");
	}

	public void testDivisible() {
		check("Divisible(2 Pi, Pi/2)", "True");
		check("Divisible(10,3)", "False");
		check("Divisible(2^100-1,3)", "True");
		check("Divisible({200, 201, 202, 203}, 3)", "{False,True,False,False}");
		check("Divisible(3/4, 1/4)", "True");
		check("Divisible(3 + I, 2 - I)", "True");
	}

	public void testDivisors() {
		check("Divisors(6)", "{1,2,3,6}");
		check("Divisors(-6)", "{1,2,3,6}");
		check("Divisors(24)", "{1,2,3,4,6,8,12,24}");
		check("Divisors(1729)", "{1,7,13,19,91,133,247,1729}");
		check("FactorInteger(1729)", "{{7,1},{13,1},{19,1}}");

		check("Divisors({605,871,824})", "{{1,5,11,55,121,605},{1,13,67,871},{1,2,4,8,103,206,412,824}}");
	}

	public void testDo() {
		check("reap(do(if(primeQ(2^n0 - 1), sow(n0)), {n0, 100}))[[2, 1]]", "{2,3,5,7,13,17,19,31,61,89}");
		check("$t = x; Do($t = 1/(1 + $t), {5}); $t", "1/(1+1/(1+1/(1+1/(1+1/(1+x)))))");
		check("Nest(1/(1 + #) &, x, 5)", "1/(1+1/(1+1/(1+1/(1+1/(1+x)))))");
	}

	public void testDot() {
		check("{{1, 2}, {3.0, 4}, {5, 6}}.{1,1}", "{3.0,7.0,11.0}");
		check("{{1, 2}, {3.0, 4}, {5, 6}}.{{1},{1}}", "{{3.0},\n" + " {7.0},\n" + " {11.0}}");
		check("{1,1,1}.{{1, 2}, {3.0, 4}, {5, 6}}", "{9.0,12.0}");
		check("{{1,1,1}}.{{1, 2}, {3.0, 4}, {5, 6}}", "{{9.0,12.0}}");
		check("{1,2,3.0}.{4,5.0,6}", "32.0");

		check("{{1, 2}, {3, 4}, {5, 6}}.{1,1}", "{3,7,11}");
		check("{{1, 2}, {3, 4}, {5, 6}}.{{1},{1}}", "{{3},\n" + " {7},\n" + " {11}}");
		check("{1,1,1}.{{1, 2}, {3, 4}, {5, 6}}", "{9,12}");
		check("{{1,1,1}}.{{1, 2}, {3, 4}, {5, 6}}", "{{9,12}}");
		check("{1,2,3}.{4,5,6}", "32");
	}

	public void testDrop() {
		check("Drop({a, b, c, d, e, f}, 2)", "{c,d,e,f}");
		check("Drop[{a, b, c, d, e, f}, -3]", "{a,b,c}");
		check("Drop[{a, b, c, d, e, f}, {2, 4}]", "{a,e,f}");
		check("Drop({{11, 12, 13}, {21, 22, 23}, {31, 32, 33}}, 1, 2)", "{{23},{33}}");
		check("Drop({{11, 12, 13}, {21, 22, 23}, a, {31, 32, 33}}, 1, 2)",
				"Drop({{11,12,13},{21,22,23},a,{31,32,33}},1,2)");
	}

	public void testNDSolve() {

		check("NDSolve({ y(x)*Cos(x + y(x))== (y'(x)), y(0)==1}, y, {x, 0, 30})", "InterpolatingFunction(\n"
				+ "{{0.0,1.0486539247435627},\n" + " {0.1,1.0854808036771377},\n" + " {0.2,1.1096485558561129},\n"
				+ " {0.30000000000000004,1.1212578599391958},\n" + " {0.4,1.1211098516670583},\n"
				+ " {0.5,1.110440971080707},\n" + " {0.6,1.0906930188004873},\n" + " {0.7,1.0633460402333474},\n"
				+ " {0.7999999999999999,1.0298136072918775},\n" + " {0.8999999999999999,0.991387315006244},\n"
				+ " {0.9999999999999999,0.9492149070968016},\n" + " {1.0999999999999999,0.9042988579035411},\n"
				+ " {1.2,0.857505923891753},\n" + " {1.3,0.8095815052617051},\n"
				+ " {1.4000000000000001,0.7611651420556339},\n" + " {1.5000000000000002,0.7128051392128717},\n"
				+ " {1.6000000000000003,0.6649713621384138},\n" + " {1.7000000000000004,0.6180658664467911},\n"
				+ " {1.8000000000000005,0.5724313779772732},\n" + " {1.9000000000000006,0.5283578293741383},\n"
				+ " {2.0000000000000004,0.48608725600029157},\n" + " {2.1000000000000005,0.4458173974209415},\n"
				+ " {2.2000000000000006,0.4077043633624414},\n" + " {2.3000000000000007,0.37186471548866024},\n"
				+ " {2.400000000000001,0.3383772924103512},\n" + " {2.500000000000001,0.3072850658079258},\n"
				+ " {2.600000000000001,0.2785972606942865},\n" + " {2.700000000000001,0.2522919043406724},\n"
				+ " {2.800000000000001,0.22831889029854097},\n" + " {2.9000000000000012,0.20660356283726058},\n"
				+ " {3.0000000000000013,0.18705075124931517},\n" + " {3.1000000000000014,0.1695491213425783},\n"
				+ " {3.2000000000000015,0.15397566999707005},\n" + " {3.3000000000000016,0.14020017181526293},\n"
				+ " {3.4000000000000017,0.1280893946838693},\n" + " {3.5000000000000018,0.11751092979804098},\n"
				+ " {3.600000000000002,0.10833652492796243},\n" + " {3.700000000000002,0.10044485973079281},\n"
				+ " {3.800000000000002,0.09372375133539376},\n" + " {3.900000000000002,0.08807182139220295},\n"
				+ " {4.000000000000002,0.0833996886046539},\n" + " {4.100000000000001,0.07963077199182245},\n"
				+ " {4.200000000000001,0.07670180014927495},\n" + " {4.300000000000001,0.07456312212288695},\n"
				+ " {4.4,0.07317890821085377},\n" + " {4.5,0.07252731596246077},\n" + " {4.6,0.0726006791932994},\n"
				+ " {4.699999999999999,0.0734057565099308},\n" + " {4.799999999999999,0.07496405020983263},\n"
				+ " {4.899999999999999,0.07731217510263196},\n" + " {4.999999999999998,0.08050221749315989},\n"
				+ " {5.099999999999998,0.084601974272093},\n" + " {5.1999999999999975,0.08969489746733304},\n"
				+ " {5.299999999999997,0.0958794879078716},\n" + " {5.399999999999997,0.10326778201284252},\n"
				+ " {5.4999999999999964,0.11198246184539966},\n" + " {5.599999999999996,0.12215200271817592},\n"
				+ " {5.699999999999996,0.13390318126361409},\n" + " {5.799999999999995,0.14735024731166288},\n"
				+ " {5.899999999999995,0.16258018854901138},\n" + " {5.999999999999995,0.17963388536831357},\n"
				+ " {6.099999999999994,0.19848366765525352},\n" + " {6.199999999999994,0.21900890659080618},\n"
				+ " {6.299999999999994,0.24097273961477084},\n" + " {6.399999999999993,0.2640045482014827},\n"
				+ " {6.499999999999993,0.2875938184944301},\n" + " {6.5999999999999925,0.311100756322707},\n"
				+ " {6.699999999999992,0.33378687199758955},\n" + " {6.799999999999992,0.35486468555987505},\n"
				+ " {6.8999999999999915,0.37356070170896716},\n" + " {6.999999999999991,0.38918166156437567},\n"
				+ " {7.099999999999991,0.401172598399326},\n" + " {7.19999999999999,0.4091571693969712},\n"
				+ " {7.29999999999999,0.4129553215557175},\n" + " {7.39999999999999,0.41257870289475035},\n"
				+ " {7.499999999999989,0.4082084528387488},\n" + " {7.599999999999989,0.40016206914955754},\n"
				+ " {7.699999999999989,0.38885599961989714},\n" + " {7.799999999999988,0.37476918269614945},\n"
				+ " {7.899999999999988,0.3584108421644447},\n" + " {7.999999999999988,0.34029407839355436},\n"
				+ " {8.099999999999987,0.3209155086689302},\n" + " {8.199999999999987,0.30074044115694204},\n"
				+ " {8.299999999999986,0.2801927248235081},\n" + " {8.399999999999986,0.2596483599261049},\n"
				+ " {8.499999999999986,0.23943205150206048},\n" + " {8.599999999999985,0.21981604663931104},\n"
				+ " {8.699999999999985,0.20102075567302183},\n" + " {8.799999999999985,0.18321678667386834},\n"
				+ " {8.899999999999984,0.1665281105852},\n" + " {8.999999999999984,0.15103612290345214},\n"
				+ " {9.099999999999984,0.136784386073114},\n" + " {9.199999999999983,0.12378383728408018},\n"
				+ " {9.299999999999983,0.11201824175272579},\n" + " {9.399999999999983,0.10144967223258593},\n"
				+ " {9.499999999999982,0.09202380805440191},\n" + " {9.599999999999982,0.08367487371122105},\n"
				+ " {9.699999999999982,0.07633007619745596},\n" + " {9.799999999999981,0.06991344748031084},\n"
				+ " {9.89999999999998,0.06434904783208746},\n" + " {9.99999999999998,0.05956353167221671},\n"
				+ " {10.09999999999998,0.055488115733649444},\n" + " {10.19999999999998,0.052060017293655024},\n"
				+ " {10.29999999999998,0.0492234472683428},\n" + " {10.399999999999979,0.04693025003153322},\n"
				+ " {10.499999999999979,0.04514028068683145},\n" + " {10.599999999999978,0.043821603367067995},\n"
				+ " {10.699999999999978,0.042950583025463014},\n" + " {10.799999999999978,0.04251192976987961},\n"
				+ " {10.899999999999977,0.04249874014945849},\n" + " {10.999999999999977,0.042912564384982844},\n"
				+ " {11.099999999999977,0.043763512208115654},\n" + " {11.199999999999976,0.045070392103282626},\n"
				+ " {11.299999999999976,0.046860858299487156},\n" + " {11.399999999999975,0.049171515562746765},\n"
				+ " {11.499999999999975,0.052047902387254485},\n" + " {11.599999999999975,0.05554423757203267},\n"
				+ " {11.699999999999974,0.059722773229538936},\n" + " {11.799999999999974,0.0646525504791017},\n"
				+ " {11.899999999999974,0.07040730672504374},\n" + " {11.999999999999973,0.0770622441521745},\n"
				+ " {12.099999999999973,0.0846893525976165},\n" + " {12.199999999999973,0.09335100838707118},\n"
				+ " {12.299999999999972,0.10309167339440412},\n" + " {12.399999999999972,0.11392772849437467},\n"
				+ " {12.499999999999972,0.1258358199938911},\n" + " {12.599999999999971,0.138740580936534},\n"
				+ " {12.69999999999997,0.15250316894309834},\n" + " {12.79999999999997,0.16691262572026747},\n"
				+ " {12.89999999999997,0.18168242049828567},\n" + " {12.99999999999997,0.19645445463107747},\n"
				+ " {13.09999999999997,0.2108120803325641},\n" + " {13.199999999999969,0.22430228748955305},\n"
				+ " {13.299999999999969,0.23646536721975198},\n" + " {13.399999999999968,0.24686856012190703},\n"
				+ " {13.499999999999968,0.25513903798118154},\n" + " {13.599999999999968,0.26099149103034053},\n"
				+ " {13.699999999999967,0.26424665962609567},\n" + " {13.799999999999967,0.26483900018855316},\n"
				+ " {13.899999999999967,0.26281370586093794},\n" + " {13.999999999999966,0.2583149394045044},\n"
				+ " {14.099999999999966,0.2515680519994532},\n" + " {14.199999999999966,0.24285873137419975},\n"
				+ " {14.299999999999965,0.2325116382150581},\n" + " {14.399999999999965,0.220870422513877},\n"
				+ " {14.499999999999964,0.20828029902084735},\n" + " {14.599999999999964,0.19507375354460899},\n"
				+ " {14.699999999999964,0.1815595106786453},\n" + " {14.799999999999963,0.16801461631765874},\n"
				+ " {14.899999999999963,0.1546793400273539},\n" + " {14.999999999999963,0.14175453929296386},\n"
				+ " {15.099999999999962,0.12940111187045478},\n" + " {15.199999999999962,0.11774116741799541},\n"
				+ " {15.299999999999962,0.10686056081330114},\n" + " {15.399999999999961,0.09681244213729613},\n"
				+ " {15.499999999999961,0.08762149329437179},\n" + " {15.59999999999996,0.07928854218346183},\n"
				+ " {15.69999999999996,0.07179527544171443},\n" + " {15.79999999999996,0.06510881121842255},\n"
				+ " {15.89999999999996,0.05918594276300758},\n" + " {15.99999999999996,0.053976918199585305},\n"
				+ " {16.09999999999996,0.049428676909066306},\n" + " {16.19999999999996,0.04548751365173981},\n"
				+ " {16.29999999999996,0.042101184130222284},\n" + " {16.399999999999963,0.03922049785649547},\n"
				+ " {16.499999999999964,0.03680046534374525},\n" + " {16.599999999999966,0.03480107758305633},\n"
				+ " {16.699999999999967,0.0331877982037784},\n" + " {16.79999999999997,0.03193184479148035},\n"
				+ " {16.89999999999997,0.03101032767125873},\n" + " {16.99999999999997,0.030406303862182932},\n"
				+ " {17.099999999999973,0.03010879219599716},\n" + " {17.199999999999974,0.030112783566147657},\n"
				+ " {17.299999999999976,0.030419268228300628},\n" + " {17.399999999999977,0.03103528987978064},\n"
				+ " {17.49999999999998,0.031974023436000365},\n" + " {17.59999999999998,0.03325485929623045},\n"
				+ " {17.69999999999998,0.034903460631464545},\n" + " {17.799999999999983,0.036951741050493675},\n"
				+ " {17.899999999999984,0.03943768737279855},\n" + " {17.999999999999986,0.042404926190700305},\n"
				+ " {18.099999999999987,0.04590190449334592},\n" + " {18.19999999999999,0.049980526549024254},\n"
				+ " {18.29999999999999,0.054694066601254296},\n" + " {18.39999999999999,0.06009416801910799},\n"
				+ " {18.499999999999993,0.0662267563624869},\n" + " {18.599999999999994,0.07312675184871771},\n"
				+ " {18.699999999999996,0.08081158301761861},\n" + " {18.799999999999997,0.0892736922498442},\n"
				+ " {18.9,0.09847248889531121},\n" + " {19.0,0.10832652953813567},\n" + " {19.1,0.11870703751609235},\n"
				+ " {19.200000000000003,0.12943412780911714},\n" + " {19.300000000000004,0.1402771628385532},\n"
				+ " {19.400000000000006,0.15096041573160082},\n" + " {19.500000000000007,0.16117459959331792},\n"
				+ " {19.60000000000001,0.1705938815306118},\n" + " {19.70000000000001,0.17889692028539034},\n"
				+ " {19.80000000000001,0.18578953051456235},\n" + " {19.900000000000013,0.19102607653255335},\n"
				+ " {20.000000000000014,0.19442681148735713},\n" + " {20.100000000000016,0.19588908551204787},\n"
				+ " {20.200000000000017,0.19539143760805033},\n" + " {20.30000000000002,0.19299074958941248},\n"
				+ " {20.40000000000002,0.188813589734067},\n" + " {20.50000000000002,0.18304344050912236},\n"
				+ " {20.600000000000023,0.1759056646182722},\n" + " {20.700000000000024,0.1676519017849422},\n"
				+ " {20.800000000000026,0.15854523818283722},\n" + " {20.900000000000027,0.14884707766919583},\n"
				+ " {21.00000000000003,0.13880625822789994},\n" + " {21.10000000000003,0.12865064403209947},\n"
				+ " {21.20000000000003,0.11858119433695793},\n" + " {21.300000000000033,0.10876835547392645},\n"
				+ " {21.400000000000034,0.09935052412493195},\n" + " {21.500000000000036,0.09043427196672443},\n"
				+ " {21.600000000000037,0.08209599140343246},\n" + " {21.70000000000004,0.07438461231024102},\n"
				+ " {21.80000000000004,0.06732504714496039},\n" + " {21.90000000000004,0.0609220448460617},\n"
				+ " {22.000000000000043,0.05516417096178315},\n" + " {22.100000000000044,0.05002767965231217},\n"
				+ " {22.200000000000045,0.045480098453626217},\n" + " {22.300000000000047,0.04148340402665843},\n"
				+ " {22.40000000000005,0.03799672158222671},\n" + " {22.50000000000005,0.03497852815326329},\n"
				+ " {22.60000000000005,0.032388377671356924},\n" + " {22.700000000000053,0.030188192781695093},\n"
				+ " {22.800000000000054,0.02834318482532842},\n" + " {22.900000000000055,0.026822470808221865},\n"
				+ " {23.000000000000057,0.025599456420398434},\n" + " {23.10000000000006,0.02465204936600566},\n"
				+ " {23.20000000000006,0.023962759329861603},\n" + " {23.30000000000006,0.023518731365109482},\n"
				+ " {23.400000000000063,0.023311749398506023},\n" + " {23.500000000000064,0.02333823650818959},\n"
				+ " {23.600000000000065,0.02359926881772662},\n" + " {23.700000000000067,0.024100610123988526},\n"
				+ " {23.800000000000068,0.02485276434553134},\n" + " {23.90000000000007,0.025871031998059613},\n"
				+ " {24.00000000000007,0.027175544570715412},\n" + " {24.100000000000072,0.02879123634710425},\n"
				+ " {24.200000000000074,0.030747696564390598},\n" + " {24.300000000000075,0.03307882595701917},\n"
				+ " {24.400000000000077,0.035822201576675955},\n" + " {24.500000000000078,0.03901803437743281},\n"
				+ " {24.60000000000008,0.04270758911304822},\n" + " {24.70000000000008,0.046930931420902475},\n"
				+ " {24.800000000000082,0.051723880711205865},\n" + " {24.900000000000084,0.05711408982254241},\n"
				+ " {25.000000000000085,0.06311625424637678},\n" + " {25.100000000000087,0.06972658382886275},\n"
				+ " {25.200000000000088,0.07691685011526753},\n" + " {25.30000000000009,0.0846285415836176},\n"
				+ " {25.40000000000009,0.09276788599437955},\n" + " {25.500000000000092,0.10120268026019423},\n"
				+ " {25.600000000000094,0.10976193177974276},\n" + " {25.700000000000095,0.11823918766143336},\n"
				+ " {25.800000000000097,0.12640006310087093},\n" + " {25.900000000000098,0.13399389013035107},\n"
				+ " {26.0000000000001,0.14076868583650626},\n" + " {26.1000000000001,0.14648794906949023},\n"
				+ " {26.200000000000102,0.1509473266388399},\n" + " {26.300000000000104,0.15398909090144997},\n"
				+ " {26.400000000000105,0.15551268333713533},\n" + " {26.500000000000107,0.15548022163602201},\n"
				+ " {26.600000000000108,0.15391666739307647},\n" + " {26.70000000000011,0.15090510975131846},\n"
				+ " {26.80000000000011,0.14657818306953294},\n" + " {26.900000000000112,0.14110693216447398},\n"
				+ " {27.000000000000114,0.13468847749049695},\n" + " {27.100000000000115,0.1275336799610958},\n"
				+ " {27.200000000000117,0.11985574493622606},\n" + " {27.300000000000118,0.11186041177815408},\n"
				+ " {27.40000000000012,0.1037381005484644},\n" + " {27.50000000000012,0.09565815735831991},\n"
				+ " {27.600000000000122,0.08776516222812088},\n" + " {27.700000000000124,0.08017713485435946},\n"
				+ " {27.800000000000125,0.07298538709313364},\n" + " {27.900000000000126,0.06625571890495106},\n"
				+ " {28.000000000000128,0.06003063117527128},\n" + " {28.10000000000013,0.0543322298578955},\n"
				+ " {28.20000000000013,0.04916551743387415},\n" + " {28.300000000000132,0.04452180553916086},\n"
				+ " {28.400000000000134,0.04038203184386874},\n" + " {28.500000000000135,0.03671981933966998},\n"
				+ " {28.600000000000136,0.03350417158494679},\n" + " {28.700000000000138,0.03070174835662014},\n"
				+ " {28.80000000000014,0.02827870903992453},\n" + " {28.90000000000014,0.026202144002981702},\n"
				+ " {29.000000000000142,0.02444113673212715},\n" + " {29.100000000000144,0.0229675124577755},\n"
				+ " {29.200000000000145,0.02175633398892615},\n" + " {29.300000000000146,0.02078620446123},\n"
				+ " {29.400000000000148,0.02003943163828367},\n" + " {29.50000000000015,0.019502100971413434},\n"
				+ " {29.60000000000015,0.019164096103692605},\n" + " {29.700000000000152,0.01901909674905609},\n"
				+ " {29.800000000000153,0.019064575332356216},\n" + " {29.900000000000155,0.019301805546317868}})");

		// 10, 28, 8/3 as constants for the Lorenz equations
		// https://socialinnovationsimulation.com/2013/07/19/tutorial-differential-equations-2/
		check("NDSolve({x'(t) == 10*(y(t) - x(t)), \n"
				+ " y'(t) == x(t)*(28 - z(t)) - y(t), z'(t) == x(t)*y(t) - 8/3*z(t),\n"
				+ " x(0)== 0, y(0) == 1, z(0) == 0}, {x, y, z}, {t, 0, 20})",
				"InterpolatingFunction(\n" + "{{0.0,0.9120387226885125,2.091928395794948,0.06245149745549743},\n"
						+ " {0.1,3.0547472482265867,6.6383081885909405,0.7671363260472597},\n"
						+ " {0.2,9.462727500963515,19.370147786242246,7.5868957305217855},\n"
						+ " {0.30000000000000004,19.59444172025279,23.402220005818663,39.84913319685856},\n"
						+ " {0.4,9.819547568813158,-6.620759108724325,41.60317772251752},\n"
						+ " {0.5,-2.04438548139555,-8.941778547067877,29.856066548763092},\n"
						+ " {0.6,-6.097490967269484,-8.341668817036748,26.232129739540905},\n"
						+ " {0.7,-7.76132775294623,-9.120441909925662,25.461808154934186},\n"
						+ " {0.7999999999999999,-8.953583987350978,-9.90857031633744,26.60081042584465},\n"
						+ " {0.8999999999999999,-9.443146568684758,-9.378901383760137,28.337792282826587},\n"
						+ " {0.9999999999999999,-8.860726085493004,-7.892657351525051,28.67838606147366},\n"
						+ " {1.0999999999999999,-7.8771829074113615,-7.056086437586972,27.351028589193998},\n"
						+ " {1.2,-7.451910367868102,-7.471753378752172,25.725832092046073},\n"
						+ " {1.3,-7.9020997585135495,-8.725140582501306,25.117627922409987},\n"
						+ " {1.4000000000000001,-8.883909153528379,-9.868696742527987,26.168398195496668},\n"
						+ " {1.5000000000000002,-9.519402665860657,-9.655505310775023,28.11674194667205},\n"
						+ " {1.6000000000000003,-9.073197278773105,-8.138169067027974,28.87241376785506},\n"
						+ " {1.7000000000000004,-8.01250059860916,-7.0287359482251786,27.704263166226962},\n"
						+ " {1.8000000000000005,-7.400544046951537,-7.236258310113128,25.922739746462675},\n"
						+ " {1.9000000000000006,-7.70908112705835,-8.449518436535662,24.99252248561958},\n"
						+ " {2.0000000000000004,-8.698766876300466,-9.79324074818465,25.766373281017014},\n"
						+ " {2.1000000000000005,-9.531799001728224,-9.919018239045549,27.814746954998206},\n"
						+ " {2.2000000000000006,-9.283782930345184,-8.444739178125943,29.01023993440923},\n"
						+ " {2.3000000000000007,-8.187260627314446,-7.056200552462571,28.08291817164977},\n"
						+ " {2.400000000000001,-7.383428464247613,-7.014565785627403,26.18567427220732},\n"
						+ " {2.500000000000001,-7.5214411256586455,-8.143242435919282,24.932052566669494},\n"
						+ " {2.600000000000001,-8.479254088983692,-9.644814227285014,25.369700753770974},\n"
						+ " {2.700000000000001,-9.491422149859519,-10.142186954692487,27.42127995143334},\n"
						+ " {2.800000000000001,-9.486291870566598,-8.807194405929554,29.066650978916027},\n"
						+ " {2.9000000000000012,-8.403517594413051,-7.150078085563753,28.474726095397543},\n"
						+ " {3.0000000000000013,-7.40816273583971,-6.8161927923627985,26.515878982391282},\n"
						+ " {3.1000000000000014,-7.347264002508071,-7.814020546477093,24.946649128545673},\n"
						+ " {3.2000000000000015,-8.230760380609384,-9.42199395730754,24.998780640863927},\n"
						+ " {3.3000000000000016,-9.391183445084328,-10.302285843028777,26.943210696957998},\n"
						+ " {3.4000000000000017,-9.666048038109086,-9.215627898777004,29.016873045706692},\n"
						+ " {3.5000000000000018,-8.659498977622867,-7.323489525833889,28.862931815018268},\n"
						+ " {3.600000000000002,-7.4823916275054945,-6.652664128174806,26.9131059498479},\n"
						+ " {3.700000000000002,-7.195491945146096,-7.470811877628359,25.045832630318202},\n"
						+ " {3.800000000000002,-7.960628995222847,-9.127162055711096,24.675170769874114},\n"
						+ " {3.900000000000002,-9.226569096072769,-10.376811674430172,26.395461043628774},\n"
						+ " {4.000000000000002,-9.805591427554925,-9.651824655008111,28.83630419608766},\n"
						+ " {4.100000000000001,-8.949915457044026,-7.589612386486224,29.22414378135347},\n"
						+ " {4.200000000000001,-7.613957738869209,-6.53808778870912,27.37477314490137},\n"
						+ " {4.300000000000001,-7.076158566456391,-7.123775640437806,25.238130915525794},\n"
						+ " {4.4,-7.678076711783419,-8.766424404676304,24.420636982064366},\n"
						+ " {4.5,-8.996491915270333,-10.345934327211824,25.801416501643136},\n"
						+ " {4.6,-9.885496588521299,-10.087701693246105,28.5038955492431},\n"
						+ " {4.699999999999999,-9.264522222034374,-7.959707097275667,29.52658890717317},\n"
						+ " {4.799999999999999,-7.810426950044977,-6.49009433215633,27.8947536314523},\n"
						+ " {4.899999999999999,-7.000630383221647,-6.784505762697917,25.5310549786133},\n"
						+ " {4.999999999999998,-7.394108846372224,-8.349242108038565,24.256351108196345},\n"
						+ " {5.099999999999998,-8.703938151458653,-10.195069319884905,25.19236562336142},\n"
						+ " {5.1999999999999975,-9.885889842046797,-10.48495655560025,28.006858868285207},\n"
						+ " {5.299999999999997,-9.586489511837888,-8.439603988026237,29.72868528651162},\n"
						+ " {5.399999999999997,-8.078143155504296,-6.530876836882358,28.46134785482499},\n"
						+ " {5.4999999999999964,-6.9819231356456735,-6.466653684227476,25.93099167063439},\n"
						+ " {5.599999999999996,-7.121528329889899,-7.88787820628363,24.20234203211027},\n"
						+ " {5.699999999999996,-8.356319684726474,-9.917090522637212,24.605928142304602},\n"
						+ " {5.799999999999995,-9.788698837554207,-10.796826580519939,27.346231921596694},\n"
						+ " {5.899999999999995,-9.89088437452763,-9.024206741976506,29.778897274403636},\n"
						+ " {5.999999999999995,-8.420514003836658,-6.688003191360965,29.054010617552613},\n"
						+ " {6.099999999999994,-7.0350303126035385,-6.187136328051479,26.442829958983},\n"
						+ " {6.199999999999994,-6.875134322098752,-7.396835400653886,24.277267277225956},\n"
						+ " {6.299999999999994,-7.965505350000965,-9.513706182569262,24.083696166133432},\n"
						+ " {6.399999999999993,-9.580464781494461,-10.972652577486608,26.542183883834678},\n"
						+ " {6.499999999999993,-10.143864136654022,-9.690049542339503,29.618507928721556},\n"
						+ " {6.5999999999999925,-8.835130162770739,-6.9942087112661335,29.638329494359365},\n"
						+ " {6.699999999999992,-7.1771023287764635,-5.9681992684508165,27.068979398031377},\n"
						+ " {6.799999999999992,-6.672218103337097,-6.892529995702175,24.49850762897821},\n"
						+ " {6.8999999999999915,-7.547604783842454,-8.995678079082628,23.668554118274997},\n"
						+ " {6.999999999999991,-9.255295936551637,-10.965221929331387,25.637390555478067},\n"
						+ " {7.099999999999991,-10.303527906139927,-10.387163214560308,29.18947823247501},\n"
						+ " {7.19999999999999,-9.309298121095098,-7.484528415950674,30.158943223163043},\n"
						+ " {7.29999999999999,-7.427113438456424,-5.8406623187458235,27.807125180733564},\n"
						+ " {7.39999999999999,-6.533469147547356,-6.393550298591854,24.88250407961853},\n"
						+ " {7.499999999999989,-7.122694523471927,-8.381845439889812,23.402276783698536},\n"
						+ " {7.599999999999989,-8.81734250048592,-10.739707268016904,24.69677928932921},\n"
						+ " {7.699999999999989,-10.323568010943477,-11.033573175004346,28.449196672647037},\n"
						+ " {7.799999999999988,-9.813820053319306,-8.187859337633467,30.531082211517695},\n"
						+ " {7.899999999999988,-7.804262326210729,-5.848579786010087,28.645453202060366},\n"
						+ " {7.999999999999988,-6.484382375876885,-5.922044404201991,25.44512579810147},\n"
						+ " {8.099999999999987,-6.71479744825337,-7.697250344847535,23.323980786489905},\n"
						+ " {8.199999999999987,-8.282169771686645,-10.281871362538242,23.8027874819233},\n"
						+ " {8.299999999999986,-10.16053849213318,-11.517937939192192,27.391537903939057},\n"
						+ " {8.399999999999986,-10.29577281602351,-9.108909593577899,30.63419195058698},\n"
						+ " {8.499999999999986,-8.323651273656298,-6.05487990392961,29.552985842070917},\n"
						+ " {8.599999999999985,-6.557142364544888,-5.507175860978072,26.201580409319476},\n"
						+ " {8.699999999999985,-6.352556183592447,-6.970988308974587,23.469836537072393},\n"
						+ " {8.799999999999985,-7.676705599602827,-9.602793387380213,23.046912770717523},\n"
						+ " {8.899999999999984,-9.784332641313917,-11.715962235243525,26.06876894041491},\n"
						+ " {8.999999999999984,-10.673124480112802,-10.19768420411118,30.3166193900599},\n"
						+ " {9.099999999999984,-8.986721467890124,-6.545207858679382,30.46111292717043},\n"
						+ " {9.199999999999983,-6.792562845928071,-5.1923178995220525,27.16466920947156},\n"
						+ " {9.299999999999983,-6.071200830157529,-6.2347593328048605,23.87419391826131},\n"
						+ " {9.399999999999983,-7.038032455274588,-8.73820272797494,22.520074821448194},\n"
						+ " {9.499999999999982,-9.189429057765125,-11.52142707090118,24.603551641442476},\n"
						+ " {9.599999999999982,-10.837271452436473,-11.311890368759574,29.42702411421525},\n"
						+ " {9.699999999999982,-9.762922520209818,-7.4208924617749785,31.23174822532446},\n"
						+ " {9.799999999999981,-7.240486828658113,-5.048765303583415,28.33827573741936},\n"
						+ " {9.89999999999998,-5.9166181235139055,-5.523717770743894,24.571964904745048},\n"
						+ " {9.99999999999998,-6.412065859061753,-7.742415871649952,22.305215345630685},\n"
						+ " {10.09999999999998,-8.402729347401698,-10.882838512642685,23.17996849655513},\n"
						+ " {10.19999999999998,-10.671861927795705,-12.196879317607667,27.886666520568113},\n"
						+ " {10.29999999999998,-10.560341374194314,-8.759163239659822,31.61397425139721},\n"
						+ " {10.399999999999979,-7.953941363878062,-5.19933171501072,29.697897462557087},\n"
						+ " {10.499999999999979,-5.9524714767244715,-4.882802865277057,25.601545451339934},\n"
						+ " {10.599999999999978,-5.8540201050878675,-6.679057330652225,22.475135205699424},\n"
						+ " {10.699999999999978,-7.484228104970368,-9.828022253310575,22.011772762027004},\n"
						+ " {10.799999999999978,-10.092026714044628,-12.52743197416596,25.789560522220974},\n"
						+ " {10.899999999999977,-11.19255489647054,-10.505983600772847,31.220789937944303},\n"
						+ " {10.999999999999977,-8.965121044598563,-5.849888048045914,31.137929855668762},\n"
						+ " {11.099999999999977,-6.27066734127163,-4.385897940702082,27.0056553697583},\n"
						+ " {11.199999999999976,-5.43367809741895,-5.612614897027493,23.097194751284906},\n"
						+ " {11.299999999999976,-6.520389239352411,-8.461037625352171,21.30138867482854},\n"
						+ " {11.399999999999975,-9.09254251001058,-12.037448766413442,23.464661049273023},\n"
						+ " {11.499999999999975,-11.37483349933275,-12.297931651442056,29.624377360051817},\n"
						+ " {11.599999999999975,-10.21883679262754,-7.294560055453709,32.345877038462504},\n"
						+ " {11.699999999999974,-7.0008163397378755,-4.184978584616743,28.819428853185137},\n"
						+ " {11.799999999999974,-5.250418131530365,-4.607616956586191,24.24512049445921},\n"
						+ " {11.899999999999974,-5.615862525986154,-6.930588330424711,21.212432684353352},\n"
						+ " {11.999999999999973,-7.775302266555136,-10.679619067324436,21.415922516663603},\n"
						+ " {12.099999999999973,-10.811991074596987,-13.368870642183051,26.6948666516594},\n"
						+ " {12.199999999999973,-11.437085497726185,-9.751583587169751,32.58388577908576},\n"
						+ " {12.299999999999972,-8.293936465952418,-4.621490087914189,31.001252828605793},\n"
						+ " {12.399999999999972,-5.465968669642688,-3.7521323454235858,26.016637249024384},\n"
						+ " {12.499999999999972,-4.895397270227832,-5.386123068978331,21.873475499084158},\n"
						+ " {12.599999999999971,-6.331471404713105,-8.68198921325011,20.147259045433827},\n"
						+ " {12.69999999999997,-9.39765795846881,-12.881928249684945,23.041378651907912},\n"
						+ " {12.79999999999997,-11.977234234274372,-12.749539216996647,30.6434266798744},\n"
						+ " {12.89999999999997,-10.186755814569121,-6.396082951233546,33.14039841231305},\n"
						+ " {12.99999999999997,-6.360563406115833,-3.2615472491991966,28.544314992167045},\n"
						+ " {13.09999999999997,-4.537164058778808,-3.9478797939571617,23.41855705359471},\n"
						+ " {13.199999999999969,-4.9930552297831206,-6.432654501500081,20.0119714645256},\n"
						+ " {13.299999999999969,-7.376365439567543,-10.662440099825155,19.94476887384887},\n"
						+ " {13.399999999999968,-11.02801826603393,-14.370855029529618,25.90019648615212},\n"
						+ " {13.499999999999968,-12.126773220956897,-10.324268036247961,33.585007085213164},\n"
						+ " {13.599999999999968,-8.355650061464395,-3.8449020079118625,31.8808691920283},\n"
						+ " {13.699999999999967,-4.882597220633385,-2.745516336814401,26.06680415274968},\n"
						+ " {13.799999999999967,-4.01601110178183,-4.281533104354812,21.225338679455934},\n"
						+ " {13.899999999999967,-5.256139297742469,-7.469114332340346,18.5734202422289},\n"
						+ " {13.999999999999966,-8.451930693645433,-12.517580110812991,20.28904853023611},\n"
						+ " {14.099999999999966,-12.302054659290857,-14.82898743507637,28.99264405288425},\n"
						+ " {14.199999999999966,-11.52631918077078,-7.531506165538236,34.886335092824886},\n"
						+ " {14.299999999999965,-6.69542166916538,-2.3037884322135076,30.222805213960516},\n"
						+ " {14.399999999999965,-3.8154093685799784,-2.416865961552572,24.066123542736424},\n"
						+ " {14.499999999999964,-3.5655982115618996,-4.32747448347877,19.447556224745828},\n"
						+ " {14.599999999999964,-5.275635592129369,-7.9916933865392945,17.223777101161144},\n"
						+ " {14.699999999999964,-9.069200084727672,-13.778269174309404,20.150083648819784},\n"
						+ " {14.799999999999963,-13.143226518316009,-15.108628515162069,30.95510746041761},\n"
						+ " {14.899999999999963,-11.086959059824053,-5.701489042195943,35.57130689772124},\n"
						+ " {14.999999999999963,-5.600514699112376,-1.2115912894360246,29.31797559288444},\n"
						+ " {15.099999999999962,-2.9191744975493172,-1.7447900970221062,22.90013286381473},\n"
						+ " {15.199999999999962,-2.7808312452243915,-3.5346325353092376,18.150672772799133},\n"
						+ " {15.299999999999962,-4.399775654701482,-6.984132950048632,15.50962575313553},\n"
						+ " {15.399999999999961,-8.219456323747764,-13.301742541045133,17.516741428994806},\n"
						+ " {15.499999999999961,-13.399384945454587,-17.096911381037206,29.232298394948657},\n"
						+ " {15.59999999999996,-12.313677093172037,-6.546338512201242,37.32804586679732},\n"
						+ " {15.69999999999996,-5.722641169620936,-0.10555637399974413,30.645318830857804},\n"
						+ " {15.79999999999996,-2.126333794203669,-0.28963242084833496,23.46978871022727},\n"
						+ " {15.89999999999996,-1.3395805304149055,-1.339052122810352,18.083084427114986},\n"
						+ " {15.99999999999996,-1.8837721768253342,-2.977852249070624,14.140233814766907},\n"
						+ " {16.09999999999996,-3.7504338218753146,-6.60333140753705,11.980044212229298},\n"
						+ " {16.19999999999996,-8.132937631374102,-14.231313519187237,14.594497001901146},\n"
						+ " {16.29999999999996,-14.682287068951466,-19.471144138179564,30.009742759676744},\n"
						+ " {16.399999999999963,-12.770451933100775,-4.598118830408501,39.6917836232053},\n"
						+ " {16.499999999999964,-4.299659216711984,2.3050610511019927,30.633970857967668},\n"
						+ " {16.599999999999966,-0.1643119307694276,2.0771824936488694,23.101347732370712},\n"
						+ " {16.699999999999967,1.3123610456691028,2.436538370747271,17.828891964873034},\n"
						+ " {16.79999999999997,2.667180037805829,4.48694436887686,14.249359807408213},\n"
						+ " {16.89999999999997,5.451730151815137,9.476646501998331,13.356080624236856},\n"
						+ " {16.99999999999997,10.96538638525251,17.549609066102683,20.29494690489792},\n"
						+ " {17.099999999999973,15.097857209077725,14.164299407077277,36.87492242687258},\n"
						+ " {17.199999999999974,8.980415101623704,0.5471316240247538,35.78445510589357},\n"
						+ " {17.299999999999976,2.515157940791873,-1.5515329478964237,26.972068988923567},\n"
						+ " {17.399999999999977,0.14041906386704145,-1.065929353262967,20.533810364435165},\n"
						+ " {17.49999999999998,-0.658924809003148,-1.2906527775884806,15.760075257125887},\n"
						+ " {17.59999999999998,-1.457826846325033,-2.567921357233959,12.246350239062611},\n"
						+ " {17.69999999999998,-3.2418911272466633,-5.952405669590169,10.231652432142338},\n"
						+ " {17.799999999999983,-7.5438689839414526,-13.753392941167434,12.449711730045182},\n"
						+ " {17.899999999999984,-14.820832951579197,-21.01607648739084,28.422271853093154},\n"
						+ " {17.999999999999986,-13.683176926227128,-4.965944037476744,41.195533894379},\n"
						+ " {18.099999999999987,-4.161789659163693,3.4053845278030974,31.426463586079546},\n"
						+ " {18.19999999999999,0.6083667910633452,3.2662526439034294,23.685718805228962},\n"
						+ " {18.29999999999999,2.5046433944166533,4.136658992216096,18.6771250489035},\n"
						+ " {18.39999999999999,4.581594032997886,7.376140419358811,16.068750071443542},\n"
						+ " {18.499999999999993,8.488965192225134,13.516374614414277,18.35023612918718},\n"
						+ " {18.599999999999994,13.359186419663313,16.5002885305022,29.881782761762437},\n"
						+ " {18.699999999999996,11.91026748156324,6.2095631233557045,36.79291368823467},\n"
						+ " {18.799999999999997,5.656642431531676,0.440062093926901,30.20683702215112},\n"
						+ " {18.9,2.358976255357753,0.734940716082909,23.260015186964907},\n"
						+ " {19.0,1.7757219643125992,2.0096909429012983,18.037672175159905},\n"
						+ " {19.1,2.663187965496507,4.251063231712484,14.399264846392672},\n"
						+ " {19.200000000000003,5.259246085564077,9.09939977359405,13.300007972515175},\n"
						+ " {19.300000000000004,10.616639557620896,17.166635887449125,19.598132064923973},\n"
						+ " {19.400000000000006,15.11798166274968,14.918184332532482,36.208440117374025},\n"
						+ " {19.500000000000007,9.431807557122353,0.9607171840708291,36.24828132920109},\n"
						+ " {19.60000000000001,2.7489905601227407,-1.5551619130664216,27.378643976683495},\n"
						+ " {19.70000000000001,0.2218752611829794,-1.0583214426918113,20.82784214655041},\n"
						+ " {19.80000000000001,-0.6021150183496091,-1.2202722907843684,15.978078368122015},\n"
						+ " {19.900000000000013,-1.3563508805926336,-2.3873032702684966,12.389986364305651}})");
	}

	public void testDSolve() {
		check("DSolve(D(f(x, y), x) == D(f(x, y), y), f, {x, y})",
				"DSolve(Derivative(1,0)[f][x,y]==Derivative(0,1)[f][x,y],f,{x,y})");
		check("DSolve({y'(x)==y(x),y(0)==1},y(x), x)", "{{y(x)->E^x}}");
		check("DSolve({y'(x)==y(x)+2,y(0)==1},y(x), x)", "{{y(x)->-2+3*E^x}}");

		check("DSolve({y(0)==0,y'(x) + y(x) == a*Sin(x)}, y(x), x)", "{{y(x)->a/(2*E^x)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");
		check("DSolve({y'(x) + y(x) == a*Sin(x),y(0)==0}, y(x), x)", "{{y(x)->a/(2*E^x)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");

		check("DSolve(y'(x) + y(x) == a*Sin(x), y(x), x)", "{{y(x)->C(1)/E^x-1/2*a*Cos(x)+1/2*a*Sin(x)}}");

		check("DSolve(y'(x)-x ==0, y(x), x)", "{{y(x)->x^2/2+C(1)}}");
		check("DSolve(y'(x)+k*y(x) ==0, y(x), x)", "{{y(x)->C(1)/E^(k*x)}}");

		check("DSolve(y'(x)-3/x*y(x)-7==0, y(x), x)", "{{y(x)->-7/2*x+x^3*C(1)}}");
		check("DSolve(y'(x)== 0, y(x), x)", "{{y(x)->C(1)}}");
		check("DSolve(y'(x) + y(x)*Tan(x) == 0, y(x), x)", "{{y(x)->C(1)*Cos(x)}}");
		check("DSolve(y'(x) + y(x)*Cos(x) == 0, y(x), x)", "{{y(x)->C(1)/E^Sin(x)}}");
		check("DSolve(y'(x) == 3*y(x), y(x), x)", "{{y(x)->E^(3*x)*C(1)}}");
		check("DSolve(y'(x) + 2*y(x)/(1-x^2) == 0, y(x), x)", "{{y(x)->((1-x)*C(1))/(2+2*x)}}");
		check("DSolve(y'(x) == -y(x), y(x), x)", "{{y(x)->C(1)/E^x}}");
		check("DSolve(y'(x) == y(x)+a*Cos(x), y(x), x)", "{{y(x)->E^x*C(1)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");
		// not implemented yet
		check("DSolve(y'(x) == -3*y(x)^2, y(x), x)", "DSolve(y'(x)==-3*y(x)^2,y(x),x)");
	}

	public void testEasterSunday() {
		check("EasterSunday(2000)", "{2000,4,23}");
		check("EasterSunday(2030)", "{2030,4,21}");
	}

	public void testEigenvalues() {
		check("Eigenvalues({{a}})", "{a}");
		check("Eigenvalues({{a, b}, {0, a}})", "{a,a}");
		check("Eigenvalues({{a, b}, {0, d}})", "{1/2*(a+d-Sqrt(a^2-2*a*d+d^2)),1/2*(a+d+Sqrt(a^2-2*a*d+d^2))}");
		check("Eigenvalues({{a,b}, {c,d}})",
				"{1/2*(a+d-Sqrt(a^2+4*b*c-2*a*d+d^2)),1/2*(a+d+Sqrt(a^2+4*b*c-2*a*d+d^2))}");
		check("Eigenvalues({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", "{1.0,1.0,1.0}");
	}

	public void testEigenvectors() {
		check("Eigenvectors({{a}})", "1");
		check("Eigenvectors({{a, b}, {0, a}})", "{{1,0},{0,0}}");
		check("Eigenvectors({{a, b}, {0, d}})", "{{1,0},{-b/(a-d),1}}");
		check("Eigenvectors({{a, b}, {c, d}})",
				"{{-(-a+d+Sqrt(a^2+4*b*c-2*a*d+d^2))/(2*c),1},{-(-a+d-Sqrt(a^2+4*b*c-2*a*d+d^2))/(\n" + "2*c),1}}");
		check("Eigenvectors({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", "{{1.0,0.0,0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}}");
		check("Eigenvectors({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})",
				"{{0.23197068724628567,0.5253220933012341,0.8186734993561818},{0.8169642040610363,0.09018835790853769,-0.6365874882439638},{0.4082482904638629,-0.8164965809277261,0.40824829046386285}}");
	}

	public void testElement() {
		check("Element(pi, reals)", "True");
		check("Element(sin, reals)", "Element(Sin,Reals)");
		// check("Element[Sqrt[2], #] & /@ {Complexes, Algebraics, Reals,
		// Rationals, Integers, Primes}", "");
		check("Element(E, Algebraics)", "False");
		check("Element(Pi, Algebraics)", "False");
		check("Element(ComplexInfinity, Algebraics)", "False");
		check("Element(I, Algebraics)", "True");
	}

	public void testElementData() {
		check("ElementData(6)", "\"Carbon\"");
		check("ElementData(\"Carbon\", \"Name\")", "\"carbon\"");
		check("ElementData(79, \"Abbreviation\")", "\"Au\"");
		check("ElementData(\"Au\", \"StandardName\")", "\"Gold\"");
		check("ElementData(\"Gold\", \"AtomicNumber\")", "79");
		check("ElementData(\"Carbon\", \"AtomicNumber\")", "6");
		check("ElementData(\"He\", \"AtomicNumber\")", "2");

		check("ElementData(\"Chlorine\", \"BoilingPoint\")", "-34.04");
		check("ElementData(\"C\", \"AtomicWeight\")", "12.01");
		check("ElementData(117, \"AtomicWeight\")", "294");

		// check("ElementData(\"Pd\", \"AtomicRadius\")", "140");
		check("ElementData(\"Pd\", \"VanDerWaalsRadius\")", "163");
		// check("ElementData(\"Pd\", \"CovalentRadius\")", "131");
		check("ElementData(\"Pd\", \"IonizationEnergies\")", "{804.4,1870,3177}");

		check("ElementData(\"Pd\", \"ElectronAffinity\")", "54.24");
		check("ElementData(\"Pd\", \"ThermalConductivity\")", "71.8");
		check("ElementData(\"Pd\", \"YoungModulus\")", "121");
		check("ElementData(\"Pd\", \"PoissonRatio\")", "0.39");
		check("ElementData(\"Pd\", \"BulkModulus\")", "180");
		check("ElementData(\"Pd\", \"ShearModulus\")", "44");
		check("ElementData(\"Pd\", \"ElectronConfiguration\")", "{{2},{2,6},{2,6,10},{2,6,10}}");
		check("ElementData(\"Pd\", \"ElectronConfigurationString\")", "\"[Kr] 4d10\"");
		check("ElementData(\"Pd\", \"ElectronShellConfiguration\")", "{2,8,18,18}");

		// check("ElementData(\"Helium\", \"MeltingPoint\")",
		// "Missing(NotApplicable)");
		// check("ElementData(\"Tungsten\", \"ThermalConductivity\")", "173");

	}

	public void testEliminate() {
		check("Eliminate({x == 2 + y, y == z}, y)", "{x==2+z}");
		check("Eliminate({x == 2 + y, y == z}, {y,v})", "{x==2+z}");
		check("Eliminate({2 x + 3 y + 4 z == 1, 9 x + 8 y + 7 z == 2}, z)", "{11/2*x+11/4*y==1/4}");
		check("Eliminate({x^2 + y^2 + z^2 == 1, x - y + z == 2, x^3 - y^2 == z + 1}, {y, z})",
				"{-4*x+2*x^2-4*z+2*x*z+2*z^2==-3,-4+4*x-x^2+x^3+4*z-2*x*z-z^2==1+z}");
		check("Eliminate({x == 2 + y^3, y^2 == z}, y)", "{x==2+z^(3/2)}");

		// use evaluation step: Cos(ArcSin(y)) => Sqrt(1-y^2)
		check("Eliminate({Sin(x)==y, Cos(x) == z}, x)", "{Sqrt(1-y^2)==z}");
		check("Eliminate({a^x==y, b^(2*x) == z}, x)", "{b^((2*Log(y))/Log(a))==z}");
	}

	public void testEllipticE() {
		check("EllipticE(0.4)", "1.3993921388974326");
		check("EllipticE(2,0.999999)", "1.0");
	}

	public void testEllipticPi() {
		check("EllipticPi(0.4,0.6)", "2.59092115655522");
		check("EllipticPi(1/3, Pi/5, 0.3)", "0.6593968569137456");
	}

	public void testEqual() {
		check("Pi==3", "False");
		check("(E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi==0", "True");
	}

	public void testEquivalent() {
		check("Equivalent()", "True");
		check("Equivalent(4)", "True");
		check("Equivalent(a,a)", "True");
		check("Equivalent(a,b,a,b,c)", "Equivalent(a,b,c)");
		check("Equivalent(a,b,c,True,False)", "False");
		check("Equivalent(a,b,c,True)", "a&&b&&c");
		check("Equivalent(a,b,c,False)", "!a&&!b&&!c");
		check("BooleanConvert(Equivalent(x, y, z))", "x&&y&&z||!x&&!y&&!z");
	}

	public void testErf() {
		check("Erf(Infinity)", "1");
		check("Erf(-Infinity)", "-1");
		check("Erf(-x)", "-Erf(x)");
		check("Erf(0)", "0");
		check("Erf(0.95)", "0.8208908072732778");
	}

	public void testEulerE() {
		check("Table(EulerE(k), {k, 0, 15})", "{1,0,-1,0,5,0,-61,0,1385,0,-50521,0,2702765,0,-199360981,0}");
	}

	public void testExcept() {
		check("Cases({1, 0, 2, 0, 3}, Except(0))", "{1,2,3}");
		check("Cases({a, b, 0, 1, 2, x, y}, Except(_Integer))", "{a,b,x,y}");
		check("Cases({a, b, 0, 1, 2, x, y}, Except(0, _Integer))", "{1,2}");
		check("Cases({1, 1, -5, EulerGamma, r, I, 0, Pi, 1/2}, Except(_Integer))", "{EulerGamma,r,I,Pi,1/2}");
	}

	public void testExp() {
		check("Exp(a+b)", "E^(a+b)");
		check("E^(I*Pi)", "-1");
		check("E^(2*I*Pi)", "1");
		check("E^(2*I*Pi*3)", "1");
		check("E^(5*I*Pi)", "-1");
		check("E^Infinity", "Infinity");
		check("E^(-Infinity)", "0");
		check("E^(I*Infinity)", "Indeterminate");
		check("E^(-I*Infinity)", "Indeterminate");
		check("E^ComplexInfinity", "Indeterminate");
		check("Conjugate(E^z)", "E^Conjugate(z)");
	}

	public void testExpand() {
		check("Expand({x*(1+x)})", "{x+x^2}");
		check("Expand((-g^2+4*f*h)*h)", "-g^2*h+4*f*h^2");
		check("expand((1 + x)^10)", "1+10*x+45*x^2+120*x^3+210*x^4+252*x^5+210*x^6+120*x^7+45*x^8+10*x^9+x^10");
		check("expand((1 + x + y)*(2 - x)^3)", "8-4*x-6*x^2+5*x^3-x^4+8*y-12*x*y+6*x^2*y-x^3*y");
		check("expand((x + y)/z)", "x/z+y/z");
		check("expand((x^s + y^s)^4)", "x^(4*s)+4*x^(3*s)*y^s+6*x^(2*s)*y^(2*s)+4*x^s*y^(3*s)+y^(4*s)");

		check("Expand((1 + x) (2 + x) (3 + x))", "6+11*x+6*x^2+x^3");
		check("Distribute((1 + x) (2 + x) (3 + x))", "6+11*x+6*x^2+x^3");

		check("expand(2*(x + y)^2*Sin(x))", "2*x^2*Sin(x)+4*x*y*Sin(x)+2*y^2*Sin(x)");
		check("expand(4*(a+b)*(c+d)*(f+g)^(-2))", "(4*a*c)/(f+g)^2+(4*b*c)/(f+g)^2+(4*a*d)/(f+g)^2+(4*b*d)/(f+g)^2");
	}

	public void testExpandAll() {
		// IExpr[] temp=
		// Apart.getFractionalPartsTimes(F.Times(F.Plus(F.c,F.b),F.Power(F.a,F.CN1),F.b),
		// true);
		// issue#122
		// check("ExpandAll(( ( ( X3 - X1$c) * ( ( X1 + ( ( X4$c * X3 ) + X5$c))
		// + X3$b)) * ( ( X3 - X1 ) + ( X3$c + X5 ))))",
		// "");
		check("ExpandAll(( ( ( X3 - X1_c) * ( ( X1 + ( ( X4_c * X3 ) + X5_c)) + X3_b)) * ( ( X3 - X1 ) + ( X3_c + X5 ))))",
				"-x1^2*x3+x1*x3^2+x1*x3*x5+x1^2*x1_c-x1*x3*x1_c-x1*x5*x1_c-x1*x3*x3_b+x3^2*x3_b+x3*x5*x3_b+x1*x1_c*x3_b-x3*x1_c*x3_b-x5*x1_c*x3_b+x1*x3*x3_c-x1*x1_c*x3_c+x3*x3_b*x3_c-x1_c*x3_b*x3_c-x1*x3^\n"
						+ "2*x4_c+x3^3*x4_c+x3^2*x5*x4_c+x1*x3*x1_c*x4_c-x3^2*x1_c*x4_c-x3*x5*x1_c*x4_c+x3^\n"
						+ "2*x3_c*x4_c-x3*x1_c*x3_c*x4_c-x1*x3*x5_c+x3^2*x5_c+x3*x5*x5_c+x1*x1_c*x5_c-x3*x1_c*x5_c-x5*x1_c*x5_c+x3*x3_c*x5_c-x1_c*x3_c*x5_c");

		check("ExpandAll(1/(1 + x)^3 + Sin((1 + x)^3))", "1/(1+3*x+3*x^2+x^3)+Sin(1+3*x+3*x^2+x^3)");
		check("Expand(1/(1 + x)^3 + Sin((1 + x)^3))", "1/(1+x)^3+Sin((1+x)^3)");

		check("ExpandAll(2*x*(x^2-x+1)^(-1))", "(2*x)/(1-x+x^2)");
		check("ExpandAll((2+x)*(x^2-x+1)^(-1))", "(2+x)/(1-x+x^2)");
		check("ExpandAll(2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1))", "(10*x^3-8*x^4+4*x^6)/(2+3*x^2)");
		check("ExpandAll((b+c)*((b+c)*(a)^(-1)+1))", "(b^2+b*c)/a+(b*c+c^2)/a+b+c");
		check("ExpandAll((-2*x^3+4*x-5)*((-2*x^3+4*x-5)*(a)^(-1)-2*x))",
				"(25-20*x+10*x^3)/a+(-20*x+16*x^2-8*x^4)/a+(10*x^3-8*x^4+4*x^6)/a+10*x-8*x^2+4*x^\n" + "4");
		check("ExpandAll((-(-2*x^3+4*x-5)*(-(-2*x^3+4*x-5)*(3*x^2+2)^(-1)-2*x)*(3*x^2+2)^(-1)+x^2-2))",
				"-2+x^2+(-10*x+8*x^2-4*x^4+(25-20*x+10*x^3)/(2+3*x^2)+(-20*x+16*x^2-8*x^4)/(2+3*x^\n"
						+ "2)+(10*x^3-8*x^4+4*x^6)/(2+3*x^2))/(2+3*x^2)");
		check("ExpandAll(Sqrt((1 + x)^2))", "Sqrt(1+2*x+x^2)");
	}

	public void testExponent() {
		check("Exponent(f(x^2),x)", "0");
		check("Exponent(f(x^2),x,List)", "{0}");
		check("Exponent(x*(b+a),x)", "1");
		check("Exponent(x*(b+a),{a,b,x})", "{1,1,1}");
		check("Exponent(x*(b+a),x,List)", "{1}");
		check("Exponent(0, x)", "-Infinity");
		check("Exponent(2, x)", "0");
		check("Exponent(2*x, x)", "1");
		check("Exponent(x, x)", "1");
		check("Exponent(x^3, x)", "3");
		check("Exponent(a*x^(-1), x)", "-1");
		check("Exponent(x^(-3), x)", "-3");
		check("Exponent(x^(-3)+x^(-2), x)", "-2");
		check("Exponent(x+42, x)", "1");
		check("Exponent(1 + x^2 + a*x^3, x)", "3");
		check("Exponent((x^2 + 1)^3 + 1, x)", "6");
		check("Exponent(x^(n0 + 1) + 2*Sqrt(x) + 1, x)", "Max(1/2,1+n0)");
		check("Exponent((x^2 + 1)^3 - 1, x, Min)", "2");
		check("Exponent((x^2 + 1)^3 + 1, x)", "6");
		check("Exponent(1 + x^2 + a*x^3, x, List)", "{0,2,3}");
		check("Exponent((a+b)/c, c)", "-1");
		check("Exponent(a/c+b/c, c)", "-1");
	}

	// public void testExpand() {
	// check("expand(2*(x + y)^2*Sin(x))",
	// "2*y^2*Sin(x)+4*x*y*Sin(x)+2*x^2*Sin(x)");
	// check("expand(4*(a+b)*(c+d)*(f+g)^(-2))",
	// "4*b*d*(g+f)^(-2)+4*a*d*(g+f)^(-2)+4*b*c*(g+f)^(-2)+4*a*c*(g+f)^(-2)");
	// check("expand((1 + x)^10)",
	// "x^10+10*x^9+45*x^8+120*x^7+210*x^6+252*x^5+210*x^4+120*x^3+45*x^2+10*x+1");
	// check("expand((x + y)/z)", "y*z^(-1)+x*z^(-1)");
	// check("expand((x^s + y^s)^4)",
	// "y^(4*s)+4*x^s*y^(3*s)+6*x^(2*s)*y^(2*s)+4*x^(3*s)*y^s+x^(4*s)");
	// check("expand((1 + x + y)*(2 - x)^3)",
	// "-x^3*y+6*x^2*y-12*x*y+8*y-x^4+5*x^3-6*x^2-4*x+8");
	// }

	public void testExtendedGCD() {
		check("ExtendedGCD(2,3)", "{1,{-1,1}}");
		check("ExtendedGCD(6,15,30)", "{3,{-2,1,0}}");
		check("ExtendedGCD(3,{5,15})", "{{1,{2,-1}},{3,{1,0}}}");
		check("ExtendedGCD(6,21)", "{3,{-3,1}}");
		check("GCD(6,21)", "3");
	}

	// public void testExpandAll() {
	// // IExpr[] temp=
	// Apart.getFractionalPartsTimes(F.Times(F.Plus(F.c,F.b),F.Power(F.a,F.CN1),F.b),
	// true);
	// check("ExpandAll(2*x*(x^2-x+1)^(-1))", "2*x*(x^2-x+1)^(-1)");
	// check("ExpandAll((2+x)*(x^2-x+1)^(-1))", "(x+2)*(x^2-x+1)^(-1)");
	// check("ExpandAll(2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1))",
	// "(4*x^6-8*x^4+10*x^3)*(3*x^2+2)^(-1)");
	// check("ExpandAll((b+c)*((b+c)*(a)^(-1)+1))",
	// "c+b+(c^2+b*c)*a^(-1)+(b*c+b^2)*a^(-1)");
	// check("ExpandAll((-2*x^3+4*x-5)*((-2*x^3+4*x-5)*(a)^(-1)-2*x))",
	// "4*x^4-8*x^2+10*x+(4*x^6-8*x^4+10*x^3)*a^(-1)+(-8*x^4+16*x^2-20*x)*a^(-1)+(10*x^3\n"
	// + "-20*x+25)*a^(-1)");
	// check("ExpandAll((-(-2*x^3+4*x-5)*(-(-2*x^3+4*x-5)*(3*x^2+2)^(-1)-2*x)*(3*x^2+2)^(-1)+x^2-2))",
	// "x^2+(-4*x^4+8*x^2+(4*x^6-8*x^4+10*x^3)*(3*x^2+2)^(-1)+(-8*x^4+16*x^2-20*x)*(3*x^\n"
	// + "2+2)^(-1)+(10*x^3-20*x+25)*(3*x^2+2)^(-1)-10*x)*(3*x^2+2)^(-1)-2");
	// }

	public void testFactor() {
		check("Factor({x+x^2})", "{x*(1+x)}");
		check("Factor(x^259+1)",
				"(1+x)*(1+x-x^7-x^8+x^14+x^15-x^21-x^22+x^28+x^29-x^35-x^36-x^37-x^38+x^42+x^43+x^\n"
						+ "44+x^45-x^49-x^50-x^51-x^52+x^56+x^57+x^58+x^59-x^63-x^64-x^65-x^66+x^70+x^71+x^\n"
						+ "72+x^73+x^74+x^75-x^77-x^78-x^79-x^80-x^81-x^82+x^84+x^85+x^86+x^87+x^88+x^89-x^\n"
						+ "91-x^92-x^93-x^94-x^95-x^96+x^98+x^99+x^100+x^101+x^102+x^103-x^105-x^106-x^107-x^\n"
						+ "108-x^109-x^110-x^111+x^113+x^114+x^115+x^116+x^117+x^118-x^120-x^121-x^122-x^\n"
						+ "123-x^124-x^125+x^127+x^128+x^129+x^130+x^131+x^132-x^134-x^135-x^136-x^137-x^\n"
						+ "138-x^139+x^141+x^142+x^143+x^144+x^145+x^146-x^150-x^151-x^152-x^153+x^157+x^\n"
						+ "158+x^159+x^160-x^164-x^165-x^166-x^167+x^171+x^172+x^173+x^174-x^178-x^179-x^\n"
						+ "180-x^181+x^187+x^188-x^194-x^195+x^201+x^202-x^208-x^209+x^215+x^216)*(1-x+x^2-x^\n"
						+ "3+x^4-x^5+x^6)*(1-x+x^2-x^3+x^4-x^5+x^6-x^7+x^8-x^9+x^10-x^11+x^12-x^13+x^14-x^\n"
						+ "15+x^16-x^17+x^18-x^19+x^20-x^21+x^22-x^23+x^24-x^25+x^26-x^27+x^28-x^29+x^30-x^\n"
						+ "31+x^32-x^33+x^34-x^35+x^36)");
		check("Factor(x^258-1)",
				"(-1+x)*(1+x)*(1+x+x^2)*(1+x+x^2+x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+x^11+x^12+x^13+x^\n"
						+ "14+x^15+x^16+x^17+x^18+x^19+x^20+x^21+x^22+x^23+x^24+x^25+x^26+x^27+x^28+x^29+x^\n"
						+ "30+x^31+x^32+x^33+x^34+x^35+x^36+x^37+x^38+x^39+x^40+x^41+x^42)*(1+x-x^3-x^4+x^6+x^\n"
						+ "7-x^9-x^10+x^12+x^13-x^15-x^16+x^18+x^19-x^21-x^22+x^24+x^25-x^27-x^28+x^30+x^31-x^\n"
						+ "33-x^34+x^36+x^37-x^39-x^40+x^42-x^44-x^45+x^47+x^48-x^50-x^51+x^53+x^54-x^56-x^\n"
						+ "57+x^59+x^60-x^62-x^63+x^65+x^66-x^68-x^69+x^71+x^72-x^74-x^75+x^77+x^78-x^80-x^\n"
						+ "81+x^83+x^84)*(1-x+x^2)*(1-x+x^2-x^3+x^4-x^5+x^6-x^7+x^8-x^9+x^10-x^11+x^12-x^13+x^\n"
						+ "14-x^15+x^16-x^17+x^18-x^19+x^20-x^21+x^22-x^23+x^24-x^25+x^26-x^27+x^28-x^29+x^\n"
						+ "30-x^31+x^32-x^33+x^34-x^35+x^36-x^37+x^38-x^39+x^40-x^41+x^42)*(1-x+x^3-x^4+x^6-x^\n"
						+ "7+x^9-x^10+x^12-x^13+x^15-x^16+x^18-x^19+x^21-x^22+x^24-x^25+x^27-x^28+x^30-x^31+x^\n"
						+ "33-x^34+x^36-x^37+x^39-x^40+x^42-x^44+x^45-x^47+x^48-x^50+x^51-x^53+x^54-x^56+x^\n"
						+ "57-x^59+x^60-x^62+x^63-x^65+x^66-x^68+x^69-x^71+x^72-x^74+x^75-x^77+x^78-x^80+x^\n"
						+ "81-x^83+x^84)");
		check("Factor(4*x^2+3, Extension->I)", "4*(3/4+x^2)");
		check("Factor(3/4*x^2+9/16, Extension->I)", "3/4*(3/4+x^2)");
		check("Factor(1+x^2, GaussianIntegers->True)", "(-I+x)*(I+x)");
		check("Factor(1+x^2, Extension->I)", "(-I+x)*(I+x)");
		check("Factor(x^10 - 1, Modulus -> 2)", "(1+x)^2*(1+x+x^2+x^3+x^4)^2");

		check("factor(-1+x^16)", "(-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8)");
		check("factor((-3)*x^3 +10*x^2-11*x+4)", "-(-4+3*x)*(1-x)^2");
		check("factor(x^2-a^2)", "-(a+x)*(a-x)");
		// is sometimes inperformant, if it calls
		// FactorAbstract#factorsSquarefreeKronecker()
		check("factor(2 x^3 y - 2 a^2 x y - 3 a^2 x^2 + 3 a^4)", "(a+x)*(a-x)*(3*a^2-2*x*y)");
		check("expand((x+a)*(-x+a)*(-2*x*y+3*a^2))", "3*a^4-3*a^2*x^2-2*a^2*x*y+2*x^3*y");
	}

	public void testFactorial() {
		check("Factorial(0)", "1");
		check("Factorial(1)", "1");
		check("Factorial(-1)", "-1");
		check("Factorial(10)", "3628800");
		check("Factorial(-10)", "3628800");
		check("Factorial(11)", "39916800");
		check("Factorial(-11)", "-39916800");
		check("Factorial(19)", "121645100408832000");
		check("Factorial(20)", "2432902008176640000");
		check("Factorial(21)", "51090942171709440000");
	}

	public void testFactorInteger() {
		check("FactorInteger(4)", "{{2,2}}");
		check("FactorInteger(3/8)", "{{2,-3},{3,1}}");
		// sort is important for rational numbers
		check("FactorInteger(2345354/2424245)", "{{2,1},{5,-1},{11,1},{17,1},{311,-1},{1559,-1},{6271,1}}");

		check("FactorInteger(-1)", "{{-1,1}}");
		check("FactorInteger(-100)", "{{-1,1},{2,2},{5,2}}");
		check("FactorInteger(-5!)", "{{-1,1},{2,3},{3,1},{5,1}}");
		check("FactorInteger(-4)", "{{-1,1},{2,2}}");
		check("FactorInteger(0)", "{{0,1}}");
		check("FactorInteger(2941189)", "{{1709,1},{1721,1}}");
		check("FactorInteger(12007001)", "{{3001,1},{4001,1}}");
		check("FactorInteger(16843009)", "{{257,1},{65537,1}}");
		check("FactorInteger(-5!)", "{{-1,1},{2,3},{3,1},{5,1}}");
		check("Table(FactorInteger(2^2^n + 1), {n, 6})",
				"{{{5,1}},{{17,1}},{{257,1}},{{65537,1}},{{641,1},{6700417,1}},{{274177,1},{\n"
						+ "67280421310721,1}}}");
		check("FactorInteger(44343535354351600000003434353)", "{{149,1},{329569479697,1},{903019357561501,1}}");
	}

	public void testFactorSquareFreeList() {
		check("FactorSquareFreeList(x^5 - x^3 - x^2 + 1)", "{{-1+x,2},{1+2*x+2*x^2+x^3,1}}");
		check("FactorSquareFreeList(x^8 + 11 x^7 + 43 x^6 + 59 x^5 - 35 x^4 - 151 x^3 - 63 x^2 + 81 x + 54)",
				"{{2+x,1},{3+x,3},{-1+x^2,2}}");
		check("FactorSquareFreeList((-3)*x^3 +10*x^2-11*x+4)", "{{-1,1},{-1+x,2},{-4+3*x,1}}");
	}

	public void testFactorTerms() {
		check("factorterms(3 + 6 x + 3 x^2)", "3*(1+2*x+x^2)");
	}

	public void testFibonacci() {
		check("Table(Fibonacci(-n), {n, 10})", "{1,-1,2,-3,5,-8,13,-21,34,-55}");
		check("Table(Fibonacci(n), {n, 20})", "{1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765}");
		check("Fibonacci(1000)",
				"4346655768693745643568852767504062580256466051737178040248172908953655541794905\\\n"
						+ "1890403879840079255169295922593080322634775209689623239873322471161642996440906\\\n"
						+ "533187938298969649928516003704476137795166849228875");

	}

	public void testFindInstance() {
		check("FindInstance(Xor(a, b, c, d) && (a || b) && ! (c || d), {a, b, c, d}, Booleans)",
				"{{a->False,b->True,c->False,d->False}}");

		check("FindInstance(Sin((-3+x^2)/x) ==2,{x})", "{{x->-Sqrt(12+ArcSin(2)^2)/2+ArcSin(2)/2}}");
		// check("FindInstance(Abs((-3+x^2)/x) ==2,{x})", "{{x->-3}}");
		check("FindInstance({x^2-11==y, x+y==-9}, {x,y})", "{{x->-2,y->-7}}");

		check("FindInstance(2*Sin(x)==1/2,x)", "{{x->ArcSin(1/4)}}");
		check("FindInstance(3+2*Cos(x)==1/2,x)", "{{x->-Pi+ArcCos(5/4)}}");
		check("FindInstance(Sin(x)==0,x)", "{{x->0}}");
		check("FindInstance(Sin(x)==0.0,x)", "{{x->0}}");
		check("FindInstance(Sin(x)==1/2,x)", "{{x->Pi/6}}");
		check("FindInstance(sin(x)==0.5,x)", "{{x->0.5235987755982989}}");
		check("FindInstance(x^2-2500.00==0,x)", "{{x->-50.0}}");
		check("FindInstance(x^2+a*x+1 == 0, x)", "{{x->-a/2-Sqrt(-4+a^2)/2}}");
		check("FindInstance((-3)*x^3 +10*x^2-11*x == (-4), {x})", "{{x->1}}");

		check("FindInstance(x^2+50*x-2500.00==0,x)", "{{x->-80.90169943749474}}");
		check("FindInstance(x+5.0==a,x)", "{{x->-5.0+a}}");

		check("FindInstance(a x + y == 7 && b x - y == 1, {x, y})", "{{x->8/(a+b),y->(a-7*b)/(-a-b)}}");
		check("FindInstance({a x + y == 7, b x - y == 1}, {x, y})", "{{x->8/(a+b),y->(a-7*b)/(-a-b)}}");

	}

	public void testFit() {
		check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, 1, x)", "0.18644067796610156+0.6949152542372881*x");
	}

	public void testFixedPoint() {
		check("FixedPoint((# + 2/#)/2 &, 1.)", "1.414213562373095");
		check("FixedPoint(1 + Floor(#/2) &, 1000)", "2");
		check("21!=0", "True");
		check("{28, 21} /. {a_, b_}  -> {b, Mod(a, b)}", "{21,7}");
		check("{28, 21} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", "{21,7}");
		check("{21, 7} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", "{7,0}");
		check("{7, 0} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", "{7,0}");
		check("FixedPoint(# /. {a_, b_} /; b != 0 -> {b, Mod(a, b)} &, {28, 21})", "{7,0}");
	}

	public void testFlatten() {
		check("Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}})", "{a,b,c,d,e,f,g,h}");
		check("Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}}, 1)", "{a,b,c,{d},e,f,{g,h}}");
		check("Flatten(f(f(x, y), z))", "f(x,y,z)");
		check("Flatten({0, {1}, {{2, -2}}, {{{3}, {-3}}}, {{{{4}}}}}, 0)", "{0,{1},{{2,-2}},{{{3},{-3}}},{{{{4}}}}}");
		check("Flatten(f(g(u, v), f(x, y)), Infinity, g)", "f(u,v,f(x,y))");
		check("Flatten(f(g(u, v), f(x, y)), Infinity, f)", "f(g(u,v),x,y)");
	}

	public void testFloor() {
		check("Floor(1.5)", "1");
		check("Floor(1.5 + 2.7 I)", "1+I*2");
	}

	public void testFold() {
		check("Fold(f, x, {a, b, c, d})", "f(f(f(f(x,a),b),c),d)");
		check("Fold(List, x, {a, b, c, d})", "{{{{x,a},b},c},d}");
		check("Fold(Times, 1, {a, b, c, d})", "a*b*c*d");
		check("Fold(#1^#2 &, x, {a, b, c, d})", "(((x^a)^b)^c)^d");
		check("Catch(Fold(If(# > 10^6, Throw(#), #^2 + #1) &, 2, Range(6)))", "3263442");
		check("Fold(g(#2, #1) &, x, {a, b, c, d})", "g(d,g(c,g(b,g(a,x))))");
		check("Fold(x *#1 + #2 &, 0, {a, b, c, d, e})", "e+x*(d+x*(c+x*(b+a*x)))");
	}

	public void testFoldList() {
		check("foldlist(#^2 + #1 &, 2, range(6))", "{2,6,42,1806,3263442,10650056950806,113423713055421844361000442}");
		check("foldlist(f, x, {a, b, c, d})", "{x,f(x,a),f(f(x,a),b),f(f(f(x,a),b),c),f(f(f(f(x,a),b),c),d)}");
		// check("FoldList(Times, 1, Array(Prime, 10))", "");
		check("foldlist(1/(#2 + #1) &, x, reverse({a, b, c}))", "{x,1/(c+x),1/(b+1/(c+x)),1/(a+1/(b+1/(c+x)))}");
		check("", "");
	}

	public void testFor() {
		check("For($i = 0, $i < 4, $i++, Print($i))", "");
		check("For($i = 0, $i < 4, $i++)", "");
		check("$i = 0;For($j = 0, $i < 4, $i++, Print($i));$i", "4");
		check("$i = 0;For($j = 0, $i < 4, $i++);$i", "4");
		check("$i = 0;For($j = 0, $i < 4, $i++)", "");
		check("For($ = 1, $i < 1000, $i++, If($i > 10, Break())); $i", "11");
		check("For($t = 1; $k = 1, $k <= 5, $k++, $t *= $k; Print($t); If($k < 2, Continue()); $t += 2)", "");
	}

	public void testFractionalPart() {
		check("FractionalPart(2.4)", "0.3999999999999999");
		check("FractionalPart(-2.4)", "-0.3999999999999999");
		check("FractionalPart({-2.4, -2.5, -3.0})", "{-0.3999999999999999,-0.5,0.0}");
	}

	public void testFreeQ() {
		// see notes for MemberQ
		check("FreeQ(x_+y_+z_)[a+b]", "True");
		check("FreeQ(a + b + c, a + c)", "False");
	}

	public void testFresnelC() {
		check("FresnelC(0)", "0");
		check("FresnelC(Infinity)", "1/2");
		check("FresnelC(-Infinity)", "-1/2");
		check("FresnelC(I*Infinity)", "I*1/2");
		check("FresnelC(-I*Infinity)", "-I*1/2");

		check("FresnelC(-z)", "-FresnelC(z)");
		check("FresnelC(I*z)", "I*FresnelC(z)");
		check("FresnelC(1.8)", "0.33363292722155624");

		check("D(FresnelC(x),x)", "Cos(1/2*Pi*x^2)");
	}

	public void testFresnelS() {
		check("FresnelS(0)", "0");
		check("FresnelS(Infinity)", "1/2");
		check("FresnelS(-Infinity)", "-1/2");
		check("FresnelS(I*Infinity)", "-I*1/2");
		check("FresnelS(-I*Infinity)", "I*1/2");

		check("FresnelS(-z)", "-FresnelS(z)");
		check("FresnelS(I*z)", "-I*FresnelS(z)");
		check("FresnelS(1.8)", "0.4509387692675837");
		check("D(Fresnels(x),x)", "Sin(1/2*Pi*x^2)");
	}

	public void testFrobeniusSolve() {
		check("FrobeniusSolve({2, 3, 4}, 29)",
				"{{0,3,5},{0,7,2},{1,1,6},{1,5,3},{1,9,0},{2,3,4},{2,7,1},{3,1,5},{3,5,2},{4,3,3},{\n"
						+ "4,7,0},{5,1,4},{5,5,1},{6,3,2},{7,1,3},{7,5,0},{8,3,1},{9,1,2},{10,3,0},{11,1,1},{\n"
						+ "13,1,0}}");
		check("frobeniussolve({ 12, 16, 20, 27},123 )",
				"{{0,1,4,1},{0,6,0,1},{1,4,1,1},{2,2,2,1},{3,0,3,1},{4,3,0,1},{5,1,1,1},{8,0,0,1}}");
		check("frobeniussolve({ 12, 16, 20, 27},89 )", "{}");
		check("frobeniussolve({1, 5, 10, 25}, 42)",
				"{{2,0,4,0},{2,1,1,1},{2,2,3,0},{2,3,0,1},{2,4,2,0},{2,6,1,0},{2,8,0,0},{7,0,1,1},{\n"
						+ "7,1,3,0},{7,2,0,1},{7,3,2,0},{7,5,1,0},{7,7,0,0},{12,0,3,0},{12,1,0,1},{12,2,2,0},{\n"
						+ "12,4,1,0},{12,6,0,0},{17,0,0,1},{17,1,2,0},{17,3,1,0},{17,5,0,0},{22,0,2,0},{22,\n"
						+ "2,1,0},{22,4,0,0},{27,1,1,0},{27,3,0,0},{32,0,1,0},{32,2,0,0},{37,1,0,0},{42,0,0,\n"
						+ "0}}");
	}

	public void testFromPolarCoordinates() {
		check("FromPolarCoordinates({r, t})", "{r*Cos(t),r*Sin(t)}");
		check("FromPolarCoordinates({r, t, p})", "{r*Cos(t),r*Cos(p)*Sin(t),r*Sin(p)*Sin(t)}");
		check("FromPolarCoordinates({{{r, t}, {1,0}}, {{2, Pi}, {1, Pi/2}}})",
				"{{{r*Cos(t),r*Sin(t)},{1,0}},{{-2,0},{0,1}}}");
	}

	public void testFunction() {
		check("Function({x, y}, x^2 + y^3)[a, b]", "a^2+b^3");
		check("f(x, ##, y, ##) &(a, b, c, d)", "f(x,a,b,c,d,y,a,b,c,d)");
		check("f(x, ##2, y, ##3) &(a, b, c, d)", "f(x,b,c,d,y,c,d)");
		check("If(# > 5, #, False) &(2)", "False");
		check("{##} &(a, b, c)", "{a,b,c}");
		check("{##2} &(a, b, c)", "{b,c}");
		check("Table(a(i0, j), ##) & @@ {{i0, 3}, {j, 2}}", "{{a(1,1),a(1,2)},{a(2,1),a(2,2)},{a(3,1),a(3,2)}}");
		check("Map(Function(-#),-z+w)", "-w+z");
		check("f(#1) &(x, y, z)", "f(x)");
		check("17 & /@ {1, 2, 3}", "{17,17,17}");
		check("(p + #) & /. p -> q", "q+#1&");
		check("FullForm(x -> y &)", "\"Function(Rule(x, y))\"");
		check("FullForm(x -> (y &))", "\"Rule(x, Function(y))\"");
		check("FullForm(Mod(#, 5) == 1 &)", "\"Function(Equal(Mod(Slot(1), 5), 1))\"");
		check("FullForm(a == b && c == d &)", "\"Function(And(Equal(a, b), Equal(c, d)))\"");
		check("FullForm(Mod(#, 3) == 1 && Mod(#, 5) == 1 &)",
				"\"Function(And(Equal(Mod(Slot(1), 3), 1), Equal(Mod(Slot(1), 5), 1)))\"");
	}

	public void testGamma() {
		check("Gamma(8)", "5040");
		// check("Gamma(1.0+I)", "");
		check("Gamma(2.2)", "1.1018024908797128");
	}

	public void testGather() {
		check("Gather({{a, 1}, {b, 1}, {a, 2}, {d, 1}, {b, 3}}, (First(#1) == First(#2)) &)",
				"{{{a,1},{a,2}},{{b,1},{b,3}},{{d,1}}}");
		check("Gather({1,2,3,2,3,4,5,6,2,3})", "{{1},{2,2,2},{3,3,3},{4},{5},{6}}");
		check("Gather(Range(0, 3, 1/3), Floor(#1) == Floor(#2) &)", "{{0,1/3,2/3},{1,4/3,5/3},{2,7/3,8/3},{3}}");
	}

	public void testGCD() {
		check("GCD()", "0");
		check("GCD(10)", "10");
		check("GCD(2, 3, 5)", "1");
		check("GCD(1/3, 2/5, 3/7)", "1/105");
		check("GCD(-3, 9)", "3");
		check("GCD(b, a)", "GCD(a,b)");
	}

	public void testGeometricMean() {
		check("GeometricMean({1, 2.0, 3, 4})", "2.213363839400643");
		check("GeometricMean({Pi,E,2})", "2^(1/3)*(E*Pi)^(1/3)");
		check("GeometricMean({1, 2, 3, 4})", "24^(1/4)");

		check("GeometricMean({})", "GeometricMean({})");
		check("GeometricMean({2, 6, 5, 15, 10, 1})", "9000^(1/6)");
		check("GeometricMean(N({2, 6, 5, 15, 10, 1}))", "4.56079359657056");
	}

	public void testGet() {
		String s = System.getProperty("os.name");
		if (s.contains("Windows")) {
			String pathToVectorAnalysis;
			pathToVectorAnalysis = getClass().getResource("/VectorAnalysis.m").toString();
			// remove 'file:/'
			pathToVectorAnalysis = pathToVectorAnalysis.substring(6);
			System.out.println(pathToVectorAnalysis);
			check("Get(\"" + pathToVectorAnalysis + "\")", "");
			check("DotProduct({a,b,c},{d,e,f}, Spherical)",
					"a*d*Cos(b)*Cos(e)+a*d*Cos(c)*Cos(f)*Sin(b)*Sin(e)+a*d*Sin(b)*Sin(c)*Sin(e)*Sin(f)");
		}
	}

	public void testGreater() {
		check("Pi>0", "True");
		check("Pi+E<8", "True");
		check("2/17 > 1/5 > Pi/10", "False");
		check("x<x", "False");
		check("x<=x", "True");
		check("x>x", "False");
		check("x>=x", "True");
	}

	public void testGroebnerBasis() {
		check("GroebnerBasis({-5*x^2+y*z-x-1, 2*x+3*x*y+y^2,x-3*y+x*z-2*z^2},{x,y,z}, MonomialOrder ->DegreeReverseLexicographic)",
				"{x-3*y+x*z-2*z^2,2*x+3*x*y+y^2,1+x+5*x^2-y*z,-1+27*y+5*y^2-z-29*y*z+18*z^2+y*z^2\n"
						+ "-20*z^3,6-156*y-20*y^2+6*z+174*y*z+y^2*z-104*z^2+120*z^3,180-20*x-4185*y-559*y^2+\n"
						+ "15*y^3+162*z+4680*y*z-2808*z^2+3240*z^3,4026-20*x-106386*y-17140*y^2+4086*z+\n"
						+ "114129*y*z-70866*z^2+78768*z^3+1560*z^4}");
		check("GroebnerBasis({x^2 - 2 y^2, x y - 3}, {x, y})", "{-9+2*y^4,3*x-2*y^3}");
		check("GroebnerBasis({x + y, x^2 - 1, y^2 - 2 x}, {x, y})", "{1}");
		check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x y - z + 2, z^2 - 2 x + 3 y}, {x, y, z})",
				"{1024-832*z-215*z^2+156*z^3-25*z^4+24*z^5+13*z^6+z^8,-11552+2560*y+2197*z+2764*z^\n"
						+ "2+443*z^3+728*z^4+169*z^5+32*z^6+13*z^7,-34656+5120*x+6591*z+5732*z^2+1329*z^3+\n"
						+ "2184*z^4+507*z^5+96*z^6+39*z^7}");
		check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x y - z + 2}, {x, y, z})",
				"{4-y^2+y^4-4*z+z^2+y^2*z^2,-2*x-y+y^3+x*z+y*z^2,2+x*y-z,-1+x^2+y^2+z^2}");
		check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x y - z + 2, z^2 - 3 + x,x - y^2 + 1}, {x, y, z})", "{1}");
	}

	public void testHarmonicNumber() {
		check("HarmonicNumber(2,-3/2)", "1+2*Sqrt(2)");
		check("Table(HarmonicNumber(n), {n, 10})",
				"{1,3/2,11/6,25/12,137/60,49/20,363/140,761/280,7129/2520,7381/2520}");
		check("HarmonicNumber(4,r)", "1+2^(-r)+3^(-r)+4^(-r)");
		check("HarmonicNumber(1,r)", "1");
		check("HarmonicNumber(0,r)", "0");
		check("HarmonicNumber(Infinity,2)", "Pi^2/6");
	}

	public void testHaversine() {
		check("Haversine(0.5)", "0.06120871905481365");
		check("Haversine(1.5+I)", "0.44542339697277344+I*0.5861286494553963");
		check("Haversine(Pi/3)", "1/4");
		check("Haversine(90 Degree)", "1/2");
		check("Haversine({0, Pi/4, Pi/3, Pi/2})", "{0,1/4*(2-Sqrt(2)),1/4,1/2}");
	}

	public void testHead() {
		check("Head(f(a, b))", "f");
		check("Head(a + b + c)", "Plus");
		check("Head(a / b)", "Times");
		check("Head(45)", "Integer");
		check("Head(x)", "Symbol");
		check("Head(f(x)[y][z])", "f(x)[y]");
		check("Head({3, 4, 5})", "List");
		check("FixedPoint(Head, f(x)[y][z])", "Symbol");
		check("FixedPoint(Head, {3, 4, 5})", "Symbol");
	}
	
	public void testHeavisideTheta() {
		check("HeavisideTheta(0)", "HeavisideTheta(0)");
		check("HeavisideTheta(42)", "1");
		check("HeavisideTheta(-1)", "0");
		check("HeavisideTheta(-42)", "0");
		check("HeavisideTheta({1.6, 1.6000000000000000000000000})", "{1,1}");
		check("HeavisideTheta({-1, 0, 1})", "{0,HeavisideTheta(0),1}");
		check("HeavisideTheta(1, 2, 3)", "1");
		check("HeavisideTheta(-2, -1, 1, 2)", "0");
	}

	public void testHermiteH() {
		check("HermiteH(10, x)", "-30240+302400*x^2-403200*x^4+161280*x^6-23040*x^8+1024*x^10");
	}

	public void testHilbertMatrix() {
		check("Inverse(HilbertMatrix(3))", "{{9,-36,30},\n" + " {-36,192,-180},\n" + " {30,-180,180}}");
	}

	public void testHornerForm() {
		check("HornerForm(11*x^3 - 4*x^2 + 7*x + 2)", "2+x*(7+x*(-4+11*x))");
		check("HornerForm(a+b*x+c*x^2,x)", "a+x*(b+c*x)");
	}

	public void testHypergeometric1F1() {
		check("Hypergeometric1F1(1,2,3.0)", "6.361845641062556");
		check("Hypergeometric1F1(1,{2,3,4},5.0)", "{29.4826318205153,11.393052728206118,6.235831636923671}");
	}

	public void testHypergeometric2F1() {
		check("Hypergeometric2F1(0.5,0.333,0.666,0.5)", "1.1856642499574486");
		check("Hypergeometric2F1(0.5,0.333,0.666,-0.5)", "0.9026782488379839");
		check("Hypergeometric2F1(0.5,0.333,0.666,0.75)", "1.397573218428824");
		check("Hypergeometric2F1(0.5,0.333,0.666,-0.75)", "0.8677508558430699");

		// print message: Hypergeometric2F1: No convergence after 50000
		// iterations! Limiting value: 9.789346
		check("Hypergeometric2F1(0.5,0.333,0.666,1)", "Hypergeometric2F1(0.5,0.333,0.666,1.0)");
	}

	public void testIf() {
		check("If(a>b,true)", "");
	}

	public void testIm() {
		check("Im(0)", "0");
		check("Im(I)", "1");
		check("Im(Indeterminate)", "Indeterminate");
		check("Im(Infinity)", "0");
		check("Im(-Infinity)", "0");
		check("Im(ComplexInfinity)", "Indeterminate");
	}

	public void testImplies() {
		check("Implies(True,a)", "a");
		check("Implies(False,a)", "True");
		check("Implies(p,q)", "Implies(p,q)");

		check("Implies(a,True)", "True");
		check("Implies(a,False)", "!a");
		check("Implies(a,a)", "True");
		check("BooleanConvert(Implies(x, y))", "!x||y");
	}

	public void testImportExport() {
		check("Export(\"c:\\\\temp\\\\out.dat\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}})", "\"c:\\temp\\out.dat\"");
		check("Import(\"c:\\\\temp\\\\out.dat\", \"Table\")", "{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");
	}

	public void testInner() {
		check("Inner(Times, {a, b}, {x, y}, Plus)", "a*x+b*y");
		check("Inner(Times, {a, b}, {x, y})", "a*x+b*y");
		check("Inner(Power, {a, b, c}, {x, y, z}, Times)", "a^x*b^y*c^z");
		check("Inner(f, {a, b}, {x, y}, g)", "g(f(a,x),f(b,y))");
		check("Inner(f, {{a, b}, {c, d}}, {x, y}, g)", "{g(f(a,x),f(b,y)),g(f(c,x),f(d,y))}");
		check("Inner(f, {{a, b}, {c, d}}, {{u, v}, {w, x}}, g)",
				"{{g(f(a,u),f(b,w)),g(f(a,v),f(b,x))},{g(f(c,u),f(d,w)),g(f(c,v),f(d,x))}}");
		check("Inner(f, {x, y}, {{a, b}, {c, d}}, g)", "{g(f(x,a),f(y,c)),g(f(x,b),f(y,d))}");
		check("Inner(s, f(1), f(2), t)", "t(s(1,2))");
		check("Inner(And, {{False, False}, {False, True}}, {{True, False}, {True, True}}, Or)",
				"{{False,False},{True,True}}");
		check("Inner(f, {{{a, b}}, {{x, y}}}, {{1}, {2}}, g)", "{{{g(f(a,1),f(b,2))}},{{g(f(x,1),f(y,2))}}}");
	}

	public void testIndeterminate() {
		check("{And(True, Indeterminate), And(False, Indeterminate)}", "{Indeterminate,False}");
		check("Indeterminate==Indeterminate", "False");
		check("Indeterminate===Indeterminate", "True");
		check("{Re(Indeterminate), Im(Indeterminate)}", "{Indeterminate,Indeterminate}");
		check("NumberQ(Indeterminate)", "False");
		check("{1,2,3} Indeterminate", "{Indeterminate,Indeterminate,Indeterminate}");
		check("{1,2,3}+Indeterminate", "{Indeterminate,Indeterminate,Indeterminate}");

		check("Integrate(Indeterminate,x)", "Indeterminate");
		check("D(Indeterminate,x)", "Indeterminate");
		check("DirectedInfinity(Indeterminate)", "ComplexInfinity");
	}

	public void testInsert() {
		check("Insert({a, b, c, d, e}, x, 3)", "{a,b,x,c,d,e}");
		check("Insert({a, b, c, d, e}, x, -2)", "{a,b,c,d,x,e}");
	}

	public void testIntegerExponent() {
		check("IntegerExponent(1230000)", "4");
		check("IntegerExponent(2^10+2^7, 2)", "7");
		check("IntegerExponent(0, 2)", "Infinity");
		check("IntegerExponent(100,100)", "1");
		check("Table(IntegerExponent(n!), {n, 50})",
				"{0,0,0,0,1,1,1,1,1,2,2,2,2,2,3,3,3,3,3,4,4,4,4,4,6,6,6,6,6,7,7,7,7,7,8,8,8,8,8,9,\n"
						+ "9,9,9,9,10,10,10,10,10,12}");
		check("IntegerExponent(2524,2)", "2");
		check("IntegerExponent(-510000)", "4");
	}

	public void testIntegerPart() {
		check("IntegerPart(2.4)", "2");
		check("IntegerPart(-2.4)", "-2");
		check("IntegerPart({-2.4, -2.5, -3.0})", "{-2,-2,-3}");
	}

	public void testIntegrate() {
		check("Integrate(f(x,y),x)", "Integrate(f(x,y),x)");
		check("Integrate(f(x,x),x)", "Integrate(f(x,x),x)");
	}

	public void testInterpolatingFunction() {
		check("InterpolatingFunction({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}})",
				"InterpolatingFunction({{0,0},{1,1},{2,3},{3,4},{4,3},{5,0}})");
		check("ipf=InterpolatingFunction({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}});{ipf[2.5],ipf[3.0],ipf[3.5]}",
				"{3.7109375,4.0,3.7734375}");
		check("InterpolatingFunction({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}})",
				"InterpolatingFunction({{0,0},{1,1},{2,3},{3,4},{4,3},{5,0}})");
	}

	public void testInterpolatingPolynomial() {
		check("InterpolatingPolynomial({1,4},x)", "1+3*(-1+x)");
		check("InterpolatingPolynomial({1,4,9},x)", "1+(-1+x)*(1+x)");
		check("InterpolatingPolynomial({1,4,9,16},x)", "1+(-1+x)*(1+x)");
		check("InterpolatingPolynomial({1,2},x)", "x");

		check("InterpolatingPolynomial({{-1, 4}, {0, 2}, {1, 6}}, x)", "4+(-2+3*x)*(1+x)");
		check("Expand((3*x-2)*(x+1)+4)", "2+x+3*x^2");

		check("InterpolatingPolynomial({{0, 1}, {a, 0}, {b, 0}, {c, 0}}, x)",
				"1+x*((-a+x)*(1/(a*b)-(-b+x)/(a*b*c))-1/a)");

		check("InterpolatingPolynomial({1,2,3,5,8,5},x)", "1+(-1+x)*(1+(-3+x)*(-2+x)*(1/6+(-4+x)*(-1/24-(-5+x)/20)))");

		check("((x-1)*((x-3)*(x-2)*((x-4)*(-1/20*x+5/24)+1/6)+1)+1) /. x -> Range(6)", "{1,2,3,5,8,5}");
	}

	public void testIntersection() {
		check("Intersection({a,a,b,c})", "{a,b,c}");
		check("Intersection({a,a,b,c},{b,a})", "{a,b}");
	}

	public void testInterval() {
		// https://de.wikipedia.org/wiki/Intervallarithmetik
		check("Interval({1, 6}) * Interval({0, 2})", "Interval({0,12})");
		check("Interval({-2, 5})^2", "Interval({0,25})");
		check("Interval({-7, 5})^2", "Interval({0,49})");
		check("Interval({-2, 5})^(-2)", "1/Interval({0,25})");
		check("Interval({2, 5})^2", "Interval({4,25})");
		check("Interval({-2, 5})^3", "Interval({-8,125})");
		check("Interval({-10, -5})^2", "Interval({25,100})");
		check("Pi>3", "True");
		check("3>Pi", "False");
		check("Pi<3", "False");
		check("3<Pi", "True");
		check("Pi>=3", "True");
		check("3>=Pi", "False");
		check("Pi<=3", "False");
		check("3<=Pi", "True");
		// check("Max(Interval({4,2}))", "4");
		check("Interval({5,8})>2", "True");
		check("Interval({3,4})>Pi", "Interval({3,4})>Pi");
		check("Interval({1,2})>Pi", "False");
		check("Interval({5,8})<2", "False");
		check("Interval({3,4})<Pi", "Interval({3,4})<Pi");
		check("Interval({1,2})<Pi", "True");
		check("Interval({5,8})>=2", "True");
		check("Interval({3,4})>=Pi", "Interval({3,4})>=Pi");
		check("Interval({1,2})>=Pi", "False");
		check("Interval({5,8})<=2", "False");
		check("Interval({3,4})<=Pi", "Interval({3,4})<=Pi");
		check("Interval({1,2})<=Pi", "True");

		check("Interval({5,8})>Interval({1,2})", "True");
		check("Interval({3,4})>Interval({Pi,5})", "Interval({3,4})>Interval({Pi,5})");
		check("Interval({1,2})>Interval({Pi,5})", "False");
		check("Interval({5,8})<Interval({1,2})", "False");
		check("Interval({3,4})<Interval({Pi,5})", "Interval({3,4})<Interval({Pi,5})");
		check("Interval({1,2})<Interval({Pi,5})", "True");

		check("Limit(Sin(x),x->Infinity)", "Interval({-1,1})");
		check("Limit(Sin(x),x->-Infinity)", "Interval({-1,1})");
		check("Limit(Sin(1/x),x->0)", "Interval({-1,1})");
		check("Max(Interval({2,4}))", "4");
		check("Min(Interval({2,4}))", "2");
	}

	public void testInverse() {
		check("Inverse({{u, v}, {v, u}})", "{{u/(u^2-v^2),-v/(u^2-v^2)},\n" + " {-v/(u^2-v^2),u/(u^2-v^2)}}");
	}

	public void testInverseErf() {
		check("InverseErf(0)", "0");
		check("InverseErf(1)", "Infinity");
		check("InverseErf(-1)", "-Infinity");
		check("InverseErf(0.6)", "0.5951160814499948");
		check("Sqrt(2)*InverseErf(0.99)", "2.5758293035489004");
		check("InverseErf(1/{2., 3., 4., 5.})",
				"{0.47693627620446977,0.3045701941739856,0.22531205501217808,0.17914345462129166}");
		check("InverseErf(-1/{2., 3., 4., 5.})",
				"{-0.47693627620446977,-0.3045701941739856,-0.22531205501217808,-0.17914345462129166}");
		check("InverseErf({-2.,-3.,3.})", "{InverseErf(-2.0),InverseErf(-3.0),InverseErf(3.0)}");
	}

	public void testInverseErfc() {
		check("InverseErfc(0)", "Infinity");
		check("InverseErfc(1)", "0");
		check("InverseErfc(2)", "-Infinity");
		check("InverseErfc(0.6)", "0.37080715859355795");
		check("Sqrt(2)*InverseErfc(0.99)", "0.012533469508069274");
		check("InverseErfc(1/{2., 3., 4., 5.})",
				"{0.47693627620446977,0.6840703496566226,0.8134198475976184,0.9061938024368233}");
		check("InverseErfc(-1/{2., 3., 4., 5.})",
				"{InverseErfc(-0.5),InverseErfc(-0.3333333333333333),InverseErfc(-0.25),InverseErfc(-0.2)}");
	}

	public void testInverseFunction() {
		check("InverseFunction(Abs)", "-#1&");
		check("InverseFunction(Sin)", "ArcSin");
	}

	public void testInverseHaversine() {
		check("InverseHaversine(1/4)", "Pi/3");
		check("InverseHaversine(0.7)", "1.9823131728623846");

		check("ArcSin(1.3038404810405297)", "1.5707963267948966+I*(-0.7610396837318266)");
		check("InverseHaversine(1.7)", "3.141592653589793+I*(-1.5220793674636532)");
	}

	public void testJaccardDissimilarity() {
		check("JaccardDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", "3/5");
		check("JaccardDissimilarity({True, False, True}, {True, True, False})", "2/3");
		check("JaccardDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", "0");
		check("JaccardDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", "1");
	}

	public void testJacobiSymbol() {
		check("JacobiSymbol(10^10+1, Prime[1000])", "1");
		check("JacobiSymbol(10^11+1, Prime[2000])", "-1");
		check("JacobiSymbol(10, 5)", "0");
		check("Table(f(n, m), {n, 0, 10}, {m, 1, n, 2})",
				"{{},{f(1,1)},{f(2,1)},{f(3,1),f(3,3)},{f(4,1),f(4,3)},{f(5,1),f(5,3),f(5,5)},{f(\n"
						+ "6,1),f(6,3),f(6,5)},{f(7,1),f(7,3),f(7,5),f(7,7)},{f(8,1),f(8,3),f(8,5),f(8,7)},{f(\n"
						+ "9,1),f(9,3),f(9,5),f(9,7),f(9,9)},{f(10,1),f(10,3),f(10,5),f(10,7),f(10,9)}}");
		check("Table(JacobiSymbol(n, m), {n, 0, 10}, {m, 1, n, 2})",
				"{{},{1},{1},{1,0},{1,1},{1,-1,0},{1,0,1},{1,1,-1,0},{1,-1,-1,1},{1,0,1,1,0},{1,1,\n" + "0,-1,1}}");
		check("JacobiSymbol(1001, 9907)", "-1");
		check("JacobiSymbol({2, 3, 5, 7, 11}, 3)", "{-1,0,-1,1,-1}");
		check("JacobiSymbol(3, {1, 3, 5, 7})", "{1,0,-1,-1}");
		check("JacobiSymbol(7, 6)", "1");
		// check("JacobiSymbol(n, 1)", "n");
		check("JacobiSymbol(-3, {1, 3, 5, 7})",
				"{JacobiSymbol(-3,1),JacobiSymbol(-3,3),JacobiSymbol(-3,5),JacobiSymbol(-3,7)}");
	}

	public void testJoin() {
		check("Join(x, y)", "Join(x,y)");
		check("Join({a,b}, {x,y,z})", "{a,b,x,y,z}");
		check("Join({{a, b}, {x, y}}, {{1, 2}, {3, 4}})", "{{a,b},{x,y},{1,2},{3,4}}");
	}

	public void testKurtosis() {
		check("Kurtosis({1.1, 1.2, 1.4, 2.1, 2.4})", "1.4209750290831376");
	}

	public void testLaguerreL() {
		check("LaguerreL(3, x)", "1-3*x+3/2*x^2-x^3/6");
		check("LaguerreL(4, x)", "1-4*x+3*x^2-2/3*x^3+x^4/24");
		check("LaguerreL(5, x)", "1-5*x+5*x^2-5/3*x^3+5/24*x^4-x^5/120");
	}

	public void testLaplaceTransform() {
		check("LaplaceTransform(t, t, t)", "LaplaceTransform(t,t,t)");
		check("LaplaceTransform(t, t, s)", "1/s^2");
		check("LaplaceTransform(t, s, t)", "1");
		check("LaplaceTransform(s, t, t)", "LaplaceTransform(s,t,t)");
		check("LaplaceTransform(E^(-t), t, s)", "1/(1+s)");
		check("LaplaceTransform(t^4 Sin(t), t, s)", "(384*s^4)/(1+s^2)^5+(-288*s^2)/(1+s^2)^4+24/(1+s^2)^3");
		check("LaplaceTransform(t^(1/2), t, s)", "Sqrt(Pi)/(2*s^(3/2))");
		check("LaplaceTransform(t^(1/3), t, s)", "Gamma(4/3)/s^(4/3)");
		check("LaplaceTransform(t^a, t, s)", "Gamma(1+a)/s^(1+a)");
		check("LaplaceTransform(Sin(t), t, s)", "1/(1+s^2)");
		check("LaplaceTransform(Sin(t), t, t)", "1/(1+t^2)");
		check("LaplaceTransform(Cos(t), t, s)", "s/(1+s^2)");
		check("LaplaceTransform(Sinh(t), t, s)", "c/(-1+s^2)");
		check("LaplaceTransform(Cosh(t), t, s)", "s/(-1+s^2)");
		check("LaplaceTransform(Log(t), t, s)", "-(EulerGamma+Log(s))/s");
		check("LaplaceTransform(Log(t)^2, t, s)", "(6*EulerGamma^2+Pi^2+6*(2*EulerGamma+Log(s))*Log(s))/(6*s)");
		check("LaplaceTransform(Erf(t), t, s)", "(E^(s^2/4)*Erfc(s/2))/s");
		check("LaplaceTransform(Erf(t^(1/2)), t, s)", "1/(s*Sqrt(1+s))");

		check("LaplaceTransform(Sin(t)*Exp(t), t, s)", "1/(1+(1-s)^2)");
	}

	public void testLCM() {
		check("LCM(10)", "10");
		check("LCM(2, 3, 5)", "30");
		check("LCM(-3, 7)", "21");
		check("LCM(4)", "4");
		check("LCM(2, {3, 5, 7})", "{6,10,14}");
		check("LCM(0,0)", "0");
		check("LCM(0,10)", "0");
		check("LCM(10,0)", "0");
		check("LCM(a)", "LCM(a)");
		// check("LCM(1/3, 2/5, 3/7)", "");
	}

	public void testLegendreP() {
		check("LegendreP(1,x)", "x");
		check("LegendreP(4,x)", "3/8-15/4*x^2+35/8*x^4");
		check("LegendreP(7,x)", "-35/16*x+315/16*x^3-693/16*x^5+429/16*x^7");
		check("LegendreP(10,x)", "-63/256+3465/256*x^2-15015/128*x^4+45045/128*x^6-109395/256*x^8+46189/256*x^10");
	}

	// public void testJacobianMatrix() {
	// check("JacobianMatrix({Rr, Ttheta, Zz}, Cylindrical)", "");
	// }

	public void testLength() {
		check("Length(a + b + c + d)", "4");
		check("Length(x)", "0");
		check("Length(1/10)", "0");
		check("Length(3 + I)", "0");
		check("Map(Length, {{a, b}, {a, b, c}, {x}})", "{2,3,1}");
	}

	public void testLess() {
		check("(2*x+5)<(5^(1/2))", "x<1/2*(-5+Sqrt(5))");
		check("(-2*x+5)<(5^(1/2))", "x>-(-5+Sqrt(5))/2");
	}

	public void testLetterQ() {
		check("LetterQ(\"a\")", "True");
		check("LetterQ(\"2\")", "False");
		check("LetterQ(\"äü\")", "True");
	}

	public void testLevel() {
		check("Level(a + f(x, y^n), {-1})", "{a,x,y,n}");
		check("Level(a + f(x, y^n0), 2)", "{a,x,y^n0,f(x,y^n0)}");
		check("Level(a + f(x, y^n0), {0, Infinity})", "{a,x,y,n0,y^n0,f(x,y^n0),a+f(x,y^n0)}");
		check("Level({{{{a}}}}, 1)", "{{{{a}}}}");
		check("Level({{{{a}}}}, 2)", "{{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, 3)", "{{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, 4)", "{a,{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, 5)", "{a,{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -1)", "{a,{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -2)", "{{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -3)", "{{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -4)", "{{{{a}}}}");
		check("Level({{{{a}}}}, -5)", "{}");
		check("Level({{{{a}}}}, {2, 3})", "{{a},{{a}}}");
		check("Level({{{{a}}}}, {0, -1})", "{a,{a},{{a}},{{{a}}},{{{{a}}}}}");
		check("Level(h0(h1(h2(h3(a)))), {0, -1})", "{a,h3(a),h2(h3(a)),h1(h2(h3(a))),h0(h1(h2(h3(a))))}");
		check("Level({{{{a}}}}, 3, Heads -> True)", "{List,List,List,{a},{{a}},{{{a}}}}");
		check("Level(x^2 + y^3, 3, Heads -> True)", "{Plus,Power,x,2,x^2,Power,y,3,y^3}");
		check("Level(h1(h2(h3(x))), -1)", "{x,h3(x),h2(h3(x))}");
		check("Level(h1(h2(h3(x))), {0, -1})", "{x,h3(x),h2(h3(x)),h1(h2(h3(x)))}");

		check("Level(f(f(g(a), a), a, h(a), f), 2)", "{g(a),a,f(g(a),a),a,a,h(a),f}");
		check("Level(f(f(g(a), a), a, h(a), f), {2})", "{g(a),a,a}");
		check("Level(f(f(g(a), a), a, h(a), f), {-1})", "{a,a,a,a,f}");
		check("Level(f(f(g(a), a), a, h(a), f), {-2})", "{g(a),h(a)}");
	}

	public void testLimit() {
		check("Limit(Log(x), x -> 0)", "-Infinity");
		check("Limit(x^x, x -> 0)", "1");
		check("Limit(1/x, x -> Infinity, Direction->1)", "0");
		check("Limit(1/x, x -> Infinity, Direction->-1)", "0");
		check("Limit(1/x, x -> 0, Direction->1)", "-Infinity");
		check("Limit(1/x, x -> 0, Direction->-1)", "Infinity");
		check("1/0", "ComplexInfinity");
		// check("Limit((4 - x), x -> 4)", "0");
		check("Limit(1/(4 - x), x -> 4)", "-Infinity");
		check("Limit(1/(x - 4), x -> 4)", "Infinity");
		check("Limit(1/(4 - x), x -> 4)", "-Infinity");

		check("Infinity-1", "Infinity");
		check("Limit(a+b+2*x,x->-Infinity)", "-Infinity");
		check("Limit(a+b+2*x,x->Infinity)", "Infinity");
		check("Limit(E^(-x)*Sqrt(x), x -> Infinity)", "0");
		check("Limit(Sin(x)/x,x->0)", "1");
		check("Limit(-x,x->Infinity)", "-Infinity");
		check("Limit(Sin(x)/x, x -> 0)", "1");
		check("Limit((1 + x/n)^n, n -> Infinity)", "E^x");
		check("Limit((x^2 - 2 x - 8)/(x - 4), x -> 4)", "6");
		check("Limit((x^3-1)/(2*x^3-3x),x->Infinity)", "1/2");
		check("Limit((x^3-1)/(2*x^3+3x),x->Infinity)", "1/2");

		check("Limit((2*x^3-3x),x->Infinity)", "Infinity");
		check("Limit((2x^3+3x),x->Infinity)", "Infinity");

		check("Limit(E^x,x->-Infinity)", "0");
		// TOOO distinguish between upper and lower limit convergence
		check("Limit(1/(x - 4), x -> 4)", "Infinity");

	}

	public void testLinearProgramming() {
		check("LinearProgramming({1, 1}, {{1, 2}}, {3})", "{0.0,1.5}");
		check("LinearProgramming({1, 1}, {{1, 2}}, {{3,0}})", "{0.0,1.5}");
		check("LinearProgramming({1, 1}, {{1, 2}}, {{3,-1}})", "{0.0,0.0}");
		check("LinearProgramming({1., 1.}, {{5., 2.}}, {3.})", "{0.6,0.0}");
	}

	public void testLinearSolve() {
		check("LinearSolve({{1, 1, 1}, {1, 2, 3}, {1, 4, 9}}, {1, 2, 3})", "{-1/2,2,-1/2}");
		check("LinearSolve(N({{1, 1, 1}, {1, 2, 3}, {1, 4, 9}}), N({1, 2, 3}))", "{-0.5,2.0,-0.5}");
		check("LinearSolve({{a, b}, {c, d}}, {x, y})", "{(d*x-b*y)/(-b*c+a*d),(-c*x+a*y)/(-b*c+a*d)}");
	}

	public void testLog() {
		// test alias
		check("Ln(E)", "1");
		check("ln(E)", "1");

		check("Log(Pi^E)", "E*Log(Pi)");
		check("Log(E)", "1");
		check("Log(-E)", "1+I*Pi");
		check("D(Log(a, x),x)", "1/(x*Log(a))");
		check("Log(1000.)", "6.907755278982137");
		check("Log(2.5 + I)", "0.9905007344332918+I*0.3805063771123649");
		check("Log({2.1, 3.1, 4.1})", "{0.7419373447293773,1.1314021114911006,1.410986973710262}");
		check("Log(2, 16)", "4");
		check("Log(10, 1000)", "3");
		check("Log(10, 10)", "1");
		check("Log(0)", "-Infinity");
		check("Log(1)", "0");
		check("Log(-1)", "I*Pi");
		check("Log(I)", "I*1/2*Pi");
		check("Log(-I)", "-I*1/2*Pi");
		check("Log(GoldenRatio)", "ArcCsch(2)");
		check("Log(Infinity)", "Infinity");
		check("Log(-Infinity)", "Infinity");

		check("Log(I*Infinity)", "Infinity");
		check("Log(-I*Infinity)", "Infinity");
		check("Log(ComplexInfinity)", "Infinity");
	}

	public void testLog10() {
		check("Log10(x)", "Log(10,x)");
	}

	public void testLog2() {
		check("Log2(x)", "Log(2,x)");
	}

	public void testLogisticSigmoid() {
		check("LogisticSigmoid(0.5)", "0.6224593312018546");
		check("LogisticSigmoid(0.5 + 2.3 I)", "1.0647505893884985+I*0.8081774171575826");
		check("LogisticSigmoid({-0.2, 0.1, 0.3})", "{0.45016600268752216,0.52497918747894,0.574442516811659}");
	}

	public void testMap() {
		check("Map(List,Join({1,2,3},4-{1,2,3}))", "{{1},{2},{3},{3},{2},{1}}");
		check("Map(f, {{{{{a}}}}}, 2)", "{f({f({{{a}}})})}");
		check("Map(f, {{{{{a}}}}}, {2})", "{{f({{{a}}})}}");
		check("Map(f, {{{{{a}}}}}, {0,2})", "f({f({f({{{a}}})})})");
		check("Map(f, {{{{{a}}}}}, Infinity)", "{f({f({f({f({f(a)})})})})}");
		check("Map(f, {{{{{a}}}}}, {0, Infinity})", "f({f({f({f({f({f(a)})})})})})");
		check("Map(f, {{{{{a}}}}}, 3)", "{f({f({f({{a}})})})}");
		check("Map(f, {{{{{a}}}}}, Infinity)", "{f({f({f({f({f(a)})})})})}");
		check("Map(f, {{{{{a}}}}}, {0, Infinity})", "f({f({f({f({f({f(a)})})})})})");

		check("Map(f, {{{{{a}}}}}, {2, -3})", "{{f({f({{a}})})}}");
		check("Map(f, h0(h1(h2(h3(h4(a))))), {2, -3})", "h0(h1(f(h2(f(h3(h4(a)))))))");
		check("Map(f, {{{{a}}}}, 2, Heads -> True)", "f(List)[f(f(List)[f({{a}})])]");

		check("Map(f, {a, b, c})", "{f(a),f(b),f(c)}");
		check("Map(f, {a, b, c}, Heads -> True)", "f(List)[f(a),f(b),f(c)]");
	}

	public void testMapAt() {
		check("MapAt(f, {a, b, c, d}, 2)", "{a,f(b),c,d}");
	}

	public void testMapThread() {
		check("MapThread(f, {{a, b, c}, {x, y, z}})", "{f(a,x),f(b,y),f(c,z)}");
	}

	public void testMatchingDissimilarity() {
		check("MatchingDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", "3/5");
		check("MatchingDissimilarity({True, False, True}, {True, True, False})", "2/3");
		check("MatchingDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", "0");
		check("MatchingDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", "1");
	}

	public void testMatchQ() {
		check("MatchQ(_Integer)[123]", "True");
		check("MatchQ(22/7, _Rational)", "True");
		check("MatchQ(6/3, _Rational)", "False");
	}

	public void testMatrices() {
		check("Table(a(i0, j), {i0, 2}, {j, 2})", "{{a(1,1),a(1,2)},{a(2,1),a(2,2)}}");
		check("Array(a, {2, 2})", "{{a(1,1),a(1,2)},{a(2,1),a(2,2)}}");
		check("ConstantArray(0, {3, 2})", "{{0,0},{0,0},{0,0}}");
		check("DiagonalMatrix({a, b, c})", "{{a,0,0},{0,b,0},{0,0,c}}");
		check("IdentityMatrix(3)", "{{1,0,0},\n" + " {0,1,0},\n" + " {0,0,1}}");
	}

	public void testMatrixRank() {
		check("MatrixRank({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", "2");
		check("MatrixRank({{1, 0}, {3, 2}, {7, 2}, {8, 1}})", "2");
		check("MatrixRank({{a, b}, {c, d}})", "2");
		check("MatrixRank({{a, b}, {2*a, 2*b}})", "1");
		check("MatrixRank({{1., 2., 3.}, {4., 5., 6.}, {7., 8., 9.}})", "2");
		check("MatrixRank({{1, I}, {I, -1}})", "1");
		check("MatrixRank({{1, 2, 3, 4.0 },\n" + "{ 1, 1, 1, 1 },\n" + "{ 2, 3, 4, 5 },\n" + "{ 2, 2, 2, 2 }})", "2");
		check("MatrixRank({{ 1.0, 2.0, 3.0, 4.0 },\n" + "{ 1.0, 1.0, 1.0, 1.0 },\n" + "{ 2.0, 3.0, 4.0, 5.0 },\n"
				+ "{ 2.0, 2.0, 2.0, 2.0 }})", "2");
	}

	public void testMax() {
		check("Max(Abs(x), Abs(y))", "Max(Abs(x),Abs(y))");
	}

	public void testMean() {
		check("Mean({{a, u}, {b, v}, {c, w}})", "{1/3*(a+b+c),1/3*(u+v+w)}");
		check("Mean({1.21, 3.4, 2.15, 4, 1.55})", "2.4619999999999997");
		check("Mean({a,b,c,d})", "1/4*(a+b+c+d)");

		check("Mean(BernoulliDistribution(p))", "p");
		check("Mean(PoissonDistribution(p))", "p");
		check("Mean(BinomialDistribution(n, p))", "n*p");
		check("Mean(NormalDistribution(n, p))", "n");
		check("Mean(HypergeometricDistribution(n, ns, nt))", "(n*ns)/nt");
	}

	public void testMedian() {
		check("Median({1,2,3,4,5,6,7.0})", "4.0");
		check("Median({1,2,3,4,5,6,7.0,8})", "4.5");
		check("Median({1,2,3,4,5,6,7})", "4");
	}

	public void testMemberQ() {
		check("MemberQ(x^_)[{x^2, y^2, x^3}]", "True");
		check("MemberQ({1, 3, 4, 1, 2}, 2)", "True");
		check("MemberQ({x^2, y^2, x^3}, x^_)", "True");
		check("MemberQ(a + b + f(c), f)", "False");
		check("MemberQ(a + b + f(c), f, Heads->True)", "True");
		check("MemberQ(a + b + c, a + c)", "False");
	}

	public void testMod() {
		check("Mod(-10,3)", "2");
		check("Mod(10,3)", "1");
		check("Mod(10,-3)", "-2");
		check("Mod(-10,-3)", "-1");

		check("Mod(-23,7)", "5");
		check("Mod(23,7)", "2");
		check("Mod(23,-7)", "-5");
		check("Mod(-23,-7)", "-2");
	}

	public void testModule() {
		check("xm=10;Module({xm=xm}, xm=xm+1;xm)", "11");
		check("xm=10;Module({xm=xm}, xm=xm+1;xm);xm", "10");
		check("xm=10;Module({t=xm}, xm=xm+1;t)", "10");
		check("xm=10;Module({t=xm}, xm=xm+1;t);xm", "11");
		check("Module({a}, Block({a}, a))", "a");
		check("Module({a}, Block({}, a))", "a$6");
		check("t === Module({t}, t)", "False");
		check("$g(x_) := Module({v=x},int(v,x)/;v=!=x);$g(f(x))", "$g(f(x))");
		check("$g(x_) := Module({v=x},int1(v,x)/;v===x);$g(f(x))", "int1(f(x),f(x))");
		check("$h(x_) := Module({$u}, $u^2 /; (($u = x - 1) > 0));$h(6)", "25");
		check("$f(x0_) :=\n" + " Module({x = x0},\n" + "  While(x > 0, x = Log(x));\n" + "  x\n" + "  );$f(2.0)",
				"-0.36651292058166435");

		check("$fib(n_) :=\n" + " Module({$f},\n" + "  $f(1) = $f(2) = 1;\n"
				+ "  $f(i0_) := $f(i0) = $f(i0 - 1) + $f(i0 - 2);\n" + "  $f(n)\n" + "  );$fib(5)", "5");

		check("$gcd(m0_, n0_) :=\n" + " Module({m = m0, n = n0},\n" + "  While(n != 0, {m, n} = {n, Mod(m, n)});\n"
				+ "  m\n" + "  );$gcd(18, 21)", "3");

		if (Config.SERVER_MODE == false) {
			check("f(x0_) :=\n" + " Module({x = x0},\n" + "  While(x > 0, x = Log(x));\n" + "  x\n" + "  );f(2.0)",
					"-0.36651292058166435");

			check("fib(n_) :=\n" + " Module({f},\n" + "  f(1) = f(2) = 1;\n"
					+ "  f(i_) := f(i) = f(i - 1) + f(i - 2);\n" + "  f(n)\n" + "  );fib(5)", "5");

			check("gcd(m0_, n0_) :=\n" + " Module({m = m0, n = n0},\n" + "  While(n != 0, {m, n} = {n, Mod(m, n)});\n"
					+ "  m\n" + "  );gcd(18, 21)", "3");
		}
	}

	public void testMoebiusMu() {
		check("MoebiusMu(47)", "-1");
		check("MoebiusMu(51)", "1");
		check("MoebiusMu(17291)", "-1");
		check("MoebiusMu({2, 4, 7, 9})", "{-1,0,-1,0}");
		check("MoebiusMu(-100)", "0");
	}

	public void testMonomialList() {
		check("MonomialList((x + y)^3)", "{x^3,3*x^2*y,3*x*y^2,y^3}");
		check("MonomialList(x^2 y^2 + x^3, {x, y})", "{x^3,x^2*y^2}");
		check("MonomialList(x^2 y^2 + x^3, {x, y},\"DegreeLexicographic\")", "{x^2*y^2,x^3}");
		check("MonomialList((x + 1)^5, x, Modulus -> 2)", "{x^5,x^4,x,1}");

		check("MonomialList(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z})",
				"{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-10*x^2*y*z^5,-7*x*y^5*z^4,6*x*y^4*z^3,6*x*y^3*z^\n"
						+ "3,3*x*y^2*z,y^4*z,-7*y^2*z,2*z^5}");

		check("MonomialList(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"NegativeLexicographic\")",
				"{2*z^5,-7*y^2*z,y^4*z,3*x*y^2*z,6*x*y^3*z^3,6*x*y^4*z^3,-7*x*y^5*z^4,-10*x^2*y*z^\n"
						+ "5,7*x^2*y^5*z^3,-10*x^5*y^4*z^2}");
		check("MonomialList(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"DegreeLexicographic\")",
				"{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-7*x*y^5*z^4,-10*x^2*y*z^5,6*x*y^4*z^3,6*x*y^3*z^\n"
						+ "3,y^4*z,2*z^5,3*x*y^2*z,-7*y^2*z}");
		check("MonomialList(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"NegativeDegreeReverseLexicographic\")",
				"{-7*y^2*z,3*x*y^2*z,y^4*z,2*z^5,6*x*y^3*z^3,6*x*y^4*z^3,-10*x^2*y*z^5,7*x^2*y^5*z^\n"
						+ "3,-7*x*y^5*z^4,-10*x^5*y^4*z^2}");
		check("MonomialList(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"DegreeReverseLexicographic\")",
				"{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-7*x*y^5*z^4,6*x*y^4*z^3,-10*x^2*y*z^5,6*x*y^3*z^\n"
						+ "3,y^4*z,2*z^5,3*x*y^2*z,-7*y^2*z}");
		check("MonomialList(-10 x^5 y^4 z^2 + 7 x^2 y^5 z^3 - 10 x^2 y z^5 - 7 x y^5 z^4 +  6 x y^4 z^3 + 6 x y^3 z^3 + 3 x y^2 z + y^4 z - 7 y^2 z + 2 z^5, {x, y, z}, \"NegativeDegreeLexicographic\")",
				"{-7*y^2*z,3*x*y^2*z,y^4*z,2*z^5,6*x*y^3*z^3,-10*x^2*y*z^5,6*x*y^4*z^3,7*x^2*y^5*z^\n"
						+ "3,-7*x*y^5*z^4,-10*x^5*y^4*z^2}");
	}

	public void testMultinomial() {
		check("Multinomial(1)", "1");
		check("Multinomial(f(x))", "1");
		check("Multinomial(f(x), g(x))", "Binomial(f(x)+g(x),f(x))");
		check("Multinomial(n-k, k)", "Binomial(n,-k+n)");
		check("Multinomial(k, 2)", "Binomial(2+k,k)");
	}

	public void testNames() {
		check("Names(\"Int*\" )",
				"{Interval,IntegerExponent,IntegerPart,IntegerPartitions,IntegerQ,Integrate,Interpolation,InterpolatingFunction,InterpolatingPolynomial,Intersection,Integer,Integers}");
		check("Names(\"Integer*\" )", "{IntegerExponent,IntegerPart,IntegerPartitions,IntegerQ,Integer,Integers}");
		check("Names(\"IntegerPart\" )", "{IntegerPart}");
		// check("Names(\"*\" )", "{}");
	}

	public void testNand() {
		check("Nand( )", "False");
		check("Nand(2+2)", "!4");
		check("Nand(x,y,z)", "Nand(x,y,z)");
		check("Nand(x,True,z)", "Nand(x,z)");
		check("Nand(x,False,z)", "True");
		check("Nand(True,False)", "True");
		check("Nand(Print(1); False, Print(2); True)", "True");
		check("Nand(Print(1); True, Print(2); True)", "False");
		check("BooleanConvert(Nand(p, q, r))", "!p||!q||!r");
		check("BooleanConvert(!Nand(p, q, r))", "p&&q&&r");
	}

	public void testNearest() {
		check("Nearest[{1, 2, 4, 8, 16, 32}, 20]", "{16}");
		check("Nearest[{1, 2, 4, 8, 16, 24, 32}, 20]", "{16,24}");
	}

	public void testNest() {
		check("Nest(f, x, 3)", "f(f(f(x)))");
		check("Nest((1 + #)^2 &, 1, 3)", "676");
		check("Nest((1 + #)^2 &, x, 5)", "(1+(1+(1+(1+(1+x)^2)^2)^2)^2)^2");
		check("Nest(Sqrt, 100.0, 4)", "1.333521432163324");
	}

	public void testNestList() {
		check("NestList(f, x, 4)", "{x,f(x),f(f(x)),f(f(f(x))),f(f(f(f(x))))}");
		check("NestList(Cos, 1.0, 10)",
				"{1.0,0.5403023058681398,0.8575532158463934,0.6542897904977791,0.7934803587425656,0.7013687736227565,0.7639596829006542,0.7221024250267077,0.7504177617637605,0.7314040424225098,0.7442373549005569}");
		check("NestList((1 + #)^2 &, x, 3)", "{x,(1+x)^2,(1+(1+x)^2)^2,(1+(1+(1+x)^2)^2)^2}");
	}

	public void testNestWhile() {
		check("NestWhile(#/2 &, 123456, EvenQ)", "1929");
		check("NestWhile(Log, 100, # > 0 &)", "Log(Log(Log(Log(100))))");
	}

	public void testNestWhileList() {
		check("NestWhileList(#/2 &, 123456, EvenQ)", "{123456,61728,30864,15432,7716,3858,1929}");
		check("NestWhileList(Log, 100, # > 0 &)",
				"{100,Log(100),Log(Log(100)),Log(Log(Log(100))),Log(Log(Log(Log(100))))}");
	}

	public void testNIntegrate() {
		check("NIntegrate(Cos(x), {x, 0, Pi})", "0.0");
		check("NIntegrate(1/Sin(Sqrt(x)), {x, 0, 1}, PrecisionGoal->10)", "2.1108620052");
	}

	public void testNMaximize() {
		check("NMaximize({-x - y, 3 x + 2 y >= 7 && x + 2 y >= 6 && x >= 0 && y >= 0}, {x, y})",
				"{-3.2500000000000004,{0.5000000000000009,2.7499999999999996}}");
	}

	public void testNMinimize() {
		check("NMinimize({x + 2*y, -5*x + y == 7 && x + y >= 26 && x >= 3 && y >= 4}, {x, y})",
				"{48.83333333333333,{3.1666666666666665,22.833333333333332}}");
		check("NMinimize({x + y, 3*x + 2*y >= 7 && x + 2 y >= 6 && x >= 0 && y >= 0}, {x, y})",
				"{3.2500000000000004,{0.5000000000000009,2.7499999999999996}}");
	}

	public void testNonCommutativeMultiply() {
		check("{0 ** a, 1 ** a}", "{0**a,1**a}");
		check("{a*b == b*a, a ** b == b ** a}", "{True,a**b==b**a}");
		check("a ** (b ** c) == (a ** b) ** c", "True");
		check("NonCommutativeMultiply(a)", "NonCommutativeMultiply(a)");
	}

	public void testNoneTrue() {
		check("NoneTrue({1, 2, 3, 4, 5, 6}, EvenQ)", "False");
		check("NoneTrue({1, 3, 5, 7}, EvenQ)", "True");
		check("NoneTrue({12, 16, x, 14, y}, # < 10 &)", "Nor(x<10,y<10)");
		check("NoneTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", "True");
		check("NoneTrue(f(1, 7, 3), OddQ)", "False");
	}

	public void testNor() {
		check("Nor( )", "True");
		check("Nor(2+2)", "!4");
		check("Nor(True,False)", "False");
		check("Nor(x,y,z)", "Nor(x,y,z)");
		check("Nor(x,True,z)", "False");
		check("Nor(x,False,z)", "Nor(x,z)");
		check("BooleanConvert(Nor(p, q, r))", "!p&&!q&&!r");
		check("BooleanConvert(!Nor(p, q, r))", "p||q||r");
	}

	public void testNorm() {
		check("Norm(0)", "0");
		check("Norm({x, y}, 0)", "The function: Norm({x,y},0) has wrong argument 0 at position:2:\n"
				+ "0 not allowed as second argument!");
		check("Norm({x, y}, 0.5)",
				"The function: Norm({x,y},0.5) has wrong argument 0.5 at position:2:\n" + "Second argument is < 1!");
		check("Norm({})", "Norm({})");
		check("Norm({1, 2, 3, 4}, 2)", "Sqrt(30)");
		check("Norm({10, 100, 200}, 1)", "310");
		check("Norm({a,b,c})", "Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)");

		check("Norm({x, y, z}, Infinity)", "Max(Abs(x),Abs(y),Abs(z))");
		check("Norm({x, y, z})", "Sqrt(Abs(x)^2+Abs(y)^2+Abs(z)^2)");
		check("Norm({x, y, z}, p)", "(Abs(x)^p+Abs(y)^p+Abs(z)^p)^(1/p)");

		check("Norm(-2+I)", "Sqrt(5)");
		check("Norm({1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1})", "Sqrt(5)");
		check("Norm(N({1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1}))", "2.23606797749979");
	}

	public void testNormalize() {
		check("Normalize({0})", "{0}");
		check("Normalize(0)", "0");
		check("Normalize({1,5,1})", "{1/(3*Sqrt(3)),5/3*1/Sqrt(3),1/(3*Sqrt(3))}");
		check("Normalize({x,y})", "{x/Sqrt(Abs(x)^2+Abs(y)^2),y/Sqrt(Abs(x)^2+Abs(y)^2)}");
		check("Normalize({x,y}, f)", "{x/f({x,y}),y/f({x,y})}");
		check("Normalize({1, 2 I, 3, 4 I, 5, 6 I})",
				"{1/Sqrt(91),(I*2)/Sqrt(91),3/Sqrt(91),(I*4)/Sqrt(91),5/Sqrt(91),(I*6)/Sqrt(91)}");
		check("Normalize(N({1, 2 I, 3, 4 I, 5, 6 I}))", "{0.10482848367219183," + "I*0.20965696734438366,"
				+ "0.3144854510165755," + "I*0.4193139346887673," + "0.5241424183609591," + "I*0.628970902033151}");
		check("Normalize({{1, 2}, {4, 5}}, Norm)",
				"{{1/Norm({{1,2},{4,5}}),2/Norm({{1,2},{4,5}})},{4/Norm({{1,2},{4,5}}),5/Norm({{1,\n" + "2},{4,5}})}}");
		check("Normalize(1 + x + x^2, Integrate(#^2, {x, -1, 1}) &)", "5/22*(1+x+x^2)");
	}

	public void testNot() {
		check("Not(Not(x))", "x");
		check("Not(a<b)", "a>=b");
		check("Not(a<=b)", "a>b");
		check("Not(a>b)", "a<=b");
		check("Not(a>=b)", "a<b");
		check("Not(a==b)", "a!=b");
	}

	public void testNSolve() {
		check("NSolve(x^3 + 2.0*x^2 - 5*x -3.0 ==0,x)",
				"{{x->-3.253418039587852},{x->-0.5199693720627908},{x->1.773387411650643}}");
		check("NSolve(x^3 + 2x^2 - 5x -3 ==0,x)",
				"{{x->-3.253418039587852},{x->-0.5199693720627908},{x->1.773387411650643}}");
	}

	public void testNullSpace() {
		check("NullSpace({{1, 2, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 1},\n"
				+ "{0, 0, -1},\n" + "{1, 2, 1}})", "{{-2,1,0}}");
		check("NullSpace({{1,2,3},{4,5,6},{7,8,9}})", "{{1,-2,1}}");
		check("NullSpace({{1,1,0,1,5},{1,0,0,2,2},{0,0,1,4,-1},{0,0,0,0,0}})", "{{-2,1,-4,1,0},\n" + " {-2,-3,1,0,1}}");
		check("NullSpace({{a,b,c}," + "{c,b,a}})", "{{1,-(a+c)/b,1}}");
		check("NullSpace({{1,2,3}," + "{5,6,7}," + "{9,10,11}})", "{{1,-2,1}}");
		check("NullSpace({{1,2,3,4}," + "{5,6,7,8}," + "{9,10,11,12}})", "{{1,-2,1,0},\n" + " {2,-3,0,1}}");
		check("(-1/2+I*1/2)*(-I)", "1/2+I*1/2");
		check("NullSpace({{1+I,1-I}, {-1+I,1+I}})", "{{I,1}}");
		check("NullSpace({{1,1,1,1,1},{1,0,0,0,0},{0,0,0,0,1},{0,1,1,1,0},{1,0,0,0,1}})",
				"{{0,-1,1,0,0},\n" + " {0,-1,0,1,0}}");
	}

	public void testNumerator() {
		check("Numerator(Csc(x))", "Csc(x)");
		check("Numerator(Csc(x), Trig->True)", "1");
		check("Numerator(Csc(x)^4)", "Csc(x)^4");
		check("Numerator(Csc(x)^4, Trig->True)", "1");
		check("Numerator(42*Csc(x))", "42*Csc(x)");
		check("Numerator(42*Csc(x), Trig->True)", "42");
		check("Numerator(42*Csc(x)^3)", "42*Csc(x)^3");
		check("Numerator(42*Csc(x)^3, Trig->True)", "42");
		check("Numerator(E^(-x)*x^(1/2))", "Sqrt(x)");

		check("Numerator(Sec(x))", "Sec(x)");
		check("Numerator(Sec(x), Trig->True)", "1");
		check("Numerator(Tan(x))", "Tan(x)");
		check("Numerator(Tan(x), Trig->True)", "Sin(x)");
	}

	public void testOddQ() {
		check("OddQ({1,3}) && OddQ({5,7})", "{True,True}&&{True,True}");
	}

	public void testOr() {
		check("Or( )", "False");
		check("Or(2+2)", "4");
		check("FullForm( Or(x, Or(y, z)) )", "\"Or(x, y, z)\"");
		check("Or(x, False, z)", "x||z");
		check("Or(x, True, z)", "True");
		check("BooleanConvert(! (a && b))", "!a||!b");
		check("BooleanConvert(! (a || b || c))", "!a&&!b&&!c");
	}

	public void testOuter() {
		check("Outer(f, {a, b}, {x, y, z})", "{{f(a,x),f(a,y),f(a,z)},{f(b,x),f(b,y),f(b,z)}}");
		check("Outer(Times, {1, 2, 3, 4}, {a, b, c})", "{{a,b,c},{2*a,2*b,2*c},{3*a,3*b,3*c},{4*a,4*b,4*c}}");
		check("Outer(Times, {{1, 2}, {3, 4}}, {{a, b}, {c, d}})",
				"{{{{a,b},{c,d}},{{2*a,2*b},{2*c,2*d}}},{{{3*a,3*b},{3*c,3*d}},{{4*a,4*b},{4*c,4*d}}}}");
		check("Outer(f, {a, b}, {x, y, z}, {u, v})",
				"{{{f(a,x,u),f(a,x,v)},{f(a,y,u),f(a,y,v)},{f(a,z,u),f(a,z,v)}},{{f(b,x,u),f(b,x,v)},{f(b,y,u),f(b,y,v)},{f(b,z,u),f(b,z,v)}}}");
		check("Outer(Times, {{1, 2}, {3, 4}}, {{a, b, c}, {d, e}})",
				"{{{{a,b,c},{d,e}},{{2*a,2*b,2*c},{2*d,2*e}}},{{{3*a,3*b,3*c},{3*d,3*e}},{{4*a,4*b,\n"
						+ "4*c},{4*d,4*e}}}}");
		check("Outer(g, f(a, b), f(x, y, z))", "f(f(g(a,x),g(a,y),g(a,z)),f(g(b,x),g(b,y),g(b,z)))");
		check("Dimensions(Outer(f, {x, x, x}, {x, x, x, x}, {x, x}, {x, x, x, x, x}))", "{3,4,2,5}");
		check("Dimensions(Outer(f, {{x, x}, {x, x}}, {x, x, x}, {{x}}))", "{2,2,3,1,1}");
		check("Outer(f, {a, b}, {1,2,3})", "{{f(a,1),f(a,2),f(a,3)},{f(b,1),f(b,2),f(b,3)}}");
		check("Outer(Times, {{1, 2}}, {{a, b}, {x, y, z}})", "{{{{a,b},{x,y,z}},{{2*a,2*b},{2*x,2*y,2*z}}}}");
	}

	// public void testOrderedQ() {
	// check("OrderedQ({x^2, 4+6*x})", "True");
	//
	// check("Sort({x^2,4+6*x})", "{x^2,4+6*x}");
	// check("Sort({4+4*a,x^2,x^3, 4+6*x})", "{4+4*a,x^2,x^3,4+6*x}");
	// check("OrderedQ({x^2,x^3})", "True");
	//// check("OrderedQ({4+4*a,x^2,x^3, 4+6*x})", "True");
	//
	// check("OrderedQ({x,x^6.0 })", "True");
	// check("OrderedQ({4.0*x,33.0*x^6.0 })", "True");
	// check("OrderedQ({x^3,4+4*a })", "False");
	//// check("OrderedQ({x^2, 4+6*x})", "True");
	//
	// check("OrderedQ({x^2, 6*x})", "False");
	// check("OrderedQ({6*x,x^2})", "True");
	//// check("OrderedQ({3*x^2, x*(4+6*x)})", "True");
	// check("OrderedQ({a,a})", "True");
	// check("OrderedQ({x, y, x + y})", "True");
	// }

	public void testPadLeft() {
		check("PadLeft({a, b, c}, 10)", "{0,0,0,0,0,0,0,a,b,c}");
		check("PadLeft({a, b, c}, 10, {x, y, z})", "{z,x,y,z,x,y,z,a,b,c}");
		check("PadLeft({a, b, c}, 9, {x, y, z})", "{x,y,z,x,y,z,a,b,c}");
		check("PadLeft({a, b, c}, 8, {x, y, z})", "{y,z,x,y,z,a,b,c}");
		check("PadLeft({a, b, c}, 10, 42)", "{42,42,42,42,42,42,42,a,b,c}");
	}

	public void testPadRight() {
		check("PadRight({a, b, c}, 10)", "{a,b,c,0,0,0,0,0,0,0}");
		check("PadRight({a, b, c}, 10, {x, y, z})", "{a,b,c,x,y,z,x,y,z,x}");
		check("PadRight({a, b, c}, 9, {x, y, z})", "{a,b,c,x,y,z,x,y,z}");
		check("PadRight({a, b, c}, 8, {x, y, z})", "{a,b,c,x,y,z,x,y}");
		check("PadRight({a, b, c}, 10, 42)", "{a,b,c,42,42,42,42,42,42,42}");
	}

	public void testParserFixedPoint() {
		try {
			Parser p = new Parser(true);
			ASTNode obj = p.parse("{28, 21} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}");
			assertEquals(obj.toString(),
					"ReplaceAll(List(28, 21), Rule(Condition(List(a_, b_), Unequal(b, 0)), List(b, Mod(a, b))))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testPart() {
		check("(1 + 2 x^2 + y^2)[[2]]", "2*x^2");
		check("(1 + 2 x^2 + y^2)[[1]]", "1");
		check("(x/y)[[2]]", "1/y");
		check("(y/x)[[2]]", "y");

		check("{{a, b, c}, {d, e, f}}[[1]][[2]]", "b");
		check("{{a, b, c}, {d, e, f}}[[1, 2]]", "b");
	}

	public void testPDF() {
		check("N(PDF(NormalDistribution(0, 1), 0))", "0.3989422804014327");
		check("N(PDF(BinomialDistribution(40, 0.5), 1))", "3.6379788070917175E-11");
		check("N(PDF(HypergeometricDistribution(20,50,100), 10))", "0.19687121770654953");
		check("N(PDF(PoissonDistribution(10), 15))", "0.03471806963068409");
	}

	public void testPatternTest() {
		check("$j(x_, y_:1, z_:2) := jp[x, y, z]; $j(a,b)", "jp(a,b,2)");
		check("$j(x_, y_:1, z_:2) := jp[x, y, z]; $j(a)", "jp(a,1,2)");
		check("$f(x_:2):={x};$f()", "{2}");
		check("$f(x_:2):={x};$f(a)", "{a}");

		check("Cases({1,2,3,5,x,y,4},_?NumberQ)", "{1,2,3,5,4}");
		check("MatchQ({1,8,Pi},{__?Positive})", "True");
		check("MatchQ({1,I,0},{__?Positive})", "False");

		check("{3,-5,2,7,-6,3} /. _?Negative:>0", "{3,0,2,7,0,3}");

		check("Cases(Range(0,350),_?(Divisible(#,7)&&Divisible(#,5)&))", "{0,35,70,105,140,175,210,245,280,315,350}");

		check("$f(n_?NonNegative, p_?PrimeQ):=n^p; $f(2,3)", "8");
		check("$f(n_?NonNegative, p_?PrimeQ):=n^p; $f(2,4)", "$f(2,4)");

		check("MatchQ({{a,b},{c,d}},{_,_}?MatrixQ)", "True");
		check("MatchQ({a,b},{_,_}?MatrixQ)", "False");

		check("Cases({{a,b},{1,2,3},{{d,6},{d,10}}}, {_,_}?VectorQ)", "{{a,b}}");
		check("Cases({{a,b},{1,2,3},{{d,6},{d,10}}}, {x_,y_}/;!ListQ(x)&&!ListQ(y))", "{{a,b}}");

	}

	public void testPiecewise() {
		check("Piecewise({{1, False}})", "0");
		check("$pw = Piecewise({{Sin(x)/x, x < 0}, {1, x == 0}}, -x^2/100 + 1); $pw /. {{x -> -5}, {x -> 0}, {x -> 5}}",
				"{Sin(5)/5,1,3/4}");
		check("Piecewise({{e1, True}, {e2, d2}, {e3, d3}}, e0)", "e1");
		check("Piecewise({{e1, d1}, {e2, d2}, {e3, True}, {e4, d4}, {e5, d5}}, e0)",
				"Piecewise(\n" + "{{e1,d1},\n" + " {e2,d2},\n" + " {e3,True}})");
		check("Piecewise({{e1, d1}, {e2, d2}, {e3, d2 && d3}, {e4, d4}}, e0)",
				"Piecewise(\n" + "{{e1,d1},\n" + " {e2,d2},\n" + " {e3,d2&&d3},\n" + " {e4,d4}},e0)");
		check("Piecewise({{e1, d1}, {e2, d2}, {e3, False}, {e4, d4}, {e5, d5}}, e0)",
				"Piecewise(\n" + "{{e1,d1},\n" + " {e2,d2},\n" + " {e4,d4},\n" + " {e5,d5}},e0)");
	}

	public void testPlus() {
		check("Interval({1,6})+Interval({0,2})", "Interval({1,8})");
		check("Interval({a,b})+z", "z+Interval({a,b})");
		check("(Interval({-1,1})+1/2)^2 - 1/4", "Interval({-1/4,2})");
		check("f+Interval({a,b})+Interval({c,d})", "f+Interval({a+c,b+d})");
		check("Interval({a,b})+Interval({c,d})", "Interval({a+c,b+d})");
		check("1+Interval({2,3})", "Interval({3,4})");
		check("Plus()", "0");
	}

	public void testPochhammer() {
		check("Pochhammer(4, 8)", "6652800");
		check("Pochhammer(10, 6)", "3603600");
		check("Pochhammer(10, -6)", "1/60480");
		check("Pochhammer(-10, -6)", "1/5765760");
		check("Pochhammer(-10, -7)", "-1/98017920");
		check("Pochhammer(-10, -12)", "1/309744468633600");
		check("Pochhammer(3/2, 1/2)", "2/Sqrt(Pi)");
		check("Pochhammer(-5, -3)", "-1/336");
	}

	public void testPolynomialExtendedGCD() {
		// check("PolynomialExtendedGCD((x - a)*(b*x - c)^2, (x - a)*(x^2 -
		// b*c), x)", "");
		check("PolynomialExtendedGCD((x - 1)*(x - 2)^2, (x - 1)*(x^2 - 3), x)", "{-1+x,{7+4*x,9-4*x}}");

		check("PolynomialExtendedGCD((x - 1)^2*(x - 2)^2, (x - 1)*(x^2 - 3), x)",
				"{-1+x,{19/2+11/2*x,-13+18*x-11/2*x^2}}");
		check("PolynomialExtendedGCD((x - 1)^2*(x - 2)^2, (x - 1)*(x^2 - 3), x,  Modulus -> 2)", "{1+x^2,{1,1+x}}");

		check("PolynomialExtendedGCD(a*x^2 + b*x + c, x - r, x)", "{1,{1/(c+b*r+a*r^2),(-b-a*r-a*x)/(c+b*r+a*r^2)}}");

	}

	public void testPolynomialGCD() {
		check("PolynomialGCD((x + 1)^3, x^3 + x, Modulus -> 2)", "(1+x)^2");
		check("PolynomialGCD((x - a)*(b x - c)^2, (x - a)*(x^2 - b*c))", "a-x");
		check("PolynomialGCD[(1 + x)^2*(2 + x)*(4 + x), (1 + x)*(2 + x)*(3 + x)]", "2+3*x+x^2");
		check("PolynomialGCD(x^4 - 4, x^4 + 4 x^2 + 4)", "2+x^2");

		check("PolynomialGCD(x^2 + 2 x y + y^2, x^3 + y^3)", "x+y");
		check("PolynomialGCD(x^2 - 1, x^3 - 1, x^4 - 1, x^5 - 1, x^6 - 1, x^7 - 1)", "-1+x");

		check("PolynomialGCD(x^2 - 4, x^2 + 4 x + 4)", "2+x");
		check("PolynomialGCD(3*x + 9, 6*x^3 - 3*x + 12)", "3");
	}

	public void testPolynomialLCM() {
		check("Expand((-1+x)*(1+x)*(1+x^2)*(1-x+x^2)*(1+x+x^2)*(1+x+x^2+x^3+x^4)*(1+x+x^2+x^3+x^4+x^5+x^6))",
				"-1-2*x-4*x^2-6*x^3-8*x^4-9*x^5-9*x^6-7*x^7-4*x^8+4*x^10+7*x^11+9*x^12+9*x^13+8*x^\n"
						+ "14+6*x^15+4*x^16+2*x^17+x^18");

		check("PolynomialLCM((1 + x)^2 (2 + x) (4 + x), (1 + x) (2 + x) (3 + x))", "24+74*x+85*x^2+45*x^3+11*x^4+x^5");
		check("Expand((1+x)^2*(2+x)*(3+x)*(4+x))", "24+74*x+85*x^2+45*x^3+11*x^4+x^5");

		check("PolynomialLCM(x^2 + 2 x y + y^2, x^3 + y^3)", "x^4+x^3*y+x*y^3+y^4");
		check("Expand((x+y)*(x^3+y^3))", "x^4+x^3*y+x*y^3+y^4");

		check("PolynomialLCM(x^2 - 1, x^3 - 1, x^4 - 1, x^5 - 1, x^6 - 1, x^7 - 1)",
				"-1-2*x-4*x^2-6*x^3-8*x^4-9*x^5-9*x^6-7*x^7-4*x^8+4*x^10+7*x^11+9*x^12+9*x^13+8*x^\n"
						+ "14+6*x^15+4*x^16+2*x^17+x^18");

	}

	public void testPolynomialQ() {

		check("PolynomialQ(f(a)+f(a)^2, f(a))", "True");
		check("PolynomialQ(Sin(f(a))+f(a)^2, f(a))", "False");
		check("PolynomialQ(x^3 - 2 x/y + 3 x z, x)", "True");
		check("PolynomialQ(x^3 - 2 x/y + 3 x z, y)", "False");
		check("PolynomialQ(x^2 + a x y^2 - b Sin(c), {x, y})", "True");
		check("PolynomialQ(x^2 + a x y^2 - b Sin(c), {a, b, c})", "False");

		check("PolynomialQ((1+x)^3*(1-y-x)^2, x)", "True");
		check("PolynomialQ(x+Sin(x), x)", "False");
		check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {x, y})", "True");
		check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {a, b, c})", "False");

		check("PolynomialQ(f(a)+f(a)^2,f(a))", "True");

		check("PolynomialQ(I*x^(3)+(x+2)^2,x)", "True");
		check("PolynomialQ(I,x)", "True");
	}

	public void testPolynomialQuotient() {
		check("PolynomialQuotient(x^2, x + a,x)", "-a+x");
		check("PolynomialQuotient(x^2 + x + 1, 2*x + 1, x)", "1/4+x/2");
		check("PolynomialQuotient(x^2 + b*x + 1, a*x + 1, x)", "(-1/a+b)/a+x/a");
		check("PolynomialQuotient(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 2)", "1+x^2");
		check("PolynomialQuotient(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 3)", "1+2*x");
	}

	public void testPolynomialQuotientRemainder() {
		check("PolynomialQuotientRemainder(x^2, x + a,x)", "{-a+x,a^2}");
		check("PolynomialQuotientRemainder(x^2 + x + 1, 2*x + 1, x)", "{1/4+x/2,3/4}");
		check("PolynomialQuotientRemainder(x^2 + b*x + 1, a*x + 1, x)", "{(-1/a+b)/a+x/a,1-(-1/a+b)/a}");

		check("PolynomialQuotientRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 2)", "{1+x^2,0}");
		check("PolynomialQuotientRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 3)", "{1+2*x,0}");
	}

	public void testPolynomialRemainder() {
		check("PolynomialRemainder(x^2, x + a,x)", "a^2");
		check("PolynomialRemainder(x^2 + 4 x + 1, 2 x + 1, x, Modulus -> 2)", "0");
		check("PolynomialRemainder(x^2 + 4 x + 1, 2 x + 1, x, Modulus -> 5)", "3");
	}

	public void testPosition() {
		check("Position({1.0, 2+3, b}, _Integer)", "{{2}}");
		check("Position(_Integer)[{1.0, 2+3, b}]", "{{2}}");
		check("Position(_Integer)[{1.0, 2, b}]", "{{2}}");
		check("Position(_Integer)[{a, 2, b}]", "{{2}}");
		check("Position({x, {x, y}, y},x,1)", "{{1}}");
		check("Position({x, {x, y}, y},x,2)", "{{1},{2,1}}");
		check("Position({x, {x, y}, y},x,{2})", "{{2,1}}");

		check("Position(f(f(g(a), a), a, h(a), f), a, {2, Infinity})", "{{1,1,1},{1,2},{3,1}}");
		check("Position(f(f(g(a), a), a, h(a), f), f, Heads->False)", "{{4}}");
		check("Position(f(f(g(a), a), a, h(a), f), f, Heads->True)", "{{0},{1,0},{4}}");

		check("Position({f(a), g(b), f(c)}, f(x_))", "{{1},{3}}");
	}

	public void testPossibleZeroQ() {

		check("PossibleZeroQ(2^(2*I) - 2^(-2*I) - 2*I*Sin(Log(4)))", "True");
		check("PossibleZeroQ(E^Pi - Pi^E)", "False");
		check("PossibleZeroQ((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)", "True");
		check("PossibleZeroQ(E^(I Pi/4) - (-1)^(1/4))", "True");
		check("PossibleZeroQ((x + 1) (x - 1) - x^2 + 1)", "True");
		check("PossibleZeroQ(1/x + 1/y - (x + y)/(x y))", "True");
		check("PossibleZeroQ(Sqrt(x^2) - x)", "False");
	}

	public void testPower() {
		// check("Exp(y + Log(x))", "x+E^y");
		check("E^(2*(y+Log(x)))", "E^(2*y)*x^2");
		// don't change see issue #137
		check("2^(3+x)", "2^(3+x)");

		check("I^(1/3)", "(-1)^(1/6)");
		check("I^(1/4)", "(-1)^(1/8)");
		check("I^(1/8)", "(-1)^(1/16)");
		check("I^(3/8)", "(-1)^(3/16)");
		check("I^(3/5)", "(-1)^(3/10)");
		check("(-I)^(1/2)", "-(-1)^(3/4)");
		check("(-I)^(1/3)", "-(-1)^(5/6)");
		check("(-I)^(3/5)", "-(-1)^(7/10)");
		check("(-I)^(21/32)", "-(-1)^(43/64)");
		check("(-I)^(32/21)", "-(-1)^(5/21)");
		check("(-I)^(1/4)", "-(-1)^(7/8)");
		check("(-I)^(1/5)", "-(-1)^(9/10)");
		check("(-I)^(1/6)", "-(-1)^(11/12)");
		check("(-I)^(1/24)", "-(-1)^(47/48)");
		check("(-I)^(64/7)", "-(-1)^(3/7)");
		check("(-I)^(71/7)", "(-1)^(13/14)");
		check("27^(1/3)", "3");
		check("5103^(1/3)", "9*7^(1/3)");
		check("5103^(1/2)", "27*Sqrt(7)");
		check("Sqrt(75/4)", "5/2*Sqrt(3)");

		check("0^(-1/2)", "ComplexInfinity");

		check("E^(x+2*Pi*I)", "E^x");
		check("E^(x+11*Pi*I)", "-E^x");
		check("E^(x+Sin(a)+2*Pi*I)", "E^(x+Sin(a))");

		check("(-9/5)*(3)^(-1/2)", "-3/5*Sqrt(3)");
		check("(-1/9)*3^(1/2)", "-1/(3*Sqrt(3))");
		check("3^(1/2)/9", "1/(3*Sqrt(3))");
		check("0^0", "Indeterminate");
		check("27^(1/3)", "3");
		check("(-27)^(1/3)", "3*(-1)^(1/3)");
		check("(-5)^(1/2)", "I*Sqrt(5)");
		check("(-5)^(-1/2)", "-I*Sqrt(1/5)");
		check("(-(2/3))^(-1/2)", "-I*Sqrt(3/2)");
		check("FullForm(a^b^c)", "\"Power(a, Power(b, c))\"");
		check("FullForm((a^b)^c)", "\"Power(Power(a, b), c)\"");
		check("(a*b)^3", "a^3*b^3");
		check("(a*b)^(1/2)", "Sqrt(a*b)");
		check("FullForm((a^b)^3)", "\"Power(a, Times(3, b))\"");
		check("{2,3,4,5}^3", "{8,27,64,125}");
		check("N(29^(1/3))", "3.072316825685847");
		check("50!^(1/6)", "604800*621447116887301398870058090208^(1/6)");
		check("(z^(1/3))^3", "z");
		check("(z^3)^(1/3)", "(z^3)^(1/3)");
		check("Sqrt(x^2)", "Sqrt(x^2)");

		check("E^(Log(x))", "x");
		check("E^(y+Log(x))", "E^y*x");
		check("E^(y+Log(x)-z)", "E^(y-z)*x");
		check("E^(y-Log(x)-z)", "E^(y-z)/x");
		check("E^(y+Log(x)-a*Log(v)*b*Log(u)-z)", "(E^(y-z)*x)/v^(a*b*Log(u))");
	}

	public void testPowerExpand() {
		check("-2^(1/2)*3^(1/2)", "-Sqrt(6)");
		check("Sqrt(x*y)", "Sqrt(x*y)");
		check("{Sqrt(x y), Sqrt(x) Sqrt(y)} /. {x -> -2, y -> -3}", "{Sqrt(6),-Sqrt(6)}");
		check("PowerExpand((a^b)^(1/2))", "a^(b/2)");
		check("Powerexpand((a*b)^(1/2))", "Sqrt(a)*Sqrt(b)");
		check("Powerexpand(Log((a^b)^c))", "b*c*Log(a)");
		check("Powerexpand({y*(a^b)^g, x+(a*b)^42,Log(a^b)})", "{a^(b*g)*y,a^42*b^42+x,b*Log(a)}");
		check("Powerexpand(Sqrt(x^2))", "x");
		check("Powerexpand(Log(1/z))", "-Log(z)");
		check("Powerexpand(2-Log(1/z^3))", "2+3*Log(z)");
		check("Powerexpand(Log(z^a))", "a*Log(z)");
		check("Powerexpand(Sqrt(a b) + Sqrt(c d))", "Sqrt(a)*Sqrt(b)+Sqrt(c)*Sqrt(d)");
		check("PowerExpand(Sqrt(x y))", "Sqrt(x)*Sqrt(y)");

		check("PowerExpand(Log(z^a), Assumptions->True)", "I*2*Pi*Floor((Pi-Im(a*Log(z)))/(2*Pi))+a*Log(z)");

		check("PowerExpand((E^x)^(y), Assumptions->True)", "E^(x*y+I*2*Pi*y*Floor((Pi-Im(x))/(2*Pi)))");
		// "E^(x*y)*E^(I*2*Pi*y*Floor(1/2*(-Im(x)+Pi)*Pi^(-1)))");

		check("PowerExpand((x*y)^(1/2), Assumptions->True)",
				"E^(I*Pi*Floor(1/2-Arg(x)/(2*Pi)-Arg(y)/(2*Pi)))*Sqrt(x)*Sqrt(y)");
		check("PowerExpand((a*b*c)^(1/3), Assumptions->True)",
				"E^(I*2/3*Pi*Floor(1/2-Arg(a)/(2*Pi)-Arg(b)/(2*Pi)-Arg(c)/(2*Pi)))*a^(1/3)*b^(1/3)*c^(\n" + "1/3)");
	}

	public void testMultiplicativeOrder() {
		check("MultiplicativeOrder(7, 108)", "18");
		check("MultiplicativeOrder(10^100 + 1, Prime(1000))", "3959");
		check("MultiplicativeOrder(-5, 7)", "3");

		check("Select(Range(43), MultiplicativeOrder(#, 43) == EulerPhi(43) &)", "{3,5,12,18,19,20,26,28,29,30,33,34}");
	}

	public void testPolyGamma() {
		check("PolyGamma(-2)", "ComplexInfinity");
		check("PolyGamma(1)", "-EulerGamma");
		check("PolyGamma(2)", "1-EulerGamma");
		check("PolyGamma(3)", "3/2-EulerGamma");
		check("PolyGamma(1,1/4)", "8*Catalan+Pi^2");
		check("PolyGamma(1,3/4)", "-8*Catalan+Pi^2");
		check("PolyGamma(2,5/6)", "4*Sqrt(3)*Pi^3-182*Zeta(3)");
	}

	public void testStieltjesGamma() {
		check("StieltjesGamma(0)", "EulerGamma");
		check("StieltjesGamma(0,a)", "-PolyGamma(0,a)");
	}

	public void testPolyLog() {
		check("PolyLog(2,0)", "0");
		check("PolyLog(2,-1)", "-Pi^2/12");
		check("PolyLog(2,1)", "Pi^2/6");
		check("PolyLog(2,1/2)", "Pi^2/12-Log(2)^2/2");
		check("PolyLog(2,2)", "Pi^2/4-I*Pi*Log(2)");
		check("PolyLog(2,I)", "I*Catalan-Pi^2/48");
		check("PolyLog(2,-I)", "-I*Catalan-Pi^2/48");
		check("PolyLog(2,1-I)", "-I*Catalan+Pi^2/16-I*1/4*Pi*Log(2)");
		check("PolyLog(2,1+I)", "I*Catalan+Pi^2/16+I*1/4*Pi*Log(2)");
		check("PolyLog(3,1)", "Zeta(3)");
		check("PolyLog(f(x),-1)", "(-1+2^(1-f(x)))*Zeta(f(x))");
		check("PolyLog(0,f(x))", "f(x)/(1-f(x))");
		check("PolyLog(1,f(x))", "-Log(1-f(x))");
		check("PolyLog(-1,f(x))", "f(x)/(1-f(x))^2");
		check("PolyLog(-2,f(x))", "(-(1+f(x))*f(x))/(-1+f(x))^3");
		check("PolyLog(-3,f(x))", "((1+f(x)^2+4*f(x))*f(x))/(1-f(x))^4");
	}

	public void testPowerMod() {
		// check("PowerMod(6, 1/2, 10)", "1");

		check("PowerMod(2, 10, 3)", "1");
		// similar to Java modInverse()
		check("PowerMod(3, -1, 7)", "5");
		// prints warning
		check("PowerMod(0, -1, 2)", "PowerMod(0,-1,2)");
		// prints warning
		check("PowerMod(5, 2, 0)", "PowerMod(5,2,0)");

		check("PowerMod(2, 10^9, 18)", "16");
		check("PowerMod(2, {10, 11, 12, 13, 14}, 5)", "{4,3,1,2,4}");
		check("PowerMod(147198853397, -1, 73599183960)", "43827926933");
	}

	public void testPrependTo() {
		check("$l = {1, 2, 4, 9};PrependTo($l, 16)", "{16,1,2,4,9}");
		check("$l = {1, 2, 4, 9};PrependTo($l, 16);$l", "{16,1,2,4,9}");
	}

	public void testPrime() {
		check("Prime(10^6)", "15485863");
		check("Prime(10^7)", "179424673");
		// check("Prime(10^8)", "2038074743");
		// check("Prime(103000000)", "2102429869");

		// above the limit return Prime(...)
		// check("Prime(10^9)", "22801763489");
		// check("Prime(10^10)", "252097800623");
		// check("Prime(10^11)", "2760727302517");
	}

	public void testPrimeQ() {
		check("PrimeQ(99999999999971)", "True");
	}

	public void testPrimitiveRoots() {
		check("PrimitiveRoots(9)", "{2,5}");
		check("PrimitiveRoots(7)", "{3,5}");
		check("PrimitiveRoots(12)", "{}");
		check("PrimitiveRoots(19)", "{2,3,10,13,14,15}");
		check("PrimitiveRoots(43)", "{3,5,12,18,19,20,26,28,29,30,33,34}");
	}

	public void testPrint() {
		check("do(print(i0);if(i0>4,Return(toobig)), {i0,1,10})", "toobig");
	}

	public void testProduct() {
		check("Product(a^i, {i, n})", "a^(1/2*n*(1+n))");
		check("Product(c, {i, 1, j}, {j, 2})", "c^(2*j)");
		check("Product(c, {i, 1, j}, {j, 1, 2})", "c^(2*j)");
		check("Product(c, {i, 1, n})", "c^n");
		check("Product(c+n, {i, 1, n})", "(c+n)^n");
		check("Product(c+n, {i, 0, n})", "(c+n)^(1+n)");
		check("n!", "n!");
		check("$prod(x_,{x_,1,m_}) := m!; $prod(i0, {i0, 1, n0})", "n0!");
		check("Product(i0, {i0, 1, n0})", "n0!");
		check("Product(i^2, {i, 1, n})", "(n!)^2");
		check("Product(i0^2, {i0, 0, n0})", "0");
		check("Product(4*i0^2, {i0, 0, n0})", "0");
		check("Product(i0^3, {i0, 1, n0})", "(n0!)^3");
		check("Product(i0^3+p^2, {i0, 1, n0})", "Product(i0^3+p^2,{i0,1,n0})");
		check("Product(p, {i0, 1, n0})", "p^n0");
		check("Product(p+q, {i0, 1, n0})", "(p+q)^n0");
		check("Product(p, {i0, 0, n0})", "p^(1+n0)");
		check("Product(4, {i0, 0, n0})", "4^(1+n0)");

		check("Product(c, {i, 1, n}, {j, 1, n})", "(c^n)^n");
		check("Product(c, {j, 1, n}, {i, 1, j})", "c^(1/2*n*(1+n))");
		check("Product(f(i, j), {i, 1, 3}, {j, 1, 3})",
				"f(1,1)*f(1,2)*f(1,3)*f(2,1)*f(2,2)*f(2,3)*f(3,1)*f(3,2)*f(3,3)");
		check("Product(f(i, j), {i, 1, 3, 2}, {j, 1, 3, 1/2})",
				"f(1,1)*f(1,3/2)*f(1,2)*f(1,5/2)*f(1,3)*f(3,1)*f(3,3/2)*f(3,2)*f(3,5/2)*f(3,3)");
		// check("Product(2^(j + i0), {i0, 1, p}, {j, 1, i0})", "");
	}

	public void testProductLog() {
		String s = System.getProperty("os.name");
		if (s.contains("Windows")) {
			check("ProductLog(-1.5)", "-3.2783735915572e-2+I*1.549643823350159");
			check("ProductLog({0.2, 0.5, 0.8})", "{1.689159734991096e-1,3.517337112491959e-1,4.900678588015799e-1}");
			check("ProductLog(2.5 + 2*I)", "1.056167968948635+I*3.5256052020787e-1");
			check("N(ProductLog(4/10),50)", "2.9716775067313854677972696224702134190445810155014e-1");

			check("N(ProductLog(-1),20)", "-3.181315052047641353e-1+I*1.3372357014306894089");
		}

		check("ProductLog(0)", "0");
		check("ProductLog(-Pi/2)", "I*1/2*Pi");
		check("ProductLog(-1/E)", "-1");

		check("ProductLog(Infinity)", "Infinity");
		check("ProductLog(-Infinity)", "-Infinity");
		check("ProductLog(I*Infinity)", "Infinity");
		check("ProductLog(-I*Infinity)", "Infinity");
		check("ProductLog(ComplexInfinity)", "Infinity");

		check("ProductLog(0,a)", "ProductLog(a)");
		check("ProductLog(42,0)", "-Infinity");
		check("ProductLog(-1,(-1/2)*Pi)", "-I*1/2*Pi");
		check("ProductLog(-1,-E^(-1))", "-1");
	}

	public void testPseudoInverse() {
		check("PseudoInverse({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}})",
				"{{-0.4833333333333334,-0.24444444444444524,-0.005555555555555791,0.233333333333334},\n"
						+ " {-0.03333333333333403,-0.011111111111111523,0.011111111111111075,0.033333333333333694},\n"
						+ " {0.41666666666666724,0.22222222222222315,0.027777777777778002,-0.16666666666666746}}");
		check("PseudoInverse(N({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}))",
				"{{-0.4833333333333334,-0.24444444444444524,-0.005555555555555791,0.233333333333334},\n"
						+ " {-0.03333333333333403,-0.011111111111111523,0.011111111111111075,0.033333333333333694},\n"
						+ " {0.41666666666666724,0.22222222222222315,0.027777777777778002,-0.16666666666666746}}");
		check("PseudoInverse(N({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}))",
				"{{-0.6388888888888884,-0.166666666666667,0.3055555555555557},\n"
						+ " {-0.05555555555555664,-3.5041414214731503E-16,0.055555555555556066},\n"
						+ " {0.5277777777777783,0.16666666666666718,-0.19444444444444492}}");

	}

	public void testPutGet() {
		String s = System.getProperty("os.name");
		if (s.contains("Windows")) {
			check("Put(x + y, \"c:/temp/example_file1.m\"); Get(\"c:/temp/example_file1.m\")", "x+y");
			check("Put(x + y, 2x^2 + 4z!, Cos(x) + I Sin(x), \"c:/temp/example_file2.m\");"
					+ "Get(\"c:/temp/example_file2.m\")", "I*Sin(x)+Cos(x)");
			check("Put(47!, \"c:/temp/test.m\"); Get(\"c:/temp/test.m\")",
					"258623241511168180642964355153611979969197632389120000000000");
		}
	}

	public void testQuiet() {
		check("Quiet(1/0)", "ComplexInfinity");
		check("1/0", "ComplexInfinity");
	}

	public void testQuotient() {
		check("Quotient(-17, 7)", "-3");
		check("Quotient(15, -5)", "-3");
		check("Quotient(17, 5)", "3");
		check("Quotient(-17, -4)", "4");
		check("Quotient(-14, 7)", "-2");
		check("Quotient(13, 0)", "/ by zero");
	}

	public void testRange() {
		// check("Range(0,10,Pi)", "");

		check("Range(0)", "{}");
		check("Range(1)", "{1}");
		check("Range(-1)", "{}");
		check("Range(10)", "{1,2,3,4,5,6,7,8,9,10}");
		check("Range(1,10,2)", "{1,3,5,7,9}");
		check("Range(10,20,3)", "{10,13,16,19}");
		check("Range(10,1,-1)", "{10,9,8,7,6,5,4,3,2,1}");

	}

	public void testRational() {
		check("f[22/7, 201/64, x/y] /. Rational[n_, d_] :> d/n", "f(7/22,64/201,x/y)");
	}

	public void testRationalize() {
		check("Rationalize(0.202898)", "101449/500000");
		check("Rationalize(1.2 + 6.7 x)", "6/5+67/10*x");
		check("Rationalize(Exp(Sqrt(2)), 2^-12)", "218/53");
		check("Rationalize(6.75)", "27/4");
		check("Rationalize(Pi)", "245850922/78256779");
		check("Rationalize(Pi, .01)", "22/7");
		check("Rationalize(Pi, .001)", "333/106");
	}

	public void testRe() {
		check("Re(0)", "0");
		check("Re(I)", "0");
		check("Re(Indeterminate)", "Indeterminate");
		check("Re(Infinity)", "Infinity");
		check("Re(-Infinity)", "-Infinity");
		check("Re(ComplexInfinity)", "Indeterminate");
	}

	public void testReap() {
		check("Reap(x)", "{x,{}}");
		check("Reap(Sow(a); b; Sow(c); Sow(d); e)", "{e,{{a,c,d}}}");
		check("Reap(Sum(Sow(i0^2) + 1, {i0, 10}))", "{395,{{1,4,9,16,25,36,49,64,81,100}}}");
	}

	public void testRefine() {
		check("Refine(Sqrt(x^2), Element(x, Reals))", "Abs(x)");
		check("Refine(Sqrt(x^2), Assumptions -> Element(x, Reals))", "Abs(x)");
		check("Refine(Sqrt(x^2), Element(x, Integers))", "Abs(x)");
		check("Refine(Sqrt(x^2), x>=0)", "x");

		check("Refine((x^3)^(1/3), x >= 0)", "x");

		check("Refine(Log(x), x<0)", "I*Pi+Log(-x)");

		check("Refine(Abs(x), x>0)", "x");
		check("Refine(Abs(x), Assumptions -> x>0)", "x");
		check("Refine(Abs(x), x>1)", "x");

		check("Refine(x>0, x>0)", "True");
		check("Refine(x>=0, x>0)", "True");
		check("Refine(x<0, x>0)", "False");

		check("Refine(x>-1, x>0)", "True");
		check("Refine(x>=-1, x>0)", "True");
		check("Refine(x<-1, x>0)", "False");

		check("Refine(x<0, x<0)", "True");
		check("Refine(x<=0, x<0)", "True");
		check("Refine(x>0, x<0)", "False");

		check("Refine(x<-1, x<0)", "x<-1");
		check("Refine(x<=-1, x<0)", "x<=-1");
		check("Refine(x>-1, x<0)", "x>-1");
		check("Refine(x>-1, x>0)", "True");
		check("Refine(x>-1, x>=0)", "True");

		check("Refine(Log(-4), x<0)", "I*Pi+Log(4)");

		check("Refine(Sin(k*Pi), Element(k, Integers))", "0");
		check("Refine(Cos(x+k*Pi), Element(k, Integers))", "(-1)^k*Cos(x)");

		check("Refine(Floor(2*a + 1), Element(a, Integers))", "1+2*a");
		check("Floor(2*a + 1)", "1+Floor(2*a)");

		check("Refine(Element(x, Integers), Element(x, integers))", "True");
		check("Refine(Floor(x),Element(x,Integers))", "x");

		check("Refine(Arg(x), Assumptions -> x>0)", "0");
		check("Refine(Arg(x), Assumptions -> x<0)", "Pi");
	}

	public void testReplace() {
		// By default, only the top level is searched for matches
		check("Replace(1 + x, {x -> 2})", "1+x");
		// use Replace() as an operator
		check("Replace({x_ -> x + 1})[10]", "11");
		// Replace replaces the deepest levels first
		check("Replace(x(1), {x(1) -> y, 1 -> 2}, All)", "x(2)");
		// Replace stops after the first replacement
		check("Replace(x, {x -> {}, _List -> y})", "{}");
		check("Replace(x^2, x^2 -> a + b)", "a+b");
		check("Replace(1+x^2, x^2 -> a + b)", "1+x^2");

		check("Replace(x, {x -> a, x -> b})", "a");
		check("Replace(x, {y -> a, x -> b, x->c})", "b");

		check("Replace(x, {{x -> a}, {x -> b}})", "{a,b}");
		check("Replace(x, {{e->q, x -> a}, {x -> b}})", "{a,b}");

		// Test with level specification
		check("Replace(f(1, x^2,x^2), x^2 -> a + b, {1})", "f(1,a+b,a+b)");
		check("Replace(f(1, x^2,x^2), z -> a + b, {1})", "f(1,x^2,x^2)");
		check("Replace(f(1, x^2,x^2), {{1 -> a + b},{x^2 -> a + b}}, {1})", "{f(a+b,x^2,x^2),f(1,a+b,a+b)}");
		check("Replace(f(1, x^2,x^2), {{z -> a + b},{w -> a + b}}, {1})", "{f(1,x^2,x^2),f(1,x^2,x^2)}");
		check("Replace(f(1, x, x), {y -> a, x -> b, x->c}, {1})", "f(1,b,b)");
		check("Replace(f(1, x, x), {y -> a, z -> b, w->c}, {1})", "f(1,x,x)");
		// check("Replace({x, x, x}, x :> RandomReal(), {1})",
		// "{0.20251412388709988,0.7585256738344558,0.0882472501351631}");
	}

	public void testReplaceAll() {
		check("ReplaceAll({a -> 1})[{a, b}]", "{1,b}");
		check("{x, x^2, y, z} /. x -> a", "{a,a^2,y,z}");
		check("{x, x^2, y, z} /. x -> {a, b}", "{{a,b},{a^2,b^2},y,z}");
		check("Sin(x) /. Sin -> Cos", "Cos(x)");
		check("1 + x^2 + x^4 /. x^p_ -> f(p)", "1+f(2)+f(4)");
		check("x /. {x -> 1, x -> 3, x -> 7}", "1");
		check("x /. {{x -> 1}, {x -> 3}, {x -> 7}}", "{1,3,7}");
		check("x /. {{a->z, x -> 1}, {x -> 3}, {x -> 7}}", "{1,3,7}");
		check("{a, b, c} /. List -> f", "f(a,b,c)");
		check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", "$r(a,rp(b),c)");

		check("f(a) + f(b) /. f(x_) -> x^2", "a^2+b^2");
		check("{1 + a, 2 + a, -3 + a} /. (x_ /; x < 0) + a -> p(x)", "{1+a,2+a,p(-3)}");
		check("$fac(x_ /; x > 0) := x!;$fac(6) + $fac(-4)", "720+$fac(-4)");

		check("f(a + b) + f(a + c) /. f(a + x_) + f(c + y_) -> p(x, y)", "p(b,a)");
		// wrong result
		check("f(a + b) + f(a + c) + f(b + d) /. f(a + x_) + f(c + y_) -> p(x, y)", "f(b+d)+p(b,a)");

		check("g(a + b, a) /. g(x_ + y_, x_) -> p(x, y)", "p(a,b)");
		check("g(a + b, b) /. g(x_ + y_, x_) -> p(x, y)", "p(b,a)");
		check("h(a + b, a + b) /. h(x_ + y_, x_ + z_) -> p(x, y, z)", "p(a,b,b)");
		check("SetAttributes($q, Orderless);f($q(a, b), $q(b, c)) /. f($q(x_, y_), $q(x_, z_)) -> p(x, y, z)",
				"p(b,a,c)");
		check("g(a + b + c) /. g(x_ + y_) -> p(x, y)", "p(a,b+c)");
		check("g(a + b + c + d) /. g(x_ + y_) -> p(x, y)", "p(a,b+c+d)");
		check("g(a + b + c + d, b + d) /. g(x_ + y_, x_) -> p(x, y)", "p(b+d,a+c)");
		check("a + b + c /. a + c -> p", "b+p");
		check("u(a) + u(b) + v(c) + v(d) /. u(x_) + u(y_) -> u(x + y)", "u(a+b)+v(c)+v(d)");
		check("SetAttributes($r, Flat);$r(a, b, a, b) /. $r(x_, x_) -> rp(x)", "rp($r(a,b))");

		// correct because OneIdentity is set:
		check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", "$r(a,rp(b),c)");
		check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(b, b) -> rp(b)", "$r(a,rp(b),c)");

		// wrong because OneIdentity is not set:
		check("SetAttributes($r, Flat);$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", "$r(a,rp(b),c)");
		// wrong because OneIdentity is not set:
		check("SetAttributes($r, Flat);$r(a, b, b, c) /. $r(b, b) -> rp(b)", "$r(a,rp(b),c)");
		// check("","");

		check("{c+d+e} /. x_+y_->{x,y}", "{{c,d+e}}");
		check("{a+b,x,c+d+e} /. x_+y_->{x,y}", "{{a,b},x,{c,d+e}}");
	}

	public void testReplacePart() {
		check("ReplacePart({a, b, c, d, e}, 3 -> xxx)", "{a,b,xxx,d,e}");
		check("ReplacePart({a, b, c, d, e}, {2 -> xx, 5 -> yy})", "{a,xx,c,d,yy}");
		check("ReplacePart({a,b,c^n}, {{3, 2} -> x + y, 2 -> b^100})", "{a,b^100,c^(x+y)}");

		check("ReplacePart({a, b, c, d, e}, xxx, 3)", "{a,b,xxx,d,e}");
		check("ReplacePart({a,b,c^n}, x+y, {{3, 2}, 2})", "{a,x+y,c^(x+y)}");
	}

	public void testReplaceTransformations() {
		check("x + y /. x -> 3", "3+y");
		check("x + y /. {x -> a, y -> b}", "a+b");
		check("x + y /. {{x -> 1, y -> 2}, {x -> 4, y -> 2}}", "{3,6}");
		check("Solve[x^3 - 5 x^2 + 2 x + 8 == 0, x]", "{{x->-1},{x->2},{x->4}}");
		check("x^2 + 6 /. {{x->-1},{x->2},{x->4}}", "{7,10,22}");
		check("{x^2, x^3, x^4} /. {x^3 -> u, x^n_ -> p(n)}", "{p(2),u,p(4)}");
		check("h(x + h(y)) /. h(u_) -> u^2", "(x+h(y))^2");
		check("{x^2, y^3} /. {x -> y, y -> x}", "{y^2,x^3}");
		check("x^2 /. x -> (1 + y) /. y -> b", "(1+b)^2");

		check("x^2 + y^6 /. {x -> 2 + a, a -> 3}", "(2+a)^2+y^6");
		check("x^2 + y^6 //. {x -> 2 + a, a -> 3}", "25+y^6");
		check("mylog(a b c d) /. mylog(x_ y_) -> mylog(x) + mylog(y)", "mylog(a)+mylog(b*c*d)");
		check("mylog(a b c d) //. mylog(x_ y_) -> mylog(x) + mylog(y)", "mylog(a)+mylog(b)+mylog(c)+mylog(d)");

		// check("ReplaceList({a, b, c, d}, {x__, y__} -> g({x}, {y}))", "");
		// check("", "");
		// check("", "");
		// check("", "");
	}

	public void testRest() {
		check("Rest(a + b + c + d)", "b+c+d");
		check("Rest(f(a, b, c, d))", "f(b,c,d)");
		check("NestList(Rest, {a, b, c, d, e}, 3)", "{{a,b,c,d,e},{b,c,d,e},{c,d,e},{d,e}}");
		check("Rest(1/b)", "-1");
	}

	public void testResultant() {

		check("Resultant((x-y)^2-2 , y^3-5, y)", "17-60*x+12*x^2-10*x^3-6*x^4+x^6");
		check("Resultant(x^2 - 2 x + 7, x^3 - x + 5, x)", "265");
		check("Resultant(x^2 + 2*x , x-c, x)", "2*c+c^2");

		check("Resultant(x^2 - 4, x^2 + 4*x + 4, x)", "0");
		check("Resultant(3 x + 9, 6 x^3 - 3 x + 12, x)", "-3807");

		// check("Resultant[a x^3 + b x^2 + c x + f, f x^3 + c x^2 + b x + a,
		// x]", "");
	}

	public void testReturn() {
		check("$a(x_):=Return(1); $b(x_):=Module({},$c=$a(y);2); $b(1)", "2");
		check("($f(x_) := (If(x > 5, Return(a)); x + 3));$f(6)", "a");
		check("($g(x_) := (Do(If(x > 5, Return(a)), {3}); x));$g(6)", "6");
		check("($h(x_) := (Catch(Do(If(x > 5, Throw(a)), {3}); x)));$h(6)", "a");
	}

	public void testRiffle() {
		check("Riffle({1, 2, 3, 4, 5, 6, 7, 8, 9}, x)", "{1,x,2,x,3,x,4,x,5,x,6,x,7,x,8,x,9}");
		check("Riffle({1, 2, 3, 4, 5, 6, 7, 8, 9}, {x, y})", "{1,x,2,y,3,x,4,y,5,x,6,y,7,x,8,y,9}");
		check("Riffle({1}, x)", "{1}");
		check("Riffle({a, b, c, d}, {x, y, z, w})", "{a,x,b,y,c,z,d,w}");
	}

	public void testRogersTanimotoDissimilarity() {
		check("RogersTanimotoDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", "3/4");
		check("RogersTanimotoDissimilarity({True, False, True}, {True, True, False})", "4/5");
		check("RogersTanimotoDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", "0");
		check("RogersTanimotoDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", "1");
	}

	public void testRoot() {
		// check("Root((#^2-3#-1)&, 1)", "");
	}

	public void testRoots() {
		// check("Roots(a*x^3+b*x^2+c^2+d, x)",
		// "{(-b/2-Sqrt(b^2-4*a*c)/2)/a,(-b/2+Sqrt(b^2-4*a*c)/2)/a}");
		check("Roots(a*x^2+b*x+c==0, x)", "x==(-b/2-Sqrt(b^2-4*a*c)/2)/a||x==(-b/2+Sqrt(b^2-4*a*c)/2)/a");
		check("Roots(3*x^3-8*x^2+-11*x+10==0,x)", "x==2/3||x==1+Sqrt(6)||x==1-Sqrt(6)");
		check("Roots(3*x^3-5*x^2+5*x-2==0,x)", "x==2/3||x==1/2-I*1/2*Sqrt(3)||x==1/2+I*1/2*Sqrt(3)");
		check("Roots(x^3 - 5*x + 4==0,x)", "x==1||x==-1/2-Sqrt(17)/2||x==-1/2+Sqrt(17)/2");

	}

	public void testRotateLeft() {
		check("RotateLeft({1,2,3,4,5},2)", "{3,4,5,1,2}");

	}

	public void testRotateRight() {
		check("RotateRight({1,2,3,4,5},2)", "{4,5,1,2,3}");

	}

	public void testRowReduce() {
		check("RowReduce({{1, 2, 3, 1}, {4, 5, 6, 1}, {7, 8, 9, 1}})",
				"{{1,0,-1,-1},\n" + " {0,1,2,1},\n" + " {0,0,0,0}}");
		check("RowReduce({{1, 2, 3, 1}, {4, 5, 6, -1}, {7, 8, 9, 2}})",
				"{{1,0,-1,0},\n" + " {0,1,2,0},\n" + " {0,0,0,1}}");

		check("RowReduce({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", "{{1,0,-1},\n" + " {0,1,2},\n" + " {0,0,0}}");
		check("RowReduce({{3, 1, a}, {2, 1, b}})", "{{1,0,a-b},\n" + " {0,1,-2*a+3*b}}");
		check("RowReduce({{1., 2., 3.}, {4., 5., 6.}, {7., 8., 9.}})",
				"{{1.0,0.0,-1.0},\n" + " {0.0,1.0,2.0},\n" + " {0.0,0.0,0.0}}");
		check("RowReduce({{1, I}, {I, -1}})", "{{1,I},\n" + " {0,0}}");
		check("RowReduce({{1,2,3,1,0,0}, {4,5,6,0,1,0}, {7,8,9,0,0,1}})",
				"{{1,0,-1,0,-8/3,5/3},\n" + " {0,1,2,0,7/3,-4/3},\n" + " {0,0,0,1,-2,1}}");
	}

	public void testRussellRaoDissimilarity() {
		check("RussellRaoDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", "3/5");
		check("RussellRaoDissimilarity({True, False, True}, {True, True, False})", "2/3");
		check("RussellRaoDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", "0");
		check("RussellRaoDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", "1");
	}

	public void testSameQUnsameQ() {
		check("SameQ(0.0, 0)", "False");
		check("UnsameQ(0.0, 0)", "True");
		check("$g(f(x))===v", "False");
		check("$g(f(x))===$g(f(x))", "True");
		check("$g(f(x))=!=v", "True");
		check("$g(f(x))=!=$g(f(x))", "False");
		check("Boole(Array(UnsameQ, {3, 3, 3}))",
				"{{{0,0,0},{0,0,1},{0,1,0}},{{0,0,1},{0,0,0},{1,0,0}},{{0,1,0},{1,0,0},{0,0,0}}}");
	}

	public void testSatisfiableQ() {
		check("SatisfiableQ[(a || b) && (! a || ! b), {a, b}]", "True");
		check("SatisfiableQ[(a && b) && (! a || ! b), {a, b}]", "False");
	}

	public void testScan() {
		check("Scan(($u(#) = x) &, {55, 11, 77, 88});{$u(76), $u(77), $u(78)}", "{$u(76),x,$u(78)}");
		check("Map(If(# > 5, #, False) &, {2, 4, 6, 8})", "{False,False,6,8}");
		check("Catch(Map(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", "6");
		check("Catch(Scan(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", "6");
		check("Reap(Scan(\n" + "   If(# > 0, Sow(#)) &, {1, {-2, Pi}, -Sqrt(3)},Infinity))[[2, 1]]",
				"{1,Pi,3,1/2,Sqrt(3)}");
	}

	public void testSec() {
		check("Sec(Pi/2)", "ComplexInfinity");
		check("Sec(0)", "1");
		check("Sec(2/5*Pi)", "1+Sqrt(5)");
		check("Sec(23/12*Pi)", "-Sqrt(2)+Sqrt(6)");
		check("Sec(z+1/2*Pi)", "-Csc(z)");
		check("Sec(Pi)", "-1");
		check("Sec(33*Pi)", "-1");
		check("Sec(z+Pi)", "-Sec(z)");
		check("Sec(z+42*Pi)", "Sec(z)");
		check("Sec(x+y+z+43*Pi)", "-Sec(x+y+z)");
		check("Sec(z+42*a*Pi)", "Sec(42*a*Pi+z)");
		check("Sec(z+4/3*Pi)", "-Sec(Pi/3+z)");
	}

	public void testSech() {
		check("Sech(1.8)", "0.3218048695065878");
		check("Sech(-x)", "Sech(x)");
		check("D(Sech(x),x)", "-Sech(x)*Tanh(x)");
	}

	public void testSelect() {
		check("Select({1, 2, 4, 7, 6, 2}, EvenQ)", "{2,4,6,2}");
		check("Select({1, 2, 4, 7, 6, 2}, # > 2 &)", "{4,7,6}");
		check("Select({1, 2, 4, 7, 6, 2}, # > 2 &, 1)", "{4}");
		check("Select({1, 2, 4, 7, x}, # > 2 &)", "{4,7}");
		check("Select(f(1, a, 2, b, 3), IntegerQ)", "f(1,2,3)");
		check("Select(Range(100), Mod(#, 3) == 1 && Mod(#, 5) == 1 &)", "{1,16,31,46,61,76,91}");
	}

	public void testSequence() {
		check("f(a, Sequence(b, c), d)", "f(a,b,c,d)");
		check("$u = Sequence(a, b, c)", "Sequence(a,b,c)");
		check("$u = Sequence(a, b, c);{$u,$u,$u}", "{a,b,c,a,b,c,a,b,c}");
		check("f({{a, b}, {c, d}, {a}}) /. List -> Sequence", "f(a,b,c,d,a)");
		check("f(a, b, c) /. f(x__) -> x", "Sequence(a,b,c)");
		check("{a, Sequence(b), c, Identity(d)}", "{a,b,c,d}");
	}

	public void testNormal() {
		check("Normal(Series(Exp(x),{x,0,5}))", "1+x+x^2/2+x^3/6+x^4/24+x^5/120");
		check("Normal(SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2))",
				"Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880");
	}

	public void testSeries() {
		// check("FullForm(Series(Exp(x),{x,0,10}))", "");
		// check("Series(Sin(Sqrt(x)), {x, 0, 5})", "");
		check("Series(f(x),{x,0,3})", "f(0)+f'(0)*x+f''(0)/2*x^2+Derivative(3)[f][0]/6*x^3+O(x)^4");
		check("Series(Exp(x),{x,0,2})", "1+x+x^2/2+O(x)^3");
		check("Series(Exp(f(x)),{x,0,2})", "E^f(0)+E^f(0)*f'(0)*x+1/2*(E^f(0)*f'(0)^2+E^f(0)*f''(0))*x^2+O(x)^3");
		check("Series(Exp(x),{x,0,5})", "1+x+x^2/2+x^3/6+x^4/24+x^5/120+O(x)^6");
		check("Series(100,{x,a,5})", "100");
	}

	public void testSeriesData() {
		check("SeriesData(100, 0, Table(i^2, {i, 10}), 0, 10, 1)", "Indeterminate");
		// check("SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1)",
		// "1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9+O(x)^10");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)",
				"Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)");
	}

	public void testShare() {
		check("Share(Table(j*(x + i), {i, 5}, {j, i}))", "24");
		check("Share(Table(xi = x + i; Table(j*xi, {j, i}), {i, 5}))", "0");
	}

	public void testSign() {
		check("Sign(Indeterminate)", "Indeterminate");
		check("Sign(2.5)", "1");
		check("Sign(-2.5)", "-1");
		check("Sign(0.0)", "0");
		check("Sign({-2, -1, 0, 1, 2})", "{-1,-1,0,1,1}");
		check("Pi>E", "True");
		check("Pi<E", "False");
		check("Sign(1+I)", "(1+I)/Sqrt(2)");
		check("Sign(1.0+I)", "0.7071067811865475+I*0.7071067811865475");
		check("Sign(E - Pi)", "-1");
		check("Sign(0)", "0");
		check("Sign(I)", "I");
		check("Sign(-2*I)", "-I");
		check("Sign(Indeterminate)", "Indeterminate");
		check("Sign(Infinity)", "1");
		check("Sign(-Infinity)", "-1");
		check("Sign(DirectedInfinity(1+I*3))", "(1+I*3)/Sqrt(10)");
		check("Sign(ComplexInfinity)", "Indeterminate");
		check("Sign(I*Infinity)", "I");

		check("Sign(-x)", "-Sign(x)");
		check("Sign(-3*a*b*c)", "-Sign(a*b*c)");
		check("Sign(1/z)", "1/Sign(z)");
	}

	public void testSimplify() {
		check("Simplify(Sqrt(x^2), Assumptions -> x>0)", "x");
		check("Together(2/(1/Tan(x) + Tan(x)))", "2/(Cot(x)+Tan(x))");
		check("Together(2*Tan(x)/(1 + Tan(x)^2))", "(2*Tan(x))/(1+Tan(x)^2)");
		check("Simplify(Sin(x)^2 + Cos(x)^2)", "1");
		check("Simplify((x - 1) (x + 1) (x^2 + 1) + 1)", "x^4");
		check("Simplify(3/(x + 3) + x/(x + 3))", "1");

		check("Simplify(2*Tan(x)/(1 + Tan(x)^2))", "(2*Tan(x))/(1+Tan(x)^2)");
	}

	public void testSin() {
		check("Sin(1.1*Pi)", "-0.30901699437494773");
		check("Sin({-0.5,9.1})", "{-0.479425538604203,0.3190983623493521}");
		check("Sin({{0.5,1.1},{6.4,7.5}})",
				"{{0.479425538604203,0.8912073600614354},\n" + " {0.11654920485049364,0.9379999767747389}}");
		check("Sin({1,2})", "{Sin(1),Sin(2)}");
		check("Sin(z+1/4*Pi)", "Sin(Pi/4+z)");
		check("Sin(z+1/2*Pi)", "Cos(z)");
		check("Sin(z+1/3*Pi)", "Sin(Pi/3+z)");
		check("Sin(Pi)", "0");
		check("Sin(z+Pi)", "-Sin(z)");
		check("Sin(z+42*Pi)", "Sin(z)");
		check("Sin(x+y+z+43*Pi)", "-Sin(x+y+z)");
		check("Sin(z+42*a*Pi)", "Sin(42*a*Pi+z)");
	}

	public void testSinc() {
		check("(2*Sqrt(2))/Pi", "(2*Sqrt(2))/Pi");
		check("2+(-Sqrt(5))/8", "2-Sqrt(5)/8");
		check("Sinc(0)", "1");
		check("Sinc(1/6*Pi)", "Pi/3");
		check("Sinc(1/4*Pi)", "(2*Sqrt(2))/Pi");
		check("Sinc(1/3*Pi)", "3/2*Sqrt(3)/Pi");
		check("Sinc(1/2*Pi)", "2/Pi");
		check("Sinc(Pi)", "0");
		check("Sinc(5/12*Pi)", "3/5*((1+Sqrt(3))*Sqrt(2))/Pi");
		check("Sinc(Pi/5)", "(5*Sqrt(5/8-Sqrt(5)/8))/Pi");
		check("Sinc(Pi/12)", "(3*(-1+Sqrt(3))*Sqrt(2))/Pi");
		check("Sinc(Pi/10)", "5/2*(-1+Sqrt(5))/Pi");
		check("Sinc(2/5*Pi)", "5/2*Sqrt(5/8+Sqrt(5)/8)/Pi");
		check("Sinc(3/10*Pi)", "5/6*(1+Sqrt(5))/Pi");
		check("Sinc(I)", "Sinh(1)");
		check("Sinc(ArcSin(x))", "x/ArcSin(x)");
		check("Sinc(ArcCos(x))", "Sqrt(1-x^2)/ArcCos(x)");
		check("Sinc(ArcTan(x))", "x/(Sqrt(1+x^2)*ArcTan(x))");
		check("Sinc(I*Infinity)", "Infinity");
		check("Sinc(ComplexInfinity)", "Indeterminate");
	}

	public void testSinh() {
		check("Sinh(0)", "0");
		check("Sinh(42*I*Pi)", "0");
		check("Sinh(3/2*I*Pi)", "-I");
		check("Sinh(5/3*Pi*I)", "-I*1/2*Sqrt(3)");

		check("Sinh(Infinity)", "Infinity");
		check("Sinh(ComplexInfinity)", "Indeterminate");
	}

	public void testSinIntegral() {
		check("SinIntegral(2.8)", "1.8320965890813214");
	}

	public void testSkewness() {
		check("Skewness({1.1, 1.2, 1.4, 2.1, 2.4})", "0.4070412816074879");
	}

	public void testSlot() {
		check("#", "#1");
		check("#42", "#42");
	}

	public void testSlotSequence() {
		check("##", "##1");
		check("##42", "##42");
		check("f(x, ##, y, ##) &[a, b, c, d]", "f(x,a,b,c,d,y,a,b,c,d)");
		check("f(##2) &[a, b, c, d]", "f(b,c,d)");
		check("{##2} &[a, b, c]", "{b,c}");
	}

	public void testSokalSneathDissimilarity() {
		check("SokalSneathDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", "3/4");
		check("SokalSneathDissimilarity({True, False, True}, {True, True, False})", "4/5");
		check("SokalSneathDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", "0");
		check("SokalSneathDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", "1");
	}

	public void testSolveIssue130() {
		check("Sqrt(-1)*(-1)^(1/10)", "(-1)^(3/5)");
		check("-1*Sqrt(-1)*(-1)^(1/10)", "-(-1)^(3/5)");
		check("Solve(x^10-1==0,x)",
				"{{x->-1},{x->1},{x->(-1)^(1/5)},{x->-(-1)^(1/5)},{x->(-1)^(2/5)},{x->-(-1)^(2/5)},{x->(\n"
						+ "-1)^(3/5)},{x->-(-1)^(3/5)},{x->(-1)^(4/5)},{x->-(-1)^(4/5)}}");

		// check("Trace(Solve(x^6-b==0,x))",
		// "???");
		check("Solve(x^4+b==0,x)", "{{x->(-1)^(1/4)*b^(1/4)},{x->-(-1)^(1/4)*b^(1/4)},{x->(-1)^(3/4)*b^(1/4)},{x->-(\n"
				+ "-1)^(3/4)*b^(1/4)}}");
		check("Solve(x^4-b==0,x)", "{{x->b^(1/4)},{x->-b^(1/4)},{x->-I*b^(1/4)},{x->I*b^(1/4)}}");
		check("Solve(x^8+b==0,x)",
				"{{x->(-1)^(1/8)*b^(1/8)},{x->-(-1)^(1/8)*b^(1/8)},{x->(-1)^(3/8)*b^(1/8)},{x->-(\n"
						+ "-1)^(3/8)*b^(1/8)},{x->(-1)^(5/8)*b^(1/8)},{x->-(-1)^(5/8)*b^(1/8)},{x->(-1)^(7/\n"
						+ "8)*b^(1/8)},{x->-(-1)^(7/8)*b^(1/8)}}");
		check("Solve(x^8-b==0,x)",
				"{{x->b^(1/8)},{x->-b^(1/8)},{x->-I*b^(1/8)},{x->I*b^(1/8)},{x->(-1)^(1/4)*b^(1/8)},{x->-(\n"
						+ "-1)^(1/4)*b^(1/8)},{x->(-1)^(3/4)*b^(1/8)},{x->-(-1)^(3/4)*b^(1/8)}}");
		check("Solve(x^10+b==0,x)", "{{x->-I*b^(1/10)},{x->I*b^(1/10)},{x->(-1)^(1/10)*b^(1/10)},{x->-(-1)^(1/10)*b^(\n"
				+ "1/10)},{x->(-1)^(3/10)*b^(1/10)},{x->-(-1)^(3/10)*b^(1/10)},{x->(-1)^(7/10)*b^(1/\n"
				+ "10)},{x->-(-1)^(7/10)*b^(1/10)},{x->(-1)^(9/10)*b^(1/10)},{x->-(-1)^(9/10)*b^(1/\n" + "10)}}");
		check("Solve(x^10-b==0,x)",
				"{{x->b^(1/10)},{x->-b^(1/10)},{x->(-1)^(1/5)*b^(1/10)},{x->-(-1)^(1/5)*b^(1/10)},{x->(\n"
						+ "-1)^(2/5)*b^(1/10)},{x->-(-1)^(2/5)*b^(1/10)},{x->(-1)^(3/5)*b^(1/10)},{x->-(-1)^(\n"
						+ "3/5)*b^(1/10)},{x->(-1)^(4/5)*b^(1/10)},{x->-(-1)^(4/5)*b^(1/10)}}");
		check("Solve(x^6+b==0,x)",
				"{{x->-I*b^(1/6)},{x->I*b^(1/6)},{x->(-1)^(1/6)*b^(1/6)},{x->-(-1)^(1/6)*b^(1/6)},{x->(\n"
						+ "-1)^(5/6)*b^(1/6)},{x->-(-1)^(5/6)*b^(1/6)}}");
		check("Solve(x^6-b==0,x)",
				"{{x->b^(1/6)},{x->-b^(1/6)},{x->(-1)^(1/3)*b^(1/6)},{x->-(-1)^(1/3)*b^(1/6)},{x->(\n"
						+ "-1)^(2/3)*b^(1/6)},{x->-(-1)^(2/3)*b^(1/6)}}");
		check("Solve(x^258==1,x)",
				"{{x->-1},{x->1},{x->(-1)^(1/129)},{x->-(-1)^(1/129)},{x->(-1)^(2/129)},{x->-(-1)^(\n"
						+ "2/129)},{x->(-1)^(1/43)},{x->-(-1)^(1/43)},{x->(-1)^(4/129)},{x->-(-1)^(4/129)},{x->(\n"
						+ "-1)^(5/129)},{x->-(-1)^(5/129)},{x->(-1)^(2/43)},{x->-(-1)^(2/43)},{x->(-1)^(7/\n"
						+ "129)},{x->-(-1)^(7/129)},{x->(-1)^(8/129)},{x->-(-1)^(8/129)},{x->(-1)^(3/43)},{x->-(\n"
						+ "-1)^(3/43)},{x->(-1)^(10/129)},{x->-(-1)^(10/129)},{x->(-1)^(11/129)},{x->-(-1)^(\n"
						+ "11/129)},{x->(-1)^(4/43)},{x->-(-1)^(4/43)},{x->(-1)^(13/129)},{x->-(-1)^(13/129)},{x->(\n"
						+ "-1)^(14/129)},{x->-(-1)^(14/129)},{x->(-1)^(5/43)},{x->-(-1)^(5/43)},{x->(-1)^(\n"
						+ "16/129)},{x->-(-1)^(16/129)},{x->(-1)^(17/129)},{x->-(-1)^(17/129)},{x->(-1)^(6/\n"
						+ "43)},{x->-(-1)^(6/43)},{x->(-1)^(19/129)},{x->-(-1)^(19/129)},{x->(-1)^(20/129)},{x->-(\n"
						+ "-1)^(20/129)},{x->(-1)^(7/43)},{x->-(-1)^(7/43)},{x->(-1)^(22/129)},{x->-(-1)^(\n"
						+ "22/129)},{x->(-1)^(23/129)},{x->-(-1)^(23/129)},{x->(-1)^(8/43)},{x->-(-1)^(8/43)},{x->(\n"
						+ "-1)^(25/129)},{x->-(-1)^(25/129)},{x->(-1)^(26/129)},{x->-(-1)^(26/129)},{x->(-1)^(\n"
						+ "9/43)},{x->-(-1)^(9/43)},{x->(-1)^(28/129)},{x->-(-1)^(28/129)},{x->(-1)^(29/129)},{x->-(\n"
						+ "-1)^(29/129)},{x->(-1)^(10/43)},{x->-(-1)^(10/43)},{x->(-1)^(31/129)},{x->-(-1)^(\n"
						+ "31/129)},{x->(-1)^(32/129)},{x->-(-1)^(32/129)},{x->(-1)^(11/43)},{x->-(-1)^(11/\n"
						+ "43)},{x->(-1)^(34/129)},{x->-(-1)^(34/129)},{x->(-1)^(35/129)},{x->-(-1)^(35/129)},{x->(\n"
						+ "-1)^(12/43)},{x->-(-1)^(12/43)},{x->(-1)^(37/129)},{x->-(-1)^(37/129)},{x->(-1)^(\n"
						+ "38/129)},{x->-(-1)^(38/129)},{x->(-1)^(13/43)},{x->-(-1)^(13/43)},{x->(-1)^(40/\n"
						+ "129)},{x->-(-1)^(40/129)},{x->(-1)^(41/129)},{x->-(-1)^(41/129)},{x->(-1)^(14/43)},{x->-(\n"
						+ "-1)^(14/43)},{x->(-1)^(1/3)},{x->-(-1)^(1/3)},{x->(-1)^(44/129)},{x->-(-1)^(44/\n"
						+ "129)},{x->(-1)^(15/43)},{x->-(-1)^(15/43)},{x->(-1)^(46/129)},{x->-(-1)^(46/129)},{x->(\n"
						+ "-1)^(47/129)},{x->-(-1)^(47/129)},{x->(-1)^(16/43)},{x->-(-1)^(16/43)},{x->(-1)^(\n"
						+ "49/129)},{x->-(-1)^(49/129)},{x->(-1)^(50/129)},{x->-(-1)^(50/129)},{x->(-1)^(17/\n"
						+ "43)},{x->-(-1)^(17/43)},{x->(-1)^(52/129)},{x->-(-1)^(52/129)},{x->(-1)^(53/129)},{x->-(\n"
						+ "-1)^(53/129)},{x->(-1)^(18/43)},{x->-(-1)^(18/43)},{x->(-1)^(55/129)},{x->-(-1)^(\n"
						+ "55/129)},{x->(-1)^(56/129)},{x->-(-1)^(56/129)},{x->(-1)^(19/43)},{x->-(-1)^(19/\n"
						+ "43)},{x->(-1)^(58/129)},{x->-(-1)^(58/129)},{x->(-1)^(59/129)},{x->-(-1)^(59/129)},{x->(\n"
						+ "-1)^(20/43)},{x->-(-1)^(20/43)},{x->(-1)^(61/129)},{x->-(-1)^(61/129)},{x->(-1)^(\n"
						+ "62/129)},{x->-(-1)^(62/129)},{x->(-1)^(21/43)},{x->-(-1)^(21/43)},{x->(-1)^(64/\n"
						+ "129)},{x->-(-1)^(64/129)},{x->(-1)^(65/129)},{x->-(-1)^(65/129)},{x->(-1)^(22/43)},{x->-(\n"
						+ "-1)^(22/43)},{x->(-1)^(67/129)},{x->-(-1)^(67/129)},{x->(-1)^(68/129)},{x->-(-1)^(\n"
						+ "68/129)},{x->(-1)^(23/43)},{x->-(-1)^(23/43)},{x->(-1)^(70/129)},{x->-(-1)^(70/\n"
						+ "129)},{x->(-1)^(71/129)},{x->-(-1)^(71/129)},{x->(-1)^(24/43)},{x->-(-1)^(24/43)},{x->(\n"
						+ "-1)^(73/129)},{x->-(-1)^(73/129)},{x->(-1)^(74/129)},{x->-(-1)^(74/129)},{x->(-1)^(\n"
						+ "25/43)},{x->-(-1)^(25/43)},{x->(-1)^(76/129)},{x->-(-1)^(76/129)},{x->(-1)^(77/\n"
						+ "129)},{x->-(-1)^(77/129)},{x->(-1)^(26/43)},{x->-(-1)^(26/43)},{x->(-1)^(79/129)},{x->-(\n"
						+ "-1)^(79/129)},{x->(-1)^(80/129)},{x->-(-1)^(80/129)},{x->(-1)^(27/43)},{x->-(-1)^(\n"
						+ "27/43)},{x->(-1)^(82/129)},{x->-(-1)^(82/129)},{x->(-1)^(83/129)},{x->-(-1)^(83/\n"
						+ "129)},{x->(-1)^(28/43)},{x->-(-1)^(28/43)},{x->(-1)^(85/129)},{x->-(-1)^(85/129)},{x->(\n"
						+ "-1)^(2/3)},{x->-(-1)^(2/3)},{x->(-1)^(29/43)},{x->-(-1)^(29/43)},{x->(-1)^(88/\n"
						+ "129)},{x->-(-1)^(88/129)},{x->(-1)^(89/129)},{x->-(-1)^(89/129)},{x->(-1)^(30/43)},{x->-(\n"
						+ "-1)^(30/43)},{x->(-1)^(91/129)},{x->-(-1)^(91/129)},{x->(-1)^(92/129)},{x->-(-1)^(\n"
						+ "92/129)},{x->(-1)^(31/43)},{x->-(-1)^(31/43)},{x->(-1)^(94/129)},{x->-(-1)^(94/\n"
						+ "129)},{x->(-1)^(95/129)},{x->-(-1)^(95/129)},{x->(-1)^(32/43)},{x->-(-1)^(32/43)},{x->(\n"
						+ "-1)^(97/129)},{x->-(-1)^(97/129)},{x->(-1)^(98/129)},{x->-(-1)^(98/129)},{x->(-1)^(\n"
						+ "33/43)},{x->-(-1)^(33/43)},{x->(-1)^(100/129)},{x->-(-1)^(100/129)},{x->(-1)^(\n"
						+ "101/129)},{x->-(-1)^(101/129)},{x->(-1)^(34/43)},{x->-(-1)^(34/43)},{x->(-1)^(\n"
						+ "103/129)},{x->-(-1)^(103/129)},{x->(-1)^(104/129)},{x->-(-1)^(104/129)},{x->(-1)^(\n"
						+ "35/43)},{x->-(-1)^(35/43)},{x->(-1)^(106/129)},{x->-(-1)^(106/129)},{x->(-1)^(\n"
						+ "107/129)},{x->-(-1)^(107/129)},{x->(-1)^(36/43)},{x->-(-1)^(36/43)},{x->(-1)^(\n"
						+ "109/129)},{x->-(-1)^(109/129)},{x->(-1)^(110/129)},{x->-(-1)^(110/129)},{x->(-1)^(\n"
						+ "37/43)},{x->-(-1)^(37/43)},{x->(-1)^(112/129)},{x->-(-1)^(112/129)},{x->(-1)^(\n"
						+ "113/129)},{x->-(-1)^(113/129)},{x->(-1)^(38/43)},{x->-(-1)^(38/43)},{x->(-1)^(\n"
						+ "115/129)},{x->-(-1)^(115/129)},{x->(-1)^(116/129)},{x->-(-1)^(116/129)},{x->(-1)^(\n"
						+ "39/43)},{x->-(-1)^(39/43)},{x->(-1)^(118/129)},{x->-(-1)^(118/129)},{x->(-1)^(\n"
						+ "119/129)},{x->-(-1)^(119/129)},{x->(-1)^(40/43)},{x->-(-1)^(40/43)},{x->(-1)^(\n"
						+ "121/129)},{x->-(-1)^(121/129)},{x->(-1)^(122/129)},{x->-(-1)^(122/129)},{x->(-1)^(\n"
						+ "41/43)},{x->-(-1)^(41/43)},{x->(-1)^(124/129)},{x->-(-1)^(124/129)},{x->(-1)^(\n"
						+ "125/129)},{x->-(-1)^(125/129)},{x->(-1)^(42/43)},{x->-(-1)^(42/43)},{x->(-1)^(\n"
						+ "127/129)},{x->-(-1)^(127/129)},{x->(-1)^(128/129)},{x->-(-1)^(128/129)}}");
		check("Solve(a*x^8+b==0,x)",
				"{{x->((-1)^(1/8)*b^(1/8))/a^(1/8)},{x->(-(-1)^(1/8)*b^(1/8))/a^(1/8)},{x->((-1)^(\n"
						+ "3/8)*b^(1/8))/a^(1/8)},{x->(-(-1)^(3/8)*b^(1/8))/a^(1/8)},{x->((-1)^(5/8)*b^(1/8))/a^(\n"
						+ "1/8)},{x->(-(-1)^(5/8)*b^(1/8))/a^(1/8)},{x->((-1)^(7/8)*b^(1/8))/a^(1/8)},{x->(-(\n"
						+ "-1)^(7/8)*b^(1/8))/a^(1/8)}}");
		check("Solve(a*x^10+b==0,x)",
				"{{x->(-I*b^(1/10))/a^(1/10)},{x->(I*b^(1/10))/a^(1/10)},{x->((-1)^(1/10)*b^(1/10))/a^(\n"
						+ "1/10)},{x->(-(-1)^(1/10)*b^(1/10))/a^(1/10)},{x->((-1)^(3/10)*b^(1/10))/a^(1/10)},{x->(-(\n"
						+ "-1)^(3/10)*b^(1/10))/a^(1/10)},{x->((-1)^(7/10)*b^(1/10))/a^(1/10)},{x->(-(-1)^(\n"
						+ "7/10)*b^(1/10))/a^(1/10)},{x->((-1)^(9/10)*b^(1/10))/a^(1/10)},{x->(-(-1)^(9/10)*b^(\n"
						+ "1/10))/a^(1/10)}}");

		check("Solve(a*x^3+b==0,x)",
				"{{x->-b^(1/3)/a^(1/3)},{x->((-1)^(1/3)*b^(1/3))/a^(1/3)},{x->(-(-1)^(2/3)*b^(1/3))/a^(\n" + "1/3)}}");
		check("Solve(a*x^5+b==0,x)",
				"{{x->-b^(1/5)/a^(1/5)},{x->((-1)^(1/5)*b^(1/5))/a^(1/5)},{x->(-(-1)^(2/5)*b^(1/5))/a^(\n"
						+ "1/5)},{x->((-1)^(3/5)*b^(1/5))/a^(1/5)},{x->(-(-1)^(4/5)*b^(1/5))/a^(1/5)}}");
		check("Solve(a*x^5-b==0,x)",
				"{{x->b^(1/5)/a^(1/5)},{x->(-(-1)^(1/5)*b^(1/5))/a^(1/5)},{x->((-1)^(2/5)*b^(1/5))/a^(\n"
						+ "1/5)},{x->(-(-1)^(3/5)*b^(1/5))/a^(1/5)},{x->((-1)^(4/5)*b^(1/5))/a^(1/5)}}");
		check("Solve(a*x^11-b==0,x)",
				"{{x->b^(1/11)/a^(1/11)},{x->(-(-1)^(1/11)*b^(1/11))/a^(1/11)},{x->((-1)^(2/11)*b^(\n"
						+ "1/11))/a^(1/11)},{x->(-(-1)^(3/11)*b^(1/11))/a^(1/11)},{x->((-1)^(4/11)*b^(1/11))/a^(\n"
						+ "1/11)},{x->(-(-1)^(5/11)*b^(1/11))/a^(1/11)},{x->((-1)^(6/11)*b^(1/11))/a^(1/11)},{x->(-(\n"
						+ "-1)^(7/11)*b^(1/11))/a^(1/11)},{x->((-1)^(8/11)*b^(1/11))/a^(1/11)},{x->(-(-1)^(\n"
						+ "9/11)*b^(1/11))/a^(1/11)},{x->((-1)^(10/11)*b^(1/11))/a^(1/11)}}");

		check("Solve(y==x+((1)/(x)),y)", "{{y->-(-1-x^2)/x}}");
		check("Solve(y==((1-x)^(1/(2)))+((x+3)^(1/(2))),y)", "{{y->Sqrt(1-x)+Sqrt(3+x)}}");

		check("Solve(x^24==1,x)",
				"{{x->-1},{x->1},{x->-I},{x->I},{x->(-1)^(1/12)},{x->-(-1)^(1/12)},{x->(-1)^(1/6)},{x->-(\n"
						+ "-1)^(1/6)},{x->(-1)^(1/4)},{x->-(-1)^(1/4)},{x->(-1)^(1/3)},{x->-(-1)^(1/3)},{x->(\n"
						+ "-1)^(5/12)},{x->-(-1)^(5/12)},{x->(-1)^(7/12)},{x->-(-1)^(7/12)},{x->(-1)^(2/3)},{x->-(\n"
						+ "-1)^(2/3)},{x->(-1)^(3/4)},{x->-(-1)^(3/4)},{x->(-1)^(5/6)},{x->-(-1)^(5/6)},{x->(\n"
						+ "-1)^(11/12)},{x->-(-1)^(11/12)}}");
	}

	public void testSolve() {
		// check("Solve(Abs((-3+x^2)/x) ==2,{x})",
		// "{{x->-3},{x->-1},{x->1},{x->3}}");
		check("Solve(x^3==-2,x)", "{{x->-2^(1/3)},{x->(-1)^(1/3)*2^(1/3)},{x->-(-1)^(2/3)*2^(1/3)}}");

		check("Solve(1 - (i*1)/10 == 0, i, Integers)", "{{i->10}}");
		check("Solve({x^2 + 2 y^3 == 3681, x > 0, y > 0}, {x, y}, Integers)",
				"{{x->15,y->12},{x->41,y->10},{x->57,y->6}}");
		check("Solve({x>=0,y>=0,x+y==7,2*x+4*y==20},{x,y}, Integers)", "{{x->4,y->3}}");
		check("Solve(x>=0 && y>=0 && x+y==7 && 2*x+4*y==20,{x,y}, Integers)", "{{x->4,y->3}}");
		check("Solve({2 x + 3 y == 4, 3 x - 4 y <= 5,x - 2 y > -21}, {x,  y}, Integers)",
				"{{x->-7,y->6},{x->-4,y->4},{x->-1,y->2}}");

		// timeouts in Cream engine
		// check("Solve({x^2 + x y + y^2 == 109}, {x, y}, Integers)", "");
		// check("Solve({x^12345 - 2 x^777 + 1 == 0}, {x}, Integers)", "");
		// check("Solve({2 x + 3 y - 5 z == 1 , 3 x - 4 y + 7 z == 3}, {x,
		// y, z}, Integers)", "");

		check("Solve((k*Q*q)/r^2+1/r^4==E,r)",
				"{{r->Sqrt(1/2)*Sqrt((Q*k*q+Sqrt(4*E+Q^2*k^2*q^2))/E)},{r->-Sqrt(1/2)*Sqrt((Q*k*q+Sqrt(\n"
						+ "4*E+Q^2*k^2*q^2))/E)},{r->-I*Sqrt(1/2)*Sqrt((-Q*k*q+Sqrt(4*E+Q^2*k^2*q^2))/E)},{r->I*Sqrt(\n"
						+ "1/2)*Sqrt((-Q*k*q+Sqrt(4*E+Q^2*k^2*q^2))/E)}}");
		// issue #120
		check("Solve(Sin(x)*x==0, x)", "{{x->0}}");
		check("Solve(Cos(x)*x==0, x)", "{{x->0},{x->Pi/2}}");
		// issue #121
		check("Solve(Sqrt(x)==-1, x)", "{}");
		check("Solve(x^2+1==0, x)", "{{x->-I},{x->I}}");
		check("Solve((k*Q*q)/r^2==E,r)",
				"{{r->(Sqrt(Q)*Sqrt(k)*Sqrt(q))/Sqrt(E)},{r->(-Sqrt(Q)*Sqrt(k)*Sqrt(q))/Sqrt(E)}}");
		check("Solve((k*Q*q)/r^2+1/r^4==E,r)",
				"{{r->Sqrt(1/2)*Sqrt((Q*k*q+Sqrt(4*E+Q^2*k^2*q^2))/E)},{r->-Sqrt(1/2)*Sqrt((Q*k*q+Sqrt(\n"
						+ "4*E+Q^2*k^2*q^2))/E)},{r->-I*Sqrt(1/2)*Sqrt((-Q*k*q+Sqrt(4*E+Q^2*k^2*q^2))/E)},{r->I*Sqrt(\n"
						+ "1/2)*Sqrt((-Q*k*q+Sqrt(4*E+Q^2*k^2*q^2))/E)}}");
		check("Solve((k*Q*q)/r^2+1/r^4==0,r)", "{{r->-I/(Sqrt(Q)*Sqrt(k)*Sqrt(q))},{r->I/(Sqrt(Q)*Sqrt(k)*Sqrt(q))}}");
		check("Solve(Abs(x-1) ==1,{x})", "{{x->0},{x->2}}");
		check("Solve(Abs(x^2-1) ==0,{x})", "{{x->-1},{x->1}}");
		check("Solve(Xor(a, b, c, d) && (a || b) && ! (c || d), {a, b, c, d}, Booleans)",
				"{{a->False,b->True,c->False,d->False},{a->True,b->False,c->False,d->False}}");
		check("Solve(Sin((-3+x^2)/x) ==2,{x})",
				"{{x->-Sqrt(12+ArcSin(2)^2)/2+ArcSin(2)/2},{x->Sqrt(12+ArcSin(2)^2)/2+ArcSin(2)/2}}");
		check("Solve({x^2-11==y, x+y==-9}, {x,y})", "{{x->-2,y->-7},{x->1,y->-10}}");

		// issue 42
		// check("$sol=Solve(x^3 + 2x^2 - 5x -3 ==0,x);N($sol)",
		// "{{x->-3.2534180395878516},{x->-0.5199693720627901},{x->1.773387411650642}}");

		// check("Solve(x^3 + 2x^2 - 5x -3 ==0, x)",
		// "{{x->(-1/3)*((1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(1/2)^(1/3)*(-I*9*331^(1/2)+25)^(\n"
		// +
		// "1/3)+2)},{x->(-1/3)*((-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(I*\n"
		// +
		// "1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-I*9*331^(1/2)+25)^(1/3)+2)},{x->(-1/3)*((I*1/2*3^(\n"
		// +
		// "1/2)-1/2)*(1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-I*\n"
		// + "9*331^(1/2)+25)^(1/3)+2)}}");
		check("Solve(2*Sin(x)==1/2,x)", "{{x->ArcSin(1/4)}}");
		check("Solve(3+2*Cos(x)==1/2,x)", "{{x->-Pi+ArcCos(5/4)}}");
		check("Solve(Sin(x)==0,x)", "{{x->0}}");
		check("Solve(Sin(x)==0.0,x)", "{{x->0}}");
		check("Solve(Sin(x)==1/2,x)", "{{x->Pi/6}}");
		check("Solve(sin(x)==0.5,x)", "{{x->0.5235987755982989}}");
		check("Solve(x^2-2500.00==0,x)", "{{x->-50.0},{x->50.0}}");
		check("Solve(x^2+a*x+1 == 0, x)", "{{x->-a/2-Sqrt(-4+a^2)/2},{x->-a/2+Sqrt(-4+a^2)/2}}");
		check("Solve((-3)*x^3 +10*x^2-11*x == (-4), {x})", "{{x->1},{x->4/3}}");

		check("Solve(x^2+50*x-2500.00==0,x)", "{{x->-80.90169943749474},{x->30.90169943749474}}");
		check("Solve(x+5.0==a,x)", "{{x->-5.0+a}}");

		check("Solve(a x + y == 7 && b x - y == 1, {x, y})", "{{x->8/(a+b),y->(a-7*b)/(-a-b)}}");
		check("Solve({a x + y == 7, b x - y == 1}, {x, y})", "{{x->8/(a+b),y->(a-7*b)/(-a-b)}}");

	}

	public void testSort() {
		check("Sort({d, b, c, a})", "{a,b,c,d}");
		check("Sort({4, 1, 3, 2, 2}, Greater)", "{4,3,2,2,1}");
		check("Sort({4, 1, 3, 2, 2}, #1 > #2 &)", "{4,3,2,2,1}");
		check("Sort({{a, 2}, {c, 1}, {d, 3}}, #1[[2]] < #2[[2]] &)", "{{c,1},{a,2},{d,3}}");
	}

	public void testSow() {
		check("Reap(Sow(a); b; Sow(c); Sow(d); e)", "{e,{{a,c,d}}}");
		check("Reap(Sum(Sow(i0^2) + 1, {i0, 10}))", "{395,{{1,4,9,16,25,36,49,64,81,100}}}");
	}

	public void testSpan() {
		check("{a, b, c, d, e, f, g, h}[[2 ;; -3]]", "{b,c,d,e,f}");
		check("{a, b, c, d, e, f, g, h}[[2 ;; 5]]", "{b,c,d,e}");
		check("{a, b, c, d, e, f, g, h}[[2 ;; All]]", "{b,c,d,e,f,g,h}");
	}

	public void testStirlingS2() {
		check("StirlingS2(10,11)", "0");
		check("StirlingS2(0,0)", "1");
		check("StirlingS2(a+b,0)", "0");
		check("StirlingS2(b,b)", "1");
		check("Table(StirlingS2(10, m), {m, 10})", "{1,511,9330,34105,42525,22827,5880,750,45,1}");
		check("StirlingS2({2, 4, 6}, 2)", "{1,7,31}");
		check("StirlingS2(10,4)", "34105");
		check("StirlingS2(1000, 500)", "11897164077580438091910055658742826<<SHORT>>", 35);
	}

	public void testStringLength() {
		check("StringLength(\"tiger\")", "5");
	}

	public void testStringTake() {
		check("StringTake(\"abcdefghijklm\", 6)", "\"abcdef\"");
		check("StringTake(\"abcdefghijklm\", -4)", "\"jklm\"");
		// check("StringTake(\"abcdefghijklm\", {5, 10})", "\"efghij\"");
	}

	public void testSubfactorial() {
		check("Subfactorial(n)", "Subfactorial(n)");
		check("Table(Subfactorial(n), {n, 10})", "{0,1,2,9,44,265,1854,14833,133496,1334961}");

		// The only number equal to the sum of subfactorials of its digits:
		check("148349 == Total(Subfactorial({1, 4, 8, 3, 4, 9}))", "True");

		// check("Subfactorial(10000)", "Subfactorial(10000)");
	}

	public void testSubsets() {
		check("Subsets({a,b,c})", "{{},{a},{b},{c},{a,b},{a,c},{b,c},{a,b,c}}");
		check("Subsets({a,b,c,d},{2})", "{{a,b},{a,c},{a,d},{b,c},{b,d},{c,d}}");
	}

	public void testSum() {
		check("Sum(i!,{i,3,n})", "-4-Subfactorial(-1)+(-1)^(1+n)*Gamma(2+n)*Subfactorial(-2-n)");
		check("Sum(i!,{i,1,n})", "-1-Subfactorial(-1)+(-1)^(1+n)*Gamma(2+n)*Subfactorial(-2-n)");

		check("Sum(g(i),{i,10,2})", "0");
		check("Sum(0.5^i,{i,1,Infinity})", "1.0");

		check("Sum((1/2)^i,{i,0,1})", "3/2");
		check("Sum((1/2)^i,{i,0,Infinity})", "2");
		check("Sum((1/2)^i,{i,1,Infinity})", "1");
		check("Sum((1/2)^i,{i,3,Infinity})", "1/4");

		check("Sum(a^i,{i,0,1})", "1+a");
		check("Sum(a^i,{i,0,Infinity})", "1-a/(-1+a)");
		check("Sum(a^i,{i,1,Infinity})", "-a/(-1+a)");
		check("Sum(a^i,{i,3,Infinity})", "-a-a^2-a/(-1+a)");

		check("Sum(0,{i,-4,Infinity})", "0");
		check("Sum((-2)^i,{i,0,Infinity})", "Sum((-2)^i,{i,0,Infinity})");
		check("Sum(42^i,{i,0,Infinity})", "Sum(42^i,{i,0,Infinity})");

		check("Sum(i^k,{i,1,n})", "HarmonicNumber(n,-k)");
		check("Sum(i^5,{i,1,n})", "-n^2/12+5/12*n^4+n^5/2+n^6/6");
		check("Sum(f(i,1),{i,{a,b}})", "f(a,1)+f(b,1)");

		check("Sum(c/(i-j+1), {j,i+1,n}, {i,1,n})", "c*Sum(1/(i-j+1),{j,i+1,n},{i,1,n})");
		check("Sum(-(-c*j+c),{j,i+1,n})", "-c*(-i+n)+1/2*c*(1+i+n)*(-i+n)");

		check("Sum(c*(i-j+1), {j,i+1,n}, {i,1,n})",
				"c*n*(-i+n)-1/2*c*n*(1+i+n)*(-i+n)+c*(1/2*n*(-i+n)+1/2*(-i+n)*n^2)");
		check("Simplify(1/2*c*(n-i)*n^2-1/2*c*n*(n+i+1)*(n-i)+3/2*c*n*(n-i))", "1/2*c*(-2+i)*n*(i-n)");

		check("Sum(c*(n-1), {j,i,n-1})", "-c*(-i+n)+c*n*(-i+n)");
		check("Sum(c, {j,i,n-1}, {i,1,n-1})", "-c*(-i+n)+c*n*(-i+n)");
		check("Sum(1,{k,j+i,n})", "1-i-j+n");
		check("Sum(k,{k,1,n+1})", "1/2*(1+n)*(2+n)");
		check("Sum(i^(1/2), {i, 1, n} )", "HarmonicNumber(n,-1/2)");
		check("Sum(1/i, {i, 1, n} )", "HarmonicNumber(n)");
		check("Sum(i^(-3), {i, 1, n} )", "HarmonicNumber(n,3)");
		check("Sum(Ceiling(Log(i)),{i,1,n})",
				"(1-(1+Floor(Log(n)))*E^Floor(Log(n))+E^(1+Floor(Log(n)))*Floor(Log(n)))/(-1+E)+(-E^Floor(Log(n))+n)*Ceiling(Log(n))");
		check("Sum(Ceiling(Log(a,i)),{i,1,n})",
				"(1-(1+Floor(Log(a,n)))*a^Floor(Log(a,n))+a^(1+Floor(Log(a,n)))*Floor(Log(a,n)))/(\n"
						+ "-1+a)+(-a^Floor(Log(a,n))+n)*Ceiling(Log(a,n))");
		check("Sum(i*1/2*i,{i,1,n})", "1/2*(n/6+n^2/2+n^3/3)");
		check("Sum(k * k,{k,1,n+1})", "1+13/6*n+3/2*n^2+n^3/3");
		check("Sum(k,{k,4,2})", "0");
		check("Sum(k,{k,a,b})", "1/2*(1-a+b)*(a+b)");
		check("Sum(c, {k, 1, Infinity} )", "Sum(c,{k,1,Infinity})");
		check("Sum(k,{k,1,n+1})", "1/2*(1+n)*(2+n)");
		check("Sum(f(i,1),{i,{a,b}})", "f(a,1)+f(b,1)");
		check("Sum(f(i, j), {i, {a, b}}, {j, 1, 2})", "f(a,1)+f(a,2)+f(b,1)+f(b,2)");
		check("Sum(c, {i, 1, j}, {j, 1, 2})", "2*c*j");

		check("Sum(c, {k, -Infinity, 10} )", "Sum(c,{k,-Infinity,10})");

		check("Sum(c+k, {k, 1, m} )", "c*m+1/2*m*(1+m)");
		check("Sum(c, {k, l, m} )", "c*(1-l+m)");
		check("Sum(c, {k, 1, m} )", "c*m");
		check("Sum(a, {k, j, n} )", "a*(1-j+n)");
		check("Sum(c, {i0, 1, n0} )", "c*n0");
		check("Sum(c, {i0, 0, n0} )", "c*(1+n0)");
		check("Sum(c*n0, {i0, 1, n0} )", "c*n0^2");
		check("Sum(c*n0, {i0, 0, n0} )", "c*n0*(1+n0)");

		check("Sum(c, {i0, 1, n0}, {j0, 1, n0})", "c*n0^2");
		check("Sum(i0, {i0, 0, n0})", "1/2*n0*(1+n0)");
		check("Sum(i^2, {i, 1, n})", "n/6+n^2/2+n^3/3");
		check("Sum(4*i^2, {i, 0, n})", "4*(n/6+n^2/2+n^3/3)");
		check("Sum(i0^3, {i0, 0, n0})", "n0^2/4+n0^3/2+n0^4/4");
		check("Sum(i0^3+p^2, {i0, 0, n0})", "n0^2/4+n0^3/2+n0^4/4+(1+n0)*p^2");
		check("Sum(Binomial(n0,i0), {i0, 0, n0})", "2^n0");
		check("sum(i0*binomial(n0,i0), {i0, 0, n0})", "n0/2^(1-n0)");
		check("sum(p, {i0, 1, n0})", "n0*p");
		check("sum(p+q, {i0, 1, n0})", "n0*p+n0*q");
		check("sum(p, {i0, 0, n0})", "(1+n0)*p");
		check("sum(4, {i0, 0, n0})", "4*(1+n0)");
		check("sum(lcm(3, k), {k, 100})", "11784");
		check("Sum(sin(x), x)", "Sum(Sin(x),x)");
		check("Sum(x, x)", "1/2*x*(1+x)");
		check("Sum(x^2, x)", "x/6+x^2/2+x^3/3");
		check("Sum(x^3, x)", "x^2/4+x^3/2+x^4/4");
		check("Sum(x^4, x)", "-x/30+x^3/3+x^4/2+x^5/5");
		check("Sum(c, {i, 1, n}, {j, 1, n})", "c*n^2");
		check("Sum(c, {i, 1, j}, {j, 1, n})", "c*j*n");
		check("Sum(c, {j, 1, n}, {i, 1, j})", "1/2*c*n*(1+n)");
		check("Sum((i^2 + i)/2, {i,1,n})", "1/4*n*(1+n)+1/2*(n/6+n^2/2+n^3/3)");
		check("Sum(i*(i + 1)/2, {i,1,n})", "1/4*n*(1+n)+1/2*(n/6+n^2/2+n^3/3)");

		check("Sum(k^a,{k,j,n})", "-HurwitzZeta(-a,1+n)+HurwitzZeta(-a,j)");
		check("-1/4*a^4", "-a^4/4");
		check("Sum(k^3,{k,a,b})", "-a^2/4+a^3/2-a^4/4+b^2/4+b^3/2+b^4/4");
	}

	public void testSurd() {
		check("Surd(-2.,5)", "-1.148698354997035");
		check("Surd(-3,2)", "Indeterminate");
		check("Surd({-3, -2, -1, 0, 1, 2, 3}, 7)", "{(-3)^(1/7),(-2)^(1/7),-1,0,1,2^(1/7),3^(1/7)}");
		check("N(Surd({-3, -2, -1, 0, 1, 2, 3}, 7))",
				"{-1.169930812758687,-1.1040895136738123,-1.0,0.0,1.0,1.1040895136738123,1.169930812758687}");
		check("N(Surd( -2,  5),25)", "-1.1486983549970350067986269");

	}

	public void testSwitch() {
		check("$f(b_) := switch(b, True, 1, False, 0, _, -1);{$f(True), $f(False), $f(x)}", "{1,0,-1}");
	}

	public void testTable() {
		// check("Timing(Length(Table(i, {i, 1, 10000})))", "{0.159,10000}");
		check("Table(x,10)", "{x,x,x,x,x,x,x,x,x,x}");
		check("Table(x,-1)", "{}");
		check("Table(0,{4-1})", "{0,0,0}");
		check("$a=10;Table($a^2, {$a, 10})", "{1,4,9,16,25,36,49,64,81,100}");
		check("Table(f[a], {a, 0, 20, 2})", "{f(0),f(2),f(4),f(6),f(8),f(10),f(12),f(14),f(16),f(18),f(20)}");
		check("Table(x, {10})", "{x,x,x,x,x,x,x,x,x,x}");
		check("Table(10 a + j, {a, 4}, {j, 3})", "{{11,12,13},{21,22,23},{31,32,33},{41,42,43}}");
		check("Table(f[a], {a, 10, -5, -2})", "{f(10),f(8),f(6),f(4),f(2),f(0),f(-2),f(-4)}");
		check("Table(Sqrt(x), {x, {1, 4, 9, 16}})", "{1,2,3,4}");
		check("Table(100*a + 10 j + k, {a, 3}, {j, 2}, {k, 4})",
				"{{{111,112,113,114},{121,122,123,124}},{{211,212,213,214},{221,222,223,224}},{{\n"
						+ "311,312,313,314},{321,322,323,324}}}");
		check("Table(j^(1/a), {a, {1, 2, 4}}, {j, {1, 4, 9}})", "{{1,4,9},{1,2,3},{1,4^(1/4),9^(1/4)}}");
		check("Table(2^x + x, {x, a, a + 5*b, b})",
				"{a+2^a,a+b+2^(a+b),a+2^(a+2*b)+2*b,a+2^(a+3*b)+3*b,a+2^(a+4*b)+4*b,a+2^(a+5*b)+5*b}");
		check("Table(a, {a, Pi, 2 Pi, Pi / 2})", "{Pi,3/2*Pi,2*Pi}");
	}

	public void testTake() {
		check("Take({a, b, c, d, e, f}, 4)", "{a,b,c,d}");
		check("Take({a, b, c, d, e, f}, -3)", "{d,e,f}");
		check("Take({a, b, c, d, e, f}, {2,4})", "{b,c,d}");
		check("Take({{11, 12, 13}, {21, 22, 23},{31, 32, 33}}, 2, 2)", "{{11,12},{21,22}}");
		check("Take({{11, 12, 13}, {21, 22, 23},a,{31, 32, 33}}, 3, 2)",
				"Take({{11,12,13},{21,22,23},a,{31,32,33}},3,2)");
	}

	public void testTally() {
		check("Tally({{a, b}, {w, x, y, z}, E, {w, x, y, z}, E}, Head(#1) === Head(#2) &)", "{{{a,b},3},{E,2}}");
		check("Tally({a,a,b,a,c,b,a})", "{{a,4},{b,2},{c,1}}");
		check("Tally({b,a,b,a,c,b,a})", "{{b,3},{a,3},{c,1}}");
	}

	public void testTan() {
		check("Tan(Pi/2)", "ComplexInfinity");
		check("Tan(1/6*Pi)", "1/Sqrt(3)");
		check("Tan(Pi)", "0");
		check("Tan(z+Pi)", "Tan(z)");
		check("Tan(z+42*Pi)", "Tan(z)");
		check("Tan(z+42*a*Pi)", "Tan(42*a*Pi+z)");
		check("Tan(z+1/2*Pi)", "-Cot(z)");
		check("Tan(Pi)", "0");
		check("Tan(33*Pi)", "0");
		check("Tan(z+Pi)", "Tan(z)");
		check("Tan(z+42*Pi)", "Tan(z)");
		check("Tan(x+y+z+43*Pi)", "Tan(x+y+z)");
		check("Tan(z+42*a*Pi)", "Tan(42*a*Pi+z)");
		check("Tan(z+4/3*Pi)", "Tan(Pi/3+z)");
	}

	public void testTautologyQ() {
		check("TautologyQ((a || b) && (! a || ! b), {a, b})", "False");
		check("TautologyQ((a || b) || (! a && ! b), {a, b})", "True");
	}

	public void testTaylor() {
		check("Taylor(ArcSin(x),{x,0,10})", "x+x^3/6+3/40*x^5+5/112*x^7+35/1152*x^9");
		check("Limit(ArcSin(x)/x,x->0)", "1");
		check("(-0^2+1)^(-1/2)", "1");
	}

	public void testTeXForm() {
		check("TeXForm(Infinity)", "\"\\infty\"");
		check("TeXForm(-Infinity)", "\"-\\infty\"");
		check("TeXForm(Hold(GoldenRatio))", "\"\\text{Hold}(\\phi)\"");
		check("TeXForm(GoldenRatio)", "\"\\frac{1+\\sqrt{5}}{2}\"");
		check("TeXForm(2+I*3)", "\"2 + 3\\,i \"");
		check("TeXForm(a+b^2)", "\"a+b^{2}\"");
		check("TeXForm(Expand((x+y)^3))", "\"x^{3}+3\\,x^{2}\\,y+3\\,x\\,y^{2}+y^{3}\"");
		check("TeXForm(3*a+b^2)", "\"3\\,a+b^{2}\"");
		check("TeXForm(x/Sqrt(5))", "\"\\frac{x}{\\sqrt{5}}\"");
		check("TeXForm(x^(1/3))", "\"\\sqrt[3]{x}\"");
		check("TeXForm(alpha)", "\"\\alpha\"");
		check("TeXForm({a,b,c})", "\"\\{a,b,c\\}\"");
		check("TeXForm({{a,b},c})", "\"\\{\\{a,b\\},c\\}\"");
		check("TeXForm({{a, b, c}, {d, e, f}})", "\"\\left(\n" + "\\begin{array}{ccc}\n" + "a & b & c \\\\\n"
				+ "d & e & f \n" + "\\end{array}\n" + "\\right) \"");

		check("TeXForm(Integrate(f(x),x))", "\"\\int  f(x)\\,\\mathrm{d}x\"");
		check("TeXForm(Limit(f(x), x ->Infinity))", "\"\\lim_{x\\to {\\infty} }\\,{f(x)}\"");
		check("TeXForm(Sum(f(n), {n, 1, m}))", "\"\\sum_{n = 1}^{m} {f(n)}\"");
		check("TeXForm(Product(f(n), {n, 1, m}))", "\"\\prod_{n = 1}^{m} {f(n)}\"");
		check("TeXForm(Subscript(a,b))", "\"a_b\"");
		check("TeXForm(Superscript(a,b))", "\"a^b\"");
		check("TeXForm(Subscript(x,2*k+1))", "\"x_{1+2\\,k}\"");
		check("TeXForm(Subsuperscript(a,b,c))", "\"a_b^c\"");
		check("TeXForm(HarmonicNumber(n))", "\"H_n\"");
		check("TeXForm(HarmonicNumber(m,n))", "\"H_m^{(n)}\"");
		check("TeXForm(HurwitzZeta(m,n))", "\"zeta (m,n)\"");
		check("TeXForm(Zeta(m,n))", "\"zeta (m,n)\"");

		check("TeXForm(fgh(a,b))", "\"\\text{fgh}(a,b)\"");
	}

	public void testThread() {
		check("Thread(f({a, b, c}))", "{f(a),f(b),f(c)}");
		check("Thread(f({a, b, c}, {x, y, z}))", "{f(a,x),f(b,y),f(c,z)}");
		check("Thread(Log(x == y), Equal)", "Log(x)==Log(y)");
	}

	public void testTimes() {
		// issue #137
		check("12*2^x*3^y", "2^(2+x)*3^(1+y)");
		check("8*2^x", "2^(3+x)");
		check("12*2^x", "3*2^(2+x)");

		check("-Infinity", "-Infinity");
		check("Times(I*Sqrt(2), I*Sqrt(3))", "-Sqrt(6)");
		check("Sin(x)^(-2)/Tan(x)", "Csc(x)^2*Cot(x)");
		check("Sin(x)/Tan(x)", "Cos(x)");
		// check("Sin(x)^2/Tan(x)^3", "Cos(x)^2*Cot(x)");
		// check("Sin(x)^3/Tan(x)^2", "Cos(x)^2*Sin(x)");
		// check("Sin(x)^2/Tan(x)", "Cos(x)*Sin(x)");
		// check("Sin(x)/Tan(x)^2", "Cos(x)*Cot(x)");

		check("Sin(x)^(-2)", "Csc(x)^2");
		check("Sin(x)/Tan(x)^(-2)", "Tan(x)^2*Sin(x)");
		check("Sin(x)/Cos(x)", "Tan(x)");
		check("Cos(x)*Tan(x)", "Sin(x)");
		check("Cos(x)/Sin(x)", "Cot(x)");
		check("Tan(x)/Sin(x)", "Sec(x)");

		check("Times()", "1");
		// OutputForm: I*Infinity is DirectedInfinity[I]
		check("I*Infinity", "I*Infinity");
	}

	public void testTimeConstrained() {
		if (!Config.JAS_NO_THREADS) {
			check("TimeConstrained(Do(i^2, {i, 10000000}), 1)", "$Aborted");
		}
	}

	public void testTogether() {
		check("Together(1 < 1/x + 1/(1 + x) < 2)", "1<(1+2*x)/(x+x^2)<2");
		check("Together(1/(1+1/(1+1/a)))", "(1+a)/(1+2*a)");
		check("Together(1/(1+1/(1+1/(1+a))))", "(2+a)/(3+2*a)");
		check("ExpandAll(a*b)", "a*b");
		check("ExpandAll(a*b^(-1))", "a/b");
		check("(a*b)^(-1)", "1/(a*b)");
		check("Together(a/b + c/d)", "(b*c+a*d)/(b*d)");
		check("Together((-7*a^(-1)*b+1)*(-a^(-1)*b-1)^(-1))", "(a-7*b)/(-a-b)");
		check("Together(a*b^(-2)+c*d^(-3))", "(b^2*c+a*d^3)/(b^2*d^3)");
		check("Together(-a*b^(-2)-c*d^(-3))", "(-b^2*c-a*d^3)/(b^2*d^3)");
		check("Together((-8)*a^(-1)*(-a^(-1)*b-1)^(-1))", "8/(a+b)");

		check("Together((2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1)-4*x*(2*x^3-4*x+5)*(3*x^2+2)^(-1)+5*(2*x^3-4*x+\n"
				+ "5)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)*(3*x^2+2)^(-1)+x^2-2)",
				"(17-60*x+12*x^2-10*x^3-6*x^4+x^6)/(4+12*x^2+9*x^4)");
		check("Together((4*x^6-8*x^4+10*x^3)*(3*x^2+2)^(-1)+(-8*x^4+16*x^2-20*x)*(3*x^2+2)^(-1)+(10*x^3\n"
				+ "-20*x+25)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)", "(25-60*x+32*x^2-10*x^3-8*x^6)/(2+3*x^2)");
		check("Together((2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1)-4*x*(2*x^3-4*x+5)*(3*x^2+2)^(-1)+5*(2*x^3-4*x+\n"
				+ "5)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)*(x)^(-1)-2)", "(25-64*x+32*x^2-16*x^3-8*x^6)/(2*x+3*x^3)");
		check("Together(a + c/g)", "(c+a*g)/g");
		check("Together(((7*b*a^(-1)-1)*(-b*a^(-1)-1)^(-1)+7)*a^(-1))", "8/(a+b)");

		check("ExpandAll((x^2-1)*x^2+x*(x^2-1))", "-x-x^2+x^3+x^4");
		check("together(x^2/(x^2 - 1) + x/(x^2 - 1))", "x/(-1+x)");
		check("Together((1/3*x-1/6)*(x^2-x+1)^(-1))", "(-1+2*x)/(6*(1-x+x^2))");
		check("Together((-a^2*r^2*x-a*b*r*x-a*c*x-a^2*r^3-2*a*b*r^2-a*c*r-b^2*r-b*c)*((a*r^2+b*r)^2+2*c*(a*r^2+b*r)+c^2)^(-1))",
				"(-b-a*r-a*x)/(c+b*r+a*r^2)");
		check("Together((-8)*a^(-1)*(-a^(-1)*b-1)^(-1))", "8/(a+b)");

		check("Together(a/b + c/d)", "(b*c+a*d)/(b*d)");
		check("Together((-a^(-1)*b-1))", "(-a-b)/a");
		check("((-b-a)*a^(-1))^(-1)", "a/(-a-b)");
		check("Together(1/x + 1/(x + 1) + 1/(x + 2) + 1/(x + 3))", "(6+22*x+18*x^2+4*x^3)/(6*x+11*x^2+6*x^3+x^4)");

	}

	public void testToPolarCoordinates() {
		check("-Pi/2 < 0", "True");
		check("Arg(1) ", "0");
		check("-Pi/2 < Arg(1) ", "True");
		check(" Arg(1) <= Pi/2", "True");

		check("ToPolarCoordinates({x, y})", "{Sqrt(x^2+y^2),ArcTan(x,y)}");
		check("ToPolarCoordinates({1, 1})", "{Sqrt(2),Pi/4}");
		check("ToPolarCoordinates({x, y, z})", "{Sqrt(x^2+y^2+z^2),ArcCos(x/Sqrt(x^2+y^2+z^2)),ArcTan(y,z)}");
		check("ToPolarCoordinates({{{x, y}, {1, 0}}, {{-2, 0}, {0, 1}}})",
				"{{{Sqrt(x^2+y^2),ArcTan(x,y)},{1,0}},{{2,Pi},{1,Pi/2}}}");
		check("ToPolarCoordinates({{{1, -1}}})", "{{{Sqrt(2),-Pi/4}}}");
		check("ToPolarCoordinates({{} , {}})", "{{},{}}");
	}

	public void testTotal() {
		check("Total({x^2, 3 x^3, 1},{1})", "1+x^2+3*x^3");
		check("Total({x^2, 3 x^3, 1})", "1+x^2+3*x^3");
		check("Total({{1,2,3},{4,5,6},{7,8,9}})", "{12,15,18}");
		check("Total({{1,2,3},{4,5,6},{7,8,9}},{1})", "{12,15,18}");
		// total the rows
		check("Total({{1,2,3},{4,5,6},{7,8,9}},{2})", "{6,15,24}");
		check("Total({{1,2,3},{4,5,6},{7,8,9}},2)", "45");
	}

	public void testTr() {
		check("Tr({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, f)", "f(1,5,9)");
		// check("Tr[{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, Plus, 1]", "");
	}

	public void testTrace() {
		check("Trace(u = 2; Do(u = u*u, {3}); u, Times)", "{{{{{u*u,2*2}},{{u*u,4*4}},{{u*u,16*16}}}}}");
		check("x=5;Trace(Mod((3 + x)^2, x - 1))", "{{{{x,5},3+5,8},8^2,64},{{x,5},-1+5,4},Mod(64,4),0}");
		check("Trace(u = 2; Do(u = u*u, {3}); u)",
				"{{{u=2,2},{{{{u,2},{u,2},2*2,4},4},{{{u,4},{u,4},4*4,16},16},{{{u,16},{u,16},16*\n"
						+ "16,256},256},Null},Null},{u,256},256}");

	}

	public void testTrigExpand() {
		check("trigexpand(Sin(2 x))", "2*Cos(x)*Sin(x)");
		check("trigexpand(Sin(x)*Tan(x))", "Sin(x)*Tan(x)");
		check("trigexpand(Sin(x + y))", "Cos(y)*Sin(x)+Cos(x)*Sin(y)");
		check("trigexpand(Cos(x + y))", "Cos(x)*Cos(y)-Sin(x)*Sin(y)");
		check("trigexpand(Sin(x + y + z))",
				"Cos(y)*Cos(z)*Sin(x)+Cos(x)*Cos(z)*Sin(y)+Cos(x)*Cos(y)*Sin(z)-Sin(x)*Sin(y)*Sin(z)");
		check("trigexpand(Cos(2 x))", "Cos(x)^2-Sin(x)^2");
		check("trigexpand(Sin(5*x))", "-10*Cos(x)^2*Sin(x)^3+Sin(x)^5+5*Cos(x)^4*Sin(x)");
	}

	public void testTrigReduce() {
		check("TrigReduce(Sin(x)*Tan(y))", "1/2*(-Cos(x+y)+Cos(x-y))*Sec(y)");
		check("TrigReduce(Cos(x)*Tan(y))", "-1/2*(-Sin(x+y)+Sin(x-y))*Sec(y)");
		check("TrigReduce(2 Cos(x)^2)", "1+Cos(2*x)");
		check("TrigReduce(2 Cos(x)*Sin(y))", "Sin(x+y)+Sin(-x+y)");
		check("TrigReduce(15 Sin(12 x)^2 + 12 Sin(15 x)^2)", "27/2-15/2*Cos(24*x)-6*Cos(30*x)");
		check("TrigReduce(2*Sinh(u)*Cosh(v))", "Sinh(u+v)+Sinh(u-v)");
		check("TrigReduce(3*Sinh(u)*Cosh(v)*k)", "3/2*k*Sinh(u+v)+3/2*k*Sinh(u-v)");
		// check("TrigReduce(2 Tan(x)*Tan(y))",
		// "(2*Cos(-y+x)-2*Cos(y+x))*(Cos(-y+x)+Cos(y+x))^(-1)");
	}

	public void testTrigToExp() {
		check("TrigToExp(Cos(x))", "1/(2*E^(I*x))+E^(I*x)/2");
		check("TrigToExp(Cosh(x)+a)", "a+1/2*(E^x+E^(-x))");
		check("TrigToExp(Csch(x)+a)", "a+2/(E^x-1/E^x)");
		check("TrigToExp(Coth(x)+a)", "a+(E^x+E^(-x))/(E^x-1/E^x)");
		check("TrigToExp(Sech(x)+a)", "a+2/(E^x+E^(-x))");
		check("TrigToExp(Sinh(x)+a)", "a+1/2*(E^x-1/E^x)");
		check("TrigToExp(Tanh(x))", "(E^x-1/E^x)/(E^x+E^(-x))");
		check("TrigToExp(a+b)", "a+b");
	}

	public void testTrueQ() {
		check("TrueQ(True)", "True");
		check("TrueQ(False)", "False");
		check("TrueQ(x)", "False");
	}

	public void testTuples() {
		check("Tuples({}, 2)", "{}");
		check("Tuples({a, b, c}, 0)", "{{}}");
		check("tuples({{a, b}, {1, 2, 3, 4}})", "{{a,1},{a,2},{a,3},{a,4},{b,1},{b,2},{b,3},{b,4}}");
		check("tuples({{a, b}, {1, 2, 3, 4}, {x}})",
				"{{a,1,x},{a,2,x},{a,3,x},{a,4,x},{b,1,x},{b,2,x},{b,3,x},{b,4,x}}");

		check("tuples({0,1},3)", "{{0,0,0},{0,0,1},{0,1,0},{0,1,1},{1,0,0},{1,0,1},{1,1,0},{1,1,1}}");
		check("tuples({1,0},3)", "{{1,1,1},{1,1,0},{1,0,1},{1,0,0},{0,1,1},{0,1,0},{0,0,1},{0,0,0}}");

		// The head of list need not be 'List':
		check("Tuples(f(a, b, c), 2)", "{f(a,a),f(a,b),f(a,c),f(b,a),f(b,b),f(b,c),f(c,a),f(c,b),f(c,c)}");
		// However, when specifying multiple expressions, 'List' is always used:
		check("Tuples({f(a, b), g(x, y)})", "{{a,x},{a,y},{b,x},{b,y}}");
	}

	public void testUnequal() {
		check("(E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi!=0", "False");
	}

	public void testUnion() {
		check("Union({a,a,b,c})", "{a,b,c}");
		check("Union({9, 0, 0, 3, 2, 3, 6, 2, 9, 8, 4, 9, 0, 2, 6, 5, 7, 4, 9, 8})", "{0,2,3,4,5,6,7,8,9}");
	}

	public void testUnique() {
		check("Unique()", "$1");
		check("Unique(x)", "x$2");
		check("Unique(\"x\")", "x3");
	}

	public void testUnitStep() {
		check("UnitStep(Interval({0,42}))", "Interval({1,1})");
		check("UnitStep(Interval({-3,-1}))", "Interval({0,0})");
		check("UnitStep(Interval({-1,2}))", "Interval({0,1})");
		check("UnitStep(0)", "1");
		check("UnitStep(42)", "1");
		check("UnitStep(-1)", "0");
		check("UnitStep(-42)", "0");
		check("UnitStep({1.6, 1.6000000000000000000000000})", "{1,1}");
		check("UnitStep({-1, 0, 1})", "{0,1,1}");
		check("UnitStep(1, 2, 3)", "1");
	}

	public void testUnitVector() {
		check("UnitVector(2)", "{0,1}");
		check("UnitVector(4,3)", "{0,0,1,0}");
		check("UnitVector(4,4)", "{0,0,0,1}");
		check("UnitVector(4,5)", "UnitVector(4,5)");
	}

	public void testUnset() {
		check("$x=5;$x=.;$x", "$x");
		check("$f(x_):=x^2;$f(x_)=.;$f(3)", "$f(3)");
	}

	public void testUpSet() {
		check("$f($abc(0))^=100;$f($abc(0))", "100");
	}

	public void testUpSetDelayed() {
		check("$f($h(0)) ^= h0;$f($h(x_)) ^:= 2 $f($h(x - 1));$f($h(10))", "1024*h0");
	}

	public void testVariables() {
		check("Variables((x + y)^2 + 3 z^2 - y z + 7)", "{x,y,z}");
		check("Variables((a - b)/(x + y) - 2/z)", "{a,b,x,y,z}");
		check("Variables(Sqrt(x + y - z^2) + (-2 t)^(2/3))", "{t,x,y,z}");

		// TODO: see http://reference.wolfram.com/language/ref/MonomialList.html
		check("Variables(y + x z)", "{x,y,z}");
	}

	public void testVariance() {
		check("Variance({Pi,E,3})//Together", "1/3*(9-3*E+E^2-3*Pi-E*Pi+Pi^2)");
		check("Variance({a,b,c,d})",
				"1/12*(-(-3*a+b+c+d)*Conjugate(a)-(a-3*b+c+d)*Conjugate(b)-(a+b-3*c+d)*Conjugate(c)-(a+b+c\n"
						+ "-3*d)*Conjugate(d))");
		check("Variance({1., 2., 3., 4.})", "1.6666666666666667");
		check("Variance({{5.2, 7}, {5.3, 8}, {5.4, 9}})", "{0.010000000000000018,1.0}");
		check("Variance({1.21, 3.4, 2, 4.66, 1.5, 5.61, 7.22})", "5.16122380952381");

		// check("Variance(BernoulliDistribution(p))", "p*(1-p)");
		// check("Variance(BinomialDistribution(n, p))", "n*p*(1-p)");
	}

	public void testVectorAngle() {
		check("VectorAngle({1,0},{0,1})", "Pi/2");
		check("VectorAngle({1,0,0},{1,1,1})", "ArcCos(1/Sqrt(3))");
		check("VectorAngle({1,0},{1,1})", "Pi/4");

		check("Norm({1,0})", "1");
		check("Norm({1,1})", "Sqrt(2)");
		check("{1,0}.{1,1}", "1");

	}

	public void testWhich() {
		check("$a = 2;which($a == 1, x, $a == 2, b)", "b");
		check("Which(1 < 0, a,  x == 0, b,  0 < 1, c)", "Which(x==0,b,0<1,c)");
		check("$a = 2;which($a == 1, x, $a == 3, b)", "");
		check("$x=-2;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", "-1");
		check("$x=0;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", "Indeterminate");
		check("$x=3;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", "1");
	}

	public void testWhile() {
		check("$n = 1; While($n < 4, Print($n); $n++)", "");
		check("$n = 1; While(++$n < 4); $n", "4");
		check("$n = 1; While($n < 4, $n++); $n", "4");
		check("$n = 1; While(True, If($n > 10, Break()); $n++);$n", "11");
	}

	public void testXor() {

		check("Xor()", "False");
		check("Xor(False)", "False");
		check("Xor(True)", "True");
		check("Xor(f(x))", "f(x)");
		check("Xor(a,a)", "False");
		check("Xor(a,a,a,b)", "Xor(a,b)");
		check("Xor(a,c,a,b)", "Xor(b,c)");
		check("Xor(True, False, False)", "True");
		check("Xor(True, True, True)", "True");
		check("Xor(True, True, True, True)", "False");
		check("Xor(False, False, False, False)", "False");
		check("Xor(True, False, True)", "False");
	}

	public void testYuleDissimilarity() {
		check("YuleDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", "2");
		check("YuleDissimilarity({True, False, True}, {True, True, False})", "2");
		check("YuleDissimilarity({0, 0, 0, 0}, {0, 0, 0, 0})", "0");
		check("YuleDissimilarity({0, 1, 0, 1}, {1, 0, 1, 0})", "2");
	}

	public void testZeta() {
		check("Zeta(s, 0)", "Zeta(s)");
		check("Zeta(s, 1/2)", "(-1+s^2)*Zeta(s)");
		check("Zeta(s, -1)", "1+Zeta(s)");
		check("Zeta(s, 2)", "-1+Zeta(s)");
		check("Zeta(4, -12)", "638942263173398977/590436101122560000+Pi^4/90");
		check("Zeta(11, -12)", "Zeta(11,-12)");
		check("Zeta(-5, -12)", "158938415/252");
		check("Zeta(6)", "Pi^6/945");
		check("Zeta(-11)", "691/32760");
		check("Zeta(-42)", "0");
		check("Zeta(2)", "Pi^2/6");
		check("Zeta(Infinity)", "1");
	}
}
