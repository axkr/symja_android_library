package org.matheclipse.ortools.symja;

import org.matheclipse.core.optimization.ILinearOptimizer;
import org.matheclipse.ortools.linearsolver.MPConstraint;
import org.matheclipse.ortools.linearsolver.MPObjective;
import org.matheclipse.ortools.linearsolver.MPSolver;
import org.matheclipse.ortools.linearsolver.MPVariable;

/**
 * {@link ILinearOptimizer} with the <code>MPSolver</code> of the OR-Tools port: the Glop simplex
 * for a linear program, the branch and bound over Glop for a mixed integer program.
 *
 * <p>
 * Glop's presolve sometimes proves only that a problem is "infeasible or unbounded", which
 * <code>MPSolver</code> reports as <code>INFEASIBLE</code>. Such a result is checked by solving the
 * same constraints with a zero objective, which can't be unbounded: if that problem has a
 * solution, the original one is unbounded. For a mixed integer problem only an unbounded linear
 * relaxation can hide behind <code>INFEASIBLE</code>, so the relaxation is checked first and the
 * second branch and bound runs only if it is unbounded.
 */
public class OrToolsLinearOptimizer implements ILinearOptimizer {

  @Override
  public Result solve(Problem problem) {
    Result result = solve(problem, false);
    if (result.status != Status.INFEASIBLE) {
      return result;
    }
    if (problem.hasIntegerVariables()) {
      Problem relaxation = new Problem(problem.objective, problem.offset, problem.maximize,
          problem.rows, problem.rowLower, problem.rowUpper, problem.varLower, problem.varUpper,
          null, problem.timeLimit);
      if (solve(relaxation).status != Status.UNBOUNDED) {
        // an infeasible or bounded relaxation: the branch and bound proved infeasibility
        return result;
      }
    }
    Result feasibility = solve(problem, true);
    if (feasibility.hasSolution()) {
      return new Result(Status.UNBOUNDED, null, Double.NaN);
    }
    return result;
  }

  /**
   * @param zeroObjective <code>true</code> to only search a point which satisfies the constraints
   */
  private static Result solve(Problem problem, boolean zeroObjective) {
    final int n = problem.numVariables();
    final boolean mip = problem.hasIntegerVariables();
    MPSolver solver = MPSolver.createSolver(mip ? "SCIP" : "GLOP");
    if (solver == null) {
      return new Result(Status.ABNORMAL, null, Double.NaN);
    }
    MPVariable[] x = new MPVariable[n];
    for (int j = 0; j < n; j++) {
      double lower = problem.varLower[j];
      double upper = problem.varUpper[j];
      x[j] = mip && problem.integer[j] ? solver.makeIntVar(lower, upper, "x" + j)
          : solver.makeNumVar(lower, upper, "x" + j);
    }
    for (int i = 0; i < problem.rows.length; i++) {
      MPConstraint c = solver.makeConstraint(problem.rowLower[i], problem.rowUpper[i], "c" + i);
      double[] row = problem.rows[i];
      for (int j = 0; j < n; j++) {
        if (row[j] != 0.0) {
          c.setCoefficient(x[j], row[j]);
        }
      }
    }
    MPObjective objective = solver.objective();
    if (!zeroObjective) {
      for (int j = 0; j < n; j++) {
        if (problem.objective[j] != 0.0) {
          objective.setCoefficient(x[j], problem.objective[j]);
        }
      }
      objective.setOffset(problem.offset);
    }
    if (problem.maximize) {
      objective.setMaximization();
    } else {
      objective.setMinimization();
    }
    if (problem.timeLimit > 0.0 && problem.timeLimit < Double.POSITIVE_INFINITY) {
      solver.setTimeLimit((long) Math.ceil(problem.timeLimit * 1000.0));
    }
    MPSolver.ResultStatus status = solver.solve();
    switch (status) {
      case OPTIMAL:
      case FEASIBLE: {
        double[] values = new double[n];
        for (int j = 0; j < n; j++) {
          values[j] = x[j].solutionValue();
        }
        return new Result(status == MPSolver.ResultStatus.OPTIMAL ? Status.OPTIMAL
            : Status.FEASIBLE, values, objective.value());
      }
      case INFEASIBLE:
        return new Result(Status.INFEASIBLE, null, Double.NaN);
      case UNBOUNDED:
        return new Result(Status.UNBOUNDED, null, Double.NaN);
      case NOT_SOLVED:
        return new Result(Status.NOT_SOLVED, null, Double.NaN);
      default:
        return new Result(Status.ABNORMAL, null, Double.NaN);
    }
  }
}
