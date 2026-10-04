package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Integrate(Sqrt((c+d*x)/(a+b*x)), x)</code>
 */
public class IntegrateSqrtQuotientTest extends ExprEvaluatorTestCase {

  @Test
  public void testSqrtOfLinearQuotient() {
    // in the radical of the integrand; this came back with Sqrt(-2-3*x) and an ArcSin
    check("Integrate(Sqrt((2+3*x)/(1+x)), x)", //
        "(1+x)*Sqrt((2+3*x)/(1+x))-ArcTanh(Sqrt((2+3*x)/(1+x))/Sqrt(3))/Sqrt(3)");
    check("Integrate(Sqrt((5+2*x)/(2+x)), x)", //
        "(2+x)*Sqrt((5+2*x)/(2+x))+ArcTanh(Sqrt((5+2*x)/(2+x))/Sqrt(2))/Sqrt(2)");
    check("Integrate(Sqrt((2+5*x)/(1+x)), x)", //
        "(1+x)*Sqrt((2+5*x)/(1+x))+(-3*ArcTanh(Sqrt((2+5*x)/(1+x))/Sqrt(5)))/Sqrt(5)");
    // b*d < 0: the ArcTan form
    check("Integrate(Sqrt((2-3*x)/(1+x)), x)", //
        "Sqrt((2-3*x)/(1+x))*(1+x)+(-5*ArcTan(Sqrt((2-3*x)/(1+x))/Sqrt(3)))/Sqrt(3)");
    check("Integrate(Sqrt((2+3*x)/(1-x)), x)", //
        "(-1+x)*Sqrt((2+3*x)/(1-x))+(5*ArcTan(Sqrt((2+3*x)/(1-x))/Sqrt(3)))/Sqrt(3)");
    check("Integrate(Sqrt((2-3*x)/(1-x)), x)", //
        "Sqrt((2-3*x)/(1-x))*(-1+x)+ArcTanh(Sqrt((2-3*x)/(1-x))/Sqrt(3))/Sqrt(3)");
    check("Integrate(Sqrt(x/(1+x)), x)", //
        "Sqrt(x/(1+x))*(1+x)-ArcTanh(Sqrt(x/(1+x)))");
    check("Integrate(Sqrt((c+d*x)/(a+b*x)), x)", //
        "((a+b*x)*Sqrt((c+d*x)/(a+b*x)))/b+((b*c-a*d)*ArcTanh((Sqrt(b)*Sqrt((c+d*x)/(a+b*x)))/Sqrt(d)))/(b^(\n" //
            + "3/2)*Sqrt(d))");
  }

  @Test
  public void testDerivativeIsTheIntegrand() {
    check("Simplify(D(Integrate(Sqrt((2+3*x)/(1+x)), x), x) - Sqrt((2+3*x)/(1+x)))", //
        "0");
    check("Simplify(D(Integrate(Sqrt((2-3*x)/(1+x)), x), x) - Sqrt((2-3*x)/(1+x)))", //
        "0");
    check("Simplify(D(Integrate(Sqrt((c+d*x)/(a+b*x)), x), x) - Sqrt((c+d*x)/(a+b*x)))", //
        "0");
    check("Integrate(Sqrt((2+3*x)/(1+x)), {x, 0, 1})", //
        "-Sqrt(2)+Sqrt(10)+ArcTanh(Sqrt(2/3))/Sqrt(3)-ArcTanh(Sqrt(5/6))/Sqrt(3)");
    check("NIntegrate(Sqrt((2+3*x)/(1+x)), {x, 0, 1})", //
        "1.51812");
  }
}
