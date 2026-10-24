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
// ortools/lp_data/matrix_scaler.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.lpdata;

import java.util.Arrays;
import org.matheclipse.ortools.glop.GlopParameters;

/**
 * Scales the rows and columns of a sparse matrix: A is replaced by R.A.C with diagonal R and C.
 *
 * <p>
 * The algorithm is the geometric scaling of Benichou et al. (at most 4 passes) followed by an
 * equilibration of the rows and the columns. The <code>RUIZ</code> method is the Ruiz
 * equilibration to convergence (see {@link #ruizEquilibrate()}). The <code>LINEAR_PROGRAM</code>
 * scaling method of the C++ code, which solves an auxiliary LP, is not ported; it falls back to the
 * default method as the C++ code does when that LP fails.
 */
public final class SparseMatrixScaler {

  private SparseMatrix matrix;
  private double[] rowScales = new double[0];
  private double[] colScales = new double[0];

  /** Attaches the scaler to a matrix, all the scales are 1.0. */
  public void init(SparseMatrix matrix) {
    this.matrix = matrix;
    rowScales = new double[matrix.numRows()];
    colScales = new double[matrix.numCols()];
    Arrays.fill(rowScales, 1.0);
    Arrays.fill(colScales, 1.0);
  }

  /** Detaches the scaler. */
  public void clear() {
    matrix = null;
    rowScales = new double[0];
    colScales = new double[0];
  }

  /** The unscaling factor of a row. */
  public double rowUnscalingFactor(int row) {
    return row < rowScales.length ? rowScales[row] : 1.0;
  }

  /** The unscaling factor of a column. */
  public double colUnscalingFactor(int col) {
    return col < colScales.length ? colScales[col] : 1.0;
  }

  /** The scaling factor of a row. */
  public double rowScalingFactor(int row) {
    return 1.0 / rowUnscalingFactor(row);
  }

  /** The scaling factor of a column. */
  public double colScalingFactor(int col) {
    return 1.0 / colUnscalingFactor(col);
  }

  /** Computes R and C and replaces the matrix by R.A.C. */
  public void scale(GlopParameters.ScalingAlgorithm method) {
    final double[] minMax = matrix.computeMinAndMaxMagnitudes();
    final double minMagnitude = minMax[0];
    final double maxMagnitude = minMax[1];
    if (minMagnitude == 0.0) {
      return; // Null matrix: nothing to do.
    }
    if (method == GlopParameters.ScalingAlgorithm.RUIZ) {
      ruizEquilibrate();
      return;
    }
    final double dynamicRange = maxMagnitude / minMagnitude;
    final double kMaxDynamicRangeForGeometricScaling = 1e20;
    if (dynamicRange < kMaxDynamicRangeForGeometricScaling) {
      final int kScalingIterations = 4;
      final double kVarianceThreshold = 10.0;
      for (int iteration = 0; iteration < kScalingIterations; ++iteration) {
        final int numRowsScaled = scaleRowsGeometrically();
        final int numColsScaled = scaleColumnsGeometrically();
        final double variance = varianceOfAbsoluteValueOfNonZeros();
        if (variance < kVarianceThreshold || (numColsScaled == 0 && numRowsScaled == 0)) {
          break;
        }
      }
    }
    equilibrateRows();
    equilibrateColumns();
  }

  private static void scaleVector(double[] scale, boolean up, double[] vectorToScale) {
    final int size = Math.min(scale.length, vectorToScale.length);
    if (up) {
      for (int i = 0; i < size; ++i) {
        vectorToScale[i] *= scale[i];
      }
    } else {
      for (int i = 0; i < size; ++i) {
        vectorToScale[i] /= scale[i];
      }
    }
  }

  /** Multiplies (up) or divides a column-indexed vector by the column scales. */
  public void scaleRowVector(boolean up, double[] rowVector) {
    scaleVector(colScales, up, rowVector);
  }

  /** Multiplies (up) or divides a row-indexed vector by the row scales. */
  public void scaleColumnVector(boolean up, double[] columnVector) {
    scaleVector(rowScales, up, columnVector);
  }

  /** The variance of the absolute values of the non-zero coefficients. */
  public double varianceOfAbsoluteValueOfNonZeros() {
    double sigmaSquare = 0.0;
    double sigmaAbs = 0.0;
    double n = 0.0;
    for (int col = 0; col < matrix.numCols(); ++col) {
      final SparseColumn c = matrix.column(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        final double magnitude = Math.abs(c.entryCoefficient(i));
        sigmaSquare += magnitude * magnitude;
        sigmaAbs += magnitude;
        ++n;
      }
    }
    if (n == 0.0) {
      return 0.0;
    }
    return (sigmaSquare - sigmaAbs * sigmaAbs / n) / n;
  }

  private int scaleRowsGeometrically() {
    final int numRows = matrix.numRows();
    final double[] maxInRow = new double[numRows];
    final double[] minInRow = new double[numRows];
    Arrays.fill(minInRow, LpTypes.INFINITY);
    for (int col = 0; col < matrix.numCols(); ++col) {
      final SparseColumn c = matrix.column(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        final double magnitude = Math.abs(c.entryCoefficient(i));
        final int row = c.entryRow(i);
        if (magnitude != 0.0) {
          maxInRow[row] = Math.max(maxInRow[row], magnitude);
          minInRow[row] = Math.min(minInRow[row], magnitude);
        }
      }
    }
    final double[] scalingFactor = new double[numRows];
    for (int row = 0; row < numRows; ++row) {
      if (maxInRow[row] == 0.0) {
        scalingFactor[row] = 1.0;
      } else {
        scalingFactor[row] = Math.sqrt(maxInRow[row] * minInRow[row]);
      }
    }
    return scaleMatrixRows(scalingFactor);
  }

  private int scaleColumnsGeometrically() {
    int numColsScaled = 0;
    for (int col = 0; col < matrix.numCols(); ++col) {
      double maxInCol = 0.0;
      double minInCol = LpTypes.INFINITY;
      final SparseColumn c = matrix.column(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        final double magnitude = Math.abs(c.entryCoefficient(i));
        if (magnitude != 0.0) {
          maxInCol = Math.max(maxInCol, magnitude);
          minInCol = Math.min(minInCol, magnitude);
        }
      }
      if (maxInCol != 0.0) {
        final double factor = Math.sqrt(maxInCol * minInCol);
        scaleMatrixColumn(col, factor);
        numColsScaled++;
      }
    }
    return numColsScaled;
  }

  private int equilibrateRows() {
    final int numRows = matrix.numRows();
    final double[] maxMagnitudes = new double[numRows];
    for (int col = 0; col < matrix.numCols(); ++col) {
      final SparseColumn c = matrix.column(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        final double magnitude = Math.abs(c.entryCoefficient(i));
        if (magnitude != 0.0) {
          final int row = c.entryRow(i);
          maxMagnitudes[row] = Math.max(maxMagnitudes[row], magnitude);
        }
      }
    }
    for (int row = 0; row < numRows; ++row) {
      if (maxMagnitudes[row] == 0.0) {
        maxMagnitudes[row] = 1.0;
      }
    }
    return scaleMatrixRows(maxMagnitudes);
  }

  private int equilibrateColumns() {
    int numColsScaled = 0;
    for (int col = 0; col < matrix.numCols(); ++col) {
      final double maxMagnitude = matrix.column(col).infinityNorm();
      if (maxMagnitude != 0.0 && maxMagnitude != 1.0) {
        scaleMatrixColumn(col, maxMagnitude);
        numColsScaled++;
      }
    }
    return numColsScaled;
  }

  private int scaleMatrixRows(double[] factors) {
    final int numRows = matrix.numRows();
    int numRowsScaled = 0;
    for (int row = 0; row < numRows; ++row) {
      final double factor = factors[row];
      if (factor != 1.0) {
        ++numRowsScaled;
        rowScales[row] *= factor;
      }
    }
    for (int col = 0; col < matrix.numCols(); ++col) {
      matrix.mutableColumn(col).componentWiseDivide(factors);
    }
    return numRowsScaled;
  }

  private static int updateScales(double[] factors, double[] scales) {
    int numScaled = 0;
    for (int i = 0; i < factors.length; ++i) {
      final double factor = factors[i];
      if (factor != 1.0) {
        ++numScaled;
        scales[i] *= factor;
      }
    }
    return numScaled;
  }

  /**
   * Runs the Ruiz equilibration to convergence, at most 10 passes on both rows and columns. Each
   * pass divides the rows and the columns by the square root of their infinity norm; at convergence
   * both the row and the column infinity norms approach 1.
   *
   * <p>
   * Reference: D. Ruiz, "A Scaling Algorithm to Equilibrate Both Rows and Columns Norms in
   * Matrices", RAL-TR-2001-034, Rutherford Appleton Laboratory, 2001.
   */
  void ruizEquilibrate() {
    final int numRows = matrix.numRows();
    final int numCols = matrix.numCols();
    // Each pass reduces the deviation of the infinity norms from 1 by a constant factor; in
    // practice 3-5 passes make the factors reach 1.0 in double precision. 10 is a safety cap.
    final int kRuizIterations = 10;
    for (int k = 0; k < kRuizIterations; ++k) {
      final double[] colFactors = new double[numCols];
      final double[] rowFactors = new double[numRows];
      for (int col = 0; col < numCols; ++col) {
        final SparseColumn column = matrix.column(col);
        final double colMagnitude = column.infinityNorm();
        colFactors[col] = colMagnitude == 0.0 ? 1.0 : Math.sqrt(colMagnitude);
        for (int i = 0; i < column.numEntries(); ++i) {
          final double rowMagnitude = Math.abs(column.entryCoefficient(i));
          final int row = column.entryRow(i);
          rowFactors[row] = Math.max(rowMagnitude, rowFactors[row]);
        }
      }
      for (int row = 0; row < numRows; ++row) {
        rowFactors[row] = rowFactors[row] == 0.0 ? 1.0 : Math.sqrt(rowFactors[row]);
      }
      final int numRowsScaled = updateScales(rowFactors, rowScales);
      final int numColsScaled = updateScales(colFactors, colScales);
      if (numRowsScaled == 0 && numColsScaled == 0) {
        break;
      }
      for (int col = 0; col < numCols; ++col) {
        final double colFactor = colFactors[col];
        final SparseColumn column = matrix.mutableColumn(col);
        for (int i = 0; i < column.numEntries(); ++i) {
          final double rowFactor = rowFactors[column.entryRow(i)];
          if (colFactor == 1.0) {
            if (rowFactor != 1.0) {
              column.setEntryCoefficient(i, column.entryCoefficient(i) / rowFactor);
            }
          } else {
            column.setEntryCoefficient(i, column.entryCoefficient(i) / (rowFactor * colFactor));
          }
        }
      }
    }
  }

  private void scaleMatrixColumn(int col, double factor) {
    colScales[col] *= factor;
    matrix.mutableColumn(col).divideByConstant(factor);
  }
}
