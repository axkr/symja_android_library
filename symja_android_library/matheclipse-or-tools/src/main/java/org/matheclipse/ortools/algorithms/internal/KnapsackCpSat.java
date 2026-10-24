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

import org.matheclipse.ortools.sat.BoolVar;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.CpSolver;
import org.matheclipse.ortools.sat.CpSolverStatus;
import org.matheclipse.ortools.sat.LinearExpr;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * The knapsack solver of the CP-SAT solver type. It solves the multi-dimensional problem with any
 * number of items.
 *
 * <p>
 * Like upstream it does not override
 * {@link BaseKnapsackSolver#getLowerAndUpperBoundWhenItem(int, boolean, long[])}, so the
 * reduction of {@link org.matheclipse.ortools.algorithms.KnapsackSolver} never fixes items for
 * this solver type.
 */
public final class KnapsackCpSat extends BaseKnapsackSolver {
  private long[] profits = new long[0];
  private long[][] weights = new long[0][];
  private long[] capacities = new long[0];
  private boolean[] bestSolution = new boolean[0];

  /** Creates the solver. */
  public KnapsackCpSat(String solverName) {
    super(solverName);
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
    // one BoolVar per item, one constraint sum_j weights[i][j] * x_j <= capacities[i] per
    // dimension and the objective "maximize sum_j profits[j] * x_j"
    final int numItems = profits.length;
    CpModel model = new CpModel();
    BoolVar[] x = new BoolVar[numItems];
    for (int j = 0; j < numItems; ++j) {
      x[j] = model.newBoolVar("x" + j);
    }
    for (int i = 0; i < weights.length; ++i) {
      model.addLessOrEqual(LinearExpr.weightedSum(x, weights[i]), capacities[i]);
    }
    model.maximize(LinearExpr.weightedSum(x, profits));
    CpSolver solver = new CpSolver();
    // upstream asks for 16 workers above 100 items, else 8; the Java port searches sequentially
    solver.getParameters().setNumWorkers(numItems > 100 ? 16 : 8);
    if (timeLimitInSeconds < Double.POSITIVE_INFINITY) {
      solver.getParameters().setMaxTimeInSeconds(Math.max(0.0, timeLimitInSeconds));
    }
    final CpSolverStatus status = solver.solve(model);

    if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
      isSolutionOptimal[0] = status == CpSolverStatus.OPTIMAL;
      for (int j = 0; j < numItems; ++j) {
        bestSolution[j] = solver.booleanValue(x[j]);
      }
      return (long) solver.objectiveValue();
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
