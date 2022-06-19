package org.matheclipse.core.system;

/** Tests for NewLimit function */
public class NewLimitTest extends ExprEvaluatorTestCase {

  public NewLimitTest(String name) {
    super(name);
  }

  public void testNewLimitExp() {
    // stub
    check("NewLimit(E^x, x->Infinity)", //
        "NewLimit(E^x,x->Infinity)");

  }

}
