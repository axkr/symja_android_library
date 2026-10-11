package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexStruveMath} and {@link ApcomplexAsymptoticMath}. The expected values
 * were calculated with mpmath.
 */
public class ApcomplexStruveMathTest
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
    public void testStruveH()
    {
        // an order below the argument: the asymptotic expansion
        assertClose("H(401/2, 10000)", new Apfloat("6.29584497732206863572177868919880063232982134e362"),
                    ApcomplexStruveMath.struveH(q(401, 2), q(10000, 1)), 37);
        assertClose("H(3/10, 60)", new Apfloat("0.128487741682796155163898969908220512461842086"),
                    ApcomplexStruveMath.struveH(q(3, 10), q(60, 1)), 37);
        // the expansion doesn't reach 40 digits: the series with extra digits
        assertClose("H(3/10, 20 + 3 i)", c("0.0309253723592520440231287136114442625236362739", "1.7672721818137484813682701203756094063896005"),
                    ApcomplexStruveMath.struveH(q(3, 10), new Apcomplex(q(20, 1), q(3, 1))), 37);
        // an order above the argument, where the expansion is not valid
        assertClose("H(401/2, 100)", new Apfloat("6.58648647757561537074786653604570461037152246e-37"),
                    ApcomplexStruveMath.struveH(q(401, 2), q(100, 1)), 37);
    }

    @Test
    public void testStruveL()
    {
        assertClose("L(3/10, 40)", new Apfloat("14877812549676171.9093606133939844387685336574"),
                    ApcomplexStruveMath.struveL(q(3, 10), q(40, 1)), 37);
        assertClose("L(5/2, 60 + 10 i)", c("-4878782889830143738510342.1572729400903531689", "-2670914144672146705603666.76072707736172279082"),
                    ApcomplexStruveMath.struveL(q(5, 2), new Apcomplex(q(60, 1), q(10, 1))), 37);
        // next to the imaginary axis the expansion leaves out a term which is not small
        assertClose("L(3/10, 1/2 + 50 i)", c("0.0372976711661534978563070316946980152152632386", "-0.0664907548737491438063063241373586892947483984"),
                    ApcomplexStruveMath.struveL(q(3, 10), new Apcomplex(q(1, 2), q(50, 1))), 37);
        assertClose("L(401/2, 100)", new Apfloat("6.76927175093007210939050667895731373337574238e-31"),
                    ApcomplexStruveMath.struveL(q(401, 2), q(100, 1)), 37);
    }

    @Test
    public void testStruveExpansionDeclines()
    {
        // an order above the argument
        assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.struveH(q(401, 2), q(100, 1)));
        // an integer order
        assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.struveH(q(2, 1), q(100, 1)));
        // too close to the imaginary axis
        assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.struveL(q(3, 10), new Apcomplex(q(1, 2), q(50, 1))));
    }

    @Test
    public void testAngerJ()
    {
        // the order carries the digits which its reduction modulo 2 takes away
        Apfloat ν = new Aprational(new Apint(4001), new Apint(4)).precision(PRECISION + 10);
        assertClose("J(4001/4, 5)", new Apfloat("0.000223903584114817768087624572199108603191481449"),
                    ApcomplexAsymptoticMath.angerJ(ν, q(5, 1)), 37);
        ν = new Aprational(new Apint(-2001), new Apint(2)).precision(PRECISION + 10);
        assertClose("J(-2001/2, 4/5)", new Apfloat("0.000318405408061086484198114092778925472521202181"),
                    ApcomplexAsymptoticMath.angerJ(ν, q(4, 5)), 37);
        // at an integer order the expansion vanishes and the function is the Bessel function
        assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.angerJ(new Apfloat(1000, PRECISION + 10), q(990, 1)));
        // an argument above the order
        assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.angerJ(new Apfloat("1000.25", PRECISION + 10), q(2000, 1)));
    }

    @Test
    public void testHermiteH()
    {
        assertClose("H(1/3, 20)", new Apfloat("3.42042622929695955373103985130302985423463325"),
                    ApcomplexAsymptoticMath.hermiteH(q(1, 3), q(20, 1)), 37);
        assertClose("H(-3/2, 30 + 10 i)", c("0.0017602833995979376066552803730160448696789178", "-0.000921047079011335249645276477656910148036824202"),
                    ApcomplexAsymptoticMath.hermiteH(q(-3, 2), new Apcomplex(q(30, 1), q(10, 1))), 37);
        // a nonnegative integer order terminates: H(2, z) = 4 z^2 - 2
        assertClose("H(2, 5)", new Apfloat(98, PRECISION), ApcomplexAsymptoticMath.hermiteH(q(2, 1), q(5, 1)), 38);
        // too small an argument for 40 digits
        assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.hermiteH(q(5, 2), q(3, 2)));
    }
}
