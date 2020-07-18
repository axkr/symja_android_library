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

package de.lab4inf.math.extrema;

import de.lab4inf.math.Function;
import de.lab4inf.math.lapack.LinearAlgebra;

import static de.lab4inf.math.lapack.LinearAlgebra.add;
import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.getCol;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.sub;
import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Minimisation using Powell/Brents's method with bisection
 * based on the golden ratio in N dimensions without first
 * or second derivatives.
 *
 * @author nwulff
 * @version $Id: PowellOptimizer.java,v 1.13 2014/11/16 21:47:23 nwulff Exp $
 * @since 22.12.2009
 */
public class PowellOptimizer extends GenericOptimizer {
    private StraightFunction fct1d;

    /**
     * Sole constructor.
     */
    public PowellOptimizer() {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMaximisation(de.lab4inf.math.Function, double[])
     */
    @Override
    protected boolean runMaximisation(final Function fct, final double... guess) {
        return runMinimisation(new MinimizerFct(fct), guess);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMinimisation(de.lab4inf.math.Function, double[])
     */
    @Override
    protected boolean runMinimisation(final Function fct, final double... guess) {
        setTarget(fct);
        return powell(guess, fct);
    }

    /**
     * Maximize the given function within the bracketing cube [a,b].
     *
     * @param fct   to maximize
     * @param a     left borders
     * @param b     right borders
     * @param guess initial and final optimum
     * @return convergence flag
     */
    public boolean runMaximisation(final Function fct, final double[] a, final double[] b, final double[] guess) {
        setTarget(fct);
        return powell(a, b, guess, new MinimizerFct(fct));
    }

    /**
     * Minimize the given function within the bracketing cube [a,b].
     *
     * @param fct   to maximize
     * @param a     left borders
     * @param b     right borders
     * @param guess initial and final optimum
     * @return convergence flag
     */
    public boolean runMinimization(final Function fct, final double[] a, final double[] b, final double[] guess) {
        setTarget(fct);
        return powell(a, b, guess, fct);
    }

    /**
     * Given two n-dimensional points x0 and  x1, moves and resets x0 to where
     * the function takes on a minimum along the direction x1 from x0 and
     * replaces x1 by the actual vector displacement that x0 was moved.
     */
    private double linmin(final double[] x0, final double[] x1) {
        final int n = x0.length;
        double x, y;
        final double eps = getPrecision();
        fct1d.setX0(x0);
        fct1d.setX1(x1);
        x = fct1d.brent(0, 1, eps);
        y = fct1d.f(x);
        System.arraycopy(mult(x1, x), 0, x1, 0, n);
        System.arraycopy(add(x0, x1), 0, x0, 0, n);
        return y;
    }

    /**
     * Minimization of function using the Powell algorithm.
     *
     * @param xn   array with initial and final guess
     * @param func function to minimize
     * @return boolean success flag
     */
    public boolean powell(final double[] xn, final Function func) {
        return powell(null, null, xn, func);
    }

    /**
     * Check if x lies in the cube marked by [a,b].
     *
     * @param a left borders
     * @param b right borders
     * @param x values to check
     * @return true if a[j] &lt; x[j] &lt; b[j]; j=0...n-1
     */
    public boolean checkInterval(final double[] a, final double[] b, final double[] x) {
        boolean ok = true;
        if (null == a || null == b) {
            return true;
        }
        final int n = x.length;
        for (int i = 0; i < n; i++) {
            if (x[i] < a[i] || b[i] < x[i]) {
                ok = false;
                if (logger.isWarnEnabled()) {
                    logger.warn(format("leaving interval A[%s] X[%s] B[%s]", display(a), display(x), display(b)));
                }
                break;
            }
        }
        return ok;
    }

    /**
     * Minimization of function using the Powell algorithm within the
     * bracketing interval/cube borders [a,b].
     *
     * @param a    array with left bracketing borders
     * @param b    array with right bracketing borders
     * @param xn   array with initial and final guess
     * @param func function to minimize
     * @return boolean success flag
     */
    private boolean powell(final double[] a, final double[] b, final double[] xn, final Function func) {
        fct1d = new StraightFunction(func);
        int id, iter = 0;
        final int n = xn.length;
        double df, fo, fxx, t, fn = func.f(xn);
        double[] xo, dxx, dx;
        final double[][] xi = LinearAlgebra.identity(n);
        informIterationIsFinished(iter, xn);
        do {
            xo = copy(xn);
            fo = fn;
            id = 0;
            df = 0;
            // loop over all directions.
            for (int i = 0; i < n; i++) {
                dx = getCol(xi, i);
                fxx = fn;
                fn = linmin(xn, dx);
                if (fxx - fn > df) {
                    df = fxx - fn; // remember the largest decrease
                    id = i;
                }
            }
            if (!checkInterval(a, b, xn)) {
                // for(int i=0; i<n; xn[i] = xo[i],i++);
                informOptimizationIsFinished(++iter, xn);
                return false;
            }
            // Construct the extrapolated point and the
            // average direction moved.
            dx = sub(xn, xo);
            dxx = sub(mult(xn, 2), xo);
            fxx = func.f(dxx); // Function value at extrapolated point.
            if (fxx < fo) {
                t = 2 * (fo - 2 * fn + fxx) * sqrt(fo - fn - df) - df * sqrt(fo - fxx);
                if (t < 0) {
                    fn = linmin(xn, dx); // Move to the new minimum
                    for (int j = 0; j < n; j++) { // and save the new direction.
                        xi[j][id] = xi[j][n - 1];
                        xi[j][n - 1] = dx[j];
                    }
                }
            }
            informIterationIsFinished(iter, xn);
            // if (logger.isInfoEnabled()) {
            // logger.info(format("%d: %s", iter, display(xn)));
            // }
        } while (!hasReachedAccuracy(xn, xo, getPrecision()) && ++iter < getMaxIterations());
        informOptimizationIsFinished(iter, xn);
        if (iter == getMaxIterations()) {
            logger.warn(format("max iterations exceeded %s ", display(xn)));
            return false;
        }
        return checkInterval(a, b, xn);
    }

}
 