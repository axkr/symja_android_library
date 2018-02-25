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

import static de.lab4inf.math.functions.ArcTangent.atan;
import static java.lang.Math.abs;
import static java.lang.Math.log;
import static java.lang.Math.log1p;

/**
 * Real and complex arc hyperbolic tangent.
 *
 * @author nwulff
 * @version $Id: ArcHyperbolicTangent.java,v 1.5 2014/11/16 21:47:23 nwulff Exp $
 * @since 16.11.2010
 */

public final class ArcHyperbolicTangent extends AbstractArcFunction {
    /**
     * Implementation of the atanh for real argument.  A&amp;ST 4.6.22.
     *
     * @param x real argument
     * @return real atanh(x)
     */
    public static double atanh(final double x) {
        if (abs(x) > 1) {
            throw new IllegalArgumentException(ARC_RANGE_ERROR);
        }
        if (abs(x) < 0.1) {
            return (log1p(x) - log1p(-x)) / 2;
        }
        if (abs(x) > 0.9) {
            double u = log(1 + x);
            final double v = -log(1 - x);
            u = (u + v) / 2;
            return u;
        }
        return log((1 + x) / (1 - x)) / 2;
    }

    /**
     * Implementation of atanh for complex argument.  A&amp;ST 4.6.16
     *
     * @param z complex argument
     * @return complex atanh(x)
     */
    public static ComplexNumber atanh(final Complex z) {
        double x = z.real(), y = z.imag();
        final Complex iz = new ComplexNumber(-y, x);
        final Complex atanh = atan(iz);
        x = atanh.real();
        y = atanh.imag();
        return new ComplexNumber(y, -x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(double)
     */
    @Override
    public double f(final double x) {

        return atanh(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(de.lab4inf.math.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return atanh(z);
    }
}
 