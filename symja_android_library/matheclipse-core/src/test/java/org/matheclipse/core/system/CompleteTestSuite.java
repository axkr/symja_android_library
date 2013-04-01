package org.matheclipse.core.system;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * General test suite. Starts all other tests.
 */
public class CompleteTestSuite extends TestCase {

	public CompleteTestSuite(String name) {
		super(name);
	}

	/**
	 * A unit test suite
	 * 
	 *@return The test suite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(PatternMatchingTestCase.class);
		suite.addTestSuite(CombinatoricTestCase.class);
		suite.addTestSuite(HeadlistTestCase.class);
		suite.addTestSuite(FindRootScriptTestCase.class);
		suite.addTestSuite(ScriptEngineTestCase.class);
		suite.addTestSuite(SystemTestCase.class);
		suite.addTestSuite(SerializableTest.class);
		return suite;

	}

	/**
	 * Run all tests in a swing GUI
	 * 
	 *@param args
	 *          Description of Parameter
	 */
	// public static void main(String args[]) {
	// junit.swingui.TestRunner.run(CompleteTestSuite.class);
	// }
}
