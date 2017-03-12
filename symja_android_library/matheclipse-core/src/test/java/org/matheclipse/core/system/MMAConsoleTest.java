package org.matheclipse.core.system;

import org.matheclipse.core.eval.MMAConsole;

import junit.framework.TestCase;

public class MMAConsoleTest extends TestCase {
	MMAConsole console;

	public void testInput() {
		assertEquals("f[x]", console.interpreter("f[x]"));
	}

	/**
	 * The JUnit setup method
	 */
	protected void setUp() {
		try {
			console = new MMAConsole();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
