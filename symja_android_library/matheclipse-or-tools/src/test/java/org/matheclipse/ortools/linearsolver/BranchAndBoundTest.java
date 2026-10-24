package org.matheclipse.ortools.linearsolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests of the branch-and-bound behind the MIP problem types of {@link MPSolver}: random small
 * integer programs against exhaustive enumeration, models with symmetric columns, node and time
 * limits, hints and the result statuses.
 */
public class BranchAndBoundTest {

  private static final double INF = Double.POSITIVE_INFINITY;
  private static final double TOLERANCE = 1e-6;

  /** A dense mixed integer program: integer columns first, then at most one continuous one. */
  static final class DenseMip {
    int numIntegers;
    boolean hasContinuous;
    double[] lb;
    double[] ub;
    double[] c;
    double[][] a;
    double[] lo;
    double[] hi;
    boolean maximize;

    int numVars() {
      return numIntegers + (hasContinuous ? 1 : 0);
    }

    MPSolver toSolver(String type) {
      final MPSolver solver = MPSolver.createSolver(type);
      final int n = numVars();
      final MPVariable[] x = new MPVariable[n];
      for (int j = 0; j < n; ++j) {
        x[j] = solver.makeVar(lb[j], ub[j], j < numIntegers, "x" + j);
      }
      for (int i = 0; i < a.length; ++i) {
        final MPConstraint ct = solver.makeConstraint(lo[i], hi[i], "c" + i);
        for (int j = 0; j < n; ++j) {
          ct.setCoefficient(x[j], a[i][j]);
        }
      }
      for (int j = 0; j < n; ++j) {
        solver.objective().setCoefficient(x[j], c[j]);
      }
      solver.objective().setOptimizationDirection(maximize);
      return solver;
    }
  }

  static DenseMip randomMip(Random r, boolean withContinuous) {
    return randomMip(r, withContinuous, 2 + r.nextInt(3));
  }

  /**
   * A random program whose rows contain a random point of the box, shifted with probability 0.2
   * (which may make the problem infeasible).
   */
  static DenseMip randomMip(Random r, boolean withContinuous, int numIntegers) {
    final DenseMip p = new DenseMip();
    p.numIntegers = numIntegers;
    p.hasContinuous = withContinuous;
    final int n = p.numVars();
    final int m = 1 + r.nextInt(4);
    p.lb = new double[n];
    p.ub = new double[n];
    p.c = new double[n];
    for (int j = 0; j < n; ++j) {
      p.lb[j] = -2 + r.nextInt(3);
      p.ub[j] = p.lb[j] + r.nextInt(5);
      // quarters, so the objective step is 1/4 or a multiple
      p.c[j] = (r.nextInt(41) - 20) / 4.0;
    }
    if (withContinuous) {
      p.lb[n - 1] = -3.5 + r.nextInt(3);
      p.ub[n - 1] = p.lb[n - 1] + 0.5 + r.nextInt(6);
    }
    p.a = new double[m][n];
    p.lo = new double[m];
    p.hi = new double[m];
    final double[] point = new double[n];
    for (int j = 0; j < n; ++j) {
      point[j] = p.lb[j] + r.nextInt((int) (p.ub[j] - p.lb[j]) + 1);
    }
    for (int i = 0; i < m; ++i) {
      double activity = 0;
      for (int j = 0; j < n; ++j) {
        p.a[i][j] = r.nextInt(4) == 0 ? 0 : r.nextInt(11) - 5;
        if (j == n - 1 && withContinuous) {
          p.a[i][j] = (r.nextInt(9) - 4) / 2.0;
        }
        activity += p.a[i][j] * point[j];
      }
      final double center = r.nextInt(5) == 0 ? activity + r.nextInt(7) - 3 : activity;
      switch (r.nextInt(5) == 0 ? 2 : r.nextInt(3) == 2 ? 3 : r.nextInt(2)) {
        case 0:
          p.lo[i] = Double.NEGATIVE_INFINITY;
          p.hi[i] = center + r.nextInt(3);
          break;
        case 1:
          p.lo[i] = center - r.nextInt(3);
          p.hi[i] = INF;
          break;
        case 2:
          p.lo[i] = center;
          p.hi[i] = center;
          break;
        default:
          p.lo[i] = center - r.nextInt(3);
          p.hi[i] = center + r.nextInt(3);
          break;
      }
    }
    p.maximize = r.nextBoolean();
    return p;
  }

  /**
   * The optimal objective by enumeration of the integer points; the continuous variable (if any)
   * takes the best value of the interval left by the rows. NaN if infeasible.
   */
  static double bruteForce(DenseMip p) {
    final int k = p.numIntegers;
    final int[] x = new int[k];
    for (int j = 0; j < k; ++j) {
      x[j] = (int) p.lb[j];
    }
    double best = Double.NaN;
    while (true) {
      final double value = bestContinuousCompletion(p, x);
      if (!Double.isNaN(value)
          && (Double.isNaN(best) || (p.maximize ? value > best : value < best))) {
        best = value;
      }
      int j = 0;
      while (j < k && x[j] == (int) p.ub[j]) {
        x[j] = (int) p.lb[j];
        ++j;
      }
      if (j == k) {
        break;
      }
      ++x[j];
    }
    return best;
  }

  private static double bestContinuousCompletion(DenseMip p, int[] x) {
    final int k = p.numIntegers;
    double yLow = p.hasContinuous ? p.lb[k] : 0.0;
    double yHigh = p.hasContinuous ? p.ub[k] : 0.0;
    for (int i = 0; i < p.a.length; ++i) {
      double s = 0;
      for (int j = 0; j < k; ++j) {
        s += p.a[i][j] * x[j];
      }
      final double ay = p.hasContinuous ? p.a[i][k] : 0.0;
      if (ay == 0.0) {
        if (s < p.lo[i] - 1e-9 || s > p.hi[i] + 1e-9) {
          return Double.NaN;
        }
        continue;
      }
      // lo <= s + ay y <= hi
      double l = (p.lo[i] - s) / ay;
      double h = (p.hi[i] - s) / ay;
      if (ay < 0) {
        final double t = l;
        l = h;
        h = t;
      }
      yLow = Math.max(yLow, l);
      yHigh = Math.min(yHigh, h);
    }
    if (yLow > yHigh + 1e-9) {
      return Double.NaN;
    }
    double value = 0;
    for (int j = 0; j < k; ++j) {
      value += p.c[j] * x[j];
    }
    if (p.hasContinuous) {
      final double cy = p.c[k];
      final boolean high = p.maximize ? cy > 0 : cy < 0;
      value += cy * (high ? yHigh : yLow);
    }
    return value;
  }

  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  private static void checkRandomMips(long seed, int count, boolean withContinuous,
      int maxIntegers) {
    final Random r = new Random(seed);
    final String[] types = {"SCIP", "CBC", "SAT"};
    int numInfeasible = 0;
    for (int t = 0; t < count; ++t) {
      final DenseMip p = randomMip(r, withContinuous, 2 + r.nextInt(maxIntegers - 1));
      final double expected = bruteForce(p);
      final MPSolver solver = p.toSolver(types[t % types.length]);
      if (t % 4 == 3) {
        // also without the presolve, the cuts and the bound rounding of the integral rows
        assertTrue(solver.setSolverSpecificParametersAsString(
            "use_cuts: false, use_presolve: false"));
      }
      final MPSolver.ResultStatus status = solver.solve();
      final String context = "problem " + t + " of seed " + seed;
      if (Double.isNaN(expected)) {
        assertEquals(MPSolver.ResultStatus.INFEASIBLE, status, context);
        ++numInfeasible;
        continue;
      }
      assertEquals(MPSolver.ResultStatus.OPTIMAL, status, context);
      assertEquals(expected, solver.objective().value(), 1e-6, context);
      assertTrue(solver.verifySolution(1e-6, false), context);
      // the bound of a proven optimum equals the objective (up to the relative gap)
      assertEquals(expected, solver.objective().bestBound(),
          1e-4 * Math.max(1.0, Math.abs(expected)), context);
    }
    // the generator must produce both feasible and infeasible problems
    assertTrue(numInfeasible > 0 && numInfeasible < count / 2,
        numInfeasible + " infeasible problems of " + count);
  }

  @Test
  public void testRandomIntegerPrograms() {
    checkRandomMips(1, 300, false, 4);
  }

  @Test
  public void testRandomMixedIntegerPrograms() {
    checkRandomMips(2, 300, true, 4);
  }

  @Test
  public void testMoreRandomPrograms() {
    for (long seed = 10; seed < 16; ++seed) {
      checkRandomMips(seed, 500, (seed & 1) == 0, 6);
    }
  }

  /** The best value of at most numBins disjoint item sets of weight at most capacity. */
  static double multipleKnapsackOptimum(int[] weights, int[] values, int numBins, int capacity) {
    final int n = weights.length;
    final int full = (1 << n) - 1;
    final boolean[] fits = new boolean[1 << n];
    final int[] value = new int[1 << n];
    for (int s = 0; s <= full; ++s) {
      int w = 0;
      for (int i = 0; i < n; ++i) {
        if ((s & (1 << i)) != 0) {
          w += weights[i];
          value[s] += values[i];
        }
      }
      fits[s] = w <= capacity;
    }
    boolean[] reachable = new boolean[1 << n];
    reachable[0] = true;
    for (int b = 0; b < numBins; ++b) {
      final boolean[] next = reachable.clone();
      for (int m = 0; m <= full; ++m) {
        if (reachable[m]) {
          final int free = full & ~m;
          for (int s = free; s != 0; s = (s - 1) & free) {
            if (fits[s]) {
              next[m | s] = true;
            }
          }
        }
      }
      reachable = next;
    }
    int best = 0;
    for (int m = 0; m <= full; ++m) {
      if (reachable[m]) {
        best = Math.max(best, value[m]);
      }
    }
    return best;
  }

  static MPSolver multipleKnapsack(int[] weights, int[] values, int[] capacities) {
    final MPSolver solver = MPSolver.createSolver("SCIP");
    final int n = weights.length;
    final int k = capacities.length;
    final MPVariable[][] x = new MPVariable[n][k];
    for (int i = 0; i < n; ++i) {
      final MPConstraint once = solver.makeConstraint(0, 1, "");
      for (int b = 0; b < k; ++b) {
        x[i][b] = solver.makeBoolVar("x_" + i + "_" + b);
        once.setCoefficient(x[i][b], 1);
        solver.objective().setCoefficient(x[i][b], values[i]);
      }
    }
    for (int b = 0; b < k; ++b) {
      final MPConstraint capacity = solver.makeConstraint(0, capacities[b], "");
      for (int i = 0; i < n; ++i) {
        capacity.setCoefficient(x[i][b], weights[i]);
      }
    }
    solver.objective().setMaximization();
    return solver;
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testRandomSymmetricMultipleKnapsacks() {
    // identical bins: the symmetry breaking rows and the subset sum tightening must keep an
    // optimal solution
    final Random r = new Random(3);
    for (int t = 0; t < 40; ++t) {
      final int n = 5 + r.nextInt(4);
      final int k = 2 + r.nextInt(2);
      final int[] weights = new int[n];
      final int[] values = new int[n];
      for (int i = 0; i < n; ++i) {
        weights[i] = 3 + r.nextInt(12);
        values[i] = 1 + r.nextInt(20);
      }
      final int capacity = 8 + r.nextInt(15);
      final int[] capacities = new int[k];
      java.util.Arrays.fill(capacities, capacity);
      final MPSolver solver = multipleKnapsack(weights, values, capacities);
      assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve(), "problem " + t);
      assertEquals(multipleKnapsackOptimum(weights, values, k, capacity),
          solver.objective().value(), TOLERANCE, "problem " + t);
      assertTrue(solver.verifySolution(1e-6, false));
    }
  }

  @Test
  public void testMultipleKnapsackWithDifferentBins() {
    // bins of different capacities are not interchangeable; enumerate the assignments
    final int[] weights = {4, 7, 5, 3, 6};
    final int[] values = {5, 9, 6, 3, 8};
    final int[] capacities = {9, 7};
    int best = 0;
    final int[] bin = new int[weights.length];
    for (int code = 0; code < 243; ++code) {
      int c = code;
      for (int i = 0; i < weights.length; ++i) {
        bin[i] = c % 3; // 0: not packed, 1 or 2: bin index + 1
        c /= 3;
      }
      final int[] load = new int[2];
      int value = 0;
      for (int i = 0; i < weights.length; ++i) {
        if (bin[i] > 0) {
          load[bin[i] - 1] += weights[i];
          value += values[i];
        }
      }
      if (load[0] <= capacities[0] && load[1] <= capacities[1]) {
        best = Math.max(best, value);
      }
    }
    final MPSolver solver = multipleKnapsack(weights, values, capacities);
    assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve());
    assertEquals(best, solver.objective().value(), TOLERANCE);
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testRandomBinPacking() {
    // interchangeable bins with a "used" variable in each capacity row
    final Random r = new Random(4);
    for (int t = 0; t < 20; ++t) {
      final int n = 4 + r.nextInt(3);
      final int capacity = 10;
      final int[] weights = new int[n];
      for (int i = 0; i < n; ++i) {
        weights[i] = 2 + r.nextInt(8);
      }
      // the minimal number of bins by dynamic programming over the sets of packed items
      final int full = (1 << n) - 1;
      final int[] minBins = new int[1 << n];
      java.util.Arrays.fill(minBins, Integer.MAX_VALUE);
      minBins[0] = 0;
      for (int m = 1; m <= full; ++m) {
        for (int s = m; s != 0; s = (s - 1) & m) {
          int w = 0;
          for (int i = 0; i < n; ++i) {
            if ((s & (1 << i)) != 0) {
              w += weights[i];
            }
          }
          if (w <= capacity && minBins[m & ~s] != Integer.MAX_VALUE) {
            minBins[m] = Math.min(minBins[m], minBins[m & ~s] + 1);
          }
        }
      }
      final MPSolver solver = MPSolver.createSolver("CBC");
      final MPVariable[][] x = new MPVariable[n][n];
      final MPVariable[] y = new MPVariable[n];
      for (int b = 0; b < n; ++b) {
        y[b] = solver.makeBoolVar("y" + b);
        solver.objective().setCoefficient(y[b], 1);
      }
      for (int i = 0; i < n; ++i) {
        final MPConstraint once = solver.makeConstraint(1, 1, "");
        for (int b = 0; b < n; ++b) {
          x[i][b] = solver.makeBoolVar("x" + i + "_" + b);
          once.setCoefficient(x[i][b], 1);
        }
      }
      for (int b = 0; b < n; ++b) {
        final MPConstraint load = solver.makeConstraint(-INF, 0, "");
        for (int i = 0; i < n; ++i) {
          load.setCoefficient(x[i][b], weights[i]);
        }
        load.setCoefficient(y[b], -capacity);
      }
      assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve(), "problem " + t);
      assertEquals(minBins[full], solver.objective().value(), TOLERANCE, "problem " + t);
      assertTrue(solver.verifySolution(1e-6, false));
    }
  }

  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  private static int[][] hardKnapsackData(long seed, int n) {
    final Random r = new Random(seed);
    final int[] weights = new int[n];
    final int[] values = new int[n];
    for (int i = 0; i < n; ++i) {
      weights[i] = 100 + r.nextInt(900);
      // strongly correlated values make the LP bound weak
      values[i] = weights[i] + 50 + r.nextInt(3);
    }
    return new int[][] {weights, values};
  }

  @Test
  public void testNodeLimit() {
    final int[][] data = hardKnapsackData(5, 40);
    final MPSolver solver = multipleKnapsack(data[0], data[1], new int[] {2000, 2100, 2200});
    assertTrue(solver.setSolverSpecificParametersAsString("max_nodes: 20"));
    final MPSolver.ResultStatus status = solver.solve();
    assertTrue(status == MPSolver.ResultStatus.FEASIBLE || status == MPSolver.ResultStatus.OPTIMAL
        || status == MPSolver.ResultStatus.NOT_SOLVED, status.toString());
    assertTrue(solver.nodes() <= 20, "nodes " + solver.nodes());
    if (status == MPSolver.ResultStatus.FEASIBLE) {
      assertTrue(solver.verifySolution(1e-6, false));
      // maximization: the bound is at least the objective
      assertTrue(solver.objective().bestBound() >= solver.objective().value() - TOLERANCE);
    }
  }

  @Test
  public void testTimeLimit() {
    final int[][] data = hardKnapsackData(6, 60);
    final MPSolver solver = multipleKnapsack(data[0], data[1], new int[] {3000, 3100, 3200, 3300});
    solver.setTimeLimit(200);
    final long start = System.nanoTime();
    final MPSolver.ResultStatus status = solver.solve();
    final double seconds = (System.nanoTime() - start) * 1e-9;
    assertTrue(seconds < 2.0, "the solve took " + seconds + " s");
    assertTrue(status == MPSolver.ResultStatus.FEASIBLE || status == MPSolver.ResultStatus.OPTIMAL
        || status == MPSolver.ResultStatus.NOT_SOLVED, status.toString());
    if (status != MPSolver.ResultStatus.NOT_SOLVED) {
      assertTrue(solver.verifySolution(1e-6, false));
    }
  }

  @Test
  public void testInfeasibleAndUnbounded() {
    MPSolver solver = MPSolver.createSolver("SCIP");
    MPVariable x = solver.makeIntVar(0, 10, "x");
    MPConstraint ct = solver.makeConstraint(0.2, 0.8, "between");
    ct.setCoefficient(x, 1);
    // no integer in [0.2, 0.8]
    assertEquals(MPSolver.ResultStatus.INFEASIBLE, solver.solve());

    solver = MPSolver.createSolver("CBC");
    x = solver.makeIntVar(0, INF, "x");
    MPVariable y = solver.makeIntVar(0, INF, "y");
    ct = solver.makeConstraint(-INF, 3, "");
    ct.setCoefficient(x, 1);
    ct.setCoefficient(y, -1);
    solver.objective().setCoefficient(x, 1);
    solver.objective().setMaximization();
    assertEquals(MPSolver.ResultStatus.UNBOUNDED, solver.solve());

    // infeasible integer bounds
    solver = MPSolver.createSolver("SCIP");
    solver.makeIntVar(0.3, 0.7, "z");
    assertEquals(MPSolver.ResultStatus.INFEASIBLE, solver.solve());
  }

  @Test
  public void testHint() {
    final int[][] data = hardKnapsackData(7, 12);
    final MPSolver solver = multipleKnapsack(data[0], data[1], new int[] {1500});
    // the hint packs nothing, a feasible solution of value 0
    final MPVariable[] vars = solver.variables();
    final double[] zeros = new double[vars.length];
    solver.setHint(vars, zeros);
    assertTrue(solver.setSolverSpecificParametersAsString("max_nodes: 1"));
    final MPSolver.ResultStatus status = solver.solve();
    assertTrue(status == MPSolver.ResultStatus.FEASIBLE || status == MPSolver.ResultStatus.OPTIMAL,
        status.toString());
    assertTrue(solver.objective().value() >= 0.0);
  }

  @Test
  public void testIntegralityOfReportedValues() {
    final MPSolver solver = MPSolver.createSolver("SCIP");
    final MPVariable x = solver.makeIntVar(-5, 5, "x");
    final MPVariable y = solver.makeIntVar(-5, 5, "y");
    final MPConstraint ct = solver.makeConstraint(-INF, 1.5, "");
    ct.setCoefficient(x, 3);
    ct.setCoefficient(y, 2);
    solver.objective().setCoefficient(x, 1);
    solver.objective().setCoefficient(y, 1);
    solver.objective().setMaximization();
    assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve());
    // 3x + 2y <= 1 with x + y maximal: y = 5 - ... enumerate
    double best = Double.NEGATIVE_INFINITY;
    for (int a = -5; a <= 5; ++a) {
      for (int b = -5; b <= 5; ++b) {
        if (3 * a + 2 * b <= 1.5) {
          best = Math.max(best, a + b);
        }
      }
    }
    assertEquals(best, solver.objective().value(), TOLERANCE);
    assertEquals(Math.rint(x.solutionValue()), x.solutionValue(), 0.0);
    assertEquals(Math.rint(y.solutionValue()), y.solutionValue(), 0.0);
    assertNotNull(solver.solverVersion());
  }
}
