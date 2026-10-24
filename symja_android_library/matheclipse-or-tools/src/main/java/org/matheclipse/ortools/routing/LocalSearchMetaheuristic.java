package org.matheclipse.ortools.routing;

/**
 * The local search metaheuristics of the routing solver, shaped like the protocol buffer message
 * <code>operations_research.routing.LocalSearchMetaheuristic</code> of the OR-Tools Java wrapper
 * (the nested enum {@link Value} carries the same constant names and numbers).
 *
 * <p>
 * This class reimplements the API for the simplified routing engine of this module:
 * <code>GREEDY_DESCENT</code> (also used for <code>AUTOMATIC</code> and <code>UNSET</code>) stops
 * at the first local optimum; <code>GUIDED_LOCAL_SEARCH</code> continues with arc penalties until
 * a limit is reached, and <code>SIMULATED_ANNEALING</code>, <code>TABU_SEARCH</code> and
 * <code>GENERIC_TABU_SEARCH</code> run the guided local search.
 */
public final class LocalSearchMetaheuristic {

  private LocalSearchMetaheuristic() {}

  /** The metaheuristics. */
  public enum Value {
    /** Not set: same as AUTOMATIC. */
    UNSET(0),
    /** Lets the solver choose (greedy descent). */
    AUTOMATIC(6),
    /** Accepts improving moves until a local optimum is reached. */
    GREEDY_DESCENT(1),
    /** Guided local search. */
    GUIDED_LOCAL_SEARCH(2),
    /** Simulated annealing (guided local search in this implementation). */
    SIMULATED_ANNEALING(3),
    /** Tabu search (guided local search in this implementation). */
    TABU_SEARCH(4),
    /** Generic tabu search (guided local search in this implementation). */
    GENERIC_TABU_SEARCH(5),
    /** An unknown number. */
    UNRECOGNIZED(-1);

    /** The number of UNSET. */
    public static final int UNSET_VALUE = 0;

    /** The number of AUTOMATIC. */
    public static final int AUTOMATIC_VALUE = 6;

    /** The number of GREEDY_DESCENT. */
    public static final int GREEDY_DESCENT_VALUE = 1;

    /** The number of GUIDED_LOCAL_SEARCH. */
    public static final int GUIDED_LOCAL_SEARCH_VALUE = 2;

    /** The number of SIMULATED_ANNEALING. */
    public static final int SIMULATED_ANNEALING_VALUE = 3;

    /** The number of TABU_SEARCH. */
    public static final int TABU_SEARCH_VALUE = 4;

    /** The number of GENERIC_TABU_SEARCH. */
    public static final int GENERIC_TABU_SEARCH_VALUE = 5;

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
