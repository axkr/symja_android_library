package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.ExprMonomial;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.ExprTermOrderByName;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class PolynomialExample {
	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator();
			IExpr expr = util.eval("x^2+y+a*x+b*y+c");
			System.out.println(expr.toString());

			final IAST variables = F.List(F.x, F.y);
			ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables, variables.size() - 1,
					ExprTermOrderByName.Lexicographic, false);

			ExprPolynomial poly = ring.create(expr);
			System.out.println(poly.toString());

			// x degree
			System.out.println(poly.degree(0));
			// y degree
			System.out.println(poly.degree(1));

			// show internal structure:
			System.out.println(poly.coefficientRules());

			System.out.println();
			for (ExprMonomial monomial : poly) {
				System.out.println(monomial.toString());
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
