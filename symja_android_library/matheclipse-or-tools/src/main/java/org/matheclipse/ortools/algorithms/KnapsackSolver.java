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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/algorithms/knapsack_solver.h/.cc C++ to Java, API adapted from the OR-Tools Java wrapper, see NOTICE in the module root.

package org.matheclipse.ortools.algorithms;

import org.matheclipse.ortools.algorithms.internal.BaseKnapsackSolver;
import org.matheclipse.ortools.algorithms.internal.Knapsack64ItemsSolver;
import org.matheclipse.ortools.algorithms.internal.KnapsackBruteForceSolver;
import org.matheclipse.ortools.algorithms.internal.KnapsackCpSat;
import org.matheclipse.ortools.algorithms.internal.KnapsackDivideAndConquerSolver;
import org.matheclipse.ortools.algorithms.internal.KnapsackDynamicProgrammingSolver;
import org.matheclipse.ortools.algorithms.internal.KnapsackGenericSolver;
import org.matheclipse.ortools.algorithms.internal.KnapsackMIPSolver;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * This library solves knapsack problems: 0-1 knapsack problems and multi-dimensional knapsack
 * problems.
 *
 * <p>
 * Given n items, each with a profit and a weight, and a knapsack of capacity c, the goal is to
 * find a subset of items which fits inside c and maximizes the total profit. The problem extends
 * to d dimensions, for instance to limit the number of items in the knapsack:
 *
 * <pre>
 * ForEach(j:1..d)(Sum(i:1..n)(weight_ij * item_i) &lt;= c_j  where item_i is a 0-1 variable
 * </pre>
 *
 * and the goal is to maximize <code>Sum(i:1..n)(profit_i * item_i)</code>. Profits and weights
 * are assumed to be non-negative.
 *
 * <p>
 * Example:
 *
 * <pre>
 * final long[] profits = {1, 2, 3, 4, 5, 6, 7, 8, 9};
 * final long[][] weights = {{1, 2, 3, 4, 5, 6, 7, 8, 9}, {1, 1, 1, 1, 1, 1, 1, 1, 1}};
 * final long[] capacities = {34, 4};
 * KnapsackSolver solver = new KnapsackSolver(
 *     KnapsackSolver.SolverType.KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER,
 *     "Multi-dimensional solver");
 * solver.init(profits, weights, capacities);
 * final long profit = solver.solve();
 * </pre>
 *
 * <p>
 * Unless disabled with {@link #setUseReduction(boolean)}, {@link #init(long[], long[][], long[])}
 * first drops the dimensions whose capacity exceeds the sum of the weights, then fixes the items
 * that bounds prove to be in or out of every optimal solution and passes only the remaining items
 * to the underlying solver.
 */
public class KnapsackSolver {
  private BaseKnapsackSolver solver;
  private boolean[] knownValue = new boolean[0];
  private boolean[] bestSolution = new boolean[0];
  private boolean isSolutionOptimal = false;
  private int[] mappingReducedItemId = new int[0];
  private boolean isProblemSolved;
  private long additionalProfit;
  private boolean useReduction;
  private double timeLimitSeconds;
  private TimeLimit timeLimit;

  /** Creates a solver of type {@link SolverType#KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER}. */
  public KnapsackSolver(String solver_name) {
    this(SolverType.KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER, solver_name);
  }

  /** Creates a solver of the given type. */
  public KnapsackSolver(KnapsackSolver.SolverType solver_type, String solver_name) {
    this.isProblemSolved = false;
    this.additionalProfit = 0L;
    this.useReduction = true;
    this.timeLimitSeconds = Double.POSITIVE_INFINITY;
    switch (solver_type) {
      case KNAPSACK_BRUTE_FORCE_SOLVER:
        solver = new KnapsackBruteForceSolver(solver_name);
        break;
      case KNAPSACK_64ITEMS_SOLVER:
        solver = new Knapsack64ItemsSolver(solver_name);
        break;
      case KNAPSACK_DYNAMIC_PROGRAMMING_SOLVER:
        solver = new KnapsackDynamicProgrammingSolver(solver_name);
        break;
      case KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER:
        solver = new KnapsackGenericSolver(solver_name);
        break;
      case KNAPSACK_DIVIDE_AND_CONQUER_SOLVER:
        solver = new KnapsackDivideAndConquerSolver(solver_name);
        break;
      case KNAPSACK_MULTIDIMENSION_CBC_MIP_SOLVER:
        // an MPSolver model; the MIP solver types of this port all use its branch and bound
        solver = new KnapsackMIPSolver(KnapsackMIPSolver.ProblemType.CBC_MIXED_INTEGER_PROGRAMMING,
            solver_name);
        break;
      case KNAPSACK_MULTIDIMENSION_SCIP_MIP_SOLVER:
        // an MPSolver model; the MIP solver types of this port all use its branch and bound
        solver = new KnapsackMIPSolver(
            KnapsackMIPSolver.ProblemType.SCIP_MIXED_INTEGER_PROGRAMMING, solver_name);
        break;
      case KNAPSACK_MULTIDIMENSION_CP_SAT_SOLVER:
        // a CpModel solved by the CP-SAT port
        solver = new KnapsackCpSat(solver_name);
        break;
      default:
        throw new IllegalArgumentException("Unknown knapsack solver type.");
    }
  }

  /** Does nothing; kept for compatibility with the OR-Tools Java wrapper. */
  public void delete() {}

  /**
   * Initializes the solver and enters the problem to be solved: <code>weights[d][i]</code> is the
   * weight of item <code>i</code> in dimension <code>d</code>, <code>capacities[d]</code> the
   * capacity of dimension <code>d</code>.
   *
   * @throws IllegalArgumentException if the sizes are inconsistent, or if the selected solver does
   *         not support the problem (more than one dimension left after the reduction for the
   *         single-dimension solvers, too many items for the brute force and 64 items solvers)
   */
  public void init(long[] profits, long[][] weights, long[] capacities) {
    for (long[] w : weights) {
      if (profits.length != w.length) {
        throw new IllegalArgumentException(
            "Profits and inner weights must have the same size (#items)");
      }
    }
    if (capacities.length != weights.length) {
      throw new IllegalArgumentException("Capacities and weights must have the same size (#bins)");
    }
    timeLimit = new TimeLimit(timeLimitSeconds);
    isSolutionOptimal = false;
    additionalProfit = 0L;
    isProblemSolved = false;

    final int numItems = profits.length;
    long[][] reducedWeights;
    long[] reducedCapacities;
    if (useReduction) {
      ReducedCapacities reduced = reduceCapacities(numItems, weights, capacities);
      reducedWeights = reduced.weights;
      reducedCapacities = reduced.capacities;
      if (reduced.numReducedItems > 0) {
        computeAdditionalProfit(profits);
      }
    } else {
      reducedWeights = weights;
      reducedCapacities = capacities;
    }
    if (!isProblemSolved) {
      solver.init(profits, reducedWeights, reducedCapacities);
      if (useReduction) {
        final int numReducedItems = reduceProblem(numItems);

        if (numReducedItems > 0) {
          computeAdditionalProfit(profits);
        }

        if (numReducedItems > 0 && numReducedItems < numItems) {
          initReducedProblem(profits, reducedWeights, reducedCapacities);
        }
      }
    }
    if (isProblemSolved) {
      isSolutionOptimal = true;
    }
  }

  /** Solves the problem and returns the profit of the best solution found. */
  public long solve() {
    if (isProblemSolved) {
      return additionalProfit;
    }
    boolean[] optimal = {isSolutionOptimal};
    final long profit = solver.solve(timeLimit, timeLimitSeconds, optimal);
    isSolutionOptimal = optimal[0];
    return additionalProfit + profit;
  }

  /** Returns true if the item <code>item_id</code> is packed in the optimal knapsack. */
  public boolean bestSolutionContains(int item_id) {
    final int mappedItemId = useReduction ? mappingReducedItemId[item_id] : item_id;
    return (useReduction && knownValue[item_id]) ? bestSolution[item_id]
        : solver.bestSolution(mappedItemId);
  }

  /** Returns true if the solution was proven optimal. */
  public boolean isSolutionOptimal() {
    return isSolutionOptimal;
  }

  /** The name given at construction. */
  public String getName() {
    return solver.getName();
  }

  /** Returns true if the problem reduction is enabled (the default). */
  public boolean useReduction() {
    return useReduction;
  }

  /** Enables or disables the problem reduction done by {@link #init(long[], long[][], long[])}. */
  public void setUseReduction(boolean use_reduction) {
    useReduction = use_reduction;
  }

  /**
   * Time limit in seconds, measured from this call or from the next
   * {@link #init(long[], long[][], long[])}. When a finite time limit is set the solution obtained
   * might not be optimal if the limit is reached. Only the branch and bound solvers (and the
   * reduction) check it.
   */
  public void setTimeLimit(double time_limit_seconds) {
    timeLimitSeconds = time_limit_seconds;
    timeLimit = new TimeLimit(timeLimitSeconds);
  }

  // The result of reduceCapacities().
  private static final class ReducedCapacities {
    long[][] weights;
    long[] capacities;
    int numReducedItems;
  }

  // Drops the capacity constraints whose capacity is at least the sum of the weights. If none is
  // left, all the items are packed and the problem is solved. Returns the number of reduced
  // items.
  private ReducedCapacities reduceCapacities(int numItems, long[][] weights, long[] capacities) {
    knownValue = new boolean[numItems];
    bestSolution = new boolean[numItems];
    mappingReducedItemId = new int[numItems];
    boolean[] activeCapacities = new boolean[weights.length];
    int numberOfActiveCapacities = 0;
    for (int i = 0; i < weights.length; ++i) {
      long maxWeight = 0;
      for (long weight : weights[i]) {
        maxWeight += weight;
      }
      if (maxWeight <= capacities[i]) {
        activeCapacities[i] = false;
      } else {
        activeCapacities[i] = true;
        ++numberOfActiveCapacities;
      }
    }
    ReducedCapacities result = new ReducedCapacities();
    result.weights = new long[numberOfActiveCapacities][];
    result.capacities = new long[numberOfActiveCapacities];
    int j = 0;
    for (int i = 0; i < weights.length; ++i) {
      if (activeCapacities[i]) {
        result.weights[j] = weights[i];
        result.capacities[j] = capacities[i];
        j++;
      }
    }
    if (numberOfActiveCapacities == 0) {
      // No capacity constraint left: all the items are in the best solution.
      for (int itemId = 0; itemId < numItems; ++itemId) {
        knownValue[itemId] = true;
        bestSolution[itemId] = true;
      }
      isProblemSolved = true;
      result.numReducedItems = numItems;
      return result;
    }
    // There are still capacity constraints, so no item is reduced.
    result.numReducedItems = 0;
    return result;
  }

  // Fixes the items whose bounds prove that they are in (or out of) the best solution. Returns
  // the number of reduced items.
  private int reduceProblem(int numItems) {
    knownValue = new boolean[numItems];
    bestSolution = new boolean[numItems];
    mappingReducedItemId = new int[numItems];
    additionalProfit = 0L;

    for (int itemId = 0; itemId < numItems; ++itemId) {
      mappingReducedItemId[itemId] = itemId;
    }

    long bestLowerBound = 0L;
    long[] j0UpperBounds = new long[numItems];
    long[] j1UpperBounds = new long[numItems];
    java.util.Arrays.fill(j0UpperBounds, Long.MAX_VALUE);
    java.util.Arrays.fill(j1UpperBounds, Long.MAX_VALUE);
    final long[] bounds = new long[2];
    for (int itemId = 0; itemId < numItems; ++itemId) {
      if (timeLimit.limitReached()) {
        break;
      }
      bounds[0] = 0L;
      bounds[1] = Long.MAX_VALUE;
      solver.getLowerAndUpperBoundWhenItem(itemId, false, bounds);
      j1UpperBounds[itemId] = bounds[1];
      bestLowerBound = Math.max(bestLowerBound, bounds[0]);

      solver.getLowerAndUpperBoundWhenItem(itemId, true, bounds);
      j0UpperBounds[itemId] = bounds[1];
      bestLowerBound = Math.max(bestLowerBound, bounds[0]);
    }

    int numReducedItems = 0;
    for (int itemId = 0; itemId < numItems; ++itemId) {
      if (bestLowerBound > j0UpperBounds[itemId]) {
        knownValue[itemId] = true;
        bestSolution[itemId] = false;
        ++numReducedItems;
      } else if (bestLowerBound > j1UpperBounds[itemId]) {
        knownValue[itemId] = true;
        bestSolution[itemId] = true;
        ++numReducedItems;
      }
    }

    isProblemSolved = numReducedItems == numItems;
    return numReducedItems;
  }

  private void computeAdditionalProfit(long[] profits) {
    final int numItems = profits.length;
    additionalProfit = 0L;
    for (int itemId = 0; itemId < numItems; ++itemId) {
      if (knownValue[itemId] && bestSolution[itemId]) {
        additionalProfit += profits[itemId];
      }
    }
  }

  // Initializes the underlying solver with the items whose value is not known.
  private void initReducedProblem(long[] profits, long[][] weights, long[] capacities) {
    final int numItems = profits.length;
    final int numDimensions = capacities.length;

    int numReducedProfits = 0;
    for (int itemId = 0; itemId < numItems; ++itemId) {
      if (!knownValue[itemId]) {
        numReducedProfits++;
      }
    }
    long[] reducedProfits = new long[numReducedProfits];
    int k = 0;
    for (int itemId = 0; itemId < numItems; ++itemId) {
      if (!knownValue[itemId]) {
        mappingReducedItemId[itemId] = k;
        reducedProfits[k++] = profits[itemId];
      }
    }

    long[][] reducedWeights = new long[numDimensions][];
    long[] reducedCapacities = capacities.clone();
    for (int dim = 0; dim < numDimensions; ++dim) {
      final long[] oneDimensionWeights = weights[dim];
      long[] oneDimensionReducedWeights = new long[numReducedProfits];
      k = 0;
      for (int itemId = 0; itemId < numItems; ++itemId) {
        if (knownValue[itemId]) {
          if (bestSolution[itemId]) {
            reducedCapacities[dim] -= oneDimensionWeights[itemId];
          }
        } else {
          oneDimensionReducedWeights[k++] = oneDimensionWeights[itemId];
        }
      }
      reducedWeights[dim] = oneDimensionReducedWeights;
    }
    solver.init(reducedProfits, reducedWeights, reducedCapacities);
  }

  /**
   * Enum controlling which underlying algorithm is used. It is passed to the constructor of the
   * KnapsackSolver object.
   */
  public enum SolverType {
    /**
     * Brute force method. Limited to 30 items and one dimension, this solver explores all
     * possible states. Experiments show competitive performance for instances with less than 15
     * items.
     */
    KNAPSACK_BRUTE_FORCE_SOLVER(0),
    /**
     * Optimized method for single dimension small problems. Limited to 64 items and one
     * dimension, this solver uses a branch and bound algorithm. It is about 4 times faster than
     * KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER.
     */
    KNAPSACK_64ITEMS_SOLVER(1),
    /**
     * Dynamic Programming approach for single dimension problems. The time and space complexity
     * is O(capacity * number_of_items).
     */
    KNAPSACK_DYNAMIC_PROGRAMMING_SOLVER(2),
    /**
     * CBC based solver: deals with both large number of items and several dimensions. Upstream
     * uses the integer programming solver CBC; this port currently solves it with the generic
     * branch and bound.
     */
    KNAPSACK_MULTIDIMENSION_CBC_MIP_SOLVER(3),
    /**
     * Generic solver: deals with both large number of items and several dimensions, based on
     * branch and bound.
     */
    KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER(5),
    /**
     * SCIP based solver: deals with both large number of items and several dimensions. Upstream
     * uses the integer programming solver SCIP; this port currently solves it with the generic
     * branch and bound.
     */
    KNAPSACK_MULTIDIMENSION_SCIP_MIP_SOLVER(6),
    /**
     * Divide and Conquer approach for single dimension problems, suitable for larger problems
     * than the dynamic programming solver. The time complexity is O(capacity * number_of_items)
     * and the space complexity is O(capacity + number_of_items).
     */
    KNAPSACK_DIVIDE_AND_CONQUER_SOLVER(9),
    /**
     * CP-SAT based solver: deals with both large number of items and several dimensions. Upstream
     * uses the CP-SAT solver; this port currently solves it with the generic branch and bound.
     */
    KNAPSACK_MULTIDIMENSION_CP_SAT_SOLVER(10);

    private final int swigValue;

    SolverType(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The value of the C++ enum constant. */
    public final int swigValue() {
      return swigValue;
    }

    /** The constant with the given C++ enum value. */
    public static SolverType swigToEnum(int swigValue) {
      for (SolverType swigEnum : values()) {
        if (swigEnum.swigValue == swigValue) {
          return swigEnum;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + SolverType.class + " with value " + swigValue);
    }
  }
}
