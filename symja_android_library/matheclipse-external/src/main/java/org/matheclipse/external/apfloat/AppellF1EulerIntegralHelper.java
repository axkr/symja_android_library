package org.matheclipse.external.apfloat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatInterruptedException;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;

/**
 * Appell's <i>F<sub>1</sub></i> from its Euler integral, for the arguments which the series of
 * {@link ApcomplexAppellMath#appellF1(Apcomplex, Apcomplex, Apcomplex, Apcomplex, Apcomplex, Apcomplex)}
 * and their transformations don't reach:<p>
 *
 * <i>F<sub>1</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i> = &Gamma;(<i>c</i>) / (&Gamma;(<i>a</i>) &Gamma;(<i>c</i> - <i>a</i>))
 * &int;<sub>0</sub><sup>1</sup> <i>t<sup>a-1</sup></i> (1 - <i>t</i>)<sup><i>c-a-1</i></sup> (1 - <i>x t</i>)<sup>-<i>b<sub>1</sub></i></sup> (1 - <i>y t</i>)<sup>-<i>b<sub>2</sub></i></sup> d<i>t</i><p>
 *
 * which holds for Re(<i>c</i>) &gt; Re(<i>a</i>) &gt; 0. The powers are principal values, so for a
 * real argument greater than one the integral is the limit from below the branch cut, the same
 * value as <code>ApcomplexMath.hypergeometric2F1()</code> takes on its cut.<p>
 *
 * The integrand is singular at <i>t</i> = 0, <i>t</i> = 1 and, for a real argument <i>z</i> &ge; 1,
 * at <i>t</i> = 1/<i>z</i>. The interval is cut at these points and every piece is cut once more
 * in its middle, so that each part has a singular point at one end only. Each part is integrated
 * in the distance <i>u</i> from that end, and the factors which vanish there are computed from
 * <i>u</i>: 1 - <i>z t</i> = -<i>z u</i> just above 1/<i>z</i>, rather than as a difference which
 * loses the digits the singularity depends on. The integral exists only if every exponent at a
 * singular point is greater than -1.
 */

class AppellF1EulerIntegralHelper
{
    // Digits beyond the requested precision for the parameters
    private static final long GUARD_DIGITS = 10;

    // The step of the machine precision quadrature is halved at most this many times
    private static final int MACHINE_LEVELS = 8;

    // Relative difference of two machine precision estimates which ends the halving
    private static final double MACHINE_TOLERANCE = 1.0e-13;

    // The largest ratio of the integral of |integrand| to |integral| which the machine precision sum accepts: the samples are only correct relative to the former
    private static final double MAX_MACHINE_CANCELLATION = 100.0;

    // The integral is computed again with the digits it lost at most this many times
    private static final int MAX_ATTEMPTS = 3;

    // A point where the integrand may be singular, with the arguments whose factor vanishes there
    private static final class Point
    {
        final Apfloat t;
        // The sum of the exponents of the factors which vanish at t
        Apcomplex exponent;
        boolean x;
        boolean y;

        Point(Apfloat t, Apcomplex exponent)
        {
            this.t = t;
            this.exponent = exponent;
        }
    }

    // A complex number in double precision, for the machine precision sum
    private static final class MachineComplex
    {
        static final MachineComplex ZERO = new MachineComplex(0.0, 0.0),
                                    ONE = new MachineComplex(1.0, 0.0);

        final double re;
        final double im;

        MachineComplex(double re, double im)
        {
            this.re = re;
            this.im = im;
        }

        MachineComplex(Apcomplex z)
        {
            this(z.real().doubleValue(), z.imag().doubleValue());
        }

        MachineComplex add(MachineComplex z)
        {
            return new MachineComplex(this.re + z.re, this.im + z.im);
        }

        MachineComplex subtract(MachineComplex z)
        {
            return new MachineComplex(this.re - z.re, this.im - z.im);
        }

        MachineComplex multiply(MachineComplex z)
        {
            return new MachineComplex(this.re * z.re - this.im * z.im, this.re * z.im + this.im * z.re);
        }

        MachineComplex multiply(double d)
        {
            return new MachineComplex(this.re * d, this.im * d);
        }

        double abs()
        {
            return Math.hypot(this.re, this.im);
        }

        boolean isFinite()
        {
            return Double.isFinite(this.re) && Double.isFinite(this.im);
        }

        // 1 - z t for a real t
        static MachineComplex oneMinus(MachineComplex z, double t)
        {
            return new MachineComplex(1.0 - z.re * t, -z.im * t);
        }

        // The principal power of a real base, with the argument Pi for a negative one; a -0.0 imaginary part can't turn it into -Pi here
        static MachineComplex pow(double base, MachineComplex w)
        {
            if (w.re == 0.0 && w.im == 0.0)
            {
                return ONE;
            }
            MachineComplex log = (base < 0.0 ? new MachineComplex(Math.log(-base), Math.PI) : new MachineComplex(Math.log(base), 0.0));
            return exp(w.multiply(log));
        }

        static MachineComplex pow(MachineComplex base, MachineComplex w)
        {
            if (base.im == 0.0)
            {
                return pow(base.re, w);
            }
            if (w.re == 0.0 && w.im == 0.0)
            {
                return ONE;
            }
            MachineComplex log = new MachineComplex(Math.log(base.abs()), Math.atan2(base.im, base.re));
            return exp(w.multiply(log));
        }

        static MachineComplex exp(MachineComplex z)
        {
            double e = Math.exp(z.re);
            return new MachineComplex(e * Math.cos(z.im), e * Math.sin(z.im));
        }
    }

    private final Apcomplex aMinus1;
    private final Apcomplex cMinusAMinus1;
    private final Apcomplex minusB1;
    private final Apcomplex minusB2;
    private final Apcomplex x;
    private final Apcomplex y;
    private final Apfloat zero;
    private final Apfloat one;
    // The digits which the machine precision sum found to cancel, or 0
    private long machineCancellation;

    private AppellF1EulerIntegralHelper(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y, long working)
    {
        this.one = new Apfloat(1, working);
        this.zero = new Apfloat(0, working);
        this.aMinus1 = a.subtract(this.one);
        this.cMinusAMinus1 = c.subtract(a).subtract(this.one);
        this.minusB1 = b1.negate();
        this.minusB2 = b2.negate();
        this.x = x;
        this.y = y;
    }

    /**
     * <i>F<sub>1</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i> with <code>precision</code> digits.
     *
     * @param machine If <code>true</code> the integral is summed in <code>double</code>, and only if that doesn't converge in apfloat with <code>precision</code> digits.
     *
     * @throws ArithmeticException If Re(<i>c</i>) &gt; Re(<i>a</i>) &gt; 0 doesn't hold, the integral doesn't exist for these parameters or the quadrature doesn't converge.
     */

    static Apcomplex appellF1(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y, long precision, boolean machine)
        throws ArithmeticException, ApfloatRuntimeException
    {
        // A machine number has fewer digits than the quadrature compares its estimates to; its binary value is exact, so the missing digits are zeros
        long working = precision + GUARD_DIGITS;
        a = ApfloatHelperShim.ensurePrecision(a, working);
        b1 = ApfloatHelperShim.ensurePrecision(b1, working);
        b2 = ApfloatHelperShim.ensurePrecision(b2, working);
        c = ApfloatHelperShim.ensurePrecision(c, working);
        x = ApfloatHelperShim.ensurePrecision(x, working);
        y = ApfloatHelperShim.ensurePrecision(y, working);
        Apcomplex cMinusA = c.subtract(a);
        if (a.real().signum() <= 0 || cMinusA.real().signum() <= 0)
        {
            throw new ArithmeticException("Euler integral of the Appell function needs Re(c) > Re(a) > 0");
        }
        AppellF1EulerIntegralHelper integral = new AppellF1EulerIntegralHelper(a, b1, b2, c, x, y, working);
        List<Point> points = integral.singularPoints();
        if (points == null)
        {
            throw new ArithmeticException("Euler integral of the Appell function diverges");
        }
        try
        {
            Apcomplex gammas = ApcomplexMath.gamma(c).divide(ApcomplexMath.gamma(a).multiply(ApcomplexMath.gamma(cMinusA)));
            Apcomplex sum = (machine ? integral.machineSum(points) : null);
            if (sum != null)
            {
                return gammas.multiply(sum);
            }
            // The parts cancel when the integrand oscillates: apfloat keeps track of the digits which are lost, and the integral is computed again with that many more.
            // The machine precision sum, if it ran, already measured the cancellation
            long digits = precision + integral.machineCancellation;
            Apcomplex value = null;
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
            {
                long attemptWorking = digits + GUARD_DIGITS;
                boolean same = (attemptWorking == working);
                AppellF1EulerIntegralHelper attemptIntegral = (same ? integral : new AppellF1EulerIntegralHelper(ApfloatHelperShim.ensurePrecision(a, attemptWorking), ApfloatHelperShim.ensurePrecision(b1, attemptWorking),
                                                                                                                        ApfloatHelperShim.ensurePrecision(b2, attemptWorking), ApfloatHelperShim.ensurePrecision(c, attemptWorking),
                                                                                                                        ApfloatHelperShim.ensurePrecision(x, attemptWorking), ApfloatHelperShim.ensurePrecision(y, attemptWorking), attemptWorking));
                List<Point> attemptPoints = (same ? points : attemptIntegral.singularPoints());
                sum = Apcomplex.ZERO;
                for (int i = 1; i < attemptPoints.size(); i++)
                {
                    Point lower = attemptPoints.get(i - 1),
                          upper = attemptPoints.get(i);
                    Apfloat half = upper.t.subtract(lower.t).divide(new Apfloat(2, attemptWorking));
                    sum = sum.add(attemptIntegral.part(lower, 1, half, digits));
                    sum = sum.add(attemptIntegral.part(upper, -1, half, digits));
                }
                value = ApfloatHelperShim.ensurePrecision(gammas, attemptWorking).multiply(sum);
                if (value.precision() >= precision || (value.real().signum() == 0 && value.imag().signum() == 0))
                {
                    break;
                }
                digits += precision - value.precision() + GUARD_DIGITS;
            }
            return value;
        }
        catch (ApfloatDoubleExponential.Failure f)
        {
            throw new ArithmeticException("Euler integral of the Appell function doesn't converge");
        }
    }

    // The points 0, 1/z for a real argument z > 1, and 1 in increasing order, or null if the integral doesn't exist
    private List<Point> singularPoints()
    {
        List<Point> points = new ArrayList<>();
        points.add(new Point(this.zero, this.aMinus1));
        points.add(new Point(this.one, this.cMinusAMinus1));
        for (int k = 0; k < 2; k++)
        {
            Apcomplex z = (k == 0 ? this.x : this.y),
                      exponent = (k == 0 ? this.minusB1 : this.minusB2);
            if (z.imag().signum() != 0 || z.real().compareTo(this.one) < 0 || isZero(exponent))
            {
                continue;
            }
            Apfloat t = this.one.divide(z.real());
            Point point = null;
            for (Point p : points)
            {
                if (p.t.compareTo(t) == 0)
                {
                    point = p;
                }
            }
            if (point == null)
            {
                point = new Point(t, Apcomplex.ZERO);
                points.add(point);
            }
            point.exponent = point.exponent.add(exponent);
            if (k == 0)
            {
                point.x = true;
            }
            else
            {
                point.y = true;
            }
        }
        for (Point p : points)
        {
            if (p.exponent.real().compareTo(this.one.negate()) <= 0)
            {
                return null;
            }
        }
        points.sort((p, q) -> p.t.compareTo(q.t));
        return points;
    }

    // The integral from the point near over the distance length in the direction side, which is 1 or -1
    private Apcomplex part(Point near, int side, Apfloat length, long precision)
        throws ApfloatDoubleExponential.Failure
    {
        boolean atZero = (near.t.signum() == 0),
                atOne = (near.t.compareTo(this.one) == 0);
        Apfloat sign = (side > 0 ? this.one : this.one.negate());
        ApfloatDoubleExponential.Sampler integrand = u ->
        {
            try
            {
                Apfloat signedU = u.multiply(sign),
                        t = near.t.add(signedU),
                        tBase = (atZero ? u : t),
                        oneMinusT = (atOne ? u : this.one.subtract(t));
                // 1 - z t = -z (t - 1/z) next to t = 1/z
                Apcomplex xBase = (near.x ? this.x.multiply(signedU).negate() : this.one.subtract(this.x.multiply(t))),
                          yBase = (near.y ? this.y.multiply(signedU).negate() : this.one.subtract(this.y.multiply(t)));
                return pow(tBase, this.aMinus1).multiply(pow(oneMinusT, this.cMinusAMinus1)).multiply(pow(xBase, this.minusB1)).multiply(pow(yBase, this.minusB2));
            }
            catch (ApfloatInterruptedException aie)
            {
                throw aie;
            }
            catch (ArithmeticException | ApfloatRuntimeException e)
            {
                // The integrand has no value at this abscissa
                return null;
            }
        };
        // Three more digits for the sum of the parts
        long digits = precision + 3;
        Apfloat ePlus1 = near.exponent.real().add(this.one);
        if (ePlus1.compareTo(this.one) >= 0)
        {
            return ApfloatDoubleExponential.integrate(integrand, this.zero, endWithDigits(length, digits), digits);
        }
        // u = s^k with k = 1/(e+1) turns u^e du into k s^0 ds: the quadrature can't sample close enough to the end to integrate a strong singularity u^e, -1 < e < 0
        Apfloat k = this.one.divide(ePlus1);
        ApfloatDoubleExponential.Sampler smooth = s ->
        {
            Apfloat u = ApfloatMath.pow(s, k);
            Apcomplex value = integrand.value(u);
            // du = k s^(k-1) ds = k u/s ds
            return (value == null ? null : value.multiply(k.multiply(u).divide(s)));
        };
        return ApfloatDoubleExponential.integrate(smooth, this.zero, endWithDigits(ApfloatMath.pow(length, ePlus1), digits), digits);
    }

    // The end of a part with the digits the quadrature asks for: the distance 1 - 1/z of a singular point next to 1 has lost digits in the subtraction, although 1/z has its working precision
    private static Apfloat endWithDigits(Apfloat end, long digits)
    {
        long needed = digits + 5;
        return (end.precision() < needed ? end.precision(needed) : end);
    }

    // The sum of all parts in double, with the same cuts and substitutions as part(), or null if a part doesn't converge to machine precision or the parts cancel
    private Apcomplex machineSum(List<Point> points)
    {
        MachineComplex dA1 = new MachineComplex(this.aMinus1),
                       dCA1 = new MachineComplex(this.cMinusAMinus1),
                       dB1 = new MachineComplex(this.minusB1),
                       dB2 = new MachineComplex(this.minusB2),
                       dx = new MachineComplex(this.x),
                       dy = new MachineComplex(this.y),
                       sum = MachineComplex.ZERO;
        // The integral of the absolute value of the integrand: the rounding errors of the samples are relative to it, not to the sum
        double[] absolute = new double[1];
        double totalAbsolute = 0.0;
        for (int i = 1; i < points.size(); i++)
        {
            Point lower = points.get(i - 1),
                  upper = points.get(i);
            double half = (upper.t.doubleValue() - lower.t.doubleValue()) / 2.0;
            for (int side = 1; side >= -1; side -= 2)
            {
                Point near = (side > 0 ? lower : upper);
                double nearT = near.t.doubleValue(),
                       sign = side,
                       ePlus1 = near.exponent.real().doubleValue() + 1.0,
                       k = (ePlus1 < 1.0 ? 1.0 / ePlus1 : 1.0);
                boolean atZero = (near.t.signum() == 0),
                        atOne = (near.t.compareTo(this.one) == 0);
                DoubleFunction<MachineComplex> integrand = s ->
                {
                    double u = (k == 1.0 ? s : Math.pow(s, k));
                    if (u == 0.0)
                    {
                        // Underflow next to the end
                        return null;
                    }
                    double t = nearT + sign * u;
                    MachineComplex value = MachineComplex.pow(atZero ? u : t, dA1).multiply(MachineComplex.pow(atOne ? u : 1.0 - t, dCA1))
                                                        .multiply(MachineComplex.pow(near.x ? dx.multiply(-sign * u) : MachineComplex.oneMinus(dx, t), dB1))
                                                        .multiply(MachineComplex.pow(near.y ? dy.multiply(-sign * u) : MachineComplex.oneMinus(dy, t), dB2));
                    return (k == 1.0 ? value : value.multiply(k * u / s));
                };
                MachineComplex part = tanhSinh(integrand, ePlus1 < 1.0 ? Math.pow(half, ePlus1) : half, absolute);
                if (part == null)
                {
                    return null;
                }
                sum = sum.add(part);
                totalAbsolute += absolute[0];
            }
        }
        if (!(totalAbsolute <= MAX_MACHINE_CANCELLATION * sum.abs()))
        {
            // An oscillating integrand, or parts which cancel: the rounding errors of the samples exceed the machine precision of the sum.
            // The ratio tells how many digits the apfloat quadrature will lose as well
            double ratio = totalAbsolute / sum.abs();
            this.machineCancellation = (Double.isFinite(ratio) ? (long) Math.ceil(Math.log10(ratio)) : 0);
            return null;
        }
        return new Apcomplex(new Apfloat(sum.re), new Apfloat(sum.im));
    }

    // Tanh-sinh quadrature over [0, length] in double; an abscissa next to zero is computed from its distance to zero. Null if the estimates don't agree to machine precision or a sample isn't finite.
    // absolute[0] receives the quadrature of the absolute value of the integrand
    private static MachineComplex tanhSinh(DoubleFunction<MachineComplex> f, double length, double[] absolute)
    {
        double halfPi = Math.PI / 2.0,
               h = 1.0,
               absoluteSum = 0.0;
        MachineComplex sum = MachineComplex.ZERO,
                       previous = null;
        for (int level = 0; level <= MACHINE_LEVELS; level++)
        {
            ApfloatHelperShim.checkInterrupted();
            int step = (level == 0 ? 1 : 2);
            for (int sign = 1; sign >= -1; sign -= 2)
            {
                int start = (level == 0 ? (sign > 0 ? 0 : 1) : 1),
                    negligible = 0;
                double largest = 0.0;
                for (int j = start; ; j += step)
                {
                    double t = sign * j * h;
                    if (Math.abs(t) > 4.0)
                    {
                        break;
                    }
                    double v = halfPi * Math.sinh(t),
                           s = length / (1.0 + Math.exp(-2.0 * v));
                    if (s <= 0.0 || s >= length)
                    {
                        // The abscissa merged with an end
                        break;
                    }
                    double coshV = Math.cosh(v),
                           weight = length * halfPi * Math.cosh(t) / (2.0 * coshV * coshV);
                    MachineComplex value = f.apply(s);
                    if (value == null)
                    {
                        break;
                    }
                    if (!value.isFinite())
                    {
                        return null;
                    }
                    MachineComplex term = value.multiply(weight);
                    sum = sum.add(term);
                    double magnitude = term.abs();
                    absoluteSum += magnitude;
                    largest = Math.max(largest, magnitude);
                    if (magnitude <= 1.0e-18 * largest)
                    {
                        if (++negligible >= 3)
                        {
                            break;
                        }
                    }
                    else
                    {
                        negligible = 0;
                    }
                }
            }
            MachineComplex estimate = sum.multiply(h);
            if (previous != null && estimate.subtract(previous).abs() <= MACHINE_TOLERANCE * estimate.abs())
            {
                absolute[0] = absoluteSum * h;
                return estimate;
            }
            previous = estimate;
            h /= 2.0;
        }
        return null;
    }

    private static boolean isZero(Apcomplex z)
    {
        return z.real().signum() == 0 && z.imag().signum() == 0;
    }

    private static Apcomplex pow(Apcomplex z, Apcomplex w)
    {
        if (isZero(w))
        {
            return Apcomplex.ONE;
        }
        return ApcomplexMath.pow(z, w);
    }
}
