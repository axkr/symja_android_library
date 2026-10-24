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
// Modified for the Symja matheclipse-or-tools module: tests ported from
// ortools/lp_data/matrix_scaler_test.cc (OR-Tools 10.0) to JUnit 5 (java.util.Random instead of
// std::mt19937, the parameterized tests are loops over all the methods, RUIZ included).

package org.matheclipse.ortools.lpdata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.glop.GlopParameters;
import org.matheclipse.ortools.glop.GlopParameters.ScalingAlgorithm;

/** Tests of {@link SparseMatrixScaler}. */
public class SparseMatrixScalerTest {

  private static void assertWithinTolerance(double expected, double actual, double tolerance) {
    final double scale = Math.max(1.0, Math.abs(expected));
    assertEquals(expected, actual, tolerance * scale);
  }

  private static SparseMatrix zero(int numRows, int numCols) {
    SparseMatrix matrix = new SparseMatrix();
    matrix.reset(numCols, numRows);
    return matrix;
  }

  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  private static void checkRandomMatrix(ScalingAlgorithm method) {
    Random random = new Random(0);
    final int maxCol = 100;
    final int maxRow = 500;
    final int range = 100000000;
    SparseMatrix matrix = zero(maxRow, maxCol);
    for (int col = 0; col < maxCol; ++col) {
      SparseColumn column = matrix.mutableColumn(col);
      for (int row = 0; row < maxRow; ++row) {
        final double v = -range + 2.0 * random.nextInt(range);
        if (v != 0.0) {
          column.setCoefficient(row, v);
        }
      }
    }
    matrix.cleanUp();
    SparseMatrix copy = new SparseMatrix();
    copy.populateFromSparseMatrix(matrix);

    SparseMatrixScaler scaler = new SparseMatrixScaler();
    scaler.init(matrix);
    scaler.scale(method);

    double[] colVector = new double[maxRow];
    Arrays.fill(colVector, 1.0);
    double[] colCopy = colVector.clone();
    scaler.scaleColumnVector(true, colVector);
    double[] rowVector = new double[maxCol];
    Arrays.fill(rowVector, 1.0);
    double[] rowCopy = rowVector.clone();
    scaler.scaleRowVector(false, rowVector);

    for (int col = 0; col < maxCol; ++col) {
      SparseColumn column = matrix.column(col);
      SparseColumn columnCopy = copy.column(col);
      assertEquals(columnCopy.numEntries(), column.numEntries());
      for (int i = 0; i < column.numEntries(); ++i) {
        assertEquals(columnCopy.entryRow(i), column.entryRow(i));
        final double copyValue =
            columnCopy.entryCoefficient(i) / colVector[column.entryRow(i)] * rowVector[col];
        assertWithinTolerance(column.entryCoefficient(i), copyValue, 1e-9);
      }
    }

    // Unscale the matrix.
    double[] rowScales = new double[maxRow];
    for (int row = 0; row < maxRow; ++row) {
      rowScales[row] = scaler.rowUnscalingFactor(row);
    }
    for (int col = 0; col < maxCol; ++col) {
      SparseColumn column = matrix.mutableColumn(col);
      column.multiplyByConstant(scaler.colUnscalingFactor(col));
      column.componentWiseMultiply(rowScales);
    }
    for (int col = 0; col < maxCol; ++col) {
      SparseColumn column = matrix.column(col);
      SparseColumn columnCopy = copy.column(col);
      for (int i = 0; i < column.numEntries(); ++i) {
        assertWithinTolerance(column.entryCoefficient(i), columnCopy.entryCoefficient(i), 1e-9);
      }
    }

    scaler.scaleColumnVector(false, colVector);
    scaler.scaleRowVector(true, rowVector);
    for (int row = 0; row < maxRow; ++row) {
      assertWithinTolerance(colCopy[row], colVector[row], 1e-9);
    }
    for (int col = 0; col < maxCol; ++col) {
      assertWithinTolerance(rowCopy[col], rowVector[col], 1e-9);
    }
  }

  private static void checkEmptyRow(ScalingAlgorithm method) {
    SparseMatrix matrix = new SparseMatrix();
    matrix.setNumRows(2);
    matrix.appendUnitVector(0, 100.0);
    SparseMatrixScaler scaler = new SparseMatrixScaler();
    scaler.init(matrix);
    scaler.scale(method);
    assertEquals(1, matrix.column(0).numEntries());
    assertEquals(0, matrix.column(0).entryRow(0));
    assertEquals(1.0, matrix.column(0).entryCoefficient(0), 1e-9);
  }

  private static void checkEmptyColumn(ScalingAlgorithm method) {
    SparseMatrix matrix = new SparseMatrix();
    matrix.setNumRows(2);
    matrix.appendEmptyColumn();
    matrix.appendUnitVector(0, 100.0);
    SparseMatrixScaler scaler = new SparseMatrixScaler();
    scaler.init(matrix);
    scaler.scale(method);
    assertEquals(0, matrix.column(0).numEntries());
    assertEquals(1, matrix.column(1).numEntries());
    assertEquals(1.0, matrix.column(1).entryCoefficient(0), 1e-9);
  }

  private static void checkEmptyMatrices(ScalingAlgorithm method) {
    for (SparseMatrix matrix : new SparseMatrix[] {new SparseMatrix(), zero(0, 10),
        zero(10, 0)}) {
      SparseMatrixScaler scaler = new SparseMatrixScaler();
      scaler.init(matrix);
      scaler.scale(method);
      for (int j = 0; j < 100; ++j) {
        assertEquals(1.0, scaler.rowUnscalingFactor(j));
        assertEquals(1.0, scaler.colUnscalingFactor(j));
      }
    }
  }

  private static void checkNullMatrix(ScalingAlgorithm method) {
    SparseMatrix matrix = zero(10, 10);
    SparseMatrixScaler scaler = new SparseMatrixScaler();
    scaler.init(matrix);
    scaler.scale(method);
    for (int col = 0; col < 10; ++col) {
      assertEquals(0, matrix.column(col).numEntries());
    }
  }

  private static SparseMatrix fromRows(double[][] rows) {
    SparseMatrix matrix = zero(rows.length, rows[0].length);
    for (int col = 0; col < rows[0].length; ++col) {
      for (int row = 0; row < rows.length; ++row) {
        if (rows[row][col] != 0.0) {
          matrix.mutableColumn(col).setCoefficient(row, rows[row][col]);
        }
      }
    }
    matrix.cleanUp();
    return matrix;
  }

  @Test
  public void testHighDynamicRange() {
    SparseMatrix matrix = fromRows(new double[][] {{1, -1e-44, 0}, {0, -1, 1e-50},
        {0.1, -1e-64, 1}, {1, -1e-40, 0}});
    SparseMatrixScaler scaler = new SparseMatrixScaler();
    scaler.init(matrix);
    scaler.scale(GlopParameters.ScalingAlgorithm.EQUILIBRATION);
    // The scaler does nothing.
    for (int row = 0; row < matrix.numRows(); ++row) {
      assertEquals(1.0, scaler.rowUnscalingFactor(row));
    }
    for (int col = 0; col < matrix.numCols(); ++col) {
      assertEquals(1.0, scaler.colUnscalingFactor(col));
    }
  }

  /**
   * Scales the symmetric matrix with the Ruiz equilibration and checks that the symmetry is kept:
   * A'[i,j] == A'[j,i] and the row scales are equal to the column scales.
   */
  private static void assertSymmetricAfterRuiz(SparseMatrix matrix) {
    final int n = matrix.numRows();
    assertEquals(n, matrix.numCols());
    SparseMatrixScaler scaler = new SparseMatrixScaler();
    scaler.init(matrix);
    scaler.scale(GlopParameters.ScalingAlgorithm.RUIZ);
    final double tolerance = 1e-9;
    for (int i = 0; i < n; ++i) {
      for (int j = i + 1; j < n; ++j) {
        assertEquals(matrix.lookUpValue(i, j), matrix.lookUpValue(j, i), tolerance,
            "symmetry broken at (" + i + ", " + j + ")");
      }
    }
    for (int k = 0; k < n; ++k) {
      assertEquals(scaler.rowUnscalingFactor(k), scaler.colUnscalingFactor(k), tolerance);
    }
  }

  @Test
  public void testRuizPreservesSymmetrySmall() {
    SparseMatrix matrix = fromRows(new double[][] {{2.0, 0.5, 0.0, 100.0}, {0.5, 8.0, 3.0, 0.0},
        {0.0, 3.0, 50.0, 7.0}, {100.0, 0.0, 7.0, 1.0}});
    assertSymmetricAfterRuiz(matrix);
    // At convergence the row and column infinity norms are close to 1.
    for (int col = 0; col < 4; ++col) {
      assertEquals(1.0, matrix.column(col).infinityNorm(), 1e-3);
    }
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testRuizPreservesSymmetryRandom() {
    final int size = 50;
    Random random = new Random(42);
    SparseMatrix matrix = zero(size, size);
    for (int i = 0; i < size; ++i) {
      final double diag = Math.pow(10.0, -3.0 + 6.0 * random.nextDouble());
      matrix.mutableColumn(i).setCoefficient(i, diag);
      for (int j = 0; j < i; ++j) {
        if (random.nextDouble() >= 0.3) {
          continue;
        }
        final double magnitude = Math.pow(10.0, -3.0 + 6.0 * random.nextDouble());
        final double value = random.nextBoolean() ? -magnitude : magnitude;
        matrix.mutableColumn(j).setCoefficient(i, value);
        matrix.mutableColumn(i).setCoefficient(j, value);
      }
    }
    matrix.cleanUp();
    assertSymmetricAfterRuiz(matrix);
  }

  @Test
  public void testRandomMatrix() {
    for (ScalingAlgorithm method : ScalingAlgorithm.values()) {
      checkRandomMatrix(method);
    }
  }

  @Test
  public void testEmptyRow() {
    for (ScalingAlgorithm method : ScalingAlgorithm.values()) {
      checkEmptyRow(method);
    }
  }

  @Test
  public void testEmptyColumn() {
    for (ScalingAlgorithm method : ScalingAlgorithm.values()) {
      checkEmptyColumn(method);
    }
  }

  @Test
  public void testEmptyMatrices() {
    for (ScalingAlgorithm method : ScalingAlgorithm.values()) {
      checkEmptyMatrices(method);
    }
  }

  @Test
  public void testNullMatrix() {
    for (ScalingAlgorithm method : ScalingAlgorithm.values()) {
      checkNullMatrix(method);
    }
  }
}
