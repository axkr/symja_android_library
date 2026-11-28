package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
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
 * The plots with axes of their own:
 *
 * <ul>
 * <li><code>TernaryListPlot({{a, b, c}, ...})</code>: compositions of three parts, each point at
 * the barycentric position <code>{a, b, c}/(a + b + c)</code> in a triangle whose corners are the
 * pure parts (a bottom right, b at the top, c bottom left), with a grid at every fifth and the
 * share of the part an edge leads to written along it;</li>
 * <li><code>TimelinePlot({event, ...})</code>: dates as points on a time axis and
 * <code>Interval({date1, date2})</code> as bars above it, the bars and the labels of
 * <code>Labeled(event, label)</code> stacked on levels so they don't overlap.</li>
 * </ul>
 */
public final class SpecialAxesPlots {

  private SpecialAxesPlots() {}

  private static final double SQRT3_2 = Math.sqrt(3.0) / 2.0;

  /** The colour of dataset <code>index</code> under a <code>PlotStyle</code>. */
  private static IExpr datasetStyle(int index, IExpr plotStyle) {
    if (plotStyle.isList() && plotStyle.argSize() > 0) {
      return plotStyle.getAt(index % plotStyle.argSize() + 1);
    }
    if (plotStyle.isPresent() && !plotStyle.isAutomatic()) {
      return plotStyle;
    }
    org.matheclipse.core.convert.RGBColor color = GraphicsOptions.plotStyleColor(index, S.None);
    return F.RGBColor(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
  }

  private static @AllowNIL IExpr option(IAST ast, int from, ISymbol name) {
    for (int i = from; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && arg.first() == name) {
        return arg.second();
      }
    }
    return F.NIL;
  }

  // ---------------------------------------------------------------- TernaryListPlot

  /** The point of the triangle for the shares <code>a, b, c</code>. */
  private static IAST ternary(double a, double b, double c) {
    double s = a + b + c;
    return F.List(F.num((a + b / 2.0) / s), F.num(b * SQRT3_2 / s));
  }

  /** <code>TernaryListPlot(data, options)</code>. */
  public static final class TernaryListPlot extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() < 1 || ast.arg1().isRuleAST()) {
        return F.NIL;
      }
      IExpr data = engine.evalN(ast.arg1());
      if (!data.isList() || data.argSize() == 0) {
        return F.NIL;
      }
      IAST datasets = isTriple(data.first()) ? F.List(data) : (IAST) data;
      IExpr plotStyle = option(ast, 2, S.PlotStyle);
      IASTAppendable content = F.ListAlloc();
      // the grid at every fifth of each part, and the outline
      IASTAppendable grid = F.ListAlloc();
      grid.append(F.GrayLevel(F.num(0.85)));
      for (int k = 1; k <= 4; k++) {
        double t = k / 5.0;
        grid.append(F.Line(F.List(ternary(1 - t, 0, t), ternary(0, 1 - t, t))));
        grid.append(F.Line(F.List(ternary(t, 1 - t, 0), ternary(t, 0, 1 - t))));
        grid.append(F.Line(F.List(ternary(1 - t, t, 0), ternary(0, t, 1 - t))));
      }
      content.append(grid);
      content.append(F.List(F.GrayLevel(F.C0),
          F.Line(F.List(ternary(1, 0, 0), ternary(0, 1, 0), ternary(0, 0, 1), ternary(1, 0, 0)))));
      IASTAppendable labels = F.ListAlloc();
      labels.append(F.GrayLevel(F.num(0.3)));
      // 0 is left out: it would sit on the corner where the next edge writes its 1
      for (int k = 1; k <= 5; k++) {
        double t = k / 5.0;
        IExpr text = F.stringx(TickGenerator.trim(t));
        // a along the bottom edge, b up the right edge, c down the left edge: each edge counts
        // the share of the part whose corner it leads to
        labels.append(F.Text(text, ternary(t, 0, 1 - t), F.List(F.C0, F.C1)));
        labels.append(F.Text(text, ternary(1 - t, t, 0), F.List(F.num(-1.4), F.C0)));
        labels.append(F.Text(text, ternary(0, 1 - t, t), F.List(F.num(1.4), F.C0)));
      }
      content.append(labels);
      for (int d = 0; d < datasets.argSize(); d++) {
        IExpr dataset = datasets.get(d + 1);
        if (!dataset.isList()) {
          return F.NIL;
        }
        IASTAppendable points = F.ListAlloc(dataset.argSize());
        for (IExpr triple : (IAST) dataset) {
          if (!isTriple(triple)) {
            return Errors.printMessage(S.TernaryListPlot, "arrayerr", F.list(ast.arg1()),
                engine);
          }
          double a = triple.first().evalfNaN();
          double b = triple.second().evalfNaN();
          double c = triple.getAt(3).evalfNaN();
          if (a < 0 || b < 0 || c < 0 || !(a + b + c > 0)) {
            continue;
          }
          points.append(ternary(a, b, c));
        }
        content.append(F.List(datasetStyle(d, plotStyle), F.PointSize(F.num(0.015)),
            F.Point(points)));
      }
      IASTAppendable graphics = F.ast(S.Graphics, ast.size() + 3);
      graphics.append(content);
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST() && arg.first() != S.PlotStyle) {
          graphics.append(arg);
        }
      }
      if (option(ast, 2, S.AspectRatio).isNIL()) {
        graphics.append(F.Rule(S.AspectRatio, S.Automatic));
      }
      if (option(ast, 2, S.ImagePadding).isNIL()) {
        graphics.append(F.Rule(S.ImagePadding, F.ZZ(25)));
      }
      return graphics;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static boolean isTriple(IExpr expr) {
    return expr.isList() && expr.argSize() == 3 && !expr.first().isList();
  }

  // ---------------------------------------------------------------- TimelinePlot

  /** Only the bottom edge of the frame of a timeline, which carries the dates. */
  private static final IAST TIMELINE_FRAME =
      F.List(F.List(S.False, S.False), F.List(S.True, S.False));

  /** The shape of a timeline: half the height of a plot. */
  private static final IExpr TIMELINE_ASPECT_RATIO =
      F.Divide(F.C1, F.Times(F.C2, S.GoldenRatio));

  /** The height of one level of labels above the axis. */
  private static final double LEVEL = 0.6;

  /** One event of a timeline: a moment or a span of time, with its label. */
  private static final class Event {
    final double start;
    final double end;
    final IExpr label;

    Event(double start, double end, IExpr label) {
      this.start = start;
      this.end = end;
      this.label = label;
    }

    boolean isSpan() {
      return end > start;
    }
  }

  /** <code>TimelinePlot({event, ...}, options)</code>. */
  public static final class TimelinePlot extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() < 1 || ast.arg1().isRuleAST()) {
        return F.NIL;
      }
      IExpr data = engine.evaluate(ast.arg1());
      List<Event> events = new ArrayList<>();
      if (data.isAssociation()) {
        IAST rules = ((org.matheclipse.core.interfaces.IAssociation) data).normal(false);
        for (IExpr rule : rules) {
          if (!addEvent(events, rule.second(), rule.first(), engine)) {
            return F.NIL;
          }
        }
      } else if (isDateList(data)) {
        // one date {y, m, d, ...}
        if (!addEvent(events, data, F.NIL, engine)) {
          return F.NIL;
        }
      } else if (data.isList()) {
        for (IExpr item : (IAST) data) {
          if (!addEvent(events, item, F.NIL, engine)) {
            return F.NIL;
          }
        }
      }
      if (events.isEmpty()) {
        // nothing which is an event: the empty frame of a timeline
        return F.Graphics(F.CEmptyList, F.Rule(S.Frame, TIMELINE_FRAME),
            F.Rule(S.AspectRatio, TIMELINE_ASPECT_RATIO));
      }
      events.sort((p, q) -> Double.compare(p.start, q.start));
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for (Event e : events) {
        min = Math.min(min, e.start);
        max = Math.max(max, e.end);
      }
      if (max == min) {
        // one moment: a day either side
        min -= 86400;
        max += 86400;
      }
      double span = max - min;
      IExpr color = datasetStyle(0, option(ast, 2, S.PlotStyle));
      IASTAppendable points = F.ListAlloc();
      IASTAppendable stems = F.ListAlloc();
      IASTAppendable bars = F.ListAlloc();
      IASTAppendable texts = F.ListAlloc();
      // the right end of the last label on each level, in units of time
      List<Double> labelEnds = new ArrayList<>();
      int topLevel = 0;
      for (Event e : events) {
        // a label takes about 2% of the axis per character
        double width = e.label.isPresent()
            ? span * 0.02 * Math.max(3, e.label.toString().length())
            : 0.0;
        if (e.isSpan()) {
          // a span is a bar above the axis, on a level of its own with its label over it
          double middle = (e.start + e.end) / 2;
          int level = freeLevel(labelEnds, Math.min(e.start, middle - width / 2),
              Math.max(e.end, middle + width / 2));
          topLevel = Math.max(topLevel, level + 1);
          double y = LEVEL * (level + 1);
          bars.append(F.Line(F.List(F.List(F.num(e.start), F.num(y)),
              F.List(F.num(e.end), F.num(y)))));
          if (e.label.isPresent()) {
            texts.append(F.Text(e.label, F.List(F.num(middle), F.num(y)),
                F.List(F.C0, F.num(-1.8))));
          }
          continue;
        }
        IAST at = F.List(F.num(e.start), F.C0);
        points.append(at);
        if (e.label.isPresent()) {
          int level = freeLevel(labelEnds, e.start - width / 2, e.start + width / 2);
          topLevel = Math.max(topLevel, level + 1);
          double y = LEVEL * (level + 1);
          stems.append(F.Line(F.List(at, F.List(F.num(e.start), F.num(y)))));
          texts.append(F.Text(e.label, F.List(F.num(e.start), F.num(y)), F.List(F.C0, F.CN1)));
        }
      }
      IASTAppendable content = F.ListAlloc();
      content.append(F.List(F.GrayLevel(F.num(0.6)), stems));
      content.append(F.List(color, F.AbsoluteThickness(F.ZZ(6)), F.unaryAST1(S.CapForm, F.stringx("Round")), bars));
      content.append(F.List(color, F.unaryAST1(S.AbsolutePointSize, F.ZZ(7)), F.Point(points)));
      content.append(F.List(F.GrayLevel(F.C0), texts));
      IASTAppendable graphics = F.ast(S.Graphics, ast.size() + 6);
      graphics.append(content);
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST() && arg.first() != S.PlotStyle) {
          graphics.append(arg);
        }
      }
      double pad = span * 0.05;
      // the time axis: a line through the events, the dates along the bottom of the frame
      content.append(1, F.List(F.GrayLevel(F.num(0.7)),
          F.Line(F.List(F.List(F.num(min - pad), F.C0), F.List(F.num(max + pad), F.C0)))));
      appendDefault(graphics, ast, S.Frame, TIMELINE_FRAME);
      appendDefault(graphics, ast, S.FrameTicks,
          F.List(F.List(S.None, S.None), F.List(DateListPlot.dateTicks(min, max), S.None)));
      appendDefault(graphics, ast, S.PlotRange,
          F.List(F.List(F.num(min - pad), F.num(max + pad)),
              F.List(F.num(-0.45), F.num(LEVEL * topLevel + 0.5))));
      appendDefault(graphics, ast, S.AspectRatio, TIMELINE_ASPECT_RATIO);
      return graphics;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static void appendDefault(IASTAppendable graphics, IAST ast, ISymbol key,
      IExpr value) {
    if (option(ast, 2, key).isNIL()) {
      graphics.append(F.Rule(key, value));
    }
  }

  /** The lowest level whose last item ends before <code>from</code>; it now ends at <code>to</code>. */
  private static int freeLevel(List<Double> ends, double from, double to) {
    for (int level = 0; level < ends.size(); level++) {
      if (ends.get(level) < from) {
        ends.set(level, to);
        return level;
      }
    }
    ends.add(to);
    return ends.size() - 1;
  }

  /**
   * Add a date, an <code>Interval({date1, date2})</code> or a <code>Labeled(event, label)</code>.
   *
   * @return false when the item is no event
   */
  private static boolean addEvent(List<Event> events, IExpr item, IExpr label,
      EvalEngine engine) {
    if (item.isAST(S.Labeled) && item.argSize() >= 2) {
      return addEvent(events, item.first(), item.second(), engine);
    }
    if (item.isAST(S.Interval) && item.argSize() >= 1 && item.first().isList2()) {
      double a = seconds(item.first().first(), engine);
      double b = seconds(item.first().second(), engine);
      if (!Double.isFinite(a) || !Double.isFinite(b)) {
        return false;
      }
      events.add(new Event(Math.min(a, b), Math.max(a, b), label));
      return true;
    }
    double t = seconds(item, engine);
    if (!Double.isFinite(t)) {
      return false;
    }
    events.add(new Event(t, t, label));
    return true;
  }

  /**
   * Whether <code>expr</code> is one date <code>{y, m}</code>, <code>{y, m, d}</code>, ... up to
   * seconds: an integer year followed by a month, a day, hours and minutes in their ranges.
   */
  private static boolean isDateList(IExpr expr) {
    if (!expr.isList() || expr.argSize() < 2 || expr.argSize() > 6
        || !((IAST) expr).forAll(x -> x.isReal()) || !expr.first().isInteger()) {
      return false;
    }
    int[][] ranges = {{1, 12}, {1, 31}, {0, 24}, {0, 59}, {0, 60}};
    for (int k = 2; k <= expr.argSize(); k++) {
      double v = expr.getAt(k).evalfNaN();
      if (!(v >= ranges[k - 2][0] && v <= ranges[k - 2][1])) {
        return false;
      }
    }
    return true;
  }

  /** A date as its absolute time in seconds, or NaN. */
  private static double seconds(IExpr date, EvalEngine engine) {
    if (date.isReal()) {
      return date.evalfNaN();
    }
    if (!(date.head() == S.DateObject || date.isString() || date.isList())) {
      return Double.NaN;
    }
    IExpr time = engine.evaluate(F.unaryAST1(S.AbsoluteTime, date));
    return time.isReal() ? time.evalfNaN() : Double.NaN;
  }
}
