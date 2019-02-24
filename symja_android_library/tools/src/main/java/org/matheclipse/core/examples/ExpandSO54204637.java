package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * See <a href="http://stackoverflow.com/questions/54204637">Stackoverflow 54204637</a>
 */
public class ExpandSO54204637 {

	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator();
			IExpr expr = util.eval("a*(b+c)");

			// use https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Distribute.md
			IExpr result = util.eval(F.Distribute(expr));
			// print: a*b+a*c
			System.out.println(result.toString());

			// use https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Expand.md
			result = util.eval(F.Expand(expr));
			// print: a*b+a*c
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
