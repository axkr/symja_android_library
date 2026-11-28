package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The <code>PlotLayout</code> option of <code>Plot</code> and the list plots:
 *
 * <ul>
 * <li><code>"Overlaid"</code> and <code>Automatic</code>: all curves in one plot;
 * <li><code>"Stacked"</code>: dataset <code>k</code> is drawn at the sum of datasets
 * <code>1 ... k</code>, point by point;
 * <li><code>"Percentile"</code>: the same sums as percentages of the sum of all datasets, so the top
 * curve runs at <code>100</code>;
 * <li><code>"Row"</code>, <code>"Column"</code>, <code>{"Row", n}</code>, <code>{"Column", n}</code>:
 * one plot per curve, side by side, below each other, or in <code>n</code> rows or columns. Each
 * keeps the colour it has in the overlaid plot.
 * </ul>
 */
final class PlotLayouts {

  private PlotLayouts() {}

  /** The setting of <code>PlotLayout</code> in a call, or {@link S#Automatic}. */
  static IExpr layout(IAST originalAST) {
    return GraphicsOptions.optionValue(originalAST, S.PlotLayout, S.Automatic);
  }

  /** Whether a layout draws one plot per curve. */
  static boolean isPanels(IExpr layout) {
    IExpr kind = layout.isList2() ? layout.first() : layout;
    return kind.isString("Row") || kind.isString("Column");
  }

  /**
   * One plot per curve, laid out as the <code>PlotLayout</code> asks.
   *
   * @param originalAST the call with its options
   * @param curves the datasets or functions, one per plot
   * @return a <code>GraphicsRow</code>, <code>GraphicsColumn</code> or <code>GraphicsGrid</code>
   *         of the plots, or {@link F#NIL} when there is only one curve
   */
  static @AllowNIL IExpr panels(IExpr layout, IAST originalAST, IAST curves, EvalEngine engine) {
    int count = curves.argSize();
    if (count < 2) {
      return F.NIL;
    }
    IExpr plotStyle = GraphicsOptions.optionValue(originalAST, S.PlotStyle, S.Automatic);
    IASTAppendable plots = F.ListAlloc(count);
    for (int i = 1; i <= count; i++) {
      IASTAppendable call = F.ast(originalAST.head(), originalAST.size() + 1);
      call.append(curves.get(i));
      for (int j = 2; j < originalAST.size(); j++) {
        IExpr arg = originalAST.get(j);
        if (arg.isRuleAST() && (arg.first() == S.PlotLayout || arg.first() == S.PlotStyle
            || arg.first() == S.PlotLegends)) {
          continue;
        }
        call.append(arg);
      }
      // the curve keeps the colour it has among the others
      RGBColor color = GraphicsOptions.plotStyleColor(i - 1, plotStyle);
      IExpr style = F.RGBColor(color.getRed() / 255.0, color.getGreen() / 255.0,
          color.getBlue() / 255.0);
      if (plotStyle.isList() && plotStyle.argSize() >= 1) {
        style = plotStyle.getAt((i - 1) % plotStyle.argSize() + 1);
      } else if (plotStyle.isPresent() && !plotStyle.isAutomatic() && !plotStyle.isList()) {
        style = plotStyle;
      }
      call.append(F.Rule(S.PlotStyle, style));
      plots.append(engine.evaluate(call));
    }
    boolean column = (layout.isList2() ? layout.first() : layout).isString("Column");
    int lines = layout.isList2() ? layout.second().toIntDefault(0) : 0;
    if (lines <= 1 || lines >= count) {
      return F.unaryAST1(column ? S.GraphicsColumn : S.GraphicsRow, plots);
    }
    // {"Row", n}: n rows, filled row by row; {"Column", n}: n columns, filled column by column
    int perLine = (count + lines - 1) / lines;
    int rows = column ? perLine : lines;
    int cols = column ? lines : perLine;
    IASTAppendable grid = F.ListAlloc(rows);
    for (int r = 0; r < rows; r++) {
      IASTAppendable row = F.ListAlloc(cols);
      for (int c = 0; c < cols; c++) {
        int index = column ? c * rows + r : r * cols + c;
        row.append(index < count ? plots.get(index + 1) : F.CEmptyString);
      }
      grid.append(row);
    }
    return F.unaryAST1(S.GraphicsGrid, grid);
  }

  /**
   * The datasets of a list plot as <code>"Stacked"</code> or <code>"Percentile"</code> draws them:
   * dataset <code>k</code> moved to the sum of the values of datasets <code>1 ... k</code> at the
   * same x (the position of a plain value, the x of a pair), as a percentage of the sum of all of
   * them for <code>"Percentile"</code>. A dataset is a list of values or of <code>{x, y}</code>
   * pairs.
   *
   * @return the datasets, or {@link F#NIL} when the layout stacks nothing or the data doesn't fit
   */
  static @AllowNIL IAST stacked(IExpr layout, IExpr data) {
    boolean percentile = layout.isString("Percentile");
    if (!(percentile || layout.isString("Stacked")) || !data.isList() || data.argSize() < 2) {
      return F.NIL;
    }
    IAST datasets = (IAST) data;
    int length = 0;
    for (IExpr dataset : datasets) {
      if (!dataset.isList()) {
        return F.NIL;
      }
      length = Math.max(length, dataset.argSize());
    }
    int n = datasets.argSize();
    double[][] y = new double[n][length];
    boolean[][] pairs = new boolean[n][length];
    IExpr[][] x = new IExpr[n][length];
    for (int k = 0; k < n; k++) {
      IAST dataset = (IAST) datasets.get(k + 1);
      for (int i = 0; i < length; i++) {
        y[k][i] = Double.NaN;
        if (i < dataset.argSize()) {
          IExpr p = dataset.get(i + 1);
          if (p.isList2()) {
            pairs[k][i] = true;
            // the x entry stays as it is: a date list or a DateObject is stacked like a number
            x[k][i] = p.first();
            y[k][i] = p.second().evalfNaN();
          } else {
            y[k][i] = p.evalfNaN();
          }
        }
      }
    }
    // the values are added where the datasets share an x: the position 1, 2, ... of a plain
    // value, the x of an {x, y} pair (equal numbers, or equal dates)
    Object[][] keys = new Object[n][length];
    java.util.Map<Object, Double> sum = new java.util.HashMap<>();
    java.util.Map<Object, Double> total = new java.util.HashMap<>();
    for (int k = 0; k < n; k++) {
      for (int i = 0; i < length; i++) {
        keys[k][i] = pairs[k][i] ? xKey(x[k][i]) : Double.valueOf(i + 1);
        if (Double.isFinite(y[k][i])) {
          total.merge(keys[k][i], y[k][i], Double::sum);
        }
      }
    }
    IASTAppendable out = F.ListAlloc(n);
    for (int k = 0; k < n; k++) {
      IAST dataset = (IAST) datasets.get(k + 1);
      IASTAppendable stackedSet = F.ListAlloc(dataset.argSize());
      for (int i = 0; i < dataset.argSize(); i++) {
        if (!Double.isFinite(y[k][i])) {
          stackedSet.append(dataset.get(i + 1));
          continue;
        }
        double s = sum.merge(keys[k][i], y[k][i], Double::sum);
        double t = total.get(keys[k][i]);
        double value = percentile ? (t == 0 ? 0.0 : 100.0 * s / t) : s;
        stackedSet.append(pairs[k][i] ? F.List(x[k][i], F.num(value)) : F.num(value));
      }
      out.append(stackedSet);
    }
    return out;
  }

  /** The key of an x entry: its value for a number, the expression itself for a date. */
  private static Object xKey(IExpr x) {
    double v = x.evalfNaN();
    return Double.isFinite(v) ? Double.valueOf(v == 0.0 ? 0.0 : v) : x;
  }
}
