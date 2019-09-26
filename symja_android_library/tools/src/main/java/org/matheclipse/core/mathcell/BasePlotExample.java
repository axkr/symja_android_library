package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public abstract class BasePlotExample {

	public String exampleFunction() {
		return "Manipulate(ListPlot(Table({Sin(t), Cos(t*a)}, {t, 100})), {a,1,4,1})";
	}

	public void generateHTML() {
		try {
			ExprEvaluator util = new ExprEvaluator();

			IExpr result = util.eval(exampleFunction());
			if (result.isAST(F.JSFormData, 3)) {
				if (result.second().toString().equals("mathcell")) {
					String manipulateStr = ((IAST) result).arg1().toString();
					String js = Config.MATHCELL_PAGE;
					js = js.replaceAll("`1`", manipulateStr);
					System.out.println(js);
				} else {
					String manipulateStr = ((IAST) result).arg1().toString();
					String js = Config.JSXGRAPH_PAGE;
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
