package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Log(b, z) of two powers of a common rational is that rational, for numbers of any size. */
public class LogExactTest extends ExprEvaluatorTestCase {

  @Test
  public void testExactLogOfPowers() {
    check("{Log(10, 10^30), Log(2, 2^100), Log10(10^30), Log2(2^1000), Log(6, 36^7)}", //
        "{30,100,30,1000,14}");
    check("{Log(10, 1000), Log(4, 8), Log(8, 2), Log(9, 27), Log(100, 1000), Log(1/2, 8), "
        + "Log(10, 1/100), Log(2, 1)}", //
        "{3,3/2,1/3,3/2,3/2,-3,-2,0}");
    // not powers of a common base
    check("{Log(3, 10), Log(10, 10^30+1)}", //
        "{Log(10)/Log(3),Log(1000000000000000000000000000001)/Log(10)}");
  }
}
