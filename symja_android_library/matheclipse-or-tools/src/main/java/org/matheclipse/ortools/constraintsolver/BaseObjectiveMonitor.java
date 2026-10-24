package org.matheclipse.ortools.constraintsolver;

/**
 * The common base of the objective monitors, mirroring
 * <code>com.google.ortools.constraintsolver.BaseObjectiveMonitor</code> of the OR-Tools Java
 * wrapper. This reimplementation supports a single objective variable.
 */
public class BaseObjectiveMonitor extends SearchMonitor {

  private boolean active = true;

  BaseObjectiveMonitor(Solver s) {
    super(s);
  }

  /** The objective variable (only index 0 exists). */
  public IntVar ObjectiveVar(int index) {
    throw new UnsupportedOperationException();
  }

  /** The variable which is minimized (the objective, or its opposite when maximizing). */
  public IntVar MinimizationVar(int index) {
    throw new UnsupportedOperationException();
  }

  /** The improvement step. */
  public long Step(int index) {
    throw new UnsupportedOperationException();
  }

  /** Whether the objective is maximized. */
  public boolean Maximize(int index) {
    throw new UnsupportedOperationException();
  }

  /** The best objective value found so far. */
  public long BestValue(int index) {
    throw new UnsupportedOperationException();
  }

  /** The number of objectives (1). */
  public int Size() {
    return 1;
  }

  /** Whether the monitor enforces its bound. */
  public boolean is_active() {
    return active;
  }

  /** Enables or disables the bound enforcement. */
  public void set_active(boolean isActive) {
    active = isActive;
  }
}
