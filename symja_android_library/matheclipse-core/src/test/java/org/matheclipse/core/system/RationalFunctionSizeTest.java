package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * JUnit Jupiter tests for {@code Together}, {@code Cancel} and {@code Apart} on many summands, long
 * products of factors and high degrees, where the polynomial arithmetic runs over the rationals
 * instead of the evaluator.
 */
public class RationalFunctionSizeTest extends ExprEvaluatorTestCase {

  @Test
  public void testTogetherManySummands() {
    // distinct linear denominators are coprime
    check("r=Together(Sum((2*k+1)/(x+k), {k, 1, 60}));"
        + "{Exponent(Denominator(r),x), (r-Sum((2*k+1)/(x+k), {k, 1, 60}))/.x->7/3}", //
        "{60,0}");
    check("r=Together(Sum((a + k*b)/((a + k*b + c)*(b - k*c + 2)), {k, 1, 8}));"
        + "{Exponent(Denominator(r),a), "
        + "(r-Sum((a + k*b)/((a + k*b + c)*(b - k*c + 2)), {k, 1, 8}))/.{a->2/3,b->1/5,c->3/7}}", //
        "{8,0}");
  }

  @Test
  public void testTogetherNested() {
    check("r=Together(Nest(y + 2/(3 + #) &, y, 20));"
        + "{Exponent(Denominator(r),y), (r-Nest(y + 2/(3 + #) &, y, 20))/.y->5/2}", //
        "{20,0}");
  }

  @Test
  public void testTogetherCommonFactors() {
    // a common factor of degree 2 of the numerator and one denominator
    check("Together(2/(x^2-4)+(x+3)/(x^3+8))", //
        "(2-3*x+3*x^2)/((-2+x)*(2+x)*(4-2*x+x^2))");
    check("Together((x^2+x+1)/(x^3-1)+1/(x+1))", //
        "(2*x)/((-1+x)*(1+x))");
    check("Together(x/(x^2-1)+1/(x^2-1))", //
        "1/(-1+x)");
    // a denominator factor split by its gcd with the numerator keeps the content of the factor
    check("Together((1-6*x+9*x^2)/(4*(x-x^2)^2)+1/(x-x^2)+(-1+2*x-3*x^2)/(2*(x-x^2)^2))", //
        "-1/(4*x^2)");
    check("Together((4-4*x+x^2)/(3*(x^2-2*x)^2)+5/(6*(x^2-2*x)))", //
        "(-4+7*x)/(6*(-2+x)*x^2)");
  }

  @Test
  public void testCancelQuotients() {
    // two sums are divided by their GCD, not factored
    check("Cancel((x^6-1)/(x^4-1))", //
        "(1+x^2+x^4)/(1+x^2)");
    check("Cancel((x^4-1)/(x+1), Modulus->3)", //
        "2+x+2*x^2+x^3");
    check("Options(Cancel)", //
        "{Extension->None,Modulus->0,Trig->False}");
    // the options of Apart with their default values
    check("Apart((x+1)/(x^2-4), x, Trig->False)", //
        "3/(4*(-2+x))+1/(4*(2+x))");
    check("Apart(1/(x^3+1), x, Modulus->0)", //
        "1/(3*(1+x))+(2-x)/(3*(1-x+x^2))");
    check("Cancel((2 - 4*x)/(2 - 2*x))", //
        "(1-2*x)/(1-x)");
  }

  @Test
  public void testTogetherGaussian() {
    // the linear denominators stay as they are written, Gaussian-primitive
    check("Together(1/(x-3*I)+1/(x+3*I))", //
        "(2*x)/((-I*3+x)*(I*3+x))");
    check("Together(1/(1-3*I*x)+1/(1+3*I*x))", //
        "2/((-I+3*x)*(I+3*x))");
    check("Together(1/x+1/(x+2), Trig->False)", //
        "(2*(1+x))/(x*(2+x))");
  }

  @Test
  public void testCancelHighDegree() {
    check("Cancel(Expand((x^2+x+1)^20*(x^3+2))/Expand((x^2+x+1)^20*(x^3+5)))", //
        "(2+x^3)/(5+x^3)");
    check("Cancel(Expand((x^2+3)^18*(2*x+1))/Expand(4*(x^2+3)^17*(x+5)))", //
        "(3+6*x+x^2+2*x^3)/(4*(5+x))");
  }

  @Test
  public void testApartProductDenominator() {
    // each factor of the product is factored on its own
    check("f=(x^2+3*x+7)/Product(x+2*k, {k, 1, 30});r=Apart(f, x);{Length(r), (r-f)/.x->1/3}", //
        "{30,0}");
    check("f=(x+5)/((2*x+1)*(3*x-2)^2*(5-x));r=Apart(f,x);{r,Together(r-f)}", //
        "{51/(91*(2-3*x)^2)-10/(1859*(-5+x))+36/(539*(1+2*x))-696/(8281*(-2+3*x)),0}");
    check("f=(4*x^3-1)/(7*(2*x+3)*(x^2+2)*(1-3*x));Together(Apart(f,x)-f)", //
        "0");
    check("Apart((5*x+1)/((x-1)*(x+2)^2*(x^2+1)), x)", //
        "1/(3*(-1+x))+3/(5*(2+x)^2)+26/(75*(2+x))+(-6-17*x)/(25*(1+x^2))");
  }
}
