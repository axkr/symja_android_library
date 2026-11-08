package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** <code>Mod</code> of machine numbers is the exact remainder of the doubles. */
public class ModMachineRemainderTest extends ExprEvaluatorTestCase {

  @Test
  public void testRemainderInRange() {
    check("Mod(-0.25,4)", //
        "3.75");
    check("Mod(7.5,-2)", //
        "-0.5");
    check("Mod(3.*10^-19,5)", //
        "3.*10^-19");
    check("Mod(-3.*10^-19,-5)", //
        "-3.*10^-19");
  }

  @Test
  public void testCorrectionRoundsToModulus() {
    // a sign correction which rounds to the modulus gives 0
    check("Mod(-1.*10^-18,7)", //
        "0.0");
    check("Mod(1.*10^-18,-7)", //
        "0.0");
    check("Mod(-3.*10^-19,5/2)", //
        "0.0");
    check("Mod(-8.,-4)", //
        "0.0");
  }
}
