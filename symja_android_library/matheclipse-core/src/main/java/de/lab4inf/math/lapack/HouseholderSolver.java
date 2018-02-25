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
* limitations under the License. *
*
*/

package de.lab4inf.math.lapack;

import de.lab4inf.math.Complex;
import de.lab4inf.math.Numeric;

import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.createRealMatrix;
import static de.lab4inf.math.lapack.LinearAlgebra.imag;
import static de.lab4inf.math.lapack.LinearAlgebra.isHermitian;
import static de.lab4inf.math.lapack.LinearAlgebra.isImaginary;
import static de.lab4inf.math.lapack.LinearAlgebra.isReal;
import static de.lab4inf.math.lapack.LinearAlgebra.isSelfAdjoint;
import static de.lab4inf.math.lapack.LinearAlgebra.isSymmetric;
import static de.lab4inf.math.lapack.LinearAlgebra.real;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;

/**
 * Linear algebra solver for A*x=y for a symmetric matrix A.
 * This solver uses householder transformations to decompose the matrix A.
 * <hr/>
 * <code>
 * Note: Part of the implementation is based on algorithm 4.40 from
 * "Formelsammlung zur Numerischen Mathematik",
 * G. Engeln-Muellges.
 * </code>
 * <hr/>
 *
 * @author nwulff
 * @version $Id: HouseholderSolver.java,v 1.32 2014/12/08 15:21:05 nwulff Exp $
 * @since 07.01.2009
 */
@Symmetric
public class HouseholderSolver extends LASolver {
    /**
     * Check if the given matrix A is appropriate for the householder
     * decomposition, that A is symmetric.
     *
     * @param a NxN matrix to check.
     */
    private static void matrixCheck(final float[][] a) {
        if (!isSymmetric(a)) {
            String msg = NOT_SYMMETRIC;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Check if the given matrix A is appropriate for the householder
     * decomposition, that A is symmetric.
     *
     * @param a NxN matrix to check.
     */
    private static void matrixCheck(final double[][] a) {
        if (!isSymmetric(a)) {
            String msg = NOT_SYMMETRIC;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Check if the given matrix A is appropriate for the householder
     * decomposition, that A is hermitian.
     *
     * @param a NxN matrix to check.
     */
    private static void matrixCheck(final Complex[][] a) {
        if (!isHermitian(a)) {
            String msg = NOT_HERMITIAN;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Check if the given matrix A is appropriate for the householder
     * decomposition, that A is symmetric.
     *
     * @param a NxN matrix to check.
     */
    private static <T extends Numeric<T>> void matrixCheck(final T[][] a) {
        if (!isSelfAdjoint(a)) {
            String msg = NOT_SELF_ADJOINT;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
    }

    /**
     * Internal householder decomposition.
     *
     * @param a matrix  to decompose
     * @param w the coefficients of the householder matrix
     * @param d the diagonal elements of the matrix
     */
    private static void decompose(final float[][] a, final float[] w, final float[] d) {
        int i, j, k, n = a.length;
        float tmp, r, r2;
        for (i = 0; i < n; i++) {
            for (r2 = 0, j = i; j < n; tmp = a[j][i], r2 += tmp * tmp, j++)
                ;
            r = signum(a[i][i]) * (float) sqrt(r2);
            w[i] = 1.f / (r2 + r * a[i][i]);
            a[i][i] += r;
            d[i] = -r;
            checkRegular(i, r);
            for (k = i + 1; k < n; k++) {
                for (tmp = 0, j = i; j < n; tmp += a[j][k] * a[j][i], j++)
                    ;
                for (tmp *= w[i], j = i; j < n; a[j][k] -= tmp * a[j][i], j++)
                    ;
            }
        }
        diagonalCheck(d);
    }

    private static void detDecompse(final float[][] a) {
        int i, im, n = a.length;
        // bring matrix into (symmetric) tri-diagonal form
        // using the external householder
        Householder.householder(a);
        // decompose into C*B
        for (i = 1, im = i - 1; i < n; im++, i++) {
            checkRegular(im, a);
            a[i][im] /= a[im][im];
            a[i][i] -= a[i][im] * a[im][i];
        }
    }

    private static void detDecompse(final double[][] a) {
        int i, im, n = a.length;
        // bring matrix into (symmetric) tri-diagonal form
        // using the external householder
        Householder.householder(a);
        // decompose into C*B
        for (i = 1, im = i - 1; i < n; im++, i++) {
            checkRegular(im, a);
            a[i][im] /= a[im][im];
            a[i][i] -= a[i][im] * a[im][i];
        }
    }

    /**
     * Internal householder decomposition.
     *
     * @param a matrix  to decompose
     * @param w the coefficients of the householder matrix
     * @param d the diagonal elements of the matrix
     */
    private static <T extends Numeric<T>> void decompose(final T[][] a, final T[] w, final T[] d) {
        int i, j, k, n = a.length;
        T zero = a[0][0].getZero();
        T one = zero.getOne();
        T tmp, r, r2;
        for (i = 0; i < n; i++) {
            for (r2 = zero, j = i; j < n; tmp = a[j][i], r2 = r2.plus(tmp.multiply(tmp)), j++)
                ;
            r = r2.sqrt();
            if (a[i][i].lt(zero)) {
                d[i] = r;
                r = r.multiply(one.getMinusOne());
            } else {
                d[i] = r.multiply(one.getMinusOne());
            }

            w[i] = one.div(r2.plus(r.multiply(a[i][i])));
            a[i][i] = a[i][i].plus(r);
            // checkRegular(i, r);
            for (k = i + 1; k < n; k++) {
                for (tmp = zero, j = i; j < n; tmp = tmp.plus(a[j][k].multiply(a[j][i])), j++)
                    ;
                for (tmp = tmp.multiply(w[i]), j = i; j < n; a[j][k] = a[j][k].minus(tmp.multiply(a[j][i])), j++)
                    ;
            }
        }
        diagonalCheck(d);
    }

    /**
     * Solve the equation A*x = y for x.
     * Note: matrix A has to be symmetric.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    @Override
    public float[] solve(final float[][] a, final float[] y) {
        matrixCheck(a);
        return solveSymmetric(a, y);
    }

    /**
     * Solve the equation A*x = y for x.
     * Note: matrix A has to be symmetric.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    @Override
    public double[] solve(final double[][] a, final double[] y) {
        matrixCheck(a);
        return solveSymmetric(a, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(float[][], float[][])
     */
    @Override
    public float[][] solve(final float[][] a, final float[][] b) {
        matrixCheck(a);
        return solveSymmetric(a, b);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(double[][], double[][])
     */
    @Override
    public double[][] solve(final double[][] a, final double[][] b) {
        matrixCheck(a);
        return solveSymmetric(a, b);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#inverse(float[][])
     */
    @Override
    public float[][] inverse(final float[][] a) {
        matrixCheck(a);
        float[][] e = LinearAlgebra.identityFloat(a.length);
        return solve(a, e);
    }

    /**
     * Calculate the determinate of matrix A.
     * Note: matrix A has to be symmetric.
     *
     * @param a double[][] the NxN matrix
     * @return double the determinant of A
     */
    public float det(final float[][] a) {
        matrixCheck(a);
        int i, n = a.length;
        float[][] t = copy(a);
        // decompose A into tri-diagonal matrix A = C*B.
        detDecompse(t);
        float det = 1;
        float[] d = new float[n];
        for (i = 0; i < n; i++) {
            det *= t[i][i];
            d[i] = t[i][i];
        }
        diagonalCheck(d);
        return det;
    }

    /**
     * Calculate the determinate of matrix A.
     * Note: matrix A has to be symmetric.
     *
     * @param a double[][] the NxN matrix
     * @return double the determinant of A
     */
    @Override
    public double det(final double[][] a) {
        matrixCheck(a);
        int i, n = a.length;
        double[][] t = copy(a);
        // decompose A into tri-diagonal matrix A = C*B.
        detDecompse(t);
        double det = 1;
        double[] d = new double[n];
        for (i = 0; i < n; i++) {
            det *= t[i][i];
            d[i] = t[i][i];
        }
        diagonalCheck(d);
        return det;
    }

    /**
     * Solve the problem A*x = y  for x with symmetric matrix A using
     * householder transformations.
     * Implementation is based on alogrithm 4.40 from G. Engeln-Muellges.
     *
     * @param a the NxN matrix
     * @param y right side of the equation
     * @return solution x
     */
    @Override
    public float[] solveSymmetric(final float[][] a, final float[] y) {
        int i, j, k, n = a.length;
        float s;
        float[][] mat = copy(a);
        float[] b = copy(y);
        float[] ai = new float[n];
        float[] x = new float[n];
        float[] d = new float[n];
        decompose(mat, ai, d);
        // forward substitution
        for (i = 0; i < n; i++) {
            for (s = 0, j = i; j < n; s += b[j] * mat[j][i], j++)
                ;
            s *= ai[i];
            for (j = i; j < n; b[j] = b[j] - s * mat[j][i], j++)
                ;
        }
        // back substitution
        for (i = n - 1; i >= 0; x[i] = (b[i] - s) / d[i], i--) {
            for (s = 0, k = i + 1; k < n; s += mat[i][k] * x[k], k++)
                ;
        }
        return x;
    }

    /**
     * Solve the problem A*x = y  for x with symmetric matrix A using
     * householder transformations.
     * Implementation is based on alogrithm 4.40 from G. Engeln-Muellges.
     *
     * @param a the NxN matrix
     * @param y right side of the equation
     * @return solution x
     */
    @Override
    public double[] solveSymmetric(final double[][] a, final double[] y) {
        int i, j, k, n = a.length;
        double s;
        double[][] mat = copy(a);
        double[] b = copy(y);
        double[] ai = new double[n];
        double[] x = new double[n];
        double[] d = new double[n];
        decompose(mat, ai, d);
        // forward substitution
        for (i = 0; i < n; i++) {
            for (s = 0, j = i; j < n; s += b[j] * mat[j][i], j++)
                ;
            s *= ai[i];
            for (j = i; j < n; b[j] = b[j] - s * mat[j][i], j++)
                ;
        }
        // back substitution
        for (i = n - 1; i >= 0; x[i] = (b[i] - s) / d[i], i--) {
            for (s = 0, k = i + 1; k < n; s += mat[i][k] * x[k], k++)
                ;
        }
        return x;
    }

    /**
     * Solve the equation A*X = Y for X where A has to be symmetric.
     *
     * @param a double[][] real NxN symmetric matrix
     * @param y double[][] real NxN right side vectors as matrix
     * @return double[][] X solution matrix
     */
    public float[][] solveSymmetric(final float[][] a, final float[][] y) {
        int n = a.length;
        int m = y[0].length;
        float s;
        float[][] x = new float[n][m];
        float[][] mat = copy(a);
        float[] w = new float[n];
        float[] d = new float[n];
        float[] b = new float[n];

        decompose(mat, w, d);
        for (int p = 0; p < m; p++) {
            for (int i = 0; i < n; b[i] = y[i][p], i++)
                ;
            // forward substitution
            for (int i = 0; i < n; i++) {
                s = 0;
                for (int j = i; j < n; s += b[j] * mat[j][i], j++)
                    ;
                s *= w[i];
                for (int j = i; j < n; j++) {
                    b[j] = b[j] - s * mat[j][i];
                }
            }
            // backward substitution
            for (int i = n - 1; i >= 0; i--) {
                s = 0;
                for (int k = i + 1; k < n; s += mat[i][k] * x[k][p], k++)
                    ;
                x[i][p] = (b[i] - s) / d[i];
            }
        }
        return x;
    }

    /**
     * Solve the equation A*X = Y for X where A has to be symmetric.
     *
     * @param a double[][] real NxN symmetric matrix
     * @param y double[][] real NxN right side vectors as matrix
     * @return double[][] X solution matrix
     */
    @Override
    public double[][] solveSymmetric(final double[][] a, final double[][] y) {
        int n = a.length;
        int m = y[0].length;
        double s;
        double[][] x = new double[n][m];
        double[][] mat = copy(a);
        double[] w = new double[n];
        double[] d = new double[n];
        double[] b = new double[n];

        decompose(mat, w, d);
        for (int p = 0; p < m; p++) {
            for (int i = 0; i < n; b[i] = y[i][p], i++)
                ;
            // forward substitution
            for (int i = 0; i < n; i++) {
                s = 0;
                for (int j = i; j < n; s += b[j] * mat[j][i], j++)
                    ;
                s *= w[i];
                for (int j = i; j < n; j++) {
                    b[j] = b[j] - s * mat[j][i];
                }
            }
            // backward substitution
            for (int i = n - 1; i >= 0; i--) {
                s = 0;
                for (int k = i + 1; k < n; s += mat[i][k] * x[k][p], k++)
                    ;
                x[i][p] = (b[i] - s) / d[i];
            }
        }
        return x;
    }

    /**
     * Internal householder decomposition.
     *
     * @param a matrix  to decompose
     * @param w the coefficients of the householder matrix
     * @param d the diagonal elements of the matrix
     */
    private void decompose(final double[][] a, final double[] w, final double[] d) {
        int i, j, k, n = a.length;
        double tmp, r, r2;
        for (i = 0; i < n; i++) {
            for (r2 = 0, j = i; j < n; tmp = a[j][i], r2 += tmp * tmp, j++)
                ;
            r = signum(a[i][i]) * sqrt(r2);
            w[i] = 1. / (r2 + r * a[i][i]);
            a[i][i] += r;
            d[i] = -r;
            checkRegular(i, r);
            for (k = i + 1; k < n; k++) {
                for (tmp = 0, j = i; j < n; tmp += a[j][k] * a[j][i], j++)
                    ;
                for (tmp *= w[i], j = i; j < n; a[j][k] -= tmp * a[j][i], j++)
                    ;
            }
        }
        diagonalCheck(d);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#det(de.lab4inf.math.Complex[][])
     */
    public Complex det(final Complex[][] c) {
        GenericSolver solver = new GenericSolver();
        double det;
        double[][] a, b;
        int n = c.length;
        matrixCheck(c);
        if (isReal(c)) {
            // a has to be symmetric
            a = real(c);
            det = solver.det(a);
        } else if (isImaginary(c)) {
            // b has to be anti-symmetric
            if (n % 2 == 0) {
                b = imag(c);
                det = solver.det(b);
            } else {
                det = 0;
            }
        } else {
            a = createRealMatrix(c);
            // TODO the square root is unique up to a sign.
            det = -sqrt(det(a));
            // boolean negative = (n%2 == 1);
            // if(negative) det = -det;
        }
        // for a hermitian matrix the determinant has to be real!
        return LinearAlgebra.newComplex(det);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(de.lab4inf.math.Complex[][], de.lab4inf.math.Complex[])
     */
    public Complex[] solve(final Complex[][] a, final Complex[] y) {
        matrixCheck(a);
        return solveViaDecomposition(a, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(de.lab4inf.math.Complex[][], de.lab4inf.math.Complex[][])
     */
    public Complex[][] solve(final Complex[][] a, final Complex[][] b) {
        matrixCheck(a);
        return solveViaDecomposition(a, b);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#det(T[][])
     */
    @Override
    public <T extends Numeric<T>> T det(final T[][] a) {
        if (isAComplex(a)) {
            return asT(a[0][0], det(asComplex(a)));
        }
        matrixCheck(a);
        final int n = a.length;
        T[][] mat = copy(a);
        T[] ai = create(a, n);
        T[] d = create(a, n);
        decompose(mat, ai, d);
        T det = d[0];
        for (int j = 1; j < n; j++)
            det = det.multiply(d[j]);
        return det.abs();
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
        matrixCheck(a);
        return solveSymmetric(a, y);
    }

    /*
     * (non-Javadoc
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(T[][], T[][])
     */
    @Override
    public <T extends Numeric<T>> T[][] solve(final T[][] a, final T[][] b) {
        if (isAComplex(a)) {
            return asT(a, solve(asComplex(a), asComplex(b)));
        }
        matrixCheck(a);
        return solveSymmetric(a, b);
    }

    /**
     * Solve the problem A*x = y  for x with symmetric matrix A using
     * householder transformations.
     * Implementation is based on alogrithm 4.40 from G. Engeln-Muellges.
     *
     * @param a   the NxN matrix
     * @param y   right side of the equation
     * @param <T> matrix type
     * @return solution x
     */
    public <T extends Numeric<T>> T[] solveSymmetric(final T[][] a, final T[] y) {
        int i, j, k, n = a.length;
        final T zero = a[0][0].getZero();
        T s;
        T[][] mat = copy(a);
        T[] b = copy(y);
        T[] ai = create(a, n);
        T[] x = create(a, n);
        T[] d = create(a, n);
        decompose(mat, ai, d);
        // forward substitution
        for (i = 0; i < n; i++) {
            for (s = zero, j = i; j < n; s = s.plus(b[j].multiply(mat[j][i])), j++)
                ;
            s = s.multiply(ai[i]);
            for (j = i; j < n; b[j] = b[j].minus(s.multiply(mat[j][i])), j++)
                ;
        }
        // back substitution
        for (i = n - 1; i >= 0; x[i] = (b[i].minus(s)).div(d[i]), i--) {
            for (s = zero, k = i + 1; k < n; s = s.plus(mat[i][k].multiply(x[k])), k++)
                ;
        }
        return x;
    }

    /**
     * Solve the problem A*x = y  for x with symmetric matrix A using
     * householder transformations.
     * Implementation is based on alogrithm 4.40 from G. Engeln-Muellges.
     *
     * @param a   the NxN matrix
     * @param y   right side of the equation
     * @param <T> matrix type
     * @return solution x
     */
    public <T extends Numeric<T>> T[][] solveSymmetric(final T[][] a, final T[][] y) {
        final int n = a.length;
        final int m = a[0].length;
        final T zero = a[0][0].getZero();
        T s;
        T[][] mat = copy(a);
        T[][] x = create(a, n, m);
        T[] b = create(y, n);
        T[] ai = create(a, n);
        T[] d = create(a, n);
        int i, j, k;
        decompose(mat, ai, d);
        for (int p = 0; p < m; p++) {
            for (i = 0; i < n; b[i] = y[i][p], i++)
                ;

            // forward substitution
            for (i = 0; i < n; i++) {
                for (s = zero, j = i; j < n; s = s.plus(b[j].multiply(mat[j][i])), j++)
                    ;
                s = s.multiply(ai[i]);
                for (j = i; j < n; b[j] = b[j].minus(s.multiply(mat[j][i])), j++)
                    ;
            }
            // back substitution
            for (i = n - 1; i >= 0; x[i][p] = (b[i].minus(s)).div(d[i]), i--) {
                for (s = zero, k = i + 1; k < n; s = s.plus(mat[i][k].multiply(x[k][p])), k++)
                    ;
            }
        }
        return x;
    }

}
 