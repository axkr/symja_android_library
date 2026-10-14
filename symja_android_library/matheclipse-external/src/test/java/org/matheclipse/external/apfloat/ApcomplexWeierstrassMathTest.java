package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexWeierstrassMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexWeierstrassMathTest
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

    // the roots of 4 t^3 - 12 t - 4
    private static final Apfloat E1 = new Apfloat("1.8793852415718167681082185546494629398724162686"),
                                 E2 = new Apfloat("-0.347296355333860697703433253538629592000751354354"),
                                 E3 = new Apfloat("-1.53208888623795607040478530111083334787166491407");

    @Test
    public void testWeierstrassP()
    {
        Apfloat z = q(1, 3);
        Apcomplex expected = new Apfloat("9.06859872743213464027503708312066215286412015"),
                  derivative = new Apfloat("-53.5757808566723929058692544483301390704197158");
        assertClose("P(1/3)", expected, ApcomplexWeierstrassMath.weierstrassP(z, E1, E2, E3), PRECISION - 2);
        // the function does not depend on the order of the roots
        assertClose("P(1/3), other order", expected, ApcomplexWeierstrassMath.weierstrassP(z, E3, E1, E2), PRECISION - 2);
        assertClose("P'(1/3)", derivative, ApcomplexWeierstrassMath.weierstrassPPrime(z, E1, E2, E3), PRECISION - 2);
        assertClose("P'(1/3), other order", derivative, ApcomplexWeierstrassMath.weierstrassPPrime(z, E2, E3, E1), PRECISION - 2);
        // the differential equation P'^2 = 4 P^3 - g2 P - g3 at a complex argument
        Apcomplex w = new Apcomplex(q(1, 2), q(-3, 4)),
                  p = ApcomplexWeierstrassMath.weierstrassP(w, E1, E2, E3),
                  dp = ApcomplexWeierstrassMath.weierstrassPPrime(w, E1, E2, E3);
        assertClose("differential equation", p.multiply(p).multiply(p).multiply(new Apint(4)).subtract(p.multiply(new Apint(12))).subtract(new Apint(4)),
                    dp.multiply(dp), PRECISION - 4);
        assertThrows(ArithmeticException.class, () -> ApcomplexWeierstrassMath.weierstrassP(z, E1, E2, E1));
    }

    @Test
    public void testHalfPeriodsAndInvariants()
    {
        Apcomplex[] periods = ApcomplexWeierstrassMath.weierstrassHalfPeriods(E1.precision(PRECISION), E2.precision(PRECISION), E3.precision(PRECISION));
        assertClose("real half-period", new Apfloat("0.943487520500501860596609400101505641379384265"), periods[0], PRECISION - 2);
        assertClose("imaginary half-period", new Apcomplex(Apfloat.ZERO, new Apfloat("1.08877571311878374489564022924402972877072383")), periods[1], PRECISION - 2);
        Apcomplex[] invariants = ApcomplexWeierstrassMath.weierstrassInvariants(periods[0], periods[1]);
        assertClose("g2", new Apfloat(12), invariants[0], PRECISION - 4);
        assertClose("g3", new Apfloat(4), invariants[1], PRECISION - 4);
        // the same lattice from the half-periods in the other order
        invariants = ApcomplexWeierstrassMath.weierstrassInvariants(periods[1], periods[0]);
        assertClose("g2, other order", new Apfloat(12), invariants[0], PRECISION - 4);
        assertClose("g3, other order", new Apfloat(4), invariants[1], PRECISION - 4);
        assertThrows(ArithmeticException.class, () -> ApcomplexWeierstrassMath.weierstrassInvariants(q(1, 1), q(2, 1)));
    }

    @Test
    public void testZetaAndSigma()
    {
        // the reference values satisfy zeta' = -P and sigma' / sigma = zeta
        Apfloat z = q(1, 3);
        Apcomplex zeta = new Apfloat("2.992467037562534582971273182773017422272"),
                  sigma = new Apfloat("0.3331253491651603091207854959417316575662");
        assertClose("zeta(1/3)", zeta, ApcomplexWeierstrassMath.weierstrassZeta(z, E1, E2, E3), PRECISION - 2);
        assertClose("sigma(1/3)", sigma, ApcomplexWeierstrassMath.weierstrassSigma(z, E1, E2, E3), PRECISION - 2);
        // the functions belong to the lattice: the order of the roots, which gives other half-periods, does not matter
        assertClose("zeta(1/3), other order", zeta, ApcomplexWeierstrassMath.weierstrassZeta(z, E3, E1, E2), PRECISION - 2);
        assertClose("sigma(1/3), other order", sigma, ApcomplexWeierstrassMath.weierstrassSigma(z, E2, E3, E1), PRECISION - 2);
        Apcomplex w = new Apcomplex(q(1, 2), q(-3, 4));
        assertClose("zeta(1/2 - 3/4 i)", new Apcomplex(new Apfloat("0.7497446108397828520359460006002833166741"), new Apfloat("0.9394226217615553289916783834123321002126")),
                    ApcomplexWeierstrassMath.weierstrassZeta(w, E1, E2, E3), PRECISION - 2);
        assertClose("sigma(1/2 - 3/4 i)", new Apcomplex(new Apfloat("0.4924332649519382313569495213430024998102"), new Apfloat("-0.77767211862318560054321339810153323922")),
                    ApcomplexWeierstrassMath.weierstrassSigma(w, E1, E2, E3), PRECISION - 2);
    }

    @Test
    public void testZetaAndSigmaFarFromTheOrigin()
    {
        // the argument is moved by periods, and the quasi-periodicity gives the value
        Apfloat x = q(73, 10);
        assertClose("zeta(7.3)", new Apfloat("2.824009177563463611244591692786921390119"), ApcomplexWeierstrassMath.weierstrassZeta(x, E1, E2, E3), PRECISION - 2);
        assertClose("sigma(7.3)", new Apfloat("-7785321476.895285747827068071641734971889"), ApcomplexWeierstrassMath.weierstrassSigma(x, E1, E2, E3), PRECISION - 2);
        Apcomplex w = new Apcomplex(q(-25, 2), q(9, 1));
        assertClose("zeta(-12.5 + 9 i)", new Apcomplex(new Apfloat("-10.81732065535201714487241175206518412862"), new Apfloat("-5.993307620335563175213610787348039211003")),
                    ApcomplexWeierstrassMath.weierstrassZeta(w, E1, E2, E3), PRECISION - 2);
        assertClose("sigma(-12.5 + 9 i)", new Apcomplex(new Apfloat("3.215013304201996559913927850577930484154e40"), new Apfloat("-3.217331682312502317496610928033806205309e41")),
                    ApcomplexWeierstrassMath.weierstrassSigma(w, E1, E2, E3), PRECISION - 2);
    }

    @Test
    public void testZetaAndSigmaAtTheOrigin()
    {
        // next to the pole of zeta and the zero of sigma
        Apfloat x = q(1, 1000);
        assertClose("zeta(0.001)", new Apfloat("999.9999999997999999714285542857116883105"), ApcomplexWeierstrassMath.weierstrassZeta(x, E1, E2, E3), PRECISION - 2);
        assertClose("sigma(0.001)", new Apfloat("0.0009999999999999499999952380943452380735931"), ApcomplexWeierstrassMath.weierstrassSigma(x, E1, E2, E3), PRECISION - 2);
        assertTrue(ApcomplexWeierstrassMath.weierstrassSigma(Apcomplex.ZERO, E1, E2, E3).isZero());
        assertThrows(ArithmeticException.class, () -> ApcomplexWeierstrassMath.weierstrassZeta(Apcomplex.ZERO, E1, E2, E3));
        assertThrows(ArithmeticException.class, () -> ApcomplexWeierstrassMath.weierstrassZeta(q(1, 3), E1, E2, E1));
    }
}
