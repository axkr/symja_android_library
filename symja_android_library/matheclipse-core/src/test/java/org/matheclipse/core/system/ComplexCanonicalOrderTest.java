package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The canonical order of exact complex numbers: by the real part, then by the size of the imaginary
 * part, then by its sign - the order machine complex numbers already had.
 */
public class ComplexCanonicalOrderTest extends ExprEvaluatorTestCase {

  @Test
  public void testImaginaryMagnitudeBeforeSign() {
    check("Sort({5*I, -2*I, 2*I, -I})", //
        "{-I,-I*2,I*2,I*5}");
    check("Order(-4*I, I)", //
        "-1");
    check("Order(3-I, 3+2*I)", //
        "1");
    check("Sort({(2+5*I)*z, (2-I)*z, (2-5*I)*z})", //
        "{(2-I)*z,(2-I*5)*z,(2+I*5)*z}");
  }

  @Test
  public void testRealBeforeComplexWithEqualRealPart() {
    check("Sort({4-I, 4, 4+I})", //
        "{4,4-I,4+I}");
    // exact and machine complex numbers follow the same rule
    check("Sort({Complex(1.5,-3.0), Complex(1.5,2.0), Complex(1.5,-1.0)})", //
        "{1.5+I*(-1.0),1.5+I*2.0,1.5+I*(-3.0)}");
  }

  @Test
  public void testRationalBeforeComplexWithEqualRealPart() {
    check("Sort({3+2*I, 3, 5/2, 3-I, 3-2*I})", //
        "{5/2,3,3-I,3-I*2,3+I*2}");
  }

  @Test
  public void testRealAgainstInexactComplexBothWays() {
    check("{Order(7/2, Complex(3.5, 1.0)), Order(Complex(3.5, 1.0), 7/2)}", //
        "{1,-1}");
    check("{Order(4.0, Complex(4.0, -1.0)), Order(Complex(4.0, -1.0), 4.0)}", //
        "{1,-1}");
    check("{Order(5, Complex(5.0, -2.0)), Order(Complex(5.0, -2.0), 5)}", //
        "{1,-1}");
  }
}
