package org.matheclipse.external.apfloat;

import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexBesselMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexBesselMathTest
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
    public void testBesselY()
    {
        assertClose("Y(0, 5)", new Apfloat("-0.308517625249033780073648984212046611386347062"),
                    ApcomplexBesselMath.besselY(q(0, 1), q(5, 1)), PRECISION - 1);
        // an integer order with a large argument
        assertClose("Y(3, 100)", new Apfloat("0.0234457866877609115602292983749693789144530001"),
                    ApcomplexBesselMath.besselY(q(3, 1), q(100, 1)), PRECISION - 1);
        // the two Bessel functions of the first kind cancel
        assertClose("Y(-5/2, 1/100)", new Apfloat("0.000000531919241095508045723630590451015402313026206"),
                    ApcomplexBesselMath.besselY(q(-5, 2), q(1, 100)), PRECISION - 1);
        assertThrows(ArithmeticException.class, () -> ApcomplexBesselMath.besselY(q(1, 1), q(0, 1)));
    }

    @Test
    public void testLargeArgument()
    {
        // the phase takes twice the digits in front of the decimal point
        assertClose("J(0, 10^100)", new Apfloat("-7.33704873653861571243692861417547889358875997e-51"),
                    ApcomplexBesselMath.besselJ(q(0, 1), new Apfloat("1e100", PRECISION)), PRECISION - 1);
        assertClose("Y(1/3, 10^100)", new Apfloat("6.38371677504482546122359215350099660259871349e-51"),
                    ApcomplexBesselMath.besselY(q(1, 3), new Apfloat("1e100", PRECISION)), PRECISION - 1);
        assertClose("J(5/2, 10^30)", new Apfloat("7.1902884703081171615311395126562876309600011e-17"),
                    ApcomplexBesselMath.besselJ(q(5, 2), new Apfloat("1e30", PRECISION)), PRECISION - 1);
        // Hankel's expansion
        assertClose("J(0, 10^300)", new Apfloat("-6.51054959171479341636564806480688593600489043e-151"),
                    ApcomplexBesselMath.besselJ(q(0, 1), new Apfloat("1e300", PRECISION)), PRECISION - 1);
        assertClose("Y(7, 10^300)", new Apfloat("-6.51054959171479341636564806480688593600489043e-151"),
                    ApcomplexBesselMath.besselY(q(7, 1), new Apfloat("1e300", PRECISION)), PRECISION - 1);
        // which has no digit for an argument of the size of the order
        assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.besselJY(q(1, 3), q(7, 2)));
        // at a half-integer order the series ends, and the expansion is the function
        assertClose("J(5/2, 7/2)", ApcomplexBesselMath.besselJ(q(5, 2), q(7, 2)), ApcomplexAsymptoticMath.besselJY(q(5, 2), q(7, 2))[0], PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexAsymptoticMath.besselJY(q(10000000000L, 1), new Apfloat("1e16", PRECISION)));
    }

    @Test
    @Tag("slow")
    public void testLargeOrder()
    {
        // the expansion of the library for a large argument cancels about nu^2 / z digits
        assertClose("I(3001/2, 1200)", new Apfloat("3.45349350008928163081519183936231397071671943e149"),
                    ApcomplexBesselMath.besselI(q(3001, 2), q(1200, 1)), PRECISION - 1);
        // too many for the functions of the second kind
        assertThrows(org.apfloat.LossOfPrecisionException.class, () -> ApcomplexBesselMath.besselK(q(3001, 2), q(1200, 1)));
    }

    @Test
    @Tag("slow")
    public void testBesselK()
    {
        // an integer order with a large argument
        assertClose("K(2, 60)", new Apfloat("1.46141890810967795320279258525600845013346421e-27"),
                    ApcomplexBesselMath.besselK(q(2, 1), q(60, 1)), PRECISION - 1);
        assertClose("K(1, 40+40i)",
                    new Apcomplex(new Apfloat("-6.42487667971333679293111405641338907580768724e-19"),
                                  new Apfloat("-3.05124579288040251823838207377774884661873657e-19")),
                    ApcomplexBesselMath.besselK(q(1, 1), new Apcomplex(q(40, 1), q(40, 1))), PRECISION - 1);
        assertThrows(ArithmeticException.class, () -> ApcomplexBesselMath.besselK(q(1, 1), q(0, 1)));
    }

    @Test
    public void testSphericalBessel()
    {
        assertClose("j(3, 1/2)", new Apfloat("0.0011740354438675573089672593115209"),
                    ApcomplexBesselMath.sphericalBesselJ(q(3, 1).precision(30), q(1, 2).precision(30)), 28);
        assertClose("y(0, 60)", new Apfloat("0.015873549673585938211563609933216"),
                    ApcomplexBesselMath.sphericalBesselY(q(0, 1), q(60, 1).precision(30)), 28);
    }
}
