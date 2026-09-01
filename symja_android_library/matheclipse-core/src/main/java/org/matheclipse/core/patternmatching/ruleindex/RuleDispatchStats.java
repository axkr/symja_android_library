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

  /**
   * One right-hand-side condition which held, so the rule fired. The difference to
   * {@link #rhsConditionEvaluations()} is the work spent on rules which matched structurally and
   * were then rejected by their condition.
   */
  public static void rhsConditionHeld() {
    if (Config.RULE_DISPATCH_STATISTICS) {
      RHS_CONDITIONS_HELD.incrementAndGet();
    }
  }

  private static final AtomicLong RHS_CONDITIONS_HELD = new AtomicLong();

  public static long rhsConditionsHeld() {
    return RHS_CONDITIONS_HELD.get();
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

  /**
   * Number of pattern rule dispatches per symbol, for the question which rule sets the dispatch is
   * entered for at all. Only filled while {@link Config#RULE_DISPATCH_STATISTICS} is enabled.
   */
  private static final java.util.concurrent.ConcurrentHashMap<String, AtomicLong> PER_SYMBOL =
      new java.util.concurrent.ConcurrentHashMap<String, AtomicLong>();

  /** Rule set size of every symbol seen by {@link #symbolDispatch(String, int)}. */
  private static final java.util.concurrent.ConcurrentHashMap<String, Integer> PER_SYMBOL_RULES =
      new java.util.concurrent.ConcurrentHashMap<String, Integer>();

  /**
   * One dispatch into the pattern down-rules of <code>symbolName</code>.
   *
   * @param symbolName the symbol whose rule set is scanned
   * @param rules the number of pattern rules that symbol has
   */
  public static void symbolDispatch(String symbolName, int rules) {
    if (Config.RULE_DISPATCH_STATISTICS) {
      PER_SYMBOL.computeIfAbsent(symbolName, k -> new AtomicLong()).incrementAndGet();
      PER_SYMBOL_RULES.put(symbolName, rules);
    }
  }

  /**
   * The symbols dispatched most often, as
   * <code>name dispatches=n rules=m maxVisits=n*m</code> lines ordered by descending
   * <code>n*m</code> - the upper bound of the rule list walk they can cause.
   *
   * @param limit maximum number of symbols to report
   */
  public static String perSymbolSummary(int limit) {
    return PER_SYMBOL.entrySet().stream() //
        .sorted((a, b) -> Long.compare(bound(b.getKey(), b.getValue()), //
            bound(a.getKey(), a.getValue()))) //
        .limit(limit) //
        .map(e -> String.format("%-28s dispatches=%-9d rules=%-6d maxVisits=%d", e.getKey(),
            e.getValue().get(), PER_SYMBOL_RULES.getOrDefault(e.getKey(), 0),
            bound(e.getKey(), e.getValue()))) //
        .reduce((a, b) -> a + "\n" + b) //
        .orElse("");
  }

  private static long bound(String symbolName, AtomicLong dispatches) {
    return dispatches.get() * PER_SYMBOL_RULES.getOrDefault(symbolName, 0).longValue();
  }

  public static void reset() {
    PER_SYMBOL.clear();
    PER_SYMBOL_RULES.clear();
    DISPATCHES.set(0);
    INDEXED_DISPATCHES.set(0);
    RULES_VISITED.set(0);
    INDEXABLE_RULES_VISITED.set(0);
    MATCH_ATTEMPTS.set(0);
    RHS_CONDITION_EVALUATIONS.set(0);
    RHS_CONDITIONS_HELD.set(0);
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
    buf.append(" rhsConditionsHeld=").append(RHS_CONDITIONS_HELD.get());
    if (dispatches > 0) {
      buf.append(" rulesPerDispatch=").append(RULES_VISITED.get() / dispatches);
    }
    return buf.toString();
  }
}
