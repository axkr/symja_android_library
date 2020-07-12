package org.matheclipse.core.builtin;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;
import org.hipparchus.complex.Complex;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.stat.StatUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;

import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;
import tech.tablesaw.plotly.traces.BoxTrace;
import tech.tablesaw.plotly.traces.HeatmapTrace;
import tech.tablesaw.plotly.traces.Histogram2DTrace;
import tech.tablesaw.plotly.traces.Histogram2DTrace.Histogram2DBuilder;
import tech.tablesaw.plotly.traces.HistogramTrace;
import tech.tablesaw.plotly.traces.PieTrace;

public class ManipulateFunction {

	/**
	 * Default plot style colors for functions
	 */
	private final static java.awt.Color[] PLOT_COLORS = new java.awt.Color[] { //
			new java.awt.Color(0.368417f, 0.506779f, 0.709798f), //
			new java.awt.Color(0.880722f, 0.611041f, 0.142051f), //
			new java.awt.Color(0.560181f, 0.691569f, 0.194885f), //
			new java.awt.Color(0.922526f, 0.385626f, 0.209179f), //
			new java.awt.Color(0.528488f, 0.470624f, 0.701351f), //
			new java.awt.Color(0.772079f, 0.431554f, 0.102387f), //
			new java.awt.Color(0.363898f, 0.618501f, 0.782349f), //
			new java.awt.Color(1.0f, 0.75f, 0.0f), //
			new java.awt.Color(0.647624f, 0.37816f, 0.614037f), //
			new java.awt.Color(0.571589f, 0.586483f, 0.0f), //
			new java.awt.Color(0.915f, 0.3325f, 0.2125f), //
			new java.awt.Color(0.40082222609352647f, 0.5220066643438841f, 0.85f), //
			new java.awt.Color(0.9728288904374106f, 0.621644452187053f, 0.07336199581899142f), //
			new java.awt.Color(0.736782672705901f, 0.358f, 0.5030266573755369f), //
			new java.awt.Color(0.28026441037696703f, 0.715f, 0.4292089322474965f) //
	};

	private final static String JSXGRAPH = //
			"`1`\n" + //
					"`2`" + //
					"\n" + //
					"`3`" + //
					"\n" + //
					"";

	private final static String MATHCELL = //
			"MathCell( id, [ `1` ] );\n" + //
					"\n" + //
					"parent.update = function( id ) {\n" + //
					"\n" + //
					"`2`" + //
					"\n" + //
					"`3`" + //
					"\n" + //
					"`4`" + //
					"\n" + //
					"}";

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			if (Config.USE_MANIPULATE_JS) {
				F.BarChart.setEvaluator(new BarChart());
				F.BoxWhiskerChart.setEvaluator(new BoxWhiskerChart());
				F.ComplexPlot3D.setEvaluator(new ComplexPlot3D());
				F.ContourPlot.setEvaluator(new ContourPlot());
				F.DensityPlot.setEvaluator(new DensityPlot());
				F.DensityHistogram.setEvaluator(new DensityHistogram());
				F.Histogram.setEvaluator(new Histogram());
				F.PieChart.setEvaluator(new PieChart());
				F.Manipulate.setEvaluator(new Manipulate());
				F.MatrixPlot.setEvaluator(new MatrixPlot());
			}
		}
	}

	/**
	 * Chart methods which use <a href="https://github.com/paulmasson/mathcell">Mathcell Javascript</a>
	 *
	 */
	final static class Mathcell {

		private static IExpr plot(IAST plot, final IAST manipulateAST, EvalEngine engine) {
			// final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);
			JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1,
					JavaScriptFormFactory.USE_MATHCELL);
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

						if (manipulateAST.arg1().isAST(F.ListLinePlot)) {
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
					if (manipulateAST.arg1().isAST(F.ListLinePlot)) {
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
		 * <p>
		 * Convert the <code>plot</code> function into a JavaScript mathcell graphic control.
		 * </p>
		 * See: <a href="https://github.com/paulmasson/mathcell">github mathcell project</a>
		 * 
		 * @param plot
		 * @param plotRangeX
		 * @param plotRangeY
		 * @param manipulateAST
		 * 
		 * @return
		 * @throws IOException
		 */
		private static IExpr sliderWithPlot(IAST plot, IAST plotRangeX, IAST plotRangeY, final IAST manipulateAST,
				EvalEngine engine) {
			JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1,
					JavaScriptFormFactory.USE_MATHCELL);

			int plotID = plot.headID();
			final OptionArgs options;
			String colorMap = "hot";
			if (plotID == ID.Plot3D || //
					plotID == ID.ComplexPlot3D || //
					plotID == ID.ContourPlot || //
					plotID == ID.DensityPlot) {
				if (plotID == ID.Plot3D) {
					options = new OptionArgs(plot.topHead(), plot, 4, engine);
				} else {// if (plotID == ID.ComplexPlot3D) {
					options = new OptionArgs(plot.topHead(), plot, 3, engine);
				}
				IExpr colorFunction = options.getOption(F.ColorFunction);
				if (colorFunction == F.Automatic) {
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
						IOFunctions.printMessage(F.ColorData, "notent", F.List(F.ColorData, colorFunction), engine);
					}
				} else if (colorFunction.isPresent()) {
					// `2` is not a known entity, class, or tag for `1`.
					IOFunctions.printMessage(F.ColorData, "notent", F.List(F.ColorData, colorFunction), engine);
				}
			} else {
				options = new OptionArgs(plot.topHead(), plot, 3, engine);
			}
			IExpr plotRange = options.getOption(F.PlotRange);
			IAST optionPlotRange = F.NIL;
			if (plotRange.isPresent()) {
				if (plotRange.isAST(F.List, 3)) {
					optionPlotRange = F.List(F.Full, F.List(plotRange.first(), plotRange.second()));
				} else if (plotRange.isReal()) {
					if (plotID == ID.Plot) {
						optionPlotRange = F.List(F.Full, F.List(plotRange.negate(), plotRange));
					} else if (plotID == ID.ListPlot || plotID == ID.ListLinePlot) {
						optionPlotRange = F.List(F.Full, F.List(F.C0, plotRange));
					} else if (plotID == ID.PolarPlot) {
						optionPlotRange = F.List(F.List(plotRange.negate(), plotRange), //
								F.List(plotRange.negate(), plotRange));
					} else if (plotID == ID.ParametricPlot) {
						optionPlotRange = F.List(F.List(plotRange.negate(), plotRange), //
								F.List(plotRange.negate(), plotRange));
					}
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
				listOfFunctions = F.unaryAST1(F.List, plotFunction);
			}
			if (plotID == ID.Plot3D || //
					plotID == ID.ContourPlot || //
					plotID == ID.DensityPlot) {
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
			} else if (manipulateAST.arg1().isAST(F.ComplexPlot3D)) {
				if (plotRangeY.isPresent()) {
					return F.NIL;
				}
				for (int i = 1; i < listOfFunctions.size(); i++) {
					function.append("function z" + i + "(");
					toJS.convert(function, plotSymbolX);
					function.append(") { return  ");
					// toJS.convert(function, plotSymbolX);
					// function.append(", ");
					// toJS.convert(function, plotSymbolY);
					toJS.convert(function, listOfFunctions.get(i));
					function.append("; }\n");
				}
			} else {
				for (int i = 1; i < listOfFunctions.size(); i++) {
					function.append("function z");
					function.append(i);
					function.append("(");
					toJS.convert(function, plotSymbolX);
					function.append(") { return ");
					toJS.convert(function, listOfFunctions.get(i));
					function.append("; }\n");
				}

			}
			js = js.replace("`3`", function.toString());

			// plot( x => (Math.sin(x*(1+a*x))), [0, 2*Math.PI], { } )
			StringBuilder graphicControl = new StringBuilder();

			if (plotID == ID.ContourPlot || //
					plotID == ID.DensityPlot) {
				if (!plotRangeY.isPresent()) {
					return F.NIL;
				}
				contourPlot(listOfFunctions, plotRangeX, plotRangeY, graphicControl, plotID, toJS);
			} else if (plotID == ID.Plot3D) {
				if (!plotRangeY.isPresent()) {
					return F.NIL;
				}
				plot3D(listOfFunctions, plotRangeX, plotRangeY, graphicControl, colorMap, toJS);
			} else if (manipulateAST.arg1().isAST(F.ComplexPlot3D)) {
				if (plotRangeY.isPresent()) {
					return F.NIL;
				}
				complexPlot3D(listOfFunctions, plotRangeX, graphicControl, optionPlotRange, toJS);
			} else {
				if (plotID == ID.ParametricPlot || //
						plotID == ID.PolarPlot) {
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
					IExpr option = optionPlotRange.arg2();
					if (option.isAST(F.List, 3)) {
						plotRangeY = F.List(option.first(), option.second());
					}
				}
				if (optionPlotRange.isPresent() && optionPlotRange.second().isAST(F.List, 3)) {
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

		public static void contourPlot(IAST listOfFunctions, IAST plotRangeX, IAST plotRangeY,
				StringBuilder graphicControl, int plotID, JavaScriptFormFactory toJS) {
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

		public static void plot3D(IAST listOfFunctions, IAST plotRangeX, IAST plotRangeY, StringBuilder graphicControl,
				String colorMap, JavaScriptFormFactory toJS) {
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
				;
			}
			graphicControl.append("];\n");
		}

		public static void parametricPlot(IAST listOfFunctions, IAST plotRangeX, ISymbol plotSymbolX,
				StringBuilder graphicControl, JavaScriptFormFactory toJS) {
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

		public static void complexPlot3D(IAST listOfFunctions, IAST plotRangeX, StringBuilder graphicControl,
				IAST optionPlotRange, JavaScriptFormFactory toJS) {
			for (int i = 1; i < listOfFunctions.size(); i++) {
				graphicControl.append("var p" + i + " = ");
				graphicControl.append("parametric( (re,im) => [ re, im, z" + i + "(complex(re,im)) ]");

				ManipulateFunction.complexRange(graphicControl, plotRangeX, -1, toJS);

				graphicControl.append(", { complexFunction: 'abs', colormap: '");
				graphicControl.append("complexArgument");
				graphicControl.append("' } );\n");
			}
			graphicControl.append("\n  var config = { type: 'threejs'");
			if (optionPlotRange.isPresent() && optionPlotRange.second().isAST(F.List, 3)) {
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
		 * <p>
		 * Evaluate <code>Table( &lt;formula&gt;, &lt;sliderRange&gt; )</code>. If the result is a list, then convert
		 * this list in a JavaScript list of LaTeX formulas, which could be rendered with MathJAX.
		 * </p>
		 * See: <a href="https://github.com/paulmasson/mathcell/issues/1">github mathcell #1</a>
		 * 
		 * @param formula
		 *            the formula which should be evaluated into a table
		 * @param sliderRange
		 * @param stepExpr
		 * @param engine
		 * @return
		 * @throws IOException
		 */
		private static IExpr sliderWithFormulas(IExpr formula, IAST sliderRange, IExpr stepExpr, EvalEngine engine) {
			JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1,
					JavaScriptFormFactory.USE_MATHCELL);
			IExpr list = engine.evaluate(F.Table(formula, sliderRange));
			if (list.isNonEmptyList()) {
				IAST listOfFormulas = (IAST) list;
				String sliderSymbol = toJS.toString(sliderRange.arg1());
				String min = toJS.toString(sliderRange.arg2());
				String max = toJS.toString(sliderRange.arg3());
				String step = toJS.toString(stepExpr);
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

				graphicControl.append("  var data = '\\\\\\\\[' + expressions[n-");
				graphicControl.append(min);
				graphicControl.append("] + '\\\\\\\\]';\n\n");
				graphicControl.append("  data = data.replace( /\\\\\\\\/g, '&#92;' );\n\n");
				graphicControl.append("  var config = {type: 'text', center: true };\n\n");
				graphicControl.append("  evaluate( id, data, config );\n\n");
				graphicControl.append("  MathJax.Hub.Queue( [ 'Typeset', MathJax.Hub, id ] );\n");

				js = js.replace("`4`", graphicControl.toString());

				return F.JSFormData(js, "mathcell");
			}
			return F.NIL;
		}

		static boolean singleSlider(final IAST ast, int i, StringBuilder slider, StringBuilder variable,
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
					if (listOfButtons.get(j).isFalse() || //
							listOfButtons.get(j).isTrue()) {
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
	 *
	 */
	final static class JSXGraph {

		private static IExpr plot(IAST plot, final IAST manipulateAST, EvalEngine engine) {
			// final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);
			if (plot.size() < 2) {
				return F.NIL;
			}
			// xmin, ymax, xmax, ymin
			// double[] boundingbox = new double[] { -5.0, 5.0, 5.0, -5.0 };

			JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1,
					JavaScriptFormFactory.USE_MATHCELL);
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
				int[] dimension = pointList.isMatrix(false);
				if (dimension != null) {
					if (dimension[1] == 2) {
						return sequencePointListPlot(manipulateAST, pointList, toJS, engine);
					}
					return F.NIL;
				} else {
					return sequenceYValuesListPlot(manipulateAST, pointList, toJS, engine);
				}
			}
			return F.NIL;
		}

		/**
		 * <p>
		 * Convert the <code>plot</code> function into a JavaScript JSXGraph graphic control.
		 * </p>
		 * See: <a href="http://jsxgraph.uni-bayreuth.de">JSXGraph</a>
		 * 
		 * @param plot
		 * @param plotRangeX
		 * @param manipulateAST
		 * @param engine
		 *            the evaluation engine
		 * 
		 * @return
		 * @throws IOException
		 */
		private static IExpr sliderWithPlot(IAST plot, IAST plotRangeX, final IAST manipulateAST, EvalEngine engine) {
			int plotID = plot.headID();

			final OptionArgs options;
			if (plotID == ID.Plot3D || //
					plotID == ID.ComplexPlot3D || //
					plotID == ID.ContourPlot || //
					plotID == ID.DensityPlot) {
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
			if (plotRangeX.isAST(F.List, 4)) {
				try {
					plotRangeXMin = engine.evalDouble(plotRangeX.arg2());
					plotRangeXMax = engine.evalDouble(plotRangeX.arg3());
				} catch (RuntimeException rex) {
				}
			}
			IExpr option = options.getOption(F.PlotStyle);
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
			IExpr plotRangeY = options.getOption(F.PlotRange);
			// IAST optionPlotRange = F.NIL;
			if (plotRangeY.isPresent()) {
				if (plotRangeY.isAST(F.List, 3)) {
					try {
						if (plotRangeY.first().isAST(F.List, 3) && //
								plotRangeY.second().isAST(F.List, 3)) {
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

					} catch (RuntimeException rex) {
					}
					// optionPlotRange = F.List(F.Full, F.List(plotRange.first(), plotRange.second()));
				} else if (plotRangeY.isReal()) {
					if (plotID == ID.Plot) {
						try {
							plotRangeYMin = engine.evalDouble(plotRangeY.negate());
							plotRangeYMax = engine.evalDouble(plotRangeY);
						} catch (RuntimeException rex) {
						}
						// optionPlotRange = F.List(F.Full, F.List(plotRange.negate(), plotRange));
					} else if (plotID == ID.ParametricPlot || //
							plotID == ID.PolarPlot) {
						try {
							plotRangeYMin = engine.evalDouble(plotRangeY.negate());
							plotRangeYMax = engine.evalDouble(plotRangeY);
						} catch (RuntimeException rex) {
						}
						// optionPlotRange = F.List(F.List(plotRange.negate(), plotRange), //
						// F.List(plotRange.negate(), plotRange));
					}
				}
			}

			// xmin, ymax, xmax, ymin
			double[] boundingbox = new double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE,
					Double.MAX_VALUE };

			JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1,
					JavaScriptFormFactory.USE_MATHCELL);

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
			if ((plotID == ID.ParametricPlot || //
					plotID == ID.PolarPlot) && //
					plotRangeYMax != Double.MIN_VALUE && //
					plotRangeYMin != Double.MAX_VALUE) {
				try {
					plotRangeXMin = plotRangeYMin;
					plotRangeXMax = plotRangeYMax;
				} catch (RuntimeException rex) {
				}
			}

			// function z1(x,y) { return [ x, y, Math.sin( a * x * y ) ]; }
			StringBuilder function = new StringBuilder();

			// boundingbox = new double[] { 0.0, Double.MIN_VALUE, listOfFunctions.size(), Double.MAX_VALUE };
			if (plotID == ID.ParametricPlot) {
				return parametricPlot(plotRangeX, manipulateAST, engine, plotID, plotRangeYMax, plotRangeYMin,
						plotRangeXMax, plotRangeXMin, plotStyle, boundingbox, toJS, arg1, plotSymbolX, function);
			} else if (plotID == ID.PolarPlot) {
				return polarPlot(plotRangeX, manipulateAST, engine, plotID, plotRangeYMax, plotRangeYMin, plotRangeXMax,
						plotRangeXMin, plotStyle, boundingbox, toJS, arg1, plotSymbolX, function);
			}

			IAST listOfFunctions = (IAST) arg1;
			for (int i = 1; i < listOfFunctions.size(); i++) {
				function.append("function z");
				function.append(i);
				function.append("(");
				toJS.convert(function, plotSymbolX);
				function.append(") ");
				unaryJSFunction(toJS, function, listOfFunctions, i);
				ISymbol sym = F.Dummy("$z" + i);
				IExpr functionRange = F.FunctionRange.of(engine, listOfFunctions.get(i), plotSymbolX, sym);
				ManipulateFunction.yBoundingBoxFunctionRange(engine, boundingbox, functionRange);
			}

			for (int i = 1; i < listOfFunctions.size(); i++) {
				function.append("var p");
				function.append(i);
				function.append(" = ");
				function.append("board.create('functiongraph',[z");
				function.append(i);
				function.append(", ");
				JSXGraph.rangeArgs(function, plotRangeX, -1, toJS);
				function.append("]");
				java.awt.Color color = plotStyleColor(i, plotStyle, function);
				function.append(",{strokecolor:'");
				function.append(Convert.toHex(color));
				function.append("'}");
				function.append(");\n");
			}

			// listOfFunctions = (IAST) plot.arg1();
			function.append("var data = [ ");
			for (int i = 1; i < listOfFunctions.size(); i++) {
				function.append("p");
				function.append(i);
				if (i < listOfFunctions.size() - 1) {
					function.append(", ");
				}
			}
			function.append(" ];\n");
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
			return JSXGraph.boundingBox(manipulateAST, boundingbox, function.toString(), toJS, false, true);

		}

		private static void unaryJSFunction(JavaScriptFormFactory toJS, StringBuilder function, IAST listOfFunctions,
				int i) {
			function.append("{ try { return ");
			toJS.convert(function, listOfFunctions.get(i));
			function.append(";} catch(e) { return Number.NaN;} }\n");
		}

		private static IExpr parametricPlot(IAST plotRangeX, final IAST manipulateAST, EvalEngine engine, int plotID,
				double plotRangeYMax, double plotRangeYMin, double plotRangeXMax, double plotRangeXMin, IAST plotStyle,
				double[] boundingbox, JavaScriptFormFactory toJS, IExpr arg, ISymbol plotSymbolX,
				StringBuilder function) {
			int[] dim = arg.isMatrix(false);
			IAST list;
			if (dim == null) {
				int vectorDim = arg.isVector();
				if (vectorDim != 2) {
					return F.NIL;
				}
				list = F.List(arg);
			} else {
				if (dim[1] != 2) {
					return F.NIL;
				}
				list = (IAST) arg;
			}

			for (int i = 1; i < list.size(); i++) {
				IAST listOfFunctions = (IAST) list.get(i);
				for (int j = 1; j < listOfFunctions.size(); j++) {
					function.append("function z");
					function.append(i);
					function.append(j);
					function.append("(");
					toJS.convert(function, plotSymbolX);
					function.append(") ");
					unaryJSFunction(toJS, function, listOfFunctions, j);
					ISymbol sym = F.Dummy("$z" + i);
					IExpr functionRange = F.FunctionRange.of(engine, listOfFunctions.get(i), plotSymbolX, sym);
					if (j == 1) {
						xBoundingBoxFunctionRange(engine, boundingbox, functionRange);
					} else {
						yBoundingBoxFunctionRange(engine, boundingbox, functionRange);
					}
				}

				function.append("board.create('curve',[");
				for (int j = 1; j < listOfFunctions.size(); j++) {
					function.append("function(");
					toJS.convert(function, plotSymbolX);
					function.append("){return z");
					function.append(i);
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
				java.awt.Color color = plotStyleColor(i, plotStyle, function);
				function.append(",{strokecolor:'");
				function.append(Convert.toHex(color));
				function.append("'}");
				function.append(");\n");
			}
			// function.append(", { } );\n");

			if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeXMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)
					&& F.isFuzzyEquals(Double.MAX_VALUE, boundingbox[0], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
				boundingbox[0] = plotRangeXMin;
			}
			if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeYMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
				boundingbox[1] = plotRangeYMax;
			}
			if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeXMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)
					&& F.isFuzzyEquals(Double.MIN_VALUE, boundingbox[2], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
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

			return JSXGraph.boundingBox(manipulateAST, boundingbox, function.toString(), toJS, false, true);
		}

		private static IExpr polarPlot(IAST plotRangeX, final IAST manipulateAST, EvalEngine engine, int plotID,
				double plotRangeYMax, double plotRangeYMin, double plotRangeXMax, double plotRangeXMin, IAST plotStyle,
				double[] boundingbox, JavaScriptFormFactory toJS, IExpr arg1, ISymbol plotSymbolX,
				StringBuilder function) {
			IAST listOfFunctions = (IAST) arg1;
			int[] dim = arg1.isMatrix(false);
			if (dim != null) {
				if (dim[1] != 2) {
					return F.NIL;
				}
				listOfFunctions = (IAST) listOfFunctions.arg1();
			}

			for (int i = 1; i < listOfFunctions.size(); i++) {
				function.append("function z");
				function.append(i);
				function.append("(");
				toJS.convert(function, plotSymbolX);
				function.append(") ");
				unaryJSFunction(toJS, function, listOfFunctions, i);
				ISymbol sym = F.Dummy("$z" + i);
				IExpr functionRange = F.FunctionRange.of(engine, listOfFunctions.get(i), plotSymbolX, sym);
				if (i == 1) {
					ManipulateFunction.xBoundingBoxFunctionRange(engine, boundingbox, functionRange);
				} else {
					ManipulateFunction.yBoundingBoxFunctionRange(engine, boundingbox, functionRange);
				}
			}

			for (int i = 1; i < listOfFunctions.size(); i++) {
				function.append("board.create('curve', [");

				function.append("function(");
				toJS.convert(function, plotSymbolX);
				function.append("){return z");
				function.append(i);
				function.append("(");
				toJS.convert(function, plotSymbolX);
				function.append(");}");
				// if (i < listOfFunctions.size() - 1) {
				// function.append(",");
				// }

				// origin of polar plot
				function.append(",[0,0], ");

				toJS.convert(function, plotRangeX.arg2());
				function.append(", ");
				toJS.convert(function, plotRangeX.arg3());

				function.append("]");
				function.append(", {curveType:'polar'");

				java.awt.Color color = plotStyleColor(i, plotStyle, function);
				function.append(",strokeWidth:2, strokecolor:'");
				function.append(Convert.toHex(color));
				function.append("'");
				function.append("} );\n");
			}

			if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeXMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)
					&& F.isFuzzyEquals(Double.MAX_VALUE, boundingbox[0], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
				boundingbox[0] = plotRangeXMin;
			}
			if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeYMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
				boundingbox[1] = plotRangeYMax;
			}
			if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeXMax, Config.SPECIAL_FUNCTIONS_TOLERANCE)
					&& F.isFuzzyEquals(Double.MIN_VALUE, boundingbox[2], Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
				boundingbox[2] = plotRangeXMax;
			}
			if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeYMin, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
				boundingbox[3] = plotRangeYMin;
			}

			if (plotID == ID.PolarPlot) {
				if (-plotRangeXMax < boundingbox[0]) {
					boundingbox[0] = -plotRangeXMax;
				}
				if (plotRangeXMax > boundingbox[1]) {
					boundingbox[1] = plotRangeXMax;
				}
				if (boundingbox[2] < plotRangeXMax) {
					boundingbox[2] = plotRangeXMax;
				}
				if (-plotRangeXMax < boundingbox[3]) {
					boundingbox[3] = -plotRangeXMax;
				}
			}

			return JSXGraph.boundingBox(manipulateAST, boundingbox, function.toString(), toJS, false, true);
		}

		/**
		 * 
		 * @param functionNumber
		 *            the number of the function which should be plotted
		 * @param plotStyle
		 *            if present a <code>List()</code> is expected
		 * @param function
		 */
		private static java.awt.Color plotStyleColor(int functionNumber, IAST plotStyle, StringBuilder function) {
			if (plotStyle.isList() && plotStyle.size() > functionNumber) {
				IExpr temp = plotStyle.get(functionNumber);
				if (temp.isASTSizeGE(F.Directive, 2)) {
					IAST directive = (IAST) temp;
					for (int j = 1; j < directive.size(); j++) {
						temp = directive.get(j);
						java.awt.Color color = Convert.toAWTColor(temp);
						if (color != null) {
							return color;
						}
					}
				} else {
					java.awt.Color color = Convert.toAWTColor(temp);
					if (color != null) {
						return color;
					}
				}
			}
			return PLOT_COLORS[(functionNumber - 1) % PLOT_COLORS.length];
		}

		/**
		 * Create JSXGraph bounding box and sliders.
		 * 
		 * @param ast
		 *            from position 2 to size()-1 there maybe some <code>Manipulate</code> sliders defined
		 * @param boundingbox
		 *            an array of double values (length 4) which describes the bounding box
		 *            <code>[xMin, yMAx, xMax, yMin]</code>
		 * @param function
		 *            the generated JavaScript function
		 * @param toJS
		 *            the Symja to JavaScript converter factory
		 * @param fixedBounds
		 *            if <code>false</code> recalculate <code>boundingbox</code> min and max values
		 * @param axes
		 *            define <code>axes: true</code>
		 * @return
		 */
		private static IExpr boundingBox(IAST ast, double[] boundingbox, String function, JavaScriptFormFactory toJS,
				boolean fixedBounds, boolean axes) {
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
			double xPadding = (boundingbox[2] - boundingbox[0]) / 20;
			double yPadding = (boundingbox[1] - boundingbox[3]) / 20;
			boundingbox[0] = boundingbox[0] - xPadding;// xMin
			boundingbox[2] = boundingbox[2] + xPadding;// xMax
			boundingbox[1] = boundingbox[1] + yPadding;// yMax
			boundingbox[3] = boundingbox[3] - yPadding;// yMin

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

		private static void rangeArgs(StringBuilder graphicControl, IAST plotRange, int steps,
				JavaScriptFormFactory toJS) {
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
		 * @param xPos1Slider
		 *            x start position of slider
		 * @param xPos2Slider
		 *            x end position of slider
		 * @param yPosSlider
		 *            y position of slider
		 * @param toJS
		 *            the Symja to JavaScript converter factory
		 * @return <code>true</code> if successfully generated
		 */
		static boolean singleSlider(final IAST sliderRange, StringBuilder slider, double xPos1Slider,
				double xPos2Slider, double yPosSlider, JavaScriptFormFactory toJS) {

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
		 * @param sliderRange
		 *            a single <code>List(slider-name,...)</code> representing a slider definition
		 * @param toJS
		 *            the Symja to JavaScript converter factory
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
					;
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
		 * @param ast
		 *            from position 2 to size()-1 there maybe some <code>Manipulate</code> sliders defined
		 * @param toJS
		 *            the Symja to JavaScript converter factory
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
		 * @param ast
		 *            from position 2 to size()-1 there maybe some <code>Manipulate</code> sliders defined
		 * @param js
		 *            the JSXGraph JavaScript template
		 * @param boundingbox
		 *            an array of double values (length 4) which describes the bounding box
		 *            <code>[xMin, yMAx, xMax, yMin]</code>
		 * @param toJS
		 *            the Symja to JavaScript converter factory
		 * @return
		 */
		private static String slidersFromList(final IAST ast, String js, double[] boundingbox,
				JavaScriptFormFactory toJS) {
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
							if (!ManipulateFunction.JSXGraph.singleSlider((IAST) ast.getAST(i), slider, xPos1Slider,
									xPos2Slider, yPosSlider, toJS)) {
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
	 *
	 */
	private final static class Plotly {

		private static IExpr plot(IAST plot, final IAST manipulateAST, EvalEngine engine) {
			if (plot.size() < 2) {
				return F.NIL;
			}
			IExpr arg1 = plot.arg1();
			if (!arg1.isList()) {
				arg1 = engine.evaluate(arg1);
			}

			if (plot.isAST(F.DensityHistogram)) {
				return densityHistogram(arg1);
			} else if (plot.isAST(F.Histogram)) {
				return histogram(arg1);
			} else if (plot.isAST(F.BarChart)) {
				return barChart(arg1, plot, engine);
			} else if (plot.isAST(F.BoxWhiskerChart)) {
				return boxWhiskerChart(arg1);
			} else if (plot.isAST(F.PieChart)) {
				return pieChart(arg1);
			} else if (plot.isAST(F.MatrixPlot)) {
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
				String[] xStrs = new String[rowCount];
				final int colCount = matrix[0].length;
				for (int i = 0; i < colCount; i++) {
					xStrs[i] = Integer.toString(i + 1);
				}
				Layout layout = Layout.builder().autosize(true).build();
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

				Layout layout = Layout.builder().autosize(true).build();
				PieTrace trace = PieTrace.builder(strs, vector).build();
				Figure figure = new Figure(layout, trace);
				return F.JSFormData(figure.asJavascript("plotly"), "plotly");
			}
			return F.NIL;
		}

		private static IExpr boxWhiskerChart(IExpr arg) {
			double[] vector = arg.toDoubleVector();
			if (vector != null && vector.length > 0) {
				String[] strs = new String[vector.length];
				for (int i = 0; i < vector.length; i++) {
					strs[i] = Integer.toString(i + 1);
				}
				Layout layout = Layout.builder().autosize(true).build();
				BoxTrace trace = BoxTrace.builder(strs, vector).build();
				Figure figure = new Figure(layout, trace);
				return F.JSFormData(figure.asJavascript("plotly"), "plotly");
			}
			return F.NIL;
		}

		private static IExpr barChart(IExpr arg, IAST plot, EvalEngine engine) {
			double[] vector = arg.toDoubleVector();
			if (vector != null && vector.length > 0) {
				Orientation orientation = Orientation.VERTICAL;
				OptionArgs options = new OptionArgs(F.BarChart, plot, 2, engine);
				IExpr orientExpr = options.getOption(F.BarOrigin);
				if (orientExpr == F.Bottom) {
					orientation = Orientation.VERTICAL;
				} else if (orientExpr == F.Left) {
					orientation = Orientation.HORIZONTAL;
				}
				String[] strs = new String[vector.length];
				for (int i = 0; i < vector.length; i++) {
					strs[i] = Integer.toString(i + 1);
				}

				Layout layout = Layout.builder().autosize(true).build();
				// BarBuilder barBuilder = BarTrace.builder(strs, vector).orientation(Orientation.VERTICAL)

				BarTrace trace = BarTrace.builder(strs, vector).orientation(orientation).build();
				Figure figure = new Figure(layout, trace);
				return F.JSFormData(figure.asJavascript("plotly"), "plotly");
			}
			return F.NIL;
		}

		private static IExpr histogram(IExpr arg1) {
			double[] vector = arg1.toDoubleVectorIgnore();
			if (vector != null && vector.length > 0) {
				Layout layout = Layout.builder().autosize(true).build();// .title("Histogram").build();

				HistogramTrace trace = HistogramTrace.builder(vector).build();
				Figure figure = new Figure(layout, trace);
				// System.out.println(figure.asJavascript("plotly"));
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
						if (vector1 != null && vector1.length > 0 && //
								vector2 != null && vector2.length > 0) {
							Histogram2DBuilder builder = Histogram2DTrace.builder(vector1, vector2);
							// builder.opacity(opacity);

							Figure figure = new Figure(Layout.builder("Histogram", "x", "y").autosize(true).build(),
									builder.build());
							return F.JSFormData(figure.asJavascript("plotly"), "plotly");
						}
					}
				}
			}
			return F.NIL;
		}

	}

	private final static class BarChart extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}
	}

	private final static class BoxWhiskerChart extends AbstractEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}
	}

	private final static class ComplexPlot3D extends AbstractEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}
	}

	private final static class ContourPlot extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}

	}

	private final static class DensityPlot extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}

	}

	private final static class DensityHistogram extends AbstractEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}
	}

	private final static class Histogram extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}
	}

	private final static class MatrixPlot extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}

	}

	private final static class PieChart extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return redirectToManipulate(ast, engine);
		}

	}

	private static IExpr redirectToManipulate(final IAST ast, EvalEngine engine) {
		if (Config.USE_MANIPULATE_JS) {
			IExpr temp = F.Manipulate.of(engine, ast);
			if (temp.headID() == ID.JSFormData) {
				return temp;
			}
		}
		return F.NIL;
	}

	private static class Manipulate extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST manipulateAST, EvalEngine engine) {
			try {
				IExpr arg1 = manipulateAST.arg1();
				// if (arg1.isAST(F.ComplexPlot3D)) {
				// // `1` currently not supported in `2`.
				// return IOFunctions.printMessage(ast.topHead(), "unsupported",
				// F.List(F.ComplexPlot3D, F.stringx("Symja")), engine);
				// }

				if (arg1.isAST(F.BarChart) || //
						arg1.isAST(F.BoxWhiskerChart) || //
						arg1.isAST(F.DensityHistogram) || //
						arg1.isAST(F.Histogram) || //
						arg1.isAST(F.MatrixPlot) || //
						arg1.isAST(F.PieChart)) {
					return Plotly.plot((IAST) arg1, manipulateAST, engine);
				} else if (arg1.isAST(F.ListLinePlot) || //
						arg1.isAST(F.ListPlot)) {
					return JSXGraph.plot((IAST) arg1, manipulateAST, engine);
				} else if (arg1.isAST(F.ListPlot3D)) {
					return Mathcell.plot((IAST) arg1, manipulateAST, engine);
				} else if (arg1.isAST(F.Plot) || //
						arg1.isAST(F.ParametricPlot) || arg1.isAST(F.PolarPlot)) {
					IAST plot = (IAST) arg1;
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
									// assumption: button should be displayed
									return Mathcell.sliderWithPlot(plot, plotRangeX, plotRangeY, manipulateAST, engine);
								}
							}
						}

						if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
							// return mathcellSliderWithPlot(ast, plot, plotRangeX, plotRangeY, engine);
							return JSXGraph.sliderWithPlot(plot, plotRangeX, manipulateAST, engine);
						}

					}
				} else if (arg1.isAST(F.Plot3D) || //
						arg1.isAST(F.ComplexPlot3D) || //
						arg1.isAST(F.ContourPlot) || //
						arg1.isAST(F.DensityPlot)) {
					IAST plot = (IAST) arg1;
					if (plot.size() >= 3 && plot.arg2().isList()) {
						IAST plotRangeX = (IAST) plot.arg2();
						// TODO find better default Y plot range instead of [-5, 5]
						IAST plotRangeY = F.NIL;
						if (plot.size() >= 4 && plot.arg3().isList()) {
							plotRangeY = (IAST) plot.arg3();
						}
						if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
							return Mathcell.sliderWithPlot(plot, plotRangeX, plotRangeY, manipulateAST, engine);
						}
					}
				} else if (manipulateAST.isAST2() && manipulateAST.arg2().isList()) {
					IExpr formula = arg1;
					IAST sliderRange = (IAST) manipulateAST.arg2();
					IExpr step = F.C1;
					if (sliderRange.size() == 4 || sliderRange.size() == 5) {
						if (sliderRange.size() == 5) {
							step = sliderRange.arg4();
						}
						if (sliderRange.arg1().isSymbol()) {
							return Mathcell.sliderWithFormulas(formula, sliderRange, step, engine);
						}
					}
				}
			} catch (Exception ex) {
				if (FEConfig.SHOW_STACKTRACE) {
					ex.printStackTrace();
				}
				return IOFunctions.printMessage(F.Manipulate, ex, engine);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_INFINITY;
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
	 * @param plotRange
	 *            example <code>{x, -2, 2}</code>
	 * @param steps
	 *            an additional step parameter. If less <code>0</code> the parameter will be ignored
	 * @param toJS
	 *            the expression to JavaScript transpiler
	 */
	private static void realRange(StringBuilder graphicControl, IAST plotRange, int steps, JavaScriptFormFactory toJS) {
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
	 * @param plotRange
	 *            example <code>{z, -2-2*I, 2+2*I}</code>
	 * @param steps
	 *            an additional step parameter. If less <code>0</code> the parameter will be ignored
	 * @param toJS
	 *            the expression to JavaScript transpiler
	 */
	private static void complexRange(StringBuilder graphicControl, IAST plotRange, int steps,
			JavaScriptFormFactory toJS) {
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
	}

	/**
	 * Plot a list of 2D points.
	 * 
	 * @param ast
	 * @param pointList
	 * @param toJS
	 * @param engine
	 * @return
	 */
	private static IExpr sequencePointListPlot(final IAST ast, IAST pointList, JavaScriptFormFactory toJS,
			EvalEngine engine) {
		double[] boundingbox;
		// plot a list of 2D points
		StringBuilder function = new StringBuilder();
		boundingbox = new double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE };
		if (ast.arg1().isAST(F.ListLinePlot) && pointList.size() > 2) {
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
							function.append(" {name:'', face:'o', size: 2 } );\n");
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
						function.append("]]");
						function.append(", {straightFirst:false, straightLast:false, strokeWidth:2});\n");
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
				function.append(" {name:'', face:'o', size: 2 } );\n");
			}
		}
		return JSXGraph.boundingBox(ast, boundingbox, function.toString(), toJS, false, true);
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
	private static IExpr sequenceYValuesListPlot(final IAST ast, IAST pointList, JavaScriptFormFactory toJS,
			EvalEngine engine) {
		double[] boundingbox;

		StringBuilder function = new StringBuilder();
		boundingbox = new double[] { 0.0, Double.MIN_VALUE, pointList.size(), Double.MAX_VALUE };
		if (ast.arg1().isAST(F.ListLinePlot)) {
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
							function.append("], ");
							function.append(" {name:'', face:'o', size: 2 } );\n");
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
						function.append("]]");
						function.append(", {straightFirst:false, straightLast:false, strokeWidth:2});\n");
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
				function.append(" {name:'', face:'o', size: 2 } );\n");
			}
		}

		return JSXGraph.boundingBox(ast, boundingbox, function.toString(), toJS, false, true);
	}

	private static boolean isNonReal(IExpr lastPoint) {
		return lastPoint == F.Indeterminate || //
				lastPoint == F.None || //
				lastPoint.isAST(F.Missing);
	}

	private static boolean isNonReal(IExpr lastPointX, IExpr lastPointY) {
		return isNonReal(lastPointX) || //
				isNonReal(lastPointY);
	}

	private static int[] calcHistogram(double[] data, double min, double max, int numBins) {
		final int[] result = new int[numBins];
		final double binSize = (max - min) / numBins;

		for (double d : data) {
			int bin = (int) ((d - min) / binSize);
			if (bin < 0) {
				/* this data is smaller than min */
			} else if (bin >= numBins) {
				/* this data point is bigger than max */
			} else {
				result[bin] += 1;
			}
		}
		return result;
	}

	private static IExpr sequenceBarChart(final IAST ast, IAST pointList, JavaScriptFormFactory toJS,
			EvalEngine engine) {
		double[] boundingbox;

		StringBuilder function = new StringBuilder();
		boundingbox = new double[] { 0.0, 0.0, pointList.size() - 0.5, 0.0 };

		if (ast.arg1().isAST(F.Histogram)) {
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

			boundingbox = new double[] { min, 0.0, max, 0.0 };
			for (int i = 0; i < buckets.length; i++) {
				IInteger value = F.ZZ(buckets[i]);
				toJS.convert(function, value);
				yBoundingBox(engine, boundingbox, value);
				if (i < buckets.length - 1) {
					function.append(",");
				}
			}
			function.append("];\n");
		} else if (ast.arg1().isAST(F.BarChart)) {
			function.append("var dataArr = [");
			boundingbox = new double[] { 0.0, 0.0, pointList.size() - 0.5, 0.0 };
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
		if (ast.arg1().isAST(F.Histogram)) {
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

	private static void xBoundingBoxFunctionRange(EvalEngine engine, double[] boundingbox, IExpr functionRange) {
		IExpr l = F.NIL;
		IExpr u = F.NIL;
		if ((functionRange.isAST(F.LessEqual, 4) || functionRange.isAST(F.Less, 4)) //
				&& functionRange.second().isSymbol()) {
			l = functionRange.getAt(1);
			u = functionRange.getAt(3);
		} else if ((functionRange.isAST(F.GreaterEqual, 4) || functionRange.isAST(F.Greater, 4)) //
				&& functionRange.second().isSymbol()) {
			u = functionRange.getAt(1);
			l = functionRange.getAt(3);
		} else if ((functionRange.isAST(F.LessEqual, 3) || functionRange.isAST(F.Less, 4)) //
				&& functionRange.first().isSymbol()) {
			u = functionRange.second();
		} else if ((functionRange.isAST(F.GreaterEqual, 3) || functionRange.isAST(F.Greater, 4)) //
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

	private static void yBoundingBoxFunctionRange(EvalEngine engine, double[] boundingbox, IExpr functionRange) {
		IExpr l = F.NIL;
		IExpr u = F.NIL;
		if ((functionRange.isAST(F.LessEqual, 4) || functionRange.isAST(F.Less, 4)) //
				&& functionRange.second().isSymbol()) {
			l = functionRange.getAt(1);
			u = functionRange.getAt(3);
		} else if ((functionRange.isAST(F.GreaterEqual, 4) || functionRange.isAST(F.Greater, 4)) //
				&& functionRange.second().isSymbol()) {
			u = functionRange.getAt(1);
			l = functionRange.getAt(3);
		} else if ((functionRange.isAST(F.LessEqual, 3) || functionRange.isAST(F.Less, 4)) //
				&& functionRange.first().isSymbol()) {
			u = functionRange.second();
		} else if ((functionRange.isAST(F.GreaterEqual, 3) || functionRange.isAST(F.Greater, 4)) //
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

	private ManipulateFunction() {

	}

}
