package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexMathieuMath}. The quotients <i>C</i>(<i>z</i>) / <i>C</i>(0),
 * <i>S</i>(<i>z</i>) / <i>S</i>'(0) and <i>C</i>'(<i>z</i>) / <i>C</i>(0) are the basic solutions of
 * Mathieu's equation, whatever the normalization of the functions is; the expected values are an
 * integration of the equation with mpmath.
 */
public class ApcomplexMathieuMathTest
{
    private static final long PRECISION = 30;

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

    private static Apfloat q(long p, long q)
    {
        return new Aprational(new Apint(p), new Apint(q)).precision(PRECISION);
    }

    private static Apcomplex c(String real, String imag)
    {
        return new Apcomplex(new Apfloat(real), new Apfloat(imag));
    }

    private static void assertBasicSolutions(String message, Apcomplex a, Apcomplex q, Apcomplex z, Apcomplex even, Apcomplex odd, Apcomplex evenPrime)
    {
        long working = PRECISION + 5;
        Apcomplex zero = Apcomplex.ZERO,
                  c0 = ApcomplexMathieuMath.mathieuC(a, q, zero, working),
                  s0 = ApcomplexMathieuMath.mathieuSPrime(a, q, zero, working);
        assertClose(message + " C", even, ApcomplexMathieuMath.mathieuC(a, q, z, working).divide(c0), PRECISION - 2);
        assertClose(message + " S", odd, ApcomplexMathieuMath.mathieuS(a, q, z, working).divide(s0), PRECISION - 2);
        assertClose(message + " C'", evenPrime, ApcomplexMathieuMath.mathieuCPrime(a, q, z, working).divide(c0), PRECISION - 2);
    }

    @Test
    public void testLargeParameter()
    {
        // the largest coefficient of the series is far from the index of the reduced exponent
        assertBasicSolutions("a = 1000", q(1000, 1), q(1, 1), q(7, 10),
                             new Apfloat("-0.9912539571050478477319940158268955260152"),
                             new Apfloat("-0.00407644010807843471928018919665636807934"),
                             new Apfloat("4.072642768877818854006129022257812673455"));
        assertBasicSolutions("a = 1000000", q(1000000, 1), q(5, 1), q(1, 4),
                             new Apfloat("0.2398248153164624048224442430485449849332"),
                             new Apfloat("-0.0009708207202172789145584292555613036076635"),
                             new Apfloat("970.811605652811656319360394259805100996"));
    }

    @Test
    public void testComplexArgument()
    {
        // the terms of the series grow with the index, and cancel
        assertBasicSolutions("z = 1+4i", q(2, 1), q(1, 1), new Apcomplex(q(1, 1), q(4, 1)),
                             c("-5323621711943459248.453727092772962139812", "4786426655531497994.606584811656778825141"),
                             c("-7404933757063936667.455323831458959562412", "-748145090853225552.0890549922111622848664"),
                             c("60576911770702477437.92379715116777091443", "383146951726141006332.2366004120768382887"));
        assertBasicSolutions("z = 2+3i", q(3, 1), q(20, 1), new Apcomplex(q(2, 1), q(3, 1)),
                             c("-35673723576851748339553614413060112.81762", "-12309804542758150860210486239484310.69729"),
                             c("-5963825524078134737105131836015317.832945", "-2064456282781618805853111220152009.502135"),
                             c("333802477903162805202850996494323270.7418", "3356483379167356102432433328064944559.768"));
        // out of reach of the series
        assertThrows(ArithmeticException.class, () -> ApcomplexMathieuMath.mathieuC(q(2, 1), q(1, 1), new Apcomplex(Apfloat.ZERO, q(40, 1)), PRECISION));
    }

    @Test
    public void testCharacteristicValueOfLargeOrder()
    {
        // a(r, q) = r^2 + q^2 / (2 (r^2 - 1)) + (5 r^2 + 7) q^4 / (32 (r^2 - 1)^3 (r^2 - 4)) + ...
        Apcomplex a = ApcomplexMathieuMath.mathieuCharacteristicA(q(5000, 1), q(3, 1), 40);
        assertClose("a(5000, 3)", new Apfloat("25000000.00000018000000720000109800028368"), a, 38);
        assertClose("b(5000, 3)", a, ApcomplexMathieuMath.mathieuCharacteristicB(q(5000, 1), q(3, 1), 40), 38);
        // the exponent of a characteristic value is its order
        Apcomplex half = ApcomplexMathieuMath.mathieuCharacteristicA(q(401, 2), q(3, 1), 50);
        assertClose("nu(a(401/2, 3), 3)", q(401, 2), ApcomplexMathieuMath.mathieuCharacteristicExponent(half, q(3, 1), PRECISION), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexMathieuMath.mathieuCharacteristicB(Apcomplex.ZERO, q(3, 1), PRECISION));
    }

    @Test
    public void testTridiagonalEigenvalues()
    {
        // the matrix with 2 on and -1 next to the diagonal has the eigenvalues 2 - 2 cos(k pi / (n + 1))
        int n = 7;
        double[] main = new double[n],
                 secondarySquared = new double[n - 1];
        java.util.Arrays.fill(main, 2.0);
        java.util.Arrays.fill(secondarySquared, 1.0);
        for (int k = 0; k < n; k++)
        {
            double expected = 2.0 - 2.0 * Math.cos((k + 1) * Math.PI / (n + 1));
            assertEquals(expected, MathieuCharacteristicHelper.eigenvalue(main, secondarySquared, k), 1.0e-14);
            assertEquals(k, MathieuCharacteristicHelper.eigenvaluesBelow(main, secondarySquared, expected - 1.0e-9));
            assertEquals(k + 1, MathieuCharacteristicHelper.eigenvaluesBelow(main, secondarySquared, expected + 1.0e-9));
        }
    }
}
