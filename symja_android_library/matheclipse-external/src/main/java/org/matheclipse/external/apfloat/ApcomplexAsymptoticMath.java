package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;

/**
 * Asymptotic expansions of special functions for {@link Apcomplex} arguments, for the
 * arguments where the series which define the functions are impractically slow.<p>
 *
 * Each method sums an asymptotic series to its smallest term, and throws an
 * <code>ArithmeticException</code> if that doesn't reach the precision of the arguments.
 * The caller is expected to use the convergent series of the function then, for example the
 * method of the same name in <code>org.apfloat.ApcomplexMath</code>.<p>
 *
 * The precision of the result is the smallest precision of the arguments.
 */

public class ApcomplexAsymptoticMath
{
    private ApcomplexAsymptoticMath()
    {
    }

    // The asymptotic series are summed to their smallest term, but at most to this many terms
    private static final int MAX_TERMS = 200;

    /**
     * Struve function <b>H</b><sub>&nu;</sub>(<i>z</i>) for a large argument, by
     * <b>H</b><sub>&nu;</sub>(<i>z</i>) = <i>Y</i><sub>&nu;</sub>(<i>z</i>) + 1/&pi; &sum; &Gamma;(<i>k</i> + 1/2) / &Gamma;(&nu; + 1/2 - <i>k</i>) (<i>z</i>/2)<sup>&nu;-2k-1</sup>.
     *
     * @param ν The order, which is not an integer.
     * @param z The argument, with a positive real part.
     *
     * @return <b>H</b><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the expansion doesn't reach the precision for these arguments.
     *
     * @see <a href="https://dlmf.nist.gov/11.6#E1">DLMF 11.6.1</a>, the expansion of <b>H</b> - <i>Y</i> which is summed here
     * @see <a href="https://functions.wolfram.com/Bessel-TypeFunctions/StruveH/26/01/02/">The hypergeometric representation</a>, which mpmath and apfloat sum for any argument
     */

    public static Apcomplex struveH(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = finitePrecision(ν, z);
        if (z.real().signum() <= 0 || isInteger(ν))
        {
            // The Bessel function Y of an integer order is a limit, which loses digits next to it
            throw new ArithmeticException("Struve H expansion needs a positive real part of the argument and a non-integer order");
        }
        requireLargeArgument(ν, z, "Struve H");
        FixedPrecisionApfloatHelper h = helper(precision);
        Apfloat[] error = new Apfloat[1];
        Apcomplex correction = struveCorrection(ν, z, false, h, error),
                  bessel = h.besselY(ν, z),
                  result = h.add(bessel, correction);
        requireNoCancellation(result, bessel, correction, "Struve H");
        return accept(result, error[0], precision, "Struve H");
    }

    /**
     * Modified Struve function <b>L</b><sub>&nu;</sub>(<i>z</i>) for a large argument, by
     * <b>L</b><sub>&nu;</sub>(<i>z</i>) = <i>I</i><sub>-&nu;</sub>(<i>z</i>) - 1/&pi; &sum; (-1)<sup>k</sup> &Gamma;(<i>k</i> + 1/2) / &Gamma;(&nu; + 1/2 - <i>k</i>) (<i>z</i>/2)<sup>&nu;-2k-1</sup>.<p>
     *
     * The expansion leaves out a multiple of <i>K</i><sub>&nu;</sub>(<i>z</i>), which is of the relative
     * size <i>e</i><sup>-2 Re z</sup>. The real part of the argument has to be large enough for that to
     * be below the precision.
     *
     * @param ν The order.
     * @param z The argument, with a positive real part.
     *
     * @return <b>L</b><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the expansion doesn't reach the precision for these arguments.
     *
     * @see <a href="https://dlmf.nist.gov/11.6#E2">DLMF 11.6.2</a>, the expansion of <b>L</b> - <i>I</i> which is summed here
     * @see <a href="https://functions.wolfram.com/Bessel-TypeFunctions/StruveL/26/01/02/">The hypergeometric representation</a>, which mpmath and apfloat sum for any argument
     */

    public static Apcomplex struveL(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = finitePrecision(ν, z);
        // e^(-2 Re z) < radix^-(precision + 2)
        if (z.real().signum() <= 0 ||
            2.0 * z.real().doubleValue() < (precision + 2) * Math.log(z.radix()))
        {
            throw new ArithmeticException("Struve L expansion needs a larger real part of the argument");
        }
        requireLargeArgument(ν, z, "Struve L");
        FixedPrecisionApfloatHelper h = helper(precision);
        Apfloat[] error = new Apfloat[1];
        Apcomplex correction = struveCorrection(ν, z, true, h, error),
                  bessel = h.besselI(ν.negate(), z),
                  result = h.subtract(bessel, correction);
        requireNoCancellation(result, bessel, correction, "Struve L");
        return accept(result, error[0], precision, "Struve L");
    }

    // 1/pi sum s_k Gamma(k + 1/2) / Gamma(nu + 1/2 - k) (z/2)^(nu-2k-1) with s_k = 1, or (-1)^k when alternating
    private static Apcomplex struveCorrection(Apcomplex ν, Apcomplex z, boolean alternating, FixedPrecisionApfloatHelper h, Apfloat[] error)
    {
        int radix = z.radix();
        Apint one = ApfloatHelperShim.ONES[radix],
              two = new Apint(2, radix);
        Apfloat half = h.divide(one, two);
        Apcomplex νPlusHalf = h.add(ν, half);
        if (isInteger(νPlusHalf) && νPlusHalf.real().signum() <= 0)
        {
            // Gamma(nu + 1/2) has a pole and the leading terms vanish
            throw new ArithmeticException("Struve expansion at a pole of the gamma function");
        }
        Apcomplex halfZ = h.multiply(z, half),
                  step = h.divide(one, h.multiply(halfZ, halfZ));
        if (alternating)
        {
            step = step.negate();
        }
        // k = 0: Gamma(1/2) / Gamma(nu + 1/2) (z/2)^(nu-1)
        Apcomplex term = h.multiply(h.divide(h.sqrt(h.pi()), h.gamma(νPlusHalf)),
                                    h.exp(h.multiply(h.subtract(ν, one), h.log(halfZ)))),
                  sum = ApfloatHelperShim.ZEROS[radix];
        Apfloat previous = null,
                smallest = null;
        for (int k = 0; k < MAX_TERMS; k++)
        {
            Apfloat size = ApcomplexMath.abs(term);
            if (previous != null && size.compareTo(previous) > 0)
            {
                // An asymptotic series says nothing past its smallest term
                break;
            }
            sum = h.add(sum, term);
            previous = smallest = size;
            if (size.signum() == 0)
            {
                // The series terminates at a half-integer order
                break;
            }
            // term(k+1) / term(k) = (k + 1/2) (nu - 1/2 - k) (z/2)^-2
            Apint kInt = new Apint(k, radix);
            term = h.multiply(h.multiply(h.multiply(term, h.add(kInt, half)), h.subtract(h.subtract(ν, half), kInt)), step);
        }
        error[0] = h.divide(smallest, h.pi());
        return h.divide(sum, h.pi());
    }

    /**
     * Anger function <b>J</b><sub>&nu;</sub>(<i>z</i>) for a real order which is large against the
     * argument, by the expansion of 1/&pi; &int;<sub>0</sub><sup>&pi;</sup> cos(&nu; <i>t</i> - <i>z</i> sin <i>t</i>) d<i>t</i>
     * at the two ends of the interval, in powers of 1/&nu;.<p>
     *
     * The order is reduced modulo 2 for <i>e</i><sup>i &pi; &nu;</sup>, which takes the digits in front of
     * the decimal point away from its precision: the precision of the result is the smaller
     * one of the precision of the argument and the number of digits of the order after the
     * decimal point.
     *
     * @param ν The order, which is not an integer.
     * @param z The argument, with |<i>z</i>| &lt; |&nu;|.
     *
     * @return <b>J</b><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the expansion doesn't reach the precision for these arguments.
     *
     * @see <a href="https://dlmf.nist.gov/11.10#E1">DLMF 11.10.1</a>, the integral which is expanded here
     * @see <a href="https://dlmf.nist.gov/11.11#ii">DLMF 11.11(ii)</a>, the expansion for a large order
     */

    public static Apcomplex angerJ(Apfloat ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long fractionPrecision = (ν.precision() == Apfloat.INFINITE ? Apfloat.INFINITE : ν.precision() - Math.max(ν.scale(), 0)),
             precision = Math.min(fractionPrecision, z.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new org.apfloat.InfiniteExpansionException("Cannot calculate Anger function to infinite precision", "angerJ.infinitePrecision");
        }
        if (precision <= 0)
        {
            throw new ArithmeticException("Anger expansion has no digits of the order left");
        }
        if (ν.isInteger())
        {
            // The expansion vanishes identically: what is left is the Bessel function, which it doesn't see
            throw new ArithmeticException("Anger expansion at an integer order");
        }
        if (ApcomplexMath.abs(z).compareTo(ApfloatMath.abs(ν)) >= 0)
        {
            throw new ArithmeticException("Anger expansion needs an order larger than the argument");
        }
        FixedPrecisionApfloatHelper h = helper(precision);
        Apfloat νModTwo = ν.mod(new Apint(2, ν.radix()));
        // cos(nu t - z sin t) is the half sum of the two exponentials, and the second one is the first one
        // with both arguments negated
        Apfloat[] error = new Apfloat[2];
        Apcomplex forward = angerEndpointSum(ν, νModTwo, z, h, error, 0),
                  backward = angerEndpointSum(ν.negate(), νModTwo.negate(), z.negate(), h, error, 1);
        Apfloat twoPi = h.multiply(h.pi(), new Apint(2, z.radix()));
        Apcomplex result = h.divide(h.add(forward, backward), twoPi);
        return accept(result, h.divide(h.add(error[0], error[1]), twoPi), precision, "Anger");
    }

    // sum c_m (e^(i pi nu) - (-1)^m) / (i nu)^(m+1), summed to its smallest term, whose size goes to error[slot]
    private static Apcomplex angerEndpointSum(Apfloat ν, Apfloat νModTwo, Apcomplex z, FixedPrecisionApfloatHelper h, Apfloat[] error, int slot)
    {
        int radix = z.radix(),
            maxTerms = 24;
        Apint one = ApfloatHelperShim.ONES[radix];
        Apcomplex i = new Apcomplex(ApfloatHelperShim.ZEROS[radix], one),
                  expIPiν = h.exp(h.multiply(h.multiply(i, h.pi()), νModTwo)),
                  iν = h.multiply(i, ν),
                  sum = ApfloatHelperShim.ZEROS[radix],
                  power = one;
        Apcomplex[] taylor = expOfMinusISin(z, maxTerms, h);
        Apfloat factorial = one,
                previous = null,
                smallest = ApfloatHelperShim.ZEROS[radix];
        for (int m = 0; m <= maxTerms; m++)
        {
            if (m > 0)
            {
                factorial = h.multiply(factorial, new Apint(m, radix));
            }
            // c_m = m! [t^m] e^(-i z sin t)
            Apcomplex term = h.multiply(h.multiply(taylor[m], factorial),
                                        (m & 1) == 0 ? h.subtract(expIPiν, one) : h.add(expIPiν, one));
            power = h.multiply(power, iν);
            term = h.divide(term, power);
            Apfloat size = ApcomplexMath.abs(term);
            if (previous != null && size.compareTo(previous) > 0)
            {
                // An asymptotic series says nothing past its smallest term
                break;
            }
            sum = h.add(sum, term);
            previous = smallest = size;
        }
        error[slot] = smallest;
        return sum;
    }

    // The Taylor coefficients of E = e^S with S = -i z sin t at t = 0, by E_n = 1/n sum k S_k E_(n-k)
    private static Apcomplex[] expOfMinusISin(Apcomplex z, int maxTerms, FixedPrecisionApfloatHelper h)
    {
        int radix = z.radix();
        Apint one = ApfloatHelperShim.ONES[radix];
        Apcomplex w = h.multiply(new Apcomplex(ApfloatHelperShim.ZEROS[radix], one).negate(), z);
        Apcomplex[] sine = new Apcomplex[maxTerms + 1];
        java.util.Arrays.fill(sine, ApfloatHelperShim.ZEROS[radix]);
        Apfloat factorial = one;
        for (int k = 1, sign = 1; k <= maxTerms; k += 2, sign = -sign)
        {
            for (int j = (k == 1 ? 1 : k - 1); j <= k; j++)
            {
                factorial = h.multiply(factorial, new Apint(j, radix));
            }
            Apcomplex coefficient = h.divide(w, factorial);
            sine[k] = (sign > 0 ? coefficient : coefficient.negate());
        }
        Apcomplex[] result = new Apcomplex[maxTerms + 1];
        result[0] = one;
        for (int n = 1; n <= maxTerms; n++)
        {
            Apcomplex sum = ApfloatHelperShim.ZEROS[radix];
            for (int k = 1; k <= n; k++)
            {
                sum = h.add(sum, h.multiply(h.multiply(sine[k], new Apint(k, radix)), result[n - k]));
            }
            result[n] = h.divide(sum, new Apint(n, radix));
        }
        return result;
    }

    /**
     * Hermite function <i>H</i><sub>&nu;</sub>(<i>z</i>) of any order for a large argument, by
     * <i>H</i><sub>&nu;</sub>(<i>z</i>) = (2 <i>z</i>)<sup>&nu;</sup> &sum; (-&nu;/2)<sub>k</sub> ((1 - &nu;)/2)<sub>k</sub> / <i>k</i>! (-1/<i>z</i><sup>2</sup>)<sup>k</sup>,
     * which terminates at a nonnegative integer order.
     *
     * @param ν The order.
     * @param z The argument, with a positive real part.
     *
     * @return <i>H</i><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the expansion doesn't reach the precision for these arguments.
     *
     * @see <a href="https://functions.wolfram.com/HypergeometricFunctions/HermiteHGeneral/06/02/">The expansions of the Hermite function at infinity</a>
     */

    public static Apcomplex hermiteH(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = finitePrecision(ν, z);
        if (z.real().signum() <= 0)
        {
            throw new ArithmeticException("Hermite expansion needs a positive real part of the argument");
        }
        FixedPrecisionApfloatHelper h = helper(precision);
        int radix = z.radix();
        Apint one = ApfloatHelperShim.ONES[radix],
              two = new Apint(2, radix);
        // -1/z^2, the variable of the series
        Apcomplex argument = h.divide(one, h.multiply(z, z)).negate(),
                  a = h.divide(ν, two).negate(),
                  b = h.divide(h.subtract(one, ν), two),
                  sum = one,
                  term = one;
        Apfloat previous = null,
                smallest = ApfloatHelperShim.ZEROS[radix];
        for (int k = 0; k < MAX_TERMS; k++)
        {
            Apint kInt = new Apint(k, radix);
            term = h.multiply(h.divide(h.multiply(h.multiply(term, h.add(a, kInt)), h.add(b, kInt)), new Apint(k + 1L, radix)), argument);
            Apfloat size = ApcomplexMath.abs(term);
            if (previous != null && size.compareTo(previous) > 0)
            {
                // An asymptotic series says nothing past its smallest term
                break;
            }
            sum = h.add(sum, term);
            previous = smallest = size;
            if (size.signum() == 0)
            {
                // A nonnegative integer order terminates the series
                break;
            }
        }
        Apcomplex power = h.exp(h.multiply(ν, h.log(h.multiply(two, z))));
        return accept(h.multiply(power, sum), h.multiply(ApcomplexMath.abs(power), smallest), precision, "Hermite");
    }

    // The expansions in 1/z are not uniform in the order: up to it, and a little above it, the smallest term of the
    // series says nothing about the error
/**
     * Anger function <b>J</b><sub>&nu;</sub>(<i>z</i>) for an argument with a positive real part
     * which is large against the order, from its difference to the Bessel function
     * <i>J</i><sub>&nu;</sub>(<i>z</i>).
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <b>J</b><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the expansion doesn't reach the precision of the arguments.
     *
     * @see <a href="https://dlmf.nist.gov/11.11#E2">DLMF 11.11.2</a>, the expansion which is summed here
     */

    public static Apcomplex angerJLargeArgument(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return angerWeberLargeArgument(ν, z, false);
    }

    /**
     * Weber function <b>E</b><sub>&nu;</sub>(<i>z</i>) for an argument with a positive real part
     * which is large against the order, from its difference to the Bessel function
     * -<i>Y</i><sub>&nu;</sub>(<i>z</i>).
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <b>E</b><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the expansion doesn't reach the precision of the arguments.
     *
     * @see <a href="https://dlmf.nist.gov/11.11#E3">DLMF 11.11.3</a>, the expansion which is summed here
     */

    public static Apcomplex weberELargeArgument(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return angerWeberLargeArgument(ν, z, true);
    }

    private static Apcomplex angerWeberLargeArgument(Apcomplex ν, Apcomplex z, boolean weber)
    {
        String name = (weber ? "Weber E" : "Anger J");
        long precision = finitePrecision(ν, z);
        if (z.real().signum() <= 0)
        {
            throw new ArithmeticException(name + " expansion needs a positive real part of the argument");
        }
        requireLargeArgument(ν, z, name);
        FixedPrecisionApfloatHelper h = helper(precision);
        long workingPrecision = h.precision();
        ν = h.valueOf(ν);
        z = h.valueOf(z);
        Apfloat one = new Apfloat(1, workingPrecision, z.radix()),
                pi = h.pi();
        // The sums of F(k) / z^(2k) and G(k) / z^(2k) with F(k) = (nu^2 - 1^2)(nu^2 - 3^2)...(nu^2 - (2k-1)^2)
        // and G(k) = (nu^2 - 2^2)(nu^2 - 4^2)...(nu^2 - (2k)^2)
        Apcomplex ν2 = h.multiply(ν, ν),
                  z2 = h.multiply(z, z),
                  f = one,
                  g = one,
                  sumF = Apcomplex.ZERO,
                  sumG = Apcomplex.ZERO;
        Apfloat smallest = null,
                epsilon = ApfloatMath.scale(one, -workingPrecision);
        boolean converged = false;
        for (int k = 0; k < MAX_TERMS; k++)
        {
            Apfloat size = h.add(h.abs(f), h.abs(g));
            if (smallest != null && size.compareTo(smallest) > 0)
            {
                // The terms of the asymptotic series grow again
                break;
            }
            smallest = size;
            sumF = h.add(sumF, f);
            sumG = h.add(sumG, g);
            if (size.compareTo(epsilon) < 0)
            {
                converged = true;
                break;
            }
            long odd = 2L * k + 1,
                 even = 2L * k + 2;
            f = h.divide(h.multiply(f, h.subtract(ν2, new Apfloat(odd * odd, workingPrecision, z.radix()))), z2);
            g = h.divide(h.multiply(g, h.subtract(ν2, new Apfloat(even * even, workingPrecision, z.radix()))), z2);
        }
        if (!converged && smallest.scale() > -precision)
        {
            // Before the Bessel function is paid for
            throw new ArithmeticException(name + " expansion doesn't reach the precision");
        }
        Apcomplex πν = h.multiply(ν, pi),
                  πz = h.multiply(z, pi),
                  bessel,
                  correction;
        if (weber)
        {
            // -Y - (1 + cos(pi nu)) / (pi z) sumF - nu (1 - cos(pi nu)) / (pi z^2) sumG
            Apcomplex cos = h.cos(πν);
            bessel = ApcomplexBesselMath.besselY(ν, z).negate();
            correction = h.add(h.divide(h.multiply(h.add(one, cos), sumF), πz),
                               h.divide(h.multiply(h.multiply(ν, h.subtract(one, cos)), sumG), h.multiply(πz, z))).negate();
        }
        else
        {
            // J + sin(pi nu) / (pi z) (sumF - nu / z sumG)
            bessel = h.besselJ(ν, z);
            correction = h.divide(h.multiply(h.sin(πν), h.subtract(sumF, h.divide(h.multiply(ν, sumG), z))), πz);
        }
        Apcomplex result = h.add(bessel, correction);
        requireNoCancellation(result, bessel, correction, name);
        Apfloat error = (converged ? Apfloat.ZERO : h.divide(smallest, h.abs(πz)));
        return accept(result, error, precision, name);
    }

        private static void requireLargeArgument(Apcomplex ν, Apcomplex z, String name)
    {
        int radix = z.radix();
        if (ApcomplexMath.abs(z).multiply(new Apint(4, radix)).compareTo(ApcomplexMath.abs(ν).multiply(new Apint(5, radix))) <= 0)
        {
            throw new ArithmeticException(name + " expansion needs an argument larger than the order");
        }
    }

    // The helper pads the digits which a sum cancels, so a sum far below its terms has none
    private static void requireNoCancellation(Apcomplex sum, Apcomplex x, Apcomplex y, String name)
    {
        if ((sum.real().signum() == 0 && sum.imag().signum() == 0) ||
            sum.scale() < Math.max(x.scale(), y.scale()) - ApfloatHelperShim.EXTRA_PRECISION / 2)
        {
            throw new ArithmeticException(name + " expansion loses the precision to cancellation");
        }
    }

    private static long finitePrecision(Apcomplex ν, Apcomplex z)
    {
        long precision = Math.min(ν.precision(), z.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new org.apfloat.InfiniteExpansionException("Cannot calculate asymptotic expansion to infinite precision", "asymptotic.infinitePrecision");
        }
        return precision;
    }

    private static FixedPrecisionApfloatHelper helper(long precision)
    {
        return new FixedPrecisionApfloatHelper(ApfloatHelperShim.extendPrecision(precision));
    }

    private static boolean isInteger(Apcomplex z)
    {
        return z.imag().signum() == 0 && z.real().isInteger();
    }

    // The result with the precision, if the smallest term of the series is below its last digit
    private static Apcomplex accept(Apcomplex result, Apfloat error, long precision, String name)
    {
        if (error.signum() != 0 &&
            (result.real().signum() == 0 && result.imag().signum() == 0 || error.scale() > result.scale() - precision - 1))
        {
            throw new ArithmeticException(name + " expansion doesn't reach the precision");
        }
        return ApfloatHelperShim.limitPrecision(result, precision);
    }
}
