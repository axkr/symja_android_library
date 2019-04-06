package org.matheclipse.core.rubi.step02;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RubiTests02 {

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
		return suite;

	}
}
