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

import static de.lab4inf.math.lapack.LinearAlgebra.add;
import static de.lab4inf.math.lapack.LinearAlgebra.diff;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.lang.System.arraycopy;

/**
 * Solve a set of second order differential equations
 * <pre>
 *      y_1'' = f1(x,y_1,y_2,...,y_n,dy_1,...,dy_n)
 *         ...
 *      y_n'' = fn(x,y_1,y_2,...,y_n,dy_1,...,dy_n)
 * </pre>
 * using the Runge-Kutta Nyström method.
 *
 * @author nwulff
 * @version $Id: SecondOrderSystemSolver.java,v 1.6 2014/05/12 14:44:48 nwulff Exp $
 * @since 08.05.2014
 */
public class SecondOrderSystemSolver extends AbstractOdeSolver implements SecondOrderOdeSystemSolver {

    /**
     * format for logging messages.
     */
    static final String FMT = "ite:%2d x:%.4f y:%.7f q:%.2f h:%.4g dw:%f";


    private double[] evaluate(final Function[] sys, final double x0,
                              final double[] y, final double[] dy, final double[] x) {
        int n = sys.length;
        double[] z = new double[n];
        x[0] = x0;
        arraycopy(y, 0, x, 1, n);
        arraycopy(dy, 0, x, 1 + n, n);
        for (int i = 0; i < n; i++) {
            z[i] = sys[i].f(x);
        }
        return z;
    }

    /**
     * Evaluate the next (y1,dy1) at point x1=x0+h, given x0, y0 and dy0 for a
     * given differential equation y''=f(x,y,y'), using a Runge-Kutta Nyström
     * approximation.
     *
     * @param x0     double the x starting point
     * @param y0     double the value y(x0).
     * @param dy0    double the value y'(x0).
     * @param h      double the increment in x
     * @param system reference to the differential equation
     * @return the approximate solution y1,dy1 at point x1=x0+h
     */
    private double[][] nextStepRKN(final double x0, final double[] y0,
                                   final double[] dy0, final double h, final Function[] system) {
        int n = y0.length;
        double[] x = new double[2 * n + 1];
        double h2 = h / 2, hq = h * h;
        double[] k1, k2, k3, k4, k23;
        double[] y1, y2, y3, y4, y, dy;
        double[] dy1, dy2, dy3, dy4;
        double x1 = x0, x2 = x0 + h2, x3 = x0 + h2, x4 = x0 + h;
        y1 = y0;
        dy1 = dy0;
        k1 = evaluate(system, x1, y1, dy1, x);

        y2 = add(y0, add(mult(dy0, h2), mult(k1, hq / 8)));
        dy2 = add(dy0, mult(k1, h2));
        k2 = evaluate(system, x2, y2, dy2, x);

        y3 = y2;
        dy3 = add(dy0, mult(k2, h2));
        k3 = evaluate(system, x3, y3, dy3, x);

        y4 = add(y0, add(mult(dy0, h), mult(k3, hq / 2)));
        dy4 = add(dy0, mult(k3, h));
        k4 = evaluate(system, x4, y4, dy4, x);


        k23 = add(k2, k3);
        // y = y0+h*dy0 + hq*(k1+k2+k3)/6;
        y = add(y0, mult(dy0, h));
        y = add(y, mult(add(k1, k23), hq / 6));

        // dy = dy0 + h*(k1+2*k2+2*k3+k4)/6;
        dy = add(dy0, mult(add(add(k1, k4), mult(k23, 2)), h / 6));
        return new double[][]{y, dy};

    }

    /**
     * Calculate y1 at point x1, given x0, y0 and dy0 with eps precision for the
     * given 2.st order system of differential equations
     * y_k''=f(x,y_1,...,y_n,dy_1,...,dy_n)
     * using the Runge-Kutta Nyström method.
     * The step width is adjusted until precision is reached.
     *
     * @param x0  double the x starting point
     * @param y0  starting values y_1(x0),...,y_n(x0)
     * @param dy0 starting values dy_1(x0),...,dy_n(x0)
     * @param x1  double the x end point
     * @param f   reference to the set of differential equations
     * @param eps double the maximal error of y
     * @return array with the approximate solutions y1(x),...,yn(x) at point x1
     */
    public double[] rungeKuttaNystroem(final double x0, final double[] y0,
                                       final double[] dy0, final double x1, final Function[] f, final double eps) {
        double x, h;
        double[][] ya, yc = {y0, dy0}, yb = yc;
        h = abs(x1 - x0) / 8;
        h = abs(min(h, H_MAX));
        do {
            ya = yb;
            h /= 2;
            for (x = x0, yb = yc; x < x1; x += h) {
                yb = nextStepRKN(x, yb[0], yb[1], h, f);
            }
        } while (diff(ya, yb) > eps && h > H_MIN);
        if (h < H_MIN) {
            String msg = format("RKN no convergence h=%g", h);
            logger.warn(msg);
            throw new ArithmeticException(msg);
        }
        return yb[0];
    }

    /* (non-Javadoc)
     * @see SecondOrderOdeSystemSolver#solve(double, double[], double[], double,Function[], double)
     */
    @Override
    public double[] solve(final double x0, final double[] y0, final double[] dy0,
                          final double x1, final Function[] f, final double eps) {
        double e = eps / 10;
        if (eps < EPS_MIN) {
            String msg = format("epsilon:%.2g less than esp_min=%.2g", eps, EPS_MIN);
            logger.warn(msg);
            e = max(e, EPS_MIN);
        }
        return rungeKuttaNystroem(x0, y0, dy0, x1, f, e);
    }

}
 