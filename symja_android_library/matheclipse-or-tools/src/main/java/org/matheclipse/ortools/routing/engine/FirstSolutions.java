package org.matheclipse.ortools.routing.engine;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.ortools.constraintsolver.cp.CapOps;

/**
 * The first solution heuristics of the simplified routing engine of this module, which
 * reimplements the corresponding OR-Tools strategies:
 *
 * <ul>
 * <li>path cheapest arc: extend each route from its last node by the cheapest feasible arc;
 * <li>savings: sequential Clarke and Wright savings per vehicle;
 * <li>cheapest insertion (parallel or local): insert nodes, pickup and delivery pairs together,
 * at the position of smallest cost increase;
 * <li>all unperformed: every optional node is left unperformed.
 * </ul>
 *
 * Nodes left over by a heuristic are inserted by the parallel cheapest insertion; optional nodes
 * which can't be inserted (or cost more than their penalty) stay unperformed.
 */
final class FirstSolutions {

  private final SearchState s;

  private final RoutingProblem p;

  private final RoutingEngine engine;

  FirstSolutions(SearchState s, RoutingEngine engine) {
    this.s = s;
    this.p = s.p;
    this.engine = engine;
  }

  /** The units to insert: single nodes and pickup and delivery pairs, in index order. */
  private List<int[]> undecidedUnits() {
    List<int[]> units = new ArrayList<>();
    boolean[] seen = new boolean[p.numIndices];
    for (int i = 0; i < p.size; i++) {
      if (!p.isVisit(i) || s.status[i] != SearchState.UNDECIDED || seen[i]) {
        continue;
      }
      int q = p.pairOfNode[i];
      if (q >= 0) {
        int pk = p.pairPickup[q];
        int dl = p.pairDelivery[q];
        if (s.status[pk] == SearchState.UNDECIDED && s.status[dl] == SearchState.UNDECIDED) {
          seen[pk] = true;
          seen[dl] = true;
          units.add(new int[] {pk, dl});
          continue;
        }
      }
      seen[i] = true;
      units.add(new int[] {i});
    }
    return units;
  }

  private boolean unitAllowed(int[] unit) {
    for (int x : unit) {
      if (s.status[x] != SearchState.UNDECIDED || !s.canActivate(x)) {
        return false;
      }
    }
    if (unit.length == 2) {
      // both nodes of the pair must fit into the cardinalities
      for (int d : p.disjunctionsOfNode[unit[0]]) {
        for (int d2 : p.disjunctionsOfNode[unit[1]]) {
          if (d == d2 && s.disjCount[d] + 2 > p.disjunctionMaxCardinality[d]) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /** The penalty saved by inserting the unit; MAX_VALUE if a node is mandatory. */
  private long unitPenalty(int[] unit) {
    long total = 0;
    for (int x : unit) {
      long pen = s.inactivePenalty(x);
      if (pen == Long.MAX_VALUE) {
        return Long.MAX_VALUE;
      }
      total = CapOps.capAdd(total, pen);
    }
    return total;
  }

  /** A candidate insertion of a unit. */
  private static final class Insertion {
    final int vehicle;

    final int[] route;

    final long cost;

    Insertion(int vehicle, int[] route, long cost) {
      this.vehicle = vehicle;
      this.route = route;
      this.cost = cost;
    }
  }

  /** All insertions of the unit with their estimated cost, cheapest first. */
  private List<Insertion> insertions(int[] unit) {
    List<Insertion> list = new ArrayList<>();
    for (int v = 0; v < p.vehicles; v++) {
      boolean allowed = true;
      for (int x : unit) {
        allowed &= p.vehicleAllowed(x, v);
      }
      if (!allowed) {
        continue;
      }
      int[] sq = s.seq[v];
      if (unit.length == 1) {
        int u = unit[0];
        for (int k = 0; k + 1 < sq.length; k++) {
          engine.checkStop();
          int[] ns = SearchState.insert(sq, k, u);
          long cost;
          if (s.arcOnly) {
            cost = s.sa(v, sq[k], u) + s.sa(v, u, sq[k + 1]) - s.sa(v, sq[k], sq[k + 1])
                + s.emptyAdjust(v, sq.length == 2, false);
          } else {
            cost = routeCostDelta(v, ns);
            if (cost == Long.MAX_VALUE) {
              continue;
            }
          }
          list.add(new Insertion(v, ns, cost));
        }
      } else {
        for (int a = 0; a + 1 < sq.length; a++) {
          int[] withPickup = SearchState.insert(sq, a, unit[0]);
          for (int b = a + 1; b + 1 < withPickup.length; b++) {
            engine.checkStop();
            int[] ns = SearchState.insert(withPickup, b, unit[1]);
            long cost = routeCostDelta(v, ns);
            if (cost == Long.MAX_VALUE) {
              continue;
            }
            list.add(new Insertion(v, ns, cost));
          }
        }
      }
    }
    list.sort((x, y) -> Long.compare(x.cost, y.cost));
    return list;
  }

  /** The exact search cost change of replacing the route of v (MAX_VALUE if infeasible). */
  private long routeCostDelta(int v, int[] ns) {
    SearchState.RouteEval e = new SearchState.RouteEval(p.dims.length);
    if (!s.evaluate(v, ns, e)) {
      return Long.MAX_VALUE;
    }
    long delta = CapOps.capSub(e.search, s.routes[v].search);
    long gs = s.globalSpan(v, e, -1, null);
    return CapOps.capAdd(delta, CapOps.capSub(gs, s.globalSpanCost));
  }

  /** The cheapest feasible insertion of the unit, or null. */
  private Insertion bestFeasible(int[] unit) {
    for (Insertion ins : insertions(unit)) {
      if (s.tryMove(ins.vehicle, ins.route, -1, null, unit, null, false, true)) {
        return ins;
      }
    }
    return null;
  }

  /** Parallel cheapest insertion of all undecided units; false if a mandatory unit fails. */
  boolean parallelCheapestInsertion() {
    List<int[]> units = undecidedUnits();
    while (!units.isEmpty()) {
      Insertion best = null;
      int bestUnit = -1;
      long bestScore = Long.MAX_VALUE;
      for (int u = 0; u < units.size(); u++) {
        int[] unit = units.get(u);
        if (!unitAllowed(unit)) {
          continue;
        }
        Insertion ins = bestFeasible(unit);
        if (ins == null) {
          continue;
        }
        long penalty = unitPenalty(unit);
        if (penalty != Long.MAX_VALUE && ins.cost >= penalty) {
          continue;
        }
        // mandatory units first, then by cost
        long score = penalty == Long.MAX_VALUE ? ins.cost : ins.cost;
        boolean mandatory = penalty == Long.MAX_VALUE;
        boolean bestMandatory = bestUnit >= 0 && unitPenalty(units.get(bestUnit)) == Long.MAX_VALUE;
        if (best == null || (mandatory && !bestMandatory)
            || (mandatory == bestMandatory && score < bestScore)) {
          best = ins;
          bestUnit = u;
          bestScore = score;
        }
      }
      if (best == null) {
        break;
      }
      int[] unit = units.remove(bestUnit);
      if (!s.tryMove(best.vehicle, best.route, -1, null, unit, null, false)) {
        // the dry run succeeded, so this should not happen
        break;
      }
    }
    return finish();
  }

  /** Local cheapest insertion: units in index order, each at its cheapest position. */
  boolean localCheapestInsertion() {
    for (int[] unit : undecidedUnits()) {
      if (!unitAllowed(unit)) {
        continue;
      }
      long penalty = unitPenalty(unit);
      for (Insertion ins : insertions(unit)) {
        if (penalty != Long.MAX_VALUE && ins.cost >= penalty) {
          break;
        }
        if (s.tryMove(ins.vehicle, ins.route, -1, null, unit, null, false)) {
          break;
        }
      }
    }
    return parallelCheapestInsertion();
  }

  /** Extends every route by the cheapest feasible arc from its last node. */
  boolean pathCheapestArc() {
    for (int v = 0; v < p.vehicles; v++) {
      while (true) {
        engine.checkStop();
        int[] sq = s.seq[v];
        int last = sq[sq.length - 2];
        final int vv = v;
        List<long[]> cands = new ArrayList<>();
        for (int j = 0; j < p.size; j++) {
          if (!p.isVisit(j) || s.status[j] != SearchState.UNDECIDED || !s.canActivate(j)
              || !p.vehicleAllowed(j, v) || !p.nextAllowed(last, j)) {
            continue;
          }
          int q = p.pairOfNode[j];
          if (q >= 0 && p.pairDelivery[q] == j) {
            int pk = p.pairPickup[q];
            if (s.status[pk] != SearchState.ACTIVE || s.veh[pk] != v) {
              continue;
            }
          }
          cands.add(new long[] {s.sa(vv, last, j), j});
        }
        if (cands.isEmpty()) {
          break;
        }
        cands.sort((a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
        boolean extended = false;
        for (long[] c : cands) {
          int j = (int) c[1];
          int[] ns = SearchState.insert(sq, sq.length - 2, j);
          if (s.tryMove(v, ns, -1, null, new int[] {j}, null, false)) {
            extended = true;
            break;
          }
        }
        if (!extended) {
          break;
        }
      }
    }
    return parallelCheapestInsertion();
  }

  /** Sequential savings: each vehicle grows one route by the largest feasible savings. */
  boolean savings() {
    List<Integer> nodes = new ArrayList<>();
    for (int i = 0; i < p.size; i++) {
      if (p.isVisit(i) && s.status[i] == SearchState.UNDECIDED) {
        nodes.add(i);
      }
    }
    for (int v = 0; v < p.vehicles; v++) {
      final int start = p.start[v];
      final int end = p.end[v];
      final int vv = v;
      List<long[]> savings = new ArrayList<>();
      for (int i : nodes) {
        for (int j : nodes) {
          if (i != j && s.status[i] == SearchState.UNDECIDED
              && s.status[j] == SearchState.UNDECIDED) {
            long saving = s.sa(vv, i, end) + s.sa(vv, start, j) - s.sa(vv, i, j);
            savings.add(new long[] {saving, i, j});
          }
        }
      }
      savings.sort((a, b) -> a[0] != b[0] ? Long.compare(b[0], a[0])
          : (a[1] != b[1] ? Long.compare(a[1], b[1]) : Long.compare(a[2], b[2])));
      boolean grew = true;
      while (grew) {
        grew = false;
        for (long[] sv : savings) {
          engine.checkStop();
          int i = (int) sv[1];
          int j = (int) sv[2];
          int[] sq = s.seq[v];
          if (sq.length == 2) {
            if (s.status[i] == SearchState.UNDECIDED && s.status[j] == SearchState.UNDECIDED
                && s.canActivate(i) && s.canActivate(j)) {
              int[] ns = new int[] {start, i, j, end};
              if (s.tryMove(v, ns, -1, null, new int[] {i, j}, null, false)) {
                grew = true;
              }
            }
          } else if (sq[sq.length - 2] == i && s.status[j] == SearchState.UNDECIDED
              && s.canActivate(j)) {
            if (s.tryMove(v, SearchState.insert(sq, sq.length - 2, j), -1, null, new int[] {j},
                null, false)) {
              grew = true;
            }
          } else if (sq[1] == j && s.status[i] == SearchState.UNDECIDED && s.canActivate(i)) {
            if (s.tryMove(v, SearchState.insert(sq, 0, i), -1, null, new int[] {i}, null,
                false)) {
              grew = true;
            }
          }
        }
      }
    }
    return parallelCheapestInsertion();
  }

  /** Leaves all optional nodes unperformed; fails if a node is mandatory. */
  boolean allUnperformed() {
    for (int i = 0; i < p.size; i++) {
      if (p.isVisit(i) && s.status[i] == SearchState.UNDECIDED) {
        if (!s.isOptional(i)) {
          return false;
        }
        s.status[i] = SearchState.INACTIVE;
      }
    }
    return finish();
  }

  /** Makes the remaining undecided nodes unperformed; false if one of them is mandatory. */
  private boolean finish() {
    for (int i = 0; i < p.size; i++) {
      if (p.isVisit(i) && s.status[i] == SearchState.UNDECIDED) {
        if (!s.mayStayInactive(i)) {
          return false;
        }
        s.status[i] = SearchState.INACTIVE;
      }
    }
    return true;
  }
}
