/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2011,  Prof. Dr. Nikolaus Wulff
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

/**
 * Utility class to calculate the Fibonacci numbers.
 * <pre>
 * F(n+1) = F(n) + F(n-1)
 * </pre>
 * </br>
 * <p>
 * Note: This simple implementation can only calculate the first 92 numbers,
 * as F(92)= 7540113804746346429 the calculus will fail for higher arguments.
 * </br></hr>
 *
 * @author nwulff
 * @version $Id: FibonacciNumbers.java,v 1.4 2011/10/14 19:57:09 nwulff Exp $
 * @since 03.10.2011
 */
public final class FibonacciNumbers {
    private static final int MAX = 93;
    private static final long[] FN = new long[MAX];

    /**
     * Hidden constructor for utility class.
     */
    private FibonacciNumbers() {
    }

    /**
     * Calculate the n-th Fibonacci number.
     *
     * @param n the argument
     * @return F(n)
     */
    public static long fibonacci(final int n) {
        long fn;
        if (n >= MAX) {
            throw new IllegalArgumentException("too large n=" + n);
        }
        if (n < 0) {
            if ((n & 1) == 1)
                fn = fibonacci(-n);
            else
                fn = -fibonacci(-n);
        } else if (n == 0) {
            fn = 0;
        } else if (n == 1) {
            fn = 1;
        } else if (FN[n] > 0) {
            fn = FN[n];
        } else {
            fn = fibonacci(n - 1) + fibonacci(n - 2);
            FN[n] = fn;
        }
        return fn;
    }

}
 