package org.matheclipse.core.reflection.system;

import java.util.function.Predicate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;

import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;

/**
 * Cancel(expr) - cancels out common factors in numerators and denominators.
 */
public class Cancel extends AbstractFunctionEvaluator {

	/**
	 * This predicate identifies polynomial expressions. It requires that the
	 * given expression is already expanded for
	 * <code>Plus,Power and Times</code> operations.
	 */
	private static final class PolynomialPredicate implements Predicate<IExpr> {

		@Override
		public boolean test(IExpr expr) {
			return expr.isPolynomial(F.List());
		}
	}

	/**
	 * Return the result divided by the gcd value.
	 * 
	 * @param numeratorPlus
	 *            a <code>Plus[...]</code> expression as the numerator
	 * @param denominatorInt
	 *            an integer value for the denominator
	 * @param gcd
	 *            the integer gcd value
	 * @return
	 */
	private static IExpr[] calculatePlusIntegerGCD(IAST numeratorPlus, IInteger denominatorInt, IInteger gcd) {
		for (int i = 1; i < numeratorPlus.size(); i++) {
			if (numeratorPlus.get(i).isInteger()) {
				numeratorPlus.set(i, ((IInteger) numeratorPlus.get(i)).div(gcd));
			} else if (numeratorPlus.get(i).isTimes() && numeratorPlus.get(i).getAt(1).isInteger()) {
				IAST times = ((IAST) numeratorPlus.get(i)).clone();
				times.set(1, ((IInteger) times.arg1()).div(gcd));
				numeratorPlus.set(i, times);
			} else {
				throw new WrongArgumentType(numeratorPlus, numeratorPlus.get(i), i, "unexpected argument");
			}
		}
		IExpr[] result = new IExpr[3];
		result[0] = F.C1;
		result[1] = numeratorPlus;
		result[2] = denominatorInt.div(gcd);
		return result;
	}

	/**
	 * Calculate the 3 elements result array
	 * 
	 * <pre>
	 * [ 
	 *   commonFactor, 
	 *   numeratorPolynomial.divide(gcd(numeratorPolynomial, denominatorPolynomial)), 
	 *   denominatorPolynomial.divide(gcd(numeratorPolynomial, denominatorPolynomial)) 
	 * ]
	 * </pre>
	 * 
	 * for the given expressions <code>numeratorPolynomial</code> and
	 * <code>denominatorPolynomial</code>.
	 * 
	 * 
	 * @param numeratorPolynomial
	 *            a <code>BigRational</code> polynomial which could be converted
	 *            to JAS polynomial
	 * @param denominatorPolynomial
	 *            a <code>BigRational</code> polynomial which could be converted
	 *            to JAS polynomial
	 * @return <code>null</code> if the expressions couldn't be converted to JAS
	 *         polynomials or gcd equals 1
	 * @throws JASConversionException
	 */
	public static IExpr[] cancelGCD(IExpr numeratorPolynomial, IExpr denominatorPolynomial)
			throws JASConversionException {

		try {
			if (denominatorPolynomial.isInteger() && numeratorPolynomial.isPlus()) {
				IExpr[] result = cancelPlusIntegerGCD((IAST) numeratorPolynomial, (IInteger) denominatorPolynomial);
				if (result != null) {
					return result;
				}
			}

			VariablesSet eVar = new VariablesSet(numeratorPolynomial);
			eVar.addVarList(denominatorPolynomial);
			if (eVar.size() == 0) {
				return null;
			}

			IAST vars = eVar.getVarList();
			ExprPolynomialRing ring = new ExprPolynomialRing(vars);
			ExprPolynomial pol1 = ring.create(numeratorPolynomial);
			ExprPolynomial pol2 = ring.create(denominatorPolynomial);
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
		} catch (RuntimeException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Calculate the GCD[] of the integer factors in each element of the
	 * <code>numeratorPlus</code> expression with the
	 * <code>denominatorInt</code>. After that return the result divided by the
	 * gcd value, if possible.
	 * 
	 * @param numeratorPlus
	 *            a <code>Plus[...]</code> expression as the numerator
	 * @param denominatorInt
	 *            an integer value for the denominator
	 * @return <code>null</code> if no gcd value was found
	 */
	private static IExpr[] cancelPlusIntegerGCD(IAST numeratorPlus, IInteger denominatorInt) {
		IAST plus = ((IAST) numeratorPlus).clone();
		IAST gcd = F.ast(F.GCD, plus.size() + 1, false);
		gcd.append(denominatorInt);
		boolean evaled = true;
		for (int i = 1; i < plus.size(); i++) {
			if (plus.get(i).isInteger()) {
				gcd.append(plus.get(i));
			} else if (plus.get(i).isTimes() && plus.get(i).getAt(1).isInteger()) {
				gcd.append(plus.get(i).getAt(1));
			} else {
				evaled = false;
				break;
			}
		}
		if (evaled) {
			// GCD() has attribute Orderless, so the arguments will
			// be sorted by evaluation!
			IExpr temp = F.eval(gcd);
			if (temp.isInteger() && !temp.isOne()) {
				IInteger igcd = (IInteger) temp;
				return calculatePlusIntegerGCD(plus, denominatorInt, igcd);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param powerTimesAST
	 *            an <code>Times[...] or Power[...]</code> AST, where common
	 *            factors should be canceled out.
	 * @return <code>F.NIL</code> is no evaluation was possible
	 * @throws JASConversionException
	 */
	public static IExpr cancelPowerTimes(IExpr powerTimesAST) throws JASConversionException {
		IExpr[] parts = Apart.getFractionalParts(powerTimesAST, false);
		if (parts != null && parts[0].isPlus() && parts[1].isPlus()) {
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
		return F.NIL;
	}

	public Cancel() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (ast.isAST1() && arg1.isAtom()) {
			return arg1;
		}
		IAST temp = Thread.threadPlusLogicEquationOperators(arg1, ast, 1);
		if (temp.isPresent()) {
			return temp;
		}
		try {
			if (arg1.isTimes() || arg1.isPower()) {
				IExpr result = cancelPowerTimes(arg1);
				if (result.isPresent()) {
					return result;
				}
			}
			IExpr expandedArg1 = F.evalExpandAll(arg1);

			if (expandedArg1.isPlus()) {
				return ((IAST) expandedArg1).mapAt(F.Cancel(null), 1);
			} else if (expandedArg1.isTimes() || expandedArg1.isPower()) {
				IExpr result = cancelPowerTimes(expandedArg1);
				if (result.isPresent()) {
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

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(newSymbol);
	}
}