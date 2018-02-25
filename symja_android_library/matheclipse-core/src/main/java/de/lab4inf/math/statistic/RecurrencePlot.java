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
package de.lab4inf.math.statistic;

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.lapack.LinearAlgebra;

import static java.lang.Math.abs;

/**
 * Construction of a recurrence plot from a given sequence X.
 * <pre>
 * J.-P. Eckmann, S. Oliffson Kamphorst, D. Ruelle:
 * Recurrence Plots of Dynamical Systems.
 * Europhys. Lett. 4, pp. 973-977, 1987.
 * </pre>
 *
 * @author nwulff
 * @version $Id: RecurrencePlot.java,v 1.2 2011/10/03 09:16:43 nwulff Exp $
 * @since 02.10.2011
 */
public final class RecurrencePlot extends L4MObject {
    /**
     * Hidden constructor for utility class without instances.
     */
    private RecurrencePlot() {
    }

    /**
     * Calculate the recurrence plot for the given sequence X.
     * The threshold is calculated via the mean distances.
     *
     * @param x sequence to analyze
     * @return the recurrence plot
     */
    public static byte[][] calculateRP(final double[] x) {
        int n = x.length;
        double e = 0;
        for (int j = 0; j < n; j++)
            for (int k = 0; k < n; e += abs(x[j] - x[k]), k++) ;
        e /= (10.0 * n * n);
        return calculateRP(x, e);
    }

    /**
     * Calculate the recurrence plot for the given sequence X,
     * using the threshold parameter e.
     *
     * @param x sequence to analyze
     * @param e threshold to use
     * @return the recurrence plot
     */
    public static byte[][] calculateRP(final double[] x, final double e) {
        int n = x.length;
        byte[][] rp = new byte[n][n];
        for (int j = 0; j < n; j++)
            for (int k = 0; k < n; k++) {
                if (abs(x[j] - x[k]) < e)
                    rp[j][k] = 1;
            }
        return rp;
    }

    /**
     * Calculate the recurrence plot for the given sequence X of vectors.
     * The threshold is calculated via the mean Euclidean distances.
     *
     * @param x sequence to analyze
     * @return the recurrence plot
     */
    public static byte[][] calculateRP(final double[][] x) {
        int n = x.length;
        double e = 0;
        for (int j = 0; j < n; j++)
            for (int k = 0; k < n; e += LinearAlgebra.diff(x[j], x[k]), k++) ;
        e /= (10.0 * n * n);
        return calculateRP(x, e);
    }

    /**
     * Calculate the recurrence plot for the given sequence X of vectors,
     * using the threshold parameter e.
     *
     * @param x sequence of vectors to analyze
     * @param e threshold to use
     * @return the recurrence plot
     */
    public static byte[][] calculateRP(final double[][] x, final double e) {
        int n = x.length;
        byte[][] rp = new byte[n][n];
        for (int j = 0; j < n; j++)
            for (int k = 0; k < n; k++) {
                if (LinearAlgebra.diff(x[j], x[k]) < e)
                    rp[j][k] = 1;
            }
        return rp;
    }
}
 