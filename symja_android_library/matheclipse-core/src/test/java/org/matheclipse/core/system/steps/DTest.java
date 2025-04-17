package org.matheclipse.core.system.steps;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class DTest extends ExprEvaluatorTestCase {

  public DTest() {
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
  public void testTraceD001() {
    String input = "D(1/x,x)";
    checkJSON(input, //
        x -> true, "-1/x^2");
  }

  @Test
  public void testTraceD002() {
    String input = "D(Exp(x),x)";
    checkJSON(input, //
        x -> true, "E^x");
  }

  @Test
  public void testTraceD003() {
    String input = "D(Log(x),x)";
    checkJSON(input, //
        x -> true, "1/x");
  }

  @Test
  public void testTraceD004() {
    String input = "D(3*x^2-2,x)";
    checkJSON(input, //
        x -> true, //
        "6*x");
  }

  @Test
  public void testTraceD005() {
    String input = "D(Sin(3*x^3+Log(x)),x)";
    checkJSON(input, //
        x -> true, //
        "(1/x+9*x^2)*Cos(3*x^3+Log(x))");
  }

  @Test
  public void testTraceD006() {
    String input = "D(Sin(g(x)),x)";
    checkJSON(input, //
        x -> true, //
        "Cos(g(x))*g'(x)");
  }

  @Test
  public void testTraceD007() {
    String input = "D(Sin(Cos(x)),x)";
    checkJSON(input, //
        x -> true, //
        "-Cos(Cos(x))*Sin(x)");
  }

  @Test
  public void testTraceD008() {
    String input = "D(4*x,x)";
    checkJSON(input, //
        x -> true, "4");
  }

  @Test
  public void testTraceD009() {
    String input = "D(4*x^3,x)";
    checkJSON(input, //
        x -> true, "12*x^2");
  }

  @Test
  public void testTraceD010() {
    String input = "D(3*x+7,x)";
    checkJSON(input, //
        x -> true, "3");
  }

  @Test
  public void testTraceD011() {
    String input = "D(x/(x+1),x)";
    checkJSON(input, //
        x -> true, //
        "-x/(1+x)^2+1/(1+x)");
  }

  @Test
  public void testTraceD012() {
    String input = "D((3*x+9)/(2-x),x)";
    checkJSON(input, //
        x -> true, //
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
