package org.matheclipse.core.examples;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * See <a href="http://stackoverflow.com/questions/43024172">Stackoverflow
 * 43024172</a>
 */
public class SolveSO43024172 {

	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator();
			IExpr expr = util.eval("(x1)^2+4*(x2)^2-2*x1-4*x2");
			System.out.println(expr.toString());
			
			// determine the variables used in the expression
			IAST variableList = VariablesSet.getVariables(expr);
			System.out.println(variableList.toString());
			
			IExpr a = util.eval("a");
			IExpr x1 = util.eval("x1");
			IExpr x2 = util.eval("x2");
			IExpr x1Substitute = util.eval("5+a");
			IExpr x2Substitute = util.eval("2");

			IAST astRules = F.List(F.Rule(x1, x1Substitute), F.Rule(x2, x2Substitute));
			// {x1->5+a,x2->2}
			System.out.println(astRules.toString());

			IExpr replacedExpr = expr.replaceAll(astRules);
			// -2*(5+a)+(5+a)^2+(-4)*2+4*2^2
			System.out.println(replacedExpr.toString());
			// 8+(5+a)^2-2*(5+a)
			replacedExpr = util.eval(replacedExpr);
			System.out.println(replacedExpr.toString());

			// replacedExpr = util.evaluate(F.ExpandAll(replacedExpr));
			// 23+8*a+a^2
			// System.out.println(replacedExpr.toString());

			IExpr derivedExpr = util.eval(F.D(replacedExpr, a));
			// -2+2*(5+a)
			System.out.println(derivedExpr.toString());

			IExpr equation = F.Equal(derivedExpr, F.C0);
			// -2+2*(5+a)==0
			System.out.println(equation.toString());

			IExpr solvedEquation = util.eval(F.Solve(equation, a));
			// {{a->-4}}
			System.out.println(solvedEquation.toString());

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
