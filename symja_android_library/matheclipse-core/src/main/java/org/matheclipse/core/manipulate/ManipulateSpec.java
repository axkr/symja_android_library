package org.matheclipse.core.manipulate;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A parsed <code>Manipulate[body, controls..., options...]</code>.
 *
 * <p>
 * The body is kept unevaluated. Nothing here renders anything: the front end builds the controls
 * from {@link #toJSON(ObjectMapper)} and asks the server to evaluate the body again for each set of
 * control values, so a body of any shape works - a plot, a piece of 3D graphics, a matrix, a
 * symbolic result - instead of only the shapes a JavaScript transpiler happens to cover.
 *
 * <p>
 * <code>Animate</code>, <code>ListAnimate</code> and <code>Animator</code> parse into the same
 * object with {@link #isAnimated()} set.
 */
public class ManipulateSpec {

  /** Names of the options that are understood; anything else is reported by the caller. */
  private static final ISymbol[] KNOWN_OPTIONS = { //
      S.AnimationDirection, S.AnimationRate, S.AnimationRepetitions, S.AnimationRunning,
      S.Alignment, S.Appearance, S.AppearanceElements, S.AutoAction, S.AutorunSequencing,
      S.BaseStyle, S.BaselinePosition, S.Bookmarks, S.ContentSize, S.ContinuousAction,
      S.ControlPlacement, S.ControlType, S.DefaultDuration, S.Deinitialization, S.Deployed,
      S.DisplayAllSteps, S.Enabled, S.Evaluator, S.Exclusions, S.FrameLabel, S.FrameMargins,
      S.ImageMargins, S.ImageSize, S.Initialization, S.InterpolationOrder, S.LabelStyle,
      S.LocalizeVariables, S.Method, S.Paneled, S.PreserveImageOptions, S.RefreshRate,
      S.RotateLabel, S.SaveDefinitions, S.ShrinkingDelay, S.SynchronousInitialization,
      S.SynchronousUpdating, S.TouchscreenAutoZoom, S.TrackedSymbols, S.UnsavedVariables,
      S.UntrackedVariables};

  private final IExpr body;

  private final List<ManipulateControl> controls = new ArrayList<ManipulateControl>();

  private final Map<ISymbol, IExpr> options = new IdentityHashMap<ISymbol, IExpr>();

  /**
   * <code>true</code> for <code>Animate</code>, <code>ListAnimate</code>, <code>Animator</code>.
   */
  private boolean animated = false;

  /** The variable an animation advances, or <code>null</code> for the first continuous control. */
  private String animationVariable = null;

  /** Options that are not understood, collected while parsing and reported to the user. */
  private final List<String> unknownControlOptions = new ArrayList<String>();

  private ManipulateSpec(IExpr body) {
    this.body = body;
  }

  public IExpr getBody() {
    return body;
  }

  public List<ManipulateControl> getControls() {
    return controls;
  }

  public boolean isAnimated() {
    return animated;
  }

  public IExpr getOption(ISymbol name) {
    IExpr value = options.get(name);
    return value == null ? F.NIL : value;
  }

  /** The <code>Initialization :&gt; ...</code> code, run once before the first rendering. */
  public IExpr getInitialization() {
    return getOption(S.Initialization);
  }

  public IExpr getDeinitialization() {
    return getOption(S.Deinitialization);
  }

  /**
   * The variables whose change re-runs the body, or <code>null</code> when everything is tracked.
   */
  public List<String> getTrackedSymbols() {
    IExpr tracked = getOption(S.TrackedSymbols);
    if (!tracked.isPresent() || tracked == S.All || tracked == S.True) {
      return null;
    }
    List<String> names = new ArrayList<String>();
    if (tracked.isList()) {
      IAST list = (IAST) tracked;
      for (int i = 1; i < list.size(); i++) {
        if (list.get(i).isSymbol()) {
          names.add(((ISymbol) list.get(i)).getSymbolName());
        }
      }
    } else if (tracked.isSymbol()) {
      names.add(((ISymbol) tracked).getSymbolName());
    }
    return names;
  }

  public ManipulateControl controlNamed(String name) {
    for (ManipulateControl control : controls) {
      if (control.bindsVariable() && control.getName().equals(name)) {
        return control;
      }
    }
    return null;
  }

  /**
   * Parse a held <code>Manipulate</code> / <code>Animate</code> / <code>ListAnimate</code> /
   * <code>Animator</code> expression.
   *
   * @return the specification, or <code>null</code> when the expression is not one of those heads
   *         or carries no usable control
   */
  public static ManipulateSpec parse(IExpr expr, EvalEngine engine) {
    if (!expr.isAST()) {
      return null;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    boolean animate = head == S.Animate || head == S.ListAnimate || head == S.Animator;
    if (head != S.Manipulate && !animate) {
      return null;
    }
    if (ast.size() < 2) {
      return null;
    }

    ManipulateSpec spec = new ManipulateSpec(ast.arg1());
    spec.animated = animate;

    for (int i = 2; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (isOptionRule(arg)) {
        spec.putOption((IAST) arg);
        continue;
      }
      if (arg.isList() && arg.isAST() && allOptionRules((IAST) arg)) {
        IAST list = (IAST) arg;
        for (int j = 1; j < list.size(); j++) {
          spec.putOption((IAST) list.get(j));
        }
        continue;
      }
      // a PaneSelector argument is a whole panel of controls per pane, only one of which is on
      // screen at a time; when its panes hold no controls it is prose, and falls through to be
      // read as an ordinary argument below
      if (arg.isAST(S.PaneSelector) && arg.size() >= 3
          && spec.addPaneSelector((IAST) arg, engine)) {
        continue;
      }
      ManipulateControl control = parseControl(arg, engine, spec.unknownControlOptions);
      if (control != null) {
        spec.controls.add(control);
      }
    }

    if (spec.controls.isEmpty()) {
      return null;
    }
    if (animate) {
      for (ManipulateControl control : spec.controls) {
        if (ManipulateControl.SLIDER.equals(control.getKind())) {
          spec.animationVariable = control.getName();
          break;
        }
      }
    }
    // a control asked for by ControlType -> Trigger drives the animation instead
    for (ManipulateControl control : spec.controls) {
      if (ManipulateControl.TRIGGER.equals(control.getKind())) {
        spec.animated = true;
        spec.animationVariable = control.getName();
        break;
      }
    }
    return spec;
  }

  /**
   * Add the controls of every pane of a <code>PaneSelector[{v1 -&gt; ..., ...}, sel]</code> argument.
   *
   * <p>
   * All of them are added, each carrying the test that says whether its own pane is the one
   * showing. Adding only the visible pane's would mean rebuilding the panel whenever the selector
   * moved, and would leave the hidden panes' variables unbound - so the body could not refer to
   * them, which it regularly does.
   *
   * <p>
   * A pane holds either one control specification of its own, or any arrangement of
   * <code>Control[...]</code> wrappers. That is the only way to tell a pane holding several
   * controls from a single control written as a list, since both are lists of lists; a pane with
   * more than one control has to name each with <code>Control</code>.
   *
   * @return <code>true</code> when the selector contributed at least one control, and so has been
   *         dealt with here
   */
  private boolean addPaneSelector(IAST paneSelector, EvalEngine engine) {
    if (!paneSelector.arg1().isList()) {
      return false;
    }
    IExpr selector = Dynamics.release(paneSelector.arg2());
    IAST panes = (IAST) paneSelector.arg1();
    List<ManipulateControl> added = new ArrayList<ManipulateControl>();
    for (int i = 1; i < panes.size(); i++) {
      IExpr pane = panes.get(i);
      if (!pane.isRule() && !pane.isRuleDelayed()) {
        continue;
      }
      IExpr value = ((IAST) pane).arg1();
      IExpr content = ((IAST) pane).arg2();
      IExpr condition = F.Equal(selector, value);
      for (ManipulateControl control : paneControls(content, engine)) {
        control.setVisibleCondition(condition);
        added.add(control);
      }
    }
    controls.addAll(added);
    return !added.isEmpty();
  }

  /** The controls one pane declares. */
  private List<ManipulateControl> paneControls(IExpr content, EvalEngine engine) {
    List<ManipulateControl> found = new ArrayList<ManipulateControl>();
    if (containsControlWrapper(content)) {
      collectControlWrappers(content, engine, found);
      return found;
    }
    ManipulateControl single = parseControl(content, engine, unknownControlOptions);
    if (single != null && single.bindsVariable()) {
      found.add(single);
    }
    return found;
  }

  private static boolean containsControlWrapper(IExpr expr) {
    if (expr.isAST(S.Control, 2)) {
      return true;
    }
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    for (int i = 1; i < ast.size(); i++) {
      if (containsControlWrapper(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  private void collectControlWrappers(IExpr expr, EvalEngine engine,
      List<ManipulateControl> found) {
    if (expr.isAST(S.Control, 2)) {
      ManipulateControl control =
          parseControl(((IAST) expr).arg1(), engine, unknownControlOptions);
      if (control != null) {
        found.add(control);
      }
      return;
    }
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    for (int i = 1; i < ast.size(); i++) {
      collectControlWrappers(ast.get(i), engine, found);
    }
  }

  private void putOption(IAST rule) {
    if (rule.arg1().isSymbol()) {
      options.put((ISymbol) rule.arg1(), rule.arg2());
    }
  }

  private static boolean isOptionRule(IExpr expr) {
    return expr.isRule() || expr.isRuleDelayed();
  }

  private static boolean allOptionRules(IAST list) {
    if (list.size() < 2) {
      return false;
    }
    for (int i = 1; i < list.size(); i++) {
      if (!isOptionRule(list.get(i))) {
        return false;
      }
    }
    return true;
  }

  /** The options that may be given on a single control rather than on the Manipulate itself. */
  private static final ISymbol[] KNOWN_CONTROL_OPTIONS =
      {S.Appearance, S.AppearanceElements, S.BaseStyle, S.ControlPlacement, S.ControlType,
          S.ContinuousAction, S.Enabled, S.ImageSize, S.LabelStyle, S.LocatorAutoCreate, S.Method};

  private static boolean isKnownControlOption(ISymbol name) {
    for (ISymbol candidate : KNOWN_CONTROL_OPTIONS) {
      if (candidate == name) {
        return true;
      }
    }
    return false;
  }

  /** Names of the options this implementation does not know, on the Manipulate or on a control. */
  public List<String> unknownOptions() {
    List<String> unknown = new ArrayList<String>(unknownControlOptions);
    for (ISymbol name : options.keySet()) {
      boolean known = false;
      for (ISymbol candidate : KNOWN_OPTIONS) {
        if (candidate == name) {
          known = true;
          break;
        }
      }
      if (!known && !unknown.contains(name.getSymbolName())) {
        unknown.add(name.getSymbolName());
      }
    }
    return unknown;
  }

  /**
   * The messages to show above the widget: one per option that is not understood, in the shape we
   * use for an unknown option.
   */
  public List<String> warnings() {
    List<String> messages = new ArrayList<String>();
    for (String name : unknownOptions()) {
      messages.add("Manipulate: " + name + " is not a known option and was ignored.");
    }
    return messages;
  }

  // ---------------------------------------------------------------- controls

  private static ManipulateControl parseControl(IExpr spec, EvalEngine engine,
      List<String> unknown) {
    if (spec == S.Delimiter) {
      return ManipulateControl.delimiter();
    }
    if (spec.isString()) {
      return ManipulateControl.heading(spec.toString());
    }
    if (spec.isAST(S.Button, 3)) {
      IAST button = (IAST) spec;
      return ManipulateControl.button(labelOf(button.arg1()), button.arg2());
    }
    if (spec.isAST(S.Control, 2)) {
      return parseControl(((IAST) spec).arg1(), engine, unknown);
    }
    if (spec.isList() && spec.size() >= 2) {
      return parseListControl((IAST) spec, engine, unknown);
    }
    // An argument that carries a Dynamic is a live read-out, not a control and not a static
    // heading: Manipulate[..., Row[{"moves: ", Dynamic[moves]}]] has to follow the moves it
    // counts. Taking its text once - which is what the heading below does - would freeze it at
    // whatever the first frame happened to show.
    if (Dynamics.containsDynamic(spec)) {
      return ManipulateControl.display(spec);
    }
    if (spec.isAST(S.Style, 2, 4)) {
      return ManipulateControl.heading(((IAST) spec).arg1().toString());
    }
    return null;
  }

  /**
   * Parse the <code>{u, ...}</code> and <code>{{u, uinit, ulabel}, ...}</code> forms, in every
   * combination for the second and later arguments of Manipulate.
   */
  private static ManipulateControl parseListControl(IAST spec, EvalEngine engine,
      List<String> unknown) {
    IExpr first = spec.arg1();
    ISymbol variable;
    IExpr initial = F.NIL;
    String label = null;

    if (first.isSymbol()) {
      variable = (ISymbol) first;
    } else if (first.isList() && first.size() >= 2 && first.first().isSymbol()) {
      IAST head = (IAST) first;
      variable = (ISymbol) head.arg1();
      if (head.size() >= 3) {
        initial = head.arg2();
      }
      if (head.size() >= 4) {
        label = labelOf(head.arg3());
      }
    } else {
      return null;
    }

    // split the rest into positional arguments and per control options
    List<IExpr> args = new ArrayList<IExpr>();
    Map<ISymbol, IExpr> controlOptions = new IdentityHashMap<ISymbol, IExpr>();
    for (int i = 2; i < spec.size(); i++) {
      IExpr arg = spec.get(i);
      if (isOptionRule(arg) && ((IAST) arg).arg1().isSymbol()) {
        ISymbol optionName = (ISymbol) ((IAST) arg).arg1();
        if (!isKnownControlOption(optionName)) {
          unknown.add(optionName.getSymbolName());
        }
        controlOptions.put(optionName, ((IAST) arg).arg2());
      } else {
        // a bound written {t, 0, Dynamic[period]} follows another control; the wrapper is not
        // part of the number, so it comes off before the range is read
        args.add(Dynamics.release(arg));
      }
    }

    String controlType = typeName(controlOptions.get(S.ControlType));
    if (controlType == null) {
      // the control may also be named positionally, as in {{p, {0, 0}}, Locator}
      controlType = takePositionalControlType(args);
    }
    ManipulateControl control = build(variable, args, initial, controlType, engine);
    if (control == null) {
      return null;
    }
    if (label != null) {
      control.setLabel(label);
    }
    IExpr appearance = controlOptions.get(S.Appearance);
    if (appearance != null) {
      control.setAppearance(lowerName(typeName(appearance)));
    }
    IExpr enabled = controlOptions.get(S.Enabled);
    if (enabled != null) {
      // Enabled -> Dynamic[cond] is how a demonstration greys one control out from the state of
      // another; the condition is resolved against the live values, so the wrapper adds nothing
      control.setEnabledCondition(Dynamics.release(enabled));
    }
    IExpr placement = controlOptions.get(S.ControlPlacement);
    if (placement != null) {
      control.setPlacement(lowerName(typeName(placement)));
    }
    if (controlOptions.containsKey(S.LocatorAutoCreate)) {
      control.setAutoCreate(!controlOptions.get(S.LocatorAutoCreate).isFalse());
    }
    return control;
  }

  /** The control heads that may stand among the positional arguments instead of after a rule. */
  private static final String[] POSITIONAL_CONTROL_TYPES = {"Locator", "Slider", "Slider2D",
      "IntervalSlider", "VerticalSlider", "Manipulator", "Checkbox", "Toggler", "TogglerBar",
      "SetterBar", "RadioButtonBar", "PopupMenu", "Setter", "RadioButton", "Trigger", "Animator",
      "InputField", "ColorSetter", "ColorSlider", "ProgressIndicator"};

  /**
   * Take a bare control head out of the positional arguments, as in
   * <code>{{p, {0, 0}}, Locator}</code>, and return its name.
   */
  private static String takePositionalControlType(List<IExpr> args) {
    for (int i = 0; i < args.size(); i++) {
      IExpr arg = args.get(i);
      if (!arg.isSymbol()) {
        continue;
      }
      String name = ((ISymbol) arg).getSymbolName();
      for (String candidate : POSITIONAL_CONTROL_TYPES) {
        if (name.equalsIgnoreCase(candidate)) {
          args.remove(i);
          return candidate;
        }
      }
    }
    return null;
  }

  private static ManipulateControl build(ISymbol variable, List<IExpr> args, IExpr initial,
      String controlType, EvalEngine engine) {

    if (isName(controlType, "Locator")) {
      return locator(variable, args, initial);
    }

    // {u, {choices...}} - a choice out of a list
    if (args.size() == 1 && args.get(0).isList()) {
      return discrete(variable, (IAST) args.get(0), initial, controlType);
    }

    // {u, {xmin, ymin}, {xmax, ymax}} - a 2D slider over a rectangle
    if (args.size() >= 2 && args.get(0).isList2() && args.get(1).isList2()) {
      IAST low = (IAST) args.get(0);
      IAST high = (IAST) args.get(1);
      ManipulateControl control =
          new ManipulateControl(kindOr(controlType, ManipulateControl.SLIDER2D), variable);
      control.setRange(ManipulateControl.toDouble(low.arg1(), 0.0),
          ManipulateControl.toDouble(high.arg1(), 1.0), Double.NaN);
      control.setRangeY(ManipulateControl.toDouble(low.arg2(), 0.0),
          ManipulateControl.toDouble(high.arg2(), 1.0));
      control.setInitial(initial.isPresent() ? initial : F.list(low.arg1(), low.arg2()));
      return control;
    }

    // {u, umin, umax} and {u, umin, umax, du}
    if (args.size() >= 2) {
      double min = ManipulateControl.toDouble(args.get(0), 0.0);
      double max = ManipulateControl.toDouble(args.get(1), 1.0);
      double step =
          args.size() >= 3 ? ManipulateControl.toDouble(args.get(2), Double.NaN) : Double.NaN;

      String kind = ManipulateControl.SLIDER;
      if (controlType != null) {
        if (isName(controlType, "Trigger") || isName(controlType, "Animator")) {
          kind = ManipulateControl.TRIGGER;
        } else if (isName(controlType, "IntervalSlider")) {
          kind = ManipulateControl.INTERVAL;
        } else if (isName(controlType, "Locator")) {
          kind = ManipulateControl.LOCATOR;
        } else if (isName(controlType, "InputField")) {
          kind = ManipulateControl.INPUTFIELD;
        } else if (isName(controlType, "None")) {
          return null;
        }
      }
      // an initial value that is a pair asks for an interval slider
      if (ManipulateControl.SLIDER.equals(kind) && initial.isList2()) {
        kind = ManipulateControl.INTERVAL;
      }
      ManipulateControl control = new ManipulateControl(kind, variable);
      control.setRange(min, max, step);
      control.setInitial(initial.isPresent() ? initial : F.num(min));
      return control;
    }

    // {u} with an initial value only - a checkbox for a boolean, otherwise an input field
    if (args.isEmpty()) {
      if (initial.isTrue() || initial.isFalse()) {
        ManipulateControl control =
            new ManipulateControl(kindOr(controlType, ManipulateControl.CHECKBOX), variable);
        control.setInitial(initial);
        return control;
      }
      if (initial.isPresent()) {
        ManipulateControl control =
            new ManipulateControl(kindOr(controlType, ManipulateControl.INPUTFIELD), variable);
        control.setInitial(initial);
        return control;
      }
    }
    return null;
  }

  /**
   * A <code>Locator</code> control: one or more draggable points.
   *
   * <p>
   * The initial value is either a single point <code>{x, y}</code> or a list of them; the optional
   * <code>{xmin, ymin}, {xmax, ymax}</code> arguments give the rectangle they move in, defaulting
   * to the unit square the way <code>LocatorPane</code> does.
   */
  private static ManipulateControl locator(ISymbol variable, List<IExpr> args, IExpr initial) {
    ManipulateControl control = new ManipulateControl(ManipulateControl.LOCATOR, variable);

    double xMin = 0.0, yMin = 0.0, xMax = 1.0, yMax = 1.0;
    boolean explicitRectangle = args.size() >= 2 && args.get(0).isList2() && args.get(1).isList2();
    if (explicitRectangle) {
      IAST low = (IAST) args.get(0);
      IAST high = (IAST) args.get(1);
      xMin = ManipulateControl.toDouble(low.arg1(), 0.0);
      yMin = ManipulateControl.toDouble(low.arg2(), 0.0);
      xMax = ManipulateControl.toDouble(high.arg1(), 1.0);
      yMax = ManipulateControl.toDouble(high.arg2(), 1.0);
    }
    control.setRange(xMin, xMax, Double.NaN);
    control.setRangeY(yMin, yMax);

    if (initial.isList2() && !initial.first().isList()) {
      // a single point: the variable is bound to that point, not to a list of one
      IAST point = (IAST) initial;
      control.setSinglePoint(true);
      control.addPoint(ManipulateControl.toDouble(point.arg1(), xMin),
          ManipulateControl.toDouble(point.arg2(), yMin));
    } else if (initial.isList()) {
      IAST points = (IAST) initial;
      for (int i = 1; i < points.size(); i++) {
        if (points.get(i).isList2()) {
          IAST point = (IAST) points.get(i);
          control.addPoint(ManipulateControl.toDouble(point.arg1(), xMin),
              ManipulateControl.toDouble(point.arg2(), yMin));
        }
      }
    }
    if (!control.hasPoints()) {
      // no initial position given: one point, in the middle of the rectangle
      control.setSinglePoint(true);
      control.addPoint((xMin + xMax) / 2.0, (yMin + yMax) / 2.0);
    }
    if (!explicitRectangle) {
      // The unit square is only a fallback. A point sitting on its edge - which is what
      // {{p, {1, 1}}, Locator} gives - could then only be dragged inwards, so the box is grown
      // until every point has room to move on all sides.
      control.growToFitPoints();
    }
    control.setInitial(initial);
    return control;
  }

  private static ManipulateControl discrete(ISymbol variable, IAST choices, IExpr initial,
      String controlType) {
    List<IExpr> values = new ArrayList<IExpr>();
    List<String> labels = new ArrayList<String>();
    for (int i = 1; i < choices.size(); i++) {
      IExpr choice = choices.get(i);
      if (choice.isRule()) {
        // value -> label
        values.add(((IAST) choice).arg1());
        labels.add(labelOf(((IAST) choice).arg2()));
      } else {
        values.add(choice);
        labels.add(labelOf(choice));
      }
    }
    if (values.isEmpty()) {
      return null;
    }

    String kind = ManipulateControl.DISCRETE;
    if (controlType != null) {
      if (isName(controlType, "Checkbox") || isName(controlType, "Toggler")) {
        kind = ManipulateControl.CHECKBOX;
      } else if (isName(controlType, "None")) {
        return null;
      }
    } else if (ManipulateControl.isBooleanPair(values)) {
      kind = ManipulateControl.CHECKBOX;
    }

    ManipulateControl control = new ManipulateControl(kind, variable);
    if (ManipulateControl.CHECKBOX.equals(kind)) {
      control.setInitial(initial.isPresent() ? initial : S.True);
      return control;
    }
    for (int i = 0; i < values.size(); i++) {
      control.addValue(values.get(i), labels.get(i));
    }
    int index = 0;
    if (initial.isPresent()) {
      for (int i = 0; i < values.size(); i++) {
        if (values.get(i).equals(initial)) {
          index = i;
          break;
        }
      }
    }
    control.setInitialIndex(index);
    if (controlType != null) {
      control.setAppearance(lowerName(controlType));
    }
    return control;
  }

  /** Map a <code>ControlType</code> name to the control kind it asks for. */
  private static String kindOr(String controlType, String fallback) {
    if (controlType == null) {
      return fallback;
    }
    if (isName(controlType, "Checkbox") || isName(controlType, "Toggler")) {
      return ManipulateControl.CHECKBOX;
    }
    if (isName(controlType, "ColorSetter") || isName(controlType, "ColorSlider")) {
      return ManipulateControl.COLOR;
    }
    if (isName(controlType, "InputField")) {
      return ManipulateControl.INPUTFIELD;
    }
    if (isName(controlType, "Locator")) {
      return ManipulateControl.LOCATOR;
    }
    if (isName(controlType, "Slider2D")) {
      return ManipulateControl.SLIDER2D;
    }
    if (isName(controlType, "IntervalSlider")) {
      return ManipulateControl.INTERVAL;
    }
    if (isName(controlType, "Trigger") || isName(controlType, "Animator")) {
      return ManipulateControl.TRIGGER;
    }
    return fallback;
  }

  /**
   * Compare a name a user wrote with the one this code expects, ignoring case: with the relaxed
   * Symja syntax the parser lowercases symbol names, so <code>ControlType -&gt; Trigger</code>
   * arrives here as <code>trigger</code>.
   */
  private static boolean isName(String actual, String expected) {
    return actual != null && actual.equalsIgnoreCase(expected);
  }

  private static String typeName(IExpr expr) {
    if (expr == null || !expr.isPresent()) {
      return null;
    }
    if (expr.isSymbol()) {
      return ((ISymbol) expr).getSymbolName();
    }
    if (expr.isString()) {
      return expr.toString();
    }
    if (expr.isAST() && expr.head().isSymbol()) {
      return ((ISymbol) expr.head()).getSymbolName();
    }
    return null;
  }

  private static String lowerName(String name) {
    return name == null ? null : name.toLowerCase();
  }

  private static String labelOf(IExpr expr) {
    if (expr.isString()) {
      return expr.toString();
    }
    if (expr.isAST(S.Style, 2, 4)) {
      return labelOf(((IAST) expr).arg1());
    }
    return expr.toString();
  }

  // ---------------------------------------------------------------- output

  public ObjectNode toJSON(ObjectMapper mapper) {
    ObjectNode json = mapper.createObjectNode();
    ArrayNode controlsNode = mapper.createArrayNode();
    for (ManipulateControl control : controls) {
      controlsNode.add(control.toJSON(mapper));
    }
    json.set("controls", controlsNode);

    ObjectNode optionsNode = mapper.createObjectNode();
    optionsNode.put("animated", animated);
    if (animationVariable != null) {
      optionsNode.put("animationVariable", animationVariable);
    }
    optionsNode.put("continuousAction", !getOption(S.ContinuousAction).isFalse());
    optionsNode.put("animationRunning", !getOption(S.AnimationRunning).isFalse());
    optionsNode.put("animationRate", ManipulateControl.toDouble(getOption(S.AnimationRate), 1.0));
    optionsNode.put("animationDirection", directionSign());
    optionsNode.put("animationRepetitions",
        ManipulateControl.toDouble(getOption(S.AnimationRepetitions), Double.POSITIVE_INFINITY));
    optionsNode.put("defaultDuration",
        ManipulateControl.toDouble(getOption(S.DefaultDuration), 5.0));
    optionsNode.put("paneled", !getOption(S.Paneled).isFalse());
    optionsNode.put("deployed", getOption(S.Deployed).isTrue());
    optionsNode.put("appearanceNone", isAppearanceNone());
    String placement = lowerName(typeName(getOption(S.ControlPlacement)));
    optionsNode.put("controlPlacement", placement == null ? "top" : placement);
    IExpr imageSize = getOption(S.ImageSize);
    if (imageSize.isPresent()) {
      optionsNode.put("imageSize", imageSize.toString());
    }
    ArrayNode elements = mapper.createArrayNode();
    IExpr appearanceElements = getOption(S.AppearanceElements);
    if (appearanceElements.isList()) {
      IAST list = (IAST) appearanceElements;
      for (int i = 1; i < list.size(); i++) {
        elements.add(list.get(i).toString());
      }
    }
    optionsNode.set("appearanceElements", elements);

    List<String> tracked = getTrackedSymbols();
    if (tracked != null) {
      ArrayNode trackedNode = mapper.createArrayNode();
      for (String name : tracked) {
        trackedNode.add(name);
      }
      optionsNode.set("trackedSymbols", trackedNode);
    }
    json.set("options", optionsNode);
    return json;
  }

  /** <code>Appearance -&gt; None</code> hides the control rows. */
  public boolean isAppearanceNone() {
    IExpr appearance = getOption(S.Appearance);
    return appearance == S.None;
  }

  private int directionSign() {
    IExpr direction = getOption(S.AnimationDirection);
    if (direction.isPresent()) {
      String name = typeName(direction);
      if (isName(name, "Backward")) {
        return -1;
      }
      if (isName(name, "ForwardBackward")) {
        return 2;
      }
    }
    return 1;
  }
}
