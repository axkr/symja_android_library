package org.matheclipse.core.manipulate;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>LocatorPane[locators, body]</code> - a picture with points the user can move on it.
 *
 * <p>
 * A locator pane is two things at once, and both have to be produced from the one expression: a
 * graphic, which is the body with a marker drawn wherever a locator currently is, and a control,
 * which is what moves them. Neither is any use without the other - a pane with no markers does not
 * show where its points are, and a pane with no control cannot be operated.
 *
 * <p>
 * The markers are ordinary graphics primitives added to the body's own, so they are drawn by the
 * same renderer and inherit the body's plot range, axes and frame. Their size is a fraction of that
 * range rather than a fixed number of pixels, so a pane over <code>{{0, 1}, {0, 1}}</code> and one
 * over <code>{{0, 1000}, {0, 1000}}</code> look the same.
 */
public class LocatorPanes {

  /** The radius of the default marker, as a fraction of the longer side of the plot range. */
  private static final double MARKER_RADIUS = 0.015;

  /** The half length of the default marker's crosshair arms, in the same units. */
  private static final double MARKER_ARM = 0.03;

  private LocatorPanes() {}

  /**
   * Where the locators of a pane currently are.
   *
   * <p>
   * <code>Dynamic[p]</code> reads <code>p</code>, and so does <code>Dynamic[p, setter]</code> - the
   * setter is what runs when a locator is dragged and says nothing about where it is now. A
   * demonstration often writes the whole thing as <code>p = ...; Dynamic[p, setter]</code>,
   * computing the position from its sliders first, so a leading statement is evaluated for its
   * effect before the last one is read.
   *
   * @param scope the <code>Block</code> variable list of the frame, or {@link F#NIL}
   */
  public static IExpr positions(IExpr locators, IExpr scope, EvalEngine engine) {
    if (locators.isAST(S.CompoundExpression) && locators.size() >= 2) {
      IAST compound = (IAST) locators;
      for (int i = 1; i < compound.argSize(); i++) {
        evaluateQuietly(compound.get(i), scope, engine);
      }
      return positions(compound.last(), scope, engine);
    }
    return evaluateQuietly(Dynamics.release(locators), scope, engine);
  }

  private static IExpr evaluateQuietly(IExpr expr, IExpr scope, EvalEngine engine) {
    try {
      return engine.evaluate(scope.isList() ? F.Block(scope, expr) : expr);
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  /** The points a locator value stands for: one point binds one, a list of them binds each. */
  public static List<double[]> points(IExpr value) {
    List<double[]> points = new ArrayList<double[]>();
    if (isPoint(value)) {
      IAST point = (IAST) value;
      points.add(new double[] {ManipulateControl.toDouble(point.arg1(), 0.0),
          ManipulateControl.toDouble(point.arg2(), 0.0)});
      return points;
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      for (int i = 1; i < list.size(); i++) {
        if (isPoint(list.get(i))) {
          IAST point = (IAST) list.get(i);
          points.add(new double[] {ManipulateControl.toDouble(point.arg1(), 0.0),
              ManipulateControl.toDouble(point.arg2(), 0.0)});
        }
      }
    }
    return points;
  }

  /** Whether the value is one <code>{x, y}</code> rather than a list of them. */
  public static boolean isPoint(IExpr value) {
    return value.isList2() && !value.first().isList();
  }

  /**
   * The body of a pane with a marker drawn at every locator.
   *
   * <p>
   * The body's options are kept, so the pane inherits the plot range, axes and grid lines the body
   * asked for - and so the markers, which are placed in the body's own coordinates, land where the
   * body's contents are.
   *
   * @return the composed graphic, or {@link F#NIL} when the body is not one
   */
  public static IExpr graphic(IAST pane, List<double[]> points, EvalEngine engine) {
    IExpr body = Dynamics.releaseAll(pane.arg2());
    try {
      body = engine.evaluate(body);
    } catch (RuntimeException rex) {
      return F.NIL;
    }
    if (!body.isAST(S.Graphics) || body.size() < 2) {
      return F.NIL;
    }
    IAST graphics = (IAST) body;

    IASTAppendable primitives = F.ListAlloc(points.size() + 1);
    primitives.append(graphics.arg1());
    IExpr appearance = optionOf(pane, S.Appearance);
    double span = span(graphics, points);
    for (int i = 0; i < points.size(); i++) {
      IExpr marker = marker(appearance, i, points.get(i), span);
      if (marker.isPresent()) {
        primitives.append(marker);
      }
    }

    IASTAppendable composed = F.ast(S.Graphics, graphics.size());
    composed.append(primitives);
    for (int i = 2; i < graphics.size(); i++) {
      composed.append(graphics.get(i));
    }
    return composed;
  }

  /**
   * One locator's marker.
   *
   * <p>
   * <code>Appearance -&gt; {g1, g2, ...}</code> gives one picture per locator, each drawn about the
   * origin, so the marker is that picture moved onto the point - which is how a demonstration
   * labels the vertices of a shape it lets you drag. A single picture is used for every locator,
   * <code>None</code> draws nothing, and with no <code>Appearance</code> at all the marker is a
   * small circle with a crosshair through it.
   */
  private static IExpr marker(IExpr appearance, int index, double[] point, double span) {
    IExpr at = F.list(F.num(point[0]), F.num(point[1]));
    if (appearance.isPresent()) {
      if (appearance == S.None) {
        return F.NIL;
      }
      if (appearance.isList() && appearance.size() >= 2) {
        IAST pictures = (IAST) appearance;
        IExpr picture = pictures.get(1 + (index % pictures.argSize()));
        return F.binaryAST2(S.Translate, primitivesOf(picture), at);
      }
      return F.binaryAST2(S.Translate, primitivesOf(appearance), at);
    }
    double radius = span * MARKER_RADIUS;
    double arm = span * MARKER_ARM;
    return F.list( //
        F.binaryAST2(S.Circle, at, F.num(radius)), //
        F.unaryAST1(S.Line, F.list(offset(point, -arm, 0.0), offset(point, arm, 0.0))), //
        F.unaryAST1(S.Line, F.list(offset(point, 0.0, -arm), offset(point, 0.0, arm))));
  }

  private static IExpr offset(double[] point, double dx, double dy) {
    return F.list(F.num(point[0] + dx), F.num(point[1] + dy));
  }

  /** The drawable part of an <code>Appearance</code> picture. */
  private static IExpr primitivesOf(IExpr picture) {
    if (picture.isAST(S.Graphics) && picture.size() >= 2) {
      return ((IAST) picture).arg1();
    }
    return picture;
  }

  /**
   * The size the default marker is measured against: the longer side of the plot range, or - when
   * the body does not give one - the spread of the locators themselves, so that a pane over large
   * coordinates does not get a marker too small to see.
   */
  private static double span(IAST graphics, List<double[]> points) {
    IExpr plotRange = optionOf(graphics, S.PlotRange);
    if (plotRange.isList2() && plotRange.first().isList2()) {
      IAST axes = (IAST) plotRange;
      double x = extent(axes.arg1());
      double y = extent(axes.arg2());
      double longer = Math.max(x, y);
      if (longer > 0.0) {
        return longer;
      }
    }
    double lowX = Double.MAX_VALUE, highX = -Double.MAX_VALUE;
    double lowY = Double.MAX_VALUE, highY = -Double.MAX_VALUE;
    for (double[] point : points) {
      lowX = Math.min(lowX, point[0]);
      highX = Math.max(highX, point[0]);
      lowY = Math.min(lowY, point[1]);
      highY = Math.max(highY, point[1]);
    }
    double spread = Math.max(highX - lowX, highY - lowY);
    return spread > 0.0 ? spread : 1.0;
  }

  private static double extent(IExpr axis) {
    if (!axis.isList2()) {
      return 0.0;
    }
    IAST bounds = (IAST) axis;
    return Math.abs(ManipulateControl.toDouble(bounds.arg2(), 0.0)
        - ManipulateControl.toDouble(bounds.arg1(), 0.0));
  }

  /**
   * The control that moves a pane's locators.
   *
   * <p>
   * The rectangle the points may be moved in is the body's plot range, because that is the part of
   * the plane the pane is showing and a point outside it could not be seen. When the body gives no
   * plot range the box is grown around the points instead, so that each one still has room to move
   * on every side.
   *
   * @param single whether the variable holds one point rather than a list of them - the binding has
   *        to mirror the shape the user wrote, so that a body written for a single point keeps
   *        working
   */
  public static ManipulateControl control(IAST pane, String name, IAST dynamic,
      List<double[]> points, boolean single, IExpr composed) {
    ManipulateControl control = new ManipulateControl(ManipulateControl.LOCATOR, null);
    control.setName(name);
    control.setDynamic(dynamic);
    control.setLabel("");
    control.setSinglePoint(single);
    for (double[] point : points) {
      control.addPoint(point[0], point[1]);
    }
    if (points.isEmpty()) {
      control.setSinglePoint(true);
      control.addPoint(0.0, 0.0);
    }

    double[] box = plotRange(composed);
    if (box != null) {
      control.setRange(box[0], box[1], Double.NaN);
      control.setRangeY(box[2], box[3]);
    } else {
      control.setRange(0.0, 1.0, Double.NaN);
      control.setRangeY(0.0, 1.0);
      control.growToFitPoints();
    }
    if (!Dynamics.isSettable(dynamic)) {
      control.setReadOnly(true);
    }
    IExpr autoCreate = optionOf(pane, S.LocatorAutoCreate);
    if (autoCreate.isPresent()) {
      control.setAutoCreate(!autoCreate.isFalse());
    }
    return control;
  }

  /** The <code>{xmin, xmax, ymin, ymax}</code> of a graphic's plot range, or <code>null</code>. */
  private static double[] plotRange(IExpr composed) {
    IExpr plotRange = composed.isAST() ? optionOf((IAST) composed, S.PlotRange) : F.NIL;
    if (plotRange.isList2() && plotRange.first().isList2()
        && ((IAST) plotRange).arg2().isList2()) {
      IAST axes = (IAST) plotRange;
      IAST x = (IAST) axes.arg1();
      IAST y = (IAST) axes.arg2();
      return new double[] {ManipulateControl.toDouble(x.arg1(), 0.0),
          ManipulateControl.toDouble(x.arg2(), 1.0), ManipulateControl.toDouble(y.arg1(), 0.0),
          ManipulateControl.toDouble(y.arg2(), 1.0)};
    }
    return null;
  }

  private static IExpr optionOf(IAST ast, IExpr name) {
    for (int i = 2; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if ((arg.isRule() || arg.isRuleDelayed()) && ((IAST) arg).arg1() == name) {
        return ((IAST) arg).arg2();
      }
    }
    return F.NIL;
  }
}
