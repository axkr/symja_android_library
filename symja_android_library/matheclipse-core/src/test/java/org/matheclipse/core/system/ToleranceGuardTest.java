package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/** Verifies that the tolerant numeric comparison accepts rounding noise but not real differences. */
public class ToleranceGuardTest extends ExprEvaluatorTestCase {

  private boolean eq(String expected, String actual) {
    return equalsNumeric(evaluatorN, expected, actual, DEFAULT_NUMERIC_RELATIVE_TOLERANCE);
  }

  @Test
  public void testAcceptsRoundingNoise() {
    assertTrue(eq("-0.2193839343955203", "-0.2193839343955202"));
    assertTrue(eq("2.866515718791932E-7", "2.8665157187919423E-7"));
    assertTrue(eq("{{2.0,1.9999999999999996,0.9999999999999994}}", "{{2.0,2.0,1.0}}"));
    assertTrue(eq("(-1.54308-I*3.19744*10^-14)", "(-1.54308-I*3.73035*10^-14)"));
    assertTrue(eq("{p->0.04999709393822403}", "{p->0.049997093938224026}"));
    assertTrue(eq("{0.5+I*0.5,1.1+I*(-5.27356*10^-16)}", "{0.5+I*0.5,1.1+I*(-5.13478*10^-16)}"));
  }

  @Test
  public void testRejectsRealDifferences() {
    // an 11th-significant-digit change is above the default tolerance
    assertFalse(eq("3.141592653589793", "3.141592658000000"));
    // a genuinely tiny standalone value still has to match to full precision
    assertFalse(eq("6.220960574271835E-16", "9.999960574271835E-16"));
    // sign, structure and symbolic content must still match
    assertFalse(eq("0.5", "-0.5"));
    assertFalse(eq("{1.0,2.0}", "{1.0,2.0,3.0}"));
    assertFalse(eq("{p->1.0}", "{q->1.0}"));
    assertFalse(eq("Indeterminate", "0.0"));
    // the optimizer-scale difference must NOT pass at the default tolerance
    assertFalse(eq("4.493409457738778", "4.493409458054649"));
  }
}
