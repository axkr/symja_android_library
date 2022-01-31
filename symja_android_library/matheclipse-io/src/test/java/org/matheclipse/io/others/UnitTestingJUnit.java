package org.matheclipse.io.others;

import org.matheclipse.core.basic.Config;
import org.matheclipse.io.system.AbstractTestCase;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based
 * integrator</a>.
 */
public class UnitTestingJUnit extends AbstractTestCase {
  public UnitTestingJUnit(String name) {
    super(name);
  }

  public void testVerificationTest() {
    check("VerificationTest(3! < 3^3)", //
        "TestResultObject(Outcome->Success,TestID->None)");
    check("VerificationTest(3^3, 27)", //
        "TestResultObject(Outcome->Success,TestID->None)");
    check("VerificationTest(RandomInteger({1, 10}), _Integer, SameTest -> MatchQ)", //
        "TestResultObject(Outcome->Success,TestID->None)");
    check("VerificationTest(2*2, 3)", //
        "TestResultObject(Outcome->Failure,ExpectedOutput->3,ActualOutput->4,TestID->None)");
    check("VerificationTest(Sin(E) > Cos(Pi))", //
        "TestResultObject(Outcome->Success,TestID->None)");
  }

  public void testTestReport() {
    check(
        "TestReport({VerificationTest(Sin(E) > Cos(E)), VerificationTest(MatrixQ({{1, 2, 3}, {4, 5, 6, 7}})), VerificationTest(1/0, ComplexInfinity)})", //
        "TestReportObject(TestResults-><|1->TestResultObject(Outcome->Success,TestID->None),2->TestResultObject(Outcome->Failure,ExpectedOutput->True,ActualOutput->False,TestID->None),\n"
            + //
            "3->TestResultObject(Outcome->Success,TestID->None)|>)");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.FILESYSTEM_ENABLED = true;
  }
}
