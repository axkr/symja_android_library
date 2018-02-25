/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.ode;

import de.lab4inf.math.Function;

/**
 * Solve a set of second order differential equations.
 * <p>
 * <pre>
 *      y_1'' = f1(x,y_1,y_2,...,y_n,dy_1,...,dy_n)
 *         ...
 *      y_n'' = fn(x,y_1,y_2,...,y_n,dy_1,...,dy_n)
 * </pre>
 *
 * @author nwulff
 * @version $Id: SecondOrderOdeSystemSolver.java,v 1.1 2014/05/11 08:48:43 nwulff Exp $
 * @since 08.05.2014
 */
public interface SecondOrderOdeSystemSolver extends OdeSolver {
    /**
     * Calculate the vector y1 at point x1, given x0, vector y0 and the
     * derivative vector dy0 with eps precision for the given of second
     * order differential equations.
     * <pre>
     *  y_k''=f(x,y_1, ..., y_n, dy_1,...,dy_n)    k=1,...,n
     * </pre>
     *
     * @param x0  the starting point
     * @param y0  the starting values y(x0).
     * @param dy0 the starting derivative values y'(x0).
     * @param x1  the end point
     * @param f   reference to the differential equations
     * @param eps double the maximal error of y1
     * @return double array the approximate solutions y1(x1)
     */
    double[] solve(final double x0, final double[] y0, final double[] dy0,
                   final double x1, final Function[] f, final double eps);

}
 