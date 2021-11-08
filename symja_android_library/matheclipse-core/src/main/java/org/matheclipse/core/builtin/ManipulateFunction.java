package org.matheclipse.core.builtin;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.complex.Complex;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.stat.StatUtils;
import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.Dimensions2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Layout.LayoutBuilder;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;
import tech.tablesaw.plotly.traces.HeatmapTrace;
import tech.tablesaw.plotly.traces.Histogram2DTrace;
import tech.tablesaw.plotly.traces.Histogram2DTrace.Histogram2DBuilder;
import tech.tablesaw.plotly.traces.HistogramTrace;
import tech.tablesaw.plotly.traces.PieTrace;

public class ManipulateFunction {

  private static final Logger LOGGER = LogManager.getLogger();

  public static boolean AUTOSIZE = true;

  public static int WIDTH = 600;

  public static int HEIGHT = 400;

  private static final int N = 100;

  /** Default plot style colors for functions */
  private static final RGBColor[] PLOT_COLORS =
      new RGBColor[] { //
        new RGBColor(0.368417f, 0.506779f, 0.709798f), //
        new RGBColor(0.880722f, 0.611041f, 0.142051f), //
        new RGBColor(0.560181f, 0.691569f, 0.194885f), //
        new RGBColor(0.922526f, 0.385626f, 0.209179f), //
        new RGBColor(0.528488f, 0.470624f, 0.701351f), //
        new RGBColor(0.772079f, 0.431554f, 0.102387f), //
        new RGBColor(0.363898f, 0.618501f, 0.782349f), //
        new RGBColor(1.0f, 0.75f, 0.0f), //
        new RGBColor(0.647624f, 0.37816f, 0.614037f), //
        new RGBColor(0.571589f, 0.586483f, 0.0f), //
        new RGBColor(0.915f, 0.3325f, 0.2125f), //
        new RGBColor(0.40082222609352647f, 0.5220066643438841f, 0.85f), //
        new RGBColor(0.9728288904374106f, 0.621644452187053f, 0.07336199581899142f), //
        new RGBColor(0.736782672705901f, 0.358f, 0.5030266573755369f), //
        new RGBColor(0.28026441037696703f, 0.715f, 0.4292089322474965f) //
      };

  private static final String JSXGRAPH = //
      "`1`\n" + //
          "`2`" + //
          "\n" + //
          "`3`" + //
          "\n" + //
          "";

  private static final String MATHCELL = //
      "var parent = document.currentScript.parentNode;\n"
          + "var id = generateId();\n"
          + "parent.id = id;\n"
          + "MathCell( id, [ `1` ] );\n"
          + //
          "\n"
          + //
          "parent.update = function( id ) {\n"
          + "\n"
          + "`2`"
          + "\n"
          + "`3`"
          + "\n"
          + "`4`"
          + "\n"
          + "}\n"
          + "parent.update( id );\n";

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (Config.USE_MANIPULATE_JS) {
        S.BarChart.setEvaluator(new BarChart());
        S.BoxWhiskerChart.setEvaluator(new BoxWhiskerChart());
        S.ComplexPlot3D.setEvaluator(new ComplexPlot3D());
        S.ContourPlot.setEvaluator(new ContourPlot());
        S.DensityPlot.setEvaluator(new DensityPlot());
        S.DensityHistogram.setEvaluator(new DensityHistogram());
        S.Histogram.setEvaluator(new Histogram());
        S.PieChart.setEvaluator(new PieChart());
        S.Manipulate.setEvaluator(new Manipulate());
        S.MatrixPlot.setEvaluator(new MatrixPlot());
      }
    }
  }

  /**
   * Chart methods which use <a href="https://github.com/paulmasson/mathcell">Mathcell
   * Javascript</a>
   */
  static final class Mathcell {

    private static IExpr plot(IAST plot, final IAST manipulateAST, EvalEngine engine) {
      // final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);
      JavaScriptFormFactory toJS =
          new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);
      String js = ManipulateFunction.MATHCELL;
      js = slidersFromList(manipulateAST, js, toJS);
      if (js == null) {
        return F.NIL;
      }

      if (plot.size() < 2) {
        return F.NIL;
      }
      IExpr arg1 = plot.arg1();
      if (!arg1.isList()) {
        arg1 = engine.evaluate(arg1);
      }
      if (arg1.isList()) {
        IAST pointList = (IAST) arg1;
        int[] dimension = pointList.isMatrix(false);
        if (dimension != null) {
          if (dimension[1] == 2) {
            StringBuilder function = new StringBuilder();

            if (manipulateAST.arg1().isAST(S.ListLinePlot)) {
              function.append("var data = [ listPlot( [\n");
              for (int i = 1; i < pointList.size(); i++) {
                IAST rowList = (IAST) pointList.get(i);
                function.append("[ ");
                toJS.convert(function, rowList.arg1());
                function.append(",");
                toJS.convert(function, rowList.arg2());
                function.append("] ");
                if (i < pointList.size() - 1) {
                  function.append(",");
                }
                function.append("\n");
              }
              function.append("], { })];");
            } else {
              function.append("var data = [\n");
              // point( [ x, y, z ],
              // { color: 'hsl(' + 360*Math.random() + ',100%,50%)', size: 5 } )
              for (int i = 1; i < pointList.size(); i++) {
                IAST rowList = (IAST) pointList.get(i);
                function.append("point( [ ");
                toJS.convert(function, rowList.arg1());
                function.append(",");
                toJS.convert(function, rowList.arg2());
                function.append("], ");
                function.append(" {size: 2 } )");
                if (i < pointList.size() - 1) {
                  function.append(",");
                }
                function.append("\n");
              }
              function.append("];");
            }

            js = js.replace("`3`", function.toString());

            StringBuilder graphicControl = new StringBuilder();

            // var config = dim === 'two' ? { type: 'svg', ticks: false }
            // : { type: 'threejs', axesLabels: false };
            graphicControl.append("var config = { type: 'svg' };\n");
            graphicControl.append("evaluate( id, data, config );\n");
            js = js.replace("`4`", graphicControl.toString());

            return F.JSFormData(js, "mathcell");
          }
          if (dimension[1] == 3) {
            StringBuilder function = new StringBuilder();

            function.append("var data = [\n");
            // point( [ x, y, z ],
            // { color: 'hsl(' + 360*Math.random() + ',100%,50%)', size: 5 } )
            for (int i = 1; i < pointList.size(); i++) {
              IAST rowList = (IAST) pointList.get(i);
              function.append("point( [ ");
              toJS.convert(function, rowList.arg1());
              function.append(",");
              toJS.convert(function, rowList.arg2());
              function.append(",");
              toJS.convert(function, rowList.arg3());
              function.append("], ");
              function.append(" {size: 2 } )");
              if (i < pointList.size() - 1) {
                function.append(",");
              }
              function.append("\n");
            }
            function.append("];");

            js = js.replace("`3`", function.toString());

            StringBuilder graphicControl = new StringBuilder();

            // var config = dim === 'two' ? { type: 'svg', ticks: false }
            // : { type: 'threejs', axesLabels: false };
            graphicControl.append("var config = { type: 'threejs' };\n");
            graphicControl.append("evaluate( id, data, config );\n");
            js = js.replace("`4`", graphicControl.toString());

            return F.JSFormData(js, "mathcell");
          }
          return F.NIL;
        } else {
          StringBuilder function = new StringBuilder();
          if (manipulateAST.arg1().isAST(S.ListLinePlot)) {
            function.append("var data = [ listPlot( [\n");
            for (int i = 1; i < pointList.size(); i++) {
              function.append("[ ");
              function.append(i);
              function.append(",");
              toJS.convert(function, pointList.get(i));
              function.append("] ");
              if (i < pointList.size() - 1) {
                function.append(",");
              }
              function.append("\n");
            }
            function.append("], { })];");
          } else {
            function.append("var data = [\n");
            // point( [ 2*Math.random() - 1, 2*Math.random() - 1, 2*Math.random() - 1 ],
            // { color: 'hsl(' + 360*Math.random() + ',100%,50%)', size: 5 } )
            for (int i = 1; i < pointList.size(); i++) {
              function.append("point( [ ");
              function.append(i);
              function.append(",");
              toJS.convert(function, pointList.get(i));
              function.append("], ");
              function.append(" {size: 2 } )");
              if (i < pointList.size() - 1) {
                function.append(",");
              }
              function.append("\n");
            }
            function.append("];");
          }

          js = js.replace("`3`", function.toString());

          StringBuilder graphicControl = new StringBuilder();

          // var config = dim === 'two' ? { type: 'svg', ticks: false }
          // : { type: 'threejs', axesLabels: false };
          graphicControl.append("var config = { type: 'svg' };\n");
          graphicControl.append("evaluate( id, data, config );\n");
          js = js.replace("`4`", graphicControl.toString());

          return F.JSFormData(js, "mathcell");
        }
      }
      return F.NIL;
    }

    /**
     * Convert the <code>plot</code> function into a JavaScript mathcell graphic control. See: <a
     * href="https://github.com/paulmasson/mathcell">github mathcell project</a>
     *
     * @param plot
     * @param plotRangeX
     * @param plotRangeY
     * @param manipulateAST
     * @return
     * @throws IOException
     */
    private static IExpr sliderWithPlot(
        IAST plot, IAST plotRangeX, IAST plotRangeY, final IAST manipulateAST, EvalEngine engine) {
      JavaScriptFormFactory toJS =
          new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);

      int plotID = plot.headID();
      String colorMap = "hot";
      final OptionArgs options;
      if (plotID == ID.Plot3D
          || plotID == ID.ComplexPlot3D
          || plotID == ID.ContourPlot
          || plotID == ID.DensityPlot) {
        if (plotID == ID.ComplexPlot3D) {
          options = new OptionArgs(plot.topHead(), plot, 3, engine);
          if (plot.size() > 3 && options.isInvalidPosition(plot, 2)) {
            return F.NIL;
          }
        } else {
          options = new OptionArgs(plot.topHead(), plot, 4, engine);
          if (plot.size() > 4 && options.isInvalidPosition(plot, 3)) {
            return F.NIL;
          }
        }
        IExpr colorFunction = options.getOption(S.ColorFunction);
        if (colorFunction == S.Automatic) {
        } else if (colorFunction.isString()) {
          String newColorMap = colorFunction.toString();
          if (newColorMap.equals("CherryTones")) {
            colorMap = "cherry";
          } else if (newColorMap.equals("Rainbow")) {
            colorMap = "rainbow2";
          } else if (newColorMap.equals("RustTones")) {
            colorMap = "rust";
          } else if (newColorMap.equals("SunsetColors")) {
            colorMap = "sunset";
          } else if (newColorMap.equals("TemperatureMap")) {
            colorMap = "temperature";
          } else if (newColorMap.equals("ThermometerColors")) {
            colorMap = "thermometer";
          } else if (newColorMap.equals("WatermelonColors")) {
            colorMap = "watermelon";
          } else {
            // `2` is not a known entity, class, or tag for `1`.
            IOFunctions.printMessage(
                S.ColorData, "notent", F.List(S.ColorData, colorFunction), engine);
          }
        } else if (colorFunction.isPresent()) {
          // `2` is not a known entity, class, or tag for `1`.
          IOFunctions.printMessage(
              S.ColorData, "notent", F.List(S.ColorData, colorFunction), engine);
        }
      } else {
        options = new OptionArgs(plot.topHead(), plot, 3, engine);
      }
      IExpr plotRange = options.getOption(S.PlotRange);
      IAST optionPlotRange = F.NIL;
      if (plotRange.isPresent()) {
        if (plotRange.isAST(S.List, 3)) {
          optionPlotRange = F.List(S.Full, F.List(plotRange.first(), plotRange.second()));
        } else if (plotRange.isReal()) {
          if (plotID == ID.Plot) {
            optionPlotRange = F.List(S.Full, F.List(plotRange.negate(), plotRange));
          } else if (plotID == ID.ListPlot || plotID == ID.ListLinePlot) {
            optionPlotRange = F.List(S.Full, F.List(F.C0, plotRange));
          } else if ((plotID == ID.PolarPlot) || (plotID == ID.ParametricPlot)) {
            optionPlotRange =
                F.List(
                    F.List(plotRange.negate(), plotRange), //
                    F.List(plotRange.negate(), plotRange));
          }
        }
        if (!optionPlotRange.isPresent()) {
          // Value of option `1` is not All, Full, Automatic, a positive machine
          // number, or an appropriate list of range specifications.
          IOFunctions.printMessage(
              plot.topHead(), "prng", F.List(F.Rule(S.PlotRange, plotRange)), engine);
        }
      }

      String js = ManipulateFunction.MATHCELL;
      js = Mathcell.slidersFromList(manipulateAST, js, toJS);
      if (js == null) {
        return F.NIL;
      }

      ISymbol plotSymbolX = (ISymbol) plotRangeX.arg1();

      // function z1(x,y) { return [ x, y, Math.sin( a * x * y ) ]; }
      StringBuilder function = new StringBuilder();
      IAST listOfFunctions;
      IExpr plotFunction = engine.evaluate(plot.arg1());
      if (plotFunction.isList()) {
        listOfFunctions = (IAST) plotFunction;
      } else {
        listOfFunctions = F.unaryAST1(S.List, plotFunction);
      }
      if (plotID == ID.Plot3D || plotID == ID.ContourPlot || plotID == ID.DensityPlot) {
        if (!plotRangeY.isPresent()) {
          return F.NIL;
        }
        for (int i = 1; i < listOfFunctions.size(); i++) {
          function.append("function z" + i + "(");
          ISymbol plotSymbolY = (ISymbol) plotRangeY.arg1();
          toJS.convert(function, plotSymbolX);
          function.append(",");
          toJS.convert(function, plotSymbolY);
          function.append(") { return [ ");
          toJS.convert(function, plotSymbolX);
          function.append(", ");
          toJS.convert(function, plotSymbolY);
          function.append(", ");
          toJS.convert(function, listOfFunctions.get(i));
          function.append(" ]; }\n");
        }
      } else if (manipulateAST.arg1().isAST(S.ComplexPlot3D)) {
        for (int i = 1; i < listOfFunctions.size(); i++) {
          function.append("function z" + i + "(");
          toJS.convert(function, plotSymbolX);
          function.append(") { try { return  ");
          // toJS.convert(function, plotSymbolX);
          // function.append(", ");
          // toJS.convert(function, plotSymbolY);
          toJS.convert(function, listOfFunctions.get(i));
          function.append(";}catch(e){return complex(Number.NaN);} }\n");
        }
      } else {
        for (int i = 1; i < listOfFunctions.size(); i++) {
          function.append("function z");
          function.append(i);
          function.append("(");
          toJS.convert(function, plotSymbolX);
          function.append(") { try { return ");
          toJS.convert(function, listOfFunctions.get(i));
          function.append(";}catch(e){return complex(Number.NaN);} }\n");
        }
      }
      js = js.replace("`3`", function.toString());

      // plot( x => (Math.sin(x*(1+a*x))), [0, 2*Math.PI], { } )
      StringBuilder graphicControl = new StringBuilder();

      if (plotID == ID.ContourPlot || plotID == ID.DensityPlot) {
        if (!plotRangeY.isPresent()) {
          return F.NIL;
        }
        contourPlot(listOfFunctions, plotRangeX, plotRangeY, graphicControl, plotID, toJS);
      } else if (plotID == ID.Plot3D) {
        if (!plotRangeY.isPresent()) {
          return F.NIL;
        }
        plot3D(listOfFunctions, plotRangeX, plotRangeY, graphicControl, colorMap, toJS);
      } else if (manipulateAST.arg1().isAST(S.ComplexPlot3D)) {
        if (plotRangeY.isPresent()) {
          return F.NIL;
        }
        complexPlot3D(listOfFunctions, plotRangeX, graphicControl, optionPlotRange, toJS);
      } else {
        if (plotID == ID.ParametricPlot || plotID == ID.PolarPlot) {
          parametricPlot(listOfFunctions, plotRangeX, plotSymbolX, graphicControl, toJS);
        } else {
          for (int i = 1; i < listOfFunctions.size(); i++) {
            graphicControl.append("var p" + i + " = plot( z" + i + ", ");
            ManipulateFunction.realRange(graphicControl, plotRangeX, -1, toJS);

            // each function gets it's own color (hue, saturation, lightness)
            graphicControl.append(", { color: 'hsl(");
            graphicControl.append(72 * (i - 1));
            graphicControl.append(",100%,50%)' }");

            graphicControl.append(" );\n");
          }

          // var data = [ p1, p2 ];
          // if (plot.arg1().isList()) {
          // listOfFunctions = (IAST) plot.arg1();
          graphicControl.append("var data = [ ");
          for (int i = 1; i < listOfFunctions.size(); i++) {
            graphicControl.append("p");
            graphicControl.append(i);
            if (i < listOfFunctions.size() - 1) {
              graphicControl.append(", ");
            }
          }
          graphicControl.append(" ];\n");
        }
        graphicControl.append("var config = { type: 'svg' ");
        if (optionPlotRange.isPresent()) {
          // IExpr option = optionPlotRange.arg2();
          // if (option.isAST(S.List, 3)) {
          // plotRangeY = F.List(option.first(), option.second());
          // }
        }
        if (optionPlotRange.isPresent() && optionPlotRange.second().isAST(S.List, 3)) {
          IAST list = (IAST) optionPlotRange.second();
          // var config = { type: 'svg', yMin: -5, yMax: 5 };
          graphicControl.append(", yMin: ");
          toJS.convert(graphicControl, list.arg1());
          graphicControl.append(", yMax: ");
          toJS.convert(graphicControl, list.arg2());
        }
        graphicControl.append(" };\n");
      }

      graphicControl.append("evaluate( id, data, config );\n");
      js = js.replace("`4`", graphicControl.toString());

      return F.JSFormData(js, "mathcell");
    }

    public static void contourPlot(
        IAST listOfFunctions,
        IAST plotRangeX,
        IAST plotRangeY,
        StringBuilder graphicControl,
        int plotID,
        JavaScriptFormFactory toJS) {
      for (int i = 1; i < listOfFunctions.size(); i++) {
        graphicControl.append("var p" + i + " = ");
        if (plotID == ID.DensityPlot) {
          graphicControl.append("isoline( z" + i + ", ");
        } else {
          graphicControl.append("isoband( z" + i + ", ");
        }
        ManipulateFunction.realRange(graphicControl, plotRangeX, -1, toJS);
        graphicControl.append(", ");
        ManipulateFunction.realRange(graphicControl, plotRangeY, -1, toJS);
        graphicControl.append(" );\n");
        // graphicControl.append(", { colormap: '");
        // graphicControl.append(colorMap);
        // graphicControl.append("' } );\n");
      }
      graphicControl.append("\n  var config = { type: 'threejs' };\n");
      graphicControl.append("  var data = [");
      for (int i = 1; i < listOfFunctions.size(); i++) {
        graphicControl.append("p" + i);
        if (i < listOfFunctions.size() - 1) {
          graphicControl.append(",");
        }
      }
      graphicControl.append("];\n");
    }

    public static void plot3D(
        IAST listOfFunctions,
        IAST plotRangeX,
        IAST plotRangeY,
        StringBuilder graphicControl,
        String colorMap,
        JavaScriptFormFactory toJS) {
      for (int i = 1; i < listOfFunctions.size(); i++) {
        graphicControl.append("var p" + i + " = ");
        graphicControl.append("parametric( z" + i + ", ");
        ManipulateFunction.realRange(graphicControl, plotRangeX, -1, toJS);
        graphicControl.append(", ");
        ManipulateFunction.realRange(graphicControl, plotRangeY, -1, toJS);
        graphicControl.append(", { colormap: '");
        graphicControl.append(colorMap);
        graphicControl.append("' } );\n");
      }
      graphicControl.append("\n  var config = { type: 'threejs' };\n");
      graphicControl.append("  var data = [");
      for (int i = 1; i < listOfFunctions.size(); i++) {
        graphicControl.append("p" + i);
        if (i < listOfFunctions.size() - 1) {
          graphicControl.append(",");
        }
      }
      graphicControl.append("];\n");
    }

    public static void parametricPlot(
        IAST listOfFunctions,
        IAST plotRangeX,
        ISymbol plotSymbolX,
        StringBuilder graphicControl,
        JavaScriptFormFactory toJS) {
      graphicControl.append("var data = [ parametric( ");
      toJS.convert(graphicControl, plotSymbolX);
      graphicControl.append(" => [");
      for (int i = 1; i < listOfFunctions.size(); i++) {
        graphicControl.append("z" + i + "(");
        toJS.convert(graphicControl, plotSymbolX);
        graphicControl.append(")");
        if (i < listOfFunctions.size() - 1) {
          graphicControl.append(",");
        }
      }
      graphicControl.append("], ");
      ManipulateFunction.realRange(graphicControl, plotRangeX, 1500, toJS);
      graphicControl.append(" )];\n");
    }

    public static void complexPlot3D(
        IAST listOfFunctions,
        IAST plotRangeX,
        StringBuilder graphicControl,
        IAST optionPlotRange,
        JavaScriptFormFactory toJS) {
      double[] rangeXY = null;
      for (int i = 1; i < listOfFunctions.size(); i++) {
        graphicControl.append("var p" + i + " = ");
        graphicControl.append("parametric( (re,im) => [ re, im, z" + i + "(complex(re,im)) ]");

        rangeXY = ManipulateFunction.complexRange(graphicControl, plotRangeX, -1, toJS);
        graphicControl.append(", { complexFunction: 'abs', colormap: 'complexArgument");
        graphicControl.append("' } );\n");
      }
      graphicControl.append("\n  var config = { type: 'threejs',");
      if (rangeXY != null) {
        setBoxRatios(graphicControl, rangeXY);
      }

      if (optionPlotRange.isPresent() && optionPlotRange.second().isAST(S.List, 3)) {
        IAST list = (IAST) optionPlotRange.second();
        // var config = { type: 'svg', yMin: -5, yMax: 5 };
        graphicControl.append(", zMin: ");
        toJS.convert(graphicControl, list.arg1());
        graphicControl.append(", zMax: ");
        toJS.convert(graphicControl, list.arg2());
      }
      graphicControl.append(" };\n");
      graphicControl.append("  var data = [");
      for (int i = 1; i < listOfFunctions.size(); i++) {
        graphicControl.append("p" + i);
        if (i < listOfFunctions.size() - 1) {
          graphicControl.append(",");
        }
      }
      graphicControl.append("];\n");
    }

    /**
     * Set mathcell's <code>aspectRatio</code> parameter to get a "x/y square plot".
     *
     * @param graphicControl
     * @param rangeXY
     */
    private static void setBoxRatios(StringBuilder graphicControl, double[] rangeXY) {
      graphicControl.append(" aspectRatio: [");
      if (rangeXY[0] > rangeXY[1]) {
        double aspectRatio = rangeXY[0] / rangeXY[1];
        graphicControl.append("1,");
        graphicControl.append(Double.toString(aspectRatio));
        graphicControl.append(",1]");
      } else {
        double aspectRatio = rangeXY[1] / rangeXY[0];
        graphicControl.append(Double.toString(aspectRatio));
        graphicControl.append(",1,1]");
      }
    }

    /**
     * Evaluate <code>Table( &lt;formula&gt;, &lt;sliderRange&gt; )</code>. If the result is a list,
     * then convert this list in a JavaScript list of LaTeX formulas, which could be rendered with
     * MathJAX. See: <a href="https://github.com/paulmasson/mathcell/issues/1">github mathcell
     * #1</a>
     *
     * @param formula the formula which should be evaluated into a table
     * @param sliderRange
     * @param engine
     * @return
     * @throws IOException
     */
    private static IExpr sliderWithFormulas(IExpr formula, IAST sliderRange, EvalEngine engine) {
      JavaScriptFormFactory toJS =
          new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);
      IASTAppendable newsliderRange = sliderRange.copyAppendable();
      double stepValue = 1.0;
      double minValue = sliderRange.arg2().evalDouble();
      double maxValue = sliderRange.arg3().evalDouble();
      if (sliderRange.size() == 5) {
        stepValue = sliderRange.arg4().evalDouble();
      } else {
        stepValue = (maxValue - minValue) / 100.0;
        newsliderRange.append(stepValue);
      }
      IExpr list = engine.evaluate(F.Table(formula, newsliderRange));
      if (list.isNonEmptyList()) {
        IAST listOfFormulas = (IAST) list;
        String sliderSymbol = toJS.toString(newsliderRange.arg1());
        String min = Double.toString(minValue);
        String max = Double.toString(maxValue);
        String step = Double.toString(stepValue);
        String js = ManipulateFunction.MATHCELL;
        // { type: 'slider', min: 1, max: 5, step: 1, name: 'n', label: 'n' }
        StringBuilder slider = new StringBuilder();
        slider.append("{ type: 'slider', min: ");
        slider.append(min);
        slider.append(", max: ");
        slider.append(max);
        slider.append(", step: ");
        slider.append(step);
        slider.append(", name: '");
        slider.append(sliderSymbol);
        slider.append("', label: '");
        slider.append(sliderSymbol);
        slider.append("' }\n");
        js = StringUtils.replace(js, "`1`", slider.toString());

        StringBuilder variable = new StringBuilder();
        variable.append("var ");
        variable.append(sliderSymbol);

        variable.append(" = getVariable(id, '");
        variable.append(sliderSymbol);
        variable.append("');\n");

        js = StringUtils.replace(js, "`2`", variable.toString());
        js = StringUtils.replace(js, "`3`", "");

        TeXUtilities texUtil = new TeXUtilities(engine, true);
        StringBuilder graphicControl = new StringBuilder();
        graphicControl.append("var expressions = [ ");
        for (int i = 1; i < listOfFormulas.size(); i++) {
          StringWriter stw = new StringWriter();
          texUtil.toTeX(listOfFormulas.get(i), stw);
          graphicControl.append("'");
          String texForm = stw.toString();
          // TODO implement better backslash escaping
          texForm = texForm.replace("\\", "\\\\\\\\");
          graphicControl.append(texForm);
          graphicControl.append("'");
          if (i < listOfFormulas.size() - 1) {
            graphicControl.append(",\n");
          }
        }
        graphicControl.append(" ];\n\n");

        graphicControl.append("  var data = '\\\\\\\\[' + expressions[Math.trunc((");
        graphicControl.append(sliderSymbol);
        graphicControl.append("-");
        graphicControl.append(min);
        graphicControl.append(")/");
        graphicControl.append(step);
        graphicControl.append(")] + '\\\\\\\\]';\n\n");
        graphicControl.append("  data = data.replace( /\\\\\\\\/g, '&#92;' );\n\n");
        graphicControl.append("  var config = {type: 'text', center: true };\n\n");
        graphicControl.append("  evaluate( id, data, config );\n\n");
        graphicControl.append("  MathJax.Hub.Queue( [ 'Typeset', MathJax.Hub, id ] );\n");

        js = js.replace("`4`", graphicControl.toString());

        return F.JSFormData(js, "mathcell");
      }
      return F.NIL;
    }

    static boolean singleSlider(
        final IAST ast,
        int i,
        StringBuilder slider,
        StringBuilder variable,
        JavaScriptFormFactory toJS) {
      IAST sliderRange = (IAST) ast.get(i);
      if (sliderRange.isAST2() && sliderRange.arg2().isList()) {
        // assume arg2 is list of button definitions
        IAST listOfButtons = (IAST) sliderRange.arg2();
        String sliderSymbol;
        String defaultValue = null;
        String label;
        if (sliderRange.arg1().isList()) {
          IAST sliderParameters = (IAST) sliderRange.arg1();
          if (sliderParameters.size() < 4) {
            return false;
          }
          sliderSymbol = toJS.toString(sliderParameters.arg1());
          defaultValue = toJS.toString(sliderRange.arg2());
          label = toJS.toString(sliderParameters.arg3());
        } else {
          sliderSymbol = toJS.toString(sliderRange.arg1());
          label = sliderSymbol;
        }
        if (i > 2) {
          slider.append(", ");
        }
        slider.append("{ type: 'buttons', values: [");
        for (int j = 1; j < listOfButtons.size(); j++) {
          if (listOfButtons.get(j).isFalse() || listOfButtons.get(j).isTrue()) {
            // replace true and false values with 0, 1
            if (listOfButtons.get(j).isFalse()) {
              slider.append("0");
            } else {
              slider.append("1");
            }
          } else {
            slider.append("'");
            toJS.convert(slider, listOfButtons.get(j));
            slider.append("'");
          }
          if (j < listOfButtons.size() - 1) {
            slider.append(",");
          }
        }
        slider.append("]");
        slider.append(", labels: [");
        for (int j = 1; j < listOfButtons.size(); j++) {
          slider.append("'");
          slider.append(listOfButtons.get(j).toString());
          slider.append("'");
          if (j < listOfButtons.size() - 1) {
            slider.append(",");
          }
        }
        slider.append("]");

        if (defaultValue != null) {
          slider.append(", default: ");
          slider.append(defaultValue);
        }
        slider.append(", name: '");
        slider.append(sliderSymbol);
        slider.append("', label: '");
        slider.append(label);
        slider.append("' }\n");

        variable.append("var ");
        variable.append(sliderSymbol);
        // variable.append(" = document.getElementById( id + '");
        variable.append(" = getVariable(id, '");
        variable.append(sliderSymbol);
        // variable.append("' ).value;\n");
        variable.append("');\n");
        return true;
      } else if (sliderRange.isAST3() || sliderRange.size() == 5) {
        IExpr step = null;
        if (sliderRange.size() == 5) {
          step = sliderRange.arg4();
        }
        String sliderSymbol;
        String defaultValue = null;
        String label;
        if (sliderRange.arg1().isList()) {
          IAST sliderParameters = (IAST) sliderRange.arg1();
          if (sliderParameters.size() < 4) {
            return false;
          }
          sliderSymbol = toJS.toString(sliderParameters.arg1());
          defaultValue = toJS.toString(sliderRange.arg2());
          label = toJS.toString(sliderParameters.arg3());
        } else {
          sliderSymbol = toJS.toString(sliderRange.arg1());
          label = sliderSymbol;
        }
        if (i > 2) {
          slider.append(", ");
        }
        slider.append("{ type: 'slider', min: ");
        toJS.convert(slider, sliderRange.arg2());
        slider.append(", max: ");
        toJS.convert(slider, sliderRange.arg3());
        if (step != null) {
          slider.append(", step: ");
          toJS.convert(slider, step);
        }
        if (defaultValue != null) {
          slider.append(", default: ");
          slider.append(defaultValue);
        }
        slider.append(", name: '");
        slider.append(sliderSymbol);
        slider.append("', label: '");
        slider.append(label);
        slider.append("' }\n");

        variable.append("var ");
        variable.append(sliderSymbol);
        // variable.append(" = document.getElementById( id + '");
        variable.append(" = getVariable(id, '");
        variable.append(sliderSymbol);
        // variable.append("' ).value;\n");
        variable.append("');\n");
        return true;
      }
      return false;
    }

    private static String slidersFromList(final IAST ast, String js, JavaScriptFormFactory toJS) {
      if (ast.size() >= 3) {
        if (ast.arg2().isList()) {
          // { type: 'slider', min: 0, max: 2*Math.PI, name: 'phase', label: 'phase' }
          StringBuilder slider = new StringBuilder();
          // var a = document.getElementById( id + 'a' ).value;
          StringBuilder variable = new StringBuilder();
          for (int i = 2; i < ast.size(); i++) {
            if (ast.get(i).isList()) {
              if (!ManipulateFunction.Mathcell.singleSlider(ast, i, slider, variable, toJS)) {
                return null;
              }
            } else {
              break;
            }
          }
          js = StringUtils.replace(js, "`1`", slider.toString());
          js = StringUtils.replace(js, "`2`", variable.toString());
        }
      } else {
        js = StringUtils.replace(js, "`1`", "");
        js = StringUtils.replace(js, "`2`", "");
      }
      return js;
    }
  }

  /**
   * Chart methods which use <a href="https://github.com/jsxgraph/jsxgraph">JSXGraph Javascript</a>
   */
  static final class JSXGraph {

    private static boolean plot(
        IAST plot,
        final IAST manipulateAST,
        JavaScriptFormFactory toJS,
        StringBuilder function,
        double[] boundingbox,
        int[] colour,
        EvalEngine engine) {
      // final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);
      if (plot.size() < 2) {
        return false;
      }

      JSXGraph.sliderNamesFromList(manipulateAST, toJS);
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
        if (pointList.isListOfLists()) {
          int[] dimension = pointList.isMatrix(false);
          if (dimension != null) {
            if (dimension[1] == 2) {
              sequencePointListPlot(
                  manipulateAST, 1, pointList, toJS, function, boundingbox, colour, engine);
              return true; // JSXGraph.boundingBox(manipulateAST, boundingbox, function.toString(),
              // toJS,
              // false, true);
            }
          }
          IAST listOfLists = pointList;
          for (int i = 1; i < listOfLists.size(); i++) {
            pointList = (IAST) listOfLists.get(i);
            dimension = pointList.isMatrix(false);
            if (dimension != null) {
              if (dimension[1] == 2) {
                sequencePointListPlot(
                    manipulateAST, i, pointList, toJS, function, boundingbox, colour, engine);
              } else {
                return false;
              }
            } else {
              sequenceYValuesListPlot(
                  manipulateAST, i, pointList, toJS, function, boundingbox, colour, engine);
            }
          }
        } else {
          sequenceYValuesListPlot(
              manipulateAST, 1, pointList, toJS, function, boundingbox, colour, engine);
        }
        return true; // JSXGraph.boundingBox(manipulateAST, boundingbox, function.toString(),
        // toJS, false,
        // true);
      }
      return false;
    }

    public static IExpr showPlots(final IAST manipulateAST, EvalEngine engine, IAST plots) {
      double[] boundingbox =
          new double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE};
      StringBuilder function = new StringBuilder();
      JavaScriptFormFactory toJS =
          new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);
      int[] colour = new int[] {1};
      for (int i = 1; i < plots.size(); i++) {
        IAST plot = (IAST) plots.get(i);
        if (plot.isAST(S.ListLinePlot) //
            || plot.isAST(S.ListPlot)) {
          if (!JSXGraph.plot(plot, manipulateAST, toJS, function, boundingbox, colour, engine)) {
            return F.NIL;
          }
        } else if (plot.isAST(S.Plot) //
            || plot.isAST(S.ParametricPlot)
            || plot.isAST(S.PolarPlot)) {

          if (plot.size() < 3 || !plot.arg2().isList3() || !plot.arg2().first().isSymbol()) {
            // Range specification `1` is not of the form {x, xmin, xmax}.
            IExpr arg2 = plot.size() >= 3 ? plot.arg2() : F.CEmptyString;
            return IOFunctions.printMessage(plot.topHead(), "pllim", F.List(arg2), engine);
          }
          if (plot.size() >= 3 && plot.arg2().isList()) {
            IAST plotRangeX = (IAST) plot.arg2();
            IAST plotRangeY = F.NIL;
            if (plot.size() >= 4 && plot.arg3().isList()) {
              plotRangeY = (IAST) plot.arg3();
            }
            if (manipulateAST.size() >= 3) {
              if (manipulateAST.arg2().isList()) {
                IAST sliderRange = (IAST) manipulateAST.arg2();
                if (sliderRange.isAST2() && sliderRange.arg2().isList()) {
                  // assumption: buttons should be displayed
                  if (plots.size() == 2) {
                    return Mathcell.sliderWithPlot(
                        plot, plotRangeX, plotRangeY, manipulateAST, engine);
                  }
                  return F.NIL;
                }
              }
            }

            if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
              // return mathcellSliderWithPlot(ast, plot, plotRangeX, plotRangeY, engine);
              if (!JSXGraph.sliderWithPlot(
                  plot, plotRangeX, manipulateAST, toJS, function, boundingbox, colour, engine)) {
                return F.NIL;
              }
            }
          }
        }
      }
      return JSXGraph.boundingBox(
          manipulateAST, boundingbox, function.toString(), toJS, false, true);
    }

    /**
     * Convert the <code>plot</code> function into a JavaScript JSXGraph graphic control. See: <a
     * href="http://jsxgraph.uni-bayreuth.de">JSXGraph</a>
     *
     * @param plot
     * @param plotRangeX
     * @param manipulateAST
     * @param toJS
     * @param function
     * @param boundingbox
     * @param colour
     * @param engine the evaluation engine
     * @return
     * @throws IOException
     */
    private static boolean sliderWithPlot(
        IAST plot,
        IAST plotRangeX,
        final IAST manipulateAST,
        JavaScriptFormFactory toJS,
        StringBuilder function,
        double[] boundingbox,
        int[] colour,
        EvalEngine engine) {
      int plotID = plot.headID();

      final OptionArgs options;
      if (plotID == ID.Plot3D
          || plotID == ID.ComplexPlot3D
          || plotID == ID.ContourPlot
          || plotID == ID.DensityPlot) {
        options = new OptionArgs(plot.topHead(), plot, 4, engine);
        // } else if (plotID == ID.Plot) {
        // options = new OptionArgs(plot.topHead(), plot, 3, engine);
      } else {
        options = new OptionArgs(plot.topHead(), plot, 3, engine);
      }

      double plotRangeYMax = Double.MIN_VALUE;
      double plotRangeYMin = Double.MAX_VALUE;
      double plotRangeXMax = Double.MIN_VALUE;
      double plotRangeXMin = Double.MAX_VALUE;
      if (plotRangeX.isAST(S.List, 4)) {
        try {
          plotRangeXMin = engine.evalDouble(plotRangeX.arg2());
          plotRangeXMax = engine.evalDouble(plotRangeX.arg3());
        } catch (RuntimeException rex) {
        }
      }
      IExpr option = options.getOption(S.PlotStyle);
      IAST plotStyle = F.NIL;
      if (option.isPresent()) {
        if (!option.isList()) {
          option = F.List(option);
        }
        option = engine.evaluate(option);
        if (option.isList()) {
          plotStyle = (IAST) option;
        }
      }
      IExpr plotRangeY = options.getOption(S.PlotRange);
      // IAST optionPlotRange = F.NIL;
      if (plotRangeY.isPresent()) {
        boolean plotRangeEvaled = false;
        if (plotRangeY.isAST(S.List, 3)) {
          try {
            if (plotRangeY.first().isAST(S.List, 3) //
                && plotRangeY.second().isAST(S.List, 3)) {
              IAST list = (IAST) plotRangeY.first();
              plotRangeXMin = engine.evalDouble(list.first());
              plotRangeXMax = engine.evalDouble(list.second());
              list = (IAST) plotRangeY.second();
              plotRangeYMin = engine.evalDouble(list.first());
              plotRangeYMax = engine.evalDouble(list.second());
            } else {
              plotRangeYMin = engine.evalDouble(plotRangeY.first());
              plotRangeYMax = engine.evalDouble(plotRangeY.second());
            }
            plotRangeEvaled = true;
          } catch (RuntimeException rex) {
          }
        } else if (plotRangeY.isReal()) {
          if ((plotID == ID.Plot) //
              || (plotID == ID.ParametricPlot //
                  || plotID == ID.PolarPlot)) {
            try {
              plotRangeYMin = engine.evalDouble(plotRangeY.negate());
              plotRangeYMax = engine.evalDouble(plotRangeY);
              plotRangeEvaled = true;
            } catch (RuntimeException rex) {
            }
          }
        }

        if (!plotRangeEvaled) {
          // Value of option `1` is not All, Full, Automatic, a positive machine
          // number, or an appropriate list of range specifications.
          IOFunctions.printMessage(
              plot.topHead(), "prng", F.List(F.Rule(S.PlotRange, plotRangeY)), engine);
        }
      }

      // xmin, ymax, xmax, ymin
      // double[] boundingbox = new double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE,
      // Double.MAX_VALUE };

      JSXGraph.sliderNamesFromList(manipulateAST, toJS);
      IExpr arg1 = engine.evaluate(plot.arg1());
      if (!arg1.isList()) {
        arg1 = F.List(arg1);
      }

      ISymbol plotSymbolX = (ISymbol) plotRangeX.arg1();
      // double plotRangeXMax = Double.MIN_VALUE;
      // double plotRangeXMin = Double.MAX_VALUE;
      // if (plotRangeX.isAST(F.List, 4)) {
      // try {
      // plotRangeXMin = engine.evalDouble(plotRangeX.arg2());
      // plotRangeXMax = engine.evalDouble(plotRangeX.arg3());
      // } catch (RuntimeException rex) {
      // }
      // }
      if ((plotID == ID.ParametricPlot || plotID == ID.PolarPlot)
          && plotRangeYMax != Double.MIN_VALUE
          && plotRangeYMin != Double.MAX_VALUE) {
        try {
          plotRangeXMin = plotRangeYMin;
          plotRangeXMax = plotRangeYMax;
        } catch (RuntimeException rex) {
        }
      }

      // function z1(x,y) { return [ x, y, Math.sin( a * x * y ) ]; }
      // StringBuilder function = new StringBuilder();

      // boundingbox = new double[] { 0.0, Double.MIN_VALUE, listOfFunctions.size(),
      // Double.MAX_VALUE };
      if (plotID == ID.ParametricPlot) {
        return parametricPlot(
            plotRangeX,
            manipulateAST,
            engine,
            plotID,
            plotRangeYMax,
            plotRangeYMin,
            plotRangeXMax,
            plotRangeXMin,
            plotStyle,
            boundingbox,
            toJS,
            arg1,
            plotSymbolX,
            function,
            colour);
      } else if (plotID == ID.PolarPlot) {
        return polarPlot(
            plotRangeX,
            manipulateAST,
            engine,
            plotID,
            plotRangeYMax,
            plotRangeYMin,
            plotRangeXMax,
            plotRangeXMin,
            plotStyle,
            boundingbox,
            toJS,
            arg1,
            plotSymbolX,
            function,
            colour);
      }

      IAST listOfFunctions = (IAST) arg1;
      String[] functionNames = new String[listOfFunctions.size() - 1];
      for (int i = 0; i < functionNames.length; i++) {
        functionNames[i] = EvalEngine.uniqueName("$f");
      }
      for (int i = 1; i < listOfFunctions.size(); i++) {
        function.append("function ");
        function.append(functionNames[i - 1]);
        function.append("(");
        toJS.convert(function, plotSymbolX);
        function.append(") ");
        unaryJSFunction(toJS, function, plotSymbolX, listOfFunctions, i);

        IAST variables = VariablesSet.getVariables(listOfFunctions.get(i));
        if (variables.size() <= 2) {
          Dimensions2D plotRange = new Dimensions2D();
          unaryPlotParameters(
              plotSymbolX, plotRangeXMin, plotRangeXMax, listOfFunctions.get(i), plotRange, engine);
          xBoundingBoxFunctionRange(boundingbox, plotRange);
          yBoundingBoxFunctionRange(boundingbox, plotRange);
        } else {
          ISymbol sym = F.Dummy("$z" + i);
          IExpr functionRange =
              S.FunctionRange.of(engine, listOfFunctions.get(i), plotSymbolX, sym);
          yBoundingBoxFunctionRange(engine, boundingbox, functionRange);
        }
      }

      for (int i = 1; i < listOfFunctions.size(); i++) {
        function.append("board.create('functiongraph',[");
        function.append(functionNames[i - 1]);
        function.append(", ");
        JSXGraph.rangeArgs(function, plotRangeX, -1, toJS);
        function.append("]");

        RGBColor color = plotStyleColor(colour[0]++, plotStyle);
        function.append(",{strokecolor:'");
        function.append(Convert.toHex(color));
        function.append("'}");
        function.append(");\n");
      }

      if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeXMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[0] = plotRangeXMin;
      }
      if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeYMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[1] = plotRangeYMax;
      }
      if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeXMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[2] = plotRangeXMax;
      }
      if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeYMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[3] = plotRangeYMin;
      }
      return true;
    }

    private static boolean parametricPlot(
        IAST plotRangeX,
        final IAST manipulateAST,
        EvalEngine engine,
        int plotID,
        double plotRangeYMax,
        double plotRangeYMin,
        double plotRangeXMax,
        double plotRangeXMin,
        IAST plotStyle,
        double[] boundingbox,
        JavaScriptFormFactory toJS,
        IExpr arg,
        ISymbol plotSymbolX,
        StringBuilder function,
        int[] colour) {
      int[] dim = arg.isMatrix(false);
      IAST list;
      if (dim == null) {
        int vectorDim = arg.isVector();
        if (vectorDim != 2) {
          return false;
        }
        list = F.List(arg);
      } else {
        if (dim[1] != 2) {
          return false;
        }
        list = (IAST) arg;
      }

      String[] functionNames = new String[list.size() - 1];
      for (int i = 0; i < functionNames.length; i++) {
        functionNames[i] = EvalEngine.uniqueName("$f");
      }
      for (int i = 1; i < list.size(); i++) {
        IAST listOfFunctions = (IAST) list.get(i);
        if (listOfFunctions.isAST2()) {
          for (int j = 1; j < listOfFunctions.size(); j++) {
            function.append("function ");
            function.append(functionNames[i - 1]);
            function.append(j);
            function.append("(");
            toJS.convert(function, plotSymbolX);
            function.append(") ");
            unaryJSFunction(toJS, function, plotSymbolX, listOfFunctions, j);
          }

          IAST variables1 = VariablesSet.getVariables(listOfFunctions.get(1));
          IAST variables2 = VariablesSet.getVariables(listOfFunctions.get(2));
          if (variables1.size() <= 2 && variables2.size() <= 2) {
            Dimensions2D plotRange = new Dimensions2D();
            binaryPlotParameters(
                plotSymbolX,
                plotRangeXMin,
                plotRangeXMax, //
                listOfFunctions.get(1),
                listOfFunctions.get(2), //
                plotRange,
                engine);
            xBoundingBoxFunctionRange(boundingbox, plotRange);
            yBoundingBoxFunctionRange(boundingbox, plotRange);
          } else {
            ISymbol sym = F.Dummy("$z" + i);
            IExpr functionRange =
                S.FunctionRange.of(engine, listOfFunctions.get(1), plotSymbolX, sym);
            xBoundingBoxFunctionRange(engine, boundingbox, functionRange);
            functionRange = S.FunctionRange.of(engine, listOfFunctions.get(2), plotSymbolX, sym);
            yBoundingBoxFunctionRange(engine, boundingbox, functionRange);
          }

          function.append("board.create('curve',[");
          for (int j = 1; j < listOfFunctions.size(); j++) {
            function.append("function(");
            toJS.convert(function, plotSymbolX);
            function.append("){return ");
            function.append(functionNames[i - 1]);
            function.append(j);
            function.append("(");
            toJS.convert(function, plotSymbolX);
            function.append(");}");
            if (j < listOfFunctions.size() - 1) {
              function.append(",");
            }
          }

          function.append(", ");
          JSXGraph.rangeArgs(function, plotRangeX, -1, toJS);
          function.append("]");
          final RGBColor color = plotStyleColor(colour[0]++, plotStyle);
          function.append(",{strokecolor:'");
          function.append(Convert.toHex(color));
          function.append("'}");
          function.append(");\n");
        }
      }

      if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeXMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)
          && F.isFuzzyEquals(
              Double.MAX_VALUE, boundingbox[0], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[0] = plotRangeXMin;
      }
      if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeYMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[1] = plotRangeYMax;
      }
      if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeXMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)
          && F.isFuzzyEquals(
              Double.MIN_VALUE, boundingbox[2], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[2] = plotRangeXMax;
      }
      if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeYMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[3] = plotRangeYMin;
      }

      return true;
    }

    private static boolean polarPlot(
        IAST plotRangeX,
        final IAST manipulateAST,
        EvalEngine engine,
        int plotID,
        double plotRangeYMax,
        double plotRangeYMin,
        double plotRangeXMax,
        double plotRangeXMin,
        IAST plotStyle,
        double[] boundingbox,
        JavaScriptFormFactory toJS,
        IExpr arg1,
        ISymbol plotSymbolX,
        StringBuilder function,
        int[] colour) {
      IAST listOfFunctions = (IAST) arg1;
      int[] dim = arg1.isMatrix(false);
      if (dim != null) {
        if (dim[1] != 2) {
          return false;
        }
        listOfFunctions = (IAST) listOfFunctions.arg1();
      }

      String[] functionNames = new String[listOfFunctions.size() - 1];
      for (int i = 0; i < functionNames.length; i++) {
        functionNames[i] = EvalEngine.uniqueName("$f");
      }
      for (int i = 1; i < listOfFunctions.size(); i++) {
        function.append("function ");
        function.append(functionNames[i - 1]);
        function.append("(");
        toJS.convert(function, plotSymbolX);
        function.append(") ");
        unaryJSFunction(toJS, function, plotSymbolX, listOfFunctions, i);

        IAST variables = VariablesSet.getVariables(listOfFunctions.get(i));
        if (variables.size() <= 2) {
          Dimensions2D plotRange = new Dimensions2D();
          polarPlotParameters(
              plotSymbolX, plotRangeXMin, plotRangeXMax, listOfFunctions.get(i), plotRange, engine);
          xBoundingBoxFunctionRange(boundingbox, plotRange);
          yBoundingBoxFunctionRange(boundingbox, plotRange);
        } else {
          ISymbol sym = F.Dummy("$z" + i);
          IExpr functionRange =
              S.FunctionRange.of(engine, listOfFunctions.get(i), plotSymbolX, sym);
          yBoundingBoxFunctionRange(engine, boundingbox, functionRange);
        }
      }

      for (int i = 1; i < listOfFunctions.size(); i++) {
        function.append("board.create('curve', [");

        function.append("function(");
        toJS.convert(function, plotSymbolX);
        function.append("){return ");
        function.append(functionNames[i - 1]);
        function.append("(");
        toJS.convert(function, plotSymbolX);
        function.append(");}");

        // origin of polar plot
        function.append(",[0,0], ");

        toJS.convert(function, plotRangeX.arg2());
        function.append(", ");
        toJS.convert(function, plotRangeX.arg3());

        function.append("]");
        function.append(", {curveType:'polar'");

        final RGBColor color = plotStyleColor(colour[0]++, plotStyle);
        function.append(",strokeWidth:2, strokecolor:'");
        function.append(Convert.toHex(color));
        function.append("'");
        function.append("} );\n");
      }

      if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeXMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)
          && F.isFuzzyEquals(
              Double.MAX_VALUE, boundingbox[0], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[0] = plotRangeXMin;
      }
      if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeYMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[1] = plotRangeYMax;
      }
      if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeXMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)
          && F.isFuzzyEquals(
              Double.MIN_VALUE, boundingbox[2], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[2] = plotRangeXMax;
      }
      if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeYMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        boundingbox[3] = plotRangeYMin;
      }

      // if (plotID == ID.PolarPlot) {
      // if (-plotRangeXMax < boundingbox[0]) {
      // boundingbox[0] = -plotRangeXMax;
      // }
      // if (plotRangeXMax > boundingbox[1]) {
      // boundingbox[1] = plotRangeXMax;
      // }
      // if (boundingbox[2] < plotRangeXMax) {
      // boundingbox[2] = plotRangeXMax;
      // }
      // if (-plotRangeXMax < boundingbox[3]) {
      // boundingbox[3] = -plotRangeXMax;
      // }
      // }

      return true;
    }

    /**
     * Create JSXGraph bounding box and sliders.
     *
     * @param ast from position 2 to size()-1 there maybe some <code>Manipulate</code> sliders
     *     defined
     * @param boundingbox an array of double values (length 4) which describes the bounding box
     *     <code>[xMin, yMAx, xMax, yMin]</code>
     * @param function the generated JavaScript function
     * @param toJS the Symja to JavaScript converter factory
     * @param fixedBounds if <code>false</code> recalculate <code>boundingbox</code> min and max
     *     values
     * @param axes define <code>axes: true</code>
     * @return
     */
    private static IExpr boundingBox(
        IAST ast,
        double[] boundingbox,
        String function,
        JavaScriptFormFactory toJS,
        boolean fixedBounds,
        boolean axes) {
      String js = ManipulateFunction.JSXGRAPH;
      if (!fixedBounds) {
        if (F.isFuzzyEquals(Double.MAX_VALUE, boundingbox[0], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          boundingbox[0] = -5.0;
        } else {
          boundingbox[0] = boundingbox[0] - 0.5;
        }
        if (F.isFuzzyEquals(Double.MIN_VALUE, boundingbox[1], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          boundingbox[1] = 5.0;
        } else {
          boundingbox[1] = boundingbox[1] + 0.5;
        }
        if (F.isFuzzyEquals(Double.MIN_VALUE, boundingbox[2], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          boundingbox[2] = 5.0;
        } else {
          boundingbox[2] = boundingbox[2] + 0.5;
        }
        if (F.isFuzzyEquals(Double.MAX_VALUE, boundingbox[3], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          boundingbox[3] = -5.0;
        } else {
          boundingbox[3] = boundingbox[3] - 0.5;
        }
      }

      // add some "padding" around bounding box
      double xPadding = (boundingbox[2] - boundingbox[0]) / 20.0;
      double yPadding = (boundingbox[1] - boundingbox[3]) / 20.0;
      if (F.isZero(xPadding)) {
        xPadding = 5.0;
      }
      if (F.isZero(yPadding)) {
        yPadding = 5.0;
      }
      boundingbox[0] = boundingbox[0] - xPadding; // xMin
      boundingbox[2] = boundingbox[2] + xPadding; // xMax
      boundingbox[1] = boundingbox[1] + yPadding; // yMax
      boundingbox[3] = boundingbox[3] - yPadding; // yMin

      js = JSXGraph.slidersFromList(ast, js, boundingbox, toJS);

      js = js.replace("`2`", function);

      StringBuilder graphicControl = new StringBuilder();

      js = js.replace("`3`", graphicControl.toString());

      StringBuilder jsControl = new StringBuilder();
      if (axes) {
        jsControl.append("var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[");
      } else {
        jsControl.append("var board = JXG.JSXGraph.initBoard('jxgbox', {axis:false,boundingbox:[");
      }

      for (int i = 0; i < boundingbox.length; i++) {
        jsControl.append(boundingbox[i]);
        if (i < 3) {
          jsControl.append(",");
        }
      }
      jsControl.append("]});\n");
      jsControl.append("board.suspendUpdate();\n");
      jsControl.append(js);
      jsControl.append("board.unsuspendUpdate();\n");
      return F.JSFormData(jsControl.toString(), "jsxgraph");
    }

    // private static void range(StringBuilder graphicControl, IAST plotRange, int steps,
    // JavaScriptFormFactory toJS) {
    // graphicControl.append("[");
    // JSXGraph.jsxgraphRangeArgs(graphicControl, plotRange, steps, toJS);
    // graphicControl.append("]");
    // }

    private static void rangeArgs(
        StringBuilder graphicControl, IAST plotRange, int steps, JavaScriptFormFactory toJS) {
      toJS.convert(graphicControl, plotRange.arg2());
      graphicControl.append(", ");
      toJS.convert(graphicControl, plotRange.arg3());
      if (steps > 0) {
        graphicControl.append(", ");
        graphicControl.append(steps);
      }
    }

    /**
     * Generate a single JSXGraph JavaScript slider.
     *
     * @param sliderRange
     * @param slider
     * @param xPos1Slider x start position of slider
     * @param xPos2Slider x end position of slider
     * @param yPosSlider y position of slider
     * @param toJS the Symja to JavaScript converter factory
     * @return <code>true</code> if successfully generated
     */
    static boolean singleSlider(
        final IAST sliderRange,
        StringBuilder slider,
        double xPos1Slider,
        double xPos2Slider,
        double yPosSlider,
        JavaScriptFormFactory toJS) {

      if (sliderRange.isAST3() || sliderRange.size() == 5) {
        IExpr step = null;
        if (sliderRange.size() == 5) {
          step = sliderRange.arg4();
        }
        String sliderSymbol;
        String defaultValue = null;
        String label;
        if (sliderRange.arg1().isList()) {
          IAST sliderParameters = (IAST) sliderRange.arg1();
          if (sliderParameters.size() < 4) {
            return false;
          }
          sliderSymbol = sliderParameters.arg1().toString();
          // sliderNames.add(sliderSymbol);
          defaultValue = toJS.toString(sliderRange.arg2());
          label = toJS.toString(sliderParameters.arg3());
        } else {
          sliderSymbol = sliderRange.arg1().toString();
          label = sliderSymbol;
          // sliderNames.add(sliderSymbol);
        }
        slider.append("var ");
        slider.append(sliderSymbol);
        slider.append(" = board.create('slider',");

        slider.append("[[");
        slider.append(xPos1Slider);
        slider.append(",");
        slider.append(yPosSlider);
        slider.append("],[");
        slider.append(xPos2Slider);
        slider.append(",");
        slider.append(yPosSlider);
        slider.append("],[");
        toJS.convert(slider, sliderRange.arg2());
        slider.append(",");
        if (defaultValue != null) {
          slider.append(defaultValue);
        } else {
          toJS.convert(slider, sliderRange.arg2());
        }
        slider.append(",");
        toJS.convert(slider, sliderRange.arg3());
        slider.append("]],");

        slider.append("{name:'");
        slider.append(label);
        slider.append("'");
        if (step != null) {
          slider.append(",snapWidth:");
          toJS.convert(slider, step);
        }
        slider.append("});\n");

        return true;
      }
      return false;
    }

    /**
     * Add the slider name to the toJS slider names.
     *
     * @param sliderRange a single <code>List(slider-name,...)</code> representing a slider
     *     definition
     * @param toJS the Symja to JavaScript converter factory
     * @return
     */
    static boolean singleSliderName(final IAST sliderRange, JavaScriptFormFactory toJS) {
      if (sliderRange.isAST3() || sliderRange.size() == 5) {
        String sliderSymbol;
        if (sliderRange.arg1().isList()) {
          IAST sliderParameters = (IAST) sliderRange.arg1();
          if (sliderParameters.size() < 4) {
            return false;
          }
          sliderSymbol = sliderParameters.arg1().toString();
          toJS.appendSlider(sliderSymbol);
        } else {
          sliderSymbol = sliderRange.arg1().toString();
        }
        toJS.appendSlider(sliderSymbol);
        return true;
      }
      return false;
    }

    /**
     * Add all slider names to the toJS slider names.
     *
     * @param ast from position 2 to size()-1 there maybe some <code>Manipulate</code> sliders
     *     defined
     * @param toJS the Symja to JavaScript converter factory
     */
    private static void sliderNamesFromList(final IAST ast, JavaScriptFormFactory toJS) {
      if (ast.size() >= 3) {
        if (ast.arg2().isList()) {
          for (int i = 2; i < ast.size(); i++) {
            if (ast.get(i).isList()) {
              if (!ManipulateFunction.JSXGraph.singleSliderName((IAST) ast.get(i), toJS)) {
                return;
              }
            } else {
              break;
            }
          }
        }
      }
    }

    /**
     * Create JSXGraph sliders.
     *
     * @param ast from position 2 to size()-1 there maybe some <code>Manipulate</code> sliders
     *     defined
     * @param js the JSXGraph JavaScript template
     * @param boundingbox an array of double values (length 4) which describes the bounding box
     *     <code>[xMin, yMAx, xMax, yMin]</code>
     * @param toJS the Symja to JavaScript converter factory
     * @return
     */
    private static String slidersFromList(
        final IAST ast, String js, double[] boundingbox, JavaScriptFormFactory toJS) {
      if (ast.size() >= 3) {
        if (ast.arg2().isList()) {
          double xDelta = (boundingbox[2] - boundingbox[0]) / 10;
          double yDelta = (boundingbox[1] - boundingbox[3]) / 10;
          double xPos1Slider = boundingbox[0] + xDelta;
          double xPos2Slider = boundingbox[2] - xDelta;
          double yPosSlider = boundingbox[1] - yDelta;
          StringBuilder slider = new StringBuilder();
          for (int i = 2; i < ast.size(); i++) {
            if (ast.get(i).isList()) {
              if (!ManipulateFunction.JSXGraph.singleSlider(
                  ast.getAST(i), slider, xPos1Slider, xPos2Slider, yPosSlider, toJS)) {
                return null;
              }
              yPosSlider -= yDelta;
            } else {
              break;
            }
          }
          js = StringUtils.replace(js, "`1`", slider.toString());
        }
      } else {
        js = StringUtils.replace(js, "`1`", "");
      }
      return js;
    }
  }

  /**
   * Chart methods which use <a href="https://github.com/plotly/plotly.js">plotly.js Javascript</a>
   */
  private static final class Plotly {

    private static IExpr plot(IAST plot, final IAST manipulateAST, EvalEngine engine) {
      if (plot.size() < 2) {
        return F.NIL;
      }
      IExpr arg1 = plot.arg1();
      if (!arg1.isList()) {
        arg1 = engine.evaluate(arg1);
      }
      // if (!arg1.isList()) {
      // // example SparseArray
      // arg1 = arg1.normal(false);
      // }

      if (plot.isAST(S.DensityHistogram)) {
        return densityHistogram(arg1);
      } else if (plot.isAST(S.Histogram)) {
        return histogram(arg1);
      } else if (plot.isAST(S.BarChart)) {
        return barChart(arg1, plot, engine);
      } else if (plot.isAST(S.BoxWhiskerChart)) {
        return boxWhiskerChart(arg1);
      } else if (plot.isAST(S.PieChart)) {
        return pieChart(arg1);
      } else if (plot.isAST(S.MatrixPlot)) {
        return matrixPlot(arg1);
      }
      return F.NIL;
    }

    private static IExpr matrixPlot(IExpr arg) {
      double[][] matrix = arg.toDoubleMatrix();
      if (matrix != null && matrix.length > 0) {
        final int rowCount = matrix.length;
        String[] yStrs = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
          yStrs[i] = Integer.toString(i + 1);
        }
        final int colCount = matrix[0].length;
        String[] xStrs = new String[colCount];
        for (int i = 0; i < colCount; i++) {
          xStrs[i] = Integer.toString(i + 1);
        }
        Layout layout = buildLayout("MatrixPlot").build();
        HeatmapTrace trace = HeatmapTrace.builder(xStrs, yStrs, matrix).build();
        Figure figure = new Figure(layout, trace);
        return F.JSFormData(figure.asJavascript("plotly"), "plotly");
      }
      return F.NIL;
    }

    private static IExpr pieChart(IExpr arg) {
      double[] vector = arg.toDoubleVector();
      if (vector != null && vector.length > 0) {
        String[] strs = new String[vector.length];
        for (int i = 0; i < vector.length; i++) {
          strs[i] = Integer.toString(i + 1);
        }

        Layout layout = buildLayout("PieChart").build();
        PieTrace trace = PieTrace.builder(strs, vector).build();
        Figure figure = new Figure(layout, trace);
        return F.JSFormData(figure.asJavascript("plotly"), "plotly");
      }
      return F.NIL;
    }

    private static IExpr boxWhiskerChart(IExpr arg) {
      if (arg.isListOfLists()) {
        IAST listOfLists = (IAST) arg;
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < listOfLists.size(); i++) {
          buf.append("var y" + i);
          buf.append("=[");
          double[] vector = listOfLists.get(i).toDoubleVector();
          if (vector != null && vector.length > 0) {
            Convert.joinToString(vector, buf, ",");
            buf.append("];\n");
            buf.append("var trace" + i);
            buf.append(" = {y: y" + i);
            buf.append(", type: 'box'};\n");
          } else {
            return F.NIL;
          }
        }
        buf.append("var data = [");
        for (int i = 1; i < listOfLists.size(); i++) {
          buf.append("trace" + i);
          if (i < listOfLists.size() - 1) {
            buf.append(",");
          }
        }
        buf.append("];\n");
        buf.append("Plotly.newPlot('plotly', data);");
        return F.JSFormData(buf.toString(), "plotly");
      }
      double[] vector = arg.toDoubleVector();
      if (vector != null && vector.length > 0) {
        StringBuilder buf = new StringBuilder();
        buf.append("var y1=[");
        Convert.joinToString(vector, buf, ",");
        buf.append("];\n");
        buf.append(
            "var trace1 = {y: y1, type: 'box'};\n" //
                + "var data = [trace1];\n"
                + "Plotly.newPlot('plotly', data);");
        return F.JSFormData(buf.toString(), "plotly");
      }
      return F.NIL;
    }

    private static IExpr barChart(IExpr arg, IAST plot, EvalEngine engine) {
      double[] vector = arg.toDoubleVector();
      if (vector != null && vector.length > 0) {
        Orientation orientation = Orientation.VERTICAL;
        OptionArgs options = new OptionArgs(S.BarChart, plot, 2, engine);
        IExpr orientExpr = options.getOption(S.BarOrigin);
        if (orientExpr == S.Bottom) {
          orientation = Orientation.VERTICAL;
        } else if (orientExpr == S.Left) {
          orientation = Orientation.HORIZONTAL;
        }
        String[] strs = new String[vector.length];
        for (int i = 0; i < vector.length; i++) {
          strs[i] = Integer.toString(i + 1);
        }
        Layout layout = buildLayout("BarChart").build();
        BarTrace trace = BarTrace.builder(strs, vector).orientation(orientation).build();
        Figure figure = new Figure(layout, trace);
        return F.JSFormData(figure.asJavascript("plotly"), "plotly");
      }
      return F.NIL;
    }

    private static LayoutBuilder buildLayout(String chartType) {
      if (AUTOSIZE) {
        return Layout.builder(chartType).autosize(true).width(WIDTH).height(HEIGHT);
      }
      return Layout.builder(chartType).autosize(false);
    }

    private static LayoutBuilder buildLayout(String chartType, String xTitle, String yTitle) {
      if (AUTOSIZE) {
        return Layout.builder(chartType, xTitle, yTitle).autosize(true).width(WIDTH).height(HEIGHT);
      }
      return Layout.builder(chartType, xTitle, yTitle).autosize(false);
    }

    private static IExpr histogram(IExpr arg1) {
      double[] vector = arg1.toDoubleVectorIgnore();
      if (vector != null && vector.length > 0) {
        Layout layout = buildLayout("Histogram").build();
        HistogramTrace trace = HistogramTrace.builder(vector).build();
        Figure figure = new Figure(layout, trace);
        // Plot.show(figure);
        return F.JSFormData(figure.asJavascript("plotly"), "plotly");
      }
      return F.NIL;
    }

    private static IExpr densityHistogram(IExpr arg) {
      int[] dims = arg.isMatrixIgnore();
      if (dims != null) {
        if (dims[1] == 2) {
          RealMatrix m = arg.toRealMatrixIgnore();
          if (m != null) {
            // double opacity = 1.0;
            // if (plot.size() >= 2 && plot.arg2().isAST(F.List, 2)) {
            // double binWidth = plot.arg2().first().evalDouble();
            // }
            double[] vector1 = m.getColumn(0);
            double[] vector2 = m.getColumn(1);
            if (vector1 != null && vector1.length > 0 && vector2 != null && vector2.length > 0) {
              Histogram2DBuilder builder = Histogram2DTrace.builder(vector1, vector2);
              // builder.opacity(opacity);
              Layout layout = buildLayout("DensityHistogram", "x", "y").build();
              Figure figure = new Figure(layout, builder.build());
              return F.JSFormData(figure.asJavascript("plotly"), "plotly");
            }
          }
        }
      }
      return F.NIL;
    }
  }

  private static final class BarChart extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return redirectToManipulate(ast, engine);
    }
  }

  private static final class BoxWhiskerChart extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return redirectToManipulate(ast, engine);
      // "Function `1` not implemented.", //
      //      return IOFunctions.printMessage(ast.topHead(), "zznotimpl", F.List(ast.topHead()),
      // engine);
      // return redirectToManipulate(ast, engine);
    }
  }

  private static final class ComplexPlot3D extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return redirectToManipulate(ast, engine);
    }
  }

  private static final class ContourPlot extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // "Function `1` not implemented.", //
      return IOFunctions.printMessage(ast.topHead(), "zznotimpl", F.List(ast.topHead()), engine);
      // return redirectToManipulate(ast, engine);
    }
  }

  private static final class DensityPlot extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // "Function `1` not implemented.", //
      return IOFunctions.printMessage(ast.topHead(), "zznotimpl", F.List(ast.topHead()), engine);
      // return redirectToManipulate(ast, engine);
    }
  }

  private static final class DensityHistogram extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return redirectToManipulate(ast, engine);
    }
  }

  private static final class Histogram extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return redirectToManipulate(ast, engine);
    }
  }

  private static final class MatrixPlot extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return redirectToManipulate(ast, engine);
    }
  }

  private static final class PieChart extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return redirectToManipulate(ast, engine);
    }
  }

  private static IExpr redirectToManipulate(final IAST ast, EvalEngine engine) {
    if (Config.USE_MANIPULATE_JS) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
    }
    return F.NIL;
  }

  private static class Manipulate extends AbstractEvaluator {

    /**
     * Return a list of JSXGraph plots
     *
     * @param expr
     * @param engine
     * @return a list of JSXGraph plots, <code>F.NIL</code> otherwise
     */
    private static IAST checkJSXGraphPlots(IExpr expr, EvalEngine engine) {
      if (expr.isList()) {
        IAST listOfSymbols = (IAST) expr;
        for (int i = 1; i < listOfSymbols.size(); i++) {
          IExpr arg = listOfSymbols.get(i);
          if (arg.isAST(S.ListLinePlot)
              || arg.isAST(S.ListPlot)
              || arg.isAST(S.Plot)
              || arg.isAST(S.ParametricPlot)
              || arg.isAST(S.PolarPlot)) {
            continue;
          }
          return F.NIL;
        }
        return listOfSymbols;
      } else {
        if (expr.isAST(S.ListLinePlot)
            || expr.isAST(S.ListPlot)
            || expr.isAST(S.Plot)
            || expr.isAST(S.ParametricPlot)
            || expr.isAST(S.PolarPlot)) {
          return F.List(expr);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST manipulateAST, EvalEngine engine) {
      try {
        IExpr arg1 = manipulateAST.arg1();
        IAST plots = checkJSXGraphPlots(arg1, engine);
        if (plots.isPresent()) {
          return JSXGraph.showPlots(manipulateAST, engine, plots);
        }
        // if (arg1.isAST(F.ComplexPlot3D)) {
        // // `1` currently not supported in `2`.
        // return IOFunctions.printMessage(ast.topHead(), "unsupported",
        // F.List(F.ComplexPlot3D, F.stringx("Symja")), engine);
        // }

        if (arg1.isAST(S.BarChart)
            || arg1.isAST(S.BoxWhiskerChart)
            || arg1.isAST(S.DensityHistogram)
            || arg1.isAST(S.Histogram)
            || arg1.isAST(S.MatrixPlot)
            || arg1.isAST(S.PieChart)) {
          return Plotly.plot((IAST) arg1, manipulateAST, engine);
        }

        if (arg1.isAST(S.Plot3D)
            || arg1.isAST(S.ComplexPlot3D)
            || arg1.isAST(S.ContourPlot)
            || arg1.isAST(S.DensityPlot)) {
          IAST plot = (IAST) arg1;
          if (plot.size() >= 3) {
            if (!plot.arg2().isList3() || !plot.arg2().first().isSymbol()) {
              // Range specification `1` is not of the form {x, xmin, xmax}.
              return IOFunctions.printMessage(plot.topHead(), "pllim", F.List(plot.arg2()), engine);
            }
            IAST plotRangeX = (IAST) plot.arg2();
            IAST plotRangeY = F.NIL;
            if (plot.size() >= 4) {
              if (plot.arg3().isList3()) {
                plotRangeY = (IAST) plot.arg3();
              } else if (!arg1.isAST(S.ComplexPlot3D)) {
                if (!plot.arg3().isList3() || !plot.arg3().first().isSymbol()) {
                  // Range specification `1` is not of the form {x, xmin, xmax}.
                  return IOFunctions.printMessage(
                      plot.topHead(), "pllim", F.List(plot.arg3()), engine);
                }
              }
            }

            return Mathcell.sliderWithPlot(plot, plotRangeX, plotRangeY, manipulateAST, engine);
          }
        } else if (arg1.isAST(S.ListPlot3D)) {
          return Mathcell.plot((IAST) arg1, manipulateAST, engine);
        } else if (manipulateAST.isAST2() && manipulateAST.arg2().isList()) {
          IExpr formula = arg1;
          IAST sliderRange = (IAST) manipulateAST.arg2();

          if (sliderRange.size() == 4 || sliderRange.size() == 5) {
            if (sliderRange.arg1().isSymbol()) {
              return Mathcell.sliderWithFormulas(formula, sliderRange, engine);
            }
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), S.Manipulate, rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   * Convert a range of 2 real numbers into a <code>Plot3D</code> compatible range.
   *
   * @param graphicControl
   * @param plotRange example <code>{x, -2, 2}</code>
   * @param steps an additional step parameter. If less <code>0</code> the parameter will be ignored
   * @param toJS the expression to JavaScript transpiler
   */
  private static void realRange(
      StringBuilder graphicControl, IAST plotRange, int steps, JavaScriptFormFactory toJS) {
    graphicControl.append("[");
    toJS.convert(graphicControl, plotRange.arg2());
    graphicControl.append(", ");
    toJS.convert(graphicControl, plotRange.arg3());
    if (steps > 0) {
      graphicControl.append(", ");
      graphicControl.append(steps);
    }
    graphicControl.append("]");
  }

  /**
   * Convert a range of 2 complex numbers into a <code>ComplexPlot3D</code> compatible range.
   *
   * @param graphicControl
   * @param plotRange example <code>{z, -2-2*I, 2+2*I}</code>
   * @param steps an additional step parameter. If less <code>0</code> the parameter will be ignored
   * @param toJS the expression to JavaScript transpiler
   * @return an array for the x-(real)-range and the y-(imaginary)-range of the 3D plot
   */
  private static double[] complexRange(
      StringBuilder graphicControl, IAST plotRange, int steps, JavaScriptFormFactory toJS) {
    double[] result = new double[2];
    IExpr zMin = plotRange.arg2();
    IExpr zMax = plotRange.arg3();
    Complex cMin = zMin.evalComplex();
    Complex cMax = zMax.evalComplex();
    if (cMin != null && cMax != null) {
      double reMin = cMin.getReal();
      double imMin = cMin.getImaginary();
      double reMax = cMax.getReal();
      double imMax = cMax.getImaginary();
      if (reMin > reMax) {
        // swap range values
        double r = reMax;
        reMax = reMin;
        reMin = r;
      }
      if (imMin > imMax) {
        // swap range values
        double i = imMax;
        imMax = imMin;
        imMin = i;
      }
      result[0] = reMax - reMin;
      result[1] = imMax - imMin;
      graphicControl.append(", [");
      toJS.convert(graphicControl, F.num(reMin));
      graphicControl.append(", ");
      toJS.convert(graphicControl, F.num(reMax));
      if (steps > 0) {
        graphicControl.append(", ");
        graphicControl.append(steps);
      }
      graphicControl.append("], [");
      toJS.convert(graphicControl, F.num(imMin));
      graphicControl.append(", ");
      toJS.convert(graphicControl, F.num(imMax));
      if (steps > 0) {
        graphicControl.append(", ");
        graphicControl.append(steps);
      }
      graphicControl.append("]");
    }
    return result;
  }

  /**
   * Plot a list of 2D points.
   *
   * @param ast
   * @param arg the number of the current argument
   * @param pointList
   * @param toJS
   * @param engine
   * @return
   */
  private static void sequencePointListPlot(
      final IAST ast,
      int arg,
      IAST pointList,
      JavaScriptFormFactory toJS,
      StringBuilder function,
      double[] boundingbox,
      int[] colour,
      EvalEngine engine) {
    // plot a list of 2D points
    final RGBColor color = plotStyleColor(colour[0]++, F.NIL);
    if (ast.arg1().isAST(S.ListLinePlot) && pointList.size() > 2) {
      // IAST lastPoint = (IAST) pointList.arg1();
      IAST lastPoint = F.NIL;
      boolean isConnected = false;
      int start = Integer.MAX_VALUE;
      for (int i = 1; i < pointList.size(); i++) {
        IAST point = (IAST) pointList.get(i);
        if (isNonReal(point.arg1(), point.arg2())) {
          continue;
        }
        lastPoint = point;
        start = i + 1;
        break;
      }

      if (start < Integer.MAX_VALUE) {
        xBoundingBox(engine, boundingbox, lastPoint.arg1());
        yBoundingBox(engine, boundingbox, lastPoint.arg2());
        for (int i = start; i < pointList.size(); i++) {

          IAST point = (IAST) pointList.get(i);
          if (isNonReal(point.arg1(), point.arg2())) {
            if (!isConnected && lastPoint.isPresent()) {
              xBoundingBox(engine, boundingbox, lastPoint.arg1());
              yBoundingBox(engine, boundingbox, lastPoint.arg2());
              function.append("board.create('point', [");
              function.append("function() {return ");
              toJS.convert(function, lastPoint.arg1());
              function.append(";}");
              function.append(",");
              function.append("function() {return ");
              toJS.convert(function, lastPoint.arg2());
              function.append(";}");
              function.append("], ");
              function.append(" {color:'");
              function.append(Convert.toHex(color));
              function.append("' ,name:'', face:'o', size: 2 } );\n");
            }
            lastPoint = F.NIL;
            isConnected = false;
            continue;
          }
          if (lastPoint.isPresent()) {
            function.append("board.create('line',");
            xBoundingBox(engine, boundingbox, point.arg1());
            yBoundingBox(engine, boundingbox, point.arg2());
            function.append("[[");
            function.append("function() {return ");
            toJS.convert(function, lastPoint.arg1());
            function.append(";}");
            function.append(",");
            function.append("function() {return ");
            toJS.convert(function, lastPoint.arg2());
            function.append(";}");
            function.append("],");
            function.append("[");
            function.append("function() {return ");
            toJS.convert(function, point.arg1());
            function.append(";}");
            function.append(",");
            function.append("function() {return ");
            toJS.convert(function, point.arg2());
            function.append(";}");
            function.append("]],");
            function.append(" {color:'");
            function.append(Convert.toHex(color));
            function.append("', straightFirst:false, straightLast:false, strokeWidth:2});\n");
            isConnected = true;
          }
          lastPoint = point;
        }
      }
    } else {
      for (int i = 1; i < pointList.size(); i++) {
        IAST point = (IAST) pointList.get(i);
        if (isNonReal(point.arg1(), point.arg2())) {
          continue;
        }
        xBoundingBox(engine, boundingbox, point.arg1());
        yBoundingBox(engine, boundingbox, point.arg2());
        function.append("board.create('point', [");
        function.append("function() {return ");
        toJS.convert(function, point.arg1());
        function.append(";}");
        function.append(",");
        function.append("function() {return ");
        toJS.convert(function, point.arg2());
        function.append(";}");
        function.append("], ");
        function.append(" {color:'");
        function.append(Convert.toHex(color));
        function.append("' ,name:'', face:'o', size: 2 } );\n");
      }
    }
  }

  /**
   * Plot a list of points for Y-values for the X-values <code>1,2,3,...</code>.
   *
   * @param ast
   * @param pointList
   * @param toJS
   * @param engine
   * @return
   */
  private static void sequenceYValuesListPlot(
      final IAST ast,
      int arg,
      IAST pointList,
      JavaScriptFormFactory toJS,
      StringBuilder function,
      double[] boundingbox,
      int[] colour,
      EvalEngine engine) {
    final RGBColor color = plotStyleColor(colour[0]++, F.NIL);
    // StringBuilder function = new StringBuilder();
    // boundingbox = new double[] { 0.0, Double.MIN_VALUE, pointList.size(), Double.MAX_VALUE };
    xBoundingBox(engine, boundingbox, F.C0);
    xBoundingBox(engine, boundingbox, F.ZZ(pointList.size()));
    if (ast.arg1().isAST(S.ListLinePlot)) {
      IExpr lastPoint = F.NIL;
      int lastPosition = -1;
      boolean isConnected = false;
      int start = Integer.MAX_VALUE;
      for (int i = 1; i < pointList.size(); i++) {
        IExpr currentPointY = pointList.get(i);
        if (isNonReal(currentPointY)) {
          continue;
        }
        lastPoint = currentPointY;
        lastPosition = i;
        start = i + 1;
        break;
      }
      if (start < Integer.MAX_VALUE) {
        yBoundingBox(engine, boundingbox, lastPoint);
        for (int i = start; i < pointList.size(); i++) {
          IExpr currentPointY = pointList.get(i);
          if (isNonReal(currentPointY)) {
            if (!isConnected && lastPoint.isPresent()) {
              yBoundingBox(engine, boundingbox, lastPoint);
              function.append("board.create('point', [");
              function.append("function() {return " + lastPosition + ";}");
              function.append(",");
              function.append("function() {return ");
              toJS.convert(function, lastPoint);
              function.append(";}");
              function.append("],");
              function.append(" {color:'");
              function.append(Convert.toHex(color));
              function.append("' ,name:'', face:'o', size: 2 } );\n");
            }
            lastPoint = F.NIL;
            lastPosition = -1;
            isConnected = false;
            continue;
          }
          if (lastPoint.isPresent()) {
            yBoundingBox(engine, boundingbox, currentPointY);
            function.append("board.create('line',");
            function.append("[[");
            function.append("function() {return " + lastPosition + ";}");
            function.append(",");
            function.append("function() {return ");
            toJS.convert(function, lastPoint);
            function.append(";}");
            function.append("],");
            function.append("[");
            function.append("function() {return " + i + ";}");
            function.append(",");
            function.append("function() {return ");
            toJS.convert(function, currentPointY);
            function.append(";}");
            function.append("]],");
            function.append(" {color:'");
            function.append(Convert.toHex(color));
            function.append("' ,straightFirst:false, straightLast:false, strokeWidth:2});\n");
            isConnected = true;
          }
          lastPoint = currentPointY;
          lastPosition = i;
        }
      }
    } else {
      for (int i = 1; i < pointList.size(); i++) {
        IExpr currentPointY = pointList.get(i);
        if (isNonReal(currentPointY)) {
          continue;
        }
        yBoundingBox(engine, boundingbox, currentPointY);
        function.append("board.create('point', [");
        function.append("function() {return " + i + ";}");
        function.append(",");
        function.append("function() {return ");
        toJS.convert(function, pointList.get(i));
        function.append(";}");
        function.append("], ");
        function.append(" {color:'");
        function.append(Convert.toHex(color));
        function.append("' ,name:'', face:'o', size: 2 } );\n");
      }
    }
  }

  private static boolean isNonReal(IExpr lastPoint) {
    return lastPoint == S.Indeterminate || lastPoint == S.None || lastPoint.isAST(S.Missing);
  }

  private static boolean isNonReal(IExpr lastPointX, IExpr lastPointY) {
    return isNonReal(lastPointX) || isNonReal(lastPointY);
  }

  private static void unaryJSFunction(
      JavaScriptFormFactory toJS,
      StringBuilder function,
      ISymbol plotSymbolX,
      IAST listOfFunctions,
      int i) {
    toJS.setVariables(plotSymbolX);
    function.append("{ try { return [");
    toJS.convert(function, listOfFunctions.get(i));
    function.append("];} catch(e) { return Number.NaN;} }\n");
  }

  public static void unaryPlotParameters(
      final ISymbol xVariable,
      final double xMin,
      final double xMax,
      final IExpr yFunction,
      Dimensions2D autoPlotRange,
      final EvalEngine engine) {
    final double step = (xMax - xMin) / N;
    double y;

    final UnaryNumerical f1;
    if (yFunction.isList() && yFunction.isAST1()) {
      f1 = new UnaryNumerical(yFunction.first(), xVariable, engine);
    } else {
      f1 = new UnaryNumerical(yFunction, xVariable, engine);
    }
    final double data[][] = new double[2][N + 1];
    double x = xMin;

    for (int i = 0; i < N + 1; i++) {
      y = f1.value(x);
      data[0][i] = x;
      data[1][i] = y;
      x += step;
    }
    double[] vMinMax = automaticPlotRange(data[1]);
    autoPlotRange.minMax(xMin, x, vMinMax[0], vMinMax[1]);
  }

  public static void binaryPlotParameters(
      ISymbol timeVariable,
      final double timeMin,
      final double timeMax,
      final IExpr xFunction,
      final IExpr yFunction,
      Dimensions2D plotRange,
      final EvalEngine engine) {
    final double step = (timeMax - timeMin) / N;
    final UnaryNumerical f1Unary = new UnaryNumerical(xFunction, timeVariable, engine);
    final UnaryNumerical f2Unary = new UnaryNumerical(yFunction, timeVariable, engine);
    final double data[][] = new double[2][N + 1];
    double t = timeMin;

    for (int i = 0; i < N + 1; i++) {
      data[0][i] = f1Unary.value(t);
      data[1][i] = f2Unary.value(t);
      t += step;
    }
    double[] xMinMax = automaticPlotRange(data[0]);
    double[] yMinMax = automaticPlotRange(data[1]);
    plotRange.minMax(
        xMinMax[0],
        xMinMax[1], //
        yMinMax[0],
        yMinMax[1]);
  }

  public static void polarPlotParameters(
      final ISymbol xVariable,
      final double xMin,
      final double xMax,
      final IExpr yFunction,
      Dimensions2D plotRange,
      final EvalEngine engine) {
    final double step = (xMax - xMin) / N;
    double y;

    final UnaryNumerical f1 = new UnaryNumerical(yFunction, xVariable, engine);
    final double data[][] = new double[2][N + 1];
    double x = xMin;

    for (int i = 0; i < N + 1; i++) {
      y = f1.value(x);
      data[0][i] = y * Math.cos(x);
      data[1][i] = y * Math.sin(x);
      x += step;
    }
    double[] xMinMax = automaticPlotRange(data[0]);
    double[] yMinMax = automaticPlotRange(data[1]);
    plotRange.minMax(
        xMinMax[0],
        xMinMax[1], //
        yMinMax[0],
        yMinMax[1]);
  }

  /**
   * Calculates mean and standard deviation, throwing away all points which are more than 'thresh'
   * number of standard deviations away from the mean. These are then used to find good vmin and
   * vmax values. These values can then be used to find Automatic Plotrange.
   *
   * @param values of the y-axe
   * @return vmin and vmax value of the range
   */
  private static double[] automaticPlotRange(final double values[]) {

    double thresh = 2.0;
    double[] yValues = new double[values.length];
    System.arraycopy(values, 0, yValues, 0, values.length);
    Arrays.sort(yValues);
    if (Math.abs(yValues[0]) < 100.0
        && //
        Math.abs(yValues[values.length - 1]) < 100.0) {
      return new double[] {yValues[0], yValues[values.length - 1]};
    }
    double valavg = new Mean().evaluate(yValues);
    double valdev = new StandardDeviation().evaluate(yValues, valavg);
    if (Double.isFinite(valavg) && Double.isFinite(valdev)) {

      int n1 = 0;
      int n2 = values.length - 1;
      if (valdev != 0) {
        for (double v : yValues) {
          if (Double.isFinite(v)) {
            if (Math.abs(v - valavg) / valdev < thresh) {
              break;
            }
            n1 += 1;
          }
        }
        for (int i = yValues.length - 1; i >= 0; i--) {
          double v = yValues[i];
          if (Double.isFinite(v)) {
            if (Math.abs(v - valavg) / valdev < thresh) {
              break;
            }
            n2 -= 1;
          }
        }
      }

      double vrange = yValues[n2] - yValues[n1];
      double vmin = yValues[n1] - 0.05 * vrange; // 5% extra looks nice
      double vmax = yValues[n2] + 0.05 * vrange;
      return new double[] {vmin, vmax};
    }
    double vmin = -5.0;
    double vmax = 5.0;
    for (int i = 0; i < yValues.length; i++) {
      double v = yValues[i];
      if (Double.isFinite(v) && v >= -5.0 && v <= 5.0) {
        vmin = v;
        break;
      }
    }
    for (int i = yValues.length - 1; i >= 0; i--) {
      double v = yValues[i];
      if (Double.isFinite(v) && v >= -5.0 && v <= 5.0) {
        vmax = v;
        break;
      }
    }
    return new double[] {vmin, vmax};
  }

  /**
   * @param functionNumber the number of the function which should be plotted
   * @param plotStyle if present a <code>List()</code> is expected
   */
  private static RGBColor plotStyleColor(int functionNumber, IAST plotStyle) {
    if (plotStyle.isList() && plotStyle.size() > functionNumber) {
      IExpr temp = plotStyle.get(functionNumber);
      if (temp.isASTSizeGE(S.Directive, 2)) {
        IAST directive = (IAST) temp;
        for (int j = 1; j < directive.size(); j++) {
          temp = directive.get(j);
          RGBColor color = Convert.toAWTColor(temp);
          if (color != null) {
            return color;
          }
        }
      } else {
        RGBColor color = Convert.toAWTColor(temp);
        if (color != null) {
          return color;
        }
      }
    }
    return PLOT_COLORS[(functionNumber - 1) % PLOT_COLORS.length];
  }

  private static int[] calcHistogram(double[] data, double min, double max, int numBins) {
    final int[] result = new int[numBins];
    final double binSize = (max - min) / numBins;

    for (double d : data) {
      int bin = (int) ((d - min) / binSize);
      if ((bin < 0) || (bin >= numBins)) {
        /* this data is smaller than min */
      } else {
        result[bin] += 1;
      }
    }
    return result;
  }

  /**
   * @param ast
   * @param pointList
   * @param toJS
   * @param engine
   * @return
   * @deprecated use Plotly methods
   */
  @Deprecated
  private static IExpr sequenceBarChart(
      final IAST ast, IAST pointList, JavaScriptFormFactory toJS, EvalEngine engine) {
    double[] boundingbox;

    StringBuilder function = new StringBuilder();
    boundingbox = new double[] {0.0, 0.0, pointList.size() - 0.5, 0.0};

    if (ast.arg1().isAST(S.Histogram)) {
      function.append("var dataArr = [");
      double[] dData = pointList.toDoubleVector();
      if (dData == null) {
        return F.NIL;
      }
      double min = StatUtils.min(dData);
      double max = StatUtils.max(dData);
      double defaultRange = (max - min) / (0.5);
      int nRanges = (int) Math.ceil(defaultRange);
      if (nRanges < 10) {
        nRanges = 10;
      }
      if (nRanges > 100) {
        nRanges = 100;
      }
      defaultRange = (max - min) / (nRanges);
      int[] buckets = calcHistogram(dData, min, max, nRanges);

      boundingbox = new double[] {min, 0.0, max, 0.0};
      for (int i = 0; i < buckets.length; i++) {
        IInteger value = F.ZZ(buckets[i]);
        toJS.convert(function, value);
        yBoundingBox(engine, boundingbox, value);
        if (i < buckets.length - 1) {
          function.append(",");
        }
      }
      function.append("];\n");
    } else if (ast.arg1().isAST(S.BarChart)) {
      function.append("var dataArr = [");
      boundingbox = new double[] {0.0, 0.0, pointList.size() - 0.5, 0.0};
      for (int i = 1; i < pointList.size(); i++) {
        IExpr currentPointY = pointList.get(i);
        if (isNonReal(currentPointY)) {
          continue;
        }
        toJS.convert(function, currentPointY);
        yBoundingBox(engine, boundingbox, currentPointY);
        if (i < pointList.size() - 1) {
          function.append(",");
        }
      }
      function.append("];\n");
    }

    function.append("board.create('chart', dataArr,");
    if (ast.arg1().isAST(S.Histogram)) {
      function.append(" {chartStyle:'bar',width:1.0,labels:dataArr} );\n");
      return JSXGraph.boundingBox(ast, boundingbox, function.toString(), toJS, true, true);
    }
    function.append(" {chartStyle:'bar',width:0.6,labels:dataArr} );\n");
    return JSXGraph.boundingBox(ast, boundingbox, function.toString(), toJS, false, true);
  }

  private static void xBoundingBox(EvalEngine engine, double[] boundingbox, IExpr xExpr) {
    try {
      double xValue = engine.evalDouble(xExpr);
      if (Double.isFinite(xValue)) {
        if (xValue < boundingbox[0]) {
          boundingbox[0] = xValue;
        }
        if (xValue > boundingbox[2]) {
          boundingbox[2] = xValue;
        }
      }
    } catch (RuntimeException rex) {
      //
    }
  }

  private static void xBoundingBoxFunctionRange(double[] boundingbox, Dimensions2D plotRange) {
    if (plotRange != null) {
      double xValue = plotRange.xMin;
      if (Double.isFinite(xValue)) {
        if (xValue < boundingbox[0]) { // min
          boundingbox[0] = xValue;
        }
      }
      xValue = plotRange.xMax;
      if (Double.isFinite(xValue)) {
        if (xValue > boundingbox[2]) { // max
          boundingbox[2] = xValue;
        }
      }
    }
  }

  private static void yBoundingBoxFunctionRange(double[] boundingbox, Dimensions2D plotRange) {
    if (plotRange != null) {
      double yValue = plotRange.yMin;
      if (Double.isFinite(yValue)) {
        if (yValue < boundingbox[3]) { // min
          boundingbox[3] = yValue;
        }
      }
      yValue = plotRange.yMax;
      if (Double.isFinite(yValue)) {
        if (yValue > boundingbox[1]) { // max
          boundingbox[1] = yValue;
        }
      }
    }
  }

  private static void xBoundingBoxFunctionRange(
      EvalEngine engine, double[] boundingbox, IExpr functionRange) {
    if (functionRange.isPresent()) {
      IExpr l = F.NIL;
      IExpr u = F.NIL;
      if ((functionRange.isAST(S.LessEqual, 4) || functionRange.isAST(S.Less, 4)) //
          && functionRange.second().isSymbol()) {
        l = functionRange.first();
        u = functionRange.last();
      } else if ((functionRange.isAST(S.GreaterEqual, 4) || functionRange.isAST(S.Greater, 4)) //
          && functionRange.second().isSymbol()) {
        u = functionRange.first();
        l = functionRange.last();
      } else if ((functionRange.isAST(S.LessEqual, 3) || functionRange.isAST(S.Less, 4)) //
          && functionRange.first().isSymbol()) {
        u = functionRange.second();
      } else if ((functionRange.isAST(S.GreaterEqual, 3) || functionRange.isAST(S.Greater, 4)) //
          && functionRange.first().isSymbol()) {
        l = functionRange.second();
      }

      if (l.isPresent()) {
        try {
          double xValue = engine.evalDouble(l);
          if (Double.isFinite(xValue)) {
            if (xValue < boundingbox[0]) { // min
              boundingbox[0] = xValue;
            }
          }
        } catch (RuntimeException rex) {
          //
        }
      }
      if (u.isPresent()) {
        try {
          double xValue = engine.evalDouble(u);
          if (Double.isFinite(xValue)) {
            if (xValue > boundingbox[2]) { // max
              boundingbox[2] = xValue;
            }
          }
        } catch (RuntimeException rex) {
          //
        }
      }
    }
  }

  private static void yBoundingBoxFunctionRange(
      EvalEngine engine, double[] boundingbox, IExpr functionRange) {
    if (functionRange.isPresent()) {
      IExpr l = F.NIL;
      IExpr u = F.NIL;
      if ((functionRange.isAST(S.LessEqual, 4) || functionRange.isAST(S.Less, 4)) //
          && functionRange.second().isSymbol()) {
        l = functionRange.first();
        u = functionRange.last();
      } else if ((functionRange.isAST(S.GreaterEqual, 4) || functionRange.isAST(S.Greater, 4)) //
          && functionRange.second().isSymbol()) {
        u = functionRange.first();
        l = functionRange.last();
      } else if ((functionRange.isAST(S.LessEqual, 3) || functionRange.isAST(S.Less, 4)) //
          && functionRange.first().isSymbol()) {
        u = functionRange.second();
      } else if ((functionRange.isAST(S.GreaterEqual, 3) || functionRange.isAST(S.Greater, 4)) //
          && functionRange.first().isSymbol()) {
        l = functionRange.second();
      }

      if (l.isPresent()) {
        try {
          double yValue = engine.evalDouble(l);
          if (Double.isFinite(yValue)) {
            if (yValue < boundingbox[3]) { // min
              boundingbox[3] = yValue;
            }
          }
        } catch (RuntimeException rex) {
          //
        }
      }
      if (u.isPresent()) {
        try {
          double yValue = engine.evalDouble(u);
          if (Double.isFinite(yValue)) {
            if (yValue > boundingbox[1]) { // max
              boundingbox[1] = yValue;
            }
          }
        } catch (RuntimeException rex) {
          //
        }
      }
    }
  }

  private static void yBoundingBox(EvalEngine engine, double[] boundingbox, IExpr yExpr) {
    try {
      double yValue = engine.evalDouble(yExpr);
      if (Double.isFinite(yValue)) {
        if (yValue > boundingbox[1]) {
          boundingbox[1] = yValue;
        }
        if (yValue < boundingbox[3]) {
          boundingbox[3] = yValue;
        }
      }
    } catch (RuntimeException rex) {
      //
    }
  }

  public static void initialize() {
    if (ToggleFeature.MANIPULATE) {
      Initializer.init();
    }
  }

  private ManipulateFunction() {}
}
