package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The sign of an exact numeric expression whose machine value is a rounding error of a
 * cancellation, like <code>-E^40+(1+E^120)^(1/3)</code>, a positive number of about
 * <code>10^-35</code>: it is decided with extra precision, so that <code>Log</code> and
 * <code>Sqrt</code> don't take the branch of the wrong sign.
 */
public class SignOfCancellationTest extends ExprEvaluatorTestCase {

  @Test
  public void testLogOfPositiveDifference() {
    check("Log(-E^40+(1+E^120)^(1/3))", //
        "Log(-E^40+(1+E^120)^(1/3))");
    // a negative difference: the imaginary part of its logarithm is Pi
    check("Chop(Im(N(Log(E^25-(1+E^75)^(1/3)),30))-Pi)", //
        "0");
  }

  @Test
  public void testSqrtOfSquare() {
    check("Sqrt((-E^30+Sqrt(1+E^60))^2)", //
        "-E^30+Sqrt(1+E^60)");
  }

  @Test
  public void testValueOfIdentityWithCancellation() {
    // (c-E^40)*(c^2+c*E^40+E^80) is 1 for c = (1+E^120)^(1/3): both logarithms are real
    check("Chop(N(Log(-E^40+(1+E^120)^(1/3))"
        + "+Log((1+E^120)^(2/3)+E^40*(1+E^120)^(1/3)+E^80),30))", //
        "0");
  }
}
