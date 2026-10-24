package org.matheclipse.ortools.routing;

/**
 * The first solution strategies of the routing solver, shaped like the protocol buffer message
 * <code>operations_research.routing.FirstSolutionStrategy</code> of the OR-Tools Java wrapper (the
 * nested enum {@link Value} carries the same constant names and numbers).
 *
 * <p>
 * This class reimplements the API for the simplified routing engine of this module, which
 * implements <code>PATH_CHEAPEST_ARC</code>, <code>SAVINGS</code>,
 * <code>PARALLEL_CHEAPEST_INSERTION</code>, <code>LOCAL_CHEAPEST_INSERTION</code> and
 * <code>ALL_UNPERFORMED</code>; <code>PARALLEL_SAVINGS</code> is run as <code>SAVINGS</code>, the
 * other insertion strategies as <code>PARALLEL_CHEAPEST_INSERTION</code>, the remaining ones as
 * <code>PATH_CHEAPEST_ARC</code>. <code>AUTOMATIC</code> selects the parallel cheapest insertion
 * for models with pickups and deliveries and the path cheapest arc otherwise.
 */
public final class FirstSolutionStrategy {

  private FirstSolutionStrategy() {}

  /** The strategies. */
  public enum Value {
    /** Not set: same as AUTOMATIC. */
    UNSET(0),
    /** Lets the solver choose. */
    AUTOMATIC(15),
    /** Extends routes by the cheapest arc from their last node. */
    PATH_CHEAPEST_ARC(3),
    /** Extends routes by the most constrained arc (cheapest arc in this implementation). */
    PATH_MOST_CONSTRAINED_ARC(4),
    /** Uses the first solution evaluator (cheapest arc in this implementation). */
    EVALUATOR_STRATEGY(5),
    /** Clarke and Wright savings. */
    SAVINGS(10),
    /** Parallel savings (sequential savings in this implementation). */
    PARALLEL_SAVINGS(17),
    /** Sweep (cheapest arc in this implementation: no coordinates are known). */
    SWEEP(11),
    /** Christofides (cheapest arc in this implementation). */
    CHRISTOFIDES(13),
    /** All optional nodes unperformed. */
    ALL_UNPERFORMED(6),
    /** Best insertion (parallel cheapest insertion in this implementation). */
    BEST_INSERTION(7),
    /** Parallel cheapest insertion. */
    PARALLEL_CHEAPEST_INSERTION(8),
    /** Sequential cheapest insertion (parallel cheapest insertion in this implementation). */
    SEQUENTIAL_CHEAPEST_INSERTION(14),
    /** Local cheapest insertion. */
    LOCAL_CHEAPEST_INSERTION(9),
    /** Local cheapest cost insertion (parallel cheapest insertion in this implementation). */
    LOCAL_CHEAPEST_COST_INSERTION(16),
    /** Global cheapest arc (cheapest arc in this implementation). */
    GLOBAL_CHEAPEST_ARC(1),
    /** Local cheapest arc (cheapest arc in this implementation). */
    LOCAL_CHEAPEST_ARC(2),
    /** First unbound variable with minimal value (cheapest arc in this implementation). */
    FIRST_UNBOUND_MIN_VALUE(12),
    /** An unknown number. */
    UNRECOGNIZED(-1);

    /** The number of UNSET. */
    public static final int UNSET_VALUE = 0;

    /** The number of AUTOMATIC. */
    public static final int AUTOMATIC_VALUE = 15;

    /** The number of PATH_CHEAPEST_ARC. */
    public static final int PATH_CHEAPEST_ARC_VALUE = 3;

    /** The number of PATH_MOST_CONSTRAINED_ARC. */
    public static final int PATH_MOST_CONSTRAINED_ARC_VALUE = 4;

    /** The number of EVALUATOR_STRATEGY. */
    public static final int EVALUATOR_STRATEGY_VALUE = 5;

    /** The number of SAVINGS. */
    public static final int SAVINGS_VALUE = 10;

    /** The number of PARALLEL_SAVINGS. */
    public static final int PARALLEL_SAVINGS_VALUE = 17;

    /** The number of SWEEP. */
    public static final int SWEEP_VALUE = 11;

    /** The number of CHRISTOFIDES. */
    public static final int CHRISTOFIDES_VALUE = 13;

    /** The number of ALL_UNPERFORMED. */
    public static final int ALL_UNPERFORMED_VALUE = 6;

    /** The number of BEST_INSERTION. */
    public static final int BEST_INSERTION_VALUE = 7;

    /** The number of PARALLEL_CHEAPEST_INSERTION. */
    public static final int PARALLEL_CHEAPEST_INSERTION_VALUE = 8;

    /** The number of SEQUENTIAL_CHEAPEST_INSERTION. */
    public static final int SEQUENTIAL_CHEAPEST_INSERTION_VALUE = 14;

    /** The number of LOCAL_CHEAPEST_INSERTION. */
    public static final int LOCAL_CHEAPEST_INSERTION_VALUE = 9;

    /** The number of LOCAL_CHEAPEST_COST_INSERTION. */
    public static final int LOCAL_CHEAPEST_COST_INSERTION_VALUE = 16;

    /** The number of GLOBAL_CHEAPEST_ARC. */
    public static final int GLOBAL_CHEAPEST_ARC_VALUE = 1;

    /** The number of LOCAL_CHEAPEST_ARC. */
    public static final int LOCAL_CHEAPEST_ARC_VALUE = 2;

    /** The number of FIRST_UNBOUND_MIN_VALUE. */
    public static final int FIRST_UNBOUND_MIN_VALUE_VALUE = 12;

    private final int number;

    Value(int number) {
      this.number = number;
    }

    /** The protobuf number of the value. */
    public int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
      }
      return number;
    }

    /** The value with the given number, or null. */
    public static Value forNumber(int value) {
      for (Value v : values()) {
        if (v != UNRECOGNIZED && v.number == value) {
          return v;
        }
      }
      return null;
    }

    /** Same as {@link #forNumber(int)}. */
    @Deprecated
    public static Value valueOf(int value) {
      return forNumber(value);
    }
  }
}
