/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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

import de.lab4inf.math.util.ChebyshevExpansion;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.String.format;

/**
 * Fresnel sine integral S(x), see  A&amp;ST 7.3.2.
 * <pre>
 *            x
 *           /
 *   S(x):= / sin(&pi;/2 t<sup>2</sup>) dt = 1/2 - f(x)*cos(z) - g(x)*sin(z); z=&pi;/2*x<sup>2</sup>
 *         /
 *        0
 *
 * </pre>
 * <p>
 * The approximation is accurate to ~5.E-12.
 *
 * @author nwulff
 * @version $Id: FresnelS.java,v 1.19 2015/01/29 15:13:03 nwulff Exp $
 * @since 09.09.2009
 */
public class FresnelS extends AbstractFresnelIntegrals {
    /**
     * Coefficients from R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967).
     */
    private static final double[] A = {+1.734174339031447, -1.247697507291387, +0.926493976989515, -0.688881695298469,
            +0.515461606559411, -0.300398786877130, +0.122191066602012, -0.035248288029314, +0.007517763479240,
            -0.001232314420465, +0.000160243443651, -0.000016954178157, +0.000001489719660, -0.000000110548467,
            +0.000000007025677, -0.000000000386931, +0.000000000018654, -0.000000000000794, +0.000000000000030,
            -0.000000000000001};

    static {
        // first terms have to be halved
        A[0] /= 2;
        double c1 = 0, c2 = 0;
        for (int i = 0; i < A.length; i++) {
            c1 += A[i];
            c2 += abs(A[i]);
        }
        c2 /= 3 * Math.PI / 2;
        getLogger().info(format("Fresnel Sine check sum A[k]=%.15f; sum |A[k]|=%.15f", c1, c2));
    }

    /**
     * Approximation of the Fresnel sine integral.
     *
     * @param x the argument
     * @return S(x)
     */
    public static double fresnelS(final double x) {
        double y;
        final double z = abs(x);
        double w = z * z;
        if (z <= AK) {
            // y = fresnelSSeries(z);
            double u = w / 9;
            u = 2 * u * u - 1;
            y = ChebyshevExpansion.cheby(u, A) * z * w / 9;
        } else {
            w *= PIH;
            y = 0.5 - faux(z) * cos(w) - gaux(z) * sin(w);
        }
        if (x < 0) {
            y = -y;
        }
        return y;
    }

    /**
     * Internal series expansion for small x values.
     * Formulae A&amp;ST 7.3.13
     *
     * @param x the argument 0 &lt; x &lt; 1
     * @return S(x)
     */
    public static double fresnelSSeries(final double x) {
        double yo, yn = 1.0 / 3.0;
        double fac = 1;
        final double x2 = PIH * x * x;
        final double x4 = -x2 * x2;
        double z4 = 1;
        int k = 0;
        do {
            k++;
            fac *= (2.0 * k);
            fac *= (2.0 * k + 1.0);
            yo = yn;
            z4 *= x4;
            yn += z4 / (fac * (4.0 * k + 3.0));
        } while (abs(yn - yo) > abs(yo) * EPS);
        yn *= x * x2;
        return yn;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return fresnelS(x[0]);
    }

}
 