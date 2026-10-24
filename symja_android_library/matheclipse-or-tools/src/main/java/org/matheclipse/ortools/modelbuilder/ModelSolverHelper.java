// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from
// ortools/linear_solver/wrappers/model_builder_helper.h/.cc,
// ortools/linear_solver/proto_solver/glop_proto_solver.cc and model_validator.cc C++ to Java
// (replaces the SWIG generated class), see NOTICE in the module root.

package org.matheclipse.ortools.modelbuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.matheclipse.ortools.linearsolver.MPSolver;
import org.matheclipse.ortools.linearsolver.internal.BranchAndBoundInterface;
import org.matheclipse.ortools.linearsolver.internal.GlopInterface;
import org.matheclipse.ortools.linearsolver.internal.MPModel;
import org.matheclipse.ortools.linearsolver.internal.MPSolverInterface;

/**
 * Solves a {@link ModelBuilderHelper} model with the solver given by its name: the LP types
 * ("glop", "clp", "pdlp", "highs_lp", ...) with Glop, the MIP types ("scip", "cbc", "sat",
 * "highs", ...) with the Java branch-and-bound. Unlike the C++ helper, all the known solver names
 * are supported.
 */
public class ModelSolverHelper {

  private final MPSolver.OptimizationProblemType solverType;
  private MPSolverInterface currentInterface;
  private Consumer<String> logCallback;
  private boolean solverOutput;
  private double timeLimitInSeconds = -1.0;
  private String solverSpecificParameters = "";
  private boolean hasResponse;
  private SolveStatus status = SolveStatus.UNKNOWN_STATUS;
  private String statusString = "";
  private double objectiveValue;
  private double bestObjectiveBound;
  private double[] variableValues = new double[0];
  private double[] reducedCosts = new double[0];
  private double[] dualValues = new double[0];
  private double[] activities = new double[0];
  private MPModel modelOfLastSolve;
  private double wallTime;
  private double userTime;

  /** Creates the helper for the solver with the given name (case insensitive). */
  public ModelSolverHelper(String solver_name) {
    this.solverType = (solver_name == null || solver_name.isEmpty()) ? null
        : MPSolver.parseSolverType(solver_name);
  }

  /** Does nothing: the memory is managed by the garbage collector. */
  public void delete() {
    // nothing to release
  }

  /** Returns true if the solver name was recognized. */
  public boolean solverIsSupported() {
    return solverType != null;
  }

  private static boolean isNanOrInfinite(double d) {
    return Double.isNaN(d) || Double.isInfinite(d);
  }

  private static String findErrorInBounds(double lb, double ub) {
    if (Double.isNaN(lb) || Double.isNaN(ub) || lb == Double.POSITIVE_INFINITY
        || ub == Double.NEGATIVE_INFINITY || lb > ub) {
      return "Infeasible bounds: [" + lb + ", " + ub + "]";
    }
    return "";
  }

  private static String findErrorInConstraint(MPModel.Constraint c, int numVariables) {
    final String bound = findErrorInBounds(c.lowerBound, c.upperBound);
    if (!bound.isEmpty()) {
      return bound;
    }
    if (c.varIndex.size() != c.coefficient.size()) {
      return "var_index_size() != coefficient_size()";
    }
    final Set<Integer> seen = new HashSet<>();
    for (int k = 0; k < c.varIndex.size(); ++k) {
      final int var = c.varIndex.get(k);
      if (var < 0 || var >= numVariables) {
        return "var_index(" + k + ")=" + var + " is out of bounds.";
      }
      if (!seen.add(var)) {
        return "var_index #" + k + " (" + var + ") is a duplicate.";
      }
      if (isNanOrInfinite(c.coefficient.get(k))) {
        return "coefficient(" + k + ")=" + c.coefficient.get(k) + " is invalid.";
      }
    }
    return "";
  }

  /** Returns an error message for an invalid model, "" for a valid one. */
  static String findErrorInModel(MPModel model) {
    if (isNanOrInfinite(model.objectiveOffset)) {
      return "Invalid objective_offset: " + model.objectiveOffset;
    }
    final int numVariables = model.variables.size();
    for (int j = 0; j < numVariables; ++j) {
      final MPModel.Variable v = model.variables.get(j);
      final String bound = findErrorInBounds(v.lowerBound, v.upperBound);
      if (!bound.isEmpty()) {
        return "In variable #" + j + ": " + bound;
      }
      if (v.isInteger && Math.ceil(v.lowerBound) > Math.floor(v.upperBound)) {
        return "In variable #" + j + ": Infeasible bounds for integer variable: ["
            + v.lowerBound + ", " + v.upperBound + "] translate to the empty set";
      }
      if (isNanOrInfinite(v.objectiveCoefficient)) {
        return "In variable #" + j + ": Invalid objective_coefficient: " + v.objectiveCoefficient;
      }
    }
    for (int i = 0; i < model.constraints.size(); ++i) {
      final String error = findErrorInConstraint(model.constraints.get(i), numVariables);
      if (!error.isEmpty()) {
        return "In constraint #" + i + ": " + error;
      }
    }
    for (int i = 0; i < model.generalConstraints.size(); ++i) {
      final MPModel.IndicatorConstraint ind = model.generalConstraints.get(i);
      if (ind.varIndex < 0 || ind.varIndex >= numVariables) {
        return "In general constraint #" + i + ": var_index is out of bounds.";
      }
      final MPModel.Variable v = model.variables.get(ind.varIndex);
      if (!v.isInteger || v.lowerBound < 0 || v.upperBound > 1) {
        return "In general constraint #" + i + ": the indicator variable must be boolean.";
      }
      if (ind.varValue != 0 && ind.varValue != 1) {
        return "In general constraint #" + i + ": var_value=" + ind.varValue
            + " must be 0 or 1.";
      }
      final String error = findErrorInConstraint(ind.constraint, numVariables);
      if (!error.isEmpty()) {
        return "In general constraint #" + i + ": " + error;
      }
    }
    for (int k = 0; k < model.hintVarIndex.size(); ++k) {
      final int var = model.hintVarIndex.get(k);
      if (var < 0 || var >= numVariables || isNanOrInfinite(model.hintVarValue.get(k))) {
        return "Invalid solution hint #" + k;
      }
    }
    return "";
  }

  private void clearResponse() {
    hasResponse = false;
    status = SolveStatus.UNKNOWN_STATUS;
    statusString = "";
    objectiveValue = 0.0;
    bestObjectiveBound = 0.0;
    variableValues = new double[0];
    reducedCosts = new double[0];
    dualValues = new double[0];
    activities = new double[0];
    modelOfLastSolve = null;
    wallTime = 0.0;
    userTime = 0.0;
  }

  private static SolveStatus toSolveStatus(MPSolver.ResultStatus s) {
    switch (s) {
      case OPTIMAL:
        return SolveStatus.OPTIMAL;
      case FEASIBLE:
        return SolveStatus.FEASIBLE;
      case INFEASIBLE:
        return SolveStatus.INFEASIBLE;
      case UNBOUNDED:
        return SolveStatus.UNBOUNDED;
      case ABNORMAL:
        return SolveStatus.ABNORMAL;
      case MODEL_INVALID:
        return SolveStatus.MODEL_INVALID;
      case NOT_SOLVED:
        return SolveStatus.NOT_SOLVED;
      default:
        return SolveStatus.UNKNOWN_STATUS;
    }
  }

  /**
   * Solves the model of the given helper. Throws {@link IllegalArgumentException} if the model
   * contains an enforced constraint whose big-M reformulation needs an infinite bound (MIP
   * solvers only).
   */
  public void solve(ModelBuilderHelper model) {
    clearResponse();
    hasResponse = true;
    if (solverType == null) {
      status = SolveStatus.SOLVER_TYPE_UNAVAILABLE;
      return;
    }
    final long start = System.nanoTime();
    final MPModel mp = model.model().copy();
    final String error = findErrorInModel(mp);
    if (!error.isEmpty()) {
      status = error.contains("Infeasible") ? SolveStatus.INFEASIBLE : SolveStatus.MODEL_INVALID;
      statusString = error;
      return;
    }
    if (mp.variables.isEmpty() && mp.constraints.isEmpty() && mp.generalConstraints.isEmpty()) {
      status = SolveStatus.OPTIMAL;
      objectiveValue = mp.objectiveOffset;
      bestObjectiveBound = objectiveValue;
      statusString = "Requests without variables and constraints are considered OPTIMAL";
      modelOfLastSolve = mp;
      return;
    }
    final MPSolverInterface.SolveParameters params = new MPSolverInterface.SolveParameters();
    params.enableOutput = solverOutput;
    params.logCallback = logCallback;
    params.solverSpecificParameters = solverSpecificParameters;
    if (timeLimitInSeconds >= 0.0) {
      params.timeLimitMillis = Math.max(1L, (long) Math.ceil(timeLimitInSeconds * 1000.0));
    }
    if (!solverType.isMip()) {
      if (!mp.generalConstraints.isEmpty()) {
        status = SolveStatus.MODEL_INVALID;
        statusString = "GLOP does not support general constraints";
        return;
      }
      final GlopInterface glop = new GlopInterface();
      if (!glop.checkSolverSpecificParameters(solverSpecificParameters)) {
        status = SolveStatus.INVALID_SOLVER_PARAMETERS;
        statusString = "solver_specific_parameters is not a valid textual representation of the "
            + "GlopParameters proto";
        return;
      }
      currentInterface = glop;
    } else {
      // ModelSolver("sat") (SAT_INTEGER_PROGRAMMING) is solved by the branch-and-bound too:
      // upstream scales the floating point model to integers for CP-SAT, which is not ported
      final BranchAndBoundInterface bb = new BranchAndBoundInterface(solverType);
      if (!bb.checkSolverSpecificParameters(solverSpecificParameters)) {
        status = SolveStatus.INVALID_SOLVER_PARAMETERS;
        statusString = "invalid solver specific parameters";
        return;
      }
      currentInterface = bb;
    }
    final MPSolverInterface.SolveResult result = currentInterface.solve(mp, params);
    status = toSolveStatus(result.status);
    if (result.status == MPSolver.ResultStatus.OPTIMAL
        || result.status == MPSolver.ResultStatus.FEASIBLE) {
      objectiveValue = result.objectiveValue;
      bestObjectiveBound = solverType.isMip() ? result.bestObjectiveBound : objectiveValue;
      variableValues = result.variableValues.clone();
      reducedCosts = result.reducedCosts.clone();
      modelOfLastSolve = mp;
      activities = new double[model.numConstraints()];
      Arrays.fill(activities, Double.NaN);
    }
    dualValues = result.dualValues.clone();
    wallTime = (System.nanoTime() - start) * 1e-9;
    userTime = wallTime;
  }

  /** Interrupts the current solve; returns true. */
  public boolean interruptSolve() {
    if (currentInterface != null) {
      currentInterface.interruptSolve();
    }
    return true;
  }

  /** Sets the callback receiving the log lines. */
  public void setLogCallback(Consumer<String> log_callback) {
    this.logCallback = log_callback;
  }

  /** Removes the log callback. */
  public void clearLogCallback() {
    this.logCallback = null;
  }

  /** Returns true if solve() was called. */
  public boolean hasResponse() {
    return hasResponse;
  }

  /** Returns true if the last solve found a solution. */
  public boolean hasSolution() {
    return hasResponse && (status == SolveStatus.OPTIMAL || status == SolveStatus.FEASIBLE);
  }

  /** The status of the last solve. */
  public SolveStatus getStatus() {
    return hasResponse ? status : SolveStatus.UNKNOWN_STATUS;
  }

  /** The objective value of the last solution. */
  public double getObjectiveValue() {
    return hasResponse ? objectiveValue : 0.0;
  }

  /** The best objective bound of the last solve. */
  public double getBestObjectiveBound() {
    return hasResponse ? bestObjectiveBound : 0.0;
  }

  /** The value of a variable in the last solution. */
  public double getVariableValue(int var_index) {
    if (!hasResponse || var_index >= variableValues.length) {
      return 0.0;
    }
    return variableValues[var_index];
  }

  /** The reduced cost of a variable (LP only). */
  public double getReducedCost(int var_index) {
    if (!hasResponse || var_index >= reducedCosts.length) {
      return 0.0;
    }
    return reducedCosts[var_index];
  }

  /** The dual value of a constraint (LP only). */
  public double getDualValue(int ct_index) {
    if (!hasResponse || ct_index >= dualValues.length) {
      return 0.0;
    }
    return dualValues[ct_index];
  }

  /** The activity of a linear constraint in the last solution. */
  public double getActivity(int ct_index) {
    if (!hasResponse || ct_index >= activities.length || modelOfLastSolve == null
        || ct_index >= modelOfLastSolve.constraints.size()) {
      return 0.0;
    }
    if (Double.isNaN(activities[ct_index])) {
      final MPModel.Constraint c = modelOfLastSolve.constraints.get(ct_index);
      double result = 0.0;
      for (int i = 0; i < c.varIndex.size(); ++i) {
        result += getVariableValue(c.varIndex.get(i)) * c.coefficient.get(i);
      }
      activities[ct_index] = result;
    }
    return activities[ct_index];
  }

  /** The status message of the last solve. */
  public String getStatusString() {
    return hasResponse ? statusString : "";
  }

  /** The wall time of the last solve in seconds. */
  public double getWallTime() {
    return wallTime;
  }

  /** The user time of the last solve in seconds (equal to the wall time here). */
  public double getUserTime() {
    return userTime;
  }

  /** Sets the time limit of the next solves. */
  public void setTimeLimitInSeconds(double limit) {
    this.timeLimitInSeconds = limit;
  }

  /** Sets the solver specific parameters (text format). */
  public void setSolverSpecificParameters(String solver_specific_parameters) {
    this.solverSpecificParameters =
        solver_specific_parameters == null ? "" : solver_specific_parameters;
  }

  /** Enables or disables the solver log. */
  public void enableOutput(boolean enabled) {
    this.solverOutput = enabled;
  }
}
