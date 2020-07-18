/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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
import de.lab4inf.math.lapack.LinearAlgebra;

import static de.lab4inf.math.lapack.LinearAlgebra.add;
import static de.lab4inf.math.lapack.LinearAlgebra.maxdiff;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.arraycopy;

/**
 * Solve a set of first order differential equations
 * <pre>
 *    y1' = f1(x,y1,...,yn)
 *        ...
 *    yn' = fn(x,y1,...,yn)
 * </pre>
 * <p>
 * using an adaptive Runge-Kutta-Fehlberg or classical
 * Runge-Kutta method.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: FirstOrderSystemSolver.java,v 1.18 2014/05/28 18:18:02 nwulff Exp $
 * @since 18.01.2009
 */
public class FirstOrderSystemSolver extends RKFSolver implements FirstOrderOdeSystemSolver {
    protected Solver method;

    /**
     * Default constructor using the RungeKuttaFehlberg.
     */
    public FirstOrderSystemSolver() {
        this(Solver.RungeKuttaFehlberg);
    }

    ;

    /**
     * Solve the set of equations using the given solver method.
     *
     * @param solver algorithm to choose.
     */
    public FirstOrderSystemSolver(final Solver solver) {
        method = solver;
    }

    /**
     * Solve the given set of first order ODEs.
     *
     * @param x0  start point
     * @param y0  initial vector y(x0)
     * @param x1  end point
     * @param f   function array of the ODEs
     * @param eps desired accuracy
     * @return y(x1) solution vector
     */
    public double[] solve(final double x0, final double[] y0, final double x1,
                          final Function[] f, final double eps) {
        double[] y;
        double e = eps / 10;

        if (eps < EPS_MIN) {
            String msg = String.format("epsilon:%.2g less than esp_min=%.2g",
                    eps, EPS_MIN);
            logger.warn(msg);
            e = max(eps, EPS_MIN);
        }
        switch (method) {
            case RungeKutta:
                y = rungeKutta(x0, y0, x1, f, e);
                break;
            case RungeKuttaFehlberg:
                y = rungeKuttaFehlberg(x0, y0, x1, f, e);
                break;
            default:
                throw new IllegalArgumentException("no ODE solver selected");
        }
        //        int n = y0.length;
        //        for (int i = 0; i<n; i++) {
        //            y[i] = Accuracy.round(y[i], eps/100);
        //        }
        return y;
    }

    private double[] evaluate(final Function[] sys, final double x0,
                              final double[] w, final double[] x) {
        int n = sys.length;
        double[] y = new double[n];
        x[0] = x0;
        arraycopy(w, 0, x, 1, n);
        for (int i = 0; i < n; i++) {
            y[i] = sys[i].f(x);
        }
        return y;
    }

    /**
     * Evaluate the next y1 at point x1=x0+h, given x0 and y0 for a given
     * differential equation y'=f(x,y), using a fourth order Runge-Kutta
     * approximation.
     *
     * @param x0     double the x starting point
     * @param y0     double the value y(x0).
     * @param h      double the increment in x
     * @param system reference to the differential equation
     * @return double the approximate solution y1 at point x1=x0+h
     */
    private double[] nextStepRK(final double x0, final double[] y0,
                                final double h, final Function[] system) {
        int n = y0.length;
        double h2 = h / 2, xp = x0 + h2;
        double[] y = y0, k1, k2, k3, k4, x = new double[n + 1];
        k1 = evaluate(system, x0, y, x);
        y = add(y0, mult(k1, h2));
        k2 = evaluate(system, xp, y, x);
        y = add(y0, mult(k2, h2));
        k3 = evaluate(system, xp, y, x);
        y = add(y0, mult(k3, h));
        k4 = evaluate(system, x0 + h, y, x);
        for (int i = 0; i < n; i++) {
            y[i] = y0[i] + h * (k1[i] + 2 * (k2[i] + k3[i]) + k4[i]) / 6;
        }
        return y;
    }

    /**
     * Calculate y1 at point x1, given x0 and y0 with eps precision for the
     * given 1.st order system of differential equations y_k'=f(x,y_1,...,y_n)
     * using a classical Runge-Kutta method of fourth order.
     * The step width is adjusted until precision is reached.
     *
     * @param x0  double the x starting point
     * @param y0  starting values y_1(x0),...,y_n(x0)
     * @param x1  double the x end point
     * @param f   reference to the set of differential equations
     * @param eps double the maximal error of y
     * @return array with the approximate solutions y1(x),...,yn(x) at point x1
     */
    public double[] rungeKutta(final double x0, final double[] y0,
                               final double x1, final Function[] f, final double eps) {
        double x, h;
        double[] ya, yb = y0;
        h = abs(x1 - x0) / 8;
        h = abs(min(h, H_MAX));
        do {
            ya = yb;
            h /= 2;
            for (x = x0, yb = y0; x < x1; x += h) {
                yb = nextStepRK(x, yb, h, f);
            }
        } while (LinearAlgebra.maxdiff(ya, yb) > eps && h > H_MIN);
        if (h < H_MIN) {
            String msg = String.format("RK Sys no convergence h=%g", h);
            logger.warn(msg);
            throw new ArithmeticException(msg);
        }
        return yb;
    }

    /**
     * Calculate y1 at point x1, given x0 and y0 with eps precision for the
     * given 1.st order system of differential equations y_k'=f(x,y_1,...,y_n)
     * using a adaptive Runge-Kutta-Fehlberg method of fourth order.
     * <p>
     * This method uses less function evaluations but is not as precise as the
     * Runge-Kutta method.
     *
     * @param x0  double the x starting point
     * @param y0  starting values y_1(x0),...,y_n(x0)
     * @param x1  double the x end point
     * @param f   reference to the set of differential equations
     * @param eps double the maximal error of y
     * @return array with the approximate solutions y1(x),...,yn(x) at point x1
     */
    public double[] rungeKuttaFehlberg(final double x0, final double[] y0,
                                       final double x1, final Function[] f, final double eps) {
        int i = 0, n = y0.length, max = (int) (2 * abs(x0 - x1) / H_MIN);
        double h = min(H_MAX, abs(x1 - x0) / 8);
        double xj = x0, err, q;
        double[] k1, k2, k3, k4, k5, k6, x = new double[n + 1];
        double[] rk4, rk5, y, yj = y0;

        do {

            k1 = evaluate(f, xj, yj, x);

            y = add(yj, mult(k1, h * B21));
            k2 = evaluate(f, xj + A2 * h, y, x);

            y = add(add(yj, mult(k1, h * B31)), mult(k2, h * B32));
            k3 = evaluate(f, xj + A3 * h, y, x);

            y = add(add(add(yj, mult(k1, h * B41)), mult(k2, h * B42)), mult(k3, h
                    * B43));
            k4 = evaluate(f, xj + A4 * h, y, x);

            y = add(add(yj, mult(k1, h * B51)), mult(k2, h * B52));
            y = add(add(y, mult(k3, h * B53)), mult(k4, h * B54));
            k5 = evaluate(f, xj + A5 * h, y, x);

            y = add(add(yj, mult(k1, h * B61)), mult(k2, h * B62));
            y = add(add(y, mult(k3, h * B63)), mult(k4, h * B64));
            y = add(y, mult(k5, h * B65));
            k6 = evaluate(f, xj + A6 * h, y, x);

            // runge-kutta-fehlberg approximation to fourth order
            // rk4 = wj + C1 * k1 + C3 * k3 + C4 * k4 + C5 * k5;
            rk4 = add(add(yj, mult(k1, h * C1)), mult(k3, h * C3));
            rk4 = add(add(rk4, mult(k4, h * C4)), mult(k5, h * C5));

            // runge-kutta-fehlberg approximation to fifth order
            // rk5 = wj + D1 * k1 + D3 * k3 + D4 * k4 + D5 * k5 + D6 * k6;
            rk5 = add(add(yj, mult(k1, h * D1)), mult(k3, h * D3));
            rk5 = add(add(rk5, mult(k4, h * D4)), mult(k5, h * D5));
            rk5 = add(rk5, mult(k6, h * D6));

            // the approximate error
            err = maxdiff(rk4, rk5);
            if (err < eps && i > 0) {
                xj += h;
                yj = rk4;
            }
            // calculate the next step width
            q = qNext(h, err, eps);
            h = min(q * h, H_MAX);
            //logger.warn(String.format("x:%.4f q:%.2f h:%.3g err:%.3g", xj, q,h, err));

            if (h < H_MIN && (xj + h < x1)) {
                String msg = String.format("RKF Sys no convergence! step width %g", h);
                logger.info(msg);
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
        } while (xj < x1 && (++i < max));
        return yj;
    }

    /**
     * Enumeration of possible methods.
     * Default is adaptive Runge-Kutta-Fehlberg.
     */
    public static enum Solver {
        /**
         * Flag for Runge-Kutta method.
         */
        RungeKutta,
        /**
         * Flag for Runge-Kutta-Fehlber method.
         */
        RungeKuttaFehlberg
    }
}
 