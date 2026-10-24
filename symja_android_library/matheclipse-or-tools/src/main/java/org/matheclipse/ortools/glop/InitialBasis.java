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
// ortools/glop/initial_basis.h/.cc and the MatrixNonZeroPattern of markowitz.h/.cc C++ to Java,
// see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.VariableType;

/**
 * Crash procedures replacing slack (mostly fixed slack) columns of the all slack initial basis by
 * structural columns: the triangular completion (default), Bixby's method and Maros' method. The
 * resulting basis is triangular after permutation and thus factorizable.
 */
public final class InitialBasis {

  /** The non-zero pattern of the residual matrix used by the triangular and Maros crashes. */
  static final class MatrixNonZeroPattern {
    private int[] rowDegree = new int[0];
    private int[] colDegree = new int[0];
    private List<List<Integer>> rowNonZero = new ArrayList<>();
    private boolean[] deletedColumns = new boolean[0];

    void reset(int numRows, int numCols) {
      rowDegree = new int[numRows];
      colDegree = new int[numCols];
      rowNonZero = new ArrayList<>(numRows);
      for (int i = 0; i < numRows; ++i) {
        rowNonZero.add(new ArrayList<>());
      }
      deletedColumns = new boolean[numCols];
    }

    void addEntry(int row, int col) {
      ++rowDegree[row];
      ++colDegree[col];
      rowNonZero.get(row).add(col);
    }

    int colDegree(int col) {
      return colDegree[col];
    }

    int rowDegree(int row) {
      return rowDegree[row];
    }

    int decreaseColDegree(int col) {
      return --colDegree[col];
    }

    void deleteRowAndColumn(int pivotRow, int pivotCol) {
      deletedColumns[pivotCol] = true;
      rowDegree[pivotRow] = 0;
    }

    List<Integer> rowNonZero(int row) {
      return rowNonZero.get(row);
    }
  }

  private double maxScaledAbsCost;
  private final CompactSparseMatrix compactMatrix;
  private final double[] objective;
  private final double[] lowerBound;
  private final double[] upperBound;
  private final VariableType[] variableType;

  /** Creates the crash for the given problem (with slacks). */
  public InitialBasis(CompactSparseMatrix compactMatrix, double[] objective, double[] lowerBound,
      double[] upperBound, VariableType[] variableType) {
    this.compactMatrix = compactMatrix;
    this.objective = objective;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.variableType = variableType;
  }

  private double restrictedInfinityNorm(int col, boolean[] rowsToConsider, int[] rowIndex) {
    double infinityNorm = 0.0;
    for (int i = compactMatrix.columnStart(col); i < compactMatrix.columnStart(col + 1); ++i) {
      final int row = compactMatrix.entryRow(i);
      final double magnitude = Math.abs(compactMatrix.entryCoefficient(i));
      if (rowsToConsider[row] && magnitude > infinityNorm) {
        infinityNorm = magnitude;
        rowIndex[0] = row;
      }
    }
    return infinityNorm;
  }

  /**
   * Completes the basis (entries -1 are free positions) with Bixby's method using the columns in
   * [0, numCols).
   */
  public void completeBixbyBasis(int numCols, int[] basis) {
    final int numRows = compactMatrix.numRows();
    final boolean[] canBeReplaced = new boolean[numRows];
    final boolean[] hasZeroCoefficient = new boolean[numRows];
    for (int row = 0; row < numRows; ++row) {
      if (basis[row] == LpTypes.INVALID_COL) {
        canBeReplaced[row] = true;
        hasZeroCoefficient[row] = true;
      }
    }
    final double[] scaledDiagonalAbs = new double[numRows];
    Arrays.fill(scaledDiagonalAbs, LpTypes.INFINITY);
    final List<Integer> candidates = computeCandidates(numCols);
    final int[] rowIndex = new int[1];
    for (int candidate : candidates) {
      boolean enterBasis = false;
      if (compactMatrix.columnInfinityNorm(candidate) != 1.0) {
        continue;
      }
      rowIndex[0] = LpTypes.INVALID_ROW;
      double candidateCoeff = restrictedInfinityNorm(candidate, hasZeroCoefficient, rowIndex);
      final double kBixbyHighThreshold = 0.99;
      if (candidateCoeff > kBixbyHighThreshold) {
        enterBasis = true;
      } else if (isDominated(candidate, scaledDiagonalAbs)) {
        candidateCoeff = restrictedInfinityNorm(candidate, canBeReplaced, rowIndex);
        if (candidateCoeff != 0.0) {
          enterBasis = true;
        }
      }
      if (enterBasis) {
        final int candidateRow = rowIndex[0];
        canBeReplaced[candidateRow] = false;
        for (int i = compactMatrix.columnStart(candidate); i < compactMatrix
            .columnStart(candidate + 1); ++i) {
          if (compactMatrix.entryCoefficient(i) != 0.0) {
            hasZeroCoefficient[compactMatrix.entryRow(i)] = false;
          }
        }
        final double kBixbyLowThreshold = 0.01;
        scaledDiagonalAbs[candidateRow] = kBixbyLowThreshold * Math.abs(candidateCoeff);
        basis[candidateRow] = candidate;
      }
    }
  }

  private boolean isDominated(int col, double[] radius) {
    for (int i = compactMatrix.columnStart(col); i < compactMatrix.columnStart(col + 1); ++i) {
      if (Math.abs(compactMatrix.entryCoefficient(i)) > radius[compactMatrix.entryRow(i)]) {
        return false;
      }
    }
    return true;
  }

  /** Maros' crash for the primal simplex. */
  public void getPrimalMarosBasis(int numCols, int[] basis) {
    getMarosBasis(false, numCols, basis);
  }

  /** Maros' crash for the dual simplex (only zero cost columns). */
  public void getDualMarosBasis(int numCols, int[] basis) {
    getMarosBasis(true, numCols, basis);
  }

  /** Triangular completion for the primal simplex. */
  public void completeTriangularPrimalBasis(int numCols, int[] basis) {
    completeTriangularBasis(false, numCols, basis);
  }

  /** Triangular completion for the dual simplex (only zero cost columns). */
  public void completeTriangularDualBasis(int numCols, int[] basis) {
    completeTriangularBasis(true, numCols, basis);
  }

  private void completeTriangularBasis(boolean onlyAllowZeroCostColumn, int numCols,
      int[] basis) {
    final int numRows = compactMatrix.numRows();
    final boolean[] canBeReplaced = new boolean[numRows];
    for (int row = 0; row < numRows; ++row) {
      if (basis[row] == LpTypes.INVALID_COL) {
        canBeReplaced[row] = true;
      }
    }
    final MatrixNonZeroPattern residualPattern = new MatrixNonZeroPattern();
    residualPattern.reset(numRows, numCols);
    for (int col = 0; col < numCols; ++col) {
      if (onlyAllowZeroCostColumn && objective[col] != 0.0) {
        continue;
      }
      for (int i = compactMatrix.columnStart(col); i < compactMatrix.columnStart(col + 1); ++i) {
        final int row = compactMatrix.entryRow(i);
        if (canBeReplaced[row]) {
          residualPattern.addEntry(row, col);
        }
      }
    }
    final List<Integer> residualSingletonColumn = new ArrayList<>();
    maxScaledAbsCost = 0.0;
    for (int col = 0; col < numCols; ++col) {
      maxScaledAbsCost = Math.max(maxScaledAbsCost, Math.abs(objective[col]));
      if (residualPattern.colDegree(col) == 1) {
        residualSingletonColumn.add(col);
      }
    }
    final double kBixbyWeight = 1000.0;
    maxScaledAbsCost = (maxScaledAbsCost == 0.0) ? 1.0 : kBixbyWeight * maxScaledAbsCost;
    final Comparator<Integer> comparator = (a, b) -> {
      if (a.intValue() == b.intValue()) {
        return 0;
      }
      final int categoryA = getColumnCategory(a);
      final int categoryB = getColumnCategory(b);
      if (categoryA != categoryB) {
        return Integer.compare(categoryA, categoryB);
      }
      final int entriesA = compactMatrix.columnNumEntries(a);
      final int entriesB = compactMatrix.columnNumEntries(b);
      if (entriesA != entriesB) {
        return Integer.compare(entriesA, entriesB);
      }
      final double penaltyA = getColumnPenalty(a);
      final double penaltyB = getColumnPenalty(b);
      if (penaltyA != penaltyB) {
        return Double.compare(penaltyA, penaltyB);
      }
      // When everything but the column is equal, do not depend on the order of the priority queue
      // implementation: the smallest column comes first.
      return Integer.compare(a, b);
    };
    final PriorityQueue<Integer> queue =
        new PriorityQueue<>(Math.max(1, residualSingletonColumn.size()), comparator);
    queue.addAll(residualSingletonColumn);
    while (!queue.isEmpty()) {
      final int candidate = queue.poll();
      if (residualPattern.colDegree(candidate) != 1) {
        continue;
      }
      int row = LpTypes.INVALID_ROW;
      double coeff = 0.0;
      double maxMagnitude = 0.0;
      for (int i = compactMatrix.columnStart(candidate); i < compactMatrix
          .columnStart(candidate + 1); ++i) {
        maxMagnitude = Math.max(maxMagnitude, Math.abs(compactMatrix.entryCoefficient(i)));
        if (canBeReplaced[compactMatrix.entryRow(i)]) {
          row = compactMatrix.entryRow(i);
          coeff = compactMatrix.entryCoefficient(i);
          break;
        }
      }
      final double kStabilityThreshold = 0.01;
      if (Math.abs(coeff) < kStabilityThreshold * maxMagnitude) {
        continue;
      }
      basis[row] = candidate;
      canBeReplaced[row] = false;
      residualPattern.deleteRowAndColumn(row, candidate);
      for (int col : residualPattern.rowNonZero(row)) {
        if (col == candidate) {
          continue;
        }
        residualPattern.decreaseColDegree(col);
        if (residualPattern.colDegree(col) == 1) {
          queue.add(col);
        }
      }
    }
  }

  private int getMarosPriority(int col) {
    switch (variableType[col]) {
      case UNCONSTRAINED:
        return 3;
      case LOWER_BOUNDED:
      case UPPER_BOUNDED:
        return 2;
      case UPPER_AND_LOWER_BOUNDED:
        return 1;
      case FIXED_VARIABLE:
      default:
        return 0;
    }
  }

  private int getMarosRowPriority(int row) {
    final int slackIndex = row + compactMatrix.numCols() - compactMatrix.numRows();
    return getMarosPriority(slackIndex);
  }

  private void getMarosBasis(boolean onlyAllowZeroCostColumn, int numCols, int[] basis) {
    final int numRows = compactMatrix.numRows();
    final int firstSlack = numCols - numRows;
    for (int row = 0; row < numRows; row++) {
      basis[row] = firstSlack + row;
    }
    final boolean[] available = new boolean[numCols];
    Arrays.fill(available, true);
    for (int col = 0; col < firstSlack; ++col) {
      if (variableType[col] == VariableType.FIXED_VARIABLE
          || (onlyAllowZeroCostColumn && objective[col] != 0.0)) {
        available[col] = false;
      }
    }
    for (int col = firstSlack; col < numCols; ++col) {
      if (variableType[col] == VariableType.UNCONSTRAINED) {
        available[col] = false;
      }
    }
    final MatrixNonZeroPattern residualPattern = new MatrixNonZeroPattern();
    residualPattern.reset(numRows, numCols);
    for (int col = 0; col < firstSlack; ++col) {
      for (int i = compactMatrix.columnStart(col); i < compactMatrix.columnStart(col + 1); ++i) {
        final int row = compactMatrix.entryRow(i);
        if (available[row] && available[col]) {
          residualPattern.addEntry(row, col);
        }
      }
    }
    for (int row = 0; row < numRows; row++) {
      if (residualPattern.rowDegree(row) == 0) {
        available[row + firstSlack] = false;
      }
    }
    for (;;) {
      int maxRowPriorityFunction = Integer.MIN_VALUE;
      int maxRpfRow = LpTypes.INVALID_ROW;
      for (int row = 0; row < numRows; row++) {
        if (available[row + firstSlack]) {
          final int rpf = 10 * (3 - getMarosRowPriority(row)) - residualPattern.rowDegree(row);
          if (rpf > maxRowPriorityFunction) {
            maxRowPriorityFunction = rpf;
            maxRpfRow = row;
          }
        }
      }
      if (maxRpfRow == LpTypes.INVALID_ROW) {
        break;
      }
      final double kStabilityThreshold = 1e-3;
      int maxCpfCol = LpTypes.INVALID_COL;
      int maxColPriorityFunction = Integer.MIN_VALUE;
      for (int col : residualPattern.rowNonZero(maxRpfRow)) {
        if (!available[col]) {
          continue;
        }
        final int cpf = 10 * getMarosPriority(col) - residualPattern.colDegree(col);
        if (cpf > maxColPriorityFunction) {
          double maxMagnitude = 0;
          double pivotAbsoluteValue = 0.0;
          for (int i = compactMatrix.columnStart(col); i < compactMatrix.columnStart(col + 1);
              ++i) {
            final double absoluteValue = Math.abs(compactMatrix.entryCoefficient(i));
            if (compactMatrix.entryRow(i) == maxRpfRow) {
              pivotAbsoluteValue = absoluteValue;
            }
            maxMagnitude = Math.max(maxMagnitude, absoluteValue);
          }
          if (pivotAbsoluteValue >= kStabilityThreshold * maxMagnitude) {
            maxColPriorityFunction = cpf;
            maxCpfCol = col;
          }
        }
      }
      if (maxCpfCol == LpTypes.INVALID_COL) {
        available[maxRpfRow + firstSlack] = false;
        continue;
      }
      final int rowPriority = getMarosRowPriority(maxRpfRow);
      final int columnPriority = getMarosPriority(maxCpfCol);
      if (rowPriority >= columnPriority) {
        available[maxRpfRow + firstSlack] = false;
        continue;
      }
      basis[maxRpfRow] = maxCpfCol;
      available[maxCpfCol] = false;
      available[firstSlack + maxRpfRow] = false;
      residualPattern.deleteRowAndColumn(maxRpfRow, maxCpfCol);
      for (int col : residualPattern.rowNonZero(maxRpfRow)) {
        available[col] = false;
      }
    }
  }

  /** The candidate columns for Bixby's crash, sorted by preference. */
  public List<Integer> computeCandidates(int numCols) {
    final List<Integer> candidates = new ArrayList<>();
    maxScaledAbsCost = 0.0;
    for (int col = 0; col < numCols; ++col) {
      if (variableType[col] != VariableType.FIXED_VARIABLE
          && compactMatrix.columnNumEntries(col) > 0) {
        candidates.add(col);
        maxScaledAbsCost = Math.max(maxScaledAbsCost, Math.abs(objective[col]));
      }
    }
    final double kBixbyWeight = 1000.0;
    maxScaledAbsCost = (maxScaledAbsCost == 0.0) ? 1.0 : kBixbyWeight * maxScaledAbsCost;
    candidates.sort((a, b) -> {
      if (a.intValue() == b.intValue()) {
        return 0;
      }
      final int categoryA = getColumnCategory(a);
      final int categoryB = getColumnCategory(b);
      if (categoryA != categoryB) {
        return Integer.compare(categoryA, categoryB);
      }
      return Double.compare(getColumnPenalty(a), getColumnPenalty(b));
    });
    return candidates;
  }

  private int getColumnCategory(int col) {
    switch (variableType[col]) {
      case UNCONSTRAINED:
        return 2;
      case LOWER_BOUNDED:
      case UPPER_BOUNDED:
        return 3;
      case UPPER_AND_LOWER_BOUNDED:
        return 4;
      case FIXED_VARIABLE:
      default:
        return 5;
    }
  }

  private double getColumnPenalty(int col) {
    final VariableType type = variableType[col];
    double penalty = 0.0;
    if (type == VariableType.LOWER_BOUNDED) {
      penalty = lowerBound[col];
    }
    if (type == VariableType.UPPER_BOUNDED) {
      penalty = -upperBound[col];
    }
    if (type == VariableType.UPPER_AND_LOWER_BOUNDED) {
      penalty = lowerBound[col] - upperBound[col];
    }
    return penalty + Math.abs(objective[col]) / maxScaledAbsCost;
  }
}
