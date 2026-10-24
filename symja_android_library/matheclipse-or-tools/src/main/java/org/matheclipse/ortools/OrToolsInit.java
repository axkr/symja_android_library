package org.matheclipse.ortools;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.optimization.IGraphFlowSolver;
import org.matheclipse.core.optimization.IIntegerConstraintSolver;
import org.matheclipse.core.optimization.ILinearOptimizer;
import org.matheclipse.core.optimization.IRoutingSolver;
import org.matheclipse.ortools.symja.KnapsackSolve;
import org.matheclipse.ortools.symja.OrToolsGraphFlows;
import org.matheclipse.ortools.symja.OrToolsIntegerConstraintSolver;
import org.matheclipse.ortools.symja.OrToolsLinearOptimizer;
import org.matheclipse.ortools.symja.OrToolsRouting;

/**
 * Installs the OR-Tools solvers of the <code>matheclipse-or-tools</code> module into the
 * interfaces of <code>matheclipse-core</code> (package
 * <code>org.matheclipse.core.optimization</code>) and registers the function
 * <code>KnapsackSolve</code>. Call this after <code>F.initSymja()</code>;
 * <code>org.matheclipse.io.IOInit</code> does so when the module is on the classpath.
 *
 * <p>
 * Afterwards these functions use OR-Tools for the option <code>Method -&gt; "ORTools"</code>:
 * <code>LinearProgramming</code>, <code>LinearOptimization</code>, <code>Minimize</code>,
 * <code>Maximize</code>, <code>NMinimize</code>, <code>NMaximize</code> (Glop and branch and
 * bound), <code>Solve</code> and <code>FindInstance</code> over the integers (CP-SAT),
 * <code>FindMaximumFlow</code> and <code>FindMinimumCostFlow</code> (graph flows) and
 * <code>FindShortestTour</code> (routing). Installing twice is harmless.
 */
public final class OrToolsInit {

  private OrToolsInit() {}

  public static void init() {
    ILinearOptimizer.install(new OrToolsLinearOptimizer());
    IIntegerConstraintSolver.install(new OrToolsIntegerConstraintSolver());
    IGraphFlowSolver.install(new OrToolsGraphFlows());
    IRoutingSolver.install(new OrToolsRouting());
    S.KnapsackSolve.setEvaluator(new KnapsackSolve());
  }
}
