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
 * Real and complex arc hyperbolic cosine.
 *
 * @author nwulff
 * @version $Id: ArcHyperbolicCosine.java,v 1.3 2014/11/16 21:47:23 nwulff Exp $
 * @since 16.11.2010
 */

public final class ArcHyperbolicCosine extends AbstractArcFunction {

    /**
     * Calculate the real acosh. From  A&amp;ST 4.6.21.
     *
     * @param a real argument a &ge; 1
     * @return acosh(a)
     */
    public static double acosh(final double a) {
        if (a < 1) {
            throw new IllegalArgumentException(HARC_RANGE_ERROR);
        }
        return log(a + sqrt(a * a - 1));
    }

    /**
     * Implementation of acosh for complex argument.  A&amp;ST 4.6.15
     *
     * @param z complex argument
     * @return complex acosh(x)
     */
    public static ComplexNumber acosh(final Complex z) {
        final Complex acos = ArcCosine.acos(new ComplexNumber(z));
        return new ComplexNumber(-acos.imag(), acos.real());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(double)
     */
    @Override
    public double f(final double x) {
        return acosh(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.AbstractArcFunction#f(de.lab4inf.math.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return acosh(z);
    }

}
 