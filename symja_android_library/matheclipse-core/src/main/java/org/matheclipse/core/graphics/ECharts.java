package org.matheclipse.core.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ECharts {
  public final static int X_JSFORM = 0;

  public final static int X_FILLING = 1;

  public final static int X_AXES = 2;

  public final static int X_PLOTRANGE = 3;

  public final static int X_$SCALING = 4;

  public final static int X_JOINED = 5;

  public final static int X_PLOTLEGENDS = 6;

  public final static int X_PLOTLABEL = 7;

  public final static int X_AXESLABEL = 8;

  // public static final String DEFAULT_SERIES_NAME = "Series";

  public static final String TYPE_CATEGORY = "category";

  public static final String TYPE_LINE = "line";

  public static final String TYPE_SCATTER = "scatter";

  public static final String TYPE_VALUE = "value";

  private static final String JSON_TEMPLATE = //
      "option={\n" //
          + "  title: {\n" //
          + "    text: \"`title`\"\n" //
          + "  },\n" //
          + "  tooltip: {\n" //
          + "    trigger: \"axis\"\n" //
          + "  },\n" //
          + "  legend: {\n`legend`\n" // "data: ['Step Start', 'Step Middle', 'Step End']\n" //
          + "  },\n" //
          + "  grid: {\n" //
          + "    left: '3%',\n" //
          + "    right: '4%',\n" //
          + "    bottom: '3%',\n" //
          + "    containLabel: true\n" //
          + "  },\n" //
          + "  toolbox: {\n" //
          + "    feature: {\n" //
          + "      saveAsImage: {}\n" //
          + "    }\n" //
          + "  },\n" //
          + "  xAxis: {\n" //
          + "    `xaxis`\n" //
          + "    `xdata`\n" //
          + "  },\n" //
          + "  yAxis: {\n" //
          + "    `yaxis`\n" //
          // + " type: 'log',\n" //
          // + " name: 'y'" //
          + "  },\n" //
          + "  series: [\n`series`\n" //
          // + " {\n" //
          // + " name: 'Step Start',\n" //
          // + " type: 'line',\n" //
          // + " step: 'start',\n" //
          // + " data: [120, 132, 101, 134, 90, 230, 210]\n" //
          // + " },\n" //
          + "  ]\n" //
          + "};";


  public static ECharts build(GraphicsOptions graphicsOptions, StringBuilder xAxisCategoryBuffer,
      StringBuilder yAxisSeriesBuffer) {
    String plotLabel = createPlotLabel(graphicsOptions);
    ECharts echarts = new ECharts(graphicsOptions, plotLabel);
    // // legend
    String legends = createLegends(graphicsOptions);
    echarts.setLegend(legends);
    // x-axis categories
    if (xAxisCategoryBuffer == null || xAxisCategoryBuffer.length() == 0) {
      echarts.setXData(null);
    } else {
      echarts.setXData(xAxisCategoryBuffer.toString());
    }

    // y-axis series
    echarts.setSeriesValues(yAxisSeriesBuffer.toString());
    // legend
    return echarts;
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

  protected static boolean isNonReal(IExpr point) {
    return point.isComplex() || point.isComplexNumeric() || point.isDirectedInfinity()
        || point == S.Indeterminate || point == S.None || point.isAST(S.Missing);
  }

  protected static boolean isNonReal(IExpr lastPointX, IExpr lastPointY) {
    return isNonReal(lastPointX) || isNonReal(lastPointY);
  }

  public static void seriesData(StringBuilder yAxisSeriesBuffer, IAST listOfLists,
      GraphicsOptions graphicsOptions, String type, String step) {
    double[] minMax = new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    final int argSize = listOfLists.argSize();
    String[] legends = createLegends(graphicsOptions, argSize);
    for (int i = 1; i <= argSize; i++) {
      IAST singlePointList = (IAST) listOfLists.get(i);
      // y-axis values
      StringBuilder yAxisString = new StringBuilder();
      // yAxisString.append( //
      // "{\n" //
      // + " name: '" + name + i + "',\n" //
      // + " type: '" + type + "',\n"); //
      String stepI = "";
      if (step != null && step.length() > 0) {
        // step must contain a string with length greater than 0
        // yAxisString.append(" step: '" + step + i + "',\n");
        stepI = step + i;
      }
      ECharts.yAxisSingleSeries(yAxisString, singlePointList, legends[i - 1], type, stepI, minMax);
      yAxisSeriesBuffer.append(yAxisString);
      if (i < listOfLists.size() - 1) {
        yAxisSeriesBuffer.append(",\n");
      }
    }
  }

  private static String createPlotLabel(GraphicsOptions graphicsOptions) {
    IExpr plotLabel = graphicsOptions.plotLabel();
    if (plotLabel != S.None) {
      return plotLabel.toString();
    }
    return "";
  }

  private static String[] createLegends(GraphicsOptions graphicsOptions, final int argSize) {
    String[] legends = new String[argSize];
    for (int i = 0; i < argSize; i++) {
      legends[i] = "";
    }
    IExpr plotLegends = graphicsOptions.plotLegends();
    if (plotLegends != S.None) {
      if (plotLegends == S.Automatic) {
        for (int i = 0; i < argSize; i++) {
          legends[i] = "" + (i + 1);
        }
      } else if (plotLegends.isList()) {
        IAST list = (IAST) plotLegends;
        for (int i = 1; i < list.size(); i++) {
          legends[i - 1] = list.get(i).toString();
        }
      } else if (argSize == 1) {
        legends[0] = plotLegends.toString();
      }
    }
    return legends;
  }

  private static String createLegends(GraphicsOptions graphicsOptions) {

    IExpr plotLegends = graphicsOptions.plotLegends();
    if (plotLegends != S.None) {
      if (plotLegends.isList() && plotLegends.argSize() > 0) {
        StringBuilder legends = new StringBuilder();
        IAST list = (IAST) plotLegends;
        for (int i = 1; i < list.size(); i++) {
          legends.append("'");
          legends.append(list.get(i).toString());
          legends.append("'");
          if (i < list.argSize()) {
            legends.append(", ");
          }
        }
        return legends.toString();
      }
    }
    return "";
  }

  public static void setGraphicOptions(GraphicsOptions graphicsOptions, IAST plot, int startIndex,
      IExpr[] options, EvalEngine engine) {
    final OptionArgs optionArgs = new OptionArgs(plot.topHead(), plot, startIndex, engine, true);
    if (!options[X_AXES].isFalse()) {
      graphicsOptions.setAxes(options[X_AXES]);
    }
    if (options[X_AXESLABEL] != S.None) {
      graphicsOptions.setAxesLabel(options[X_AXESLABEL]);
    }

    if (options[X_JOINED].isTrue()) {
      graphicsOptions.setJoined(true);
    }
    if (options[X_PLOTLEGENDS] != S.None) {
      graphicsOptions.setPlotLegends(options[X_PLOTLEGENDS]);
    }
    if (options[X_PLOTLABEL] != S.None) {
      graphicsOptions.setPlotLabel(options[X_PLOTLABEL]);
    }
    graphicsOptions.setOptions(optionArgs);
    graphicsOptions.setScalingFunctions(options);
  }

  public static void xAxisCategory(StringBuilder xAxisString, IAST pointList) {
    for (int i = 1; i < pointList.size(); i++) {
      xAxisString.append("'");
      xAxisString.append(i);
      xAxisString.append("'");
      if (i < pointList.size() - 1) {
        xAxisString.append(", ");
      }
    }
  }

  public static void xyAxesPoint2D(IAST pointList2D, StringBuilder xAxisBuffer,
      StringBuilder yAxisBuffer, GraphicsOptions graphicsOptions) {
    String type = graphicsOptions.isJoined() ? ECharts.TYPE_LINE : ECharts.TYPE_SCATTER;
    xyAxesPoint2D(pointList2D, xAxisBuffer, yAxisBuffer, graphicsOptions, type, "");
  }

  public static void xyAxesPoint2D(IAST pointList2D, StringBuilder xAxisBuffer,
      StringBuilder yAxisBuffer, GraphicsOptions graphicsOptions, String type, String step) {

    String[] legends = createLegends(graphicsOptions, 1);
    yAxisBuffer.append( //
        "{\n" //
            + " name: '" + legends[0] + "',\n" //
            + " showSymbol: false,\n" //
            + " type: '" + type + "',\n");
    if (step != null && step.length() > 0) {
      // step must contain a string with length greater than 0
      yAxisBuffer.append(" step: '" + step + "',\n");
    }
    JavaScriptFormFactory toJS =
        new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);
    StringBuilder values = new StringBuilder();
    values.append(" data: [");
    // x-axis categories
    // StringBuilder xAxisString = new StringBuilder();
    for (int i = 1; i < pointList2D.size(); i++) {
      IAST list2 = (IAST) pointList2D.get(i);
      IAST currentPointY = getPoint2D(list2);

      IExpr xValue = currentPointY.first();
      if (isNonReal(xValue)) {
        xAxisBuffer.append("''");
        values.append("''");
      } else {
        xAxisBuffer.append("'");
        toJS.convertExpr(xAxisBuffer, xValue);
        xAxisBuffer.append("'");

        IExpr yValue = currentPointY.second();
        if (isNonReal(yValue)) {
          values.append("''");
        } else {
          toJS.convertExpr(values, yValue);
        }
      }
      if (i < pointList2D.size() - 1) {
        values.append(", ");
        xAxisBuffer.append(", ");
      }

    }
    values.append("]\n");
    yAxisBuffer.append(values);
    yAxisBuffer.append("}\n");
  }

  public static void yAxisSingleSeries(StringBuilder yAxisBuffer, IAST pointList,
      GraphicsOptions graphicsOptions, double[] minMax) {
    String[] legends = createLegends(graphicsOptions, 1);
    String type = graphicsOptions.isJoined() ? ECharts.TYPE_LINE : ECharts.TYPE_SCATTER;
    yAxisSingleSeries(yAxisBuffer, pointList, legends[0], type, "", minMax);
  }

  public static void yAxisSingleSeries(StringBuilder yAxisBuffer, IAST pointList,
      GraphicsOptions graphicsOptions, String type, String step, double[] minMax) {
    String[] legends = createLegends(graphicsOptions, 1);
    yAxisSingleSeries(yAxisBuffer, pointList, legends[0], type, step, minMax);
  }

  public static void yAxisSingleSeries(StringBuilder yAxisBuffer, IAST pointList, String name,
      String type, String step, double[] minMax) {
    yAxisBuffer.append( //
        "{\n" //
            + " name: '" + name + "',\n" //
            + " showSymbol: false,\n" //
            + " type: '" + type + "',\n");
    if (step != null && step.length() > 0) {
      // step must contain a string with length greater than 0
      yAxisBuffer.append(" step: '" + step + "',\n");
    }
    JavaScriptFormFactory toJS =
        new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);
    StringBuilder values = new StringBuilder();
    values.append(" data: [");
    yAxisSingleSeriesRecursive(values, pointList, toJS, minMax);
    values.append("]\n");
    yAxisBuffer.append(values);
    yAxisBuffer.append("}\n");
  }


  private static void yAxisSingleSeriesRecursive(StringBuilder values, IAST pointList,
      JavaScriptFormFactory toJS, double[] minMax) {
    if (pointList.isListOfPoints(2)) {
      for (int i = 1; i < pointList.size(); i++) {
        IExpr arg = pointList.get(i);
        IExpr xExpr = arg.first();
        IExpr yExpr = getPointY(arg.second());
        if (isNonReal(xExpr) || isNonReal(yExpr)) {
          values.append("''");
        } else {
          try {
            double xValue = xExpr.evalf();
            if (xValue > minMax[1]) {
              minMax[1] = xValue;
            }
            if (xValue < minMax[0]) {
              minMax[0] = xValue;
            }
            double yValue = yExpr.evalf();
            if (yValue > minMax[3]) {
              minMax[3] = yValue;
            }
            if (yValue < minMax[2]) {
              minMax[2] = yValue;
            }
          } catch (ArgumentTypeException e) {
            // yValue isn't real ?
          }
          toJS.convertExpr(values, arg);
        }
        if (i < pointList.size() - 1) {
          values.append(", ");
        }
      }
    } else {
      if (pointList.isListOfLists()) {
        for (int i = 1; i < pointList.size(); i++) {
          yAxisSingleSeriesRecursive(values, (IAST) pointList.get(i), toJS, minMax);
        }

      } else {
        // assume a list of values
        for (int i = 1; i < pointList.size(); i++) {
          IExpr yExpr = getPointY(pointList.get(i));
          if (isNonReal(yExpr)) {
            values.append("''");
          } else {
            try {
              double xValue = i;
              if (xValue > minMax[1]) {
                minMax[1] = xValue;
              } else if (xValue < minMax[0]) {
                minMax[0] = xValue;
              }
              double yValue = yExpr.evalf();
              if (yValue > minMax[3]) {
                minMax[3] = yValue;
              } else if (yValue < minMax[2]) {
                minMax[2] = yValue;
              }
            } catch (ArgumentTypeException e) {
              // yValue isn't real ?
            }
            toJS.convertExpr(values, yExpr);
          }
          if (i < pointList.size() - 1) {
            values.append(", ");
          }
        }
      }
    }
  }

  String jsonStr = JSON_TEMPLATE;

  final GraphicsOptions graphicsOptions;

  public ECharts(GraphicsOptions graphicsOptions) {
    this.graphicsOptions = graphicsOptions;
  }

  public ECharts(GraphicsOptions graphicsOptions, String plotLabel) {
    this.graphicsOptions = graphicsOptions;
    setTitle(plotLabel);
  }

  public String getJSONStr() {
    return jsonStr;
  }

  public String setLegend(String legend) {
    jsonStr = jsonStr.replace("`legend`", "data: [" + legend + "]");
    return jsonStr;
  }

  public String setSeriesValues(String ydata) {
    jsonStr = jsonStr.replace("`series`", ydata);
    return jsonStr;
  }

  public String setTitle(String title) {
    jsonStr = jsonStr.replace("`title`", title);
    return jsonStr;
  }

  /**
   * Set x-axis to type &quot;category&quot;.
   * 
   * @return
   */
  public String setXAxis() {
    return setXAxis("category");
  }

  /**
   * Set x-axis to the specified type.
   * 
   * @param type
   * @return
   */
  public String setXAxis(String type) {
    jsonStr = jsonStr.replace("`xaxis`", //
        "type: '" + type + "',\n" //
            + "    show: " + isXAxis() + ",\n" // show x-axis
            + "    name: '" + xAxisLabel() + "',");
    return jsonStr;
  }

  public String setXAxisPlot() {
    jsonStr = jsonStr.replace("`xaxis`", //
        "name: '" + xAxisLabel() + "', \n" //
            + "    minorTick: { \n" //
            + "      show: true\n" //
            + "    },\n" //
            + "    minorSplitLine: { \n" //
            + "      show: true\n" //
            + "    }"); //
    return jsonStr;
  }


  private boolean isXAxis() {
    IExpr axes = graphicsOptions.axes();
    if (axes == S.True) {
      return true;
    }
    if (axes == S.False) {
      return false;
    }
    if (axes.isList() && axes.argSize() > 0) {
      return axes.first().isTrue();
    }
    return false;
  }

  private String xAxisLabel() {
    IExpr axesLabel = graphicsOptions.axesLabel();
    if (axesLabel == S.Automatic) {
      // TODO
      return "";
    }
    if (axesLabel == S.None) {
      return "";
    }
    if (axesLabel.isList() && axesLabel.argSize() > 0) {
      IExpr labelX = axesLabel.first();
      if (labelX == S.Automatic) {
        // TODO
        return "";
      }
      if (labelX == S.None) {
        return "";
      }
      return labelX.toString();
    }
    return axesLabel.toString();
  }

  public String setXData(String xdata) {
    if (xdata == null) {
      jsonStr = jsonStr.replace("`xdata`", "");
      return jsonStr;
    }
    jsonStr = jsonStr.replace("`xdata`", "data: [" + xdata + "]");
    return jsonStr;
  }

  public String setYAxis() {
    return setYAxis("value");
  }

  public String setYAxis(String type) {
    // + " type: 'log',\n" //
    // + " name: 'y'" //
    jsonStr = jsonStr.replace(//
        "`yaxis`", //
        "type: '" + type + "',\n" //
            + "    show: " + isYAxis() + ",\n" // show y-axis
            + "    name: '" + yAxisLabel() + "'");
    return jsonStr;
  }

  public String setYAxisPlot(double yMin, double yMax) {
    if (!Double.isFinite(yMin)) {
      yMin = 0.0;
    }
    if (!Double.isFinite(yMax)) {
      yMax = 100.0;
    }
    if (yMin > yMax) {
      yMax = yMin + 1;
    }


    yMin = Math.floor(yMin);
    yMax = Math.ceil(yMax);
    double diff = yMax - yMin;
    if (yMin > 0 && diff < 50 && diff >= 0.0) {
      yMin = 0.0;
    } else if (yMin < 0 && diff > -50 && diff <= 0.0) {
      yMin = 0.0;
    }

    jsonStr = jsonStr.replace("`yaxis`", //
        "name: '" + yAxisLabel() + "', \n" //
            + "    min: " + yMin + ",\n" //
            + "    max: " + yMax + ",\n" //
            + "    minorTick: {\n" //
            + "      show: true\n" //
            + "    },\n" //
            + "    minorSplitLine: {\n" //
            + "      show: true\n" //
            + "    }"); //
    return jsonStr;
  }

  private boolean isYAxis() {
    IExpr axes = graphicsOptions.axes();
    if (axes == S.True) {
      return true;
    }
    if (axes == S.False) {
      return false;
    }
    if (axes.isList() && axes.argSize() > 1) {
      return axes.second().isTrue();
    }
    return false;
  }

  private String yAxisLabel() {
    IExpr axesLabel = graphicsOptions.axesLabel();
    if (axesLabel == S.Automatic) {
      // TODO
      return "";
    }
    if (axesLabel == S.None) {
      return "";
    }
    if (axesLabel.isList() && axesLabel.argSize() > 1) {
      IExpr labelY = axesLabel.second();
      if (labelY == S.Automatic) {
        // TODO
        return "";
      }
      if (labelY == S.None) {
        return "";
      }
      return labelY.toString();
    }
    return "";
  }
}
