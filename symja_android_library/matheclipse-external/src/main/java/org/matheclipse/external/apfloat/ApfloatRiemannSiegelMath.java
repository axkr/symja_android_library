package org.matheclipse.external.apfloat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * The Riemann-Siegel function <i>Z</i>(<i>t</i>) for a large argument, by the Riemann-Siegel
 * formula.<p>
 *
 * The formula is a sum of &radic;(<i>t</i>/2&pi;) terms and a correction, an asymptotic series
 * in 1/&radic;(<i>t</i>/2&pi;). It is implemented as described by J. Arias de Reyna, which
 * gives the number of terms of the correction for a precision, and is the way mpmath
 * evaluates the function. The precision that can be reached grows with the argument;
 * where the argument is too small for the precision of it, the methods of this class
 * throw an <code>ArithmeticException</code>.
 *
 * @see <a href="https://doi.org/10.1090/S0025-5718-2010-02426-3">J. Arias de Reyna, High
 *      precision computation of Riemann's zeta function by the Riemann-Siegel formula, I,
 *      Mathematics of Computation 80 (2011), 995-1009</a>
 * @see <a href="https://dlmf.nist.gov/25.10#ii">DLMF 25.10(ii)</a>
 */

public class ApfloatRiemannSiegelMath
{
    // Digits beyond the phases t log(n), which lose as many digits as t log(t) has
    private static final long GUARD_DIGITS = 25;

    // The precision which the bounds of the double version are calculated for
    private static final int DOUBLE_DIGITS = 16;

    // The constants b and c of the bound of the correction terms, and A, for the critical line
    private static final double B = 2.0;
    private static final double C = 3.0 / 4.44288;
    private static final double A = 3.0;

    // The Euler numbers E(0), E(2), E(4), ...
    private static final List<BigInteger> EULER = new ArrayList<BigInteger>();

    // The rows of the numbers d(n, k) of the correction terms, which are rational
    private static final List<Aprational[]> D = new ArrayList<Aprational[]>();

    // The Taylor coefficients of the function F of the correction, as last calculated
    private static Coefficients coefficients;

    // The logarithms and inverse square roots of the primes of the main sum, as last calculated
    private static Primes primes;

    private static volatile double[][] doubleCoefficients;

    private ApfloatRiemannSiegelMath()
    {
    }

    /**
     * Riemann-Siegel function <i>Z</i>(<i>t</i>) = <i>e</i><sup><i>i</i>&theta;(<i>t</i>)</sup>
     * &zeta;(1/2 + <i>i</i> <i>t</i>), to the precision of the argument.
     *
     * @param t The argument, which must be positive.
     *
     * @return <i>Z</i>(<i>t</i>)
     *
     * @throws ArithmeticException If the argument is too small for the Riemann-Siegel formula to reach its precision.
     */

    public static Apfloat riemannSiegelZ(Apfloat t)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = t.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Riemann-Siegel function to infinite precision", "riemannSiegel.infinitePrecision");
        }
        if (t.radix() != 10)
        {
            throw new ArithmeticException("Riemann-Siegel formula is implemented for radix 10");
        }
        double td = t.doubleValue();
        if (!(td >= 100.0) || Double.isInfinite(td))
        {
            throw new ArithmeticException("Riemann-Siegel formula needs a large argument");
        }

        // The numbers of terms, from bounds which need a few digits only
        double a = Math.sqrt(td / (2.0 * Math.PI)),
               logEps2 = -precision * Math.log(10.0) + Math.log(Math.sqrt(a) / 6.0);
        int terms = 1;
        while (Math.log(3.0 * C) + logGamma(0.5 * terms) - terms * Math.log(B * a) >= logEps2)
        {
            terms++;
            if (3.0 * terms >= 2.0 * a * a / 25.0)
            {
                throw new ArithmeticException("Riemann-Siegel formula cannot reach the precision at this argument");
            }
        }
        terms = Math.max(2, terms);
        if (3.0 * terms >= 2.0 * a * a / 25.0)
        {
            throw new ArithmeticException("Riemann-Siegel formula cannot reach the precision at this argument");
        }
        double logEps4 = logEps2 - Math.log(4.0 * terms) - Math.log(3.0 * terms);
        int derivatives = derivatives(logEps4, a, terms),
            taylorTerms = taylorTerms(logEps4, a, derivatives);

        long workingPrecision = precision + GUARD_DIGITS + (long) Math.ceil(Math.log10(td * Math.log(td)));
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision),
                two = new Apfloat(2, workingPrecision),
                pi = h.pi(),
                tw = t.precision(workingPrecision),
                t2pi = h.divide(tw, h.multiply(two, pi)),
                aw = h.sqrt(t2pi);
        long n = aw.truncate().longValue();
        // p = 1 - 2 (a - N)
        Apfloat p = h.subtract(one, h.multiply(two, h.subtract(aw, new Apfloat(n, workingPrecision))));

        // The derivatives of F at p, from its Taylor polynomial
        Apcomplex[] c = coefficients(taylorTerms, workingPrecision).clone();
        Apcomplex[] f = new Apcomplex[3 * terms];
        int degree = 2 * taylorTerms;
        for (int m = 0; m <= derivatives; m++)
        {
            Apcomplex sum = Apcomplex.ZERO;
            for (int k = degree - m - 1; k >= 0; k--)
            {
                sum = h.add(h.multiply(sum, p), c[k]);
            }
            f[m] = sum;
            for (int k = 0; k < degree - m - 1; k++)
            {
                c[k] = h.multiply(c[k + 1], new Apfloat(k + 1, workingPrecision));
            }
        }
        for (int m = derivatives + 1; m < f.length; m++)
        {
            f[m] = Apcomplex.ZERO;
        }

        // The correction: sum over k of a^(-k) sum over l of d(k, l) F^(3k-2l)(p) / (pi^(2k-l) (2i)^l)
        Apfloat[] piPower = new Apfloat[2 * terms + 1];
        piPower[0] = one;
        for (int k = 1; k < piPower.length; k++)
        {
            piPower[k] = h.multiply(piPower[k - 1], pi);
        }
        Apcomplex twoI = new Apcomplex(Apfloat.ZERO, two),
                  correction = Apcomplex.ZERO;
        Apfloat aPower = one,
                aInverse = h.divide(one, aw);
        for (int k = 0; k < terms; k++)
        {
            Aprational[] d = d(k);
            Apcomplex term = Apcomplex.ZERO,
                      twoIPower = Apcomplex.ONE;
            for (int l = 0; l <= 3 * k / 2; l++)
            {
                if (d[l].signum() != 0)
                {
                    Apcomplex summand = h.multiply(f[3 * k - 2 * l], d[l].precision(workingPrecision));
                    summand = h.divide(summand, h.multiply(twoIPower, piPower[2 * k - l]));
                    term = h.add(term, summand);
                }
                twoIPower = h.multiply(twoIPower, twoI);
            }
            correction = h.add(correction, h.multiply(term, aPower));
            aPower = h.multiply(aPower, aInverse);
        }
        // S3 = (-1)^(N-1) a^(-1/2) e^(-i (t/2 log(t/(2 pi)) - t/2 - pi/8))
        Apfloat tHalf = h.divide(tw, two),
                phase = h.subtract(h.subtract(h.multiply(tHalf, h.log(t2pi)), tHalf), h.divide(pi, new Apfloat(8, workingPrecision)));
        Apcomplex s3 = h.divide(h.exp(new Apcomplex(Apfloat.ZERO, phase.negate())), h.sqrt(aw));
        if ((n & 1) == 0)
        {
            s3 = s3.negate();
        }

        // The main sum over n^(-1/2 - i t), whose terms are completely multiplicative
        Primes primes = primes((int) n, workingPrecision);
        Apcomplex[] power = new Apcomplex[(int) n + 1];
        Apcomplex sum = Apcomplex.ONE;
        power[1] = Apcomplex.ONE;
        for (int k = 2; k <= n; k++)
        {
            int q = primes.smallestFactor[k];
            if (q == k)
            {
                power[k] = h.multiply(h.exp(new Apcomplex(Apfloat.ZERO, h.multiply(tw, primes.log[k]).negate())), primes.inverseRoot[k]);
            }
            else
            {
                power[k] = h.multiply(power[q], power[k / q]);
            }
            sum = h.add(sum, power[k]);
        }

        Apcomplex r = h.add(sum, h.multiply(correction, s3));
        Apfloat theta = ApcomplexZetaMath.riemannSiegelTheta(tw.precision(workingPrecision + GUARD_DIGITS));
        Apcomplex z = h.multiply(h.exp(new Apcomplex(Apfloat.ZERO, theta)), r);
        return h.multiply(two, z.real()).precision(precision);
    }

    /**
     * Riemann-Siegel function <i>Z</i>(<i>t</i>) in <code>double</code> arithmetic.<p>
     *
     * The terms of the formula have phases as large as <i>t</i> log(<i>t</i>), so that the
     * absolute error of the result is about {@link #doubleError(double)}, which grows with
     * the argument: 10<sup>-7</sup> at <i>t</i> = 10<sup>7</sup>.
     *
     * @param t The argument, which must be at least 10000.
     *
     * @return <i>Z</i>(<i>t</i>)
     *
     * @throws ArithmeticException If the argument is too small.
     */

    public static double riemannSiegelZ(double t)
        throws ArithmeticException
    {
        if (!(t >= 1.0e4) || Double.isInfinite(t))
        {
            throw new ArithmeticException("Riemann-Siegel formula needs a large argument");
        }
        double a = Math.sqrt(t / (2.0 * Math.PI)),
               logEps2 = -DOUBLE_DIGITS * Math.log(10.0) + Math.log(Math.sqrt(a) / 6.0);
        int terms = 1;
        while (Math.log(3.0 * C) + logGamma(0.5 * terms) - terms * Math.log(B * a) >= logEps2)
        {
            terms++;
        }
        terms = Math.max(2, terms);
        if (3.0 * terms >= 2.0 * a * a / 25.0)
        {
            throw new ArithmeticException("Riemann-Siegel formula cannot reach the precision at this argument");
        }
        double logEps4 = logEps2 - Math.log(4.0 * terms) - Math.log(3.0 * terms);
        int derivatives = derivatives(logEps4, a, terms),
            taylorTerms = taylorTerms(logEps4, a, derivatives);
        long n = (long) Math.floor(a);
        double p = 1.0 - 2.0 * (a - n);

        // The derivatives of F at p
        double[][] coefficients = doubleCoefficients(taylorTerms);
        int degree = coefficients[0].length;
        double[] cRe = coefficients[0].clone(),
                 cIm = coefficients[1].clone(),
                 fRe = new double[3 * terms],
                 fIm = new double[3 * terms];
        for (int m = 0; m <= derivatives; m++)
        {
            double re = 0.0,
                   im = 0.0;
            for (int k = degree - m - 1; k >= 0; k--)
            {
                re = re * p + cRe[k];
                im = im * p + cIm[k];
            }
            fRe[m] = re;
            fIm[m] = im;
            for (int k = 0; k < degree - m - 1; k++)
            {
                cRe[k] = (k + 1) * cRe[k + 1];
                cIm[k] = (k + 1) * cIm[k + 1];
            }
        }

        // The correction
        double correctionRe = 0.0,
               correctionIm = 0.0,
               aPower = 1.0;
        for (int k = 0; k < terms; k++)
        {
            Aprational[] d = d(k);
            double termRe = 0.0,
                   termIm = 0.0;
            for (int l = 0; l <= 3 * k / 2; l++)
            {
                // divided by pi^(2k-l) (2i)^l, where 1/i^l is 1, -i, -1, i
                double factor = d[l].doubleValue() / (Math.pow(Math.PI, 2 * k - l) * Math.pow(2.0, l)),
                       re = fRe[3 * k - 2 * l] * factor,
                       im = fIm[3 * k - 2 * l] * factor;
                switch (l & 3)
                {
                    case 0:
                        termRe += re;
                        termIm += im;
                        break;
                    case 1:
                        termRe += im;
                        termIm -= re;
                        break;
                    case 2:
                        termRe -= re;
                        termIm -= im;
                        break;
                    default:
                        termRe -= im;
                        termIm += re;
                }
            }
            correctionRe += termRe * aPower;
            correctionIm += termIm * aPower;
            aPower /= a;
        }
        // e^(i theta) S3 = (-1)^(N-1) a^(-1/2) e^(i delta): the large phases of the two cancel
        double t2 = t * t,
               delta = 1.0 / (48.0 * t) + 7.0 / (5760.0 * t * t2),
               remainder = (correctionRe * Math.cos(delta) - correctionIm * Math.sin(delta)) / Math.sqrt(a);
        if ((n & 1) == 0)
        {
            remainder = -remainder;
        }

        double theta = 0.5 * t * Math.log(t / (2.0 * Math.PI)) - 0.5 * t - Math.PI / 8.0 + delta,
               sum = 0.0;
        for (long k = 1; k <= n; k++)
        {
            sum += Math.cos(theta - t * Math.log((double) k)) / Math.sqrt((double) k);
        }
        return 2.0 * (sum + remainder);
    }

    /**
     * An estimate of the absolute error of {@link #riemannSiegelZ(double)}.
     *
     * @param t The argument.
     *
     * @return The estimate.
     */

    public static double doubleError(double t)
    {
        // Every one of the sqrt(t/(2 pi)) terms has a phase of the size of t log(t)
        double a = Math.sqrt(t / (2.0 * Math.PI));
        return 1.0e-15 + 2.3e-16 * t * Math.log(t) * Math.sqrt(a);
    }

    // The real and the imaginary parts of the Taylor coefficients of F
    private static double[][] doubleCoefficients(int taylorTerms)
    {
        double[][] c = doubleCoefficients;
        if (c == null || c[0].length < 2 * taylorTerms)
        {
            Apcomplex[] coefficients = coefficients(Math.max(taylorTerms, 40), 2 * DOUBLE_DIGITS);
            c = new double[2][coefficients.length - 4];
            for (int k = 0; k < c[0].length; k++)
            {
                c[0][k] = coefficients[k].real().doubleValue();
                c[1][k] = coefficients[k].imag().doubleValue();
            }
            doubleCoefficients = c;
        }
        return c;
    }

    // The number M of derivatives of F which the terms need
    private static int derivatives(double logEps4, double a, int terms)
    {
        double aux1 = Math.log(126.0657606 * A) - logEps4,
               aux2 = (2.0 * Math.log(Math.PI) + Math.log(a)) / 3.0 - Math.log(2.0 * Math.PI) / 2.0;
        int m = 3 * terms - 3;
        while (m > 1 && aux1 < m * aux2 + (logGamma(m + 1.0) - logGamma(m / 3.0 + 2.0)) / 2.0 - logGamma((m + 1.0) / 2.0))
        {
            m--;
        }
        return m;
    }

    // The number J of terms of the Taylor series of F
    private static int taylorTerms(double logEps4, double a, int derivatives)
    {
        double logH1 = logEps4 - Math.log(632.0 * A),
               h2 = Math.PI * Math.PI * a * Math.sqrt(3.0) * Math.E * Math.E,
               logH2 = logH1 + (derivatives - 1) / 3.0 * Math.log(h2 / ((double) derivatives * derivatives)) - Math.log(derivatives),
               logH3 = Math.min(logH1, logH2);
        int j = 12;
        while (j * Math.log(2.0 * Math.PI) - logGamma(j + 1.0) > logH3)
        {
            j++;
        }
        return j;
    }

    // log(Gamma(x)) for x > 0, to the few digits which the bounds need
    private static double logGamma(double x)
    {
        double shift = 0.0;
        while (x < 10.0)
        {
            shift += Math.log(x);
            x += 1.0;
        }
        double x2 = x * x;
        return (x - 0.5) * Math.log(x) - x + 0.5 * Math.log(2.0 * Math.PI) + 1.0 / (12.0 * x) - 1.0 / (360.0 * x * x2) + 1.0 / (1260.0 * x2 * x2 * x) - shift;
    }

    // The Euler number E(2n)
    private static synchronized BigInteger euler(int n)
    {
        while (EULER.size() <= n)
        {
            // The sum over k of binomial(2m, 2k) E(2k) is zero
            int m = EULER.size();
            BigInteger sum = BigInteger.ZERO,
                       binomial = BigInteger.ONE;
            for (int k = 0; k < m; k++)
            {
                sum = sum.add(binomial.multiply(EULER.get(k)));
                // binomial(2m, 2k + 2) from binomial(2m, 2k)
                binomial = binomial.multiply(BigInteger.valueOf((long) (2 * m - 2 * k) * (2 * m - 2 * k - 1)))
                                   .divide(BigInteger.valueOf((long) (2 * k + 1) * (2 * k + 2)));
            }
            EULER.add(m == 0 ? BigInteger.ONE : sum.negate());
        }
        return EULER.get(n);
    }

    // The numbers d(n, k) for 0 <= k <= 3n/2, for the critical line
    private static synchronized Aprational[] d(int n)
    {
        while (D.size() <= n)
        {
            int row = D.size();
            Aprational[] d = new Aprational[3 * row / 2 + 1];
            if (row == 0)
            {
                d[0] = Apint.ONE;
            }
            else
            {
                Aprational[] previous = D.get(row - 1);
                for (int k = 0; k < d.length; k++)
                {
                    int m = 3 * row - 2 * k;
                    if (m != 0)
                    {
                        // d(n, k) = d(n-1, k) / (4m) - (m+1) d(n-1, k-2)
                        Aprational value = Apint.ZERO;
                        if (k < previous.length)
                        {
                            value = previous[k].divide(new Apint(4L * m));
                        }
                        if (k >= 2 && k - 2 < previous.length)
                        {
                            value = value.subtract(previous[k - 2].multiply(new Apint(m + 1)));
                        }
                        d[k] = value;
                    }
                    else
                    {
                        // d(n, k) = -sum over r < k of (-1)^(k-r) d(n, r) (2k-2r)! / (k-r)!
                        Aprational value = Apint.ZERO;
                        for (int r = 0; r < k; r++)
                        {
                            Apint quotient = Apint.ONE;
                            for (int i = k - r + 1; i <= 2 * k - 2 * r; i++)
                            {
                                quotient = quotient.multiply(new Apint(i));
                            }
                            Aprational add = d[r].multiply(quotient);
                            value = ((k - r) & 1) == 0 ? value.subtract(add) : value.add(add);
                        }
                        d[k] = value;
                    }
                }
            }
            D.add(d);
        }
        return D.get(n);
    }

    private static class Coefficients
    {
        int taylorTerms;
        long precision;
        Apcomplex[] c;
    }

    // The Taylor coefficients c(0), ..., c(2J-1) of
    // F(z) = (e^(pi i (z^2/2 + 3/8)) - i sqrt(2) cos(pi z/2)) / (2 cos(pi z))
    private static synchronized Apcomplex[] coefficients(int taylorTerms, long precision)
    {
        if (coefficients != null && coefficients.taylorTerms >= taylorTerms && coefficients.precision >= precision)
        {
            return coefficients.c;
        }
        // The two sums of a coefficient are as large as 4^n, and cancel
        int newJ = taylorTerms + 2;
        long workingPrecision = precision + (long) (1.3 * newJ) + 10;
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision),
                two = new Apfloat(2, workingPrecision),
                pi = h.pi();
        Apfloat[] piPower = new Apfloat[2 * newJ + 1];
        piPower[0] = one;
        for (int k = 1; k < piPower.length; k++)
        {
            piPower[k] = h.multiply(piPower[k - 1], pi);
        }
        // v(n) = (-1)^n E(2n) pi^(2n) / (2n)! and w(n) = pi^n / (n! 2^n)
        Apfloat[] v = new Apfloat[newJ + 1],
                  w = new Apfloat[2 * newJ + 1];
        Apfloat factorial = one;
        for (int k = 0; k <= 2 * newJ; k++)
        {
            if (k > 0)
            {
                factorial = h.multiply(factorial, new Apfloat(2L * k, workingPrecision));
            }
            // factorial is k! 2^k
            w[k] = h.divide(piPower[k], factorial);
        }
        factorial = one;
        for (int k = 0; k <= newJ; k++)
        {
            if (k > 0)
            {
                factorial = h.multiply(factorial, new Apfloat((2L * k - 1) * (2L * k), workingPrecision));
            }
            BigInteger e = euler(k);
            Apfloat value = h.divide(h.multiply(new Apfloat(e, workingPrecision), piPower[2 * k]), factorial);
            v[k] = (k & 1) == 0 ? value : value.negate();
        }
        Apfloat mu = h.divide(h.sqrt(two), two);
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one),
                  nu = h.divide(h.exp(new Apcomplex(Apfloat.ZERO, h.divide(h.multiply(new Apfloat(3, workingPrecision), pi), new Apfloat(8, workingPrecision)))), two);
        Apcomplex[] iPower = { Apcomplex.ONE, i, Apcomplex.ONE.negate(), i.negate() };
        Apcomplex[] c = new Apcomplex[2 * newJ];
        for (int n = 0; n < newJ; n++)
        {
            // P1(n) = (-1)^(n+1) i sum of (-1)^k v(k) w(2n-2k), P2(n) = sum of i^(n-k) v(k) w(n-k)
            Apfloat sum1 = Apfloat.ZERO;
            Apcomplex sum2 = Apcomplex.ZERO;
            for (int k = 0; k <= n; k++)
            {
                Apfloat product = h.multiply(v[k], w[2 * n - 2 * k]);
                sum1 = (k & 1) == 0 ? h.add(sum1, product) : h.subtract(sum1, product);
                sum2 = h.add(sum2, h.multiply(iPower[(n - k) & 3], h.multiply(v[k], w[n - k])));
            }
            Apcomplex p1 = h.multiply(i, (n & 1) == 0 ? sum1.negate() : sum1);
            c[2 * n] = h.add(h.multiply(p1, mu), h.multiply(nu, sum2));
            c[2 * n + 1] = Apcomplex.ZERO;
        }
        Coefficients result = new Coefficients();
        result.taylorTerms = taylorTerms;
        result.precision = precision;
        result.c = c;
        coefficients = result;
        return c;
    }

    private static class Primes
    {
        long precision;
        int[] smallestFactor;
        Apfloat[] log;
        Apfloat[] inverseRoot;
    }

    private static synchronized Primes primes(int n, long precision)
    {
        if (primes != null && primes.smallestFactor.length > n && primes.precision >= precision)
        {
            return primes;
        }
        // Some more than asked for: the next arguments are near this one
        int size = n + 16;
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision);
        Primes result = new Primes();
        result.precision = precision;
        result.smallestFactor = new int[size + 1];
        result.log = new Apfloat[size + 1];
        result.inverseRoot = new Apfloat[size + 1];
        for (int k = 2; k <= size; k++)
        {
            if (result.smallestFactor[k] == 0)
            {
                for (long m = k; m <= size; m += k)
                {
                    if (result.smallestFactor[(int) m] == 0)
                    {
                        result.smallestFactor[(int) m] = k;
                    }
                }
                Apfloat q = new Apfloat(k, precision);
                result.log[k] = h.log(q);
                result.inverseRoot[k] = h.inverseRoot(q, 2);
            }
        }
        primes = result;
        return result;
    }
}
