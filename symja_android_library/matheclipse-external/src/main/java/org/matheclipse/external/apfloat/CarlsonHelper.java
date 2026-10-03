package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
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
     * @return R<sub>J</sub>(x, y, z, p)
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
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                two = new Apfloat(2, workingPrecision, radix),
                five = new Apfloat(5, workingPrecision, radix),
                quarter = quarter(workingPrecision, radix),
                sixtyFourth = quarter.multiply(quarter).multiply(quarter);
        x = ApfloatHelperShim.ensurePrecision(x, workingPrecision);
        y = ApfloatHelperShim.ensurePrecision(y, workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        p = ApfloatHelperShim.ensurePrecision(p, workingPrecision);

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
        Apcomplex result = power.multiply(series).divide(am.multiply(sqrt(am, workingPrecision))).add(new Apfloat(6, workingPrecision, radix).multiply(sum));
        return ApfloatHelperShim.limitPrecision(result, precision);
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
        // (z RF - (x - z) (y - z) RD / 3 + sqrt(x y / z)) / 2
        Apcomplex rf = rf(x, y, z, workingPrecision),
                  rd = rd(x, y, z, workingPrecision),
                  result = z.multiply(rf)
                            .subtract(ensure(x.subtract(z), workingPrecision).multiply(ensure(y.subtract(z), workingPrecision)).multiply(rd).divide(new Apfloat(3, workingPrecision, radix)))
                            .add(sqrt(x.multiply(y).divide(z), workingPrecision))
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
        Apfloat ratio = q.precision(DOUBLE_PRECISION).divide(ApcomplexMath.abs(am).precision(DOUBLE_PRECISION));
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
