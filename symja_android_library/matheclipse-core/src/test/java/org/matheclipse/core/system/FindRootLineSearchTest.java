package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>FindRoot</code> from a complex start value: a Newton step which doesn't decrease the
 * residual enough is halved, so a far start which overshoots the region of convergence still
 * reaches the root.
 */
public class FindRootLineSearchTest extends ExprEvaluatorTestCase {

  @Test
  public void testOvershootingNewtonSteps() {
    // the full Newton steps of ArcTan(z) from |z| > 1.39 grow without bound
    check("Chop(z/.FindRoot(ArcTan(z),{z,2+I/10}))", //
        "0");
    check("Chop(z/.FindRoot(ArcTan(z-1)+z/10,{z,4+I/5}))", //
        "0.908863");
  }
}
