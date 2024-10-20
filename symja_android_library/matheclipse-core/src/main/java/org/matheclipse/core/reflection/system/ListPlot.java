package org.matheclipse.core.reflection.system;

import java.util.function.Function;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.ECharts;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/** Plot a list of Points */
public class ListPlot extends AbstractFunctionOptionEvaluator {

  public ListPlot() {}

  public IExpr evaluateECharts(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }

    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    String graphicsPrimitivesStr = listPlotECharts(ast, options, graphicsOptions, engine);
    if (graphicsPrimitivesStr != null) {
      StringBuilder jsControl = new StringBuilder();
      jsControl.append("var eChart = echarts.init(document.getElementById('main'));\n");
      jsControl.append(graphicsPrimitivesStr);
      jsControl.append("\neChart.setOption(option);");

      return F.JSFormData(jsControl.toString(), "echarts");
    }
    return F.NIL;
  }

  protected static String listPlotECharts(IAST plot, IExpr[] options,
      GraphicsOptions graphicsOptions, EvalEngine engine) {
    if (plot.size() < 2) {
      return null;
    }
    ECharts.setGraphicOptions(graphicsOptions, plot, 2, options, engine);

    IExpr arg1 = plot.arg1();
    if (!arg1.isList()) {
      arg1 = engine.evaluate(arg1);
    }
    if (arg1.isAssociation()) {
      IAssociation assoc = ((IAssociation) arg1);
      arg1 = assoc.matrixOrList();
    }
    return listPlotECharts(arg1, graphicsOptions);
  }

  protected static String listPlotECharts(IExpr listData, GraphicsOptions graphicsOptions) {
    if (listData.isNonEmptyList()) {
      IAST pointList = (IAST) listData;
      double[] minMax = new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
          Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
      // IASTAppendable[] pointSets =
      // org.matheclipse.core.reflection.system.ListPlot.pointsOfListPlot(pointList, minMax);
      // if (pointSets != null) {
      if (pointList.isListOfLists()) {
        StringBuilder yAxisSeriesBuffer = new StringBuilder();
        // StringBuilder xAxisCategoryBuffer = new StringBuilder();
        if (pointList.isListOfPoints(2)) {
          ECharts.yAxisSingleSeries(yAxisSeriesBuffer, pointList, graphicsOptions, minMax);
        } else {
          for (int i = 1; i < pointList.size(); i++) {
            IExpr pointSet = pointList.get(i);
            if (pointSet != null && pointSet.isListOfLists()) {
              if (i > 1) {
                yAxisSeriesBuffer.append(",\n");
              }
              ECharts.yAxisSingleSeries(yAxisSeriesBuffer, (IAST) pointSet, graphicsOptions,
                  minMax);
            }
          }
        }
        ECharts echarts = ECharts.build(graphicsOptions, null, yAxisSeriesBuffer);
        echarts.setXAxisPlot();
        if (minMax[2] < -1000.0) {
          minMax[2] = -50;
        }
        if (minMax[3] > 1000.0) {
          minMax[3] = 50;
        }
        echarts.setYAxisPlot(minMax[2], minMax[3]);
        return echarts.getJSONStr();
      }

      // TODO Labeled lists
      if (pointList.isList()) {// x -> x.isList())) {
        if (pointList.isListOfPoints(2)) {
          return point2DListLinePlot(pointList, graphicsOptions);
        }
        if (pointList.isListOfLists()) {
          IAST listOfLists = pointList;
          StringBuilder yAxisSeriesBuffer = new StringBuilder();
          String type = graphicsOptions.isJoined() ? ECharts.TYPE_LINE : ECharts.TYPE_SCATTER;
          ECharts.seriesData(yAxisSeriesBuffer, listOfLists, graphicsOptions, type, "");
          StringBuilder xAxisCategoryBuffer = new StringBuilder();
          ECharts.xAxisCategory(xAxisCategoryBuffer, (IAST) listOfLists.arg1());
          ECharts echarts = ECharts.build(graphicsOptions, xAxisCategoryBuffer, yAxisSeriesBuffer);
          echarts.setXAxisPlot();
          echarts.setYAxisPlot(minMax[2], minMax[3]);
          return echarts.getJSONStr();
        }

      }
      return yValueListLinePlot(pointList, graphicsOptions);
    }
    return null;
  }

  /**
   * Plot a list of 2D points.
   * 
   * @param pointList2D list of 2D points
   * @return
   */
  private static String point2DListLinePlot(IAST pointList2D, GraphicsOptions graphicsOptions) {
    StringBuilder xAxisString = new StringBuilder();
    StringBuilder yAxisString = new StringBuilder();
    // yAxisString.append( //
    // "{\n" //
    // + " name: 'List 1',\n" //
    // + " type: 'line',\n");
    ECharts.xyAxesPoint2D(pointList2D, xAxisString, yAxisString, graphicsOptions);

    ECharts echarts = ECharts.build(graphicsOptions, xAxisString, yAxisString);
    echarts.setXAxis();
    echarts.setYAxis("value");
    return echarts.getJSONStr();
  }

  private static String yValueListLinePlot(IAST pointList, GraphicsOptions graphicsOptions) {
    double[] minMax = new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    // y-axis values
    StringBuilder yAxisString = new StringBuilder();
    ECharts.yAxisSingleSeries(yAxisString, pointList, graphicsOptions, minMax);

    // x-axis categories
    StringBuilder xAxisString = new StringBuilder();
    ECharts.xAxisCategory(xAxisString, pointList);
    ECharts echarts = ECharts.build(graphicsOptions, xAxisString, yAxisString);
    echarts.setXAxis();
    echarts.setYAxis("value");
    return echarts.getJSONStr();
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (ToggleFeature.JS_ECHARTS) {
      return evaluateECharts(ast, argSize, options, engine, originalAST);
    }
    if (options[ECharts.X_JSFORM].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    // boundingbox an array of double values (length 4) which describes the bounding box
    // <code>[xMin, xMax, yMin, yMax]</code>
    IAST graphicsPrimitives = listPlot(ast, options, graphicsOptions, engine);
    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      IAST listOfOptions = F.List(F.Rule(S.Axes, S.True), //
          graphicsOptions.plotRange());
      return createGraphicsFunction(graphicsPrimitives, listOfOptions, graphicsOptions);
    }

    return F.NIL;
  }

  protected IExpr createGraphicsFunction(IAST graphicsPrimitives, IAST listOfOptions,
      GraphicsOptions graphicsOptions) {
    OptionArgs options = graphicsOptions.options();
    options.appendOptionRules(listOfOptions);
    IASTAppendable result = F.Graphics(graphicsPrimitives);// , //
    result.appendArgs(options.getListOfRules());
    return result;
  }

  /**
   * 
   * @param plot a list of lists of lists of points
   * @param graphicsOptions
   * @param engine
   * @param colour
   * @return
   */
  protected static IAST plot(IAST plot, IExpr[] options, GraphicsOptions graphicsOptions,
      EvalEngine engine) {
    if (plot.size() < 2) {
      return F.NIL;
    }
    ECharts.setGraphicOptions(graphicsOptions, plot, 3, options, engine);
    // final OptionArgs optionArgs = new OptionArgs(plot.topHead(), plot, 3, engine, true);
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
    if (arg1.isListOfLists()) {
      IAST listOfLists = (IAST) arg1;
      final IASTAppendable graphicsPrimitives = F.ListAlloc();
      for (int j = 1; j < listOfLists.size(); j++) {
        IAST pointList = (IAST) listOfLists.get(j);
        IAST color = GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
        Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
        Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();
        for (int i = 1; i < pointList.size(); i++) {
          IAST singlePointList = (IAST) pointList.get(i);
          sequencePointListPlot(graphicsPrimitives, singlePointList, graphicsOptions, color,
              xFunction, yFunction, engine);
        }
      }
      return graphicsPrimitives;
    }

    return F.NIL;
  }

  /**
   * 
   * @param plot
   * @param options TODO
   * @param graphicsOptions TODO
   * @param engine
   * @param colour
   * @return
   */
  protected static IAST listPlot(IAST plot, IExpr[] options, GraphicsOptions graphicsOptions,
      EvalEngine engine) {
    if (plot.size() < 2) {
      return F.NIL;
    }
    final OptionArgs optionArgs = new OptionArgs(plot.topHead(), plot, 2, engine, true);
    if (options[ECharts.X_JOINED].isTrue()) {
      graphicsOptions.setJoined(true);
    }
    graphicsOptions.setOptions(optionArgs);
    graphicsOptions.setScalingFunctions(options);

    IExpr arg1 = plot.arg1();
    if (!arg1.isList()) {
      arg1 = engine.evaluate(arg1);
    }
    if (arg1.isAssociation()) {
      IAssociation assoc = ((IAssociation) arg1);
      arg1 = assoc.matrixOrList();
    }
    if (arg1.isNonEmptyList()) {
      final IASTAppendable graphicsPrimitives = F.ListAlloc();
      IAST pointList = (IAST) arg1;
      // TODO Labeled lists
      if (pointList.isList()) {// x -> x.isList())) {
        if (pointList.isListOfPoints(2)) {
          IAST color = GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
          Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
          Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();
          sequencePointListPlot(graphicsPrimitives, pointList, graphicsOptions, color, xFunction,
              yFunction, engine);
          return graphicsPrimitives;
        }
        if (pointList.isListOfLists()) {
          IAST listOfLists = pointList;
          for (int i = 1; i < listOfLists.size(); i++) {
            pointList = (IAST) listOfLists.get(i);
            // int[] dimension = pointList.isMatrix(false);
            if (pointList.isListOfPoints(2)) {
              IAST color =
                  GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
              Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
              Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();
              sequencePointListPlot(graphicsPrimitives, pointList, graphicsOptions, color,
                  xFunction, yFunction, engine);
              // } else {
              // return graphicsPrimitives;
              // }
            } else {
              sequenceYValuesListPlot(graphicsPrimitives, pointList, graphicsOptions, engine);
            }
          }
          return graphicsPrimitives;
        }

      }
      sequenceYValuesListPlot(graphicsPrimitives, pointList, graphicsOptions, engine);
      return graphicsPrimitives;
    }
    return F.NIL;
  }

  /**
   * Plot a list of 2D points.
   * 
   * @param arg the number of the current argument
   * @param pointList
   * @param graphicsOptions TODO
   * @param engine
   * 
   * @return
   */
  private static void sequencePointListPlot(IASTAppendable graphicsPrimitives, IAST pointList,
      GraphicsOptions graphicsOptions, IAST color, Function<IExpr, IExpr> xFunction,
      Function<IExpr, IExpr> yFunction, EvalEngine engine) {
    // plot a list of 2D points
    double[] boundingbox = graphicsOptions.boundingBox();
    // if (linePlot) {
    if (pointList.size() > 2) {
      IAST lastPoint = F.NIL;
      IAST lastArg = F.NIL;
      boolean isConnected = false;
      int start = Integer.MAX_VALUE;
      for (int i = 1; i < pointList.size(); i++) {
        IExpr arg = pointList.get(i);
        IAST point = getPoint2D(arg);
        if (!point.isPresent() || isNonReal(point.arg1(), point.arg2())) {
          continue;
        }
        lastArg = (IAST) arg;
        lastPoint = point;
        start = i + 1;
        break;
      }

      if (start < Integer.MAX_VALUE && lastPoint.isPresent()) {
        xBoundingBox(boundingbox, xFunction.apply(lastPoint.arg1()), engine);
        yBoundingBox(boundingbox, yFunction.apply(lastPoint.arg2()), engine);
        graphicsPrimitives.append(color);

        IASTAppendable pointPrimitives = F.ListAlloc();
        IASTAppendable graphicsExtraPrimitives = F.ListAlloc();
        for (int i = start; i < pointList.size(); i++) {
          IExpr arg = pointList.get(i);
          IAST point = getPoint2D(arg);
          if (!point.isPresent() || isNonReal(point)) {
            if (pointPrimitives.argSize() > 0) {
              graphicsPrimitives.append(F.Line(pointPrimitives));
              pointPrimitives = F.ListAlloc();
            }
            isConnected = false;
            lastPoint = F.NIL;
            continue;
          }
          IExpr xValue = xFunction.apply(point.arg1());
          IExpr yValue = yFunction.apply(point.arg2());
          if (!isConnected && lastPoint.isPresent()) {
            IExpr xLast = xFunction.apply(lastPoint.arg1());
            IExpr yLast = yFunction.apply(lastPoint.arg2());
            if (xBoundingBox(boundingbox, xLast, engine)
                && yBoundingBox(boundingbox, yLast, engine)) {
              addSinglePoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xLast,
                  yLast, lastArg);
            }

            if (xBoundingBox(boundingbox, xValue, engine)
                && yBoundingBox(boundingbox, yValue, engine)) {
              addSinglePoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xValue,
                  yValue, (IAST) arg);
              isConnected = true;
              continue;
            }
          }
          if (isConnected) {
            if (xBoundingBox(boundingbox, xValue, engine)
                && yBoundingBox(boundingbox, yValue, engine)) {
              addSinglePoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xValue,
                  yValue, (IAST) arg);
            }
          }
          lastPoint = point;
        }
        if (!isConnected && lastPoint.isPresent()) {
          // dummy line for a single point
          IExpr xLast = xFunction.apply(lastPoint.arg1());
          IExpr yLast = yFunction.apply(lastPoint.arg2());
          if (xBoundingBox(boundingbox, xLast, engine)
              && yBoundingBox(boundingbox, yLast, engine)) {
            addSinglePoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xLast,
                yLast, lastArg);
            addSinglePoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xLast,
                yLast, lastArg);
          }
        }

        if (pointPrimitives.argSize() > 0) {
          graphicsPrimitives.append(graphicsOptions.addPoints(pointPrimitives));
        }
        if (graphicsExtraPrimitives.argSize() > 0) {
          graphicsPrimitives.append(graphicsExtraPrimitives);
        }
      }
    }
    // } else {
    // graphicsPrimitives.append(color);
    // IASTAppendable pointPrimitives = F.ListAlloc();
    // IASTAppendable textPrimitives = F.ListAlloc();
    // for (int i = 1; i < pointList.size(); i++) {
    // IAST point = (IAST) pointList.get(i);
    // if (isNonReal(point.arg1(), point.arg2())) {
    // continue;
    // }
    // IExpr xValue = xFunction.apply(point.arg1());
    // IExpr yValue = yFunction.apply(point.arg2());
    // xBoundingBox(boundingbox, xValue, engine);
    // yBoundingBox(boundingbox, yValue, engine);
    // addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, xValue, yValue);
    // }
    // if (pointPrimitives.argSize() > 0) {
    // graphicsPrimitives.append(F.Point(pointPrimitives));
    // }
    // if (textPrimitives.argSize() > 0) {
    // graphicsPrimitives.append(textPrimitives);
    // }
    // }
  }

  protected static IAST getPoint2D(IExpr arg) {
    if (arg.isList2()) {
      return (IAST) arg;
    }
    if (arg.isASTSizeGE(S.Style, 1) //
        || arg.isASTSizeGE(S.Labeled, 1)) {
      if (arg.first().isList2()) {
        return (IAST) arg.first();
      }
    }
    return F.NIL;
  }

  protected static IExpr getPointY(IExpr arg) {
    if (arg.isASTSizeGE(S.Style, 1) //
        || arg.isASTSizeGE(S.Labeled, 1)) {
      return arg.first();
    }
    return arg;
  }

  /**
   * Plot a list of points for Y-values for the X-values <code>1,2,3,...</code>.
   * 
   * @param graphicsPrimitives the genertal list of points
   * @param pointList
   * @param graphicsOptions
   * @param engine
   * @param graphicsExtraPrimitives special points which have extra style
   * @return
   */
  private static void sequenceYValuesListPlot(IASTAppendable graphicsPrimitives, IAST pointList,
      GraphicsOptions graphicsOptions, EvalEngine engine) {
    double[] boundingbox = graphicsOptions.boundingBox();
    IAST color = GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
    Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
    Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();
    xBoundingBox(boundingbox, xFunction.apply(F.C1), engine);
    xBoundingBox(boundingbox, xFunction.apply(F.ZZ(pointList.size())), engine);

    graphicsPrimitives.append(color);
    IExpr lastPoint = F.NIL;
    IExpr lastArg = F.NIL;
    int lastPosition = -1;
    boolean isConnected = false;
    int start = Integer.MAX_VALUE;
    for (int i = 1; i < pointList.size(); i++) {
      IExpr arg = pointList.get(i);
      IExpr currentPointY = getPointY(arg);
      if (isNonReal(currentPointY)) {
        continue;
      }
      lastArg = arg;
      lastPoint = currentPointY;
      lastPosition = i;
      start = i + 1;
      break;
    }
    if (start < Integer.MAX_VALUE) {
      yBoundingBox(boundingbox, yFunction.apply(lastPoint), engine);
      IASTAppendable pointPrimitives = F.ListAlloc();
      IASTAppendable graphicsExtraPrimitives = F.ListAlloc();
      for (int i = start; i < pointList.size(); i++) {
        IExpr arg = pointList.get(i);
        IExpr currentPointY = getPointY(arg);
        if (isNonReal(currentPointY)) {
          if (pointPrimitives.argSize() > 0) {
            graphicsPrimitives.append(graphicsOptions.addPoints(pointPrimitives));
            pointPrimitives = F.ListAlloc();
          }
          lastArg = F.NIL;
          lastPoint = F.NIL;
          isConnected = false;
          continue;
        }

        if (!isConnected && lastPoint.isPresent()) {
          addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
              xFunction.apply(F.num(lastPosition)), yFunction.apply(lastPoint), lastArg);
          addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
              xFunction.apply(F.num(i)), yFunction.apply(currentPointY), arg);
          isConnected = true;
          continue;
        }
        if (isConnected) {
          addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
              xFunction.apply(F.num(i)), yFunction.apply(currentPointY), arg);
        }
        lastArg = arg;
        lastPoint = currentPointY;
        lastPosition = i;
      }
      if (!isConnected && lastPoint.isPresent()) {
        // dummy line for a single point
        addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
            xFunction.apply(F.num(lastPosition)), yFunction.apply(lastPoint), lastArg);
        addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
            xFunction.apply(F.num(pointList.size() - 1)), yFunction.apply(lastPoint), lastArg);
      }

      if (pointPrimitives.argSize() > 0) {
        graphicsPrimitives.append(graphicsOptions.addPoints(pointPrimitives));
      }
      if (graphicsExtraPrimitives.argSize() > 0) {
        graphicsPrimitives.append(graphicsExtraPrimitives);
      }
    }
    // } else {
    // graphicsPrimitives.append(color);
    // if (pointList.isAST(S.Labeled, 3) && pointList.arg1().isList()) {
    // pointList = (IAST) pointList.arg1();
    // xBoundingBox(boundingbox, xFunction.apply(F.ZZ(pointList.size())), engine);
    // }
    // IASTAppendable pointPrimitives = F.ListAlloc();
    // IASTAppendable textPrimitives = F.ListAlloc();
    // for (int i = 1; i < pointList.size(); i++) {
    // IExpr currentPointY = pointList.get(i);
    // if (isNonReal(currentPointY)) {
    // continue;
    // }
    // addIndexedYPoint(pointPrimitives, textPrimitives, boundingbox, engine,
    // xFunction.apply(F.num(i)), yFunction.apply(currentPointY));
    // }
    // if (pointPrimitives.argSize() > 0) {
    // graphicsPrimitives.append(F.Point(pointPrimitives));
    // }
    // if (textPrimitives.argSize() > 0) {
    // graphicsPrimitives.append(textPrimitives);
    // }
    // }
  }

  private static boolean addSinglePoint(IASTAppendable pointPrimitives,
      IASTAppendable graphicsExtraPrimitives, double[] boundingbox, EvalEngine engine,
      IExpr xScaled, IExpr yScaled, IAST arg) {
    IReal x = xScaled.evalReal();
    IReal y = yScaled.evalReal();
    if (x != null && y != null) {
      if (xBoundingBox(boundingbox, x, engine) && yBoundingBox(boundingbox, y, engine)) {
        pointPrimitives.append(F.List(x, y));
        if (arg.isAST(S.Labeled, 3)) {
          graphicsExtraPrimitives.append(GraphicsFunctions.textAtPoint(arg, x, y));
        } else if (arg.isASTSizeGE(S.Style, 2)) {
          IASTMutable styledPoint = arg.setAtCopy(1, F.Point(F.List(x, y)));
          graphicsExtraPrimitives.append(styledPoint);
        }
        return true;
      }
    }
    return false;
  }

  protected static boolean addIndexedYPoint(IASTAppendable pointPrimitives,
      IASTAppendable textPrimitives, double[] boundingbox, EvalEngine engine, IExpr xScaled,
      IExpr yScaled, IExpr currentYPrimitive) {
    IReal y = yScaled.evalReal();
    if (y != null && yBoundingBox(boundingbox, yScaled, engine)) {
      if (currentYPrimitive.isAST(S.Labeled, 3)) {
        textPrimitives.append(GraphicsFunctions.textAtPoint(currentYPrimitive, xScaled, y));
      } else if (currentYPrimitive.isASTSizeGE(S.Style, 2)) {
        IASTMutable styledPoint =
            ((IAST) currentYPrimitive).setAtCopy(1, F.Point(F.List(xScaled, y)));
        textPrimitives.append(styledPoint);
      }
      pointPrimitives.append(F.List(xScaled, yScaled));
      return true;
    }
    return false;
  }


  protected static boolean isNonReal(IExpr point) {
    return point.isComplex() || point.isComplexNumeric() || point.isDirectedInfinity()
        || point == S.Indeterminate || point == S.None || point.isAST(S.Missing);
  }

  protected static boolean isNonReal(IExpr lastPointX, IExpr lastPointY) {
    return isNonReal(lastPointX) || isNonReal(lastPointY);
  }

  protected static boolean xBoundingBox(double[] boundingbox, IExpr xExpr, EvalEngine engine) {
    try {
      double xValue = engine.evalDouble(xExpr);
      if (Double.isFinite(xValue)) {
        if (xValue < boundingbox[0]) {
          boundingbox[0] = xValue;
        }
        if (xValue > boundingbox[1]) {
          boundingbox[1] = xValue;
        }
      }
      return true;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return false;
  }

  protected static boolean yBoundingBox(double[] boundingbox, IExpr yExpr, EvalEngine engine) {
    try {
      double yValue = engine.evalDouble(yExpr);
      if (Double.isFinite(yValue)) {
        if (yValue < boundingbox[2]) {
          boundingbox[2] = yValue;
        }
        if (yValue > boundingbox[3]) {
          boundingbox[3] = yValue;
        }
      }
      return true;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return false;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  public static IASTAppendable[] pointsOfListPlot(final IAST ast, double[] minMax) {
    IExpr arg1 = ast.arg1();
    if (arg1.isVector() > 0) {
      double[] rowPoints = arg1.toDoubleVectorIgnore();
      if (rowPoints != null && rowPoints.length > 0) {
        IASTAppendable points = createPointsArray(rowPoints, minMax);
        return new IASTAppendable[] {points};
      }
    } else {
      if (arg1.isList()) {
        // IAST list = getPoint2D(arg1);
        return pointsOfMatrix((IAST) arg1, minMax);
      }
    }
    return null;
  }

  public static IASTAppendable[] pointsOfMatrix(IAST tensor, double[] minMax) {
    IntArrayList dimensions = LinearAlgebra.dimensions(tensor);
    if (dimensions.size() == 3 && dimensions.getInt(2) == 2) {
      IASTAppendable[] result = new IASTAppendable[tensor.argSize()];
      for (int i = 1; i < tensor.size(); i++) {
        result[i - 1] = listPlotMatrix(tensor.get(i), minMax);
      }
      return result;
    }
    if (dimensions.size() == 2 && dimensions.getInt(1) == 2) {
      // matrix n X 2
      IASTAppendable points = listPlotMatrix(tensor, minMax);
      return new IASTAppendable[] {points};
    }
    if (tensor.isListOfLists()) {
      IASTAppendable[] result = new IASTAppendable[tensor.argSize()];
      for (int i = 1; i < tensor.size(); i++) {
        double[] rowPoints = tensor.get(i).toDoubleVectorIgnore();
        if (rowPoints != null && rowPoints.length > 0) {
          IASTAppendable points = createPointsArray(rowPoints, minMax);
          result[i - 1] = points;
        } else {
          result[i - 1] = F.NIL;
        }
      }
      return result;
    }
    return null;
  }

  private static IASTAppendable createPointsArray(double[] allPoints, double[] minMax) {
    IASTAppendable points = F.NIL;
    if (0.0 < minMax[0]) {
      minMax[0] = 0.0;
    }
    if (allPoints.length > minMax[1]) {
      minMax[1] = allPoints.length;
    }
    if (0.0 < minMax[2]) {
      minMax[2] = 0.0;
    }

    points = F.ast(S.List, allPoints.length);

    for (int i = 0; i < allPoints.length; i++) {
      if (allPoints[i] > minMax[3]) {
        minMax[3] = allPoints[i];
      } else if (allPoints[i] < minMax[2]) {
        minMax[2] = allPoints[i];
      }
      points.append(F.list(F.num(i + 1), F.num(allPoints[i])));
    }
    return points;
  }

  private static IASTAppendable listPlotMatrix(IExpr arg1, double[] minMax) {
    double[][] allPoints = arg1.toDoubleMatrix();
    return listPlotMatrix(allPoints, minMax);
  }

  private static IASTAppendable listPlotMatrix(double[][] allPoints, double[] minMax) {
    IASTAppendable points = F.NIL;
    if (allPoints != null && allPoints.length > 0) {
      points = F.ListAlloc(allPoints.length);

      for (int i = 0; i < allPoints.length; i++) {
        for (int j = 0; j < allPoints[i].length; j++) {
          if (allPoints[i][j] > minMax[1]) {
            minMax[1] = allPoints[i][0];
          } else if (allPoints[i][j] < minMax[0]) {
            minMax[0] = allPoints[i][0];
          }
          if (allPoints[i][j] > minMax[3]) {
            minMax[3] = allPoints[i][1];
          } else if (allPoints[i][j] < minMax[2]) {
            minMax[2] = allPoints[i][1];
          }
        }
        points.append(F.list(F.num(allPoints[i][0]), F.num(allPoints[i][1])));
      }
    }
    return points;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(false));
  }
}
