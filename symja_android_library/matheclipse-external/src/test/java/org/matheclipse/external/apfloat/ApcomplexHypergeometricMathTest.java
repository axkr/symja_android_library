package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexHypergeometricMath} and {@link ApcomplexPolynomialMath}. The expected
 * values were calculated with mpmath.
 */
public class ApcomplexHypergeometricMathTest
{
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

    @Test
    public void testHypergeometricOnesTwos()
    {
        // the terms grow to e^30 before they cancel
        assertClose("3F3(1,1,1;2,2,2;-30)", new Apfloat("0.2912117405986065604589737676474355367181"),
                    ApcomplexHypergeometricMath.hypergeometricOnesTwos(3, new Apfloat(-30, 30)), 29);
        assertClose("2F2(1,1;2,2;3+4i)", new Apcomplex(new Apfloat("-0.2221496831537595277999920234622136903921"), new Apfloat("1.418574044879261118585663663948402662469")),
                    ApcomplexHypergeometricMath.hypergeometricOnesTwos(2, new Apcomplex(new Apfloat(3, 30), new Apfloat(4, 30))), 29);
    }

    @Test
    public void testPolynomialRoot()
    {
        // x^5 - x - 1
        Apcomplex[] coefficients = { new Apfloat(-1, 60), new Apfloat(-1, 60), Apcomplex.ZERO, Apcomplex.ZERO, Apcomplex.ZERO, new Apfloat(1, 60) };
        assertClose("root of x^5-x-1", new Apfloat("1.167303978261418684256045899854842180721"),
                    ApcomplexPolynomialMath.root(coefficients, new Apfloat("1.1673", 60), 1.0, 40), 39);
        // an iteration which ends at another root
        assertThrows(ArithmeticException.class, () -> ApcomplexPolynomialMath.root(coefficients, new Apfloat("1.1673", 60), 1.0e-10, 40));
        // a double root: x^2
        Apcomplex[] square = { Apcomplex.ZERO, Apcomplex.ZERO, new Apfloat(1, 60) };
        assertThrows(ArithmeticException.class, () -> ApcomplexPolynomialMath.root(square, Apcomplex.ZERO, 1.0, 40));
    }

    private static Apfloat r(long p, long q)
    {
        return new org.apfloat.Aprational(new org.apfloat.Apint(p), new org.apfloat.Apint(q)).precision(30);
    }

    @Test
    public void testHypergeometricPFQ()
    {
        Apcomplex[] a2 = { r(1, 3), r(5, 2) },
                    a3 = { r(1, 3), r(5, 2), r(1, 1) },
                    b2 = { r(7, 3), r(3, 2) };
        // the terms grow to e^400 before they cancel
        assertClose("2F2(-400)", new Apfloat("0.1256697428849529268621477327904677817141"),
                    ApcomplexHypergeometricMath.hypergeometricPFQ(a2, b2, r(-400, 1)), 28);
        assertClose("3F2(9/10)", new Apfloat("1.606708432655421067816179990400533689554"),
                    ApcomplexHypergeometricMath.hypergeometricPFQ(a3, b2, r(9, 10)), 28);
        // outside the unit circle, on the cut: the value from below
        assertClose("3F2(3)", new Apcomplex(new Apfloat("0.6560531843921831173849552341133517445259"), new Apfloat("-0.7171222010810242390276692075995769511275")),
                    ApcomplexHypergeometricMath.hypergeometricPFQ(a3, b2, r(3, 1)), 28);
        // upper parameters which differ by an integer
        assertClose("3F2(1,1,1;2,2;-5)", new Apfloat("0.5498558252121616580051175030752537288994"),
                    ApcomplexHypergeometricMath.hypergeometricPFQ(new Apcomplex[] { r(1, 1), r(1, 1), r(1, 1) }, new Apcomplex[] { r(2, 1), r(2, 1) }, r(-5, 1)), 28);
        // a polynomial
        assertClose("3F0(-12)", new Apfloat("122652861136845.3792758099172668038408779"),
                    ApcomplexHypergeometricMath.hypergeometricPFQ(new Apcomplex[] { r(-12, 1), r(1, 3), r(5, 2) }, new Apcomplex[0], r(1, 2)), 28);
        assertClose("1F2(-200)", new Apfloat("0.1944327747783915796980939420099209533334"),
                    ApcomplexHypergeometricMath.hypergeometricPFQ(new Apcomplex[] { r(1, 3) }, b2, r(-200, 1)), 28);
        // next to the unit circle, a divergent series, a pole
        assertThrows(ArithmeticException.class, () -> ApcomplexHypergeometricMath.hypergeometricPFQ(a3, b2, r(1, 1)));
        assertThrows(ArithmeticException.class, () -> ApcomplexHypergeometricMath.hypergeometricPFQ(a2, new Apcomplex[0], r(-1, 100)));
        assertThrows(ArithmeticException.class, () -> ApcomplexHypergeometricMath.hypergeometricPFQ(new Apcomplex[] { r(-4, 1), r(5, 2) }, new Apcomplex[] { r(-2, 1) }, r(1, 2)));
    }

    @Test
    public void testHypergeometricPFQOnTheUnitCircle()
    {
        Apcomplex[] a = { r(1, 3), r(5, 2), r(1, 1) },
                    b = { r(7, 3), r(5, 2) };
        // the series converges like k^(-2) at 1, and not at all at -1 and i: its Levin transform
        assertClose("3F2(1)", new Apfloat("1.33333333333333333333333333333333333333333333"), ApcomplexHypergeometricMath.hypergeometricPFQ(a, b, r(1, 1)), 28);
        assertClose("3F2(-1)", new Apfloat("0.895063595372589475565609225866962044764059007"), ApcomplexHypergeometricMath.hypergeometricPFQ(a, b, r(-1, 1)), 28);
        assertClose("3F2(i)", new Apcomplex(new Apfloat("0.955995213070746608862017780385942487242996995"), new Apfloat("0.12072951692597884938506050757882211578013749")),
                    ApcomplexHypergeometricMath.hypergeometricPFQ(a, b, new Apcomplex(Apfloat.ZERO, r(1, 1))), 28);
    }
}
