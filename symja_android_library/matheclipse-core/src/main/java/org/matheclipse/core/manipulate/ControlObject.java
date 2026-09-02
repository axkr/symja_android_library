package org.matheclipse.core.manipulate;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A control object - <code>Slider[Dynamic[x]]</code>, <code>Checkbox[Dynamic[b]]</code>,
 * <code>PopupMenu[Dynamic[v], {...}]</code> - as a {@link ManipulateControl}.
 *
 * <p>
 * These are the other half of <code>Dynamic</code>. <code>Dynamic[x]</code> shows what
 * <code>x</code> is; a control object wrapped around the same <code>Dynamic</code> is how the user
 * changes it. They appear both on their own and inside the body of a <code>Manipulate</code>, and
 * they describe exactly the widgets {@link ManipulateSpec} already builds from a
 * <code>{u, umin, umax}</code> specification - so they are translated into the same descriptions,
 * and everything downstream, from the JSON to the browser widget to the read-back of the value,
 * stays the one implementation.
 *
 * <p>
 * What a control object carries that a <code>Manipulate</code> specification does not is the
 * <code>Dynamic</code> itself: the specification binds a variable the server chose, while a control
 * object writes wherever the user pointed it, through whatever setter function it was given. That
 * expression is kept on the control - {@link ManipulateControl#getDynamic()} - and the write goes
 * through {@link Dynamics#assign(IAST, IExpr, org.matheclipse.core.eval.EvalEngine)}.
 */
public class ControlObject {

  private ControlObject() {}

  /**
   * The control a control object describes, or <code>null</code> when the expression is not one, or
   * is one this implementation cannot show.
   *
   * <p>
   * The initial value is read out of the <code>Dynamic</code> as it stands rather than evaluated
   * here: a control object inside a body is rebuilt with every frame, so the value it should start
   * at is whatever the variable holds at that moment, and the caller already has an engine in the
   * right state to ask.
   *
   * @param name the identity the browser uses for this control; control objects are not bound by
   *        variable name the way a <code>Manipulate</code> control is, because two of them may
   *        perfectly well drive the same variable
   * @param current the current value of what the <code>Dynamic</code> holds, or {@link F#NIL} when
   *        it has none yet
   */
  public static ManipulateControl parse(IExpr expr, String name, IExpr current) {
    if (!Dynamics.isDynamicControl(expr)) {
      return null;
    }
    IAST call = (IAST) expr;
    IExpr head = call.head();
    IAST dynamic = (IAST) call.arg1();
    // the arguments after the Dynamic that are not options: the range, the choice list, ...
    IExpr second = call.size() > 2 && !isOption(call.arg2()) ? call.arg2() : F.NIL;

    ManipulateControl control = build(head, dynamic, second, current);
    if (control == null) {
      return null;
    }
    control.setName(name);
    control.setDynamic(dynamic);
    control.setLabel("");
    if (!Dynamics.isSettable(dynamic) || head == S.ProgressIndicator) {
      control.setReadOnly(true);
    }
    IExpr appearance = optionOf(call, S.Appearance);
    if (appearance.isPresent()) {
      control.setAppearance(nameOf(appearance));
    }
    IExpr enabled = optionOf(call, S.Enabled);
    if (enabled.isPresent()) {
      control.setEnabledCondition(Dynamics.release(enabled));
    }
    return control;
  }

  private static ManipulateControl build(IExpr head, IAST dynamic, IExpr second, IExpr current) {
    if (head == S.Checkbox || head == S.Toggler || head == S.Opener) {
      ManipulateControl control = new ManipulateControl(ManipulateControl.CHECKBOX, null);
      control.setInitial(current.isTrue() ? S.True : S.False);
      return control;
    }
    if (head == S.PopupMenu || head == S.SetterBar || head == S.RadioButtonBar
        || head == S.RadioButton || head == S.Setter) {
      return discrete(head, second, current);
    }
    if (head == S.TogglerBar || head == S.CheckboxBar) {
      return multi(second, current);
    }
    if (head == S.FileNameSetter) {
      ManipulateControl control = new ManipulateControl(ManipulateControl.FILE, null);
      control.setInitial(current.isString() ? current : F.NIL);
      if (second.isString()) {
        control.setFileDialog(second.toString());
      }
      return control;
    }
    if (head == S.InputField) {
      ManipulateControl control = new ManipulateControl(ManipulateControl.INPUTFIELD, null);
      control.setInitial(current);
      return control;
    }
    if (head == S.ColorSetter || head == S.ColorSlider) {
      ManipulateControl control = new ManipulateControl(ManipulateControl.COLOR, null);
      control.setInitial(current);
      return control;
    }
    if (head == S.Slider2D) {
      return slider2D(second, current);
    }
    if (head == S.IntervalSlider) {
      ManipulateControl control = new ManipulateControl(ManipulateControl.INTERVAL, null);
      applyRange(control, second);
      control.setInitial(current.isList2() ? current : F.NIL);
      return control;
    }
    if (head == S.Locator) {
      return locator(second, current);
    }
    if (head == S.Trigger || head == S.Animator) {
      ManipulateControl control = new ManipulateControl(ManipulateControl.TRIGGER, null);
      applyRange(control, second);
      control.setInitial(current);
      return control;
    }
    if (head == S.Slider || head == S.VerticalSlider || head == S.Manipulator
        || head == S.ProgressIndicator) {
      ManipulateControl control = new ManipulateControl(ManipulateControl.SLIDER, null);
      applyRange(control, second);
      control.setInitial(current);
      return control;
    }
    return null;
  }

  /**
   * The range of a slider style control.
   *
   * <p>
   * <code>Slider[Dynamic[x]]</code> with no range at all runs over the unit interval, which is what
   * the defaults on the control already are; <code>{min, max}</code> and
   * <code>{min, max, step}</code> say otherwise.
   */
  private static void applyRange(ManipulateControl control, IExpr range) {
    if (!range.isList() || range.size() < 3) {
      control.setRange(0.0, 1.0, Double.NaN);
      return;
    }
    IAST list = (IAST) range;
    double min = ManipulateControl.toDouble(list.arg1(), 0.0);
    double max = ManipulateControl.toDouble(list.arg2(), 1.0);
    double step = list.size() >= 4 ? ManipulateControl.toDouble(list.arg3(), Double.NaN)
        : Double.NaN;
    control.setRange(min, max, step);
  }

  /** A choice out of a list, with the current value picked out of it. */
  private static ManipulateControl discrete(IExpr head, IExpr choices, IExpr current) {
    if (!choices.isList() || choices.size() < 2) {
      return null;
    }
    ManipulateControl control = new ManipulateControl(ManipulateControl.DISCRETE, null);
    IAST list = (IAST) choices;
    int index = 0;
    for (int i = 1; i < list.size(); i++) {
      IExpr choice = list.get(i);
      IExpr value = choice;
      String label;
      if (choice.isRule()) {
        value = ((IAST) choice).arg1();
        label = labelOf(((IAST) choice).arg2());
      } else {
        label = labelOf(choice);
      }
      if (value.equals(current)) {
        index = i - 1;
      }
      control.addValue(value, label);
    }
    control.setInitialIndex(index);
    if (head == S.PopupMenu) {
      control.setAppearance("popupmenu");
    } else if (head == S.RadioButtonBar || head == S.RadioButton) {
      control.setAppearance("radiobuttonbar");
    } else {
      control.setAppearance("setterbar");
    }
    return control;
  }

  /**
   * A bar whose variable holds the <em>list</em> of everything that is switched on, the way
   * <code>TogglerBar</code> and <code>CheckboxBar</code> work.
   */
  private static ManipulateControl multi(IExpr choices, IExpr current) {
    if (!choices.isList() || choices.size() < 2) {
      return null;
    }
    ManipulateControl control = new ManipulateControl(ManipulateControl.MULTI, null);
    IAST list = (IAST) choices;
    for (int i = 1; i < list.size(); i++) {
      IExpr choice = list.get(i);
      if (choice.isRule()) {
        control.addValue(((IAST) choice).arg1(), labelOf(((IAST) choice).arg2()));
      } else {
        control.addValue(choice, labelOf(choice));
      }
    }
    control.setInitial(current.isList() ? current : F.CEmptyList);
    return control;
  }

  private static ManipulateControl slider2D(IExpr range, IExpr current) {
    ManipulateControl control = new ManipulateControl(ManipulateControl.SLIDER2D, null);
    double xMin = 0.0, yMin = 0.0, xMax = 1.0, yMax = 1.0;
    if (range.isList2() && range.first().isList2() && ((IAST) range).arg2().isList2()) {
      IAST low = (IAST) range.first();
      IAST high = (IAST) ((IAST) range).arg2();
      xMin = ManipulateControl.toDouble(low.arg1(), 0.0);
      yMin = ManipulateControl.toDouble(low.arg2(), 0.0);
      xMax = ManipulateControl.toDouble(high.arg1(), 1.0);
      yMax = ManipulateControl.toDouble(high.arg2(), 1.0);
    }
    control.setRange(xMin, xMax, Double.NaN);
    control.setRangeY(yMin, yMax);
    control.setInitial(current.isList2() ? current : F.list(F.num(xMin), F.num(yMin)));
    return control;
  }

  /**
   * A draggable point. <code>Locator[Dynamic[p]]</code> binds one point, so the box it moves in is
   * grown around it the way a <code>Locator</code> control specification is.
   */
  private static ManipulateControl locator(IExpr range, IExpr current) {
    ManipulateControl control = new ManipulateControl(ManipulateControl.LOCATOR, null);
    control.setRange(0.0, 1.0, Double.NaN);
    control.setRangeY(0.0, 1.0);
    if (current.isList2() && !current.first().isList()) {
      IAST point = (IAST) current;
      control.setSinglePoint(true);
      control.addPoint(ManipulateControl.toDouble(point.arg1(), 0.0),
          ManipulateControl.toDouble(point.arg2(), 0.0));
    } else if (current.isList()) {
      IAST points = (IAST) current;
      for (int i = 1; i < points.size(); i++) {
        if (points.get(i).isList2()) {
          IAST point = (IAST) points.get(i);
          control.addPoint(ManipulateControl.toDouble(point.arg1(), 0.0),
              ManipulateControl.toDouble(point.arg2(), 0.0));
        }
      }
    }
    if (!control.hasPoints()) {
      control.setSinglePoint(true);
      control.addPoint(0.5, 0.5);
    }
    control.growToFitPoints();
    control.setInitial(current);
    return control;
  }

  private static boolean isOption(IExpr expr) {
    return expr.isRule() || expr.isRuleDelayed();
  }

  private static IExpr optionOf(IAST call, ISymbol name) {
    for (int i = 2; i < call.size(); i++) {
      IExpr arg = call.get(i);
      if (isOption(arg) && ((IAST) arg).arg1() == name) {
        return ((IAST) arg).arg2();
      }
    }
    return F.NIL;
  }

  private static String nameOf(IExpr expr) {
    if (expr.isSymbol()) {
      return ((ISymbol) expr).getSymbolName().toLowerCase();
    }
    if (expr.isString()) {
      return expr.toString().toLowerCase();
    }
    return null;
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
}
