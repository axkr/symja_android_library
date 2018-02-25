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

import java.util.Arrays;

import de.lab4inf.math.Complex;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Numeric;
import de.lab4inf.math.Solver;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.lapack.LinearAlgebra.asDouble;
import static de.lab4inf.math.lapack.LinearAlgebra.asFloat;
import static de.lab4inf.math.lapack.LinearAlgebra.maxabs;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.squareCheck;
import static de.lab4inf.math.lapack.LinearAlgebra.sub;
import static de.lab4inf.math.util.Accuracy.DEPS;
import static java.lang.Math.abs;
import static java.lang.String.format;

/**
 * Generic matrix solver for linear equations of the form
 * <hr/>
 * <pre>
 *        A*x = y
 * </pre>
 * <hr/>
 * using a Gauss-, Householder- or Cholesky-algorithm depending on the
 * real oder complex input matrix A.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: LASolver.java,v 1.75 2014/12/08 15:21:05 nwulff Exp $
 * @since 31.03.2008
 */
abstract class LASolver extends L4MObject implements Solver {
    /**
     * reference to the NOT_POSITIVE_DEFINITE attribute.
     */
    public static final String NOT_POSITIVE_DEFINITE = "matrix not positive-definite";
    /**
     * reference to the NOT_SELF_ADJOINT attribute.
     */
    public static final String NOT_SELF_ADJOINT = "matrix not self-adjoint";
    /**
     * reference to the NOT_SYMMETRIC attribute.
     */
    public static final String NOT_SYMMETRIC = "matrix not symmetric";
    /**
     * reference to the NOT_HERMITIAN attribute.
     */
    public static final String NOT_HERMITIAN = "matrix not hermitian";
    /**
     * reference to the NOT_SQUARE attribute.
     */
    public static final String NOT_SQUARE = "matrix not square";
    /**
     * reference to the NOT_REGULAR attribute.
     */
    public static final String NOT_REGULAR = "Matrix not regular";
    /**
     * reference to the singular matrix element FMT.
     */
    public static final String SINGULAR = "numerical singular element[%d][%d]=%.1e";
    /**
     * reference to the singular complex matrix element FMT.
     */
    public static final String CSINGULAR = "numerical singular element[%d][%d]=%s";
    /**
     * the smallest number to hold.
     */
    public static final double DTINY = 5E6 * Accuracy.DEPS;
    public static final float FTINY = 5E1f * Accuracy.FEPS;
    protected static final String NOT_IMPLEMENTED = "not implemented";
    private static final String NOT_A_NUMBER = "not a number A[%d,%d]";
    private static final String DIAGONAL_NOT_A_NUMBER = "Diagonal not a number A[%d,%d]";
    private static SVDSolver svd = new SVDSolver();
    /**
     * flag to indicate pivot usage.
     */
    protected boolean usePivot;
    /**
     * maximal residual error without correction.
     */
    private double residualError = 5.E-14;
    private boolean shouldThrowSingular = true;

    /**
     * No public constructor allowed.
     */
    protected LASolver() {
    }

    /**
     * Calculate the residual of the solution for A*x=y. That is
     * <pre>
     *      r = y - A*x
     * </pre>
     *
     * @param a the matrix of the equation
     * @param y the right side of the equation
     * @param x the solution vector
     * @return the residual difference vector
     */
    public static float[] residual(final float[][] a, final float[] y, final float[] x) {
        final float[] r = sub(y, mult(a, x));
        return r;
    }

    /**
     * Calculate the residual of the solution for A*x=y. That is
     * <pre>
     *      r = y - A*x
     * </pre>
     *
     * @param a the matrix of the equation
     * @param y the right side of the equation
     * @param x the solution vector
     * @return the residual difference vector
     */
    public static double[] residual(final double[][] a, final double[] y, final double[] x) {
        final double[] r = sub(y, mult(a, x));
        return r;
    }

    /**
     * Calculate the residual of the solution for A*x=y. That is
     * <pre>
     *      r = y - A*x
     * </pre>
     *
     * @param a   the matrix of the equation
     * @param y   the right side of the equation
     * @param x   the solution vector
     * @param <T> the type of the numeary
     * @return the residual difference vector
     */
    public static <T extends Numeric<T>> T[] residual(final T[][] a, final T[] y, final T[] x) {
        final T[] r = sub(y, mult(a, x));
        return r;
    }
 
     /*
      * (non-Javadoc)
      * 
      * @see de.lab4inf.math.Solver#det(float[][])
      */
    // public float det(final float[][] a) {
    // // TODO quick hack to be optimized
    // return asFloat(det(asDouble(a)));
    // }

    /**
     * Calculate the residual of the solution for A*X=Y. That is
     * <pre>
     *      r = y - A*x
     * </pre>
     *
     * @param a the matrix of the equation
     * @param y the right side of the equation
     * @param x the solution matrix
     * @return the residual difference matrix
     */
    public static float[][] residual(final float[][] a, final float[][] y, final float[][] x) {
        final float[][] r = sub(y, mult(a, x));
        return r;
    }

    /**
     * Calculate the residual of the solution for A*X=Y. That is
     * <pre>
     *      r = y - A*x
     * </pre>
     *
     * @param a the matrix of the equation
     * @param y the right side of the equation
     * @param x the solution matrix
     * @return the residual difference matrix
     */
    public static double[][] residual(final double[][] a, final double[][] y, final double[][] x) {
        final double[][] r = sub(y, mult(a, x));
        return r;
    }

    /**
     * Calculate the residual of the solution for A*X=Y. That is
     * <pre>
     *      r = y - A*x
     * </pre>
     *
     * @param a   the matrix of the equation
     * @param y   the right side of the equation
     * @param x   the solution matrix
     * @param <T> the type of the numeric
     * @return the residual difference matrix
     */
    public static <T extends Numeric<T>> T[][] residual(final T[][] a, final T[][] y, final T[][] x) {
        final T[][] r = sub(y, mult(a, x));
        return r;
    }

    /**
     * Throw an exception if the diagonal matrix element at the given index
     * is singular.
     *
     * @param i the index
     * @param a the matrix to check
     */
    protected static final void checkRegular(final int i, final double[][] a) {
        checkRegular(i, a[i][i]);
    }

    /**
     * Throw an exception if the diagonal matrix element at the given index
     * is singular.
     *
     * @param i the index
     * @param a the matrix to check
     */
    protected static final void checkRegular(final int i, final float[][] a) {
        checkRegular(i, a[i][i]);
    }

    /**
     * Throw an exception if the diagonal matrix element at the given index
     * is singular.
     *
     * @param i   the index
     * @param a   the matrix to check
     * @param <T> number type
     */
    protected static final <T extends Numeric<T>> void checkRegular(final int i, final T[][] a) {
        checkRegular(i, a[i][i]);
    }

    /**
     * Throw an exception if the diagonal matrix element is singular.
     *
     * @param i   the index
     * @param a   the element to check
     * @param <T> number type
     */
    protected static final <T extends Numeric<T>> void checkRegular(final int i, final T a) {
        checkRegular(i, a.abs().doubleValue());
    }

    /**
     * Throw an exception if the given diagonal matrix element is numerical singular.
     *
     * @param i    the index of the element
     * @param diag the element value
     */
    protected static final void checkRegular(final int i, final float diag) {
        if (Float.isNaN(diag)) {
            final String msg = format(NOT_A_NUMBER, i, i);
            final SingularException error = new SingularException(msg);
            getLogger().info(msg);
            throw error;
        }
        if (abs(diag) < FTINY) {
            final String msg = format(SINGULAR, i, i, diag);
            final SingularException error = new SingularException(msg);
            getLogger().info(msg);
            throw error;
        }
    }

    /**
     * Throw an exception if the given diagonal matrix element is numerical singular.
     *
     * @param i    the index of the element
     * @param diag the element value
     */
    protected static final void checkRegular(final int i, final double diag) {
        if (Double.isNaN(diag)) {
            final String msg = format(NOT_A_NUMBER, i, i);
            final SingularException error = new SingularException(msg);
            getLogger().info(msg);
            throw error;
        }
        if (abs(diag) < DTINY) {
            final String msg = format(SINGULAR, i, i, diag);
            final SingularException error = new SingularException(msg);
            getLogger().info(msg);
            throw error;
        }
    }

    /**
     * Check the diagonal elements of size n for significance.
     * Let abs(d[k]) be the biggest element then it should hold:
     * <pre>
     *     n*eps &lt; abs(d[j]/d[k]) for all j
     * </pre>
     *
     * @param d the diagonal elements of the matrix to check
     */
    protected static final void diagonalCheck(final float[] d) {
        final int n = d.length;
        final float dmax = maxabs(d);
        final float tol = n * dmax * FTINY;
        for (int i = 0; i < n; i++) {
            if (Float.isNaN(d[i])) {
                final String msg = format(DIAGONAL_NOT_A_NUMBER, i, i);
                final SingularException error = new SingularException(msg);
                // getLogger().error(msg,error);
                throw error;
            }
            if (abs(d[i]) < tol) {
                final String msg = format(SINGULAR, i, i, d[i]);
                final SingularException error = new SingularException(msg);
                getLogger().warn(msg);
                throw error;
            }
        }
    }

    /**
     * Check the diagonal elements of size n for significance.
     * Let abs(d[k]) be the biggest element then it should hold:
     * <pre>
     *     n*eps &lt; abs(d[j]/d[k]) for all j
     * </pre>
     *
     * @param d   the diagonal elements of the matrix to check
     * @param <T> the type of the numeric
     */
    protected static final <T extends Numeric<T>> void diagonalCheck(final T[] d) {
        final int n = d.length;
        final T dmax = maxabs(d);
        final double tol = n * dmax.abs().doubleValue() * DEPS * 1.E4;
        for (int i = 0; i < n; i++) {
            if (d[i].abs().doubleValue() < tol) {
                final String msg = format(CSINGULAR, i, i, d[i]);
                final SingularException error = new SingularException(msg);
                getLogger().warn(msg);
                throw error;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#cond(float[][])
     */
    @Override
    public float cond(final float[][] a) {
        // TODO quick hack to be optimized
        return asFloat(cond(asDouble(a)));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#rank(float[][])
     */
    @Override
    public int rank(final float[][] a) {
        // TODO quick hack to be optimized
        return rank(asDouble(a));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#eigenvalues(float[][])
     */
    @Override
    public float[] eigenvalues(final float[][] a) {
        // TODO quick hack to be optimized
        return asFloat(eigenvalues(asDouble(a)));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#trace(float[][])
     */
    @Override
    public float trace(final float[][] a) {
        return LinearAlgebra.trace(a);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solve(float[][], float[])
     */
    @Override
    public float[] solve(final float[][] a, final float[] y) {
        // TODO quick hack to be optimized
        return asFloat(solve(asDouble(a), asDouble(y)));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solveSymmetric(float[][], float[])
     */
    @Override
    public float[] solveSymmetric(final float[][] a, final float[] y) {
        // TODO quick hack to be optimized
        return asFloat(solveSymmetric(asDouble(a), asDouble(y)));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solve(float[][], float[][])
     */
    @Override
    public float[][] solve(final float[][] a, final float[][] y) {
        // TODO quick hack to be optimized
        return asFloat(solve(asDouble(a), asDouble(y)));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#inverse(float[][])
     */
    @Override
    public float[][] inverse(final float[][] a) {
        // TODO quick hack to be optimized
        return asFloat(inverse(asDouble(a)));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#trace(T[][])
     */
    @Override
    public <T extends Numeric<T>> T trace(final T[][] a) {
        return LinearAlgebra.trace(a);
    }

    /**
     * Calculate the eigenvalues of matrix A.
     *
     * @param a the matrix
     * @return the eigenvalues of A
     */
    @Override
    public double[] eigenvalues(final double[][] a) {
        return svd.eigenvalues(a);
    }

    /**
     * Calculate the condition of matrix A.
     *
     * @param a the matrix
     * @return cond(A)
     */
    @Override
    public double cond(final double[][] a) {
        // return LinearAlgebra.cond(a);
        return svd.cond(a);
    }

    /**
     * Calculate the rank of matrix A.
     *
     * @param a the matrix
     * @return rank(A)
     */
    @Override
    public int rank(final double[][] a) {
        return svd.rank(a);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#trace(double[][])
     */
    @Override
    public double trace(final double[][] a) {
        return LinearAlgebra.trace(a);
    }

    /**
     * Get the usePivot value.
     *
     * @return the usePivot
     */
    public boolean isUsingPivot() {
        return usePivot;
    }

    /**
     * Set the flag to use pivot search or not.
     *
     * @param usingPivot boolean flag to use
     */
    public void setUsingPivot(final boolean usingPivot) {
        usePivot = usingPivot;
    }

    /**
     * Calculate the determinate of matrix A.
     *
     * @param a double[][] the NxN matrix
     * @return double the determinant of A
     */
    @Override
    public abstract double det(final double[][] a);

    /**
     * Calculate the determinate of matrix A.
     *
     * @param a   type T[][]  NxN matrix
     * @param <T> the type of the numeary
     * @return the determinant of A
     */
    // public abstract Complex det(final Complex[][] a);
    @Override
    public abstract <T extends Numeric<T>> T det(final T[][] a);

    /**
     * Solve the equation A*x = y for x. This method evaluates the matrix
     * A and chooses between different Solvers, e.g. Gauss-, Cholesky-,
     * HouseholderSolver etc.
     *
     * @param a double[][] real NxN matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    @Override
    public abstract double[] solve(final double[][] a, final double[] y);

    /**
     * Solve the equation A*x = y for x for complex vectors and matrix.
     * This method evaluates the matrix A and chooses between different Solvers,
     * e.g. Gauss-, Cholesky-, HouseholderSolver etc.
     *
     * @param a   NxN matrix.
     * @param y   N dimensional right side vector.
     * @param <T> the type of the numeary
     * @return N dimensional solution x.
     */
    @Override
    public abstract <T extends Numeric<T>> T[] solve(final T[][] a, final T[] y);

    /**
     * Solve the equation A*x = y for x for a <b>symmetric matrix A</b>.
     * This method evaluates the matrix and chooses between the
     * Cholesky- or HouseholderSolver.
     *
     * @param a double[][] real NxN symmetrix matrix.
     * @param y double[] real N dimensional right side vector.
     * @return double[] real N dimensional solution x.
     */
    @Override
    public double[] solveSymmetric(final double[][] a, final double[] y) {
        return solve(a, y);
    }

    /**
     * Solve the equation A*x = y for x for a <b>hermitian matrix A</b>.
     * This method evaluates the matrix and chooses between the
     * Cholesky- or HouseholderSolver.
     *
     * @param a complex NxN hermitian matrix.
     * @param y complex N dimensional right side vector.
     * @return complex N dimensional solution x.
     */
    @Override
    public Complex[] solveHermitian(final Complex[][] a, final Complex[] y) {
        return solve(a, y);
    }

    /**
     * Solve the multi equation A*X = Y for x for a <b>hermitian matrix A</b>.
     * This method evaluates the matrix and chooses between the
     * Cholesky- or HouseholderSolver.
     *
     * @param a complex NxN hermitian matrix.
     * @param y complex NxM dimensional right side matrix.
     * @return complex NxM dimensional solution x.
     */
    public Complex[][] solveHermitian(final Complex[][] a, final Complex[][] y) {
        return solve(a, y);
    }

    /**
     * Solve the equation A*X = B for X. This method evaluates the matrix
     * A and chooses between different Solvers, e.g. Gauss-, Cholesky-,
     * HouseholderSolver etc.
     *
     * @param a double[][] real NxN matrix
     * @param b double[][] real NxN right side vectors as matrix
     * @return double[][] X solution matrix
     */
    @Override
    public abstract double[][] solve(final double[][] a, final double[][] b);

    /**
     * Solve the equation A*X = B for X. This method evaluates the matrix
     * A and chooses between different Solvers, e.g. Gauss-, Cholesky-,
     * HouseholderSolver etc.
     *
     * @param a   NxN matrix
     * @param b   NxM right side vectors as matrix
     * @param <T> the type of the numeary
     * @return NxM solution matrix X
     */
    @Override
    public abstract <T extends Numeric<T>> T[][] solve(final T[][] a, final T[][] b);

    /**
     * Solve the equation A*X = B for X for a <b>symmetric matrix A</b>.
     * This method evaluates the matrix A and chooses between different Solvers,
     * e.g. Cholesky- or HouseholderSolver.
     *
     * @param a double[][] real symmetric NxN matrix
     * @param b double[][] real NxN right side vectors as matrix
     * @return double[][] X solution matrix
     */
    public double[][] solveSymmetric(final double[][] a, final double[][] b) {
        return solve(a, b);
    }

    /**
     * Calculate the inverse matrix of A.
     *
     * @param a matrix to invert
     * @return 1/A the inverse matrix
     */
    @Override
    public double[][] inverse(final double[][] a) {
        squareCheck(a);
        final double[][] e = LinearAlgebra.identity(a.length);
        return solve(a, e);
    }

    /**
     * Calculate the inverse matrix of A.
     *
     * @param a   matrix to invert
     * @param <T> the type of the numeary
     * @return 1/A the inverse matrix
     */
    @Override
    public <T extends Numeric<T>> T[][] inverse(final T[][] a) {
        squareCheck(a);
        final T[][] e = LinearAlgebra.identity(a[0][0], a.length);
        return solve(a, e);
    }

    /**
     * Get the maximal tolerated residual error.
     *
     * @return the residualError
     */
    public double getResidualError() {
        return residualError;
    }

    /**
     * Set the maximal tolerated residual error.
     *
     * @param residualError the residualError to set
     */
    public void setResidualError(final double residualError) {
        this.residualError = residualError;
    }

    /**
     * Check the diagonal elements of size n for significance.
     * Let abs(d[k]) be the biggest element then it should hold:
     * <pre>
     *     n*eps &lt; abs(d[j]/d[k]) for all j
     * </pre>
     *
     * @param d the diagonal elements of the matrix to check
     */
    protected final void diagonalCheck(final double[] d) {
        final int n = d.length;
        String msg;
        final double dmax = maxabs(d);
        final double tol = n * dmax * DTINY;
        for (int i = 0; i < n; i++) {
            // msg = format("diag[%d]=%.2e tol=%.2e", i, d[i],tol);
            // getLogger().warn(msg);
            if (Double.isNaN(d[i])) {
                msg = format(DIAGONAL_NOT_A_NUMBER, i, i);
                final SingularException error = new SingularException(msg);
                // getLogger().error(msg,error);
                throw error;
            }
            if (abs(d[i]) < tol) {
                // msg = format(SINGULAR, i, i, d[i]);
                msg = format("singular d[%d] tol=%.2e %s ", i, tol, Arrays.toString(d));
                if (shouldThrowSingular) {
                    final SingularException error = new SingularException(msg);
                    throw error;
                }
                getLogger().error(msg);
            }
        }
    }

    /**
     * Solve the complex system C*z = b by decomposition it into
     * two real systems:
     * <pre>
     *
     *       C    *   z     =    b
     *
     *    (A + jB)*(x + jy) = (c + jd)
     *
     *     I: A*x - B*y = c
     *    II: B*x + A*y = d
     *
     * </pre>
     *
     * @param c the complex input matrix
     * @param b the complex right side vector
     * @return z the complex solution
     */
    protected Complex[] solveViaDecomposition(final Complex[][] c, final Complex[] b) {
        double[] x, y;
        final double[][] a = LinearAlgebra.createRealMatrix(c);
        y = LinearAlgebra.createRealVector(b);
        x = solve(a, y);
        return LinearAlgebra.createComplexVector(x);
    }

    protected Complex[][] solveViaDecomposition(final Complex[][] c, final Complex[][] b) {
        double[][] x, y;
        final double[][] a = LinearAlgebra.createRealMatrix(c);
        y = LinearAlgebra.createRealMatrix(b);
        x = solve(a, y);
        return LinearAlgebra.createComplexMatrix(x);
    }

    /**
     * Get the shouldThrowSingular value.
     *
     * @return the shouldThrowSingular
     */
    public boolean isShouldThrowSingular() {
        return shouldThrowSingular;
    }

    /**
     * If set to true a numerical singular diagonal element forces an exception.
     *
     * @param throwSingular flag indicating exception throwing
     */
    public void setShouldThrowSingular(boolean throwSingular) {
        this.shouldThrowSingular = throwSingular;
    }
}
 