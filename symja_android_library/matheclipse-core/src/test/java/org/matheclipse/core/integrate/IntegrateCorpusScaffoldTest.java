package org.matheclipse.core.integrate;

import org.junit.jupiter.api.Test;

/**
 * M0 template for the per-method corpus suites (see {@code INTEGRATE_MATHILDA_PORT_PLAN.md} §6).
 *
 * <p>
 * Copy this class per native stage (e.g. {@code IntegrateChebyshevTest},
 * {@code IntegrateWeierstrassTest}) pointing at that stage's clean-room corpus resource and, where
 * applicable, forcing the stage with its {@code Method ->} name.
 */
public class IntegrateCorpusScaffoldTest extends AbstractIntegrateCorpusTest {

  /**
   * Rational-function integrands through the Automatic cascade. The rational stage is enabled by
   * default, so every case must resolve and differentiate back to the integrand (no regressions).
   */
  @Test
  public void rationalSeedAutomatic() {
    runCorpusResource("/integrate/rational_seed.txt", null);
  }

  /**
   * Same corpus, forcing the single {@code "Rational"} stage via the {@code Method} option. Cases the
   * native rational integrator does not yet cover surface as UNRESOLVED (tolerated); a wrong
   * antiderivative would surface as a FAIL.
   */
  @Test
  public void rationalSeedForcedMethod() {
    runCorpusResource("/integrate/rational_seed.txt", "Rational");
  }
}
