package org.matheclipse.core.system;

import org.matheclipse.core.form.mathml.MathMLPresentationTestCase;
import org.matheclipse.core.form.tex.BasicTeXTestCase;
import org.matheclipse.parser.test.BracketBalancerTestCase;
import org.matheclipse.parser.test.ParserTestCase;
import org.matheclipse.parser.test.RelaxedParserTestCase;
import org.matheclipse.parser.test.eval.EvalDoubleTestCase;

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
	 * @return The test suite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(MainTestCase.class);
		suite.addTestSuite(BasicPatternPropertiesTestCase.class);
		suite.addTestSuite(CompareToTestCase.class);
		suite.addTestSuite(Java8TestCase.class);
		suite.addTestSuite(PatternMatchingTestCase.class);
		suite.addTestSuite(RubiIntegrationTest.class);
		suite.addTestSuite(CombinatoricTestCase.class);

		suite.addTestSuite(MathMLPresentationTestCase.class);
		suite.addTestSuite(BasicTeXTestCase.class);

		suite.addTestSuite(LastCalculationsHistoryTest.class);
		suite.addTestSuite(ExpandTestCase.class);

		suite.addTestSuite(OpenFixedSizeMapTest.class);
		suite.addTestSuite(NumberTest.class);
		suite.addTestSuite(JavaFormTestCase.class);

		suite.addTestSuite(LowercaseTestCase.class);
		suite.addTestSuite(SerializableTest.class);
		suite.addTestSuite(PatternsTest.class);
		
		suite.addTestSuite(ParserTestCase.class);
		suite.addTestSuite(RelaxedParserTestCase.class);
		suite.addTestSuite(BracketBalancerTestCase.class);
		suite.addTestSuite(EvalDoubleTestCase.class);
		suite.addTestSuite(ExprEvaluatorTest.class);
		
		suite.addTestSuite(NumberTheoryTestCase.class);
		suite.addTestSuite(ASCIIPrintTest.class);
		suite.addTestSuite(SVGGraphicsTestCase.class);
		suite.addTestSuite(Graphics3DTestCase.class);
		suite.addTestSuite(SymjifyTestCase.class);
		
		suite.addTestSuite(ConsoleTestCase.class);
		
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
