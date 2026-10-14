package org.matheclipse.external.apfloat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.LossOfPrecisionException;

/**
 * The zeros of the Riemann zeta function on the critical line, as the zeros of the real function
 * <code>Z(t) = e<sup>i &theta;(t)</sup> &zeta;(1/2 + i t)</code>.<p>
 *
 * The methods refine a zero which is known to lie between two numbers; counting the zeros, which
 * says which one it is, is not done here.
 *
 * @see <a href="https://dlmf.nist.gov/25.10#i">DLMF 25.10(i)</a>
 */

public class ApfloatZetaZeroMath
{
    // Below this height the Riemann-Siegel formula is not asked
    private static final double RIEMANN_SIEGEL_LIMIT = 5000.0;

    // From this height on the zeta values come from the summation of CriticalLineZ
    private static final double CRITICAL_LINE_LIMIT = 200.0;

    private ApfloatZetaZeroMath()
    {
    }

    /**
     * The function <code>Z(t)</code> from the zeta function and the Riemann-Siegel theta function,
     * calculated at the given precision without guard digits.
     *
     * @param t The argument.
     * @param precision The working precision.
     *
     * @return <code>Z(t)</code>
     */

    public static Apfloat z(Apfloat t, long precision)
        throws ApfloatRuntimeException
    {
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision);
        Apfloat half = new Apfloat("0.5", precision, t.radix());
        Apcomplex zeta = h.zeta(new Apcomplex(half, t)),
                  factor = h.exp(new Apcomplex(Apfloat.ZERO, theta(h, t)));
        return h.multiply(factor, zeta).real();
    }

    // The Riemann-Siegel theta function at the precision of the helper: Im(logGamma(1/4 + i t / 2)) - t / 2 log(pi)
    private static Apfloat theta(FixedPrecisionApfloatHelper h, Apfloat t)
    {
        long precision = h.precision();
        Apfloat two = new Apfloat(2, precision, t.radix()),
                quarter = new Apfloat("0.25", precision, t.radix()),
                logPi = ApfloatMath.log(ApfloatMath.pi(precision, t.radix()));
        Apcomplex logGamma = h.logGamma(new Apcomplex(quarter, t.divide(two)));
        return h.subtract(logGamma.imag(), h.divide(t, two).multiply(logPi));
    }

    /**
     * The zero of <code>Z(t)</code> between two numbers, with the secant method and a bisection
     * as safeguard: the iterates converge superlinearly, so that a handful of evaluations of
     * <code>Z</code> are needed instead of the 3.3 for every digit of a bisection.
     *
     * @param lower A number below the zero.
     * @param upper A number above the zero.
     * @param precision The precision of the result.
     *
     * @return The zero.
     *
     * @exception ArithmeticException If <code>Z</code> has the same sign at both numbers.
     */

    public static Apfloat zero(double lower, double upper, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision);
        Apfloat a = new Apfloat(lower, precision),
                b = new Apfloat(upper, precision),
                two = new Apfloat(2, precision);
        UnaryOperator<Apfloat> z;
        if (riemannSiegelReaches(a))
        {
            // a sum of sqrt(t / (2 pi)) terms
            z = ApfloatRiemannSiegelMath::riemannSiegelZ;
        }
        else if (upper >= CRITICAL_LINE_LIMIT)
        {
            // much faster at a large t than the general zeta function
            z = new CriticalLineZ(precision, upper)::z;
        }
        else
        {
            z = x -> z(x, precision);
        }
        // Converged at a couple of units in the last place of t: a secant step that small cannot be resolved at this
        // precision, and a looser test leaves the last digits unfinished
        Apfloat tolerance = ApfloatMath.pow(new Apfloat(10, precision), -precision).multiply(new Apfloat(2.0 * Math.max(1.0, upper), precision)),
                fa,
                fb;
        try
        {
            fa = z.apply(a);
            if (fa.signum() == 0)
            {
                return a;
            }
            fb = z.apply(b);
            if (fb.signum() == 0)
            {
                return b;
            }
        }
        catch (LossOfPrecisionException lope)
        {
            // An endpoint sits on the zero: zeta(1/2 + i t) underflowed to zero at the working precision
            return b;
        }
        if (fa.signum() == fb.signum())
        {
            throw new ArithmeticException("No change of sign between the numbers");
        }
        // The two most recent iterates of the secant method; the numbers a and b keep the zero between them
        Apfloat x0 = a,
                fx0 = fa,
                x1 = b,
                fx1 = fb;
        long maxIterations = Math.min(100000L, precision * 4 + 80);
        for (long i = 0; i < maxIterations; i++)
        {
            Apfloat denominator = fx1.subtract(fx0),
                    x2;
            if (denominator.signum() != 0)
            {
                x2 = x1.subtract(fx1.multiply(x1.subtract(x0)).divide(denominator));
                if (ApfloatMath.abs(x2.subtract(x1)).compareTo(tolerance) < 0)
                {
                    // Converged - possibly onto the endpoint which the last step became, which the test below would
                    // take for leaving the interval and answer with a slow bisection
                    return x2;
                }
                if (x2.compareTo(a) <= 0 || x2.compareTo(b) >= 0)
                {
                    x2 = a.add(b).divide(two);
                }
            }
            else
            {
                x2 = a.add(b).divide(two);
            }
            Apfloat fx2;
            try
            {
                fx2 = z.apply(x2);
            }
            catch (LossOfPrecisionException lope)
            {
                // zeta(1/2 + i x2) underflowed to zero at the working precision: x2 is the zero
                return x2;
            }
            if (fx2.signum() == 0 || ApfloatMath.abs(x2.subtract(x1)).compareTo(tolerance) < 0)
            {
                return x2;
            }
            if (ApfloatMath.abs(fx2).compareTo(ApfloatMath.abs(fx1)) >= 0
                && ApfloatMath.abs(x2.subtract(x1)).compareTo(tolerance.multiply(new Apfloat(1000))) < 0)
            {
                // Z no longer shrinks: the iterates are at the noise floor of the working precision
                return x1;
            }
            if (fx2.signum() == fa.signum())
            {
                a = x2;
                fa = fx2;
            }
            else
            {
                b = x2;
                fb = fx2;
            }
            x0 = x1;
            fx0 = fx1;
            x1 = x2;
            fx1 = fx2;
        }
        return x1;
    }

    /**
     * The zero of <code>Z(t)</code> between two numbers at which it has different signs, by
     * bisection with {@link #z(Apfloat,long)}.
     *
     * @param a A number below the zero.
     * @param b A number above the zero.
     * @param tolerance The length of the interval at which the bisection ends.
     * @param precision The working precision.
     *
     * @return The middle of the last interval.
     */

    public static Apfloat bisect(Apfloat a, Apfloat b, Apfloat tolerance, long precision)
        throws ApfloatRuntimeException
    {
        Apfloat two = new Apfloat(2, precision),
                fa = z(a, precision),
                middle = a.add(b).divide(two);
        long maxIterations = Math.min(100000L, precision * 4 + 80);
        for (long i = 0; i < maxIterations; i++)
        {
            middle = a.add(b).divide(two);
            Apfloat fm = z(middle, precision);
            if (fm.signum() == 0 || ApfloatMath.abs(b.subtract(a)).compareTo(tolerance) < 0)
            {
                return middle;
            }
            if (fm.signum() == fa.signum())
            {
                a = middle;
                fa = fm;
            }
            else
            {
                b = middle;
            }
        }
        return middle;
    }

    // Whether the Riemann-Siegel formula reaches the precision of t at t: the precision it can reach grows with the height
    private static boolean riemannSiegelReaches(Apfloat t)
    {
        if (t.doubleValue() < RIEMANN_SIEGEL_LIMIT)
        {
            return false;
        }
        try
        {
            ApfloatRiemannSiegelMath.riemannSiegelZ(t);
            return true;
        }
        catch (ArithmeticException ae)
        {
            return false;
        }
    }

    // Z(t) for a large t, from zeta(1/2 + i t) by Euler-Maclaurin summation.
    //
    // The direct sum runs to N ~ t / 2, so that t / (2 pi N) = 1 / pi and every Bernoulli term of the tail is about
    // 1 / pi^2 of the one before. Its terms k^(-1/2 - i t) are completely multiplicative, so only the primes need a
    // complex exponential; every other term is the product of two earlier ones. The logarithms and square roots of the
    // primes do not depend on t and are kept for the few evaluations which the search of a zero makes. The working
    // precision has guard digits for the phase t log(k) of every term.
    private static final class CriticalLineZ
    {
        private final long precision;
        private final int n;
        private final FixedPrecisionApfloatHelper h;
        private final int[] smallestFactor;
        private final Apfloat[] logPrime,
                                inverseRootPrime;
        private final Apfloat half,
                              two,
                              epsilon;
        private final List<Apfloat> bernoulliOverFactorial = new ArrayList<Apfloat>();

        CriticalLineZ(long precision, double tMax)
        {
            this.precision = precision;
            this.n = (int) Math.ceil(0.5 * tMax) + 30;
            long workingPrecision = precision + (long) Math.ceil(Math.log10(tMax * Math.log(n) + 10.0)) + 6;
            this.h = new FixedPrecisionApfloatHelper(workingPrecision);
            this.half = new Apfloat("0.5", workingPrecision);
            this.two = new Apfloat(2, workingPrecision);
            this.epsilon = ApfloatMath.pow(new Apfloat(10, workingPrecision), -workingPrecision);
            smallestFactor = new int[n + 1];
            logPrime = new Apfloat[n + 1];
            inverseRootPrime = new Apfloat[n + 1];
            for (int k = 2; k <= n; k++)
            {
                if (smallestFactor[k] == 0)
                {
                    for (long m = k; m <= n; m += k)
                    {
                        if (smallestFactor[(int) m] == 0)
                        {
                            smallestFactor[(int) m] = k;
                        }
                    }
                    Apfloat prime = new Apfloat(k, workingPrecision);
                    logPrime[k] = h.log(prime);
                    inverseRootPrime[k] = h.inverseRoot(prime, 2);
                }
            }
        }

        // The coefficient B(2j) / (2j)! of the Euler-Maclaurin tail, with B(2j) = (-1)^(j-1) 2j T(j) / (4^j (4^j - 1))
        private Apfloat bernoulliOverFactorial(int j)
        {
            if (bernoulliOverFactorial.size() < j)
            {
                int count = Math.max(2 * j, 40);
                BigInteger[] tangent = ApcomplexZetaMath.tangentNumbers(count);
                BigInteger factorial = BigInteger.ONE;
                bernoulliOverFactorial.clear();
                for (int i = 1; i <= count; i++)
                {
                    factorial = factorial.multiply(BigInteger.valueOf((2L * i - 1) * (2L * i)));
                    BigInteger four = BigInteger.ONE.shiftLeft(2 * i);
                    Apfloat numerator = new Apfloat(tangent[i].multiply(BigInteger.valueOf(2L * i)), h.precision()),
                            denominator = new Apfloat(four.multiply(four.subtract(BigInteger.ONE)).multiply(factorial), h.precision()),
                            quotient = h.divide(numerator, denominator);
                    bernoulliOverFactorial.add(i % 2 == 0 ? quotient.negate() : quotient);
                }
            }
            return bernoulliOverFactorial.get(j - 1);
        }

        // Z(t) at the precision which this object was made for
        Apfloat z(Apfloat argument)
        {
            Apfloat t = argument.precision(h.precision());
            Apcomplex[] power = new Apcomplex[n + 1];
            power[1] = Apcomplex.ONE;
            Apcomplex sum = Apcomplex.ONE;
            for (int k = 2; k <= n; k++)
            {
                int p = smallestFactor[k];
                if (p == k)
                {
                    Apfloat phase = h.multiply(t, logPrime[k]);
                    power[k] = new Apcomplex(h.multiply(inverseRootPrime[k], h.cos(phase)), h.multiply(inverseRootPrime[k], h.sin(phase)).negate());
                }
                else
                {
                    power[k] = h.multiply(power[p], power[k / p]);
                }
                if (k < n)
                {
                    sum = h.add(sum, power[k]);
                }
            }
            Apcomplex s = new Apcomplex(half, t);
            Apfloat bigN = new Apfloat(n, h.precision());
            Apcomplex powerN = power[n];
            // N^(1-s) / (s-1) + N^(-s) / 2
            sum = h.add(sum, h.divide(h.multiply(powerN, bigN), h.subtract(s, Apcomplex.ONE)));
            sum = h.add(sum, h.divide(powerN, two));
            // The Bernoulli tail: B(2j) / (2j)! s (s+1) ... (s+2j-2) N^(-s-2j+1)
            Apcomplex rising = s,
                      tailPower = h.divide(powerN, bigN);
            Apfloat nSquared = h.multiply(bigN, bigN),
                    previous = null;
            for (int j = 1; j <= 400; j++)
            {
                Apcomplex term = h.multiply(h.multiply(rising, tailPower), bernoulliOverFactorial(j));
                Apfloat size = h.abs(term);
                sum = h.add(sum, term);
                if (size.compareTo(epsilon) < 0 || (previous != null && size.compareTo(previous) > 0))
                {
                    break;
                }
                previous = size;
                rising = h.multiply(h.multiply(rising, h.add(s, new Apfloat(2 * j - 1))), h.add(s, new Apfloat(2 * j)));
                tailPower = h.divide(tailPower, nSquared);
            }
            Apfloat theta = theta(h, t),
                    value = h.subtract(h.multiply(h.cos(theta), sum.real()), h.multiply(h.sin(theta), sum.imag()));
            return value.precision(precision);
        }
    }
}
