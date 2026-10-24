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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/glop/update_row.h/.cc
// C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Arrays;
import java.util.BitSet;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.ScatteredVector;

/**
 * Computes the "update row" of a simplex pivot: the leaving row of B^-1.N, i.e. the coefficients
 * of the non-basic relevant columns in the row of the leaving variable. It is computed either
 * row-wise (with the transposed matrix) or column-wise depending on the sparsity.
 */
public final class UpdateRow {

  private final CompactSparseMatrix matrix;
  private final CompactSparseMatrix transposedMatrix;
  private final VariablesInfo variablesInfo;
  private final RevisedSimplex.IntArrayRef basis;
  private final BasisFactorization basisFactorization;

  private final ScatteredVector unitRowLeftInverse = new ScatteredVector();
  private int[] unitRowLeftInverseFilteredNonZeros = new int[0];
  private int numFilteredNonZeros;
  private int numNonZeros;
  private int[] nonZeroPositionList = new int[0];
  private BitSet nonZeroPositionSet = new BitSet();
  private double[] coefficient = new double[0];
  private int leftInverseComputedFor = LpTypes.INVALID_ROW;
  private int updateRowComputedFor = LpTypes.INVALID_ROW;
  private long numOperations;
  private GlopParameters parameters = new GlopParameters();

  UpdateRow(CompactSparseMatrix matrix, CompactSparseMatrix transposedMatrix,
      VariablesInfo variablesInfo, RevisedSimplex.IntArrayRef basis,
      BasisFactorization basisFactorization) {
    this.matrix = matrix;
    this.transposedMatrix = transposedMatrix;
    this.variablesInfo = variablesInfo;
    this.basis = basis;
    this.basisFactorization = basisFactorization;
  }

  /** Invalidates the cached computations (after a basis change). */
  public void invalidate() {
    leftInverseComputedFor = LpTypes.INVALID_ROW;
    updateRowComputedFor = LpTypes.INVALID_ROW;
  }

  /** The left inverse e_leaving_row.B^-1 of the last computation. */
  public ScatteredVector getUnitRowLeftInverse() {
    return unitRowLeftInverse;
  }

  /** Computes the left inverse of a row on a refactorized basis without caching it. */
  public ScatteredVector computeAndGetUnitRowLeftInverse(int leavingRow) {
    invalidate();
    basisFactorization.temporaryLeftSolveForUnitRow(leavingRow, unitRowLeftInverse);
    return unitRowLeftInverse;
  }

  /** Computes the left inverse of the leaving row (cached). */
  public void computeUnitRowLeftInverse(int leavingRow) {
    if (leftInverseComputedFor == leavingRow) {
      return;
    }
    leftInverseComputedFor = leavingRow;
    basisFactorization.leftSolveForUnitRow(leavingRow, unitRowLeftInverse);
  }

  /** Returns true if the update row was computed for this row. */
  public boolean isComputedFor(int leavingRow) {
    return updateRowComputedFor == leavingRow;
  }

  /** The dense coefficients of the update row (only valid at the non-zero positions). */
  public double[] getCoefficients() {
    return coefficient;
  }

  /** The non-zero positions of the update row. */
  public int[] getNonZeroPositions() {
    return nonZeroPositionList;
  }

  /** The number of non-zero positions of the update row. */
  public int getNumNonZeroPositions() {
    return numNonZeros;
  }

  /** The coefficient of a column in the update row. */
  public double getCoefficient(int col) {
    return coefficient[col];
  }

  /** Sets the parameters. */
  public void setParameters(GlopParameters parameters) {
    this.parameters = parameters;
  }

  /** The deterministic time spent in this class. */
  public double deterministicTime() {
    return LpTypes.deterministicTimeForFpOperations(numOperations);
  }

  private void pushFiltered(int col) {
    if (numFilteredNonZeros == unitRowLeftInverseFilteredNonZeros.length) {
      unitRowLeftInverseFilteredNonZeros = Arrays.copyOf(unitRowLeftInverseFilteredNonZeros,
          Math.max(8, 2 * unitRowLeftInverseFilteredNonZeros.length));
    }
    unitRowLeftInverseFilteredNonZeros[numFilteredNonZeros++] = col;
  }

  private void ensureOutputSize() {
    final int numCols = matrix.numCols();
    if (coefficient.length != numCols) {
      coefficient = Arrays.copyOf(coefficient, numCols);
    }
    if (nonZeroPositionList.length != numCols) {
      nonZeroPositionList = new int[numCols];
    }
  }

  /** Computes the update row for the leaving row (cached). */
  public void computeUpdateRow(int leavingRow) {
    if (updateRowComputedFor == leavingRow) {
      return;
    }
    updateRowComputedFor = leavingRow;
    computeUnitRowLeftInverse(leavingRow);
    ensureOutputSize();
    if (parameters.getUseTransposedMatrix()) {
      long numRowWiseEntries = 0;
      final double dropTolerance = parameters.getDropTolerance();
      numFilteredNonZeros = 0;
      final double[] values = unitRowLeftInverse.values;
      if (unitRowLeftInverse.numNonZeros == 0) {
        for (int col = 0; col < values.length; ++col) {
          if (Math.abs(values[col]) > dropTolerance) {
            pushFiltered(col);
            numRowWiseEntries += transposedMatrix.columnNumEntries(col);
          }
        }
      } else {
        for (int i = 0; i < unitRowLeftInverse.numNonZeros; ++i) {
          final int col = unitRowLeftInverse.nonZeros[i];
          if (Math.abs(values[col]) > dropTolerance) {
            pushFiltered(col);
            numRowWiseEntries += transposedMatrix.columnNumEntries(col);
          }
        }
      }
      if (numFilteredNonZeros == 1) {
        computeUpdatesForSingleRow(unitRowLeftInverseFilteredNonZeros[0]);
        numOperations += numRowWiseEntries;
        return;
      }
      final long numColWiseEntries = variablesInfo.getNumEntriesInRelevantColumns();
      final double rowWise = numRowWiseEntries;
      if (rowWise < 0.5 * numColWiseEntries) {
        if (rowWise < 1.1 * matrix.numCols()) {
          computeUpdatesRowWiseHypersparse();
          numOperations += 5 * numRowWiseEntries + matrix.numCols() / 64;
        } else {
          computeUpdatesRowWise();
          numOperations += numRowWiseEntries + matrix.numRows();
        }
      } else {
        computeUpdatesColumnWise();
        numOperations += numColWiseEntries + matrix.numCols();
      }
    } else {
      computeUpdatesColumnWise();
      numOperations += variablesInfo.getNumEntriesInRelevantColumns() + matrix.numCols();
    }
  }

  private void computeUpdatesRowWise() {
    Arrays.fill(coefficient, 0.0);
    for (int k = 0; k < numFilteredNonZeros; ++k) {
      final int col = unitRowLeftInverseFilteredNonZeros[k];
      final double multiplier = unitRowLeftInverse.values[col];
      for (int i = transposedMatrix.columnStart(col); i < transposedMatrix.columnStart(col + 1);
          ++i) {
        coefficient[transposedMatrix.entryRow(i)] += multiplier * transposedMatrix
            .entryCoefficient(i);
      }
    }
    final double dropTolerance = parameters.getDropTolerance();
    int n = 0;
    final BitSet relevant = variablesInfo.getIsRelevantBitRow();
    for (int col = relevant.nextSetBit(0); col >= 0; col = relevant.nextSetBit(col + 1)) {
      if (Math.abs(coefficient[col]) > dropTolerance) {
        nonZeroPositionList[n++] = col;
      }
    }
    numNonZeros = n;
  }

  private void computeUpdatesRowWiseHypersparse() {
    final int numCols = matrix.numCols();
    nonZeroPositionSet = new BitSet(numCols);
    for (int k = 0; k < numFilteredNonZeros; ++k) {
      final int col = unitRowLeftInverseFilteredNonZeros[k];
      final double multiplier = unitRowLeftInverse.values[col];
      for (int i = transposedMatrix.columnStart(col); i < transposedMatrix.columnStart(col + 1);
          ++i) {
        final int pos = transposedMatrix.entryRow(i);
        final double v = multiplier * transposedMatrix.entryCoefficient(i);
        if (!nonZeroPositionSet.get(pos)) {
          coefficient[pos] = v;
          nonZeroPositionSet.set(pos);
        } else {
          coefficient[pos] += v;
        }
      }
    }
    nonZeroPositionSet.and(variablesInfo.getIsRelevantBitRow());
    final double dropTolerance = parameters.getDropTolerance();
    int n = 0;
    for (int col = nonZeroPositionSet.nextSetBit(0); col >= 0;
        col = nonZeroPositionSet.nextSetBit(col + 1)) {
      if (Math.abs(coefficient[col]) > dropTolerance) {
        nonZeroPositionList[n++] = col;
      }
    }
    numNonZeros = n;
  }

  private void computeUpdatesForSingleRow(int rowAsCol) {
    final BitSet isRelevant = variablesInfo.getIsRelevantBitRow();
    final double dropTolerance = parameters.getDropTolerance();
    final double multiplier = unitRowLeftInverse.values[rowAsCol];
    int n = 0;
    for (int i = transposedMatrix.columnStart(rowAsCol); i < transposedMatrix
        .columnStart(rowAsCol + 1); ++i) {
      final int pos = transposedMatrix.entryRow(i);
      if (!isRelevant.get(pos)) {
        continue;
      }
      final double v = multiplier * transposedMatrix.entryCoefficient(i);
      if (Math.abs(v) > dropTolerance) {
        coefficient[pos] = v;
        nonZeroPositionList[n++] = pos;
      }
    }
    numNonZeros = n;
  }

  private void computeUpdatesColumnWise() {
    final int firstSlack = matrix.numCols() - matrix.numRows();
    final double dropTolerance = parameters.getDropTolerance();
    final double[] y = unitRowLeftInverse.values;
    final BitSet relevant = variablesInfo.getIsRelevantBitRow();
    int n = 0;
    for (int col = relevant.nextSetBit(0); col >= 0; col = relevant.nextSetBit(col + 1)) {
      final double coeff =
          col >= firstSlack ? y[col - firstSlack] : matrix.columnScalarProduct(col, y);
      if (Math.abs(coeff) > dropTolerance) {
        nonZeroPositionList[n++] = col;
        coefficient[col] = coeff;
      }
    }
    numNonZeros = n;
  }

  /** Computes the full row (basic and non-basic columns) for the dual ray. */
  public double[] computeFullUpdateRow(int leavingRow) {
    final int numCols = matrix.numCols();
    final double[] output = new double[numCols];
    output[basis.get()[leavingRow]] = 1.0;
    final double dropTolerance = parameters.getDropTolerance();
    final double[] y = unitRowLeftInverse.values;
    final BitSet notBasic = variablesInfo.getNotBasicBitRow();
    for (int col = notBasic.nextSetBit(0); col >= 0; col = notBasic.nextSetBit(col + 1)) {
      final double coeff = matrix.columnScalarProduct(col, y);
      if (Math.abs(coeff) > dropTolerance) {
        output[col] = coeff;
      }
    }
    return output;
  }
}
