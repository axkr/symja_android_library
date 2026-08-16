package org.matheclipse.core.patternmatching.ruleindex;

import java.util.concurrent.atomic.AtomicLong;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * Records the outcome of the dual run check enabled with
 * {@link org.matheclipse.core.basic.Config#RULE_INDEX_VALIDATE}.
 *
 * <p>
 * The check verifies the only property the evaluation depends on: whenever a linear scan rewrites
 * an expression with a rule, the {@link RuleFeatureIndex} must report that rule as a candidate. A
 * rule which the index drops although it would have fired is an unsound
 * {@link RuleFeatureAnalyzer} result and would silently change results.
 */
public final class RuleIndexValidation {

  private static final AtomicLong CHECKED = new AtomicLong();

  private static final AtomicLong MISSED = new AtomicLong();

  private static volatile String firstMiss = null;

  private RuleIndexValidation() {}

  /**
   * @param contained <code>true</code> if the index reported the rule which fired
   * @param subject the expression which was rewritten
   * @param matcher the rule which fired
   */
  public static void checked(boolean contained, IExpr subject, IPatternMatcher matcher) {
    CHECKED.incrementAndGet();
    if (!contained) {
      MISSED.incrementAndGet();
      if (firstMiss == null) {
        firstMiss = "subject: " + subject + "\n  rule: " + (matcher == null ? "?" : matcher.getLHS())
            + "\n  priority: " + (matcher == null ? -1 : matcher.getLHSPriority());
        System.err.println("RuleFeatureIndex dropped a matching rule!\n  " + firstMiss);
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
