package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * Weierstrass elliptic functions.<p>
 *
 * The functions take the three roots <code>e<sub>1</sub>, e<sub>2</sub>, e<sub>3</sub></code> of
 * <code>4 t<sup>3</sup> - g<sub>2</sub> t - g<sub>3</sub></code> instead of the invariants. The
 * function &weierp; and its derivative do not depend on the order of the roots; the half-periods
 * do, as there are many pairs of them for one lattice.
 *
 * @see <a href="https://dlmf.nist.gov/23.6">DLMF 23.6</a>
 */

public class ApcomplexWeierstrassMath
{
    private ApcomplexWeierstrassMath()
    {
    }

    /**
     * Weierstrass elliptic function &weierp;(z).
     *
     * @param z The argument.
     * @param e1 A root of the cubic.
     * @param e2 Another root of the cubic.
     * @param e3 The third root of the cubic.
     *
     * @return <code>e<sub>3</sub> + (e<sub>1</sub> - e<sub>3</sub>) / sn<sup>2</sup>(z &radic;(e<sub>1</sub> - e<sub>3</sub>), m)</code> with <code>m = (e<sub>2</sub> - e<sub>3</sub>) / (e<sub>1</sub> - e<sub>3</sub>)</code>
     *
     * @exception ArithmeticException If <code>z</code> is a pole, or the first and the third root are equal.
     */

    public static Apcomplex weierstrassP(Apcomplex z, Apcomplex e1, Apcomplex e2, Apcomplex e3)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return evaluate(z, e1, e2, e3, false);
    }

    /**
     * Derivative &weierp;'(z) of the Weierstrass elliptic function.
     *
     * @param z The argument.
     * @param e1 A root of the cubic.
     * @param e2 Another root of the cubic.
     * @param e3 The third root of the cubic.
     *
     * @return <code>-2 (e<sub>1</sub> - e<sub>3</sub>)<sup>3/2</sup> cn dn / sn<sup>3</sup></code> at the argument and parameter of {@link #weierstrassP(Apcomplex,Apcomplex,Apcomplex,Apcomplex)}
     *
     * @exception ArithmeticException If <code>z</code> is a pole, or the first and the third root are equal.
     */

    public static Apcomplex weierstrassPPrime(Apcomplex z, Apcomplex e1, Apcomplex e2, Apcomplex e3)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return evaluate(z, e1, e2, e3, true);
    }

    private static Apcomplex evaluate(Apcomplex z, Apcomplex e1, Apcomplex e2, Apcomplex e3, boolean derivative)
    {
        long precision = precision(z, e1, e2, e3);
        // next to the pole at zero the function is the square, or the cube, of a large number
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, ApfloatHelperShim.EXTRA_PRECISION + 3 * Math.max(0, -z.scale()));
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        e1 = ApfloatHelperShim.ensurePrecision(e1, workingPrecision);
        e2 = ApfloatHelperShim.ensurePrecision(e2, workingPrecision);
        e3 = ApfloatHelperShim.ensurePrecision(e3, workingPrecision);
        Apcomplex difference = h.subtract(e1, e3);
        if (difference.isZero())
        {
            throw new ArithmeticException("Equal roots");
        }
        Apcomplex scale = h.sqrt(difference),
                  m = h.divide(h.subtract(e2, e3), difference);
        Apcomplex[] functions = ApcomplexThetaMath.jacobi(h.multiply(z, scale), m);
        Apcomplex sn = functions[0];
        if (sn.isZero())
        {
            throw new ArithmeticException("Pole of the Weierstrass function");
        }
        Apcomplex result;
        if (derivative)
        {
            result = h.divide(h.multiply(h.multiply(new Apfloat(-2, workingPrecision, z.radix()), h.multiply(difference, scale)), h.multiply(functions[1], functions[2])), h.multiply(sn, h.multiply(sn, sn)));
        }
        else
        {
            Apcomplex quotient = h.divide(difference, h.multiply(sn, sn));
            result = (e3.isZero() ? quotient : h.add(e3, quotient));
        }
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    /**
     * A pair of half-periods of the lattice with the given roots.
     *
     * @param e1 A root of the cubic.
     * @param e2 Another root of the cubic.
     * @param e3 The third root of the cubic.
     *
     * @return <code>K(m) / &radic;(e<sub>1</sub> - e<sub>3</sub>)</code> and <code>i K(1 - m) / &radic;(e<sub>1</sub> - e<sub>3</sub>)</code> with <code>m = (e<sub>2</sub> - e<sub>3</sub>) / (e<sub>1</sub> - e<sub>3</sub>)</code>
     *
     * @exception ArithmeticException If two of the roots are equal.
     *
     * @see <a href="https://dlmf.nist.gov/23.6#E17">DLMF 23.6.17</a>
     */

    public static Apcomplex[] weierstrassHalfPeriods(Apcomplex e1, Apcomplex e2, Apcomplex e3)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = precision(e1, e2, e3);
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        e1 = ApfloatHelperShim.ensurePrecision(e1, workingPrecision);
        e2 = ApfloatHelperShim.ensurePrecision(e2, workingPrecision);
        e3 = ApfloatHelperShim.ensurePrecision(e3, workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, e1.radix());
        Apcomplex difference = h.subtract(e1, e3),
                  numerator = h.subtract(e2, e3);
        if (difference.isZero() || numerator.isZero() || h.subtract(e1, e2).isZero())
        {
            throw new ArithmeticException("Equal roots");
        }
        Apcomplex scale = h.sqrt(difference),
                  m = h.divide(numerator, difference),
                  w1 = h.divide(h.ellipticK(m), scale),
                  w3 = h.divide(h.multiply(new Apcomplex(Apfloat.ZERO, one), h.ellipticK(h.subtract(one, m))), scale);
        return new Apcomplex[] { ApfloatHelperShim.limitPrecision(w1, precision), ApfloatHelperShim.limitPrecision(w3, precision) };
    }

    /**
     * The invariants <code>g<sub>2</sub></code> and <code>g<sub>3</sub></code> of the lattice with the
     * half-periods <code>&omega;<sub>1</sub></code> and <code>&omega;<sub>3</sub></code>.
     *
     * @param w1 A half-period.
     * @param w3 Another half-period.
     *
     * @return The invariants <code>g<sub>2</sub></code> and <code>g<sub>3</sub></code>.
     *
     * @exception ArithmeticException If the quotient of the half-periods is real.
     *
     * @see <a href="https://dlmf.nist.gov/23.6#i">DLMF 23.6(i)</a>
     */

    public static Apcomplex[] weierstrassInvariants(Apcomplex w1, Apcomplex w3)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = precision(w1, w3);
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        w1 = ApfloatHelperShim.ensurePrecision(w1, workingPrecision);
        w3 = ApfloatHelperShim.ensurePrecision(w3, workingPrecision);
        if (w1.isZero() || w3.isZero())
        {
            throw new ArithmeticException("Half-period zero");
        }
        int radix = w1.radix();
        Apcomplex ratio = h.divide(w3, w1);
        if (ratio.imag().signum() < 0)
        {
            // the same lattice with the upper half plane as the place of the quotient
            Apcomplex swap = w1;
            w1 = w3;
            w3 = swap;
            ratio = h.divide(w3, w1);
        }
        if (ratio.imag().signum() == 0)
        {
            throw new ArithmeticException("Half-periods with a real quotient");
        }
        Apcomplex nome = h.exp(h.multiply(new Apcomplex(Apfloat.ZERO, h.pi()), ratio)),
                  a = h.pow(ApcomplexThetaMath.ellipticTheta(2, Apcomplex.ZERO, nome), 4),
                  b = h.pow(ApcomplexThetaMath.ellipticTheta(3, Apcomplex.ZERO, nome), 4),
                  a2 = h.multiply(a, a),
                  b2 = h.multiply(b, b),
                  ab = h.multiply(a, b),
                  // pi / (2 w1), squared
                  factor = h.pow(h.divide(h.pi(), h.multiply(new Apfloat(2, workingPrecision, radix), w1)), 2),
                  factor2 = h.multiply(factor, factor),
                  g2 = h.multiply(h.multiply(h.divide(new Apfloat(4, workingPrecision, radix), new Apfloat(3, workingPrecision, radix)), factor2), h.add(h.subtract(a2, ab), b2)),
                  half = h.multiply(new Apfloat("1.5", workingPrecision, radix), h.add(h.multiply(a2, b), h.multiply(a, b2))),
                  g3 = h.multiply(h.multiply(h.divide(new Apfloat(8, workingPrecision, radix), new Apfloat(27, workingPrecision, radix)), h.multiply(factor2, factor)), h.subtract(h.add(h.multiply(a2, a), h.multiply(b2, b)), half));
        return new Apcomplex[] { ApfloatHelperShim.limitPrecision(g2, precision), ApfloatHelperShim.limitPrecision(g3, precision) };
    }

    /**
     * Weierstrass zeta function &zeta;(z), whose derivative is -&weierp;(z).
     *
     * @param z The argument.
     * @param e1 A root of the cubic.
     * @param e2 Another root of the cubic.
     * @param e3 The third root of the cubic.
     *
     * @return <code>&eta;<sub>1</sub> z / &omega;<sub>1</sub> + &pi; / (2 &omega;<sub>1</sub>) &theta;<sub>1</sub>'(v) / &theta;<sub>1</sub>(v)</code> with <code>v = &pi; z / (2 &omega;<sub>1</sub>)</code>
     *
     * @exception ArithmeticException If <code>z</code> is a pole, or two of the roots are equal.
     *
     * @see <a href="https://dlmf.nist.gov/23.6#E13">DLMF 23.6.13</a>
     */

    public static Apcomplex weierstrassZeta(Apcomplex z, Apcomplex e1, Apcomplex e2, Apcomplex e3)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return quasiPeriodic(z, e1, e2, e3, false);
    }

    /**
     * Weierstrass sigma function &sigma;(z), whose logarithmic derivative is &zeta;(z).
     *
     * @param z The argument.
     * @param e1 A root of the cubic.
     * @param e2 Another root of the cubic.
     * @param e3 The third root of the cubic.
     *
     * @return <code>2 &omega;<sub>1</sub> / &pi; exp(&eta;<sub>1</sub> z<sup>2</sup> / (2 &omega;<sub>1</sub>)) &theta;<sub>1</sub>(v) / &theta;<sub>1</sub>'(0)</code> with <code>v = &pi; z / (2 &omega;<sub>1</sub>)</code>
     *
     * @exception ArithmeticException If two of the roots are equal.
     *
     * @see <a href="https://dlmf.nist.gov/23.6#E9">DLMF 23.6.9</a>
     */

    public static Apcomplex weierstrassSigma(Apcomplex z, Apcomplex e1, Apcomplex e2, Apcomplex e3)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return quasiPeriodic(z, e1, e2, e3, true);
    }

    // Both functions belong to the lattice and not to a pair of half-periods. The pair is changed until the nome is
    // small, and the argument is moved by periods into the middle of the lattice, where the theta series have no
    // large terms to cancel; the quasi-periodicity gives the value at the argument itself
    private static Apcomplex quasiPeriodic(Apcomplex z, Apcomplex e1, Apcomplex e2, Apcomplex e3, boolean sigma)
    {
        long precision = precision(z, e1, e2, e3);
        int radix = z.radix();
        if (z.isZero())
        {
            if (sigma)
            {
                return z;
            }
            throw new ArithmeticException("Pole of the Weierstrass zeta function");
        }
        // next to its pole at zero the zeta function is a large number
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, ApfloatHelperShim.EXTRA_PRECISION + Math.max(0, -z.scale()));
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        Apcomplex[] halfPeriods = weierstrassHalfPeriods(ApfloatHelperShim.ensurePrecision(e1, workingPrecision), ApfloatHelperShim.ensurePrecision(e2, workingPrecision), ApfloatHelperShim.ensurePrecision(e3, workingPrecision));
        Apcomplex w1 = ApfloatHelperShim.ensurePrecision(halfPeriods[0], workingPrecision),
                  w3 = ApfloatHelperShim.ensurePrecision(halfPeriods[1], workingPrecision),
                  τ = h.divide(w3, w1);
        if (τ.imag().signum() < 0)
        {
            w3 = w3.negate();
            τ = τ.negate();
        }
        if (τ.imag().signum() == 0)
        {
            throw new ArithmeticException("Half-periods with a real quotient");
        }
        // The quotient into the fundamental domain: |Re τ| <= 1/2 and |τ| >= 1, where the nome is below 0.07
        for (int count = 0; count < 1000; count++)
        {
            double real = τ.real().doubleValue(),
                   imag = τ.imag().doubleValue();
            long shift = Math.round(real);
            if (shift != 0)
            {
                w3 = h.subtract(w3, h.multiply(w1, new Apfloat(shift, workingPrecision, radix)));
            }
            else if (real * real + imag * imag < 0.999)
            {
                Apcomplex swap = w1;
                w1 = w3;
                w3 = swap.negate();
            }
            else
            {
                break;
            }
            τ = h.divide(w3, w1);
        }
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                two = new Apfloat(2, workingPrecision, radix),
                pi = h.pi(radix);
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one),
                  q = h.exp(h.multiply(h.multiply(i, pi), τ)),
                  q2 = h.multiply(q, q);
        // Up to the factor 2 q^(1/4): the first derivative of θ1 at zero and minus its third derivative, the sums
        // of (-1)^n (2n + 1) q^(n (n + 1)) and of (-1)^n (2n + 1)^3 q^(n (n + 1))
        Apcomplex s1 = one,
                  s3 = one,
                  power = one,          // q^(n (n + 1))
                  step = one;           // q^(2 n)
        long limit = -workingPrecision - 5;
        for (long n = 1; ; n++)
        {
            step = h.multiply(step, q2);
            power = h.multiply(power, step);
            if (power.isZero() || power.scale() + 3 * (long) Math.ceil(Math.log10(2.0 * n + 1.0)) < limit)
            {
                break;
            }
            Apfloat odd = new Apfloat(2 * n + 1, workingPrecision, radix);
            Apcomplex term = h.multiply(power, odd);
            s1 = (n % 2 == 0 ? h.add(s1, term) : h.subtract(s1, term));
            term = h.multiply(term, h.multiply(odd, odd));
            s3 = (n % 2 == 0 ? h.add(s3, term) : h.subtract(s3, term));
        }
        // η1 = ζ(ω1) is π^2 / (12 ω1) times the quotient of the sums, and η3 comes from the relation of Legendre,
        // η1 ω3 - η3 ω1 = π i / 2
        Apcomplex η1 = h.divide(h.multiply(h.multiply(pi, pi), s3), h.multiply(h.multiply(new Apfloat(12, workingPrecision, radix), w1), s1)),
                  η3 = h.divide(h.subtract(h.multiply(η1, w3), h.divide(h.multiply(i, pi), two)), w1);
        // The argument without the periods 2 m ω1 + 2 n ω3 next to it
        Apcomplex halfPi = h.divide(pi, two),
                  v = h.divide(h.multiply(halfPi, z), w1);
        long n = Math.round(v.imag().doubleValue() / (Math.PI * τ.imag().doubleValue()));
        Apcomplex reduced = z;
        if (n != 0)
        {
            reduced = h.subtract(reduced, h.multiply(h.multiply(two, w3), new Apfloat(n, workingPrecision, radix)));
            v = h.divide(h.multiply(halfPi, reduced), w1);
        }
        long m = Math.round(v.real().doubleValue() / Math.PI);
        if (m != 0)
        {
            reduced = h.subtract(reduced, h.multiply(h.multiply(two, w1), new Apfloat(m, workingPrecision, radix)));
            v = h.divide(h.multiply(halfPi, reduced), w1);
        }
        // Up to the same factor: θ1(v) and its derivative, the sums of (-1)^k q^(k (k + 1)) sin((2k + 1) v) and
        // of (-1)^k (2k + 1) q^(k (k + 1)) cos((2k + 1) v)
        Apcomplex t0 = ApfloatHelperShim.ZEROS[radix],
                  t1 = ApfloatHelperShim.ZEROS[radix];
        power = one;
        step = one;
        double growth = Math.abs(v.imag().doubleValue()) / Math.log(10.0);
        for (long k = 0; ; k++)
        {
            if (k > 0)
            {
                step = h.multiply(step, q2);
                power = h.multiply(power, step);
                if (power.isZero() || power.scale() + (2 * k + 1) * growth + 2 < limit)
                {
                    break;
                }
            }
            Apfloat odd = new Apfloat(2 * k + 1, workingPrecision, radix);
            Apcomplex angle = h.multiply(v, odd),
                      sine = h.multiply(power, h.sin(angle)),
                      cosine = h.multiply(h.multiply(power, odd), h.cos(angle));
            t0 = (k % 2 == 0 ? h.add(t0, sine) : h.subtract(t0, sine));
            t1 = (k % 2 == 0 ? h.add(t1, cosine) : h.subtract(t1, cosine));
        }
        Apcomplex shift = h.multiply(two, h.add(h.multiply(η1, new Apfloat(m, workingPrecision, radix)), h.multiply(η3, new Apfloat(n, workingPrecision, radix)))),
                  result;
        if (sigma)
        {
            // 2 ω1 / π exp(η1 z^2 / (2 ω1)) θ1(v) / θ1'(0)
            result = h.multiply(h.multiply(h.divide(w1, halfPi), h.exp(h.divide(h.multiply(η1, h.multiply(reduced, reduced)), h.multiply(two, w1)))), h.divide(t0, s1));
            if (m != 0 || n != 0)
            {
                // σ(z + 2 m ω1 + 2 n ω3) = (-1)^(m + n + m n) exp((2 m η1 + 2 n η3) (z + m ω1 + n ω3)) σ(z)
                Apcomplex middle = h.add(reduced, h.add(h.multiply(w1, new Apfloat(m, workingPrecision, radix)), h.multiply(w3, new Apfloat(n, workingPrecision, radix))));
                result = h.multiply(result, h.exp(h.multiply(shift, middle)));
                if (((m + n + m * n) & 1) != 0)
                {
                    result = result.negate();
                }
            }
        }
        else
        {
            if (t0.isZero())
            {
                throw new ArithmeticException("Pole of the Weierstrass zeta function");
            }
            // η1 z / ω1 + π / (2 ω1) θ1'(v) / θ1(v), and ζ(z + 2 m ω1 + 2 n ω3) = ζ(z) + 2 m η1 + 2 n η3
            result = h.add(h.divide(h.multiply(η1, reduced), w1), h.divide(h.multiply(halfPi, t1), h.multiply(w1, t0)));
            result = h.add(result, shift);
        }
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    private static long precision(Apcomplex... values)
    {
        long precision = Apfloat.INFINITE;
        for (Apcomplex value : values)
        {
            precision = Math.min(precision, value.precision());
        }
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Weierstrass function to infinite precision", "weierstrass.infinitePrecision");
        }
        return precision;
    }
}
