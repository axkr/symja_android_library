package org.matheclipse.parser.test;

import junit.framework.TestCase;

import org.matheclipse.parser.client.Parser;

/**
 * Tests parser functions for the simple parser style
 */
public class RelaxedParserTestCase extends TestCase {

	public RelaxedParserTestCase(String name) {
		super(name);
	}

	public void testParser0() {
		try { 
			Parser p = new Parser(true);
			Object obj = p.parse("Integrate(Sin(x)^2+3*x^4, x)");
			assertEquals(obj.toString(), "Integrate[Plus[Power[Sin[x], 2], Times[3, Power[x, 4]]], x]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testParser1() {
		try {
			Parser p = new Parser(true);
			Object obj = p.parse("a()(0)(1)f[[x]]");
			assertEquals(obj.toString(), "Times[a[][0][1], Part[f, x]]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testParser2() {
		try {
			Parser p = new Parser(true);
			Object obj = p.parse("a sin()cos()x()y z");
			assertEquals(obj.toString(), "Times[Times[Times[Times[Times[a, sin[]], cos[]], x[]], y], z]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}