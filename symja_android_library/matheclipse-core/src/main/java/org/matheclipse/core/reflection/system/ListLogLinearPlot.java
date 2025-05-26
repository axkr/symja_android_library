package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.graphics.ECharts;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ListLogLinearPlot extends ListPlot {

  @Override
  public IExpr evaluateECharts(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    StringBuilder jsControl = new StringBuilder();
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    String graphicsPrimitivesStr = listLogLinearPlot(ast, options, graphicsOptions, engine);
    if (graphicsPrimitivesStr != null) {
      jsControl.append("var eChart = echarts.init(document.getElementById('main'));\n");
      jsControl.append(graphicsPrimitivesStr);
      jsControl.append("\neChart.setOption(option);");

      return F.JSFormData(jsControl.toString(), "echarts");
    }
    return F.NIL;
  }

  protected static String listLogLinearPlot(IAST plot, IExpr[] options,
      GraphicsOptions graphicsOptions, EvalEngine engine) {
    if (plot.size() < 2) {
      return null;
    }
    graphicsOptions.setGraphicOptions(options, engine);
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
      if (pointList.isList()) {
        if (pointList.isListOfPoints(2)) {
          return point2DListLogLinearPlot(pointList, graphicsOptions);
        }
        if (pointList.isListOfLists()) {
          IAST listOfLists = pointList;

          StringBuilder yAxisSeriesBuffer = new StringBuilder();
          String type = graphicsOptions.isJoined() ? ECharts.TYPE_LINE : ECharts.TYPE_SCATTER;
          ECharts.seriesData(yAxisSeriesBuffer, listOfLists, graphicsOptions, type, "");
          StringBuilder xAxisCategoryBuffer = new StringBuilder();
          ECharts.xAxisCategory(xAxisCategoryBuffer, (IAST) listOfLists.arg1());
          ECharts echarts = ECharts.build(graphicsOptions, xAxisCategoryBuffer, yAxisSeriesBuffer);
          echarts.setXAxisMin("log", 0.1);
          echarts.setYAxis("value");
          return echarts.getJSONStr();
        }

      }
      return yValueListLogLinearPlot(pointList, graphicsOptions);
    }
    return null;
  }

  /**
   * Plot a list of 2D points.
   * 
   * @param pointList2D list of 2D points
   * @return
   */
  private static String point2DListLogLinearPlot(IAST pointList2D,
      GraphicsOptions graphicsOptions) {
    StringBuilder xAxisString = new StringBuilder();
    StringBuilder yAxisString = new StringBuilder();
    // yAxisString.append( //
    // "{\n" //
    // + " name: 'ListLogLinearPlot',\n" //
    // + " type: 'line',\n");
    String type = graphicsOptions.isJoined() ? ECharts.TYPE_LINE : ECharts.TYPE_SCATTER;
    // ECharts.xyAxesPoint2D(pointList2D, xAxisString, yAxisString, graphicsOptions, type, "");
    ECharts.seriesData2D(pointList2D, yAxisString, graphicsOptions, type, "");
    // ECharts echarts = new ECharts("ListLogPlot");
    // // legend
    // echarts.setLegend("");
    // // x-axis categories
    // echarts.setXAxis();
    // echarts.setXData(xAxisString.toString());
    // // y-axis series
    // echarts.setYAxis("log", "");
    // echarts.setSeriesValues(yAxisString.toString());

    ECharts echarts = ECharts.build(graphicsOptions, xAxisString, yAxisString);
    echarts.setXAxisMin("log", 0.1);
    echarts.setYAxis("value");
    return echarts.getJSONStr();
  }

  private static String yValueListLogLinearPlot(IAST pointList, GraphicsOptions graphicsOptions) {
    double[] minMax = new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    // y-axis values
    StringBuilder yAxisString = new StringBuilder();
    // yAxisString.append( //
    // "{\n" //
    // + " name: 'ListLogLogPlot',\n" //
    // + " type: 'line',\n");
    ECharts.yAxisSingleSeries(yAxisString, pointList, graphicsOptions, minMax);
    // yAxisSingleSeries(yAxisString, pointList, "");

    // x-axis categories
    StringBuilder xAxisString = new StringBuilder();
    ECharts.xAxisCategory(xAxisString, pointList);

    ECharts echarts = ECharts.build(graphicsOptions, xAxisString, yAxisString);
    echarts.setXAxisMin("log", 0.1);
    echarts.setYAxis("value");
    return echarts.getJSONStr();
  }
  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options, final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(options, engine);
    graphicsOptions.setXFunction(x -> F.Log10(x));
    graphicsOptions.setXScale("Log10");
    return graphicsOptions;
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (ToggleFeature.JS_ECHARTS) {
      return evaluateECharts(ast, argSize, options, engine, originalAST);
    }
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    IAST graphicsPrimitives = listPlot(ast, options, graphicsOptions, engine);
    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      return createGraphicsFunction(graphicsPrimitives, graphicsOptions);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(false, false));
  }
}
