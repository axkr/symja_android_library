package org.matheclipse.ortools.constraintsolver;

import org.matheclipse.ortools.constraintsolver.cp.CpFail;
import org.matheclipse.ortools.constraintsolver.cp.CpVar;

/**
 * An integer expression of a {@link Solver}, mirroring
 * <code>com.google.ortools.constraintsolver.IntExpr</code> of the OR-Tools Java wrapper.
 *
 * <p>
 * In this reimplementation every expression is backed by an engine variable whose defining
 * constraint is posted when the expression is created, so the expressions returned by the
 * <code>make...</code> methods of {@link Solver} are in fact {@link IntVar} instances and
 * {@link #var()} returns the expression itself.
 */
public class IntExpr extends PropagationBaseObject {

  final CpVar cpVar;

  IntExpr(Solver solver, CpVar v) {
    super(solver);
    this.cpVar = v;
    v.owner = this;
  }

  /** The smallest value of the expression in the current state. */
  public long min() {
    return cpVar.min();
  }

  /** The largest value of the expression in the current state. */
  public long max() {
    return cpVar.max();
  }

  /** Removes the values below <code>m</code>. */
  public void setMin(long m) {
    try {
      cpVar.setMin(m);
    } catch (CpFail f) {
      solver.domainFailure();
    }
  }

  /** Removes the values above <code>m</code>. */
  public void setMax(long m) {
    try {
      cpVar.setMax(m);
    } catch (CpFail f) {
      solver.domainFailure();
    }
  }

  /** Stores the bounds in <code>l[0]</code> and <code>u[0]</code>. */
  public void range(long[] l, long[] u) {
    l[0] = min();
    u[0] = max();
  }

  /** Restricts the expression to <code>[l, u]</code>. */
  public void setRange(long l, long u) {
    try {
      cpVar.setRange(l, u);
    } catch (CpFail f) {
      solver.domainFailure();
    }
  }

  /** Binds the expression to <code>v</code>. */
  public void setValue(long v) {
    try {
      cpVar.setValue(v);
    } catch (CpFail f) {
      solver.domainFailure();
    }
  }

  /** Whether the expression has a single possible value. */
  public boolean bound() {
    return cpVar.isBound();
  }

  /** Always true in this implementation: every expression is backed by a variable. */
  public boolean isVar() {
    return true;
  }

  /** The variable equal to this expression (the expression itself). */
  public IntVar var() {
    return (IntVar) this;
  }

  /** {@link #var()} with a new name. */
  public IntVar varWithName(String name) {
    IntVar v = var();
    v.setName(name);
    return v;
  }

  @Override
  public String name() {
    return cpVar.name == null ? "" : cpVar.name;
  }

  @Override
  public void setName(String name) {
    cpVar.name = name == null || name.isEmpty() ? null : name;
  }

  /** The engine variable behind this expression; for use by the routing layer. */
  CpVar engineVar() {
    return cpVar;
  }

  @Override
  public String toString() {
    String n = name();
    return (n.isEmpty() ? "DomainIntVar" : n) + "(" + cpVar.domainString() + ")";
  }
}
