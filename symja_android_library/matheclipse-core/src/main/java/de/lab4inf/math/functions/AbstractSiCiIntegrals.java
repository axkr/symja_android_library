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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Base class for the two Sine and Cosine integrals.
 * <pre>
 *             x
 *            /
 *   Si(x) = / sin(t)/t dt = &pi;/2 - f(x)*sin(x) - g(x)*sin(x)
 *          /
 *         0
 * </pre>
 * and
 * <pre>
 *                        x
 *                       /
 *  Ci(x) = &gamma; + ln|x| + / [cos(t)-1]/t dt = f(x)*sin(x) - g(x)*cos(x)
 *                     /
 *                    0
 * </pre>
 * as defined in  A&amp;ST 5.2.1 and 5.2.2.
 * <hr/>
 * Used for the calculation of the integrals is a Chebyshev approximation from
 * <pre> R. Bulirsch, Numerische Mathematik 9, pp 380-385 1967 </pre>, with an
 * accuracy of about ~5.E-12.
 * <hr/>
 *
 * @author nwulff
 * @version $Id: AbstractSiCiIntegrals.java,v 1.11 2015/01/29 15:13:03 nwulff Exp $
 * @since 18.09.2009
 */
abstract class AbstractSiCiIntegrals extends L4MFunction {
    protected static final double PIH = PI / 2;
    /**
     * Turning point between the large and small expansion.
     */
    protected static final double AK = 16;
    /**
     * Coefficients for large values of f(x).
     * From R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967)
     **/
    private static final double[] F = {+0.124527458057854, -0.000233756041393, +0.000002453755677, -0.000000058670317,
            +0.000000002356196, -0.000000000136096, +0.000000000010308, -0.000000000000964, +0.000000000000107,
            -0.000000000000014, +0.000000000000002};
    /**
     * Coefficients for large values of g(x).
     * From R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967)
     **/
    private static final double[] G = {+0.007725712193407, -0.000042644182622, +0.000000724995950, -0.000000023468225,
            +0.000000001169202, -0.000000000079604, +0.000000000006875, -0.000000000000717, +0.000000000000087,
            -0.000000000000012, +0.000000000000002};

    static {
        // first terms have to be halved
        F[0] /= 2;
        G[0] /= 2;
    }

    /**
     * Hidden constructor.
     */
    protected AbstractSiCiIntegrals() {
    }

    /**
     * Approximation for 1 < x < infinity with accuracy better than 5E-7,
     * formula 5.2.38 Abramowitz-Stegun.
     */
    private static double fSimple(final double x) {
        final double[] a = {38.027264, 265.187033, 335.677320, 038.102495};
        final double[] b = {40.021433, 322.624911, 570.236280, 157.105423};
        final int n = a.length;
        final double z = x * x;
        double u = 1, d = 1;
        for (int i = 0; i < n; i++) {
            u = a[i] + u * z;
            d = b[i] + d * z;
        }
        return u / (x * d);
    }

    /**
     * Approximation for 1 < x < infinity with accuracy better than 3E-7,
     * formula 5.2.39 Abramowitz-Stegun.
     */
    private static double gSimple(final double x) {
        final double[] a = {42.242855, 302.757865, 352.018498, 021.821899};
        final double[] b = {48.196927, 482.485984, 1114.978885, 449.690326};
        final int n = a.length;
        final double z = x * x;
        double u = 1, d = 1;
        for (int i = 0; i < n; i++) {
            u = a[i] + u * z;
            d = b[i] + d * z;
        }
        return u / (x * x * d);
    }

    /**
     * Chebyshev approximation for g, from R. Burlish, Numerische Mathematik 9, (1967).
     *
     * @param x argument to be greater then AK=16.
     * @return g(x) approximation
     */
    public static double auxg(final double x) {
        if (x < AK) {
            if (x < 1) {
                return -CosineIntegral.ci(x) * cos(x) - (SineIntegral.si(x) - PIH) * sin(x);
            }
            return gSimple(x);
        }
        final double z = AK / x;
        final double g = z * z;
        final double b = g * 2 - 1;
        return g * ChebyshevExpansion.cheby(b, G);
    }

    /**
     * Chebyshev approximation for f, from R. Burlish, Numerische Mathematik 9, (1967).
     *
     * @param x argument to be greater then AK=16.
     * @return f(x) approximation
     */
    public static double auxf(final double x) {
        if (x < AK) {
            if (x < 1) {
                return CosineIntegral.ci(x) * sin(x) - (SineIntegral.si(x) - PIH) * cos(x);
            }
            return fSimple(x);
        }
        final double z = AK / x;
        final double g = z * z;
        final double b = g * 2 - 1;
        return z * ChebyshevExpansion.cheby(b, F);
    }
}
 