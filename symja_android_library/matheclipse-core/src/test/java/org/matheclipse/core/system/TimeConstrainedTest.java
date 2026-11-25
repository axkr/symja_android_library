package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** TimeConstrained honours fractions of a second. */
public class TimeConstrainedTest extends ExprEvaluatorTestCase {

  @Test
  @Tag(TestTags.SLOW)
  public void testFractionalSecondsWMA() {
    // WMA: {0.300372, "x"} - it used to be rounded up to a whole second
    check("t = AbsoluteTiming(TimeConstrained(While(True, 1), 0.3, \"x\")); "
        + "{t[[1]] < 0.95, t[[2]]}", //
        "{True,x}");
    check("TimeConstrained(1+1, 0.001)", //
        "2");
  }

  @Test
  public void testLimitInsideLoopTest() {
    // the limit passes while NextPrime in the loop test runs: the loop must not end as if the
    // test had answered False
    check("TimeConstrained(Module({q = 7}, While(Mod(q, 10^9 + 7) != 1, q = NextPrime(q)); q), 0.2)", //
        "$Aborted");
    // the same for the condition of a definition
    check("walk(n_) := Module({q = n}, While(NextPrime(q) < 10^15, q = NextPrime(q)); q);"
        + "big(x_ /; walk(x) > 1) := x; TimeConstrained(big(3), 0.2)", //
        "$Aborted");
    // a short loop still ends normally
    check("TimeConstrained(Module({q = 7}, While(NextPrime(q) < 60, q = NextPrime(q)); q), 5)", //
        "59");
  }

  @Test
  public void testMessage() {
    check("TimeConstrained(1+1, 0)", //
        "TimeConstrained(1+1,0)");
  }
}
