package org.matheclipse.core.rubi;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * General test suite. Starts all other tests.
 */
public class RubiTests extends TestCase {

	public RubiTests(String name) {
		super(name);
	}

	/**
	 * A unit test suite
	 * 
	 * @return The test suite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(Exponentials.class);
		suite.addTestSuite(HyperbolicFunctions.class);
		suite.addTestSuite(IndependentTestSuites.class);
		suite.addTestSuite(InverseHyperbolicFunctions.class);
		suite.addTestSuite(InverseTrigFunctions.class);
		suite.addTestSuite(Logarithms.class);
		suite.addTestSuite(SpecialFunctions.class);
		suite.addTestSuite(TrigFunctions.class);
		suite.addTestSuite(AlgebraicFunctions.class);
		// suite.addTestSuite(AlgebraicFunctions2.class);
		return suite;

	}

	/**
	 * Run all tests in a swing GUI
	 * 
	 * @param args
	 *            Description of Parameter
	 */
	// public static void main(String args[]) {
	// junit.swingui.TestRunner.run(CompleteTestSuite.class);
	// }
}
