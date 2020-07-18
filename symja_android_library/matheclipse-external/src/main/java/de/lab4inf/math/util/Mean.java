/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2015,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
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

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Utility class to compute various types of mean values, like arithmetic,
 * geometric or harmonic mean, including the agm mean.
 *
 * @author nwulff
 * @version $Id: Mean.java,v 1.5 2015/01/23 15:03:43 nwulff Exp $
 * @since 21.01.2015
 */
public final class Mean {
    /**
     * Utility class without public constructor.
     */
    private Mean() {
    }

    /**
     * Calculate the arithmetic mean.
     *
     * @param x varargs values
     * @return arithmetic mean
     */
    public static double arithmeticMean(final double... x) {
        final int n = x.length;
        double y = 0;
        if (n > 0) {
            for (int j = 0; j < n; j++) {
                y += x[j];
            }
            y /= n;
        }
        return y;
    }

    /**
     * Calculate the geometric mean.
     *
     * @param x varargs values
     * @return geometric mean
     */
    public static double geometricMean(final double... x) {
        final int n = x.length;
        double y = 0;
        if (n > 0) {
            y = 1;
            for (int j = 0; j < n; j++) {
                y *= x[j];
            }
            y = pow(y, 1. / n);
        }
        return y;
    }

    /**
     * Calculate the harmonic mean.
     *
     * @param x varargs values
     * @return harmonic mean
     */
    public static double harmonicMean(final double... x) {
        final int n = x.length;
        double y = 0;
        if (n > 0) {
            for (int j = 0; j < n; j++) {
                if (x[j] != 0) {
                    y += 1 / x[j];
                } else {
                    return 0;
                }
            }
            y = 1. / y;
        }
        return y;
    }

    /**
     * Calculate the arithmetic-geometric mean (agm).
     *
     * @param x first argument
     * @param y double argument
     * @return agm(x, y)
     */
    public static double agmMean(final double x, final double y) {
        double a, b, c, ao = x, bo = y;
        do {
            a = (ao + bo) / 2;
            b = sqrt(ao * bo);
            c = (a - b) / 2;
            ao = a;
            bo = b;
        } while (abs(c) > Accuracy.DEPS);
        return a;
    }
}
 