package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Definite integrals of <code>If(c, a, b)</code> integrands whose condition depends on the
 * integration variable: they are integrated as <code>Piecewise({{a, c}}, b)</code>, split at the
 * points where the condition switches.
 */
public class IntegrateIfConditionTest extends ExprEvaluatorTestCase {

  @Test
  public void testIfIntegrand() {
    check("Integrate(If(x<1/3, 3*x, 1), {x,0,1})", //
        "5/6");
    // reversed limits
    check("Integrate(If(x<1/3, 3*x, 1), {x,1,0})", //
        "-5/6");
    check("Integrate(If(x>=-1/2, x^2, -x), {x,-2,1})", //
        "9/4");
  }

  @Test
  public void testNestedIfAndProducts() {
    check("Integrate(If(x^2<1/4, If(x>0, 1, 3), 2), {x,-1,1})", //
        "4");
    check("Integrate(If(x<1/3, 2, 1)*Piecewise({{4, x<2/3}}, 1), {x,0,1})", //
        "13/3");
    check("Integrate(x*If(x<1, x, 2-x)*If(x<1/2, 1, 3), {x,0,2})", //
        "35/12");
  }

  @Test
  public void testDerivativeOfIfFunction() {
    check("g = If(#<1, #^3, 3*#-2)&; Integrate(g'(x), {x,0,2})", //
        "4");
  }

  @Test
  public void testSymbolicSwitchingPoint() {
    check("Integrate(If(x<a, 1, 2), {x,0,1})", //
        "Piecewise({{2,a<=0},{1,a>=1}},2-a)");
  }

  @Test
  public void testConditionWithoutComparison() {
    check("Integrate(If(f(x), 1, 2), {x,0,1})", //
        "Integrate(If(f(x),1,2),{x,0,1})");
  }

  @Test
  public void testContinuousAntiderivative() {
    // the antiderivative of every branch but the first gets the constant which makes it continuous
    check("Integrate(If(x<1, 2*x, If(x<2, 2, x)), x)", //
        "Piecewise({{x^2,x<=1},{-1+2*x,1<x<=2}},1+x^2/2)");
    check("Integrate(Piecewise({{4*x^3, x<-1}}, 4), x)", //
        "Piecewise({{x^4,x<=-1}},5+4*x)");
    check("Integrate(x*If(x<1, 1, -1), x)", //
        "Piecewise({{x^2/2,x<=1}},1-x^2/2)");
    check("Integrate(Piecewise({{1/x, x>1}}, 1), x)", //
        "Piecewise({{x,x<=1}},1+Log(x))");
    // a condition which isn't an interval of x is integrated branch by branch
    check("Integrate(Piecewise({{2*x, x^2<1}}, 1), x)", //
        "Piecewise({{x^2,x^2<1}},x)");
    // a symbolic switching point stays unevaluated
    check("Integrate(If(x<a, 1, 2), x)", //
        "Integrate(If(x<a,1,2),x)");
  }
}
