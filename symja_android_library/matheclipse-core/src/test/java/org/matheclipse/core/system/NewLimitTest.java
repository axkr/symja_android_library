package org.matheclipse.core.system;

import org.junit.Test;

/** Tests for NewLimit function */
public class NewLimitTest extends ExprEvaluatorTestCase {

  @Test
  public void testNewLimitExp() {
    // stub
    check("NewLimit(E^x, x->Infinity)", //
        "NewLimit(E^x,x->Infinity)");

  }

}
