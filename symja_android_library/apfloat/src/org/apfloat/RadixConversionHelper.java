package org.apfloat;

import java.util.List;
import java.util.ArrayList;

import org.apfloat.spi.Util;
import static org.apfloat.spi.RadixConstants.*;

/**
 * Helper class for radix conversion.
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

class RadixConversionHelper
{
    private static class RadixPowerList
    {
        public RadixPowerList(int fromRadix, int toRadix, long precision)
            throws ApfloatRuntimeException
        {
            this.list = new ArrayList<Apfloat>();
            this.list.add(new Apfloat(fromRadix, ApfloatHelper.extendPrecision(precision), toRadix));
        }

        public Apfloat pow(long n)
            throws ApfloatRuntimeException
        {
            if (n == 0)
            {
                return Apfloat.ONE;
            }

            int p = 0;

            while ((n & 1) == 0)
            {
                p++;
                n >>>= 1;
            }

            Apfloat r = get(p);

            while ((n >>>= 1) > 0)
            {
                Apfloat x = get(++p);
                if ((n & 1) != 0)
                {
                    r = r.multiply(x);
                }
            }

            return r;
        }

        private Apfloat get(int index)
            throws ApfloatRuntimeException
        {
            Apfloat x;
            if (this.list.size() > index)
            {
                x = this.list.get(index);
            }
            else
            {
                x = get(index - 1);
                x = x.multiply(x);
                this.list.add(x);
            }
            return x;
        }

        private List<Apfloat> list;
    }

    private RadixConversionHelper()
    {
    }

    public static Apfloat toRadix(Apfloat x, int toRadix)
        throws ApfloatRuntimeException
    {
        if (x.radix() == toRadix)
        {
            return x;
        }

        if (x.signum() == 0)
        {
            return new Apfloat(0, toRadix);
        }

        int fromRadix = x.radix();
        long size = x.size(),
             scale = x.scale(),
             precision = getPrecision(x.precision(), fromRadix, toRadix);
        RadixPowerList radixPowerList = new RadixPowerList(fromRadix, toRadix, precision);

        return toRadixIntegerPart(x, toRadix, size, scale, radixPowerList)
          .add(toRadixFractionalPart(x, toRadix, size, scale, radixPowerList))
          .precision(precision);
    }

    private static Apfloat toRadixIntegerPart(Apfloat x, int toRadix, long size, long scale, RadixPowerList radixPowerList)
        throws ApfloatRuntimeException
    {
        if (scale <= 0)
        {
            // Integer part doesn't exist
            return Apfloat.ZERO;
        }
        else if (scale > size)
        {
            // No fractional part; trailing zeros in integer part
            long shift = scale - size;
            x = ApfloatMath.scale(x, -shift);
            x = toRadixNormalizedPart(x, toRadix, size, radixPowerList);
            return x.multiply(radixPowerList.pow(shift));
        }
        else
        {
            // Fractional part exists (might be zero-length though)
            x = x.truncate();
            return toRadixNormalizedPart(x, toRadix, x.scale(), radixPowerList);
        }
    }

    private static Apfloat toRadixFractionalPart(Apfloat x, int toRadix, long size, long scale, RadixPowerList radixPowerList)
        throws ApfloatRuntimeException
    {
        if (size > scale)
        {
            // Fractional part exists
            if (scale > 0)
            {
                // Both integer and fractional parts exist
                x = x.frac();
                size -= scale;
                scale = 0;
            }
            long precision = getPrecision(x.precision(), x.radix(), toRadix),
                 shift = size - scale;
            x = ApfloatMath.scale(x, shift);
            x = toRadixNormalizedPart(x, toRadix, size, radixPowerList);
            return x.precision(precision).divide(radixPowerList.pow(shift));
        }
        else
        {
            // Fractional part doesn't exist
            return Apfloat.ZERO;
        }
    }

    private static Apfloat toRadixNormalizedPart(Apfloat x, int toRadix, long size, RadixPowerList radixPowerList)
        throws ApfloatRuntimeException
    {
        long maxPow2 = Util.round2down(size);
        return split(x, toRadix, size, maxPow2, radixPowerList);
    }

    private static Apfloat split(Apfloat x, int toRadix, long size, long split, RadixPowerList radixPowerList)
        throws ApfloatRuntimeException
    {
        if (size <= 0)
        {
            return Apfloat.ZERO;
        }
        else if (size <= LONG_DIGITS[x.radix()])
        {
            return new Apfloat(x.longValue(), Apfloat.INFINITE, toRadix);
        }
        else
        {
            x = ApfloatMath.scale(x, -split);
            Apfloat top = x.truncate(),
                    bottom = ApfloatMath.scale(x.frac(), split);
            return split(top, toRadix, size - split, split >> 1, radixPowerList).multiply(radixPowerList.pow(split))
              .add(split(bottom, toRadix, split, split >> 1, radixPowerList));
        }
    }

    private static long getPrecision(long precision, int fromRadix, int toRadix)
        throws ApfloatRuntimeException
    {
        long newPrecision = (long) ((double) precision * Math.log((double) fromRadix) / Math.log((double) toRadix));
        if (fromRadix < toRadix)
        {
            // Underflow is possible but overflow not, in the above calculation
            newPrecision = Math.max(1, newPrecision);
        }
        return Util.ifFinite(precision, newPrecision);
    }
}
