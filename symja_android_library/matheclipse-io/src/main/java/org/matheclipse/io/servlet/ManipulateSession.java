package org.matheclipse.io.servlet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.output.StringBuilderWriter;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.manipulate.ManipulateControl;
import org.matheclipse.core.manipulate.ManipulateSpec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The live <code>Manipulate</code> widgets of the browser session.
 *
 * <p>
 * A widget keeps its body here, on the server, and the browser only holds the control values. Every
 * time a control moves the browser posts those values back and the body is evaluated again inside a
 * {@link S#Block}, which is what makes a body of any shape work: the result travels through the
 * same renderer a plain result does.
 */
public class ManipulateSession {

  /** How many widgets one browser session keeps before the oldest is dropped. */
  private static final int MAX_WIDGETS_PER_SESSION = 32;

  /** Widgets by session id, each an access ordered map of widget id to specification. */
  private static final Map<String, Map<String, ManipulateSpec>> SESSIONS =
      new LinkedHashMap<String, Map<String, ManipulateSpec>>();

  /** Counter for the widget ids; the id only has to be unique inside one session. */
  private static long widgetCounter = 0L;

  private ManipulateSession() {}

  private static synchronized String store(String sessionID, ManipulateSpec spec) {
    Map<String, ManipulateSpec> widgets = SESSIONS.get(sessionID);
    if (widgets == null) {
      widgets = new LinkedHashMap<String, ManipulateSpec>(16, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ManipulateSpec> eldest) {
          return size() > MAX_WIDGETS_PER_SESSION;
        }
      };
      SESSIONS.put(sessionID, widgets);
    }
    String id = "mp" + (++widgetCounter);
    widgets.put(id, spec);
    return id;
  }

  static synchronized ManipulateSpec lookup(String sessionID, String id) {
    Map<String, ManipulateSpec> widgets = SESSIONS.get(sessionID);
    return widgets == null ? null : widgets.get(id);
  }

  /** Forget every widget of a session that has ended. */
  public static synchronized void remove(String sessionID) {
    SESSIONS.remove(sessionID);
  }

  /**
   * Register a new widget and render it for the first time.
   *
   * @return the JSON result for the browser, in the <code>String[]</code> shape the servlet uses
   */
  static String[] create(EvalEngine engine, ManipulateSpec spec, StringBuilderWriter outWriter,
      StringBuilderWriter errorWriter) throws java.io.IOException {
    String id = store(engine.getSessionID(), spec);

    // Initialization :> ... runs once, before the first rendering. Its definitions stay in the
    // session engine, so every later evaluation of the body still sees them.
    IExpr initialization = spec.getInitialization();
    if (initialization.isPresent()) {
      try {
        engine.evaluate(initialization);
      } catch (RuntimeException rex) {
        // an initialization that fails must not stop the widget from being shown
      }
    }

    IExpr result = evaluateBody(engine, spec, initialBindings(spec));
    String[] rendered = AJAXQueryServlet.renderResult(engine, result, outWriter, errorWriter);
    return JSONBuilder.createJSONManipulate(id, spec, rendered[1]);
  }

  /** The control values as the browser would first send them. */
  private static ObjectNode initialBindings(ManipulateSpec spec) {
    ObjectNode bindings = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    for (ManipulateControl control : spec.getControls()) {
      if (!control.bindsVariable()) {
        continue;
      }
      Object value = control.initialValue();
      String name = control.getName();
      if (value instanceof Integer) {
        bindings.put(name, ((Integer) value).intValue());
      } else if (value instanceof Boolean) {
        bindings.put(name, ((Boolean) value).booleanValue());
      } else if (value instanceof Double) {
        bindings.put(name, ((Double) value).doubleValue());
      } else if (value instanceof String) {
        bindings.put(name, (String) value);
      } else if (value instanceof double[]) {
        double[] pair = (double[]) value;
        ArrayNode node = bindings.putArray(name);
        node.add(pair[0]);
        node.add(pair[1]);
      } else if (value instanceof List) {
        ArrayNode node = bindings.putArray(name);
        for (Object point : (List<?>) value) {
          double[] xy = (double[]) point;
          ArrayNode pair = node.addArray();
          pair.add(xy[0]);
          pair.add(xy[1]);
        }
      }
    }
    return bindings;
  }

  /**
   * Evaluate the body of a widget with the control values the browser sent.
   *
   * <p>
   * The bindings are installed with {@link S#Block}, so the control variables are local to this
   * evaluation and a global symbol of the same name is left alone.
   */
  static IExpr evaluateBody(EvalEngine engine, ManipulateSpec spec, JsonNode bindings) {
    IASTAppendable locals = F.ListAlloc(spec.getControls().size());
    for (ManipulateControl control : spec.getControls()) {
      if (!control.bindsVariable()) {
        continue;
      }
      ISymbol variable = control.getVariable();
      IExpr value = valueOf(control, bindings.get(control.getName()), engine);
      if (value.isPresent()) {
        locals.append(F.Set(variable, value));
      }
    }
    IAST block = F.Block(locals, spec.getBody());
    return engine.evaluate(block);
  }

  /** Turn one control value from the browser into the expression its variable is bound to. */
  private static IExpr valueOf(ManipulateControl control, JsonNode node, EvalEngine engine) {
    String kind = control.getKind();
    if (node == null || node.isNull()) {
      IExpr initial = fromInitial(control);
      return initial;
    }
    if (ManipulateControl.DISCRETE.equals(kind)) {
      List<IExpr> values = control.getValues();
      int index = node.asInt(0);
      if (index >= 0 && index < values.size()) {
        return values.get(index);
      }
      return values.isEmpty() ? F.NIL : values.get(0);
    }
    if (ManipulateControl.CHECKBOX.equals(kind)) {
      return node.asBoolean(false) ? S.True : S.False;
    }
    if (ManipulateControl.SLIDER2D.equals(kind) || ManipulateControl.INTERVAL.equals(kind)) {
      if (node.isArray() && node.size() >= 2) {
        return F.list(F.num(node.get(0).asDouble()), F.num(node.get(1).asDouble()));
      }
      return F.NIL;
    }
    if (ManipulateControl.LOCATOR.equals(kind)) {
      if (node.isArray()) {
        IASTAppendable points = F.ListAlloc(node.size());
        for (JsonNode point : node) {
          if (point.isArray() && point.size() >= 2) {
            points.append(F.list(F.num(point.get(0).asDouble()), F.num(point.get(1).asDouble())));
          }
        }
        return points;
      }
      return F.NIL;
    }
    if (ManipulateControl.COLOR.equals(kind)) {
      return colorOf(node.asText(""));
    }
    if (ManipulateControl.INPUTFIELD.equals(kind)) {
      String text = node.asText("");
      if (text.isEmpty()) {
        return F.NIL;
      }
      try {
        return engine.parse(text);
      } catch (RuntimeException rex) {
        return F.stringx(text);
      }
    }
    // a slider or a trigger: a plain number. An integral value stays an integer, so a body that
    // indexes a list with the control variable keeps working.
    double value = node.asDouble(0.0);
    if (value == Math.rint(value) && Math.abs(value) < 1.0e15 && isIntegerStep(control)) {
      return F.ZZ((long) value);
    }
    return F.num(value);
  }

  private static boolean isIntegerStep(ManipulateControl control) {
    double step = control.effectiveStep();
    return step == Math.rint(step) && step != 0.0;
  }

  private static IExpr fromInitial(ManipulateControl control) {
    Object initial = control.initialValue();
    if (initial instanceof Double) {
      return F.num(((Double) initial).doubleValue());
    }
    if (initial instanceof Boolean) {
      return ((Boolean) initial).booleanValue() ? S.True : S.False;
    }
    return F.NIL;
  }

  /** <code>#rrggbb</code> from a colour input to an <code>RGBColor</code>. */
  private static IExpr colorOf(String text) {
    if (text.length() == 7 && text.charAt(0) == '#') {
      try {
        int r = Integer.parseInt(text.substring(1, 3), 16);
        int g = Integer.parseInt(text.substring(3, 5), 16);
        int b = Integer.parseInt(text.substring(5, 7), 16);
        return F.RGBColor(r / 255.0, g / 255.0, b / 255.0);
      } catch (NumberFormatException nfe) {
        // fall through
      }
    }
    return F.NIL;
  }

  /**
   * Run a <code>Button</code>'s action against the live bindings. The action may assign to the
   * widget's own control variables, so the values it wrote are read back and returned.
   */
  static ObjectNode runButtonAction(EvalEngine engine, ManipulateSpec spec, JsonNode bindings,
      int controlIndex) {
    List<ManipulateControl> controls = spec.getControls();
    if (controlIndex < 0 || controlIndex >= controls.size()) {
      return null;
    }
    ManipulateControl button = controls.get(controlIndex);
    if (!ManipulateControl.BUTTON.equals(button.getKind())) {
      return null;
    }
    // Evaluate the action with the control variables bound, then read them back: an action such
    // as "t = 0" has to move the slider it targets, not just run.
    IASTAppendable locals = F.ListAlloc(controls.size());
    for (ManipulateControl control : controls) {
      if (control.bindsVariable()) {
        IExpr value = valueOf(control, bindings.get(control.getName()), engine);
        if (value.isPresent()) {
          locals.append(F.Set(control.getVariable(), value));
        }
      }
    }
    IASTAppendable body = F.ast(S.CompoundExpression, 2);
    body.append(button.getAction());
    IASTAppendable readBack = F.ListAlloc(controls.size());
    for (ManipulateControl control : controls) {
      if (control.bindsVariable()) {
        readBack.append(control.getVariable());
      }
    }
    body.append(readBack);
    IExpr result = engine.evaluate(F.Block(locals, body));

    ObjectNode updated = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    if (result.isList()) {
      IAST values = (IAST) result;
      int i = 1;
      for (ManipulateControl control : controls) {
        if (!control.bindsVariable() || i >= values.size()) {
          continue;
        }
        putBinding(updated, control, values.get(i++));
      }
    }
    return updated;
  }

  /** Write a value the body or a button assigned back into the browser's binding shape. */
  private static void putBinding(ObjectNode updated, ManipulateControl control, IExpr value) {
    String kind = control.getKind();
    String name = control.getName();
    if (ManipulateControl.DISCRETE.equals(kind)) {
      List<IExpr> values = control.getValues();
      for (int i = 0; i < values.size(); i++) {
        if (values.get(i).equals(value)) {
          updated.put(name, i);
          return;
        }
      }
      return;
    }
    if (ManipulateControl.CHECKBOX.equals(kind)) {
      if (value.isTrue() || value.isFalse()) {
        updated.put(name, value.isTrue());
      }
      return;
    }
    if (ManipulateControl.SLIDER2D.equals(kind) || ManipulateControl.INTERVAL.equals(kind)) {
      if (value.isList2()) {
        IAST pair = (IAST) value;
        ArrayNode node = updated.putArray(name);
        node.add(ManipulateControl.toDouble(pair.arg1(), 0.0));
        node.add(ManipulateControl.toDouble(pair.arg2(), 0.0));
      }
      return;
    }
    if (ManipulateControl.LOCATOR.equals(kind)) {
      if (value.isList()) {
        IAST points = (IAST) value;
        ArrayNode node = updated.putArray(name);
        for (int i = 1; i < points.size(); i++) {
          if (points.get(i).isList2()) {
            IAST point = (IAST) points.get(i);
            ArrayNode pair = node.addArray();
            pair.add(ManipulateControl.toDouble(point.arg1(), 0.0));
            pair.add(ManipulateControl.toDouble(point.arg2(), 0.0));
          }
        }
      }
      return;
    }
    if (value.isNumber()) {
      updated.put(name, ManipulateControl.toDouble(value, 0.0));
    }
  }
}
