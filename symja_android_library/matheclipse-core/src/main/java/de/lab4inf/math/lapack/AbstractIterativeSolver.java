/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
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
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.lapack.LinearAlgebra.asDouble;
import static de.lab4inf.math.lapack.LinearAlgebra.asFloat;
import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.getCol;
import static de.lab4inf.math.lapack.LinearAlgebra.isDiagonalDominant;
import static de.lab4inf.math.lapack.LinearAlgebra.isSquare;
import static de.lab4inf.math.lapack.LinearAlgebra.rndVector;
import static de.lab4inf.math.lapack.LinearAlgebra.transpose;
import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.abs;

/**
 * Generic iterative Solver for A*x=y base class.
 * Starting from a random vector x_0 a sequence x_n converging
 * against the solution is generated, if the matrix norm ||A||
 * is less than one.
 *
 * @author nwulff
 * @version $Id: AbstractIterativeSolver.java,v 1.21 2014/02/11 17:30:54 nwulff Exp $
 * @since 20.01.2010
 */
public abstract class AbstractIterativeSolver extends LASolver {
    /**
     * Reference to GaussSeidelSolver.java.
     */
    protected static final String ITERATIONS_NEEDED = "%d iterations for %dx%d convergence";
    /**
     * Reference to GaussSeidelSolver.java.
     */
    protected static final String NO_CONVERGENCE = "no convergence!";
    /**
     * Reference to GaussSeidelSolver.java.
     */
    protected static final String NOT_DIAGONAL_DOMINANT = "matrix not diagonal dominant";
    /**
     * maximal number of iterations.
     */
    protected static final int ITE_MAX = 500;
    /**
     * Reference to AbstractIterativeSolver.java.
     */
    private static final String SWAPPING_DIAGONAL = "swapping zero diagonal element";
    /**
     * accuracy to reach.
     */
    private static double eps = 100 * Accuracy.DEPS;

    /**
     * Get the value of accuracy.
     *
     * @return the accuracy to aim for
     */
    public static double getEps() {
        return eps;
    }

    /**
     * Set the precision to reach.
     *
     * @param eps the accuracy to set
     */
    public static void setEps(final double eps) {
        AbstractIterativeSolver.eps = eps;
    }

    /**
     * Check if the given matrix A is square.
     *
     * @param a NxN matrix to check.
     */
    protected static void matrixCheck(final double[][] a) {
        if (!isSquare(a)) {
            String msg = NOT_SQUARE;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            throw error;
        }
    }

    /**
     * Check if the given matrix A is square.
     *
     * @param a   NxN matrix to check.
     * @param <T> the type of the elements
     */
    protected static <T extends Numeric<T>> void matrixCheck(final T[][] a) {
        if (!isSquare(a)) {
            String msg = NOT_SQUARE;
            IllegalArgumentException error = new IllegalArgumentException(msg);
            throw error;
        }
    }

    /**
     * Calculate a pivot vector for the given matrix a, so that after pivoting
     * the matrix is diagonal dominant if possible.
     *
     * @param a matrix to check
     * @return pivot array
     */
    public static int[] createPivot(final double[][] a) {
        int t, n = a.length;
        int[] p = new int[n];
        double aMax, aTmp;
        for (int i = 0; i < n; p[i] = i, i++) ;
        for (int j = 0; j < n; j++) {
            aMax = abs(a[p[j]][j]);
            for (int k = j + 1; k < n; k++) {
                aTmp = abs(a[p[k]][j]);
                if (aTmp > aMax) {
                    aMax = aTmp;
                    t = p[k];
                    p[k] = p[j];
                    p[j] = t;
                    //getLogger().error(String.format("%d <-> %d",p[k],p[j]));
                }
            }
            if (aMax == 0 && j < n - 2) {
                getLogger().warn(SWAPPING_DIAGONAL);
                t = p[j + 1];
                p[j + 1] = p[j];
                p[j] = t;
            }
        }
        return p;
    }

    /**
     * Calculate a pivot vector for the given matrix a, so that after pivoting
     * the matrix is diagonal dominant if possible.
     *
     * @param a   matrix to check
     * @param <T> the type of the elements
     * @return pivot array
     */
    public static <T extends Numeric<T>> int[] createPivot(final T[][] a) {
        int t, n = a.length;
        int[] p = new int[n];
        T aMax, aTmp;
        for (int i = 0; i < n; p[i] = i, i++) ;
        for (int j = 0; j < n; j++) {
            aMax = a[p[j]][j].abs();
            for (int k = j + 1; k < n; k++) {
                aTmp = a[p[k]][j].abs();
                if (aTmp.gt(aMax)) {
                    aMax = aTmp;
                    t = p[k];
                    p[k] = p[j];
                    p[j] = t;
                    getLogger().error(String.format("%d <-> %d", p[k], p[j]));
                }
            }
            if (aMax.isZero() && j < n - 2) {
                getLogger().warn(SWAPPING_DIAGONAL);
                t = p[j + 1];
                p[j + 1] = p[j];
                p[j] = t;
            }
        }
        return p;
    }

    /**
     * Solve the equation A*X = Y for X, using large
     * diagonal pivot elements if applicable.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[][] real NxN dimensional right side matrix.
     * @return double[][] real NxN dimensional solution matrix X.
     */
    @Override
    public double[][] solve(final double[][] a, final double[][] y) {
        int n = a.length;
        double[][] x = new double[n][];
        boolean dominant = isDiagonalDominant(a);
        if (dominant) {
            for (int i = 0; i < n; i++) {
                x[i] = solveDominant(a, getCol(y, i));
            }
        } else {
            int[] p = createPivot(a);
            for (int i = 0; i < n; i++) {
                x[i] = solvePivot(p, a, getCol(y, i));
            }
        }
        return transpose(x);
    }

    /**
     * Solve the equation A*x = y for x, using large
     * diagonal pivot elements if applicable.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    @Override
    public double[] solve(final double[][] a, final double[] y) {
        matrixCheck(a);
        if (isDiagonalDominant(a)) {
            return solveDominant(a, y);
        }
        return solvePivot(a, y);
    }

    /**
     * Solve the equation A*x = y for x using pivoting.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    protected double[] solvePivot(final double[][] a, final double[] y) {
        int[] p = createPivot(a);
        return solvePivot(p, a, y);
    }

    /**
     * Solve the equation A*x = y for x. Without using pivot
     * elements, e.g. A[i][i] must be none zero.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    protected final double[] solveDominant(final double[][] a, final double[] y) {
        int k = 0;
        double[] xo, x = rndVector(a.length);
        // main iteration loop until convergence
        do {
            xo = copy(x);
            updateX(a, y, x, xo);
        } while (!hasReachedAccuracy(x, xo, getEps()) && ++k < ITE_MAX);

        if (k >= ITE_MAX) {
            logger.warn(NO_CONVERGENCE);
            throw new ArithmeticException(NO_CONVERGENCE);
        }
        return x;
    }

    /**
     * Perform an internal update step of vector x, given a and y.
     *
     * @param a  the matrix
     * @param y  the left side
     * @param x  the next iteration value
     * @param xo the actual iteration value
     */
    protected abstract void updateX(final double[][] a, final double[] y,
                                    final double[] x, final double[] xo);

    /**
     * Solve the equation A*x = y for x using pivoting.
     *
     * @param p int[]  n-dimensional pivot array.
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    protected final double[] solvePivot(final int[] p, final double[][] a,
                                        final double[] y) {
        int ite = 0;
        double[] xo, x = rndVector(a.length);
        //        if (!isDiagonalDominant(p, a)) {
        //            throw new ArithmeticException(NOT_DIAGONAL_DOMINANT);
        //        }
        do {
            xo = copy(x);
            updateX(p, a, y, x, xo);
        } while (!hasReachedAccuracy(x, xo, getEps()) && ++ite < ITE_MAX);
        if (ite >= ITE_MAX) {
            logger.warn(NO_CONVERGENCE);
            throw new ArithmeticException(NO_CONVERGENCE);
        }
        return x;
    }

    /**
     * Perform an internal update step of vector x, given a and y and the pivot
     * ordering p.
     *
     * @param p  the pivot vector
     * @param a  the matrix
     * @param y  the left side
     * @param x  the next iteration value
     * @param xo the actual iteration value
     */
    protected abstract void updateX(int[] p, final double[][] a,
                                    final double[] y, final double[] x, final double[] xo);

    /* (non-Javadoc)
     * @see de.lab4inf.math.lapack.LASolver#det(double[][])
     */
    @Override
    public double det(final double[][] a) {
        throw new IllegalStateException(NOT_IMPLEMENTED);
    }

    /**
     * Calculate the absolute value for the determinate of matrix A.
     *
     * @param a float[][] the NxN matrix
     * @return abs(|A|)
     */
    public float det(final float[][] a) {
        return asFloat(det(asDouble(a)));
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.lapack.LASolver#det(de.lab4inf.math.Complex[][])
     */
    @Override
    public <T extends Numeric<T>> T det(final T[][] a) {
        throw new IllegalStateException(NOT_IMPLEMENTED);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.lapack.LASolver#solve(T[][], T[])
     */
    @Override
    public <T extends Numeric<T>> T[] solve(final T[][] a, final T[] y) {
        matrixCheck(a);
        if (isDiagonalDominant(a)) {
            return solveDominant(a, y);
        }
        return solvePivot(a, y);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.lapack.LASolver#solve(de.lab4inf.math.Complex[][], de.lab4inf.math.Complex[][])
     */
    public Complex[][] solve(final Complex[][] a, final Complex[][] b) {
        return solveViaDecomposition(a, b);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.lapack.LASolver#solve(T[][], T[][])
     */
    @Override
    public <T extends Numeric<T>> T[][] solve(final T[][] a, final T[][] b) {
        int n = a.length;
        T[][] x = LinearAlgebra.create(a, n, n);
        boolean dominant = isDiagonalDominant(a);
        if (dominant) {
            for (int i = 0; i < n; i++) {
                x[i] = solveDominant(a, getCol(b, i));
            }
        } else {
            int[] p = createPivot(a);
            for (int i = 0; i < n; i++) {
                x[i] = solvePivot(p, a, getCol(b, i));
            }
        }
        return transpose(x);
    }

    /**
     * Perform an internal update step of vector x, given a and y.
     *
     * @param a   the matrix
     * @param y   the left side
     * @param x   the next iteration value
     * @param xo  the actual iteration value
     * @param <T> the type of the elements
     */
    protected abstract <T extends Numeric<T>> void updateX(final T[][] a, final T[] y,
                                                           final T[] x, final T[] xo);

    /**
     * Perform an internal update step of vector x, given a and y.
     *
     * @param p   the pivot vector
     * @param a   the matrix
     * @param y   the left side
     * @param x   the next iteration value
     * @param xo  the actual iteration value
     * @param <T> the type of the elements
     */
    protected abstract <T extends Numeric<T>> void updateX(int[] p,
                                                           final T[][] a, final T[] y, final T[] x, final T[] xo);

    /**
     * Solve the equation A*x = y for x using pivoting.
     *
     * @param a   double[][] real NxN matrix.
     * @param y   double[] real N dimensional right side vector.
     * @param <T> the type of the elements
     * @return double[] real N dimensional solution x.
     */
    protected <T extends Numeric<T>> T[] solvePivot(final T[][] a, final T[] y) {
        int[] p = createPivot(a);
        return solvePivot(p, a, y);
    }

    /**
     * Solve the equation A*x = y for x. Without using pivot
     * elements, e.g. A[i][i] must be none zero.
     *
     * @param a   double[][] real NxN matrix.
     * @param y   double[] real N dimensional right side vector.
     * @param <T> the type of the elements
     * @return double[] real N dimensional solution x.
     */
    protected final <T extends Numeric<T>> T[] solveDominant(final T[][] a, final T[] y) {
        int k = 0;
        T[] xo, x = rndVector(a, a.length);
        // main iteration loop until convergence
        do {
            xo = copy(x);
            updateX(a, y, x, xo);
        } while (!hasReachedAccuracy(x, xo, getEps()) && ++k < ITE_MAX);

        if (k >= ITE_MAX) {
            logger.warn(NO_CONVERGENCE);
            throw new ArithmeticException(NO_CONVERGENCE);
        }
        return x;
    }

    /**
     * Solve the equation A*x = y for x using pivoting.
     *
     * @param p   int[]  n-dimensional pivot array.
     * @param a   double[][] real NxN matrix.
     * @param y   double[] real N dimensional right side vector.
     * @param <T> the type of the elements
     * @return double[] real N dimensional solution x.
     */
    protected final <T extends Numeric<T>> T[] solvePivot(final int[] p,
                                                          final T[][] a, final T[] y) {
        int ite = 0;
        T[] xo, x = rndVector(a, a.length);
        //        if (!isDiagonalDominant(p, a)) {
        //            throw new ArithmeticException(NOT_DIAGONAL_DOMINANT);
        //        }
        do {
            xo = copy(x);
            updateX(p, a, y, x, xo);
        } while (!hasReachedAccuracy(x, xo, getEps()) && ++ite < ITE_MAX);
        if (ite >= ITE_MAX) {
            logger.warn(NO_CONVERGENCE);
            throw new ArithmeticException(NO_CONVERGENCE);
        }
        return x;
    }
}
 