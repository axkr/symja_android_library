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

package de.lab4inf.math.functions;

import de.lab4inf.math.CFunction;
import de.lab4inf.math.Complex;
import de.lab4inf.math.Letters;
import de.lab4inf.math.sets.ComplexNumber;

import static de.lab4inf.math.functions.Power.pow;
import static de.lab4inf.math.functions.Sine.sin;
import static de.lab4inf.math.sets.ComplexNumber.ONE;
import static de.lab4inf.math.sets.ComplexNumber.ZERO;
import static de.lab4inf.math.util.Accuracy.isSimilar;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;

//import static java.lang.Math.pow;
//import static java.lang.Math.sin;

/**
 * The gamma function &Gamma;(z)=(z-1)! for real or complex arguments.
 * <pre>
 *           &infin;
 *          /
 *  &Gamma;(z) = / t<sup>(z-1)</sup>*e<sup>-t</sup> dt
 *        /
 *       0
 * </pre>
 * <p>
 * The log(&Gamma;(x)) implementation is based on Cornelius Lanczos:
 * <hr/>
 * <pre>
 *  "A precision approximation of the gamma function",
 *  SIAM Journal on Numerical Analysis, B,
 *  Volume 1, 1964, pages 86-96.
 * </pre>
 * <hr/>
 * The coefficients are from the GNU GSL library.
 * <p>
 * The calculation of the inverse gamma function is based on the series
 * expansion 6.1.34 from
 * <hr/>
 * <pre>
 * "Pocketbook of Mathematical Functions", Abramowitz and Stegun, (1984).
 * </pre>
 * <hr/>
 * Both methods are compared to give the same results with
 * a relative accuracy of 10E-15 in the range [-5.5, 5.5],
 * which is an absolute precision of 10E-13 for real arguments.
 * <hr/>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Gamma.java,v 1.28 2014/11/16 21:47:23 nwulff Exp $
 * @since 23.12.2008
 */
public class Gamma extends L4MFunction implements CFunction {
    /**
     * the greek upper &Gamma; character as unicode.
     */
    public static final char GAMMA = Letters.UPPER_GAMMA;

    private static final double SQPI = sqrt(PI);
    private static final double TSQPI = sqrt(2 * PI);
    private static final double LOGSQPI = 0.9189385332046727;
    /**
     * coefficients for the inverse gamma function.
     */
    private static final double[] C = {1, 0.5772156649015329, -0.6558780715202538, -4.20026350340952E-2,
            0.1665386113822915, -4.21977345555443E-2, -9.6219715278770E-3, 7.2189432466630E-3, -1.1651675918591E-3,
            -2.152416741149E-4, 1.280502823882E-4, -2.01348547807E-5, -1.2504934821E-6, 1.1330272320E-6,
            -2.056338417E-7, 6.1160950E-9, 5.02075E-9, -1.1812746E-9, 1.043427E-10, 7.7823E-12, -3.6968E-12, 5.1E-13,
            -2.06E-14, -5.4E-15, 1.4E-15, 1.E-16};
    /**
     * coefficients for log gamma for the Lanczos series
     */
    private static final double[] B = {0.99999999999980993227684700473478, 676.520368121885098567009190444019,
            -1259.13921672240287047156078755283, 771.3234287776530788486528258894, -176.61502916214059906584551354,
            12.507343278686904814458936853, -0.13857109526572011689554707, 9.984369578019570859563e-6,
            1.50563273514931155834e-7};
    /**
     * number of coefficients for the B array.
     */
    private static final int NB = B.length;
    /**
     * number of coefficients for the C array.
     */
    private static final int NC = C.length;

    /**
     * Calculate the gamma function at point x.
     *
     * @param x the real argument
     * @return gamma(x) value
     */
    public static double gamma(final double x) {
        // return recGamma(x);
        return gammaLanczos(x);
    }

    /**
     * Calculate the complex gamma function at point z.
     *
     * @param z the complex argument
     * @return gamma(z) value
     */
    public static Complex gamma(final Complex z) {
        if (z.isReal()) {
            return new ComplexNumber(gamma(z.real()));
        }
        return recGamma(z);
    }

    /**
     * Calculate the gamma function at point x,
     * using exp(lnGamma(x)) with the help of the
     * Lanczos expansion.
     *
     * @param x the real argument
     * @return gamma(x) value
     */
    public static double gammaLanczos(final double x) {
        double y = Double.POSITIVE_INFINITY;
        if (x < 0) {
            // make use of reflection 6.1.17
            y = -PI / (x * sin(PI * x) * exp(lngamma(-x)));
        } else if (x > 0) {
            y = exp(lngamma(x));
        }
        return y;
    }

    /**
     * Implementation of the Lanczos algorithm,
     * calculate the natural logarithm of gamma(x).
     *
     * @param x the argument greater than zero
     * @return the value log(gamma(x))
     */
    public static double lngamma(final double x) {
        double z = x + 7, lngamma = 0;
        if (x <= 0) {
            throw new IllegalArgumentException("x <=0 ");
        }
        for (int j = NB - 1; j >= 1; j--) {
            lngamma += B[j] / z--;
        }
        z = x + 6.5;
        lngamma += B[0];
        lngamma = log(lngamma) + LOGSQPI - z + (x - 0.5) * log(z);
        return lngamma;
    }

    /**
     * Calculation of the inverse gamma function.
     * From formula 6.1.34 of Abramowitz and Stegun (1984).
     *
     * @param x the real argument
     * @return 1/gamma(x) the inverse gamma(x) value
     */
    public static double invGamma(final double x) {
        double iGamma = 0;
        if (x < 0) {
            final double z = -x;
            iGamma = x * sin(PI * z) / (PI * invGamma(z));
        } else if (x > 2) {
            final double z = x / 2;
            iGamma = 2 * SQPI * invGamma(z) * invGamma(z + 0.5) / pow(4, z);
        } else if (x > 1) {
            final double z = x - 1;
            iGamma = invGamma(z) / z;
        } else if (x > 0.5) {
            iGamma = sin(PI * x) / (PI * invGamma(1 - x));
        } else {
            iGamma = invSeries(x);
        }
        return iGamma;
    }

    /**
     * Calculation of the inverse gamma function.
     * From formula 6.1.34 of Abramowitz and Stegun (1984).
     *
     * @param z the complex argument
     * @return 1/gamma(z) the inverse gamma(z) value
     */
    public static Complex invGamma(final Complex z) {
        Complex iGamma = ZERO;
        double a = z.abs2();

        if (a >= 9) { // use Gauss multiplication formula A&ST 6.1.20
            final int n = (int) Math.sqrt(a);
            Complex v, w = z.div(n);
            iGamma = invGamma(w);
            for (double k = 1; k < n; k++) {
                v = w.plus(k / n);
                iGamma = iGamma.multiply(invGamma(v));
            }
            a = pow(TSQPI, 1 - n);
            w = z.minus(0.5);
            w = pow(n, w);
            v = w.multiply(a);
            iGamma = iGamma.div(v);
        } else if (a >= 4) { // use duplication formula A&ST 6.1.18
            final Complex w = z.div(2);
            iGamma = invGamma(w).multiply(invGamma(w.plus(0.5)));
            iGamma = iGamma.div(pow(4, w).div(2 * SQPI));
        } else if (z.real() > 1) {
            final Complex w = z.minus(1);
            iGamma = invGamma(w).div(w);
        } else if (z.real() > 0.5) {
            final Complex w = ONE.minus(z);
            iGamma = sin(z.multiply(PI)).div(invGamma(w).multiply(PI));
        } else {
            iGamma = invSeries(z);
        }
        return iGamma;
    }

    /**
     * Calculate the inverse gamma function using a series expansion.
     * Formula 6.1.34 from Abramowitz and Stegun.
     *
     * @param x the argument
     * @return 1/gamma(x)
     */
    private static double invSeries(final double x) {
        final double gEPS = 1.E-15;
        double dG, gammaInv = 0, zk = x;
        for (int k = 0; k < NC; k++) {
            dG = C[k] * zk;
            gammaInv += dG;
            if (abs(dG / (gammaInv * gammaInv)) < gEPS) {
                break;
            }
            zk *= x;
        }
        return gammaInv;
    }

    /**
     * Calculate the inverse gamma function using a series expansion.
     * Formula 6.1.34 from Abramowitz and Stegun.
     *
     * @param z complex the argument
     * @return 1/gamma(x)
     */
    private static Complex invSeries(final Complex z) {
        final double gEPS = 1.E-25;
        double a, b, rdG, idG, rGInv = 0, iGInv = 0, rzk = z.real(), izk = z.imag();
        for (int k = 0; k < NC; k++) {
            rdG = rzk * C[k];
            idG = izk * C[k];
            rGInv += rdG;
            iGInv += idG;
            a = max(abs(rdG), abs(idG));
            b = max(abs(rGInv), abs(iGInv));
            if (a < b * gEPS) {
                break;
            }
            a = rzk;
            rzk = rzk * z.real() - izk * z.imag();
            izk = izk * z.real() + a * z.imag();
        }
        return new ComplexNumber(rGInv, iGInv);
    }

    /**
     * Calculation of the gamma function with help of the recursion
     * Gamma(x+1)=x*Gamma(x), which allows to calculate the gamma function
     * solely within the unit interval via A&amp;ST 6.1.16:
     * <pre>
     * Gamma(x+n)=(x+n-1)*(x+n-2)*...(x+1)*Gamma(x+1)    0 &lt; x &le; 1
     * </pre>
     *
     * @param x the real argument
     * @return gamma(x)
     */
    public static double recGamma(final double x) {
        double gRecursive = 1;
        if (x < 0) {
            // make use of reflection 6.1.17
            gRecursive = -PI / (x * sin(PI * x) * recGamma(-x));
        } else {
            // int n = (int) Math.ceil(x);
            final int n = (int) x;
            final double z = x - n;
            if (isSimilar(z, 0)) {
                for (int i = 2; i < n; gRecursive *= i, i++)
                    ;
            } else {
                for (int i = 0; i < n; gRecursive *= (z + i), i++)
                    ;
                if (z > 0.5) {
                    final double w = z - 0.5;
                    gRecursive *= PI / cos(PI * w) * invSeries(1 - z);
                } else {
                    gRecursive /= invSeries(z);
                }
            }
        }
        if (getLogger().isInfoEnabled()) {
            getLogger().info(String.format(GAMMA + "(%.3f)=%g", x, gRecursive));
        }
        return gRecursive;
    }

    /**
     * Calculation of the gamma function with help of the recursion
     * Gamma(z+1)=z*Gamma(z), which allows to calculate the gamma function
     * solely within the unit circle via A&ST 6.1.16:
     * <pre>
     * Gamma(z+n)=(z+n-1)*(z+n-2)*...z*Gamma(z)    0 &lt; |z| &le; 1
     * </pre>
     *
     * @param z the complex argument
     * @return gamma(z)
     */
    public static Complex recGamma(final Complex z) {
        Complex gRecursive = ONE;
        final double x = z.real();
        final double y = z.imag();
        if (x < 0) {
            final Complex w = new ComplexNumber(1 - x, y);
            // make use of reflection 6.1.17
            gRecursive = recGamma(w).div(z);
        } else if (x <= 1.0) {
            gRecursive = gRecursive.div(invGamma(z));
        } else {
            final int n = (int) x;
            final Complex zmn = z.minus(n);
            if (zmn.isZero()) {
                int j = 1;
                for (int i = 2; i < n; j *= i, i++)
                    ;
                gRecursive = new ComplexNumber(j);
            } else {
                Complex zi;
                for (int i = 0; i < n; i++) {
                    // gRecursive *= (z+i);
                    zi = zmn.plus(i);
                    gRecursive = gRecursive.multiply(zi);
                }
                gRecursive = gRecursive.div(invGamma(zmn));
            }
        }
        return gRecursive;
    }

    // private static Complex invSeries(final Complex z) {
    // final double gEPS = 1.E-25;
    // Complex dG, gammaInv = ZERO, zk = z;
    // for (int k = 0; k<NC; k++) {
    // dG = zk.multiply(C[k]);
    // gammaInv = gammaInv.plus(dG);
    // if (dG.multiply(gammaInv).abs2()<gEPS) {
    // break;
    // }
    // zk = zk.multiply(z);
    // }
    // return gammaInv;
    // }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return gamma(x[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
     */
    @Override
    public Complex f(final Complex... z) {
        return gamma(z[0]);
    }
}
 