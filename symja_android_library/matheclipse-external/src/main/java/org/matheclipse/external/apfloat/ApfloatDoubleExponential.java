package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatInterruptedException;
import org.apfloat.ApfloatMath;

/**
 * Double exponential quadrature in arbitrary precision: tanh-sinh on a finite interval, exp-sinh
 * on a half-infinite one and sinh-sinh on the whole real line.<p>
 *
 * The step is halved until two estimates agree to the requested number of digits. An integrable
 * singularity at a finite end is never sampled: the abscissas next to it are computed from their
 * distance to the end, so that the integrand can be given that distance with all its digits.<p>
 *
 * A quadrature which runs on a thread that is interrupted stops with an
 * {@link ApfloatInterruptedException}.
 */

public final class ApfloatDoubleExponential
{
    /**
     * The integrand.
     */

    @FunctionalInterface
    public interface Sampler
    {
        /**
         * The value of the integrand.
         *
         * @param x The abscissa: next to a finite end with more digits than the working precision, and an exact zero at the center of the whole real line. An exact number has no precision in apfloat, so an integrand which combines it with constants gives those a precision.
         *
         * @return The value at <code>x</code>, or <code>null</code> if there is no numerical value.
         */

        Apcomplex value(Apfloat x);
    }

    /**
     * The integral did not converge or the integrand had no numerical value.
     */

    public static final class Failure
        extends Exception
    {
        private static final long serialVersionUID = 1L;

        Failure(String message)
        {
            super(message, null, false, false);
        }
    }

    // The step is halved at most this many times
    private static final int MAX_LEVELS = 10;

    // A finite end must carry this many digits more than the precision: the estimates are compared three digits past it, and the samples next to the end round at the end's last digit
    private static final long END_GUARD_DIGITS = 5;

    private final Sampler f;
    private final long precision;
    private final long workPrecision;
    private final Apfloat halfPi;
    private final Apfloat epsilon;

    private ApfloatDoubleExponential(Sampler f, long precision)
    {
        this.f = f;
        this.precision = precision;
        this.workPrecision = precision + 15;
        this.halfPi = ApfloatMath.pi(this.workPrecision).divide(new Apfloat(2, this.workPrecision));
        this.epsilon = ApfloatMath.pow(new Apfloat(10, this.workPrecision), -(precision + 3));
    }

    /**
     * The integral of <code>f</code> from <code>a</code> to <code>b</code>.
     *
     * @param f The integrand.
     * @param a The lower end, <code>null</code> for minus infinity.
     * @param b The upper end, <code>null</code> for infinity.
     * @param precision The number of correct digits wanted.
     *
     * @return The integral, with <code>precision</code> digits.
     *
     * @throws IllegalArgumentException If a finite end carries fewer than <code>precision + 5</code> digits: the estimates can't agree to the precision then.
     * @throws Failure If the estimates don't converge or a sample has no numerical value.
     */

    public static Apcomplex integrate(Sampler f, Apfloat a, Apfloat b, long precision)
        throws Failure
    {
        checkEnd(a, precision);
        checkEnd(b, precision);
        return new ApfloatDoubleExponential(f, precision).integrate(a, b);
    }

    private static void checkEnd(Apfloat end, long precision)
    {
        if (end != null && end.precision() < precision + END_GUARD_DIGITS)
        {
            throw new IllegalArgumentException("An end with " + end.precision() + " digits; the integral to " + precision + " digits needs " + (precision + END_GUARD_DIGITS));
        }
    }

    private Apcomplex integrate(Apfloat a, Apfloat b)
        throws Failure
    {
        Apcomplex previous = null;
        Apcomplex sum = Apcomplex.ZERO;
        Apfloat h = new Apfloat(1, this.workPrecision);
        for (int level = 0; level <= MAX_LEVELS; level++)
        {
            // The new abscissas are the odd multiples of h; level 0 takes every integer
            int step = (level == 0 ? 1 : 2),
                start = (level == 0 ? 0 : 1);
            sum = sum.add(sweep(a, b, h, start, step, 1));
            sum = sum.add(sweep(a, b, h, Math.max(start, 1), step, -1));
            Apcomplex estimate = sum.multiply(h);
            if (previous != null)
            {
                Apfloat difference = ApcomplexMath.abs(estimate.subtract(previous)),
                        scale = ApcomplexMath.abs(estimate);
                if (difference.compareTo(this.epsilon.multiply(scale.signum() == 0 ? Apfloat.ONE : scale)) <= 0 ||
                    difference.signum() == 0)
                {
                    return estimate.precision(this.precision);
                }
            }
            previous = estimate;
            h = h.divide(new Apfloat(2, this.workPrecision));
        }
        throw new Failure("no convergence");
    }

    // The weighted samples at t = sign*k*h, k = start, start+step, ..., until they are negligible
    private Apcomplex sweep(Apfloat a, Apfloat b, Apfloat h, int start, int step, int sign)
        throws Failure
    {
        Apcomplex sum = Apcomplex.ZERO;
        // The largest weighted sample so far: a term is negligible relative to it
        Apfloat largest = Apfloat.ZERO;
        int negligible = 0;
        for (int k = start; ; k += step)
        {
            ApfloatHelperShim.checkInterrupted();
            Apfloat t = h.multiply(new Apfloat(sign * (long) k, this.workPrecision));
            if (ApfloatMath.abs(t).compareTo(new Apfloat(8, this.workPrecision)) > 0)
            {
                return sum;
            }
            Apcomplex term = weightedSample(a, b, t);
            if (term == null)
            {
                // The abscissa merged with an end of the interval
                return sum;
            }
            sum = sum.add(term);
            Apfloat magnitude = ApcomplexMath.abs(term);
            if (magnitude.compareTo(largest) > 0)
            {
                largest = magnitude;
            }
            if (magnitude.compareTo(this.epsilon.multiply(largest)) <= 0)
            {
                if (++negligible >= 3)
                {
                    return sum;
                }
            }
            else
            {
                negligible = 0;
            }
        }
    }

    // The precision of an abscissa end +- distance which keeps the working precision of the distance: an end of zero needs no extra digits
    private long guardedDigits(Apfloat end, Apfloat distance)
    {
        if (end.signum() == 0)
        {
            return this.workPrecision;
        }
        return this.workPrecision + Math.max(0L, end.scale() - distance.scale());
    }

    // w(t)*f(x(t)), or null if x(t) can't be told apart from a finite end
    private Apcomplex weightedSample(Apfloat a, Apfloat b, Apfloat t)
        throws Failure
    {
        Apfloat two = new Apfloat(2, this.workPrecision),
                u = this.halfPi.multiply(ApfloatMath.sinh(t)),
                du = this.halfPi.multiply(ApfloatMath.cosh(t)),
                x,
                weight;
        long digits = this.workPrecision;
        if (a != null && b != null)
        {
            // x = (a+b)/2 + (b-a)/2*tanh(u), the distance to the nearer end is (b-a)/(1+E^(2|u|))
            Apfloat half = b.subtract(a).divide(two),
                    e = ApfloatMath.exp(ApfloatMath.abs(u).multiply(two)),
                    distance = b.subtract(a).divide(Apfloat.ONE.add(e));
            if (distance.signum() == 0 ||
                distance.compareTo(ApfloatMath.abs(b.subtract(a)).multiply(ApfloatMath.pow(new Apfloat(10, this.workPrecision), -2 * this.workPrecision))) < 0)
            {
                return null;
            }
            Apfloat nearEnd = (u.signum() >= 0 ? b : a);
            digits = guardedDigits(nearEnd, distance);
            x = (u.signum() >= 0 ? b.precision(digits).subtract(distance) : a.precision(digits).add(distance));
            if (x.compareTo(a) <= 0 || x.compareTo(b) >= 0)
            {
                // The abscissa rounded onto an end, where the integrand may be singular
                return null;
            }
            Apfloat cosh = ApfloatMath.cosh(u);
            weight = half.multiply(du).divide(cosh.multiply(cosh));
        }
        else if (a != null || b != null)
        {
            // x = a + E^u or x = b - E^u
            Apfloat e = ApfloatMath.exp(u),
                    end = (a != null ? a : b);
            if (end.signum() != 0 &&
                e.compareTo(ApfloatMath.abs(end).multiply(ApfloatMath.pow(new Apfloat(10, this.workPrecision), -this.workPrecision))) < 0)
            {
                // The abscissa merged with the finite end
                return null;
            }
            digits = guardedDigits(end, e);
            x = (a != null ? a.precision(digits).add(e) : b.precision(digits).subtract(e));
            if (x.compareTo(end) == 0)
            {
                return null;
            }
            weight = du.multiply(e);
        }
        else
        {
            // x = Sinh(u)
            x = ApfloatMath.sinh(u);
            weight = du.multiply(ApfloatMath.cosh(u));
        }
        if (weight.signum() == 0)
        {
            return Apcomplex.ZERO;
        }
        // An abscissa of exact operands, a + 1 on a half-infinite interval, is exact: the integrand can't be evaluated to infinite precision (a zero has no precision to cap)
        if (x.precision() > digits)
        {
            x = x.precision(digits);
        }
        Apcomplex value = this.f.value(x);
        if (value == null)
        {
            throw new Failure("not numerical");
        }
        return value.multiply(weight);
    }
}
