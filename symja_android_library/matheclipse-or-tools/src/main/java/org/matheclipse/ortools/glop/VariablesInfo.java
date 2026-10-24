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
// ortools/glop/variables_info.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Arrays;
import java.util.BitSet;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.VariableStatus;
import org.matheclipse.ortools.lpdata.VariableType;

/**
 * Maintains the information of each variable that depends on its bounds and status: its type, its
 * status, whether it can increase or decrease, whether it is relevant for the pricing, and the set
 * of basic, non-basic and non-basic boxed variables.
 */
public final class VariablesInfo {

  private final CompactSparseMatrix matrix;

  private double[] lowerBounds = new double[0];
  private double[] upperBounds = new double[0];
  private double[] savedLowerBounds = new double[0];
  private double[] savedUpperBounds = new double[0];

  private VariableStatus[] variableStatus = new VariableStatus[0];
  private VariableType[] variableType = new VariableType[0];

  private BitSet canIncrease = new BitSet();
  private BitSet canDecrease = new BitSet();
  private BitSet relevance = new BitSet();
  private BitSet isBasic = new BitSet();
  private BitSet notBasic = new BitSet();
  private BitSet nonBasicBoxedVariables = new BitSet();

  private long numEntriesInRelevantColumns;
  private boolean boxedVariablesAreRelevant = true;
  private boolean inDualPhaseOne = false;

  /** Creates the object for the given matrix (with slacks). */
  public VariablesInfo(CompactSparseMatrix matrix) {
    this.matrix = matrix;
  }

  /** Loads the bounds of an LP in equation form; returns true if they did not change. */
  public boolean loadBoundsAndReturnTrueIfUnchanged(double[] newLowerBounds,
      double[] newUpperBounds) {
    final int numCols = matrix.numCols();
    if (Arrays.equals(lowerBounds, newLowerBounds) && Arrays.equals(upperBounds, newUpperBounds)) {
      return true;
    }
    lowerBounds = newLowerBounds.clone();
    upperBounds = newUpperBounds.clone();
    variableType = Arrays.copyOf(variableType, numCols);
    for (int col = 0; col < numCols; ++col) {
      variableType[col] = computeVariableType(col);
    }
    return false;
  }

  /** Recomputes the types after the bounds were changed through the mutable accessors. */
  public void initializeFromMutatedState() {
    final int numCols = matrix.numCols();
    variableType = Arrays.copyOf(variableType, numCols);
    for (int col = 0; col < numCols; ++col) {
      variableType[col] = computeVariableType(col);
    }
  }

  /**
   * Loads the bounds of an LP not in equation form: the slack of row r gets the bounds
   * [-ub(r), -lb(r)]. Returns true if nothing changed.
   */
  public boolean loadBoundsAndReturnTrueIfUnchanged(double[] varLowerBounds,
      double[] varUpperBounds, double[] constraintLowerBounds, double[] constraintUpperBounds) {
    final int numCols = matrix.numCols();
    final int numVariables = varUpperBounds.length;
    final int numRows = constraintLowerBounds.length;
    boolean isUnchanged = (numCols == lowerBounds.length);
    final int oldSize = lowerBounds.length;
    lowerBounds = Arrays.copyOf(lowerBounds, numCols);
    upperBounds = Arrays.copyOf(upperBounds, numCols);
    variableType = Arrays.copyOf(variableType, numCols);
    for (int col = oldSize; col < numCols; ++col) {
      variableType[col] = VariableType.FIXED_VARIABLE;
    }
    for (int col = 0; col < numVariables; ++col) {
      if (lowerBounds[col] != varLowerBounds[col] || upperBounds[col] != varUpperBounds[col]) {
        lowerBounds[col] = varLowerBounds[col];
        upperBounds[col] = varUpperBounds[col];
        isUnchanged = false;
        variableType[col] = computeVariableType(col);
      }
    }
    for (int row = 0; row < numRows; ++row) {
      final int col = numVariables + row;
      if (lowerBounds[col] != -constraintUpperBounds[row]
          || upperBounds[col] != -constraintLowerBounds[row]) {
        lowerBounds[col] = -constraintUpperBounds[row];
        upperBounds[col] = -constraintLowerBounds[row];
        isUnchanged = false;
        variableType[col] = computeVariableType(col);
      }
    }
    return isUnchanged;
  }

  private void resetStatusInfo() {
    final int numCols = matrix.numCols();
    final int oldSize = variableStatus.length;
    variableStatus = Arrays.copyOf(variableStatus, numCols);
    for (int col = oldSize; col < numCols; ++col) {
      variableStatus[col] = VariableStatus.FREE;
    }
    canIncrease = new BitSet(numCols);
    canDecrease = new BitSet(numCols);
    isBasic = new BitSet(numCols);
    notBasic = new BitSet(numCols);
    nonBasicBoxedVariables = new BitSet(numCols);
    boxedVariablesAreRelevant = true;
    numEntriesInRelevantColumns = 0;
    relevance = new BitSet(numCols);
  }

  /**
   * Initializes the statuses from a warm-start state, correcting the statuses that are
   * incompatible with the current bounds and shifting the slack statuses if
   * <code>numNewCols</code> structural columns were added. A FREE status is replaced by the
   * default status.
   */
  public void initializeFromBasisState(int firstSlackCol, int numNewCols, BasisState state) {
    initializeFromBasisState(firstSlackCol, numNewCols, state, 0);
  }

  /**
   * Like {@link #initializeFromBasisState(int, int, BasisState)}, but a FREE status of a column
   * <code>col &lt; numFreeValues</code> (the columns with a starting value, see
   * {@link RevisedSimplex#setStartingVariableValuesForNextSolve(double[])}) is kept (FIXED_VALUE if
   * the bounds are equal).
   */
  public void initializeFromBasisState(int firstSlackCol, int numNewCols, BasisState state,
      int numFreeValues) {
    resetStatusInfo();
    final int numCols = lowerBounds.length;
    final int firstNewCol = firstSlackCol - numNewCols;
    final int stateSize = state.statuses.length;
    for (int col = 0; col < numCols; ++col) {
      VariableStatus status;
      if (col < firstNewCol && col < stateSize) {
        status = state.statuses[col];
      } else if (col >= firstSlackCol && col - numNewCols < stateSize) {
        status = state.statuses[col - numNewCols];
      } else {
        updateToNonBasicStatus(col, defaultVariableStatus(col));
        continue;
      }
      switch (status) {
        case BASIC:
          variableStatus[col] = VariableStatus.BASIC;
          isBasic.set(col, true);
          break;
        case AT_LOWER_BOUND:
          if (lowerBounds[col] == upperBounds[col]) {
            updateToNonBasicStatus(col, VariableStatus.FIXED_VALUE);
          } else {
            updateToNonBasicStatus(col,
                lowerBounds[col] == -LpTypes.INFINITY ? defaultVariableStatus(col) : status);
          }
          break;
        case AT_UPPER_BOUND:
          if (lowerBounds[col] == upperBounds[col]) {
            updateToNonBasicStatus(col, VariableStatus.FIXED_VALUE);
          } else {
            updateToNonBasicStatus(col,
                upperBounds[col] == LpTypes.INFINITY ? defaultVariableStatus(col) : status);
          }
          break;
        case FREE:
          if (lowerBounds[col] == upperBounds[col]) {
            updateToNonBasicStatus(col, VariableStatus.FIXED_VALUE);
          } else {
            updateToNonBasicStatus(col, col < numFreeValues ? status : defaultVariableStatus(col));
          }
          break;
        default:
          updateToNonBasicStatus(col, defaultVariableStatus(col));
      }
    }
  }

  /**
   * Changes to FREE (FIXED_VALUE for fixed variables) every BASIC variable not in
   * <code>basis</code> and makes the basis columns basic. Returns the number of such variables.
   */
  public int changeUnusedBasicVariablesToFree(int[] basis) {
    final int numCols = lowerBounds.length;
    isBasic = new BitSet(numCols);
    for (int col : basis) {
      updateToBasicStatus(col);
    }
    int numNoLongerInBasis = 0;
    for (int col = 0; col < numCols; ++col) {
      if (!isBasic.get(col) && variableStatus[col] == VariableStatus.BASIC) {
        ++numNoLongerInBasis;
        if (variableType[col] == VariableType.FIXED_VARIABLE) {
          updateToNonBasicStatus(col, VariableStatus.FIXED_VALUE);
        } else {
          updateToNonBasicStatus(col, VariableStatus.FREE);
        }
      }
    }
    return numNoLongerInBasis;
  }

  /**
   * Moves the FREE variables with bounds to their closest bound when the starting value is within
   * <code>distance</code> of it. Returns the number of changes.
   */
  public int snapFreeVariablesToBound(double distance, double[] startingValues) {
    int numChanges = 0;
    final int numCols = lowerBounds.length;
    for (int col = 0; col < numCols; ++col) {
      if (variableStatus[col] != VariableStatus.FREE) {
        continue;
      }
      if (variableType[col] == VariableType.UNCONSTRAINED) {
        continue;
      }
      final double value = col < startingValues.length ? startingValues[col] : 0.0;
      final double diffUb = upperBounds[col] - value;
      final double diffLb = value - lowerBounds[col];
      if (diffLb <= diffUb) {
        if (diffLb <= distance) {
          ++numChanges;
          updateToNonBasicStatus(col, VariableStatus.AT_LOWER_BOUND);
        }
      } else {
        if (diffUb <= distance) {
          ++numChanges;
          updateToNonBasicStatus(col, VariableStatus.AT_UPPER_BOUND);
        }
      }
    }
    return numChanges;
  }

  /** Sets all the variables to their default non-basic status. */
  public void initializeToDefaultStatus() {
    resetStatusInfo();
    final int numCols = lowerBounds.length;
    for (int col = 0; col < numCols; ++col) {
      updateToNonBasicStatus(col, defaultVariableStatus(col));
    }
  }

  private VariableStatus defaultVariableStatus(int col) {
    if (lowerBounds[col] == upperBounds[col]) {
      return VariableStatus.FIXED_VALUE;
    }
    if (lowerBounds[col] == -LpTypes.INFINITY && upperBounds[col] == LpTypes.INFINITY) {
      return VariableStatus.FREE;
    }
    return Math.abs(lowerBounds[col]) <= Math.abs(upperBounds[col]) ? VariableStatus.AT_LOWER_BOUND
        : VariableStatus.AT_UPPER_BOUND;
  }

  /** Makes the non-basic boxed variables relevant or not. */
  public void makeBoxedVariableRelevant(boolean value) {
    if (value == boxedVariablesAreRelevant) {
      return;
    }
    boxedVariablesAreRelevant = value;
    if (value) {
      for (int col = nonBasicBoxedVariables.nextSetBit(0); col >= 0;
          col = nonBasicBoxedVariables.nextSetBit(col + 1)) {
        setRelevance(col, variableType[col] != VariableType.FIXED_VARIABLE);
      }
    } else {
      for (int col = nonBasicBoxedVariables.nextSetBit(0); col >= 0;
          col = nonBasicBoxedVariables.nextSetBit(col + 1)) {
        setRelevance(col, false);
      }
    }
  }

  /** Marks a variable as basic. */
  public void updateToBasicStatus(int col) {
    if (inDualPhaseOne) {
      if (lowerBounds[col] != 0.0) {
        lowerBounds[col] = -LpTypes.INFINITY;
      }
      if (upperBounds[col] != 0.0) {
        upperBounds[col] = LpTypes.INFINITY;
      }
      variableType[col] = computeVariableType(col);
    }
    variableStatus[col] = VariableStatus.BASIC;
    isBasic.set(col, true);
    notBasic.set(col, false);
    canIncrease.set(col, false);
    canDecrease.set(col, false);
    nonBasicBoxedVariables.set(col, false);
    setRelevance(col, false);
  }

  /** Marks a variable as non-basic with the given status. */
  public void updateToNonBasicStatus(int col, VariableStatus status) {
    variableStatus[col] = status;
    isBasic.set(col, false);
    notBasic.set(col, true);
    canIncrease.set(col,
        status == VariableStatus.AT_LOWER_BOUND || status == VariableStatus.FREE);
    canDecrease.set(col,
        status == VariableStatus.AT_UPPER_BOUND || status == VariableStatus.FREE);
    final boolean boxed = variableType[col] == VariableType.UPPER_AND_LOWER_BOUNDED;
    nonBasicBoxedVariables.set(col, boxed);
    final boolean rel =
        status != VariableStatus.FIXED_VALUE && (boxedVariablesAreRelevant || !boxed);
    setRelevance(col, rel);
  }

  /** The variable types. */
  public VariableType[] getTypeRow() {
    return variableType;
  }

  /** The variable statuses. */
  public VariableStatus[] getStatusRow() {
    return variableStatus;
  }

  /** The non-basic variables that can increase without increasing the infeasibility. */
  public BitSet getCanIncreaseBitRow() {
    return canIncrease;
  }

  /** The non-basic variables that can decrease without increasing the infeasibility. */
  public BitSet getCanDecreaseBitRow() {
    return canDecrease;
  }

  /** The variables considered by the pricing. */
  public BitSet getIsRelevantBitRow() {
    return relevance;
  }

  /** The basic variables. */
  public BitSet getIsBasicBitRow() {
    return isBasic;
  }

  /** The non-basic variables. */
  public BitSet getNotBasicBitRow() {
    return notBasic;
  }

  /** The non-basic boxed variables. */
  public BitSet getNonBasicBoxedVariables() {
    return nonBasicBoxedVariables;
  }

  /** The variable lower bounds. */
  public double[] getVariableLowerBounds() {
    return lowerBounds;
  }

  /** The variable upper bounds. */
  public double[] getVariableUpperBounds() {
    return upperBounds;
  }

  /** The number of columns. */
  public int getNumberOfColumns() {
    return matrix.numCols();
  }

  /** The number of entries of the relevant columns. */
  public long getNumEntriesInRelevantColumns() {
    return numEntriesInRelevantColumns;
  }

  /** upper - lower bound of a column. */
  public double getBoundDifference(int col) {
    return upperBounds[col] - lowerBounds[col];
  }

  /** The bounds for modification; call {@link #initializeFromMutatedState()} afterwards. */
  public double[] mutableLowerBounds() {
    return lowerBounds;
  }

  /** The bounds for modification; call {@link #initializeFromMutatedState()} afterwards. */
  public double[] mutableUpperBounds() {
    return upperBounds;
  }

  /** Replaces the bound arrays (advanced incremental use). */
  public void setBounds(double[] lower, double[] upper) {
    this.lowerBounds = lower;
    this.upperBounds = upper;
  }

  private VariableType computeVariableType(int col) {
    if (lowerBounds[col] == -LpTypes.INFINITY) {
      if (upperBounds[col] == LpTypes.INFINITY) {
        return VariableType.UNCONSTRAINED;
      }
      return VariableType.UPPER_BOUNDED;
    } else if (upperBounds[col] == LpTypes.INFINITY) {
      return VariableType.LOWER_BOUNDED;
    } else if (lowerBounds[col] == upperBounds[col]) {
      return VariableType.FIXED_VARIABLE;
    } else {
      return VariableType.UPPER_AND_LOWER_BOUNDED;
    }
  }

  private void setRelevance(int col, boolean rel) {
    if (relevance.get(col) == rel) {
      return;
    }
    if (rel) {
      relevance.set(col);
      numEntriesInRelevantColumns += matrix.columnNumEntries(col);
    } else {
      relevance.clear(col);
      numEntriesInRelevantColumns -= matrix.columnNumEntries(col);
    }
  }

  private void updateStatusForNewType(int col) {
    switch (variableStatus[col]) {
      case BASIC:
        updateToBasicStatus(col);
        break;
      case AT_LOWER_BOUND:
        if (lowerBounds[col] == upperBounds[col]) {
          updateToNonBasicStatus(col, VariableStatus.FIXED_VALUE);
        } else if (lowerBounds[col] == -LpTypes.INFINITY) {
          updateToNonBasicStatus(col, defaultVariableStatus(col));
        } else {
          updateToNonBasicStatus(col, variableStatus[col]);
        }
        break;
      case AT_UPPER_BOUND:
        if (lowerBounds[col] == upperBounds[col]) {
          updateToNonBasicStatus(col, VariableStatus.FIXED_VALUE);
        } else if (upperBounds[col] == LpTypes.INFINITY) {
          updateToNonBasicStatus(col, defaultVariableStatus(col));
        } else {
          updateToNonBasicStatus(col, variableStatus[col]);
        }
        break;
      default:
        updateToNonBasicStatus(col, defaultVariableStatus(col));
    }
  }

  /**
   * Transforms the bounds for the dual phase I of Koberstein and Suhl: boxed and fixed variables
   * get [0, 0], lower bounded ones [0, 1], upper bounded ones [-1, 0] and free ones [-1000, 1000].
   */
  public void transformToDualPhaseIProblem(double dualFeasibilityTolerance,
      double[] reducedCosts) {
    inDualPhaseOne = true;
    savedLowerBounds = lowerBounds.clone();
    savedUpperBounds = upperBounds.clone();
    final int numCols = matrix.numCols();
    for (int col = 0; col < numCols; ++col) {
      switch (variableType[col]) {
        case FIXED_VARIABLE:
        case UPPER_AND_LOWER_BOUNDED:
          lowerBounds[col] = 0.0;
          upperBounds[col] = 0.0;
          variableType[col] = VariableType.FIXED_VARIABLE;
          break;
        case LOWER_BOUNDED:
          lowerBounds[col] = 0.0;
          upperBounds[col] = 1.0;
          variableType[col] = VariableType.UPPER_AND_LOWER_BOUNDED;
          break;
        case UPPER_BOUNDED:
          lowerBounds[col] = -1.0;
          upperBounds[col] = 0.0;
          variableType[col] = VariableType.UPPER_AND_LOWER_BOUNDED;
          break;
        case UNCONSTRAINED:
          lowerBounds[col] = -1000.0;
          upperBounds[col] = 1000.0;
          variableType[col] = VariableType.UPPER_AND_LOWER_BOUNDED;
          break;
        default:
          break;
      }
      if (variableType[col] == VariableType.UPPER_AND_LOWER_BOUNDED) {
        if (reducedCosts[col] > dualFeasibilityTolerance) {
          variableStatus[col] = VariableStatus.AT_LOWER_BOUND;
        } else if (reducedCosts[col] < -dualFeasibilityTolerance) {
          variableStatus[col] = VariableStatus.AT_UPPER_BOUND;
        }
      }
      updateStatusForNewType(col);
    }
  }

  /** Restores the bounds saved by {@link #transformToDualPhaseIProblem(double, double[])}. */
  public void endDualPhaseI(double dualFeasibilityTolerance, double[] reducedCosts) {
    inDualPhaseOne = false;
    lowerBounds = savedLowerBounds;
    upperBounds = savedUpperBounds;
    savedLowerBounds = new double[0];
    savedUpperBounds = new double[0];
    final int numCols = matrix.numCols();
    for (int col = 0; col < numCols; ++col) {
      variableType[col] = computeVariableType(col);
      if (variableType[col] == VariableType.UPPER_AND_LOWER_BOUNDED) {
        if (reducedCosts[col] > dualFeasibilityTolerance) {
          variableStatus[col] = VariableStatus.AT_LOWER_BOUND;
        } else if (reducedCosts[col] < -dualFeasibilityTolerance) {
          variableStatus[col] = VariableStatus.AT_UPPER_BOUND;
        }
      }
      updateStatusForNewType(col);
    }
  }
}
