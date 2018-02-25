/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2011,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.fft;

import de.lab4inf.math.Complex;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.util.BitReversal;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Utility class to provide various types of Fourier transformations for
 * a given data sample.
 * <p>
 * If the data length N is a power of two a fast Cooley-Tukey radix 2
 * Fourier transformation FFT with a time dependency O(N*logN)
 * otherwise a simple DFT with a time dependency O(N*N) is used.
 * </hr>
 * <pre>
 * Note: The FFT is adapted from Paul Bourke's implementation
 *       http://paulbourke.net/miscellaneous/dft/
 * </pre>
 * </hr>
 *
 * @author nwulff
 * @version $Id: FFT.java,v 1.11 2014/02/04 18:39:43 nwulff Exp $
 * @since 16.02.2011
 */
public final class FFT extends L4MObject {
    /**
     * No instances are allowed for this utility class.
     */
    private FFT() {
    }

    /**
     * Indicate if the argument is a power of 2.
     *
     * @param n value to check
     * @return true if n = 2**m, for some m
     */
    protected static boolean isPower2(final int n) {
        return n > 1 && (n & (n - 1)) == 0;
    }

    /**
     * Calculate the binary logarithm log2(n) if n is a power of 2.
     *
     * @param n the argument
     * @return log2(n)
     */
    protected static int log2(final int n) {
        if (!isPower2(n)) {
            throw new IllegalArgumentException("not a power of two: " + n);
        }
        int m = 0, p = n;
        while (p > 1) {
            m++;
            p >>= 1;
        }
        return m;
    }

    /**
     * Fourier sine transformation.
     *
     * @param f the real function values
     * @param a the coefficients to calculate
     */
    public static void sineFFT(final double[] f, final double[] a) {
        fft(f, a, true);
    }

    /**
     * Fourier cosine transformation.
     *
     * @param f the real function values
     * @param a the coefficients to calculate
     */
    public static void cosineFFT(final double[] f, final double[] a) {
        fft(f, a, false);
    }

    /**
     * Real Fourier transformation with cosine and sine coefficients.
     *
     * @param f the real function values
     * @param a the cosine coefficients to calculate
     * @param b the sine coefficients to calculate
     */
    public static void fft(final double[] f, final double[] a, final double[] b) {
        int n = f.length;
        boolean simple = !isPower2(n);
        if (simple) {
            dft(f, a, b);
        } else {
            // this can still be optimized using a N/2 FFT...
            double[] u = f.clone();
            double[] v = new double[n];
            // for (int j = 0; j<n; u[j] = f[j], j++);
            fft(true, u, v);
            a[0] = u[0];
            b[0] = v[0];
            for (int j = 1; j < n / 2; j++) {
                a[j] = u[j] + u[n - j];
                b[j] = v[n - j] - v[j];
            }
        }
    }

    /**
     * Complex Fourier transformation.
     *
     * @param f the complex function values
     * @param a the complex amplitudes
     */
    public static void fft(final Complex[] f, final Complex[] a) {
        int n = f.length;
        boolean simple = !isPower2(n);
        if (simple) {
            dft(f, a);
        } else {
            double[] u = new double[n];
            double[] v = new double[n];
            for (int j = 0; j < n; u[j] = f[j].real(), v[j] = f[j].imag(), j++) ;
            fft(true, u, v);
            for (int j = 0; j < n / 2; j++) {
                a[j] = f[j].newComplex(u[j], v[j]);
            }
        }
    }

    /**
     * Fourier sine or cosine transformation, with given pitch frequency.
     *
     * @param f   the real function values
     * @param a   the coefficients to calculate
     * @param odd boolean indicating if sine (odd) or cosine (even) series
     */
    private static void fft(final double[] f, final double[] a, final boolean odd) {
        dft(f, a, odd);
    }


    /**
     * Basic primitive discrete none optimized Fourier sine or cosine transformation,
     * with a worse time dependency of NxN used if the data length is not a power of two.
     *
     * @param f   the real function values
     * @param a   the coefficients to calculate
     * @param odd boolean indicating if sine (odd) or cosine (even) series
     */
    private static void dft(final double[] f, final double[] a, final boolean odd) {
        int k, n = f.length, m = a.length;
        double z, w = n * 0.5, omega = 2 * PI;
        double c, s, c1, s1, cj, sj, ck, sk;
        z = omega / n;
        c = cos(z);
        s = sin(z);
        cj = 1;
        sj = 0;
        if (odd) {
            for (int j = 1; j < m; a[j] /= w, j++) {
                z = cj;
                cj = cj * c - sj * s;
                sj = z * s + sj * c;
                c1 = cj;
                s1 = sj;
                a[j] = 0;
                for (ck = c1, sk = s1, k = 1; k < n; a[j] += f[k] * sk,
                        z = ck, ck = ck * c1 - sk * s1, sk = z * s1 + sk * c1, k++)
                    ;
            }
        } else {
            for (k = 0; k < n; a[0] += f[k], k++)
                ;
            a[0] /= 2 * w;
            for (int j = 1; j < m; a[j] /= w, j++) {
                z = cj;
                cj = cj * c - sj * s;
                sj = z * s + sj * c;
                c1 = cj;
                s1 = sj;
                a[j] = 0;
                for (ck = 1, sk = 0, k = 0; k < n; a[j] += f[k] * ck,
                        z = ck, ck = ck * c1 - sk * s1, sk = z * s1 + sk * c1, k++)
                    ;
            }
        }
    }

    /**
     * Basic primitive discrete none optimized Fourier transformation,
     * with a worse time dependency of NxN, used if the data length is not
     * a power of two.
     *
     * @param f the real function values
     * @param a the cosine coefficients to calculate
     * @param b the sine coefficients to calculate
     */
    private static void dft(final double[] f, final double[] a, final double[] b) {
        int k, n = f.length, m = a.length;
        double c, s, c1, s1, cj, sj, ck, sk;
        double z, w = n * 0.5, omega = 2 * PI;
        for (k = 0; k < n; a[0] += f[k], k++) ;
        a[0] /= 2 * w;
        z = omega / n;
        c = cos(z);
        s = sin(z);
        cj = 1;
        sj = 0;
        for (int j = 1; j < m; a[j] /= w, b[j] /= w, j++) {
            z = cj;
            cj = cj * c - sj * s;
            sj = z * s + sj * c;
            a[j] = 0;
            b[j] = 0;
            c1 = cj;
            s1 = sj;
            for (ck = 1, sk = 0, k = 0; k < n; a[j] += f[k] * ck, b[j] += f[k] * sk,
                    z = ck, ck = ck * c1 - sk * s1, sk = z * s1 + sk * c1, k++)
                ;
        }
    }

    /**
     * Basic primitive discrete none optimized Fourier transformation,
     * with a worse time dependency of NxN, used if the data length is not
     * a power of two.
     *
     * @param f the complex function values
     * @param a the complex coefficients to calculate
     */
    private static void dft(final Complex[] f, final Complex[] a) {
        int k, n = f.length, m = a.length;
        double c, s, c1, s1, cj, sj, ck, sk;
        double z, aj, bj, fr, fi, omega = 2 * PI;
        a[0] = f[0].newComplex(0, 0);
        for (k = 0; k < n; a[0] = a[0].plus(f[k]), k++) ;
        a[0] = a[0].div(n);
        z = omega / n;
        c = cos(z);
        s = sin(z);
        cj = 1;
        sj = 0;
        for (int j = 1; j < m; j++) {
            z = cj;
            cj = cj * c - sj * s;
            sj = z * s + sj * c;
            c1 = cj;
            s1 = sj;
            for (aj = 0, bj = 0, ck = 1, sk = 0, k = 0; k < n;
                 z = ck, ck = ck * c1 - sk * s1, sk = z * s1 + sk * c1, k++) {
                fr = f[k].real();
                fi = f[k].imag();
                aj += fr * ck - fi * sk;
                bj += fr * sk + fi * ck;
            }
            a[j] = f[j].newComplex(aj / n, bj / n);
        }
    }

    /**
     * Internal helper to make a bit reversal swapping within the arrays.
     *
     * @param m the radix
     * @param x the real field
     * @param y the imaginary field
     */
    private static void bitreverse(final int m, final double[] x, final double[] y) {
        int[] r = BitReversal.reversInts(m);
        int i, j, n = x.length;
        double tx, ty;
         /* using the cached bit reversed array we swap if needed... */
        for (i = 0; i < n - 1; i++) {
            j = r[i];
            if (i < j) {
                tx = x[i];
                ty = y[i];
                x[i] = x[j];
                y[i] = y[j];
                x[j] = tx;
                y[j] = ty;
            }
        }
    }

    /**
     * This is the Paul Bourke algorithm adapted and rearranged for Java.
     * <p>
     * This computes an in-place complex-to-complex FFT x and y are the real
     * and imaginary arrays of 2^m points.
     */
    private static void fft(final boolean forward,
                            final double[] x, final double[] y) {
        int i, j, k, l, n = x.length, m = log2(n), o = 1, p;
        double ck = -1, sk = 0, cj, sj, tx, ty;
         /* Do the bit reversal */
        bitreverse(m, x, y);
 		/* Compute the FFT */
        for (k = 0; k < m; k++) {
            p = o;
            o <<= 1;
            for (cj = 1, sj = 0, j = 0; j < p; j++) {
                for (i = j, l = i + p; i < n; i += o, l += o) {
                    tx = cj * x[l] - sj * y[l];
                    ty = cj * y[l] + sj * x[l];
                    x[l] = x[i] - tx;
                    y[l] = y[i] - ty;
                    x[i] += tx;
                    y[i] += ty;
                }
                tx = cj * ck - sj * sk;
                sj = cj * sk + sj * ck;
                cj = tx;
            }
            sk = sqrt((1 - ck) / 2);
            if (forward) {
                sk = -sk;
            }
            ck = sqrt((1 + ck) / 2);
        }
 		/* Scaling for forward transform */
        if (forward) {
            for (i = 0; i < n; i++) {
                x[i] /= n;
                y[i] /= n;
            }
        }
    }
}
 