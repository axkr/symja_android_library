package org.matheclipse.core.system.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * A derivation of an integral shows mathematics, not the rule set's own working.
 *
 * <p>
 * A step is recorded from the right-hand side of the rule which fired, before the engine evaluates
 * it, so what the Rubi rules record is written in their own helpers: <code>Simp</code> for "this,
 * tidied up", <code>Dist</code> for a product held apart, and <code>§sin</code> for an inert
 * <code>Sin</code> the trigonometric simplifiers keep away from.
 */
public class StepDisplayTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
  }

  private String derivation(String integrand) {
    return evalString("TraceForm(Integrate(" + integrand + ",x), Infinity)");
  }

  // ---------------------------------------------------------------- the helpers are gone

  @Test
  public void testNoRuleSetHelpersSurvive() {
    for (String integrand : new String[] {"Sin(x)/x^3", "Sin(x)^3", "Tan(x)^2", "Cos(x)^4",
        "x^2*Sin(x)"}) {
      String steps = derivation(integrand);
      assertFalse(steps.contains("Rubi`"), integrand + ":\n" + steps);
      assertFalse(steps.contains("§"), integrand + ":\n" + steps);
      assertFalse(steps.contains("simp("), integrand + ":\n" + steps);
      assertFalse(steps.contains("dist("), integrand + ":\n" + steps);
    }
  }

  @Test
  public void testTheArithmeticOfARewriteIsWorkedOut() {
    // the rule records (0+1*x)^(1-3) and 1/(1*(1-3)); a reader is shown x^-2 and -1/2
    String steps = derivation("Sin(x)/x^3");
    assertTrue(steps.contains("Integrate(Sin(x)/x^3,x) -> Integrate(Cos(x)/x^2,x)/2-Sin(x)/(2*x^2)"),
        steps);
  }

  @Test
  public void testTheIntegralsAreLeftStanding() {
    // working out the arithmetic must not work out the integral: the step would then show the
    // answer instead of the step
    String steps = derivation("Sin(x)/x^3");
    assertTrue(steps.contains("Integrate(Cos(x)/x^2,x)"), steps);
  }

  // ---------------------------------------------------------------- the nesting

  @Test
  public void testASubIntegralIsAChildOfTheStepThatMadeIt() {
    check("TraceForm(Integrate(Sin(x)/x^3,x), Infinity)[[2,1,4,1,3,2]]", //
        "RubiRule");
    // and one deeper again
    check("TraceForm(Integrate(Sin(x)/x^3,x), Infinity)[[2,1,4,1,4,1,3,2]]", //
        "ConstantFactor");
  }

  @Test
  public void testTheDerivationIsOneChainNotAPileOfSiblings() {
    check("Length(TraceForm(Integrate(Sin(x)/x^3,x), Infinity)[[2]])", //
        "1");
  }

  @Test
  public void testTheIndentationShowsInTheTeX() {
    String tex = evalString("TeXForm(TraceForm(Integrate(Sin(x)/x^3,x), Infinity))");
    assertTrue(tex.contains("\\hspace{1.5em}\\hspace{1.5em}\\hspace{1.5em}"), tex);
  }

  // ---------------------------------------------------------------- the plumbing is dropped

  @Test
  public void testAStepWhichChangesNothingIsNotShown() {
    // rule 3054 only rewrites the integrand into the rule set's inert spelling
    String steps = derivation("Sin(x)/x^3");
    assertFalse(steps.contains("rule 3054"), steps);
  }

  @Test
  public void testAnAnnotationIsStillShown() {
    // `addTraceInfoStep` records an expression against itself on purpose, and those must survive
    // the same test that drops the plumbing
    String steps = evalString("TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Arithmetic\")");
    assertTrue(steps.contains("Square -4."), steps);
  }

  // ---------------------------------------------------------------- nothing else moved

  @Test
  public void testTheAnswersAreUnchanged() {
    check("Integrate(Sin(x)/x^3,x)", //
        "-Cos(x)/(2*x)-Sin(x)/(2*x^2)-SinIntegral(x)/2");
    check("TraceForm(Integrate(Sin(x)/x^3,x))[[1]]", //
        "-Cos(x)/(2*x)-Sin(x)/(2*x^2)-SinIntegral(x)/2");
    check("Integrate((x^2+x+1)/(x^4+x^3+x+1),x)", //
        "-1/(3*(1+x))+4/3*ArcTan((2*(-1/2+x))/Sqrt(3))/Sqrt(3)");
  }

  @Test
  public void testADerivationWithNoRuleSetInItIsUntouched() {
    assertEquals("2*x*Cos(x^2)\n" //
        + "If $h(x) = f(g(x))$ apply the chain rule $h'(x) = f'(g(x)) \\cdot g'(x)$.\n" //
        + "D(Sin(x^2),x) -> D(x^2,x)*Sin'(x^2)\n" //
        + "  The derivative of $a \\cdot x^n$ is $n \\cdot a \\cdot x^{(n-1)}$.\n" //
        + "  D(x^2,x) -> 2*x^1*D(x,x)\n" //
        + "    Apply the identity rule $\\frac{d}{dx} x = 1$ - expression D(x,x) is rewritten as 1.\n" //
        + "    D(x,x) -> 1", //
        evalString("TraceForm(D(Sin(x^2),x))").trim());
  }
}
