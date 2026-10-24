package org.matheclipse.ortools.routing;

/**
 * The status of a routing search, shaped like the protocol buffer message
 * <code>operations_research.routing.RoutingSearchStatus</code> of the OR-Tools Java wrapper (the
 * nested enum {@link Value} carries the same constant names and numbers). Reimplemented for the
 * simplified routing engine of this module.
 */
public final class RoutingSearchStatus {

  private RoutingSearchStatus() {}

  /** The status values. */
  public enum Value {
    /** No search has been run yet. */
    ROUTING_NOT_SOLVED(0),
    /** A solution has been found. */
    ROUTING_SUCCESS(1),
    /** A solution has been found, but the local search did not reach a local optimum. */
    ROUTING_PARTIAL_SUCCESS_LOCAL_OPTIMUM_NOT_REACHED(2),
    /** No solution has been found. */
    ROUTING_FAIL(3),
    /** The time limit was reached before a solution was found. */
    ROUTING_FAIL_TIMEOUT(4),
    /** The model or the search parameters are invalid. */
    ROUTING_INVALID(5),
    /** The problem has been proven infeasible. */
    ROUTING_INFEASIBLE(6),
    /** The solution is optimal. */
    ROUTING_OPTIMAL(7),
    /** An unknown number. */
    UNRECOGNIZED(-1);

    /** The number of ROUTING_NOT_SOLVED. */
    public static final int ROUTING_NOT_SOLVED_VALUE = 0;

    /** The number of ROUTING_SUCCESS. */
    public static final int ROUTING_SUCCESS_VALUE = 1;

    /** The number of ROUTING_PARTIAL_SUCCESS_LOCAL_OPTIMUM_NOT_REACHED. */
    public static final int ROUTING_PARTIAL_SUCCESS_LOCAL_OPTIMUM_NOT_REACHED_VALUE = 2;

    /** The number of ROUTING_FAIL. */
    public static final int ROUTING_FAIL_VALUE = 3;

    /** The number of ROUTING_FAIL_TIMEOUT. */
    public static final int ROUTING_FAIL_TIMEOUT_VALUE = 4;

    /** The number of ROUTING_INVALID. */
    public static final int ROUTING_INVALID_VALUE = 5;

    /** The number of ROUTING_INFEASIBLE. */
    public static final int ROUTING_INFEASIBLE_VALUE = 6;

    /** The number of ROUTING_OPTIMAL. */
    public static final int ROUTING_OPTIMAL_VALUE = 7;

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
