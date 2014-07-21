package org.apfloat;

import org.apfloat.spi.Util;

/**
 * Fixed-precision mathematical functions for complex numbers.<p>
 *
 * All results of the mathematical operations are set to have the specified precision.
 * Also all input arguments are set to the specified precision before the operation.
 * If the specified precision is not infinite, this helper class also avoids 
 * <code>InfiniteExpansionException</code> e.g. in case where it would happen with
 * <code>ApcomplexMath.acos(Apcomplex.ZERO)</code>.
 *
 * @since 1.5
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class FixedPrecisionApcomplexHelper
{
    /**
     * Constructs an apcomplex fixed-precison helper with the specified precision.
     * The results of all mathematical operations are set to the specified precision.
     *
     * @param precision The precision of the results.
     *
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public FixedPrecisionApcomplexHelper(long precision)
        throws IllegalArgumentException
    {
        ApfloatHelper.checkPrecision(precision);
        this.precision = precision;
    }

    /**
     * Returns the value with the specified precision.
     *
     * @param z The value.
     *
     * @return The value with to the specified precision.
     */

    public Apcomplex valueOf(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return z.precision(precision());
    }

    /**
     * Negation.
     *
     * @param z The value to negate.
     *
     * @return <code>-z</code>.
     */

    public Apcomplex negate(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(z).negate();
    }

    /**
     * Complex conjugate.
     *
     * @param z The operand.
     *
     * @return <code>x - i y</code> where <code>z</code> is <code>x + i y</code>.
     */

    public Apcomplex conj(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(z).conj();
    }

    /**
     * Addition.
     *
     * @param z The first operand.
     * @param w The second operand.
     *
     * @return <code>z + w</code>.
     */

    public Apcomplex add(Apcomplex z, Apcomplex w)
        throws ApfloatRuntimeException
    {
        return valueOf(setPrecision(z).add(setPrecision(w)));
    }

    /**
     * Subtraction.
     *
     * @param z The first operand.
     * @param w The second operand.
     *
     * @return <code>z - w</code>.
     */

    public Apcomplex subtract(Apcomplex z, Apcomplex w)
        throws ApfloatRuntimeException
    {
        return valueOf(setPrecision(z).subtract(setPrecision(w)));
    }

    /**
     * Multiplication.
     *
     * @param z The first operand.
     * @param w The second operand.
     *
     * @return <code>z * w</code>.
     */

    public Apcomplex multiply(Apcomplex z, Apcomplex w)
        throws ApfloatRuntimeException
    {
        return valueOf(setPrecision(z).multiply(setPrecision(w)));
    }

    /**
     * Division.
     *
     * @param z The first operand.
     * @param w The second operand.
     *
     * @return <code>z / w</code>.
     *
     * @exception java.lang.ArithmeticException If <code>w</code> is zero.
     */

    public Apcomplex divide(Apcomplex z, Apcomplex w)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(setPrecision(z).divide(setPrecision(w)));
    }

    /**
     * Power.
     *
     * @param z The first operand.
     * @param w The second operand.
     *
     * @return <code>z<sup>w</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> and <code>w</code> are zero.
     */

    public Apcomplex pow(Apcomplex z, Apcomplex w)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apcomplex result = ApfloatHelper.checkPow(z, w, precision());
        if (result != null)
        {
            return valueOf(result);
        }
        return exp(multiply(log(z), w));
    }

    /**
     * Integer power.
     *
     * @param z The first operand.
     * @param n The first operand.
     *
     * @return <code>z<sup>n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> and <code>n</code> are zero, or <code>z</code> is zero and <code>n</code> is negative.
     */

    public Apcomplex pow(Apcomplex z, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.pow(setPrecision(z), n));
    }

    /**
     * Complex angle.
     *
     * @param z The operand.
     *
     * @return The angle of <code>z</code> on the complex plane.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is zero.
     */

    public Apfloat arg(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.arg(setPrecision(z)));
    }

    /**
     * Imaginary part.
     *
     * @param z The operand.
     *
     * @return The imaginary part of <code>z</code>.
     */

    public Apfloat imag(Apcomplex z)
    {
        return valueOf(z.imag());
    }

    /**
     * Real part.
     *
     * @param z The operand.
     *
     * @return The real part of <code>z</code>.
     */

    public Apfloat real(Apcomplex z)
    {
        return valueOf(z.real());
    }

    /**
     * Absolute value.
     *
     * @param z The operand.
     *
     * @return The absolute value of <code>z</code>.
     */

    public Apfloat abs(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.abs(setPrecision(z)));
    }

    /**
     * Norm.
     *
     * @param z The operand.
     *
     * @return <code>x<sup>2</sup> + y<sup>2</sup></code> where <code>z</code> is <code>x + i y</code>.
     */

    public Apfloat norm(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.norm(setPrecision(z)));
    }

    /**
     * Arc cosine.
     *
     * @param z The operand.
     *
     * @return The arc cosine of <code>z</code>.
     */

    public Apcomplex acos(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z.real().signum() == 0 && z.imag().signum() == 0)
        {
            // Zero always has infinite precision so when zero input causes nonzero output special care must be taken
            return divide(pi(z.radix()), new Apfloat(2, precision(), z.radix()));
        }
        return valueOf(ApcomplexMath.acos(setPrecision(z)));
    }

    /**
     * Hyperbolic arc cosine.
     *
     * @param z The operand.
     *
     * @return The hyperbolic arc cosine of <code>z</code>.
     */

    public Apcomplex acosh(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z.real().signum() == 0 && z.imag().signum() == 0)
        {
            // Zero always has infinite precision so when zero input causes nonzero output special care must be taken
            return valueOf(new Apcomplex(Apfloat.ZERO, pi(z.radix()).divide(new Apfloat(2, precision(), z.radix()))));
        }
        return valueOf(ApcomplexMath.acosh(setPrecision(z)));
    }

    /**
     * Arc sine.
     *
     * @param z The operand.
     *
     * @return The arc sine of <code>z</code>.
     */

    public Apcomplex asin(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.asin(setPrecision(z)));
    }

    /**
     * Hyperbolic arc sine.
     *
     * @param z The operand.
     *
     * @return The hyperbolic arc sine of <code>z</code>.
     */

    public Apcomplex asinh(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.asinh(setPrecision(z)));
    }

    /**
     * Arc tangent.
     *
     * @param z The operand.
     *
     * @return The arc tangent of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is <code>i</code>.
     */

    public Apcomplex atan(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.atan(setPrecision(z)));
    }

    /**
     * Hyperbolic arc tangent.
     *
     * @param z The operand.
     *
     * @return The hyperbolic arc tangent of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is 1 or -1.
     */

    public Apcomplex atanh(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.atanh(setPrecision(z)));
    }

    /**
     * Cube root.
     *
     * @param z The operand.
     *
     * @return The cube root of <code>z</code>.
     */

    public Apcomplex cbrt(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.cbrt(setPrecision(z)));
    }

    /**
     * Cosine.
     *
     * @param z The operand.
     *
     * @return The cosine of <code>z</code>.
     */

    public Apcomplex cos(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.cos(setTrigExpPrecision(z)));
    }

    /**
     * Hyperbolic cosine.
     *
     * @param z The operand.
     *
     * @return The hyperbolic cosine of <code>z</code>.
     */

    public Apcomplex cosh(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.cosh(setExpTrigPrecision(z)));
    }

    /**
     * Exponential function.
     *
     * @param z The operand.
     *
     * @return <code>e<sup>z</sup></code>.
     */

    public Apcomplex exp(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.exp(setExpTrigPrecision(z)));
    }

    /**
     * Natural logarithm.
     *
     * @param z The operand.
     *
     * @return The natural logarithm of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is zero.
     */

    public Apcomplex log(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.log(setPrecision(z)));
    }

    /**
     * Logarithm in specified base.
     *
     * @param z The operand.
     * @param w The base.
     *
     * @return The base-<code>w</code> logarithm of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> or <code>w</code> is zero.
     *
     * @since 1.6
     */

    public Apcomplex log(Apcomplex z, Apcomplex w)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.log(setPrecision(z), setPrecision(w)));
    }

    /**
     * Sine.
     *
     * @param z The operand.
     *
     * @return The sine of <code>z</code>.
     */

    public Apcomplex sin(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.sin(setTrigExpPrecision(z)));
    }

    /**
     * Hyperbolic sine.
     *
     * @param z The operand.
     *
     * @return The hyperbolic sine of <code>z</code>.
     */

    public Apcomplex sinh(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.sinh(setExpTrigPrecision(z)));
    }

    /**
     * Square root.
     *
     * @param z The operand.
     *
     * @return The square root of <code>z</code>.
     */

    public Apcomplex sqrt(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.sqrt(setPrecision(z)));
    }

    /**
     * Tangent.
     *
     * @param z The operand.
     *
     * @return The tangent of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is &pi;/2 + n &pi; where n is an integer.
     */

    public Apcomplex tan(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.tan(setTrigExpPrecision(z)));
    }

    /**
     * Hyperbolic tangent.
     *
     * @param z The operand.
     *
     * @return The hyperbolic tangent of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is i (&pi;/2 + n &pi;) where n is an integer.
     */

    public Apcomplex tanh(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.tanh(setExpTrigPrecision(z)));
    }

    /**
     * Arithmetic-geometric mean.
     *
     * @param a The first operand.
     * @param b The first operand.
     *
     * @return The arithmetic-geometric mean of <code>a</code> and <code>b</code>.
     */

    public Apcomplex agm(Apcomplex a, Apcomplex b)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.agm(setPrecision(a), setPrecision(b)));
    }

    /**
     * Inverse root.
     *
     * @param z The operand.
     * @param n Which inverse root to take.
     *
     * @return <code>z<sup>-1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> or <code>n</code> is zero.
     */

    public Apcomplex inverseRoot(Apcomplex z, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.inverseRoot(setPrecision(z), n));
    }

    /**
     * Inverse root with branch.
     *
     * @param z The operand.
     * @param n Which inverse root to take.
     * @param k Which branch to take.
     *
     * @return <code>z<sup>-1/n</sup>e<sup>-i2&pi;k/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> or <code>n</code> is zero.
     */

    public Apcomplex inverseRoot(Apcomplex z, long n, long k)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.inverseRoot(setPrecision(z), n, k));
    }

    /**
     * Root.
     *
     * @param z The operand.
     * @param n Which root to take.
     *
     * @return <code>z<sup>1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>n</code> is zero, or <code>z</code> is zero and <code>n</code> is negative.
     */

    public Apcomplex root(Apcomplex z, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.root(setPrecision(z), n));
    }

    /**
     * Root with branch.
     *
     * @param z The operand.
     * @param n Which root to take.
     * @param k Which branch to take.
     *
     * @return <code>z<sup>1/n</sup>e<sup>i2&pi;sk/n</sup></code> where <code>s</code> is the signum of the imaginary part of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>n</code> is zero, or <code>z</code> is zero and <code>n</code> is negative.
     */

    public Apcomplex root(Apcomplex z, long n, long k)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.root(setPrecision(z), n, k));
    }

    /**
     * All branches of a root.
     *
     * @param z The operand.
     * @param n Which root to take.
     *
     * @return <code>z<sup>1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>n</code> is zero, or <code>z</code> is zero and <code>n</code> is negative.
     */

    public Apcomplex[] allRoots(Apcomplex z, int n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apcomplex[] allRoots = ApcomplexMath.allRoots(setPrecision(z), n);
        for (int i = 0; i < allRoots.length; i++)
        {
            allRoots[i] = valueOf(allRoots[i]);
        }
        return allRoots;
    }

    /**
     * Move the radix point.
     *
     * @param z The operand.
     * @param scale The amount to move the radix point.
     *
     * @return <code>z * z.radix()<sup>scale</sup></code>.
     */

    public Apcomplex scale(Apcomplex z, long scale)
        throws ApfloatRuntimeException
    {
        return ApcomplexMath.scale(valueOf(z), scale);
    }

    /**
     * Lambert W function.
     *
     * @param z The operand.
     *
     * @return <code>W<sub>0</sub>(z)</code>.
     *
     * @since 1.8.0
     */

    public Apcomplex w(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.w(setPrecision(z)));
    }

    /**
     * Lambert W function for the specified branch.
     *
     * @param z The operand.
     * @param k The branch.
     *
     * @return <code>W<sub>k</sub>(z)</code>.
     *
     * @since 1.8.0
     */

    public Apcomplex w(Apcomplex z, long k)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.w(setPrecision(z), k));
    }

    /**
     * Product.
     *
     * @param z The operand(s).
     *
     * @return The product of the operands.
     */

    public Apcomplex product(Apcomplex... z)
        throws ApfloatRuntimeException
    {
        return valueOf(ApcomplexMath.product(setPrecision(z)));
    }

    /**
     * Sum.
     *
     * @param z The operand(s).
     *
     * @return The sum of the operands.
     */

    public Apcomplex sum(Apcomplex... z)
        throws ApfloatRuntimeException
    {
        // This is not entirely optimal as the real and imaginary parts might have different scales and one of them could have thus reduced precision
        return valueOf(ApcomplexMath.sum(setPrecision(z)));
    }

    /**
     * Returns the precision, which is used for the results.
     *
     * @return The precision of the results.
     */

    public long precision()
    {
        return this.precision;
    }

    Apfloat valueOf(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.precision(precision());
    }

    Apfloat pi()
        throws ApfloatRuntimeException
    {
        return ApfloatMath.pi(precision());
    }

    Apfloat pi(int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return ApfloatMath.pi(precision(), radix);
    }

    Apfloat setTrigonometricPrecision(Apfloat x)
        throws ApfloatRuntimeException
    {
        long precision = ApfloatHelper.extendPrecision(precision(), Math.max(0, x.scale()));
        return x.precision(precision);
    }

    Apfloat setExponentialPrecision(Apfloat x)
        throws ApfloatRuntimeException
    {
        if (x.scale() <= -precision())
        {
            // Result will be rounded to one so avoid heavy high-precision calculation
            x = new Apfloat(0, Apfloat.DEFAULT, x.radix());
        }
        else if (x.scale() < 0)
        {
            // Taylor series would increase precision, thus reduce it
            long precision = Util.ifFinite(precision(), precision() + x.scale());
            x = x.precision(precision);
        }
        else
        {
            x = x.precision(precision());
        }
        return x;
    }

    private Apcomplex setPrecision(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return z.precision(precision());
    }

    private Apcomplex[] setPrecision(Apcomplex[] z)
        throws ApfloatRuntimeException
    {
        Apcomplex[] tmp = new Apcomplex[z.length];
        for (int i = 0; i < z.length; i++)
        {
            tmp[i] = setPrecision(z[i]);
        }
        return tmp;
    }

    private Apcomplex setExpTrigPrecision(Apcomplex z)
    {
        return new Apcomplex(setExponentialPrecision(z.real()), setTrigonometricPrecision(z.imag()));
    }

    private Apcomplex setTrigExpPrecision(Apcomplex z)
    {
        return new Apcomplex(setTrigonometricPrecision(z.real()), setExponentialPrecision(z.imag()));
    }

    private long precision;
}
