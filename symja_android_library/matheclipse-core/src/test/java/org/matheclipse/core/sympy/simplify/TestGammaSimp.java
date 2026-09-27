package org.matheclipse.core.sympy.simplify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.hipparchus.complex.Complex;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestGammaSimp extends ExprEvaluatorTestCase {

  private IExpr parse(String str) {
    return evaluator.getEvalEngine().parse(str);
  }

  private IExpr eval(String str) {
    return evaluator.getEvalEngine().evaluate(str);
  }

  private static int count(IExpr expr, String head) {
    if (!expr.isAST()) {
      return 0;
    }
    int result = expr.head().toString().equals(head) ? 1 : 0;
    for (int i = 1; i < expr.size(); i++) {
      result += count(((org.matheclipse.core.interfaces.IAST) expr).get(i), head);
    }
    return result;
  }

  /**
   * Check that the simplified expression contains <code>gammas</code> gamma functions and has the
   * same numeric values as the input and the expected expression.
   */
  private void checkGammaSimp(String input, String expected, int gammas) {
    IExpr expr = eval(input);
    IExpr simplified = GammaSimp.gammasimp(expr);
    String message = input + " was simplified to " + simplified;
    assertEquals(gammas, count(simplified, "Gamma"), message);
    assertEquals(0, count(simplified, "Binomial") + count(simplified, "Factorial"), message);
    IExpr expectedExpr = eval(expected);
    String[] variables = {"x", "y", "n", "k", "a", "b"};
    double[] values = {0.7123, 1.3217, 5.4321, 2.2468, 0.4142, 3.1729};
    IExpr v1 = expr;
    IExpr v2 = simplified;
    IExpr v3 = expectedExpr;
    for (int i = 0; i < variables.length; i++) {
      IExpr variable = parse(variables[i]);
      v1 = F.subst(v1, variable, F.num(values[i]));
      v2 = F.subst(v2, variable, F.num(values[i]));
      v3 = F.subst(v3, variable, F.num(values[i]));
    }
    Complex c1 = evaluator.getEvalEngine().evalN(v1).evalfc();
    Complex c2 = evaluator.getEvalEngine().evalN(v2).evalfc();
    Complex c3 = evaluator.getEvalEngine().evalN(v3).evalfc();
    assertTrue(c1.subtract(c2).norm() <= 1e-9 * (1.0 + c1.norm()), message + ": " + c1 + " " + c2);
    assertTrue(c1.subtract(c3).norm() <= 1e-9 * (1.0 + c1.norm()),
        message + " expected " + expected + ": " + c1 + " " + c3);
  }

  @Test
  public void testGammaSimp() {
    // https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_gammasimp.py
    // assert gammasimp(gamma(x + 1)/x) == gamma(x)
    checkGammaSimp("Gamma(x + 1)/x", "Gamma(x)", 1);
    // assert gammasimp(gamma(x)/(x - 1)) == gamma(x - 1)
    checkGammaSimp("Gamma(x)/(x - 1)", "Gamma(x - 1)", 1);
    // assert gammasimp(x*gamma(x)) == gamma(x + 1)
    checkGammaSimp("x*Gamma(x)", "Gamma(x + 1)", 1);
    // assert gammasimp((x + 1)*gamma(x + 1)) == gamma(x + 2)
    checkGammaSimp("(x + 1)*Gamma(x + 1)", "Gamma(x + 2)", 1);
    // assert gammasimp(gamma(x + y)*(x + y)) == gamma(x + y + 1)
    checkGammaSimp("Gamma(x + y)*(x + y)", "Gamma(x + y + 1)", 1);
    // assert gammasimp(x/gamma(x + 1)) == 1/gamma(x)
    checkGammaSimp("x/Gamma(x + 1)", "1/Gamma(x)", 1);
    // assert gammasimp((x + 1)**2/gamma(x + 2)) == (x + 1)/gamma(x + 1)
    checkGammaSimp("(x + 1)^2/Gamma(x + 2)", "(x + 1)/Gamma(x + 1)", 1);
    // assert gammasimp(x*gamma(x) + gamma(x + 3)/(x + 2)) == (x + 2)*gamma(x + 1)
    checkGammaSimp("x*Gamma(x) + Gamma(x + 3)/(x + 2)", "(x + 2)*Gamma(x + 1)", 1);

    // assert gammasimp(gamma(2*x)*x) == gamma(2*x + 1)/2
    checkGammaSimp("Gamma(2*x)*x", "Gamma(2*x + 1)/2", 1);
    // assert gammasimp(gamma(2*x)/(x - S.Half)) == 2*gamma(2*x - 1)
    checkGammaSimp("Gamma(2*x)/(x - 1/2)", "2*Gamma(2*x - 1)", 1);

    // >>> gammasimp(gamma(x)/gamma(x - 3))
    // (x - 3)*(x - 2)*(x - 1)
    checkGammaSimp("Gamma(x)/Gamma(x - 3)", "(x - 3)*(x - 2)*(x - 1)", 0);
    checkGammaSimp("Gamma(x+3)/Gamma(x+1)", "(x + 1)*(x + 2)", 0);
  }

  @Test
  public void testReflection() {
    // assert gammasimp(gamma(x)*gamma(1 - x)) == pi/sin(pi*x)
    checkGammaSimp("Gamma(x)*Gamma(1 - x)", "Pi/Sin(Pi*x)", 0);
    // assert gammasimp(1/gamma(x + 3)/gamma(1 - x)) == sin(pi*x)/(pi*x*(x + 1)*(x + 2))
    checkGammaSimp("1/Gamma(x + 3)/Gamma(1 - x)", "Sin(Pi*x)/(Pi*x*(x + 1)*(x + 2))", 0);
    // assert gammasimp(gamma(-x)*gamma(x+1)) == -pi/sin(pi*x) (from gamma(x)*gamma(-x))
    checkGammaSimp("Gamma(x)*Gamma(-x)", "-Pi/(x*Sin(Pi*x))", 0);
  }

  @Test
  public void testDuplication() {
    // assert gammasimp(gamma(2*x)/gamma(x)) == 2**(2*x - 1)*gamma(x + S.Half)/sqrt(pi)
    checkGammaSimp("Gamma(2*x)/Gamma(x)", "2^(2*x - 1)*Gamma(x + 1/2)/Sqrt(Pi)", 1);
    checkGammaSimp("Gamma(2*x)/(Gamma(x)*Gamma(x+1/2))", "2^(2*x - 1)/Sqrt(Pi)", 0);
  }

  @Test
  public void testMultiplicationTheorem() {
    // assert gammasimp(gamma(x)*gamma(x + S.Half)*gamma(y)/gamma(x + y)) ==
    // 2**(-2*x + 1)*sqrt(pi)*gamma(2*x)*gamma(y)/gamma(x + y)
    checkGammaSimp("Gamma(x)*Gamma(x + 1/2)*Gamma(y)/Gamma(x + y)",
        "2^(-2*x + 1)*Sqrt(Pi)*Gamma(2*x)*Gamma(y)/Gamma(x + y)", 3);
    // >>> gammasimp(gamma(x)*gamma(x+S(1)/3)*gamma(x+S(2)/3))
    // 6*3**(-3*x - 1/2)*pi*gamma(3*x)
    checkGammaSimp("Gamma(x)*Gamma(x + 1/3)*Gamma(x + 2/3)", "6*3^(-3*x - 1/2)*Pi*Gamma(3*x)", 1);
    // >>> gammasimp(gamma(x)*gamma(x+S(1)/3)*gamma(x+S(5)/3))
    // 2*3**(-3*x - 1/2)*pi*(3*x + 2)*gamma(3*x)
    checkGammaSimp("Gamma(x)*Gamma(x + 1/3)*Gamma(x + 5/3)",
        "2*3^(-3*x - 1/2)*Pi*(3*x + 2)*Gamma(3*x)", 1);
  }

  @Test
  public void testCombinatorial() {
    // https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_combsimp.py
    // assert combsimp(factorial(n)/factorial(n - 3)) == n*(-2 + n)*(-1 + n)
    checkGammaSimp("n!/(n - 3)!", "n*(-2 + n)*(-1 + n)", 0);
    // assert combsimp(binomial(n + 1, k + 1)/binomial(n, k)) == (1 + n)/(1 + k)
    checkGammaSimp("Binomial(n + 1, k + 1)/Binomial(n, k)", "(1 + n)/(1 + k)", 0);
    // assert combsimp(binomial(n, k + 1)/binomial(n, k)) == Mul(n - k, 1/(k + 1))
    checkGammaSimp("Binomial(n, k + 1)/Binomial(n, k)", "(n - k)/(k + 1)", 0);
    // assert combsimp(binomial(n + 2, k + S.Half)/binomial(n, k)) ... uses gamma functions
    // assert combsimp(factorial(n + 1)/factorial(n)) == n + 1
    checkGammaSimp("(n + 1)!/n!", "n + 1", 0);
    // Pochhammer(a, 3)
    checkGammaSimp("Pochhammer(a, n+1)/Pochhammer(a, n)", "a + n", 0);
    // Beta(a, b+1)/Beta(a, b) = b/(a+b)
    checkGammaSimp("Beta(a, b+1)/Beta(a, b)", "b/(a+b)", 0);

    assertEquals("n!", GammaSimp.combsimp(eval("n*(n-1)!")).toString());
    assertEquals("x+y", GammaSimp.gammasimp(eval("x+y")).toString());
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
