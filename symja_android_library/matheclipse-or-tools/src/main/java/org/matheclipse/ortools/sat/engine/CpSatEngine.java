package org.matheclipse.ortools.sat.engine;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import org.matheclipse.ortools.sat.CpModelProto;
import org.matheclipse.ortools.sat.DecisionStrategyProto;
import org.matheclipse.ortools.sat.SatParameters;

/**
 * Resumable depth-first search of the Java constraint engine which replaces the C++ CP-SAT solver
 * of OR-Tools: constraint propagation over integer domains, binary branching and, for models with
 * an objective, branch and bound with a linear relaxation bound.
 *
 * <p>
 * The search is driven by {@link #nextSolution()}: for a model without objective every call
 * returns the next solution, so that all solutions are enumerated (each exactly once); for a model
 * with an objective each call returns a strictly better solution. When {@link #nextSolution()}
 * returns false, {@link #isExhausted()} tells whether the search space was completely explored
 * (then the last solution is optimal, or the model is infeasible if there was none) or whether
 * the search was stopped (time limit, {@link #stop()} or thread interruption).
 *
 * <p>
 * Usage:
 *
 * <pre>
 * CpSatEngine engine = new CpSatEngine(model.model(), SatParameters.getDefaultInstance());
 * while (engine.nextSolution()) {
 *   long[] values = engine.solution(); // one value per model variable
 * }
 * boolean complete = engine.isExhausted();
 * </pre>
 *
 * The model must be valid, see {@link CpModelValidator#validate(CpModelProto)}.
 */
@SuppressFBWarnings(
    value = {"AT_NONATOMIC_OPERATIONS_ON_SHARED_VARIABLE", "AT_STALE_THREAD_WRITE_OF_PRIMITIVE",
        "AT_NONATOMIC_64BIT_PRIMITIVE"},
    justification = "search state is confined to the solving thread; only stopRequested is shared")
public final class CpSatEngine {
  private static final int OP_EQ = 0;
  private static final int OP_LE = 1;
  private static final int OP_GE = 2;
  /** Domains with more values are split in halves by the default heuristic. */
  private static final long SPLIT_THRESHOLD = 64;

  private final ModelLoader loader;
  private final DomainStore s;
  private final SatParameters params;
  private final long startNanos;
  private final long deadlineNanos;
  private volatile boolean stopRequested;
  private final Random random;
  private final LpRelaxation lp;

  // decision stack
  private int[] decVar = new int[64];
  private int[] decOp = new int[64];
  private long[] decVal = new long[64];
  private boolean[] decRefuted = new boolean[64];
  private int[] levelPos = new int[64];
  private int depth;
  private int baseDepth;

  private static final int INIT = 0;
  private static final int SEARCHING = 1;
  private static final int AFTER_SOLUTION = 2;
  private static final int DONE = 3;
  private int phase = INIT;
  private boolean exhausted;
  private boolean assumptionsFailed;
  private boolean stopped;

  private long[] solution;
  private long numSolutions;
  private long numBranches;
  private long numConflicts;
  private double innerLowerBound = Double.NEGATIVE_INFINITY;
  private long bestInner = Long.MAX_VALUE;

  /**
   * Prepares the search. The model must be valid; parameters used: max_time_in_seconds,
   * random_seed, linearization_level (0 disables the linear relaxation), symmetry_level.
   *
   * <p>
   * The other parameters are accepted and ignored. In particular every search_branching value
   * (including ROUND_ROBIN_SHAVING_SEARCH and ALT_FIXED_SEARCH of OR-Tools 10.0) gives the same
   * complete search, which branches first on the decision strategies of the model (like
   * FIXED_SEARCH) and then with the default heuristic on the remaining variables.
   */
  public CpSatEngine(CpModelProto model, SatParameters params) {
    this.startNanos = System.nanoTime();
    this.params = params;
    double maxTime = params.getMaxTimeInSeconds();
    this.deadlineNanos = Double.isInfinite(maxTime) || maxTime > 1e9 ? Long.MAX_VALUE
        : startNanos + (long) (Math.max(0, maxTime) * 1e9);
    this.random = new Random(params.getRandomSeed());
    boolean breakSymmetries = params.getSymmetryLevel() > 0
        && (model.hasObjective() || model.hasFloatingPointObjective());
    this.loader = new ModelLoader(model, breakSymmetries);
    this.s = loader.s;
    s.stopCheck = this::shouldStop;
    boolean useLp = loader.objective != null && params.getLinearizationLevel() > 0
        && !loader.rows.isEmpty();
    this.lp = useLp ? new LpRelaxation(loader, s) : null;
  }

  /** Requests the search to stop; {@link #nextSolution()} then returns false. Thread safe. */
  public void stop() {
    stopRequested = true;
  }

  private boolean shouldStop() {
    if (stopRequested) {
      return true;
    }
    if (deadlineNanos != Long.MAX_VALUE && System.nanoTime() > deadlineNanos) {
      return true;
    }
    return Thread.currentThread().isInterrupted();
  }

  /** True if the model has an objective. */
  public boolean hasObjective() {
    return loader.objective != null;
  }

  /** The values of the model variables in the last solution, or null. */
  public long[] solution() {
    return solution;
  }

  /** Number of solutions returned so far. */
  public long numSolutions() {
    return numSolutions;
  }

  /** True when the search space was completely explored. */
  public boolean isExhausted() {
    return exhausted;
  }

  /** True if the model is infeasible because of its assumptions (after exhaustion). */
  public boolean assumptionsFailed() {
    return assumptionsFailed;
  }

  /** True if the search was stopped before it was exhausted. */
  public boolean isStopped() {
    return stopped;
  }

  /** Number of decisions taken so far. */
  public long numBranches() {
    return numBranches;
  }

  /** Number of failures (dead ends) so far. */
  public long numConflicts() {
    return numConflicts;
  }

  /** Number of propagator calls so far. */
  public long numPropagations() {
    return s.numPropagations;
  }

  /** Number of engine variables: the model variables plus the auxiliary ones. */
  public int numEngineVariables() {
    return s.numVars;
  }

  /** Number of lexicographic constraints added to break the symmetry of identical columns. */
  public int numSymmetryBreakingConstraints() {
    return loader.numSymmetryConstraints;
  }

  /** Number of propagators of the loaded model. */
  public int numPropagators() {
    return s.propagators.size();
  }

  /** Seconds since the creation of the engine. */
  public double wallTime() {
    return (System.nanoTime() - startNanos) * 1e-9;
  }

  /** The displayed objective value of the last solution (0 if none). */
  public double objectiveValue() {
    if (solution == null || loader.objective == null) {
      return 0;
    }
    return loader.displayedObjective(solution);
  }

  /**
   * The best proven bound of the displayed objective: a lower bound when minimizing, an upper
   * bound when maximizing. Equal to the objective value once optimality is proven.
   */
  public double bestObjectiveBound() {
    if (loader.objective == null) {
      return 0;
    }
    if (exhausted && solution != null) {
      return objectiveValue();
    }
    double lb = innerLowerBound;
    if (Double.isInfinite(lb)) {
      return loader.objScale > 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }
    return loader.displayedBound(lb);
  }

  /** The inner (integer, minimized) objective of the last solution. */
  public long innerObjectiveValue() {
    return bestInner;
  }

  /**
   * Searches the next solution. Returns false if there is none (search exhausted) or the search
   * was stopped.
   */
  public boolean nextSolution() {
    if (phase == DONE) {
      return false;
    }
    try {
      boolean r = search();
      if (stopped) {
        phase = DONE;
      }
      return r;
    } catch (DomainStore.StopException e) {
      stopped = true;
      phase = DONE;
      return false;
    }
  }

  private boolean search() {
    if (phase == INIT) {
      if (!loader.feasible || !s.propagate()) {
        finish();
        return false;
      }
      s.isRootLevel = false;
      updateRootBound();
      if (lp != null && !lpBound()) {
        finish();
        return false;
      }
      for (int lit : loader.assumptions) {
        pushLevel();
        decVar[depth - 1] = -1;
        if (!s.setLit(lit, true) || !s.propagate()) {
          assumptionsFailed = true;
          finish();
          return false;
        }
      }
      baseDepth = depth;
      phase = SEARCHING;
    } else if (phase == AFTER_SOLUTION) {
      if (loader.objective != null) {
        loader.objective.tightenUpperBound(bestInner - 1);
      }
      if (!backtrack()) {
        finish();
        return false;
      }
      phase = SEARCHING;
    }
    if (loader.objective != null) {
      s.enqueue(loader.objective);
    }
    long nodes = 0;
    while (true) {
      if ((++nodes & 127) == 0 && shouldStop()) {
        stopped = true;
        return false;
      }
      boolean ok = !pendingFailure && s.propagate();
      pendingFailure = false;
      if (ok && lp != null && depth > baseDepth) {
        ok = lpBound();
      }
      if (!ok) {
        numConflicts++;
        if (!backtrack()) {
          finish();
          return false;
        }
        continue;
      }
      if (!decide()) {
        // all variables are fixed
        long[] values = new long[loader.numModelVars];
        for (int v = 0; v < values.length; v++) {
          values[v] = s.value(v);
        }
        if (SolutionChecker.isFeasible(loader.model, values)
            && (loader.objective == null || loader.innerObjective(values) < bestInner)) {
          solution = values;
          numSolutions++;
          if (loader.objective != null) {
            bestInner = loader.innerObjective(values);
          }
          phase = AFTER_SOLUTION;
          return true;
        }
        numConflicts++;
        if (!backtrack()) {
          finish();
          return false;
        }
      }
    }
  }

  private void finish() {
    phase = DONE;
    exhausted = true;
    s.resetQueue();
  }

  private void updateRootBound() {
    if (loader.objective != null) {
      innerLowerBound = Math.max(innerLowerBound, loader.objective.minActivity(s));
    }
  }

  // linear relaxation bound at the current node; false if the node can be pruned
  private boolean lpBound() {
    long upper = loader.objective.dynamicUpperBound();
    int r = lp.solveAndPrune(upper, depth == 0 && phase == INIT);
    if (depth == 0 && lp.lastBound() > innerLowerBound) {
      innerLowerBound = Math.max(innerLowerBound, Math.ceil(lp.lastBound() - 1e-9));
    }
    if (r < 0) {
      return false;
    }
    return true;
  }

  private void pushLevel() {
    if (depth == decVar.length) {
      int n = depth * 2;
      decVar = java.util.Arrays.copyOf(decVar, n);
      decOp = java.util.Arrays.copyOf(decOp, n);
      decVal = java.util.Arrays.copyOf(decVal, n);
      decRefuted = java.util.Arrays.copyOf(decRefuted, n);
      levelPos = java.util.Arrays.copyOf(levelPos, n);
    }
    levelPos[depth] = s.trailPosition();
    decRefuted[depth] = false;
    depth++;
    s.newStamp();
  }

  private boolean apply(int v, int op, long val, boolean negate) {
    if (!negate) {
      switch (op) {
        case OP_EQ:
          return s.fix(v, val);
        case OP_LE:
          return s.setUb(v, val);
        default:
          return s.setLb(v, val);
      }
    }
    switch (op) {
      case OP_EQ:
        return s.remove(v, val) && !s.contains(v, val);
      case OP_LE:
        return s.setLb(v, val + 1);
      default:
        return s.setUb(v, val - 1);
    }
  }

  /** Undoes the last decision and applies its refutation; false if the search is exhausted. */
  private boolean backtrack() {
    while (depth > baseDepth) {
      int top = depth - 1;
      s.resetQueue();
      s.restore(levelPos[top]);
      if (!decRefuted[top]) {
        decRefuted[top] = true;
        if (apply(decVar[top], decOp[top], decVal[top], true)) {
          return true;
        }
        numConflicts++;
        continue;
      }
      depth--;
    }
    return false;
  }

  // ---------------------------------------------------------------------------------- heuristics

  /** Makes the next decision; returns false if all variables are fixed. */
  private boolean decide() {
    // 1. decision strategies of the model
    for (ModelLoader.Strategy st : loader.strategies) {
      Affine chosen = null;
      long best = 0;
      for (Affine e : st.exprs) {
        if (e.isFixed(s)) {
          continue;
        }
        long score;
        switch (st.varSel) {
          case CHOOSE_LOWEST_MIN:
            score = e.min(s);
            break;
          case CHOOSE_HIGHEST_MAX:
            score = -e.max(s);
            break;
          case CHOOSE_MIN_DOMAIN_SIZE:
            score = e.size(s);
            break;
          case CHOOSE_MAX_DOMAIN_SIZE:
            score = -e.size(s);
            break;
          default:
            score = 0;
            break;
        }
        if (chosen == null || score < best) {
          chosen = e;
          best = score;
        }
        if (st.varSel == DecisionStrategyProto.VariableSelectionStrategy.CHOOSE_FIRST) {
          break;
        }
      }
      if (chosen != null) {
        decideOnExpr(chosen, st.domRed);
        return true;
      }
    }
    // 2. solution hints
    for (int k = 0; k < loader.hintVars.length; k++) {
      int v = loader.hintVars[k];
      long value = loader.hintValues[k];
      if (!s.isFixed(v) && s.contains(v, value)) {
        decideValue(v, value);
        return true;
      }
    }
    // 3. dom/wdeg over the model variables, then the auxiliary variables
    int v = chooseVariable(0, loader.numModelVars);
    if (v < 0) {
      v = chooseVariable(loader.numModelVars, s.numVars);
    }
    if (v < 0) {
      return false;
    }
    long c = v < loader.objCoefOfVar.length ? loader.objCoefOfVar[v] : 0;
    if (s.size(v) > SPLIT_THRESHOLD) {
      // large domains are split in halves, the preferred half first
      long mid = s.lb[v] + (s.ub[v] - s.lb[v]) / 2;
      if (c < 0) {
        branch(v, OP_GE, mid + 1);
      } else {
        branch(v, OP_LE, mid);
      }
      return true;
    }
    decideValue(v, c < 0 ? s.ub[v] : s.lb[v]);
    return true;
  }

  private int chooseVariable(int from, int to) {
    int best = -1;
    double bestScore = Double.MAX_VALUE;
    for (int v = from; v < to; v++) {
      if (s.lb[v] == s.ub[v]) {
        continue;
      }
      double score = (double) s.size(v) / (1 + s.varWeight[v]);
      if (score < bestScore) {
        bestScore = score;
        best = v;
      }
    }
    return best;
  }

  private void decideValue(int v, long value) {
    if (value <= s.lb[v]) {
      value = s.lb[v];
    } else if (value >= s.ub[v]) {
      value = s.ub[v];
    }
    if (value == s.lb[v] || value == s.ub[v] || s.hasBitset(v)) {
      branch(v, OP_EQ, value);
    } else {
      // interior value of a large domain: split at the value
      branch(v, OP_GE, value);
    }
  }

  private void decideOnExpr(Affine e, DecisionStrategyProto.DomainReductionStrategy dr) {
    int v = e.var;
    boolean positive = e.coeff > 0;
    long lb = s.lb[v];
    long ub = s.ub[v];
    long mid = lb + (ub - lb) / 2;
    switch (dr) {
      case SELECT_MAX_VALUE:
        branch(v, OP_EQ, positive ? ub : lb);
        break;
      case SELECT_LOWER_HALF:
        if (positive) {
          branch(v, OP_LE, mid);
        } else {
          branch(v, OP_GE, mid + 1 > ub ? ub : mid + 1);
        }
        break;
      case SELECT_UPPER_HALF:
        if (positive) {
          branch(v, OP_GE, mid + 1 > ub ? ub : mid + 1);
        } else {
          branch(v, OP_LE, mid);
        }
        break;
      case SELECT_MEDIAN_VALUE: {
        long size = s.size(v);
        long k = (size - 1) / 2;
        long x = lb;
        while (k > 0) {
          long next = s.nextValueAtOrAfter(v, x + 1);
          if (next == Long.MAX_VALUE) {
            break;
          }
          x = next;
          k--;
        }
        decideValue(v, x);
        break;
      }
      case SELECT_RANDOM_HALF:
        if (random.nextBoolean()) {
          branch(v, OP_LE, mid);
        } else {
          branch(v, OP_GE, mid + 1 > ub ? ub : mid + 1);
        }
        break;
      default:
        // SELECT_MIN_VALUE
        branch(v, OP_EQ, positive ? lb : ub);
        break;
    }
  }

  private void branch(int v, int op, long value) {
    numBranches++;
    pushLevel();
    int top = depth - 1;
    decVar[top] = v;
    decOp[top] = op;
    decVal[top] = value;
    long lb0 = s.lb[v];
    long ub0 = s.ub[v];
    if (!apply(v, op, value, false)) {
      // immediate failure: seen by the next loop iteration
      pendingFailure = true;
    } else if (s.lb[v] == lb0 && s.ub[v] == ub0 && s.size(v) > 1 && op != OP_EQ) {
      // defensive: a decision must split the domain, otherwise the search would not progress
      decOp[top] = OP_EQ;
      decVal[top] = lb0;
      if (!s.fix(v, lb0)) {
        pendingFailure = true;
      }
    }
  }

  private boolean pendingFailure;
}
