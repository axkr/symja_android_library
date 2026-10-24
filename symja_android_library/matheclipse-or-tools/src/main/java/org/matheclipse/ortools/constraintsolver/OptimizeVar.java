package org.matheclipse.ortools.constraintsolver;

/**
 * Turns a search into an optimization, mirroring
 * <code>com.google.ortools.constraintsolver.OptimizeVar</code> of the OR-Tools Java wrapper
 * (reimplemented on the simplified engine of this module).
 *
 * <p>
 * After each solution the objective must improve by at least <code>step</code>: the bound is
 * applied before every decision and after every refutation (branch and bound). A search run by
 * {@link Solver#solve(DecisionBuilder, SearchMonitor)} with this monitor continues until the
 * search tree has been explored, so the last solution is optimal.
 */
public class OptimizeVar extends ObjectiveMonitor {

  /** Creates a monitor minimizing (or maximizing) <code>var</code>. */
  public OptimizeVar(Solver solver, boolean maximize, IntVar var, long step) {
    super(solver, maximize, var, step);
  }

  /** The best objective value found so far. */
  public long best() {
    return best;
  }

  /** The objective variable. */
  public IntVar var() {
    return objective;
  }

  @Override
  public void beginNextDecision(DecisionBuilder db) {
    applyBound();
  }

  @Override
  public void refuteDecision(Decision d) {
    // the bound is applied after the refutation by afterDecision
  }

  @Override
  public void afterDecision(Decision d, boolean apply) {
    applyBound();
  }

  @Override
  public void enterSearch() {
    super.enterSearch();
  }

  @Override
  public boolean acceptSolution() {
    if (!found || !is_active()) {
      return true;
    }
    long v = objective.value();
    return maximize ? v >= best + step : v <= best - step;
  }

  @Override
  public boolean atSolution() {
    return super.atSolution();
  }

  /** Restricts the objective to values which improve the best solution by the step. */
  public void applyBound() {
    if (found && is_active() && solver.insideSearch()) {
      if (maximize) {
        objective.setMin(best + step);
      } else {
        objective.setMax(best - step);
      }
    }
  }

  /** The name of the monitor. */
  public String Name() {
    return maximize ? "MaximizeVar" : "MinimizeVar";
  }

  @Override
  public String toString() {
    return (maximize ? "MaximizeVar(" : "MinimizeVar(") + objective + ", step = " + step
        + ", best = " + (found ? Long.toString(best) : "none") + ")";
  }
}
