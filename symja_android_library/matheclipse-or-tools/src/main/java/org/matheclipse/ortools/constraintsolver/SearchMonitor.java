package org.matheclipse.ortools.constraintsolver;

/**
 * Observes (and influences) a search, mirroring
 * <code>com.google.ortools.constraintsolver.SearchMonitor</code> of the OR-Tools Java wrapper.
 *
 * <p>
 * Part of the reimplementation of the OR-Tools constraint solver API on a simpler engine. The
 * depth first search of {@link Solver} calls the hooks below; Java subclasses are supported.
 */
public class SearchMonitor extends BaseObject {

  /** Returned by {@link #progressPercent()} when no progress is known. */
  public static final int kNoProgress = -1;

  final Solver solver;

  /** Creates a monitor for searches of <code>s</code>. */
  public SearchMonitor(Solver s) {
    this.solver = s;
  }

  /** Kept for source compatibility with the wrapper: does nothing. */
  public void keepAliveDecisionBuilder(DecisionBuilder db) {}

  /** Called when a search starts. */
  public void enterSearch() {}

  /** Called when a search restarts (not used by this implementation). */
  public void restartSearch() {}

  /** Called when a search ends. */
  public void exitSearch() {}

  /** Called before the decision builder is asked for the next decision. */
  public void beginNextDecision(DecisionBuilder b) {}

  /** Called after the decision builder returned <code>d</code> (possibly null). */
  public void endNextDecision(DecisionBuilder b, Decision d) {}

  /** Called before a decision is applied. */
  public void applyDecision(Decision d) {}

  /** Called before a decision is refuted. */
  public void refuteDecision(Decision d) {}

  /** Called after a decision has been applied (<code>apply</code>) or refuted. */
  public void afterDecision(Decision d, boolean apply) {}

  /** Called when a failure starts. */
  public void beginFail() {}

  /** Called after a failure has been processed. */
  public void endFail() {}

  /** Called before the initial propagation. */
  public void beginInitialPropagation() {}

  /** Called after the initial propagation. */
  public void endInitialPropagation() {}

  /** Called when a solution is found; returning false rejects it. */
  public boolean acceptSolution() {
    return true;
  }

  /**
   * Called for an accepted solution; returning true asks {@link Solver#solve(DecisionBuilder)}
   * to continue the search.
   */
  public boolean atSolution() {
    return false;
  }

  /** Called when the search tree has been explored. */
  public void noMoreSolutions() {}

  /** Not used by this implementation (no local search). */
  public boolean AtLocalOptimum() {
    return false;
  }

  /** Not used by this implementation (no local search). */
  public boolean acceptDelta(Assignment delta, Assignment deltadelta) {
    return true;
  }

  /** Not used by this implementation (no local search). */
  public void acceptNeighbor() {}

  /** Not used by this implementation (no local search). */
  public void AcceptUncheckedNeighbor() {}

  /** Not used by this implementation. */
  public boolean IsUncheckedSolutionLimitReached() {
    return false;
  }

  /** Called periodically during the search. */
  public void periodicCheck() {}

  /** The estimated progress of the search, or {@link #kNoProgress}. */
  public int progressPercent() {
    return kNoProgress;
  }

  /** Not used: monitors are passed to the search methods of the solver. */
  public void install() {}

  /** The solver of this monitor. */
  public Solver solver() {
    return solver;
  }
}
