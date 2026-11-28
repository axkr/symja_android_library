package org.matheclipse.core.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.svg.TickGenerator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The graphics primitives which stand for a drawing worked out from their arguments, rewritten into
 * the primitives that draw them:
 *
 * <ul>
 * <li><code>PolarCurve(r, {t, tmin, tmax})</code>: the curve <code>{r cos t, r sin t}</code>, sampled
 * like <code>PolarPlot</code> samples it, as a <code>Line</code>;
 * <li><code>FilledPolarCurve(r, {t, tmin, tmax})</code> and <code>FilledPolarCurve(r, t)</code> (a
 * full turn): the area the radius sweeps, as a
 * <code>Polygon</code> from the pole along the curve;
 * <li><code>AxisObject(path, {tmin, tmax})</code>: the path with tick marks and labels for the values
 * <code>tmin ... tmax</code> spread along it.
 * </ul>
 */
public final class CurvePrimitives {

  private CurvePrimitives() {}

  /** The length of a major tick of an {@code AxisObject} in printer's points. */
  private static final double MAJOR_TICK = 5.0;

  /** The length of a minor tick of an {@code AxisObject} in printer's points. */
  private static final double MINOR_TICK = 2.5;

  /**
   * The primitive drawing a <code>PolarCurve</code> or <code>FilledPolarCurve</code>.
   *
   * @return a <code>Line</code> or <code>Polygon</code>, or {@link F#NIL} when the curve can't be
   *         sampled
   */
  public static @AllowNIL IExpr polarCurve(IAST ast, EvalEngine engine) {
    boolean filled = ast.isAST(S.FilledPolarCurve);
    IAST curve = ast;
    if (curve.argSize() != 2) {
      return F.NIL;
    }
    IExpr radius = curve.arg1();
    IExpr range = curve.arg2();
    ISymbol variable;
    double tMin;
    double tMax;
    if (range.isList3() && range.first().isSymbol()) {
      variable = (ISymbol) range.first();
      tMin = range.second().evalfNaN();
      tMax = range.get(3).evalfNaN();
    } else if (filled && range.isSymbol() && !range.isConstantAttribute()) {
      // FilledPolarCurve(r, t) sweeps a full turn
      variable = (ISymbol) range;
      tMin = 0.0;
      tMax = 2.0 * Math.PI;
    } else {
      return F.NIL;
    }
    if (!Double.isFinite(tMin) || !Double.isFinite(tMax) || tMin == tMax) {
      return F.NIL;
    }
    List<List<double[]>> segments = samplePolar(radius, variable, tMin, tMax, engine);
    if (segments.isEmpty()) {
      return F.NIL;
    }
    if (filled) {
      // the area the radius sweeps: from the pole along the curve and back
      IASTAppendable polygon = F.ListAlloc();
      boolean closed = Math.abs(tMax - tMin) >= 2.0 * Math.PI - 1e-12;
      if (!closed) {
        polygon.append(F.List(F.CD0, F.CD0));
      }
      for (List<double[]> segment : segments) {
        for (double[] p : segment) {
          polygon.append(F.List(F.num(p[0]), F.num(p[1])));
        }
      }
      return polygon.argSize() >= 3 ? F.Polygon(polygon) : F.NIL;
    }
    IASTAppendable lines = F.ListAlloc(segments.size());
    for (List<double[]> segment : segments) {
      if (segment.size() < 2) {
        continue;
      }
      IASTAppendable points = F.ListAlloc(segment.size());
      for (double[] p : segment) {
        points.append(F.List(F.num(p[0]), F.num(p[1])));
      }
      lines.append(points);
    }
    if (lines.argSize() == 0) {
      return F.NIL;
    }
    return F.Line(lines.argSize() == 1 ? lines.arg1() : lines);
  }

  /**
   * The points <code>{r(t) cos t, r(t) sin t}</code> for <code>tMin &lt;= t &lt;= tMax</code>,
   * sampled adaptively; a stretch where the radius has no real value breaks the curve.
   */
  private static List<List<double[]>> samplePolar(IExpr radius, ISymbol variable, double tMin,
      double tMax, EvalEngine engine) {
    List<List<double[]>> segments = new ArrayList<>();
    final UnaryNumerical function = new UnaryNumerical(radius, variable, Double.NaN, engine);
    DoubleUnaryOperator sampled = t -> {
      try {
        return function.applyAsDouble(t);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Double.NaN;
      }
    };
    double lo = Math.min(tMin, tMax);
    double hi = Math.max(tMin, tMax);
    double[][] data = org.matheclipse.core.sympy.plotting.Plot.computePlot(sampled, null, lo, hi,
        "Linear", -1, -1, false);
    if (data == null) {
      return segments;
    }
    List<double[]> current = new ArrayList<>();
    for (int i = 0; i < data[0].length; i++) {
      double t = data[0][i];
      double r = data[1][i];
      if (Double.isFinite(r)) {
        current.add(new double[] {r * Math.cos(t), r * Math.sin(t)});
      } else if (!current.isEmpty()) {
        segments.add(current);
        current = new ArrayList<>();
      }
    }
    if (!current.isEmpty()) {
      segments.add(current);
    }
    return segments;
  }

  /**
   * The primitives drawing <code>AxisObject(path, {tmin, tmax}, options)</code>: the path, a tick
   * mark at each round value of <code>tmin ... tmax</code> and a label at each major one. The
   * values are spread along the path by arc length; without a range they are the arc length,
   * counted from the start of the path. The ticks stand above the path (left of a vertical one)
   * and the labels below it, whichever way the path runs.
   *
   * <p>
   * Options: <code>AxisStyle</code> and <code>BaseStyle</code> style the path,
   * <code>TicksStyle</code> the ticks, <code>TickLabels -&gt; None</code> leaves out the labels,
   * <code>Ticks -&gt; None</code> the ticks.
   *
   * @return the primitives, or {@link F#NIL} when the path is no line
   */
  public static @AllowNIL IExpr axisObject(IAST ast) {
    if (ast.argSize() < 1) {
      return F.NIL;
    }
    List<double[]> path = linePoints(ast.arg1());
    if (path == null || path.size() < 2) {
      return F.NIL;
    }
    double[] cumulative = new double[path.size()];
    for (int i = 1; i < path.size(); i++) {
      cumulative[i] = cumulative[i - 1]
          + Math.hypot(path.get(i)[0] - path.get(i - 1)[0], path.get(i)[1] - path.get(i - 1)[1]);
    }
    double length = cumulative[cumulative.length - 1];
    if (!(length > 0)) {
      return F.NIL;
    }
    double tMin = 0.0;
    double tMax = length;
    if (ast.argSize() >= 2 && ast.arg2().isList2()) {
      double a = ast.arg2().first().evalfNaN();
      double b = ast.arg2().second().evalfNaN();
      if (Double.isFinite(a) && Double.isFinite(b) && a != b) {
        tMin = a;
        tMax = b;
      }
    }
    IExpr axisStyle = optionByName(ast, "AxisStyle");
    IExpr baseStyle = optionByName(ast, "BaseStyle");
    IExpr ticksStyle = optionByName(ast, "TicksStyle");
    boolean labels = !optionByName(ast, "TickLabels").isNone();
    boolean ticks = !optionByName(ast, "Ticks").isNone();

    IASTAppendable out = F.ListAlloc();
    IASTAppendable line = F.ListAlloc();
    if (baseStyle.isPresent()) {
      line.append(baseStyle);
    }
    if (axisStyle.isPresent()) {
      line.append(axisStyle);
    }
    line.append(ast.arg1());
    out.append(line);
    if (!ticks) {
      return out;
    }
    IASTAppendable marks = F.ListAlloc();
    if (baseStyle.isPresent()) {
      marks.append(baseStyle);
    }
    if (axisStyle.isPresent()) {
      marks.append(axisStyle);
    }
    if (ticksStyle.isPresent()) {
      marks.append(ticksStyle);
    }
    IASTAppendable texts = F.ListAlloc();
    double lo = Math.min(tMin, tMax);
    double hi = Math.max(tMin, tMax);
    List<Double> minor = TickGenerator.linearWithMinor(lo, hi);
    List<TickGenerator.Tick> major = TickGenerator.linear(lo, hi);
    double step = major.size() >= 2 ? Math.abs(major.get(1).value - major.get(0).value) : hi - lo;
    double tolerance = (hi - lo) * 1e-9;
    for (Double tickValue : minor) {
      // the multiples of the step carry rounding noise, 0.30000000000000004 for 3 * 0.1
      double value = roundedTick(tickValue);
      boolean isMajor = false;
      String label = null;
      for (TickGenerator.Tick tick : major) {
        if (tick.major && Math.abs(tick.value - value) <= Math.max(tolerance, step * 1e-9)) {
          isMajor = true;
          label = tick.label;
          break;
        }
      }
      if (value < lo - tolerance || value > hi + tolerance) {
        continue;
      }
      double s = (value - tMin) / (tMax - tMin) * length;
      double[] at = pointAt(path, cumulative, s);
      if (at == null) {
        continue;
      }
      // the ticks stand on the upper side of the path (the left side of a vertical one), the
      // labels on the other side, whichever way the path runs
      double nx = -at[3];
      double ny = at[2];
      if (ny < 0 || (ny == 0 && nx > 0)) {
        nx = -nx;
        ny = -ny;
      }
      double size = isMajor ? MAJOR_TICK : MINOR_TICK;
      IAST position = F.List(F.num(at[0]), F.num(at[1]));
      marks.append(F.Line(F.List(position,
          offset(F.List(F.num(nx * size), F.num(ny * size)), position))));
      if (isMajor && labels && label != null) {
        texts.append(F.Text(F.stringx(label),
            offset(F.List(F.num(-nx * 2), F.num(-ny * 2)), position),
            F.List(F.num(1.2 * nx), F.num(1.2 * ny))));
      }
    }
    out.append(marks);
    if (texts.argSize() > 0) {
      IASTAppendable labelGroup = F.ListAlloc();
      if (baseStyle.isPresent()) {
        labelGroup.append(baseStyle);
      }
      labelGroup.appendArgs(texts);
      out.append(labelGroup);
    }
    return out;
  }

  /**
   * The point at arc length <code>s</code> along the path and the unit direction there, as
   * <code>{x, y, dx, dy}</code>.
   */
  private static double[] pointAt(List<double[]> path, double[] cumulative, double s) {
    int last = path.size() - 1;
    for (int i = 1; i <= last; i++) {
      if (s <= cumulative[i] + 1e-12 || i == last) {
        double segment = cumulative[i] - cumulative[i - 1];
        if (segment <= 0) {
          continue;
        }
        double f = (s - cumulative[i - 1]) / segment;
        double[] a = path.get(i - 1);
        double[] b = path.get(i);
        double dx = (b[0] - a[0]) / segment;
        double dy = (b[1] - a[1]) / segment;
        return new double[] {a[0] + f * (b[0] - a[0]), a[1] + f * (b[1] - a[1]), dx, dy};
      }
    }
    return null;
  }

  /** <code>Offset(shift, position)</code>: a position moved by printer's points. */
  private static IAST offset(IAST shift, IAST position) {
    return F.binaryAST2(S.Offset, shift, position);
  }

  /** The points of <code>Line({p1, p2, ...})</code>, or {@code null}. */
  private static List<double[]> linePoints(IExpr line) {
    if (!line.isAST(S.Line) || line.argSize() < 1 || !line.first().isList()) {
      return null;
    }
    IAST points = (IAST) line.first();
    List<double[]> out = new ArrayList<>(points.argSize());
    for (IExpr p : points) {
      if (!p.isList2()) {
        return null;
      }
      double x = p.first().evalfNaN();
      double y = p.second().evalfNaN();
      if (!Double.isFinite(x) || !Double.isFinite(y)) {
        return null;
      }
      out.add(new double[] {x, y});
    }
    return out;
  }

  /**
   * The value of the option <code>name -&gt; value</code> of a call, compared by name: the relaxed
   * syntax lowercases the names of symbols which aren't built in, and some options have no
   * built-in symbol.
   *
   * @return the value, or {@link F#NIL} when the call has no such option
   */
  public static @AllowNIL IExpr optionByName(IAST ast, String name) {
    for (int i = 1; i <= ast.argSize(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && arg.first().isSymbol()
          && name.equalsIgnoreCase(((ISymbol) arg.first()).getSymbolName())) {
        return arg.second();
      }
    }
    return F.NIL;
  }

  /** A tick value rounded to 12 significant digits. */
  public static double roundedTick(double value) {
    if (value == 0.0 || !Double.isFinite(value)) {
      return value;
    }
    return new java.math.BigDecimal(value).round(new java.math.MathContext(12)).doubleValue();
  }

}
