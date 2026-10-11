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
 * Tests of {@link ApcomplexZetaMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexZetaMathTest
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
    public void testPolylogOfImaginaryOrder()
    {
        // an order on the imaginary axis with an argument between 0 and 1
        assertClose("Li(i, 9/10)", c("-1.8249618890069197527777458082656", "-5.0071682282891799898521397037109"),
                    ApcomplexZetaMath.polylog(new Apcomplex(Apfloat.ZERO, q(1, 1)), q(9, 10)), PRECISION - 1);
        assertClose("Li(-3i, 1/3)", c("0.23808715550794144471120110938286", "0.075512338965162940313377456438391"),
                    ApcomplexZetaMath.polylog(new Apcomplex(Apfloat.ZERO, q(-3, 1)), q(1, 3)), PRECISION - 1);
    }

    @Test
    public void testHurwitzZeta()
    {
        // a positive real part of the second argument is the function of apfloat
        assertClose("zeta(3/2, 2 + i)", c("1.4233898216682515940711952459", "-0.418015008964862953694721690616"),
                    ApcomplexZetaMath.hurwitzZeta(q(3, 2), new Apcomplex(q(2, 1), q(1, 1))), 28);
        // a negative second argument: the principal value of the powers of the negative numbers
        assertClose("zeta(1/2, -3/2)", c("-0.604898643421630370247265914236", "-2.23071014330082108153411674911"),
                    ApcomplexZetaMath.hurwitzZeta(q(1, 2), q(-3, 2)), 28);
        assertClose("zeta(2 + i, -7/2)", c("87.00590411222613861651034", "52.38695491904497144200485"),
                    ApcomplexZetaMath.hurwitzZeta(new Apcomplex(q(2, 1), q(1, 1)), q(-7, 2)), 24);
        // an integer power of a negative number is real
        Apcomplex real = ApcomplexZetaMath.hurwitzZeta(q(3, 1), q(-5, 2));
        assertEquals(0, real.imag().signum());
        assertClose("zeta(3, -5/2)", new Apfloat("0.0541020258208637015018708342839"), real, 28);
        // a pole of one of the terms
        assertThrows(ArithmeticException.class, () -> ApcomplexZetaMath.hurwitzZeta(q(1, 2), q(-3, 1)));
    }

    @Test
    public void testGeneralizedZeta()
    {
        assertClose("Zeta[1/2, -3/2]", new Apfloat("1.62581149987919071128685083488"),
                    ApcomplexZetaMath.generalizedZeta(q(1, 2), q(-3, 2)), 28);
        assertClose("Zeta[3, -5/2]", new Apfloat("16.7746946184134562940944634269"),
                    ApcomplexZetaMath.generalizedZeta(q(3, 1), q(-5, 2)), 28);
        assertClose("Zeta[2, -7/2 + i]", c("-0.160971415175897264864254747496", "-0.0580482973104891845191577705986"),
                    ApcomplexZetaMath.generalizedZeta(q(2, 1), new Apcomplex(q(-7, 2), q(1, 1))), 28);
        // the term with k + a = 0 is left out
        assertClose("Zeta[1/2, -3]", new Apfloat("0.824102541566586476020493990092"),
                    ApcomplexZetaMath.generalizedZeta(q(1, 2), q(-3, 1)), 28);
    }

    @Test
    public void testRiemannSiegelTheta()
    {
        assertClose("theta(10)", new Apfloat("-3.06707439628989529170201353481"),
                    ApcomplexZetaMath.riemannSiegelTheta(q(10, 1)), 28);
        assertClose("theta(3 + i/3)", c("-3.00391343048380515176395030487", "-0.123681586491507055332626962391"),
                    ApcomplexZetaMath.riemannSiegelTheta(new Apcomplex(q(3, 1), q(1, 3))), 28);
        // next to the zero of the function the two terms cancel: the argument determines one digit
        Apfloat nearZero = ApcomplexZetaMath.riemannSiegelTheta(new Apfloat("17.8455995404108608168263384125", PRECISION));
        assertTrue(nearZero.precision() <= 3, "precision " + nearZero.precision());
        assertClose("theta next to its zero", new Apfloat("-9.966256021e-30"), nearZero, 1);
    }

    @Test
    @org.junit.jupiter.api.Tag("slow")
    public void testStieltjesGamma()
    {
        assertClose("gamma(1)", new Apfloat("-0.072815845483676724860586375874901"),
                    ApcomplexZetaMath.stieltjesGamma(1, q(1, 1)), PRECISION - 2);
        assertClose("gamma(1, 1/3)", new Apfloat("-3.2595575159179101952508745826766"),
                    ApcomplexZetaMath.stieltjesGamma(1, q(1, 3)), PRECISION - 2);
        assertClose("gamma(2, 5/2)", new Apfloat("-0.1016428552115557576809181346649"),
                    ApcomplexZetaMath.stieltjesGamma(2, q(5, 2)), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexZetaMath.stieltjesGamma(1, q(-1, 2)));
        assertThrows(IllegalArgumentException.class, () -> ApcomplexZetaMath.stieltjesGamma(-1, q(1, 2)));
    }

    @Test
    public void testLerchPhi()
    {
        assertClose("Phi(1/3, 1+i, 1/3)", c("1.6398513024579957768517764493553", "2.5508485997149758558804991632636"),
                    ApcomplexZetaMath.lerchPhi(q(1, 3), new Apcomplex(q(1, 1), q(1, 1)), q(1, 3)), PRECISION - 2);
        assertClose("Phi(-7/10, 1/2, 5/2)", new Apfloat("0.40012796681291791168003049682211"),
                    ApcomplexZetaMath.lerchPhi(q(-7, 10), q(1, 2), q(5, 2)), PRECISION - 2);
        // outside of the reach of the series
        assertThrows(ArithmeticException.class, () -> ApcomplexZetaMath.lerchPhi(q(3, 1), q(2, 1), q(1, 2)));
    }
}
