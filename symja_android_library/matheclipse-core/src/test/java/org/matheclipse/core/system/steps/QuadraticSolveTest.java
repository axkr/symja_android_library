package org.matheclipse.core.system.steps;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class QuadraticSolveTest extends ExprEvaluatorTestCase {

  public QuadraticSolveTest() {
    super();
  }

  @Override
  @Before
  public void setUp() {
    super.setUp();
    ToggleFeature.SHOW_STEPS = true;
    Config.TRACE_REWRITE_RULE = true;
    Config.USER_STEPS_PARSER = true;
  }


  @AfterClass
  public static void afterAll() {
    ToggleFeature.SHOW_STEPS = false;
    Config.TRACE_REWRITE_RULE = false;
    Config.USER_STEPS_PARSER = false;
  }

  @Test
  public void testTraceRewrite001() {
    String input = "Integrate(Sin(x)^3,x)";
    checkJSON(input, null, //
        "-Cos(x)+Cos(x)^3/3");
  }

  @Test
  public void testTraceQuarticSolve001() {
    // x^2 +2*x -3
    String input = "QuarticSolve(1,2,-3)";
    checkJSON(input, x -> x.equals(S.QuarticSolve), //
        "{1,-3}");
  }

  @Test
  public void testTraceQuarticSolve002() {
    // x^2 - 4*x -3
    String input = "QuarticSolve(1,-4,-3)";
    checkJSON(input, x -> x.equals(S.QuarticSolve), //
        "{1/2*(4+2*Sqrt(7)),1/2*(4-2*Sqrt(7))}");
  }

  @Test
  public void testTraceBiQuarticSolve001() {
    // 7*x^2 + 12
    String input = "QuarticSolve(7,0,12)";
    checkJSON(input, x -> true, //
        "{I*2*Sqrt(3/7),-I*2*Sqrt(3/7)}");
  }

  @Test
  public void testTraceBiQuarticSolve002() {
    // 7*x^2 - 12
    String input = "QuarticSolve(7,0,-12)";
    checkJSON(input, x -> true, //
        "{2*Sqrt(3/7),-2*Sqrt(3/7)}");
  }

  @Test
  public void testTraceQuarticSolve004() {
    // x^2 - 5*x -150
    String input = "QuarticSolve(2,5,-150)";
    checkJSON(input, x -> x.equals(S.QuarticSolve), //
        "{15/2,-10}");
  }

  @Test
  public void testTraceQuarticSolve005() {
    // x^2 - 2*x -4
    String input = "QuarticSolve(1,2,-4)";
    checkJSON(input, x -> x.equals(S.QuarticSolve), //
        "{1/2*(-2+2*Sqrt(5)),1/2*(-2-2*Sqrt(5))}");
  }

  @Test
  public void testTraceQuarticSolve006() {
    // 2*x^2 - 150
    String input = "QuarticSolve(2,0,-150)";
    checkJSON(input, x -> x.equals(S.QuarticSolve), //
        "{5*Sqrt(3),-5*Sqrt(3)}");
  }

  @Test
  public void testTraceQuarticSolve007() {
    // 2*x^2 - 5*x
    String input = "QuarticSolve(2,-5,0)";
    checkJSON(input, x -> x.equals(S.QuarticSolve), //
        "{5/2,0}");
  }
}
