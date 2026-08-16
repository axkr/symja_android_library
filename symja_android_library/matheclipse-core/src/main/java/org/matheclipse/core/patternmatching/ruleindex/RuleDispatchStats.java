package org.matheclipse.core.patternmatching.ruleindex;

import java.util.concurrent.atomic.AtomicLong;
import org.matheclipse.core.basic.Config;

/**
 * Counters for the rule dispatch in
 * <code>org.matheclipse.core.patternmatching.RulesData#evalDownRule(IExpr, EvalEngine)</code>.
 *
 * <p>
 * Collection is off unless {@link Config#RULE_DISPATCH_STATISTICS} is enabled, so a disabled
 * counter costs one static boolean read. The counters distinguish the two cost classes of the Rubi
 * rule set:
 * <ul>
 * <li>{@link #rulesVisited} - how much of the rule list the dispatch had to walk. This is what the
 * {@link RuleFeatureIndex} reduces.</li>
 * <li>{@link #rhsConditionEvaluations} - how often a structurally matching rule evaluated a
 * right-hand-side <code>With</code>/<code>Module</code>/<code>Condition</code> guard. A rule
 * rejected there is a genuine candidate, so no prefilter can avoid this work.</li>
 * </ul>
 */
public final class RuleDispatchStats {

  private static final AtomicLong DISPATCHES = new AtomicLong();

  private static final AtomicLong RULES_VISITED = new AtomicLong();

  private static final AtomicLong MATCH_ATTEMPTS = new AtomicLong();

  private static final AtomicLong RHS_CONDITION_EVALUATIONS = new AtomicLong();

  private static final AtomicLong INDEXED_DISPATCHES = new AtomicLong();

  /**
   * Rules visited by dispatches on symbols which are large enough for an index. Only these can
   * profit from it, so the ratio of this counter with and without the index is the selectivity the
   * index actually achieves; {@link #RULES_VISITED} is dominated by the many small rule sets which
   * are dispatched by a linear scan either way.
   */
  private static final AtomicLong INDEXABLE_RULES_VISITED = new AtomicLong();

  private RuleDispatchStats() {}

  /** One call of <code>evalDownRule</code> which reached the pattern down-rules. */
  public static void dispatch(boolean indexed) {
    if (Config.RULE_DISPATCH_STATISTICS) {
      DISPATCHES.incrementAndGet();
      if (indexed) {
        INDEXED_DISPATCHES.incrementAndGet();
      }
    }
  }

  /**
   * One rule taken from the rule list, before the pattern hash prefilter.
   *
   * @param indexable <code>true</code> if the symbol has enough rules to use an index
   */
  public static void ruleVisited(boolean indexable) {
    if (Config.RULE_DISPATCH_STATISTICS) {
      RULES_VISITED.incrementAndGet();
      if (indexable) {
        INDEXABLE_RULES_VISITED.incrementAndGet();
      }
    }
  }

  /** One rule handed to a pattern matcher. */
  public static void matchAttempt() {
    if (Config.RULE_DISPATCH_STATISTICS) {
      MATCH_ATTEMPTS.incrementAndGet();
    }
  }

  /** One evaluation of a right-hand-side condition during matching. */
  public static void rhsConditionEvaluation() {
    if (Config.RULE_DISPATCH_STATISTICS) {
      RHS_CONDITION_EVALUATIONS.incrementAndGet();
    }
  }

  public static long dispatches() {
    return DISPATCHES.get();
  }

  public static long indexedDispatches() {
    return INDEXED_DISPATCHES.get();
  }

  public static long rulesVisited() {
    return RULES_VISITED.get();
  }

  public static long indexableRulesVisited() {
    return INDEXABLE_RULES_VISITED.get();
  }

  public static long matchAttempts() {
    return MATCH_ATTEMPTS.get();
  }

  public static long rhsConditionEvaluations() {
    return RHS_CONDITION_EVALUATIONS.get();
  }

  public static void reset() {
    DISPATCHES.set(0);
    INDEXED_DISPATCHES.set(0);
    RULES_VISITED.set(0);
    INDEXABLE_RULES_VISITED.set(0);
    MATCH_ATTEMPTS.set(0);
    RHS_CONDITION_EVALUATIONS.set(0);
  }

  public static String summary() {
    long dispatches = DISPATCHES.get();
    StringBuilder buf = new StringBuilder();
    buf.append("dispatches=").append(dispatches);
    buf.append(" indexed=").append(INDEXED_DISPATCHES.get());
    buf.append(" rulesVisited=").append(RULES_VISITED.get());
    buf.append(" indexableRulesVisited=").append(INDEXABLE_RULES_VISITED.get());
    buf.append(" matchAttempts=").append(MATCH_ATTEMPTS.get());
    buf.append(" rhsConditions=").append(RHS_CONDITION_EVALUATIONS.get());
    if (dispatches > 0) {
      buf.append(" rulesPerDispatch=").append(RULES_VISITED.get() / dispatches);
    }
    return buf.toString();
  }
}
