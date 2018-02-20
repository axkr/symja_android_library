package org.apfloat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;
import java.util.PriorityQueue;

import org.apfloat.spi.Util;

/**
 * Various mathematical functions for arbitrary precision complex numbers.
 *
 * @see ApfloatMath
 *
 * @version 1.8.1
 * @author Mikko Tommila
 */

public class ApcomplexMath
{
    private ApcomplexMath()
    {
    }

    /**
     * Negative value.
     *
     * @deprecated Use {@link Apcomplex#negate()}.
     *
     * @param z The argument.
     *
     * @return <code>-z</code>.
     */

    @Deprecated
    public static Apcomplex negate(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return z.negate();
    }

    /**
     * Absolute value.
     *
     * @param z The argument.
     *
     * @return <code>sqrt(x<sup>2</sup> + y<sup>2</sup>)</code>, where <code>z = x + <i>i</i> y</code>.
     */

    public static Apfloat abs(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z.real().signum() == 0)
        {
             return ApfloatMath.abs(z.imag());
        }
        else if (z.imag().signum() == 0)
        {
             return ApfloatMath.abs(z.real());
        }
        else
        {
             return ApfloatMath.sqrt(norm(z));
        }
    }

    /**
     * Norm. Square of the magnitude.
     *
     * @param z The argument.
     *
     * @return <code>x<sup>2</sup> + y<sup>2</sup></code>, where <code>z = x + <i>i</i> y</code>.
     */

    public static Apfloat norm(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return ApfloatMath.multiplyAdd(z.real(), z.real(), z.imag(), z.imag());
    }

    /**
     * Angle of the complex vector in the complex plane.
     *
     * @param z The argument.
     *
     * @return <code>arctan(y / x)</code> from the appropriate branch, where <code>z = x + <i>i</i> y</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is zero.
     */

    public static Apfloat arg(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return ApfloatMath.atan2(z.imag(), z.real());
    }

    /**
     * Multiply by a power of the radix.
     *
     * @param z The argument.
     * @param scale The scaling factor.
     *
     * @return <code>z * z.radix()<sup>scale</sup></code>.
     */

    public static Apcomplex scale(Apcomplex z, long scale)
        throws ApfloatRuntimeException
    {
        return new Apcomplex(ApfloatMath.scale(z.real(), scale),
                             ApfloatMath.scale(z.imag(), scale));
    }

    /**
     * Integer power.
     *
     * @param z Base of the power operator.
     * @param n Exponent of the power operator.
     *
     * @return <code>z</code> to the <code>n</code>:th power, that is <code>z<sup>n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If both <code>z</code> and <code>n</code> are zero.
     */

    public static Apcomplex pow(Apcomplex z, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n == 0)
        {
            if (z.real().signum() == 0 && z.imag().signum() == 0)
            {
                throw new ArithmeticException("Zero to power zero");
            }

            return new Apcomplex(new Apfloat(1, Apfloat.INFINITE, z.radix()));
        }
        else if (n < 0)
        {
            z = Apcomplex.ONE.divide(z);
            n = -n;
        }

        return powAbs(z, n);
    }

    // Absolute value of n used
    private static Apcomplex powAbs(Apcomplex z, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        z = ApfloatHelper.extendPrecision(z);   // Big exponents will accumulate round-off errors

        // Algorithm improvements by Bernd Kellner
        int b2pow = 0;

        while ((n & 1) == 0)
        {
            b2pow++;
            n >>>= 1;
        }

        Apcomplex r = z;

        while ((n >>>= 1) > 0)
        {
            z = z.multiply(z);
            if ((n & 1) != 0)
            {
                r = r.multiply(z);
            }
        }

        while (b2pow-- > 0)
        {
            r = r.multiply(r);
        }

        return ApfloatHelper.setPrecision(r, precision);
    }

    /**
     * Square root.
     *
     * @param z The argument.
     *
     * @return Square root of <code>z</code>.
     */

    public static Apcomplex sqrt(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return root(z, 2);
    }

    /**
     * Cube root.
     *
     * @param z The argument.
     *
     * @return Cube root of <code>z</code>.
     */

    public static Apcomplex cbrt(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return root(z, 3);
    }

    /**
     * Positive integer root. The branch that has the smallest angle
     * and same sign of imaginary part as <code>z</code> is always chosen.
     *
     * @param z The argument.
     * @param n Which root to take.
     *
     * @return <code>n</code>:th root of <code>z</code>, that is <code>z<sup>1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>n</code> is zero.
     */

    public static Apcomplex root(Apcomplex z, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return root(z, n, 0);
    }

    /**
     * Positive integer root. The specified branch counting from the smallest angle
     * and same sign of imaginary part as <code>z</code> is chosen.
     *
     * @param z The argument.
     * @param n Which root to take.
     * @param k Which branch to take.
     *
     * @return <code>n</code>:th root of <code>z</code>, that is <code>z<sup>1/n</sup>e<sup>i2&pi;sk/n</sup></code> where <code>s</code> is the signum of the imaginary part of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>n</code> is zero.
     *
     * @since 1.5
     */

    public static Apcomplex root(Apcomplex z, long n, long k)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n == 0)
        {
            throw new ArithmeticException("Zeroth root");
        }
        else if (z.real().signum() == 0 && z.imag().signum() == 0)
        {
            if (n < 0)
            {
                throw new ArithmeticException("Inverse root of zero");
            }
            return Apcomplex.ZERO;              // Avoid division by zero
        }
        else if (n == 1)
        {
            return z;
        }
        k %= n;
        if (z.imag().signum() == 0 && z.real().signum() > 0 && k == 0)
        {
            return new Apcomplex(ApfloatMath.root(z.real(), n));
        }
        else if (n < 0)                         // Also correctly handles 0x8000000000000000L
        {
            return inverseRootAbs(z, -n, k);
        }
        else if (n == 2)
        {
            return z.multiply(inverseRootAbs(z, 2, k));
        }
        else if (n == 3)
        {
            // Choose the correct branch
            if (z.real().signum() < 0)
            {
                k = (z.imag().signum() == 0 ? 1 - k : k - 1);
                k %= n;
            }
            else
            {
                k = -k;
            }
            Apcomplex w = z.multiply(z);
            return z.multiply(inverseRootAbs(w, 3, k));
        }
        else
        {
            return inverseRootAbs(inverseRootAbs(z, n, k), 1, 0);
        }
    }

    /**
     * Inverse positive integer root. The branch that has the smallest angle
     * and different sign of imaginary part than <code>z</code> is always chosen.
     *
     * @param z The argument.
     * @param n Which inverse root to take.
     *
     * @return Inverse <code>n</code>:th root of <code>z</code>, that is <code>z<sup>-1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> or <code>n</code> is zero.
     */

    public static Apcomplex inverseRoot(Apcomplex z, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return inverseRoot(z, n, 0);
    }

    /**
     * Inverse positive integer root. The specified branch counting from the smallest angle
     * and different sign of imaginary part than <code>z</code> is chosen.
     *
     * @param z The argument.
     * @param n Which inverse root to take.
     * @param k Which branch to take.
     *
     * @return Inverse <code>n</code>:th root of <code>z</code>, that is <code>z<sup>-1/n</sup>e<sup>-i2&pi;k/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> or <code>n</code> is zero.
     */

    public static Apcomplex inverseRoot(Apcomplex z, long n, long k)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z.real().signum() == 0 && z.imag().signum() == 0)
        {
            throw new ArithmeticException("Inverse root of zero");
        }
        else if (n == 0)
        {
            throw new ArithmeticException("Inverse zeroth root");
        }
        k %= n;
        if (z.imag().signum() == 0 && z.real().signum() > 0 && k == 0)
        {
            return new Apcomplex(ApfloatMath.inverseRoot(z.real(), n));
        }
        else if (n < 0)
        {
            return inverseRootAbs(inverseRootAbs(z, -n, k), 1, 0);      // Also correctly handles 0x8000000000000000L
        }

        return inverseRootAbs(z, n, k);
    }

    // Absolute value of n used
    private static Apcomplex inverseRootAbs(Apcomplex z, long n, long k)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z.equals(Apcomplex.ONE) && k == 0)
        {
            // Trivial case
            return z;
        }
        else if (n == 2 && z.imag().signum() == 0 && z.real().signum() < 0)
        {
            // Avoid round-off errors and produce a pure imaginary result
            Apfloat y = ApfloatMath.inverseRoot(z.real().negate(), n);
            return new Apcomplex(Apfloat.ZERO, k == 0 ? y.negate() : y);
        }

        long targetPrecision = z.precision();

        if (targetPrecision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate inverse root to infinite precision");
        }

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                divisor = ApfloatMath.abs(new Apfloat(n, Apfloat.INFINITE, z.radix()));

        double doubleReal,
               doubleImag,
               magnitude,
               angle,
               doubleN = Math.abs((double) n);

        long realScale = z.real().scale(),
             imagScale = z.imag().scale(),
             scale = Math.max(realScale, imagScale),
             scaleDiff = scale - Math.min(realScale, imagScale),
             doublePrecision = ApfloatHelper.getDoublePrecision(z.radix()),
             precision = doublePrecision,       // Accuracy of initial guess
             scaleQuot = scale / n,             // If n is 0x8000000000000000 then this will be zero
             scaleRem = scale - scaleQuot * n;
        double scaleRemFactor = Math.pow((double) z.radix(), (double) -scaleRem / doubleN);

        Apcomplex result;

        // Calculate initial guess from z
        if (z.imag().signum() == 0 ||
            (scaleDiff > doublePrecision / 2 || scaleDiff < 0) && realScale > imagScale)        // Detect overflow
        {
            // z.real() is a lot bigger in magnitude than z.imag()
            Apfloat tmpReal = z.real().precision(doublePrecision),
                    tmpImag = z.imag().precision(doublePrecision);
            Apcomplex tweak = new Apcomplex(Apfloat.ZERO,
                                            tmpImag.divide(divisor.multiply(tmpReal)));

            tmpReal = ApfloatMath.scale(tmpReal, -tmpReal.scale());     // Allow exponents in excess of doubles'

            if ((magnitude = tmpReal.doubleValue()) >= 0.0)
            {
                doubleReal = Math.pow(magnitude, -1.0 / doubleN) * scaleRemFactor;
                doubleImag = 0.0;
            }
            else
            {
                magnitude = Math.pow(-magnitude, -1.0 / doubleN) * scaleRemFactor;
                angle = (tmpImag.signum() >= 0 ? -Math.PI : Math.PI) / doubleN;
                doubleReal = magnitude * Math.cos(angle);
                doubleImag = magnitude * Math.sin(angle);
            }

            tmpReal = ApfloatMath.scale(new Apfloat(doubleReal, doublePrecision, z.radix()), -scaleQuot);
            tmpImag = ApfloatMath.scale(new Apfloat(doubleImag, doublePrecision, z.radix()), -scaleQuot);
            result = new Apcomplex(tmpReal, tmpImag);
            result = result.subtract(result.multiply(tweak));               // Must not be real
        }
        else if (z.real().signum() == 0 ||
                 (scaleDiff > doublePrecision / 2 || scaleDiff < 0) && imagScale > realScale)        // Detect overflow
        {
            // z.imag() is a lot bigger in magnitude than z.real()
            Apfloat tmpReal = z.real().precision(doublePrecision),
                    tmpImag = z.imag().precision(doublePrecision);
            Apcomplex tweak = new Apcomplex(Apfloat.ZERO,
                                            tmpReal.divide(divisor.multiply(tmpImag)));

            tmpImag = ApfloatMath.scale(tmpImag, -tmpImag.scale());     // Allow exponents in exess of doubles'

            if ((magnitude = tmpImag.doubleValue()) >= 0.0)
            {
                magnitude = Math.pow(magnitude, -1.0 / doubleN) * scaleRemFactor;
                angle = -Math.PI / (2.0 * doubleN);
            }
            else
            {
                magnitude = Math.pow(-magnitude, -1.0 / doubleN) * scaleRemFactor;
                angle = Math.PI / (2.0 * doubleN);
            }

            doubleReal = magnitude * Math.cos(angle);
            doubleImag = magnitude * Math.sin(angle);

            tmpReal = ApfloatMath.scale(new Apfloat(doubleReal, doublePrecision, z.radix()), -scaleQuot);
            tmpImag = ApfloatMath.scale(new Apfloat(doubleImag, doublePrecision, z.radix()), -scaleQuot);
            result = new Apcomplex(tmpReal, tmpImag);
            result = result.add(result.multiply(tweak));               // Must not be pure imaginary
        }
        else
        {
            // z.imag() and z.real() approximately the same in magnitude
            Apfloat tmpReal = z.real().precision(doublePrecision),
                    tmpImag = z.imag().precision(doublePrecision);

            tmpReal = ApfloatMath.scale(tmpReal, -scale);       // Allow exponents in exess of doubles'
            tmpImag = ApfloatMath.scale(tmpImag, -scale);       // Allow exponents in exess of doubles'

            doubleReal = tmpReal.doubleValue();
            doubleImag = tmpImag.doubleValue();

            magnitude = Math.pow(doubleReal * doubleReal + doubleImag * doubleImag, -1.0 / (2.0 * doubleN)) * scaleRemFactor;
            angle = -Math.atan2(doubleImag, doubleReal) / doubleN;

            doubleReal = magnitude * Math.cos(angle);
            doubleImag = magnitude * Math.sin(angle);

            tmpReal = ApfloatMath.scale(new Apfloat(doubleReal, doublePrecision, z.radix()), -scaleQuot);
            tmpImag = ApfloatMath.scale(new Apfloat(doubleImag, doublePrecision, z.radix()), -scaleQuot);
            result = new Apcomplex(tmpReal, tmpImag);
        }

        // Alter the angle by the branch chosen
        if (k != 0)
        {
            Apcomplex branch;
            // Handle exact cases
            k = (k < 0 ? k + n : k);
            if (n % 4 == 0 && (n >>> 2) == k)
            {
                branch = new Apcomplex(Apfloat.ZERO, one);
            }
            else if (n % 4 == 0 && (n >>> 2) * 3 == k)
            {
                branch = new Apcomplex(Apfloat.ZERO, one.negate());
            }
            else if (n % 2 == 0 && (n >>> 1) == k)
            {
                branch = one.negate();
            }
            else
            {
                angle = 2.0 * Math.PI * (double) k / doubleN;
                doubleReal = Math.cos(angle);
                doubleImag = Math.sin(angle);
                Apfloat tmpReal = new Apfloat(doubleReal, doublePrecision, z.radix());
                Apfloat tmpImag = new Apfloat(doubleImag, doublePrecision, z.radix());
                branch = new Apcomplex(tmpReal, tmpImag);
            }
            result = result.multiply(z.imag().signum() >= 0 ? branch.conj() : branch);
        }

        int iterations = 0;

        // Compute total number of iterations
        for (long maxPrec = precision; maxPrec < targetPrecision; maxPrec <<= 1)
        {
            iterations++;
        }

        int precisingIteration = iterations;

        // Check where the precising iteration should be done
        for (long minPrec = precision; precisingIteration > 0; precisingIteration--, minPrec <<= 1)
        {
            if ((minPrec - Apcomplex.EXTRA_PRECISION) << precisingIteration >= targetPrecision)
            {
                break;
            }
        }

        z = ApfloatHelper.extendPrecision(z);

        // Newton's iteration
        while (iterations-- > 0)
        {
            precision *= 2;
            result = ApfloatHelper.setPrecision(result, Math.min(precision, targetPrecision));

            Apcomplex t = powAbs(result, n);
            t = lastIterationExtendPrecision(iterations, precisingIteration, t);
            t = one.subtract(z.multiply(t));
            if (iterations < precisingIteration)
            {
                t = new Apcomplex(t.real().precision(precision / 2),
                                  t.imag().precision(precision / 2));
            }

            result = lastIterationExtendPrecision(iterations, precisingIteration, result);
            result = result.add(result.multiply(t).divide(divisor));

            // Precising iteration
            if (iterations == precisingIteration)
            {
                t = powAbs(result, n);
                t = lastIterationExtendPrecision(iterations, -1, t);

                result = lastIterationExtendPrecision(iterations, -1, result);
                result = result.add(result.multiply(one.subtract(z.multiply(t))).divide(divisor));
            }
        }

        return ApfloatHelper.setPrecision(result, targetPrecision);
    }

    /**
     * All values of the positive integer root.<p>
     *
     * Returns all of the <code>n</code> values of the root, in the order
     * of the angle, starting from the smallest angle and same sign of
     * imaginary part as <code>z</code>.
     *
     * @param z The argument.
     * @param n Which root to take.
     *
     * @return All values of the <code>n</code>:th root of <code>z</code>, that is <code>z<sup>1/n</sup></code>, in the order of the angle.
     *
     * @exception java.lang.ArithmeticException If <code>n</code> is zero.
     *
     * @since 1.5
     */

    public static Apcomplex[] allRoots(Apcomplex z, int n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n == 0)
        {
            throw new ArithmeticException("Zeroth root");
        }
        else if (n == 1)
        {
            return new Apcomplex[] { z };
        }
        else if (n == 0x80000000)
        {
            throw new ApfloatRuntimeException("Maximum array size exceeded");
        }
        else if (z.real().signum() == 0 && z.imag().signum() == 0)
        {
            if (n < 0)
            {
                throw new ArithmeticException("Inverse root of zero");
            }
            Apcomplex[] allRoots = new Apcomplex[n];
            Arrays.fill(allRoots, Apcomplex.ZERO);
            return allRoots;                                    // Avoid division by zero
        }

        boolean inverse = (n < 0);
        n = Math.abs(n);

        long precision = z.precision();
        z = ApfloatHelper.extendPrecision(z);                   // Big roots will accumulate round-off errors

        Apcomplex w = inverseRootAbs(new Apfloat(1, precision, z.radix()), n, 1);
        w = (z.imag().signum() >= 0 ^ inverse ? w.conj() : w);  // Complex n:th root of unity

        Apcomplex[] allRoots = new Apcomplex[n];
        Apcomplex root = (inverse ? inverseRootAbs(z, n, 0) : root(z, n));
        allRoots[0] = ApfloatHelper.setPrecision(root, precision);
        for (int i = 1; i < n; i++)
        {
            root = root.multiply(w);
            allRoots[i] = ApfloatHelper.setPrecision(root, precision);
        }
        return allRoots;
    }

    /**
     * Arithmetic-geometric mean.
     *
     * @param a First argument.
     * @param b Second argument.
     *
     * @return Arithmetic-geometric mean of <code>a</code> and <code>b</code>.
     */

    public static Apcomplex agm(Apcomplex a, Apcomplex b)
        throws ApfloatRuntimeException
    {
        if (a.real().signum() == 0 && a.imag().signum() == 0 ||
            b.real().signum() == 0 && b.imag().signum() == 0)         // Would not converge quadratically
        {
            return Apcomplex.ZERO;
        }

        long workingPrecision = Math.min(a.precision(), b.precision()),
             targetPrecision = Math.max(a.precision(), b.precision());

        if (workingPrecision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate agm to infinite precision");
        }

        // Some minimum precision is required for the algorithm to work
        workingPrecision = ApfloatHelper.extendPrecision(workingPrecision);
        a = ApfloatHelper.ensurePrecision(a, workingPrecision);
        b = ApfloatHelper.ensurePrecision(b, workingPrecision);

        long precision = 0,
             halfWorkingPrecision = (workingPrecision + 1) / 2;
        final long CONVERGING = 1000;           // Arbitrarily chosen value...
        Apfloat two = new Apfloat(2, Apfloat.INFINITE, a.radix());

        // First check convergence
        while (precision < CONVERGING && precision < halfWorkingPrecision)
        {
            Apcomplex t = a.add(b).divide(two);
            b = sqrt(a.multiply(b));
            a = t;

            // Conserve precision in case of accumulating round-off errors
            a = ApfloatHelper.ensurePrecision(a, workingPrecision);
            b = ApfloatHelper.ensurePrecision(b, workingPrecision);

            precision = a.equalDigits(b);
        }

        // Now we know quadratic convergence
        while (precision <= halfWorkingPrecision)
        {
            Apcomplex t = a.add(b).divide(two);
            b = sqrt(a.multiply(b));
            a = t;

            // Conserve precision in case of accumulating round-off errors
            a = ApfloatHelper.ensurePrecision(a, workingPrecision);
            b = ApfloatHelper.ensurePrecision(b, workingPrecision);

            precision *= 2;
        }

        return ApfloatHelper.setPrecision(a.add(b).divide(two), targetPrecision);
    }

    /**
     * Natural logarithm.<p>
     *
     * The logarithm is calculated using the arithmetic-geometric mean.
     * See the Borweins' book for the formula.
     *
     * @param z The argument.
     *
     * @return Natural logarithm of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is zero.
     */

    public static Apcomplex log(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z.real().signum() >= 0 && z.imag().signum() == 0)
        {
            return ApfloatMath.log(z.real());
        }

        // Calculate the log using 1 / radix <= |z| < 1 and the log addition formula
        // because the agm converges badly for big z

        long targetPrecision = z.precision();

        if (targetPrecision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate logarithm to infinite precision");
        }

        Apfloat imagBias;

        // Scale z so that real part of z is always >= 0, that is its angle is -pi/2 <= angle(z) <= pi/2 to avoid possible instability near z.imag() = +-pi
        if (z.real().signum() < 0)
        {
            Apfloat pi = ApfloatHelper.extendPrecision(ApfloatMath.pi(targetPrecision, z.radix()), z.radix() <= 3 ? 1 : 0);     // pi may have 1 digit more than pi/2

            if (z.imag().signum() >= 0)
            {
                imagBias = pi;
            }
            else
            {
                imagBias = pi.negate();
            }

            z = z.negate();
        }
        else
        {
            // No bias
            imagBias = Apfloat.ZERO;
        }

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                x = abs(z);

        long originalScale = z.scale();

        z = scale(z, -originalScale);   // Set z's scale to zero

        Apfloat radixPower;
        if (originalScale == 0)
        {
            radixPower = Apfloat.ZERO;
        }
        else
        {
            Apfloat logRadix = ApfloatHelper.extendPrecision(ApfloatMath.logRadix(targetPrecision, z.radix()));
            radixPower = new Apfloat(originalScale, Apfloat.INFINITE, z.radix()).multiply(logRadix);
        }

        Apcomplex result = ApfloatHelper.extendPrecision(rawLog(z)).add(radixPower);

        // If the absolute value of the argument is close to 1, the real part of the result is less accurate
        // If the angle of the argument is close to zero, the imaginary part of the result is less accurate
        long finalRealPrecision = Math.max(targetPrecision - one.equalDigits(x), 1),
             finalImagPrecision = Math.max(targetPrecision - 1 + result.imag().scale(), 1);     // Scale of pi/2 is always 1

        return new Apcomplex(result.real().precision(finalRealPrecision),
                             result.imag().precision(finalImagPrecision).add(imagBias));
    }

    /**
     * Logarithm in arbitrary base.<p>
     *
     * @param z The argument.
     * @param w The base.
     *
     * @return Base-<code>w</code> logarithm of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> or <code>w</code> is zero.
     *
     * @since 1.6
     */

    public static Apcomplex log(Apcomplex z, Apcomplex w)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z.real().signum() >= 0 && z.imag().signum() == 0 &&
            w.real().signum() >= 0 && w.imag().signum() == 0)
        {
            return ApfloatMath.log(z.real(), w.real());
        }

        long targetPrecision = Math.min(z.precision(), w.precision());

        if (z.real().signum() >= 0 && z.imag().signum() == 0)
        {
            Apfloat x = z.real();

            Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());
            targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(x)); // If the log() argument is close to 1, the result is less accurate
            x = x.precision(Math.min(x.precision(), targetPrecision));

            return ApfloatMath.log(x).divide(log(w));
        }
        else if (w.real().signum() >= 0 && w.imag().signum() == 0)
        {
            Apfloat y = w.real();

            Apfloat one = new Apfloat(1, Apfloat.INFINITE, y.radix());
            targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(y)); // If the log() argument is close to 1, the result is less accurate
            y = y.precision(Math.min(y.precision(), targetPrecision));

            return log(z).divide(ApfloatMath.log(y));
        }
        else
        {
            return log(z).divide(log(w));
        }
    }

    // Raw logarithm, regardless of z
    // Doesn't work for really big z, but is faster if used alone for small numbers
    private static Apcomplex rawLog(Apcomplex z)
        throws ApfloatRuntimeException
    {
        assert (z.real().signum() != 0 || z.imag().signum() != 0);      // Infinity

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix());

        final int EXTRA_PRECISION = 25;

        long targetPrecision = z.precision(),
             workingPrecision = ApfloatHelper.extendPrecision(targetPrecision),
             n = targetPrecision / 2 + EXTRA_PRECISION;                 // Very rough estimate

        z = ApfloatHelper.extendPrecision(z, EXTRA_PRECISION);

        Apfloat e = one.precision(workingPrecision);
        e = ApfloatMath.scale(e, -n);
        z = scale(z, -n);

        Apfloat agme = ApfloatHelper.extendPrecision(ApfloatMath.agm(one, e));
        Apcomplex agmez = ApfloatHelper.extendPrecision(agm(one, z));

        Apfloat pi = ApfloatHelper.extendPrecision(ApfloatMath.pi(targetPrecision, z.radix()));
        Apcomplex log = pi.multiply(agmez.subtract(agme)).divide(new Apfloat(2, Apfloat.INFINITE, z.radix()).multiply(agme).multiply(agmez));

        return ApfloatHelper.setPrecision(log, targetPrecision);
    }

    /**
     * Exponent function.
     * Calculated using Newton's iteration for the inverse of logarithm.
     *
     * @param z The argument.
     *
     * @return <code>e<sup>z</sup></code>.
     */

    public static Apcomplex exp(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return ApfloatMath.exp(z.real());
        }

        int radix = z.radix();
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, radix);

        long doublePrecision = ApfloatHelper.getDoublePrecision(radix);

        // If the real part of the argument is close to 0, the result is more accurate
        // The imaginary part must be scaled to the range of -pi ... pi, which may limit the precision
        long targetPrecision = (z.imag().precision() >= z.imag().scale() ?
                                Math.min(Util.ifFinite(z.real().precision(), z.real().precision() + Math.max(1 - z.real().scale(), 0)),
                                         Util.ifFinite(z.imag().precision(), 1 + z.imag().precision() - z.imag().scale())) :
                                0);

        if (targetPrecision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate exponent to infinite precision");
        }
        else if (z.real().compareTo(new Apfloat((double) Long.MAX_VALUE * Math.log((double) radix), doublePrecision, radix)) >= 0)
        {
            throw new OverflowException("Overflow");
        }
        else if (z.real().compareTo(new Apfloat((double) Long.MIN_VALUE * Math.log((double) radix), doublePrecision, radix)) <= 0)
        {
            // Underflow

            return Apcomplex.ZERO;
        }
        else if (targetPrecision == 0)
        {
            throw new LossOfPrecisionException("Complete loss of accurate digits in imaginary part");
        }

        boolean negateResult = false;                           // If the final result is to be negated
        Apfloat zImag;

        if (z.imag().scale() > 0)
        {
            long piPrecision = Util.ifFinite(targetPrecision, targetPrecision + z.imag().scale());
            Apfloat pi = ApfloatMath.pi(piPrecision, radix),    // This is precalculated for initial check only
                    twoPi = pi.add(pi),
                    halfPi = pi.divide(new Apfloat(2, targetPrecision, radix));

            // Scale z so that -pi < z.imag() <= pi
            zImag = ApfloatMath.fmod(z.imag(), twoPi);
            if (zImag.compareTo(pi) > 0)
            {
                zImag = zImag.subtract(twoPi);
            }
            else if (zImag.compareTo(pi.negate()) <= 0)
            {
                zImag = zImag.add(twoPi);
            }
            // More, scale z so that -pi/2 < z.imag() <= pi/2 to avoid instability near z.imag() = +-pi
            if (zImag.compareTo(halfPi) > 0)
            {
                // exp(z - i*pi) = exp(z)/exp(i*pi) = -exp(z)
                zImag = zImag.subtract(pi);
                negateResult = true;
            }
            else if (zImag.compareTo(halfPi.negate()) <= 0)
            {
                // exp(z + i*pi) = exp(z)*exp(i*pi) = -exp(z)
                zImag = zImag.add(pi);
                negateResult = true;
            }
        }
        else
        {
            // No need to scale the imaginary part since it's small, -pi/2 < z.imag() <= pi/2
            zImag = z.imag();
        }
        z = new Apcomplex(z.real(), zImag);

        Apfloat resultReal;
        Apcomplex resultImag;

        // First handle the real part

        if (z.real().signum() == 0)
        {
            resultReal = one;
        }
        else if (z.real().scale() < -doublePrecision / 2)
        {
            // Taylor series: exp(x) = 1 + x + x^2/2 + ...

            long precision = Util.ifFinite(-z.real().scale(), -2 * z.real().scale());
            resultReal = one.precision(precision).add(z.real());
        }
        else
        {
            // Approximate starting value for iteration

            // An overflow or underflow should not occur
            long scaledRealPrecision = Math.max(0, z.real().scale()) + doublePrecision;
            Apfloat logRadix = ApfloatMath.log(new Apfloat((double) radix, scaledRealPrecision, radix)),
                    scaledReal = z.real().precision(scaledRealPrecision).divide(logRadix),
                    integerPart = scaledReal.truncate(),
                    fractionalPart = scaledReal.frac();

            resultReal = new Apfloat(Math.pow((double) radix, fractionalPart.doubleValue()), doublePrecision, radix);
            resultReal = ApfloatMath.scale(resultReal, integerPart.longValue());

            if (resultReal.signum() == 0) {
                // Underflow
                return Apcomplex.ZERO;
            }
        }

        // Then handle the imaginary part

        if (zImag.signum() == 0)
        {
            // Imaginary part may have been reduced to zero e.g. if it was exactly pi
            resultImag = one;
        }
        else if (zImag.scale() < -doublePrecision / 2)
        {
            // Taylor series: exp(z) = 1 + z + z^2/2 + ...

            long precision = Util.ifFinite(-zImag.scale(), -2 * zImag.scale());
            resultImag = new Apcomplex(one.precision(precision), zImag.precision(-zImag.scale()));
        }
        else
        {
            // Approximate starting value for iteration

            double doubleImag = zImag.doubleValue();
            resultImag = new Apcomplex(new Apfloat(Math.cos(doubleImag), doublePrecision, radix),
                                       new Apfloat(Math.sin(doubleImag), doublePrecision, radix));
        }

        // Starting value is (real part starting value) * (imag part starting value)
        Apcomplex result = resultReal.multiply(resultImag);

        long precision = result.precision();    // Accuracy of initial guess

        int iterations = 0;

        // Compute total number of iterations
        for (long maxPrec = precision; maxPrec < targetPrecision; maxPrec <<= 1)
        {
            iterations++;
        }

        int precisingIteration = iterations;

        // Check where the precising iteration should be done
        for (long minPrec = precision; precisingIteration > 0; precisingIteration--, minPrec <<= 1)
        {
            if ((minPrec - Apcomplex.EXTRA_PRECISION) << precisingIteration >= targetPrecision)
            {
                break;
            }
        }

        if (iterations > 0)
        {
            // Precalculate the needed values once to the required precision
            ApfloatMath.logRadix(targetPrecision, radix);
        }

        z = ApfloatHelper.extendPrecision(z);

        // Newton's iteration
        while (iterations-- > 0)
        {
            precision *= 2;
            result = ApfloatHelper.setPrecision(result, Math.min(precision, targetPrecision));

            Apcomplex t = log(result);
            t = lastIterationExtendPrecision(iterations, precisingIteration, t);
            t = z.subtract(t);

            if (iterations < precisingIteration)
            {
                t = new Apcomplex(t.real().precision(precision / 2),
                                  t.imag().precision(precision / 2));
            }

            result = lastIterationExtendPrecision(iterations, precisingIteration, result);
            result = result.add(result.multiply(t));

            // Precising iteration
            if (iterations == precisingIteration)
            {
                t = log(result);
                t = lastIterationExtendPrecision(iterations, -1, t);

                result = lastIterationExtendPrecision(iterations, -1, result);
                result = result.add(result.multiply(z.subtract(t)));
            }
        }

        return ApfloatHelper.setPrecision(negateResult ? result.negate() : result, targetPrecision);
    }

    /**
     * Arbitrary power. Calculated using <code>log()</code> and <code>exp()</code>.<p>
     *
     * @param z The base.
     * @param w The exponent.
     *
     * @return <code>z<sup>w</sup></code>.
     *
     * @exception java.lang.ArithmeticException If both <code>z</code> and <code>w</code> are zero.
     */

    public static Apcomplex pow(Apcomplex z, Apcomplex w)
        throws ApfloatRuntimeException
    {
        long targetPrecision = Math.min(z.precision(), w.precision());

        Apcomplex result = ApfloatHelper.checkPow(z, w, targetPrecision);
        if (result != null)
        {
            return result;
        }

        if (z.real().signum() >= 0 && z.imag().signum() == 0)
        {
            Apfloat x = z.real();

            // Limits precision for log() but may be sub-optimal; precision could be limited more
            Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());
            targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(x)); // If the log() argument is close to 1, the result is less accurate
            x = x.precision(Math.min(x.precision(), targetPrecision));

            return exp(w.multiply(ApfloatMath.log(x)));
        }
        else
        {
            return exp(w.multiply(log(z)));
        }
    }

    /**
     * Inverse cosine. Calculated using <code>log()</code>.
     *
     * @param z The argument.
     *
     * @return Inverse cosine of <code>z</code>.
     */

    public static Apcomplex acos(Apcomplex z)
        throws ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix());

        if (z.imag().signum() == 0 && ApfloatMath.abs(z.real()).compareTo(one) <= 0)
        {
            return ApfloatMath.acos(z.real());
        }

        Apcomplex i = new Apcomplex(Apfloat.ZERO, one),
                  w = i.multiply(log(z.add(sqrt(z.multiply(z).subtract(one)))));

        if (z.real().signum() * z.imag().signum() >= 0)
        {
            return w.negate();
        }
        else
        {
            return w;
        }
    }

    /**
     * Inverse hyperbolic cosine. Calculated using <code>log()</code>.
     *
     * @param z The argument.
     *
     * @return Inverse hyperbolic cosine of <code>z</code>.
     */

    public static Apcomplex acosh(Apcomplex z)
        throws ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix());

        if (z.real().signum() >= 0)
        {
            return log(z.add(sqrt(z.multiply(z).subtract(one))));
        }
        else
        {
            return log(z.subtract(sqrt(z.multiply(z).subtract(one))));
        }
    }

    /**
     * Inverse sine. Calculated using <code>log()</code>.
     *
     * @param z The argument.
     *
     * @return Inverse sine of <code>z</code>.
     */

    public static Apcomplex asin(Apcomplex z)
        throws ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix());

        if (z.imag().signum() == 0 && ApfloatMath.abs(z.real()).compareTo(one) <= 0)
        {
            return ApfloatMath.asin(z.real());
        }

        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);

        if (z.imag().signum() >= 0)
        {
            return i.multiply(log(sqrt(one.subtract(z.multiply(z))).subtract(i.multiply(z))));
        }
        else
        {
            return i.multiply(log(i.multiply(z).add(sqrt(one.subtract(z.multiply(z)))))).negate();
        }
    }

    /**
     * Inverse hyperbolic sine. Calculated using <code>log()</code>.
     *
     * @param z The argument.
     *
     * @return Inverse hyperbolic sine of <code>z</code>.
     */

    public static Apcomplex asinh(Apcomplex z)
        throws ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix());

        if (z.real().signum() >= 0)
        {
            return log(sqrt(z.multiply(z).add(one)).add(z));
        }
        else
        {
            return log(sqrt(z.multiply(z).add(one)).subtract(z)).negate();
        }
    }

    /**
     * Inverse tangent. Calculated using <code>log()</code>.
     *
     * @param z The argument.
     *
     * @return Inverse tangent of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z == <i>i</i></code>.
     */

    public static Apcomplex atan(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return ApfloatMath.atan(z.real());
        }

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, z.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);

        return log(i.add(z).divide(i.subtract(z))).multiply(i).divide(two);
    }

    /**
     * Inverse hyperbolic tangent. Calculated using <code>log()</code>.
     *
     * @param z The argument.
     *
     * @return Inverse hyperbolic tangent of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is 1 or -1.
     */

    public static Apcomplex atanh(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, z.radix());

        return log(one.add(z).divide(one.subtract(z))).divide(two);
    }

    /**
     * Cosine. Calculated using <code>exp()</code>.
     *
     * @param z The argument.
     *
     * @return Cosine of <code>z</code>.
     */

    public static Apcomplex cos(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return ApfloatMath.cos(z.real());
        }

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, z.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one),
                  w = exp(i.multiply(z));

        return (w.add(one.divide(w))).divide(two);
    }

    /**
     * Hyperbolic cosine. Calculated using <code>exp()</code>.
     *
     * @param z The argument.
     *
     * @return Hyperbolic cosine of <code>z</code>.
     */

    public static Apcomplex cosh(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return ApfloatMath.cosh(z.real());
        }

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, z.radix());
        Apcomplex w = exp(z);

        return (w.add(one.divide(w))).divide(two);
    }

    /**
     * Sine. Calculated using <code>exp()</code>.
     *
     * @param z The argument.
     *
     * @return Sine of <code>z</code>.
     */

    public static Apcomplex sin(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return ApfloatMath.sin(z.real());
        }

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, z.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one),
                  w = exp(i.multiply(z));

        return one.divide(w).subtract(w).multiply(i).divide(two);
    }

    /**
     * Hyperbolic sine. Calculated using <code>exp()</code>.
     *
     * @param z The argument.
     *
     * @return Hyperbolic sine of <code>z</code>.
     */

    public static Apcomplex sinh(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return ApfloatMath.sinh(z.real());
        }

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, z.radix());
        Apcomplex w = exp(z);

        return (w.subtract(one.divide(w))).divide(two);
    }

    /**
     * Tangent. Calculated using <code>exp()</code>.
     *
     * @param z The argument.
     *
     * @return Tangent of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is &pi;/2 + n &pi; where n is an integer.
     */

    public static Apcomplex tan(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return ApfloatMath.tan(z.real());
        }

        boolean negate = z.imag().signum() > 0;
        z = (negate ? z.negate() : z);

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, z.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one),
                  w = exp(two.multiply(i).multiply(z));

        w = i.multiply(one.subtract(w)).divide(one.add(w));

        return (negate ? w.negate() : w);
    }

    /**
     * Hyperbolic tangent. Calculated using <code>exp()</code>.
     *
     * @param z The argument.
     *
     * @return Hyperbolic tangent of <code>z</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is <i>i</i> (&pi;/2 + n &pi;) where n is an integer.
     */

    public static Apcomplex tanh(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return ApfloatMath.tanh(z.real());
        }

        boolean negate = z.real().signum() < 0;
        z = (negate ? z.negate() : z);

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, z.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, z.radix());
        Apcomplex w = exp(two.multiply(z));

        w = w.subtract(one).divide(w.add(one));

        return (negate ? w.negate() : w);
    }

    /**
     * Lambert W function. The W function gives the solution to the equation
     * <code>W e<sup>W</sup> = z</code>. Also known as the product logarithm.<p>
     *
     * This function gives the solution to the principal branch, W<sub>0</sub>.
     *
     * @param z The argument.
     *
     * @return <code>W<sub>0</sub>(z)</code>.
     *
     * @since 1.8.0
     */

    public static Apcomplex w(Apcomplex z)
        throws ApfloatRuntimeException
    {
        return LambertWHelper.w(z);
    }

    /**
     * Lambert W function for the specified branch.<p>
     *
     * @param z The argument.
     * @param k The branch.
     *
     * @return <code>W<sub>k</sub>(z)</code>.
     *
     * @exception java.lang.ArithmeticException If <code>z</code> is zero and <code>k</code> is not zero.
     *
     * @see #w(Apcomplex)
     *
     * @since 1.8.0
     */

    public static Apcomplex w(Apcomplex z, long k)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return LambertWHelper.w(z, k);
    }

    /**
     * Product of numbers.
     * The precision used in the multiplications is only
     * what is needed for the end result. This method may
     * perform significantly better than simply multiplying
     * the numbers sequentially.<p>
     *
     * If there are no arguments, the return value is <code>1</code>.
     *
     * @param z The argument(s).
     *
     * @return The product of the given numbers.
     *
     * @since 1.3
     */

    public static Apcomplex product(Apcomplex... z)
        throws ApfloatRuntimeException
    {
        if (z.length == 0)
        {
            return Apcomplex.ONE;
        }

        // Determine working precision
        long maxPrec = Apcomplex.INFINITE;
        for (int i = 0; i < z.length; i++)
        {
            if (z[i].real().signum() == 0 && z[i].imag().signum() == 0)
            {
                return Apcomplex.ZERO;
            }
            maxPrec = Math.min(maxPrec, z[i].precision());
        }

        // Do not use z.clone() as the array might be of some subclass type, resulting in ArrayStoreException later
        Apcomplex[] tmp = new Apcomplex[z.length];

        // Add sqrt length digits for round-off errors
        long extraPrec = (long) Math.sqrt((double) z.length),
             destPrec = ApfloatHelper.extendPrecision(maxPrec, extraPrec);
        for (int i = 0; i < z.length; i++)
        {
            tmp[i] = z[i].precision(destPrec);
        }
        z = tmp;

        // Create a heap, ordered by size
        Queue<Apcomplex> heap = new PriorityQueue<Apcomplex>(z.length, new Comparator<Apcomplex>()
        {
            public int compare(Apcomplex z, Apcomplex w)
            {
                long zSize = z.size(),
                     wSize = w.size();
                return (zSize < wSize ? -1 : (zSize > wSize ? 1 : 0));
            }
        });

        // Perform the multiplications in parallel
        ParallelHelper.ProductKernel<Apcomplex> kernel = new ParallelHelper.ProductKernel<Apcomplex>()
        {
            public void run(Queue<Apcomplex> heap)
            {
                Apcomplex a = heap.remove();
                Apcomplex b = heap.remove();
                Apcomplex c = a.multiply(b);
                heap.add(c);
            }
        };
        ParallelHelper.parallelProduct(z, heap, kernel);

        return ApfloatHelper.setPrecision(heap.remove(), maxPrec);
    }

    /**
     * Sum of numbers.
     * The precision used in the additions is only
     * what is needed for the end result. This method may
     * perform significantly better than simply adding
     * the numbers sequentially.<p>
     *
     * If there are no arguments, the return value is <code>0</code>.
     *
     * @param z The argument(s).
     *
     * @return The sum of the given numbers.
     *
     * @since 1.3
     */

    public static Apcomplex sum(Apcomplex... z)
        throws ApfloatRuntimeException
    {
        if (z.length == 0)
        {
            return Apcomplex.ZERO;
        }

        Apfloat[] x = new Apfloat[z.length],
                  y = new Apfloat[z.length];
        for (int i = 0; i < z.length; i++)
        {
            x[i] = z[i].real();
            y[i] = z[i].imag();
        }
        return new Apcomplex(ApfloatMath.sum(x), ApfloatMath.sum(y));
    }

    // Extend the precision on last iteration
    private static Apcomplex lastIterationExtendPrecision(int iterations, int precisingIteration, Apcomplex z)
    {
        return (iterations == 0 && precisingIteration != 0 ? ApfloatHelper.extendPrecision(z) : z);
    }
}
