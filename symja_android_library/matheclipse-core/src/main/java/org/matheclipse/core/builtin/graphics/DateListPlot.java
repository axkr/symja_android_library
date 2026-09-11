package org.matheclipse.core.builtin.graphics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>DateListPlot({{date1, v1}, {date2, v2}, ...})</code> and
 * <code>DateListPlot({series1, series2, ...})</code> - values against dates.
 *
 * <p>
 * Each date - a <code>DateObject</code>, a date list <code>{y, m, d, ...}</code>, a date string or
 * an absolute time - becomes its <code>AbsoluteTime</code>, and the data is drawn by
 * <code>ListLinePlot</code> the way Mathematica draws a date plot: joined, framed, without axes, and
 * with dates written under the bottom of the frame (its output of
 * <code>DateListPlot[{{DateObject[{2022, 12}], 1}, ...}]</code> is a single <code>Line</code> through
 * <code>{AbsoluteTime, value}</code> points with <code>Frame -> True</code> and
 * <code>Axes -> False</code>). An option the caller writes wins over these defaults.
 */
public class DateListPlot extends AbstractFunctionEvaluator {

  /** <code>AbsoluteTime</code> counts seconds from the start of 1900. */
  private static final LocalDateTime EPOCH = LocalDateTime.of(1900, 1, 1, 0, 0);

  private static final double HOUR = 3600.0;
  private static final double DAY = 24 * HOUR;
  private static final double YEAR = 365.2425 * DAY;
  private static final double MONTH = YEAR / 12.0;

  /** About this many ticks at most along the bottom of the frame. */
  private static final int MAX_TICKS = 7;

  private static final DateTimeFormatter MONTH_LABEL =
      DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
  private static final DateTimeFormatter DAY_LABEL =
      DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
  private static final DateTimeFormatter HOUR_LABEL =
      DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

  public DateListPlot() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr data = engine.evaluate(ast.arg1());
    if (!data.isList() || data.argSize() == 0) {
      return F.NIL;
    }
    IAST list = (IAST) data;
    // the earliest and the latest date, in seconds
    double[] range = {Double.MAX_VALUE, -Double.MAX_VALUE};
    IExpr plotData;
    if (isDatedPair(list.arg1())) {
      plotData = convertSeries(list, range, engine);
    } else {
      IASTAppendable all = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IExpr series = list.get(i);
        if (!series.isList() || series.argSize() == 0 || !isDatedPair(series.first())) {
          return F.NIL;
        }
        IExpr converted = convertSeries((IAST) series, range, engine);
        if (converted.isNIL()) {
          return F.NIL;
        }
        all.append(converted);
      }
      plotData = all;
    }
    if (plotData.isNIL() || !(range[0] <= range[1])) {
      return F.NIL;
    }

    IASTAppendable plot = F.ast(S.ListLinePlot, ast.argSize() + 4);
    plot.append(plotData);
    for (int i = 2; i < ast.size(); i++) {
      plot.append(ast.get(i));
    }
    if (!given(ast, S.Frame)) {
      plot.append(F.Rule(S.Frame, S.True));
    }
    if (!given(ast, S.Axes)) {
      plot.append(F.Rule(S.Axes, S.False));
    }
    if (!given(ast, S.FrameTicks)) {
      IAST ticks = dateTicks(range[0], range[1]);
      if (ticks.argSize() >= 2) {
        // the top of the frame gets the same marks without labels, as in Mathematica
        IASTAppendable unlabelled = F.ListAlloc(ticks.argSize());
        for (int i = 1; i < ticks.size(); i++) {
          unlabelled.append(F.List(ticks.get(i).first(), F.stringx("")));
        }
        plot.append(F.Rule(S.FrameTicks,
            F.List(F.List(S.Automatic, S.Automatic), F.List(ticks, unlabelled))));
      }
    }
    return engine.evaluate(plot);
  }

  /** Whether the call writes an option named <code>key</code>. */
  private static boolean given(IAST ast, IExpr key) {
    for (int i = 2; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && arg.first() == key) {
        return true;
      }
    }
    return false;
  }

  /** <code>{date, value}</code>, the value a single entry rather than a list. */
  private static boolean isDatedPair(IExpr expr) {
    return expr.isList() && expr.argSize() == 2 && !expr.second().isList()
        && isDate(expr.first());
  }

  private static boolean isDate(IExpr expr) {
    // a DateObject is held as a data object, an atom whose head is DateObject
    if (expr.head() == S.DateObject || expr.isString() || expr.isReal()) {
      return true;
    }
    if (expr.isList() && expr.argSize() >= 1 && expr.argSize() <= 6) {
      IAST list = (IAST) expr;
      for (int i = 1; i < list.size(); i++) {
        if (!list.get(i).isReal()) {
          return false;
        }
      }
      // a year comes first, which tells a date list from a pair of numbers
      return list.arg1().isInteger() && list.arg1().toIntDefault() >= 1
          && list.arg1().toIntDefault() <= 9999;
    }
    return false;
  }

  /** The series with every date replaced by its absolute time; {@link F#NIL} if one is no date. */
  private static IExpr convertSeries(IAST series, double[] range, EvalEngine engine) {
    IASTAppendable result = F.ListAlloc(series.argSize());
    for (int i = 1; i < series.size(); i++) {
      IExpr pair = series.get(i);
      if (!isDatedPair(pair)) {
        return F.NIL;
      }
      IExpr date = pair.first();
      IExpr time = date.isReal() ? date : engine.evaluate(F.unaryAST1(S.AbsoluteTime, date));
      if (!time.isReal()) {
        return F.NIL;
      }
      double seconds = time.evalf();
      range[0] = Math.min(range[0], seconds);
      range[1] = Math.max(range[1], seconds);
      result.append(F.List(time, pair.second()));
    }
    return result;
  }

  /**
   * Ticks <code>{{time, "label"}, ...}</code> at whole years, months, days or hours between
   * <code>min</code> and <code>max</code>, whichever unit gives no more than about
   * {@link #MAX_TICKS} of them.
   */
  static IAST dateTicks(double min, double max) {
    IASTAppendable ticks = F.ListAlloc();
    double span = max - min;
    LocalDateTime from = at(min);
    LocalDateTime to = at(max);
    if (span >= 2 * YEAR) {
      int step = step(span / YEAR, new int[] {1, 2, 5, 10, 20, 50, 100});
      int year = from.getYear();
      if (from.isAfter(LocalDateTime.of(year, 1, 1, 0, 0))) {
        year++;
      }
      year = ((year + step - 1) / step) * step;
      for (LocalDateTime t = LocalDateTime.of(year, 1, 1, 0, 0); !t.isAfter(to); t =
          t.plusYears(step)) {
        add(ticks, t, Integer.toString(t.getYear()));
      }
    } else if (span >= 2 * MONTH) {
      int step = step(span / MONTH, new int[] {1, 2, 3, 4, 6});
      LocalDateTime t = LocalDateTime.of(from.getYear(), from.getMonth(), 1, 0, 0);
      if (t.isBefore(from)) {
        t = t.plusMonths(1);
      }
      while ((t.getMonthValue() - 1) % step != 0) {
        t = t.plusMonths(1);
      }
      for (; !t.isAfter(to); t = t.plusMonths(step)) {
        add(ticks, t, MONTH_LABEL.format(t));
      }
    } else if (span >= 2 * DAY) {
      int step = step(span / DAY, new int[] {1, 2, 5, 7, 14});
      LocalDateTime t = from.toLocalDate().atStartOfDay();
      if (t.isBefore(from)) {
        t = t.plusDays(1);
      }
      for (; !t.isAfter(to); t = t.plusDays(step)) {
        add(ticks, t, DAY_LABEL.format(t));
      }
    } else {
      int step = step(span / HOUR, new int[] {1, 2, 3, 6, 12});
      LocalDateTime t = from.withMinute(0).withSecond(0).withNano(0);
      if (t.isBefore(from)) {
        t = t.plusHours(1);
      }
      while (t.getHour() % step != 0) {
        t = t.plusHours(1);
      }
      for (; !t.isAfter(to); t = t.plusHours(step)) {
        add(ticks, t, HOUR_LABEL.format(t));
      }
    }
    return ticks;
  }

  /** The smallest of <code>steps</code> that leaves no more than {@link #MAX_TICKS} ticks. */
  private static int step(double units, int[] steps) {
    for (int step : steps) {
      if (units / step <= MAX_TICKS) {
        return step;
      }
    }
    return (int) Math.ceil(units / MAX_TICKS);
  }

  private static LocalDateTime at(double seconds) {
    return EPOCH.plusSeconds((long) Math.floor(seconds));
  }

  private static void add(IASTAppendable ticks, LocalDateTime t, String label) {
    ticks.append(F.List(F.ZZ(Duration.between(EPOCH, t).getSeconds()), F.stringx(label)));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
