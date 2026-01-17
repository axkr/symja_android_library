package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** Plots x/y functions */
public class Plot extends ListPlot {

  final static double NUMBER_OF_PIXELS = 1200.0;

  /** Constructor for the singleton */
  public static final Plot CONST = new Plot();

  public Plot() {}


  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2 || !ast.arg2().isList3() || !ast.arg2().first().isSymbol()) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      IExpr arg2 = argSize >= 2 ? ast.arg2() : F.CEmptyString;
      return Errors.printMessage(S.Plot, "pllim", F.list(arg2), engine);
    }

    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }
    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    if (argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }
    IExpr function = ast.arg1();
    if (ast.arg2().isList3()) {
      final IAST rangeList = (IAST) ast.arg2();
      IExpr variable = rangeList.arg1();
      if (variable.isVariable()) {
        try {
          if (rangeList.isList3()) {

            final IAST listOfLines =
                plotToListPoints(function, rangeList, ast, graphicsOptions, engine);
            if (listOfLines.isNIL()) {
              return F.NIL;
            }

            if (ToggleFeature.JS_ECHARTS) {
              String graphicsPrimitivesStr = listPlotECharts(listOfLines, graphicsOptions);
              if (graphicsPrimitivesStr != null) {
                StringBuilder jsControl = new StringBuilder();
                jsControl.append("var eChart = echarts.init(document.getElementById('main'));\n");
                jsControl.append(graphicsPrimitivesStr);
                jsControl.append("\neChart.setOption(option);");

                return F.JSFormData(jsControl.toString(), "echarts");
              }
              return F.NIL;
            } else {
              // simulate ListPlot data
              GraphicsOptions listPlotOptions = graphicsOptions.copy();
              IASTMutable listPlot = ast.setAtCopy(1, listOfLines);
              IAST graphicsPrimitives = plot(listPlot, options, listPlotOptions, engine);
              if (graphicsPrimitives.isPresent()) {
                return createGraphicsFunction(graphicsPrimitives, listPlotOptions, ast);
              }
            }

          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          rex.printStackTrace();
        }
      }
    }
    return F.NIL;
  }

  @Override
  protected IExpr createGraphicsFunction(IAST graphicsPrimitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    graphicsOptions.setBoundingBox(graphicsOptions.boundingBox());
    return super.createGraphicsFunction(graphicsPrimitives, graphicsOptions, plotAST);
  }

  private static IAST plotToListPoints(IExpr functionOrListOfFunctions, final IAST rangeList,
      final IAST ast, GraphicsOptions graphicsOptions, EvalEngine engine) {
    if (!rangeList.arg1().isSymbol()) {
      // `1` is not a valid variable.
      return Errors.printMessage(ast.topHead(), "ivar", F.list(rangeList.arg1()), engine);
    }
    final ISymbol x = (ISymbol) rangeList.arg1();
    final IExpr xMin = engine.evalN(rangeList.arg2());
    final IExpr xMax = engine.evalN(rangeList.arg3());
    if ((!(xMin instanceof INum)) || (!(xMax instanceof INum)) || xMin.equals(xMax)) {
      // Endpoints in `1` must be distinct machine-size real numbers.
      return Errors.printMessage(ast.topHead(), "plld", F.List(x, rangeList), engine);
    }
    double xMinD = ((INum) xMin).getRealPart();
    double xMaxD = ((INum) xMax).getRealPart();
    if (xMaxD < xMinD) {
      double temp = xMinD;
      xMinD = xMaxD;
      xMaxD = temp;
    }

    final IAST list = functionOrListOfFunctions.makeList();
    int size = list.size();
    List<double[][]> dataList = new ArrayList<double[][]>(size - 1);
    final IASTAppendable listOfLines = F.ListAlloc(size - 1);
    double[] yMinMax = new double[] {Double.MAX_VALUE, Double.MIN_VALUE};

    // Ensure we have a valid scale string
    String scale = graphicsOptions.xScale();
    if (scale == null || scale.isEmpty()) {
      scale = "Linear";
    }

    for (int i = 1; i < size; i++) {
      IExpr function = list.get(i);
      double[][] data = null;
      final UnaryNumerical hun = new UnaryNumerical(function, x, engine);
      data = org.matheclipse.core.sympy.plotting.Plot.computePlot(hun, data, xMinD, xMaxD, scale);
      if (data != null) {
        dataList.add(data);
        automaticYPlotRange(//
            graphicsOptions.plotRange().isAutomatic() ? function : null, //
            data[1], yMinMax);
      }
    }
    // graphicsOptions.mergeOptions(graphicsOptions.options().getListOfRules(), yMinMax);
    for (int i = 0; i < dataList.size(); i++) {
      IExpr temp = plotLine(dataList.get(i), x, xMinD, xMaxD, yMinMax, graphicsOptions, engine);
      if (temp.isPresent()) {
        listOfLines.append(temp);
      }
    }
    return listOfLines;
  }

  /**
   * Plot a single data line. * @param data the function data points which should be plotted
   * * @param xVar the variable symbol
   * 
   * @param xMin the minimum x-range value
   * @param xMax the maximum x-range value
   * @param yMinMax the y plot-range
   * @param graphicsOptions options context
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> is no conversion of the data into an <code>IExpr</code> was possible
   */
  private static IAST plotLine(double[][] data, final ISymbol xVar, final double xMin,
      final double xMax, double[] yMinMax, GraphicsOptions graphicsOptions,
      final EvalEngine engine) {

    graphicsOptions.setBoundingBoxScaled(new double[] {xMin, xMax, yMinMax[0], yMinMax[1]});
    final IASTAppendable listOfLines = F.ListAlloc();
    IASTAppendable lineList = F.NIL;
    double lastx = Double.NaN;
    double lasty = Double.NaN;

    // Discontinuity Detection Setup
    DoubleUnaryOperator tx = GraphicsOptions.getScalingFunction(graphicsOptions.xScale());
    DoubleUnaryOperator ty = GraphicsOptions.getScalingFunction(graphicsOptions.yScale());

    // Normalize Dimensions for Visual Slope Calculation
    double tMinX = tx.applyAsDouble(xMin);
    double tMaxX = tx.applyAsDouble(xMax);
    double tMinY = ty.applyAsDouble(yMinMax[0]);
    double tMaxY = ty.applyAsDouble(yMinMax[1]);

    double spanX = Math.abs(tMaxX - tMinX);
    double spanY = Math.abs(tMaxY - tMinY);
    if (spanX < 1.0e-9)
      spanX = 1.0;
    if (spanY < 1.0e-9)
      spanY = 1.0;

    // Heuristic Thresholds
    // slope > 100 means visual angle > 89 degrees
    double jumpSlopeThreshold = 100.0;
    // flat < 10 means visual angle < 84 degrees (considered "flat" relative to a jump)
    double flatSlopeThreshold = 10.0;

    double lastSlope = 0.0;

    for (int i = 0; i < data[0].length; i++) {
      double curX = data[0][i];
      double curY = data[1][i];

      if (!Double.isFinite(curY)) {
        if (lineList.isPresent()) {
          listOfLines.append(lineList);
          lineList = F.NIL;
        }
        lastx = Double.NaN;
        lasty = Double.NaN;
        lastSlope = 0.0;
        continue;
      }

      // Clipping
      if (curY < yMinMax[0] || curY > yMinMax[1]) {
        if (lineList.isPresent()) {
          lineList.append(graphicsOptions.point(curX, curY));
          listOfLines.append(lineList);
          lineList = F.NIL;
          lastx = Double.NaN;
          lasty = Double.NaN;
          lastSlope = 0.0;
        } else {
          lastx = curX;
          lasty = curY;
        }
        continue;
      }

      // --- Smart Discontinuity Detection ---
      boolean breakLine = false;
      if (Double.isFinite(lastx) && Double.isFinite(lasty)) {
        double transX = tx.applyAsDouble(curX);
        double transY = ty.applyAsDouble(curY);
        double lastTransX = tx.applyAsDouble(lastx);
        double lastTransY = ty.applyAsDouble(lasty);

        double dx = Math.abs(transX - lastTransX) / spanX;
        double dy = Math.abs(transY - lastTransY) / spanY;

        double currentSlope = (dx < 1e-9) ? Double.MAX_VALUE : (dy / dx);

        // Heuristic: If current step is very steep AND previous step was relatively flat
        // it indicates a discrete jump (Step Function).
        // Continuous steep curves (like Sin[E^x]) are steep for multiple segments.
        if (dx < 0.01 && currentSlope > jumpSlopeThreshold) {
          if (lastSlope < flatSlopeThreshold) {
            breakLine = true;
          }
        }
        lastSlope = currentSlope;
      } else {
        lastSlope = 0.0; // Reset on new segment
      }

      if (breakLine) {
        if (lineList.isPresent()) {
          listOfLines.append(lineList);
          lineList = F.NIL;
        }
        lastx = Double.NaN;
        lasty = Double.NaN;

        // Start new line from current point
        lineList = F.ListAlloc();
        lineList.append(graphicsOptions.point(curX, curY));
        lastx = curX;
        lasty = curY;
        continue;
      }

      if (lineList.isNIL()) {
        lineList = F.ListAlloc();
        if (Double.isFinite(lastx) && Double.isFinite(lasty)) {
          lineList.append(graphicsOptions.point(lastx, lasty));
        }
      }
      lineList.append(graphicsOptions.point(curX, curY));
      lastx = curX;
      lasty = curY;
    }
    if (lineList.isPresent()) {
      listOfLines.append(lineList);
      lineList = F.NIL;
    }
    return listOfLines;
  }

  /**
   * Automatic y-plot range determination based on robust percentiles (10th-90th) to handle
   * singularities and exponential growth gracefully.
   *
   * @param function the current function
   * @param values current y-values of the current function curve
   * @param yMinMax y-plot range which will be updated [min, max]
   */
  private static void automaticYPlotRange(final IExpr function, final double[] values,
      double[] yMinMax) {
    if (values == null || values.length == 0) {
      return; // No data to analyze
    }

    if (function != null) {
      int headID = function.headID();
      switch (headID) {
        case ID.Cot:
        case ID.Csc:
        case ID.Sec:
        case ID.Tan:
          setYRange(-7.0, 7.0, yMinMax);
          return;
      }
    }

    // Filter and Sort Data
    // We need a sorted list of finite values to determine percentiles.
    double[] sorted = Arrays.stream(values).filter(Double::isFinite).sorted().toArray();
    int n = sorted.length;

    if (n == 0) {
      return; // All NaNs
    }

    // Identify Core Distribution (10th to 90th percentile)
    // This ignores the extreme tails (asymptotes or exponential explosions).
    double p10 = sorted[(int) (n * 0.10)];
    double p90 = sorted[(int) (n * 0.90)];
    double median = sorted[n / 2];

    double bodyRange = p90 - p10;

    // Handle flat functions (e.g., y=5)
    if (bodyRange < 1.0e-9) {
      double margin = Math.abs(median) * 0.1;
      if (margin < 1.0e-9)
        margin = 1.0;
      setYRange(median - margin, median + margin, yMinMax);
      return;
    }

    // Define "Reasonable" Visual Bounds
    // Expand the core body by a factor (1.5x) to include "interesting" variation
    // but cut off extreme outliers found in the top/bottom 10%.
    double expansionFactor = 1.5;
    double proposedMin = p10 - (bodyRange * expansionFactor);
    double proposedMax = p90 + (bodyRange * expansionFactor);

    // Clamp to Actual Data Limits
    // We never want to show a range *larger* than the actual data exists (empty whitespace).
    // But we *do* want to show a range *smaller* than data if data has singularities.
    double actualMin = sorted[0];
    double actualMax = sorted[n - 1];

    // If proposed bound extends beyond actual data, clamp it.
    // If proposed bound is inside actual data (cutting off singularity), keep it.
    double finalMin = Math.max(proposedMin, actualMin);
    double finalMax = Math.min(proposedMax, actualMax);

    if (actualMin >= 0 && finalMin < 0) {
      finalMin = actualMin;
    }

    setYRange(finalMin, finalMax, yMinMax);
  }


  private static void setYRange(double vmin, double vmax, double[] yMinMax) {
    if (vmin < yMinMax[0]) {
      yMinMax[0] = vmin;
    }
    if (vmax > yMinMax[1]) {
      yMinMax[1] = vmax;
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, true);
    // Explicitly default AspectRatio to 1/GoldenRatio for Plot (NOT Automatic)
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.Power(S.GoldenRatio, F.CN1);
    defaults[GraphicsOptions.X_PLOTRANGE] = S.Automatic;
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}
