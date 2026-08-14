package org.matheclipse.core.polynomials.longexponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Tests {@link ExprPolynomial#gcd(ExprPolynomial)} for more than one variable, which is implemented
 * by {@link ExprPolynomialGcd}.
 */
public class ExprPolynomialGcdTest extends ExprEvaluatorTestCase {

  private EvalEngine engine() {
    return EvalEngine.get();
  }

  private IAST variables(String... names) {
    IAST list = (IAST) engine().evaluate("{" + String.join(",", names) + "}");
    return list;
  }

  private ExprPolynomial polynomial(ExprPolynomialRing ring, String expression) {
    return ring.create(engine().evaluate(F.ExpandAll(engine().evaluate(expression))));
  }

  private void checkGcd(String[] names, String a, String b, String expected) {
    ExprPolynomialRing ring = new ExprPolynomialRing(variables(names));
    IExpr gcd =
        engine().evaluate(F.ExpandAll(polynomial(ring, a).gcd(polynomial(ring, b)).getExpr()));
    IExpr want = engine().evaluate(F.ExpandAll(engine().evaluate(expected)));
    assertEquals(want.toString(), gcd.toString(), "gcd(" + a + ", " + b + ")");
  }

  @Test
  public void testTwoVariables() {
    checkGcd(new String[] {"x", "y"}, "x^2-y^2", "x^2-2*x*y+y^2", "x-y");
    checkGcd(new String[] {"x", "y"}, "(x-y)*(x+y)", "(x-y)^2*(x+2*y)", "x-y");
    checkGcd(new String[] {"x", "y"}, "(x^2+y^2+1)*(x-y)", "(x^2+y^2+1)*(x+y)", "x^2+y^2+1");
    checkGcd(new String[] {"x", "y"}, "x^2*y^2", "x*y", "x*y");
  }

  @Test
  public void testCoprime() {
    checkGcd(new String[] {"x", "y"}, "x+y", "x-y", "1");
    checkGcd(new String[] {"x", "y"}, "x^2+y", "y^2+x", "1");
  }

  @Test
  public void testThreeAndFourVariables() {
    checkGcd(new String[] {"x", "y", "z"}, "(x*y+z)*(x-z)", "(x*y+z)*(y+z)", "x*y+z");
    checkGcd(new String[] {"x", "y", "z"}, "x*y*z", "x^2*y^2*z^2", "x*y*z");
    checkGcd(new String[] {"w", "x", "y", "z"}, "(w*x+y*z)*(w-z)", "(w*x+y*z)*(x+y)", "w*x+y*z");
  }

  /** A parameter of the coefficient domain is just another variable of the ring. */
  @Test
  public void testParameterAsVariable() {
    checkGcd(new String[] {"a", "x", "y"}, "a*x+a*y", "x^2-y^2", "x+y");
    checkGcd(new String[] {"a", "x", "y"}, "a*x", "a*y", "a");
    checkGcd(new String[] {"a", "x", "y"}, "a^2*x*y-a^2*y^2", "a*x^2-a*y^2", "a*x-a*y");
  }

  /**
   * Coefficients which the conversion to a JAS polynomial does not accept. They are not part of the
   * result, it is normalized to a leading coefficient of 1.
   */
  @Test
  public void testSymbolicCoefficients() {
    checkGcd(new String[] {"x", "y"}, "Sqrt(2)*(x^2-y^2)", "x-y", "x-y");
    checkGcd(new String[] {"x", "y"}, "Sin(z)*(x^2-y^2)", "Sin(z)*(x-y)", "x-y");
    checkGcd(new String[] {"x", "y"}, "Pi*(x^2-y^2)", "x^2-2*x*y+y^2", "x-y");
  }

  @Test
  public void testZeroArgument() {
    ExprPolynomialRing ring = new ExprPolynomialRing(variables("x", "y"));
    ExprPolynomial a = polynomial(ring, "x^2-y^2");
    assertEquals(a.toString(), a.gcd(ring.getZero()).toString());
    assertEquals(a.toString(), ring.getZero().gcd(a).toString());
  }

  /**
   * <code>gcd(g*p, g*q) == g</code> up to a constant factor, for coprime <code>p</code> and
   * <code>q</code>. Coprimality is decided by the JAS engine behind <code>PolynomialGCD</code>,
   * which is independent of the code under test.
   */
  private void checkRandomized(String[] names, int rounds, int terms, int maxDegree, long seed) {
    IAST variables = variables(names);
    ExprPolynomialRing ring = new ExprPolynomialRing(variables);
    Random random = new Random(seed);
    int checked = 0;
    for (int round = 0; round < rounds; round++) {
      IExpr g = randomPolynomial(random, variables, terms, maxDegree);
      IExpr p = randomPolynomial(random, variables, terms, maxDegree);
      IExpr q = randomPolynomial(random, variables, terms, maxDegree);
      if (g.isZero() || p.isZero() || q.isZero()) {
        continue;
      }
      if (!engine().evaluate(F.PolynomialGCD(p, q)).isNumber()) {
        continue; // p and q are not coprime, g is not the expected result
      }
      ExprPolynomial a = ring.create(engine().evaluate(F.ExpandAll(F.Times(g, p))));
      ExprPolynomial b = ring.create(engine().evaluate(F.ExpandAll(F.Times(g, q))));
      IExpr expected = ring.create(engine().evaluate(F.ExpandAll(g))).monic().getExpr();
      IExpr ratio = engine().evaluate(F.Cancel(F.Together(F.Divide(a.gcd(b).getExpr(), expected))));
      assertTrue(ratio.isNumber(), "gcd((" + g + ")*(" + p + "), (" + g + ")*(" + q + ")) = "
          + a.gcd(b).getExpr() + ", expected " + expected + " up to a constant factor");
      checked++;
    }
    assertTrue(checked > 0, "no round of the randomized test was applicable");
  }

  private IExpr randomPolynomial(Random random, IAST variables, int terms, int maxDegree) {
    IExpr sum = F.C0;
    for (int term = 0; term < terms; term++) {
      IExpr monomial = F.ZZ(random.nextInt(9) - 4);
      for (int i = 1; i < variables.size(); i++) {
        monomial =
            F.Times(monomial, F.Power(variables.get(i), F.ZZ(random.nextInt(maxDegree + 1))));
      }
      sum = F.Plus(sum, monomial);
    }
    return engine().evaluate(F.ExpandAll(sum));
  }

  @Test
  public void testRandomizedTwoVariables() {
    checkRandomized(new String[] {"x", "y"}, 60, 3, 3, 11L);
  }

  @Test
  public void testRandomizedThreeVariables() {
    checkRandomized(new String[] {"x", "y", "z"}, 40, 3, 2, 22L);
  }

  @Test
  public void testRandomizedFourVariables() {
    checkRandomized(new String[] {"w", "x", "y", "z"}, 25, 2, 2, 33L);
  }
}
