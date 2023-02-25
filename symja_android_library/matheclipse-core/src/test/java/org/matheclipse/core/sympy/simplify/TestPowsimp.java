package org.matheclipse.core.sympy.simplify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;

public class TestPowsimp {

  @BeforeClass
  public static void setupBeforeClass() {
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
  }

  @Test
  public void test_powsimp() {
    // x, y, z, n = symbols('x,y,z,n')
    // f = Function('f')
    ISymbol n = F.n;
    ISymbol x = F.x;
    ISymbol y = F.y;
    ISymbol z = F.z;
    ISymbol f = F.f;
    // assert powsimp( 4**x * 2**(-x) * 2**(-x) ) == 1
    assertEquals(
        Powsimp
            .powsimp(
                F.Times(F.Power(F.C4, x), F.Power(F.C2, F.Negate(x)), F.Power(F.C2, F.Negate(x))))
            .toString(), //
        "1");
    // assert powsimp(f(4**x * 2**(-x) * 2**(-x)) ) == f(4**x * 2**(-x) * 2**(-x))
    assertEquals(
        Powsimp
            .powsimp(F.unaryAST1(f,
                F.Times(F.Power(F.C4, x), F.Power(F.C2, F.Negate(x)), F.Power(F.C2, F.Negate(x)))))
            .toString(), //
        "f(4^x/(2^x*2^x))");

    // assert powsimp( (-4)**x * (-2)**(-x) * 2**(-x) ) == 1
    assertEquals(
        Powsimp
            .powsimp(
                F.Times(F.Power(F.CN4, x), F.Power(F.CN2, F.Negate(x)), F.Power(F.C2, F.Negate(x))))
            .toString(), //
        "1");

    // assert powsimp( f(4**x * 2**(-x) * 2**(-x)), deep=True ) == f(1)
    assertEquals(
        Powsimp.powsimp(
            F.unaryAST1(f,
                F.Times(F.Power(F.C4, x), F.Power(F.C2, F.Negate(x)), F.Power(F.C2, F.Negate(x)))),
            true, "all").toString(), //
        "f(1)");
    // assert exp(x)*exp(y) == exp(x)*exp(y)
    assertEquals(F.Times(F.Power(S.E, x), F.Power(S.E, y)).toString(), //
        "E^x*E^y");
    // assert powsimp(exp(x)*exp(y)) == exp(x + y)
    assertEquals(Powsimp.powsimp(F.Times(F.Power(S.E, x), F.Power(S.E, y))).toString(), //
        "E^(x+y)");

    // assert powsimp(exp(x)*exp(y)*2**x*2**y) == (2*E)**(x + y)
    F.eval(F.Times(F.Power(S.E, x), F.Power(S.E, y), F.Power(F.C2, x), F.Power(F.C2, y)));
    
    assertEquals(
        Powsimp
            .powsimp(
                S.Times.of(F.Power(S.E, x), F.Power(S.E, y), F.Power(F.C2, x), F.Power(F.C2, y)))
            .toString(), //
        "(2*E)^(x+y)");
    // assert powsimp(exp(x)*exp(y)*2**x*2**y, combine='exp') == \
    // exp(x + y)*2**(x + y)
    // assert powsimp(exp(x)*exp(y)*exp(2)*sin(x) + sin(y) + 2**x*2**y) == \
    // exp(2 + x + y)*sin(x) + sin(y) + 2**(x + y)
    // assert powsimp(sin(exp(x)*exp(y))) == sin(exp(x)*exp(y))
    // assert powsimp(sin(exp(x)*exp(y)), deep=True) == sin(exp(x + y))
    // assert powsimp(x**2*x**y) == x**(2 + y)
    // # This should remain factored, because 'exp' with deep=True is supposed
    // # to act like old automatic exponent combining.
    // assert powsimp((1 + E*exp(E))*exp(-E), combine='exp', deep=True) == \
    // (1 + exp(1 + E))*exp(-E)
    // assert powsimp((1 + E*exp(E))*exp(-E), deep=True) == \
    // (1 + exp(1 + E))*exp(-E)
    // assert powsimp((1 + E*exp(E))*exp(-E)) == (1 + exp(1 + E))*exp(-E)
    // assert powsimp((1 + E*exp(E))*exp(-E), combine='exp') == \
    // (1 + exp(1 + E))*exp(-E)
    // assert powsimp((1 + E*exp(E))*exp(-E), combine='base') == \
    // (1 + E*exp(E))*exp(-E)
    // x, y = symbols('x,y', nonnegative=True)
    // n = Symbol('n', real=True)
    // assert powsimp(y**n * (y/x)**(-n)) == x**n
    // assert powsimp(x**(x**(x*y)*y**(x*y))*y**(x**(x*y)*y**(x*y)), deep=True) \
    // == (x*y)**(x*y)**(x*y)
    // assert powsimp(2**(2**(2*x)*x), deep=False) == 2**(2**(2*x)*x)
    // assert powsimp(2**(2**(2*x)*x), deep=True) == 2**(x*4**x)
    // assert powsimp(
    // exp(-x + exp(-x)*exp(-x*log(x))), deep=False, combine='exp') == \
    // exp(-x + exp(-x)*exp(-x*log(x)))
    // assert powsimp(
    // exp(-x + exp(-x)*exp(-x*log(x))), deep=False, combine='exp') == \
    // exp(-x + exp(-x)*exp(-x*log(x)))
    // assert powsimp((x + y)/(3*z), deep=False, combine='exp') == (x + y)/(3*z)
    // assert powsimp((x/3 + y/3)/z, deep=True, combine='exp') == (x/3 + y/3)/z
    // assert powsimp(exp(x)/(1 + exp(x)*exp(y)), deep=True) == \
    // exp(x)/(1 + exp(x + y))
    // assert powsimp(x*y**(z**x*z**y), deep=True) == x*y**(z**(x + y))
    // assert powsimp((z**x*z**y)**x, deep=True) == (z**(x + y))**x
    // assert powsimp(x*(z**x*z**y)**x, deep=True) == x*(z**(x + y))**x
    // p = symbols('p', positive=True)
    // assert powsimp((1/x)**log(2)/x) == (1/x)**(1 + log(2))
    // assert powsimp((1/p)**log(2)/p) == p**(-1 - log(2))
    //
    // # coefficient of exponent can only be simplified for positive bases
    // assert powsimp(2**(2*x)) == 4**x
    // assert powsimp((-1)**(2*x)) == (-1)**(2*x)
    // i = symbols('i', integer=True)
    // assert powsimp((-1)**(2*i)) == 1
    // assert powsimp((-1)**(-x)) != (-1)**x # could be 1/((-1)**x), but is not
    // # force=True overrides assumptions
    // assert powsimp((-1)**(2*x), force=True) == 1
    //
    // # rational exponents allow combining of negative terms
    // w, n, m = symbols('w n m', negative=True)
    // e = i/a # not a rational exponent if `a` is unknown
    // ex = w**e*n**e*m**e
    // assert powsimp(ex) == m**(i/a)*n**(i/a)*w**(i/a)
    // e = i/3
    // ex = w**e*n**e*m**e
    // assert powsimp(ex) == (-1)**i*(-m*n*w)**(i/3)
    // e = (3 + i)/i
    // ex = w**e*n**e*m**e
    // assert powsimp(ex) == (-1)**(3*e)*(-m*n*w)**e
    //
    // eq = x**(a*Rational(2, 3))
    // # eq != (x**a)**(2/3) (try x = -1 and a = 3 to see)
    // assert powsimp(eq).exp == eq.exp == a*Rational(2, 3)
    // # powdenest goes the other direction
    // assert powsimp(2**(2*x)) == 4**x
    //
    // assert powsimp(exp(p/2)) == exp(p/2)
    //
    // # issue 6368
    // eq = Mul(*[sqrt(Dummy(imaginary=True)) for i in range(3)])
    // assert powsimp(eq) == eq and eq.is_Mul
    //
    // assert all(powsimp(e) == e for e in (sqrt(x**a), sqrt(x**2)))
    //
    // # issue 8836
    // assert str( powsimp(exp(I*pi/3)*root(-1,3)) ) == '(-1)**(2/3)'
    //
    // # issue 9183
    // assert powsimp(-0.1**x) == -0.1**x
    //
    // # issue 10095
    // assert powsimp((1/(2*E))**oo) == (exp(-1)/2)**oo
    //
    // # PR 13131
    // eq = sin(2*x)**2*sin(2.0*x)**2
    // assert powsimp(eq) == eq
    //
    // # issue 14615
    // assert powsimp(x**2*y**3*(x*y**2)**Rational(3, 2)
    // ) == x*y*(x*y**2)**Rational(5, 2)
  }

}
