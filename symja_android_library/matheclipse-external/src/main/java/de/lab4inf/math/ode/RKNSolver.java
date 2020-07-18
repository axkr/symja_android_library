/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.ode;

import de.lab4inf.math.Function;

import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
//import de.lab4inf.math.util.Accuracy;

/**
 * Solve the second order differential equation
 * <pre>
 *    y'' = f(x,y,y')
 * </pre>
 * <p>
 * via the Runge-Kutta Nyström method using
 * <pre>
 * Abramowitz & Stegun, Algorithm 25.5.20.
 * </pre>
 *
 * @author nwulff
 * @version $Id: RKNSolver.java,v 1.7 2014/05/28 17:34:55 nwulff Exp $
 * @since 06.05.2014
 */
public class RKNSolver extends AbstractOdeSolver implements SecondOrderOdeSolver {

    /* (non-Javadoc)
     * @see de.lab4inf.math.ode.SecondOrderOdeSolver#solve(double, double, double, double, Function, double)
     */
    @Override
    public double solve(final double x0, final double y0, final double dy0,
                        final double x1, final Function f, final double eps) {
        double y, e = eps / 10;
        if (eps < EPS_MIN) {
            String msg = String.format("epsilon:%.2g less than esp_min=%.2g",
                    eps, EPS_MIN);
            logger.warn(msg);
            e = max(eps, EPS_MIN);
        }
        y = rungeKuttaNystrom(x0, y0, dy0, x1, f, e);
        //y = Accuracy.round(y, eps/100);
        return y;
    }

    /**
     * Evaluate the next y1 at point x1=x0+h, given x0 and y0 for a given
     * differential equation y''=f(x,y,y''), using a Runge-Kutta Nyström
     * approximation.
     *
     * @param x0  double the x starting point
     * @param y0  double the value y(x0).
     * @param dy0 double the value y'(x0).
     * @param h   double the increment in x
     * @param dy  reference to the differential equation
     * @return double the approximate solution y1 and dy1 at point x1=x0+h
     */
    private double[] nextStepRKN(final double x0, final double y0, final double dy0,
                                 final double h, final Function fct) {
        double h2 = h / 2, hq = h * h;
        double y, dy;
        double k1, k2, k3, k4;
        double y1, y2, y3, y4;
        double dy1, dy2, dy3, dy4;
        double x1 = x0, x2 = x0 + h2, x3 = x0 + h2, x4 = x0 + h;
        y1 = y0;
        dy1 = dy0;
        k1 = fct.f(x1, y1, dy1);

        y2 = y0 + h2 * dy0 + hq * k1 / 8;
        dy2 = dy0 + h2 * k1;
        k2 = fct.f(x2, y2, dy2);

        y3 = y2;
        dy3 = dy0 + h2 * k2;
        k3 = fct.f(x3, y3, dy3);

        y4 = y0 + h * dy0 + hq * k3 / 2;
        dy4 = dy0 + h * k3;
        k4 = fct.f(x4, y4, dy4);

        y = y0 + h * dy0 + hq * (k1 + k2 + k3) / 6;
        dy = dy0 + h * (k1 + 2 * k2 + 2 * k3 + k4) / 6;
        return new double[]{y, dy};
    }

    /**
     * Calculate y1 at point x1, given x0, y(x0) and y'(x0) with eps precision
     * for the given 2.nd order differential equation y''=f(x,y,y') using a
     * classical Runge-Kutta Nyström method of fourth order. The step width is
     * adjusted until precision is reached.
     *
     * @param x0  double the x starting point
     * @param y0  double the value y(x0).
     * @param dy0 double the derivative value y'(x0).
     * @param x1  double the x end point
     * @param f   reference to the differential equation
     * @param eps double the maximal error of y1
     * @return double the approximate solution y1 at point x1
     */
    public double rungeKuttaNystrom(final double x0, final double y0, final double dy0,
                                    final double x1, final Function f, final double eps) {
        double x, h;
        double[] ya, yc = {y0, dy0}, yb = yc;
        h = abs(x1 - x0) / 8;
        h = abs(min(h, H_MAX));
        do {
            ya = yb;
            h /= 2;
            for (x = x0, yb = yc; x < x1; x += h) {
                yb = nextStepRKN(x, yb[0], yb[1], h, f);
            }
        } while (!hasReachedAccuracy(yb, ya, eps) && h > H_MIN);
        if (h < H_MIN) {
            String msg = String.format("RKN no convergence width h=%f", h);
            logger.info(msg);
            throw new ArithmeticException(msg);
        }
        return yb[0];
    }

}
 