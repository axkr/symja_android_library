package org.matheclipse.ortools.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.logging.Logger;
import org.matheclipse.ortools.constraintsolver.Assignment;
import org.matheclipse.ortools.constraintsolver.IntVar;
import org.matheclipse.ortools.constraintsolver.IntervalVar;
import org.matheclipse.ortools.constraintsolver.IntervalVarElement;
import org.matheclipse.ortools.constraintsolver.Solver;
import org.matheclipse.ortools.constraintsolver.cp.CapOps;
import org.matheclipse.ortools.constraintsolver.cp.CpFail;
import org.matheclipse.ortools.constraintsolver.cp.CpStore;
import org.matheclipse.ortools.constraintsolver.cp.CpVar;
import org.matheclipse.ortools.constraintsolver.cp.LinearPropagator;
import org.matheclipse.ortools.constraintsolver.cp.Propagator;
import org.matheclipse.ortools.constraintsolver.cp.SolverAccess;
import org.matheclipse.ortools.routing.engine.ArcMatrix;
import org.matheclipse.ortools.routing.engine.DimensionData;
import org.matheclipse.ortools.routing.engine.RoutingEngine;
import org.matheclipse.ortools.routing.engine.RoutingProblem;
import org.matheclipse.ortools.routing.engine.Scheduler;
import org.matheclipse.ortools.routing.engine.SideConstraints;

/**
 * A vehicle routing model, mirroring <code>com.google.ortools.routing.Model</code> of the
 * OR-Tools 10.0 Java wrapper (named <code>com.google.ortools.constraintsolver.RoutingModel</code>
 * before). Like the wrapper, which exposes only a selected part of <code>routing.h</code>, this
 * class offers the callbacks, dimensions, disjunctions, pickups and deliveries, allowed vehicles,
 * routing variables, solving and assignments.
 *
 * <p>
 * The model is solved by the simplified engine of the <code>engine</code> subpackage of this
 * module instead of the OR-Tools constraint programming search: first solution heuristics, local
 * search and guided local search. Constraints posted by the user on <code>solver()</code> over
 * routing variables are checked on every candidate solution by the propagation engine of the
 * {@link Solver}.
 *
 * <p>
 * <code>ROUTING_OPTIMAL</code> is reported only for models with a single possible route
 * structure (no visit node, or one mandatory visit node and one vehicle); otherwise a solution
 * yields <code>ROUTING_SUCCESS</code>.
 */
public class Model {

  /** Any order of pickups and deliveries on a route. */
  public static final int PICKUP_AND_DELIVERY_NO_ORDER = 0;

  /** Deliveries in the reverse order of the pickups. */
  public static final int PICKUP_AND_DELIVERY_LIFO = 1;

  /** Deliveries in the order of the pickups. */
  public static final int PICKUP_AND_DELIVERY_FIFO = 2;

  /**
   * Penalty cost behavior of a soft disjunction bound: the penalty is paid once when the bound
   * is violated.
   */
  public static final int PENALIZE_ONCE = 0;

  /**
   * Penalty cost behavior of a soft disjunction bound: the penalty is paid for every active
   * index missing below a soft minimum (or in excess above a soft maximum).
   */
  public static final int PENALIZE_PER_INACTIVE = 1;

  /** Visit type policy constant (visit types are not supported). */
  public static final int TYPE_ADDED_TO_VEHICLE = 0;

  /** Visit type policy constant (visit types are not supported). */
  public static final int ADDED_TYPE_REMOVED_FROM_VEHICLE = 1;

  /** Visit type policy constant (visit types are not supported). */
  public static final int TYPE_ON_VEHICLE_UP_TO_VISIT = 2;

  /** Visit type policy constant (visit types are not supported). */
  public static final int TYPE_SIMULTANEOUSLY_ADDED_AND_REMOVED = 3;

  private static final Logger LOGGER = Logger.getLogger(Model.class.getName());

  private static final SolverAccess ACCESS = SolverAccess.get(SolverAccessToken.INSTANCE);

  /** The penalty of <code>addDisjunction</code> meaning "hard constraint". */
  private static final long NO_PENALTY = -1;

  /**
   * A disjunction with the fields of the C++ <code>Model::Disjunction</code>: hard minimum and
   * maximum and soft minimum and maximum of the number of active indices.
   */
  private static final class Disjunction {
    final long[] indices;

    long minCardinality;

    long softMinCardinality;

    long softMinPenalty;

    int softMinPenaltyType = PENALIZE_ONCE;

    long maxCardinality;

    long softMaxCardinality = Long.MAX_VALUE;

    long softMaxPenalty;

    int softMaxPenaltyType = PENALIZE_ONCE;

    Disjunction(long[] indices) {
      this.indices = indices;
      this.maxCardinality = indices.length;
    }
  }

  private final IndexManager manager;

  private final Solver solver;

  private final int size;

  private final int vehicles;

  private final int numIndices;

  private final IntVar[] nexts;

  private final IntVar[] vehicleVars;

  private final IntVar[] active;

  private final IntVar[] vehicleActive;

  private final IntVar costVar;

  private final List<LongBinaryOperator> transitCallbacks = new ArrayList<>();

  private final Map<Integer, ArcMatrix> matrices = new HashMap<>();

  private final int[] vehicleEvaluator;

  private final long[] fixedCost;

  private final List<Dimension> dimensions = new ArrayList<>();

  private final Map<String, Dimension> dimensionByName = new LinkedHashMap<>();

  private final List<Disjunction> disjunctions = new ArrayList<>();

  /** The vehicles allowed for each index by setAllowedVehiclesForIndex; null or empty: all. */
  private final int[][] allowedVehicles;

  private final List<int[]> pairs = new ArrayList<>();

  private final int[] policy;

  private final List<IntVar> finalizerVars = new ArrayList<>();

  private final List<long[]> finalizerKinds = new ArrayList<>(); // kind, target

  private final List<Runnable> atSolutionCallbacks = new ArrayList<>();

  private final List<IntVar> extraVars = new ArrayList<>();

  private final List<IntervalVar> extraIntervals = new ArrayList<>();

  private RoutingSearchStatus.Value status = RoutingSearchStatus.Value.ROUTING_NOT_SOLVED;

  private boolean closed;

  private String primaryDimension = "";

  /** Creates a model with the default model parameters. */
  public Model(IndexManager indexManager) {
    this(indexManager, Globals.defaultRoutingModelParameters());
  }

  /** Creates a model; only the solver parameters of <code>parameters</code> are used. */
  public Model(IndexManager indexManager, RoutingModelParameters parameters) {
    this.manager = indexManager;
    this.solver = new Solver("Routing", parameters.getSolverParameters());
    this.vehicles = indexManager.getNumberOfVehicles();
    this.numIndices = indexManager.getNumberOfIndices();
    this.size = numIndices - vehicles;
    nexts = solver.makeIntVarArray(size, 0, numIndices - 1, "Nexts");
    vehicleVars = solver.makeIntVarArray(numIndices, -1, vehicles - 1, "Vehicles");
    active = solver.makeBoolVarArray(size, "Active");
    vehicleActive = solver.makeBoolVarArray(vehicles, "ActiveVehicle");
    costVar = solver.makeIntVar(0, Long.MAX_VALUE, "Cost");
    for (int v = 0; v < vehicles; v++) {
      int s = (int) indexManager.getStartIndex(v);
      int e = (int) indexManager.getEndIndex(v);
      vehicleVars[s].setValue(v);
      vehicleVars[e].setValue(v);
      active[s].setValue(1);
    }
    vehicleEvaluator = new int[vehicles];
    fixedCost = new long[vehicles];
    policy = new int[vehicles];
    allowedVehicles = new int[numIndices][];
    // callback 0 returns zero, as the vehicle cost evaluator of the C++ model
    transitCallbacks.add(null);
  }

  // ----- callbacks -----

  /** Registers a transit callback on unary values <code>values[node]</code>. */
  public int registerUnaryTransitVector(long[] values) {
    final long[] copy = values.clone();
    return registerUnaryTransitCallback(i -> copy[manager.indexToNode(i)]);
  }

  /**
   * Registers a callback of the start index of an arc; the sign (a value of the C++ enum
   * <code>TransitEvaluatorSign</code>, whose constants the OR-Tools 10.0 wrapper does not expose)
   * is a hint, ignored by this implementation.
   */
  public int registerUnaryTransitCallback(LongUnaryOperator callback, int sign) {
    return registerUnaryTransitCallback(callback);
  }

  /** Registers a callback of the start index of an arc. */
  public int registerUnaryTransitCallback(LongUnaryOperator callback) {
    transitCallbacks.add((i, j) -> callback.applyAsLong(i));
    return transitCallbacks.size() - 1;
  }

  /** Registers a transit callback given by a matrix indexed by nodes. */
  public int registerTransitMatrix(long[][] values) {
    final long[][] copy = new long[values.length][];
    for (int i = 0; i < values.length; i++) {
      copy[i] = values[i].clone();
    }
    return registerTransitCallback(
        (i, j) -> copy[manager.indexToNode(i)][manager.indexToNode(j)]);
  }

  /**
   * Registers a callback of the arcs; the sign (a value of the C++ enum
   * <code>TransitEvaluatorSign</code>) is a hint, ignored by this implementation.
   */
  public int registerTransitCallback(LongBinaryOperator callback, int sign) {
    return registerTransitCallback(callback);
  }

  /** Registers a callback of the arcs (from index, to index). */
  public int registerTransitCallback(LongBinaryOperator callback) {
    transitCallbacks.add(callback);
    return transitCallbacks.size() - 1;
  }

  long transitValue(int evaluator, long from, long to) {
    LongBinaryOperator cb = transitCallbacks.get(evaluator);
    return cb == null ? 0 : cb.applyAsLong(from, to);
  }

  private ArcMatrix matrix(int evaluator) {
    ArcMatrix m = matrices.get(evaluator);
    if (m == null) {
      m = new ArcMatrix(numIndices, transitCallbacks.get(evaluator));
      matrices.put(evaluator, m);
    }
    return m;
  }

  // ----- dimensions -----

  /**
   * Adds a dimension with the same transit and capacity for all vehicles; returns false if a
   * dimension of this name exists.
   */
  public boolean addDimension(int evaluatorIndex, long slackMax, long capacity,
      boolean fixStartCumulToZero, String name) {
    return addDimension(evaluatorIndex, slackMax, capacity, fixStartCumulToZero, name, false);
  }

  /**
   * Adds a dimension with the same transit and capacity for all vehicles.
   * <code>hasVariableTransits</code> (a transit which may exceed the callback value, used by
   * OR-Tools to post-process schedules) is accepted and ignored by the engine of this module,
   * which uses the callback transits and the slacks.
   */
  public boolean addDimension(int evaluatorIndex, long slackMax, long capacity,
      boolean fixStartCumulToZero, String name, boolean hasVariableTransits) {
    int[] ev = new int[vehicles];
    Arrays.fill(ev, evaluatorIndex);
    long[] cap = new long[vehicles];
    Arrays.fill(cap, capacity);
    return addDimensionInternal(ev, slackMax, cap, fixStartCumulToZero, name);
  }

  /** Adds a dimension with a transit callback per vehicle. */
  public boolean addDimensionWithVehicleTransits(int[] evaluatorIndices, long slackMax,
      long capacity, boolean fixStartCumulToZero, String name) {
    return addDimensionWithVehicleTransits(evaluatorIndices, slackMax, capacity,
        fixStartCumulToZero, name, false);
  }

  /**
   * Adds a dimension with a transit callback per vehicle; <code>hasVariableTransits</code> is
   * ignored (see {@link #addDimension(int, long, long, boolean, String, boolean)}).
   */
  public boolean addDimensionWithVehicleTransits(int[] evaluatorIndices, long slackMax,
      long capacity, boolean fixStartCumulToZero, String name, boolean hasVariableTransits) {
    long[] cap = new long[vehicles];
    Arrays.fill(cap, capacity);
    return addDimensionInternal(evaluatorIndices, slackMax, cap, fixStartCumulToZero, name);
  }

  /** Adds a dimension with a capacity per vehicle. */
  public boolean addDimensionWithVehicleCapacity(int evaluatorIndex, long slackMax,
      long[] vehicleCapacities, boolean fixStartCumulToZero, String name) {
    return addDimensionWithVehicleCapacity(evaluatorIndex, slackMax, vehicleCapacities,
        fixStartCumulToZero, name, false);
  }

  /**
   * Adds a dimension with a capacity per vehicle; <code>hasVariableTransits</code> is ignored
   * (see {@link #addDimension(int, long, long, boolean, String, boolean)}).
   */
  public boolean addDimensionWithVehicleCapacity(int evaluatorIndex, long slackMax,
      long[] vehicleCapacities, boolean fixStartCumulToZero, String name,
      boolean hasVariableTransits) {
    int[] ev = new int[vehicles];
    Arrays.fill(ev, evaluatorIndex);
    return addDimensionInternal(ev, slackMax, vehicleCapacities, fixStartCumulToZero, name);
  }

  /** Adds a dimension with a transit callback and a capacity per vehicle. */
  public boolean addDimensionWithVehicleTransitAndCapacity(int[] evaluatorIndices, long slackMax,
      long[] vehicleCapacities, boolean fixStartCumulToZero, String name) {
    return addDimensionWithVehicleTransitAndCapacity(evaluatorIndices, slackMax,
        vehicleCapacities, fixStartCumulToZero, name, false);
  }

  /**
   * Adds a dimension with a transit callback and a capacity per vehicle;
   * <code>hasVariableTransits</code> is ignored (see
   * {@link #addDimension(int, long, long, boolean, String, boolean)}).
   */
  public boolean addDimensionWithVehicleTransitAndCapacity(int[] evaluatorIndices, long slackMax,
      long[] vehicleCapacities, boolean fixStartCumulToZero, String name,
      boolean hasVariableTransits) {
    return addDimensionInternal(evaluatorIndices, slackMax, vehicleCapacities,
        fixStartCumulToZero, name);
  }

  private boolean addDimensionInternal(int[] evaluators, long slackMax, long[] capacities,
      boolean fixStartCumulToZero, String name) {
    if (dimensionByName.containsKey(name) || evaluators.length != vehicles
        || capacities.length != vehicles) {
      return false;
    }
    for (long c : capacities) {
      if (c < 0) {
        return false;
      }
    }
    Dimension d = new Dimension(this, name, dimensions.size(), evaluators,
        slackMax, capacities, fixStartCumulToZero);
    dimensions.add(d);
    dimensionByName.put(name, d);
    return true;
  }

  /** Adds a dimension whose transit is <code>value</code> on every arc, with slack. */
  public IntBoolPair addConstantDimensionWithSlack(long value, long capacity, long slackMax,
      boolean fixStartCumulToZero, String name) {
    return addConstantDimensionWithSlack(value, capacity, slackMax, fixStartCumulToZero, name,
        false);
  }

  /**
   * Adds a dimension whose transit is <code>value</code> on every arc, with slack;
   * <code>hasVariableTransits</code> is ignored (see
   * {@link #addDimension(int, long, long, boolean, String, boolean)}).
   */
  public IntBoolPair addConstantDimensionWithSlack(long value, long capacity, long slackMax,
      boolean fixStartCumulToZero, String name, boolean hasVariableTransits) {
    int evaluator = registerUnaryTransitCallback(i -> value);
    return new IntBoolPair(evaluator,
        addDimension(evaluator, slackMax, capacity, fixStartCumulToZero, name));
  }

  /** Adds a dimension whose transit is <code>value</code> on every arc. */
  public IntBoolPair addConstantDimension(long value, long capacity, boolean fixStartCumulToZero,
      String name) {
    return addConstantDimensionWithSlack(value, capacity, 0, fixStartCumulToZero, name);
  }

  /** Adds a dimension whose transit from node i is <code>values[i]</code>. */
  public IntBoolPair addVectorDimension(long[] values, long capacity, boolean fixStartCumulToZero,
      String name) {
    int evaluator = registerUnaryTransitVector(values);
    return new IntBoolPair(evaluator,
        addDimension(evaluator, 0, capacity, fixStartCumulToZero, name));
  }

  /** Adds a dimension whose transit from node i to node j is <code>values[i][j]</code>. */
  public IntBoolPair addMatrixDimension(long[][] values, long capacity,
      boolean fixStartCumulToZero, String name) {
    int evaluator = registerTransitMatrix(values);
    return new IntBoolPair(evaluator,
        addDimension(evaluator, 0, capacity, fixStartCumulToZero, name));
  }

  /** Whether a dimension of this name exists. */
  public boolean hasDimension(String dimensionName) {
    return dimensionByName.containsKey(dimensionName);
  }

  /** The dimension of this name; throws if it doesn't exist. */
  public Dimension getDimensionOrDie(String dimensionName) {
    Dimension d = dimensionByName.get(dimensionName);
    if (d == null) {
      throw new IllegalArgumentException("Unknown dimension " + dimensionName);
    }
    return d;
  }

  /** The dimension of this name, or null. */
  public Dimension getMutableDimension(String dimensionName) {
    return dimensionByName.get(dimensionName);
  }

  /** Records the primary dimension (no effect on the engine). */
  public void setPrimaryConstrainedDimension(String dimensionName) {
    primaryDimension = dimensionName;
  }

  /** The primary dimension. */
  public String getPrimaryConstrainedDimension() {
    return primaryDimension;
  }

  // ----- disjunctions, pickups and deliveries -----

  /**
   * Adds a disjunction on the indices, as in OR-Tools 10.0: at most <code>maxCardinality</code>
   * of them are active. A negative penalty or <code>Long.MAX_VALUE</code> makes the disjunction
   * hard (exactly <code>maxCardinality</code> active indices); otherwise
   * <code>maxCardinality</code> is a soft minimum, and <code>penalty</code> is paid once if fewer
   * indices are active ({@link #PENALIZE_ONCE}) or for every missing active index
   * ({@link #PENALIZE_PER_INACTIVE}). The indices must not be vehicle starts or ends.
   *
   * @return the index of the disjunction
   */
  public int addDisjunction(long[] indices, long penalty, long maxCardinality,
      int penaltyCostBehavior) {
    if (maxCardinality < 1) {
      throw new IllegalArgumentException("max_cardinality must be at least 1: " + maxCardinality);
    }
    int d = makeDisjunction(indices);
    setDisjunctionHardMaximum(d, maxCardinality);
    if (penalty < 0 || penalty == Long.MAX_VALUE) {
      setDisjunctionHardMinimum(d, maxCardinality);
    } else {
      setDisjunctionSoftMinimum(d, maxCardinality, penalty, penaltyCostBehavior);
    }
    return d;
  }

  /**
   * A disjunction whose soft minimum penalty is paid once (see
   * {@link #addDisjunction(long[], long, long, int)}).
   */
  public int addDisjunction(long[] indices, long penalty, long maxCardinality) {
    return addDisjunction(indices, penalty, maxCardinality, PENALIZE_ONCE);
  }

  /** A disjunction with at most one active index (exactly one if the penalty is negative). */
  public int addDisjunction(long[] indices, long penalty) {
    return addDisjunction(indices, penalty, 1);
  }

  /** A hard disjunction: exactly one index is active. */
  public int addDisjunction(long[] indices) {
    return addDisjunction(indices, NO_PENALTY);
  }

  /**
   * Creates a disjunction without cardinality constraints (minimum 0, maximum the number of
   * indices, no soft bounds); the bounds are set by the <code>setDisjunction...</code> methods.
   *
   * @return the index of the disjunction
   */
  public int makeDisjunction(long[] indices) {
    for (long i : indices) {
      if (i < 0 || i >= size || isStart(i)) {
        throw new IllegalArgumentException("start and end indices can't be in a disjunction");
      }
    }
    disjunctions.add(new Disjunction(indices.clone()));
    return disjunctions.size() - 1;
  }

  private Disjunction disjunction(int disjunctionIndex) {
    return disjunctions.get(disjunctionIndex);
  }

  /**
   * The disjunction, after checking that <code>cardinality</code> is between 0 and its number of
   * indices, as the C++ setters do.
   */
  private Disjunction disjunction(int disjunctionIndex, long cardinality, String what) {
    Disjunction d = disjunction(disjunctionIndex);
    if (cardinality < 0 || cardinality > d.indices.length) {
      throw new IllegalArgumentException(what + " " + cardinality
          + " of the disjunction must be between 0 and its number of indices "
          + d.indices.length);
    }
    return d;
  }

  /** Sets the maximal number of active indices of the disjunction. */
  public void setDisjunctionHardMaximum(int disjunctionIndex, long maxCardinality) {
    disjunction(disjunctionIndex, maxCardinality, "the hard maximum").maxCardinality =
        maxCardinality;
  }

  /** Sets the minimal number of active indices of the disjunction. */
  public void setDisjunctionHardMinimum(int disjunctionIndex, long minCardinality) {
    disjunction(disjunctionIndex, minCardinality, "the hard minimum").minCardinality =
        minCardinality;
  }

  /**
   * Sets a soft maximum of the number <code>n</code> of active indices: the cost is
   * <code>penalty * max(0, n - softMaxCardinality)</code> with {@link #PENALIZE_PER_INACTIVE},
   * <code>penalty</code> if <code>n &gt; softMaxCardinality</code> with {@link #PENALIZE_ONCE}.
   */
  public void setDisjunctionSoftMaximum(int disjunctionIndex, long softMaxCardinality,
      long penalty, int penaltyCostBehavior) {
    if (penalty < 0) {
      throw new IllegalArgumentException("the penalty must be non-negative: " + penalty);
    }
    Disjunction d = disjunction(disjunctionIndex, softMaxCardinality, "the soft maximum");
    d.softMaxCardinality = softMaxCardinality;
    d.softMaxPenalty = penalty;
    d.softMaxPenaltyType = penaltyCostBehavior;
  }

  /**
   * Sets a soft minimum of the number <code>n</code> of active indices: the cost is
   * <code>penalty * max(0, softMinCardinality - n)</code> with {@link #PENALIZE_PER_INACTIVE},
   * <code>penalty</code> if <code>n &lt; softMinCardinality</code> with {@link #PENALIZE_ONCE}.
   */
  public void setDisjunctionSoftMinimum(int disjunctionIndex, long softMinCardinality,
      long penalty, int penaltyCostBehavior) {
    if (penalty < 0) {
      throw new IllegalArgumentException("the penalty must be non-negative: " + penalty);
    }
    Disjunction d = disjunction(disjunctionIndex, softMinCardinality, "the soft minimum");
    d.softMinCardinality = softMinCardinality;
    d.softMinPenalty = penalty;
    d.softMinPenaltyType = penaltyCostBehavior;
  }

  /** The disjunctions of an index. */
  public int[] getDisjunctionIndices(long index) {
    List<Integer> r = new ArrayList<>();
    for (int d = 0; d < disjunctions.size(); d++) {
      for (long i : disjunctions.get(d).indices) {
        if (i == index) {
          r.add(d);
          break;
        }
      }
    }
    int[] a = new int[r.size()];
    for (int k = 0; k < a.length; k++) {
      a[k] = r.get(k);
    }
    return a;
  }

  /** The maximal number of active indices of the disjunction. */
  public long getDisjunctionMaxCardinality(int index) {
    return disjunction(index).maxCardinality;
  }

  /** The minimal number of active indices of the disjunction. */
  public long getDisjunctionMinCardinality(int index) {
    return disjunction(index).minCardinality;
  }

  /** The soft maximum of the number of active indices (<code>Long.MAX_VALUE</code> if none). */
  public long getDisjunctionSoftMaxCardinality(int index) {
    return disjunction(index).softMaxCardinality;
  }

  /** The soft minimum of the number of active indices (0 if none). */
  public long getDisjunctionSoftMinCardinality(int index) {
    return disjunction(index).softMinCardinality;
  }

  /** The penalty of the soft maximum. */
  public long getDisjunctionSoftMaxPenalty(int index) {
    return disjunction(index).softMaxPenalty;
  }

  /** The penalty of the soft minimum. */
  public long getDisjunctionSoftMinPenalty(int index) {
    return disjunction(index).softMinPenalty;
  }

  /**
   * The penalty of the disjunction: -1 if its minimal cardinality is positive (a hard
   * disjunction), the penalty of the soft minimum otherwise.
   */
  public long getDisjunctionPenalty(int index) {
    Disjunction d = disjunction(index);
    return d.minCardinality > 0 ? NO_PENALTY : d.softMinPenalty;
  }

  /** The penalty cost behavior of the soft minimum. */
  public int getDisjunctionSoftMinPenaltyCostBehavior(int index) {
    return disjunction(index).softMinPenaltyType;
  }

  /** The penalty cost behavior of the soft maximum. */
  public int getDisjunctionSoftMaxPenaltyCostBehavior(int index) {
    return disjunction(index).softMaxPenaltyType;
  }

  /** The number of disjunctions. */
  public int getNumberOfDisjunctions() {
    return disjunctions.size();
  }

  /**
   * Sets all cardinalities (hard and soft) of the disjunctions whose active variables are all
   * forced to 0 to zero.
   */
  public void ignoreDisjunctionsAlreadyForcedToZero() {
    for (Disjunction d : disjunctions) {
      boolean potentiallyActive = false;
      for (long i : d.indices) {
        if (active[(int) i].max() > 0) {
          potentiallyActive = true;
          break;
        }
      }
      if (!potentiallyActive) {
        d.maxCardinality = 0;
        d.minCardinality = 0;
        d.softMaxCardinality = 0;
        d.softMinCardinality = 0;
      }
    }
  }

  /**
   * The cost of leaving the index unperformed: <code>Long.MAX_VALUE</code> if it is forced
   * active, else the soft minimum penalty of its single disjunction (0 if it has no soft
   * minimum), 0 if it is in no or in several disjunctions.
   */
  public long unperformedPenalty(long varIndex) {
    return unperformedPenaltyOrValue(0, varIndex);
  }

  /**
   * As {@link #unperformedPenalty(long)}, with <code>defaultValue</code> for an index which is in
   * no or in several disjunctions. An index whose single disjunction requires all of its indices
   * counts as forced active.
   */
  public long unperformedPenaltyOrValue(long defaultValue, long varIndex) {
    if (active[(int) varIndex].min() == 1) {
      return Long.MAX_VALUE;
    }
    int[] ds = getDisjunctionIndices(varIndex);
    if (ds.length != 1) {
      return defaultValue;
    }
    Disjunction d = disjunctions.get(ds[0]);
    if (d.minCardinality >= d.indices.length) {
      return Long.MAX_VALUE;
    }
    return Math.max(0, d.softMinCardinality > 0 ? d.softMinPenalty : 0);
  }

  /** The first start index. */
  public long getDepot() {
    return vehicles > 0 ? start(0) : -1;
  }

  /** Requires the pickup and the delivery on the same route, the pickup first. */
  public void addPickupAndDelivery(long pickup, long delivery) {
    pairs.add(new int[] {(int) pickup, (int) delivery});
  }

  private boolean isPickup(long nodeIndex) {
    for (int[] p : pairs) {
      if (p[0] == nodeIndex) {
        return true;
      }
    }
    return false;
  }

  private boolean isDelivery(long nodeIndex) {
    for (int[] p : pairs) {
      if (p[1] == nodeIndex) {
        return true;
      }
    }
    return false;
  }

  /** Sets the pickup and delivery policy (one of the PICKUP_AND_DELIVERY constants). */
  public void setPickupAndDeliveryPolicyOfAllVehicles(int policy) {
    Arrays.fill(this.policy, policy);
  }

  /** Sets the pickup and delivery policy of a vehicle. */
  public void setPickupAndDeliveryPolicyOfVehicle(int policy, int vehicle) {
    this.policy[vehicle] = policy;
  }

  /** The pickup and delivery policy of a vehicle. */
  public int getPickupAndDeliveryPolicyOfVehicle(int vehicle) {
    return policy[vehicle];
  }

  /** The number of indices which are not in a pickup and delivery pair. */
  public int getNumOfSingletonNodes() {
    int count = 0;
    for (int i = 0; i < size; i++) {
      if (!isStart(i) && !isPickup(i) && !isDelivery(i)) {
        count++;
      }
    }
    return count;
  }

  /**
   * Restricts the vehicles which may visit the index to <code>vehicles</code>; an empty array
   * allows all vehicles. Vehicle numbers out of range are accepted (and allow no vehicle of the
   * model).
   */
  public void setAllowedVehiclesForIndex(int[] vehicles, long index) {
    allowedVehicles[(int) index] = vehicles.length == 0 ? null : vehicles.clone();
  }

  /**
   * Whether the vehicle is allowed for the index by {@link #setAllowedVehiclesForIndex}: true if
   * no vehicle set was given (the domain of the vehicle variable is not consulted, as in
   * OR-Tools).
   */
  public boolean isVehicleAllowedForIndex(int vehicle, long index) {
    int[] allowed = allowedVehicles[(int) index];
    if (allowed == null) {
      return true;
    }
    for (int v : allowed) {
      if (v == vehicle) {
        return true;
      }
    }
    return false;
  }

  // ----- costs -----

  /** The arc cost of all vehicles is given by the callback. */
  public void setArcCostEvaluatorOfAllVehicles(int evaluatorIndex) {
    Arrays.fill(vehicleEvaluator, evaluatorIndex);
  }

  /** The arc cost of a vehicle is given by the callback. */
  public void setArcCostEvaluatorOfVehicle(int evaluatorIndex, int vehicle) {
    vehicleEvaluator[vehicle] = evaluatorIndex;
  }

  /** The fixed cost of every vehicle, paid if its route is not empty. */
  public void setFixedCostOfAllVehicles(long cost) {
    Arrays.fill(fixedCost, cost);
  }

  /** The fixed cost of a vehicle, paid if its route is not empty. */
  public void setFixedCostOfVehicle(long cost, int vehicle) {
    fixedCost[vehicle] = cost;
  }

  /** The fixed cost of a vehicle. */
  public long getFixedCostOfVehicle(int vehicle) {
    return fixedCost[vehicle];
  }

  /**
   * The cost of the arc for a vehicle, as in OR-Tools: the arc cost callback plus the span cost
   * coefficients times the dimension transits, plus the fixed cost on the arc leaving the start
   * of a non-empty route; 0 for an empty route, for <code>from == to</code> and for a negative
   * vehicle.
   */
  public long getArcCostForVehicle(long fromIndex, long toIndex, long vehicle) {
    if (fromIndex == toIndex || vehicle < 0) {
      return 0;
    }
    int v = (int) vehicle;
    boolean fromStart = isStart(fromIndex);
    boolean toEnd = isEnd(toIndex);
    if (fromStart && toEnd) {
      return 0;
    }
    long cost = transitValue(vehicleEvaluator[v], fromIndex, toIndex);
    for (Dimension d : dimensions) {
      if (d.spanCost[v] != 0) {
        cost = CapOps.capAdd(cost,
            CapOps.capProd(d.spanCost[v], d.getTransitValue(fromIndex, toIndex, v)));
      }
    }
    if (fromStart) {
      cost = CapOps.capAdd(cost, fixedCost[v]);
    }
    return cost;
  }

  /** Whether all vehicles have the same costs. */
  public boolean costsAreHomogeneousAcrossVehicles() {
    for (int v = 1; v < vehicles; v++) {
      if (getCostClassIndexOfVehicle(v) != getCostClassIndexOfVehicle(0)) {
        return false;
      }
    }
    return true;
  }

  /** The arc cost for vehicle 0. */
  public long getHomogeneousCost(long fromIndex, long toIndex) {
    return getArcCostForVehicle(fromIndex, toIndex, 0);
  }

  /** The arc cost used by the first solution heuristics (vehicle 0). */
  public long getArcCostForFirstSolution(long fromIndex, long toIndex) {
    return getHomogeneousCost(fromIndex, toIndex);
  }

  /** The arc cost for the vehicles of a cost class. */
  public long getArcCostForClass(long fromIndex, long toIndex, long costClassIndex) {
    for (int v = 0; v < vehicles; v++) {
      if (getCostClassIndexOfVehicle(v) == costClassIndex) {
        return getArcCostForVehicle(fromIndex, toIndex, v);
      }
    }
    return 0;
  }

  private long[] costSignature(int v) {
    long[] sig = new long[2 + 2 * dimensions.size()];
    sig[0] = vehicleEvaluator[v];
    sig[1] = fixedCost[v];
    for (int d = 0; d < dimensions.size(); d++) {
      sig[2 + 2 * d] = dimensions.get(d).spanCost[v];
      sig[3 + 2 * d] = dimensions.get(d).spanCost[v] == 0 ? -1
          : dimensions.get(d).evaluatorOfVehicle[v];
    }
    return sig;
  }

  /** The cost class of a vehicle: vehicles with the same arc and span costs share a class. */
  public int getCostClassIndexOfVehicle(long vehicle) {
    List<long[]> classes = new ArrayList<>();
    for (int v = 0; v <= vehicle; v++) {
      long[] sig = costSignature(v);
      int found = -1;
      for (int c = 0; c < classes.size(); c++) {
        if (Arrays.equals(classes.get(c), sig)) {
          found = c;
          break;
        }
      }
      if (found < 0) {
        classes.add(sig);
        found = classes.size() - 1;
      }
      if (v == vehicle) {
        return found;
      }
    }
    return 0;
  }

  /** Whether a vehicle has the given cost class. */
  public boolean hasVehicleWithCostClassIndex(int costClassIndex) {
    for (int v = 0; v < vehicles; v++) {
      if (getCostClassIndexOfVehicle(v) == costClassIndex) {
        return true;
      }
    }
    return false;
  }

  /** The number of cost classes. */
  public int getCostClassesCount() {
    int max = -1;
    for (int v = 0; v < vehicles; v++) {
      max = Math.max(max, getCostClassIndexOfVehicle(v));
    }
    return max + 1;
  }

  /** The number of cost classes with a non-zero arc cost callback. */
  public int getNonZeroCostClassesCount() {
    int count = 0;
    for (int c = 0; c < getCostClassesCount(); c++) {
      for (int v = 0; v < vehicles; v++) {
        if (getCostClassIndexOfVehicle(v) == c) {
          if (vehicleEvaluator[v] != 0) {
            count++;
          }
          break;
        }
      }
    }
    return count;
  }

  /**
   * The vehicle class signature of a vehicle, from the fields of the C++ vehicle classes which
   * this implementation models: cost class, fixed cost, start and end nodes, per dimension the
   * start and end cumul ranges, the capacity and the transit evaluator, and the indices the
   * vehicle may visit (vehicle variable domains and allowed vehicles).
   */
  private long[] vehicleClassSignature(int v) {
    long[] sig = new long[5 + 6 * dimensions.size() + size];
    int k = 0;
    sig[k++] = getCostClassIndexOfVehicle(v);
    sig[k++] = fixedCost[v];
    sig[k++] = manager.indexToNode(start(v));
    sig[k++] = manager.indexToNode(end(v));
    sig[k++] = 0; // used when empty (always false in this implementation)
    for (Dimension d : dimensions) {
      IntVar startCumul = d.cumuls[(int) start(v)];
      IntVar endCumul = d.cumuls[(int) end(v)];
      sig[k++] = startCumul.min();
      sig[k++] = startCumul.max();
      sig[k++] = endCumul.min();
      sig[k++] = endCumul.max();
      sig[k++] = d.capacity[v];
      sig[k++] = d.evaluatorOfVehicle[v];
    }
    for (int i = 0; i < size; i++) {
      sig[k++] = isStart(i) || (vehicleVars[i].contains(v) && isVehicleAllowedForIndex(v, i)) ? 1
          : 0;
    }
    return sig;
  }

  /** The vehicle class of each vehicle; vehicles with equal signatures share a class. */
  private int[] vehicleClasses() {
    int[] classOf = new int[vehicles];
    List<long[]> classes = new ArrayList<>();
    for (int v = 0; v < vehicles; v++) {
      long[] sig = vehicleClassSignature(v);
      int found = -1;
      for (int c = 0; c < classes.size(); c++) {
        if (Arrays.equals(classes.get(c), sig)) {
          found = c;
          break;
        }
      }
      if (found < 0) {
        classes.add(sig);
        found = classes.size() - 1;
      }
      classOf[v] = found;
    }
    return classOf;
  }

  /** The vehicle class of a vehicle. */
  public int getVehicleClassIndexOfVehicle(long vehicle) {
    return vehicleClasses()[(int) vehicle];
  }

  /** The number of vehicle classes. */
  public int getVehicleClassesCount() {
    int max = -1;
    for (int c : vehicleClasses()) {
      max = Math.max(max, c);
    }
    return max + 1;
  }

  // ----- finalizers and callbacks -----

  /** Adds a callback which is run at every solution found by the search. */
  public void addAtSolutionCallback(Runnable callback) {
    atSolutionCallbacks.add(callback);
  }

  /** Adds a callback which is run at every solution; the flag is ignored. */
  public void addAtSolutionCallback(Runnable callback, boolean trackUncheckedNeighbors) {
    addAtSolutionCallback(callback);
  }

  /**
   * The cumul variable is set to its smallest feasible value in the solution (finalizers on
   * other variables are ignored by this implementation).
   */
  public void addVariableMinimizedByFinalizer(IntVar var) {
    finalizerVars.add(var);
    finalizerKinds.add(new long[] {0, 0});
  }

  /** The cumul variable is set to its largest feasible value in the solution. */
  public void addVariableMaximizedByFinalizer(IntVar var) {
    finalizerVars.add(var);
    finalizerKinds.add(new long[] {1, 0});
  }

  /** The cumul variable is set to the feasible value closest to <code>target</code>. */
  public void addVariableTargetToFinalizer(IntVar var, long target) {
    finalizerVars.add(var);
    finalizerKinds.add(new long[] {2, target});
  }

  /** Adds the variable (with its model domain) to the assignments returned by the solver. */
  public void addToAssignment(IntVar var) {
    extraVars.add(var);
  }

  /** Adds the interval (with its model domain) to the assignments returned by the solver. */
  public void addIntervalToAssignment(IntervalVar interval) {
    extraIntervals.add(interval);
  }

  // ----- variables and structure -----

  /** The start index of a vehicle. */
  public long start(int vehicle) {
    return manager.getStartIndex(vehicle);
  }

  /** The end index of a vehicle. */
  public long end(int vehicle) {
    return manager.getEndIndex(vehicle);
  }

  /** Whether the index is a vehicle start. */
  public boolean isStart(long index) {
    if (index < 0 || index >= size) {
      return false;
    }
    for (int v = 0; v < vehicles; v++) {
      if (start(v) == index) {
        return true;
      }
    }
    return false;
  }

  /** Whether the index is a vehicle end. */
  public boolean isEnd(long index) {
    return index >= size;
  }

  /** The successor of the index in the assignment. */
  public long next(Assignment assignment, long index) {
    return assignment.value(nextVar(index));
  }

  /** Whether the route of the vehicle is not empty in the assignment. */
  public boolean isVehicleUsed(Assignment assignment, int vehicle) {
    return assignment.value(nextVar(start(vehicle))) != end(vehicle);
  }

  /** The successor variables. */
  public IntVar[] nexts() {
    return nexts.clone();
  }

  /** The vehicle variables (-1 for inactive indices). */
  public IntVar[] vehicleVars() {
    return vehicleVars.clone();
  }

  /** The successor variable of an index. */
  public IntVar nextVar(long index) {
    return nexts[(int) index];
  }

  /** The 0/1 variable telling whether an index is visited. */
  public IntVar activeVar(long index) {
    return active[(int) index];
  }

  /** The 0/1 variable telling whether the route of a vehicle is not empty. */
  public IntVar activeVehicleVar(int vehicle) {
    return vehicleActive[vehicle];
  }

  /** The vehicle variable of an index (-1 if inactive). */
  public IntVar vehicleVar(long index) {
    return vehicleVars[(int) index];
  }

  /** The variable equal to the objective of a solution (bound in the solution callbacks). */
  public IntVar costVar() {
    return costVar;
  }

  /** The underlying constraint solver, where constraints on routing variables can be posted. */
  public Solver solver() {
    return solver;
  }

  /** The number of nodes. */
  public int nodes() {
    return manager.getNumberOfNodes();
  }

  /** The number of vehicles. */
  public int vehicles() {
    return vehicles;
  }

  /** The number of indices with a successor variable (all indices except the ends). */
  public long size() {
    return size;
  }

  /** The status of the last search. */
  public RoutingSearchStatus.Value status() {
    return status;
  }

  /** Whether the model is closed. */
  boolean isClosed() {
    return closed;
  }

  /** Closes the model; later changes to the model are ignored by this implementation. */
  public void closeModel() {
    closed = true;
  }

  /** Closes the model. */
  public void closeModelWithParameters(RoutingSearchParameters searchParameters) {
    closeModel();
  }

  // ----- building the engine problem -----

  private static BitSet domainBits(IntVar var, int offset, int count) {
    if (var.min() <= -offset && var.max() >= count - 1 - offset && var.size() == count) {
      return null;
    }
    BitSet b = new BitSet(count);
    for (int k = 0; k < count; k++) {
      if (var.contains(k - offset)) {
        b.set(k);
      }
    }
    return b;
  }

  private RoutingProblem buildProblem() {
    RoutingProblem p = new RoutingProblem();
    p.size = size;
    p.vehicles = vehicles;
    p.numIndices = numIndices;
    p.start = new int[vehicles];
    p.end = new int[vehicles];
    p.vehicleOfStartEnd = new int[numIndices];
    Arrays.fill(p.vehicleOfStartEnd, -1);
    for (int v = 0; v < vehicles; v++) {
      p.start[v] = (int) start(v);
      p.end[v] = (int) end(v);
      p.vehicleOfStartEnd[p.start[v]] = v;
      p.vehicleOfStartEnd[p.end[v]] = v;
    }
    p.arcCost = new ArcMatrix[vehicles];
    p.costClass = new int[vehicles];
    Map<Integer, Integer> classOfEvaluator = new HashMap<>();
    for (int v = 0; v < vehicles; v++) {
      p.arcCost[v] = matrix(vehicleEvaluator[v]);
      Integer c = classOfEvaluator.get(vehicleEvaluator[v]);
      if (c == null) {
        c = classOfEvaluator.size();
        classOfEvaluator.put(vehicleEvaluator[v], c);
      }
      p.costClass[v] = c;
    }
    p.fixedCost = fixedCost.clone();
    p.usedWhenEmpty = new boolean[vehicles];
    int nd = disjunctions.size();
    p.disjunctionNodes = new int[nd][];
    p.disjunctionMinCardinality = new int[nd];
    p.disjunctionMaxCardinality = new int[nd];
    p.disjunctionSoftMinCardinality = new int[nd];
    p.disjunctionSoftMinPenalty = new long[nd];
    p.disjunctionSoftMinPerInactive = new boolean[nd];
    p.disjunctionSoftMaxCardinality = new int[nd];
    p.disjunctionSoftMaxPenalty = new long[nd];
    p.disjunctionSoftMaxPerInactive = new boolean[nd];
    List<List<Integer>> ofNode = new ArrayList<>();
    for (int i = 0; i < numIndices; i++) {
      ofNode.add(new ArrayList<>());
    }
    for (int d = 0; d < nd; d++) {
      Disjunction disj = disjunctions.get(d);
      long[] idx = disj.indices;
      p.disjunctionNodes[d] = new int[idx.length];
      for (int k = 0; k < idx.length; k++) {
        p.disjunctionNodes[d][k] = (int) idx[k];
        ofNode.get((int) idx[k]).add(d);
      }
      p.disjunctionMinCardinality[d] = toInt(disj.minCardinality);
      p.disjunctionMaxCardinality[d] = toInt(disj.maxCardinality);
      p.disjunctionSoftMinCardinality[d] = toInt(disj.softMinCardinality);
      p.disjunctionSoftMinPenalty[d] = disj.softMinPenalty;
      p.disjunctionSoftMinPerInactive[d] = disj.softMinPenaltyType == PENALIZE_PER_INACTIVE;
      p.disjunctionSoftMaxCardinality[d] = toInt(disj.softMaxCardinality);
      p.disjunctionSoftMaxPenalty[d] = disj.softMaxPenalty;
      p.disjunctionSoftMaxPerInactive[d] = disj.softMaxPenaltyType == PENALIZE_PER_INACTIVE;
    }
    p.disjunctionsOfNode = new int[numIndices][];
    for (int i = 0; i < numIndices; i++) {
      List<Integer> l = ofNode.get(i);
      p.disjunctionsOfNode[i] = new int[l.size()];
      for (int k = 0; k < l.size(); k++) {
        p.disjunctionsOfNode[i][k] = l.get(k);
      }
    }
    p.forcedActive = new boolean[numIndices];
    p.forcedInactive = new boolean[numIndices];
    p.allowedNext = new BitSet[numIndices];
    p.allowedVehicles = new BitSet[numIndices];
    for (int i = 0; i < size; i++) {
      if (active[i].min() == 1 || vehicleVars[i].min() >= 0) {
        p.forcedActive[i] = true;
      }
      if (active[i].max() == 0 || vehicleVars[i].max() < 0) {
        p.forcedInactive[i] = true;
      }
      p.allowedNext[i] = domainBits(nexts[i], 0, numIndices);
    }
    for (int i = 0; i < numIndices; i++) {
      IntVar vv = vehicleVars[i];
      BitSet b = new BitSet(vehicles);
      boolean all = true;
      for (int v = 0; v < vehicles; v++) {
        if (vv.contains(v) && (isStart(i) || isEnd(i) || isVehicleAllowedForIndex(v, i))) {
          b.set(v);
        } else {
          all = false;
        }
      }
      p.allowedVehicles[i] = all ? null : b;
    }
    p.pairPickup = new int[pairs.size()];
    p.pairDelivery = new int[pairs.size()];
    p.pairOfNode = new int[numIndices];
    Arrays.fill(p.pairOfNode, -1);
    for (int q = 0; q < pairs.size(); q++) {
      p.pairPickup[q] = pairs.get(q)[0];
      p.pairDelivery[q] = pairs.get(q)[1];
      p.pairOfNode[p.pairPickup[q]] = q;
      p.pairOfNode[p.pairDelivery[q]] = q;
    }
    p.policy = policy.clone();
    p.maxActiveVehicles = vehicles;
    p.dims = new DimensionData[dimensions.size()];
    for (int d = 0; d < dimensions.size(); d++) {
      p.dims[d] = dimensionData(dimensions.get(d));
    }
    p.sideConstraints = buildSideConstraints(p);
    return p;
  }

  private static int toInt(long value) {
    return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, value));
  }

  private DimensionData dimensionData(Dimension rd) {
    DimensionData d = new DimensionData();
    d.name = rd.name;
    d.transitOfVehicle = new ArcMatrix[vehicles];
    for (int v = 0; v < vehicles; v++) {
      d.transitOfVehicle[v] = matrix(rd.evaluatorOfVehicle[v]);
    }
    d.slackMax = rd.slackMax;
    d.capacity = rd.capacity.clone();
    d.cumulMin = new long[numIndices];
    d.cumulMax = new long[numIndices];
    d.allowed = new long[numIndices][];
    for (int i = 0; i < numIndices; i++) {
      IntVar c = rd.cumuls[i];
      d.cumulMin[i] = c.min();
      d.cumulMax[i] = c.max();
      CpVar x = ACCESS.cpVar(c);
      if (x.hasHoles()) {
        List<Long> bounds = new ArrayList<>();
        long value = x.min();
        while (value <= x.max()) {
          long lo = x.nextValue(value);
          if (lo == Long.MAX_VALUE) {
            break;
          }
          long hi = lo;
          while (hi < x.max() && x.contains(hi + 1)) {
            hi++;
          }
          bounds.add(lo);
          bounds.add(hi);
          value = hi + 1;
        }
        long[] a = new long[bounds.size()];
        for (int k = 0; k < a.length; k++) {
          a[k] = bounds.get(k);
        }
        d.allowed[i] = a;
      }
    }
    d.spanCost = rd.spanCost.clone();
    d.slackCost = rd.slackCost.clone();
    d.globalSpanCost = rd.globalSpanCost;
    d.spanUpperBound = rd.spanUpperBound.clone();
    d.softSpanBound = new long[vehicles];
    d.softSpanCost = new long[vehicles];
    d.quadSoftSpanBound = new long[vehicles];
    d.quadSoftSpanCost = new long[vehicles];
    for (int v = 0; v < vehicles; v++) {
      BoundCost b = rd.softSpanUpperBound[v];
      if (b != null) {
        d.softSpanBound[v] = b.getBound();
        d.softSpanCost[v] = b.getCost();
      }
      BoundCost q = rd.quadraticSoftSpanUpperBound[v];
      if (q != null) {
        d.quadSoftSpanBound[v] = q.getBound();
        d.quadSoftSpanCost[v] = q.getCost();
      }
    }
    d.softUpperBound = rd.softUpperBound.clone();
    d.softUpperCost = rd.softUpperCost.clone();
    d.softLowerBound = rd.softLowerBound.clone();
    d.softLowerCost = rd.softLowerCost.clone();
    d.breaks = new DimensionData.BreakSpec[vehicles][];
    d.visitTransit = new long[vehicles][];
    d.preTravel = new ArcMatrix[vehicles];
    for (int v = 0; v < vehicles; v++) {
      IntervalVar[] bs = rd.breaks[v];
      if (bs == null || bs.length == 0) {
        d.breaks[v] = new DimensionData.BreakSpec[0];
        continue;
      }
      IntervalVar[] sorted = bs.clone();
      Arrays.sort(sorted, (a, b) -> Long.compare(a.startMin(), b.startMin()));
      rd.breaks[v] = sorted;
      d.breaks[v] = new DimensionData.BreakSpec[sorted.length];
      for (int k = 0; k < sorted.length; k++) {
        IntervalVar iv = sorted[k];
        d.breaks[v][k] = new DimensionData.BreakSpec(iv.startMin(), iv.startMax(),
            iv.durationMin(), !iv.mustBePerformed());
      }
      d.visitTransit[v] = rd.visitTransits[v];
      if (rd.preTravelEvaluator[v] >= 0) {
        d.preTravel[v] = matrix(rd.preTravelEvaluator[v]);
      }
    }
    long[][] prec = new long[rd.precedences.size()][];
    for (int k = 0; k < prec.length; k++) {
      prec[k] = rd.precedences.get(k).clone();
    }
    d.precedences = prec;
    return d;
  }

  // ----- user constraints on routing variables -----

  /** The role of a routing variable for the side constraint checker. */
  private static final int ROLE_NEXT = 0;

  private static final int ROLE_VEHICLE = 1;

  private static final int ROLE_ACTIVE = 2;

  private static final int ROLE_VEHICLE_ACTIVE = 3;

  private static final int ROLE_CUMUL = 4;

  private static final int ROLE_SLACK = 5;

  private static final int ROLE_TRANSIT = 6;

  private static final int ROLE_COST = 7;

  private IdentityHashMap<CpVar, int[]> routingVarRoles() {
    IdentityHashMap<CpVar, int[]> roles = new IdentityHashMap<>();
    for (int i = 0; i < size; i++) {
      roles.put(ACCESS.cpVar(nexts[i]), new int[] {ROLE_NEXT, i, 0});
      roles.put(ACCESS.cpVar(active[i]), new int[] {ROLE_ACTIVE, i, 0});
    }
    for (int i = 0; i < numIndices; i++) {
      roles.put(ACCESS.cpVar(vehicleVars[i]), new int[] {ROLE_VEHICLE, i, 0});
    }
    for (int v = 0; v < vehicles; v++) {
      roles.put(ACCESS.cpVar(vehicleActive[v]), new int[] {ROLE_VEHICLE_ACTIVE, v, 0});
    }
    roles.put(ACCESS.cpVar(costVar), new int[] {ROLE_COST, 0, 0});
    for (Dimension d : dimensions) {
      for (int i = 0; i < numIndices; i++) {
        roles.put(ACCESS.cpVar(d.cumuls[i]), new int[] {ROLE_CUMUL, i, d.index});
      }
      for (int i = 0; i < size; i++) {
        roles.put(ACCESS.cpVar(d.slacks[i]), new int[] {ROLE_SLACK, i, d.index});
        if (!roles.containsKey(ACCESS.cpVar(d.transits[i]))) {
          roles.put(ACCESS.cpVar(d.transits[i]), new int[] {ROLE_TRANSIT, i, d.index});
        }
      }
    }
    return roles;
  }

  private SideConstraints buildSideConstraints(RoutingProblem p) {
    CpStore store = ACCESS.store(solver);
    List<Propagator> props = store.propagators();
    if (props.isEmpty()) {
      return null;
    }
    IdentityHashMap<CpVar, int[]> roles = routingVarRoles();
    // propagators connected (through shared variables) to routing variables
    IdentityHashMap<CpVar, List<Propagator>> byVar = new IdentityHashMap<>();
    for (Propagator prop : props) {
      for (CpVar x : prop.variables()) {
        byVar.computeIfAbsent(x, k -> new ArrayList<>()).add(prop);
      }
    }
    IdentityHashMap<Propagator, Boolean> relevant = new IdentityHashMap<>();
    IdentityHashMap<CpVar, Boolean> seenVars = new IdentityHashMap<>();
    ArrayList<CpVar> queue = new ArrayList<>();
    for (CpVar x : byVar.keySet()) {
      if (roles.containsKey(x)) {
        queue.add(x);
        seenVars.put(x, Boolean.TRUE);
      }
    }
    for (int k = 0; k < queue.size(); k++) {
      CpVar x = queue.get(k);
      List<Propagator> list = byVar.get(x);
      if (list == null) {
        continue;
      }
      for (Propagator prop : list) {
        if (relevant.put(prop, Boolean.TRUE) == null) {
          for (CpVar y : prop.variables()) {
            if (seenVars.put(y, Boolean.TRUE) == null) {
              queue.add(y);
            }
          }
        }
      }
    }
    if (relevant.isEmpty()) {
      return null;
    }
    List<CpVar> referenced = new ArrayList<>();
    List<CpVar> others = new ArrayList<>();
    for (CpVar x : queue) {
      if (roles.containsKey(x)) {
        referenced.add(x);
      } else {
        others.add(x);
      }
    }
    return new Checker(p, roles, referenced, others);
  }

  /** Checks the user constraints with the propagation and search of the solver's engine. */
  private final class Checker implements SideConstraints {
    private final RoutingProblem p;

    private final IdentityHashMap<CpVar, int[]> roles;

    private final CpVar[] referenced;

    private final CpVar[] others;

    private final boolean[] dimUsed;

    private final Scheduler scheduler = new Scheduler();

    private final CpVar[] decisionOrder;

    private final boolean[] maximize;

    Checker(RoutingProblem p, IdentityHashMap<CpVar, int[]> roles, List<CpVar> referenced,
        List<CpVar> others) {
      this.p = p;
      this.roles = roles;
      this.referenced = referenced.toArray(new CpVar[0]);
      this.others = others.toArray(new CpVar[0]);
      dimUsed = new boolean[dimensions.size()];
      for (CpVar x : referenced) {
        int[] r = roles.get(x);
        if (r[0] == ROLE_CUMUL || r[0] == ROLE_SLACK || r[0] == ROLE_TRANSIT) {
          dimUsed[r[2]] = true;
        }
      }
      // decision order: finalizer variables first, then the referenced cumuls, then the rest
      List<CpVar> order = new ArrayList<>();
      List<Boolean> max = new ArrayList<>();
      IdentityHashMap<CpVar, Boolean> inOrder = new IdentityHashMap<>();
      IdentityHashMap<CpVar, Boolean> isReferenced = new IdentityHashMap<>();
      for (CpVar x : referenced) {
        isReferenced.put(x, Boolean.TRUE);
      }
      for (int k = 0; k < finalizerVars.size(); k++) {
        CpVar x = ACCESS.cpVar(finalizerVars.get(k));
        if (isReferenced.containsKey(x) && inOrder.put(x, Boolean.TRUE) == null) {
          order.add(x);
          max.add(finalizerKinds.get(k)[0] == 1);
        }
      }
      for (CpVar x : referenced) {
        if (inOrder.put(x, Boolean.TRUE) == null) {
          order.add(x);
          max.add(false);
        }
      }
      for (CpVar x : others) {
        if (inOrder.put(x, Boolean.TRUE) == null) {
          order.add(x);
          max.add(false);
        }
      }
      decisionOrder = order.toArray(new CpVar[0]);
      maximize = new boolean[decisionOrder.length];
      for (int k = 0; k < maximize.length; k++) {
        maximize[k] = max.get(k);
      }
    }

    @Override
    public boolean usesDimension(int d) {
      return dimUsed[d];
    }

    @Override
    public boolean check(int[][] routes, byte[] status, long[][] fixedCumuls) {
      CpStore store = ACCESS.store(solver);
      int base = store.level();
      store.pushLevel();
      try {
        int[] vehOf = new int[numIndices];
        int[] nextOf = new int[numIndices];
        Arrays.fill(vehOf, -1);
        Arrays.fill(nextOf, -1);
        for (int v = 0; v < vehicles; v++) {
          int[] r = routes[v];
          for (int k = 0; k < r.length; k++) {
            vehOf[r[k]] = v;
            if (k + 1 < r.length) {
              nextOf[r[k]] = r[k + 1];
            }
          }
        }
        for (CpVar x : referenced) {
          int[] role = roles.get(x);
          int i = role[1];
          switch (role[0]) {
            case ROLE_NEXT:
              if (status[i] == SideConstraints.ACTIVE && nextOf[i] >= 0) {
                x.setValue(nextOf[i]);
              } else if (status[i] == SideConstraints.INACTIVE) {
                x.setValue(i);
              }
              break;
            case ROLE_VEHICLE:
              if (status[i] == SideConstraints.ACTIVE && vehOf[i] >= 0) {
                x.setValue(vehOf[i]);
              } else if (status[i] == SideConstraints.INACTIVE) {
                x.setValue(-1);
              }
              break;
            case ROLE_ACTIVE:
              if (status[i] == SideConstraints.ACTIVE) {
                x.setValue(1);
              } else if (status[i] == SideConstraints.INACTIVE) {
                x.setValue(0);
              }
              break;
            case ROLE_VEHICLE_ACTIVE:
              x.setValue(routes[i].length > 2 ? 1 : 0);
              break;
            default:
              break;
          }
        }
        // cumuls: windows of the routes and the arc relations
        for (int d = 0; d < dimUsed.length; d++) {
          if (!dimUsed[d]) {
            continue;
          }
          Dimension rd = dimensions.get(d);
          DimensionData dd = p.dims[d];
          for (int v = 0; v < vehicles; v++) {
            int[] r = routes[v];
            if (!scheduler.run(dd, v, r, null)) {
              return false;
            }
            for (int k = 0; k < r.length; k++) {
              ACCESS.cpVar(rd.cumuls[r[k]]).setRange(scheduler.min(k), scheduler.max(k));
            }
            for (int k = 0; k + 1 < r.length; k++) {
              long t = scheduler.transit(k);
              CpVar a = ACCESS.cpVar(rd.cumuls[r[k]]);
              CpVar b = ACCESS.cpVar(rd.cumuls[r[k + 1]]);
              CpVar s = ACCESS.cpVar(rd.slacks[r[k]]);
              store.post(new LinearPropagator(new long[] {1, -1, -1}, new CpVar[] {b, a, s}, t,
                  t));
              CpVar tv = ACCESS.cpVar(rd.transits[r[k]]);
              tv.setValue(t);
            }
          }
        }
        store.enqueueAll();
        store.propagate();
        int pushed = store.depthFirstSearch(decisionOrder, maximize, 2000);
        if (pushed < 0) {
          return false;
        }
        if (fixedCumuls != null) {
          for (int d = 0; d < dimensions.size(); d++) {
            if (!dimUsed[d]) {
              continue;
            }
            Dimension rd = dimensions.get(d);
            for (int i = 0; i < numIndices; i++) {
              if (vehOf[i] >= 0) {
                CpVar c = ACCESS.cpVar(rd.cumuls[i]);
                if (c.isBound()) {
                  fixedCumuls[d][i] = c.value();
                }
              }
            }
          }
        }
        return true;
      } catch (CpFail f) {
        return false;
      } finally {
        store.popToLevel(base);
      }
    }
  }

  // ----- solving -----

  /** Solves with the default search parameters, starting from the assignment if not null. */
  public Assignment solve(Assignment assignment) {
    if (assignment != null) {
      return solveFromAssignmentWithParameters(assignment,
          Globals.defaultRoutingSearchParameters());
    }
    return solveWithParameters(Globals.defaultRoutingSearchParameters());
  }

  /** Solves with the default search parameters. */
  public Assignment solve() {
    return solve(null);
  }

  /** Solves with the given search parameters; returns null if no solution was found. */
  public Assignment solveWithParameters(RoutingSearchParameters searchParameters) {
    return solveInternal(null, searchParameters);
  }

  /** Solves starting from the routes of <code>assignment</code>. */
  public Assignment solveFromAssignmentWithParameters(Assignment assignment,
      RoutingSearchParameters searchParameters) {
    int[][] routes = assignment == null ? null : routesOf(assignment);
    if (assignment != null && routes == null) {
      closeModel();
      status = RoutingSearchStatus.Value.ROUTING_FAIL;
      return null;
    }
    return solveInternal(routes, searchParameters);
  }

  /** The full routes (start to end) given by the next values of the assignment, or null. */
  private int[][] routesOf(Assignment assignment) {
    int[][] routes = new int[vehicles][];
    boolean[] seen = new boolean[numIndices];
    for (int v = 0; v < vehicles; v++) {
      List<Integer> r = new ArrayList<>();
      int index = (int) start(v);
      r.add(index);
      seen[index] = true;
      while (!isEnd(index)) {
        if (!assignment.contains(nexts[index])) {
          return null;
        }
        int next = (int) assignment.value(nexts[index]);
        if (next < 0 || next >= numIndices || seen[next]) {
          return null;
        }
        seen[next] = true;
        r.add(next);
        index = next;
      }
      if (index != end(v)) {
        return null;
      }
      routes[v] = new int[r.size()];
      for (int k = 0; k < r.size(); k++) {
        routes[v][k] = r.get(k);
      }
    }
    return routes;
  }

  private RoutingEngine.Parameters engineParameters(RoutingSearchParameters sp) {
    RoutingEngine.Parameters ep = new RoutingEngine.Parameters();
    ep.firstSolutionStrategy = sp.getFirstSolutionStrategy().getNumber();
    ep.metaheuristic = sp.getLocalSearchMetaheuristic().getNumber();
    if (sp.hasTimeLimit()) {
      ep.timeLimitNanos = sp.getTimeLimit().toNanos();
    }
    ep.solutionLimit = sp.getSolutionLimit();
    ep.glsLambda = sp.getGuidedLocalSearchLambdaCoefficient();
    ep.logSearch = sp.getLogSearch();
    return ep;
  }

  private Assignment solveInternal(int[][] initialRoutes, RoutingSearchParameters sp) {
    closeModel();
    String error = Globals.findErrorInRoutingSearchParameters(sp);
    if (!error.isEmpty()) {
      LOGGER.warning("Invalid routing search parameters: " + error);
      status = RoutingSearchStatus.Value.ROUTING_INVALID;
      return null;
    }
    if (ACCESS.isRootFailed(solver)) {
      status = RoutingSearchStatus.Value.ROUTING_INFEASIBLE;
      return null;
    }
    final RoutingProblem problem = buildProblem();
    RoutingEngine.Listener listener = null;
    if (!atSolutionCallbacks.isEmpty()) {
      listener = (routes, st, objective) -> {
        runCallbacks(problem, routes, st, objective);
        if (ACCESS.isFinishRequested(solver)) {
          ACCESS.clearFinishRequest(solver);
          return false;
        }
        return true;
      };
    }
    RoutingEngine engine = new RoutingEngine(problem, engineParameters(sp), listener);
    RoutingEngine.Result result = engine.solve(initialRoutes);
    if (!result.found) {
      status = result.timedOut ? RoutingSearchStatus.Value.ROUTING_FAIL_TIMEOUT
          : RoutingSearchStatus.Value.ROUTING_FAIL;
      return null;
    }
    status = isTriviallyOptimal(problem) ? RoutingSearchStatus.Value.ROUTING_OPTIMAL
        : RoutingSearchStatus.Value.ROUTING_SUCCESS;
    return buildAssignment(problem, engine, result.routes, result.status, result.objective);
  }

  private boolean isTriviallyOptimal(RoutingProblem p) {
    int visits = 0;
    int last = -1;
    for (int i = 0; i < size; i++) {
      if (p.isVisit(i)) {
        visits++;
        last = i;
      }
    }
    if (visits == 0) {
      return true;
    }
    return visits == 1 && vehicles == 1 && p.disjunctionsOfNode[last].length == 0;
  }

  private void runCallbacks(RoutingProblem problem, int[][] routes, byte[] st, long objective) {
    CpStore store = ACCESS.store(solver);
    int base = store.level();
    store.pushLevel();
    try {
      bind(ACCESS.cpVar(costVar), objective);
      for (int v = 0; v < vehicles; v++) {
        int[] r = routes[v];
        bind(ACCESS.cpVar(vehicleActive[v]), r.length > 2 ? 1 : 0);
        for (int k = 0; k + 1 < r.length; k++) {
          bind(ACCESS.cpVar(nexts[r[k]]), r[k + 1]);
          bind(ACCESS.cpVar(active[r[k]]), 1);
        }
        for (int x : r) {
          bind(ACCESS.cpVar(vehicleVars[x]), v);
        }
      }
      for (int i = 0; i < size; i++) {
        if (problem.isVisit(i) && st[i] == SideConstraints.INACTIVE) {
          bind(ACCESS.cpVar(nexts[i]), i);
          bind(ACCESS.cpVar(active[i]), 0);
          bind(ACCESS.cpVar(vehicleVars[i]), -1);
        }
      }
      Scheduler scheduler = new Scheduler();
      for (int d = 0; d < dimensions.size(); d++) {
        Dimension rd = dimensions.get(d);
        for (int v = 0; v < vehicles; v++) {
          int[] r = routes[v];
          if (scheduler.run(problem.dims[d], v, r, null)) {
            for (int k = 0; k < r.length; k++) {
              try {
                ACCESS.cpVar(rd.cumuls[r[k]]).setRange(scheduler.min(k), scheduler.max(k));
              } catch (CpFail f) {
                // keep the model domain
              }
            }
          }
        }
      }
      store.clearQueue();
      for (Runnable callback : atSolutionCallbacks) {
        callback.run();
      }
    } finally {
      store.popToLevel(base);
    }
  }

  private static void bind(CpVar x, long value) {
    try {
      x.setValue(value);
    } catch (CpFail f) {
      // the value is outside of the model domain; leave the variable unbound
    }
  }

  private long[][] finalizerSpecs() {
    Map<CpVar, int[]> cumulOf = new IdentityHashMap<>();
    for (Dimension d : dimensions) {
      for (int i = 0; i < numIndices; i++) {
        cumulOf.put(ACCESS.cpVar(d.cumuls[i]), new int[] {d.index, i});
      }
    }
    List<long[]> specs = new ArrayList<>();
    for (int k = 0; k < finalizerVars.size(); k++) {
      int[] c = cumulOf.get(ACCESS.cpVar(finalizerVars.get(k)));
      if (c != null) {
        long[] kind = finalizerKinds.get(k);
        specs.add(new long[] {c[0], c[1], kind[0], kind[1]});
      }
    }
    return specs.toArray(new long[0][]);
  }

  private Assignment buildAssignment(RoutingProblem problem, RoutingEngine engine, int[][] routes,
      byte[] st, long objective) {
    RoutingEngine.Details det = engine.details(routes, st, finalizerSpecs());
    Assignment a = new Assignment(solver);
    int[] nextOf = new int[numIndices];
    int[] vehOf = new int[numIndices];
    Arrays.fill(vehOf, -1);
    for (int i = 0; i < size; i++) {
      nextOf[i] = i;
    }
    for (int v = 0; v < vehicles; v++) {
      int[] r = routes[v];
      for (int k = 0; k < r.length; k++) {
        vehOf[r[k]] = v;
        if (k + 1 < r.length) {
          nextOf[r[k]] = r[k + 1];
        }
      }
    }
    for (int i = 0; i < size; i++) {
      a.add(nexts[i]).setValue(nextOf[i]);
    }
    for (int i = 0; i < numIndices; i++) {
      a.add(vehicleVars[i]).setValue(vehOf[i]);
    }
    for (int i = 0; i < size; i++) {
      a.add(active[i]).setValue(vehOf[i] >= 0 ? 1 : 0);
    }
    for (int v = 0; v < vehicles; v++) {
      a.add(vehicleActive[v]).setValue(routes[v].length > 2 ? 1 : 0);
    }
    for (int d = 0; d < dimensions.size(); d++) {
      Dimension rd = dimensions.get(d);
      for (int i = 0; i < numIndices; i++) {
        a.add(rd.cumuls[i]).setRange(det.cumulMin[d][i], det.cumulMax[d][i]);
      }
      for (int i = 0; i < size; i++) {
        a.add(rd.slacks[i]).setRange(det.slackMin[d][i], det.slackMax[d][i]);
        a.add(rd.transits[i]).setValue(det.transit[d][i]);
      }
      for (int v = 0; v < vehicles; v++) {
        IntervalVar[] bs = rd.breaks[v];
        if (bs == null) {
          continue;
        }
        long[] starts = det.breakStart[d][v];
        for (int k = 0; k < bs.length; k++) {
          IntervalVarElement e = a.add(bs[k]);
          if (starts != null && k < starts.length && starts[k] != Long.MIN_VALUE) {
            long duration = bs[k].durationMin();
            e.setStartValue(starts[k]);
            e.setDurationValue(duration);
            e.setEndValue(starts[k] + duration);
            e.setPerformedValue(1);
          } else {
            e.setPerformedValue(0);
          }
        }
      }
    }
    for (IntVar x : extraVars) {
      if (!a.contains(x)) {
        a.add(x);
      }
    }
    for (IntervalVar x : extraIntervals) {
      if (!a.contains(x)) {
        a.add(x);
      }
    }
    a.addObjective(costVar);
    a.setObjectiveValue(objective);
    return a;
  }

  // ----- routes and assignments -----

  /**
   * An assignment with the given routes (indices visited by each vehicle, without start and
   * end); nodes not on a route are unperformed. Returns null if the routes are invalid or
   * violate a constraint of the model.
   */
  public Assignment readAssignmentFromRoutes(long[][] routes, boolean ignoreInactiveIndices) {
    closeModel();
    RoutingProblem problem = buildProblem();
    int[][] r = new int[vehicles][];
    for (int v = 0; v < vehicles; v++) {
      List<Integer> list = new ArrayList<>();
      if (v < routes.length && routes[v] != null) {
        for (long x : routes[v]) {
          if (ignoreInactiveIndices && x >= 0 && x < size && active[(int) x].max() == 0) {
            continue;
          }
          list.add((int) x);
        }
      }
      r[v] = new int[list.size()];
      for (int k = 0; k < list.size(); k++) {
        r[v][k] = list.get(k);
      }
    }
    RoutingEngine engine = new RoutingEngine(problem, new RoutingEngine.Parameters(), null);
    RoutingEngine.Result result = engine.evaluateRoutes(r);
    if (result == null) {
      return null;
    }
    return buildAssignment(problem, engine, result.routes, result.status, result.objective);
  }

  /**
   * Fills the successor values of the given routes into <code>assignment</code>. With
   * <code>closeRoutes</code> the indices on no route are made unperformed (successor = itself).
   */
  public boolean routesToAssignment(long[][] routes, boolean ignoreInactiveIndices,
      boolean closeRoutes, Assignment assignment) {
    if (routes.length > vehicles) {
      return false;
    }
    boolean[] visited = new boolean[numIndices];
    for (int v = 0; v < routes.length; v++) {
      long previous = start(v);
      for (long x : routes[v]) {
        if (x < 0 || x >= size || isStart(x) || visited[(int) x]) {
          return false;
        }
        if (ignoreInactiveIndices && active[(int) x].max() == 0) {
          continue;
        }
        visited[(int) x] = true;
        if (!nexts[(int) previous].contains(x)) {
          return false;
        }
        assignment.add(nexts[(int) previous]).setValue(x);
        previous = x;
      }
      assignment.add(nexts[(int) previous]).setValue(end(v));
    }
    for (int v = routes.length; v < vehicles && closeRoutes; v++) {
      assignment.add(nexts[(int) start(v)]).setValue(end(v));
    }
    if (closeRoutes) {
      for (int i = 0; i < size; i++) {
        if (!isStart(i) && !visited[i]) {
          assignment.add(nexts[i]).setValue(i);
        }
      }
    }
    return true;
  }

  /** Stores the routes of the assignment (without start and end) into <code>routes</code>. */
  public void assignmentToRoutes(Assignment assignment, long[][] routes) {
    for (int v = 0; v < vehicles && v < routes.length; v++) {
      List<Long> list = new ArrayList<>();
      long index = assignment.value(nexts[(int) start(v)]);
      while (!isEnd(index)) {
        list.add(index);
        index = assignment.value(nexts[(int) index]);
      }
      routes[v] = new long[list.size()];
      for (int k = 0; k < list.size(); k++) {
        routes[v][k] = list.get(k);
      }
    }
  }

  /** A copy of the assignment (no compaction is done by this implementation). */
  public Assignment compactAssignment(Assignment assignment) {
    return new Assignment(assignment);
  }

  /** A copy of the assignment (no compaction is done by this implementation). */
  public Assignment compactAndCheckAssignment(Assignment assignment) {
    return new Assignment(assignment);
  }

  /**
   * Fixes the beginning of every route: vehicle <code>v</code> visits <code>locks[v]</code>
   * first; with <code>closeRoutes</code> the route ends after them.
   */
  public boolean applyLocksToAllVehicles(long[][] locks, boolean closeRoutes) {
    for (int v = 0; v < locks.length && v < vehicles; v++) {
      long previous = start(v);
      for (long x : locks[v]) {
        if (!nexts[(int) previous].contains(x)) {
          return false;
        }
        nexts[(int) previous].setValue(x);
        previous = x;
      }
      if (closeRoutes) {
        nexts[(int) previous].setValue(end(v));
      }
    }
    return !ACCESS.isRootFailed(solver);
  }

  /** A textual description of the routes of the assignment with a dimension's cumuls. */
  public String debugOutputAssignment(Assignment solutionAssignment, String dimensionToPrint) {
    StringBuilder buf = new StringBuilder();
    Dimension d = dimensionByName.get(dimensionToPrint);
    for (int v = 0; v < vehicles; v++) {
      buf.append("Route ").append(v).append(": ");
      long index = start(v);
      while (true) {
        buf.append(index);
        if (d != null && solutionAssignment.contains(d.cumuls[(int) index])) {
          buf.append(' ').append(dimensionToPrint).append('(')
              .append(solutionAssignment.min(d.cumuls[(int) index])).append(", ")
              .append(solutionAssignment.max(d.cumuls[(int) index])).append(')');
        }
        if (isEnd(index)) {
          break;
        }
        buf.append(" -> ");
        index = solutionAssignment.value(nexts[(int) index]);
      }
      buf.append('\n');
    }
    return buf.toString();
  }
}
