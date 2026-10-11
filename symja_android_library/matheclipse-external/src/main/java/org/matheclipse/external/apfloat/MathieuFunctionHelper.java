package org.matheclipse.external.apfloat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatInterruptedException;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;

/**
 * The Mathieu functions <code>MathieuC</code>, <code>MathieuS</code> and their derivatives: the
 * even and the odd solution of
 *
 * <pre>
 * y''(z) + (a - 2*q*Cos(2*z))*y(z) == 0
 * </pre>
 *
 * for an arbitrary - not only a characteristic - value <code>a</code>, complex arguments and any
 * working precision.
 *
 * <p>
 * Both are the two halves of one Floquet solution
 * <code>f(z) = Sum(c(k)*Exp(I*(nu+2*k)*z), {k,-Infinity,Infinity})</code>:
 *
 * <pre>
 * MathieuC(a, q, z) = (f(z) + f(-z))/2   = Sum(c(k)*Cos((nu+2*k)*z), k)
 * MathieuS(a, q, z) = (f(z) - f(-z))/(2*I) = Sum(c(k)*Sin((nu+2*k)*z), k)
 * </pre>
 *
 * which is what makes <code>MathieuC(a,0,z) == Cos(Sqrt(a)*z)</code> and
 * <code>MathieuS(a,0,z) == Sin(Sqrt(a)*z)</code>, and <code>MathieuS</code> imaginary for real
 * arguments inside an instability gap just as <code>Sin(Sqrt(a)*z)</code> is for a negative
 * <code>a</code>.
 *
 * <p>
 * The characteristic exponent comes from the monodromy of the equation,
 * <code>Cos(Pi*nu) == 2*y1(Pi/2)*y2'(Pi/2) - 1</code>, with the basic solutions <code>y1</code>
 * (<code>y(0)==1, y'(0)==0</code>) and <code>y2</code> (<code>y(0)==0, y'(0)==1</code>) integrated
 * by Taylor steps. <code>ArcCos</code> loses half of the digits next to an integer exponent, so
 * this stage runs at twice the working precision. The coefficients follow from the three term
 * recurrence <code>(a-(nu+2*k)^2)*c(k) == q*(c(k-1)+c(k+1))</code>, whose minimal solution is
 * reached by backward recurrence from both ends. The exponent is only fixed up to an even
 * integer, and is taken next to <code>Sqrt(a)</code>: the largest coefficient is then
 * <code>c(0)</code>, where the two recurrences meet, and neither of them has to run through
 * indices at which the solution it follows is the one that dies out.
 *
 * <p>
 * Off the real axis the terms of the series grow like <code>Exp(2*k*Abs(Im(z)))</code>: more of
 * them are needed, and for a positive <code>q</code> they cancel - the function oscillates
 * there. The series is summed again with the digits it lost, up to {@link #MAX_EXTRA_DIGITS}.
 *
 * <p>
 * Everything that depends on <code>a</code> and <code>q</code> only is cached, so a plot over
 * <code>z</code> pays for the exponent once.
 */
final class MathieuFunctionHelper
{

    /** Which of the four functions is asked for. */
    enum Kind
    {
        C, C_PRIME, S, S_PRIME
    }

    /** Digits carried in addition to the precision asked for. */
    static final int GUARD_DIGITS = 10;

    /** Most digits added for the cancellation of the series off the real axis. */
    private static final long MAX_EXTRA_DIGITS = 1000;

    /** Most coefficients of the series on one side. */
    private static final int MAX_COEFFICIENTS = 200000;

    private static final int CACHE_SIZE = 32;

    /** The Floquet solution for one <code>(a, q)</code>. */
    private static final class Floquet
    {
        final Apcomplex nu;
        /** <code>c[i]</code> is the coefficient of index <code>k == i - offset</code>. */
        final Apcomplex[] c;
        final int offset;
        /** <code>MathieuC(a,q,0)</code> */
        final Apcomplex valueC;
        /** <code>MathieuSPrime(a,q,0)</code> */
        final Apcomplex slopeS;

        Floquet(Apcomplex nu, Apcomplex[] c, int offset, Apcomplex valueC, Apcomplex slopeS)
        {
            this.nu = nu;
            this.c = c;
            this.offset = offset;
            this.valueC = valueC;
            this.slopeS = slopeS;
        }
    }

    private static final Map<List<Object>, Floquet> CACHE =
            new LinkedHashMap<List<Object>, Floquet>(CACHE_SIZE * 2, 0.75f, true)
    {
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<List<Object>, Floquet> eldest)
                {
                    return size() > CACHE_SIZE;
                }
            };

    private MathieuFunctionHelper()
    {
    }

    /**
     * The Mathieu function <code>kind</code> to <code>precision</code> digits.
     *
     * @throws ArithmeticException if it could not be determined
     */
    static Apcomplex mathieu(Kind kind, Apcomplex a, Apcomplex q, Apcomplex z, long precision)
    {
        Apcomplex result;
        if (isZero(q))
        {
            result = trigonometric(kind, a, z, new FixedPrecisionApfloatHelper(precision + GUARD_DIGITS));
        }
        else if (isSmallStep(a, q, z))
        {
            FixedPrecisionApfloatHelper wh = new FixedPrecisionApfloatHelper(precision + GUARD_DIGITS);
            result = fromBasicSolutions(kind, floquet(a, q, precision, 0), a, q, z, wh);
        }
        else
        {
            // decimal digits by which a term of the series grows with its index
            double growth = 2.0 * Math.abs(z.imag().doubleValue()) / Math.log(10.0);
            if (!(growth < 30.0))
            {
                throw new ArithmeticException("Mathieu function too far from the real axis");
            }
            int level = (int) Math.ceil(growth);
            long extra = 0;
            while (true)
            {
                FixedPrecisionApfloatHelper wh = new FixedPrecisionApfloatHelper(precision + extra + GUARD_DIGITS);
                long[] loss = new long[1];
                result = fromFourierSeries(kind, floquet(a, q, precision + extra, level), z, wh, loss);
                if (level == 0 || loss[0] <= extra + GUARD_DIGITS - 4)
                {
                    break;
                }
                extra = loss[0];
                if (extra > MAX_EXTRA_DIGITS)
                {
                    throw new ArithmeticException("Mathieu series loses " + extra + " digits");
                }
            }
        }
        if (result == null)
        {
            throw new ArithmeticException("Mathieu function does not converge");
        }
        if (isReal(a) && isReal(q) && isReal(z))
        {
            // the function is real or - MathieuS in an instability gap - imaginary here, the other part
            // is rounding
            result = chop(result, precision);
        }
        return new FixedPrecisionApcomplexHelper(precision).valueOf(result);
    }

    /** <code>q == 0</code>: the equation has constant coefficients. */
    private static Apcomplex trigonometric(Kind kind, Apcomplex a, Apcomplex z,
            FixedPrecisionApfloatHelper h)
    {
        Apcomplex sqrtA = isZero(a) ? Apcomplex.ZERO : h.sqrt(a);
        Apcomplex w = h.multiply(sqrtA, z);
        switch (kind)
        {
            case C:
                return h.cos(w);
            case C_PRIME:
                return h.negate(h.multiply(sqrtA, h.sin(w)));
            case S:
                return h.sin(w);
            default:
                return h.multiply(sqrtA, h.cos(w));
        }
    }

    // ---------------------------------------------------------------------------------------------
    // evaluation
    // ---------------------------------------------------------------------------------------------

    /**
     * <code>f(z)</code> and <code>f(-z)</code> from the powers of <code>Exp(2*I*z)</code>, which
     * costs three exponentials whatever the number of coefficients is.
     */
    private static Apcomplex fromFourierSeries(Kind kind, Floquet floquet, Apcomplex z,
            FixedPrecisionApfloatHelper h, long[] loss)
    {
        long plusScale = Long.MIN_VALUE;
        long minusScale = Long.MIN_VALUE;
        final boolean derivative = kind == Kind.C_PRIME || kind == Kind.S_PRIME;
        final Apcomplex i = new Apcomplex(Apfloat.ZERO, new Apfloat(1, h.precision()));
        Apcomplex iz = h.multiply(i, z);
        Apcomplex w = h.exp(h.add(iz, iz));
        Apcomplex wInverse = h.divide(one(h), w);
        final int offset = floquet.offset;
        final Apcomplex[] c = floquet.c;

        Apcomplex plus = Apcomplex.ZERO;
        Apcomplex minus = Apcomplex.ZERO;
        // k >= 0 with w^k and w^-k, then k < 0 the other way round
        Apcomplex up = one(h);
        Apcomplex down = one(h);
        for (int k = 0; k + offset < c.length; k++)
        {
            Apcomplex ck = weight(floquet, k, derivative, i, h);
            Apcomplex plusTerm = h.multiply(ck, up);
            Apcomplex minusTerm = h.multiply(ck, down);
            plusScale = Math.max(plusScale, scale(plusTerm));
            minusScale = Math.max(minusScale, scale(minusTerm));
            plus = h.add(plus, plusTerm);
            minus = h.add(minus, minusTerm);
            up = h.multiply(up, w);
            down = h.multiply(down, wInverse);
        }
        up = wInverse;
        down = w;
        for (int k = -1; k + offset >= 0; k--)
        {
            Apcomplex ck = weight(floquet, k, derivative, i, h);
            Apcomplex plusTerm = h.multiply(ck, up);
            Apcomplex minusTerm = h.multiply(ck, down);
            plusScale = Math.max(plusScale, scale(plusTerm));
            minusScale = Math.max(minusScale, scale(minusTerm));
            plus = h.add(plus, plusTerm);
            minus = h.add(minus, minusTerm);
            up = h.multiply(up, wInverse);
            down = h.multiply(down, w);
        }
        Apcomplex e = h.exp(h.multiply(iz, floquet.nu));
        plus = h.multiply(plus, e);
        minus = h.divide(minus, e);
        if (derivative)
        {
            // d/dz of Exp(-I*(nu+2*k)*z) carries the opposite sign
            minus = h.negate(minus);
        }
        Apcomplex two = new Apcomplex(new Apfloat(2, h.precision()));
        Apcomplex result = (kind == Kind.C || kind == Kind.C_PRIME) ? h.divide(h.add(plus, minus), two)
                : h.divide(h.subtract(plus, minus), h.multiply(two, i));
        // the digits which the largest term has in front of the result
        long eScale = scale(e);
        long largest = Math.max(plusScale == Long.MIN_VALUE ? Long.MIN_VALUE : plusScale + eScale,
                minusScale == Long.MIN_VALUE ? Long.MIN_VALUE : minusScale - eScale);
        loss[0] = isZero(result) || largest == Long.MIN_VALUE ? 0 : Math.max(0, largest - scale(result));
        return result;
    }

    /** The decimal exponent of the larger part. */
    private static long scale(Apcomplex x)
    {
        if (isZero(x))
        {
            return Long.MIN_VALUE;
        }
        if (x.real().signum() == 0)
        {
            return x.imag().scale();
        }
        if (x.imag().signum() == 0)
        {
            return x.real().scale();
        }
        return Math.max(x.real().scale(), x.imag().scale());
    }

    /** <code>c(k)</code>, or <code>I*(nu+2*k)*c(k)</code> for a derivative. */
    private static Apcomplex weight(Floquet floquet, int k, boolean derivative, Apcomplex i,
            FixedPrecisionApfloatHelper h)
    {
        Apcomplex ck = floquet.c[k + floquet.offset];
        if (!derivative)
        {
            return ck;
        }
        Apcomplex frequency = h.add(floquet.nu, new Apcomplex(new Apfloat(2L * k, h.precision())));
        return h.multiply(h.multiply(i, frequency), ck);
    }

    /**
     * Next to the origin <code>MathieuS</code> is the small difference of <code>f(z)</code> and
     * <code>f(-z)</code>; one Taylor step from the origin has no such cancellation.
     */
    private static Apcomplex fromBasicSolutions(Kind kind, Floquet floquet, Apcomplex a, Apcomplex q,
            Apcomplex z, FixedPrecisionApfloatHelper h)
    {
        final boolean even = kind == Kind.C || kind == Kind.C_PRIME;
        Apcomplex[] y = even ? new Apcomplex[] {one(h), Apcomplex.ZERO}
                : new Apcomplex[] {Apcomplex.ZERO, one(h)};
        y = taylorStep(a, q, Apcomplex.ZERO, z, y, h);
        if (y == null)
        {
            return null;
        }
        Apcomplex value = (kind == Kind.C || kind == Kind.S) ? y[0] : y[1];
        return h.multiply(even ? floquet.valueC : floquet.slopeS, value);
    }

    private static boolean isSmallStep(Apcomplex a, Apcomplex q, Apcomplex z)
    {
        double size = magnitude(z);
        return size < 0.25 && size * Math.sqrt(magnitude(a) + 2.0 * magnitude(q)) <= 1.0;
    }

    // ---------------------------------------------------------------------------------------------
    // Floquet solution
    // ---------------------------------------------------------------------------------------------

    /**
     * @param level the decimal digits by which the terms of the series grow with their index
     * @throws ArithmeticException if the solution could not be determined
     */
    private static Floquet floquet(Apcomplex a, Apcomplex q, long precision, int level)
    {
        List<Object> key = Arrays.asList(a, q, Long.valueOf(precision), Integer.valueOf(level));
        synchronized (CACHE)
        {
            Floquet cached = CACHE.get(key);
            if (cached != null)
            {
                return cached;
            }
        }
        Floquet floquet = createFloquet(a, q, precision, level);
        if (floquet == null)
        {
            throw new ArithmeticException("Mathieu characteristic exponent does not converge");
        }
        synchronized (CACHE)
        {
            CACHE.put(key, floquet);
        }
        return floquet;
    }

    private static Floquet createFloquet(Apcomplex a, Apcomplex q, long precision, int level)
    {
        final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision + GUARD_DIGITS);
        Apcomplex nu = exponentNearSqrt(a, q, precision);
        if (nu == null)
        {
            nu = characteristicExponent(a, q, precision);
            if (nu == null)
            {
                return null;
            }
        }
        nu = nearSqrt(a, h.valueOf(nu), h);

        // past Sqrt(|q|) indices the recurrence is dominated by (2*k)^2 and c(k) decays like
        // q^k/(4^k*k!^2)
        final double qSize = Math.max(1.0, magnitude(q));
        int m = 0;
        double digits = 0.0;
        while (digits < precision + 2 * GUARD_DIGITS)
        {
            m++;
            digits += Math.max(0.0, Math.log10(4.0 * m * m / qSize) - level);
            if (m > MAX_COEFFICIENTS)
            {
                throw new ArithmeticException("Mathieu series needs too many coefficients");
            }
        }
        int n = (int) Math.ceil(Math.sqrt(qSize)) + m;
        // the coefficients rise again at the index with the opposite frequency, k == -nu, which
        // matters next to an integer exponent and is out of reach of the precision beyond 2*n
        double mirror = Math.abs(nu.real().doubleValue());
        if (mirror <= 2.0 * n)
        {
            n += (int) Math.ceil(mirror) + 1;
        }

        // ratio[n+k] == c(k)/c(k-1) for k > 0, and c(k)/c(k+1) for k < 0
        Apcomplex[] ratio = new Apcomplex[2 * n + 1];
        Apcomplex r = Apcomplex.ZERO;
        for (int k = n; k > 0; k--)
        {
            r = h.divide(q, h.subtract(v(a, nu, k, h), h.multiply(q, r)));
            ratio[n + k] = r;
        }
        r = Apcomplex.ZERO;
        for (int k = -n; k < 0; k++)
        {
            r = h.divide(q, h.subtract(v(a, nu, k, h), h.multiply(q, r)));
            ratio[n + k] = r;
        }
        checkInterrupt();

        Apcomplex[] c = new Apcomplex[2 * n + 1];
        c[n] = one(h);
        for (int k = 1; k <= n; k++)
        {
            c[n + k] = h.multiply(c[n + k - 1], ratio[n + k]);
        }
        for (int k = -1; k >= -n; k--)
        {
            c[n + k] = h.multiply(c[n + k + 1], ratio[n + k]);
        }

        // Sum(Abs(c(k))^2) == 1, and the phase that leaves MathieuC(a,q,0) positive
        Apfloat norm = Apfloat.ZERO;
        Apcomplex sum = Apcomplex.ZERO;
        for (Apcomplex ck : c)
        {
            norm = h.add(norm, h.norm(ck));
            sum = h.add(sum, ck);
        }
        Apcomplex scale = new Apcomplex(h.sqrt(norm));
        if (!isZero(sum))
        {
            scale = h.multiply(scale, h.divide(sum, new Apcomplex(h.abs(sum))));
        }
        Apcomplex valueC = Apcomplex.ZERO;
        Apcomplex slopeS = Apcomplex.ZERO;
        for (int j = 0; j < c.length; j++)
        {
            c[j] = h.divide(c[j], scale);
            valueC = h.add(valueC, c[j]);
            Apcomplex frequency = h.add(nu, new Apcomplex(new Apfloat(2L * (j - n), h.precision())));
            slopeS = h.add(slopeS, h.multiply(frequency, c[j]));
        }

        if (isOppositeExponent(a, nu, slopeS, h))
        {
            // -nu belongs to f(-z): the same MathieuC and the opposite MathieuS
            nu = h.negate(nu);
            slopeS = h.negate(slopeS);
            for (int j = 0; j < n; j++)
            {
                Apcomplex t = c[j];
                c[j] = c[2 * n - j];
                c[2 * n - j] = t;
            }
        }
        return new Floquet(nu, c, n, valueC, slopeS);
    }

    /**
     * The sign of <code>nu</code> is the sign of <code>MathieuS</code>. It is chosen as it is for
     * <code>q == 0</code>, where <code>MathieuSPrime(a,0,0) == Sqrt(a)</code> is the principal root:
     * the slope <code>MathieuSPrime(a,q,0)</code> lies on the side of <code>Sqrt(a)</code>, and where
     * that does not decide - a positive <code>a</code> inside an instability gap, with an imaginary
     * slope - on the positive imaginary axis.
     */
    private static boolean isOppositeExponent(Apcomplex a, Apcomplex nu, Apcomplex slopeS,
            FixedPrecisionApfloatHelper h)
    {
        Apcomplex side = isZero(a) ? slopeS : h.multiply(slopeS, h.conj(h.sqrt(a)));
        side = chop(side, h.precision() - GUARD_DIGITS);
        if (side.real().signum() != 0)
        {
            return side.real().signum() < 0;
        }
        return side.imag().signum() < 0;
    }

    /**
     * The one of <code>nu + 2*k</code> for which <code>a - (nu+2*k)^2</code> is least: next to
     * <code>Sqrt(a)</code> or to <code>-Sqrt(a)</code>.
     */
    private static Apcomplex nearSqrt(Apcomplex a, Apcomplex nu, FixedPrecisionApfloatHelper h)
    {
        if (isZero(a))
        {
            return nu;
        }
        double root = h.sqrt(a).real().doubleValue();
        double exponent = nu.real().doubleValue();
        long best = 0;
        double least = magnitude(v(a, nu, 0, h));
        for (double target : new double[] {root, -root})
        {
            double shift = Math.rint((target - exponent) / 2.0);
            if (Math.abs(shift) > 1.0e9)
            {
                throw new ArithmeticException("Mathieu characteristic exponent too large");
            }
            long k = (long) shift;
            double size = magnitude(v(a, nu, k, h));
            if (size < least)
            {
                least = size;
                best = k;
            }
        }
        return best == 0 ? nu : h.add(nu, new Apcomplex(new Apfloat(2L * best, h.precision())));
    }

    /** <code>a - (nu+2*k)^2</code> */
    private static Apcomplex v(Apcomplex a, Apcomplex nu, long k, FixedPrecisionApfloatHelper h)
    {
        Apcomplex frequency = h.add(nu, new Apcomplex(new Apfloat(2L * k, h.precision())));
        return h.subtract(a, h.multiply(frequency, frequency));
    }

    /**
     * The exponent next to <code>Sqrt(a)</code> for an <code>a</code> far above <code>q</code>, or
     * <code>null</code> to leave it to the monodromy - whose integration takes
     * <code>Sqrt(|a|)</code> steps, and whose <code>ArcCos</code> has lost the digits of an
     * exponent this large.
     *
     * <p>
     * The exponent is the root of the equation of the index 0 of the recurrence,
     * <code>a - nu^2 == q*(c(1)/c(0) + c(-1)/c(0))</code>, with the two ratios from the backward
     * recurrences. Up here the equation is <code>a - nu^2</code> and a small correction, the gaps
     * between the stability bands are far narrower than any working precision, and the secant
     * method from <code>Sqrt(a)</code> settles in a few steps.
     */
    static Apcomplex exponentNearSqrt(Apcomplex a, Apcomplex q, long precision)
    {
        final double aSize = magnitude(a);
        final double qSize = Math.max(1.0, magnitude(q));
        if (!(aSize + 2.0 * qSize > 1.0e4 && aSize >= 4.0 * qSize) || Double.isInfinite(aSize))
        {
            return null;
        }
        final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision + GUARD_DIGITS);
        int m = 0;
        double digits = 0.0;
        while (digits < precision + 2 * GUARD_DIGITS)
        {
            m++;
            digits += Math.max(0.0, Math.log10(4.0 * m * m / qSize));
        }
        final int n = (int) Math.ceil(Math.sqrt(qSize)) + m;
        final Apfloat tolerance = new Apfloat("1e-" + (precision + 2), h.precision());

        Apcomplex x0 = h.sqrt(a);
        Apcomplex f0 = exponentEquation(a, q, x0, n, h);
        if (isZero(f0))
        {
            return x0;
        }
        // the derivative is -2*nu and a small correction
        Apcomplex x1 = h.add(x0, h.divide(f0, h.add(x0, x0)));
        for (int i = 0; i < 40; i++)
        {
            Apcomplex f1 = exponentEquation(a, q, x1, n, h);
            Apcomplex difference = h.subtract(f1, f0);
            if (isZero(f1) || isZero(difference))
            {
                return x1;
            }
            Apcomplex step = h.divide(h.multiply(f1, h.subtract(x1, x0)), difference);
            x0 = x1;
            f0 = f1;
            x1 = h.subtract(x1, step);
            if (h.abs(step).compareTo(h.multiply(tolerance, h.abs(x1))) <= 0)
            {
                return x1;
            }
        }
        return null;
    }

    /** <code>a - nu^2 - q*(c(1)/c(0) + c(-1)/c(0))</code> */
    private static Apcomplex exponentEquation(Apcomplex a, Apcomplex q, Apcomplex nu, int n,
            FixedPrecisionApfloatHelper h)
    {
        Apcomplex up = Apcomplex.ZERO;
        for (int k = n; k > 0; k--)
        {
            up = h.divide(q, h.subtract(v(a, nu, k, h), h.multiply(q, up)));
        }
        Apcomplex down = Apcomplex.ZERO;
        for (int k = -n; k < 0; k++)
        {
            down = h.divide(q, h.subtract(v(a, nu, k, h), h.multiply(q, down)));
        }
        return h.subtract(v(a, nu, 0, h), h.multiply(q, h.add(up, down)));
    }

    /**
     * <code>nu</code> with <code>0 <= Re(nu) <= 1</code> from
     * <code>Cos(Pi*nu) == 2*y1(Pi/2)*y2'(Pi/2) - 1</code>.
     */
    private static Apcomplex characteristicExponent(Apcomplex a, Apcomplex q, long precision)
    {
        final FixedPrecisionApfloatHelper h =
                new FixedPrecisionApfloatHelper(2 * precision + GUARD_DIGITS);
        Apcomplex cosine = cosPiNu(a, q, h);
        if (cosine == null)
        {
            return null;
        }
        return h.divide(h.acos(cosine), new Apcomplex(h.pi()));
    }

    /**
     * <code>Cos(Pi*nu) == 2*y1(Pi/2)*y2'(Pi/2) - 1</code> at the precision of <code>h</code>, which
     * should be about twice the precision wanted for <code>nu</code>.
     */
    static Apcomplex cosPiNu(Apcomplex a, Apcomplex q, FixedPrecisionApfloatHelper h)
    {
        final Apfloat pi = h.pi();
        final Apfloat halfPi = h.divide(pi, new Apfloat(2, h.precision()));
        final double rate = Math.sqrt(magnitude(a) + 2.0 * magnitude(q) + 1.0);
        final int steps = (int) Math.min(1000000.0, Math.ceil(0.5 * Math.PI * rate));
        final Apcomplex step = new Apcomplex(h.divide(halfPi, new Apfloat(steps, h.precision())));

        Apcomplex[] y1 = {one(h), Apcomplex.ZERO};
        Apcomplex[] y2 = {Apcomplex.ZERO, one(h)};
        Apcomplex z0 = Apcomplex.ZERO;
        for (int s = 0; s < steps; s++)
        {
            checkInterrupt();
            y1 = taylorStep(a, q, z0, step, y1, h);
            y2 = taylorStep(a, q, z0, step, y2, h);
            if (y1 == null || y2 == null)
            {
                return null;
            }
            z0 = h.add(z0, step);
        }
        Apcomplex product = h.multiply(y1[0], y2[1]);
        return h.subtract(h.add(product, product), one(h));
    }

    /**
     * <code>{y(z0+step), y'(z0+step)}</code> from <code>{y(z0), y'(z0)}</code> by the Taylor series
     * of the solution at <code>z0</code>:
     *
     * <pre>
     * (n+2)*(n+1)*y(n+2) == -a*y(n) + 2*q*Sum(g(j)*y(n-j), {j,0,n})
     * </pre>
     *
     * with the coefficients <code>g(j) == 2^j/j!*Cos(2*z0+j*Pi/2)</code> of
     * <code>Cos(2*(z0+t))</code>.
     *
     * @return <code>null</code> if the series did not settle
     */
    private static Apcomplex[] taylorStep(Apcomplex a, Apcomplex q, Apcomplex z0, Apcomplex step,
            Apcomplex[] y, FixedPrecisionApfloatHelper h)
    {
        final long precision = h.precision();
        final int maxTerms = (int) Math.min(100000L, 40 * precision + 200);
        Apcomplex twoZ0 = h.add(z0, z0);
        Apcomplex cos = h.cos(twoZ0);
        Apcomplex sin = h.sin(twoZ0);
        final Apcomplex[] cycle = {cos, h.negate(sin), h.negate(cos), sin};
        final Apcomplex twoQ = h.add(q, q);

        Apcomplex[] g = new Apcomplex[64];
        Apcomplex[] t = new Apcomplex[64];
        t[0] = y[0];
        t[1] = y[1];
        Apfloat factor = new Apfloat(1, precision);

        Apcomplex value = Apcomplex.ZERO;
        Apcomplex slope = Apcomplex.ZERO;
        Apcomplex power = one(h); // step^n
        int settled = 0;
        for (int n = 0; n < maxTerms; n++)
        {
            if (n + 3 > t.length)
            {
                g = Arrays.copyOf(g, 2 * g.length);
                t = Arrays.copyOf(t, 2 * t.length);
            }
            if (n > 0)
            {
                factor = h.divide(h.multiply(factor, new Apfloat(2, precision)),
                        new Apfloat(n, precision));
            }
            g[n] = h.multiply(new Apcomplex(factor), cycle[n % 4]);
            Apcomplex convolution = Apcomplex.ZERO;
            for (int j = 0; j <= n; j++)
            {
                convolution = h.add(convolution, h.multiply(g[j], t[n - j]));
            }
            Apcomplex next = h.subtract(h.multiply(twoQ, convolution), h.multiply(a, t[n]));
            t[n + 2] = h.divide(next, new Apcomplex(new Apfloat((n + 2L) * (n + 1L), precision)));

            // y gets t(n)*step^n, y' gets (n+1)*t(n+1)*step^n
            Apcomplex valueTerm = h.multiply(t[n], power);
            Apcomplex slopeTerm =
                    h.multiply(h.multiply(t[n + 1], new Apcomplex(new Apfloat(n + 1L, precision))), power);
            value = h.add(value, valueTerm);
            slope = h.add(slope, slopeTerm);
            power = h.multiply(power, step);

            if (isNegligible(valueTerm, value, precision) && isNegligible(slopeTerm, slope, precision))
            {
                // one of the two basic solutions has every second coefficient zero
                if (++settled >= 4)
                {
                    return new Apcomplex[] {value, slope};
                }
            }
            else
            {
                settled = 0;
            }
            if ((n & 31) == 31)
            {
                checkInterrupt();
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------------------------------

    static void checkInterrupt()
    {
        if (Thread.currentThread().isInterrupted())
        {
            throw new ApfloatInterruptedException("Interrupted");
        }
    }

    private static boolean isNegligible(Apcomplex term, Apcomplex total, long precision)
    {
        if (isZero(term))
        {
            return true;
        }
        if (isZero(total))
        {
            return false;
        }
        return term.scale() < total.scale() - precision - 2;
    }

    /** Drops a real or an imaginary part that is rounding error next to the other one. */
    private static Apcomplex chop(Apcomplex value, long precision)
    {
        Apfloat re = value.real();
        Apfloat im = value.imag();
        if (re.signum() == 0 || im.signum() == 0)
        {
            return value;
        }
        long digits = Math.max(3L, precision - 3L);
        if (im.scale() < re.scale() - digits)
        {
            return new Apcomplex(re);
        }
        if (re.scale() < im.scale() - digits)
        {
            return new Apcomplex(Apfloat.ZERO, im);
        }
        return value;
    }

    private static Apcomplex one(FixedPrecisionApcomplexHelper h)
    {
        return new Apcomplex(new Apfloat(1, h.precision()));
    }

    private static boolean isZero(Apcomplex x)
    {
        return x.real().signum() == 0 && x.imag().signum() == 0;
    }

    private static boolean isReal(Apcomplex x)
    {
        return x.imag().signum() == 0;
    }

    private static double magnitude(Apcomplex x)
    {
        return Math.hypot(x.real().doubleValue(), x.imag().doubleValue());
    }
}

