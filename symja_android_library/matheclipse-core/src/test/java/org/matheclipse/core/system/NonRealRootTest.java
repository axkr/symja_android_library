package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Root</code> objects of a polynomial with a coefficient which isn't real: their numeric
 * values, and their order - by real part, and for equal real parts the roots which aren't real, by
 * imaginary part, before the real one.
 */
public class NonRealRootTest extends ExprEvaluatorTestCase {

  @Test
  public void testOrder() {
    check("Table(Root((# - 4)*(# - 2*I)*(# + 3 - I) &, k), {k, 3})", //
        "{-3+I,I*2,4}");
    check("Table(Root((# - 2 - I)*(# - 2 + 3*I)*(# - 2) &, k), {k, 3})", //
        "{2-I*3,2+I,2}");
    check("Table(Root((# - 2*I)*(# + 2*I)*(# - 5*I)*(# - 1/3) &, k), {k, 4})", //
        "{-I*2,I*2,I*5,1/3}");
    // a double root has two indices
    check("Table(Root((# - 2*I)^2*(# + 5) &, k), {k, 3})", //
        "{-5,I*2,I*2}");
  }

  @Test
  public void testNumericValues() {
    check("N(Root(#^2 + 2*I &, 1))", //
        "-1.0+I*1.0");
    check("N(Table(Root(#^3 - 2*I*# + 3 &, k), {k, 3}))", //
        "{-1.44799+I*(-0.477743),0.337588+I*(-1.00638),1.1104+I*1.48412}");
    check("N(Root(#^5 + 2*I*#^2 + 3 &, 4), 25)", //
        "0.7743190619873576984370281+I*0.6489228296422770614049022");
    // each value is a root
    check("Max(Abs(Table(z^5 + 2*I*z^2 + 3 /. z -> N(Root(#^5 + 2*I*#^2 + 3 &, k)), {k, 5}))) < 10^-12", //
        "True");
  }

  @Test
  public void testRealIrrationalCoefficients() {
    // a real coefficient which is no number object: the real root comes first
    check("Im(N(Root(#^3 - Sqrt(3)*# - 2 &, 1))) == 0", //
        "True");
    check("Max(Abs(Table(z^3 - Sqrt(3)*z - 2 /. z -> N(Root(#^3 - Sqrt(3)*# - 2 &, k)), {k, 3}))) < 10^-12", //
        "True");
  }

  @Test
  public void testCloseRootsAndRadicals() {
    // two roots 10^-7 apart are two roots, a double root is one
    check("Abs(N(Root((# - 2*I)*(# - 2*I - 10^-7)*(# + 5) &, 3)) - N(Root((# - 2*I)*(# - 2*I - 10^-7)*(# + 5) &, 2))) > 5*10^-8", //
        "True");
    // the radical form and the numeric value name the same root
    check("Max(Abs(N(Table(ToRadicals(Root(#^3 - 2*I*# + 3 &, k)), {k, 3})) - N(Table(Root(#^3 - 2*I*# + 3 &, k), {k, 3})))) < 10^-12", //
        "True");
  }

  @Test
  public void testRealCoefficientsUnchanged() {
    check("N(Table(Root(#^3 - 5 &, k), {k, 3}))", //
        "{1.70998,-0.854988+I*(-1.48088),-0.854988+I*1.48088}");
  }
}
