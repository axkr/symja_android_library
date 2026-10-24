package org.matheclipse.ortools.linearsolver.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.init.OrToolsVersion;
import org.matheclipse.ortools.testutil.OutputCapture;

/**
 * Runs the ported linear solver samples and checks the printed optimal values. Every expected
 * value is recomputed here independently of the solvers: by enumerating the vertices of the two
 * dimensional LPs, by exhaustive search for the integer programs, by a lower bound plus the
 * printed feasible packing for the bin packing, and from the literature for the Stigler diet.
 * The <code>LinearProgramming</code> and <code>IntegerProgramming</code> samples (former
 * examples) map every solver name to Glop (LP) or to the Java branch-and-bound (MIP), so all
 * their runs must print the same optimum. The model builder samples are run by
 * <code>ModelBuilderSamplesTest</code>.
 */
public class LinearSolverSamplesTest {

  private static final double TOLERANCE = 1e-6;

  // ---------------------------------------------------------------------------------------------
  // independent reference computations

  /**
   * Maximizes c.x over the polygon {x : a.x &lt;= b} by enumerating the intersections of pairs of
   * constraint lines (the polygon must be bounded). Returns {value, x, y}.
   */
  public static double[] maximize2d(double[][] a, double[] b, double[] c) {
    double best = Double.NEGATIVE_INFINITY;
    double[] result = null;
    for (int i = 0; i < a.length; ++i) {
      for (int j = i + 1; j < a.length; ++j) {
        final double det = a[i][0] * a[j][1] - a[i][1] * a[j][0];
        if (Math.abs(det) < 1e-12) {
          continue;
        }
        final double x = (b[i] * a[j][1] - a[i][1] * b[j]) / det;
        final double y = (a[i][0] * b[j] - b[i] * a[j][0]) / det;
        boolean feasible = true;
        for (int k = 0; k < a.length && feasible; ++k) {
          feasible = a[k][0] * x + a[k][1] * y <= b[k] + 1e-9;
        }
        if (feasible && c[0] * x + c[1] * y > best + 1e-12) {
          best = c[0] * x + c[1] * y;
          result = new double[] {best, x, y};
        }
      }
    }
    return result;
  }

  /**
   * Maximizes c.x over the integer points of {x : lo &lt;= a.x &lt;= hi, 0 &lt;= x &lt;= box} by
   * depth first enumeration; the rows with nonnegative coefficients prune the partial points.
   */
  public static double[] maximizeInteger(double[][] a, double[] lo, double[] hi, double[] c, int[] box) {
    final double[] best = {Double.NEGATIVE_INFINITY};
    final int[] x = new int[c.length];
    final int[] bestX = new int[c.length];
    final boolean[] nonnegativeRow = new boolean[a.length];
    for (int i = 0; i < a.length; ++i) {
      nonnegativeRow[i] = true;
      for (double v : a[i]) {
        nonnegativeRow[i] &= v >= 0;
      }
    }
    enumerate(0, a, lo, hi, c, box, x, new double[a.length], nonnegativeRow, best, bestX);
    final double[] result = new double[c.length + 1];
    result[0] = best[0];
    for (int j = 0; j < c.length; ++j) {
      result[j + 1] = bestX[j];
    }
    return result;
  }

  private static void enumerate(int j, double[][] a, double[] lo, double[] hi, double[] c,
      int[] box, int[] x, double[] activity, boolean[] nonnegativeRow, double[] best,
      int[] bestX) {
    if (j == c.length) {
      for (int i = 0; i < a.length; ++i) {
        if (activity[i] < lo[i] - 1e-9 || activity[i] > hi[i] + 1e-9) {
          return;
        }
      }
      double value = 0;
      for (int k = 0; k < c.length; ++k) {
        value += c[k] * x[k];
      }
      if (value > best[0]) {
        best[0] = value;
        System.arraycopy(x, 0, bestX, 0, x.length);
      }
      return;
    }
    for (int v = 0; v <= box[j]; ++v) {
      x[j] = v;
      boolean pruned = false;
      for (int i = 0; i < a.length; ++i) {
        activity[i] += a[i][j] * v;
        if (nonnegativeRow[i] && activity[i] > hi[i] + 1e-9) {
          pruned = true;
        }
      }
      if (!pruned) {
        enumerate(j + 1, a, lo, hi, c, box, x, activity, nonnegativeRow, best, bestX);
      }
      for (int i = 0; i < a.length; ++i) {
        activity[i] -= a[i][j] * v;
      }
      if (pruned) {
        break; // larger values only increase the nonnegative rows
      }
    }
  }

  /** The minimal cost of assigning each task to a distinct worker. */
  public static double minAssignment(double[][] costs, boolean[] allowedWorkers) {
    return minAssignment(costs, allowedWorkers, 0, new boolean[costs.length]);
  }

  private static double minAssignment(double[][] costs, boolean[] allowed, int task,
      boolean[] used) {
    if (task == costs[0].length) {
      return 0.0;
    }
    double best = Double.POSITIVE_INFINITY;
    for (int w = 0; w < costs.length; ++w) {
      if (used[w] || (allowed != null && !allowed[w])) {
        continue;
      }
      used[w] = true;
      best = Math.min(best, costs[w][task] + minAssignment(costs, allowed, task + 1, used));
      used[w] = false;
    }
    return best;
  }

  private static double sum(double[] values) {
    double s = 0;
    for (double v : values) {
      s += v;
    }
    return s;
  }

  // ---------------------------------------------------------------------------------------------
  // the samples

  @Test
  public void basicExample() {
    final String out = OutputCapture.run(() -> BasicExample.main(new String[0]));
    final String version =
        OrToolsVersion.getMajorNumber() + "." + OrToolsVersion.getMinorNumber();
    OutputCapture.assertContains(out, "Google OR-Tools version: " + version);
    OutputCapture.assertContains(out, "Solving with Glop solver v" + version);
    OutputCapture.assertContains(out, "Status: OPTIMAL");
    // max 3x + y, 0 <= x <= 1, 0 <= y <= 2, x + y <= 2
    final double[] expected = maximize2d(
        new double[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}},
        new double[] {1, 0, 2, 0, 2}, new double[] {3, 1});
    assertEquals(4.0, expected[0], TOLERANCE);
    assertEquals(expected[0], OutputCapture.numberAfter(out, "Objective value = "), TOLERANCE);
    assertEquals(expected[1], OutputCapture.numberAfter(out, "\nx = "), TOLERANCE);
    assertEquals(expected[2], OutputCapture.numberAfter(out, "\ny = "), TOLERANCE);
  }

  @Test
  public void linearProgrammingExample() {
    final String out = OutputCapture.run(() -> LinearProgrammingExample.main(new String[0]));
    // max 3x + 4y, x + 2y <= 14, 3x - y >= 0, x - y <= 2, x, y >= 0
    final double[] expected = maximize2d(
        new double[][] {{1, 2}, {-3, 1}, {1, -1}, {-1, 0}, {0, -1}},
        new double[] {14, 0, 2, 0, 0}, new double[] {3, 4});
    assertEquals(34.0, expected[0], TOLERANCE);
    assertEquals(expected[0], OutputCapture.numberAfter(out, "Objective value = "), TOLERANCE);
    assertEquals(expected[1], OutputCapture.numberAfter(out, "\nx = "), TOLERANCE);
    assertEquals(expected[2], OutputCapture.numberAfter(out, "\ny = "), TOLERANCE);
  }

  /** Checks the output of the simple LP samples (MPSolver and model builder). */
  public static void checkSimpleLp(String out) {
    // max x + 10y, x + 7y <= 17.5, x <= 3.5, x, y >= 0
    final double[] expected = maximize2d(new double[][] {{1, 7}, {1, 0}, {-1, 0}, {0, -1}},
        new double[] {17.5, 3.5, 0, 0}, new double[] {1, 10});
    assertEquals(25.0, expected[0], TOLERANCE);
    assertEquals(expected[0], OutputCapture.numberAfter(out, "Objective value = "), TOLERANCE);
    assertEquals(expected[1], OutputCapture.numberAfter(out, "\nx = "), TOLERANCE);
    assertEquals(expected[2], OutputCapture.numberAfter(out, "\ny = "), TOLERANCE);
  }

  @Test
  public void simpleLpProgram() {
    checkSimpleLp(OutputCapture.run(() -> SimpleLpProgram.main(new String[0])));
  }


  private static double[] simpleMipOptimum() {
    // the same model with integer x, y; x <= 3, y <= 2 follow from the constraints
    return maximizeInteger(new double[][] {{1, 7}, {1, 0}},
        new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
        new double[] {17.5, 3.5}, new double[] {1, 10}, new int[] {17, 3});
  }

  /** Checks the output of the simple MIP samples (MPSolver and model builder). */
  public static void checkSimpleMip(String out) {
    final double[] expected = simpleMipOptimum();
    assertEquals(23.0, expected[0], TOLERANCE);
    assertEquals(expected[0], OutputCapture.numberAfter(out, "Objective value = "), TOLERANCE);
    assertEquals(expected[1], OutputCapture.numberAfter(out, "\nx = "), TOLERANCE);
    assertEquals(expected[2], OutputCapture.numberAfter(out, "\ny = "), TOLERANCE);
  }

  @Test
  public void simpleMipProgram() {
    checkSimpleMip(OutputCapture.run(() -> SimpleMipProgram.main(new String[0])));
  }


  @Test
  public void mipVarArray() {
    final String out = OutputCapture.run(() -> MipVarArray.main(new String[0]));
    final double[][] a = {
        {5, 7, 9, 2, 1},
        {18, 4, -9, 10, 12},
        {4, 7, 3, 8, 5},
        {5, 13, 16, 3, -7},
    };
    final double[] hi = {250, 285, 211, 315};
    final double[] c = {7, 8, 2, 9, 6};
    // rows 0 and 2 have positive coefficients: x_j <= min(250 / a0j, 211 / a2j)
    final int[] box = new int[5];
    for (int j = 0; j < 5; ++j) {
      box[j] = (int) Math.floor(Math.min(hi[0] / a[0][j], hi[2] / a[2][j]));
    }
    final double[] expected = maximizeInteger(a, new double[] {0, 0, 0, 0}, hi, c, box);
    assertEquals(260.0, expected[0], TOLERANCE);
    assertEquals(expected[0], OutputCapture.numberAfter(out, "Objective value = "), TOLERANCE);
    // the printed solution must be feasible and reach the optimum
    final double[] x = new double[5];
    for (int j = 0; j < 5; ++j) {
      x[j] = OutputCapture.numberAfter(out, "x[" + j + "] = ");
      assertEquals(Math.rint(x[j]), x[j], TOLERANCE);
    }
    double value = 0;
    for (int j = 0; j < 5; ++j) {
      value += c[j] * x[j];
    }
    assertEquals(expected[0], value, TOLERANCE);
    for (int i = 0; i < 4; ++i) {
      double activity = 0;
      for (int j = 0; j < 5; ++j) {
        activity += a[i][j] * x[j];
      }
      assertTrue(activity >= -TOLERANCE && activity <= hi[i] + TOLERANCE);
    }
  }

  @Test
  public void stiglerDiet() {
    final String out = OutputCapture.run(() -> StiglerDiet.main(new String[0]));
    assertContains(out, "Number of variables = 77");
    assertContains(out, "Number of constraints = 9");
    // the optimum of the LP published by Dantzig: 0.10866 dollars per day, 39.66 per year
    assertEquals(39.6617, OutputCapture.numberAfter(out, "Optimal annual price: $"), 1e-3);
    // the printed daily nutrients satisfy the minimal amounts
    final String[] nutrients = {"Calories (kcal): ", "Protein (g): ", "Calcium (g): ",
        "Iron (mg): ", "Vitamin A (kIU): ", "Vitamin B1 (mg): ", "Vitamin B2 (mg): ",
        "Niacin (mg): ", "Vitamin C (mg): "};
    for (String nutrient : nutrients) {
      final java.util.regex.Matcher m = OutputCapture.assertFind(out,
          java.util.regex.Pattern.quote(nutrient) + "([-0-9.eE]+) \\(min ([-0-9.eE]+)\\)");
      final double amount = Double.parseDouble(m.group(1));
      final double minimum = Double.parseDouble(m.group(2));
      assertTrue(amount >= minimum - 1e-6, nutrient + amount + " < " + minimum);
    }
    // the five foods of the optimal diet (as in the documented output of the sample)
    assertContains(out, "Wheat Flour (Enriched): $10.77");
    assertContains(out, "Liver (Beef): $0.69");
    assertContains(out, "Cabbage: $4.09");
    assertContains(out, "Spinach: $1.82");
    assertContains(out, "Navy Beans, Dried: $22.27");
  }

  private static void assertContains(String out, String expected) {
    OutputCapture.assertContains(out, expected);
  }

  private static final double[][] ASSIGNMENT_COSTS = {
      {90, 80, 75, 70},
      {35, 85, 55, 65},
      {125, 95, 90, 95},
      {45, 110, 95, 115},
      {50, 100, 90, 100},
  };

  /** Checks the output of the assignment samples (MPSolver and model builder). */
  public static void checkAssignment(String out) {
    final double expected = minAssignment(ASSIGNMENT_COSTS, null);
    assertEquals(265.0, expected, TOLERANCE);
    assertEquals(expected, OutputCapture.numberAfter(out, "Total cost: "), TOLERANCE);
    // the printed assignment costs add up to the total
    final List<Double> costs = OutputCapture.numbersAfter(out, "Cost = ");
    assertEquals(4, costs.size());
    double total = 0;
    for (double v : costs) {
      total += v;
    }
    assertEquals(expected, total, TOLERANCE);
  }

  @Test
  public void assignmentMip() {
    checkAssignment(OutputCapture.run(() -> AssignmentMip.main(new String[0])));
  }


  @Test
  public void assignmentGroupsMip() {
    final String out = OutputCapture.run(() -> AssignmentGroupsMip.main(new String[0]));
    final double[][] costs = {
        {90, 76, 75, 70, 50, 74},
        {35, 85, 55, 65, 48, 101},
        {125, 95, 90, 105, 59, 120},
        {45, 110, 95, 115, 104, 83},
        {60, 105, 80, 75, 59, 62},
        {45, 65, 110, 95, 47, 31},
        {38, 51, 107, 41, 69, 99},
        {47, 85, 57, 71, 92, 77},
        {39, 63, 97, 49, 118, 56},
        {47, 101, 71, 60, 88, 109},
        {17, 39, 103, 64, 61, 92},
        {101, 45, 83, 59, 92, 27},
    };
    final int[][] group1 = {{2, 3}, {1, 3}, {1, 2}, {0, 1}, {0, 2}};
    final int[][] group2 = {{6, 7}, {5, 7}, {5, 6}, {4, 5}, {4, 7}};
    final int[][] group3 = {{10, 11}, {9, 11}, {9, 10}, {8, 10}, {8, 11}};
    // exactly one allowed pair of each group works, each of the six workers gets one task
    double expected = Double.POSITIVE_INFINITY;
    for (int[] p1 : group1) {
      for (int[] p2 : group2) {
        for (int[] p3 : group3) {
          final boolean[] allowed = new boolean[costs.length];
          for (int[] p : new int[][] {p1, p2, p3}) {
            allowed[p[0]] = true;
            allowed[p[1]] = true;
          }
          expected = Math.min(expected, minAssignment(costs, allowed));
        }
      }
    }
    assertEquals(239.0, expected, TOLERANCE);
    assertEquals(expected, OutputCapture.numberAfter(out, "Total cost: "), TOLERANCE);
    assertEquals(expected, sum(OutputCapture.numbersAfter(out, "Cost: ").stream()
        .mapToDouble(Double::doubleValue).toArray()), TOLERANCE);
  }

  @Test
  public void assignmentTaskSizesMip() {
    final String out = OutputCapture.run(() -> AssignmentTaskSizesMip.main(new String[0]));
    final double[][] costs = {
        {90, 76, 75, 70, 50, 74, 12, 68},
        {35, 85, 55, 65, 48, 101, 70, 83},
        {125, 95, 90, 105, 59, 120, 36, 73},
        {45, 110, 95, 115, 104, 83, 37, 71},
        {60, 105, 80, 75, 59, 62, 93, 88},
        {45, 65, 110, 95, 47, 31, 81, 34},
        {38, 51, 107, 41, 69, 99, 115, 48},
        {47, 85, 57, 71, 92, 77, 109, 36},
        {39, 63, 97, 49, 118, 56, 92, 61},
        {47, 101, 71, 60, 88, 109, 52, 90},
    };
    final int[] sizes = {10, 7, 3, 12, 15, 4, 11, 5};
    // remaining[t] = sum over the tasks t' >= t of the cheapest cost of t' (a lower bound)
    final double[] remaining = new double[sizes.length + 1];
    for (int t = sizes.length - 1; t >= 0; --t) {
      double cheapest = Double.POSITIVE_INFINITY;
      for (double[] row : costs) {
        cheapest = Math.min(cheapest, row[t]);
      }
      remaining[t] = remaining[t + 1] + cheapest;
    }
    final double[] best = {Double.POSITIVE_INFINITY};
    minTaskSizes(costs, sizes, 15, 0, new int[costs.length], 0.0, remaining, best);
    final double expected = best[0];
    assertEquals(326.0, expected, TOLERANCE);
    assertEquals(expected, OutputCapture.numberAfter(out, "Total cost: "), TOLERANCE);
    assertEquals(8, OutputCapture.count(out, "assigned to task"));
  }

  /**
   * Exhaustive search with bound pruning: each task to one worker, the sizes of the tasks of a
   * worker at most the capacity.
   */
  private static void minTaskSizes(double[][] costs, int[] sizes, int capacity, int task,
      int[] load, double cost, double[] remaining, double[] best) {
    if (cost + remaining[task] >= best[0]) {
      return;
    }
    if (task == sizes.length) {
      best[0] = cost;
      return;
    }
    for (int w = 0; w < costs.length; ++w) {
      if (load[w] + sizes[task] > capacity) {
        continue;
      }
      load[w] += sizes[task];
      minTaskSizes(costs, sizes, capacity, task + 1, load, cost + costs[w][task], remaining,
          best);
      load[w] -= sizes[task];
    }
  }

  @Test
  public void assignmentTeamsMip() {
    final String out = OutputCapture.run(() -> AssignmentTeamsMip.main(new String[0]));
    final double[][] costs = {
        {90, 76, 75, 70},
        {35, 85, 55, 65},
        {125, 95, 90, 105},
        {45, 110, 95, 115},
        {60, 105, 80, 75},
        {45, 65, 110, 95},
    };
    // distinct workers for the four tasks, at most two tasks per team {0, 2, 4} and {1, 3, 5}
    double expected = Double.POSITIVE_INFINITY;
    for (int a = 0; a < 6; ++a) {
      for (int b = 0; b < 6; ++b) {
        for (int c = 0; c < 6; ++c) {
          for (int d = 0; d < 6; ++d) {
            final int[] w = {a, b, c, d};
            boolean ok = true;
            int team1 = 0;
            for (int i = 0; i < 4; ++i) {
              for (int k = i + 1; k < 4; ++k) {
                ok &= w[i] != w[k];
              }
              if (w[i] % 2 == 0) {
                ++team1;
              }
            }
            if (ok && team1 <= 2 && 4 - team1 <= 2) {
              expected = Math.min(expected, costs[a][0] + costs[b][1] + costs[c][2] + costs[d][3]);
            }
          }
        }
      }
    }
    assertEquals(250.0, expected, TOLERANCE);
    assertEquals(expected, OutputCapture.numberAfter(out, "Total cost: "), TOLERANCE);
  }

  /** Checks the output of the bin packing samples (MPSolver and model builder). */
  public static void checkBinPacking(String out) {
    final double[] weights = {48, 30, 19, 36, 36, 27, 42, 42, 36, 24, 30};
    // at least ceil(370 / 100) = 4 bins are needed; the printed packing uses 4
    final int lowerBound = (int) Math.ceil(sum(weights) / 100.0);
    assertEquals(4, lowerBound);
    assertEquals(lowerBound, OutputCapture.numberAfter(out, "Number of bins used: "), TOLERANCE);
    final List<Double> binWeights = OutputCapture.numbersAfter(out, "Packed bin weight: ");
    assertEquals(lowerBound, binWeights.size());
    for (double w : binWeights) {
      assertTrue(w <= 100.0 + TOLERANCE);
    }
    assertEquals(sum(weights), OutputCapture.numberAfter(out, "Total packed weight: "), TOLERANCE);
    // every item is packed once
    assertEquals(weights.length, OutputCapture.count(out, "^Item \\d+ - weight"));
  }

  @Test
  public void binPackingMip() {
    checkBinPacking(OutputCapture.run(() -> BinPackingMip.main(new String[0])));
  }


  /**
   * The best total value of at most five disjoint item sets of weight at most 100, by dynamic
   * programming over the sets of used items.
   */
  private static double multipleKnapsackOptimum(double[] weights, double[] values, int numBins,
      double capacity) {
    final int n = weights.length;
    final int full = (1 << n) - 1;
    final boolean[] fits = new boolean[1 << n];
    final double[] value = new double[1 << n];
    for (int s = 0; s <= full; ++s) {
      double w = 0;
      double v = 0;
      for (int i = 0; i < n; ++i) {
        if ((s & (1 << i)) != 0) {
          w += weights[i];
          v += values[i];
        }
      }
      fits[s] = w <= capacity;
      value[s] = v;
    }
    boolean[] reachable = new boolean[1 << n];
    reachable[0] = true;
    for (int bin = 0; bin < numBins; ++bin) {
      final boolean[] next = reachable.clone();
      for (int m = 0; m <= full; ++m) {
        if (!reachable[m]) {
          continue;
        }
        final int free = full & ~m;
        // all nonempty subsets of the free items which fit into a bin
        for (int s = free; s != 0; s = (s - 1) & free) {
          if (fits[s]) {
            next[m | s] = true;
          }
        }
      }
      reachable = next;
    }
    double best = 0;
    for (int m = 0; m <= full; ++m) {
      if (reachable[m]) {
        best = Math.max(best, value[m]);
      }
    }
    return best;
  }

  @Tag("slow")
  @Test
  public void multipleKnapsackMip() {
    final String out = OutputCapture.run(() -> MultipleKnapsackMip.main(new String[0]));
    final double[] weights = {48, 30, 42, 36, 36, 48, 42, 42, 36, 24, 30, 30, 42, 36, 36};
    final double[] values = {10, 30, 25, 50, 35, 30, 15, 40, 30, 35, 45, 10, 20, 30, 25};
    final double expected = multipleKnapsackOptimum(weights, values, 5, 100);
    assertEquals(395.0, expected, TOLERANCE);
    assertEquals(expected, OutputCapture.numberAfter(out, "Total packed value: "), TOLERANCE);
    for (double w : OutputCapture.numbersAfter(out, "Packed bin weight: ")) {
      assertTrue(w <= 100.0 + TOLERANCE);
    }
    assertEquals(expected,
        sum(OutputCapture.numbersAfter(out, "Packed bin value: ").stream()
            .mapToDouble(Double::doubleValue).toArray()),
        TOLERANCE);
  }

  @Test
  public void linearProgramming() {
    final String out = OutputCapture.run(() -> LinearProgramming.main(new String[0]));
    OutputCapture.assertNotContains(out, "Could not create solver");
    OutputCapture.assertNotContains(out, "Failed to run");
    OutputCapture.assertNotContains(out, "does not have an optimal solution");
    // the GLOP run prints the model in LP format
    OutputCapture.assertContains(out, "Maximize");
    OutputCapture.assertContains(out, " Obj: +10 x1 +6 x2 +4 x3");
    // max 10 x1 + 6 x2 + 4 x3 subject to x1 + x2 + x3 <= 100, 10 x1 + 4 x2 + 5 x3 <= 600,
    // 2 x1 + 2 x2 + 6 x3 <= 300, x >= 0. At x = (100/3, 200/3, 0) the first two rows are tight
    // and the duals y = (10/3, 2/3, 0) solve y0 + 10 y1 = 10, y0 + 4 y1 = 6; the reduced cost of
    // x3 is 4 - (10/3 + 5 * 2/3) = -8/3 <= 0, so the point is optimal with value
    // 100 * 10/3 + 600 * 2/3 = 2200/3.
    final List<Double> objectives = OutputCapture.numbersAfter(out, "Optimal objective value = ");
    assertEquals(3, objectives.size()); // GLOP, CLP and XPRESS_LP
    for (double v : objectives) {
      assertEquals(2200.0 / 3.0, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "x1 = ")) {
      assertEquals(100.0 / 3.0, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "x2 = ")) {
      assertEquals(200.0 / 3.0, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "x3 = ")) {
      assertEquals(0.0, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "x3: reduced cost = ")) {
      assertEquals(-8.0 / 3.0, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "c0: dual value = ")) {
      assertEquals(10.0 / 3.0, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "c1: dual value = ")) {
      assertEquals(2.0 / 3.0, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "c2: dual value = ")) {
      assertEquals(0.0, v, TOLERANCE);
    }
    final List<Double> activities = OutputCapture.numbersAfter(out, "    activity = ");
    assertEquals(9, activities.size());
    for (int run = 0; run < 3; ++run) {
      assertEquals(100.0, activities.get(3 * run), TOLERANCE);
      assertEquals(600.0, activities.get(3 * run + 1), TOLERANCE);
      assertEquals(200.0, activities.get(3 * run + 2), TOLERANCE);
    }
  }

  @Test
  public void integerProgramming() {
    final String out = OutputCapture.run(() -> IntegerProgramming.main(new String[0]));
    OutputCapture.assertNotContains(out, "Could not create solver");
    OutputCapture.assertNotContains(out, "Failed to run");
    OutputCapture.assertNotContains(out, "does not have an optimal solution");
    // min x1 + 2 x2 subject to 3 x1 + 2 x2 >= 17, x integer >= 0: for x2 = 0, 1, 2, ... the
    // smallest x1 is 6, 5, 5, 4, 3, 3, 2, 1, 1 giving 6, 7, 9, 10, 11, 13, 14, 15, 17, and from
    // x2 = 9 on the cost is at least 18; the optimum is 6 at (6, 0)
    double expected = Double.POSITIVE_INFINITY;
    for (int x2 = 0; x2 <= 9; ++x2) {
      final int x1 = Math.max(0, (int) Math.ceil((17.0 - 2.0 * x2) / 3.0));
      expected = Math.min(expected, x1 + 2.0 * x2);
    }
    assertEquals(6.0, expected, TOLERANCE);
    final List<Double> objectives = OutputCapture.numbersAfter(out, "Optimal objective value = ");
    assertEquals(5, objectives.size()); // SCIP, CBC, GLPK, SAT and XPRESS
    for (double v : objectives) {
      assertEquals(expected, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "x1 = ")) {
      assertEquals(6.0, v, TOLERANCE);
    }
    for (double v : OutputCapture.numbersAfter(out, "x2 = ")) {
      assertEquals(0.0, v, TOLERANCE);
    }
  }
}
