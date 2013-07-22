package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Predicate;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;

/**
 * Greatest common divisor of two polynomials. See: <a href=
 * "http://en.wikipedia.org/wiki/Greatest_common_divisor_of_two_polynomials"
 * >Wikipedia:Greatest common divisor of two polynomials</a>
 */
public class Cancel extends AbstractFunctionEvaluator {
	/**
	 * this predicate identifies polynomial expressions. It requires that the
	 * given expression is already expanded for <code>Plus,Power,Times</code>
	 * operations.
	 * 
	 */
	private final class PolynomialPredicate implements Predicate<IExpr> {

		public boolean apply(IExpr expr) {
			if (expr.isRational()) {
				return true;
			}
			if (expr.isSymbol()) {
				return true;
			}
			if (expr.isTimes() || expr.isPlus()) {
				IAST ast = (IAST) expr;
				for (int i = 1; i < ast.size(); i++) {
					if (!apply(ast.get(i))) {
						return false;
					}
				}
				return true;
			}
			if (expr.isPower() && ((IAST) expr).get(1).isSymbol() && ((IAST) expr).get(2).isInteger()) {
				try {
					int in = ((IInteger) ((IAST) expr).get(2)).toInt();
					if (in > 0) {
						return true;
					}
				} catch (ArithmeticException ae) {

				}
				return false;
			}
			return false;
		}
	}

	public Cancel() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		IExpr arg = F.evalExpandAll(ast.get(1));
		try {
			if (arg.isPlus()) {
				IAST result = ((IAST) arg).map(Functors.evalArg(F.Cancel(F.Slot1), 1, EvalEngine.get()));
				if (result == null) {
					return arg;
				}
				return result;
			} else if (arg.isTimes() || arg.isPower()) {
				IExpr[] parts = Apart.getFractionalParts(arg);
				if (parts != null) {
					if (parts[0].isPlus() && parts[1].isPlus()) {
						IAST[] numParts = ((IAST) parts[0]).split(new PolynomialPredicate());
						IAST[] denParts = ((IAST) parts[1]).split(new PolynomialPredicate());
						IExpr denParts0 = F.eval(denParts[0]);
						if (!denParts0.equals(F.C1)) {
							IExpr[] result = Cancel.cancelGCD(numParts[0], denParts0);
							if (result != null) {
								return F.Times(result[0], numParts[1], F.Power(F.Times(result[1], denParts[1]), F.CN1));
							}
						}
					}
				}
			}
		} catch (JASConversionException jce) {
			if (Config.DEBUG) {
				jce.printStackTrace();
			}
		}
		return arg;
	}

	/**
	 * Calculate the result array
	 * <code>[ poly1.divide(gcd(poly1, poly2)), poly2.divide(gcd(poly1, poly2)) ]</code>
	 * if the given expressions <code>poly1</code> and <code>poly2</code> are
	 * univariate polynomials with equal variable name.
	 * 
	 * 
	 * @param poly1
	 *            univariate polynomial
	 * @param poly2
	 *            univariate polynomial
	 * @return <code>null</code> if the expressions couldn't be converted to JAS
	 *         polynomials
	 */
	public static IExpr[] cancelGCD(IExpr poly1, IExpr poly2) throws JASConversionException {

		try {
			ExprVariables eVar = new ExprVariables(poly1);
			eVar.addVarList(poly2);
			if (!eVar.isSize(1)) {
				// gcd only possible for univariate polynomials
				return null;
			}

			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			JASConvert<BigRational> jas = new JASConvert<BigRational>(r.toList(), BigRational.ZERO);
			GenPolynomial<BigRational> p1 = jas.expr2JAS(poly1);
			GenPolynomial<BigRational> p2 = jas.expr2JAS(poly2);
			GenPolynomial<BigRational> gcd = p1.gcd(p2);
			IExpr[] result = new IExpr[2];
			if (gcd.isONE()) {
				result[0] = jas.rationalPoly2Expr(p1);
				result[1] = jas.rationalPoly2Expr(p2);
			} else {
				result[0] = jas.rationalPoly2Expr(p1.divide(gcd));
				result[1] = jas.rationalPoly2Expr(p2.divide(gcd));
			}
			return result;
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}