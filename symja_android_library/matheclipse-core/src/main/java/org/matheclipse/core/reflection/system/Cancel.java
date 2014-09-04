package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.Polynomial;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Predicate;

import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;

/**
 * Cancel(expr) - cancels out common factors in numerators and denominators.
 */
public class Cancel extends AbstractFunctionEvaluator {
	/**
	 * This predicate identifies polynomial expressions. It requires that the given expression is already expanded for
	 * <code>Plus,Power and Times</code> operations.
	 * 
	 */
	private final static class PolynomialPredicate implements Predicate<IExpr> {

		public boolean apply(IExpr expr) {
			return PolynomialQ.polynomialQ(expr, F.List());
			// if (expr.isRational()) {
			// return true;
			// }
			// if (expr.isSymbol()) {
			// return true;
			// }
			// if (expr.isTimes() || expr.isPlus()) {
			// IAST ast = (IAST) expr;
			// for (int i = 1; i < ast.size(); i++) {
			// if (!apply(ast.get(i))) {
			// return false;
			// }
			// }
			// return true;
			// }
			// if (expr.isPower() && ((IAST) expr).arg1().isSymbol() && ((IAST) expr).arg2().isInteger()) {
			// try {
			// int in = ((IInteger) ((IAST) expr).arg2()).toInt();
			// if (in > 0) {
			// return true;
			// }
			// } catch (ArithmeticException ae) {
			//
			// }
			// return false;
			// }
			// return false;
		}
	}

	public Cancel() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		if (arg1.isPlus()) {
			// IAST result = ((IAST) arg1).map(Functors.replaceArg(F.Cancel(F.Slot1), 1));
			IAST result = ((IAST) arg1).mapAt(F.Cancel(null), 1);
			if (result != null) {
				return result;
			}
		}
		IExpr expandedArg1 = F.evalExpandAll(arg1);
		try {
			if (expandedArg1.isPlus()) {
				// IAST result = ((IAST) expandedArg1).map(Functors.replaceArg(F.Cancel(F.Slot1), 1));
				IAST result = ((IAST) expandedArg1).mapAt(F.Cancel(null), 1);
				if (result != null) {
					return result;
				}
			} else if (expandedArg1.isTimes() || expandedArg1.isPower()) {
				IExpr result = cancelPowerTimes(expandedArg1);
				if (result != null) {
					return result;
				}
			}
		} catch (JASConversionException jce) {
			if (Config.DEBUG) {
				jce.printStackTrace();
			}
		}
		return arg1;
	}

	/**
	 * 
	 * @param powerTimesAST
	 *            an <code>Times[...] or Power[...]</code> AST, where common factors should be canceled out.
	 * @return
	 * @throws JASConversionException
	 */
	public static IExpr cancelPowerTimes(IExpr powerTimesAST) throws JASConversionException {
		IExpr[] parts = Apart.getFractionalParts(powerTimesAST);
		if (parts != null) {
			if (parts[0].isPlus() && parts[1].isPlus()) {
				IAST[] numParts = ((IAST) parts[0]).split(new PolynomialPredicate());
				IAST[] denParts = ((IAST) parts[1]).split(new PolynomialPredicate());
				IExpr denParts0 = F.eval(denParts[0]);
				if (!denParts0.equals(F.C1)) {
					IExpr[] result = Cancel.cancelGCD(numParts[0], denParts0);
					if (result != null) {
						return F.Times(result[0], result[1], numParts[1], F.Power(F.Times(result[2], denParts[1]), F.CN1));
					}
				}
			}
		}
		return null;
	}

	/**
	 * Calculate the 3 element result array
	 * <code>[ commonFactor, poly1.divide(gcd(poly1, poly2)), poly2.divide(gcd(poly1, poly2)) ]</code> for the given expressions
	 * <code>poly1</code> and <code>poly2</code>.
	 * 
	 * 
	 * @param poly1
	 *            a <code>BigRational</code> polynomial which could be converted to JAS polynomial
	 * @param poly2
	 *            a <code>BigRational</code> polynomial which could be converted to JAS polynomial
	 * @return <code>null</code> if the expressions couldn't be converted to JAS polynomials or gcd equals 1
	 */
	public static IExpr[] cancelGCD(IExpr poly1, IExpr poly2) throws JASConversionException {

		try {
			ExprVariables eVar = new ExprVariables(poly1);
			eVar.addVarList(poly2);
			if (eVar.size() == 0) {
				return null;
			}

			Polynomial pol1 = new Polynomial(poly1, eVar);
			Polynomial pol2 = new Polynomial(poly2, eVar);
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			JASIExpr jas = new JASIExpr(r.toList(), true);
			GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
			GenPolynomial<IExpr> p2 = jas.expr2IExprJAS(pol2);

			GreatestCommonDivisor<IExpr> engine;
			engine = GCDFactory.getImplementation(new ExprRingFactory());
			GenPolynomial<IExpr> gcd = engine.gcd(p1, p2);
			IExpr[] result = new IExpr[3];
			if (gcd.isONE()) {
				result[0] = jas.exprPoly2Expr(gcd);
				result[1] = jas.exprPoly2Expr(p1);
				result[2] = jas.exprPoly2Expr(p2);
			} else {
				result[0] = F.C1;
				result[1] = F.eval(jas.exprPoly2Expr(p1.divide(gcd)));
				result[2] = F.eval(jas.exprPoly2Expr(p2.divide(gcd)));
				// java.math.BigInteger commonNumerator, commonDenominator;
				// Object[] objects = jas.factorTerms(p1.divide(gcd));
				// commonNumerator = (java.math.BigInteger) objects[0];
				// result[1] = jas.integerPoly2Expr((GenPolynomial<BigInteger>) objects[2]);
				// objects = jas.factorTerms(p2.divide(gcd));
				// commonDenominator = (java.math.BigInteger) objects[0];
				// result[2] = jas.integerPoly2Expr((GenPolynomial<BigInteger>) objects[2]);
				// result[0] = F.fraction(commonNumerator, commonDenominator);
			}
			return result;
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
		// try {
		// ExprVariables eVar = new ExprVariables(poly1);
		// eVar.addVarList(poly2);
		//
		// ASTRange r = new ASTRange(eVar.getVarList(), 1);
		// JASConvert<BigRational> jas = new JASConvert<BigRational>(r.toList(), BigRational.ZERO);
		// GenPolynomial<BigRational> p1 = jas.expr2JAS(poly1, false);
		// GenPolynomial<BigRational> p2 = jas.expr2JAS(poly2, false);
		//
		// BigRational cofac = new BigRational();
		// GreatestCommonDivisor<BigRational> engine;
		// engine = GCDFactory.getImplementation(cofac);
		// GenPolynomial<BigRational> gcd = engine.gcd(p1, p2);
		// IExpr[] result = new IExpr[3];
		// if (gcd.isONE()) {
		// result[0] = jas.rationalPoly2Expr(gcd);
		// result[1] = jas.rationalPoly2Expr(p1);
		// result[2] = jas.rationalPoly2Expr(p2);
		// } else {
		// java.math.BigInteger commonNumerator, commonDenominator;
		// Object[] objects = jas.factorTerms(p1.divide(gcd));
		// commonNumerator = (java.math.BigInteger) objects[0];
		// result[1] = jas.integerPoly2Expr((GenPolynomial<BigInteger>) objects[2]);
		// objects = jas.factorTerms(p2.divide(gcd));
		// commonDenominator = (java.math.BigInteger) objects[0];
		// result[2] = jas.integerPoly2Expr((GenPolynomial<BigInteger>) objects[2]);
		// result[0] = F.fraction(commonNumerator, commonDenominator);
		// }
		// return result;
		// } catch (Exception e) {
		// if (Config.SHOW_STACKTRACE) {
		// e.printStackTrace();
		// }
		// }
		// return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}