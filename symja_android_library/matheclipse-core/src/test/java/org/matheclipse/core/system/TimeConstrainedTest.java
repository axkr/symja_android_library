package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** TimeConstrained honours fractions of a second. */
public class TimeConstrainedTest extends ExprEvaluatorTestCase {

  @Test
  public void testFractionalSecondsWMA() {
    // WMA: {0.300372, "x"} - it used to be rounded up to a whole second
    check("t = AbsoluteTiming(TimeConstrained(While(True, 1), 0.3, \"x\")); "
        + "{t[[1]] < 0.95, t[[2]]}", //
        "{True,x}");
    check("TimeConstrained(1+1, 0.001)", //
        "2");
  }

  @Test
  public void testMessage() {
    check("TimeConstrained(1+1, 0)", //
        "TimeConstrained(1+1,0)");
  }
}
