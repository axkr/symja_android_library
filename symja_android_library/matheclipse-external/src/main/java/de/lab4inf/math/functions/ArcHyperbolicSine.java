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

import static java.lang.Math.log;
import static java.lang.Math.sqrt;

/**
 * Real and complex arc hyperbolic sine.
 *
 * @author nwulff
 * @version $Id: ArcHyperbolicSine.java,v 1.3 2014/11/16 21:47:23 nwulff Exp $
 * @since 16.11.2010
 */

public final class ArcHyperbolicSine extends AbstractArcFunction {
    /**
     * Implementation of the asinh for real argument.  A&amp;STT 4.6.20.
     *
     * @param x real argument
     * @return real asinh(x)
     */
    public static double asinh(final double x) {
        return log(x + sqrt(1 + x * x));
    }

    /**
     * Implementation of asinh for complex argument.  A&amp;ST 4.6.14
     *
     * @param z complex argument
     * @return complex asinh(x)
     */
    public static ComplexNumber asinh(final Complex z) {
        double x = z.real(), y = z.imag();
        final Complex asin = ArcSine.asin(new ComplexNumber(-y, x));
        x = asin.real();
        y = asin.imag();
        return new ComplexNumber(y, -x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(double)
     */
    @Override
    public double f(final double x) {
        return asinh(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(de.lab4inf.math.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return asinh(z);
    }
}
 