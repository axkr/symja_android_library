package org.matheclipse.core.manipulate;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The interactive parts of an expression that is about to be shown: the buttons the user can press
 * and the controls the user can move.
 *
 * <p>
 * A rendering is a picture. Nothing in it can hold a piece of Symja code, and nothing in it should:
 * an action that travelled to the browser as text and came back again would be a way to run
 * anything at all in someone else's session. So the code stays here, on the server, and what goes
 * out in its place is a number - the position of the button or control in this rendering. When one
 * comes back, that number is the whole message.
 *
 * <p>
 * The rewritten expression is what gets rendered: <code>Button["reset", k = 0]</code> becomes
 * <code>Button["reset", 3]</code>, and <code>Slider[Dynamic[x], {0, 10}]</code> becomes
 * <code>Slider[2]</code>. Both are turned into real HTML by the MathML output, which recognises
 * that lone integer as a position rather than an argument.
 */
public class Interactions {

  private final EvalEngine engine;

  /**
   * The local values a control's variable should be read against, as a <code>Block</code> variable
   * list, or {@link F#NIL} to read the session's own.
   */
  private final IExpr scope;

  private final List<IExpr> actions = new ArrayList<IExpr>();

  private final List<ManipulateControl> controls = new ArrayList<ManipulateControl>();

  /**
   * @param engine the engine whose values the controls should start at - a control object is
   *        rebuilt with every frame, so it starts wherever its variable stands now
   */
  public Interactions(EvalEngine engine) {
    this(engine, F.NIL);
  }

  /**
   * @param scope the <code>Block</code> variable list the frame was rendered with, so that a
   *        control pointing at a variable of the surrounding widget starts at the value that frame
   *        used rather than at whatever the session happens to hold outside it
   */
  public Interactions(EvalEngine engine, IExpr scope) {
    this.engine = engine;
    this.scope = scope;
  }

  /** The held action of every <code>Button</code> found, in the order they were numbered. */
  public List<IExpr> getActions() {
    return actions;
  }

  /** Every control object found, in the order they were numbered. */
  public List<ManipulateControl> getControls() {
    return controls;
  }

  public boolean isEmpty() {
    return actions.isEmpty() && controls.isEmpty();
  }

  /**
   * Take the interactive parts out of <code>expr</code> and put their positions in their place.
   *
   * @return the expression to render, which is <code>expr</code> itself when it holds nothing
   *         interactive
   */
  public IExpr rewrite(IExpr expr) {
    if (!expr.isAST()) {
      return expr;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.Button, 3)) {
      actions.add(ast.arg2());
      return F.binaryAST2(ast.head(), ast.arg1(), F.ZZ(actions.size() - 1));
    }
    if (ast.isAST(S.LocatorPane) && ast.size() >= 3) {
      IExpr pane = rewriteLocatorPane(ast);
      if (pane.isPresent()) {
        return pane;
      }
      // a pane whose body is not a picture falls through and is rendered as it stands
    }
    if (Dynamics.isDynamicControl(ast)) {
      ManipulateControl control = toControl(ast);
      if (control != null) {
        controls.add(control);
        // the position keeps the Dynamic it replaces, so that the marker cannot be confused
        // with a control object nobody is driving - Slider[0.5] is a legal static slider
        return F.unaryAST1(ast.head(), F.unaryAST1(S.Dynamic, F.ZZ(controls.size() - 1)));
      }
      // a control this implementation cannot show falls through and is rendered as it stands
    }
    IASTMutable copy = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr child = rewrite(ast.get(i));
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
   * A <code>LocatorPane[locators, body]</code> as the two things it is: the body with a marker on
   * every locator, and underneath it the control that moves them.
   *
   * <p>
   * They are stacked in a <code>Column</code> because both have to be on screen at once and the
   * picture is the thing the user is looking at, so the control belongs below it rather than beside
   * it.
   *
   * @return the pair, or {@link F#NIL} when the pane cannot be shown - which is the case when its
   *         body is not a picture, since there would then be nothing to put markers on
   */
  private IExpr rewriteLocatorPane(IAST pane) {
    IExpr value = LocatorPanes.positions(pane.arg1(), scope, engine);
    if (!value.isPresent()) {
      return F.NIL;
    }
    List<double[]> points = LocatorPanes.points(value);
    IExpr composed = LocatorPanes.graphic(pane, points, engine);
    if (!composed.isPresent()) {
      return F.NIL;
    }
    // only a pane pointed at a Dynamic can be moved; one showing a fixed list of points is a
    // picture, and gets its markers without a control
    IAST dynamic = Dynamics.isDynamic(pane.arg1()) ? (IAST) pane.arg1() : null;
    if (dynamic == null && pane.arg1().isAST(S.CompoundExpression)
        && Dynamics.isDynamic(((IAST) pane.arg1()).last())) {
      dynamic = (IAST) ((IAST) pane.arg1()).last();
    }
    if (dynamic == null) {
      return composed;
    }
    ManipulateControl control = LocatorPanes.control(pane, "$c" + controls.size(), dynamic, points,
        LocatorPanes.isPoint(value), composed);
    controls.add(control);
    return F.unaryAST1(S.Column, F.list(composed,
        F.unaryAST1(S.LocatorPane, F.unaryAST1(S.Dynamic, F.ZZ(controls.size() - 1)))));
  }

  private ManipulateControl toControl(IAST call) {
    IAST dynamic = (IAST) call.arg1();
    IExpr target = Dynamics.release(dynamic);
    IExpr current;
    try {
      current = engine.evaluate(scope.isList() ? F.Block(scope, target) : target);
    } catch (RuntimeException rex) {
      current = F.NIL;
    }
    return ControlObject.parse(call, "$c" + controls.size(), current);
  }
}
