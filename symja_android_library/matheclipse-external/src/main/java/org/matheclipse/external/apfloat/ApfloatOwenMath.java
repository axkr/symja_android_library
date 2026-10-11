package org.matheclipse.external.apfloat;

import java.math.RoundingMode;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * Owen's T function for {@link Apfloat} arguments.
 */

public class ApfloatOwenMath
{
    private ApfloatOwenMath()
    {
    }

    // The series is summed for h^2/2 up to this: its extra digits grow with h^2/2, to a result below e^(-h^2/2)
    private static final double MAX_SERIES_ARGUMENT = 1.0e5;

    /**
     * Owen's T function
     * <i>T</i>(<i>h</i>, <i>a</i>) = 1/(2&pi;) &int;<sub>0</sub><sup>a</sup> <i>e</i><sup>-h&sup2;(1+x&sup2;)/2</sup> / (1 + <i>x</i><sup>2</sup>) d<i>x</i>.<p>
     *
     * For |<i>a</i>| &le; 1 the series of Owen is summed, whose terms cancel to a result of the
     * size <i>e</i><sup>-h&sup2;/2</sup>. For |<i>a</i>| &gt; 1 the function is
     * (<i>Q</i>(<i>h</i>) + <i>Q</i>(<i>ah</i>))/2 - <i>Q</i>(<i>h</i>) <i>Q</i>(<i>ah</i>) - <i>T</i>(<i>ah</i>, 1/<i>a</i>)
     * with the upper tail <i>Q</i> of the normal distribution.
     *
     * @param h The first argument.
     * @param a The second argument.
     *
     * @return <i>T</i>(<i>h</i>, <i>a</i>)
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision and the result is not zero.
     * @throws ArithmeticException If <i>h</i><sup>2</sup>/2 is above 100000 where the series is needed: the result is below <i>e</i><sup>-100000</sup>.
     *
     * @see <a href="https://doi.org/10.1214/aoms/1177728074">D. B. Owen, Tables for computing bivariate normal probabilities, Ann. Math. Statist. 27 (1956)</a>
     * @see <a href="https://doi.org/10.18637/jss.v005.i05">M. Patefield, D. Tandy, Fast and accurate calculation of Owen's T function, J. Statist. Software 5 (2000)</a>
     */

    public static Apfloat owenT(Apfloat h, Apfloat a)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (a.signum() == 0)
        {
            return a;
        }
        long precision = Math.min(h.precision(), a.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Owen T to infinite precision", "owenT.infinitePrecision");
        }
        return owenT(h, a, precision);
    }

    /**
     * Owen's T function to the given precision.
     *
     * @param h The first argument.
     * @param a The second argument.
     * @param precision The precision of the result.
     *
     * @return <i>T</i>(<i>h</i>, <i>a</i>)
     *
     * @throws ArithmeticException If <i>h</i><sup>2</sup>/2 is above 100000 where the series is needed.
     */

    public static Apfloat owenT(Apfloat h, Apfloat a, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (a.signum() == 0)
        {
            return a;
        }
        if (a.signum() < 0)
        {
            return owenT(h, a.negate(), precision).negate();
        }
        h = ApfloatMath.abs(h);
        int radix = h.radix();
        Apint one = ApfloatHelperShim.ONES[radix],
              two = new Apint(2, radix);
        if (a.compareTo(one) <= 0)
        {
            return ApfloatMath.roundToPrecision(series(h, a, precision), precision, RoundingMode.HALF_EVEN);
        }
        // (Q(h) + Q(a h)) / 2 - Q(h) Q(a h): the tails are small, nothing cancels
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, ApfloatHelperShim.EXTRA_PRECISION / 2);
        FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat ah = helper.multiply(a, h),
                sqrt2 = helper.sqrt(two),
                tailH = helper.divide(helper.erfc(helper.divide(h, sqrt2)), two),
                tailAH = helper.divide(helper.erfc(helper.divide(ah, sqrt2)), two),
                value = helper.subtract(helper.divide(helper.add(tailH, tailAH), two), helper.multiply(tailH, tailAH));
        // T(a h, 1/a) is below e^(-(a h)^2/2) and the value is about e^(-h^2/2): it is dropped below the precision
        double hd = h.doubleValue(),
               ahd = ah.doubleValue();
        if ((ahd * ahd - hd * hd) / 2.0 < (workingPrecision + 2) * Math.log(radix))
        {
            value = helper.subtract(value, series(ah, helper.divide(one, a), workingPrecision));
        }
        return ApfloatMath.roundToPrecision(value, precision, RoundingMode.HALF_EVEN);
    }

    // (atan(a) - sum (-1)^j c_j a^(2j+1)) / (2 pi) with c_j = (1 - e^(-x) sum_(i<=j) x^i / i!) / (2j + 1), x = h^2/2, for 0 < a <= 1
    private static Apfloat series(Apfloat h, Apfloat a, long precision)
    {
        int radix = h.radix();
        double hd = h.doubleValue(),
               xd = hd * hd / 2.0;
        if (!(xd <= MAX_SERIES_ARGUMENT))
        {
            throw new ArithmeticException("Owen T of a too large first argument");
        }
        // The series cancels to a result of the size e^(-x): that many extra digits
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, ApfloatHelperShim.EXTRA_PRECISION / 2 + (long) (xd / Math.log(radix)));
        FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(workingPrecision);
        Apint one = ApfloatHelperShim.ONES[radix],
              two = new Apint(2, radix);
        Apfloat x = helper.divide(helper.multiply(h, h), two),
                expMinusX = helper.exp(x.negate()),
                a2 = helper.multiply(a, a),
                poissonTerm = expMinusX,      // e^(-x) x^j / j!
                poissonSum = expMinusX,       // e^(-x) sum_(i<=j) x^i / i!
                aPower = a,                   // a^(2j+1)
                sum = ApfloatHelperShim.ZEROS[radix],
                epsilon = ApfloatMath.scale(new Apfloat(1, workingPrecision, radix), -workingPrecision);
        for (long j = 0; ; j++)
        {
            Apfloat term = helper.multiply(helper.divide(helper.subtract(one, poissonSum), new Apint(2 * j + 1, radix)), aPower);
            sum = ((j & 1) == 0 ? helper.add(sum, term) : helper.subtract(sum, term));
            if (j > xd && ApfloatMath.abs(term).compareTo(epsilon) < 0)
            {
                break;
            }
            poissonTerm = helper.divide(helper.multiply(poissonTerm, x), new Apint(j + 1, radix));
            poissonSum = helper.add(poissonSum, poissonTerm);
            aPower = helper.multiply(aPower, a2);
        }
        return helper.divide(helper.subtract(helper.atan(a), sum), helper.multiply(two, helper.pi()));
    }
}
