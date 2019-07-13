package org.matheclipse.core.builtin;

import java.io.IOException;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
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
					"  `2`" + //
					"\n" + //
					"  `3`" + //
					"\n" + //
					"  `4`" + //
					// " var p2 = plot( x => Math.cos(x-phase), [0, 2*Math.PI], { color: 'purple' } );\n" + //
					// "\n" + //
					// " var data = [ p1, p2 ];\n" + //
					"\n" + //
					// " var config = { type: 'svg' };\n" + //
					// "\n" + //
					"  evaluate( id, data, config );\n" + //
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
					int plotID = plot.headID();
					if ((plot.size() == 3 || plot.size() == 4) && plot.arg2().isList()) {
						IAST plotRangeX = (IAST) plot.arg2();
						IAST plotRangeY = F.NIL;
						if (plot.size() == 4 && plot.arg3().isList()) {
							plotRangeY = (IAST) plot.arg3();
						}
						if (plotRangeX.isAST3() && plotRangeX.arg1().isSymbol()) {
							ISymbol plotSymbolX = (ISymbol) plotRangeX.arg1();

							String js = MATHCELL;
							if (ast.isAST2()) {
								if (ast.arg2().isList()) {
									IAST sliderRange = (IAST) ast.arg2();
									if (sliderRange.isAST3() && sliderRange.arg1().isSymbol()) {
										String sliderSymbol = OutputFunctions.toJavaScript(sliderRange.arg1());

										// { type: 'slider', min: 0, max: 2*Math.PI, name: 'phase', label: 'phase' }

										StringBuilder slider = new StringBuilder();
										slider.append("{ type: 'slider', min: ");
										slider.append(OutputFunctions.toJavaScript(sliderRange.arg2()));
										slider.append(", max: ");
										slider.append(OutputFunctions.toJavaScript(sliderRange.arg3()));
										slider.append(", name: '");
										slider.append(sliderSymbol);
										slider.append("', label: '");
										slider.append(sliderSymbol);
										slider.append("' }\n");
										js = js.replaceAll("`1`", slider.toString());

										// var a = document.getElementById( id + 'a' ).value;
										StringBuilder variable = new StringBuilder();
										variable.append("var ");
										variable.append(sliderSymbol);

										// variable.append(" = getVariable( id, '");
										variable.append(" = document.getElementById( id + '");
										variable.append(sliderSymbol);
										variable.append("' ).value;\n");
										js = js.replaceAll("`2`", variable.toString());
									}
								}
							} else {
								js = js.replaceAll("`1`", "");
								js = js.replaceAll("`2`", "");
							}

							// function z1(x,y) { return [ x, y, Math.sin( a * x * y ) ]; }
							StringBuilder function = new StringBuilder();
							function.append("function z1(");
							if (plotID == ID.Plot3D) {
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
								// if (plotID == ID.ParametricPlot) {
								// function.append(OutputFunctions.toJavaScript(plotSymbolX));
								// function.append(") { return ");
								// function.append(OutputFunctions.toJavaScript(plot.arg1()));
								// function.append("; }\n");
								// } else {
								function.append(OutputFunctions.toJavaScript(plotSymbolX));
								function.append(") { return ");
								function.append(OutputFunctions.toJavaScript(plot.arg1()));
								function.append("; }\n");
								// }
							}
							js = js.replaceAll("`3`", function.toString());

							// plot( x => (Math.sin(x*(1+a*x))), [0, 2*Math.PI], { } )
							StringBuilder graphicControl = new StringBuilder();
							graphicControl.append("var p1 = ");

							if (plotID == ID.Plot3D) {
								if (!plotRangeY.isPresent()) {
									return F.NIL;
								}
								graphicControl.append("parametric( z1, ");
								range(graphicControl, plotRangeX);
								graphicControl.append(", ");
								range(graphicControl, plotRangeY);
								graphicControl.append(", { colormap: (x,y) => ( 1 - Math.sin(x*y) ) / 2 } );\n\n\n");

								graphicControl.append("  var config = { type: 'threejs' };\n");
							} else {
								graphicControl.append("plot( z1, ");
								range(graphicControl, plotRangeX);
								graphicControl.append(", { } );\n\n\n");

								graphicControl.append("  var config = { type: 'svg' };\n");
							}

							// var data = [ p1, p2 ];
							graphicControl.append("  var data = [ p1 ];");

							js = js.replaceAll("`4`", graphicControl.toString());

							return F.JSFormData(js, "mathcell");

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

		private static void range(StringBuilder graphicControl, IAST plotRange) throws IOException {
			graphicControl.append("[");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg2()));
			graphicControl.append(", ");
			graphicControl.append(OutputFunctions.toJavaScript(plotRange.arg3()));
			graphicControl.append("]");
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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
