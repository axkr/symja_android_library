package org.matheclipse.ortools.constraintsolver;

/**
 * The stored bounds of one interval variable in an {@link Assignment}, mirroring
 * <code>com.google.ortools.constraintsolver.IntervalVarElement</code> of the OR-Tools Java
 * wrapper. <code>toString()</code> follows the C++ format, e.g.
 * <code>(start = 50..60, duration = 10, status = 1)</code>. Reimplemented for the
 * simplified engine of this module.
 */
public class IntervalVarElement extends AssignmentElement implements Cloneable {

  private IntervalVar var;

  private long startMin;

  private long startMax;

  private long durationMin;

  private long durationMax;

  private long endMin;

  private long endMax;

  private long performedMin;

  private long performedMax;

  /** Creates an element without variable. */
  public IntervalVarElement() {}

  /** Creates an element for <code>var</code> holding its current bounds. */
  public IntervalVarElement(IntervalVar var) {
    reset(var);
  }

  /** Attaches the element to <code>var</code> and stores its current bounds. */
  public void reset(IntervalVar var) {
    this.var = var;
    if (var != null) {
      store();
    }
    activate();
  }

  /** A copy of this element. */
  public IntervalVarElement clone() {
    IntervalVarElement e = new IntervalVarElement();
    e.copy(this);
    return e;
  }

  /** Copies the fields of <code>element</code>. */
  public void copy(IntervalVarElement element) {
    var = element.var;
    startMin = element.startMin;
    startMax = element.startMax;
    durationMin = element.durationMin;
    durationMax = element.durationMax;
    endMin = element.endMin;
    endMax = element.endMax;
    performedMin = element.performedMin;
    performedMax = element.performedMax;
    if (element.activated()) {
      activate();
    } else {
      deactivate();
    }
  }

  /** The interval variable. */
  public IntervalVar var() {
    return var;
  }

  /** Stores the current bounds of the variable. */
  public void store() {
    startMin = var.startMin();
    startMax = var.startMax();
    durationMin = var.durationMin();
    durationMax = var.durationMax();
    endMin = var.endMin();
    endMax = var.endMax();
    performedMin = var.mustBePerformed() ? 1 : 0;
    performedMax = var.mayBePerformed() ? 1 : 0;
  }

  /** Restricts the variable to the stored bounds. */
  public void restore() {
    if (activated()) {
      var.setStartRange(startMin, startMax);
      var.setDurationRange(durationMin, durationMax);
      var.setEndRange(endMin, endMax);
      if (performedMin == performedMax) {
        var.setPerformed(performedMin == 1);
      }
    }
  }

  /** The stored earliest start. */
  public long startMin() {
    return startMin;
  }

  /** The stored latest start. */
  public long startMax() {
    return startMax;
  }

  /** The stored start (the earliest one if not fixed). */
  public long startValue() {
    return startMin;
  }

  /** The stored smallest duration. */
  public long durationMin() {
    return durationMin;
  }

  /** The stored largest duration. */
  public long durationMax() {
    return durationMax;
  }

  /** The stored duration (the smallest one if not fixed). */
  public long durationValue() {
    return durationMin;
  }

  /** The stored earliest end. */
  public long endMin() {
    return endMin;
  }

  /** The stored latest end. */
  public long endMax() {
    return endMax;
  }

  /** The stored end (the earliest one if not fixed). */
  public long endValue() {
    return endMin;
  }

  /** The stored minimum of the performed status. */
  public long performedMin() {
    return performedMin;
  }

  /** The stored maximum of the performed status. */
  public long performedMax() {
    return performedMax;
  }

  /** The stored performed status (the minimum if not fixed). */
  public long performedValue() {
    return performedMin;
  }

  /** Changes the stored earliest start. */
  public void setStartMin(long m) {
    startMin = m;
  }

  /** Changes the stored latest start. */
  public void setStartMax(long m) {
    startMax = m;
  }

  /** Changes the stored start range. */
  public void setStartRange(long mi, long ma) {
    startMin = mi;
    startMax = ma;
  }

  /** Stores a fixed start. */
  public void setStartValue(long v) {
    startMin = v;
    startMax = v;
  }

  /** Changes the stored smallest duration. */
  public void setDurationMin(long m) {
    durationMin = m;
  }

  /** Changes the stored largest duration. */
  public void setDurationMax(long m) {
    durationMax = m;
  }

  /** Changes the stored duration range. */
  public void setDurationRange(long mi, long ma) {
    durationMin = mi;
    durationMax = ma;
  }

  /** Stores a fixed duration. */
  public void setDurationValue(long v) {
    durationMin = v;
    durationMax = v;
  }

  /** Changes the stored earliest end. */
  public void setEndMin(long m) {
    endMin = m;
  }

  /** Changes the stored latest end. */
  public void setEndMax(long m) {
    endMax = m;
  }

  /** Changes the stored end range. */
  public void setEndRange(long mi, long ma) {
    endMin = mi;
    endMax = ma;
  }

  /** Stores a fixed end. */
  public void setEndValue(long v) {
    endMin = v;
    endMax = v;
  }

  /** Changes the stored minimum of the performed status. */
  public void setPerformedMin(long m) {
    performedMin = m;
  }

  /** Changes the stored maximum of the performed status. */
  public void setPerformedMax(long m) {
    performedMax = m;
  }

  /** Changes the stored range of the performed status. */
  public void setPerformedRange(long mi, long ma) {
    performedMin = mi;
    performedMax = ma;
  }

  /** Stores a fixed performed status. */
  public void setPerformedValue(long v) {
    performedMin = v;
    performedMax = v;
  }

  /** Whether all stored ranges are single values. */
  public boolean bound() {
    return performedMin == performedMax
        && (performedMin == 0 || (startMin == startMax && durationMin == durationMax
            && endMin == endMax));
  }

  @Override
  public String toString() {
    if (!activated()) {
      return "(...)";
    }
    StringBuilder out = new StringBuilder();
    out.append("(start = ").append(startMin);
    if (startMax != startMin) {
      out.append("..").append(startMax);
    }
    out.append(", duration = ").append(durationMin);
    if (durationMax != durationMin) {
      out.append("..").append(durationMax);
    }
    out.append(", status = ").append(performedMin);
    if (performedMax != performedMin) {
      out.append("..").append(performedMax);
    }
    out.append(')');
    return out.toString();
  }
}
