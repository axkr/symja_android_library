package org.matheclipse.ortools.constraintsolver;

import org.matheclipse.ortools.constraintsolver.cp.CpFail;
import org.matheclipse.ortools.constraintsolver.cp.CpVar;

/**
 * An integer variable of a {@link Solver}, mirroring
 * <code>com.google.ortools.constraintsolver.IntVar</code> of the OR-Tools Java wrapper
 * (reimplemented on the simplified engine of this module).
 *
 * <p>
 * The domain is an interval with optional holes. Outside a search, modifications change the
 * model; inside a search they are undone on backtracking. <code>toString()</code> follows the
 * C++ <code>DebugString()</code>: <code>name(8)</code> when bound, <code>name(0..100)</code>
 * otherwise.
 */
public class IntVar extends IntExpr {

  IntVar(Solver solver, CpVar v) {
    super(solver, v);
  }

  @Override
  public boolean isVar() {
    return true;
  }

  @Override
  public IntVar var() {
    return this;
  }

  /** The value of a bound variable (the minimum if the variable is not bound). */
  public long value() {
    return cpVar.value();
  }

  /** Removes <code>v</code> from the domain. */
  public void removeValue(long v) {
    try {
      cpVar.removeValue(v);
    } catch (CpFail f) {
      solver.domainFailure();
    }
  }

  /** Removes the values of <code>[l, u]</code> from the domain. */
  public void removeInterval(long l, long u) {
    try {
      cpVar.removeInterval(l, u);
    } catch (CpFail f) {
      solver.domainFailure();
    }
  }

  /** Removes all the given values from the domain. */
  public void removeValues(long[] values) {
    for (long v : values) {
      removeValue(v);
    }
  }

  /** Restricts the domain to the given values. */
  public void setValue(long[] values) {
    if (values.length == 0) {
      solver.domainFailure();
      return;
    }
    long[] sorted = values.clone();
    java.util.Arrays.sort(sorted);
    setRange(sorted[0], sorted[sorted.length - 1]);
    for (int i = 0; i + 1 < sorted.length; i++) {
      if (sorted[i + 1] > sorted[i] + 1) {
        removeInterval(sorted[i] + 1, sorted[i + 1] - 1);
      }
    }
  }

  /** The number of values of the domain. */
  public long size() {
    return cpVar.size();
  }

  /** Whether <code>v</code> belongs to the domain. */
  public boolean contains(long v) {
    return cpVar.contains(v);
  }

  /** The minimum (this implementation keeps no history of the domain). */
  public long oldMin() {
    return min();
  }

  /** The maximum (this implementation keeps no history of the domain). */
  public long oldMax() {
    return max();
  }

  /** The type tag of the variable; 0 (domain variable) in this implementation. */
  public int varType() {
    return 0;
  }

  /** A 0/1 variable equal to <code>this == constant</code>. */
  public IntVar isEqual(long constant) {
    return solver.makeIsEqualCstVar(this, constant);
  }

  /** A 0/1 variable equal to <code>this != constant</code>. */
  public IntVar isDifferent(long constant) {
    return solver.makeIsDifferentCstVar(this, constant);
  }

  /** A 0/1 variable equal to <code>this &gt;= constant</code>. */
  public IntVar isGreaterOrEqual(long constant) {
    return solver.makeIsGreaterOrEqualCstVar(this, constant);
  }

  /** A 0/1 variable equal to <code>this &lt;= constant</code>. */
  public IntVar isLessOrEqual(long constant) {
    return solver.makeIsLessOrEqualCstVar(this, constant);
  }

  /** The index of the variable in its solver. */
  public int index() {
    return cpVar.id;
  }
}
