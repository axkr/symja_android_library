package org.matheclipse.io.servlet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.output.StringBuilderWriter;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
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

  /**
   * The actions of the <code>Button</code> elements that the body itself produced, per widget.
   *
   * <p>
   * A body may build its own buttons - <code>Manipulate[{x^2, Button["reset", x = 0]}, ...]</code>
   * - and their code stays here rather than travelling to the browser and back as text: the browser
   * only sends the position of the button it was told about. The list is replaced on every
   * rendering, because the body may build different buttons each time.
   */
  private static final Map<String, List<IExpr>> BODY_ACTIONS =
      new LinkedHashMap<String, List<IExpr>>();

  private ManipulateSession() {}

  private static synchronized String store(EvalEngine engine, String sessionID,
      ManipulateSpec spec) {
    Map<String, ManipulateSpec> widgets = SESSIONS.get(sessionID);
    if (widgets == null) {
      widgets = new LinkedHashMap<String, ManipulateSpec>(16, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ManipulateSpec> eldest) {
          if (size() > MAX_WIDGETS_PER_SESSION) {
            // the widget is gone from here on, so let it clean up after itself
            deinitialize(EvalEngine.get(), eldest.getValue());
            return true;
          }
          return false;
        }
      };
      SESSIONS.put(sessionID, widgets);
    }
    String id = "mp" + (++widgetCounter);
    widgets.put(id, spec);
    return id;
  }

  /**
   * Run a widget's <code>Deinitialization :&gt; ...</code> code. It will run when the widget goes
   * away; here that is when its cell is deleted, when it drops out of the per session cache, or
   * when the browser session ends.
   */
  /**
   * Take the <code>Button</code> elements out of a rendered body: each action is kept here and the
   * button is rewritten to carry its position, which is what the MathML output turns into a real
   * button for the browser.
   */
  static synchronized IExpr registerBodyButtons(String widgetId, IExpr result) {
    List<IExpr> actions = new ArrayList<IExpr>();
    IExpr rewritten = rewriteButtons(result, actions);
    if (actions.isEmpty()) {
      BODY_ACTIONS.remove(widgetId);
    } else {
      BODY_ACTIONS.put(widgetId, actions);
    }
    return rewritten;
  }

  private static IExpr rewriteButtons(IExpr expr, List<IExpr> actions) {
    if (!expr.isAST()) {
      return expr;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.Button, 3)) {
      actions.add(ast.arg2());
      // the second argument becomes the position the browser sends back
      return F.binaryAST2(S.Button, ast.arg1(), F.ZZ(actions.size() - 1));
    }
    IASTMutable copy = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr child = rewriteButtons(ast.get(i), actions);
      if (child != ast.get(i)) {
        if (!copy.isPresent()) {
          copy = ast.copy();
        }
        copy.set(i, child);
      }
    }
    return copy.isPresent() ? copy : expr;
  }

  private static void deinitialize(EvalEngine engine, ManipulateSpec spec) {
    if (engine == null || spec == null) {
      return;
    }
    IExpr deinitialization = spec.getDeinitialization();
    if (!deinitialization.isPresent()) {
      return;
    }
    try {
      engine.evaluate(deinitialization);
    } catch (RuntimeException rex) {
      // a failing Deinitialization must not stop the widget from being released
    }
  }

  /**
   * Drop a single widget after running its <code>Deinitialization</code>, for a cell the user
   * deleted.
   *
   * @return <code>true</code> if the widget was there
   */
  static synchronized boolean dispose(EvalEngine engine, String sessionID, String id) {
    Map<String, ManipulateSpec> widgets = SESSIONS.get(sessionID);
    if (widgets == null) {
      return false;
    }
    ManipulateSpec spec = widgets.remove(id);
    if (spec == null) {
      return false;
    }
    BODY_ACTIONS.remove(id);
    deinitialize(engine, spec);
    return true;
  }

  static synchronized ManipulateSpec lookup(String sessionID, String id) {
    Map<String, ManipulateSpec> widgets = SESSIONS.get(sessionID);
    return widgets == null ? null : widgets.get(id);
  }

  /**
   * Forget every widget of a session that has ended, running each one's
   * <code>Deinitialization</code> first.
   *
   * @param engine the engine of that session, or <code>null</code> when it is already gone
   */
  public static synchronized void remove(EvalEngine engine, String sessionID) {
    Map<String, ManipulateSpec> widgets = SESSIONS.remove(sessionID);
    if (widgets == null || engine == null) {
      return;
    }
    for (String widgetId : widgets.keySet()) {
      BODY_ACTIONS.remove(widgetId);
    }
    for (ManipulateSpec spec : widgets.values()) {
      deinitialize(engine, spec);
    }
  }

  /**
   * Register a new widget and render it for the first time.
   *
   * @return the JSON result for the browser, in the <code>String[]</code> shape the servlet uses
   */
  static String[] create(EvalEngine engine, ManipulateSpec spec, StringBuilderWriter outWriter,
      StringBuilderWriter errorWriter) throws java.io.IOException {
    String id = store(engine, engine.getSessionID(), spec);

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

    ObjectNode bindings = initialBindings(spec);
    IExpr result = registerBodyButtons(id, evaluateBody(engine, spec, bindings));
    String[] rendered = AJAXQueryServlet.renderResult(engine, result, outWriter, errorWriter);
    return JSONBuilder.createJSONManipulate(id, spec, rendered[1],
        resolveEnabled(engine, spec, bindings), spec.warnings());
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
    return engine.evaluate(F.Block(bindingList(engine, spec, bindings), spec.getBody()));
  }

  /**
   * Resolve every control's <code>Enabled</code> condition against the control values the browser
   * holds, so a control can be greyed out by the state of another one.
   *
   * @return one flag per control, in the order of {@link ManipulateSpec#getControls()}, or
   *         <code>null</code> when no control carries a condition
   */
  static ArrayNode resolveEnabled(EvalEngine engine, ManipulateSpec spec, JsonNode bindings) {
    boolean any = false;
    for (ManipulateControl control : spec.getControls()) {
      if (control.getEnabledCondition().isPresent()) {
        any = true;
        break;
      }
    }
    if (!any) {
      return null;
    }
    IASTAppendable locals = bindingList(engine, spec, bindings);
    ArrayNode flags = JSONBuilder.JSON_OBJECT_MAPPER.createArrayNode();
    for (ManipulateControl control : spec.getControls()) {
      IExpr condition = control.getEnabledCondition();
      if (!condition.isPresent()) {
        flags.add(true);
        continue;
      }
      try {
        IExpr value = engine.evaluate(F.Block(locals, condition));
        // anything that is not explicitly False leaves the control usable
        flags.add(!value.isFalse());
      } catch (RuntimeException rex) {
        flags.add(true);
      }
    }
    return flags;
  }

  /** The <code>Block</code> local assignments for the control values the browser sent. */
  private static IASTAppendable bindingList(EvalEngine engine, ManipulateSpec spec,
      JsonNode bindings) {
    IASTAppendable locals = F.ListAlloc(spec.getControls().size());
    for (ManipulateControl control : spec.getControls()) {
      if (!control.bindsVariable()) {
        continue;
      }
      IExpr value = valueOf(control, bindings.get(control.getName()), engine);
      if (value.isPresent()) {
        locals.append(F.Set(control.getVariable(), value));
      }
    }
    return locals;
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
        if (control.isSinglePoint()) {
          // declared as one point, so the variable is that point rather than a list of one
          JsonNode point = node.size() > 0 ? node.get(0) : null;
          if (point != null && point.isArray() && point.size() >= 2) {
            return F.list(F.num(point.get(0).asDouble()), F.num(point.get(1).asDouble()));
          }
          return F.NIL;
        }
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
    return runAction(engine, spec, bindings, button.getAction());
  }

  /**
   * Run the action of a <code>Button</code> that the body itself produced, such as the reset button
   * of <code>Manipulate[{x^2, Button["reset", x = 0]}, {x, 0, 10}]</code>.
   *
   * @param widgetId the widget the button belongs to
   * @param actionIndex the position of the button in the last rendering of that widget
   */
  static ObjectNode runBodyButtonAction(EvalEngine engine, ManipulateSpec spec, JsonNode bindings,
      String widgetId, int actionIndex) {
    List<IExpr> actions;
    synchronized (ManipulateSession.class) {
      actions = BODY_ACTIONS.get(widgetId);
    }
    if (actions == null || actionIndex < 0 || actionIndex >= actions.size()) {
      return null;
    }
    return runAction(engine, spec, bindings, actions.get(actionIndex));
  }

  /**
   * Evaluate an action with the control variables bound, then read them back: an action such as
   * <code>t = 0</code> has to move the slider it targets, not merely run.
   */
  private static ObjectNode runAction(EvalEngine engine, ManipulateSpec spec, JsonNode bindings,
      IExpr action) {
    List<ManipulateControl> controls = spec.getControls();
    IASTAppendable locals = bindingList(engine, spec, bindings);
    IASTAppendable body = F.ast(S.CompoundExpression, 2);
    body.append(action);
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
      if (control.isSinglePoint() && value.isList2() && !value.first().isList()) {
        // a single point written back by an action; the browser always holds a list of rows
        IAST point = (IAST) value;
        ArrayNode node = updated.putArray(name);
        ArrayNode pair = node.addArray();
        pair.add(ManipulateControl.toDouble(point.arg1(), 0.0));
        pair.add(ManipulateControl.toDouble(point.arg2(), 0.0));
        return;
      }
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
