package org.apfloat;

import java.io.Serializable;
import java.io.PushbackReader;
import java.io.Writer;
import java.io.IOException;
import java.util.Formattable;
import java.util.Formatter;
import static java.util.FormattableFlags.*;

/**
 * Arbitrary precision complex number class. An apcomplex consists of
 * a real and imaginary part of type {@link Apfloat}.<p>
 *
 * Note that although the Apcomplex class extends <code>Number</code>,
 * the methods inherited from <code>Number</code> return the value of
 * the real part of the complex number. Thus they are more meaningful
 * for the {@link Apfloat} class and its subclasses.<p>
 *
 * @see Apfloat
 * @see ApcomplexMath
 *
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class Apcomplex
    extends Number
    implements Formattable, Serializable
{
    /**
     * Constant for zero. It is safe to use <code>ZERO</code>
     * in all addition, subtraction, multiplication, division and
     * comparison operations regardless of the radix used.
     */

    public static final Apint ZERO = new Apint(0);

    /**
     * Constant for one. Note that this number is created using the
     * initial default radix. It is safe to use <code>ONE</code>
     * in all multiplication, division and equality comparison operations
     * regardless of the radix used. However, in subtraction and addition
     * it only works with numbers in the same radix.
     */

    public static final Apint ONE = new Apint(1);

    /**
     * Imaginary unit. That is, <code>Apcomplex(ZERO, ONE)</code>.
     * It is safe to use <code>I</code> in all multiplication,
     * division and equality comparison operations
     * regardless of the radix used. In addition and subtraction
     * it only works with numbers in the same radix.
     */

    public static final Apcomplex I = new Apcomplex(ZERO, ONE);

    /**
     * Infinite precision or scale. Can be used as the precision argument
     * when constructing apfloats.
     */

    public static final long INFINITE = Long.MAX_VALUE;

    /**
     * Default precision. Can be used as an argument when constructing apfloats.
     */

    public static final long DEFAULT = 0x8000000000000000L;

    /**
     * Extra precision that is added in various apfloat internal
     * operations to avoid round-off errors.
     */

    static final int EXTRA_PRECISION = 20;

    /**
     * Default constructor. To be used only by subclasses that
     * overload all needed methods.
     */

    protected Apcomplex()
    {
    }

    /**
     * Construct a real apcomplex whose imaginary part is zero.
     *
     * @param real The real part of the number.
     */

    public Apcomplex(Apfloat real)
    {
        this(real, ZERO);
    }

    /**
     * Construct an apcomplex with the specified real and imaginary part.
     *
     * @param real The real part of the number.
     * @param imag The imaginary part of the number.
     *
     * @exception java.lang.IllegalArgumentException If the real part and imaginary part are not zero but have different radixes.
     */

    public Apcomplex(Apfloat real, Apfloat imag)
        throws IllegalArgumentException
    {
        if (real.signum() != 0 && imag.signum() != 0 && real.radix() != imag.radix())
        {
            throw new IllegalArgumentException("Real part and imaginary part must have the same radix");
        }

        this.real = real;
        this.imag = imag;
    }

    /**
     * Constructs an apcomplex from a string.<p>
     *
     * The input must be of one of the formats<p>
     *
     * <code>realPart</code><br>
     * <code>"(" [whitespace] realPart [whitespace] ")"</code><br>
     * <code>"(" [whitespace] realPart [whitespace] "," [whitespace] imaginaryPart [whitespace] ")"</code><br>
     *
     * @param value The input string.
     *
     * @exception java.lang.NumberFormatException If the number is invalid.
     */

    public Apcomplex(String value)
        throws NumberFormatException, ApfloatRuntimeException
    {
        if (!value.startsWith("("))
        {
            this.real = new Apfloat(value);
            this.imag = ZERO;
            return;
        }
        if (!value.endsWith(")"))
        {
            throw new NumberFormatException("Missing end parenthesis");
        }

        int index = value.indexOf(',');
        if (index < 0)
        {
            this.real = new Apfloat(value.substring(1, value.length() - 1).trim());
            this.imag = ZERO;
            return;
        }

        this.real = new Apfloat(value.substring(1, index).trim());
        this.imag = new Apfloat(value.substring(index + 1, value.length() - 1).trim());
    }

    /**
     * Reads an apcomplex from a reader. The constructor stops reading
     * at the first character it doesn't understand. The reader must thus
     * be a <code>PushbackReader</code> so that the invalid character can
     * be returned back to the stream.<p>
     *
     * The input must be of one of the formats<p>
     *
     * <code>realPart</code><br>
     * <code>"(" [whitespace] realPart [whitespace] ")"</code><br>
     * <code>"(" [whitespace] realPart [whitespace] "," [whitespace] imaginaryPart [whitespace] ")"</code><br>
     *
     * @param in The input stream.
     *
     * @exception java.io.IOException In case of I/O error reading from the stream.
     * @exception java.lang.NumberFormatException If the number is invalid.
     */

    public Apcomplex(PushbackReader in)
        throws IOException, NumberFormatException, ApfloatRuntimeException
    {
        if (!ApfloatHelper.readMatch(in, '('))
        {
            this.real = new Apfloat(in);
            this.imag = ZERO;
            return;
        }

        ApfloatHelper.extractWhitespace(in);
        this.real = new Apfloat(in);
        ApfloatHelper.extractWhitespace(in);

        if (ApfloatHelper.readMatch(in, ','))
        {
            ApfloatHelper.extractWhitespace(in);
            this.imag = new Apfloat(in);
        }
        else
        {
            this.imag = ZERO;
        }

        ApfloatHelper.extractWhitespace(in);

        if (!ApfloatHelper.readMatch(in, ')'))
        {
            throw new NumberFormatException("Missing end parenthesis");
        }
    }

    /**
     * Radix of this apcomplex.
     *
     * @return Radix of this apcomplex.
     */

    public int radix()
    {
        return (real().signum() == 0 ? (imag().signum() == 0 ? real().radix() : imag().radix()) : real().radix());
    }

    /**
     * Returns the real part of this apcomplex.
     *
     * @return The real part of this apcomplex.
     */

    public Apfloat real()
    {
        return this.real;
    }

    /**
     * Returns the imaginary part of this apcomplex.
     *
     * @return The imaginary part of this apcomplex.
     */

    public Apfloat imag()
    {
        return this.imag;
    }

    /**
     * Returns the complex conjugate of this apcomplex.
     *
     * @return <code>x - <i>i</i> y</code>, where this apcomplex is <code>x + <i>i</i> y</code>.
     */

    public Apcomplex conj()
        throws ApfloatRuntimeException
    {
        return new Apcomplex(real(), imag().negate());
    }

    /**
     * Returns the precision of this apcomplex.
     *
     * @return The precision of this apcomplex in number of digits of the radix in which it's presented.
     */

    public long precision()
        throws ApfloatRuntimeException
    {
        if (real().signum() == 0 || imag().signum() == 0)
        {
            return Math.min(real().precision(), imag().precision());
        }
        else
        {
            long[] precisions = ApfloatHelper.getMatchingPrecisions(real(), imag());

            return Math.max(precisions[0], precisions[1]);
        }
    }

    /**
     * Returns an apcomplex with the same value as this apcomplex accurate to the
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
     * @param precision Precision of the new apcomplex.
     *
     * @return An apcomplex with the specified precision and same value as this apcomplex.
     *
     * @exception java.lang.IllegalArgumentException If <code>precision</code> is <= 0.
     *
     * @since 1.2
     */

    public Apcomplex precision(long precision)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        ApfloatHelper.checkPrecision(precision);

        Apcomplex z = new Apcomplex(real().precision(precision), imag().precision(precision));

        if (real().signum() == 0 || imag().signum() == 0)
        {
            return z;
        }

        long[] precisions = ApfloatHelper.getMatchingPrecisions(z.real(), z.imag());
        long realPrecision = precisions[0],
             imagPrecision = precisions[1];

        return new Apcomplex(realPrecision > 0 ? z.real().precision(realPrecision) : Apfloat.ZERO,
                             imagPrecision > 0 ? z.imag().precision(imagPrecision) : Apfloat.ZERO);
    }

    /**
     * Returns the scale of this apcomplex. The scale is the maximum of the scale of the real part and imaginary part.<p>
     *
     * Zero has a scale of <code>-INFINITE</code>.
     *
     * @return The exponent of this apcomplex in number of digits of the radix in which it's presented.
     *
     * @see Apfloat#scale()
     */

    public long scale()
        throws ApfloatRuntimeException
    {
        return Math.max(real().scale(), imag().scale());
    }

    /**
     * Returns the size of this apcomplex. The size is the maximum of the size of the real part and imaginary part.<p>
     *
     * Zero has a size of <code>0</code>.
     *
     * @return The number of digits in this number, from the most significant digit to the least significant nonzero digit, in the radix in which it's presented.
     *
     * @see Apfloat#size()
     *
     * @since 1.6
     */

    public long size()
        throws ApfloatRuntimeException
    {
        return Math.max(real().size(), imag().size());
    }

    /**
     * Negative value.
     *
     * @return <code>-this</code>.
     *
     * @since 1.1
     */

    public Apcomplex negate()
        throws ApfloatRuntimeException
    {
        return new Apcomplex(real().negate(), imag().negate());
    }

    /**
     * Adds two apcomplex numbers.
     *
     * @param z The number to be added to this number.
     *
     * @return <code>this + z</code>.
     */

    public Apcomplex add(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return new Apcomplex(real().add(z.real()),
                             imag().add(z.imag()));
    }

    /**
     * Subtracts two apcomplex numbers.
     *
     * @param z The number to be subtracted from this number.
     *
     * @return <code>this - z</code>.
     */

    public Apcomplex subtract(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return new Apcomplex(real().subtract(z.real()),
                             imag().subtract(z.imag()));
    }

    /**
     * Multiplies two apcomplex numbers.
     *
     * @param z The number to be multiplied by this number.
     *
     * @return <code>this * z</code>.
     */

    public Apcomplex multiply(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return new Apcomplex(ApfloatMath.multiplySubtract(real(), z.real(), imag(), z.imag()),
                             ApfloatMath.multiplyAdd(real(), z.imag(), imag(), z.real()));
    }

    /**
     * Divides two apcomplex numbers.
     *
     * @param z The number by which this number is to be divided.
     *
     * @return <code>this / z</code>.
     *
     * @exception java.lang.ArithmeticException In case the divisor is zero.
     */

    public Apcomplex divide(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z.real().signum() == 0 && z.imag().signum() == 0)
        {
            throw new ArithmeticException(real().signum() == 0 && imag().signum() == 0 ? "Zero divided by zero" : "Division by zero");
        }

        Apfloat tmpReal,
                tmpImag;

        // Multiply both numbers by i if z is pure imaginary
        if (z.real().signum() == 0)
        {
            z = new Apcomplex(z.imag(), z.real().negate());
            tmpReal = imag();
            tmpImag = real().negate();
        }
        else
        {
            tmpReal = real();
            tmpImag = imag();
        }

        if (tmpImag.signum() == 0)
        {
            if (tmpReal.signum() == 0)
            {
                // 0 / x = 0
                return this;
            }
            else if (z.imag().signum() == 0)
            {
                // Real
                return tmpReal.divide(z.real());
            }
        }
        else if (z.imag().signum() == 0)
        {
            if (z.real().equals(ONE))
            {
                // x / 1 = x
                return new Apcomplex(tmpReal.precision(Math.min(tmpReal.precision(), z.real().precision())),
                                     tmpImag.precision(Math.min(tmpImag.precision(), z.real().precision())));
            }
            else if (z.real().isShort())
            {
                // If the divisor real and "short", it's faster to divide directly
                return new Apcomplex(tmpReal.divide(z.real()),
                                     tmpImag.divide(z.real()));
            }

            // If the divisor is real but not "short", it's faster to generate the inverse root (only once) and multiply by it
            long precision = Math.min(precision(), z.real().precision());
            Apfloat inverse = ApfloatMath.inverseRoot(z.real(), 1, precision);

            return new Apcomplex(tmpReal.multiply(inverse),
                                 tmpImag.multiply(inverse));
        }

        long precision = Math.min(precision(), z.precision());
        Apcomplex zApprox = new Apcomplex(z.real().precision(Math.min(precision, z.real().precision())),
                                          z.imag().precision(Math.min(precision, z.imag().precision())));

        return multiply(z.conj()).divide(ApcomplexMath.norm(zApprox));
    }

    /**
     * Returns the value of the this number as a <code>double</code>.
     * Only takes the real part of this number.
     *
     * @see Apfloat#doubleValue()
     *
     * @return The numeric value represented by this object after conversion to type <code>double</code>.
     */

    public double doubleValue()
    {
        return real().doubleValue();
    }

    /**
     * Returns the value of the this number as a <code>float</code>.
     * Only takes the real part of this number.
     *
     * @see Apfloat#floatValue()
     *
     * @return The numeric value represented by this object after conversion to type <code>float</code>.
     */

    public float floatValue()
    {
        return real().floatValue();
    }

    /**
     * Returns the value of the this number as a <code>byte</code>.
     * Only takes the real part of this number.
     *
     * @see Apfloat#byteValue()
     *
     * @return The numeric value represented by this object after conversion to type <code>byte</code>.
     */

    public byte byteValue()
    {
        return real().byteValue();
    }

    /**
     * Returns the value of the this number as a <code>short</code>.
     * Only takes the real part of this number.
     *
     * @see Apfloat#shortValue()
     *
     * @return The numeric value represented by this object after conversion to type <code>short</code>.
     */

    public short shortValue()
    {
        return real().shortValue();
    }

    /**
     * Returns the value of the this number as an <code>int</code>.
     * Only takes the real part of this number.
     *
     * @see Apfloat#intValue()
     *
     * @return The numeric value represented by this object after conversion to type <code>int</code>.
     */

    public int intValue()
    {
        return real().intValue();
    }

    /**
     * Returns the value of the this number as a <code>long</code>.
     * Only takes the real part of this number.
     *
     * @see Apfloat#longValue()
     *
     * @return The numeric value represented by this object after conversion to type <code>long</code>.
     */

    public long longValue()
    {
        return real().longValue();
    }

    /**
     * Computes number of equal digits.<p>
     *
     * Compares the digits of the numbers starting from the
     * most significant digits. The exponent and sign are
     * taken into consideration, so if either one doesn't match,
     * the numbers are considered to have zero equal digits.<p>
     *
     * For example, the numbers (12345, 123) and (123456, 12) have
     * zero matching digits, and the numbers (12345, 12) and
     * (12355, 13) have three matching digits.
     *
     * @param z Number to compare with.
     *
     * @return Number of matching digits in the radix in which the numbers are presented.
     */

    public long equalDigits(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (real().signum() == 0 && imag().signum() == 0 &&
            z.real().signum() == 0 && z.imag().signum() == 0)
        {
            // Both are zero
            return Apfloat.INFINITE;
        }

        long minScale = Math.min(scale(), z.scale()),
             maxScale = Math.max(scale(), z.scale());

        if (maxScale - 1 > minScale)
        {
            // No match
            return 0;
        }
        else
        {
            // Neither is zero, but the real part OR the imaginary part of each number may be zero
            long realScale = Math.max(real().scale(), z.real().scale()),
                 imagScale = Math.max(imag().scale(), z.imag().scale()),
                 realScaleDiff = (maxScale - realScale < 0 ? Apfloat.INFINITE : maxScale - realScale),
                 imagScaleDiff = (maxScale - imagScale < 0 ? Apfloat.INFINITE : maxScale - imagScale),
                 realEquals = real().equalDigits(z.real()),
                 imagEquals = imag().equalDigits(z.imag());

            return Math.min(realEquals + realScaleDiff < 0 ? Apfloat.INFINITE : realEquals + realScaleDiff,
                            imagEquals + imagScaleDiff < 0 ? Apfloat.INFINITE : imagEquals + imagScaleDiff);
        }
    }

    /**
     * Convert this apcomplex to the specified radix.
     *
     * @param radix The radix.
     *
     * @return This number in the specified radix.
     *
     * @exception java.lang.NumberFormatException If the radix is invalid.
     *
     * @since 1.2
     */

    public Apcomplex toRadix(int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return new Apcomplex(real().toRadix(radix), imag().toRadix(radix));
    }

    /**
     * Compares this object to the specified object.<p>
     *
     * Note: two apfloats are considered equal if they have an identical mantissa,
     * but different precision.
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
        else if (obj instanceof Apcomplex)
        {
            Apcomplex that = (Apcomplex) obj;
            return real().equals(that.real()) &&
                   imag().equals(that.imag());
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns a hash code for this apcomplex.
     *
     * @return The hash code value for this object.
     */

    public int hashCode()
    {
        return real().hashCode() * 3 +
               imag().hashCode();
    }

    /**
     * Returns a string representation of this apcomplex.
     *
     * @return A string representing this object.
     */

    public String toString()
    {
        return toString(false);
    }

    /**
     * Returns a string representation of this apcomplex.
     *
     * @param pretty <code>true</code> to use a fixed-point notation, <code>false</code> to use an exponential notation.
     *
     * @return A string representing this object.
     */

    public String toString(boolean pretty)
        throws ApfloatRuntimeException
    {
        if (imag().signum() == 0)
        {
            return real().toString(pretty);
        }
        else
        {
            return '(' + real().toString(pretty) + ", " +
                         imag().toString(pretty) + ')';
        }
    }

    /**
     * Write a string representation of this apcomplex to a <code>Writer</code>.
     *
     * @param out The output <code>Writer</code>.
     *
     * @exception java.io.IOException In case of I/O error writing to the stream.
     */

    public void writeTo(Writer out)
        throws IOException, ApfloatRuntimeException
    {
        writeTo(out, false);
    }

    /**
     * Write a string representation of this apcomplex to a <code>Writer</code>.
     *
     * @param out The output <code>Writer</code>.
     * @param pretty <code>true</code> to use a fixed-point notation, <code>false</code> to use an exponential notation.
     *
     * @exception java.io.IOException In case of I/O error writing to the stream.
     */

    public void writeTo(Writer out, boolean pretty)
        throws IOException, ApfloatRuntimeException
    {
        if (imag().signum() == 0)
        {
            real().writeTo(out, pretty);
        }
        else
        {
            out.write('(');
            real().writeTo(out, pretty);
            out.write(", ");
            imag().writeTo(out, pretty);
            out.write(')');
        }
    }

    /**
     * Formats the object using the provided formatter.
     *
     * @param formatter The formatter.
     * @param flags The flags to modify the output format.
     * @param width The minimum number of characters to be written to the output, or <code>-1</code> for no minimum.
     * @param precision The maximum number of characters to be written to the output, or <code>-1</code> for no maximum.
     *
     * @since 1.3
     *
     * @see Apfloat#formatTo(Formatter,int,int,int)
     */

    public void formatTo(Formatter formatter, int flags, int width, int precision)
    {
        if (imag().signum() == 0)
        {
            real().formatTo(formatter, flags, width, precision);
        }
        else
        {
            if (width == -1)
            {
                formatter.format("(");
                real().formatTo(formatter, flags, width, precision);
                formatter.format(", ");
                imag().formatTo(formatter, flags, width, precision);
                formatter.format(")");
            }
            else
            {
                try
                {
                    Writer out = FormattingHelper.wrapAppendableWriter(formatter.out());
                    out = FormattingHelper.wrapPadWriter(out, (flags & LEFT_JUSTIFY) == LEFT_JUSTIFY);
                    formatter = new Formatter(out, formatter.locale());
                    formatter.format("(");
                    real().formatTo(formatter, flags, -1, precision);
                    formatter.format(", ");
                    imag().formatTo(formatter, flags, -1, precision);
                    formatter.format(")");
                    FormattingHelper.finishPad(out, width);
                }
                catch (IOException ioe)
                {
                    // Ignore as we can't propagate it; unfortunately we can't set it to the formattable either
                }
            }
        }
    }

    static final Apint[] ONES;

    static
    {
        ONES = new Apint[37];
        for (int i =  2; i <= 36; i++)
        {
            ONES[i] = new Apint(1, i);
        }
        ONES[ONE.radix()] = ONE;
    }

    private static final long serialVersionUID = 3642932980384250551L;

    private Apfloat real;
    private Apfloat imag;
}
