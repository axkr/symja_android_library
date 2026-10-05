package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Arg</code> under assumptions, and Bernoulli equations with an irrational exponent.
 */
public class RefineArgBernoulliTest extends ExprEvaluatorTestCase {

  @Test
  public void testArg() {
    // the sign of a product or a power under the assumptions
    check("Refine({Arg(c^2), Arg(I*c), Arg(-c), Arg(1/c^3), Arg(-5*c^2), Arg(-3*I*c)}, c>0)", //
        "{0,Pi/2,Pi,0,Pi,-Pi/2}");
    check("Refine({Arg(c*d), Arg(I*c*d), Arg(c+d)}, c>0&&d>0)", //
        "{0,Pi/2,0}");
    // the sign of d is not known
    check("Refine(Arg(c*d), c>0)", //
        "Arg(d)");
    check("Arg(c^2)", //
        "Arg(c^2)");
  }

  @Test
  public void testBernoulliIrrationalExponent() {
    // the solution is the power of a base which is negative at the points the residual is sampled at.
    // There the principal value of y^Sqrt(3) is another branch, and the solution was rejected
    check("DSolve(x*y'(x) + y(x) == x*y(x)^Sqrt(3), y(x), x)", //
        "{{y(x)->(x/(2-Sqrt(3))+(-Sqrt(3)*x)/(2-Sqrt(3))+C(1)/x^(1-Sqrt(3)))^(1/(1-Sqrt(3)))}}");
    check("DSolve(y'(x) + y(x)/x == y(x)^Sqrt(3), y(x), x)", //
        "{{y(x)->(x/(2-Sqrt(3))+(-Sqrt(3)*x)/(2-Sqrt(3))+C(1)/x^(1-Sqrt(3)))^(1/(1-Sqrt(3)))}}");
    check("DSolve(x*y'(x) + 2*y(x) == x*y(x)^Sqrt(7), y(x), x)", //
        "{{y(x)->(x/(3-2*Sqrt(7))+(-Sqrt(7)*x)/(3-2*Sqrt(7))+C(1)/x^(2-2*Sqrt(7)))^(1/(1-Sqrt(\n" //
            + "7)))}}");
  }
}
