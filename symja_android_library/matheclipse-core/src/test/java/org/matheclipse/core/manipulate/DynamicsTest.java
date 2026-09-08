package org.matheclipse.core.manipulate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** What <code>Dynamic</code> means: what it shows, what it writes, and what it watches. */
public class DynamicsTest {

  private EvalEngine engine;

  @BeforeAll
  public static void setUpClass() {
    F.initSymja();
  }

  @BeforeEach
  public void setUp() {
    engine = new EvalEngine(true);
    EvalEngine.set(engine);
  }

  private IExpr eval(String input) {
    return engine.evaluate(engine.parse(input));
  }

  /** An expression as written, with nothing evaluated - which is how a front end receives one. */
  private IExpr heldExpr(String input) {
    return engine.evaluate(engine.parse("Hold(" + input + ")")).first();
  }

  private IAST held(String input) {
    return (IAST) heldExpr(input);
  }

  // ---------------------------------------------------------------- the head

  @Test
  public void testDynamicHoldsItsFirstArgumentOnly() {
    // HoldFirst rather than HoldAll: Dynamic[x] has to keep the symbol a control writes to,
    // while the setter of Dynamic[x, f] is an ordinary value
    assertEquals("{HoldFirst,Protected,ReadProtected}", eval("Attributes(Dynamic)").toString());
  }

  @Test
  public void testDynamicStaysUnevaluated() {
    // there is no front end here, so the expression is its own result
    assertEquals("Dynamic(1+1)", eval("Dynamic(1+1)").toString());
    eval("x = 5");
    assertEquals("Dynamic(x)", eval("Dynamic(x)").toString());
  }

  @Test
  public void testRefreshIsItsExpression() {
    // Refresh only says when to look again; what it stands for is always the expression
    assertEquals("3", eval("Refresh(1+2, UpdateInterval -> 1)").toString());
  }

  // ---------------------------------------------------------------- display

  @Test
  public void testResolveShowsTheCurrentValue() {
    eval("x = 5");
    assertEquals("5", Dynamics.resolve(held("Dynamic(x)"), engine).toString());
  }

  @Test
  public void testResolveReachesInsideAnExpression() {
    eval("moves = 3");
    IExpr resolved = Dynamics.resolve(held("Row({\"moves: \", Dynamic(moves)})"), engine);
    // OutputForm would place the elements side by side - check the structure instead
    assertEquals("Row({\"moves: \",3})", IStringX.inputForm(resolved));
  }

  @Test
  public void testResolveLeavesAControlAlone() {
    // resolving Slider[Dynamic[x]] to Slider[5] would turn a control into a picture of one
    eval("x = 5");
    assertEquals("Slider(Dynamic(x))",
        Dynamics.resolve(held("Slider(Dynamic(x))"), engine).toString());
  }

  @Test
  public void testResolveLeavesABrokenDynamicAsItStands() {
    // a rendering has to survive a dynamic whose body fails, and show it as itself
    IExpr broken = held("Dynamic(Throw(1))");
    assertEquals(broken.toString(), Dynamics.resolve(broken, engine).toString());
  }

  @Test
  public void testReleaseTakesOffOneWrapper() {
    assertEquals("x", Dynamics.release(held("Dynamic(x)")).toString());
    // anything that is not a Dynamic is left alone
    assertEquals("x", Dynamics.release(heldExpr("x")).toString());
  }

  // ---------------------------------------------------------------- releasing

  @Test
  public void testReleaseAllClearsAnOptionPosition() {
    // the option is read while the picture is drawn, so the wrapper has to be gone by then
    assertEquals("Plot3D(x,ViewPoint->vp)",
        Dynamics.releaseAll(held("Plot3D(x, ViewPoint -> Dynamic(vp))")).toString());
  }

  @Test
  public void testReleaseAllClearsAPrimitiveArgument() {
    assertEquals("Line({p,q})", Dynamics.releaseAll(held("Line(Dynamic({p, q}))")).toString());
  }

  @Test
  public void testReleaseAllKeepsAControlsDynamic() {
    // Point[Dynamic[p]] is draggable and Dynamic[Point[p]] is not; the wrapper is the difference
    assertEquals("Slider(Dynamic(x))", Dynamics.releaseAll(held("Slider(Dynamic(x))")).toString());
    assertEquals("Column({a,Slider(Dynamic(a))})",
        Dynamics.releaseAll(held("Column({Dynamic(a), Slider(Dynamic(a))})")).toString());
  }

  @Test
  public void testReleaseAllDropsTheSetterWithTheWrapper() {
    // the setter describes editing, which a display has no use for
    assertEquals("Row({x})", IStringX.inputForm(Dynamics.releaseAll(held("Row({Dynamic(x, f)})"))));
  }

  // ---------------------------------------------------------------- editing

  @Test
  public void testAssignWritesToTheTarget() {
    assertEquals("9", Dynamics.assign(held("Dynamic(y)"), F.ZZ(9), engine).toString());
    assertEquals("9", eval("y").toString());
  }

  @Test
  public void testAssignReportsWhatTheSetterActuallyStored() {
    // Dynamic[x, f] runs f instead of assigning, and f may clamp, round or refuse the value.
    // The control has to follow what was stored, not what it sent.
    IAST clamped = held("Dynamic(x, (x = Max(0, #1))&)");
    assertEquals("0", Dynamics.assign(clamped, F.ZZ(-3), engine).toString());
    assertEquals("7", Dynamics.assign(clamped, F.ZZ(7), engine).toString());
    assertEquals("7", eval("x").toString());
  }

  @Test
  public void testNoneIsReadOnly() {
    eval("y = 1");
    IAST readOnly = held("Dynamic(y, None)");
    assertFalse(Dynamics.isSettable(readOnly));
    assertFalse(Dynamics.assign(readOnly, F.ZZ(2), engine).isPresent());
    assertEquals("1", eval("y").toString());
  }

  @Test
  public void testTheThreeInteractionFunctions() {
    IAST three = held("Dynamic(z, {f1, f2, f3})");
    assertEquals("f1", Dynamics.startSetter(three).toString());
    assertEquals("f2", Dynamics.setter(three).toString());
    assertEquals("f3", Dynamics.endSetter(three).toString());

    // {f, fend} has no start function
    IAST two = held("Dynamic(z, {g1, g2})");
    assertFalse(Dynamics.startSetter(two).isPresent());
    assertEquals("g1", Dynamics.setter(two).toString());
    assertEquals("g2", Dynamics.endSetter(two).toString());

    // a lone function runs during the interaction and nowhere else
    IAST one = held("Dynamic(z, h)");
    assertFalse(Dynamics.startSetter(one).isPresent());
    assertEquals("h", Dynamics.setter(one).toString());
    assertFalse(Dynamics.endSetter(one).isPresent());
  }

  // ---------------------------------------------------------------- tracking

  @Test
  public void testTrackedSymbolsDefaultToEverythingMentioned() {
    Set<String> tracked = Dynamics.trackedSymbols(held("Dynamic(Sin(a) + b)"));
    assertTrue(tracked.contains("a"));
    assertTrue(tracked.contains("b"));
    // Sin is a builtin: nothing can assign to it, so watching it would only cause needless work
    assertFalse(tracked.contains("Sin"));
  }

  @Test
  public void testTrackedSymbolsOptionNamesThem() {
    Set<String> tracked = Dynamics.trackedSymbols(held("Dynamic(a + b, TrackedSymbols :> {a})"));
    assertEquals(1, tracked.size());
    assertTrue(tracked.contains("a"));
  }

  @Test
  public void testUpdateIntervalAndUnknownOptions() {
    IAST dynamic = held("Dynamic(a, UpdateInterval -> 0.5)");
    assertEquals(0.5, Dynamics.updateInterval(dynamic), 1.0e-9);
    assertTrue(Dynamics.unknownOptions(dynamic).isEmpty());

    // the default is to update only when something changes
    assertTrue(Double.isInfinite(Dynamics.updateInterval(held("Dynamic(a)"))));
    // the relaxed syntax this engine uses lowercases symbol names, so the option is reported
    // under the name it actually parsed to
    assertEquals("[whatever]",
        Dynamics.unknownOptions(held("Dynamic(a, Whatever -> 1)")).toString());
  }

  // ---------------------------------------------------------------- controls

  @Test
  public void testControlHeadsAreRecognised() {
    assertTrue(Dynamics.isDynamicControl(held("Slider(Dynamic(x))")));
    assertTrue(Dynamics.isDynamicControl(held("Checkbox(Dynamic(b))")));
    // a Dynamic around a control is a picture of one, not a control
    assertFalse(Dynamics.isDynamicControl(held("Dynamic(Slider(x))")));
    assertFalse(Dynamics.isDynamicControl(held("Sin(Dynamic(x))")));
  }

  @Test
  public void testSliderControlObject() {
    ManipulateControl control =
        ControlObject.parse(held("Slider(Dynamic(x), {0, 10, 0.5})"), "$c0", F.num(2.5));
    assertNotNull(control);
    assertEquals(ManipulateControl.SLIDER, control.getKind());
    assertEquals("$c0", control.getName());
    assertEquals(0.5, control.effectiveStep(), 1.0e-9);
    assertEquals(Double.valueOf(2.5), control.initialValue());
    // the write target travels with the control, because it is the only record of it
    assertEquals("Dynamic(x)", control.getDynamic().toString());
  }

  @Test
  public void testSliderWithoutARangeIsTheUnitInterval() {
    ManipulateControl control = ControlObject.parse(held("Slider(Dynamic(x))"), "$c0", F.C0);
    assertNotNull(control);
    assertEquals(0.01, control.effectiveStep(), 1.0e-9);
  }

  @Test
  public void testDiscreteControlObjectPicksOutTheCurrentValue() {
    ManipulateControl control = ControlObject.parse(
        held("PopupMenu(Dynamic(v), {\"one\", \"two\", \"three\"})"), "$c0", F.stringx("two"));
    assertNotNull(control);
    assertEquals(ManipulateControl.DISCRETE, control.getKind());
    assertEquals(Integer.valueOf(1), control.initialValue());
  }

  @Test
  public void testTogglerBarBindsTheListOfWhatIsOn() {
    ManipulateControl control =
        ControlObject.parse(held("TogglerBar(Dynamic(v), {1, 2, 3})"), "$c0", F.list(F.C1, F.C3));
    assertNotNull(control);
    assertEquals(ManipulateControl.MULTI, control.getKind());
    assertEquals("[0, 2]", control.initialValue().toString());
  }

  @Test
  public void testColorControlObjectIsAnHtmlColour() {
    ManipulateControl control =
        ControlObject.parse(held("ColorSetter(Dynamic(c))"), "$c0", F.RGBColor(1.0, 0.5, 0.0));
    assertNotNull(control);
    assertEquals(ManipulateControl.COLOR, control.getKind());
    assertEquals("#ff8000", control.initialValue());
  }

  @Test
  public void testNoneMakesAControlReadOnly() {
    ManipulateControl control = ControlObject.parse(held("Slider(Dynamic(x, None))"), "$c0", F.C0);
    assertNotNull(control);
    assertTrue(control.isReadOnly());
  }

  @Test
  public void testAnUnknownControlHeadIsNotOne() {
    assertNull(ControlObject.parse(held("Sin(Dynamic(x))"), "$c0", F.C0));
  }

  // ---------------------------------------------------------------- scanning

  @Test
  public void testInteractionsNumberControlsAndButtons() {
    eval("x = 0.25");
    Interactions interactions = new Interactions(engine);
    IExpr rewritten = interactions.rewrite(
        held("Column({Slider(Dynamic(x)), Button(\"reset\", x = 0), Slider(Dynamic(y))})"));

    // nothing that can run code is left in the rendering; only positions travel to the browser
    assertEquals("Column({Slider(Dynamic(0)),Button(reset,0),Slider(Dynamic(1))})",
        rewritten.toString());
    assertEquals(2, interactions.getControls().size());
    assertEquals(1, interactions.getActions().size());
    assertEquals("x=0", interactions.getActions().get(0).toString());
    assertEquals("Dynamic(x)", interactions.getControls().get(0).getDynamic().toString());
    assertEquals("Dynamic(y)", interactions.getControls().get(1).getDynamic().toString());
  }

  @Test
  public void testInteractionsReadAControlAgainstAScope() {
    eval("k = 1");
    // the frame was rendered with k bound to 8, so that is where the control has to start -
    // not at the session's own k
    Interactions scoped = new Interactions(engine, heldExpr("{k = 8}"));
    scoped.rewrite(held("Slider(Dynamic(k), {0, 10, 1})"));
    assertEquals(Double.valueOf(8.0), scoped.getControls().get(0).initialValue());

    Interactions unscoped = new Interactions(engine);
    unscoped.rewrite(held("Slider(Dynamic(k), {0, 10, 1})"));
    assertEquals(Double.valueOf(1.0), unscoped.getControls().get(0).initialValue());
  }

  // ---------------------------------------------------------- LocatorPane

  /** The graphic and the control a pane is rewritten into, for the assertions below. */
  private Interactions scanPane(String input) {
    Interactions interactions = new Interactions(engine);
    interactions.rewrite(engine.evaluate(Dynamics.releaseAll(held(input))));
    return interactions;
  }

  /**
   * The rendering of a pane with its spacing taken out - the printer breaks a long expression over
   * several lines, which is nothing to do with what was built.
   */
  private String renderedPane(String input) {
    Interactions interactions = new Interactions(engine);
    return interactions.rewrite(engine.evaluate(Dynamics.releaseAll(held(input)))).toString()
        .replaceAll("\\s+", "");
  }

  @Test
  public void testLocatorPaneKeepsItsDynamicThroughRelease() {
    // the pane writes through this Dynamic, so it has to survive the pass that takes the display
    // wrappers off - including in the "compute the point first" form
    assertEquals("LocatorPane(Dynamic(p),Graphics({Point(p)}))", Dynamics
        .releaseAll(held("LocatorPane(Dynamic(p), Graphics({Point(Dynamic(p))}))")).toString());
    assertEquals("LocatorPane(q={0,0};Dynamic(q,f),Graphics({Point(q)}))",
        Dynamics.releaseAll(held("LocatorPane(q = {0,0}; Dynamic(q, f), Graphics({Point(q)}))"))
            .toString());
  }

  @Test
  public void testLocatorPaneDrawsAMarkerAndOffersAControl() {
    eval("p = {0.3, 0.7}");
    String rendered = renderedPane(
        "LocatorPane(Dynamic(p), Graphics({Line({{0,0},{1,1}})}, PlotRange -> {{0,1},{0,1}}))");
    // the picture and the control that moves it are stacked, both on screen at once
    assertTrue(rendered.startsWith("Column({Graphics("), rendered);
    assertTrue(rendered.endsWith("LocatorPane(Dynamic(0))})"), rendered);
    // a marker at the locator, in the body's own coordinates
    assertTrue(rendered.contains("Circle({0.3,0.7}"), rendered);
    // and the body's own contents and options are kept
    assertTrue(rendered.contains("Line({{0,0},{1,1}})"), rendered);
    assertTrue(rendered.contains("PlotRange->{{0,1},{0,1}}"), rendered);

    Interactions interactions = scanPane(
        "LocatorPane(Dynamic(p), Graphics({Line({{0,0},{1,1}})}, PlotRange -> {{0,1},{0,1}}))");
    assertEquals(1, interactions.getControls().size());
    ManipulateControl control = interactions.getControls().get(0);
    assertEquals(ManipulateControl.LOCATOR, control.getKind());
    assertEquals("Dynamic(p)", control.getDynamic().toString());
    // one point in, one point out: the binding mirrors the shape the user wrote
    assertTrue(control.isSinglePoint());
  }

  @Test
  public void testLocatorPaneWithSeveralPoints() {
    eval("pts = {{0.2,0.2},{0.8,0.2},{0.5,0.9}}");
    String rendered = renderedPane(
        "LocatorPane(Dynamic(pts), Graphics({Polygon(pts)}, PlotRange -> {{0,1},{0,1}}))");
    assertTrue(rendered.contains("Circle({0.2,0.2}"), rendered);
    assertTrue(rendered.contains("Circle({0.8,0.2}"), rendered);
    assertTrue(rendered.contains("Circle({0.5,0.9}"), rendered);

    ManipulateControl control =
        scanPane("LocatorPane(Dynamic(pts), Graphics({Polygon(pts)}, PlotRange -> {{0,1},{0,1}}))")
            .getControls().get(0);
    assertFalse(control.isSinglePoint());
    assertEquals("[[0.2,0.2],[0.8,0.2],[0.5,0.9]]",
        control.toJSON(new ObjectMapper()).get("value").toString());
  }

  @Test
  public void testLocatorPaneTakesItsBoxFromThePlotRange() {
    eval("p = {5.0, 5.0}");
    ManipulateControl control =
        scanPane("LocatorPane(Dynamic(p), Graphics({Point(p)}, PlotRange -> {{0,10},{0,20}}))")
            .getControls().get(0);
    // a point outside the plot range could not be seen, so that is the box it moves in
    assertEquals("{\"kind\":\"locator\",\"name\":\"$c0\",\"label\":\"\",\"enabled\":true,"
        + "\"value\":[[5.0,5.0]],\"min\":0.0,\"max\":10.0,\"minY\":0.0,\"maxY\":20.0,"
        + "\"autoCreate\":false}", control.toJSON(new ObjectMapper()).toString());
  }

  @Test
  public void testLocatorPaneAppearanceNoneDrawsNoMarker() {
    eval("p = {0.3, 0.7}");
    String rendered = renderedPane("LocatorPane(Dynamic(p), Graphics({Point(p)},"
        + " PlotRange -> {{0,1},{0,1}}), Appearance -> None)");
    assertFalse(rendered.contains("Circle"), rendered);
    // the control is still there: only the marker was asked to go away
    assertTrue(rendered.contains("LocatorPane(Dynamic(0))"), rendered);
  }

  @Test
  public void testLocatorPaneComputesItsPointFirst() {
    // the "p = f[a]; Dynamic[p, setter]" form: the statements run for their effect and the
    // Dynamic at the end is still what the control writes to
    String rendered = renderedPane("LocatorPane(q = {0.4, 0.4}; Dynamic(q),"
        + " Graphics({Point(q)}, PlotRange -> {{0,1},{0,1}}))");
    assertTrue(rendered.contains("Circle({0.4,0.4}"), rendered);
    assertEquals("0.4", eval("q[[1]]").toString());
  }

  @Test
  public void testLocatorPaneOverSomethingThatIsNotAPictureIsLeftAlone() {
    // there would be nothing to put markers on
    assertEquals("LocatorPane(Dynamic(p),Sin(x))", renderedPane("LocatorPane(Dynamic(p), Sin(x))"));
  }

  // ---------------------------------------------------------- PaneSelector

  @Test
  public void testPaneSelectorShowsTheMatchingPane() {
    eval("sel = 2");
    assertEquals("two",
        Dynamics.resolve(held("PaneSelector({1 -> \"one\", 2 -> \"two\"}, Dynamic(sel))"), engine)
            .toString());
  }

  @Test
  public void testPaneSelectorWithNoMatchShowsNothingOrTheDefault() {
    eval("sel = 9");
    assertEquals("",
        Dynamics.resolve(held("PaneSelector({1 -> \"one\"}, Dynamic(sel))"), engine).toString());
    assertEquals("fallback",
        Dynamics.resolve(held("PaneSelector({1 -> \"one\"}, Dynamic(sel), \"fallback\")"), engine)
            .toString());
  }

  @Test
  public void testPaneSelectorInsideALayout() {
    eval("sel = 1");
    assertEquals("Column({aaa,bbb})", Dynamics
        .resolve(held("Column({PaneSelector({1 -> aaa, 2 -> ccc}, Dynamic(sel)), bbb})"), engine)
        .toString());
  }

  @Test
  public void testAnExpressionWithNothingInteractiveIsUntouched() {
    Interactions interactions = new Interactions(engine);
    IExpr expr = held("Sin(x) + 1");
    assertTrue(expr == interactions.rewrite(expr));
    assertTrue(interactions.isEmpty());
  }

  /**
   * <code>FileNameSetter[Dynamic[f]]</code> is a control object like any other, so that a browser
   * can draw it. What its value means differs by where the kernel runs - a path locally, a name in
   * the session's directory over HTTP - but the variable holds a name <code>Import</code> can open
   * either way, which is the part a notebook is written against.
   */
  @Test
  public void testFileNameSetterIsAControlObject() {
    IExpr expr = heldExpr("FileNameSetter(Dynamic(f))");
    assertTrue(Dynamics.isDynamicControl(expr));

    ManipulateControl control = ControlObject.parse(expr, "$c0", F.NIL);
    assertNotNull(control);
    assertEquals(ManipulateControl.FILE, control.getKind());
    assertEquals("Open", control.getFileDialog());
    assertEquals("", control.initialValue());
  }

  @Test
  public void testFileNameSetterTakesItsDialogTypeAndItsCurrentName() {
    ManipulateControl control = ControlObject
        .parse(heldExpr("FileNameSetter(Dynamic(f), \"OpenList\")"), "$c0", F.$str("data.csv"));
    assertNotNull(control);
    assertEquals("OpenList", control.getFileDialog());
    assertEquals("data.csv", control.initialValue());

    ObjectNode json = control.toJSON(new ObjectMapper());
    assertEquals("file", json.get("kind").asText());
    assertEquals("OpenList", json.get("dialog").asText());
    assertEquals("data.csv", json.get("value").asText());
  }

  /**
   * A name that is not a string is no name at all, so the control starts empty rather than wrong.
   */
  @Test
  public void testFileNameSetterIgnoresANonStringValue() {
    ManipulateControl control =
        ControlObject.parse(heldExpr("FileNameSetter(Dynamic(f))"), "$c0", F.C7);
    assertNotNull(control);
    assertEquals("", control.initialValue());
  }

}
