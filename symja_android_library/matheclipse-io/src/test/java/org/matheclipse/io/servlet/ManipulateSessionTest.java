package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.WebGLGraphics3D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.manipulate.ManipulateControl;
import org.matheclipse.core.manipulate.ManipulateSpec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * One frame of a <code>Manipulate</code> widget as the servlet servers render it: the body, then
 * the read-out rows beside the controls.
 *
 * <p>
 * The example is a slider laid out in a <code>Row</code> with its own read-out, and a styled
 * read-out of a variable the body writes, declared with <code>ControlType -&gt; None</code>. The
 * read-outs have to show the values, not the names of the variables. This class reads it in Symja's
 * syntax, as <code>ServletServer</code> does; {@link ManipulateSessionMathematicaSyntaxTest} reads
 * it as <code>MMAServletServer</code> does.
 */
public class ManipulateSessionTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeAll
  public static void beforeAll() {
    F.initSymja();
    // the solar system widget calls AstronomicalData, which the servlets register through IOInit
    org.matheclipse.astro.AstroInit.init();
  }

  /** Parse and evaluate the way a servlet server does, and return the widget it would store. */
  static ManipulateSpec widget(EvalEngine engine, String input) {
    EvalEngine.set(engine);
    ManipulateSpec spec = ManipulateSpec.parse(engine.evaluate(engine.parse(input)), engine);
    assertNotNull(spec, "not a widget: " + input);
    return spec;
  }

  /** The read-out rows of one frame, rendered with the slider at <code>target</code>. */
  static String displays(EvalEngine engine, ManipulateSpec spec, double target) throws Exception {
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("target", target);
    ManipulateSession.Frame frame = ManipulateSession.evaluateFrame(engine, spec, bindings);
    ObjectNode displays = ManipulateSession.renderDisplays(engine, spec, frame.locals,
        new StringBuilderWriter(), new StringBuilderWriter());
    assertNotNull(displays, "the widget has read-out rows");
    return displays.toString();
  }

  static void checkReadOuts(EvalEngine engine, String input) throws Exception {
    ManipulateSpec spec = widget(engine, input);
    // the Row's slider is a control of its own, and the Row and the Style are the read-outs
    assertEquals("target", spec.getControls().get(0).getName());

    String first = displays(engine, spec, 1);
    assertTrue(first.contains("not yet"), "the body's status reaches its read-out: " + first);
    assertFalse(first.contains("status"), "no variable name in the read-outs: " + first);
    assertFalse(first.contains("target"), "no variable name in the read-outs: " + first);

    String reached = displays(engine, spec, 9);
    assertTrue(reached.contains("reached") && !reached.contains("not yet"),
        "moving the slider past 8 changes the read-out: " + reached);
  }

  private static EvalEngine engine() {
    return new EvalEngine("manipulate-test", 256, System.out, true);
  }


  /**
   * The scene one frame of the solar system widget draws, with the sliders at the given values.
   *
   * <p>
   * The widget's <code>Initialization</code> defines the helpers its body calls, and a servlet runs
   * that once before the first frame, which is what {@link ManipulateSession#create} does; here it
   * is run by hand so that the frame itself can be looked at without a session around it.
   */
  static JsonNode solarSystemScene(EvalEngine engine, ManipulateSpec spec, int baseYear,
      double dayFrac) throws Exception {
    EvalEngine.set(engine);
    IExpr initialization = spec.getInitialization();
    if (initialization.isPresent()) {
      engine.evaluate(initialization);
    }
    // Symja's relaxed syntax lower-cases identifiers and the Wolfram one does not, so the values
    // are keyed by the names this spec actually carries rather than by the ones written above
    ObjectNode bindings = MAPPER.createObjectNode();
    for (ManipulateControl control : spec.getControls()) {
      String name = control.getName();
      if ("dayFrac".equalsIgnoreCase(name)) {
        bindings.put(name, dayFrac);
      } else if ("baseYear".equalsIgnoreCase(name)) {
        bindings.put(name, baseYear);
      } else if ("zoom".equalsIgnoreCase(name)) {
        bindings.put(name, 1);
      }
    }
    IExpr body = ManipulateSession.evaluateFrame(engine, spec, bindings).result;
    assertTrue(body.isAST(S.Graphics3D), "the body must draw a scene: " + body);
    return MAPPER.readTree(WebGLGraphics3D.generateJSON((IAST) body));
  }

  /** The centres of every sphere in a scene, in the order they were drawn. */
  static String sphereCentres(JsonNode scene) {
    StringBuilder centres = new StringBuilder();
    for (JsonNode element : scene.get("elements")) {
      if ("Sphere".equals(element.get("type").asText())) {
        centres.append(element.get("centers").toString());
      }
    }
    return centres.toString();
  }

  /**
   * A Demonstration-style widget that plots the planets from <code>AstronomicalData</code> at a
   * date two sliders build with <code>DatePlus</code>. The planets have to move when either slider
   * does, and the label has to name the date they are drawn for.
   */
  static void checkSolarSystem(EvalEngine engine, String input) throws Exception {
    ManipulateSpec spec = widget(engine, input);
    assertEquals("[dayfrac, baseyear, zoom]", spec.getControls().stream()
        .map(c -> c.getName().toLowerCase(Locale.US)).collect(Collectors.toList()).toString());

    JsonNode scene = solarSystemScene(engine, spec, 2020, 0);
    int spheres = 0;
    for (JsonNode element : scene.get("elements")) {
      if ("Sphere".equals(element.get("type").asText())) {
        spheres++;
        assertTrue(element.has("radiusScaled"),
            "a Scaled radius stays scaled all the way to the scene: " + element);
      }
    }
    assertEquals(9, spheres, "eight planets and the Sun");
    assertEquals("January 2020", scene.get("plotLabel").asText());

    // a century on, every planet is somewhere else and the label follows the date
    JsonNode later = solarSystemScene(engine, spec, 2120, 0);
    assertEquals("January 2120", later.get("plotLabel").asText());
    assertNotEquals(sphereCentres(scene), sphereCentres(later));

    // and half a year moves them too, which needs a fractional year to survive DatePlus
    JsonNode halfYear = solarSystemScene(engine, spec, 2020, 0.5);
    assertEquals("July 2020", halfYear.get("plotLabel").asText());
    assertNotEquals(sphereCentres(scene), sphereCentres(halfYear));
  }

  @Test
  public void testSolarSystemWidgetInSymjaSyntax() throws Exception {
    checkSolarSystem(engine(), "Manipulate(Graphics3D({Sphere(#1, Scaled(0.015)) & /@"
        + " (orbitPos(dateAt(baseYear, dayFrac), #1) & /@ Range(8)),"
        + " {Yellow, Sphere({0, 0, 0}, Scaled(0.02))}},"
        + " PlotRange -> Exp(4*(zoom - 1)), ImageSize -> {360, 360},"
        + " PlotLabel -> DateString(dateAt(baseYear, dayFrac), {\"MonthName\", \" \", \"Year\"}),"
        + " SphericalRegion -> True),"
        + " {{dayFrac, 0, \"day offset\"}, 0, 1, ControlType -> Slider},"
        + " {{baseYear, 2020, \"year\"}, 2020, 2170, 1, ControlType -> Slider},"
        + " {{zoom, 1, \"zoom\"}, 0, 1},"
        + " SaveDefinitions -> True, SynchronousUpdating -> False,"
        + " Initialization :> (dateAt(y_, d_) := DatePlus({y}, {d, \"Year\"});"
        + " orbitPos(t_, k_) := AstronomicalData(AstronomicalData(k),"
        + " {\"Position\", t})/(7*10^12);))");
  }

  @Test
  public void testReadOutsShowTheValues() throws Exception {
    checkReadOuts(engine(),
        "Manipulate(status = If(target >= 8, \"reached\", \"not yet\"); target,"
            + " Row({Control({{target, 1, \"target\"}, 0, 10}), Style(\" \"),"
            + " Style(Dynamic(target))}),"
            + " Style(Dynamic(status), Bold, Red), {{status, \"\", \"\"}, ControlType -> None})");
  }

  /** Reading the variables back does not change what the body itself shows. */
  @Test
  public void testTheBodyResultIsUnchanged() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine, "Manipulate({u, u^2}, {u, 0, 10, 1})");
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("u", 3);
    assertEquals("{3,9}",
        ManipulateSession.evaluateFrame(engine, spec, bindings).result.toString());
  }

  /** A slider bound written as Dynamic follows the control it is written with. */
  @Test
  public void testDynamicBoundFollowsTheOtherSlider() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine,
        "Manipulate({a, b}, {{a, 1}, 1, 5, 1}, {{b, 2}, -1, Dynamic(4 * a)}, {c, 0, 1})");
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("a", 3);
    bindings.put("b", 2);
    bindings.put("c", 0.5);
    assertEquals("[null,[-1.0,12.0],null]",
        ManipulateSession.resolveRanges(engine, spec, bindings).toString());
    // a bound below the other pins the slider
    ManipulateSpec pinned = widget(engine, "Manipulate(b, {{a, 1}, 1, 5, 1}, {{b, 0}, 0, Dynamic(a - 3)})");
    assertEquals("[null,[0.0,0.0]]",
        ManipulateSession.resolveRanges(engine, pinned, bindings).toString());
    // no moving bound at all: nothing to send
    ManipulateSpec fixed = widget(engine, "Manipulate(a, {a, 0, 2})");
    assertNull(ManipulateSession.resolveRanges(engine, fixed, bindings));
  }

  /** The body of one frame with the given slider value, as a string. */
  private static String frame(EvalEngine engine, ManipulateSpec spec, String name, int value) {
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put(name, value);
    return ManipulateSession.evaluateFrame(engine, spec, bindings).result.toString();
  }

  /** A hidden variable the body writes keeps its value from one frame to the next. */
  @Test
  public void testHiddenVariableKeepsItsValue() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine,
        "Manipulate(calls = calls + 1; {w, calls}, {w, 0, 4, 1}, {{calls, 0}, ControlType -> None})");
    assertEquals("{2,1}", frame(engine, spec, "w", 2));
    assertEquals("{3,2}", frame(engine, spec, "w", 3));
    assertEquals("{3,3}", frame(engine, spec, "w", 3));
  }

  /** What a button of the body writes into a hidden variable is there in the next frame. */
  @Test
  public void testBodyButtonWritesAHiddenVariable() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine, "Manipulate({Button(\"add\", acc = acc + w), acc},"
        + " {w, 1, 6, 1}, {{acc, 10}, ControlType -> None})");
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("w", 4);
    ManipulateSession.Frame first = ManipulateSession.evaluateFrame(engine, spec, bindings);
    ManipulateSession.registerBodyInteractions(engine, spec, bindings, "hidden-test", first.result);
    assertNotNull(ManipulateSession.runBodyButtonAction(engine, spec, bindings, "hidden-test", 0));
    assertTrue(frame(engine, spec, "w", 4).endsWith(",14}"), frame(engine, spec, "w", 4));
  }

  /**
   * The locals of a module at the end of the body survive the frames its own buttons cause, and
   * start over when a panel control moves.
   */
  @Test
  public void testModuleLocalsBetweenFrames() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine, "Manipulate(DynamicModule({stock = 5},"
        + " {Button(\"take\", stock = stock - w), stock}), {w, 1, 3, 1})");
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("w", 2);
    ManipulateSession.Frame first = ManipulateSession.evaluateFrame(engine, spec, bindings);
    assertTrue(first.result.toString().endsWith(",5}"), first.result.toString());
    ManipulateSession.registerBodyInteractions(engine, spec, bindings, "module-test", first.result);
    ManipulateSession.runBodyButtonAction(engine, spec, bindings, "module-test", 0);
    assertTrue(frame(engine, spec, "w", 2).endsWith(",3}"), frame(engine, spec, "w", 2));
    ManipulateSession.controlsMoved(spec);
    assertTrue(frame(engine, spec, "w", 3).endsWith(",5}"), frame(engine, spec, "w", 3));
  }

  /**
   * A Refresh with an UpdateInterval gives the frame its timer, worked out with the frame's own
   * control values; the timed frames carry the hidden variables on.
   */
  @Test
  public void testRefreshUpdateInterval() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine, "Manipulate(Refresh(beats = beats + 1; {w, beats},"
        + " UpdateInterval -> If(w > 0, 1/4, Infinity)), {w, 0, 2, 1},"
        + " {{beats, 0}, ControlType -> None})");
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("w", 1);
    ManipulateSession.Frame frame = ManipulateSession.evaluateFrame(engine, spec, bindings);
    assertEquals("{1,1}", frame.result.toString());
    assertEquals(0.25, frame.updateInterval);
    frame = ManipulateSession.evaluateFrame(engine, spec, bindings);
    assertEquals("{1,2}", frame.result.toString());
    bindings.put("w", 0);
    frame = ManipulateSession.evaluateFrame(engine, spec, bindings);
    assertEquals("{0,3}", frame.result.toString());
    assertTrue(Double.isInfinite(frame.updateInterval), "no timer once the interval is Infinity");
    // a body without Refresh has no timer
    ManipulateSpec plain = widget(engine, "Manipulate(w^2, {w, 0, 2, 1})");
    assertTrue(
        Double.isInfinite(ManipulateSession.evaluateFrame(engine, plain, bindings).updateInterval));
  }

  /** A click on a ClickPane of the body applies its function to the point, for the next frame. */
  @Test
  public void testBodyClickPaneGetsThePoint() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine, "Manipulate({ClickPane(Graphics(Point(spot),"
        + " PlotRange -> 1), (spot = #) &), w, spot}, {w, 0, 2, 1},"
        + " {{spot, {0, 0}}, ControlType -> None})");
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("w", 1);
    ManipulateSession.Frame first = ManipulateSession.evaluateFrame(engine, spec, bindings);
    IExpr rewritten = ManipulateSession.registerBodyInteractions(engine, spec, bindings,
        "click-test", first.result);
    assertTrue(rewritten.toString().startsWith("{ClickPane(Graphics("), rewritten.toString());
    assertNotNull(ManipulateSession.runBodyButtonAction(engine, spec, bindings, "click-test", 0,
        ManipulateSession.pointOf("[0.5, -0.25]")));
    assertTrue(frame(engine, spec, "w", 1).endsWith(",1,{0.5,-0.25}}"),
        frame(engine, spec, "w", 1));
    assertTrue(ManipulateSession.pointOf("[\"a\", 1]").isNIL());
  }

  /** A locator the body leaves where it was is not moved, whatever number type it was sent as. */
  @Test
  public void testUnchangedLocatorIsNotMoved() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec =
        widget(engine, "Manipulate(Graphics(Point(p), PlotRange -> 2), {{p, {1, 1}}, Locator})");
    ObjectNode bindings = (ObjectNode) MAPPER.readTree("{\"p\": [[1, 1]]}");
    ManipulateSession.Frame frame = ManipulateSession.evaluateFrame(engine, spec, bindings);
    assertTrue(ManipulateSession.movedBindings(spec, bindings, frame.locals).isEmpty());
  }

  /** The locals of a hoisted module are its own, not the symbols of the same name outside. */
  @Test
  public void testModuleLocalsAreRenamed() throws Exception {
    EvalEngine engine = engine();
    EvalEngine.set(engine);
    engine.evaluate(engine.parse("stock = 100; peek() := stock"));
    ManipulateSpec spec = widget(engine, "Manipulate(DynamicModule({stock = 5},"
        + " {Button(\"take\", stock = stock - w), stock, peek()}), {w, 1, 3, 1})");
    assertTrue(frame(engine, spec, "w", 2).endsWith(",5,100}"), frame(engine, spec, "w", 2));
  }

  /** Only a Refresh the frame evaluates asks for the next frame. */
  @Test
  public void testRefreshInAnUnusedBranch() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine,
        "Manipulate(If(w > 1, Refresh(w, UpdateInterval -> 0.2), \"off\"), {w, 0, 2, 1})");
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("w", 0);
    assertTrue(Double
        .isInfinite(ManipulateSession.evaluateFrame(engine, spec, bindings).updateInterval));
    bindings.put("w", 2);
    assertEquals(0.2, ManipulateSession.evaluateFrame(engine, spec, bindings).updateInterval);
  }

  /** A control variable the body assigns moves its control. */
  @Test
  public void testBodyAssignmentMovesTheControl() throws Exception {
    EvalEngine engine = engine();
    ManipulateSpec spec = widget(engine, "Manipulate(If(w > 4, w = 4); w^2, {w, 0, 9, 1})");
    ObjectNode bindings = MAPPER.createObjectNode();
    bindings.put("w", 7);
    ManipulateSession.Frame frame = ManipulateSession.evaluateFrame(engine, spec, bindings);
    assertEquals("16", frame.result.toString());
    assertEquals(4.0,
        ManipulateSession.movedBindings(spec, bindings, frame.locals).get("w").asDouble());
    bindings.put("w", 2);
    frame = ManipulateSession.evaluateFrame(engine, spec, bindings);
    assertTrue(ManipulateSession.movedBindings(spec, bindings, frame.locals).isEmpty());
  }

  /**
   * The text a browser sends for an input field is code only where the field says it takes an
   * expression and the deployment lets it.
   */
  @Test
  public void testInputFieldText() {
    org.matheclipse.core.eval.EvalEngine engine = new org.matheclipse.core.eval.EvalEngine(false);
    org.matheclipse.core.interfaces.IAST dynamic = org.matheclipse.core.expression.F
        .unaryAST1(org.matheclipse.core.expression.S.Dynamic,
            org.matheclipse.core.expression.F.symbol("fieldValue"));
    org.matheclipse.core.interfaces.IExpr none = org.matheclipse.core.expression.F.NIL;
    org.matheclipse.core.manipulate.ManipulateControl asString =
        org.matheclipse.core.manipulate.ControlObject.parse(
            org.matheclipse.core.expression.F.binaryAST2(
                org.matheclipse.core.expression.S.InputField, dynamic,
                org.matheclipse.core.expression.S.String),
            "$c0", none);
    org.matheclipse.core.manipulate.ManipulateControl asNumber =
        org.matheclipse.core.manipulate.ControlObject.parse(
            org.matheclipse.core.expression.F.binaryAST2(
                org.matheclipse.core.expression.S.InputField, dynamic,
                org.matheclipse.core.expression.S.Number),
            "$c1", none);
    org.matheclipse.core.manipulate.ManipulateControl asExpression =
        org.matheclipse.core.manipulate.ControlObject.parse(org.matheclipse.core.expression.F
            .unaryAST1(org.matheclipse.core.expression.S.InputField, dynamic), "$c2", none);
    com.fasterxml.jackson.databind.node.JsonNodeFactory json =
        com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

    org.matheclipse.core.interfaces.IExpr text =
        ManipulateSession.valueOf(asString, json.textNode("2+3"), engine);
    assertTrue(text.isString(), "a String field keeps its text: " + text);
    assertEquals("2+3", text.toString());

    assertEquals("42",
        ManipulateSession.valueOf(asNumber, json.textNode(" 42 "), engine).toString());
    assertEquals("2.5",
        ManipulateSession.valueOf(asNumber, json.textNode("2.5"), engine).toString());
    assertTrue(ManipulateSession.valueOf(asNumber, json.textNode("2+3"), engine).isNIL(),
        "a Number field takes a number and nothing else");

    boolean expressionInput = ManipulateSession.EXPRESSION_INPUT;
    try {
      ManipulateSession.EXPRESSION_INPUT = true;
      assertTrue(ManipulateSession.valueOf(asExpression, json.textNode("2+3"), engine).isAST(),
          "an expression field reads its text as one");
      ManipulateSession.EXPRESSION_INPUT = false;
      org.matheclipse.core.interfaces.IExpr kept =
          ManipulateSession.valueOf(asExpression, json.textNode("2+3"), engine);
      assertTrue(kept.isString(), "switched off, the text stays text: " + kept);
      assertEquals("7",
          ManipulateSession.valueOf(asExpression, json.textNode("7"), engine).toString());
    } finally {
      ManipulateSession.EXPRESSION_INPUT = expressionInput;
    }
  }
}
