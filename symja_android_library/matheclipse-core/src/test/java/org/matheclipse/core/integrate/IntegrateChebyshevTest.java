package org.matheclipse.core.integrate;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

/**
 * M1 corpus test for the Chebyshev binomial-differential stage ({@link ChebyshevIntegration}), wired
 * into the {@code Integrate} Automatic cascade as a post-Rubi fallback and reachable directly via
 * {@code Integrate[f, x, Method -> "Chebyshev"]}. See {@code INTEGRATE_MATHILDA_PORT_PLAN.md} §6.
 */
public class IntegrateChebyshevTest extends AbstractIntegrateCorpusTest {

  /**
   * Force the single {@code "Chebyshev"} stage. Every seed case satisfies one of Chebyshev's three
   * integer conditions, so the stage must resolve it and differentiate back (no regressions).
   */
  @Test
  public void chebyshevForced() {
    runCorpusResource("/integrate/chebyshev_seed.txt", "Chebyshev");
  }

  /** Same corpus through the full Automatic cascade. All must differentiate back to the integrand. */
  @Test
  public void chebyshevAutomatic() {
    runCorpusResource("/integrate/chebyshev_seed.txt", null);
  }

  /**
   * Quantify the coverage the post-Rubi Chebyshev stage adds: with the stage disabled, count how
   * many seed cases the Rubi rules leave unevaluated (those are the ones the stage recovers).
   */
  @Test
  public void chebyshevAddedCoverageOverRubi() {
    boolean old = Config.INTEGRATE_ALGORITHM_CHEBYCHEV;
    Config.INTEGRATE_ALGORITHM_CHEBYCHEV = false;
    try {
      CorpusResult rubiOnly = runCorpusResource("/integrate/chebyshev_seed.txt", null);
      System.out.println("Chebyshev adds coverage for " + rubiOnly.unresolved
          + "/5 seed cases that Rubi alone leaves unevaluated");
    } finally {
      Config.INTEGRATE_ALGORITHM_CHEBYCHEV = old;
    }
  }
}
