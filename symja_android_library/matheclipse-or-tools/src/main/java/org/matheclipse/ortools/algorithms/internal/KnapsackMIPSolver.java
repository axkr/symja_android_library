// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from ortools/algorithms/knapsack_solver.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.algorithms.internal;

import org.matheclipse.ortools.linearsolver.MPConstraint;
import org.matheclipse.ortools.linearsolver.MPObjective;
import org.matheclipse.ortools.linearsolver.MPSolver;
import org.matheclipse.ortools.linearsolver.MPVariable;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * The knapsack solver of the MIP solver types (CBC, SCIP, ...). It solves the multi-dimensional
 * problem with any number of items.
 *
 * <p>
 * Like upstream it does not override
 * {@link BaseKnapsackSolver#getLowerAndUpperBoundWhenItem(int, boolean, long[])}, so the
 * reduction of {@link org.matheclipse.ortools.algorithms.KnapsackSolver} never fixes items for
 * these solver types.
 */
public final class KnapsackMIPSolver extends BaseKnapsackSolver {
  /** The MIP backends of the knapsack solver types. */
  public enum ProblemType {
    /** <code>MPSolver::CBC_MIXED_INTEGER_PROGRAMMING</code>. */
    CBC_MIXED_INTEGER_PROGRAMMING,
    /** <code>MPSolver::SCIP_MIXED_INTEGER_PROGRAMMING</code>. */
    SCIP_MIXED_INTEGER_PROGRAMMING,
    /** <code>MPSolver::XPRESS_MIXED_INTEGER_PROGRAMMING</code>. */
    XPRESS_MIXED_INTEGER_PROGRAMMING,
    /** <code>MPSolver::CPLEX_MIXED_INTEGER_PROGRAMMING</code>. */
    CPLEX_MIXED_INTEGER_PROGRAMMING
  }

  private final ProblemType problemType;
  private long[] profits = new long[0];
  private long[][] weights = new long[0][];
  private long[] capacities = new long[0];
  private boolean[] bestSolution = new boolean[0];

  /** Creates the solver for the given MIP backend. */
  public KnapsackMIPSolver(ProblemType problemType, String solverName) {
    super(solverName);
    this.problemType = problemType;
  }

  /** The MIP backend requested at construction. */
  public ProblemType problemType() {
    return problemType;
  }

  @Override
  public void init(long[] profits, long[][] weights, long[] capacities) {
    if (weights.length != capacities.length) {
      throw new IllegalArgumentException("Weights should be vector of num_dimensions ("
          + capacities.length + ") vectors of size num_items (" + profits.length + ").");
    }
    // only the data is stored; the model is built in solve()
    this.profits = profits.clone();
    this.weights = new long[weights.length][];
    for (int i = 0; i < weights.length; ++i) {
      this.weights[i] = weights[i].clone();
    }
    this.capacities = capacities.clone();
    bestSolution = new boolean[profits.length];
  }

  @Override
  public long solve(TimeLimit timeLimit, double timeLimitInSeconds, boolean[] isSolutionOptimal) {
    // One Boolean variable per item, one row 0 <= sum_j weights[i][j] * x_j <= capacities[i] per
    // dimension and the objective "minimize sum_j -profits[j] * x_j": upstream minimizes because
    // CBC does not always find the optimal solution of the maximization.
    MPSolver solver =
        new MPSolver(getName(), MPSolver.OptimizationProblemType.valueOf(problemType.name()));
    final int numItems = profits.length;
    MPVariable[] x = new MPVariable[numItems];
    for (int j = 0; j < numItems; ++j) {
      x[j] = solver.makeBoolVar("x" + j);
    }
    for (int i = 0; i < weights.length; ++i) {
      MPConstraint ct = solver.makeConstraint(0.0, capacities[i]);
      for (int j = 0; j < numItems; ++j) {
        ct.setCoefficient(x[j], weights[i][j]);
      }
    }
    MPObjective objective = solver.objective();
    for (int j = 0; j < numItems; ++j) {
      objective.setCoefficient(x[j], -profits[j]);
    }
    objective.setMinimization();
    solver.suppressOutput();
    solver.setTimeLimit((long) (timeLimitInSeconds * 1000.0));
    final MPSolver.ResultStatus status = solver.solve();

    if (status == MPSolver.ResultStatus.OPTIMAL || status == MPSolver.ResultStatus.FEASIBLE) {
      isSolutionOptimal[0] = status == MPSolver.ResultStatus.OPTIMAL;
      for (int j = 0; j < numItems; ++j) {
        bestSolution[j] = x[j].solutionValue() >= 0.5;
      }
      // the objective is integral; round it like upstream
      return (long) (-objective.value() + 0.5);
    }
    isSolutionOptimal[0] = false;
    java.util.Arrays.fill(bestSolution, false);
    return 0;
  }

  @Override
  public boolean bestSolution(int itemId) {
    return bestSolution[itemId];
  }
}
