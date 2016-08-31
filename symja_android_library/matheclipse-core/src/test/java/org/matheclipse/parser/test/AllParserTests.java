package org.matheclipse.parser.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllParserTests extends TestCase {
	public AllParserTests(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite s = new TestSuite();

		s.addTestSuite(ParserTestCase.class);
		s.addTestSuite(RelaxedParserTestCase.class);
		return s;
	}

}
