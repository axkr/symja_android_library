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
