package org.matheclipse.external.apfloat;

import java.util.ArrayList;
import java.util.List;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;

/**
 * Confluent hypergeometric function of the second kind <i>U</i>(<i>a</i>, <i>b</i>, <i>z</i>)
 * for an integer <i>b</i> and a large <i>z</i>.<p>
 *
 * For an integer <i>b</i>, <code>org.apfloat.HypergeometricHelper.hypergeometricU()</code> moves
 * <i>b</i> off the integer by a tiny offset and sums two <sub>1</sub><i>F</i><sub>1</sub> series,
 * one of them with the lower parameter 2 - <i>b</i>, next to a non-positive integer. For a large
 * <i>z</i> the series evaluation first searches the largest term with
 * <code>HypergeometricHelper.startTerm()</code>, which rounds the parameters to the precision of a
 * <code>double</code>. That puts 2 - <i>b</i> back onto the non-positive integer, the Pochhammer
 * symbol in <code>termScale()</code> is zero, and the method fails with "Division by zero", e.g.
 * for <code>hypergeometricU(15, 6, 60)</code> at 30 digits (apfloat 1.16.0).<p>
 *
 * The correction in apfloat is to skip the search for such a parameter, in <code>startTerm()</code>
 * after the parameters are rounded:
 * <pre>
 *         // A b that was moved off a nonpositive integer by a tiny offset (see hypergeometricU) is that integer again in double precision, the term scales would divide by zero
 *         if (Arrays.stream(b).anyMatch(ApcomplexMath::isNonPositiveInteger))
 *         {
 *             return null;
 *         }
 * </pre>
 * With it the series is summed from its first term, and apfloat gives the same values as this
 * class. When the correction is made in apfloat itself, this class is deleted.<p>
 *
 * Until then {@link #hypergeometricU(FixedPrecisionApfloatHelper, Apcomplex, Apcomplex, Apcomplex)}
 * calls apfloat, and where apfloat fails it evaluates <i>U</i> the way mpmath's
 * <code>hyperu</code> does: the asymptotic series
 * <i>z</i><sup>-<i>a</i></sup> <sub>2</sub><i>F</i><sub>0</sub>(<i>a</i>, 1+<i>a</i>-<i>b</i>; ; -1/<i>z</i>)
 * when it converges to the working precision, otherwise Kummer's connection formula
 * <p>
 * <i>U</i> = &pi;/sin(&pi;<i>b</i>) &middot; (<sub>1</sub><i>F</i><sub>1</sub>(<i>a</i>; <i>b</i>; <i>z</i>) / (&Gamma;(<i>a</i>-<i>b</i>+1) &Gamma;(<i>b</i>))
 * - <i>z</i><sup>1-<i>b</i></sup> <sub>1</sub><i>F</i><sub>1</sub>(<i>a</i>-<i>b</i>+1; 2-<i>b</i>; <i>z</i>) / (&Gamma;(<i>a</i>) &Gamma;(2-<i>b</i>)))
 * <p>
 * as a sum of {@link ApcomplexHypercombMath#hypercomb} terms, whose poles for an integer <i>b</i>
 * cancel: hypercomb moves <i>b</i> apart by a small amount and raises the working precision for
 * the digits the terms cancel.
 */

public class HypergeometricUWorkaround
{
    private HypergeometricUWorkaround()
    {
    }

    /**
     * <i>U</i>(<i>a</i>, <i>b</i>, <i>z</i>) by the apfloat routine of the helper, and by
     * {@link #hypergeometricU(Apcomplex, Apcomplex, Apcomplex, long)} where that routine fails
     * (it divides by zero for an integer <i>b</i> and a large <i>z</i>).
     *
     * @param h The helper, which gives the precision.
     * @param a The first parameter.
     * @param b The second parameter.
     * @param z The argument.
     *
     * @return <i>U</i>(<i>a</i>, <i>b</i>, <i>z</i>).
     *
     * @throws ArithmeticException If neither evaluation succeeds.
     */

    public static Apcomplex hypergeometricU(FixedPrecisionApfloatHelper h, Apcomplex a, Apcomplex b, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        try
        {
            return h.hypergeometricU(a, b, z);
        }
        catch (ArithmeticException ae)
        {
            if (ApcomplexHypercombMath.isZero(z))
            {
                throw ae;
            }
            return hypergeometricU(a, b, z, h.precision());
        }
    }

    /**
     * <i>U</i>(<i>a</i>, <i>b</i>, <i>z</i>).
     *
     * @param a The first parameter.
     * @param b The second parameter.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>U</i>(<i>a</i>, <i>b</i>, <i>z</i>) to the precision.
     *
     * @throws ArithmeticException If <i>z</i> is zero and the function is infinite there, or the sum doesn't reach the precision.
     */

    public static Apcomplex hypergeometricU(Apcomplex a, Apcomplex b, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int radix = z.radix();
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision);
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, radix);
        if (ApcomplexHypercombMath.isZero(z))
        {
            // U(a, b, 0) = Gamma(1-b)/Gamma(a-b+1) for Re(b) < 1
            if (b.real().compareTo(one) < 0)
            {
                return h.multiply(h.gamma(h.subtract(one, b)), ApcomplexHypercombMath.reciprocalGamma(h, h.add(h.subtract(a, b), one)));
            }
            throw new ApfloatArithmeticException("Division by zero", "divide.byZero");
        }
        if (ApcomplexHypercombMath.isZero(a))
        {
            return new Apfloat(1, precision, radix);
        }
        Apcomplex asymptotic = asymptotic(a, b, z, precision);
        if (asymptotic != null)
        {
            return asymptotic;
        }
        ApcomplexHypercombMath.Terms terms = (parameters, workingPrecision) ->
        {
            FixedPrecisionApfloatHelper w = new FixedPrecisionApfloatHelper(workingPrecision);
            Apcomplex pa = parameters[0],
                      pb = parameters[1];
            Apfloat pi = ApfloatMath.pi(workingPrecision, radix);
            Apcomplex sinPiB = sinPi(w, pb, pi);
            Apcomplex ab1 = w.add(w.subtract(pa, pb), one),
                      b2 = w.subtract(new Apfloat(2, Apfloat.INFINITE, radix), pb),
                      b1 = w.subtract(one, pb);
            Apcomplex[] none = new Apcomplex[0];
            List<ApcomplexHypercombMath.Term> list = new ArrayList<ApcomplexHypercombMath.Term>();
            // pi/sin(pi*b) * 1F1(a; b; z) / (Gamma(a-b+1)*Gamma(b))
            list.add(new ApcomplexHypercombMath.Term(new Apcomplex[] { pi, sinPiB }, new Apcomplex[] { one, one.negate() },
                                                     none, new Apcomplex[] { ab1, pb },
                                                     new Apcomplex[] { pa }, new Apcomplex[] { pb }, z));
            // -pi/sin(pi*b) * z^(1-b) * 1F1(a-b+1; 2-b; z) / (Gamma(a)*Gamma(2-b))
            list.add(new ApcomplexHypercombMath.Term(new Apcomplex[] { pi, sinPiB.negate(), z }, new Apcomplex[] { one, one.negate(), b1 },
                                                     none, new Apcomplex[] { pa, b2 },
                                                     new Apcomplex[] { ab1 }, new Apcomplex[] { b2 }, z));
            return list;
        };
        Apcomplex result = ApcomplexHypercombMath.hypercomb(terms, new Apcomplex[] { a, b }, precision);
        if (a.imag().signum() == 0 && b.imag().signum() == 0 && z.imag().signum() == 0 && z.real().signum() > 0)
        {
            // real parameters and a positive argument: the value is real, the imaginary part is rounding noise
            return result.real();
        }
        return result;
    }

    // sin(pi*b), exactly zero for an integer b, so that hypercomb sees the pole
    private static Apcomplex sinPi(FixedPrecisionApfloatHelper w, Apcomplex b, Apfloat pi)
    {
        if (b.imag().signum() == 0 && b.real().isInteger())
        {
            return Apcomplex.ZERO;
        }
        return w.sin(w.multiply(pi, b));
    }

    // The asymptotic series z^(-a) * Sum((a)_k*(1+a-b)_k/k!*(-1/z)^k), summed at ten more digits while its terms decrease; null if they grow before the sum reaches the working precision
    private static Apcomplex asymptotic(Apcomplex a, Apcomplex b, Apcomplex z, long precision)
    {
        long workingPrecision = precision + 10;
        FixedPrecisionApfloatHelper w = new FixedPrecisionApfloatHelper(workingPrecision);
        int radix = z.radix();
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, radix);
        Apcomplex wa = ApfloatHelperShim.ensurePrecision(a, workingPrecision),
                  wb = ApfloatHelperShim.ensurePrecision(b, workingPrecision),
                  wz = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        Apcomplex c = w.add(w.subtract(wa, wb), one);
        Apcomplex x = w.divide(one.negate(), wz);
        Apcomplex term = new Apfloat(1, workingPrecision, radix),
                  sum = term;
        long maxTerms = 4 * workingPrecision + 20;
        for (long k = 1; k <= maxTerms; k++)
        {
            Apfloat kk = new Apfloat(k, Apfloat.INFINITE, radix),
                    k1 = new Apfloat(k - 1, Apfloat.INFINITE, radix);
            Apcomplex next = w.divide(w.multiply(w.multiply(w.multiply(term, w.add(wa, k1)), w.add(c, k1)), x), kk);
            if (ApcomplexHypercombMath.isZero(next))
            {
                // a or 1+a-b is a non-positive integer: the series ends, the sum is exact
                return w.multiply(sum, w.pow(wz, wa.negate())).precision(precision);
            }
            if (w.abs(next).compareTo(w.abs(term)) > 0)
            {
                return null;
            }
            term = next;
            sum = w.add(sum, term);
            if (ApcomplexHypercombMath.isZero(sum))
            {
                return null;
            }
            if (term.scale() < sum.scale() - workingPrecision)
            {
                return w.multiply(sum, w.pow(wz, wa.negate())).precision(precision);
            }
        }
        return null;
    }
}
