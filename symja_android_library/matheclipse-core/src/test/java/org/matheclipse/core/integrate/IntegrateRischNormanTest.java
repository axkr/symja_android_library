package org.matheclipse.core.integrate;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

/**
 * M3 corpus test for the Risch-Norman heuristic stage ({@link RischNorman}), wired into the
 * {@code Integrate} Automatic cascade as a post-Rubi fallback and reachable via
 * {@code Integrate[f, x, Method -> "RischNorman"]}. See {@code INTEGRATE_MATHILDA_PORT_PLAN.md} §6.
 */
public class IntegrateRischNormanTest extends AbstractIntegrateCorpusTest {

  @Test
  public void rischNormanForced() {
    runCorpusResource("/integrate/rischnorman_seed.txt", "RischNorman");
  }

  @Test
  public void rischNormanAutomatic() {
    runCorpusResource("/integrate/rischnorman_seed.txt", null);
  }

  @Test
  public void rischNormanAddedCoverageOverRubi() {
    boolean old = Config.INTEGRATE_ALGORITHM_RISCH_NORMAN;
    Config.INTEGRATE_ALGORITHM_RISCH_NORMAN = false;
    try {
      CorpusResult rubiOnly = runCorpusResource("/integrate/rischnorman_seed.txt", null);
      System.out.println("Risch-Norman adds coverage for " + rubiOnly.unresolved
          + "/5 seed cases that Rubi alone leaves unevaluated");
    } finally {
      Config.INTEGRATE_ALGORITHM_RISCH_NORMAN = old;
    }
  }
}
