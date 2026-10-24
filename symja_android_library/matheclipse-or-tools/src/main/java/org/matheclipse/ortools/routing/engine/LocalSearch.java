package org.matheclipse.ortools.routing.engine;

import java.util.ArrayList;
import java.util.Arrays;
import org.matheclipse.ortools.constraintsolver.cp.CapOps;

/**
 * The neighborhoods of the simplified routing engine of this module, which reimplements the
 * local search of the OR-Tools routing library with a first improvement descent over these
 * operators: relocate and Or-opt (chains of up to three nodes, also reversed), exchange, 2-opt,
 * cross (exchange of route tails), make active, make inactive, swap active, and for pickup and
 * delivery pairs pair relocate, pair activation and pair deactivation.
 *
 * <p>
 * Candidates are restricted to the closest nodes of each node. When all costs are arc, fixed and
 * penalty costs, the cost change of a candidate is computed in constant time from prefix sums
 * and only improving candidates are built and checked.
 */
final class LocalSearch {

  /** Signals that the time limit or a stop request ended the search. */
  static final class Stop extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Stop() {
      super(null, null, false, false);
    }
  }

  static final Stop STOP = new Stop();

  private final SearchState s;

  private final RoutingProblem p;

  private final RoutingEngine engine;

  /** The closest visit nodes of every index which has a successor. */
  private final int[][] neighbors;

  private final int[] visitNodes;

  private long evaluations;

  LocalSearch(SearchState s, RoutingEngine engine) {
    this.s = s;
    this.p = s.p;
    this.engine = engine;
    ArrayList<Integer> visits = new ArrayList<>();
    for (int i = 0; i < p.size; i++) {
      if (p.isVisit(i)) {
        visits.add(i);
      }
    }
    visitNodes = new int[visits.size()];
    for (int k = 0; k < visitNodes.length; k++) {
      visitNodes[k] = visits.get(k);
    }
    neighbors = buildNeighbors();
  }

  private int[][] buildNeighbors() {
    final int k = visitNodes.length <= 60 ? visitNodes.length : 40;
    int[][] result = new int[p.size][];
    ArcMatrix cost = p.arcCost[0];
    boolean zero = true;
    for (int v = 0; v < p.vehicles && zero; v++) {
      if (!p.arcCost[v].isZero()) {
        cost = p.arcCost[v];
        zero = false;
      }
    }
    if (zero && p.dims.length > 0) {
      cost = p.dims[0].transitOfVehicle[0];
    }
    final ArcMatrix c = cost;
    final int m = visitNodes.length;
    long[] packed = new long[m];
    for (int i = 0; i < p.size; i++) {
      final int from = i;
      int[] order = null;
      // sort by (cost, index) packed into one long when the costs are small enough
      boolean small = true;
      for (int j = 0; j < m && small; j++) {
        int a = visitNodes[j];
        long ca = CapOps.capAdd(c.get(from, a), c.get(a, from));
        if (ca < 0 || ca >= (1L << 40)) {
          small = false;
        } else {
          packed[j] = (ca << 22) | j;
        }
      }
      if (small && m < (1 << 22)) {
        Arrays.sort(packed, 0, m);
        order = new int[m];
        for (int j = 0; j < m; j++) {
          order[j] = visitNodes[(int) (packed[j] & ((1 << 22) - 1))];
        }
      } else {
        Integer[] boxed = new Integer[m];
        for (int j = 0; j < m; j++) {
          boxed[j] = visitNodes[j];
        }
        Arrays.sort(boxed, (a, b) -> {
          long ca = CapOps.capAdd(c.get(from, a), c.get(a, from));
          long cb = CapOps.capAdd(c.get(from, b), c.get(b, from));
          return ca != cb ? Long.compare(ca, cb) : Integer.compare(a, b);
        });
        order = new int[m];
        for (int j = 0; j < m; j++) {
          order[j] = boxed[j];
        }
      }
      int[] list = new int[Math.min(k + 1, m)];
      int n = 0;
      for (int j = 0; j < m && n < list.length; j++) {
        if (order[j] != i) {
          list[n++] = order[j];
        }
      }
      result[i] = Arrays.copyOf(list, n);
    }
    return result;
  }

  private void tick() {
    if ((++evaluations & 63) == 0 && engine.shouldStop()) {
      throw STOP;
    }
  }

  /** Runs the descent until no operator improves; returns whether something improved. */
  boolean descend() {
    boolean improvedAny = false;
    int failures = 0;
    int op = 0;
    final int opCount = 9;
    while (failures < opCount) {
      boolean improved = runOperator(op);
      if (improved) {
        improvedAny = true;
        failures = 0;
        engine.onImprovement();
      } else {
        failures++;
        op = (op + 1) % opCount;
      }
    }
    return improvedAny;
  }

  private boolean runOperator(int op) {
    switch (op) {
      case 0:
        return relocate(1);
      case 1:
        return twoOpt();
      case 2:
        return relocate(3);
      case 3:
        return exchange();
      case 4:
        return cross();
      case 5:
        return makeActive();
      case 6:
        return swapActive();
      case 7:
        return makeInactive();
      default:
        return pairMoves();
    }
  }

  private boolean worth(long estimate) {
    return !s.arcOnly || estimate < 0;
  }

  /** The search cost of the path <code>src[from..to]</code> driven by vehicle v. */
  private long pathCost(int v, int srcVehicle, int[] src, int from, int to, boolean reversed) {
    if (from >= to) {
      return 0;
    }
    if (p.costClass[v] == p.costClass[srcVehicle]) {
      // same arc costs (the guided search penalties don't depend on the vehicle)
      long[] f = s.forward[srcVehicle];
      long[] b = s.backward[srcVehicle];
      return reversed ? b[to] - b[from] : f[to] - f[from];
    }
    long c = 0;
    for (int k = from; k < to; k++) {
      c = CapOps.capAdd(c, reversed ? s.sa(v, src[k + 1], src[k]) : s.sa(v, src[k], src[k + 1]));
    }
    return c;
  }

  // ----- relocate and Or-opt -----

  private boolean relocate(int maxChain) {
    for (int node : visitNodes) {
      if (s.status[node] != SearchState.ACTIVE) {
        continue;
      }
      int v1 = s.veh[node];
      int p1 = s.pos[node];
      int[] s1 = s.seq[v1];
      for (int len = maxChain == 1 ? 1 : 2; len <= maxChain; len++) {
        if (p1 + len > s1.length - 1) {
          break;
        }
        if (relocateChain(v1, p1, len)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean relocateChain(int v1, int p1, int len) {
    int[] s1 = s.seq[v1];
    int first = s1[p1];
    int last = s1[p1 + len - 1];
    int prev = s1[p1 - 1];
    int next = s1[p1 + len];
    long chainF = pathCost(v1, v1, s1, p1, p1 + len - 1, false);
    long removeDelta = s.sa(v1, prev, next) - s.sa(v1, prev, first) - s.sa(v1, last, next)
        - chainF;
    boolean empty1 = s1.length - len == 2;
    int[] targets = neighbors[first];
    for (int t = 0; t <= targets.length + p.vehicles; t++) {
      int j;
      if (t < targets.length) {
        j = targets[t];
        if (s.status[j] != SearchState.ACTIVE) {
          continue;
        }
      } else if (t < targets.length + p.vehicles) {
        j = p.start[t - targets.length];
      } else {
        break;
      }
      int v2 = s.veh[j];
      int q = s.pos[j];
      if (v2 == v1 && q >= p1 - 1 && q <= p1 + len - 1) {
        continue;
      }
      int[] s2 = s.seq[v2];
      int jn = s2[q + 1];
      for (int dir = 0; dir < (len > 1 ? 2 : 1); dir++) {
        tick();
        boolean rev = dir == 1;
        long chain2 = v2 == v1 ? (rev ? pathCost(v1, v1, s1, p1, p1 + len - 1, true) : chainF)
            : pathCost(v2, v1, s1, p1, p1 + len - 1, rev);
        int a = rev ? last : first;
        int b = rev ? first : last;
        long insertDelta = s.sa(v2, j, a) + chain2 + s.sa(v2, b, jn) - s.sa(v2, j, jn);
        long estimate = removeDelta + insertDelta;
        if (v2 != v1) {
          estimate += s.emptyAdjust(v1, false, empty1) + s.emptyAdjust(v2, s2.length == 2, false);
        }
        if (!worth(estimate)) {
          continue;
        }
        int[] chain = Arrays.copyOfRange(s1, p1, p1 + len);
        if (rev) {
          chain = SearchState.reverse(chain, 0, len - 1);
        }
        if (v2 == v1) {
          int[] base = SearchState.remove(s1, p1, len);
          int qq = q < p1 ? q : q - len;
          int[] ns = SearchState.insert(base, qq, chain);
          if (s.tryMove(v1, ns, -1, null, null, null, true)) {
            return true;
          }
        } else {
          int[] ns1 = SearchState.remove(s1, p1, len);
          int[] ns2 = SearchState.insert(s2, q, chain);
          if (s.tryMove(v1, ns1, v2, ns2, null, null, true)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  // ----- exchange -----

  private boolean exchange() {
    for (int i : visitNodes) {
      if (s.status[i] != SearchState.ACTIVE) {
        continue;
      }
      for (int j : neighbors[i]) {
        if (s.status[j] != SearchState.ACTIVE || j < i && isNeighbor(j, i)) {
          continue;
        }
        tick();
        if (tryExchange(i, j)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isNeighbor(int a, int b) {
    for (int x : neighbors[a]) {
      if (x == b) {
        return true;
      }
    }
    return false;
  }

  private boolean tryExchange(int i, int j) {
    int v1 = s.veh[i];
    int v2 = s.veh[j];
    int pi = s.pos[i];
    int pj = s.pos[j];
    int[] s1 = s.seq[v1];
    int[] s2 = s.seq[v2];
    long estimate;
    if (v1 == v2 && Math.abs(pi - pj) == 1) {
      int a = Math.min(pi, pj);
      int x = s1[a];
      int y = s1[a + 1];
      int before = s1[a - 1];
      int after = s1[a + 2];
      estimate = s.sa(v1, before, y) + s.sa(v1, y, x) + s.sa(v1, x, after) - s.sa(v1, before, x)
          - s.sa(v1, x, y) - s.sa(v1, y, after);
    } else {
      estimate = s.sa(v1, s1[pi - 1], j) + s.sa(v1, j, s1[pi + 1]) - s.sa(v1, s1[pi - 1], i)
          - s.sa(v1, i, s1[pi + 1]) + s.sa(v2, s2[pj - 1], i) + s.sa(v2, i, s2[pj + 1])
          - s.sa(v2, s2[pj - 1], j) - s.sa(v2, j, s2[pj + 1]);
    }
    if (!worth(estimate)) {
      return false;
    }
    if (v1 == v2) {
      int[] ns = s1.clone();
      ns[pi] = j;
      ns[pj] = i;
      return s.tryMove(v1, ns, -1, null, null, null, true);
    }
    int[] ns1 = s1.clone();
    int[] ns2 = s2.clone();
    ns1[pi] = j;
    ns2[pj] = i;
    return s.tryMove(v1, ns1, v2, ns2, null, null, true);
  }

  // ----- 2-opt -----

  private boolean twoOpt() {
    for (int v = 0; v < p.vehicles; v++) {
      int[] sq = s.seq[v];
      if (sq.length < 5) {
        continue;
      }
      for (int x = 0; x + 2 < sq.length - 1; x++) {
        int i = sq[x];
        for (int j : neighbors[i]) {
          if (s.status[j] != SearchState.ACTIVE || s.veh[j] != v) {
            continue;
          }
          int y = s.pos[j];
          // new arc (i, j): reverse x+1 .. y
          if (y > x + 1) {
            tick();
            if (tryTwoOpt(v, x, y)) {
              return true;
            }
          }
          // new arc (succ(i), succ(j)) with j before i: reverse y+1 .. x
          if (y + 1 < x) {
            tick();
            if (tryTwoOpt(v, y, x)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /** Reverses positions x+1 .. y of the route of v. */
  private boolean tryTwoOpt(int v, int x, int y) {
    int[] sq = s.seq[v];
    long[] f = s.forward[v];
    long[] b = s.backward[v];
    long estimate = s.sa(v, sq[x], sq[y]) + s.sa(v, sq[x + 1], sq[y + 1])
        - s.sa(v, sq[x], sq[x + 1]) - s.sa(v, sq[y], sq[y + 1]) + (b[y] - b[x + 1])
        - (f[y] - f[x + 1]);
    if (!worth(estimate)) {
      return false;
    }
    return s.tryMove(v, SearchState.reverse(sq, x + 1, y), -1, null, null, null, true);
  }

  // ----- cross: exchange of route tails -----

  private boolean cross() {
    if (p.vehicles < 2) {
      return false;
    }
    for (int v1 = 0; v1 < p.vehicles; v1++) {
      int[] s1 = s.seq[v1];
      for (int x = 0; x + 1 < s1.length; x++) {
        int i = s1[x];
        for (int j : neighbors[i]) {
          if (s.status[j] != SearchState.ACTIVE) {
            continue;
          }
          int v2 = s.veh[j];
          if (v2 == v1) {
            continue;
          }
          tick();
          if (tryCross(v1, x, v2, s.pos[j] - 1)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Route v1 keeps positions 0..x, route v2 keeps 0..y; the tails are exchanged, each
   * optionally reversed.
   */
  private boolean tryCross(int v1, int x, int v2, int y) {
    int[] s1 = s.seq[v1];
    int[] s2 = s.seq[v2];
    int n1 = s1.length;
    int n2 = s2.length;
    int tail1 = n1 - 2 - x; // nodes after x (without end)
    int tail2 = n2 - 2 - y;
    if (tail1 == 0 && tail2 == 0) {
      return false;
    }
    int e1 = s1[n1 - 1];
    int e2 = s2[n2 - 1];
    // old cost of the tails including the arcs into them and to the ends
    long old = (s.forward[v1][n1 - 1] - s.forward[v1][x]) + (s.forward[v2][n2 - 1]
        - s.forward[v2][y]);
    long adjust = s.emptyAdjust(v1, n1 == 2, x == 0 && tail2 == 0)
        + s.emptyAdjust(v2, n2 == 2, y == 0 && tail1 == 0);
    for (int variant = 0; variant < 4; variant++) {
      boolean rev1 = (variant & 1) != 0;
      boolean rev2 = (variant & 2) != 0;
      if ((rev1 && tail1 < 2) || (rev2 && tail2 < 2)) {
        continue;
      }
      long nw = 0;
      // new route 1: s1[0..x], tail of route 2, e1
      int prev = s1[x];
      if (tail2 > 0) {
        int first = rev2 ? s2[n2 - 2] : s2[y + 1];
        int last = rev2 ? s2[y + 1] : s2[n2 - 2];
        nw += s.sa(v1, prev, first) + pathCost(v1, v2, s2, y + 1, n2 - 2, rev2);
        prev = last;
      }
      nw += s.sa(v1, prev, e1);
      prev = s2[y];
      if (tail1 > 0) {
        int first = rev1 ? s1[n1 - 2] : s1[x + 1];
        int last = rev1 ? s1[x + 1] : s1[n1 - 2];
        nw += s.sa(v2, prev, first) + pathCost(v2, v1, s1, x + 1, n1 - 2, rev1);
        prev = last;
      }
      nw += s.sa(v2, prev, e2);
      long estimate = nw - old + adjust;
      if (!worth(estimate)) {
        continue;
      }
      int[] ns1 = new int[x + 1 + tail2 + 1];
      System.arraycopy(s1, 0, ns1, 0, x + 1);
      System.arraycopy(s2, y + 1, ns1, x + 1, tail2);
      ns1[ns1.length - 1] = e1;
      if (rev2) {
        ns1 = SearchState.reverse(ns1, x + 1, x + tail2);
      }
      int[] ns2 = new int[y + 1 + tail1 + 1];
      System.arraycopy(s2, 0, ns2, 0, y + 1);
      System.arraycopy(s1, x + 1, ns2, y + 1, tail1);
      ns2[ns2.length - 1] = e2;
      if (rev1) {
        ns2 = SearchState.reverse(ns2, y + 1, y + tail1);
      }
      if (s.tryMove(v1, ns1, v2, ns2, null, null, true)) {
        return true;
      }
    }
    return false;
  }

  // ----- activity of optional nodes -----

  private boolean makeActive() {
    for (int u : visitNodes) {
      if (s.status[u] != SearchState.INACTIVE || !s.canActivate(u)
          || (s.hasPairs && p.pairOfNode[u] >= 0)) {
        continue;
      }
      long gain = s.penaltyDelta(new int[] {u}, null);
      int[] targets = neighbors[u];
      for (int t = 0; t < targets.length + p.vehicles; t++) {
        int j = t < targets.length ? targets[t] : p.start[t - targets.length];
        if (s.status[j] != SearchState.ACTIVE) {
          continue;
        }
        tick();
        int v = s.veh[j];
        int q = s.pos[j];
        int[] sq = s.seq[v];
        int jn = sq[q + 1];
        long estimate = s.sa(v, j, u) + s.sa(v, u, jn) - s.sa(v, j, jn)
            + s.emptyAdjust(v, sq.length == 2, false) + gain;
        if (!worth(estimate)) {
          continue;
        }
        if (s.tryMove(v, SearchState.insert(sq, q, u), -1, null, new int[] {u}, null, true)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean makeInactive() {
    for (int u : visitNodes) {
      if (s.status[u] != SearchState.ACTIVE || !s.isOptional(u)
          || (s.hasPairs && p.pairOfNode[u] >= 0)) {
        continue;
      }
      tick();
      int v = s.veh[u];
      int q = s.pos[u];
      int[] sq = s.seq[v];
      long estimate = s.sa(v, sq[q - 1], sq[q + 1]) - s.sa(v, sq[q - 1], u) - s.sa(v, u, sq[q + 1])
          + s.emptyAdjust(v, false, sq.length == 3) + s.penaltyDelta(null, new int[] {u});
      if (!worth(estimate)) {
        continue;
      }
      if (s.tryMove(v, SearchState.remove(sq, q, 1), -1, null, null, new int[] {u}, true)) {
        return true;
      }
    }
    return false;
  }

  private boolean swapActive() {
    for (int u : visitNodes) {
      if (s.status[u] != SearchState.ACTIVE || p.disjunctionsOfNode[u].length == 0
          || (s.hasPairs && p.pairOfNode[u] >= 0)) {
        continue;
      }
      int v = s.veh[u];
      int q = s.pos[u];
      int[] sq = s.seq[v];
      // candidates: the inactive nodes of the same disjunctions and the inactive neighbors
      for (int pass = 0; pass < 2; pass++) {
        int[] cands = pass == 0 ? sameDisjunction(u) : neighbors[u];
        for (int w : cands) {
          if (w == u || s.status[w] != SearchState.INACTIVE || p.forcedInactive[w]
              || (s.hasPairs && p.pairOfNode[w] >= 0)) {
            continue;
          }
          tick();
          long estimate = s.sa(v, sq[q - 1], w) + s.sa(v, w, sq[q + 1]) - s.sa(v, sq[q - 1], u)
              - s.sa(v, u, sq[q + 1]) + s.penaltyDelta(new int[] {w}, new int[] {u});
          if (!worth(estimate)) {
            continue;
          }
          int[] ns = sq.clone();
          ns[q] = w;
          if (s.tryMove(v, ns, -1, null, new int[] {w}, new int[] {u}, true)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private int[] sameDisjunction(int u) {
    int[] ds = p.disjunctionsOfNode[u];
    if (ds.length == 1) {
      return p.disjunctionNodes[ds[0]];
    }
    int n = 0;
    for (int d : ds) {
      n += p.disjunctionNodes[d].length;
    }
    int[] r = new int[n];
    int k = 0;
    for (int d : ds) {
      for (int x : p.disjunctionNodes[d]) {
        r[k++] = x;
      }
    }
    return r;
  }

  // ----- pickup and delivery pairs -----

  private boolean pairMoves() {
    if (!s.hasPairs) {
      return false;
    }
    for (int q = 0; q < p.pairPickup.length; q++) {
      int pk = p.pairPickup[q];
      int dl = p.pairDelivery[q];
      if (s.status[pk] == SearchState.ACTIVE && s.status[dl] == SearchState.ACTIVE) {
        if (pairRelocate(pk, dl)) {
          return true;
        }
        if (s.isOptional(pk) && s.isOptional(dl) && pairDeactivate(pk, dl)) {
          return true;
        }
      } else if (s.status[pk] == SearchState.INACTIVE && s.status[dl] == SearchState.INACTIVE
          && s.canActivate(pk) && s.canActivate(dl)) {
        if (pairInsert(pk, dl, -1, null, true)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean pairRelocate(int pk, int dl) {
    int v1 = s.veh[pk];
    int[] s1 = s.seq[v1];
    int a = s.pos[pk];
    int b = s.pos[dl];
    int[] base = SearchState.remove(s1, Math.max(a, b), 1);
    base = SearchState.remove(base, Math.min(a, b), 1);
    return pairInsert(pk, dl, v1, base, false);
  }

  /**
   * Inserts the pair into every route at every position (pickup before delivery); the route of
   * vehicle <code>fromVehicle</code> is replaced by <code>fromBase</code> (the route without the
   * pair) when the pair is relocated.
   */
  private boolean pairInsert(int pk, int dl, int fromVehicle, int[] fromBase, boolean activate) {
    int[] act = activate ? new int[] {pk, dl} : null;
    for (int v = 0; v < p.vehicles; v++) {
      if (!p.vehicleAllowed(pk, v) || !p.vehicleAllowed(dl, v)) {
        continue;
      }
      int[] base = v == fromVehicle ? fromBase : s.seq[v];
      for (int i = 0; i + 1 < base.length; i++) {
        int[] withPickup = SearchState.insert(base, i, pk);
        for (int j = i + 1; j + 1 < withPickup.length; j++) {
          tick();
          int[] ns = SearchState.insert(withPickup, j, dl);
          if (v == fromVehicle) {
            if (Arrays.equals(ns, s.seq[v])) {
              continue;
            }
            if (s.tryMove(v, ns, -1, null, null, null, true)) {
              return true;
            }
          } else if (fromVehicle >= 0) {
            if (s.tryMove(fromVehicle, fromBase, v, ns, null, null, true)) {
              return true;
            }
          } else {
            if (s.tryMove(v, ns, -1, null, act, null, true)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private boolean pairDeactivate(int pk, int dl) {
    int v = s.veh[pk];
    int[] sq = s.seq[v];
    int a = s.pos[pk];
    int b = s.pos[dl];
    int[] base = SearchState.remove(sq, Math.max(a, b), 1);
    base = SearchState.remove(base, Math.min(a, b), 1);
    tick();
    return s.tryMove(v, base, -1, null, null, new int[] {pk, dl}, true);
  }

  // ----- guided local search -----

  /**
   * Penalizes the arcs of the current solution with the largest utility
   * <code>cost / (1 + penalty)</code>, as the guided local search of OR-Tools does at each
   * local optimum.
   */
  void penalizeLocalOptimum() {
    double best = -1;
    ArrayList<long[]> bestArcs = new ArrayList<>();
    for (int v = 0; v < p.vehicles; v++) {
      int[] sq = s.seq[v];
      if (sq.length == 2) {
        continue;
      }
      for (int k = 0; k + 1 < sq.length; k++) {
        int i = sq[k];
        int j = sq[k + 1];
        long key = (long) i * p.numIndices + j;
        double utility = (double) s.ra(v, i, j) / (1 + s.glsPenalties.get(key));
        if (utility > best + 1e-12) {
          best = utility;
          bestArcs.clear();
          bestArcs.add(new long[] {key});
        } else if (Math.abs(utility - best) <= 1e-12) {
          bestArcs.add(new long[] {key});
        }
      }
    }
    for (long[] key : bestArcs) {
      s.glsPenalties.increment(key[0]);
    }
  }
}
