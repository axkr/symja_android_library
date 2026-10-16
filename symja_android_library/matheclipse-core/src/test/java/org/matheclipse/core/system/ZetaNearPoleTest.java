package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <code>Zeta(s)</code> for an <code>s</code> next to the pole at <code>1</code>.
 */
public class ZetaNearPoleTest extends ExprEvaluatorTestCase {

  @Test
  @Tag(TestTags.SLOW)
  public void testZetaNextToThePole() {
    // 1/(s-1)+EulerGamma-StieltjesGamma(1)*(s-1): only 18 of the 30 digits of the argument are
    // digits of s-1, and the missing ones were shown as noise
    check("N(Zeta(1 + 10^(-12)), 30)", //
        "1000000000000.57721566490160568");
    check("N(Zeta(1 - 10^(-12)), 30)", //
        "-999999999999.422784335098539955");
    // closer than the precision of a machine number: this stayed unevaluated
    check("N(Zeta(1 + 10^(-24)), 40)", //
        "1000000000000000000000000.577215664901533");
    check("N(Zeta(1 - 10^(-24)), 40)", //
        "-999999999999999999999999.4227843350984671");
    check("N(Zeta(1 + 10^(-24)), 60)", //
        "1000000000000000000000000.57721566490153286060651216289824791");
    check("N(Zeta(1 + I*10^(-24)), 40)", //
        "0.5772156649015328606+I*(-1000000000000000000000000)");
    // 20 digits cannot tell the argument from 1
    check("N(Zeta(1 + 10^(-24)), 20)", //
        "ComplexInfinity");
    // away from the pole nothing changes
    check("N(Zeta(3/2), 30)", //
        "2.61237534868548834334856756792");
  }
}
