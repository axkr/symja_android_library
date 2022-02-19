package org.matheclipse.io.system;

/** Tests for NewLimit function */
public class NewLimitTest extends AbstractTestCase {

  public NewLimitTest(String name) {
    super(name);
  }

  public void testNewLimitExp() {
    // stub
    check("NewLimit(E^x, x->Infinity)", //
        "NewLimit(E^x,x->Infinity)");

  }

}
