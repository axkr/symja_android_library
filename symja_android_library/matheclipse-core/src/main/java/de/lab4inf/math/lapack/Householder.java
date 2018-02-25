/*
 * Project: Lab4Math
 *
 * Copyright(c) 2008-2009, Prof. Dr. Nikolaus Wulff
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
* limitations under the License. *
*
*/

package de.lab4inf.math.lapack;

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.lapack.LinearAlgebra.isSymmetric;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;

/**
 * This class performs Householder transformations
 * for a symmetric matrix to reduce it to tri-diagonal form.
 *
 * @author nwulff
 * @version $Id: Householder.java,v 1.16 2014/11/18 23:41:21 nwulff Exp $
 * @since 07.01.2009
 */
public final class Householder extends L4MObject {
    private static final String HOUSEHOLDER_R0 = "householder r(%d) == 0";
    private static final String FMT_3F = "%+.3f ";

    /**
     * private constructor as no instances are allowed.
     */
    private Householder() {
    }

    /**
     * Reduce matrix A to tri-diagonal form via Householder transformations.
     *
     * @param a matrix to transform
     */
    public static void transform(final double[][] a) {
        if (!isSymmetric(a)) {
            throw new IllegalArgumentException("matrix not symmetric");
        }
        householder(a);
    }

    /**
     * The internal transformation routine without the
     * symmetry check, to be called from the LASolver
     * or classes which already made a symmetry check.
     *
     * @param mat matrix to transform
     */
    static void householder(final float[][] a) {
        int i, j, k, kp;
        final int n = a.length;
        final float[] w = new float[n];
        double s, r, tmp, amax;
        for (k = 0, kp = k + 1; k < n - 2; kp++, k++) {
            for (amax = 0, i = kp; i < n; amax = max(amax, abs(a[i][k])), i++)
                ;
            if (amax > 0) {
                for (s = 0, i = kp; i < n; tmp = a[i][k] / amax, s += tmp * tmp, i++)
                    ;
                s = amax * sqrt(s);
                if (abs(a[kp][k]) > Accuracy.DEPS) {
                    s *= signum(a[kp][k]);
                }
                r = (s + a[kp][k]) * s;
                w[k] = 0;
                w[kp] = a[kp][k] + (float) s;
                for (j = kp + 1; j < n; w[j] = a[j][k], j++)
                    ;
                if (r != 0) {
                    multPartial(a, kp, w, (float) r);
                } else {
                    LOGGER.error(String.format(HOUSEHOLDER_R0, k));
                    for (j = 0; j < n; j++) {
                        for (k = 0; k < n; k++) {
                            LOGGER.error(String.format(FMT_3F, a[j][k]));
                        }
                        LOGGER.error("\n");
                    }
                }
            }
        }
    }

    /**
     * The internal transformation routine without the
     * symmetry check, to be called from the LASolver
     * or classes which already made a symmetry check.
     *
     * @param mat matrix to transform
     */
    static void householder(final double[][] a) {
        int i, j, k, kp;
        final int n = a.length;
        final double[] w = new double[n];
        double s, r, tmp, amax;
        for (k = 0, kp = k + 1; k < n - 2; kp++, k++) {
            for (amax = 0, i = kp; i < n; amax = max(amax, abs(a[i][k])), i++)
                ;
            if (amax > 0) {
                for (s = 0, i = kp; i < n; tmp = a[i][k] / amax, s += tmp * tmp, i++)
                    ;
                s = amax * sqrt(s);
                if (abs(a[kp][k]) > Accuracy.DEPS) {
                    s *= signum(a[kp][k]);
                }
                r = (s + a[kp][k]) * s;
                w[k] = 0;
                w[kp] = a[kp][k] + s;
                for (j = kp + 1; j < n; w[j] = a[j][k], j++)
                    ;
                if (r != 0) {
                    multPartial(a, kp, w, r);
                } else {
                    LOGGER.warn(String.format(HOUSEHOLDER_R0, k));
                    for (j = 0; j < n; j++) {
                        for (k = 0; k < n; k++) {
                            System.err.printf(FMT_3F, a[j][k]);
                        }
                        System.err.println();
                    }
                }
            }
        }
    }

    /**
     * Do the partial product H*A*H starting with index k with the
     * Householder matrix H derived from vector w.
     *
     * @param a the matrix
     * @param k the start index
     * @param w the vector
     * @param r the scaling factor
     */
    private static void multPartial(final float[][] a, final int k, final float[] w, final float r) {
        int i, j;
        final int n = a.length, km = k - 1;
        final float[] u = new float[n];
        final float[] z = new float[n];
        float tmp;
        for (j = km; j < n; u[j] = tmp / r, j++)
            for (tmp = 0, i = k; i < n; tmp += a[j][i] * w[i], i++)
                ;

        for (tmp = 0, i = k; i < n; tmp += w[i] * u[i], i++)
            ;
        for (tmp /= 2, j = km; j < n; z[j] = u[j] - tmp * w[j] / r, j++)
            ;

        for (i = k; i < n; a[i][i] -= 2 * w[i] * z[i], i++)
            for (j = i + 1; j < n; a[j][i] -= w[i] * z[j] + w[j] * z[i], a[i][j] = a[j][i], j++)
                ;
        // clear the zero row,columns
        for (j = k + 1; j < n; a[j][km] = 0, a[km][j] = 0, j++)
            ;

        a[k][km] -= w[k] * z[km];
        a[km][k] = a[k][km];
    }

    /**
     * Do the partial product H*A*H starting with index k with the
     * Householder matrix H derived from vector w.
     *
     * @param a the matrix
     * @param k the start index
     * @param w the vector
     * @param r the scaling factor
     */
    private static void multPartial(final double[][] a, final int k, final double[] w, final double r) {
        int i, j;
        final int n = a.length, km = k - 1;
        final double[] u = new double[n];
        final double[] z = new double[n];
        double tmp;
        for (j = km; j < n; u[j] = tmp / r, j++)
            for (tmp = 0, i = k; i < n; tmp += a[j][i] * w[i], i++)
                ;

        for (tmp = 0, i = k; i < n; tmp += w[i] * u[i], i++)
            ;
        for (tmp /= 2, j = km; j < n; z[j] = u[j] - tmp * w[j] / r, j++)
            ;

        for (i = k; i < n; a[i][i] -= 2 * w[i] * z[i], i++)
            for (j = i + 1; j < n; a[j][i] -= w[i] * z[j] + w[j] * z[i], a[i][j] = a[j][i], j++)
                ;
        // clear the zero row,columns
        for (j = k + 1; j < n; a[j][km] = 0, a[km][j] = 0, j++)
            ;

        a[k][km] -= w[k] * z[km];
        a[km][k] = a[k][km];
    }
}
 