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
 * Tests of {@link ApcomplexTrigMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexTrigMathTest
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
        Apcomplex z = new Apcomplex(q(1, 2), q(1, 1));
        assertClose("cot(1/2+i)", c("0.261172821133439187473697849087165974549693", "-1.12569224807059883019500857231964015543907"), ApcomplexTrigMath.cot(z), PRECISION - 2);
        assertClose("sec(1/2+i)", c("0.629485781359569554328159123381701857571619", "0.261904347488437633859126026134836275548645"), ApcomplexTrigMath.sec(z), PRECISION - 2);
        assertClose("csc(1/2+i)", c("0.459228271084788721662816074692450589835397", "-0.64020496704646348267016091998451863347132"), ApcomplexTrigMath.csc(z), PRECISION - 2);
        assertClose("coth(1/2+i)", c("0.599828866643342290068370358510918284512372", "-0.464110186344714939232521099036726273823637"), ApcomplexTrigMath.coth(z), PRECISION - 2);
        assertClose("sech(1/2+i)", c("1.08126832311555084753651176025030253627021", "-0.778194035118073232807267948161101312912095"), ApcomplexTrigMath.sech(z), PRECISION - 2);
        assertClose("csch(1/2+i)", c("0.287408174140753481448070428383036416612304", "-0.968610889043278923649599803102960088829516"), ApcomplexTrigMath.csch(z), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexTrigMath.cot(Apcomplex.ZERO));
        assertThrows(ArithmeticException.class, () -> ApcomplexTrigMath.csch(Apcomplex.ZERO));
    }

    @Test
    public void testInverseFunctions()
    {
        Apcomplex z = new Apcomplex(q(1, 2), q(1, 1));
        assertClose("acot(1/2+i)", c("0.66290883183401623252961960521423781559222", "-0.708303336014054020062383654468281633897051"), ApcomplexTrigMath.acot(z), PRECISION - 2);
        assertClose("asec(1/2+i)", c("1.25957052955013552390485015766575599054792", "0.763884345953711045406919441806476410345603"), ApcomplexTrigMath.asec(z), PRECISION - 2);
        assertClose("acsc(1/2+i)", c("0.311225797244761095326471533973995451550666", "-0.763884345953711045406919441806476410345603"), ApcomplexTrigMath.acsc(z), PRECISION - 2);
        assertClose("acoth(1/2+i)", c("0.238877861256859090363182027084782741319917", "-0.723220666124067592099983421237940208262707"), ApcomplexTrigMath.acoth(z), PRECISION - 2);
        assertClose("asech(1/2+i)", c("0.763884345953711045406919441806476410345603", "-1.25957052955013552390485015766575599054792"), ApcomplexTrigMath.asech(z), PRECISION - 2);
        assertClose("acsch(1/2+i)", c("0.53321829058411214108324400278040499274759", "-0.77308635671950473342021856983828475662614"), ApcomplexTrigMath.acsch(z), PRECISION - 2);
    }

    @Test
    public void testBranchCuts()
    {
        Apfloat third = q(1, 3),
                halfPi = ApfloatMath.pi(PRECISION).divide(new Apfloat(2)),
                acosh3 = new Apfloat("1.76274717403908605046521864995958461805632"),
                atanh3 = new Apfloat("0.34657359027997265470861606072908828403775");
        Apcomplex i3 = new Apcomplex(Apfloat.ZERO, third);
        // inside (-1, 1), where the values of a real argument are complex
        assertClose("asec(1/3)", new Apcomplex(Apfloat.ZERO, acosh3), ApcomplexTrigMath.asec(third), PRECISION - 2);
        assertClose("acsc(1/3)", new Apcomplex(halfPi, acosh3.negate()), ApcomplexTrigMath.acsc(third), PRECISION - 2);
        assertClose("acoth(1/3)", new Apcomplex(atanh3, halfPi.negate()), ApcomplexTrigMath.acoth(third), PRECISION - 2);
        // outside (0, 1], on both sides
        assertClose("asech(-5/2)", c("0", "1.98231317286238463861605958925708704695429"), ApcomplexTrigMath.asech(q(-5, 2)), PRECISION - 2);
        assertClose("asech(5/2)", c("0", "1.15927948072740859984658379402241583724288"), ApcomplexTrigMath.asech(q(5, 2)), PRECISION - 2);
        // on the imaginary axis between -i and i
        assertClose("acsch(i/3)", new Apcomplex(acosh3.negate(), halfPi.negate()), ApcomplexTrigMath.acsch(i3), PRECISION - 2);
        assertClose("acot(i/3)", new Apcomplex(halfPi.negate(), atanh3.negate()), ApcomplexTrigMath.acot(i3), PRECISION - 2);
        // next to the branch point i
        assertClose("acot(1.0000000001 i)", c("0", "-11.859499055275201074797948334150930154985"),
                    ApcomplexTrigMath.acot(new Apcomplex(Apfloat.ZERO, new Apfloat("1.0000000001", PRECISION))), PRECISION - 2);
    }

    @Test
    public void testZero()
    {
        // a zero has infinite precision: the result has the precision which is given
        Apfloat halfPi = ApfloatMath.pi(PRECISION).divide(new Apfloat(2));
        assertEquals(new Apcomplex(halfPi), ApcomplexTrigMath.acot(Apcomplex.ZERO, PRECISION));
        assertEquals(new Apcomplex(Apfloat.ZERO, halfPi), ApcomplexTrigMath.acoth(Apcomplex.ZERO, PRECISION));
        assertThrows(InfiniteExpansionException.class, () -> ApcomplexTrigMath.acot(Apcomplex.ZERO));
        assertThrows(ArithmeticException.class, () -> ApcomplexTrigMath.asec(Apcomplex.ZERO, PRECISION));
        assertThrows(ArithmeticException.class, () -> ApcomplexTrigMath.acsch(Apcomplex.ZERO, PRECISION));
        // a real argument gives the value of the real function
        assertClose("acot(0.3)", ApfloatTrigMath.acot(new Apfloat("0.3", PRECISION)), ApcomplexTrigMath.acot(new Apcomplex(new Apfloat("0.3", PRECISION))), PRECISION - 2);
    }
}
