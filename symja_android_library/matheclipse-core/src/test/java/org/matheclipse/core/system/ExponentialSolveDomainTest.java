package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Exponential equations with orderings of the variable and over the rationals, Check with several
 * message names, RootSum with machine coefficients and the divergence of an infinite series.
 */
public class ExponentialSolveDomainTest extends ExprEvaluatorTestCase {

  @Test
  public void testOrderingsMakeTheVariableReal() {
    check("{Solve(5^x == 125 && x > 1, x), Solve(5^x == 125 && x > 4, x)}", //
        "{{{x->3}},{}}");
    // the families 2*I*Pi*C(1) and Log(3)+2*I*Pi*C(1) of real members 0 and Log(3)
    check("Solve(E^(2*x) - 4*E^x + 3 == 0 && x >= 0, x)", //
        "{{x->0},{x->Log(3)}}");
    check("Solve(3^x == 81 && 0 < x < 7, x)", //
        "{{x->4}}");
    // a periodic family with a real period keeps its members in the interval
    check("Solve(Sin(x) == 0 && x > 1, x)", //
        "{{x->ConditionalExpression(2*Pi*C(1),C(1)∈Integers&&C(1)>1/(2*Pi))},{x->ConditionalExpression(Pi+\n"
            + "2*Pi*C(1),C(1)∈Integers&&C(1)>(1-Pi)/(2*Pi))}}");
  }

  @Test
  public void testIntegerSolutionsOfAnExponentialEquation() {
    check("Solve(27^x == 9, x, Integers)", //
        "{}");
    check("Solve(9^x - 12*3^x + 27 == 0, x, Integers)", //
        "{{x->1},{x->2}}");
    // the real solutions -2*ProductLog(...)/Log(3) aren't proven, the integer 3 isn't missed
    check("Solve(3^x == x^3, x, Integers)", //
        "Solve(3^x==x^3,x,Integers)");
  }

  @Test
  public void testOrderingOfAnAbsoluteValue() {
    // |x| < 10 doesn't make x real: Log(3)-2*I*Pi and Log(3)+2*I*Pi are solutions too, and
    // the real member Log(3) alone would be a wrong answer
    check("Solve(E^x == 3 && Abs(x) < 10, x)", //
        "Solve(E^x==3&&Abs(x)<10,x)");
  }

  @Test
  public void testNegativeBaseOverTheReals() {
    check("Reduce((-7)^x == 49, x, Reals)", //
        "x==2");
    check("Reduce((-5)^x == -125, x, Reals)", //
        "x==3");
  }

  @Test
  public void testPolynomialGCDWithGaussianAndRadicalCoefficients() {
    check("PolynomialGCD(x^2 + (2 + I)*x + 2*I, x^2 + (I - Sqrt(5))*x - I*Sqrt(5))", //
        "I+x");
  }

  @Test
  public void testRationalSolutionOfAnExponentialEquation() {
    // the real member Log(8)/Log(16) of the family is 3/4
    check("Solve(16^x == 8, x, Rationals)", //
        "{{x->3/4}}");
    check("Solve(2^20 + 2^20 == 4^x, x)", //
        "{{x->ConditionalExpression(21/2+(I*2*Pi*C(1))/Log(4),C(1)∈Integers)}}");
  }

  @Test
  public void testCheckWithSeveralMessageNames() {
    check("Check(1/0, failed, Power::indet, Power::infy)", //
        "failed");
    check("Check(1/0, failed, Power::indet, General::stop)", //
        "ComplexInfinity");
  }

  @Test
  public void testRootSumWithMachineCoefficients() {
    check("Chop(RootSum(#^3 - 1.5*# + 0.5 &, Exp) - N(RootSum(#^3 - 3/2*# + 1/2 &, Exp)))", //
        "0");
    // a rational summand is a sum of powers of the roots
    check("RootSum(#^3 - 2.5*# + 1.5 &, #^2 &)", //
        "5.0");
  }

  @Test
  public void testDivergentSeries() {
    // the ratio 4/E > 1
    check("Check(Sum(k!*4^k/k^k, {k, 1, Infinity}), failed)", //
        "failed");
    check("Quiet(Check(Sum(k!*5^k/k^k, {k, 1, Infinity}), failed))", //
        "failed");
    check("Check(Sum(k!*2^k/k^k, {k, 1, Infinity}), failed)", //
        "Sum((2^k*k!)/k^k,{k,1,Infinity})");
  }
}
