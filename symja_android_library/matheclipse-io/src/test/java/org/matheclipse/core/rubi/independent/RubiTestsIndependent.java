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

    suite.addTestSuite(HebischProblemsTests.class);
    suite.addTestSuite(CharlwoodProblemsTests.class);
    suite.addTestSuite(TimofeevProblemsTests.class);
    suite.addTestSuite(HearnProblemsTests.class);
    suite.addTestSuite(JeffreyProblemsTests.class);
    suite.addTestSuite(MosesProblemsTests.class);
    suite.addTestSuite(StewartProblemsTests.class);
    suite.addTestSuite(WelzProblemsTests.class);
    suite.addTestSuite(ApostolProblemsTests.class);
    suite.addTestSuite(BondarenkoProblemsTests.class);
    suite.addTestSuite(BronsteinProblemsTests.class);
    suite.addTestSuite(WesterProblemsTests.class);
    return suite;
  }
}
