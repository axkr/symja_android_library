package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexCoulombMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexCoulombMathTest
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

    @Test
    public void testCoulombF()
    {
        assertClose("F(0, 1/3, 7)", new Apfloat("-0.336986201275319163755964283097711004053033812"), ApcomplexCoulombMath.coulombF(q(0, 1).precision(PRECISION), q(1, 3), q(7, 1)), PRECISION - 2);
        assertClose("F(2, -2, 7)", new Apfloat("0.907006881603599837927793522678357740667908027"), ApcomplexCoulombMath.coulombF(q(2, 1), q(-2, 1), q(7, 1)), PRECISION - 2);
        // without a charge
        assertClose("F(1, 0, 2)", new Apfloat("0.870795549959983234695578162456634611117128257"), ApcomplexCoulombMath.coulombF(q(1, 1), Apcomplex.ZERO, q(2, 1)), PRECISION - 2);
    }

    @Test
    public void testCoulombG()
    {
        assertClose("G(0, 1/3, 7)", new Apfloat("0.968058102171807368337512934044627833299697106"), ApcomplexCoulombMath.coulombG(Apcomplex.ZERO, q(1, 3), q(7, 1)), PRECISION - 2);
        assertClose("G(1/2, -2, 1/2)", new Apfloat("0.234942048659657340825208834042188165609117717"), ApcomplexCoulombMath.coulombG(q(1, 2), q(-2, 1), q(1, 2)), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexCoulombMath.coulombG(q(1, 1), q(1, 1), q(-2, 1)));
    }
}
