package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexThetaMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexThetaMathTest
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
    public void testEllipticTheta()
    {
        // the alternating series cancels for a nome next to 1
        assertClose("theta4(0, 9/10)", new Apfloat("0.0000000007373526938473068047969135475469319468124"),
                    ApcomplexThetaMath.ellipticTheta(4, Apcomplex.ZERO, q(9, 10)), PRECISION - 2);
        assertClose("theta3(1/3, 99/100)", new Apfloat("0.0002793561852476686905651185654569480429789"),
                    ApcomplexThetaMath.ellipticTheta(3, q(1, 3), q(99, 100)), PRECISION - 2);
        assertClose("theta1(1/2+i, 1/4+i/3)", new Apcomplex(new Apfloat("2.44923162174696548747422228797909146587"), new Apfloat("-1.070272652775854642179840042127857021676")),
                    ApcomplexThetaMath.ellipticTheta(1, new Apcomplex(q(1, 2), q(1, 1)), new Apcomplex(q(1, 4), q(1, 3))), PRECISION - 2);
        assertTrue(ApcomplexThetaMath.ellipticTheta(1, Apcomplex.ZERO, q(1, 3)).isZero());
        assertThrows(ArithmeticException.class, () -> ApcomplexThetaMath.ellipticTheta(3, q(1, 3), q(3, 2)));
        assertThrows(IllegalArgumentException.class, () -> ApcomplexThetaMath.ellipticTheta(5, q(1, 3), q(1, 2)));
    }

    @Test
    public void testJacobi()
    {
        Apcomplex[] functions = ApcomplexThetaMath.jacobi(q(2, 1), q(9, 10));
        assertClose("sn(2 | 9/10)", new Apfloat("0.9816158695184937992547326258910294172047"), functions[0], PRECISION - 2);
        // a parameter above 1
        functions = ApcomplexThetaMath.jacobi(q(2, 1), q(3, 1));
        assertClose("cn(2 | 3)", new Apfloat("0.9999976785143824864541558849729383510131"), functions[1], PRECISION - 2);
        functions = ApcomplexThetaMath.jacobi(new Apcomplex(q(1, 2), q(1, 1)), new Apcomplex(q(1, 4), q(1, 3)));
        assertClose("dn(1/2+i | 1/4+i/3)", new Apcomplex(new Apfloat("1.336887441894234154669824476727513789413"), new Apfloat("-0.1346510506036005092213235707540123745258")),
                    functions[2], PRECISION - 2);
    }
}
