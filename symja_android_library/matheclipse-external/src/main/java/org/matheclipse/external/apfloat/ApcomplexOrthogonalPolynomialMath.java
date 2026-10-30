package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.Aprational;

/**
 * Orthogonal polynomials of an integer degree with {@link Apcomplex} arguments, evaluated with the
 * three term recurrence relation in the degree.<p>
 *
 * The explicit sums and the hypergeometric series of these polynomials have alternating terms
 * which are much larger than the result for a large degree: <i>P<sub>1000</sub>(3/10)</i> is about
 * 0.03 while its terms reach 10<sup>450</sup>. The recurrence relation takes <i>n</i> steps
 * without such a cancellation and loses only a few digits, which the guard digits cover.
 */

public class ApcomplexOrthogonalPolynomialMath
{
    // digits more than asked for, in addition to the digits of the degree
    private static final long GUARD_DIGITS = 10;

    private ApcomplexOrthogonalPolynomialMath()
    {
    }

    /**
     * Gegenbauer polynomial <i>C<sub>n</sub><sup>(λ)</sup>(z)</i> of a non-negative integer degree.
     *
     * @param n The degree.
     * @param λ The parameter.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>C<sub>n</sub><sup>(λ)</sup>(z)</i>
     *
     * @throws ArithmeticException If <code>n</code> is negative.
     */

    public static Apcomplex gegenbauerC(long n, Apcomplex λ, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n < 0)
        {
            throw new ArithmeticException("Negative degree");
        }
        long work = workingPrecision(n, precision);
        Apcomplex l = λ.precision(work);
        Apcomplex x = z.precision(work);
        Apcomplex previous = one();
        if (n == 0)
        {
            return previous;
        }
        Apcomplex current = l.multiply(x).multiply(new Apint(2));
        for (long k = 2; k <= n; k++)
        {
            // k*C(k) == 2*(k+λ-1)*z*C(k-1) - (k+2*λ-2)*C(k-2)
            Apcomplex a = l.add(new Apint(k - 1)).multiply(x).multiply(new Apint(2));
            Apcomplex b = l.multiply(new Apint(2)).add(new Apint(k - 2));
            Apcomplex next = a.multiply(current).subtract(b.multiply(previous)).divide(new Apint(k));
            previous = current;
            // apfloat counts the digits cancelled in the subtraction as lost, though the
            // recurrence is stable: keep the working precision
            next = next.precision(work);
            current = next;
        }
        return current.precision(precision);
    }

    /**
     * Chebyshev polynomial of the first kind <i>T<sub>n</sub>(z)</i> of an integer degree,
     * <i>T<sub>-n</sub> == T<sub>n</sub></i>.
     *
     * @param n The degree.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>T<sub>n</sub>(z)</i>
     */

    public static Apcomplex chebyshevT(long n, Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        long degree = Math.abs(n);
        long work = workingPrecision(degree, precision);
        Apcomplex x = z.precision(work);
        Apcomplex previous = one();
        if (degree == 0)
        {
            return previous;
        }
        Apcomplex current = x;
        Apint two = new Apint(2);
        for (long k = 1; k < degree; k++)
        {
            // T(k+1) == 2*z*T(k) - T(k-1)
            Apcomplex next = two.multiply(x).multiply(current).subtract(previous).precision(work);
            previous = current;
            current = next;
        }
        return current.precision(precision);
    }

    /**
     * Jacobi polynomial <i>P<sub>n</sub><sup>(a,b)</sup>(z)</i> of a non-negative integer degree.
     *
     * @param n The degree.
     * @param a The first parameter.
     * @param b The second parameter.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>P<sub>n</sub><sup>(a,b)</sup>(z)</i>
     *
     * @throws ArithmeticException If <code>n</code> is negative or a coefficient of the recurrence relation is zero, as for <code>a+b</code> a negative integer.
     */

    public static Apcomplex jacobiP(long n, Apcomplex a, Apcomplex b, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n < 0)
        {
            throw new ArithmeticException("Negative degree");
        }
        long ℓ = negativeInteger(a);
        if (0 < ℓ && ℓ <= n)
        {
            // P(n,-l,b,z) == Binomial(n+b,l)/Binomial(n,l)*((z-1)/2)^l*P(n-l,l,b,z): the recurrence
            // relation meets a zero coefficient for a+b a negative integer
            long work = workingPrecision(n, precision);
            Apcomplex x = z.precision(work);
            Apcomplex factor = ApcomplexMath.pow(x.subtract(one()).divide(new Apint(2)), ℓ);
            for (long i = 0; i < ℓ; i++)
            {
                factor = factor.multiply(b.precision(work).add(new Apint(n - i))).divide(new Apint(n - i));
            }
            return factor.multiply(jacobiP(n - ℓ, new Apint(ℓ), b, x, work)).precision(precision);
        }
        if (0 < negativeInteger(b) && negativeInteger(b) <= n)
        {
            // P(n,a,b,z) == (-1)^n*P(n,b,a,-z)
            Apcomplex result = jacobiP(n, b, a, z.negate(), precision);
            return ((n & 1) == 1 ? result.negate() : result);
        }
        long work = workingPrecision(n, precision);
        Apcomplex x = z.precision(work);
        Apcomplex α = a.precision(work);
        Apcomplex β = b.precision(work);
        Apcomplex αβ = α.add(β);
        Apcomplex previous = one();
        if (n == 0)
        {
            return previous;
        }
        Apint two = new Apint(2);
        // P(1) == (a+1) + (a+b+2)*(z-1)/2
        Apcomplex current = α.add(one()).add(αβ.add(two).multiply(x.subtract(one())).divide(two));
        Apcomplex differenceOfSquares = α.multiply(α).subtract(β.multiply(β));
        for (long k = 2; k <= n; k++)
        {
            // 2k(k+a+b)(2k+a+b-2)*P(k) ==
            // (2k+a+b-1)*((2k+a+b)(2k+a+b-2)*z + a^2-b^2)*P(k-1) - 2(k+a-1)(k+b-1)(2k+a+b)*P(k-2)
            Apcomplex c = αβ.add(new Apint(2 * k));
            Apcomplex cMinus2 = c.subtract(two);
            Apcomplex denominator = new Apint(2 * k).multiply(αβ.add(new Apint(k))).multiply(cMinus2);
            if (denominator.real().signum() == 0 && denominator.imag().signum() == 0)
            {
                throw new ArithmeticException("Degenerate recurrence relation");
            }
            Apcomplex first = c.subtract(one()).multiply(c.multiply(cMinus2).multiply(x).add(differenceOfSquares));
            Apcomplex second = two.multiply(α.add(new Apint(k - 1))).multiply(β.add(new Apint(k - 1))).multiply(c);
            Apcomplex next = first.multiply(current).subtract(second.multiply(previous)).divide(denominator);
            previous = current;
            // apfloat counts the digits cancelled in the subtraction as lost, though the
            // recurrence is stable: keep the working precision
            next = next.precision(work);
            current = next;
        }
        return current.precision(precision);
    }

    /**
     * Legendre polynomial <i>P<sub>n</sub>(z)</i> of an integer degree. A negative degree is the one of
     * <i>P<sub>-n-1</sub>(z) == P<sub>n</sub>(z)</i>.
     *
     * @param n The degree.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>P<sub>n</sub>(z)</i>
     */

    public static Apcomplex legendreP(long n, Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        long degree = (n < 0 ? -n - 1 : n);
        long work = workingPrecision(degree, precision);
        Apcomplex x = z.precision(work);
        Apcomplex previous = one();
        if (degree == 0)
        {
            return previous;
        }
        return legendreRecurrence(degree, x, previous, x, work).precision(precision);
    }

    /**
     * The Legendre function of degree <code>n &gt;= 1</code> with the recurrence relation
     * <code>(k+1)*F(k+1) == (2k+1)*z*F(k) - k*F(k-1)</code> from <code>F(0)</code> and <code>F(1)</code>,
     * which <i>P<sub>n</sub></i> and <i>Q<sub>n</sub></i> share.
     */

    private static Apcomplex legendreRecurrence(long n, Apcomplex x, Apcomplex f0, Apcomplex f1, long work)
    {
        Apcomplex previous = f0;
        Apcomplex current = f1;
        for (long k = 1; k < n; k++)
        {
            Apcomplex next = new Apint(2 * k + 1).multiply(x).multiply(current).subtract(new Apint(k).multiply(previous)).divide(new Apint(k + 1));
            previous = current;
            // apfloat counts the digits cancelled in the subtraction as lost, though the
            // recurrence is stable: keep the working precision
            current = next.precision(work);
        }
        return current;
    }

    /**
     * Associated Legendre function <i>P<sub>n</sub><sup>m</sup>(z)</i> of integer degree and order,
     * <code>(-1)^m*w(z)*D(LegendreP(n,z),{z,m})</code> with the factor <code>w(z)</code> of the type:
     * <code>(1-z^2)^(m/2)</code> for type 1, <code>(1+z)^(m/2)*(1-z)^(m/2)</code> for type 2,
     * <code>(1-z)^m*(1+z)^(m/2)/(z-1)^(m/2)</code> for type 3. The <i>m</i>-th derivative of
     * <i>P<sub>n</sub></i> is <code>(2m-1)!!*GegenbauerC(n-m,m+1/2,z)</code>. A negative order
     * takes the factor <code>(-1)^m*(n-m)!/(n+m)!</code> of the positive one.
     *
     * @param n The degree.
     * @param m The order.
     * @param type The type 1, 2 or 3.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>P<sub>n</sub><sup>m</sup>(z)</i>
     *
     * @throws ArithmeticException If the type factor is not defined, as at <code>z == 1</code> for type 3.
     */

    public static Apcomplex legendreP(long n, long m, int type, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long degree = (n < 0 ? -n - 1 : n);
        long order = Math.abs(m);
        if (order > degree)
        {
            return Apcomplex.ZERO;
        }
        long work = workingPrecision(degree, precision);
        Apcomplex x = z.precision(work);
        Apcomplex derivative = gegenbauerC(degree - order, new Aprational(new Apint(2 * order + 1), new Apint(2)), x, work).multiply(doubleFactorial(2 * order - 1));
        Apcomplex result = typeFactor(type, order, x, work).multiply(derivative);
        if ((order & 1) == 1)
        {
            result = result.negate();
        }
        if (m < 0)
        {
            result = result.multiply(factorialRatio(degree - order, degree + order));
            if ((order & 1) == 1)
            {
                result = result.negate();
            }
        }
        return result.precision(precision);
    }

    /**
     * Legendre function of the second kind <i>Q<sub>n</sub>(z)</i> of a non-negative integer degree,
     * with the recurrence relation of <i>P<sub>n</sub></i> from <code>Q(0) == L/2</code> and
     * <code>Q(1) == z*Q(0)-1</code>, where <code>L</code> is <code>Log(1+z)-Log(1-z)</code> for type 2
     * and <code>Log(1+z)-Log(z-1)</code> for type 3.<p>
     *
     * The forward recurrence is stable for <i>z</i> on the cut <i>(-1, 1)</i> and next to it; far from
     * it <i>Q<sub>n</sub></i> decreases with the degree and the recurrence loses digits.
     *
     * @param n The degree.
     * @param type The type 2 or 3.
     * @param z The argument.
     * @param precision The precision of the result.
     *
     * @return <i>Q<sub>n</sub>(z)</i>
     *
     * @throws ArithmeticException If <code>n</code> is negative or <code>z</code> is 1 or -1.
     */

    public static Apcomplex legendreQ(long n, int type, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n < 0)
        {
            throw new ArithmeticException("Negative degree");
        }
        long work = workingPrecision(n, precision);
        Apcomplex x = z.precision(work);
        Apcomplex logTerm = ApcomplexMath.log(one().add(x)).subtract(ApcomplexMath.log(type == 3 ? x.subtract(one()) : one().subtract(x)));
        Apcomplex previous = logTerm.divide(new Apint(2));
        if (n == 0)
        {
            return previous.precision(precision);
        }
        return legendreRecurrence(n, x, previous, x.multiply(previous).subtract(one()), work).precision(precision);
    }

    /**
     * Spherical harmonic <i>Y<sub>l</sub><sup>m</sup>(θ, φ)</i> of integer degree and order,
     * <code>Sqrt((2l+1)/(4Pi)*(l-m)!/(l+m)!)*LegendreP(l,m,Cos(θ))*Exp(I*m*φ)</code>.
     * A negative degree is the one of <i>Y<sub>-l-1</sub><sup>m</sup> == Y<sub>l</sub><sup>m</sup></i>.
     *
     * @param l The degree.
     * @param m The order.
     * @param θ The polar angle.
     * @param φ The azimuthal angle.
     * @param precision The precision of the result.
     *
     * @return <i>Y<sub>l</sub><sup>m</sup>(θ, φ)</i>
     */

    public static Apcomplex sphericalHarmonicY(long l, long m, Apcomplex θ, Apcomplex φ, long precision)
        throws ApfloatRuntimeException
    {
        long degree = (l < 0 ? -l - 1 : l);
        if (Math.abs(m) > degree)
        {
            return Apcomplex.ZERO;
        }
        long work = workingPrecision(degree, precision);
        Apcomplex t = θ.precision(work);
        Apcomplex p = φ.precision(work);
        Apcomplex legendre = legendreP(degree, m, 1, ApcomplexMath.cos(t), work);
        Apfloat norm = factorialRatio(degree - m, degree + m).multiply(new Apint(2 * degree + 1)).precision(work).divide(ApfloatMath.pi(work).multiply(new Apint(4)));
        Apcomplex phase = ApcomplexMath.exp(Apcomplex.I.multiply(new Apint(m)).multiply(p));
        return ApfloatMath.sqrt(norm).multiply(legendre).multiply(phase).precision(precision);
    }

    /**
     * Zernike radial polynomial <i>R<sub>n</sub><sup>m</sup>(r)</i> for <code>0 &lt;= m &lt;= n</code>,
     * <code>(-1)^k*r^m*JacobiP(k,m,0,1-2r^2)</code> with <code>k == (n-m)/2</code>, and 0 for an odd
     * <code>n-m</code>.
     *
     * @param n The degree.
     * @param m The order.
     * @param r The argument.
     * @param precision The precision of the result.
     *
     * @return <i>R<sub>n</sub><sup>m</sup>(r)</i>
     *
     * @throws ArithmeticException If not <code>0 &lt;= m &lt;= n</code>.
     */

    public static Apcomplex zernikeR(long n, long m, Apcomplex r, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (m < 0 || m > n)
        {
            throw new ArithmeticException("Order not in [0, degree]");
        }
        if (((n - m) & 1) == 1)
        {
            return Apcomplex.ZERO;
        }
        long k = (n - m) / 2;
        long work = workingPrecision(n, precision);
        Apcomplex x = r.precision(work);
        Apcomplex argument = one().subtract(x.multiply(x).multiply(new Apint(2)));
        Apcomplex result = jacobiP(k, new Apint(m), Apcomplex.ZERO, argument, work).multiply(ApcomplexMath.pow(x, m));
        if ((k & 1) == 1)
        {
            result = result.negate();
        }
        return result.precision(precision);
    }

    // l for a parameter -l which is a negative integer, otherwise 0
    private static long negativeInteger(Apcomplex a)
    {
        if (a.imag().signum() != 0 || a.real().signum() >= 0 || a.real().frac().signum() != 0)
        {
            return 0;
        }
        return -a.real().longValue();
    }

    private static long workingPrecision(long degree, long precision)
    {
        return precision + GUARD_DIGITS + Long.toString(degree).length();
    }

    private static Apint one()
    {
        return new Apint(1);
    }

    /**
     * The factor <code>w(z)</code> of the associated Legendre function of the type.
     */

    private static Apcomplex typeFactor(int type, long m, Apcomplex z, long precision)
    {
        if (m == 0)
        {
            return one();
        }
        Apcomplex x = z.precision(precision);
        switch (type)
        {
            case 2:
                return ApcomplexMath.pow(ApcomplexMath.sqrt(one().add(x)).multiply(ApcomplexMath.sqrt(one().subtract(x))), m);
            case 3:
                return ApcomplexMath.pow(one().subtract(x), m).multiply(ApcomplexMath.pow(ApcomplexMath.sqrt(one().add(x)).divide(ApcomplexMath.sqrt(x.subtract(one()))), m));
            default:
                return ApcomplexMath.pow(ApcomplexMath.sqrt(one().subtract(x.multiply(x))), m);
        }
    }

    // (2m-1)!! == 1*3*5*...*(2m-1), 1 for m == 0
    private static Apint doubleFactorial(long n)
    {
        Apint result = one();
        for (long k = 3; k <= n; k += 2)
        {
            result = result.multiply(new Apint(k));
        }
        return result;
    }

    // a!/b! for 0 <= a, 0 <= b
    private static Aprational factorialRatio(long a, long b)
    {
        Apint numerator = one();
        Apint denominator = one();
        for (long k = b + 1; k <= a; k++)
        {
            numerator = numerator.multiply(new Apint(k));
        }
        for (long k = a + 1; k <= b; k++)
        {
            denominator = denominator.multiply(new Apint(k));
        }
        return new Aprational(numerator, denominator);
    }
}
