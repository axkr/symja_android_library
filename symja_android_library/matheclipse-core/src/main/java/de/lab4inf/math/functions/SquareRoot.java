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
import de.lab4inf.math.Numeric;
import de.lab4inf.math.sets.ComplexNumber;

/**
 * Real and complex square root.
 *
 * @author nwulff
 * @version $Id: SquareRoot.java,v 1.4 2013/06/22 19:14:03 nwulff Exp $
 * @since 14.11.2010
 */

public class SquareRoot extends L4MFunction implements CFunction {

    /**
     * Complex root of x.
     *
     * @param x the real argument
     * @return sqrt(x)
     */
    public static ComplexNumber sqrt(final double x) {
        if (x >= 0) {
            return new ComplexNumber(Math.sqrt(x), 0);
        }
        return new ComplexNumber(0, Math.sqrt(-x));
    }

    public static <T extends Numeric<T>> T sqrt(final T x) {
        return x.sqrt();
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return Math.sqrt(x[0]);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
     */
    @Override
    public Complex f(final Complex... z) {
        return sqrt(z[0]);
    }
}
 