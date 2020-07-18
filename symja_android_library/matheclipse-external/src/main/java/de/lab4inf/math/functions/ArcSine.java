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
import static java.lang.Math.atan2;
import static java.lang.Math.log;
import static java.lang.Math.log1p;
import static java.lang.Math.sqrt;

/**
 * Real and complex arc sine function.
 *
 * @author nwulff
 * @version $Id: ArcSine.java,v 1.4 2014/11/16 21:47:23 nwulff Exp $
 * @since 14.11.2010
 */

public final class ArcSine extends AbstractArcFunction {
    /**
     * Complex arc sine function asin(z). This implementation is a
     * extended version of  A&amp;STT 4.4.37 with some bound checks due
     * <pre>
     *   Hull et al, "Implementing the complex arcsin and arccosine
     *   functions using exception handling", ACM Transactions on
     *   Mathematical Software, Volume 23 (1997) pp 299-335.
     * </pre>
     * The result is only unique within the strip |re z| &lt; &pi;/2.
     *
     * @param z the complex argument
     * @return asin(z)
     */
    public static ComplexNumber asin(final Complex z) {
        double a, b, c, d, q, x, y, y2;
        double real, imag;
        final double re = z.real(), im = z.imag();

        if (im == 0) {
            real = asin(re);
            imag = 0;
        } else {
            x = abs(re);
            y = abs(im);
            c = hypot(x + 1, y);
            d = hypot(x - 1, y);
            a = (c + d) / 2;
            b = x / a;
            y2 = y * y;

            if (b <= BMAX) {
                real = asin(b);
            } else {
                final double apx = a + x;
                if (x <= 1) {
                    q = 0.5 * (apx) * (y2 / (c + x + 1) + (d + (1 - x)));
                    real = atan2(x, sqrt(q));
                } else {
                    q = 0.5 * (apx / (c + x + 1) + apx / (d + (x - 1)));
                    real = atan2(x, y * sqrt(q));
                }
            }

            if (a <= AMAX) {
                double am1;

                if (x < 1) {
                    am1 = 0.5 * (y2 / (c + (x + 1)) + y2 / (d + (1 - x)));
                } else {
                    am1 = 0.5 * (y2 / (c + (x + 1)) + (d + (x - 1)));
                }

                imag = log1p(am1 + sqrt(am1 * (a + 1)));
            } else {
                imag = log(a + sqrt(a * a - 1));
            }

            if (z.real() < 0)
                real = -real;
            if (z.imag() < 0)
                imag = -imag;
        }

        return new ComplexNumber(real, imag);
    }

    /**
     * The direct implementation of  A&amp;ST 4.4.37, without checks.
     * <p>
     * Calculate the complex arc sine  asin(z).
     *
     * @param z the complex argument
     * @return asin(z)
     */
    public static ComplexNumber asinAST(final Complex z) {
        double a, b, c, d, x, y;
        x = z.real();
        y = z.imag();
        if (!z.isComplex()) {
            x = asin(x);
            y = 0;
        } else {
            x = abs(x);
            y = abs(y);
            c = hypot(x + 1, y);
            d = hypot(x - 1, y);
            a = (c + d) / 2;
            b = (c - d) / 2;
            x = asin(b);
            y = acosh(a);
            if (z.real() < 0)
                x = -x;
            if (z.imag() < 0)
                y = -y;
        }
        return new ComplexNumber(x, y);
    }

    /**
     * Calculate arc sine for real argument x.
     *
     * @param x real argument with |x| le; 1
     * @return asin(x)
     */
    public static double asin(final double x) {
        if (abs(x) > 1) {
            throw new IllegalArgumentException(ARC_RANGE_ERROR);
        }
        return Math.asin(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(double)
     */
    @Override
    public double f(final double x) {
        return Math.asin(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(de.lab4inf.math.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return asin(z);
    }

}
 