package org.matheclipse.ortools.constraintsolver;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Logs the progress of a search through <code>java.util.logging</code>, mirroring
 * <code>com.google.ortools.constraintsolver.SearchLog</code> of the OR-Tools Java wrapper (whose
 * C++ counterpart writes to the glog INFO stream). Reimplemented on the simplified engine of this
 * module.
 */
public class SearchLog extends SearchMonitor {

  private static final Logger LOGGER = Logger.getLogger(SearchLog.class.getName());

  private final int period;

  private final IntVar[] vars;

  private final OptimizeVar objective;

  private final Supplier<String> display;

  private long solutionCount;

  private long lastBranchReport;

  SearchLog(Solver s, int period, IntVar[] vars, OptimizeVar objective, Supplier<String> display) {
    super(s);
    this.period = period;
    this.vars = vars;
    this.objective = objective;
    this.display = display;
  }

  private String counters() {
    return "time = " + solver.wallTime() + " ms, branches = " + solver.branches()
        + ", failures = " + solver.failures() + ", memory used = "
        + (Solver.usedMemory() >> 20) + " MB";
  }

  @Override
  public void enterSearch() {
    solutionCount = 0;
    lastBranchReport = solver.branches();
    LOGGER.info("Start search (memory used = " + (Solver.usedMemory() >> 20) + " MB)");
  }

  @Override
  public void exitSearch() {
    LOGGER.info("End search (" + counters() + ", solutions = " + solutionCount + ")");
  }

  @Override
  public boolean atSolution() {
    solutionCount++;
    StringBuilder buf = new StringBuilder("Solution #").append(solutionCount).append(" (");
    if (objective != null) {
      buf.append("objective value = ").append(valueString(objective.var())).append(", ");
    }
    if (vars != null) {
      for (IntVar v : vars) {
        buf.append(v.name()).append(" = ").append(valueString(v)).append(", ");
      }
    }
    buf.append(counters());
    if (display != null) {
      buf.append(", ").append(display.get());
    }
    buf.append(')');
    LOGGER.info(buf.toString());
    return false;
  }

  private static String valueString(IntVar v) {
    return v.bound() ? Long.toString(v.value()) : v.min() + ".." + v.max();
  }

  @Override
  public void beginNextDecision(DecisionBuilder b) {
    if (period > 0 && solver.branches() - lastBranchReport >= period) {
      lastBranchReport = solver.branches();
      LOGGER.info(solver.branches() + " branches, " + counters());
    }
  }

  @Override
  public void noMoreSolutions() {
    LOGGER.info("Finished search tree (" + counters() + ")");
  }

  @Override
  public void beginInitialPropagation() {
    // nothing to report
  }

  @Override
  public void endInitialPropagation() {
    LOGGER.fine("Root node processed (" + counters() + ")");
  }

  @Override
  public String toString() {
    return "SearchLog";
  }
}
