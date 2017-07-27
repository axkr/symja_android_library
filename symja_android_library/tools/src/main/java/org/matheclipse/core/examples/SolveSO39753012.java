package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * See <a href="http://stackoverflow.com/questions/39753012">Stackoverflow 39753012</a>
 */
public class SolveSO39753012 {

	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator();
			IExpr eq = F.Equal(F.Plus(F.a, F.b), F.c);
			IExpr eq1 = eq.replaceAll(F.Rule(F.a, F.integer(1)));
			eq1 = eq1.replaceAll(F.Rule(F.b, F.integer(2)));

			// Solve(1+2==c, c)
			IExpr result = util.eval(F.Solve(eq1, F.c));
			// print: {{c->3}}
			System.out.println(result.toString());

			IExpr eq2 = eq.replaceAll(F.Rule(F.a, F.integer(1)));
			eq2 = eq2.replaceAll(F.Rule(F.c, F.integer(3)));
			// Solve(1+b==3, b)
			result = util.eval(F.Solve(eq2, F.b));
			// print: {{b->2}}
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
