/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
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
import de.lab4inf.math.Integrable;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * The sinc function sin(x)/x.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Sinc.java,v 1.9 2014/06/17 09:38:22 nwulff Exp $
 * @since 29.12.2008
 */
public class Sinc extends L4MFunction implements Differentiable, Integrable {
    /**
     * Sinc function sin(x)/x.
     *
     * @param x argument
     * @return sinc(x)
     */
    public static double sinc(final double x) {
        if (x == 0) {
            return 1;
        }
        return sin(x) / x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return sinc(x[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    public Function getDerivative() {
        return new SincDerivative();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Integrable#getAntiderivative()
     */
    public Function getAntiderivative() {
        return new SineIntegral();
    }

    /**
     * Internal Sinc  derivative.
     */
    static class SincDerivative extends L4MFunction {
        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double)
         */
        @Override
        public double f(final double... x) {
            double xx = x[0];
            if (xx == 0) {
                return 0;
            }
            return cos(xx) / xx - sin(xx) / (xx * xx);
        }
    }
}
 