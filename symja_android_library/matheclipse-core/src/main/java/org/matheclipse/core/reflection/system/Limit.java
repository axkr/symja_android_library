package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CNInfinity;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Limit;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negative;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.SetDelayed;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.n;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.PartialFractionGenerator;

import edu.jas.poly.GenPolynomial;

/**
 * Limit of a function. See <a href="http://en.wikipedia.org/wiki/List_of_limits">List of Limits</a>
 */
public class Limit extends AbstractFunctionEvaluator {

	// {
	// Limit[x_^n_IntegerQ, x_Symbol->Infinity]:= 0 /; Negative[n],
	// Limit[x_^n_IntegerQ, x_Symbol->DirectedInfinity[-1]]:= 0 /; Negative[n],
	// Limit[(1+x_^(-1))^x_, x_Symbol->Infinity]=E,
	// Limit[(1+a_*(x_^(-1)))^x_, x_Symbol->Infinity]=E^(-1) /; FreeQ(a,x)
	// }

	final static IAST RULES = List(
			SetDelayed(Limit(Power(x_, $p(n, $s("IntegerQ"))), Rule(x_Symbol, CInfinity)), Condition(C0, Negative(n))),
			SetDelayed(Limit(Power(x_, $p(n, $s("IntegerQ"))), Rule(x_Symbol, CNInfinity)), Condition(C0, Negative(n))),
			Set(Limit(Power(Plus(C1, Power(x_, CN1)), x_), Rule(x_Symbol, CInfinity)), E),
			Set(Limit(Power(Plus(C1, Times(a_, Power(x_, CN1))), x_), Rule(x_Symbol, CInfinity)),
					Condition(Power(E, a), FreeQ(a, x))));

	/**
	 * Try L'hospitales rule. See <a href="http://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule">Wikipedia L'Hôpital's rule</a>
	 * 
	 * @param numerator
	 * @param denominator
	 * @param symbol
	 *            the variable for which to approach to the limit
	 * @param limit
	 *            the limit value which the variable should approach to
	 * @param rule
	 * @param direction
	 *            <ul>
	 *            <li>0 - no direction defined</li>
	 *            <li>1 - approach from smaller values</li>
	 *            <li>-1 - approach from larger values</li>
	 * @return
	 */
	private static IExpr lHospitalesRule(IExpr numerator, IExpr denominator, ISymbol symbol, IExpr limit, IAST rule, int direction) {
		EvalEngine engine = EvalEngine.get();
		int recursionLimit = engine.getRecursionLimit();
		if (recursionLimit > 0) {
			IExpr expr = F.eval(F.Times(F.D(numerator, symbol), F.Power(F.D(denominator, symbol), F.CN1)));
			return evalLimit(expr, symbol, limit, rule, direction, false);
		}
		try {
			if (recursionLimit <= 0) {
				// set recursion limit for using l'Hospitales rule
				engine.setRecursionLimit(128);
			}
			IExpr expr = F.eval(F.Times(F.D(numerator, symbol), F.Power(F.D(denominator, symbol), F.CN1)));
			return evalLimit(expr, symbol, limit, rule, direction, false);
		} catch (RecursionLimitExceeded rle) {
			engine.setRecursionLimit(recursionLimit);
		} finally {
			engine.setRecursionLimit(recursionLimit);
		}
		return null;
	}

	/**
	 * 
	 * @param expr
	 * @param symbol
	 *            the variable for which to approach to the limit
	 * @param limit
	 *            the limit value which the variable should approach to
	 * @param rule
	 * @param direction
	 *            <ul>
	 *            <li>0 - no direction defined</li>
	 *            <li>1 - approach from smaller values</li>
	 *            <li>-1 - approach from larger values</li>
	 * @param evalExpr
	 * @return
	 */
	public static IExpr evalLimit(final IExpr expr, ISymbol symbol, IExpr limit, IAST rule, int direction, boolean evalExpr) {
		IExpr expression = expr;
		if (evalExpr) {
			IExpr result = F.eval(expression);
			if (result.isNumericFunction()) {
				return result;
			}
			if (!result.equals(F.Indeterminate)) {
				expression = result;
			}

			if (limit.isNumericFunction()) {
				result = expression.replaceAll(rule);
				if (result != null) {
					result = F.eval(result);
					if (result.isNumericFunction()) {
						return result;
					}
				}
			}
		}
		if (expression.isFree(symbol, true)) {
			// Limit[a_,sym->lim] -> a
			return expression;
		}
		if (expression.equals(symbol)) {
			// Limit[x_,x_->lim] -> lim
			return limit;
		}
		if (expression.isAST()) {
			final IAST arg1 = (IAST) expression;
			final IExpr header = arg1.head();
			if (arg1.size() == 2) {
				if (header.equals(F.Sin) || header.equals(F.Cos)) {
					return F.$(header, F.Limit(arg1.arg1(), rule));
				}
			}
			if (header == F.Plus) {
				// Limit[a_+b_+c_,sym->lim] ->
				// Limit[a,sym->lim]+Limit[b,sym->lim]+Limit[c,sym->lim]
				if (limit.isInfinity() || limit.isNegativeInfinity()) {
					GenPolynomial<IExpr> poly = PolynomialQ.polynomial(arg1, symbol, true);
					if (poly != null) {
						IExpr coeff = poly.leadingBaseCoefficient();
						long oddDegree = poly.degree() % 2;
						if (oddDegree == 1) {
							// odd degree
							return F.Limit(F.Times(coeff, limit), rule);
						} else {
							// even degree
							return F.Limit(F.Times(coeff, F.CInfinity), rule);
						}
					}
				}
				return mapLimit(arg1, rule);
			} else if (header == F.Times) {
				IExpr[] parts = org.matheclipse.core.reflection.system.Apart.getFractionalPartsTimes(arg1, false);
				if (parts != null) {

					IExpr numerator = parts[0];
					IExpr denominator = parts[1];
					if (limit.isInfinity() || limit.isNegativeInfinity()) {
						GenPolynomial<IExpr> denominatorPoly = PolynomialQ.polynomial(denominator, symbol, true);
						if (denominatorPoly != null) {
							GenPolynomial<IExpr> numeratorPoly = PolynomialQ.polynomial(numerator, symbol, true);
							if (numeratorPoly != null) {
								return limitsInfinityOfRationalFunctions(numeratorPoly, denominatorPoly, symbol, limit, rule);
							}
						}
					}

					IAST plusResult = org.matheclipse.core.reflection.system.Apart.partialFractionDecompositionRational(
							new PartialFractionGenerator(), parts, symbol);
					if (plusResult != null) {
						// OneIdentity if plusResult.size() == 2
						if (plusResult.size() > 2) {
							return mapLimit(plusResult, rule);
						}
					}

					IExpr temp = timesLimit(numerator, denominator, symbol, limit, rule, direction);
					if (temp != null) {
						return temp;
					}
				}
				return mapLimit(arg1, rule);
			} else if (arg1.isAST(F.Power, 3)) {
				if (arg1.get(2).isInteger()) {
					// Limit[a_^n_,sym->lim] -> Limit[a,sym->lim]^n
					IInteger n = (IInteger) arg1.get(2);
					IExpr temp = F.eval(F.Limit(arg1.arg1(), rule));
					if (temp.isInfinity()) {
						if (n.isPositive()) {
							return temp;
						} else if (n.isNegative()) {
							return F.C0;
						}
						return null;
					} else if (temp.isNegativeInfinity()) {
						if (n.isPositive()) {
							if (n.isEven()) {
								return F.CInfinity;
							} else {
								return F.CNInfinity;
							}
						} else if (n.isNegative()) {
							return F.C0;
						}
						return null;
					} else if (temp.equals(F.Indeterminate) || temp.isAST(F.Limit)) {
						return null;
					}
					if (n.isPositive()) {
						return F.Power(temp, n);
					} else if (n.isNegative() && n.isEven()) {
						return F.Power(temp, n);
					}

				}
			}

		}

		return null;
	}

	/**
	 * See: <a href="http://en.wikibooks.org/wiki/Calculus/Infinite_Limits">Limits at Infinity of Rational Functions</a>
	 * 
	 * @param numeratorPoly
	 * @param denominatorPoly
	 * @param symbol
	 *            the variable for which to approach to the limit
	 * @param limit
	 *            the limit value which the variable should approach to
	 * @param rule
	 * @return
	 */
	private static IExpr limitsInfinityOfRationalFunctions(GenPolynomial<IExpr> numeratorPoly,
			GenPolynomial<IExpr> denominatorPoly, ISymbol symbol, IExpr limit, IAST rule) {
		long numDegree = numeratorPoly.degree();
		long denomDegree = denominatorPoly.degree();
		if (numDegree > denomDegree) {
			// If the numerator has the highest term, then the fraction is called "top-heavy". If, when you divide the numerator
			// by the denominator the resulting exponent on the variable is even, then the limit (at both \infty and -\infty) is
			// \infty. If it is odd, then the limit at \infty is \infty, and the limit at -\infty is -\infty.
			long oddDegree = (numDegree + denomDegree) % 2;
			if (oddDegree == 1) {
				return F.Limit(
						F.Times(F.Divide(numeratorPoly.leadingBaseCoefficient(), denominatorPoly.leadingBaseCoefficient()), limit),
						rule);
			} else {
				return F.Limit(F.Times(F.Divide(numeratorPoly.leadingBaseCoefficient(), denominatorPoly.leadingBaseCoefficient()),
						F.CInfinity), rule);
			}
		} else if (numDegree < denomDegree) {
			// If the denominator has the highest term, then the fraction is called "bottom-heavy" and the limit (at both \infty
			// and -\infty) is zero.
			return F.C0;
		}
		// If the exponent of the highest term in the numerator matches the exponent of the highest term in the denominator,
		// the limit (at both \infty and -\infty) is the ratio of the coefficients of the highest terms.
		return F.Divide(numeratorPoly.leadingBaseCoefficient(), denominatorPoly.leadingBaseCoefficient());
	}

	private static IExpr mapLimit(final IAST expr, IAST rule) {
		final IAST resultList = expr.clone();
		for (int i = 1; i < resultList.size(); i++) {
			resultList.set(i, F.Limit(resultList.get(i), rule));
		}
		return resultList;
	}

	/**
	 * 
	 * @param numerator
	 * @param denominator
	 * @param symbol
	 *            the variable for which to approach to the limit
	 * @param limit
	 *            the limit value which the variable should approach to
	 * @param rule
	 * @param direction
	 *            <ul>
	 *            <li>0 - no direction defined</li>
	 *            <li>1 - approach from smaller values</li>
	 *            <li>-1 - approach from larger values</li>
	 * @return <code>null</code> if no limit found
	 */
	private static IExpr timesLimit(final IExpr numerator, final IExpr denominator, ISymbol symbol, IExpr limit, IAST rule,
			int direction) {
		IExpr numValue;
		IExpr denValue;
		if (denominator.isOne() && numerator.isTimes()) {
			// Limit[a_*b_*c_,sym->lim] ->
			// Limit[a,sym->lim]*Limit[b,sym->lim]*Limit[c,sym->lim]
			return mapLimit((IAST) numerator, rule);
		}
		if (!denominator.isNumber() || denominator.isZero()) {
			denValue = F.evalBlock(denominator, symbol, limit);
			if (denValue.equals(F.Indeterminate)) {
				return null;
			} else if (denValue.isZero()) {
				numValue = F.evalBlock(numerator, symbol, limit);
				if (numValue.isZero()) {
					return lHospitalesRule(numerator, denominator, symbol, limit, rule, direction);
				}
				return null;
			} else if (F.CInfinity.equals(denValue)) {
				numValue = F.evalBlock(numerator, symbol, limit);
				if (F.CInfinity.equals(numValue)) {
					return lHospitalesRule(numerator, denominator, symbol, limit, rule, direction);
				}
				return null;
			} else if (denValue.isNegativeInfinity()) {
				numValue = F.evalBlock(numerator, symbol, limit);
				if (numValue.isNegativeInfinity()) {
					return lHospitalesRule(numerator, denominator, symbol, limit, rule, direction);
				}
				return null;
			}
		}
		return F.Times(F.Limit(numerator, rule), F.Power(F.Limit(denominator, rule), F.CN1));
	}

	public Limit() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);

		if (!ast.arg2().isRuleAST()) {
			throw new WrongArgumentType(ast, ast.get(2), 2, "Limit: rule definition expected!");
		}
		IAST rule = (IAST) ast.arg2();
		if (!(rule.arg1().isSymbol())) {
			throw new WrongArgumentType(ast, ast.get(2), 2, "Limit: variable symbol for rule definition expected!");
		}
		int direction = 0;
		if (ast.size() == 4) {
			final Options options = new Options(ast.topHead(), ast, 2);
			IExpr option = options.getOption("Direction");
			if (option != null) {
				if (option.equals(F.C1)) {
					direction = 1;
				} else if (option.equals(F.C1)) {
					direction = -1;
				} else {
					throw new WrongArgumentType(ast, ast.get(2), 2, "Limit: direction option expected!");
				}
			} else {
				throw new WrongArgumentType(ast, ast.get(2), 2, "Limit: direction option expected!");
			}
		}
		ISymbol symbol = (ISymbol) rule.arg1();
		IExpr limit = null;
		if (rule.arg2().isFree(symbol, true)) {
			limit = rule.get(2);
		} else {
			throw new WrongArgumentType(ast, ast.get(2), 2, "Limit: limit value contains variable symbol for rule definition!");
		}
		return evalLimit(ast.arg1(), symbol, limit, rule, direction, true);
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

}