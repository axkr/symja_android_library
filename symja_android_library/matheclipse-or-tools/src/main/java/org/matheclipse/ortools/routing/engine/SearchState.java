package org.matheclipse.ortools.routing.engine;

import java.util.Arrays;
import org.matheclipse.ortools.constraintsolver.cp.CapOps;

/**
 * The current solution of the simplified routing engine of this module (which reimplements the
 * behaviour of the OR-Tools routing library): one index sequence per vehicle, the status of every
 * index, cached route costs, and the machinery to evaluate, check and commit moves.
 *
 * <p>
 * A move replaces the routes of at most two vehicles and may activate or deactivate nodes.
 * {@link #tryMove} evaluates the new routes (arc costs, fixed costs, dimension schedules and
 * costs, pickup and delivery order), the disjunction penalties and the global span costs, applies
 * the move tentatively, runs the global checks (pickup and delivery vehicles, cardinalities,
 * user constraints) and either commits or undoes it.
 */
final class SearchState {

  static final byte ACTIVE = SideConstraints.ACTIVE;

  static final byte INACTIVE = SideConstraints.INACTIVE;

  static final byte UNDECIDED = SideConstraints.UNDECIDED;

  /** The result of the evaluation of one route. */
  static final class RouteEval {
    boolean feasible;

    long search;

    long real;

    boolean used;

    /** Per dimension: the latest feasible start cumul. */
    final long[] spanStart;

    /** Per dimension: the earliest feasible end cumul. */
    final long[] spanEnd;

    /** Per dimension: the earliest feasible start cumul. */
    final long[] earliestStart;

    /** Per dimension: the sum of the transits of the route. */
    final long[] transitSum;

    RouteEval(int dims) {
      spanStart = new long[dims];
      spanEnd = new long[dims];
      earliestStart = new long[dims];
      transitSum = new long[dims];
    }

    void copyFrom(RouteEval o) {
      feasible = o.feasible;
      search = o.search;
      real = o.real;
      used = o.used;
      System.arraycopy(o.spanStart, 0, spanStart, 0, spanStart.length);
      System.arraycopy(o.spanEnd, 0, spanEnd, 0, spanEnd.length);
      System.arraycopy(o.earliestStart, 0, earliestStart, 0, earliestStart.length);
      System.arraycopy(o.transitSum, 0, transitSum, 0, transitSum.length);
    }
  }

  final RoutingProblem p;

  final int vehicles;

  final int numIndices;

  final int size;

  /** True when all costs are arc, fixed and penalty costs (fast move pre-filtering). */
  final boolean arcOnly;

  final boolean hasPairs;

  final Scheduler scheduler = new Scheduler();

  int[][] seq;

  final int[] veh;

  final int[] pos;

  final byte[] status;

  final int[] disjCount;

  final RouteEval[] routes;

  long penaltyCost;

  long globalSpanCost;

  /** Prefix sums of the search arc costs (forward and backward) per vehicle. */
  final long[][] forward;

  final long[][] backward;

  // guided local search
  boolean gls;

  long glsFactor;

  final LongIntMap glsPenalties = new LongIntMap();

  /** During the construction of the first solution undecided indices are allowed. */
  boolean partial = true;

  /**
   * In the repair phase mandatory nodes may be unperformed at the price of {@link #BIG}, so that
   * the local search can complete a partial first solution.
   */
  boolean relaxed;

  /** The penalty of an unperformed mandatory node in the repair phase. */
  static final long BIG = 1L << 50;

  /** The search cost change of the last committed move. */
  long lastDelta;

  private final RouteEval eval1;

  private final RouteEval eval2;

  // scratch data for the pickup and delivery checks
  private final int[] mark;

  private int markStamp;

  private final int[] pairSeen;

  private int[] pairQueue = new int[16];

  // undo log
  private int[] undoNode = new int[64];

  private int[] undoVeh = new int[64];

  private int[] undoPos = new int[64];

  private byte[] undoStatus = new byte[64];

  private int undoSize;

  SearchState(RoutingProblem p) {
    this.p = p;
    this.vehicles = p.vehicles;
    this.numIndices = p.numIndices;
    this.size = p.size;
    this.arcOnly = !p.hasDimensionCosts();
    this.hasPairs = p.pairPickup.length > 0;
    this.seq = new int[vehicles][];
    this.veh = new int[numIndices];
    this.pos = new int[numIndices];
    this.status = new byte[numIndices];
    this.disjCount = new int[p.disjunctionNodes.length];
    this.routes = new RouteEval[vehicles];
    this.forward = new long[vehicles][];
    this.backward = new long[vehicles][];
    this.eval1 = new RouteEval(p.dims.length);
    this.eval2 = new RouteEval(p.dims.length);
    this.mark = new int[numIndices];
    this.pairSeen = new int[p.pairPickup.length];
    Arrays.fill(veh, -1);
    Arrays.fill(status, UNDECIDED);
    for (int v = 0; v < vehicles; v++) {
      int s = p.start[v];
      int e = p.end[v];
      seq[v] = new int[] {s, e};
      veh[s] = v;
      veh[e] = v;
      pos[s] = 0;
      pos[e] = 1;
      status[s] = ACTIVE;
      status[e] = ACTIVE;
      routes[v] = new RouteEval(p.dims.length);
    }
  }

  /** Removes all guided local search penalties. */
  void glsFactorReset() {
    glsPenalties.clear();
    glsFactor = 0;
  }

  /** Recomputes all cached route data; false if some route is infeasible. */
  boolean recomputeAll() {
    boolean ok = true;
    for (int v = 0; v < vehicles; v++) {
      if (!evaluate(v, seq[v], routes[v])) {
        ok = false;
      }
      computePrefix(v);
    }
    penaltyCost = 0;
    Arrays.fill(disjCount, 0);
    for (int i = 0; i < numIndices; i++) {
      if (status[i] == ACTIVE && p.isVisit(i)) {
        for (int d : p.disjunctionsOfNode[i]) {
          disjCount[d]++;
        }
      }
    }
    for (int d = 0; d < disjCount.length; d++) {
      penaltyCost = CapOps.capAdd(penaltyCost, disjunctionPenalty(d, disjCount[d]));
    }
    if (relaxed) {
      for (int i = 0; i < numIndices; i++) {
        if (p.isVisit(i) && status[i] != ACTIVE && hardMandatory(i)) {
          penaltyCost = CapOps.capAdd(penaltyCost, BIG);
        }
      }
    }
    globalSpanCost = globalSpan(-1, null, -1, null);
    return ok;
  }

  /** The search cost of the arc <code>i -&gt; j</code> on vehicle <code>v</code>. */
  long sa(int v, int i, int j) {
    long c = p.arcCost[v].get(i, j);
    if (gls) {
      int pen = glsPenalties.get((long) i * numIndices + j);
      if (pen != 0) {
        c = CapOps.capAdd(c, CapOps.capProd(glsFactor, pen));
      }
    }
    return c;
  }

  /** The real cost of the arc <code>i -&gt; j</code> on vehicle <code>v</code>. */
  long ra(int v, int i, int j) {
    return p.arcCost[v].get(i, j);
  }

  void computePrefix(int v) {
    int[] s = seq[v];
    long[] f = forward[v];
    long[] b = backward[v];
    if (f == null || f.length < s.length) {
      f = new long[Math.max(s.length, 8)];
      b = new long[f.length];
      forward[v] = f;
      backward[v] = b;
    }
    f[0] = 0;
    b[0] = 0;
    for (int k = 0; k + 1 < s.length; k++) {
      f[k + 1] = CapOps.capAdd(f[k], sa(v, s[k], s[k + 1]));
      b[k + 1] = CapOps.capAdd(b[k], sa(v, s[k + 1], s[k]));
    }
  }

  /**
   * The penalty of disjunction <code>d</code> with <code>count</code> active nodes, as the cost
   * terms of the C++ <code>Model::CreateDisjunction</code>: the soft minimum penalty (paid once,
   * or per node missing below the soft minimum, counted from the hard minimum) and the soft
   * maximum penalty (paid once, or per node in excess). In the repair phase a missing node below
   * the hard minimum costs {@link #BIG}.
   */
  long disjunctionPenalty(int d, int count) {
    int min = p.disjunctionMinCardinality[d];
    long total = 0;
    if (count < min && relaxed) {
      total = CapOps.capProd(BIG, min - count);
    }
    int softMin = p.disjunctionSoftMinCardinality[d];
    long minPenalty = p.disjunctionSoftMinPenalty[d];
    if (minPenalty > 0 && softMin > min) {
      int effective = Math.max(count, min);
      if (effective < softMin) {
        total = CapOps.capAdd(total, p.disjunctionSoftMinPerInactive[d]
            ? CapOps.capProd(minPenalty, softMin - effective) : minPenalty);
      }
    }
    int softMax = p.disjunctionSoftMaxCardinality[d];
    long maxPenalty = p.disjunctionSoftMaxPenalty[d];
    if (maxPenalty > 0 && softMax < p.disjunctionMaxCardinality[d] && count > softMax) {
      total = CapOps.capAdd(total, p.disjunctionSoftMaxPerInactive[d]
          ? CapOps.capProd(maxPenalty, count - softMax) : maxPenalty);
    }
    return total;
  }

  /**
   * Whether <code>node</code> may be unperformed in some solution: it is not forced active and
   * each of its disjunctions (it has at least one) has a hard minimum below its number of nodes.
   */
  boolean isOptional(int node) {
    if (relaxed) {
      return true;
    }
    if (p.forcedActive[node]) {
      return false;
    }
    int[] ds = p.disjunctionsOfNode[node];
    if (ds.length == 0) {
      return false;
    }
    for (int d : ds) {
      if (p.disjunctionMinCardinality[d] >= p.disjunctionNodes[d].length) {
        return false;
      }
    }
    return true;
  }

  /** Whether a disjunction of <code>node</code> has fewer active nodes than its hard minimum. */
  boolean belowHardMinimum(int node) {
    for (int d : p.disjunctionsOfNode[node]) {
      if (disjCount[d] < p.disjunctionMinCardinality[d]) {
        return true;
      }
    }
    return false;
  }

  /** Whether a disjunction of <code>node</code> has reached its hard maximum. */
  boolean inSaturatedDisjunction(int node) {
    for (int d : p.disjunctionsOfNode[node]) {
      if (disjCount[d] >= p.disjunctionMaxCardinality[d]) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether the undecided <code>node</code> may become unperformed when a first solution is
   * completed: it is optional and its hard minima are reached, or it cannot be activated any
   * more.
   */
  boolean mayStayInactive(int node) {
    if (relaxed) {
      return true;
    }
    if (hardMandatory(node)) {
      return false;
    }
    return (isOptional(node) && !belowHardMinimum(node)) || inSaturatedDisjunction(node);
  }

  /** Whether <code>node</code> must be performed regardless of disjunctions. */
  boolean hardMandatory(int node) {
    return p.forcedActive[node] || p.disjunctionsOfNode[node].length == 0;
  }

  /** Whether <code>node</code> could be activated without exceeding a cardinality. */
  boolean canActivate(int node) {
    if (p.forcedInactive[node]) {
      return false;
    }
    for (int d : p.disjunctionsOfNode[node]) {
      if (disjCount[d] >= p.disjunctionMaxCardinality[d]) {
        return false;
      }
    }
    return true;
  }

  /**
   * The penalty for leaving the inactive <code>node</code> unperformed, MAX_VALUE if it is
   * mandatory or needed for a hard minimum: per disjunction the penalty change when it becomes
   * active; a soft minimum paid once counts in full while it is not reached (every node inserted
   * brings the disjunction closer to it).
   */
  long inactivePenalty(int node) {
    if (!isOptional(node) || (!relaxed && belowHardMinimum(node))) {
      return Long.MAX_VALUE;
    }
    long total = hardMandatory(node) ? BIG : 0;
    for (int d : p.disjunctionsOfNode[node]) {
      int count = disjCount[d];
      long saved = CapOps.capSub(disjunctionPenalty(d, count), disjunctionPenalty(d, count + 1));
      if (!p.disjunctionSoftMinPerInactive[d] && p.disjunctionSoftMinPenalty[d] > 0
          && count + 1 < p.disjunctionSoftMinCardinality[d]) {
        saved = CapOps.capAdd(saved, p.disjunctionSoftMinPenalty[d]);
      }
      total = CapOps.capAdd(total, saved);
    }
    return total;
  }

  /** The change of the penalty cost when <code>act</code> become active, <code>deact</code> not. */
  long penaltyDelta(int[] act, int[] deact) {
    if ((act == null || act.length == 0) && (deact == null || deact.length == 0)) {
      return 0;
    }
    long before = 0;
    long after = 0;
    // collect touched disjunctions with their count change
    int[] touched = new int[8];
    int[] change = new int[8];
    int n = 0;
    if (act != null) {
      for (int node : act) {
        for (int d : p.disjunctionsOfNode[node]) {
          n = addChange(touched, change, n, d, 1);
          if (n < 0) {
            return 0;
          }
        }
      }
    }
    if (deact != null) {
      for (int node : deact) {
        for (int d : p.disjunctionsOfNode[node]) {
          n = addChange(touched, change, n, d, -1);
          if (n < 0) {
            return 0;
          }
        }
      }
    }
    for (int k = 0; k < n; k++) {
      int d = touched[k];
      before = CapOps.capAdd(before, disjunctionPenalty(d, disjCount[d]));
      after = CapOps.capAdd(after, disjunctionPenalty(d, disjCount[d] + change[k]));
    }
    long delta = CapOps.capSub(after, before);
    if (relaxed) {
      if (act != null) {
        for (int node : act) {
          if (hardMandatory(node)) {
            delta = CapOps.capSub(delta, BIG);
          }
        }
      }
      if (deact != null) {
        for (int node : deact) {
          if (hardMandatory(node)) {
            delta = CapOps.capAdd(delta, BIG);
          }
        }
      }
    }
    return delta;
  }

  private static int addChange(int[] touched, int[] change, int n, int d, int delta) {
    for (int k = 0; k < n; k++) {
      if (touched[k] == d) {
        change[k] += delta;
        return n;
      }
    }
    if (n == touched.length) {
      return -1;
    }
    touched[n] = d;
    change[n] = delta;
    return n + 1;
  }

  /**
   * The search cost change of the arcs and fixed cost when a route of <code>v</code> changes
   * between empty and non-empty, for the move estimates computed with the raw start to end arc.
   */
  long emptyAdjust(int v, boolean oldEmpty, boolean newEmpty) {
    if (oldEmpty == newEmpty || p.usedWhenEmpty[v]) {
      return 0;
    }
    long se = sa(v, p.start[v], p.end[v]);
    if (oldEmpty) {
      // the empty route cost nothing, the raw estimate subtracted the start to end arc
      return CapOps.capAdd(se, p.fixedCost[v]);
    }
    return CapOps.capSub(-se, p.fixedCost[v]);
  }

  /** Evaluates route <code>s</code> of vehicle <code>v</code> into <code>out</code>. */
  boolean evaluate(int v, int[] s, RouteEval out) {
    out.feasible = false;
    final int n = s.length;
    for (int k = 0; k + 1 < n; k++) {
      if (!p.nextAllowed(s[k], s[k + 1])) {
        return false;
      }
    }
    for (int k = 1; k + 1 < n; k++) {
      if (!p.vehicleAllowed(s[k], v)) {
        return false;
      }
    }
    if (hasPairs && !checkPairsInRoute(v, s)) {
      return false;
    }
    boolean used = n > 2 || p.usedWhenEmpty[v];
    long real = 0;
    long search = 0;
    if (used) {
      for (int k = 0; k + 1 < n; k++) {
        long c = ra(v, s[k], s[k + 1]);
        real = CapOps.capAdd(real, c);
        search = CapOps.capAdd(search, gls ? sa(v, s[k], s[k + 1]) : c);
      }
      real = CapOps.capAdd(real, p.fixedCost[v]);
      search = CapOps.capAdd(search, p.fixedCost[v]);
    }
    for (int d = 0; d < p.dims.length; d++) {
      DimensionData dim = p.dims[d];
      if (!scheduler.run(dim, v, s, null)) {
        return false;
      }
      for (long[] prec : dim.precedences) {
        // cumul(second) >= cumul(first) + offset for two nodes of this route
        int a = indexOf(s, (int) prec[0]);
        int b = a < 0 ? -1 : indexOf(s, (int) prec[1]);
        if (b >= 0 && CapOps.capSub(scheduler.max(b), scheduler.min(a)) < prec[2]) {
          return false;
        }
      }
      if (used && dim.hasCost()) {
        long c = scheduler.cost(dim, v, s);
        real = CapOps.capAdd(real, c);
        search = CapOps.capAdd(search, c);
      }
      out.spanStart[d] = scheduler.max(0);
      out.earliestStart[d] = scheduler.min(0);
      out.spanEnd[d] = scheduler.end;
      out.transitSum[d] = scheduler.transitSum();
    }
    out.used = used;
    out.real = real;
    out.search = search;
    out.feasible = true;
    return true;
  }

  private static int indexOf(int[] s, int x) {
    for (int k = 0; k < s.length; k++) {
      if (s[k] == x) {
        return k;
      }
    }
    return -1;
  }

  private boolean checkPairsInRoute(int v, int[] s) {
    final int n = s.length;
    markStamp++;
    if (markStamp == Integer.MAX_VALUE) {
      Arrays.fill(mark, 0);
      Arrays.fill(pairSeen, 0);
      markStamp = 1;
    }
    for (int k = 1; k + 1 < n; k++) {
      mark[s[k]] = markStamp;
    }
    int policy = p.policy[v];
    int head = 0;
    int tail = 0;
    if (pairQueue.length < n) {
      pairQueue = new int[n];
    }
    for (int k = 1; k + 1 < n; k++) {
      int x = s[k];
      int q = p.pairOfNode[x];
      if (q < 0) {
        continue;
      }
      if (p.pairPickup[q] == x) {
        pairSeen[q] = markStamp;
        if (policy != 0 && mark[p.pairDelivery[q]] == markStamp) {
          pairQueue[tail++] = q;
        }
      } else {
        int pickup = p.pairPickup[q];
        if (mark[pickup] == markStamp) {
          if (pairSeen[q] != markStamp) {
            // delivery before its pickup
            return false;
          }
          if (policy == 1) {
            if (tail == head || pairQueue[tail - 1] != q) {
              return false;
            }
            tail--;
          } else if (policy == 2) {
            if (tail == head || pairQueue[head] != q) {
              return false;
            }
            head++;
          }
        }
      }
    }
    return true;
  }

  /**
   * The global span cost with the routes of v1 and v2 replaced by the given evaluations. The
   * cost-minimal common window starts at the smallest latest start A of the used routes; every
   * route then starts at <code>max(A, earliest start)</code> and ends at
   * <code>max(start + transits, earliest end)</code>.
   */
  long globalSpan(int v1, RouteEval e1, int v2, RouteEval e2) {
    long total = 0;
    for (int d = 0; d < p.dims.length; d++) {
      long coef = p.dims[d].globalSpanCost;
      if (coef == 0) {
        continue;
      }
      long a = Long.MAX_VALUE;
      boolean any = false;
      for (int v = 0; v < vehicles; v++) {
        RouteEval e = v == v1 ? e1 : (v == v2 ? e2 : routes[v]);
        if (e.used) {
          a = Math.min(a, e.spanStart[d]);
          any = true;
        }
      }
      if (!any) {
        continue;
      }
      long b = Long.MIN_VALUE;
      for (int v = 0; v < vehicles; v++) {
        RouteEval e = v == v1 ? e1 : (v == v2 ? e2 : routes[v]);
        if (e.used) {
          long start = Math.max(a, e.earliestStart[d]);
          long end = Math.max(CapOps.capAdd(start, e.transitSum[d]), e.spanEnd[d]);
          b = Math.max(b, end);
        }
      }
      total = CapOps.capAdd(total, CapOps.capProd(coef, CapOps.capSub(b, a)));
    }
    return total;
  }

  /** The real objective of the current solution. */
  long realObjective() {
    long total = CapOps.capAdd(penaltyCost, globalSpanCost);
    for (int v = 0; v < vehicles; v++) {
      total = CapOps.capAdd(total, routes[v].real);
    }
    return total;
  }

  /** The search (augmented) objective of the current solution. */
  long searchObjective() {
    long total = CapOps.capAdd(penaltyCost, globalSpanCost);
    for (int v = 0; v < vehicles; v++) {
      total = CapOps.capAdd(total, routes[v].search);
    }
    return total;
  }

  private void logUndo(int node) {
    if (undoSize == undoNode.length) {
      int m = undoSize * 2;
      undoNode = Arrays.copyOf(undoNode, m);
      undoVeh = Arrays.copyOf(undoVeh, m);
      undoPos = Arrays.copyOf(undoPos, m);
      undoStatus = Arrays.copyOf(undoStatus, m);
    }
    undoNode[undoSize] = node;
    undoVeh[undoSize] = veh[node];
    undoPos[undoSize] = pos[node];
    undoStatus[undoSize] = status[node];
    undoSize++;
  }

  /**
   * Tries the move which gives vehicle <code>v1</code> the route <code>s1</code> (and
   * <code>v2</code> the route <code>s2</code> if <code>v2 &gt;= 0</code>), activates
   * <code>act</code> and deactivates <code>deact</code>. The move is committed if it is feasible
   * and, when <code>requireImprovement</code>, decreases the search objective.
   */
  boolean tryMove(int v1, int[] s1, int v2, int[] s2, int[] act, int[] deact,
      boolean requireImprovement) {
    return tryMove(v1, s1, v2, s2, act, deact, requireImprovement, false);
  }

  /**
   * Like {@link #tryMove(int, int[], int, int[], int[], int[], boolean)}; with
   * <code>dryRun</code> a feasible move is undone after the checks (its cost change is left in
   * {@link #lastDelta}).
   */
  boolean tryMove(int v1, int[] s1, int v2, int[] s2, int[] act, int[] deact,
      boolean requireImprovement, boolean dryRun) {
    if (!evaluate(v1, s1, eval1)) {
      return false;
    }
    if (v2 >= 0 && !evaluate(v2, s2, eval2)) {
      return false;
    }
    long pd = penaltyDelta(act, deact);
    long gsNew = globalSpan(v1, eval1, v2, v2 >= 0 ? eval2 : null);
    long delta = CapOps.capSub(eval1.search, routes[v1].search);
    if (v2 >= 0) {
      delta = CapOps.capAdd(delta, CapOps.capSub(eval2.search, routes[v2].search));
    }
    delta = CapOps.capAdd(delta, pd);
    delta = CapOps.capAdd(delta, CapOps.capSub(gsNew, globalSpanCost));
    if (requireImprovement && delta >= 0) {
      return false;
    }
    // tentative application
    undoSize = 0;
    int[] old1 = seq[v1];
    int[] old2 = v2 >= 0 ? seq[v2] : null;
    for (int k = 1; k + 1 < old1.length; k++) {
      logUndo(old1[k]);
      veh[old1[k]] = -1;
    }
    if (old2 != null) {
      for (int k = 1; k + 1 < old2.length; k++) {
        logUndo(old2[k]);
        veh[old2[k]] = -1;
      }
    }
    if (act != null) {
      for (int node : act) {
        logUndo(node);
        status[node] = ACTIVE;
        for (int d : p.disjunctionsOfNode[node]) {
          disjCount[d]++;
        }
      }
    }
    if (deact != null) {
      for (int node : deact) {
        logUndo(node);
        status[node] = INACTIVE;
        veh[node] = -1;
        for (int d : p.disjunctionsOfNode[node]) {
          disjCount[d]--;
        }
      }
    }
    seq[v1] = s1;
    place(v1, s1);
    if (v2 >= 0) {
      seq[v2] = s2;
      place(v2, s2);
    }
    boolean feasible = globalChecks(v1, s1, v2, s2, act, deact);
    if (feasible && dryRun) {
      lastDelta = delta;
    }
    if (!feasible || dryRun) {
      // undo
      seq[v1] = old1;
      if (v2 >= 0) {
        seq[v2] = old2;
      }
      if (act != null) {
        for (int node : act) {
          for (int d : p.disjunctionsOfNode[node]) {
            disjCount[d]--;
          }
        }
      }
      if (deact != null) {
        for (int node : deact) {
          for (int d : p.disjunctionsOfNode[node]) {
            disjCount[d]++;
          }
        }
      }
      for (int k = undoSize - 1; k >= 0; k--) {
        int node = undoNode[k];
        veh[node] = undoVeh[k];
        pos[node] = undoPos[k];
        status[node] = undoStatus[k];
      }
      place(v1, old1);
      if (v2 >= 0) {
        place(v2, old2);
      }
      return feasible;
    }
    // commit
    routes[v1].copyFrom(eval1);
    computePrefix(v1);
    if (v2 >= 0) {
      routes[v2].copyFrom(eval2);
      computePrefix(v2);
    }
    penaltyCost = CapOps.capAdd(penaltyCost, pd);
    globalSpanCost = gsNew;
    lastDelta = delta;
    return true;
  }

  private void place(int v, int[] s) {
    for (int k = 0; k < s.length; k++) {
      int node = s[k];
      veh[node] = v;
      pos[node] = k;
      status[node] = ACTIVE;
    }
  }

  private boolean globalChecks(int v1, int[] s1, int v2, int[] s2, int[] act, int[] deact) {
    if (hasPairs) {
      if (!pairsAcrossRoutes(s1) || (s2 != null && !pairsAcrossRoutes(s2))) {
        return false;
      }
      if (deact != null) {
        for (int node : deact) {
          int q = p.pairOfNode[node];
          if (q >= 0) {
            int partner = p.pairPickup[q] == node ? p.pairDelivery[q] : p.pairPickup[q];
            if (status[partner] == ACTIVE || (!partial && status[partner] == UNDECIDED)) {
              return false;
            }
          }
        }
      }
    }
    if (act != null) {
      for (int node : act) {
        for (int d : p.disjunctionsOfNode[node]) {
          if (disjCount[d] > p.disjunctionMaxCardinality[d]) {
            return false;
          }
        }
      }
    }
    if (deact != null) {
      for (int node : deact) {
        if (relaxed) {
          continue;
        }
        if (p.forcedActive[node]) {
          return false;
        }
        for (int d : p.disjunctionsOfNode[node]) {
          if (!partial && disjCount[d] < p.disjunctionMinCardinality[d]) {
            return false;
          }
        }
        if (p.disjunctionsOfNode[node].length == 0) {
          return false;
        }
      }
    }
    if (p.maxActiveVehicles < vehicles) {
      int used = 0;
      for (int v = 0; v < vehicles; v++) {
        if (seq[v].length > 2) {
          used++;
        }
      }
      if (used > p.maxActiveVehicles) {
        return false;
      }
    }
    if (p.sideConstraints != null) {
      return p.sideConstraints.check(seq, status, null);
    }
    return true;
  }

  private boolean pairsAcrossRoutes(int[] s) {
    for (int k = 1; k + 1 < s.length; k++) {
      int x = s[k];
      int q = p.pairOfNode[x];
      if (q < 0) {
        continue;
      }
      int partner = p.pairPickup[q] == x ? p.pairDelivery[q] : p.pairPickup[q];
      byte st = status[partner];
      if (st == ACTIVE) {
        if (veh[partner] != veh[x]) {
          return false;
        }
      } else if (st == INACTIVE || !partial) {
        return false;
      }
    }
    return true;
  }

  /**
   * Whether the current solution is complete: no undecided index, every inactive index in a
   * disjunction, the number of active nodes of every disjunction within its hard bounds.
   */
  boolean isCompleteAndFeasible() {
    for (int i = 0; i < size; i++) {
      if (p.isVisit(i) && status[i] == UNDECIDED) {
        return false;
      }
      if (p.isVisit(i) && status[i] == INACTIVE && hardMandatory(i)) {
        return false;
      }
    }
    for (int d = 0; d < disjCount.length; d++) {
      if (disjCount[d] > p.disjunctionMaxCardinality[d]
          || disjCount[d] < p.disjunctionMinCardinality[d]) {
        return false;
      }
    }
    for (int v = 0; v < vehicles; v++) {
      if (!routes[v].feasible) {
        return false;
      }
      if (hasPairs && !pairsAcrossRoutes(seq[v])) {
        return false;
      }
    }
    if (p.sideConstraints != null) {
      return p.sideConstraints.check(seq, status, null);
    }
    return true;
  }

  /** Replaces the solution by the given routes and statuses (and recomputes the caches). */
  void load(int[][] routesToLoad, byte[] statusToLoad) {
    System.arraycopy(statusToLoad, 0, status, 0, status.length);
    Arrays.fill(veh, -1);
    for (int v = 0; v < vehicles; v++) {
      seq[v] = routesToLoad[v].clone();
      for (int k = 0; k < seq[v].length; k++) {
        veh[seq[v][k]] = v;
        pos[seq[v][k]] = k;
      }
    }
    recomputeAll();
  }

  /** A copy of the routes. */
  int[][] copyRoutes() {
    int[][] r = new int[vehicles][];
    for (int v = 0; v < vehicles; v++) {
      r[v] = seq[v].clone();
    }
    return r;
  }

  // ----- array helpers for building new routes -----

  /** <code>s</code> without positions <code>[from, from + count)</code>. */
  static int[] remove(int[] s, int from, int count) {
    int[] r = new int[s.length - count];
    System.arraycopy(s, 0, r, 0, from);
    System.arraycopy(s, from + count, r, from, s.length - from - count);
    return r;
  }

  /** <code>s</code> with <code>values</code> inserted after position <code>after</code>. */
  static int[] insert(int[] s, int after, int... values) {
    int[] r = new int[s.length + values.length];
    System.arraycopy(s, 0, r, 0, after + 1);
    System.arraycopy(values, 0, r, after + 1, values.length);
    System.arraycopy(s, after + 1, r, after + 1 + values.length, s.length - after - 1);
    return r;
  }

  /** <code>s</code> with positions <code>[from, to]</code> reversed. */
  static int[] reverse(int[] s, int from, int to) {
    int[] r = s.clone();
    while (from < to) {
      int t = r[from];
      r[from] = r[to];
      r[to] = t;
      from++;
      to--;
    }
    return r;
  }
}
