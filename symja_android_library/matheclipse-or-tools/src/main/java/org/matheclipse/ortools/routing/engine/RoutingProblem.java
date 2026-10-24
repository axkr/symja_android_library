package org.matheclipse.ortools.routing.engine;

import java.util.BitSet;

/**
 * The closed model handed to the simplified routing engine of this module, which reimplements
 * the behaviour of the OR-Tools routing library (first solution heuristics, local search and
 * guided local search) instead of porting its constraint programming machinery.
 *
 * <p>
 * Indices follow <code>IndexManager</code>: <code>0 .. size-1</code> are the visit nodes
 * and the vehicle starts, <code>size .. size+vehicles-1</code> the vehicle ends. The arrays are
 * filled by <code>Model</code> when it closes the model.
 */
public final class RoutingProblem {

  /** Number of indices which have a successor (visit nodes and starts). */
  public int size;

  /** Number of vehicles. */
  public int vehicles;

  /** <code>size + vehicles</code>. */
  public int numIndices;

  /** The start index per vehicle. */
  public int[] start;

  /** The end index per vehicle. */
  public int[] end;

  /** The vehicle of a start or end index, -1 for visit nodes. */
  public int[] vehicleOfStartEnd;

  /** The arc cost evaluator per vehicle (without fixed and span costs). */
  public ArcMatrix[] arcCost;

  /** A cost class per vehicle: vehicles of the same class have the same arc costs. */
  public int[] costClass;

  /** The fixed cost per vehicle, paid if the route is not empty. */
  public long[] fixedCost;

  /** Vehicles whose empty route is still paid for. */
  public boolean[] usedWhenEmpty;

  /** The disjunctions: node indices of each disjunction. */
  public int[][] disjunctionNodes = new int[0][];

  /** The hard minimal number of active nodes of each disjunction. */
  public int[] disjunctionMinCardinality = new int[0];

  /** The hard maximal number of active nodes of each disjunction. */
  public int[] disjunctionMaxCardinality = new int[0];

  /** The soft minimal number of active nodes of each disjunction (0 if none). */
  public int[] disjunctionSoftMinCardinality = new int[0];

  /** The penalty of the soft minimum of each disjunction. */
  public long[] disjunctionSoftMinPenalty = new long[0];

  /** Whether the soft minimum penalty is paid per missing node (otherwise once). */
  public boolean[] disjunctionSoftMinPerInactive = new boolean[0];

  /** The soft maximal number of active nodes of each disjunction (MAX_VALUE if none). */
  public int[] disjunctionSoftMaxCardinality = new int[0];

  /** The penalty of the soft maximum of each disjunction. */
  public long[] disjunctionSoftMaxPenalty = new long[0];

  /** Whether the soft maximum penalty is paid per excess node (otherwise once). */
  public boolean[] disjunctionSoftMaxPerInactive = new boolean[0];

  /** The disjunctions of each index. */
  public int[][] disjunctionsOfNode;

  /** Indices whose active variable is bound to 1. */
  public boolean[] forcedActive;

  /** Indices whose active variable is bound to 0. */
  public boolean[] forcedInactive;

  /** The allowed successors per index, <code>null</code> if all are allowed. */
  public BitSet[] allowedNext;

  /** The allowed vehicles per index, <code>null</code> if all are allowed. */
  public BitSet[] allowedVehicles;

  /** The pickup of each pickup and delivery pair. */
  public int[] pairPickup = new int[0];

  /** The delivery of each pickup and delivery pair. */
  public int[] pairDelivery = new int[0];

  /** The pair of each index or -1. */
  public int[] pairOfNode;

  /** The pickup and delivery policy per vehicle (0 none, 1 LIFO, 2 FIFO). */
  public int[] policy;

  /** The dimensions. */
  public DimensionData[] dims = new DimensionData[0];

  /** The maximal number of non-empty routes. */
  public int maxActiveVehicles;

  /** Constraints posted by the user on the routing variables, or <code>null</code>. */
  public SideConstraints sideConstraints;

  /** Whether <code>index</code> is a start. */
  public boolean isStart(int index) {
    return index < size && vehicleOfStartEnd[index] >= 0;
  }

  /** Whether <code>index</code> is an end. */
  public boolean isEnd(int index) {
    return index >= size;
  }

  /** Whether <code>index</code> is a visit node (neither start nor end). */
  public boolean isVisit(int index) {
    return index < size && vehicleOfStartEnd[index] < 0;
  }

  /** Whether <code>i -&gt; j</code> is allowed by the domain of the next variable of i. */
  public boolean nextAllowed(int i, int j) {
    BitSet b = allowedNext[i];
    return b == null || b.get(j);
  }

  /** Whether <code>index</code> may be served by vehicle <code>v</code>. */
  public boolean vehicleAllowed(int index, int v) {
    BitSet b = allowedVehicles[index];
    return b == null || b.get(v);
  }

  /** Whether some dimension has a cost which depends on the cumuls. */
  public boolean hasDimensionCosts() {
    for (DimensionData d : dims) {
      if (d.hasCost()) {
        return true;
      }
    }
    return false;
  }

  /** Whether some vehicle has breaks. */
  public boolean hasBreaks() {
    for (DimensionData d : dims) {
      for (int v = 0; v < vehicles; v++) {
        if (d.hasBreaks(v)) {
          return true;
        }
      }
    }
    return false;
  }
}
