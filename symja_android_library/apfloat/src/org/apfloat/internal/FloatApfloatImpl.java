package org.apfloat.internal;

import java.io.ObjectInputStream;
import java.io.PushbackReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.OverflowException;
import org.apfloat.spi.ApfloatImpl;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;
import org.apfloat.spi.ConvolutionBuilder;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.Util;
import static org.apfloat.spi.RadixConstants.*;
import static org.apfloat.internal.FloatRadixConstants.*;

/**
 * Immutable apfloat implementation class for the
 * <code>float</code> data element type.<p>
 *
 * The associated {@link DataStorage} is assumed to be immutable also.
 * This way performance can be improved by sharing the data storage between
 * different <code>ApfloatImpl</code>'s and by only varying the
 * <code>ApfloatImpl</code> specific fields, like sign, precision and exponent.<p>
 *
 * This implementation doesn't necessarily store any extra digits for added
 * precision, so the last digit of any operation may be inaccurate.
 *
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class FloatApfloatImpl
    extends FloatBaseMath
    implements ApfloatImpl
{
    // Implementation notes:
    // - The dataStorage must never contain leading zeros or trailing zeros
    // - If precision is reduced then the dataStorage can contain trailing zeros (physically in the middle)
    // - The dataStorage should not be unnecessarily subsequenced if precision is reduced e.g. to allow autoconvolution
    // - Precision is in digits but exponent is in base units
    private FloatApfloatImpl(int sign, long precision, long exponent, DataStorage dataStorage, int radix)
    {
        super(radix);

        assert (sign == 0 || sign == -1 || sign == 1);
        assert (precision > 0);
        assert (sign != 0 || precision == Apfloat.INFINITE);
        assert (sign != 0 || exponent == 0);
        assert (sign != 0 || dataStorage == null);
        assert (sign == 0 || dataStorage != null);
        assert (exponent <= MAX_EXPONENT[radix] && exponent >= -MAX_EXPONENT[radix]);
        assert (dataStorage == null || dataStorage.isReadOnly());

        this.sign = sign;
        this.precision = precision;
        this.exponent = exponent;
        this.dataStorage = dataStorage;
        this.radix = radix;
    }

    /**
     * Create a new <code>FloatApfloatImpl</code> instance from a String.
     *
     * @param value The string to be parsed to a number.
     * @param precision The precision of the number (in digits of the radix).
     * @param radix The radix in which the number is created.
     * @param isInteger Specifies if the number to be parsed from the string is to be treated as an integer or not.
     *
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public FloatApfloatImpl(String value, long precision, int radix, boolean isInteger)
        throws NumberFormatException, ApfloatRuntimeException
    {
        super(checkRadix(radix));

        assert (precision == Apfloat.DEFAULT || precision > 0);

        this.radix = radix;

        // Default sign if not specified
        this.sign = 1;

        int startIndex = -1,
            pointIndex = -1,
            expIndex = -1,
            leadingZeros = 0,
            trailingZeros = 0,
            digitSize = 0;

        // Scan through the string looking for various things
        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);
            int digit = Character.digit(c, radix);

            // Note that checking for a valid digit takes place before checking for e or E in the string
            if (digit == -1)
            {
                if (i == 0 && (c == '-' || c == '+'))
                {
                    // Get sign
                    this.sign = (c == '-' ? -1 : 1);
                }
                else if (!isInteger && c == '.' && pointIndex == -1)
                {
                    // Mark decimal point location
                    pointIndex = digitSize;
                }
                else if (!isInteger && (c == 'e' || c == 'E') && expIndex == -1)
                {
                    // Mark index after which the exponent is specified
                    expIndex = i;
                    break;
                }
                else
                {
                    throw new NumberFormatException("Invalid character: " + c + " at position " + i);
                }
            }
            else
            {
                if (leadingZeros == digitSize && digit == 0)
                {
                    // Increase number of leading zeros
                    leadingZeros++;
                }
                else if (startIndex == -1)
                {
                    // Mark index where the significant digits start
                    startIndex = i;
                }

                // Increase number of digits
                digitSize++;

                if (digit == 0)
                {
                    // Increase number of trailing zeros
                    trailingZeros++;
                }
                else
                {
                    // Reset number of trailing zeros
                    trailingZeros = 0;
                }
            }
        }

        // Check if no digits were specified
        if (digitSize == 0)
        {
            throw new NumberFormatException("No digits");
        }

        // Check if this number is zero
        if (startIndex == -1)
        {
            this.sign = 0;
            this.precision = Apfloat.INFINITE;
            this.exponent = 0;
            this.dataStorage = null;

            return;
        }

        // Default precision is number of significant digits, if not specified
        if (precision == Apfloat.DEFAULT)
        {
            assert (!isInteger);
            precision = digitSize - leadingZeros;
        }
        this.precision = precision;

        // Size of integer part
        int integerSize = (pointIndex >= 0 ? pointIndex : digitSize) - leadingZeros;

        // Read exponent as specified in string
        if (expIndex >= 0)
        {
            // Thanks to Charles Oliver Nutter for finding this bug
            String expString = value.substring(expIndex + 1);
            if (expString.startsWith("+"))
            {
                expString = expString.substring(1);
            }

            try
            {
                this.exponent = Long.parseLong(expString);
            }
            catch (NumberFormatException nfe)
            {
                throw new NumberFormatException("Invalid exponent: " + expString);
            }
        }
        else
        {
            this.exponent = 0;
        }

        // Do not allow the exponent to be too close to the limits (MIN_VALUE, MAX_VALUE), leave some slack
        int slack = BASE_DIGITS[radix];

        // Check for overflow in exponent, roughly
        if (integerSize >= -slack && this.exponent >= Long.MAX_VALUE - integerSize - slack)
        {
            throw new NumberFormatException("Exponent overflow");
        }
        else if (integerSize <= slack && this.exponent <= Long.MIN_VALUE - integerSize + slack)
        {
            // Underflow
            this.sign = 0;
            this.precision = Apfloat.INFINITE;
            this.exponent = 0;
            this.dataStorage = null;

            return;
        }

        // Adjust exponent by decimal point location
        this.exponent += integerSize;

        // Exponent rounded towards positive infinity to base unit
        long baseExp = (this.exponent + (this.exponent > 0 ? BASE_DIGITS[radix] - 1 : 0)) / BASE_DIGITS[radix];

        // Check for overflow in exponent as represented in base units
        if (baseExp > MAX_EXPONENT[this.radix])
        {
            throw new OverflowException("Overflow");
        }
        else if (baseExp < -MAX_EXPONENT[this.radix])
        {
            // Underflow
            this.sign = 0;
            this.precision = Apfloat.INFINITE;
            this.exponent = 0;
            this.dataStorage = null;

            return;
        }

        // Leading zeros in first base unit
        int digitsInBase = (int) (baseExp * BASE_DIGITS[radix] - this.exponent);

        // The stored exponent is really the one per base unit
        this.exponent = baseExp;

        // Remove leading and trailing zeros from size
        digitSize -= leadingZeros + trailingZeros;

        // Limit number of significant digits by specified precision
        digitSize = (int) Math.min(digitSize, precision);

        // Needed storage size in floats
        int size = (int) getBasePrecision(digitSize, BASE_DIGITS[radix] - digitsInBase);

        this.dataStorage = createDataStorage(size);
        this.dataStorage.setSize(size);

        // Base unit that is constructed and stored to an element of the data storage
        float word = 0;

        DataStorage.Iterator iterator = this.dataStorage.iterator(DataStorage.WRITE, 0, size);

        // Set the data
        for (int i = startIndex; digitSize > 0; i++)
        {
            char c = value.charAt(i);
            if (c == '.')
            {
                continue;
            }

            int digit = Character.digit(c, radix);
            word *= (float) radix;
            word += (float) digit;

            if (digitSize == 1)
            {
                // Last digit
                while (digitsInBase < BASE_DIGITS[radix] - 1)
                {
                    // Fill last word with trailing zeros
                    word *= (float) radix;
                    digitsInBase++;
                }
            }

            if (++digitsInBase == BASE_DIGITS[radix])
            {
                // Word is full, write word
                digitsInBase = 0;
                iterator.setFloat(word);
                iterator.next();
                word = 0;
            }

            digitSize--;
        }

        assert (!iterator.hasNext());

        this.dataStorage.setReadOnly();
    }

    /**
     * Create a new <code>FloatApfloatImpl</code> instance from a <code>long</code>.
     *
     * @param value The value of the number.
     * @param precision The precision of the number (in digits of the radix).
     * @param radix The radix in which the number is created.
     *
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public FloatApfloatImpl(long value, long precision, int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        super(checkRadix(radix));

        assert (precision > 0);

        this.radix = radix;

        if (value > 0)
        {
            this.sign = 1;
            value = -value;         // Calculate here as negative to handle 0x8000000000000000
        }
        else if (value < 0)
        {
            this.sign = -1;
        }
        else
        {
            this.sign = 0;
            this.precision = Apfloat.INFINITE;
            this.exponent = 0;
            this.dataStorage = null;

            return;
        }

        this.precision = precision;

        int size;
        float[] data = new float[MAX_LONG_SIZE];
        long longBase = (long) BASE[radix];

        if (-longBase < value)
        {
            size = 1;                               // Nonzero
            data[MAX_LONG_SIZE - 1] = (float) -value;
        }
        else
        {
            for (size = 0; value != 0; size++)
            {
                long newValue = value / longBase;
                data[MAX_LONG_SIZE - 1 - size] = (float) (newValue * longBase - value);   // Negated here
                value = newValue;
            }
        }

        this.exponent = size;

        // Check if precision in floats is less than size; truncate size if so
        long basePrecision = getBasePrecision(precision, getDigits(data[MAX_LONG_SIZE - size]));
        if (basePrecision < size)
        {
            size = (int) basePrecision;
        }

        // Remove trailing zeros from data
        while (data[MAX_LONG_SIZE - 1 - (int) this.exponent + size] == 0)
        {
            size--;
        }

        this.dataStorage = createDataStorage(size);
        this.dataStorage.setSize(size);

        ArrayAccess arrayAccess = this.dataStorage.getArray(DataStorage.WRITE, 0, size);
        System.arraycopy(data, MAX_LONG_SIZE - (int) this.exponent, arrayAccess.getData(), arrayAccess.getOffset(), size);
        arrayAccess.close();

        this.dataStorage.setReadOnly();
    }

    /**
     * Create a new <code>FloatApfloatImpl</code> instance from a <code>double</code>.
     *
     * @param value The value of the number.
     * @param precision The precision of the number (in digits of the radix).
     * @param radix The radix in which the number is created.
     *
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public FloatApfloatImpl(double value, long precision, int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        super(checkRadix(radix));

        if (Double.isInfinite(value) || Double.isNaN(value))
        {
            throw new NumberFormatException(value + " is not a valid number");
        }

        this.radix = radix;

        if (value > 0)
        {
            this.sign = 1;
        }
        else if (value < 0)
        {
            this.sign = -1;
            value = -value;
        }
        else
        {
            this.sign = 0;
            this.precision = Apfloat.INFINITE;
            this.exponent = 0;
            this.dataStorage = null;

            return;
        }

        this.precision = precision;

        int size;
        float[] data = new float[MAX_DOUBLE_SIZE];
        double doubleBase = (double) BASE[radix];

        this.exponent = (long) Math.floor(Math.log(value) / Math.log(doubleBase));
        // Avoid overflow in intermediate value
        if (this.exponent > 0)
        {
            value *= Math.pow(doubleBase, (double) -this.exponent);
        }
        else if (this.exponent < 0)
        {
            value *= Math.pow(doubleBase, (double) (-this.exponent - MAX_DOUBLE_SIZE));
            value *= Math.pow(doubleBase, (double) MAX_DOUBLE_SIZE);
        }
        this.exponent++;

        if (value < 1.0)
        {
            // Round-off error in case the input was very close but just under the base, e.g. 9.999999999999996E-10
            value = 1.0;
        }

        for (size = 0; size < MAX_DOUBLE_SIZE && value > 0.0; size++)
        {
            double tmp = Math.floor(value);

            assert (tmp <= doubleBase);

            if (tmp == doubleBase)
            {
                // Round-off error e.g. in case of the number being exactly 1/radix
                tmp -= 1.0;
            }

            data[size] = (float) tmp;
            value -= tmp;
            value *= doubleBase;
        }

        // Check if precision in floats is less than size; truncate size if so
        long basePrecision = getBasePrecision(precision, getDigits(data[0]));
        if (basePrecision < size)
        {
            size = (int) basePrecision;
        }

        // Remove trailing zeros from data
        while (data[size - 1] == 0)
        {
            size--;
        }

        this.dataStorage = createDataStorage(size);
        this.dataStorage.setSize(size);

        ArrayAccess arrayAccess = this.dataStorage.getArray(DataStorage.WRITE, 0, size);
        System.arraycopy(data, 0, arrayAccess.getData(), arrayAccess.getOffset(), size);
        arrayAccess.close();

        this.dataStorage.setReadOnly();
    }

    private static long readExponent(PushbackReader in)
        throws IOException, NumberFormatException
    {
        StringBuilder buffer = new StringBuilder(20);
        int input;

        for (long i = 0; (input = in.read()) != -1; i++)
        {
            char c = (char) input;
            int digit = Character.digit(c, 10);         // Exponent is always in base 10

            if (i == 0 && c == '-' ||
                digit != -1)
            {
                buffer.append(c);
            }
            else
            {
                // Stop at first invalid character and put it back
                in.unread(input);
                break;
            }
        }

        return Long.parseLong(buffer.toString());
    }

    /**
     * Create a new <code>FloatApfloatImpl</code> instance reading from a stream.<p>
     *
     * Implementation note: this constructor calls the <code>in</code> stream's
     * single-character <code>read()</code> method. If the underlying stream doesn't
     * explicitly implement this method in some efficient way, but simply inherits it
     * from the <code>Reader</code> base class, performance will suffer as the default
     * <code>Reader</code> method creates a <code>new char[1]</code> on every call to
     * <code>read()</code>.
     *
     * @param in The stream to read from.
     * @param precision The precision of the number (in digits of the radix).
     * @param radix The radix in which the number is created.
     * @param isInteger Specifies if the number to be parsed from the stream is to be treated as an integer or not.
     *
     * @exception java.io.IOException If an I/O error occurs accessing the stream.
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public FloatApfloatImpl(PushbackReader in, long precision, int radix, boolean isInteger)
        throws IOException, NumberFormatException, ApfloatRuntimeException
    {
        super(checkRadix(radix));

        assert (precision == Apfloat.DEFAULT || precision > 0);

        this.radix = radix;

        // Default sign if not specified
        this.sign = 1;

        // Allocate a maximum memory block, since we don't know how much data to expect
        ApfloatContext ctx = ApfloatContext.getContext();
        long initialSize = ctx.getMemoryThreshold() / 4,
             previousAllocatedSize = 0,
             allocatedSize = initialSize;
        this.dataStorage = createDataStorage(initialSize);
        this.dataStorage.setSize(initialSize);

        // Base unit that is constructed and stored to an element of the data storage
        float word = 0;

        // Number of digits stored in word
        int digitsInBase = 0;

        DataStorage.Iterator iterator = this.dataStorage.iterator(DataStorage.WRITE, previousAllocatedSize, allocatedSize);

        int input;
        long actualSize = 0,
             startIndex = -1,
             pointIndex = -1,
             leadingZeros = 0,
             trailingZeros = 0,
             digitSize = 0;

        // Scan through the string looking for various things
        for (long i = 0; (input = in.read()) != -1; i++)
        {
            char c = (char) input;
            int digit = Character.digit(c, radix);

            // Note that checking for a valid digit takes place before checking for e or E in the string
            if (digit == -1)
            {
                if (i == 0 && (c == '-' || c == '+'))
                {
                    // Get sign
                    this.sign = (c == '-' ? -1 : 1);
                }
                else if (!isInteger && c == '.' && pointIndex == -1)
                {
                    // Mark decimal point location
                    pointIndex = digitSize;
                }
                else if (!isInteger && digitSize > 0 && (c == 'e' || c == 'E'))
                {
                    // Read the exponent and stop
                    this.exponent = readExponent(in);
                    break;
                }
                else
                {
                    // Stop at first invalid character and put it back
                    in.unread(input);
                    break;
                }
            }
            else
            {
                if (leadingZeros == digitSize && digit == 0)
                {
                    // Increase number of leading zeros
                    leadingZeros++;
                }
                else
                {
                    if (startIndex == -1)
                    {
                        // Mark index where the significant digits start
                        startIndex = i;
                    }

                    // Set the data
                    word *= (float) radix;
                    word += (float) digit;

                    // Reallocate storage if needed; done here to prepare storing last (partial) word
                    if (actualSize == allocatedSize)
                    {
                        if (actualSize == initialSize)
                        {
                            // Maximum memory block size exceeded; prepare to allocate anything
                            DataStorage dataStorage = createDataStorage(Long.MAX_VALUE / 4);
                            dataStorage.copyFrom(this.dataStorage, actualSize);
                            this.dataStorage = dataStorage;
                        }
                        previousAllocatedSize = allocatedSize;
                        allocatedSize += getBlockSize();
                        this.dataStorage.setSize(allocatedSize);
                        iterator.close();
                        iterator = this.dataStorage.iterator(DataStorage.WRITE, previousAllocatedSize, allocatedSize);
                    }

                    if (++digitsInBase == BASE_DIGITS[radix])
                    {
                        // Word is full, write word
                        digitsInBase = 0;
                        iterator.setFloat(word);
                        iterator.next();
                        word = 0;
                        actualSize++;
                    }
                }

                // Increase number of digits
                digitSize++;

                if (digit == 0)
                {
                    // Increase number of trailing zeros
                    trailingZeros++;
                }
                else
                {
                    // Reset number of trailing zeros
                    trailingZeros = 0;
                }
            }
        }

        // Check if no digits were specified
        if (digitSize == 0)
        {
            throw new NumberFormatException("No digits");
        }

        // Check if this number is zero
        if (startIndex == -1)
        {
            this.sign = 0;
            this.precision = Apfloat.INFINITE;
            this.exponent = 0;
            this.dataStorage = null;

            return;
        }

        // Handle last word
        if (digitsInBase > 0 && word != 0)
        {
            // Last digit
            while (digitsInBase < BASE_DIGITS[radix])
            {
                // Fill last word with trailing zeros
                word *= (float) radix;
                digitsInBase++;
            }

            // Write word
            iterator.setFloat(word);
            actualSize++;
        }

        iterator.close();

        // Default precision is number of significant digits, if not specified
        if (precision == Apfloat.DEFAULT)
        {
            assert (!isInteger);
            precision = digitSize - leadingZeros;
        }
        this.precision = precision;

        // Size of integer part
        long integerSize = (pointIndex >= 0 ? pointIndex : digitSize) - leadingZeros;

        // Do not allow the exponent to be too close to the limits (MIN_VALUE, MAX_VALUE), leave some slack
        int slack = BASE_DIGITS[radix];

        // Check for overflow in exponent, roughly
        if (integerSize >= -slack && this.exponent >= Long.MAX_VALUE - integerSize - slack)
        {
            throw new NumberFormatException("Exponent overflow");
        }
        else if (integerSize <= slack && this.exponent <= Long.MIN_VALUE - integerSize + slack)
        {
            // Underflow
            this.sign = 0;
            this.precision = Apfloat.INFINITE;
            this.exponent = 0;
            this.dataStorage = null;

            return;
        }

        // Adjust exponent by decimal point location
        this.exponent += integerSize;

        // Exponent rounded towards negative infinity to base unit
        long baseExp = (this.exponent - (this.exponent < 0 ? BASE_DIGITS[radix] - 1 : 0)) / BASE_DIGITS[radix];

        // Check for overflow in exponent as represented in base units
        if (baseExp > MAX_EXPONENT[this.radix])
        {
            throw new OverflowException("Overflow");
        }
        else if (baseExp < -MAX_EXPONENT[this.radix])
        {
            // Underflow
            this.sign = 0;
            this.precision = Apfloat.INFINITE;
            this.exponent = 0;
            this.dataStorage = null;

            return;
        }

        // How much the data needs to be shifted
        int bias = (int) (this.exponent - baseExp * BASE_DIGITS[radix]);

        // The stored exponent is really the one per base unit
        this.exponent = baseExp;

        // Remove leading and trailing zeros from size
        digitSize -= leadingZeros + trailingZeros;

        // Limit number of significant digits by specified precision
        digitSize = Math.min(digitSize, precision);

        // Needed storage size in floats
        actualSize = (digitSize + BASE_DIGITS[radix] - 1) / BASE_DIGITS[radix];

        // Truncate allocated space to actually used amount
        this.dataStorage.setSize(actualSize);

        this.dataStorage.setReadOnly();

        if (bias != 0)
        {
            // Shift by bias
            long factor = 1;

            for (int i = 0; i < bias; i++)
            {
                factor *= radix;
            }

            FloatApfloatImpl tmp = (FloatApfloatImpl) multiply(new FloatApfloatImpl(factor, Apfloat.INFINITE, radix));

            this.exponent = tmp.exponent;
            this.dataStorage = tmp.dataStorage;
            this.initialDigits = UNDEFINED;     // Needs to be reset
        }
    }

    // Returns number of trailing zeros before specified index
    private static long getTrailingZeros(DataStorage dataStorage, long index)
        throws ApfloatRuntimeException
    {
        long count = 0;

        DataStorage.Iterator iterator = dataStorage.iterator(DataStorage.READ, index, 0);

        while (iterator.hasNext())
        {
            if (iterator.getFloat() != 0)
            {
                iterator.close();
                break;
            }

            iterator.next();
            count++;
        }

        return count;
    }

    // Returns number of leading zeros starting from specified index
    private static long getLeadingZeros(DataStorage dataStorage, long index)
        throws ApfloatRuntimeException
    {
        long count = 0;

        DataStorage.Iterator iterator = dataStorage.iterator(DataStorage.READ, index, dataStorage.getSize());

        while (iterator.hasNext())
        {
            if (iterator.getFloat() != 0)
            {
                iterator.close();
                break;
            }

            iterator.next();
            count++;
        }

        return count;
    }

    public ApfloatImpl addOrSubtract(ApfloatImpl x, boolean subtract)
        throws ApfloatRuntimeException
    {
        if (!(x instanceof FloatApfloatImpl))
        {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }

        FloatApfloatImpl that = (FloatApfloatImpl) x;

        if (this.radix != that.radix)
        {
            throw new RadixMismatchException("Cannot use numbers with different radixes: " + this.radix + " and " + that.radix);
        }

        assert (this.sign != 0);
        assert (that.sign != 0);

        int realThatSign = (subtract ? -that.sign : that.sign);
        boolean reallySubtract = (this.sign != realThatSign);

        ApfloatContext ctx = ApfloatContext.getContext();
        AdditionBuilder<Float> additionBuilder = ctx.getBuilderFactory().getAdditionBuilder(Float.TYPE);
        AdditionStrategy<Float> additionStrategy = additionBuilder.createAddition(this.radix);

        int sign;
        long exponent,
             precision;
        DataStorage dataStorage;

        if (this == that)
        {
            if (reallySubtract)
            {
                // x - x = 0

                return zero();
            }
            else
            {
                // x + x = 2 * x

                sign = this.sign;
                exponent = this.exponent;
                precision = this.precision;
                long size = getSize() + 1;

                dataStorage = createDataStorage(size);
                dataStorage.setSize(size);

                DataStorage.Iterator src1 = this.dataStorage.iterator(DataStorage.READ, size - 1, 0),
                                     src2 = this.dataStorage.iterator(DataStorage.READ, size - 1, 0),   // Sub-optimal: could be the same
                                     dst = dataStorage.iterator(DataStorage.WRITE, size, 0);

                float carry = additionStrategy.add(src1, src2, (float) 0, dst, size - 1);

                dst.setFloat(carry);
                dst.close();

                size -= getTrailingZeros(dataStorage, size);

                // Check if carry occurred
                int carrySize = (int) carry,
                    leadingZeros = 1 - carrySize;

                dataStorage = dataStorage.subsequence(leadingZeros, size - leadingZeros);
                exponent += carrySize;

                if (this.exponent == MAX_EXPONENT[this.radix] && carrySize > 0)
                {
                    throw new OverflowException("Overflow");
                }

                if (precision != Apfloat.INFINITE &&
                    (carrySize > 0 || getInitialDigits(dataStorage) > getInitialDigits()))
                {
                    // Carry overflow for most significant digit; number of significant digits increases by one
                    precision++;
                }
            }
        }
        else
        {
            // Now this != that

            int comparison;
            if (scale() > that.scale())
            {
                comparison = 1;
            }
            else if (scale() < that.scale())
            {
                comparison = -1;
            }
            else if (reallySubtract)
            {
                comparison = compareMantissaTo(that);           // Might be sub-optimal, but a more efficient algorithm would be complicated
            }
            else
            {
                comparison = 1;                                 // Add equally big numbers; arbitrarily choose one
            }

            FloatApfloatImpl big,
                               small;

            if (comparison > 0)
            {
                big = this;
                small = that;
                sign = this.sign;
            }
            else if (comparison < 0)
            {
                big = that;
                small = this;
                sign = realThatSign;
            }
            else
            {
                // x - x = 0
                return zero();
            }

            long scaleDifference = big.scale() - small.scale(),
                 exponentDifference,
                 size,
                 bigSize,
                 smallSize;

            if (scaleDifference < 0)
            {
                // Small number is completely insignificantly small compared to big
                precision = big.precision;
                exponent = big.exponent;
                bigSize = big.getSize();
                smallSize = 0;
                size = bigSize;
                exponentDifference = bigSize;
            }
            else
            {
                precision = Math.min(big.precision, Util.ifFinite(small.precision, scaleDifference + small.precision)); // Detects overflow also
                long basePrecision = Math.min(MAX_EXPONENT[this.radix], getBasePrecision(precision, big.getInitialDigits()));
                exponent = big.exponent;

                exponentDifference = big.exponent - small.exponent;
                size = Math.min(basePrecision, Math.max(big.getSize(), exponentDifference + small.getSize()));
                bigSize = Math.min(size, big.getSize());
                smallSize = Math.max(0, Math.min(size - exponentDifference, small.getSize()));
            }

            long dstSize = size + 1;                    // One extra word for carry overflow
            dataStorage = createDataStorage(dstSize);
            dataStorage.setSize(dstSize);

            DataStorage.Iterator src1 = big.dataStorage.iterator(DataStorage.READ, bigSize, 0),
                                 src2 = small.dataStorage.iterator(DataStorage.READ, smallSize, 0),
                                 dst = dataStorage.iterator(DataStorage.WRITE, dstSize, 0);

            float carry = 0;

            // big:       XXXXXXXX               XXXX
            // small:         XXXXXXXX        or         XXXX
            // This part:         XXXX                   XXXX
            if (size > bigSize)
            {
                long blockSize = Math.min(size - bigSize, smallSize);
                if (reallySubtract)
                {
                    carry = additionStrategy.subtract(null, src2, carry, dst, blockSize);
                }
                else
                {
                    carry = additionStrategy.add(null, src2, carry, dst, blockSize);
                }
            }
            // big:        XXXXXXXXXXXX
            // small:          XXXX
            // This part:          XXXX
            else if (size > exponentDifference + smallSize)
            {
                long blockSize = size - exponentDifference - smallSize;
                if (reallySubtract)
                {
                    carry = additionStrategy.subtract(src1, null, carry, dst, blockSize);
                }
                else
                {
                    carry = additionStrategy.add(src1, null, carry, dst, blockSize);
                }
            }
            // big:        XXXX
            // small:              XXXX
            // This part:      XXXX
            if (exponentDifference > bigSize)
            {
                long blockSize = exponentDifference - bigSize;
                if (reallySubtract)
                {
                    carry = additionStrategy.subtract(null, null, carry, dst, blockSize);
                }
                else
                {
                    carry = additionStrategy.add(null, null, carry, dst, blockSize);
                }
            }
            // big:        XXXXXXXX               XXXXXXXXXXXX
            // small:          XXXXXXXX        or     XXXX
            // This part:      XXXX                   XXXX
            else if (bigSize > exponentDifference)
            {
                long blockSize = Math.min(bigSize - exponentDifference, smallSize);
                if (reallySubtract)
                {
                    carry = additionStrategy.subtract(src1, src2, carry, dst, blockSize);
                }
                else
                {
                    carry = additionStrategy.add(src1, src2, carry, dst, blockSize);
                }
            }
            // big:        XXXXXXXX               XXXXXXXXXXXX           XXXX
            // small:          XXXXXXXX        or     XXXX            or         XXXX
            // This part:  XXXX                   XXXX                   XXXX
            if (exponentDifference > 0)
            {
                long blockSize = Math.min(bigSize, exponentDifference);
                if (reallySubtract)
                {
                    carry = additionStrategy.subtract(src1, null, carry, dst, blockSize);
                }
                else
                {
                    carry = additionStrategy.add(src1, null, carry, dst, blockSize);
                }
            }

            // Set most significant word
            dst.setFloat(carry);
            dst.close();

            long leadingZeros;

            if (reallySubtract)
            {
                // Get denormalization
                leadingZeros = getLeadingZeros(dataStorage, 0);

                assert (leadingZeros <= size);
            }
            else
            {
                // Check if carry occurred up to and including most significant word
                leadingZeros = (carry == 0 ? 1 : 0);

                if (this.exponent == MAX_EXPONENT[this.radix] && leadingZeros == 0)
                {
                    throw new OverflowException("Overflow");
                }
            }

            dstSize -= getTrailingZeros(dataStorage, dstSize);

            dataStorage = dataStorage.subsequence(leadingZeros, dstSize - leadingZeros);
            exponent += 1 - leadingZeros;

            if (exponent < -MAX_EXPONENT[this.radix])
            {
                // Underflow
                return zero();
            }

            if (precision != Apfloat.INFINITE)
            {
                // If scale of number changes, the number of significant digits changes accordingly
                long scaleChange = (1 - leadingZeros) * BASE_DIGITS[this.radix] + getInitialDigits(dataStorage) - big.getInitialDigits();
                if (-scaleChange >= precision)
                {
                    // All significant digits were lost anyway, due to trailing garbage digits
                    return zero();
                }
                precision += scaleChange;
                precision = (precision <= 0 ? Apfloat.INFINITE : precision);    // Detect overflow
            }
        }

        dataStorage.setReadOnly();

        return new FloatApfloatImpl(sign, precision, exponent, dataStorage, this.radix);
    }

    public ApfloatImpl multiply(ApfloatImpl x)
        throws ApfloatRuntimeException
    {
        if (!(x instanceof FloatApfloatImpl))
        {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }

        FloatApfloatImpl that = (FloatApfloatImpl) x;

        if (this.radix != that.radix)
        {
            throw new RadixMismatchException("Cannot multiply numbers with different radixes: " + this.radix + " and " + that.radix);
        }

        int sign = this.sign * that.sign;

        if (sign == 0)
        {
            return zero();
        }

        long exponent = this.exponent + that.exponent;

        if (exponent > MAX_EXPONENT[this.radix])
        {
            throw new OverflowException("Overflow");
        }
        else if (exponent < -MAX_EXPONENT[this.radix])
        {
            // Underflow
            return zero();
        }

        long precision = Math.min(this.precision, that.precision),
             basePrecision = getBasePrecision(precision, 0),            // Round up
             thisSize = getSize(),
             thatSize = that.getSize(),
             size = Math.min(Util.ifFinite(basePrecision, basePrecision + 1), thisSize + thatSize),     // Reserve one extra word for carry
             thisDataSize = Math.min(thisSize, basePrecision),
             thatDataSize = Math.min(thatSize, basePrecision);

        DataStorage thisDataStorage = this.dataStorage.subsequence(0, thisDataSize),
                    thatDataStorage = (this.dataStorage == that.dataStorage ?
                                       thisDataStorage :                                                // Enable auto-convolution
                                       that.dataStorage.subsequence(0, thatDataSize));

        ApfloatContext ctx = ApfloatContext.getContext();
        ConvolutionBuilder convolutionBuilder = ctx.getBuilderFactory().getConvolutionBuilder();
        ConvolutionStrategy convolutionStrategy = convolutionBuilder.createConvolution(this.radix, thisDataSize, thatDataSize, size);

        // Possibly sub-optimal: could look up trailing zeros of the subsequences
        DataStorage dataStorage = convolutionStrategy.convolute(thisDataStorage, thatDataStorage, size);

        // Check if carry occurred up to and including most significant word
        int leadingZeros = (getMostSignificantWord(dataStorage) == 0 ? 1 : 0);

        exponent -= leadingZeros;

        if (exponent < -MAX_EXPONENT[this.radix])
        {
            // Underflow
            return zero();
        }

        size -= leadingZeros;
        dataStorage = dataStorage.subsequence(leadingZeros, size);

        size = Math.min(size, getBasePrecision(precision, getInitialDigits(dataStorage)));
        size -= getTrailingZeros(dataStorage, size);

        dataStorage = dataStorage.subsequence(0, size);

        dataStorage.setReadOnly();

        return new FloatApfloatImpl(sign, precision, exponent, dataStorage, this.radix);
    }

    public boolean isShort()
        throws ApfloatRuntimeException
    {
        return (this.sign == 0 || getSize() == 1);
    }

    public ApfloatImpl divideShort(ApfloatImpl x)
        throws ApfloatRuntimeException
    {
        if (!(x instanceof FloatApfloatImpl))
        {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }

        FloatApfloatImpl that = (FloatApfloatImpl) x;

        if (this.radix != that.radix)
        {
            throw new RadixMismatchException("Cannot divide numbers with different radixes: " + this.radix + " and " + that.radix);
        }

        assert (this.sign != 0);
        assert (that.sign != 0);

        int sign = this.sign * that.sign;

        long exponent = this.exponent - that.exponent + 1;

        if (exponent > MAX_EXPONENT[this.radix])
        {
            throw new OverflowException("Overflow");
        }
        else if (exponent < -MAX_EXPONENT[this.radix])
        {
            // Underflow
            return zero();
        }

        long precision = Math.min(this.precision, that.precision),
             basePrecision = getBasePrecision(),
             thisDataSize = Math.min(getSize(), basePrecision);

        DataStorage dataStorage;

        float divisor = getMostSignificantWord(that.dataStorage);

        if (divisor == (float) 1)
        {
            long size = thisDataSize - getTrailingZeros(this.dataStorage, thisDataSize);

            dataStorage = this.dataStorage.subsequence(0, size);
        }
        else
        {
            ApfloatContext ctx = ApfloatContext.getContext();
            AdditionBuilder<Float> additionBuilder = ctx.getBuilderFactory().getAdditionBuilder(Float.TYPE);
            AdditionStrategy<Float> additionStrategy = additionBuilder.createAddition(this.radix);

            long size;
            float carry;

            // Check for finite or infinite result sequence
            float dividend = divisor;

            // Check that the factorization of the divisor consists entirely of factors of the base
            // E.g. if base is 10=2*5 then the divisor should be 2^n*5^m
            for (int i = 0; i < RADIX_FACTORS[this.radix].length; i++)
            {
                float factor = RADIX_FACTORS[this.radix][i],
                        quotient;

                // Keep dividing by factor as long as dividend % factor == 0
                // that is remove factors of the base from the divisor
                while ((dividend - factor * (quotient = (float) (long) (dividend / factor))) == 0)
                {
                    dividend = quotient;
                }
            }

            // Check if the divisor was factored all the way to one by just dividing by factors of the base
            if (dividend != (float) 1)
            {
                // Divisor does not contain only factors of the base; infinite nonzero sequence

                if (basePrecision == Apfloat.INFINITE)
                {
                    throw new InfiniteExpansionException("Cannot perform inexact division to infinite precision");
                }

                size = basePrecision;
            }
            else
            {
                // Divisor contains only factors of the base; calculate maximum sequence length
                carry = (float) 1;
                DataStorage.Iterator dummy = new DataStorage.Iterator()
                {
                    public void setFloat(float value) {}
                    public void next() {}
                    private static final long serialVersionUID = 1L;
                };
                long sequenceSize;
                for (sequenceSize = 0; carry != 0; sequenceSize++)
                {
                    carry = additionStrategy.divide(null, divisor, carry, dummy, 1);
                }

                size = Math.min(basePrecision, thisDataSize + sequenceSize);
            }

            // One extra word for result in case the initial word becomes zero; to avoid loss of precision
            size++;

            dataStorage = createDataStorage(size);
            dataStorage.setSize(size);

            DataStorage.Iterator src = this.dataStorage.iterator(DataStorage.READ, 0, thisDataSize),
                                 dst = dataStorage.iterator(DataStorage.WRITE, 0, size);

            // Perform actual division
            carry = additionStrategy.divide(src, divisor, (float) 0, dst, thisDataSize);

            // Produce the trailing sequence of digits due to inexact division
            carry = additionStrategy.divide(null, divisor, carry, dst, size - thisDataSize);

            size -= getTrailingZeros(dataStorage, size);

            // Check if initial word of result is zero
            int leadingZeros = (getMostSignificantWord() < divisor ? 1 : 0);

            dataStorage = dataStorage.subsequence(leadingZeros, size - leadingZeros);
            exponent -= leadingZeros;

            if (exponent < -MAX_EXPONENT[this.radix])
            {
                // Underflow
                return zero();
            }

            dataStorage.setReadOnly();
        }

        return new FloatApfloatImpl(sign, precision, exponent, dataStorage, this.radix);
    }

    public ApfloatImpl absFloor()
        throws ApfloatRuntimeException
    {
        if (this.sign == 0 ||
            this.exponent >= this.dataStorage.getSize())        // Is integer already, with no extra hidden trailing digits
        {
            return precision(Apfloat.INFINITE);
        }
        else if (this.exponent <= 0)                            // Is less than one in absolute value
        {
            return zero();
        }

        long size = this.exponent;                              // Size of integer part, now that this.dataStorage.getSize() > this.exponent
        size -= getTrailingZeros(this.dataStorage, size);

        DataStorage dataStorage = this.dataStorage.subsequence(0, size);

        ApfloatImpl apfloatImpl = new FloatApfloatImpl(this.sign, Apfloat.INFINITE, this.exponent, dataStorage, this.radix);

        return apfloatImpl;
    }

    public ApfloatImpl absCeil()
        throws ApfloatRuntimeException
    {
        if (this.sign == 0)
        {
            return this;
        }

        long exponent;
        DataStorage dataStorage;
        DataStorage.Iterator iterator = null;

        if (this.exponent <= 0)
        {
            // Number is < 1 but > 0; result is one
            int size = 1;
            dataStorage = createDataStorage(size);
            dataStorage.setSize(size);
            ArrayAccess arrayAccess = dataStorage.getArray(DataStorage.WRITE, 0, size);
            arrayAccess.getFloatData()[arrayAccess.getOffset()] = (float) 1;
            arrayAccess.close();

            exponent = 1;
        }
        else if (getSize() <= this.exponent ||          // Check if the fractional part is nonzero
                 findMismatch(iterator = getZeroPaddedIterator(this.exponent, getSize()), ZERO_ITERATOR, getSize() - this.exponent) < 0)
        {
            // Fractional part is zero; the result is the number itself (to infinite precision)
            long size = Math.min(this.dataStorage.getSize(), this.exponent);
            size -= getTrailingZeros(this.dataStorage, size);
            dataStorage = this.dataStorage.subsequence(0, size);        // Ensure truncation

            exponent = this.exponent;
        }
        else
        {
            // Fractional part is nonzero; round up

            ApfloatContext ctx = ApfloatContext.getContext();
            AdditionBuilder<Float> additionBuilder = ctx.getBuilderFactory().getAdditionBuilder(Float.TYPE);
            AdditionStrategy<Float> additionStrategy = additionBuilder.createAddition(this.radix);

            long size = this.exponent;                  // Size of integer part
            dataStorage = createDataStorage(size + 1);     // Reserve room for carry overflow
            dataStorage.setSize(size + 1);
            DataStorage.Iterator src = this.dataStorage.iterator(DataStorage.READ, size, 0),
                                 dst = dataStorage.iterator(DataStorage.WRITE, size + 1, 0);
            float carry = additionStrategy.add(src, null, (float) 1, dst, size);     // Add carry
            dst.setFloat(carry);                      // Set leading float as overflow carry
            src.close();
            dst.close();
            int carrySize = (int) carry;                // For adjusting size, if carry did overflow or not
            size -= getTrailingZeros(dataStorage, size + 1);
            dataStorage = dataStorage.subsequence(1 - carrySize, size + carrySize);

            exponent = this.exponent + carrySize;
        }

        if (iterator != null)
        {
            iterator.close();
        }

        dataStorage.setReadOnly();

        ApfloatImpl apfloatImpl = new FloatApfloatImpl(this.sign, Apfloat.INFINITE, exponent, dataStorage, this.radix);

        return apfloatImpl;
    }

    public ApfloatImpl frac()
        throws ApfloatRuntimeException
    {
        if (this.sign == 0 ||
            this.exponent <= 0)                                 // Is less than one in absolute value already
        {
            return this;
        }
        if (this.exponent >= getSize())                         // Is an integer, fractional part is zero
        {
            return zero();
        }

        long size = this.dataStorage.getSize() - this.exponent; // Size of fractional part, now that getSize() > this.exponent
        long leadingZeros = getLeadingZeros(this.dataStorage, this.exponent);
        if (this.exponent + leadingZeros >= getSize())
        {
            // All significant digits were lost, only trailing garbage digits
            return zero();
        }

        DataStorage dataStorage = this.dataStorage.subsequence(this.exponent + leadingZeros, size - leadingZeros);

        long precision;
        if (this.precision != Apfloat.INFINITE)
        {
            // Precision is reduced as the integer part is omitted, plus any leading zeros
            precision = this.precision - getInitialDigits() - (this.exponent + leadingZeros) * BASE_DIGITS[this.radix] + getInitialDigits(dataStorage);
            if (precision <= 0)
            {
                // All significant digits were lost anyway, only trailing garbage digits
                return zero();
            }
        }
        else
        {
            precision = Apfloat.INFINITE;
        }

        long exponent = -leadingZeros;

        ApfloatImpl apfloatImpl = new FloatApfloatImpl(this.sign, precision, exponent, dataStorage, this.radix);

        return apfloatImpl;
    }

    private ApfloatImpl zero()
    {
        return new FloatApfloatImpl(0, Apfloat.INFINITE, 0, null, this.radix);
    }

    public int radix()
    {
        return this.radix;
    }

    public long precision()
    {
        return this.precision;
    }

    public long size()
        throws ApfloatRuntimeException
    {
        assert (this.dataStorage != null);

        if (this.size == 0)
        {
            // Writes and reads of volatile long values are always atomic so multiple threads can read and write this at the same time
            this.size = getInitialDigits() + (getSize() - 1) * BASE_DIGITS[this.radix] - getLeastZeros();
        }

        return this.size;
    }

    // Get number of trailing zeros
    private long getLeastZeros()
        throws ApfloatRuntimeException
    {
        if (this.leastZeros == UNDEFINED)
        {
            // Cache the value
            // NOTE: This is not synchronized; it's OK if multiple threads set this at the same time
            // Writes and reads of volatile long values are always atomic so multiple threads can read and write this at the same time
            long index = getSize() - 1;
            float word = getWord(index);
            word = getLeastSignificantWord(index, word);

            long leastZeros = 0;
            if (word == 0)
            {
                // Usually the last word is nonzero but in case precision was later changed, it might be zero
                long trailingZeros = getTrailingZeros(this.dataStorage, index) + 1;
                index -= trailingZeros;
                word = getWord(index);
                word = getLeastSignificantWord(index, word);

                leastZeros += trailingZeros * BASE_DIGITS[this.radix];
            }

            assert (word != 0);

            while (word % this.radix == 0)
            {
                leastZeros++;
                word /= this.radix;
            }
            this.leastZeros = leastZeros;
        }

        return this.leastZeros;
    }

    public ApfloatImpl precision(long precision)
    {
        if (this.sign == 0 || precision == this.precision)
        {
            return this;
        }
        else
        {
            return new FloatApfloatImpl(this.sign, precision, this.exponent, this.dataStorage, this.radix);
        }
    }

    public long scale()
        throws ApfloatRuntimeException
    {
        assert (this.dataStorage != null);

        return (this.exponent - 1) * BASE_DIGITS[this.radix] + getInitialDigits();
    }

    public int signum()
    {
        return this.sign;
    }

    public ApfloatImpl negate()
        throws ApfloatRuntimeException
    {
        return new FloatApfloatImpl(-this.sign, this.precision, this.exponent, this.dataStorage, this.radix);
    }

    public double doubleValue()
    {
        if (this.sign == 0)
        {
            return 0.0;
        }

        double value = 0.0,
               doubleBase = (double) BASE[this.radix];

        int size = (int) Math.min(MAX_DOUBLE_SIZE, getSize());

        DataStorage.Iterator iterator = this.dataStorage.iterator(DataStorage.READ, size, 0);

        while (iterator.hasNext())
        {
            value += (double) iterator.getFloat();
            value /= doubleBase;
            iterator.next();
        }

        // If the end result fits in a double, any intermediate calculation must not overflow
        // Note that 1/BASE <= value < 1
        if (this.exponent > 0)
        {
            return this.sign * value * Math.pow((double) BASE[this.radix], (double) (this.exponent - 1)) * BASE[this.radix];
        }
        else
        {
            return this.sign * value * Math.pow((double) BASE[this.radix], (double) this.exponent);
        }
    }

    public long longValue()
    {
        if (this.sign == 0 || this.exponent <= 0)
        {
            return 0;
        }
        else if (this.exponent > MAX_LONG_SIZE)
        {
            // Overflow for sure
            return (this.sign > 0 ? Long.MAX_VALUE : Long.MIN_VALUE);
        }

        long value = 0,
             longBase = (long) BASE[this.radix],
             maxPrevious = Long.MIN_VALUE / longBase;

        // Number of words in integer part of the number
        int size = (int) Math.min(this.exponent, getSize());

        DataStorage.Iterator iterator = this.dataStorage.iterator(DataStorage.READ, 0, size);

        for (int i = 0; i < (int) this.exponent; i++)
        {
            if (value < maxPrevious)
            {
                // Overflow
                value = 0;
                iterator.close();
                break;
            }
            value *= longBase;
            if (i < size)
            {
                value -= (long) iterator.getFloat();      // Calculate value negated to handle 0x8000000000000000
                iterator.next();
            }
        }

        if (value == Long.MIN_VALUE || value >= 0)
        {
            // Overflow
            return (this.sign > 0 ? Long.MAX_VALUE : Long.MIN_VALUE);
        }
        else
        {
            return -this.sign * value;
        }
    }

    // If this ApfloatImpl is equal to 1
    public boolean isOne()
        throws ApfloatRuntimeException
    {
        return (this.sign == 1 && this.exponent == 1 && getSize() == 1 && getMostSignificantWord() == (float) 1);
    }

    public long equalDigits(ApfloatImpl x)
        throws ApfloatRuntimeException
    {
        if (!(x instanceof FloatApfloatImpl))
        {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }

        FloatApfloatImpl that = (FloatApfloatImpl) x;

        if (this.sign == 0 && that.sign == 0)           // Both are zero
        {
            return Apfloat.INFINITE;
        }
        else if (this.sign != that.sign)                // No match
        {
            return 0;
        }
        else if (this.radix != that.radix)
        {
            throw new RadixMismatchException("Cannot compare values with different radixes: " + this.radix + " and " + that.radix);
        }

        long thisScale = scale(),
             thatScale = that.scale(),
             minScale = Math.min(thisScale, thatScale),
             maxScale = Math.max(thisScale, thatScale);

        if (maxScale - 1 > minScale)                    // No match
        {
            return 0;
        }

        // Need to compare mantissas
        long thisSize = getSize(),
             thatSize = that.getSize(),
             size = Math.max(thisSize, thatSize);
        DataStorage.Iterator thisIterator = getZeroPaddedIterator(0, thisSize),
                             thatIterator = that.getZeroPaddedIterator(0, thatSize);

        long index,
             result = Math.min(this.precision, that.precision);         // If mantissas are identical
        int lastMatchingDigits = -1;                                    // Will be used for deferred comparison hanging in last word, e.g. this = 1.000000000, that = 0.999999999
        float carry,
                base = BASE[this.radix];

        if (this.exponent > that.exponent)
        {
            // Possible case this = 1.0000000, that = 0.9999999
            float value = thisIterator.getFloat();                  // Check first word

            if (value != (float) 1)
            {
                // No match
                thisIterator.close();
                thatIterator.close();

                return 0;
            }

            carry = base;
            thisIterator.next();
        }
        else if (this.exponent < that.exponent)
        {
            // Possible case this = 0.9999999, that = 1.0000000
            float value = thatIterator.getFloat();                  // Check first word

            if (value != (float) 1)
            {
                // No match
                thisIterator.close();
                thatIterator.close();

                return 0;
            }

            carry = -base;
            thatIterator.next();
        }
        else
        {
            // Trivial case, e.g. this = 111234, that = 111567
            carry = 0;
        }

        // Calculate this - that, stopping at first difference
        for (index = 0; index < size; index++)
        {
            float value = thisIterator.getFloat() - thatIterator.getFloat() + carry;

            if (value == 0)
            {
                // Trivial case; words are equal
                carry = 0;
            }
            else if (Math.abs(value) > (float) 1)
            {
                // Mismatch found
                if (Math.abs(value) >= base)
                {
                    // Deferred comparison, e.g. this = 1.0000000002, that = 0.9999999991
                    lastMatchingDigits = -1;
                }
                else
                {
                    // Any trivial cases and e.g. this = 1.0000000001, that = 0.9999999992
                    lastMatchingDigits = BASE_DIGITS[this.radix] - getDigits(Math.abs(value));
                }

                break;
            }
            else if (value == (float) 1)
            {
                // Case this = 1.0000000..., that = 0.9999999...
                carry = base;
            }
            else if (value == (float) -1)
            {
                // Case this = 0.9999999..., that = 1.0000000...
                carry = -base;
            }

            thisIterator.next();
            thatIterator.next();
        }

        if (index < size || carry != 0)                 // Mismatch found
        {
            long initialMatchingDigits = (this.exponent == that.exponent ?
                                          Math.min(getInitialDigits(), that.getInitialDigits()) :       // Normal case, e.g. this = 10, that = 5
                                          BASE_DIGITS[this.radix]);                                     // Special case, e.g. this = 1.0, that = 0.9

            // Note that this works even if index == 0
            long middleMatchingDigits = (index - 1) * BASE_DIGITS[this.radix];                          // This is correct even if exponents are different

            // Limit by available precision
            result = Math.min(result, initialMatchingDigits + middleMatchingDigits + lastMatchingDigits);

            // Handle some cases e.g. 0.15 vs. 0.04
            result = Math.max(result, 0);
        }

        thisIterator.close();
        thatIterator.close();

        return result;
    }

    public int compareTo(ApfloatImpl x)
        throws ApfloatRuntimeException
    {
        if (!(x instanceof FloatApfloatImpl))
        {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }

        FloatApfloatImpl that = (FloatApfloatImpl) x;

        if (this.sign == 0 && that.sign == 0)
        {
            return 0;
        }
        else if (this.sign < that.sign)                 // Now we know that not both are zero
        {
            return -1;
        }
        else if (this.sign > that.sign)
        {
            return 1;
        }
        else if (this.radix != that.radix)
        {
            throw new RadixMismatchException("Cannot compare values with different radixes: " + this.radix + " and " + that.radix);
        }
        else if (scale() < that.scale())                // Now we know that both have same sign (which is not zero)
        {
            return -this.sign;
        }
        else if (scale() > that.scale())
        {
            return this.sign;
        }

        // Need to compare mantissas
        return this.sign * compareMantissaTo(that);
    }

    // Returns an iterator for this number's data storage from start to end,
    // least significant word is correctly truncated with getLeastSignificantWord(),
    // after that the iterator returns zeros only
    private DataStorage.Iterator getZeroPaddedIterator(final long start, final long end)
        throws ApfloatRuntimeException
    {
        final DataStorage.Iterator iterator = this.dataStorage.iterator(DataStorage.READ, start, end);

        return new DataStorage.Iterator()
        {
            public float getFloat()
                throws ApfloatRuntimeException
            {
                float value;

                if (this.index < end)
                {
                    value = iterator.getFloat();
                    if (this.index == end - 1)
                    {
                        value = getLeastSignificantWord(this.index, value);
                    }
                }
                else
                {
                    value = 0;
                }

                return value;
            }

            public void next()
                throws ApfloatRuntimeException
            {
                if (this.index < end)
                {
                    iterator.next();
                    this.index++;
                }
            }

            public void close()
                throws ApfloatRuntimeException
            {
                iterator.close();
            }

            private static final long serialVersionUID = 1L;

            private long index = start;
        };
    }

    // Compare absolute values of mantissas
    private int compareMantissaTo(FloatApfloatImpl that)
        throws ApfloatRuntimeException
    {
        long thisSize = getSize(),
             thatSize = that.getSize(),
             size = Math.max(thisSize, thatSize);
        DataStorage.Iterator thisIterator = getZeroPaddedIterator(0, thisSize),
                             thatIterator = that.getZeroPaddedIterator(0, thatSize);
        int result = 0;

        long index = findMismatch(thisIterator, thatIterator, size);

        if (index >= 0)                 // Mismatch found
        {
            float thisValue = thisIterator.getFloat(),
                    thatValue = thatIterator.getFloat();

            if (thisValue < thatValue)
            {
                result = -1;
            }
            else if (thisValue > thatValue)
            {
                result = 1;
            }
        }

        thisIterator.close();
        thatIterator.close();

        return result;
    }

    // Returns index of first mismatching float, or -1 if mantissas are equal
    // Iterators are left to point to the mismatching words
    private long findMismatch(DataStorage.Iterator thisIterator, DataStorage.Iterator thatIterator, long size)
        throws ApfloatRuntimeException
    {
        for (long index = 0; index < size; index++)
        {
            float thisValue = thisIterator.getFloat(),
                    thatValue = thatIterator.getFloat();

            if (thisValue != thatValue)
            {
                return index;
            }

            thisIterator.next();
            thatIterator.next();
        }

        // All searched words matched exactly
        return -1;
    }

    // Truncate insignificant digits from the last float of the number
    private float getLeastSignificantWord(long index, float word)
        throws ApfloatRuntimeException
    {
        if (this.precision == Apfloat.INFINITE)
        {
            return word;
        }

        // Total digits including the specified index
        long digits = getInitialDigits() + index * BASE_DIGITS[this.radix];

        if (this.precision >= digits)
        {
            return word;
        }

        // Assert that the second array access will not be out of bounds
        float divisor = MINIMUM_FOR_DIGITS[this.radix][(int) (digits - this.precision)];

        return (float) (long) (word / divisor) * divisor;
    }

    /**
     * Compares this object to the specified object.
     *
     * @param obj The object to compare with.
     *
     * @return <code>true</code> if the objects are equal; <code>false</code> otherwise.
     */

    public boolean equals(Object obj)
    {
        if (!(obj instanceof ApfloatImpl))
        {
            return false;
        }

        ApfloatImpl thatImpl = (ApfloatImpl) obj;

        // Special comparisons against Apfloat.ZERO and Apfloat.ONE work regardless of radix or implementation class
        if (signum() == 0 && thatImpl.signum() == 0)
        {
            return true;
        }
        else if (isOne() && thatImpl.isOne())
        {
            return true;
        }

        if (!(obj instanceof FloatApfloatImpl))
        {
            return false;
        }

        FloatApfloatImpl that = (FloatApfloatImpl) obj;

        if (this.radix != that.radix)
        {
            // Limitation: cannot compare values with different radixes
            return false;
        }
        else if (this.sign != that.sign ||
                 scale()   != that.scale())
        {
            return false;
        }
        else
        {
            // Need to compare mantissas
            return compareMantissaTo(that) == 0;
        }
    }

    public int hashCode()
    {
        if (this.hashCode == 0)
        {
            // Cache the value
            // NOTE: This is not synchronized; it's OK if multiple threads set this at the same time
            int hashCode = 1 + this.sign + (int) this.exponent + (int) (this.exponent >>> 32);

            if (this.dataStorage != null)
            {
                long size = getSize();

                // Scan through log(size) scattered words in the mantissa
                for (long i = 0; i < size; i = i + i + 1)
                {
                    float word = getWord(i);

                    if (i == size - 1)
                    {
                        word = getLeastSignificantWord(i, word);
                    }

                    long element = (long) word;
                    hashCode += (int) element + (int) (element >>> 32);
                }
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    public String toString(boolean pretty)
        throws ApfloatRuntimeException
    {
        if (this.sign == 0)
        {
            return "0";
        }

        long size = getSize() * BASE_DIGITS[this.radix],    // This is a rounded up value
             length;
        if (pretty)
        {
             long scale = scale();
             if (scale <= 0)
             {
                 length = 2 - scale + size;         // Format is 0.xxxx or 0.0000xxx
             }
             else if (size > scale)
             {
                 length = 1 + size;                 // Format is x.xxx
             }
             else
             {
                 length = scale;                    // Format is xxxx or xxxx0000
             }
             length += (this.sign < 0 ? 1 : 0);     // Room for minus sign
        }
        else
        {
            length = size + 24;     // Sign, "0.", "e", exponent sign and 19 digits of exponent
        }

        if (length > Integer.MAX_VALUE || length < 0)           // Detect overflow
        {
            throw new ApfloatInternalException("Number is too large to fit in a String");
        }

        StringWriter writer = new StringWriter((int) length);

        try
        {
            writeTo(writer, pretty);
        }
        catch (IOException ioe)
        {
            throw new ApfloatInternalException("Unexpected I/O error writing to StringWriter", ioe);
        }

        String value = writer.toString();

        assert (value.length() <= length);      // Postcondition to ensure performance

        return value;
    }

    private static void writeZeros(Writer out, long count)
        throws IOException
    {
        for (long i = 0; i < count; i++)
        {
            out.write('0');
        }
    }

    public void writeTo(Writer out, boolean pretty)
        throws IOException, ApfloatRuntimeException
    {
        if (this.sign == 0)
        {
            out.write('0');
            return;
        }

        if (this.sign < 0)
        {
            out.write('-');
        }

        long integerDigits,                 // Number of digits to write before the decimal point
             exponent;                      // Exponent to print

        if (pretty)
        {
            if (this.exponent <= 0)
            {
                out.write("0.");            // Output is 0.xxxx
                writeZeros(out, -scale());  // Print leading zeros after decimal point before first nonzero digit
                integerDigits = -1;         // Decimal point is already written
            }
            else
            {
                integerDigits = scale();    // Decimal point location
            }
            exponent = 0;                   // Do not print exponent
        }
        else
        {
            integerDigits = 1;              // Always write as x.xxxey
            exponent = scale() - 1;         // Print exponent
        }

        boolean leftPadZeros = false;       // If the written base unit should be left-padded with zeros
        long size = getSize(),
             digitsToWrite = Math.min(this.precision, getInitialDigits() + (size - 1) * BASE_DIGITS[this.radix]),
             digitsWritten = 0,
             trailingZeros = 0;
        DataStorage.Iterator iterator = this.dataStorage.iterator(DataStorage.READ, 0, size);
        char[] buffer = new char[BASE_DIGITS[this.radix]];

        while (size > 0)
        {
            int start = (leftPadZeros ? 0 : BASE_DIGITS[this.radix] - getInitialDigits()),
                digits = (int) Math.min(digitsToWrite, BASE_DIGITS[this.radix] - start);

            formatWord(buffer, iterator.getFloat());

            for (int i = 0; i < digits; i++)
            {
                int c = buffer[start + i];
                if (c == '0')
                {
                    trailingZeros++;
                    digitsToWrite--;
                }
                else
                {
                    while (trailingZeros > 0)
                    {
                        if (digitsWritten == integerDigits)
                        {
                            out.write('.');
                        }
                        out.write('0');
                        digitsWritten++;
                        trailingZeros--;
                    }
                    if (digitsWritten == integerDigits)
                    {
                        out.write('.');
                    }
                    out.write(c);
                    digitsWritten++;
                    digitsToWrite--;
                }
            }
            leftPadZeros = true;                        // Always pad with zeros after first word

            iterator.next();
            size--;
        }

        if (!pretty && exponent != 0)
        {
            out.write("e" + exponent);
        }

        writeZeros(out, integerDigits - digitsWritten); // If format is xxxx0000
    }

    private void formatWord(char[] buffer, float word)
    {
        int position = BASE_DIGITS[this.radix];
        while (position > 0 && word > 0)
        {
            float newWord = (float) (long) (word / this.radix);
            int digit = (int) (word -  newWord * this.radix);
            word = newWord;
            position--;
            buffer[position] = Character.forDigit(digit, this.radix);
        }

        // Left pad zeros
        while (position > 0)
        {
            position--;
            buffer[position] = '0';
        }
    }

    // Effective size, in floats
    private long getSize()
        throws ApfloatRuntimeException
    {
        assert (this.dataStorage != null);

        return Math.min(getBasePrecision(),
                        this.dataStorage.getSize());
    }

    private static int checkRadix(int radix)
        throws NumberFormatException
    {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
        {
            throw new NumberFormatException("Invalid radix " + radix + "; radix must be between " + Character.MIN_RADIX + " and " + Character.MAX_RADIX);
        }

        return radix;
    }

    // Get the most significant word of this number
    private float getMostSignificantWord()
        throws ApfloatRuntimeException
    {
        return getMostSignificantWord(this.dataStorage);
    }

    // Get the most significant word of the specified data storage
    private static float getMostSignificantWord(DataStorage dataStorage)
        throws ApfloatRuntimeException
    {
        float msw;

        ArrayAccess arrayAccess = dataStorage.getArray(DataStorage.READ, 0, 1);
        msw = arrayAccess.getFloatData()[arrayAccess.getOffset()];
        arrayAccess.close();

        return msw;
    }

    // Get number of digits in the most significant word
    private int getInitialDigits()
        throws ApfloatRuntimeException
    {
        if (this.initialDigits == UNDEFINED)
        {
            // Cache the value
            // NOTE: This is not synchronized; it's OK if multiple threads set this at the same time
            this.initialDigits = getDigits(getMostSignificantWord());
        }

        return this.initialDigits;
    }

    // Get number of digits in the most significant word of specified data
    private int getInitialDigits(DataStorage dataStorage)
        throws ApfloatRuntimeException
    {
        return getDigits(getMostSignificantWord(dataStorage));
    }

    // Gets the number of digits in the specified float and this number's radix
    private int getDigits(float x)
    {
        assert (x > 0);

        float[] minimums = MINIMUM_FOR_DIGITS[this.radix];
        int i = minimums.length;

        while (x < minimums[--i])
        {
        }

        return i + 1;
    }

    // Gets the precision in floats
    private long getBasePrecision()
        throws ApfloatRuntimeException
    {
        return getBasePrecision(this.precision, getInitialDigits());
    }

    // Gets the precision in floats, based on specified precision (in digits),
    // number of digits in most significant word and this number's radix
    private long getBasePrecision(long precision, int mswDigits)
    {
        if (precision == Apfloat.INFINITE)
        {
            return Apfloat.INFINITE;
        }
        else
        {
            return (precision + BASE_DIGITS[this.radix] - mswDigits - 1) / BASE_DIGITS[this.radix] + 1;
        }
    }

    private float getWord(long index)
    {
        ArrayAccess arrayAccess = this.dataStorage.getArray(DataStorage.READ, index, 1);
        float word = arrayAccess.getFloatData()[arrayAccess.getOffset()];
        arrayAccess.close();

        return word;
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        this.leastZeros = UNDEFINED;
        in.defaultReadObject();
    }

    // Gets a new data storage for specified size
    private static DataStorage createDataStorage(long size)
        throws ApfloatRuntimeException
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        return dataStorageBuilder.createDataStorage(size * 4);
    }

    // Gets I/O block size in floats
    private static int getBlockSize()
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        return ctx.getBlockSize() / 4;
    }

    private static final DataStorage.Iterator ZERO_ITERATOR =
    new DataStorage.Iterator()
    {
        public float getFloat() { return 0; }
        public void next() { }
        private static final long serialVersionUID = 1L;
    };

    private static final long serialVersionUID = 4198839366471670758L;

    private static final int UNDEFINED = 0x80000000;
    private static final int MAX_LONG_SIZE = 4;
    private static final int MAX_DOUBLE_SIZE = 4;

    private int sign;
    private long precision;
    private long exponent;
    private DataStorage dataStorage;
    private int radix;
    private int hashCode = 0;
    private int initialDigits = UNDEFINED;
    private volatile long leastZeros = UNDEFINED;
    private volatile long size = 0;
}
