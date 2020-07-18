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

import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static java.lang.Math.abs;

/**
 * Interpolate a set of tuples (x,y) using the Neville algorithm to
 * construct the Lagrange polynomial interpolating the (x,y) tupels.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: NevilleInterpolator.java,v 1.6 2011/09/12 14:49:20 nwulff Exp $
 * @since 08.02.2005
 */
public class NevilleInterpolator extends Interpolator {
    /**
     * Constructor to interpolate the given (x,y)-tuples.
     *
     * @param xv x values
     * @param yv y values
     */
    public NevilleInterpolator(final double[] xv, final double[] yv) {
        super(xv, yv);

    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.interpolation.Interpolator#interpolate(double)
     */
    @Override
    protected double interpolate(final double x) {
        return neville(x);
        // int n = xv.length - 1;
        // return nevilleRecursive(n, n, x);
    }

    /**
     * The original none optimized recursive version of Neville's algorithm.
     *
     * @param j first in tableau.
     * @param k second in tableau.
     * @param x double point to evaluate
     * @return double polynom value p_ij(x).
     */
    public double nevilleRecursive(final int j, final int k, final double x) {
        double y;
        if (j == 0) {
            y = yv[k];
        } else {
            y = (x - xv[k - j]) * nevilleRecursive(j - 1, k, x) - (x - xv[k])
                    * nevilleRecursive(j - 1, k - 1, x);
            y /= (xv[k] - xv[k - j]);
        }
        return y;
    }

    /**
     * An optimized iterative version of Neville's algorithm.
     *
     * @param x the argument to evaluate
     * @return the polynomial value p(x)
     */
    public double neville(final double x) {
        int ns, n = xv.length;
        double dx, xi, xm, w, dy = 0, y = 0;
        double[] c = copy(yv);
        double[] d = copy(yv);
        ns = nearestIndex(x);
        // ns = lowerIndex(x);
        y = yv[ns];
        if (abs(xv[ns] - x) < TINY) {
            return y;
        }
        ns--;
        for (int m = 1; m <= n; m++) {
            for (int i = 1; i <= n - m; i++) {
                xi = xv[i - 1] - x;
                xm = xv[i + m - 1] - x;
                w = c[i] - d[i - 1];
                dx = xi - xm;
                if (dx == 0) {
                    String msg = String.format("x[%d] == x[%d+%d]", i, i, m);
                    throw new IllegalArgumentException(msg);
                }
                dx = w / dx;
                d[i - 1] = xm * dx;
                c[i - 1] = xi * dx;
            }
            if (2 * ns < (n - m)) {
                dy = c[ns + 1];
            } else {
                dy = d[ns--];
            }
            y += dy;
        }
        // logger.info(String.format("p(%+.2f)=%f err:%+.6g",x,y,dy));
        return y;
    }
}
 