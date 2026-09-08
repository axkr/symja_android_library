package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class FunctionContinuousTest extends ExprEvaluatorTestCase {

  // function_continuous_polynomial
  @Test
  public void testFunctionContinuousPolynomial() {
    check("FunctionContinuous(x^2,x)", "True");
    check("FunctionContinuous(x^2+3*x+1,x)", "True");
  }

  // function_continuous_trig
  @Test
  public void testFunctionContinuousTrig() {
    check("FunctionContinuous(Sin(x),x)", "True");
    check("FunctionContinuous(Cos(x),x)", "True");
    check("FunctionContinuous(Tan(x),x)", "False");
  }

  // function_continuous_exp
  @Test
  public void testFunctionContinuousExp() {
    check("FunctionContinuous(Exp(x),x)", "True");
  }

  // function_continuous_discontinuous
  @Test
  public void testFunctionContinuousDiscontinuous() {
    check("FunctionContinuous(1/x,x)", "False");
    check("FunctionContinuous(Floor(x),x)", "False");
    check("FunctionContinuous(Sign(x),x)", "False");
    check("FunctionContinuous(Sqrt(x),x)", "False");
    check("FunctionContinuous(Log(x),x)", "False");
  }

  // function_continuous_abs
  @Test
  public void testFunctionContinuousAbs() {
    check("FunctionContinuous(Abs(x),x)", "True");
  }

  // function_continuous_constant
  @Test
  public void testFunctionContinuousConstant() {
    check("FunctionContinuous(5,x)", "True");
  }

  // function_continuous_composite
  @Test
  public void testFunctionContinuousComposite() {
    check("FunctionContinuous(x^2+Sin(x),x)", "True");
  }

  // function_continuous_restricted_domain
  @Test
  public void testFunctionContinuousRestrictedDomain() {
    check("FunctionContinuous({Log(x),x>0},x)", "True");
    check("FunctionContinuous({Sqrt(x),x>0},x)", "True");
    check("FunctionContinuous({1/x,x>0},x)", "True");
  }

  // function_continuous_multivariate
  @Test
  public void testFunctionContinuousMultivariate() {
    check("FunctionContinuous(x+y,{x,y})", "True");
  }

  // function_continuous_fractional_power
  @Test
  public void testFunctionContinuousFractionalPower() {
    check("FunctionContinuous(x^(1/3),x)", "False");
  }
}
