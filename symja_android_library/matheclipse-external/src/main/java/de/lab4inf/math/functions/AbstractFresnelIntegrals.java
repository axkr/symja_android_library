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
import static java.lang.Math.abs;

/**
 * Base class for the two Fresnel integrals.
 * <pre>
 *            x
 *           /
 *   C(x) = / cos(&pi;/2 t<sup>2</sup>) dt = 1/2 + f(x)*sin(z) - g(x)*cos(z)
 *         /
 *        0
 * </pre>
 * <p>
 * and
 * <p>
 * <pre>
 *            x
 *           /
 *   S(x) = / sin(&pi;/2 t<sup>2</sup>) dt = 1/2 - f(x)*cos(z) - g(x)*sin(z)
 *         /
 *        0
 * </pre>
 * as defined in  A&amp;ST 7.3.1 and 7.3.2, with z = &pi;/2*x<sup>2</sup>.
 * <hr/>
 * Used for the calculation of the integrals is a Chebyshev approximation from
 * <pre> R. Bulirsch, Numerische Mathematik 9, pp 380-385 1967 </pre>, with an
 * accuracy of about ~5.E-12.
 * <hr/>
 *
 * @author nwulff
 * @version $Id: AbstractFresnelIntegrals.java,v 1.17 2015/01/29 15:13:03 nwulff Exp $
 * @since 09.09.2009
 */
abstract class AbstractFresnelIntegrals extends L4MFunction {
    protected static final double EPS = 1.E-16;
    protected static final double PIH = PI / 2;
    /**
     * Turning point between the large and small expansion.
     */
    protected static final double AK = 3;
    /**
     * Coefficients  for large values of f(x).
     * From R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967)
     **/
    private static final double[] F = {+0.635461098412986, -0.000573621372272, +0.000005571891859, -0.000000137398906,
            +0.000000005908966, -0.000000000370990, +0.000000000030716, -0.000000000003145, +0.000000000000381,
            -0.000000000000053, +0.000000000000008, -0.000000000000001};
    /**
     * Coefficients  for large values of g(x).
     * From R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967)
     **/
    private static final double[] G = {+0.022315579858535, -0.000098395902454, +0.000001663169852, -0.000000056963997,
            +0.000000003074598, -0.000000000228879, +0.000000000021678, -0.000000000002478, +0.000000000000330,
            -0.000000000000050, +0.000000000000008, -0.000000000000002};

    static {
        // first terms have to be halved
        F[0] /= 2;
        G[0] /= 2;
    }

    /**
     * Hidden constructor.
     */
    protected AbstractFresnelIntegrals() {
    }

    /**
     * Signal if the two values x and y have a difference less then EPS.
     *
     * @param x first argument
     * @param y second argument
     * @return boolean indicating if x and y converge
     */
    protected static boolean hasRelativeConverged(final double x, final double y) {
        final double dx = abs(x - y);
        final double sx = abs(x + y) / 2;
        if (sx != 0) {
            return dx / sx < EPS;
        }
        return dx < EPS;
    }

    /**
     * Rational approximation  A&amp;ST 7.3.33 for g.
     * The error is 2.E-3
     *
     * @param x argument to be positive
     * @return g(x) approximation
     */
    public static double gSimple(final double x) {
        final double z = abs(x);
        return 1.0 / (2 + 4.142 * z + 3.492 * z * z + 6.67 * z * z * z);
    }

    /**
     * Rational approximation  A&amp;ST 7.3.32 for f.
     * The error is 2.E-3
     *
     * @param x argument to be positive
     * @return f(x) approximation
     */
    public static double fSimple(final double x) {
        final double z = abs(x);
        return (1 + 0.926 * z) / (2 + 1.792 * z + 3.104 * z * z);
    }

    /**
     * Chebyshev approximation for g, from R. Burlish, Numerische Mathematik 9, (1967).
     *
     * @param x argument to be greater then AK=3.
     * @return g(x) approximation
     */
    public static double gaux(final double x) {
        if (x < AK) {
            return gSimple(x);
        }
        final double z = 9 / (x * x);
        final double u = 2 * z * z - 1;
        return ChebyshevExpansion.cheby(u, G) * z / x;
    }

    /**
     * Chebyshev approximation for f, from R. Burlish, Numerische Mathematik 9, (1967).
     *
     * @param x argument to be greater then AK=3.
     * @return f(x) approximation
     */
    public static double faux(final double x) {
        if (x < AK) {
            return fSimple(x);
        }
        final double z = 9 / (x * x);
        final double u = 2 * z * z - 1;
        return ChebyshevExpansion.cheby(u, F) / x;
    }
}
 