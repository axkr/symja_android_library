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

import de.lab4inf.math.CDifferentiable;
import de.lab4inf.math.CFunction;
import de.lab4inf.math.CIntegrable;
import de.lab4inf.math.Complex;
import de.lab4inf.math.Interval;
import de.lab4inf.math.sets.ComplexNumber;
import de.lab4inf.math.sets.IntervalNumber;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sinh;
import static java.lang.Math.tanh;

/**
 * Tangent function for real and complex arguments.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Tangent.java,v 1.19 2014/06/26 11:25:35 nwulff Exp $
 * @since 22.12.2008
 */
public final class Tangent extends TrigonometricFunction {
    static final Dtan DERIVATIVE = new Dtan();
    static final Itan PRIMITIVE = new Itan();
    static volatile Tangent instance;

    /**
     * Default constructor.
     */
    public Tangent() {
        super(false);
        if (instance == null) {
            instance = this;
        }
    }

    /**
     * Complex tangent for real argument.
     *
     * @param x the argument
     * @return tan(x)
     */
    public static ComplexNumber tan(final double x) {
        return new ComplexNumber(Math.tan(x));
    }

    /**
     * Calculate the complex tangent  tan(z), basically A&ST 4.3.57.
     *
     * @param z the complex argument
     * @return tan(z)
     */
    public static ComplexNumber tan(final Complex z) {
        double c, s, t, d, u;
        double r = z.real();
        double i = z.imag();
        double x, y;
        if (!z.isComplex()) {
            x = Math.tan(r);
            y = 0;
        } else {
            if (abs(i) < 1) {
                // A&ST 4.3.57 with 4.3.25 and 4.5.32
                c = cos(r);
                s = sinh(i);
                d = 2 * (c * c + s * s);
                x = sin(2 * r) / d;
                y = sinh(2 * i) / d;
            } else {
                t = 1.0 / tanh(i);
                u = exp(-i);
                u = 2 * u / (1 - u * u);
                s = u * u;
                c = cos(r);
                d = 1 + c * c * s;
                x = 0.5 * sin(2 * r) * s / d;
                y = t / d;
            }
        }
        return new ComplexNumber(x, y);
    }

    /**
     * Calculate the tangent for an interval number.
     *
     * @param x the interval
     * @return tan(x) as interval number
     */
    public static Interval tan(final Interval x) {
        double l = Math.tan(x.left());
        double r = Math.tan(x.right());
        return new IntervalNumber(l, r);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#createDerivative()
     */
    @Override
    protected CFunction createDerivative() {
        return DERIVATIVE;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#createAntiDerivative()
     */
    @Override
    protected CFunction createAntiDerivative() {
        return PRIMITIVE;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Function#f(double)
     */
    @Override
    public double f(final double x) {
        return Math.tan(x);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(de.lab4inf.math.sets.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return tan(z);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(de.lab4inf.math.Interval)
     */
    @Override
    public Interval f(final Interval v) {
        return tan(v);
    }

    /**
     * internal derivative of the tangent.
     */
    static class Dtan extends L4MFunction implements CFunction, CIntegrable {

        /* (non-Javadoc)
         * @see de.lab4inf.math.Function#f(double)
         */
        @Override
        public double f(final double... x) {
            double xx = x[0];
            double c = cos(xx);
            return 1.0 / (c * c);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
         */
        @Override
        public Complex f(final Complex... z) {
            Complex w = z[0];
            Complex u = Cosine.cos(w);
            return Power.pow(u, -2);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Integrable#getAntiderivative()
         */
        public CFunction getAntiderivative() {
            return instance;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.CIntegrable#getCAntiderivative()
         */
        @Override
        public CFunction getCAntiderivative() {
            return instance;
        }

    }

    /**
     * internal anti derivative of the tangent.
     */
    static class Itan extends L4MFunction implements CFunction, CDifferentiable {

        /* (non-Javadoc)
         * @see de.lab4inf.math.Function#f(double)
         */
        @Override
        public double f(final double... x) {
            double xx = x[0];
            double c = cos(xx);
            return -log(c);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
         */
        @Override
        public Complex f(final Complex... z) {
            Complex w = z[0];
            Complex u = Cosine.cos(w);
            return Logarithm.ln(u).multiply(-1);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Differentiable#getDerivative()
         */
        public CFunction getDerivative() {
            return instance;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.CDifferentiable#getCDerivative()
         */
        @Override
        public CFunction getCDerivative() {
            return instance;
        }
    }
}
 