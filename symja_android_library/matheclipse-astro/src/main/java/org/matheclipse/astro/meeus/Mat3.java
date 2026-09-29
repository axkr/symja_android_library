/*
 * Mat3.java  -  3x3 matrix class
 * Copyright (C) 2011-2023 Brian Simpson
 * This file is part of Night Vision.
 *
 * Night Vision is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Night Vision is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Night Vision.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Modified 2026 for Symja: ported from com.nvastro.nvj.Matrix3x3 and Matrix3x1 (Night Vision 5.5)
 * to package org.matheclipse.astro.meeus. The matrix is immutable, and the 3x1 matrix is a plain
 * double[3].
 */

package org.matheclipse.astro.meeus;

/**
 * Immutable 3x3 matrix, just enough for the rotations of this package.
 *
 * @author Brian Simpson
 */
final class Mat3 {

  static final Mat3 IDENTITY = new Mat3(1, 0, 0, 0, 1, 0, 0, 0, 1);

  private final double[][] num = new double[3][3];

  Mat3(double a, double b, double c, double d, double e, double f, double g, double h, double i) {
    num[0][0] = a;
    num[0][1] = b;
    num[0][2] = c;
    num[1][0] = d;
    num[1][1] = e;
    num[1][2] = f;
    num[2][0] = g;
    num[2][1] = h;
    num[2][2] = i;
  }

  /** Multiplies 2 3x3 matrices as m x n. */
  static Mat3 mult(Mat3 m, Mat3 n) {
    double[][] a = m.num;
    double[][] b = n.num;
    return new Mat3(a[0][0] * b[0][0] + a[0][1] * b[1][0] + a[0][2] * b[2][0],
        a[0][0] * b[0][1] + a[0][1] * b[1][1] + a[0][2] * b[2][1],
        a[0][0] * b[0][2] + a[0][1] * b[1][2] + a[0][2] * b[2][2],
        a[1][0] * b[0][0] + a[1][1] * b[1][0] + a[1][2] * b[2][0],
        a[1][0] * b[0][1] + a[1][1] * b[1][1] + a[1][2] * b[2][1],
        a[1][0] * b[0][2] + a[1][1] * b[1][2] + a[1][2] * b[2][2],
        a[2][0] * b[0][0] + a[2][1] * b[1][0] + a[2][2] * b[2][0],
        a[2][0] * b[0][1] + a[2][1] * b[1][1] + a[2][2] * b[2][1],
        a[2][0] * b[0][2] + a[2][1] * b[1][2] + a[2][2] * b[2][2]);
  }

  /** Multiplies 2 3x3 matrices as m x this. */
  Mat3 premult(Mat3 m) {
    return mult(m, this);
  }

  /** Multiplies 2 3x3 matrices as this x n. */
  Mat3 postmult(Mat3 n) {
    return mult(this, n);
  }

  /** Multiplies this 3x3 matrix with a 3x1 matrix. */
  double[] mult(double[] v) {
    return new double[] {num[0][0] * v[0] + num[0][1] * v[1] + num[0][2] * v[2],
        num[1][0] * v[0] + num[1][1] * v[1] + num[1][2] * v[2],
        num[2][0] * v[0] + num[2][1] * v[1] + num[2][2] * v[2]};
  }

  /** Inverts a 3x3 matrix. */
  Mat3 invert() {
    double[][] d = new double[3][3];
    int i, j, m, n, p, q;

    /* Calculate determinant */
    double det = num[0][0] * num[1][1] * num[2][2] + num[0][1] * num[1][2] * num[2][0]
        + num[0][2] * num[1][0] * num[2][1] - num[0][2] * num[1][1] * num[2][0]
        - num[0][0] * num[1][2] * num[2][1] - num[0][1] * num[1][0] * num[2][2];
    // Doubt that a 0 determinant will result from rot. matrices
    for (i = 0; i < 3; i++) { // Row
      m = (i + 1) % 3;
      n = (i + 2) % 3; // Other rows
      for (j = 0; j < 3; j++) { // Column
        p = (j + 1) % 3;
        q = (j + 2) % 3; // Other columns
        d[j][i] = (num[m][p] * num[n][q] - num[m][q] * num[n][p]) / det;
      }
    }
    return new Mat3(d[0][0], d[0][1], d[0][2], d[1][0], d[1][1], d[1][2], d[2][0], d[2][1],
        d[2][2]);
  }

  /** Unit vector of a right ascension and declination (or longitude and latitude) in radians. */
  static double[] unit(double ra, double dec) {
    double cosdec = Math.cos(dec);
    return new double[] {cosdec * Math.cos(ra), cosdec * Math.sin(ra), Math.sin(dec)};
  }

  /**
   * Rotates a right ascension and declination in radians.
   *
   * @return <code>{ra, dec}</code>, ra in -pi..pi
   */
  double[] rotateRADec(double ra, double dec) {
    double[] v = mult(unit(ra, dec));
    double d;
    if (v[2] > 1) {
      d = Math.PI / 2;
    } else if (v[2] < -1) {
      d = -Math.PI / 2;
    } else {
      d = Math.asin(v[2]);
    }
    double r = (v[0] == 0 && v[1] == 0) ? 0.0 : Math.atan2(v[1], v[0]);
    return new double[] {r, d};
  }
}
