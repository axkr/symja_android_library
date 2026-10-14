package org.matheclipse.external.apfloat;

import java.math.RoundingMode;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;

/**
 * Helper class for the incomplete elliptic integrals of the first, second and third kind, and
 * for the complete elliptic integral of the third kind.<p>
 *
 * The integrals use the parameter <i>m</i> = <i>k</i><sup>2</sup>, like
 * <code>ApcomplexMath.ellipticK(z)</code> and <code>ApcomplexMath.ellipticE(z)</code> do:
 * <i>F(&phi;|m)</i> = &int;<sub>0</sub><sup>&phi;</sup> (1 - <i>m</i> sin<sup>2</sup> <i>t</i>)<sup>-1/2</sup> d<i>t</i>,
 * <i>E(&phi;|m)</i> = &int;<sub>0</sub><sup>&phi;</sup> (1 - <i>m</i> sin<sup>2</sup> <i>t</i>)<sup>1/2</sup> d<i>t</i>,
 * <i>&Pi;(n; &phi;|m)</i> = &int;<sub>0</sub><sup>&phi;</sup> (1 - <i>n</i> sin<sup>2</sup> <i>t</i>)<sup>-1</sup> (1 - <i>m</i> sin<sup>2</sup> <i>t</i>)<sup>-1/2</sup> d<i>t</i>.<p>
 *
 * They are computed from Carlson's symmetric integrals (see {@link CarlsonHelper}), which
 * are valid for an amplitude with -&pi;/2 &le; Re &phi; &le; &pi;/2. An amplitude outside of that
 * strip is first reduced with the quasi-periodicity
 * <i>F(&phi; + k &pi;|m)</i> = <i>F(&phi;|m)</i> + 2 <i>k</i> <i>K(m)</i>, and likewise for the
 * other two kinds.
 *
 * @implNote
 * This implementation is <i>slow</i>, meaning that it isn't a <i>fast algorithm</i>.
 * It is impractically slow beyond a precision of a few thousand digits.
 */

class EllipticHelper
{
    private EllipticHelper()
    {
    }

    /**
     * Incomplete elliptic integral of the first kind.
     *
     * @param φ The amplitude.
     * @param m The parameter.
     * @param precision The precision of the result.
     *
     * @return <i>F(&phi;|m)</i>
     *
     * @throws InfiniteExpansionException If the precision is infinite and the result is not exact.
     * @throws ArithmeticException If the integral is infinite.
     */

    public static Apcomplex ellipticF(Apcomplex φ, Apcomplex m, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int radix = φ.radix();
        if (φ.isZero())
        {
            return ApfloatHelperShim.ZEROS[radix];
        }
        checkPrecision(precision);
        if (m.isZero())
        {
            return ApfloatHelperShim.limitPrecision(φ, precision);
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        m = ApfloatHelperShim.ensurePrecision(m, workingPrecision);
        Reduction reduction = new Reduction(φ, workingPrecision);
        Apcomplex result = reduction.sin.multiply(CarlsonHelper.rf(reduction.cos2, reduction.Δ2(m), reduction.one, workingPrecision));
        if (reduction.k.signum() != 0)
        {
            result = result.add(reduction.twoK().multiply(ellipticK(m, workingPrecision)));
        }
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    /**
     * Incomplete elliptic integral of the second kind.
     *
     * @param φ The amplitude.
     * @param m The parameter.
     * @param precision The precision of the result.
     *
     * @return <i>E(&phi;|m)</i>
     *
     * @throws InfiniteExpansionException If the precision is infinite and the result is not exact.
     */

    public static Apcomplex ellipticE(Apcomplex φ, Apcomplex m, long precision)
        throws ApfloatRuntimeException
    {
        int radix = φ.radix();
        if (φ.isZero())
        {
            return ApfloatHelperShim.ZEROS[radix];
        }
        checkPrecision(precision);
        if (m.isZero())
        {
            return ApfloatHelperShim.limitPrecision(φ, precision);
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        m = ApfloatHelperShim.ensurePrecision(m, workingPrecision);
        Reduction reduction = new Reduction(φ, workingPrecision);
        Apcomplex result;
        if (isOne(m))
        {
            // E(φ|1) is sin(φ) on the principal strip, and the complete integral E(1) is one
            result = reduction.sin.add(reduction.twoK());
        }
        else
        {
            Apcomplex Δ2 = reduction.Δ2(m),
                      sin3 = reduction.sin.multiply(reduction.sin2);
            result = reduction.sin.multiply(CarlsonHelper.rf(reduction.cos2, Δ2, reduction.one, workingPrecision))
                              .subtract(m.multiply(sin3).multiply(CarlsonHelper.rd(reduction.cos2, Δ2, reduction.one, workingPrecision)).divide(reduction.three));
            if (reduction.k.signum() != 0)
            {
                result = result.add(reduction.twoK().multiply(ellipticE(m, workingPrecision)));
            }
        }
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    /**
     * Incomplete elliptic integral of the third kind.
     *
     * @param n The characteristic.
     * @param φ The amplitude.
     * @param m The parameter.
     * @param precision The precision of the result.
     *
     * @return <i>&Pi;(n; &phi;|m)</i>
     *
     * @throws InfiniteExpansionException If the precision is infinite and the result is not exact.
     * @throws ArithmeticException If the integral is infinite.
     */

    public static Apcomplex ellipticPi(Apcomplex n, Apcomplex φ, Apcomplex m, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int radix = φ.radix();
        if (φ.isZero())
        {
            return ApfloatHelperShim.ZEROS[radix];
        }
        if (n.isZero())
        {
            return ellipticF(φ, m, precision);
        }
        checkPrecision(precision);
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        n = ApfloatHelperShim.ensurePrecision(n, workingPrecision);
        m = (m.isZero() ? m : ApfloatHelperShim.ensurePrecision(m, workingPrecision));
        Reduction reduction = new Reduction(φ, workingPrecision);
        Apcomplex result = ellipticPi(n, m, reduction, workingPrecision);
        if (reduction.k.signum() != 0)
        {
            result = result.add(reduction.twoK().multiply(ellipticPi(n, m, new Reduction(radix, workingPrecision), workingPrecision)));
        }
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    /**
     * Complete elliptic integral of the third kind.
     *
     * @param n The characteristic.
     * @param m The parameter.
     * @param precision The precision of the result.
     *
     * @return <i>&Pi;(n|m)</i>
     *
     * @throws InfiniteExpansionException If the precision is infinite.
     * @throws ArithmeticException If the integral is infinite.
     */

    public static Apcomplex ellipticPi(Apcomplex n, Apcomplex m, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        checkPrecision(precision);
        int radix = (n.isZero() ? m.radix() : n.radix());
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        n = (n.isZero() ? n : ApfloatHelperShim.ensurePrecision(n, workingPrecision));
        m = (m.isZero() ? m : ApfloatHelperShim.ensurePrecision(m, workingPrecision));
        Apcomplex result = ellipticPi(n, m, new Reduction(radix, workingPrecision), workingPrecision);
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    // The integral of the third kind for an amplitude on the principal strip
    private static Apcomplex ellipticPi(Apcomplex n, Apcomplex m, Reduction reduction, long workingPrecision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int radix = reduction.one.radix();
        Apcomplex Δ2 = reduction.Δ2(m),
                  sin3 = reduction.sin.multiply(reduction.sin2);
        if (n.isZero())
        {
            return reduction.sin.multiply(CarlsonHelper.rf(reduction.cos2, Δ2, reduction.one, workingPrecision));
        }
        Apcomplex p = ensure(reduction.one.subtract(n.multiply(reduction.sin2)), workingPrecision);
        if (p.isZero())
        {
            throw new ApfloatArithmeticException("Elliptic Pi is infinite", "ellipticPi.infinite");
        }
        // A real characteristic whose pole 1 / sin^2 = n is on the path of integration. The change of parameter has
        // the pole in the logarithm RC alone. For a real parameter that is its principal value. For a complex one it
        // is one of the two integrals which pass the pole on a side - conjugate parameters have conjugate values
        boolean pole = n.imag().signum() == 0 && reduction.sin2.imag().signum() == 0 && p.imag().signum() == 0 && p.real().signum() < 0
                       && !(Δ2.imag().signum() == 0 && Δ2.real().signum() < 0)
                       && !ensure(reduction.one.subtract(m.divide(n).multiply(reduction.sin2)), workingPrecision).isZero();
        // With ω = m / n next to 1 the integral Π(ω; φ|m) of the change of parameter is itself next to its pole: infinite for the complete integral
        if (pole || n.imag().signum() == 0 && n.real().compareTo(new Apint(-1, radix)) < 0 && !nearOne(m.divide(n)))
        {
            // For a large negative characteristic the terms sin RF and n/3 sin^3 RJ cancel,
            // down to the order of 1 / sqrt(-n). The change of parameter ω = m / n,
            // Π(n; φ|m) + Π(ω; φ|m) = F(φ|m) + sqrt(c) RC((c - 1)(c - m), (c - n)(c - ω)) with c = 1 / sin^2 φ,
            // leaves Π(ω; φ|m) - F(φ|m) = ω/3 sin^3 RJ, which is small and is computed without cancellation
            Apcomplex ω = m.divide(n),
                      q = ensure(reduction.one.subtract(ω.multiply(reduction.sin2)), workingPrecision),
                      // the complete integral with the pole is the principal value for every parameter: no logarithm
                      result = (pole && reduction.cos2.isZero() ? ApfloatHelperShim.ZEROS[radix] : reduction.sin.multiply(CarlsonHelper.rc(reduction.cos2.multiply(Δ2), p.multiply(q), workingPrecision)));
            if (pole && !reduction.cos2.isZero() && m.imag().signum() != 0 && Δ2.real().compareTo(reduction.one) > 0)
            {
                // The side of the pole is the one which the transformation of RJ(cos^2, Δ^2, 1, p) to a positive
                // fourth argument takes, when it singles out the argument with the largest real part. That is
                // the principal branch of RC above, unless Δ^2 is larger than 1: then it is the other side, which
                // differs by 2 pi i times the residue, pi i / sqrt((n - 1) (1 - ω))
                Apcomplex root = ApcomplexMath.sqrt(ensure(n.subtract(reduction.one).multiply(reduction.one.subtract(ω)), workingPrecision)),
                          jump = new Apcomplex(Apfloat.ZERO, ApfloatMath.pi(workingPrecision, radix)).divide(root);
                result = (m.imag().signum() * reduction.sin.real().signum() > 0 ? result.add(jump) : result.subtract(jump));
            }
            if (!ω.isZero())
            {
                result = result.subtract(ω.multiply(sin3).multiply(CarlsonHelper.rj(reduction.cos2, Δ2, reduction.one, q, workingPrecision)).divide(reduction.three));
            }
            return result;
        }
        // sin RF + n/3 sin^3 RJ
        return reduction.sin.multiply(CarlsonHelper.rf(reduction.cos2, Δ2, reduction.one, workingPrecision))
                            .add(n.multiply(sin3).multiply(CarlsonHelper.rj(reduction.cos2, Δ2, reduction.one, p, workingPrecision)).divide(reduction.three));
    }

    private static boolean nearOne(Apcomplex ω)
    {
        return Math.hypot(ω.real().doubleValue() - 1.0, ω.imag().doubleValue()) < 0.5;
    }

    // The complete integral of the first kind, also for an exact zero or a real argument
    private static Apcomplex ellipticK(Apcomplex m, long workingPrecision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return ApcomplexMath.ellipticK(m.precision(workingPrecision));
    }

    // The complete integral of the second kind, also for an exact zero or a real argument
    private static Apcomplex ellipticE(Apcomplex m, long workingPrecision)
        throws ApfloatRuntimeException
    {
        return ApcomplexMath.ellipticE(m.precision(workingPrecision));
    }

    private static boolean isOne(Apcomplex m)
    {
        return m.imag().signum() == 0 && m.real().compareTo(ApfloatHelperShim.ONES[m.radix()]) == 0;
    }

    private static Apcomplex ensure(Apcomplex z, long workingPrecision)
        throws ApfloatRuntimeException
    {
        return (z.isZero() ? z : ApfloatHelperShim.ensurePrecision(z, workingPrecision));
    }

    private static void checkPrecision(long precision)
        throws InfiniteExpansionException
    {
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate elliptic integral to infinite precision", "elliptic.infinitePrecision");
        }
    }

    // The amplitude φ = r + k π with -π/2 <= Re r <= π/2, and the functions of r which the Carlson forms need
    private static class Reduction
    {
        // The amplitude π/2 of the complete integrals
        public Reduction(int radix, long workingPrecision)
            throws ApfloatRuntimeException
        {
            this.workingPrecision = workingPrecision;
            this.one = new Apfloat(1, workingPrecision, radix);
            this.three = new Apfloat(3, workingPrecision, radix);
            this.k = ApfloatHelperShim.ZEROS[radix];
            this.sin = this.one;
            this.sin2 = this.one;
            this.cos2 = ApfloatHelperShim.ZEROS[radix];
        }

        public Reduction(Apcomplex φ, long workingPrecision)
            throws ApfloatRuntimeException
        {
            int radix = φ.radix();
            this.workingPrecision = workingPrecision;
            this.one = new Apfloat(1, workingPrecision, radix);
            this.three = new Apfloat(3, workingPrecision, radix);
            // The digits of φ before the radix point are lost in the reduction, which is inherent in the problem
            long reductionPrecision = ApfloatHelperShim.extendPrecision(workingPrecision, Math.max(0, φ.scale()));
            Apfloat pi = ApfloatMath.pi(reductionPrecision, radix),
                    halfPi = pi.divide(new Apfloat(2, reductionPrecision, radix)),
                    real = φ.real();
            Apcomplex r = ApfloatHelperShim.ensurePrecision(φ, reductionPrecision);
            if (ApfloatMath.abs(real).compareTo(halfPi) > 0)
            {
                this.k = ApfloatMath.roundToInteger(real.precision(ApfloatHelperShim.extendPrecision(Math.max(1, real.scale()))).divide(pi), RoundingMode.HALF_EVEN);
                r = r.subtract(this.k.multiply(pi));
            }
            else
            {
                this.k = ApfloatHelperShim.ZEROS[radix];
            }
            r = (r.isZero() ? r : ApfloatHelperShim.ensurePrecision(r, workingPrecision));
            this.sin = (r.isZero() ? r : ApcomplexMath.sin(r));
            Apcomplex cos = (r.isZero() ? this.one : ApcomplexMath.cos(r));
            this.sin2 = this.sin.multiply(this.sin);
            // cos^2 and not 1 - sin^2, which would lose digits near ±π/2
            this.cos2 = cos.multiply(cos);
        }

        // 1 - m sin^2
        public Apcomplex Δ2(Apcomplex m)
            throws ApfloatRuntimeException
        {
            return ensure(this.one.subtract(m.multiply(this.sin2)), this.workingPrecision);
        }

        public Apfloat twoK()
            throws ApfloatRuntimeException
        {
            return new Apfloat(2, this.workingPrecision, this.one.radix()).multiply(this.k);
        }

        public final long workingPrecision;
        public final Apfloat one;
        public final Apfloat three;
        public final Apint k;
        public final Apcomplex sin;
        public final Apcomplex sin2;
        public final Apcomplex cos2;
    }
}
