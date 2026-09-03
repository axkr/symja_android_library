package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Matrix Plots.
 * <p>
 * Example: <code>MatrixPlot[RandomReal[1, {10, 10}]]</code>
 */
public class MatrixPlot extends ListPlot {

  public MatrixPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg = engine.evaluate(ast.arg1());
    if (!dataArg.isList()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    boolean colorFunctionScaling = true;
    IExpr colorFunctionOpt = S.Automatic;
    IExpr colorRulesOpt = S.None;
    IExpr meshOpt = S.None;
    // the options array holds resolved values, not the rules the caller wrote,
    // so the option rules are read back off the original call
    for (IExpr opt : originalAST) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) key).ordinal()) {
            case ID.ColorFunctionScaling:
              if (val.isFalse()) {
                colorFunctionScaling = false;
              }
              break;
            case ID.ColorFunction:
              colorFunctionOpt = val;
              break;
            case ID.ColorRules:
              colorRulesOpt = val;
              break;
            case ID.Mesh:
              meshOpt = val;
              break;
          }
        }
      }
    }

    IAST list = (IAST) dataArg;
    // MaxPlotPoints draws a large matrix from a sample of it rather than from every entry
    IAST thinned = GraphicsOptions.downsampleMatrix(list,
        GraphicsOptions.optionValue(originalAST, S.MaxPlotPoints, S.Automatic).toIntDefault(-1));
    if (thinned.isPresent()) {
      list = thinned;
    }
    int rows = list.argSize();
    if (rows == 0)
      return F.NIL;

    int cols = 0;
    for (IExpr row : list) {
      if (row.isList()) {
        cols = Math.max(cols, ((IAST) row).argSize());
      }
    }
    if (cols == 0)
      return F.NIL;

    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;

    double[][] data = new double[rows][cols];
    for (int r = 0; r < rows; r++) {
      IExpr rowExpr = list.get(r + 1);
      if (rowExpr.isList()) {
        IAST rowAst = (IAST) rowExpr;
        for (int c = 0; c < Math.min(cols, rowAst.size()); c++) {
          try {
            double val = rowAst.get(c + 1).evalfNaN();
            data[r][c] = val;
            if (Double.isFinite(val)) {
              if (val < min)
                min = val;
              if (val > max)
                max = val;
            }
          } catch (Exception e) {
            data[r][c] = Double.NaN;
          }
        }
        for (int c = rowAst.size(); c < cols; c++)
          data[r][c] = Double.NaN;
      } else {
        for (int c = 0; c < cols; c++)
          data[r][c] = Double.NaN;
      }
    }

    IASTAppendable primitives = F.ListAlloc();

    // One raster rather than one rectangle per cell: row 0 of the matrix is drawn at the top,
    // which is the order rasterTopFirst expects.
    final double[] sortedValues = colorFunctionScaling ? sortedFiniteValues(data) : null;
    final boolean scaling = colorFunctionScaling;
    // The default scale places a value by its rank among the others, which is what keeps a matrix
    // of wildly different magnitudes readable. A ColorFunction is given the plain range instead:
    // a caller who wrote GrayLevel(#) asked for the value, not for its position in a sort.
    PlotColorFunction colorMap = PlotColorFunction
        .of(PlotColorFunction.Family.ARRAY, colorFunctionOpt, F.bool(scaling), S.MatrixPlot, engine)
        .range(1, minValue(data), maxValue(data)).sink(PlotColorFunction.Sink.FLAT)
        .fallback(GraphicsOptions::getMatrixColor).build();
    IExpr[][] cells = new IExpr[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        double val = data[r][c];
        if (Double.isNaN(val)) {
          continue;
        }
        // an explicit rule for this value wins, then ColorFunction, then the matrix colour map
        IExpr ruleColor = GraphicsOptions.colorRule(colorRulesOpt, list.getAt(r + 1).getAt(c + 1));
        if (ruleColor != null) {
          cells[r][c] = ruleColor;
        } else if (colorMap != null) {
          cells[r][c] = colorMap.color(val);
        } else {
          cells[r][c] = GraphicsOptions
              .getMatrixColor(scaling ? rankFraction(sortedValues, val) : val);
        }
      }
    }
    primitives.append(GraphicsOptions.rasterTopFirst(cells, 0, 0, cols, rows));
    IExpr meshLines = GraphicsOptions.meshGrid(meshOpt, 0, 0, cols, rows, cols, rows);
    if (meshLines.isPresent()) {
      primitives.append(meshLines);
    }

    graphicsOptions.setBoundingBox(new double[] {0, cols, 0, rows});

    if (graphicsOptions.aspectRatio() == S.Automatic) {
      graphicsOptions.setAspectRatio(F.num((double) rows / (double) cols));
    }

    // Generate FrameTicks to match MatrixPlot style
    // Top: Columns 1..Cols
    // Left: Rows 1..Rows (Inverted labels)

    // Top Ticks
    IASTAppendable topTicks = F.ListAlloc();
    List<Double> cTicks = getNiceTicks(0, cols, 10);
    for (double v : cTicks) {
      if (v > 0 && v <= cols) {
        topTicks.append(F.List(F.num(v), F.num(v))); // {val, label}
      }
    }

    // Left Ticks (Inverted: Row 1 is at top y=rows-0.5)
    IASTAppendable leftTicks = F.ListAlloc();
    List<Double> rTicks = getNiceTicks(0, rows, 10);
    for (double v : rTicks) {
      if (v > 0 && v <= rows) {
        // Map index v to Y coordinate: rows - v + 0.5 (center of cell)
        leftTicks.append(F.List(F.num(rows - v + 0.5), F.num(v)));
      }
    }

    // FrameTicks -> {{Left, Right}, {Bottom, Top}}. The reference rendering labels all four edges,
    // so the row ticks go on both sides and the column ticks on both top and bottom.
    IExpr frameTicks = F.List(F.List(leftTicks, leftTicks), F.List(topTicks, topTicks));
    // through addOption, not setFrameTicks: the field is not one of the values getListOfRules
    // emits, so setting it alone leaves the registered default of None in the output
    graphicsOptions.addOption(F.Rule(S.FrameTicks, frameTicks));

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /** Every finite entry of the matrix, in ascending order. */
  /** The smallest finite entry, or 0 when there is none. */
  private static double minValue(double[][] data) {
    double min = Double.POSITIVE_INFINITY;
    for (double[] row : data) {
      for (double v : row) {
        if (!Double.isNaN(v) && v < min) {
          min = v;
        }
      }
    }
    return Double.isFinite(min) ? min : 0.0;
  }

  /** The largest finite entry, or 1 when there is none. */
  private static double maxValue(double[][] data) {
    double max = Double.NEGATIVE_INFINITY;
    for (double[] row : data) {
      for (double v : row) {
        if (!Double.isNaN(v) && v > max) {
          max = v;
        }
      }
    }
    return Double.isFinite(max) ? max : 1.0;
  }

  private static double[] sortedFiniteValues(double[][] data) {
    int count = 0;
    for (double[] row : data) {
      for (double v : row) {
        if (Double.isFinite(v)) {
          count++;
        }
      }
    }
    double[] values = new double[count];
    int i = 0;
    for (double[] row : data) {
      for (double v : row) {
        if (Double.isFinite(v)) {
          values[i++] = v;
        }
      }
    }
    java.util.Arrays.sort(values);
    return values;
  }

  /**
   * Where a value sits in the distribution of the matrix, from 0 for the smallest to 1 for the
   * largest.
   *
   * <p>
   * Scaling linearly between the smallest and largest entry collapses the picture whenever the
   * values span orders of magnitude: for {@code Table[Binomial[n, k], ...]} that leaves about
   * ninety-eight percent of the cells within one percent of the pale end, so the plot reads as a
   * flat field with a single bright spot. Ranking the values instead spends the colour range on
   * where the data actually is. This is not the exact rescaling the reference rendering uses — that
   * one could not be recovered from the captured colours alone — but it is far closer than a linear
   * ramp, and it is monotonic, so the ordering of the cells is still faithful.
   */
  private static double rankFraction(double[] sortedValues, double value) {
    int n = sortedValues.length;
    if (n <= 1) {
      return 0.5;
    }
    // number of entries strictly smaller, so the smallest maps to 0 and the largest to 1
    int low = 0;
    int high = n;
    while (low < high) {
      int mid = (low + high) >>> 1;
      if (sortedValues[mid] < value) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }
    return (double) low / (n - 1);
  }

  private List<Double> getNiceTicks(double min, double max, int maxTicks) {
    List<Double> ticks = new ArrayList<>();
    double range = max - min;
    if (range <= 0)
      return ticks;
    double step = Math.pow(10, Math.floor(Math.log10(range / maxTicks)));
    if (2.0 * range / step < maxTicks)
      step /= 2;
    if (2.0 * range / step < maxTicks)
      step /= 2;
    else if (range / step > maxTicks * 2)
      step *= 2;

    double start = Math.ceil(min / step) * step;
    for (double t = start; t <= max; t += step) {
      ticks.add(t);
    }
    return ticks;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    // charts and rasters draw their own extent, so the reference rendering does not clip
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;

    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = S.Automatic;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.rasterExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
