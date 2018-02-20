package org.matheclipse.core.system;

import org.matheclipse.core.eval.MMAConsole;

import junit.framework.TestCase;

public class MMAConsoleTest extends TestCase {
	MMAConsole console;

	public void testInput() {
		assertEquals("f[x]", console.interpreter("f[x]"));
		assertEquals("x/2-1/2*Cos[x]*Sin[x]", console.interpreter("Integrate[Sin[x]^2,x]"));
	}

	/**
	 * The JUnit setup method
	 */
	@Override
	protected void setUp() {
		try {
			console = new MMAConsole();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
