package org.matheclipse.core.optimization;

/**
 * Traveling salesman tours with the routing library of OR-Tools.
 *
 * <p>
 * The implementation lives in the optional <code>matheclipse-or-tools</code> module, installed
 * like {@link ILinearOptimizer}. <code>FindShortestTour</code> uses it for the option
 * <code>Method -&gt; "ORTools"</code>. The routing search is a heuristic: the tour is short, but
 * for larger inputs not necessarily the shortest.
 */
public interface IRoutingSolver {

  /** The installed solver, or <code>null</code>. */
  IRoutingSolver[] INSTANCE = new IRoutingSolver[1];

  /** Install the solver. Called from <code>org.matheclipse.ortools.OrToolsInit</code>. */
  static void install(IRoutingSolver solver) {
    INSTANCE[0] = solver;
  }

  /** @return the installed solver, or <code>null</code> */
  static IRoutingSolver get() {
    return INSTANCE[0];
  }

  /** @return <code>true</code> if a solver is installed */
  static boolean isAvailable() {
    return INSTANCE[0] != null;
  }

  /**
   * A closed tour through all nodes <code>0..n-1</code>.
   *
   * @param distances <code>distances[i][j]</code> is the non-negative cost of going from
   *        <code>i</code> to <code>j</code>
   * @param timeLimit the time limit of the search in seconds
   * @return the nodes in the order of the tour, starting with <code>0</code> and without
   *         repeating it at the end; <code>null</code> if no tour was found
   */
  int[] shortestTour(long[][] distances, double timeLimit);
}
