package org.matheclipse.core.manipulate;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * One row of a {@link ManipulateSpec} control area.
 *
 * <p>
 * A control is described here, not rendered: the browser builds the widget from
 * {@link #toJSON(ObjectMapper)} and sends the value the user picked back as a plain number, index
 * or list. Turning that value into the expression the body is evaluated with is the job of
 * <code>ManipulateSession#valueOf</code> in the servlet, so no user-visible expression has to
 * travel through the browser as text.
 */
public class ManipulateControl {

  /** A continuous slider: <code>{u, umin, umax}</code> or <code>{u, umin, umax, du}</code>. */
  public static final String SLIDER = "slider";

  /** A choice out of a fixed list: setter bar, popup menu, radio buttons or toggler bar. */
  public static final String DISCRETE = "discrete";

  /** A two state control for <code>{u, {True, False}}</code>. */
  public static final String CHECKBOX = "checkbox";

  /** A 2D slider binding its variable to a <code>{x, y}</code> pair. */
  public static final String SLIDER2D = "slider2d";

  /** An interval slider binding its variable to a <code>{min, max}</code> pair. */
  public static final String INTERVAL = "interval";

  /** A list of draggable points. */
  public static final String LOCATOR = "locator";

  /** A slider that can play its range on its own. */
  public static final String TRIGGER = "trigger";

  /** A colour picker. */
  public static final String COLOR = "color";

  /** A free text field, parsed as an expression when it is read back. */
  public static final String INPUTFIELD = "inputfield";

  /** A <code>Button[label, action]</code> row. Binds no variable. */
  public static final String BUTTON = "button";

  /** A heading between controls, from a string or <code>Style</code> argument. */
  public static final String HEADING = "heading";

  /** A <code>Delimiter</code> argument: a horizontal rule. */
  public static final String DELIMITER = "delimiter";

  private final String kind;

  /** The bound variable, or <code>null</code> for a row that binds nothing. */
  private final ISymbol variable;

  private String label;

  // continuous controls
  private double min = 0.0;
  private double max = 1.0;
  private double step = Double.NaN;
  private IExpr initial = F.NIL;

  // 2D and interval controls
  private double minY = 0.0;
  private double maxY = 1.0;

  // discrete controls
  private final List<IExpr> values = new ArrayList<IExpr>();
  private final List<String> valueLabels = new ArrayList<String>();
  private int initialIndex = 0;

  // locator
  private final List<double[]> points = new ArrayList<double[]>();
  private boolean autoCreate = false;

  /**
   * Whether this Locator was declared with a single point rather than a list of them.
   *
   * <p>
   * The binding has to mirror the shape the user wrote: <code>{{p, {1, 1}}, Locator}</code> binds
   * <code>p</code> to the point <code>{1, 1}</code>, so that a body such as
   * <code>Line[{{0, 0}, p}]</code> works, while <code>{{pts, {{0, 0}, {1, 1}}}, Locator}</code>
   * binds a list of points.
   */
  private boolean singlePoint = false;

  /** A <code>Button</code>'s action, held. */
  private IExpr action = F.NIL;

  /** <code>Appearance</code> of this control, or <code>null</code> for the default. */
  private String appearance;

  /** <code>Enabled -> cond</code>, held so it can be resolved against the live bindings. */
  private IExpr enabled = F.NIL;

  /** <code>ControlPlacement</code> of this single control, or <code>null</code>. */
  private String placement;

  ManipulateControl(String kind, ISymbol variable) {
    this.kind = kind;
    this.variable = variable;
    this.label = variable == null ? "" : variable.getSymbolName();
  }

  public String getKind() {
    return kind;
  }

  public ISymbol getVariable() {
    return variable;
  }

  public String getName() {
    return variable == null ? "" : variable.getSymbolName();
  }

  /** Whether this row binds a variable; headings, delimiters and buttons do not. */
  public boolean bindsVariable() {
    return variable != null;
  }

  public IExpr getAction() {
    return action;
  }

  public IExpr getEnabledCondition() {
    return enabled;
  }

  public List<IExpr> getValues() {
    return values;
  }

  void setLabel(String label) {
    this.label = label;
  }

  void setRange(double min, double max, double step) {
    this.min = min;
    this.max = max;
    this.step = step;
  }

  void setRangeY(double minY, double maxY) {
    this.minY = minY;
    this.maxY = maxY;
  }

  void setInitial(IExpr initial) {
    this.initial = initial;
  }

  void setInitialIndex(int index) {
    this.initialIndex = index;
  }

  void addValue(IExpr value, String valueLabel) {
    values.add(value);
    valueLabels.add(valueLabel);
  }

  void addPoint(double x, double y) {
    points.add(new double[] {x, y});
  }

  /** Whether this Locator carries at least one point. Visible for tests. */
  public boolean hasPointsForTest() {
    return hasPoints();
  }

  /** Whether this Locator already carries at least one point. */
  boolean hasPoints() {
    return !points.isEmpty();
  }

  /** Whether the bound variable is one point rather than a list of them. */
  public boolean isSinglePoint() {
    return singlePoint;
  }

  void setSinglePoint(boolean singlePoint) {
    this.singlePoint = singlePoint;
  }

  /**
   * Widen the rectangle until every point of this Locator has room to move on all sides. Only used
   * when the specification gave no rectangle of its own.
   */
  void growToFitPoints() {
    if (points.isEmpty()) {
      return;
    }
    double lowX = points.get(0)[0], highX = points.get(0)[0];
    double lowY = points.get(0)[1], highY = points.get(0)[1];
    for (double[] point : points) {
      lowX = Math.min(lowX, point[0]);
      highX = Math.max(highX, point[0]);
      lowY = Math.min(lowY, point[1]);
      highY = Math.max(highY, point[1]);
    }
    // a point on or outside an edge gets half the spread - at least half a unit - of room
    if (lowX <= min || highX >= max) {
      double margin = Math.max(highX - lowX, 1.0) / 2.0;
      min = Math.min(min, lowX - margin);
      max = Math.max(max, highX + margin);
    }
    if (lowY <= minY || highY >= maxY) {
      double margin = Math.max(highY - lowY, 1.0) / 2.0;
      minY = Math.min(minY, lowY - margin);
      maxY = Math.max(maxY, highY + margin);
    }
  }

  void setAutoCreate(boolean autoCreate) {
    this.autoCreate = autoCreate;
  }

  void setAction(IExpr action) {
    this.action = action;
  }

  void setAppearance(String appearance) {
    this.appearance = appearance;
  }

  void setEnabledCondition(IExpr enabled) {
    this.enabled = enabled;
  }

  void setPlacement(String placement) {
    this.placement = placement;
  }

  /**
   * The initial value of this control, in the shape the browser sends back. Used so that the first
   * rendering and every later one go through exactly the same code path.
   */
  public Object initialValue() {
    if (DISCRETE.equals(kind)) {
      return Integer.valueOf(initialIndex);
    }
    if (CHECKBOX.equals(kind)) {
      return Boolean.valueOf(initial.isTrue());
    }
    if (SLIDER2D.equals(kind) || INTERVAL.equals(kind)) {
      double[] pair = pairInitial();
      return new double[] {pair[0], pair[1]};
    }
    if (LOCATOR.equals(kind)) {
      return points;
    }
    if (COLOR.equals(kind) || INPUTFIELD.equals(kind)) {
      return initial.isPresent() ? initial.toString() : "";
    }
    return Double.valueOf(numericInitial());
  }

  private double numericInitial() {
    if (initial.isPresent() && initial.isNumericFunction(true)) {
      IExpr n = F.evaln(initial);
      if (n.isReal()) {
        return n.evalf();
      }
    }
    return min;
  }

  private double[] pairInitial() {
    if (initial.isList2()) {
      IAST list = (IAST) initial;
      return new double[] {toDouble(list.arg1(), min), toDouble(list.arg2(), minY)};
    }
    if (SLIDER2D.equals(kind)) {
      return new double[] {min, minY};
    }
    return new double[] {min, max};
  }

  public static double toDouble(IExpr expr, double fallback) {
    if (expr.isPresent()) {
      IExpr n = F.evaln(expr);
      if (n.isReal()) {
        return n.evalf();
      }
    }
    return fallback;
  }

  /** The step to use when the specification did not give one. */
  public double effectiveStep() {
    if (!Double.isNaN(step) && step != 0.0) {
      return step;
    }
    double span = Math.abs(max - min);
    return span > 0.0 ? span / 100.0 : 1.0;
  }

  public ObjectNode toJSON(ObjectMapper mapper) {
    ObjectNode json = mapper.createObjectNode();
    json.put("kind", kind);
    json.put("name", getName());
    json.put("label", label);
    if (appearance != null) {
      json.put("appearance", appearance);
    }
    if (placement != null) {
      json.put("placement", placement);
    }
    json.put("enabled", true);

    if (SLIDER.equals(kind) || TRIGGER.equals(kind)) {
      json.put("min", min);
      json.put("max", max);
      json.put("step", effectiveStep());
      json.put("value", numericInitial());
    } else if (INTERVAL.equals(kind)) {
      double[] pair = pairInitial();
      json.put("min", min);
      json.put("max", max);
      json.put("step", effectiveStep());
      json.set("value", pairNode(mapper, pair));
    } else if (SLIDER2D.equals(kind)) {
      double[] pair = pairInitial();
      json.put("min", min);
      json.put("max", max);
      json.put("minY", minY);
      json.put("maxY", maxY);
      json.set("value", pairNode(mapper, pair));
    } else if (DISCRETE.equals(kind)) {
      ArrayNode labels = mapper.createArrayNode();
      for (String valueLabel : valueLabels) {
        labels.add(valueLabel);
      }
      json.set("labels", labels);
      json.put("value", initialIndex);
    } else if (CHECKBOX.equals(kind)) {
      json.put("value", initial.isTrue());
    } else if (LOCATOR.equals(kind)) {
      ArrayNode pointsNode = mapper.createArrayNode();
      for (double[] point : points) {
        pointsNode.add(pairNode(mapper, point));
      }
      json.set("value", pointsNode);
      json.put("min", min);
      json.put("max", max);
      json.put("minY", minY);
      json.put("maxY", maxY);
      json.put("autoCreate", autoCreate);
    } else if (COLOR.equals(kind) || INPUTFIELD.equals(kind)) {
      json.put("value", initial.isPresent() ? initial.toString() : "");
    } else if (BUTTON.equals(kind)) {
      // the action stays on the server; the browser only sends the row index back
    }
    return json;
  }

  private static ArrayNode pairNode(ObjectMapper mapper, double[] pair) {
    ArrayNode node = mapper.createArrayNode();
    node.add(pair[0]);
    node.add(pair[1]);
    return node;
  }

  /** A control that only labels the panel, for a string or <code>Style</code> argument. */
  static ManipulateControl heading(String label) {
    ManipulateControl control = new ManipulateControl(HEADING, null);
    control.setLabel(label);
    return control;
  }

  static ManipulateControl delimiter() {
    return new ManipulateControl(DELIMITER, null);
  }

  static ManipulateControl button(String label, IExpr action) {
    ManipulateControl control = new ManipulateControl(BUTTON, null);
    control.setLabel(label);
    control.setAction(action);
    return control;
  }

  /** <code>true</code> if the two values are exactly boolean pair <code>(True,False)</code>. */
  static boolean isBooleanPair(List<IExpr> values) {
    return values.size() == 2 && values.get(0) == S.True && values.get(1) == S.False;
  }
}
