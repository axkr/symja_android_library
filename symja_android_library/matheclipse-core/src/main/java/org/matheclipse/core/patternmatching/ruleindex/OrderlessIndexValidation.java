package org.matheclipse.core.patternmatching.ruleindex;

import java.util.concurrent.atomic.AtomicLong;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.hash.AbstractHashedPatternRules;

/**
 * Records the outcome of the dual run check enabled with
 * {@link org.matheclipse.core.basic.Config#ORDERLESS_PAIR_INDEX_VALIDATE}.
 *
 * <p>
 * With the check enabled the matcher computes the {@link OrderlessPairIndex} features but does not
 * apply them, so every argument pair is looked up exactly like before the index existed. Whenever a
 * rule rewrites a pair the index is asked whether it would have kept that pair. A rewrite the index
 * would have excluded is the only failure mode which changes results, so it is the one property
 * worth checking.
 *
 * <p>
 * Slow and not thread safe with respect to the reported first miss - for testing only.
 */
public final class OrderlessIndexValidation {

  private static final AtomicLong CHECKED = new AtomicLong();

  private static final AtomicLong MISSED = new AtomicLong();

  private static volatile String firstMiss = null;

  private OrderlessIndexValidation() {}

  /**
   * @param kept <code>true</code> if the index would have kept the pair which fired
   * @param orderlessAST the expression which was rewritten
   * @param arg1 the first matched argument
   * @param arg2 the second matched argument
   * @param hashRule the rule which fired
   */
  public static void checked(boolean kept, IAST orderlessAST, IExpr arg1, IExpr arg2,
      AbstractHashedPatternRules hashRule) {
    CHECKED.incrementAndGet();
    if (!kept) {
      MISSED.incrementAndGet();
      if (firstMiss == null) {
        firstMiss = "expression: " + orderlessAST + "\n  arguments: " + arg1 + ", " + arg2
            + "\n  rule: " + hashRule;
        System.err.println("OrderlessPairIndex dropped a pair which fired!\n  " + firstMiss);
      }
    }
  }

  public static long checked() {
    return CHECKED.get();
  }

  public static long missed() {
    return MISSED.get();
  }

  public static String firstMiss() {
    return firstMiss;
  }

  public static void reset() {
    CHECKED.set(0);
    MISSED.set(0);
    firstMiss = null;
  }
}
