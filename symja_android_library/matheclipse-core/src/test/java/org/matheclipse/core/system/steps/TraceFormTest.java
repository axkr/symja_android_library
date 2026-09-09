package org.matheclipse.core.system.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.commons.io.output.StringBuilderWriter;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * The step-by-step display of {@link org.matheclipse.core.expression.S#TraceForm}: the shape of the
 * expression it evaluates to, how the steps are nested, the depth cap and the step levels.
 */
public class TraceFormTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
  }

  // ---------------------------------------------------------------- the shape of the result

  @Test
  public void testResultIsADisplayWrapper() {
    // the result keeps its steps and does not evaluate any further
    check("FullForm(TraceForm(1+1))", //
        "TraceForm(HoldForm(2), List())");
  }

  @Test
  public void testBuiltFormStaysUnevaluated() {
    // an already built form is a display wrapper, like TableForm
    check("FullForm(TraceForm(HoldForm(x), {}))", //
        "TraceForm(HoldForm(x), List())");
  }

  @Test
  public void testResultIsHeld() {
    // taking the tree apart must not evaluate the result again
    check("TraceForm(D(x^2,x))[[1]]", //
        "2*x");
  }

  @Test
  public void testStepShape() {
    // {HoldForm(input), HoldForm(result), {symbol, "RuleKey", hint...}, {subStep...}}
    check("Length(TraceForm(D(x^2,x))[[2,1]])", //
        "4");
    check("TraceForm(D(x^2,x))[[2,1,3,1]]", //
        "D");
    check("TraceForm(D(x^2,x))[[2,1,3,2]]", //
        "PowerRule");
  }

  // ---------------------------------------------------------------- the hierarchy

  @Test
  public void testStepsAreNested() {
    // the chain rule causes the power rule, which causes the identity rule
    check("TraceForm(D(Sin(x^2),x))[[2,1,3,2]]", //
        "ChainRule");
    check("TraceForm(D(Sin(x^2),x))[[2,1,4,1,3,2]]", //
        "PowerRule");
    check("TraceForm(D(Sin(x^2),x))[[2,1,4,1,4,1,3,2]]", //
        "IdentityRule");
  }

  @Test
  public void testOutputForm() {
    assertEquals("2*x*Cos(x^2)\n" //
        + "If $h(x) = f(g(x))$ apply the chain rule $h'(x) = f'(g(x)) \\cdot g'(x)$.\n" //
        + "D(Sin(x^2),x) -> D(x^2,x)*Sin'(x^2)\n" //
        + "  The derivative of $a \\cdot x^n$ is $n \\cdot a \\cdot x^{(n-1)}$.\n" //
        + "  D(x^2,x) -> 2*x^1*D(x,x)\n" //
        + "    Apply the identity rule $\\frac{d}{dx} x = 1$ - expression D(x,x) is rewritten as 1.\n" //
        + "    D(x,x) -> 1", //
        evalString("TraceForm(D(Sin(x^2),x))").trim());
  }

  @Test
  public void testTeXForm() {
    String tex = evalString("TeXForm(TraceForm(D(x^2,x)))");
    assertTrue(tex.startsWith("\\begin{array}{l}"), tex);
    assertTrue(tex.contains("\\rightarrow"), tex);
    assertTrue(tex.endsWith("\\end{array}"), tex);
  }

  @Test
  public void testTeXFormIndentsNestedSteps() {
    // one \hspace per level the step is nested in
    String tex = evalString("TeXForm(TraceForm(D(Sin(x^2),x)))");
    assertTrue(tex.contains("\\hspace{1.5em}\\hspace{1.5em}"), tex);
  }

  @Test
  public void testMathMLForm() {
    // through MathMLUtilities, the way the servlets reach the MathML factory. Note that the
    // MathMLForm() function does not evaluate its argument, so it cannot be used here.
    EvalEngine engine = EvalEngine.get();
    IExpr traceForm = engine.evaluate(engine.parse("TraceForm(D(x^2,x))"));
    StringBuilderWriter out = new StringBuilderWriter();
    assertTrue(new MathMLUtilities(engine, false, true).toMathML(traceForm, out), "conversion");
    String mathml = out.toString();
    assertTrue(mathml.contains("<mtable columnalign=\"left\">"), mathml);
    assertTrue(mathml.contains("<mtext>"), mathml);
    assertTrue(mathml.contains("&#x2192;"), mathml);
  }

  // ---------------------------------------------------------------- the depth cap

  @Test
  public void testDefaultDepthIsFive() {
    // the fifth level is the deepest one shown
    check("TraceForm(D(Sin(Sin(Sin(Sin(x^2)))),x))[[2,1,4,1,4,1,4,1,4,1,3,2]]", //
        "PowerRule");
    // and the sixth is dropped and marked
    check("TraceForm(D(Sin(Sin(Sin(Sin(x^2)))),x))[[2,1,4,1,4,1,4,1,4,1,4,1,3,2]]", //
        "Truncated");
    // that sixth level is really there, it is the cap which is hiding it
    check("TraceForm(D(Sin(Sin(Sin(Sin(x^2)))),x), Infinity)[[2,1,4,1,4,1,4,1,4,1,4,1,3,2]]", //
        "IdentityRule");
  }

  @Test
  public void testTheDepthCountsStepsTheReaderSees() {
    // The cap used to count recorded levels, and an integration rule set reaches its answer
    // through rewrites which say nothing once they are written the ordinary way. Those are
    // dropped when the derivation is rendered, so counting them spent the reader's depth on
    // steps the reader never saw and an ordinary integral stopped short of its answer.
    check("Length(Cases(TraceForm(Integrate(Sin(x)/x^3,x)), "
        + "{_,_,{TraceForm,\"Truncated\"},_}, Infinity))", //
        "0");
    check("TraceForm(Integrate(Sin(x)/x^3,x))[[2,1,4,1,4,1,4,1,2]]", //
        "SinIntegral(x)");
  }

  @Test
  public void testTheMarkerSaysWhyTheDerivationStops() {
    // its sentence was looked up under the bare rule key while `i18n/en.json` holds the
    // qualified one, so a capped derivation used to stop without a word about it
    String steps = evalString("TraceForm(D(Sin(Sin(x^2)),x), 1)");
    assertTrue(steps.contains("Further sub-steps are not shown"), steps);
  }

  @Test
  public void testDepthOne() {
    check("TraceForm(D(Sin(x^2),x), 1)[[2,1,4,1,3]]", //
        "{TraceForm,Truncated}");
  }

  @Test
  public void testDepthZeroKeepsOnlyTheMarker() {
    // nothing is shown, but the reader is told that there would have been something
    check("TraceForm(D(Sin(x^2),x), 0)[[2]]", //
        "{{Null,Null,{TraceForm,Truncated},{}}}");
  }

  @Test
  public void testInfiniteDepth() {
    check("TraceForm(D(Sin(x^2),x), Infinity)[[2,1,4,1,4,1,3,2]]", //
        "IdentityRule");
  }

  @Test
  public void testNegativeDepthIsRejected() {
    check("TraceForm(D(x^2,x), -1)", //
        "TraceForm(D(x^2,x),-1)");
  }

  // ---------------------------------------------------------------- the step levels

  @Test
  public void testRuleLevelHidesAlgebraSteps() {
    // Cancel announces its factoring at the "Algebra" level only
    check("TraceForm(Cancel((x^2-1)/(x-1)))[[2]]", //
        "{}");
  }

  @Test
  public void testAlgebraLevelShowsCancelling() {
    check("TraceForm(Cancel((x^2-1)/(x-1)), Infinity, \"Algebra\")[[2,1,3,2]]", //
        "Factor");
    check("TraceForm(Cancel((x^2-1)/(x-1)), Infinity, \"Algebra\")[[2,2,3,2]]", //
        "CancelCommonFactors");
  }

  @Test
  public void testRuleLevelHidesArithmeticSteps() {
    // the arithmetic of the quadratic formula, "Square -4", "Take the square root of 28", ...
    check("Length(TraceForm(QuarticSolve(1,-4,-3))[[2]])", //
        "4");
  }

  @Test
  public void testArithmeticLevelShowsTheQuadraticFormula() {
    check("Length(TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Arithmetic\")[[2]])", //
        "10");
    check("TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Arithmetic\")[[2,3,3,2]]", //
        "QuadraticFormulaSquareB");
  }

  @Test
  public void testLevelAcceptsAnInteger() {
    check("TraceForm(Cancel((x^2-1)/(x-1)), Infinity, 2)[[2,1,3,2]]", //
        "Factor");
  }

  @Test
  public void testLevelNoneKeepsNoSteps() {
    check("TraceForm(D(x^2,x), Infinity, \"None\")[[2]]", //
        "{}");
  }

  // ---------------------------------------------------------------- rewrite rules

  @Test
  public void testUserRuleIsAStep() {
    // the rule which fired, its right-hand-side and what that evaluated to
    check("f(x_):=x^2; TraceForm(f(3))[[2,1,3]]", //
        "{f,RewriteRule,f(x_),x^2,9}");
  }

  @Test
  public void testFailingConditionLeavesNoStep() {
    // the rule never applied, so it must not appear as a step
    check("g(x_):=h(x) /; x>10; TraceForm(g(2))[[2]]", //
        "{}");
  }

  @Test
  public void testHoldingConditionShowsOnlyTheBody() {
    // `Condition(h(20), 20>10)` is recorded before the guard is known to hold; only the body shows
    check("g(x_):=h(x) /; x>10; TraceForm(g(20))[[2,1,2]]", //
        "h(20)");
  }

  @Test
  public void testRubiRuleCarriesItsRuleNumber() {
    check("TraceForm(Integrate(Sin(x)^3,x))[[2,1,3,2]]", //
        "RubiRule");
    check("IntegerQ(TraceForm(Integrate(Sin(x)^3,x))[[2,1,3,3]])", //
        "True");
  }

  @Test
  public void testRubiInternalsAreNotSteps() {
    // the predicates the Rubi rule set is built from are implementation detail, not steps
    check("Cases(TraceForm(Integrate(Sin(x)^3,x), Infinity)[[2]], "
        + "{_,_,{s_,___},_} :> s, Infinity) // DeleteDuplicates", //
        "{Integrate}");
  }

  // ---------------------------------------------------------------- the engine is put back

  @Test
  public void testEngineIsRestored() {
    // tracing must not leak into the evaluations which follow: Trace() sees the same thing
    // before and after a TraceForm
    check("before = Trace(D(Sin(x),x)); TraceForm(D(x^2,x)); "
        + "before === Trace(D(Sin(x),x))", //
        "True");
  }

  @Test
  public void testTraceIsUnchangedByTheRewriteRuleHooks() {
    // Trace() must not start reporting every rewrite rule: it does not ask for them
    check("f(x_):=x^2; Trace(f(3))", //
        "{{3^2,9},9}");
  }

  @Test
  public void testNestedTraceForm() {
    check("TraceForm(TraceForm(D(x^2,x))[[1]])[[1]]", //
        "2*x");
  }
}
