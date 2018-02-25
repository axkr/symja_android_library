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

import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Solve the first order differential equation
 * <pre>
 *    y' = f(x,y)
 * </pre>
 * using a classical Runge-Kutta method.
 * <pre>
 * Implementation is based on the book "Numerical Methods",
 * J.D. Faires and R.L.Burden, (1995), chapter 5.
 * and
 * "Formelsammlung zur Numerischen Mathematik", G.Engeln-Muellges, chapter 15.
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: RKSolver.java,v 1.7 2014/05/28 18:18:02 nwulff Exp $
 * @since 21.12.2008
 */
public class RKSolver extends AbstractOdeSolver implements FirstOrderOdeSolver {

    /**
     * Calculate y1 at point x1, given x0 and y0 with eps precision
     * for the given first order differential equation y'=f(x,y).
     *
     * @param x0  the starting point
     * @param y0  the starting value y(x0).
     * @param x1  the end point
     * @param f   reference to the differential equation
     * @param eps double the maximal error of y1
     * @return double the approximate solution y1(x1)
     */
    public double solve(final double x0, final double y0, final double x1,
                        final Function f, final double eps) {
        // logger.info(
        // String.format("selecting solver %s with precision %g", m, eps));
        double y, e = eps;
        if (eps < EPS_MIN) {
            String msg = String.format("epsilon:%.2g less than esp_min=%.2g",
                    eps, EPS_MIN);
            logger.warn(msg);
            e = max(eps, EPS_MIN);
        }
        y = rungeKutta(x0, y0, x1, f, e);
        //y = Accuracy.round(y, eps/100);
        return y;
    }

    /**
     * Evaluate the next y1 at point x1=x0+h, given x0 and y0 for a given
     * differential equation y'=f(x,y), using a fourth order Runge-Kutta
     * approximation.
     *
     * @param x0 double the x starting point
     * @param y0 double the value y(x0).
     * @param h  double the increment in x
     * @param dy reference to the differential equation
     * @return double the approximate solution y1 at point x1=x0+h
     */
    private double nextStepRK(final double x0, final double y0, final double h,
                              final Function dy) {
        double xp, h2;
        double y, k1, k2, k3, k4;
        h2 = h / 2;
        xp = x0 + h2;
        k1 = dy.f(x0, y0);
        k2 = dy.f(xp, y0 + h2 * k1);
        k3 = dy.f(xp, y0 + h2 * k2);
        k4 = dy.f(x0 + h, y0 + h * k3);
        y = y0 + h * (k1 + 2 * k2 + 2 * k3 + k4) / 6;
        return y;
    }

    /**
     * Calculate y1 at point x1, given x0 and y0 with eps precision for the
     * given 1.st order differential equation y'=f(x,y) using a classical
     * Runge-Kutta method of fourth order. The step width is adjusted until
     * precision is reached.
     *
     * @param x0  double the x starting point
     * @param y0  double the value y(x0).
     * @param x1  double the x end point
     * @param f   reference to the differential equation
     * @param eps double the maximal error of y1
     * @return double the approximate solution y1 at point x1
     */
    public double rungeKutta(final double x0, final double y0, final double x1,
                             final Function f, final double eps) {
        double x, ya, yb = y0, h;
        h = abs(x1 - x0) / 8;
        h = abs(min(h, H_MAX));
        do {
            ya = yb;
            h /= 2;
            for (x = x0, yb = y0; x < x1; x += h) {
                yb = nextStepRK(x, yb, h, f);
            }
        } while (!hasReachedAccuracy(yb, ya, eps) && h > H_MIN);
        if (h < H_MIN) {
            String msg = String.format("RK no convergence width h=%f", h);
            logger.info(msg);
            throw new ArithmeticException(msg);
        }
        return yb;
    }
}
 