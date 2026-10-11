package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApcomplexHelper;

/**
 * Mathieu functions: the solutions of Mathieu's equation
 * <i>y</i>''(<i>z</i>) + (<i>a</i> - 2 <i>q</i> cos(2 <i>z</i>)) <i>y</i>(<i>z</i>) = 0,
 * its characteristic values and its characteristic exponent, for {@link Apcomplex}
 * arguments.<p>
 *
 * The even solution <i>C</i>(<i>a</i>, <i>q</i>, <i>z</i>) and the odd solution
 * <i>S</i>(<i>a</i>, <i>q</i>, <i>z</i>) are defined for any <i>a</i>, not only for a
 * characteristic value: they are the two halves of the Floquet solution
 * <i>f</i>(<i>z</i>) = &sum; <i>c</i><sub><i>k</i></sub> <i>e</i><sup><i>i</i>(&nu; + 2<i>k</i>)<i>z</i></sup>,
 * <i>C</i> = (<i>f</i>(<i>z</i>) + <i>f</i>(-<i>z</i>)) / 2 and
 * <i>S</i> = (<i>f</i>(<i>z</i>) - <i>f</i>(-<i>z</i>)) / (2 <i>i</i>), with
 * &sum; |<i>c</i><sub><i>k</i></sub>|<sup>2</sup> = 1 and <i>C</i>(<i>a</i>, <i>q</i>, 0) &gt; 0.
 * For <i>q</i> = 0 they are cos(&radic;<i>a</i> <i>z</i>) and sin(&radic;<i>a</i> <i>z</i>).<p>
 *
 * The methods throw an <code>ArithmeticException</code> where the result cannot be
 * calculated: a characteristic value of the order 0 of the odd solutions, or an argument
 * so far from the real axis that the series cancels more digits than can be added.
 *
 * @see <a href="https://dlmf.nist.gov/28.2">DLMF 28.2</a>
 * @see <a href="https://dlmf.nist.gov/28.4">DLMF 28.4</a>
 * @see <a href="https://dlmf.nist.gov/28.12">DLMF 28.12</a>
 */

public class ApcomplexMathieuMath
{
    private ApcomplexMathieuMath()
    {
    }

    /**
     * The even Mathieu function <i>C</i>(<i>a</i>, <i>q</i>, <i>z</i>).
     *
     * @param a The parameter <i>a</i>.
     * @param q The parameter <i>q</i>.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>C</i>(<i>a</i>, <i>q</i>, <i>z</i>)
     *
     * @throws ArithmeticException If the result cannot be calculated.
     */

    public static Apcomplex mathieuC(Apcomplex a, Apcomplex q, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return MathieuFunctionHelper.mathieu(MathieuFunctionHelper.Kind.C, a, q, z, precision);
    }

    /**
     * The derivative of the even Mathieu function <i>C</i>(<i>a</i>, <i>q</i>, <i>z</i>) with
     * respect to <i>z</i>.
     *
     * @param a The parameter <i>a</i>.
     * @param q The parameter <i>q</i>.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>C</i>'(<i>a</i>, <i>q</i>, <i>z</i>)
     *
     * @throws ArithmeticException If the result cannot be calculated.
     */

    public static Apcomplex mathieuCPrime(Apcomplex a, Apcomplex q, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return MathieuFunctionHelper.mathieu(MathieuFunctionHelper.Kind.C_PRIME, a, q, z, precision);
    }

    /**
     * The odd Mathieu function <i>S</i>(<i>a</i>, <i>q</i>, <i>z</i>).
     *
     * @param a The parameter <i>a</i>.
     * @param q The parameter <i>q</i>.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>S</i>(<i>a</i>, <i>q</i>, <i>z</i>)
     *
     * @throws ArithmeticException If the result cannot be calculated.
     */

    public static Apcomplex mathieuS(Apcomplex a, Apcomplex q, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return MathieuFunctionHelper.mathieu(MathieuFunctionHelper.Kind.S, a, q, z, precision);
    }

    /**
     * The derivative of the odd Mathieu function <i>S</i>(<i>a</i>, <i>q</i>, <i>z</i>) with
     * respect to <i>z</i>.
     *
     * @param a The parameter <i>a</i>.
     * @param q The parameter <i>q</i>.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>S</i>'(<i>a</i>, <i>q</i>, <i>z</i>)
     *
     * @throws ArithmeticException If the result cannot be calculated.
     */

    public static Apcomplex mathieuSPrime(Apcomplex a, Apcomplex q, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return MathieuFunctionHelper.mathieu(MathieuFunctionHelper.Kind.S_PRIME, a, q, z, precision);
    }

    /**
     * The characteristic value <i>a</i><sub><i>r</i></sub>(<i>q</i>), for which Mathieu's
     * equation has an even solution <i>e</i><sup><i>i r z</i></sup> <i>p</i>(<i>z</i>) with a
     * periodic <i>p</i>.
     *
     * @param r The order.
     * @param q The parameter <i>q</i>.
     * @param precision The precision of the result.
     *
     * @return <i>a</i><sub><i>r</i></sub>(<i>q</i>)
     *
     * @throws ArithmeticException If the result cannot be calculated.
     */

    public static Apcomplex mathieuCharacteristicA(Apcomplex r, Apcomplex q, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return check(MathieuCharacteristicHelper.characteristicA(r, q, new FixedPrecisionApcomplexHelper(precision)));
    }

    /**
     * The characteristic value <i>b</i><sub><i>r</i></sub>(<i>q</i>), for which Mathieu's
     * equation has an odd solution <i>e</i><sup><i>i r z</i></sup> <i>p</i>(<i>z</i>) with a
     * periodic <i>p</i>. For a non-integer order it is <i>a</i><sub><i>r</i></sub>(<i>q</i>).
     *
     * @param r The order.
     * @param q The parameter <i>q</i>.
     * @param precision The precision of the result.
     *
     * @return <i>b</i><sub><i>r</i></sub>(<i>q</i>)
     *
     * @throws ArithmeticException If the order is zero, or the result cannot be calculated.
     */

    public static Apcomplex mathieuCharacteristicB(Apcomplex r, Apcomplex q, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return check(MathieuCharacteristicHelper.characteristicB(r, q, new FixedPrecisionApcomplexHelper(precision)));
    }

    /**
     * The characteristic exponent &nu; of Mathieu's equation, for which it has the solution
     * <i>e</i><sup><i>i</i> &nu; <i>z</i></sup> <i>p</i>(<i>z</i>) with a periodic <i>p</i>.
     * For real parameters it is real in a stability band, and an integer with a positive
     * imaginary part added in a gap.
     *
     * @param a The parameter <i>a</i>.
     * @param q The parameter <i>q</i>.
     * @param precision The precision of the result.
     *
     * @return &nu;
     *
     * @throws ArithmeticException If the result cannot be calculated.
     */

    public static Apcomplex mathieuCharacteristicExponent(Apcomplex a, Apcomplex q, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return check(MathieuCharacteristicHelper.characteristicExponent(a, q, new FixedPrecisionApcomplexHelper(precision)));
    }

    private static Apcomplex check(Apcomplex value)
    {
        if (value == null)
        {
            throw new ArithmeticException("Mathieu characteristic value cannot be calculated");
        }
        return value;
    }
}
