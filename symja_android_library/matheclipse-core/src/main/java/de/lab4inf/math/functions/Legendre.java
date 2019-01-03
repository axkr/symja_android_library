/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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

/**
 * The Legendre polynomials P<sub>n</sub>(x).
 * These are solutions of the differential equation:
 * <pre>
 *
 *   (1-x<sup>2</sup>)y'' -2xy'+n(n+1)y=0
 *
 * <pre>
 *
 * @author nwulff
 * @since 28.05.2014
 * @version $Id: Legendre.java,v 1.5 2014/11/16 21:47:23 nwulff Exp $
 */
public class Legendre extends Polynomial {
    /**
     * Constructor for the Legendre polynomial of degree n.
     *
     * @param n the degree of the Legendre polynomial.
     */
    public Legendre(final int n) {
        super(coefficients(n));
    }

    /**
     * Construct the coefficients of the Legendre polynomial via recursion
     * from A&amp;ST 22.7.10.
     *
     * @param n the degree
     * @return the (n+1) coefficients
     */
    public static final double[] coefficients(final int n) {
        if (n < 0)
            return coefficients(-n - 1);
        final double[] a = new double[n + 1];
        final double[] b = new double[n + 1];
        final double[] c = new double[n + 1];
        a[n] = 1;
        if (n > 1) {
            c[0] = 1;
            b[1] = 1;

            for (int k = 2; k <= n; k++) {
                a[0] = -(k - 1) * c[0] / k;
                for (int j = 1; j < k; j++) {
                    a[j] = ((2.0 * k - 1.0) * b[j - 1] - (k - 1.0) * c[j]) / k;
                }
                a[k] = (2.0 * k - 1.0) * b[k - 1] / k;

                for (int m = 0; m <= k; m++) {
                    c[m] = b[m];
                    b[m] = a[m];
                }
            }
        }
        return a;
    }
}
 