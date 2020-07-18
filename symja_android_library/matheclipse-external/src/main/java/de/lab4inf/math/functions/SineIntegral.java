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

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.util.ChebyshevExpansion;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.String.format;

/**
 * Sine Integral approximation Si(x) via Chebyshev expansion.
 * <pre>
 *             x
 *            /
 *   Si(x) = / sin(t)/t dt = &pi;/2 - f(x)*sin(x) - g(x)*sin(x)
 *          /
 *         0
 * </pre>
 * The approximation is from:
 * <pre>
 * R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967).
 * </pre>
 * with an accuracy of ~5.E-12.
 *
 * @author nwulff
 * @version $Id: SineIntegral.java,v 1.13 2015/01/29 15:13:03 nwulff Exp $
 * @since 18.09.2009
 */
public class SineIntegral extends AbstractSiCiIntegrals implements Differentiable {
    /**
     * accuracy for series expansion.
     */
    private static final double EPS = 5.E-13;
    /**
     * internal faculty cache.
     */
    private static final double[] FACULTY = new double[16];
    /**
     * Coefficients from R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967).
     */
    private static final double[] A = {+8.105852955361245, -4.063980844911986, +2.778756381742663, -1.926565091150656,
            +1.389308771171888, -0.968322236987086, +0.530148847916522, -0.211263780976555, +0.062033679432003,
            -0.013867445589417, +0.002436221404749, -0.000345469155569, +0.000040420271419, -0.000003972908746,
            +0.000000332988589, -0.000000024100076, +0.000000001522370, -0.000000000084710, +0.000000000004185,
            -0.000000000000185, +0.000000000000007};

    static {
        final int n = FACULTY.length;
        FACULTY[0] = 1;
        for (int i = 1; i < n; i++) {
            FACULTY[i] = i * FACULTY[i - 1];
        }
        // first terms have to be halved
        A[0] /= 2;
        double c1 = 0, c2 = 0;
        for (int i = 0; i < A.length; i++) {
            c1 += A[i];
            c2 += abs(A[i]);
        }
        getLogger().info(format("Sine integral check sum A[k]=%.15f; sum |A[k]|=%.15f", c1, c2));
    }

    /**
     * Si kernal to use.
     */
    private Sinc kernel = null;

    /**
     * Approximation of the sine integral.
     *
     * @param x the argument
     * @return Si(x)
     */
    public static double si(final double x) {
        double y;
        final double z = abs(x);
        if (z < AK) {
            final double w = z / AK;
            final double u = 2 * w * w - 1;
            y = ChebyshevExpansion.cheby(u, A) * w;
        } else {
            y = PIH - auxf(z) * cos(z) - auxg(z) * sin(z);
        }
        if (x < 0) {
            y = -y;
        }
        return y;
    }

    /**
     * Calculation of (cached) faculty x!.
     *
     * @param x value
     * @return x!
     */
    private static double facul(final int x) {
        double f = 1;
        final int n = FACULTY.length - 1;
        if (x <= n) {
            return FACULTY[x];
        }
        getLogger().info(format("%d! not cached", x));
        f = FACULTY[n];
        for (int i = n + 1; i <= x; i++) {
            f *= i;
        }
        return f;
    }

    /**
     * series expansion for |x| &lt; 1 with accuracy ~1.E-13.
     *
     * @param x the argument
     * @return Si(x) for small x
     */
    public static double expandSi(final double x) {
        double delta = 1;
        double sum = 0, xn = x;
        final double x2 = x * x;
        int sign = 1;
        int nn, n = 0;
        double fac;
        do {
            nn = 2 * n + 1;
            fac = nn * facul(nn);
            delta = xn / fac;
            sum += sign * delta;
            sign = -sign;
            xn *= x2;
            n++;
        } while (abs(delta) > EPS && n < 13);
        // logger.info(String.format("x:%.4f max %d*%d! = %d ",x , nn,nn,fac));
        return sum;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return si(x[0]);
    }

    /**
     * Get the value of the derivative a point x.
     *
     * @param x point to evaluate
     * @return f'(x)
     */
    public double dF(final double x) {
        return getDerivative().f(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public Function getDerivative() {
        if (kernel == null) {
            kernel = new Sinc();
        }
        return kernel;
    }
}
 