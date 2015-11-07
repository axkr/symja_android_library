package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.parser.client.SyntaxError;

import java.util.function.Predicate;

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
	 */
	private final static class PolynomialPredicate implements Predicate<IExpr> {

		@Override
		public boolean test(IExpr expr) {
			return expr.isPolynomial(F.List());
		}
	}

	public Cancel() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (ast.size() == 2 && arg1.isAtom()) {
			return arg1;
		}
		if (arg1.isPlus()) {
			return ((IAST) arg1).mapAt(F.Cancel(null), 1);
		}
		try {
			if (arg1.isTimes() || arg1.isPower()) {
				IExpr result = cancelPowerTimes(arg1);
				if (result != null) {
					return result;
				}
			}
			IExpr expandedArg1 = F.evalExpandAll(arg1);

			if (expandedArg1.isPlus()) {
				return ((IAST) expandedArg1).mapAt(F.Cancel(null), 1);
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
				IAST[] numParts = ((IAST) parts[0]).filter(new PolynomialPredicate());
				IAST[] denParts = ((IAST) parts[1]).filter(new PolynomialPredicate());
				IExpr denParts0 = F.eval(denParts[0]);
				if (!denParts0.isOne()) {
					IExpr[] result = Cancel.cancelGCD(numParts[0], denParts0);
					if (result != null) {
						return F.Times(result[0], result[1], numParts[1].getOneIdentity(F.C1),
								F.Power(F.Times(result[2], denParts[1].getOneIdentity(F.C1)), F.CN1));
					}
				}
			}
		}
		return null;
	}

	/**
	 * Calculate the 3 elements result array
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
			VariablesSet eVar = new VariablesSet(poly1);
			eVar.addVarList(poly2);
			if (eVar.size() == 0) {
				return null;
			}

			IAST vars = eVar.getVarList();
			ExprPolynomialRing ring = new ExprPolynomialRing(vars);
			ExprPolynomial pol1 = ring.create(poly1);
			ExprPolynomial pol2 = ring.create(poly2);
			// Polynomial pol1 = new Polynomial(poly1, eVar.getVarList(), false);
			// Polynomial pol2 = new Polynomial(poly2, eVar.getVarList(), false);
			// if (pol1.createPolynomial(poly1, true, false) && pol2.createPolynomial(poly2, true, false)) {
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
			}
			return result;
			// }
		} catch (Exception e) {
			if (Config.DEBUG) {
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