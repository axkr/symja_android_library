package org.matheclipse.core.optimization;

/**
 * Maximum flows and minimum cost flows on integer networks.
 *
 * <p>
 * The implementation lives in the optional <code>matheclipse-or-tools</code> module (the
 * push-relabel algorithms of OR-Tools), installed like {@link ILinearOptimizer}. The graph
 * functions of <code>matheclipse-graphtheory</code> use it for the option
 * <code>Method -&gt; "ORTools"</code>.
 *
 * <p>
 * A network is given by parallel arrays: arc <code>a</code> goes from node <code>tails[a]</code>
 * to node <code>heads[a]</code>; nodes are numbered from <code>0</code>.
 */
public interface IGraphFlowSolver {

  /** The installed solver, or <code>null</code>. */
  IGraphFlowSolver[] INSTANCE = new IGraphFlowSolver[1];

  /** Install the solver. Called from <code>org.matheclipse.ortools.OrToolsInit</code>. */
  static void install(IGraphFlowSolver solver) {
    INSTANCE[0] = solver;
  }

  /** @return the installed solver, or <code>null</code> */
  static IGraphFlowSolver get() {
    return INSTANCE[0];
  }

  /** @return <code>true</code> if a solver is installed */
  static boolean isAvailable() {
    return INSTANCE[0] != null;
  }

  /** The outcome of a solve. */
  enum Status {
    OPTIMAL, INFEASIBLE, UNBALANCED, BAD_INPUT, BAD_RESULT, POSSIBLE_OVERFLOW
  }

  /** The result of a maximum flow or a minimum cost flow. */
  final class FlowResult {
    public final Status status;
    /** the flow value of a maximum flow, the cost of a minimum cost flow */
    public final long value;
    /** the flow on each arc; <code>null</code> unless OPTIMAL */
    public final long[] arcFlow;

    public FlowResult(Status status, long value, long[] arcFlow) {
      this.status = status;
      this.value = value;
      this.arcFlow = arcFlow;
    }
  }

  /** A maximum flow from <code>source</code> to <code>sink</code>. */
  FlowResult maxFlow(int numNodes, int[] tails, int[] heads, long[] capacities, int source,
      int sink);

  /**
   * A flow of minimum cost which meets the supplies: <code>supplies[n] &gt; 0</code> is a supply,
   * <code>supplies[n] &lt; 0</code> a demand, and the supplies sum to <code>0</code>.
   */
  FlowResult minCostFlow(int numNodes, int[] tails, int[] heads, long[] capacities,
      long[] unitCosts, long[] supplies);
}
