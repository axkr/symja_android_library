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

import de.lab4inf.math.CDifferentiable;
import de.lab4inf.math.CFunction;
import de.lab4inf.math.CIntegrable;
import de.lab4inf.math.Complex;
import de.lab4inf.math.IDifferentiable;
import de.lab4inf.math.IFunction;
import de.lab4inf.math.IIntegrable;
import de.lab4inf.math.Interval;
import de.lab4inf.math.sets.ComplexNumber;
import de.lab4inf.math.sets.IntervalNumber;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.log;
import static java.lang.Math.log1p;

/**
 * Real and complex logarithm.
 *
 * @author nwulff
 * @version $Id: Logarithm.java,v 1.8 2014/06/17 09:38:22 nwulff Exp $
 * @since 14.11.2010
 */

public class Logarithm extends L4MFunction implements CDifferentiable, CIntegrable, IDifferentiable, IIntegrable {
    public static final double LOG10 = Math.log(10);
    public static final double LOG2 = Math.log(2);
    private static final DLog DERIVATIVE = new DLog();
    private static final ILog ANTIDERIVATIVE = new ILog();

    /**
     * Calculate the real natural logarithm.
     *
     * @param x argument
     * @return logE(x)
     */
    public static double ln(final double x) {
        return log(x);
    }

    /**
     * Calculate the real logarithm for basis 10.
     *
     * @param x argument
     * @return log10(x)
     */
    public static double log10(final double x) {
        return Math.log10(x);
    }

    /**
     * Calculate the real logarithm for basis 2.
     *
     * @param x argument
     * @return log2(x)
     */
    public static double log2(final double x) {
        return ln(x) / LOG2;
    }

    /**
     * Calculate the complex logarithm ln(z).
     *
     * @param z the complex argument not zero
     * @return ln(z)
     */
    public static ComplexNumber ln(final Complex z) {
        double x, y = 0;
        double a = abs(z.real()), b = abs(z.imag());
        if (a < b) {
            x = a;
            a = b;
            b = x;
        }
        if ((a < 0.5) || (a > 1.414)) {
            x = log(a * a + b * b) / 2;
        } else {
            x = log1p((a - 1) * (a + 1) + b * b) / 2;
        }
        y = z.rad();
        if (y > PI) {
            y = -(y % PI);
        }
        return new ComplexNumber(x, y);
    }

    /**
     * Calculate the natural interval logarithm ln(v).
     *
     * @param v the interval argument not zero
     * @return ln(v)
     */
    public static Interval ln(final Interval v) {
        double a = ln(v.left());
        double b = ln(v.right());
        return new IntervalNumber(a, b);
    }

    /**
     * Calculate the interval logarithm log10(v) to basis ten.
     *
     * @param v the interval argument not zero
     * @return log10(v)
     */
    public static Interval log10(final Interval v) {
        double a = ln(v.left()) / LOG10;
        double b = ln(v.right()) / LOG10;
        return new IntervalNumber(a, b);
    }

    /**
     * Calculate the interval logarithm log2(v) to basis two.
     *
     * @param v the interval argument not zero
     * @return log2(v)
     */
    public static Interval log2(final Interval v) {
        double a = ln(v.left()) / LOG2;
        double b = ln(v.right()) / LOG2;
        return new IntervalNumber(a, b);
    }

    /**
     * Calculate the complex logarithm log10(z) to basis 10.
     *
     * @param z the complex argument not zero
     * @return log10(z)
     */
    public static ComplexNumber log10(final Complex z) {
        ComplexNumber ln = ln(z);
        ln = ln.div(LOG10);
        return ln;
    }

    /**
     * Calculate the complex logarithm log2(z) to basis 2.
     *
     * @param z the complex argument not zero
     * @return log2(z)
     */
    public static ComplexNumber log2(final Complex z) {
        ComplexNumber ln = ln(z);
        ln = ln.div(LOG2);
        return ln;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CDifferentiable#getDerivative()
     */
    @Override
    public CFunction getDerivative() {
        return DERIVATIVE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.IDifferentiable#getIDerivative()
     */
    @Override
    public IFunction getIDerivative() {
        return DERIVATIVE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CDifferentiable#getCDerivative()
     */
    @Override
    public CFunction getCDerivative() {
        return DERIVATIVE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CIntegrable#getAntiderivative()
     */
    @Override
    public CFunction getAntiderivative() {
        return ANTIDERIVATIVE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.IIntegrable#getIAntiderivative()
     */
    @Override
    public IFunction getIAntiderivative() {
        return ANTIDERIVATIVE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CIntegrable#getCAntiderivative()
     */
    @Override
    public CFunction getCAntiderivative() {
        return ANTIDERIVATIVE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return log(x[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
     */
    @Override
    public Complex f(final Complex... z) {
        return ln(z[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.IFunction#f(de.lab4inf.math.Interval[])
     */
    @Override
    public Interval f(final Interval... v) {
        return ln(v[0]);
    }

    static class DLog extends L4MFunction implements CFunction, IFunction {
        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... z) {
            double x = z[0];
            if (x == 0)
                return Double.NEGATIVE_INFINITY;
            else if (x < 0)
                return Double.NaN;
            return 1.0 / x;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
         */
        @Override
        public Complex f(final Complex... z) {
            return z[0].invers();
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.IFunction#f(de.lab4inf.math.Interval[])
         */
        @Override
        public Interval f(final Interval... v) {
            return IntervalNumber.div(1, v[0]);
        }
    }

    static class ILog extends L4MFunction implements CFunction, IFunction {
        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... z) {
            double x = z[0];
            if (x == 0)
                return Double.NEGATIVE_INFINITY;
            else if (x < 0)
                return Double.NaN;
            return x * log(x) - x;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
         */
        @Override
        public Complex f(final Complex... z) {
            Complex x = z[0];
            return x.multiply(ln(x)).minus(x);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.IFunction#f(de.lab4inf.math.Interval[])
         */
        @Override
        public Interval f(final Interval... v) {
            Interval x = v[0];
            double l = f(x.left());
            double r = f(x.right());
            return new IntervalNumber(l, r);
        }
    }
}
 