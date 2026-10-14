package org.matheclipse.external.apfloat;

import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexWeberMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexWeberMathTest
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

    @Test
    @Tag("slow")
    public void testWeberE()
    {
        assertClose("E(1/2, 3)", new Apfloat("-0.570621336461999334625833563463396920837137519"),
                    ApcomplexWeberMath.weberE(q(1, 2), q(3, 1)), PRECISION - 1);
        // an integer order
        assertClose("E(7, 300)", new Apfloat("0.0305453742504866305011167633672392273101910776"),
                    ApcomplexWeberMath.weberE(q(7, 1), q(300, 1)), PRECISION - 1);
        // the two series cancel
        assertClose("E(7/2, 250)", new Apfloat("0.047380515514504736920319537398788916757818697"),
                    ApcomplexWeberMath.weberE(q(7, 2), q(250, 1)), PRECISION - 1);
        assertClose("E(-5/2, 80)", new Apfloat("-0.0928464996233765580501613827003959778173715608"),
                    ApcomplexWeberMath.weberE(q(-5, 2), q(80, 1)), PRECISION - 1);
        assertClose("E(1/3, 2+i)", new Apcomplex(new Apfloat("-0.796058029521489025606148219799477077501850262"), new Apfloat("-0.288867290298489452285542922770035461773656768")),
                    ApcomplexWeberMath.weberE(q(1, 3), new Apcomplex(q(2, 1), q(1, 1))), PRECISION - 1);
    }

    @Test
    public void testLargeArgument()
    {
        assertClose("E(1/2, 1500)", new Apfloat("-0.0024839280776082072151195633224014"),
                    ApcomplexAsymptoticMath.weberELargeArgument(q(1, 2), q(1500, 1).precision(30)), 28);
        assertClose("J(1/3, 20000)", new Apfloat("0.0043728441589955823628130669727813"),
                    ApcomplexAsymptoticMath.angerJLargeArgument(q(1, 3), q(20000, 1).precision(30)), 28);
        // the terms of the expansion grow before they reach the precision
        org.junit.jupiter.api.Assertions.assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.weberELargeArgument(q(1, 2), q(30, 1)));
    }
}
