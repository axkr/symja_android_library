package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphicsUtil;
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
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.Plot, "pllim", F.list(arg2), engine);
    }

    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.funEval(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }
    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine, originalAST);
    // PlotMarkers and Mesh are family options appended after the positional block, so they
    // are read from the call rather than by index
    graphicsOptions
        .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.Automatic));
    graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
    graphicsOptions.readColorFunction(originalAST);
    graphicsOptions.applyPlotTheme(originalAST);

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
                plotToListPoints(function, rangeList, ast, graphicsOptions, engine,
                    GraphicsOptions.optionValue(originalAST, S.PlotPoints, S.Automatic)
                        .toIntDefault(-1),
                    GraphicsOptions.optionValue(originalAST, S.MaxRecursion, S.Automatic)
                        .toIntDefault(-1),
                    GraphicsOptions.optionValue(originalAST, S.Exclusions, S.Automatic),
                    GraphicsOptions.optionValue(originalAST, S.WorkingPrecision, S.MachinePrecision)
                        .toIntDefault(-1));
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
      final IAST ast, GraphicsOptions graphicsOptions, EvalEngine engine, int plotPoints,
      int maxRecursion, IExpr exclusions, int workingPrecision) {
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
      final UnaryNumerical hun = new UnaryNumerical(function, x, Double.NaN, engine);
      // WorkingPrecision only matters where machine arithmetic loses the answer
      hun.setPrecision(workingPrecision);
      data = org.matheclipse.core.sympy.plotting.Plot.computePlot(hun, data, xMinD, xMaxD, scale,
          plotPoints, maxRecursion);
      if (data != null) {
        dataList.add(data);
        GraphicsUtil.automaticPlotRange2D(//
            graphicsOptions.plotRange().isAutomatic() ? function : null, //
            data[1], yMinMax);
      }
    }
    // graphicsOptions.mergeOptions(graphicsOptions.options().getListOfRules(), yMinMax);
    for (int i = 0; i < dataList.size(); i++) {
      IExpr temp =
          plotLine(dataList.get(i), x, xMinD, xMaxD, yMinMax, graphicsOptions, engine, exclusions);
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
  /**
   * The x positions {@code Exclusions} asks the curve to be broken at.
   *
   * <p>
   * Accepts a bare value, a list of them, and equations such as {@code x == 0}, which are solved
   * for the plot variable. Anything that does not reduce to a real position is passed over rather
   * than failing the plot.
   *
   * @param exclusions the option value
   * @param xVar the plot variable
   */
  private static double[] exclusionValues(IExpr exclusions, ISymbol xVar, EvalEngine engine) {
    if (exclusions == null || exclusions.isAutomatic() || exclusions.isNone()
        || exclusions.isFalse()) {
      return new double[0];
    }
    List<Double> cuts = new ArrayList<Double>();
    collectExclusions(exclusions, xVar, engine, cuts);
    double[] out = new double[cuts.size()];
    for (int i = 0; i < out.length; i++) {
      out[i] = cuts.get(i);
    }
    return out;
  }

  private static void collectExclusions(IExpr spec, ISymbol xVar, EvalEngine engine,
      List<Double> cuts) {
    if (spec.isList()) {
      IAST list = (IAST) spec;
      for (int i = 1; i < list.size(); i++) {
        collectExclusions(list.get(i), xVar, engine, cuts);
      }
      return;
    }
    double value = spec.evalfNaN();
    if (Double.isFinite(value)) {
      cuts.add(value);
      return;
    }
    // an equation names the positions by their solutions
    try {
      IExpr solutions = S.Solve.of(engine, spec, xVar);
      if (solutions.isList()) {
        IAST list = (IAST) solutions;
        for (int i = 1; i < list.size(); i++) {
          IExpr rules = list.get(i);
          if (!rules.isList()) {
            continue;
          }
          IAST ruleList = (IAST) rules;
          for (int j = 1; j < ruleList.size(); j++) {
            if (ruleList.get(j).isRuleAST()) {
              double solved = ((IAST) ruleList.get(j)).second().evalfNaN();
              if (Double.isFinite(solved)) {
                cuts.add(solved);
              }
            }
          }
        }
      }
    } catch (RuntimeException rex) {
      // an exclusion that cannot be solved simply does not break the curve
    }
  }

  private static IAST plotLine(double[][] data, final ISymbol xVar, final double xMin,
      final double xMax, double[] yMinMax, GraphicsOptions graphicsOptions, final EvalEngine engine,
      IExpr exclusions) {
    // Exclusions -> None keeps the curve unbroken across a jump; an explicit list breaks it where
    // the caller says instead of where the jump detector thinks
    boolean detectJumps = exclusions == null || exclusions.isAutomatic();
    double[] excluded = exclusionValues(exclusions, xVar, engine);

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
        if (detectJumps && dx < 0.01 && currentSlope > jumpSlopeThreshold) {
          if (lastSlope < flatSlopeThreshold) {
            breakLine = true;
          }
        }
        for (double cut : excluded) {
          if (cut > Math.min(lastx, curX) && cut <= Math.max(lastx, curX)) {
            breakLine = true;
            break;
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
    GraphicsOptions.OptionSet optionSet = GraphicsOptions.functionPlotExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
