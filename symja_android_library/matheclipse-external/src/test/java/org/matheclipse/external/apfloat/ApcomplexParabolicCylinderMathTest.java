package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexParabolicCylinderMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexParabolicCylinderMathTest
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
    public void testParabolicCylinderD()
    {
        assertClose("D(1/3, 5/2)", new Apfloat("0.288894041930229473108452533510980110594952653"), ApcomplexParabolicCylinderMath.parabolicCylinderD(q(1, 3), q(5, 2)), PRECISION - 2);
        assertClose("D(2, 1/2+i)", new Apcomplex(new Apfloat("-1.74685393670813428929231174415274196045520988"), new Apfloat("1.69097721619592985286119294027231683523137529")),
                    ApcomplexParabolicCylinderMath.parabolicCylinderD(q(2, 1), new Apcomplex(q(1, 2), q(1, 1))), PRECISION - 2);
        // large on the negative axis
        assertClose("D(-5/2, -10)", new Apfloat("4309674085362.18187190726380790033290057449007"), ApcomplexParabolicCylinderMath.parabolicCylinderD(q(-5, 2), q(-10, 1)), PRECISION - 2);
        // an odd order: only the odd series is left
        assertClose("D(5, 5/2)", new Apfloat("-4.42149019771846969386794801191030069695225105"), ApcomplexParabolicCylinderMath.parabolicCylinderD(q(5, 1), q(5, 2)), PRECISION - 2);
    }

    @Test
    public void testScorer()
    {
        assertClose("Gi(5/2)", new Apfloat("0.13827755841857411984929518771423016888999611"), ApcomplexParabolicCylinderMath.scorerGi(q(5, 2)), PRECISION - 2);
        // the terms of the series are 10^47 times the sum
        assertClose("Gi(30)", new Apfloat("0.0106111160731938282303635345798386318867656373"), ApcomplexParabolicCylinderMath.scorerGi(q(30, 1)), PRECISION - 2);
        assertClose("Hi(-30)", new Apfloat("0.0106095441701062579222454430167902235013320657"), ApcomplexParabolicCylinderMath.scorerHi(q(-30, 1)), PRECISION - 2);
        assertClose("Hi(1/2+i)", new Apcomplex(new Apfloat("0.349304687636805842291452017453022355645100548"), new Apfloat("0.397100228989780901207959703624836247554325613")),
                    ApcomplexParabolicCylinderMath.scorerHi(new Apcomplex(q(1, 2), q(1, 1))), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexParabolicCylinderMath.scorerGi(q(1000, 1)));
    }
}
