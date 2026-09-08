package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.interfaces.IExpr;
import edu.jas.kern.JASConfig;

/**
 * {@link JASConfig#USE_SPARSE_GCD} - Zippel's sparse interpolation, selected by
 * {@link org.matheclipse.core.basic.Config#JAS_GCD_SPARSE} - must change the running time of the
 * greatest common divisor and nothing else.
 *
 * <p>
 * Every expression below is evaluated twice, once with the flag off and once with it on, and the
 * two results are compared with each other rather than with a written down answer. A change of the
 * expected output on the default path therefore cannot make this test pass silently, and the test
 * needs no maintenance when unrelated normalization changes.
 */
public class SparseGcdTest extends ExprEvaluatorTestCase {

  private static final String[] EXPRESSIONS = { //
      // PolynomialGCD, the direct beneficiary
      "PolynomialGCD(x^2 - y^2, x^2 - 2*x*y + y^2)", //
      "PolynomialGCD(x^2 + 2*x*y + y^2, x^3 + y^3)", //
      "PolynomialGCD((x - a)*(b*x - c)^2, (x - a)*(x^2 - b*c))", //
      "PolynomialGCD(x^4 - 4, x^4 + 4*x^2 + 4)", //
      "PolynomialGCD(3*x + 9, 6*x^3 - 3*x + 12)", //
      "PolynomialGCD(a*x + a*y, x^2 - y^2)", //
      "PolynomialGCD(2*x*y, 4*x^2)", //
      "PolynomialGCD(x^2 - 1, x^3 - 1, x^4 - 1, x^5 - 1, x^6 - 1, x^7 - 1)", //
      "PolynomialGCD(a + b*x, c + d*x)", //
      // several variables, where the sparse algorithm is supposed to be used
      "PolynomialGCD((x*y + z)*(x - z), (x*y + z)*(y + z))", //
      "PolynomialGCD((w*x + y*z)*(w - z), (w*x + y*z)*(x + y))", //
      "PolynomialGCD((x^2*y + z^3 + 1)*(x + y + z), (x^2*y + z^3 + 1)*(x - y))", //
      "PolynomialGCD(x^3*y^2*z, x^2*y^3*z^2)", //
      // coefficients the JAS conversion rejects, the ExprPolynomial fallback
      "PolynomialGCD(Sqrt(2)*x^2 - Sqrt(2)*y^2, x - y)", //
      // Modulus
      "PolynomialGCD((x + 1)^3, x^3 + x, Modulus -> 2)", //
      // PolynomialLCM
      "PolynomialLCM(x^2 - 1, x - 1)", //
      "PolynomialLCM((1 + x)^2*(2 + x)*(4 + x), (1 + x)*(2 + x)*(3 + x))", //
      "PolynomialLCM(x^2 + 2*x*y + y^2, x^3 + y^3)", //
      "PolynomialLCM(x^2 + 1, x + 1, Modulus -> 2)", //
      // everything else which reaches a gcd
      "Together(1/(x - y) + 1/(x + y))", //
      "Together(x/(x^2 - y^2) + y/(x - y))", //
      "Cancel((x^2 - y^2)/(x - y))", //
      "Cancel((x^3 - 1)/(x - 1))", //
      "Apart(1/(x^2 - 1))", //
      "Factor(x^4 - 1)", //
      "Factor(x^2*y^2 - 1)", //
      "Factor(x^3 + y^3)", //
      "FactorSquareFree(x^3 - 3*x + 2)", //
      // resultants, which the sparse engine does not compute itself but must not break
      "Resultant(x^2 + y, x + z, x)", //
      "Resultant(x^2 - 1, x^3 - 1, x)", //
      "Resultant(f + g*x + h*x^2, a + b*x, x)", //
      // integration over rational functions runs the gcd underneath
      "Integrate(1/(x^2 - 1), x)", //
      "Integrate(x/(x^2 + 2*x + 1), x)", //
      // Groebner bases reach the engine through edu.jas.gbufd
      "GroebnerBasis({x^2 + y^2 - 1, x - y}, {x, y})", //
      "GroebnerBasis({x^2 + y*z - 1, x*y - z^2}, {x, y, z})", //
  };

  @Test
  public void testSparseGcdChangesNoResult() {
    boolean saved = JASConfig.USE_SPARSE_GCD;
    try {
      for (String expression : EXPRESSIONS) {
        JASConfig.USE_SPARSE_GCD = false;
        IExpr dense = evaluator.eval(expression);
        JASConfig.USE_SPARSE_GCD = true;
        IExpr sparse = evaluator.eval(expression);
        assertEquals(dense.toString(), sparse.toString(),
            "sparse gcd changed the result of " + expression);
      }
    } finally {
      JASConfig.USE_SPARSE_GCD = saved;
    }
  }

}
