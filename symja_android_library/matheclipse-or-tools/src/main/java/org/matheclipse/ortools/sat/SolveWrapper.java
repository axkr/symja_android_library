package org.matheclipse.ortools.sat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.matheclipse.ortools.sat.engine.CpModelValidator;
import org.matheclipse.ortools.sat.engine.CpSatEngine;

/**
 * Solves a {@link CpModelProto} with the Java constraint engine of this module; replaces the SWIG
 * class {@code SolveWrapper} which calls the C++ CP-SAT solver in the OR-Tools Java wrapper.
 *
 * <p>
 * The status follows the CP-SAT conventions: a model without objective is {@code OPTIMAL} as soon
 * as a solution is found (with {@code enumerate_all_solutions} once all solutions have been
 * enumerated); a model with objective is {@code OPTIMAL} when the search proved that no better
 * solution exists, {@code FEASIBLE} when it was stopped (time limit, {@link #stopSearch()},
 * {@code stop_after_first_solution}) after finding a solution. {@code INFEASIBLE} is only
 * reported after a complete search; with assumptions, a subset of them which is sufficient for the
 * infeasibility is computed by deletion.
 *
 * <p>
 * A {@link RuntimeException} (or {@link Error}) thrown by a solution, log or best bound callback
 * ends the search and propagates out of {@link #solve(CpModelProto)}, as in OR-Tools 10.0 where
 * the Java exception pending in the callback is thrown when the native solve returns.
 */
@SuppressFBWarnings(
    value = {"AT_NONATOMIC_OPERATIONS_ON_SHARED_VARIABLE", "AT_STALE_THREAD_WRITE_OF_PRIMITIVE",
        "AT_NONATOMIC_64BIT_PRIMITIVE"},
    justification = "confined to the solving thread; only stopRequested and engine are shared")
public class SolveWrapper {
  private SatParameters parameters = SatParameters.getDefaultInstance();
  private final List<SolutionCallback> solutionCallbacks = new ArrayList<>();
  private final List<Consumer<String>> logCallbacks = new ArrayList<>();
  private final List<Consumer<Double>> bestBoundCallbacks = new ArrayList<>();
  private volatile boolean stopRequested;
  private volatile CpSatEngine engine;
  private StringBuilder solveLog;
  private long startNanos;

  /** Creates a wrapper with default parameters. */
  public SolveWrapper() {}

  /** Sets the parameters of the next solve. */
  public void setParameters(SatParameters parameters) {
    this.parameters = parameters;
  }

  /** Adds a callback called at each solution. */
  public void addSolutionCallback(SolutionCallback callback) {
    solutionCallbacks.add(callback);
  }

  /** Removes a solution callback. */
  public void clearSolutionCallback(SolutionCallback callback) {
    solutionCallbacks.remove(callback);
  }

  /** Adds a callback receiving the log lines (when log_search_progress is true). */
  public void addLogCallback(Consumer<String> logCallback) {
    logCallbacks.add(logCallback);
  }

  /** Adds a callback receiving each new best objective bound. */
  public void addBestBoundCallback(Consumer<Double> bestBoundCallback) {
    bestBoundCallbacks.add(bestBoundCallback);
  }

  /** Stops the search as soon as possible. Thread safe. */
  public void stopSearch() {
    stopRequested = true;
    CpSatEngine e = engine;
    if (e != null) {
      e.stop();
    }
  }

  private void log(String line) {
    if (!parameters.getLogSearchProgress()) {
      return;
    }
    String text = parameters.getLogPrefix() + line;
    for (Consumer<String> cb : logCallbacks) {
      cb.accept(text);
    }
    if (parameters.getLogToStdout()) {
      System.out.println(text);
    }
    if (solveLog != null) {
      solveLog.append(text).append('\n');
    }
  }

  private double elapsed() {
    return (System.nanoTime() - startNanos) * 1e-9;
  }

  /** Solves the model and returns the response. */
  public CpSolverResponse solve(CpModelProto model) {
    startNanos = System.nanoTime();
    stopRequested = false;
    solveLog = parameters.getLogToResponse() ? new StringBuilder() : null;
    CpSolverResponse.Builder response = CpSolverResponse.newBuilder();
    log("");
    log("Starting CP-SAT solver v" + org.matheclipse.ortools.init.OrToolsVersion.getVersionString()
        + " (Java engine of the Symja matheclipse-or-tools module)");
    log("Parameters: " + TextFormat.shortDebugString(parameters));
    log("");
    String error = CpModelValidator.validateInput(model, parameters.getMipMaxValidMagnitude());
    if (!error.isEmpty()) {
      log("Invalid model: " + error);
      response.setStatus(CpSolverStatus.MODEL_INVALID);
      response.setSolutionInfo(error);
      return finish(response, null);
    }
    log("Initial " + CpSatHelper.modelStats(model).trim().replace("\n", "\n"));
    CpSatEngine e = new CpSatEngine(model, parameters);
    engine = e;
    if (stopRequested) {
      e.stop();
    }
    boolean enumerate = parameters.getEnumerateAllSolutions();
    boolean hasObjective = e.hasObjective();
    long[] best = null;
    long count = 0;
    List<long[]> pool = new ArrayList<>();
    double lastBound = Double.NaN;
    while (e.nextSolution()) {
      best = e.solution();
      count++;
      if (parameters.getFillAdditionalSolutionsInResponse()) {
        pool.add(best);
      }
      if (hasObjective) {
        log(String.format(java.util.Locale.ROOT, "#%-5d %6.2fs best:%s", count, elapsed(),
            format(e.objectiveValue())));
        double bound = e.bestObjectiveBound();
        if (!Double.isInfinite(bound) && !Double.isNaN(bound) && bound != lastBound) {
          lastBound = bound;
          notifyBound(bound);
        }
      } else {
        log(String.format(java.util.Locale.ROOT, "#%-5d %6.2fs", count, elapsed()));
      }
      for (SolutionCallback cb : new ArrayList<>(solutionCallbacks)) {
        cb.setContext(this, best, e.objectiveValue(), e.bestObjectiveBound(), numBooleans(model),
            e.numBranches(), e.numConflicts(), e.numPropagations(), elapsed());
        try {
          cb.onSolutionCallback();
        } finally {
          cb.clearContext();
        }
      }
      if (stopRequested) {
        break;
      }
      if (!hasObjective && !enumerate) {
        break;
      }
      if (hasObjective && parameters.getStopAfterFirstSolution()) {
        break;
      }
    }
    CpSolverStatus status;
    if (count > 0) {
      if (hasObjective) {
        status = e.isExhausted() ? CpSolverStatus.OPTIMAL : CpSolverStatus.FEASIBLE;
      } else if (enumerate) {
        status = e.isExhausted() ? CpSolverStatus.OPTIMAL : CpSolverStatus.FEASIBLE;
      } else {
        status = CpSolverStatus.OPTIMAL;
      }
    } else {
      status = e.isExhausted() ? CpSolverStatus.INFEASIBLE : CpSolverStatus.UNKNOWN;
    }
    response.setStatus(status);
    if (best != null) {
      for (long v : best) {
        response.addSolution(v);
      }
      if (hasObjective) {
        response.setObjectiveValue(e.objectiveValue());
        response.setInnerObjectiveLowerBound(
            status == CpSolverStatus.OPTIMAL ? e.innerObjectiveValue() : 0);
      }
    }
    if (hasObjective) {
      double bound = e.bestObjectiveBound();
      if (status == CpSolverStatus.OPTIMAL) {
        bound = e.objectiveValue();
      }
      if (!Double.isNaN(bound) && !Double.isInfinite(bound)) {
        response.setBestObjectiveBound(bound);
        if (bound != lastBound) {
          notifyBound(bound);
        }
      }
    }
    for (long[] sol : pool) {
      CpSolverSolution.Builder b = response.addAdditionalSolutionsBuilder();
      for (long v : sol) {
        b.addValues(v);
      }
    }
    if (status == CpSolverStatus.INFEASIBLE && model.getAssumptionsCount() > 0) {
      for (int lit : sufficientAssumptions(model)) {
        response.addSufficientAssumptionsForInfeasibility(lit);
      }
    }
    response.setNumBranches(e.numBranches());
    response.setNumConflicts(e.numConflicts());
    response.setNumBinaryPropagations(e.numPropagations());
    response.setNumIntegerPropagations(e.numPropagations());
    response.setNumBooleans(numBooleans(model));
    response.setNumIntegers(model.getVariablesCount() - numBooleans(model));
    response.setSolutionInfo(e.isStopped() ? "stopped" : (e.isExhausted() ? "complete search" : ""));
    engine = null;
    return finish(response, model);
  }

  private CpSolverResponse finish(CpSolverResponse.Builder response, CpModelProto model) {
    double t = elapsed();
    response.setWallTime(t);
    response.setUserTime(t);
    response.setDeterministicTime(0);
    log("");
    for (String line : CpSatHelper.solverResponseStats(response.build()).split("\n")) {
      log(line);
    }
    if (solveLog != null) {
      response.setSolveLog(solveLog.toString());
    }
    return response.build();
  }

  private void notifyBound(double bound) {
    for (Consumer<Double> cb : bestBoundCallbacks) {
      cb.accept(bound);
    }
  }

  private static String format(double v) {
    return ProtoPrinter.formatDouble(v);
  }

  private static long numBooleans(CpModelProto model) {
    long n = 0;
    for (IntegerVariableProto v : model.getVariablesList()) {
      if (v.getDomainCount() > 0 && v.getDomain(0) >= 0
          && v.getDomain(v.getDomainCount() - 1) <= 1) {
        n++;
      }
    }
    return n;
  }

  // a subset of the assumptions which is still infeasible, by deletion
  private List<Integer> sufficientAssumptions(CpModelProto model) {
    List<Integer> core = new ArrayList<>(model.getAssumptionsList());
    if (isInfeasible(model, new ArrayList<>())) {
      return new ArrayList<>();
    }
    for (int i = core.size() - 1; i >= 0; i--) {
      if (stopRequested) {
        break;
      }
      List<Integer> candidate = new ArrayList<>(core);
      candidate.remove(i);
      if (isInfeasible(model, candidate)) {
        core = candidate;
      }
    }
    return core;
  }

  private boolean isInfeasible(CpModelProto model, List<Integer> assumptions) {
    CpModelProto.Builder b = model.toBuilder();
    b.clearAssumptions();
    b.addAllAssumptions(assumptions);
    b.clearObjective();
    b.clearFloatingPointObjective();
    SatParameters.Builder p = parameters.toBuilder();
    if (!Double.isInfinite(parameters.getMaxTimeInSeconds())) {
      p.setMaxTimeInSeconds(Math.max(0, parameters.getMaxTimeInSeconds() - elapsed()));
    }
    CpSatEngine e = new CpSatEngine(b.build(), p.build());
    engine = e;
    if (stopRequested) {
      e.stop();
    }
    boolean found = e.nextSolution();
    engine = null;
    return !found && e.isExhausted();
  }
}
