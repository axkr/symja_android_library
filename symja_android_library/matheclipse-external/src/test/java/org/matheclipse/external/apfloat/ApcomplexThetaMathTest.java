package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexThetaMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexThetaMathTest
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
    public void testEllipticTheta()
    {
        // the alternating series cancels for a nome next to 1
        assertClose("theta4(0, 9/10)", new Apfloat("0.0000000007373526938473068047969135475469319468124"),
                    ApcomplexThetaMath.ellipticTheta(4, Apcomplex.ZERO, q(9, 10)), PRECISION - 2);
        assertClose("theta3(1/3, 99/100)", new Apfloat("0.0002793561852476686905651185654569480429789"),
                    ApcomplexThetaMath.ellipticTheta(3, q(1, 3), q(99, 100)), PRECISION - 2);
        assertClose("theta1(1/2+i, 1/4+i/3)", new Apcomplex(new Apfloat("2.44923162174696548747422228797909146587"), new Apfloat("-1.070272652775854642179840042127857021676")),
                    ApcomplexThetaMath.ellipticTheta(1, new Apcomplex(q(1, 2), q(1, 1)), new Apcomplex(q(1, 4), q(1, 3))), PRECISION - 2);
        assertTrue(ApcomplexThetaMath.ellipticTheta(1, Apcomplex.ZERO, q(1, 3)).isZero());
        assertThrows(ArithmeticException.class, () -> ApcomplexThetaMath.ellipticTheta(3, q(1, 3), q(3, 2)));
        assertThrows(IllegalArgumentException.class, () -> ApcomplexThetaMath.ellipticTheta(5, q(1, 3), q(1, 2)));
    }

    @Test
    public void testJacobi()
    {
        Apcomplex[] functions = ApcomplexThetaMath.jacobi(q(2, 1), q(9, 10));
        assertClose("sn(2 | 9/10)", new Apfloat("0.9816158695184937992547326258910294172047"), functions[0], PRECISION - 2);
        // a parameter above 1
        functions = ApcomplexThetaMath.jacobi(q(2, 1), q(3, 1));
        assertClose("cn(2 | 3)", new Apfloat("0.9999976785143824864541558849729383510131"), functions[1], PRECISION - 2);
        functions = ApcomplexThetaMath.jacobi(new Apcomplex(q(1, 2), q(1, 1)), new Apcomplex(q(1, 4), q(1, 3)));
        assertClose("dn(1/2+i | 1/4+i/3)", new Apcomplex(new Apfloat("1.336887441894234154669824476727513789413"), new Apfloat("-0.1346510506036005092213235707540123745258")),
                    functions[2], PRECISION - 2);
    }

    @Test
    public void testInverseJacobi()
    {
        Apfloat third = q(1, 3);
        assertClose("arcsn(1/3, 9/10)", new Apfloat("0.34586687152379939419467492265030821786142063"),
                    ApcomplexThetaMath.inverseJacobi('s', 'n', third, q(9, 10)), PRECISION - 2);
        // the functions without the letter s are continued to negative values with the period
        assertClose("arccn(-1/2, 1/3)", new Apfloat("2.36206180970438163423301112904355276178525228"),
                    ApcomplexThetaMath.inverseJacobi('c', 'n', q(-1, 2), third), PRECISION - 2);
        assertClose("arcdn(97/100, 1/3)", new Apfloat("0.439139078841977456184593033345921310932560083"),
                    ApcomplexThetaMath.inverseJacobi('d', 'n', q(97, 100), third), PRECISION - 2);
        assertClose("arcdc(3, 1/3)", new Apfloat("1.39191281182950228476880955195848407119726125"),
                    ApcomplexThetaMath.inverseJacobi('d', 'c', q(3, 1), third), PRECISION - 2);
        assertClose("arcns(3, 1/3)", new Apfloat("0.342004073428432740316112773710196306423534432"),
                    ApcomplexThetaMath.inverseJacobi('n', 's', q(3, 1), third), PRECISION - 2);
        // the inverse of the function
        Apcomplex u = new Apcomplex(q(1, 2), q(1, 3)),
                  m = new Apcomplex(q(1, 4), q(1, 3));
        assertClose("sc(arcsc)", u, ApcomplexThetaMath.inverseJacobi('s', 'c', ratio(ApcomplexThetaMath.jacobi(u, m), 0, 1), m), PRECISION - 4);
        assertTrue(ApcomplexThetaMath.inverseJacobi('c', 'n', q(1, 1), third).isZero());
        assertThrows(IllegalArgumentException.class, () -> ApcomplexThetaMath.inverseJacobi('s', 's', third, third));
    }

    @Test
    public void testInverseJacobiOfReciprocalFunctions()
    {
        Apfloat third = q(1, 3);
        // the inverse of the function with the letters exchanged, at 1/x
        assertClose("arccs(2, 1/3)", new Apfloat("0.469119487327158746673308497325119188691481568"),
                    ApcomplexThetaMath.inverseJacobi('c', 's', q(2, 1), third), PRECISION - 2);
        assertClose("arcds(2, 1/3)", new Apfloat("0.507985306007726029062028998933105706379312031"),
                    ApcomplexThetaMath.inverseJacobi('d', 's', q(2, 1), third), PRECISION - 2);
        // odd, as the functions with the letter s are
        assertClose("arccs(-2, 1/3)", new Apfloat("-0.469119487327158746673308497325119188691481568"),
                    ApcomplexThetaMath.inverseJacobi('c', 's', q(-2, 1), third), PRECISION - 2);
        Apcomplex x = new Apcomplex(q(-6, 5), q(7, 10)),
                  m = new Apcomplex(q(3, 10), q(2, 5));
        for (String function : new String[] { "ns", "nc", "nd", "cs", "ds", "dc" })
        {
            Apcomplex u = ApcomplexThetaMath.inverseJacobi(function.charAt(0), function.charAt(1), x, m);
            assertClose(function + "(arc" + function + ")", x, ratio(ApcomplexThetaMath.jacobi(u, m), index(function.charAt(0)), index(function.charAt(1))), PRECISION - 4);
        }
        assertThrows(ArithmeticException.class, () -> ApcomplexThetaMath.inverseJacobi('n', 's', Apcomplex.ZERO, third));
    }

    // sn, cn, dn and 1
    private static Apcomplex ratio(Apcomplex[] functions, int numerator, int denominator)
    {
        Apcomplex one = new Apfloat(1, PRECISION + 10);
        return (numerator < 3 ? functions[numerator] : one).divide(denominator < 3 ? functions[denominator] : one);
    }

    private static int index(char letter)
    {
        return "scdn".indexOf(letter);
    }

    @Test
    public void testInverseJacobiInTheLeftHalfPlane()
    {
        Apfloat m = q(3, 10);
        // dn is negative on a line which is 2 i K' away from the real axis
        assertClose("arcdn(-9/10, 3/10)", new Apcomplex(new Apfloat("0.956625600683233198301078937888821090787534276"), new Apfloat("4.15072627058493828770688111176483161147531136")),
                    ApcomplexThetaMath.inverseJacobi('d', 'n', q(-9, 10), m), PRECISION - 2);
        Apcomplex[] arguments = { new Apcomplex(q(-6, 5), q(7, 10)), new Apcomplex(q(-6, 5), q(-7, 10)), new Apcomplex(q(-2, 5)), new Apcomplex(q(-5, 2)),
                                  new Apcomplex(Apfloat.ZERO, q(-2, 1)), new Apcomplex(Apfloat.ZERO, q(-1, 2)) };
        for (Apcomplex x : arguments)
        {
            for (String function : new String[] { "cn", "cd", "dn" })
            {
                Apcomplex u = ApcomplexThetaMath.inverseJacobi(function.charAt(0), function.charAt(1), x, m);
                assertClose(function + "(arc" + function + "(" + x + "))", x, ratio(ApcomplexThetaMath.jacobi(u, m), index(function.charAt(0)), index(function.charAt(1))), PRECISION - 4);
            }
        }
        // on the lower imaginary axis the mirror image of the value on the upper one
        Apcomplex upper = ApcomplexThetaMath.inverseJacobi('c', 'n', new Apcomplex(Apfloat.ZERO, q(2, 1)), m),
                  lower = ApcomplexThetaMath.inverseJacobi('c', 'n', new Apcomplex(Apfloat.ZERO, q(-2, 1)), m);
        assertClose("arccn(-2i)", upper.conj(), lower, PRECISION - 2);
    }

    @Test
    public void testInverseJacobiOnBranchCuts()
    {
        // above 1 the limit from above: for a parameter next to 0 the mirror image of the inverse sine and cosine
        Apfloat small = new Apfloat("1e-40", PRECISION),
                x = q(5, 2);
        assertClose("arcsn(5/2, 0)", new Apcomplex(new Apfloat("1.5707963267948966192313216916397514420985847"), new Apfloat("1.56679923697241107866405686258048349386208235")),
                    ApcomplexThetaMath.inverseJacobi('s', 'n', x, small), PRECISION - 4);
        assertClose("arcsn(-5/2, 0)", new Apcomplex(new Apfloat("-1.5707963267948966192313216916397514420985847"), new Apfloat("-1.56679923697241107866405686258048349386208235")),
                    ApcomplexThetaMath.inverseJacobi('s', 'n', x.negate(), small), PRECISION - 4);
        assertClose("arccn(5/2, 0)", new Apcomplex(Apfloat.ZERO, new Apfloat("-1.56679923697241107866405686258048349386208235")),
                    ApcomplexThetaMath.inverseJacobi('c', 'n', x, small), PRECISION - 4);
        // Mathematica: InverseJacobiSN[2.5, 0.3] = 0.8447480298725263 + 2.0753631352924686 I
        assertClose("arcsn(5/2, 3/10)", new Apcomplex(new Apfloat("0.8447480298725263"), new Apfloat("2.0753631352924686")),
                    ApcomplexThetaMath.inverseJacobi('s', 'n', x, q(3, 10)), 14);
    }

    @Test
    public void testJacobiAmplitude()
    {
        // 40 is beyond six quarter periods
        assertClose("am(40, 9/10)", new Apfloat("24.0967706752357467381611460164339393760210725"),
                    ApcomplexThetaMath.jacobiAmplitude(q(40, 1), q(9, 10)), PRECISION - 2);
        assertClose("am(-40, 9/10)", new Apfloat("-24.0967706752357467381611460164339393760210725"),
                    ApcomplexThetaMath.jacobiAmplitude(q(-40, 1), q(9, 10)), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexThetaMath.jacobiAmplitude(q(1, 3), q(3, 2)));
    }

    @Test
    public void testQPochhammer()
    {
        assertClose("(1/3; -7/10)", new Apfloat("0.726644937151787578197510179986395160117997604"),
                    ApcomplexThetaMath.qPochhammer(q(1, 3), q(-7, 10)), PRECISION - 2);
        assertClose("(5/2; 1/4+i/3)", new Apcomplex(new Apfloat("-0.175111929248196943555815195513378333534099071"), new Apfloat("2.01116459517160317036600272186261120244707307")),
                    ApcomplexThetaMath.qPochhammer(q(5, 2), new Apcomplex(q(1, 4), q(1, 3))), PRECISION - 2);
        // a factor 1 - a q^k which is zero
        assertTrue(ApcomplexThetaMath.qPochhammer(q(4, 1), q(1, 2)).isZero());
        assertThrows(ArithmeticException.class, () -> ApcomplexThetaMath.qPochhammer(q(1, 3), q(3, 2)));
    }

    @Test
    public void testModularFunctions()
    {
        Apcomplex tau = new Apcomplex(q(1, 3), q(1, 1));
        assertClose("lambda(1/3+i)", new Apcomplex(new Apfloat("0.414405480384997491318429003209241320614919794"), new Apfloat("0.399584938884196014642324960988448530010006652")),
                    ApcomplexThetaMath.modularLambda(tau), PRECISION - 2);
        assertClose("eta(1/3+i)", new Apcomplex(new Apfloat("0.767562142473995134656213562591362022071039979"), new Apfloat("0.0659058210877420180182611147164826658853319479")),
                    ApcomplexThetaMath.dedekindEta(tau), PRECISION - 2);
        assertClose("eta(i)", new Apfloat("0.768225422326056659002594179576180644517866914"),
                    ApcomplexThetaMath.dedekindEta(new Apcomplex(Apfloat.ZERO, q(1, 1))), PRECISION - 2);
        assertClose("q(1/3)", new Apfloat("0.0253199133662851868666269711106802025040489877"), ApcomplexThetaMath.ellipticNomeQ(q(1, 3)), PRECISION - 2);
        assertClose("q(-2)", new Apfloat("-0.0682378277453383330883815168277460604619053795"), ApcomplexThetaMath.ellipticNomeQ(q(-2, 1)), PRECISION - 2);
        assertThrows(ArithmeticException.class, () -> ApcomplexThetaMath.modularLambda(q(1, 3)));
        assertThrows(ArithmeticException.class, () -> ApcomplexThetaMath.dedekindEta(new Apcomplex(q(1, 3), q(-1, 1))));
    }
}
