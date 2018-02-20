package org.matheclipse.parser.test;

import org.matheclipse.parser.client.Scanner;

import junit.framework.TestCase;

/**
 * Tests parser function for SimpleParserFactory
 */
public class BracketBalancerTestCase extends TestCase {

	public BracketBalancerTestCase(String name) {
		super(name);
	}

	public void testBracketBalancer1() {
		String result = Scanner.balanceCode("int(f(cos(x),x");
		assertEquals(result, "))");
	}

	public void testBracketBalancer2() {
		String result = Scanner.balanceCode("int(sin(cos(x)),x)");
		assertEquals(result, "");
	}

	public void testBracketBalancer3() {
		String result = Scanner.balanceCode("int(f[[2,g(x,y[[z]],{1,2,3");
		assertEquals(result, "})]])");
	}

	public void testBracketBalancer4() {
		String result = Scanner.balanceCode("int(f[[2,g(x,y[[z)){1,2,3");
		assertEquals(result, null);
	}
}