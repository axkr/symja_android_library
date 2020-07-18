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
package de.lab4inf.math.statistic;

/**
 * Discrete parameterized filter.
 * It does a simple filter step using the last n measured x values via:
 * <pre>
 *          n-1
 *   y(k) = sum a(j)*x(k-j)
 *          j=0
 * </pre>
 * The coefficients are normalized during construction.
 * <pre>
 *          n-1
 *      1 = sum a(j)
 *          j=0
 * </pre>
 *
 * @author nwulff
 * @version $Id: DiscreteFilter.java,v 1.3 2014/06/01 16:56:28 nwulff Exp $
 * @since 14.03.2011
 */

public class DiscreteFilter {
    private double[] a;
    private double[] x;
    private int p, n;

    /**
     * Constructor for a parameterized filter. The coefficients will
     * be normalized to unity during setup.
     *
     * @param a the filter coefficients.
     */
    public DiscreteFilter(final double[] a) {
        this.a = a.clone();
        n = a.length;
        x = new double[n];
        double norm = 0;
        for (int i = 0; i < n; i++) {
            norm += a[i];
        }
        if (norm <= 0) {
            throw new IllegalArgumentException("norm is zero");
        }
        for (int i = 0; i < n; i++) {
            this.a[i] /= norm;
        }
    }

    /**
     * Calculate for the last xk value the filtered yk value.
     *
     * @param xk the unfiltered value
     * @return yk the filtered value
     */
    public double filter(final double xk) {
        double yk = 0;
        int j;
        x[p] = xk;
        for (int i = 0; i < n; i++) {
            j = (n + p - i) % n;
            yk += a[i] * x[j];
        }
        p = ((p + 1) % n);
        return yk;
    }
}
 