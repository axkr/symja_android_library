/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
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

import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.getCol;
import static de.lab4inf.math.lapack.LinearAlgebra.isDiagonalDominant;
import static de.lab4inf.math.lapack.LinearAlgebra.isSquare;
import static de.lab4inf.math.lapack.LinearAlgebra.maxnorm;
import static de.lab4inf.math.lapack.LinearAlgebra.setCol;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

/**
 * Generic matrix solver using Gauss algorithm.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: GaussSolver.java,v 1.54 2014/12/08 15:21:05 nwulff Exp $
 * @since 31.03.2008
 */
public class GaussSolver extends LASolver {
    /**
     *
     */
    private static final String RESIDUAL_ERROR_CORRECTED = "Global Residual Error %.2e corrected to %.2e ratio=%f";
    /**
     * Reference to GaussSolver.java.
     */
    private static final String GLOBAL_RESIDUAL_ERROR_CHANGED = "Global Residual Error %.2e without pivot changed to %.2e ratio=%f";
    /**
     * reference to the RESIDUAL_ERROR attribute.
     */
    private static final String RESIDUAL_ERROR = "residual error %g";
    /**
     * reference to the RESIDUAL_CHANGED attribute.
     */
    private static final String RESIDUAL_CHANGED = "residual changed to %g ";
    /**
     * debug flag during residual test phase.
     */
    private static final boolean GDEBUG = false;

    /**
     * POJO constructor.
     */
    public GaussSolver() {
        setUsingPivot(true);
    }

    /**
     * Check if the given matrix A is symmetric.
     *
     * @param a NxN matrix to check.
     */
    private static void matrixCheck(final float[][] a) {
        if (!isSquare(a)) {
            String msg = NOT_SQUARE;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            // getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Check if the given matrix A is symmetric.
     *
     * @param a NxN matrix to check.
     */
    private static void matrixCheck(final double[][] a) {
        if (!isSquare(a)) {
            String msg = NOT_SQUARE;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            // getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Check if the given matrix A is symmetric.
     *
     * @param a NxN matrix to check.
     */
    private static <T> void matrixCheck(final T[][] a) {
        if (!isSquare(a)) {
            String msg = NOT_SQUARE;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            // getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Calculate the determinant without pivot search.
     *
     * @param a NxN matrix
     * @return det(A)
     */
    private static float detWithoutPivot(final float[][] a) {
        int n = a.length;
        float[][] ma = copy(a);
        // decompose A = L*R into left and right triangular matrix
        lrdecompose(ma);
        float det = ma[0][0];
        // determinant is product of diagonal elements
        for (int i = 1; i < n; det *= ma[i][i], i++)
            ;
        return det;
    }

    /**
     * Count the number of permutations within the pivot array p.
     *
     * @param p the pivot array
     * @return number of permutations
     */
    private static int permutations(final int[] p) {
        int permuts = 0, n = p.length;
        for (int j = 0; j < n; j++)
            for (int i = 0; i < j; i++)
                if (p[i] > p[j]) {
                    permuts++;
                }
        return permuts;
    }

    /**
     * Calculate the determinant with pivot search.
     *
     * @param a NxN matrix
     * @return det(A)
     */
    private static float detWithPivot(final float[][] a) {
        int n = a.length;
        float[][] ma = copy(a);
        int[] p = new int[n];
        matrixCheck(a);
        // decompose A = L*R into left and right triangular matrix
        lrdecompose(ma, p);
        float det = 1;
        // determinant is product of diagonal elements
        for (int i = 0; i < n; det *= ma[p[i]][i], i++)
            ;
        // change sign if odd number of permutations
        if ((permutations(p) & 1) == 1) {
            det = -det;
        }
        return det;
    }

    /**
     * Calculate the determinant with pivot search.
     *
     * @param a   NxN complex matrix
     * @param <T> the type of the numeary
     * @return det(A)
     */
    private static <T extends Numeric<T>> T detWithPivot(final T[][] a) {
        int n = a.length;
        T[][] ma = copy(a);
        int[] p = new int[n];
        matrixCheck(a);
        // decompose A = L*R into left and right triangular matrix
        lrdecompose(ma, p);
        T det = ma[p[0]][0];
        // determinant is product of diagonal elements
        for (int i = 1; i < n; det = det.multiply(ma[p[i]][i]), i++)
            ;
        // change sign if odd number of permutations
        if ((permutations(p) & 1) == 1) {
            final T minusOne = det.getMinusOne();
            det = det.multiply(minusOne);
        }
        return det;
    }

    /**
     * Calculate the determinant without pivot search.
     *
     * @param a NxN matrix
     * @return det(A)
     */
    private static <T extends Numeric<T>> T detWithoutPivot(final T[][] a) {
        int n = a.length;
        T[][] ma = copy(a);
        // decompose A = L*R into left and right triangular matrix
        lrdecompose(ma);
        T det = ma[0][0];
        // determinant is product of diagonal elements
        for (int i = 1; i < n; det = det.multiply(ma[i][i]), i++)
            ;
        return det;
    }

    /**
     * Internal helper method to perform the L*R decomposition.
     * It has a runtime complexity of order 2/3n**3
     *
     * @param a double[][] the matrix to decompose.
     */
    private static void lrdecompose(final float[][] a) {
        int n = a.length;
        int k;
        float aii, aij, aji;
        float[] d = new float[n];
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; a[i][j] = aij, j++)
                for (k = 0, aij = a[i][j]; k < i; aij -= a[i][k] * a[k][j], k++)
                    ;

            aii = a[i][i];
            d[i] = aii;
            checkRegular(i, aii);
            for (int j = i + 1; j < n; a[j][i] = aji / aii, j++) {
                for (k = 0, aji = a[j][i]; k < i; aji -= a[j][k] * a[k][i], k++)
                    ;
            }
        }
        diagonalCheck(d);
    }

    /**
     * Internal helper method to perform the L*R decomposition.
     * It has a runtime complexity of order 2/3n**3
     *
     * @param a   T[][] the matrix to decompose.
     * @param <T> the type of the numeary
     */
    private static <T extends Numeric<T>> void lrdecompose(final T[][] a) {
        int n = a.length;
        int k;
        T caii, caij, caji;
        T[] cd = create(a[0][0], n);
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; a[i][j] = caij, j++)
                for (k = 0, caij = a[i][j]; k < i; caij = caij.minus(a[i][k].multiply(a[k][j])), k++)
                    ;

            caii = a[i][i];
            cd[i] = caii;
            checkRegular(i, caii);
            for (int j = i + 1; j < n; a[j][i] = caji.div(caii), j++) {
                for (k = 0, caji = a[j][i]; k < i; caji = caji.minus(a[j][k].multiply(a[k][i])), k++)
                    ;
            }
        }
        diagonalCheck(cd);

    }

    /**
     * Set the actual pivot element.
     *
     * @param m integer the column/row index to start with
     * @param p int[] array with the pivot indexes
     * @param a double[][] the matrix
     */
    private static void pivot(final int m, final int[] p, final float[][] a) {
        int j, k, n = a.length;
        float sk, skk;
        float[] s = new float[n];
        // calculate the scaling to avoid fake pivot elements
        for (k = m; k < n; s[k] = sk, k++)
            for (sk = abs(a[p[k]][m]), j = m + 1; j < n; sk = max(sk, abs(a[p[k]][j])), j++)
                ;
        // compare the scaled pivot elements
        sk = abs(a[p[m]][m]) / s[m];
        for (k = m + 1; k < n; k++) {
            skk = abs(a[p[k]][m]) / s[k];
            if (skk > sk) {
                int pp = p[m];
                p[m] = p[k];
                p[k] = pp;
                sk = skk;
            }
        }
    }

    /**
     * Set the actual pivot element.
     *
     * @param m integer the column/row index to start with
     * @param p int[] array with the pivot indexes
     * @param a double[][] the matrix
     */
    private static void pivot(final int m, final int[] p, final double[][] a) {
        int j, k, n = a.length;
        double sk, skk;
        double[] s = new double[n];
        // calculate the scaling to avoid fake pivot elements
        for (k = m; k < n; s[k] = sk, k++)
            for (sk = abs(a[p[k]][m]), j = m + 1; j < n; sk = max(sk, abs(a[p[k]][j])), j++)
                ;
        // compare the scaled pivot elements
        sk = abs(a[p[m]][m]) / s[m];
        for (k = m + 1; k < n; k++) {
            skk = abs(a[p[k]][m]) / s[k];
            if (skk > sk) {
                int pp = p[m];
                p[m] = p[k];
                p[k] = pp;
                sk = skk;
            }
        }
    }

    /**
     * Set the actual pivot element.
     *
     * @param m integer the column/row index to start with
     * @param p int[] array with the pivot indexes
     * @param a T[][] the matrix
     * @paran <T> the type of the numeary
     */
    private static <T extends Numeric<T>> void pivot(final int m, final int[] p, final T[][] a) {
        int j, k, n = a.length;
        double sk, skk;
        double[] s = new double[n];
        // calculate the scaling to avoid fake pivot elements
        for (k = m; k < n; s[k] = sk, k++)
            for (sk = a[p[k]][m].abs().doubleValue(), j = m + 1; j < n; sk = max(sk, a[p[k]][j].abs().doubleValue()), j++)
                ;
        // compare the scaled pivot elements
        sk = a[p[m]][m].abs().doubleValue() / s[m];
        for (k = m + 1; k < n; k++) {
            skk = a[p[k]][m].abs().doubleValue() / s[k];
            if (skk > sk) {
                int pp = p[m];
                p[m] = p[k];
                p[k] = pp;
                sk = skk;
            }
        }
    }

    /**
     * LR decomposition with pivot search.
     *
     * @param a double[][] the matrix
     * @param p int[] array with the pivot indexes
     */
    private static void lrdecompose(final float[][] a, final int[] p) {
        int i, j, k, n = a.length;
        float aii, aij, aji;
        for (i = 0; i < n; p[i] = i, i++)
            ;
        float[] d = new float[n];

        for (i = 0; i < n; i++) {
            pivot(i, p, a);
            for (j = i; j < n; a[p[i]][j] = aij, j++)
                for (k = 0, aij = a[p[i]][j]; k < i; aij -= a[p[i]][k] * a[p[k]][j], k++)
                    ;

            aii = a[p[i]][i];
            d[i] = aii;
            checkRegular(i, aii);
            for (j = i + 1; j < n; a[p[j]][i] = aji / aii, j++)
                for (k = 0, aji = a[p[j]][i]; k < i; aji -= a[p[j]][k] * a[p[k]][i], k++)
                    ;

        }
        diagonalCheck(d);
    }

    /**
     * LR decomposition with pivot search.
     *
     * @param a double[][] the matrix
     * @param p int[] array with the pivot indexes
     * @paran <T> the type of the numeary
     */
    private static <T extends Numeric<T>> void lrdecompose(final T[][] a, final int[] p) {
        int i, j, k, n = a.length;
        T aii, aij, aji;
        for (i = 0; i < n; p[i] = i, i++)
            ;
        T[] d = create(a[0][0], n);

        for (i = 0; i < n; i++) {
            pivot(i, p, a);
            for (j = i; j < n; a[p[i]][j] = aij, j++)
                for (k = 0, aij = a[p[i]][j]; k < i; aij = aij.minus(a[p[i]][k].multiply(a[p[k]][j])), k++)
                    ;

            aii = a[p[i]][i];
            d[i] = aii;
            checkRegular(i, aii.abs().doubleValue());
            for (j = i + 1; j < n; a[p[j]][i] = aji.div(aii), j++)
                for (k = 0, aji = a[p[j]][i]; k < i; aji = aji.minus(a[p[j]][k].multiply(a[p[k]][i])), k++)
                    ;

        }
        diagonalCheck(d);
    }

    /**
     * Residual correction for a matrix equation A*x = y with pivot exchange.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition of A
     * @param y  the right side vector
     * @param x  the left side solution vector
     * @param p  the pivot vector
     */
    private static float[] residualCorrection(final float[][] a, final float[][] lr, final float[] y, final float[] x,
                                              final int[] p) {
        int i, j, n = a.length;
        float tmp;
        float[] z, dx = new float[n];
        float[][] l = lr, r = lr;
        z = residual(a, y, x);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[p[i]] = tmp, i++)
            for (tmp = z[p[i]], j = 0; j < i; tmp -= l[p[i]][j] * z[p[j]], j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[p[j]] = tmp, dx[j] = tmp / r[p[j]][j], x[j] += dx[j], j--)
            for (tmp = z[p[j]], i = j + 1; i < n; tmp -= r[p[j]][i] * dx[i], i++)
                ;

        return x;
    }

    /**
     * Residual correction for a matrix equation A*x = y with pivot exchange.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition of A
     * @param y  the right side vector
     * @param x  the left side solution vector
     * @param p  the pivot vector
     */
    private static double[] residualCorrection(final double[][] a, final double[][] lr, final double[] y,
                                               final double[] x, final int[] p) {
        int i, j, n = a.length;
        double tmp;
        double[] z, dx = new double[n];
        double[][] l = lr, r = lr;
        z = residual(a, y, x);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[p[i]] = tmp, i++)
            for (tmp = z[p[i]], j = 0; j < i; tmp -= l[p[i]][j] * z[p[j]], j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[p[j]] = tmp, dx[j] = tmp / r[p[j]][j], x[j] += dx[j], j--)
            for (tmp = z[p[j]], i = j + 1; i < n; tmp -= r[p[j]][i] * dx[i], i++)
                ;

        return x;
    }

    /**
     * Residual correction for a matrix equation A*x = y with pivot exchange.
     *
     * @param a   the original matrix
     * @param lr  the LR decomposition of A
     * @param y   the right side vector
     * @param x   the left side solution vector
     * @param p   the pivot vector
     * @param <T> the type of the elements
     */
    private static <T extends Numeric<T>> T[] residualCorrection(final T[][] a, final T[][] lr, final T[] y,
                                                                 final T[] x, final int[] p) {
        int i, j, n = a.length;
        T tmp;
        T[] z, dx = create(a, n);
        T[][] l = lr, r = lr;
        z = residual(a, y, x);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[p[i]] = tmp, i++)
            for (tmp = z[p[i]], j = 0; j < i; tmp = tmp.minus(l[p[i]][j].multiply(z[p[j]])), j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[p[j]] = tmp, dx[j] = tmp.div(r[p[j]][j]), x[j] = x[j].plus(dx[j]), j--)
            for (tmp = z[p[j]], i = j + 1; i < n; tmp = tmp.minus(r[p[j]][i].multiply(dx[i])), i++)
                ;

        return x;
    }

    /**
     * Residual correction for a matrix equation A*x = y.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition of A
     * @param y  the right side vector
     * @param x  the left side solution vector
     */
    private static float[] residualCorrection(final float[][] a, final float[][] lr, final float[] y, final float[] x) {
        int i, j, n = a.length;
        float tmp;
        float[] z, dx = new float[n];
        float[][] l = lr, r = lr;
        z = residual(a, y, x);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[i] = tmp, i++)
            for (tmp = z[i], j = 0; j < i; tmp -= l[i][j] * z[j], j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[j] = tmp, dx[j] = tmp / r[j][j], x[j] += dx[j], j--)
            for (tmp = z[j], i = j + 1; i < n; tmp -= r[j][i] * dx[i], i++)
                ;

        return x;
    }

    /**
     * Residual correction for a matrix equation A*x = y.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition of A
     * @param y  the right side vector
     * @param x  the left side solution vector
     */
    private static double[] residualCorrection(final double[][] a, final double[][] lr, final double[] y,
                                               final double[] x) {
        int i, j, n = a.length;
        double tmp;
        double[] z, dx = new double[n];
        double[][] l = lr, r = lr;
        z = residual(a, y, x);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[i] = tmp, i++)
            for (tmp = z[i], j = 0; j < i; tmp -= l[i][j] * z[j], j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[j] = tmp, dx[j] = tmp / r[j][j], x[j] += dx[j], j--)
            for (tmp = z[j], i = j + 1; i < n; tmp -= r[j][i] * dx[i], i++)
                ;

        return x;
    }

    /**
     * Residual correction for a matrix equation A*x = y.
     *
     * @param a   the original matrix
     * @param lr  the LR decomposition of A
     * @param y   the right side vector
     * @param x   the left side solution vector
     * @param <T> the type of the elements
     */
    private static <T extends Numeric<T>> T[] residualCorrection(final T[][] a, final T[][] lr, final T[] y, final T[] x) {
        int i, j, n = a.length;
        T tmp;
        T[] z, dx = create(x, n);
        T[][] l = lr, r = lr;
        z = residual(a, y, x);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[i] = tmp, i++)
            for (tmp = z[i], j = 0; j < i; tmp = tmp.minus(l[i][j].multiply(z[j])), j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[j] = tmp, dx[j] = tmp.div(r[j][j]), x[j] = x[j].plus(dx[j]), j--)
            for (tmp = z[j], i = j + 1; i < n; tmp = tmp.minus(r[j][i].multiply(dx[i])), i++)
                ;

        return x;
    }

    /**
     * Residual correction for a multi matrix equation A*X = B.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition
     * @param b  the right side matrix
     * @param x  the left side solution matrix
     */
    private static void residualCorrection(final float[][] a, final float[][] lr, final float[][] b, final float[][] x) {
        int m = b[0].length;
        for (int k = 0; k < m; k++)
            setCol(x, k, residualCorrection(a, lr, getCol(b, k), getCol(x, k)));
    }

    /**
     * Residual correction for a multi matrix equation A*X = B.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition
     * @param b  the right side matrix
     * @param x  the left side solution matrix
     */
    private static void residualCorrection(final double[][] a, final double[][] lr, final double[][] b,
                                           final double[][] x) {
        int m = b[0].length;
        for (int k = 0; k < m; k++)
            setCol(x, k, residualCorrection(a, lr, getCol(b, k), getCol(x, k)));
    }

    /**
     * Residual correction for a multi matrix equation A*X = B.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition
     * @param b  the right side matrix
     * @param x  the left side solution matrix
     * @param p  the pivot vector
     */
    private static <T extends Numeric<T>> void residualCorrection(final T[][] a, final T[][] lr, final T[][] b,
                                                                  final T[][] x) {
        int m = b[0].length;
        for (int k = 0; k < m; k++)
            setCol(x, k, residualCorrection(a, lr, getCol(b, k), getCol(x, k)));
    }

    /**
     * Residual correction for a multi matrix equation A*X = B with pivot exchange.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition
     * @param b  the right side matrix
     * @param x  the left side solution matrix
     * @param p  the pivot vector
     */
    private static void residualCorrection(final float[][] a, final float[][] lr, final float[][] b, final float[][] x,
                                           final int[] p) {
        int m = b[0].length;
        for (int k = 0; k < m; k++)
            setCol(x, k, residualCorrection(a, lr, getCol(b, k), getCol(x, k), p));
    }

    /**
     * Residual correction for a multi matrix equation A*X = B with pivot exchange.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition
     * @param b  the right side matrix
     * @param x  the left side solution matrix
     * @param p  the pivot vector
     */
    private static void residualCorrection(final double[][] a, final double[][] lr, final double[][] b,
                                           final double[][] x, final int[] p) {
        int m = b[0].length;
        for (int k = 0; k < m; k++)
            setCol(x, k, residualCorrection(a, lr, getCol(b, k), getCol(x, k), p));
    }

    /**
     * Residual correction for a multi matrix equation A*X = B with pivot exchange.
     *
     * @param a  the original matrix
     * @param lr the LR decomposition
     * @param b  the right side matrix
     * @param x  the left side solution matrix
     * @param p  the pivot vector
     */
    private static <T extends Numeric<T>> void residualCorrection(final T[][] a, final T[][] lr, final T[][] b,
                                                                  final T[][] x, final int[] p) {
        int m = b[0].length;
        for (int k = 0; k < m; k++)
            setCol(x, k, residualCorrection(a, lr, getCol(b, k), getCol(x, k), p));
    }

    /**
     * Internal check if pivot search for matrix A should be used.
     * This is true if the matrix is not diagonal dominant.
     *
     * @param a float[][] the NxN matrix
     * @return boolean true if pivoting required
     */
    private boolean shouldUsePivot(final float[][] a) {
        return isUsingPivot() && !isDiagonalDominant(a);
    }

    /**
     * Internal check if pivot search for matrix A should be used.
     * This is true if the matrix is not diagonal dominant.
     *
     * @param a double[][] the NxN matrix
     * @return boolean true if pivoting required
     */
    private boolean shouldUsePivot(final double[][] a) {
        return isUsingPivot() && !isDiagonalDominant(a);
    }

    /**
     * Internal check if pivot search for matrix A should be used.
     * This is true if the matrix is not diagonal dominant.
     *
     * @param a   T[][] the NxN matrix
     * @param <T> the type of the elements
     * @return boolean true if pivoting required
     */
    private <T extends Numeric<T>> boolean shouldUsePivot(final T[][] a) {
        return isUsingPivot() && !isDiagonalDominant(a);
    }

    /**
     * Calculate the determinate of matrix A.
     *
     * @param a double[][] the NxN matrix
     * @return double the determinant of A
     */
    public float det(final float[][] a) {
        if (shouldUsePivot(a)) {
            return detWithPivot(a);
        }
        return detWithoutPivot(a);
    }

    /**
     * Calculate the determinate of matrix A.
     *
     * @param a double[][] the NxN matrix
     * @return double the determinant of A
     */
    @Override
    public double det(final double[][] a) {
        if (shouldUsePivot(a)) {
            return detWithPivot(a);
        }
        return detWithoutPivot(a);
    }

    /**
     * Calculate the determinate of the  matrix A.
     *
     * @param a   T[][] the NxN matrix
     * @param <T> the elements type
     * @return T the determinant of A
     */
    @Override
    public <T extends Numeric<T>> T det(final T[][] a) {
        if (shouldUsePivot(a)) {
            return detWithPivot(a);
        }
        return detWithoutPivot(a);
    }

    /**
     * Solve the equation A*x = y for x.
     *
     * @param a float[][] real NxN matrix.
     * @param y float[] real N dimensional right side vector.
     * @return float[] real N dimensional solution x.
     */
    @Override
    public float[] solve(final float[][] a, final float[] y) {
        if (shouldUsePivot(a)) {
            return solveWithPivot(a, y);
        }
        return solveWithoutPivot(a, y);
    }

    /**
     * Solve the equation A*x = y for x.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    @Override
    public double[] solve(final double[][] a, final double[] y) {
        if (shouldUsePivot(a)) {
            return solveWithPivot(a, y);
        }
        return solveWithoutPivot(a, y);
    }

    /**
     * Solve the equation A*x = y for x.
     *
     * @param a   type T NxN matrix.
     * @param y   N dimensional right side vector.
     * @param <T> the elements type
     * @return N dimensional solution x.
     */
    @Override
    public <T extends Numeric<T>> T[] solve(final T[][] a, final T[] y) {
        if (shouldUsePivot(a)) {
            return solveWithPivot(a, y);
        }
        return solveWithoutPivot(a, y);
    }

    /**
     * Solve the equation A*X = Y for X.
     *
     * @param a   Complex NxN matrix.
     * @param y   Complex NxN dimensional right side vectors as matrix.
     * @param <T> the elements type
     * @return Complex NxN dimensional solution matrix.
     */
    @Override
    public <T extends Numeric<T>> T[][] solve(final T[][] a, final T[][] y) {
        if (shouldUsePivot(a)) {
            return solveWithPivot(a, y);
        }
        return solveWithoutPivot(a, y);
    }

    /**
     * Solve the equation A*X = B for X.
     *
     * @param a float[][] real NxN matrix
     * @param b float[][] real NxN right side vectors as matrix
     * @return float[][] X solution matrix
     */
    @Override
    public float[][] solve(final float[][] a, final float[][] b) {
        if (shouldUsePivot(a)) {
            return solveWithPivot(a, b);
        }
        return solveWithoutPivot(a, b);
    }

    /**
     * Solve the equation A*X = B for X.
     *
     * @param a double[][] real NxN matrix
     * @param b double[][] real NxN right side vectors as matrix
     * @return double[][] X solution matrix
     */
    @Override
    public double[][] solve(final double[][] a, final double[][] b) {
        if (shouldUsePivot(a)) {
            return solveWithPivot(a, b);
        }
        return solveWithoutPivot(a, b);
    }

    /**
     * Calculate the determinant without pivot search.
     *
     * @param a NxN matrix
     * @return det(A)
     */
    private double detWithoutPivot(final double[][] a) {
        int n = a.length;
        double[][] ma = copy(a);
        // decompose A = L*R into left and right triangular matrix
        lrdecompose(ma);
        double det = ma[0][0];
        // determinant is product of diagonal elements
        for (int i = 1; i < n; det *= ma[i][i], i++)
            ;
        return det;
    }

    /**
     * Calculate the determinant with pivot search.
     *
     * @param a NxN matrix
     * @return det(A)
     */
    private double detWithPivot(final double[][] a) {
        int n = a.length;
        double[][] ma = copy(a);
        int[] p = new int[n];
        matrixCheck(a);
        // decompose A = L*R into left and right triangular matrix
        lrdecompose(ma, p);
        double det = 1;
        // determinant is product of diagonal elements
        for (int i = 0; i < n; det *= ma[p[i]][i], i++)
            ;
        // change sign if odd number of permutations
        if ((permutations(p) & 1) == 1) {
            det = -det;
        }
        return det;
    }

    /**
     * Solve the equation A*x = y without pivot search.
     *
     * @param a double[][] real NxN matrix
     * @param y double[] real right side vector
     * @return double[] x solution vector
     */
    private float[] solveWithoutPivot(final float[][] a, final float[] y) {
        int i, j, n = a.length;
        float[][] ma = copy(a);
        float[][] r = ma, l = ma;
        float[] x = new float[n];
        float[] z = copy(y);
        float tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[i] = tmp, i++)
            for (tmp = z[i], j = 0; j < i; tmp -= l[i][j] * z[j], j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[j] = tmp, x[j] = tmp / r[j][j], j--)
            for (tmp = z[j], i = j + 1; i < n; tmp -= r[j][i] * x[i], i++)
                ;

        float[] dy = residual(a, y, x);
        float resErr = maxnorm(dy);
        if (resErr > getResidualError() * maxnorm(y)) {
            residualCorrection(a, ma, y, x);
            if (GDEBUG) {
                getLogger().warn(format(RESIDUAL_ERROR, resErr));
                resErr = maxnorm(residual(a, y, x));
                getLogger().warn(format(RESIDUAL_CHANGED, resErr));
            }
        }

        return x;
    }

    /**
     * Solve the equation A*x = y without pivot search.
     *
     * @param a double[][] real NxN matrix
     * @param y double[] real right side vector
     * @return double[] x solution vector
     */
    private double[] solveWithoutPivot(final double[][] a, final double[] y) {
        int i, j, n = a.length;
        double[][] ma = copy(a);
        double[][] r = ma, l = ma;
        double[] x = new double[n];
        double[] z = copy(y);
        double tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[i] = tmp, i++)
            for (tmp = z[i], j = 0; j < i; tmp -= l[i][j] * z[j], j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[j] = tmp, x[j] = tmp / r[j][j], j--)
            for (tmp = z[j], i = j + 1; i < n; tmp -= r[j][i] * x[i], i++)
                ;

        double[] dy = residual(a, y, x);
        double resErr = maxnorm(dy);
        if (resErr > getResidualError() * maxnorm(y)) {
            residualCorrection(a, ma, y, x);
            if (GDEBUG) {
                getLogger().warn(format(RESIDUAL_ERROR, resErr));
                resErr = maxnorm(residual(a, y, x));
                getLogger().warn(format(RESIDUAL_CHANGED, resErr));
            }
        }

        return x;
    }

    /**
     * Solve the equation A*x = y without pivot search.
     *
     * @param a   type T[][] NxN matrix
     * @param y   T[] real right side vector
     * @param <T> the type of the numeary
     * @return T[] x solution vector
     */
    private <T extends Numeric<T>> T[] solveWithoutPivot(final T[][] a, final T[] y) {
        int i, j, n = a.length;
        T[][] ma = copy(a);
        T[][] r = ma, l = ma;
        T[] x = create(y[0], n);
        T[] z = copy(y);
        T tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[i] = tmp, i++)
            for (tmp = z[i], j = 0; j < i; tmp = tmp.minus(l[i][j].multiply(z[j])), j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[j] = tmp, x[j] = tmp.div(r[j][j]), j--)
            for (tmp = z[j], i = j + 1; i < n; tmp = tmp.minus(r[j][i].multiply(x[i])), i++)
                ;

        T[] dy = residual(a, y, x);
        double resErr = maxnorm(dy);
        if (resErr > getResidualError() * maxnorm(y)) {
            residualCorrection(a, ma, y, x);
            if (GDEBUG) {
                getLogger().warn(format(RESIDUAL_ERROR, resErr));
                resErr = maxnorm(residual(a, y, x));
                getLogger().warn(format(RESIDUAL_CHANGED, resErr));
            }
        }

        return x;
    }

    /**
     * Solve the equation A*x = y with pivot search.
     *
     * @param a double[][] real NxN matrix
     * @param y double[] real right side vector
     * @return double[] x solution vector
     */
    private float[] solveWithPivot(final float[][] a, final float[] y) {
        int i, j, n = a.length;
        int[] p = new int[n];
        float[][] ma = copy(a);
        float[][] l = ma, r = ma;
        float[] x = new float[n];
        float[] z = copy(y);
        float tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma, p);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[p[i]] = tmp, i++)
            for (tmp = z[p[i]], j = 0; j < i; tmp -= l[p[i]][j] * z[p[j]], j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[p[j]] = tmp, x[j] = tmp / r[p[j]][j], j--)
            for (tmp = z[p[j]], i = j + 1; i < n; tmp -= r[p[j]][i] * x[i], i++)
                ;

        float[] dy = residual(a, y, x);
        float resErr = maxnorm(dy);
        if (resErr > getResidualError() * maxnorm(y)) {
            residualCorrection(a, ma, y, x, p);
            if (GDEBUG) {
                getLogger().warn(format(RESIDUAL_ERROR, resErr));
                resErr = maxnorm(residual(a, y, x));
                getLogger().warn(format(RESIDUAL_CHANGED, resErr));
            }
        }

        return x;
    }

    /**
     * Solve the equation A*x = y with pivot search.
     *
     * @param a double[][] real NxN matrix
     * @param y double[] real right side vector
     * @return double[] x solution vector
     */
    private double[] solveWithPivot(final double[][] a, final double[] y) {
        int i, j, n = a.length;
        int[] p = new int[n];
        double[][] ma = copy(a);
        double[][] l = ma, r = ma;
        double[] x = new double[n];
        double[] z = copy(y);
        double tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma, p);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[p[i]] = tmp, i++)
            for (tmp = z[p[i]], j = 0; j < i; tmp -= l[p[i]][j] * z[p[j]], j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[p[j]] = tmp, x[j] = tmp / r[p[j]][j], j--)
            for (tmp = z[p[j]], i = j + 1; i < n; tmp -= r[p[j]][i] * x[i], i++)
                ;

        double[] dy = residual(a, y, x);
        double resErr = maxnorm(dy);
        if (resErr > getResidualError() * maxnorm(y)) {
            residualCorrection(a, ma, y, x, p);
            if (GDEBUG) {
                getLogger().warn(format(RESIDUAL_ERROR, resErr));
                resErr = maxnorm(residual(a, y, x));
                getLogger().warn(format(RESIDUAL_CHANGED, resErr));
            }
        }

        return x;
    }

    /**
     * Solve the equation A*x = y with pivot search.
     *
     * @param a   type T[][] NxN matrix
     * @param y   T[] right side vector
     * @param <T> the type of the numeary
     * @return T[] x solution vector
     */
    private <T extends Numeric<T>> T[] solveWithPivot(final T[][] a, final T[] y) {
        int i, j, n = a.length;
        int[] p = new int[n];
        T[][] ma = copy(a);
        T[][] l = ma, r = ma;
        T[] x = create(y[0], n);
        T[] z = copy(y);
        T tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma, p);
        // forward elimination: R*x = L^{-1}*y = z
        for (i = 0; i < n; z[p[i]] = tmp, i++)
            for (tmp = z[p[i]], j = 0; j < i; tmp = tmp.minus(l[p[i]][j].multiply(z[p[j]])), j++)
                ;

        // backward elimination: z=R*x => x
        for (j = n - 1; j >= 0; z[p[j]] = tmp, x[j] = tmp.div(r[p[j]][j]), j--)
            for (tmp = z[p[j]], i = j + 1; i < n; tmp = tmp.minus(r[p[j]][i].multiply(x[i])), i++)
                ;

        T[] dy = residual(a, y, x);
        double resErr = maxnorm(dy);
        if (resErr > getResidualError() * maxnorm(y)) {
            residualCorrection(a, ma, y, x, p);
            if (GDEBUG) {
                getLogger().warn(String.format(RESIDUAL_ERROR, resErr));
                resErr = maxnorm(residual(a, y, x));
                getLogger().warn(String.format(RESIDUAL_CHANGED, resErr));
            }
        }

        return x;
    }

    /**
     * Solve the equation A*x=b without pivot.
     *
     * @param a NxN matrix
     * @param b NxM right side
     * @return x NxM solution vectors
     */
    private float[][] solveWithoutPivot(final float[][] a, final float[][] b) {
        int i, j, k, n = a.length, m = b[0].length;
        final float[][] ma = copy(a);
        final float[][] mb = copy(b);
        final float[][] x = new float[n][m];
        float[][] l = ma, r = ma;
        float tmp;
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma);
        // forward elimination: R*x = L^{-1}*y = z
        for (k = 0; k < m; k++)
            for (i = 0; i < n; mb[i][k] = tmp, i++)
                for (tmp = mb[i][k], j = 0; j < i; tmp -= l[i][j] * mb[j][k], j++)
                    ;

        // backward elimination: z=R*x => x = R^{-1}*z
        for (k = 0; k < m; k++)
            for (j = n - 1; j >= 0; mb[j][k] = tmp, x[j][k] = tmp / r[j][j], j--)
                for (tmp = mb[j][k], i = j + 1; i < n; tmp -= r[j][i] * x[i][k], i++)
                    ;

        // residual check and optional correction
        float resErr = LinearAlgebra.norm1(residual(a, b, x));
        if (resErr > getResidualError()) {
            residualCorrection(a, ma, b, x);
            double corrected = LinearAlgebra.norm1(residual(a, b, x));
            getLogger().warn(String.format(GLOBAL_RESIDUAL_ERROR_CHANGED, resErr, corrected, corrected / resErr));
        }
        return x;
    }

    /**
     * Solve the equation A*x=b without pivot.
     *
     * @param a NxN matrix
     * @param b NxM right side
     * @return x NxM solution vectors
     */
    private double[][] solveWithoutPivot(final double[][] a, final double[][] b) {
        int i, j, k, n = a.length, m = b[0].length;
        final double[][] ma = copy(a);
        final double[][] mb = copy(b);
        final double[][] x = new double[n][m];
        double[][] l = ma, r = ma;
        double tmp;
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma);
        // forward elimination: R*x = L^{-1}*y = z
        for (k = 0; k < m; k++)
            for (i = 0; i < n; mb[i][k] = tmp, i++)
                for (tmp = mb[i][k], j = 0; j < i; tmp -= l[i][j] * mb[j][k], j++)
                    ;

        // backward elimination: z=R*x => x = R^{-1}*z
        for (k = 0; k < m; k++)
            for (j = n - 1; j >= 0; mb[j][k] = tmp, x[j][k] = tmp / r[j][j], j--)
                for (tmp = mb[j][k], i = j + 1; i < n; tmp -= r[j][i] * x[i][k], i++)
                    ;

        // residual check and optional correction
        double resErr = LinearAlgebra.norm1(residual(a, b, x));
        if (resErr > getResidualError()) {
            residualCorrection(a, ma, b, x);
            double corrected = LinearAlgebra.norm1(residual(a, b, x));
            getLogger().warn(String.format(GLOBAL_RESIDUAL_ERROR_CHANGED, resErr, corrected, corrected / resErr));
        }
        return x;
    }

    /**
     * Solve the equation A*x=b without pivot.
     *
     * @param a   NxN matrix
     * @param b   NxM right side
     * @param <T> the type of the numeary
     * @return x NxM solution vectors
     */
    private <T extends Numeric<T>> T[][] solveWithoutPivot(final T[][] a, final T[][] b) {
        int i, j, k, n = a.length, m = b[0].length;
        final T[][] ma = copy(a);
        final T[][] mb = copy(b);
        final T[][] x = create(a[0][0], n, m);
        T[][] l = ma, r = ma;
        T tmp;
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma);
        // forward elimination: R*x = L^{-1}*y = z
        for (k = 0; k < m; k++)
            for (i = 0; i < n; mb[i][k] = tmp, i++)
                for (tmp = mb[i][k], j = 0; j < i; tmp = tmp.minus(l[i][j].multiply(mb[j][k])), j++)
                    ;

        // backward elimination: z=R*x => x = R^{-1}*z
        for (k = 0; k < m; k++)
            for (j = n - 1; j >= 0; mb[j][k] = tmp, x[j][k] = tmp.div(r[j][j]), j--)
                for (tmp = mb[j][k], i = j + 1; i < n; tmp = tmp.minus(r[j][i].multiply(x[i][k])), i++)
                    ;

        // residual check and optional correction
        double resErr = LinearAlgebra.norm1(residual(a, b, x));
        if (resErr > getResidualError()) {
            residualCorrection(a, ma, b, x);
            double corrected = LinearAlgebra.norm1(residual(a, b, x));
            getLogger().warn(String.format(GLOBAL_RESIDUAL_ERROR_CHANGED, resErr, corrected, corrected / resErr));
        }
        return x;
    }

    /**
     * Solve the equation A*x=b using pivot.
     *
     * @param a NxN matrix
     * @param b NxM right side
     * @return x NxM solution vectors
     */
    private float[][] solveWithPivot(final float[][] a, final float[][] b) {
        int i, j, k, n = a.length, m = b[0].length;
        int[] p = new int[n];
        final float[][] ma = copy(a);
        final float[][] mb = copy(b);
        final float[][] x = new float[n][m];
        final float[][] l = ma, r = ma;
        float tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma, p);
        // forward elimination: R*x = L^{-1}*y = z
        for (k = 0; k < m; k++)
            for (i = 0; i < n; mb[p[i]][k] = tmp, i++)
                for (tmp = mb[p[i]][k], j = 0; j < i; tmp -= l[p[i]][j] * mb[p[j]][k], j++)
                    ;

        // backward elimination: z=R*x => x = R^{-1}*z
        for (k = 0; k < m; k++)
            for (j = n - 1; j >= 0; mb[p[j]][k] = tmp, x[j][k] = tmp / r[p[j]][j], j--)
                for (tmp = mb[p[j]][k], i = j + 1; i < n; tmp -= r[p[j]][i] * x[i][k], i++)
                    ;

        // residual check and optional correction
        float resErr = LinearAlgebra.norm1(residual(a, b, x));
        if (resErr > getResidualError()) {
            residualCorrection(a, ma, b, x, p);
            double corrected = LinearAlgebra.norm1(residual(a, b, x));
            getLogger().warn(String.format(RESIDUAL_ERROR_CORRECTED, resErr, corrected, corrected / resErr));
        }
        return x;
    }

    /**
     * Solve the equation A*x=b using pivot.
     *
     * @param a NxN matrix
     * @param b NxM right side
     * @return x NxM solution vectors
     */
    private double[][] solveWithPivot(final double[][] a, final double[][] b) {
        int i, j, k, n = a.length, m = b[0].length;
        int[] p = new int[n];
        final double[][] ma = copy(a);
        final double[][] mb = copy(b);
        final double[][] x = new double[n][m];
        final double[][] l = ma, r = ma;
        double tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma, p);
        // forward elimination: R*x = L^{-1}*y = z
        for (k = 0; k < m; k++)
            for (i = 0; i < n; mb[p[i]][k] = tmp, i++)
                for (tmp = mb[p[i]][k], j = 0; j < i; tmp -= l[p[i]][j] * mb[p[j]][k], j++)
                    ;

        // backward elimination: z=R*x => x = R^{-1}*z
        for (k = 0; k < m; k++)
            for (j = n - 1; j >= 0; mb[p[j]][k] = tmp, x[j][k] = tmp / r[p[j]][j], j--)
                for (tmp = mb[p[j]][k], i = j + 1; i < n; tmp -= r[p[j]][i] * x[i][k], i++)
                    ;

        // residual check and optional correction
        double resErr = LinearAlgebra.norm1(residual(a, b, x));
        if (resErr > getResidualError()) {
            residualCorrection(a, ma, b, x, p);
            double corrected = LinearAlgebra.norm1(residual(a, b, x));
            getLogger().warn(String.format(RESIDUAL_ERROR_CORRECTED, resErr, corrected, corrected / resErr));
        }
        return x;
    }

    /**
     * Solve the equation A*x=b using pivot.
     *
     * @param a   NxN matrix
     * @param b   NxM right side
     * @param <T> the type of the numeary
     * @return x NxM solution vectors
     */
    private <T extends Numeric<T>> T[][] solveWithPivot(final T[][] a, final T[][] b) {
        int i, j, k, n = a.length, m = b[0].length;
        int[] p = new int[n];
        final T[][] ma = copy(a);
        final T[][] mb = copy(b);
        final T[][] x = create(a[0][0], n, m);
        final T[][] l = ma, r = ma;
        T tmp;
        matrixCheck(a);
        // decompose A into left and right triangular matrix A = L*R.
        lrdecompose(ma, p);
        // forward elimination: R*x = L^{-1}*y = z
        for (k = 0; k < m; k++)
            for (i = 0; i < n; mb[p[i]][k] = tmp, i++)
                for (tmp = mb[p[i]][k], j = 0; j < i; tmp = tmp.minus(l[p[i]][j].multiply(mb[p[j]][k])), j++)
                    ;

        // backward elimination: z=R*x => x = R^{-1}*z
        for (k = 0; k < m; k++)
            for (j = n - 1; j >= 0; mb[p[j]][k] = tmp, x[j][k] = tmp.div(r[p[j]][j]), j--)
                for (tmp = mb[p[j]][k], i = j + 1; i < n; tmp = tmp.minus(r[p[j]][i].multiply(x[i][k])), i++)
                    ;

        // residual check and optional correction
        double resErr = LinearAlgebra.norm1(residual(a, b, x));
        if (resErr > getResidualError()) {
            residualCorrection(a, ma, b, x, p);
            double corrected = LinearAlgebra.norm1(residual(a, b, x));
            getLogger().warn(
                    String.format("Global Residual Error %e changed to %e ratio=%f", resErr, corrected, corrected
                            / resErr));
        }
        return x;
    }

    /**
     * Internal helper method to perform the L*R decomposition.
     * It has a runtime complexity of order 2/3n**3
     *
     * @param a double[][] the matrix to decompose.
     */
    private void lrdecompose(final double[][] a) {
        int n = a.length;
        int k;
        double aii, aij, aji;
        double[] d = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; a[i][j] = aij, j++)
                for (k = 0, aij = a[i][j]; k < i; aij -= a[i][k] * a[k][j], k++)
                    ;

            aii = a[i][i];
            d[i] = aii;
            checkRegular(i, aii);
            for (int j = i + 1; j < n; a[j][i] = aji / aii, j++) {
                for (k = 0, aji = a[j][i]; k < i; aji -= a[j][k] * a[k][i], k++)
                    ;
            }
        }
        diagonalCheck(d);

    }

    /**
     * LR decomposition with pivot search.
     *
     * @param a double[][] the matrix
     * @param p int[] array with the pivot indexes
     */
    private void lrdecompose(final double[][] a, final int[] p) {
        int i, j, k, n = a.length;
        double aii, aij, aji;
        for (i = 0; i < n; p[i] = i, i++)
            ;
        double[] d = new double[n];

        for (i = 0; i < n; i++) {
            pivot(i, p, a);
            for (j = i; j < n; a[p[i]][j] = aij, j++)
                for (k = 0, aij = a[p[i]][j]; k < i; aij -= a[p[i]][k] * a[p[k]][j], k++)
                    ;

            aii = a[p[i]][i];
            d[i] = aii;
            checkRegular(i, aii);
            for (j = i + 1; j < n; a[p[j]][i] = aji / aii, j++)
                for (k = 0, aji = a[p[j]][i]; k < i; aji -= a[p[j]][k] * a[p[k]][i], k++)
                    ;

        }
        diagonalCheck(d);
    }

}
 