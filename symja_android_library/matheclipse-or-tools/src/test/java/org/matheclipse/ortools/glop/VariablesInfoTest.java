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
// ortools/glop/variables_info_test.cc (OR-Tools 10.0) to JUnit 5, plus a test of the FREE
// statuses kept for the columns with a starting value.

package org.matheclipse.ortools.glop;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.SparseMatrix;
import org.matheclipse.ortools.lpdata.VariableStatus;

/** Tests of {@link VariablesInfo}. */
public class VariablesInfoTest {

  private static final double INF = LpTypes.INFINITY;

  /** The identity of size numCols - 1 followed by an empty column. */
  private static CompactSparseMatrix identityAndEmptyColumn(int numCols) {
    SparseMatrix matrix = new SparseMatrix();
    matrix.reset(numCols, numCols - 1);
    for (int col = 0; col < numCols - 1; ++col) {
      matrix.mutableColumn(col).setCoefficient(col, 1.0);
    }
    CompactSparseMatrix compact = new CompactSparseMatrix();
    compact.populateFromMatrixView(matrix);
    return compact;
  }

  private static VariableStatus[] fill(int n, VariableStatus status) {
    VariableStatus[] statuses = new VariableStatus[n];
    Arrays.fill(statuses, status);
    return statuses;
  }

  @Test
  public void testWarmStart() {
    final int numCols = 10;
    VariablesInfo info = new VariablesInfo(identityAndEmptyColumn(numCols));
    info.loadBoundsAndReturnTrueIfUnchanged(
        new double[] {-INF, -INF, 0.0, 0.0, 0.0, -INF, -INF, 0.0, 0.0, 0.0},
        new double[] {INF, 0.0, INF, 0.0, 10.0, INF, 0.0, INF, 0.0, 10.0});
    info.initializeToDefaultStatus();

    info.initializeFromBasisState(numCols, 0,
        new BasisState(fill(numCols, VariableStatus.AT_LOWER_BOUND)));
    assertArrayEquals(new VariableStatus[] {VariableStatus.FREE, VariableStatus.AT_UPPER_BOUND,
        VariableStatus.AT_LOWER_BOUND, VariableStatus.FIXED_VALUE, VariableStatus.AT_LOWER_BOUND,
        VariableStatus.FREE, VariableStatus.AT_UPPER_BOUND, VariableStatus.AT_LOWER_BOUND,
        VariableStatus.FIXED_VALUE, VariableStatus.AT_LOWER_BOUND}, info.getStatusRow());

    info.initializeFromBasisState(numCols, 0,
        new BasisState(fill(numCols, VariableStatus.AT_UPPER_BOUND)));
    assertArrayEquals(new VariableStatus[] {VariableStatus.FREE, VariableStatus.AT_UPPER_BOUND,
        VariableStatus.AT_LOWER_BOUND, VariableStatus.FIXED_VALUE, VariableStatus.AT_UPPER_BOUND,
        VariableStatus.FREE, VariableStatus.AT_UPPER_BOUND, VariableStatus.AT_LOWER_BOUND,
        VariableStatus.FIXED_VALUE, VariableStatus.AT_UPPER_BOUND}, info.getStatusRow());
  }

  /**
   * A FREE status of a bounded column with a starting value (col &lt; numFreeValues) is kept,
   * FIXED_VALUE for a fixed column; without a starting value the default status is used.
   */
  @Test
  public void testWarmStartKeepsFreeStatusOfColumnsWithStartingValue() {
    final int numCols = 6;
    VariablesInfo info = new VariablesInfo(identityAndEmptyColumn(numCols));
    info.loadBoundsAndReturnTrueIfUnchanged(new double[] {0.0, -5.0, 2.0, 0.0, -INF, -1.0},
        new double[] {10.0, 1.0, 2.0, 10.0, INF, 4.0});
    info.initializeToDefaultStatus();
    BasisState state = new BasisState(fill(numCols, VariableStatus.FREE));

    info.initializeFromBasisState(numCols, 0, state, 3);
    assertArrayEquals(new VariableStatus[] {VariableStatus.FREE, VariableStatus.FREE,
        VariableStatus.FIXED_VALUE, VariableStatus.AT_LOWER_BOUND, VariableStatus.FREE,
        VariableStatus.AT_LOWER_BOUND}, info.getStatusRow());

    info.initializeFromBasisState(numCols, 0, state);
    assertArrayEquals(new VariableStatus[] {VariableStatus.AT_LOWER_BOUND,
        VariableStatus.AT_UPPER_BOUND, VariableStatus.FIXED_VALUE, VariableStatus.AT_LOWER_BOUND,
        VariableStatus.FREE, VariableStatus.AT_LOWER_BOUND}, info.getStatusRow());
  }

  @Test
  public void testChangeUnusedBasicVariablesToFree() {
    final int numCols = 6;
    SparseMatrix matrix = new SparseMatrix();
    matrix.reset(numCols, numCols);
    for (int col = 0; col < numCols; ++col) {
      matrix.mutableColumn(col).setCoefficient(col, 1.0);
    }
    CompactSparseMatrix compact = new CompactSparseMatrix();
    compact.populateFromMatrixView(matrix);
    VariablesInfo info = new VariablesInfo(compact);
    double[] lower = new double[numCols];
    double[] upper = new double[numCols];
    Arrays.fill(upper, 1.0);
    info.loadBoundsAndReturnTrueIfUnchanged(lower, upper);
    info.initializeToDefaultStatus();
    info.initializeFromBasisState(numCols, 0, new BasisState(fill(numCols, VariableStatus.BASIC)));
    org.junit.jupiter.api.Assertions.assertEquals(4,
        info.changeUnusedBasicVariablesToFree(new int[] {2, 4}));
    assertArrayEquals(new VariableStatus[] {VariableStatus.FREE, VariableStatus.FREE,
        VariableStatus.BASIC, VariableStatus.FREE, VariableStatus.BASIC, VariableStatus.FREE},
        info.getStatusRow());
  }
}
