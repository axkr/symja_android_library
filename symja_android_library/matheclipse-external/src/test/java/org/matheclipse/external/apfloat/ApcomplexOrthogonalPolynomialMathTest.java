package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexOrthogonalPolynomialMath} for large degrees, where the explicit sums
 * cancel. The expected values were calculated with mpmath at 60 digits.
 */
public class ApcomplexOrthogonalPolynomialMathTest
{
    private static final long PRECISION = 40;

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

    // The rational number p/q with the precision of the tests
    private static Apfloat q(long p, long q)
    {
        return new Aprational(new Apint(p), new Apint(q)).precision(PRECISION);
    }

    private static Apcomplex c(String real, String imag)
    {
        return new Apcomplex(new Apfloat(real), new Apfloat(imag));
    }

    @Test
    public void testLegendreP()
    {
        assertClose("P(1000, 3/10)", new Apfloat("-0.0256691675079361898775335961185268642956367501"),
                    ApcomplexOrthogonalPolynomialMath.legendreP(1000, q(3, 10), PRECISION), 37);
        // P(-n-1) == P(n)
        assertClose("P(-1001, 3/10)", new Apfloat("-0.0256691675079361898775335961185268642956367501"),
                    ApcomplexOrthogonalPolynomialMath.legendreP(-1001, q(3, 10), PRECISION), 37);
        assertClose("P(40, 3/10+2/5*I)", c("254093.26907495984816280304408976870934733522", "778419.146872720269861505651183854245609698638"),
                    ApcomplexOrthogonalPolynomialMath.legendreP(40, new Apcomplex(q(3, 10), q(2, 5)), PRECISION), 37);
        assertClose("P(0, z)", new Apint(1), ApcomplexOrthogonalPolynomialMath.legendreP(0, q(3, 10), PRECISION), 37);
    }

    @Test
    public void testAssociatedLegendreP()
    {
        assertClose("P(300, 3, 3/10)", new Apfloat("555489.173232984150749770604721070451731371453"),
                    ApcomplexOrthogonalPolynomialMath.legendreP(300, 3, 1, q(3, 10), PRECISION), 36);
        // type 2 is type 1 on (-1, 1)
        assertClose("P(300, 3, 2, 3/10)", new Apfloat("555489.173232984150749770604721070451731371453"),
                    ApcomplexOrthogonalPolynomialMath.legendreP(300, 3, 2, q(3, 10), PRECISION), 36);
        assertClose("P(7, -3, 3/10)", new Apfloat("-0.00040277772458327098370225404964427090660474295"),
                    ApcomplexOrthogonalPolynomialMath.legendreP(7, -3, 1, q(3, 10), PRECISION), 37);
        assertClose("P(5, 2, 3, 2)", new Apint(3465),
                    ApcomplexOrthogonalPolynomialMath.legendreP(5, 2, 3, q(2, 1), PRECISION), 37);
        assertClose("P(5, 6, 3/10)", Apcomplex.ZERO,
                    ApcomplexOrthogonalPolynomialMath.legendreP(5, 6, 1, q(3, 10), PRECISION), 37);
    }

    @Test
    public void testLegendreQ()
    {
        assertClose("Q(300, 3/10)", new Apfloat("-0.0324703308837395788534334504373277248427062657"),
                    ApcomplexOrthogonalPolynomialMath.legendreQ(300, 2, q(3, 10), PRECISION), 37);
        assertThrows(ArithmeticException.class, () -> ApcomplexOrthogonalPolynomialMath.legendreQ(-1, 2, q(3, 10), PRECISION));
    }

    @Test
    public void testGegenbauerC()
    {
        assertClose("C(300, 2, 3/10)", new Apfloat("-101.847511688347993366495145233930787584306848"),
                    ApcomplexOrthogonalPolynomialMath.gegenbauerC(300, new Apint(2), q(3, 10), PRECISION), 37);
        // C(n, 1/2, z) == P(n, z)
        assertClose("C(1000, 1/2, 3/10)", new Apfloat("-0.0256691675079361898775335961185268642956367501"),
                    ApcomplexOrthogonalPolynomialMath.gegenbauerC(1000, q(1, 2), q(3, 10), PRECISION), 37);
    }

    @Test
    public void testJacobiP()
    {
        assertClose("P(300, 1, 2, 3/10)", new Apfloat("-0.120859212882760636587271596278278746513167882"),
                    ApcomplexOrthogonalPolynomialMath.jacobiP(300, new Apint(1), new Apint(2), q(3, 10), PRECISION), 37);
        // a negative integer parameter: the reduction to a positive one
        assertClose("P(300, -1, -1, 3/10)", new Apfloat("-0.0222288840531175298314315035905"),
                    ApcomplexOrthogonalPolynomialMath.jacobiP(300, new Apint(-1), new Apint(-1), q(3, 10), PRECISION), 29);
        // P(4, -1, -1, z) == 3/16-9/8*z^2+15/16*z^4
        assertClose("P(4, -1, -1, 3/10)", q(1, 1).multiply(new Aprational(new Apint(3), new Apint(16)))
                        .subtract(new Aprational(new Apint(9), new Apint(8)).multiply(q(9, 100)))
                        .add(new Aprational(new Apint(15), new Apint(16)).multiply(q(81, 10000))),
                    ApcomplexOrthogonalPolynomialMath.jacobiP(4, new Apint(-1), new Apint(-1), q(3, 10), PRECISION), 37);
        // a+b == -2 without an integer parameter meets a zero coefficient
        assertThrows(ArithmeticException.class, () -> ApcomplexOrthogonalPolynomialMath.jacobiP(5, q(-1, 2), q(-3, 2), q(3, 10), PRECISION));
    }

    @Test
    public void testSphericalHarmonicY()
    {
        assertClose("Y(300, 3, 3/10, 1/5)", c("0.479706125700756673362823175567653026946922866", "0.328184617778874331302948626800878541950832363"),
                    ApcomplexOrthogonalPolynomialMath.sphericalHarmonicY(300, 3, q(3, 10), q(1, 5), PRECISION), 36);
        assertClose("Y(3, 4, 3/10, 1/5)", Apcomplex.ZERO,
                    ApcomplexOrthogonalPolynomialMath.sphericalHarmonicY(3, 4, q(3, 10), q(1, 5), PRECISION), 37);
    }

    @Test
    public void testZernikeR()
    {
        assertClose("R(150, 2, 3/10)", new Apfloat("-0.0347418253888635605500513591495994900265310445"),
                    ApcomplexOrthogonalPolynomialMath.zernikeR(150, 2, q(3, 10), PRECISION), 37);
        assertClose("R(151, 2, 3/10)", Apcomplex.ZERO,
                    ApcomplexOrthogonalPolynomialMath.zernikeR(151, 2, q(3, 10), PRECISION), 37);
        assertThrows(ArithmeticException.class, () -> ApcomplexOrthogonalPolynomialMath.zernikeR(3, 4, q(3, 10), PRECISION));
    }
}
