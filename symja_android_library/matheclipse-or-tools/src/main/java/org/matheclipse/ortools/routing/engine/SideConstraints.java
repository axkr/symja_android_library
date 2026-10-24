package org.matheclipse.ortools.routing.engine;

/**
 * Constraints which the user posted on the routing variables through
 * <code>Model.solver()</code> (for example <code>vehicleVar(p) == vehicleVar(d)</code> or a
 * cumulative over interval variables built on cumul variables). The simplified routing engine of
 * this module, which reimplements the behaviour of the OR-Tools routing library, can't reason
 * about them itself and asks this checker whether a (possibly partial) solution satisfies them.
 */
public interface SideConstraints {

  /** Status of an index which is visited. */
  byte ACTIVE = 1;

  /** Status of an index which is not visited. */
  byte INACTIVE = 0;

  /** Status of an index which a first solution heuristic has not decided yet. */
  byte UNDECIDED = 2;

  /**
   * Checks the solution given by the routes (each from start to end index) and the status of
   * every index.
   *
   * @param routes the index sequence of every vehicle, including start and end
   * @param status the status of every index
   * @param fixedCumuls if not null, receives per dimension and index the cumul value chosen by
   *     the check (<code>Long.MIN_VALUE</code> where nothing was chosen); requests a complete
   *     search for values instead of propagation only
   * @return whether the constraints can be satisfied
   */
  boolean check(int[][] routes, byte[] status, long[][] fixedCumuls);

  /** Whether the check depends on the cumuls of dimension <code>d</code>. */
  boolean usesDimension(int d);
}
