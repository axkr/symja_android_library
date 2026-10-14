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
            return lerchContinuation(z, s, a, precision);
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

    /**
     * Riemann zeta function &zeta;(<i>s</i>) for a large real part of the argument, as the sum of
     * the Dirichlet series &sum; <i>n</i><sup>-<i>s</i></sup>.<p>
     *
     * <code>ApcomplexMath.zeta()</code> takes a number of terms which grows with the argument:
     * &zeta;(10<sup>10</sup>) doesn't return at the precision of a double, and from
     * 10<sup>20</sup> on the result is 0. With a real part of at least 0.7 times the precision the
     * series has no more than 31 terms above the last digit.
     *
     * @param s The argument.
     *
     * @return &zeta;(<i>s</i>), or <code>null</code> if the real part of the argument is too small for the series.
     */

    public static Apcomplex zetaOfLargeRealPart(Apcomplex s)
        throws ApfloatRuntimeException
    {
        long precision = s.precision();
        if (precision == Apfloat.INFINITE || s.real().signum() <= 0 || s.real().scale() < 2)
        {
            return null;
        }
        int radix = s.radix();
        double σ = s.real().doubleValue(),
               digits = (precision + 3) * Math.log(radix);
        // (N + 1)^-σ is below the last digit for N + 1 = 32
        if (!(σ * Math.log(32.0) >= digits))
        {
            return null;
        }
        // The digits of the imaginary part in front of the decimal point are no digits of the phase of n^-s
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision) + Math.max(0, s.imag().scale());
        Apcomplex exponent = ApfloatHelperShim.ensurePrecision(s, workingPrecision).negate(),
                  sum = new Apfloat(1, workingPrecision, radix);
        for (long n = 2; σ * Math.log(n) < digits + ApfloatHelperShim.EXTRA_PRECISION * Math.log(radix); n++)
        {
            sum = sum.add(ApcomplexMath.exp(exponent.multiply(ApfloatMath.log(new Apfloat(n, workingPrecision, radix)))));
        }
        return ApfloatHelperShim.limitPrecision(sum, precision);
    }

    // Up to this index the rational Bernoulli number of the library is taken
    private static final long MAX_RATIONAL_BERNOULLI = 60;

    /**
     * Bernoulli number <i>B</i><sub><i>n</i></sub> as a number of the precision, from
     * |<i>B</i><sub><i>n</i></sub>| = 2 <i>n</i>! &zeta;(<i>n</i>) / (2 &pi;)<sup><i>n</i></sup>.<p>
     *
     * <code>AprationalMath.bernoulli()</code> calculates the rational number, which takes 12
     * seconds for <i>n</i> = 1000.
     *
     * @param n The index, which is not negative.
     * @param precision The precision.
     *
     * @return <i>B</i><sub><i>n</i></sub>
     *
     * @see <a href="https://dlmf.nist.gov/24.4#E12">DLMF 25.6.2</a>
     */

    public static Apfloat bernoulli(long n, long precision)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        if (n < 0)
        {
            throw new IllegalArgumentException("Bernoulli number of a negative index");
        }
        if (n <= MAX_RATIONAL_BERNOULLI)
        {
            return org.apfloat.AprationalMath.bernoulli(n).precision(precision);
        }
        if ((n & 1) == 1)
        {
            return Apfloat.ZERO;
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        Apfloat index = new Apfloat(n, workingPrecision);
        Apcomplex zeta = zetaOfLargeRealPart(index);
        if (zeta == null)
        {
            zeta = ZetaWorkaround.zeta(index);
        }
        Apfloat twoPi = ApfloatMath.pi(workingPrecision).multiply(new Apfloat(2, workingPrecision)),
                value = ApfloatMath.gamma(new Apfloat(n + 1, workingPrecision)).multiply(new Apfloat(2, workingPrecision)).multiply(zeta.real()).divide(ApfloatMath.pow(twoPi, n));
        if ((n & 3) == 0)
        {
            value = value.negate();
        }
        return value.precision(precision);
    }

    // Largest number of terms with a negative real part of k + a, which are summed one by one
    private static final long MAX_REFLECTED_TERMS = 100000;

    /**
     * Lerch transcendent as the sum of <i>z</i><sup><i>k</i></sup> /
     * ((<i>k</i> + <i>a</i>)<sup>2</sup>)<sup><i>s</i>/2</sup>, which leaves out a term with
     * <i>k</i> + <i>a</i> = 0.<p>
     *
     * The square takes the sign of <i>k</i> + <i>a</i> away. The terms with a positive real part of
     * <i>k</i> + <i>a</i> are those of {@link #lerchPhi(Apcomplex, Apcomplex, Apcomplex)}; the
     * finitely many others have -(<i>k</i> + <i>a</i>) as the base of the power, and are real for
     * real arguments where the terms of that sum are not.
     *
     * @param z The argument.
     * @param s The exponent.
     * @param a The shift.
     *
     * @return The sum, to the smallest precision of the arguments.
     *
     * @throws ArithmeticException If the sum of the rest of the terms cannot be calculated, or the real part of the shift is below -100000.
     */

    public static Apcomplex lerchPhiOfSquares(Apcomplex z, Apcomplex s, Apcomplex a)
        throws ArithmeticException, ApfloatRuntimeException
    {
        // The principal value of the square root of (k + a)^2 is k + a for an angle in (-pi/2, pi/2]
        if (a.real().signum() > 0 || (a.real().signum() == 0 && a.imag().signum() > 0))
        {
            return lerchPhi(z, s, a);
        }
        long precision = Math.min(z.precision(), Math.min(s.precision(), a.precision()));
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Lerch transcendent to infinite precision", "lerchPhi.infinitePrecision");
        }
        Apfloat size = a.real().negate();
        if (size.compareTo(new Apfloat(MAX_REFLECTED_TERMS, Apfloat.INFINITE, size.radix())) > 0)
        {
            throw new ArithmeticException("Lerch transcendent with too many terms of a negative shift");
        }
        // The first k with an angle of k + a in (-pi/2, pi/2]
        long terms = ApfloatMath.ceil(size).longValue();
        if (size.isInteger() && a.imag().signum() <= 0)
        {
            terms++;
        }
        int radix = (z.isZero() ? a.radix() : z.radix());
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apcomplex zw = ApfloatHelperShim.ensurePrecision(z, workingPrecision),
                  sw = ApfloatHelperShim.ensurePrecision(s, workingPrecision).negate(),
                  aw = ApfloatHelperShim.ensurePrecision(a, workingPrecision),
                  power = new Apfloat(1, workingPrecision, radix),
                  sum = Apcomplex.ZERO;
        for (long k = 0; k < terms; k++)
        {
            Apcomplex base = h.add(aw, new Apfloat(k, workingPrecision, radix)).negate();
            if (!base.isZero())
            {
                sum = h.add(sum, h.multiply(power, h.pow(base, sw)));
            }
            power = h.multiply(power, zw);
        }
        if (!power.isZero())
        {
            Apcomplex shifted = a.add(new Apint(terms, radix));
            sum = h.add(sum, h.multiply(power, lerchPhi(z, s, new Apcomplex(shifted.real().precision(a.precision()), shifted.imag().signum() == 0 ? shifted.imag() : shifted.imag().precision(a.precision())))));
        }
        return ApfloatHelperShim.limitPrecision(sum, precision);
    }

    // The Lerch transcendent next to and outside of the unit circle: the first terms of the series, and the rest from the
    // Euler-Maclaurin formula or - where its Bernoulli series does not converge - from the Abel-Plana formula
    private static Apcomplex lerchContinuation(Apcomplex z, Apcomplex s, Apcomplex a, long precision)
    {
        int radix = z.radix();
        if (z.imag().signum() == 0 && z.real().equals(new Apfloat(1, z.precision(), radix)))
        {
            throw new ArithmeticException("Lerch transcendent of the argument 1");
        }
        long guarded = precision + ApfloatHelperShim.EXTRA_PRECISION + 5;
        Apcomplex log = ApcomplexMath.log(z.precision(ApfloatHelperShim.EXTRA_PRECISION));
        // The Bernoulli series has the ratio (|log z| / (2 pi))^2
        double ratio = Math.hypot(log.real().doubleValue(), log.imag().doubleValue()) / (2.0 * Math.PI),
               digits = guarded * Math.log(radix),
               // the highest derivative, and the shifted argument which keeps the expansion in 1 / A decreasing up to there
               order = Math.max(digits / -Math.log(Math.max(ratio, 1.0e-3)), 8.0),
               shift = Math.max(order, digits) / (2.0 * Math.PI) * 1.3 + 10.0;
        long start = Math.max(0, (long) Math.ceil(shift - a.real().doubleValue())),
             // outside of the unit circle the first terms and the rest are larger than their sum by z^n
             loss = (long) Math.ceil(start * Math.max(0.0, Math.log(ApcomplexMath.abs(z).doubleValue()) / Math.log(radix)));
        if (ratio < 0.75 && loss <= guarded)
        {
            Apcomplex previous = null;
            for (int attempt = 0; attempt < 3; attempt++)
            {
                Apcomplex result;
                try
                {
                    result = lerchEulerMaclaurin(z, s, a, start + 12L * attempt, guarded + 2 * loss + 10L * attempt, 2 * (int) order + 20);
                }
                catch (ArithmeticException ae)
                {
                    break;
                }
                if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
                {
                    return ApfloatHelperShim.limitPrecision(result, precision);
                }
                previous = result;
            }
        }
        return lerchAbelPlana(z, s, a, precision);
    }

    // sum_{k<n} z^k (k+a)^(-s) + z^n (int_0^oo g + g(0)/2 - sum_j B(2j)/(2j)! g^(2j-1)(0)) with g(x) = z^x (x+A)^(-s) and A = a + n
    private static Apcomplex lerchEulerMaclaurin(Apcomplex z, Apcomplex s, Apcomplex a, long n, long workingPrecision, int max)
    {
        int radix = z.radix();
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                two = new Apfloat(2, workingPrecision, radix);
        Apcomplex zw = ApfloatHelperShim.ensurePrecision(z, workingPrecision),
                  sw = ApfloatHelperShim.ensurePrecision(s, workingPrecision),
                  aw = ApfloatHelperShim.ensurePrecision(a, workingPrecision),
                  ns = sw.negate(),
                  log = h.log(zw),
                  sum = Apcomplex.ZERO,
                  power = one;
        for (long k = 0; k < n; k++)
        {
            sum = h.add(sum, h.multiply(power, h.exp(h.multiply(ns, h.log(h.add(aw, new Apfloat(k, workingPrecision, radix)))))));
            power = h.multiply(power, zw);
        }
        Apcomplex shifted = h.add(aw, new Apfloat(n, workingPrecision, radix)),
                  base = h.exp(h.multiply(ns, h.log(shifted))),
                  tail = h.add(lerchIntegral(h, shifted, sw, log, one), h.divide(base, two));
        // g^(m)(0) = sum_i C(m,i) L^(m-i) c(i) with c(i) = (-s) (-s-1) ... (-s-i+1) A^(-s-i)
        Apcomplex[] c = new Apcomplex[max + 1],
                    logPower = new Apcomplex[max + 1];
        c[0] = base;
        logPower[0] = one;
        for (int i = 1; i <= max; i++)
        {
            c[i] = h.divide(h.multiply(c[i - 1], h.subtract(ns, new Apfloat(i - 1, workingPrecision, radix))), shifted);
            logPower[i] = h.multiply(logPower[i - 1], log);
        }
        java.math.BigInteger[] tangent = tangentNumbers(max / 2 + 1);
        Apfloat factorial = two;
        long smallest = Long.MAX_VALUE;
        for (int j = 1; 2 * j - 1 <= max; j++)
        {
            int m = 2 * j - 1;
            Apcomplex derivative = Apcomplex.ZERO;
            Apfloat binomial = one;
            for (int i = 0; i <= m; i++)
            {
                derivative = h.add(derivative, h.multiply(h.multiply(binomial, logPower[m - i]), c[i]));
                binomial = h.divide(h.multiply(binomial, new Apfloat(m - i, workingPrecision, radix)), new Apfloat(i + 1, workingPrecision, radix));
            }
            // B(2j) = (-1)^(j-1) 2j T(j) / (4^j (4^j - 1))
            java.math.BigInteger four = java.math.BigInteger.ONE.shiftLeft(2 * j);
            Apfloat bernoulli = h.divide(new Apfloat(tangent[j].multiply(java.math.BigInteger.valueOf(2L * j)), workingPrecision, radix),
                                         new Apfloat(four.multiply(four.subtract(java.math.BigInteger.ONE)), workingPrecision, radix));
            Apcomplex term = h.divide(h.multiply(j % 2 == 0 ? bernoulli.negate() : bernoulli, derivative), factorial);
            if (!term.isZero())
            {
                if (smallest != Long.MAX_VALUE && term.scale() > smallest + 3)
                {
                    throw new ArithmeticException("Euler-Maclaurin terms grow");
                }
                smallest = Math.min(smallest, term.scale());
            }
            tail = h.subtract(tail, term);
            if (term.isZero() || term.scale() < tail.scale() - workingPrecision)
            {
                return h.add(sum, h.multiply(power, tail));
            }
            factorial = h.multiply(factorial, new Apfloat((2L * j + 1) * (2L * j + 2), workingPrecision, radix));
        }
        throw new ArithmeticException("Euler-Maclaurin series does not end");
    }

    // int_0^oo z^x (x+a)^(-s) dx = z^(-a) (-L)^(s-1) gamma(1-s, -a L) with L = log(z), and its continuation beyond the unit circle
    private static Apcomplex lerchIntegral(FixedPrecisionApfloatHelper h, Apcomplex a, Apcomplex s, Apcomplex log, Apfloat one)
    {
        Apcomplex minusLog = log.negate();
        return h.multiply(h.multiply(h.exp(h.multiply(a, log).negate()), h.exp(h.multiply(h.subtract(s, one), h.log(minusLog)))),
                          h.gamma(h.subtract(one, s), h.multiply(a, minusLog)));
    }

    // The tangent numbers T(1), ..., T(n): Brent and Harvey, Fast computation of Bernoulli, tangent and secant numbers
    static java.math.BigInteger[] tangentNumbers(int n)
    {
        java.math.BigInteger[] t = new java.math.BigInteger[n + 1];
        t[1] = java.math.BigInteger.ONE;
        for (int k = 2; k <= n; k++)
        {
            t[k] = t[k - 1].multiply(java.math.BigInteger.valueOf(k - 1));
        }
        for (int k = 2; k <= n; k++)
        {
            for (int j = k; j <= n; j++)
            {
                t[j] = t[j - 1].multiply(java.math.BigInteger.valueOf(j - k)).add(t[j].multiply(java.math.BigInteger.valueOf(j - k + 2)));
            }
        }
        return t;
    }

    // Abel-Plana: sum f(k) = f(0)/2 + int_0^oo f + i int_0^oo (f(it) - f(-it)) / (e^(2 pi t) - 1) dt for f(x) = z^x (x+a)^(-s)
    // and a positive real part of a, with a double exponential quadrature. Slow: thousands of logarithms
    private static Apcomplex lerchAbelPlana(Apcomplex z, Apcomplex s, Apcomplex a, long precision)
    {
        int radix = z.radix();
        // the first terms, until the real part of the shifted a is at least 1
        long first = Math.max(0, (long) Math.ceil(1.0 - a.real().doubleValue()));
        Apcomplex previous = null;
        for (int level = 4; level <= 11; level++)
        {
            long workingPrecision = precision + ApfloatHelperShim.EXTRA_PRECISION + 10L * (level - 4) + (long) Math.ceil(first * Math.max(0.0, Math.log10(ApcomplexMath.abs(z).doubleValue())));
            FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
            Apfloat one = new Apfloat(1, workingPrecision, radix),
                    two = new Apfloat(2, workingPrecision, radix),
                    twoPi = h.multiply(h.pi(), two),
                    halfPi = h.divide(h.pi(), two),
                    step = h.divide(one, new Apfloat(1L << level, workingPrecision, radix));
            Apcomplex zw = ApfloatHelperShim.ensurePrecision(z, workingPrecision),
                      sw = ApfloatHelperShim.ensurePrecision(s, workingPrecision),
                      aw = ApfloatHelperShim.ensurePrecision(a, workingPrecision),
                      i = new Apcomplex(Apfloat.ZERO, one),
                      log = h.log(zw),
                      ns = sw.negate(),
                      head = Apcomplex.ZERO,
                      power = one;
            for (long k = 0; k < first; k++)
            {
                head = h.add(head, h.multiply(power, h.exp(h.multiply(ns, h.log(h.add(aw, new Apfloat(k, workingPrecision, radix)))))));
                power = h.multiply(power, zw);
            }
            Apcomplex shifted = (first == 0 ? aw : h.add(aw, new Apfloat(first, workingPrecision, radix))),
                      sum = Apcomplex.ZERO;
            // z^(it) grows as e^(|arg z| t) against the e^(2 pi t) of the denominator
            double growth = Math.abs(log.imag().doubleValue());
            // t = e^(pi/2 sinh(u)), with the weight t pi/2 cosh(u), from the middle to both sides
            for (int direction = -1; direction <= 1; direction += 2)
            {
                int small = 0;
                for (long k = (direction < 0 ? 1 : 0); k < 100000; k++)
                {
                    Apfloat u = h.multiply(step, new Apfloat(direction * k, workingPrecision, radix)),
                            t = h.exp(h.multiply(halfPi, h.sinh(u))),
                            weight = h.multiply(h.multiply(t, halfPi), h.cosh(u)),
                            exponent = h.multiply(twoPi, t);
                    Apcomplex value = Apcomplex.ZERO;
                    // otherwise e^(2 pi t) is beyond all digits
                    if (exponent.scale() <= 4 || (2.0 * Math.PI - growth) * t.doubleValue() <= 2.4 * workingPrecision + 50.0)
                    {
                        Apcomplex it = new Apcomplex(Apfloat.ZERO, t),
                                  phase = h.exp(h.multiply(it, log)),
                                  numerator = h.subtract(h.multiply(phase, h.exp(h.multiply(ns, h.log(h.add(shifted, it))))),
                                                         h.multiply(h.divide(one, phase), h.exp(h.multiply(ns, h.log(h.subtract(shifted, it))))));
                        Apfloat denominator = h.subtract(h.exp(exponent), one);
                        // at a t below all digits the integrand is finite, and the weight is nothing
                        if (denominator.signum() != 0 && !numerator.isZero())
                        {
                            value = h.divide(numerator, denominator);
                        }
                    }
                    Apcomplex term = h.multiply(value, weight);
                    sum = h.add(sum, term);
                    small = (term.isZero() || (!sum.isZero() && term.scale() < sum.scale() - workingPrecision) ? small + 1 : 0);
                    if (small >= 3)
                    {
                        break;
                    }
                }
            }
            Apcomplex tail = h.add(h.add(h.divide(h.exp(h.multiply(ns, h.log(shifted))), two), lerchIntegral(h, shifted, sw, log, one)), h.multiply(h.multiply(i, sum), step)),
                      result = h.add(head, h.multiply(power, tail));
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
    }

    // The primes whose terms are summed directly, and taken out of the zeta functions
    private static final int[] SMALL_PRIMES = { 2, 3, 5, 7, 11, 13, 17, 19 };

    /**
     * The derivative &zeta;'(<i>s</i>) of the Riemann zeta function: the derivative of the
     * alternating series, with the reflection formula for a real part below 1/2. Next to the
     * other zeros of the denominator of that series it is the mean of the function on a circle
     * around <i>s</i> (Cauchy's integral formula with the trapezoidal rule).
     *
     * @param s The argument.
     *
     * @return &zeta;'(<i>s</i>)
     *
     * @throws ArithmeticException If <i>s</i> is 1.
     *
     * @see <a href="https://dlmf.nist.gov/25.2#E3">DLMF 25.2.3</a>
     * @see <a href="https://dlmf.nist.gov/25.4">DLMF 25.4</a>
     */

    public static Apcomplex zetaDerivative(Apcomplex s)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = s.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate zeta derivative to infinite precision", "zeta.infinitePrecision");
        }
        int radix = s.radix();
        double distance = Math.hypot(s.real().doubleValue() - 1.0, s.imag().doubleValue());
        if (distance == 0.0)
        {
            throw new ArithmeticException("Zeta derivative of one");
        }
        // The pole is the only singularity: with a radius of a quarter of its distance the rule gains log10(4) digits with a node
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        Apcomplex z = ApfloatHelperShim.ensurePrecision(s, workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                half = new Apfloat("0.5", workingPrecision, radix);
        boolean reflect = (z.real().compareTo(half) < 0);
        Apcomplex w = (reflect ? one.subtract(z) : z);
        // 1 - 2^(1 - w), the denominator of the alternating series, is also zero at 1 + 2 pi i k / log(2)
        double period = 2.0 * Math.PI / Math.log(2.0),
               imag = w.imag().doubleValue(),
               offset = Math.hypot(w.real().doubleValue() - 1.0, imag - period * Math.rint(imag / period));
        if (offset > 0.01 || Math.abs(imag) < 1.0)
        {
            Apcomplex[] sums = ZetaWorkaround.alternatingSums(w);
            Apcomplex result = sums[1];
            if (reflect)
            {
                // zeta(s) = chi(s) zeta(1 - s) with chi(s) = 2^s pi^(s - 1) sin(pi s / 2) gamma(1 - s): DLMF 25.4
                FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
                Apfloat pi = h.pi(),
                        two = new Apfloat(2, workingPrecision, radix);
                Apcomplex angle = h.multiply(h.multiply(pi, half), z),
                          sine = h.sin(angle),
                          cosine = h.cos(angle),
                          factor = h.multiply(h.multiply(h.pow(two, z), h.pow(pi, h.subtract(z, one))), h.gamma(w)),
                          logarithmic = h.subtract(h.log(h.multiply(two, pi)), h.digamma(w)),
                          // chi'(s) without the common factor
                          dchi = h.add(h.multiply(sine, logarithmic), h.multiply(h.multiply(pi, half), cosine));
                result = h.multiply(factor, h.subtract(h.multiply(dchi, sums[0]), h.multiply(sine, sums[1])));
            }
            if (s.imag().signum() == 0)
            {
                result = new Apcomplex(result.real());
            }
            return ApfloatHelperShim.limitPrecision(result, precision);
        }
        double radius = Math.min(0.5, distance / 4.0);
        try
        {
            return zetaDerivative(s, radius, precision);
        }
        catch (LossOfPrecisionException lope)
        {
            // a node of the rule at a trivial zero, where the reflection formula has no digits
            return zetaDerivative(s, 0.83 * radius, precision);
        }
    }

    private static Apcomplex zetaDerivative(Apcomplex s, double radius, long precision)
    {
        int radix = s.radix();
        // On the circle the function is larger than the derivative times the radius by about distance / radius next to the pole
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, ApfloatHelperShim.EXTRA_PRECISION + (long) Math.ceil(Math.max(0.0, -2.0 * Math.log10(radius))));
        int nodes = (int) Math.ceil((workingPrecision * Math.log10(radix) + 2.0) / Math.log10(4.0));
        nodes += nodes % 2;
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat r = new Apfloat(radius, workingPrecision, radix),
                angle = h.divide(h.pi(), new Apfloat(nodes, workingPrecision, radix));
        // the nodes are at the odd multiples of pi / nodes: none of them on the real axis for a real argument
        Apcomplex center = ApfloatHelperShim.ensurePrecision(s, workingPrecision),
                  direction = h.exp(new Apcomplex(Apfloat.ZERO, angle)),
                  step = h.multiply(direction, direction),
                  sum = Apcomplex.ZERO;
        boolean real = (s.imag().signum() == 0);
        // for a real argument the nodes of the lower half are the conjugates of the upper ones
        int count = (real ? nodes / 2 : nodes);
        for (int k = 0; k < count; k++)
        {
            Apcomplex value = h.divide(ZetaWorkaround.zeta(h.add(center, h.multiply(r, direction))), direction);
            if (real)
            {
                value = new Apcomplex(h.multiply(new Apfloat(2, workingPrecision, radix), value.real()));
            }
            sum = h.add(sum, value);
            direction = h.multiply(direction, step);
        }
        Apcomplex result = h.divide(sum, h.multiply(r, new Apfloat(nodes, workingPrecision, radix)));
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

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
