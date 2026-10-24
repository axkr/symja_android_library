package org.matheclipse.ortools.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.algorithms.KnapsackSolver.SolverType;

/**
 * Every {@link SolverType} on the same random instances, with and without reduction, compared
 * against brute force enumeration.
 */
public class KnapsackSolverTypesTest {

  private static final SolverType[] MULTI_DIMENSION_TYPES = {
      SolverType.KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER,
      SolverType.KNAPSACK_MULTIDIMENSION_CBC_MIP_SOLVER,
      SolverType.KNAPSACK_MULTIDIMENSION_SCIP_MIP_SOLVER,
      SolverType.KNAPSACK_MULTIDIMENSION_CP_SAT_SOLVER};

  // The best profit by enumerating all subsets.
  private static long bruteForce(long[] profits, long[][] weights, long[] capacities) {
    final int n = profits.length;
    long best = 0;
    for (long mask = 0; mask < (1L << n); ++mask) {
      long profit = 0;
      boolean fits = true;
      for (int d = 0; d < weights.length && fits; ++d) {
        long weight = 0;
        for (int i = 0; i < n; ++i) {
          if ((mask & (1L << i)) != 0) {
            weight += weights[d][i];
          }
        }
        fits = weight <= capacities[d];
      }
      if (!fits) {
        continue;
      }
      for (int i = 0; i < n; ++i) {
        if ((mask & (1L << i)) != 0) {
          profit += profits[i];
        }
      }
      best = Math.max(best, profit);
    }
    return best;
  }

  // Solves and checks the profit and that the packed items are feasible and worth the profit.
  private static void check(SolverType type, boolean useReduction, long[] profits,
      long[][] weights, long[] capacities, long expected, String context) {
    KnapsackSolver solver = new KnapsackSolver(type, "test");
    solver.setUseReduction(useReduction);
    assertEquals(useReduction, solver.useReduction());
    solver.init(profits, weights, capacities);
    final long profit = solver.solve();
    assertEquals(expected, profit, context + " " + type + " reduction=" + useReduction);
    assertTrue(solver.isSolutionOptimal(), context + " " + type);
    long packedProfit = 0;
    long[] packedWeight = new long[weights.length];
    for (int i = 0; i < profits.length; ++i) {
      if (solver.bestSolutionContains(i)) {
        packedProfit += profits[i];
        for (int d = 0; d < weights.length; ++d) {
          packedWeight[d] += weights[d][i];
        }
      }
    }
    assertEquals(expected, packedProfit,
        context + " " + type + " reduction=" + useReduction + " packed items");
    for (int d = 0; d < weights.length; ++d) {
      assertTrue(packedWeight[d] <= capacities[d], context + " " + type + " capacity " + d);
    }
  }

  private static long[][] randomInstance(Random random, int n, int dims, int maxWeight) {
    long[][] data = new long[dims + 2][];
    data[0] = new long[n];
    for (int i = 0; i < n; ++i) {
      data[0][i] = random.nextInt(4) == 0 ? 0 : random.nextInt(100);
    }
    data[dims + 1] = new long[dims];
    for (int d = 0; d < dims; ++d) {
      data[d + 1] = new long[n];
      long total = 0;
      for (int i = 0; i < n; ++i) {
        data[d + 1][i] = random.nextInt(6) == 0 ? 0 : 1 + random.nextInt(maxWeight);
        total += data[d + 1][i];
      }
      // Sometimes a capacity that does not bind, to exercise the capacity reduction.
      data[dims + 1][d] = random.nextInt(8) == 0 ? total + random.nextInt(5)
          : random.nextInt((int) Math.max(1, total));
    }
    return data;
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testOneDimensionAllTypes() {
    Random random = new Random(1234L);
    for (int round = 0; round < 120; ++round) {
      final int n = random.nextInt(14);
      long[][] data = randomInstance(random, n, 1, 30);
      long[] profits = data[0];
      long[][] weights = {data[1]};
      long[] capacities = data[2];
      final long expected = bruteForce(profits, weights, capacities);
      for (SolverType type : SolverType.values()) {
        for (boolean reduction : new boolean[] {true, false}) {
          check(type, reduction, profits, weights, capacities, expected, "round " + round);
        }
      }
    }
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testMultiDimension() {
    Random random = new Random(99L);
    for (int round = 0; round < 80; ++round) {
      final int n = random.nextInt(13);
      final int dims = 1 + random.nextInt(3);
      long[][] data = randomInstance(random, n, dims, 20);
      long[] profits = data[0];
      long[][] weights = new long[dims][];
      for (int d = 0; d < dims; ++d) {
        weights[d] = data[d + 1];
      }
      long[] capacities = data[dims + 1];
      final long expected = bruteForce(profits, weights, capacities);
      for (SolverType type : MULTI_DIMENSION_TYPES) {
        check(type, true, profits, weights, capacities, expected, "round " + round);
        check(type, false, profits, weights, capacities, expected, "round " + round);
      }
    }
  }

  @Test
  @Tag("slow")
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testLargerInstances() {
    Random random = new Random(5L);
    for (int round = 0; round < 40; ++round) {
      final int n = 18 + random.nextInt(4);
      long[][] data = randomInstance(random, n, 1, 60);
      long[] profits = data[0];
      long[][] weights = {data[1]};
      long[] capacities = data[2];
      final long expected = bruteForce(profits, weights, capacities);
      for (SolverType type : SolverType.values()) {
        check(type, true, profits, weights, capacities, expected, "round " + round);
        check(type, false, profits, weights, capacities, expected, "round " + round);
      }
    }
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testSixtyFourItems() {
    // 64 items: the limit of the 64 items solver; compared with dynamic programming.
    Random random = new Random(64L);
    long[] profits = new long[64];
    long[] weights = new long[64];
    for (int i = 0; i < 64; ++i) {
      profits[i] = 1 + random.nextInt(50);
      weights[i] = 1 + random.nextInt(50);
    }
    long[] capacities = {600};
    KnapsackSolver dp = new KnapsackSolver(SolverType.KNAPSACK_DYNAMIC_PROGRAMMING_SOLVER, "dp");
    dp.init(profits, new long[][] {weights}, capacities);
    final long expected = dp.solve();
    for (SolverType type : new SolverType[] {SolverType.KNAPSACK_64ITEMS_SOLVER,
        SolverType.KNAPSACK_DIVIDE_AND_CONQUER_SOLVER,
        SolverType.KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER}) {
      check(type, true, profits, new long[][] {weights}, capacities, expected, "64 items");
      check(type, false, profits, new long[][] {weights}, capacities, expected, "64 items");
    }
  }

  @Test
  public void testZeroWeightItemsDynamicProgramming() {
    // A zero-weight item selected by the table must not lower the returned profit.
    long[] profits = {10, 3};
    long[][] weights = {{5, 0}};
    long[] capacities = {5};
    check(SolverType.KNAPSACK_DYNAMIC_PROGRAMMING_SOLVER, false, profits, weights, capacities, 13,
        "zero weight");
    check(SolverType.KNAPSACK_DIVIDE_AND_CONQUER_SOLVER, false, profits, weights, capacities, 13,
        "zero weight");
    // Capacity zero: only the zero-weight items fit.
    long[] capacityZero = {0};
    for (SolverType type : SolverType.values()) {
      check(type, false, new long[] {10, 3, 4}, new long[][] {{5, 0, 0}}, capacityZero, 7,
          "capacity zero");
    }
  }

  @Test
  public void testReductionSolvesTrivialProblems() {
    // The capacity exceeds the total weight: every item is packed without search.
    KnapsackSolver solver = new KnapsackSolver(SolverType.KNAPSACK_BRUTE_FORCE_SOLVER, "trivial");
    solver.init(new long[] {4, 5, 6}, new long[][] {{1, 2, 3}}, new long[] {6});
    assertTrue(solver.isSolutionOptimal());
    assertEquals(15, solver.solve());
    for (int i = 0; i < 3; ++i) {
      assertTrue(solver.bestSolutionContains(i));
    }

    // No dimension at all.
    KnapsackSolver none = new KnapsackSolver("none");
    none.init(new long[] {4, 5}, new long[0][], new long[0]);
    assertEquals(9, none.solve());
    KnapsackSolver noneMip =
        new KnapsackSolver(SolverType.KNAPSACK_MULTIDIMENSION_CBC_MIP_SOLVER, "none");
    noneMip.setUseReduction(false);
    noneMip.init(new long[] {4, 0, 5}, new long[0][], new long[0]);
    assertEquals(9, noneMip.solve());

    // A non-binding dimension is dropped, so a single dimension solver accepts the problem.
    KnapsackSolver twoDims =
        new KnapsackSolver(SolverType.KNAPSACK_DYNAMIC_PROGRAMMING_SOLVER, "two");
    twoDims.init(new long[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
        new long[][] {{1, 2, 3, 4, 5, 6, 7, 8, 9}, {1, 1, 1, 1, 1, 1, 1, 1, 1}},
        new long[] {34, 9});
    assertEquals(34, twoDims.solve());
  }

  @Test
  public void testEmptyProblem() {
    for (SolverType type : SolverType.values()) {
      for (boolean reduction : new boolean[] {true, false}) {
        KnapsackSolver solver = new KnapsackSolver(type, "empty");
        solver.setUseReduction(reduction);
        solver.init(new long[0], new long[][] {{}}, new long[] {10});
        assertEquals(0, solver.solve(), type + " reduction=" + reduction);
      }
    }
  }

  @Test
  public void testUnsupportedProblems() {
    long[] profits = new long[31];
    long[] weights = new long[31];
    for (int i = 0; i < 31; ++i) {
      profits[i] = i + 1;
      weights[i] = i + 1;
    }
    KnapsackSolver bruteForce = new KnapsackSolver(SolverType.KNAPSACK_BRUTE_FORCE_SOLVER, "bf");
    assertThrows(IllegalArgumentException.class,
        () -> bruteForce.init(profits, new long[][] {weights}, new long[] {100}));

    KnapsackSolver dp = new KnapsackSolver(SolverType.KNAPSACK_DYNAMIC_PROGRAMMING_SOLVER, "dp");
    assertThrows(IllegalArgumentException.class, () -> dp.init(new long[] {1, 2},
        new long[][] {{1, 2}, {2, 1}}, new long[] {2, 2}));

    KnapsackSolver generic = new KnapsackSolver("generic");
    assertThrows(IllegalArgumentException.class,
        () -> generic.init(new long[] {1, 2}, new long[][] {{1}}, new long[] {2}));
    assertThrows(IllegalArgumentException.class,
        () -> generic.init(new long[] {1, 2}, new long[][] {{1, 2}}, new long[] {2, 3}));
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testTimeLimit() {
    // A hard instance (strongly correlated) with a time limit of zero: the search stops
    // immediately and the solution is not proven optimal, but it is still feasible.
    Random random = new Random(3L);
    final int n = 40;
    long[] profits = new long[n];
    long[] weights = new long[n];
    long total = 0;
    for (int i = 0; i < n; ++i) {
      weights[i] = 1000 + random.nextInt(1000);
      profits[i] = weights[i] + 100;
      total += weights[i];
    }
    long[] capacities = {total / 2};
    KnapsackSolver solver = new KnapsackSolver("time limit");
    solver.setTimeLimit(0.0);
    solver.init(profits, new long[][] {weights}, capacities);
    final long profit = solver.solve();
    assertFalse(solver.isSolutionOptimal());
    long packedProfit = 0;
    long packedWeight = 0;
    for (int i = 0; i < n; ++i) {
      if (solver.bestSolutionContains(i)) {
        packedProfit += profits[i];
        packedWeight += weights[i];
      }
    }
    assertEquals(profit, packedProfit);
    assertTrue(packedWeight <= capacities[0]);
  }

  @Test
  public void testNamesAndValues() {
    assertEquals("my name", new KnapsackSolver("my name").getName());
    assertEquals("dc",
        new KnapsackSolver(SolverType.KNAPSACK_DIVIDE_AND_CONQUER_SOLVER, "dc").getName());
    assertEquals(8, SolverType.values().length);
    assertEquals(5, SolverType.KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER.swigValue());
    assertEquals(9, SolverType.KNAPSACK_DIVIDE_AND_CONQUER_SOLVER.swigValue());
    assertEquals(10, SolverType.KNAPSACK_MULTIDIMENSION_CP_SAT_SOLVER.swigValue());
    assertEquals(SolverType.KNAPSACK_MULTIDIMENSION_SCIP_MIP_SOLVER, SolverType.swigToEnum(6));
    assertThrows(IllegalArgumentException.class, () -> SolverType.swigToEnum(4));
  }
}
