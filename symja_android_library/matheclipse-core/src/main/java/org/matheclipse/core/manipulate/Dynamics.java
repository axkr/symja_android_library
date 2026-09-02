package org.matheclipse.core.manipulate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Everything a front end has to know about <code>Dynamic</code>.
 *
 * <p>
 * <code>Dynamic[expr]</code> is not a value but an instruction to a front end: show what
 * <code>expr</code> is <em>now</em>, and re-show it whenever that changes. The head therefore never
 * evaluates - see {@link org.matheclipse.core.builtin.ManipulateFunction} - and everything that
 * gives it a meaning lives here, so that the same rules apply wherever a rendering happens.
 *
 * <p>
 * The three things a caller wants are:
 *
 * <ul>
 * <li>{@link #resolve(IExpr, EvalEngine)} - the expression as it should be shown right now, with
 * every <code>Dynamic</code> inside it replaced by its current value.
 * <li>{@link #release(IExpr)} - the held expression of a single <code>Dynamic</code>, for the
 * places where a wrapper only stands in the way: an option value, a graphics primitive's argument,
 * the position list of a <code>LocatorPane</code>.
 * <li>{@link #assign(IAST, IExpr, EvalEngine)} - what happens when the user moves a control that
 * was written <code>Slider[Dynamic[x]]</code>.
 * </ul>
 */
public class Dynamics {

  /** How deep {@link #resolve(IExpr, EvalEngine)} follows a value that is itself dynamic. */
  private static final int MAX_RESOLVE_DEPTH = 8;

  private Dynamics() {}

  // ---------------------------------------------------------------- shape

  /** Whether <code>expr</code> is a <code>Dynamic[...]</code> with at least one argument. */
  public static boolean isDynamic(IExpr expr) {
    return expr.isAST(S.Dynamic) && expr.size() >= 2;
  }

  /**
   * The held expression of a <code>Dynamic</code>, or the argument itself when it is not one.
   *
   * <p>
   * This is the rule for every position where the wrapper carries no information a renderer can
   * use: <code>ViewPoint -&gt; Dynamic[vp]</code> is the view point <code>vp</code> names, and
   * <code>Line[Dynamic[{p, q}]]</code> is the line through the points that list currently holds.
   * Without it, <code>Dynamic</code> being held means the option silently falls back to its default
   * and the primitive reads as malformed.
   */
  public static IExpr release(IExpr expr) {
    return isDynamic(expr) ? ((IAST) expr).arg1() : expr;
  }

  /** Whether a <code>Dynamic</code> appears anywhere inside <code>expr</code>. */
  public static boolean containsDynamic(IExpr expr) {
    if (isDynamic(expr)) {
      return true;
    }
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    for (int i = 0; i < ast.size(); i++) {
      if (containsDynamic(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  // ---------------------------------------------------------------- controls

  /**
   * The heads that turn a <code>Dynamic</code> into something the user can operate rather than
   * something to look at.
   *
   * <p>
   * The distinction runs through everything below. <code>Dynamic[Point[{x, y}]]</code> shows where
   * the point is; <code>Point[Dynamic[{x, y}]]</code> - the same two heads the other way round -
   * lets the user drag it. So a <code>Dynamic</code> that is the first argument of one of these is
   * the control's connection to its variable and has to survive intact, while one anywhere else is
   * only a wrapper around something to display.
   */
  private static final IBuiltInSymbol[] CONTROL_HEADS = {S.Slider, S.Slider2D, S.IntervalSlider,
      S.VerticalSlider, S.Manipulator, S.Checkbox, S.Toggler, S.TogglerBar, S.CheckboxBar,
      S.SetterBar, S.RadioButton, S.RadioButtonBar, S.PopupMenu, S.ActionMenu, S.Setter, S.Trigger,
      S.Animator, S.InputField, S.ColorSetter, S.ColorSlider, S.ProgressIndicator, S.Locator,
      S.LocatorPane, S.Opener, S.ButtonBar, S.PaneSelector, S.FileNameSetter};

  /** Whether this head makes its first argument a control rather than a display. */
  public static boolean isControlHead(IExpr head) {
    for (IBuiltInSymbol candidate : CONTROL_HEADS) {
      if (candidate == head) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether <code>expr</code> is a control bound to a <code>Dynamic</code>, such as
   * <code>Slider[Dynamic[x]]</code>.
   */
  public static boolean isDynamicControl(IExpr expr) {
    return expr.isAST() && expr.size() >= 2 && isControlHead(expr.head())
        && isDynamic(((IAST) expr).arg1());
  }

  /**
   * Whether the first argument of this control is where it writes, and so has to be left alone.
   *
   * <p>
   * Usually that is a plain <code>Dynamic</code>. A <code>LocatorPane</code> is regularly written
   * <code>LocatorPane[p = f[a]; Dynamic[p, setter], body]</code>, computing the position from other
   * controls before handing it over - the statements are run for their effect and the
   * <code>Dynamic</code> at the end is still the write target, so that form has to survive too.
   */
  private static boolean holdsControlTarget(IAST call) {
    if (!isControlHead(call.head()) || call.size() < 2) {
      return false;
    }
    IExpr first = call.arg1();
    if (isDynamic(first)) {
      return true;
    }
    return first.isAST(S.CompoundExpression) && first.size() >= 2
        && isDynamic(((IAST) first).last());
  }

  /**
   * Take the <code>Dynamic</code> wrappers out of an expression that is about to be evaluated,
   * leaving the controls alone.
   *
   * <p>
   * Inside something that is re-evaluated as a whole - the body of a <code>Manipulate</code>, a
   * dynamic cell - a <code>Dynamic</code> around a displayed value carries no information: the
   * re-evaluation <em>is</em> the update. Dropping the wrapper before the evaluation rather than
   * after it is what makes it work in the places where the wrapper would otherwise be read as data:
   * <code>Plot3D[..., ViewPoint -&gt; Dynamic[vp]]</code> has to see the view point while it draws,
   * and <code>Line[Dynamic[{p, q}]]</code> has to see two points rather than an unknown head.
   *
   * <p>
   * Controls are the exception, and keep their <code>Dynamic</code>: that is the only record of
   * which variable the user is about to write to.
   */
  public static IExpr releaseAll(IExpr expr) {
    if (!expr.isAST()) {
      return expr;
    }
    if (isDynamic(expr)) {
      // the setter functions describe editing, not display, and go away with the wrapper
      return releaseAll(((IAST) expr).arg1());
    }
    IAST ast = (IAST) expr;
    boolean keepFirst = holdsControlTarget(ast);
    IASTMutable copy = F.NIL;
    for (int i = 0; i < ast.size(); i++) {
      if (keepFirst && i == 1) {
        continue;
      }
      IExpr child = releaseAll(ast.get(i));
      if (child != ast.get(i)) {
        if (!copy.isPresent()) {
          copy = ast.copy();
        }
        copy.set(i, child);
      }
    }
    return copy.isPresent() ? copy : expr;
  }

  // ---------------------------------------------------------------- display

  /**
   * The expression to show right now: every <code>Dynamic</code> inside <code>expr</code> replaced
   * by the current value of what it holds.
   *
   * <p>
   * Only the displayed expression - the first argument - is evaluated. The setter functions of a
   * two or three argument <code>Dynamic</code> describe what to do when the user edits the value
   * and must stay held; see {@link #assign(IAST, IExpr, EvalEngine)}.
   *
   * <p>
   * A value that is itself dynamic is followed, up to a small depth, so that
   * <code>a = Dynamic[b]</code> shows the value of <code>b</code> rather than the wrapper. A
   * <code>Dynamic</code> whose body fails is left as it stands, the way a front end shows a broken
   * dynamic as itself instead of losing the whole rendering.
   *
   * <p>
   * This is the pass for an expression that has already been evaluated - the result a body
   * produced, rather than the body itself. Where the expression is still going to be evaluated,
   * {@link #releaseAll(IExpr)} is the one to use, because it gets the wrapper out of the way before
   * anything reads around it. Controls keep their <code>Dynamic</code> here too: resolving
   * <code>Slider[Dynamic[x]]</code> to <code>Slider[5]</code> would turn a control into a picture
   * of one.
   */
  public static IExpr resolve(IExpr expr, EvalEngine engine) {
    return resolve(expr, engine, MAX_RESOLVE_DEPTH);
  }

  private static IExpr resolve(IExpr expr, EvalEngine engine, int depth) {
    if (depth <= 0 || !expr.isAST()) {
      return expr;
    }
    if (isDynamic(expr)) {
      IExpr value = evaluateQuietly(((IAST) expr).arg1(), engine);
      if (!value.isPresent()) {
        return expr;
      }
      return resolve(value, engine, depth - 1);
    }
    if (expr.isAST(S.PaneSelector) && expr.size() >= 3) {
      IExpr pane = selectedPane((IAST) expr, engine);
      if (pane.isPresent()) {
        return resolve(pane, engine, depth - 1);
      }
      return expr;
    }
    IAST ast = (IAST) expr;
    boolean keepFirst = isDynamicControl(ast);
    IASTMutable copy = F.NIL;
    for (int i = 0; i < ast.size(); i++) {
      if (keepFirst && i == 1) {
        continue;
      }
      IExpr child = resolve(ast.get(i), engine, depth);
      if (child != ast.get(i)) {
        if (!copy.isPresent()) {
          copy = ast.copy();
        }
        copy.set(i, child);
      }
    }
    return copy.isPresent() ? copy : expr;
  }

  /**
   * The pane a <code>PaneSelector[{v1 -&gt; e1, ...}, sel]</code> is currently showing.
   *
   * <p>
   * The selector is evaluated and matched against each pane's value. A <code>PaneSelector</code>
   * keeps every pane, and shows exactly one of them: which is why it is resolved here rather than
   * left to be rendered, and why the panes that are not showing simply do not appear.
   *
   * <p>
   * When no pane matches, the third argument is the default; without one, so the answer is the
   * empty string rather than the expression - a selector that has moved off the end of its panes
   * should leave a gap, not print its own source.
   *
   * @return the pane, or {@link F#NIL} when the expression is not a well formed selector
   */
  public static IExpr selectedPane(IAST paneSelector, EvalEngine engine) {
    if (!paneSelector.arg1().isList()) {
      return F.NIL;
    }
    IExpr selector = evaluateQuietly(release(paneSelector.arg2()), engine);
    if (!selector.isPresent()) {
      return F.NIL;
    }
    IAST panes = (IAST) paneSelector.arg1();
    for (int i = 1; i < panes.size(); i++) {
      IExpr pane = panes.get(i);
      if ((pane.isRule() || pane.isRuleDelayed())
          && evaluateQuietly(((IAST) pane).arg1(), engine).equals(selector)) {
        return ((IAST) pane).arg2();
      }
    }
    // a third argument is the default pane; a rule there is an option, not a pane
    if (paneSelector.size() >= 4 && !paneSelector.arg3().isRule()
        && !paneSelector.arg3().isRuleDelayed()) {
      return paneSelector.arg3();
    }
    return F.stringx("");
  }

  /**
   * Evaluate an expression for display. A rendering must survive a body that throws, so a failure
   * answers {@link F#NIL} and the caller keeps what it had.
   */
  private static IExpr evaluateQuietly(IExpr expr, EvalEngine engine) {
    try {
      return engine.evaluate(expr);
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  // ---------------------------------------------------------------- editing

  /**
   * Whether the user may edit through this <code>Dynamic</code>.
   *
   * <p>
   * <code>Dynamic[expr, None]</code> is explicitly read only. Everything else is editable: either
   * by assigning to what it holds, or by running the function it was given.
   */
  public static boolean isSettable(IAST dynamic) {
    return !(dynamic.size() == 3 && dynamic.arg2() == S.None);
  }

  /**
   * The expression a control writes to - the first argument, which is held and so is still the
   * symbol or part the user wrote rather than its value.
   */
  public static IExpr target(IAST dynamic) {
    return dynamic.arg1();
  }

  /**
   * The function to run while the user is editing, or {@link F#NIL} when the value is simply
   * assigned.
   *
   * <p>
   * <code>Dynamic[expr, f]</code> gives one, <code>Dynamic[expr, {f, fend}]</code> gives the same
   * one for the duration of the interaction, and <code>Dynamic[expr, {fstart, f, fend}]</code> puts
   * it in the middle.
   */
  public static IExpr setter(IAST dynamic) {
    return setterAt(dynamic, 1);
  }

  /** The function to run when the interaction starts, or {@link F#NIL}. */
  public static IExpr startSetter(IAST dynamic) {
    return setterAt(dynamic, 0);
  }

  /** The function to run when the interaction ends, or {@link F#NIL}. */
  public static IExpr endSetter(IAST dynamic) {
    return setterAt(dynamic, 2);
  }

  /**
   * One of the three interaction functions.
   *
   * @param slot <code>0</code> for the start, <code>1</code> for during, <code>2</code> for the end
   *        of the interaction
   */
  private static IExpr setterAt(IAST dynamic, int slot) {
    if (dynamic.size() < 3 || dynamic.arg2() == S.None) {
      return F.NIL;
    }
    IExpr second = dynamic.arg2();
    if (!second.isList()) {
      // Dynamic[expr, f]: f runs while the value is being changed and nothing else does
      return slot == 1 ? second : F.NIL;
    }
    IAST functions = (IAST) second;
    switch (functions.argSize()) {
      case 2:
        // {f, fend}
        return slot == 0 ? F.NIL : functions.get(slot);
      case 3:
        // {fstart, f, fend}
        return functions.get(slot + 1);
      case 1:
        return slot == 1 ? functions.arg1() : F.NIL;
      default:
        return F.NIL;
    }
  }

  /**
   * Write a new value through a <code>Dynamic</code> and report what it actually became.
   *
   * <p>
   * A control never simply owns its value. <code>Dynamic[x]</code> assigns, but
   * <code>Dynamic[x, (x = Max[0, #]) &amp;]</code> runs a function that may clamp the value, round
   * it, or refuse the change altogether - so the answer is read back from the target rather than
   * assumed to be what was sent. That read back is what lets a control snap to a legal position
   * instead of drifting away from the value the session actually holds.
   *
   * @param dynamic the <code>Dynamic</code> the control was built from
   * @param value the value the user produced
   * @return the value the target holds afterwards, or {@link F#NIL} when nothing was written
   */
  public static IExpr assign(IAST dynamic, IExpr value, EvalEngine engine) {
    if (!isSettable(dynamic)) {
      return F.NIL;
    }
    IExpr target = target(dynamic);
    IExpr function = setter(dynamic);
    try {
      if (function.isPresent()) {
        // f[val, expr] is the documented call; a one argument pure function - by far the
        // commonest way it is written - takes the value alone
        engine.evaluate(F.binaryAST2(function, value, target));
      } else {
        engine.evaluate(F.Set(target, value));
      }
      return evaluateQuietly(target, engine);
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  /**
   * Run the function of a <code>Dynamic</code> that belongs to the start or the end of an
   * interaction, if it has one.
   *
   * @param slot <code>0</code> for the start, <code>2</code> for the end
   */
  public static void runInteractionEdge(IAST dynamic, IExpr value, int slot, EvalEngine engine) {
    IExpr function = setterAt(dynamic, slot);
    if (!function.isPresent()) {
      return;
    }
    try {
      engine.evaluate(F.binaryAST2(function, value, target(dynamic)));
    } catch (RuntimeException rex) {
      // an edge callback that fails must not stop the value from being written
    }
  }

  // ---------------------------------------------------------------- tracking

  /**
   * The symbols whose change makes this <code>Dynamic</code> show something else.
   *
   * <p>
   * <code>TrackedSymbols :&gt; {...}</code> names them outright. Otherwise every symbol the
   * displayed expression mentions is tracked, which is what <code>TrackedSymbols -&gt; All</code>
   * means: the front end has no way to know which of them the value really depended on, so it
   * watches all of them.
   */
  public static Set<String> trackedSymbols(IAST dynamic) {
    Set<String> names = new LinkedHashSet<String>();
    IExpr tracked = optionValue(dynamic, S.TrackedSymbols);
    if (tracked.isPresent() && tracked != S.All && tracked != S.True && tracked != S.Automatic) {
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
    collectSymbols(dynamic.arg1(), names);
    return names;
  }

  /**
   * Every symbol of <code>expr</code> that a session could assign to.
   *
   * <p>
   * Built in symbols are skipped: <code>Dynamic[Sin[x]]</code> is watching <code>x</code>, and
   * treating <code>Sin</code> as a dependency would make every dynamic on the page look like it had
   * changed whenever anything did.
   */
  public static void collectSymbols(IExpr expr, Set<String> names) {
    if (expr.isSymbol()) {
      if (!(expr instanceof IBuiltInSymbol)) {
        names.add(((ISymbol) expr).getSymbolName());
      }
      return;
    }
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    for (int i = 0; i < ast.size(); i++) {
      collectSymbols(ast.get(i), names);
    }
  }

  /**
   * How often a front end should re evaluate this <code>Dynamic</code> even when nothing it tracks
   * has changed, in seconds.
   *
   * @return the interval, or {@link Double#POSITIVE_INFINITY} for the default of only updating on a
   *         change
   */
  public static double updateInterval(IAST dynamic) {
    IExpr interval = optionValue(dynamic, S.UpdateInterval);
    if (!interval.isPresent()) {
      return Double.POSITIVE_INFINITY;
    }
    return ManipulateControl.toDouble(interval, Double.POSITIVE_INFINITY);
  }

  /**
   * <code>SynchronousUpdating -&gt; False</code> lets the page carry on while the value is built.
   */
  public static boolean isSynchronous(IAST dynamic) {
    return !optionValue(dynamic, S.SynchronousUpdating).isFalse();
  }

  /** The <code>Initialization :&gt; ...</code> code of a <code>Dynamic</code>, or {@link F#NIL}. */
  public static IExpr initialization(IAST dynamic) {
    return optionValue(dynamic, S.Initialization);
  }

  /**
   * The <code>Deinitialization :&gt; ...</code> code of a <code>Dynamic</code>, or {@link F#NIL}.
   */
  public static IExpr deinitialization(IAST dynamic) {
    return optionValue(dynamic, S.Deinitialization);
  }

  /**
   * The value of one option of a <code>Dynamic</code>.
   *
   * <p>
   * The options sit among the arguments rather than after a fixed number of them, because the
   * second argument may be a setter function or may already be an option; a rule with the name we
   * are looking for is the only reliable marker.
   */
  public static IExpr optionValue(IAST dynamic, ISymbol name) {
    for (int i = 2; i < dynamic.size(); i++) {
      IExpr arg = dynamic.get(i);
      if ((arg.isRule() || arg.isRuleDelayed()) && ((IAST) arg).arg1() == name) {
        return ((IAST) arg).arg2();
      }
      if (arg.isList()) {
        IAST list = (IAST) arg;
        for (int j = 1; j < list.size(); j++) {
          IExpr option = list.get(j);
          if ((option.isRule() || option.isRuleDelayed()) && ((IAST) option).arg1() == name) {
            return ((IAST) option).arg2();
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * The names of the options a <code>Dynamic</code> understands, for reporting the ones it does
   * not.
   */
  private static final ISymbol[] KNOWN_OPTIONS =
      {S.TrackedSymbols, S.UpdateInterval, S.SynchronousUpdating, S.BaseStyle, S.Initialization,
          S.Deinitialization, S.ShrinkingDelay, S.CachedValue};

  /** The options of this <code>Dynamic</code> that are not understood, in the order written. */
  public static List<String> unknownOptions(IAST dynamic) {
    List<String> unknown = new ArrayList<String>();
    for (int i = 2; i < dynamic.size(); i++) {
      IExpr arg = dynamic.get(i);
      if (!(arg.isRule() || arg.isRuleDelayed()) || !((IAST) arg).arg1().isSymbol()) {
        continue;
      }
      ISymbol name = (ISymbol) ((IAST) arg).arg1();
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
}
