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
// ortools/glop/reduced_costs.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ScatteredVector;
import org.matheclipse.ortools.lpdata.VariableType;

/**
 * Maintains the reduced costs of the non-basic variables and the dual values (the left inverse of
 * the basic objective). They are updated incrementally at each pivot and recomputed from scratch
 * when needed for precision. It also handles the cost perturbations and shifts of the dual simplex.
 */
public final class ReducedCosts {

  private final CompactSparseMatrix matrix;
  private final RevisedSimplex.DoubleArrayRef objective;
  private final RevisedSimplex.IntArrayRef basis;
  private final VariablesInfo variablesInfo;
  private final BasisFactorization basisFactorization;
  private Random random;
  private GlopParameters parameters = new GlopParameters();

  private boolean mustRefactorizeBasis;
  private boolean recomputeBasicObjectiveLeftInverse = true;
  private boolean recomputeBasicObjective = true;
  private boolean recomputeReducedCosts = true;
  private boolean areReducedCostsPrecise;
  private boolean areReducedCostsRecomputed;
  private boolean hasCostShift;

  private double[] basicObjective = new double[0];
  private double[] costPerturbations = new double[0];
  private double[] reducedCosts = new double[0];
  private final ScatteredVector basicObjectiveLeftInverse = new ScatteredVector();
  private double dualFeasibilityTolerance;
  private final List<Runnable> watchers = new ArrayList<>();
  private double deterministicTime;

  ReducedCosts(CompactSparseMatrix matrix, RevisedSimplex.DoubleArrayRef objective,
      RevisedSimplex.IntArrayRef basis, VariablesInfo variablesInfo,
      BasisFactorization basisFactorization, Random random) {
    this.matrix = matrix;
    this.objective = objective;
    this.basis = basis;
    this.variablesInfo = variablesInfo;
    this.basisFactorization = basisFactorization;
    this.random = random;
  }

  /** Returns true if a refactorization is requested for precision reasons. */
  public boolean needsBasisRefactorization() {
    return mustRefactorizeBasis;
  }

  /** Changes the random generator. */
  public void setRandom(Random random) {
    this.random = random;
  }

  /**
   * Recomputes precisely the reduced cost of the entering column with its direction and triggers
   * a full recomputation if the incremental value was too imprecise. Returns the precise value.
   */
  public double testEnteringReducedCostPrecision(int enteringCol, ScatteredVector direction) {
    if (recomputeBasicObjective) {
      computeBasicObjective();
    }
    final double oldReducedCost = reducedCosts[enteringCol];
    final double preciseReducedCost = objective.get()[enteringCol]
        + costPerturbations[enteringCol] - LpUtils.scalarProduct(basicObjective, direction);
    reducedCosts[enteringCol] = preciseReducedCost;
    if (!recomputeReducedCosts) {
      final double estimatedReducedCostsAccuracy = oldReducedCost - preciseReducedCost;
      final double scale = (Math.abs(preciseReducedCost) <= 1.0) ? 1.0 : preciseReducedCost;
      if (Math.abs(estimatedReducedCostsAccuracy) / scale > parameters
          .getRecomputeReducedCostsThreshold()) {
        makeReducedCostsPrecise();
      }
    }
    return preciseReducedCost;
  }

  /** max |c_B - y.B| over the basic columns. */
  public double computeMaximumDualResidual() {
    double dualResidualError = 0.0;
    final int numRows = matrix.numRows();
    final double[] dualValues = getDualValues();
    final double[] obj = objective.get();
    final int[] b = basis.get();
    for (int row = 0; row < numRows; ++row) {
      final int basicCol = b[row];
      final double residual = obj[basicCol] + costPerturbations[basicCol]
          - matrix.columnScalarProduct(basicCol, dualValues);
      dualResidualError = Math.max(dualResidualError, Math.abs(residual));
    }
    return dualResidualError;
  }

  /** The maximum dual infeasibility over the relevant columns. */
  public double computeMaximumDualInfeasibility() {
    getReducedCosts();
    double maximumDualInfeasibility = 0.0;
    final BitSet canDecrease = variablesInfo.getCanDecreaseBitRow();
    final BitSet canIncrease = variablesInfo.getCanIncreaseBitRow();
    final BitSet relevant = variablesInfo.getIsRelevantBitRow();
    for (int col = relevant.nextSetBit(0); col >= 0; col = relevant.nextSetBit(col + 1)) {
      final double rc = reducedCosts[col];
      if ((canIncrease.get(col) && rc < 0.0) || (canDecrease.get(col) && rc > 0.0)) {
        maximumDualInfeasibility = Math.max(maximumDualInfeasibility, Math.abs(rc));
      }
    }
    return maximumDualInfeasibility;
  }

  /** The maximum dual infeasibility over the non-basic non-boxed columns. */
  public double computeMaximumDualInfeasibilityOnNonBoxedVariables() {
    getReducedCosts();
    double maximumDualInfeasibility = 0.0;
    final BitSet canDecrease = variablesInfo.getCanDecreaseBitRow();
    final BitSet canIncrease = variablesInfo.getCanIncreaseBitRow();
    final BitSet isBoxed = variablesInfo.getNonBasicBoxedVariables();
    final BitSet notBasic = variablesInfo.getNotBasicBitRow();
    for (int col = notBasic.nextSetBit(0); col >= 0; col = notBasic.nextSetBit(col + 1)) {
      if (isBoxed.get(col)) {
        continue;
      }
      final double rc = reducedCosts[col];
      if ((canIncrease.get(col) && rc < 0.0) || (canDecrease.get(col) && rc > 0.0)) {
        maximumDualInfeasibility = Math.max(maximumDualInfeasibility, Math.abs(rc));
      }
    }
    return maximumDualInfeasibility;
  }

  /** The sum of the dual infeasibilities over the relevant columns. */
  public double computeSumOfDualInfeasibilities() {
    getReducedCosts();
    double sum = 0.0;
    final BitSet canDecrease = variablesInfo.getCanDecreaseBitRow();
    final BitSet canIncrease = variablesInfo.getCanIncreaseBitRow();
    final BitSet relevant = variablesInfo.getIsRelevantBitRow();
    for (int col = relevant.nextSetBit(0); col >= 0; col = relevant.nextSetBit(col + 1)) {
      final double rc = reducedCosts[col];
      if ((canIncrease.get(col) && rc < 0.0) || (canDecrease.get(col) && rc > 0.0)) {
        sum += Math.abs(rc);
      }
    }
    return sum;
  }

  /** Updates the reduced costs and the basic objective before a basis pivot. */
  public void updateBeforeBasisPivot(int enteringCol, int leavingRow, ScatteredVector direction,
      UpdateRow updateRow) {
    final int leavingCol = basis.get()[leavingRow];
    if (!recomputeReducedCosts) {
      updateReducedCosts(enteringCol, leavingCol, leavingRow, direction.values[leavingRow],
          updateRow);
    }
    updateBasicObjective(enteringCol, leavingRow);
  }

  /** Sets the cost of a non-basic variable to zero (primal phase I). */
  public void setNonBasicVariableCostToZero(int col) {
    final double[] obj = objective.get();
    reducedCosts[col] -= obj[col];
    obj[col] = 0.0;
  }

  /** Sets the parameters. */
  public void setParameters(GlopParameters parameters) {
    this.parameters = parameters;
  }

  /** Returns true if the reduced costs were just recomputed on a refactorized basis. */
  public boolean areReducedCostsPrecise() {
    return areReducedCostsPrecise;
  }

  /** Returns true if the reduced costs are or will be recomputed from scratch. */
  public boolean areReducedCostsRecomputed() {
    return recomputeReducedCosts || areReducedCostsRecomputed;
  }

  /** To call when the objective changed. */
  public void resetForNewObjective() {
    recomputeBasicObjective = true;
    recomputeBasicObjectiveLeftInverse = true;
    areReducedCostsPrecise = false;
    setRecomputeReducedCostsAndNotifyWatchers();
  }

  /** To call when the basis was permuted. */
  public void updateDataOnBasisPermutation() {
    recomputeBasicObjective = true;
    recomputeBasicObjectiveLeftInverse = true;
  }

  /** Requests a precise recomputation (with a refactorized basis). */
  public void makeReducedCostsPrecise() {
    if (areReducedCostsPrecise) {
      return;
    }
    mustRefactorizeBasis = true;
    recomputeBasicObjectiveLeftInverse = true;
    setRecomputeReducedCostsAndNotifyWatchers();
  }

  /** Perturbs the costs in a direction that keeps a dual feasible solution feasible. */
  public void perturbCosts() {
    double maxCostMagnitude = 0.0;
    final int structuralSize = matrix.numCols() - matrix.numRows();
    final double[] obj = objective.get();
    for (int col = 0; col < structuralSize; ++col) {
      maxCostMagnitude = Math.max(maxCostMagnitude, Math.abs(obj[col]));
    }
    costPerturbations = new double[matrix.numCols()];
    final VariableType[] types = variablesInfo.getTypeRow();
    for (int col = 0; col < structuralSize; ++col) {
      final double o = obj[col];
      final double magnitude = (1.0 + random.nextDouble())
          * (parameters.getRelativeCostPerturbation() * Math.abs(o)
              + parameters.getRelativeMaxCostPerturbation() * maxCostMagnitude);
      switch (types[col]) {
        case UNCONSTRAINED:
        case FIXED_VARIABLE:
          break;
        case LOWER_BOUNDED:
          costPerturbations[col] = magnitude;
          break;
        case UPPER_BOUNDED:
          costPerturbations[col] = -magnitude;
          break;
        case UPPER_AND_LOWER_BOUNDED:
          if (o > 0.0) {
            costPerturbations[col] = magnitude;
          } else if (o < 0.0) {
            costPerturbations[col] = -magnitude;
          }
          break;
        default:
          break;
      }
    }
  }

  /** Shifts the cost of a column to guarantee a minimum dual step. */
  public void shiftCostIfNeeded(boolean increasingRcIsNeeded, int col) {
    final double minimumDelta =
        parameters.getDegenerateMinistepFactor() * dualFeasibilityTolerance;
    if (increasingRcIsNeeded && reducedCosts[col] <= -minimumDelta) {
      return;
    }
    if (!increasingRcIsNeeded && reducedCosts[col] >= minimumDelta) {
      return;
    }
    final double delta = increasingRcIsNeeded ? minimumDelta : -minimumDelta;
    costPerturbations[col] -= reducedCosts[col] + delta;
    reducedCosts[col] = -delta;
    hasCostShift = true;
  }

  /** Returns true if a cost was shifted. */
  public boolean hasCostShift() {
    return hasCostShift;
  }

  /** Returns true if the dual step on this column is degenerate. */
  public boolean stepIsDualDegenerate(boolean increasingRcIsNeeded, int col) {
    if (increasingRcIsNeeded && reducedCosts[col] >= 0.0) {
      return true;
    }
    return !increasingRcIsNeeded && reducedCosts[col] <= 0.0;
  }

  /** Removes all the cost shifts and perturbations. */
  public void clearAndRemoveCostShifts() {
    hasCostShift = false;
    costPerturbations = new double[matrix.numCols()];
    recomputeBasicObjective = true;
    recomputeBasicObjectiveLeftInverse = true;
    areReducedCostsPrecise = false;
    setRecomputeReducedCostsAndNotifyWatchers();
  }

  /** The reduced costs, recomputed from scratch if not already done since the last change. */
  public double[] getFullReducedCosts() {
    if (!areReducedCostsRecomputed) {
      setRecomputeReducedCostsAndNotifyWatchers();
    }
    return getReducedCosts();
  }

  /** The current reduced costs. */
  public double[] getReducedCosts() {
    if (basisFactorization.isRefactorized()) {
      mustRefactorizeBasis = false;
    }
    if (recomputeReducedCosts) {
      computeReducedCosts();
    }
    return reducedCosts;
  }

  /** The dual values y with y.B = c_B. */
  public double[] getDualValues() {
    computeBasicObjectiveLeftInverse();
    return basicObjectiveLeftInverse.values;
  }

  /** The current dual feasibility tolerance (at least the dual residual). */
  public double getDualFeasibilityTolerance() {
    return dualFeasibilityTolerance;
  }

  /** Returns true if the column can enter the basis in the primal simplex. */
  public boolean isValidPrimalEnteringCandidate(int col) {
    final double reducedCost = reducedCosts[col];
    final double tolerance = dualFeasibilityTolerance;
    return (variablesInfo.getCanIncreaseBitRow().get(col) && reducedCost < -tolerance)
        || (variablesInfo.getCanDecreaseBitRow().get(col) && reducedCost > tolerance);
  }

  /** The current cost perturbations. */
  public double[] getCostPerturbations() {
    return costPerturbations;
  }

  /** The deterministic time spent in this class. */
  public double deterministicTime() {
    return deterministicTime;
  }

  /** Registers an action run whenever a full recomputation is requested. */
  public void addRecomputationWatcher(Runnable watcher) {
    watchers.add(watcher);
  }

  private void computeBasicObjective() {
    final int numColsInBasis = matrix.numRows();
    if (costPerturbations.length != matrix.numCols()) {
      costPerturbations = Arrays.copyOf(costPerturbations, matrix.numCols());
    }
    if (basicObjective.length != numColsInBasis) {
      basicObjective = new double[numColsInBasis];
    }
    final double[] obj = objective.get();
    final int[] b = basis.get();
    for (int col = 0; col < numColsInBasis; ++col) {
      final int basisCol = b[col];
      basicObjective[col] = obj[basisCol] + costPerturbations[basisCol];
    }
    recomputeBasicObjective = false;
    recomputeBasicObjectiveLeftInverse = true;
  }

  private void computeReducedCosts() {
    if (recomputeBasicObjectiveLeftInverse) {
      computeBasicObjectiveLeftInverse();
    }
    double dualResidualError = 0.0;
    final int numCols = matrix.numCols();
    final int firstSlack = numCols - matrix.numRows();
    if (reducedCosts.length != numCols) {
      reducedCosts = Arrays.copyOf(reducedCosts, numCols);
    }
    final BitSet isBasic = variablesInfo.getIsBasicBitRow();
    final double[] obj = objective.get();
    final double[] y = basicObjectiveLeftInverse.values;
    for (int col = 0; col < firstSlack; ++col) {
      reducedCosts[col] =
          obj[col] + costPerturbations[col] - matrix.columnScalarProduct(col, y);
      if (isBasic.get(col)) {
        dualResidualError = Math.max(dualResidualError, Math.abs(reducedCosts[col]));
      }
    }
    for (int col = firstSlack; col < numCols; ++col) {
      reducedCosts[col] = obj[col] + costPerturbations[col] - y[col - firstSlack];
      if (isBasic.get(col)) {
        dualResidualError = Math.max(dualResidualError, Math.abs(reducedCosts[col]));
      }
    }
    deterministicTime += LpTypes.deterministicTimeForFpOperations(matrix.numEntries());
    recomputeReducedCosts = false;
    areReducedCostsRecomputed = true;
    areReducedCostsPrecise = basisFactorization.isRefactorized();
    dualFeasibilityTolerance = parameters.getDualFeasibilityTolerance();
    if (dualResidualError > dualFeasibilityTolerance) {
      dualFeasibilityTolerance = dualResidualError;
    }
  }

  private void computeBasicObjectiveLeftInverse() {
    if (recomputeBasicObjective) {
      computeBasicObjective();
    }
    basicObjectiveLeftInverse.values = basicObjective.clone();
    basicObjectiveLeftInverse.clearNonZeros();
    basisFactorization.leftSolve(basicObjectiveLeftInverse);
    recomputeBasicObjectiveLeftInverse = false;
  }

  private void updateReducedCosts(int enteringCol, int leavingCol, int leavingRow, double pivot,
      UpdateRow updateRow) {
    if (recomputeReducedCosts) {
      return;
    }
    final double enteringReducedCost = reducedCosts[enteringCol];
    if (enteringReducedCost == 0.0) {
      areReducedCostsPrecise = false;
      return;
    }
    areReducedCostsRecomputed = false;
    areReducedCostsPrecise = false;
    updateRow.computeUpdateRow(leavingRow);
    final double newLeavingReducedCost = enteringReducedCost / -pivot;
    final double[] updateCoeffs = updateRow.getCoefficients();
    final int[] positions = updateRow.getNonZeroPositions();
    final int numPositions = updateRow.getNumNonZeroPositions();
    for (int i = 0; i < numPositions; ++i) {
      final int col = positions[i];
      reducedCosts[col] += newLeavingReducedCost * updateCoeffs[col];
    }
    reducedCosts[leavingCol] = newLeavingReducedCost;
    reducedCosts[enteringCol] = 0.0;
  }

  private void updateBasicObjective(int enteringCol, int leavingRow) {
    basicObjective[leavingRow] =
        objective.get()[enteringCol] + costPerturbations[enteringCol];
    recomputeBasicObjectiveLeftInverse = true;
  }

  private void setRecomputeReducedCostsAndNotifyWatchers() {
    recomputeReducedCosts = true;
    for (Runnable watcher : watchers) {
      watcher.run();
    }
  }
}
