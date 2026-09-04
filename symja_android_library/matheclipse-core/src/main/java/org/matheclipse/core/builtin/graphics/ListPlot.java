package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.builtin.QuantityFunctions;
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
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.external.fastutil.ints.IntArrayList;

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

  /**
   * The call with a <code>Dataset</code> first argument replaced by its rows, and the call itself
   * otherwise.
   *
   * <p>
   * For the one-column dataset these plots are usually handed -
   * <code>planets[All, "radius"]</code> - the rows are already the bare list of numbers
   * <code>checkList</code> is looking for, and without this that check rejects the dataset before
   * anything else runs. Deliberately not applied to an <code>Association</code>, which this family
   * already plots its own way.
   */
  protected static IAST withDatasetRows(IAST ast) {
    IExpr rows = IASTDataset.normalizeDataset(ast.arg1());
    return rows == ast.arg1() ? ast : ast.setAtCopy(1, rows);
  }

  /**
   * Replace quantity data by its magnitudes before the plotting pipeline sees it.
   *
   * <p>
   * That pipeline reaches {@code toDoubleVectorIgnore}, which cannot turn a quantity into a
   * machine number and drops it in silence, so plotting a list of quantities produced a
   * <code>Graphics</code> holding no <code>Line</code> at all. See
   * {@link QuantityFunctions#quantityPlotMagnitudes}.
   */
  protected static IAST withQuantityMagnitudes(IAST ast, IAST originalAST, EvalEngine engine) {
    IExpr data = QuantityFunctions.quantityPlotMagnitudes(ast.arg1(),
        GraphicsOptions.optionValue(originalAST, S.TargetUnits, S.Automatic), engine);
    return data == ast.arg1() ? ast : ast.setAtCopy(1, data);
  }

  public IExpr evaluateECharts(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    ast = withQuantityMagnitudes(withDatasetRows(ast), originalAST, engine);
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine, originalAST);
    // PlotMarkers and Mesh are family options appended after the positional block, so they
    // are read from the call rather than by index
    graphicsOptions
        .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.Automatic));
    graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
    graphicsOptions.readColorFunction(originalAST);
    graphicsOptions.applyPlotTheme(originalAST);
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
    ast = withQuantityMagnitudes(withDatasetRows(ast), originalAST, engine);
    IExpr arg1 = ast.arg1();
    if (!checkList(engine, arg1)) {
      return Errors.printMessage(ast.topHead(), "lpn", F.List(arg1), engine);
    }
    if (ToggleFeature.JS_ECHARTS) {
      return evaluateECharts(ast, argSize, options, engine, originalAST);
    }
    if (options[GraphicsOptions.X_JSFORM].isTrue()) {
      IExpr temp = S.Manipulate.funEval(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }
    GraphicsOptions graphicsOptions =
        setGraphicsOptions(options, GraphicsOptions.listPlotDefaultOptionKeys(), engine);
    graphicsOptions.forwardOptions(originalAST);
    // PlotMarkers and Mesh are family options appended after the positional block, so they
    // are read from the call rather than by index
    graphicsOptions
        .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.Automatic));
    graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
    graphicsOptions.readColorFunction(originalAST);
    graphicsOptions.applyPlotTheme(originalAST);

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
        if (options[GraphicsOptions.X_PLOTLEGENDS] == S.None) {
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

  /**
   * Whether this plot already takes a display wrapper off its first argument itself.
   *
   * <p>
   * The plots that read their data point by point put each label where it belongs and must not
   * have one applied over the whole picture afterwards - an outer label wins in the renderer, so a
   * second one would overwrite every label the plot had just placed. The plots that draw their data
   * as a single field or raster do not read wrappers at all, and say so by overriding this, which
   * is what gets them a label over the picture for free.
   *
   * <p>
   * The default is the cautious one: a plot that says nothing is assumed to have handled it, so a
   * new plot cannot silently overwrite its own labels.
   */
  protected boolean readsArgumentWrapper() {
    return true;
  }

  /**
   * The primitives, with one label over all of them when the plot did not read the wrapper itself.
   *
   * <p>
   * A field or raster plot draws its data as one picture, so the only level a label can sit at is
   * the whole of it. The plots that read their data point by point have already placed their
   * labels and say so through {@link #readsArgumentWrapper()}.
   */
  protected IExpr labelledContent(IAST graphicsPrimitives, IAST plotAST) {
    if (readsArgumentWrapper() || plotAST == null || plotAST.size() <= 1) {
      return graphicsPrimitives;
    }
    return PlotWrapper.of(plotAST.arg1()).wrapTooltip(graphicsPrimitives);
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
            graphicsOptions.fillingStyle(), 0.0, graphicsOptions.effectiveYScale());
        graphicsPrimitives = newPrimitives;
      } catch (RuntimeException rex) {
      }
    }

    IASTAppendable result = F.Graphics(labelledContent(graphicsPrimitives, plotAST));
    result.appendArgs(graphicsOptions.getListOfRules());
    // System.out.println(result);
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

    // a wrapper around one dataset says how that whole curve is shown, so it comes off before the
    // shape is judged and goes back on around everything the curve drew
    PlotWrapper[] curveWrappers = null;
    if (arg1.isList()) {
      IAST outer = (IAST) arg1;
      curveWrappers = new PlotWrapper[outer.size()];
      IASTAppendable stripped = F.ListAlloc(outer.argSize());
      boolean anyWrapper = false;
      for (int j = 1; j < outer.size(); j++) {
        curveWrappers[j] = PlotWrapper.of(outer.get(j));
        stripped.append(curveWrappers[j].datum);
        anyWrapper |= !curveWrappers[j].isPlain();
      }
      if (anyWrapper) {
        arg1 = stripped;
      }
    }

    if (arg1.isListOfLists()) {
      IAST listOfLists = (IAST) arg1;
      final IASTAppendable graphicsPrimitives = F.ListAlloc();
      for (int j = 1; j < listOfLists.size(); j++) {
        IAST curveData = (IAST) listOfLists.get(j);
        PlotWrapper curveWrapper =
            curveWrappers != null && j < curveWrappers.length ? curveWrappers[j] : null;
        // when this curve carries a tooltip its primitives are gathered so the label can enclose
        // them; without one they go straight into the shared list, as they always did
        final IASTAppendable curvePrimitives =
            curveWrapper != null && curveWrapper.hasTooltip() ? F.ListAlloc() : graphicsPrimitives;

        IExpr defaultColor = GraphicsOptions.plotStyleDirective(graphicsOptions.incColorIndex(),
            F.NIL, graphicsOptions.curveThickness());
        IExpr style = defaultColor;
        if (!plotStyle.isNone()) {
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
            sequencePointListPlot(curvePrimitives, segment, graphicsOptions, style, xFunction,
                yFunction, engine);
          }
        } else {
          if (curveData.isListOfPoints(2)) {
            sequencePointListPlot(curvePrimitives, curveData, graphicsOptions, style, xFunction,
                yFunction, engine);
          } else {
            sequenceYValuesListPlot(curvePrimitives, curveData, graphicsOptions, style, engine);
          }
        }
        if (curvePrimitives != graphicsPrimitives && curvePrimitives.argSize() > 0) {
          graphicsPrimitives
              .append(F.binaryAST2(S.Tooltip, curvePrimitives, curveWrapper.tooltip));
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

    // a wrapper around the whole dataset comes off before its shape is judged, and its label goes
    // back on around everything the dataset drew
    PlotWrapper dataWrapper = PlotWrapper.of(arg1);
    if (!dataWrapper.isPlain()) {
      arg1 = dataWrapper.datum;
    }

    if (arg1.isNonEmptyList()) {
      final IASTAppendable graphicsPrimitives = F.ListAlloc();
      IAST pointList = (IAST) arg1;
      if (pointList.isList()) {
        if (pointList.isListOfPoints(2)) {
          IExpr defaultColor = GraphicsOptions.plotStyleDirective(graphicsOptions.incColorIndex(),
              F.NIL, graphicsOptions.curveThickness());
          IExpr style = defaultColor;
          if (plotStyle.isPresent() && plotStyle != S.None) {
            IExpr userStyle = GraphicsOptions.getPlotStyle(plotStyle, 0);
            style = F.Directive(defaultColor, userStyle);
          }

          Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
          Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();
          sequencePointListPlot(graphicsPrimitives, pointList, graphicsOptions, style, xFunction,
              yFunction, engine);
          return dataWrapper.hasTooltip()
              ? F.List(F.binaryAST2(S.Tooltip, graphicsPrimitives, dataWrapper.tooltip))
              : graphicsPrimitives;
        }
        // each of several datasets may be wrapped in its own right, so the wrappers come off
        // before the shape is judged and each label goes back on around what that dataset drew
        IASTAppendable strippedLists = null;
        PlotWrapper[] datasetWrappers = null;
        if (pointList.isList()) {
          datasetWrappers = new PlotWrapper[pointList.size()];
          strippedLists = F.ListAlloc(pointList.argSize());
          boolean anyWrapper = false;
          for (int i = 1; i < pointList.size(); i++) {
            datasetWrappers[i] = PlotWrapper.of(pointList.get(i));
            strippedLists.append(datasetWrappers[i].datum);
            anyWrapper |= !datasetWrappers[i].isPlain();
          }
          if (anyWrapper && strippedLists.isListOfLists()) {
            pointList = strippedLists;
          } else {
            datasetWrappers = null;
          }
        }
        if (pointList.isListOfLists()) {
          IAST listOfLists = pointList;
          for (int i = 1; i < listOfLists.size(); i++) {
            pointList = (IAST) listOfLists.get(i);

            IExpr defaultColor = GraphicsOptions.plotStyleDirective(graphicsOptions.incColorIndex(),
                F.NIL, graphicsOptions.curveThickness());
            IExpr style = defaultColor;
            if (plotStyle.isPresent() && plotStyle != S.None) {
              IExpr userStyle = GraphicsOptions.getPlotStyle(plotStyle, i - 1);
              style = F.Directive(defaultColor, userStyle);
            }

            PlotWrapper each =
                datasetWrappers != null && i < datasetWrappers.length ? datasetWrappers[i] : null;
            final IASTAppendable into =
                each != null && each.hasTooltip() ? F.ListAlloc() : graphicsPrimitives;
            if (pointList.isListOfPoints(2)) {
              Function<IExpr, IExpr> xFunction = graphicsOptions.xFunction();
              Function<IExpr, IExpr> yFunction = graphicsOptions.yFunction();
              sequencePointListPlot(into, pointList, graphicsOptions, style, xFunction, yFunction,
                  engine);
            } else {
              sequenceYValuesListPlot(into, pointList, graphicsOptions, style, engine);
            }
            if (into != graphicsPrimitives && into.argSize() > 0) {
              graphicsPrimitives.append(F.binaryAST2(S.Tooltip, into, each.tooltip));
            }
          }
          return dataWrapper.hasTooltip()
              ? F.List(F.binaryAST2(S.Tooltip, graphicsPrimitives, dataWrapper.tooltip))
              : graphicsPrimitives;
        }

      }
      IExpr defaultColor = GraphicsOptions.plotStyleDirective(graphicsOptions.incColorIndex(),
          F.NIL, graphicsOptions.curveThickness());
      IExpr style = defaultColor;
      if (plotStyle.isPresent() && plotStyle != S.None) {
        IExpr userStyle = GraphicsOptions.getPlotStyle(plotStyle, 0);
        style = F.Directive(defaultColor, userStyle);
      }

      sequenceYValuesListPlot(graphicsPrimitives, pointList, graphicsOptions, style, engine);
      return dataWrapper.hasTooltip()
          ? F.List(F.binaryAST2(S.Tooltip, graphicsPrimitives, dataWrapper.tooltip))
          : graphicsPrimitives;
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
            // If lastPoint was isolated, render it now before breaking
            if (!isConnected && lastPoint.isPresent()) {
              IExpr xLast = xFunction.apply(lastPoint.arg1());
              IExpr yLast = yFunction.apply(lastPoint.arg2());
              if (xBoundingBox(boundingbox, xLast, engine)
                  && yBoundingBox(boundingbox, yLast, engine)) {
                addSinglePoint(graphicsOptions, pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xLast,
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
              addSinglePoint(graphicsOptions, pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xLast,
                  yLast, lastArg);
            }

            if (xBoundingBox(boundingbox, xValue, engine)
                && yBoundingBox(boundingbox, yValue, engine)) {
              addSinglePoint(graphicsOptions, pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xValue,
                  yValue, (IAST) arg);
              isConnected = true;
              continue;
            }
          }
          if (isConnected) {
            if (xBoundingBox(boundingbox, xValue, engine)
                && yBoundingBox(boundingbox, yValue, engine)) {
              addSinglePoint(graphicsOptions, pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xValue,
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
            addSinglePoint(graphicsOptions, pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xLast,
                yLast, lastArg);
            addSinglePoint(graphicsOptions, pointPrimitives, graphicsExtraPrimitives, boundingbox, engine, xLast,
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
    // a display wrapper says how the point is shown, not where it is
    IExpr datum = arg.stripDisplayWrappers();
    return datum.isList2() ? (IAST) datum : F.NIL;
  }

  protected static IExpr getPointY(IExpr arg) {
    return arg.stripDisplayWrappers();
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
      drMin = ((IAST) dataRange).arg1().evalfNaN();
      drMax = ((IAST) dataRange).arg2().evalfNaN();
      useDataRange = !Double.isNaN(drMin) && !Double.isNaN(drMax);
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
          // If lastPoint was isolated (valid but not connected to anything), draw it now
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

  private static boolean addSinglePoint(GraphicsOptions graphicsOptions,
      IASTAppendable pointPrimitives, IASTAppendable graphicsExtraPrimitives, double[] boundingbox,
      EvalEngine engine, IExpr xScaled, IExpr yScaled, IAST arg) {
    IReal x = xScaled.evalReal();
    IReal y = yScaled.evalReal();
    if (x != null && y != null) {
      if (xBoundingBox(boundingbox, x, engine) && yBoundingBox(boundingbox, y, engine)) {
        IAST scaledPoint = F.List(x, y);
        // the scaling functions rebuild the point, so anything the sampler recorded about it -
        // the parameter of a parametric curve - has to follow it here or it is lost
        graphicsOptions.carryPointParameters(arg, scaledPoint);
        pointPrimitives.append(scaledPoint);
        if (arg.isAST(S.Labeled, 3)) {
          // Manual Text creation with offset
          IExpr label = arg.arg2();
          // Text[label, {x,y}, {0, -1.5}]
          graphicsExtraPrimitives
              .append(F.function(S.Text, label, scaledPoint, F.List(F.C0, F.num(-1.5))));
        } else if (arg.isAST(S.Style, 3)) {
          IASTMutable styledPoint = arg.setAtCopy(1, F.Point(scaledPoint));
          graphicsExtraPrimitives.append(styledPoint);
        }
        appendHitTarget(graphicsExtraPrimitives, graphicsOptions, arg, scaledPoint);
        return true;
      }
    }
    return false;
  }

  /**
   * A transparent mark over a tooltipped point, so there is something to hover.
   *
   * <p>
   * The point itself stays in the bulk primitive that carries the whole dataset - taking it out
   * would cost it its colour function, its mesh spacing, its share of the bounding box and the
   * parameter a parametric curve recorded on it. So the tooltip goes on a mark of its own, laid
   * over the drawn one. It is a {@code Point} rather than a {@code Disk} because a point size is
   * resolved in pixels: a disk radius is in data units and would come out as a scale dependent
   * ellipse on any plot whose axes differ. It is invisible but still answers a hover, because the
   * renderer writes a fully transparent fill rather than no fill at all, and SVG hit tests a
   * painted fill whatever its opacity.
   *
   * @param graphicsOptions the plot's options, for the drawn point size; may be {@code null}
   * @param arg the datum as written, which is where the tooltip is read from
   */
  protected static void appendHitTarget(IASTAppendable extraPrimitives,
      GraphicsOptions graphicsOptions, IExpr arg, IAST scaledPoint) {
    appendHitTargetFor(extraPrimitives, graphicsOptions, PlotWrapper.of(arg).tooltip, scaledPoint);
  }

  /**
   * The same, for a plot that has already taken the wrapper apart and holds the label by itself.
   *
   * @param label the tooltip text, or {@link F#NIL} to add nothing
   */
  protected static void appendHitTargetFor(IASTAppendable extraPrimitives,
      GraphicsOptions graphicsOptions, IExpr label, IAST scaledPoint) {
    if (label == null || !label.isPresent()) {
      return;
    }
    double drawn = graphicsOptions == null ? 0.0 : graphicsOptions.pointSize();
    // never smaller than the mark it covers, and large enough to be worth aiming at
    double hitSize = Math.max(drawn * 1.5, 0.02);
    extraPrimitives.append(F.binaryAST2(S.Tooltip,
        F.List(F.unaryAST1(S.Opacity, F.C0), F.unaryAST1(S.PointSize, F.num(hitSize)),
            F.Point(scaledPoint)),
        label));
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
      appendHitTarget(textPrimitives, null, currentYPrimitive, F.List(xScaled, y));
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

  /**
   * The graphics options of a call, including the ones the positional block does not carry.
   *
   * @param originalAST the unevaluated call, read for the options that are not positional
   */
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
    graphicsOptions.forwardOptions(originalAST);
    return graphicsOptions;
  }

  protected GraphicsOptions setGraphicsOptions(final IExpr[] options,
      final IBuiltInSymbol[] optionSymbols, final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(optionSymbols, options, engine);
    return graphicsOptions;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet optionSet = GraphicsOptions.listPlotExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(),
            GraphicsOptions.listPlotDefaultOptionValues(false, false)));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
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
      if (head == S.Line) {
        if (ast.arg1().isList()) {
          curves.add(new CurveData((IAST) ast.arg1(), currentColor, false));
        }
      } else if (head == S.Point) {
        if (ast.arg1().isList()) {
          curves.add(new CurveData((IAST) ast.arg1(), currentColor, true));
        }
      } else if (head == S.Style) {
        IExpr newColor = currentColor;
        for (int i = 2; i <= ast.size(); i++) {
          IExpr arg = ast.get(i);
          if (isColor(arg)) {
            newColor = arg;
          }
        }
        extractCurvesRecursive(ast.arg1(), curves, newColor);
      } else if (head == S.GraphicsGroup || head == S.Annotation
          || head == S.Tooltip) {
        // a fully transparent group is a hover target rather than something drawn, so it has no
        // curve to fill under; without this the mark over a tooltipped point becomes a spurious
        // one point curve and the fill goes down to the axis beneath it
        if (!isTransparent(ast.arg1())) {
          extractCurvesRecursive(ast.arg1(), curves, currentColor);
        }
      }
    }
  }

  /** Whether these primitives are drawn with no ink at all, and so describe no curve. */
  private static boolean isTransparent(IExpr primitives) {
    if (!primitives.isList()) {
      return false;
    }
    IAST list = (IAST) primitives;
    for (int i = 1; i < list.size(); i++) {
      IExpr part = list.get(i);
      if (part.isAST(S.Opacity, 2) && part.first().isZero()) {
        return true;
      }
    }
    return false;
  }

  private static boolean isColor(IExpr e) {
    return e.isAST(S.RGBColor) || e.isAST(S.Hue) || e.isAST(S.GrayLevel) || e.isAST(S.CMYKColor)
        || e.isSymbol() && (e == S.Red || e == S.Green || e == S.Blue
            || e == S.Black || e == S.White || e == S.Gray || e == S.Yellow
            || e == S.Cyan || e == S.Magenta || e == S.Orange || e == S.Pink
            || e == S.Purple || e == S.Brown);
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

  /** True for the logarithmic y scalings the plot family uses. */
  protected static boolean isLogScale(String scale) {
    return scale != null && (scale.equalsIgnoreCase("Log") || scale.equalsIgnoreCase("Log10")
        || scale.equalsIgnoreCase("Log2"));
  }

  /** The smallest strictly positive y value across all curves, or 0 when there is none. */
  private static double smallestPositiveY(List<CurveData> curves) {
    double smallest = Double.MAX_VALUE;
    for (CurveData curve : curves) {
      for (int i = 1; i < curve.points.size(); i++) {
        IExpr point = curve.points.get(i);
        if (point.isList() && ((IAST) point).argSize() >= 2) {
          double y = ((IAST) point).arg2().evalfNaN();
          if (Double.isFinite(y) && y > 0 && y < smallest) {
            smallest = y;
          }
        }
      }
    }
    return smallest == Double.MAX_VALUE ? 0 : smallest;
  }

  private static void processFillingAction(int srcIndex, IExpr target, List<CurveData> curves,
      IASTAppendable out, IExpr globalStyle, double baseline) {
    CurveData srcCurve = curves.get(srcIndex);
    IExpr styleToUse = globalStyle;
    if (styleToUse == null || styleToUse.isAutomatic()) {
      IExpr color = srcCurve.color != null ? srcCurve.color : S.Black;
      styleToUse = F.Directive(F.Opacity(0.2), color, F.EdgeForm(F.None));
    }

    if (target.isList() && target.first().isInteger()) {
      int targetIndex = ((IAST) target).first().toIntDefault(0) - 1;
      if (targetIndex >= 0 && targetIndex < curves.size()) {
        out.append(F.List(styleToUse,
            createPolygonBetween(srcCurve.points, curves.get(targetIndex).points)));
      }
    } else if (target == S.Axis || target == S.Bottom) {
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
    double xStart = ((IAST) pts.arg1()).arg1().evalfNaN();
    double xEnd = ((IAST) pts.get(pts.argSize())).arg1().evalfNaN();
    if (Double.isNaN(xStart) || Double.isNaN(xEnd)) {
      return F.NIL;
    }
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

  /**
   * @param yScale the y axis scaling, so that a logarithmic axis can be filled to the bottom of the
   *        data instead of to zero
   */
  protected static IAST processFilling(IExpr primitives, IExpr filling, IExpr fillingStyle,
      double baseline, String yScale) {
    List<CurveData> curves = new ArrayList<>();
    extractCurvesRecursive(primitives, curves, null);
    if (curves.isEmpty())
      return (IAST) primitives;
    if (isLogScale(yScale)) {
      // Zero lies at minus infinity on a logarithmic axis, so filling down to it would stretch the
      // visible range by however far the converter clamps the logarithm, burying the data at the
      // top of the picture. Fill to the smallest value actually plotted instead.
      double smallest = smallestPositiveY(curves);
      if (smallest > 0) {
        baseline = smallest;
      }
    }
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

          if ((key == S.PlotLegends || key == S.PlotLabels)
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
