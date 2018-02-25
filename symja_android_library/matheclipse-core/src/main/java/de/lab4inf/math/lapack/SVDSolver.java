/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math.lapack;

import de.lab4inf.math.Numeric;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.lapack.LinearAlgebra.asDouble;
import static de.lab4inf.math.lapack.LinearAlgebra.asFloat;
import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.isSymmetric;
import static de.lab4inf.math.lapack.LinearAlgebra.maxabs;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.norm;
import static de.lab4inf.math.lapack.LinearAlgebra.transpose;
import static de.lab4inf.math.util.Accuracy.DEPS;
import static de.lab4inf.math.util.Accuracy.FEPS;
import static java.lang.Math.abs;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Solver for A*x=y with help of the singular value decomposition A = UxDxV*.
 * The formal solution is given by the pseudo inverse A+=VxD+xU* with x=A+*y.
 * <br/>
 * <p>
 * As this algorithm is much more complex and time consuming (~40 times the
 * Gauss or Cholesky method) it should only be used in case of singular values,
 * e.g. the determinant vanishes.
 * <p>
 * <hr/>
 * <pre>
 * This implementation is partially derived form the LINPACK package ported
 * and refactored for Java.  http://www.netlib.org/linpack/dsvdc.f
 * </pre>
 * <hr/>
 *
 * @author nwulff
 * @version $Id: SVDSolver.java,v 1.35 2014/12/08 15:21:05 nwulff Exp $
 * @since 11.07.2009
 */
public final class SVDSolver extends LASolver {
    /**
     * reference to the LOW_RANK attribute.
     */
    private static final String LOW_RANK = "Matrix has low rank: %d < %d ";
    private static final String DIM_MISSMATCH = "dimension missmatch %d < %d";
    private static final double EPS = DEPS;
    private static final double VERY_TINY = 1.E-290; // pow(2.0, -966.0);
    private static boolean roundResult = false;

    /**
     * Internal utility method to round the eigenvalues to significant digits.
     *
     * @param d eigenvalues to round
     * @return rounded eigenvalue array
     */
    private static double[] roundEVs(final double[] d) {
        final int n = d.length;
        final double tol = n * d[0] * EPS;
        final int digits = -(int) log10(abs(tol));

        for (int i = 0; i < d.length; i++) {
            if (d[i] < tol) {
                d[i] = 0;
            } else {
                d[i] = Accuracy.round(d[i], digits);
            }
        }
        return d;
    }

    private static double[] roundUVD(final double[][] u, final double[] d, final double[][] v) {
        final int n = d.length;
        final int m = u[0].length;
        final double tol = n * d[0] * EPS;
        final int digits = -(int) log10(abs(tol));

        for (int i = 0; i < d.length; i++) {
            if (d[i] < tol) {
                d[i] = 0;
            } else {
                d[i] = Accuracy.round(d[i], digits);
            }
            for (int j = 0; j < m; j++) {
                u[i][j] = Accuracy.round(u[i][j], digits);
                v[i][j] = Accuracy.round(v[i][j], digits);
            }
        }
        return d;
    }

    /**
     * Indicate if |x| is much smaller than |y|.
     * I.e. |x| <= TINY + EPS*|y| where TINY and EPS
     * are ~1.E-290 and ~ 1.E-15 for double precision.
     *
     * @param x 1.st argument to check
     * @param y 2.nd argument to compare to
     * @return small indicator
     */
    private static boolean isSmall(final double x, final double y) {
        return abs(x) <= VERY_TINY + EPS * abs(y);
    }

    /**
     * Swap column k with column j of matrix A.
     *
     * @param a the NxM matrix A
     * @param m dimension to consider M<=N
     * @param k the first column index
     * @param j the second column index
     */
    private static void swapColumn(final double[][] a, final int m, final int k, final int j) {
        double t;
        for (int i = 0; i < m; t = a[i][k], a[i][k] = a[i][j], a[i][j] = t, i++)
            ;
    }

    /**
     * Perform a givens rotation between index j and k for all rows of matrix a.
     *
     * @param a  matrix A
     * @param j  index one of the rotation
     * @param k  index two of the rotation
     * @param cs cosine value of rotation
     * @param sn sine value of rotation
     */
    private static void givensrotation(final double[][] a, final int j, final int k, final double cs, final double sn) {
        double t;
        final int n = a.length;
        for (int i = 0; i < n; t = cs * a[i][j] + sn * a[i][k], a[i][k] = -sn * a[i][j] + cs * a[i][k], a[i][j] = t, i++)
            ;
    }

    /**
     * Get the roundResult flag.
     *
     * @return true if result is rounded
     */
    public static boolean isRoundResult() {
        return roundResult;
    }

    /**
     * Signal if rounded results should be used.
     *
     * @param roundResult flag to set
     */
    public static void setRoundResult(final boolean roundResult) {
        SVDSolver.roundResult = roundResult;
    }

    /**
     * Calculate the eigenvalues of matrix A.
     *
     * @param a double[][] the NxN matrix
     * @return double[] with the eigenvalues
     */
    @Override
    public double[] eigenvalues(final double[][] a) {
        final int m = a.length, n = a[0].length;
        if (m != n) {
            final String msg = format(DIM_MISSMATCH, m, n);
            throw new IllegalArgumentException(msg);
        }
        if (isSymmetric(a)) {
            return eigenvaluesSymmetric(a);
        }
        final double[] d = new double[min(m + 1, n)];
        svdDecompose(a, null, d, null);
        return d;
    }

    public double[] eigenvaluesSymmetric(final double[][] a) {
        final int m = a.length, n = a[0].length;
        if (m != n) {
            final String msg = format(DIM_MISSMATCH, m, n);
            throw new IllegalArgumentException(msg);
        }
        final double[] d = new double[n];
        svdDecompose(a, null, d, null);
        return d;
    }

    /**
     * Calculate the absolute value for the determinate of matrix A.
     *
     * @param a float[][] the NxN matrix
     * @return abs(|A|)
     */
    @Override
    public float det(final float[][] a) {
        return asFloat(det(asDouble(a)));
    }

    /**
     * Calculate the absolute value for the determinate of matrix A.
     *
     * @param a double[][] the NxN matrix
     * @return abs(|A|)
     */
    @Override
    public double det(final double[][] a) {
        final int m = a.length, n = a[0].length;
        if (m != n) {
            final String msg = format(DIM_MISSMATCH, m, n);
            throw new IllegalArgumentException(msg);
        }
        final double[] d = new double[min(m + 1, n)];
        svdDecompose(a, null, d, null);
        diagonalCheck(d);
        double det = 1;
        for (int i = 0; i < n; det *= d[i], i++)
            ;
        return det;
    }

    /**
     * Calculate the inverse matrix of A. In case A is singular the
     * pseudo-inverse A+ is returned.
     *
     * @param a matrix to invert
     * @return 1/A the (pseudo) inverse matrix
     */
    @Override
    public double[][] inverse(final double[][] a) {
        final int m = a.length, n = a[0].length;
        if (m != n) {
            final String msg = format(DIM_MISSMATCH, m, n);
            throw new IllegalArgumentException(msg);
        }
        final double[] d = new double[min(m + 1, n)];
        double[][] ap;
        final double[][] u = new double[n][m];
        final double[][] v = new double[m][n];

        svdDecompose(a, u, d, v);
        diagonalCheck(d);
        ap = pseudoInverse(u, d, v);
        final int rank = rank(d);
        if (rank != m) {
            final String msg = format(LOW_RANK, rank, m);
            getLogger().warn(msg);
            checkPseudoInverse(a, ap);
            // throw new IllegalArgumentException(msg);
        }
        return ap;
    }

    /**
     * Solve the equation A*x = y for x.
     *
     * @param a double[][] real NxM matrix.
     * @param y double[] real M dimensional right side vector.
     * @return double[] real M dimensional solution x.
     */
    @Override
    public double[] solve(final double[][] a, final double[] y) {
        final int n = a.length, m = a[0].length;
        if (n < m) {
            final String msg = format(DIM_MISSMATCH, n, m);
            throw new IllegalArgumentException(msg);
        }
        double[][] ap;
        final double[][] u = new double[n][m];
        final double[][] v = new double[m][m];
        final double[] d = new double[m];
        double[] x;

        svdDecompose(a, u, d, v);
        diagonalCheck(d);
        ap = pseudoInverse(u, d, v);

        final int rank = rank(d);
        if (rank != m) {
            final String msg = format(LOW_RANK, rank, m);
            getLogger().warn(msg);
            checkPseudoInverse(a, ap);
            // throw new IllegalArgumentException(msg);
        }
        x = mult(ap, y);
        return x;
    }

    /**
     * Solve the equation A*X = B for X.
     *
     * @param a double[][] real NxM matrix with M &le; N
     * @param b double[][] real MxL right side vectors as matrix
     * @return double[][]  real MxL solution matrix X
     */
    @Override
    public double[][] solve(final double[][] a, final double[][] b) {
        final int n = a.length, m = a[0].length; // nu = min(n,m);
        final int l = b[0].length;
        if (n < m) {
            final String msg = format(DIM_MISSMATCH, n, m);
            throw new IllegalArgumentException(msg);
        }
        if (n != b.length) {
            final String msg = format(DIM_MISSMATCH, n, b.length);
            throw new IllegalArgumentException(msg);
        }
        final double[][] u = new double[n][m];
        final double[][] v = new double[m][m];
        double[][] ap, x = new double[l][m];
        final double[] d = new double[m];

        svdDecompose(a, u, d, v);
        ap = pseudoInverse(u, d, v);
        final int rank = rank(d);
        if (rank != m) {
            final String msg = format(LOW_RANK, rank, m);
            getLogger().warn(msg);
            checkPseudoInverse(a, ap);
            // throw new IllegalArgumentException(msg);
        }
        final double[][] bt = transpose(b);
        for (int k = 0; k < l; x[k] = mult(ap, bt[k]), k++)
            ;
        x = transpose(x);
        return x;
    }

    /**
     * Calculate the pseudo inverse matrix A+ = VxD+xU*.
     *
     * @param u the matrix U
     * @param d the diagonal of matrix D
     * @param v the transpose of matrix V
     * @return
     */
    private double[][] pseudoInverse(final double[][] u, final double[] d, final double[][] v) {
        // calculate the pseudo inverse matrix A+=VxD+xU*.
        final int n = d.length;
        final int m = d.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++)
                if (d[i] != 0) {
                    u[j][i] /= d[i];
                }
        }
        final double[][] ap = mult(v, transpose(u));
        return ap;
    }

    /**
     * Check if the pseudo-inverse had been calculated correctly,
     * which means AxApxA = A.
     *
     * @param a  the original matrix
     * @param ap the pseudo-inverse matrix
     */
    private void checkPseudoInverse(final double[][] a, final double[][] ap) {
        double[][] b = mult(a, mult(ap, a));
        b = LinearAlgebra.sub(b, a);
        final double nr = norm(b);
        if (nr > FEPS) {
            final String msg = format("wrong pseudo inverse norm: %.2g", nr);
            getLogger().error(msg);
            throw new SingularException(msg);
        }
        getLogger().info(format("pseudo inverse check accuracy: %.3g", nr));
    }

    /**
     * Calculate the condition number max(D)/min(D) of matrix A, where
     * D are the eigenvalues.
     *
     * @param a the matrix
     * @return condition number of matrix A
     */
    @Override
    public double cond(final double[][] a) {
        final int n = a.length, m = a[0].length;
        if (n < m) {
            final String msg = format(DIM_MISSMATCH, n, m);
            throw new IllegalArgumentException(msg);
        }
        final double[] d = new double[m];
        svdDecompose(a, null, d, null);
        return cond(d);
    }

    /**
     * Calculate the condition number max(D)/min(D).
     * The eigenvalues have to be specified in descending order
     * within the array d.
     *
     * @param d the eigenvalues
     * @return condition number of matrix A
     */
    private double cond(final double[] d) {
        final int n = d.length;
        final double dmin = d[n - 1];
        if (dmin > 0) {
            return d[0] / dmin;
        }
        return 1.0 / DEPS; // Double.MAX_VALUE;
    }

    /**
     * Calculate the effective rank of the matrix, i.e. the number of
     * numerical not zero eigenvalues.
     *
     * @param a the matrix
     * @return rank of matrix A
     */
    @Override
    public int rank(final double[][] a) {
        final int n = a.length, m = a[0].length;
        if (n < m) {
            final String msg = format(DIM_MISSMATCH, n, m);
            throw new IllegalArgumentException(msg);
        }
        final double[] d = new double[m];
        svdDecompose(a, null, d, null);
        return rank(d);
    }

    /**
     * Calculate the effective rank of the matrix, i.e. the number of
     * numerical not zero eigenvalues.
     *
     * @param d the eigenvalues
     * @return rank of matrix A
     */
    private int rank(final double[] d) {
        final int n = d.length;
        final double tol = n * d[0] * EPS;
        int r = 0;
        for (int i = 0; i < d.length; i++) {
            if (d[i] > tol) {
                r++;
            }
        }
        return r;
    }

    /**
     * Perform the singular value decomposition for matrix a.
     *
     * @param a the NxN matrix A
     * @param u the NxM matrix U
     * @param d the diagonal elements with the eigenvalues
     * @param v the NxN matrix V
     */
    public void svdDecompose(final double[][] a, final double[][] u, final double[] d, final double[][] v) {
        final int rows = a.length;
        final int cols = a[0].length;
        if (rows < cols) {
            throw new IllegalArgumentException(DIM_MISSMATCH);
        }
        final int nu = min(rows, cols);
        final int nct = min(rows - 1, cols);
        final int nrt = max(0, min(cols - 2, rows));
        final double[] e = new double[cols];
        final double[][] aa = copy(a);
        // calculate the upper-diagonal form of A.
        bidiagonal(aa, u, d, e, v);
        // If required, generate U.
        if (u != null)
            generateU(d, u, nu, nct);
        // If required, generate V.
        if (v != null)
            generateV(e, v, nu, nrt);
        updateUDV(d, e, u, v, cols);
        if (roundResult) {
            if (u != null && v != null) {
                roundUVD(u, d, v);
            } else {
                roundEVs(d);
            }
        }
    }

    /**
     * Calculate the upper-diagonal form of A.
     *
     * @param a matrix A
     * @param u matrix U
     * @param d diagonal D
     * @param e upper diagonal elements
     * @param v matrix V
     */
    private void bidiagonal(final double[][] a, final double[][] u, final double[] d, final double[] e,
                            final double[][] v) {
        final boolean wantv = (v != null), wantu = (u != null);
        int i;
        final int rows = a.length, cols = a[0].length;
        final int nct = min(rows - 1, cols), nrt = max(0, min(cols - 2, rows));
        final int maxk = max(nct, nrt);
        double tmp, aik, amax;
        final double[] work = new double[rows];
        for (int k = 0; k < maxk; k++) {
            if (k < nct) {
                // Compute the transformation for the k-th column and
                // place the k-th diagonal in d[k].
                // for (tmp = 0, i = k; i<rows; tmp = norm(tmp, a[i][k]), i++);

                for (tmp = 0, amax = 0, i = k; i < rows; amax = max(amax, abs(a[i][k])), i++)
                    ;
                if (amax > 0) {
                    for (tmp = 0, i = k; i < rows; aik = a[i][k] / amax, tmp += aik * aik, i++)
                        ;
                    tmp = amax * sqrt(tmp);
                    if (a[k][k] < 0.0) {
                        tmp = -tmp;
                    }
                    for (i = k; i < rows; a[i][k] /= tmp, i++)
                        ;
                    a[k][k] += 1.0;
                }
                d[k] = -tmp;
            }
            for (int j = k + 1; j < cols; j++) {
                if ((k < nct) && (d[k] != 0.0)) {
                    // Apply the transformation.
                    for (tmp = 0, i = k; i < rows; tmp += a[i][k] * a[i][j], i++)
                        ;
                    tmp = -tmp / a[k][k];
                    for (i = k; i < rows; a[i][j] += tmp * a[i][k], i++)
                        ;
                }
                // Place the k-th row of A into e for the
                // subsequent calculation of the row transformation.
                e[j] = a[k][j];
            }
            if (wantu && (k < nct)) {
                // Place the transformation in U.
                for (i = k; i < rows; u[i][k] = a[i][k], i++)
                    ;
            }
            if (k < nrt) {
                // Compute the k-th row transformation and place the
                // k-th super-diagonal in e[k].
                // for (tmp=0, i = k+1; i<cols; tmp = norm(tmp, e[i]), i++);
                tmp = norm(k + 1, cols, e);
                if (tmp != 0.0) {
                    if (e[k + 1] < 0.0) {
                        tmp = -tmp;
                    }
                    for (i = k + 1; i < cols; e[i] /= tmp, i++)
                        ;
                    e[k + 1] += 1.0;
                }
                e[k] = -tmp;
                if ((k + 1 < rows) && (e[k] != 0.0)) {
                    // Apply the transformation.
                    for (i = k + 1; i < rows; work[i] = 0.0, i++)
                        ;
                    for (int j = k + 1; j < cols; j++)
                        for (i = k + 1; i < rows; work[i] += e[j] * a[i][j], i++)
                            ;

                    for (int j = k + 1; j < cols; j++)
                        for (tmp = -e[j] / e[k + 1], i = k + 1; i < rows; a[i][j] += tmp * work[i], i++)
                            ;

                }
                if (wantv) {
                    // Place the transformation in V.
                    for (i = k + 1; i < cols; v[i][k] = e[i], i++)
                        ;
                }
            }
        } // k-loop

        // Set up the final bi-diagonal matrix elements.
        if (nct < cols) {
            d[nct] = a[nct][nct];
        }
        if (rows < cols) {
            d[cols - 1] = 0.0;
        }
        if (nrt + 1 < cols) {
            e[nrt] = a[nrt][cols - 1];
        }
        e[cols - 1] = 0.0;
    }

    /**
     * Main decomposition loop for the singular values.
     *
     * @param d    the diagonal elements
     * @param e    the upper diagonal elements
     * @param u    matrix U if not null it will be calculated
     * @param v    matrix V if not null it will be calculated
     * @param cols the number of columns
     */
    private void updateUDV(final double[] d, final double[] e, final double[][] u, final double[][] v, final int cols) {
        final boolean wantUV = (u != null) && (v != null);
        int k, kd, col = cols;
        final int cmax = col - 1;
        double tmp1, tmp2;
        while (col > 0) {
            for (k = col - 2; k >= 0; k--) {
                if (isSmall(e[k], abs(d[k]) + abs(d[k + 1]))) {
                    e[k] = 0.0;
                    break;
                }
            } // k-loop
            if (k == col - 2) {
                // Make the singular values positive and sort them.
                if (wantUV) {
                    order(d, u, v, k + 1, cmax);
                } else {
                    order(d, k + 1, cmax);
                }
                col--;
            } else {
                tmp1 = 0;
                tmp2 = 0;
                for (kd = col - 1; kd > k; tmp1 = 0, tmp2 = 0, kd--) {
                    if (kd != col)
                        tmp1 = abs(e[kd]);
                    if (kd != k + 1)
                        tmp2 = abs(e[kd - 1]);
                    if (isSmall(d[kd], tmp1 + tmp2)) {
                        d[kd] = 0.0;
                        break;
                    }
                }
                if (kd == k) {
                    shift(d, e, u, v, k + 1, col);
                } else if (kd == col - 1) {
                    deflate(d, e, v, k + 1, col);
                } else {
                    k = kd;
                    split(d, e, u, k + 1, col);
                }
            } // else k != col-2
            k++;
        } // while col>0
    }

    /**
     * Initial setup for the V matrix.
     *
     * @param e
     * @param v   NxN matrix V of decomposition
     * @param nu
     * @param nrt
     */
    private void generateV(final double[] e, final double[][] v, final int nu, final int nrt) {
        double t;
        int i, j, k;
        final int n = v.length;
        for (k = n - 1; k >= 0; k--) {
            if ((k < nrt) && (e[k] != 0.0)) {
                for (j = k + 1; j < nu; j++) {
                    for (t = 0, i = k + 1; i < n; t += v[i][k] * v[i][j], i++)
                        ;
                    for (t /= -v[k + 1][k], i = k + 1; i < n; v[i][j] += t * v[i][k], i++)
                        ;
                }
            }
            for (i = 0; i < n; v[i][k] = 0.0, i++)
                ;
            v[k][k] = 1.0;
        }
    }

    /**
     * Initial setup for the U matrix.
     *
     * @param d   the singular values
     * @param u   the U matrix
     * @param nu
     * @param nct
     */
    private void generateU(final double[] d, final double[][] u, final int nu, final int nct) {
        double t;
        int i, j, k;
        final int m = u.length;
        for (j = nct; j < nu; u[j][j] = 1.0, j++)
            for (i = 0; i < m; u[i][j] = 0.0, i++)
                ;

        for (k = nct - 1; k >= 0; k--) {
            if (d[k] != 0.0) {
                for (j = k + 1; j < nu; j++) {
                    for (t = 0, i = k; i < m; t += u[i][k] * u[i][j], i++)
                        ;
                    for (t /= -u[k][k], i = k; i < m; u[i][j] += t * u[i][k], i++)
                        ;
                }
                for (i = k; i < m; u[i][k] = -u[i][k], i++)
                    ;
                u[k][k] += 1.0;
                for (i = 0; i < k - 1; u[i][k] = 0.0, i++)
                    ;
            } else {
                for (i = 0; i < m; u[i][k] = 0.0, i++)
                    ;
                u[k][k] = 1.0;
            }
        }
    }

    /**
     * Make the singular values positive and order them descending.
     *
     * @param d   the singular value diagonal elements
     * @param u   matrix U if not null it will be generated
     * @param v   matrix V if not null it will be generated
     * @param beg minimal index to start the ordering from
     * @param end maximal index to order to
     */
    private void order(final double[] d, final double[][] u, final double[][] v, final int beg, final int end) {
        double tmp;
        int kp;
        final int m = u.length, n = d.length;
        int k = beg;
        // make the singular values positive
        if (d[k] <= 0.0) {
            d[k] = -d[k];
            for (int i = 0; i <= end; v[i][k] = -v[i][k], i++)
                ;
        }
        // Order the singular values.
        for (k = beg, kp = k + 1; k < end; kp++, k++) {
            if (d[k] >= d[kp]) {
                break;
            }
            // swap(d, kp, k);
            tmp = d[k];
            d[k] = d[kp];
            d[kp] = tmp;
            if ((k < n - 1)) {
                swapColumn(v, n, kp, k);
            }
            if ((k < m - 1)) {
                swapColumn(u, m, kp, k);
            }
        }
    }

    /**
     * Make the singular values positive and order them descending.
     *
     * @param d   the singular value diagonal elements
     * @param beg minimal index to start the ordering from
     * @param end maximal index to order to
     */
    private void order(final double[] d, final int beg, final int end) {
        int kp, k = beg;
        double tmp;
        // make the singular values positive
        if (d[k] <= 0.0) {
            d[k] = -d[k];
        }
        // Order the singular values.
        for (k = beg, kp = k + 1; k < end; kp++, k++) {
            if (d[k] >= d[kp]) {
                break;
            }
            // swap(d, kp, k);
            tmp = d[k];
            d[k] = d[kp];
            d[kp] = tmp;
        }
    }

    /**
     * Calculate the shift.
     *
     * @param d   the diagonal elements
     * @param e   the upper diagonal elements
     * @param u   matrix U if not null it will be generated
     * @param v   matrix V if not null it will be generated
     * @param beg start index
     * @param end end index
     */
    private void shift(final double[] d, final double[] e, final double[][] u, final double[][] v, final int start,
                       final int end) {
        final boolean wantv = (v != null);
        final boolean wantu = (u != null);
        int m = 0, j, jp;
        final double scale = maxabs(d[end - 1], d[end - 2], e[end - 2], d[start], e[start]);
        final double sp = d[end - 1] / scale;
        final double spm1 = d[end - 2] / scale;
        final double epm1 = e[end - 2] / scale;
        final double sk = d[start] / scale;
        final double ek = e[start] / scale;
        final double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
        final double c = (sp * epm1) * (sp * epm1);
        double shift = 0.0;
        double cs, sn, sc;
        if (wantu) {
            m = u.length;
        }
        if ((b != 0.0) || (c != 0.0)) {
            shift = sqrt(b * b + c);
            if (b < 0.0) {
                shift = -shift;
            }
            shift = c / (b + shift);
        }
        double f = (sk + sp) * (sk - sp) + shift;
        double g = sk * ek;

        // Chase zeros.

        for (j = start, jp = j + 1; j < end - 1; jp++, j++) {
            sc = hypot(f, g);
            cs = f / sc;
            sn = g / sc;
            if (j != start) {
                e[j - 1] = sc;
            }
            f = cs * d[j] + sn * e[j];
            g = cs * e[j] - sn * d[j];
            e[j] = g;
            g = sn * d[jp];
            d[jp] *= cs;
            if (wantv) {
                givensrotation(v, j, jp, cs, sn);
            }
            sc = hypot(f, g);
            cs = f / sc;
            sn = g / sc;
            d[j] = sc;
            f = cs * e[j] + sn * d[jp];
            g = cs * d[jp] - sn * e[j];
            d[jp] = g;
            g = sn * e[jp];
            e[jp] *= cs;
            if (wantu && (j < m - 1)) {
                givensrotation(u, j, jp, cs, sn);
            }
        }
        e[end - 2] = f;
    }

    /**
     * Split at negligible elements.
     *
     * @param d   the singular value diagonal elements
     * @param e   the upper diagonal elements
     * @param u   matrix U if not null it will be generated
     * @param beg start index
     * @param end end index
     */
    private void split(final double[] d, final double[] e, final double[][] u, final int beg, final int end) {
        final boolean wantu = (u != null);
        int i, j, m = 0;
        double t, cs, sn, f = e[beg - 1];
        if (wantu) {
            m = u.length;
        }
        e[beg - 1] = 0.0;
        for (j = beg; j < end; j++) {
            t = hypot(d[j], f);
            cs = d[j] / t;
            sn = f / t;
            d[j] = t;
            f = -sn * e[j];
            e[j] = cs * e[j];
            if (wantu) {
                for (i = 0; i < m; t = cs * u[i][j] + sn * u[i][beg - 1], u[i][beg - 1] = -sn * u[i][j] + cs * u[i][beg - 1], u[i][j] = t, i++)
                    ;
            }
        }
    }

    /**
     * Deflate negligible diagonal elements.
     *
     * @param d   the diagonal elements
     * @param e   the upper diagonal elements
     * @param v   matrix V if not null it will be generated
     * @param beg start index
     * @param end end index
     */
    private void deflate(final double[] d, final double[] e, final double[][] v, final int start, final int end) {
        int i, j;
        final int n = d.length;
        final boolean wantv = (v != null);
        double cs, sn, t, f = e[end - 2];
        e[end - 2] = 0.0;
        for (j = end - 2; j >= start; j--) {
            t = hypot(d[j], f);
            cs = d[j] / t;
            sn = f / t;
            d[j] = t;
            if (j != start) {
                f = -sn * e[j - 1];
                e[j - 1] = cs * e[j - 1];
            }
            if (wantv) {
                for (i = 0; i < n; t = cs * v[i][j] + sn * v[i][end - 1], v[i][end - 1] = -sn * v[i][j] + cs * v[i][end - 1], v[i][j] = t, i++)
                    ;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#det(de.lab4inf.math.Complex[][])
     */
    @Override
    public <T extends Numeric<T>> T det(final T[][] a) {
        throw new IllegalStateException(NOT_IMPLEMENTED);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(T[][], T[])
     */
    @Override
    public <T extends Numeric<T>> T[] solve(final T[][] a, final T[] y) {
        throw new IllegalStateException(NOT_IMPLEMENTED);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(T[][], T[][])
     */
    @Override
    public <T extends Numeric<T>> T[][] solve(final T[][] a, final T[][] b) {
        throw new IllegalStateException(NOT_IMPLEMENTED);
    }
}
 