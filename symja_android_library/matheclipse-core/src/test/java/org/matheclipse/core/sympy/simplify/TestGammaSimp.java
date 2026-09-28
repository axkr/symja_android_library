package org.matheclipse.core.sympy.simplify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.hipparchus.complex.Complex;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Port of <a href="https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_gammasimp.py">test_gammasimp.py</a>
 * and <a href="https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_combsimp.py">test_combsimp.py</a>.
 *
 * <p>
 * Symja returns other forms than sympy (for example <code>Gamma</code> instead of
 * <code>binomial</code>), therefore every case checks that the result contains at most as many
 * gamma and combinatorial functions as sympy's result and has the same numeric value as the input
 * and as sympy's result. The symbols <code>n, k, m, i, p</code> are integers in the sympy tests and
 * are replaced by integers; <code>x, y</code> are replaced by other numbers.
 */
public class TestGammaSimp extends ExprEvaluatorTestCase {

  /** Two sets of values for the variables <code>x, y, n, k, m, i, p, a, b</code> */
  private static final String[] POINTS = {//
      "{x->0.7123, y->1.3217, n->7, k->3, m->4, i->2, p->1, a->0.4142, b->3.1729}", //
      "{x->2.2468, y->0.5831, n->9, k->4, m->6, i->3, p->2, a->1.7321, b->0.6180}"};

  private IExpr eval(String str) {
    return evaluator.getEvalEngine().evaluate(str);
  }

  private static int count(IExpr expr) {
    if (!expr.isAST()) {
      return 0;
    }
    IAST ast = (IAST) expr;
    String head = ast.head().toString();
    int result = (head.equals("Gamma") || head.equals("Factorial") || head.equals("Binomial")
        || head.equals("Pochhammer") || head.equals("FactorialPower")) ? 1 : 0;
    for (int i = 1; i < ast.size(); i++) {
      result += count(ast.get(i));
    }
    return result;
  }

  private Complex numeric(IExpr expr, String point) {
    EvalEngine engine = evaluator.getEvalEngine();
    return engine.evalN(F.ReplaceAll(expr, engine.parse(point))).evalfc();
  }

  private void assertSameValue(IExpr expected, IExpr actual, String message, String... points) {
    for (String point : points.length > 0 ? points : POINTS) {
      Complex c1 = numeric(expected, point);
      Complex c2 = numeric(actual, point);
      assertTrue(c1.subtract(c2).norm() <= 1e-9 * (1.0 + c1.norm()),
          message + " at " + point + ": " + c1 + " " + c2);
    }
  }

  /** <code>assert gammasimp(input) == expected</code> */
  private void checkGammaSimp(String input, String expected, String... points) {
    check(input, expected, false, points);
  }

  /** <code>assert combsimp(input) == expected</code> */
  private void checkCombSimp(String input, String expected, String... points) {
    check(input, expected, true, points);
  }

  private void check(String input, String expected, boolean comb, String... points) {
    IExpr expr = eval(input);
    IExpr expectedExpr = eval(expected);
    IExpr simplified = comb ? GammaSimp.combsimp(expr) : GammaSimp.gammasimp(expr);
    String message = input + " was simplified to " + simplified + ", expected " + expected;
    assertTrue(count(simplified) <= count(expectedExpr), message);
    assertSameValue(expr, simplified, message, points);
    assertSameValue(expectedExpr, simplified, message, points);
  }

  @Test
  public void testGammaSimp() {
    // https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_gammasimp.py
    // assert gammasimp(gamma(x)) == gamma(x)
    assertEquals("Gamma(x)", GammaSimp.gammasimp(eval("Gamma(x)")).toString());
    // assert gammasimp(gamma(x + 1)/x) == gamma(x)
    checkGammaSimp("Gamma(x + 1)/x", "Gamma(x)");
    // assert gammasimp(gamma(x)/(x - 1)) == gamma(x - 1)
    checkGammaSimp("Gamma(x)/(x - 1)", "Gamma(x - 1)");
    // assert gammasimp(x*gamma(x)) == gamma(x + 1)
    checkGammaSimp("x*Gamma(x)", "Gamma(x + 1)");
    // assert gammasimp((x + 1)*gamma(x + 1)) == gamma(x + 2)
    checkGammaSimp("(x + 1)*Gamma(x + 1)", "Gamma(x + 2)");
    // assert gammasimp(gamma(x + y)*(x + y)) == gamma(x + y + 1)
    checkGammaSimp("Gamma(x + y)*(x + y)", "Gamma(x + y + 1)");
    // assert gammasimp(x/gamma(x + 1)) == 1/gamma(x)
    checkGammaSimp("x/Gamma(x + 1)", "1/Gamma(x)");
    // assert gammasimp((x + 1)**2/gamma(x + 2)) == (x + 1)/gamma(x + 1)
    checkGammaSimp("(x + 1)^2/Gamma(x + 2)", "(x + 1)/Gamma(x + 1)");
    // assert gammasimp(x*gamma(x) + gamma(x + 3)/(x + 2)) == (x + 2)*gamma(x + 1)
    checkGammaSimp("x*Gamma(x) + Gamma(x + 3)/(x + 2)", "(x + 2)*Gamma(x + 1)");

    // assert gammasimp(gamma(2*x)*x) == gamma(2*x + 1)/2
    checkGammaSimp("Gamma(2*x)*x", "Gamma(2*x + 1)/2");
    // assert gammasimp(gamma(2*x)/(x - S.Half)) == 2*gamma(2*x - 1)
    checkGammaSimp("Gamma(2*x)/(x - 1/2)", "2*Gamma(2*x - 1)");

    // assert gammasimp(gamma(x)*gamma(1 - x)) == pi/sin(pi*x)
    checkGammaSimp("Gamma(x)*Gamma(1 - x)", "Pi/Sin(Pi*x)");
    // assert gammasimp(gamma(x)*gamma(-x)) == -pi/(x*sin(pi*x))
    checkGammaSimp("Gamma(x)*Gamma(-x)", "-Pi/(x*Sin(Pi*x))");
    // assert gammasimp(1/gamma(x + 3)/gamma(1 - x)) == sin(pi*x)/(pi*x*(x + 1)*(x + 2))
    checkGammaSimp("1/Gamma(x + 3)/Gamma(1 - x)", "Sin(Pi*x)/(Pi*x*(x + 1)*(x + 2))");

    // assert gammasimp(factorial(n + 2)) == gamma(n + 3)
    checkGammaSimp("(n + 2)!", "Gamma(n + 3)");
    // assert gammasimp(binomial(n, k)) == gamma(n + 1)/(gamma(k + 1)*gamma(-k + n + 1))
    checkGammaSimp("Binomial(n, k)", "Gamma(n + 1)/(Gamma(k + 1)*Gamma(-k + n + 1))");

    // assert powsimp(gammasimp(gamma(x)*gamma(x + S.Half)*gamma(y)/gamma(x + y))) ==
    // 2**(-2*x + 1)*sqrt(pi)*gamma(2*x)*gamma(y)/gamma(x + y)
    checkGammaSimp("Gamma(x)*Gamma(x + 1/2)*Gamma(y)/Gamma(x + y)",
        "2^(-2*x + 1)*Sqrt(Pi)*Gamma(2*x)*Gamma(y)/Gamma(x + y)");
    // assert gammasimp(1/gamma(x)/gamma(x - Rational(1, 3))/gamma(x + Rational(1, 3))) ==
    // 3**(3*x - Rational(3, 2))/(2*pi*gamma(3*x - 1))
    checkGammaSimp("1/Gamma(x)/Gamma(x - 1/3)/Gamma(x + 1/3)",
        "3^(3*x - 3/2)/(2*Pi*Gamma(3*x - 1))");
    // assert simplify(gamma(S.Half + x/2)*gamma(1 + x/2)/gamma(1 + x)/sqrt(pi)*2**x) == 1
    checkGammaSimp("Gamma(1/2 + x/2)*Gamma(1 + x/2)/Gamma(1 + x)/Sqrt(Pi)*2^x", "1");
    // assert gammasimp(gamma(Rational(-1, 4))*gamma(Rational(-3, 4))) == 16*sqrt(2)*pi/3
    checkGammaSimp("Gamma(-1/4)*Gamma(-3/4)", "16*Sqrt(2)*Pi/3");

    // assert powsimp(gammasimp(gamma(2*x)/gamma(x))) == 2**(2*x - 1)*gamma(x + S.Half)/sqrt(pi)
    checkGammaSimp("Gamma(2*x)/Gamma(x)", "2^(2*x - 1)*Gamma(x + 1/2)/Sqrt(Pi)");
  }

  @Test
  public void testIssue6792() {
    // e = (-gamma(k)*gamma(k + 2) + gamma(k + 1)**2)/gamma(k)**2
    // assert gammasimp(e) == -k
    String e = "(-Gamma(k)*Gamma(k + 2) + Gamma(k + 1)^2)/Gamma(k)^2";
    checkGammaSimp(e, "-k");
    // assert gammasimp(1/e) == -1/k
    checkGammaSimp("1/(" + e + ")", "-1/k");
    // e = (gamma(x) + gamma(x + 1))/gamma(x)
    // assert gammasimp(e) == x + 1
    checkGammaSimp("(Gamma(x) + Gamma(x + 1))/Gamma(x)", "x + 1");
    // assert gammasimp(1/e) == 1/(x + 1)
    checkGammaSimp("Gamma(x)/(Gamma(x) + Gamma(x + 1))", "1/(x + 1)");
    // e = (gamma(x) + gamma(x + 2))*(gamma(x - 1) + gamma(x))/gamma(x)
    // assert gammasimp(e) == (x**2 + x + 1)*gamma(x + 1)/(x - 1)
    checkGammaSimp("(Gamma(x) + Gamma(x + 2))*(Gamma(x - 1) + Gamma(x))/Gamma(x)",
        "(x^2 + x + 1)*Gamma(x + 1)/(x - 1)");
    // assert gammasimp(e**2) == k**2
    checkGammaSimp("(" + e + ")^2", "k^2");
    // assert gammasimp(e**2/gamma(k + 1)) == k/gamma(k)
    checkGammaSimp("(" + e + ")^2/Gamma(k + 1)", "k/Gamma(k)");
    // a = R(1, 2) + R(1, 3)
    // b = a + R(1, 3)
    // assert gammasimp(gamma(2*k)/gamma(k)*gamma(k + a)*gamma(k + b)) ==
    // 3*2**(2*k + 1)*3**(-3*k - 2)*sqrt(pi)*gamma(3*k + R(3, 2))/2
    checkGammaSimp("Gamma(2*k)/Gamma(k)*Gamma(k + 5/6)*Gamma(k + 7/6)",
        "3*2^(2*k + 1)*3^(-3*k - 2)*Sqrt(Pi)*Gamma(3*k + 3/2)/2");
  }

  @Test
  public void testIssue9699() {
    // assert gammasimp((x + 1)*factorial(x)/gamma(y)) == gamma(x + 2)/gamma(y)
    checkGammaSimp("(x + 1)*x!/Gamma(y)", "Gamma(x + 2)/Gamma(y)");
    // assert gammasimp(rf(x + n, k)*binomial(n, k)).simplify() == Piecewise(
    // (gamma(n + 1)*gamma(k + n + x)/(gamma(k + 1)*gamma(n + x)*gamma(-k + n + 1)), n > -x),
    // ((-1)**k*gamma(n + 1)*gamma(-n - x + 1)/(gamma(k + 1)*gamma(-k + n + 1)*gamma(-k - n - x +
    // 1)), True))
    // the values of the test points satisfy n > -x
    checkGammaSimp("Pochhammer(x + n, k)*Binomial(n, k)",
        "Gamma(n + 1)*Gamma(k + n + x)/(Gamma(k + 1)*Gamma(n + x)*Gamma(-k + n + 1))");
    // A, B = symbols('A B', commutative=False)
    // assert gammasimp(e*B*A) == gammasimp(e)*B*A
    // isn't ported: Symja has no non-commutative symbols
  }

  @Test
  public void testIteration() {
    // assert gammasimp(gamma(2*k)/gamma(k)*gamma(-k - R(1, 2))) == (
    // -2**(2*k + 1)*sqrt(pi)/(2*((2*k + 1)*cos(pi*k))))
    checkGammaSimp("Gamma(2*k)/Gamma(k)*Gamma(-k - 1/2)",
        "-2^(2*k + 1)*Sqrt(Pi)/(2*((2*k + 1)*Cos(Pi*k)))");
    // assert gammasimp(gamma(k)*gamma(k + R(1, 3))*gamma(k + R(2, 3))/gamma(k*R(3, 2))) == (
    // 3*2**(3*k + 1)*3**(-3*k - S.Half)*sqrt(pi)*gamma(k*R(3, 2) + S.Half)/2)
    checkGammaSimp("Gamma(k)*Gamma(k + 1/3)*Gamma(k + 2/3)/Gamma(k*3/2)",
        "3*2^(3*k + 1)*3^(-3*k - 1/2)*Sqrt(Pi)*Gamma(k*3/2 + 1/2)/2");
    // # issue 6153
    // assert gammasimp(gamma(Rational(1, 4))/gamma(Rational(5, 4))) == 4
    checkGammaSimp("Gamma(1/4)/Gamma(5/4)", "4");
  }

  @Test
  public void testBinomialGamma() {
    // assert gammasimp(binomial(n + 2, k + S.Half)) == gamma(n + 3)/
    // (gamma(k + R(3, 2))*gamma(-k + n + R(5, 2)))
    checkGammaSimp("Binomial(n + 2, k + 1/2)",
        "Gamma(n + 3)/(Gamma(k + 3/2)*Gamma(-k + n + 5/2))");
    // assert gammasimp(binomial(n + 2, k + 2.0)) ==
    // gamma(n + 3)/(gamma(k + 3.0)*gamma(-k + n + 1))
    checkGammaSimp("Binomial(n + 2, k + 2.0)", "Gamma(n + 3)/(Gamma(k + 3.0)*Gamma(-k + n + 1))");
    // # issue 11548
    // assert gammasimp(binomial(0, x)) == sin(pi*x)/(pi*x)
    checkGammaSimp("Binomial(0, x)", "Sin(Pi*x)/(Pi*x)");
    // e = gamma(n + Rational(1, 3))*gamma(n + R(2, 3))
    // assert gammasimp(e) == e
    checkGammaSimp("Gamma(n + 1/3)*Gamma(n + 2/3)", "Gamma(n + 1/3)*Gamma(n + 2/3)");
    // assert gammasimp(gamma(4*n + S.Half)/gamma(2*n - R(3, 4))) ==
    // 2**(4*n - R(5, 2))*(8*n - 3)*gamma(2*n + R(3, 4))/sqrt(pi)
    checkGammaSimp("Gamma(4*n + 1/2)/Gamma(2*n - 3/4)",
        "2^(4*n - 5/2)*(8*n - 3)*Gamma(2*n + 3/4)/Sqrt(Pi)");
    // i, m = symbols('i m', integer = True)
    // e = gamma(exp(i))
    // assert gammasimp(e) == e
    assertEquals("Gamma(E^i)", GammaSimp.gammasimp(eval("Gamma(Exp(i))")).toString());
    // e = gamma(m + 3)
    // assert gammasimp(e) == e
    assertEquals("Gamma(3+m)", GammaSimp.gammasimp(eval("Gamma(m + 3)")).toString());
    // e = gamma(m + 1)/(gamma(i + 1)*gamma(-i + m + 1))
    // assert gammasimp(e) == e
    checkGammaSimp("Gamma(m + 1)/(Gamma(i + 1)*Gamma(-i + m + 1))",
        "Gamma(m + 1)/(Gamma(i + 1)*Gamma(-i + m + 1))");
    // p = symbols("p", integer=True, positive=True)
    // assert gammasimp(gamma(-p + 4)) == gamma(-p + 4)
    assertEquals("Gamma(4-p)", GammaSimp.gammasimp(eval("Gamma(-p + 4)")).toString());
  }

  @Test
  public void testIssue22606() {
    // fx = Function('f')(x)
    // eq = x + gamma(y)
    // ans = gammasimp(eq)
    IExpr ans = GammaSimp.gammasimp(eval("x + Gamma(y)"));
    // assert gammasimp(eq.subs(x, fx)).subs(fx, x) == ans
    assertEquals(ans, F.subst(GammaSimp.gammasimp(eval("f(x) + Gamma(y)")), eval("f(x)"),
        eval("x")));
    // assert gammasimp(eq.subs(x, cos(x))).subs(cos(x), x) == ans
    assertEquals(ans, F.subst(GammaSimp.gammasimp(eval("Cos(x) + Gamma(y)")), eval("Cos(x)"),
        eval("x")));
    // assert 1/gammasimp(1/eq) == ans
    assertEquals(ans, eval("1/(" + GammaSimp.gammasimp(eval("1/(x + Gamma(y))")) + ")"));
    // assert gammasimp(fx.subs(x, eq)).args[0] == ans
    assertEquals(ans, GammaSimp.gammasimp(eval("f(x + Gamma(y))")).first());
  }

  @Test
  public void testCombSimp() {
    // https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_combsimp.py
    // k, m, n = symbols('k m n', integer = True)
    // assert combsimp(factorial(n)) == factorial(n)
    assertEquals("n!", GammaSimp.combsimp(eval("n!")).toString());
    // assert combsimp(binomial(n, k)) == binomial(n, k)
    checkCombSimp("Binomial(n, k)", "Binomial(n, k)");

    // assert combsimp(factorial(n)/factorial(n - 3)) == n*(-1 + n)*(-2 + n)
    checkCombSimp("n!/(n - 3)!", "n*(-1 + n)*(-2 + n)");
    // assert combsimp(binomial(n + 1, k + 1)/binomial(n, k)) == (1 + n)/(1 + k)
    checkCombSimp("Binomial(n + 1, k + 1)/Binomial(n, k)", "(1 + n)/(1 + k)");

    // assert combsimp(binomial(3*n + 4, n + 1)/binomial(3*n + 1, n)) ==
    // Rational(3, 2)*((3*n + 2)*(3*n + 4)/((n + 1)*(2*n + 3)))
    checkCombSimp("Binomial(3*n + 4, n + 1)/Binomial(3*n + 1, n)",
        "3/2*((3*n + 2)*(3*n + 4)/((n + 1)*(2*n + 3)))");

    // assert combsimp(factorial(n)**2/factorial(n - 3)) == factorial(n)*n*(-1 + n)*(-2 + n)
    checkCombSimp("n!^2/(n - 3)!", "n!*n*(-1 + n)*(-2 + n)");
    // assert combsimp(factorial(n)*binomial(n + 1, k + 1)/binomial(n, k)) ==
    // factorial(n + 1)/(1 + k)
    checkCombSimp("n!*Binomial(n + 1, k + 1)/Binomial(n, k)", "(n + 1)!/(1 + k)");

    // assert combsimp(gamma(n + 3)) == factorial(n + 2)
    checkCombSimp("Gamma(n + 3)", "(n + 2)!");
    // assert combsimp(factorial(x)) == gamma(x + 1)
    checkCombSimp("x!", "Gamma(x + 1)");

    // # issue 9699
    // assert combsimp((n + 1)*factorial(n)) == factorial(n + 1)
    checkCombSimp("(n + 1)*n!", "(n + 1)!");
    // assert combsimp(factorial(n)/n) == factorial(n-1)
    checkCombSimp("n!/n", "(n-1)!");

    // # issue 6658
    // assert combsimp(binomial(n, n - k)) == binomial(n, k)
    checkCombSimp("Binomial(n, n - k)", "Binomial(n, k)");

    // # issue 6341, 7135
    // assert combsimp(factorial(n)/(factorial(k)*factorial(n - k))) == binomial(n, k)
    checkCombSimp("n!/(k!*(n - k)!)", "Binomial(n, k)");
    // assert combsimp(factorial(k)*factorial(n - k)/factorial(n)) == 1/binomial(n, k)
    checkCombSimp("k!*(n - k)!/n!", "1/Binomial(n, k)");
    // assert combsimp(factorial(2*n)/factorial(n)**2) == binomial(2*n, n)
    checkCombSimp("(2*n)!/n!^2", "Binomial(2*n, n)");
    // assert combsimp(factorial(2*n)*factorial(k)*factorial(n - k)/
    // factorial(n)**3) == binomial(2*n, n)/binomial(n, k)
    checkCombSimp("(2*n)!*k!*(n - k)!/n!^3", "Binomial(2*n, n)/Binomial(n, k)");

    // assert combsimp(factorial(n*(1 + n) - n**2 - n)) == 1
    checkCombSimp("(n*(1 + n) - n^2 - n)!", "1");
  }

  @Test
  public void testCombSimpFactorialPower() {
    // assert combsimp(6*FallingFactorial(-4, n)/factorial(n)) ==
    // (-1)**n*(n + 1)*(n + 2)*(n + 3)
    checkCombSimp("6*FactorialPower(-4, n)/n!", "(-1)^n*(n + 1)*(n + 2)*(n + 3)");
    // assert combsimp(6*FallingFactorial(-4, n - 1)/factorial(n - 1)) ==
    // (-1)**(n - 1)*n*(n + 1)*(n + 2)
    checkCombSimp("6*FactorialPower(-4, n - 1)/(n - 1)!", "(-1)^(n - 1)*n*(n + 1)*(n + 2)");
    // assert combsimp(6*FallingFactorial(-4, n - 3)/factorial(n - 3)) ==
    // (-1)**(n - 3)*n*(n - 1)*(n - 2)
    checkCombSimp("6*FactorialPower(-4, n - 3)/(n - 3)!", "(-1)^(n - 3)*n*(n - 1)*(n - 2)");
    // assert combsimp(6*FallingFactorial(-4, -n - 1)/factorial(-n - 1)) ==
    // -(-1)**(-n - 1)*n*(n - 1)*(n - 2)
    checkCombSimp("6*FactorialPower(-4, -n - 1)/(-n - 1)!", "-(-1)^(-n - 1)*n*(n - 1)*(n - 2)",
        "{n->-5}", "{n->-7}");

    // assert combsimp(6*RisingFactorial(4, n)/factorial(n)) == (n + 1)*(n + 2)*(n + 3)
    checkCombSimp("6*Pochhammer(4, n)/n!", "(n + 1)*(n + 2)*(n + 3)");
    // assert combsimp(6*RisingFactorial(4, n - 1)/factorial(n - 1)) == n*(n + 1)*(n + 2)
    checkCombSimp("6*Pochhammer(4, n - 1)/(n - 1)!", "n*(n + 1)*(n + 2)");
    // assert combsimp(6*RisingFactorial(4, n - 3)/factorial(n - 3)) == n*(n - 1)*(n - 2)
    checkCombSimp("6*Pochhammer(4, n - 3)/(n - 3)!", "n*(n - 1)*(n - 2)");
    // assert combsimp(6*RisingFactorial(4, -n - 1)/factorial(-n - 1)) == -n*(n - 1)*(n - 2)
    checkCombSimp("6*Pochhammer(4, -n - 1)/(-n - 1)!", "-n*(n - 1)*(n - 2)", "{n->-5}",
        "{n->-7}");
  }

  @Test
  public void testIssue6878() {
    // assert combsimp(RisingFactorial(-10, n)) == 3628800*(-1)**n/factorial(10 - n)
    checkCombSimp("Pochhammer(-10, n)", "3628800*(-1)^n/(10 - n)!");
  }

  @Test
  public void testIssue14528() {
    // p = symbols("p", integer=True, positive=True)
    // assert combsimp(binomial(1,p)) == 1/(factorial(p)*factorial(1-p))
    checkCombSimp("Binomial(1,p)", "1/(p!*(1-p)!)", "{p->1}");
    // assert combsimp(factorial(2-p)) == factorial(2-p)
    checkCombSimp("(2-p)!", "(2-p)!");
  }

  @Test
  public void testFullSimplify() {
    check("FullSimplify(Binomial(n+1,k+1)/Binomial(n,k))", //
        "(1+n)/(1+k)");
    check("FullSimplify(Gamma(2*x)/(Gamma(x)*Gamma(x+1/2)))", //
        "1/(2^(1-2*x)*Sqrt(Pi))");
    check("FullSimplify(Gamma(x+1)/Gamma(x))", //
        "x");
    check("FullSimplify(Gamma(x)*Gamma(1-x))", //
        "Pi*Csc(Pi*x)");
    check("FullSimplify(n!/(n-2)!)", //
        "(-1+n)*n");
    check("FullSimplify(Binomial(n,k)*k!*(n-k)!)", //
        "Gamma(1+n)");
  }
}
