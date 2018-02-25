/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.extrema;

import de.lab4inf.math.Function;
import de.lab4inf.math.differentiation.Gradient;
import de.lab4inf.math.differentiation.Hessian;
import de.lab4inf.math.util.Aitken;

import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.diff;
import static de.lab4inf.math.lapack.LinearAlgebra.identity;
import static de.lab4inf.math.lapack.LinearAlgebra.norm;
import static de.lab4inf.math.lapack.LinearAlgebra.sub;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static de.lab4inf.math.util.Accuracy.round;
import static java.lang.String.format;

/**
 * Optimizer using a quasi newton gradient method.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: GradientOptimizer.java,v 1.16 2012/01/12 16:38:40 nwulff Exp $
 * @since 17.06.2007
 */
public class GradientOptimizer extends GenericOptimizer {
    static final int DIGIGTS = 6;

    /*
     *     (non-Javadoc)
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMinimisation(de.lab4inf.math.Function, double[])
     */
    @Override
    protected boolean runMinimisation(final Function fct, final double... guess) {
        return runMaximisation(new MinimizerFct(fct), guess);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMaximisation(de.lab4inf.math.Function, double[])
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean runMaximisation(final Function fct, final double... guess) {
        int iteration = 0;
        int n = guess.length;
        double delta;
        double[] dx;
        double[] x = copy(guess), yn = x, yo;
        double[] grad;
        double[][] hess, hessOld;
        Aitken[] a = new Aitken[n];
        for (int i = 0; i < n; a[i] = new Aitken(), i++) ;
        setTarget(fct);
        Gradient g = getGradient(fct);
        Hessian h = getHessian(fct);
        if (DEBUG) {
            logger.info(format("%3d %s r:%5.4f", iteration, display(x), norm(x)));
        }
        informIterationIsFinished(iteration, x);
        hessOld = identity(n);
        do {
            yo = yn;
            // solve the equation
            //
            // Hess(F) dx = -grad(F)
            //
            grad = g.gradient(x);
            hess = h.hessian(x);
            try {
                dx = laSolver.solveSymmetric(hess, grad);
                hessOld = hess;
            } catch (IllegalArgumentException e) {
                // determinant zero choose the old hessisan matrix ...
                logger.warn("determinant is zero, using old hesse approximation");
                dx = laSolver.solveSymmetric(hessOld, grad);
            }
            x = sub(x, dx);
            yn = aitkenAccelerate(x, a);
            if (DEBUG) {
                delta = diff(yn, yo);
                logger.info(format(" %3d %s r:%5.4f diff:%6.5f", iteration,
                        display(yn), norm(yn), delta));
            }
            informIterationIsFinished(iteration, yn);
        } while (!hasConverged(yn, yo, getPrecision(), ++iteration,
                getMaxIterations()));

        for (int i = 0; i < n; i++) {
            guess[i] = round(yn[i], getPrecision());
        }

        informOptimizationIsFinished(iteration, guess);
        return iteration < getMaxIterations();
    }

}
 