package org.matheclipse.core.system.steps;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.steps.StepLevel;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class QuadraticSolveTest extends ExprEvaluatorTestCase {

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
  public void testTraceRewrite001() {
    String input = "Integrate(Sin(x)^3,x)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "-Cos(x)+Cos(x)^3/3");
  }

  @Test
  public void testTraceQuarticSolve001() {
    // x^2 +2*x -3
    String input = "QuarticSolve(1,2,-3)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "{1,-3}");
  }

  @Test
  public void testTraceQuarticSolve002() {
    // x^2 - 4*x -3
    String input = "QuarticSolve(1,-4,-3)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "{1/2*(4+2*Sqrt(7)),1/2*(4-2*Sqrt(7))}");
  }

  @Test
  public void testTraceBiQuarticSolve001() {
    // 7*x^2 + 12
    String input = "QuarticSolve(7,0,12)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "{I*2*Sqrt(3/7),-I*2*Sqrt(3/7)}");
  }

  @Test
  public void testTraceBiQuarticSolve002() {
    // 7*x^2 - 12
    String input = "QuarticSolve(7,0,-12)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "{2*Sqrt(3/7),-2*Sqrt(3/7)}");
  }

  @Test
  public void testTraceQuarticSolve004() {
    // x^2 - 5*x -150
    String input = "QuarticSolve(2,5,-150)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "{15/2,-10}");
  }

  @Test
  public void testTraceQuarticSolve005() {
    // x^2 - 2*x -4
    String input = "QuarticSolve(1,2,-4)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "{1/2*(-2+2*Sqrt(5)),1/2*(-2-2*Sqrt(5))}");
  }

  @Test
  public void testTraceQuarticSolve006() {
    // 2*x^2 - 150
    String input = "QuarticSolve(2,0,-150)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "{5*Sqrt(3),-5*Sqrt(3)}");
  }

  @Test
  public void testTraceQuarticSolve007() {
    // 2*x^2 - 5*x
    String input = "QuarticSolve(2,-5,0)";
    checkSteps(input, StepLevel.ARITHMETIC, //
        "{5/2,0}");
  }
}
