package org.matheclipse.ortools.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.constraintsolver.Assignment;
import org.matheclipse.ortools.util.Duration;

/**
 * Checks the simplified routing engine behind {@link Model} on small models against brute force
 * optima and the documented OR-Tools semantics, including the hard and soft disjunction
 * cardinalities of OR-Tools 10.0.
 */
public class RoutingEngineTest {

  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  private static long[][] randomMatrix(int n, long seed, boolean symmetric) {
    Random rnd = new Random(seed);
    long[][] m = new long[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i != j) {
          m[i][j] = symmetric && j < i ? m[j][i] : 1 + rnd.nextInt(100);
        }
      }
    }
    return m;
  }

  private static long bruteForceTour(long[][] m) {
    int n = m.length;
    int[] perm = new int[n - 1];
    for (int i = 0; i < n - 1; i++) {
      perm[i] = i + 1;
    }
    return permute(m, perm, 0);
  }

  private static long permute(long[][] m, int[] perm, int k) {
    if (k == perm.length) {
      long c = m[0][perm[0]] + m[perm[perm.length - 1]][0];
      for (int i = 0; i + 1 < perm.length; i++) {
        c += m[perm[i]][perm[i + 1]];
      }
      return c;
    }
    long best = Long.MAX_VALUE;
    for (int i = k; i < perm.length; i++) {
      int t = perm[k];
      perm[k] = perm[i];
      perm[i] = t;
      best = Math.min(best, permute(m, perm, k + 1));
      perm[i] = perm[k];
      perm[k] = t;
    }
    return best;
  }

  private static long tourLength(Model routing, Assignment solution, long[][] m,
      IndexManager manager) {
    long length = 0;
    int visited = 0;
    long index = routing.start(0);
    while (!routing.isEnd(index)) {
      long next = solution.value(routing.nextVar(index));
      length += m[manager.indexToNode(index)][manager.indexToNode(next)];
      index = next;
      visited++;
    }
    assertEquals(m.length, visited);
    return length;
  }

  /** The cardinalities of a disjunction must lie between 0 and its number of indices. */
  @Test
  public void testDisjunctionCardinalityChecks() {
    IndexManager manager = new IndexManager(4, 1, 0);
    Model routing = new Model(manager);
    long[] two = {manager.nodeToIndex(1), manager.nodeToIndex(2)};
    // a mandatory disjunction can't ask for more active indices than it has
    assertThrows(IllegalArgumentException.class, () -> routing.addDisjunction(two, -1, 3));
    assertThrows(IllegalArgumentException.class,
        () -> routing.addDisjunction(two, Long.MAX_VALUE, 3));
    assertThrows(IllegalArgumentException.class, () -> routing.addDisjunction(two, 10, 0));
    int d = routing.makeDisjunction(two);
    assertThrows(IllegalArgumentException.class, () -> routing.setDisjunctionHardMinimum(d, 3));
    assertThrows(IllegalArgumentException.class, () -> routing.setDisjunctionHardMaximum(d, -1));
    assertThrows(IllegalArgumentException.class,
        () -> routing.setDisjunctionSoftMinimum(d, 3, 10, Model.PENALIZE_ONCE));
    assertThrows(IllegalArgumentException.class,
        () -> routing.setDisjunctionSoftMaximum(d, -1, 10, Model.PENALIZE_ONCE));
    assertThrows(IllegalArgumentException.class,
        () -> routing.setDisjunctionSoftMaximum(d, 1, -10, Model.PENALIZE_ONCE));
    // the bounds 0 and the size are allowed
    routing.setDisjunctionHardMinimum(d, 0);
    routing.setDisjunctionHardMaximum(d, 2);
    routing.setDisjunctionSoftMinimum(d, 2, 10, Model.PENALIZE_PER_INACTIVE);
    assertEquals(2, routing.getDisjunctionMaxCardinality(d));
    assertEquals(2, routing.getDisjunctionSoftMinCardinality(d));
    // a soft disjunction whose maximum equals its size is accepted
    assertEquals(d + 1, routing.addDisjunction(two, 10, 2));
  }

  @Test
  public void testTspAllFirstSolutionStrategies() {
    long[][] m = randomMatrix(8, 42, false);
    long optimum = bruteForceTour(m);
    for (FirstSolutionStrategy.Value strategy : FirstSolutionStrategy.Value.values()) {
      if (strategy == FirstSolutionStrategy.Value.UNRECOGNIZED
          || strategy == FirstSolutionStrategy.Value.ALL_UNPERFORMED) {
        continue;
      }
      IndexManager manager = new IndexManager(m.length, 1, 0);
      Model routing = new Model(manager);
      routing.setArcCostEvaluatorOfAllVehicles(routing.registerTransitMatrix(m));
      Assignment solution = routing.solveWithParameters(Globals.defaultRoutingSearchParameters()
          .toBuilder().setFirstSolutionStrategy(strategy).build());
      assertNotNull(solution, strategy.toString());
      assertEquals(RoutingSearchStatus.Value.ROUTING_SUCCESS, routing.status());
      assertEquals(tourLength(routing, solution, m, manager), solution.objectiveValue());
      assertEquals(optimum, solution.objectiveValue(), strategy.toString());
    }
  }

  @Test
  public void testAllUnperformedWithMandatoryNodesFails() {
    IndexManager manager = new IndexManager(4, 1, 0);
    Model routing = new Model(manager);
    routing.setArcCostEvaluatorOfAllVehicles(routing.registerTransitMatrix(randomMatrix(4, 1,
        true)));
    Assignment solution = routing.solveWithParameters(Globals.defaultRoutingSearchParameters()
        .toBuilder().setFirstSolutionStrategy(FirstSolutionStrategy.Value.ALL_UNPERFORMED)
        .build());
    assertNull(solution);
    assertEquals(RoutingSearchStatus.Value.ROUTING_FAIL, routing.status());
  }

  @Test
  public void testInvalidParameters() {
    IndexManager manager = new IndexManager(4, 1, 0);
    Model routing = new Model(manager);
    assertNull(routing.solveWithParameters(RoutingSearchParameters.newBuilder().build()));
    assertEquals(RoutingSearchStatus.Value.ROUTING_INVALID, routing.status());
    assertTrue(!Globals.findErrorInRoutingSearchParameters(
        RoutingSearchParameters.newBuilder().build()).isEmpty());
    assertEquals("", Globals.findErrorInRoutingSearchParameters(
        Globals.defaultRoutingSearchParameters()));
    RoutingSearchParameters merged = RoutingSearchParameters.newBuilder()
        .mergeFrom(Globals.defaultRoutingSearchParameters())
        .setTimeLimit(Duration.newBuilder().setSeconds(2)).build();
    assertEquals(2, merged.getTimeLimit().getSeconds());
    assertEquals(0.1, merged.getGuidedLocalSearchLambdaCoefficient());
    assertEquals(FirstSolutionStrategy.Value.AUTOMATIC, merged.getFirstSolutionStrategy());
    assertEquals(8, FirstSolutionStrategy.Value.PARALLEL_CHEAPEST_INSERTION.getNumber());
    assertEquals(FirstSolutionStrategy.Value.SAVINGS, FirstSolutionStrategy.Value.forNumber(10));
  }

  @Test
  public void testForbiddenArcsAndVehicleRestrictions() {
    long[][] m = randomMatrix(7, 7, true);
    IndexManager manager = new IndexManager(7, 2, 0);
    Model routing = new Model(manager);
    routing.setArcCostEvaluatorOfAllVehicles(routing.registerTransitMatrix(m));
    // node 3 only on vehicle 1, node 4 not directly after node 2 or the start
    routing.vehicleVar(manager.nodeToIndex(3)).setValue(1);
    routing.nextVar(manager.nodeToIndex(2)).removeValue(manager.nodeToIndex(4));
    for (int v = 0; v < 2; v++) {
      routing.nextVar(routing.start(v)).removeValue(manager.nodeToIndex(4));
    }
    routing.setFixedCostOfVehicle(1000, 0);
    Assignment solution = routing.solve();
    assertNotNull(solution);
    assertEquals(1, solution.value(routing.vehicleVar(manager.nodeToIndex(3))));
    assertTrue(solution.value(routing.nextVar(manager.nodeToIndex(2))) != manager.nodeToIndex(4));
    // the fixed cost of vehicle 0 makes a single route on vehicle 1 cheaper
    assertTrue(!routing.isVehicleUsed(solution, 0));
    long cost = 0;
    for (int v = 0; v < 2; v++) {
      long index = routing.start(v);
      while (!routing.isEnd(index)) {
        long next = solution.value(routing.nextVar(index));
        cost += routing.getArcCostForVehicle(index, next, v);
        index = next;
      }
    }
    assertEquals(cost, solution.objectiveValue());
    assertEquals(bruteForceTour(m), cost);
  }

  @Test
  public void testDisjunctionPenaltiesAndSoftUpperBound() {
    // a line 0 - 1 - 2 - 3 - 4 with node 4 far away and optional
    long[][] m = new long[5][5];
    int[] x = {0, 1, 2, 3, 50};
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        m[i][j] = Math.abs(x[i] - x[j]);
      }
    }
    IndexManager manager = new IndexManager(5, 1, 0);
    Model routing = new Model(manager);
    int transit = routing.registerTransitMatrix(m);
    routing.setArcCostEvaluatorOfAllVehicles(transit);
    routing.addDisjunction(new long[] {manager.nodeToIndex(4)}, 60);
    Assignment solution = routing.solve();
    assertNotNull(solution);
    // visiting node 4 costs 94 more than skipping it, its penalty is 60
    assertEquals(manager.nodeToIndex(4), solution.value(routing.nextVar(manager.nodeToIndex(4))));
    assertEquals(0, solution.value(routing.activeVar(manager.nodeToIndex(4))));
    assertEquals(-1, solution.value(routing.vehicleVar(manager.nodeToIndex(4))));
    assertEquals(6 + 60, solution.objectiveValue());

    // a soft upper bound on the cumul of node 3 in a dimension with free waiting
    IndexManager manager2 = new IndexManager(4, 1, 0);
    Model routing2 = new Model(manager2);
    long[][] m2 = {{0, 1, 2, 3}, {1, 0, 1, 2}, {2, 1, 0, 1}, {3, 2, 1, 0}};
    int t2 = routing2.registerTransitMatrix(m2);
    routing2.setArcCostEvaluatorOfAllVehicles(t2);
    assertTrue(routing2.addDimension(t2, 0, 100, true, "Distance"));
    Dimension d = routing2.getMutableDimension("Distance");
    d.setCumulVarSoftUpperBound(manager2.nodeToIndex(3), 1, 10);
    Assignment s2 = routing2.solve();
    assertNotNull(s2);
    // every tour through the line costs 6; the soft bound costs 10 * (arrival at 3 - 1), which
    // is smallest when node 3 is visited first (arrival 3)
    long arrival = s2.value(d.cumulVar(manager2.nodeToIndex(3)));
    assertEquals(3, arrival);
    assertEquals(6 + 10 * (3 - 1), s2.objectiveValue());
  }

  @Test
  public void testPickupDeliveryWithCapacity() {
    // pickups 1, 3 and deliveries 2, 4 with loads +1 / -1 and capacity 1: no interleaving
    long[][] m = randomMatrix(5, 3, true);
    IndexManager manager = new IndexManager(5, 1, 0);
    Model routing = new Model(manager);
    routing.setArcCostEvaluatorOfAllVehicles(routing.registerTransitMatrix(m));
    long[] demand = {0, 1, -1, 1, -1};
    int load = routing.registerUnaryTransitVector(demand);
    assertTrue(routing.addDimension(load, 0, 1, true, "Load"));
    routing.addPickupAndDelivery(manager.nodeToIndex(1), manager.nodeToIndex(2));
    routing.addPickupAndDelivery(manager.nodeToIndex(3), manager.nodeToIndex(4));
    Assignment solution = routing.solve();
    assertNotNull(solution);
    long[] order = new long[5];
    long index = routing.start(0);
    int pos = 0;
    while (!routing.isEnd(index)) {
      order[manager.indexToNode(index)] = pos++;
      index = solution.value(routing.nextVar(index));
    }
    assertTrue(order[1] < order[2] && order[3] < order[4]);
    assertTrue(order[2] < order[3] || order[4] < order[1], "interleaved pairs");
    // best of the two non-interleaved orders (each pair internally fixed)
    long a = m[0][1] + m[1][2] + m[2][3] + m[3][4] + m[4][0];
    long b = m[0][3] + m[3][4] + m[4][1] + m[1][2] + m[2][0];
    assertEquals(Math.min(a, b), solution.objectiveValue());
  }

  @Test
  public void testRoutesToAssignmentAndSolveFromAssignment() {
    long[][] m = randomMatrix(6, 11, true);
    IndexManager manager = new IndexManager(6, 2, 0);
    Model routing = new Model(manager);
    routing.setArcCostEvaluatorOfAllVehicles(routing.registerTransitMatrix(m));
    long[][] routes = {{manager.nodeToIndex(1), manager.nodeToIndex(2)},
        {manager.nodeToIndex(3), manager.nodeToIndex(4), manager.nodeToIndex(5)}};
    Assignment initial = routing.readAssignmentFromRoutes(routes, false);
    assertNotNull(initial);
    long expected = m[0][1] + m[1][2] + m[2][0] + m[0][3] + m[3][4] + m[4][5] + m[5][0];
    assertEquals(expected, initial.objectiveValue());
    long[][] back = new long[2][];
    routing.assignmentToRoutes(initial, back);
    assertEquals(routes[1].length, back[1].length);
    assertEquals(routes[1][2], back[1][2]);
    Assignment improved = routing.solveFromAssignmentWithParameters(initial,
        Globals.defaultRoutingSearchParameters());
    assertNotNull(improved);
    assertTrue(improved.objectiveValue() <= expected);
    assertEquals(bruteForceTour(m), improved.objectiveValue());
    // a route visiting a node twice is rejected
    long[][] invalid = {{manager.nodeToIndex(1)}, {manager.nodeToIndex(1)}};
    assertNull(routing.readAssignmentFromRoutes(invalid, false));
  }

  @Test
  public void testAtSolutionCallbackAndSolutionLimit() {
    long[][] m = randomMatrix(9, 5, true);
    IndexManager manager = new IndexManager(9, 1, 0);
    Model routing = new Model(manager);
    routing.setArcCostEvaluatorOfAllVehicles(routing.registerTransitMatrix(m));
    long[] calls = new long[1];
    long[] last = new long[1];
    routing.addAtSolutionCallback(() -> {
      calls[0]++;
      last[0] = routing.costVar().value();
      // the routing variables are bound during the callback
      assertTrue(routing.nextVar(routing.start(0)).bound());
    });
    Assignment solution = routing.solveWithParameters(Globals.defaultRoutingSearchParameters()
        .toBuilder().setSolutionLimit(3).build());
    assertNotNull(solution);
    assertEquals(3, calls[0]);
    assertTrue(solution.objectiveValue() <= last[0]);
  }

  /** A model on a line: depot 0 and nodes at the given positions, arc cost = distance. */
  private static Model lineModel(IndexManager manager, int[] x) {
    long[][] m = new long[x.length][x.length];
    for (int i = 0; i < x.length; i++) {
      for (int j = 0; j < x.length; j++) {
        m[i][j] = Math.abs(x[i] - x[j]);
      }
    }
    Model routing = new Model(manager);
    routing.setArcCostEvaluatorOfAllVehicles(routing.registerTransitMatrix(m));
    return routing;
  }

  @Test
  public void testDisjunctionCardinalitiesOfAddDisjunction() {
    IndexManager manager = new IndexManager(4, 1, 0);
    Model routing = lineModel(manager, new int[] {0, 1, 2, 3});
    long[] a = {manager.nodeToIndex(1)};
    long[] bc = {manager.nodeToIndex(2), manager.nodeToIndex(3)};
    // a negative penalty: hard, min = max = max cardinality
    int hard = routing.addDisjunction(a);
    assertEquals(1, routing.getDisjunctionMinCardinality(hard));
    assertEquals(1, routing.getDisjunctionMaxCardinality(hard));
    assertEquals(0, routing.getDisjunctionSoftMinCardinality(hard));
    assertEquals(-1, routing.getDisjunctionPenalty(hard));
    assertEquals(Long.MAX_VALUE, routing.unperformedPenalty(a[0]));
    // Long.MAX_VALUE is a hard disjunction too
    int hardMax = routing.addDisjunction(bc, Long.MAX_VALUE, 2);
    assertEquals(2, routing.getDisjunctionMinCardinality(hardMax));
    assertEquals(-1, routing.getDisjunctionPenalty(hardMax));
    // a soft disjunction: the max cardinality is the soft minimum
    int soft = routing.addDisjunction(bc, 7, 2, Model.PENALIZE_PER_INACTIVE);
    assertEquals(0, routing.getDisjunctionMinCardinality(soft));
    assertEquals(2, routing.getDisjunctionMaxCardinality(soft));
    assertEquals(2, routing.getDisjunctionSoftMinCardinality(soft));
    assertEquals(7, routing.getDisjunctionSoftMinPenalty(soft));
    assertEquals(7, routing.getDisjunctionPenalty(soft));
    assertEquals(Model.PENALIZE_PER_INACTIVE,
        routing.getDisjunctionSoftMinPenaltyCostBehavior(soft));
    assertEquals(Long.MAX_VALUE, routing.getDisjunctionSoftMaxCardinality(soft));
    assertEquals(0, routing.getDisjunctionSoftMaxPenalty(soft));
    assertEquals(Model.PENALIZE_ONCE, routing.getDisjunctionSoftMaxPenaltyCostBehavior(soft));
    // the three argument version penalizes once
    int once = routing.addDisjunction(bc, 5, 2);
    assertEquals(Model.PENALIZE_ONCE, routing.getDisjunctionSoftMinPenaltyCostBehavior(once));
    // a disjunction without bounds
    int free = routing.makeDisjunction(bc);
    assertEquals(0, routing.getDisjunctionMinCardinality(free));
    assertEquals(2, routing.getDisjunctionMaxCardinality(free));
    assertEquals(0, routing.getDisjunctionPenalty(free));
    assertEquals(5, routing.getNumberOfDisjunctions());
    assertEquals(4, routing.getDisjunctionIndices(bc[0]).length);
    // the index of b is in several disjunctions
    assertEquals(-3, routing.unperformedPenaltyOrValue(-3, bc[0]));
  }

  @Test
  public void testSoftMinimumPenalizedOnceOrPerInactive() {
    // depot 0, near nodes 1..3, two far nodes 4 and 5 at the same place: visiting one of them
    // costs as much as visiting both (100 instead of 6)
    int[] x = {0, 1, 2, 3, 50, 50};
    for (int behavior : new int[] {Model.PENALIZE_ONCE, Model.PENALIZE_PER_INACTIVE}) {
      IndexManager manager = new IndexManager(6, 1, 0);
      Model routing = lineModel(manager, x);
      long[] far = {manager.nodeToIndex(4), manager.nodeToIndex(5)};
      routing.addDisjunction(far, 60, 2, behavior);
      Assignment solution = routing.solve();
      assertNotNull(solution);
      long active = solution.value(routing.activeVar(far[0]))
          + solution.value(routing.activeVar(far[1]));
      if (behavior == Model.PENALIZE_ONCE) {
        // none: 6 + 60, one: 100 + 60, both: 100
        assertEquals(0, active);
        assertEquals(66, solution.objectiveValue());
      } else {
        // none: 6 + 120, one: 100 + 60, both: 100
        assertEquals(2, active);
        assertEquals(100, solution.objectiveValue());
      }
    }
  }

  @Test
  public void testHardMinimumOfMadeDisjunction() {
    // the cheaper far node is visited to reach the hard minimum of one
    IndexManager manager = new IndexManager(6, 1, 0);
    Model routing = lineModel(manager, new int[] {0, 1, 2, 3, 50, 60});
    long[] far = {manager.nodeToIndex(4), manager.nodeToIndex(5)};
    int d = routing.makeDisjunction(far);
    routing.setDisjunctionHardMinimum(d, 1);
    assertEquals(1, routing.getDisjunctionMinCardinality(d));
    assertEquals(-1, routing.getDisjunctionPenalty(d));
    Assignment solution = routing.solve();
    assertNotNull(solution);
    assertEquals(1, solution.value(routing.activeVar(far[0])));
    assertEquals(0, solution.value(routing.activeVar(far[1])));
    assertEquals(100, solution.objectiveValue());
    // a hard maximum of zero forbids both far nodes; with a minimum of one the model is
    // infeasible
    routing = lineModel(manager, new int[] {0, 1, 2, 3, 50, 60});
    d = routing.makeDisjunction(far);
    routing.setDisjunctionHardMaximum(d, 0);
    solution = routing.solve();
    assertNotNull(solution);
    assertEquals(6, solution.objectiveValue());
    routing = lineModel(manager, new int[] {0, 1, 2, 3, 50, 60});
    d = routing.makeDisjunction(far);
    routing.setDisjunctionHardMaximum(d, 0);
    routing.setDisjunctionHardMinimum(d, 1);
    assertNull(routing.solve());
  }

  @Test
  public void testSoftMinimumAndSoftMaximum() {
    // nodes 1, 2, 3 on a line; n active nodes cost 2 n (route) + 3 (3 - n) (soft minimum 3)
    // + 3 max(0, n - 1) (soft maximum 1): 9, 8, 10, 12 for n = 0 .. 3
    IndexManager manager = new IndexManager(4, 1, 0);
    Model routing = lineModel(manager, new int[] {0, 1, 2, 3});
    long[] nodes = {manager.nodeToIndex(1), manager.nodeToIndex(2), manager.nodeToIndex(3)};
    int d = routing.makeDisjunction(nodes);
    routing.setDisjunctionSoftMinimum(d, 3, 3, Model.PENALIZE_PER_INACTIVE);
    routing.setDisjunctionSoftMaximum(d, 1, 3, Model.PENALIZE_PER_INACTIVE);
    assertEquals(1, routing.getDisjunctionSoftMaxCardinality(d));
    assertEquals(3, routing.getDisjunctionSoftMaxPenalty(d));
    assertEquals(Model.PENALIZE_PER_INACTIVE,
        routing.getDisjunctionSoftMaxPenaltyCostBehavior(d));
    Assignment solution = routing.solve();
    assertNotNull(solution);
    assertEquals(8, solution.objectiveValue());
    assertEquals(1, solution.value(routing.activeVar(nodes[0])));
    assertEquals(0, solution.value(routing.activeVar(nodes[1])));
    assertEquals(0, solution.value(routing.activeVar(nodes[2])));
  }

  @Test
  public void testIgnoreDisjunctionsAlreadyForcedToZero() {
    IndexManager manager = new IndexManager(4, 1, 0);
    Model routing = lineModel(manager, new int[] {0, 1, 2, 3});
    long index = manager.nodeToIndex(3);
    int d = routing.addDisjunction(new long[] {index});
    routing.activeVar(index).setValue(0);
    // the hard disjunction requires the node which cannot be active
    assertNull(routing.solve());
    routing = lineModel(manager, new int[] {0, 1, 2, 3});
    d = routing.addDisjunction(new long[] {index});
    routing.activeVar(index).setValue(0);
    routing.ignoreDisjunctionsAlreadyForcedToZero();
    assertEquals(0, routing.getDisjunctionMinCardinality(d));
    assertEquals(0, routing.getDisjunctionMaxCardinality(d));
    Assignment solution = routing.solve();
    assertNotNull(solution);
    assertEquals(4, solution.objectiveValue());
  }

  @Test
  public void testVehicleClassesFollowAllowedVehicles() {
    IndexManager manager = new IndexManager(3, 2, 0);
    Model routing = lineModel(manager, new int[] {0, 1, 2});
    assertEquals(1, routing.getVehicleClassesCount());
    assertTrue(routing.isVehicleAllowedForIndex(1, manager.nodeToIndex(2)));
    routing.setAllowedVehiclesForIndex(new int[] {1}, manager.nodeToIndex(2));
    assertFalse(routing.isVehicleAllowedForIndex(0, manager.nodeToIndex(2)));
    assertEquals(2, routing.getVehicleClassesCount());
    assertEquals(0, routing.getVehicleClassIndexOfVehicle(0));
    assertEquals(1, routing.getVehicleClassIndexOfVehicle(1));
    Assignment solution = routing.solve();
    assertNotNull(solution);
    assertEquals(1, solution.value(routing.vehicleVar(manager.nodeToIndex(2))));
  }

  @Test
  public void testParameterValidationAndDefaults() {
    assertTrue(Globals.findErrorsInRoutingSearchParameters(
        Globals.defaultRoutingSearchParameters()).isEmpty());
    assertTrue(Globals.findErrorsInRoutingSearchParameters(
        Globals.defaultSecondaryRoutingSearchParameters()).isEmpty());
    assertEquals(LocalSearchMetaheuristic.Value.GREEDY_DESCENT,
        Globals.defaultSecondaryRoutingSearchParameters().getLocalSearchMetaheuristic());
    // the empty message violates several bounds; the first error is reported alone
    List<String> errors =
        Globals.findErrorsInRoutingSearchParameters(RoutingSearchParameters.newBuilder().build());
    assertTrue(errors.size() > 1, errors.toString());
    assertEquals("Invalid ls_operator_neighbors_ratio: 0", errors.get(0));
    assertEquals(errors.get(0), Globals.findErrorInRoutingSearchParameters(
        RoutingSearchParameters.newBuilder().build()));
    RoutingSearchParameters negative = Globals.defaultRoutingSearchParameters().toBuilder()
        .setTimeLimit(Duration.newBuilder().setSeconds(-1)).setSolutionLimit(0).build();
    errors = Globals.findErrorsInRoutingSearchParameters(negative);
    assertEquals(2, errors.size(), errors.toString());
    assertEquals("Invalid solution_limit: 0", errors.get(0));
    assertEquals("Invalid time_limit: seconds: -1", errors.get(1));
    assertEquals(0.1, Globals.defaultRoutingSearchParameters()
        .getGuidedLocalSearchLambdaCoefficient());
  }
}
