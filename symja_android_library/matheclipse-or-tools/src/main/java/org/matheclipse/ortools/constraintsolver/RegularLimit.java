package org.matheclipse.ortools.constraintsolver;

/**
 * A limit on wall time (milliseconds), branches, failures and solutions, mirroring
 * <code>com.google.ortools.constraintsolver.RegularLimit</code> of the OR-Tools Java wrapper
 * (reimplemented on the simplified engine of this module).
 *
 * <p>
 * Unless the limit is cumulative, the counters are measured from the start of each search.
 */
public class RegularLimit extends SearchLimit {

  private long timeMs;

  private long branches;

  private long failures;

  private long solutions;

  private final boolean smartTimeCheck;

  private final boolean cumulative;

  private long startTimeMs;

  private long startBranches;

  private long startFailures;

  private long startSolutions;

  private boolean started;

  RegularLimit(Solver s, long timeMs, long branches, long failures, long solutions,
      boolean smartTimeCheck, boolean cumulative) {
    super(s);
    this.timeMs = timeMs;
    this.branches = branches;
    this.failures = failures;
    this.solutions = solutions;
    this.smartTimeCheck = smartTimeCheck;
    this.cumulative = cumulative;
  }

  @Override
  public void init() {
    super.init();
    if (!cumulative || !started) {
      startTimeMs = solver.wallTime();
      startBranches = solver.branches();
      startFailures = solver.failures();
      startSolutions = solver.solutions();
      started = true;
    }
  }

  @Override
  boolean isLimitReached() {
    if (!started) {
      init();
    }
    return solver.wallTime() - startTimeMs >= timeMs
        || solver.branches() - startBranches >= branches
        || solver.failures() - startFailures >= failures
        || solver.solutions() - startSolutions >= solutions;
  }

  /** A new limit with the same settings. */
  @Override
  public SearchLimit makeClone() {
    return MakeIdenticalClone();
  }

  /** A new limit with the same settings. */
  public RegularLimit MakeIdenticalClone() {
    return new RegularLimit(solver, timeMs, branches, failures, solutions, smartTimeCheck,
        cumulative);
  }

  /** Copies the settings of <code>limit</code>, which must be a regular limit. */
  @Override
  public void copy(SearchLimit limit) {
    RegularLimit other = (RegularLimit) limit;
    timeMs = other.timeMs;
    branches = other.branches;
    failures = other.failures;
    solutions = other.solutions;
  }

  /** The time limit in milliseconds. */
  public long wall_time() {
    return timeMs;
  }

  /** The branch limit. */
  public long branches() {
    return branches;
  }

  /** The failure limit. */
  public long failures() {
    return failures;
  }

  /** The solution limit. */
  public long solutions() {
    return solutions;
  }

  /** Changes the limits (time in milliseconds). */
  public void UpdateLimits(long timeMs, long branches, long failures, long solutions) {
    this.timeMs = timeMs;
    this.branches = branches;
    this.failures = failures;
    this.solutions = solutions;
  }

  /** The elapsed fraction of the time limit in percent, or {@link #kNoProgress}. */
  @Override
  public int progressPercent() {
    if (timeMs == Long.MAX_VALUE || timeMs <= 0) {
      return kNoProgress;
    }
    return (int) Math.min(100, (solver.wallTime() - startTimeMs) * 100 / timeMs);
  }

  @Override
  public String toString() {
    return "RegularLimit(crossed = " + crossed() + ", duration_limit = " + timeMs
        + "ms, branches = " + branches + ", failures = " + failures + ", solutions = " + solutions
        + ")";
  }
}
