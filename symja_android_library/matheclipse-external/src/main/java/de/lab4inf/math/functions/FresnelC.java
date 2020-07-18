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
 * Fresnel cosine integral C(x), see  A&amp;ST 7.3.1.
 * <pre>
 *            x
 *           /
 *   C(x):= / cos(&pi;/2 t<sup>2</sup>) dt = 1/2 + f(x)*sin(z) - g(x)*cos(z); z=&pi;/2*x<sup>2</sup>
 *         /
 *        0
 * </pre>
 * <p>
 * The approximation is accurate to ~5.E-12.
 *
 * @author nwulff
 * @version $Id: FresnelC.java,v 1.18 2015/01/29 15:13:03 nwulff Exp $
 * @since 09.09.2009
 */
public class FresnelC extends AbstractFresnelIntegrals {
    /**
     * Coefficients from R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967).
     */
    private static final double[] A = {+0.566094879476909, -0.174163078153421, +0.147534155215236, -0.092641852979503,
            +0.097246391833287, -0.105139484620109, +0.066060370528389, -0.025736708168279, +0.006861115379812,
            -0.001341081352431, +0.000201616882443, -0.000024136195791, +0.000002361906788, -0.000000192850090,
            +0.000000013357248, -0.000000000795550, +0.000000000041213, -0.000000000001875, +0.000000000000076,
            -0.000000000000003};

    static {
        // first terms have to be halved
        A[0] /= 2;
        double c1 = 0, c2 = 0;
        for (int i = 0; i < A.length; i++) {
            c1 += A[i];
            c2 += abs(A[i]);
        }
        getLogger().info(format("Fresnel Cosine check sum A[k]=%.15f;  sum |A[k]|=%.15f", c1, c2));
    }

    /**
     * Approximation of the Fresnel cosine integral.
     *
     * @param x the argument
     * @return C(x)
     */
    public static double fresnelC(final double x) {
        double y;
        final double z = abs(x);
        double w = z * z;
        if (z < AK) {
            // y = fresnelCSeries(z);
            double u = w / 9;
            u = 2 * u * u - 1;
            y = ChebyshevExpansion.cheby(u, A) * z;
        } else {
            w *= PIH;
            y = 0.5 + faux(z) * sin(w) - gaux(z) * cos(w);
        }
        if (x < 0) {
            y = -y;
        }
        return y;
    }

    /**
     * Internal series expansion for small x values.
     * Formulae A&amp;ST 7.3.11
     *
     * @param x the argument 0 &lt; x &lt; 1
     * @return C(x)
     */
    public static double fresnelCSeries(final double x) {
        double yo, yn = 1;
        double fac = 1;
        final double x2 = PIH * x * x;
        final double x4 = -x2 * x2;
        double z4 = 1;
        int k = 0;
        do {
            k++;
            fac *= (2.0 * k - 1.0);
            fac *= (2.0 * k);
            yo = yn;
            z4 *= x4;
            yn += z4 / (fac * (4.0 * k + 1.0));
        } while (!hasRelativeConverged(yn, yo));
        yn *= x;
        return yn;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return fresnelC(x[0]);
    }
}
 