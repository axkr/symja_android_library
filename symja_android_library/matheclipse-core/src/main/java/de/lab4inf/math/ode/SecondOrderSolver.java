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

package de.lab4inf.math.ode;

import de.lab4inf.math.Function;
import de.lab4inf.math.gof.Visitor;

/**
 * Solve a second order differential equation.
 * <pre>
 *    y'' = f(x,y,y')
 * </pre>
 * Done by transforming to a set of two first
 * order ode's, using the first order system
 * solver.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: SecondOrderSolver.java,v 1.9 2014/06/26 11:25:35 nwulff Exp $
 * @since 18.01.2009
 */
public abstract class SecondOrderSolver extends FirstOrderSystemSolver implements
        SecondOrderOdeSolver {


    /**
     * Constructor with a given solver method.
     *
     * @param method to choose
     */
    protected SecondOrderSolver(final Solver method) {
        super(method);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.ode.SecondOrderOdeSolver#solve()
     */
    public double solve(final double x0, final double y0, final double dy0,
                        final double x1, final Function f, final double eps) {
        // solve y'' = f(x,y,y') by setting
        // y' = z
        // z' = f(x,y,y')
        Function z = new Ode2Wrapper();
        Function[] sys = {z, f};
        double[] ybeg = {y0, dy0};
        double[] yend = solve(x0, ybeg, x1, sys, eps);
        return yend[0];
    }

    static class Ode2Wrapper implements Function {

        /* (non-Javadoc)
         * @see de.lab4inf.math.Function#f(double[])
         */
        public double f(final double... x) {
            return x[2];
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

}
 