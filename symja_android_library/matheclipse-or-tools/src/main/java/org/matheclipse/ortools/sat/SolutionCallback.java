package org.matheclipse.ortools.sat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Base class of the solution callbacks; replaces the SWIG director class {@code SolutionCallback}
 * of the OR-Tools Java wrapper. {@link SolveWrapper} calls {@link #onSolutionCallback()} for each
 * solution found by the search; during the call the methods of this class give access to the
 * solution and to the search statistics.
 *
 * <p>
 * Users normally extend {@link CpSolverSolutionCallback}.
 */
@SuppressFBWarnings(
    value = {"AT_NONATOMIC_OPERATIONS_ON_SHARED_VARIABLE", "AT_STALE_THREAD_WRITE_OF_PRIMITIVE",
        "AT_NONATOMIC_64BIT_PRIMITIVE"},
    justification = "statistics are used on the solving thread; only wrapper is shared")
public class SolutionCallback {
  private long[] solution = new long[0];
  private double objectiveValue;
  private double bestObjectiveBound;
  private long numBooleans;
  private long numBranches;
  private long numConflicts;
  private long numPropagations;
  private double wallTime;
  private volatile SolveWrapper wrapper;

  /** Creates a callback. */
  public SolutionCallback() {}

  /** Called at each new solution. */
  public void onSolutionCallback() {}

  void setContext(SolveWrapper wrapper, long[] solution, double objectiveValue,
      double bestObjectiveBound, long numBooleans, long numBranches, long numConflicts,
      long numPropagations, double wallTime) {
    this.wrapper = wrapper;
    this.solution = solution;
    this.objectiveValue = objectiveValue;
    this.bestObjectiveBound = bestObjectiveBound;
    this.numBooleans = numBooleans;
    this.numBranches = numBranches;
    this.numConflicts = numConflicts;
    this.numPropagations = numPropagations;
    this.wallTime = wallTime;
  }

  void clearContext() {
    this.wrapper = null;
  }

  /** Returns the number of Boolean variables of the model. */
  public long numBooleans() {
    return numBooleans;
  }

  /** Returns the number of search branches explored so far. */
  public long numBranches() {
    return numBranches;
  }

  /** Returns the number of conflicts (failures) so far. */
  public long numConflicts() {
    return numConflicts;
  }

  /** Returns the number of Boolean propagations; the Java engine counts all propagations. */
  public long numBinaryPropagations() {
    return numPropagations;
  }

  /** Returns the number of propagator calls so far. */
  public long numIntegerPropagations() {
    return numPropagations;
  }

  /** Returns the wall time in seconds since the start of the solve. */
  public double wallTime() {
    return wallTime;
  }

  /** Returns the user time in seconds; the same as {@link #wallTime()}. */
  public double userTime() {
    return wallTime;
  }

  /** Returns the objective value of the current solution. */
  public double objectiveValue() {
    return objectiveValue;
  }

  /** Returns the best proven bound of the objective so far. */
  public double bestObjectiveBound() {
    return bestObjectiveBound;
  }

  /** Returns the value of the variable (or of its negation for a negative index). */
  public long solutionIntegerValue(int index) {
    if (index >= 0) {
      return solution[index];
    }
    return -solution[-index - 1];
  }

  /** Returns the value of a literal in the current solution. */
  public boolean solutionBooleanValue(int index) {
    if (index >= 0) {
      return solution[index] != 0;
    }
    return solution[-index - 1] == 0;
  }

  /** Stops the search after the current callback returns. */
  public void stopSearch() {
    SolveWrapper w = wrapper;
    if (w != null) {
      w.stopSearch();
    }
  }
}
