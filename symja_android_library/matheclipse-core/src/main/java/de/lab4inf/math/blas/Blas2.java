/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2013,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.blas;

import java.util.concurrent.CountDownLatch;

import static de.lab4inf.math.blas.Blas1.ddot;
import static java.lang.String.format;

/**
 * <h1> BLAS II </h1>
 * <p>
 * Basic Linear Algebra Subroutines Level 2.
 * Performs scaled matrix-vector multiplication
 * <pre>
 *
 *   y  &larr; &alpha;*A*x + &beta;*y
 *
 * </pre>
 * with optional increments in x or y.
 *
 * @author nwulff
 * @version $Id: Blas2.java,v 1.18 2014/11/16 21:47:23 nwulff Exp $
 * @since 29.01.2013
 */
public final class Blas2 extends BasicBlas {
    private static final int CHUNK = 32;
    private static ParallizerPool<Parallizer2> pool;

    static {
        final int nCached = NPROCESSORS * 2;
        Parallizer2 worker;
        final Parallizer2[] workers = new Parallizer2[nCached];
        for (int i = 0; i < nCached; i++) {
            worker = new Parallizer2();
            worker.setDaemon(true);
            worker.setPriority(Thread.MAX_PRIORITY);
            worker.start();
            workers[i] = worker;
        }
        pool = new ParallizerPool<Parallizer2>(workers);
        LOGGER.info(format("creating #%d parallizers", nCached));
    }

    /**
     * Hidden constructor for a utility class.
     */
    private Blas2() {
    }

    /**
     * Perform the calculation:
     * <pre>
     *   y  &larr; alpha*A*x + beta*y
     * </pre>
     *
     * @param alpha the multiplication factor for the matrix
     * @param a     the matrix coefficients
     * @param x     the x vector
     * @param beta  the multiplication factor for the vector y
     * @param y     the result vector y
     */
    public static void dgemv(final double alpha, final double[][] a, final double[] x, final double beta,
                             final double[] y) {
        pgemv(alpha, a, x, beta, y);
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*AT*x + beta*y
     * </pre>
     * with internal transpose of A.
     *
     * @param alpha the multiplication factor for the matrix
     * @param a     the matrix coefficients
     * @param x     the x vector
     * @param beta  the multiplication factor for the vector y
     * @param y     the result vector y
     */
    public static void dgemvt(final double alpha, final double[][] a, final double[] x, final double beta,
                              final double[] y) {
        checkDimension(true, a, x, 1, y, 1);
        pgemvt(alpha, a, x, beta, y);
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*A*x + beta*y
     * </pre>
     * with optional in situ transpose of A.
     *
     * @param transpose if true transpose matrix multiplication
     * @param alpha     the multiplication factor for the matrix
     * @param a         the matrix coefficients
     * @param x         the x vector
     * @param beta      the multiplication factor for the vector y
     * @param y         the result vector y
     */
    public static void dgemv(final boolean transpose, final double alpha, final double[][] a, final double[] x,
                             final double beta, final double[] y) {
        checkDimension(transpose, a, x, 1, y, 1);
        if (transpose) {
            pgemvt(alpha, a, x, beta, y);
        } else {
            pgemv(alpha, a, x, beta, y);
        }
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*A*x + beta*y
     * </pre>
     * for a <b>symmetric matrix A</b>.
     * For speed-up reasons the symmetry will not be
     * checked but has to be guaranteed by the caller.
     *
     * @param alpha the multiplication factor for the matrix
     * @param a     the matrix coefficients
     * @param x     the x vector
     * @param beta  the multiplication factor for the vector y
     * @param y     the result vector y
     */
    public static void dsymv(final double alpha, final double[][] a, final double[] x, final double beta,
                             final double[] y) {
        pgemv(alpha, a, x, beta, y);
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*A*x + beta*y
     * </pre>
     * with optional increments in x and y for a <b>symmetric matrix A</b>.
     * For speed-up reasons the symmetry will not be
     * checked but has to be guaranteed by the caller.
     *
     * @param alpha the multiplication factor for the matrix
     * @param a     the matrix coefficients
     * @param x     the x vector
     * @param incX  the x increment
     * @param beta  the multiplication factor for the vector y
     * @param y     the result vector y
     * @param incY  the y increment
     */
    public static void dsymv(final double alpha, final double[][] a, final double[] x, final int incX,
                             final double beta, final double[] y, final int incY) {
        checkDimension(true, a, x, incX, y, incY);
        dgemv(alpha, a, x, incX, beta, y, incY);
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*A*x + beta*y
     * </pre>
     * with optional increments in x and y.
     *
     * @param transpose if true transpose matrix multiplication
     * @param alpha     the multiplication factor for the matrix
     * @param a         the matrix coefficients
     * @param x         the x vector
     * @param incX      the x increment
     * @param beta      the multiplication factor for the vector y
     * @param y         the result vector y
     * @param incY      the y increment
     */
    public static void dgemv(final boolean transpose, final double alpha, final double[][] a, final double[] x,
                             final int incX, final double beta, final double[] y, final int incY) {
        checkDimension(transpose, a, x, incX, y, incY);
        if (transpose) {
            dgemvt(alpha, a, x, incX, beta, y, incY);
        } else {
            dgemv(alpha, a, x, incX, beta, y, incY);
        }
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*AT*x + beta*y
     * </pre>
     * with optional increments in x and y using internally the transpose of A.
     *
     * @param alpha the multiplication factor for the matrix
     * @param a     the matrix coefficients
     * @param x     the x vector
     * @param incX  the x increment
     * @param beta  the multiplication factor for the vector y
     * @param y     the result vector y
     * @param incY  the y increment
     */
    static void dgemvt(final double alpha, final double[][] a, final double[] x, final int incX, final double beta,
                       final double[] y, final int incY) {
        final int n = a[0].length;
        int j, jy;
        if (incX == 1 && incY == 1) {
            pgemvt(alpha, a, x, beta, y);
        } else {
            for (jy = 0, j = 0; j < n; jy += incY, j++) {
                update(jy, ddot(n, column(a, j), 1, x, incX), alpha, beta, y);
            }
        }
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*A*x + beta*y
     * </pre>
     * with optional increments in x and y.
     *
     * @param alpha the multiplication factor for the matrix
     * @param a     the matrix coefficients
     * @param x     the x vector
     * @param incX  the x increment
     * @param beta  the multiplication factor for the vector y
     * @param y     the result vector y
     * @param incY  the y increment
     */
    static void dgemv(final double alpha, final double[][] a, final double[] x, final int incX, final double beta,
                      final double[] y, final int incY) {
        final int n = a.length;
        int j, jy;
        if (incX == 1 && incY == 1) {
            pgemv(alpha, a, x, beta, y);
        } else {
            for (jy = 0, j = 0; j < n; jy += incY, j++) {
                update(jy, ddot(n, a[j], 1, x, incX), alpha, beta, y);
            }
        }
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*A*x + beta*y
     * </pre>
     *
     * @param alpha the multiplication factor for the matrix
     * @param a     the matrix coefficients
     * @param x     the x vector
     * @param beta  the multiplication factor for the vector y
     * @param y     the result vector y
     */
    static void pgemv(final double alpha, final double[][] a, final double[] x, final double beta, final double[] y) {
        final int n = a.length;
        if (shouldRunParallel(n)) {
            int j, b, e;
            final int m = n / CHUNK;
            final CountDownLatch latch = new CountDownLatch(m);
            for (b = 0, e = b + CHUNK, j = 0; j < m; b = e, e += CHUNK, j++) {
                parallelUpdate(false, latch, b, e, alpha, a, x, beta, y);
            }
            for (j = b; j < n; update(j, ddot(a[j], x), alpha, beta, y), j++)
                ;
            waitForFinish(latch);
        } else {
            for (int j = 0; j < n; update(j, ddot(a[j], x), alpha, beta, y), j++)
                ;
        }
    }

    /**
     * Perform the matrix-vector calculation:
     * <pre>
     *   y  &larr; alpha*AT*x + beta*y
     * </pre>
     * using internally the transpose of A.
     *
     * @param alpha the multiplication factor for the matrix
     * @param a     the matrix coefficients
     * @param x     the x vector
     * @param beta  the multiplication factor for the vector y
     * @param y     the result vector y
     */
    static void pgemvt(final double alpha, final double[][] a, final double[] x, final double beta, final double[] y) {
        final int n = a[0].length;
        if (shouldRunParallel(n)) {
            int j, b, e;
            final int m = n / CHUNK;
            final CountDownLatch latch = new CountDownLatch(m);
            for (b = 0, e = b + CHUNK, j = 0; j < m; b = e, e += CHUNK, j++) {
                parallelUpdate(true, latch, b, e, alpha, a, x, beta, y);
            }
            for (j = b; j < n; update(j, ddot(column(a, j), x), alpha, beta, y), j++)
                ;
            waitForFinish(latch);
        } else {
            for (int j = 0; j < n; j++) {
                update(j, ddot(column(a, j), x), alpha, beta, y);
            }
        }
    }

    /**
     * Internal kernel for the vector update.
     */
    private static void update(final int j, final double prod, final double alpha, final double beta, final double[] y) {
        double tmp = prod;
        if (beta == 0)
            y[j] = 0;
        else if (beta != 1)
            y[j] *= beta;
        if (alpha != 1)
            tmp *= alpha;
        y[j] += tmp;
    }

    /**
     * Start a parallel thread for execution.
     */
    private static void parallelUpdate(final boolean transpose, final CountDownLatch latch, final int j, final int k,
                                       final double alpha, final double[][] a, final double[] x, final double beta, final double[] y) {
        final Parallizer2 runner = pool.require();
        runner.setTranspose(transpose);
        runner.parallize(latch, j, k, alpha, a, x, beta, y);
    }

    private static void checkDimension(final boolean transpose, final double[][] a, final double[] x, final int incX,
                                       final double[] y, final int incY) {
        final int na = a.length;
        final int ma = a[0].length;
        final int nx = x.length;
        final int ny = y.length;
        if (transpose) {
            if (na * incX != nx) {
                final String msg = String.format("no AT(%d,%d) * x(%d) inc: %d", na, ma, nx, incX);
                throw new IllegalArgumentException(msg);
            }
            if (ma * incY != ny) {
                final String msg = String.format("no AT(%d,%d) + y(%d) inc: %d", na, ma, ny, incY);
                throw new IllegalArgumentException(msg);
            }
        } else {
            if (ma * incX != nx) {
                final String msg = String.format("no A(%d,%d) * x(%d) inc:%d", na, ma, nx, incX);
                throw new IllegalArgumentException(msg);
            }
            if (na * incY != ny) {
                final String msg = String.format("no A(%d,%d) + y(%d) inc:%d", na, ma, ny, incY);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    /**
     * Internal runnable to execute this matrix multiplication
     * in parallel worker threads.
     */
    static class Parallizer2 extends BasicBlas.Parallizer {
        private static int pId;
        private boolean transpose;
        private double alpha, beta;
        private double[][] a;
        private double[] x, y;
        private int b, e;

        protected Parallizer2() {
            super(format("Parallizer-II: %3d", ++pId));
        }

        synchronized void parallize(final CountDownLatch latch, final int start, final int end, final double sa,
                                    final double[][] ma, final double[] vx, final double sb, final double[] vy) {
            if (null != counter) {
                final String msg = format("Parallizer %s still in use!", this);
                throw new IllegalStateException(msg);
            }
            alpha = sa;
            beta = sb;
            a = ma;
            x = vx;
            y = vy;
            b = start;
            e = end;
            counter = latch;

            if (null != a) {
                notifyAll();
            }
        }

        @Override
        protected void doWork() {
            if (isTranspose()) {
                for (int j = b; j < e; j++) {
                    update(j, ddot(column(a, j), x), alpha, beta, y);
                }
            } else {
                for (int j = b; j < e; j++) {
                    update(j, ddot(a[j], x), alpha, beta, y);
                }
            }
            a = null;
            x = null;
            y = null;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.blas.BasicBlas.Parallizer#finishedWork()
         */
        @Override
        protected void finishedWork() {
            counter.countDown();
            counter = null;
            // LOGGER.error("finished work " + this);
            pool.release(this);
        }

        /**
         * @return the transpose
         */
        public boolean isTranspose() {
            return transpose;
        }

        /**
         * @param transpose the transpose to set
         */
        public void setTranspose(final boolean transpose) {
            this.transpose = transpose;
        }
    }

}
 