package org.apfloat;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.PushbackReader;
import java.io.Writer;
import java.io.IOException;
import java.util.Formatter;
import static java.util.FormattableFlags.*;

import org.apfloat.spi.ApfloatImpl;

/**
 * Arbitrary precision floating-point number class.<p>
 *
 * Apfloat numbers are immutable.<p>
 *
 * A pitfall exists with the constructors {@link #Apfloat(float,long)}
 * and {@link #Apfloat(double,long)}. Since <code>float</code>s and
 * <code>double</code>s are always represented internally in radix 2, the
 * conversion to any other radix usually causes round-off errors, and the
 * resulting apfloat won't be accurate to the desired number of digits.<p>
 *
 * For example, <code>0.3</code> can't be presented exactly in base 2. When
 * you construct an apfloat like <code>new Apfloat(0.3f, 1000)</code>, the
 * resulting number won't be accurate to 1000 digits, but only to roughly 7
 * digits (in radix 10). In fact, the resulting number will be something like
 * <code>0.30000001192092896</code>...
 *
 * @see ApfloatMath
 *
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class Apfloat
    extends Apcomplex
    implements Comparable<Apfloat>
{
    /**
     * Default constructor. To be used only by subclasses that
     * overload all needed methods.
     */

    protected Apfloat()
    {
    }

    /**
     * Constructs an apfloat that is backed by the specified
     * <code>ApfloatImpl</code> object.
     *
     * @param impl The <code>ApfloatImpl</code> object backing this apfloat.
     */

    protected Apfloat(ApfloatImpl impl)
    {
        assert (impl.precision() > 0);
        this.impl = impl;
    }

    /**
     * Constructs an apfloat from the specified string.
     * The default radix will be used.<p>
     *
     * The precision will be calculated from the number
     * of digits specified in the string. For example:<p>
     *
     * <code>"0.1"</code> will have a precision of 1 digit.<br>
     * <code>"1.0"</code> will have a precision of 2 digits.<br>
     * <code>"100"</code> will have a precision of 3 digits.<br>
     *
     * @param value The string representing the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     */

    public Apfloat(String value)
        throws NumberFormatException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, false));
    }

    /**
     * Constructs an apfloat from the specified string and precision.
     * The default radix will be used.
     *
     * @param value The string representing the number.
     * @param precision The precision of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(String value, long precision)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision, false));
    }

    /**
     * Constructs an apfloat from the specified string, precision and radix.<p>
     *
     * Note that it's impossible to construct apfloats with a specified exponent
     * and with radix >= 14, since the characters 'e' and 'E' will be treated as
     * digits of the mantissa.<p>
     *
     * For example, in radix 10, "1e5" means the decimal number 100000. But in
     * radix 16, "1e5" means the decimal number 485.
     *
     * @param value The string representing the number.
     * @param precision The precision of the number.
     * @param radix The radix of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(String value, long precision, int radix)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision, radix, false));
    }

    /**
     * Constructs an apfloat from the specified <code>long</code>.
     * The default radix will be used. The precision of the number
     * will be {@link #INFINITE}.
     *
     * @param value The value of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     */

    public Apfloat(long value)
        throws NumberFormatException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value));
    }

    /**
     * Constructs an apfloat from the specified <code>long</code>
     * and precision. The default radix will be used.
     *
     * @param value The value of the number.
     * @param precision The precision of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(long value, long precision)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    /**
     * Constructs an apfloat from the specified <code>long</code>,
     * precision and radix.
     *
     * @param value The value of the number.
     * @param precision The precision of the number.
     * @param radix The radix of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(long value, long precision, int radix)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision, radix));
    }

    /**
     * Constructs an apfloat from the specified <code>float</code>.
     * The default radix will be used. The precision of the number
     * will be the precision of a <code>float</code> in the default
     * radix, for example in radix 10 the precision is 7 digits.
     *
     * @param value The value of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     */

    public Apfloat(float value)
        throws NumberFormatException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value));
    }

    /**
     * Constructs an apfloat from the specified <code>float</code>
     * and precision. The default radix will be used.
     *
     * @param value The value of the number.
     * @param precision The precision of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(float value, long precision)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    /**
     * Constructs an apfloat from the specified <code>float</code>,
     * precision and radix.
     *
     * @param value The value of the number.
     * @param precision The precision of the number.
     * @param radix The radix of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(float value, long precision, int radix)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision, radix));
    }

    /**
     * Constructs an apfloat from the specified <code>double</code>.
     * The default radix will be used. The precision of the number
     * will be the precision of a <code>double</code> in the default
     * radix, for example in radix 10 the precision is 16 digits.
     *
     * @param value The value of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     */

    public Apfloat(double value)
        throws NumberFormatException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value));
    }

    /**
     * Constructs an apfloat from the specified <code>double</code>
     * and precision. The default radix will be used.
     *
     * @param value The value of the number.
     * @param precision The precision of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(double value, long precision)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    /**
     * Constructs an apfloat from the specified <code>double</code>,
     * precision and radix.
     *
     * @param value The value of the number.
     * @param precision The precision of the number.
     * @param radix The radix of the number.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(double value, long precision, int radix)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision, radix));
    }

    /**
     * Reads an apfloat from a stream using default precision and radix.
     * The stream needs to be a <code>PushbackReader</code>,
     * as the first invalid character is pushed back to the stream.<p>
     *
     * Note that since only a pushback buffer of one character is used,
     * the number read may still not be valid. For example, if the stream
     * contains <code>"-#"</code> or <code>"1.5e#"</code> (here <code>'#'</code>
     * is the first invalid character), the number is actually not valid, and
     * only the character <code>'#'</code> would be put back to the stream.<p>
     *
     * The precision is determined similarly as in the {@link #Apfloat(String)}
     * constructor that is as the number of digits read from the stream.
     *
     * @param in The stream to read from
     *
     * @exception java.io.IOException If an I/O error occurs accessing the stream.
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public Apfloat(PushbackReader in)
        throws IOException, NumberFormatException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(in, false));
    }

    /**
     * Reads an apfloat from a stream using the specified precision.
     * The default radix is used.
     *
     * @param in The stream to read from
     * @param precision The precision of the number.
     *
     * @exception java.io.IOException If an I/O error occurs accessing the stream.
     * @exception java.lang.NumberFormatException If the number is not valid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     *
     * @see #Apfloat(PushbackReader)
     */

    public Apfloat(PushbackReader in, long precision)
        throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(in, precision, false));
    }

    /**
     * Reads an apfloat from a stream using the specified precision
     * and radix.
     *
     * @param in The stream to read from
     * @param precision The precision of the number.
     * @param radix The radix of the number.
     *
     * @exception java.io.IOException If an I/O error occurs accessing the stream.
     * @exception java.lang.NumberFormatException If the number is not valid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     *
     * @see #Apfloat(PushbackReader)
     */

    public Apfloat(PushbackReader in, long precision, int radix)
        throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(in, precision, radix, false));
    }

    /**
     * Constructs an apfloat from a <code>BigInteger</code>.
     * Precision will be {@link #INFINITE} and the default radix
     * is used.
     *
     * @param value The value of the number.
     *
     * @exception java.lang.NumberFormatException If the default radix is not valid.
     */

    public Apfloat(BigInteger value)
        throws NumberFormatException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value));
    }

    /**
     * Constructs an apfloat from a <code>BigInteger</code> with
     * the specified precision. The default radix is used.
     *
     * @param value The value of the number.
     * @param precision The precision of the number.
     *
     * @exception java.lang.NumberFormatException If the default radix is not valid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(BigInteger value, long precision)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    /**
     * Constructs an apfloat from a <code>BigInteger</code> with
     * the specified precision and radix.
     *
     * @param value The value of the number.
     * @param precision The precision of the number.
     * @param radix The radix of the number.
     *
     * @exception java.lang.NumberFormatException If the radix is not valid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(BigInteger value, long precision, int radix)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision, radix));
    }

    /**
     * Creates an apfloat from a <code>BigDecimal</code>. An apfloat created this
     * way will always have radix 10 regardless of the current default radix.
     *
     * @param value The value to use.
     */

    public Apfloat(BigDecimal value)
        throws ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value));
    }

    /**
     * Creates an apfloat from a <code>BigDecimal</code>. An apfloat created this
     * way will always have radix 10 regardless of the current default radix.
     *
     * @param value The value to use.
     * @param precision The precision to use, in decimal digits.
     *
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public Apfloat(BigDecimal value, long precision)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    /**
     * Radix of this apfloat.
     *
     * @return Radix of this apfloat.
     */

    public int radix()
    {
        return this.impl.radix();
    }

    /**
     * Real part of this apfloat.
     *
     * @return <code>this</code>
     */

    public Apfloat real()
    {
        return this;
    }

    /**
     * Imaginary part of this apfloat.
     *
     * @return {@link #ZERO}
     */

    public Apfloat imag()
    {
        return Apfloat.ZERO;
    }

    /**
     * Returns the precision of this apfloat.
     *
     * @return The precision of this apfloat in number of digits of the radix in which it's presented.
     */

    public long precision()
        throws ApfloatRuntimeException
    {
        return this.impl.precision();
    }

    /**
     * Returns an apfloat with the same value as this apfloat accurate to the
     * specified precision.<p>
     *
     * If the requested precision less than this number's current precision, the
     * functionality is quite obvious: the precision is simply truncated, and e.g.
     * comparison and equality checking will work as expected. Some rounding errors
     * in e.g. addition and subtraction may still occur, as "invisible" trailing
     * digits can remain in the number.<p>
     *
     * If the requested precision more than this number's current precision, the
     * functionality is quite undefined: the digits up to this number's current
     * precision are guaranteed to be the same, but the "new" digits are undefined:
     * they may be zero, or they may be digits that have been previously discarded
     * with a call to precision() with a smaller number of digits, or they may be
     * something else, or any combination of these.<p>
     *
     * These limitations allow various performance optimizations to be made.
     *
     * @param precision Precision of the new apfloat.
     *
     * @return An apfloat with the specified precision and same value as this apfloat.
     *
     * @exception java.lang.IllegalArgumentException If <code>precision</code> is <= 0.
     */

    public Apfloat precision(long precision)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        ApfloatHelper.checkPrecision(precision);

        return new Apfloat(getImpl(precision));
    }

    /**
     * Returns the scale of this apfloat. The scale is defined here as<p>
     *
     * <code>apfloat = signum * mantissa * radix<sup>scale</sup></code><p>
     *
     * where 1/radix <= mantissa < 1. In other words,
     * <code>scale&nbsp;=&nbsp;floor(log<sub>radix</sub>(apfloat))&nbsp;+&nbsp;1</code>.<p>
     *
     * For example, 1 has a scale of 1, and 100 has a scale of 3 (in radix 10).
     * For integers, scale is equal to the number of digits in the apfloat.<p>
     *
     * Zero has a scale of <code>-INFINITE</code>.<p>
     *
     * Note that this definition of <code>scale</code> is different than in <code>java.math.BigDecimal</code>.
     *
     * @return The exponent of this apfloat in number of digits of the radix in which it's presented.
     */

    public long scale()
        throws ApfloatRuntimeException
    {
        if (signum() == 0)
        {
            return -INFINITE;
        }
        else
        {
            return this.impl.scale();
        }
    }

    /**
     * Returns the size of this apfloat. The size is defined here as<p>
     *
     * <code>apfloat = signum * mantissa * radix<sup>scale</sup></code> and<p>
     * <code>mantissa = n / radix<sup>size</sup></code><p>
     *
     * where 1/radix <= mantissa < 1 and n is the smallest possible integer.
     * In other words, the size is the number of significant digits in the
     * mantissa (excluding leading and trailing zeros but including all zeros
     * between the first and last nonzero digit).
     *
     * For example, 1 has a size of 1, and 100 has also a size of 1 (in radix 10).
     * 11 has a size of 2, and 10001000 has a size of 5.<p>
     *
     * Zero has a size of <code>0</code>.
     *
     * @return The number of digits in this number, from the most significant digit to the least significant nonzero digit, in the radix in which it's presented.
     *
     * @since 1.6
     */

    public long size()
        throws ApfloatRuntimeException
    {
        if (signum() == 0)
        {
            return 0;
        }
        else
        {
            return this.impl.size();
        }
    }

    /**
     * Returns the signum function of this apfloat.
     *
     * @return -1, 0 or 1 as the value of this apfloat is negative, zero or positive, correspondingly.
     */

    public int signum()
    {
        return this.impl.signum();
    }

    /**
     * Returns if this apfloat is "short". In practice an apfloat is "short" if its
     * mantissa fits in one machine word. If the apfloat is "short", some algorithms
     * can be performed faster.<p>
     *
     * For example, division by a "short" apfloat requires only a single pass through
     * the data, but that algorithm can't be used for divisors that aren't "short",
     * where calculating an inverse root is required instead.<p>
     *
     * The return value of this method is implementation dependent.
     *
     * @return <code>true</code> if the apfloat is "short", <code>false</code> if not.
     */

    public boolean isShort()
        throws ApfloatRuntimeException
    {
        return this.impl.isShort();
    }

    /**
     * Negative value.
     *
     * @return <code>-this</code>.
     *
     * @since 1.1
     */

    public Apfloat negate()
        throws ApfloatRuntimeException
    {
        return new Apfloat(this.impl.negate());
    }

    /**
     * Adds two apfloats.
     *
     * @param x The number to be added to this number.
     *
     * @return <code>this + x</code>.
     */

    public Apfloat add(Apfloat x)
        throws ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            // x + 0 = x
            return this;
        }
        else if (signum() == 0)
        {
            // 0 + x = x
            return x;
        }

        return addOrSubtract(x, false);
    }

    /**
     * Subtracts two apfloats.
     *
     * @param x The number to be subtracted from this number.
     *
     * @return <code>this - x</code>.
     */

    public Apfloat subtract(Apfloat x)
        throws ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            // x - 0 = x
            return this;
        }
        else if (signum() == 0)
        {
            ApfloatImpl impl = x.getImpl();
            impl = impl.negate();

            return new Apfloat(impl);
        }

        return addOrSubtract(x, true);
    }

    private Apfloat addOrSubtract(Apfloat x, boolean subtract)
        throws ApfloatRuntimeException
    {
        long[] precisions = ApfloatHelper.getMatchingPrecisions(this, x);
        ApfloatImpl impl;

        if (precisions[0] == 0)
        {
            impl = x.getImpl(precisions[1]);
            if (subtract)
            {
                impl = impl.negate();
            }
        }
        else if (precisions[1] == 0)
        {
            impl = getImpl(precisions[0]);
        }
        else
        {
            impl = getImpl(precisions[0]);
            ApfloatImpl xImpl = x.getImpl(precisions[1]);
            impl = impl.addOrSubtract(xImpl, subtract);
        }

        return new Apfloat(impl);
    }

    /**
     * Multiplies two apfloats.
     *
     * @param x The number to be multiplied by this number.
     *
     * @return <code>this * x</code>.
     */

    public Apfloat multiply(Apfloat x)
        throws ApfloatRuntimeException
    {
        if (signum() == 0)
        {
            // 0 * x = 0
            return this;
        }
        else if (x.signum() == 0)
        {
            // x * 0 = 0
            return x;
        }
        else if (equals(ONE))
        {
            // 1 * x = x
            return x.precision(Math.min(precision(), x.precision()));
        }
        else if (x.equals(ONE))
        {
            // x * 1 = x
            return precision(Math.min(precision(), x.precision()));
        }

        long targetPrecision = Math.min(precision(),
                                        x.precision());

        ApfloatImpl thisImpl = getImpl(targetPrecision),
                    xImpl = x.getImpl(targetPrecision),
                    impl = thisImpl.multiply(xImpl);

        return new Apfloat(impl);
    }

    /**
     * Divides two apfloats.
     *
     * @param x The number by which this number is to be divided.
     *
     * @return <code>this / x</code>.
     *
     * @exception java.lang.ArithmeticException In case the divisor is zero.
     */

    public Apfloat divide(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            throw new ArithmeticException(signum() == 0 ? "Zero divided by zero" : "Division by zero");
        }
        else if (signum() == 0)
        {
            // 0 / x = 0
            return this;
        }
        else if (x.equals(ONE))
        {
            // x / 1 = x
            return precision(Math.min(precision(), x.precision()));
        }

        long targetPrecision = Math.min(precision(),
                                        x.precision());

        if (x.isShort())
        {
            ApfloatImpl thisImpl = getImpl(targetPrecision),
                        xImpl = x.getImpl(targetPrecision),
                        impl = thisImpl.divideShort(xImpl);

            return new Apfloat(impl);
        }
        else
        {
            Apfloat inverse = ApfloatMath.inverseRoot(x, 1, targetPrecision);
            return multiply(inverse);
        }
    }

    /**
     * Calculates the remainder when divided by an apfloat.
     * The result has the same sign as this number.
     * If <code>x</code> is zero, then zero is returned.
     *
     * @param x The number that is used as the divisor in the remainder calculation.
     *
     * @return <code>this % x</code>.
     *
     * @see ApfloatMath#fmod(Apfloat,Apfloat)
     *
     * @since 1.2
     */

    public Apfloat mod(Apfloat x)
        throws ApfloatRuntimeException
    {
        return ApfloatMath.fmod(this, x);
    }

    /**
     * Floor function. Returns the largest (closest to positive infinity) value
     * that is not greater than this apfloat and is equal to a mathematical integer.
     *
     * @return This apfloat rounded towards negative infinity.
     */

    public Apint floor()
        throws ApfloatRuntimeException
    {
        if (signum() >= 0)
        {
            return new Apint(new Apfloat(this.impl.absFloor()));
        }
        else
        {
            return new Apint(new Apfloat(this.impl.absCeil()));
        }
    }

    /**
     * Ceiling function. Returns the smallest (closest to negative infinity) value
     * that is not less than this apfloat and is equal to a mathematical integer.
     *
     * @return This apfloat rounded towards positive infinity.
     */

    public Apint ceil()
        throws ApfloatRuntimeException
    {
        if (signum() >= 0)
        {
            return new Apint(new Apfloat(this.impl.absCeil()));
        }
        else
        {
            return new Apint(new Apfloat(this.impl.absFloor()));
        }
    }

    /**
     * Truncates fractional part.
     *
     * @return This apfloat rounded towards zero.
     */

    public Apint truncate()
        throws ApfloatRuntimeException
    {
        return new Apint(new Apfloat(this.impl.absFloor()));
    }

    /**
     * Returns the fractional part. The fractional part is always <code>0 <= abs(frac()) < 1</code>.
     * The fractional part has the same sign as the number. For the fractional and integer parts, this always holds:<p>
     *
     * <code>x = x.truncate() + x.frac()</code>
     *
     * @return The fractional part of this apfloat.
     *
     * @since 1.7.0
     */

    public Apfloat frac()
        throws ApfloatRuntimeException
    {
        return new Apfloat(this.impl.frac());
    }

    /**
     * Returns the value of the this number as a <code>double</code>.
     * If the number is too big to fit in a <code>double</code>,
     * <code>Double.POSITIVE_INFINITY</code> or
     * <code>Double.NEGATIVE_INFINITY</code> is returned.
     *
     * @return The numeric value represented by this object after conversion to type <code>double</code>.
     */

    public double doubleValue()
    {
        int targetPrecision = ApfloatHelper.getDoublePrecision(radix());
        ApfloatImpl impl = getImpl(targetPrecision);

        return impl.doubleValue();
    }

    /**
     * Returns the value of the this number as a <code>float</code>.
     * If the number is too big to fit in a <code>float</code>,
     * <code>Float.POSITIVE_INFINITY</code> or
     * <code>Float.NEGATIVE_INFINITY</code> is returned.
     *
     * @return The numeric value represented by this object after conversion to type <code>float</code>.
     */

    public float floatValue()
    {
        return (float) doubleValue();
    }

    /**
     * Returns the value of the this number as a <code>byte</code>.
     * If the number is too big to fit in a <code>byte</code>,
     * <code>Byte.MIN_VALUE</code> or
     * <code>Byte.MAX_VALUE</code> is returned.
     *
     * @return The numeric value represented by this object after conversion to type <code>byte</code>.
     */

    public byte byteValue()
    {
        long longValue = longValue();
        return (byte) Math.min(Math.max(longValue, Byte.MIN_VALUE), Byte.MAX_VALUE);
    }

    /**
     * Returns the value of the this number as a <code>short</code>.
     * If the number is too big to fit in a <code>short</code>,
     * <code>Short.MIN_VALUE</code> or
     * <code>Short.MAX_VALUE</code> is returned.
     *
     * @return The numeric value represented by this object after conversion to type <code>short</code>.
     */

    public short shortValue()
    {
        long longValue = longValue();
        return (short) Math.min(Math.max(longValue, Short.MIN_VALUE), Short.MAX_VALUE);
    }

    /**
     * Returns the value of the this number as an <code>int</code>.
     * If the number is too big to fit in an <code>int</code>,
     * <code>Integer.MIN_VALUE</code> or
     * <code>Integer.MAX_VALUE</code> is returned.
     *
     * @return The numeric value represented by this object after conversion to type <code>int</code>.
     */

    public int intValue()
    {
        long longValue = longValue();
        return (int) Math.min(Math.max(longValue, Integer.MIN_VALUE), Integer.MAX_VALUE);
    }

    /**
     * Returns the value of the this number as a <code>long</code>.
     * If the number is too big to fit in a <code>long</code>,
     * <code>Long.MIN_VALUE</code> or
     * <code>Long.MAX_VALUE</code> is returned.
     *
     * @return The numeric value represented by this object after conversion to type <code>long</code>.
     */

    public long longValue()
    {
        int targetPrecision = ApfloatHelper.getLongPrecision(radix());
        ApfloatImpl impl = getImpl(targetPrecision);

        return impl.longValue();
    }

    /**
     * Computes number of equal digits.<p>
     *
     * Compares the digits of the numbers starting from the
     * most significant digits. The exponent and sign are
     * taken into consideration, so if either one doesn't match,
     * the numbers are considered to have zero equal digits.<p>
     *
     * For example, the numbers 12345 and 123456 have zero
     * matching digits, and the numbers 12345 and 12355 have
     * three matching digits.<p>
     *
     * The result of this method is roughly equal to
     * <code>Math.min(scale(), x.scale()) - subtract(x).scale()</code>
     * but it typically is a lot more efficient to execute.
     *
     * @param x Number to compare with.
     *
     * @return Number of matching digits in the radix in which the numbers are presented.
     */

    public long equalDigits(Apfloat x)
        throws ApfloatRuntimeException
    {
        long targetPrecision = Math.min(precision(),
                                        x.precision());
        ApfloatImpl thisImpl = getImpl(targetPrecision),
                    xImpl = x.getImpl(targetPrecision);

        return thisImpl.equalDigits(xImpl);
    }

    /**
     * Convert this apfloat to the specified radix.
     *
     * @param radix The radix.
     *
     * @exception java.lang.NumberFormatException If the radix is invalid.
     *
     * @since 1.2
     */

    public Apfloat toRadix(int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return RadixConversionHelper.toRadix(this, radix);
    }

    /**
     * Compare this apfloat to the specified apfloat.<p>
     *
     * Note: if two apfloats are compared where one number doesn't have enough
     * precise digits, the mantissa is assumed to contain zeros. For example:<p>
     *
     * <pre>
     * Apfloat x = new Apfloat("0.12", 2);
     * Apfloat y = new Apfloat("0.12345", 5);
     * </pre>
     *
     * Now <code>x.compareTo(y) < 0</code> because <code>x</code> is assumed to
     * be <code>0.12000</code>.<p>
     *
     * However, <code>new Apfloat("0.12", 2)</code> and <code>new Apfloat("0.12", 5)</code>
     * would be considered equal.
     *
     * @param x Apfloat to which this apfloat is to be compared.
     *
     * @return -1, 0 or 1 as this apfloat is numerically less than, equal to, or greater than <code>x</code>.
     */

    public int compareTo(Apfloat x)
    {
        if (x.preferCompare(this))
        {
            // Special handling of aprationals
            return -x.compareTo(this);
        }
        else
        {
            // Compare with maximum available precision; would not be efficient with aprationals
            return getImpl().compareTo(x.getImpl());
        }
    }

    /**
     * Tests if the comparison with <code>equals</code> and <code>compareTo</code> should be done in the opposite order.<p>
     *
     * Implementations should avoid infinite recursion.
     *
     * @param x The number to compare to.
     *
     * @return <code>true</code> if this object should invoke <code>x.equals(this)</code> and <code>-x.compareTo(this)</code> instead of comparing normally.
     *
     * @since 1.7.0
     */

    public boolean preferCompare(Apfloat x)
    {
        return false;
    }

    /**
     * Compares this object to the specified object.<p>
     *
     * Note: if two apfloats are compared where one number doesn't have enough
     * precise digits, the mantissa is assumed to contain zeros.
     * See {@link #compareTo(Apfloat)}.
     *
     * @param obj The object to compare with.
     *
     * @return <code>true</code> if the objects are equal; <code>false</code> otherwise.
     */

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof Apfloat)
        {
            Apfloat x = (Apfloat) obj;
            if (x.preferCompare(this))
            {
                // Special handling of aprationals
                return x.equals(this);
            }
            return getImpl().equals(x.getImpl());
        }
        else
        {
            return super.equals(obj);
        }
    }

    /**
     * Returns a hash code for this apfloat.
     *
     * @return The hash code value for this object.
     */

    public int hashCode()
    {
        return this.impl.hashCode();
    }

    /**
     * Returns a string representation of this apfloat.
     *
     * @param pretty <code>true</code> to use a fixed-point notation, <code>false</code> to use an exponential notation.
     *
     * @return A string representing this object.
     */

    public String toString(boolean pretty)
        throws ApfloatRuntimeException
    {
        return this.impl.toString(pretty);
    }

    /**
     * Write a string representation of this apfloat to a <code>Writer</code>.
     *
     * @param out The output <code>Writer</code>.
     * @param pretty <code>true</code> to use a fixed-point notation, <code>false</code> to use an exponential notation.
     *
     * @exception java.io.IOException In case of I/O error writing to the stream.
     */

    public void writeTo(Writer out, boolean pretty)
        throws IOException, ApfloatRuntimeException
    {
        this.impl.writeTo(out, pretty);
    }

    /**
     * Formats the object using the provided formatter.<p>
     *
     * The format specifiers affect the output as follows:<p>
     * <ul>
     *   <li>By default, the exponential notation is used.</li>
     *   <li>If the alternate format is specified (<code>'#'</code>), then the fixed-point notation is used.</li>
     *   <li>Width is the minimum number of characters output. Any padding is done using spaces. Padding is on the left by default.</li>
     *   <li>If the <code>'-'</code> flag is specified, then the padding will be on the right.</li>
     *   <li>The precision is the number of significant digts output. If the precision of the number exceeds the number of characters output, the rounding mode for output is undefined.</li>
     * </ul>
     *
     * The decimal separator will be localized if the formatter specifies a locale.
     * The digits will be localized also, but only if the radix is less than or equal to 10.
     *
     * @param formatter The formatter.
     * @param flags The flags to modify the output format.
     * @param width The minimum number of characters to be written to the output, or <code>-1</code> for no minimum.
     * @param precision The maximum number of characters to be written to the output, or <code>-1</code> for no maximum.
     *
     * @since 1.3
     */

    public void formatTo(Formatter formatter, int flags, int width, int precision)
    {
        Apfloat x = (precision == -1 ? this : ApfloatHelper.limitPrecision(this, precision));
        try
        {
            Writer out = FormattingHelper.wrapAppendableWriter(formatter.out());
            out = FormattingHelper.wrapLocalizeWriter(out, formatter, radix(), (flags & UPPERCASE) == UPPERCASE);
            if (width == -1)
            {
                x.writeTo(out, (flags & ALTERNATE) == ALTERNATE);
            }
            else
            {
                out = FormattingHelper.wrapPadWriter(out, (flags & LEFT_JUSTIFY) == LEFT_JUSTIFY);
                x.writeTo(out, (flags & ALTERNATE) == ALTERNATE);
                FormattingHelper.finishPad(out, width);
            }
        }
        catch (IOException ioe)
        {
            // Ignore as we can't propagate it; unfortunately we can't set it to the formattable either
        }
    }

    /**
     * Returns an <code>ApfloatImpl</code> representing the actual instance
     * of this apfloat up to the requested precision.<p>
     *
     * For apfloats this is simply the underlying <code>ApfloatImpl</code>,
     * but e.g. the {@link Aprational} class implements this so that
     * it only returns an approximation of the rational number.
     *
     * @param precision Precision of the <code>ApfloatImpl</code> that is needed.
     *
     * @return An <code>ApfloatImpl</code> representing this object to the requested precision.
     */

    protected ApfloatImpl getImpl(long precision)
        throws ApfloatRuntimeException
    {
        if (precision == precision())
        {
            return this.impl;
        }
        else
        {
            return this.impl.precision(precision);
        }
    }

    // Round away from zero i.e. opposite direction of rounding than in truncate()
    Apint roundAway()
        throws ApfloatRuntimeException
    {
        return new Apint(new Apfloat(this.impl.absCeil()));
    }

    Apfloat scale(long scale)
    {
        return ApfloatMath.scale(this, scale);
    }

    Apfloat abs()
    {
        return ApfloatMath.abs(this);
    }

    int compareToHalf()
    {
        return RoundingHelper.compareToHalf(this);
    }

    private ApfloatImpl getImpl()
        throws ApfloatRuntimeException
    {
        long precision = precision();
        return getImpl(precision);
    }

    private static final long serialVersionUID = -36707433458144439L;

    private ApfloatImpl impl;
}
