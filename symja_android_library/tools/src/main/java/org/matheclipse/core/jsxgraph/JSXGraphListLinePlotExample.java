package org.matheclipse.core.jsxgraph;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class JSXGraphListLinePlotExample {
	final static String WEB_PAGE = //
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

	public static void main(String[] args) {
		try {
			Config.USE_JSXGRAPH = true;
			ExprEvaluator util = new ExprEvaluator();

			IExpr result = util.eval("Manipulate(ListLinePlot(Table({Sin(t), Cos(t*a)}, {t, 50})), {a,1,4,1})");
			if (result.isAST(F.JSFormData, 3) && result.second().toString().equals("jsxgraph")) {
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
