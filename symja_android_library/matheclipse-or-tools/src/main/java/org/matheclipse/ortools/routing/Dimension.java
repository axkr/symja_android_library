package org.matheclipse.ortools.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongBinaryOperator;
import org.matheclipse.ortools.constraintsolver.IntVar;
import org.matheclipse.ortools.constraintsolver.IntervalVar;
import org.matheclipse.ortools.constraintsolver.Solver;

/**
 * A quantity accumulated along the routes (load, time, distance ...), mirroring
 * <code>com.google.ortools.routing.Dimension</code> of the OR-Tools 10.0 Java wrapper (named
 * <code>com.google.ortools.constraintsolver.RoutingDimension</code> before), with the methods the
 * wrapper exposes.
 *
 * <p>
 * Reimplemented on the simplified routing engine of this module. Every index has a cumul
 * variable (domain <code>[0, max capacity]</code>, restricted further by the user), every index
 * with a successor a transit and a slack variable; along a route
 * <code>cumul(next) = cumul(i) + transit(i, next) + slack(i)</code>,
 * <code>0 &lt;= slack &lt;= slack_max</code> and <code>cumul &lt;= capacity(vehicle)</code>. The
 * engine computes the cumuls of a solution directly; dimensions with span or soft bound costs
 * get the cost-minimal schedule, the others report the feasible window of every cumul.
 */
public class Dimension {

  /** Break interval ordering: the breaks of a vehicle may be taken in any order. */
  public static final int kUnordered = 0;

  /** Break interval ordering: each break of a vehicle starts after the previous one ends. */
  public static final int kOrdered = 1;

  final Model model;

  final String name;

  final int index;

  final int[] evaluatorOfVehicle;

  final long slackMax;

  final long[] capacity;

  final boolean fixStartCumulToZero;

  final IntVar[] cumuls;

  final IntVar[] transits;

  final IntVar[] fixedTransits;

  final IntVar[] slacks;

  final long[] spanCost;

  final long[] slackCost;

  long globalSpanCost;

  final long[] spanUpperBound;

  final long[] softUpperBound;

  final long[] softUpperCost;

  final long[] softLowerBound;

  final long[] softLowerCost;

  final BoundCost[] softSpanUpperBound;

  final BoundCost[] quadraticSoftSpanUpperBound;

  final IntervalVar[][] breaks;

  final long[][] visitTransits;

  final int[] preTravelEvaluator;

  final int[] postTravelEvaluator;

  final long[][] breakDistanceDuration;

  final int[] breakIntervalsOrdering;

  final List<long[]> precedences = new ArrayList<>();

  Dimension(Model model, String name, int index, int[] evaluators, long slackMax,
      long[] capacity, boolean fixStartCumulToZero) {
    this.model = model;
    this.name = name;
    this.index = index;
    this.evaluatorOfVehicle = evaluators.clone();
    this.slackMax = slackMax;
    this.capacity = capacity.clone();
    this.fixStartCumulToZero = fixStartCumulToZero;
    int vehicles = model.vehicles();
    int size = (int) model.size();
    int numIndices = size + vehicles;
    Solver solver = model.solver();
    long maxCapacity = 0;
    for (long c : capacity) {
      maxCapacity = Math.max(maxCapacity, c);
    }
    cumuls = solver.makeIntVarArray(numIndices, 0, maxCapacity, name);
    slacks = solver.makeIntVarArray(size, 0, slackMax, name + "_slack");
    transits = solver.makeIntVarArray(size, Long.MIN_VALUE / 2, Long.MAX_VALUE / 2,
        name + "_transit");
    fixedTransits = transits;
    if (fixStartCumulToZero) {
      for (int v = 0; v < vehicles; v++) {
        cumuls[(int) model.start(v)].setValue(0);
      }
    }
    spanCost = new long[vehicles];
    slackCost = new long[vehicles];
    spanUpperBound = new long[vehicles];
    Arrays.fill(spanUpperBound, Long.MAX_VALUE);
    softUpperBound = new long[numIndices];
    softUpperCost = new long[numIndices];
    softLowerBound = new long[numIndices];
    softLowerCost = new long[numIndices];
    softSpanUpperBound = new BoundCost[vehicles];
    quadraticSoftSpanUpperBound = new BoundCost[vehicles];
    breaks = new IntervalVar[vehicles][];
    visitTransits = new long[vehicles][];
    preTravelEvaluator = new int[vehicles];
    postTravelEvaluator = new int[vehicles];
    Arrays.fill(preTravelEvaluator, -1);
    Arrays.fill(postTravelEvaluator, -1);
    breakDistanceDuration = new long[vehicles][];
    breakIntervalsOrdering = new int[vehicles];
  }

  /** The transit of the arc for the given vehicle. */
  public long getTransitValue(long fromIndex, long toIndex, long vehicle) {
    return model.transitValue(evaluatorOfVehicle[(int) vehicle], fromIndex, toIndex);
  }

  /** The transit of the arc for the vehicles of the given class (the class index is a vehicle). */
  public long getTransitValueFromClass(long fromIndex, long toIndex, long vehicleClass) {
    return getTransitValue(fromIndex, toIndex, vehicleClass);
  }

  /** The cumul variable of an index. */
  public IntVar cumulVar(long index) {
    return cumuls[(int) index];
  }

  /** The transit variable of an index. */
  public IntVar transitVar(long index) {
    return transits[(int) index];
  }

  /** The fixed transit variable of an index (the transit variable in this implementation). */
  public IntVar fixedTransitVar(long index) {
    return fixedTransits[(int) index];
  }

  /** The slack variable of an index. */
  public IntVar slackVar(long index) {
    return slacks[(int) index];
  }

  /** All cumul variables. */
  public IntVar[] cumuls() {
    return cumuls.clone();
  }

  /** All transit variables. */
  public IntVar[] transits() {
    return transits.clone();
  }

  /** All slack variables. */
  public IntVar[] slacks() {
    return slacks.clone();
  }

  /** The span (end cumul minus start cumul) of the vehicle must be at most the bound. */
  public void setSpanUpperBoundForVehicle(long upperBound, int vehicle) {
    spanUpperBound[vehicle] = upperBound;
  }

  /** Adds <code>coefficient * span</code> of the vehicle to the cost. */
  public void setSpanCostCoefficientForVehicle(long coefficient, int vehicle) {
    spanCost[vehicle] = coefficient;
  }

  /** Adds <code>coefficient * span</code> of every vehicle to the cost. */
  public void setSpanCostCoefficientForAllVehicles(long coefficient) {
    Arrays.fill(spanCost, coefficient);
  }

  /**
   * Adds <code>coefficient * (max end cumul - min start cumul)</code> over the used vehicles to
   * the cost.
   */
  public void setGlobalSpanCostCoefficient(long coefficient) {
    globalSpanCost = coefficient;
  }

  /** Adds <code>coefficient * max(0, cumul(index) - upperBound)</code> to the cost. */
  public void setCumulVarSoftUpperBound(long index, long upperBound, long coefficient) {
    softUpperBound[(int) index] = upperBound;
    softUpperCost[(int) index] = coefficient;
  }

  /** Whether the index has a soft upper bound. */
  public boolean hasCumulVarSoftUpperBound(long index) {
    return softUpperCost[(int) index] != 0;
  }

  /** The soft upper bound of the index, or the maximum of its cumul if none was set. */
  public long getCumulVarSoftUpperBound(long index) {
    return hasCumulVarSoftUpperBound(index) ? softUpperBound[(int) index]
        : cumuls[(int) index].max();
  }

  /** The cost coefficient of the soft upper bound of the index (0 if none). */
  public long getCumulVarSoftUpperBoundCoefficient(long index) {
    return softUpperCost[(int) index];
  }

  /** Adds <code>coefficient * max(0, lowerBound - cumul(index))</code> to the cost. */
  public void setCumulVarSoftLowerBound(long index, long lowerBound, long coefficient) {
    softLowerBound[(int) index] = lowerBound;
    softLowerCost[(int) index] = coefficient;
  }

  /** Whether the index has a soft lower bound. */
  public boolean hasCumulVarSoftLowerBound(long index) {
    return softLowerCost[(int) index] != 0;
  }

  /** The soft lower bound of the index, or the minimum of its cumul if none was set. */
  public long getCumulVarSoftLowerBound(long index) {
    return hasCumulVarSoftLowerBound(index) ? softLowerBound[(int) index]
        : cumuls[(int) index].min();
  }

  /** The cost coefficient of the soft lower bound of the index (0 if none). */
  public long getCumulVarSoftLowerBoundCoefficient(long index) {
    return softLowerCost[(int) index];
  }

  /**
   * Sets the breaks of a vehicle; <code>preTravelEvaluator</code> gives the time at the start of
   * an arc during which no break may be taken (the post travel evaluator is ignored by this
   * implementation). A break may take place before the start, after the end or during a travel
   * between two nodes, where it uses slack.
   */
  public void setBreakIntervalsOfVehicle(IntervalVar[] breakIntervals, int vehicle,
      int preTravelEvaluator, int postTravelEvaluator) {
    breaks[vehicle] = breakIntervals.clone();
    this.preTravelEvaluator[vehicle] = preTravelEvaluator;
    this.postTravelEvaluator[vehicle] = postTravelEvaluator;
    visitTransits[vehicle] = null;
  }

  /**
   * Sets the breaks of a vehicle; a break may not overlap the visit
   * <code>[cumul(i), cumul(i) + nodeVisitTransits[i])</code> of a node.
   */
  public void setBreakIntervalsOfVehicle(IntervalVar[] breakIntervals, int vehicle,
      long[] nodeVisitTransits) {
    breaks[vehicle] = breakIntervals.clone();
    visitTransits[vehicle] = nodeVisitTransits.clone();
    preTravelEvaluator[vehicle] = -1;
  }

  /** As {@link #setBreakIntervalsOfVehicle(IntervalVar[], int, long[])}; delays are ignored. */
  public void setBreakIntervalsOfVehicle(IntervalVar[] breakIntervals, int vehicle,
      long[] nodeVisitTransits, LongBinaryOperator delays) {
    setBreakIntervalsOfVehicle(breakIntervals, vehicle, nodeVisitTransits);
  }

  /** Stored but not enforced by this implementation. */
  public void setBreakDistanceDurationOfVehicle(long distance, long duration, int vehicle) {
    breakDistanceDuration[vehicle] = new long[] {distance, duration};
  }

  /** The breaks of a vehicle. */
  public IntervalVar[] getBreakIntervalsOfVehicle(int vehicle) {
    return breaks[vehicle] == null ? new IntervalVar[0] : breaks[vehicle].clone();
  }

  /**
   * Sets whether the breaks of the vehicle are ordered in time ({@link #kOrdered}) or not
   * ({@link #kUnordered}, the default). The engine of this module schedules the breaks of a
   * vehicle in the order of their earliest starts in both cases.
   */
  public void setBreakIntervalsOrderingOfVehicle(int vehicle, int ordering) {
    breakIntervalsOrdering[vehicle] = ordering;
  }

  /** The break interval ordering of the vehicle ({@link #kUnordered} if none was set). */
  public int getBreakIntervalsOrderingOfVehicle(int vehicle) {
    return breakIntervalsOrdering[vehicle];
  }

  /**
   * Requires <code>cumul(secondNode) &gt;= cumul(firstNode) + offset</code> when both nodes are
   * performed on the same route.
   */
  public void addNodePrecedence(long firstNode, long secondNode, long offset) {
    precedences.add(new long[] {firstNode, secondNode, offset});
  }

  /** The span upper bound of a vehicle. */
  public long getSpanUpperBoundForVehicle(int vehicle) {
    return spanUpperBound[vehicle];
  }

  /** The span cost coefficient of a vehicle. */
  public long getSpanCostCoefficientForVehicle(int vehicle) {
    return spanCost[vehicle];
  }

  /** The global span cost coefficient. */
  public long getGlobalSpanCostCoefficient() {
    return globalSpanCost;
  }

  /** Always 0: no cumul offsets are used. */
  public long getLocalOptimizerOffsetForVehicle(int vehicle) {
    return 0;
  }

  /** Adds <code>cost * max(0, span - bound)</code> of the vehicle to the cost. */
  public void setSoftSpanUpperBoundForVehicle(BoundCost boundCost, int vehicle) {
    softSpanUpperBound[vehicle] = new BoundCost(boundCost.getBound(), boundCost.getCost());
  }

  /** Whether a soft span upper bound was set. */
  public boolean hasSoftSpanUpperBounds() {
    for (BoundCost b : softSpanUpperBound) {
      if (b != null) {
        return true;
      }
    }
    return false;
  }

  /** The soft span upper bound of a vehicle (bound MAX_VALUE and cost 0 if none). */
  public BoundCost getSoftSpanUpperBoundForVehicle(int vehicle) {
    BoundCost b = softSpanUpperBound[vehicle];
    return b == null ? new BoundCost(Long.MAX_VALUE, 0) : new BoundCost(b.getBound(), b.getCost());
  }

  /** Adds <code>cost * max(0, span - bound)^2</code> of the vehicle to the cost. */
  public void setQuadraticCostSoftSpanUpperBoundForVehicle(BoundCost boundCost, int vehicle) {
    quadraticSoftSpanUpperBound[vehicle] = new BoundCost(boundCost.getBound(), boundCost.getCost());
  }

  /** Whether a quadratic soft span upper bound was set. */
  public boolean hasQuadraticCostSoftSpanUpperBounds() {
    for (BoundCost b : quadraticSoftSpanUpperBound) {
      if (b != null) {
        return true;
      }
    }
    return false;
  }

  /** The quadratic soft span upper bound of a vehicle (bound MAX_VALUE and cost 0 if none). */
  public BoundCost getQuadraticCostSoftSpanUpperBoundForVehicle(int vehicle) {
    BoundCost b = quadraticSoftSpanUpperBound[vehicle];
    return b == null ? new BoundCost(Long.MAX_VALUE, 0) : new BoundCost(b.getBound(), b.getCost());
  }
}
