package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>BulletGauge(values, references, {min, r1, ..., max})</code> - a horizontal bullet graph.
 *
 * <p>
 * The scale runs from <code>0</code> to <code>1</code> in the graphics coordinates. The qualitative
 * ranges are grey bands between the fractions of <code>r1, ...</code> (<code>{min, max}</code>
 * alone splits at a half and at three quarters), the scale line carries the ticks of a division of
 * <code>{min, max}</code> into about five steps of <code>1, 2</code> or <code>5</code> times a
 * power of ten (<code>ScaleDivisions -&gt; n</code> asks for <code>n</code> equal steps), every
 * value is a bar, the longest first, clipped to the range, and every reference a translucent
 * marker. <code>GaugeStyle</code> gives the colours of the bars and <code>GaugeLabels</code> a
 * label below the scale.
 */
public class BulletGauge extends AbstractEvaluator {

  private static final IExpr GRAY_25 = F.RGBColor(0.25005721986295587, 0.25005721986295587,
      0.25005721986295587);
  private static final IExpr GRAY_50 =
      F.RGBColor(0.5001144397259117, 0.5001144397259117, 0.5001144397259117);
  private static final IExpr[] BAND_GRAYS =
      {F.RGBColor(0.600079345256126, 0.600079345256126, 0.600079345256126),
          F.RGBColor(0.7501007083058716, 0.7501007083058716, 0.7501007083058716),
          F.RGBColor(0.8999984736740948, 0.8999984736740948, 0.8999984736740948)};

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int positional = 0;
    while (positional < ast.argSize() && !ast.get(positional + 1).isRuleAST()) {
      positional++;
    }
    if (positional != 3) {
      return F.NIL;
    }
    IAST values = ast.arg1().makeList();
    IAST references = ast.arg2().makeList();
    IExpr rangeArg = ast.arg3();
    if (!rangeArg.isList() || rangeArg.argSize() < 2 || !isReal(values) || !isReal(references)
        || !isReal((IAST) rangeArg)) {
      return F.NIL;
    }
    IAST range = (IAST) rangeArg;
    final IExpr min = range.arg1();
    final IExpr max = range.last();
    final double minValue = min.evalf();
    final double maxValue = max.evalf();
    if (!(maxValue > minValue)) {
      return F.NIL;
    }

    IAST styles = F.CEmptyList;
    IExpr label = F.NIL;
    int divisions = -1;
    for (int i = 4; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      IExpr value = option.second();
      if (option.first() == S.GaugeStyle) {
        styles = value.makeList();
      } else if (option.first() == S.GaugeLabels) {
        label = value.isString() ? value : F.NIL;
      } else if (option.first() == S.ScaleDivisions) {
        IExpr n = value.isList() && value.argSize() > 0 ? value.first() : value;
        divisions = n.toIntDefault();
      }
    }

    // the qualitative ranges
    List<IExpr> edges = new ArrayList<IExpr>();
    edges.add(F.C0);
    if (range.argSize() == 2) {
      edges.add(F.num(0.5));
      edges.add(F.num(0.75));
    } else {
      for (int i = 2; i < range.argSize(); i++) {
        edges.add(fraction(range.get(i), min, max, engine));
      }
    }
    edges.add(F.C1);
    IASTAppendable bands = F.ListAlloc(edges.size());
    for (int i = 0; i + 1 < edges.size(); i++) {
      bands.append(F.list(F.Directive(BAND_GRAYS[i % 3]),
          F.ternaryAST3(S.Rectangle, F.list(edges.get(i), F.C0),
              F.list(edges.get(i + 1), F.num(0.1)), F.Rule(F.stringx("RoundingRadius"), F.C0))));
    }

    // the scale with its ticks and their labels
    IAST ticks = divisions > 0 ? equalTicks(min, max, divisions, engine)
        : niceTicks(min, max, minValue, maxValue, engine);
    IASTAppendable tickLines = F.ListAlloc(ticks.argSize());
    IASTAppendable tickTexts = F.ListAlloc(ticks.argSize());
    for (IExpr tick : ticks) {
      IExpr x = engine.evalN(fraction(tick.first(), min, max, engine));
      tickLines.append(F.list(F.list(x, F.CD0), F.list(x, F.num(-0.015))));
      tickTexts.append(F.Text(tick.second(), F.list(x, F.num(-0.025)), F.list(F.CD0, F.CD1)));
    }
    IAST scale = F.list(
        F.Style(F.Line(F.list(F.list(F.CD0, F.CD0), F.list(F.CD1, F.CD0))), GRAY_25),
        F.Style(F.Line(tickLines), GRAY_25),
        F.Style(tickTexts, GRAY_50, F.Rule(S.FontSize, F.ZZ(10))));

    // the bars of the values, longest first, and the markers of the references
    IASTAppendable marks = F.ListAlloc(values.argSize() + references.argSize());
    Integer[] order = new Integer[values.argSize()];
    for (int i = 0; i < order.length; i++) {
      order[i] = i + 1;
    }
    java.util.Arrays.sort(order,
        (a, b) -> Double.compare(values.get(b).evalf(), values.get(a).evalf()));
    for (int rank = 0; rank < order.length; rank++) {
      IExpr v = values.get(order[rank]);
      double value = v.evalf();
      if (value < minValue) {
        v = engine.evalN(min);
      } else if (value > maxValue) {
        v = engine.evalN(max);
      }
      IExpr color = styles.argSize() > 0 ? styles.get(rank % styles.argSize() + 1)
          : F.Hue(F.C0, F.C1, F.num(0.8));
      IAST bar = F.list(F.Directive(color),
          F.ternaryAST3(S.Rectangle, F.list(F.num(-0.06875), F.C0),
              F.list(F.num(-0.03125), fraction(v, min, max, engine)),
              F.Rule(F.stringx("RoundingRadius"), F.C0)));
      marks.append(F.ternaryAST3(S.Rotate, F.list(F.Tooltip(bar, v), F.CEmptyList),
          F.Times(F.CN1D2, S.Pi), F.list(F.C0, F.C0)));
    }
    for (int j = 1; j <= references.argSize(); j++) {
      IExpr r = references.get(j);
      IExpr style =
          F.binaryAST2(S.Directive, seriesColor(values.argSize() + j - 1), F.Opacity(F.num(0.5)));
      IExpr[] markerParts = {F.CEmptyList, style, F.CEmptyList,
          F.Rule(F.stringx("MarkerRotation"), F.Times(F.C1D2, S.Pi)),
          F.Tooltip(F.Rectangle(F.list(F.num(-0.5), F.num(-0.075)),
              F.list(F.num(0.5), F.num(0.075))), r)};
      IAST marker = F.List(markerParts);
      IAST transformation = F.list(
          F.list(F.list(F.CD0, F.num(-0.075)), F.list(F.num(0.075), F.CD0)),
          F.list(engine.evalN(fraction(r, min, max, engine)), F.num(0.05)));
      marks.append(
          F.list(style, F.binaryAST2(S.GeometricTransformation, marker, transformation)));
    }

    IASTAppendable primitives = F.ListAlloc(4);
    primitives.append(F.ternaryAST3(S.Rotate, bands, F.C0, F.list(F.C0, F.C0)));
    primitives.append(F.list(F.Style(scale,
        F.binaryAST2(S.Directive, F.AbsoluteThickness(F.C1), F.GrayLevel(F.C1D2)))));
    primitives.append(label.isPresent()
        ? F.list(F.ternaryAST3(S.Inset, F.Style(label, GRAY_50, F.Rule(S.FontSize, F.ZZ(10))),
            F.list(F.num(0.5), F.num(-0.12)), S.Top))
        : F.CEmptyList);
    primitives.append(marks);
    IASTAppendable graphics = F.ast(S.Graphics);
    graphics.append(primitives);
    graphics.append(F.Rule(S.AspectRatio, S.Automatic));
    graphics.append(F.Rule(S.Axes, S.False));
    graphics.append(F.Rule(S.ImageSize, S.Small));
    graphics.append(F.Rule(S.PlotRange, S.All));
    return graphics;
  }

  private static boolean isReal(IAST list) {
    for (IExpr x : list) {
      if (!x.isRealResult() || Double.isNaN(x.evalfNaN())) {
        return false;
      }
    }
    return true;
  }

  /** <code>(x - min)/(max - min)</code>, exact for exact numbers. */
  private static IExpr fraction(IExpr x, IExpr min, IExpr max, EvalEngine engine) {
    return engine.evaluate(F.Divide(F.Subtract(x, min), F.Subtract(max, min)));
  }

  /**
   * The <code>{value, label}</code> pairs of <code>n</code> equal steps; a label after the first
   * shows the decimals of the step.
   */
  private static IAST equalTicks(IExpr min, IExpr max, int n, EvalEngine engine) {
    if (n > 100) {
      return F.CEmptyList;
    }
    double step = (max.evalf() - min.evalf()) / n;
    int decimals = 0;
    while (decimals < 6) {
      double scaled = step * Math.pow(10, decimals);
      if (Math.abs(scaled - Math.rint(scaled)) < 1e-9) {
        break;
      }
      decimals++;
    }
    final int d = decimals;
    return F.mapRange(0, n + 1, k -> {
      IExpr value =
          engine.evaluate(F.Plus(min, F.Times(F.ZZ(k), F.Divide(F.Subtract(max, min), F.ZZ(n)))));
      IExpr text = k == 0 || d == 0 ? value
          : F.binaryAST2(S.NumberForm, engine.evalN(value), F.list(F.CInfinity, F.ZZ(d)));
      return F.list(value, text);
    });
  }

  /**
   * The <code>{value, value}</code> pairs of the multiples of a step of <code>1, 2</code> or
   * <code>5</code> times a power of ten which divides the range into about five parts.
   */
  private static IAST niceTicks(IExpr min, IExpr max, double minValue, double maxValue,
      EvalEngine engine) {
    double raw = (maxValue - minValue) / 5.0;
    int exponent = (int) Math.floor(Math.log10(raw));
    double unit = Math.pow(10, exponent);
    int mantissa = raw <= unit * (1 + 1e-12) ? 1
        : raw <= 2 * unit * (1 + 1e-12) ? 2 : raw <= 5 * unit * (1 + 1e-12) ? 5 : 10;
    IExpr step = exponent >= 0 ? F.ZZ(mantissa).times(F.ZZ(10).pow(exponent))
        : F.QQ(mantissa, 1).times(F.QQ(1, 10).pow(-exponent));
    double stepValue = mantissa * unit;
    long first = (long) Math.ceil(minValue / stepValue - 1e-9);
    long last = (long) Math.floor(maxValue / stepValue + 1e-9);
    IASTAppendable ticks = F.ListAlloc();
    for (long k = first; k <= last && ticks.argSize() < 100; k++) {
      IExpr value = engine.evaluate(F.Times(F.ZZ(k), step));
      if (!min.isExactNumber() || !max.isExactNumber()) {
        value = engine.evalN(value);
      }
      ticks.append(F.list(value, value));
    }
    return ticks;
  }

  /**
   * The colour of the <code>k</code>-th series, counting the bars first: hues
   * <code>0.9*(GoldenRatio - 1)</code> apart modulo <code>0.9</code>, at saturation and brightness
   * <code>0.73</code>.
   */
  private static IExpr seriesColor(int k) {
    double phi = (1.0 + Math.sqrt(5.0)) / 2.0;
    double step = 0.9 * (phi - 1.0);
    double h = 0.9 * (1.0 / 60.0 + 1.0 / phi);
    for (int i = 1; i < k; i++) {
      h += step;
      if (h >= 0.9) {
        h -= 0.9;
      }
    }
    double s = 0.73;
    double v = 0.73;
    double x = h * 6.0;
    double sector = Math.floor(x);
    double f = x - sector;
    double p = v * (1.0 - s);
    double q = v * (1.0 - s * f);
    double t = v * (1.0 - s * (1.0 - f));
    switch ((int) sector % 6) {
      case 0:
        return F.RGBColor(v, t, p);
      case 1:
        return F.RGBColor(q, v, p);
      case 2:
        return F.RGBColor(p, v, t);
      case 3:
        return F.RGBColor(p, q, v);
      case 4:
        return F.RGBColor(t, p, v);
      default:
        return F.RGBColor(v, p, q);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_INFINITY;
  }
}
