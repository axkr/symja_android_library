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

package de.lab4inf.math.util;

import java.util.HashMap;
import java.util.Random;

import de.lab4inf.math.Function;
import de.lab4inf.math.Integrator;
import de.lab4inf.math.L4MLoader;
import de.lab4inf.math.L4MObject;

import static de.lab4inf.math.util.Accuracy.isInteger;
import static de.lab4inf.math.util.Accuracy.isSimilar;
import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.String.format;

/**
 * Generic random generator for various distributions.
 * <p>
 * <ol>
 * <li>uniform box distribution</li>
 * <li>uniform Cauchy distribution</li>
 * <li>normal Gaussian distribution</li>
 * <li>Gamma distribution</li>
 * <li>Chi-square distribution</li>
 * <li>Poisson distribution</li>
 * <li>Beta distribution</li>
 * <li>Binomial distribution</li>
 * <hr/>
 * <li>Neumann's rejection method for a generic PDF</li>
 * </ol>
 * <p>
 * This random number generator is backed up by the
 * 'java.util.Random' generator providing the normal
 * and gaussian distribution used for the other ones.
 * The seed can be set explicit, otherwise the current
 * time in milliseconds will be used as seed value.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Randomizer.java,v 1.38 2014/11/16 21:47:23 nwulff Exp $
 * @since 20.12.2008
 */
public final class Randomizer extends L4MObject {
    public static final String CHI2 = "\u03C7\u00B2";
    /**
     * random number to create at once.
     */
    private static final int NUMRND = 500;
    /**
     * maximal tolerated integration error for PDF check.
     */
    private static final double PDF_ERROR = 5.E-5;
    /**
     * HashMap with user defined pdf's.
     */
    private static HashMap<Function, Boolean> pdfs = new HashMap<Function, Boolean>();
    /**
     * seed value to start with.
     */
    private static long rndSeed;
    /**
     * Random instance to use.
     */
    private static Random rnd;
    /**
     * counter for the available random numbers.
     */
    private static volatile int randomsCount;
    /**
     * counter for the available gaussian numbers.
     */
    private static volatile int gaussianCount;
    /**
     * array with random numbers.
     */
    private static double[] randoms;
    /**
     * array with gaussian random numbers.
     */
    private static double[] gaussians;
    /**
     * integrator needed for PDF check.
     */
    private static Integrator integrator = L4MLoader.load(Integrator.class);

    /**
     * static default initialization.
     */
    static {
        rndSeed = (int) System.currentTimeMillis();
        randoms = new double[NUMRND];
        gaussians = new double[NUMRND];
        seed(rndSeed);
    }

    /**
     * Hidden constructor as no instances are allowed.
     */
    private Randomizer() {
    }

    /**
     * Generate the next n random numbers between 0 and 1.
     *
     * @param n numbers to generate
     */
    private static synchronized void nextRnd(final int n) {
        for (int i = 0; i < n; i++) {
            randoms[i] = rnd.nextDouble();
        }
        randomsCount = n;
    }

    /**
     * Generate the next n gaussian numbers with (0,1) gaussian distribution.
     *
     * @param n numbers to generate
     */
    private static synchronized void nextGaussian(final int n) {
        for (int i = 0; i < n; i++) {
            gaussians[i] = rnd.nextGaussian();
        }
        gaussianCount = n;
    }

    /**
     * Initialize a fresh number generator with given seed value. This will
     * generate the first NUMRND fresh normal and gaussian distributed random
     * numbers.
     *
     * @param seed the inital starting seed value
     */
    public static synchronized void seed(final long seed) {
        rndSeed = seed;
        rnd = new Random(rndSeed);
        getLogger().info(String.format("random generator seed %d", rndSeed));
    }

    /**
     * Get the actual used random number generator seed value.
     *
     * @return seed value of random generator
     */
    public static synchronized long getSeed() {
        return rndSeed;
    }

    /**
     * Return the next random number within [0,1].
     *
     * @return a normal distributed random number
     */
    public static synchronized double rndBox() {
        if (randomsCount == 0) {
            nextRnd(NUMRND);
        }
        return randoms[--randomsCount];
    }

    /**
     * Return the next random number within [min,max].
     *
     * @param min the lower border
     * @param max the upper border
     * @return random number between min and max
     */
    public static double rndBox(final double min, final double max) {
        return (max - min) * rndBox() + min;
    }

    /**
     * Return the next random integer between [0, max-1].
     *
     * @param max the maximal random integer
     * @return random integer
     */
    public static int rndInt(final double max) {
        return (int) (rndBox() * max);
    }

    /**
     * Generate a Cauchy or Breit-Wigner distributed random number with
     * (median,halfwidth)=(0,1).
     *
     * @return Cauchy random number
     */
    public static double rndCauchy() {
        return rndCauchy(0, 1);
    }

    /**
     * Generate a Cauchy or Breit-Wigner distributed random number.
     *
     * @param median the mean value
     * @param sigma  the half-width at half-maximum
     * @return a Cauchy random number
     */
    public static double rndCauchy(final double median, final double sigma) {
        double u;
        do {
            u = rndBox();
        } while (isSimilar(u, 0.5));
        return median + sigma * tan(PI * u);
    }

    /**
     * Return the next gaussian distributed random number with
     * (mean,variance)=(0,1).
     *
     * @return gaussian random number
     */
    public static synchronized double rndGaussian() {
        if (gaussianCount == 0) {
            nextGaussian(NUMRND);
        }
        return gaussians[--gaussianCount];
    }

    /**
     * Return the next gaussian distributed random number with
     * (mean,variance)=(mean,sigma).
     *
     * @param mean  the mean value
     * @param sigma the deviation
     * @return gaussian random number
     */
    public static double rndGaussian(final double mean, final double sigma) {
        return mean + sigma * rndGaussian();
    }

    /**
     * Return a chi2(n) distributed random number.
     *
     * @param n the degree of freedom has to be greater than zero
     * @return chi2 distributed random number
     */
    public static double rndChi2(final int n) {
        int m;
        double y, z, u = 1;
        if (n <= 0) {
            throw new IllegalArgumentException(CHI2 + "n<=0");
        }
        if (n % 2 != 0) {
            z = rndGaussian();
            y = z * z;
            m = (n - 1) / 2;
        } else {
            m = n / 2;
            y = 0;
        }
        for (int i = 0; i < m; i++) {
            u *= rndBox();
        }
        y -= 2 * log(u);
        return y;
    }

    /**
     * Return a poisson distributed integer random number.
     *
     * @param mean a none negative mean value
     * @return poisson random number.
     */
    public static int rndPoisson(final double mean) {
        int k = 0;
        double m, a = 1, mu = mean;
        if (mean < 0) {
            throw new IllegalArgumentException("mean<0");
        }

        while (mu > 10) {
            final int mm = (int) (mu * (7.0 / 8.0));
            final double x = rndGammaInt(mm);

            if (x >= mu) {
                return k + rndBinominal(mm - 1, mu / x);
            }
            k += mm;
            mu -= x;
        }

        m = exp(-mu);
        do {
            k++;
            a *= rndBox();
        } while (a > m);
        return k - 1;
    }

    /**
     * Generate an exponential distributed random number.
     * This simulates a radioactive decay of a particle
     * with lifetime tau=1/a.
     *
     * @param a the inverse lifetime greater than zero.
     * @return exponential distributed random number
     */
    public static double rndExponential(final double a) {
        return -log(rndBox()) / a;
    }

    /**
     * Generate a beta distributed random number.
     *
     * @param a first parameter, has to be positive
     * @param b second parameter, has to be positive
     * @return beta(a, b) distributed random number
     */
    public static double rndBeta(final double a, final double b) {
        final double x1 = rndGamma(a, 1.0);
        final double x2 = rndGamma(b, 1.0);
        return x1 / (x1 + x2);
    }

    /**
     * Return a binomial distributed random number. This algorithm is based on
     * D. Knuth.
     *
     * @param n a none negative integer
     * @param p a value between zero and one.
     * @return binomial random number
     */
    public static int rndBinominal(final int n, final double p) {
         /* This parameter is tunable. */
        final int nMax = 8;
        final double half = 0.5;
        double pp = p;
        final double q = 1 - p;
        int i, a, b, k = 0, nn = n;
        if (p > half) {
            return n - rndBinominal(n, q);
        }

        while (nn > nMax) {
            double x;
            a = 1 + (nn / 2);
            b = 1 + nn - a;
            x = rndBeta(a, b);
            if (x >= pp) {
                nn = a - 1;
                pp /= x;
            } else {
                k += a;
                nn = b - 1;
                pp = (pp - x) / (1 - x);
            }
        }
        for (i = 0; i < nn; i++) {
            final double u = rndBox();
            if (u < pp)
                k++;
        }
        return k;

    }

    /**
     * Return a gamma distributed random variable.
     * The algorithms is based on Knuth, vol 2, 2nd ed, p. 129.
     *
     * @param a the none negative shape parameter
     * @param b the none negative scale parameter
     * @return gamma(a, b) distributed random number
     */
    public static double rndGamma(final double a, final double b) {
        if (a < 0) {
            throw new IllegalArgumentException("a<0");
        }
        final int na = (int) floor(a);
        double ret = 0;
        if (a >= 12) {
            ret = b * (rndGammaLarge(a) + rndGammaFrac(a - na));
        } else if (isInteger(a)) {
            ret = b * rndGammaInt(na);
        } else if (na == 0) {
            ret = b * rndGammaFrac(a);
        } else {
            ret = b * (rndGammaInt(na) + rndGammaFrac(a - na));
        }
        return ret;
    }

    /**
     * Internal helper method for integer arguments.
     */
    private static double rndGammaInt(final int a) {
        if (a < 12) {
            double prod = 1;
            for (int i = 0; i < a; i++) {
                prod *= rndBox();
            }
            return -log(prod);
        }
        return rndGammaLarge(a);
    }

    /**
     * Internal helper method for large values.
     */
    private static double rndGammaLarge(final double a) {
         /*
          * Works only if a > 1, and is most efficient if a is large
          * 
          * This algorithm, reported in Knuth, is attributed to Ahrens. A faster one, we are told, can be found in: J. H.
          * Ahrens and U. Dieter, Computing 12 (1974) 223-246.
          */

        double sqa, x, y, v;
        sqa = sqrt(2 * a - 1);
        do {
            do {
                y = tan(PI * rndBox());
                x = sqa * y + a - 1;
            } while (x <= 0);
            v = rndBox();
        } while (v > (1 + y * y) * exp((a - 1) * log(x / (a - 1)) - sqa * y));

        return x;
    }

    /**
     * Internal helper method for values less than one.
     */
    private static double rndGammaFrac(final double a) {
         /*
          * This is exercise 16 from Knuth; see page 135, and the solution is on page 551.
          */
        // if (a>1) {
        // throw new IllegalArgumentException("a>1");
        // }
        double x, u, v = 0, q;
        final double p = E / (a + E);
        do {
            u = rndBox();
            while (v == 0) {
                v = rndBox();
            }

            if (u < p) {
                x = exp(log(v) / a);
                q = exp(-x);
            } else {
                x = 1 - log(v);
                q = exp((a - 1) * log(x));
            }
        } while (rndBox() >= q);
        return x;
    }

    /**
     * Generate a random number with probability function pdf(x) with 0 &le; x &le; 1
     * and 0 &le; pdf(x) &le; 1 using Neumann's rejection method.
     *
     * @param pdf probability function
     * @return random variable with given pdf
     */
    public static double rndNeumann(final Function pdf) {
        return rndNeumann(pdf, 0, 1);
    }

    /**
     * Generate a random number with probability function pdf(x) with a &le; x &le; b
     * and 0 &le; pdf(x)&le; 1 using Neumann's rejection method.
     *
     * @param pdf probability function
     * @param a   left interval border
     * @param b   right interval border
     * @return random variable with given pdf
     */
    public static double rndNeumann(final Function pdf, final double a, final double b) {
        double u, y;
        // quick (cached) check that the Function is a probability density
        // function
        if (!pdfCheck(pdf, a, b)) {
            final String msg = format("not a pdf %s", pdf.getClass().getName());
            final IllegalArgumentException error = new IllegalArgumentException(msg);
            getLogger().warn(msg);
            throw error;
        }
        do {
            u = rndBox();
            y = rndBox(a, b);
        } while (u >= pdf.f(y));
        return y;
    }

    /**
     * Check that the given function is a probability density, e.g.
     * 0 &le; pdf(x) &le; 1 and the the integral sums up to unity.
     *
     * @param pdf probability density function
     * @param a   left interval border
     * @param b   right interval border
     * @return true if function is a probability density false otherwise
     */
    public static boolean pdfCheck(final Function pdf, final double a, final double b) {
        synchronized (pdfs) {
            if (!pdfs.containsKey(pdf)) {
                final double dx = (b - a) / 1000;
                double x, xRnd, y, norm;
                // default is true but wait...
                boolean isValid = true;
                // check some values within the interval
                for (x = a + dx; x < b; x += dx) {
                    xRnd = rndBox(x - dx, x + dx);
                    y = pdf.f(xRnd);
                    if (y < 0 || y > 1) {
                        getLogger().warn(format("wrong value pdf(%f)=%f", xRnd, y));
                        isValid = false;
                        break;
                    }
                }
                // check the norm to be nearly unity
                if (isValid) {
                    norm = integrator.integrate(pdf, a, b);
                    if (abs(norm - 1) > PDF_ERROR) {
                        getLogger().warn("unnormalized pdf norm: " + norm);
                        isValid = false;
                    }
                }
                pdfs.put(pdf, isValid);
            }
        }
        return pdfs.get(pdf);
    }
}
 