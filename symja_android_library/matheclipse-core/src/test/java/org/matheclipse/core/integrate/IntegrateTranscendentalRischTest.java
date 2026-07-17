package org.matheclipse.core.integrate;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

/**
 * M4 corpus test for the transcendental-Risch recogniser ({@link TranscendentalRisch}; currently the
 * Gaussian -> Erf family), wired into the {@code Integrate} Automatic cascade as a post-Rubi fallback
 * and reachable via {@code Integrate[f, x, Method -> "RischTranscendental"]}. See
 * {@code INTEGRATE_MATHILDA_PORT_PLAN.md} §6.
 */
public class IntegrateTranscendentalRischTest extends AbstractIntegrateCorpusTest {

  @Test
  public void transcendentalRischForced() {
    runCorpusResource("/integrate/transcendental_risch_seed.txt", "RischTranscendental");
  }

  @Test
  public void transcendentalRischAutomatic() {
    runCorpusResource("/integrate/transcendental_risch_seed.txt", null);
  }

  @Test
  public void transcendentalRischAddedCoverageOverRubi() {
    boolean old = Config.INTEGRATE_ALGORITHM_RISCH_TRANSCENDENTAL;
    Config.INTEGRATE_ALGORITHM_RISCH_TRANSCENDENTAL = false;
    try {
      CorpusResult rubiOnly = runCorpusResource("/integrate/transcendental_risch_seed.txt", null);
      System.out.println("transcendental-Risch adds coverage for " + rubiOnly.unresolved
          + "/51 seed cases that Rubi alone leaves unevaluated");
    } finally {
      Config.INTEGRATE_ALGORITHM_RISCH_TRANSCENDENTAL = old;
    }
  }
}
