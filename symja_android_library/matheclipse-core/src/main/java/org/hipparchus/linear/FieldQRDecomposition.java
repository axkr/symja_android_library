/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hipparchus.linear;

import org.hipparchus.RealFieldElement;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathArrays;

import java.util.Arrays;


/**
 * Calculates the QR-decomposition of a field matrix.
 * <p>The QR-decomposition of a matrix A consists of two matrices Q and R
 * that satisfy: A = QR, Q is orthogonal (Q<sup>T</sup>Q = I), and R is
 * upper triangular. If A is m&times;n, Q is m&times;m and R m&times;n.</p>
 * <p>This class compute the decomposition using Householder reflectors.</p>
 * <p>For efficiency purposes, the decomposition in packed form is transposed.
 * This allows inner loop to iterate inside rows, which is much more cache-efficient
 * in Java.</p>
 * <p>This class is based on the class {@link QRDecomposition}.</p>
 *
 * @param <T> type of the underlying field elements
 * @see <a href="http://mathworld.wolfram.com/QRDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/QR_decomposition">Wikipedia</a>
 */
public class FieldQRDecomposition<T extends RealFieldElement<T>> {
    /**
     * Singularity threshold.
     */
    private final T threshold;
    /**
     * A packed TRANSPOSED representation of the QR decomposition.
     * <p>The elements BELOW the diagonal are the elements of the UPPER triangular
     * matrix R, and the rows ABOVE the diagonal are the Householder reflector vectors
     * from which an explicit form of Q can be recomputed if desired.</p>
     */
    private T[][] qrt;
    /**
     * The diagonal elements of R.
     */
    private T[] rDiag;
    /**
     * Cached value of Q.
     */
    private FieldMatrix<T> cachedQ;
    /**
     * Cached value of QT.
     */
    private FieldMatrix<T> cachedQT;
    /**
     * Cached value of R.
     */
    private FieldMatrix<T> cachedR;
    /**
     * Cached value of H.
     */
    private FieldMatrix<T> cachedH;

    /**
     * Calculates the QR-decomposition of the given matrix.
     * The singularity threshold defaults to zero.
     *
     * @param matrix The matrix to decompose.
     * @see #FieldQRDecomposition(FieldMatrix, RealFieldElement)
     */
    public FieldQRDecomposition(FieldMatrix<T> matrix) {
        this(matrix, matrix.getField().getZero());
    }

    /**
     * Calculates the QR-decomposition of the given matrix.
     *
     * @param matrix    The matrix to decompose.
     * @param threshold Singularity threshold.
     */
    public FieldQRDecomposition(FieldMatrix<T> matrix, T threshold) {
        this.threshold = threshold;

        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        qrt = matrix.transpose().getData();
        rDiag = MathArrays.buildArray(threshold.getField(), FastMath.min(m, n));
        cachedQ = null;
        cachedQT = null;
        cachedR = null;
        cachedH = null;

        decompose(qrt);

    }

    /**
     * Decompose matrix.
     *
     * @param matrix transposed matrix
     */
    protected void decompose(T[][] matrix) {
        for (int minor = 0; minor < FastMath.min(matrix.length, matrix[0].length); minor++) {
            performHouseholderReflection(minor, matrix);
        }
    }

    /**
     * Perform Householder reflection for a minor A(minor, minor) of A.
     *
     * @param minor  minor index
     * @param matrix transposed matrix
     */
    protected void performHouseholderReflection(int minor, T[][] matrix) {

        final T[] qrtMinor = matrix[minor];
        final T zero = threshold.getField().getZero();
        /*
         * Let x be the first column of the minor, and a^2 = |x|^2.
         * x will be in the positions qr[minor][minor] through qr[m][minor].
         * The first column of the transformed minor will be (a,0,0,..)'
         * The sign of a is chosen to be opposite to the sign of the first
         * component of x. Let's find a:
         */
        T xNormSqr = zero;
        for (int row = minor; row < qrtMinor.length; row++) {
            final T c = qrtMinor[row];
            xNormSqr = xNormSqr.add(c.multiply(c));
        }
        final T a = (qrtMinor[minor].getReal() > 0) ? xNormSqr.sqrt().negate() : xNormSqr.sqrt();
        rDiag[minor] = a;

        if (a.getReal() != 0.0) {

            /*
             * Calculate the normalized reflection vector v and transform
             * the first column. We know the norm of v beforehand: v = x-ae
             * so |v|^2 = <x-ae,x-ae> = <x,x>-2a<x,e>+a^2<e,e> =
             * a^2+a^2-2a<x,e> = 2a*(a - <x,e>).
             * Here <x, e> is now qr[minor][minor].
             * v = x-ae is stored in the column at qr:
             */
            qrtMinor[minor] = qrtMinor[minor].subtract(a); // now |v|^2 = -2a*(qr[minor][minor])

            /*
             * Transform the rest of the columns of the minor:
             * They will be transformed by the matrix H = I-2vv'/|v|^2.
             * If x is a column vector of the minor, then
             * Hx = (I-2vv'/|v|^2)x = x-2vv'x/|v|^2 = x - 2<x,v>/|v|^2 v.
             * Therefore the transformation is easily calculated by
             * subtracting the column vector (2<x,v>/|v|^2)v from x.
             *
             * Let 2<x,v>/|v|^2 = alpha. From above we have
             * |v|^2 = -2a*(qr[minor][minor]), so
             * alpha = -<x,v>/(a*qr[minor][minor])
             */
            for (int col = minor + 1; col < matrix.length; col++) {
                final T[] qrtCol = matrix[col];
                T alpha = zero;
                for (int row = minor; row < qrtCol.length; row++) {
                    alpha = alpha.subtract(qrtCol[row].multiply(qrtMinor[row]));
                }
                alpha = alpha.divide(a.multiply(qrtMinor[minor]));

                // Subtract the column vector alpha*v from x.
                for (int row = minor; row < qrtCol.length; row++) {
                    qrtCol[row] = qrtCol[row].subtract(alpha.multiply(qrtMinor[row]));
                }
            }
        }
    }


    /**
     * Returns the matrix R of the decomposition.
     * <p>R is an upper-triangular matrix</p>
     *
     * @return the R matrix
     */
    public FieldMatrix<T> getR() {

        if (cachedR == null) {

            // R is supposed to be m x n
            final int n = qrt.length;
            final int m = qrt[0].length;
            T[][] ra = MathArrays.buildArray(threshold.getField(), m, n);
            // copy the diagonal from rDiag and the upper triangle of qr
            for (int row = FastMath.min(m, n) - 1; row >= 0; row--) {
                ra[row][row] = rDiag[row];
                for (int col = row + 1; col < n; col++) {
                    ra[row][col] = qrt[col][row];
                }
            }
            cachedR = MatrixUtils.createFieldMatrix(ra);
        }

        // return the cached matrix
        return cachedR;
    }

    /**
     * Returns the matrix Q of the decomposition.
     * <p>Q is an orthogonal matrix</p>
     *
     * @return the Q matrix
     */
    public FieldMatrix<T> getQ() {
        if (cachedQ == null) {
            cachedQ = getQT().transpose();
        }
        return cachedQ;
    }

    /**
     * Returns the transpose of the matrix Q of the decomposition.
     * <p>Q is an orthogonal matrix</p>
     *
     * @return the transpose of the Q matrix, Q<sup>T</sup>
     */
    public FieldMatrix<T> getQT() {
        if (cachedQT == null) {

            // QT is supposed to be m x m
            final int n = qrt.length;
            final int m = qrt[0].length;
            T[][] qta = MathArrays.buildArray(threshold.getField(), m, m);

            /*
             * Q = Q1 Q2 ... Q_m, so Q is formed by first constructing Q_m and then
             * applying the Householder transformations Q_(m-1),Q_(m-2),...,Q1 in
             * succession to the result
             */
            for (int minor = m - 1; minor >= FastMath.min(m, n); minor--) {
                qta[minor][minor] = threshold.getField().getOne();
            }

            for (int minor = FastMath.min(m, n) - 1; minor >= 0; minor--) {
                final T[] qrtMinor = qrt[minor];
                qta[minor][minor] = threshold.getField().getOne();
                if (qrtMinor[minor].getReal() != 0.0) {
                    for (int col = minor; col < m; col++) {
                        T alpha = threshold.getField().getZero();
                        for (int row = minor; row < m; row++) {
                            alpha = alpha.subtract(qta[col][row].multiply(qrtMinor[row]));
                        }
                        alpha = alpha.divide(rDiag[minor].multiply(qrtMinor[minor]));

                        for (int row = minor; row < m; row++) {
                            qta[col][row] = qta[col][row].add(alpha.negate().multiply(qrtMinor[row]));
                        }
                    }
                }
            }
            cachedQT = MatrixUtils.createFieldMatrix(qta);
        }

        // return the cached matrix
        return cachedQT;
    }

    /**
     * Returns the Householder reflector vectors.
     * <p>H is a lower trapezoidal matrix whose columns represent
     * each successive Householder reflector vector. This matrix is used
     * to compute Q.</p>
     *
     * @return a matrix containing the Householder reflector vectors
     */
    public FieldMatrix<T> getH() {
        if (cachedH == null) {

            final int n = qrt.length;
            final int m = qrt[0].length;
            T[][] ha = MathArrays.buildArray(threshold.getField(), m, n);
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < FastMath.min(i + 1, n); ++j) {
                    ha[i][j] = qrt[j][i].divide(rDiag[j].negate());
                }
            }
            cachedH = MatrixUtils.createFieldMatrix(ha);
        }

        // return the cached matrix
        return cachedH;
    }

    /**
     * Get a solver for finding the A &times; X = B solution in least square sense.
     * <p>
     * Least Square sense means a solver can be computed for an overdetermined system,
     * (i.e. a system with more equations than unknowns, which corresponds to a tall A
     * matrix with more rows than columns). In any case, if the matrix is singular
     * within the tolerance set at {@link #FieldQRDecomposition(FieldMatrix,
     * RealFieldElement) construction}, an error will be triggered when
     * the {@link DecompositionSolver#solve(RealVector) solve} method will be called.
     * </p>
     *
     * @return a solver
     */
    public FieldDecompositionSolver<T> getSolver() {
        return new FieldSolver<T>(qrt, rDiag, threshold);
    }

    /**
     * Specialized solver.
     *
     * @param <T> type of the underlying field elements
     */
    private static class FieldSolver<T extends RealFieldElement<T>> implements FieldDecompositionSolver<T> {
        /**
         * A packed TRANSPOSED representation of the QR decomposition.
         * <p>The elements BELOW the diagonal are the elements of the UPPER triangular
         * matrix R, and the rows ABOVE the diagonal are the Householder reflector vectors
         * from which an explicit form of Q can be recomputed if desired.</p>
         */
        private final T[][] qrt;
        /**
         * The diagonal elements of R.
         */
        private final T[] rDiag;
        /**
         * Singularity threshold.
         */
        private final T threshold;

        /**
         * Build a solver from decomposed matrix.
         *
         * @param qrt       Packed TRANSPOSED representation of the QR decomposition.
         * @param rDiag     Diagonal elements of R.
         * @param threshold Singularity threshold.
         */
        private FieldSolver(final T[][] qrt,
                            final T[] rDiag,
                            final T threshold) {
            this.qrt = qrt;
            this.rDiag = rDiag;
            this.threshold = threshold;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isNonSingular() {
            return !checkSingular(rDiag, threshold, false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FieldVector<T> solve(FieldVector<T> b) {
            final int n = qrt.length;
            final int m = qrt[0].length;
            if (b.getDimension() != m) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                        b.getDimension(), m);
            }
            checkSingular(rDiag, threshold, true);

            final T[] x = MathArrays.buildArray(threshold.getField(), n);
            final T[] y = b.toArray();

            // apply Householder transforms to solve Q.y = b
            for (int minor = 0; minor < FastMath.min(m, n); minor++) {

                final T[] qrtMinor = qrt[minor];
                T dotProduct = threshold.getField().getZero();
                for (int row = minor; row < m; row++) {
                    dotProduct = dotProduct.add(y[row].multiply(qrtMinor[row]));
                }
                dotProduct = dotProduct.divide(rDiag[minor].multiply(qrtMinor[minor]));

                for (int row = minor; row < m; row++) {
                    y[row] = y[row].add(dotProduct.multiply(qrtMinor[row]));
                }
            }

            // solve triangular system R.x = y
            for (int row = rDiag.length - 1; row >= 0; --row) {
                y[row] = y[row].divide(rDiag[row]);
                final T yRow = y[row];
                final T[] qrtRow = qrt[row];
                x[row] = yRow;
                for (int i = 0; i < row; i++) {
                    y[i] = y[i].subtract(yRow.multiply(qrtRow[i]));
                }
            }

            return new ArrayFieldVector<T>(x, false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FieldMatrix<T> solve(FieldMatrix<T> b) {
            final int n = qrt.length;
            final int m = qrt[0].length;
            if (b.getRowDimension() != m) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                        b.getRowDimension(), m);
            }
            checkSingular(rDiag, threshold, true);

            final int columns = b.getColumnDimension();
            final int blockSize = BlockFieldMatrix.BLOCK_SIZE;
            final int cBlocks = (columns + blockSize - 1) / blockSize;
            final T[][] xBlocks = BlockFieldMatrix.createBlocksLayout(threshold.getField(), n, columns);
            final T[][] y = MathArrays.buildArray(threshold.getField(), b.getRowDimension(), blockSize);
            final T[] alpha = MathArrays.buildArray(threshold.getField(), blockSize);

            for (int kBlock = 0; kBlock < cBlocks; ++kBlock) {
                final int kStart = kBlock * blockSize;
                final int kEnd = FastMath.min(kStart + blockSize, columns);
                final int kWidth = kEnd - kStart;

                // get the right hand side vector
                b.copySubMatrix(0, m - 1, kStart, kEnd - 1, y);

                // apply Householder transforms to solve Q.y = b
                for (int minor = 0; minor < FastMath.min(m, n); minor++) {
                    final T[] qrtMinor = qrt[minor];
                    final T factor = rDiag[minor].multiply(qrtMinor[minor]).reciprocal();

                    Arrays.fill(alpha, 0, kWidth, threshold.getField().getZero());
                    for (int row = minor; row < m; ++row) {
                        final T d = qrtMinor[row];
                        final T[] yRow = y[row];
                        for (int k = 0; k < kWidth; ++k) {
                            alpha[k] = alpha[k].add(d.multiply(yRow[k]));
                        }
                    }

                    for (int k = 0; k < kWidth; ++k) {
                        alpha[k] = alpha[k].multiply(factor);
                    }

                    for (int row = minor; row < m; ++row) {
                        final T d = qrtMinor[row];
                        final T[] yRow = y[row];
                        for (int k = 0; k < kWidth; ++k) {
                            yRow[k] = yRow[k].add(alpha[k].multiply(d));
                        }
                    }
                }

                // solve triangular system R.x = y
                for (int j = rDiag.length - 1; j >= 0; --j) {
                    final int jBlock = j / blockSize;
                    final int jStart = jBlock * blockSize;
                    final T factor = rDiag[j].reciprocal();
                    final T[] yJ = y[j];
                    final T[] xBlock = xBlocks[jBlock * cBlocks + kBlock];
                    int index = (j - jStart) * kWidth;
                    for (int k = 0; k < kWidth; ++k) {
                        yJ[k] = yJ[k].multiply(factor);
                        xBlock[index++] = yJ[k];
                    }

                    final T[] qrtJ = qrt[j];
                    for (int i = 0; i < j; ++i) {
                        final T rIJ = qrtJ[i];
                        final T[] yI = y[i];
                        for (int k = 0; k < kWidth; ++k) {
                            yI[k] = yI[k].subtract(yJ[k].multiply(rIJ));
                        }
                    }
                }
            }

            return new BlockFieldMatrix<T>(n, columns, xBlocks, false);
        }

        /**
         * {@inheritDoc}
         *
         * @throws MathIllegalArgumentException if the decomposed matrix is singular.
         */
        @Override
        public FieldMatrix<T> getInverse() {
            return solve(MatrixUtils.createFieldIdentityMatrix(threshold.getField(), qrt[0].length));
        }

        /**
         * Check singularity.
         *
         * @param diag  Diagonal elements of the R matrix.
         * @param min   Singularity threshold.
         * @param raise Whether to raise a {@link MathIllegalArgumentException}
         *              if any element of the diagonal fails the check.
         * @return {@code true} if any element of the diagonal is smaller
         * or equal to {@code min}.
         * @throws MathIllegalArgumentException if the matrix is singular and
         *                                      {@code raise} is {@code true}.
         */
        private boolean checkSingular(T[] diag,
                                      T min,
                                      boolean raise) {
            final int len = diag.length;
            for (int i = 0; i < len; i++) {
                final T d = diag[i];
                if (FastMath.abs(d.getReal()) <= min.getReal()) {
                    if (raise) {
                        throw new MathIllegalArgumentException(LocalizedCoreFormats.SINGULAR_MATRIX);
                    } else {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
