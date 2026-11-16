package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** <code>FactorInteger</code> of a Gaussian integer which isn't real. */
public class GaussianFactorIntegerTest extends ExprEvaluatorTestCase {

  @Test
  public void testGaussianIntegerArgument() {
    // a non-real Gaussian integer is factored over the Gaussian integers without the option
    check("FactorInteger(5+12*I)", //
        "{{3+I*2,2}}");
    check("FactorInteger(5+12*I) == FactorInteger(5+12*I, GaussianIntegers->True)", //
        "True");
    check("FactorInteger(6*I)", //
        "{{1+I,2},{3,1}}");
    check("f = FactorInteger(123456789+987654321*I); Times @@ Power @@@ f", //
        "123456789+I*987654321");
    // a real integer is still factored over the integers
    check("FactorInteger(10)", //
        "{{2,1},{5,1}}");
    // a Gaussian rational: the primes of the denominator have negative exponents
    check("FactorInteger(1/3+I)", //
        "{{1+I,1},{2+I,1},{3,-1}}");
    check("FactorInteger((2+I)/13)", //
        "{{I,1},{2+I,1},{2+I*3,-1},{3+I*2,-1}}");
    check("f = FactorInteger(7/6+5/4*I); Times @@ Power @@@ f", //
        "7/6+I*5/4");
    check("FactorInteger(1/2, GaussianIntegers->True)", //
        "{{I,1},{1+I,-2}}");
    // a number which isn't real is factored over the Gaussian integers whatever the option says
    check("FactorInteger(10*I, GaussianIntegers->False)", //
        "{{-I,1},{1+I,2},{1+I*2,1},{2+I,1}}");
  }
}
