package org.matheclipse.ortools.routing.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * The simplified routing engine of this module, which reimplements the search of the OR-Tools
 * routing library (<code>Model::SolveWithParameters</code>) as:
 *
 * <ol>
 * <li>a first solution heuristic ({@link FirstSolutions}) chosen by the first solution strategy,
 * with the parallel cheapest insertion as fallback;
 * <li>a first improvement local search ({@link LocalSearch}) until a local optimum (greedy
 * descent, the default metaheuristic);
 * <li>for <code>GUIDED_LOCAL_SEARCH</code> (and simulated annealing and tabu search, which are
 * mapped to it): arc penalties at every local optimum and further descents on the augmented
 * cost until the time or solution limit.
 * </ol>
 *
 * The best solution found (by the real objective) is returned.
 */
public final class RoutingEngine {

  /** The search settings taken from <code>RoutingSearchParameters</code>. */
  public static final class Parameters {
    /** The number of the first solution strategy. */
    public int firstSolutionStrategy = 15;

    /** The number of the local search metaheuristic. */
    public int metaheuristic = 6;

    /** The time limit in nanoseconds (MAX_VALUE = none). */
    public long timeLimitNanos = Long.MAX_VALUE;

    /** The maximal number of solutions (MAX_VALUE = none). */
    public long solutionLimit = Long.MAX_VALUE;

    /** The guided local search lambda coefficient. */
    public double glsLambda = 0.1;

    /** Whether to log the search progress. */
    public boolean logSearch;
  }

  /** Receives the solutions of the search. */
  public interface Listener {
    /**
     * Called for the first solution and for every accepted move.
     *
     * @return false to stop the search
     */
    boolean onSolution(int[][] routes, byte[] status, long objective);
  }

  /** The outcome of a search. */
  public static final class Result {
    /** Whether a solution was found. */
    public boolean found;

    /** Whether the time limit was reached. */
    public boolean timedOut;

    /** The routes of the best solution (start ... end per vehicle). */
    public int[][] routes;

    /** The status of every index in the best solution. */
    public byte[] status;

    /** The objective of the best solution. */
    public long objective;

    /** The number of solutions found. */
    public long solutions;
  }

  /** Cumul, slack, transit and break values of a solution. */
  public static final class Details {
    /** Per dimension and index. */
    public long[][] cumulMin;

    /** Per dimension and index. */
    public long[][] cumulMax;

    /** Per dimension and index with a successor. */
    public long[][] slackMin;

    /** Per dimension and index with a successor. */
    public long[][] slackMax;

    /** Per dimension and index with a successor. */
    public long[][] transit;

    /** Per dimension, vehicle and break: the start, or MIN_VALUE if unperformed. */
    public long[][][] breakStart;
  }

  private static final Logger LOGGER = Logger.getLogger(RoutingEngine.class.getName());

  private final RoutingProblem p;

  private final Parameters params;

  private final Listener listener;

  private long startNanos;

  private long deadline;

  private long solutionCount;

  private boolean stopRequested;

  private boolean timedOut;

  private boolean repairing;

  private SearchState current;

  private int[][] bestRoutes;

  private byte[] bestStatus;

  private long bestObjective = Long.MAX_VALUE;

  /** Creates an engine for the closed problem. */
  public RoutingEngine(RoutingProblem p, Parameters params, Listener listener) {
    this.p = p;
    this.params = params;
    this.listener = listener;
  }

  /** Whether the search must stop (time limit, solution limit, stop request). */
  boolean shouldStop() {
    if (stopRequested) {
      return true;
    }
    if (solutionCount >= params.solutionLimit) {
      return true;
    }
    if (params.timeLimitNanos != Long.MAX_VALUE && System.nanoTime() - deadline > 0) {
      timedOut = true;
      return true;
    }
    return false;
  }

  /** Throws the stop signal if the search must stop. */
  void checkStop() {
    if (shouldStop()) {
      throw LocalSearch.STOP;
    }
  }

  private long elapsedMillis() {
    return (System.nanoTime() - startNanos) / 1_000_000L;
  }

  /** Records the current solution (counts it, keeps it if best, informs the listener). */
  void onImprovement() {
    if (repairing) {
      return;
    }
    SearchState s = current;
    solutionCount++;
    long objective = s.realObjective();
    if (bestRoutes == null || objective < bestObjective) {
      bestObjective = objective;
      bestRoutes = s.copyRoutes();
      bestStatus = s.status.clone();
      if (params.logSearch) {
        LOGGER.info("Solution #" + solutionCount + " (objective " + objective + ", time = "
            + elapsedMillis() + " ms)");
      }
    }
    if (listener != null && !listener.onSolution(s.seq, s.status, objective)) {
      stopRequested = true;
    }
  }

  private static int resolveStrategy(int strategy, boolean pairs) {
    switch (strategy) {
      case 0:
      case 15:
        return pairs ? 8 : 3;
      case 6:
      case 8:
      case 9:
      case 10:
        return strategy;
      case 7:
      case 14:
      case 16:
        return 8;
      case 17:
        return 10;
      default:
        return 3;
    }
  }

  private boolean runStrategy(SearchState s, int strategy) {
    FirstSolutions fs = new FirstSolutions(s, this);
    switch (strategy) {
      case 6:
        return fs.allUnperformed();
      case 8:
        return fs.parallelCheapestInsertion();
      case 9:
        return fs.localCheapestInsertion();
      case 10:
        return fs.savings();
      default:
        return fs.pathCheapestArc();
    }
  }

  /** Builds a first solution, trying fallback heuristics; null if none was found. */
  private SearchState firstSolution() {
    int strategy = resolveStrategy(params.firstSolutionStrategy, p.pairPickup.length > 0);
    List<Integer> chain = new ArrayList<>();
    chain.add(strategy);
    if (strategy != 6) {
      if (!chain.contains(8)) {
        chain.add(8);
      }
      if (!chain.contains(3)) {
        chain.add(3);
      }
    }
    SearchState partialBest = null;
    int partialUndecided = Integer.MAX_VALUE;
    for (int st : chain) {
      SearchState s = new SearchState(p);
      current = s;
      boolean ok = runStrategy(s, st);
      if (!ok) {
        int undecided = 0;
        for (int i = 0; i < p.size; i++) {
          if (p.isVisit(i) && s.status[i] == SearchState.UNDECIDED) {
            undecided++;
          }
        }
        if (st != 6 && undecided < partialUndecided) {
          partialBest = s;
          partialUndecided = undecided;
        }
        continue;
      }
      s.partial = false;
      if (s.recomputeAll() && s.isCompleteAndFeasible()) {
        if (params.logSearch) {
          LOGGER.info("First solution (strategy " + st + "): objective " + s.realObjective()
              + ", time = " + elapsedMillis() + " ms");
        }
        return s;
      }
    }
    return partialBest == null ? null : repair(partialBest);
  }

  /**
   * Completes a partial first solution: the missing mandatory nodes become unperformed at a huge
   * penalty and the local search (with guided local search while it makes progress) inserts
   * them; succeeds if all mandatory nodes end up performed.
   */
  private SearchState repair(SearchState s) {
    current = s;
    // undecided nodes become unperformed, with the active partner of an undecided pair node
    for (int i = 0; i < p.size; i++) {
      if (p.isVisit(i) && s.status[i] == SearchState.UNDECIDED) {
        s.status[i] = SearchState.INACTIVE;
        int q = p.pairOfNode[i];
        if (q >= 0) {
          int partner = p.pairPickup[q] == i ? p.pairDelivery[q] : p.pairPickup[q];
          if (s.status[partner] == SearchState.ACTIVE) {
            int v = s.veh[partner];
            s.seq[v] = SearchState.remove(s.seq[v], s.pos[partner], 1);
            s.status[partner] = SearchState.INACTIVE;
            s.veh[partner] = -1;
            for (int k = 0; k < s.seq[v].length; k++) {
              s.pos[s.seq[v][k]] = k;
            }
          }
        }
      }
    }
    s.partial = false;
    s.relaxed = true;
    if (!s.recomputeAll()) {
      return null;
    }
    LocalSearch ls = new LocalSearch(s, this);
    long budget = 50;
    repairing = true;
    boolean complete = false;
    for (int round = 0; round < budget && !complete; round++) {
      ls.descend();
      complete = s.penaltyCost < SearchState.BIG;
      if (!complete) {
        if (!s.gls) {
          long base = 0;
          for (int v = 0; v < p.vehicles; v++) {
            base += s.routes[v].real;
          }
          s.glsFactor = Math.max(1, base / Math.max(1, p.size) / 10);
          s.gls = true;
        }
        ls.penalizeLocalOptimum();
        s.recomputeAll();
      }
    }
    repairing = false;
    s.gls = false;
    s.relaxed = false;
    s.glsFactorReset();
    if (!complete || !s.recomputeAll() || !s.isCompleteAndFeasible()) {
      return null;
    }
    if (params.logSearch) {
      LOGGER.info("First solution (repaired): objective " + s.realObjective() + ", time = "
          + elapsedMillis() + " ms");
    }
    return s;
  }

  /** Loads the given routes (without start and end) as the current solution, or null. */
  private SearchState load(int[][] routes, boolean full) {
    SearchState s = new SearchState(p);
    current = s;
    for (int v = 0; v < p.vehicles && v < routes.length; v++) {
      int[] r = routes[v];
      int from = full ? 1 : 0;
      int to = full ? r.length - 1 : r.length;
      int[] sq = new int[to - from + 2];
      sq[0] = p.start[v];
      sq[sq.length - 1] = p.end[v];
      for (int k = from; k < to; k++) {
        int node = r[k];
        if (node < 0 || node >= p.size || !p.isVisit(node)
            || s.status[node] != SearchState.UNDECIDED) {
          return null;
        }
        sq[k - from + 1] = node;
        s.status[node] = SearchState.ACTIVE;
      }
      s.seq[v] = sq;
      for (int k = 0; k < sq.length; k++) {
        s.veh[sq[k]] = v;
        s.pos[sq[k]] = k;
      }
    }
    for (int i = 0; i < p.size; i++) {
      if (p.isVisit(i) && s.status[i] == SearchState.UNDECIDED) {
        s.status[i] = SearchState.INACTIVE;
      }
    }
    s.partial = false;
    if (!s.recomputeAll() || !s.isCompleteAndFeasible()) {
      return null;
    }
    return s;
  }

  /**
   * Evaluates routes given as node lists per vehicle (without start and end); returns null if
   * they don't form a feasible solution.
   */
  public Result evaluateRoutes(int[][] routes) {
    SearchState s = load(routes, false);
    if (s == null) {
      return null;
    }
    Result r = new Result();
    r.found = true;
    r.routes = s.copyRoutes();
    r.status = s.status.clone();
    r.objective = s.realObjective();
    return r;
  }

  /**
   * Searches a solution, starting from <code>initialRoutes</code> (full routes per vehicle,
   * start to end) if not null.
   */
  public Result solve(int[][] initialRoutes) {
    startNanos = System.nanoTime();
    deadline = params.timeLimitNanos == Long.MAX_VALUE ? Long.MAX_VALUE
        : startNanos + params.timeLimitNanos;
    Result result = new Result();
    SearchState s;
    try {
      s = initialRoutes != null ? load(initialRoutes, true) : firstSolution();
    } catch (LocalSearch.Stop stop) {
      s = null;
    }
    if (s == null) {
      result.timedOut = timedOut;
      return result;
    }
    current = s;
    onImprovement();
    if (!shouldStop()) {
      LocalSearch ls = new LocalSearch(s, this);
      try {
        ls.descend();
        int meta = params.metaheuristic;
        boolean guided = meta == 2 || meta == 3 || meta == 4 || meta == 5;
        boolean unlimited = params.timeLimitNanos == Long.MAX_VALUE
            && params.solutionLimit == Long.MAX_VALUE;
        if (guided) {
          // without a limit the guided search of OR-Tools runs forever; stop instead after 200
          // rounds without a new best solution
          guidedLocalSearch(s, ls, Integer.MAX_VALUE, unlimited ? 200 : Integer.MAX_VALUE);
        } else {
          // the neighborhoods are smaller than those of OR-Tools: escape the first local
          // optimum by a few guided rounds, keeping the best solution
          guidedLocalSearch(s, ls, ESCAPE_ROUNDS, ESCAPE_ROUNDS_WITHOUT_IMPROVEMENT);
        }
      } catch (LocalSearch.Stop stop) {
        // limit reached
      }
    }
    result.found = true;
    result.timedOut = timedOut;
    result.routes = bestRoutes;
    result.status = bestStatus;
    result.objective = bestObjective;
    result.solutions = solutionCount;
    return result;
  }

  /** Guided rounds after the local optimum of a greedy descent. */
  static final int ESCAPE_ROUNDS = 60;

  /** Guided rounds without a new best solution after which the escape stops. */
  static final int ESCAPE_ROUNDS_WITHOUT_IMPROVEMENT = 20;

  /**
   * Guided local search: penalizes the arcs of the local optimum, descends on the augmented
   * cost, and repeats until a limit, <code>maxRounds</code> rounds or
   * <code>maxRoundsWithoutImprovement</code> rounds without a new best solution.
   */
  private void guidedLocalSearch(SearchState s, LocalSearch ls, int maxRounds,
      int maxRoundsWithoutImprovement) {
    long arcs = 0;
    for (int v = 0; v < p.vehicles; v++) {
      if (s.seq[v].length > 2) {
        arcs += s.seq[v].length - 1;
      }
    }
    long base = 0;
    for (int v = 0; v < p.vehicles; v++) {
      base += s.routes[v].real;
    }
    long factor = (long) Math.floor(params.glsLambda * base / Math.max(1, arcs));
    s.glsFactor = Math.max(1, factor);
    s.gls = true;
    int sinceBest = 0;
    long lastBest = bestObjective;
    java.util.Random random = new java.util.Random(0x5EED);
    for (int round = 0; round < maxRounds && !shouldStop(); round++) {
      if (round % 3 == 2) {
        ruinAndRecreate(s, ls, random, round);
      } else {
        ls.penalizeLocalOptimum();
        s.recomputeAll();
        ls.descend();
      }
      if (bestObjective < lastBest) {
        lastBest = bestObjective;
        sinceBest = 0;
      } else if (++sinceBest >= maxRoundsWithoutImprovement) {
        break;
      }
    }
  }

  /**
   * Large neighborhood step: removes some performed nodes (alternately a random set and a node
   * with its closest performed nodes), reinserts them by the parallel cheapest insertion and
   * descends; the result is kept if its real objective is better, otherwise the solution is
   * restored.
   */
  private void ruinAndRecreate(SearchState s, LocalSearch ls, java.util.Random random,
      int round) {
    List<Integer> performed = new ArrayList<>();
    for (int i = 0; i < p.size; i++) {
      if (p.isVisit(i) && s.status[i] == SearchState.ACTIVE) {
        performed.add(i);
      }
    }
    if (performed.size() < 2) {
      return;
    }
    int[][] savedRoutes = s.copyRoutes();
    byte[] savedStatus = s.status.clone();
    long before = s.realObjective();
    boolean gls = s.gls;
    int k = Math.max(2, Math.min(15, performed.size() / 5 + 2));
    boolean[] removed = new boolean[p.numIndices];
    int seed = performed.get(random.nextInt(performed.size()));
    if ((round / 3) % 2 == 0) {
      // the seed and its closest performed nodes
      final ArcMatrix c = p.arcCost[s.veh[seed]];
      performed.sort((a, b) -> {
        long ca = c.get(seed, a) + c.get(a, seed);
        long cb = c.get(seed, b) + c.get(b, seed);
        return ca != cb ? Long.compare(ca, cb) : Integer.compare(a, b);
      });
      for (int j = 0; j < k && j < performed.size(); j++) {
        removed[performed.get(j)] = true;
      }
    } else {
      java.util.Collections.shuffle(performed, random);
      for (int j = 0; j < k; j++) {
        removed[performed.get(j)] = true;
      }
    }
    for (int q = 0; q < p.pairPickup.length; q++) {
      if (removed[p.pairPickup[q]] || removed[p.pairDelivery[q]]) {
        removed[p.pairPickup[q]] = s.status[p.pairPickup[q]] == SearchState.ACTIVE;
        removed[p.pairDelivery[q]] = s.status[p.pairDelivery[q]] == SearchState.ACTIVE;
      }
    }
    int[][] routes = new int[p.vehicles][];
    byte[] status = savedStatus.clone();
    for (int v = 0; v < p.vehicles; v++) {
      int[] r = savedRoutes[v];
      int n = 0;
      int[] nr = new int[r.length];
      for (int x : r) {
        if (removed[x]) {
          status[x] = SearchState.UNDECIDED;
        } else {
          nr[n++] = x;
        }
      }
      routes[v] = Arrays.copyOf(nr, n);
    }
    s.gls = false;
    s.partial = true;
    s.load(routes, status);
    boolean ok;
    try {
      ok = new FirstSolutions(s, this).parallelCheapestInsertion();
    } finally {
      s.partial = false;
    }
    ok = ok && s.recomputeAll() && s.isCompleteAndFeasible();
    if (ok) {
      ls.descend();
    }
    if (ok && s.realObjective() < before) {
      if (s.realObjective() < bestObjective) {
        onImprovement();
      }
      s.gls = gls;
      s.recomputeAll();
      return;
    }
    s.load(savedRoutes, savedStatus);
    s.gls = gls;
    s.recomputeAll();
  }

  /**
   * Computes the cumul windows, slacks, transits and break starts of a solution. Cumuls are
   * fixed in this order: values chosen by the user constraints, the finalizer variables (each
   * specification is <code>{dimension, index, kind (0 min, 1 max, 2 target), target}</code>),
   * and for dimensions with costs the cost-minimal schedule.
   */
  public Details details(int[][] routes, byte[] status, long[][] finalizers) {
    final int nd = p.dims.length;
    Details det = new Details();
    det.cumulMin = new long[nd][p.numIndices];
    det.cumulMax = new long[nd][p.numIndices];
    det.slackMin = new long[nd][p.size];
    det.slackMax = new long[nd][p.size];
    det.transit = new long[nd][p.size];
    det.breakStart = new long[nd][p.vehicles][];
    long[][] fixed = new long[nd][p.numIndices];
    for (long[] f : fixed) {
      Arrays.fill(f, Long.MIN_VALUE);
    }
    if (p.sideConstraints != null) {
      p.sideConstraints.check(routes, status, fixed);
    }
    int[] vehOf = new int[p.numIndices];
    Arrays.fill(vehOf, -1);
    for (int v = 0; v < p.vehicles; v++) {
      for (int x : routes[v]) {
        vehOf[x] = v;
      }
    }
    Scheduler sch = new Scheduler();
    if (finalizers != null) {
      for (long[] f : finalizers) {
        int d = (int) f[0];
        int index = (int) f[1];
        int v = vehOf[index];
        if (v < 0 || fixed[d][index] != Long.MIN_VALUE) {
          continue;
        }
        int[] r = routes[v];
        if (!sch.run(p.dims[d], v, r, fixed[d])) {
          continue;
        }
        int k = indexOf(r, index);
        long lo = sch.min(k);
        long hi = sch.max(k);
        long value = f[2] == 0 ? lo : (f[2] == 1 ? hi : Math.max(lo, Math.min(hi, f[3])));
        fixed[d][index] = value;
      }
    }
    // global span: all used routes start at max(A, earliest start), A the smallest latest start
    for (int d = 0; d < nd; d++) {
      DimensionData dim = p.dims[d];
      if (dim.globalSpanCost == 0) {
        continue;
      }
      long a = Long.MAX_VALUE;
      for (int v = 0; v < p.vehicles; v++) {
        int[] r = routes[v];
        if ((r.length > 2 || p.usedWhenEmpty[v]) && sch.run(dim, v, r, fixed[d])) {
          a = Math.min(a, sch.max(0));
        }
      }
      for (int v = 0; v < p.vehicles && a != Long.MAX_VALUE; v++) {
        int[] r = routes[v];
        if ((r.length > 2 || p.usedWhenEmpty[v]) && fixed[d][r[0]] == Long.MIN_VALUE
            && sch.run(dim, v, r, fixed[d])) {
          fixed[d][r[0]] = Math.max(a, sch.min(0));
        }
      }
    }
    for (int d = 0; d < nd; d++) {
      DimensionData dim = p.dims[d];
      for (int i = 0; i < p.numIndices; i++) {
        det.cumulMin[d][i] = dim.cumulMin[i];
        det.cumulMax[d][i] = dim.cumulMax[i];
      }
      for (int i = 0; i < p.size; i++) {
        det.slackMin[d][i] = 0;
        det.slackMax[d][i] = dim.slackMax;
        det.transit[d][i] = 0;
      }
      for (int v = 0; v < p.vehicles; v++) {
        int[] r = routes[v];
        boolean ok = sch.run(dim, v, r, fixed[d]);
        if (!ok) {
          ok = sch.run(dim, v, r, null);
        }
        if (!ok) {
          continue;
        }
        boolean concrete = dim.hasCost() && (r.length > 2 || p.usedWhenEmpty[v]);
        if (concrete) {
          long[] values = new long[r.length];
          for (int k = 0; k < r.length; k++) {
            values[k] = sch.cumul(k);
          }
          long[] fx = fixed[d].clone();
          for (int k = 0; k < r.length; k++) {
            fx[r[k]] = values[k];
          }
          if (!sch.run(dim, v, r, fx)) {
            sch.run(dim, v, r, fixed[d]);
          }
        }
        for (int k = 0; k < r.length; k++) {
          det.cumulMin[d][r[k]] = sch.min(k);
          det.cumulMax[d][r[k]] = sch.max(k);
        }
        for (int k = 0; k + 1 < r.length; k++) {
          long t = sch.transit(k);
          det.transit[d][r[k]] = t;
          long smin = Math.max(0, sch.min(k + 1) - sch.max(k) - t);
          long smax = Math.min(dim.slackMax, sch.max(k + 1) - sch.min(k) - t);
          det.slackMin[d][r[k]] = Math.min(smin, Math.max(0, smax));
          det.slackMax[d][r[k]] = Math.max(det.slackMin[d][r[k]], smax);
        }
        if (dim.hasBreaks(v)) {
          det.breakStart[d][v] = sch.breakStarts.clone();
        }
      }
    }
    return det;
  }

  private static int indexOf(int[] a, int x) {
    for (int k = 0; k < a.length; k++) {
      if (a[k] == x) {
        return k;
      }
    }
    return -1;
  }
}
