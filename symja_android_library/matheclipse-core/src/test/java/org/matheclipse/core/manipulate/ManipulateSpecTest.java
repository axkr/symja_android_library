package org.matheclipse.core.manipulate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Parsing of the control and option forms of <code>Manipulate</code>. */
public class ManipulateSpecTest {

  @BeforeAll
  public static void setUpClass() {
    F.initSymja();
  }

  private static ManipulateSpec parse(String input) {
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    IExpr expr = engine.evaluate(engine.parse(input));
    return ManipulateSpec.parse(expr, engine);
  }

  private static ManipulateControl singleControl(String input) {
    ManipulateSpec spec = parse(input);
    assertNotNull(spec, "not parsed: " + input);
    assertEquals(1, spec.getControls().size());
    return spec.getControls().get(0);
  }

  private static ObjectMapper mapper() {
    return new ObjectMapper();
  }

  @Test
  public void testSliderRange() {
    ManipulateControl control = singleControl("Manipulate(x^2, {a, 0, 10})");
    assertEquals(ManipulateControl.SLIDER, control.getKind());
    assertEquals("a", control.getName());
    // no step given: a hundredth of the range
    assertEquals(0.1, control.effectiveStep(), 1.0e-9);
  }

  @Test
  public void testSliderStep() {
    ManipulateControl control = singleControl("Manipulate(x^2, {a, 1, 20, 1})");
    assertEquals(ManipulateControl.SLIDER, control.getKind());
    assertEquals(1.0, control.effectiveStep(), 1.0e-9);
  }

  @Test
  public void testInitialValueAndLabel() {
    ManipulateControl control = singleControl("Manipulate(x^n, {{n, 2, \"exponent\"}, 1, 5, 1})");
    assertEquals(ManipulateControl.SLIDER, control.getKind());
    assertEquals("n", control.getName());
    assertEquals(Double.valueOf(2.0), control.initialValue());
    assertEquals("exponent", control.toJSON(mapper()).get("label").asText());
  }

  @Test
  public void testDiscreteChoices() {
    ManipulateControl control = singleControl("Manipulate(f, {f, {Sin, Cos, Tan}})");
    assertEquals(ManipulateControl.DISCRETE, control.getKind());
    assertEquals(3, control.getValues().size());
  }

  @Test
  public void testDiscreteRuleLabels() {
    ManipulateControl control = singleControl("Manipulate(c, {c, {1 -> \"one\", 2 -> \"two\"}})");
    assertEquals(ManipulateControl.DISCRETE, control.getKind());
    assertEquals(2, control.getValues().size());
    // the value is the left hand side of the rule, the label the right hand side
    assertEquals(F.C1, control.getValues().get(0));
    assertEquals("one", control.toJSON(mapper()).get("labels").get(0).asText());
  }

  @Test
  public void testBooleanPairIsACheckbox() {
    ManipulateControl control = singleControl("Manipulate(b, {b, {True, False}})");
    assertEquals(ManipulateControl.CHECKBOX, control.getKind());
  }

  @Test
  public void testInitialOnlyBooleanIsACheckbox() {
    ManipulateControl control = singleControl("Manipulate(b, {{b, True}})");
    assertEquals(ManipulateControl.CHECKBOX, control.getKind());
    assertEquals(Boolean.TRUE, control.initialValue());
  }

  @Test
  public void testSlider2D() {
    ManipulateControl control = singleControl("Manipulate(p, {p, {0, 0}, {10, 10}})");
    assertEquals(ManipulateControl.SLIDER2D, control.getKind());
  }

  @Test
  public void testControlTypeTrigger() {
    // the relaxed syntax lowercases symbol names, so the ControlType test has to ignore case
    ManipulateControl control =
        singleControl("Manipulate(k, {k, 0, 5, 1, ControlType -> Trigger})");
    assertEquals(ManipulateControl.TRIGGER, control.getKind());
  }

  @Test
  public void testControlTypeNoneDropsTheControl() {
    ManipulateSpec spec = parse("Manipulate(k, {k, 0, 5, ControlType -> None}, {j, 0, 1})");
    assertNotNull(spec);
    assertEquals(1, spec.getControls().size());
    assertEquals("j", spec.getControls().get(0).getName());
  }

  @Test
  public void testHeadingAndDelimiterRows() {
    ManipulateSpec spec = parse("Manipulate(g, Delimiter, \"a heading\", {g, 0, 1})");
    assertNotNull(spec);
    assertEquals(3, spec.getControls().size());
    assertEquals(ManipulateControl.DELIMITER, spec.getControls().get(0).getKind());
    assertEquals(ManipulateControl.HEADING, spec.getControls().get(1).getKind());
    assertEquals(ManipulateControl.SLIDER, spec.getControls().get(2).getKind());
    // only the slider binds a variable
    assertTrue(spec.getControls().get(2).bindsVariable());
    assertTrue(!spec.getControls().get(0).bindsVariable());
  }

  @Test
  public void testButtonRow() {
    ManipulateSpec spec = parse("Manipulate(k, {k, 0, 5}, Button(\"reset\", k = 0))");
    assertNotNull(spec);
    assertEquals(2, spec.getControls().size());
    ManipulateControl button = spec.getControls().get(1);
    assertEquals(ManipulateControl.BUTTON, button.getKind());
    assertTrue(button.getAction().isPresent());
    assertTrue(!button.bindsVariable());
  }

  @Test
  public void testAnimateIsAnimated() {
    ManipulateSpec spec = parse("Animate(Sin(t), {t, 0, 10})");
    assertNotNull(spec);
    assertTrue(spec.isAnimated());
  }

  @Test
  public void testManipulateIsNotAnimated() {
    ManipulateSpec spec = parse("Manipulate(Sin(t), {t, 0, 10})");
    assertNotNull(spec);
    assertTrue(!spec.isAnimated());
  }

  @Test
  public void testOptionsAreRead() {
    ManipulateSpec spec =
        parse("Manipulate(x, {x, 0, 10}, ControlPlacement -> Bottom, ContinuousAction -> False)");
    assertNotNull(spec);
    assertTrue(spec.getOption(S.ContinuousAction).isFalse());
    assertEquals("bottom", spec.toJSON(mapper()).get("options").get("controlPlacement").asText());
  }

  @Test
  public void testInitializationIsHeld() {
    ManipulateSpec spec = parse("Manipulate(n, {n, 0, 10}, Initialization :> (q = 5))");
    assertNotNull(spec);
    assertTrue(spec.getInitialization().isPresent());
  }

  @Test
  public void testTrackedSymbols() {
    ManipulateSpec spec = parse("Manipulate(a + b, {a, 0, 1}, {b, 0, 1}, TrackedSymbols :> {a})");
    assertNotNull(spec);
    List<String> tracked = spec.getTrackedSymbols();
    assertNotNull(tracked);
    assertEquals(1, tracked.size());
    assertEquals("a", tracked.get(0));
  }

  @Test
  public void testTrackedSymbolsDefaultsToEverything() {
    ManipulateSpec spec = parse("Manipulate(a, {a, 0, 1})");
    assertNotNull(spec);
    assertNull(spec.getTrackedSymbols());
  }

  @Test
  public void testAppearanceNone() {
    ManipulateSpec spec = parse("Manipulate(a, {a, 0, 1}, Appearance -> None)");
    assertNotNull(spec);
    assertTrue(spec.isAppearanceNone());
  }

  @Test
  public void testARowWithADynamicIsALiveReadOut() {
    // Manipulate[..., Row[{"moves: ", Dynamic[moves]}]] is the usual way to put a counter beside
    // the sliders. Taking its text once - which is what a heading row does - would freeze it.
    ManipulateSpec spec =
        parse("Manipulate(k, {k, 0, 5}, Row({\"moves: \", Dynamic(k)}))");
    assertNotNull(spec);
    assertEquals(2, spec.getControls().size());
    ManipulateControl display = spec.getControls().get(1);
    assertEquals(ManipulateControl.DISPLAY, display.getKind());
    assertTrue(display.getDisplay().isAST(S.Row));
    assertTrue(!display.bindsVariable());
  }

  @Test
  public void testATrailingDynamicIsALiveReadOut() {
    ManipulateSpec spec = parse("Manipulate(k, {k, 0, 5}, Dynamic(k))");
    assertNotNull(spec);
    assertEquals(2, spec.getControls().size());
    assertEquals(ManipulateControl.DISPLAY, spec.getControls().get(1).getKind());
  }

  @Test
  public void testAPlainStyleRowIsStillAHeading() {
    // only a Dynamic makes an annotation live; static text stays static
    ManipulateSpec spec = parse("Manipulate(k, {k, 0, 5}, Style(\"a heading\", Bold))");
    assertNotNull(spec);
    assertEquals(ManipulateControl.HEADING, spec.getControls().get(1).getKind());
  }

  @Test
  public void testEnabledDynamicLosesItsWrapper() {
    // Enabled -> Dynamic[cond] is how one control greys another out; the condition is resolved
    // against the live values, so the wrapper carries nothing
    ManipulateSpec spec =
        parse("Manipulate(a + b, {a, 0, 1}, {b, 0, 1, Enabled -> Dynamic(a > 0.5)})");
    assertNotNull(spec);
    IExpr condition = spec.getControls().get(1).getEnabledCondition();
    assertTrue(condition.isPresent());
    assertTrue(condition.isAST(S.Greater));
  }

  @Test
  public void testADynamicBoundIsReadThroughItsWrapper() {
    // {t, 0, Dynamic[period]} bounds a slider by another variable
    ManipulateSpec spec = parse("period = 4; Manipulate(t, {t, 0, Dynamic(period)})");
    assertNotNull(spec);
    ManipulateControl control = spec.getControls().get(0);
    assertEquals(ManipulateControl.SLIDER, control.getKind());
    assertEquals(0.04, control.effectiveStep(), 1.0e-9);
  }

  @Test
  public void testPaneSelectorSwapsControlPanels() {
    // one pane's controls are on screen at a time, but every pane's are in the panel: the hidden
    // ones stay bound, so the body may go on using them
    ManipulateSpec spec = parse("Manipulate(a + b + s, {s, {1, 2}},"
        + " PaneSelector({1 -> {a, 0, 1}, 2 -> {b, 0, 1}}, Dynamic(s)))");
    assertNotNull(spec);
    assertEquals(3, spec.getControls().size());
    assertNull(nullIfAbsent(spec.getControls().get(0).getVisibleCondition()));
    assertEquals("s==1", spec.getControls().get(1).getVisibleCondition().toString());
    assertEquals("s==2", spec.getControls().get(2).getVisibleCondition().toString());
    assertTrue(spec.getControls().get(1).bindsVariable());
  }

  @Test
  public void testPaneSelectorPaneWithSeveralControls() {
    // more than one control in a pane has to name each with Control, because a pane holding two
    // specifications and a single specification written as a list look the same
    ManipulateSpec spec = parse("Manipulate(x, {s, {1, 2}},"
        + " PaneSelector({1 -> {Control({a, 0, 1}), Control({b, 0, 1})}, 2 -> Control({c, 0, 1})},"
        + " Dynamic(s)))");
    assertNotNull(spec);
    assertEquals(4, spec.getControls().size());
    assertEquals("a", spec.getControls().get(1).getName());
    assertEquals("s==1", spec.getControls().get(1).getVisibleCondition().toString());
    assertEquals("b", spec.getControls().get(2).getName());
    assertEquals("s==1", spec.getControls().get(2).getVisibleCondition().toString());
    assertEquals("c", spec.getControls().get(3).getName());
    assertEquals("s==2", spec.getControls().get(3).getVisibleCondition().toString());
  }

  @Test
  public void testPaneSelectorOfProseIsALiveReadOut() {
    // panes that hold no controls are a caption that changes with the selector, not a panel
    ManipulateSpec spec = parse("Manipulate(x, {s, {1, 2}},"
        + " PaneSelector({1 -> \"first\", 2 -> \"second\"}, Dynamic(s)))");
    assertNotNull(spec);
    assertEquals(2, spec.getControls().size());
    assertEquals(ManipulateControl.DISPLAY, spec.getControls().get(1).getKind());
  }

  /** {@code null} for an absent condition, so the assertion above reads as "always shown". */
  private static Object nullIfAbsent(IExpr condition) {
    return condition.isPresent() ? condition : null;
  }

  @Test
  public void testBodyStaysUnevaluated() {
    // Manipulate holds its arguments: the body must arrive with the control variable still free
    ManipulateSpec spec = parse("Manipulate(Factor(x^n + 1), {n, 1, 20, 1})");
    assertNotNull(spec);
    assertTrue(spec.getBody().isAST(S.Factor));
  }

  @Test
  public void testLocatorNamedPositionally() {
    // Write the control head as a positional argument: {{p, {0, 0}}, Locator}
    ManipulateControl control = singleControl("Manipulate(p, {{p, {0.25, 0.75}}, Locator})");
    assertEquals(ManipulateControl.LOCATOR, control.getKind());
    assertTrue(control.hasPointsForTest());
  }

  @Test
  public void testLocatorWithSeveralPoints() {
    ManipulateControl control =
        singleControl("Manipulate(p, {{p, {{0, 0}, {1, 1}, {2, 0}}}, Locator})");
    assertEquals(ManipulateControl.LOCATOR, control.getKind());
    assertEquals(3, control.toJSON(mapper()).get("value").size());
  }

  @Test
  public void testLocatorDefaultsToTheMiddleOfItsRectangle() {
    // no initial position: one point, in the middle of the unit square
    ManipulateControl control = singleControl("Manipulate(p, {p, ControlType -> Locator})");
    assertEquals(ManipulateControl.LOCATOR, control.getKind());
    assertEquals(1, control.toJSON(mapper()).get("value").size());
    assertEquals(0.5, control.toJSON(mapper()).get("value").get(0).get(0).asDouble(), 1.0e-9);
  }

  @Test
  public void testLocatorRectangle() {
    ManipulateControl control =
        singleControl("Manipulate(p, {{p, {1, 2}}, {0, 0}, {10, 20}, Locator})");
    assertEquals(ManipulateControl.LOCATOR, control.getKind());
    assertEquals(10.0, control.toJSON(mapper()).get("max").asDouble(), 1.0e-9);
    assertEquals(20.0, control.toJSON(mapper()).get("maxY").asDouble(), 1.0e-9);
  }

  @Test
  public void testLocatorSinglePointBindsAPointNotAListOfOne() {
    // {{p, {1, 1}}, Locator} has to bind p to the point itself, so that a body such as
    // Line[{{0, 0}, p}] is well formed; binding {{1, 1}} produced a degenerate line
    ManipulateControl control = singleControl("Manipulate(p, {{p, {1, 1}}, Locator})");
    assertEquals(ManipulateControl.LOCATOR, control.getKind());
    assertTrue(control.isSinglePoint());
  }

  @Test
  public void testLocatorPointListBindsAList() {
    ManipulateControl control = singleControl("Manipulate(p, {{p, {{0, 0}, {1, 1}}}, Locator})");
    assertEquals(ManipulateControl.LOCATOR, control.getKind());
    assertTrue(!control.isSinglePoint());
  }

  @Test
  public void testLocatorDefaultBoxGrowsForAPointOnItsEdge() {
    // the point sits on the edge of the fallback unit square, so the box is widened and it can
    // be dragged in every direction
    ManipulateControl control = singleControl("Manipulate(p, {{p, {1, 1}}, Locator})");
    assertEquals(1.5, control.toJSON(mapper()).get("max").asDouble(), 1.0e-9);
    assertEquals(1.5, control.toJSON(mapper()).get("maxY").asDouble(), 1.0e-9);
  }

  @Test
  public void testLocatorDefaultBoxIsKeptForAPointInside() {
    ManipulateControl control = singleControl("Manipulate(p, {{p, {0.5, 0.5}}, Locator})");
    assertEquals(0.0, control.toJSON(mapper()).get("min").asDouble(), 1.0e-9);
    assertEquals(1.0, control.toJSON(mapper()).get("max").asDouble(), 1.0e-9);
  }

  @Test
  public void testLocatorExplicitRectangleIsNotGrown() {
    ManipulateControl control =
        singleControl("Manipulate(p, {{p, {1, 1}}, {0, 0}, {10, 10}, Locator})");
    assertEquals(0.0, control.toJSON(mapper()).get("min").asDouble(), 1.0e-9);
    assertEquals(10.0, control.toJSON(mapper()).get("max").asDouble(), 1.0e-9);
  }

  @Test
  public void testEnabledConditionIsHeld() {
    ManipulateSpec spec =
        parse("Manipulate(a + b, {on, {True, False}}, {b, 0, 10, 1, Enabled -> on})");
    assertNotNull(spec);
    ManipulateControl b = spec.controlNamed("b");
    assertNotNull(b);
    // held so that it can be resolved against the live control values on every evaluation
    assertTrue(b.getEnabledCondition().isPresent());
    assertTrue(!spec.controlNamed("on").getEnabledCondition().isPresent());
  }

  @Test
  public void testNotAManipulate() {
    assertNull(parse("Plot(Sin(x), {x, 0, 1})"));
  }

  @Test
  public void testManipulateWithoutAControl() {
    assertNull(parse("Manipulate(x)"));
  }
}
