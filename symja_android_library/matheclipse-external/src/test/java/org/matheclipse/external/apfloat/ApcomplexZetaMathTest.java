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
    }

    @Test
    public void testLerchPhiOfSquares()
    {
        // the terms with a negative k + a are real, where those of lerchPhi() are complex
        assertClose("Phi(3/10, 5/2, -3/2)", new Apfloat("2.57980500631959120376355322884182917693568703"),
                    ApcomplexZetaMath.lerchPhiOfSquares(q(3, 10), q(5, 2), q(-3, 2)), PRECISION - 2);
        assertClose("Hurwitz-Lerch Phi(3/10, 5/2, -3/2)", c("0.519861362170665575098225415389318683842310781", "-2.05994364414892562866532781345251049309337625"),
                    ApcomplexZetaMath.lerchPhi(q(3, 10), q(5, 2), q(-3, 2)), PRECISION - 2);
        assertClose("Phi(-7/10, 3/2, -13/4)", new Apfloat("-2.11492659592632682202072525744669488709182121"),
                    ApcomplexZetaMath.lerchPhiOfSquares(q(-7, 10), q(3, 2), q(-13, 4)), PRECISION - 2);
        // an even exponent: the two sums are the same
        assertClose("Phi(1/2 + i/4, 2, -3/2 + i)", c("-0.155546260835057943172758706107673996846040642", "0.272478302374136843316693953591304650120581227"),
                    ApcomplexZetaMath.lerchPhiOfSquares(new Apcomplex(q(1, 2), q(1, 4)), q(2, 1), new Apcomplex(q(-3, 2), q(1, 1))), PRECISION - 2);
        // on the negative imaginary axis the square root of the square is -a
        assertClose("Phi(3/10, 5/2, -i/2)", c("-3.89528612424743460056217092915561534099724659", "4.2172530055270929147229233660789481764182476"),
                    ApcomplexZetaMath.lerchPhiOfSquares(q(3, 10), q(5, 2), new Apcomplex(Apfloat.ZERO, q(-1, 2))), PRECISION - 2);
        // the term with k + a = 0 is left out: 1/8 + 3/10 + 27/1000 Phi(3/10, 3, 1)
        assertClose("Phi(3/10, 3, -2)", ApcomplexZetaMath.lerchPhi(q(3, 10), q(3, 1), q(1, 1)).multiply(q(27, 1000)).add(q(17, 40)),
                    ApcomplexZetaMath.lerchPhiOfSquares(q(3, 10), q(3, 1), q(-2, 1)), PRECISION - 3);
        assertThrows(ArithmeticException.class, () -> ApcomplexZetaMath.lerchPhiOfSquares(q(3, 10), q(5, 2), q(-1000001, 2)));
    }

    @Test
    public void testZetaOfLargeRealPart()
    {
        Apfloat one = new Apfloat(1, PRECISION);
        // the Dirichlet series
        assertClose("zeta(60) - 1", new Apfloat("8.673617380119933728342055067343e-19"),
                    ApcomplexZetaMath.zetaOfLargeRealPart(q(60, 1)).subtract(one), PRECISION - 20);
        assertClose("zeta(40 + 10^5 i) - 1", c("0.000000000000170465235973685338678416407976003950323543055", "0.000000000000893376897842167945310424909813522425607863847"),
                    ApcomplexZetaMath.zetaOfLargeRealPart(new Apcomplex(q(40, 1), q(100000, 1))).subtract(one), PRECISION - 14);
        assertClose("zeta(10^30)", one, ApcomplexZetaMath.zetaOfLargeRealPart(new Apfloat("1e30", PRECISION)), PRECISION - 1);
        // a real part which is too small for the series
        assertTrue(ApcomplexZetaMath.zetaOfLargeRealPart(q(5, 1)) == null);
        assertTrue(ApcomplexZetaMath.zetaOfLargeRealPart(q(-50, 1)) == null);
    }

    @Test
    public void testBernoulli()
    {
        // 2 n! zeta(n) / (2 pi)^n
        assertClose("B(1000)", new Apfloat("-5.31870446941552203648291374376708554520936601e1769"),
                    ApcomplexZetaMath.bernoulli(1000, PRECISION), PRECISION - 2);
        assertClose("B(62)", new Apfloat("2050097572347809756992173309567231025.16666667"),
                    ApcomplexZetaMath.bernoulli(62, PRECISION), PRECISION - 2);
        // the rational number of the library
        assertClose("B(60)", org.apfloat.AprationalMath.bernoulli(60).precision(PRECISION), ApcomplexZetaMath.bernoulli(60, PRECISION), PRECISION - 1);
        assertTrue(ApcomplexZetaMath.bernoulli(1001, PRECISION).signum() == 0);
        assertThrows(IllegalArgumentException.class, () -> ApcomplexZetaMath.bernoulli(-2, PRECISION));
    }

    @Test
    public void testZetaDerivative()
    {
        assertClose("zeta'(2)", new Apfloat("-0.937548254315843753702574094567864977897860289"),
                    ApcomplexZetaMath.zetaDerivative(q(2, 1)), PRECISION - 2);
        // with the reflection formula
        assertClose("zeta'(-7/2)", new Apfloat("0.00915421362994151246116075153449671106077615703"),
                    ApcomplexZetaMath.zetaDerivative(q(-7, 2)), PRECISION - 2);
        // at a trivial zero of the function
        assertClose("zeta'(-4)", new Apfloat("0.00798381145026862428069667079878930390523769336"),
                    ApcomplexZetaMath.zetaDerivative(q(-4, 1)), PRECISION - 2);
        assertClose("zeta'(1/2+20i)", c("0.714506790843775992376675382600255550384150014", "1.00524088394701315547272407660690518081618417"),
                    ApcomplexZetaMath.zetaDerivative(new Apcomplex(q(1, 2), q(20, 1))), PRECISION - 2);
        // next to the pole, where it is -1/(s-1)^2
        assertClose("zeta'(1001/1000)", new Apfloat("-999999.927193845906045828989141270727687351"),
                    ApcomplexZetaMath.zetaDerivative(q(1001, 1000)), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexZetaMath.zetaDerivative(q(1, 1)));
    }

    @Test
    public void testLerchPhiContinuation()
    {
        // beyond the unit circle, on the cut from 1
        assertClose("Phi(3, 2, 5/2)", c("-0.113891935915635448735805613698216781671887252", "-0.221406918969568486024779752568119546183649328"), ApcomplexZetaMath.lerchPhi(q(3, 1), q(2, 1), q(5, 2)), PRECISION - 2);
        // on the unit circle
        assertClose("Phi(-1, 2, 5/2)", new Apfloat("0.108306821153320504662858504173980887541041942"), ApcomplexZetaMath.lerchPhi(q(-1, 1), q(2, 1), q(5, 2)), PRECISION - 2);
        assertClose("Phi(99/100, 2, 5/2)", new Apfloat("0.458211381536511712892976540077370388899395877"), ApcomplexZetaMath.lerchPhi(q(99, 100), q(2, 1), q(5, 2)), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexZetaMath.lerchPhi(q(1, 1), q(2, 1), q(5, 2)));
    }
}
