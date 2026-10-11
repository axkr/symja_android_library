package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.InfiniteExpansionException;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatTrigMath}. The expected values were calculated with mpmath.
 */
public class ApfloatTrigMathTest
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
    public void testReciprocalFunctions()
    {
        Apfloat x = q(5, 2);
        assertClose("cot(5/2)", new Apfloat("-1.33864812830415136021088702399024513004059"), ApfloatTrigMath.cot(x), PRECISION - 2);
        assertClose("sec(5/2)", new Apfloat("-1.24821565146881783090643327407488299157829"), ApfloatTrigMath.sec(x), PRECISION - 2);
        assertClose("csc(5/2)", new Apfloat("1.67092154555867992794667687039599880135105"), ApfloatTrigMath.csc(x), PRECISION - 2);
        assertClose("coth(5/2)", new Apfloat("1.01356730981260846219203980180602556844858"), ApfloatTrigMath.coth(x), PRECISION - 2);
        assertClose("sech(5/2)", new Apfloat("0.163071231929977826043195139320894767452351"), ApfloatTrigMath.sech(x), PRECISION - 2);
        assertClose("csch(5/2)", new Apfloat("0.165283669855095564578652155098521114710007"), ApfloatTrigMath.csch(x), PRECISION - 2);
        // cosh and sinh overflow, their quotient does not
        assertClose("coth(10^20)", new Apfloat(1), ApfloatTrigMath.coth(new Apfloat("1e20", PRECISION)), PRECISION - 2);
        assertClose("sech(1000)", new Apfloat("1.01519177950989135305836189591486738386112e-434"), ApfloatTrigMath.sech(q(1000, 1)), PRECISION - 2);
        assertClose("csch(-1000)", new Apfloat("-1.01519177950989135305836189591486738386112e-434"), ApfloatTrigMath.csch(q(-1000, 1)), PRECISION - 2);
        assertClose("coth(10^-20)", new Apfloat("100000000000000000000.000000000000000000003"), ApfloatTrigMath.coth(new Apfloat("1e-20", PRECISION)), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.cot(Apfloat.ZERO));
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.csc(Apfloat.ZERO));
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.coth(Apfloat.ZERO));
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.csch(Apfloat.ZERO));
    }

    @Test
    public void testInverseFunctions()
    {
        Apfloat x = q(5, 2);
        assertClose("acot(5/2)", new Apfloat("0.380506377112364886303587916810433104497406"), ApfloatTrigMath.acot(x), PRECISION - 2);
        assertClose("asec(5/2)", new Apfloat("1.15927948072740859984658379402241583724288"), ApfloatTrigMath.asec(x), PRECISION - 2);
        assertClose("acsc(5/2)", new Apfloat("0.411516846067488019384737897617335604855701"), ApfloatTrigMath.acsc(x), PRECISION - 2);
        assertClose("acoth(5/2)", new Apfloat("0.423648930193601806855053753260327012494797"), ApfloatTrigMath.acoth(x), PRECISION - 2);
        assertClose("acsch(5/2)", new Apfloat("0.390035319770715276080163379883629644959705"), ApfloatTrigMath.acsch(x), PRECISION - 2);
        assertClose("asech(1/3)", new Apfloat("1.76274717403908605046521864995958461805632"), ApfloatTrigMath.asech(q(1, 3)), PRECISION - 2);
        // next to the branch points
        assertClose("acoth(1.0000000001)", new Apfloat("11.859499055275201074797948334150930154985"), ApfloatTrigMath.acoth(new Apfloat("1.0000000001", PRECISION)), PRECISION - 2);
        assertClose("asech(0.9999999999)", new Apfloat("0.0000141421356243202061390436838352559217193554"), ApfloatTrigMath.asech(new Apfloat("0.9999999999", PRECISION)), PRECISION - 2);
        // acot is odd with a jump at zero
        Apfloat halfPi = ApfloatMath.pi(PRECISION).divide(new Apfloat(2)),
                small = new Apfloat("1e-50", PRECISION);
        assertClose("acot(1e-50)", halfPi, ApfloatTrigMath.acot(small), PRECISION - 2);
        assertClose("acot(-1e-50)", halfPi.negate(), ApfloatTrigMath.acot(small.negate()), PRECISION - 2);
    }

    @Test
    public void testRealDomain()
    {
        // where the function of a real argument is not real
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.asec(q(1, 3)));
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.acsc(q(-1, 3)));
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.acoth(q(1, 3)));
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.asech(q(5, 2)));
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.asech(q(-1, 3)));
        // poles
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.asec(Apfloat.ZERO));
        assertThrows(ArithmeticException.class, () -> ApfloatTrigMath.acsch(Apfloat.ZERO));
    }

    @Test
    public void testExactArguments()
    {
        // an exact number has infinite precision: the result has the precision which is given
        Apfloat halfPi = ApfloatMath.pi(PRECISION).divide(new Apfloat(2));
        assertEquals(halfPi, ApfloatTrigMath.acot(Apfloat.ZERO, PRECISION));
        assertThrows(InfiniteExpansionException.class, () -> ApfloatTrigMath.acot(Apfloat.ZERO));
        assertClose("acsc(1)", halfPi, ApfloatTrigMath.acsc(new Apint(1), PRECISION), PRECISION - 2);
        assertClose("asec(-1)", ApfloatMath.pi(PRECISION), ApfloatTrigMath.asec(new Apint(-1), PRECISION), PRECISION - 2);
        assertClose("acsch(2)", new Apfloat("0.48121182505960344749775891342436842313518"), ApfloatTrigMath.acsch(new Apint(2), PRECISION), PRECISION - 2);
        assertThrows(InfiniteExpansionException.class, () -> ApfloatTrigMath.acsch(new Apint(2)));
    }
}
