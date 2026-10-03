package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatEllipticMath} and {@link ApcomplexEllipticMath}. The expected values are
 * closed forms and identities of the functions, computed with apfloat itself.
 */
public class ApfloatEllipticMathTest
{
    private static final long PRECISION = 40;

    private static Apfloat number(String value)
    {
        return new Apfloat(value, PRECISION);
    }

    // a and b agree except for the last digits of the given precision
    private static void assertClose(String message, Apcomplex expected, Apcomplex actual, long digits)
    {
        Apcomplex difference = expected.subtract(actual);
        if (difference.real().signum() == 0 && difference.imag().signum() == 0)
        {
            return;
        }
        long scale = Math.max(expected.scale(), actual.scale());
        assertTrue(difference.scale() <= scale - digits, message + ": expected " + expected + " but was " + actual);
    }

    private static void assertClose(String message, Apcomplex expected, Apcomplex actual)
    {
        assertClose(message, expected, actual, PRECISION - 3);
    }

    @Test
    public void testCarlsonEqualArguments()
    {
        Apfloat x = number("2.75"),
                root = ApfloatMath.sqrt(x),
                one = new Apfloat(1, PRECISION);
        assertClose("RF", one.divide(root), ApfloatEllipticMath.carlsonRF(x, x, x));
        assertClose("RC", one.divide(root), ApfloatEllipticMath.carlsonRC(x, x));
        assertClose("RD", one.divide(root.multiply(x)), ApfloatEllipticMath.carlsonRD(x, x, x));
        assertClose("RJ", one.divide(root.multiply(x)), ApfloatEllipticMath.carlsonRJ(x, x, x, x));
        assertClose("RG", root, ApfloatEllipticMath.carlsonRG(x, x, x));
    }

    @Test
    public void testCarlsonClosedForms()
    {
        Apfloat pi = ApfloatMath.pi(PRECISION),
                zero = new Apfloat(0),
                one = new Apfloat(1, PRECISION),
                two = new Apfloat(2, PRECISION);
        // RC(1, 2) = atan(1) and RC(2, 1) = atanh(1/sqrt(2)) sqrt(2) / sqrt(2)
        assertClose("RC(1, 2)", pi.divide(new Apfloat(4, PRECISION)), ApfloatEllipticMath.carlsonRC(one, two));
        assertClose("RC(2, 1)", ApfloatMath.atanh(ApfloatMath.sqrt(one.divide(two))), ApfloatEllipticMath.carlsonRC(two, one));
        // RF(0, 1, 1) = pi / 2 and RG(0, 1, 1) = pi / 4
        assertClose("RF(0, 1, 1)", pi.divide(two), ApfloatEllipticMath.carlsonRF(zero, one, one));
        assertClose("RG(0, 1, 1)", pi.divide(new Apfloat(4, PRECISION)), ApfloatEllipticMath.carlsonRG(zero, one, one));
        // RD(x, y, z) = RJ(x, y, z, z)
        Apfloat x = number("0.5"),
                y = number("1.75"),
                z = number("3.25");
        assertClose("RD", ApfloatEllipticMath.carlsonRJ(x, y, z, z), ApfloatEllipticMath.carlsonRD(x, y, z));
        // RD(x, y, z) + RD(y, z, x) + RD(z, x, y) = 3 / sqrt(x y z)
        Apfloat sum = ApfloatEllipticMath.carlsonRD(x, y, z).add(ApfloatEllipticMath.carlsonRD(y, z, x)).add(ApfloatEllipticMath.carlsonRD(z, x, y));
        assertClose("RD sum", new Apfloat(3, PRECISION).divide(ApfloatMath.sqrt(x.multiply(y).multiply(z))), sum);
    }

    @Test
    public void testCarlsonPrecision()
    {
        // twice the precision gives the same digits
        Apfloat value = ApfloatEllipticMath.carlsonRJ(new Apfloat("0.5", 30), new Apfloat("1.75", 30), new Apfloat("3.25", 30), new Apfloat("4.5", 30)),
                exact = ApfloatEllipticMath.carlsonRJ(new Apfloat("0.5", 60), new Apfloat("1.75", 60), new Apfloat("3.25", 60), new Apfloat("4.5", 60));
        assertEquals(30, value.precision());
        assertEquals(60, exact.precision());
        assertClose("RJ", exact, value, 28);
    }

    @Test
    public void testCarlsonInfinite()
    {
        Apfloat zero = new Apfloat(0),
                x = number("5");
        assertThrows(ApfloatArithmeticException.class, () -> ApfloatEllipticMath.carlsonRF(zero, zero, x));
        assertThrows(ApfloatArithmeticException.class, () -> ApfloatEllipticMath.carlsonRD(x, x, zero));
        assertThrows(InfiniteExpansionException.class, () -> ApfloatEllipticMath.carlsonRF(new Apint(1), new Apint(2), new Apint(3)));
    }

    @Test
    public void testEllipticParameterZeroAndOne()
    {
        Apfloat φ = number("0.875"),
                zero = new Apfloat(0),
                one = new Apfloat(1, PRECISION);
        assertClose("F(phi|0)", φ, ApfloatEllipticMath.ellipticF(φ, zero));
        assertClose("E(phi|0)", φ, ApfloatEllipticMath.ellipticE(φ, zero));
        assertClose("F(phi|1)", ApfloatMath.atanh(ApfloatMath.sin(φ)), ApfloatEllipticMath.ellipticF(φ, one));
        assertClose("E(phi|1)", ApfloatMath.sin(φ), ApfloatEllipticMath.ellipticE(φ, one));
        // Pi(0; phi|m) = F(phi|m)
        Apfloat m = number("0.375");
        assertClose("Pi(0; phi|m)", ApfloatEllipticMath.ellipticF(φ, m), ApfloatEllipticMath.ellipticPi(zero, φ, m));
        // Pi(n; phi|0) = atan(sqrt(1 - n) tan(phi)) / sqrt(1 - n)
        Apfloat n = number("-2.5"),
                root = ApfloatMath.sqrt(one.subtract(n));
        assertClose("Pi(n; phi|0)", ApfloatMath.atan(root.multiply(ApfloatMath.tan(φ))).divide(root), ApfloatEllipticMath.ellipticPi(n, φ, zero));
    }

    @Test
    public void testEllipticEOffThePrincipalStripAtOne()
    {
        // E(r + k pi|1) = sin(r) + 2 k
        Apfloat one = new Apfloat(1, PRECISION),
                pi = ApfloatMath.pi(PRECISION);
        Apfloat φ = number("2.5");
        assertClose("E(2.5|1)", new Apfloat(2, PRECISION).add(ApfloatMath.sin(φ.subtract(pi))), ApfloatEllipticMath.ellipticE(φ, one));
        φ = number("-7.25");
        assertClose("E(-7.25|1)", new Apfloat(-4, PRECISION).add(ApfloatMath.sin(φ.add(pi.multiply(new Apfloat(2, PRECISION))))), ApfloatEllipticMath.ellipticE(φ, one));
    }

    @Test
    public void testEllipticQuasiPeriod()
    {
        Apfloat φ = number("0.625"),
                m = number("0.375"),
                n = number("0.25"),
                two = new Apfloat(2, PRECISION),
                pi = ApfloatMath.pi(PRECISION + 5),
                shifted = φ.add(pi.multiply(new Apfloat(3, PRECISION))).precision(PRECISION);
        // F(phi + k pi|m) = F(phi|m) + 2 k K(m), and the same for E and Pi
        Apfloat six = new Apfloat(6, PRECISION);
        assertClose("F", ApfloatEllipticMath.ellipticF(φ, m).add(six.multiply(ApfloatMath.ellipticK(m))), ApfloatEllipticMath.ellipticF(shifted, m), PRECISION - 5);
        assertClose("E", ApfloatEllipticMath.ellipticE(φ, m).add(six.multiply(ApfloatMath.ellipticE(m))), ApfloatEllipticMath.ellipticE(shifted, m), PRECISION - 5);
        assertClose("Pi", ApfloatEllipticMath.ellipticPi(n, φ, m).add(six.multiply(ApfloatEllipticMath.ellipticPi(n, m))), ApfloatEllipticMath.ellipticPi(n, shifted, m), PRECISION - 5);
        // odd in the amplitude
        assertClose("F odd", ApfloatEllipticMath.ellipticF(φ, m).negate(), ApfloatEllipticMath.ellipticF(φ.negate(), m));
        assertClose("Pi odd", ApfloatEllipticMath.ellipticPi(n, φ, m).negate(), ApfloatEllipticMath.ellipticPi(n, φ.negate(), m));
        // the complete integrals at the amplitude pi / 2
        Apfloat half = pi.divide(two).precision(PRECISION);
        assertClose("K", ApfloatMath.ellipticK(m), ApfloatEllipticMath.ellipticF(half, m), PRECISION - 5);
        assertClose("E", ApfloatMath.ellipticE(m), ApfloatEllipticMath.ellipticE(half, m), PRECISION - 5);
    }

    @Test
    public void testEllipticPiComplete()
    {
        Apfloat one = new Apfloat(1, PRECISION),
                m = number("0.375");
        // Pi(m|m) = E(m) / (1 - m) and Pi(0|m) = K(m)
        assertClose("Pi(m|m)", ApfloatMath.ellipticE(m).divide(one.subtract(m)), ApfloatEllipticMath.ellipticPi(m, m));
        assertClose("Pi(0|m)", ApfloatMath.ellipticK(m), ApfloatEllipticMath.ellipticPi(new Apfloat(0), m));
        // Pi(n|0) = pi / (2 sqrt(1 - n))
        Apfloat n = number("-6.5");
        assertClose("Pi(n|0)", ApfloatMath.pi(PRECISION).divide(new Apfloat(2, PRECISION).multiply(ApfloatMath.sqrt(one.subtract(n)))), ApfloatEllipticMath.ellipticPi(n, new Apfloat(0)));
    }

    @Test
    public void testEllipticPiLargeNegativeCharacteristic()
    {
        // The two Carlson terms of the definition cancel down to the order of 1 / sqrt(-n)
        Apfloat value = ApfloatEllipticMath.ellipticPi(new Apfloat("-1e24", 30), new Apfloat("0.4", 30)),
                exact = ApfloatEllipticMath.ellipticPi(new Apfloat("-1e24", 60), new Apfloat("0.4", 60));
        assertClose("Pi(-1e24|0.4)", exact, value, 27);
        // pi / (2 sqrt(-n)) to the first order
        assertClose("Pi(-1e24|0.4)", ApfloatMath.pi(30).divide(new Apfloat("2e12", 30)), value, 11);
        value = ApfloatEllipticMath.ellipticPi(new Apfloat("-1e24", 30), new Apfloat("0.875", 30), new Apfloat("0.4", 30));
        exact = ApfloatEllipticMath.ellipticPi(new Apfloat("-1e24", 60), new Apfloat("0.875", 60), new Apfloat("0.4", 60));
        assertClose("Pi(-1e24; 0.875|0.4)", exact, value, 27);
    }

    @Test
    public void testEllipticInfinite()
    {
        Apfloat one = new Apfloat(1, PRECISION),
                φ = number("2.5");
        // 1 / cos(t) is not integrable over pi / 2
        assertThrows(ApfloatArithmeticException.class, () -> ApfloatEllipticMath.ellipticF(φ, one));
        assertThrows(ApfloatArithmeticException.class, () -> ApfloatEllipticMath.ellipticPi(number("0.25"), φ, one));
        // the result is not real
        assertThrows(ApfloatArithmeticException.class, () -> ApfloatEllipticMath.ellipticF(number("1.5"), number("3")));
        assertThrows(InfiniteExpansionException.class, () -> ApfloatEllipticMath.ellipticF(new Apint(1), new Apint(-2)));
    }

    @Test
    public void testEllipticComplex()
    {
        Apcomplex φ = new Apcomplex(number("0.75"), number("0.5")),
                  m = new Apcomplex(number("0.375"), number("-1.25"));
        // d/dphi F(phi|m) = 1 / sqrt(1 - m sin(phi)^2), by a central difference
        Apfloat h = new Apfloat("1e-12", PRECISION);
        Apcomplex difference = ApcomplexEllipticMath.ellipticF(φ.add(h), m).subtract(ApcomplexEllipticMath.ellipticF(φ.subtract(h), m)).divide(h.multiply(new Apfloat(2, PRECISION))),
                  sin = ApcomplexMath.sin(φ),
                  derivative = new Apfloat(1, PRECISION).divide(ApcomplexMath.sqrt(new Apfloat(1, PRECISION).subtract(m.multiply(sin).multiply(sin))));
        assertClose("F'", derivative, difference, 20);
        // conjugate symmetry
        assertClose("conj", ApcomplexEllipticMath.ellipticE(φ, m).conj(), ApcomplexEllipticMath.ellipticE(φ.conj(), m.conj()));
    }
}
