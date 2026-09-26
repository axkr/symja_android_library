package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotWrapper;
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

  /**
   * The data is drawn as one field rather than point by point, so no wrapper is read here; the
   * label goes over the whole picture instead.
   */
  @Override
  protected boolean readsArgumentWrapper() {
    return false;
  }

  public MatrixPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // the shape tests below read the data through any display wrapper; `wrappedAST` keeps the
    // wrapper, so the label can still be put over the finished picture
    final IAST wrappedAST = ast;
    if (ast.size() > 1) {
      IExpr unwrapped = PlotWrapper.strip(ast.arg1());
      if (unwrapped != ast.arg1()) {
        ast = ast.setAtCopy(1, unwrapped);
      }
    }
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
            IExpr entry = rowAst.get(c + 1);
            if (entry.isNumber() && !entry.isReal()) {
              // a complex entry is drawn by its real part
              entry = ((org.matheclipse.core.interfaces.INumber) entry).re();
            }
            double val = entry.evalfNaN();
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
    // compiled once for the whole matrix: the rules are matched like Replace, once per cell
    GraphicsOptions.ColorRuleTable colorRules = GraphicsOptions.colorRules(colorRulesOpt, engine);
    IExpr[][] cells = new IExpr[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        double val = data[r][c];
        if (Double.isNaN(val)) {
          continue;
        }
        // an explicit rule for this value wins, then ColorFunction, then the matrix colour map
        IExpr ruleColor =
            colorRules == null ? null : colorRules.color(list.getAt(r + 1).getAt(c + 1));
        if (ruleColor != null) {
          cells[r][c] = ruleColor;
        } else if (colorMap != null) {
          cells[r][c] = colorMap.color(val);
        } else {
          cells[r][c] =
              GraphicsOptions.getMatrixColor(scaling ? rankFraction(sortedValues, val) : val);
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

    // one tick per cell index at the centre of its cell, row 1 at the top; explicit FrameTicks of
    // the caller's own are left alone
    IExpr frameTicksOpt = GraphicsOptions.optionValue(originalAST, S.FrameTicks, S.Automatic);
    if (frameTicksOpt.isAutomatic() || frameTicksOpt.isTrue()) {
      // through addOption, not setFrameTicks: the field is not one of the values getListOfRules
      // emits, so setting it alone leaves the registered default of None in the output
      graphicsOptions
          .addOption(F.Rule(S.FrameTicks, GraphicsOptions.matrixIndexFrameTicks(rows, cols)));
    }

    return createGraphicsFunction(primitives, graphicsOptions, wrappedAST);
  }

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

  /** Every finite entry of the matrix, in ascending order. */
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
    // Each sign is ranked on its own and zero is the middle of the scale, which is white: the
    // negative entries fill 0..0.5 (the most negative at 0), the positive ones 0.5..1 (the largest
    // at 1)
    if (value == 0.0 || sortedValues.length == 0) {
      return 0.5;
    }
    int negatives = countBelow(sortedValues, 0.0);
    int firstPositive = sortedValues.length - countAbove(sortedValues, 0.0);
    int positives = sortedValues.length - firstPositive;
    int below = countBelow(sortedValues, value);
    if (value < 0.0) {
      return negatives <= 1 ? 0.0 : 0.5 * below / negatives;
    }
    return positives <= 1 ? 1.0 : 0.5 + 0.5 * (below - firstPositive + 1) / positives;
  }

  /** The number of entries strictly smaller than <code>value</code>. */
  private static int countBelow(double[] sortedValues, double value) {
    int low = 0;
    int high = sortedValues.length;
    while (low < high) {
      int mid = (low + high) >>> 1;
      if (sortedValues[mid] < value) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }
    return low;
  }

  /** The number of entries strictly greater than <code>value</code>. */
  private static int countAbove(double[] sortedValues, double value) {
    int low = 0;
    int high = sortedValues.length;
    while (low < high) {
      int mid = (low + high) >>> 1;
      if (sortedValues[mid] <= value) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }
    return sortedValues.length - low;
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
