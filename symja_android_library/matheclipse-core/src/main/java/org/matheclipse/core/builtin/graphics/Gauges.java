package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.svg.TickGenerator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The gauges which show values on a scale:
 *
 * <ul>
 * <li><code>AngularGauge(value, {min, max})</code>: a round dial whose scale runs clockwise from
 * 240 degrees through 300 degrees, with a needle at the value;</li>
 * <li><code>HorizontalGauge(value, {min, max})</code>: a scale line with a triangle marker above
 * it at the value;</li>
 * <li><code>ThermometerGauge(value, {min, max})</code>: a thermometer filled from its bulb up to
 * the value, with the scale on its left.</li>
 * </ul>
 *
 * The range defaults to <code>{0, 1}</code>; a decreasing range is sorted. A list of values draws
 * an indicator for each (red, blue, then the plot colours); a value which is no number draws the
 * gauge without one. Options: <code>GaugeStyle</code> (the colours of the indicators),
 * <code>ScaleDivisions</code> (about <code>n</code> steps of a round width, <code>None</code> for no
 * ticks, by default round values with minor ticks), <code>ScaleRanges</code> (<code>{{a, b},
 * ...}</code> marked along the scale) and <code>GaugeLabels</code> (<code>"Value"</code> writes the
 * first value in a box, any other expression is written as it is). The other options are those of
 * the picture.
 */
public final class Gauges {

  private Gauges() {}

  /** The colours of the first indicators; the scale ranges take the first one. */
  private static final IAST[] GAUGE_COLORS =
      {F.RGBColor(0.73, 0.245, 0.197), F.RGBColor(0.196, 0.502, 0.729)};

  /** The most steps <code>ScaleDivisions -&gt; n</code> divides a scale into. */
  private static final int MAX_DIVISIONS = 1000;

  /** The colour of the scale and its labels. */
  private static final IAST SCALE_COLOR = F.GrayLevel(F.num(0.25));

  /** The settings of one gauge, read off its call. */
  private static final class Gauge {
    final List<Double> values = new ArrayList<>();
    final List<IExpr> valueExprs = new ArrayList<>();
    IExpr min = F.C0;
    IExpr max = F.C1;
    double low = 0.0;
    double high = 1.0;
    IExpr styles = F.NIL;
    IExpr divisions = S.Automatic;
    IExpr ranges = F.NIL;
    IExpr label = F.NIL;
    final IASTAppendable graphicsOptions = F.ListAlloc();

    /** The position of a value on the scale, from 0 at the minimum to 1 at the maximum. */
    double fraction(double v) {
      return Math.max(0.0, Math.min(1.0, (v - low) / (high - low)));
    }

    IExpr style(int index) {
      if (styles.isList() && styles.argSize() > 0) {
        return styles.getAt(index % styles.argSize() + 1);
      }
      if (styles.isPresent() && !styles.isAutomatic()) {
        return styles;
      }
      return index < GAUGE_COLORS.length ? GAUGE_COLORS[index]
          : GraphicsOptions.plotStyleColorExpr(index, F.NIL);
    }

    /** The ticks: <code>{value, label, major}</code> as <code>{double, IExpr, Boolean}</code>. */
    List<Object[]> ticks(EvalEngine engine) {
      List<Object[]> ticks = new ArrayList<>();
      if (divisions.isNone() || divisions.isFalse()) {
        return ticks;
      }
      List<Double> majors = new ArrayList<>();
      List<Double> minors = new ArrayList<>();
      int n = divisions.toIntDefault(-1);
      if (n > MAX_DIVISIONS) {
        // more steps than can be told apart: the automatic ticks
        n = -1;
      }
      if (n > 0) {
        // about n steps of a round width
        double step = roundStep((high - low) / n);
        for (long k = (long) Math.ceil(low / step - 1e-9); k * step <= high + step * 1e-9; k++) {
          majors.add(k * step);
        }
      } else {
        for (TickGenerator.Tick tick : TickGenerator.linear(low, high)) {
          majors.add(tick.value);
        }
        minors.addAll(TickGenerator.linearWithMinor(low, high));
      }
      double step = majors.size() > 1 ? majors.get(1) - majors.get(0) : high - low;
      int decimals = Math.max(0, (int) -Math.floor(Math.log10(step) + 1e-9));
      for (double v : majors) {
        ticks.add(new Object[] {v, F.stringx(format(v, decimals)), Boolean.TRUE});
      }
      for (double v : minors) {
        boolean major = false;
        for (double m : majors) {
          major |= Math.abs(m - v) < (high - low) * 1e-9;
        }
        if (!major) {
          ticks.add(new Object[] {v, F.NIL, Boolean.FALSE});
        }
      }
      return ticks;
    }

    /** The label below the gauge, or {@link F#NIL}. */
    @AllowNIL
    IExpr labelText() {
      if (label.isNIL() || label.isNone() || label.isAutomatic()) {
        return F.NIL;
      }
      if (label.isString("Value")) {
        return values.isEmpty() ? F.NIL : F.stringx(formatValue(values.get(0)));
      }
      return label;
    }
  }

  /**
   * Read a gauge call: the value(s), the range and the options.
   *
   * @return the settings, or {@code null} when the call is no gauge
   */
  private static Gauge read(IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1 || ast.arg1().isRuleAST()) {
      return null;
    }
    Gauge gauge = new Gauge();
    int optionStart = 2;
    if (ast.argSize() >= 2 && !ast.arg2().isRuleAST()) {
      if (!ast.arg2().isList2()) {
        return null;
      }
      gauge.min = ast.arg2().first();
      gauge.max = ast.arg2().second();
      optionStart = 3;
    }
    gauge.low = gauge.min.evalfNaN();
    gauge.high = gauge.max.evalfNaN();
    if (!Double.isFinite(gauge.low) || !Double.isFinite(gauge.high) || gauge.high == gauge.low) {
      return null;
    }
    if (gauge.high < gauge.low) {
      IExpr swap = gauge.min;
      gauge.min = gauge.max;
      gauge.max = swap;
      gauge.low = gauge.min.evalfNaN();
      gauge.high = gauge.max.evalfNaN();
    }
    for (IExpr v : ast.arg1().makeList()) {
      double d = v.evalfNaN();
      if (Double.isFinite(d)) {
        gauge.values.add(d);
        gauge.valueExprs.add(v);
      }
    }
    for (int i = optionStart; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        return null;
      }
      IExpr name = option.first();
      IExpr setting = option.second();
      if (name == S.GaugeStyle) {
        gauge.styles = setting;
      } else if (name == S.ScaleDivisions) {
        gauge.divisions = setting;
      } else if (name == S.ScaleRanges) {
        gauge.ranges = setting;
      } else if (name == S.GaugeLabels) {
        gauge.label = setting;
      } else if (name.isBuiltInSymbol()) {
        gauge.graphicsOptions.append(option);
      }
    }
    return gauge;
  }

  /** The ranges of <code>ScaleRanges</code> as fractions of the scale. */
  private static List<double[]> scaleRanges(Gauge gauge) {
    List<double[]> out = new ArrayList<>();
    IExpr ranges = gauge.ranges;
    if (ranges.isNIL() || ranges.isNone() || ranges.isAutomatic()) {
      return out;
    }
    IAST list = ranges.isList2() && !ranges.first().isList() ? F.List(ranges) : ranges.makeList();
    for (IExpr r : list) {
      if (r.isList2()) {
        double a = r.first().evalfNaN();
        double b = r.second().evalfNaN();
        if (Double.isFinite(a) && Double.isFinite(b)) {
          out.add(new double[] {gauge.fraction(Math.min(a, b)), gauge.fraction(Math.max(a, b))});
        }
      }
    }
    return out;
  }

  private static IAST pt(double x, double y) {
    return F.List(F.num(x), F.num(y));
  }

  /** A width of 1, 2 or 5 times a power of ten, not smaller than <code>w</code>. */
  private static double roundStep(double w) {
    double power = Math.pow(10, Math.floor(Math.log10(w)));
    for (double m : new double[] {1, 2, 5}) {
      if (m * power >= w * (1 - 1e-9)) {
        return m * power;
      }
    }
    return 10 * power;
  }

  /** A tick label with <code>decimals</code> decimals; zero is written <code>0</code>. */
  private static String format(double v, int decimals) {
    if (Math.abs(v) < 1e-12) {
      return "0";
    }
    if (decimals == 0) {
      return Long.toString(Math.round(v));
    }
    return String.format(java.util.Locale.US, "%." + decimals + "f", v);
  }

  /** A value as the gauges write it: at least one decimal, at most four. */
  private static String formatValue(double v) {
    String text = String.format(java.util.Locale.US, "%.4f", v);
    while (text.endsWith("0") && !text.endsWith(".0")) {
      text = text.substring(0, text.length() - 1);
    }
    return text;
  }

  /** A box with the label written white on gray, centred at <code>{x, y}</code>. */
  private static IAST labelBox(IExpr label, double x, double y, double charWidth,
      double height) {
    double w = 0.5 * charWidth * (label.toString().length() + 1);
    return F.List(F.GrayLevel(F.num(0.6)), F.EdgeForm(S.None),
        F.Rectangle(pt(x - w, y - height / 2), pt(x + w, y + height / 2)),
        F.GrayLevel(F.C1), F.Text(label, pt(x, y)));
  }

  private static IExpr finish(IASTAppendable primitives, Gauge gauge, IExpr aspectRatio,
      IExpr imageSize) {
    IASTAppendable graphics = F.ast(S.Graphics, gauge.graphicsOptions.size() + 3);
    graphics.append(primitives);
    graphics.appendArgs(gauge.graphicsOptions);
    if (!gauge.graphicsOptions.exists(rule -> rule.first() == S.AspectRatio)) {
      graphics.append(F.Rule(S.AspectRatio, aspectRatio));
    }
    if (imageSize.isPresent()
        && !gauge.graphicsOptions.exists(rule -> rule.first() == S.ImageSize)) {
      graphics.append(F.Rule(S.ImageSize, imageSize));
    }
    return graphics;
  }

  /** An evaluator which hands its call to a gauge drawing. */
  private abstract static class GaugeEvaluator extends AbstractFunctionEvaluator {
    abstract IExpr draw(Gauge gauge, EvalEngine engine);

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      Gauge gauge = read(ast, engine);
      return gauge == null ? F.NIL : draw(gauge, engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.addAttributes(ISymbol.READPROTECTED);
      setOptions(newSymbol,
          F.List(F.Rule(S.GaugeLabels, S.None), F.Rule(S.ScaleDivisions, S.Automatic),
              F.Rule(S.ScaleRanges, S.Automatic), F.Rule(S.GaugeStyle, S.Automatic)));
    }
  }

  // ---------------------------------------------------------------- AngularGauge

  /** The angle of the minimum of the dial, measured counter clockwise from the x axis. */
  private static final double START = Math.toRadians(240);

  /** The angle the scale sweeps clockwise. */
  private static final double SWEEP = Math.toRadians(300);

  /** The radius of the dial's face, inside the bezel. */
  private static final double FACE = 0.92;

  /** <code>AngularGauge(value, {min, max})</code>. */
  public static final class AngularGauge extends GaugeEvaluator {

    private static double angle(double fraction) {
      return START - fraction * SWEEP;
    }

    private static IAST polar(double r, double angle) {
      return pt(r * Math.cos(angle), r * Math.sin(angle));
    }

    @Override
    IExpr draw(Gauge gauge, EvalEngine engine) {
      IASTAppendable p = F.ListAlloc();
      // the bezel and the face
      p.append(F.List(F.GrayLevel(F.num(0.8)), F.EdgeForm(F.GrayLevel(F.num(0.6))),
          F.Disk(pt(0, 0), F.C1)));
      p.append(F.List(F.GrayLevel(F.C1), F.EdgeForm(F.GrayLevel(F.num(0.6))),
          F.Disk(pt(0, 0), F.num(FACE))));
      // the ranges, as bands along the rim
      for (double[] r : scaleRanges(gauge)) {
        IASTAppendable band = F.ListAlloc();
        int steps = 24;
        for (int k = 0; k <= steps; k++) {
          band.append(polar(FACE, angle(r[0] + (r[1] - r[0]) * k / steps)));
        }
        for (int k = steps; k >= 0; k--) {
          band.append(polar(FACE - 0.1, angle(r[0] + (r[1] - r[0]) * k / steps)));
        }
        p.append(F.List(GAUGE_COLORS[0], F.Polygon(band)));
      }
      // the ticks, from the rim inwards, and the labels inside them
      IASTAppendable scale = F.ListAlloc();
      scale.append(SCALE_COLOR);
      IASTAppendable labels = F.ListAlloc();
      labels.append(SCALE_COLOR);
      for (Object[] tick : gauge.ticks(engine)) {
        double a = angle(gauge.fraction((Double) tick[0]));
        boolean major = (Boolean) tick[2];
        scale.append(F.Line(F.List(polar(FACE - (major ? 0.1 : 0.06), a), polar(FACE, a))));
        if (major) {
          labels.append(F.Text((IExpr) tick[1], polar(0.68, a)));
        }
      }
      p.append(scale);
      p.append(labels);
      IExpr label = gauge.labelText();
      if (label.isPresent()) {
        p.append(labelBox(label, 0, -0.38, 0.11, 0.2));
      }
      // the needles, tapering from the hub, and the hub in the colour of the last one
      for (int i = 0; i < gauge.values.size(); i++) {
        double a = angle(gauge.fraction(gauge.values.get(i)));
        IAST tip = polar(0.8, a);
        IAST normal = polar(1, a + Math.PI / 2);
        double nx = normal.first().evalfNaN();
        double ny = normal.second().evalfNaN();
        double tx = tip.first().evalfNaN();
        double ty = tip.second().evalfNaN();
        p.append(F.List(gauge.style(i),
            F.Polygon(F.List(pt(0.055 * nx, 0.055 * ny), pt(tx + 0.025 * nx, ty + 0.025 * ny),
                pt(tx - 0.025 * nx, ty - 0.025 * ny), pt(-0.055 * nx, -0.055 * ny)))));
      }
      if (!gauge.values.isEmpty()) {
        p.append(F.List(gauge.style(gauge.values.size() - 1), F.Disk(pt(0, 0), F.num(0.07))));
        p.append(F.List(F.GrayLevel(F.C1), F.Disk(pt(0, 0), F.num(0.035))));
      }
      return finish(p, gauge, S.Automatic, F.ZZ(180));
    }
  }

  // ---------------------------------------------------------------- HorizontalGauge

  /** The height of a marker of a horizontal gauge, as a share of its length. */
  private static final double MARKER = 0.06;

  /** <code>HorizontalGauge(value, {min, max})</code>. */
  public static final class HorizontalGauge extends GaugeEvaluator {

    @Override
    IExpr draw(Gauge gauge, EvalEngine engine) {
      IASTAppendable p = F.ListAlloc();
      for (double[] r : scaleRanges(gauge)) {
        p.append(F.List(GAUGE_COLORS[0], F.Rectangle(pt(r[0], 0), pt(r[1], 0.035))));
      }
      IASTAppendable scale = F.ListAlloc();
      scale.append(SCALE_COLOR);
      scale.append(F.Line(F.List(pt(0, 0), pt(1, 0))));
      for (Object[] tick : gauge.ticks(engine)) {
        double x = gauge.fraction((Double) tick[0]);
        boolean major = (Boolean) tick[2];
        scale.append(F.Line(F.List(pt(x, 0), pt(x, major ? 0.02 : 0.012))));
        if (major) {
          scale.append(F.Text((IExpr) tick[1], pt(x, -0.02), F.List(F.C0, F.C1)));
        }
      }
      p.append(scale);
      // a triangle above the line, its tip at the value
      for (int i = 0; i < gauge.values.size(); i++) {
        double x = gauge.fraction(gauge.values.get(i));
        p.append(F.List(gauge.style(i), F.Polygon(F.List(pt(x, 0.006),
            pt(x - MARKER / 2, 0.006 + MARKER), pt(x + MARKER / 2, 0.006 + MARKER)))));
      }
      IExpr label = gauge.labelText();
      if (label.isPresent()) {
        p.append(labelBox(label, 1.08, 0.03, 0.03, 0.06));
      }
      return finish(p, gauge, S.Automatic, F.NIL);
    }
  }

  // ---------------------------------------------------------------- ThermometerGauge

  /** The half width of the tube of a thermometer. */
  private static final double TUBE = 0.05;

  /** The radius of the bulb of a thermometer. */
  private static final double BULB = 0.075;

  /** <code>ThermometerGauge(value, {min, max})</code>. */
  public static final class ThermometerGauge extends GaugeEvaluator {

    @Override
    IExpr draw(Gauge gauge, EvalEngine engine) {
      IASTAppendable p = F.ListAlloc();
      // the glass tube runs from the bulb to above the scale, which runs from 0 to 1
      p.append(F.List(F.GrayLevel(F.num(0.75)), F.EdgeForm(F.GrayLevel(F.num(0.6))),
          F.Rectangle(pt(-TUBE, -0.04), pt(TUBE, 1.0))));
      p.append(F.List(F.GrayLevel(F.num(0.88)), F.Rectangle(pt(-TUBE * 0.3, -0.04),
          pt(TUBE * 0.3, 1.0))));
      for (double[] r : scaleRanges(gauge)) {
        p.append(F.List(GAUGE_COLORS[0], F.Rectangle(pt(-TUBE - 0.04, r[0]), pt(-TUBE - 0.015,
            r[1]))));
      }
      IExpr liquid = gauge.style(0);
      // the liquid: the bulb and the tube up to the highest value
      p.append(F.List(liquid, F.Disk(pt(0, -0.04), F.num(BULB))));
      double top = 0.0;
      for (double v : gauge.values) {
        top = Math.max(top, gauge.fraction(v));
      }
      if (!gauge.values.isEmpty()) {
        p.append(F.List(liquid, F.Rectangle(pt(-TUBE * 0.8, -0.04), pt(TUBE * 0.8, top))));
      }
      IExpr label = gauge.labelText();
      if (label.isPresent()) {
        // at the height of the liquid, or at the bottom of the scale without a value
        p.append(labelBox(label, TUBE + 0.12, top, 0.045, 0.06));
      }
      IASTAppendable scale = F.ListAlloc();
      scale.append(SCALE_COLOR);
      for (Object[] tick : gauge.ticks(engine)) {
        double y = gauge.fraction((Double) tick[0]);
        boolean major = (Boolean) tick[2];
        scale.append(F.Line(F.List(pt(-TUBE - 0.01, y), pt(-TUBE - (major ? 0.06 : 0.035), y))));
        if (major) {
          scale.append(F.Text((IExpr) tick[1], pt(-TUBE - 0.08, y), F.List(F.C1, F.C0)));
        }
      }
      p.append(scale);
      return finish(p, gauge, S.Automatic, F.List(S.Automatic, F.ZZ(300)));
    }
  }
}
