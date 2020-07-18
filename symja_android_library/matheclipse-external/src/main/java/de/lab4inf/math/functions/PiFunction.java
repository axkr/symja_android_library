/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2012,  Prof. Dr. Nikolaus Wulff
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

import de.lab4inf.math.Letters;
import de.lab4inf.math.util.PrimeNumbers;

/**
 * The number-theoretic &pi;(x)-function counting all primes less than x.
 *
 * @author nwulff
 * @version $Id: PiFunction.java,v 1.3 2014/06/17 09:38:22 nwulff Exp $
 * @since 22.08.2012
 */
public class PiFunction extends L4MFunction {
    public static final char PI = Letters.PI;
    private static final PrimeNumbers SIEVE = new PrimeNumbers();

    /**
     * Calculate the number of primes less x.
     *
     * @param x the argument
     * @return number of primes < x
     */
    public static double pi(final double x) {
        return SIEVE.pi(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.L4MFunction#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (x.length != 1)
            throw new IllegalArgumentException(String.format("%s function needs 1 argument", PI));
        return pi(x[0]);
    }

}
 