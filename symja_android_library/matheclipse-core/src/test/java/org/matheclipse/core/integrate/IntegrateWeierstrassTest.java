package org.matheclipse.core.integrate;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

/**
 * M2 corpus test for the Weierstrass half-angle stage ({@link WeierstrassIntegration}), wired into
 * the {@code Integrate} Automatic cascade as a post-Rubi fallback and reachable via
 * {@code Integrate[f, x, Method -> "Weierstrass"]}. See {@code INTEGRATE_MATHILDA_PORT_PLAN.md} §6.
 */
public class IntegrateWeierstrassTest extends AbstractIntegrateCorpusTest {

  @Test
  public void weierstrassForced() {
    runCorpusResource("/integrate/weierstrass_seed.txt", "Weierstrass");
  }

  @Test
  public void weierstrassAutomatic() {
    runCorpusResource("/integrate/weierstrass_seed.txt", null);
  }

  @Test
  public void weierstrassAddedCoverageOverRubi() {
    boolean old = Config.INTEGRATE_ALGORITHM_WEIERSTRASS;
    Config.INTEGRATE_ALGORITHM_WEIERSTRASS = false;
    try {
      CorpusResult rubiOnly = runCorpusResource("/integrate/weierstrass_seed.txt", null);
      System.out.println("Weierstrass adds coverage for " + rubiOnly.unresolved
          + "/6 seed cases that Rubi alone leaves unevaluated");
    } finally {
      Config.INTEGRATE_ALGORITHM_WEIERSTRASS = old;
    }
  }
}
