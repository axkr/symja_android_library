/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2010,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.functions;

import de.lab4inf.math.CFunction;
import de.lab4inf.math.Complex;
import de.lab4inf.math.IFunction;
import de.lab4inf.math.Interval;
import de.lab4inf.math.sets.ComplexNumber;
import de.lab4inf.math.sets.IntervalNumber;

/**
 * Real and complex power function z<sup>a</sup>.
 *
 * @author nwulff
 * @version $Id: Power.java,v 1.6 2015/01/29 15:13:03 nwulff Exp $
 * @since 14.11.2010
 */

public final class Power extends L4MFunction implements CFunction, IFunction {

    /**
     * Calculate the complex power a^b.
     *
     * @param a the basis
     * @param b the exponent
     * @return a^b
     */
    public static Complex pow(final Complex a, final Complex b) {
        Complex z;
        if (a.isZero()) {
            if (b.isZero()) {
                z = new ComplexNumber(1.0, 0.0);
            } else {
                z = new ComplexNumber(0.0, 0.0);
            }
        } else if (b.isOne()) {
            z = a;
        } else if (b.real() == -1.0 && b.imag() == 0.0) {
            z = a.invers();
        } else {
            double logr = Math.log(a.abs2()) / 2;
            double t = a.rad();
            double br = b.real(), bi = b.imag();

            double r = Math.exp(logr * br - bi * t);
            double beta = t * br + bi * logr;

            z = new ComplexNumber(r * Math.cos(beta), r * Math.sin(beta));
        }
        return z;
    }

    /**
     * Calculate the complex power a^b.
     *
     * @param a the basis
     * @param b the exponent
     * @return a^b
     */
    public static Complex pow(final Complex a, final double b) {
        return pow(a, new ComplexNumber(b, 0));
    }

    /**
     * Calculate the complex power a^b.
     *
     * @param a the basis
     * @param b the exponent
     * @return a^b
     */
    public static Complex pow(final double a, final Complex b) {
        return pow(new ComplexNumber(a, 0), b);
    }

    /**
     * Calculate the real power a^b.
     *
     * @param a the basis
     * @param b the exponent
     * @return a^b
     */
    public static double pow(final double a, final double b) {
        return Math.pow(a, b);
    }

    /**
     * Power of two intervals.
     *
     * @param a first argument
     * @param b second argument
     * @return a**b
     */
    public static Interval pow(final Interval a, final Interval b) {
        return a.pow(b);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return Math.pow(x[0], x[1]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
     */
    @Override
    public Complex f(final Complex... z) {
        return pow(z[0], z[1]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.IFunction#f(de.lab4inf.math.Interval[])
     */
    @Override
    public Interval f(final Interval... v) {
        return pow(v[0], v[1]);
    }

    public Interval f(final Interval x, final Interval y) {
        return pow(x, y);
    }

    public Interval f(final Interval x, final double y) {
        return x.pow(y);
    }

    public Interval f(final double x, final Interval y) {
        return IntervalNumber.pow(x, y);
    }

    public Complex f(final Complex x, final Complex y) {
        return pow(x, y);
    }

    public Complex f(final Complex x, final double y) {
        return pow(x, y);
    }

    public Complex f(final double x, final Complex y) {
        return pow(new ComplexNumber(x), y);
    }

    public Complex f(final double x, final double y) {
        return new ComplexNumber(Math.pow(x, y));
    }

}
 