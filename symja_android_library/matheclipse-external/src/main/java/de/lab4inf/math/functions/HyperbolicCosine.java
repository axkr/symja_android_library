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
import de.lab4inf.math.sets.ComplexNumber;

/**
 * Complex and real hyperbolic cosine implementation cosh(z).
 *
 * @author nwulff
 * @version $Id: HyperbolicCosine.java,v 1.4 2015/01/29 15:13:03 nwulff Exp $
 * @since 14.11.2010
 */

public class HyperbolicCosine extends HyperbolicFunction {
    public static final HyperbolicCosine FCT = new HyperbolicCosine();

    /**
     * Calculate the complex hyperbolic cosine cosh(z).
     *
     * @param z the complex argument
     * @return cosh(z)
     */
    public static ComplexNumber cosh(final Complex z) {
        double r = z.real();
        double i = z.imag();
        double x, y;
        if (!z.isComplex()) {
            x = Math.cosh(r);
            y = 0;
        } else {
            x = Math.cosh(r) * Math.cos(i);
            y = Math.sinh(r) * Math.sin(i);
        }
        return new ComplexNumber(x, y);
    }

    /**
     * Calculate the hyperbolic cosine for real argument.
     *
     * @param x real argument
     * @return cosh(x)
     */
    public static double cosh(final double x) {
        return Math.cosh(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.HyperbolicFunction#f(double)
     */
    @Override
    public double f(final double x) {
        return Math.cosh(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.HyperbolicFunction#createDerivative()
     */
    @Override
    protected CFunction createDerivative() {
        return HyperbolicSine.FCT;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.HyperbolicFunction#createAntiDerivative()
     */
    @Override
    protected CFunction createAntiDerivative() {
        return HyperbolicSine.FCT;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.HyperbolicFunction#f(de.lab4inf.math.Complex)
     */
    @Override
    public Complex f(final Complex z) {
        return cosh(z);
    }
}
 