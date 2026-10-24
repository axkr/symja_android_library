package org.matheclipse.ortools.constraintsolver;

import java.util.ArrayList;

/**
 * Records solutions of a search, mirroring
 * <code>com.google.ortools.constraintsolver.SolutionCollector</code> of the OR-Tools Java wrapper
 * (reimplemented on the simplified engine of this module).
 *
 * <p>
 * The variables to record are given by {@link #add(IntVar)} (or by the prototype assignment of
 * the factory methods of {@link Solver}); depending on the kind of collector the first, the last,
 * the best or all solutions are kept.
 */
public class SolutionCollector extends SearchMonitor {

  enum Kind {
    FIRST, LAST, ALL, BEST, PLAIN
  }

  private static final class Entry {
    final Assignment solution;

    final long wallTime;

    final long branches;

    final long failures;

    final long objective;

    Entry(Assignment solution, long wallTime, long branches, long failures, long objective) {
      this.solution = solution;
      this.wallTime = wallTime;
      this.branches = branches;
      this.failures = failures;
      this.objective = objective;
    }
  }

  private final Assignment prototype;

  private final Kind kind;

  private final boolean maximize;

  private final ArrayList<Entry> entries = new ArrayList<>();

  SolutionCollector(Solver solver, Assignment prototype, Kind kind, boolean maximize) {
    super(solver);
    this.prototype = prototype == null ? new Assignment(solver) : new Assignment(prototype);
    this.kind = kind;
    this.maximize = maximize;
  }

  /** Creates a collector which records every solution of the variables of the assignment. */
  public SolutionCollector(Solver solver, Assignment assignment) {
    this(solver, assignment, Kind.PLAIN, false);
  }

  /** Creates a collector which records every solution. */
  public SolutionCollector(Solver solver) {
    this(solver, null, Kind.PLAIN, false);
  }

  /** Records <code>var</code> in the solutions. */
  public void add(IntVar var) {
    prototype.add(var);
  }

  /** Records all <code>vars</code> in the solutions. */
  public void add(IntVar[] vars) {
    prototype.add(vars);
  }

  /** Records the interval <code>var</code> in the solutions. */
  public void add(IntervalVar var) {
    prototype.add(var);
  }

  /** Records all intervals in the solutions. */
  public void add(IntervalVar[] vars) {
    prototype.add(vars);
  }

  /** Records <code>objective</code> as the objective of the solutions. */
  public void addObjective(IntVar objective) {
    prototype.addObjective(objective);
  }

  @Override
  public void enterSearch() {
    entries.clear();
  }

  @Override
  public boolean atSolution() {
    Assignment snapshot = new Assignment(prototype);
    snapshot.store();
    long objectiveValue = snapshot.hasObjective() ? snapshot.objectiveValue() : 0;
    Entry e = new Entry(snapshot, solver.wallTime(), solver.branches(), solver.failures(),
        objectiveValue);
    switch (kind) {
      case FIRST:
        if (entries.isEmpty()) {
          entries.add(e);
        }
        return false;
      case LAST:
        entries.clear();
        entries.add(e);
        return true;
      case BEST:
        if (entries.isEmpty() || (maximize ? objectiveValue > entries.get(0).objective
            : objectiveValue < entries.get(0).objective)) {
          entries.clear();
          entries.add(e);
        }
        return true;
      default:
        entries.add(e);
        return true;
    }
  }

  /** The number of recorded solutions. */
  public int solutionCount() {
    return entries.size();
  }

  /** Whether a solution has been recorded. */
  public boolean has_solution() {
    return !entries.isEmpty();
  }

  /** The <code>n</code>-th recorded solution. */
  public Assignment solution(int n) {
    return entries.get(n).solution;
  }

  /** The last recorded solution or null. */
  public Assignment last_solution_or_null() {
    return entries.isEmpty() ? null : entries.get(entries.size() - 1).solution;
  }

  /** The wall time in milliseconds at which solution <code>n</code> was found. */
  public long wallTime(int n) {
    return entries.get(n).wallTime;
  }

  /** The number of branches when solution <code>n</code> was found. */
  public long branches(int n) {
    return entries.get(n).branches;
  }

  /** The number of failures when solution <code>n</code> was found. */
  public long failures(int n) {
    return entries.get(n).failures;
  }

  /** The objective value of solution <code>n</code>. */
  public long objectiveValue(int n) {
    return entries.get(n).objective;
  }

  /** The value of <code>var</code> in solution <code>n</code>. */
  public long value(int n, IntVar var) {
    return solution(n).value(var);
  }

  /** The start of <code>var</code> in solution <code>n</code>. */
  public long startValue(int n, IntervalVar var) {
    return solution(n).startValue(var);
  }

  /** The end of <code>var</code> in solution <code>n</code>. */
  public long endValue(int n, IntervalVar var) {
    return solution(n).endValue(var);
  }

  /** The duration of <code>var</code> in solution <code>n</code>. */
  public long durationValue(int n, IntervalVar var) {
    return solution(n).durationValue(var);
  }

  /** The performed status of <code>var</code> in solution <code>n</code>. */
  public long performedValue(int n, IntervalVar var) {
    return solution(n).performedValue(var);
  }

  @Override
  public String toString() {
    return kind.name().toLowerCase() + "SolutionCollector(" + entries.size() + " solutions)";
  }
}
