/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.functions.Erf;
import de.lab4inf.math.functions.Gamma;
import de.lab4inf.math.functions.IncompleteBeta;
import de.lab4inf.math.functions.IncompleteGamma;
import de.lab4inf.math.gof.Visitor;
import de.lab4inf.math.util.ContinuedFraction;

import static de.lab4inf.math.functions.Beta.beta;
import static de.lab4inf.math.functions.Gamma.gamma;
import static de.lab4inf.math.functions.Gamma.lngamma;
import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Collection of various discrete and continuous probability distributions
 * and where possible their cumulative probability functions.
 * <pre>
 *                x
 *               /
 *     cdf(x) = / dt pdf(t)
 *             /
 *       0 or -infinity
 * </pre>
 * <p>
 * and the inverse of the cumulative probability functions, e.g for
 * p = cdf(x) calculate the quantile x = invCdf(p).
 *
 * @author nwulff
 * @version $Id: ProbabilityDistribution.java,v 1.39 2014/11/16 21:47:23 nwulff Exp $
 * @since 14.02.2009
 */
public final class ProbabilityDistribution extends L4MObject {
    /**
     * reference to the PARAMETER_NEGATIVE attribute.
     */
    private static final String PARAMETER_NEGATIVE = "param %f negative";
    /**
     * reference to the ARGUMENT_NEGATIVE attribute.
     */
    private static final String ARGUMENT_NEGATIVE = "argument %f negative";
    /**
     * precision of the quantile calculations.
     */
    private static final double PRECISION = 1.E-5;
    /**
     * maximal number of iterations for the quantile calculations.
     */
    private static final int MAX_ITE = 256;
    /**
     * the constant sqrt(2pi).
     */
    private static final double SQ2PI = sqrt(2 * PI);
    /**
     * the constant sqrt(2).
     */
    private static final double SQTWO = sqrt(2);

    /**
     * There are no instances of this utility class.
     */
    private ProbabilityDistribution() {
    }

    /**
     * Calculate the chi squared distribution for v degrees of freedom.
     *
     * @param v the number of freedoms
     * @param x the real argument to be positive
     * @return chi2_v(x)
     */
    public static double pdfChi2(final int v, final double x) {
        if (x <= 0) {
            if (x < 0)
                getLogger().warn(String.format(ARGUMENT_NEGATIVE, x));
            return 0;
        }
        final double xh = x / 2, vh = v / 2.0;
        return exp((vh - 1) * log(xh) - xh - lngamma(vh)) / 2;
    }

    /**
     * Calculate the cumulative chi squared distribution for v degrees of freedom.
     *
     * @param v the number of freedoms
     * @param x the real argument to be positive
     * @return the integral cdf(x) = int pdf(t) dt
     */
    public static double cdfChi2(final int v, final double x) {
        if (x <= 0) {
            if (x < 0)
                getLogger().warn(String.format(ARGUMENT_NEGATIVE, x));
            return 0;
        }
        return IncompleteGamma.regGammaP(v / 2.0, x / 2);
    }

    /**
     * Calculate the quantile, e.g. inverse of the chi squared cumulative
     * distribution function.
     *
     * @param v the number of freedoms
     * @param p the probability
     * @return the quantile
     */
    public static double quantileChi2(final int v, final double p) {
        checkProbabilty(p);

        if (p <= 0) {
            if (p < 0)
                getLogger().warn(String.format(ARGUMENT_NEGATIVE, p));
            return 0;
        }
        return quantileGamma(v / 2.0, 2.0, p);
    }

    /**
     * The  gaussian(0,1)/ normal distribution.
     *
     * @param x the real argument
     * @return n(0, 1)
     */
    public static double pdfNormal(final double x) {
        return exp(-x * x / 2) / SQ2PI;
    }

    /**
     * The cumulative distribution function for the normal distribution.
     *
     * @param x the argument
     * @return cdf(x)
     */
    public static double cdfNormal(final double x) {
        final boolean cdfCF = false;
        if (cdfCF) {
            return cdfNormalCF(x);
        }
        return cdfNormalErf(x);
    }

    /**
     * Cumulative distribution function of the normal distribution, using
     * the error integral erf(x), formula 26.2.29 A &amp;S.
     *
     * @param x the argument
     * @return cdf(x)
     */
    public static double cdfNormalErf(final double x) {
        // formula 26.2.29 A&S
        return 0.5 * (1 + Erf.erf(x / SQTWO));
    }

    /**
     * Cumulative distribution function of the normal distribution, using
     * a continued fraction approximation, formula 26.2.14 or 26.2.15 A &amp;S.
     *
     * @param x the argument
     * @return cdf(x)
     */
    public static double cdfNormalCF(final double x) {
        final double eps = 1.E-15;
        double p, q, r;
        final double z = abs(x);
        // calculate Q(x) via CF
        if (z > 1) {
            // formula 26.2.14 A&S for large x
            final ContinuedFraction cf = new NormalCFLargeX();
            r = cf.evaluate(z, eps);
            q = r * pdfNormal(x);
        } else {
            // formula 26.2.15 A&S for small x
            final ContinuedFraction cf = new NormalCFSmallX();
            r = cf.evaluate(z, eps);
            q = 0.5 - r * pdfNormal(x);
        }
        // using P(x) + Q(x) = 1 and P(-x) = Q(x)
        if (x > 0) {
            p = 1 - q;
        } else {
            p = q;
        }
        return p;
    }

    /**
     * Calculate the inverse cumulative distribution function for the
     * normal distribution. That is for p=cdf(x) calculate x=invcdf(p).
     * <p>
     * Credits: The algorithm is adapted from
     * <a href="http://home.online.no/~pjacklam/notes/invnorm/">Peter J. Acklam</a>
     * <br/>
     * <p>
     * TODO This algorithm seems to be fitted for single floating
     * point precision, as it fails for |1 - p | or |0 - p| &lt; 1.E-7 values.
     * Which means that the returned x values have to be in the
     * interval [-5.5, 5.5].
     *
     * @param p the cdf probability
     * @return x such that cdf(x)=p
     */
    public static double quantileNormal(final double p) {
        return calcIncCdf(p, true);
    }

    /**
     * The internal kernel of Acklam's algorithm.
     */
    private static double calcIncCdf(final double p, final boolean highPrecision) {
        final double pLow = 0.02425;
        // Coefficients in rational approximations.
        final double[] a = {-3.969683028665376e+01, 2.209460984245205e+02, -2.759285104469687e+02,
                1.383577518672690e+02, -3.066479806614716e+01, 2.506628277459239e+00};

        final double[] b = {-5.447609879822406e+01, 1.615858368580409e+02, -1.556989798598866e+02,
                6.680131188771972e+01, -1.328068155288572e+01};

        final double[] c = {-7.784894002430293e-03, -3.223964580411365e-01, -2.400758277161838e+00,
                -2.549732539343734e+00, 4.374664141464968e+00, 2.938163982698783e+00,};

        final double[] d = {7.784695709041462e-03, 3.224671290700398e-01, 2.445134137142996e+00,
                3.754408661907416e+00,

        };

        double q, u, t;

        checkProbabilty(p);
        q = min(p, 1 - p);
        if (q > pLow) {
             /* Rational approximation for central region. */
            u = q - 0.5;
            t = u * u;
            u = u * (((((a[0] * t + a[1]) * t + a[2]) * t + a[3]) * t + a[4]) * t + a[5]) / (((((b[0] * t + b[1]) * t + b[2]) * t + b[3]) * t + b[4]) * t + 1);
        } else {
             /* Rational approximation for tail region. */
            t = sqrt(-2 * log(q));
            u = (((((c[0] * t + c[1]) * t + c[2]) * t + c[3]) * t + c[4]) * t + c[5]) / ((((d[0] * t + d[1]) * t + d[2]) * t + d[3]) * t + 1);
        }
        if (highPrecision) {
            u = refine(u, q);
        }
        if (p > 0.5)
            u = -u;
        return u;
    }

    /**
     * Refining algorithm is based on Halley rational method
     * for finding roots of equations.
     */
    private static double refine(final double x, final double p) {
        double z = x;
        if (0 < p && p < 1) {
            final double e = 0.5 * Erf.erfc(-x / SQTWO) - p;
            final double u = e * SQ2PI * exp((x * x) / 2.0);
            z -= u / (1.0 + x * u / 2.0);
        }
        return z;
    }

    /**
     * Check if the given probability value is within the range
     * 0 &le; p &le; 1. If not an exception is thrown.
     *
     * @param p probability value to check.
     */
    private static void checkProbabilty(final double p) {
        if (Double.isNaN(p) || p < 0 || p > 1) {
            throw new IllegalArgumentException(String.format("not a probability: %f", p));
        }
    }

    /**
     * Check if the given value is within the range
     * 0 &le; x &le; 1. If not an exception is thrown.
     *
     * @param x value to check.
     */
    private static void checkUnit(final double x) {
        if (Double.isNaN(x) || x < 0 || x > 1) {
            throw new IllegalArgumentException(String.format("not within [0,1]: %f", x));
        }
    }

    /**
     * The Cauchy or Breit-Wigner probability distribution.
     *
     * @param mean  the median value (it has no mean!)
     * @param sigma the half-width at half-maximum (it has no deviation!)
     * @param x     the real argument
     * @return the Cauchy pdf value
     */
    public static double pdfCauchy(final double mean, final double sigma, final double x) {
        final double z = (x - mean) / sigma;
        return 1. / (PI * sigma * (1 + z * z));
    }

    /**
     * The cumulative distribution function of a Cauchy distribution.
     *
     * @param mean  the median value (it has no mean!)
     * @param sigma the half-width at half-maximum (it has no deviation!)
     * @param x     the real argument
     * @return the Cauchy cdf value
     */
    public static double cdfCauchy(final double mean, final double sigma, final double x) {
        final double z = (x - mean) / sigma;
        return 0.5 + atan(z) / PI;
    }

    /**
     * The gaussian(mean,sigma) probability distribution.
     *
     * @param mean  the mean value
     * @param sigma the deviation
     * @param x     the real argument
     * @return the gaussian pdf value
     */
    public static double pdfGaussian(final double mean, final double sigma, final double x) {
        final double z = (x - mean) / sigma;
        return pdfNormal(z) / sigma;
    }

    /**
     * The cumulative distribution function of a gaussian distribution.
     *
     * @param mean  the mean value
     * @param sigma the deviation
     * @param x     the real argument
     * @return the gaussian cdf value
     */
    public static double cdfGaussian(final double mean, final double sigma, final double x) {
        final double z = (x - mean) / sigma;
        return cdfNormal(z) / sigma;
    }

    /**
     * The exponential distribution a*exp(-ax) with parameter a.
     *
     * @param a the parameter, to be positive
     * @param x the argument, to be positive
     * @return a*exp(-ax)
     */
    public static double pdfExponential(final double a, final double x) {
        if (x < 0) {
            getLogger().warn(String.format(ARGUMENT_NEGATIVE, x));
            return 0;
        }
        if (a < 0) {
            getLogger().warn(String.format(PARAMETER_NEGATIVE, a));
            return 0;
        }
        return a * exp(-a * x);
    }

    /**
     * The cumulative distribution function for an exponential distribution.
     *
     * @param a the parameter, to be positive
     * @param x the argument, to be positive
     * @return 1 - exp(-ax) the cdf
     */
    public static double cdfExponential(final double a, final double x) {
        if (x <= 0) {
            if (x < 0)
                getLogger().warn(String.format(ARGUMENT_NEGATIVE, x));
            return 0;
        }
        if (a < 0) {
            getLogger().warn(String.format(PARAMETER_NEGATIVE, a));
            return 0;
        }
        return 1 - exp(-a * x);
    }

    /**
     * Calculate the inverse cumulative distribution function for the
     * exponential distribution. That is for p=cdf(x) calculate x=invcdf(p).
     *
     * @param a the parameter, to be positive
     * @param p the probability 0 &le; p &le; 1
     * @return the x with cdf(x)=p
     */
    public static double quantileExponential(final double a, final double p) {
        checkProbabilty(p);
        if (p == 0) {
            return 0;
        } else if (p == 1) {
            return Double.MAX_VALUE;
        }
        return -log(1 - p) / a;
    }

    /**
     * The discrete poisson distribution for given probability a to happen k times.
     *
     * @param k the success factor
     * @param a the probability for one event
     * @return the poissonian
     */
    public static double pdfPoisson(final int k, final double a) {
        if (a <= 0) {
            if (a < 0)
                getLogger().warn(String.format(ARGUMENT_NEGATIVE, a));
            return 0;
        }
        if (k < 0) {
            return 0;
        }
        // return exp(k * log(a) - a - Gamma.lngamma(k + 1));
        double y = exp(-a);
        for (int j = 1; j <= k; j++) {
            y *= a / j;
        }
        return y;
    }

    /**
     * The cumulative poisson distribution function for given
     * probability a to happen less equals x times.
     *
     * @param x the argument
     * @param a the probability for one event
     * @return the cumulative poisson distribution
     */
    public static double cdfPoisson(final double x, final double a) {
        double cdf = 0;
        final int k = (int) x;
        for (int i = 1; i <= k; i++) {
            cdf += pdfPoisson(i, a);
        }
        return cdf;
    }

    /**
     * The discrete binomial distribution to get k success out of n trials
     * with probability a.
     *
     * @param n the number of trials
     * @param k the number of success
     * @param a the probability
     * @return binomial(n, k, a)
     */
    public static double pdfBinomial(final int n, final int k, final double a) {
        double binomi = 1;
        final double q = 1 - a;
        int kk = k;
        if (n < 0 || n < k || k < 0) {
            return 0;
        }
        if (0 < k && k < n) {
            if (k > n / 2) {
                kk = n - k; // Use symmetry
            }
            for (long j = 1; j <= kk; j++) {
                binomi = (binomi * (n - kk + j)) / j;
            }
        }
        binomi *= pow(a, k) * pow(q, n - k);
        return binomi;
    }

    /**
     * The cumulative discrete binomial distribution to get
     * less equal x successes out of n trials with probability a.
     *
     * @param x the maximal number of successes
     * @param n the number of trials
     * @param a the probability
     * @return binomial(n, k, a)
     */
    public static double cdfBinomial(final double x, final int n, final double a) {
        double cdf = 0;
        final int k = (int) x;
        for (int j = 0; j <= k; j++) {
            cdf += pdfBinomial(n, j, a);
        }
        return cdf;
    }

    /**
     * Calculate the beta distribution with parameters a,b for argument x.
     * <pre>
     * beta(a,b;x) = x**(a-1)*(1-x)**(b-1)*gamma(a+b)/(gamma(a)*gamma(b))
     * </pre>
     *
     * @param a first power to be positive
     * @param b second power to be positive
     * @param x the argument between 0 and 1
     * @return beta distribution
     */
    public static double pdfBeta(final double a, final double b, final double x) {
        checkUnit(x);
        return pow(x, a - 1) * pow(1 - x, b - 1) / beta(a, b);
    }

    /**
     * Calculate the cumulative beta distribution.
     *
     * @param a first power to be positive
     * @param b second power to be positive
     * @param x the argument between 0 and 1
     * @return cumulative beta distribution
     */
    public static double cdfBeta(final double a, final double b, final double x) {
        checkUnit(x);
        return IncompleteBeta.incBeta(x, a, b);
    }

    /**
     * Calculate the quantile xp for the beta distribution. E.g. if p=cdf(xp)
     * return xp.
     *
     * @param a first parameter of the beta function
     * @param b second parameter of the beta function
     * @param p the cdf
     * @return xp with cdf(xp)=p
     */
    public static double quantileBeta(final double a, final double b, final double p) {
        checkProbabilty(p);
        double x;
        // create the error function err(x) = cdf(x) - p
        // used for the Newton iteration.
        final Differentiable fct = new BetaError(a, b, p);
        final double x0 = 0, x1 = 1, f0 = fct.f(x0), f1 = fct.f(x1);
        if (f0 == 0) {
            return x0;
        } else if (f1 == 0) {
            return x1;
        }
        // find an initial starting guess
        x = (a * p + b * (1 - p)) / (a + b);
        x = quantile(x, fct);
        return x;
    }

    /**
     * Calculate the gamma distribution with shape k and scale t for argument x.
     *
     * @param k the shape parameter
     * @param t the scale parameter
     * @param x the argument
     * @return gamma(k, t;x)
     */
    public static double pdfGamma(final double k, final double t, final double x) {
        if (x < 0 || k < 0 || t < 0) {
            throw new IllegalArgumentException(String.format("g(%f,%f,%f)", k, t, x));
        }
        final double z = x / t;
        if (x == 0) {
            if (k == 1) {
                return 1. / t;
            }
            return 0;
        }
        return exp((k - 1) * log(z) - z - Gamma.lngamma(k)) / t;
    }

    /**
     * Calculate the cumulative gamma distribution with shape k and scale t for
     * argument x.
     *
     * @param k the shape parameter
     * @param t the scale parameter
     * @param x the argument
     * @return integral gamma(k,t;x)
     */
    public static double cdfGamma(final double k, final double t, final double x) {
        double p;
        final double y = x / t;

        if (x <= 0.0) {
            return 0.0;
        }

        if (y > k) {
            p = 1 - IncompleteGamma.regGammaQ(k, y);
        } else {
            p = IncompleteGamma.regGammaP(k, y);
        }

        return p;
    }

    /**
     * Calculate the quantile of the gamma distribution. E.g.
     * the inverse of the cumulative distribution function.
     *
     * @param k the shape parameter
     * @param t the scale parameter
     * @param p the cumulative probability
     * @return the quantile xp with cdf(xp)=p
     */
    public static double quantileGamma(final double k, final double t, final double p) {
        double x;
        checkProbabilty(p);
        if (p == 1.0) {
            return Double.MAX_VALUE;
        } else if (p == 0.0) {
            return 0.0;
        }
        // create the error function err(x) = cdf(x) - p
        // used for the Newton iteration.
        final Differentiable fct = new GammaError(k, p);
         /*
          * Find a first x0 starting guess for small,large and intermediate cases separately.
          */
        if (p < 0.05) {
            x = exp((lngamma(k) + log(p)) / k);
        } else if (p > 0.95) {
            x = -log(1 - p) + Gamma.lngamma(k);
        } else {
            final double sk = sqrt(k);
            x = quantileNormal(p);
            if (x < -sk) {
                x = k;
            } else {
                x = sk * x + k;
            }
        }
        x = quantile(x, fct);
        return t * x;
    }

    /**
     * Internal helper function to find the quantile xp with
     * cdf(xp)=p for a given p value, via a Newton method.
     *
     * @param x0  the initial starting value
     * @param err the error(x) = cdf(x) - p
     * @return xp with err(xp)=0
     */
    private static double quantile(final double x0, final Differentiable err) {
        final Function pdf = err.getDerivative();
        double g, e, t, f, d, x, y = x0;
        int n = 0;
        do {
            g = -1;
            x = y;
            e = err.f(x);
            if (e == 0) {
                return x;
            }
            d = e / max(2 * abs(e / x), pdf.f(x));
            t = y + g * d;
            try {
                f = err.f(t);
                while (abs(f) > abs(e) && g <= 1) {
                    g += .33;
                    t = y + g * d;
                    f = err.f(t);
                    // logger.warn("shrinking dx: " + g);
                }
            } catch (final Throwable error) {
                getLogger().warn("error for " + t);
                if (t > 1) {
                    g = (y - 1) / (2 * d);
                }
                t = y + g * d;
                getLogger().warn("using " + t);
            }

            if (y + g * d < 0) {
                getLogger().warn("dx too large: " + g * d);
                d = -x / (2 * g);
            }
            y = x + g * d;
        } while (!hasReachedAccuracy(y, x, PRECISION) && ++n < MAX_ITE);
        if (n >= MAX_ITE) {
            final double p = -err.f(0);
            final String msg = String.format("quantile(%f)=%f no convergence", p, x);
            getLogger().warn(msg);
        }
        return y;
    }

    /**
     * Calculate the student t-distribution with n degrees of freedom.
     *
     * @param n degree of freedom
     * @param x the argument
     * @return student(n;x)
     */
    public static double pdfStudent(final int n, final double x) {
        double y = (1 + x * x / n);
        final double nn = (1.0 + n) / 2;
        y = pow(y, -nn) * gamma(nn) / (sqrt(n * PI) * gamma(n / 2.0));
        return y;
    }

    ;

    /**
     * Calculate the cumulative student t-distribution with n degrees of freedom.
     *
     * @param n degree of freedom
     * @param x the argument
     * @return student(n;x)
     */
    public static double cdfStudent(final int n, final double x) {
        double y = sqrt(n + x * x);
        final double a = n / 2.0;
        final double b = a;
        final double z = (x + y) / (2 * y);
        y = IncompleteBeta.incBeta(z, a, b);
        return y;
    }

    /**
     * Calculate the quantile xp for the student distribution.
     * E.g. if p=cdf(xp) return xp.
     *
     * @param n degree of freedom for the student t-function
     * @param p the cdf
     * @return xp with cdf(xp)=p
     */
    public static double quantileStudent(final int n, final double p) {
        checkProbabilty(p);
        double x;
        // create the error function err(x) = cdf(x) - p
        // used for the Newton iteration.
        final Differentiable fct = new StudentError(n, p);
        final double x0 = 0, x1 = 1, f0 = fct.f(x0), f1 = fct.f(x1);
        if (f0 == 0) {
            return x0;
        } else if (f1 == 0) {
            return x1;
        }
        // find an initial starting guess
        // student goes for larger n like normal gaussian...
        x = quantileNormal(p);
        x = quantile(x, fct);
        return x;
    }

    /**
     * Calculate the Fischer f-distribution with n,m degrees of freedom.
     *
     * @param n numerator degree of freedom
     * @param d dominator degree of freedom
     * @param x the argument
     * @return fischer(n, d;x)
     */
    public static double pdfFischer(final int n, final int d, final double x) {
        double y = 0;
        // double beta = exp(-lngamma((n+d)/2.)+lngamma(n/2.)+lngamma(d/2.));
        if (x > 0) {
            y = pow((n * x) / d, n / 2.) / (x * pow(1. + (n * x) / d, (n + d) / 2.));
            y /= beta(n / 2., d / 2.);
        }
        return y;
    }

    /**
     * Calculate the cumulative Fischer f-distribution with n,m degrees of freedom.
     *
     * @param n numerator degree of freedom
     * @param d dominator degree of freedom
     * @param x the argument
     * @return fischer(n, m;x)
     */
    public static double cdfFischer(final int n, final int d, final double x) {
        double y = 0;
        if (x > 0) {
            y = n * x / (n * x + d);
            y = IncompleteBeta.incBeta(y, n / 2., d / 2.);
        }
        return y;
    }

    /**
     * Calculate the quantile xp for the student distribution.
     * E.g. if p=cdf(xp) return xp.
     *
     * @param n 1.st degree of freedom for the Fischer f-function
     * @param m 2.nd degree of freedom for the Fischer f-function
     * @param p the cdf
     * @return xp with cdf(xp)=p
     */
    public static double quantileFischer(final int n, final int m, final double p) {
        checkProbabilty(p);
        double x;
        // if(p<0.5) return 1.0/quantileFischer(m,n,1-p);
        // create the error function err(x) = cdf(x) - p
        // used for the Newton iteration.
        final Differentiable fct = new FischerError(n, m, p);
        final double x0 = 0, x1 = 1, f0 = fct.f(x0), f1 = fct.f(x1);
        if (f0 == 0) {
            return x0;
        } else if (f1 == 0) {
            return x1;
        }
        // find an initial starting guess
        x = 1;
        x = quantile(x, fct);
        return x;
    }

    ;

    /**
     * Internal helper for the continued fraction of the normal CDF
     * for large x greater than one. Formula 26.2.14 A &amp;S.
     */
    static class NormalCFLargeX extends ContinuedFraction {
        @Override
        protected double getA0(final double x) {
            return 0;
        }

        @Override
        protected double getAn(final int n, final double x) {
            return x;
        }

        @Override
        protected double getBn(final int n, final double x) {
            if (n == 1)
                return 1;
            return n - 1;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

    /**
     * Internal helper for the continued fraction of the normal CDF
     * for small x less than one. Formula 26.2.15 A &amp;S.
     */
    static class NormalCFSmallX extends ContinuedFraction {
        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.util.ContinuedFraction#getA0(double)
         */
        @Override
        protected double getA0(final double x) {
            return 0;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.util.ContinuedFraction#getAn(int, double)
         */
        @Override
        protected double getAn(final int n, final double x) {
            return (n << 1) - 1;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.util.ContinuedFraction#getBn(int, double)
         */
        @Override
        protected double getBn(final int n, final double x) {
            if (n == 1)
                return x;
            final double z = (n - 1) * x * x;
            if (n % 2 == 0) {
                return -z;
            }
            return z;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

    abstract static class PDFunction implements Function {
        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

    abstract static class PDDifferentiable implements Differentiable {
        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

    /**
     * Internal error for the Beta function quantile.
     */
    static class BetaError extends PDDifferentiable {
        final double a, b, p;

        public BetaError(final double a, final double b, final double p) {
            this.a = a;
            this.b = b;
            this.p = p;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return cdfBeta(a, b, x[0]) - p;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Differentiable#getDerivative()
         */
        @Override
        public Function getDerivative() {
            return new BetaPdf(a, b, p);
        }
    }

    /**
     * Internal helper for the Beta function quantile.
     */
    static class BetaPdf extends PDFunction {
        final double a, b;

        public BetaPdf(final double a, final double b, final double p) {
            this.a = a;
            this.b = b;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return pdfBeta(a, b, x[0]);
        }
    }

    /**
     * Internal error for the Gamma function quantile.
     */
    static class GammaError extends PDDifferentiable {
        final double k, p;

        GammaError(final double k, final double p) {
            this.k = k;
            this.p = p;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return cdfGamma(k, 1., x[0]) - p;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Differentiable#getDerivative()
         */
        @Override
        public Function getDerivative() {
            return new GammaPdf(k);
        }
    }

    /**
     * Internal helper for the Gamma function quantile.
     */
    static class GammaPdf extends PDFunction {
        final double k;

        GammaPdf(final double k) {
            this.k = k;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return pdfGamma(k, 1., x[0]);
        }
    }

    /**
     * Internal error for the Student t-function quantile.
     */
    static class StudentError extends PDDifferentiable {
        final double p;
        final int n;

        StudentError(final int n, final double p) {
            this.n = n;
            this.p = p;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return cdfStudent(n, x[0]) - p;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Differentiable#getDerivative()
         */
        @Override
        public Function getDerivative() {
            return new StudentPdf(n);
        }
    }

    /**
     * Internal helper for the Student t-function quantile.
     */
    static class StudentPdf extends PDFunction {
        final int n;

        StudentPdf(final int n) {
            this.n = n;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return pdfStudent(n, x[0]);
        }
    }

    /**
     * Internal error for the Fischer function quantile.
     */
    static class FischerError extends PDDifferentiable {
        final double p;
        final int n, m;

        FischerError(final int n, final int m, final double p) {
            this.n = n;
            this.m = m;
            this.p = p;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return cdfFischer(n, m, x[0]) - p;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Differentiable#getDerivative()
         */
        @Override
        public Function getDerivative() {
            return new FischerPdf(n, m);
        }
    }

    /**
     * Internal helper for the Fischer function quantile.
     */
    static class FischerPdf extends PDFunction {
        final int n, m;

        FischerPdf(final int n, final int m) {
            this.n = n;
            this.m = m;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return pdfFischer(n, m, x[0]);
        }
    }

}
 