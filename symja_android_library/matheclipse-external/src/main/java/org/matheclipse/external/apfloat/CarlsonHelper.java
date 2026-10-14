package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * Helper class for Carlson's symmetric elliptic integrals.<p>
 *
 * The integrals are computed with the duplication algorithms of B. C. Carlson,
 * "Numerical computation of real or complex elliptic integrals", Numerical Algorithms 10
 * (1995), pp. 13-26. The duplication is repeated until the truncation error of the
 * terminating series is below the working precision, so the number of iterations grows
 * linearly with the precision, and every iteration takes three or four square roots.
 *
 * @implNote
 * This implementation is <i>slow</i>, meaning that it isn't a <i>fast algorithm</i>.
 * It is impractically slow beyond a precision of a few thousand digits.
 */

class CarlsonHelper
{
    private CarlsonHelper()
    {
    }

    // The natural logarithm of four, the factor by which the duplication contracts
    private static final double LOG4 = Math.log(4.0);

    /**
     * R<sub>F</sub>(x, y, z).
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     * @param precision The precision of the result.
     *
     * @return R<sub>F</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If the precision is infinite.
     * @throws ArithmeticException If more than one of the arguments is zero.
     */

    public static Apcomplex rf(Apcomplex x, Apcomplex y, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        checkPrecision(precision);
        if (zeros(x, y, z) >= 2)
        {
            throw new ApfloatArithmeticException("Carlson RF is infinite", "carlson.rf.infinite");
        }
        int radix = x.radix();
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        Apfloat three = new Apfloat(3, workingPrecision, radix),
                quarter = quarter(workingPrecision, radix);
        x = ApfloatHelperShim.ensurePrecision(x, workingPrecision);
        y = ApfloatHelperShim.ensurePrecision(y, workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);

        Apcomplex a0 = ensure(x.add(y).add(z), workingPrecision).divide(three),
                  xm = x,
                  ym = y,
                  zm = z,
                  am = a0;
        Apfloat q = max(ApcomplexMath.abs(a0.subtract(x)), ApcomplexMath.abs(a0.subtract(y)), ApcomplexMath.abs(a0.subtract(z))),
                power = new Apfloat(1, workingPrecision, radix);    // 4^(-m)
        // The truncation error of the series is of the order of (4^(-m) q / |Am|)^6 / 3
        double target = workingPrecision * Math.log(radix) / 6.0 - Math.log(3.0) / 6.0;
        for (long m = 0; !isConverged(q, am, m, target); m++)
        {
            Apcomplex λ = lambda(sqrt(xm, workingPrecision), sqrt(ym, workingPrecision), sqrt(zm, workingPrecision));
            xm = ensure(xm.add(λ), workingPrecision).multiply(quarter);
            ym = ensure(ym.add(λ), workingPrecision).multiply(quarter);
            zm = ensure(zm.add(λ), workingPrecision).multiply(quarter);
            am = ensure(am.add(λ), workingPrecision).multiply(quarter);
            power = power.multiply(quarter);
        }

        // X = (A0 - x) / (4^m Am)
        Apcomplex t = power.divide(am),
                  X = ensure(a0.subtract(x), workingPrecision).multiply(t),
                  Y = ensure(a0.subtract(y), workingPrecision).multiply(t),
                  Z = X.add(Y).negate(),
                  E2 = X.multiply(Y).subtract(Z.multiply(Z)),
                  E3 = X.multiply(Y).multiply(Z);
        // (1 - E2/10 + E3/14 + E2^2/24 - 3 E2 E3/44) / sqrt(Am)
        Apcomplex series = ensure(new Apfloat(1, workingPrecision, radix)
                               .subtract(E2.divide(new Apfloat(10, workingPrecision, radix)))
                               .add(E3.divide(new Apfloat(14, workingPrecision, radix)))
                               .add(E2.multiply(E2).divide(new Apfloat(24, workingPrecision, radix)))
                               .subtract(three.multiply(E2).multiply(E3).divide(new Apfloat(44, workingPrecision, radix))), workingPrecision);
        Apcomplex result = series.divide(sqrt(am, workingPrecision));
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    /**
     * R<sub>C</sub>(x, y) = R<sub>F</sub>(x, y, y). For a negative real <code>y</code>
     * the Cauchy principal value is returned.
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param precision The precision of the result.
     *
     * @return R<sub>C</sub>(x, y)
     *
     * @throws InfiniteExpansionException If the precision is infinite.
     * @throws ArithmeticException If <code>y</code> is zero.
     */

    public static Apcomplex rc(Apcomplex x, Apcomplex y, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        checkPrecision(precision);
        if (y.isZero())
        {
            throw new ApfloatArithmeticException("Carlson RC is infinite", "carlson.rc.infinite");
        }
        if (y.imag().signum() == 0 && y.real().signum() < 0)
        {
            // The Cauchy principal value is sqrt(x / (x - y)) RC(x - y, -y)
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
            x = ApfloatHelperShim.ensurePrecision(x, workingPrecision);
            y = ApfloatHelperShim.ensurePrecision(y, workingPrecision);
            Apcomplex xy = ensure(x.subtract(y), workingPrecision);
            if (x.isZero())
            {
                return ApfloatHelperShim.ZEROS[x.radix()];
            }
            Apcomplex result = sqrt(x.divide(xy), workingPrecision).multiply(rf(xy, y.negate(), y.negate(), workingPrecision));
            return ApfloatHelperShim.limitPrecision(result, precision);
        }
        return rf(x, y, y, precision);
    }

    /**
     * R<sub>D</sub>(x, y, z) = R<sub>J</sub>(x, y, z, z).
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     * @param precision The precision of the result.
     *
     * @return R<sub>D</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If the precision is infinite.
     * @throws ArithmeticException If <code>z</code> is zero or both <code>x</code> and <code>y</code> are zero.
     */

    public static Apcomplex rd(Apcomplex x, Apcomplex y, Apcomplex z, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return rj(x, y, z, z, precision);
    }

    /**
     * R<sub>J</sub>(x, y, z, p).
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     * @param p The fourth argument.
     * @param precision The precision of the result.
     *
     * @return R<sub>J</sub>(x, y, z, p); the Cauchy principal value for a negative <code>p</code>
     *
     * @throws InfiniteExpansionException If the precision is infinite.
     * @throws ArithmeticException If <code>p</code> is zero or more than one of <code>x</code>, <code>y</code> and <code>z</code> is zero.
     */

    public static Apcomplex rj(Apcomplex x, Apcomplex y, Apcomplex z, Apcomplex p, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        checkPrecision(precision);
        if (p.isZero() || zeros(x, y, z) >= 2)
        {
            throw new ApfloatArithmeticException("Carlson RJ is infinite", "carlson.rj.infinite");
        }
        int radix = x.radix();
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        x = ApfloatHelperShim.ensurePrecision(x, workingPrecision);
        y = ApfloatHelperShim.ensurePrecision(y, workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        p = ApfloatHelperShim.ensurePrecision(p, workingPrecision);

        Apcomplex result;
        // A negative p is a pole on the path of integration, of which the Cauchy principal value is taken;
        // not if one of the roots has its branch point on the path as well
        boolean principalValue = p.imag().signum() == 0 && p.real().signum() < 0 && !negative(x) && !negative(y) && !negative(z);
        if (rjDuplicates(x, y, z, p))
        {
            result = rjDuplication(x, y, z, p, workingPrecision);
            if (principalValue)
            {
                // x, y and z are not negative: the residue is real, and passing the pole on either side adds an imaginary number
                result = result.real();
            }
        }
        else
        {
            // The duplication may end on another sheet. Then it differs from the integral by a multiple of the
            // residue at the pole, and a few digits of the integral tell which multiple
            Apcomplex duplication = rjDuplication(x, y, z, p, workingPrecision),
                      reference = rjLine(x, y, z, p, REFERENCE_PRECISION),
                      root = sqrt(ensure(x.subtract(p), workingPrecision), workingPrecision)
                             .multiply(sqrt(ensure(y.subtract(p), workingPrecision), workingPrecision))
                             .multiply(sqrt(ensure(z.subtract(p), workingPrecision), workingPrecision)),
                      // 2 pi i times the residue 3/2 / (sqrt(x - p) sqrt(y - p) sqrt(z - p))
                      unit = new Apcomplex(Apfloat.ZERO, new Apfloat(3, workingPrecision, radix).multiply(ApfloatMath.pi(workingPrecision, radix))).divide(root),
                      ratio = reference.subtract(duplication).precision(REFERENCE_PRECISION).divide(unit.precision(REFERENCE_PRECISION));
            double multiple = Math.rint(ratio.real().doubleValue());
            if (Math.abs(ratio.real().doubleValue() - multiple) < 1.0e-6 && Math.abs(ratio.imag().doubleValue()) < 1.0e-6 && Math.abs(multiple) < 1.0e6)
            {
                result = (multiple == 0.0 ? duplication : duplication.add(unit.multiply(new Apfloat((long) multiple, workingPrecision, radix))));
            }
            else
            {
                result = rjLine(x, y, z, p, workingPrecision);
            }
            if (principalValue)
            {
                // The line passes above the pole, a path below it has 2 pi i times the residue more: the mean of the two
                result = result.add(unit.divide(new Apfloat(2, workingPrecision, radix)));
            }
        }
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    // The digits of the integral which decide the sheet
    private static final long REFERENCE_PRECISION = 12;

    // RJ as the integral from zero to a number N along a line, and the duplication for the rest, whose arguments are
    // in the right half plane
    private static Apcomplex rjLine(Apcomplex x, Apcomplex y, Apcomplex z, Apcomplex p, long workingPrecision)
    {
        x = x.precision(workingPrecision);
        y = y.precision(workingPrecision);
        z = z.precision(workingPrecision);
        p = p.precision(workingPrecision);
        Apcomplex end = rjEnd(x, y, z, p, workingPrecision),
                  initial = (workingPrecision == REFERENCE_PRECISION ? rjIntegral(x, y, z, p, end) : null);
        if (initial == null)
        {
            initial = rjIntegral(x, y, z, p, end, workingPrecision);
        }
        return initial.add(rjDuplication(ensure(x.add(end), workingPrecision), ensure(y.add(end), workingPrecision), ensure(z.add(end), workingPrecision), ensure(p.add(end), workingPrecision), workingPrecision));
    }

    private static Apcomplex rjDuplication(Apcomplex x, Apcomplex y, Apcomplex z, Apcomplex p, long workingPrecision)
    {
        int radix = x.radix();
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                two = new Apfloat(2, workingPrecision, radix),
                five = new Apfloat(5, workingPrecision, radix),
                quarter = quarter(workingPrecision, radix),
                sixtyFourth = quarter.multiply(quarter).multiply(quarter);
        Apcomplex a0 = ensure(x.add(y).add(z).add(two.multiply(p)), workingPrecision).divide(five),
                  δ = ensure(p.subtract(x), workingPrecision).multiply(ensure(p.subtract(y), workingPrecision)).multiply(ensure(p.subtract(z), workingPrecision)),
                  xm = x,
                  ym = y,
                  zm = z,
                  pm = p,
                  am = a0,
                  sum = ApfloatHelperShim.ZEROS[radix];
        Apfloat q = max(max(ApcomplexMath.abs(a0.subtract(x)), ApcomplexMath.abs(a0.subtract(y)), ApcomplexMath.abs(a0.subtract(z))), ApcomplexMath.abs(a0.subtract(p)), ApfloatHelperShim.ZEROS[radix]),
                power = one,        // 4^(-m)
                power3 = one;       // 4^(-3 m)
        // The truncation error of the series is of the order of 4 (4^(-m) q / |Am|)^6
        double target = workingPrecision * Math.log(radix) / 6.0 + LOG4 / 6.0;
        for (long m = 0; ; m++)
        {
            Apcomplex sx = sqrt(xm, workingPrecision),
                      sy = sqrt(ym, workingPrecision),
                      sz = sqrt(zm, workingPrecision),
                      sp = sqrt(pm, workingPrecision),
                      λ = lambda(sx, sy, sz),
                      dm = ensure(sp.add(sx), workingPrecision).multiply(ensure(sp.add(sy), workingPrecision)).multiply(ensure(sp.add(sz), workingPrecision));
            if (isConverged(q, am, m, target))
            {
                break;
            }
            // e(m) = δ 4^(-3 m) / d(m)^2 and the term 4^(-m) RC(1, 1 + e(m)) / d(m)
            Apcomplex em = δ.multiply(power3).divide(dm.multiply(dm));
            sum = sum.add(rc1(em, workingPrecision).multiply(power).divide(dm));
            xm = ensure(xm.add(λ), workingPrecision).multiply(quarter);
            ym = ensure(ym.add(λ), workingPrecision).multiply(quarter);
            zm = ensure(zm.add(λ), workingPrecision).multiply(quarter);
            pm = ensure(pm.add(λ), workingPrecision).multiply(quarter);
            am = ensure(am.add(λ), workingPrecision).multiply(quarter);
            power = power.multiply(quarter);
            power3 = power3.multiply(sixtyFourth);
        }

        Apcomplex t = power.divide(am),
                  X = ensure(a0.subtract(x), workingPrecision).multiply(t),
                  Y = ensure(a0.subtract(y), workingPrecision).multiply(t),
                  Z = ensure(a0.subtract(z), workingPrecision).multiply(t),
                  P = X.add(Y).add(Z).divide(two).negate(),
                  XYZ = X.multiply(Y).multiply(Z),
                  P2 = P.multiply(P),
                  P3 = P2.multiply(P),
                  E2 = X.multiply(Y).add(X.multiply(Z)).add(Y.multiply(Z)).subtract(new Apfloat(3, workingPrecision, radix).multiply(P2)),
                  E3 = XYZ.add(two.multiply(E2).multiply(P)).add(new Apfloat(4, workingPrecision, radix).multiply(P3)),
                  E4 = two.multiply(XYZ).add(E2.multiply(P)).add(new Apfloat(3, workingPrecision, radix).multiply(P3)).multiply(P),
                  E5 = XYZ.multiply(P2);
        // (24024 - 5148 E2 + 2457 E2^2 + 4004 E3 - 4158 E2 E3 - 3276 E4 + 2772 E5) / 24024
        Apcomplex series = ensure(new Apfloat(24024, workingPrecision, radix)
                               .subtract(new Apfloat(5148, workingPrecision, radix).multiply(E2))
                               .add(new Apfloat(2457, workingPrecision, radix).multiply(E2).multiply(E2))
                               .add(new Apfloat(4004, workingPrecision, radix).multiply(E3))
                               .subtract(new Apfloat(4158, workingPrecision, radix).multiply(E2).multiply(E3))
                               .subtract(new Apfloat(3276, workingPrecision, radix).multiply(E4))
                               .add(new Apfloat(2772, workingPrecision, radix).multiply(E5)), workingPrecision)
                               .divide(new Apfloat(24024, workingPrecision, radix));
        // 4^(-m) Am^(-3/2) series + 6 sum
        return power.multiply(series).divide(am.multiply(sqrt(am, workingPrecision))).add(new Apfloat(6, workingPrecision, radix).multiply(sum));
    }

    // Whether the duplication of RJ gives the integral over the positive real axis for these arguments
    private static boolean rjDuplicates(Apcomplex x, Apcomplex y, Apcomplex z, Apcomplex p)
    {
        if (x.real().signum() >= 0 && y.real().signum() >= 0 && z.real().signum() >= 0 && p.real().signum() > 0)
        {
            return true;
        }
        if (x.equals(p) || y.equals(p) || z.equals(p))
        {
            return true;
        }
        if (p.imag().signum() == 0 && nonnegative(x) && nonnegative(y) && nonnegative(z))
        {
            // the pole is on the path: the real part of the result is the principal value
            return true;
        }
        if (p.imag().signum() != 0 || p.real().signum() >= 0)
        {
            return nonnegative(x) && y.conj().equals(z)
                || nonnegative(y) && x.conj().equals(z)
                || nonnegative(z) && x.conj().equals(y);
        }
        return false;
    }

    private static boolean nonnegative(Apcomplex x)
    {
        return x.imag().signum() == 0 && x.real().signum() >= 0;
    }

    private static boolean negative(Apcomplex x)
    {
        return x.imag().signum() == 0 && x.real().signum() < 0;
    }

    // The end of the line for the integral: to the right of the negatives of all arguments, and beside the real axis,
    // so that the line passes the pole and the branch points on one side
    private static Apcomplex rjEnd(Apcomplex x, Apcomplex y, Apcomplex z, Apcomplex p, long workingPrecision)
    {
        int radix = x.radix();
        Apcomplex[] arguments = { x, y, z, p };
        Apfloat smallest = x.real();
        boolean upper = true,
                lower = true;
        for (Apcomplex t : arguments)
        {
            smallest = (t.real().compareTo(smallest) < 0 ? t.real() : smallest);
            upper &= t.imag().signum() >= 0 || t.real().signum() > 0;
            lower &= t.imag().signum() < 0 || t.real().signum() > 0;
        }
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                n = ApfloatMath.ceil(smallest.negate()).add(one).precision(workingPrecision),
                margin = one;
        if (!upper && lower)
        {
            margin = one.negate();
        }
        else if (!upper)
        {
            // Through the upper half plane, but so low that no argument of the lower half plane crosses a branch cut
            Apfloat half = new Apfloat("0.5", workingPrecision, radix);
            for (Apcomplex t : arguments)
            {
                if (t.imag().signum() < 0 && t.real().signum() <= 0)
                {
                    Apfloat distance = ApfloatMath.abs(t.imag()).multiply(half);
                    margin = (distance.compareTo(margin) < 0 ? distance : margin);
                }
            }
        }
        return new Apcomplex(n, margin.precision(workingPrecision));
    }

    // The integral in machine numbers, which are enough to decide the sheet; null if the rule does not converge
    private static Apcomplex rjIntegral(Apcomplex x, Apcomplex y, Apcomplex z, Apcomplex p, Apcomplex end)
    {
        double[][] arguments = { { x.real().doubleValue(), x.imag().doubleValue() }, { y.real().doubleValue(), y.imag().doubleValue() }, { z.real().doubleValue(), z.imag().doubleValue() } };
        double pr = p.real().doubleValue(),
               pi = p.imag().doubleValue(),
               er = end.real().doubleValue(),
               ei = end.imag().doubleValue(),
               sumr = 0,
               sumi = 0,
               previousr = 0,
               previousi = 0,
               step = 1;
        for (double[] argument : arguments)
        {
            if (Double.isInfinite(argument[0]) || Double.isInfinite(argument[1]) || Double.isInfinite(pr) || Double.isInfinite(pi))
            {
                return null;
            }
        }
        for (int level = 0; level <= 10; level++)
        {
            for (int side = -1; side <= 1; side += 2)
            {
                int small = 0;
                for (long k = (level == 0 && side == 1 ? 0 : 1); small < 3; k += (level == 0 ? 1 : 2))
                {
                    double u = step * k,
                           q = Math.exp(-Math.PI * Math.sinh(u)),
                           part = (side < 0 ? q : 1) / (1 + q),
                           weight = Math.PI * Math.cosh(u) * q / ((1 + q) * (1 + q)),
                           // the denominator sqrt(t + x) sqrt(t + y) sqrt(t + z) (t + p)
                           dr = er * part + pr,
                           di = ei * part + pi;
                    if (q == 0)
                    {
                        break;
                    }
                    for (double[] argument : arguments)
                    {
                        double re = er * part + argument[0],
                               im = ei * part + argument[1],
                               modulus = Math.hypot(re, im),
                               // the principal square root
                               rootr = Math.sqrt(0.5 * (modulus + Math.abs(re))),
                               rooti = (rootr == 0 ? 0 : 0.5 * im / rootr);
                        if (re < 0)
                        {
                            double swap = Math.abs(rooti);
                            rooti = (im < 0 ? -rootr : rootr);
                            rootr = swap;
                        }
                        double product = dr * rootr - di * rooti;
                        di = dr * rooti + di * rootr;
                        dr = product;
                    }
                    double norm = dr * dr + di * di,
                           termr = weight * dr / norm,
                           termi = -weight * di / norm;
                    if (Double.isNaN(termr) || Double.isNaN(termi) || Double.isInfinite(termr) || Double.isInfinite(termi))
                    {
                        return null;
                    }
                    sumr += termr;
                    sumi += termi;
                    small = (Math.abs(termr) + Math.abs(termi) < 1.0e-18 * (Math.abs(sumr) + Math.abs(sumi)) ? small + 1 : 0);
                }
            }
            // 3/2 times the sum, the step and the length of the line
            double factorr = 1.5 * step * er,
                   factori = 1.5 * step * ei,
                   valuer = sumr * factorr - sumi * factori,
                   valuei = sumr * factori + sumi * factorr;
            if (level > 0 && Math.hypot(valuer - previousr, valuei - previousi) <= 1.0e-10 * Math.hypot(valuer, valuei))
            {
                return new Apcomplex(new Apfloat(valuer, REFERENCE_PRECISION, x.radix()), new Apfloat(valuei, REFERENCE_PRECISION, x.radix()));
            }
            previousr = valuer;
            previousi = valuei;
            step *= 0.5;
        }
        return null;
    }

    // 3/2 times the integral of 1 / (sqrt(t + x) sqrt(t + y) sqrt(t + z) (t + p)) over the line from zero to the end,
    // by the tanh-sinh rule: x may be zero, which makes the integrand infinite at the start
    private static Apcomplex rjIntegral(Apcomplex x, Apcomplex y, Apcomplex z, Apcomplex p, Apcomplex end, long workingPrecision)
    {
        int radix = x.radix();
        long precision = ApfloatHelperShim.extendPrecision(workingPrecision, 10);
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision);
        Apfloat one = new Apfloat(1, precision, radix),
                two = new Apfloat(2, precision, radix),
                pi = h.pi(radix),
                epsilon = h.pow(new Apfloat(radix, precision, radix), -precision - 5),
                step = one;
        Apcomplex sum = ApfloatHelperShim.ZEROS[radix],
                  previous = null;
        for (int level = 0; level <= 14; level++)
        {
            // every level halves the step and adds the nodes between the old ones
            for (int side = -1; side <= 1; side += 2)
            {
                int small = 0;
                for (long k = (level == 0 && side == 1 ? 0 : 1); small < 3; k += (level == 0 ? 1 : 2))
                {
                    Apfloat u = h.multiply(step, new Apfloat(k, precision, radix)),
                            // q = e^(-pi sinh(u)); the node is the part q / (1 + q) of the line from its nearer end
                            q = h.exp(h.multiply(pi, h.sinh(u)).negate()),
                            onePlusQ = h.add(one, q),
                            part = h.divide(side < 0 ? q : one, onePlusQ),
                            weight = h.divide(h.multiply(h.multiply(pi, h.cosh(u)), q), h.multiply(onePlusQ, onePlusQ));
                    Apcomplex t = h.multiply(end, part),
                              denominator = h.multiply(h.multiply(h.sqrt(h.add(t, x)), h.sqrt(h.add(t, y))), h.multiply(h.sqrt(h.add(t, z)), h.add(t, p))),
                              term = h.divide(weight, denominator);
                    sum = h.add(sum, term);
                    small = (h.abs(term).compareTo(epsilon) < 0 ? small + 1 : 0);
                }
            }
            Apcomplex value = h.multiply(h.multiply(sum, step), h.multiply(end, new Apfloat("1.5", precision, radix)));
            if (previous != null && GuardDigitsHelper.agree(previous, value, workingPrecision))
            {
                return value;
            }
            previous = value;
            step = h.divide(step, two);
        }
        throw new ArithmeticException("Carlson RJ: the integral does not converge");
    }

    /**
     * R<sub>G</sub>(x, y, z).
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     * @param precision The precision of the result.
     *
     * @return R<sub>G</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If the precision is infinite.
     */

    public static Apcomplex rg(Apcomplex x, Apcomplex y, Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        checkPrecision(precision);
        int radix = x.radix();
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        Apfloat two = new Apfloat(2, workingPrecision, radix);
        if (zeros(x, y, z) >= 2)
        {
            // RG(0, 0, z) = sqrt(z) / 2
            Apcomplex result = sqrt(ApfloatHelperShim.ensurePrecision(x.add(y).add(z), workingPrecision), workingPrecision).divide(two);
            return ApfloatHelperShim.limitPrecision(result, precision);
        }
        if (z.isZero())
        {
            // The formula below needs a nonzero z, and RG is symmetric
            return (x.isZero() ? rg(z, x, y, precision) : rg(y, z, x, precision));
        }
        x = ApfloatHelperShim.ensurePrecision(x, workingPrecision);
        y = ApfloatHelperShim.ensurePrecision(y, workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        // (z RF - (x - z) (y - z) RD / 3 + sqrt(x) sqrt(y) / sqrt(z)) / 2, DLMF 19.21.10: the root of the quotient x y / z is another branch for complex arguments
        Apcomplex rf = rf(x, y, z, workingPrecision),
                  rd = rd(x, y, z, workingPrecision),
                  result = z.multiply(rf)
                            .subtract(ensure(x.subtract(z), workingPrecision).multiply(ensure(y.subtract(z), workingPrecision)).multiply(rd).divide(new Apfloat(3, workingPrecision, radix)))
                            .add(sqrt(x, workingPrecision).multiply(sqrt(y, workingPrecision)).divide(sqrt(z, workingPrecision)))
                            .divide(two);
        return ApfloatHelperShim.limitPrecision(result, precision);
    }

    // RC(1, 1 + e) = atan(sqrt(e)) / sqrt(e), which tends to 1 for e -> 0
    private static Apcomplex rc1(Apcomplex e, long workingPrecision)
        throws ApfloatRuntimeException
    {
        if (e.isZero())
        {
            return new Apfloat(1, workingPrecision, e.radix());
        }
        Apcomplex s = sqrt(e, workingPrecision);
        return ApcomplexMath.atan(s).divide(s);
    }

    // sqrt(x) sqrt(y) + sqrt(x) sqrt(z) + sqrt(y) sqrt(z)
    private static Apcomplex lambda(Apcomplex sx, Apcomplex sy, Apcomplex sz)
        throws ApfloatRuntimeException
    {
        return sx.multiply(sy).add(sx.multiply(sz)).add(sy.multiply(sz));
    }

    // Whether 4^(-m) q / |Am| is below the tolerance, that is m log(4) >= log(q / |Am|) + target
    private static boolean isConverged(Apfloat q, Apcomplex am, long m, double target)
        throws ApfloatRuntimeException
    {
        if (q.signum() == 0)
        {
            return true;
        }
        Apfloat mean = ApcomplexMath.abs(am).precision(DOUBLE_PRECISION);
        if (mean.signum() == 0)
        {
            // The arguments cancel in their mean, as 0, 3, 1 and twice -2 do: the next step moves it away from zero
            return false;
        }
        Apfloat ratio = q.precision(DOUBLE_PRECISION).divide(mean);
        double logRatio = ApfloatMath.log(ratio).doubleValue();
        return m * LOG4 >= logRatio + target;
    }

    private static Apcomplex sqrt(Apcomplex z, long workingPrecision)
        throws ApfloatRuntimeException
    {
        if (z.isZero())
        {
            return z;
        }
        return ApcomplexMath.sqrt(ApfloatHelperShim.ensurePrecision(z, workingPrecision));
    }

    // The sums and differences are of numbers which are exact to the working precision
    private static Apcomplex ensure(Apcomplex z, long workingPrecision)
        throws ApfloatRuntimeException
    {
        return (z.isZero() ? z : ApfloatHelperShim.ensurePrecision(z, workingPrecision));
    }

    private static Apfloat quarter(long workingPrecision, int radix)
        throws ApfloatRuntimeException
    {
        return new Apfloat(1, workingPrecision, radix).divide(new Apfloat(4, workingPrecision, radix));
    }

    private static Apfloat max(Apfloat a, Apfloat b, Apfloat c)
    {
        return ApfloatMath.max(ApfloatMath.max(a, b), c);
    }

    private static int zeros(Apcomplex x, Apcomplex y, Apcomplex z)
    {
        return (x.isZero() ? 1 : 0) + (y.isZero() ? 1 : 0) + (z.isZero() ? 1 : 0);
    }

    private static void checkPrecision(long precision)
        throws InfiniteExpansionException
    {
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Carlson integral to infinite precision", "carlson.infinitePrecision");
        }
    }

    // A precision which is enough for a double
    private static final long DOUBLE_PRECISION = 20;
}
