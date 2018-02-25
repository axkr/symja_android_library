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

import de.lab4inf.math.functions.L4MFunction;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static java.lang.Math.abs;

/**
 * Interpolate a set of (x,y) tuples.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Interpolator.java,v 1.13 2011/09/12 14:49:20 nwulff Exp $
 * @since 07.01.2005
 */
public abstract class Interpolator extends L4MFunction {
    protected static final double TINY = 100 * Accuracy.DEPS;
    protected double[] xv, yv;

    /**
     * Constructor setting the (x,y)-tuples to interpolate.
     *
     * @param xv the x values
     * @param yv The y values
     */
    public Interpolator(final double[] xv, final double[] yv) {
        this.xv = copy(xv);
        this.yv = copy(yv);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.Function#f(double)
     */
    @Override
    public final double f(final double... x) {
        return interpolate(x[0]);
    }

    /**
     * interpolation function to implement.
     *
     * @param x value to evaluate
     * @return the interpolation at point x
     */
    protected abstract double interpolate(final double x);

    /**
     * find k such that xv[k] less than x less than xv[k+1].
     *
     * @param x double the value
     * @return the lower index
     */
    protected int lowerIndex(final double x) {
        int klo, khi, k;
        klo = 0;
        khi = xv.length - 1;
        while (khi - klo > 1) {
            k = (khi + klo) >>> 1;
            if (xv[k] > x) {
                khi = k;
            } else {
                klo = k;
            }
        }
        return klo;
    }

    /**
     * find k such that |x-xv[k]| is minimal, that is xv[k]
     * is the nearest to x.
     *
     * @param x double the value
     * @return the nearest index
     */
    protected int nearestIndex(final double x) {
        int k;
        double dl, dh, d;
        k = lowerIndex(x);
        dl = abs(xv[k] - x);
        dh = abs(xv[k + 1] - x);
        d = dl - dh;
        if (abs(d) < TINY) { // nearly equal within machine precision
            k++; // in the middle we take the higher value
        } else {
            if (dh < dl) {
                k++;
            }
        }
        return k;
    }

}
 