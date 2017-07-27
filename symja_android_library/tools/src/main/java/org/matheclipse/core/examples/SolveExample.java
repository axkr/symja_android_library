package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class SolveExample {

	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator();
			IExpr result = util.eval("Solve(2*x==5 + 4*x,x)");
			// print: {{x->-5/2}}
			System.out.println(result.toString());

			result = util.eval("Roots(2*x==5+4*x, x)");
			// print: x==-5/2
			System.out.println(result.toString());
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
