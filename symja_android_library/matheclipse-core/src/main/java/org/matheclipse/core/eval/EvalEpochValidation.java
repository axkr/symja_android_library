package org.matheclipse.core.eval;

import java.util.concurrent.atomic.AtomicLong;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Records the outcome of the check enabled with
 * {@link org.matheclipse.core.basic.Config#EVAL_EPOCH_VALIDATE}.
 *
 * <p>
 * The evaluation loop skips an expression which carries a current fixed point stamp. The check
 * evaluates such an expression anyway: a stamp which is right gives no result. A result means the
 * loop would have returned an expression which still evaluates to something else - the stamp
 * outlived a change it should not have, or it depends on engine state the stamp does not record.
 */
public final class EvalEpochValidation {

  private static final int MAX_REPORTS = 50;

  private static final AtomicLong CHECKED = new AtomicLong();

  private static final AtomicLong STALE = new AtomicLong();

  private static volatile String firstStale = null;

  private EvalEpochValidation() {}

  /**
   * @param stamped the expression which carries a current stamp
   * @param result what evaluating it anyway returned
   */
  static void checked(IExpr stamped, IExpr result) {
    CHECKED.incrementAndGet();
    if (result.isPresent()) {
      final long count = STALE.incrementAndGet();
      if (count <= MAX_REPORTS) {
        String message;
        try {
          message = stamped.fullFormString() + "\n  evaluates to: " + result.fullFormString();
        } catch (RuntimeException rex) {
          message = "<" + rex + ">";
        }
        if (firstStale == null) {
          firstStale = message;
        }
        System.err.println("Stale fixed point stamp!\n  " + message);
      }
    }
  }

  public static long checked() {
    return CHECKED.get();
  }

  public static long stale() {
    return STALE.get();
  }

  public static String firstStale() {
    return firstStale;
  }

  public static void reset() {
    CHECKED.set(0);
    STALE.set(0);
    firstStale = null;
  }
}
