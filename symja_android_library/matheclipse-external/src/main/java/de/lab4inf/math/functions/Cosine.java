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
 * Cosine function for real and complex arguments.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Cosine.java,v 1.14 2011/09/12 14:49:20 nwulff Exp $
 * @since 03.02.2005
 */
public class Cosine extends TrigonometricFunction {

    /**
     * Default constructor.
     */
    public Cosine() {
        this(false);
    }

    /**
     * Constructor for the negative cosine used
     * for derivatives.
     *
     * @param negative
     */
    Cosine(final boolean negative) {
        super(negative);

    }

    /**
     * Calculate the complex cosine cos(z).
     *
     * @param z the complex argument
     * @return cos(z)
     */
    public static ComplexNumber cos(final Complex z) {
        double r = z.real();
        double i = z.imag();
        double x, y;
        if (!z.isComplex()) {
            x = Math.cos(r);
            y = 0;
        } else {
            x = Math.cos(r) * Math.cosh(i);
            y = Math.sin(r) * Math.sinh(-i);
        }
        return new ComplexNumber(x, y);
    }

    /**
     * Calculate the real cosine cos(z) for real argument.
     *
     * @param z the real argument
     * @return cos(z)
     */
    public static double cos(final double z) {
        return Math.cos(z);
    }

    /**
     * Calculate the cosine for an interval number.
     *
     * @param x the interval
     * @return cos(x) as interval number
     */
    public static Interval cos(final Interval x) {
        double l = Math.cos(x.left());
        double r = Math.cos(x.right());
        return new IntervalNumber(l, r);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#createDerivative()
     */
    @Override
    protected final CFunction createDerivative() {
        return new Sine(!negative);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#createAntiDerivative()
     */
    @Override
    protected final CFunction createAntiDerivative() {
        return new Sine(negative);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(double)
     */
    @Override
    public double f(final double x) {
        if (negative) {
            return -Math.cos(x);
        }
        return Math.cos(x);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(de.lab4inf.math.sets.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        if (negative) {
            return cos(z).multiply(-1);
        }
        return cos(z);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(de.lab4inf.math.Interval)
    */
    @Override
    public Interval f(final Interval v) {
        return cos(v);
    }

}
 