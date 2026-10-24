package org.matheclipse.ortools.constraintsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.matheclipse.ortools.constraintsolver.cp.CapOps;
import org.matheclipse.ortools.constraintsolver.cp.CpFail;
import org.matheclipse.ortools.constraintsolver.cp.CpStore;
import org.matheclipse.ortools.constraintsolver.cp.CpVar;
import org.matheclipse.ortools.constraintsolver.cp.LinearPropagator;
import org.matheclipse.ortools.constraintsolver.cp.Propagator;
import org.matheclipse.ortools.constraintsolver.cp.Propagators;
import org.matheclipse.ortools.constraintsolver.cp.Propagators.Relation;
import org.matheclipse.ortools.constraintsolver.cp.SolverAccess;

/**
 * The original OR-Tools constraint programming solver, mirroring
 * <code>com.google.ortools.constraintsolver.Solver</code> of the OR-Tools Java wrapper.
 *
 * <p>
 * This class reimplements a subset of the API on a much simpler engine: variables with interval
 * and hole domains, bounds propagation for arithmetic constraints, value elimination plus Hall
 * intervals for <code>AllDifferent</code>, time tables for <code>Cumulative</code>, and a
 * depth first search driven by {@link DecisionBuilder}s and {@link SearchMonitor}s. Expressions
 * are backed by auxiliary variables whose defining constraints are posted when the expression is
 * created. Local search (operators, filters, LNS), sequence variables and custom C++ constraints
 * are not available.
 */
public class Solver {

  /** Signals a failure of the search, as thrown by {@link #fail()}. */
  public static class FailException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates the exception. */
    public FailException() {
      super();
    }

    /** Creates the exception with a message. */
    public FailException(String message) {
      super(message);
    }
  }

  /** The cheap failure thrown by the engine; it carries no stack trace. */
  private static final class StacklessFail extends FailException {
    private static final long serialVersionUID = 1L;

    StacklessFail() {
      super("failure");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
      return this;
    }
  }

  private static final FailException FAIL = new StacklessFail();

  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

  static {
    SolverAccess.install(new SolverAccessImpl());
  }

  // ----- constants of the C++ enums -----

  /** Number of demon priorities. */
  public static final int kNumPriorities = 3;

  /** Default variable selection (first unbound variable). */
  public static final int INT_VAR_DEFAULT = 0;

  /** Simple variable selection (first unbound variable). */
  public static final int INT_VAR_SIMPLE = 1;

  /** Selects the first unbound variable. */
  public static final int CHOOSE_FIRST_UNBOUND = 2;

  /** Selects a random unbound variable. */
  public static final int CHOOSE_RANDOM = 3;

  /** Smallest domain, ties broken by the lowest minimum. */
  public static final int CHOOSE_MIN_SIZE_LOWEST_MIN = 4;

  /** Smallest domain, ties broken by the highest minimum. */
  public static final int CHOOSE_MIN_SIZE_HIGHEST_MIN = 5;

  /** Smallest domain, ties broken by the lowest maximum. */
  public static final int CHOOSE_MIN_SIZE_LOWEST_MAX = 6;

  /** Smallest domain, ties broken by the highest maximum. */
  public static final int CHOOSE_MIN_SIZE_HIGHEST_MAX = 7;

  /** Lowest minimum. */
  public static final int CHOOSE_LOWEST_MIN = 8;

  /** Highest maximum. */
  public static final int CHOOSE_HIGHEST_MAX = 9;

  /** Smallest domain. */
  public static final int CHOOSE_MIN_SIZE = 10;

  /** Largest domain. */
  public static final int CHOOSE_MAX_SIZE = 11;

  /** Largest difference between the two smallest values. */
  public static final int CHOOSE_MAX_REGRET_ON_MIN = 12;

  /** Path following selection (first unbound variable in this implementation). */
  public static final int CHOOSE_PATH = 13;

  /** Default value selection (minimum). */
  public static final int INT_VALUE_DEFAULT = 0;

  /** Simple value selection (minimum). */
  public static final int INT_VALUE_SIMPLE = 1;

  /** Assigns the minimum. */
  public static final int ASSIGN_MIN_VALUE = 2;

  /** Assigns the maximum. */
  public static final int ASSIGN_MAX_VALUE = 3;

  /** Assigns a random value. */
  public static final int ASSIGN_RANDOM_VALUE = 4;

  /** Assigns the value closest to the middle of the domain. */
  public static final int ASSIGN_CENTER_VALUE = 5;

  /** Splits the domain and explores the lower half first. */
  public static final int SPLIT_LOWER_HALF = 6;

  /** Splits the domain and explores the upper half first. */
  public static final int SPLIT_UPPER_HALF = 7;

  /** Evaluator strategy: static global best. */
  public static final int CHOOSE_STATIC_GLOBAL_BEST = 0;

  /** Evaluator strategy: dynamic global best. */
  public static final int CHOOSE_DYNAMIC_GLOBAL_BEST = 1;

  /** Sequence strategy constants (sequences are not supported). */
  public static final int SEQUENCE_DEFAULT = 0;

  /** Sequence strategy constant. */
  public static final int SEQUENCE_SIMPLE = 1;

  /** Sequence strategy constant. */
  public static final int CHOOSE_MIN_SLACK_RANK_FORWARD = 2;

  /** Sequence strategy constant. */
  public static final int CHOOSE_RANDOM_RANK_FORWARD = 3;

  /** Interval strategy constant. */
  public static final int INTERVAL_DEFAULT = 0;

  /** Interval strategy constant. */
  public static final int INTERVAL_SIMPLE = 1;

  /** Interval strategy constant. */
  public static final int INTERVAL_SET_TIMES_FORWARD = 2;

  /** Interval strategy constant. */
  public static final int INTERVAL_SET_TIMES_BACKWARD = 3;

  /** Local search operator constant (local search is not supported). */
  public static final int TWOOPT = 0;

  /** Local search operator constant. */
  public static final int OROPT = 1;

  /** Local search operator constant. */
  public static final int RELOCATE = 2;

  /** Local search operator constant. */
  public static final int EXCHANGE = 3;

  /** Local search operator constant. */
  public static final int CROSS = 4;

  /** Local search operator constant. */
  public static final int MAKEACTIVE = 5;

  /** Local search operator constant. */
  public static final int MAKEINACTIVE = 6;

  /** Local search operator constant. */
  public static final int MAKECHAININACTIVE = 7;

  /** Local search operator constant. */
  public static final int SWAPACTIVE = 8;

  /** Local search operator constant. */
  public static final int SWAPACTIVECHAIN = 9;

  /** Local search operator constant. */
  public static final int EXTENDEDSWAPACTIVE = 10;

  /** Local search operator constant. */
  public static final int PATHLNS = 11;

  /** Local search operator constant. */
  public static final int FULLPATHLNS = 12;

  /** Local search operator constant. */
  public static final int UNACTIVELNS = 13;

  /** Local search operator constant. */
  public static final int INCREMENT = 14;

  /** Local search operator constant. */
  public static final int DECREMENT = 15;

  /** Local search operator constant. */
  public static final int SIMPLELNS = 16;

  /** Evaluator local search operator constant. */
  public static final int LK = 0;

  /** Evaluator local search operator constant. */
  public static final int TSPOPT = 1;

  /** Evaluator local search operator constant. */
  public static final int TSPLNS = 2;

  /** Filter bound constant. */
  public static final int GE = 0;

  /** Filter bound constant. */
  public static final int LE = 1;

  /** Filter bound constant. */
  public static final int EQ = 2;

  /** Demon priority constant. */
  public static final int DELAYED_PRIORITY = 0;

  /** Demon priority constant. */
  public static final int VAR_PRIORITY = 1;

  /** Demon priority constant. */
  public static final int NORMAL_PRIORITY = 2;

  /** Binary interval relation: t1 ends after t2 ends. */
  public static final int ENDS_AFTER_END = 0;

  /** Binary interval relation: t1 ends after t2 starts. */
  public static final int ENDS_AFTER_START = 1;

  /** Binary interval relation: t1 ends when t2 ends. */
  public static final int ENDS_AT_END = 2;

  /** Binary interval relation: t1 ends when t2 starts. */
  public static final int ENDS_AT_START = 3;

  /** Binary interval relation: t1 starts after t2 ends. */
  public static final int STARTS_AFTER_END = 4;

  /** Binary interval relation: t1 starts after t2 starts. */
  public static final int STARTS_AFTER_START = 5;

  /** Binary interval relation: t1 starts when t2 ends. */
  public static final int STARTS_AT_END = 6;

  /** Binary interval relation: t1 starts when t2 starts. */
  public static final int STARTS_AT_START = 7;

  /** Binary interval relation: same start and end. */
  public static final int STAYS_IN_SYNC = 8;

  /** Unary interval relation: ends after d. */
  public static final int ENDS_AFTER = 0;

  /** Unary interval relation: ends at d. */
  public static final int ENDS_AT = 1;

  /** Unary interval relation: ends before d. */
  public static final int ENDS_BEFORE = 2;

  /** Unary interval relation: starts after d. */
  public static final int STARTS_AFTER = 3;

  /** Unary interval relation: starts at d. */
  public static final int STARTS_AT = 4;

  /** Unary interval relation: starts before d. */
  public static final int STARTS_BEFORE = 5;

  /** Unary interval relation: crosses d. */
  public static final int CROSS_DATE = 6;

  /** Unary interval relation: avoids d. */
  public static final int AVOID_DATE = 7;

  /** Decision modification constant. */
  public static final int NO_CHANGE = 0;

  /** Decision modification constant. */
  public static final int KEEP_LEFT = 1;

  /** Decision modification constant. */
  public static final int KEEP_RIGHT = 2;

  /** Decision modification constant. */
  public static final int KILL_BOTH = 3;

  /** Decision modification constant. */
  public static final int SWITCH_BRANCHES = 4;

  /** Marker type constant. */
  public static final int SENTINEL = 0;

  /** Marker type constant. */
  public static final int SIMPLE_MARKER = 1;

  /** Marker type constant. */
  public static final int CHOICE_POINT = 2;

  /** Marker type constant. */
  public static final int REVERSIBLE_ACTION = 3;

  /** Solver state: no search running. */
  public static final int OUTSIDE_SEARCH = 0;

  /** Solver state: at the root of a search. */
  public static final int IN_ROOT_NODE = 1;

  /** Solver state: searching. */
  public static final int IN_SEARCH = 2;

  /** Solver state: at a solution. */
  public static final int AT_SOLUTION = 3;

  /** Solver state: the search tree has been explored. */
  public static final int NO_MORE_SOLUTIONS = 4;

  /** Solver state: the problem is infeasible. */
  public static final int PROBLEM_INFEASIBLE = 5;

  /** Optimization direction constant. */
  public static final int NOT_SET = 0;

  /** Optimization direction constant. */
  public static final int MAXIMIZATION = 1;

  /** Optimization direction constant. */
  public static final int MINIMIZATION = 2;

  // ----- state -----

  private final String name;

  private final ConstraintSolverParameters parameters;

  final CpStore store = new CpStore();

  private final long startNanos = System.nanoTime();

  private final Random random = new Random(0);

  private int constraintCount;

  private long branches;

  private long failures;

  private long solutions;

  private boolean rootFailed;

  private boolean finishRequested;

  private Search search;

  private int state = OUTSIDE_SEARCH;

  /** Creates a solver with the default parameters. */
  public Solver(String name) {
    this(name, defaultSolverParameters());
  }

  /** Creates a solver with the given parameters. */
  public Solver(String name, ConstraintSolverParameters parameters) {
    this.name = name;
    this.parameters = parameters;
  }

  /** The default parameters of a solver. */
  public static ConstraintSolverParameters defaultSolverParameters() {
    return ConstraintSolverParameters.newBuilder()
        .setCompressTrail(ConstraintSolverParameters.TrailCompression.NO_COMPRESSION)
        .setTrailBlockSize(8000).setArraySplitSize(16).setStoreNames(true)
        .setCheckSolutionPeriod(1).build();
  }

  /** The parameters of this solver. */
  public ConstraintSolverParameters parameters() {
    return parameters;
  }

  /** The name given at construction. */
  public String model_name() {
    return name;
  }

  /** The engine store; used by the routing layer. */
  CpStore store() {
    return store;
  }

  @Override
  public String toString() {
    return "Solver(name = \"" + name + "\", state = " + stateName() + ", branches = " + branches
        + ", fails = " + failures + ", decisions = " + branches + ", propagation loops = "
        + store.propagations + ", demons Run = " + store.propagations + ", Run time = "
        + wallTime() + " ms)";
  }

  private String stateName() {
    switch (state) {
      case IN_ROOT_NODE:
        return "IN_ROOT_NODE";
      case IN_SEARCH:
        return "IN_SEARCH";
      case AT_SOLUTION:
        return "AT_SOLUTION";
      case NO_MORE_SOLUTIONS:
        return "NO_MORE_SOLUTIONS";
      case PROBLEM_INFEASIBLE:
        return "PROBLEM_INFEASIBLE";
      default:
        return "OUTSIDE_SEARCH";
    }
  }

  // ----- failures -----

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
    throw (T) t;
  }

  /** Abandons the current branch of the search; a backtrack follows. */
  public void fail() {
    if (insideSearch()) {
      Solver.<RuntimeException>sneakyThrow(FAIL);
    }
    rootFailed = true;
  }

  /** Called by the API objects when a domain became empty. */
  void domainFailure() {
    if (insideSearch()) {
      Solver.<RuntimeException>sneakyThrow(FAIL);
    }
    rootFailed = true;
  }

  /** Whether a search (or an engine check of the routing layer) is running. */
  boolean insideSearch() {
    return store.level() > 0;
  }

  /** Whether a domain wipe-out happened outside of any search (the model is infeasible). */
  boolean isRootFailed() {
    return rootFailed;
  }

  // ----- statistics -----

  /** The memory used by the JVM in bytes (for the search log). */
  static long usedMemory() {
    Runtime rt = Runtime.getRuntime();
    return rt.totalMemory() - rt.freeMemory();
  }

  /** The milliseconds since the creation of the solver. */
  public long wallTime() {
    return (System.nanoTime() - startNanos) / 1_000_000L;
  }

  /** The number of branches (applied decisions). */
  public long branches() {
    return branches;
  }

  /** The number of solutions found. */
  public long solutions() {
    return solutions;
  }

  /** The number of solutions found (no unchecked solutions exist in this implementation). */
  public long unchecked_solutions() {
    return solutions;
  }

  /** The number of failures. */
  public long failures() {
    return failures;
  }

  /** The number of accepted neighbors (0: no local search). */
  public long acceptedNeighbors() {
    return 0;
  }

  /** A counter which changes on every choice. */
  public long stamp() {
    return branches + failures;
  }

  /** A counter which changes on every failure. */
  public long fail_stamp() {
    return failures;
  }

  /** The number of constraints added to the model. */
  public int constraints() {
    return constraintCount;
  }

  /** The depth of the current search node. */
  public int searchDepth() {
    return search == null ? 0 : search.stack.size();
  }

  /** The state of the solver (one of the state constants). */
  public int state() {
    return state;
  }

  // ----- variables -----

  IntVar wrap(CpVar v) {
    return new IntVar(this, v);
  }

  private IntVar newVar(long min, long max, String varName) {
    return wrap(store.newVar(min, max, varName == null || varName.isEmpty() ? null : varName));
  }

  /** A variable with domain <code>[min, max]</code>. */
  public IntVar makeIntVar(long min, long max, String varName) {
    return newVar(min, max, varName);
  }

  /** A variable with the given values. */
  public IntVar makeIntVar(long[] values, String varName) {
    return wrap(store.newVar(values, varName == null || varName.isEmpty() ? null : varName));
  }

  /** A variable with the given values. */
  public IntVar makeIntVar(int[] values, String varName) {
    long[] v = new long[values.length];
    for (int i = 0; i < values.length; i++) {
      v[i] = values[i];
    }
    return makeIntVar(v, varName);
  }

  /** An unnamed variable with domain <code>[min, max]</code>. */
  public IntVar makeIntVar(long min, long max) {
    return newVar(min, max, null);
  }

  /** An unnamed variable with the given values. */
  public IntVar makeIntVar(long[] values) {
    return makeIntVar(values, null);
  }

  /** An unnamed variable with the given values. */
  public IntVar makeIntVar(int[] values) {
    return makeIntVar(values, null);
  }

  /** A 0/1 variable. */
  public IntVar makeBoolVar(String varName) {
    return newVar(0, 1, varName);
  }

  /** An unnamed 0/1 variable. */
  public IntVar makeBoolVar() {
    return newVar(0, 1, null);
  }

  /** A constant. */
  public IntVar makeIntConst(long val, String varName) {
    return newVar(val, val, varName);
  }

  /** An unnamed constant. */
  public IntVar makeIntConst(long val) {
    return newVar(val, val, null);
  }

  /** <code>count</code> variables with domain <code>[min, max]</code>. */
  public IntVar[] makeIntVarArray(int count, long min, long max) {
    IntVar[] array = new IntVar[count];
    for (int i = 0; i < count; ++i) {
      array[i] = makeIntVar(min, max);
    }
    return array;
  }

  /** <code>count</code> variables named <code>name0, name1, ...</code>. */
  public IntVar[] makeIntVarArray(int count, long min, long max, String varName) {
    IntVar[] array = new IntVar[count];
    for (int i = 0; i < count; ++i) {
      array[i] = makeIntVar(min, max, varName + i);
    }
    return array;
  }

  /** <code>count</code> 0/1 variables. */
  public IntVar[] makeBoolVarArray(int count) {
    IntVar[] array = new IntVar[count];
    for (int i = 0; i < count; ++i) {
      array[i] = makeBoolVar();
    }
    return array;
  }

  /** <code>count</code> 0/1 variables named <code>name0, name1, ...</code>. */
  public IntVar[] makeBoolVarArray(int count, String varName) {
    IntVar[] array = new IntVar[count];
    for (int i = 0; i < count; ++i) {
      array[i] = makeBoolVar(varName + i);
    }
    return array;
  }

  // ----- expressions -----

  static CpVar[] engine(IntExpr[] exprs) {
    CpVar[] r = new CpVar[exprs.length];
    for (int i = 0; i < exprs.length; i++) {
      r[i] = exprs[i].cpVar;
    }
    return r;
  }

  private void postInternal(Propagator p) {
    store.post(p);
    if (insideSearch()) {
      try {
        store.propagate();
      } catch (CpFail f) {
        domainFailure();
      }
    } else {
      store.clearQueue();
    }
  }

  private IntVar linearExpr(long[] coefs, CpVar[] vars, long offset) {
    long[] b = LinearPropagator.bounds(coefs, vars, offset);
    IntVar z = newVar(b[0], b[1], null);
    long[] c = Arrays.copyOf(coefs, coefs.length + 1);
    c[coefs.length] = -1;
    CpVar[] v = Arrays.copyOf(vars, vars.length + 1);
    v[vars.length] = z.cpVar;
    postInternal(new LinearPropagator(c, v, CapOps.capOpp(offset), CapOps.capOpp(offset)));
    return z;
  }

  /** <code>left + right</code>. */
  public IntExpr makeSum(IntExpr left, IntExpr right) {
    return linearExpr(new long[] {1, 1}, new CpVar[] {left.cpVar, right.cpVar}, 0);
  }

  /** <code>expr + value</code>. */
  public IntExpr makeSum(IntExpr expr, long value) {
    if (value == 0) {
      return expr;
    }
    return linearExpr(new long[] {1}, new CpVar[] {expr.cpVar}, value);
  }

  /** The sum of <code>vars</code>. */
  public IntExpr makeSum(IntVar[] vars) {
    long[] c = new long[vars.length];
    Arrays.fill(c, 1);
    return linearExpr(c, engine(vars), 0);
  }

  /** The scalar product of <code>vars</code> and <code>coefs</code>. */
  public IntExpr makeScalProd(IntVar[] vars, long[] coefs) {
    return linearExpr(coefs.clone(), engine(vars), 0);
  }

  /** The scalar product of <code>vars</code> and <code>coefs</code>. */
  public IntExpr makeScalProd(IntVar[] vars, int[] coefs) {
    return makeScalProd(vars, toLong(coefs));
  }

  /** <code>left - right</code>. */
  public IntExpr makeDifference(IntExpr left, IntExpr right) {
    return linearExpr(new long[] {1, -1}, new CpVar[] {left.cpVar, right.cpVar}, 0);
  }

  /** <code>value - expr</code>. */
  public IntExpr makeDifference(long value, IntExpr expr) {
    return linearExpr(new long[] {-1}, new CpVar[] {expr.cpVar}, value);
  }

  /** <code>-expr</code>. */
  public IntExpr makeOpposite(IntExpr expr) {
    return linearExpr(new long[] {-1}, new CpVar[] {expr.cpVar}, 0);
  }

  /** <code>left * right</code>. */
  public IntExpr makeProd(IntExpr left, IntExpr right) {
    long[] b = Propagators.Product.bounds(left.cpVar, right.cpVar);
    IntVar z = newVar(b[0], b[1], null);
    postInternal(new Propagators.Product(left.cpVar, right.cpVar, z.cpVar));
    return z;
  }

  /** <code>expr * value</code>. */
  public IntExpr makeProd(IntExpr expr, long value) {
    if (value == 1) {
      return expr;
    }
    return linearExpr(new long[] {value}, new CpVar[] {expr.cpVar}, 0);
  }

  /** <code>expr / value</code> rounded towards zero. */
  public IntExpr makeDiv(IntExpr expr, long value) {
    return makeDiv(expr, makeIntConst(value));
  }

  /** <code>numerator / denominator</code> rounded towards zero. */
  public IntExpr makeDiv(IntExpr numerator, IntExpr denominator) {
    long[] b = Propagators.Div.bounds(numerator.cpVar, denominator.cpVar);
    IntVar z = newVar(b[0], b[1], null);
    postInternal(new Propagators.Div(numerator.cpVar, denominator.cpVar, z.cpVar));
    return z;
  }

  /** <code>|expr|</code>. */
  public IntExpr makeAbs(IntExpr expr) {
    long[] b = Propagators.Abs.bounds(expr.cpVar);
    IntVar z = newVar(b[0], b[1], null);
    postInternal(new Propagators.Abs(expr.cpVar, z.cpVar));
    return z;
  }

  /** <code>expr * expr</code>. */
  public IntExpr makeSquare(IntExpr expr) {
    return makeProd(expr, expr);
  }

  /** <code>expr ^ n</code> for <code>n &gt;= 0</code>. */
  public IntExpr makePower(IntExpr expr, long n) {
    if (n < 0) {
      throw new IllegalArgumentException("negative power");
    }
    if (n == 0) {
      return makeIntConst(1);
    }
    IntExpr r = expr;
    for (long i = 1; i < n; i++) {
      r = makeProd(r, expr);
    }
    return r;
  }

  /** <code>values[index]</code>. */
  public IntExpr makeElement(long[] values, IntVar index) {
    long lo = Long.MAX_VALUE;
    long hi = Long.MIN_VALUE;
    for (long v : values) {
      lo = Math.min(lo, v);
      hi = Math.max(hi, v);
    }
    if (values.length == 0) {
      throw new IllegalArgumentException("empty element table");
    }
    IntVar z = newVar(lo, hi, null);
    postInternal(new Propagators.Element(values, index.cpVar, z.cpVar));
    return z;
  }

  /** <code>values[index]</code>. */
  public IntExpr makeElement(int[] values, IntVar index) {
    return makeElement(toLong(values), index);
  }

  /** <code>values(index)</code> for a function evaluated on the domain of index. */
  public IntExpr makeElement(LongUnaryOperator values, IntVar index) {
    long[] b = Propagators.ElementFunction.bounds(values, index.cpVar);
    IntVar z = newVar(b[0], b[1], null);
    postInternal(new Propagators.ElementFunction(values, index.cpVar, z.cpVar));
    return z;
  }

  /** <code>values(index1, index2)</code>. */
  public IntExpr makeElement(LongBinaryOperator values, IntVar index1, IntVar index2) {
    long[] b = Propagators.ElementFunction2.bounds(values, index1.cpVar, index2.cpVar);
    IntVar z = newVar(b[0], b[1], null);
    postInternal(new Propagators.ElementFunction2(values, index1.cpVar, index2.cpVar, z.cpVar));
    return z;
  }

  /** <code>vars[index]</code>. */
  public IntExpr makeElement(IntVar[] vars, IntVar index) {
    long lo = Long.MAX_VALUE;
    long hi = Long.MIN_VALUE;
    for (IntVar v : vars) {
      lo = Math.min(lo, v.min());
      hi = Math.max(hi, v.max());
    }
    IntVar z = newVar(lo, hi, null);
    postInternal(new Propagators.ElementVar(engine(vars), index.cpVar, z.cpVar));
    return z;
  }

  private IntVar minMax(CpVar[] vars, boolean isMax) {
    long[] b = Propagators.MinMax.bounds(vars, isMax);
    IntVar z = newVar(b[0], b[1], null);
    postInternal(new Propagators.MinMax(vars, z.cpVar, isMax));
    return z;
  }

  /** The minimum of <code>vars</code>. */
  public IntExpr makeMin(IntVar[] vars) {
    return minMax(engine(vars), false);
  }

  /** <code>min(left, right)</code>. */
  public IntExpr makeMin(IntExpr left, IntExpr right) {
    return minMax(new CpVar[] {left.cpVar, right.cpVar}, false);
  }

  /** <code>min(expr, value)</code>. */
  public IntExpr makeMin(IntExpr expr, long value) {
    return minMax(new CpVar[] {expr.cpVar, store.newConstant(value)}, false);
  }

  /** <code>min(expr, value)</code>. */
  public IntExpr makeMin(IntExpr expr, int value) {
    return makeMin(expr, (long) value);
  }

  /** The maximum of <code>vars</code>. */
  public IntExpr makeMax(IntVar[] vars) {
    return minMax(engine(vars), true);
  }

  /** <code>max(left, right)</code>. */
  public IntExpr makeMax(IntExpr left, IntExpr right) {
    return minMax(new CpVar[] {left.cpVar, right.cpVar}, true);
  }

  /** <code>max(expr, value)</code>. */
  public IntExpr makeMax(IntExpr expr, long value) {
    return minMax(new CpVar[] {expr.cpVar, store.newConstant(value)}, true);
  }

  /** <code>max(expr, value)</code>. */
  public IntExpr makeMax(IntExpr expr, int value) {
    return makeMax(expr, (long) value);
  }

  /** <code>x % mod</code> with the sign of the dividend. */
  public IntExpr makeModulo(IntExpr x, long mod) {
    return makeModulo(x, makeIntConst(mod));
  }

  /** <code>x % mod</code> with the sign of the dividend. */
  public IntExpr makeModulo(IntExpr x, IntExpr mod) {
    long[] b = Propagators.Mod.bounds(x.cpVar, mod.cpVar);
    IntVar z = newVar(b[0], b[1], null);
    postInternal(new Propagators.Mod(x.cpVar, mod.cpVar, z.cpVar));
    return z;
  }

  /** <code>condition ? expr : unperformedValue</code>. */
  public IntExpr makeConditionalExpression(IntVar condition, IntExpr expr, long unperformedValue) {
    IntVar z = newVar(Math.min(expr.min(), unperformedValue),
        Math.max(expr.max(), unperformedValue), null);
    postInternal(new Propagators.Conditional(condition.cpVar, expr.cpVar, unperformedValue,
        z.cpVar));
    return z;
  }

  // ----- reified relations -----

  private IntVar reified(IntExpr left, IntExpr right, Relation r, long c) {
    IntVar b = makeBoolVar();
    postInternal(reifiedPropagator(left, right, r, c, b));
    return b;
  }

  private static Propagator reifiedPropagator(IntExpr left, IntExpr right, Relation r, long c,
      IntVar b) {
    if (right == null) {
      return new Propagators.Reified(new long[] {1}, new CpVar[] {left.cpVar}, r, c, b.cpVar);
    }
    return new Propagators.Reified(new long[] {1, -1}, new CpVar[] {left.cpVar, right.cpVar}, r,
        c, b.cpVar);
  }

  private Constraint reifiedCt(IntExpr left, IntExpr right, Relation r, long c, IntVar b) {
    return new Constraint(this, "IsRelation", null, reifiedPropagator(left, right, r, c, b));
  }

  /** <code>boolvar == (var == value)</code>. */
  public Constraint makeIsEqualCstCt(IntExpr var, long value, IntVar boolvar) {
    return reifiedCt(var, null, Relation.EQ, value, boolvar);
  }

  /** A 0/1 variable equal to <code>var == value</code>. */
  public IntVar makeIsEqualCstVar(IntExpr var, long value) {
    return reified(var, null, Relation.EQ, value);
  }

  /** <code>b == (v1 == v2)</code>. */
  public Constraint makeIsEqualVar(IntExpr v1, IntExpr v2, IntVar b) {
    return reifiedCt(v1, v2, Relation.EQ, 0, b);
  }

  /** A 0/1 variable equal to <code>v1 == v2</code>. */
  public IntVar makeIsEqualVar(IntExpr v1, IntExpr v2) {
    return reified(v1, v2, Relation.EQ, 0);
  }

  /** <code>boolvar == (var != value)</code>. */
  public Constraint makeIsDifferentCstCt(IntExpr var, long value, IntVar boolvar) {
    return reifiedCt(var, null, Relation.NE, value, boolvar);
  }

  /** A 0/1 variable equal to <code>var != value</code>. */
  public IntVar makeIsDifferentCstVar(IntExpr var, long value) {
    return reified(var, null, Relation.NE, value);
  }

  /** A 0/1 variable equal to <code>v1 != v2</code>. */
  public IntVar makeIsDifferentCstVar(IntExpr v1, IntExpr v2) {
    return reified(v1, v2, Relation.NE, 0);
  }

  /** <code>b == (v1 != v2)</code>. */
  public Constraint makeIsDifferentCstCt(IntExpr v1, IntExpr v2, IntVar b) {
    return reifiedCt(v1, v2, Relation.NE, 0, b);
  }

  /** <code>boolvar == (var &lt;= value)</code>. */
  public Constraint makeIsLessOrEqualCstCt(IntExpr var, long value, IntVar boolvar) {
    return reifiedCt(var, null, Relation.LE, value, boolvar);
  }

  /** A 0/1 variable equal to <code>var &lt;= value</code>. */
  public IntVar makeIsLessOrEqualCstVar(IntExpr var, long value) {
    return reified(var, null, Relation.LE, value);
  }

  /** A 0/1 variable equal to <code>left &lt;= right</code>. */
  public IntVar makeIsLessOrEqualVar(IntExpr left, IntExpr right) {
    return reified(left, right, Relation.LE, 0);
  }

  /** <code>b == (left &lt;= right)</code>. */
  public Constraint makeIsLessOrEqualCt(IntExpr left, IntExpr right, IntVar b) {
    return reifiedCt(left, right, Relation.LE, 0, b);
  }

  /** <code>boolvar == (var &gt;= value)</code>. */
  public Constraint makeIsGreaterOrEqualCstCt(IntExpr var, long value, IntVar boolvar) {
    return reifiedCt(var, null, Relation.GE, value, boolvar);
  }

  /** A 0/1 variable equal to <code>var &gt;= value</code>. */
  public IntVar makeIsGreaterOrEqualCstVar(IntExpr var, long value) {
    return reified(var, null, Relation.GE, value);
  }

  /** A 0/1 variable equal to <code>left &gt;= right</code>. */
  public IntVar makeIsGreaterOrEqualVar(IntExpr left, IntExpr right) {
    return reified(left, right, Relation.GE, 0);
  }

  /** <code>b == (left &gt;= right)</code>. */
  public Constraint makeIsGreaterOrEqualCt(IntExpr left, IntExpr right, IntVar b) {
    return reifiedCt(left, right, Relation.GE, 0, b);
  }

  /** <code>b == (v &gt; c)</code>. */
  public Constraint makeIsGreaterCstCt(IntExpr v, long c, IntVar b) {
    return reifiedCt(v, null, Relation.GE, CapOps.capAdd(c, 1), b);
  }

  /** A 0/1 variable equal to <code>var &gt; value</code>. */
  public IntVar makeIsGreaterCstVar(IntExpr var, long value) {
    return reified(var, null, Relation.GE, CapOps.capAdd(value, 1));
  }

  /** A 0/1 variable equal to <code>left &gt; right</code>. */
  public IntVar makeIsGreaterVar(IntExpr left, IntExpr right) {
    return reified(left, right, Relation.GE, 1);
  }

  /** <code>b == (left &gt; right)</code>. */
  public Constraint makeIsGreaterCt(IntExpr left, IntExpr right, IntVar b) {
    return reifiedCt(left, right, Relation.GE, 1, b);
  }

  /** <code>b == (v &lt; c)</code>. */
  public Constraint makeIsLessCstCt(IntExpr v, long c, IntVar b) {
    return reifiedCt(v, null, Relation.LE, CapOps.capSub(c, 1), b);
  }

  /** A 0/1 variable equal to <code>var &lt; value</code>. */
  public IntVar makeIsLessCstVar(IntExpr var, long value) {
    return reified(var, null, Relation.LE, CapOps.capSub(value, 1));
  }

  /** A 0/1 variable equal to <code>left &lt; right</code>. */
  public IntVar makeIsLessVar(IntExpr left, IntExpr right) {
    return reified(left, right, Relation.LE, -1);
  }

  /** <code>b == (left &lt; right)</code>. */
  public Constraint makeIsLessCt(IntExpr left, IntExpr right, IntVar b) {
    return reifiedCt(left, right, Relation.LE, -1, b);
  }

  // ----- constraints -----

  private Constraint linearCt(String description, long[] coefs, CpVar[] vars, long lo, long hi,
      Supplier<IntVar> reifier) {
    return new Constraint(this, description, reifier,
        new LinearPropagator(coefs, vars, lo, hi));
  }

  private Constraint compare(String op, IntExpr left, IntExpr right, long lo, long hi,
      Relation relation, long c) {
    return linearCt(left + " " + op + " " + right, new long[] {1, -1},
        new CpVar[] {left.cpVar, right.cpVar}, lo, hi,
        () -> reified(left, right, relation, c));
  }

  private Constraint compareCst(String op, IntExpr expr, long value, long lo, long hi,
      Relation relation, long c) {
    return linearCt(expr + " " + op + " " + value, new long[] {1}, new CpVar[] {expr.cpVar}, lo,
        hi, () -> reified(expr, null, relation, c));
  }

  /** The constraint which is always satisfied. */
  public Constraint makeTrueConstraint() {
    return new Constraint(this, "TrueConstraint()", null);
  }

  /** The constraint which is never satisfied. */
  public Constraint makeFalseConstraint() {
    return makeFalseConstraint("");
  }

  /** The constraint which is never satisfied. */
  public Constraint makeFalseConstraint(String explanation) {
    CpVar one = store.newConstant(1);
    return new Constraint(this, "FalseConstraint(" + explanation + ")", null,
        new LinearPropagator(new long[] {1}, new CpVar[] {one}, 0, 0));
  }

  /** <code>left == right</code>. */
  public Constraint makeEquality(IntExpr left, IntExpr right) {
    return compare("==", left, right, 0, 0, Relation.EQ, 0);
  }

  /** <code>expr == value</code>. */
  public Constraint makeEquality(IntExpr expr, long value) {
    return compareCst("==", expr, value, value, value, Relation.EQ, value);
  }

  /** <code>expr == value</code>. */
  public Constraint makeEquality(IntExpr expr, int value) {
    return makeEquality(expr, (long) value);
  }

  /** <code>left != right</code>. */
  public Constraint makeNonEquality(IntExpr left, IntExpr right) {
    return new Constraint(this, left + " != " + right,
        () -> reified(left, right, Relation.NE, 0), new Propagators.NotEqual(new long[] {1, -1},
            new CpVar[] {left.cpVar, right.cpVar}, 0));
  }

  /** <code>expr != value</code>. */
  public Constraint makeNonEquality(IntExpr expr, long value) {
    return new Constraint(this, expr + " != " + value,
        () -> reified(expr, null, Relation.NE, value),
        new Propagators.NotEqual(new long[] {1}, new CpVar[] {expr.cpVar}, value));
  }

  /** <code>expr != value</code>. */
  public Constraint makeNonEquality(IntExpr expr, int value) {
    return makeNonEquality(expr, (long) value);
  }

  /** <code>left &lt;= right</code>. */
  public Constraint makeLessOrEqual(IntExpr left, IntExpr right) {
    return compare("<=", left, right, Long.MIN_VALUE, 0, Relation.LE, 0);
  }

  /** <code>expr &lt;= value</code>. */
  public Constraint makeLessOrEqual(IntExpr expr, long value) {
    return compareCst("<=", expr, value, Long.MIN_VALUE, value, Relation.LE, value);
  }

  /** <code>expr &lt;= value</code>. */
  public Constraint makeLessOrEqual(IntExpr expr, int value) {
    return makeLessOrEqual(expr, (long) value);
  }

  /** <code>left &gt;= right</code>. */
  public Constraint makeGreaterOrEqual(IntExpr left, IntExpr right) {
    return compare(">=", left, right, 0, Long.MAX_VALUE, Relation.GE, 0);
  }

  /** <code>expr &gt;= value</code>. */
  public Constraint makeGreaterOrEqual(IntExpr expr, long value) {
    return compareCst(">=", expr, value, value, Long.MAX_VALUE, Relation.GE, value);
  }

  /** <code>expr &gt;= value</code>. */
  public Constraint makeGreaterOrEqual(IntExpr expr, int value) {
    return makeGreaterOrEqual(expr, (long) value);
  }

  /** <code>left &gt; right</code>. */
  public Constraint makeGreater(IntExpr left, IntExpr right) {
    return compare(">", left, right, 1, Long.MAX_VALUE, Relation.GE, 1);
  }

  /** <code>expr &gt; value</code>. */
  public Constraint makeGreater(IntExpr expr, long value) {
    long v = CapOps.capAdd(value, 1);
    return compareCst(">", expr, value, v, Long.MAX_VALUE, Relation.GE, v);
  }

  /** <code>expr &gt; value</code>. */
  public Constraint makeGreater(IntExpr expr, int value) {
    return makeGreater(expr, (long) value);
  }

  /** <code>left &lt; right</code>. */
  public Constraint makeLess(IntExpr left, IntExpr right) {
    return compare("<", left, right, Long.MIN_VALUE, -1, Relation.LE, -1);
  }

  /** <code>expr &lt; value</code>. */
  public Constraint makeLess(IntExpr expr, long value) {
    long v = CapOps.capSub(value, 1);
    return compareCst("<", expr, value, Long.MIN_VALUE, v, Relation.LE, v);
  }

  /** <code>expr &lt; value</code>. */
  public Constraint makeLess(IntExpr expr, int value) {
    return makeLess(expr, (long) value);
  }

  private static long[] ones(int n) {
    long[] c = new long[n];
    Arrays.fill(c, 1);
    return c;
  }

  /** <code>sum(vars) &lt;= cst</code>. */
  public Constraint makeSumLessOrEqual(IntVar[] vars, long cst) {
    return linearCt("SumLessOrEqual", ones(vars.length), engine(vars), Long.MIN_VALUE, cst, null);
  }

  /** <code>sum(vars) &gt;= cst</code>. */
  public Constraint makeSumGreaterOrEqual(IntVar[] vars, long cst) {
    return linearCt("SumGreaterOrEqual", ones(vars.length), engine(vars), cst, Long.MAX_VALUE,
        null);
  }

  /** <code>sum(vars) == cst</code>. */
  public Constraint makeSumEquality(IntVar[] vars, long cst) {
    return linearCt("SumEquality", ones(vars.length), engine(vars), cst, cst, null);
  }

  /** <code>sum(vars) == var</code>. */
  public Constraint makeSumEquality(IntVar[] vars, IntVar var) {
    long[] c = Arrays.copyOf(ones(vars.length), vars.length + 1);
    c[vars.length] = -1;
    CpVar[] v = Arrays.copyOf(engine(vars), vars.length + 1);
    v[vars.length] = var.cpVar;
    return linearCt("SumEquality", c, v, 0, 0, null);
  }

  /** <code>sum(coefficients * vars) == cst</code>. */
  public Constraint makeScalProdEquality(IntVar[] vars, long[] coefficients, long cst) {
    return linearCt("ScalProdEquality", coefficients.clone(), engine(vars), cst, cst, null);
  }

  /** <code>sum(coefficients * vars) == cst</code>. */
  public Constraint makeScalProdEquality(IntVar[] vars, int[] coefficients, long cst) {
    return makeScalProdEquality(vars, toLong(coefficients), cst);
  }

  /** <code>sum(coefficients * vars) == target</code>. */
  public Constraint makeScalProdEquality(IntVar[] vars, long[] coefficients, IntVar target) {
    long[] c = Arrays.copyOf(coefficients, vars.length + 1);
    c[vars.length] = -1;
    CpVar[] v = Arrays.copyOf(engine(vars), vars.length + 1);
    v[vars.length] = target.cpVar;
    return linearCt("ScalProdEquality", c, v, 0, 0, null);
  }

  /** <code>sum(coefficients * vars) == target</code>. */
  public Constraint makeScalProdEquality(IntVar[] vars, int[] coefficients, IntVar target) {
    return makeScalProdEquality(vars, toLong(coefficients), target);
  }

  /** <code>sum(coeffs * vars) &gt;= cst</code>. */
  public Constraint makeScalProdGreaterOrEqual(IntVar[] vars, long[] coeffs, long cst) {
    return linearCt("ScalProdGreaterOrEqual", coeffs.clone(), engine(vars), cst, Long.MAX_VALUE,
        null);
  }

  /** <code>sum(coeffs * vars) &gt;= cst</code>. */
  public Constraint makeScalProdGreaterOrEqual(IntVar[] vars, int[] coeffs, long cst) {
    return makeScalProdGreaterOrEqual(vars, toLong(coeffs), cst);
  }

  /** <code>sum(coefficients * vars) &lt;= cst</code>. */
  public Constraint makeScalProdLessOrEqual(IntVar[] vars, long[] coefficients, long cst) {
    return linearCt("ScalProdLessOrEqual", coefficients.clone(), engine(vars), Long.MIN_VALUE,
        cst, null);
  }

  /** <code>sum(coefficients * vars) &lt;= cst</code>. */
  public Constraint makeScalProdLessOrEqual(IntVar[] vars, int[] coefficients, long cst) {
    return makeScalProdLessOrEqual(vars, toLong(coefficients), cst);
  }

  /** <code>min_var == min(vars)</code>. */
  public Constraint makeMinEquality(IntVar[] vars, IntVar minVar) {
    return new Constraint(this, "MinEquality", null,
        new Propagators.MinMax(engine(vars), minVar.cpVar, false));
  }

  /** <code>max_var == max(vars)</code>. */
  public Constraint makeMaxEquality(IntVar[] vars, IntVar maxVar) {
    return new Constraint(this, "MaxEquality", null,
        new Propagators.MinMax(engine(vars), maxVar.cpVar, true));
  }

  /** <code>target == vals[index]</code>. */
  public Constraint makeElementEquality(long[] vals, IntVar index, IntVar target) {
    return new Constraint(this, "ElementEquality", null,
        new Propagators.Element(vals, index.cpVar, target.cpVar));
  }

  /** <code>target == vals[index]</code>. */
  public Constraint makeElementEquality(int[] vals, IntVar index, IntVar target) {
    return makeElementEquality(toLong(vals), index, target);
  }

  /** <code>target == vars[index]</code>. */
  public Constraint makeElementEquality(IntVar[] vars, IntVar index, IntVar target) {
    return new Constraint(this, "ElementEquality", null,
        new Propagators.ElementVar(engine(vars), index.cpVar, target.cpVar));
  }

  /** <code>target == vars[index]</code>. */
  public Constraint makeElementEquality(IntVar[] vars, IntVar index, long target) {
    return makeElementEquality(vars, index, makeIntConst(target));
  }

  /** <code>abs_var == |var|</code>. */
  public Constraint makeAbsEquality(IntVar var, IntVar absVar) {
    return new Constraint(this, "AbsEquality", null,
        new Propagators.Abs(var.cpVar, absVar.cpVar));
  }

  /** <code>l &lt;= expr &lt;= u</code>. */
  public Constraint makeBetweenCt(IntExpr expr, long l, long u) {
    return linearCt("BetweenCt", new long[] {1}, new CpVar[] {expr.cpVar}, l, u, null);
  }

  /** <code>expr</code> takes one of <code>values</code>. */
  public Constraint makeMemberCt(IntExpr expr, long[] values) {
    return new Constraint(this, "MemberCt", null,
        new Propagators.Member(expr.cpVar, values, values, true));
  }

  /** <code>expr</code> takes one of <code>values</code>. */
  public Constraint makeMemberCt(IntExpr expr, int[] values) {
    return makeMemberCt(expr, toLong(values));
  }

  /** <code>expr</code> takes none of <code>values</code>. */
  public Constraint makeNotMemberCt(IntExpr expr, long[] values) {
    return new Constraint(this, "NotMemberCt", null,
        new Propagators.Member(expr.cpVar, values, values, false));
  }

  /** <code>expr</code> takes none of <code>values</code>. */
  public Constraint makeNotMemberCt(IntExpr expr, int[] values) {
    return makeNotMemberCt(expr, toLong(values));
  }

  /** <code>expr</code> lies in none of the intervals <code>[starts[i], ends[i]]</code>. */
  public Constraint makeNotMemberCt(IntExpr expr, long[] starts, long[] ends) {
    return new Constraint(this, "NotMemberCt", null,
        new Propagators.Member(expr.cpVar, starts, ends, false));
  }

  /** <code>expr</code> lies in none of the intervals <code>[starts[i], ends[i]]</code>. */
  public Constraint makeNotMemberCt(IntExpr expr, int[] starts, int[] ends) {
    return makeNotMemberCt(expr, toLong(starts), toLong(ends));
  }

  /** Exactly <code>maxCount</code> of <code>vars</code> are equal to <code>value</code>. */
  public Constraint makeCount(IntVar[] vars, long value, long maxCount) {
    return makeCount(vars, value, makeIntConst(maxCount));
  }

  /** Exactly <code>maxCount</code> of <code>vars</code> are equal to <code>value</code>. */
  public Constraint makeCount(IntVar[] vars, long value, IntVar maxCount) {
    return new Constraint(this, "Count", null,
        new Propagators.Count(engine(vars), value, maxCount.cpVar));
  }

  /** All variables take different values. */
  public Constraint makeAllDifferent(IntVar[] vars) {
    return makeAllDifferent(vars, true);
  }

  /** All variables take different values; strong propagation adds Hall interval reasoning. */
  public Constraint makeAllDifferent(IntVar[] vars, boolean strongerPropagation) {
    return new Constraint(this, "AllDifferent", null,
        new Propagators.AllDifferent(engine(vars), strongerPropagation));
  }

  /** Adds a constraint to the model (or, inside a search, to the current search node). */
  public void addConstraint(Constraint c) {
    if (c.solver != this) {
      throw new IllegalArgumentException("constraint of another solver");
    }
    constraintCount++;
    for (Propagator p : c.propagators) {
      postInternal(p);
    }
    if (parameters.getPrintAddedConstraints()) {
      LOGGER.info(c.toString());
    }
  }

  // ----- intervals and scheduling -----

  private IntervalVar interval(IntVar start, IntVar duration, IntVar performed, String varName) {
    long[] b = LinearPropagator.bounds(new long[] {1, 1},
        new CpVar[] {start.cpVar, duration.cpVar}, 0);
    IntVar end = newVar(b[0], b[1], null);
    postInternal(new LinearPropagator(new long[] {1, 1, -1},
        new CpVar[] {start.cpVar, duration.cpVar, end.cpVar}, 0, 0));
    return new IntervalVar(this, start, duration, end, performed, varName);
  }

  /** An interval with a fixed duration, which is optional if <code>optional</code>. */
  public IntervalVar makeFixedDurationIntervalVar(long startMin, long startMax, long duration,
      boolean optional, String varName) {
    return interval(makeIntVar(startMin, startMax), makeIntConst(duration),
        optional ? makeBoolVar() : makeIntConst(1), varName);
  }

  /** A performed interval with a fixed duration starting at <code>startVariable</code>. */
  public IntervalVar makeFixedDurationIntervalVar(IntVar startVariable, long duration,
      String varName) {
    return interval(startVariable, makeIntConst(duration), makeIntConst(1), varName);
  }

  /** An interval with a fixed duration starting at <code>startVariable</code>. */
  public IntervalVar makeFixedDurationIntervalVar(IntVar startVariable, long duration,
      IntVar performedVariable, String varName) {
    return interval(startVariable, makeIntConst(duration), performedVariable, varName);
  }

  /** A fixed, performed interval. */
  public IntervalVar makeFixedInterval(long start, long duration, String varName) {
    return interval(makeIntConst(start), makeIntConst(duration), makeIntConst(1), varName);
  }

  /** An interval with variable start, duration and end. */
  public IntervalVar makeIntervalVar(long startMin, long startMax, long durationMin,
      long durationMax, long endMin, long endMax, boolean optional, String varName) {
    IntervalVar iv = interval(makeIntVar(startMin, startMax), makeIntVar(durationMin, durationMax),
        optional ? makeBoolVar() : makeIntConst(1), varName);
    iv.end.setRange(endMin, endMax);
    return iv;
  }

  /** <code>count</code> intervals with a fixed duration named <code>name0, name1, ...</code>. */
  public IntervalVar[] makeFixedDurationIntervalVarArray(int count, long startMin, long startMax,
      long duration, boolean optional, String varName) {
    IntervalVar[] array = new IntervalVar[count];
    for (int i = 0; i < count; ++i) {
      array[i] = makeFixedDurationIntervalVar(startMin, startMax, duration, optional, varName + i);
    }
    return array;
  }

  /** Relation between an interval and a date (assuming the interval is performed). */
  public Constraint makeIntervalVarRelation(IntervalVar t, int r, long d) {
    switch (r) {
      case ENDS_AFTER:
        return makeGreaterOrEqual(t.end, d);
      case ENDS_AT:
        return makeEquality(t.end, d);
      case ENDS_BEFORE:
        return makeLessOrEqual(t.end, d);
      case STARTS_AFTER:
        return makeGreaterOrEqual(t.start, d);
      case STARTS_AT:
        return makeEquality(t.start, d);
      case STARTS_BEFORE:
        return makeLessOrEqual(t.start, d);
      default:
        throw new UnsupportedOperationException("interval relation " + r);
    }
  }

  /** Relation between two intervals (assuming both are performed). */
  public Constraint makeIntervalVarRelation(IntervalVar t1, int r, IntervalVar t2) {
    return makeIntervalVarRelationWithDelay(t1, r, t2, 0);
  }

  /** Relation between two intervals with a delay (assuming both are performed). */
  public Constraint makeIntervalVarRelationWithDelay(IntervalVar t1, int r, IntervalVar t2,
      long delay) {
    IntVar a;
    IntVar b;
    boolean equal = false;
    switch (r) {
      case ENDS_AFTER_END:
        a = t1.end;
        b = t2.end;
        break;
      case ENDS_AFTER_START:
        a = t1.end;
        b = t2.start;
        break;
      case ENDS_AT_END:
        a = t1.end;
        b = t2.end;
        equal = true;
        break;
      case ENDS_AT_START:
        a = t1.end;
        b = t2.start;
        equal = true;
        break;
      case STARTS_AFTER_END:
        a = t1.start;
        b = t2.end;
        break;
      case STARTS_AFTER_START:
        a = t1.start;
        b = t2.start;
        break;
      case STARTS_AT_END:
        a = t1.start;
        b = t2.end;
        equal = true;
        break;
      case STARTS_AT_START:
        a = t1.start;
        b = t2.start;
        equal = true;
        break;
      default:
        throw new UnsupportedOperationException("interval relation " + r);
    }
    // a >= b + delay (or a == b + delay)
    return linearCt("IntervalVarRelation", new long[] {1, -1}, new CpVar[] {a.cpVar, b.cpVar},
        delay, equal ? delay : Long.MAX_VALUE, null);
  }

  /** Both intervals have the same start and end. */
  public Constraint makeEquality(IntervalVar var1, IntervalVar var2) {
    return new Constraint(this, "IntervalEquality", null,
        new LinearPropagator(new long[] {1, -1}, new CpVar[] {var1.start.cpVar, var2.start.cpVar},
            0, 0),
        new LinearPropagator(new long[] {1, -1}, new CpVar[] {var1.end.cpVar, var2.end.cpVar}, 0,
            0));
  }

  private Constraint cumulative(IntervalVar[] intervals, CpVar[] demands, CpVar capacity,
      String ctName) {
    CpVar[] starts = new CpVar[intervals.length];
    CpVar[] durations = new CpVar[intervals.length];
    CpVar[] performed = new CpVar[intervals.length];
    for (int i = 0; i < intervals.length; i++) {
      starts[i] = intervals[i].start.cpVar;
      durations[i] = intervals[i].duration.cpVar;
      performed[i] = intervals[i].performed.cpVar;
    }
    return new Constraint(this, "Cumulative(" + ctName + ")", null,
        new Propagators.Cumulative(starts, durations, performed, demands, capacity));
  }

  private CpVar[] constants(long[] values) {
    CpVar[] r = new CpVar[values.length];
    for (int i = 0; i < values.length; i++) {
      r[i] = store.newConstant(values[i]);
    }
    return r;
  }

  /** The demands of the performed intervals at any time are at most <code>capacity</code>. */
  public Constraint makeCumulative(IntervalVar[] intervals, long[] demands, long capacity,
      String ctName) {
    return cumulative(intervals, constants(demands), store.newConstant(capacity), ctName);
  }

  /** The demands of the performed intervals at any time are at most <code>capacity</code>. */
  public Constraint makeCumulative(IntervalVar[] intervals, int[] demands, long capacity,
      String ctName) {
    return makeCumulative(intervals, toLong(demands), capacity, ctName);
  }

  /** The demands of the performed intervals at any time are at most <code>capacity</code>. */
  public Constraint makeCumulative(IntervalVar[] intervals, long[] demands, IntVar capacity,
      String ctName) {
    return cumulative(intervals, constants(demands), capacity.cpVar, ctName);
  }

  /** The demands of the performed intervals at any time are at most <code>capacity</code>. */
  public Constraint makeCumulative(IntervalVar[] intervals, int[] demands, IntVar capacity,
      String ctName) {
    return makeCumulative(intervals, toLong(demands), capacity, ctName);
  }

  /** The demands of the performed intervals at any time are at most <code>capacity</code>. */
  public Constraint makeCumulative(IntervalVar[] intervals, IntVar[] demands, long capacity,
      String ctName) {
    return cumulative(intervals, engine(demands), store.newConstant(capacity), ctName);
  }

  /** The demands of the performed intervals at any time are at most <code>capacity</code>. */
  public Constraint makeCumulative(IntervalVar[] intervals, IntVar[] demands, IntVar capacity,
      String ctName) {
    return cumulative(intervals, engine(demands), capacity.cpVar, ctName);
  }

  /** The performed intervals don't overlap (a cumulative of capacity 1). */
  public Constraint makeDisjunctiveConstraint(IntervalVar[] intervals, String ctName) {
    long[] ones = ones(intervals.length);
    return makeCumulative(intervals, ones, 1, ctName);
  }

  // ----- assignments, collectors, objectives, limits, logs -----

  /** An empty assignment. */
  public Assignment makeAssignment() {
    return new Assignment(this);
  }

  /** A copy of <code>a</code>. */
  public Assignment makeAssignment(Assignment a) {
    return new Assignment(a);
  }

  /** Collects the first solution of the variables of <code>assignment</code>. */
  public SolutionCollector makeFirstSolutionCollector(Assignment assignment) {
    return new SolutionCollector(this, assignment, SolutionCollector.Kind.FIRST, false);
  }

  /** Collects the first solution. */
  public SolutionCollector makeFirstSolutionCollector() {
    return makeFirstSolutionCollector(null);
  }

  /** Collects the last solution of the variables of <code>assignment</code>. */
  public SolutionCollector makeLastSolutionCollector(Assignment assignment) {
    return new SolutionCollector(this, assignment, SolutionCollector.Kind.LAST, false);
  }

  /** Collects the last solution. */
  public SolutionCollector makeLastSolutionCollector() {
    return makeLastSolutionCollector(null);
  }

  /** Collects the solution with the best objective of <code>assignment</code>. */
  public SolutionCollector makeBestValueSolutionCollector(Assignment assignment,
      boolean maximize) {
    return new SolutionCollector(this, assignment, SolutionCollector.Kind.BEST, maximize);
  }

  /** Collects the solution with the best objective. */
  public SolutionCollector makeBestValueSolutionCollector(boolean maximize) {
    return makeBestValueSolutionCollector(null, maximize);
  }

  /** Collects all solutions of the variables of <code>assignment</code>. */
  public SolutionCollector makeAllSolutionCollector(Assignment assignment) {
    return new SolutionCollector(this, assignment, SolutionCollector.Kind.ALL, false);
  }

  /** Collects all solutions. */
  public SolutionCollector makeAllSolutionCollector() {
    return makeAllSolutionCollector(null);
  }

  /** Minimizes <code>v</code>; each solution must improve by <code>step</code>. */
  public OptimizeVar makeMinimize(IntVar v, long step) {
    return new OptimizeVar(this, false, v, step);
  }

  /** Maximizes <code>v</code>; each solution must improve by <code>step</code>. */
  public OptimizeVar makeMaximize(IntVar v, long step) {
    return new OptimizeVar(this, true, v, step);
  }

  /** Minimizes or maximizes <code>v</code>. */
  public OptimizeVar makeOptimize(boolean maximize, IntVar v, long step) {
    return new OptimizeVar(this, maximize, v, step);
  }

  /** A limit on the number of branches. */
  public RegularLimit makeBranchesLimit(long branchLimit) {
    return limit(Long.MAX_VALUE, branchLimit, Long.MAX_VALUE, Long.MAX_VALUE, false, false);
  }

  /** A limit on the number of failures. */
  public RegularLimit makeFailuresLimit(long failureLimit) {
    return limit(Long.MAX_VALUE, Long.MAX_VALUE, failureLimit, Long.MAX_VALUE, false, false);
  }

  /** A limit on the number of solutions. */
  public RegularLimit makeSolutionsLimit(long solutionLimit) {
    return limit(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, solutionLimit, false, false);
  }

  /** A combined limit (time in milliseconds). */
  private RegularLimit limit(long time, long branchLimit, long failureLimit, long solutionLimit,
      boolean smartTimeCheck, boolean cumulative) {
    return new RegularLimit(this, time, branchLimit, failureLimit, solutionLimit, smartTimeCheck,
        cumulative);
  }

  /** A limit built from the parameters message. */
  public RegularLimit makeLimit(RegularLimitParameters proto) {
    return limit(proto.getTime(), proto.getBranches(), proto.getFailures(), proto.getSolutions(),
        proto.getSmartTimeCheck(), proto.getCumulative());
  }

  /** Parameters without any limit. */
  public RegularLimitParameters makeDefaultRegularLimitParameters() {
    return RegularLimitParameters.newBuilder().setTime(Long.MAX_VALUE)
        .setBranches(Long.MAX_VALUE).setFailures(Long.MAX_VALUE).setSolutions(Long.MAX_VALUE)
        .setSmartTimeCheck(false).setCumulative(false).build();
  }

  /**
   * A limit reached when either limit is reached. The OR-Tools 10.0 Java wrapper renames only the
   * <code>RegularLimitParameters</code> overload of <code>Solver::MakeLimit</code> to
   * <code>makeLimit</code>; this overload keeps its C++ name.
   */
  public SearchLimit MakeLimit(SearchLimit limit1, SearchLimit limit2) {
    return new SearchLimit(this) {
      @Override
      boolean isLimitReached() {
        return limit1.check() | limit2.check();
      }

      @Override
      public void init() {
        super.init();
        limit1.init();
        limit2.init();
      }
    };
  }

  /** A limit reached when <code>limiter</code> returns true. */
  public SearchLimit makeCustomLimit(BooleanSupplier limiter) {
    return new SearchLimit(this) {
      @Override
      boolean isLimitReached() {
        return limiter.getAsBoolean();
      }
    };
  }

  /** Logs the search every <code>branchPeriod</code> branches and at every solution. */
  public SearchMonitor makeSearchLog(int branchPeriod) {
    return new SearchLog(this, branchPeriod, null, null, null);
  }

  /** A search log which also shows <code>var</code>. */
  public SearchMonitor makeSearchLog(int branchPeriod, IntVar var) {
    return new SearchLog(this, branchPeriod, new IntVar[] {var}, null, null);
  }

  /** A search log which also shows the text of <code>displayCallback</code>. */
  public SearchMonitor makeSearchLog(int branchPeriod, Supplier<String> displayCallback) {
    return new SearchLog(this, branchPeriod, null, null, displayCallback);
  }

  /** A search log which also shows <code>var</code> and the text of the callback. */
  public SearchMonitor makeSearchLog(int branchPeriod, IntVar var,
      Supplier<String> displayCallback) {
    return new SearchLog(this, branchPeriod, new IntVar[] {var}, null, displayCallback);
  }

  /** A search log which also shows <code>vars</code> and the text of the callback. */
  public SearchMonitor makeSearchLog(int branchPeriod, IntVar[] vars,
      Supplier<String> displayCallback) {
    return new SearchLog(this, branchPeriod, vars.clone(), null, displayCallback);
  }

  /** A search log which also shows the objective. */
  public SearchMonitor makeSearchLog(int branchPeriod, OptimizeVar optVar) {
    return new SearchLog(this, branchPeriod, null, optVar, null);
  }

  /** A search log which also shows the objective and the text of the callback. */
  public SearchMonitor makeSearchLog(int branchPeriod, OptimizeVar optVar,
      Supplier<String> displayCallback) {
    return new SearchLog(this, branchPeriod, null, optVar, displayCallback);
  }

  // ----- decisions and decision builders -----

  /** A decision which fails in both branches. */
  public Decision makeFailDecision() {
    return new Phases.Fail();
  }

  /** A decision whose branches run the given closures. */
  public Decision makeDecision(Consumer<Solver> apply, Consumer<Solver> refute) {
    return new Phases.Closure(apply, refute);
  }

  /** Assigns <code>vars</code> by the given variable and value strategies. */
  public DecisionBuilder makePhase(IntVar[] vars, int varStr, int valStr) {
    return new Phases.Phase(vars, varStr, valStr, null, null, random);
  }

  /** Assigns the variable with the smallest evaluation first. */
  public DecisionBuilder makePhase(IntVar[] vars, LongUnaryOperator varEvaluator, int valStr) {
    return new Phases.Phase(vars, INT_VAR_DEFAULT, valStr, varEvaluator, null, random);
  }

  /** Assigns the value with the smallest evaluation <code>valueEvaluator(var, value)</code>. */
  public DecisionBuilder makePhase(IntVar[] vars, int varStr, LongBinaryOperator valueEvaluator) {
    return new Phases.Phase(vars, varStr, INT_VALUE_DEFAULT, null, valueEvaluator, random);
  }

  /** Evaluator based selection of both the variable and the value. */
  public DecisionBuilder makePhase(IntVar[] vars, LongUnaryOperator varEvaluator,
      LongBinaryOperator valueEvaluator) {
    return new Phases.Phase(vars, INT_VAR_DEFAULT, INT_VALUE_DEFAULT, varEvaluator,
        valueEvaluator, random);
  }

  /** Value selection by evaluator; the tie breaker is ignored by this implementation. */
  public DecisionBuilder makePhase(IntVar[] vars, int varStr, LongBinaryOperator valueEvaluator,
      LongUnaryOperator tieBreaker) {
    return makePhase(vars, varStr, valueEvaluator);
  }

  /** Evaluator based selection; the tie breaker is ignored by this implementation. */
  public DecisionBuilder makePhase(IntVar[] vars, LongUnaryOperator varEvaluator,
      LongBinaryOperator valueEvaluator, LongUnaryOperator tieBreaker) {
    return makePhase(vars, varEvaluator, valueEvaluator);
  }

  /** The default phase (smallest domain first, smallest value first). */
  public DecisionBuilder makeDefaultPhase(IntVar[] vars) {
    return makePhase(vars, CHOOSE_MIN_SIZE_LOWEST_MIN, ASSIGN_MIN_VALUE);
  }

  /** Assigns one variable. */
  public DecisionBuilder makePhase(IntVar v0, int varStr, int valStr) {
    return makePhase(new IntVar[] {v0}, varStr, valStr);
  }

  /** Assigns two variables. */
  public DecisionBuilder makePhase(IntVar v0, IntVar v1, int varStr, int valStr) {
    return makePhase(new IntVar[] {v0, v1}, varStr, valStr);
  }

  /** Assigns three variables. */
  public DecisionBuilder makePhase(IntVar v0, IntVar v1, IntVar v2, int varStr, int valStr) {
    return makePhase(new IntVar[] {v0, v1, v2}, varStr, valStr);
  }

  /** Assigns four variables. */
  public DecisionBuilder makePhase(IntVar v0, IntVar v1, IntVar v2, IntVar v3, int varStr,
      int valStr) {
    return makePhase(new IntVar[] {v0, v1, v2, v3}, varStr, valStr);
  }

  /** Assigns the (variable, value) pair with the smallest <code>eval(var, value)</code>. */
  public DecisionBuilder makePhase(IntVar[] vars, LongBinaryOperator eval, int str) {
    return new Phases.GlobalBestPhase(vars, eval, null);
  }

  /** Assigns the (variable, value) pair with the smallest evaluation, ties by tie_breaker. */
  public DecisionBuilder makePhase(IntVar[] vars, LongBinaryOperator eval,
      LongUnaryOperator tieBreaker, int str) {
    return new Phases.GlobalBestPhase(vars, eval, tieBreaker);
  }

  // ----- search -----

  /** One running search: the decision builder, the monitors and the choice point stack. */
  private final class Search {
    final DecisionBuilder db;

    final SearchMonitor[] monitors;

    final boolean createdBySolve;

    final ArrayList<ChoicePoint> stack = new ArrayList<>();

    int baseLevel;

    boolean started;

    boolean finished;

    long solutionCount;

    Search(DecisionBuilder db, SearchMonitor[] monitors, boolean createdBySolve) {
      this.db = db;
      this.monitors = monitors;
      this.createdBySolve = createdBySolve;
    }
  }

  private static final class ChoicePoint {
    final Decision decision;

    int level;

    boolean refuted;

    ChoicePoint(Decision decision, int level) {
      this.decision = decision;
      this.level = level;
    }
  }

  private static SearchMonitor[] monitors(SearchMonitor... ms) {
    ArrayList<SearchMonitor> list = new ArrayList<>();
    for (SearchMonitor m : ms) {
      if (m != null) {
        list.add(m);
      }
    }
    return list.toArray(new SearchMonitor[0]);
  }

  /** Searches for a solution; returns true if one was found. */
  public boolean solve(DecisionBuilder db, SearchMonitor[] monitors) {
    startSearch(db, monitors(monitors), true);
    nextSolution();
    boolean found = search.solutionCount > 0;
    endSearch();
    return found;
  }

  /** Searches for a solution. */
  public boolean solve(DecisionBuilder db) {
    return solve(db, new SearchMonitor[0]);
  }

  /** Searches for a solution with one monitor. */
  public boolean solve(DecisionBuilder db, SearchMonitor m1) {
    return solve(db, new SearchMonitor[] {m1});
  }

  /** Searches for a solution with two monitors. */
  public boolean solve(DecisionBuilder db, SearchMonitor m1, SearchMonitor m2) {
    return solve(db, new SearchMonitor[] {m1, m2});
  }

  /** Searches for a solution with three monitors. */
  public boolean solve(DecisionBuilder db, SearchMonitor m1, SearchMonitor m2, SearchMonitor m3) {
    return solve(db, new SearchMonitor[] {m1, m2, m3});
  }

  /** Searches for a solution with four monitors. */
  public boolean solve(DecisionBuilder db, SearchMonitor m1, SearchMonitor m2, SearchMonitor m3,
      SearchMonitor m4) {
    return solve(db, new SearchMonitor[] {m1, m2, m3, m4});
  }

  /** Starts a search whose solutions are enumerated by {@link #nextSolution()}. */
  public void newSearch(DecisionBuilder db, SearchMonitor[] monitors) {
    startSearch(db, monitors(monitors), false);
  }

  /** Starts a search. */
  public void newSearch(DecisionBuilder db) {
    newSearch(db, new SearchMonitor[0]);
  }

  /** Starts a search with one monitor. */
  public void newSearch(DecisionBuilder db, SearchMonitor m1) {
    newSearch(db, new SearchMonitor[] {m1});
  }

  /** Starts a search with two monitors. */
  public void newSearch(DecisionBuilder db, SearchMonitor m1, SearchMonitor m2) {
    newSearch(db, new SearchMonitor[] {m1, m2});
  }

  /** Starts a search with three monitors. */
  public void newSearch(DecisionBuilder db, SearchMonitor m1, SearchMonitor m2,
      SearchMonitor m3) {
    newSearch(db, new SearchMonitor[] {m1, m2, m3});
  }

  /** Starts a search with four monitors. */
  public void newSearch(DecisionBuilder db, SearchMonitor m1, SearchMonitor m2, SearchMonitor m3,
      SearchMonitor m4) {
    newSearch(db, new SearchMonitor[] {m1, m2, m3, m4});
  }

  private void startSearch(DecisionBuilder db, SearchMonitor[] ms, boolean createdBySolve) {
    if (search != null) {
      throw new IllegalStateException("nested searches are not supported; call endSearch()");
    }
    search = new Search(db, ms, createdBySolve);
    finishRequested = false;
    state = IN_ROOT_NODE;
  }

  /** Ends the current search and restores the state before the search. */
  public void endSearch() {
    if (search == null) {
      return;
    }
    if (search.started) {
      for (SearchMonitor m : search.monitors) {
        m.exitSearch();
      }
      store.popToLevel(search.baseLevel - 1);
    }
    search = null;
    state = OUTSIDE_SEARCH;
  }

  /** Stops the current search at the next node. */
  public void finishCurrentSearch() {
    finishRequested = true;
  }

  /** Restarting a search is not supported by this implementation. */
  public void restartCurrentSearch() {
    throw new UnsupportedOperationException("restartCurrentSearch");
  }

  /** Whether {@link #finishCurrentSearch()} has been called during the current search. */
  boolean isFinishRequested() {
    return finishRequested;
  }

  /** Clears the request of {@link #finishCurrentSearch()}. */
  void clearFinishRequest() {
    finishRequested = false;
  }

  private boolean limitReached(Search s) {
    if (finishRequested) {
      return true;
    }
    boolean reached = false;
    for (SearchMonitor m : s.monitors) {
      m.periodicCheck();
      if (m instanceof SearchLimit) {
        reached |= ((SearchLimit) m).check();
      }
    }
    return reached;
  }

  private void onFailure(Search s) {
    failures++;
    for (SearchMonitor m : s.monitors) {
      m.beginFail();
    }
    for (SearchMonitor m : s.monitors) {
      m.endFail();
    }
  }

  private static boolean isFail(Throwable t) {
    return t instanceof CpFail || t instanceof FailException;
  }

  /** Backtracks to the most recent unrefuted choice point and refutes it. */
  private boolean backtrack(Search s) {
    while (!s.stack.isEmpty()) {
      ChoicePoint cp = s.stack.get(s.stack.size() - 1);
      store.popToLevel(cp.level - 1);
      if (cp.refuted) {
        s.stack.remove(s.stack.size() - 1);
        continue;
      }
      cp.refuted = true;
      store.pushLevel();
      cp.level = store.level();
      for (SearchMonitor m : s.monitors) {
        m.refuteDecision(cp.decision);
      }
      if (parameters.getTraceSearch()) {
        LOGGER.log(Level.FINE, "Refute " + cp.decision);
      }
      boolean ok = runGuarded(() -> {
        cp.decision.refute(this);
        store.propagate();
        for (SearchMonitor m : s.monitors) {
          m.afterDecision(cp.decision, false);
        }
        store.propagate();
      });
      if (ok) {
        return true;
      }
      onFailure(s);
    }
    return false;
  }

  /** Runs code which may fail; returns false (with an empty queue) on failure. */
  private boolean runGuarded(Runnable r) {
    try {
      r.run();
      return true;
    } catch (Throwable t) {
      if (isFail(t)) {
        store.clearQueue();
        return false;
      }
      if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
      }
      if (t instanceof Error) {
        throw (Error) t;
      }
      throw new IllegalStateException(t);
    }
  }

  private boolean finishSearch(Search s, boolean exhausted) {
    s.finished = true;
    if (exhausted) {
      for (SearchMonitor m : s.monitors) {
        m.noMoreSolutions();
      }
      state = NO_MORE_SOLUTIONS;
    }
    return false;
  }

  /**
   * Continues the current search until the next solution; returns false when the search tree is
   * exhausted or a limit was reached. Inside a search started by {@link #solve} the search
   * continues after a solution while a monitor asks for it (optimization, collectors).
   */
  public boolean nextSolution() {
    final Search s = search;
    if (s == null) {
      throw new IllegalStateException("no search; call newSearch() first");
    }
    if (s.finished) {
      return false;
    }
    if (!s.started) {
      s.started = true;
      store.pushLevel();
      s.baseLevel = store.level();
      for (SearchMonitor m : s.monitors) {
        m.enterSearch();
      }
      for (SearchMonitor m : s.monitors) {
        m.beginInitialPropagation();
      }
      boolean ok = !rootFailed && runGuarded(() -> {
        store.enqueueAll();
        store.propagate();
      });
      for (SearchMonitor m : s.monitors) {
        m.endInitialPropagation();
      }
      if (!ok) {
        onFailure(s);
        state = PROBLEM_INFEASIBLE;
        return finishSearch(s, true);
      }
      state = IN_SEARCH;
    } else {
      if (!backtrack(s)) {
        return finishSearch(s, true);
      }
    }
    while (true) {
      if (limitReached(s)) {
        return finishSearch(s, false);
      }
      final Decision[] next = new Decision[1];
      boolean ok = runGuarded(() -> {
        for (SearchMonitor m : s.monitors) {
          m.beginNextDecision(s.db);
        }
        store.propagate();
        next[0] = s.db.nextWrap(this);
        store.propagate();
        for (SearchMonitor m : s.monitors) {
          m.endNextDecision(s.db, next[0]);
        }
      });
      if (!ok) {
        onFailure(s);
        if (!backtrack(s)) {
          return finishSearch(s, true);
        }
        continue;
      }
      Decision d = next[0];
      if (d == null) {
        boolean accepted = true;
        for (SearchMonitor m : s.monitors) {
          accepted &= m.acceptSolution();
        }
        if (!accepted) {
          onFailure(s);
          if (!backtrack(s)) {
            return finishSearch(s, true);
          }
          continue;
        }
        solutions++;
        s.solutionCount++;
        state = AT_SOLUTION;
        boolean more = false;
        for (SearchMonitor m : s.monitors) {
          more |= m.atSolution();
        }
        if (s.createdBySolve) {
          if (more && !limitReached(s)) {
            state = IN_SEARCH;
            if (!backtrack(s)) {
              return finishSearch(s, true);
            }
            continue;
          }
          s.finished = true;
        }
        return true;
      }
      branches++;
      store.pushLevel();
      final ChoicePoint cp = new ChoicePoint(d, store.level());
      s.stack.add(cp);
      for (SearchMonitor m : s.monitors) {
        m.applyDecision(d);
      }
      if (parameters.getTraceSearch()) {
        LOGGER.log(Level.FINE, "Apply " + d);
      }
      ok = runGuarded(() -> {
        d.apply(this);
        store.propagate();
        for (SearchMonitor m : s.monitors) {
          m.afterDecision(d, true);
        }
        store.propagate();
      });
      if (!ok) {
        onFailure(s);
        if (!backtrack(s)) {
          return finishSearch(s, true);
        }
      }
    }
  }

  private static long[] toLong(int[] values) {
    long[] r = new long[values.length];
    for (int i = 0; i < values.length; i++) {
      r[i] = values[i];
    }
    return r;
  }
}
