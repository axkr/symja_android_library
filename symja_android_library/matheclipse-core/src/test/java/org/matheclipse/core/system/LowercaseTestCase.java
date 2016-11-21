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
		check("Abs(0)", "0");
		check("Abs(10/3)", "10/3");
		check("Abs(-10/3)", "10/3");
		check("Abs(Indeterminate)", "Indeterminate");
		check("Abs(Infinity)", "Infinity");
		check("Abs(-1*Infinity)", "Infinity");
		check("Abs(ComplexInfinity)", "Infinity");
		check("Abs(I*Infinity)", "Infinity");

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
		check("Arg(Indeterminate)", "Indeterminate");
		check("Arg(0)", "0");
		check("Arg(10/3)", "0");
		check("Arg(-10/3)", "Pi");
		check("Arg(I*Infinity)", "Pi/2");
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
				"1+4*a+x*(4+12*a+12*a^2+4*a^3)+6*a^2+4*a^3+a^4+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4");
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
		check("Conjugate(E^z)", "E^Conjugate(z)");
		check("Conjugate(Pi)", "Pi");
		check("Conjugate(0)", "0");
		check("Conjugate(I)", "-I");
		check("Conjugate(Indeterminate)", "Indeterminate");
		check("Conjugate(Infinity)", "Infinity");
		check("Conjugate(-Infinity)", "-Infinity");
		check("Conjugate(ComplexInfinity)", "ComplexInfinity");
		check("Conjugate(Transpose({{1,2+I,3},{4,5-I,6},{7,8,9}}))", "{{1,4,7},{2-I,5+I,8},{3,6,9}}");
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
		check("Derivative(1)[ArcCoth]", "1/(1-#1^2)&");
		check("y''", "Derivative(2)[y]");
		check("y''(x)", "y''(x)");
		check("y''''(x)", "Derivative(4)[y][x]");

		check("x*x^a", "x^(1+a)");
		check("x/x^(1-x)", "x^x");
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

	public void testDSolve() {
		check("DSolve(D(f(x, y), x) == D(f(x, y), y), f, {x, y})", "DSolve(Derivative(1,0)[f][x,y]==0,f,{x,y})");
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
		check("DSolve(y'(x) + 2*y(x)/(1-x^2) == 0, y(x), x)", "{{y(x)->C(1)/E^(2*(-Log(1-x)/2+Log(2+2*x)/2))}}");
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
		check("Eigenvalues({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", "{1.0,1.0,1.0}");
	}

	public void testEigenvectors() {
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
	}

	public void testExpand() {
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
		check("FindInstance(Abs((-3+x^2)/x) ==2,{x})", "{{x->-3}}");
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
		String pathToVectorAnalysis;
		pathToVectorAnalysis = getClass().getResource("/VectorAnalysis.m").toString();
		// remove 'file:/'
		pathToVectorAnalysis = pathToVectorAnalysis.substring(6);
		System.out.println(pathToVectorAnalysis);
		check("Get(\"" + pathToVectorAnalysis + "\")", "");
		check("DotProduct({a,b,c},{d,e,f}, Spherical)",
				"a*d*Cos(b)*Cos(e)+a*d*Cos(c)*Cos(f)*Sin(b)*Sin(e)+a*d*Sin(b)*Sin(c)*Sin(e)*Sin(f)");
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
		check("Haversine({0, Pi/4, Pi/3, Pi/2})", "{0,Abs(2-Sqrt(2))/4,1/4,1/2}");
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
		check("InterpolatingFunction({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}})[2.5]", "3.7171052631578947");
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
		check("D(Log(a, x),x)", "1/(x*Log(a))");
		check("Log(1000.)", "6.907755278982137");
		check("Log(2.5 + I)", "0.9905007344332918+I*0.3805063771123649");
		check("Log({2.1, 3.1, 4.1})", "{0.7419373447293773,1.1314021114911006,1.410986973710262}");
		check("Log(2, 16)", "4");
		check("Log(10, 1000)", "3");
		check("Log(10, 10)", "1");
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
				"{Interval,IntegerExponent,IntegerPart,IntegerPartitions,IntegerQ,Integrate,InterpolatingFunction,InterpolatingPolynomial,Intersection}");
		check("Names(\"Integer*\" )", "{IntegerExponent,IntegerPart,IntegerPartitions,IntegerQ}");
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
		check("ProductLog(-1.5)", "-3.2783735915572e-2+I*1.549643823350159");
		check("ProductLog({0.2, 0.5, 0.8})", "{1.689159734991096e-1,3.517337112491959e-1,4.900678588015799e-1}");
		check("ProductLog(2.5 + 2*I)", "1.056167968948635+I*3.5256052020787e-1");
		check("N(ProductLog(4/10),50)", "2.9716775067313854677972696224702134190445810155014e-1");

		check("N(ProductLog(-1),20)", "-3.181315052047641353e-1+I*1.3372357014306894089");

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
		check("Put(x + y, \"c:/temp/example_file1.m\"); Get(\"c:/temp/example_file1.m\")", "x+y");
		check("Put(x + y, 2x^2 + 4z!, Cos(x) + I Sin(x), \"c:/temp/example_file2.m\");"
				+ "Get(\"c:/temp/example_file2.m\")", "I*Sin(x)+Cos(x)");
		check("Put(47!, \"c:/temp/test.m\"); Get(\"c:/temp/test.m\")",
				"258623241511168180642964355153611979969197632389120000000000");
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

	// public void testNormal() {
	// check("Normal(Series(Exp(x),{x,0,5}))",
	// "1+x+x^2/2+x^3/6+x^4/24+x^5/120");
	// check("Normal(SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1,
	// 11, 2))",
	// "Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880");
	//
	// }
	//
	// public void testSeries() {
	// // check("FullForm(Series(Exp(x),{x,0,10}))", "");
	// // check("Series(Sin(Sqrt(x)), {x, 0, 5})", "");
	// check("Series(f(x),{x,0,3})",
	// "f(0)+f'(0)*x+f''(0)/2*x^2+Derivative(3)[f][0]/6*x^3+O(x)^4");
	// check("Series(Exp(x),{x,0,2})", "1+x+x^2/2+O(x)^3");
	// check("Series(Exp(f(x)),{x,0,2})",
	// "E^f(0)+E^f(0)*f'(0)*x+1/2*(E^f(0)*f'(0)^2+E^f(0)*f''(0))*x^2+O(x)^3");
	// check("Series(Exp(x),{x,0,5})", "1+x+x^2/2+x^3/6+x^4/24+x^5/120+O(x)^6");
	// check("Series(100,{x,a,5})", "100");
	// }
	//
	// public void testSeriesData() {
	// check("SeriesData(100, 0, Table(i^2, {i, 10}), 0, 10, 1)",
	// "Indeterminate");
	// check("SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1)",
	// "1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9+O(x)^10");
	// check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11,
	// 2)",
	// "Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)");
	// }

	public void testSign() {
		check("Pi>E", "True");
		check("Pi<E", "False");
		check("Sign(1+I)", "(1+I)/Sqrt(2)");
		check("Sign(1.0+I)", "0.7071067811865475+I*0.7071067811865475");
		check("Sign(E - Pi)", "-1");
		check("Sign(0)", "0");
		check("Sign(I)", "I");
		check("Sign(Indeterminate)", "Indeterminate");
		check("Sign(Infinity)", "1");
		check("Sign(-Infinity)", "-1");
		check("Sign(DirectedInfinity(1+I*3))", "(1+I*3)/Sqrt(10)");
		check("Sign(ComplexInfinity)", "Indeterminate");
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
		check("Solve(Abs((-3+x^2)/x) ==2,{x})", "{{x->-3},{x->-1},{x->1},{x->3}}");
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

	public void testTrigExpand() {
		check("trigexpand(Sin(x)*Tan(x))", "Sin(x)*Tan(x)");
		check("trigexpand(Sin(x + y))", "Cos(y)*Sin(x)+Cos(x)*Sin(y)");
		check("trigexpand(Cos(x + y))", "Cos(x)*Cos(y)-Sin(x)*Sin(y)");
		check("trigexpand(Sin(x + y + z))",
				"Cos(y)*Cos(z)*Sin(x)+Cos(x)*Cos(z)*Sin(y)+Cos(x)*Cos(y)*Sin(z)-Sin(x)*Sin(y)*Sin(z)");
		check("trigexpand(Sin(2 x))", "2*Cos(x)*Sin(x)");
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
		check("UnitStep(42)", "1");
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
		check("Zeta(2)", "Pi^2/6");
	}
}
