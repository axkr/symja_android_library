/*
 * Project: Lab4Math
 *
 * Copyright(c) 2008-2009, Prof. Dr. Nikolaus Wulff
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

import de.lab4inf.math.Complex;
import de.lab4inf.math.Numeric;

import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.isHermitian;
import static de.lab4inf.math.lapack.LinearAlgebra.isPositiveDefinite;
import static de.lab4inf.math.lapack.LinearAlgebra.isSymmetric;

/**
 * Cholesky decomposition of a positive-definite symmetric matrix A
 * into A = B^{T}*D*B, with upper normed triangular matrix B and
 * diagonal matrix D, to solve the equation A*x = y for x.
 * <hr/>
 * <code>
 * Note: The implementation is based on algorithm 4.29* from
 * "Formelsammlung zur Numerischen Mathematik",
 * G. Engeln-Muellges.
 * </code>
 * <hr/>
 * <code>
 * For the complex case we use a modified complex version of
 * algorithm 4.29.
 * </code>
 * <hr/>
 *
 * @author nwulff
 * @version $Id: CholeskySolver.java,v 1.43 2014/12/09 13:17:29 nwulff Exp $
 * @since 17.12.2008
 */
@Symmetric("only symmetric matrix allowed")
@PositiveDefinite("all eigenvalues have to be greater than 0")
public final class CholeskySolver extends LASolver {

    /**
     * Check if the given matrix A is appropriate for the cholesky
     * decomposition, that is symmetric and positive-definite.
     *
     * @param a NxN matrix to check.
     */
    private static void matrixCheck(final float[][] a) {
        // if (!isSquare(a)) {
        // String msg = NOT_SQUARE;
        // IllegalArgumentException error = new IllegalArgumentException(msg);
        // getLogger().warn(msg);
        // throw error;
        // }
        if (!isSymmetric(a)) {
            final String msg = NOT_SYMMETRIC;
            final IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
        if (!isPositiveDefinite(a)) {
            final String msg = NOT_POSITIVE_DEFINITE;
            final IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Check if the given matrix A is appropriate for the cholesky
     * decomposition, that is symmetric and positive-definite.
     *
     * @param a NxN matrix to check.
     */
    private static void matrixCheck(final double[][] a) {
        // if (!isSquare(a)) {
        // String msg = NOT_SQUARE;
        // IllegalArgumentException error = new IllegalArgumentException(msg);
        // getLogger().warn(msg);
        // throw error;
        // }
        if (!isSymmetric(a)) {
            final String msg = NOT_SYMMETRIC;
            final IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
        if (!isPositiveDefinite(a)) {
            final String msg = NOT_POSITIVE_DEFINITE;
            final IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Check if the given matrix A is appropriate for the cholesky
     * decomposition, that is hermitian and positive-definite.
     *
     * @param a NxN matrix to check.
     */
    private static void matrixCheck(final Complex[][] a) {
        // if (!isSquare(a)) {
        // String msg = NOT_SQUARE;
        // IllegalArgumentException error = new IllegalArgumentException(msg);
        // getLogger().warn(msg);
        // throw error;
        // }
        if (!isHermitian(a)) {
            final String msg = NOT_HERMITIAN;
            final IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
        // Not a checked exception at present
        if (!isPositiveDefinite(a)) {
            final String msg = NOT_POSITIVE_DEFINITE;
            getLogger().warn(msg);
            // IllegalArgumentException error = new IllegalArgumentException(msg);
            // throw error;
        }
    }

    /**
     * Check if the given matrix A is appropriate for the cholesky
     * decomposition, that is hermitian and positive-definite.
     *
     * @param a NxN matrix to check.
     */
    private static <T extends Numeric<T>> void matrixCheck(final T[][] a) {
        if (!isSymmetric(a)) {
            final String msg = NOT_SYMMETRIC;
            final IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
        // Not a checked exception at present
        if (!isPositiveDefinite(a)) {
            final String msg = NOT_POSITIVE_DEFINITE;
            getLogger().error(msg);
            // IllegalArgumentException error = new IllegalArgumentException(msg);
            // throw error;
        }
    }

    /**
     * Internal multi-solve method.
     *
     * @param a NxN matrix
     * @param y NxM right side vectors
     * @return x solution of A*x=y
     */
    static float[][] cholesky(final float[][] a, final float[][] y) {
        int i, j, k;
        final int n = a.length, m = y[0].length;
        final float[][] u = copy(a);
        final float[][] x = new float[n][m];
        final float[] d = new float[n];
        final float[] z = new float[n];
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(u, d);
        for (k = 0; k < m; k++) {
            // forward elimination: B^T*z = y, D*B*x = z = D*u
            for (j = 0; j < n; x[j][k] = z[j] / d[j], j++)
                for (z[j] = y[j][k], i = 0; i < j; z[j] -= u[i][j] * z[i], i++)
                    ;

            // backward elimination: B*x = D^{-1}*z = u
            for (j = n - 1; j >= 0; j--)
                for (i = j + 1; i < n; x[j][k] -= u[j][i] * x[i][k], i++)
                    ;

        }
        return x;
    }

    /**
     * Internal cholesky method to solve A*x = y.
     *
     * @param a float[][] matrix to decompose
     * @param y float[] left side of the equation
     * @return float[] x solution vector
     */
    static float[] cholesky(@Symmetric final float[][] a, final float[] y) {
        int i, j;
        final int n = y.length;
        final float[][] b = copy(a);
        final float[] d = new float[n];
        final float[] x = new float[n];
        final float[] z = copy(y);
        final float[] u = x;
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(b, d);
        // forward elimination: B^T*z = y, D*B*x = z = D*u
        for (j = 0; j < n; u[j] = z[j] / d[j], j++)
            for (i = 0; i < j; z[j] -= b[i][j] * z[i], i++)
                ;

        // backward elimination: B*x = D^{-1}*z = u
        for (j = n - 1; j >= 0; j--)
            for (i = j + 1; i < n; x[j] -= b[j][i] * x[i], i++)
                ;

        return x;
    }

    /**
     * Internal Cholesky decomposition A = B^T*D*B.
     *
     * @param a double[][] matrix which will be overwritten.
     * @param d double[] the diagonal elements.
     */
    private static void decompose(@Symmetric final float[][] a, final float[] d) {
        int i, j, k;
        final int n = a.length;
        float h;
        final float[][] b = a;
        for (j = 0; j < n; d[j] = a[j][j], j++) {
            for (i = 0; i < j; i++)
                for (h = a[i][j], b[i][j] /= d[i], k = i + 1; h != 0 && k <= j; a[k][j] -= h * b[i][k], k++)
                    ;

            checkRegular(j, a[j][j]);
        }
        diagonalCheck(d);
    }

    /**
     * Internal Cholesky decomposition A = B^T*D*B.
     *
     * @param a T typed matrix which will be overwritten.
     * @param d T[] the diagonal elements.
     */
    private static <T extends Numeric<T>> void decompose(@Symmetric final T[][] a, final T[] d) {
        int i, j, k;
        final int n = a.length;
        T h;
        final T[][] b = a;
        for (j = 0; j < n; d[j] = a[j][j], j++) {
            for (i = 0; i < j; i++) {
                h = a[i][j];
                b[i][j] = b[i][j].div(d[i]);
                if (!h.isZero())
                    for (k = i + 1; k <= j; k++)
                        a[k][j] = a[k][j].minus(h.multiply(b[i][k]));

            }
        }
        diagonalCheck(d);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solve(float[][], float[])
     */
    @Override
    public float[] solve(final float[][] a, final float[] y) {
        matrixCheck(a);
        return cholesky(a, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solveSymmetric(float[][], float[])
     */
    @Override
    public float[] solveSymmetric(@Symmetric final float[][] a, final float[] y) {
        matrixCheck(a);
        return cholesky(a, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solve(float[][], float[][])
     */
    @Override
    public float[][] solve(final float[][] a, final float[][] y) {
        matrixCheck(a);
        return cholesky(a, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#inverse(float[][])
     */
    @Override
    public float[][] inverse(final float[][] a) {
        matrixCheck(a);
        final float[][] e = LinearAlgebra.identityFloat(a.length);
        return solve(a, e);
    }

    /**
     * Solve the equation A*x = y for x.
     * Note: matrix A has to be symmetric and positive-definite.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    @Override
    public double[] solve(final double[][] a, final double[] y) {
        matrixCheck(a);
        return cholesky(a, y);
    }

    /**
     * Solve the equation A*x = y for x.
     * Note: matrix A has to be hermitian and positive-definite.
     *
     * @param a Complex[][] NxN matrix.
     * @param y Complex[] N dimensional right side vector.
     * @return Complex[] N dimensional solution x.
     */
    public Complex[] solve(final Complex[][] a, final Complex[] y) {
        matrixCheck(a);
        return cholesky(a, y);
    }

    /**
     * Solve the multi-equation A*X = B for X.
     * Note: matrix A has to be symmetric and positive-definite.
     *
     * @param a double[][] real NxN matrix
     * @param b double[][] real NxM right side vectors as matrix
     * @return double[][] X solution matrix
     */
    @Override
    public double[][] solve(final double[][] a, final double[][] b) {
        matrixCheck(a);
        return cholesky(a, b);
    }

    /**
     * Solve the multi-equation A*X = B for X.
     * Note: matrix A has to be hermitian and positive-definite.
     *
     * @param a Complex[][] NxN matrix.
     * @param b Complex[][] NxM right side vectors as matrix
     * @return Complex[][] X solution matrix
     */
    public Complex[][] solve(final Complex[][] a, final Complex[][] b) {
        matrixCheck(a);
        return cholesky(a, b);
    }

    /**
     * Calculate the determinate of matrix A.
     * Note: matrix A has to be symmetric and positive definite.
     *
     * @param a double[][] the NxN matrix
     * @return double the determinant of A
     */
    @Override
    public float det(final float[][] a) {
        matrixCheck(a);
        int i;
        final int n = a.length;
        final float[] d = new float[n];
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(copy(a), d);
        float det = 1;
        for (i = 0; i < n; det *= d[i], i++)
            ;
        return det;
    }

    /**
     * Calculate the determinate of matrix A.
     * Note: matrix A has to be symmetric and positive definite.
     *
     * @param a double[][] the NxN matrix
     * @return double the determinant of A
     */
    @Override
    public double det(final double[][] a) {
        matrixCheck(a);
        int i;
        final int n = a.length;
        final double[] d = new double[n];
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(copy(a), d);
        double det = 1;
        for (i = 0; i < n; det *= d[i], i++)
            ;
        return det;
    }

    /**
     * Calculate the determinate of matrix A.
     * Note: matrix A has to be hermitian and positive definite,
     * which also means, that the determinant is reel.
     *
     * @param a Complex[][] the NxN matrix
     * @return Complex the determinant of A
     */
    public Complex det(final Complex[][] a) {
        matrixCheck(a);
        int i;
        final int n = a.length;
        final double[] d = new double[n];
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(copy(a), d);
        double det = d[0];
        for (i = 1; i < n; det *= d[i], i++)
            ;
        return a[0][0].newComplex(det, 0);
    }

    /**
     * Internal multi-solve method.
     *
     * @param a NxN matrix
     * @param y NxM right side vectors
     * @return x solution of A*x=y
     */
    double[][] cholesky(@Symmetric final double[][] a, final double[][] y) {
        int i, j, k;
        final int n = a.length, m = y[0].length;
        final double[][] u = copy(a);
        final double[][] x = new double[n][m];
        final double[] d = new double[n];
        final double[] z = new double[n];
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(u, d);
        for (k = 0; k < m; k++) {
            // forward elimination: B^T*z = y, D*B*x = z = D*u
            for (j = 0; j < n; x[j][k] = z[j] / d[j], j++)
                for (z[j] = y[j][k], i = 0; i < j; z[j] -= u[i][j] * z[i], i++)
                    ;

            // backward elimination: B*x = D^{-1}*z = u
            for (j = n - 1; j >= 0; j--)
                for (i = j + 1; i < n; x[j][k] -= u[j][i] * x[i][k], i++)
                    ;

        }
        return x;
    }

    /**
     * Internal cholesky method to solve A*x = y.
     *
     * @param a double[][] matrix to decompose
     * @param y double[] left side of the equation
     * @return double[] x solution vector
     */
    double[] cholesky(@Symmetric final double[][] a, final double[] y) {
        int i, j;
        final int n = y.length;
        final double[][] b = copy(a);
        final double[] d = new double[n];
        final double[] x = new double[n];
        final double[] z = copy(y);
        final double[] u = x;
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(b, d);
        // forward elimination: B^T*z = y, D*B*x = z = D*u
        for (j = 0; j < n; u[j] = z[j] / d[j], j++)
            for (i = 0; i < j; z[j] -= b[i][j] * z[i], i++)
                ;

        // backward elimination: B*x = D^{-1}*z = u
        for (j = n - 1; j >= 0; j--)
            for (i = j + 1; i < n; x[j] -= b[j][i] * x[i], i++)
                ;

        return x;
    }

    /**
     * Internal cholesky method to solve A*x = y for complex elements.
     *
     * @param a Complex[][] matrix to decompose
     * @param y Complex[] left side of the equation
     * @return Complex[] x solution vector
     */
    Complex[] cholesky(final Complex[][] a, final Complex[] y) {
        int i, j;
        final int n = y.length;
        final Complex[][] b = copy(a);
        final double[] d = new double[n];
        final Complex[] x = create(a, n);
        final Complex[] z = copy(y);
        final Complex[] u = x;
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(b, d);
        // forward elimination: B^T*z = y, D*B*x = z = D*u
        for (j = 0; j < n; u[j] = z[j].div(d[j]), j++) {
            // z[j] = y[j];
            for (i = 0; i < j; z[j] = z[j].minus(b[i][j].cmultiply(z[i])), i++)
                ;

        }
        // backward elimination: B*x = D^{-1}*z = u
        for (j = n - 1; j >= 0; j--) {
            // x[j] = b[j];
            for (i = j + 1; i < n; x[j] = x[j].minus(b[j][i].multiply(x[i])), i++)
                ;
        }
        return x;
    }

    /**
     * Internal multi-solve method.
     *
     * @param a NxN matrix
     * @param y NxM right side vectors
     * @return x solution of A*x=y
     */
    Complex[][] cholesky(final Complex[][] a, final Complex[][] y) {
        int i, j, k;
        final int n = a.length, m = y[0].length;
        final Complex[][] u = copy(a);
        final Complex[][] x = new Complex[n][m];
        final double[] d = new double[n];
        final Complex[] z = new Complex[n];
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(u, d);
        for (k = 0; k < m; k++) {
            // forward elimination: B^T*z = y, D*B*x = z = D*u
            for (j = 0; j < n; x[j][k] = z[j].div(d[j]), j++) {
                for (z[j] = y[j][k], i = 0; i < j; z[j] = z[j].minus(u[i][j].cmultiply(z[i])), i++)
                    ;
            }
            // backward elimination: B*x = D^{-1}*z = u
            for (j = n - 1; j >= 0; j--) {
                for (i = j + 1; i < n; x[j][k] = x[j][k].minus(u[j][i].multiply(x[i][k])), i++)
                    ;
            }
        }
        return x;
    }

    /**
     * Internal Cholesky decomposition A = B^T*D*B.
     *
     * @param a double[][] matrix which will be overwritten.
     * @param d double[] the diagonal elements.
     */
    private void decompose(@Symmetric final double[][] a, final double[] d) {
        int i, j, k;
        final int n = a.length;
        double h;
        final double[][] b = a;
        for (j = 0; j < n; d[j] = a[j][j], j++) {
            for (i = 0; i < j; i++)
                for (h = a[i][j], b[i][j] /= d[i], k = i + 1; h != 0 && k <= j; a[k][j] -= h * b[i][k], k++)
                    ;

            checkRegular(i, a[j][j]);
        }
        diagonalCheck(d);
    }

    /**
     * Internal Cholesky decomposition A = B^T*D*B.
     *
     * @param a Complex matrix which will be overwritten.
     * @param d double[] the diagonal elements.
     */
    private void decompose(final Complex[][] a, final double[] d) {
        int i, j, k;
        final int n = a.length;
        Complex h;
        final Complex[][] b = a;
        for (j = 0; j < n; j++) {
            for (i = 0; i < j; i++) {
                h = a[i][j];
                for (k = 0; k < i; h = h.minus(b[k][i].cmultiply(b[k][j]).multiply(d[k])), k++)
                    ;
                b[i][j] = h.div(d[i]);
            }
            d[j] = a[j][j].real();
            for (k = 0; k < j; d[j] -= d[k] * b[k][j].abs2(), k++)
                ;
            checkRegular(j, d[j]);
        }
        diagonalCheck(d);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#det(T[][])
     */
    @Override
    public <T extends Numeric<T>> T det(final T[][] a) {
        T det;
        if (isAComplex(a)) {
            return asT(a[0][0], det(asComplex(a)));
        } else {
            matrixCheck(a);
            int i;
            final int n = a.length;
            final T[] d = create(a, n);
            // decompose A into diagonal and right triangular matrix A = B^T*D*B.
            decompose(copy(a), d);
            det = d[0];
            for (i = 1; i < n; det = det.multiply(d[i]), i++)
                ;
        }
        return det;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(T[][], T[])
     */
    @Override
    public <T extends Numeric<T>> T[] solve(final T[][] a, final T[] y) {
        if (isAComplex(a)) {
            return asT(y, solve(asComplex(a), asComplex(y)));
        }
        int i, j;
        final int n = y.length;
        final T[][] b = copy(a);
        final T[] d = create(a, n);
        final T[] x = create(a, n);
        final T[] z = copy(y);
        final T[] u = x;
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(b, d);
        // forward elimination: B^T*z = y, D*B*x = z = D*u
        for (j = 0; j < n; u[j] = z[j].div(d[j]), j++) {
            // z[j] = y[j];
            for (i = 0; i < j; z[j] = z[j].minus(b[i][j].multiply(z[i])), i++)
                ;

        }
        // backward elimination: B*x = D^{-1}*z = u
        for (j = n - 1; j >= 0; j--) {
            // x[j] = b[j];
            for (i = j + 1; i < n; x[j] = x[j].minus(b[j][i].multiply(x[i])), i++)
                ;
        }
        return x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(T[][], T[][])
     */
    @Override
    public <T extends Numeric<T>> T[][] solve(final T[][] a, final T[][] y) {
        if (isAComplex(a)) {
            return asT(a, solve(asComplex(a), asComplex(y)));
        }
        matrixCheck(a);
        int i, j, k;
        final int n = a.length, m = y[0].length;
        final T[][] u = copy(a);
        final T[][] x = create(a, n, m);
        final T[] d = create(a, n);
        final T[] z = create(a, n);
        // decompose A into diagonal and right triangular matrix A = B^T*D*B.
        decompose(u, d);
        for (k = 0; k < m; k++) {
            // forward elimination: B^T*z = y, D*B*x = z = D*u
            for (j = 0; j < n; x[j][k] = z[j].div(d[j]), j++) {
                for (z[j] = y[j][k], i = 0; i < j; z[j] = z[j].minus(u[i][j].multiply(z[i])), i++)
                    ;
            }
            // backward elimination: B*x = D^{-1}*z = u
            for (j = n - 1; j >= 0; j--) {
                for (i = j + 1; i < n; x[j][k] = x[j][k].minus(u[j][i].multiply(x[i][k])), i++)
                    ;
            }
        }
        return x;
    }

}
 