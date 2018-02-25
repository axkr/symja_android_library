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

package de.lab4inf.math.lapack;

import java.util.Arrays;

import de.lab4inf.math.L4MLoader;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.RootFinder;
import de.lab4inf.math.functions.Polynomial;
import de.lab4inf.math.util.Accuracy;
import de.lab4inf.math.util.Aitken;

import static de.lab4inf.math.lapack.LinearAlgebra.maxnorm;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.rndVector;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static de.lab4inf.math.util.Accuracy.round;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;

/**
 * Calculate the eigenvalues of a square matrix.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: EigenvalueSearcher.java,v 1.23 2015/04/21 14:15:46 nwulff Exp $
 * @since 20.01.2009
 */
public final class EigenvalueSearcher extends L4MObject {
    private static final int MAX_ITERATIONS = 500;
    private static final double RELEPS = 1.E-2;
    private static final double EPS = 1.E-6;
    private static final int NUM_TRIES = 7;
    private static final RootFinder RF = L4MLoader.load(RootFinder.class);

    /**
     * No public constructor allowed.
     */
    private EigenvalueSearcher() {
    }

    /**
     * Return a string representation of the vector v.
     *
     * @param v vector to present
     * @return string
     */
    public static String asString(final double[] v) {
        int i;
        final StringBuffer sb = new StringBuffer();
        for (i = 0; i < v.length - 1; i++) {
            sb.append(format(" %.3f,", v[i]));
        }
        sb.append(format(" %.3f", v[i]));
        return sb.toString();
    }

    /**
     * Print the eigenvalue iteration status.
     *
     * @param k
     * @param ev
     * @param v
     * @return
     */
    static String printEV(final int k, final double ev, final double[] v) {
        final String fmt = "ev[%2d]: %.3f  vector:%s";
        return format(fmt, k, ev, asString(v));
    }

    /**
     * Calculate the biggest eigenvalue of symmetric matrix \a a within the
     * given accuracy using the power method.
     *
     * @param a   matrix
     * @param eps accuracy of the eigenvalue
     * @return eigenvalue
     */
    @SuppressWarnings("rawtypes")
    public static double eigenvalue(final @Symmetric double[][] a, final double eps) {
        double e0 = 0, ek = 0;
        int k = 0;
        final int n = a.length;
        if (a[0].length != n) {
            throw new IllegalArgumentException("matrix not square");
        }
        final double[] evs = new double[NUM_TRIES];
        double evMin = Double.MAX_VALUE, evMax = -evMin;
        for (int j = 0; j < NUM_TRIES; j++) {
            final Aitken<?> aitken = new Aitken();
            double[] yk, y0 = rndVector(n);
            ek = 0;
            k = 0;
            do {
                e0 = ek;
                yk = mult(a, y0);
                ek = maxnorm(yk);
                yk = mult(yk, 1. / ek);
                y0 = yk;
                ek = aitken.next(ek);
                if (getLogger().isInfoEnabled()) {
                    getLogger().info(printEV(k, ek, y0));
                }
            } while (!hasConverged(ek, e0, eps / 5, ++k, MAX_ITERATIONS));
            evs[j] = ek;
        }
        for (int j = 0; j < NUM_TRIES; j++) {
            evMin = min(evMin, abs(evs[j]));
            evMax = max(evMax, abs(evs[j]));
            evMin = round(evMin, eps);
            evMax = round(evMax, eps);
        }
        if (abs(evMin - evMax) > RELEPS * abs(evMax)) {
            getLogger().info(format("EV min:%f max:%f", evMin, evMax));
            getLogger().info(format("EVs %s", Arrays.toString(evs)));
        }
        ek = round(evMax, eps);
        return ek;
    }

    /**
     * Calculate the biggest eigenvalue of symmetric matrix \a a with default accuracy
     * of 1.E-6 using the power method.
     *
     * @param a matrix
     * @return eigenvalue
     */
    public static double eigenvalue(final @Symmetric double[][] a) {
        return eigenvalue(a, EPS);
    }

    /**
     * Calculate the all real eigenvalues of a symmetric matrix a via its
     * characteristic polynomial.
     *
     * @param a the matrix
     * @return the eigenvalues
     */
    public static double[] eigenvalues(final @Symmetric double[][] a) {
        if (!LinearAlgebra.isSymmetric(a)) {
            getLogger().warning("matrix is not symmetric!");
            // return new double[0];
        }
        final double cond = (new GenericSolver()).cond(a);
        if (cond > 30) {
            getLogger().warn("bad condition: " + cond);
        }
        int j;
        final int n = a.length;
        double e, de;
        final double eps = Accuracy.DEPS * Math.pow(10, n);
        RF.setEpsilon(eps);
        Polynomial p, p0;
        final double[] evs = new double[n];
        final double[] guess = LinearAlgebra.eigenvalueRange(a);
        final double[] coeff = LinearAlgebra.characteristic(a);
        p = new Polynomial(coeff);
        e = guess[0];
        de = (guess[1] - guess[0]) / n;
        // first and naive guess ...
        for (j = 0; j < n; evs[j] = e, e += de, j++) ;

        // now do Newton iterations to find all roots
        for (int k = 1; k <= 2; k++) { // do it two times to enhance the accuracy!
            for (j = 0; j < n; j++) {
                try {
                    evs[j] = RF.root(p, evs[j]);
                } catch (final ArithmeticException error) {
                    getLogger().warn(error);
                    // we continue...
                }
                p0 = new Polynomial(-evs[j], 1); // separate the
                p = Polynomial.divide(p, p0); // found eigenvalue
            }
            Arrays.sort(evs);
            p = new Polynomial(coeff);
        }
        // reorder the eigenvalues, biggest first...
        for (j = 0; j < n / 2; j++) {
            final double tmp = evs[j];
            evs[j] = evs[n - j - 1];
            evs[n - j - 1] = tmp;
        }

        return evs;
    }
}
 