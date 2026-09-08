package org.matheclipse.core.system.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * The steps of an integration which the Rubi rules never see.
 *
 * <p>
 * Symja answers the easy integrals itself, before the rule set is asked: the linearity of the
 * integral, a constant factor moved out of it, the power rule, and a cascade of algorithms for
 * whole classes of integrand. Those used to leave a derivation with nothing in it.
 */
public class IntegrateStepsTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
  }

  private void assertStep(String input, String expectedSentence) {
    String steps = evalString(input);
    assertTrue(steps.contains(expectedSentence), steps);
  }

  @Test
  public void testPowerRule() {
    assertStep("TraceForm(Integrate(x^2,x))", "Apply the power rule");
    check("TraceForm(Integrate(x^2,x))[[2,1,3,2]]", //
        "PowerRule");
  }

  @Test
  public void testPowerRuleNamesTheExponent() {
    check("TraceForm(Integrate(x^7,x))[[2,1,3,4]]", //
        "7");
  }

  @Test
  public void testReciprocalRule() {
    assertStep("TraceForm(Integrate(1/x,x))", "The integral of");
    check("TraceForm(Integrate(1/x,x))[[2,1,3,2]]", //
        "ReciprocalRule");
  }

  @Test
  public void testExponentialRule() {
    check("TraceForm(Integrate(2^x,x))[[2,1,3,2]]", //
        "ExponentialRule");
  }

  @Test
  public void testConstantFactorIsMovedOut() {
    assertStep("TraceForm(Integrate(5*Sin(x),x))", "Move the constant factor 5 out of the integral");
    check("TraceForm(Integrate(5*Sin(x),x))[[2,1,3,2]]", //
        "ConstantFactor");
  }

  @Test
  public void testTheRulesForTheRestNestUnderTheConstantFactor() {
    // the factor is moved out first and the integral it leaves is what the rules then work on
    check("TraceForm(Integrate(5*Sin(x),x))[[2,1,4,1,3,1]]", //
        "Integrate");
    check("TraceForm(Integrate(5*Sin(x),x))[[2,1,4,1,3,2]]", //
        "RubiRule");
  }

  @Test
  public void testLinearityOfARuleSet() {
    // a sum of transcendental terms is split by the rule set's own linearity rule, 2009
    check("TraceForm(Integrate(Sin(x)+Cos(x),x), Infinity)[[2,1,3,3]]", //
        "2009");
  }

  @Test
  public void testAnAlgorithmStageNamesItself() {
    // a rational function is answered in one go, so the method is what can be said about it
    assertStep("TraceForm(Integrate(3*x^2+2*x,x))",
        "Integrate by the rational function algorithm");
    check("TraceForm(Integrate(1/(1+x^2),x))[[2,1,3,2]]", //
        "Method");
  }

  @Test
  public void testEveryIntegralNowShowsSomething() {
    for (String integrand : new String[] {"x^2", "1/x", "3*x^2+2*x", "5*Sin(x)", "1/(1+x^2)",
        "Sin(x)^3", "x*Exp(x)", "Log(x)"}) {
      String length =
          evalString("Length(TraceForm(Integrate(" + integrand + ",x), Infinity)[[2]])");
      assertTrue(!"0".equals(length.trim()),
          "no steps at all for the integral of " + integrand);
    }
  }

  // ---------------------------------------------------------------- nothing changes when off

  @Test
  public void testTheResultsAreUnchanged() {
    // the steps must be an observation of the evaluation, never a different evaluation
    check("Integrate(x^2,x)", "x^3/3");
    check("Integrate(1/x,x)", "Log(x)");
    check("Integrate(5*Sin(x),x)", "-5*Cos(x)");
    check("Integrate(3*x^2+2*x,x)", "x^2+x^3");
    check("Integrate(2^x,x)", "2^x/Log(2)");
    check("TraceForm(Integrate(x^2,x))[[1]]", "x^3/3");
    check("TraceForm(Integrate(5*Sin(x),x))[[1]]", "-5*Cos(x)");
  }
}
