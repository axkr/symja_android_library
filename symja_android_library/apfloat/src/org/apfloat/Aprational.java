package org.apfloat;

import java.math.BigInteger;
import java.io.PushbackReader;
import java.io.Writer;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Formatter;
import static java.util.FormattableFlags.*;

import org.apfloat.spi.ApfloatImpl;
import static org.apfloat.spi.RadixConstants.*;

/**
 * Arbitrary precision rational number class. An aprational consists of
 * a numerator and a denominator of type {@link Apint}.<p>
 *
 * @see Apint
 * @see AprationalMath
 *
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class Aprational
    extends Apfloat
{
    /**
     * Default constructor. To be used only by subclasses that
     * overload all needed methods.
     */

    protected Aprational()
    {
    }

    /**
     * Construct an integer aprational whose denominator is one.
     *
     * @param value The numerator of the number.
     */

    public Aprational(Apint value)
        throws ApfloatRuntimeException
    {
        this(value, ONES[value.radix()]);
    }

    /**
     * Construct an aprational with the specified numerator and denominator.
     *
     * @param numerator The numerator.
     * @param denominator The denominator.
     *
     * @exception java.lang.IllegalArgumentException In case the denominator is zero, or if the denominator is not one or the numerator is not zero, and the radix of the numerator and denominator are different.
     */

    public Aprational(Apint numerator, Apint denominator)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        this.numerator = numerator;
        this.denominator = denominator;

        checkDenominator();

        reduce();
    }

    /**
     * Constructs an aprational from a string. The default radix is used.<p>
     *
     * The input must be of one of the formats<p>
     *
     * <code>integer</code><br>
     * <code>numerator [whitespace] "/" [whitespace] denominator</code><br>
     *
     * @param value The input string.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the denominator is zero.
     */

    public Aprational(String value)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(value, ApfloatContext.getContext().getDefaultRadix());
    }

    /**
     * Constructs an aprational from a string with the specified radix.<p>
     *
     * The input must be of one of the formats<p>
     *
     * <code>integer</code><br>
     * <code>numerator [whitespace] "/" [whitespace] denominator</code><br>
     *
     * @param value The input string.
     * @param radix The radix to be used.
     *
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the denominator is zero.
     */

    public Aprational(String value, int radix)
        throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        int index = value.indexOf('/');
        if (index < 0)
        {
            this.numerator = new Apint(value, radix);
            this.denominator = ONES[radix];
            return;
        }

        this.numerator = new Apint(value.substring(0, index).trim(), radix);
        this.denominator = new Apint(value.substring(index + 1).trim(), radix);

        checkDenominator();

        reduce();
    }

    /**
     * Reads an aprational from a reader. The default radix is used. The constructor
     * stops reading at the first character it doesn't understand. The reader must
     * thus be a <code>PushbackReader</code> so that the invalid character can be
     * returned back to the stream.<p>
     *
     * The input must be of one of the formats<p>
     *
     * <code>integer [whitespace]</code><br>
     * <code>numerator [whitespace] "/" [whitespace] denominator</code><br>
     *
     * @param in The input stream.
     *
     * @exception java.io.IOException In case of I/O error reading the stream.
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the denominator is zero.
     */

    public Aprational(PushbackReader in)
        throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this(in, ApfloatContext.getContext().getDefaultRadix());
    }

    /**
     * Reads an aprational from a reader. The specified radix is used.
     *
     * @param in The input stream.
     * @param radix The radix to be used.
     *
     * @exception java.io.IOException In case of I/O error reading the stream.
     * @exception java.lang.NumberFormatException In case the number is invalid.
     * @exception java.lang.IllegalArgumentException In case the denominator is zero.
     *
     * @see #Aprational(PushbackReader)
     */

    public Aprational(PushbackReader in, int radix)
        throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException
    {
        this.numerator = new Apint(in, radix);

        ApfloatHelper.extractWhitespace(in);

        if (!ApfloatHelper.readMatch(in, '/'))
        {
            this.denominator = ONES[radix];
            return;
        }

        ApfloatHelper.extractWhitespace(in);
        this.denominator = new Apint(in, radix);

        checkDenominator();

        reduce();
    }

    /**
     * Constructs an aprational from a <code>BigInteger</code>.
     * The default radix is used.
     *
     * @param value The numerator of the number.
     */

    public Aprational(BigInteger value)
        throws ApfloatRuntimeException
    {
        this.numerator = new Apint(value);
        this.denominator = ONE;
    }

    /**
     * Constructs an aprational from a <code>BigInteger</code> using the specified radix.
     *
     * @param value The numerator of the number.
     * @param radix The radix of the number.
     */

    public Aprational(BigInteger value, int radix)
        throws ApfloatRuntimeException
    {
        this.numerator = new Apint(value, radix);
        this.denominator = ONES[radix];
    }

    /**
     * Numerator of this aprational.
     *
     * @return <code>n</code> where <code>this = n / m</code>.
     */

    public Apint numerator()
    {
        return this.numerator;
    }

    /**
     * Denominator of this aprational.
     *
     * @return <code>m</code> where <code>this = n / m</code>.
     */

    public Apint denominator()
    {
        return this.denominator;
    }

    /**
     * Radix of this aprational.
     *
     * @return Radix of this aprational.
     */

    public int radix()
    {
        return (numerator() == ONE ? denominator().radix() : numerator().radix());
    }

    /**
     * Returns the precision of this aprational.
     *
     * @return <code>INFINITE</code>
     */

    public long precision()
        throws ApfloatRuntimeException
    {
        return INFINITE;
    }

    /**
     * Returns the scale of this aprational. Scale is equal to the number of digits in the aprational's truncated value.<p>
     *
     * Zero has a scale of <code>-INFINITE</code>.
     *
     * @return Number of digits in the truncated value of this aprational in the radix in which it's presented.
     *
     * @see Apfloat#scale()
     */

    public long scale()
        throws ApfloatRuntimeException
    {
        if (signum() == 0)
        {
            return -INFINITE;
        }

        if (this.scale == UNDEFINED)
        {
            long scale = numerator().scale() - denominator().scale();

            if (scale > 0)
            {
                scale = truncate().scale();
            }
            else
            {
                scale = AprationalMath.scale(this, 1 - scale).truncate().scale() + scale - 1;
            }

            // Writes and reads of volatile long values are always atomic so multiple threads can read and write this at the same time
            this.scale = scale;
        }

        return this.scale;
    }

    /**
     * Returns the size of this aprational. Size is equal to the number of significant
     * digits in the aprational's floating-point expansion. If the expansion is infinite
     * then this method returns <code>INFINITE</code>.<p>
     *
     * Zero has a size of <code>0</code>.
     *
     * @return Number of significant digits in the floating-point expansion of this aprational in the radix in which it's presented.
     *
     * @see Apfloat#size()
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

        if (this.size == 0)
        {
            long size;

            // Check that the factorization of the divisor consists entirely of factors of the base
            // E.g. if base is 10=2*5 then the divisor should be 2^n*5^m
            Apint dividend = denominator();
            for (int i = 0; i < RADIX_FACTORS[radix()].length; i++)
            {
                Apint factor = new Apint(RADIX_FACTORS[radix()][i], radix());
                Apint[] quotientAndRemainder;

                // Keep dividing by factor as long as dividend % factor == 0
                // that is remove factors of the base from the divisor
                while ((quotientAndRemainder = ApintMath.div(dividend, factor))[1].signum() == 0)
                {
                    dividend = quotientAndRemainder[0];
                }
            }

            // Check if the divisor was factored all the way to one by just dividing by factors of the base
            if (!dividend.equals(ONE))
            {
                // No - infinite floating-point expansion
                size = INFINITE;
            }
            else
            {
                // Yes - calculate the number of digits
                // Scale the number so that all significant digits will fit in the integer part
                // The factor 5 is a rough estimate; e.g. if the denominator is 2^n then in base 34 we get close to that value
                size = ApintMath.scale(numerator(), denominator().scale() * 5).divide(denominator()).size();
            }

            // Writes and reads of volatile long values are always atomic so multiple threads can read and write this at the same time
            this.size = size;
        }

        return this.size;
    }

    /**
     * Returns the signum function of this aprational.
     *
     * @return -1, 0 or 1 as the value of this aprational is negative, zero or positive.
     */

    public int signum()
    {
        return numerator().signum();
    }

    /**
     * Returns if this aprational is "short".
     *
     * @return <code>true</code> if the aprational is "short", <code>false</code> if not.
     *
     * @see Apfloat#isShort()
     */

    public boolean isShort()
        throws ApfloatRuntimeException
    {
        return numerator().isShort() && denominator().equals(ONE);
    }

    /**
     * Negative value.
     *
     * @return <code>-this</code>.
     *
     * @since 1.1
     */

    public Aprational negate()
        throws ApfloatRuntimeException
    {
        return new Aprational(numerator().negate(), denominator());
    }

    /**
     * Adds two aprational numbers.
     *
     * @param x The number to be added to this number.
     *
     * @return <code>this + x</code>.
     */

    public Aprational add(Aprational x)
        throws ApfloatRuntimeException
    {
        return new Aprational(numerator().multiply(x.denominator()).add(denominator().multiply(x.numerator())),
                              denominator().multiply(x.denominator())).reduce();
    }

    /**
     * Subtracts two aprational numbers.
     *
     * @param x The number to be subtracted from this number.
     *
     * @return <code>this - x</code>.
     */

    public Aprational subtract(Aprational x)
        throws ApfloatRuntimeException
    {
        return new Aprational(numerator().multiply(x.denominator()).subtract(denominator().multiply(x.numerator())),
                              denominator().multiply(x.denominator())).reduce();
    }

    /**
     * Multiplies two aprational numbers.
     *
     * @param x The number to be multiplied by this number.
     *
     * @return <code>this * x</code>.
     */

    public Aprational multiply(Aprational x)
        throws ApfloatRuntimeException
    {
        Aprational result = new Aprational(numerator().multiply(x.numerator()),
                                           denominator().multiply(x.denominator()));

        if (this == x)
        {
            // When squaring we know that no reduction is needed
            return result;
        }
        else
        {
            return result.reduce();
        }
    }

    /**
     * Divides two aprational numbers.
     *
     * @param x The number by which this number is to be divided.
     *
     * @return <code>this / x</code>.
     *
     * @exception java.lang.ArithmeticException In case the divisor is zero.
     */

    public Aprational divide(Aprational x)
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
        // Comparison against one would be inefficient at this point

        return new Aprational(numerator().multiply(x.denominator()),
                              denominator().multiply(x.numerator())).reduce();
    }

    /**
     * Calculates the remainder when divided by an aprational.
     * The result has the same sign as this number.
     * If <code>x</code> is zero, then zero is returned.
     *
     * @param x The number that is used as the divisor in the remainder calculation.
     *
     * @return <code>this % x</code>.
     *
     * @since 1.2
     */

    public Aprational mod(Aprational x)
        throws ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            return x;                           // By definition
        }
        else if (signum() == 0)
        {
            // 0 % x = 0
            return this;
        }

        return subtract(divide(x).truncate().multiply(x));
    }

    /**
     * Floor function. Returns the largest (closest to positive infinity) value
     * that is not greater than this aprational and is equal to a mathematical integer.
     *
     * @return This aprational rounded towards negative infinity.
     */

    public Apint floor()
        throws ApfloatRuntimeException
    {
        if (signum() >= 0)
        {
            return truncate();
        }
        else
        {
            return roundAway();
        }
    }

    /**
     * Ceiling function. Returns the smallest (closest to negative infinity) value
     * that is not less than this aprational and is equal to a mathematical integer.
     *
     * @return This aprational rounded towards positive infinity.
     */

    public Apint ceil()
        throws ApfloatRuntimeException
    {
        if (signum() <= 0)
        {
            return truncate();
        }
        else
        {
            return roundAway();
        }
    }

    /**
     * Truncates fractional part.
     *
     * @return This aprational rounded towards zero.
     */

    public Apint truncate()
        throws ApfloatRuntimeException
    {
        return numerator().divide(denominator());
    }

    /**
     * Returns the fractional part. The fractional part is always <code>0 <= abs(x.frac()) < 1</code>.
     * The fractional part has the same sign as the number. For the fractional and integer parts, this always holds:<p>
     *
     * <code>x = x.truncate() + x.frac()</code>
     *
     * @return The fractional part of this aprational.
     *
     * @since 1.7.0
     */

    public Aprational frac()
        throws ApfloatRuntimeException
    {
        return new Aprational(numerator().mod(denominator()), denominator());
    }

    /**
     * Convert this aprational to the specified radix.
     *
     * @param radix The radix.
     *
     * @exception java.lang.NumberFormatException If the radix is invalid.
     *
     * @since 1.2
     */

    public Aprational toRadix(int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return new Aprational(numerator().toRadix(radix), denominator().toRadix(radix));
    }

    /**
     * Compare this aprational to the specified aprational.<p>
     *
     * @param x Aprational to which this aprational is to be compared.
     *
     * @return -1, 0 or 1 as this aprational is numerically less than, equal to, or greater than <code>x</code>.
     */

    public int compareTo(Aprational x)
    {
        Apint a = numerator().multiply(x.denominator()),
              b = x.numerator().multiply(denominator());

        return a.compareTo(b);
    }

    /**
     * Compare this aprational to the specified apfloat.<p>
     *
     * @param x Apfloat to which this aprational is to be compared.
     *
     * @return -1, 0 or 1 as this aprational is numerically less than, equal to, or greater than <code>x</code>.
     */

    public int compareTo(Apfloat x)
    {
        if (x instanceof Aprational)
        {
            return compareTo((Aprational) x);
        }
        else
        {
            // Sub-optimal performance wise, but works
            Apfloat a = numerator().precision(INFINITE),                // Actual class must be Apfloat
                    b = x.multiply(denominator()).precision(INFINITE);  // Actual class must be Apfloat

            return a.compareTo(b);
        }
    }

    public boolean preferCompare(Apfloat x)
    {
        return !(x instanceof Aprational);
    }

    /**
     * Compares this object to the specified object.<p>
     *
     * Note: if two apfloats are compared where one number doesn't have enough
     * precise digits, the mantissa is assumed to contain zeros.
     * See {@link Apfloat#compareTo(Apfloat)}.
     *
     * @param obj The object to compare with.
     *
     * @return <code>true</code> if the objects are the same; <code>false</code> otherwise.
     */

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof Aprational)
        {
            Aprational that = (Aprational) obj;
            return numerator().equals(that.numerator()) &&
                   denominator().equals(that.denominator());
        }
        else if (obj instanceof Apfloat)
        {
            Apfloat that = (Apfloat) obj;

            // Sub-optimal performance wise, but works
            Apfloat a = numerator().precision(INFINITE),                    // Actual class must be Apfloat
                    b = that.multiply(denominator()).precision(INFINITE);   // Actual class must be Apfloat

            return a.equals(b);
        }
        else
        {
            return super.equals(obj);
        }
    }

    /**
     * Returns a hash code for this aprational.
     *
     * @return The hash code value for this object.
     */

    public int hashCode()
    {
        return numerator().hashCode() * 3 +
               denominator().hashCode();
    }

    /**
     * Returns a string representation of this aprational.
     *
     * @return A string representing this object.
     */

    public String toString()
    {
        return toString(true);
    }

    /**
     * Returns a string representation of this aprational.
     *
     * @param pretty <code>true</code> to use a fixed-point notation, <code>false</code> to use an exponential notation.
     *
     * @return A string representing this object.
     */

    public String toString(boolean pretty)
        throws ApfloatRuntimeException
    {
        return numerator().toString(pretty) +
               (denominator().equals(ONE) ? "" : '/' + denominator().toString(pretty));
    }

    /**
     * Write a string representation of this aprational to a <code>Writer</code>.
     *
     * @param out The output <code>Writer</code>.
     *
     * @exception java.io.IOException In case of I/O error writing to the stream.
     */

    public void writeTo(Writer out)
        throws IOException, ApfloatRuntimeException
    {
        writeTo(out, true);
    }

    /**
     * Write a string representation of this aprational to a <code>Writer</code>.
     *
     * @param out The output <code>Writer</code>.
     * @param pretty <code>true</code> to use a fixed-point notation, <code>false</code> to use an exponential notation.
     *
     * @exception java.io.IOException In case of I/O error writing to the stream.
     */

    public void writeTo(Writer out, boolean pretty)
        throws IOException, ApfloatRuntimeException
    {
        numerator().writeTo(out, pretty);
        if (!denominator().equals(ONE))
        {
            out.write('/');
            denominator().writeTo(out, pretty);
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
     */

    public void formatTo(Formatter formatter, int flags, int width, int precision)
    {
        if (denominator().equals(ONE))
        {
            numerator().formatTo(formatter, flags, width, precision);
        }
        else
        {
            if (width == -1)
            {
                numerator().formatTo(formatter, flags, width, precision);
                formatter.format("/");
                denominator().formatTo(formatter, flags, width, precision);
            }
            else
            {
                try
                {
                    Writer out = FormattingHelper.wrapAppendableWriter(formatter.out());
                    out = FormattingHelper.wrapPadWriter(out, (flags & LEFT_JUSTIFY) == LEFT_JUSTIFY);
                    formatter = new Formatter(out, formatter.locale());
                    numerator().formatTo(formatter, flags, -1, precision);
                    formatter.format("/");
                    denominator().formatTo(formatter, flags, -1, precision);
                    FormattingHelper.finishPad(out, width);
                }
                catch (IOException ioe)
                {
                    // Ignore as we can't propagate it; unfortunately we can't set it to the formattable either
                }
            }
        }
    }

    /**
     * Returns an <code>ApfloatImpl</code> representing the approximation of this
     * aprational up to the requested precision.<p>
     *
     * @param precision Precision of the <code>ApfloatImpl</code> that is needed.
     *
     * @return An <code>ApfloatImpl</code> representing this object to the requested precision.
     */

    protected ApfloatImpl getImpl(long precision)
        throws ApfloatRuntimeException
    {
        return ensureApprox(precision).getImpl(precision);
    }

    // Round away from zero i.e. opposite direction of rounding than in truncate()
    Apint roundAway()
        throws ApfloatRuntimeException
    {
        Apint[] div = ApintMath.div(numerator(), denominator());

        if (div[1].signum() == 0)
        {
            // No remainder from division; result is exact
            return div[0];
        }
        else
        {
            // Remainder from division; round away from zero
            return div[0].add(new Apint(signum(), div[0].radix()));
        }
    }

    Aprational scale(long scale)
    {
        return AprationalMath.scale(this, scale);
    }

    Aprational abs()
    {
        return AprationalMath.abs(this);
    }

    int compareToHalf()
    {
        return RoundingHelper.compareToHalf(this);
    }

    private void checkDenominator()
        throws IllegalArgumentException
    {
        if (this.denominator.signum() == 0)
        {
            throw new IllegalArgumentException("Denominator is zero");
        }
    }

    // Reduce the numerator and denominator to smallest possible terms and set the signs properly
    // NOTE: the method mutates this object, so it must only be called for newly constructed aprationals
    // Returns this, for convenience
    private Aprational reduce()
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        if (this.numerator.signum() == 0)
        {
            this.denominator = ONES[this.denominator.radix()];
        }
        else
        {
            if (!this.numerator.equals(ONE) && !this.denominator.equals(ONE))
            {
                if (this.numerator.radix() != this.denominator.radix())
                {
                    throw new IllegalArgumentException("Numerator and denominator must have the same radix");
                }

                Apint gcd = ApintMath.gcd(this.numerator, this.denominator);
                this.numerator = this.numerator.divide(gcd);
                this.denominator = this.denominator.divide(gcd);
            }

            int sign = this.numerator.signum() * this.denominator.signum();

            this.denominator = ApintMath.abs(this.denominator);

            if (sign != this.numerator.signum())
            {
                this.numerator = this.numerator.negate();
            }
        }

        return this;
    }

    private synchronized Apfloat ensureApprox(long precision)
        throws ApfloatRuntimeException
    {
        Apfloat approx = getApprox(precision);
        if (approx == null || approx.precision() < precision)
        {
            if (denominator().equals(ONE))
            {
                approx = numerator();
            }
            else
            {
                precision = Math.max(precision, 1);     // In case the requested precision would be zero

                if (denominator().isShort())
                {
                    approx = numerator().precision(precision).divide(denominator());
                    setApprox(approx);
                }
                else
                {
                    Apfloat inverseDen = getInverseDen();
                    inverseDen = ApfloatMath.inverseRoot(denominator(), 1, precision, inverseDen);
                    approx = numerator().multiply(inverseDen);
                    setApprox(approx);
                    setInverseDen(inverseDen);
                }
            }
        }

        return approx;
    }

    private Apfloat getApprox(long precision)
    {
        return (this.approx == null ? null : this.approx.get());
    }

    private void setApprox(Apfloat approx)
    {
        this.approx = new SoftReference<Apfloat>(approx);
    }

    private Apfloat getInverseDen()
    {
        return (this.inverseDen == null ? null : this.inverseDen.get());
    }

    private void setInverseDen(Apfloat inverseDen)
    {
        this.inverseDen = new SoftReference<Apfloat>(inverseDen);
    }

    private static final long serialVersionUID = -224128535732558313L;

    private static final long UNDEFINED = 0x8000000000000000L;

    private Apint numerator;
    private Apint denominator;
    private volatile long scale = UNDEFINED;
    private volatile long size = 0;
    private transient SoftReference<Apfloat> inverseDen = null;
    private transient SoftReference<Apfloat> approx = null;
}
