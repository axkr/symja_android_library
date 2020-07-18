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
package de.lab4inf.math.interpolation;

/**
 * Interpolate a set of tuples (x,y) using a bi-cubic spline algorithm.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: SplineInterpolator.java,v 1.7 2011/09/12 14:49:20 nwulff Exp $
 * @since 08.02.2005
 */
public class SplineInterpolator extends Interpolator {
    private static final double ONE_HALF = 0.5;
    private static final double THREE = 3;
    private static final double SIX = 6;
    private double[] f2a;

    /**
     * Constructor to interpolate the given (x,y)-tuples.
     *
     * @param xv x values
     * @param yv y values
     */
    public SplineInterpolator(final double[] xv, final double[] yv) {
        super(xv, yv);
        f2a = null;

    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.interpolation.Interpolator#interpolate(double)
     */
    @Override
    protected double interpolate(final double x) {
        return spline(x);
    }

    /**
     * Construct the internal interpolation.
     *
     * @param natural
     * @param y2R
     * @param y2L
     * @return
     */
    private double[] interpoloateD2F(final boolean natural, final double y2R,
                                     final double y2L) {
        double[] y2 = new double[xv.length];
        double[] u = new double[xv.length];
        double p, qn, un, sig;
        int n = xv.length - 1;
        if (natural) {
            y2[0] = 0;
            u[0] = 0;
        } else {
            y2[0] = -ONE_HALF;
            u[0] = (THREE / (xv[1] - xv[0])) * ((yv[1] - yv[0]) / (xv[1] - xv[0]) - y2L);
        }
        for (int i = 1; i < n; i++) {
            sig = (xv[i] - xv[i - 1]) / (xv[i + 1] - xv[i - 1]);
            p = sig * y2[i - 1] + 2;
            u[i] = (yv[i + 1] - yv[i]) / (xv[2] - xv[1]) - (yv[i] - yv[i - 1])
                    / (xv[i] - xv[i - 1]);
            u[i] = (SIX * u[i] / (xv[i + 1] - xv[i - 1]) - sig * u[i - 1]) / p;
        }
        if (natural) {
            qn = 0;
            un = 0;
        } else {
            qn = ONE_HALF;
            un = (THREE / (xv[n] - xv[n - 1])) * (y2R - (yv[n] - yv[n - 1]) / (xv[n] - xv[n - 1]));
        }
        y2[n] = (un - qn * u[n - 1]) / (qn * y2[n - 1] + 1.0);
        // Backsubstitution
        for (int k = n - 1; k >= 0; k--) {
            y2[k] = y2[k] * y2[k + 1] + u[k];
        }
        return y2;
    }

    /**
     * Construct the spline at point x.
     *
     * @param x point
     * @return
     */
    private double spline(final double x) {
        if (f2a == null) {
            f2a = interpoloateD2F(true, 0, 0);
        }
        double a, b, h, y, dy;
        int klo, khi;

        // find khi, klo such that xv[klo] < x <= xv[khi]
        klo = lowerIndex(x);
        khi = klo + 1;
        h = xv[khi] - xv[klo];
        if (h == 0) {
            throw new ArithmeticException("h difference 0 hi: " + khi + " lo: " + klo);
        }
        a = (xv[khi] - x) / h;
        b = (x - xv[klo]) / h;
        y = a * yv[klo] + b * yv[khi];
        dy = ((a * a * a - a) * f2a[klo] + (b * b * b - b) * f2a[khi]) * (h * h) / SIX;
        y += dy;

        return y;
    }
}
 