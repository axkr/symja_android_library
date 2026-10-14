package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * Coulomb wave functions.
 *
 * @see <a href="https://dlmf.nist.gov/33.2">DLMF 33.2</a>
 */

public class ApcomplexCoulombMath
{
    // The largest number of extra digits which a cancelling series is summed with
    private static final long MAX_EXTRA_PRECISION = 2000;

    private ApcomplexCoulombMath()
    {
    }

    /**
     * Regular Coulomb wave function <code>F<sub>L</sub>(&eta;, &rho;)</code>.
     *
     * @param l The order.
     * @param η The parameter.
     * @param ρ The argument.
     *
     * @return <code>F<sub>L</sub>(&eta;, &rho;)</code>
     *
     * @exception ArithmeticException If the terms cancel beyond the largest working precision.
     *
     * @see <a href="https://dlmf.nist.gov/33.2#E3">DLMF 33.2.3</a>
     */

    public static Apcomplex coulombF(Apcomplex l, Apcomplex η, Apcomplex ρ)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return evaluate(l, η, ρ, true);
    }

    /**
     * Irregular Coulomb wave function <code>G<sub>L</sub>(&eta;, &rho;)</code>.
     *
     * @param l The order.
     * @param η The parameter.
     * @param ρ The argument.
     *
     * @return <code>G<sub>L</sub>(&eta;, &rho;)</code>
     *
     * @exception ArithmeticException If the argument is a nonpositive real number, or the terms cancel beyond the largest working precision.
     *
     * @see <a href="https://dlmf.nist.gov/33.2#E7">DLMF 33.2.7</a>
     * @see <a href="https://dlmf.nist.gov/33.2#E11">DLMF 33.2.11</a>
     */

    public static Apcomplex coulombG(Apcomplex l, Apcomplex η, Apcomplex ρ)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (ρ.imag().signum() == 0 && ρ.real().signum() <= 0)
        {
            // on the branch cut of the logarithm of the argument
            throw new ArithmeticException("Irregular Coulomb wave function of a nonpositive argument");
        }
        return evaluate(l, η, ρ, false);
    }

    private static Apcomplex evaluate(Apcomplex l, Apcomplex η, Apcomplex ρ, boolean regular)
    {
        long precision = Math.min(l.precision(), Math.min(η.precision(), ρ.precision()));
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Coulomb wave function to infinite precision", "coulomb.infinitePrecision");
        }
        // The terms of the confluent series of the argument 2 i rho are of the size exp(2 |rho|)
        double size = Math.hypot(ρ.real().doubleValue(), ρ.imag().doubleValue());
        long loss = (long) Math.ceil(2.0 * size / Math.log(ρ.radix()));
        if (loss > MAX_EXTRA_PRECISION)
        {
            throw new ArithmeticException("Coulomb wave function of a too large argument");
        }
        Apcomplex previous = null;
        for (long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION + loss; extraPrecision <= MAX_EXTRA_PRECISION + loss; extraPrecision += ApfloatHelperShim.EXTRA_PRECISION + extraPrecision / 2)
        {
            Apcomplex result = (regular ? f(l, η, ρ, precision + extraPrecision) : g(l, η, ρ, precision + extraPrecision));
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
        }
        throw new ArithmeticException("Coulomb wave function does not reach the precision");
    }

    // C_L(eta) rho^(L+1) exp(-i rho) M(L + 1 - i eta, 2 L + 2, 2 i rho)
    private static Apcomplex f(Apcomplex l, Apcomplex η, Apcomplex ρ, long workingPrecision)
    {
        int radix = ρ.radix();
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                two = new Apfloat(2, workingPrecision, radix);
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        l = ApfloatHelperShim.ensurePrecision(l, workingPrecision);
        η = ApfloatHelperShim.ensurePrecision(η, workingPrecision);
        ρ = ApfloatHelperShim.ensurePrecision(ρ, workingPrecision);
        Apcomplex l1 = h.add(l, one),
                  iη = h.multiply(i, η),
                  a = (iη.isZero() ? l1 : h.subtract(l1, iη)),
                  b = h.multiply(two, l1),
                  x = h.multiply(h.multiply(two, i), ρ),
                  series = one,
                  term = one;
        double bound = 2.0 * (Math.hypot(x.real().doubleValue(), x.imag().doubleValue()) + Math.hypot(a.real().doubleValue(), a.imag().doubleValue()));
        for (long k = 0; k < 10000000; k++)
        {
            Apfloat index = new Apfloat(k, workingPrecision, radix);
            Apcomplex numerator = h.add(a, index);
            if (numerator.isZero())
            {
                break;
            }
            term = h.divide(h.multiply(h.multiply(term, numerator), x), h.multiply(h.add(b, index), new Apfloat(k + 1, workingPrecision, radix)));
            series = h.add(series, term);
            if (term.isZero() || (k > bound && term.scale() < series.scale() - workingPrecision - 2))
            {
                break;
            }
        }
        return h.multiply(h.multiply(h.multiply(normalization(h, l, η, one, two), h.exp(h.multiply(l1, h.log(ρ)))), h.exp(h.multiply(i, ρ).negate())), series);
    }

    // C_L(eta) = 2^L exp(-pi eta / 2) sqrt(gamma(L + 1 + i eta) gamma(L + 1 - i eta)) / gamma(2 L + 2)
    private static Apcomplex normalization(FixedPrecisionApfloatHelper h, Apcomplex l, Apcomplex η, Apfloat one, Apfloat two)
    {
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one),
                  l1 = h.add(l, one),
                  iη = h.multiply(i, η),
                  product = (iη.isZero() ? h.multiply(h.gamma(l1), h.gamma(l1)) : h.multiply(h.gamma(h.add(l1, iη)), h.gamma(h.subtract(l1, iη))));
        return h.divide(h.multiply(h.multiply(h.exp(h.multiply(l, h.log(two))), h.exp(h.divide(h.multiply(h.pi(), η), two).negate())), h.sqrt(product)), h.gamma(h.multiply(two, l1)));
    }

    // (H+ + H-) / 2 with H+- = exp(+-i theta) (-+2 i rho)^(L + 1 +- i eta) U(L + 1 +- i eta, 2 L + 2, -+2 i rho)
    private static Apcomplex g(Apcomplex l, Apcomplex η, Apcomplex ρ, long workingPrecision)
    {
        int radix = ρ.radix();
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                two = new Apfloat(2, workingPrecision, radix);
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        l = ApfloatHelperShim.ensurePrecision(l, workingPrecision);
        η = ApfloatHelperShim.ensurePrecision(η, workingPrecision);
        ρ = ApfloatHelperShim.ensurePrecision(ρ, workingPrecision);
        Apcomplex l1 = h.add(l, one),
                  iη = h.multiply(i, η),
                  b = h.multiply(two, l1),
                  // sigma = (log gamma(L + 1 + i eta) - log gamma(L + 1 - i eta)) / (2 i)
                  σ = (iη.isZero() ? Apcomplex.ZERO : h.divide(h.subtract(h.logGamma(h.add(l1, iη)), h.logGamma(h.subtract(l1, iη))), h.multiply(two, i))),
                  θ = h.add(h.subtract(h.subtract(ρ, h.multiply(η, h.log(h.multiply(two, ρ)))), h.divide(h.multiply(l, h.pi()), two)), σ),
                  sum = Apcomplex.ZERO;
        for (int sign = 1; sign >= -1; sign -= 2)
        {
            Apcomplex a = (iη.isZero() ? l1 : sign > 0 ? h.add(l1, iη) : h.subtract(l1, iη)),
                      x = h.multiply(h.multiply(two, i), ρ);
            if (sign > 0)
            {
                x = x.negate();
            }
            Apcomplex phase = h.exp(sign > 0 ? h.multiply(i, θ) : h.multiply(i, θ).negate());
            sum = h.add(sum, h.multiply(h.multiply(phase, h.exp(h.multiply(a, h.log(x)))), h.hypergeometricU(a, b, x)));
        }
        return h.divide(sum, two);
    }
}
