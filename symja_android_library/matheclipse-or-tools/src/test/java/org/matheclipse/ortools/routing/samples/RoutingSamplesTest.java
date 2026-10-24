package org.matheclipse.ortools.routing.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.matheclipse.ortools.routing.samples.RoutingOutput.assertCovers;
import static org.matheclipse.ortools.routing.samples.RoutingOutput.assertTour;
import static org.matheclipse.ortools.routing.samples.RoutingOutput.length;
import static org.matheclipse.ortools.routing.samples.RoutingOutput.routes;
import static org.matheclipse.ortools.testutil.OutputCapture.assertContains;
import static org.matheclipse.ortools.testutil.OutputCapture.numberAfter;
import static org.matheclipse.ortools.testutil.OutputCapture.numbersAfter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.constraintsolver.Assignment;
import org.matheclipse.ortools.routing.Dimension;
import org.matheclipse.ortools.routing.FirstSolutionStrategy;
import org.matheclipse.ortools.routing.Globals;
import org.matheclipse.ortools.routing.IndexManager;
import org.matheclipse.ortools.routing.LocalSearchMetaheuristic;
import org.matheclipse.ortools.routing.Model;
import org.matheclipse.ortools.routing.RoutingSearchParameters;
import org.matheclipse.ortools.routing.RoutingSearchStatus;
import org.matheclipse.ortools.routing.samples.RoutingOutput.Route;
import org.matheclipse.ortools.testutil.OutputCapture;
import org.matheclipse.ortools.util.Duration;

/**
 * Runs the routing samples (ported from <code>ortools/routing/samples/java</code> of OR-Tools 10.0)
 * and checks the feasibility of the printed routes, the consistency of the printed costs and the
 * objective against exact optima (small TSPs) or the values of the OR-Tools guide.
 */
public class RoutingSamplesTest {

  private static final List<Integer> DEPOT = Collections.singletonList(0);

  private static final List<Integer> NONE = Collections.emptyList();

  private static long[][] tspMatrix;

  /** The Manhattan matrix of the 17 locations of the Tsp and Vrp samples (identical data). */
  private static synchronized long[][] tspMatrix() {
    if (tspMatrix == null) {
      tspMatrix = new TspDistanceMatrix.DataModel().distanceMatrix;
    }
    return tspMatrix;
  }

  private static long tspOptimum17 = -1;

  private static synchronized long tspOptimum17() {
    if (tspOptimum17 < 0) {
      tspOptimum17 = RoutingOutput.shortestTour(tspMatrix());
    }
    return tspOptimum17;
  }

  /** Checks the routes of a VRP sample whose objective is distance + 100 * max route distance. */
  private static void assertGlobalSpanVrp(String out, long[][] matrix, long maxDistance,
      long expectedAtMost) {
    List<Route> rs = routes(out);
    long total = 0;
    long max = 0;
    for (Route r : rs) {
      long d = length(r, matrix);
      assertEquals(d, r.reported, "printed distance of " + r);
      assertTrue(d <= maxDistance, "distance capacity exceeded by " + r);
      total += d;
      max = Math.max(max, d);
    }
    if (out.contains("Maximum of the route distances:")) {
      assertEquals(max, (long) numberAfter(out, "Maximum of the route distances:"));
    }
    long objective = (long) numberAfter(out, "Objective :");
    assertEquals(total + 100 * max, objective);
    assertTrue(objective <= expectedAtMost, "objective " + objective + " > " + expectedAtMost);
  }

  @Test
  public void testSimpleRoutingProgram() {
    String out = OutputCapture.run(() -> SimpleRoutingProgram.main(new String[0]));
    List<Route> rs = routes(out);
    assertEquals(1, rs.size());
    assertTour(rs.get(0), 5);
    long[][] m = new long[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        m[i][j] = Math.abs(i - j);
      }
    }
    long optimum = RoutingOutput.shortestTour(m);
    assertEquals(8, optimum);
    assertEquals(optimum, (long) numberAfter(out, "Objective:"));
    assertEquals(optimum, length(rs.get(0), m));
    assertEquals(optimum, (long) numberAfter(out, "Distance of the route:"));
  }

  @Test
  public void testTsp() {
    String out = OutputCapture.run(() -> Tsp.main(new String[0]));
    assertContains(out, "Status: ROUTING_SUCCESS");
    List<Route> rs = routes(out);
    assertEquals(1, rs.size());
    assertTour(rs.get(0), 17);
    long optimum = tspOptimum17();
    assertEquals(4384, optimum);
    assertEquals(optimum, (long) numberAfter(out, "Objective :"));
    assertEquals(optimum, length(rs.get(0), tspMatrix()));
    assertEquals(optimum, rs.get(0).reported);
  }

  @Test
  public void testTspCities() {
    String out = OutputCapture.run(() -> TspCities.main(new String[0]));
    long[][] m = new TspCities.DataModel().distanceMatrix;
    List<Route> rs = routes(out);
    assertEquals(1, rs.size());
    assertTour(rs.get(0), m.length);
    long optimum = RoutingOutput.shortestTour(m);
    assertEquals(7293, optimum);
    assertEquals(optimum, (long) numberAfter(out, "Objective:"));
    assertEquals(optimum, length(rs.get(0), m));
    assertEquals(optimum, (long) numberAfter(out, "Route distance:"));
  }

  @Test
  public void testTspDistanceMatrix() {
    String out = OutputCapture.run(() -> TspDistanceMatrix.main(new String[0]));
    List<Route> rs = routes(out);
    assertEquals(1, rs.size());
    assertTour(rs.get(0), 17);
    assertEquals(tspOptimum17(), (long) numberAfter(out, "Objective :"));
    assertEquals(tspOptimum17(), length(rs.get(0), tspMatrix()));
    assertEquals(tspOptimum17(), rs.get(0).reported);
  }

  @Test
  public void testTspCircuitBoard() {
    String out = OutputCapture.run(() -> TspCircuitBoard.main(new String[0]));
    int[][] loc = new TspCircuitBoard.DataModel().locations;
    long[][] m = new long[loc.length][loc.length];
    for (int i = 0; i < loc.length; i++) {
      for (int j = 0; j < loc.length; j++) {
        m[i][j] = i == j ? 0 : (long) Math.hypot(loc[j][0] - loc[i][0], loc[j][1] - loc[i][1]);
      }
    }
    List<Route> rs = routes(out);
    assertEquals(1, rs.size());
    assertTour(rs.get(0), loc.length);
    long objective = (long) numberAfter(out, "Objective:");
    assertEquals(objective, length(rs.get(0), m));
    assertEquals(objective, (long) numberAfter(out, "Route distance:"));
    // the OR-Tools guide reports 2790 for this sample (path cheapest arc, greedy descent)
    assertTrue(objective <= 2790, "objective " + objective);
  }

  @Test
  public void testVrp() {
    String out = OutputCapture.run(() -> Vrp.main(new String[0]));
    assertContains(out, "Status: ROUTING_SUCCESS");
    List<Route> rs = routes(out);
    assertCovers(rs, 17, DEPOT, NONE);
    long total = 0;
    for (Route r : rs) {
      assertEquals(0, r.start());
      assertEquals(0, r.end());
      assertEquals(length(r, tspMatrix()), r.reported);
      total += r.reported;
    }
    assertEquals(total, (long) numberAfter(out, "Total Distance of all routes:"));
    assertEquals(total, (long) numberAfter(out, "Objective :"));
    // without capacities the best plan is the optimal tour of a single vehicle
    assertEquals(tspOptimum17(), total);
  }

  @Test
  public void testVrpGlobalSpan() {
    String out = OutputCapture.run(() -> VrpGlobalSpan.main(new String[0]));
    List<Route> rs = routes(out);
    assertCovers(rs, 17, DEPOT, NONE);
    // the OR-Tools guide reports four routes of 1552m, objective 161408
    assertGlobalSpanVrp(out, new VrpGlobalSpan.DataModel().distanceMatrix, 3000, 161408);
  }

  @Test
  public void testVrpStartsEnds() {
    String out = OutputCapture.run(() -> VrpStartsEnds.main(new String[0]));
    VrpStartsEnds.DataModel data = new VrpStartsEnds.DataModel();
    List<Route> rs = routes(out);
    List<Integer> depots = new ArrayList<>();
    for (int v = 0; v < data.vehicleNumber; v++) {
      depots.add(data.starts[v]);
      depots.add(data.ends[v]);
    }
    assertCovers(rs, 17, depots, NONE);
    for (Route r : rs) {
      assertEquals(data.starts[r.vehicle], r.start());
      assertEquals(data.ends[r.vehicle], r.end());
    }
    assertGlobalSpanVrp(out, data.distanceMatrix, 2000, Long.MAX_VALUE);
  }

  /** Checks the loads of a capacitated VRP output. */
  private static long assertLoads(List<Route> rs, long[] demands, long capacity) {
    long total = 0;
    for (Route r : rs) {
      long load = 0;
      for (int k = 0; k + 1 < r.nodes.size(); k++) {
        load += demands[r.nodes.get(k)];
        assertEquals(load, (long) r.loads.get(k), "printed load in " + r);
      }
      assertTrue(load <= capacity, "capacity exceeded by " + r);
      total += load;
    }
    return total;
  }

  @Tag("slow")
  @Test
  public void testVrpCapacity() {
    String out = OutputCapture.run(() -> VrpCapacity.main(new String[0]));
    VrpCapacity.DataModel data = new VrpCapacity.DataModel();
    List<Route> rs = routes(out);
    assertCovers(rs, 17, DEPOT, NONE);
    long load = assertLoads(rs, data.demands, 15);
    assertEquals(load, (long) numberAfter(out, "Total load of all routes:"));
    assertEquals(Arrays.stream(data.demands).sum(), load);
    long total = 0;
    for (Route r : rs) {
      assertEquals(length(r, data.distanceMatrix), r.reported);
      total += r.reported;
    }
    assertEquals(total, (long) numberAfter(out, "Total distance of all routes:"));
    assertEquals(total, (long) numberAfter(out, "Objective:"));
    // the OR-Tools guide reports a total distance of 6208m
    assertTrue(total <= 6208, "total distance " + total);
  }

  /** The VrpCapacity model with a short time limit. */
  @Test
  public void testVrpCapacityShortTimeLimit() {
    VrpCapacity.DataModel data = new VrpCapacity.DataModel();
    IndexManager manager =
        new IndexManager(data.distanceMatrix.length, data.vehicleNumber, data.depot);
    Model routing = new Model(manager);
    int transit = routing.registerTransitCallback((long i, long j) ->
        data.distanceMatrix[manager.indexToNode(i)][manager.indexToNode(j)]);
    routing.setArcCostEvaluatorOfAllVehicles(transit);
    int demand = routing.registerUnaryTransitCallback(
        (long i) -> data.demands[manager.indexToNode(i)]);
    assertTrue(routing.addDimensionWithVehicleCapacity(demand, 0, data.vehicleCapacities, true,
        "Capacity"));
    RoutingSearchParameters parameters = Globals.defaultRoutingSearchParameters().toBuilder()
        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
        .setTimeLimit(Duration.newBuilder().setNanos(200_000_000).build()).build();
    Assignment solution = routing.solveWithParameters(parameters);
    assertNotNull(solution);
    assertEquals(RoutingSearchStatus.Value.ROUTING_SUCCESS, routing.status());
    Dimension capacity = routing.getMutableDimension("Capacity");
    long total = 0;
    int visited = 0;
    for (int v = 0; v < data.vehicleNumber; v++) {
      long index = routing.start(v);
      while (!routing.isEnd(index)) {
        long next = solution.value(routing.nextVar(index));
        total += routing.getArcCostForVehicle(index, next, v);
        if (!routing.isStart(index)) {
          visited++;
        }
        index = next;
      }
      assertTrue(solution.value(capacity.cumulVar(index)) <= data.vehicleCapacities[v]);
    }
    assertEquals(16, visited);
    assertEquals(total, solution.objectiveValue());
  }

  /** Parses the dropped nodes and checks a VrpDropNodes output; returns the objective. */
  private static long assertDropNodesOutput(String out) {
    VrpDropNodes.DataModel data = new VrpDropNodes.DataModel();
    List<Integer> dropped = RoutingOutput.listAfter(out, "Dropped nodes:");
    List<Route> rs = routes(out);
    assertCovers(rs, 17, DEPOT, dropped);
    long load = assertLoads(rs, data.demands, 15);
    assertEquals(load, (long) numberAfter(out, "Total Load of all routes:"));
    long total = 0;
    for (Route r : rs) {
      assertEquals(length(r, data.distanceMatrix), r.reported);
      total += r.reported;
    }
    assertEquals(total, (long) numberAfter(out, "Total Distance of all routes:"));
    long objective = (long) numberAfter(out, "Objective:");
    assertEquals(total + 1000L * dropped.size(), objective);
    // the demands exceed the fleet capacity by 10, so nodes must be dropped
    long droppedDemand = 0;
    for (int x : dropped) {
      droppedDemand += data.demands[x];
    }
    assertTrue(droppedDemand >= 10);
    return objective;
  }

  @Tag("slow")
  @Test
  public void testVrpDropNodes() {
    String out = OutputCapture.run(() -> VrpDropNodes.main(new String[0]));
    long objective = assertDropNodesOutput(out);
    // two dropped nodes cost 2000; the remaining routes are shorter than the 6208m of VrpCapacity
    assertTrue(objective < 2000 + 6208, "objective " + objective);
  }

  @Test
  public void testVrpInitialRoutes() {
    String out = OutputCapture.run(() -> VrpInitialRoutes.main(new String[0]));
    VrpInitialRoutes.DataModel data = new VrpInitialRoutes.DataModel();
    int after = out.indexOf("Solution after search:");
    assertTrue(after > 0);
    String initial = out.substring(0, after);
    String searched = out.substring(after);
    List<Route> init = routes(initial);
    assertEquals(data.initialRoutes.length, init.size());
    for (Route r : init) {
      long[] expected = data.initialRoutes[r.vehicle];
      assertEquals(expected.length, r.visits().size());
      for (int k = 0; k < expected.length; k++) {
        assertEquals(expected[k], (long) r.visits().get(k));
      }
    }
    assertGlobalSpanVrp(initial, data.distanceMatrix, 3000, Long.MAX_VALUE);
    long initialObjective = (long) numberAfter(initial, "Objective :");
    assertCovers(routes(searched), 17, DEPOT, NONE);
    // the search starts from the initial routes; OR-Tools reaches 161408 (as VrpGlobalSpan)
    assertGlobalSpanVrp(searched, data.distanceMatrix, 3000,
        Math.min(initialObjective, 161408));
  }

  /** Checks a pickup and delivery output; <code>policy</code> 0 none, 1 LIFO, 2 FIFO. */
  private static long assertPickupDelivery(String out, long[][] matrix, int[][] pairs,
      int policy) {
    List<Route> rs = routes(out);
    assertCovers(rs, 17, DEPOT, NONE);
    for (int[] pair : pairs) {
      Route pr = null;
      Route dr = null;
      for (Route r : rs) {
        if (r.visits().contains(pair[0])) {
          pr = r;
        }
        if (r.visits().contains(pair[1])) {
          dr = r;
        }
      }
      assertTrue(pr != null && pr == dr, "pair " + Arrays.toString(pair) + " split in " + rs);
      assertTrue(pr.nodes.indexOf(pair[0]) < pr.nodes.indexOf(pair[1]),
          "delivery before pickup " + Arrays.toString(pair) + " in " + pr);
    }
    if (policy != 0) {
      for (Route r : rs) {
        List<Integer> open = new ArrayList<>();
        for (int x : r.visits()) {
          for (int[] pair : pairs) {
            if (pair[0] == x) {
              open.add(x);
            } else if (pair[1] == x) {
              int expected = policy == 1 ? open.get(open.size() - 1) : open.get(0);
              assertEquals(expected, pair[0], (policy == 1 ? "LIFO" : "FIFO") + " order in " + r);
              open.remove(Integer.valueOf(pair[0]));
            }
          }
        }
      }
    }
    assertGlobalSpanVrp(out, matrix, 3000, Long.MAX_VALUE);
    long total = 0;
    for (Route r : rs) {
      total += r.reported;
    }
    assertEquals(total, (long) numberAfter(out, "Total Distance of all routes:"));
    return (long) numberAfter(out, "Objective :");
  }

  @Test
  public void testVrpPickupDelivery() {
    String out = OutputCapture.run(() -> VrpPickupDelivery.main(new String[0]));
    VrpPickupDelivery.DataModel data = new VrpPickupDelivery.DataModel();
    long objective = assertPickupDelivery(out, data.distanceMatrix, data.pickupsDeliveries, 0);
    // the OR-Tools guide reports 226116 (total distance 6916m)
    assertTrue(objective <= 226116, "objective " + objective);
  }

  @Test
  public void testVrpPickupDeliveryFifo() {
    String out = OutputCapture.run(() -> VrpPickupDeliveryFifo.main(new String[0]));
    VrpPickupDeliveryFifo.DataModel data = new VrpPickupDeliveryFifo.DataModel();
    assertPickupDelivery(out, data.distanceMatrix, data.pickupsDeliveries, 2);
  }

  @Test
  public void testVrpPickupDeliveryLifo() {
    String out = OutputCapture.run(() -> VrpPickupDeliveryLifo.main(new String[0]));
    VrpPickupDeliveryLifo.DataModel data = new VrpPickupDeliveryLifo.DataModel();
    assertPickupDelivery(out, data.distanceMatrix, data.pickupsDeliveries, 1);
  }

  /**
   * Checks time windows: every printed window lies in the node window and the windows are
   * consistent with the travel times and the waiting limit of 30.
   */
  private static long assertTimeWindows(List<Route> rs, long[][] time, long[][] windows) {
    long transits = 0;
    for (Route r : rs) {
      for (int k = 0; k < r.nodes.size(); k++) {
        long[] t = r.times.get(k);
        int node = r.nodes.get(k);
        assertTrue(t[0] <= t[1], "window " + Arrays.toString(t) + " in " + r);
        assertTrue(t[0] >= windows[node][0] && t[1] <= windows[node][1] || k == r.nodes.size() - 1,
            "time of node " + node + " outside of its window in " + r);
        assertTrue(t[1] <= 30, "capacity of the time dimension in " + r);
        if (k + 1 < r.nodes.size()) {
          long travel = time[node][r.nodes.get(k + 1)];
          long[] u = r.times.get(k + 1);
          transits += travel;
          // the earliest arrival is at least the earliest departure plus the travel time
          assertTrue(u[0] >= t[0] + travel, "earliest times of " + r);
          // waiting is at most 30
          assertTrue(u[0] <= t[1] + travel + 30, "waiting limit in " + r);
        }
      }
    }
    return transits;
  }

  @Test
  public void testVrpTimeWindows() {
    String out = OutputCapture.run(() -> VrpTimeWindows.main(new String[0]));
    VrpTimeWindows.DataModel data = new VrpTimeWindows.DataModel();
    List<Route> rs = routes(out);
    assertCovers(rs, 17, DEPOT, NONE);
    long transits = assertTimeWindows(rs, data.timeMatrix, data.timeWindows);
    long objective = (long) numberAfter(out, "Objective :");
    assertEquals(transits, objective);
    long totalTime = 0;
    for (Route r : rs) {
      // start and end times are fixed by the finalizers
      long[] first = r.times.get(0);
      long[] last = r.times.get(r.times.size() - 1);
      assertEquals(first[0], first[1]);
      assertEquals(last[0], last[1]);
      assertEquals(last[0], r.reported);
      totalTime += last[0];
    }
    assertEquals(totalTime, (long) numberAfter(out, "Total time of all routes:"));
    // OR-Tools reaches an objective of 71
    assertTrue(objective <= 71, "objective " + objective);
  }

  @Test
  public void testVrpResources() {
    String out = OutputCapture.run(() -> VrpResources.main(new String[0]));
    VrpResources.DataModel data = new VrpResources.DataModel();
    List<Route> rs = routes(out);
    assertCovers(rs, 17, DEPOT, NONE);
    long transits = assertTimeWindows(rs, data.timeMatrix, data.timeWindows);
    assertEquals(transits, (long) numberAfter(out, "Objective :"));
    // at most two vehicles load (5 min after the start) or unload (5 min after the end) at once
    List<long[]> intervals = new ArrayList<>();
    for (Route r : rs) {
      long[] first = r.times.get(0);
      long[] last = r.times.get(r.times.size() - 1);
      assertEquals(first[0], first[1]);
      assertEquals(last[0], last[1]);
      intervals.add(new long[] {first[0], first[0] + data.vehicleLoadTime});
      intervals.add(new long[] {last[0], last[0] + data.vehicleUnloadTime});
    }
    // vehicles which are not printed are unused: they also load and unload at the depot
    for (long t = 0; t <= 60; t++) {
      int usage = 0;
      for (long[] iv : intervals) {
        if (iv[0] <= t && t < iv[1]) {
          usage++;
        }
      }
      assertTrue(usage <= data.depotCapacity, "depot overloaded at " + t + " in " + rs);
    }
  }

  @Test
  public void testVrpBreaks() {
    String out = OutputCapture.run(() -> VrpBreaks.main(new String[0]));
    VrpBreaks.DataModel data = new VrpBreaks.DataModel();
    List<Route> rs = routes(out);
    assertCovers(rs, 17, DEPOT, NONE);
    long arcCosts = 0;
    long maxEnd = 0;
    for (Route r : rs) {
      for (int k = 0; k + 1 < r.nodes.size(); k++) {
        int a = r.nodes.get(k);
        int b = r.nodes.get(k + 1);
        long transit = data.timeMatrix[a][b] + data.serviceTime[a];
        arcCosts += transit;
        long slack = r.times.get(k + 1)[0] - r.times.get(k)[0] - transit;
        assertTrue(slack >= 0 && slack <= 10, "slack " + slack + " in " + r);
      }
      long end = r.times.get(r.times.size() - 1)[0];
      assertTrue(end <= 180);
      assertEquals(end, (long) r.reported);
      maxEnd = Math.max(maxEnd, end);
    }
    // the objective: arc costs (travel + service) and the global span cost 10 * (max end - 0)
    assertEquals(arcCosts + 10 * maxEnd, (long) numberAfter(out, "Objective:"));
    // every break is performed, starts in [50, 60] and lies before the start, after the end or
    // in a travel which has a slack of at least its duration, not overlapping a visit
    java.util.regex.Matcher m = java.util.regex.Pattern
        .compile("Break for vehicle (\\d+) \\(start = (\\d+), duration = 10, status = 1\\)")
        .matcher(out);
    int breaks = 0;
    while (m.find()) {
      breaks++;
      int vehicle = Integer.parseInt(m.group(1));
      long start = Long.parseLong(m.group(2));
      assertTrue(start >= 50 && start <= 60);
      Route route = null;
      for (Route r : rs) {
        if (r.vehicle == vehicle) {
          route = r;
        }
      }
      if (route == null) {
        continue; // unused vehicle: the break lies after its (empty) route
      }
      long routeEnd = route.times.get(route.times.size() - 1)[0];
      boolean placed = start >= routeEnd;
      for (int k = 0; k + 1 < route.nodes.size() && !placed; k++) {
        int a = route.nodes.get(k);
        int b = route.nodes.get(k + 1);
        long ta = route.times.get(k)[0];
        long tb = route.times.get(k + 1)[0];
        long transit = data.timeMatrix[a][b] + data.serviceTime[a];
        placed = start >= ta + data.serviceTime[a] && start + 10 <= tb
            && tb - ta - transit >= 10;
      }
      assertTrue(placed, "break of vehicle " + vehicle + " at " + start + " in " + route);
    }
    assertEquals(4, breaks);
  }

  @Tag("slow")
  @Test
  public void testVrpSolutionCallback() {
    String out = OutputCapture.run(() -> VrpSolutionCallback.main(new String[0]));
    long[][] matrix = new VrpSolutionCallback.DataModel().distanceMatrix;
    List<Double> objectives = numbersAfter(out, "Solution objective :");
    assertTrue(!objectives.isEmpty());
    for (int k = 1; k < objectives.size(); k++) {
      assertTrue(objectives.get(k) < objectives.get(k - 1), "objectives must decrease");
    }
    // every printed solution is consistent with its objective
    String[] blocks = out.split("################");
    for (int b = 1; b < blocks.length; b++) {
      List<Route> rs = routes(blocks[b]);
      assertCovers(rs, 17, DEPOT, NONE);
      long total = 0;
      long max = 0;
      for (Route r : rs) {
        long d = length(r, matrix);
        assertEquals(d, r.reported);
        total += d;
        max = Math.max(max, d);
      }
      assertEquals(total + 100 * max, (long) numberAfter(blocks[b], "Solution objective :"));
    }
    long best = (long) numberAfter(out, "Best objective:");
    if (objectives.size() >= 15) {
      // the callback stops the search after 15 improving solutions
      assertEquals(15, objectives.size());
      assertEquals((long) (double) objectives.get(14), best);
    } else {
      assertEquals(0, best);
    }
  }

  @Tag("slow")
  @Test
  public void testVrpWithTimeLimit() {
    String out = OutputCapture.run(() -> VrpWithTimeLimit.main(new String[0]));
    assertWithTimeLimitOutput(out, true);
  }

  private static void assertWithTimeLimitOutput(String out, boolean optimal) {
    List<Route> rs = routes(out);
    assertCovers(rs, 20, DEPOT, NONE);
    long arcs = 0;
    long max = 0;
    for (Route r : rs) {
      long d = r.nodes.size() - 1;
      assertEquals(d, r.reported);
      arcs += d;
      max = Math.max(max, d);
    }
    assertEquals(max, (long) numberAfter(out, "Maximum of the route distances:"));
    long objective = (long) numberAfter(out, "Objective :");
    assertEquals(arcs + 100 * max, objective);
    if (optimal) {
      // 19 visits on 5 vehicles: 24 arcs and at most 5 arcs per route
      assertEquals(524, objective);
    }
  }

  /** The VrpWithTimeLimit model with a short time limit. */
  @Test
  public void testVrpWithShortTimeLimit() {
    IndexManager manager = new IndexManager(20, 5, 0);
    Model routing = new Model(manager);
    int transit = routing.registerTransitCallback((long i, long j) -> 1);
    routing.setArcCostEvaluatorOfAllVehicles(transit);
    assertTrue(routing.addDimension(transit, 0, 3000, true, "Distance"));
    routing.getMutableDimension("Distance").setGlobalSpanCostCoefficient(100);
    RoutingSearchParameters parameters = Globals.defaultRoutingSearchParameters().toBuilder()
        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
        .setTimeLimit(Duration.newBuilder().setNanos(200_000_000).build()).build();
    Assignment solution = routing.solveWithParameters(parameters);
    assertNotNull(solution);
    assertEquals(RoutingSearchStatus.Value.ROUTING_SUCCESS, routing.status());
    StringBuilder out = new StringBuilder("Objective : " + solution.objectiveValue() + "\n");
    long max = 0;
    for (int v = 0; v < 5; v++) {
      if (!routing.isVehicleUsed(solution, v)) {
        continue;
      }
      out.append("Route for Vehicle ").append(v).append(":\n");
      long index = routing.start(v);
      long distance = 0;
      StringBuilder route = new StringBuilder();
      while (!routing.isEnd(index)) {
        route.append(manager.indexToNode(index)).append(" -> ");
        long next = solution.value(routing.nextVar(index));
        distance += routing.getArcCostForVehicle(index, next, v);
        index = next;
      }
      out.append(route).append(manager.indexToNode(index)).append('\n');
      out.append("Distance of the route: ").append(distance).append("m\n");
      max = Math.max(max, distance);
    }
    out.append("Maximum of the route distances: ").append(max).append("m\n");
    assertWithTimeLimitOutput(out.toString(), false);
  }

  @Test
  public void testCapacitatedVehicleRoutingProblemWithTimeWindows() {
    String out = OutputCapture.run(
        () -> CapacitatedVehicleRoutingProblemWithTimeWindows.main(new String[0]));
    assertContains(out, "Status: ROUTING_SUCCESS");
    CapacitatedVehicleRoutingProblemWithTimeWindows.DataModel data =
        new CapacitatedVehicleRoutingProblemWithTimeWindows.DataModel();
    List<Integer> dropped = RoutingOutput.listAfter(out, "Dropped orders:");
    List<Route> routes = RoutingOutput.routes(out);
    List<Integer> depots = new ArrayList<>();
    for (int n = data.numberOfOrders; n < data.locations.size(); n++) {
      depots.add(n);
    }
    RoutingOutput.assertCovers(routes, data.locations.size(), depots, dropped);
    long cost = 0;
    for (int order : dropped) {
      cost += data.orderPenalties.get(order);
    }
    for (Route r : routes) {
      int v = r.vehicle;
      assertEquals(data.vehicleStarts[v], r.start());
      assertEquals(data.vehicleEnds[v], r.end());
      long load = 0;
      for (int k = 0; k < r.nodes.size(); k++) {
        int node = r.nodes.get(k);
        // the load on arrival is the demand of the earlier orders
        assertEquals(load, (long) r.loads.get(k), "load in " + r);
        assertTrue(load <= data.vehicleCapacity);
        long[] t = r.times.get(k);
        assertTrue(t[0] <= t[1], "time window in " + r);
        if (node < data.numberOfOrders) {
          load += data.orderDemands.get(node);
          assertTrue(t[0] >= data.orderTimeWindows.get(node).first
              && t[1] <= data.orderTimeWindows.get(node).second, "order window in " + r);
        }
        if (k + 1 < r.nodes.size()) {
          int next = r.nodes.get(k + 1);
          long manhattan = manhattan(data, node, next);
          assertTrue(r.times.get(k + 1)[0] >= t[0] + manhattan, "travel time in " + r);
          cost += manhattan * data.vehicleCostCoefficients.get(v);
        }
      }
      assertTrue(r.times.get(r.times.size() - 1)[1] <= data.vehicleEndTime.get(v));
    }
    assertEquals(cost, (long) numberAfter(out, "Objective :"));
  }

  private static long manhattan(CapacitatedVehicleRoutingProblemWithTimeWindows.DataModel data,
      int a, int b) {
    return Math.abs(data.locations.get(a).first - data.locations.get(b).first)
        + Math.abs(data.locations.get(a).second - data.locations.get(b).second);
  }
}
