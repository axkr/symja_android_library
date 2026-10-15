package org.matheclipse.external.apfloat;

import java.util.ArrayList;
import java.util.List;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * Sums of terms of the form
 * <i>w</i><sub>1</sub><sup><i>c</i><sub>1</sub></sup> &middot; &middot; &middot;
 * &Gamma;(&alpha;<sub>1</sub>) &middot; &middot; &middot; / &Gamma;(&beta;<sub>1</sub>) &middot; &middot; &middot;
 * <sub><i>p</i></sub><i>F</i><sub><i>q</i></sub>(<i>a</i>; <i>b</i>; <i>z</i>),
 * whose parameters depend on a list of parameters of the function which the sum represents.<p>
 *
 * Such a sum, as Slater's theorem gives for the Meijer G-function, has singular terms when two
 * parameters differ by an integer, although the function is finite there: the poles of the
 * terms cancel each other. The parameters are then moved apart by a small amount, so that the
 * terms are finite, and the sum is accepted when two different small amounts give the same value.
 * The digits which the terms cancel are added to the working precision.
 */

public class ApcomplexHypercombMath
{
    // Most extra digits of the working precision
    private static final long MAX_EXTRA_PRECISION = 1000;

    /**
     * One term of the sum: the product of the powers <i>w</i><sup><i>c</i></sup>, of
     * &Gamma;(&alpha;) and 1 / &Gamma;(&beta;) and of the generalized hypergeometric function.
     */

    public static final class Term
    {
        final Apcomplex[] bases,
                          exponents,
                          gammaNumerators,
                          gammaDenominators,
                          upper,
                          lower;
        final Apcomplex argument;

        /**
         * @param bases The bases <i>w</i> of the powers.
         * @param exponents The exponents <i>c</i> of the powers.
         * @param gammaNumerators The arguments &alpha; of the gamma functions in the numerator.
         * @param gammaDenominators The arguments &beta; of the gamma functions in the denominator.
         * @param upper The upper parameters <i>a</i> of the hypergeometric function.
         * @param lower The lower parameters <i>b</i> of the hypergeometric function.
         * @param argument The argument <i>z</i> of the hypergeometric function.
         */

        public Term(Apcomplex[] bases, Apcomplex[] exponents, Apcomplex[] gammaNumerators, Apcomplex[] gammaDenominators, Apcomplex[] upper, Apcomplex[] lower, Apcomplex argument)
        {
            this.bases = bases;
            this.exponents = exponents;
            this.gammaNumerators = gammaNumerators;
            this.gammaDenominators = gammaDenominators;
            this.upper = upper;
            this.lower = lower;
            this.argument = argument;
        }
    }

    /**
     * The terms of the sum for the given parameters.
     */

    @FunctionalInterface
    public interface Terms
    {
        /**
         * @param parameters The parameters, to the working precision.
         * @param precision The working precision.
         *
         * @return The terms.
         */

        List<Term> apply(Apcomplex[] parameters, long precision);
    }

    private ApcomplexHypercombMath()
    {
    }

    /**
     * The sum of the terms which the function gives for the parameters.
     *
     * @param function The terms of the sum.
     * @param parameters The parameters, which are moved apart where the terms are singular.
     * @param precision The precision of the result.
     *
     * @return The sum, to the precision.
     *
     * @throws ArithmeticException If the sum doesn't reach the precision within the largest working precision, or is infinite.
     */

    public static Apcomplex hypercomb(Terms function, Apcomplex[] parameters, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate a sum of hypergeometric functions to infinite precision", "hypercomb.infinitePrecision");
        }
        long workingPrecision = precision,
             perturbationExtra = 0;
        Apcomplex perturbedReference = null;
        while (true)
        {
            workingPrecision += 3;
            if (workingPrecision - precision > MAX_EXTRA_PRECISION + 2 * precision)
            {
                throw new ArithmeticException("Sum of hypergeometric functions does not reach the precision");
            }
            long beforeExtra = workingPrecision;
            Apcomplex[] params = withPrecision(parameters, workingPrecision);
            List<Term> terms = function.apply(params, workingPrecision);
            Check check = check(terms);
            workingPrecision += check.extraPrecision;
            if (check.perturb)
            {
                long magnitude = precision + 3 + perturbationExtra;
                workingPrecision = beforeExtra + 3 + magnitude + 3;
                int radix = radix(parameters);
                Apfloat h = ApfloatMath.scale(new Apfloat(1, workingPrecision, radix), -magnitude);
                params = withPrecision(parameters, workingPrecision);
                for (int k = 0; k < params.length; k++)
                {
                    params[k] = params[k].add(h);
                    // Different amounts for different parameters, so that two perturbations don't cancel in a difference
                    h = h.add(h.divide(new Apfloat(k + 1, workingPrecision, radix)));
                }
            }
            if (check.recompute)
            {
                params = withPrecision(params, workingPrecision);
                terms = function.apply(params, workingPrecision);
            }
            List<Term> remaining = new ArrayList<Term>();
            for (int i = 0; i < terms.size(); i++)
            {
                if (!check.discard.contains(i))
                {
                    remaining.add(terms.get(i));
                }
            }
            if (remaining.isEmpty())
            {
                return Apcomplex.ZERO;
            }
            List<Apcomplex> values = new ArrayList<Apcomplex>();
            for (Term term : remaining)
            {
                values.add(evaluate(term, workingPrecision));
            }
            Apcomplex sum = Apcomplex.ZERO;
            long largest = Long.MIN_VALUE;
            for (Apcomplex value : values)
            {
                sum = sum.add(value);
                if (!isZero(value))
                {
                    largest = Math.max(largest, value.scale());
                }
            }
            if (largest == Long.MIN_VALUE)
            {
                // All terms are zero
                return Apcomplex.ZERO;
            }
            if (values.size() == 1 && !check.perturb)
            {
                return round(sum, precision);
            }
            long cancellation = (isZero(sum) ? workingPrecision : largest - sum.scale());
            boolean precisionOk = cancellation < workingPrecision - precision;
            if (precisionOk && !check.perturb)
            {
                return round(sum, precision);
            }
            else if (precisionOk)
            {
                if (perturbedReference == null)
                {
                    perturbationExtra += 6;
                    perturbedReference = sum;
                    continue;
                }
                if (GuardDigitsHelper.agree(sum, perturbedReference, precision))
                {
                    return round(sum, precision);
                }
                perturbationExtra *= 2;
                perturbedReference = sum;
            }
            else
            {
                workingPrecision += Math.min(Math.max(cancellation, precision / 2), Math.max(check.extraPrecision, precision));
            }
        }
    }

    /**
     * 1 / &Gamma;(<i>x</i>), which is zero at the poles of the gamma function.
     *
     * @param h The helper of the working precision.
     * @param x The argument.
     *
     * @return 1 / &Gamma;(<i>x</i>).
     */

    public static Apcomplex reciprocalGamma(FixedPrecisionApfloatHelper h, Apcomplex x)
    {
        if (isNonPositiveInteger(x))
        {
            return Apcomplex.ZERO;
        }
        return h.divide(new Apfloat(1, h.precision(), x.radix()), h.gamma(x));
    }

    // The sum, which has more correct digits than the precision, rounded to the precision
    private static Apcomplex round(Apcomplex z, long precision)
    {
        return new Apcomplex(round(z.real(), precision), round(z.imag(), precision));
    }

    private static Apfloat round(Apfloat x, long precision)
    {
        if (x.signum() == 0 || x.precision() <= precision)
        {
            return x;
        }
        return ApfloatMath.roundToPrecision(x, precision, java.math.RoundingMode.HALF_EVEN);
    }

    // Is the value exactly a non-positive integer?
    static boolean isNonPositiveInteger(Apcomplex x)
    {
        return x.imag().signum() == 0 && x.real().signum() <= 0 && x.real().isInteger();
    }

    // Is the value exactly zero?
    static boolean isZero(Apcomplex x)
    {
        return x.real().signum() == 0 && x.imag().signum() == 0;
    }

    // The value of one term
    private static Apcomplex evaluate(Term term, long workingPrecision)
    {
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apcomplex value = (isZero(term.argument) ? new Apfloat(1, workingPrecision, term.argument.radix())
                                                 : hypergeometric(h, withFinitePrecision(term.upper, workingPrecision),
                                                                  withFinitePrecision(term.lower, workingPrecision),
                                                                  finitePrecision(term.argument, workingPrecision)));
        for (Apcomplex alpha : term.gammaNumerators)
        {
            value = h.multiply(value, h.gamma(ApfloatHelperShim.ensurePrecision(alpha, workingPrecision)));
        }
        for (Apcomplex beta : term.gammaDenominators)
        {
            value = h.multiply(value, reciprocalGamma(h, ApfloatHelperShim.ensurePrecision(beta, workingPrecision)));
        }
        for (int i = 0; i < term.bases.length; i++)
        {
            value = h.multiply(value, power(h, term.bases[i], term.exponents[i]));
        }
        return value;
    }

    // The series, and next to the unit circle, where it declines, the transformations of 2F1
    private static Apcomplex hypergeometric(FixedPrecisionApfloatHelper h, Apcomplex[] upper, Apcomplex[] lower, Apcomplex z)
    {
        try
        {
            return ApcomplexHypergeometricMath.hypergeometricPFQ(upper, lower, z);
        }
        catch (ArithmeticException ae)
        {
            if (upper.length == 2 && lower.length == 1 && !isNonPositiveInteger(lower[0]))
            {
                return h.hypergeometric2F1(upper[0], upper[1], lower[0], z);
            }
            throw ae;
        }
    }

    // w^c on the principal branch, with 0^0 = 1 and 0^c = 0 for Re(c) > 0
    private static Apcomplex power(FixedPrecisionApfloatHelper h, Apcomplex w, Apcomplex c)
    {
        long workingPrecision = h.precision();
        if (isZero(c))
        {
            return new Apfloat(1, workingPrecision, w.radix());
        }
        if (isZero(w))
        {
            if (c.real().signum() > 0)
            {
                return Apcomplex.ZERO;
            }
            throw new ArithmeticException("Power of zero is infinite");
        }
        return h.pow(ApfloatHelperShim.ensurePrecision(w, workingPrecision), ApfloatHelperShim.ensurePrecision(c, workingPrecision));
    }

    private static Apcomplex[] withPrecision(Apcomplex[] values, long workingPrecision)
    {
        Apcomplex[] result = new Apcomplex[values.length];
        for (int i = 0; i < values.length; i++)
        {
            result[i] = ApfloatHelperShim.ensurePrecision(values[i], workingPrecision);
        }
        return result;
    }

    // The values with exactly the working precision, also the exact ones; zero stays as it is
    private static Apcomplex[] withFinitePrecision(Apcomplex[] values, long workingPrecision)
    {
        Apcomplex[] result = new Apcomplex[values.length];
        for (int i = 0; i < values.length; i++)
        {
            result[i] = finitePrecision(values[i], workingPrecision);
        }
        return result;
    }

    private static Apcomplex finitePrecision(Apcomplex z, long workingPrecision)
    {
        return new Apcomplex(z.real().signum() == 0 ? z.real() : z.real().precision(workingPrecision),
                             z.imag().signum() == 0 ? z.imag() : z.imag().precision(workingPrecision));
    }

    private static int radix(Apcomplex[] values)
    {
        return (values.length == 0 ? 10 : values[0].radix());
    }

    // What the terms need: moved parameters, more digits, or nothing; and which are zero
    private static final class Check
    {
        boolean perturb,
                recompute;
        long extraPrecision;
        List<Integer> discard = new ArrayList<Integer>();
    }

    private static Check check(List<Term> terms)
    {
        Check check = new Check();
        for (int index = 0; index < terms.size(); index++)
        {
            Term term = terms.get(index);
            boolean singularWeight = false;
            for (int k = 0; k < term.bases.length; k++)
            {
                if (isZero(term.bases[k]) && term.exponents[k].real().signum() <= 0 && !isZero(term.exponents[k]))
                {
                    check.perturb = check.recompute = true;
                    singularWeight = true;
                }
            }
            // Poles of the gamma functions in the numerator, in the denominator (which are zeros), and of the series
            int[] poles = new int[3];
            Apcomplex[][] data = { term.gammaNumerators, term.gammaDenominators, term.lower };
            for (int which = 0; which < 3; which++)
            {
                for (Apcomplex x : data[which])
                {
                    long[] distance = nearestInteger(x);
                    if (distance[0] > 0)
                    {
                        continue;
                    }
                    if (distance[1] == Long.MIN_VALUE)
                    {
                        // A pole of the series doesn't count if the series ends before it
                        if (which == 2 && endsBefore(term.upper, distance[0]))
                        {
                            continue;
                        }
                        poles[which]++;
                    }
                    else if (distance[1] < 0)
                    {
                        check.extraPrecision += -distance[1];
                        check.recompute = true;
                    }
                }
            }
            if (poles[1] > poles[0] + poles[2] && !singularWeight)
            {
                check.discard.add(index);
            }
            else if (poles[0] + poles[1] + poles[2] > 0)
            {
                check.perturb = check.recompute = true;
            }
        }
        return check;
    }

    // Does the series end, at a non-positive integer upper parameter, before the term n?
    private static boolean endsBefore(Apcomplex[] upper, long n)
    {
        for (Apcomplex u : upper)
        {
            if (isNonPositiveInteger(u) && u.real().longValue() >= n)
            {
                return true;
            }
        }
        return false;
    }

    // The nearest integer to x, and the scale of the distance to it: Long.MIN_VALUE if x is that integer
    private static long[] nearestInteger(Apcomplex x)
    {
        Apfloat real = x.real();
        int radix = x.radix();
        Apint one = new Apint(1, radix),
              floor = real.floor(),
              n = (real.subtract(floor).multiply(new Apint(2, radix)).compareTo(one) >= 0 ? floor.add(one) : floor);
        long nearest;
        if (n.compareTo(new Apint(Long.MAX_VALUE / 2, radix)) > 0)
        {
            nearest = Long.MAX_VALUE / 2;
        }
        else if (n.compareTo(new Apint(Long.MIN_VALUE / 2, radix)) < 0)
        {
            nearest = Long.MIN_VALUE / 2;
        }
        else
        {
            nearest = n.longValue();
        }
        Apfloat realDistance = ApcomplexMath.abs(real.subtract(n)),
                imagDistance = ApcomplexMath.abs(x.imag());
        if (realDistance.signum() == 0 && imagDistance.signum() == 0)
        {
            return new long[] { nearest, Long.MIN_VALUE };
        }
        long scale = Math.max(realDistance.signum() == 0 ? Long.MIN_VALUE + 1 : realDistance.scale(),
                              imagDistance.signum() == 0 ? Long.MIN_VALUE + 1 : imagDistance.scale());
        // Next to the integer when the distance is below a tenth
        return new long[] { nearest, (scale <= -1 ? scale : 0) };
    }
}
