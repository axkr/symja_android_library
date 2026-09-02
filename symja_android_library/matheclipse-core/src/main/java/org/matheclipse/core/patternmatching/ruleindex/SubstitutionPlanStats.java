package org.matheclipse.core.patternmatching.ruleindex;

import java.util.concurrent.atomic.AtomicLong;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Counters for the precompiled right-hand-side substitution
 * ({@link org.matheclipse.core.patternmatching.SubstitutionPlan}) and the outcome of the dual run
 * check enabled with {@link Config#SUBSTITUTION_PLAN_VALIDATE}.
 *
 * <p>
 * The counters answer two different questions. {@link #planned()} against {@link #generic()} says
 * how much of the substitution work the plan actually covers, and the {@link #refusals()} breakdown
 * says why the rest is not covered - a refusal reason which suddenly dominates is the signal that a
 * rule set uses a construct the plan builder should learn.
 *
 * <p>
 * Collection is off unless {@link Config#RULE_DISPATCH_STATISTICS} is enabled, so a disabled
 * counter costs one static boolean read. The validation counters are independent of that flag: they
 * are only touched while {@link Config#SUBSTITUTION_PLAN_VALIDATE} runs anyway.
 */
public final class SubstitutionPlanStats {

  /** A right-hand-side the plan builder does not model. */
  public enum Refusal {
    /** Contains a pattern object; the visitor rebuilds pattern objects from substituted symbols. */
    PATTERN_OBJECT,
    /** Contains an association or a data expression, which substitute into their parts. */
    ASSOCIATION_OR_DATA,
    /** Contains <code>OptionValue(...)</code>, whose replacement is engine dependent. */
    OPTION_VALUE,
    /** The whole right-hand-side is a replaceable symbol, which this plan shape cannot express. */
    ROOT_NOT_AST
  }

  private static final AtomicLong PLANS_BUILT = new AtomicLong();

  private static final AtomicLong PLANNED = new AtomicLong();

  private static final AtomicLong GENERIC = new AtomicLong();

  private static final AtomicLong[] REFUSALS;

  static {
    Refusal[] values = Refusal.values();
    REFUSALS = new AtomicLong[values.length];
    for (int i = 0; i < values.length; i++) {
      REFUSALS[i] = new AtomicLong();
    }
  }

  private static final AtomicLong CHECKED = new AtomicLong();

  private static final AtomicLong MISMATCHED = new AtomicLong();

  private static volatile String firstMismatch = null;

  private SubstitutionPlanStats() {}

  /** One right-hand-side compiled into a plan. */
  public static void planBuilt() {
    if (Config.RULE_DISPATCH_STATISTICS) {
      PLANS_BUILT.incrementAndGet();
    }
  }

  /**
   * One right-hand-side the plan builder declined. Counted even without
   * {@link Config#RULE_DISPATCH_STATISTICS}, because a refusal happens once per rule and the
   * breakdown is the diagnostic which explains a disappointing {@link #planned()} share.
   *
   * @param reason why the right-hand-side was declined
   */
  public static void planRefused(Refusal reason) {
    REFUSALS[reason.ordinal()].incrementAndGet();
  }

  /** Record one substitution done by a plan. */
  public static void plannedSubstitution() {
    if (Config.RULE_DISPATCH_STATISTICS) {
      PLANNED.incrementAndGet();
    }
  }

  /** Record one substitution done by the generic visitor. */
  public static void genericSubstitution() {
    if (Config.RULE_DISPATCH_STATISTICS) {
      GENERIC.incrementAndGet();
    }
  }

  /**
   * Record the outcome of one dual run comparison.
   *
   * @param equal <code>true</code> if the planned result equals the generic one, evaluation flags
   *        included
   * @param planResult what the plan produced
   * @param genericResult what the generic substitution produced, which is also what is returned
   */
  public static void checked(boolean equal, IExpr planResult, IExpr genericResult) {
    CHECKED.incrementAndGet();
    if (!equal) {
      MISMATCHED.incrementAndGet();
      if (firstMismatch == null) {
        firstMismatch = "plan:    " + planResult + "\n  generic: " + genericResult;
        System.err.println("SubstitutionPlan produced a different result!\n  " + firstMismatch);
      }
    }
  }

  public static long plansBuilt() {
    return PLANS_BUILT.get();
  }

  public static long planned() {
    return PLANNED.get();
  }

  public static long generic() {
    return GENERIC.get();
  }

  public static long refusals(Refusal reason) {
    return REFUSALS[reason.ordinal()].get();
  }

  /** Total number of declined right-hand-sides. */
  public static long refusals() {
    long sum = 0;
    for (AtomicLong counter : REFUSALS) {
      sum += counter.get();
    }
    return sum;
  }

  public static long checked() {
    return CHECKED.get();
  }

  public static long mismatched() {
    return MISMATCHED.get();
  }

  public static String firstMismatch() {
    return firstMismatch;
  }

  public static void reset() {
    PLANS_BUILT.set(0);
    PLANNED.set(0);
    GENERIC.set(0);
    for (AtomicLong counter : REFUSALS) {
      counter.set(0);
    }
    CHECKED.set(0);
    MISMATCHED.set(0);
    firstMismatch = null;
  }

  public static String summary() {
    StringBuilder buf = new StringBuilder();
    buf.append("plansBuilt=").append(PLANS_BUILT.get());
    buf.append(" planned=").append(PLANNED.get());
    buf.append(" generic=").append(GENERIC.get());
    buf.append(" refused=").append(refusals());
    for (Refusal reason : Refusal.values()) {
      long count = refusals(reason);
      if (count > 0) {
        buf.append(' ').append(reason).append('=').append(count);
      }
    }
    if (CHECKED.get() > 0) {
      buf.append(" checked=").append(CHECKED.get()).append(" mismatched=").append(MISMATCHED.get());
    }
    return buf.toString();
  }
}
