package org.matheclipse.core.builtin;

import java.io.IOException;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
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
					// " var p2 = plot( x => Math.cos(x-phase), [0, 2*Math.PI], { color: 'purple' } );\n" + //
					// "\n" + //
					// " var data = [ p1, p2 ];\n" + //
					"\n" + //
					"  var config = { type: 'svg' };\n" + //
					"\n" + //
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
				if (ast.arg1().isAST(F.Plot)) {
					IAST plot = (IAST) ast.arg1();
					if (plot.size() == 3 && plot.arg2().isList()) {
						IAST plotRange = (IAST) plot.arg2();
						if (plotRange.isAST3() && plotRange.arg1().isSymbol()) {
							ISymbol plotSymbol = (ISymbol) plotRange.arg1();
							if (ast.arg2().isList()) {
								IAST sliderRange = (IAST) ast.arg2();
								if (sliderRange.isAST3() && sliderRange.arg1().isSymbol()) {
									String js = MATHCELL;
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
									variable.append(" = document.getElementById( id + '");
									variable.append(sliderSymbol);
									variable.append("' ).value;\n");
									js = js.replaceAll("`2`", variable.toString());

									// plot( x => (Math.sin(x*(1+a*x))), [0, 2*Math.PI], { } )
									StringBuilder function = new StringBuilder();
									function.append("var p1 = plot( ");
									function.append(OutputFunctions.toJavaScript(plotSymbol));
									function.append(" => (");
									function.append(OutputFunctions.toJavaScript(plot.arg1()));
									function.append("), [");
									function.append(OutputFunctions.toJavaScript(plotRange.arg2()));
									function.append(", ");
									function.append(OutputFunctions.toJavaScript(plotRange.arg3()));
									function.append("], { } );\n");
									// var data = [ p1, p2 ];
									function.append("  var data = [ p1 ];");

									js = js.replaceAll("`3`", function.toString());

									return F.JSFormData(js, "mathcell");
								}
							}
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

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
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
