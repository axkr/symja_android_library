package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public abstract class BasePlotExample {
	final static String WEB_PAGE_JSXGRAPH = //
			"<html>\n" + //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<title>JSXGraph</title>\n" + //
					"</head>\n" + //
					"\n" + //
					"<body>\n" + //
					"\n" + //
					"<script src='https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.3.5/jsxgraphcore.js'\n" + //
					"        type='text/javascript'></script>\n" + //
					"<script src='https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.3.5/geonext.min.js'\n" + //
					"        type='text/javascript'></script>\n" + //
					"<div id='jxgbox' class='jxgbox' style='width:500px; height:500px;'></div>\n" + //
					"<script type='text/javascript'>\n" + //
					"var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-10.0,2.0,10.0,-2.0]});\n"
					+ "board.suspendUpdate();\n" + //
					"`1`\n" + //
					"board.unsuspendUpdate();\n" + //
					"</script>\n" + //
					"\n" + //
					"</body>\n" + //
					"</html>";//
	private final static String WEB_PAGE_MATHCELL = //
			"<html>\n" + //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<title>MathCell</title>\n" + //
					"</head>\n" + //
					"\n" + //
					"<body>\n" + //
					"\n" + //
					"<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.4/build/math.js\"></script>" + //
					"\n" + //
					"<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.7.5/build/mathcell.js\"></script>\n"
					+ //
					"<script src=\"https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML\"></script>" +//
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

	public String exampleFunction() {
		return "Manipulate(ListPlot(Table({Sin(t), Cos(t*a)}, {t, 100})), {a,1,4,1})";
	}

	public void generateHTML() {
		try {
			Config.USE_MANIPULATE_JS = true;
			ExprEvaluator util = new ExprEvaluator();

			IExpr result = util.eval(exampleFunction());
			if (result.isAST(F.JSFormData, 3)) {
				if (result.second().toString().equals("mathcell")) {
					String manipulateStr = ((IAST) result).arg1().toString();
					String js = WEB_PAGE_MATHCELL;
					js = js.replaceAll("`1`", manipulateStr);
					System.out.println(js);
				} else {
					String manipulateStr = ((IAST) result).arg1().toString();
					String js = WEB_PAGE_JSXGRAPH;
					js = js.replaceAll("`1`", manipulateStr);
					System.out.println(js);
				}
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
