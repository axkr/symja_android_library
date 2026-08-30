package org.matheclipse.core.patternmatching.ruleindex;

import java.util.concurrent.atomic.LongAdder;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Counters for the {@link org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher}
 * dispatch of <code>Plus(...)</code> and <code>Times(...)</code>.
 *
 * <p>
 * Collection is off unless {@link Config#ORDERLESS_HASH_STATISTICS} is enabled, so a disabled
 * counter costs one static boolean read. The counters separate the work the
 * {@link OrderlessPairIndex} can remove from the work it cannot:
 * <ul>
 * <li>{@link #pairsProbed()} - argument pairs looked up in the rule map. Without the index almost
 * all of them miss, which is what the index removes.</li>
 * <li>{@link #fires()} - rewrites which actually happened. No prefilter can change this number, so
 * it is the invariant to check an index against.</li>
 * </ul>
 */
public final class OrderlessHashStats {

  /** Counters for one <code>Orderless</code> head. */
  private static final class Counters {
    final LongAdder calls = new LongAdder();
    final LongAdder hashEvaledSkips = new LongAdder();
    final LongAdder gateRejects = new LongAdder();
    final LongAdder scans = new LongAdder();
    final LongAdder scanArgs = new LongAdder();
    final LongAdder indexRejects = new LongAdder();
    final LongAdder pairs = new LongAdder();
    final LongAdder probeHits = new LongAdder();
    final LongAdder candidates = new LongAdder();
    final LongAdder downRules = new LongAdder();
    final LongAdder fires = new LongAdder();
  }

  private static final String[] NAMES = {"Plus", "Times", "other"};

  private static final Counters[] COUNTERS =
      new Counters[] {new Counters(), new Counters(), new Counters()};

  static {
    if (Config.ORDERLESS_HASH_STATISTICS) {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("=== OrderlessHashStats ===");
        System.out.println(summary());
      }));
    }
  }

  private OrderlessHashStats() {}

  private static Counters counters(IExpr head) {
    if (head == S.Plus) {
      return COUNTERS[0];
    }
    if (head == S.Times) {
      return COUNTERS[1];
    }
    return COUNTERS[2];
  }

  /** One call of <code>evaluateRepeated</code> or <code>evaluateRepeatedNoCache</code>. */
  public static void call(IExpr head) {
    counters(head).calls.increment();
  }

  /** One call which was answered by the <code>IS_HASH_EVALED</code> flag. */
  public static void hashEvaledSkip(IExpr head) {
    counters(head).hashEvaledSkips.increment();
  }

  /** One call rejected by <code>exists2ASTArguments</code>. */
  public static void gateReject(IExpr head) {
    counters(head).gateRejects.increment();
  }

  /**
   * One call of <code>evaluateHashedValues</code>.
   *
   * @param argSize number of arguments the pair loop runs over
   */
  public static void scan(IExpr head, int argSize) {
    Counters c = counters(head);
    c.scans.increment();
    c.scanArgs.add(argSize);
  }

  /** One scan for which the {@link OrderlessPairIndex} excluded every argument. */
  public static void indexReject(IExpr head) {
    counters(head).indexRejects.increment();
  }

  /** One argument pair which reached the rule map lookup. */
  public static void pair(IExpr head) {
    counters(head).pairs.increment();
  }

  /** One rule map lookup which returned a rule list. */
  public static void probeHit(IExpr head, int ruleCount) {
    Counters c = counters(head);
    c.probeHits.increment();
    c.candidates.add(ruleCount);
  }

  /** One <code>updateHashValues</code> call, i.e. one <code>evalDownRule</code> attempt. */
  public static void downRule(IExpr head, boolean fired) {
    Counters c = counters(head);
    c.downRules.increment();
    if (fired) {
      c.fires.increment();
    }
  }

  /** Argument pairs looked up in a rule map, over all heads. */
  public static long pairsProbed() {
    long sum = 0;
    for (int i = 0; i < COUNTERS.length; i++) {
      sum += COUNTERS[i].pairs.sum();
    }
    return sum;
  }

  /** Rule map lookups which returned a rule list, over all heads. */
  public static long probeHits() {
    long sum = 0;
    for (int i = 0; i < COUNTERS.length; i++) {
      sum += COUNTERS[i].probeHits.sum();
    }
    return sum;
  }

  /** Rewrites which actually happened, over all heads. */
  public static long fires() {
    long sum = 0;
    for (int i = 0; i < COUNTERS.length; i++) {
      sum += COUNTERS[i].fires.sum();
    }
    return sum;
  }

  public static void reset() {
    for (int i = 0; i < COUNTERS.length; i++) {
      COUNTERS[i] = new Counters();
    }
  }

  public static String summary() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < COUNTERS.length; i++) {
      Counters c = COUNTERS[i];
      long calls = c.calls.sum();
      if (calls == 0) {
        continue;
      }
      long scans = c.scans.sum();
      long pairs = c.pairs.sum();
      buf.append(NAMES[i]).append(":\n");
      buf.append("  calls           ").append(calls).append('\n');
      buf.append("  IS_HASH_EVALED  ").append(c.hashEvaledSkips.sum()).append('\n');
      buf.append("  gate rejects    ").append(c.gateRejects.sum()).append('\n');
      buf.append("  scans           ").append(scans).append(" (").append(percent(scans, calls))
          .append(" of calls)").append('\n');
      buf.append("  scan args       ").append(c.scanArgs.sum());
      if (scans > 0) {
        buf.append(" (avg ").append(String.format("%.2f", c.scanArgs.sum() / (double) scans))
            .append(')');
      }
      buf.append('\n');
      buf.append("  index rejects   ").append(c.indexRejects.sum()).append(" (")
          .append(percent(c.indexRejects.sum(), scans)).append(" of scans)").append('\n');
      buf.append("  pairs probed    ").append(pairs).append('\n');
      buf.append("  probe hits      ").append(c.probeHits.sum()).append(" (")
          .append(percent(c.probeHits.sum(), pairs)).append(')').append('\n');
      buf.append("  rule candidates ").append(c.candidates.sum()).append('\n');
      buf.append("  evalDownRule    ").append(c.downRules.sum()).append('\n');
      buf.append("  fires           ").append(c.fires.sum()).append('\n');
    }
    return buf.toString();
  }

  private static String percent(long part, long total) {
    return total == 0 ? "-" : String.format("%.2f%%", 100.0 * part / total);
  }
}
