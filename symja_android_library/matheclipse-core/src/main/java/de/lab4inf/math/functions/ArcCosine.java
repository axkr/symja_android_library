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

import de.lab4inf.math.Complex;
import de.lab4inf.math.sets.ComplexNumber;

import static de.lab4inf.math.functions.ArcHyperbolicCosine.acosh;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.log1p;
import static java.lang.Math.sqrt;

/**
 * Real and complex arc cosine function.
 *
 * @author nwulff
 * @version $Id: ArcCosine.java,v 1.4 2014/11/16 21:47:23 nwulff Exp $
 * @since 14.11.2010
 */

public final class ArcCosine extends AbstractArcFunction {
    /**
     * Complex arc cosine function acos(z). This implementation is a
     * extended version of  A&amp;ST 4.4.38 with some bound checks due
     * <pre>
     *   Hull et al, "Implementing the complex arcsin and arccosine
     *   functions using exception handling", ACM Transactions on
     *   Mathematical Software, Volume 23 (1997) pp 299-335.
     * </pre>
     * The result is only unique within the strip 0 &lt; re z &lt; &pi;.
     *
     * @param z the complex argument
     * @return acos(z)
     */
    public static ComplexNumber acos(final Complex z) {
        double real, imag;
        final double x = abs(z.real()), y = abs(z.imag());
        if (y == 0) {
            real = acos(z.real());
            imag = 0;
        } else {
            final double r = hypot(x + 1, y), s = hypot(x - 1, y);
            final double a = 0.5 * (r + s);
            final double b = x / a;
            final double y2 = y * y;
            if (b <= BMAX) {
                real = acos(b);
            } else {
                double d;
                final double apx = a + x;
                if (x <= 1) {
                    d = 0.5 * (apx) * (y2 / (r + x + 1) + (s + (1 - x)));
                    real = atan(sqrt(d) / x);
                } else {
                    d = 0.5 * (apx / (r + x + 1) + apx / (s + (x - 1)));
                    real = atan((y * sqrt(d)) / x);
                }
            }

            if (a <= AMAX) {
                double am1;

                if (x < 1) {
                    am1 = 0.5 * (y2 / (r + (x + 1)) + y2 / (s + (1 - x)));
                } else {
                    am1 = 0.5 * (y2 / (r + (x + 1)) + (s + (x - 1)));
                }

                imag = log1p(am1 + sqrt(am1 * (a + 1)));
            } else {
                imag = acosh(a);
            }
        }
        if (z.real() < 0) {
            real = Math.PI - real;
        }
        if (z.imag() >= 0) {
            imag = -imag;
        }
        return new ComplexNumber(real, imag);
    }

    /**
     * Calculate arc cosine for real argument x.
     *
     * @param x argument with |x| &le; 1
     * @return acos(x)
     */
    public static double acos(final double x) {
        if (Math.abs(x) > 1) {
            throw new IllegalArgumentException(ARC_RANGE_ERROR);
        }
        return Math.acos(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(double)
     */
    @Override
    public double f(final double x) {
        return acos(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(de.lab4inf.math.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return acos(z);
    }

}
 