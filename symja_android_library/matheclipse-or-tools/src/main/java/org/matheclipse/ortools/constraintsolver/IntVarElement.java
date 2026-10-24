package org.matheclipse.ortools.constraintsolver;

/**
 * The stored bounds of one integer variable in an {@link Assignment}, mirroring
 * <code>com.google.ortools.constraintsolver.IntVarElement</code> of the OR-Tools Java wrapper.
 * <code>toString()</code> follows the C++ format: <code>(5)</code> or <code>(3..7)</code>.
 * Reimplemented for the simplified engine of this module.
 */
public class IntVarElement extends AssignmentElement implements Cloneable {

  private IntVar var;

  private long min;

  private long max;

  /** Creates an element without variable. */
  public IntVarElement() {
    this.min = Long.MIN_VALUE;
    this.max = Long.MAX_VALUE;
  }

  /** Creates an element for <code>var</code> holding its current bounds. */
  public IntVarElement(IntVar var) {
    reset(var);
  }

  /** Creates a copy of <code>element</code>. */
  public IntVarElement(IntVarElement element) {
    super(element);
    this.var = element.var;
    this.min = element.min;
    this.max = element.max;
  }

  /** Attaches the element to <code>var</code> and stores its current bounds. */
  public void reset(IntVar var) {
    this.var = var;
    if (var != null) {
      this.min = var.min();
      this.max = var.max();
    }
    activate();
  }

  /** A copy of this element. */
  public IntVarElement clone() {
    return new IntVarElement(this);
  }

  /** Copies the fields of <code>element</code>. */
  public void copy(IntVarElement element) {
    this.var = element.var;
    this.min = element.min;
    this.max = element.max;
    if (element.activated()) {
      activate();
    } else {
      deactivate();
    }
  }

  /** The variable. */
  public IntVar var() {
    return var;
  }

  /** Stores the current bounds of the variable. */
  public void store() {
    min = var.min();
    max = var.max();
  }

  /** Restricts the variable to the stored bounds. */
  public void restore() {
    if (activated()) {
      var.setRange(min, max);
    }
  }

  /** The stored minimum. */
  public long min() {
    return min;
  }

  /** Changes the stored minimum. */
  public void setMin(long m) {
    min = m;
  }

  /** The stored maximum. */
  public long max() {
    return max;
  }

  /** Changes the stored maximum. */
  public void setMax(long m) {
    max = m;
  }

  /** The stored value (the minimum when the stored bounds differ). */
  public long value() {
    return min;
  }

  /** Whether the stored bounds are equal. */
  public boolean bound() {
    return min == max;
  }

  /** Changes the stored bounds. */
  public void setRange(long l, long u) {
    min = l;
    max = u;
  }

  /** Stores a single value. */
  public void setValue(long v) {
    min = v;
    max = v;
  }

  @Override
  public String toString() {
    if (!activated()) {
      return "(...)";
    }
    return min == max ? "(" + min + ")" : "(" + min + ".." + max + ")";
  }
}
