package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;

/**
 * Functions around the Riemann zeta function for {@link Apcomplex} arguments, which
 * <code>org.apfloat.ApcomplexMath</code> doesn't have or has for a part of the arguments only.<p>
 *
 * The precision of the result is the smallest precision of the arguments, less the digits
 * which the argument itself cannot determine.
 */

public class ApcomplexZetaMath
{
    private ApcomplexZetaMath()
    {
    }

    // The largest number of terms by which the second argument is shifted to a positive real part
    private static final long MAX_SHIFT = 1000000;

    /**
     * Polylogarithm Li<sub>&nu;</sub>(<i>z</i>).<p>
     *
     * <code>ApcomplexMath.polylog()</code> fails for a complex order with an integer real part
     * and a positive argument: the bound of its remainder is a power of two exact numbers
     * there. The real part of the order is moved off the integer then, by less than the last
     * digit of the result can see.
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return Li<sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the result is infinite.
     *
     * @see <a href="https://dlmf.nist.gov/25.12#ii">DLMF 25.12(ii)</a>
     */

    public static Apcomplex polylog(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        try
        {
            return ApcomplexMath.polylog(ν, z);
        }
        catch (InfiniteExpansionException iee)
        {
            long precision = Math.min(ν.precision(), z.precision());
            if (!ν.real().isInteger() || ν.imag().signum() == 0 || precision == Apfloat.INFINITE)
            {
                throw iee;
            }
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
            Apfloat shift = ApfloatMath.scale(new Apfloat(1, workingPrecision, ν.radix()), -2 * workingPrecision),
                    real = ν.real().precision(Apfloat.INFINITE).add(shift).precision(3 * workingPrecision);
            Apcomplex result = ApcomplexMath.polylog(new Apcomplex(real, ApfloatHelperShim.ensurePrecision(ν.imag(), workingPrecision)),
                                                     ApfloatHelperShim.ensurePrecision(z, workingPrecision));
            return ApfloatHelperShim.limitPrecision(result, precision);
        }
    }

    /**
     * Hurwitz zeta function &zeta;(<i>s</i>, <i>a</i>) = &sum;<sub>k&ge;0</sub> (<i>k</i> + <i>a</i>)<sup>-s</sup>,
     * also for a second argument with a real part which is not positive: there the first terms
     * of the sum are added to the function of the shifted argument, with the principal value of
     * the powers.
     *
     * @param s The first argument.
     * @param a The second argument.
     *
     * @return &zeta;(<i>s</i>, <i>a</i>)
     *
     * @throws ArithmeticException If <i>s</i> is 1 or <i>a</i> is a nonpositive integer.
     *
     * @see <a href="https://dlmf.nist.gov/25.11#E1">DLMF 25.11.1</a>
     * @see <a href="https://dlmf.nist.gov/25.11#E4">DLMF 25.11.4</a>, the shift of the second argument
     */

    public static Apcomplex hurwitzZeta(Apcomplex s, Apcomplex a)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return zeta(s, a, false);
    }

    /**
     * The generalized zeta function &sum;<sub>k&ge;0</sub> ((<i>k</i> + <i>a</i>)<sup>2</sup>)<sup>-s/2</sup>,
     * where a term with <i>k</i> + <i>a</i> = 0 is left out.<p>
     *
     * For a second argument with a positive real part this is the Hurwitz zeta function. For
     * the other ones it is the function which Mathematica calls <code>Zeta[s, a]</code>, as
     * opposed to its <code>HurwitzZeta[s, a]</code>.
     *
     * @param s The first argument.
     * @param a The second argument.
     *
     * @return The generalized zeta function.
     *
     * @throws ArithmeticException If <i>s</i> is 1.
     *
     * @see <a href="https://reference.wolfram.com/language/ref/Zeta.html">Zeta in the Wolfram Language</a>
     */

    public static Apcomplex generalizedZeta(Apcomplex s, Apcomplex a)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return zeta(s, a, true);
    }

    private static Apcomplex zeta(Apcomplex s, Apcomplex a, boolean evenPowers)
    {
        long precision = Math.min(s.precision(), a.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate zeta to infinite precision", "zeta.infinitePrecision");
        }
        if (a.real().signum() > 0)
        {
            return ApcomplexMath.zeta(s, a);
        }
        Apfloat shift = a.real().negate().floor().add(ApfloatHelperShim.ONES[a.radix()]);
        if (shift.compareTo(new Apint(MAX_SHIFT, a.radix())) > 0)
        {
            throw new ArithmeticException("Zeta of a too negative second argument");
        }
        long m = shift.longValue();
        // The powers have any phase, so the terms can cancel
        return GuardDigitsHelper.evaluate((x, y) -> shiftedZeta(x, y, m, evenPowers), s, a, precision,
                                          ApfloatHelperShim.EXTRA_PRECISION, 16 * ApfloatHelperShim.EXTRA_PRECISION);
    }

    // zeta(s, a + m) and the first m terms of the sum
    private static Apcomplex shiftedZeta(Apcomplex s, Apcomplex a, long m, boolean evenPowers)
    {
        int radix = a.radix();
        Apint two = new Apint(2, radix);
        Apcomplex exponent = (evenPowers ? s.divide(two).negate() : s.negate()),
                  sum = ApcomplexMath.zeta(s, a.add(new Apint(m, radix)));
        for (long k = 0; k < m; k++)
        {
            Apcomplex base = a.add(new Apint(k, radix));
            if (base.real().signum() == 0 && base.imag().signum() == 0)
            {
                if (evenPowers)
                {
                    continue;
                }
                throw new ArithmeticException("Zeta of a nonpositive integer second argument");
            }
            sum = sum.add(pow(evenPowers ? base.multiply(base) : base, exponent));
        }
        return sum;
    }

    // An integer power of a negative number is a real number, which the general power only approximates
    private static Apcomplex pow(Apcomplex z, Apcomplex w)
    {
        if (w.imag().signum() == 0 && w.real().isInteger() && w.real().scale() <= 18)
        {
            return ApcomplexMath.pow(z, w.real().longValue());
        }
        return ApcomplexMath.pow(z, w);
    }

    /**
     * Riemann-Siegel theta function &theta;(<i>t</i>) = Im log&Gamma;(1/4 + <i>i t</i>/2) - <i>t</i>/2 log &pi;
     * for a real argument.<p>
     *
     * The two terms cancel next to a zero of the function. The digits which they cancel are
     * digits which the argument doesn't determine, and the precision of the result is less by
     * that many.
     *
     * @param t The argument.
     *
     * @return &theta;(<i>t</i>)
     *
     * @see <a href="https://dlmf.nist.gov/25.10#E2">DLMF 25.10.2</a>
     */

    public static Apfloat riemannSiegelTheta(Apfloat t)
        throws ApfloatRuntimeException
    {
        long precision = t.precision();
        if (t.signum() == 0)
        {
            return t;
        }
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Riemann-Siegel theta to infinite precision", "theta.infinitePrecision");
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        int radix = t.radix();
        Apint two = new Apint(2, radix);
        t = ApfloatHelperShim.ensurePrecision(t, workingPrecision);
        Apfloat halfT = t.divide(two),
                quarter = new Apfloat(1, workingPrecision, radix).divide(new Apint(4, radix)),
                phase = ApcomplexMath.logGamma(new Apcomplex(quarter, halfT)).imag(),
                term = halfT.multiply(ApfloatMath.log(ApfloatMath.pi(workingPrecision, radix)));
        return limit(phase.subtract(term), Math.max(phase.scale(), term.scale()), precision);
    }

    /**
     * Riemann-Siegel theta function for a complex argument, the analytic continuation
     * &theta;(<i>t</i>) = <i>i</i>/2 (log&Gamma;(1/4 - <i>i t</i>/2) - log&Gamma;(1/4 + <i>i t</i>/2)) - <i>t</i>/2 log &pi;.
     *
     * @param t The argument.
     *
     * @return &theta;(<i>t</i>)
     *
     * @throws ArithmeticException If one of the two gamma functions has a pole.
     */

    public static Apcomplex riemannSiegelTheta(Apcomplex t)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (t.imag().signum() == 0)
        {
            return riemannSiegelTheta(t.real());
        }
        long precision = t.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Riemann-Siegel theta to infinite precision", "theta.infinitePrecision");
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        int radix = t.radix();
        Apint two = new Apint(2, radix);
        t = ApfloatHelperShim.ensurePrecision(t, workingPrecision);
        Apfloat quarter = new Apfloat(1, workingPrecision, radix).divide(new Apint(4, radix));
        Apcomplex halfI = new Apcomplex(ApfloatHelperShim.ZEROS[radix], new Apfloat(1, workingPrecision, radix).divide(two)),
                  halfIT = t.multiply(halfI),
                  gammas = halfI.multiply(ApcomplexMath.logGamma(quarter.subtract(halfIT)).subtract(ApcomplexMath.logGamma(quarter.add(halfIT)))),
                  term = t.divide(two).multiply(ApfloatMath.log(ApfloatMath.pi(workingPrecision, radix))),
                  result = gammas.subtract(term);
        long scale = Math.max(gammas.scale(), term.scale());
        return new Apcomplex(limit(result.real(), scale, precision), limit(result.imag(), scale, precision));
    }

    // The difference with the precision less the digits which were cancelled from terms of the scale
    private static Apfloat limit(Apfloat difference, long scale, long precision)
    {
        if (difference.signum() == 0)
        {
            return difference;
        }
        long cancelled = Math.max(0, scale - difference.scale());
        return ApfloatHelperShim.limitPrecision(difference, Math.max(1, precision - cancelled));
    }

    /**
     * Stieltjes constant &gamma;<sub><i>n</i></sub>(<i>a</i>): the coefficients of the Laurent
     * series &zeta;(<i>s</i>, <i>a</i>) = 1 / (<i>s</i> - 1) + &sum; (-1)<sup><i>n</i></sup>
     * &gamma;<sub><i>n</i></sub>(<i>a</i>) (<i>s</i> - 1)<sup><i>n</i></sup> / <i>n</i>!.<p>
     *
     * The constant is log(<i>a</i>)<sup><i>n</i></sup> / (2 <i>a</i>) -
     * log(<i>a</i>)<sup><i>n</i>+1</sup> / (<i>n</i> + 1) and twice the integral from 0 to
     * infinity of the real part of (<i>x</i> / <i>a</i> - <i>i</i>)
     * log(<i>a</i> - <i>i x</i>)<sup><i>n</i></sup> / ((1 + (<i>x</i> / <i>a</i>)<sup>2</sup>)
     * (<i>e</i><sup>2&pi;<i>x</i></sup> - 1)), divided by <i>a</i> - the way mpmath
     * calculates it. The integral is a double exponential quadrature.
     *
     * @param n The index, which must not be negative.
     * @param a The second argument of the zeta function, which must be positive.
     *
     * @return &gamma;<sub><i>n</i></sub>(<i>a</i>), to the precision of <i>a</i>.
     *
     * @throws IllegalArgumentException If <i>n</i> is negative.
     * @throws ArithmeticException If <i>a</i> is not positive.
     *
     * @see <a href="https://dlmf.nist.gov/25.2#E4">DLMF 25.2.4</a>, the series for <i>a</i> = 1
     */

    public static Apfloat stieltjesGamma(long n, Apfloat a)
        throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException
    {
        if (n < 0)
        {
            throw new IllegalArgumentException("Negative index of a Stieltjes constant");
        }
        if (a.signum() <= 0)
        {
            throw new ArithmeticException("Stieltjes constant of a non-positive argument");
        }
        long precision = a.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Stieltjes constant to infinite precision", "stieltjes.infinitePrecision");
        }
        if (n == 0)
        {
            return ApfloatMath.digamma(a).negate();
        }
        int radix = a.radix();
        Apfloat previous = null;
        // The step of the quadrature is halved, and the digits are doubled for the logarithm to the power n
        for (int level = 4; level <= 12; level++)
        {
            long workingPrecision = precision + ApfloatHelperShim.EXTRA_PRECISION + (long) Math.sqrt(n) + 10L * (level - 4);
            FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
            Apfloat aw = a.precision(workingPrecision),
                    one = new Apfloat(1, workingPrecision, radix),
                    two = new Apfloat(2, workingPrecision, radix),
                    twoPi = h.multiply(h.pi(), two),
                    halfPi = h.divide(h.pi(), two),
                    step = h.divide(one, new Apfloat(1L << level, workingPrecision, radix)),
                    sum = Apfloat.ZERO;
            // x = e^(pi/2 sinh(t)), with the weight x pi/2 cosh(t), from the middle to both sides
            for (int direction = -1; direction <= 1; direction += 2)
            {
                int small = 0;
                for (long k = (direction < 0 ? 1 : 0); k < 100000; k++)
                {
                    Apfloat t = h.multiply(step, new Apfloat(direction * k, workingPrecision, radix)),
                            x = h.exp(h.multiply(halfPi, h.sinh(t))),
                            weight = h.multiply(h.multiply(x, halfPi), h.cosh(t)),
                            xa = h.divide(x, aw),
                            exponent = h.multiply(twoPi, x);
                    Apfloat value;
                    if (exponent.scale() > 4 && exponent.doubleValue() > 2.4 * workingPrecision + 50.0)
                    {
                        // e^(2 pi x) is beyond all digits
                        value = Apfloat.ZERO;
                    }
                    else
                    {
                        // Re((x/a - i) log(a - i x)^n) / ((1 + (x/a)^2) (e^(2 pi x) - 1))
                        Apcomplex power = h.pow(h.log(new Apcomplex(aw, x.negate())), n),
                                  numerator = h.multiply(new Apcomplex(xa, one.negate()), power);
                        Apfloat denominator = h.multiply(h.add(one, h.multiply(xa, xa)), h.subtract(h.exp(exponent), one));
                        // At an x below all digits the integrand is finite, and the weight is nothing
                        value = (denominator.signum() == 0 ? Apfloat.ZERO : h.divide(numerator.real(), denominator));
                    }
                    Apfloat term = h.multiply(value, weight);
                    sum = h.add(sum, term);
                    small = (term.signum() == 0 || (sum.signum() != 0 && term.scale() < sum.scale() - workingPrecision) ? small + 1 : 0);
                    if (small >= 3)
                    {
                        break;
                    }
                }
            }
            Apfloat integral = h.multiply(sum, step),
                    log = h.log(aw),
                    logPower = h.pow(log, n),
                    result = h.add(h.subtract(h.divide(logPower, h.multiply(two, aw)), h.divide(h.multiply(logPower, log), new Apfloat(n + 1, workingPrecision, radix))),
                                   h.divide(h.multiply(two, integral), aw));
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return result.precision(precision);
            }
            previous = result;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
    }

    /**
     * Lerch transcendent &Phi;(<i>z</i>, <i>s</i>, <i>a</i>) = &sum; <i>z</i><sup><i>k</i></sup> /
     * (<i>k</i> + <i>a</i>)<sup><i>s</i></sup> inside the unit circle, as the sum of the series
     * with the principal value of the powers.
     *
     * @param z The argument, with |<i>z</i>| at most 19/20.
     * @param s The exponent.
     * @param a The shift, which must not be a non-positive integer.
     *
     * @return &Phi;(<i>z</i>, <i>s</i>, <i>a</i>), to the smallest precision of the arguments.
     *
     * @throws ArithmeticException If the argument is too near to the unit circle or outside of it, or <i>a</i> is a non-positive integer.
     *
     * @see <a href="https://dlmf.nist.gov/25.14#E1">DLMF 25.14.1</a>
     */

    public static Apcomplex lerchPhi(Apcomplex z, Apcomplex s, Apcomplex a)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(z.precision(), Math.min(s.precision(), a.precision()));
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Lerch transcendent to infinite precision", "lerchPhi.infinitePrecision");
        }
        if (a.imag().signum() == 0 && a.real().signum() <= 0 && a.real().isInteger())
        {
            throw new ArithmeticException("Lerch transcendent of a non-positive integer shift");
        }
        double size = ApcomplexMath.abs(z).doubleValue();
        if (!(size <= 0.95))
        {
            throw new ArithmeticException("Lerch series next to or outside of the unit circle");
        }
        int radix = (a.isZero() ? 10 : a.radix());
        // The rest after a term is at most the term times 1 / (1 - |z|)
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apcomplex zw = ApfloatHelperShim.ensurePrecision(z, workingPrecision),
                  sw = ApfloatHelperShim.ensurePrecision(s, workingPrecision).negate(),
                  aw = ApfloatHelperShim.ensurePrecision(a, workingPrecision),
                  power = new Apfloat(1, workingPrecision, radix),
                  sum = Apcomplex.ZERO;
        long largest = Long.MIN_VALUE;
        // From here on the factors (k + a)^(-s) change slowly against the powers of z
        double settled = 8.0 + 2.0 * ApcomplexMath.abs(a).doubleValue() + 4.0 * ApcomplexMath.abs(s).doubleValue() / (1.0 - size);
        for (long k = 0; ; k++)
        {
            if (k > 1000000)
            {
                throw new ArithmeticException("Lerch series does not converge");
            }
            Apcomplex term = h.multiply(power, h.pow(h.add(aw, new Apfloat(k, workingPrecision, radix)), sw));
            sum = h.add(sum, term);
            if (!term.isZero())
            {
                largest = Math.max(largest, term.scale());
            }
            if (zw.isZero() || (k > settled && (term.isZero() || term.scale() + 2 < sum.scale() - workingPrecision)))
            {
                break;
            }
            power = h.multiply(power, zw);
        }
        if (!sum.isZero() && largest - sum.scale() > ApfloatHelperShim.EXTRA_PRECISION - 5)
        {
            throw new LossOfPrecisionException("Lerch series loses its digits");
        }
        return ApfloatHelperShim.limitPrecision(sum, precision);
    }

    // The primes whose terms are summed directly, and taken out of the zeta functions
    private static final int[] SMALL_PRIMES = { 2, 3, 5, 7, 11, 13, 17, 19 };

    /**
     * Prime zeta function <i>P</i>(<i>s</i>) = &sum; <i>p</i><sup>-<i>s</i></sup> over the
     * primes, for Re(<i>s</i>) &gt; 1.<p>
     *
     * It is the sum of &mu;(<i>k</i>) log(&zeta;(<i>k s</i>)) / <i>k</i>. The primes below 20
     * are summed as they are, and their factors are taken out of the zeta functions: what is
     * left of a zeta function is 1 and a number of the size of 23<sup>-<i>k s</i></sup>,
     * and the sum over <i>k</i> ends early.
     *
     * @param s The argument.
     *
     * @return <i>P</i>(<i>s</i>), to the precision of the argument.
     *
     * @throws ArithmeticException If the real part of the argument is not above 1.
     *
     * @see <a href="https://dlmf.nist.gov/25.2#E11">DLMF 25.2.11</a>, the product over the primes
     */

    public static Apcomplex primeZeta(Apcomplex s)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = s.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate prime zeta function to infinite precision", "primeZeta.infinitePrecision");
        }
        int radix = s.radix();
        double real = s.real().doubleValue();
        if (!(real > 1.0))
        {
            throw new ArithmeticException("Prime zeta series needs a real part above 1");
        }
        // The result is of the size of 2^(-s), and every term is accurate to the last digit of 1
        long workingPrecision = precision + ApfloatHelperShim.EXTRA_PRECISION + (long) (0.31 * Math.min(real, 1.0e6));
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix);
        Apcomplex sw = ApfloatHelperShim.ensurePrecision(s, workingPrecision),
                  sum = Apcomplex.ZERO;
        Apcomplex[] primePower = new Apcomplex[SMALL_PRIMES.length];
        for (int i = 0; i < SMALL_PRIMES.length; i++)
        {
            primePower[i] = h.exp(h.multiply(sw, h.log(new Apfloat(SMALL_PRIMES[i], workingPrecision, radix))).negate());
            sum = h.add(sum, primePower[i]);
        }
        // Terms until 23^(-k Re(s)) is below the last digit
        long terms = (long) Math.ceil(workingPrecision * Math.log(radix) / (real * Math.log(23.0))) + 1;
        for (long k = 1; k <= terms; k++)
        {
            int mu = moebius(k);
            if (mu == 0)
            {
                continue;
            }
            Apfloat index = new Apfloat(k, workingPrecision, radix);
            Apcomplex rest = h.zeta(h.multiply(sw, index));
            for (Apcomplex power : primePower)
            {
                rest = h.multiply(rest, h.subtract(one, h.pow(power, k)));
            }
            Apcomplex term = h.divide(h.log(rest), index);
            sum = (mu > 0 ? h.add(sum, term) : h.subtract(sum, term));
        }
        return ApfloatHelperShim.limitPrecision(sum, precision);
    }

    private static int moebius(long k)
    {
        int result = 1;
        for (long p = 2; p * p <= k; p++)
        {
            if (k % p == 0)
            {
                k /= p;
                if (k % p == 0)
                {
                    return 0;
                }
                result = -result;
            }
        }
        return (k > 1 ? -result : result);
    }
}
