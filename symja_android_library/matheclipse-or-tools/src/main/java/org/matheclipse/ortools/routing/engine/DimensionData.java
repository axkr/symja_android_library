package org.matheclipse.ortools.routing.engine;

/**
 * The data of one routing dimension (a quantity accumulated along the routes, such as load or
 * time) as used by the simplified routing engine of this module, which reimplements the
 * behaviour of OR-Tools' <code>Dimension</code>.
 *
 * <p>
 * Along a route <code>cumul(next) = cumul(i) + transit(i, next) + slack(i)</code> with
 * <code>0 &lt;= slack(i) &lt;= slackMax</code>, every cumul lies in its window and in
 * <code>[0, capacity(vehicle)]</code>.
 */
public final class DimensionData {

  /** A break of a vehicle: a fixed duration interval with a start window. */
  public static final class BreakSpec {
    /** Earliest start. */
    public final long startMin;

    /** Latest start. */
    public final long startMax;

    /** Duration. */
    public final long duration;

    /** Whether the break may be skipped. */
    public final boolean optional;

    /** Creates a break description. */
    public BreakSpec(long startMin, long startMax, long duration, boolean optional) {
      this.startMin = startMin;
      this.startMax = startMax;
      this.duration = duration;
      this.optional = optional;
    }
  }

  /** The name of the dimension. */
  public String name;

  /** The transits per vehicle. */
  public ArcMatrix[] transitOfVehicle;

  /** The largest slack (waiting) per arc. */
  public long slackMax;

  /** The capacity (largest cumul) per vehicle. */
  public long[] capacity;

  /** The smallest cumul per index (from the domain of the cumul variable). */
  public long[] cumulMin;

  /** The largest cumul per index. */
  public long[] cumulMax;

  /**
   * For indices whose cumul domain has holes: the sorted allowed intervals as
   * <code>{lo0, hi0, lo1, hi1, ...}</code>; <code>null</code> entries have no holes.
   */
  public long[][] allowed;

  /** The span cost coefficient per vehicle. */
  public long[] spanCost;

  /** The slack cost coefficient per vehicle. */
  public long[] slackCost;

  /** The global span cost coefficient. */
  public long globalSpanCost;

  /** The span upper bound per vehicle (hard). */
  public long[] spanUpperBound;

  /** Soft span upper bound and its linear cost per vehicle (cost 0 = none). */
  public long[] softSpanBound;

  /** Linear cost per unit of span above {@link #softSpanBound}. */
  public long[] softSpanCost;

  /** Soft span upper bound with a quadratic cost per vehicle. */
  public long[] quadSoftSpanBound;

  /** Quadratic cost coefficient of the excess over {@link #quadSoftSpanBound}. */
  public long[] quadSoftSpanCost;

  /** Soft upper bound of the cumul per index (with {@link #softUpperCost}). */
  public long[] softUpperBound;

  /** Cost per unit of cumul above the soft upper bound, per index (0 = none). */
  public long[] softUpperCost;

  /** Soft lower bound of the cumul per index (with {@link #softLowerCost}). */
  public long[] softLowerBound;

  /** Cost per unit of cumul below the soft lower bound, per index (0 = none). */
  public long[] softLowerCost;

  /** The breaks per vehicle (empty arrays when none). */
  public BreakSpec[][] breaks;

  /** The visit transit per index for the vehicles with breaks (pre-travel time). */
  public long[][] visitTransit;

  /** The pre travel evaluator per vehicle for the breaks (null: use {@link #visitTransit}). */
  public ArcMatrix[] preTravel;

  /** Node precedences <code>cumul(second) &gt;= cumul(first) + offset</code>. */
  public long[][] precedences = new long[0][];

  /** Whether some cost depends on the cumuls of this dimension. */
  public boolean hasCost() {
    if (globalSpanCost != 0) {
      return true;
    }
    for (int v = 0; v < spanCost.length; v++) {
      if (spanCost[v] != 0 || slackCost[v] != 0 || softSpanCost[v] != 0
          || quadSoftSpanCost[v] != 0) {
        return true;
      }
    }
    for (long c : softUpperCost) {
      if (c != 0) {
        return true;
      }
    }
    for (long c : softLowerCost) {
      if (c != 0) {
        return true;
      }
    }
    return false;
  }

  /** Whether vehicle <code>v</code> has breaks in this dimension. */
  public boolean hasBreaks(int v) {
    return breaks != null && breaks[v] != null && breaks[v].length > 0;
  }

  /** The smallest allowed cumul of <code>index</code> which is at least <code>value</code>. */
  public long nextAllowed(int index, long value) {
    long[] a = allowed == null ? null : allowed[index];
    if (a == null) {
      return value;
    }
    for (int k = 0; k < a.length; k += 2) {
      if (value <= a[k + 1]) {
        return Math.max(value, a[k]);
      }
    }
    return Long.MAX_VALUE;
  }

  /** The largest allowed cumul of <code>index</code> which is at most <code>value</code>. */
  public long previousAllowed(int index, long value) {
    long[] a = allowed == null ? null : allowed[index];
    if (a == null) {
      return value;
    }
    for (int k = a.length - 2; k >= 0; k -= 2) {
      if (value >= a[k]) {
        return Math.min(value, a[k + 1]);
      }
    }
    return Long.MIN_VALUE;
  }
}
