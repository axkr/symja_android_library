package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * Jacobi theta functions of the nome <i>q</i>, for {@link Apcomplex} arguments.<p>
 *
 * The functions are the sums of their Fourier series, whose terms fall like
 * <i>q</i><sup><i>n</i><sup>2</sup></sup>. For a nome next to the unit circle the terms
 * cancel - &theta;<sub>4</sub>(0, 9/10) is 7 10<sup>-10</sup> - and the series is summed
 * with the digits which it loses.
 *
 * @see <a href="https://dlmf.nist.gov/20.2#i">DLMF 20.2(i)</a>
 */

public class ApcomplexThetaMath
{
    // The largest number of factors of an infinite product
    private static final double MAX_FACTORS = 2.0e6;

    // Most extra digits which are added for the cancellation of a series
    private static final long MAX_EXTRA_PRECISION = 1000;

    private static final long MAX_TERMS = 100000;

    private ApcomplexThetaMath()
    {
    }

    /**
     * Jacobi theta function &theta;<sub><i>k</i></sub>(<i>z</i>, <i>q</i>):
     * &theta;<sub>1</sub> = 2 <i>q</i><sup>1/4</sup> &sum; (-1)<sup><i>n</i></sup> <i>q</i><sup><i>n</i>(<i>n</i>+1)</sup> sin((2<i>n</i>+1)<i>z</i>),
     * &theta;<sub>2</sub> = 2 <i>q</i><sup>1/4</sup> &sum; <i>q</i><sup><i>n</i>(<i>n</i>+1)</sup> cos((2<i>n</i>+1)<i>z</i>),
     * &theta;<sub>3</sub> = 1 + 2 &sum; <i>q</i><sup><i>n</i><sup>2</sup></sup> cos(2<i>n</i> <i>z</i>) and
     * &theta;<sub>4</sub> = 1 + 2 &sum; (-1)<sup><i>n</i></sup> <i>q</i><sup><i>n</i><sup>2</sup></sup> cos(2<i>n</i> <i>z</i>),
     * with the principal value of <i>q</i><sup>1/4</sup>.
     *
     * @param k The number of the function, from 1 to 4.
     * @param z The argument.
     * @param q The nome, inside the unit circle.
     *
     * @return &theta;<sub><i>k</i></sub>(<i>z</i>, <i>q</i>), to the smaller precision of the argument and the nome.
     *
     * @throws IllegalArgumentException If <i>k</i> is not from 1 to 4.
     * @throws ArithmeticException If the nome is not inside the unit circle, or so near to it that the series cancels more digits than can be added.
     */

    public static Apcomplex ellipticTheta(int k, Apcomplex z, Apcomplex q)
        throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException
    {
        if (k < 1 || k > 4)
        {
            throw new IllegalArgumentException("Theta function " + k);
        }
        long precision = Math.min(z.precision(), q.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate theta function to infinite precision", "theta.infinitePrecision");
        }
        int radix = q.radix();
        if (q.isZero())
        {
            return (k <= 2 ? Apcomplex.ZERO : new Apfloat(1, precision, radix));
        }
        if (ApcomplexMath.abs(q).compareTo(new Apfloat(1, precision, radix)) >= 0)
        {
            throw new ArithmeticException("Nome of the theta function outside of the unit circle");
        }
        long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION / 2;
        while (true)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            long[] loss = new long[1];
            Apcomplex result = series(k, z, q, workingPrecision, loss);
            if (loss[0] + ApfloatHelperShim.getSmallExtraPrecision(radix) <= extraPrecision)
            {
                if (z.imag().signum() == 0 && q.imag().signum() == 0 && (k >= 3 || q.real().signum() > 0))
                {
                    // Real for a real argument, and a nome with a real fourth root
                    result = result.real();
                }
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            extraPrecision = loss[0] + ApfloatHelperShim.EXTRA_PRECISION / 2;
            if (extraPrecision > MAX_EXTRA_PRECISION)
            {
                throw new ArithmeticException("Theta series loses " + loss[0] + " digits");
            }
        }
    }

    /**
     * Jacobi elliptic functions sn(<i>u</i> | <i>m</i>), cn(<i>u</i> | <i>m</i>) and
     * dn(<i>u</i> | <i>m</i>) of the parameter <i>m</i>, as quotients of theta functions of the
     * nome <i>q</i> = <i>e</i><sup>-&pi; <i>K</i>(1 - <i>m</i>) / <i>K</i>(<i>m</i>)</sup>.
     *
     * @param u The argument.
     * @param m The parameter.
     *
     * @return sn, cn and dn, to the smaller precision of the argument and the parameter.
     *
     * @throws ArithmeticException At a pole, or for a parameter so near to 1 that the theta series cancel more digits than can be added.
     *
     * @see <a href="https://dlmf.nist.gov/22.2">DLMF 22.2</a>
     */

    public static Apcomplex[] jacobi(Apcomplex u, Apcomplex m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(u.precision(), m.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Jacobi elliptic function to infinite precision", "jacobi.infinitePrecision");
        }
        int radix = (u.isZero() ? m.radix() : u.radix());
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                two = new Apfloat(2, workingPrecision, radix);
        u = ApfloatHelperShim.ensurePrecision(u, workingPrecision);
        m = ApfloatHelperShim.ensurePrecision(m, workingPrecision);
        Apcomplex sn,
                  cn,
                  dn;
        if (m.isZero())
        {
            sn = h.sin(u);
            cn = h.cos(u);
            dn = one;
        }
        else if (m.equals(one))
        {
            sn = h.tanh(u);
            cn = h.divide(one, h.cosh(u));
            dn = cn;
        }
        else
        {
            Apcomplex k = h.ellipticK(m),
                      nome = h.exp(h.divide(h.multiply(h.pi(), h.ellipticK(h.subtract(one, m))), k).negate()),
                      v = h.divide(h.multiply(h.pi(), u), h.multiply(two, k)),
                      zero = Apcomplex.ZERO,
                      theta2 = ellipticTheta(2, zero, nome),
                      theta3 = ellipticTheta(3, zero, nome),
                      theta4 = ellipticTheta(4, zero, nome),
                      denominator = ellipticTheta(4, v, nome);
            sn = h.divide(h.multiply(theta3, ellipticTheta(1, v, nome)), h.multiply(theta2, denominator));
            cn = h.divide(h.multiply(theta4, ellipticTheta(2, v, nome)), h.multiply(theta2, denominator));
            dn = h.divide(h.multiply(theta4, ellipticTheta(3, v, nome)), h.multiply(theta3, denominator));
        }
        return new Apcomplex[] { ApfloatHelperShim.limitPrecision(sn, precision), ApfloatHelperShim.limitPrecision(cn, precision), ApfloatHelperShim.limitPrecision(dn, precision) };
    }

    /**
     * Inverse of the Jacobi elliptic function <code>pq(u, m)</code>, where <code>p</code> and <code>q</code>
     * are two different letters of <code>s, c, d, n</code>: a value <code>u</code> with
     * <code>pq(u, m) = x</code>, which is given by the symmetric integral of the first kind.<p>
     *
     * The inverses of <code>sn, sc, sd</code> are odd functions of <code>x</code>. For
     * <code>cn, cd</code> the integral is the value in the right half plane, and
     * <code>2 K(m)</code> minus the value at <code>-x</code> is the one in the left half plane;
     * for <code>dn</code> it is the value at <code>-x</code> shifted by <code>2 i K(1 - m)</code>,
     * with an imaginary part in <code>(-2 K(1 - m), 2 K(1 - m)]</code> for a real
     * <code>0 &lt; m &lt; 1</code>. The inverse of one of the reciprocal functions
     * <code>ns, cs, ds, nc, dc, nd</code> is the inverse of the function at <code>1 / x</code>.<p>
     *
     * On the branch cuts the value is the limit from one side, the one which Mathematica takes:
     * on the real axis above 1 the functions are continuous from above. That is not the side of
     * the inverse sine, which the inverse of <code>sn</code> is for <code>m = 0</code>.
     *
     * @param p The numerator letter of the function.
     * @param q The denominator letter of the function.
     * @param x The value of the function.
     * @param m The parameter.
     *
     * @return The argument <code>u</code> of the function.
     *
     * @exception ArithmeticException If the integral diverges, or <code>x</code> is zero for a reciprocal function.
     *
     * @see <a href="https://dlmf.nist.gov/19.25#v">DLMF 19.25(v)</a>
     */

    public static Apcomplex inverseJacobi(char p, char q, Apcomplex x, Apcomplex m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (p == q || "scdn".indexOf(p) < 0 || "scdn".indexOf(q) < 0)
        {
            throw new IllegalArgumentException("No Jacobi elliptic function " + p + q);
        }
        long precision = Math.min(x.precision(), m.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate inverse Jacobi elliptic function to infinite precision", "jacobi.infinitePrecision");
        }
        int radix = (x.isZero() ? m.radix() : x.radix());
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix);
        x = ApfloatHelperShim.ensurePrecision(x, workingPrecision);
        m = ApfloatHelperShim.ensurePrecision(m, workingPrecision);
        Apcomplex result;
        if (p == 's' || (p == 'c' && q != 's') || (p == 'd' && q == 'n'))
        {
            result = inverseJacobi(p, q, x, m, h, one);
        }
        else
        {
            if (x.isZero())
            {
                throw new ArithmeticException("Inverse of a reciprocal Jacobi elliptic function of zero");
            }
            result = inverseJacobi(q, p, h.divide(one, x), m, h, one);
        }
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    // The inverse of one of sn, sc, sd, cn, cd, dn
    private static Apcomplex inverseJacobi(char p, char q, Apcomplex x, Apcomplex m, FixedPrecisionApfloatHelper h, Apfloat one)
    {
        boolean real = (x.imag().signum() == 0);
        if (p == 's')
        {
            // odd; beyond 1 on the real axis the limit from above for a positive x
            return (real && ApfloatMath.abs(x.real()).compareTo(one) > 0 ? mirror(p, q, x, m, h, one) : integral(p, q, x, m, h, one));
        }
        Apfloat two = h.add(one, one);
        if (x.real().signum() < 0)
        {
            if (p == 'd')
            {
                // dn(u + 2 i K') = -dn(u), with the sign which the inverse hyperbolic secant has: that is the function for m = 1.
                // As that function, this one is continuous from below on the negative real axis: below -1 that is the value at -x from above
                boolean above = (real && x.real().negate().compareTo(one) > 0);
                Apcomplex positive = (above ? mirror(p, q, x.negate(), m, h, one) : inverseJacobi(p, q, x.negate(), m, h, one)),
                          shift = h.multiply(new Apcomplex(Apfloat.ZERO, two), h.ellipticK(h.subtract(one, m)));
                return (x.imag().signum() > 0 ? h.subtract(positive, shift) : h.add(positive, shift));
            }
            // cn(2 K - u) = -cn(u) and cd(2 K - u) = -cd(u); on the real axis this is the limit from above with the value at -x from below
            Apcomplex positive = (real ? integral(p, q, x.negate(), m, h, one) : inverseJacobi(p, q, x.negate(), m, h, one));
            return h.subtract(h.multiply(two, h.ellipticK(m)), positive);
        }
        if (x.real().signum() == 0 && x.imag().signum() < 0)
        {
            // x^2 is on the cut of the integral, where its value is the limit from the right half plane for the upper imaginary axis only: with the mirror image it is that limit here too
            return inverseJacobi(p, q, x.conj(), m.conj(), h, one).conj();
        }
        return (real && x.real().compareTo(one) > 0 ? mirror(p, q, x, m, h, one) : integral(p, q, x, m, h, one));
    }

    // The integral gives the limit from below on the real axis above 1: its mirror image is the limit from above
    private static Apcomplex mirror(char p, char q, Apcomplex x, Apcomplex m, FixedPrecisionApfloatHelper h, Apfloat one)
    {
        return integral(p, q, x, m.conj(), h, one).conj();
    }

    private static Apcomplex integral(char p, char q, Apcomplex x, Apcomplex m, FixedPrecisionApfloatHelper h, Apfloat one)
    {
        Apcomplex x2 = h.multiply(x, x);
        if (p == 's')
        {
            // DLMF 19.25.33
            char[] others = others(q, 's');
            return h.multiply(x, ApcomplexEllipticMath.carlsonRF(one, sum(h, one, h.multiply(delta(h, others[0], q, m, one), x2)), sum(h, one, h.multiply(delta(h, others[1], q, m, one), x2))));
        }
        // DLMF 19.25.34
        Apcomplex numerator = h.subtract(one, x2);
        if (numerator.isZero())
        {
            return Apcomplex.ZERO;
        }
        char[] others = others(p, q);
        char r = (others[0] == 's' ? others[1] : others[0]);
        Apcomplex w = h.divide(numerator, delta(h, q, p, m, one));
        return h.multiply(h.sqrt(w), ApcomplexEllipticMath.carlsonRF(x2, one, sum(h, one, h.multiply(delta(h, r, q, m, one), w))));
    }

    // The two letters of c, d, n, s, in this order, which are neither a nor b
    private static char[] others(char a, char b)
    {
        char[] result = new char[2];
        int i = 0;
        for (char letter : new char[] { 'c', 'd', 'n', 's' })
        {
            if (letter != a && letter != b && i < 2)
            {
                result[i++] = letter;
            }
        }
        return result;
    }

    // ps^2 - qs^2, which does not depend on the argument: DLMF 19.25.28 and 19.25.29
    private static Apcomplex delta(FixedPrecisionApfloatHelper h, char p, char q, Apcomplex m, Apfloat one)
    {
        return h.subtract(level(p, m, one), level(q, m, one));
    }

    private static Apcomplex level(char letter, Apcomplex m, Apfloat one)
    {
        switch (letter)
        {
            case 'c':
                return one.negate();
            case 'd':
                return m.negate();
            default:
                return Apcomplex.ZERO;
        }
    }

    // A sum which may have an exact zero as a term
    private static Apcomplex sum(FixedPrecisionApfloatHelper h, Apcomplex a, Apcomplex b)
    {
        return (a.isZero() ? b : b.isZero() ? a : h.add(a, b));
    }

    /**
     * Jacobi amplitude <code>am(u, m)</code> for real <code>u</code> and real <code>m &lt; 1</code>: the
     * continuous function of <code>u</code> with <code>sin(am(u, m)) = sn(u, m)</code> and
     * <code>am(0, m) = 0</code>.
     *
     * @param u The argument.
     * @param m The parameter.
     *
     * @return The amplitude.
     *
     * @exception ArithmeticException If <code>m</code> is not less than one.
     *
     * @see <a href="https://dlmf.nist.gov/22.16#i">DLMF 22.16(i)</a>
     */

    public static Apfloat jacobiAmplitude(Apfloat u, Apfloat m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(u.precision(), m.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Jacobi amplitude to infinite precision", "jacobi.infinitePrecision");
        }
        if (m.compareTo(Apfloat.ONE) >= 0)
        {
            throw new ArithmeticException("Jacobi amplitude of a parameter which is not less than one");
        }
        // the multiple of pi costs the digits of the size of the result
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, Math.max(0, u.scale()) + ApfloatHelperShim.EXTRA_PRECISION);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        u = ApfloatHelperShim.ensurePrecision(u, workingPrecision);
        m = ApfloatHelperShim.ensurePrecision(m, workingPrecision);
        Apfloat period = h.multiply(new Apfloat(2, workingPrecision, u.radix()), h.ellipticK(m).real()),
                half = new Apfloat("0.5", workingPrecision, u.radix()),
                n = h.floor(h.add(h.divide(u, period), half)),
                rest = h.subtract(u, h.multiply(period, n));
        Apfloat amplitude = (rest.signum() == 0 ? Apfloat.ZERO : h.asin(jacobi(rest, m)[0].real()));
        if (n.signum() != 0)
        {
            amplitude = h.add(amplitude, h.multiply(n, h.pi()));
        }
        return ApfloatHelperShim.limitPrecision(amplitude, precision);
    }

    /**
     * The q-Pochhammer symbol <code>(a; q)<sub>&infin;</sub></code>, the infinite product of
     * <code>1 - a q<sup>k</sup></code> for <code>k = 0, 1, ...</code>.
     *
     * @param a The argument.
     * @param q The base, with <code>|q| &lt; 1</code>.
     *
     * @return The product.
     *
     * @exception ArithmeticException If <code>|q| &ge; 1</code>, or so close to one that the product needs too many factors.
     *
     * @see <a href="https://dlmf.nist.gov/17.2#E4">DLMF 17.2.4</a>
     */

    public static Apcomplex qPochhammer(Apcomplex a, Apcomplex q)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(a.precision(), q.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate q-Pochhammer symbol to infinite precision", "qPochhammer.infinitePrecision");
        }
        int radix = (a.isZero() ? q.radix() : a.radix());
        if (a.isZero())
        {
            return new Apfloat(1, precision, radix);
        }
        double size = Math.hypot(q.real().doubleValue(), q.imag().doubleValue());
        if (!(size < 1.0))
        {
            throw new ArithmeticException("q-Pochhammer symbol of a base outside of the unit disk");
        }
        // the factors are 1 - a q^k until a q^k is below the precision
        double digits = precision * Math.log(radix) + Math.max(0, a.scale()) * Math.log(radix);
        double factors = (size == 0.0 ? 1.0 : digits / -Math.log(size)) + 2.0;
        if (factors > MAX_FACTORS)
        {
            throw new ArithmeticException("q-Pochhammer symbol of a base too close to the unit circle");
        }
        // each factor may lose a part of a digit
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, ApfloatHelperShim.EXTRA_PRECISION + (long) Math.ceil(Math.log10(factors)) + Math.max(0, a.scale()));
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix);
        a = ApfloatHelperShim.ensurePrecision(a, workingPrecision);
        q = ApfloatHelperShim.ensurePrecision(q, workingPrecision);
        Apcomplex product = one,
                  term = a;
        for (long k = 0; k < (long) factors; k++)
        {
            if (term.isZero() || term.scale() < -workingPrecision)
            {
                break;
            }
            Apcomplex factor = h.subtract(one, term);
            if (factor.isZero())
            {
                return Apcomplex.ZERO;
            }
            product = h.multiply(product, factor);
            term = h.multiply(term, q);
        }
        return ApfloatHelperShim.limitPrecision(product, precision);
    }

    /**
     * The nome <code>q(m) = exp(-&pi; K(1 - m) / K(m))</code> of the parameter <code>m</code>.
     *
     * @param m The parameter.
     *
     * @return The nome.
     *
     * @exception ArithmeticException If <code>m</code> is zero or one.
     *
     * @see <a href="https://dlmf.nist.gov/22.2#E1">DLMF 22.2.1</a>
     */

    public static Apcomplex ellipticNomeQ(Apcomplex m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = m.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate nome to infinite precision", "nome.infinitePrecision");
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, m.radix());
        m = ApfloatHelperShim.ensurePrecision(m, workingPrecision);
        Apcomplex result = h.exp(h.divide(h.multiply(h.pi(), h.ellipticK(h.subtract(one, m))), h.ellipticK(m)).negate());
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    /**
     * The modular lambda function <code>&lambda;(&tau;) = &theta;<sub>2</sub><sup>4</sup>(0, q) / &theta;<sub>3</sub><sup>4</sup>(0, q)</code>
     * with <code>q = exp(i &pi; &tau;)</code>.
     *
     * @param τ The argument, in the upper half plane.
     *
     * @return <code>&lambda;(&tau;)</code>
     *
     * @exception ArithmeticException If the argument is not in the upper half plane, or too near to the real axis.
     *
     * @see <a href="https://dlmf.nist.gov/23.15#E6">DLMF 23.15.6</a>
     */

    public static Apcomplex modularLambda(Apcomplex τ)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = τ.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate modular lambda to infinite precision", "lambda.infinitePrecision");
        }
        if (τ.imag().signum() <= 0)
        {
            throw new ArithmeticException("Modular lambda outside of the upper half plane");
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apcomplex nome = h.exp(h.multiply(new Apcomplex(Apfloat.ZERO, h.pi()), ApfloatHelperShim.ensurePrecision(τ, workingPrecision))),
                  result = h.pow(h.divide(ellipticTheta(2, Apcomplex.ZERO, nome), ellipticTheta(3, Apcomplex.ZERO, nome)), 4);
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    /**
     * The Dedekind eta function <code>&eta;(&tau;) = exp(i &pi; &tau; / 12) (q; q)<sub>&infin;</sub></code>
     * with <code>q = exp(2 i &pi; &tau;)</code>.
     *
     * @param τ The argument, in the upper half plane.
     *
     * @return <code>&eta;(&tau;)</code>
     *
     * @exception ArithmeticException If the argument is not in the upper half plane, or too near to the real axis.
     *
     * @see <a href="https://dlmf.nist.gov/23.15#E9">DLMF 23.15.9</a>
     */

    public static Apcomplex dedekindEta(Apcomplex τ)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = τ.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Dedekind eta to infinite precision", "eta.infinitePrecision");
        }
        if (τ.imag().signum() <= 0)
        {
            throw new ArithmeticException("Dedekind eta outside of the upper half plane");
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apcomplex t = ApfloatHelperShim.ensurePrecision(τ, workingPrecision),
                  angle = h.multiply(new Apcomplex(Apfloat.ZERO, h.pi()), t),
                  q = h.exp(h.multiply(new Apfloat(2, workingPrecision, τ.radix()), angle)),
                  result = h.multiply(h.exp(h.divide(angle, new Apfloat(12, workingPrecision, τ.radix()))), qPochhammer(q, q));
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    /**
     * Klein's absolute invariant <i>J</i>(&tau;) = 4 (1 - &lambda; + &lambda;<sup>2</sup>)<sup>3</sup> /
     * (27 &lambda;<sup>2</sup> (1 - &lambda;)<sup>2</sup>) with the modular function &lambda; =
     * &theta;<sub>2</sub><sup>4</sup> / &theta;<sub>3</sub><sup>4</sup> of the nome
     * <i>e</i><sup><i>i</i>&pi;&tau;</sup>.<p>
     *
     * The invariant does not change with &tau; + 1 and -1 / &tau;, by which &tau; is moved into the
     * fundamental domain: the nome is below 0.07 there, whatever it was.
     *
     * @param τ The argument, in the upper half plane.
     *
     * @return <i>J</i>(&tau;), to the precision of the argument.
     *
     * @throws ArithmeticException If the argument is not in the upper half plane.
     *
     * @see <a href="https://dlmf.nist.gov/23.15#E6">DLMF 23.15.6</a>, the modular function
     * @see <a href="https://dlmf.nist.gov/23.15#E7">DLMF 23.15.7</a>, the invariant
     */

    public static Apcomplex kleinInvariantJ(Apcomplex τ)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = τ.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Klein invariant to infinite precision", "kleinJ.infinitePrecision");
        }
        if (τ.imag().signum() <= 0)
        {
            throw new ArithmeticException("Klein invariant outside of the upper half plane");
        }
        int radix = τ.radix();
        Apcomplex previous = null;
        // Next to its zeros and to the point where it is 1 the invariant is a difference: with more digits until it settles
        for (long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION; extraPrecision <= 16 * ApfloatHelperShim.EXTRA_PRECISION; extraPrecision *= 2)
        {
            long workingPrecision = precision + extraPrecision;
            FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
            Apfloat one = new Apfloat(1, workingPrecision, radix);
            Apcomplex t = ApfloatHelperShim.ensurePrecision(τ, workingPrecision);
            for (int i = 0; i < 100000; i++)
            {
                Apfloat shift = ApfloatMath.roundToInteger(t.real(), java.math.RoundingMode.HALF_EVEN);
                if (shift.signum() != 0)
                {
                    t = new Apcomplex(h.subtract(t.real(), shift), t.imag());
                }
                if (h.norm(t).compareTo(one) >= 0)
                {
                    break;
                }
                t = h.divide(one, t).negate();
                if (t.imag().signum() <= 0)
                {
                    throw new ArithmeticException("Klein invariant too near to the real axis");
                }
            }
            Apcomplex nome = h.exp(h.multiply(new Apcomplex(Apfloat.ZERO, h.pi()), t)),
                      theta2 = ellipticTheta(2, Apcomplex.ZERO, nome),
                      theta3 = ellipticTheta(3, Apcomplex.ZERO, nome),
                      lambda = h.pow(h.divide(theta2, theta3), 4),
                      lambda1 = h.subtract(one, lambda),
                      numerator = h.pow(h.add(one, h.multiply(lambda, h.negate(lambda1))), 3),
                      denominator = h.multiply(h.multiply(lambda, lambda), h.multiply(lambda1, lambda1)),
                      result = h.divide(h.multiply(numerator, new Apfloat(4, workingPrecision, radix)), h.multiply(denominator, new Apfloat(27, workingPrecision, radix)));
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
        }
        throw new org.apfloat.LossOfPrecisionException("Complete loss of accurate digits");
    }

    private static Apcomplex series(int k, Apcomplex z, Apcomplex q, long workingPrecision, long[] loss)
    {
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        int radix = q.radix();
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        q = ApfloatHelperShim.ensurePrecision(q, workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix);
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one),
                  e = h.exp(h.multiply(i, z)),           // e^(i z)
                  eInverse = h.divide(one, e),
                  w = h.multiply(e, e),                  // e^(2 i z)
                  wInverse = h.multiply(eInverse, eInverse),
                  q2 = h.multiply(q, q);
        boolean odd = (k <= 2),
                alternating = (k == 1 || k == 4),
                sine = (k == 1);
        // The term of the index n is qn (up +- down), with qn = q^(n(n+1)) or q^(n^2), and up = e^(i (2n+1) z) or e^(2 i n z)
        Apcomplex qn = one,
                  step = (odd ? q2 : q),                 // qn of the next index over qn
                  up = (odd ? e : one),
                  down = (odd ? eInverse : one),
                  sum = Apcomplex.ZERO;
        long largest = Long.MIN_VALUE;
        int decreasing = 0;
        long previous = Long.MAX_VALUE;
        for (long n = 0; ; n++)
        {
            if (n > MAX_TERMS)
            {
                throw new ArithmeticException("Theta series does not converge");
            }
            Apcomplex term;
            if (!odd && n == 0)
            {
                term = one;
            }
            else
            {
                term = h.multiply(qn, sine ? h.subtract(up, down) : h.add(up, down));
            }
            if ((n & 1) == 1 && alternating)
            {
                term = term.negate();
            }
            sum = h.add(sum, term);
            long scale = (term.isZero() ? Long.MIN_VALUE : term.scale());
            largest = Math.max(largest, scale);
            // A term which is zero counts: theta 1 at a multiple of pi is a sum of zeros
            decreasing = (scale < previous || scale == Long.MIN_VALUE ? decreasing + 1 : 0);
            previous = scale;
            if (decreasing >= 3 && (sum.isZero() || scale < sum.scale() - workingPrecision - 2))
            {
                break;
            }
            qn = h.multiply(qn, step);
            step = h.multiply(step, q2);
            up = h.multiply(up, w);
            down = h.multiply(down, wInverse);
        }
        if (odd)
        {
            // 2 q^(1/4) sum of q^(n(n+1)) cos or (-1)^n sin of (2n+1) z
            Apcomplex root = h.root(q, 4);
            sum = h.multiply(sum, root);
            if (sine)
            {
                // (up - down) / i
                sum = h.divide(sum, i);
            }
        }
        loss[0] = (sum.isZero() || largest == Long.MIN_VALUE ? 0 : Math.max(0, largest - sum.scale()));
        return sum;
    }
}
