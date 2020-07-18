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
import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.Integrable;
import de.lab4inf.math.Interval;
import de.lab4inf.math.sets.IntervalNumber;

import static de.lab4inf.math.functions.Logarithm.ln;
import static de.lab4inf.math.functions.Power.pow;
import static de.lab4inf.math.functions.Sine.sin;

/**
 * CoTangent function wrapper.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: CoTangent.java,v 1.13 2014/06/26 11:25:35 nwulff Exp $
 * @since 22.07.2009
 */
public final class CoTangent extends TrigonometricFunction {
    private static final CoTangent INSTANCE = new CoTangent();
    private static final Dcotan DERIVATIVE = new Dcotan(INSTANCE);
    private static final Icotan ANTIDERIVATIVE = new Icotan(INSTANCE);

    /**
     * Default constructor.
     */
    public CoTangent() {
        super(false);
    }

    /**
     * Calculate the complex cotangent cot(z), which is
     * basically the inverse tangent.
     *
     * @param z the complex argument
     * @return cot(z)
     */
    public static Complex cot(final Complex z) {
        return Tangent.tan(z).invers();
    }

    /**
     * cotangent for real arguments.
     *
     * @param x argument
     * @return cot(x)
     */
    public static double cot(final double x) {
        return 1.0 / Math.tan(x);
    }

    /**
     * Calculate the cotangent for an interval number.
     *
     * @param x the interval
     * @return cot(x) as interval number
     */
    public static Interval cot(final Interval x) {
        double l = cot(x.left());
        double r = cot(x.right());
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
        return ANTIDERIVATIVE;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Function#f(double)
     */
    @Override
    public double f(final double x) {
        return 1.0 / Math.tan(x);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(de.lab4inf.math.sets.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return cot(z);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(de.lab4inf.math.Interval)
     */
    @Override
    public Interval f(final Interval v) {
        return cot(v);
    }

    /**
     * internal derivative of the cotangent.
     */
    private static class Dcotan extends L4MFunction implements CFunction, Integrable {
        private CoTangent anti;

        public Dcotan(final CoTangent cotan) {
            anti = cotan;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Integrable#getAntiderivative()
         */
        public Function getAntiderivative() {
            return anti;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Function#f(double)
         */
        @Override
        public double f(final double... x) {
            double xx = x[0];
            double s = Math.sin(xx);
            return 1.0 / (s * s);
        }


        /* (non-Javadoc)
         * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
         */
        @Override
        public Complex f(final Complex... z) {
            Complex u = z[0];
            Complex v = sin(u);
            return pow(v, -2);
        }
    }

    /**
     * internal anti derivative of the tangent.
     */
    private static class Icotan extends L4MFunction implements CFunction, Differentiable {
        private CoTangent deri;

        public Icotan(final CoTangent cotan) {
            deri = cotan;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Differentiable#getDerivative()
         */
        public Function getDerivative() {
            return deri;
        }

        /* (non-Javadoc)
        * @see de.lab4inf.math.Function#f(double)
        */
        @Override
        public double f(final double... x) {
            double xx = x[0];
            double s = Math.sin(xx);
            return Math.log(s);
        }


        /* (non-Javadoc)
         * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
         */
        @Override
        public Complex f(final Complex... z) {
            Complex u = z[0];
            Complex v = sin(u);
            return ln(v);
        }
    }
}
 