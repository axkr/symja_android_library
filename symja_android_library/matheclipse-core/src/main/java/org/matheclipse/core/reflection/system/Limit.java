package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
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
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.PartialFractionGenerator;
import org.matheclipse.core.reflection.system.rules.LimitRules;

import com.duy.lambda.Predicate;

/**
 * <pre>
 * Limit(expr, x -&gt; x0)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the limit of <code>expr</code> as <code>x</code> approaches <code>x0</code>
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Limit(7+Sin(x)/x, x-&gt;Infinity)
 * 7
 * </pre>
 */
public class Limit extends AbstractFunctionEvaluator implements LimitRules {

	static class LimitData implements Cloneable {
		final ISymbol symbol;

		final IExpr limitValue;

		final IAST rule;

		int direction;

		public LimitData(ISymbol symbol, IExpr limitValue, IAST rule) {
			this(symbol, limitValue, rule, DIRECTION_TWO_SIDED);
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

		/**
		 * Create a new <code>F.Limit( arg1, ... )</code> expression from his <code>LimitData</code> object
		 * 
		 * @param arg1
		 *            the first argument of the Limit expression
		 * @return a new <code>F.Limit( arg1, ... )</code> expression
		 */
		public IAST limit(IExpr arg1) {
			// if (direction == DIRECTION_FROM_LARGER_VALUES) {
			// return F.Limit(arg1, rule, F.Rule(F.Direction, F.CN1));
			// }

			if (direction == DIRECTION_FROM_BELOW) {
				return F.Limit(arg1, rule, F.Rule(F.Direction, F.C1));
			}
			return F.Limit(arg1, rule);
		}

		/**
		 * Map a <code>F.Limit( arg1, ... )</code> expression at each argument of the given <code>ast</code>.
		 * 
		 * @param ast
		 * @return
		 */
		public IExpr mapLimit(final IAST ast) {
			return ast.mapThread(limit(null), 1);
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
				if (result.isPresent()) {
					result = F.evalQuiet(result);
					if (result.isNumericFunction()) {
						if (result.isZero()) {
							IExpr temp = F
									.evalQuiet(F.N(F.Greater(F.Subtract(expression, data.getLimitValue()), F.C0)));
							if (temp != null) {
								IAssumptions assumptions = Assumptions.getInstance(temp);
								if (assumptions != null) {
									int direction = data.getDirection();
									if (assumptions.isNegative(data.getSymbol())) {
										if (direction == DIRECTION_TWO_SIDED || direction == DIRECTION_FROM_BELOW) {
											data.setDirection(DIRECTION_FROM_BELOW);
										} else {
											return F.NIL;
										}
									} else if (assumptions.isNonNegative(data.getSymbol())) {
										if (direction == DIRECTION_TWO_SIDED || direction == DIRECTION_FROM_ABOVE) {
											data.setDirection(DIRECTION_FROM_ABOVE);
										} else {
											return F.NIL;
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
				return F.unaryAST1(arg1.head(), data.limit(arg1.arg1()));
			} else if (arg1.isPlus()) {
				return plusLimit(arg1, data);
			} else if (arg1.isTimes()) {
				return timesLimit(arg1, data);
			} else if (arg1.isLog()) {
				return logLimit(arg1, data);
			} else if (arg1.isPower()) {
				return powerLimit(arg1, data);
			}

		}

		return F.NIL;
	}

	/**
	 * Try L'hospitales rule. See <a href="http://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule"> Wikipedia
	 * L'Hôpital's rule</a>
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
		try {
			int recursionCounter = engine.incRecursionCounter();
			int recursionLimit = engine.getRecursionLimit();
			if (recursionLimit > 0) {
				if (recursionCounter > recursionLimit) {
					return F.NIL;
				}
				IExpr expr = F.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
				// System.out.println(expr.toString());
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
		} finally {
			engine.decRecursionCounter();
		}
		return F.NIL;
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
	// private static IExpr limitsInfinityOfRationalFunctions(GenPolynomial<IExpr>
	// numeratorPoly,
	// GenPolynomial<IExpr> denominatorPoly, ISymbol symbol, IExpr limit, IAST rule)
	// {
	// long numDegree = numeratorPoly.degree();
	// long denomDegree = denominatorPoly.degree();
	// if (numDegree > denomDegree) {
	// // If the numerator has the highest term, then the fraction is
	// // called "top-heavy". If, when you divide the numerator
	// // by the denominator the resulting exponent on the variable is
	// // even, then the limit (at both \infty and -\infty) is
	// // \infty. If it is odd, then the limit at \infty is \infty, and the
	// // limit at -\infty is -\infty.
	// long oddDegree = (numDegree + denomDegree) % 2;
	// if (oddDegree == 1) {
	// return F.Limit(F.Times(
	// F.Divide(numeratorPoly.leadingBaseCoefficient(),
	// denominatorPoly.leadingBaseCoefficient()),
	// limit), rule);
	// } else {
	// return F.Limit(F.Times(
	// F.Divide(numeratorPoly.leadingBaseCoefficient(),
	// denominatorPoly.leadingBaseCoefficient()),
	// F.CInfinity), rule);
	// }
	// } else if (numDegree < denomDegree) {
	// // If the denominator has the highest term, then the fraction is
	// // called "bottom-heavy" and the limit (at both \infty
	// // and -\infty) is zero.
	// return F.C0;
	// }
	// // If the exponent of the highest term in the numerator matches the
	// // exponent of the highest term in the denominator,
	// // the limit (at both \infty and -\infty) is the ratio of the
	// // coefficients of the highest terms.
	// return F.Divide(numeratorPoly.leadingBaseCoefficient(),
	// denominatorPoly.leadingBaseCoefficient());
	// }

	/**
	 * See: <a href="http://en.wikibooks.org/wiki/Calculus/Infinite_Limits">Limits at Infinity of Rational Functions</a>
	 * 
	 * @param numeratorPoly
	 * @param denominatorPoly
	 * @param symbol
	 *            the variable for which to approach to the limit
	 * @param data
	 *            the limit expression which the variable should approach to
	 * @param rule
	 * @return
	 */
	private static IExpr limitsInfinityOfRationalFunctions(ExprPolynomial numeratorPoly, ExprPolynomial denominatorPoly,
			ISymbol symbol, IExpr limit, LimitData data) {
		long numDegree = numeratorPoly.degree();
		long denomDegree = denominatorPoly.degree();
		if (numDegree > denomDegree) {
			// If the numerator has the highest term, then the fraction is
			// called "top-heavy". If, when you divide the numerator
			// by the denominator the resulting exponent on the variable is
			// even, then the limit (at both \infty and -\infty) is
			// \infty. If it is odd, then the limit at \infty is \infty, and the
			// limit at -\infty is -\infty.
			long oddDegree = (numDegree + denomDegree) % 2;
			if (oddDegree == 1) {
				return data.limit(F.Times(
						F.Divide(numeratorPoly.leadingBaseCoefficient(), denominatorPoly.leadingBaseCoefficient()),
						limit));
			} else {
				return data.limit(F.Times(
						F.Divide(numeratorPoly.leadingBaseCoefficient(), denominatorPoly.leadingBaseCoefficient()),
						F.CInfinity));
			}
		} else if (numDegree < denomDegree) {
			// If the denominator has the highest term, then the fraction is
			// called "bottom-heavy" and the limit (at both \infty
			// and -\infty) is zero.
			return F.C0;
		}
		// If the exponent of the highest term in the numerator matches the
		// exponent of the highest term in the denominator,
		// the limit (at both \infty and -\infty) is the ratio of the
		// coefficients of the highest terms.
		return F.Divide(numeratorPoly.leadingBaseCoefficient(), denominatorPoly.leadingBaseCoefficient());
	}

	// private static IExpr mapLimit(final IAST ast, LimitData data) {
	// return ast.mapThread(data.limit(null), 1);
	// }

	/**
	 * Try l'Hospitales rule for numerator and denominator expression.
	 * 
	 * @param numerator
	 * @param denominator
	 * @param data
	 *            the limit data definition
	 * @return <code>F.NIL</code> if no limit found
	 */
	private static IExpr numeratorDenominatorLimit(final IExpr numerator, final IExpr denominator, LimitData data) {
		IExpr numValue;
		IExpr denValue;
		IExpr limit = data.getLimitValue();
		IAST rule = data.getRule();
		EvalEngine engine = EvalEngine.get();
		if (denominator.isOne() && numerator.isTimes()) {
			// Limit[a_*b_*c_,sym->lim] ->
			// Limit[a,sym->lim]*Limit[b,sym->lim]*Limit[c,sym->lim]
			return data.mapLimit((IAST) numerator);
		}
		if (!denominator.isNumber() || denominator.isZero()) {
			ISymbol x = data.getSymbol();
			denValue = engine.evalBlock(denominator, x, limit);
			if (denValue.equals(F.Indeterminate)) {
				return F.NIL;
			} else if (denValue.isZero()) {
				numValue = engine.evalBlock(numerator, x, limit);
				if (numValue.isZero()) {
					return lHospitalesRule(numerator, denominator, data);
				}
				return F.NIL;
			} else if (F.CInfinity.equals(denValue)) {
				numValue = engine.evalBlock(numerator, x, limit);
				if (F.CInfinity.equals(numValue)) {
					return lHospitalesRule(numerator, denominator, data);
				}
				return F.NIL;
			} else if (denValue.isNegativeInfinity()) {
				numValue = engine.evalBlock(numerator, x, limit);
				if (numValue.isNegativeInfinity()) {
					return lHospitalesRule(numerator, denominator, data);
				}
				return F.NIL;
			}
		}

		return F.Times(data.limit(numerator), F.Power(data.limit(denominator), F.CN1));
	}

	private static IExpr plusLimit(final IAST arg1, LimitData data) {
		// Limit[a_+b_+c_,sym->lim] ->
		// Limit[a,sym->lim]+Limit[b,sym->lim]+Limit[c,sym->lim]
		// IAST rule = data.getRule();
		IExpr limit = data.getLimitValue();
		if (limit.isInfinity() || limit.isNegativeInfinity()) {
			ISymbol symbol = data.getSymbol();
			try {
				ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
				ExprPolynomial poly = ring.create(arg1);
				IExpr coeff = poly.leadingBaseCoefficient();
				long oddDegree = poly.degree() % 2;
				if (oddDegree == 1) {
					// odd degree
					return data.limit(F.Times(coeff, limit));
				}
				// even degree
				return data.limit(F.Times(coeff, F.CInfinity));
			} catch (RuntimeException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}

		}
		return data.mapLimit(arg1);
	}

	private static IExpr powerLimit(final IAST powerAST, LimitData data) {
		// IAST rule = data.getRule();
		IExpr base = powerAST.arg1();
		IExpr exponent = powerAST.arg1();
		if (powerAST.arg2().equals(data.getSymbol()) && data.getLimitValue().isZero()) {
			return F.C1;
		}
		if (base.isTimes() && exponent.isFree(data.getSymbol())) {
			IAST isFreeResult = ((IAST) base).partitionTimes(new Predicate<IExpr>() {
                @Override
                public boolean test(IExpr x) {
                    return x.isFree(data.getSymbol(), true);
                }
            }, F.C1, F.C1, F.List);
			if (!isFreeResult.get(2).isOne()) {
				return F.Times(F.Power(isFreeResult.get(1), exponent),
						data.limit(F.Power(isFreeResult.get(2), exponent)));
			}
		}
		if (powerAST.arg2().isNumericFunction()) {
			// Limit[a_^exp_,sym->lim] -> Limit[a,sym->lim]^exp
			IExpr exp = powerAST.arg2();
			// IExpr temp = F.evalQuiet(F.Limit(arg1.arg1(), rule));?
			IExpr temp = evalLimitQuiet(powerAST.arg1(), data);
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
							if (data.getDirection() == DIRECTION_FROM_BELOW) {
								return F.CNInfinity;
							} else {
								data.setDirection(DIRECTION_FROM_ABOVE);
								return F.CInfinity;
							}
						} else if (exp.isFraction()) {
							if (data.getDirection() != DIRECTION_FROM_BELOW) {
								data.setDirection(DIRECTION_FROM_ABOVE);
								return F.CInfinity;
							}
						}

					}

					return F.NIL;
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
					return F.NIL;
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
					return F.NIL;
				} else if (temp.equals(F.Indeterminate) || temp.isAST(F.Limit)) {
					return F.NIL;
				}
				if (n.isPositive()) {
					return F.Power(temp, n);
				} else if (n.isNegative() && n.isEven()) {
					return F.Power(temp, n);
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Try a substitution. <code>y = 1/x</code>. As <code>|x|</code> approaches <code>Infinity</code> or
	 * <code>-Infinity</code>, <code>y</code> approaches <code>0</code>.
	 * 
	 * @param arg1
	 * @param data
	 *            (the datas limit must be Infinity or -Infinity)
	 * @return <code>F.NIL</code> if the substitution didn't succeed.
	 */
	private static IExpr substituteInfinity(final IAST arg1, LimitData data) {
		ISymbol x = data.getSymbol();
		IExpr y = F.Power(x, F.CN1); // substituting by 1/x
		IExpr temp = F.evalQuiet(F.subst(arg1, x, y));
		if (temp.isTimes()) {
			IExpr[] parts = Algebra.fractionalPartsTimesPower((IAST) temp, false, false, true, true);
			if (parts != null) {
				if (!parts[1].isOne()) { // denominator != 1
					LimitData ndData = new LimitData(x, F.C0, F.Rule(x, F.C0), data.getDirection());
					temp = numeratorDenominatorLimit(parts[0], parts[1], ndData);
					if (temp.isPresent()) {
						return temp;
					}
				}
			}
		}
		return F.NIL;
	}

	private static IExpr timesLimit(final IAST timesAST, LimitData data) {
		IAST isFreeResult = timesAST.partitionTimes(new Predicate<IExpr>() {
			@Override
			public boolean test(IExpr x) {
				return x.isFree(data.getSymbol(), true);
			}
		}, F.C1, F.C1, F.List);
		if (!isFreeResult.get(1).isOne()) {
			return F.Times(isFreeResult.get(1), data.limit(isFreeResult.get(2)));
		}
		IExpr[] parts = Algebra.fractionalPartsTimesPower(timesAST, false, false, true, true);
		if (parts != null) {

			IExpr numerator = parts[0];
			IExpr denominator = parts[1];
			IExpr limit = data.getLimitValue();
			ISymbol symbol = data.getSymbol();
			if (limit.isInfinity() || limit.isNegativeInfinity()) {
				try {
					ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
					ExprPolynomial denominatorPoly = ring.create(denominator);
					ExprPolynomial numeratorPoly = ring.create(numerator);
					return limitsInfinityOfRationalFunctions(numeratorPoly, denominatorPoly, symbol, limit, data);
				} catch (RuntimeException e) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
				}
			}

			IExpr plusResult = Algebra.partialFractionDecompositionRational(new PartialFractionGenerator(), parts,
					symbol);
			if (plusResult.isPlus()) {
				// OneIdentity if plusResult.isAST1()
				// if (plusResult.size() > 2) {
				return data.mapLimit((IAST) plusResult);
				// }
			}

			if (denominator.isOne()) {
				if (limit.isInfinity() || limit.isNegativeInfinity()) {
					IExpr temp = substituteInfinity(timesAST, data);
					if (temp.isPresent()) {
						return temp;
					}
				}
			}
			IExpr temp = numeratorDenominatorLimit(numerator, denominator, data);
			if (temp.isPresent()) {
				return temp;
			}

		}
		return data.mapLimit(timesAST);
	}

	private static IExpr logLimit(final IAST logAST, LimitData data) {
		if (logAST.isAST2() && !logAST.isFree(data.getSymbol())) {
			return F.NIL;
		}
		if (logAST.arg1().isPower() && logAST.arg1().getAt(2).isFree(data.getSymbol())) {
			IAST powerAST = (IAST) logAST.arg1();
			IAST arg1 = logAST.setAtClone(1, powerAST.arg1());
			return F.Times(powerAST.arg2(), data.limit(arg1));
		} else if (logAST.arg1().isTimes()) {
			IAST isFreeResult = logAST.arg1().partitionTimes(new Predicate<IExpr>() {
				@Override
				public boolean test(IExpr x) {
					return x.isFree(data.getSymbol(), true);
				}
			}, F.C1, F.C1, F.List);
			if (!isFreeResult.get(1).isOne()) {
				IAST arg1 = logAST.setAtClone(1, isFreeResult.get(1));
				IAST arg2 = logAST.setAtClone(1, isFreeResult.get(2));
				return F.Plus(arg1, data.limit(arg2));
			}
		}
		return F.NIL;
	}

	/**
	 * Compute the limit approaching from larger real values.
	 */
	public final static int DIRECTION_FROM_ABOVE = -1;

	/**
	 * Compute the limit approaching from larger or smaller real values automatically.
	 */
	public final static int DIRECTION_TWO_SIDED = 0;

	/**
	 * Compute the limit approaching from smaller real values.
	 */
	public final static int DIRECTION_FROM_BELOW = 1;

	public Limit() {
	}

	/**
	 * Limit of a function. See <a href="http://en.wikipedia.org/wiki/List_of_limits">List of Limits</a>
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		if (!ast.arg2().isRuleAST()) {
			throw new WrongArgumentType(ast, ast.arg2(), 2, "Limit: rule definition expected!");
		}
		IAST rule = (IAST) ast.arg2();
		if (!(rule.arg1().isSymbol())) {
			throw new WrongArgumentType(ast, ast.arg1(), 2, "Limit: variable symbol for rule definition expected!");
		}
		boolean numericMode = engine.isNumericMode();
		try {
			engine.setNumericMode(false);
			int direction = DIRECTION_TWO_SIDED; // no direction as default
			if (ast.isAST3()) {
				final Options options = new Options(ast.topHead(), ast, 2, engine);
				IExpr option = options.getOption("Direction");
				if (option.isPresent()) {
					if (option.isOne()) {
						direction = DIRECTION_FROM_BELOW;
					} else if (option.isMinusOne()) {
						direction = DIRECTION_FROM_ABOVE;
					} else if (option.equals(F.Automatic)) {
						direction = DIRECTION_TWO_SIDED;
					} else {
						throw new WrongArgumentType(ast, ast.arg2(), 2, "Limit: direction option expected!");
					}
				} else {
					throw new WrongArgumentType(ast, ast.arg2(), 2, "Limit: direction option expected!");
				}
				if (direction == DIRECTION_TWO_SIDED) {
					IExpr temp = F.Limit.evalDownRule(engine, F.Limit(ast.arg1(), ast.arg2()));
					if (temp.isPresent()) {
						return temp;
					}
				}
			}
			ISymbol symbol = (ISymbol) rule.arg1();
			IExpr limit = null;
			if (rule.isFreeAt(2, symbol)) {
				limit = rule.arg2();
			} else {
				throw new WrongArgumentType(ast, ast.arg2(), 2,
						"Limit: limit value contains variable symbol for rule definition!");
			}
			LimitData data = new LimitData(symbol, limit, rule, direction);
			return evalLimit(ast.arg1(), data, true);
		} finally {
			engine.setNumericMode(numericMode);
		}
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NHOLDALL);
		super.setUp(newSymbol);
	}

}