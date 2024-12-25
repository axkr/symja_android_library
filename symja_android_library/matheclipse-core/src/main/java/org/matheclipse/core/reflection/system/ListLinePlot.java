package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.ECharts;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Points as a single line */
public class ListLinePlot extends ListPlot {

  public ListLinePlot() {}

  @Override
  public IExpr evaluateECharts(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    StringBuilder jsControl = new StringBuilder();
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    String graphicsPrimitivesStr = listLinePlot(ast, options, graphicsOptions, engine);
    if (graphicsPrimitivesStr != null) {
      jsControl.append(
          "var eChart = echarts.init(document.getElementById('main'),{width:'100%',height:'100%'});\n");
      jsControl.append(graphicsPrimitivesStr);
      jsControl.append("\neChart.setOption(option);");

      return F.JSFormData(jsControl.toString(), "echarts");
    }
    return F.NIL;
  }

  protected static String listLinePlot(IAST plot, IExpr[] options, GraphicsOptions graphicsOptions,
      EvalEngine engine) {
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
    if (arg1.isNonEmptyList()) {
      IAST pointList = (IAST) arg1;
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
          echarts.setXAxis();
          echarts.setYAxis("value");
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
    IExpr arg1 = ast.arg1();
    if (!checkList(engine, arg1)) {
      // `1` is not a list of numbers or pairs of numbers.
      return Errors.printMessage(ast.topHead(), "lpn", F.List(arg1), engine);
    }
    if (ToggleFeature.JS_ECHARTS) {
      return evaluateECharts(ast, argSize, options, engine, originalAST);
    }
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setJoined(true);
    IAST graphicsPrimitives = listPlot(ast, options, graphicsOptions, engine);
    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      IAST listOfOptions = F.List(F.Rule(S.Axes, S.True), //
          graphicsOptions.plotRange());
      return createGraphicsFunction(graphicsPrimitives, listOfOptions, graphicsOptions);
    }

    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(true));
  }
}
