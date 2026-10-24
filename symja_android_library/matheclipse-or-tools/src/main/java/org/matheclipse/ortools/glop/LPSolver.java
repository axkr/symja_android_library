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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/glop/lp_solver.h/.cc
// C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Locale;
import java.util.Arrays;
import org.matheclipse.ortools.init.OrToolsVersion;
import org.matheclipse.ortools.lpdata.ConstraintStatus;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ProblemSolution;
import org.matheclipse.ortools.lpdata.VariableStatus;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * The Glop LP solver: validates the problem, presolves it with {@link MainLpPreprocessor}, solves
 * it with the {@link RevisedSimplex}, postsolves the solution and checks it. On a successful
 * OPTIMAL solve, the primal values are moved within their bounds and the dual values made
 * sign-feasible; the solution is declared IMPRECISE if the remaining errors are above
 * <code>solution_feasibility_tolerance</code>.
 */
public final class LPSolver {

  private final LinearProgram currentLinearProgram = new LinearProgram();
  private RevisedSimplex revisedSimplex;
  private final SolverLogger logger = new SolverLogger();
  private GlopParameters parameters = new GlopParameters();
  private int numSolves;
  private long numRevisedSimplexIterations;

  private double[] primalValues = new double[0];
  private double[] dualValues = new double[0];
  private double[] reducedCosts = new double[0];
  private double[] constraintActivities = new double[0];
  private VariableStatus[] variableStatuses = new VariableStatus[0];
  private ConstraintStatus[] constraintStatuses = new ConstraintStatus[0];
  private double[] primalRay = new double[0];
  private double[] constraintsDualRay = new double[0];
  private double[] variableBoundsDualRay = new double[0];
  private double problemObjectiveValue;
  private double maxAbsolutePrimalInfeasibility;
  private double maxAbsoluteDualInfeasibility;
  private boolean mayHaveMultipleSolutions;

  /** Creates a solver with the default parameters. */
  public LPSolver() {}

  /** The version string of Glop. */
  public static String glopVersion() {
    return "Glop solver v" + OrToolsVersion.getVersionString();
  }

  /** Sets the parameters (copied). */
  public void setParameters(GlopParameters parameters) {
    this.parameters = parameters.copy();
  }

  /** The parameters. */
  public GlopParameters getParameters() {
    return parameters;
  }

  /** The parameters for modification. */
  public GlopParameters getMutableParameters() {
    return parameters;
  }

  /** The logger. */
  public SolverLogger getSolverLogger() {
    return logger;
  }

  /** Solves with the time limit given by the parameters. */
  public ProblemStatus solve(LinearProgram lp) {
    return solveWithTimeLimit(lp,
        new TimeLimit(parameters.getMaxTimeInSeconds(), parameters.getMaxDeterministicTime()));
  }

  /** Solves the linear program, which must be cleaned up (see {@link LinearProgram#cleanUp()}). */
  public ProblemStatus solveWithTimeLimit(LinearProgram lp, TimeLimit timeLimit) {
    ++numSolves;
    numRevisedSimplexIterations = 0;
    logger.enableLogging(parameters.getLogSearchProgress());
    logger.setLogToStdOut(parameters.getLogToStdout());
    if (logger.loggingIsEnabled()) {
      logger.log("");
      logger.log("Initial problem: ", lp.getDimensionString());
    }
    if (!lp.isCleanedUp()) {
      resizeSolution(lp.numConstraints(), lp.numVariables());
      return ProblemStatus.INVALID_PROBLEM;
    }
    if (!lp.isValid(parameters.getMaxValidMagnitude())) {
      logger.log("The given linear program is invalid. It contains NaNs, "
          + "coefficients too large or invalid bounds specification.");
      resizeSolution(lp.numConstraints(), lp.numVariables());
      return ProblemStatus.INVALID_PROBLEM;
    }
    currentLinearProgram.populateFromLinearProgram(lp);
    currentLinearProgram.removeNearZeroEntries(parameters.getDropMagnitude());
    final MainLpPreprocessor preprocessor = new MainLpPreprocessor(parameters);
    preprocessor.setLogger(logger);
    preprocessor.setTimeLimit(timeLimit);
    final boolean postsolveIsNeeded = preprocessor.run(currentLinearProgram);
    if (logger.loggingIsEnabled()) {
      logger.log("");
      logger.log("Presolved problem: ", currentLinearProgram.getDimensionString());
    }
    final ProblemSolution solution = new ProblemSolution(currentLinearProgram.numConstraints(),
        currentLinearProgram.numVariables());
    solution.status = preprocessor.status();
    primalRay = new double[0];
    constraintsDualRay = new double[0];
    variableBoundsDualRay = new double[0];
    if (!timeLimit.limitReached()) {
      runRevisedSimplexIfNeeded(solution, timeLimit);
    }
    if (postsolveIsNeeded) {
      preprocessor.destructiveRecoverSolution(solution);
    }
    final ProblemStatus status = loadAndVerifySolution(lp, solution);
    if (logger.loggingIsEnabled()) {
      logger.log("status: ", status);
      logger.log("objective: ", getObjectiveValue());
      logger.log("iterations: ", getNumberOfSimplexIterations());
      logger.log("time: ", timeLimit.getElapsedTime());
      logger.log("deterministic_time: ", timeLimit.getElapsedDeterministicTime());
      logger.log("");
    }
    return status;
  }

  /** Forgets the warm start information. */
  public void clear() {
    resizeSolution(0, 0);
    revisedSimplex = null;
  }

  /** Sets the basis used to warm-start the next solve (best used without presolve). */
  public void setInitialBasis(VariableStatus[] variableStatusesIn,
      ConstraintStatus[] constraintStatusesIn) {
    final VariableStatus[] statuses =
        Arrays.copyOf(variableStatusesIn, variableStatusesIn.length + constraintStatusesIn.length);
    int k = variableStatusesIn.length;
    for (ConstraintStatus status : constraintStatusesIn) {
      switch (status) {
        case FREE:
          statuses[k++] = VariableStatus.FREE;
          break;
        case AT_LOWER_BOUND:
          statuses[k++] = VariableStatus.AT_UPPER_BOUND;
          break;
        case AT_UPPER_BOUND:
          statuses[k++] = VariableStatus.AT_LOWER_BOUND;
          break;
        case FIXED_VALUE:
          statuses[k++] = VariableStatus.FIXED_VALUE;
          break;
        case BASIC:
        default:
          statuses[k++] = VariableStatus.BASIC;
          break;
      }
    }
    if (revisedSimplex == null) {
      revisedSimplex = new RevisedSimplex();
      revisedSimplex.setLogger(logger);
    }
    revisedSimplex.loadStateForNextSolve(new BasisState(statuses));
  }

  private static double problemObjectiveValue(LinearProgram lp, double value) {
    return lp.objectiveScalingFactor() * (value + lp.objectiveOffset());
  }

  private static double allowedError(double tolerance, double value) {
    return tolerance * Math.max(1.0, Math.abs(value));
  }

  private ProblemStatus loadAndVerifySolution(LinearProgram lp, ProblemSolution solution) {
    logger.log("");
    logger.log("Final unscaled solution:");
    if (!isProblemSolutionConsistent(lp, solution)) {
      logger.log("Inconsistency detected in the solution.");
      resizeSolution(lp.numConstraints(), lp.numVariables());
      return ProblemStatus.ABNORMAL;
    }
    primalValues = solution.primalValues.clone();
    dualValues = solution.dualValues.clone();
    variableStatuses = solution.variableStatuses.clone();
    constraintStatuses = solution.constraintStatuses.clone();
    ProblemStatus status = solution.status;

    computeReducedCosts(lp);
    final double primalObjectiveValue = computeObjective(lp);
    final double dualObjectiveValue = computeDualObjective(lp);
    logger.log("Primal objective (before moving primal/dual values) = ",
        String.format(Locale.ROOT, "%.15E", problemObjectiveValue(lp, primalObjectiveValue)));
    logger.log("Dual objective (before moving primal/dual values) = ",
        String.format(Locale.ROOT, "%.15E", problemObjectiveValue(lp, dualObjectiveValue)));
    if (status == ProblemStatus.OPTIMAL && parameters.getProvideStrongOptimalGuarantee()) {
      movePrimalValuesWithinBounds(lp);
      moveDualValuesWithinBounds(lp);
    }
    problemObjectiveValue = problemObjectiveValue(lp, computeObjective(lp));
    logger.log("Primal objective (after moving primal/dual values) = ",
        String.format(Locale.ROOT, "%.15E", problemObjectiveValue));
    computeReducedCosts(lp);
    computeConstraintActivities(lp);

    final boolean[] rhsPerturbationIsTooLarge = {false};
    final boolean[] costPerturbationIsTooLarge = {false};
    final boolean[] primalInfeasibilityIsTooLarge = {false};
    final boolean[] dualInfeasibilityIsTooLarge = {false};
    final boolean[] primalResidualIsTooLarge = {false};
    final boolean[] dualResidualIsTooLarge = {false};
    computeMaxRhsPerturbationToEnforceOptimality(lp, rhsPerturbationIsTooLarge);
    computeMaxCostPerturbationToEnforceOptimality(lp, costPerturbationIsTooLarge);
    final double primalInfeasibility =
        computePrimalValueInfeasibility(lp, primalInfeasibilityIsTooLarge);
    final double dualInfeasibility = computeDualValueInfeasibility(lp, dualInfeasibilityIsTooLarge);
    final double primalResidual = computeActivityInfeasibility(lp, primalResidualIsTooLarge);
    final double dualResidual = computeReducedCostInfeasibility(lp, dualResidualIsTooLarge);
    maxAbsolutePrimalInfeasibility = Math.max(primalInfeasibility, primalResidual);
    maxAbsoluteDualInfeasibility = Math.max(dualInfeasibility, dualResidual);
    logger.log("Max. primal infeasibility = ", maxAbsolutePrimalInfeasibility);
    logger.log("Max. dual infeasibility = ", maxAbsoluteDualInfeasibility);
    final double objectiveErrorUb = computeMaxExpectedObjectiveError(lp);
    logger.log("Objective error <= ", objectiveErrorUb);

    if (status == ProblemStatus.OPTIMAL && parameters.getProvideStrongOptimalGuarantee()) {
      if (rhsPerturbationIsTooLarge[0]) {
        logger.log("The needed rhs perturbation is too large !!");
        if (parameters.getChangeStatusToImprecise()) {
          status = ProblemStatus.IMPRECISE;
        }
      }
      if (costPerturbationIsTooLarge[0]) {
        logger.log("The needed cost perturbation is too large !!");
        if (parameters.getChangeStatusToImprecise()) {
          status = ProblemStatus.IMPRECISE;
        }
      }
    }
    if (status == ProblemStatus.OPTIMAL) {
      if (Math.abs(primalObjectiveValue - dualObjectiveValue) > objectiveErrorUb) {
        logger.log("The objective gap of the final solution is too large.");
        if (parameters.getChangeStatusToImprecise()) {
          status = ProblemStatus.IMPRECISE;
        }
      }
    }
    if ((status == ProblemStatus.OPTIMAL || status == ProblemStatus.PRIMAL_FEASIBLE)
        && (primalResidualIsTooLarge[0] || primalInfeasibilityIsTooLarge[0])) {
      logger.log("The primal infeasibility of the final solution is too large.");
      if (parameters.getChangeStatusToImprecise()) {
        status = ProblemStatus.IMPRECISE;
      }
    }
    if ((status == ProblemStatus.OPTIMAL || status == ProblemStatus.DUAL_FEASIBLE)
        && (dualResidualIsTooLarge[0] || dualInfeasibilityIsTooLarge[0])) {
      logger.log("The dual infeasibility of the final solution is too large.");
      if (parameters.getChangeStatusToImprecise()) {
        status = ProblemStatus.IMPRECISE;
      }
    }
    mayHaveMultipleSolutions = (status == ProblemStatus.OPTIMAL) && isOptimalSolutionOnFacet(lp);
    return status;
  }

  private boolean isOptimalSolutionOnFacet(LinearProgram lp) {
    final double kReducedCostTolerance = 1e-9;
    final double kBoundTolerance = 1e-7;
    final int numCols = lp.numVariables();
    for (int col = 0; col < numCols; ++col) {
      if (variableStatuses[col] == VariableStatus.FIXED_VALUE) {
        continue;
      }
      final double lowerBound = lp.variableLowerBounds()[col];
      final double upperBound = lp.variableUpperBounds()[col];
      final double value = primalValues[col];
      if (LpUtils.areWithinAbsoluteTolerance(reducedCosts[col], 0.0, kReducedCostTolerance)
          && (LpUtils.areWithinAbsoluteTolerance(value, lowerBound, kBoundTolerance)
              || LpUtils.areWithinAbsoluteTolerance(value, upperBound, kBoundTolerance))) {
        return true;
      }
    }
    final int numRows = lp.numConstraints();
    for (int row = 0; row < numRows; ++row) {
      if (constraintStatuses[row] == ConstraintStatus.FIXED_VALUE) {
        continue;
      }
      final double lowerBound = lp.constraintLowerBounds()[row];
      final double upperBound = lp.constraintUpperBounds()[row];
      final double activity = constraintActivities[row];
      if (LpUtils.areWithinAbsoluteTolerance(dualValues[row], 0.0, kReducedCostTolerance)
          && (LpUtils.areWithinAbsoluteTolerance(activity, lowerBound, kBoundTolerance)
              || LpUtils.areWithinAbsoluteTolerance(activity, upperBound, kBoundTolerance))) {
        return true;
      }
    }
    return false;
  }

  /** The objective value of the last solve. */
  public double getObjectiveValue() {
    return problemObjectiveValue;
  }

  /** The maximum primal infeasibility of the returned solution. */
  public double getMaximumPrimalInfeasibility() {
    return maxAbsolutePrimalInfeasibility;
  }

  /** The maximum dual infeasibility of the returned solution. */
  public double getMaximumDualInfeasibility() {
    return maxAbsoluteDualInfeasibility;
  }

  /** Returns true if the optimal solution may not be unique. */
  public boolean mayHaveMultipleOptimalSolutions() {
    return mayHaveMultipleSolutions;
  }

  /** The number of simplex iterations of the last solve. */
  public long getNumberOfSimplexIterations() {
    return numRevisedSimplexIterations;
  }

  /** The deterministic time of the simplex. */
  public double deterministicTime() {
    return revisedSimplex == null ? 0.0 : revisedSimplex.deterministicTime();
  }

  /** The variable values. */
  public double[] variableValues() {
    return primalValues;
  }

  /** The dual values. */
  public double[] dualValues() {
    return dualValues;
  }

  /** The reduced costs. */
  public double[] reducedCosts() {
    return reducedCosts;
  }

  /** The constraint activities. */
  public double[] constraintActivities() {
    return constraintActivities;
  }

  /** The variable statuses. */
  public VariableStatus[] variableStatuses() {
    return variableStatuses;
  }

  /** The constraint statuses. */
  public ConstraintStatus[] constraintStatuses() {
    return constraintStatuses;
  }

  /** The primal ray (only without presolve and scaling). */
  public double[] getPrimalRay() {
    return primalRay;
  }

  /** The constraints dual ray (only without presolve and scaling). */
  public double[] getConstraintsDualRay() {
    return constraintsDualRay;
  }

  /** The variable bounds dual ray (only without presolve and scaling). */
  public double[] getVariableBoundsDualRay() {
    return variableBoundsDualRay;
  }

  private void movePrimalValuesWithinBounds(LinearProgram lp) {
    final int numCols = lp.numVariables();
    double error = 0.0;
    for (int col = 0; col < numCols; ++col) {
      final double lowerBound = lp.variableLowerBounds()[col];
      final double upperBound = lp.variableUpperBounds()[col];
      error = Math.max(error, primalValues[col] - upperBound);
      error = Math.max(error, lowerBound - primalValues[col]);
      primalValues[col] = Math.min(primalValues[col], upperBound);
      primalValues[col] = Math.max(primalValues[col], lowerBound);
    }
    logger.log("Max. primal values move = ", error);
  }

  private void moveDualValuesWithinBounds(LinearProgram lp) {
    final int numRows = lp.numConstraints();
    final double optimizationSign = lp.isMaximizationProblem() ? -1.0 : 1.0;
    double error = 0.0;
    for (int row = 0; row < numRows; ++row) {
      final double lowerBound = lp.constraintLowerBounds()[row];
      final double upperBound = lp.constraintUpperBounds()[row];
      double minimizationDualValue = optimizationSign * dualValues[row];
      if (lowerBound == -LpTypes.INFINITY && minimizationDualValue > 0.0) {
        error = Math.max(error, minimizationDualValue);
        minimizationDualValue = 0.0;
      }
      if (upperBound == LpTypes.INFINITY && minimizationDualValue < 0.0) {
        error = Math.max(error, -minimizationDualValue);
        minimizationDualValue = 0.0;
      }
      dualValues[row] = optimizationSign * minimizationDualValue;
    }
    logger.log("Max. dual values move = ", error);
  }

  private void resizeSolution(int numRows, int numCols) {
    primalValues = Arrays.copyOf(primalValues, numCols);
    reducedCosts = Arrays.copyOf(reducedCosts, numCols);
    final int oldCols = variableStatuses.length;
    variableStatuses = Arrays.copyOf(variableStatuses, numCols);
    for (int i = oldCols; i < numCols; ++i) {
      variableStatuses[i] = VariableStatus.FREE;
    }
    dualValues = Arrays.copyOf(dualValues, numRows);
    constraintActivities = Arrays.copyOf(constraintActivities, numRows);
    final int oldRows = constraintStatuses.length;
    constraintStatuses = Arrays.copyOf(constraintStatuses, numRows);
    for (int i = oldRows; i < numRows; ++i) {
      constraintStatuses[i] = ConstraintStatus.FREE;
    }
  }

  private void runRevisedSimplexIfNeeded(ProblemSolution solution, TimeLimit timeLimit) {
    currentLinearProgram.clearTransposeMatrix();
    if (solution.status != ProblemStatus.INIT) {
      return;
    }
    if (revisedSimplex == null) {
      revisedSimplex = new RevisedSimplex();
      revisedSimplex.setLogger(logger);
    }
    revisedSimplex.setParameters(parameters);
    if (revisedSimplex.solve(currentLinearProgram, timeLimit).ok()) {
      numRevisedSimplexIterations = revisedSimplex.getNumberOfIterations();
      solution.status = revisedSimplex.getProblemStatus();
      final int numCols = solution.primalValues.length;
      for (int col = 0; col < numCols; ++col) {
        solution.primalValues[col] = revisedSimplex.getVariableValue(col);
        solution.variableStatuses[col] = revisedSimplex.getVariableStatus(col);
      }
      final int numRows = revisedSimplex.getProblemNumRows();
      for (int row = 0; row < numRows; ++row) {
        solution.dualValues[row] = revisedSimplex.getDualValue(row);
        solution.constraintStatuses[row] = revisedSimplex.getConstraintStatus(row);
      }
      if (!parameters.getUsePreprocessing() && !parameters.getUseScaling()) {
        if (solution.status == ProblemStatus.PRIMAL_UNBOUNDED) {
          primalRay = Arrays.copyOf(revisedSimplex.getPrimalRay(), numCols);
        } else if (solution.status == ProblemStatus.DUAL_UNBOUNDED) {
          constraintsDualRay = revisedSimplex.getDualRay().clone();
          variableBoundsDualRay =
              Arrays.copyOf(revisedSimplex.getDualRayRowCombination(), numCols);
          if (currentLinearProgram.isMaximizationProblem()) {
            LpUtils.changeSign(variableBoundsDualRay);
          } else {
            LpUtils.changeSign(constraintsDualRay);
          }
        }
      }
    } else {
      logger.log("Error during the revised simplex algorithm.");
      solution.status = ProblemStatus.ABNORMAL;
    }
  }

  private boolean isProblemSolutionConsistent(LinearProgram lp, ProblemSolution solution) {
    final int numRows = lp.numConstraints();
    final int numCols = lp.numVariables();
    if (solution.variableStatuses.length != numCols) {
      return false;
    }
    if (solution.constraintStatuses.length != numRows) {
      return false;
    }
    if (solution.primalValues.length != numCols) {
      return false;
    }
    if (solution.dualValues.length != numRows) {
      return false;
    }
    if (solution.status != ProblemStatus.OPTIMAL
        && solution.status != ProblemStatus.PRIMAL_FEASIBLE
        && solution.status != ProblemStatus.DUAL_FEASIBLE) {
      return true;
    }
    int numBasicVariables = 0;
    final double[] vlb = lp.variableLowerBounds();
    final double[] vub = lp.variableUpperBounds();
    for (int col = 0; col < numCols; ++col) {
      final double value = solution.primalValues[col];
      final double lb = vlb[col];
      final double ub = vub[col];
      switch (solution.variableStatuses[col]) {
        case BASIC:
          ++numBasicVariables;
          break;
        case FIXED_VALUE:
          if (value != ub && value != lb) {
            return false;
          }
          break;
        case AT_LOWER_BOUND:
          if (value != lb || lb == ub) {
            return false;
          }
          break;
        case AT_UPPER_BOUND:
          if (!LpUtils.areWithinAbsoluteTolerance(value, ub, 1e-7) || lb == ub) {
            return false;
          }
          break;
        case FREE:
        default:
          if (lb != -LpTypes.INFINITY || ub != LpTypes.INFINITY || value != 0.0) {
            return false;
          }
          break;
      }
    }
    final double[] clb = lp.constraintLowerBounds();
    final double[] cub = lp.constraintUpperBounds();
    for (int row = 0; row < numRows; ++row) {
      final double dualValue = solution.dualValues[row];
      final double lb = clb[row];
      final double ub = cub[row];
      switch (solution.constraintStatuses[row]) {
        case BASIC:
          if (dualValue != 0.0) {
            return false;
          }
          ++numBasicVariables;
          break;
        case FIXED_VALUE:
          if (ub - lb > 1e-12) {
            return false;
          }
          break;
        case AT_LOWER_BOUND:
          if (lb == -LpTypes.INFINITY) {
            return false;
          }
          break;
        case AT_UPPER_BOUND:
          if (ub == LpTypes.INFINITY) {
            return false;
          }
          break;
        case FREE:
        default:
          if (dualValue != 0.0) {
            return false;
          }
          if (lb != -LpTypes.INFINITY || ub != LpTypes.INFINITY) {
            return false;
          }
          break;
      }
    }
    return numBasicVariables == numRows;
  }

  private double computeMaxCostPerturbationToEnforceOptimality(LinearProgram lp,
      boolean[] isTooLarge) {
    double maxCostCorrection = 0.0;
    final int numCols = lp.numVariables();
    final double optimizationSign = lp.isMaximizationProblem() ? -1.0 : 1.0;
    final double tolerance = parameters.getSolutionFeasibilityTolerance();
    for (int col = 0; col < numCols; ++col) {
      final double reducedCost = optimizationSign * reducedCosts[col];
      final VariableStatus status = variableStatuses[col];
      if (status == VariableStatus.BASIC || status == VariableStatus.FREE
          || (status == VariableStatus.AT_UPPER_BOUND && reducedCost > 0.0)
          || (status == VariableStatus.AT_LOWER_BOUND && reducedCost < 0.0)) {
        maxCostCorrection = Math.max(maxCostCorrection, Math.abs(reducedCost));
        isTooLarge[0] |=
            Math.abs(reducedCost) > allowedError(tolerance, lp.objectiveCoefficients()[col]);
      }
    }
    logger.log("Max. cost perturbation = ", maxCostCorrection);
    return maxCostCorrection;
  }

  private double computeMaxRhsPerturbationToEnforceOptimality(LinearProgram lp,
      boolean[] isTooLarge) {
    double maxRhsCorrection = 0.0;
    final int numRows = lp.numConstraints();
    final double tolerance = parameters.getSolutionFeasibilityTolerance();
    for (int row = 0; row < numRows; ++row) {
      final double lowerBound = lp.constraintLowerBounds()[row];
      final double upperBound = lp.constraintUpperBounds()[row];
      final double activity = constraintActivities[row];
      final ConstraintStatus status = constraintStatuses[row];
      double rhsError = 0.0;
      double allowed = 0.0;
      if (status == ConstraintStatus.AT_LOWER_BOUND || activity < lowerBound) {
        rhsError = Math.abs(activity - lowerBound);
        allowed = allowedError(tolerance, lowerBound);
      } else if (status == ConstraintStatus.AT_UPPER_BOUND || activity > upperBound) {
        rhsError = Math.abs(activity - upperBound);
        allowed = allowedError(tolerance, upperBound);
      }
      maxRhsCorrection = Math.max(maxRhsCorrection, rhsError);
      isTooLarge[0] |= rhsError > allowed;
    }
    logger.log("Max. rhs perturbation = ", maxRhsCorrection);
    return maxRhsCorrection;
  }

  private void computeConstraintActivities(LinearProgram lp) {
    final int numRows = lp.numConstraints();
    final int numCols = lp.numVariables();
    constraintActivities = new double[numRows];
    for (int col = 0; col < numCols; ++col) {
      lp.getSparseColumn(col).addMultipleToDenseVector(primalValues[col], constraintActivities);
    }
  }

  private void computeReducedCosts(LinearProgram lp) {
    final int numCols = lp.numVariables();
    reducedCosts = new double[numCols];
    final double[] obj = lp.objectiveCoefficients();
    for (int col = 0; col < numCols; ++col) {
      reducedCosts[col] = obj[col] - lp.getSparseColumn(col).scalarProduct(dualValues);
    }
  }

  private double computeObjective(LinearProgram lp) {
    final int numCols = lp.numVariables();
    final LpUtils.KahanSum sum = new LpUtils.KahanSum();
    final double[] obj = lp.objectiveCoefficients();
    for (int col = 0; col < numCols; ++col) {
      sum.add(obj[col] * primalValues[col]);
    }
    return sum.value();
  }

  private double computeDualObjective(LinearProgram lp) {
    final LpUtils.KahanSum dualObjective = new LpUtils.KahanSum();
    final int numRows = lp.numConstraints();
    final double optimizationSign = lp.isMaximizationProblem() ? -1.0 : 1.0;
    for (int row = 0; row < numRows; ++row) {
      final double lowerBound = lp.constraintLowerBounds()[row];
      final double upperBound = lp.constraintUpperBounds()[row];
      final double correctedValue = optimizationSign * dualValues[row];
      if (correctedValue > 0.0 && lowerBound != -LpTypes.INFINITY) {
        dualObjective.add(dualValues[row] * lowerBound);
      }
      if (correctedValue < 0.0 && upperBound != LpTypes.INFINITY) {
        dualObjective.add(dualValues[row] * upperBound);
      }
    }
    final int numCols = lp.numVariables();
    for (int col = 0; col < numCols; ++col) {
      final double lowerBound = lp.variableLowerBounds()[col];
      final double upperBound = lp.variableUpperBounds()[col];
      final double reducedCost = optimizationSign * reducedCosts[col];
      double correction = 0.0;
      if (variableStatuses[col] == VariableStatus.AT_LOWER_BOUND && reducedCost > 0.0) {
        correction = reducedCost * lowerBound;
      } else if (variableStatuses[col] == VariableStatus.AT_UPPER_BOUND && reducedCost < 0.0) {
        correction = reducedCost * upperBound;
      } else if (variableStatuses[col] == VariableStatus.FIXED_VALUE) {
        correction = reducedCost * upperBound;
      }
      dualObjective.add(optimizationSign * correction);
    }
    return dualObjective.value();
  }

  private double computeMaxExpectedObjectiveError(LinearProgram lp) {
    final int numCols = lp.numVariables();
    final double tolerance = parameters.getSolutionFeasibilityTolerance();
    double primalObjectiveError = 0.0;
    final double[] obj = lp.objectiveCoefficients();
    for (int col = 0; col < numCols; ++col) {
      primalObjectiveError += Math.abs(obj[col]) * allowedError(tolerance, primalValues[col]);
    }
    return primalObjectiveError;
  }

  private double computePrimalValueInfeasibility(LinearProgram lp, boolean[] isTooLarge) {
    double infeasibility = 0.0;
    final double tolerance = parameters.getSolutionFeasibilityTolerance();
    final int numCols = lp.numVariables();
    for (int col = 0; col < numCols; ++col) {
      final double lowerBound = lp.variableLowerBounds()[col];
      final double upperBound = lp.variableUpperBounds()[col];
      if (lowerBound == upperBound) {
        final double error = Math.abs(primalValues[col] - upperBound);
        infeasibility = Math.max(infeasibility, error);
        isTooLarge[0] |= error > allowedError(tolerance, upperBound);
        continue;
      }
      if (primalValues[col] > upperBound) {
        final double error = primalValues[col] - upperBound;
        infeasibility = Math.max(infeasibility, error);
        isTooLarge[0] |= error > allowedError(tolerance, upperBound);
      }
      if (primalValues[col] < lowerBound) {
        final double error = lowerBound - primalValues[col];
        infeasibility = Math.max(infeasibility, error);
        isTooLarge[0] |= error > allowedError(tolerance, lowerBound);
      }
    }
    return infeasibility;
  }

  private double computeActivityInfeasibility(LinearProgram lp, boolean[] isTooLarge) {
    double infeasibility = 0.0;
    int numProblematicRows = 0;
    final int numRows = lp.numConstraints();
    final double tolerance = parameters.getSolutionFeasibilityTolerance();
    for (int row = 0; row < numRows; ++row) {
      final double activity = constraintActivities[row];
      final double lowerBound = lp.constraintLowerBounds()[row];
      final double upperBound = lp.constraintUpperBounds()[row];
      if (lowerBound == upperBound) {
        if (Math.abs(activity - upperBound) > allowedError(tolerance, upperBound)) {
          ++numProblematicRows;
        }
        infeasibility = Math.max(infeasibility, Math.abs(activity - upperBound));
        continue;
      }
      if (activity > upperBound) {
        final double rowExcess = activity - upperBound;
        if (rowExcess > allowedError(tolerance, upperBound)) {
          ++numProblematicRows;
        }
        infeasibility = Math.max(infeasibility, rowExcess);
      }
      if (activity < lowerBound) {
        final double rowDeficit = lowerBound - activity;
        if (rowDeficit > allowedError(tolerance, lowerBound)) {
          ++numProblematicRows;
        }
        infeasibility = Math.max(infeasibility, rowDeficit);
      }
    }
    if (numProblematicRows > 0) {
      isTooLarge[0] = true;
    }
    return infeasibility;
  }

  private double computeDualValueInfeasibility(LinearProgram lp, boolean[] isTooLarge) {
    final double allowed = parameters.getSolutionFeasibilityTolerance();
    final double optimizationSign = lp.isMaximizationProblem() ? -1.0 : 1.0;
    double infeasibility = 0.0;
    final int numRows = lp.numConstraints();
    for (int row = 0; row < numRows; ++row) {
      final double dualValue = dualValues[row];
      final double lowerBound = lp.constraintLowerBounds()[row];
      final double upperBound = lp.constraintUpperBounds()[row];
      final double minimizationDualValue = optimizationSign * dualValue;
      if (lowerBound == -LpTypes.INFINITY) {
        isTooLarge[0] |= minimizationDualValue > allowed;
        infeasibility = Math.max(infeasibility, minimizationDualValue);
      }
      if (upperBound == LpTypes.INFINITY) {
        isTooLarge[0] |= -minimizationDualValue > allowed;
        infeasibility = Math.max(infeasibility, -minimizationDualValue);
      }
    }
    return infeasibility;
  }

  private double computeReducedCostInfeasibility(LinearProgram lp, boolean[] isTooLarge) {
    final double optimizationSign = lp.isMaximizationProblem() ? -1.0 : 1.0;
    double infeasibility = 0.0;
    final int numCols = lp.numVariables();
    final double tolerance = parameters.getSolutionFeasibilityTolerance();
    for (int col = 0; col < numCols; ++col) {
      final double reducedCost = reducedCosts[col];
      final double lowerBound = lp.variableLowerBounds()[col];
      final double upperBound = lp.variableUpperBounds()[col];
      final double minimizationReducedCost = optimizationSign * reducedCost;
      final double allowed = allowedError(tolerance, lp.objectiveCoefficients()[col]);
      if (lowerBound == -LpTypes.INFINITY) {
        isTooLarge[0] |= minimizationReducedCost > allowed;
        infeasibility = Math.max(infeasibility, minimizationReducedCost);
      }
      if (upperBound == LpTypes.INFINITY) {
        isTooLarge[0] |= -minimizationReducedCost > allowed;
        infeasibility = Math.max(infeasibility, -minimizationReducedCost);
      }
    }
    return infeasibility;
  }
}
