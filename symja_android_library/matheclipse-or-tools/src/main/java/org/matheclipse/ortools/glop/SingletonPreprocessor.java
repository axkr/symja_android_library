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
// ortools/glop/preprocessor.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.ortools.lpdata.ConstraintStatus;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ProblemSolution;
import org.matheclipse.ortools.lpdata.SparseColumn;
import org.matheclipse.ortools.lpdata.SparseMatrix;
import org.matheclipse.ortools.lpdata.VariableStatus;

/**
 * Removes the singleton rows (they become variable bounds), the zero cost singleton columns (the
 * variable absorbs the constraint) and the singleton columns of an equality (the variable is
 * substituted in the objective), and makes a constraint an equality when its singleton variable
 * is implied free. The undo stack restores the primal and dual values and the statuses.
 */
public final class SingletonPreprocessor extends Preprocessor {

  /** A matrix entry. */
  static final class MatrixEntry {
    final int row;
    final int col;
    final double coeff;

    MatrixEntry(int row, int col, double coeff) {
      this.row = row;
      this.col = col;
      this.coeff = coeff;
    }
  }

  /** The operations undone during the postsolve. */
  enum OperationType {
    SINGLETON_ROW, ZERO_COST_SINGLETON_COLUMN, SINGLETON_COLUMN_IN_EQUALITY,
    MAKE_CONSTRAINT_AN_EQUALITY
  }

  /** The information needed to undo one operation. */
  static final class SingletonUndo {
    private final OperationType type;
    private final boolean isMaximization;
    private final MatrixEntry e;
    private final double cost;
    private final double variableLowerBound;
    private final double variableUpperBound;
    private final double constraintLowerBound;
    private final double constraintUpperBound;
    private final ConstraintStatus constraintStatus;

    SingletonUndo(OperationType type, LinearProgram lp, MatrixEntry e, ConstraintStatus status) {
      this.type = type;
      this.isMaximization = lp.isMaximizationProblem();
      this.e = e;
      this.cost = lp.objectiveCoefficients()[e.col];
      this.variableLowerBound = lp.variableLowerBounds()[e.col];
      this.variableUpperBound = lp.variableUpperBounds()[e.col];
      this.constraintLowerBound = lp.constraintLowerBounds()[e.row];
      this.constraintUpperBound = lp.constraintUpperBounds()[e.row];
      this.constraintStatus = status;
    }

    MatrixEntry entry() {
      return e;
    }

    void undo(GlopParameters parameters, SparseColumn savedColumn, SparseColumn savedRow,
        ProblemSolution solution) {
      switch (type) {
        case SINGLETON_ROW:
          singletonRowUndo(savedColumn, solution);
          break;
        case ZERO_COST_SINGLETON_COLUMN:
          zeroCostSingletonColumnUndo(parameters, savedRow, solution);
          break;
        case SINGLETON_COLUMN_IN_EQUALITY:
          singletonColumnInEqualityUndo(parameters, savedRow, solution);
          break;
        case MAKE_CONSTRAINT_AN_EQUALITY:
        default:
          makeConstraintAnEqualityUndo(solution);
          break;
      }
    }

    private void singletonRowUndo(SparseColumn savedColumn, ProblemSolution solution) {
      final VariableStatus status = solution.variableStatuses[e.col];
      if (status == VariableStatus.BASIC || status == VariableStatus.FREE) {
        return;
      }
      double impliedLowerBound = constraintLowerBound / e.coeff;
      double impliedUpperBound = constraintUpperBound / e.coeff;
      if (e.coeff < 0.0) {
        final double t = impliedLowerBound;
        impliedLowerBound = impliedUpperBound;
        impliedUpperBound = t;
      }
      final boolean lowerBoundChanged = impliedLowerBound > variableLowerBound;
      final boolean upperBoundChanged = impliedUpperBound < variableUpperBound;
      if (!lowerBoundChanged && !upperBoundChanged) {
        return;
      }
      if (status == VariableStatus.AT_LOWER_BOUND && !lowerBoundChanged) {
        return;
      }
      if (status == VariableStatus.AT_UPPER_BOUND && !upperBoundChanged) {
        return;
      }
      final double reducedCost = cost - savedColumn.scalarProduct(solution.dualValues);
      final double reducedCostForMinimization = isMaximization ? -reducedCost : reducedCost;
      if (status == VariableStatus.FIXED_VALUE) {
        if (reducedCostForMinimization >= 0.0 && !lowerBoundChanged) {
          solution.variableStatuses[e.col] = VariableStatus.AT_LOWER_BOUND;
          return;
        }
        if (reducedCostForMinimization <= 0.0 && !upperBoundChanged) {
          solution.variableStatuses[e.col] = VariableStatus.AT_UPPER_BOUND;
          return;
        }
      }
      solution.dualValues[e.row] = reducedCost / e.coeff;
      ConstraintStatus newConstraintStatus = LpTypes.variableToConstraintStatus(status);
      if (status == VariableStatus.FIXED_VALUE && (!lowerBoundChanged || !upperBoundChanged)) {
        newConstraintStatus =
            lowerBoundChanged ? ConstraintStatus.AT_LOWER_BOUND : ConstraintStatus.AT_UPPER_BOUND;
      }
      if (e.coeff < 0.0) {
        if (newConstraintStatus == ConstraintStatus.AT_LOWER_BOUND) {
          newConstraintStatus = ConstraintStatus.AT_UPPER_BOUND;
        } else if (newConstraintStatus == ConstraintStatus.AT_UPPER_BOUND) {
          newConstraintStatus = ConstraintStatus.AT_LOWER_BOUND;
        }
      }
      solution.variableStatuses[e.col] = VariableStatus.BASIC;
      solution.constraintStatuses[e.row] = newConstraintStatus;
    }

    private void zeroCostSingletonColumnUndo(GlopParameters parameters, SparseColumn savedRow,
        ProblemSolution solution) {
      if (variableUpperBound == variableLowerBound) {
        solution.primalValues[e.col] = variableLowerBound;
        solution.variableStatuses[e.col] = VariableStatus.FIXED_VALUE;
        return;
      }
      final ConstraintStatus ctStatus = solution.constraintStatuses[e.row];
      if (ctStatus == ConstraintStatus.FIXED_VALUE) {
        final double correctedDual =
            isMaximization ? -solution.dualValues[e.row] : solution.dualValues[e.row];
        if (correctedDual > 0) {
          solution.primalValues[e.col] = variableLowerBound;
          solution.variableStatuses[e.col] = VariableStatus.AT_LOWER_BOUND;
        } else {
          solution.primalValues[e.col] = variableUpperBound;
          solution.variableStatuses[e.col] = VariableStatus.AT_UPPER_BOUND;
        }
        return;
      } else if (ctStatus == ConstraintStatus.AT_LOWER_BOUND
          || ctStatus == ConstraintStatus.AT_UPPER_BOUND) {
        if ((ctStatus == ConstraintStatus.AT_UPPER_BOUND && e.coeff > 0.0)
            || (ctStatus == ConstraintStatus.AT_LOWER_BOUND && e.coeff < 0.0)) {
          solution.primalValues[e.col] = variableLowerBound;
          solution.variableStatuses[e.col] = VariableStatus.AT_LOWER_BOUND;
        } else {
          solution.primalValues[e.col] = variableUpperBound;
          solution.variableStatuses[e.col] = VariableStatus.AT_UPPER_BOUND;
        }
        if (constraintUpperBound == constraintLowerBound) {
          solution.constraintStatuses[e.row] = ConstraintStatus.FIXED_VALUE;
        }
        return;
      }
      final double activity = savedRow.scalarProduct(solution.primalValues);
      final double tolerance = parameters.getPreprocessorZeroTolerance();
      if (variableLowerBound != -LpTypes.INFINITY) {
        final double activityAtLb = activity + e.coeff * variableLowerBound;
        if (LpUtils.isSmallerWithinTolerance(constraintLowerBound, activityAtLb, tolerance)
            && LpUtils.isSmallerWithinTolerance(activityAtLb, constraintUpperBound, tolerance)) {
          solution.primalValues[e.col] = variableLowerBound;
          solution.variableStatuses[e.col] = VariableStatus.AT_LOWER_BOUND;
          return;
        }
      }
      if (variableUpperBound != LpTypes.INFINITY) {
        final double activityAtUb = activity + e.coeff * variableUpperBound;
        if (LpUtils.isSmallerWithinTolerance(constraintLowerBound, activityAtUb, tolerance)
            && LpUtils.isSmallerWithinTolerance(activityAtUb, constraintUpperBound, tolerance)) {
          solution.primalValues[e.col] = variableUpperBound;
          solution.variableStatuses[e.col] = VariableStatus.AT_UPPER_BOUND;
          return;
        }
      }
      if (constraintLowerBound == -LpTypes.INFINITY && constraintUpperBound == LpTypes.INFINITY) {
        solution.primalValues[e.col] = 0.0;
        solution.variableStatuses[e.col] = VariableStatus.FREE;
        return;
      }
      solution.variableStatuses[e.col] = VariableStatus.BASIC;
      if (constraintLowerBound == constraintUpperBound) {
        solution.primalValues[e.col] = (constraintLowerBound - activity) / e.coeff;
        solution.constraintStatuses[e.row] = ConstraintStatus.FIXED_VALUE;
        return;
      }
      boolean setConstraintToLowerBound;
      if (constraintLowerBound == -LpTypes.INFINITY) {
        setConstraintToLowerBound = false;
      } else if (constraintUpperBound == LpTypes.INFINITY) {
        setConstraintToLowerBound = true;
      } else {
        final double toLb = (constraintLowerBound - activity) / e.coeff;
        final double toUb = (constraintUpperBound - activity) / e.coeff;
        setConstraintToLowerBound =
            Math.max(variableLowerBound - toLb, toLb - variableUpperBound) < Math
                .max(variableLowerBound - toUb, toUb - variableUpperBound);
      }
      if (setConstraintToLowerBound) {
        solution.primalValues[e.col] = (constraintLowerBound - activity) / e.coeff;
        solution.constraintStatuses[e.row] = ConstraintStatus.AT_LOWER_BOUND;
      } else {
        solution.primalValues[e.col] = (constraintUpperBound - activity) / e.coeff;
        solution.constraintStatuses[e.row] = ConstraintStatus.AT_UPPER_BOUND;
      }
    }

    private void singletonColumnInEqualityUndo(GlopParameters parameters,
        SparseColumn savedRow, ProblemSolution solution) {
      zeroCostSingletonColumnUndo(parameters, savedRow, solution);
      solution.dualValues[e.row] += cost / e.coeff;
      if (solution.constraintStatuses[e.row] == ConstraintStatus.BASIC) {
        solution.variableStatuses[e.col] = VariableStatus.BASIC;
        solution.constraintStatuses[e.row] = ConstraintStatus.FIXED_VALUE;
      }
    }

    private void makeConstraintAnEqualityUndo(ProblemSolution solution) {
      if (solution.constraintStatuses[e.row] == ConstraintStatus.FIXED_VALUE) {
        solution.constraintStatuses[e.row] = constraintStatus;
      }
    }
  }

  private final ColumnDeletionHelper columnDeletionHelper = new ColumnDeletionHelper();
  private final RowDeletionHelper rowDeletionHelper = new RowDeletionHelper();
  private final List<SingletonUndo> undoStack = new ArrayList<>();
  private final ColumnsSaver columnsSaver = new ColumnsSaver();
  private final ColumnsSaver rowsSaver = new ColumnsSaver();
  private boolean[] rowSumIsCached = new boolean[0];
  private LpUtils.SumWithOneMissing[] rowLbSum = new LpUtils.SumWithOneMissing[0];
  private LpUtils.SumWithOneMissing[] rowUbSum = new LpUtils.SumWithOneMissing[0];

  /** Creates the preprocessor. */
  public SingletonPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @SuppressFBWarnings(value = "FE_FLOATING_POINT_EQUALITY",
      justification = "exact comparisons of bounds, as in C++")
  private void deleteSingletonRow(MatrixEntry e, LinearProgram lp) {
    double impliedLowerBound = lp.constraintLowerBounds()[e.row] / e.coeff;
    double impliedUpperBound = lp.constraintUpperBounds()[e.row] / e.coeff;
    if (e.coeff < 0.0) {
      final double t = impliedLowerBound;
      impliedLowerBound = impliedUpperBound;
      impliedUpperBound = t;
    }
    final double oldLowerBound = lp.variableLowerBounds()[e.col];
    final double oldUpperBound = lp.variableUpperBounds()[e.col];
    final double potentialError = Math.abs(parameters.getPreprocessorZeroTolerance() / e.coeff);
    double newLowerBound =
        impliedLowerBound - potentialError > oldLowerBound ? impliedLowerBound : oldLowerBound;
    double newUpperBound =
        impliedUpperBound + potentialError < oldUpperBound ? impliedUpperBound : oldUpperBound;
    if (newUpperBound == -LpTypes.INFINITY || newLowerBound == LpTypes.INFINITY) {
      status = ProblemStatus.PRIMAL_INFEASIBLE;
      return;
    }
    if (newUpperBound < newLowerBound) {
      if (!isSmallerWithinFeasibilityTolerance(newLowerBound, newUpperBound)) {
        status = ProblemStatus.PRIMAL_INFEASIBLE;
        return;
      }
      if (newLowerBound == lp.variableLowerBounds()[e.col]) {
        newUpperBound = newLowerBound;
      }
      if (newUpperBound == lp.variableUpperBounds()[e.col]) {
        newLowerBound = newUpperBound;
      }
      newUpperBound = newLowerBound;
    }
    rowDeletionHelper.markRowForDeletion(e.row);
    undoStack.add(new SingletonUndo(OperationType.SINGLETON_ROW, lp, e, ConstraintStatus.FREE));
    columnsSaver.saveColumnIfNotAlreadyDone(e.col, lp.getSparseColumn(e.col));
    lp.setVariableBounds(e.col, newLowerBound, newUpperBound);
  }

  private void updateConstraintBoundsWithVariableBounds(MatrixEntry e, LinearProgram lp) {
    double lowerDelta = -e.coeff * lp.variableUpperBounds()[e.col];
    double upperDelta = -e.coeff * lp.variableLowerBounds()[e.col];
    if (e.coeff < 0.0) {
      final double t = lowerDelta;
      lowerDelta = upperDelta;
      upperDelta = t;
    }
    lp.setConstraintBounds(e.row, lp.constraintLowerBounds()[e.row] + lowerDelta,
        lp.constraintUpperBounds()[e.row] + upperDelta);
  }

  private boolean integerSingletonColumnIsRemovable(MatrixEntry matrixEntry, LinearProgram lp) {
    final SparseMatrix transpose = lp.getTransposeSparseMatrix();
    final SparseColumn rowAsCol = transpose.column(matrixEntry.row);
    final double tolerance = parameters.getSolutionFeasibilityTolerance();
    for (int i = 0; i < rowAsCol.numEntries(); ++i) {
      if (!lp.isVariableInteger(rowAsCol.entryRow(i))) {
        return false;
      }
      final double coefficientRatio = rowAsCol.entryCoefficient(i) / matrixEntry.coeff;
      if (!LpUtils.isIntegerWithinTolerance(coefficientRatio, tolerance)) {
        return false;
      }
    }
    final double constraintLb = lp.constraintLowerBounds()[matrixEntry.row];
    if (LpTypes.isFinite(constraintLb)) {
      if (!LpUtils.isIntegerWithinTolerance(constraintLb / matrixEntry.coeff, tolerance)) {
        return false;
      }
    }
    final double constraintUb = lp.constraintUpperBounds()[matrixEntry.row];
    if (LpTypes.isFinite(constraintUb)) {
      if (!LpUtils.isIntegerWithinTolerance(constraintUb / matrixEntry.coeff, tolerance)) {
        return false;
      }
    }
    return true;
  }

  private void deleteZeroCostSingletonColumn(SparseMatrix transpose, MatrixEntry e,
      LinearProgram lp) {
    undoStack.add(
        new SingletonUndo(OperationType.ZERO_COST_SINGLETON_COLUMN, lp, e, ConstraintStatus.FREE));
    rowsSaver.saveColumnIfNotAlreadyDone(e.row, transpose.column(e.row));
    updateConstraintBoundsWithVariableBounds(e, lp);
    columnDeletionHelper.markColumnForDeletion(e.col);
  }

  private void deleteSingletonColumnInEquality(SparseMatrix transpose, MatrixEntry e,
      LinearProgram lp) {
    final SparseColumn rowAsColumn = transpose.column(e.row);
    undoStack.add(new SingletonUndo(OperationType.SINGLETON_COLUMN_IN_EQUALITY, lp, e,
        ConstraintStatus.FREE));
    rowsSaver.saveColumnIfNotAlreadyDone(e.row, rowAsColumn);
    final double rhs = lp.constraintUpperBounds()[e.row];
    final double cost = lp.objectiveCoefficients()[e.col];
    final double multiplier = cost / e.coeff;
    lp.setObjectiveOffset(lp.objectiveOffset() + rhs * multiplier);
    for (int i = 0; i < rowAsColumn.numEntries(); ++i) {
      final int col = rowAsColumn.entryRow(i);
      if (!columnDeletionHelper.isColumnMarked(col)) {
        double newCost =
            lp.objectiveCoefficients()[col] - rowAsColumn.entryCoefficient(i) * multiplier;
        if (Math.abs(newCost) < parameters.getPreprocessorZeroTolerance()) {
          newCost = 0.0;
        }
        lp.setObjectiveCoefficient(col, newCost);
      }
    }
    updateConstraintBoundsWithVariableBounds(e, lp);
    columnDeletionHelper.markColumnForDeletion(e.col);
  }

  private boolean makeConstraintAnEqualityIfPossible(SparseMatrix transpose, MatrixEntry e,
      LinearProgram lp) {
    final double cstLowerBound = lp.constraintLowerBounds()[e.row];
    final double cstUpperBound = lp.constraintUpperBounds()[e.row];
    if (cstLowerBound == cstUpperBound) {
      return true;
    }
    if (cstLowerBound == -LpTypes.INFINITY && cstUpperBound == LpTypes.INFINITY) {
      return false;
    }
    if (inMipContext && lp.isVariableInteger(e.col)) {
      return false;
    }
    final double[] variableUbs = lp.variableUpperBounds();
    final double[] variableLbs = lp.variableLowerBounds();
    if (e.row >= rowSumIsCached.length || !rowSumIsCached[e.row]) {
      if (e.row >= rowSumIsCached.length) {
        final int newSize = e.row + 1;
        final int old = rowSumIsCached.length;
        rowSumIsCached = java.util.Arrays.copyOf(rowSumIsCached, newSize);
        rowLbSum = java.util.Arrays.copyOf(rowLbSum, newSize);
        rowUbSum = java.util.Arrays.copyOf(rowUbSum, newSize);
        for (int i = old; i < newSize; ++i) {
          rowLbSum[i] = new LpUtils.SumWithOneMissing(false);
          rowUbSum[i] = new LpUtils.SumWithOneMissing(true);
        }
      }
      rowSumIsCached[e.row] = true;
      rowLbSum[e.row].add(cstLowerBound);
      rowUbSum[e.row].add(cstUpperBound);
      final SparseColumn rowAsColumn = transpose.column(e.row);
      for (int i = 0; i < rowAsColumn.numEntries(); ++i) {
        final int rowAsCol = rowAsColumn.entryRow(i);
        if (columnDeletionHelper.isColumnMarked(rowAsCol)) {
          continue;
        }
        final double c = rowAsColumn.entryCoefficient(i);
        if (c > 0.0) {
          rowLbSum[e.row].add(-c * variableUbs[rowAsCol]);
          rowUbSum[e.row].add(-c * variableLbs[rowAsCol]);
        } else {
          rowLbSum[e.row].add(-c * variableLbs[rowAsCol]);
          rowUbSum[e.row].add(-c * variableUbs[rowAsCol]);
        }
      }
    }
    final double c = e.coeff;
    final double lb = c > 0.0 ? rowLbSum[e.row].sumWithoutLb(-c * variableUbs[e.col]) / c
        : rowUbSum[e.row].sumWithoutUb(-c * variableUbs[e.col]) / c;
    final double ub = c > 0.0 ? rowUbSum[e.row].sumWithoutUb(-c * variableLbs[e.col]) / c
        : rowLbSum[e.row].sumWithoutLb(-c * variableLbs[e.col]) / c;
    final double cost = lp.getObjectiveCoefficientForMinimizationVersion(e.col);
    ConstraintStatus relaxedStatus = ConstraintStatus.FIXED_VALUE;
    if (cost < 0.0 && isSmallerWithinPreprocessorZeroTolerance(ub,
        lp.variableUpperBounds()[e.col])) {
      if (e.coeff > 0) {
        if (cstUpperBound == LpTypes.INFINITY) {
          status = ProblemStatus.INFEASIBLE_OR_UNBOUNDED;
        } else {
          relaxedStatus = ConstraintStatus.AT_UPPER_BOUND;
          lp.setConstraintBounds(e.row, cstUpperBound, cstUpperBound);
        }
      } else {
        if (cstLowerBound == -LpTypes.INFINITY) {
          status = ProblemStatus.INFEASIBLE_OR_UNBOUNDED;
        } else {
          relaxedStatus = ConstraintStatus.AT_LOWER_BOUND;
          lp.setConstraintBounds(e.row, cstLowerBound, cstLowerBound);
        }
      }
      if (status == ProblemStatus.INFEASIBLE_OR_UNBOUNDED) {
        return false;
      }
      lp.setVariableBounds(e.col, lp.variableLowerBounds()[e.col], LpTypes.INFINITY);
    }
    if (cost > 0.0 && isSmallerWithinPreprocessorZeroTolerance(lp.variableLowerBounds()[e.col],
        lb)) {
      if (e.coeff > 0) {
        if (cstLowerBound == -LpTypes.INFINITY) {
          status = ProblemStatus.INFEASIBLE_OR_UNBOUNDED;
        } else {
          relaxedStatus = ConstraintStatus.AT_LOWER_BOUND;
          lp.setConstraintBounds(e.row, cstLowerBound, cstLowerBound);
        }
      } else {
        if (cstUpperBound == LpTypes.INFINITY) {
          status = ProblemStatus.INFEASIBLE_OR_UNBOUNDED;
        } else {
          relaxedStatus = ConstraintStatus.AT_UPPER_BOUND;
          lp.setConstraintBounds(e.row, cstUpperBound, cstUpperBound);
        }
      }
      if (status == ProblemStatus.INFEASIBLE_OR_UNBOUNDED) {
        return false;
      }
      lp.setVariableBounds(e.col, -LpTypes.INFINITY, lp.variableUpperBounds()[e.col]);
    }
    if (lp.constraintLowerBounds()[e.row] == lp.constraintUpperBounds()[e.row]) {
      undoStack.add(
          new SingletonUndo(OperationType.MAKE_CONSTRAINT_AN_EQUALITY, lp, e, relaxedStatus));
      return true;
    }
    return false;
  }

  @Override
  public boolean run(LinearProgram lp) {
    if (!parameters.getUseSingletonPreprocessor()) {
      return false;
    }
    final SparseMatrix matrix = lp.getSparseMatrix();
    final SparseMatrix transpose = lp.getTransposeSparseMatrix();
    final int numCols = matrix.numCols();
    final int numRows = matrix.numRows();
    final int[] columnDegree = new int[numCols];
    final List<Integer> columnToProcess = new ArrayList<>();
    for (int col = 0; col < numCols; ++col) {
      columnDegree[col] = matrix.column(col).numEntries();
      if (columnDegree[col] == 1) {
        columnToProcess.add(col);
      }
    }
    final int[] rowDegree = new int[numRows];
    final List<Integer> rowToProcess = new ArrayList<>();
    for (int row = 0; row < numRows; ++row) {
      rowDegree[row] = transpose.column(row).numEntries();
      if (rowDegree[row] == 1) {
        rowToProcess.add(row);
      }
    }
    while (status == ProblemStatus.INIT
        && (!columnToProcess.isEmpty() || !rowToProcess.isEmpty())) {
      while (status == ProblemStatus.INIT && !columnToProcess.isEmpty()) {
        final int col = columnToProcess.remove(columnToProcess.size() - 1);
        if (columnDegree[col] <= 0) {
          continue;
        }
        final MatrixEntry e = getSingletonColumnMatrixEntry(col, matrix);
        if (inMipContext && lp.isVariableInteger(e.col)
            && !integerSingletonColumnIsRemovable(e, lp)) {
          continue;
        }
        if (lp.objectiveCoefficients()[col] == 0.0) {
          deleteZeroCostSingletonColumn(transpose, e, lp);
        } else {
          if (Math.abs(e.coeff) < parameters.getPreprocessorZeroTolerance()) {
            continue;
          }
          if (makeConstraintAnEqualityIfPossible(transpose, e, lp)) {
            deleteSingletonColumnInEquality(transpose, e, lp);
          } else {
            continue;
          }
        }
        --rowDegree[e.row];
        if (rowDegree[e.row] == 1) {
          rowToProcess.add(e.row);
        }
      }
      while (status == ProblemStatus.INIT && !rowToProcess.isEmpty()) {
        final int row = rowToProcess.remove(rowToProcess.size() - 1);
        if (rowDegree[row] <= 0) {
          continue;
        }
        final MatrixEntry e = getSingletonRowMatrixEntry(row, transpose);
        if (inMipContext && lp.isVariableInteger(e.col)
            && !integerSingletonColumnIsRemovable(e, lp)) {
          continue;
        }
        deleteSingletonRow(e, lp);
        --columnDegree[e.col];
        if (columnDegree[e.col] == 1) {
          columnToProcess.add(e.col);
        }
      }
    }
    if (status != ProblemStatus.INIT) {
      return false;
    }
    lp.deleteColumns(columnDeletionHelper.getMarkedColumns());
    lp.deleteRows(rowDeletionHelper.getMarkedRows());
    return !columnDeletionHelper.isEmpty() || !rowDeletionHelper.isEmpty();
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    columnDeletionHelper.restoreDeletedColumns(solution);
    rowDeletionHelper.restoreDeletedRows(solution);
    for (int i = undoStack.size() - 1; i >= 0; --i) {
      final SingletonUndo undo = undoStack.get(i);
      final SparseColumn savedCol = columnsSaver.savedOrEmptyColumn(undo.entry().col);
      final SparseColumn savedRow = rowsSaver.savedOrEmptyColumn(undo.entry().row);
      undo.undo(parameters, savedCol, savedRow, solution);
    }
  }

  private MatrixEntry getSingletonColumnMatrixEntry(int col, SparseMatrix matrix) {
    final SparseColumn c = matrix.column(col);
    for (int i = 0; i < c.numEntries(); ++i) {
      if (!rowDeletionHelper.isRowMarked(c.entryRow(i))) {
        return new MatrixEntry(c.entryRow(i), col, c.entryCoefficient(i));
      }
    }
    status = ProblemStatus.ABNORMAL;
    return new MatrixEntry(0, 0, 0.0);
  }

  private MatrixEntry getSingletonRowMatrixEntry(int row, SparseMatrix transpose) {
    final SparseColumn c = transpose.column(row);
    for (int i = 0; i < c.numEntries(); ++i) {
      final int col = c.entryRow(i);
      if (!columnDeletionHelper.isColumnMarked(col)) {
        return new MatrixEntry(row, col, c.entryCoefficient(i));
      }
    }
    status = ProblemStatus.ABNORMAL;
    return new MatrixEntry(0, 0, 0.0);
  }
}
