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
package de.lab4inf.math.functions;

import de.lab4inf.math.CFunction;
import de.lab4inf.math.Complex;
import de.lab4inf.math.Interval;
import de.lab4inf.math.sets.ComplexNumber;
import de.lab4inf.math.sets.IntervalNumber;

/**
 * Sine function for real and complex arguments.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Sine.java,v 1.15 2011/10/16 15:46:03 nwulff Exp $
 * @since 03.02.2005
 */
public class Sine extends TrigonometricFunction {
    /**
     * Default constructor.
     */
    public Sine() {
        this(false);
    }

    /**
     * Constructor indicating if -sin(x)should be returned.
     * Used derivative of the cosine function.
     *
     * @param negative sign flag
     */
    Sine(final boolean negative) {
        super(negative);
    }

    /**
     * Calculate the complex sine  sin(z).
     *
     * @param z the complex argument
     * @return sin(z)
     */
    public static ComplexNumber sin(final Complex z) {
        double r = z.real();
        double i = z.imag();
        double x, y;
        if (!z.isComplex()) {
            x = Math.sin(r);
            y = 0;
        } else {
            x = Math.sin(r) * Math.cosh(i);
            y = Math.cos(r) * Math.sinh(i);
        }
        return new ComplexNumber(x, y);
    }

    /**
     * Calculate the complex sine  sin(z) for real argument.
     *
     * @param z the real argument
     * @return sin(z)
     */
    public static ComplexNumber csin(final double z) {
        return new ComplexNumber(Math.sin(z));
    }

    /**
     * Calculate the sine for an interval number.
     *
     * @param x the interval
     * @return sin(x) as interval number
     */
    public static Interval sin(final Interval x) {
        double l = Math.sin(x.left());
        double r = Math.sin(x.right());
        return new IntervalNumber(l, r);
    }

    /**
     * Calculate the real sine  sin(x).
     *
     * @param x the real argument
     * @return sin(x)
     */
    public static double sin(final double x) {
        return Math.sin(x);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(double)
     */
    @Override
    public double f(final double x) {
        if (negative) {
            return -Math.sin(x);
        }
        return Math.sin(x);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#createDerivative()
     */
    @Override
    protected final CFunction createDerivative() {
        return new Cosine(negative);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#createAntiDerivative()
     */
    @Override
    protected final CFunction createAntiDerivative() {
        return new Cosine(!negative);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(de.lab4inf.math.sets.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        if (negative) {
            return sin(z).multiply(-1);
        }
        return sin(z);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(de.lab4inf.math.Interval)
     */
    @Override
    public Interval f(final Interval v) {
        return sin(v);
    }

}
 