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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/lp_data/lp_data.h/.cc
// and lp_data_utils.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.lpdata;

import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.matheclipse.ortools.glop.GlopParameters;

/**
 * A linear program in the form
 *
 * <pre>
 * minimize (or maximize) c.x + offset
 * subject to  constraint_lower_bounds &lt;= A.x &lt;= constraint_upper_bounds
 *             variable_lower_bounds &lt;= x &lt;= variable_upper_bounds
 * </pre>
 *
 * The matrix A is stored by columns. Bounds may be infinite. The objective scaling factor is only
 * used to report objective values in the original units after the preprocessors changed the
 * problem.
 */
public final class LinearProgram {

  /** The integrality of a variable. */
  public enum VariableType {
    /** A continuous variable. */
    CONTINUOUS,
    /** An integer variable. */
    INTEGER,
    /** A variable that takes integer values in any solution with integer variables at integers. */
    IMPLIED_INTEGER
  }

  private final SparseMatrix matrix = new SparseMatrix();
  private final SparseMatrix transposeMatrix = new SparseMatrix();

  private double[] constraintLowerBounds = new double[0];
  private double[] constraintUpperBounds = new double[0];
  private final List<String> constraintNames = new ArrayList<>();
  private int numRowsAllocated;

  private double[] objectiveCoefficients = new double[0];
  private double[] variableLowerBounds = new double[0];
  private double[] variableUpperBounds = new double[0];
  private final List<String> variableNames = new ArrayList<>();
  private final List<VariableType> variableTypes = new ArrayList<>();
  private int numColsAllocated;

  private final List<Integer> integerVariablesList = new ArrayList<>();
  private final Map<String, Integer> variableTable = new HashMap<>();
  private final Map<String, Integer> constraintTable = new HashMap<>();

  private double objectiveOffset;
  private double objectiveScalingFactor;
  private boolean maximize;
  private boolean columnsAreKnownToBeClean;
  private boolean transposeMatrixIsConsistent;
  private boolean integerVariablesListIsConsistent;
  private String name;
  private int firstSlackVariable;

  /** Creates an empty minimization problem. */
  public LinearProgram() {
    clear();
  }

  /** Removes everything. */
  public void clear() {
    matrix.clear();
    transposeMatrix.clear();
    constraintLowerBounds = new double[0];
    constraintUpperBounds = new double[0];
    constraintNames.clear();
    numRowsAllocated = 0;
    objectiveCoefficients = new double[0];
    variableLowerBounds = new double[0];
    variableUpperBounds = new double[0];
    variableNames.clear();
    variableTypes.clear();
    numColsAllocated = 0;
    integerVariablesList.clear();
    constraintTable.clear();
    variableTable.clear();
    maximize = false;
    objectiveOffset = 0.0;
    objectiveScalingFactor = 1.0;
    columnsAreKnownToBeClean = true;
    transposeMatrixIsConsistent = true;
    integerVariablesListIsConsistent = true;
    name = "";
    firstSlackVariable = LpTypes.INVALID_COL;
  }

  // --- storage helpers: the arrays may be over-allocated while the problem is built ---

  private void growCols(int n) {
    if (objectiveCoefficients.length < n) {
      final int size = Math.max(n, 2 * objectiveCoefficients.length);
      objectiveCoefficients = Arrays.copyOf(objectiveCoefficients, size);
      variableLowerBounds = Arrays.copyOf(variableLowerBounds, size);
      variableUpperBounds = Arrays.copyOf(variableUpperBounds, size);
    }
  }

  private void trimCols() {
    final int n = numVariables();
    if (objectiveCoefficients.length != n) {
      objectiveCoefficients = Arrays.copyOf(objectiveCoefficients, n);
      variableLowerBounds = Arrays.copyOf(variableLowerBounds, n);
      variableUpperBounds = Arrays.copyOf(variableUpperBounds, n);
    }
  }

  private void growRows(int n) {
    if (constraintLowerBounds.length < n) {
      final int size = Math.max(n, 2 * constraintLowerBounds.length);
      constraintLowerBounds = Arrays.copyOf(constraintLowerBounds, size);
      constraintUpperBounds = Arrays.copyOf(constraintUpperBounds, size);
    }
  }

  private void trimRows() {
    final int n = numRowsAllocated;
    if (constraintLowerBounds.length != n) {
      constraintLowerBounds = Arrays.copyOf(constraintLowerBounds, n);
      constraintUpperBounds = Arrays.copyOf(constraintUpperBounds, n);
    }
  }

  /** Sets the name of the problem. */
  public void setName(String name) {
    this.name = name == null ? "" : name;
  }

  /** The name of the problem. */
  public String name() {
    return name;
  }

  /** Creates a new variable with bounds [0, +inf) and returns its index. */
  public int createNewVariable() {
    final int col = numVariables();
    growCols(col + 1);
    objectiveCoefficients[col] = 0.0;
    variableLowerBounds[col] = 0.0;
    variableUpperBounds[col] = LpTypes.INFINITY;
    variableTypes.add(VariableType.CONTINUOUS);
    variableNames.add("");
    transposeMatrixIsConsistent = false;
    ++numColsAllocated;
    return matrix.appendEmptyColumn();
  }

  /** Creates a new slack variable with the given bounds and returns its index. */
  public int createNewSlackVariable(boolean isIntegerSlackVariable, double lowerBound,
      double upperBound, String name) {
    final int col = numVariables();
    growCols(col + 1);
    objectiveCoefficients[col] = 0.0;
    variableLowerBounds[col] = lowerBound;
    variableUpperBounds[col] = upperBound;
    variableTypes
        .add(isIntegerSlackVariable ? VariableType.IMPLIED_INTEGER : VariableType.CONTINUOUS);
    variableNames.add(name);
    transposeMatrixIsConsistent = false;
    ++numColsAllocated;
    return matrix.appendEmptyColumn();
  }

  /** Creates a new constraint with bounds [0, 0] and returns its index. */
  public int createNewConstraint() {
    final int row = constraintNames.size();
    matrix.setNumRows(row + 1);
    growRows(row + 1);
    constraintLowerBounds[row] = 0.0;
    constraintUpperBounds[row] = 0.0;
    constraintNames.add("");
    numRowsAllocated = row + 1;
    transposeMatrixIsConsistent = false;
    return row;
  }

  /** Returns the variable with the given id, creating it if needed. */
  public int findOrCreateVariable(String variableId) {
    Integer col = variableTable.get(variableId);
    if (col != null) {
      return col;
    }
    final int newCol = createNewVariable();
    variableNames.set(newCol, variableId);
    variableTable.put(variableId, newCol);
    return newCol;
  }

  /** Returns the constraint with the given id, creating it if needed. */
  public int findOrCreateConstraint(String constraintId) {
    Integer row = constraintTable.get(constraintId);
    if (row != null) {
      return row;
    }
    final int newRow = createNewConstraint();
    constraintNames.set(newRow, constraintId);
    constraintTable.put(constraintId, newRow);
    return newRow;
  }

  /** Sets the name of a variable. */
  public void setVariableName(int col, String name) {
    variableNames.set(col, name);
  }

  /** Sets the name of a constraint. */
  public void setConstraintName(int row, String name) {
    constraintNames.set(row, name);
  }

  /** Sets the integrality type of a variable. */
  public void setVariableType(int col, VariableType type) {
    final boolean wasInteger = isVariableInteger(col);
    variableTypes.set(col, type);
    if (isVariableInteger(col) != wasInteger) {
      integerVariablesListIsConsistent = false;
    }
  }

  /** Returns true if the variable is integer or implied integer. */
  public boolean isVariableInteger(int col) {
    final VariableType t = variableTypes.get(col);
    return t == VariableType.INTEGER || t == VariableType.IMPLIED_INTEGER;
  }

  /** Returns true if the variable is integer with bounds within [0, 1]. */
  public boolean isVariableBinary(int col) {
    return isVariableInteger(col) && variableLowerBounds[col] < LpTypes.EPSILON
        && variableLowerBounds[col] > -1.0 && variableUpperBounds[col] > 1.0 - LpTypes.EPSILON
        && variableUpperBounds[col] < 2;
  }

  /** Sets the bounds of a variable. */
  public void setVariableBounds(int col, double lowerBound, double upperBound) {
    variableLowerBounds[col] = lowerBound;
    variableUpperBounds[col] = upperBound;
  }

  /** Sets the bounds of a constraint, creating the constraints up to <code>row</code> if needed. */
  public void setConstraintBounds(int row, double lowerBound, double upperBound) {
    resizeRowsIfNeeded(row);
    constraintLowerBounds[row] = lowerBound;
    constraintUpperBounds[row] = upperBound;
  }

  /** Sets the coefficient A[row][col]; duplicates are resolved by {@link #cleanUp()}. */
  public void setCoefficient(int row, int col, double value) {
    resizeRowsIfNeeded(row);
    columnsAreKnownToBeClean = false;
    transposeMatrixIsConsistent = false;
    matrix.mutableColumn(col).setCoefficient(row, value);
  }

  /** Sets the objective coefficient of a variable. */
  public void setObjectiveCoefficient(int col, double value) {
    objectiveCoefficients[col] = value;
  }

  /** Sets the objective offset. */
  public void setObjectiveOffset(double objectiveOffset) {
    this.objectiveOffset = objectiveOffset;
  }

  /** Sets the objective scaling factor. */
  public void setObjectiveScalingFactor(double objectiveScalingFactor) {
    this.objectiveScalingFactor = objectiveScalingFactor;
  }

  /** Sets the optimization direction. */
  public void setMaximizationProblem(boolean maximize) {
    this.maximize = maximize;
  }

  /** Sorts the columns and removes duplicates and zeros. */
  public void cleanUp() {
    if (columnsAreKnownToBeClean) {
      return;
    }
    matrix.cleanUp();
    columnsAreKnownToBeClean = true;
    transposeMatrixIsConsistent = false;
  }

  /** Returns true if the matrix columns are cleaned up. */
  public boolean isCleanedUp() {
    if (columnsAreKnownToBeClean) {
      return true;
    }
    columnsAreKnownToBeClean = matrix.isCleanedUp();
    return columnsAreKnownToBeClean;
  }

  /** The name of a variable, <code>c&lt;index&gt;</code> if it has none. */
  public String getVariableName(int col) {
    return col >= variableNames.size() || variableNames.get(col).isEmpty() ? "c" + col
        : variableNames.get(col);
  }

  /** The name of a constraint, <code>r&lt;index&gt;</code> if it has none. */
  public String getConstraintName(int row) {
    return row >= constraintNames.size() || constraintNames.get(row).isEmpty() ? "r" + row
        : constraintNames.get(row);
  }

  /** The integrality type of a variable. */
  public VariableType getVariableType(int col) {
    return variableTypes.get(col);
  }

  /** Whether the objective is maximized. */
  public boolean isMaximizationProblem() {
    return maximize;
  }

  /** The constraint matrix. */
  public SparseMatrix getSparseMatrix() {
    return matrix;
  }

  /** The transpose of the constraint matrix, computed lazily. */
  public SparseMatrix getTransposeSparseMatrix() {
    if (!transposeMatrixIsConsistent) {
      transposeMatrix.populateFromTranspose(matrix);
      transposeMatrixIsConsistent = true;
    }
    return transposeMatrix;
  }

  /** Frees the transpose. */
  public void clearTransposeMatrix() {
    transposeMatrix.clear();
    transposeMatrixIsConsistent = false;
  }

  /** The column of a variable. */
  public SparseColumn getSparseColumn(int col) {
    return matrix.column(col);
  }

  /** The column of a variable for modification. */
  public SparseColumn getMutableSparseColumn(int col) {
    columnsAreKnownToBeClean = false;
    transposeMatrixIsConsistent = false;
    return matrix.mutableColumn(col);
  }

  /** The number of variables. */
  public int numVariables() {
    return matrix.numCols();
  }

  /** The number of constraints. */
  public int numConstraints() {
    return matrix.numRows();
  }

  /** The number of matrix entries. */
  public long numEntries() {
    return matrix.numEntries();
  }

  /** The constraint lower bounds, indexed by row (the returned array is live). */
  public double[] constraintLowerBounds() {
    trimRows();
    return constraintLowerBounds;
  }

  /** The constraint upper bounds, indexed by row (the returned array is live). */
  public double[] constraintUpperBounds() {
    trimRows();
    return constraintUpperBounds;
  }

  /** The objective coefficients, indexed by column (the returned array is live). */
  public double[] objectiveCoefficients() {
    trimCols();
    return objectiveCoefficients;
  }

  /** The variable lower bounds, indexed by column (the returned array is live). */
  public double[] variableLowerBounds() {
    trimCols();
    return variableLowerBounds;
  }

  /** The variable upper bounds, indexed by column (the returned array is live). */
  public double[] variableUpperBounds() {
    trimCols();
    return variableUpperBounds;
  }

  /** The list of the integer variables. */
  public List<Integer> integerVariablesList() {
    if (!integerVariablesListIsConsistent) {
      integerVariablesList.clear();
      for (int col = 0; col < numVariables(); ++col) {
        if (isVariableInteger(col)) {
          integerVariablesList.add(col);
        }
      }
      integerVariablesListIsConsistent = true;
    }
    return integerVariablesList;
  }

  /** The objective coefficient of a variable in the minimization version of the problem. */
  public double getObjectiveCoefficientForMinimizationVersion(int col) {
    return maximize ? -objectiveCoefficients[col] : objectiveCoefficients[col];
  }

  /** The objective offset. */
  public double objectiveOffset() {
    return objectiveOffset;
  }

  /** The objective scaling factor. */
  public double objectiveScalingFactor() {
    return objectiveScalingFactor;
  }

  /** Applies the scaling factor and offset to an internal objective value. */
  public double applyObjectiveScalingAndOffset(double value) {
    return objectiveScalingFactor * (value + objectiveOffset);
  }

  /** Inverse of {@link #applyObjectiveScalingAndOffset(double)}. */
  public double removeObjectiveScalingAndOffset(double value) {
    return value / objectiveScalingFactor - objectiveOffset;
  }

  /** A short description of the dimensions of the problem. */
  public String getDimensionString() {
    return String.format(Locale.ROOT, "%d rows, %d columns, %d entries", numConstraints(), numVariables(),
        numEntries());
  }

  /**
   * Adds a slack variable to each constraint which does not have one: the constraint becomes an
   * equality to zero and the slack takes the negated constraint bounds.
   */
  public void addSlackVariablesWhereNecessary(boolean detectIntegerConstraints) {
    cleanUp();
    final int numRows = numConstraints();
    final boolean[] hasIntegerSlackVariable = new boolean[numRows];
    Arrays.fill(hasIntegerSlackVariable, detectIntegerConstraints);
    if (detectIntegerConstraints) {
      for (int col = 0; col < numVariables(); ++col) {
        final SparseColumn column = matrix.column(col);
        final boolean isIntegerVariable = isVariableInteger(col);
        for (int i = 0; i < column.numEntries(); ++i) {
          final int row = column.entryRow(i);
          hasIntegerSlackVariable[row] = hasIntegerSlackVariable[row] && isIntegerVariable
              && LpUtils.round(column.entryCoefficient(i)) == column.entryCoefficient(i);
        }
      }
    }
    final int originalNumVariables = numVariables();
    trimRows();
    for (int row = 0; row < numRows; ++row) {
      final int slackVariableIndex = getSlackVariable(row);
      if (slackVariableIndex != LpTypes.INVALID_COL && slackVariableIndex < originalNumVariables) {
        continue;
      }
      final int slackCol = createNewSlackVariable(hasIntegerSlackVariable[row],
          -constraintUpperBounds[row], -constraintLowerBounds[row], "s" + row);
      setCoefficient(row, slackCol, 1.0);
      setConstraintBounds(row, 0.0, 0.0);
    }
    columnsAreKnownToBeClean = true;
    transposeMatrixIsConsistent = false;
    if (firstSlackVariable == LpTypes.INVALID_COL) {
      firstSlackVariable = originalNumVariables;
    }
  }

  /** The index of the first slack variable or {@link LpTypes#INVALID_COL}. */
  public int getFirstSlackVariable() {
    return firstSlackVariable;
  }

  /** The slack variable of a row or {@link LpTypes#INVALID_COL}. */
  public int getSlackVariable(int row) {
    if (firstSlackVariable == LpTypes.INVALID_COL) {
      return LpTypes.INVALID_COL;
    }
    return firstSlackVariable + row;
  }

  /** Replaces this problem by a deep copy of <code>lp</code>. */
  public void populateFromLinearProgram(LinearProgram lp) {
    matrix.populateFromSparseMatrix(lp.matrix);
    if (lp.transposeMatrixIsConsistent) {
      transposeMatrixIsConsistent = true;
      transposeMatrix.populateFromSparseMatrix(lp.transposeMatrix);
    } else {
      transposeMatrixIsConsistent = false;
      transposeMatrix.clear();
    }
    numRowsAllocated = lp.numRowsAllocated;
    constraintLowerBounds = Arrays.copyOf(lp.constraintLowerBounds, lp.numRowsAllocated);
    constraintUpperBounds = Arrays.copyOf(lp.constraintUpperBounds, lp.numRowsAllocated);
    constraintNames.clear();
    constraintNames.addAll(lp.constraintNames);
    constraintTable.clear();
    final int n = lp.numVariables();
    objectiveCoefficients = Arrays.copyOf(lp.objectiveCoefficients, n);
    variableLowerBounds = Arrays.copyOf(lp.variableLowerBounds, n);
    variableUpperBounds = Arrays.copyOf(lp.variableUpperBounds, n);
    variableNames.clear();
    variableNames.addAll(lp.variableNames);
    variableTypes.clear();
    variableTypes.addAll(lp.variableTypes);
    numColsAllocated = n;
    integerVariablesListIsConsistent = false;
    variableTable.clear();
    maximize = lp.maximize;
    objectiveOffset = lp.objectiveOffset;
    objectiveScalingFactor = lp.objectiveScalingFactor;
    columnsAreKnownToBeClean = lp.columnsAreKnownToBeClean;
    name = lp.name;
    firstSlackVariable = lp.firstSlackVariable;
  }

  /** Deletes the marked columns. */
  public void deleteColumns(boolean[] columnsToDelete) {
    if (columnsToDelete == null || columnsToDelete.length == 0) {
      return;
    }
    integerVariablesListIsConsistent = false;
    final int numCols = numVariables();
    trimCols();
    final int[] permutation = new int[numCols];
    int newIndex = 0;
    for (int col = 0; col < numCols; ++col) {
      permutation[col] = newIndex;
      if (col >= columnsToDelete.length || !columnsToDelete[col]) {
        objectiveCoefficients[newIndex] = objectiveCoefficients[col];
        variableLowerBounds[newIndex] = variableLowerBounds[col];
        variableUpperBounds[newIndex] = variableUpperBounds[col];
        variableNames.set(newIndex, variableNames.get(col));
        variableTypes.set(newIndex, variableTypes.get(col));
        ++newIndex;
      } else {
        permutation[col] = LpTypes.INVALID_COL;
      }
    }
    matrix.deleteColumns(columnsToDelete);
    objectiveCoefficients = Arrays.copyOf(objectiveCoefficients, newIndex);
    variableLowerBounds = Arrays.copyOf(variableLowerBounds, newIndex);
    variableUpperBounds = Arrays.copyOf(variableUpperBounds, newIndex);
    while (variableNames.size() > newIndex) {
      variableNames.remove(variableNames.size() - 1);
      variableTypes.remove(variableTypes.size() - 1);
    }
    numColsAllocated = newIndex;
    Iterator<Map.Entry<String, Integer>> it = variableTable.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Integer> e = it.next();
      final int col = e.getValue();
      if (col >= columnsToDelete.length || !columnsToDelete[col]) {
        e.setValue(permutation[col]);
      } else {
        it.remove();
      }
    }
    if (transposeMatrixIsConsistent) {
      transposeMatrix.deleteRows(newIndex, permutation);
    }
  }

  /** Removes the slack variables and restores the constraint bounds from their bounds. */
  public void deleteSlackVariables() {
    final boolean[] slackVariables = new boolean[matrix.numCols()];
    for (int slack = firstSlackVariable; slack < matrix.numCols(); ++slack) {
      final SparseColumn column = matrix.column(slack);
      final int row = column.entryRow(0);
      setConstraintBounds(row, -variableUpperBounds[slack], -variableLowerBounds[slack]);
      slackVariables[slack] = true;
    }
    deleteColumns(slackVariables);
    firstSlackVariable = LpTypes.INVALID_COL;
  }

  private static double[] updateMinAndMaxMagnitude(double[] range, int n, double[] minMax) {
    for (int i = 0; i < n; ++i) {
      final double magnitude = Math.abs(range[i]);
      if (magnitude == 0 || magnitude == LpTypes.INFINITY) {
        continue;
      }
      minMax[0] = Math.min(minMax[0], magnitude);
      minMax[1] = Math.max(minMax[1], magnitude);
    }
    return minMax;
  }

  private static double getMedianScalingFactor(double[] range) {
    List<Double> median = new ArrayList<>();
    for (double value : range) {
      if (value == 0.0) {
        continue;
      }
      median.add(Math.abs(value));
    }
    if (median.isEmpty()) {
      return 1.0;
    }
    median.sort(null);
    return median.get(median.size() / 2);
  }

  private static double getMeanScalingFactor(double[] range) {
    double mean = 0.0;
    int numNonZeros = 0;
    for (double value : range) {
      if (value == 0.0) {
        continue;
      }
      ++numNonZeros;
      mean += Math.abs(value);
    }
    if (numNonZeros == 0) {
      return 1.0;
    }
    return mean / numNonZeros;
  }

  private static double computeDivisorSoThatRangeContainsOne(double minMagnitude,
      double maxMagnitude) {
    if (minMagnitude > 1.0 && minMagnitude < LpTypes.INFINITY) {
      return minMagnitude;
    } else if (maxMagnitude > 0.0 && maxMagnitude < 1.0) {
      return maxMagnitude;
    }
    return 1.0;
  }

  /** Scales the objective with the given method and returns the cost scaling factor. */
  public double scaleObjective(GlopParameters.CostScalingAlgorithm method) {
    final double[] objective = objectiveCoefficients();
    final double[] minMax = updateMinAndMaxMagnitude(objective, objective.length,
        new double[] {LpTypes.INFINITY, 0.0});
    double costScalingFactor = 1.0;
    switch (method) {
      case NO_COST_SCALING:
        break;
      case CONTAIN_ONE_COST_SCALING:
        costScalingFactor = computeDivisorSoThatRangeContainsOne(minMax[0], minMax[1]);
        break;
      case MEAN_COST_SCALING:
        costScalingFactor = getMeanScalingFactor(objective);
        break;
      case MEDIAN_COST_SCALING:
        costScalingFactor = getMedianScalingFactor(objective);
        break;
      default:
        break;
    }
    if (costScalingFactor != 1.0) {
      for (int col = 0; col < numVariables(); ++col) {
        if (objective[col] == 0.0) {
          continue;
        }
        setObjectiveCoefficient(col, objective[col] / costScalingFactor);
      }
      setObjectiveScalingFactor(objectiveScalingFactor() * costScalingFactor);
      setObjectiveOffset(objectiveOffset() / costScalingFactor);
    }
    return costScalingFactor;
  }

  /** Divides all the bounds by a common factor and returns it. */
  public double scaleBounds() {
    final double[] minMax = new double[] {LpTypes.INFINITY, 0.0};
    final double[] vlb = variableLowerBounds();
    final double[] vub = variableUpperBounds();
    final double[] clb = constraintLowerBounds();
    final double[] cub = constraintUpperBounds();
    updateMinAndMaxMagnitude(vlb, vlb.length, minMax);
    updateMinAndMaxMagnitude(vub, vub.length, minMax);
    updateMinAndMaxMagnitude(clb, clb.length, minMax);
    updateMinAndMaxMagnitude(cub, cub.length, minMax);
    final double boundScalingFactor = computeDivisorSoThatRangeContainsOne(minMax[0], minMax[1]);
    if (boundScalingFactor != 1.0) {
      setObjectiveScalingFactor(objectiveScalingFactor() * boundScalingFactor);
      setObjectiveOffset(objectiveOffset() / boundScalingFactor);
      for (int col = 0; col < numVariables(); ++col) {
        setVariableBounds(col, vlb[col] / boundScalingFactor, vub[col] / boundScalingFactor);
      }
      for (int row = 0; row < numConstraints(); ++row) {
        setConstraintBounds(row, clb[row] / boundScalingFactor, cub[row] / boundScalingFactor);
      }
    }
    return boundScalingFactor;
  }

  /**
   * Scales the problem with <code>scaler</code>: A is replaced by R.A.C and the objective and bounds
   * are changed accordingly.
   */
  public void scale(SparseMatrixScaler scaler, GlopParameters.ScalingAlgorithm scalingMethod) {
    scaler.init(matrix);
    scaler.scale(scalingMethod);
    scaler.scaleRowVector(false, objectiveCoefficients());
    scaler.scaleRowVector(true, variableUpperBounds());
    scaler.scaleRowVector(true, variableLowerBounds());
    scaler.scaleColumnVector(false, constraintUpperBounds());
    scaler.scaleColumnVector(false, constraintLowerBounds());
    transposeMatrixIsConsistent = false;
  }

  /** Deletes the marked rows. */
  public void deleteRows(boolean[] rowsToDelete) {
    if (rowsToDelete == null || rowsToDelete.length == 0) {
      return;
    }
    final int numRows = numConstraints();
    trimRows();
    final int[] permutation = new int[numRows];
    int newIndex = 0;
    for (int row = 0; row < numRows; ++row) {
      if (row >= rowsToDelete.length || !rowsToDelete[row]) {
        constraintLowerBounds[newIndex] = constraintLowerBounds[row];
        constraintUpperBounds[newIndex] = constraintUpperBounds[row];
        constraintNames.set(newIndex, constraintNames.get(row));
        permutation[row] = newIndex;
        ++newIndex;
      } else {
        permutation[row] = LpTypes.INVALID_ROW;
      }
    }
    constraintLowerBounds = Arrays.copyOf(constraintLowerBounds, newIndex);
    constraintUpperBounds = Arrays.copyOf(constraintUpperBounds, newIndex);
    while (constraintNames.size() > newIndex) {
      constraintNames.remove(constraintNames.size() - 1);
    }
    numRowsAllocated = newIndex;
    matrix.deleteRows(newIndex, permutation);
    Iterator<Map.Entry<String, Integer>> it = constraintTable.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Integer> e = it.next();
      final int row = e.getValue();
      if (permutation[row] != LpTypes.INVALID_ROW) {
        e.setValue(permutation[row]);
      } else {
        it.remove();
      }
    }
    if (transposeMatrixIsConsistent) {
      transposeMatrix.deleteColumns(rowsToDelete);
    }
  }

  /** Returns false for NaN or too large numbers and for invalid bounds. */
  public boolean isValid(double maxValidMagnitude) {
    if (!LpTypes.isFinite(objectiveOffset)) {
      return false;
    }
    if (Math.abs(objectiveOffset) > maxValidMagnitude) {
      return false;
    }
    if (!LpTypes.isFinite(objectiveScalingFactor)) {
      return false;
    }
    if (objectiveScalingFactor == 0.0) {
      return false;
    }
    if (Math.abs(objectiveScalingFactor) > maxValidMagnitude) {
      return false;
    }
    final int numCols = numVariables();
    for (int col = 0; col < numCols; ++col) {
      final double lb = variableLowerBounds[col];
      final double ub = variableUpperBounds[col];
      if (!areBoundsValid(lb, ub)) {
        return false;
      }
      if (LpTypes.isFinite(lb) && Math.abs(lb) > maxValidMagnitude) {
        return false;
      }
      if (LpTypes.isFinite(ub) && Math.abs(ub) > maxValidMagnitude) {
        return false;
      }
      if (!LpTypes.isFinite(objectiveCoefficients[col])) {
        return false;
      }
      if (Math.abs(objectiveCoefficients[col]) > maxValidMagnitude) {
        return false;
      }
      final SparseColumn c = getSparseColumn(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        if (!LpTypes.isFinite(c.entryCoefficient(i))) {
          return false;
        }
        if (Math.abs(c.entryCoefficient(i)) > maxValidMagnitude) {
          return false;
        }
      }
    }
    for (int row = 0; row < numRowsAllocated; ++row) {
      final double lb = constraintLowerBounds[row];
      final double ub = constraintUpperBounds[row];
      if (!areBoundsValid(lb, ub)) {
        return false;
      }
      if (LpTypes.isFinite(lb) && Math.abs(lb) > maxValidMagnitude) {
        return false;
      }
      if (LpTypes.isFinite(ub) && Math.abs(ub) > maxValidMagnitude) {
        return false;
      }
    }
    return true;
  }

  /** Returns false for NaN bounds, crossing bounds or both bounds at the same infinity. */
  public static boolean areBoundsValid(double lowerBound, double upperBound) {
    if (Double.isNaN(lowerBound)) {
      return false;
    }
    if (Double.isNaN(upperBound)) {
      return false;
    }
    if (lowerBound == LpTypes.INFINITY && upperBound == LpTypes.INFINITY) {
      return false;
    }
    if (lowerBound == -LpTypes.INFINITY && upperBound == -LpTypes.INFINITY) {
      return false;
    }
    if (lowerBound > upperBound) {
      return false;
    }
    return true;
  }

  private void resizeRowsIfNeeded(int row) {
    if (row >= numConstraints()) {
      transposeMatrixIsConsistent = false;
      matrix.setNumRows(row + 1);
      growRows(row + 1);
      for (int r = numRowsAllocated; r <= row; ++r) {
        constraintLowerBounds[r] = 0.0;
        constraintUpperBounds[r] = 0.0;
        constraintNames.add("");
      }
      numRowsAllocated = row + 1;
    }
  }

  /** Returns true if the problem is in the equation form produced by the slack addition. */
  public boolean isInEquationForm() {
    for (int row = 0; row < numConstraints(); ++row) {
      if (constraintLowerBounds[row] != 0.0 || constraintUpperBounds[row] != 0.0) {
        return false;
      }
    }
    final int numSlackVariables = numVariables() - getFirstSlackVariable();
    return numConstraints() == numSlackVariables && matrix.isRightMostSquareMatrixIdentity();
  }

  /** Removes the matrix entries and objective coefficients of magnitude at most threshold. */
  public void removeNearZeroEntries(double threshold) {
    long numRemovedMatrixEntries = 0;
    for (int col = 0; col < matrix.numCols(); ++col) {
      final int oldSize = matrix.column(col).numEntries();
      matrix.mutableColumn(col).removeNearZeroEntries(threshold);
      numRemovedMatrixEntries += oldSize - matrix.column(col).numEntries();
      if (Math.abs(objectiveCoefficients[col]) <= threshold) {
        objectiveCoefficients[col] = 0.0;
      }
    }
    if (numRemovedMatrixEntries > 0) {
      transposeMatrixIsConsistent = false;
    }
  }

  /** Returns true if the solution satisfies all the bounds and constraints within tolerance. */
  public boolean solutionIsLPFeasible(double[] solution, double absoluteTolerance) {
    final double[] lbs = variableLowerBounds();
    final double[] ubs = variableUpperBounds();
    for (int col = 0; col < numVariables(); ++col) {
      if (solution[col] < lbs[col] - absoluteTolerance
          || solution[col] > ubs[col] + absoluteTolerance) {
        return false;
      }
    }
    final double[] activity = new double[numConstraints()];
    for (int col = 0; col < numVariables(); ++col) {
      getSparseColumn(col).addMultipleToDenseVector(solution[col], activity);
    }
    final double[] clb = constraintLowerBounds();
    final double[] cub = constraintUpperBounds();
    for (int row = 0; row < numConstraints(); ++row) {
      if (activity[row] < clb[row] - absoluteTolerance
          || activity[row] > cub[row] + absoluteTolerance) {
        return false;
      }
    }
    return true;
  }
}
