package org.apfloat;

import static org.apfloat.spi.RadixConstants.*;

import org.apfloat.spi.Util;

/**
 * Helper class for Lambert W function.
 *
 * @since 1.8.0
 * @version 1.8.1
 * @author Mikko Tommila
 */

class LambertWHelper {
    private static class ComplexException
        extends Exception
    {
        public ComplexException(ArithmeticException cause)
        {
            super(cause);
        }

        public ArithmeticException getCause()
        {
            return (ArithmeticException) super.getCause();
        }

        private static final long serialVersionUID = 1;
    }

    private static final double BRANCH_POINT_BEYOND = -0.3678794411714460;  // Behind branch point on real axis (-1/e = -0.36787944117144233)

    private LambertWHelper(Apcomplex z, long k)
    {
        this.targetPrecision = z.precision();   // The final precision; might be less, but the intermediate steps must still be done to the full precision
        this.precision = ApfloatHelper.extendPrecision(this.targetPrecision);   // The working precision; must be extended because of accumulated loss of precision
        this.radix = z.radix();
        this.z = ApfloatHelper.ensurePrecision(z, this.precision);
        if (z.imag().signum() == 0)
        {
            this.x = z.real();
        }
        this.k = k;
        this.minusOne = new Apint(-1, this.radix);
        this.one = new Apint(1, this.radix);
        this.two = new Apint(2, this.radix);
        this.three = new Apint(3, this.radix);
        this.minusOnePerE = new Apfloat(BRANCH_POINT_BEYOND, Apfloat.DEFAULT, this.radix);  // Slightly beyond the limit to cater for round-off errors
        Apfloat distSquare = new Apfloat(1e-8, Apfloat.DEFAULT, this.radix);                // How close is "close" to the branch point, squared; one fourth of double precision
        this.close = ApcomplexMath.norm(z.subtract(this.minusOnePerE)).compareTo(distSquare) <= 0;

        // In case of large k, the precision (of the imaginary part) is extended
        if (k != 0)
        {
            // If 2 k pi is 10 or more, then increase precision by how many extra digits 2 k pi has
            double precisionIncrease = Math.log(Math.abs((double) k) * 2 * Math.PI) / Math.log((double) this.radix);
            this.precision = ApfloatHelper.extendPrecision(this.precision, (long) precisionIncrease);
            this.targetPrecision = ApfloatHelper.extendPrecision(this.targetPrecision, (long) precisionIncrease);
        }

        if ((this.z.real().signum() != 0 || this.z.imag().signum() != 0) && this.precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate W to infinite precision");
        }
    }

    public static Apfloat w(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return new LambertWHelper(x, 0).real();
    }

    public static Apcomplex w(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return w(z, 0);
    }

    public static Apcomplex w(Apcomplex z, long k)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return new LambertWHelper(z, k).complex();
    }

    public Apfloat real()
        throws ArithmeticException, ApfloatRuntimeException
    {
        try
        {
            return doReal();
        }
        catch (ComplexException ce)
        {
            throw ce.getCause();
        }
    }

    private Apfloat doReal()
        throws ComplexException, ApfloatRuntimeException
    {
        assert (this.k == 0 || this.x.signum() < 0);
        if (this.x.signum() == 0)
        {
            return this.x;
        }

        // Initial value
        Apfloat w;
        long initialPrecision = DOUBLE_PRECISION[this.x.radix()];
        Apfloat initialX = this.x.precision(initialPrecision);
        long digits = 0;
        long oldAccuracy = 0;
        if (this.x.compareTo(this.minusOnePerE) < 0)
        {
            // We know for sure here, but it could still be, even if we don't notice it here
            throw new ComplexException(new ArithmeticException("Result would be complex"));
        }
        else if (this.k == -1 && !this.close)
        {
            // Real valued W_-1: log(-x) - log(-log(-x)), -1/e <= x < 0 (not very close to the branch point)
            Apfloat logx = ApfloatMath.log(initialX.negate());
            w = logx.subtract(ApfloatMath.log(logx.negate()));
        }
        else if (this.k == -1)
        {
            // W_-1 close to the branch point -1/e
            w = negativeRealSeries();
            digits = oldAccuracy = w.precision();
            w = w.precision(shiftLeftPrecision(digits, 2));
        }
        else if (this.x.scale() > 1)
        {
            // Large values
            Apfloat logx = ApfloatMath.log(initialX);
            w = logx.subtract(ApfloatMath.log(logx));
        }
        else if (this.x.signum() > 0 && this.x.scale() >= 0)
        {
            // Moderate values
            w = ApfloatMath.log(initialX.add(this.one));
        }
        else if (!this.close)
        {
            // Small but not very close to the branch point; possibly close to zero
            w = initialX;
        }
        else
        {
            // Close to the branch point -1/e
            w = positiveRealSeries();
            digits = oldAccuracy = w.precision();
            w = w.precision(shiftLeftPrecision(digits, 2));
        }
        if (!this.close)
        {
            // If x is slightly close to -1/e, but not very close, precision of the result is still reduced
            this.targetPrecision -= (this.x.equalDigits(this.minusOnePerE) + 1 ) / 2;
        }
        this.targetPrecision = Math.max(this.targetPrecision, 1);   // In case input is -0.3 with precision 1

        boolean done = (digits >= this.targetPrecision);
        if (!done)
        {
            // Precalculate the needed values once to the required precision
            ApfloatMath.logRadix(this.targetPrecision, this.radix);
        }

        // Fritsch's iteration (quartic)
        // For values close to -1/e the convergence is initially worse but eventually becomes quartic
        Apfloat oldW;
        boolean converges = false;              // If there are at least a few correct digits in the result
        for (int i = 0; i < 50 && !done; i++)   // Should be enough iterations
        {
            oldW = w;

            // Calculate one Fritsch's iteration
            Apfloat z = ApfloatMath.log(this.x.divide(w)).subtract(w);
            Apfloat w1 = this.one.add(w);
            Apfloat q = z.multiply(this.two).divide(this.three).add(w1).multiply(w1).multiply(this.two);
            Apfloat e = z.divide(w1).multiply(q.subtract(z)).divide(q.subtract(this.two.multiply(z)));

            // Check the accuracy of the result, initially convergence can be slow but becomes quartic eventually
            long accuracy = (converges ? -e.scale() : digits);
            double rate = Math.min(Math.max(accuracy / Math.max(1.0, oldAccuracy), 1.0), 4.0);
            if (accuracy >= this.targetPrecision / rate)
            {
                done = true;
            }
            oldAccuracy = accuracy;
            w = w.multiply(this.one.add(e));

            // If x was close to the limit and we did not notice it earlier, w may now have diverged
            if (this.k == 0 && w.compareTo(this.minusOne) < 0 || this.k == -1 && w.compareTo(this.minusOne) > 0)
            {
                throw new ComplexException(new ArithmeticException("Result would be complex"));
            }

            // Check the convergence
            if (!converges)
            {
                digits = w.equalDigits(oldW);
                accuracy = digits;
                converges = (digits >= initialPrecision / 4);
            }
            if (converges)
            {
                w = w.precision(shiftLeftPrecision(accuracy, 4, Apcomplex.EXTRA_PRECISION));
            }
        }

        return w.precision(this.targetPrecision);
    }

    public Apcomplex complex()
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (this.z.real().signum() == 0 && this.z.imag().signum() == 0)
        {
            if (this.k == 0)
            {
                return this.z;
            }
            else
            {
                throw new ArithmeticException("W_" + this.k + " of zero");
            }
        }

        // To compute the initial value, we use the algorithms from
        // "On the Lambert W Function" (https://cs.uwaterloo.ca/research/tr/1993/03/W.pdf) by R. M. Corless et al.:
        // - (4.20): near zero and near infinity (i.e. all cases not specifically mentioned below):
        //   W_k(z) = log(z) + 2*pi*k*i-log(log(z) + 2*pi*k*i)
        // - (4.22): anywhere near the branch point -1/e (very close) for W_0:
        //   p = sqrt(2*(e*z+1))
        //   W_0(z) = -1 + p - 1/3 p^2 + 11/72 p^3 ...
        // - Similarly near the branch point, for W_-1, when imag(z) >= 0, and also for W_1 when imag(z) < 0:
        //   p = -sqrt(2*(e*z+1))
        // - (Remark after 4.20): for real-valued W_-1(x):
        //   W_-1(x) = log(-x) - log(-log(-x))
        // Note that we don't necessarily need a very accurate initial guess; the iteration only needs to converge (to the correct branch).
        // Also note that precision is greatly reduced for k = 0, 1, -1 near the branch point -1/e (only on one side for k = +-1)
        Apcomplex w;
        long initialPrecision = DOUBLE_PRECISION[this.radix];
        // If there is an imaginary part, we must keep it and not set it to zero, to get correct log() result later
        Apcomplex initialZ = ApfloatHelper.limitPrecision(this.z, initialPrecision);
        long digits = 0;
        long oldAccuracy = 0;
        try
        {
            // Check if it's a real valued argument, or at least very close to the branch point if it's beyond it
            if (this.k == 0 && this.z.imag().signum() == 0 && this.z.real().compareTo(this.minusOnePerE) > 0)
            {
                // Real valued W_0, x >= -1/e
                return doReal();
            }
            else if (this.k == -1 && this.z.imag().signum() == 0 && this.z.real().signum() < 0 && this.z.real().compareTo(this.minusOnePerE) > 0)
            {
                // Real valued W_-1, -1/e <= x < 0
                return doReal();
            }
        }
        catch (ComplexException ce)
        {
            // Value was actually slightly beyond the branch point, retry as complex valued function
        }

        if (this.k == 0 && this.z.scale() < 0)
        {
            // Small and close to zero
            w = initialZ;
        }
        else if (this.k == 0 && this.z.scale() < 1 && !this.close)
        {
            // Moderate values close to zero but not close to branch point
            w = ApcomplexMath.log(initialZ.add(this.one));
        }
        else if (this.k > 1 || this.k < -1 || !this.close)
        {
            // Close to zero, or close to infinity, or complex branches (i.e. not close to the branch point)
            w = logApprox(initialZ);
        }
        else if (this.k == 0)
        {
            // Close to the branch point -1/e
            w = positiveComplexSeries();
            digits = oldAccuracy = w.precision();
            w = w.precision(shiftLeftPrecision(digits, 2));
        }
        else if (this.k == -1 && this.z.imag().signum() >= 0 ||
                 this.k == 1 && this.z.imag().signum() < 0)
        {
            // Close to the branch point -1/e
            w = negativeComplexSeries();
            digits = oldAccuracy = w.precision();
            w = w.precision(shiftLeftPrecision(digits, 2));
        }
        else
        {
            // Close to the branch point on W_1 or W_-1 but on the the side where the default approximation works
            w = logApprox(initialZ);
        }
        if (!this.close && (this.k == 0 ||
                            this.k == -1 && this.z.imag().signum() >= 0 ||
                            this.k == 1 && this.z.imag().signum() < 0))
        {
            // If x is slightly close to the branch point (from the correct side), but not very close, precision is still reduced
            this.targetPrecision -= (this.z.equalDigits(this.minusOnePerE) + 1) / 2;
        }
        this.targetPrecision = Math.max(this.targetPrecision, 1);   // In case input is -0.3 with precision 1

        boolean done = (digits >= this.targetPrecision);
        if (!done)
        {
            // Precalculate the needed values once to the required precision
            ApfloatMath.logRadix(this.targetPrecision, this.radix);
        }

        // Fritsch's iteration (quartic)
        // For values close to -1/e the convergence is initially worse but eventually becomes quartic
        Apcomplex oldW;
        boolean converges = false;              // If there are at least a few correct digits in the result
        for (int j = 0; j < 50 && !done; j++)   // Should be enough iterations
        {
            oldW = w;

            // Calculate one Fritsch's iteration
            // In order to pick the correct branch of the logarithm, we have to look at the expanded format of the formula
            // See: F. N. Fritsch, R. E. Shafer, and W. P. Crowley, "Algorithm 443: Solution of the transcendental equation we^w = x", Communications of the ACM, 16 (1973), p. 123-124
            Apcomplex z = fixLogBranch(log(this.z.divide(w)), w).subtract(w);
            Apcomplex w1 = this.one.add(w);
            Apcomplex q = z.multiply(this.two).divide(this.three).add(w1).multiply(w1).multiply(this.two);
            Apcomplex e = z.divide(w1).multiply(q.subtract(z)).divide(q.subtract(this.two.multiply(z)));

            // Check the accuracy of the result, initially convergence can be slow but becomes quartic eventually
            long accuracy = (converges ? -e.scale() : digits);
            double rate = Math.min(Math.max(accuracy / Math.max(1.0, oldAccuracy), 1.0), 4.0);
            if (accuracy >= this.targetPrecision / rate)
            {
                done = true;
            }
            oldAccuracy = accuracy;
            w = w.multiply(this.one.add(e));

            // Check the convergence
            if (!converges)
            {
                digits = w.equalDigits(oldW);
                accuracy = digits;
                converges = (digits >= initialPrecision / 4);
            }
            if (converges)
            {
                w = w.precision(shiftLeftPrecision(accuracy, 4, Apcomplex.EXTRA_PRECISION));
            }
        }

        return w.precision(this.targetPrecision);
    }

    private Apcomplex log(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apcomplex logz = ApcomplexMath.log(z);
        if (this.k != 0)
        {
            // log(z) + 2*pi*k*i
            Apcomplex offset = new Apcomplex(Apfloat.ZERO, twoPiK());
            logz = logz.add(offset);
        }
        return logz;
    }

    private Apcomplex logApprox(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        // W_k(z) = log(z) + 2*pi*k*i - log(log(z) + 2*pi*k*i)
        Apcomplex logz = log(z);
        Apcomplex w = logz.subtract(ApcomplexMath.log(logz));
        return w;
    }

    private Apcomplex p()
        throws ApfloatRuntimeException
    {
        // p = sqrt(2 (e z + 1))
        if (this.p == null)
        {
            Apfloat e = new Apfloat(Math.E, Apfloat.DEFAULT, this.radix);
            this.p = ApcomplexMath.sqrt(this.two.multiply(e.multiply(this.z).add(this.one)));
            long precision = e.precision();
            while (this.p.precision() <= -this.p.scale() && precision < this.precision)
            {
                // Very close to the branch point; need to get a better approximation
                precision = shiftLeftPrecision(precision, 1);
                this.p = ApcomplexMath.sqrt(this.two.multiply(e(precision).multiply(this.z).add(this.one)));
            }
            // Target precision is reduced very near the branch point
            if (this.p.real().signum() == 0 && this.p.imag().signum() == 0)
            {
                this.targetPrecision /= 2;
            }
            else
            {
                this.targetPrecision += this.p.scale() - 1;
            }
        }
        return this.p;
    }

    private Apfloat positiveRealSeries()
        throws ComplexException, ApfloatRuntimeException
    {
        return realSeries(p());
    }

    private Apfloat negativeRealSeries()
        throws ComplexException, ApfloatRuntimeException
    {
        return realSeries(p().negate());
    }

    private Apcomplex positiveComplexSeries()
        throws ApfloatRuntimeException
    {
        return complexSeries(p());
    }

    private Apcomplex negativeComplexSeries()
        throws ApfloatRuntimeException
    {
        return complexSeries(p().negate());
    }

    private Apfloat realSeries(Apcomplex p)
        throws ComplexException, ApfloatRuntimeException
    {
        if (p.imag().signum() != 0)
        {
            throw new ComplexException(new ArithmeticException("Result would be complex"));
        }
        return complexSeries(p).real();
    }

    private Apcomplex complexSeries(Apcomplex p)
        throws ApfloatRuntimeException
    {
        // -1 + p - 1/3 p^2 + 11/72 p^3 + ...
        Aprational factor2 = new Aprational(this.one, this.three);
        Aprational factor3 = new Aprational(new Apint(11, this.radix), new Apint(72, this.radix));
        Apcomplex p2 = p.multiply(p);
        Apcomplex p3 = p2.multiply(p);
        Apcomplex w = this.minusOne.add(p).subtract(factor2.multiply(p2)).add(factor3.multiply(p3));
        // Error term is O(p^4) so the maximum precision from the above is -4 * p.scale(), but could be less if p is less accurate
        long seriesPrecision = Util.ifFinite(-p3.scale(), -p3.scale() - p.scale());
        w = w.precision(Math.min(w.precision(), seriesPrecision));
        return w;
    }

    // Multiply the precision and check for overflow, as ifFinite() only works reliably if the result is at most twice the original number
    private long shiftLeftPrecision(long precision, int i)
    {
        return shiftLeftPrecision(precision, i, 0);
    }

    private long shiftLeftPrecision(long precision, int i, long add)
    {
        while (--i >= 0)
        {
            precision = ApfloatHelper.extendPrecision(precision, precision);
        }
        return ApfloatHelper.extendPrecision(precision, add);
    }

    // Usually we get the correct branch of W by adding 2 k pi i to the logarithm taken previously,
    // however sometimes the result just is off by either 2 pi i or -2 pi i and we have to adjust.
    // Assuming that our initial guess is close enough to the correct branch value, if the 
    // next value differs by more than pi i, then we adjust by +-2 pi i, and can get the result to
    // converge to the correct branch of W.
    // Because we use log(z / w) instead of log(z) - log(w) the branch of the logarithm behaves
    // quite in a nontrivial way, so this is a heuristic algorithm.
    // This problem seems to apply only to the Fritsch algorithm as it uses log(); other algorithms
    // using exp() don't seem to suffer from this issue (like Halley's method).
    private Apcomplex fixLogBranch(Apcomplex next, Apcomplex previous)
        throws ApfloatRuntimeException
    {
        if (this.k != 0)
        {
            int comparePrecision = DOUBLE_PRECISION[next.radix()];
            double diff = next.imag().precision(comparePrecision).subtract(previous.imag()).doubleValue();
            if (diff < -Math.PI)
            {
                Apcomplex twoPiI = new Apcomplex(Apfloat.ZERO, twoPi());
                next = next.add(twoPiI);
            }
            else if (diff > Math.PI)
            {
                Apcomplex twoPiI = new Apcomplex(Apfloat.ZERO, twoPi());
                next = next.subtract(twoPiI);
            }
        }
        return next;
    }

    private Apfloat twoPi()
        throws ApfloatRuntimeException
    {
        if (this.twoPi == null)
        {
            this.twoPi = this.two.multiply(ApfloatMath.pi(this.precision, this.radix));
        }
        return this.twoPi;
    }

    private Apfloat twoPiK()
        throws ApfloatRuntimeException
    {
        if (this.twoPiK == null)
        {
            this.twoPiK = twoPi().multiply(new Apint(this.k, this.radix));
        }
        return this.twoPiK;
    }

    private Apfloat e(long precision)
        throws ApfloatRuntimeException
    {
        if (this.e == null || this.e.precision() < precision)
        {
            this.e = ApfloatMath.exp(new Apfloat(1, precision, this.radix));
        }
        return this.e;
    }

    private Apfloat x;
    private Apcomplex z;
    private int radix;
    private long precision;
    private long targetPrecision;
    private long k;
    private boolean close;
    private Apint minusOne;
    private Apint one;
    private Apint two;
    private Apint three;
    private Apfloat twoPi;
    private Apfloat twoPiK;
    private Apfloat e;
    private Apfloat minusOnePerE;
    private Apcomplex p;
}
