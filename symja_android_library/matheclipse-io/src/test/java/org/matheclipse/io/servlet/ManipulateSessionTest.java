package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.manipulate.ManipulateSpec;
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
