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

import static de.lab4inf.math.lapack.LinearAlgebra.add;
import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.identity;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.sub;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static de.lab4inf.math.util.Accuracy.round;
import static java.lang.String.format;

/**
 * Optimizer using the Marquardt minimisation.
 *
 * @author nwulff
 * @version $Id: MarquardtOptimizer.java,v 1.18 2012/01/12 16:38:40 nwulff Exp $
 * @since 20.07.2009
 */
public class MarquardtOptimizer extends GenericOptimizer {
    /**
     * maximal number internal marquardt updates.
     */
    private static final int MAX_MARQUARDTS = 50;

    /*
     *     (non-Javadoc)
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMaximisation(de.lab4inf.math.Function, double[])
     */
    @Override
    protected boolean runMaximisation(final Function fct, final double... guess) {
        return runMinimisation(new MinimizerFct(fct), guess);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.extrema.GenericOptimizer#runMinimisation(de.lab4inf.math.Function, double[])
     */
    @Override
    public boolean runMinimisation(final Function fct, final double... guess) {
        int iteration = 0, m;
        int n = guess.length;
        double f, fo, fvu, vu = 5, lamda = 0.01;
        double[] dx, xo, x = copy(guess), grad, xvu;
        double[][] hesslp, hess, id;
        Gradient g = getGradient(fct);
        Hessian h = getHessian(fct);
        setTarget(fct);
        id = identity(n);
        fo = fct.f(x);
        if (DEBUG) {
            logger.info(format("%3d %s f:%.2g", iteration, display(x), fo));
        }
        do {
            xo = x;
            // solve the equation: (Hess(F) + lambda*Id)* dx = -grad(F)
            grad = g.gradient(x);
            hess = h.hessian(x);
            hesslp = add(hess, mult(id, lamda));
            dx = laSolver.solveSymmetric(hesslp, grad);
            x = sub(xo, dx);
            f = fct.f(x);
            m = 0;
            do {
                m++;
                hesslp = add(hess, mult(id, lamda / vu));
                dx = laSolver.solveSymmetric(hesslp, grad);
                xvu = sub(xo, dx);
                fvu = fct.f(xvu);
                if (fvu <= fo) {
                    f = fvu;
                    x = xvu;
                    lamda /= vu;
                } else if (f < fo) {
                    break;
                } else {
                    lamda *= vu;
                }
            } while (f > fo && m < MAX_MARQUARDTS);
            if (m >= MAX_MARQUARDTS) {
                String msg = "Marquardt iterations exceeded";
                logger.error(msg);
                throw new ArithmeticException(msg);
            }
            fo = f;
            if (DEBUG) {
                logger.info(format("%15s %3d %s f:%.2g lambda:%.5f", iteration,
                        display(x), f, lamda));
            }
            informIterationIsFinished(iteration, x);
        } while (!hasConverged(x, xo, getPrecision(), ++iteration,
                getMaxIterations()));
        for (int i = 0; i < guess.length; i++) {
            guess[i] = round(x[i], getPrecision());
        }
        informOptimizationIsFinished(iteration, guess);
        return iteration < getMaxIterations();
    }
}
 