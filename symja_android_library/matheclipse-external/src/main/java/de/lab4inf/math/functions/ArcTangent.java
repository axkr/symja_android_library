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

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.log;
import static java.lang.Math.log1p;

/**
 * Real and complex arc tangent function.
 *
 * @author nwulff
 * @version $Id: ArcTangent.java,v 1.6 2014/11/16 21:47:23 nwulff Exp $
 * @since 14.11.2010
 */

public final class ArcTangent extends AbstractArcFunction {
    /**
     * The direct implementation of A&amp;ST 4.4.39, without checks.
     * <p>
     * Calculate the complex arc sine  atan(z).
     *
     * @param z the complex argument
     * @return asin(z)
     */
    public static ComplexNumber atan(final Complex z) {
        double a, b, c, d, x, y;
        x = z.real();
        y = z.imag();
        if (!z.isComplex()) {
            x = Math.atan(x);
            y = 0;
        } else {
            c = hypot2(x, y);
            a = atan2(2 * x, 1 - c) / 2;
            d = 2 * y / (1 + c);
            if (abs(d) < 0.1) {
                b = (log1p(d) - log1p(-d)) / 4;
            } else {
                c = hypot2(x, y + 1);
                d = hypot2(x, y - 1);
                b = log(c / d) / 4;
            }
            x = a;
            y = b;
        }
        return new ComplexNumber(x, y);
    }

    /**
     * Calculate arc tangent for real argument x.
     *
     * @param x real argument
     * @return atan(x)
     */
    public static double atan(final double x) {
        return Math.atan(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(double)
     */
    @Override
    public double f(final double x) {
        return Math.atan(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(de.lab4inf.math.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return atan(z);
    }

}
 