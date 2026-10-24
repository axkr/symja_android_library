package org.matheclipse.ortools.constraintsolver;

/**
 * An interval <code>[start, start + duration)</code> which may be optional, mirroring
 * <code>com.google.ortools.constraintsolver.IntervalVar</code> of the OR-Tools Java wrapper.
 *
 * <p>
 * This reimplementation represents an interval by four variables of the solver: start, duration,
 * end (with the posted constraint <code>end == start + duration</code>) and performed (0/1).
 */
public class IntervalVar extends PropagationBaseObject {

  /** The smallest valid start or end value. */
  public static long getKMinValidValue() {
    return -(1L << 62);
  }

  /** The largest valid start or end value. */
  public static long getKMaxValidValue() {
    return 1L << 62;
  }

  final IntVar start;

  final IntVar duration;

  final IntVar end;

  final IntVar performed;

  IntervalVar(Solver solver, IntVar start, IntVar duration, IntVar end, IntVar performed,
      String name) {
    super(solver);
    this.start = start;
    this.duration = duration;
    this.end = end;
    this.performed = performed;
    setName(name);
  }

  /** The earliest start. */
  public long startMin() {
    return start.min();
  }

  /** The latest start. */
  public long startMax() {
    return start.max();
  }

  /** Restricts the start to be at least <code>m</code>. */
  public void setStartMin(long m) {
    start.setMin(m);
  }

  /** Restricts the start to be at most <code>m</code>. */
  public void setStartMax(long m) {
    start.setMax(m);
  }

  /** Restricts the start to <code>[mi, ma]</code>. */
  public void setStartRange(long mi, long ma) {
    start.setRange(mi, ma);
  }

  /** The earliest start (no history is kept). */
  public long oldStartMin() {
    return startMin();
  }

  /** The latest start (no history is kept). */
  public long oldStartMax() {
    return startMax();
  }

  /** The smallest duration. */
  public long durationMin() {
    return duration.min();
  }

  /** The largest duration. */
  public long durationMax() {
    return duration.max();
  }

  /** Restricts the duration to be at least <code>m</code>. */
  public void setDurationMin(long m) {
    duration.setMin(m);
  }

  /** Restricts the duration to be at most <code>m</code>. */
  public void setDurationMax(long m) {
    duration.setMax(m);
  }

  /** Restricts the duration to <code>[mi, ma]</code>. */
  public void setDurationRange(long mi, long ma) {
    duration.setRange(mi, ma);
  }

  /** The smallest duration (no history is kept). */
  public long oldDurationMin() {
    return durationMin();
  }

  /** The largest duration (no history is kept). */
  public long oldDurationMax() {
    return durationMax();
  }

  /** The earliest end. */
  public long endMin() {
    return end.min();
  }

  /** The latest end. */
  public long endMax() {
    return end.max();
  }

  /** Restricts the end to be at least <code>m</code>. */
  public void setEndMin(long m) {
    end.setMin(m);
  }

  /** Restricts the end to be at most <code>m</code>. */
  public void setEndMax(long m) {
    end.setMax(m);
  }

  /** Restricts the end to <code>[mi, ma]</code>. */
  public void setEndRange(long mi, long ma) {
    end.setRange(mi, ma);
  }

  /** The earliest end (no history is kept). */
  public long oldEndMin() {
    return endMin();
  }

  /** The latest end (no history is kept). */
  public long oldEndMax() {
    return endMax();
  }

  /** Whether the interval is known to be performed. */
  public boolean mustBePerformed() {
    return performed.min() == 1;
  }

  /** Whether the interval may be performed. */
  public boolean mayBePerformed() {
    return performed.max() == 1;
  }

  /** Whether the interval is known to be unperformed. */
  public boolean cannotBePerformed() {
    return performed.max() == 0;
  }

  /** Whether the performed status is decided. */
  public boolean isPerformedBound() {
    return performed.bound();
  }

  /** Decides the performed status. */
  public void setPerformed(boolean val) {
    performed.setValue(val ? 1 : 0);
  }

  /** Whether the performed status is decided (no history is kept). */
  public boolean wasPerformedBound() {
    return isPerformedBound();
  }

  /** The start variable. */
  public IntExpr startExpr() {
    return start;
  }

  /** The duration variable. */
  public IntExpr durationExpr() {
    return duration;
  }

  /** The end variable. */
  public IntExpr endExpr() {
    return end;
  }

  /** The 0/1 performed variable. */
  public IntExpr performedExpr() {
    return performed;
  }

  /** The start if performed, <code>unperformedValue</code> otherwise. */
  public IntExpr safeStartExpr(long unperformedValue) {
    return solver.makeConditionalExpression(performed, start, unperformedValue);
  }

  /** The duration if performed, <code>unperformedValue</code> otherwise. */
  public IntExpr safeDurationExpr(long unperformedValue) {
    return solver.makeConditionalExpression(performed, duration, unperformedValue);
  }

  /** The end if performed, <code>unperformedValue</code> otherwise. */
  public IntExpr safeEndExpr(long unperformedValue) {
    return solver.makeConditionalExpression(performed, end, unperformedValue);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(hasName() ? name() : "IntervalVar");
    buf.append("(start = ").append(start.cpVar.domainString());
    buf.append(", duration = ").append(duration.cpVar.domainString());
    buf.append(", end = ").append(end.cpVar.domainString());
    buf.append(", performed = ").append(performed.cpVar.domainString()).append(')');
    return buf.toString();
  }
}
