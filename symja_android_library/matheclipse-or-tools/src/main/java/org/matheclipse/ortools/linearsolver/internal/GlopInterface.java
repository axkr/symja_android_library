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
// ortools/linear_solver/glop_interface.cc and glop_utils.cc C++ to Java, see NOTICE in the module
// root.

package org.matheclipse.ortools.linearsolver.internal;

import java.util.concurrent.atomic.AtomicBoolean;
import org.matheclipse.ortools.glop.GlopParameters;
import org.matheclipse.ortools.glop.LPSolver;
import org.matheclipse.ortools.glop.ProblemStatus;
import org.matheclipse.ortools.lpdata.ConstraintStatus;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.VariableStatus;
import org.matheclipse.ortools.linearsolver.MPSolver;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * The Glop backend of {@link MPSolver}. Integer variables are relaxed (Glop is an LP solver). The
 * {@link LPSolver} is kept between solves, so a solve after a change of the objective (primal
 * simplex) or of the bounds (dual simplex) is warm-started from the previous basis.
 */
public final class GlopInterface extends MPSolverInterface {

  private final LPSolver lpSolver = new LPSolver();
  private final AtomicBoolean interruptSolver = new AtomicBoolean(false);

  /** Converts a Glop status to an MPSolver status. */
  public static MPSolver.ResultStatus glopToMPSolverResultStatus(ProblemStatus s) {
    switch (s) {
      case OPTIMAL:
        return MPSolver.ResultStatus.OPTIMAL;
      case PRIMAL_FEASIBLE:
        return MPSolver.ResultStatus.FEASIBLE;
      case INFEASIBLE_OR_UNBOUNDED:
      case PRIMAL_INFEASIBLE:
      case DUAL_UNBOUNDED:
        return MPSolver.ResultStatus.INFEASIBLE;
      case DUAL_INFEASIBLE:
      case PRIMAL_UNBOUNDED:
        return MPSolver.ResultStatus.UNBOUNDED;
      case DUAL_FEASIBLE:
      case INIT:
        return MPSolver.ResultStatus.NOT_SOLVED;
      case ABNORMAL:
      case IMPRECISE:
      case INVALID_PROBLEM:
      default:
        return MPSolver.ResultStatus.ABNORMAL;
    }
  }

  /** Converts a Glop variable status to an MPSolver basis status. */
  public static MPSolver.BasisStatus glopToMPSolverVariableStatus(VariableStatus s) {
    switch (s) {
      case AT_LOWER_BOUND:
        return MPSolver.BasisStatus.AT_LOWER_BOUND;
      case AT_UPPER_BOUND:
        return MPSolver.BasisStatus.AT_UPPER_BOUND;
      case FIXED_VALUE:
        return MPSolver.BasisStatus.FIXED_VALUE;
      case BASIC:
        return MPSolver.BasisStatus.BASIC;
      case FREE:
      default:
        return MPSolver.BasisStatus.FREE;
    }
  }

  /** Converts a Glop constraint status to an MPSolver basis status. */
  public static MPSolver.BasisStatus glopToMPSolverConstraintStatus(ConstraintStatus s) {
    switch (s) {
      case AT_LOWER_BOUND:
        return MPSolver.BasisStatus.AT_LOWER_BOUND;
      case AT_UPPER_BOUND:
        return MPSolver.BasisStatus.AT_UPPER_BOUND;
      case FIXED_VALUE:
        return MPSolver.BasisStatus.FIXED_VALUE;
      case BASIC:
        return MPSolver.BasisStatus.BASIC;
      case FREE:
      default:
        return MPSolver.BasisStatus.FREE;
    }
  }

  /** Builds the Glop linear program of a model (integrality is ignored). */
  public static LinearProgram toLinearProgram(MPModel model) {
    final LinearProgram lp = new LinearProgram();
    for (MPModel.Variable v : model.variables) {
      final int col = lp.createNewVariable();
      lp.setVariableBounds(col, v.lowerBound, v.upperBound);
      lp.setObjectiveCoefficient(col, v.objectiveCoefficient);
    }
    for (MPModel.Constraint c : model.constraints) {
      final int row = lp.createNewConstraint();
      lp.setConstraintBounds(row, c.lowerBound, c.upperBound);
      for (int k = 0; k < c.varIndex.size(); ++k) {
        lp.setCoefficient(row, c.varIndex.get(k), c.coefficient.get(k));
      }
    }
    lp.setObjectiveOffset(model.objectiveOffset);
    lp.setMaximizationProblem(model.maximize);
    lp.cleanUp();
    return lp;
  }

  /** Computes the Glop parameters of a solve (MPSolverInterface::SetParameters). */
  static GlopParameters toGlopParameters(SolveParameters param) {
    final GlopParameters p = new GlopParameters();
    p.setLogSearchProgress(param.enableOutput);
    switch (param.presolve) {
      case 0:
        p.setUsePreprocessing(false);
        break;
      case 1:
        p.setUsePreprocessing(true);
        break;
      default:
        break;
    }
    switch (param.lpAlgorithm) {
      case 10:
        p.setUseDualSimplex(true);
        break;
      case 11:
        p.setUseDualSimplex(false);
        break;
      default:
        break;
    }
    switch (param.scaling) {
      case 0:
        p.setUseScaling(false);
        break;
      case 1:
        p.setUseScaling(true);
        break;
      default:
        break;
    }
    if (param.timeLimitMillis > 0) {
      p.setMaxTimeInSeconds(param.timeLimitMillis / 1000.0);
    }
    if (param.solverSpecificParameters != null && !param.solverSpecificParameters.isEmpty()) {
      p.mergeFromString(param.solverSpecificParameters);
    }
    return p;
  }

  @Override
  public SolveResult solve(MPModel model, SolveParameters param) {
    final long start = System.nanoTime();
    interruptSolver.set(false);
    final LinearProgram lp = toLinearProgram(model);
    final GlopParameters parameters = toGlopParameters(param);
    lpSolver.setParameters(parameters);
    lpSolver.getSolverLogger().setCallback(param.logCallback);
    final GlopParameters glopParameters = lpSolver.getParameters();
    final TimeLimit timeLimit = new TimeLimit(glopParameters.getMaxTimeInSeconds(),
        glopParameters.getMaxDeterministicTime());
    timeLimit.registerExternalBooleanAsLimit(interruptSolver);
    final ProblemStatus status = lpSolver.solveWithTimeLimit(lp, timeLimit);
    final SolveResult result = new SolveResult();
    result.status = glopToMPSolverResultStatus(status);
    result.objectiveValue = lpSolver.getObjectiveValue();
    final int numVars = model.variables.size();
    result.variableValues = new double[numVars];
    result.reducedCosts = new double[numVars];
    result.variableStatuses = new MPSolver.BasisStatus[numVars];
    for (int j = 0; j < numVars; ++j) {
      result.variableValues[j] = lpSolver.variableValues()[j];
      result.reducedCosts[j] = lpSolver.reducedCosts()[j];
      result.variableStatuses[j] = glopToMPSolverVariableStatus(lpSolver.variableStatuses()[j]);
    }
    final int numRows = model.constraints.size();
    result.dualValues = new double[numRows];
    result.constraintStatuses = new MPSolver.BasisStatus[numRows];
    for (int i = 0; i < numRows; ++i) {
      result.dualValues[i] = lpSolver.dualValues()[i];
      result.constraintStatuses[i] =
          glopToMPSolverConstraintStatus(lpSolver.constraintStatuses()[i]);
    }
    result.iterations = lpSolver.getNumberOfSimplexIterations();
    result.nodes = -1;
    result.wallTimeSeconds = (System.nanoTime() - start) * 1e-9;
    return result;
  }

  @Override
  public boolean isMip() {
    return false;
  }

  @Override
  public String solverVersion() {
    return LPSolver.glopVersion();
  }

  @Override
  public void reset() {
    lpSolver.clear();
  }

  @Override
  public boolean interruptSolve() {
    interruptSolver.set(true);
    return true;
  }

  @Override
  public boolean checkSolverSpecificParameters(String parameters) {
    return new GlopParameters().mergeFromString(parameters == null ? "" : parameters);
  }
}
