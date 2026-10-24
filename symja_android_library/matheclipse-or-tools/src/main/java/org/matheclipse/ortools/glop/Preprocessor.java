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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.ortools.lpdata.ConstraintStatus;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ProblemSolution;
import org.matheclipse.ortools.lpdata.SparseColumn;
import org.matheclipse.ortools.lpdata.VariableStatus;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * Base class of the LP presolve steps. <code>run()</code> transforms a linear program and returns
 * true if a postsolve is needed; <code>recoverSolution()</code> transforms a solution of the
 * transformed problem into a solution of the problem given to <code>run()</code>. A preprocessor
 * may detect the problem status (infeasible, unbounded...).
 */
public abstract class Preprocessor {

  /** The status detected by the preprocessor, INIT if nothing was detected. */
  protected ProblemStatus status = ProblemStatus.INIT;

  /** The parameters. */
  protected final GlopParameters parameters;

  /** Whether integer variables must stay integer. */
  protected boolean inMipContext;

  /** The time limit. */
  protected TimeLimit timeLimit = TimeLimit.infinite();

  /** Creates the preprocessor with the given parameters. */
  protected Preprocessor(GlopParameters parameters) {
    this.parameters = parameters;
  }

  /** Transforms the problem; returns true if a postsolve is needed. */
  public abstract boolean run(LinearProgram lp);

  /** Transforms back a solution of the transformed problem. */
  public abstract void recoverSolution(ProblemSolution solution);

  /** The detected status. */
  public ProblemStatus status() {
    return status;
  }

  /** Sets whether integer variables must stay integer. */
  public void useInMipContext() {
    inMipContext = true;
  }

  /** Sets the time limit. */
  public void setTimeLimit(TimeLimit timeLimit) {
    this.timeLimit = timeLimit;
  }

  /** a &lt;= b up to the solution feasibility tolerance. */
  protected boolean isSmallerWithinFeasibilityTolerance(double a, double b) {
    return LpUtils.isSmallerWithinTolerance(a, b, parameters.getSolutionFeasibilityTolerance());
  }

  /** a &lt;= b up to the preprocessor zero tolerance. */
  protected boolean isSmallerWithinPreprocessorZeroTolerance(double a, double b) {
    return LpUtils.isSmallerWithinTolerance(a, b, parameters.getPreprocessorZeroTolerance());
  }

  /** Subtracts multiple * column(col) from the constraint bounds and updates the offset. */
  static void subtractColumnMultipleFromConstraintBound(int col, double multiple,
      LinearProgram lp) {
    final double[] lbs = lp.constraintLowerBounds();
    final double[] ubs = lp.constraintUpperBounds();
    final SparseColumn c = lp.getSparseColumn(col);
    for (int i = 0; i < c.numEntries(); ++i) {
      final int row = c.entryRow(i);
      final double delta = multiple * c.entryCoefficient(i);
      lbs[row] -= delta;
      ubs[row] -= delta;
    }
    lp.setObjectiveOffset(lp.objectiveOffset() + lp.objectiveCoefficients()[col] * multiple);
  }

  /**
   * The status of a variable from its value: FIXED_VALUE, AT_LOWER_BOUND or AT_UPPER_BOUND for a
   * value at a bound, FREE otherwise.
   */
  static VariableStatus computeVariableStatus(double value, double lowerBound,
      double upperBound) {
    if (lowerBound == upperBound) {
      return VariableStatus.FIXED_VALUE;
    }
    if (value == lowerBound) {
      return VariableStatus.AT_LOWER_BOUND;
    }
    if (value == upperBound) {
      return VariableStatus.AT_UPPER_BOUND;
    }
    return VariableStatus.FREE;
  }

  /** Records deleted columns with their value and status, and restores them. */
  static final class ColumnDeletionHelper {
    private boolean[] isColumnDeleted = new boolean[0];
    private double[] storedValue = new double[0];
    private VariableStatus[] storedStatus = new VariableStatus[0];

    void clear() {
      isColumnDeleted = new boolean[0];
      storedValue = new double[0];
      storedStatus = new VariableStatus[0];
    }

    void markColumnForDeletion(int col) {
      markColumnForDeletionWithState(col, 0.0, VariableStatus.FREE);
    }

    void markColumnForDeletionWithState(int col, double fixedValue, VariableStatus status) {
      if (col >= isColumnDeleted.length) {
        final int old = isColumnDeleted.length;
        isColumnDeleted = Arrays.copyOf(isColumnDeleted, col + 1);
        storedValue = Arrays.copyOf(storedValue, col + 1);
        storedStatus = Arrays.copyOf(storedStatus, col + 1);
        for (int i = old; i <= col; ++i) {
          storedStatus[i] = VariableStatus.FREE;
        }
      }
      isColumnDeleted[col] = true;
      storedValue[col] = fixedValue;
      storedStatus[col] = status;
    }

    boolean isColumnMarked(int col) {
      return col < isColumnDeleted.length && isColumnDeleted[col];
    }

    boolean isEmpty() {
      return isColumnDeleted.length == 0;
    }

    boolean[] getMarkedColumns() {
      return isColumnDeleted;
    }

    void restoreDeletedColumns(ProblemSolution solution) {
      final int numCols = solution.primalValues.length;
      final List<Double> newPrimalValues = new ArrayList<>();
      final List<VariableStatus> newVariableStatuses = new ArrayList<>();
      int oldIndex = 0;
      for (int col = 0; col < isColumnDeleted.length; ++col) {
        if (isColumnDeleted[col]) {
          newPrimalValues.add(storedValue[col]);
          newVariableStatuses.add(storedStatus[col]);
        } else {
          newPrimalValues.add(solution.primalValues[oldIndex]);
          newVariableStatuses.add(solution.variableStatuses[oldIndex]);
          ++oldIndex;
        }
      }
      for (; oldIndex < numCols; ++oldIndex) {
        newPrimalValues.add(solution.primalValues[oldIndex]);
        newVariableStatuses.add(solution.variableStatuses[oldIndex]);
      }
      solution.primalValues = new double[newPrimalValues.size()];
      solution.variableStatuses = new VariableStatus[newPrimalValues.size()];
      for (int i = 0; i < newPrimalValues.size(); ++i) {
        solution.primalValues[i] = newPrimalValues.get(i);
        solution.variableStatuses[i] = newVariableStatuses.get(i);
      }
    }
  }

  /** Records deleted rows and restores them (dual value 0, BASIC status). */
  static final class RowDeletionHelper {
    private boolean[] isRowDeleted = new boolean[0];

    void clear() {
      isRowDeleted = new boolean[0];
    }

    void markRowForDeletion(int row) {
      if (row >= isRowDeleted.length) {
        isRowDeleted = Arrays.copyOf(isRowDeleted, row + 1);
      }
      isRowDeleted[row] = true;
    }

    void unmarkRow(int row) {
      if (row >= isRowDeleted.length) {
        return;
      }
      isRowDeleted[row] = false;
    }

    boolean isRowMarked(int row) {
      return row < isRowDeleted.length && isRowDeleted[row];
    }

    boolean isEmpty() {
      return isRowDeleted.length == 0;
    }

    boolean[] getMarkedRows() {
      return isRowDeleted;
    }

    void restoreDeletedRows(ProblemSolution solution) {
      final int numRows = solution.dualValues.length;
      final List<Double> newDualValues = new ArrayList<>();
      final List<ConstraintStatus> newConstraintStatuses = new ArrayList<>();
      int oldIndex = 0;
      for (int row = 0; row < isRowDeleted.length; ++row) {
        if (isRowDeleted[row]) {
          newDualValues.add(0.0);
          newConstraintStatuses.add(ConstraintStatus.BASIC);
        } else {
          newDualValues.add(solution.dualValues[oldIndex]);
          newConstraintStatuses.add(solution.constraintStatuses[oldIndex]);
          ++oldIndex;
        }
      }
      for (; oldIndex < numRows; ++oldIndex) {
        newDualValues.add(solution.dualValues[oldIndex]);
        newConstraintStatuses.add(solution.constraintStatuses[oldIndex]);
      }
      solution.dualValues = new double[newDualValues.size()];
      solution.constraintStatuses = new ConstraintStatus[newDualValues.size()];
      for (int i = 0; i < newDualValues.size(); ++i) {
        solution.dualValues[i] = newDualValues.get(i);
        solution.constraintStatuses[i] = newConstraintStatuses.get(i);
      }
    }
  }

  /** Saves copies of columns (or rows as columns of the transpose). */
  static final class ColumnsSaver {
    private final Map<Integer, SparseColumn> saved = new HashMap<>();
    private final SparseColumn emptyColumn = new SparseColumn();

    void saveColumn(int col, SparseColumn column) {
      saved.put(col, new SparseColumn(column));
    }

    void saveColumnIfNotAlreadyDone(int col, SparseColumn column) {
      if (!saved.containsKey(col)) {
        saved.put(col, new SparseColumn(column));
      }
    }

    SparseColumn savedColumn(int col) {
      return saved.get(col);
    }

    SparseColumn savedOrEmptyColumn(int col) {
      final SparseColumn c = saved.get(col);
      return c == null ? emptyColumn : c;
    }
  }

  /** Returns the value of smallest magnitude, or 0 if it is infinite. */
  static double minInMagnitudeOrZeroIfInfinite(double a, double b) {
    final double value = Math.abs(a) < Math.abs(b) ? a : b;
    return LpTypes.isFinite(value) ? value : 0.0;
  }
}
