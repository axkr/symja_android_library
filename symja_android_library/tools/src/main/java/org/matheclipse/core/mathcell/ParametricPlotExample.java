package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class ParametricPlotExample {
	private final static String WEB_PAGE = //
			"<html>\n" + //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<title>MathCell</title>\n" + //
					"</head>\n" + //
					"\n" + //
					"<body>\n" + //
					"\n" + //
					"<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.1/build/math.js\"></script>" + //
					"\n" + //
					"<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.7.0/build/mathcell.js\"></script>\n"
					+ //
						// "<script
						// src=\"https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML\"></script>"
						// + //
					"\n" + //
					"<p style=\"text-align: center; line-height: 2\"><span style=\"font-size: 20pt\">MathCell</span></p>\n"
					+ //
					"\n" + //
					"<div class=\"mathcell\" style=\"height: 4in\">\n" + //
					"<script>\n" + //
					"\n" + //
					"var parent = document.scripts[ document.scripts.length - 1 ].parentNode;\n" + //
					"\n" + //
					"var id = generateId();\n" + //
					"parent.id = id;\n" + //
					"\n" + //
					"`1`\n" + //
					"\n" + //
					"parent.update( id );\n" + //
					"\n" + //
					"</script>\n" + //
					"</div>\n" + //
					"\n" + //
					"</body>\n" + //
					"</html>";//

	public static void main(String[] args) {
		try {
			Config.USE_MATHCELL=true;
			ExprEvaluator util = new ExprEvaluator();

			//Cot(x)+ArcCot(x)
			IExpr result = util.eval(
					"ParametricPlot({Sin(t), Cos(t^2)}, {t, 0, 2*Pi})");
			if (result.isAST(F.JSFormData, 3) && result.second().toString().equals("mathcell")) {
				String manipulateStr = ((IAST) result).arg1().toString();
				String js = WEB_PAGE;
				js = js.replaceAll("`1`", manipulateStr);
				System.out.println(js);
			}
		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} catch (final StackOverflowError soe) {
			System.out.println(soe.getMessage());
		} catch (final OutOfMemoryError oome) {
			System.out.println(oome.getMessage());
		}
	}
}
