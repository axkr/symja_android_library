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

import static de.lab4inf.math.functions.Gamma.lngamma;
import static java.lang.Math.exp;
import static java.lang.Math.log;

/**
 * Poisson distribution function.
 * <pre>
 *    p_k(x) = x<sup>k</sup>*exp(-x)/k!
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Poisson.java,v 1.10 2014/06/17 09:38:22 nwulff Exp $
 * @since 20.09.2006
 */
public class Poisson extends L4MFunction {
    private double lamda;

    /**
     * Constructor with given lamda value.
     *
     * @param lamda value
     */
    public Poisson(final double lamda) {
        this.lamda = lamda;
    }

    /**
     * Constructor with given lamda value.
     *
     * @param lamda value
     */
    public Poisson(final Double lamda) {
        this(lamda.doubleValue());
    }

    /**
     * Calculate the poisson distribution for a given
     * lamda and x argument.
     *
     * @param x     the argument
     * @param lamda the lamda value
     * @return poisson distribution
     */
    public static double poisson(final double x, final double lamda) {
        double lnP = lamda * log(x) - x - lngamma(1 + lamda);
        return exp(lnP);
    }

    /**
     * Calculate the poisson distribution with a default
     * lamda of one.
     *
     * @param x the argument
     * @return poisson(x, 1)
     */
    public static double poisson(final double x) {
        return poisson(x, 1);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return poisson(x[0], lamda);
    }
}
 