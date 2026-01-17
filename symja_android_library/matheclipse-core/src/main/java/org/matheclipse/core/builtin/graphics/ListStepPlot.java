package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.ECharts;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating ListStepPlots.
 */
public class ListStepPlot extends ListPlot {

  private enum StepType {
    RIGHT, LEFT, CENTER
  }

  public ListStepPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg = engine.evaluate(ast.arg1());

    // Step Specification
    StepType stepType = StepType.RIGHT;
    if (argSize >= 2) {
      IExpr arg2 = ast.arg2();
      if (!arg2.isRuleAST()) {
        String s = arg2.toString();
        if (s.equalsIgnoreCase("Left") || s.equalsIgnoreCase("\"Left\"")) {
          stepType = StepType.LEFT;
        } else if (s.equalsIgnoreCase("Center") || s.equalsIgnoreCase("\"Center\"")) {
          stepType = StepType.CENTER;
        } else if (s.equalsIgnoreCase("Right") || s.equalsIgnoreCase("\"Right\"")) {
          stepType = StepType.RIGHT;
        }
      }
    }

    // Basic list check
    if (!dataArg.isList() && !dataArg.isAssociation()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(GraphicsOptions.listPlotDefaultOptionKeys(), options, engine);

    boolean joined = graphicsOptions.isJoined();

    IAST dataList = (IAST) dataArg;
    if (dataList.isAssociation()) {
      dataList = ((IAssociation) dataList).matrixOrList();
    }

    IASTAppendable primitives = F.ListAlloc();

    // Determine if Single or Multiple Datasets
    boolean isMultiDataset = false;
    if (dataList.isListOfPoints(2)) {
      isMultiDataset = false;
    } else if (dataList.isListOfLists() && dataList.size() > 1 && !dataList.arg1().isNumber()) {
      isMultiDataset = true;
    } else {
      // Check if it's a simple list with Missing/None values (Single Dataset)
      // If it contains things other than Lists, treat as single.
      isMultiDataset = false;
    }

    boolean generated = false;
    if (isMultiDataset) {
      for (int i = 1; i < dataList.size(); i++) {
        IExpr subData = dataList.get(i);
        if (subData.isList()) {
          IExpr style = GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
          IExpr userStyle =
              GraphicsOptions.getPlotStyle(options[GraphicsOptions.X_PLOTSTYLE], i - 1);
          if (userStyle.isPresent())
            style = F.Directive(style, userStyle);

          generated = generateStepPrimitives(primitives, (IAST) subData, stepType,
              joined, style, graphicsOptions, engine);
        }
      }
    } else {
      IExpr style = GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
      IExpr userStyle = GraphicsOptions.getPlotStyle(options[GraphicsOptions.X_PLOTSTYLE], 0);
      if (userStyle.isPresent())
        style = F.Directive(style, userStyle);

      generated = generateStepPrimitives(primitives, dataList, stepType, joined, style,
          graphicsOptions, engine);
    }

    if (!generated) {
      return F.NIL;
    }
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  private boolean generateStepPrimitives(IASTAppendable primitives, IAST data, StepType type,
      boolean joined, IExpr style, GraphicsOptions opts, EvalEngine engine) {

    Function<IExpr, IExpr> fx = opts.xFunction();
    Function<IExpr, IExpr> fy = opts.yFunction();
    double[] bbox = opts.boundingBox();

    // Points: {x, y}. y can be NaN.
    List<double[]> points = new ArrayList<>();

    // Check for explicit coordinates vs implicit
    // We must handle Missing/None correctly.
    boolean hasCoordinates = false;
    // Heuristic: Check first non-missing element
    for (IExpr e : data) {
      if (e != S.Missing && !e.isNone()) {
        if (e.isList2())
          hasCoordinates = true;
        break;
      }
    }

    if (hasCoordinates) {
      for (IExpr e : data) {
        if (e.isList2()) {
          try {
            double x = ((IAST) e).arg1().evalDouble();
            double y = ((IAST) e).arg2().evalDouble();
            points.add(new double[] {x, y});
          } catch (RuntimeException rex) {
            Errors.printMessage(S.ListStepPlot, rex);
            return false;
          }
        }
      }
    } else {
      // Implicit X (Index)
      for (int i = 1; i < data.size(); i++) {
        IExpr e = data.get(i);
        double val = Double.NaN;
        if (e != S.Missing && !e.isNone()) {
          try {
            if (e instanceof INumber)
              val = ((INumber) e).reDoubleValue();
            else
              val = e.evalf();
          } catch (Exception ex) {
          }
        }
        points.add(new double[] {i, val});
      }
    }

    if (points.isEmpty()) {
      return true;
    }
    // Update Bbox (only valid points)
    for (double[] p : points) {
      if (!Double.isNaN(p[1])) {
        xBoundingBox(bbox, F.num(p[0]), engine);
        yBoundingBox(bbox, F.num(p[1]), engine);
      }
    }

    int n = points.size();

    IASTAppendable group = F.ListAlloc();
    if (style != null)
      group.append(style);

    IASTAppendable currentLine = F.ListAlloc();

    for (int i = 0; i < n; i++) {
      double[] pCurrent = points.get(i);
      double x = pCurrent[0];
      double y = pCurrent[1];

      // Determine xNext (end of this step)
      double xNext;
      if (i < n - 1) {
        xNext = points.get(i + 1)[0];
      } else {
        // Last point: Extrapolate spacing
        double dx = 1.0;
        if (n > 1)
          dx = x - points.get(i - 1)[0];
        xNext = x + dx;
      }

      double[] pNextVal = (i < n - 1) ? points.get(i + 1) : null;
      double yNextVal = (pNextVal != null) ? pNextVal[1] : Double.NaN;

      if (Double.isNaN(y)) {
        // Gap at current index. Finish current line if exists.
        if (currentLine.size() > 1) {
          group.append(F.Line(currentLine));
          currentLine = F.ListAlloc();
        }
        continue;
      }

      // Start point of the segment
      if (currentLine.size() == 1) {
        if (type == StepType.RIGHT) {
          addScaledPoint(currentLine, x, y, fx, fy);
        } else if (type == StepType.LEFT) {
          if (!Double.isNaN(yNextVal)) {
            addScaledPoint(currentLine, x, yNextVal, fx, fy);
          }
        } else if (type == StepType.CENTER) {
          addScaledPoint(currentLine, x, y, fx, fy);
        }
      }

      // Draw step
      if (type == StepType.RIGHT) {
        // Horizontal: (x,y) -> (xNext, y)
        addScaledPoint(currentLine, xNext, y, fx, fy);

        // Vertical Riser to next level?
        if (joined && !Double.isNaN(yNextVal)) {
          addScaledPoint(currentLine, xNext, yNextVal, fx, fy);
        } else {
          // Break
          if (currentLine.size() > 1) {
            group.append(F.Line(currentLine));
            currentLine = F.ListAlloc();
          }
        }

      } else if (type == StepType.LEFT) {
        if (Double.isNaN(yNextVal)) {
          if (currentLine.size() > 1) {
            group.append(F.Line(currentLine));
            currentLine = F.ListAlloc();
          }
        } else {
          addScaledPoint(currentLine, xNext, yNextVal, fx, fy);
          if (joined && i < n - 2) {
            double yNextNext = points.get(i + 2)[1];
            if (!Double.isNaN(yNextNext)) {
              addScaledPoint(currentLine, xNext, yNextNext, fx, fy);
            } else {
              if (currentLine.size() > 1) {
                group.append(F.Line(currentLine));
                currentLine = F.ListAlloc();
              }
            }
          } else {
            if (currentLine.size() > 1) {
              group.append(F.Line(currentLine));
              currentLine = F.ListAlloc();
            }
          }
        }

      } else if (type == StepType.CENTER) {
        double mid = (x + xNext) / 2.0;
        // 1. x to mid at height y
        addScaledPoint(currentLine, mid, y, fx, fy);

        // Riser
        if (joined && !Double.isNaN(yNextVal)) {
          // 2. Vertical mid, y -> mid, yNext
          addScaledPoint(currentLine, mid, yNextVal, fx, fy);
          // 3. Horizontal mid -> xNext at yNext
          addScaledPoint(currentLine, xNext, yNextVal, fx, fy);
        } else {
          if (currentLine.size() > 1) {
            group.append(F.Line(currentLine));
            currentLine = F.ListAlloc();
          }
        }
      }
    }

    // Flush any remaining line
    if (currentLine.size() > 1) {
      group.append(F.Line(currentLine));
    }

    // Handle "Left" Pre-Step (Initial interval ending at first point)
    if (type == StepType.LEFT && !points.isEmpty()) {
      double[] p0 = points.get(0);
      if (!Double.isNaN(p0[1])) {
        double dx = 1.0;
        if (points.size() > 1)
          dx = points.get(1)[0] - p0[0];
        double xStart = p0[0] - dx;

        IASTAppendable preLine = F.ListAlloc();
        addScaledPoint(preLine, xStart, p0[1], fx, fy);
        addScaledPoint(preLine, p0[0], p0[1], fx, fy);
        if (joined && points.size() > 1 && !Double.isNaN(points.get(1)[1])) {
          addScaledPoint(preLine, p0[0], points.get(1)[1], fx, fy);
        }
        group.append(1, F.Line(preLine));
      }
    }

    primitives.append(group);
    return true;
  }

  private void addScaledPoint(IASTAppendable list, double x, double y, Function<IExpr, IExpr> fx,
      Function<IExpr, IExpr> fy) {
    list.append(F.List(fx.apply(F.num(x)), fy.apply(F.num(y))));
  }

  protected static IExpr listStepPlotEChart(IAST ast, IExpr[] options, EvalEngine engine) {
    StringBuilder jsControl = new StringBuilder();
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    String graphicsPrimitivesStr = listStepPlot(ast, options, graphicsOptions, engine);
    if (graphicsPrimitivesStr != null) {
      jsControl.append("var eChart = echarts.init(document.getElementById('main'));\n");
      jsControl.append(graphicsPrimitivesStr);
      jsControl.append("\neChart.setOption(option);");
      // jsControl.append("var eChart = echarts.init(document.getElementById(\"main\"));\n");
      // jsControl.append("\neChart.setOption(");
      // jsControl.append(graphicsPrimitivesStr);
      // jsControl.append(");");

      return F.JSFormData(jsControl.toString(), "echarts");
    }
    return F.NIL;
  }

  protected static String listStepPlot(IAST plot, IExpr[] options, GraphicsOptions graphicsOptions,
      EvalEngine engine) {
    if (plot.size() < 2) {
      return null;
    }
    // graphicsOptions.setGraphicOptions(options, engine);
    // final OptionArgs optionArgs = new OptionArgs(plot.topHead(), plot, 2, engine, true);
    // if (options[ECharts.X_JOINED].isTrue()) {
    // graphicsOptions.setJoined(true);
    // }
    // graphicsOptions.setOptions(optionArgs);
    // graphicsOptions.setScalingFunctions(options);

    IExpr arg1 = plot.arg1();
    if (!arg1.isList()) {
      arg1 = engine.evaluate(arg1);
    }
    if (arg1.isAssociation()) {
      IAssociation assoc = ((IAssociation) arg1);
      arg1 = assoc.matrixOrList();
    }
    if (arg1.isNonEmptyList()) {
      IAST pointList = (IAST) arg1;
      // TODO Labeled lists
      if (pointList.isList()) {// x -> x.isList())) {
        if (pointList.isListOfPoints(2)) {
          return point2DListStepPlot(pointList, graphicsOptions);
        }
        if (pointList.isListOfLists()) {
          IAST listOfLists = pointList;
          StringBuilder yAxisSeriesBuffer = new StringBuilder();
          String type = graphicsOptions.isJoined() ? ECharts.TYPE_LINE : ECharts.TYPE_SCATTER;
          ECharts.seriesData(yAxisSeriesBuffer, listOfLists, graphicsOptions, type, "step");
          StringBuilder xAxisCategoryBuffer = new StringBuilder();
          ECharts.xAxisCategory(xAxisCategoryBuffer, (IAST) listOfLists.arg1());
          ECharts echarts = ECharts.build(graphicsOptions, xAxisCategoryBuffer, yAxisSeriesBuffer);
          echarts.setXAxis();
          echarts.setYAxis("value");
          return echarts.getJSONStr();
        }

      }
      return yValueListStepPlot(pointList, graphicsOptions);
    }
    return null;
  }

  /**
   * Plot a list of 2D points.
   * 
   * @param pointList2D list of 2D points
   * @return
   */
  private static String point2DListStepPlot(IAST pointList2D, GraphicsOptions graphicsOptions) {
    StringBuilder xAxisString = new StringBuilder();
    StringBuilder yAxisString = new StringBuilder();
    // yAxisString.append( //
    // "{\n" //
    // + " name: 'ListStepPlot',\n" //
    // + " type: 'line',\n" //
    // + " step: '1',"); // step must contain a string with length greater than 0
    String type = graphicsOptions.isJoined() ? ECharts.TYPE_LINE : ECharts.TYPE_SCATTER;
    ECharts.xyAxesPoint2D(pointList2D, xAxisString, yAxisString, graphicsOptions, type, "1");

    ECharts echarts = ECharts.build(graphicsOptions, xAxisString, yAxisString);
    echarts.setXAxis();
    echarts.setYAxis("value");
    return echarts.getJSONStr();
  }

  private static String yValueListStepPlot(IAST pointList, GraphicsOptions graphicsOptions) {
    double[] minMax = new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    // y-axis values
    StringBuilder yAxisString = new StringBuilder();
    // yAxisString.append( //
    // "{\n" //
    // + " name: 'ListStepPlot',\n" //
    // + " type: 'line',\n" //
    // + " step: '1',\n"); // step must contain a string with length greater than 0
    String type = graphicsOptions.isJoined() ? ECharts.TYPE_LINE : ECharts.TYPE_SCATTER;
    ECharts.yAxisSingleSeries(yAxisString, pointList, graphicsOptions, type, "1", minMax);

    // x-axis categories
    StringBuilder xAxisString = new StringBuilder();
    ECharts.xAxisCategory(xAxisString, pointList);
    ECharts echarts = ECharts.build(graphicsOptions, xAxisString, yAxisString);
    echarts.setXAxis();
    echarts.setYAxis("value");
    return echarts.getJSONStr();
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // Defaults: Joined -> True
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(false, true));
  }
}
