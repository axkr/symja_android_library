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

import de.lab4inf.math.Complex;
import de.lab4inf.math.L4MObject;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Pade a like convergence acceleration for an alternating series.
 * Find an approximation for an infinite series from the first n
 * values:
 * <pre>
 *           n-1
 *    S(n) = sum (-1)**k a[k]
 *           k=0
 * </pre>
 * The a[k] should be "well behaved" and converge to zero.
 * <hr/>
 * This is Algorithm I from:
 * <pre>
 * H. Cohen, F. R. Villegaz and D. Zaiger,
 * "Convergence Acceleration of Alternating Series."
 *  Experimental Mathematics, 9:1 (2000) page 3
 * </pre>
 * adapted for Java. The precision to reach is roughly 5.828**(-n), i.e. to
 * calculate S with D digits precision the first n = 1.31D a[k] values have to
 * be accumulated.
 * <p>
 * <hr/>
 * The S value can be obtained by using the static method "approx"
 * with an array of the first n a[k] values
 * <pre>
 *    double[] a = ... // to be initialized
 *    s = Pade.approx(a);
 * </pre>
 * or by calling the  Pade instance within a loop running from k=0 to n-1.
 * <pre>
 *    double[] a = ... // to be initialized
 *    Pade p = new Pade(a.length);
 *    for(int k=0;k&lt;a.length;k++) {
 *        s = p.next(a[k]);
 *    }
 * </pre>
 * where the a[k] values can also be given by some function calls instead
 * of an array.
 *
 * @author nwulff
 * @version $Id: Pade.java,v 1.7 2014/11/16 21:47:23 nwulff Exp $
 * @since 18.03.2011
 */

public class Pade extends L4MObject {
    /**
     * too much iteration exception string.
     */
    private static final String TOO_MUCH_ITERATIONS = "too much iterations";
    private static final double SEED = 3 + 2 * sqrt(2);
    private final int n;
    private final double z;
    private final double d;
    private int k;
    private double b;
    private double c;
    private double s = 0;
    private double rCs = 0, iCs = 0;

    /**
     * Construct a Pade approximation for at most n arguments.
     *
     * @param n the number of arguments
     */
    public Pade(final int n) {
        this(n, 1);
    }

    /**
     * Construct a Pade approximation for at most n arguments.
     *
     * @param n the number of arguments
     * @param s the supremum of the coefficients ak with 1&le;s
     */
    public Pade(final int n, final double s) {
        if (n <= 0) {
            throw new IllegalArgumentException("illegal series size " + n);
        }
        this.n = n;
        z = Math.max(1, s);
        b = 2 * z + 1 + 2 * sqrt(z * (z + 1));
        b = pow(b, n);
        d = Accuracy.round((b + 1 / b) / 2, 1);
        b = -1 / d;
        c = -1;
        k = 0;
    }

    /**
     * Calculate the Pade series approximation for the given sequence.
     *
     * @param a the sequence to analyze
     * @return the Pade approximation
     */
    public static double approx(final double[] a) {
        final int n = a.length;
        double y = a[0];
        final Pade pade = new Pade(n);
        for (int k = 0; k < n; k++) {
            y = pade.next(a[k]);
        }
        return y;
    }

    /**
     * Calculate the complex Pade series approximation for the given sequence.
     *
     * @param a the sequence to analyze
     * @return the Pade approximation
     */
    public static Complex approx(final Complex[] a) {
        double b = SEED, c, d, rs = 0, is = 0;
        int k;
        final int n = a.length;
        b = pow(b, n);
        d = Accuracy.round((b + 1 / b) / 2, 1);
        b = -1 / d;
        c = -1;
        for (k = 0; k < n; k++) {
            c = b - c;
            rs += a[k].real() * c;
            is += a[k].imag() * c;
            b *= (((double)k) + ((double)n)) * (((double)k) - ((double)n)) / ((k + 0.5) * (k + 1.0));
        }
        return a[0].newComplex(rs, is);
    }

    /**
     * Calculate the next Pade approximation for the given value ak.
     * The argument counter k will be incremented during call.
     *
     * @param ak the actual k coefficients
     * @return s_n(k) the k.th approximation
     */
    public double next(final double ak) {
        if (k >= n) {
            throw new IllegalAccessError(TOO_MUCH_ITERATIONS);
        }
        c = b - c;
        s += c * abs(ak);
        b *= z * (k + n) * (k - n) / ((k + 0.5) * (k + 1));
        k++;
        return s;
    }

    /**
     * Calculate the next Pade approximation for the given value ak.
     * The argument counter k will be incremented during call.
     *
     * @param ak the actual k coefficients
     * @return s_n(k) the k.th approximation
     */
    public Complex next(final Complex ak) {
        if (k >= n) {
            throw new IllegalAccessError(TOO_MUCH_ITERATIONS);
        }
        c = b - c;
        rCs += ak.real() * c;
        iCs += ak.imag() * c;
        b *= z * (k + n) * (k - n) / ((k + 0.5) * (k + 1));
        k++;
        return ak.newComplex(rCs, iCs);
    }
}
 