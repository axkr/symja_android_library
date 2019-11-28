package org.matheclipse.core.builtin;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.form.output.DoubleFormFactory;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class ManipulateFunction {
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
				F.Manipulate.setEvaluator(new Manipulate());
			}
		}
	}

	private static class Manipulate extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.arg1().isAST(F.ListLinePlot) || //
						ast.arg1().isAST(F.ListPlot)) {
					IAST plot = (IAST) ast.arg1();
					return jsxgraphSliderWithListPlot(ast, plot, engine);
				} else if (ast.arg1().isAST(F.ListPlot3D)) {
					IAST plot = (IAST) ast.arg1();
					return mathcellSliderWithListPlot(ast, plot, engine);
				} else if (ast.arg1().isAST(F.Plot) || //
						ast.arg1().isAST(F.ParametricPlot) || ast.arg1().isAST(F.PolarPlot)) {
					IAST plot = (IAST) ast.arg1();
					if (plot.size() >= 3 && plot.arg2().isList()) {
						IAST plotRangeX = (IAST) plot.arg2();
						IAST plotRangeY = F.NIL;
						if (plot.size() >= 4 && plot.arg3().isList()) {
							plotRangeY = (IAST) plot.arg3();
						}
						if (ast.size() >= 3) {
							if (ast.arg2().isList()) {
								IAST sliderRange = (IAST) ast.arg2();
								if (sliderRange.isAST2() && sliderRange.arg2().isList()) {
									// assumption: button should be displayed
									return mathcellSliderWithPlot(ast, plot, plotRangeX, plotRangeY, engine);
								}
							}
						}

						if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
							return jsxgraphSliderWithPlot(ast, plot, plotRangeX, engine);
							// return mathcellSliderWithPlot(ast, plot, plotRangeX, plotRangeY, engine);
						}
					}
				} else if (ast.arg1().isAST(F.Plot3D)) {
					IAST plot = (IAST) ast.arg1();
					if (plot.size() >= 3 && plot.arg2().isList()) {
						IAST plotRangeX = (IAST) plot.arg2();
						// TODO find better default Y plot range instead of [-5, 5]
						IAST plotRangeY = F.NIL;
						if (plot.size() >= 4 && plot.arg3().isList()) {
							plotRangeY = (IAST) plot.arg3();
						}
						if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
							return mathcellSliderWithPlot(ast, plot, plotRangeX, plotRangeY, engine);
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
							return mathcellSliderWithFormulas(formula, sliderRange, step, engine);
						}
					}
				}
			} catch (Exception ex) {
				if (Config.SHOW_STACKTRACE) {
					ex.printStackTrace();
				}
				return IOFunctions.printMessage(F.Manipulate, ex, engine);
			}
			return F.NIL;
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
	private static IExpr mathcellSliderWithPlot(final IAST ast, IAST plot, IAST plotRangeX, IAST plotRangeY,
			EvalEngine engine) {
		JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);

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
				} else if (plotID == ID.PolarPlot) {
					optionPlotRange = F.List(F.List(plotRange.negate(), plotRange), //
							F.List(plotRange.negate(), plotRange));
				} else if (plotID == ID.ParametricPlot) {
					optionPlotRange = F.List(F.List(plotRange.negate(), plotRange), //
							F.List(plotRange.negate(), plotRange));
				}
			}
		}

		String js = MATHCELL;
		js = mathcellSlidersFromList(ast, js, toJS);
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
		if (plotID == ID.Plot3D) {
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

		if (plotID == ID.Plot3D) {
			if (!plotRangeY.isPresent()) {
				return F.NIL;
			}
			for (int i = 1; i < listOfFunctions.size(); i++) {
				graphicControl.append("var p" + i + " = ");
				graphicControl.append("parametric( z" + i + ", ");
				mathcellRange(graphicControl, plotRangeX, -1, toJS);
				graphicControl.append(", ");
				mathcellRange(graphicControl, plotRangeY, -1, toJS);
				graphicControl.append(", { colormap: (x,y) => ( 1 - Math.sin(x*y) ) / 2 } );\n");
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
		} else {
			if (plotID == ID.ParametricPlot || //
					plotID == ID.PolarPlot) {
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
				mathcellRange(graphicControl, plotRangeX, 1500, toJS);
				graphicControl.append(", { } )];\n");
			} else {
				for (int i = 1; i < listOfFunctions.size(); i++) {
					graphicControl.append("var p" + i + " = plot( z" + i + ", ");
					mathcellRange(graphicControl, plotRangeX, -1, toJS);
					graphicControl.append(", { } );\n");
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
			if (plotRangeY.isAST(F.List, 3)) {
				// var config = { type: 'svg', yMin: -5, yMax: 5 };
				graphicControl.append(", yMin: ");
				toJS.convert(graphicControl, plotRangeY.arg1());
				graphicControl.append(", yMax: ");
				toJS.convert(graphicControl, plotRangeY.arg2());
			}
			graphicControl.append(" };\n");

		}

		graphicControl.append("evaluate( id, data, config );\n");
		js = js.replace("`4`", graphicControl.toString());

		return F.JSFormData(js, "mathcell");
	}

	private static String mathcellSlidersFromList(final IAST ast, String js, JavaScriptFormFactory toJS) {
		if (ast.size() >= 3) {
			if (ast.arg2().isList()) {
				// { type: 'slider', min: 0, max: 2*Math.PI, name: 'phase', label: 'phase' }
				StringBuilder slider = new StringBuilder();
				// var a = document.getElementById( id + 'a' ).value;
				StringBuilder variable = new StringBuilder();
				for (int i = 2; i < ast.size(); i++) {
					if (ast.get(i).isList()) {
						if (!mathcellSingleSlider(ast, i, slider, variable, toJS)) {
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

	private static IExpr mathcellSliderWithListPlot(final IAST ast, IAST plot, EvalEngine engine) {
		// final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);
		JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);
		String js = MATHCELL;
		js = mathcellSlidersFromList(ast, js, toJS);
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
				if (ast.arg1().isAST(F.ListLinePlot)) {
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

	private static boolean mathcellSingleSlider(final IAST ast, int i, StringBuilder slider, StringBuilder variable,
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

	/**
	 * <p>
	 * Evaluate <code>Table( &lt;formula&gt;, &lt;sliderRange&gt; )</code>. If the result is a list, then convert this
	 * list in a JavaScript list of LaTeX formulas, which could be rendered with MathJAX.
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
	private static IExpr mathcellSliderWithFormulas(IExpr formula, IAST sliderRange, IExpr stepExpr,
			EvalEngine engine) {
		JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);
		IExpr list = engine.evaluate(F.Table(formula, sliderRange));
		if (list.isList() && list.size() > 1) {
			IAST listOfFormulas = (IAST) list;
			String sliderSymbol = toJS.toString(sliderRange.arg1());
			String min = toJS.toString(sliderRange.arg2());
			String max = toJS.toString(sliderRange.arg3());
			String step = toJS.toString(stepExpr);
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

	private static void mathcellRange(StringBuilder graphicControl, IAST plotRange, int steps,
			JavaScriptFormFactory toJS) {
		graphicControl.append("[");
		mathcellRangeArgs(graphicControl, plotRange, steps, toJS);
		graphicControl.append("]");
	}

	private static void mathcellRangeArgs(StringBuilder graphicControl, IAST plotRange, int steps,
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
	 * @deprecated there are untested changes from ListPLot, ListLinePlot to slider behaviour
	 */
	// private static IExpr jsxgraphSliderWithPlot(final IAST ast, IAST plot, IAST plotRangeX, IAST plotRangeY,
	// EvalEngine engine) {
	// int plotID = plot.headID();
	//
	// final OptionArgs options;
	// if (plotID == ID.Plot3D) {
	// options = new OptionArgs(plot.topHead(), plot, 4, engine);
	// // } else if (plotID == ID.Plot) {
	// // options = new OptionArgs(plot.topHead(), plot, 3, engine);
	// } else {
	// options = new OptionArgs(plot.topHead(), plot, 3, engine);
	// }
	// IExpr plotRange = options.getOption(F.PlotRange);
	// IAST optionPlotRange = F.NIL;
	// if (plotRange.isPresent()) {
	// if (plotRange.isAST(F.List, 3)) {
	// optionPlotRange = F.List(F.Full, F.List(plotRange.first(), plotRange.second()));
	// } else if (plotRange.isReal()) {
	// if (plotID == ID.Plot) {
	// optionPlotRange = F.List(F.Full, F.List(plotRange.negate(), plotRange));
	// } else if (plotID == ID.ListPlot || plotID == ID.ListLinePlot) {
	// optionPlotRange = F.List(F.Full, F.List(F.C0, plotRange));
	// } else if (plotID == ID.ParametricPlot) {
	// optionPlotRange = F.List(F.List(plotRange.negate(), plotRange), //
	// F.List(plotRange.negate(), plotRange));
	// }
	// }
	// }
	//
	// String js = JSXGRAPH;
	// JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_JSXGRAPH);
	// jsxgraphSliderNamesFromList(ast, toJS);
	// if (js == null) {
	// return F.NIL;
	// }
	//
	// ISymbol plotSymbolX = (ISymbol) plotRangeX.arg1();
	//
	// // function z1(x,y) { return [ x, y, Math.sin( a * x * y ) ]; }
	// StringBuilder function = new StringBuilder();
	//
	// if (plotID == ID.Plot3D) {
	// function.append("function z1(");
	// ISymbol plotSymbolY = (ISymbol) plotRangeY.arg1();
	// toJS.convert(function, plotSymbolX);
	// function.append(",");
	// toJS.convert(function, plotSymbolY);
	// function.append(") { return [ ");
	// toJS.convert(function, plotSymbolX);
	// function.append(", ");
	// toJS.convert(function, plotSymbolY);
	// function.append(", ");
	// toJS.convert(function, plot.arg1());
	// function.append(" ]; }\n");
	// } else {
	// if (plot.arg1().isList()) {
	// IAST listOfFunctions = (IAST) plot.arg1();
	// for (int i = 1; i < listOfFunctions.size(); i++) {
	// function.append("function z");
	// function.append(i);
	// function.append("(");
	// toJS.convert(function, plotSymbolX);
	// function.append(") { return ");
	// toJS.convert(function, listOfFunctions.get(i));
	// function.append("; }\n");
	// }
	// } else {
	// function.append("function z1(");
	// toJS.convert(function, plotSymbolX);
	// function.append(") { return ");
	// toJS.convert(function, plot.arg1());
	// function.append("; }\n");
	// }
	// }
	// js = js.replace("`3`", function.toString());
	//
	// // plot( x => (Math.sin(x*(1+a*x))), [0, 2*Math.PI], { } )
	// StringBuilder graphicControl = new StringBuilder();
	//
	// if (plotID == ID.Plot3D) {
	// if (!plotRangeY.isPresent()) {
	// return F.NIL;
	// }
	// graphicControl.append("var p1 = ");
	// graphicControl.append("parametric( z1, ");
	// jsxgraphRange(graphicControl, plotRangeX, -1, toJS);
	// graphicControl.append(", ");
	// jsxgraphRange(graphicControl, plotRangeY, -1, toJS);
	// graphicControl.append(", { colormap: (x,y) => ( 1 - Math.sin(x*y) ) / 2 } );\n\n\n");
	//
	// graphicControl.append(" var config = { type: 'threejs' };\n");
	// graphicControl.append(" var data = [ p1 ];\n");
	// } else {
	// if (plot.arg1().isList()) {
	// IAST listOfFunctions = (IAST) plot.arg1();
	// if (plotID == ID.ParametricPlot) {
	// graphicControl.append("board.create('curve',[");
	// // graphicControl.append(OutputFunctions.toJSXGraph(plotSymbolX));
	// // graphicControl.append(" => [");
	// for (int i = 1; i < listOfFunctions.size(); i++) {
	// graphicControl.append("function(t){return z");
	// graphicControl.append(i);
	// graphicControl.append("(");
	// toJS.convert(graphicControl, plotSymbolX);
	// graphicControl.append(");}");
	// if (i < listOfFunctions.size() - 1) {
	// graphicControl.append(",");
	// }
	// }
	// graphicControl.append(", ");
	// jsxgraphRangeArgs(graphicControl, plotRangeX, -1, toJS);
	// graphicControl.append("]");
	// graphicControl.append(", { } );\n");
	// } else {
	// for (int i = 1; i < listOfFunctions.size(); i++) {
	// graphicControl.append("var p");
	// graphicControl.append(i);
	// graphicControl.append(" = ");
	// graphicControl.append("board.create('functiongraph',[z");
	// graphicControl.append(i);
	// graphicControl.append(",0,1], {strokeWidth:2});");
	// graphicControl.append(", ");
	// jsxgraphRange(graphicControl, plotRangeX, -1, toJS);
	// graphicControl.append(", { } );\n");
	// }
	//
	// // var data = [ p1, p2 ];
	// if (plot.arg1().isList()) {
	// // listOfFunctions = (IAST) plot.arg1();
	// graphicControl.append("var data = [ ");
	// for (int i = 1; i < listOfFunctions.size(); i++) {
	// graphicControl.append("p");
	// graphicControl.append(i);
	// if (i < listOfFunctions.size() - 1) {
	// graphicControl.append(", ");
	// }
	// }
	// graphicControl.append(" ];\n");
	// } else {
	// graphicControl.append("var data = [ p1 ];\n");
	// }
	//
	// }
	// // graphicControl.append("var config = { type: 'svg' };\n");
	// } else {
	// graphicControl.append("var p1 = ");
	// graphicControl.append("board.create('functiongraph',[z1,0,1], {strokeWidth:2});");
	// }
	//
	// }
	//
	// js = js.replace("`4`", graphicControl.toString());
	//
	// return F.JSFormData(js, "jsxgraph");
	// }

	/**
	 * Add all slider names to the toJS slider names.
	 * 
	 * @param ast
	 *            from position 2 to size()-1 there maybe some <code>Manipulate</code> sliders defined
	 * @param toJS
	 *            the Symja to JavaScript converter factory
	 */
	private static void jsxgraphSliderNamesFromList(final IAST ast, JavaScriptFormFactory toJS) {
		if (ast.size() >= 3) {
			if (ast.arg2().isList()) {
				for (int i = 2; i < ast.size(); i++) {
					if (ast.get(i).isList()) {
						if (!jsxgraphSingleSliderName((IAST) ast.get(i), toJS)) {
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
	 * Add the slider name to the toJS slider names.
	 * 
	 * @param sliderRange
	 *            a single <code>List(slider-name,...)</code> representing a slider definition
	 * @param toJS
	 *            the Symja to JavaScript converter factory
	 * @return
	 */
	private static boolean jsxgraphSingleSliderName(final IAST sliderRange, JavaScriptFormFactory toJS) {
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
	private static IExpr jsxgraphSliderWithPlot(final IAST ast, IAST plot, IAST plotRangeX, EvalEngine engine) {
		int plotID = plot.headID();

		final OptionArgs options;
		if (plotID == ID.Plot3D) {
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
		double[] boundingbox = new double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE };

		JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);

		jsxgraphSliderNamesFromList(ast, toJS);
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
		if (plotID == ID.ParametricPlot || //
				plotID == ID.PolarPlot) {
			IAST listOfFunctions = (IAST) arg1;
			int[] dim = arg1.isMatrix();
			if (dim == null) {
				if (plotID == ID.ParametricPlot && listOfFunctions.size() != 3) {
					return F.NIL;
				}
			} else {
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
				function.append(") { return ");
				toJS.convert(function, listOfFunctions.get(i));
				function.append("; }\n");
				ISymbol sym = F.Dummy("$z" + i);
				IExpr functionRange = F.FunctionRange.of(engine, listOfFunctions.get(i), plotSymbolX, sym);
				if (i == 1) {
					xBoundingBoxFunctionRange(engine, boundingbox, functionRange);
				} else {
					yBoundingBoxFunctionRange(engine, boundingbox, functionRange);
				}
			}

			if (plotID == ID.PolarPlot) {
				function.append("board.create('curve', [");
				for (int i = 1; i < listOfFunctions.size(); i++) {
					function.append("function(");
					toJS.convert(function, plotSymbolX);
					function.append("){return z");
					function.append(i);
					function.append("(");
					toJS.convert(function, plotSymbolX);
					function.append(");}");
					if (i < listOfFunctions.size() - 1) {
						function.append(",");
					}
				}
				// origin of polar plot
				function.append(",[0,0], ");

				toJS.convert(function, plotRangeX.arg2());
				function.append(", ");
				toJS.convert(function, plotRangeX.arg3());
				 
				
				function.append("]");
				function.append(", {curveType:'polar'} );\n");
			} else {
				function.append("board.create('curve',[");
				// graphicControl.append(OutputFunctions.toJSXGraph(plotSymbolX));
				// graphicControl.append(" => [");
				for (int i = 1; i < listOfFunctions.size(); i++) {
					function.append("function(");
					toJS.convert(function, plotSymbolX);
					function.append("){return z");
					function.append(i);
					function.append("(");
					toJS.convert(function, plotSymbolX);
					function.append(");}");
					if (i < listOfFunctions.size() - 1) {
						function.append(",");
					}
				}
				function.append(", ");
				jsxgraphRangeArgs(function, plotRangeX, -1, toJS);
				function.append("]");
				function.append(", { } );\n");
			}

			if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeXMin, 1e-10)
					&& F.isFuzzyEquals(Double.MAX_VALUE, boundingbox[0], 1e-10)) {
				boundingbox[0] = plotRangeXMin;
			}
			if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeYMax, 1e-10)) {
				boundingbox[1] = plotRangeYMax;
			}
			if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeXMax, 1e-10)
					&& F.isFuzzyEquals(Double.MIN_VALUE, boundingbox[2], 1e-10)) {
				boundingbox[2] = plotRangeXMax;
			}
			if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeYMin, 1e-10)) {
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

			return jsxgraphBoundingBox(ast, boundingbox, function.toString(), toJS);

		}

		IAST listOfFunctions = (IAST) arg1;
		for (int i = 1; i < listOfFunctions.size(); i++) {
			function.append("function z");
			function.append(i);
			function.append("(");
			toJS.convert(function, plotSymbolX);
			function.append(") { return ");
			toJS.convert(function, listOfFunctions.get(i));
			function.append("; }\n");
			ISymbol sym = F.Dummy("$z" + i);
			IExpr functionRange = F.FunctionRange.of(engine, listOfFunctions.get(i), plotSymbolX, sym);
			yBoundingBoxFunctionRange(engine, boundingbox, functionRange);
		}

		for (int i = 1; i < listOfFunctions.size(); i++) {
			function.append("var p");
			function.append(i);
			function.append(" = ");
			function.append("board.create('functiongraph',[z");
			function.append(i);
			function.append(", ");
			jsxgraphRangeArgs(function, plotRangeX, -1, toJS);
			// jsxgraphRange(function, plotRangeX, -1, toJS);
			function.append("]);\n");
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
		if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeXMin, 1e-10)) {
			boundingbox[0] = plotRangeXMin;
		}
		if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeYMax, 1e-10)) {
			boundingbox[1] = plotRangeYMax;
		}
		if (!F.isFuzzyEquals(Double.MIN_VALUE, plotRangeXMax, 1e-10)) {
			boundingbox[2] = plotRangeXMax;
		}
		if (!F.isFuzzyEquals(Double.MAX_VALUE, plotRangeYMin, 1e-10)) {
			boundingbox[3] = plotRangeYMin;
		}
		return jsxgraphBoundingBox(ast, boundingbox, function.toString(), toJS);

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
	 * @param sliderNames
	 * @param toJS
	 *            the Symja to JavaScript converter factory
	 * @return
	 */
	private static String jsxgraphSlidersFromList(final IAST ast, String js, double[] boundingbox,
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
						if (!jsxgraphSingleSlider((IAST) ast.getAST(i), slider, xPos1Slider, xPos2Slider, yPosSlider,
								toJS)) {
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

	private static IExpr jsxgraphSliderWithListPlot(final IAST ast, IAST plot, EvalEngine engine) {
		// final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);
		if (plot.size() < 2) {
			return F.NIL;
		}
		// xmin, ymax, xmax, ymin
		// double[] boundingbox = new double[] { -5.0, 5.0, 5.0, -5.0 };

		JavaScriptFormFactory toJS = new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_MATHCELL);
		jsxgraphSliderNamesFromList(ast, toJS);
		IExpr arg1 = plot.arg1();
		if (!arg1.isList()) {
			arg1 = engine.evaluate(arg1);
		}
		if (arg1.isList() && arg1.size() > 1) {
			IAST pointList = (IAST) arg1;
			int[] dimension = pointList.isMatrix();
			if (dimension != null) {
				if (dimension[1] == 2) {
					return sequencePointListPlot(ast, pointList, toJS, engine);
				}
				return F.NIL;
			} else {
				return sequenceYValuesListPlot(ast, pointList, toJS, engine);
			}
		}
		return F.NIL;
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
		return jsxgraphBoundingBox(ast, boundingbox, function.toString(), toJS);
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

		return jsxgraphBoundingBox(ast, boundingbox, function.toString(), toJS);
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
	 * @return
	 */
	private static IExpr jsxgraphBoundingBox(IAST ast, double[] boundingbox, String function,
			JavaScriptFormFactory toJS) {
		String js = JSXGRAPH;
		if (F.isFuzzyEquals(Double.MAX_VALUE, boundingbox[0], 1e-10)) {
			boundingbox[0] = -5.0;
		}
		if (F.isFuzzyEquals(Double.MIN_VALUE, boundingbox[1], 1e-10)) {
			boundingbox[1] = 5.0;
		}
		if (F.isFuzzyEquals(Double.MIN_VALUE, boundingbox[2], 1e-10)) {
			boundingbox[2] = 5.0;
		}
		if (F.isFuzzyEquals(Double.MAX_VALUE, boundingbox[3], 1e-10)) {
			boundingbox[3] = -5.0;
		}

		// add some "padding" around bounding box
		double xPadding = (boundingbox[2] - boundingbox[0]) / 20;
		double yPadding = (boundingbox[1] - boundingbox[3]) / 20;
		boundingbox[0] = boundingbox[0] - xPadding;// xMin
		boundingbox[2] = boundingbox[2] + xPadding;// xMax
		boundingbox[1] = boundingbox[1] + yPadding;// yMax
		boundingbox[3] = boundingbox[3] - yPadding;// yMin

		js = jsxgraphSlidersFromList(ast, js, boundingbox, toJS);

		js = js.replace("`2`", function);

		StringBuilder graphicControl = new StringBuilder();

		js = js.replace("`3`", graphicControl.toString());

		StringBuilder jsControl = new StringBuilder();
		jsControl.append("var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[");

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

	/**
	 * Create JSXGraph sliders.
	 * 
	 * @param ast
	 *            from positon 2 to size()-1 there maybe some <code>Manipulate</code> sliders defined
	 * @param js
	 *            the JSXGraph JavaScript template
	 * @param boundingbox
	 *            an array of double values (length 4) which describes the bounding box
	 *            <code>[xMin, yMAx, xMax, yMin]</code>
	 * @param sliderNames
	 * @param toJS
	 * @return
	 */

	/**
	 * Generate a single JSXGraph JavaScript slider.
	 * 
	 * @param sliderRange
	 * @param slider
	 * @param sliderNames
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
	private static boolean jsxgraphSingleSlider(final IAST sliderRange, StringBuilder slider, double xPos1Slider,
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

	private static void jsxgraphRangeArgs(StringBuilder graphicControl, IAST plotRange, int steps,
			JavaScriptFormFactory toJS) {
		toJS.convert(graphicControl, plotRange.arg2());
		graphicControl.append(", ");
		toJS.convert(graphicControl, plotRange.arg3());
		if (steps > 0) {
			graphicControl.append(", ");
			graphicControl.append(steps);
		}
	}

	private static void jsxgraphRange(StringBuilder graphicControl, IAST plotRange, int steps,
			JavaScriptFormFactory toJS) {
		graphicControl.append("[");
		jsxgraphRangeArgs(graphicControl, plotRange, steps, toJS);
		graphicControl.append("]");
	}

	public static void initialize() {
		if (ToggleFeature.MANIPULATE) {
			Initializer.init();
		}
	}

	private ManipulateFunction() {

	}

}
