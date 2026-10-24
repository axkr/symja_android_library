package org.matheclipse.ortools.constraintsolver;

/**
 * An objective monitor for one variable, mirroring
 * <code>com.google.ortools.constraintsolver.ObjectiveMonitor</code> of the OR-Tools Java wrapper
 * (reimplemented on the simplified engine of this module): it records the best value of the
 * objective at each solution.
 */
public class ObjectiveMonitor extends BaseObjectiveMonitor {

  final IntVar objective;

  final boolean maximize;

  final long step;

  long best;

  boolean found;

  ObjectiveMonitor(Solver solver, boolean maximize, IntVar var, long step) {
    super(solver);
    if (step <= 0) {
      throw new IllegalArgumentException("step must be positive");
    }
    this.objective = var;
    this.maximize = maximize;
    this.step = step;
    this.best = maximize ? Long.MIN_VALUE : Long.MAX_VALUE;
  }

  @Override
  public IntVar ObjectiveVar(int index) {
    return objective;
  }

  @Override
  public IntVar MinimizationVar(int index) {
    return maximize ? solver.makeOpposite(objective).var() : objective;
  }

  @Override
  public long Step(int index) {
    return step;
  }

  @Override
  public boolean Maximize(int index) {
    return maximize;
  }

  @Override
  public long BestValue(int index) {
    return best;
  }

  @Override
  public void enterSearch() {
    found = false;
    best = maximize ? Long.MIN_VALUE : Long.MAX_VALUE;
  }

  @Override
  public boolean atSolution() {
    long v = objective.value();
    if (!found || (maximize ? v > best : v < best)) {
      best = v;
    }
    found = true;
    return true;
  }

  @Override
  public boolean acceptDelta(Assignment delta, Assignment deltadelta) {
    return true;
  }
}
