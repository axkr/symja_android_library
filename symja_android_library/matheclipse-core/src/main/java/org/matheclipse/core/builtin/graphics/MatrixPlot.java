package org.matheclipse.core.builtin.graphics;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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

  // CPD-OFF matrix plots share the preamble, the cell colouring differs
  public MatrixPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // the shape tests below read the data through any display wrapper; `wrappedAST` keeps the
    // wrapper, so the label can still be put over the finished picture
    final IAST wrappedAST = ast;
    ast = PlotWrapper.stripFirstArgument(ast);
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg = GraphicsOptions.matrixPlotData(ast.arg1(), originalAST, engine);
    if (dataArg.isNIL()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    GraphicsOptions.MatrixPlotOptions matrixOptions =
        GraphicsOptions.readMatrixPlotOptions(originalAST);

    // MaxPlotPoints has already thinned a large matrix to a sample of its entries
    IAST list = (IAST) dataArg;
    int rows = list.argSize();
    if (rows == 0)
      return F.NIL;

    int cols = 0;
    // CPD-ON
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
    final boolean scaling = matrixOptions.colorFunctionScaling;
    // The default colours and a ColorFunction see a value by its place among the others, half by
    // rank and half by size, which is what keeps a matrix of wildly different magnitudes readable.
    final DoubleUnaryOperator scale = scaling ? hybridScale(sortedFiniteValues(data)) : null;
    PlotColorFunction colorMap = PlotColorFunction
        .of(PlotColorFunction.Family.ARRAY, matrixOptions.colorFunction, F.bool(scaling),
            S.MatrixPlot, engine)
        .range(1, minValue(data), maxValue(data)).scaler(1, scale)
        .sink(PlotColorFunction.Sink.FLAT).fallback(GraphicsOptions::getMatrixColor).build();
    // compiled once for the whole matrix: the rules are matched like Replace, once per cell
    GraphicsOptions.ColorRuleTable colorRules =
        GraphicsOptions.colorRules(matrixOptions.colorRules, engine);
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
              GraphicsOptions.getMatrixColor(scaling ? scale.applyAsDouble(val) : val);
        }
      }
    }
    GraphicsOptions.finishMatrixRaster(primitives, cells, rows, cols, matrixOptions.mesh,
        graphicsOptions, originalAST, S.Automatic);

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
    Arrays.sort(values);
    return values;
  }

  /**
   * Where a value sits on the colour scale of the matrix, from 0 to 1.
   *
   * <p>
   * Scaling linearly between the smallest and largest entry collapses the picture whenever the
   * values span orders of magnitude, as for {@code Table[Binomial[n, k], ...]}. So each distinct
   * value, together with an implicit 0, is placed halfway between its rank among the distinct
   * values and its linear position between the smallest and the largest one. The result is then
   * stretched so that 0 lands on 1/2, positive values above and negative ones below, the longer
   * side of 0 reaching the end of the scale. For {@code {{1, 10}, {-5, 0}}} that is 0.65, 1, 0.25
   * and 0.5.
   *
   * @param sortedValues the finite entries in ascending order
   */
  private static DoubleUnaryOperator hybridScale(double[] sortedValues) {
    // the distinct values together with 0; -0.0 is 0 here, which Arrays.binarySearch would not
    // consider equal
    double[] distinct = new double[sortedValues.length + 1];
    int count = 0;
    boolean zeroAdded = false;
    for (double v : sortedValues) {
      double value = v == 0.0 ? 0.0 : v;
      if (!zeroAdded && value >= 0.0) {
        zeroAdded = true;
        if (value > 0.0) {
          distinct[count++] = 0.0;
        }
      }
      if (count == 0 || distinct[count - 1] != value) {
        distinct[count++] = value;
      }
    }
    if (!zeroAdded) {
      distinct[count++] = 0.0;
    }
    if (count == 1) {
      // nothing but zeros, which are the middle of the scale
      return v -> 0.5;
    }
    final double[] values = Arrays.copyOf(distinct, count);
    final double low = values[0];
    final double width = values[count - 1] - low;
    final double[] mixed = new double[count];
    for (int k = 0; k < count; k++) {
      mixed[k] = 0.5 * k / (count - 1) + 0.5 * (values[k] - low) / width;
    }
    final double zero = mixed[Arrays.binarySearch(values, 0.0)];
    return v -> {
      int k = Arrays.binarySearch(values, v == 0.0 ? 0.0 : v);
      if (k < 0) {
        // not an entry of the matrix: the nearest one below it
        k = Math.max(0, Math.min(values.length - 1, -k - 2));
      }
      return zero <= 0.5 ? 0.5 + 0.5 * (mixed[k] - zero) / (1.0 - zero) : 0.5 * mixed[k] / zero;
    };
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
