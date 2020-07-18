/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2011,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.util;

import static de.lab4inf.math.util.BinomialCoefficient.dbinomial;

/**
 * Utility class to calculate the Bernoulli numbers.
 * <br/>
 * <p>
 * Note: This simple implementation can only calculate the first 256 numbers,
 * as B(256)=-7.95e+302 the calculus will fail for higher arguments with
 * double precision.
 * <p>
 * <br/><hr/>
 * Using the recursive Zeta function definition for even argument
 * <pre>
 *                1     n-1
 *  zeta(2n) =  -----   sum zeta(2k)*zeta(2n-2k)
 *              n+1/2   k=1
 * </pre>
 * and Euler's formula, A &amp;ST 23.2.16
 * <pre>
 *  zeta(2n) = -(-1)**n (2pi)**(2n)/(2*(2n)!) B(2n)
 * </pre>
 * <p>
 * it is possible to derive a similar recursion for the Bernoulli numbers.
 * <p>
 * <pre>
 *           -1      n-1
 *  B(2n) = ------   sum  (2n over 2k) B(2k) B(2n-2k)
 *          2n + 1   k=1
 * </pre>
 * <hr/>
 *
 * @author nwulff
 * @version $Id: BernoulliNumbers.java,v 1.14 2014/11/16 21:47:23 nwulff Exp $
 * @since 13.03.2011
 */
public final class BernoulliNumbers {
    private static final int MAX = 257;
    private static final double[] BN = new double[MAX];

    /**
     * Hidden constructor for utility class.
     */
    private BernoulliNumbers() {
    }

    ;

    /**
     * Return the Bernoulli number B(n).
     *
     * @param n the argument
     * @return B(n)
     */
    public static double getB(final int n) {
        if (n >= MAX) {
            throw new IllegalArgumentException("n too large: " + n);
        }
        if (n < 0) {
            throw new IllegalArgumentException("negativ argument: " + n);
        }
        if ((n & 1) == 1 && n > 1) {
            return 0;
        }
        if (BN[n] == 0) {
            final double b = calculate(n);
            BN[n] = b;
        }
        return BN[n];
    }

    /**
     * Internal calculation of B(n).
     *
     * @param n the argument
     * @return B(n)
     */
    private static double calculate(final int n) {
        double b = 0;
        if (n == 0) { // recursion start
            b = 1;
        } else if (n == 1) { // first and only odd
            b = -0.5;
            // } else if ((n & 1) == 1) { // odd numbers can't happen by construction
            // b = 0; // of method getB ...
        } else { // the rest via recursion
            b = b(n);
        }
        return b;
    }

    /**
     * Recursive calculation.
     *
     * @param m an even argument of the form m = 2*n for n>=1.
     * @return b(m)=b(2n)
     */
    private static double b(final int m) {
        double sum = 0;
        if (m == 2) {
            sum = -1. / 6;
        } else {
            for (int k = 2; k < m; k += 2) {
                sum += getB(k) * getB(m - k) * dbinomial(m, k);
            }
            sum /= (m + 1);
        }
        return -sum;
    }
}
 