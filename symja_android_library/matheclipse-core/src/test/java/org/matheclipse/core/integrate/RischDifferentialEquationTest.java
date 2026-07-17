package org.matheclipse.core.integrate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Unit tests for the Risch differential equation core {@link RischDifferentialEquation} (the base
 * case of the transcendental Risch algorithm). See {@code INTEGRATE_MATHILDA_PORT_PLAN.md} tier T4.
 */
public class RischDifferentialEquationTest extends ExprEvaluatorTestCase {

  /** Assert {@code solvePolynomialRDE(f, g)} yields a polynomial equal to {@code expectedY}. */
  private void assertRDE(String f, String g, String expectedY) {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");
    IExpr q =
        RischDifferentialEquation.solvePolynomialRDE(engine.parse(f), engine.parse(g), x, engine);
    assertTrue(q.isPresent(), "expected a solution for D(y) + (" + f + ")*y = " + g);
    IExpr matches = engine.evaluate(F.Together(F.Subtract(q, engine.parse(expectedY))));
    assertTrue(matches.isZero(), "solution " + q + " != " + expectedY);
    // Independently confirm it solves the RDE: D(q) + f*q - g == 0.
    IExpr residual = engine.evaluate(F.Together(
        F.Subtract(F.Plus(F.D(q, x), F.Times(engine.parse(f), q)), engine.parse(g))));
    assertTrue(residual.isZero(), "not a valid RDE solution: residual " + residual);
  }

  /** Assert {@code solvePolynomialRDE(f, g)} has no polynomial solution (the decision case). */
  private void assertNoRDE(String f, String g) {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");
    IExpr q =
        RischDifferentialEquation.solvePolynomialRDE(engine.parse(f), engine.parse(g), x, engine);
    assertTrue(q.isNIL(), "expected no polynomial solution for D(y) + (" + f + ")*y = " + g);
  }

  @Test
  public void solvesKnownRDEs() {
    assertRDE("2*x", "2*x", "1"); // Integrate(2*x*E^(x^2)) = E^(x^2)
    assertRDE("2*x", "2*x^2 + 1", "x"); // Integrate((2*x^2+1)*E^(x^2)) = x*E^(x^2)
    assertRDE("1", "x", "x - 1"); // Integrate(x*E^x) = (x-1)*E^x
    assertRDE("-1", "x^2", "-x^2 - 2*x - 2"); // Integrate(x^2*E^(-x)) = (-x^2-2*x-2)*E^(-x)
  }

  @Test
  public void decidesUnsolvableRDEs() {
    // No polynomial q with q' + 2x*q = 1: Integrate(E^(x^2)) has no elementary q*E^(x^2) form.
    assertNoRDE("2*x", "1");
    // Likewise Integrate(E^(x^3)).
    assertNoRDE("3*x^2", "1");
  }

  /** Assert {@code solveRationalRDE(f, g)} yields a rational function equal to {@code expectedY}. */
  private void assertRationalRDE(String f, String g, String expectedY) {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");
    IExpr y =
        RischDifferentialEquation.solveRationalRDE(engine.parse(f), engine.parse(g), x, engine);
    assertTrue(y.isPresent(), "expected a rational solution for D(y) + (" + f + ")*y = " + g);
    IExpr matches = engine.evaluate(F.Together(F.Subtract(y, engine.parse(expectedY))));
    assertTrue(matches.isZero(), "solution " + y + " != " + expectedY);
    IExpr residual = engine.evaluate(F.Together(
        F.Subtract(F.Plus(F.D(y, x), F.Times(engine.parse(f), y)), engine.parse(g))));
    assertTrue(residual.isZero(), "not a valid RDE solution: residual " + residual);
  }

  /** Assert {@code solveRationalRDE(f, g)} has no rational solution. */
  private void assertNoRationalRDE(String f, String g) {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");
    IExpr y =
        RischDifferentialEquation.solveRationalRDE(engine.parse(f), engine.parse(g), x, engine);
    assertTrue(y.isNIL(), "expected no rational solution for D(y) + (" + f + ")*y = " + g);
  }

  @Test
  public void solvesRationalRDEs() {
    assertRationalRDE("1", "(x - 1)/x^2", "1/x"); // Integrate((x-1)/x^2*E^x) = E^x/x
    assertRationalRDE("1", "(x - 2)/(x - 1)^2", "1/(x - 1)");
    assertRationalRDE("2*x", "(2*x^2 - 1)/x^2", "1/x"); // Integrate((2x^2-1)/x^2*E^(x^2)) = E^(x^2)/x
  }

  @Test
  public void decidesRationalRDEsWithSimplePole() {
    // g has a simple pole and f is a polynomial -> no rational solution (Integrate(E^x/x) = Ei).
    assertNoRationalRDE("1", "1/x");
    assertNoRationalRDE("2*x", "1/x");
  }

  @Test
  public void solvesRationalRDEsNeedingWeakNormalizer() {
    // f = 1/x has a simple pole with residue 1 (a positive integer): needs the weak normalizer.
    assertRationalRDE("1/x", "1/x", "1"); // y' + y/x = 1/x -> y = 1
    assertRationalRDE("2/x", "2/x", "1"); // residue 2
    assertRationalRDE("1/x", "1", "x/2"); // y' + y/x = 1 -> y = x/2
  }
}
