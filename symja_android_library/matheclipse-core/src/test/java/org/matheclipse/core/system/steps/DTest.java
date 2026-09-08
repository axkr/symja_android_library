package org.matheclipse.core.system.steps;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.steps.StepLevel;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class DTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
    // show the steps on the expressions the user actually typed, not on their normal form
    Config.USER_STEPS_PARSER = true;
  }

  @AfterAll
  public static void afterAll() {
    // a global parser setting: put it back for the tests which run after this class
    Config.USER_STEPS_PARSER = false;
  }

  @Test
  public void testTraceD001() {
    String input = "D(1/x,x)";
    checkSteps(input, StepLevel.RULE, //
        "-1/x^2");
  }

  @Test
  public void testTraceD002() {
    String input = "D(Exp(x),x)";
    checkSteps(input, StepLevel.RULE, //
        "E^x");
  }

  @Test
  public void testTraceD003() {
    String input = "D(Log(x),x)";
    checkSteps(input, StepLevel.RULE, //
        "1/x");
  }

  @Test
  public void testTraceD004() {
    String input = "D(3*x^2-2,x)";
    checkSteps(input, StepLevel.RULE, //
        "6*x");
  }

  @Test
  public void testTraceD005() {
    String input = "D(Sin(3*x^3+Log(x)),x)";
    checkSteps(input, StepLevel.RULE, //
        "(1/x+9*x^2)*Cos(3*x^3+Log(x))");
  }

  @Test
  public void testTraceD006() {
    String input = "D(Sin(g(x)),x)";
    checkSteps(input, StepLevel.RULE, //
        "Cos(g(x))*g'(x)");
  }

  @Test
  public void testTraceD007() {
    String input = "D(Sin(Cos(x)),x)";
    checkSteps(input, StepLevel.RULE, //
        "-Cos(Cos(x))*Sin(x)");
  }

  @Test
  public void testTraceD008() {
    String input = "D(4*x,x)";
    checkSteps(input, StepLevel.RULE, //
        "4");
  }

  @Test
  public void testTraceD009() {
    String input = "D(4*x^3,x)";
    checkSteps(input, StepLevel.RULE, //
        "12*x^2");
  }

  @Test
  public void testTraceD010() {
    String input = "D(3*x+7,x)";
    checkSteps(input, StepLevel.RULE, //
        "3");
  }

  @Test
  public void testTraceD011() {
    String input = "D(x/(x+1),x)";
    checkSteps(input, StepLevel.RULE, //
        "-x/(1+x)^2+1/(1+x)");
  }

  @Test
  public void testTraceD012() {
    String input = "D((3*x+9)/(2-x),x)";
    checkSteps(input, StepLevel.RULE, //
        "3/(2-x)+(9+3*x)/(2-x)^2");
  }

  // @Test
  // public void testTraceD013() {
  // String input = "D(h(x)+q(x),x)";
  // checkStepsJSON(input,//
  // x -> true,//
  // "h'(x)+q'(x)",
  // "{{Evaluate{Evaluate{D::PlusRule}}}}");
  // }

}
