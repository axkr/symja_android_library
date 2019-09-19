package org.matheclipse.core.builtin;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ManipulateFunction {
	private final static String JSXGRAPH = //
			"`1`\n" + //
					"`2`" + //
					"\n" + //
					"`3`" + //
					"\n" + //
					"`4`" + //
					"\n" + //
					"";

	private final static String MATHCELL = //
			"MathCell( id, [ `1` ] );\n" + //
					"\n" + //
					"parent.update = function( id ) {\n" + //
					"\n" + //
					// " var phase = document.getElementById( id + 'phase' ).value;\n" + //
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
			if (Config.USE_JSXGRAPH) {
				F.Manipulate.setEvaluator(new ManipulateJSXGraph());
			}
			if (Config.USE_MATHCELL) {
				F.Manipulate.setEvaluator(new ManipulateMathCell());
			}
		}
	}

	private static class ManipulateJSXGraph extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.arg1().isAST(F.ListLinePlot) || //
						ast.arg1().isAST(F.ListPlot) || //
						ast.arg1().isAST(F.ListPlot3D)) {
					IAST plot = (IAST) ast.arg1();
					return createSliderWithListPlot(ast, plot, engine);
				} else if (ast.arg1().isAST(F.Plot) || //
						ast.arg1().isAST(F.ParametricPlot) || //
						ast.arg1().isAST(F.Plot3D)) {
					IAST plot = (IAST) ast.arg1();
					if (plot.size() >= 3 && plot.arg2().isList()) {
						IAST plotRangeX = (IAST) plot.arg2();
						// TODO find better default Y plot range instead of [-5, 5]
						IAST plotRangeY = F.List(F.CN5, F.C5);
						if (plot.size() >= 4 && plot.arg3().isList()) {
							plotRangeY = (IAST) plot.arg3();
						}
						if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
							return createSliderWithPlot(ast, plot, plotRangeX, plotRangeY, engine);
						}
					}
					// } else if (ast.arg2().isList() && ast.isAST2()) {
					// IExpr formula = ast.arg1();
					// IAST sliderRange = (IAST) ast.arg2();
					// IExpr step = F.C1;
					// if (sliderRange.size() == 4 || sliderRange.size() == 5) {
					// if (sliderRange.size() == 5) {
					// step = sliderRange.arg4();
					// }
					// if (sliderRange.arg1().isSymbol()) {
					// return createSliderWithFormulas(formula, sliderRange, step, engine);
					// }
					// }
				}
			} catch (IOException ioex) {
				if (Config.SHOW_STACKTRACE) {
					ioex.printStackTrace();
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
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
		 * @param ast
		 * @param plot
		 * @param plotRangeX
		 * @param plotRangeY
		 * @return
		 * @throws IOException
		 */
		private static IExpr createSliderWithPlot(final IAST ast, IAST plot, IAST plotRangeX, IAST plotRangeY,
				EvalEngine engine) throws IOException {
			int plotID = plot.headID();

			final OptionArgs options;
			if (plotID == ID.Plot3D) {
				options = new OptionArgs(plot.topHead(), plot, 4, engine);
				// } else if (plotID == ID.Plot) {
				// options = new OptionArgs(plot.topHead(), plot, 3, engine);
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
					} else if (plotID == ID.ParametricPlot) {
						optionPlotRange = F.List(F.List(plotRange.negate(), plotRange), //
								F.List(plotRange.negate(), plotRange));
					}
				}
			}

			String js = JSXGRAPH;
			List<String> sliderNames = new ArrayList<String>();
			js = createSlidersFromList(ast, js, sliderNames);
			if (js == null) {
				return F.NIL;
			}

			ISymbol plotSymbolX = (ISymbol) plotRangeX.arg1();

			// function z1(x,y) { return [ x, y, Math.sin( a * x * y ) ]; }
			StringBuilder function = new StringBuilder();

			if (plotID == ID.Plot3D) {
				function.append("function z1(");
				ISymbol plotSymbolY = (ISymbol) plotRangeY.arg1();
				function.append(OutputFunctions.toJavaScript(plotSymbolX));
				function.append(",");
				function.append(OutputFunctions.toJavaScript(plotSymbolY));
				function.append(") { return [ ");
				function.append(OutputFunctions.toJavaScript(plotSymbolX));
				function.append(", ");
				function.append(OutputFunctions.toJavaScript(plotSymbolY));
				function.append(", ");
				function.append(OutputFunctions.toJavaScript(plot.arg1(), sliderNames));
				function.append(" ]; }\n");
			} else {
				if (plot.arg1().isList()) {
					IAST listOfFunctions = (IAST) plot.arg1();
					for (int i = 1; i < listOfFunctions.size(); i++) {
						function.append("function z");
						function.append(i);
						function.append("(");
						function.append(OutputFunctions.toJavaScript(plotSymbolX));
						function.append(") { return ");
						function.append(OutputFunctions.toJavaScript(listOfFunctions.get(i), sliderNames));
						function.append("; }\n");
					}
				} else {
					function.append("function z1(");
					function.append(OutputFunctions.toJavaScript(plotSymbolX));
					function.append(") { return ");
					function.append(OutputFunctions.toJavaScript(plot.arg1(), sliderNames));
					function.append("; }\n");
				}
			}
			js = js.replace("`3`", function.toString());

			// plot( x => (Math.sin(x*(1+a*x))), [0, 2*Math.PI], { } )
			StringBuilder graphicControl = new StringBuilder();

			if (plotID == ID.Plot3D) {
				if (!plotRangeY.isPresent()) {
					return F.NIL;
				}
				graphicControl.append("var p1 = ");
				graphicControl.append("parametric( z1, ");
				range(graphicControl, plotRangeX, -1);
				graphicControl.append(", ");
				range(graphicControl, plotRangeY, -1);
				graphicControl.append(", { colormap: (x,y) => ( 1 - Math.sin(x*y) ) / 2 } );\n\n\n");

				graphicControl.append("  var config = { type: 'threejs' };\n");
				graphicControl.append("  var data = [ p1 ];\n");
			} else {
				if (plot.arg1().isList()) {
					IAST listOfFunctions = (IAST) plot.arg1();
					if (plotID == ID.ParametricPlot) {
						graphicControl.append("board.create('curve',[");
						// graphicControl.append(OutputFunctions.toJavaScript(plotSymbolX));
						// graphicControl.append(" => [");
						for (int i = 1; i < listOfFunctions.size(); i++) {
							graphicControl.append("function(t){return z");
							graphicControl.append(i);
							graphicControl.append("(");
							graphicControl.append(OutputFunctions.toJavaScript(plotSymbolX));
							graphicControl.append(");}");
							if (i < listOfFunctions.size() - 1) {
								graphicControl.append(",");
							}
						}
						graphicControl.append(", ");
						pureRange(graphicControl, plotRangeX, -1);
						graphicControl.append("]");
						graphicControl.append(", { } );\n");
					} else {
						for (int i = 1; i < listOfFunctions.size(); i++) {
							graphicControl.append("var p");
							graphicControl.append(i);
							graphicControl.append(" = ");
							graphicControl.append("board.create('functiongraph',[z");
							graphicControl.append(i);
							graphicControl.append(",0,1], {strokeWidth:2});");
							graphicControl.append(", ");
							range(graphicControl, plotRangeX, -1);
							graphicControl.append(", { } );\n");
						}

						// var data = [ p1, p2 ];
						if (plot.arg1().isList()) {
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
						} else {
							graphicControl.append("var data = [ p1 ];\n");
						}

					}
					// graphicControl.append("var config = { type: 'svg' };\n");
				} else {
					graphicControl.append("var p1 = ");
					graphicControl.append("board.create('functiongraph',[z1,0,1], {strokeWidth:2});");
				}

			}

			js = js.replace("`4`", graphicControl.toString());

			return F.JSFormData(js, "jsxgraph");
		}

		private static String createSlidersFromList(final IAST ast, String js, List<String> sliderNames)
				throws IOException {
			if (ast.size() >= 3) {
				if (ast.arg2().isList()) {
					// { type: 'slider', min: 0, max: 2*Math.PI, name: 'phase', label: 'phase' }
					StringBuilder slider = new StringBuilder();
					// var a = document.getElementById( id + 'a' ).value;
					StringBuilder variable = new StringBuilder();
					for (int i = 2; i < ast.size(); i++) {
						if (ast.get(i).isList()) {
							if (!createSingleSlider(ast, i, slider, variable, sliderNames)) {
								return null;
							}
						} else {
							break;
						}
					}
					js = js.replace("`1`", slider.toString());
					js = js.replace("`2`", variable.toString());
				}
			} else {
				js = js.replace("`1`", "");
				js = js.replace("`2`", "");
			}
			return js;
		}

		private static IExpr createSliderWithListPlot(final IAST ast, IAST plot, EvalEngine engine) throws IOException {
			// final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);

			String js = JSXGRAPH;
			List<String> sliderNames = new ArrayList<String>();
			js = createSlidersFromList(ast, js, sliderNames);
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
				int[] dimension = pointList.isMatrix();
				if (dimension != null) {
					if (dimension[1] == 2) {
						StringBuilder function = new StringBuilder();

						if (ast.arg1().isAST(F.ListLinePlot) && pointList.size() > 2) {
							IAST lastList = (IAST) pointList.arg1();
							for (int i = 2; i < pointList.size(); i++) {
								function.append("board.create('line',");
								IAST rowList = (IAST) pointList.get(i);
								function.append("[[");
								function.append(OutputFunctions.toJavaScript(lastList.arg1(), sliderNames));
								function.append(",");
								function.append(OutputFunctions.toJavaScript(lastList.arg2(), sliderNames));
								function.append("],");
								function.append("[");
								function.append(OutputFunctions.toJavaScript(rowList.arg1(), sliderNames));
								function.append(",");
								function.append(OutputFunctions.toJavaScript(rowList.arg2(), sliderNames));
								function.append("]]");
								function.append(", {straightFirst:false, straightLast:false, strokeWidth:2});\n");
								lastList = rowList;
							}
						} else {
							for (int i = 1; i < pointList.size(); i++) {
								IAST rowList = (IAST) pointList.get(i);
								function.append("board.create('point', [");
								function.append(OutputFunctions.toJavaScript(rowList.arg1(), sliderNames));
								function.append(",");
								function.append(OutputFunctions.toJavaScript(rowList.arg2(), sliderNames));
								function.append("], ");
								function.append(" {name:'', face:'o', size: 2 } );\n");
							}
						}

						js = js.replace("`3`", function.toString());

						StringBuilder graphicControl = new StringBuilder();
						js = js.replace("`4`", graphicControl.toString());

						return F.JSFormData(js, "jsxgraph");
					}
					// if (dimension[1] == 3) {
					// StringBuilder function = new StringBuilder();
					//
					// function.append("var data = [\n");
					// // point( [ x, y, z ],
					// // { color: 'hsl(' + 360*Math.random() + ',100%,50%)', size: 5 } )
					// for (int i = 1; i < pointList.size(); i++) {
					// IAST rowList = (IAST) pointList.get(i);
					// function.append("point( [ ");
					// function.append(OutputFunctions.toJavaScript(rowList.arg1()));
					// function.append(",");
					// function.append(OutputFunctions.toJavaScript(rowList.arg2()));
					// function.append(",");
					// function.append(OutputFunctions.toJavaScript(rowList.arg3()));
					// function.append("], ");
					// function.append(" {size: 2 } )");
					// if (i < pointList.size() - 1) {
					// function.append(",");
					// }
					// function.append("\n");
					// }
					// function.append("];");
					//
					// js = js.replace("`3`", function.toString());
					//
					// StringBuilder graphicControl = new StringBuilder();
					//
					// // var config = dim === 'two' ? { type: 'svg', ticks: false }
					// // : { type: 'threejs', axesLabels: false };
					// graphicControl.append("var config = { type: 'threejs' };\n");
					// graphicControl.append("evaluate( id, data, config );\n");
					// js = js.replace("`4`", graphicControl.toString());
					//
					// return F.JSFormData(js, "jsxgraph");
					// }
					return F.NIL;
				} else {
					// StringBuilder function = new StringBuilder();
					// if (ast.arg1().isAST(F.ListLinePlot)) {
					// function.append("var data = [ listPlot( [\n");
					// for (int i = 1; i < pointList.size(); i++) {
					// function.append("[ ");
					// function.append(i);
					// function.append(",");
					// function.append(OutputFunctions.toJavaScript(pointList.get(i)));
					// function.append("] ");
					// if (i < pointList.size() - 1) {
					// function.append(",");
					// }
					// function.append("\n");
					// }
					// function.append("], { })];");
					// } else {
					// function.append("var data = [\n");
					// // point( [ 2*Math.random() - 1, 2*Math.random() - 1, 2*Math.random() - 1 ],
					// // { color: 'hsl(' + 360*Math.random() + ',100%,50%)', size: 5 } )
					// for (int i = 1; i < pointList.size(); i++) {
					// function.append("point( [ ");
					// function.append(i);
					// function.append(",");
					// function.append(OutputFunctions.toJavaScript(pointList.get(i)));
					// function.append("], ");
					// function.append(" {size: 2 } )");
					// if (i < pointList.size() - 1) {
					// function.append(",");
					// }
					// function.append("\n");
					// }
					// function.append("];");
					// }
					//
					// js = js.replace("`3`", function.toString());
					//
					// StringBuilder graphicControl = new StringBuilder();
					//
					// // var config = dim === 'two' ? { type: 'svg', ticks: false }
					// // : { type: 'threejs', axesLabels: false };
					// graphicControl.append("var config = { type: 'svg' };\n");
					// graphicControl.append("evaluate( id, data, config );\n");
					// js = js.replace("`4`", graphicControl.toString());
					//
					// return F.JSFormData(js, "jsxgraph");
				}
			}
			return F.NIL;
		}

		private static boolean createSingleSlider(final IAST ast, int i, StringBuilder slider, StringBuilder variable,
				List<String> sliderNames) throws IOException {
			IAST sliderRange = (IAST) ast.get(i);
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
					sliderSymbol = OutputFunctions.toJavaScript(sliderParameters.arg1());
					sliderNames.add(sliderSymbol);
					defaultValue = OutputFunctions.toJavaScript(sliderRange.arg2());
					label = OutputFunctions.toJavaScript(sliderParameters.arg3());
				} else {
					sliderSymbol = OutputFunctions.toJavaScript(sliderRange.arg1());
					label = sliderSymbol;
					sliderNames.add(sliderSymbol);
				}
				if (i > 2) {
					slider.append(", ");
				}
				slider.append("var ");
				slider.append(sliderSymbol);
				slider.append(" = board.create('slider',");

				slider.append("[[0.1,1.5],[1.1,1.5],[1,");
				slider.append(OutputFunctions.toJavaScript(sliderRange.arg2()));
				slider.append(",");
				slider.append(OutputFunctions.toJavaScript(sliderRange.arg3()));
				slider.append("]],");
				// if (step != null) {
				// slider.append(", step: ");
				// slider.append(OutputFunctions.toJavaScript(step));
				// }
				// if (defaultValue != null) {
				// slider.append(", default: ");
				// slider.append(defaultValue);
				// }
				slider.append("{name:'");
				slider.append(sliderSymbol);

				slider.append("',snapWidth:1});\n");

				return true;
			}
			return false;
		}

		/**
		 * <p>
		 * Evaluate <code>Table( &lt;formula&gt;, &lt;sliderRange&gt; )</code>. If the result is a list, then convert
		 * this list in a JavaScript list of LaTeX formulas, which could be rendered with MathJAX.
		 * </p>
		 * 
		 * @param formula
		 *            the formula which should be evaluated into a table
		 * @param sliderRange
		 * @param stepExpr
		 * @param engine
		 * @return
		 * @throws IOException
		 */
		// private static IExpr createSliderWithFormulas(IExpr formula, IAST sliderRange, IExpr stepExpr,
		// EvalEngine engine) throws IOException {
		// IExpr list = engine.evaluate(F.Table(formula, sliderRange));
		// if (list.isList() && list.size() > 1) {
		// IAST listOfFormulas = (IAST) list;
		// String sliderSymbol = OutputFunctions.toJavaScript(sliderRange.arg1());
		// String min = OutputFunctions.toJavaScript(sliderRange.arg2());
		// String max = OutputFunctions.toJavaScript(sliderRange.arg3());
		// String step = OutputFunctions.toJavaScript(stepExpr);
		// String js = JSXGRAPH;
		// // { type: 'slider', min: 1, max: 5, step: 1, name: 'n', label: 'n' }
		// StringBuilder slider = new StringBuilder();
		// slider.append("{ type: 'slider', min: ");
		// slider.append(min);
		// slider.append(", max: ");
		// slider.append(max);
		// slider.append(", step: ");
		// slider.append(step);
		// slider.append(", name: '");
		// slider.append(sliderSymbol);
		// slider.append("', label: '");
		// slider.append(sliderSymbol);
		// slider.append("' }\n");
		// js = js.replace("`1`", slider.toString());
		//
		// StringBuilder variable = new StringBuilder();
		// variable.append("var ");
		// variable.append(sliderSymbol);
		//
		// // variable.append(" = document.getElementById( id + '");
		// variable.append(" = getVariable(id, '");
		// variable.append(sliderSymbol);
		// // variable.append("' ).value;\n");
		// variable.append("');\n");
		//
		// js = js.replace("`2`", variable.toString());
		//
		// js = js.replace("`3`", "");
		//
		// TeXUtilities texUtil = new TeXUtilities(engine, true);
		// StringBuilder graphicControl = new StringBuilder();
		// graphicControl.append("var expressions = [ ");
		// for (int i = 1; i < listOfFormulas.size(); i++) {
		// StringWriter stw = new StringWriter();
		// texUtil.toTeX(listOfFormulas.get(i), stw);
		// graphicControl.append("'");
		// String texForm = stw.toString();
		// // TODO implement better backslash escaping
		// texForm = texForm.replace("\\", "\\\\\\\\");
		// graphicControl.append(texForm);
		// graphicControl.append("'");
		// if (i < listOfFormulas.size() - 1) {
		// graphicControl.append(",\n");
		// }
		// }
		// graphicControl.append(" ];\n\n");
		//
		// graphicControl.append(" var data = '\\\\\\\\[' + expressions[n-");
		// graphicControl.append(min);
		// graphicControl.append("] + '\\\\\\\\]';\n\n");
		// graphicControl.append(" data = data.replace( /\\\\\\\\/g, '&#92;' );\n\n");
		// graphicControl.append(" var config = {type: 'text', center: true };\n\n");
		// graphicControl.append(" evaluate( id, data, config );\n\n");
		// graphicControl.append(" MathJax.Hub.Queue( [ 'Typeset', MathJax.Hub, id ] );\n");
		//
		// js = js.replace("`4`", graphicControl.toString());
		//
		// return F.JSFormData(js, "jsxgraph");
		// }
		// return F.NIL;
		// }

		private static void pureRange(StringBuilder graphicControl, IAST plotRange, int steps) throws IOException {
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg2()));
			graphicControl.append(", ");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg3()));
			if (steps > 0) {
				graphicControl.append(", ");
				graphicControl.append(steps);
			}
		}

		private static void range(StringBuilder graphicControl, IAST plotRange, int steps) throws IOException {
			graphicControl.append("[");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg2()));
			graphicControl.append(", ");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg3()));
			if (steps > 0) {
				graphicControl.append(", ");
				graphicControl.append(steps);
			}
			graphicControl.append("]");
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_INFINITY;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class ManipulateMathCell extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.arg1().isAST(F.ListLinePlot) || //
						ast.arg1().isAST(F.ListPlot) || //
						ast.arg1().isAST(F.ListPlot3D)) {
					IAST plot = (IAST) ast.arg1();
					return createSliderWithListPlot(ast, plot, engine);
				} else if (ast.arg1().isAST(F.Plot) || //
						ast.arg1().isAST(F.ParametricPlot) || //
						ast.arg1().isAST(F.Plot3D)) {
					IAST plot = (IAST) ast.arg1();
					if (plot.size() >= 3 && plot.arg2().isList()) {
						IAST plotRangeX = (IAST) plot.arg2();
						// TODO find better default Y plot range instead of [-5, 5]
						IAST plotRangeY = F.List(F.CN5, F.C5);
						if (plot.size() >= 4 && plot.arg3().isList()) {
							plotRangeY = (IAST) plot.arg3();
						}
						if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
							return createSliderWithPlot(ast, plot, plotRangeX, plotRangeY, engine);
						}
					}
				} else if (ast.arg2().isList() && ast.isAST2()) {
					IExpr formula = ast.arg1();
					IAST sliderRange = (IAST) ast.arg2();
					IExpr step = F.C1;
					if (sliderRange.size() == 4 || sliderRange.size() == 5) {
						if (sliderRange.size() == 5) {
							step = sliderRange.arg4();
						}
						if (sliderRange.arg1().isSymbol()) {
							return createSliderWithFormulas(formula, sliderRange, step, engine);
						}
					}
				}
			} catch (IOException ioex) {
				if (Config.SHOW_STACKTRACE) {
					ioex.printStackTrace();
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
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
		 * @param ast
		 * @param plot
		 * @param plotRangeX
		 * @param plotRangeY
		 * @return
		 * @throws IOException
		 */
		private static IExpr createSliderWithPlot(final IAST ast, IAST plot, IAST plotRangeX, IAST plotRangeY,
				EvalEngine engine) throws IOException {
			int plotID = plot.headID();

			final OptionArgs options;
			if (plotID == ID.Plot3D) {
				options = new OptionArgs(plot.topHead(), plot, 4, engine);
				// } else if (plotID == ID.Plot) {
				// options = new OptionArgs(plot.topHead(), plot, 3, engine);
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
					} else if (plotID == ID.ParametricPlot) {
						optionPlotRange = F.List(F.List(plotRange.negate(), plotRange), //
								F.List(plotRange.negate(), plotRange));
					}
				}
			}

			String js = MATHCELL;
			js = createSlidersFromList(ast, js);
			if (js == null) {
				return F.NIL;
			}

			ISymbol plotSymbolX = (ISymbol) plotRangeX.arg1();

			// function z1(x,y) { return [ x, y, Math.sin( a * x * y ) ]; }
			StringBuilder function = new StringBuilder();

			if (plotID == ID.Plot3D) {
				function.append("function z1(");
				ISymbol plotSymbolY = (ISymbol) plotRangeY.arg1();
				function.append(OutputFunctions.toJavaScript(plotSymbolX));
				function.append(",");
				function.append(OutputFunctions.toJavaScript(plotSymbolY));
				function.append(") { return [ ");
				function.append(OutputFunctions.toJavaScript(plotSymbolX));
				function.append(", ");
				function.append(OutputFunctions.toJavaScript(plotSymbolY));
				function.append(", ");
				function.append(OutputFunctions.toJavaScript(plot.arg1()));
				function.append(" ]; }\n");
			} else {
				if (plot.arg1().isList()) {
					IAST listOfFunctions = (IAST) plot.arg1();
					for (int i = 1; i < listOfFunctions.size(); i++) {
						function.append("function z");
						function.append(i);
						function.append("(");
						function.append(OutputFunctions.toJavaScript(plotSymbolX));
						function.append(") { return ");
						function.append(OutputFunctions.toJavaScript(listOfFunctions.get(i)));
						function.append("; }\n");
					}
				} else {
					function.append("function z1(");
					function.append(OutputFunctions.toJavaScript(plotSymbolX));
					function.append(") { return ");
					function.append(OutputFunctions.toJavaScript(plot.arg1()));
					function.append("; }\n");
				}
			}
			js = js.replace("`3`", function.toString());

			// plot( x => (Math.sin(x*(1+a*x))), [0, 2*Math.PI], { } )
			StringBuilder graphicControl = new StringBuilder();

			if (plotID == ID.Plot3D) {
				if (!plotRangeY.isPresent()) {
					return F.NIL;
				}
				graphicControl.append("var p1 = ");
				graphicControl.append("parametric( z1, ");
				range(graphicControl, plotRangeX, -1);
				graphicControl.append(", ");
				range(graphicControl, plotRangeY, -1);
				graphicControl.append(", { colormap: (x,y) => ( 1 - Math.sin(x*y) ) / 2 } );\n\n\n");

				graphicControl.append("  var config = { type: 'threejs' };\n");
				graphicControl.append("  var data = [ p1 ];\n");
			} else {
				if (plot.arg1().isList()) {
					IAST listOfFunctions = (IAST) plot.arg1();
					if (plotID == ID.ParametricPlot) {
						graphicControl.append("var data = [ parametric( ");
						graphicControl.append(OutputFunctions.toJavaScript(plotSymbolX));
						graphicControl.append(" => [");
						for (int i = 1; i < listOfFunctions.size(); i++) {
							graphicControl.append("z");
							graphicControl.append(i);
							graphicControl.append("(");
							graphicControl.append(OutputFunctions.toJavaScript(plotSymbolX));
							graphicControl.append(")");
							if (i < listOfFunctions.size() - 1) {
								graphicControl.append(",");
							}
						}
						graphicControl.append("], ");
						range(graphicControl, plotRangeX, 1500);
						graphicControl.append(", { } )];\n");
					} else {
						for (int i = 1; i < listOfFunctions.size(); i++) {
							graphicControl.append("var p");
							graphicControl.append(i);
							graphicControl.append(" = ");
							graphicControl.append("plot( z");
							graphicControl.append(i);
							graphicControl.append(", ");
							range(graphicControl, plotRangeX, -1);
							graphicControl.append(", { } );\n");
						}

						// var data = [ p1, p2 ];
						if (plot.arg1().isList()) {
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
						} else {
							graphicControl.append("var data = [ p1 ];\n");
						}

					}
					graphicControl.append("var config = { type: 'svg' };\n");
				} else {
					graphicControl.append("var p1 = ");
					graphicControl.append("plot( z1, ");
					range(graphicControl, plotRangeX, -1);
					graphicControl.append(", { } );\n");

					// var data = [ p1, p2 ];
					if (plot.arg1().isList()) {
						IAST listOfFunctions = (IAST) plot.arg1();
						graphicControl.append("var data = [ ");
						for (int i = 1; i < listOfFunctions.size(); i++) {
							graphicControl.append("p");
							graphicControl.append(i);
							if (i < listOfFunctions.size() - 1) {
								graphicControl.append(", ");
							}
						}
						graphicControl.append(" ];\n");
					} else {
						graphicControl.append("var data = [ p1 ];\n");
					}

					graphicControl.append("var config = { type: 'svg'");
					if (optionPlotRange.isPresent()) {
						IExpr option = optionPlotRange.arg2();
						if (option.isAST(F.List, 3)) {
							plotRangeY = F.List(option.first(), option.second());
						}
					}
					if (plotRangeY.isAST(F.List, 3)) {
						// var config = { type: 'svg', yMin: -5, yMax: 5 };
						graphicControl.append(", yMin: ");
						graphicControl.append(OutputFunctions.toJavaScript(plotRangeY.arg1()));
						graphicControl.append(", yMax: ");
						graphicControl.append(OutputFunctions.toJavaScript(plotRangeY.arg2()));
					}
					graphicControl.append(" };\n");

				}

			}

			graphicControl.append("evaluate( id, data, config );\n");
			js = js.replace("`4`", graphicControl.toString());

			return F.JSFormData(js, "mathcell");
		}

		private static String createSlidersFromList(final IAST ast, String js) throws IOException {
			if (ast.size() >= 3) {
				if (ast.arg2().isList()) {
					// { type: 'slider', min: 0, max: 2*Math.PI, name: 'phase', label: 'phase' }
					StringBuilder slider = new StringBuilder();
					// var a = document.getElementById( id + 'a' ).value;
					StringBuilder variable = new StringBuilder();
					for (int i = 2; i < ast.size(); i++) {
						if (ast.get(i).isList()) {
							if (!createSingleSlider(ast, i, slider, variable)) {
								return null;
							}
						} else {
							break;
						}
					}
					js = js.replace("`1`", slider.toString());
					js = js.replace("`2`", variable.toString());
				}
			} else {
				js = js.replace("`1`", "");
				js = js.replace("`2`", "");
			}
			return js;
		}

		private static IExpr createSliderWithListPlot(final IAST ast, IAST plot, EvalEngine engine) throws IOException {
			// final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);

			String js = MATHCELL;
			js = createSlidersFromList(ast, js);
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
				int[] dimension = pointList.isMatrix();
				if (dimension != null) {
					if (dimension[1] == 2) {
						StringBuilder function = new StringBuilder();

						if (ast.arg1().isAST(F.ListLinePlot)) {
							function.append("var data = [ listPlot( [\n");
							for (int i = 1; i < pointList.size(); i++) {
								IAST rowList = (IAST) pointList.get(i);
								function.append("[ ");
								function.append(OutputFunctions.toJavaScript(rowList.arg1()));
								function.append(",");
								function.append(OutputFunctions.toJavaScript(rowList.arg2()));
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
								function.append(OutputFunctions.toJavaScript(rowList.arg1()));
								function.append(",");
								function.append(OutputFunctions.toJavaScript(rowList.arg2()));
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
							function.append(OutputFunctions.toJavaScript(rowList.arg1()));
							function.append(",");
							function.append(OutputFunctions.toJavaScript(rowList.arg2()));
							function.append(",");
							function.append(OutputFunctions.toJavaScript(rowList.arg3()));
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
					if (ast.arg1().isAST(F.ListLinePlot)) {
						function.append("var data = [ listPlot( [\n");
						for (int i = 1; i < pointList.size(); i++) {
							function.append("[ ");
							function.append(i);
							function.append(",");
							function.append(OutputFunctions.toJavaScript(pointList.get(i)));
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
							function.append(OutputFunctions.toJavaScript(pointList.get(i)));
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

		private static boolean createSingleSlider(final IAST ast, int i, StringBuilder slider, StringBuilder variable)
				throws IOException {
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
					sliderSymbol = OutputFunctions.toJavaScript(sliderParameters.arg1());
					defaultValue = OutputFunctions.toJavaScript(sliderRange.arg2());
					label = OutputFunctions.toJavaScript(sliderParameters.arg3());
				} else {
					sliderSymbol = OutputFunctions.toJavaScript(sliderRange.arg1());
					label = sliderSymbol;
				}
				if (i > 2) {
					slider.append(", ");
				}
				slider.append("{ type: 'buttons', values: [");
				for (int j = 1; j < listOfButtons.size(); j++) {
					slider.append("'");
					slider.append(OutputFunctions.toJavaScript(listOfButtons.get(j)));
					slider.append("'");
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
					sliderSymbol = OutputFunctions.toJavaScript(sliderParameters.arg1());
					defaultValue = OutputFunctions.toJavaScript(sliderRange.arg2());
					label = OutputFunctions.toJavaScript(sliderParameters.arg3());
				} else {
					sliderSymbol = OutputFunctions.toJavaScript(sliderRange.arg1());
					label = sliderSymbol;
				}
				if (i > 2) {
					slider.append(", ");
				}
				slider.append("{ type: 'slider', min: ");
				slider.append(OutputFunctions.toJavaScript(sliderRange.arg2()));
				slider.append(", max: ");
				slider.append(OutputFunctions.toJavaScript(sliderRange.arg3()));
				if (step != null) {
					slider.append(", step: ");
					slider.append(OutputFunctions.toJavaScript(step));
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
		private static IExpr createSliderWithFormulas(IExpr formula, IAST sliderRange, IExpr stepExpr,
				EvalEngine engine) throws IOException {
			IExpr list = engine.evaluate(F.Table(formula, sliderRange));
			if (list.isList() && list.size() > 1) {
				IAST listOfFormulas = (IAST) list;
				String sliderSymbol = OutputFunctions.toJavaScript(sliderRange.arg1());
				String min = OutputFunctions.toJavaScript(sliderRange.arg2());
				String max = OutputFunctions.toJavaScript(sliderRange.arg3());
				String step = OutputFunctions.toJavaScript(stepExpr);
				String js = MATHCELL;
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
				js = js.replace("`1`", slider.toString());

				StringBuilder variable = new StringBuilder();
				variable.append("var ");
				variable.append(sliderSymbol);

				// variable.append(" = document.getElementById( id + '");
				variable.append(" = getVariable(id, '");
				variable.append(sliderSymbol);
				// variable.append("' ).value;\n");
				variable.append("');\n");

				js = js.replace("`2`", variable.toString());

				js = js.replace("`3`", "");

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

		private static void range(StringBuilder graphicControl, IAST plotRange, int steps) throws IOException {
			graphicControl.append("[");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg2()));
			graphicControl.append(", ");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg3()));
			if (steps > 0) {
				graphicControl.append(", ");
				graphicControl.append(steps);
			}
			graphicControl.append("]");
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_INFINITY;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
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
