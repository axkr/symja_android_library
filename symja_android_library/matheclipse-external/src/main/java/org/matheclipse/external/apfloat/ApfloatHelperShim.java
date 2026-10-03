package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.spi.Util;

/**
 * The few methods of apfloat's package-private <code>org.apfloat.ApfloatHelper</code> which the
 * classes of this package need, with the same names and the same semantics.<p>
 *
 * This class exists only because the helper is not visible from outside of the
 * <code>org.apfloat</code> package. When the classes of this package are moved into apfloat,
 * this class is deleted, every <code>ApfloatHelperShim.</code> method call becomes
 * <code>ApfloatHelper.</code>, and <code>ApfloatHelperShim.ZEROS</code> and
 * <code>ApfloatHelperShim.ONES</code> become <code>Apcomplex.ZEROS</code> and
 * <code>Apcomplex.ONES</code>.
 */

class ApfloatHelperShim
{
    private ApfloatHelperShim()
    {
    }

    // The value of the package-private Apcomplex.EXTRA_PRECISION
    static final int EXTRA_PRECISION = 20;

    // The package-private Apcomplex.ZEROS and Apcomplex.ONES, indexed by the radix
    static final Apint[] ZEROS = new Apint[Character.MAX_RADIX + 1];
    static final Apint[] ONES = new Apint[Character.MAX_RADIX + 1];

    static
    {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++)
        {
            ZEROS[radix] = new Apint(0, radix);
            ONES[radix] = new Apint(1, radix);
        }
    }

    // Returns the number of extra digits which is small but still enough for some rounding errors
    static long getSmallExtraPrecision(int radix)
    {
        assert (radix > 0);
        return Math.min(3, (long) Math.ceil(5 / Math.log(radix)));
    }

    // Returns given precision extended by specified amount
    static long extendPrecision(long precision, long extraPrecision)
    {
        return Util.ifFinite(precision, precision + extraPrecision);
    }

    // Returns given precision extended by EXTRA_PRECISION
    static long extendPrecision(long precision)
    {
        return extendPrecision(precision, EXTRA_PRECISION);
    }

    // Returns x with precision at least as specified
    static Apfloat ensurePrecision(Apfloat x, long precision)
        throws ApfloatRuntimeException
    {
        return x.precision(Math.max(x.precision(), precision));
    }

    // Returns z with precision at least as specified
    static Apcomplex ensurePrecision(Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        return new Apcomplex(ensurePrecision(z.real(), precision),
                             ensurePrecision(z.imag(), precision));
    }

    // Returns x with precision at most as specified
    static Apfloat limitPrecision(Apfloat x, long precision)
        throws ApfloatRuntimeException
    {
        return x.precision(Math.min(x.precision(), precision));
    }

    // Returns z with precision at most as specified
    static Apcomplex limitPrecision(Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        return new Apcomplex(limitPrecision(z.real(), precision),
                             limitPrecision(z.imag(), precision));
    }
}
