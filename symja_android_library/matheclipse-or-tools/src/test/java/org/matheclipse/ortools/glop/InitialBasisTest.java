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
// ortools/glop/initial_basis_test.cc (OR-Tools 10.0) to JUnit 5, plus a test of the
// deterministic tie-break of the triangular crash.

package org.matheclipse.ortools.glop;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.SparseMatrix;
import org.matheclipse.ortools.lpdata.VariableType;

/** Tests of {@link InitialBasis}. */
public class InitialBasisTest {

  private static CompactSparseMatrix compact(double[][] rows) {
    final int numRows = rows.length;
    final int numCols = rows[0].length;
    SparseMatrix matrix = new SparseMatrix();
    matrix.reset(numCols, numRows);
    for (int col = 0; col < numCols; ++col) {
      for (int row = 0; row < numRows; ++row) {
        if (rows[row][col] != 0.0) {
          matrix.mutableColumn(col).setCoefficient(row, rows[row][col]);
        }
      }
    }
    CompactSparseMatrix compact = new CompactSparseMatrix();
    compact.populateFromMatrixView(matrix);
    return compact;
  }

  private static CompactSparseMatrix identity(int size) {
    double[][] rows = new double[size][size];
    for (int i = 0; i < size; ++i) {
      rows[i][i] = 1.0;
    }
    return compact(rows);
  }

  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  private static void takeAllPossibleColumns(boolean bixbyOrTriangular) {
    final int size = 100;
    double[] objective = new double[size];
    double[] lowerBound = new double[size];
    double[] upperBound = new double[size];
    Arrays.fill(upperBound, 100.0);
    VariableType[] variableType = new VariableType[size];
    Arrays.fill(variableType, VariableType.UPPER_AND_LOWER_BOUNDED);
    InitialBasis initialBasis =
        new InitialBasis(identity(size), objective, lowerBound, upperBound, variableType);

    // No replacement possible.
    int[] basis = new int[size];
    Arrays.fill(basis, 1);
    complete(initialBasis, bixbyOrTriangular, size, basis);
    for (int row = 0; row < size; ++row) {
      assertEquals(1, basis[row]);
    }

    // All replacements possible.
    Arrays.fill(basis, LpTypes.INVALID_COL);
    complete(initialBasis, bixbyOrTriangular, size, basis);
    for (int row = 0; row < size; ++row) {
      assertEquals(row, basis[row]);
    }

    // Random replacements.
    Random random = new Random(1);
    Arrays.fill(basis, LpTypes.INVALID_COL);
    for (int row = 0; row < size; ++row) {
      if (random.nextInt(3) == 0) {
        basis[row] = row;
      }
    }
    complete(initialBasis, bixbyOrTriangular, size, basis);
    for (int row = 0; row < size; ++row) {
      assertEquals(row, basis[row]);
    }
  }

  private static void complete(InitialBasis initialBasis, boolean bixby, int numCols,
      int[] basis) {
    if (bixby) {
      initialBasis.completeBixbyBasis(numCols, basis);
    } else {
      initialBasis.completeTriangularPrimalBasis(numCols, basis);
    }
  }

  @Test
  public void testTakeAllPossibleColumnsBixby() {
    takeAllPossibleColumns(true);
  }

  @Test
  public void testTakeAllPossibleColumnsTriangular() {
    takeAllPossibleColumns(false);
  }

  private static InitialBasis exampleMatrix() {
    // Only the position of the nonzeros is taken into account.
    CompactSparseMatrix matrix = compact(new double[][] {{1, 0, 2, 3, 0, 1, 0, 0},
        {4, 5, 0, 6, 7, 0, 1, 0}, {8, 9, 0, 0, 3, 0, 0, 1}});
    final double inf = LpTypes.INFINITY;
    double[] objective = new double[8];
    double[] lowerBound = {-inf, 0, 0, 0, -inf, 0, 0, 0};
    double[] upperBound = {inf, inf, 1, inf, 0, 0, inf, 1};
    VariableType[] variableType = {VariableType.UNCONSTRAINED, VariableType.LOWER_BOUNDED,
        VariableType.UPPER_AND_LOWER_BOUNDED, VariableType.LOWER_BOUNDED,
        VariableType.UPPER_BOUNDED, VariableType.FIXED_VARIABLE, VariableType.LOWER_BOUNDED,
        VariableType.UPPER_AND_LOWER_BOUNDED};
    return new InitialBasis(matrix, objective, lowerBound, upperBound, variableType);
  }

  @Test
  public void testExampleMatrixTriangular() {
    int[] basis = {5, 6, 7};
    exampleMatrix().completeTriangularPrimalBasis(8, basis);
    assertArrayEquals(new int[] {5, 6, 7}, basis);
  }

  @Test
  public void testExampleMatrixMaros() {
    int[] basis = new int[3];
    exampleMatrix().getPrimalMarosBasis(8, basis);
    assertArrayEquals(new int[] {0, 6, 1}, basis);
  }

  @Test
  public void testCorrectColumnOrder() {
    final double inf = LpTypes.INFINITY;
    double[] objective = {0.0, 0.0, 0.0, 1.0, -2.0, 3.0, 8.0, 2.0, 3.0, 4.0};
    double[] lowerBound = {0.0, -1.0, -inf, -inf, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    double[] upperBound = {10.0, inf, 0.0, inf, 10.0, 10.0, 0.0, 2.0, 3.0, 4.0};
    VariableType[] variableType = {VariableType.UPPER_AND_LOWER_BOUNDED,
        VariableType.LOWER_BOUNDED, VariableType.UPPER_BOUNDED, VariableType.UNCONSTRAINED,
        VariableType.UPPER_AND_LOWER_BOUNDED, VariableType.UPPER_AND_LOWER_BOUNDED,
        VariableType.FIXED_VARIABLE, VariableType.UPPER_AND_LOWER_BOUNDED,
        VariableType.UPPER_AND_LOWER_BOUNDED, VariableType.UPPER_AND_LOWER_BOUNDED};
    InitialBasis initialBasis =
        new InitialBasis(identity(10), objective, lowerBound, upperBound, variableType);
    List<Integer> candidates = initialBasis.computeCandidates(10);
    assertEquals(Arrays.asList(3, 1, 2, 0, 4, 5, 9, 8, 7), candidates);
  }

  /**
   * Columns 1, 2 and 3 are identical singletons of row 1: the triangular crash takes the one with
   * the smallest index instead of an order that depends on the priority queue implementation.
   */
  @Test
  public void testTriangularTieBreakByColumn() {
    CompactSparseMatrix matrix =
        compact(new double[][] {{1, 0, 0, 0}, {0, 1, 1, 1}});
    double[] objective = new double[4];
    double[] lowerBound = new double[4];
    double[] upperBound = {1, 1, 1, 1};
    VariableType[] variableType = new VariableType[4];
    Arrays.fill(variableType, VariableType.UPPER_AND_LOWER_BOUNDED);
    InitialBasis initialBasis =
        new InitialBasis(matrix, objective, lowerBound, upperBound, variableType);
    int[] basis = {LpTypes.INVALID_COL, LpTypes.INVALID_COL};
    initialBasis.completeTriangularPrimalBasis(4, basis);
    assertArrayEquals(new int[] {0, 1}, basis);
  }
}
