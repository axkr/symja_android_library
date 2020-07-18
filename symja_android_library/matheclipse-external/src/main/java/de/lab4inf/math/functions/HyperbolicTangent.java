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
import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.Integrable;
import de.lab4inf.math.sets.ComplexNumber;

import static de.lab4inf.math.functions.HyperbolicCosine.cosh;
import static de.lab4inf.math.functions.Logarithm.ln;
import static de.lab4inf.math.functions.Power.pow;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Complex and real hyperbolic tangent implementation tanh(z).
 *
 * @author nwulff
 * @version $Id: HyperbolicTangent.java,v 1.8 2015/01/29 15:13:03 nwulff Exp $
 * @since 14.11.2010
 */

public class HyperbolicTangent extends HyperbolicFunction {
    public static final HyperbolicTangent FCT = new HyperbolicTangent();
    private static final Dtanh DERIVATIVE = new Dtanh(FCT);
    private static final Itanh ANTIDERIVATIVE = new Itanh(FCT);

    /**
     * Calculate tanh for real argument.
     *
     * @param x real argument
     * @return tanh(x)
     */
    public static double tanh(final double x) {
        return Math.tanh(x);
    }

    /**
     * Calculate the complex hyperbolic tangent tanh(z).
     *
     * @param z the complex argument
     * @return tanh(z)
     */
    public static ComplexNumber tanh(final Complex z) {
        double x = z.real();
        double y = z.imag();
        // double a;
        if (!z.isComplex()) {
            x = Math.tanh(x);
            y = 0;
        } else {
            double s2y = sin(2 * y);
            double c2y = cos(2 * y);
            double th2x = tanh(2 * x);
            double ch2x = cosh(2 * x);
            // if (abs(x)<1) {
            // a = ch2x + c2y;
            // x = sinh(2*x)/a;
            // y = s2y/a;
            // } else {
            x = th2x / (1 + c2y / ch2x);
            y = s2y / (ch2x + c2y);
            // }
        }
        return new ComplexNumber(x, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.HyperbolicFunction#f(double)
     */
    @Override
    public double f(final double x) {
        return Math.tanh(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.HyperbolicFunction#createDerivative()
     */
    @Override
    protected CFunction createDerivative() {
        return DERIVATIVE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.HyperbolicFunction#createAntiDerivative()
     */
    @Override
    protected CFunction createAntiDerivative() {
        return ANTIDERIVATIVE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.HyperbolicFunction#f(de.lab4inf.math.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return tanh(z);
    }

    /**
     * internal derivative of the tanh.
     */
    private static class Dtanh extends L4MFunction implements CFunction, Integrable {
        private final HyperbolicTangent anti;

        public Dtanh(final HyperbolicTangent tanh) {
            anti = tanh;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Integrable#getAntiderivative()
         */
        public Function getAntiderivative() {
            return anti;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double)
         */
        @Override
        public double f(final double... x) {
            double xx = x[0];
            double s = Math.cosh(xx);
            return 1.0 / (s * s);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
         */
        @Override
        public Complex f(final Complex... z) {
            Complex u = z[0];
            Complex v = cosh(u);
            return pow(v, -2);
        }
    }

    /**
     * internal anti derivative of the tanh.
     */
    private static class Itanh extends L4MFunction implements CFunction, Differentiable {
        private final HyperbolicTangent deri;

        public Itanh(final HyperbolicTangent tanh) {
            deri = tanh;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Differentiable#getDerivative()
         */
        public Function getDerivative() {
            return deri;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double)
         */
        @Override
        public double f(final double... x) {
            double xx = x[0];
            double s = Math.cosh(xx);
            return Math.log(s);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
         */
        @Override
        public Complex f(final Complex... z) {
            Complex u = z[0];
            Complex v = cosh(u);
            return ln(v);
        }
    }
}
 