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

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

/**
 * Solve the first order differential equation
 * <pre>
 *    y' = f(x,y)
 * </pre>
 * using an adaptive Runge-Kutta-Fehlberg method.
 * <pre>
 * Implementation is based on the book "Numerical Methods",
 * J.D. Faires and R.L.Burden, (1995), chapter 5.
 * and
 * "Formelsammlung zur Numerischen Mathematik", G.Engeln-Muellges, chapter 15.
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: RKFSolver.java,v 1.20 2014/05/28 18:18:02 nwulff Exp $
 * @since 21.12.2008
 */
public class RKFSolver extends AbstractOdeSolver implements FirstOrderOdeSolver {
    /**
     * constants for the runge kutta fehlberg method.
     */
    protected static final double A2 = 0.25, A3 = 0.375, A4 = 12. / 13, A5 = 1,
            A6 = 0.5;
    /**
     * constants for the runge kutta fehlberg method.
     */
    protected static final double B21 = 0.25, B31 = 3. / 32, B32 = 9. / 32;
    /**
     * constants for the runge kutta fehlberg method.
     */
    protected static final double B41 = 1932. / 2197, B42 = -7200. / 2197,
            B43 = 7296. / 2197;
    /**
     * constants for the runge kutta fehlberg method.
     */
    protected static final double B51 = 439. / 216, B52 = -8, B53 = 3680. / 513,
            B54 = -845. / 4104;
    /**
     * constants for the runge kutta fehlberg method.
     */
    protected static final double B61 = -8. / 27, B62 = 2, B63 = -3544. / 2565.,
            B64 = 1849. / 4104, B65 = -0.275;
    /**
     * constants for the runge kutta fehlberg method.
     */
    protected static final double C1 = 25. / 216, C3 = 1408. / 2565,
            C4 = 2197. / 4104, C5 = -0.2;
    /**
     * constants for the runge kutta fehlberg method.
     */
    protected static final double D1 = 16. / 135, D3 = 6656. / 12825,
            D4 = 28561. / 56430, D5 = -0.18, D6 = 2. / 55;

    /**
     * Calculate the factor q for the next step width h_new = q*h_old,
     * with 0.1 <= q <= 4.
     *
     * @param h   the actual step width
     * @param err the approximate error
     * @param eps the allowed error
     * @return q
     */
    public static double qNext(final double h, final double err, final double eps) {
        final double s = 0.84;
        double q = 1, d = err / eps;
        if (err >= eps) {
            q = s / pow(d / h, 0.2);
        } else {
            q = s / pow(h * d, 0.25);
        }
        q = max(min(q, 4), 0.1);
        return q;
    }

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
        double y, e = eps / 10;
        if (e < EPS_MIN) {
            String msg = String.format("epsilon:%.2g less than esp_min=%.2g",
                    eps, EPS_MIN);
            logger.warn(msg);
            e = max(eps, EPS_MIN);
        }
        y = rungeKuttaFehlberg(x0, y0, x1, f, e);
        //y = Accuracy.round(y, eps/100);
        return y;
    }

    /**
     * Calculate y1 at point x1, given x0 and y0 with eps precision for the
     * given first order differential equation y'=f(x,y) using an adaptive
     * Runge-Kutta-Fehlberg method of fourth order.
     *
     * @param x0  double the x starting point
     * @param y0  double the value y(x0).
     * @param x1  double the x end point
     * @param dy  reference to the differential equation
     * @param eps double the maximal error of y1
     * @return double the approximate solution y1 at point x1
     */
    public double rungeKuttaFehlberg(final double x0, final double y0,
                                     final double x1, final Function dy, final double eps) {
        double h = min(H_MAX, abs(x0 - x1) / 8);
        double k1, k2, k3, k4, k5, k6;
        double xj = x0, wj = y0, dw, rk4, rk5, q;
        while (xj < x1) {
            k1 = h * dy.f(xj, wj);
            k2 = h * dy.f(xj + A2 * h, wj + B21 * k1);
            k3 = h * dy.f(xj + A3 * h, wj + B31 * k1 + B32 * k2);
            k4 = h * dy.f(xj + A4 * h, wj + B41 * k1 + B42 * k2 + B43 * k3);
            k5 = h * dy.f(xj + A5 * h, wj + B51 * k1 + B52 * k2 + B53 * k3 + B54 * k4);
            k6 = h * dy.f(xj + A6 * h, wj + B61 * k1 + B62 * k2 + B63 * k3 + B64 * k4 + B65 * k5);
            // runge-kutta-fehlberg approximation to fourth order
            rk4 = wj + C1 * k1 + C3 * k3 + C4 * k4 + C5 * k5;
            // runge-kutta-fehlberg approximation to fifth order
            rk5 = wj + D1 * k1 + D3 * k3 + D4 * k4 + D5 * k5 + D6 * k6;
            // the approximate error
            dw = abs(rk4 - rk5);
            if (dw < eps) {
                xj += h;
                wj = rk4;
            }
            // calculate the next step width
            q = qNext(h, dw, eps);
            h = min(q * h, H_MAX);
            //logger.warn(String.format("x=%.2f y=%.2f q=%.2f h=%.3g dw=%.3g", xj + h, wj, q, h, dw));
            if (h < H_MIN && (xj + h < x1)) {
                String msg = String.format("RKF no convergence! step width %g", h);
                logger.error(msg);
                //h = H_MIN;
                throw new ArithmeticException(msg);
            }
            if (xj + h > x1) {
                if (abs(x1 - xj) < eps) {
                    xj = x1;
                } else {
                    h = x1 - xj;
                }
            }
        }
        return wj;
    }
}
 