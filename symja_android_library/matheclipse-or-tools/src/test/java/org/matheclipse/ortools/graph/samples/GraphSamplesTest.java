package org.matheclipse.ortools.graph.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.matheclipse.ortools.testutil.OutputCapture.assertContains;
import static org.matheclipse.ortools.testutil.OutputCapture.assertFind;
import static org.matheclipse.ortools.testutil.OutputCapture.count;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.graph.GraphReference;
import org.matheclipse.ortools.testutil.OutputCapture;

/**
 * Runs the graph samples (including the former examples <code>FlowExample</code> and
 * <code>LinearAssignmentAPI</code>) and checks their printed results against values computed
 * independently from the sample data (see {@link GraphReference}).
 */
public class GraphSamplesTest {

  // The 4x4 worker/task cost matrix of the assignment samples.
  private static final long[][] ASSIGNMENT_COSTS = {
      {90, 76, 75, 70}, {35, 85, 55, 65}, {125, 95, 90, 105}, {45, 110, 95, 115}};

  @Test
  public void testSimpleMaxFlowProgram() {
    String out = OutputCapture.run(() -> SimpleMaxFlowProgram.main(new String[0]));
    assertContains(out, "Max. flow: 60");

    int[] tails = {0, 0, 0, 1, 1, 2, 2, 3, 3};
    int[] heads = {1, 2, 3, 2, 4, 3, 4, 2, 4};
    long[] capacities = {20, 30, 10, 40, 30, 10, 20, 5, 20};
    assertEquals(60, GraphReference.maxFlow(5, tails, heads, capacities, 0, 4));

    // "tail -> head    flow  /  capacity" for every arc, in arc order.
    Matcher m = Pattern.compile("^(\\d+) -> (\\d+)\\s+(\\d+)\\s+/\\s+(\\d+)$", Pattern.MULTILINE)
        .matcher(out);
    long[] flows = new long[tails.length];
    int arc = 0;
    while (m.find()) {
      assertEquals(tails[arc], Integer.parseInt(m.group(1)));
      assertEquals(heads[arc], Integer.parseInt(m.group(2)));
      assertEquals(capacities[arc], Long.parseLong(m.group(4)));
      flows[arc] = Long.parseLong(m.group(3));
      arc++;
    }
    assertEquals(tails.length, arc);
    GraphReference.assertFlowConservation(5, tails, heads, capacities, flows,
        new long[] {60, 0, 0, 0, -60});
  }

  @Test
  public void testSimpleMinCostFlowProgram() {
    String out = OutputCapture.run(() -> SimpleMinCostFlowProgram.main(new String[0]));
    assertContains(out, "Minimum cost: 150");

    int[] tails = {0, 0, 1, 1, 1, 2, 2, 3, 4};
    int[] heads = {1, 2, 2, 3, 4, 3, 4, 4, 2};
    long[] capacities = {15, 8, 20, 4, 10, 15, 4, 20, 5};
    long[] unitCosts = {4, 4, 2, 2, 6, 1, 3, 2, 3};
    long[] supplies = {20, 0, 0, -5, -15};
    assertEquals(Long.valueOf(150),
        GraphReference.minCostFlow(5, tails, heads, capacities, unitCosts, supplies));

    // "tail -> head  flow  / capacity       cost" for every arc, in arc order.
    Matcher m = Pattern
        .compile("^(\\d+) -> (\\d+)\\s+(\\d+)\\s+/\\s+(\\d+)\\s+(\\d+)$", Pattern.MULTILINE)
        .matcher(out);
    long[] flows = new long[tails.length];
    long totalCost = 0;
    int arc = 0;
    while (m.find()) {
      assertEquals(tails[arc], Integer.parseInt(m.group(1)));
      assertEquals(heads[arc], Integer.parseInt(m.group(2)));
      flows[arc] = Long.parseLong(m.group(3));
      assertEquals(flows[arc] * unitCosts[arc], Long.parseLong(m.group(5)));
      totalCost += Long.parseLong(m.group(5));
      arc++;
    }
    assertEquals(tails.length, arc);
    assertEquals(150, totalCost);
    GraphReference.assertFlowConservation(5, tails, heads, capacities, flows, supplies);
  }

  @Test
  public void testAssignmentLinearSumAssignment() {
    String out = OutputCapture.run(() -> AssignmentLinearSumAssignment.main(new String[0]));
    assertContains(out, "Total cost: 265");
    assertEquals(Long.valueOf(265), GraphReference.assignment(ASSIGNMENT_COSTS));
    checkAssignmentLines(out,
        "^Worker (\\d+) assigned to task (\\d+)\\. Cost: (\\d+)$", 0, 0, 265);
  }

  @Test
  public void testAssignmentMinFlow() {
    String out = OutputCapture.run(() -> AssignmentMinFlow.main(new String[0]));
    assertContains(out, "Total cost: 265");
    // Workers are nodes 1..4, tasks nodes 5..8.
    checkAssignmentLines(out, "^Worker (\\d+) assigned to task (\\d+) Cost: (\\d+)$", 1, 5, 265);
  }

  @Test
  public void testBalanceMinFlow() {
    String out = OutputCapture.run(() -> BalanceMinFlow.main(new String[0]));
    assertContains(out, "Total cost: 250");

    int[] tails = {0, 0, 11, 11, 11, 12, 12, 12, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4,
        5, 5, 5, 5, 6, 6, 6, 6, 7, 8, 9, 10};
    int[] heads = {11, 12, 1, 3, 5, 2, 4, 6, 7, 8, 9, 10, 7, 8, 9, 10, 7, 8, 9, 10, 7, 8, 9, 10,
        7, 8, 9, 10, 7, 8, 9, 10, 13, 13, 13, 13};
    long[] capacities = {2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    long[] unitCosts = {0, 0, 0, 0, 0, 0, 0, 0, 90, 76, 75, 70, 35, 85, 55, 65, 125, 95, 90, 105,
        45, 110, 95, 115, 60, 105, 80, 75, 45, 65, 110, 95, 0, 0, 0, 0};
    long[] supplies = new long[14];
    supplies[0] = 4;
    supplies[13] = -4;
    assertEquals(Long.valueOf(250),
        GraphReference.minCostFlow(14, tails, heads, capacities, unitCosts, supplies));

    // Two workers of each team (odd: 1,3,5; even: 2,4,6), four distinct tasks 7..10.
    Matcher m = Pattern.compile("^Worker (\\d+) assigned to task (\\d+) Cost: (\\d+)$",
        Pattern.MULTILINE).matcher(out);
    Set<Integer> workers = new HashSet<>();
    Set<Integer> tasks = new HashSet<>();
    int odd = 0;
    long total = 0;
    while (m.find()) {
      int worker = Integer.parseInt(m.group(1));
      assertTrue(workers.add(worker));
      assertTrue(tasks.add(Integer.parseInt(m.group(2))));
      if ((worker & 1) == 1) {
        odd++;
      }
      total += Long.parseLong(m.group(3));
    }
    assertEquals(4, workers.size());
    assertEquals(2, odd);
    assertEquals(250, total);
  }

  // Checks the "Worker w assigned to task t ... Cost: c" lines: a permutation whose costs are
  // those of ASSIGNMENT_COSTS and sum to expectedTotal.
  private static void checkAssignmentLines(String out, String regex, int workerOffset,
      int taskOffset, long expectedTotal) {
    assertEquals(4, count(out, regex));
    Matcher m = Pattern.compile(regex, Pattern.MULTILINE).matcher(out);
    Set<Integer> tasks = new HashSet<>();
    long total = 0;
    while (m.find()) {
      int worker = Integer.parseInt(m.group(1)) - workerOffset;
      int task = Integer.parseInt(m.group(2)) - taskOffset;
      long cost = Long.parseLong(m.group(3));
      assertTrue(tasks.add(task), "task assigned twice");
      assertEquals(ASSIGNMENT_COSTS[worker][task], cost);
      total += cost;
    }
    assertEquals(expectedTotal, total);
  }

  @Test
  public void testFlowExample() {
    String out = OutputCapture.run(() -> FlowExample.main(new String[0]));
    assertContains(out, "Min Cost Flow Problem - Simple interface");
    assertContains(out, "total flow = 275/275");
    assertContains(out, "Max Flow Problem - Simple interface");
    assertContains(out, "Total flow 10/10");

    // Min cost flow: an assignment of the sources 0..3 to the targets 4..7.
    final long[][] costs = {
        {90, 75, 75, 80}, {35, 85, 55, 65}, {125, 95, 90, 105}, {45, 110, 95, 115}};
    assertEquals(Long.valueOf(275), GraphReference.assignment(costs));
    Matcher m = Pattern.compile("^From source (\\d+) to target (\\d+): cost (\\d+)$",
        Pattern.MULTILINE).matcher(out);
    Set<Integer> sources = new HashSet<>();
    Set<Integer> targets = new HashSet<>();
    long total = 0;
    while (m.find()) {
      int source = Integer.parseInt(m.group(1));
      int target = Integer.parseInt(m.group(2));
      assertTrue(sources.add(source));
      assertTrue(targets.add(target));
      assertEquals(costs[source][target - 4], Long.parseLong(m.group(3)));
      total += Long.parseLong(m.group(3));
    }
    assertEquals(4, sources.size());
    assertEquals(275, total);

    // Max flow: a flow of value 10 from 0 to 5 respecting capacities and conservation.
    final int[] tails = {0, 0, 0, 0, 1, 2, 3, 3, 4};
    final int[] heads = {1, 2, 3, 4, 3, 4, 4, 5, 5};
    final long[] capacities = {5, 8, 5, 3, 4, 5, 6, 6, 4};
    assertEquals(10, GraphReference.maxFlow(6, tails, heads, capacities, 0, 5));
    Matcher f = Pattern.compile("^From source (\\d+) to target (\\d+): (\\d+) / (\\d+)$",
        Pattern.MULTILINE).matcher(out);
    long[] flows = new long[tails.length];
    int arc = 0;
    while (f.find()) {
      assertEquals(tails[arc], Integer.parseInt(f.group(1)));
      assertEquals(heads[arc], Integer.parseInt(f.group(2)));
      assertEquals(capacities[arc], Long.parseLong(f.group(4)));
      flows[arc] = Long.parseLong(f.group(3));
      arc++;
    }
    assertEquals(tails.length, arc);
    GraphReference.assertFlowConservation(6, tails, heads, capacities, flows,
        new long[] {10, 0, 0, 0, 0, -10});
  }

  @Test
  public void testLinearAssignmentAPI() {
    String out = OutputCapture.run(() -> LinearAssignmentAPI.main(new String[0]));
    Matcher total = assertFind(out, "^Total cost = (\\d+)/(\\d+)$");
    assertEquals(total.group(1), total.group(2));
    final long[][] cost = {
        {90, 76, 75, 80}, {35, 85, 55, 65}, {125, 95, 90, 105}, {45, 110, 95, 115}};
    // The example's expected cost cost[0][3] + cost[1][2] + cost[2][1] + cost[3][0].
    assertEquals(275, Long.parseLong(total.group(1)));
    assertEquals(Long.valueOf(275), GraphReference.assignment(cost));

    // The optimum is unique: 0->3, 1->2, 2->1, 3->0.
    final int[] mates = {3, 2, 1, 0};
    Matcher m = Pattern.compile("^Left node (\\d+) assigned to right node (\\d+) with cost (\\d+)$",
        Pattern.MULTILINE).matcher(out);
    int lines = 0;
    while (m.find()) {
      int left = Integer.parseInt(m.group(1));
      int right = Integer.parseInt(m.group(2));
      assertEquals(mates[left], right);
      assertEquals(cost[left][right], Long.parseLong(m.group(3)));
      lines++;
    }
    assertEquals(4, lines);
  }
}
