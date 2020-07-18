/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2012,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math;

/**
 * Solver service for linear equations A*x=y, with A a matrix and x,y vectors.
 * <br/>
 * The solver will choose the appropriate strategy to solve
 * the above equation depending on the given matrix A, i.e. use
 * the Cholesky, Householder, Gauss, SVD, etc. algorithm.
 * <br/>
 * You can resolve a solver implementation via the L4MLoader:
 * <pre>
 *
 *   Solver s = L4MLoader.load(Solver.class);
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: Solver.java,v 1.11 2014/12/08 15:21:05 nwulff Exp $
 * @since 08.01.2012
 */
@Service
public interface Solver {
    /**
     * Get the status of the numerical singular check.
     * If set to true an exception will be thrown
     *
     * @return the shouldThrowSingular
     */
    public boolean isShouldThrowSingular();

    /**
     * If set to true a numerical singular diagonal element forces an exception.
     *
     * @param throwSingular flag indicating exception throwing
     */
    public void setShouldThrowSingular(boolean throwSingular);

    /*************  single precision calculus   ***********************************/
    /**
     * Calculate the condition number of matrix A.
     * cond(A) = EV_1/EV_n where EW are the sorted eigenvalues of A.
     *
     * @param a the matrix A
     * @return cond(A)
     */
    float cond(float[][] a);

    /**
     * Calculate the rank of matrix A.
     *
     * @param a the matrix A.
     * @return rank(A)
     */
    int rank(float[][] a);

    /**
     * Calculate the eigenvalues of matrix A.
     *
     * @param a the matrix A
     * @return the eigenvalues
     */
    float[] eigenvalues(float[][] a);

    /**
     * Calculate the determinate of matrix A.
     *
     * @param a the matrix A
     * @return |A|
     */
    float det(float[][] a);

    /**
     * Calculate the trace of a matrix NxN square A.
     *
     * @param a the matrix A which has to be square
     * @return tr(A)
     */
    float trace(float[][] a);

    /**
     * Solve the equation A*x = y for x given y.
     *
     * @param a the matrix A
     * @param y the rhs of the equation
     * @return the solution x
     */
    float[] solve(float[][] a, float[] y);

    /**
     * Solve the equation A*x = y for x given y and a <b>symmetric</b> matrix A.
     *
     * @param a the matrix A has to be symmetric
     * @param y the rhs of the equation
     * @return the solution x
     */
    float[] solveSymmetric(float[][] a, float[] y);

    /**
     * Solve the equation A*x = y for x given y vectors.
     *
     * @param a the matrix A
     * @param y the rhs of the m equations
     * @return the solution x as matrix
     */
    float[][] solve(float[][] a, float[][] y);

    /**
     * Calculate the inverse of a NxN matrix A.
     *
     * @param a the matrix A
     * @return the inverse for A
     */
    float[][] inverse(float[][] a);

    /*************  double precision calculus   ***********************************/
    /**
     * Calculate the condition number of matrix A.
     * cond(A) = EV_1/EV_n where EW are the sorted eigenvalues of A.
     *
     * @param a the matrix A
     * @return cond(A)
     */
    double cond(double[][] a);

    /**
     * Calculate the rank of matrix A.
     *
     * @param a the matrix A.
     * @return rank(A)
     */
    int rank(double[][] a);

    /**
     * Calculate the eigenvalues of matrix A.
     *
     * @param a the matrix A
     * @return the eigenvalues
     */
    double[] eigenvalues(double[][] a);

    /**
     * Calculate the determinate of matrix A.
     *
     * @param a the matrix A
     * @return |A|
     */
    double det(double[][] a);

    /**
     * Calculate the trace of a matrix NxN square A.
     *
     * @param a the matrix A which has to be square
     * @return tr(A)
     */
    double trace(double[][] a);

    /**
     * Solve the equation A*x = y for x given y.
     *
     * @param a the matrix A
     * @param y the rhs of the equation
     * @return the solution x
     */
    double[] solve(double[][] a, double[] y);

    /**
     * Solve the equation A*x = y for x given y and a <b>symmetric</b> matrix A.
     *
     * @param a the matrix A has to be symmetric
     * @param y the rhs of the equation
     * @return the solution x
     */
    double[] solveSymmetric(double[][] a, double[] y);

    /**
     * Solve the equation A*x = y for x given y vectors.
     *
     * @param a the matrix A
     * @param y the rhs of the m equations
     * @return the solution x as matrix
     */
    double[][] solve(double[][] a, double[][] y);

    /**
     * Calculate the inverse of a NxN matrix A.
     *
     * @param a the matrix A
     * @return the inverse for A
     */
    double[][] inverse(double[][] a);

    /****************  generic type calculus   ***********************************/

    /**
     * Calculate the determinate of a NxN square matrix A.
     *
     * @param a   the matrix A which has to be square
     * @param <T> the type of the elements
     * @return |A|
     */
    <T extends Numeric<T>> T det(T[][] a);

    /**
     * Calculate the trace of matrix A.
     *
     * @param a   the matrix A
     * @param <T> the type of the elements
     * @return tr(A)
     */
    <T extends Numeric<T>> T trace(T[][] a);

    /**
     * Solve the equation A*x = y for x given y.
     *
     * @param a   the matrix A
     * @param y   the rhs of the equation
     * @param <T> the type of the elements
     * @return the solution x
     */
    <T extends Numeric<T>> T[] solve(T[][] a, T[] y);

    /**
     * Solve the equation A*x = y for x given y and a <b>hermitian</b> matrix A.
     *
     * @param a the matrix A has to be hermitian
     * @param y the rhs of the equation
     * @return the solution x
     */
    Complex[] solveHermitian(Complex[][] a, Complex[] y);

    /**
     * Solve the equation A*x = y for x given y vectors.
     *
     * @param a   the matrix A
     * @param y   the rhs of the m equations
     * @param <T> the type of the elements
     * @return the solution x as matrix
     */
    <T extends Numeric<T>> T[][] solve(T[][] a, T[][] y);

    /**
     * Calculate the inverse of a NxN matrix A.
     *
     * @param a   the matrix A
     * @param <T> the type of the elements
     * @return the inverse for A
     */
    <T extends Numeric<T>> T[][] inverse(T[][] a);
}
 