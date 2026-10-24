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
// ortools/glop/primal_edge_norms.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ScatteredVector;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * The weights of the primal pricing: the squared column norms (Dantzig), the steepest edge squared
 * norms 1 + ||B^-1.a_j||^2 (updated with the Goldfarb-Reid formulas) or the Devex weights.
 */
public final class PrimalEdgeNorms {

  private final CompactSparseMatrix compactMatrix;
  private final VariablesInfo variablesInfo;
  private final BasisFactorization basisFactorization;
  private TimeLimit timeLimit;
  private GlopParameters parameters = new GlopParameters();
  private GlopParameters.PricingRule pricingRule = GlopParameters.PricingRule.DANTZIG;

  private boolean recomputeEdgeSquaredNorms = true;
  private boolean resetDevexWeights = true;
  private double[] edgeSquaredNorms = new double[0];
  private double[] matrixColumnNorms = new double[0];
  private double[] devexWeights = new double[0];
  private int numDevexUpdatesSinceReset;
  private final ScatteredVector directionLeftInverse = new ScatteredVector();
  private long numOperations;
  private final List<Runnable> watchers = new ArrayList<>();

  PrimalEdgeNorms(CompactSparseMatrix compactMatrix, VariablesInfo variablesInfo,
      BasisFactorization basisFactorization) {
    this.compactMatrix = compactMatrix;
    this.variablesInfo = variablesInfo;
    this.basisFactorization = basisFactorization;
  }

  /** Clears everything, the norms will be recomputed. */
  public void clear() {
    matrixColumnNorms = new double[0];
    recomputeEdgeSquaredNorms = true;
    resetDevexWeights = true;
    for (Runnable w : watchers) {
      w.run();
    }
  }

  /** Returns true if the steepest edge norms must be recomputed on a refactorized basis. */
  public boolean needsBasisRefactorization() {
    if (pricingRule != GlopParameters.PricingRule.STEEPEST_EDGE) {
      return false;
    }
    return recomputeEdgeSquaredNorms;
  }

  /** The weights corresponding to the current pricing rule. */
  public double[] getSquaredNorms() {
    switch (pricingRule) {
      case DANTZIG:
        return getMatrixColumnNorms();
      case STEEPEST_EDGE:
        return getEdgeSquaredNorms();
      case DEVEX:
      default:
        return getDevexWeights();
    }
  }

  /** The steepest edge squared norms. */
  public double[] getEdgeSquaredNorms() {
    if (recomputeEdgeSquaredNorms) {
      computeEdgeSquaredNorms();
    }
    return edgeSquaredNorms;
  }

  /** The Devex weights. */
  public double[] getDevexWeights() {
    if (resetDevexWeights) {
      resetDevexWeights();
    }
    return devexWeights;
  }

  /** The squared norms of the matrix columns. */
  public double[] getMatrixColumnNorms() {
    if (matrixColumnNorms.length == 0) {
      computeMatrixColumnNorms();
    }
    return matrixColumnNorms;
  }

  /**
   * Recomputes precisely the norm of the entering edge; returns false if the stored value was
   * much too small (the entering column must then be re-priced).
   */
  public boolean testEnteringEdgeNormPrecision(int enteringCol, ScatteredVector direction) {
    if (!recomputeEdgeSquaredNorms) {
      final double oldSquaredNorm = edgeSquaredNorms[enteringCol];
      final double preciseSquaredNorm = 1.0 + direction.squaredNorm();
      edgeSquaredNorms[enteringCol] = preciseSquaredNorm;
      final double preciseNorm = Math.sqrt(preciseSquaredNorm);
      final double estimatedEdgesNormAccuracy =
          (preciseNorm - Math.sqrt(oldSquaredNorm)) / preciseNorm;
      if (Math.abs(estimatedEdgesNormAccuracy) > parameters.getRecomputeEdgesNormThreshold()) {
        recomputeEdgeSquaredNorms = true;
        for (Runnable w : watchers) {
          w.run();
        }
      }
      if (oldSquaredNorm < 0.25 * preciseSquaredNorm) {
        return false;
      }
    }
    return true;
  }

  /** Updates the norms before a basis pivot. */
  public void updateBeforeBasisPivot(int enteringCol, int leavingCol, int leavingRow,
      ScatteredVector direction, UpdateRow updateRow) {
    if (!recomputeEdgeSquaredNorms) {
      updateRow.computeUpdateRow(leavingRow);
      computeDirectionLeftInverse(enteringCol, direction);
      updateEdgeSquaredNorms(enteringCol, leavingCol, leavingRow, direction.values, updateRow);
    }
    if (!resetDevexWeights) {
      ++numDevexUpdatesSinceReset;
      if (numDevexUpdatesSinceReset > parameters.getDevexWeightsResetPeriod()) {
        resetDevexWeights = true;
      } else {
        updateRow.computeUpdateRow(leavingRow);
        updateDevexWeights(enteringCol, leavingCol, leavingRow, direction.values, updateRow);
      }
    }
  }

  /** Sets the parameters. */
  public void setParameters(GlopParameters parameters) {
    this.parameters = parameters;
  }

  /** Sets the pricing rule. */
  public void setPricingRule(GlopParameters.PricingRule rule) {
    this.pricingRule = rule;
  }

  /** Registers an action run whenever the norms must be recomputed. */
  public void addRecomputationWatcher(Runnable watcher) {
    watchers.add(watcher);
  }

  /** The deterministic time spent in this class. */
  public double deterministicTime() {
    return LpTypes.deterministicTimeForFpOperations(numOperations);
  }

  /** Sets the time limit checked during long recomputations. */
  public void setTimeLimit(TimeLimit timeLimit) {
    this.timeLimit = timeLimit;
  }

  private void computeMatrixColumnNorms() {
    final int numCols = compactMatrix.numCols();
    matrixColumnNorms = new double[numCols];
    for (int col = 0; col < numCols; ++col) {
      matrixColumnNorms[col] = compactMatrix.columnSquaredNorm(col);
      numOperations += compactMatrix.columnNumEntries(col);
    }
  }

  private void computeEdgeSquaredNorms() {
    final boolean testLimit =
        timeLimit != null && basisFactorization.numberOfEntriesInLU() > 10_000;
    final int numCols = compactMatrix.numCols();
    if (edgeSquaredNorms.length != numCols) {
      final int old = edgeSquaredNorms.length;
      edgeSquaredNorms = Arrays.copyOf(edgeSquaredNorms, numCols);
      for (int i = old; i < numCols; ++i) {
        edgeSquaredNorms[i] = 1.0;
      }
    }
    final BitSet relevant = variablesInfo.getIsRelevantBitRow();
    for (int col = relevant.nextSetBit(0); col >= 0; col = relevant.nextSetBit(col + 1)) {
      edgeSquaredNorms[col] = 1.0 + basisFactorization.rightSolveSquaredNorm(col);
      if (testLimit && timeLimit.limitReached()) {
        break;
      }
    }
    recomputeEdgeSquaredNorms = false;
  }

  private void computeDirectionLeftInverse(int enteringCol, ScatteredVector direction) {
    directionLeftInverse.values = direction.values.clone();
    directionLeftInverse.clearNonZeros();
    basisFactorization.leftSolve(directionLeftInverse);
  }

  private void updateEdgeSquaredNorms(int enteringCol, int leavingCol, int leavingRow,
      double[] direction, UpdateRow updateRow) {
    final double pivot = -direction[leavingRow];
    final int firstSlack = compactMatrix.numCols() - compactMatrix.numRows();
    final double enteringSquaredNorm = edgeSquaredNorms[enteringCol];
    final double leavingSquaredNorm = Math.max(1.0, enteringSquaredNorm / (pivot * pivot));
    final double factor = 2.0 / pivot;
    final double[] dli = directionLeftInverse.values;
    final int[] positions = updateRow.getNonZeroPositions();
    final int numPositions = updateRow.getNumNonZeroPositions();
    for (int i = 0; i < numPositions; ++i) {
      final int col = positions[i];
      final double coeff = updateRow.getCoefficient(col);
      final double scalarProduct =
          col >= firstSlack ? dli[col - firstSlack] : compactMatrix.columnScalarProduct(col, dli);
      numOperations += compactMatrix.columnNumEntries(col);
      edgeSquaredNorms[col] += coeff * (coeff * leavingSquaredNorm + factor * scalarProduct);
      final double lowerBound = 1.0 + LpUtils.square(coeff / pivot);
      if (edgeSquaredNorms[col] < lowerBound) {
        edgeSquaredNorms[col] = lowerBound;
      }
    }
    edgeSquaredNorms[leavingCol] = leavingSquaredNorm;
  }

  private void updateDevexWeights(int enteringCol, int leavingCol, int leavingRow,
      double[] direction, UpdateRow updateRow) {
    final double enteringNorm = Math.sqrt(LpUtils.preciseSquaredNorm(direction));
    final double pivotMagnitude = Math.abs(direction[leavingRow]);
    final double leavingNorm = Math.max(1.0, enteringNorm / pivotMagnitude);
    final int[] positions = updateRow.getNonZeroPositions();
    final int numPositions = updateRow.getNumNonZeroPositions();
    for (int i = 0; i < numPositions; ++i) {
      final int col = positions[i];
      final double coeff = updateRow.getCoefficient(col);
      final double updateVectorNorm = Math.abs(coeff) * leavingNorm;
      devexWeights[col] = Math.max(devexWeights[col], updateVectorNorm * updateVectorNorm);
    }
    devexWeights[leavingCol] = leavingNorm * leavingNorm;
  }

  private void resetDevexWeights() {
    if (parameters.getInitializeDevexWithColumnNorms()) {
      devexWeights = getMatrixColumnNorms().clone();
    } else {
      devexWeights = new double[compactMatrix.numCols()];
      Arrays.fill(devexWeights, 1.0);
    }
    numDevexUpdatesSinceReset = 0;
    resetDevexWeights = false;
  }
}
