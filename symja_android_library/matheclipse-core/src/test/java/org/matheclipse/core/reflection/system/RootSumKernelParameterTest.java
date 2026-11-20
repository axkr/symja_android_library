package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * A rational summand of <code>RootSum</code> whose parameter is a kernel like <code>Sin(y)</code>
 * or <code>E^x</code> rather than a symbol. Its sum is a rational function of the kernel, found as
 * fast as with a symbol.
 */
public class RootSumKernelParameterTest extends ExprEvaluatorTestCase {

  @Test
  public void testSumOverRoots() {
    // the sum of s/(s-r) over the roots r of p is s*p'(s)/p(s)
    check("Together(RootSum(2+#+#^3&, Sin(y)/(Sin(y)-#)&)-Sin(y)*(1+3*Sin(y)^2)/(2+Sin(y)+Sin(y)^3))", //
        "0");
    check("Together(RootSum(5-#+#^3&, E^(2*x)/(E^(2*x)-#)&)-E^(2*x)*(3*E^(4*x)-1)/(5-E^(2*x)+E^(6*x)))", //
        "0");
  }

  @Test
  public void testKernelHeldInTheFunction() {
    // the function holds Exp(x), its evaluated polynomial has E^x
    check("Together(RootSum(#^3+Exp(t)*#+1&, Exp(t)/(Exp(t)-#)&))", //
        "(E^(2*t)+3*E^(3*t))/(1+E^(2*t)+E^(3*t))");
  }

  @Test
  public void testDerivative() {
    // the sum of 1/(p'(r)*(c-r)) over the roots r of p is 1/p(c)
    check("D(RootSum(3+#+#^3&, Log(Cos(x)-#)/(1+3*#^2)&),x)", //
        "-Sin(x)/(3+Cos(x)+Cos(x)^3)");
    check("D(RootSum(4+#^2+#^3&, Log(E^x-#)/(2*#+3*#^2)&),x)", //
        "E^x/(4+E^(2*x)+E^(3*x))");
  }
}
