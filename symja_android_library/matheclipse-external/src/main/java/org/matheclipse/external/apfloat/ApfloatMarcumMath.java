package org.matheclipse.external.apfloat;

import java.math.RoundingMode;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * The generalized Marcum Q-function for {@link Apfloat} arguments.
 */

public class ApfloatMarcumMath
{
    private ApfloatMarcumMath()
    {
    }

    // The most terms of the Poisson series which are summed: their number grows with a^2/2 and b^2/2
    private static final long MAX_TERMS = 1000000;

    // The series is summed for a^2/2 and b^2/2 up to this: above it, it needs more than MAX_TERMS terms
    private static final double MAX_SERIES_ARGUMENT = 1.0e12;

    // The most extra digits for a difference Q_m(a, b0) - Q_m(a, b1) which cancels
    private static final long MAX_CANCELLED_DIGITS = 100000;

    /**
     * The generalized Marcum Q-function
     * <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i>) = 1/<i>a</i><sup>m-1</sup> &int;<sub>b</sub><sup>&infin;</sup> <i>x</i><sup>m</sup> <i>e</i><sup>-(x&sup2;+a&sup2;)/2</sup> <i>I</i><sub>m-1</sub>(<i>a x</i>) d<i>x</i>,
     * the probability that a noncentral chi-square variable with 2<i>m</i> degrees of freedom and
     * the noncentrality <i>a</i><sup>2</sup> is above <i>b</i><sup>2</sup>.<p>
     *
     * It is the sum of the regularized upper incomplete gamma functions <i>Q</i>(<i>m</i>+<i>k</i>, <i>b</i><sup>2</sup>/2),
     * weighted with the probabilities <i>e</i><sup>-&lambda;</sup> &lambda;<sup>k</sup>/<i>k</i>! of a Poisson distribution
     * with the mean &lambda; = <i>a</i><sup>2</sup>/2. All the terms are positive, so the sum keeps the precision also where
     * the value is close to 0 or 1.
     *
     * @param m The order, a positive number.
     * @param a The noncentrality parameter.
     * @param b The lower limit of the integral, a number which is not negative.
     *
     * @return <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i>)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision and the result is not one.
     * @throws ArithmeticException If <i>m</i> is not positive, if <i>b</i> is negative, if <i>a</i><sup>2</sup>/2 or <i>b</i><sup>2</sup>/2 is above 10<sup>12</sup> or if the series needs more than a million terms.
     *
     * @see <a href="https://doi.org/10.1109/TIT.1960.1057560">J. I. Marcum, A statistical theory of target detection by pulsed radar, IRE Trans. Inform. Theory 6 (1960)</a>
     * @see <a href="https://doi.org/10.1145/2591004">A. Gil, J. Segura, N. M. Temme, Algorithm 939: Computation of the Marcum Q-function, ACM Trans. Math. Softw. 40 (2014)</a>
     */

    public static Apfloat marcumQ(Apfloat m, Apfloat a, Apfloat b)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(m.precision(), Math.min(a.precision(), b.precision()));
        if (precision == Apfloat.INFINITE && b.signum() != 0)
        {
            throw new InfiniteExpansionException("Cannot calculate Marcum Q to infinite precision", "marcumQ.infinitePrecision");
        }
        return marcumQ(m, a, b, precision);
    }

    /**
     * The generalized Marcum Q-function to the given precision.
     *
     * @param m The order, a positive number.
     * @param a The noncentrality parameter.
     * @param b The lower limit of the integral, a number which is not negative.
     * @param precision The precision of the result.
     *
     * @return <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i>)
     *
     * @throws ArithmeticException If <i>m</i> is not positive, if <i>b</i> is negative, if <i>a</i><sup>2</sup>/2 or <i>b</i><sup>2</sup>/2 is above 10<sup>12</sup> or if the series needs more than a million terms.
     */

    public static Apfloat marcumQ(Apfloat m, Apfloat a, Apfloat b, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        checkArguments(m, b);
        int radix = m.radix();
        if (b.signum() == 0)
        {
            return new Apfloat(1, precision, radix);
        }
        return ApfloatMath.roundToPrecision(series(m, ApfloatMath.abs(a), b, precision), precision, RoundingMode.HALF_EVEN);
    }

    /**
     * The difference <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i><sub>0</sub>) - <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i><sub>1</sub>)
     * of two generalized Marcum Q-functions to the given precision, for <i>b</i><sub>0</sub> &lt; <i>b</i><sub>1</sub> the probability
     * that a noncentral chi-square variable is between <i>b</i><sub>0</sub><sup>2</sup> and <i>b</i><sub>1</sub><sup>2</sup>. The two functions are calculated
     * with as many extra digits as cancel in the difference.
     *
     * @param m The order, a positive number.
     * @param a The noncentrality parameter.
     * @param b0 The lower limit of the integral, a number which is not negative.
     * @param b1 The upper limit of the integral, a number which is not negative.
     * @param precision The precision of the result.
     *
     * @return <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i><sub>0</sub>) - <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i><sub>1</sub>)
     *
     * @throws ArithmeticException If <i>m</i> is not positive, if <i>b</i><sub>0</sub> or <i>b</i><sub>1</sub> is negative,
     *         if an argument of a series is too large, if a series needs more than a million terms or if more than 100000 digits cancel.
     */

    public static Apfloat marcumQ(Apfloat m, Apfloat a, Apfloat b0, Apfloat b1, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        checkArguments(m, b0);
        checkArguments(m, b1);
        int radix = m.radix();
        if (b0.equals(b1))
        {
            return ApfloatHelperShim.ZEROS[radix];
        }
        // Q_m(a, b) decreases strictly in b, so the difference is not zero
        long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION / 2;
        while (true)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            Apfloat q0 = marcumQ(m, a, b0, workingPrecision),
                    q1 = marcumQ(m, a, b1, workingPrecision),
                    difference = q0.subtract(q1);
            long cancelledDigits = (difference.signum() == 0 ? workingPrecision : Math.max(q0.scale(), q1.scale()) - difference.scale());
            if (cancelledDigits + ApfloatHelperShim.getSmallExtraPrecision(radix) <= extraPrecision)
            {
                return ApfloatMath.roundToPrecision(difference, precision, RoundingMode.HALF_EVEN);
            }
            extraPrecision = cancelledDigits + ApfloatHelperShim.EXTRA_PRECISION / 2;
            if (extraPrecision > MAX_CANCELLED_DIGITS)
            {
                throw new ArithmeticException("Marcum Q difference cancels to more than " + MAX_CANCELLED_DIGITS + " digits");
            }
        }
    }

    /**
     * An upper bound of log <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i>), from the Chernoff bound of the noncentral
     * chi-square distribution: <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i>) &le; <i>e</i><sup>-(1-u) y + &lambda;(1-u)/u</sup> / <i>u</i><sup>m</sup>
     * for 0 &lt; <i>u</i> &le; 1, with &lambda; = <i>a</i><sup>2</sup>/2 and <i>y</i> = <i>b</i><sup>2</sup>/2. It is minimal for
     * <i>y u</i><sup>2</sup> - <i>m u</i> - &lambda; = 0. Where the bound is below log(<code>Double.MIN_VALUE</code>/2),
     * the value rounds to the machine number zero, without summing the series.
     *
     * @param m The order, a positive number.
     * @param a The noncentrality parameter.
     * @param b The lower limit of the integral, a number which is not negative.
     *
     * @return An upper bound of log <i>Q</i><sub>m</sub>(<i>a</i>, <i>b</i>), at most 0.
     */

    public static double logUpperBound(double m, double a, double b)
    {
        double lambda = a * a / 2.0,
               y = b * b / 2.0,
               u = (m + Math.sqrt(m * m + 4.0 * y * lambda)) / (2.0 * y);
        if (!(u < 1.0))
        {
            return 0.0;
        }
        return Math.min(0.0, -(1.0 - u) * y + lambda * (1.0 - u) / u - m * Math.log(u));
    }

    private static void checkArguments(Apfloat m, Apfloat b)
        throws ArithmeticException
    {
        if (m.signum() <= 0)
        {
            throw new ArithmeticException("Marcum Q of a nonpositive order");
        }
        if (b.signum() < 0)
        {
            throw new ArithmeticException("Marcum Q of a negative argument");
        }
    }

    // sum_(k>=0) e^(-lambda) lambda^k / k! Q(m + k, y) with lambda = a^2/2, y = b^2/2, for a >= 0, b > 0
    private static Apfloat series(Apfloat m, Apfloat a, Apfloat b, long precision)
    {
        int radix = m.radix();
        double ad = a.doubleValue(),
               bd = b.doubleValue(),
               lambdaD = ad * ad / 2.0,
               yD = bd * bd / 2.0;
        if (!(lambdaD <= MAX_SERIES_ARGUMENT && yD <= MAX_SERIES_ARGUMENT))
        {
            throw new ArithmeticException("Marcum Q of a too large argument");
        }
        // The exponents of the terms are of the size lambda and y: the digits of that size are lost
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, ApfloatHelperShim.EXTRA_PRECISION + (long) Math.ceil(Math.log1p(lambdaD + yD + m.doubleValue()) / Math.log(radix)));
        FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(workingPrecision);
        Apint one = ApfloatHelperShim.ONES[radix],
              two = new Apint(2, radix);
        Apfloat y = helper.divide(helper.multiply(b, b), two);
        if (a.signum() == 0)
        {
            return regularizedGammaQ(helper, m, y);
        }
        Apfloat lambda = helper.divide(helper.multiply(a, a), two);

        // The terms below the mode of the Poisson distribution decrease at least like its weights: start where the
        // weights below sum to less than the precision of the weight at the mode
        double logEpsilon = -workingPrecision * Math.log(radix);
        long start = (long) Math.floor(lambdaD),
             terms = 0;
        double logRatio = 0.0;  // log of w_(start-1) / w_mode
        while (start > 0)
        {
            double ratio = start / lambdaD;
            logRatio += Math.log(ratio);
            if (logRatio - Math.log1p(-ratio) < logEpsilon)
            {
                break;
            }
            start--;
            checkTerms(++terms);
        }

        Apint startInt = new Apint(start, radix);
        Apfloat s = helper.add(m, startInt),
                q = regularizedGammaQ(helper, s, y),           // Q(m + k, y)
                gammaTerm = helper.exp(helper.subtract(helper.multiply(s, helper.log(y)),
                                                       helper.add(y, helper.logGamma(helper.add(s, one))))),  // y^(m+k) e^(-y) / (m+k)!
                weight = helper.exp(start == 0 ? lambda.negate() :
                                    helper.subtract(helper.multiply(startInt, helper.log(lambda)),
                                                    helper.add(lambda, helper.logGamma(helper.add(startInt, one))))),  // e^(-lambda) lambda^k / k!
                sum = ApfloatHelperShim.ZEROS[radix],
                epsilon = ApfloatMath.scale(new Apfloat(1, workingPrecision, radix), -workingPrecision);
        for (long k = start; ; k++)
        {
            sum = helper.add(sum, helper.multiply(weight, q));
            // Q(s + 1, y) = Q(s, y) + y^s e^(-y) / s!: the terms are added, nothing cancels
            q = helper.add(q, gammaTerm);
            gammaTerm = helper.divide(helper.multiply(gammaTerm, y), helper.add(m, new Apint(k + 1, radix)));
            weight = helper.divide(helper.multiply(weight, lambda), new Apint(k + 1, radix));
            // Q(m + j, y) <= 1 and the weights w_j for j > k decrease faster than geometrically with the ratio lambda / (k + 2)
            double ratio = lambdaD / (k + 2);
            if (ratio < 1.0 && helper.multiply(weight, new Apfloat(1.0 / (1.0 - ratio), workingPrecision, radix)).compareTo(helper.multiply(sum, epsilon)) < 0)
            {
                return sum;
            }
            checkTerms(++terms);
        }
    }

    // The regularized upper incomplete gamma function Q(s, y) = Gamma(s, y) / Gamma(s) for s > 0, y > 0
    private static Apfloat regularizedGammaQ(FixedPrecisionApfloatHelper helper, Apfloat s, Apfloat y)
    {
        return helper.divide(helper.gamma(s, y), helper.gamma(s));
    }

    private static void checkTerms(long terms)
        throws ArithmeticException
    {
        if (terms > MAX_TERMS)
        {
            throw new ArithmeticException("Marcum Q needs more than " + MAX_TERMS + " terms");
        }
        if ((terms & 0x3FF) == 0)
        {
            ApfloatHelperShim.checkInterrupted();
        }
    }
}
