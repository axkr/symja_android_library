package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.integrate.DerivativeDivides;
import org.matheclipse.core.integrate.IntegralTable;
import org.matheclipse.core.integrate.ProductPowerIntegration;
import org.matheclipse.core.integrate.RadicalSubstitution;
import org.matheclipse.core.integrate.RationalIntegration;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Tests for the native integration algorithm stages ({@link IntegralTable},
 * {@link RationalIntegration}, {@link RadicalSubstitution}, {@link DerivativeDivides},
 * {@link org.matheclipse.core.integrate.RischNorman}) which are called from
 * <code>Integrate</code> in addition to the Rubi rules.
 */
public class IntegrateAlgorithmsTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    try {
      S.Integrate.getEvaluator().await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Verify <code>D(Integrate(integrand, x), x) == integrand</code>.
   */
  private void checkAntiderivative(String integrand) {
    check("Simplify(Together(D(Integrate(" + integrand + ", x), x) - (" + integrand + ")))", //
        "0");
  }

  /**
   * Verify that the result of a stage call is a valid antiderivative of the integrand.
   */
  private static void assertAntiderivative(IExpr result, IExpr integrand, IExpr x,
      EvalEngine engine) {
    assertTrue(result.isPresent(), "no result returned");
    IExpr diff = engine
        .evaluate(F.Simplify(F.Together(F.Subtract(F.D(result, x), integrand))));
    assertTrue(diff.isZero(), "D(result) != integrand, difference: " + diff);
  }

  @Test
  public void testIntegralTableStage() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");

    IExpr integrand = engine.parse("Sin(2+3*x)");
    IExpr result = IntegralTable.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);

    integrand = engine.parse("Log(1+2*x)");
    result = IntegralTable.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);

    integrand = engine.parse("Sech(5*x)^2");
    result = IntegralTable.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);
  }

  @Test
  public void testRationalIntegrationStage() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");

    // logarithmic part with linear and quadratic factors
    IExpr integrand = engine.parse("(2*x+1)/(x^2+x+1)");
    IExpr result = RationalIntegration.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);

    // Horowitz-Ostrogradsky reduction (multiple denominator factors)
    integrand = engine.parse("1/(x^2*(x^2+1))");
    result = RationalIntegration.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);

    // polynomial part + remainder
    integrand = engine.parse("(x^3+x+1)/(x^2+1)");
    result = RationalIntegration.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);
  }

  @Test
  public void testRationalIntegrationRejectsSymbolicCoefficients() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");
    IExpr integrand = engine.parse("a/(x^2+b)");
    // symbolic coefficients must be rejected so the caller falls back to Rubi
    assertTrue(RationalIntegration.integrate(integrand, x, engine).isNIL());
  }

  @Test
  public void testRadicalSubstitutionStage() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");

    IExpr integrand = engine.parse("x*Sqrt(1+x)");
    IExpr result = RadicalSubstitution.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);

    integrand = engine.parse("1/(1+Sqrt(x))");
    result = RadicalSubstitution.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);
  }

  @Test
  public void testDerivativeDividesStage() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");

    IExpr integrand = engine.parse("Cos(x)*E^Sin(x)");
    IExpr result = DerivativeDivides.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);
  }

  @Test
  public void testProductPowerStage() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = engine.parse("x");

    // single symbolic power factor: Integrate((a+b*x)^m) = (a+b*x)^(1+m)/(b*(1+m))
    IExpr integrand = engine.parse("(a+b*x)^m");
    IExpr result = ProductPowerIntegration.integrate(integrand, x, engine);
    assertAntiderivative(result, integrand, x, engine);
  }

  @Test
  public void testProductPowerMultiFactorEndToEnd() {
    // Two symbolic power factors with a compatible polynomial cofactor. Rubi leaves this
    // unevaluated (its linearity split over the cofactor produces unresolved pieces); the
    // product-power ansatz recovers the closed form before the rules run. The diff-back cannot be
    // simplified to zero here (Together does not know P^(1+m) = P*P^m), so assert the closed form.
    assertTrue(Config.INTEGRATE_ALGORITHMS);
    check(
        "Integrate((a+b*x+c*x^2)^m*(d+e*x+f*x^2+g*x^3)^n*(a*d+(2*b*d+2*a*e+b*d*m+a*e*n)*x"
            + "+(3*c*d+3*b*e+3*a*f+2*c*d*m+b*e*m+b*e*n+2*a*f*n)*x^2+(4*c*e+4*b*f+4*a*g+2*c*e*m"
            + "+b*f*m+c*e*n+2*b*f*n+3*a*g*n)*x^3+(5*c*f+5*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n)*x^4"
            + "+c*g*(6+2*m+3*n)*x^5), x)", //
        "x*(a+b*x+c*x^2)^(1+m)*(d+e*x+f*x^2+g*x^3)^(1+n)");
  }

  @Test
  public void testIntegrateEndToEnd() {
    // exercises the full Integrate pipeline with all stages enabled
    assertTrue(Config.INTEGRATE_ALGORITHMS);
    checkAntiderivative("Sin(2+3*x)");
    checkAntiderivative("Cos(x)^2");
    checkAntiderivative("(2*x+1)/(x^2+x+1)");
    checkAntiderivative("1/(x^3+1)");
    checkAntiderivative("x*Sqrt(1+x)");
    checkAntiderivative("Cos(x)*E^Sin(x)");
  }

  @Test
  public void testIntegrateStagesDisabled() {
    // with the master switch off the Rubi rules still handle the integrands
    boolean oldFlag = Config.INTEGRATE_ALGORITHMS;
    Config.INTEGRATE_ALGORITHMS = false;
    try {
      checkAntiderivative("Sin(2+3*x)");
      checkAntiderivative("(2*x+1)/(x^2+x+1)");
    } finally {
      Config.INTEGRATE_ALGORITHMS = oldFlag;
    }
  }
}
