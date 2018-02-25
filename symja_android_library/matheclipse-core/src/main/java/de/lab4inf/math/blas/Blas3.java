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

import static de.lab4inf.math.blas.Blas2.dgemv;
import static de.lab4inf.math.blas.Blas2.dgemvt;
import static java.lang.String.format;

/**
 * <h1> BLAS III </h1>
 * <p>
 * Basic Linear Algebra Subroutines Level 3.
 * Performs scaled matrix-matrix multiplication
 * <pre>
 *
 *   C  &larr; &alpha;*A*B + &beta;*C
 *
 * </pre>
 * with optional transpose of A and/or B using a parallel algorithm
 * on multi-processor systems if the matrix size is large.
 * <br/>
 * These BLAS 3 methods have for larger matrixes roughly a factor
 * three to four times number of processors speed-up gain compared to naive
 * matrix multiplication.
 *
 * @author nwulff
 * @version $Id: Blas3.java,v 1.24 2014/11/16 21:47:23 nwulff Exp $
 * @see de.lab4inf.math.lapack.LinearAlgebra
 * @since 30.01.2013
 */
public final class Blas3 extends BasicBlas {
    /* Parallel execution timeout within 1 minute. */
    private static ParallizerPool<Parallizer3> pool;

    static {
        final int nCached = NPROCESSORS * 2;
        LOGGER.info(format("creating #%d parallizers", nCached));
        Parallizer3 worker;
        final Parallizer3[] workers = new Parallizer3[nCached];
        for (int i = 0; i < nCached; i++) {
            worker = new Parallizer3();
            worker.setDaemon(true);
            worker.setPriority(Thread.MAX_PRIORITY);
            worker.start();
            workers[i] = worker;
        }
        pool = new ParallizerPool<Parallizer3>(workers);
    }

    /**
     * Hidden constructor for a utility class.
     */
    private Blas3() {
    }

    /**
     * Perform the matrix-matrix calculation:
     * <pre>
     *   C  &larr; alpha*A*B + beta*C
     * </pre>
     * for <b>symmetric matrixes</b> A,B and C.
     *
     * @param alpha scale for A*B
     * @param a     matrix A
     * @param b     matrix B
     * @param beta  scale for C
     * @param c     matrix C
     */
    public static void dsymm(final double alpha, final double[][] a, final double[][] b, final double beta,
                             final double[][] c) {
        checkDimension(false, false, a, b, c);
        psymm(alpha, a, b, beta, c);
    }

    /**
     * Perform the matrix-matrix calculation:
     * <pre>
     *   C  &larr; alpha*A*B + beta*C
     * </pre>
     *
     * @param alpha scale for A*B
     * @param a     matrix A
     * @param b     matrix B
     * @param beta  scale for C
     * @param c     matrix C
     */
    public static void dgemm(final double alpha, final double[][] a, final double[][] b, final double beta,
                             final double[][] c) {
        pgemm(alpha, a, b, beta, c);
    }

    /**
     * Perform the matrix-matrix calculation:
     * <pre>
     *   C  &larr; alpha*A*B + beta*C
     * </pre>
     * with optional transpose of A or B matrix.
     *
     * @param transposeA flag to indicate transpose A
     * @param transposeB flag to indicate transpose A
     * @param alpha      scale for A*B
     * @param a          matrix A
     * @param b          matrix B
     * @param beta       scale for C
     * @param c          matrix C
     */
    public static void dgemm(final boolean transposeA, final boolean transposeB, final double alpha,
                             final double[][] a, final double[][] b, final double beta, final double[][] c) {
        checkDimension(transposeA, transposeB, a, b, c);
        if (transposeA) {
            if (transposeB) {
                pgemmATBT(alpha, a, b, beta, c);
            } else {
                pgemmAT(alpha, a, b, beta, c);
            }
        } else {
            if (transposeB) {
                pgemmBT(alpha, a, b, beta, c);
            } else {
                pgemm(alpha, a, b, beta, c);
            }
        }
    }

    /**
     * Perform the matrix-matrix calculation:
     * <pre>
     *   C  &larr; alpha*A*B + beta*C
     * </pre>
     * for <b> symmetric matrixes</b>.
     *
     * @param alpha scale for A*B
     * @param a     matrix A
     * @param b     matrix B
     * @param beta  scale for C
     * @param c     matrix C
     */
    private static void psymm(final double alpha, final double[][] a, final double[][] b, final double beta,
                              final double[][] c) {
        final int n = a.length;
        if (shouldRunParallel(n)) {
            final CountDownLatch latch = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                parallelUpdate(false, latch, alpha, a[i], b, beta, c[i]);
            }
            waitForFinish(latch);
        } else {
            for (int i = 0; i < n; i++) {
                dgemv(alpha, b, a[i], beta, c[i]);
            }
        }
    }

    /**
     * Perform the matrix-matrix calculation:
     * <pre>
     *   C  &larr; alpha*A*B + beta*C
     * </pre>
     *
     * @param alpha scale for A*B
     * @param a     matrix A
     * @param b     matrix B
     * @param beta  scale for C
     * @param c     matrix C
     */
    private static void pgemm(final double alpha, final double[][] a, final double[][] b, final double beta,
                              final double[][] c) {
        final int n = c.length;
        final int m = c[0].length;
        if (shouldRunParallel(n, m)) {
            final double[][] bt = transpose(b);
            final CountDownLatch latch = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                parallelUpdate(false, latch, alpha, a[i], bt, beta, c[i]);
                // this takes much longer as one single transpose op...
                // parallelUpdate(true,latch, alpha, a[i], b, beta, c[i]);
            }
            waitForFinish(latch);
        } else {
            for (int i = 0; i < n; i++) {
                // dgemv(alpha,bt,a[i],beta,c[i]);
                dgemvt(alpha, b, a[i], beta, c[i]);
            }
        }
    }

    /**
     * Perform the matrix-matrix calculation:
     * <pre>
     *   C  &larr; alpha*A*B + beta*C
     * </pre>
     * with internally transposed matrix A.
     *
     * @param alpha scale for A*B
     * @param a     matrix A
     * @param b     matrix B
     * @param beta  scale for C
     * @param c     matrix C
     */
    private static void pgemmAT(final double alpha, final double[][] a, final double[][] b, final double beta,
                                final double[][] c) {
        final int n = c.length;
        final int m = c[0].length;
        if (shouldRunParallel(n, m)) {
            final double[][] bt = transpose(b);
            final CountDownLatch latch = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                parallelUpdate(false, latch, alpha, column(a, i), bt, beta, c[i]);
            }
            waitForFinish(latch);
        } else {
            for (int i = 0; i < n; i++) {
                dgemvt(alpha, b, column(a, i), beta, c[i]);
            }
        }
    }

    /**
     * Perform the matrix-matrix calculation:
     * <pre>
     *   C  &larr; alpha*A*B + beta*C
     * </pre>
     * with internally transposed matrix B.
     *
     * @param alpha scale for A*B
     * @param a     matrix A
     * @param b     matrix B
     * @param beta  scale for C
     * @param c     matrix C
     */
    private static void pgemmBT(final double alpha, final double[][] a, final double[][] b, final double beta,
                                final double[][] c) {
        final int n = c.length;
        final int m = c[0].length;
        if (shouldRunParallel(n, m)) {
            final CountDownLatch latch = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                parallelUpdate(false, latch, alpha, a[i], b, beta, c[i]);
            }
            waitForFinish(latch);
        } else {
            for (int i = 0; i < n; i++) {
                dgemv(alpha, b, a[i], beta, c[i]);
            }
        }
    }

    /**
     * Perform the matrix-matrix calculation:
     * <pre>
     *   C  &larr; alpha*A*B + beta*C
     * </pre>
     * with internally transposed matrix A and B.
     *
     * @param alpha scale for A*B
     * @param a     matrix A
     * @param b     matrix B
     * @param beta  scale for C
     * @param c     matrix C
     */
    private static void pgemmATBT(final double alpha, final double[][] a, final double[][] b, final double beta,
                                  final double[][] c) {
        final int n = c.length;
        final int m = c[0].length;
        if (shouldRunParallel(n, m)) {
            final CountDownLatch latch = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                parallelUpdate(false, latch, alpha, column(a, i), b, beta, c[i]);
            }
            waitForFinish(latch);
        } else {
            for (int i = 0; i < n; i++) {
                dgemv(alpha, b, column(a, i), beta, c[i]);
            }
        }
    }

    private static void checkDimension(final boolean transA, final boolean transB, final double[][] a,
                                       final double[][] b, final double[][] c) {
        int na = a.length;
        final int nb = b.length;
        final int nc = c.length;
        final int ma = a[0].length;
        int mb = b[0].length;
        final int mc = c[0].length;
        int la = ma;
        int lb = nb;

        if (transA) {
            la = na;
            na = ma;
        }
        if (transB) {
            lb = mb;
            mb = nb;
        }
        if (la != lb) {
            final String msg = format("no M(%d,%d) * M(%d,%d)", na, ma, nb, mb);
            throw new IllegalArgumentException(msg);
        }

        if (na != nc || mb != mc) {
            final String msg = format("no M(%d,%d) + M(%d,%d)", na, ma, nc, mc);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Start a parallel thread for execution.
     */
    private static void parallelUpdate(final boolean transpose, final CountDownLatch latch, final double alpha,
                                       final double[] a, final double[][] b, final double beta, final double[] c) {
        final Parallizer3 runner = pool.require();
        runner.setTranspose(transpose);
        runner.parallize(latch, alpha, a, b, beta, c);
    }

    /**
     * Internal runnable to execute this matrix multiplication
     * in parallel worker threads.
     */
    static class Parallizer3 extends BasicBlas.Parallizer {
        private static int pId;
        private boolean transpose;
        private double alpha, beta;
        private double[][] b;
        private double[] a, c;

        protected Parallizer3() {
            super(format("Parallizer-III: %3d", ++pId));
        }

        synchronized void parallize(final CountDownLatch latch, final double ascale, final double[] va,
                                    final double[][] matb, final double bscale, final double[] vc) {
            if (null != counter) {
                final String msg = format("Parallizer %s still in use!", this);
                throw new IllegalStateException(msg);
            }
            alpha = ascale;
            beta = bscale;
            a = va;
            b = matb;
            c = vc;
            counter = latch;
            if (null != a) {
                notifyAll();
            }
        }

        @Override
        protected void doWork() {
            if (isTranspose())
                dgemvt(alpha, b, a, beta, c);
            else
                dgemv(alpha, b, a, beta, c);
            a = null;
            b = null;
            c = null;
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
 