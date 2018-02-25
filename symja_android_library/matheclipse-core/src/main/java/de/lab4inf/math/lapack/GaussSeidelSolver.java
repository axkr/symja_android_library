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

import de.lab4inf.math.Numeric;

/**
 * Iterative Gauss-Seidel Solver for A*x=y.
 * Starting from a random vector x_0 a sequence x_n converging
 * against the solution is generated, if the matrix norm ||A||
 * is less than one, e.g. A is strict diagonal dominant.
 *
 * @author nwulff
 * @version $Id: GaussSeidelSolver.java,v 1.18 2014/11/24 18:14:33 nwulff Exp $
 * @since 19.01.2010
 */
@DiagonalDominant
public final class GaussSeidelSolver extends AbstractIterativeSolver {
    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.AbstractIterativeSolver#updateX(double[][], double[], double[], double[])
     */
    @Override
    protected void updateX(final double[][] a, final double[] y, final double[] x, final double[] xo) {
        int j, n = a.length;
        double s;
        double[] ak;
        for (int k = 0; k < n; x[k] = s / ak[k], k++)
            for (ak = a[k], s = y[k], j = 0; j < n; j++)
                if (j != k)
                    s -= ak[j] * x[j];
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.AbstractIterativeSolver#updateX(int[], double[][], double[], double[], double[])
     */
    @Override
    protected void updateX(final int[] p, final double[][] a, final double[] y, final double[] x, final double[] xo) {
        int j, k, n = a.length;
        double s;
        double[] ak;
        for (k = 0; k < n; x[k] = s / ak[k], k++) {
            for (ak = a[p[k]], s = y[p[k]], j = 0; j < k; s -= ak[j] * x[j], j++)
                ;
            for (j = k + 1; j < n; s -= ak[j] * x[j], j++)
                ;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.AbstractIterativeSolver#updateX(T[][], T[], T[], T[])
     */
    @Override
    protected <T extends Numeric<T>> void updateX(final T[][] a, final T[] y, final T[] x, final T[] xo) {
        int j, n = a.length;
        T s;
        T[] ak;
        for (int k = 0; k < n; x[k] = s.div(ak[k]), k++)
            for (ak = a[k], s = y[k], j = 0; j < n; j++)
                if (j != k)
                    s = s.minus(ak[j].multiply(x[j]));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lapack.AbstractIterativeSolver#updateX(int[], T[][], T[], T[], T[])
     */
    @Override
    protected <T extends Numeric<T>> void updateX(final int[] p, final T[][] a, final T[] y, final T[] x, final T[] xo) {
        int j, k, n = a.length;
        T s;
        T[] ak;
        for (k = 0; k < n; x[k] = s.div(ak[k]), k++) {
            for (ak = a[p[k]], s = y[p[k]], j = 0; j < k; s = s.minus(ak[j].multiply(x[j])), j++)
                ;
            for (j = k + 1; j < n; s = s.minus(ak[j].multiply(x[j])), j++)
                ;
        }
    }
}
 