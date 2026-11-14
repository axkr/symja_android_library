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
// ortools/glop/dual_edge_norms.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Arrays;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ScatteredVector;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * The dual steepest edge squared norms ||e_row.B^-1||^2 of the rows of the basis, updated with the
 * formulas of Forrest and Goldfarb (see Koberstein's thesis, section 8.2.2.1).
 */
public final class DualEdgeNorms {

  private GlopParameters parameters = new GlopParameters();
  private TimeLimit timeLimit;
  private final BasisFactorization basisFactorization;
  private double[] edgeSquaredNorms = new double[0];
  private boolean recomputeEdgeSquaredNorms = true;

  DualEdgeNorms(BasisFactorization basisFactorization) {
    this.basisFactorization = basisFactorization;
  }

  /** Clears the norms, they will be recomputed. */
  public void clear() {
    recomputeEdgeSquaredNorms = true;
  }

  /** Resizes after new rows were added, the new norms are 1.0. */
  public void resizeOnNewRows(int newSize) {
    final int old = edgeSquaredNorms.length;
    edgeSquaredNorms = Arrays.copyOf(edgeSquaredNorms, newSize);
    for (int i = old; i < newSize; ++i) {
      edgeSquaredNorms[i] = 1.0;
    }
  }

  /**
   * Returns true iff the norms are currently valid, that is the next call to
   * {@link #getEdgeSquaredNorms()} will not trigger a full recomputation.
   */
  public boolean areValid() {
    return !needsBasisRefactorization();
  }

  /**
   * The current norms without triggering any recomputation. They are only meaningful if
   * {@link #areValid()} is true. This is the internal array, it must not be modified.
   */
  double[] edgeSquaredNorms() {
    return edgeSquaredNorms;
  }

  /**
   * Advanced usage. Replaces the current norms by the given ones, which must have one entry per row
   * of the (refactorized) basis. Any entry that is not strictly positive is recomputed exactly.
   * This is used to transfer the norms of a previous solve when the basis didn't change much,
   * instead of paying for a full recomputation.
   */
  public void setEdgeSquaredNorms(double[] norms) {
    edgeSquaredNorms = norms;
    final int numRows = edgeSquaredNorms.length;

    // Safety net: the precision of a few of the given norms is checked, and if one of them is too
    // far off, everything is recomputed from scratch on the next getEdgeSquaredNorms().
    final int stride = Math.max(1, numRows / 10);
    for (int row = 0; row < numRows; row += stride) {
      final double givenNorm = edgeSquaredNorms[row];
      if (givenNorm <= 0.0) {
        continue;
      }
      final double exactNorm = basisFactorization.dualEdgeSquaredNorm(row);
      if (givenNorm < 0.25 * exactNorm || givenNorm > 4.0 * exactNorm) {
        recomputeEdgeSquaredNorms = true;
        return;
      }
      edgeSquaredNorms[row] = exactNorm;
    }

    // Compute exactly the unknown norms.
    for (int row = 0; row < numRows; ++row) {
      if (edgeSquaredNorms[row] > 0.0) {
        continue;
      }
      edgeSquaredNorms[row] = basisFactorization.dualEdgeSquaredNorm(row);
    }
    recomputeEdgeSquaredNorms = false;
  }

  /** Returns true if the norms must be recomputed on a refactorized basis. */
  public boolean needsBasisRefactorization() {
    return recomputeEdgeSquaredNorms;
  }

  /** The squared norms. */
  public double[] getEdgeSquaredNorms() {
    if (recomputeEdgeSquaredNorms) {
      computeEdgeSquaredNorms();
    }
    return edgeSquaredNorms;
  }

  /** Permutes the norms like the basis. */
  public void updateDataOnBasisPermutation(int[] colPerm) {
    if (recomputeEdgeSquaredNorms) {
      return;
    }
    if (colPerm.length == 0) {
      return;
    }
    LpUtils.applyColumnPermutationToRowIndexedVector(colPerm, edgeSquaredNorms);
  }

  /**
   * Compares the stored norm of the leaving row with the precise one and stores the latter.
   * Returns false if the stored norm was under-estimated (another leaving row may be chosen).
   */
  public boolean testPrecision(int leavingRow, ScatteredVector unitRowLeftInverse) {
    if (recomputeEdgeSquaredNorms) {
      return true;
    }
    final double leavingSquaredNorm = unitRowLeftInverse.squaredNorm();
    final double oldSquaredNorm = edgeSquaredNorms[leavingRow];
    final double estimatedEdgeNormsAccuracy =
        (Math.sqrt(leavingSquaredNorm) - Math.sqrt(oldSquaredNorm)) / Math.sqrt(leavingSquaredNorm);
    if (Math.abs(estimatedEdgeNormsAccuracy) > parameters.getRecomputeEdgesNormThreshold()) {
      recomputeEdgeSquaredNorms = true;
    }
    edgeSquaredNorms[leavingRow] = leavingSquaredNorm;
    return oldSquaredNorm > 0.25 * leavingSquaredNorm;
  }

  /** Updates the norms before a basis pivot. */
  public void updateBeforeBasisPivot(int enteringCol, int leavingRow, ScatteredVector direction,
      ScatteredVector unitRowLeftInverse) {
    if (recomputeEdgeSquaredNorms) {
      return;
    }
    final double[] tau = basisFactorization.rightSolveForTau(unitRowLeftInverse);
    final double pivot = direction.values[leavingRow];
    final double newLeavingSquaredNorm = edgeSquaredNorms[leavingRow] / (pivot * pivot);
    final double kLowerBound = 1e-4;
    for (int i = 0; i < direction.numNonZeros; ++i) {
      final int row = direction.nonZeros[i];
      final double c = direction.values[row];
      edgeSquaredNorms[row] += c * (c * newLeavingSquaredNorm - 2.0 / pivot * tau[row]);
      if (edgeSquaredNorms[row] < kLowerBound) {
        if (row == leavingRow) {
          continue;
        }
        edgeSquaredNorms[row] = kLowerBound;
      }
    }
    edgeSquaredNorms[leavingRow] = newLeavingSquaredNorm;
  }

  /** Sets the parameters. */
  public void setParameters(GlopParameters parameters) {
    this.parameters = parameters;
  }

  /** Sets the time limit checked during long recomputations. */
  public void setTimeLimit(TimeLimit timeLimit) {
    this.timeLimit = timeLimit;
  }

  private void computeEdgeSquaredNorms() {
    final boolean testLimit =
        timeLimit != null && basisFactorization.numberOfEntriesInLU() > 10_000;
    final int numRows = basisFactorization.getNumberOfRows();
    if (edgeSquaredNorms.length != numRows) {
      edgeSquaredNorms = new double[numRows];
      Arrays.fill(edgeSquaredNorms, 1.0);
    }
    for (int row = 0; row < numRows; ++row) {
      edgeSquaredNorms[row] = basisFactorization.dualEdgeSquaredNorm(row);
      if (testLimit && timeLimit.limitReached()) {
        break;
      }
    }
    recomputeEdgeSquaredNorms = false;
  }
}
