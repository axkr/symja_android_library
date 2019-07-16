package org.matheclipse.core.builtin;

import java.io.IOException;
import java.io.StringWriter;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ManipulateFunction {
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
					// " var p2 = plot( x => Math.cos(x-phase), [0, 2*Math.PI], { color: 'purple' } );\n" + //
					// "\n" + //
					// " var data = [ p1, p2 ];\n" + //
					"\n" + //
					// " var config = { type: 'svg' };\n" + //
					// "\n" + //
					// " evaluate( id, data, config );\n" + //
					"\n" + //
					"}";

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.Manipulate.setEvaluator(new Manipulate());
		}
	}

	private static class Manipulate extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.arg1().isAST(F.Plot) || //
				// ast.arg1().isAST(F.ParametricPlot) || //
						ast.arg1().isAST(F.Plot3D)) {
					IAST plot = (IAST) ast.arg1();
					if (plot.size() >= 3 && plot.arg2().isList()) {
						IAST plotRangeX = (IAST) plot.arg2();
						IAST plotRangeY = F.NIL;
						if (plot.size() >= 4 && plot.arg3().isList()) {
							plotRangeY = (IAST) plot.arg3();
						}
						if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
							return createSliderWithPlot(ast, plot, plotRangeX, plotRangeY);
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
		private static IExpr createSliderWithPlot(final IAST ast, IAST plot, IAST plotRangeX, IAST plotRangeY)
				throws IOException {
			int plotID = plot.headID();
			ISymbol plotSymbolX = (ISymbol) plotRangeX.arg1();

			String js = MATHCELL;
			if (ast.size() >= 3) {
				if (ast.arg2().isList()) {
					// { type: 'slider', min: 0, max: 2*Math.PI, name: 'phase', label: 'phase' }
					StringBuilder slider = new StringBuilder();
					// var a = document.getElementById( id + 'a' ).value;
					StringBuilder variable = new StringBuilder();
					for (int i = 2; i < ast.size(); i++) {
						if (ast.get(i).isList()) {
							IAST sliderRange = (IAST) ast.get(i);
							if (sliderRange.isAST3() && sliderRange.arg1().isSymbol()) {
								String sliderSymbol = OutputFunctions.toJavaScript(sliderRange.arg1());
								if (i > 2) {
									slider.append(", ");
								}
								slider.append("{ type: 'slider', min: ");
								slider.append(OutputFunctions.toJavaScript(sliderRange.arg2()));
								slider.append(", max: ");
								slider.append(OutputFunctions.toJavaScript(sliderRange.arg3()));
								slider.append(", name: '");
								slider.append(sliderSymbol);
								slider.append("', label: '");
								slider.append(sliderSymbol);
								slider.append("' }\n");

								variable.append("var ");
								variable.append(sliderSymbol);
								// variable.append(" = getVariable( id, '");
								variable.append(" = document.getElementById( id + '");
								variable.append(sliderSymbol);
								variable.append("' ).value;\n");

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
				range(graphicControl, plotRangeX);
				graphicControl.append(", ");
				range(graphicControl, plotRangeY);
				graphicControl.append(", { colormap: (x,y) => ( 1 - Math.sin(x*y) ) / 2 } );\n\n\n");

				graphicControl.append("  var config = { type: 'threejs' };\n");
			} else {
				if (plot.arg1().isList()) {
					IAST listOfFunctions = (IAST) plot.arg1();
					for (int i = 1; i < listOfFunctions.size(); i++) {
						graphicControl.append("var p");
						graphicControl.append(i);
						graphicControl.append(" = ");
						graphicControl.append("plot( z");
						graphicControl.append(i);
						graphicControl.append(", ");
						range(graphicControl, plotRangeX);
						graphicControl.append(", { } );\n");
					}
				} else {
					graphicControl.append("var p1 = ");
					graphicControl.append("plot( z1, ");
					range(graphicControl, plotRangeX);
					graphicControl.append(", { } );\n");
				}

				graphicControl.append("var config = { type: 'svg' };\n");
			}

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
			graphicControl.append("evaluate( id, data, config );\n");
			js = js.replace("`4`", graphicControl.toString());

			return F.JSFormData(js, "mathcell");
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
		 * @param step
		 * @param engine
		 * @return
		 * @throws IOException
		 */
		private static IExpr createSliderWithFormulas(IExpr formula, IAST sliderRange, IExpr step, EvalEngine engine)
				throws IOException {
			IExpr list = engine.evaluate(F.Table(formula, sliderRange));
			if (list.isList() && list.size() > 1) {
				IAST listOfFormulas = (IAST) list;
				String sliderSymbol = OutputFunctions.toJavaScript(sliderRange.arg1());
				String js = MATHCELL;
				// { type: 'slider', min: 1, max: 5, step: 1, name: 'n', label: 'n' }
				StringBuilder slider = new StringBuilder();
				slider.append("{ type: 'slider', min: ");
				slider.append(OutputFunctions.toJavaScript(sliderRange.arg2()));
				slider.append(", max: ");
				slider.append(OutputFunctions.toJavaScript(sliderRange.arg3()));
				slider.append(", step: ");
				slider.append(OutputFunctions.toJavaScript(step));
				slider.append(", name: '");
				slider.append(sliderSymbol);
				slider.append("', label: '");
				slider.append(sliderSymbol);
				slider.append("' }\n");
				js = js.replace("`1`", slider.toString());

				StringBuilder variable = new StringBuilder();
				variable.append("var ");
				variable.append(sliderSymbol);

				// variable.append(" = getVariable( id, '");
				variable.append(" = document.getElementById( id + '");
				variable.append(sliderSymbol);
				variable.append("' ).value;\n");
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
					graphicControl.append("', ");
				}
				graphicControl.append(" ];\n\n");

				graphicControl.append("  var data = '\\\\\\\\[' + expressions[n-1] + '\\\\\\\\]';\n\n");
				graphicControl.append("  data = data.replace( /\\\\\\\\/g, '&#92;' );\n\n");
				graphicControl.append("  var config = {type: 'text', center: true };\n\n");
				graphicControl.append("  evaluate( id, data, config );\n\n");
				graphicControl.append("  MathJax.Hub.Queue( [ 'Typeset', MathJax.Hub, id ] );\n");

				js = js.replace("`4`", graphicControl.toString());

				return F.JSFormData(js, "mathcell");
			}
			return F.NIL;
		}

		private static void range(StringBuilder graphicControl, IAST plotRange) throws IOException {
			graphicControl.append("[");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg2()));
			graphicControl.append(", ");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg3()));
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
