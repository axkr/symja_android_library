package org.matheclipse.ortools.linearsolver.internal;

import java.util.concurrent.atomic.AtomicBoolean;
import org.matheclipse.ortools.glop.GlopParameters;
import org.matheclipse.ortools.glop.LPSolver;
import org.matheclipse.ortools.linearsolver.MPSolver;

/**
 * The MIP backend of {@link MPSolver}: the Java {@link BranchAndBound} over Glop. It is used for
 * all the MIP problem types (SCIP, CBC, GLPK, SAT, BOP, XPRESS, GUROBI, CPLEX, HIGHS).
 *
 * <p>
 * Solver specific parameters (text format) may contain <code>max_nodes: n</code>,
 * <code>relative_mip_gap: g</code>, <code>use_cuts: true|false</code> (root cover cuts),
 * <code>use_presolve: true|false</code> (row tightening and symmetry breaking) and any Glop
 * parameter, which is used for the LP relaxations.
 */
public final class BranchAndBoundInterface extends MPSolverInterface {

  private final MPSolver.OptimizationProblemType problemType;
  private final AtomicBoolean interrupt = new AtomicBoolean(false);

  /** Creates the interface for the given problem type. */
  public BranchAndBoundInterface(MPSolver.OptimizationProblemType problemType) {
    this.problemType = problemType;
  }

  /**
   * Splits the solver specific parameters into the branch-and-bound ones (put into
   * <code>options</code>) and the Glop ones. Returns false if a parameter is invalid.
   */
  static boolean parseParameters(String text, BranchAndBound.Options options) {
    if (text == null || text.trim().isEmpty()) {
      return true;
    }
    final String[] tokens = text.trim().replace(":", " : ").split("[\\s,;]+");
    final StringBuilder glop = new StringBuilder();
    int i = 0;
    while (i < tokens.length) {
      if (tokens[i].isEmpty()) {
        ++i;
        continue;
      }
      if (i + 2 >= tokens.length) {
        return false;
      }
      if (!tokens[i + 1].equals(":")) {
        return false;
      }
      final String name = tokens[i];
      final String value = tokens[i + 2];
      try {
        switch (name) {
          case "max_nodes":
            options.maxNodes = Long.parseLong(value);
            break;
          case "relative_mip_gap":
            options.relativeMipGap = Double.parseDouble(value);
            break;
          case "use_cuts":
          case "use_presolve":
            if (!value.equals("true") && !value.equals("false")) {
              return false;
            }
            if (name.equals("use_cuts")) {
              options.useCuts = value.equals("true");
            } else {
              options.usePresolve = value.equals("true");
            }
            break;
          default:
            glop.append(name).append(": ").append(value).append(' ');
        }
      } catch (NumberFormatException e) {
        return false;
      }
      i += 3;
    }
    if (glop.length() > 0) {
      return options.lpParameters.mergeFromString(glop.toString());
    }
    return true;
  }

  @Override
  public SolveResult solve(MPModel model, SolveParameters parameters) {
    interrupt.set(false);
    final BranchAndBound.Options options = new BranchAndBound.Options();
    options.relativeMipGap = parameters.relativeMipGap;
    if (parameters.timeLimitMillis > 0) {
      options.timeLimitSeconds = parameters.timeLimitMillis / 1000.0;
    }
    options.logSearchProgress = parameters.enableOutput;
    options.interrupt = interrupt;
    options.logCallback = parameters.logCallback;
    final GlopParameters lp = new GlopParameters();
    if (parameters.scaling == 0) {
      lp.setUseScaling(false);
    }
    options.lpParameters = lp;
    parseParameters(parameters.solverSpecificParameters, options);
    // SAT_INTEGER_PROGRAMMING uses this branch-and-bound too: upstream scales the floating point
    // model to integers for CP-SAT, which is not ported
    final BranchAndBound bb = new BranchAndBound(options);
    return bb.solve(model);
  }

  @Override
  public boolean isMip() {
    return true;
  }

  @Override
  public String solverVersion() {
    return "Branch-and-bound (" + problemType + ") on " + LPSolver.glopVersion();
  }

  @Override
  public boolean interruptSolve() {
    interrupt.set(true);
    return true;
  }

  @Override
  public boolean checkSolverSpecificParameters(String parameters) {
    return parseParameters(parameters, new BranchAndBound.Options());
  }

  @Override
  public boolean setNumThreads(int numThreads) {
    // the search is sequential, the setting is accepted and ignored
    return numThreads >= 1;
  }
}
