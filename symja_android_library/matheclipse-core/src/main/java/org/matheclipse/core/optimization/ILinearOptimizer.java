package org.matheclipse.core.optimization;

/**
 * Solves linear programs and mixed integer linear programs.
 *
 * <p>
 * The implementation lives in the optional <code>matheclipse-or-tools</code> module, which
 * <code>matheclipse-core</code> must not depend on, so the dependency is inverted:
 * <code>org.matheclipse.ortools.OrToolsInit#init()</code> installs an implementation here and
 * core calls it through this interface. The same pattern
 * {@link org.matheclipse.core.compile.IExprCompiler} uses for <code>matheclipse-compile</code>.
 *
 * <p>
 * The functions <code>LinearProgramming</code>, <code>LinearOptimization</code>,
 * <code>Minimize</code>, <code>Maximize</code>, <code>NMinimize</code> and
 * <code>NMaximize</code> use it for the option <code>Method -&gt; "ORTools"</code>. Without an
 * installed implementation they keep their own algorithms.
 */
public interface ILinearOptimizer {

  /**
   * The installed solver, or <code>null</code> when <code>matheclipse-or-tools</code> is not
   * present.
   */
  ILinearOptimizer[] INSTANCE = new ILinearOptimizer[1];

  /** Install the solver. Called from <code>org.matheclipse.ortools.OrToolsInit</code>. */
  static void install(ILinearOptimizer optimizer) {
    INSTANCE[0] = optimizer;
  }

  /** @return the installed solver, or <code>null</code> */
  static ILinearOptimizer get() {
    return INSTANCE[0];
  }

  /** @return <code>true</code> if a solver is installed */
  static boolean isAvailable() {
    return INSTANCE[0] != null;
  }

  /** The outcome of a solve. */
  enum Status {
    /** The solution is optimal. */
    OPTIMAL,
    /** A feasible solution was found, but a limit stopped the search before optimality. */
    FEASIBLE,
    /** No point satisfies the constraints. */
    INFEASIBLE,
    /** The objective is unbounded in the optimization direction. */
    UNBOUNDED,
    /** The solver failed, for example from numerical trouble. */
    ABNORMAL,
    /** A limit stopped the search before any feasible solution was found. */
    NOT_SOLVED
  }

  /**
   * The problem
   *
   * <pre>
   * minimize or maximize  objective . x + offset
   * subject to            rowLower[i] &lt;= rows[i] . x &lt;= rowUpper[i]
   *                       varLower[j] &lt;= x[j] &lt;= varUpper[j]
   *                       x[j] integer if integer[j]
   * </pre>
   *
   * Missing bounds are <code>Double.NEGATIVE_INFINITY</code> or
   * <code>Double.POSITIVE_INFINITY</code>; an equality row has equal lower and upper bounds.
   */
  final class Problem {
    public final double[] objective;
    public final double offset;
    public final boolean maximize;
    public final double[][] rows;
    public final double[] rowLower;
    public final double[] rowUpper;
    public final double[] varLower;
    public final double[] varUpper;
    /** <code>null</code> for a pure linear program */
    public final boolean[] integer;
    /** the time limit in seconds, <code>Double.POSITIVE_INFINITY</code> for none */
    public final double timeLimit;

    public Problem(double[] objective, double offset, boolean maximize, double[][] rows,
        double[] rowLower, double[] rowUpper, double[] varLower, double[] varUpper,
        boolean[] integer, double timeLimit) {
      this.objective = objective;
      this.offset = offset;
      this.maximize = maximize;
      this.rows = rows;
      this.rowLower = rowLower;
      this.rowUpper = rowUpper;
      this.varLower = varLower;
      this.varUpper = varUpper;
      this.integer = integer;
      this.timeLimit = timeLimit;
    }

    /** The number of variables. */
    public int numVariables() {
      return objective.length;
    }

    /** Whether at least one variable has to be integer. */
    public boolean hasIntegerVariables() {
      if (integer != null) {
        for (boolean b : integer) {
          if (b) {
            return true;
          }
        }
      }
      return false;
    }
  }

  /** The result of {@link ILinearOptimizer#solve(Problem)}. */
  final class Result {
    public final Status status;
    /** the values of the variables; <code>null</code> unless OPTIMAL or FEASIBLE */
    public final double[] values;
    /** the objective value including the offset */
    public final double objectiveValue;

    public Result(Status status, double[] values, double objectiveValue) {
      this.status = status;
      this.values = values;
      this.objectiveValue = objectiveValue;
    }

    /** Whether {@link #values} holds a solution. */
    public boolean hasSolution() {
      return values != null && (status == Status.OPTIMAL || status == Status.FEASIBLE);
    }
  }

  /**
   * Solve the problem.
   *
   * @param problem the linear or mixed integer linear program
   * @return the result; never <code>null</code>
   */
  Result solve(Problem problem);
}
