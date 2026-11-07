package org.matheclipse.io.system;

import org.junit.jupiter.api.Test;

/**
 * The functions of the <code>Internal`</code> context live in <code>matheclipse-internal</code>,
 * which reaches the servlets and the consoles through {@link org.matheclipse.io.IOInit#init()},
 * as {@link AbstractTestCase#setUp()} calls it. If the dependency or the
 * <code>InternalInit.init()</code> call is ever dropped from <code>matheclipse-io</code>, these
 * expressions come back unevaluated and this test fails.
 */
public class InternalAvailabilityTest extends AbstractTestCase {

  @Test
  public void testBagIsRegistered() {
    check("b = Internal`Bag({3, 4}); Internal`StuffBag(b, 5); Internal`BagLength(b)", //
        "3");
  }

  @Test
  public void testInheritedBlockIsRegistered() {
    check("v = 4; Internal`InheritedBlock({v}, v = v + 1; v)", //
        "5");
  }
}
