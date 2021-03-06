package org.matheclipse.core.rubi.independent;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RubiTestsIndependent {

  /**
   * A unit test suite for indepent integral test suites.
   *
   * @return The test suite
   */
  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTestSuite(CharlwoodProblemsTests.class);
    suite.addTestSuite(TimofeevProblemsTests.class);
    return suite;
  }
}
