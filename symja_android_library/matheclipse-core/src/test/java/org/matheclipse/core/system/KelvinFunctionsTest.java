package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for the Kelvin functions */
public class KelvinFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testKelvinBer() {
    check("KelvinBer(0)", //
        "1");
    check("KelvinBer(3,0)", //
        "0");
    check("KelvinBer(0,z)", //
        "KelvinBer(0,z)");
    check("KelvinBer(n,z)", //
        "KelvinBer(n,z)");
    check("KelvinBer(1.0)", //
        "0.984382");
    check("KelvinBer(0,2.0)", //
        "0.751734");
    check("KelvinBer(2,3.5)", //
        "1.44234");
    check("KelvinBer(0.5,1.5)", //
        "0.0216918");
    check("KelvinBer(1,2.5)", //
        "-1.3731");
    check("N(KelvinBer(2), 30)", //
        "0.75173418271380822855099890123");
    check("KelvinBer({1.0,2.0})", //
        "{0.984382,0.751734}");
  }

  @Test
  public void testKelvinBei() {
    check("KelvinBei(0)", //
        "0");
    check("KelvinBei(0,z)", //
        "KelvinBei(0,z)");
    check("KelvinBei(1.0)", //
        "0.249566");
    check("KelvinBei(0,2.0)", //
        "0.972292");
    check("KelvinBei(2,3.5)", //
        "-0.948359");
    check("KelvinBei(0.5,1.5)", //
        "1.00419");
    check("KelvinBei(1,2.5)", //
        "0.0386684");
    check("N(KelvinBei(2), 30)", //
        "0.972291627306661206104032220226");
  }

  @Test
  public void testKelvinComplex() {
    // values confirmed with WMA
    // WMA: 1.32824*10^-18 + 0.0216918 I
    check("KelvinBer(0.5, -1.5)", //
        "I*0.0216918");
    check("KelvinBer(0, 1.0 + 2.0*I)", //
        "1.1058+I*0.377274");
    check("KelvinBei(0, 1.0 + 2.0*I)", //
        "-0.800765+I*0.980691");
  }
}
