package org.matheclipse.core.reflection.system;

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
import org.matheclipse.core.reflection.system.rules.LimitRules;

import edu.jas.poly.GenPolynomial;

/**
 * Limit of a function. See <a href="http://en.wikipedia.org/wiki/List_of_limits">List of Limits</a>
 */
public class Limit extends AbstractFunctionEvaluator implements LimitRules {

	/**
	 * Try L'hospitales rule. See <a href="http://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule">Wikipedia L'Hôpital's rule</a>
	 * 
	 * @param numerator
	 * @param denominator
	 * @param x
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
	private static IExpr lHospitalesRule(IExpr numerator, IExpr denominator, ISymbol x, IExpr limit, IAST rule, int direction) {
		EvalEngine engine = EvalEngine.get();
		int recursionLimit = engine.getRecursionLimit();
		if (recursionLimit > 0) {
			IExpr expr = F.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
			return evalLimit(expr, x, limit, rule, direction, false);
		}
		try {
			if (recursionLimit <= 0) {
				// set recursion limit for using l'Hospitales rule
				engine.setRecursionLimit(128);
			}
			IExpr expr = F.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
			return evalLimit(expr, x, limit, rule, direction, false);
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
			IExpr result = F.evalQuiet(expression);
			if (result.isNumericFunction()) {
				return result;
			}
			if (!result.equals(F.Indeterminate)) {
				expression = result;
			}

			if (limit.isNumericFunction()) {
				result = expression.replaceAll(rule);
				if (result != null) {
					result = F.evalQuiet(result);
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
			if (arg1.isSin() || arg1.isCos()) {
				return F.$(arg1.head(), F.Limit(arg1.arg1(), rule));
			} else if (arg1.isPlus()) {
				return plusLimit(arg1, symbol, limit, rule);
			} else if (arg1.isTimes()) {
				return timesLimit(arg1, symbol, limit, direction, rule);
			} else if (arg1.isPower()) {
				if (arg1.arg2().isNumericFunction()) {
					// Limit[a_^exp_,sym->lim] -> Limit[a,sym->lim]^exp
					IExpr exp = arg1.arg2();
					IExpr temp = F.evalQuiet(F.Limit(arg1.arg1(), rule));
					if (temp.isNumericFunction()) {
						if (temp.isZero() && exp.isNegative()) {
							return null;
						}
						return F.Power(temp, exp);
					}
					if (exp.isInteger()) {
						IInteger n = (IInteger) exp;
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

		}

		return null;
	}

	public static IExpr plusLimit(final IAST arg1, ISymbol symbol, IExpr limit, IAST rule) {
		// Limit[a_+b_+c_,sym->lim] ->
		// Limit[a,sym->lim]+Limit[b,sym->lim]+Limit[c,sym->lim]
		if (limit.isInfinity() || limit.isNegativeInfinity()) {
			GenPolynomial<IExpr> poly = org.matheclipse.core.reflection.system.PolynomialQ.polynomial(arg1, symbol, true);
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
	}

	public static IExpr timesLimit(final IAST arg1, ISymbol symbol, IExpr limit, int direction, IAST rule) {
		IExpr[] parts = org.matheclipse.core.reflection.system.Apart.getFractionalPartsTimes(arg1, false);
		if (parts != null) {

			IExpr numerator = parts[0];
			IExpr denominator = parts[1];
			if (limit.isInfinity() || limit.isNegativeInfinity()) {
				GenPolynomial<IExpr> denominatorPoly = org.matheclipse.core.reflection.system.PolynomialQ.polynomial(denominator,
						symbol, true);
				if (denominatorPoly != null) {
					GenPolynomial<IExpr> numeratorPoly = org.matheclipse.core.reflection.system.PolynomialQ.polynomial(numerator,
							symbol, true);
					if (numeratorPoly != null) {
						return limitsInfinityOfRationalFunctions(numeratorPoly, denominatorPoly, symbol, limit, rule);
					}
				}
			}

			IExpr plusResult = org.matheclipse.core.reflection.system.Apart.partialFractionDecompositionRational(
					new PartialFractionGenerator(), parts, symbol);
			if (plusResult != null && plusResult.isPlus()) {
				// OneIdentity if plusResult.size() == 2
				// if (plusResult.size() > 2) {
				return mapLimit((IAST) plusResult, rule);
				// }
			}

			if (denominator.isOne()) {
				if (limit.isInfinity() || limit.isNegativeInfinity()) {
					IExpr temp = substituteInfinity(arg1, symbol, limit, direction);
					if (temp != null) {
						return temp;
					}
				}
			}
			IExpr temp = numeratorDenominatorLimit(numerator, denominator, symbol, limit, rule, direction);
			if (temp != null) {
				return temp;
			}

		}
		return mapLimit(arg1, rule);
	}

	/**
	 * Try a substitution. <code>y = 1/x</code>. As <code>|x|</code> approaches <code>Infinity</code> or <code>-Infinity</code>,
	 * <code>y</code> approaches <code>0</code>.
	 * 
	 * @param arg1
	 * @param x
	 * @param limit
	 *            must be Infinity or -Infinity
	 * @param direction
	 * @return <code>null</code> if the substitution didn't succeed.
	 */
	private static IExpr substituteInfinity(final IAST arg1, ISymbol x, IExpr limit, int direction) {
		IExpr y = F.Power(x, F.CN1); // substituting by 1/x
		IExpr temp = F.evalQuiet(F.subst(arg1, x, y));
		if (temp.isTimes()) {
			IExpr[] parts = org.matheclipse.core.reflection.system.Apart.getFractionalPartsTimes((IAST) temp, false);
			if (parts != null) {
				if (!parts[1].isOne()) { // denominator != 1
					temp = numeratorDenominatorLimit(parts[0], parts[1], x, F.C0, F.Rule(x, F.C0), direction);
					if (temp != null) {
						return temp;
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
		return expr.mapAt(F.Limit(null, rule), 1);
	}

	/**
	 * Try l'Hospitales rule for numerator and denominator expression.
	 * 
	 * @param numerator
	 * @param denominator
	 * @param x
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
	private static IExpr numeratorDenominatorLimit(final IExpr numerator, final IExpr denominator, ISymbol x, IExpr limit,
			IAST rule, int direction) {
		IExpr numValue;
		IExpr denValue;
		if (denominator.isOne() && numerator.isTimes()) {
			// Limit[a_*b_*c_,sym->lim] ->
			// Limit[a,sym->lim]*Limit[b,sym->lim]*Limit[c,sym->lim]
			return mapLimit((IAST) numerator, rule);
		}
		if (!denominator.isNumber() || denominator.isZero()) {
			denValue = F.evalBlock(denominator, x, limit);
			if (denValue.equals(F.Indeterminate)) {
				return null;
			} else if (denValue.isZero()) {
				numValue = F.evalBlock(numerator, x, limit);
				if (numValue.isZero()) {
					return lHospitalesRule(numerator, denominator, x, limit, rule, direction);
				}
				return null;
			} else if (F.CInfinity.equals(denValue)) {
				numValue = F.evalBlock(numerator, x, limit);
				if (F.CInfinity.equals(numValue)) {
					return lHospitalesRule(numerator, denominator, x, limit, rule, direction);
				}
				return null;
			} else if (denValue.isNegativeInfinity()) {
				numValue = F.evalBlock(numerator, x, limit);
				if (numValue.isNegativeInfinity()) {
					return lHospitalesRule(numerator, denominator, x, limit, rule, direction);
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
			throw new WrongArgumentType(ast, ast.arg2(), 2, "Limit: rule definition expected!");
		}
		IAST rule = (IAST) ast.arg2();
		if (!(rule.arg1().isSymbol())) {
			throw new WrongArgumentType(ast, ast.arg1(), 2, "Limit: variable symbol for rule definition expected!");
		}
		int direction = 0;
		if (ast.size() == 4) {
			final Options options = new Options(ast.topHead(), ast, 2);
			IExpr option = options.getOption("Direction");
			if (option != null) {
				if (option.isOne()) {
					direction = 1;
				} else if (option.isMinusOne()) {
					direction = -1;
				} else {
					throw new WrongArgumentType(ast, ast.arg2(), 2, "Limit: direction option expected!");
				}
			} else {
				throw new WrongArgumentType(ast, ast.arg2(), 2, "Limit: direction option expected!");
			}
		}
		ISymbol symbol = (ISymbol) rule.arg1();
		IExpr limit = null;
		if (rule.isFreeAt(2, symbol)) {
			limit = rule.arg2();
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