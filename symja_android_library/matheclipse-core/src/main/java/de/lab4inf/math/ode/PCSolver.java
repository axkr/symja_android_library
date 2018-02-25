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

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Implementation of a predictor-corrector solver for ODEs.
 *
 * @author nwulff
 * @version $Id: PCSolver.java,v 1.1 2014/10/10 13:06:05 nwulff Exp $
 * @since 19.09.2014
 */
public class PCSolver extends AbstractOdeSolver implements FirstOrderOdeSolver {

    /* (non-Javadoc)
     * @see de.lab4inf.math.ode.FirstOrderOdeSolver#solve(double, double, double, de.lab4inf.math.Function, double)
     */
    public double solve(double x0, double y0, double x1, Function f, double eps) {
        // logger.info(
        // String.format("selecting solver %s with precision %g", m, eps));
        double y, e = eps / 10;
        if (e < EPS_MIN) {
            String msg = String.format("epsilon:%.2g less than esp_min=%.2g",
                    eps, EPS_MIN);
            logger.warn(msg);
            e = max(eps, EPS_MIN);
        }
        y = predictorCorrector(x0, y0, x1, f, e);
        //y = Accuracy.round(y, eps/100);
        return y;
    }

    /**
     * Calculate y1 at point x1, given x0 and y0 with eps precision for the
     * given first order differential equation y'=f(x,y) using an adaptive
     * predictor corrector method of fourth order.
     *
     * @param x0  double the x starting point
     * @param y0  double the value y(x0).
     * @param x1  double the x end point
     * @param dy  reference to the differential equation
     * @param eps double the maximal error of y1
     * @return double the approximate solution y1 at point x1
     */
    public double predictorCorrector(final double x0, final double y0,
                                     final double x1, final Function dy, final double eps) {
        double fn0, fn1, fn2, fn3;
        double xn0, xn1, xn2, xn3;
        double yn0, yn1, yn2, yn3;
        double py, y, err, h;
        h = abs(x1 - x0) / 100;
        h = abs(min(h, H_MAX));
        y = y0;

        xn0 = x0;
        yn0 = y0;

        xn1 = xn0 - h;
        xn2 = xn1 - h;
        xn3 = xn2 - h;

        yn1 = yn0 - h * dy.f(xn0, yn0);
        yn2 = yn1 - h * dy.f(xn1, yn1);
        yn3 = yn2 - h * dy.f(xn2, yn2);

        fn0 = dy.f(xn0, yn0);
        fn1 = dy.f(xn1, yn1);
        fn2 = dy.f(xn2, yn2);
        fn3 = dy.f(xn3, yn3);


        do {

            xn3 = xn2;
            xn2 = xn1;
            xn1 = xn0;
            yn3 = yn2;
            yn2 = yn1;
            yn1 = yn0;


            xn0 += h;

            py = y + h * (55 * fn0 - 59 * fn1 + 37 * fn2 - 9 * fn3) / 24;
            y += h * (9 * dy.f(xn0, py) + 19 * fn0 - 5 * fn1 + fn2) / 24;

            yn0 = y;

            err = Math.abs((y - py) / y);
            if (err > eps) {
                xn0 -= h;
                h /= 2;

                xn1 = xn0 - h;
                xn2 = xn1 - h;
                xn3 = xn2 - h;

                yn1 = yn0 - h * dy.f(xn0, yn0);
                yn2 = yn1 - h * dy.f(xn1, yn1);
                yn3 = yn2 - h * dy.f(xn2, yn2);

                fn0 = dy.f(xn0, yn0);
                fn1 = dy.f(xn1, yn1);
                fn2 = dy.f(xn2, yn2);
                fn3 = dy.f(xn3, yn3);
            } else {
                fn3 = fn2;
                fn2 = fn1;
                fn1 = fn0;
                fn0 = dy.f(xn0, yn0);
            }
            //             if(err < eps/1E4) {
            //                 h *=1.8;
            //                 //h = abs(min(h, H_MAX));
            //             }
        } while (xn0 < x1);

        return y;
    }
}
 