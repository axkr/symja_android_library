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

import java.lang.reflect.Array;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;

/**
 * Common base class for the BLAS routines.
 *
 * @author nwulff
 * @version $Id: BasicBlas.java,v 1.12 2013/06/26 10:36:34 nwulff Exp $
 * @since 19.02.2013
 */
public abstract class BasicBlas extends de.lab4inf.math.L4MObject {
    /**
     * Signal a column mismatch.
     */
    public static final String COL_MISMATCH = "column mismatch %d != %d";
    /**
     * Signal a row mismatch.
     */
    public static final String ROW_MISMATCH = "row mismatch %d != %d";
    /**
     * Thread group for all parallel BLAS workers.
     */
    //    protected static final ThreadGroup BLAS_THREAD_GROUP = new ThreadGroup("BLAS");
    public static final int RUN_PARALLEL_SIZE = 64;
    /* Number of CPUs. */
    public static final int NPROCESSORS = Runtime.getRuntime().availableProcessors();
    /* Parallel execution timeout within 1 minute. */
    public static final long TIME_OUT = 1000 * 60;
    public static final TimeUnit TIME_OUT_UNIT = TimeUnit.MILLISECONDS;
    /**
     * Common thread name to identify BLAS workers.
     */
    private static final String BLAS = "BLAS-";
    private static final String TIMED_OUT_MSG = "calculation has timed out";

    /**
     * Synchronization point to wait for parallel executions to be finished.
     *
     * @param latch to wait at
     */
    protected static void waitForFinish(final CountDownLatch latch) {
        try {
            if (!latch.await(TIME_OUT, TIME_OUT_UNIT))
                throw new TimeoutException(TIMED_OUT_MSG);
        } catch (Exception cause) {
            LOGGER.error(cause);
            throw new RuntimeException(cause);
        }
    }

    protected static boolean checkThread() {
        //return !BLAS_THREAD_GROUP.equals(Thread.currentThread().getThreadGroup());
        String name = Thread.currentThread().getName();
        return !name.startsWith(BLAS);
    }

    protected static boolean shouldRunParallel(final int n) {
        if (n > RUN_PARALLEL_SIZE) {
            return checkThread();
        }
        return false;
    }

    protected static boolean shouldRunParallel(final int n, final int m) {
        if (n > RUN_PARALLEL_SIZE || m > RUN_PARALLEL_SIZE) {
            return checkThread();
        }
        return false;
    }

    /**
     * Get the column vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param col the column index
     * @return mat[?][col] vector
     */
    public static double[] column(final double[][] mat, final int col) {
        final int n = mat.length;
        final int m = mat[0].length;
        double[] v = new double[n];
        if (col < 0 || col >= m) {
            throw new IllegalArgumentException(format(COL_MISMATCH, col, m));
        }
        for (int i = 0; i < n; v[i] = mat[i][col], i++) ;
        return v;
    }

    /**
     * Get the transposed matrix.
     *
     * @param mat matrix to transpose
     * @return transpose of mat
     */
    public static double[][] transpose(final double[][] mat) {
        final int n = mat.length;
        final int m = mat[0].length;
        double[][] trans = new double[m][n];
        for (int j = 0; j < n; j++)
            for (int k = 0; k < m; trans[k][j] = mat[j][k], k++) ;
        return trans;
    }

    /**
     * Internal Thread pool for parallel execution.
     *
     * @param <T> type of parallizer threads
     */
    protected static final class ParallizerPool<T> {
        private final T[] resources;
        private volatile int free;

        public ParallizerPool(final T[] res) {
            resources = allocate(res);
            free = resources.length;
        }

        /**
         * allocate private storage for the thread pool.
         *
         * @param workers seed
         * @return allocated storage
         */
        private T[] allocate(final T... workers) {
            int dim = workers.length;
            @SuppressWarnings("unchecked")
            Class<T> type = (Class<T>) workers[0].getClass();
            @SuppressWarnings("unchecked")
            T[] ret = (T[]) Array.newInstance(type, dim);
            for (int j = 0; j < dim; ret[j] = workers[j], j++) ;
            return ret;
        }

        /**
         * Release a worker thread to the pool
         *
         * @param worker thread to release
         */
        public synchronized void release(final T worker) {
            resources[free++] = worker;
            notifyAll();
        }

        /**
         * Acquire a worker thread from the pool.
         *
         * @return worker thread
         */
        public synchronized T require() {
            try {
                while (free <= 0) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return resources[--free];
        }
    }

    /**
     * Internal runnable to execute BLAS operations in parallel worker threads.
     * The doWork does all the work.
     */
    protected abstract static class Parallizer extends Thread {
        protected volatile CountDownLatch counter;
        private boolean hasToWork = true;

        protected Parallizer(final String name) {
            // super(BLAS_THREAD_GROUP,name);
            super(format("%s-%s", BLAS, name));
            LOGGER.info("created " + this);
        }

        /**
         * Internal worker method to be implemented.
         */
        protected abstract void doWork();

        protected abstract void finishedWork();

        /*
         * (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public final void run() {
            try {
                while (hasToWork) {
                    synchronized (this) {
                        while (null == counter) {
                            //LOGGER.error("going into wait " + this);
                            wait();
                        }
                    }
                    try {
                        doWork();
                    } catch (Exception cause) {
                        LOGGER.error(cause);
                    } finally {
                        finishedWork();
                    }
                }
            } catch (Throwable cause) {
                LOGGER.error(cause);
                cause.printStackTrace();
            }
        }
    }
}
 