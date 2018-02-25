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

import de.lab4inf.math.CFunction;
import de.lab4inf.math.Complex;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Abstract base class for an inverse arc function.
 *
 * @author nwulff
 * @version $Id: AbstractArcFunction.java,v 1.5 2015/01/29 15:13:03 nwulff Exp $
 * @since 14.11.2010
 */

abstract class AbstractArcFunction extends L4MFunction implements CFunction {
    /**
     * msg for the arc functions  error.
     */
    protected static final String ARC_RANGE_ERROR = "re|z|>1";
    /**
     * msg for the hyperbolic arc functions  error.
     */
    protected static final String HARC_RANGE_ERROR = "re|z|<1";
    /**
     * turning point for the real arc calculus.
     */
    protected static final double AMAX = 1.5;
    /**
     * turning point for the imaginary arc calculus.
     */
    protected static final double BMAX = 0.6417;

    /**
     * Internal helper for an accurate calculation of sqrt(x*x+y*y)
     *
     * @param x first argument
     * @param y second argument
     * @return sqrt(x*x + y*x)
     */
    public static final double hypot(final double x, final double y) {
        double a = abs(x), b = abs(y);
        if (a < b) {
            double c = a;
            a = b;
            b = c;
        }
        if (a > 0) {
            b /= a;
            b *= b;
            b += 1;
            if (b > 1) {
                return a * sqrt(b);
            }
            return 1;
        }
        return 0;
    }

    /**
     * Internal helper for sqrt(x*x+y*y)
     *
     * @param x first argument
     * @param y second argument
     * @return sqrt(x*x + y*x)
     */
    public static final double hypot2(final double x, final double y) {
        return (x * x + y * y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public final double f(final double... x) {
        return f(x[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.sets.CFunction#f(de.lab4inf.math.sets.Complex[])
     */
    public final Complex f(final Complex... z) {
        return f(z[0]);
    }

    public abstract double f(final double x);

    public abstract Complex f(final Complex z);

}
 