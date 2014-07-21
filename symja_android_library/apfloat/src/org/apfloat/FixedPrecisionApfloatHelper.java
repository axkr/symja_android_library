package org.apfloat;

import java.math.RoundingMode;

import org.apfloat.spi.Util;

/**
 * Fixed-precision mathematical functions for floating-point numbers.<p>
 *
 * All results of the mathematical operations are set to have the specified precision.
 * Also all input arguments are set to the specified precision before the operation.
 * If the specified precision is not infinite, this helper class also avoids 
 * <code>InfiniteExpansionException</code> e.g. in case where it would happen with
 * <code>ApfloatMath.acos(Apfloat.ZERO)</code>.
 *
 * @since 1.5
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class FixedPrecisionApfloatHelper
    extends FixedPrecisionApcomplexHelper
{
    /**
     * Constructs an apfloat fixed-precison helper with the specified precision.
     * The results of all mathematical operations are set to the specified precision.
     *
     * @param precision The precision of the results.
     *
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public FixedPrecisionApfloatHelper(long precision)
        throws IllegalArgumentException
    {
        super(precision);
    }

    /**
     * Returns the value with the specified precision.
     *
     * @param x The value.
     *
     * @return The value with to the specified precision.
     */

    public Apfloat valueOf(Apfloat x)
    {
        return super.valueOf(x);
    }

    /**
     * Negation.
     *
     * @param x The value to negate.
     *
     * @return <code>-x</code>.
     */

    public Apfloat negate(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(x).negate();
    }

    /**
     * Addition.
     *
     * @param x The first operand.
     * @param y The second operand.
     *
     * @return <code>x + y</code>.
     */

    public Apfloat add(Apfloat x, Apfloat y)
        throws ApfloatRuntimeException
    {
        return valueOf(setPrecision(x).add(valueOf(y)));
    }

    /**
     * Subtraction.
     *
     * @param x The first operand.
     * @param y The second operand.
     *
     * @return <code>x - y</code>.
     */

    public Apfloat subtract(Apfloat x, Apfloat y)
        throws ApfloatRuntimeException
    {
        return valueOf(setPrecision(x).subtract(setPrecision(y)));
    }

    /**
     * Multiplication.
     *
     * @param x The first operand.
     * @param y The second operand.
     *
     * @return <code>x * y</code>.
     */

    public Apfloat multiply(Apfloat x, Apfloat y)
        throws ApfloatRuntimeException
    {
        return valueOf(setPrecision(x).multiply(setPrecision(y)));
    }

    /**
     * Division.
     *
     * @param x The first operand.
     * @param y The second operand.
     *
     * @return <code>x / y</code>.
     *
     * @exception java.lang.ArithmeticException If <code>y</code> is zero.
     */

    public Apfloat divide(Apfloat x, Apfloat y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(setPrecision(x).divide(setPrecision(y)));
    }

    /**
     * Power.
     *
     * @param x The first operand.
     * @param y The second operand.
     *
     * @return <code>x<sup>y</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> and <code>y</code> are zero, or <code>x</code> is negative.
     */

    public Apfloat pow(Apfloat x, Apfloat y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apfloat result = ApfloatHelper.checkPow(x, y, precision());
        if (result != null)
        {
            return valueOf(result);
        }
        return exp(multiply(log(x), y));
    }

    /**
     * Integer power.
     *
     * @param x The first operand.
     * @param n The second operand.
     *
     * @return <code>x<sup>n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> and <code>n</code> are zero, or <code>x</code> is zero and <code>n</code> is negative.
     */

    public Apfloat pow(Apfloat x, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.pow(setPrecision(x), n));
    }

    /**
     * Absolute value.
     *
     * @param x The operand.
     *
     * @return The absolute value of <code>x</code>.
     */

    public Apfloat abs(Apfloat x)
        throws ApfloatRuntimeException
    {
        return ApfloatMath.abs(valueOf(x));
    }

    /**
     * Arc cosine.
     *
     * @param x The operand.
     *
     * @return The arc cosine of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If the absolute value of <code>x</code> is more than one.
     */

    public Apfloat acos(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            // Zero always has infinite precision so when zero input causes nonzero output special care must be taken
            return divide(pi(x.radix()), new Apfloat(2, precision(), x.radix()));
        }
        return valueOf(ApfloatMath.acos(setPrecision(x)));
    }

    /**
     * Hyperbolic arc cosine.
     *
     * @param x The operand.
     *
     * @return The hyperbolic arc cosine of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If the <code>x</code> is less than one.
     */

    public Apfloat acosh(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.acosh(setPrecision(x)));
    }

    /**
     * Arc sine.
     *
     * @param x The operand.
     *
     * @return The arc sine of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If the absolute value of <code>x</code> is more than one.
     */

    public Apfloat asin(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.asin(setPrecision(x)));
    }

    /**
     * Hyperbolic arc sine.
     *
     * @param x The operand.
     *
     * @return The hyperbolic arc sine of <code>x</code>.
     */

    public Apfloat asinh(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.asinh(setPrecision(x)));
    }

    /**
     * Arc tangent.
     *
     * @param x The operand.
     *
     * @return The arc tangent of <code>x</code>.
     */

    public Apfloat atan(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.atan(setPrecision(x)));
    }

    /**
     * Hyperbolic arc tangent.
     *
     * @param x The operand.
     *
     * @return The hyperbolic arc tangent of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If the absolute value of <code>x</code> is equal to or more than one.
     */

    public Apfloat atanh(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.atanh(setPrecision(x)));
    }

    /**
     * Cube root.
     *
     * @param x The operand.
     *
     * @return The cube root of <code>x</code>.
     */

    public Apfloat cbrt(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.cbrt(setPrecision(x)));
    }

    /**
     * Cosine.
     *
     * @param x The operand.
     *
     * @return The cosine of <code>x</code>.
     */

    public Apfloat cos(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.cos(setTrigonometricPrecision(x)));
    }

    /**
     * Hyperbolic cosine.
     *
     * @param x The operand.
     *
     * @return The hyperbolic cosine of <code>x</code>.
     */

    public Apfloat cosh(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.cosh(setExponentialPrecision(x)));
    }

    /**
     * Exponential function.
     *
     * @param x The operand.
     *
     * @return <code>e<sup>x</sup></code>.
     */

    public Apfloat exp(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.exp(setExponentialPrecision(x)));
    }

    /**
     * Natural logarithm.
     *
     * @param x The operand.
     *
     * @return The natural logarithm of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> is less than or equal to zero.
     */

    public Apfloat log(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        // If x is close to one then result will actually have limited accuracy
        // So, if the argument would have more precision, it could be used, however checking for
        // this as well as the computation itself could be very time-consuming so we don't do it
        return valueOf(ApfloatMath.log(setPrecision(x)));
    }

    /**
     * Logarithm in specified base.
     *
     * @param x The operand.
     * @param b The base.
     *
     * @return The base-<code>b</code> logarithm of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> or <code>b</code> is less than or equal to zero.
     *
     * @since 1.6
     */

    public Apfloat log(Apfloat x, Apfloat b)
        throws ArithmeticException, ApfloatRuntimeException
    {
        // If x or b is close to one then result will actually have limited accuracy
        // So, if the argument would have more precision, it could be used, however checking for
        // this as well as the computation itself could be very time-consuming so we don't do it
        return valueOf(ApfloatMath.log(setPrecision(x), setPrecision(b)));
    }

    /**
     * Sine.
     *
     * @param x The operand.
     *
     * @return The sine of <code>x</code>.
     */

    public Apfloat sin(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.sin(setTrigonometricPrecision(x)));
    }

    /**
     * Hyperbolic sine.
     *
     * @param x The operand.
     *
     * @return The hyperbolic sine of <code>x</code>.
     */

    public Apfloat sinh(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.sinh(setExponentialPrecision(x)));
    }

    /**
     * Square root.
     *
     * @param x The operand.
     *
     * @return The square root of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> is negative.
     */

    public Apfloat sqrt(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.sqrt(setPrecision(x)));
    }

    /**
     * Tangent.
     *
     * @param x The operand.
     *
     * @return The tangent of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> is &pi;/2 + n &pi; where n is an integer.
     */

    public Apfloat tan(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.tan(setTrigonometricPrecision(x)));
    }

    /**
     * Hyperbolic tangent.
     *
     * @param x The operand.
     *
     * @return The hyperbolic tangent of <code>x</code>.
     */

    public Apfloat tanh(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.tanh(setExponentialPrecision(x)));
    }

    /**
     * Arithmetic-geometric mean.
     *
     * @param a The first operand.
     * @param b The first operand.
     *
     * @return The arithmetic-geometric mean of <code>a</code> and <code>b</code>.
     */

    public Apfloat agm(Apfloat a, Apfloat b)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.agm(setPrecision(a), setPrecision(b)));
    }

    /**
     * Inverse root.
     *
     * @param x The operand.
     * @param n Which inverse root to take.
     *
     * @return <code>x<sup>-1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> or <code>n</code> is zero, or <code>x</code> is negative and <code>n</code> is even.
     */

    public Apfloat inverseRoot(Apfloat x, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.inverseRoot(setPrecision(x), n));
    }

    /**
     * Root.
     *
     * @param x The operand.
     * @param n Which root to take.
     *
     * @return <code>x<sup>1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>n</code> is zero, or <code>x</code> is negative and <code>n</code> is even.
     */

    public Apfloat root(Apfloat x, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.root(setPrecision(x), n));
    }

    /**
     * Move the radix point.
     *
     * @param x The operand.
     * @param scale The amount to move the radix point.
     *
     * @return <code>x * x.radix()<sup>scale</sup></code>.
     */

    public Apfloat scale(Apfloat x, long scale)
        throws ApfloatRuntimeException
    {
        return ApfloatMath.scale(valueOf(x), scale);
    }

    /**
     * Modulus.
     *
     * @param x The first operand.
     * @param y The second operand.
     *
     * @return <code>x % y</code>.
     */

    public Apfloat mod(Apfloat x, Apfloat y)
        throws ApfloatRuntimeException
    {
        return fmod(x, y);
    }

    /**
     * Ceiling function.
     *
     * @param x The operand.
     *
     * @return The nearest integer greater than or equal to <code>x</code>.
     */

    public Apfloat ceil(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.ceil(setPrecision(x)));
    }

    /**
     * Floor function.
     *
     * @param x The operand.
     *
     * @return The nearest integer less than or equal to <code>x</code>.
     */

    public Apfloat floor(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.floor(setPrecision(x)));
    }

    /**
     * Truncate fractional part.
     *
     * @param x The operand.
     *
     * @return The nearest integer rounded towards zero from <code>x</code>.
     */

    public Apfloat truncate(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.truncate(setPrecision(x)));
    }

    /**
     * Extract fractional part.
     *
     * @param x The operand.
     *
     * @return The fractional part of <code>x</code>.
     *
     * @since 1.7.0
     */

    public Apfloat frac(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.frac(x));
    }

    /**
     * Round with specified rounding mode.
     *
     * @param x The operand.
     * @param roundingMode The rounding mode.
     *
     * @return <code>x</code> rounded with the specified rounding mode.
     *
     * @since 1.7.0
     */

    public Apfloat round(Apfloat x, RoundingMode roundingMode)
        throws ApfloatRuntimeException
    {
        return ApfloatMath.round(x, precision(), roundingMode);
    }

    /**
     * Lambert W function.
     *
     * @param x The operand.
     *
     * @return <code>W<sub>0</sub>(x)</code>.
     *
     * @since 1.8.0
     */

    public Apfloat w(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.w(setPrecision(x)));
    }

    /**
     * Convert radians to degrees.
     *
     * @param x The angle in radians.
     *
     * @return <code>x</code> converted to degrees.
     *
     * @since 1.8.0
     */

    public Apfloat toDegrees(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.toDegrees(setPrecision(x)));
    }

    /**
     * Convert degrees to radians.
     *
     * @param x The angle in degrees.
     *
     * @return <code>x</code> converted to radians.
     *
     * @since 1.8.0
     */

    public Apfloat toRadians(Apfloat x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.toRadians(setPrecision(x)));
    }

    /**
     * Angle of point.
     *
     * @param x The operand.
     * @param y The operand.
     *
     * @return The angle of the point <code>(y, x)</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> and <code>y</code> are zero.
     */

    public Apfloat atan2(Apfloat x, Apfloat y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.atan2(setPrecision(x), setPrecision(y)));
    }

    /**
     * Copies the sign from one number to another.
     *
     * @param x The number to copy the sign to.
     * @param y The number to copy the sign from.
     *
     * @return <code>x</code> with the sign of <code>y</code>.
     */

    public Apfloat copySign(Apfloat x, Apfloat y)
        throws ApfloatRuntimeException
    {
        return ApfloatMath.copySign(valueOf(x), y);
    }

    /**
     * Modulus.
     *
     * @param x The first operand.
     * @param y The second operand.
     *
     * @return <code>x % y</code>.
     */

    public Apfloat fmod(Apfloat x, Apfloat y)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.fmod(x, setPrecision(y)));     // Since x might be much larger in scale we do not limit precision yet here
    }

    /**
     * Split to integer and fractional parts.
     *
     * @param x The operand.
     *
     * @return An array of two numbers <code>[i, f]</code> where <code>i</code> is <code>floor(x)</code> and <code>f</code> is <code>x - floor(x)</code>.
     */

    public Apfloat[] modf(Apfloat x)
        throws ApfloatRuntimeException
    {
        if (x.scale() > 0)
        {
            long precision = Util.ifFinite(precision(), precision() + x.scale());
            x = x.precision(precision);
        }
        else
        {
            x = setPrecision(x);
        }
        Apfloat[] modfs = ApfloatMath.modf(x);
        modfs[0] = valueOf(modfs[0]);
        modfs[1] = valueOf(modfs[1]);
        return modfs;
    }

    /**
     * Factorial.
     *
     * @param n The operand.
     *
     * @return <code>n!</code>.
     */

    public Apfloat factorial(long n)
        throws ApfloatRuntimeException
    {
        // For low precision and high n the result could be approximated faster with Stirling's formula
        return valueOf(ApfloatMath.factorial(n, precision()));
    }

    /**
     * Factorial.
     *
     * @param n The operand.
     * @param radix The radix of the result.
     *
     * @return <code>n!</code>.
     */

    public Apfloat factorial(long n, int radix)
        throws ApfloatRuntimeException
    {
        // For low precision and high n the result could be approximated faster with Stirling's formula
        return valueOf(ApfloatMath.factorial(n, precision(), radix));
    }

    /**
     * &pi;.
     *
     * @return <code>&pi;</code>.
     */

    public Apfloat pi()
        throws ApfloatRuntimeException
    {
        return super.pi();
    }

    /**
     * &pi;.
     *
     * @param radix The radix of the result.
     *
     * @return <code>&pi;</code>.
     *
     * @exception java.lang.NumberFormatException If the radix is invalid.
     */

    public Apfloat pi(int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return super.pi(radix);
    }

    /**
     * Logarithm.
     *
     * @param radix The radix of the result.
     *
     * @return <code>log(radix)</code>.
     *
     * @exception java.lang.NumberFormatException If the radix is invalid.
     */

    public Apfloat logRadix(int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return ApfloatMath.logRadix(precision(), radix);
    }

    /**
     * Fused multiply-add.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @param c The third operand.
     * @param d The fourth operand.
     *
     * @return <code>a * b + c * d</code>.
     */

    public Apfloat multiplyAdd(Apfloat a, Apfloat b, Apfloat c, Apfloat d)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.multiplyAdd(setPrecision(a), setPrecision(b), setPrecision(c), setPrecision(d)));
    }

    /**
     * Fused multiply-subtract.
     *
     * @param a The first operand.
     * @param b The second operand.
     * @param c The third operand.
     * @param d The fourth operand.
     *
     * @return <code>a * b - c * d</code>.
     */

    public Apfloat multiplySubtract(Apfloat a, Apfloat b, Apfloat c, Apfloat d)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.multiplySubtract(setPrecision(a), setPrecision(b), setPrecision(c), setPrecision(d)));
    }

    /**
     * Product.
     *
     * @param x The operand(s).
     *
     * @return The product of the operands.
     */

    public Apfloat product(Apfloat... x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.product(setPrecision(x)));
    }

    /**
     * Sum.
     *
     * @param x The operand(s).
     *
     * @return The sum of the operands.
     */

    public Apfloat sum(Apfloat... x)
        throws ApfloatRuntimeException
    {
        return valueOf(ApfloatMath.sum(setPrecision(x)));
    }

    private Apfloat setPrecision(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.precision(precision());
    }

    private Apfloat[] setPrecision(Apfloat[] x)
        throws ApfloatRuntimeException
    {
        Apfloat[] tmp = new Apfloat[x.length];
        for (int i = 0; i < x.length; i++)
        {
            tmp[i] = setPrecision(x[i]);
        }
        return tmp;
    }
}
