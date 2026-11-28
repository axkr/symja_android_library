package org.matheclipse.core.builtin;

import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.graphics.svg.SvgGraphics2D;
import org.matheclipse.core.graphics.svg.TickGenerator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Graphics directives, primitives and colour tests which the renderers read:
 *
 * <ul>
 * <li>the inert directives <code>Haloing</code>, <code>HatchFilling</code>,
 * <code>LightDarkSwitched</code>, <code>ThemeColor</code>, <code>SystemColor</code> and the marker
 * <code>ImageMarker</code>;
 * <li>the inert primitives <code>AxisObject</code>, <code>PolarCurve</code>,
 * <code>FilledPolarCurve</code> and <code>Raster3D</code>, which the SVG and WebGL collectors
 * rewrite into the primitives that draw them;
 * <li><code>DropShadowing</code> and <code>LinearGradientFilling</code>, which complete their
 * arguments with the defaults;
 * <li><code>ColorQ(expr)</code>;
 * <li><code>FullGraphics(g)</code>, which writes the axes, frame and ticks of <code>g</code> as
 * primitives.
 * </ul>
 */
public final class GraphicsDirectiveFunctions {

  /** The heads without an evaluation of their own. */
  private static final IBuiltInSymbol[] INERT_HEADS = {S.Haloing, S.HatchFilling,
      S.LightDarkSwitched, S.ThemeColor, S.SystemColor, S.ImageMarker, S.AxisObject, S.Raster3D};

  /** The heads whose definitions are hidden from <code>Definition</code> as well. */
  private static final IBuiltInSymbol[] READ_PROTECTED = {S.PolarCurve, S.FilledPolarCurve,
      S.Raster3D, S.Haloing, S.DropShadowing, S.LinearGradientFilling, S.HatchFilling,
      S.FullGraphics, S.ImageMarker, S.ThemeColor};

  private static class Initializer {

    private static void init() {
      Inert inert = new Inert();
      for (IBuiltInSymbol head : INERT_HEADS) {
        head.setEvaluator(inert);
      }
      S.DropShadowing.setEvaluator(new DropShadowing());
      S.LinearGradientFilling.setEvaluator(new LinearGradientFilling());
      S.ColorQ.setPredicateQ(GraphicsDirectiveFunctions::isColor);
      S.FullGraphics.setEvaluator(new FullGraphics());
      S.PolarCurve.setEvaluator(new PolarCurve(false));
      S.FilledPolarCurve.setEvaluator(new PolarCurve(true));
      for (IBuiltInSymbol head : READ_PROTECTED) {
        head.addAttributes(ISymbol.READPROTECTED);
      }
    }
  }

  /**
   * <code>PolarCurve(r, {t, tmin, tmax})</code> and <code>FilledPolarCurve(r, {t, tmin, tmax})</code>
   * or <code>FilledPolarCurve(r, t)</code>: primitives without an evaluation of their own, which
   * only check that the variable is a symbol.
   */
  private static final class PolarCurve extends AbstractFunctionEvaluator {
    private final boolean filled;

    PolarCurve(boolean filled) {
      this.filled = filled;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr range = ast.arg2();
      IExpr variable = range.isList() && range.argSize() >= 1 ? range.first()
          : filled ? range : F.NIL;
      if (variable.isPresent() && !variable.isVariable()) {
        // `1` is not a valid variable.
        Errors.printMessage(ast.topHead(), "ivar", F.list(variable), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** A head without evaluation of its own. */
  private static final class Inert extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>ColorQ(expr)</code>: whether <code>expr</code> is a colour directive with numeric
   * channels, in one of the colour spaces <code>RGBColor</code>, <code>Hue</code>,
   * <code>GrayLevel</code>, <code>CMYKColor</code>, <code>XYZColor</code>, <code>LABColor</code>,
   * <code>LCHColor</code> and <code>LUVColor</code>, or a named colour which stays a symbol such as
   * <code>Transparent</code>. The colours which depend on the background a picture is shown on
   * (<code>LightDarkSwitched</code>, <code>ThemeColor</code>, <code>SystemColor</code>) aren't
   * colours of their own.
   */
  public static boolean isColor(IExpr expr) {
    if (expr.isBuiltInSymbol()) {
      return ColorUtil.named((IBuiltInSymbol) expr) != null;
    }
    if (!expr.isAST() || !expr.head().isBuiltInSymbol()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (ast.argSize() == 1 && ast.arg1().isList()) {
      // the channels packed into one list
      ast = ((IAST) ast.arg1()).apply(ast.head());
    }
    int n = ast.argSize();
    switch (((IBuiltInSymbol) ast.head()).ordinal()) {
      case ID.RGBColor:
      case ID.XYZColor:
      case ID.LABColor:
      case ID.LCHColor:
      case ID.LUVColor:
        return (n == 3 || n == 4) && realChannels(ast);
      case ID.Hue:
        return n >= 1 && n <= 4 && realChannels(ast);
      case ID.GrayLevel:
        return (n == 1 || n == 2) && realChannels(ast);
      case ID.CMYKColor:
        return (n == 4 || n == 5) && realChannels(ast);
      default:
        return false;
    }
  }

  private static boolean realChannels(IAST ast) {
    return ast.forAll(x -> x.isReal() || (x.isNumericFunction(true) && x.isRealResult()));
  }

  /** A colour argument of a directive: one of the colour heads, <code>Opacity</code> or None. */
  private static boolean isColorArgument(IExpr expr) {
    if (expr.isNone()) {
      return true;
    }
    if (expr.isBuiltInSymbol()) {
      return ColorUtil.named((IBuiltInSymbol) expr) != null;
    }
    if (!expr.isAST() || !expr.head().isBuiltInSymbol()) {
      return false;
    }
    switch (((IBuiltInSymbol) expr.head()).ordinal()) {
      case ID.RGBColor:
      case ID.GrayLevel:
      case ID.Hue:
      case ID.CMYKColor:
      case ID.XYZColor:
      case ID.LABColor:
      case ID.LUVColor:
      case ID.LCHColor:
      case ID.Opacity:
      case ID.ThemeColor:
      case ID.SystemColor:
      case ID.LightDarkSwitched:
        return true;
      default:
        return false;
    }
  }

  /**
   * <code>DropShadowing(offset, radius, color)</code> with each argument optional, in this order:
   * <code>{dx, dy}</code> (default <code>{-3, -3}</code>), a number (default <code>2</code>) and a
   * colour or <code>None</code> (default <code>Opacity(1/3, ThemeColor("Foreground"))</code>).
   * Arguments which don't fit stay unevaluated.
   */
  private static final class DropShadowing extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr offset = F.NIL;
      IExpr radius = F.NIL;
      IExpr color = F.NIL;
      for (int i = 1; i <= ast.argSize(); i++) {
        IExpr arg = ast.get(i);
        if (offset.isNIL() && radius.isNIL() && color.isNIL() && arg.isList2()
            && arg.first().isReal() && arg.second().isReal()) {
          offset = arg;
        } else if (radius.isNIL() && color.isNIL() && arg.isReal()) {
          radius = arg;
        } else if (color.isNIL() && isColorArgument(arg)) {
          color = arg;
        } else {
          return F.NIL;
        }
      }
      if (offset.isPresent() && radius.isPresent() && color.isPresent()) {
        // already complete
        return F.NIL;
      }
      return F.ternaryAST3(S.DropShadowing, offset.orElse(F.List(F.CN3, F.CN3)),
          radius.orElse(F.C2),
          color.orElse(F.binaryAST2(S.Opacity, F.QQ(1, 3),
              F.unaryAST1(S.ThemeColor, F.stringx("Foreground")))));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>LinearGradientFilling</code> in the form <code>LinearGradientFilling({p1, ...} -&gt; {c1,
   * ...}, angle, "Fixed")</code>: <code>{c1, c2, ...}</code> spreads the colours evenly from
   * <code>0</code> to <code>1</code>, <code>{{p1, c1}, ...}</code> places them, a single colour fills
   * evenly, and <code>LinearGradientFilling()</code> runs from black to white.
   */
  private static final class LinearGradientFilling extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() > 3) {
        return F.NIL;
      }
      IExpr angle = ast.argSize() >= 2 ? ast.arg2() : F.C0;
      IExpr space = ast.argSize() >= 3 ? ast.arg3() : F.stringx("Fixed");
      if (ast.argSize() == 0) {
        return F.ternaryAST3(S.LinearGradientFilling,
            F.Rule(F.List(F.C0, F.C1), F.List(F.GrayLevel(F.C0), F.GrayLevel(F.C1))), angle,
            space);
      }
      IExpr spec = ast.arg1();
      if (spec.isRuleAST()) {
        return ast.argSize() == 3 ? F.NIL : F.ternaryAST3(S.LinearGradientFilling, spec, angle, space);
      }
      if (!spec.isList() || spec.argSize() == 0) {
        return ast.argSize() == 3 ? F.NIL : F.ternaryAST3(S.LinearGradientFilling, spec, angle, space);
      }
      IAST list = (IAST) spec;
      IASTAppendable positions = F.ListAlloc(list.argSize());
      IASTAppendable colors = F.ListAlloc(list.argSize());
      boolean pairs = list.forAll(x -> x.isList2() && !isColorArgument(x.first()));
      if (pairs) {
        for (IExpr pair : list) {
          positions.append(pair.first());
          colors.append(pair.second());
        }
      } else if (list.argSize() == 1) {
        positions.append(F.C0);
        positions.append(F.C1);
        colors.append(list.arg1());
        colors.append(list.arg1());
      } else {
        int n = list.argSize();
        for (int i = 1; i <= n; i++) {
          positions.append(F.QQ(i - 1, n - 1));
          colors.append(list.get(i));
        }
      }
      return F.ternaryAST3(S.LinearGradientFilling, F.Rule(positions, colors), angle, space);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** The length of a major tick drawn by <code>FullGraphics</code>, in printer's points. */
  private static final double MAJOR_TICK = 4.0;

  /** The length of a minor tick drawn by <code>FullGraphics</code>, in printer's points. */
  private static final double MINOR_TICK = 2.0;

  /**
   * <code>FullGraphics(Graphics(primitives, options))</code>: the picture with its axes, frame,
   * ticks and their labels written as <code>Line</code> and <code>Text</code> primitives, and no
   * options.
   */
  private static final class FullGraphics extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() != 1 || !ast.arg1().isAST(S.Graphics) || ast.arg1().argSize() < 1) {
        return F.NIL;
      }
      IAST graphics = (IAST) ast.arg1();
      double[][] range = SvgGraphics2D.plotRange(graphics);
      if (range == null || range[0][0] >= range[0][1] || range[1][0] >= range[1][1]) {
        return F.NIL;
      }
      IExpr axes = option(graphics, S.Axes);
      IExpr frame = option(graphics, S.Frame);
      IExpr ticks = option(graphics, S.Ticks);
      IExpr frameTicks = option(graphics, S.FrameTicks);
      IExpr axesLabel = option(graphics, S.AxesLabel);
      IExpr frameLabel = option(graphics, S.FrameLabel);
      IExpr axesOrigin = option(graphics, S.AxesOrigin);
      boolean[] axisOn = sides(axes, 2);
      boolean[] frameOn = sides(frame, 4);

      IASTAppendable extra = F.ListAlloc();
      double xMin = range[0][0];
      double xMax = range[0][1];
      double yMin = range[1][0];
      double yMax = range[1][1];
      double originX = xMin <= 0 && 0 <= xMax ? 0.0 : xMin;
      double originY = yMin <= 0 && 0 <= yMax ? 0.0 : yMin;
      if (axesOrigin.isList2()) {
        double ox = axesOrigin.first().evalfNaN();
        double oy = axesOrigin.second().evalfNaN();
        if (Double.isFinite(ox)) {
          originX = ox;
        }
        if (Double.isFinite(oy)) {
          originY = oy;
        }
      }
      boolean axisTicks = !ticks.isNone() && !ticks.isFalse();
      boolean frameTicksOn = !frameTicks.isNone() && !frameTicks.isFalse();
      if (axisOn[0]) {
        extra.append(F.GrayLevel(F.C0));
        extra.append(F.Line(F.List(point(xMin, originY), point(xMax, originY))));
        if (axisTicks) {
          ticks(extra, xMin, xMax, true, originY, originX);
        }
        IExpr label = labelOf(axesLabel, 1);
        if (label.isPresent()) {
          extra.append(F.Text(label, point(xMax, originY), F.List(F.CN1, F.C0)));
        }
      }
      if (axisOn[1]) {
        extra.append(F.GrayLevel(F.C0));
        extra.append(F.Line(F.List(point(originX, yMin), point(originX, yMax))));
        if (axisTicks) {
          ticks(extra, yMin, yMax, false, originX, originY);
        }
        IExpr label = labelOf(axesLabel, 2);
        if (label.isPresent()) {
          extra.append(F.Text(label, point(originX, yMax), F.List(F.C0, F.CN1)));
        }
      }
      if (frameOn[0] || frameOn[1] || frameOn[2] || frameOn[3]) {
        extra.append(F.GrayLevel(F.C0));
        // the bottom edge and its label first, then the left one; the value at the corner the two
        // share is written on neither
        if (frameOn[2]) {
          extra.append(F.Line(F.List(point(xMin, yMin), point(xMax, yMin))));
          if (frameTicksOn) {
            ticks(extra, xMin, xMax, true, yMin, xMin);
          }
        }
        IExpr bottom = labelOf(frameLabel, 1);
        if (bottom.isPresent()) {
          extra.append(F.Text(bottom, offset(0, -18, point((xMin + xMax) / 2, yMin)),
              F.List(F.C0, F.C1)));
        }
        if (frameOn[0]) {
          extra.append(F.Line(F.List(point(xMin, yMin), point(xMin, yMax))));
          if (frameTicksOn) {
            ticks(extra, yMin, yMax, false, xMin, yMin);
          }
        }
        IExpr left = labelOf(frameLabel, 2);
        if (left.isPresent()) {
          extra.append(F.Text(left, offset(-30, 0, point(xMin, (yMin + yMax) / 2)),
              F.List(F.C1, F.C0)));
        }
        if (frameOn[1]) {
          extra.append(F.Line(F.List(point(xMax, yMin), point(xMax, yMax))));
        }
        if (frameOn[3]) {
          extra.append(F.Line(F.List(point(xMin, yMax), point(xMax, yMax))));
        }
      }
      // everything the options drew is now in the primitives, so none of them is kept
      return F.Graphics(F.List(graphics.arg1(), extra));
    }

    /**
     * The ticks of one axis.
     *
     * @param horizontal whether the axis runs along x
     * @param at the other coordinate of the axis
     * @param skip the value where the other axis crosses, which gets no label; NaN for none
     */
    private static void ticks(IASTAppendable out, double min, double max, boolean horizontal,
        double at, double skip) {
      List<TickGenerator.Tick> major = TickGenerator.linear(min, max);
      List<Double> all = TickGenerator.linearWithMinor(min, max);
      double tolerance = (max - min) * 1e-9;
      for (double tickValue : all) {
        // the multiples of the step carry rounding noise, 0.30000000000000004 for 3 * 0.1
        double value = org.matheclipse.core.graphics.CurvePrimitives.roundedTick(tickValue);
        if (value < min - tolerance || value > max + tolerance) {
          continue;
        }
        TickGenerator.Tick label = null;
        for (TickGenerator.Tick tick : major) {
          if (tick.major && Math.abs(tick.value - value) <= tolerance) {
            label = tick;
            break;
          }
        }
        double length = label != null ? MAJOR_TICK : MINOR_TICK;
        IAST position = horizontal ? point(value, at) : point(at, value);
        // the ticks point up from a horizontal axis and right from a vertical one, which is into
        // the picture for the bottom and left edges of a frame
        IAST shift = horizontal ? F.List(F.C0, F.num(length)) : F.List(F.num(length), F.C0);
        out.append(F.Line(F.List(position, F.binaryAST2(S.Offset, shift, position))));
        if (label != null && !(Double.isFinite(skip) && Math.abs(value - skip) <= tolerance)) {
          IAST anchor = horizontal ? F.List(F.C0, F.C1) : F.List(F.C1, F.C0);
          IAST labelShift = horizontal ? F.List(F.C0, F.num(-3)) : F.List(F.num(-3), F.C0);
          out.append(F.Text(F.stringx(label.label), F.binaryAST2(S.Offset, labelShift, position),
              anchor));
        }
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static IAST point(double x, double y) {
    return F.List(F.num(x), F.num(y));
  }

  private static IAST offset(double dx, double dy, IAST position) {
    return F.binaryAST2(S.Offset, F.List(F.num(dx), F.num(dy)), position);
  }

  /** The label of axis <code>index</code> (1 for x, 2 for y) of an option value. */
  private static @AllowNIL IExpr labelOf(IExpr labels, int index) {
    if (labels.isNIL() || labels.isNone()) {
      return F.NIL;
    }
    if (labels.isList()) {
      if (labels.argSize() >= index) {
        IExpr label = labels.getAt(index);
        return label.isNone() ? F.NIL : label;
      }
      return F.NIL;
    }
    return index == 1 ? labels : F.NIL;
  }

  /**
   * Which sides an option turns on: <code>True</code> all, <code>False</code>/<code>None</code>
   * none, a list each by itself (<code>{x, y}</code> for axes, <code>{{left, right}, {bottom,
   * top}}</code> or <code>{left, right, bottom, top}</code> for a frame).
   */
  private static boolean[] sides(IExpr value, int count) {
    boolean[] on = new boolean[count];
    if (value.isTrue()) {
      java.util.Arrays.fill(on, true);
      return on;
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      if (count == 4 && list.argSize() == 2 && list.arg1().isList2() && list.arg2().isList2()) {
        on[0] = list.arg1().first().isTrue();
        on[1] = list.arg1().second().isTrue();
        on[2] = list.arg2().first().isTrue();
        on[3] = list.arg2().second().isTrue();
        return on;
      }
      for (int i = 0; i < count && i < list.argSize(); i++) {
        on[i] = list.get(i + 1).isTrue();
      }
    }
    return on;
  }

  private static @AllowNIL IExpr option(IAST graphics, ISymbol name) {
    for (int i = 2; i <= graphics.argSize(); i++) {
      IExpr opt = graphics.get(i);
      if (opt.isRuleAST() && opt.first() == name) {
        return opt.second();
      }
    }
    return F.NIL;
  }

  public static void initialize() {
    Initializer.init();
  }

  private GraphicsDirectiveFunctions() {}

}
