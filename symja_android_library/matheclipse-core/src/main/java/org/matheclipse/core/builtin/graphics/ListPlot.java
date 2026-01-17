package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
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
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/** Plot a list of Points */
public class ListPlot extends AbstractFunctionOptionEvaluator {

  protected static class CurveData {
    IAST points;
    IExpr color;
    boolean isPoint;

    CurveData(IAST points, IExpr color, boolean isPoint) {
      this.points = points;
      this.color = color;
      this.isPoint = isPoint;
    }
  }

  public ListPlot() {}

  public IExpr evaluateECharts(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
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
      if (pointList.isListOfLists()) {
        StringBuilder yAxisSeriesBuffer = new StringBuilder();
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

      if (pointList.isList()) {
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

  private static String point2DListLinePlot(IAST pointList2D, GraphicsOptions graphicsOptions) {
    StringBuilder xAxisString = new StringBuilder();
    StringBuilder yAxisString = new StringBuilder();
    ECharts.xyAxesPoint2D(pointList2D, xAxisString, yAxisString, graphicsOptions);

    ECharts echarts = ECharts.build(graphicsOptions, xAxisString, yAxisString);
    echarts.setXAxis();
    echarts.setYAxis("value");
    return echarts.getJSONStr();
  }

  private static String yValueListLinePlot(IAST pointList, GraphicsOptions graphicsOptions) {
    double[] minMax = new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    StringBuilder yAxisString = new StringBuilder();
    ECharts.yAxisSingleSeries(yAxisString, pointList, graphicsOptions, minMax);

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
    IExpr arg1 = ast.arg1();
    if (!checkList(engine, arg1)) {
      return Errors.printMessage(ast.topHead(), "lpn", F.List(arg1), engine);
    }
    if (ToggleFeature.JS_ECHARTS) {
      return evaluateECharts(ast, argSize, options, engine, originalAST);
    }
    if (options[GraphicsOptions.X_JSFORM].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }
    GraphicsOptions graphicsOptions =
        setGraphicsOptions(options, GraphicsOptions.listPlotDefaultOptionKeys(), engine);

    // Pre-process Labeled curves
    if (arg1.isList()) {
      IAST list = (IAST) arg1;
      IASTAppendable newData = F.ListAlloc(list.size());
      IASTAppendable legends = F.ListAlloc(list.size());
      boolean hasCurveLabels = false;

      for (IExpr elem : list) {
        if (elem.isAST(S.Labeled, 3)) {
          hasCurveLabels = appendLabeled(elem, newData, legends, hasCurveLabels, engine);
        } else {
          newData.append(elem);
          legends.append(F.CEmptyString);
        }
      }
      if (hasCurveLabels) {
        arg1 = newData;
        ast = ast.setAtCopy(1, arg1);
        if (options[GraphicsOptions.X_PLOTLEGENDS].equals(S.None)) {
          graphicsOptions.setPlotLegends(legends);
        }
      }
    }

    IAST graphicsPrimitives = listPlot(ast, options, graphicsOptions, engine);
    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);
    }

    return F.NIL;
  }

  private static boolean appendLabeled(IExpr elem, IASTAppendable newData, IASTAppendable legends,
      boolean hasCurveLabels, final EvalEngine engine) {
    IExpr content = elem.first();
    IExpr label = elem.second();
    IExpr evalContent = engine.evaluate(content);
    boolean isCurve = false;
    if (evalContent.isList()) {
      if (evalContent.isListOfLists()) {
        isCurve = true;
      } else if (evalContent.argSize() != 2) {
        isCurve = true;
      } else {
        // Argument size 2 is typically a point {x,y}, not a curve.
        isCurve = false;
      }
    }
    if (isCurve) {
      hasCurveLabels = true;
      newData.append(evalContent);
      legends.append(label);
    } else {
      newData.append(elem);
      legends.append(F.CEmptyString);
    }
    return hasCurveLabels;
  }

  protected static boolean checkList(final EvalEngine engine, IExpr arg1) {
    if (arg1.isListOfLists()) {
      IAST list = (IAST) arg1;
      for (int i = 1; i < list.size(); i++) {
        IExpr temp = list.get(i);
        if (temp.isList()) {
          if (temp.isEmpty()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  protected IExpr createGraphicsFunction(IAST graphicsPrimitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    IAST expressionsRule = checkForExpressionsLegend(plotAST.arg1(), graphicsOptions);
    if (expressionsRule.isPresent()) {
      graphicsOptions.addOption(expressionsRule);
    }

    if (graphicsOptions.filling() != S.None) {
      try {
        IAST newPrimitives = processFilling(graphicsPrimitives, graphicsOptions.filling(),
            graphicsOptions.fillingStyle(), 0.0);
        graphicsPrimitives = newPrimitives;
      } catch (RuntimeException rex) {
      }
    }

    IASTAppendable result = F.Graphics(graphicsPrimitives);
    result.appendArgs(graphicsOptions.getListOfRules());
    return result;
  }

  protected static IAST plot(IAST plot, IExpr[] options, GraphicsOptions graphicsOptions,
      EvalEngine engine) {
    if (plot.size() < 2) {
      return F.NIL;
    }

    IExpr arg1 = plot.arg1();
    if (!arg1.isList()) {
      arg1 = engine.evaluate(arg1);
    }
    if (arg1.isAssociation()) {
      IAssociation assoc = ((IAssociation) arg1);
      arg1 = assoc.matrixOrList();
    }

    IExpr plotStyle = options[GraphicsOptions.X_PLOTSTYLE];

    if (arg1.isListOfLists()) {
      IAST listOfLists = (IAST) arg1;
      final IASTAppendable graphicsPrimitives = F.ListAlloc();
      for (int j = 1; j < listOfLists.size(); j++) {
        IAST curveData = (IAST) listOfLists.get(j);

        IExpr defaultColor =
            GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
        IExpr style = defaultColor;
        if (plotStyle.isPresent() && plotStyle != S.None) {
          IExpr userStyle = GraphicsOptions.getPlotStyle(plotStyle, j - 1);
          style = F.Directive(defaultColor, userStyle);
        }

        Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
        Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();

        boolean isSegmented = false;
        if (curveData.size() > 1 && curveData.arg1().isList()) {
          IAST firstElem = (IAST) curveData.arg1();
          if (firstElem.size() > 1 && firstElem.arg1().isList()) {
            isSegmented = true;
          }
        }

        if (isSegmented) {
          for (int k = 1; k < curveData.size(); k++) {
            IAST segment = (IAST) curveData.get(k);
            sequencePointListPlot(graphicsPrimitives, segment, graphicsOptions, style, xFunction,
                yFunction, engine);
          }
        } else {
          if (curveData.isListOfPoints(2)) {
            sequencePointListPlot(graphicsPrimitives, curveData, graphicsOptions, style, xFunction,
                yFunction, engine);
          } else {
            sequenceYValuesListPlot(graphicsPrimitives, curveData, graphicsOptions, style, engine);
          }
        }
      }
      return graphicsPrimitives;
    }

    return F.NIL;
  }

  protected static IAST listPlot(IAST plot, IExpr[] options, GraphicsOptions graphicsOptions,
      EvalEngine engine) {
    if (plot.size() < 2) {
      return F.NIL;
    }
    if (options[GraphicsOptions.X_JOINED].isTrue()) {
      graphicsOptions.setJoined(true);
    }
    graphicsOptions.setScalingFunctions(options);

    IExpr arg1 = plot.arg1();
    if (!arg1.isList()) {
      arg1 = engine.evaluate(arg1);
    }
    if (arg1.isAssociation()) {
      IAssociation assoc = ((IAssociation) arg1);
      arg1 = assoc.matrixOrList();
    }

    IExpr plotStyle = options[GraphicsOptions.X_PLOTSTYLE];

    if (arg1.isNonEmptyList()) {
      final IASTAppendable graphicsPrimitives = F.ListAlloc();
      IAST pointList = (IAST) arg1;
      if (pointList.isList()) {
        if (pointList.isListOfPoints(2)) {
          IExpr defaultColor =
              GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
          IExpr style = defaultColor;
          if (plotStyle.isPresent() && plotStyle != S.None) {
            IExpr userStyle = GraphicsOptions.getPlotStyle(plotStyle, 0);
            style = F.Directive(defaultColor, userStyle);
          }

          Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
          Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();
          sequencePointListPlot(graphicsPrimitives, pointList, graphicsOptions, style, xFunction,
              yFunction, engine);
          return graphicsPrimitives;
        }
        if (pointList.isListOfLists()) {
          IAST listOfLists = pointList;
          for (int i = 1; i < listOfLists.size(); i++) {
            pointList = (IAST) listOfLists.get(i);

            IExpr defaultColor =
                GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
            IExpr style = defaultColor;
            if (plotStyle.isPresent() && plotStyle != S.None) {
              IExpr userStyle = GraphicsOptions.getPlotStyle(plotStyle, i - 1);
              style = F.Directive(defaultColor, userStyle);
            }

            if (pointList.isListOfPoints(2)) {
              Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
              Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();
              sequencePointListPlot(graphicsPrimitives, pointList, graphicsOptions, style,
                  xFunction, yFunction, engine);
            } else {
              sequenceYValuesListPlot(graphicsPrimitives, pointList, graphicsOptions, style,
                  engine);
            }
          }
          return graphicsPrimitives;
        }

      }
      IExpr defaultColor =
          GraphicsOptions.plotStyleColorExpr(graphicsOptions.incColorIndex(), F.NIL);
      IExpr style = defaultColor;
      if (plotStyle.isPresent() && plotStyle != S.None) {
        IExpr userStyle = GraphicsOptions.getPlotStyle(plotStyle, 0);
        style = F.Directive(defaultColor, userStyle);
      }

      sequenceYValuesListPlot(graphicsPrimitives, pointList, graphicsOptions, style, engine);
      return graphicsPrimitives;
    }
    return F.NIL;
  }

  private static void sequencePointListPlot(IASTAppendable graphicsPrimitives, IAST pointList,
      GraphicsOptions graphicsOptions, IExpr style, Function<IExpr, IExpr> xFunction,
      Function<IExpr, IExpr> yFunction, EvalEngine engine) {
    double[] boundingbox = graphicsOptions.boundingBox();
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

        if (style.isPresent()) {
          if (style.isList()) {
            graphicsPrimitives.appendArgs((IAST) style);
          } else {
            graphicsPrimitives.append(style);
          }
        }

        IASTAppendable pointPrimitives = F.ListAlloc();
        IASTAppendable graphicsExtraPrimitives = F.ListAlloc();
        for (int i = start; i < pointList.size(); i++) {
          IExpr arg = pointList.get(i);
          IAST point = getPoint2D(arg);
          if (!point.isPresent() || isNonReal(point)) {
            // Fix: If lastPoint was isolated, render it now before breaking
            if (!isConnected && lastPoint.isPresent()) {
              IExpr xLast = xFunction.apply(lastPoint.arg1());
              IExpr yLast = yFunction.apply(lastPoint.arg2());
              if (xBoundingBox(boundingbox, xLast, engine)
                  && yBoundingBox(boundingbox, yLast, engine)) {
                addSinglePoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xLast,
                    yLast, lastArg);
              }
            }

            if (pointPrimitives.argSize() > 0) {
              graphicsPrimitives.append(graphicsOptions.addPoints(pointPrimitives));
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
  }

  protected static IAST getPoint2D(IExpr arg) {
    if (arg.isList2()) {
      return (IAST) arg;
    }
    if (arg.isASTSizeGE(S.Style, 1) || arg.isASTSizeGE(S.Labeled, 1)) {
      if (arg.first().isList2()) {
        return (IAST) arg.first();
      }
    }
    return F.NIL;
  }

  protected static IExpr getPointY(IExpr arg) {
    if (arg.isASTSizeGE(S.Style, 1) || arg.isASTSizeGE(S.Labeled, 1)) {
      return arg.first();
    }
    return arg;
  }

  private static void sequenceYValuesListPlot(IASTAppendable graphicsPrimitives, IAST pointList,
      GraphicsOptions graphicsOptions, IExpr style, EvalEngine engine) {
    double[] boundingbox = graphicsOptions.boundingBox();
    Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
    Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();

    // Support DataRange -> {min, max}
    IExpr dataRange = graphicsOptions.dataRange();
    double drMin = 1.0;
    double drMax = pointList.argSize();
    boolean useDataRange = false;
    if (dataRange.isList2()) {
      try {
        drMin = ((IAST) dataRange).arg1().evalDouble();
        drMax = ((IAST) dataRange).arg2().evalDouble();
        useDataRange = true;
      } catch (Exception e) {
      }
    }

    double startX = useDataRange ? drMin : 1.0;
    double endX = useDataRange ? drMax : (double) pointList.argSize();

    xBoundingBox(boundingbox, xFunction.apply(F.num(startX)), engine);
    xBoundingBox(boundingbox, xFunction.apply(F.num(endX)), engine);

    if (style.isPresent()) {
      if (style.isList()) {
        graphicsPrimitives.appendArgs((IAST) style);
      } else {
        graphicsPrimitives.append(style);
      }
    }

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

      int count = pointList.argSize();

      for (int i = start; i < pointList.size(); i++) {
        IExpr arg = pointList.get(i);
        IExpr currentPointY = getPointY(arg);

        // Calculate X
        double xPrevRaw =
            useDataRange ? (drMin + (lastPosition - 1) * (drMax - drMin) / Math.max(1, count - 1))
                : lastPosition;
        double xCurrRaw =
            useDataRange ? (drMin + (i - 1) * (drMax - drMin) / Math.max(1, count - 1)) : i;
        if (count <= 1 && useDataRange) {
          xPrevRaw = drMin;
          xCurrRaw = drMin;
        }

        if (isNonReal(currentPointY)) {
          // Fix: If lastPoint was isolated (valid but not connected to anything), draw it now
          if (!isConnected && lastPoint.isPresent()) {
            addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
                xFunction.apply(F.num(xPrevRaw)), yFunction.apply(lastPoint), lastArg);
          }

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
              xFunction.apply(F.num(xPrevRaw)), yFunction.apply(lastPoint), lastArg);
          addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
              xFunction.apply(F.num(xCurrRaw)), yFunction.apply(currentPointY), arg);
          isConnected = true;
          continue;
        }
        if (isConnected) {
          addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
              xFunction.apply(F.num(xCurrRaw)), yFunction.apply(currentPointY), arg);
        }
        lastArg = arg;
        lastPoint = currentPointY;
        lastPosition = i;
      }
      if (!isConnected && lastPoint.isPresent()) {
        double xLastRaw =
            useDataRange ? (drMin + (lastPosition - 1) * (drMax - drMin) / Math.max(1, count - 1))
                : lastPosition;
        addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
            xFunction.apply(F.num(xLastRaw)), yFunction.apply(lastPoint), lastArg);
        addIndexedYPoint(pointPrimitives, graphicsExtraPrimitives, boundingbox, engine,
            xFunction.apply(F.num(xLastRaw)), yFunction.apply(lastPoint), lastArg);
      }

      if (pointPrimitives.argSize() > 0) {
        graphicsPrimitives.append(graphicsOptions.addPoints(pointPrimitives));
      }
      if (graphicsExtraPrimitives.argSize() > 0) {
        graphicsPrimitives.append(graphicsExtraPrimitives);
      }
    }
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
          // Manual Text creation with offset
          IExpr label = arg.arg2();
          // Text[label, {x,y}, {0, -1.5}]
          graphicsExtraPrimitives
              .append(F.function(S.Text, label, F.List(x, y), F.List(F.C0, F.num(-1.5))));
        } else if (arg.isAST(S.Style, 3)) {
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
        // Manual Text creation with offset
        IExpr label = currentYPrimitive.second();
        // Text[label, {x,y}, {0, -1.5}]
        textPrimitives
            .append(F.function(S.Text, label, F.List(xScaled, y), F.List(F.C0, F.num(-1.5))));
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
        || point.isIndeterminate() || point.isNone() || point.isAST(S.Missing);
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
        return pointsOfMatrix((IAST) arg1, minMax);
      }
    }
    return null;
  }

  public static IASTAppendable[] pointsOfMatrix(IAST tensor, double[] minMax) {
    IntArrayList dimensions = LinearAlgebraUtil.dimensions(tensor);
    if (dimensions.size() == 3 && dimensions.getInt(2) == 2) {
      IASTAppendable[] result = new IASTAppendable[tensor.argSize()];
      for (int i = 1; i < tensor.size(); i++) {
        result[i - 1] = listPlotMatrix(tensor.get(i), minMax);
      }
      return result;
    }
    if (dimensions.size() == 2 && dimensions.getInt(1) == 2) {
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

  protected GraphicsOptions setGraphicsOptions(final IExpr[] options, final EvalEngine engine) {
    return setGraphicsOptions(options, GraphicsOptions.listPlotDefaultOptionKeys(), engine);
  }

  protected GraphicsOptions setGraphicsOptions(final IExpr[] options,
      final IBuiltInSymbol[] optionSymbols, final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(optionSymbols, options, engine);
    return graphicsOptions;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(false, false));
  }

  protected static void extractCurvesRecursive(IExpr expr, List<CurveData> curves,
      IExpr currentColor) {
    if (expr.isList()) {
      IExpr localColor = currentColor;
      for (IExpr e : (IAST) expr) {
        if (isColor(e)) {
          localColor = e;
        } else if (e.isAST(S.Directive)) {
          for (IExpr d : (IAST) e) {
            if (isColor(d)) {
              localColor = d;
            }
          }
        } else {
          extractCurvesRecursive(e, curves, localColor);
        }
      }
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      ISymbol head = ast.topHead();
      if (head.equals(S.Line)) {
        if (ast.arg1().isList()) {
          curves.add(new CurveData((IAST) ast.arg1(), currentColor, false));
        }
      } else if (head.equals(S.Point)) {
        if (ast.arg1().isList()) {
          curves.add(new CurveData((IAST) ast.arg1(), currentColor, true));
        }
      } else if (head.equals(S.Style)) {
        IExpr newColor = currentColor;
        for (int i = 2; i <= ast.size(); i++) {
          IExpr arg = ast.get(i);
          if (isColor(arg)) {
            newColor = arg;
          }
        }
        extractCurvesRecursive(ast.arg1(), curves, newColor);
      } else if (head.equals(S.GraphicsGroup) || head.equals(S.Annotation)
          || head.equals(S.Tooltip)) {
        extractCurvesRecursive(ast.arg1(), curves, currentColor);
      }
    }
  }

  private static boolean isColor(IExpr e) {
    return e.isAST(S.RGBColor) || e.isAST(S.Hue) || e.isAST(S.GrayLevel) || e.isAST(S.CMYKColor)
        || e.isSymbol() && (e.equals(S.Red) || e.equals(S.Green) || e.equals(S.Blue)
            || e.equals(S.Black) || e.equals(S.White) || e.equals(S.Gray) || e.equals(S.Yellow)
            || e.equals(S.Cyan) || e.equals(S.Magenta) || e.equals(S.Orange) || e.equals(S.Pink)
            || e.equals(S.Purple) || e.equals(S.Brown));
  }

  private static IExpr createStemsToBottom(IAST pts, double yBottom) {
    if (pts.argSize() < 1)
      return F.NIL;
    IASTAppendable lines = F.ListAlloc(pts.size());
    IASTAppendable segments = F.ListAlloc(pts.size());
    for (IExpr pt : pts) {
      if (pt.isList2()) {
        IExpr x = ((IAST) pt).arg1();
        IExpr y = ((IAST) pt).arg2();
        segments.append(F.List(F.List(x, y), F.List(x, F.num(yBottom))));
      }
    }
    return F.Line(segments);
  }

  private static IExpr createPolygonToBottom(IAST pts) {
    if (pts.argSize() < 2)
      return F.NIL;
    double xStart = ((IAST) pts.arg1()).arg1().evalDouble();
    double xEnd = ((IAST) pts.get(pts.argSize())).arg1().evalDouble();
    double yBottom = 1e-10;
    IASTAppendable polyPts = F.ListAlloc();
    polyPts.appendArgs(pts);
    polyPts.append(F.List(F.num(xEnd), F.num(yBottom)));
    polyPts.append(F.List(F.num(xStart), F.num(yBottom)));
    return F.Polygon(polyPts);
  }

  private static IExpr createPolygonBetween(IAST pts1, IAST pts2) {
    IASTAppendable polyPts = F.ListAlloc();
    polyPts.appendArgs(pts1);
    for (int i = pts2.size() - 1; i >= 1; i--)
      polyPts.append(pts2.get(i));
    return F.Polygon(polyPts);
  }

  private static void processFillingAction(int srcIndex, IExpr target, List<CurveData> curves,
      IASTAppendable out, IExpr globalStyle, double baseline) {
    CurveData srcCurve = curves.get(srcIndex);
    IExpr styleToUse = globalStyle;
    if (styleToUse.isAutomatic() || styleToUse == null) {
      IExpr color = srcCurve.color != null ? srcCurve.color : S.Black;
      styleToUse = F.Directive(F.Opacity(0.2), color, F.EdgeForm(F.None));
    }

    if (target.isList() && target.first().isInteger()) {
      int targetIndex = ((IAST) target).first().toIntDefault(0) - 1;
      if (targetIndex >= 0 && targetIndex < curves.size()) {
        out.append(F.List(styleToUse,
            createPolygonBetween(srcCurve.points, curves.get(targetIndex).points)));
      }
    } else if (target.equals(S.Axis) || target.equals(S.Bottom)) {
      if (srcCurve.isPoint) {
        out.append(F.List(styleToUse, createStemsToBottom(srcCurve.points, baseline)));
      } else {
        out.append(F.List(styleToUse, createPolygonToBottom(srcCurve.points, baseline)));
      }
    }
  }

  private static IExpr createPolygonToBottom(IAST pts, double yBottom) {
    if (pts.argSize() < 2)
      return F.NIL;
    double xStart = ((IAST) pts.arg1()).arg1().evalf();
    double xEnd = ((IAST) pts.get(pts.argSize())).arg1().evalf();
    IASTAppendable polyPts = F.ListAlloc();
    polyPts.appendArgs(pts);
    polyPts.append(F.List(F.num(xEnd), F.num(yBottom)));
    polyPts.append(F.List(F.num(xStart), F.num(yBottom)));
    return F.Polygon(polyPts);
  }

  private static void processSingleFillingRule(IExpr rule, List<CurveData> curves,
      IASTAppendable out, IExpr globalStyle, double baseline) {
    if (rule.isRule()) {
      int srcIndex = ((IAST) rule).arg1().toIntDefault(0) - 1;
      IExpr target = ((IAST) rule).arg2();
      if (srcIndex >= 0 && srcIndex < curves.size()) {
        processFillingAction(srcIndex, target, curves, out, globalStyle, baseline);
      }
    }
  }

  protected static IAST processFilling(IExpr primitives, IExpr filling, IExpr fillingStyle,
      double baseline) {
    List<CurveData> curves = new ArrayList<>();
    extractCurvesRecursive(primitives, curves, null);
    if (curves.isEmpty())
      return (IAST) primitives;
    IASTAppendable fillingPrimitives = F.ListAlloc();
    IExpr defaultStyle = fillingStyle;

    if (filling.isList()) {
      for (IExpr rule : (IAST) filling)
        processSingleFillingRule(rule, curves, fillingPrimitives, defaultStyle, baseline);
    } else {
      for (int i = 0; i < curves.size(); i++)
        processFillingAction(i, filling, curves, fillingPrimitives, defaultStyle, baseline);
    }

    IASTAppendable result = F.ListAlloc();
    result.appendArgs(fillingPrimitives);
    if (primitives.isList())
      result.appendArgs((IAST) primitives);
    else
      result.append(primitives);
    return result;
  }

  protected static IAST checkForExpressionsLegend(IExpr functions,
      GraphicsOptions graphicsOptions) {
    boolean expressionsRequested = false;
    if (graphicsOptions.plotLegends().toString().equalsIgnoreCase("Expressions")) {
      expressionsRequested = true;
    } else {
      IAST listOfRules = graphicsOptions.getListOfRules();
      for (IExpr opt : listOfRules) {
        if (opt.isRuleAST()) {
          IExpr key = ((IAST) opt).arg1();
          IExpr val = ((IAST) opt).arg2();

          if ((key.equals(S.PlotLegends) || key.equals(S.PlotLabels))
              && val.toString().equalsIgnoreCase("Expressions")) {
            expressionsRequested = true;
            continue;
          }
        }
      }
    }

    if (expressionsRequested) {
      IASTAppendable legendList = F.ListAlloc();
      if (functions.isList()) {
        for (IExpr f : (IAST) functions) {
          legendList.append(F.stringx(f.toString()));
        }
      } else {
        legendList.append(F.stringx(functions.toString()));
      }
      return F.Rule(S.PlotLegends, legendList);
    }

    return F.NIL;
  }
}
