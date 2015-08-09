package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
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

	static class LimitData implements Cloneable {
		final ISymbol symbol;

		final IExpr limitValue;

		final IAST rule;

		int direction;

		public LimitData(ISymbol symbol, IExpr limitValue, IAST rule) {
			this(symbol, limitValue, rule, DIRECTION_AUTOMATIC);
		}

		public LimitData(ISymbol symbol, IExpr limitValue, IAST rule, int direction) {
			this.symbol = symbol;
			this.limitValue = limitValue;
			this.rule = rule;
			this.direction = direction;
		}

		@Override
		protected LimitData clone() throws CloneNotSupportedException {
			return (LimitData) super.clone();
		}

		public int getDirection() {
			return direction;
		}

		public IExpr getLimitValue() {
			return limitValue;
		}

		public IAST getRule() {
			return rule;
		}

		public ISymbol getSymbol() {
			return symbol;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}
	}

	private static IExpr evalLimitQuiet(final IExpr expr, LimitData data) {
		EvalEngine engine = EvalEngine.get();
		boolean quiet = engine.isQuietMode();
		try {
			engine.setQuietMode(true);
			return evalLimit(expr, data, true);
		} finally {
			engine.setQuietMode(quiet);
		}

	}

	/**
	 * 
	 * @param expr
	 * @param data
	 *            the limits data definition
	 * @param evalExpr
	 * @return
	 */
	private static IExpr evalLimit(final IExpr expr, LimitData data, boolean evalExpr) {
		IExpr expression = expr;
		if (evalExpr) {
			IExpr result = F.evalQuiet(expression);
			if (result.isNumericFunction()) {
				return result;
			}
			if (!result.equals(F.Indeterminate)) {
				expression = result;
			}
			if (result.isFree(data.getSymbol(), true)) {
				// Limit[a_,sym->lim] -> a
				return expression;
			}
			if (result.equals(data.getSymbol())) {
				// Limit[x_,x_->lim] -> lim
				return data.getLimitValue();
			}

			if (data.getLimitValue().isNumericFunction()) {
				result = expression.replaceAll(data.getRule());
				if (result != null) {
					result = F.evalQuiet(result);
					if (result.isNumericFunction()) {
						if (result.isZero()) {
							IExpr temp = F.evalQuiet(F.N(F.Greater(F.Subtract(expression, data.getLimitValue()), F.C0)));
							if (temp != null) {
								// System.out.println(temp.toString());
								IAssumptions assumptions = Assumptions.getInstance(temp);
								if (assumptions != null) {
									int direction = data.getDirection();
									if (assumptions.isNegative(data.getSymbol())) {
										if (direction == DIRECTION_AUTOMATIC || direction == DIRECTION_FROM_SMALLER_VALUES) {
											data.setDirection(DIRECTION_FROM_SMALLER_VALUES);
										} else {
											return null;
										}
									} else if (assumptions.isNonNegative(data.getSymbol())) {
										if (direction == DIRECTION_AUTOMATIC || direction == DIRECTION_FROM_LARGER_VALUES) {
											data.setDirection(DIRECTION_FROM_LARGER_VALUES);
										} else {
											return null;
										}
									}
								}
							}
						}
						return result;
					}
				}
			}
		}

		if (expression.isAST()) {
			final IAST arg1 = (IAST) expression;
			if (arg1.isSin() || arg1.isCos()) {
				return F.$(arg1.head(), F.Limit(arg1.arg1(), data.getRule()));
			} else if (arg1.isPlus()) {
				return plusLimit(arg1, data);
			} else if (arg1.isTimes()) {
				return timesLimit(arg1, data);
			} else if (arg1.isPower()) {
				return powerLimit(arg1, data);
			}

		}

		return null;
	}

	/**
	 * Try L'hospitales rule. See <a href="http://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule">Wikipedia L'Hôpital's rule</a>
	 * 
	 * @param numerator
	 * @param denominator
	 * @param data
	 *            the limits data definition
	 * @return
	 */
	private static IExpr lHospitalesRule(IExpr numerator, IExpr denominator, LimitData data) {
		EvalEngine engine = EvalEngine.get();
		ISymbol x = data.getSymbol();
		int recursionLimit = engine.getRecursionLimit();
		if (recursionLimit > 0) {
			IExpr expr = F.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
			return evalLimit(expr, data, false);
		}
		try {
			if (recursionLimit <= 0) {
				// set recursion limit for using l'Hospitales rule
				engine.setRecursionLimit(128);
			}
			IExpr expr = F.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
			return evalLimit(expr, data, false);
		} catch (RecursionLimitExceeded rle) {
			engine.setRecursionLimit(recursionLimit);
		} finally {
			engine.setRecursionLimit(recursionLimit);
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
	 * @param data
	 *            the limit data definition
	 * @return <code>null</code> if no limit found
	 */
	private static IExpr numeratorDenominatorLimit(final IExpr numerator, final IExpr denominator, LimitData data) {
		IExpr numValue;
		IExpr denValue;
		IExpr limit = data.getLimitValue();
		IAST rule = data.getRule();
		if (denominator.isOne() && numerator.isTimes()) {
			// Limit[a_*b_*c_,sym->lim] ->
			// Limit[a,sym->lim]*Limit[b,sym->lim]*Limit[c,sym->lim]
			return mapLimit((IAST) numerator, rule);
		}
		if (!denominator.isNumber() || denominator.isZero()) {
			ISymbol x = data.getSymbol();
			denValue = F.evalBlock(denominator, x, limit);
			if (denValue.equals(F.Indeterminate)) {
				return null;
			} else if (denValue.isZero()) {
				numValue = F.evalBlock(numerator, x, limit);
				if (numValue.isZero()) {
					return lHospitalesRule(numerator, denominator, data);
				}
				return null;
			} else if (F.CInfinity.equals(denValue)) {
				numValue = F.evalBlock(numerator, x, limit);
				if (F.CInfinity.equals(numValue)) {
					return lHospitalesRule(numerator, denominator, data);
				}
				return null;
			} else if (denValue.isNegativeInfinity()) {
				numValue = F.evalBlock(numerator, x, limit);
				if (numValue.isNegativeInfinity()) {
					return lHospitalesRule(numerator, denominator, data);
				}
				return null;
			}
		}

		return F.Times(F.Limit(numerator, rule), F.Power(F.Limit(denominator, rule), F.CN1));
	}

	private static IExpr plusLimit(final IAST arg1, LimitData data) {
		// Limit[a_+b_+c_,sym->lim] ->
		// Limit[a,sym->lim]+Limit[b,sym->lim]+Limit[c,sym->lim]
		IAST rule = data.getRule();
		IExpr limit = data.getLimitValue();
		if (limit.isInfinity() || limit.isNegativeInfinity()) {
			ISymbol symbol = data.getSymbol();
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

	private static IExpr powerLimit(final IAST arg1, LimitData data) {
		// IAST rule = data.getRule();
		if (arg1.arg2().isNumericFunction()) {
			// Limit[a_^exp_,sym->lim] -> Limit[a,sym->lim]^exp
			IExpr exp = arg1.arg2();
			// IExpr temp = F.evalQuiet(F.Limit(arg1.arg1(), rule));?
			IExpr temp = evalLimitQuiet(arg1.arg1(), data);
			if (temp.isNumericFunction()) {
				if (temp.isZero()) {
					if (exp.isPositive()) {
						// 0 ^ (positve exponent)
						return F.C0;
					}
					if (exp.isNegative()) {
						// 0 ^ (negative exponent)
						if (exp.isInteger()) {
							IInteger n = (IInteger) exp;
							if (n.isEven()) {
								return F.CInfinity;
							}
							if (data.getDirection() == DIRECTION_FROM_SMALLER_VALUES) {
								return F.CNInfinity;
							} else {
								data.setDirection(DIRECTION_FROM_LARGER_VALUES);
								return F.CInfinity;
							}
						} else if (exp.isFraction()) {
							if (data.getDirection() != DIRECTION_FROM_SMALLER_VALUES) {
								data.setDirection(DIRECTION_FROM_LARGER_VALUES);
								return F.CInfinity;
							}
						}

					}

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
		return null;
	}

	/**
	 * Try a substitution. <code>y = 1/x</code>. As <code>|x|</code> approaches <code>Infinity</code> or <code>-Infinity</code>,
	 * <code>y</code> approaches <code>0</code>.
	 * 
	 * @param arg1
	 * @param data
	 *            (the datas limit must be Infinity or -Infinity)
	 * @return <code>null</code> if the substitution didn't succeed.
	 */
	private static IExpr substituteInfinity(final IAST arg1, LimitData data) {
		ISymbol x = data.getSymbol();
		IExpr y = F.Power(x, F.CN1); // substituting by 1/x
		IExpr temp = F.evalQuiet(F.subst(arg1, x, y));
		if (temp.isTimes()) {
			IExpr[] parts = org.matheclipse.core.reflection.system.Apart.getFractionalPartsTimes((IAST) temp, false, false, true);
			if (parts != null) {
				if (!parts[1].isOne()) { // denominator != 1
					LimitData ndData = new LimitData(x, F.C0, F.Rule(x, F.C0), data.getDirection());
					temp = numeratorDenominatorLimit(parts[0], parts[1], ndData);
					if (temp != null) {
						return temp;
					}
				}
			}
		}
		return null;
	}

	private static IExpr timesLimit(final IAST arg1, LimitData data) {
		IExpr[] parts = org.matheclipse.core.reflection.system.Apart.getFractionalPartsTimes(arg1, false, false, true);
		if (parts != null) {

			IExpr numerator = parts[0];
			IExpr denominator = parts[1];
			IExpr limit = data.getLimitValue();
			ISymbol symbol = data.getSymbol();
			if (limit.isInfinity() || limit.isNegativeInfinity()) {
				GenPolynomial<IExpr> denominatorPoly = org.matheclipse.core.reflection.system.PolynomialQ.polynomial(denominator,
						symbol, true);
				if (denominatorPoly != null) {
					GenPolynomial<IExpr> numeratorPoly = org.matheclipse.core.reflection.system.PolynomialQ.polynomial(numerator,
							symbol, true);
					if (numeratorPoly != null) {
						return limitsInfinityOfRationalFunctions(numeratorPoly, denominatorPoly, symbol, limit, data.getRule());
					}
				}
			}

			IExpr plusResult = org.matheclipse.core.reflection.system.Apart.partialFractionDecompositionRational(
					new PartialFractionGenerator(), parts, symbol);
			if (plusResult != null && plusResult.isPlus()) {
				// OneIdentity if plusResult.size() == 2
				// if (plusResult.size() > 2) {
				return mapLimit((IAST) plusResult, data.getRule());
				// }
			}

			if (denominator.isOne()) {
				if (limit.isInfinity() || limit.isNegativeInfinity()) {
					IExpr temp = substituteInfinity(arg1, data);
					if (temp != null) {
						return temp;
					}
				}
			}
			IExpr temp = numeratorDenominatorLimit(numerator, denominator, data);
			if (temp != null) {
				return temp;
			}

		}
		return mapLimit(arg1, data.getRule());
	}

	/**
	 * Compute the limit approaching from larger values.
	 */
	public final static int DIRECTION_FROM_LARGER_VALUES = -1;

	/**
	 * Compute the limit approaching from larger or smaller values automatically.
	 */
	public final static int DIRECTION_AUTOMATIC = 0;

	/**
	 * Compute the limit approaching from smaller values.
	 */
	public final static int DIRECTION_FROM_SMALLER_VALUES = 1;

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
		int direction = DIRECTION_AUTOMATIC; // no direction as default
		if (ast.size() == 4) {
			final Options options = new Options(ast.topHead(), ast, 2);
			IExpr option = options.getOption("Direction");
			if (option != null) {
				if (option.isOne()) {
					direction = DIRECTION_FROM_SMALLER_VALUES;
				} else if (option.isMinusOne()) {
					direction = DIRECTION_FROM_LARGER_VALUES;
				} else if (option.equals(F.Automatic)) {
					direction = DIRECTION_AUTOMATIC;
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
		LimitData data = new LimitData(symbol, limit, rule, direction);
		return evalLimit(ast.arg1(), data, true);
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

}