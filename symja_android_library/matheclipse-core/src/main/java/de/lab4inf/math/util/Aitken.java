/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.util;

//import de.lab4inf.math.sets.Rational;

import de.lab4inf.math.Field;
import de.lab4inf.math.L4MObject;

import static java.lang.Math.abs;

/**
 * Aitken convergence acceleration.
 * Build a quadratic convergent Aitken series from a
 * at least linear convergent series.
 * <p>
 * <pre>
 * Alexander Aitken
 * "On Bernoulli's numerical solution of algebraic equations",
 * Proc. Roy. Soc. Edinburgh, (1926), vol. 46, p. 289-305
 * </pre>
 * <p>
 * Mostly this will be done for double precision, but optional this
 * class can be parameterized as a generic to work with every field
 * of a given type T.
 *
 * @param <T> Aitken acceleration over field of typ T
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Aitken.java,v 1.13 2011/03/10 19:44:01 nwulff Exp $
 * @see de.lab4inf.math.Field
 * @since 03.11.2007
 */
public final class Aitken<T extends Field<T>> extends L4MObject {
    /**
     * minimal difference to allow.
     */
    private static final double TINY = 1.E-10;
    /**
     * size of the aitken array.
     */
    private static final int M = 3;
    /**
     * internal counter.
     */
    private short dn = 0, d0 = 0, d1 = 1, d2 = 2;
    /**
     * internal counter.
     */
    private short fn = 0, f0 = 0, f1 = 1, f2 = 2;
    /**
     * the last aitken values.
     */
    private double[] d = new double[M];
    /**
     * the last aitken field elementss.
     */
    @SuppressWarnings("unchecked")
    private T[] f = (T[]) new Field[M];

    /**
     * Generate an Aitken series from the given x values.
     *
     * @param x double the successive x values of the original series
     * @return double a better convergent Aitken series
     */
    public double next(final double x) {
        short n = d0;
        double q, e, y = x;
        d[d2] = x;
        if (dn >= 2) {
            q = d[d1] - d[d0];
            e = d[d2] - 2 * d[d1] + d[d0];
            // we force a minimal delta because of rounding errors bug
            if (abs(e) > TINY) {
                y = d[d0] - q * q / e;
            }
        } else {
            dn++;
        }
        d0 = d1;
        d1 = d2;
        d2 = n;
        return y;
    }

    /**
     * Generate an Aitken series from the given x values of the field elements.
     *
     * @param x the successive field element of the original series
     * @return a better convergent Aitken series
     */
    public T next(final T x) {
        short n = f0;
        T q, e, y = x;
        f[f2] = x;
        if (fn >= 2) {
            q = f[f1].minus(f[f0]);
            e = f[f2].minus(f[f1].plus(f[f1])).plus(f[f0]);
            if (!e.isZero()) {
                y = f[f0].minus(q.multiply(q).div(e));
            }
        } else {
            fn++;
        }
        f0 = f1;
        f1 = f2;
        f2 = n;
        return y;
    }
}
 