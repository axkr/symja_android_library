package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.reflection.system.rules.LimitRules;
import org.matheclipse.core.reflection.system.rules.SeriesCoefficientRules;

public class SeriesFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.Limit.setEvaluator(new Limit());
			if (ToggleFeature.SERIES) {
				F.ComposeSeries.setEvaluator(new ComposeSeries());
				F.InverseSeries.setEvaluator(new InverseSeries());
				F.Normal.setEvaluator(new Normal());
				F.Series.setEvaluator(new Series());
				F.SeriesCoefficient.setEvaluator(new SeriesCoefficient());
				F.SeriesData.setEvaluator(new SeriesData());
			}
		}
	}

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
	private final static class Limit extends AbstractFunctionEvaluator implements LimitRules {

		/**
		 * Representing the data for the current limit.
		 * 
		 * @author khart
		 *
		 */
		static class LimitData {
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

			/**
			 * Get the optional direction value. Default is DIRECTION_TWO_SIDED = 0.
			 * 
			 * @return
			 */
			public int getDirection() {
				return direction;
			}

			/**
			 * Get the limit value of the limit definition <code>symbol->value</code>
			 * 
			 * @return
			 */
			public IExpr getLimitValue() {
				return limitValue;
			}

			public IAST getRule() {
				return rule;
			}

			/**
			 * Get the <code>symbol</code> of the limit definition <code>symbol->value</code>
			 * 
			 * @return
			 */
			public ISymbol getSymbol() {
				return symbol;
			}

			/**
			 * Set the optional direction value. Default is DIRECTION_TWO_SIDED = 0.
			 * 
			 * @param direction
			 */
			public void setDirection(int direction) {
				this.direction = direction;
			}

			/**
			 * Create a new <code>F.Limit( arg1, ... )</code> expression from this <code>LimitData</code> object
			 * 
			 * @param arg1
			 *            the first argument of the Limit expression
			 * @return a new <code>F.Limit( arg1, ... )</code> expression
			 */
			public IExpr limit(IExpr arg1) {
				return evalLimitQuiet(arg1, this);
				// if (direction == DIRECTION_FROM_ABOVE) {
				// return F.Limit(arg1, rule, F.Rule(F.Direction, F.CN1));
				// }
				// if (direction == DIRECTION_FROM_BELOW) {
				// return F.Limit(arg1, rule, F.Rule(F.Direction, F.C1));
				// }
				// return F.Limit(arg1, rule);
			}

			/**
			 * Map a <code>F.Limit( arg1, ... )</code> expression at each argument of the given <code>ast</code>.
			 * 
			 * @param ast
			 * @return
			 */
			public IAST mapLimit(final IAST ast) {
				// return ast.mapThread(limit(null), 1);
				IASTMutable result = ast.copy();
				boolean isIndeterminate = false;
				boolean isLimit = false;
				for (int i = 1; i < ast.size(); i++) {
					IExpr temp = evalLimitQuiet(ast.get(i), this);
					if (!temp.isFree(F.Limit)) {
						isLimit = true;
					} else if (temp.isIndeterminate()) {
						isIndeterminate = true;
					}
					result.set(i, temp);
				}
				if (isLimit && isIndeterminate) {
					return F.NIL;
				}
				return result;
			}
		}

		private static IExpr evalLimitQuiet(final IExpr expr, LimitData data) {
			EvalEngine engine = EvalEngine.get();
			boolean quiet = engine.isQuietMode();
			try {
				// engine.setQuietMode(true);
				// return evalLimit(expr, data, true);
				IExpr direction = data.getDirection() == DIRECTION_TWO_SIDED ? F.Reals : F.ZZ(data.getDirection());
				return engine.evaluate(F.Limit(expr, data.getRule(), F.Rule(F.Direction, direction)));
			} finally {
				engine.setQuietMode(quiet);
			}

		}

		/**
		 * Evaluate the limit for the given limit data.
		 * 
		 * @param expr
		 * @param data
		 *            the limits data definition
		 * @return
		 */
		private static IExpr evalLimit(final IExpr expr, LimitData data) {
			IExpr expression = expr;
			final IExpr limitValue = data.getLimitValue();

			IExpr result = EvalEngine.get().evalQuiet(expression);
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
				return limitValue;
			}

			if (limitValue.isNumericFunction()) {
				IExpr temp = evalReplaceAll(expression, data);
				if (temp.isPresent()) {
					return temp;
				}
			} else if ((limitValue.isInfinity() || //
					limitValue.isNegativeInfinity()) && //
					expression.isAST() && //
					expression.size() > 1) {
				IExpr temp = limitInfinityZero((IAST) expression, data, (IAST) limitValue);
				if (temp.isPresent()) {
					return temp;
				}
			}

			if (expression.isAST()) {
				if (!limitValue.isNumericFunction() && limitValue.isFree(F.DirectedInfinity)
						&& limitValue.isFree(data.getSymbol())) {
					// example Limit(E^(3*x), x->a) ==> E^(3*a)
					return expr.replaceAll(data.getRule()).orElse(expr);
				}
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

		private static IExpr evalReplaceAll(IExpr expression, LimitData data) {
			IExpr result = expression.replaceAll(data.getRule());
			if (result.isPresent()) {
				result = EvalEngine.get().evalQuiet(result);
				if (result.isNumericFunction() || result.isInfinity() || result.isNegativeInfinity()) {
					return result;
				}
			}
			return F.NIL;
		}

		/**
		 * <p>
		 * Solve for example:<br />
		 * <code>Limit(Gamma(1/t),t->Infinity) ==> Infinity</code> <br />
		 * <code>Limit(Gamma(1/t),t->-Infinity)  ==> -Infinity</code>
		 * </p>
		 * 
		 * @param ast
		 * @param data
		 * @param limitValue
		 *            <code>Infinity</code> or <code>-Infinity</code>
		 * @return
		 */
		private static IExpr limitInfinityZero(IAST ast, LimitData data, final IAST limitValue) {
			int direction = limitValue.isNegativeInfinity() ? DIRECTION_FROM_BELOW : DIRECTION_FROM_ABOVE;
			int dataDirection = data.getDirection();
			if (dataDirection == DIRECTION_TWO_SIDED || dataDirection == direction) {
				int variableArgPosition = -1;
				for (int i = 1; i < ast.size(); i++) {
					if (!ast.get(i).isFree(data.getSymbol())) {
						if (variableArgPosition == -1) {
							variableArgPosition = i;
						} else {
							// more than 1 argument contains the symbol
							return F.NIL;
						}
					}
				}
				if (variableArgPosition > 0) {
					IExpr arg1 = evalLimitQuiet(ast.get(variableArgPosition), data);
					if (arg1.isZero()) {

						LimitData tempData = new LimitData(data.getSymbol(), F.C0, F.Rule(data.getSymbol(), F.C0),
								direction);
						return evalLimitQuiet(ast.setAtCopy(variableArgPosition, data.getSymbol()), tempData);
					}
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
			int recursionLimit = engine.getRecursionLimit();
			try {
				if (recursionLimit <= 0 || recursionLimit > Config.LIMIT_LHOSPITAL_RECURSION_LIMIT) {
					// set recursion limit for using l'Hospitales rule
					engine.setRecursionLimit(Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
				}
				if (data.limitValue.isInfinity() || data.limitValue.isNegativeInfinity()) {
					if (!numerator.isPower() && denominator.isPower() && denominator.exponent().equals(F.C1D2)) {
						// github #115: numerator / Sqrt( denominator.base() )
						IFraction frac = (IFraction) denominator.exponent();
						if (frac.numerator().isOne()) {
							IInteger exp = frac.denominator(); // == 2
							IExpr expr = engine.evalQuiet(F.Times(F.D(F.Power(numerator, exp), x),
									F.Power(F.D(denominator.base(), x), F.CN1)));
							expr = evalLimit(expr, data);
							if (expr.isNumber()) {
								// Sqrt( expr )
								return F.Power(expr, frac);
							}
						}
					}
				}
				IExpr expr = engine.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
				return evalLimit(expr, data);
			} catch (RecursionLimitExceeded rle) {
				engine.setRecursionLimit(recursionLimit);
			} finally {
				engine.setRecursionLimit(recursionLimit);
			}
			return F.NIL;
		}

		/**
		 * See: <a href="http://en.wikibooks.org/wiki/Calculus/Infinite_Limits">Limits at Infinity of Rational
		 * Functions</a>
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
		 * See: <a href="http://en.wikibooks.org/wiki/Calculus/Infinite_Limits">Limits at Infinity of Rational
		 * Functions</a>
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
		private static IExpr limitsInfinityOfRationalFunctions(ExprPolynomial numeratorPoly,
				ExprPolynomial denominatorPoly, ISymbol symbol, IExpr limit, LimitData data) {
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
		private static IExpr numeratorDenominatorLimit(IExpr numerator, IExpr denominator, LimitData data) {
			IExpr numValue;
			IExpr denValue;
			IExpr limitValue = data.getLimitValue();
			// IAST rule = data.getRule();
			EvalEngine engine = EvalEngine.get();
			if (denominator.isOne() && numerator.isTimes()) {
				// Limit[a_*b_*c_,sym->lim] ->
				// Limit[a,sym->lim]*Limit[b,sym->lim]*Limit[c,sym->lim]
				return data.mapLimit((IAST) numerator);
			}
			if (!denominator.isNumber() || denominator.isZero()) {
				IExpr result = F.NIL;
				ISymbol x = data.getSymbol();
				denValue = engine.evalModuleDummySymbol(denominator, x, limitValue, true);
				if (denValue.isIndeterminate()) {
					return F.NIL;
				} else if (denValue.isZero()) {
					numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
					if (numValue.isZero()) {
						return lHospitalesRule(numerator, denominator, data);
					}
					return F.NIL;
				} else if (denValue.isInfinity()) {
					numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
					if (numValue.isInfinity()) {
						return lHospitalesRule(numerator, denominator, data);
					} else if (numValue.isNegativeInfinity()) {
						numerator = engine.evaluate(numerator.negate());
						numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
						if (numValue.isInfinity()) {
							result = lHospitalesRule(numerator, denominator, data);
							if (result.isPresent()) {
								return result.negate();
							}
						}
					}
					return F.NIL;
				} else if (denValue.isNegativeInfinity()) {
					denominator = engine.evaluate(denominator.negate());
					denValue = engine.evalModuleDummySymbol(denominator, x, limitValue, true);
					if (denValue.isInfinity()) {
						numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
						if (numValue.isInfinity()) {
							result = lHospitalesRule(numerator, denominator, data);
							if (result.isPresent()) {
								// negate because denominator.negate()
								return result.negate();
							}
						} else if (numValue.isNegativeInfinity()) {
							numerator = engine.evaluate(numerator.negate());
							numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
							if (numValue.isInfinity()) {
								// tried both cases numerator.negate() and denominator.negate()
								return lHospitalesRule(numerator, denominator, data);
							}
						}
						return F.NIL;
					}
					// numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
					// if (numValue.isInfinity()) {
					// return lHospitalesRule(numerator, denominator, data);
					// } else if (numValue.isNegativeInfinity()) {
					// numValue = engine.evalModuleDummySymbol(numerator.negate(), x, limitValue, true);
					// if (numValue.isInfinity()) {
					// result = lHospitalesRule(numerator, denominator, data);
					// if (result.isPresent()) {
					// return result.negate();
					// }
					// }
					// }
					return F.NIL;
					// } else if (denValue.isZero() || denValue.isDirectedInfinity() || denValue.isComplexInfinity()
					// || denValue.isIndeterminate()) {
					// numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
					// if (numValue.isZero() || numValue.isDirectedInfinity() || numValue.isComplexInfinity()
					// || numValue.isIndeterminate()) {
					// return lHospitalesRule(numerator, denominator, data);
					// }
					// return F.NIL;

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
						return evalLimitQuiet(F.Times(coeff, limit), data);
					}
					// even degree
					return evalLimitQuiet(F.Times(coeff, F.CInfinity), data);
				} catch (RuntimeException e) {
					if (Config.SHOW_STACKTRACE) {
						e.printStackTrace();
					}
				}

			}
			return data.mapLimit(arg1);
		}

		private static IExpr powerLimit(final IAST powerAST, LimitData data) {
			// IAST rule = data.getRule();
			IExpr base = powerAST.arg1();
			IExpr exponent = powerAST.arg2();
			if (exponent.equals(data.getSymbol()) && data.getLimitValue().isZero() && !base.isZero()) {
				return F.C1;
			}
			if (base.isRealResult() && !base.isZero()) {
				IExpr temp = evalReplaceAll(powerAST, data);
				if (temp.isPresent()) {
					return temp;
				}
			}
			if (exponent.isFree(data.getSymbol())) {
				final IExpr temp = evalLimitQuiet(base, data);
				if (temp.isPresent()) {
					if (temp.isZero() && !exponent.isNumericFunction()) {
						// ConditionalExpression(0, exponent > 0)
						return F.ConditionalExpression(F.C0, F.Greater(exponent, F.C0));
					}
					if (!temp.isZero() && temp.isFree(data.getSymbol())) {
						// ConditionalExpression(0, exponent > 0)
						return F.Power(temp, exponent);
					}
				}
				if (base.isTimes()) {
					IAST isFreeResult = ((IAST) base).partitionTimes(x -> x.isFree(data.getSymbol(), true), F.C1, F.C1,
							F.List);
					if (!isFreeResult.arg2().isOne()) {
						return F.Times(F.Power(isFreeResult.arg1(), exponent),
								data.limit(F.Power(isFreeResult.arg2(), exponent)));
					}
				}
			}
			if (exponent.isNumericFunction()) {
				// Limit[a_^exp_,sym->lim] -> Limit[a,sym->lim]^exp
				// IExpr temp = F.evalQuiet(F.Limit(arg1.arg1(), rule));?
				IExpr temp = evalLimitQuiet(base, data);
				if (temp.isNumericFunction()) {
					if (temp.isZero()) {
						if (exponent.isPositive()) {
							// 0 ^ (positve exponent)
							return F.C0;
						}
						if (exponent.isNegative()) {
							// 0 ^ (negative exponent)
							if (exponent.isInteger()) {
								IInteger n = (IInteger) exponent;
								if (n.isEven()) {
									return F.CInfinity;
								}
								if (data.getDirection() == DIRECTION_TWO_SIDED) {
									return F.Indeterminate;
								} else if (data.getDirection() == DIRECTION_FROM_BELOW) {
									return F.CNInfinity;
								} else if (data.getDirection() == DIRECTION_FROM_ABOVE) {
									return F.CInfinity;
								}
							} else if (exponent.isFraction()) {
								if (data.getDirection() == DIRECTION_TWO_SIDED) {
									return F.Indeterminate;
								} else if (data.getDirection() == DIRECTION_FROM_ABOVE) {
									return F.CInfinity;
								}
							}
						}
						return F.NIL;
					}
					return F.Power(temp, exponent);
				}
				if (!temp.isPresent()) {
					temp = base;
				}
				if (exponent.isInteger()) {
					IInteger n = (IInteger) exponent;
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
				IExpr[] parts = Algebra.fractionalPartsTimesPower((IAST) temp, false, false, true, true, true, true);
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
			EvalEngine engine = EvalEngine.get();
			IAST isFreeResult = timesAST.partitionTimes(x -> x.isFree(data.getSymbol(), true), F.C1, F.C1, F.List);
			if (!isFreeResult.arg1().isOne()) {
				return F.Times(isFreeResult.arg1(), data.limit(isFreeResult.arg2()));
			}
			IExpr[] parts = Algebra.fractionalPartsTimesPower(timesAST, false, false, true, true, true, true);
			if (parts == null) {
				IAST[] timesPolyFiltered = timesAST.filter(x -> x.isPolynomial(data.symbol));
				if (timesPolyFiltered[0].size() > 1 && timesPolyFiltered[1].size() > 1) {
					IExpr first = engine.evaluate(data.limit(timesPolyFiltered[0].oneIdentity1()));
					IExpr second = engine.evaluate(data.limit(timesPolyFiltered[1].oneIdentity1()));
					if (first.isReal() || second.isReal()) {
						IExpr temp = engine.evaluate(F.Times(first, second));
						if (!temp.isIndeterminate()) {
							return temp;
						}
						if (data.getLimitValue().isZero()) {
							// Try reciprocal of symbol and approach to +/- Infinity
							IExpr newTimes = timesAST.replaceAll(F.Rule(data.symbol, F.Power(data.symbol, F.CN1)));
							if (newTimes.isPresent()) {
								IAST infinityExpr = (data.direction == DIRECTION_FROM_BELOW) ? F.CNInfinity
										: F.CInfinity;
								LimitData copy = new LimitData(data.symbol, infinityExpr,
										F.Rule(data.symbol, infinityExpr), data.direction);
								temp = engine.evaluate(copy.limit(newTimes));
								if (!temp.isIndeterminate()) {
									return temp;
								}
							}
						}
					}
				}
			} else {

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

				IExpr plusResult = Algebra.partsApart(parts, symbol, engine);
				// Algebra.partialFractionDecompositionRational(new PartialFractionGenerator(), parts,symbol);
				if (plusResult.isPlus()) {
					return data.mapLimit((IAST) plusResult);
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
			IExpr firstArg = logAST.arg1();
			if (firstArg.isPower() && firstArg.exponent().isFree(data.getSymbol())) {
				IAST arg1 = logAST.setAtCopy(1, firstArg.base());
				return F.Times(firstArg.exponent(), data.limit(arg1));
			} else if (firstArg.isTimes()) {
				IAST isFreeResult = firstArg.partitionTimes(x -> x.isFree(data.getSymbol(), true), F.C1, F.C1, F.List);
				if (!isFreeResult.arg1().isOne()) {
					IAST arg1 = logAST.setAtCopy(1, isFreeResult.arg1());
					IAST arg2 = logAST.setAtCopy(1, isFreeResult.arg2());
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

		/**
		 * Limit of a function. See <a href="http://en.wikipedia.org/wiki/List_of_limits">List of Limits</a>
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			if (!arg2.isRuleAST()) {
				throw new WrongArgumentType(ast, arg2, 2, "Limit: rule definition expected!");
			}
			IAST rule = (IAST) arg2;

			if (!(rule.arg1().isSymbol())) {
				throw new WrongArgumentType(ast, arg1, 2, "Limit: variable symbol for rule definition expected!");
			}
			if (arg1.isList()) {
				IASTMutable clone = ast.copy();
				clone.set(1, null);
				return ((IAST) arg1).mapThread(clone, 1);
			}
			boolean numericMode = engine.isNumericMode();
			try {
				engine.setNumericMode(false);
				int direction = DIRECTION_TWO_SIDED; // no direction as default
				if (ast.isAST3()) {
					final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
					IExpr option = options.getOption(F.Direction);
					if (option.isPresent()) {
						if (option.isOne()) {
							direction = DIRECTION_FROM_BELOW;
						} else if (option.isMinusOne()) {
							direction = DIRECTION_FROM_ABOVE;
						} else if (option.equals(F.Automatic) || //
								option.equals(F.Reals)) {
							direction = DIRECTION_TWO_SIDED;
						} else {
							throw new WrongArgumentType(ast, arg2, 2, "Limit: direction option expected!");
						}
					} else {
						throw new WrongArgumentType(ast, arg2, 2, "Limit: direction option expected!");
					}
					if (direction == DIRECTION_TWO_SIDED) {
						IExpr temp = F.Limit.evalDownRule(engine, F.Limit(arg1, arg2));
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
					throw new WrongArgumentType(ast, arg2, 2,
							"Limit: limit value contains variable symbol for rule definition!");
				}
				LimitData data = new LimitData(symbol, limit, rule, direction);
				return evalLimit(arg1, data);
			} finally {
				engine.setNumericMode(numericMode);
			}
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_3;
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

	/**
	 * <pre>
	 * Normal(series)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * converts a <code>series</code> expression into a standard expression.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))
	 * 1/x-x-4*x^2-17*x^3-88*x^4-549*x^5
	 * </pre>
	 */
	private final static class Normal extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1 instanceof ASTSeriesData) {
				return ((ASTSeriesData) arg1).normal();
			}
			if (WXFFunctions.isByteArray(arg1)) {
				byte[] bArray = (byte[]) ((IDataExpr) arg1).toData();
				return WL.toList(bArray);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	/**
	 * <pre>
	 * ComposeSeries(series1, series2)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * substitute <code>series2</code> into <code>series1</code>
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * ComposeSeries(series1, series2, series3)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return multiple series composed.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ComposeSeries(SeriesData(x, 0, {1, 3}, 2, 4, 1), SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1) - 1)
	 * x^2+3*x^3+O(x)^4
	 * </pre>
	 */
	private final static class ComposeSeries extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 2) {
				if (ast.arg1() instanceof ASTSeriesData) {
					ASTSeriesData result = (ASTSeriesData) ast.arg1();
					for (int i = 2; i < ast.size(); i++) {
						if (ast.get(i) instanceof ASTSeriesData) {
							ASTSeriesData s2 = (ASTSeriesData) ast.get(i);
							result = result.compose(s2);
							if (result == null) {
								return F.NIL;
							}
						}
					}
					return result;
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * InverseSeries(series)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return the inverse series.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; InverseSeries(Series(Sin(x), {x, 0, 7}))
	 * x+x^3/6+3/40*x^5+5/112*x^7+O(x)^8
	 * </pre>
	 */
	private final static class InverseSeries extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1() && (ast.arg1() instanceof ASTSeriesData)) {

				ASTSeriesData ps = (ASTSeriesData) ast.arg1();
				ps = ps.inverse();
				if (ps != null) {
					return ps;
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Series(expr, {x, x0, n})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * create a power series of <code>expr</code> up to order <code>(x- x0)^n</code> at the point <code>x = x0</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Series(f(x),{x,a,3})  
	 * f(a)+f'(a)*(-a+x)+1/2*f''(a)*(-a+x)^2+1/6*Derivative(3)[f][a]*(-a+x)^3+O(-a+x)^4
	 * </pre>
	 */
	private final static class Series extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST2() && (ast.arg2().isVector() == 3)) {

				IExpr function = ast.arg1();

				IAST list = (IAST) ast.arg2();
				IExpr x = list.arg1();
				IExpr x0 = list.arg2();
				final int n = list.arg3().toIntDefault(Integer.MIN_VALUE);
				if (n == Integer.MIN_VALUE) {
					return F.NIL;
				}
				if (function.isFree(x)) {
					return function;
				}
				ASTSeriesData series = seriesData(function, x, x0, n, engine);
				if (series != null) {
					return series;
				}
			}
			return F.NIL;
		}

		/**
		 * Create an <code>ASTSeriesData</code> object from the given <code>function</code> expression.
		 * 
		 * @param function
		 *            the function which should be generated as a power series
		 * @param x
		 *            the variable
		 * @param x0
		 *            the point to do the power expansion for
		 * @param n
		 *            the order of the expansion
		 * @param denominator
		 * @return
		 */
		private static ASTSeriesData seriesData(final IExpr function, IExpr x, IExpr x0, final int n,
				EvalEngine engine) {
			if (function.isFree(x) || function.equals(x)) {
				Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
				IASTAppendable rest = F.PlusAlloc(4);
				return polynomialSeries(function, x, x0, n, coefficientMap, rest);
			} else if (function.isPower()) {
				ASTSeriesData temp = powerSeriesData((IAST) function, x, x0, n, engine);
				if (temp != null) {
					return temp;
				}
			} else if (function.isPlus()) {
				ASTSeriesData temp = plusSeriesData((IAST) function, x, x0, n, engine);
				if (temp != null) {
					return temp;
				}
			} else if (function.isTimes()) {
				ASTSeriesData temp = timesSeriesData((IAST) function, x, x0, n, engine);
				if (temp != null) {
					return temp;
				}
			}

			ISymbol power = F.Dummy("$$$n");
			int denominator = 1;
			IExpr temp = engine.evaluate(F.SeriesCoefficient(function, F.List(x, x0, power)));
			if (temp.isFree(F.SeriesCoefficient)) {
				int end = n;
				if (n < 0) {
					end = 0;
				}
				ASTSeriesData ps = new ASTSeriesData(x, x0, end + 1, end + denominator, denominator);
				for (int i = 0; i <= end; i++) {
					ps.setCoeff(i, engine.evaluate(F.subst(temp, F.Rule(power, F.ZZ(i)))));
				}
				return ps;
			} else {
				int end = n;
				if (n < 0) {
					end = 0;
				}
				temp = engine.evaluate(F.SeriesCoefficient(function, F.List(x, x0, F.C0)));
				if (temp.isFree(F.SeriesCoefficient)) {
					boolean evaled = true;
					ASTSeriesData ps = new ASTSeriesData(x, x0, end + 1, end + denominator, denominator);
					ps.setCoeff(0, temp);
					for (int i = 1; i <= end; i++) {
						temp = engine.evaluate(F.SeriesCoefficient(function, F.List(x, x0, F.ZZ(i))));
						if (temp.isFree(F.SeriesCoefficient)) {
							ps.setCoeff(i, temp);
						} else {
							evaled = false;
							break;
						}
					}
					if (evaled) {
						return ps;
					}
				}
			}
			ASTSeriesData ps = new ASTSeriesData(x, x0, 0, n + denominator, denominator);
			IExpr derivedFunction = function;
			for (int i = 0; i <= n; i++) {
				IExpr functionPart = engine.evalQuiet(F.ReplaceAll(derivedFunction, F.Rule(x, x0)));
				if (functionPart.isIndeterminate()) {
					functionPart = engine.evalQuiet(F.Limit(derivedFunction, F.Rule(x, x0)));
				}
				IExpr coefficient = F.Times.of(engine, F.Power(NumberTheory.factorial(i), F.CN1), functionPart);

				ps.setCoeff(i, coefficient);
				derivedFunction = F.D.of(engine, derivedFunction, x);
			}
			return ps;
		}

		private static ASTSeriesData timesSeriesData(IAST timesAST, IExpr x, IExpr x0, final int n, EvalEngine engine) {
			Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
			IASTAppendable rest = F.PlusAlloc(4);
			coefficientMap = new HashMap<IExpr, IExpr>();
			rest = F.TimesAlloc(4);
			coefficientMap = ExprPolynomialRing.createTimes(timesAST, x, coefficientMap, rest);
			int shift = 0;
			IExpr coefficient = F.C1;
			if (coefficientMap.size() == 1) {
				shift = coefficientMap.keySet().iterator().next().toIntDefault(0);
				if (shift != 0) {
					timesAST = rest;
					coefficient = coefficientMap.values().iterator().next();
				}
			}

			IExpr arg;
			// IInteger ni = F.ZZ(n + Math.abs(shift));
			int ni = n + Math.abs(shift);
			ASTSeriesData series;
			if (timesAST.size() == 1) {
				ASTSeriesData temp = seriesFromMap(x, x0, n, coefficientMap, rest);
				if (temp != null && rest.size() == 1) {
					return temp;
				}
				if (rest.size() == 1) {
					return null;
				}
				timesAST = rest;
				if (temp != null) {
					arg = seriesData(timesAST.arg1(), x, x0, ni, engine);
					if (arg instanceof ASTSeriesData) {
						series = temp.timesPS((ASTSeriesData) arg);
					} else {
						return null;
					}
				} else {
					arg = seriesData(timesAST.arg1(), x, x0, ni, engine);
					if (arg instanceof ASTSeriesData) {
						series = (ASTSeriesData) arg;
					} else {
						return null;
					}
				}
			} else {
				arg = seriesData(timesAST.arg1(), x, x0, ni, engine);
				if (arg instanceof ASTSeriesData) {
					series = (ASTSeriesData) arg;
				} else {
					return null;
				}
			}
			if (timesAST.size() != 1) {
				if (arg instanceof ASTSeriesData) {
					for (int i = 2; i < timesAST.size(); i++) {
						arg = seriesData(timesAST.get(i), x, x0, ni, engine);
						if (arg instanceof ASTSeriesData) {
							series = series.timesPS((ASTSeriesData) arg);
						} else {
							return null;
						}
					}
					if (shift != 0) {
						series = series.shift(shift, coefficient, n + 1);
					}
					return series;
				}
			}
			return null;
		}

		private static ASTSeriesData plusSeriesData(final IAST plusAST, IExpr x, IExpr x0, final int n,
				EvalEngine engine) {
			Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
			IASTAppendable rest = F.PlusAlloc(4);

			ASTSeriesData temp = polynomialSeries(plusAST, x, x0, n, coefficientMap, rest);
			if (temp != null) {
				if (rest.size() == 1) {
					return temp;
				}
			}
			ASTSeriesData series = null;
			IExpr arg;
			int start = 1;
			if (temp != null) {
				series = temp;
			} else {
				arg = seriesData(rest.arg1(), x, x0, n, engine);
				if (arg instanceof ASTSeriesData) {
					series = (ASTSeriesData) arg;
					start = 2;
				}
			}
			if (series != null) {
				for (int i = start; i < rest.size(); i++) {
					arg = seriesData(rest.get(i), x, x0, n, engine);
					if (arg instanceof ASTSeriesData) {
						series = series.plusPS((ASTSeriesData) arg);
					} else {
						series = null;
						break;
					}
				}
				if (series != null) {
					return series;
				}
			}
			return null;
		}

		private static ASTSeriesData powerSeriesData(final IExpr powerAST, IExpr x, IExpr x0, final int n,
				EvalEngine engine) {
			IExpr base = powerAST.base();
			IExpr exponent = powerAST.exponent();
			if (base.isFree(x)) {
				if (exponent.isPower() && exponent.base().equals(x) && exponent.exponent().isRational()) {
					IRational rat = (IRational) exponent.exponent();
					if (rat.isPositive()) {
						int numerator = rat.numerator().toIntDefault(Integer.MIN_VALUE);
						int denominator = rat.denominator().toIntDefault(Integer.MIN_VALUE);
						if (denominator != Integer.MIN_VALUE) {
							IExpr temp = seriesData(F.Power(base, x), x, x0, n * denominator, engine);
							if (temp instanceof ASTSeriesData) {
								ASTSeriesData series = (ASTSeriesData) temp;
								if (numerator != 1) {
									series = series.shiftTimes(numerator, F.C1, series.order());
								}
								series.setDenominator(denominator);
								return series;
							}
						}
					}
				}
			} else if (!(base instanceof ASTSeriesData)) {
				Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
				IASTAppendable rest = F.PlusAlloc(4);
				ASTSeriesData temp = polynomialSeries(powerAST, x, x0, n, coefficientMap, rest);
				if (temp != null) {
					return temp;
				}
			}
			int exp = exponent.toIntDefault(Integer.MIN_VALUE);
			if (exp != Integer.MIN_VALUE) {
				ASTSeriesData series = seriesData(base, x, x0, n, engine);
				if (series instanceof ASTSeriesData) {
					return series.pow(exp);
				}
			}
			return null;
		}

		private static ASTSeriesData polynomialSeries(final IExpr function, IExpr x, IExpr x0, final int n,
				Map<IExpr, IExpr> coefficientMap, IASTAppendable rest) {
			ExprPolynomialRing.create(function, x, coefficientMap, rest);
			if (coefficientMap.size() > 0) {
				return seriesFromMap(x, x0, n, coefficientMap, rest);
			}
			return null;
		}

		private static ASTSeriesData seriesFromMap(IExpr x, IExpr x0, final int n, Map<IExpr, IExpr> coefficientMap,
				IASTAppendable rest) {
			ASTSeriesData series = new ASTSeriesData(x, x0, 0, n + 1, 1);
			boolean evaled = false;
			for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
				IExpr coefficient = entry.getValue();
				if (coefficient.isZero()) {
					continue;
				}
				IExpr exp = entry.getKey();
				int exponent = exp.toIntDefault(Integer.MIN_VALUE);
				if (exponent == Integer.MIN_VALUE) {
					rest.append(F.Times(coefficient, F.Power(x, exp)));
				} else {
					series.setCoeff(exponent, coefficient);
					evaled = true;
				}

			}
			if (evaled) {
				return series;
			}
			return null;
		}
	}

	/**
	 * <pre>
	 * SeriesCoefficient(expr, {x, x0, n})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * get the coefficient of <code>(x- x0)^n</code> at the point <code>x = x0</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SeriesCoefficient(Sin(x),{x,f+g,n}) 
	 * Piecewise({{Sin(f+g+1/2*n*Pi)/n!,n&gt;=0}},0)
	 * </pre>
	 */
	private final static class SeriesCoefficient extends AbstractFunctionEvaluator implements SeriesCoefficientRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST2()) {
				if (ast.arg1() instanceof ASTSeriesData && ast.arg2().isInteger()) {
					ASTSeriesData series = (ASTSeriesData) ast.arg1();
					int n = ast.arg2().toIntDefault(Integer.MIN_VALUE);
					if (n >= 0) {
						int order = series.order();
						if (order > n) {
							return series.coefficient(n);
						} else {
							return F.Indeterminate;
						}
					}
					return F.NIL;
				}
				if (ast.arg2().isVector() == 3 && !(ast.arg1() instanceof ASTSeriesData)) {
					IExpr function = ast.arg1();

					IAST list = (IAST) ast.arg2();
					IExpr x = list.arg1();
					IExpr x0 = list.arg2();

					IExpr n = list.arg3();
					return functionCoefficient(ast, function, x, x0, n, engine);

				}
			}

			return F.NIL;
		}

		private static IExpr functionCoefficient(final IAST ast, IExpr function, IExpr x, IExpr x0, IExpr n,
				EvalEngine engine) {
			if (n.isReal()) {
				if (n.isFraction() && !((IFraction) n).denominator().isOne()) {
					return F.C0;
				}
				if (!n.isInteger()) {
					return F.NIL;
				} 
			}
			if (function.isFree(x)) {
				if (n.isZero()) {
					return function;
				}
				return F.Piecewise(F.List(F.List(function, F.Equal(n, F.C0))), F.C0);
			}
			IExpr temp = polynomialSeriesCoefficient(function, x, x0, n, ast, engine);
			if (temp.isPresent()) {
				return temp;
			}
			if (function.isPower()) {
				IExpr b = function.base();
				IExpr exponent = function.exponent();
				if (b.equals(x)) {
					if (exponent.isNumber()) {
						// x^exp
						INumber exp = (INumber) exponent;
						if (exp.isInteger()) {
							if (x0.isZero()) {
								return F.Piecewise(F.List(F.List(F.C1, F.Equal(n, exp))), F.C0);
							}
							return F.Piecewise(
									F.List(F.List(F.Times(F.Power(x0, F.Plus(exp, n.negate())), F.Binomial(exp, n)),
											F.LessEqual(F.C0, n, exp))),
									F.C0);
						}
					}
					if (!x0.isZero() && exponent.isFree(x)) {
						IExpr exp = exponent;
						return F.Piecewise(
								F.List(F.List(F.Times(F.Power(x0, F.Plus(exp, n.negate())), F.Binomial(exp, n)),
										F.GreaterEqual(n, F.C0))),
								F.C0);
					}
				}
				if (b.isFree(x)) {
					IExpr[] linear = exponent.linear(x);
					if (linear != null) {
						if (x0.isZero()) {
							// b^(a+c*x)
							IExpr a = linear[0];
							IExpr c = linear[1];
							return
							// [$ Piecewise({{(b^a*(c*Log(b))^n)/n!, n >= 0}}, 0) $]
							F.Piecewise(F.List(F.List(F.Times(F.Power(b, a), F.Power(F.Factorial(n), F.CN1),
									F.Power(F.Times(c, F.Log(b)), n)), F.GreaterEqual(n, F.C0))), F.C0); // $$;
						}
						if (linear[0].isZero() && linear[1].isOne()) {
							// b^x with b is free of x

							return F.Piecewise(F.List(F.List(
									F.Times(F.Power(b, x0), F.Power(F.Factorial(n), F.CN1), F.Power(F.Log(b), n)),
									F.GreaterEqual(n, F.C0))), F.C0);
						}
					}
				} else if (b.equals(exponent) && x0.isZero()) {
					// x^x
					if (exponent.equals(x)) {
						// x^x or b^x with b is free of x

						return F.Piecewise(F.List(
								F.List(F.Times(F.Power(b, x0), F.Power(F.Factorial(n), F.CN1), F.Power(F.Log(b), n)),
										F.GreaterEqual(n, F.C0))),
								F.C0);
					}
				}

			}

			if (x0.isReal()) {
				final int lowerLimit = x0.toIntDefault(Integer.MIN_VALUE);
				if (lowerLimit != 0) {
					// TODO support other cases than 0
					return F.NIL;
				}
				x0 = F.ZZ(lowerLimit);
			}

			final int degree = n.toIntDefault(Integer.MIN_VALUE);
			if (degree < 0) {
				return F.NIL;
			}

			if (degree == 0) {
				return F.ReplaceAll(function, F.Rule(x, x0));
			}
			IExpr derivedFunction = F.D.of(engine, function, F.List(x, n));
			return F.Times(F.Power(F.Factorial(n), F.CN1), F.ReplaceAll(derivedFunction, F.Rule(x, x0)));
		}

		/**
		 * 
		 * @param univariatePolynomial
		 * @param x
		 * @param x0
		 * @param n
		 * @param seriesTemplate
		 * @param engine
		 * @return
		 */
		public static IExpr polynomialSeriesCoefficient(IExpr univariatePolynomial, IExpr x, IExpr x0, IExpr n,
				final IAST seriesTemplate, EvalEngine engine) {
			try {
				// if (!x0.isZero()) {
				Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
				IASTAppendable rest = F.ListAlloc(4);
				ExprPolynomialRing.create(univariatePolynomial, x, coefficientMap, rest);
				IASTAppendable coefficientPlus = F.PlusAlloc(2);
				if (coefficientMap.size() > 0) {
					IExpr defaultValue = F.C0;
					IASTAppendable piecewiseAST = F.ast(F.Piecewise);
					IASTAppendable rules = F.ListAlloc(2);
					IASTAppendable plus = F.PlusAlloc(coefficientMap.size());
					IAST comparator = F.GreaterEqual(n, F.C0);
					for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
						IExpr exp = entry.getKey();
						if (exp.isZero()) {
							continue;
						}
						if (exp.isNegative() && x0.isZero()) {
							if (exp.equals(n)) {
								defaultValue = F.C1;
							}
							continue;
						}
						IExpr coefficient = entry.getValue();
						if (coefficient.isZero()) {
							continue;
						}

						IAST powerPart = F.Power(x0, exp);
						comparator = F.Greater(n, F.C0);
						IAST bin;
						int k = exp.toIntDefault(Integer.MIN_VALUE);
						if (k != Integer.MIN_VALUE) {
							if (k < 0) {
								// powerPart = F.Power(x0.negate(), exp);
								x0 = x0.negate();
								int nk = -k;
								IASTAppendable binomial = F.TimesAlloc(nk + 1);
								for (int i = 1; i < nk; i++) {
									binomial.append(F.Plus(n, F.ZZ(i)));
								}
								binomial.append(F.Power(F.Factorial(F.ZZ(nk - 1)), -1));
								bin = binomial;
								comparator = F.GreaterEqual(n, F.C0);
							} else {
								comparator = F.LessEqual(F.C0, n, exp);
								bin = F.Binomial(exp, n);
								// binomial = F.TimesAlloc(k);
								// for (int i = 0; i < k; i++) {
								// binomial.append(F.Subtract(n, F.ZZ(i)));
								// }
								// binomial.append(F.Power(F.Factorial(F.ZZ(k)), -1));
							}
						} else {
							bin = F.Binomial(exp, n);
						}
						if (coefficient.isOne()) {
							plus.append(F.Times(powerPart, bin));
						} else {
							plus.append(F.Times(coefficient, powerPart, bin));
						}
					}
					IExpr temp = engine.evaluate(plus);
					if (!temp.isZero()) {
						rules.append(F.List(engine.evaluate(F.Times(F.Power(x0, n.negate()), plus)), comparator));
					}
					if (comparator.isAST(F.Greater)) {
						plus = F.PlusAlloc(coefficientMap.size());
						for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
							IExpr exp = entry.getKey();
							IExpr coefficient = entry.getValue();
							if (coefficient.isZero()) {
								continue;
							}
							if (coefficient.isOne()) {
								plus.append(F.Times(F.Power(x0, exp)));
							} else {
								plus.append(F.Times(coefficient, F.Power(x0, exp)));
							}
						}
						rules.append(F.List(engine.evaluate(plus), F.Equal(n, F.C0)));
					}
					piecewiseAST.append(rules);

					piecewiseAST.append(defaultValue);
					coefficientPlus.append(piecewiseAST);
				} else {
					if (!univariatePolynomial.isPlus()) {
						return F.NIL;
					}
				}
				for (int i = 1; i < rest.size(); i++) {
					IASTAppendable term = seriesTemplate.copyAppendable();
					term.set(1, rest.get(i));
					coefficientPlus.append(term);
				}
				return coefficientPlus.oneIdentity0();
				// }
			} catch (RuntimeException re) {
				if (Config.SHOW_STACKTRACE) {
					re.printStackTrace();
				}
			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * internal structure of a power series at the point <code>x = x0</code> the <code>coeff</code>-i are coefficients
	 * of the power series.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2) 
	 * Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)
	 * </pre>
	 */
	private final static class SeriesData extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int denominator = 1;
			if (ast.size() == 6 || ast.size() == 7) {
				if (ast.arg1().isNumber()) {
					return F.Indeterminate;
				}
				IExpr x = ast.arg1();
				IExpr x0 = ast.arg2();

				if (ast.arg3().isVector() < 0) {
					return F.NIL;
				}
				IAST coefficients = (IAST) ast.arg3();
				final int nMin = ast.arg4().toIntDefault(Integer.MIN_VALUE);
				if (nMin == Integer.MIN_VALUE) {
					return F.NIL;
				}
				final int power = ast.arg5().toIntDefault(Integer.MIN_VALUE);
				if (power == Integer.MIN_VALUE) {
					return F.NIL;
				}
				if (ast.size() == 7) {
					denominator = ast.get(6).toIntDefault(Integer.MIN_VALUE);
					if (denominator < 1) {
						return F.NIL;
					}
				}
				return new ASTSeriesData(x, x0, coefficients, nMin, power, denominator);
			}
			return F.NIL;
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	private SeriesFunctions() {

	}

}
