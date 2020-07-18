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
package de.lab4inf.math.lapack;

import de.lab4inf.math.Complex;
import de.lab4inf.math.Numeric;

import static de.lab4inf.math.lapack.LinearAlgebra.isHermitian;
import static de.lab4inf.math.lapack.LinearAlgebra.isPositiveDefinite;
import static de.lab4inf.math.lapack.LinearAlgebra.isSymmetric;

/**
 * Generic linear algebra solver service, as facade to the different
 * Lab4Math LA solvers. The solver interface can be resolved by the
 * ServiceLoader, i.e. the L4MLoader:
 * <p>
 * <pre>
 * Solver solver = L4MLoader.load(Solver.class);
 *
 * double[][] A = new double[][]{{1,2}{2,3}};
 * double[] x,y = new double[] {1,2};
 * x = solver.solve(a,y);
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: GenericSolver.java,v 1.22 2015/04/21 16:03:27 nwulff Exp $
 * @see de.lab4inf.math.Solver
 * @see de.lab4inf.math.L4MLoader
 * @since 08.01.2012
 */
public class GenericSolver extends LASolver {
    private static GaussSolver gauss = new GaussSolver();
    // private static SVDSolver svd = new SVDSolver();
    private static CholeskySolver cholesky = new CholeskySolver();
    private static HouseholderSolver househ = new HouseholderSolver();

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#det(float[][])
     */
    @Override
    public float det(final float[][] a) {
        float det;
        // try {
        if (isSymmetric(a)) {
            if (isPositiveDefinite(a)) {
                det = cholesky.det(a);
            } else {
                det = househ.det(a);
            }
        } else {
            det = gauss.det(a);
        }
        // } catch (SingularException e) {
        // logger.warn(NOT_REGULAR);
        // det = svd.det(a);
        // }
        return det;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#det(double[][])
     */
    @Override
    public double det(final double[][] a) {
        double det;
        // try {
        if (isSymmetric(a)) {
            if (isPositiveDefinite(a)) {
                cholesky.setShouldThrowSingular(isShouldThrowSingular());
                det = cholesky.det(a);
            } else {
                househ.setShouldThrowSingular(isShouldThrowSingular());
                det = househ.det(a);
            }
        } else {
            gauss.setShouldThrowSingular(isShouldThrowSingular());
            det = gauss.det(a);
        }
        // } catch (SingularException e) {
        // logger.warn(NOT_REGULAR);
        // det = svd.det(a);
        // }
        return det;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solve(double[][], double[])
     */
    @Override
    public double[] solve(final double[][] a, final double[] y) {
        double[] x;
        // try {
        if (isSymmetric(a)) {
            x = solveSymmetric(a, y);
        } else {
            x = gauss.solve(a, y);
        }
        // } catch (SingularException e) {
        // getLogger().warn(NOT_REGULAR);
        // x = svd.solve(a, y);
        // }
        return x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solve(double[][], double[][])
     */
    @Override
    public double[][] solve(final double[][] a, final double[][] y) {
        double[][] x;
        // try {
        if (isSymmetric(a)) {
            x = solveSymmetric(a, y);
        } else {
            x = gauss.solve(a, y);
        }
        // } catch (SingularException e) {
        // getLogger().warn(NOT_REGULAR);
        // x = svd.solve(a, y);
        // }
        return x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#inverse(double[][])
     */
    @Override
    public double[][] inverse(final double[][] a) {
        double[][] x;
        // try {
        if (isSymmetric(a)) {
            if (isPositiveDefinite(a)) {
                x = cholesky.inverse(a);
            } else {
                x = househ.inverse(a);
            }
        } else {
            x = gauss.inverse(a);
        }
        // } catch (SingularException e) {
        // getLogger().warn(NOT_REGULAR);
        // x = svd.inverse(a);
        // }
        return x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solve(de.lab4inf.math.Complex[][], de.lab4inf.math.Complex[])
     */
    public Complex[] solve(final Complex[][] a, final Complex[] y) {
        Complex[] x;
        if (isHermitian(a)) {
            x = solveHermitian(a, y);
        } else {
            x = gauss.solve(a, y);
        }
        return x;
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
        return gauss.solve(a, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#solve(T[][], T[][])
     */
    @Override
    public <T extends Numeric<T>> T[][] solve(final T[][] a, final T[][] y) {
        if (isAComplex(a)) {
            return asT(y, solve(asComplex(a), asComplex(y)));
        }
        return gauss.solve(a, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#det(T[][])
     */
    @Override
    public <T extends Numeric<T>> T det(final T[][] a) {
        return gauss.det(a);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Solver#solve(de.lab4inf.math.Complex[][], de.lab4inf.math.Complex[][])
     */
    public Complex[][] solve(final Complex[][] a, final Complex[][] y) {
        Complex[][] x;
        if (isHermitian(a)) {
            x = solveHermitian(a, y);
        } else {
            x = gauss.solve(a, y);
        }
        return x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.LASolver#inverse(T[][])
     */
    @Override
    public <T extends Numeric<T>> T[][] inverse(final T[][] a) {
        return gauss.inverse(a);
    }

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
        double[] x;
        // try {
        if (isPositiveDefinite(a)) {
            cholesky.setShouldThrowSingular(isShouldThrowSingular());
            x = cholesky.cholesky(a, y);
        } else {
            househ.setShouldThrowSingular(isShouldThrowSingular());
            x = househ.solveSymmetric(a, y);
        }
        // } catch (SingularException e) {
        // getLogger().warn(NOT_REGULAR);
        // x = svd.solve(a, y);
        // }
        return x;
    }

    /**
     * Solve the equation A*X = B for X for a <b>symmetric matrix A</b>.
     * This method evaluates the matrix A and chooses between different Solvers,
     * e.g. Cholesky- or HouseholderSolver.
     *
     * @param a double[][] real symmetric NxN matrix
     * @param b double[][] real NxN right side vectors as matrix
     * @return double[][] X solution matrix
     */
    @Override
    public double[][] solveSymmetric(final double[][] a, final double[][] b) {
        double[][] x;
        // try {
        if (isPositiveDefinite(a)) {
            cholesky.setShouldThrowSingular(isShouldThrowSingular());
            x = cholesky.cholesky(a, b);
        } else {
            househ.setShouldThrowSingular(isShouldThrowSingular());
            x = househ.solveSymmetric(a, b);
        }
        // } catch (SingularException e) {
        // getLogger().warn(NOT_REGULAR);
        // x = svd.solve(a, b);
        // }
        return x;
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
        Complex[] x;
        // TODO implement all complex solvers
        // try {
        // if (isPositiveDefinite(a)) {
        // x = CholeskySolver.cholesky(a, y);
        // } else {
        // x = HouseholderSolver.solvesym(a, y);
        // }
        // } catch (NoneRegularException e) {
        // getLogger().warn(NOT_REGULAR);
        // x = SVDSolver.solve(a, y);
        // }
        x = cholesky.cholesky(a, y);
        return x;
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
    @Override
    public Complex[][] solveHermitian(final Complex[][] a, final Complex[][] y) {
        Complex[][] x;
        // TODO implement all complex solvers
        // try {
        // if (isPositiveDefinite(a)) {
        // x = CholeskySolver.cholesky(a, y);
        // } else {
        // x = HouseholderSolver.solvesym(a, y);
        // }
        // } catch (NoneRegularException e) {
        // getLogger().warn(NOT_REGULAR);
        // x = SVDSolver.solve(a, y);
        // }
        x = cholesky.cholesky(a, y);
        return x;
    }
}
 