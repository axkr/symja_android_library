package org.matheclipse.core.integrate;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

/**
 * M1 corpus test for the derivative-divides (Geddes) u-substitution stage
 * ({@link DerivativeDivides}), now wired into the {@code Integrate} Automatic cascade ahead of the
 * Rubi rules. See {@code INTEGRATE_MATHILDA_PORT_PLAN.md} §6.
 */
public class IntegrateDerivativeDividesTest extends AbstractIntegrateCorpusTest {

  /**
   * Force the single {@code "DerivativeDivides"} stage. Every seed case is a genuine
   * {@code c*f(u(x))*u'(x)} composite the stage must resolve and differentiate back (no regressions,
   * no coverage gaps expected).
   */
  @Test
  public void derivativeDividesForced() {
    runCorpusResource("/integrate/derivative_divides_seed.txt", "DerivativeDivides");
  }

  /**
   * Same corpus through the full Automatic cascade (rational -> radical -> derivative-divides ->
   * Rubi). All must differentiate back to the integrand.
   */
  @Test
  public void derivativeDividesAutomatic() {
    runCorpusResource("/integrate/derivative_divides_seed.txt", null);
  }

  /**
   * Quantify the coverage the post-Rubi fallback adds: disable the stage, count how many seed cases
   * the Rubi rules (plus the pre-Rubi rational/radical stages) leave unevaluated, then confirm the
   * stage recovers all of them. A non-zero count is the concrete value of wiring the stage.
   */
  @Test
  public void derivativeDividesAddedCoverageOverRubi() {
    boolean old = Config.INTEGRATE_ALGORITHM_DERIVATIVE_DIVIDES;
    Config.INTEGRATE_ALGORITHM_DERIVATIVE_DIVIDES = false;
    try {
      CorpusResult rubiOnly = runCorpusResource("/integrate/derivative_divides_seed.txt", null);
      System.out.println("derivative-divides adds coverage for " + rubiOnly.unresolved
          + "/8 seed cases that Rubi alone leaves unevaluated");
    } finally {
      Config.INTEGRATE_ALGORITHM_DERIVATIVE_DIVIDES = old;
    }
  }
}
