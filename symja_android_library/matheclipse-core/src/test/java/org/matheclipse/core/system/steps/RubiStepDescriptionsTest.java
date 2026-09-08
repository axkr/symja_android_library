package org.matheclipse.core.system.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.steps.RubiStepDescriptions;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * What the Rubi rule set says its own rules do, read out of the packed table which
 * <code>ConvertRubiShowSteps</code> writes.
 */
public class RubiStepDescriptionsTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
  }

  @Test
  public void testTheTableIsPacked() {
    // the first rule of the rule set, so the resource is really on the classpath
    assertNotNull(RubiStepDescriptions.get(1), "rubi/rubi_steps.tsv.gz is missing");
  }

  @Test
  public void testFirstRule() {
    RubiStepDescriptions.Description description = RubiStepDescriptions.get(1);
    assertEquals("If EqQ[a,0]", description.condition());
    assertEquals("Integrate[(u)*((a) + (b)*(x)^(n))^(p), x]", description.beforeText());
    assertEquals("Integrate[u*(b*x^n)^p, x]", description.afterText());
  }

  @Test
  public void testBoxesAreFlattened() {
    // the condition is written in Mathematica's box notation and has to come back as text
    assertEquals("If EqQ[n2,2 n]&&EqQ[b^2 c+a^2 d,0]&&IntegersQ[m,mm]&&EqQ[m+mm,0]",
        RubiStepDescriptions.get(13).condition());
    // a FractionBox
    assertEquals("If IntegerQ[Simplify[(m+1)/(n)]]", RubiStepDescriptions.get(21).condition());
  }

  @Test
  public void testTemplatesAreReadAsExpressions() {
    RubiStepDescriptions.Description description = RubiStepDescriptions.get(3125);
    // held, so that rendering the rule's general shape never sets the integrator going on it
    assertEquals(S.HoldForm, description.before().head());
    assertEquals(S.HoldForm, description.after().head());
    assertEquals("Integrate(Sin(c+d*x)^n,x)", description.before().toString());
    // the rule set's display operator for "this factor stands in front of that integral"
    assertTrue(description.afterText().indexOf('⋆') > 0, description.afterText());
  }

  @Test
  public void testAPlumbingRuleHasNoDescription() {
    // rule 3054 only deactivates the trigonometric functions so the trig rules can match; Rubi
    // does not show it as a step of its own
    assertNull(RubiStepDescriptions.get(3054));
  }

  @Test
  public void testAnUnknownRuleNumberIsNull() {
    assertNull(RubiStepDescriptions.get(999999));
    assertNull(RubiStepDescriptions.get(-1));
  }

  // ---------------------------------------------------------------- in a derivation

  @Test
  public void testADescribedRuleNamesItsConditionAndRewrite() {
    String steps = evalString("TraceForm(Integrate(Sin(x)^3,x), 2)");
    assertTrue(steps.contains("Rubi integration rule 3125."), steps);
    assertTrue(steps.contains("If IGtQ[(n-1)/(2),0], rewrite Integrate(Sin(c+d*x)^n,x) as"), steps);
  }

  @Test
  public void testAnUndescribedRuleStillNamesItself() {
    String steps = evalString("TraceForm(Integrate(Sin(x)^3,x), 1)");
    assertTrue(steps.contains("Apply integration rule 3054 of the Rubi rule set"), steps);
  }

  @Test
  public void testTheRewriteIsTypesetInTeX() {
    // the rule's general shape is an expression, so TeXForm renders it as mathematics rather than
    // as the source it is stored as
    String tex = evalString("TeXForm(TraceForm(Integrate(Sin(x)^3,x), 2))");
    assertTrue(tex.contains("\\int  {\\sin (c + d \\cdot x)}^{n}\\,\\mathrm{d}x"), tex);
  }
}
