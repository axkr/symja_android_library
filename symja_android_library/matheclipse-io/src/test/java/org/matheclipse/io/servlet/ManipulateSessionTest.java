package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
 * One frame of a <code>Manipulate</code> widget as the servlet servers render it: the body, then the
 * read-out rows beside the controls.
 *
 * <p>
 * The example is Woxi #792's: a slider laid out in a <code>Row</code> with its own read-out, and a
 * styled read-out of a variable the body writes, declared with <code>ControlType -&gt; None</code>.
 * The read-outs have to show the values, not the names of the variables. This class reads it in
 * Symja's syntax, as <code>ServletServer</code> does; {@link ManipulateSessionMathematicaSyntaxTest}
 * reads it as <code>MMAServletServer</code> does.
 */
public class ManipulateSessionTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
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
   * Woxi #840: a Demonstration-style widget that plots the planets from
   * <code>AstronomicalData</code> at a date two sliders build with <code>DatePlus</code>. The
   * planets have to move when either slider does, and the label has to name the date they are drawn
   * for.
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
}
