package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Factor of polynomials which contain cyclotomic factors but are not binomials. These split off
 * the cyclotomic factors before the modular recombination, which is exponential in the many
 * modular factors of a cyclotomic polynomial.
 */
public class FactorCyclotomicProductTest extends ExprEvaluatorTestCase {

  @Test
  public void testCyclotomicTimesLinear() {
    check("Factor(Expand((x^12-1)*(x+2)))", //
        "(-1+x)*(1+x)*(2+x)*(1+x^2)*(1-x+x^2)*(1+x+x^2)*(1-x^2+x^4)");
  }

  @Test
  public void testHighDegree() {
    // took about 17 seconds before the split
    check("Length(FactorList(Expand((x^96-1)*(x+2))))", //
        "14");
    check("FactorList(Expand((x^9+1)*(x^2+3)))", //
        "{{1,1},{1+x,1},{3+x^2,1},{1-x+x^2,1},{1-x^3+x^6,1}}");
  }
}
