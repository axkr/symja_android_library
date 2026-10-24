package org.matheclipse.ortools.sat.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.matheclipse.ortools.testutil.OutputCapture.assertContains;
import static org.matheclipse.ortools.testutil.OutputCapture.assertFind;
import static org.matheclipse.ortools.testutil.OutputCapture.count;
import static org.matheclipse.ortools.testutil.OutputCapture.numberAfter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.sat.BoolVar;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.IntVar;
import org.matheclipse.ortools.sat.LinearExpr;
import org.matheclipse.ortools.sat.Literal;
import org.matheclipse.ortools.testutil.OutputCapture;

/**
 * Runs the 32 CP-SAT samples and checks their output. Every expected optimum or solution count is
 * computed independently in the test (brute force, dynamic programming or a short argument), and
 * every printed solution is checked against the sample's model.
 */
public class SatSamplesTest {

  private static final Pattern ASSIGNED =
      Pattern.compile("Worker (\\d+) assigned to task (\\d+)\\.  Cost: (\\d+)");

  // ---------------------------------------------------------------------------- assignment family

  /** Parses the "Worker w assigned to task t" lines and checks cost and the total. */
  private static List<int[]> assignments(String out, int[][] costs) {
    List<int[]> result = new ArrayList<>();
    Matcher m = ASSIGNED.matcher(out);
    long total = 0;
    while (m.find()) {
      int w = Integer.parseInt(m.group(1));
      int t = Integer.parseInt(m.group(2));
      assertEquals(costs[w][t], Integer.parseInt(m.group(3)));
      total += costs[w][t];
      result.add(new int[] {w, t});
    }
    assertEquals((double) total, numberAfter(out, "Total cost:"), 1e-9);
    return result;
  }

  // minimum cost of assigning each task to a distinct worker (permutations)
  private static long minAssignment(int[][] costs, boolean[] allowedWorker, int task,
      boolean[] usedWorker) {
    int numTasks = costs[0].length;
    if (task == numTasks) {
      return 0;
    }
    long best = Long.MAX_VALUE;
    for (int w = 0; w < costs.length; w++) {
      if (usedWorker[w] || (allowedWorker != null && !allowedWorker[w])) {
        continue;
      }
      usedWorker[w] = true;
      long rest = minAssignment(costs, allowedWorker, task + 1, usedWorker);
      usedWorker[w] = false;
      if (rest != Long.MAX_VALUE) {
        best = Math.min(best, rest + costs[w][task]);
      }
    }
    return best;
  }

  @Test
  public void testAssignmentSat() {
    int[][] costs = {{90, 80, 75, 70}, {35, 85, 55, 65}, {125, 95, 90, 95}, {45, 110, 95, 115},
        {50, 100, 90, 100}};
    String out = OutputCapture.run(() -> AssignmentSat.main(new String[0]));
    long expected = minAssignment(costs, null, 0, new boolean[costs.length]);
    assertEquals(265, expected);
    assertEquals(265.0, numberAfter(out, "Total cost:"), 1e-9);
    List<int[]> a = assignments(out, costs);
    assertEquals(4, a.size());
    assertDistinctWorkersAndTasks(a, 4);
  }

  private static void assertDistinctWorkersAndTasks(List<int[]> a, int numTasks) {
    Set<Integer> workers = new HashSet<>();
    Set<Integer> tasks = new HashSet<>();
    for (int[] wt : a) {
      assertTrue(workers.add(wt[0]), "worker used twice");
      assertTrue(tasks.add(wt[1]), "task assigned twice");
    }
    assertEquals(numTasks, tasks.size());
  }

  @Test
  public void testAssignmentTeamsSat() {
    int[][] costs = {{90, 76, 75, 70}, {35, 85, 55, 65}, {125, 95, 90, 105}, {45, 110, 95, 115},
        {60, 105, 80, 75}, {45, 65, 110, 95}};
    int[] team1 = {0, 2, 4};
    // brute force: each task to a distinct worker, at most 2 tasks per team
    long best = Long.MAX_VALUE;
    int n = costs.length;
    for (int a = 0; a < n; a++) {
      for (int b = 0; b < n; b++) {
        for (int c = 0; c < n; c++) {
          for (int d = 0; d < n; d++) {
            int[] w = {a, b, c, d};
            if (new HashSet<>(Arrays.asList(a, b, c, d)).size() < 4) {
              continue;
            }
            int t1 = 0;
            for (int x : w) {
              if (Arrays.stream(team1).anyMatch(y -> y == x)) {
                t1++;
              }
            }
            if (t1 > 2 || 4 - t1 > 2) {
              continue;
            }
            best = Math.min(best, costs[a][0] + costs[b][1] + costs[c][2] + costs[d][3]);
          }
        }
      }
    }
    assertEquals(250, best);
    String out = OutputCapture.run(() -> AssignmentTeamsSat.main(new String[0]));
    assertEquals((double) best, numberAfter(out, "Total cost:"), 1e-9);
    List<int[]> as = assignments(out, costs);
    assertDistinctWorkersAndTasks(as, 4);
  }

  @Test
  public void testAssignmentGroupsSat() {
    int[][] costs = {{90, 76, 75, 70, 50, 74}, {35, 85, 55, 65, 48, 101},
        {125, 95, 90, 105, 59, 120}, {45, 110, 95, 115, 104, 83}, {60, 105, 80, 75, 59, 62},
        {45, 65, 110, 95, 47, 31}, {38, 51, 107, 41, 69, 99}, {47, 85, 57, 71, 92, 77},
        {39, 63, 97, 49, 118, 56}, {47, 101, 71, 60, 88, 109}, {17, 39, 103, 64, 61, 92},
        {101, 45, 83, 59, 92, 27}};
    int[][][] groups = {
        {{0, 0, 1, 1}, {0, 1, 0, 1}, {0, 1, 1, 0}, {1, 1, 0, 0}, {1, 0, 1, 0}},
        {{0, 0, 1, 1}, {0, 1, 0, 1}, {0, 1, 1, 0}, {1, 1, 0, 0}, {1, 0, 0, 1}},
        {{0, 0, 1, 1}, {0, 1, 0, 1}, {0, 1, 1, 0}, {1, 0, 1, 0}, {1, 0, 0, 1}}};
    // brute force: choose an allowed pair in each group, then the best assignment of the 6
    // working workers to the 6 tasks
    long best = Long.MAX_VALUE;
    for (int[] g1 : groups[0]) {
      for (int[] g2 : groups[1]) {
        for (int[] g3 : groups[2]) {
          boolean[] allowed = new boolean[12];
          for (int k = 0; k < 4; k++) {
            allowed[k] = g1[k] == 1;
            allowed[4 + k] = g2[k] == 1;
            allowed[8 + k] = g3[k] == 1;
          }
          best = Math.min(best, minAssignment(costs, allowed, 0, new boolean[12]));
        }
      }
    }
    assertEquals(239, best);
    String out = OutputCapture.run(() -> AssignmentGroupsSat.main(new String[0]));
    assertEquals((double) best, numberAfter(out, "Total cost:"), 1e-9);
    List<int[]> as = assignments(out, costs);
    assertDistinctWorkersAndTasks(as, 6);
    // the working workers form an allowed pattern in every group
    boolean[] works = new boolean[12];
    for (int[] wt : as) {
      works[wt[0]] = true;
    }
    for (int g = 0; g < 3; g++) {
      boolean ok = false;
      for (int[] tuple : groups[g]) {
        boolean match = true;
        for (int k = 0; k < 4; k++) {
          match &= (tuple[k] == 1) == works[4 * g + k];
        }
        ok |= match;
      }
      assertTrue(ok, "group " + g);
    }
  }

  private long bestTaskSizes;

  private void taskSizes(int[][] costs, int[] sizes, int[] order, int k, int[] load, long cost,
      long[] minCostFrom) {
    if (cost + minCostFrom[k] >= bestTaskSizes) {
      return;
    }
    if (k == order.length) {
      bestTaskSizes = cost;
      return;
    }
    int task = order[k];
    for (int w = 0; w < costs.length; w++) {
      if (load[w] + sizes[task] <= 15) {
        load[w] += sizes[task];
        taskSizes(costs, sizes, order, k + 1, load, cost + costs[w][task], minCostFrom);
        load[w] -= sizes[task];
      }
    }
  }

  @Test
  public void testAssignmentTaskSizesSat() {
    int[][] costs = {{90, 76, 75, 70, 50, 74, 12, 68}, {35, 85, 55, 65, 48, 101, 70, 83},
        {125, 95, 90, 105, 59, 120, 36, 73}, {45, 110, 95, 115, 104, 83, 37, 71},
        {60, 105, 80, 75, 59, 62, 93, 88}, {45, 65, 110, 95, 47, 31, 81, 34},
        {38, 51, 107, 41, 69, 99, 115, 48}, {47, 85, 57, 71, 92, 77, 109, 36},
        {39, 63, 97, 49, 118, 56, 92, 61}, {47, 101, 71, 60, 88, 109, 52, 90}};
    int[] sizes = {10, 7, 3, 12, 15, 4, 11, 5};
    // branch and bound over the tasks, largest first
    int[] order = {4, 3, 6, 0, 1, 7, 5, 2};
    long[] minCostFrom = new long[order.length + 1];
    for (int k = order.length - 1; k >= 0; k--) {
      long min = Long.MAX_VALUE;
      for (int[] row : costs) {
        min = Math.min(min, row[order[k]]);
      }
      minCostFrom[k] = minCostFrom[k + 1] + min;
    }
    bestTaskSizes = Long.MAX_VALUE;
    taskSizes(costs, sizes, order, 0, new int[costs.length], 0, minCostFrom);
    assertEquals(326, bestTaskSizes);
    String out = OutputCapture.run(() -> AssignmentTaskSizesSat.main(new String[0]));
    assertEquals((double) bestTaskSizes, numberAfter(out, "Total cost:"), 1e-9);
    List<int[]> as = assignments(out, costs);
    int[] load = new int[costs.length];
    Set<Integer> tasks = new HashSet<>();
    for (int[] wt : as) {
      load[wt[0]] += sizes[wt[1]];
      assertTrue(tasks.add(wt[1]));
    }
    assertEquals(8, tasks.size());
    for (int l : load) {
      assertTrue(l <= 15);
    }
  }

  // ---------------------------------------------------------------------------- small models

  @Test
  public void testAssumptionsSampleSat() {
    String out = OutputCapture.run(() -> AssumptionsSampleSat.main(new String[0]));
    // x > y, y > z, z > x is infeasible, every two of them are feasible: all three assumptions
    // (literals 3, 4, 5) are needed
    boolean[] feasiblePair = new boolean[3];
    boolean feasibleAll = false;
    for (int x = 0; x <= 10; x++) {
      for (int y = 0; y <= 10; y++) {
        for (int z = 0; z <= 10; z++) {
          boolean a = x > y;
          boolean b = y > z;
          boolean c = z > x;
          feasiblePair[0] |= b && c;
          feasiblePair[1] |= a && c;
          feasiblePair[2] |= a && b;
          feasibleAll |= a && b && c;
        }
      }
    }
    assertFalse(feasibleAll);
    assertTrue(feasiblePair[0] && feasiblePair[1] && feasiblePair[2]);
    assertEquals("[3, 4, 5]", out.trim());
  }

  @Test
  public void testBinPackingProblemSat() {
    String out = OutputCapture.run(() -> BinPackingProblemSat.main(new String[0]));
    int[][] items = {{20, 6}, {15, 6}, {30, 4}, {45, 3}};
    long total = 0;
    for (int[] it : items) {
      total += it[0] * it[1];
    }
    // k bins with load <= 80 and 5 - k bins with load <= 100 must hold the total load
    int maxSlack = 0;
    for (int k = 0; k <= 5; k++) {
      if (80L * k + 100L * (5 - k) >= total) {
        maxSlack = k;
      }
    }
    assertEquals(1, maxSlack);
    assertContains(out, "Solve status: OPTIMAL");
    assertEquals((double) maxSlack, numberAfter(out, "Optimal objective value:"), 1e-9);
    // the printed packing is feasible and has exactly one bin with load <= 80
    int[] copies = new int[items.length];
    int slackBins = 0;
    for (int b = 0; b < 5; b++) {
      long load = (long) numberAfter(out, "load_" + b + " =");
      long sum = 0;
      for (int i = 0; i < items.length; i++) {
        long x = (long) numberAfter(out, "item_" + i + "_" + b + " =");
        sum += x * items[i][0];
        copies[i] += x;
      }
      assertEquals(load, sum);
      assertTrue(load <= 100);
      if (load <= 80) {
        slackBins++;
      }
    }
    assertEquals(1, slackBins);
    for (int i = 0; i < items.length; i++) {
      assertEquals(items[i][1], copies[i]);
    }
  }

  @Test
  public void testChannelingSampleSat() {
    String out = OutputCapture.run(() -> ChannelingSampleSat.main(new String[0]));
    int expected = 0;
    for (int x = 0; x <= 10; x++) {
      for (int y = 0; y <= 10; y++) {
        for (int b = 0; b <= 1; b++) {
          if (channelingOk(x, y, b)) {
            expected++;
          }
        }
      }
    }
    assertEquals(11, expected);
    Matcher m = Pattern.compile("x=(\\d+) y=(\\d+) b=(\\d+)").matcher(out);
    Set<String> seen = new HashSet<>();
    while (m.find()) {
      int x = Integer.parseInt(m.group(1));
      int y = Integer.parseInt(m.group(2));
      int b = Integer.parseInt(m.group(3));
      assertTrue(channelingOk(x, y, b), m.group());
      assertTrue(seen.add(m.group()));
    }
    assertEquals(expected, seen.size());
  }

  private static boolean channelingOk(int x, int y, int b) {
    if (b == 1) {
      return x >= 5 && x + y == 10;
    }
    return x <= 4 && y == 0;
  }

  @Test
  public void testCloneModelSampleSat() {
    String out = OutputCapture.run(() -> CloneModelSampleSat.main(new String[0]));
    long best = Long.MIN_VALUE;
    long bestClone = Long.MIN_VALUE;
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        for (int z = 0; z < 3; z++) {
          if (x == y) {
            continue;
          }
          long obj = x + 2 * y + 3 * z;
          best = Math.max(best, obj);
          if (x + y <= 1) {
            bestClone = Math.max(bestClone, obj);
          }
        }
      }
    }
    assertEquals(11, best);
    assertEquals(8, bestClone);
    assertEquals((double) best, numberAfter(out, "Optimal value of the original model:"), 1e-9);
    assertEquals((double) bestClone, numberAfter(out, "Optimal value of the cloned model:"), 1e-9);
  }

  private int cpIsFunCount;

  // C P I S F U N T R E: CP + IS + FUN = TRUE with distinct digits, leading digits non zero
  private void cpIsFun(int[] digits, int k, boolean[] used) {
    if (k == 10) {
      int c = digits[0];
      int p = digits[1];
      int i = digits[2];
      int s = digits[3];
      int f = digits[4];
      int u = digits[5];
      int n = digits[6];
      int t = digits[7];
      int r = digits[8];
      int e = digits[9];
      if ((10 * c + p) + (10 * i + s) + (100 * f + 10 * u + n)
          == 1000 * t + 100 * r + 10 * u + e) {
        cpIsFunCount++;
      }
      return;
    }
    for (int d = 0; d < 10; d++) {
      if (used[d] || (d == 0 && (k == 0 || k == 2 || k == 4 || k == 7))) {
        continue;
      }
      used[d] = true;
      digits[k] = d;
      cpIsFun(digits, k + 1, used);
      used[d] = false;
    }
  }

  @Test
  public void testCpIsFunSat() {
    cpIsFunCount = 0;
    cpIsFun(new int[10], 0, new boolean[10]);
    assertEquals(72, cpIsFunCount);
    String out = OutputCapture.run(() -> CpIsFunSat.main(new String[0]));
    assertEquals((double) cpIsFunCount, numberAfter(out, "- solutions :"), 1e-9);
    Matcher m = Pattern.compile("C = (\\d)  P = (\\d)  I = (\\d)  S = (\\d)  F = (\\d)  U = (\\d)"
        + "  N = (\\d)  T = (\\d)  R = (\\d)  E = (\\d)").matcher(out);
    Set<String> seen = new HashSet<>();
    while (m.find()) {
      int[] d = new int[10];
      for (int k = 0; k < 10; k++) {
        d[k] = Integer.parseInt(m.group(k + 1));
      }
      assertEquals(10, Arrays.stream(d).distinct().count());
      assertEquals(10 * d[0] + d[1] + 10 * d[2] + d[3] + 100 * d[4] + 10 * d[5] + d[6],
          1000 * d[7] + 100 * d[8] + 10 * d[5] + d[9]);
      assertTrue(seen.add(m.group()));
    }
    assertEquals(cpIsFunCount, seen.size());
  }

  @Test
  public void testCpSatExample() {
    long best = Long.MIN_VALUE;
    for (int x = 0; x <= 50; x++) {
      for (int y = 0; y <= 50; y++) {
        for (int z = 0; z <= 50; z++) {
          if (2 * x + 7 * y + 3 * z <= 50 && 3 * x - 5 * y + 7 * z <= 45
              && 5 * x + 2 * y - 6 * z <= 37) {
            best = Math.max(best, 2 * x + 2 * y + 3 * z);
          }
        }
      }
    }
    assertEquals(35, best);
    String out = OutputCapture.run(() -> CpSatExample.main(new String[0]));
    assertEquals((double) best, numberAfter(out, "Maximum of objective function:"), 1e-9);
    long x = (long) numberAfter(out, "x =");
    long y = (long) numberAfter(out, "y =");
    long z = (long) numberAfter(out, "z =");
    assertEquals(best, 2 * x + 2 * y + 3 * z);
    assertTrue(2 * x + 7 * y + 3 * z <= 50 && 3 * x - 5 * y + 7 * z <= 45
        && 5 * x + 2 * y - 6 * z <= 37);
  }

  @Test
  public void testEarlinessTardinessCostSampleSat() {
    String out = OutputCapture.run(() -> EarlinessTardinessCostSampleSat.main(new String[0]));
    Matcher m = Pattern.compile("x=(\\d+) expr=(\\d+)").matcher(out);
    Set<Long> xs = new HashSet<>();
    while (m.find()) {
      long x = Long.parseLong(m.group(1));
      long expr = Long.parseLong(m.group(2));
      assertEquals(Math.max(Math.max(-8 * x + 40, 0), 12 * x - 180), expr, m.group());
      assertTrue(xs.add(x));
    }
    // expr is a function of x in [0, 20]
    assertEquals(21, xs.size());
  }

  @Test
  public void testMinimalJobshopSat() {
    // jobs: (machine, duration) per task
    int[][][] jobs = {{{0, 3}, {1, 2}, {2, 2}}, {{0, 2}, {2, 1}, {1, 4}}, {{1, 4}, {2, 3}}};
    long best = jobshopOptimum(jobs);
    assertEquals(11, best);
    String out = OutputCapture.run(() -> MinimalJobshopSat.main(new String[0]));
    assertEquals((double) best, numberAfter(out, "Optimal Schedule Length:"), 1e-9);
    // the printed schedule: tasks of a machine do not overlap and follow the job order
    Map<String, long[]> slots = new HashMap<>();
    for (String line : out.split("\n")) {
      if (line.startsWith("Machine")) {
        continue;
      }
    }
    String[] lines = out.split("\n");
    for (int k = 0; k < lines.length; k++) {
      if (!lines[k].startsWith("Machine")) {
        continue;
      }
      Matcher names = Pattern.compile("job_(\\d+)_task_(\\d+)").matcher(lines[k]);
      Matcher times = Pattern.compile("\\[(\\d+),(\\d+)\\]").matcher(lines[k + 1]);
      List<long[]> onMachine = new ArrayList<>();
      while (names.find()) {
        assertTrue(times.find());
        long s = Long.parseLong(times.group(1));
        long e = Long.parseLong(times.group(2));
        int job = Integer.parseInt(names.group(1));
        int task = Integer.parseInt(names.group(2));
        assertEquals(jobs[job][task][1], e - s);
        slots.put(job + "_" + task, new long[] {s, e});
        onMachine.add(new long[] {s, e});
      }
      for (int i = 0; i < onMachine.size(); i++) {
        for (int j = i + 1; j < onMachine.size(); j++) {
          assertTrue(onMachine.get(i)[1] <= onMachine.get(j)[0]
              || onMachine.get(j)[1] <= onMachine.get(i)[0]);
        }
      }
    }
    assertEquals(8, slots.size());
    long makespan = 0;
    for (int j = 0; j < jobs.length; j++) {
      for (int t = 0; t + 1 < jobs[j].length; t++) {
        assertTrue(slots.get(j + "_" + t)[1] <= slots.get(j + "_" + (t + 1))[0]);
      }
      makespan = Math.max(makespan, slots.get(j + "_" + (jobs[j].length - 1))[1]);
    }
    assertEquals(best, makespan);
  }

  // exact job shop optimum: every combination of machine orders, earliest start schedule
  private static long jobshopOptimum(int[][][] jobs) {
    List<List<int[]>> machineTasks = new ArrayList<>();
    for (int m = 0; m < 3; m++) {
      machineTasks.add(new ArrayList<>());
    }
    for (int j = 0; j < jobs.length; j++) {
      for (int t = 0; t < jobs[j].length; t++) {
        machineTasks.get(jobs[j][t][0]).add(new int[] {j, t});
      }
    }
    List<List<List<int[]>>> orders = new ArrayList<>();
    for (List<int[]> tasks : machineTasks) {
      List<List<int[]>> perms = new ArrayList<>();
      permutations(tasks, new ArrayList<>(), new boolean[tasks.size()], perms);
      orders.add(perms);
    }
    long best = Long.MAX_VALUE;
    for (List<int[]> o0 : orders.get(0)) {
      for (List<int[]> o1 : orders.get(1)) {
        for (List<int[]> o2 : orders.get(2)) {
          long ms = schedule(jobs, Arrays.asList(o0, o1, o2));
          if (ms >= 0) {
            best = Math.min(best, ms);
          }
        }
      }
    }
    return best;
  }

  private static void permutations(List<int[]> items, List<int[]> current, boolean[] used,
      List<List<int[]>> out) {
    if (current.size() == items.size()) {
      out.add(new ArrayList<>(current));
      return;
    }
    for (int i = 0; i < items.size(); i++) {
      if (!used[i]) {
        used[i] = true;
        current.add(items.get(i));
        permutations(items, current, used, out);
        current.remove(current.size() - 1);
        used[i] = false;
      }
    }
  }

  // longest path of the precedence graph, -1 if the machine orders create a cycle
  private static long schedule(int[][][] jobs, List<List<int[]>> machineOrders) {
    Map<String, Long> start = new HashMap<>();
    int total = 0;
    for (int[][] job : jobs) {
      total += job.length;
    }
    for (int round = 0; round <= total; round++) {
      boolean changed = false;
      for (int j = 0; j < jobs.length; j++) {
        for (int t = 0; t < jobs[j].length; t++) {
          long s = 0;
          if (t > 0) {
            s = Math.max(s, start.getOrDefault(j + "_" + (t - 1), 0L) + jobs[j][t - 1][1]);
          }
          List<int[]> order = machineOrders.get(jobs[j][t][0]);
          for (int k = 0; k < order.size(); k++) {
            if (order.get(k)[0] == j && order.get(k)[1] == t && k > 0) {
              int[] prev = order.get(k - 1);
              s = Math.max(s, start.getOrDefault(prev[0] + "_" + prev[1], 0L)
                  + jobs[prev[0]][prev[1]][1]);
            }
          }
          if (start.getOrDefault(j + "_" + t, -1L) != s) {
            start.put(j + "_" + t, s);
            changed = true;
          }
        }
      }
      if (!changed) {
        long ms = 0;
        for (int j = 0; j < jobs.length; j++) {
          int t = jobs[j].length - 1;
          ms = Math.max(ms, start.get(j + "_" + t) + jobs[j][t][1]);
        }
        return ms;
      }
    }
    return -1;
  }

  @Test
  public void testMultipleKnapsackSat() {
    int[] weights = {48, 30, 42, 36, 36, 48, 42, 42, 36, 24, 30, 30, 42, 36, 36};
    int[] values = {10, 30, 25, 50, 35, 30, 15, 40, 30, 35, 45, 10, 20, 30, 25};
    long best = multipleKnapsack(weights, values, 5, 100);
    assertEquals(395, best);
    String out = OutputCapture.run(() -> MultipleKnapsackSat.main(new String[0]));
    assertEquals((double) best, numberAfter(out, "Total packed value:"), 1e-9);
    Matcher m = Pattern.compile("Item (\\d+) weight: (\\d+) value: (\\d+)").matcher(out);
    Set<Integer> items = new HashSet<>();
    long value = 0;
    while (m.find()) {
      int i = Integer.parseInt(m.group(1));
      assertTrue(items.add(i), "item packed twice");
      assertEquals(weights[i], Integer.parseInt(m.group(2)));
      value += values[i];
    }
    assertEquals(best, value);
    for (double w : OutputCapture.numbersAfter(out, "Packed bin weight:")) {
      assertTrue(w <= 100);
    }
  }

  // identical bins: best value of packing the items of mask into k bins, by memoized recursion
  private static long multipleKnapsack(int[] weights, int[] values, int bins, int capacity) {
    int n = weights.length;
    List<int[]> subsets = new ArrayList<>(); // {mask, value}
    for (int mask = 1; mask < (1 << n); mask++) {
      long w = 0;
      long v = 0;
      for (int i = 0; i < n && w <= capacity; i++) {
        if ((mask & (1 << i)) != 0) {
          w += weights[i];
          v += values[i];
        }
      }
      if (w <= capacity) {
        subsets.add(new int[] {mask, (int) v});
      }
    }
    long[][] memo = new long[bins + 1][1 << n];
    for (long[] row : memo) {
      Arrays.fill(row, -1);
    }
    return knap(subsets, memo, bins, (1 << n) - 1);
  }

  private static long knap(List<int[]> subsets, long[][] memo, int k, int mask) {
    if (k == 0 || mask == 0) {
      return 0;
    }
    if (memo[k][mask] >= 0) {
      return memo[k][mask];
    }
    long best = knap(subsets, memo, k - 1, mask);
    for (int[] sub : subsets) {
      if ((sub[0] & mask) == sub[0]) {
        best = Math.max(best, sub[1] + knap(subsets, memo, k - 1, mask & ~sub[0]));
      }
    }
    memo[k][mask] = best;
    return best;
  }

  private static boolean queensOk(int[] q) {
    for (int i = 0; i < q.length; i++) {
      for (int j = i + 1; j < q.length; j++) {
        if (q[i] == q[j] || Math.abs(q[i] - q[j]) == j - i) {
          return false;
        }
      }
    }
    return true;
  }

  private static int countQueens(int[] q, int k) {
    if (k == q.length) {
      return queensOk(q) ? 1 : 0;
    }
    int c = 0;
    for (int v = 0; v < q.length; v++) {
      q[k] = v;
      c += countQueens(q, k + 1);
    }
    return c;
  }

  @Test
  public void testNQueensSat() {
    int expected = countQueens(new int[8], 0);
    assertEquals(92, expected);
    String out = OutputCapture.run(() -> NQueensSat.main(new String[0]));
    assertEquals((double) expected, numberAfter(out, "solutions :"), 1e-9);
    String[] lines = out.split("\n");
    Set<String> boards = new HashSet<>();
    for (int k = 0; k < lines.length; k++) {
      if (!lines[k].startsWith("Solution ")) {
        continue;
      }
      int[] q = new int[8];
      StringBuilder board = new StringBuilder();
      for (int row = 0; row < 8; row++) {
        String line = lines[k + 1 + row].replace(" ", "");
        board.append(line);
        assertEquals(1, line.chars().filter(ch -> ch == 'Q').count());
        q[row] = line.indexOf('Q');
      }
      assertTrue(queensOk(q));
      assertTrue(boards.add(board.toString()));
    }
    assertEquals(expected, boards.size());
  }

  @Test
  public void testNoOverlapSampleSat() {
    long best = Long.MAX_VALUE;
    int[] d = {2, 4, 3};
    int[][] weekends = {{5, 7}, {12, 14}, {19, 21}};
    for (int s0 = 0; s0 <= 21; s0++) {
      for (int s1 = 0; s1 <= 21; s1++) {
        for (int s2 = 0; s2 <= 21; s2++) {
          int[][] all = {{s0, s0 + d[0]}, {s1, s1 + d[1]}, {s2, s2 + d[2]}, weekends[0],
              weekends[1], weekends[2]};
          if (Math.max(Math.max(s0 + 2, s1 + 4), s2 + 3) > 21 || !disjoint(all)) {
            continue;
          }
          best = Math.min(best, Math.max(Math.max(s0 + 2, s1 + 4), s2 + 3));
        }
      }
    }
    assertEquals(11, best);
    String out = OutputCapture.run(() -> NoOverlapSampleSat.main(new String[0]));
    assertEquals((double) best, numberAfter(out, "Optimal Schedule Length:"), 1e-9);
    long s0 = (long) numberAfter(out, "Task 0 starts at");
    long s1 = (long) numberAfter(out, "Task 1 starts at");
    long s2 = (long) numberAfter(out, "Task 2 starts at");
    assertTrue(disjoint(new int[][] {{(int) s0, (int) s0 + 2}, {(int) s1, (int) s1 + 4},
        {(int) s2, (int) s2 + 3}, weekends[0], weekends[1], weekends[2]}));
    assertEquals(best, Math.max(Math.max(s0 + 2, s1 + 4), s2 + 3));
  }

  private static boolean disjoint(int[][] intervals) {
    for (int i = 0; i < intervals.length; i++) {
      for (int j = i + 1; j < intervals.length; j++) {
        if (intervals[i][0] < intervals[j][1] && intervals[j][0] < intervals[i][1]) {
          return false;
        }
      }
    }
    return true;
  }

  @Test
  public void testNonLinearSat() {
    long best = Long.MIN_VALUE;
    for (int x = 0; x <= 20; x++) {
      for (int y = 0; y <= 20; y++) {
        if (2 * x + 2 * y == 20) {
          best = Math.max(best, x * y);
        }
      }
    }
    assertEquals(25, best);
    String out = OutputCapture.run(() -> NonLinearSat.main(new String[0]));
    long x = (long) numberAfter(out, "x =");
    long y = (long) numberAfter(out, "y =");
    assertEquals(best, (long) numberAfter(out, "s ="));
    assertEquals(best, x * y);
    assertEquals(20, 2 * x + 2 * y);
  }

  @Test
  public void testNursesSat() {
    String out = OutputCapture.run(() -> NursesSat.main(new String[0]));
    assertContains(out, "Status: FEASIBLE");
    assertContains(out, "5 solutions found.");
    assertContains(out, "Stop search after 5 solutions");
    String[] parts = out.split("Solution #");
    Set<String> distinct = new HashSet<>();
    int solutions = 0;
    for (int k = 1; k < parts.length; k++) {
      String part = parts[k];
      int end = part.indexOf("Stop search");
      if (end >= 0) {
        part = part.substring(0, end);
      }
      // nurse, day -> shift
      int[][] shift = new int[4][3];
      for (int[] row : shift) {
        Arrays.fill(row, -1);
      }
      int day = -1;
      for (String line : part.split("\n")) {
        Matcher dm = Pattern.compile("^Day (\\d)").matcher(line);
        if (dm.find()) {
          day = Integer.parseInt(dm.group(1));
        }
        Matcher wm = Pattern.compile("Nurse (\\d) work shift (\\d)").matcher(line);
        if (wm.find()) {
          int n = Integer.parseInt(wm.group(1));
          assertEquals(-1, shift[n][day], "two shifts on a day");
          shift[n][day] = Integer.parseInt(wm.group(2));
        }
      }
      for (int d = 0; d < 3; d++) {
        for (int s = 0; s < 3; s++) {
          int workers = 0;
          for (int n = 0; n < 4; n++) {
            if (shift[n][d] == s) {
              workers++;
            }
          }
          assertEquals(1, workers);
        }
      }
      for (int n = 0; n < 4; n++) {
        int worked = 0;
        for (int d = 0; d < 3; d++) {
          if (shift[n][d] >= 0) {
            worked++;
          }
        }
        assertTrue(worked >= 2 && worked <= 3);
      }
      assertTrue(distinct.add(Arrays.deepToString(shift)));
      solutions++;
    }
    assertEquals(5, solutions);
  }

  @Test
  public void testRabbitsAndPheasantsSat() {
    String out = OutputCapture.run(() -> RabbitsAndPheasantsSat.main(new String[0]));
    int found = 0;
    for (int r = 0; r <= 100; r++) {
      for (int p = 0; p <= 100; p++) {
        if (r + p == 20 && 4 * r + 2 * p == 56) {
          assertEquals(8, r);
          assertEquals(12, p);
          found++;
        }
      }
    }
    assertEquals(1, found);
    assertContains(out, "8 rabbits, and 12 pheasants");
  }

  @Test
  public void testRankingSampleSat() {
    // tasks 0 and 1 (durations 1, 2) are performed, 2 and 3 (durations 3, 4) optional; the
    // best makespan of a set of tasks is the sum of their durations
    long best = Long.MAX_VALUE;
    for (int p2 = 0; p2 <= 1; p2++) {
      for (int p3 = 0; p3 <= 1; p3++) {
        long duration = 1 + 2 + 3 * p2 + 4 * p3;
        best = Math.min(best, -7 * (2 + p2 + p3) + 2 * duration);
      }
    }
    assertEquals(-9, best);
    String out = OutputCapture.run(() -> RankingSampleSat.main(new String[0]));
    assertEquals((double) best, numberAfter(out, "Optimal cost:"), 1e-9);
    long makespan = (long) numberAfter(out, "Makespan:");
    Matcher m = Pattern.compile("Task (\\d) starts at (\\d+) with rank (\\d+)").matcher(out);
    List<long[]> performed = new ArrayList<>();
    while (m.find()) {
      int t = Integer.parseInt(m.group(1));
      long start = Long.parseLong(m.group(2));
      performed.add(new long[] {t, start, start + t + 1, Long.parseLong(m.group(3))});
    }
    long cost = 2 * makespan - 7L * performed.size();
    assertEquals(best, cost);
    for (long[] a : performed) {
      assertTrue(a[2] <= makespan);
      long before = 0;
      for (long[] b : performed) {
        if (a != b) {
          assertTrue(a[2] <= b[1] || b[2] <= a[1], "overlap");
          if (b[1] < a[1]) {
            before++;
          }
        }
      }
      assertEquals(before, a[3]);
    }
    assertContains(out, "Task 3 in not performed and ranked at -1");
  }

  @Test
  public void testScheduleRequestsSat() {
    int[][][] requests = {
        {{0, 0, 1}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1}, {0, 1, 0}, {0, 0, 1}},
        {{0, 0, 0}, {0, 0, 0}, {0, 1, 0}, {0, 1, 0}, {1, 0, 0}, {0, 0, 0}, {0, 0, 1}},
        {{0, 1, 0}, {0, 1, 0}, {0, 0, 0}, {1, 0, 0}, {0, 0, 0}, {0, 1, 0}, {0, 0, 0}},
        {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {0, 1, 0}, {0, 0, 0}, {1, 0, 0}, {0, 0, 0}},
        {{0, 0, 0}, {0, 0, 1}, {0, 1, 0}, {0, 0, 0}, {1, 0, 0}, {0, 1, 0}, {0, 0, 0}}};
    // dynamic programming over the days; state = number of shifts of each nurse (base 6)
    Map<Integer, Integer> states = new HashMap<>();
    states.put(0, 0);
    for (int d = 0; d < 7; d++) {
      Map<Integer, Integer> next = new HashMap<>();
      for (Map.Entry<Integer, Integer> e : states.entrySet()) {
        int[] counts = decode(e.getKey());
        for (int n0 = 0; n0 < 5; n0++) {
          for (int n1 = 0; n1 < 5; n1++) {
            for (int n2 = 0; n2 < 5; n2++) {
              if (n0 == n1 || n0 == n2 || n1 == n2) {
                continue;
              }
              int[] c = counts.clone();
              c[n0]++;
              c[n1]++;
              c[n2]++;
              if (c[n0] > 5 || c[n1] > 5 || c[n2] > 5) {
                continue;
              }
              int gain = requests[n0][d][0] + requests[n1][d][1] + requests[n2][d][2];
              next.merge(encode(c), e.getValue() + gain, Math::max);
            }
          }
        }
      }
      states = next;
    }
    int best = -1;
    for (Map.Entry<Integer, Integer> e : states.entrySet()) {
      int[] c = decode(e.getKey());
      if (Arrays.stream(c).allMatch(x -> x >= 4 && x <= 5)) {
        best = Math.max(best, e.getValue());
      }
    }
    assertEquals(13, best);
    String out = OutputCapture.run(() -> ScheduleRequestsSat.main(new String[0]));
    assertEquals((double) best, numberAfter(out, "Number of shift requests met ="), 1e-9);
    assertContains(out, "(out of 20)");
    Matcher m = Pattern.compile("Nurse (\\d) works shift (\\d) \\((not requested|requested)\\)")
        .matcher(out);
    int met = 0;
    int assigned = 0;
    while (m.find()) {
      assigned++;
      if (m.group(3).equals("requested")) {
        met++;
      }
    }
    assertEquals(21, assigned);
    assertEquals(best, met);
  }

  private static int[] decode(int key) {
    int[] c = new int[5];
    for (int i = 0; i < 5; i++) {
      c[i] = key % 6;
      key /= 6;
    }
    return c;
  }

  private static int encode(int[] c) {
    int key = 0;
    for (int i = 4; i >= 0; i--) {
      key = key * 6 + c[i];
    }
    return key;
  }

  @Test
  public void testSearchForAllSolutionsSampleSat() {
    String out = OutputCapture.run(() -> SearchForAllSolutionsSampleSat.main(new String[0]));
    int expected = 0;
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        for (int z = 0; z < 3; z++) {
          if (x != y) {
            expected++;
          }
        }
      }
    }
    assertEquals(18, expected);
    assertContains(out, expected + " solutions found.");
    assertEquals(expected, count(out, "^Solution #"));
    assertXyzSolutionsValid(out, true);
  }

  // checks each "x = .. y = .. z = .." block: x != y and, if required, all distinct
  private static void assertXyzSolutionsValid(String out, boolean distinct) {
    Matcher m = Pattern.compile("x = (\\d+)\\n\\s+y = (\\d+)\\n\\s+z = (\\d+)").matcher(out);
    Set<String> seen = new HashSet<>();
    int n = 0;
    while (m.find()) {
      assertFalse(m.group(1).equals(m.group(2)), m.group());
      if (distinct) {
        assertTrue(seen.add(m.group(1) + m.group(2) + m.group(3)));
      }
      n++;
    }
    assertTrue(n > 0);
  }

  @Test
  public void testSimpleSatProgram() {
    String out = OutputCapture.run(() -> SimpleSatProgram.main(new String[0]));
    long x = (long) numberAfter(out, "x =");
    long y = (long) numberAfter(out, "y =");
    long z = (long) numberAfter(out, "z =");
    assertTrue(x != y && x >= 0 && x < 3 && y >= 0 && y < 3 && z >= 0 && z < 3);
  }

  @Test
  public void testSolveWithTimeLimitSampleSat() {
    String out = OutputCapture.run(() -> SolveWithTimeLimitSampleSat.main(new String[0]));
    long x = (long) numberAfter(out, "x =");
    long y = (long) numberAfter(out, "y =");
    assertTrue(x != y);
  }

  private static long bestXyz() {
    long best = Long.MIN_VALUE;
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        for (int z = 0; z < 3; z++) {
          if (x != y) {
            best = Math.max(best, x + 2 * y + 3 * z);
          }
        }
      }
    }
    return best;
  }

  @Test
  public void testSolutionHintingSampleSat() {
    assertEquals(11, bestXyz());
    String out = OutputCapture.run(() -> SolutionHintingSampleSat.main(new String[0]));
    List<Double> objectives = OutputCapture.numbersAfter(out, "objective value =");
    assertFalse(objectives.isEmpty());
    assertEquals((double) bestXyz(), objectives.get(objectives.size() - 1), 1e-9);
    assertXyzSolutionsValid(out, true);
  }

  @Test
  public void testSolveAndPrintIntermediateSolutionsSampleSat() {
    String out =
        OutputCapture.run(() -> SolveAndPrintIntermediateSolutionsSampleSat.main(new String[0]));
    List<Double> objectives = OutputCapture.numbersAfter(out, "objective value =");
    assertFalse(objectives.isEmpty());
    for (int i = 1; i < objectives.size(); i++) {
      assertTrue(objectives.get(i) > objectives.get(i - 1), "solutions improve");
    }
    assertEquals((double) bestXyz(), objectives.get(objectives.size() - 1), 1e-9);
    assertEquals((double) objectives.size(), numberAfter(out, "solution count:"), 1e-9);
    assertTrue(numberAfter(out, "best bound count:") >= 1);
    assertXyzSolutionsValid(out, true);
  }

  @Test
  public void testStepFunctionSampleSat() {
    String out = OutputCapture.run(() -> StepFunctionSampleSat.main(new String[0]));
    // brute force over x, expr and the three Booleans
    Set<String> expected = new HashSet<>();
    int count = 0;
    for (int x = 0; x <= 20; x++) {
      for (int expr = 0; expr <= 3; expr++) {
        for (int b = 0; b < 8; b++) {
          boolean b0 = (b & 1) != 0;
          boolean b2 = (b & 2) != 0;
          boolean b3 = (b & 4) != 0;
          boolean in0 = Arrays.asList(5, 6, 8, 9, 10).contains(x);
          boolean in2 = x <= 1 || (x >= 3 && x <= 4) || x >= 11;
          if ((b0 && !(in0 && expr == 0)) || (b2 && !(in2 && expr == 2))
              || (b3 && !(x == 7 && expr == 3)) || !(b0 || b2 || b3)) {
            continue;
          }
          count++;
          expected.add("x=" + x + " expr=" + expr);
        }
      }
    }
    // the printed (x, expr) pairs; each solution has a unique Boolean part
    assertEquals(20, count);
    assertEquals(count, expected.size());
    Matcher m = Pattern.compile("x=\\d+ expr=\\d+").matcher(out);
    Set<String> printed = new HashSet<>();
    int lines = 0;
    while (m.find()) {
      printed.add(m.group());
      lines++;
    }
    assertEquals(count, lines);
    assertEquals(expected, printed);
  }

  @Test
  public void testStopAfterNSolutionsSampleSat() {
    String out = OutputCapture.run(() -> StopAfterNSolutionsSampleSat.main(new String[0]));
    assertContains(out, "Stop search after 5 solutions");
    assertContains(out, "5 solutions found.");
    assertEquals(5, count(out, "^Solution #"));
    Matcher m = Pattern.compile("x = (\\d+)\\n\\s+y = (\\d+)\\n\\s+z = (\\d+)").matcher(out);
    Set<String> seen = new HashSet<>();
    while (m.find()) {
      assertTrue(seen.add(m.group()));
    }
    assertEquals(5, seen.size());
  }

  // ---------------------------------------------------------------------------- model samples

  @Test
  public void testBoolOrSampleSat() {
    String out = OutputCapture.run(() -> BoolOrSampleSat.main(new String[0]));
    assertEquals("", out.trim());
    CpModel model = new CpModel();
    BoolVar x = model.newBoolVar("x");
    BoolVar y = model.newBoolVar("y");
    model.addBoolOr(new Literal[] {x, y.not()});
    assertEquals("", model.validate());
  }

  @Test
  public void testLiteralSampleSat() {
    String out = OutputCapture.run(() -> LiteralSampleSat.main(new String[0]));
    assertEquals("not(x(0..1))", out.trim());
  }

  @Test
  public void testReifiedSampleSat() {
    String out = OutputCapture.run(() -> ReifiedSampleSat.main(new String[0]));
    assertEquals("", out.trim());
    CpModel model = new CpModel();
    BoolVar x = model.newBoolVar("x");
    BoolVar y = model.newBoolVar("y");
    BoolVar b = model.newBoolVar("b");
    model.addBoolAnd(new Literal[] {x, y.not()}).onlyEnforceIf(b);
    model.addImplication(b, x);
    model.addImplication(b, y.not());
    model.addBoolOr(new Literal[] {b.not(), x});
    model.addBoolOr(new Literal[] {b.not(), y.not()});
    assertEquals("", model.validate());
  }

  @Test
  public void testIntervalSampleSat() {
    String out = OutputCapture.run(() -> IntervalSampleSat.main(new String[0]));
    assertContains(out, "name: \"interval\"");
    assertContains(out, "name: \"fixed_size_interval_var\"");
    assertContains(out, "name: \"fixed_interval\"");
    assertEquals(3, count(out, "^interval \\{"));
    CpModel model = new CpModel();
    IntVar start = model.newIntVar(0, 100, "start");
    IntVar end = model.newIntVar(0, 100, "end");
    model.newIntervalVar(start, LinearExpr.constant(10), LinearExpr.affine(end, 1, 2), "interval");
    model.newFixedSizeIntervalVar(start, 10, "fixed_size_interval_var");
    model.newFixedInterval(5, 10, "fixed_interval");
    assertEquals("", model.validate());
  }

  @Test
  public void testOptionalIntervalSampleSat() {
    String out = OutputCapture.run(() -> OptionalIntervalSampleSat.main(new String[0]));
    assertEquals(3, count(out, "^enforcement_literal: 2$"));
    assertFind(out, "name: \"fixed_interval\"");
    CpModel model = new CpModel();
    IntVar start = model.newIntVar(0, 100, "start");
    IntVar end = model.newIntVar(0, 100, "end");
    Literal presence = model.newBoolVar("presence");
    model.newOptionalIntervalVar(start, LinearExpr.constant(10), LinearExpr.affine(end, 1, 2),
        presence, "interval");
    model.newOptionalFixedSizeIntervalVar(start, 10, presence, "fixed_size_interval_var");
    model.newOptionalFixedInterval(5, 10, presence, "fixed_interval");
    assertEquals("", model.validate());
  }
}
