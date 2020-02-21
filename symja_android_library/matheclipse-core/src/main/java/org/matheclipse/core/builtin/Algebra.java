package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Arg;
import static org.matheclipse.core.expression.F.Assumptions;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.I;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Null;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Combinatoric.Permutations;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherPlus;
import org.matheclipse.core.patternmatching.hash.HashedPatternRules;
import org.matheclipse.core.polynomials.IPartialFractionGenerator;
import org.matheclipse.core.polynomials.PartialFractionGenerator;
import org.matheclipse.core.polynomials.PolynomialHomogenization;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
import org.matheclipse.core.visit.VisitorExpr;

import com.google.common.math.LongMath;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.TermOrderByName;
import edu.jas.structure.RingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorComplex;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;

public class Algebra {
	public static boolean DEBUG = false;

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.Apart.setEvaluator(new Apart());
			F.Cancel.setEvaluator(new Cancel());
			F.Collect.setEvaluator(new Collect());
			F.Denominator.setEvaluator(new Denominator());
			F.Distribute.setEvaluator(new Distribute());
			F.Expand.setEvaluator(new Expand());
			F.ExpandAll.setEvaluator(new ExpandAll());
			F.Factor.setEvaluator(new Factor());
			F.FactorSquareFree.setEvaluator(new FactorSquareFree());
			F.FactorSquareFreeList.setEvaluator(new FactorSquareFreeList());
			F.FactorTerms.setEvaluator(new FactorTerms());
			F.FullSimplify.setEvaluator(new FullSimplify());
			F.Numerator.setEvaluator(new Numerator());

			F.PolynomialExtendedGCD.setEvaluator(new PolynomialExtendedGCD());
			F.PolynomialGCD.setEvaluator(new PolynomialGCD());
			F.PolynomialLCM.setEvaluator(new PolynomialLCM());
			F.PolynomialQ.setEvaluator(new PolynomialQ());
			F.PolynomialQuotient.setEvaluator(new PolynomialQuotient());
			F.PolynomialQuotientRemainder.setEvaluator(new PolynomialQuotientRemainder());
			F.PolynomialRemainder.setEvaluator(new PolynomialRemainder());

			F.PowerExpand.setEvaluator(new PowerExpand());
			F.Root.setEvaluator(new Root());
			F.Simplify.setEvaluator(new Simplify());
			F.Together.setEvaluator(new Together());
			F.ToRadicals.setEvaluator(new ToRadicals());
			F.Variables.setEvaluator(new Variables());
		}
	}

	protected static class InternalFindCommonFactorPlus {
		private static void splitTimesArg1(IExpr expr, HashMap<IExpr, IInteger> map) {
			if (expr.isTimes()) {
				IAST timesAST = (IAST) expr;
				for (int i = 1; i < timesAST.size(); i++) {
					IExpr temp = timesAST.get(i);
					if (temp.isPower() && temp.exponent().isInteger()) {
						if (!temp.base().isNumber()) {
							map.put(temp.base(), (IInteger) temp.exponent());
						}
					} else {
						if (!temp.isNumber()) {
							map.put(temp, F.C1);
						}
					}
				}
			} else if (expr.isPower() && expr.exponent().isInteger()) {
				if (!expr.base().isNumber()) {
					map.put(expr.base(), (IInteger) expr.exponent());
				}
			} else {
				if (!expr.isNumber()) {
					map.put(expr, F.C1);
				}
			}
		}

		private static boolean splitTimesRest(IExpr expr, HashMap<IExpr, IInteger> map) {
			if (map.size() > 0) {
				if (expr.isTimes()) {
					IAST timesAST = (IAST) expr;
					Iterator<Entry<IExpr, IInteger>> iter = map.entrySet().iterator();
					// for (Map.Entry<IExpr, IInteger> entry : map.entrySet()) {
					while (iter.hasNext()) {
						Map.Entry<IExpr, IInteger> entry = iter.next();
						final IExpr key = entry.getKey();
						boolean foundValue = false;
						for (int i = 1; i < timesAST.size(); i++) {
							IExpr temp = timesAST.get(i);
							if (temp.isPower() && temp.exponent().isInteger()) {
								if (temp.base().equals(key)) {
									IInteger value = entry.getValue();
									IInteger exponent = (IInteger) temp.exponent();
									if (value.equals(exponent.negate())) {
										return false;
									}
									if (exponent.isNegative()) {
										if (value.isLT(exponent)) {
											entry.setValue(exponent);
										}
									} else {
										if (value.isGT(exponent)) {
											entry.setValue(exponent);
										}
									}
									foundValue = true;
									break;
								}
							} else {
								if (temp.equals(key)) {
									IInteger value = entry.getValue();
									if (value.isMinusOne()) {
										return false;
									}
									if (value.isGT(F.C1)) {
										entry.setValue(F.C1);
									}
									foundValue = true;
									break;
								}
							}
						}
						if (!foundValue) {
							iter.remove();
							if (map.size() == 0) {
								return false;
							}
						}
					}
				} else {
					Iterator<Entry<IExpr, IInteger>> iter = map.entrySet().iterator();
					// for (Map.Entry<IExpr, IInteger> entry : map.entrySet()) {
					while (iter.hasNext()) {
						Map.Entry<IExpr, IInteger> entry = iter.next();
						final IExpr key = entry.getKey();
						if (expr.isPower() && expr.exponent().isInteger()) {
							if (!expr.base().equals(key)) {
								iter.remove();
								if (map.size() == 0) {
									return false;
								}
							} else {
								IInteger value = entry.getValue();
								IInteger exponent = (IInteger) expr.exponent();
								if (value.equals(exponent.negate())) {
									return false;
								}
								if (exponent.isNegative()) {
									if (value.isLT(exponent)) {
										entry.setValue(exponent);
									}
								} else {
									if (value.isGT(exponent)) {
										entry.setValue(exponent);
									}
								}
							}
						} else {
							if (!expr.equals(key)) {
								iter.remove();
								if (map.size() == 0) {
									return false;
								}
							} else {
								IInteger value = entry.getValue();
								if (value.isMinusOne()) {
									return false;
								}
								if (value.isGT(F.C1)) {
									entry.setValue(F.C1);
								}
							}
						}
					}
				}
			}
			return map.size() != 0;
		}

		/**
		 * Determine common factors in a <code>Plus(...)</code> expression. Index <code>[0]</code> contains the common
		 * factor. Index <code>[1]</code> contains the rest <code>Plus(...)</code> factor;
		 * 
		 * @param list
		 *            a <code>List(...)</code> or <code>Plus(...)</code> AST of terms
		 * @param reduceOneIdentityRest
		 *            reduce the rest expression if only 1 argument is assigned
		 * @return <code>null</code> if no common factor was found.
		 */
		public static IExpr[] findCommonFactors(IAST list, boolean reduceOneIdentityRest) {
			if (list.size() > 2) {
				HashMap<IExpr, IInteger> map = new HashMap<IExpr, IInteger>();
				splitTimesArg1(list.arg1(), map);
				if (map.size() != 0) {
					for (int i = 2; i < list.size(); i++) {
						if (!splitTimesRest(list.get(i), map)) {
							// fail fast
							return null;
						}
					}

					IASTAppendable commonFactor = F.TimesAlloc(map.size());
					for (Map.Entry<IExpr, IInteger> entry : map.entrySet()) {
						final IExpr key = entry.getKey();
						IInteger exponent = entry.getValue();
						if (exponent.isOne()) {
							commonFactor.append(key);
						} else {
							commonFactor.append(F.Power(key, exponent));
						}
					}

					IExpr[] result = new IExpr[2];
					result[0] = commonFactor.oneIdentity1();
					if (!result[0].isOne()) {
						IExpr inverse = result[0].inverse();

						IASTAppendable commonPlus = F.PlusAlloc(list.size());
						list.forEach(x -> commonPlus.append(F.Times(inverse, x)));
						// for (int i = 1; i < plusAST.size(); i++) {
						// commonPlus.append(F.Times(inverse, plusAST.get(i)));
						// }
						if (reduceOneIdentityRest) {
							result[1] = commonPlus.oneIdentity1();
						}
						return result;
					}
				}
			}
			return null;
		}
	}

	/**
	 * <h2>Apart</h2>
	 * 
	 * <pre>
	 * <code>Apart(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * rewrites <code>expr</code> as a sum of individual fractions.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>Apart(expr, var)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * treats <code>var</code> as main variable.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Apart((x-1)/(x^2+x))
	 * 2/(x+1)-1/x
	 * 
	 * &gt;&gt; Apart(1 / (x^2 + 5x + 6))
	 * 1/(2+x)+1/(-3-x) 
	 * </code>
	 * </pre>
	 * <p>
	 * When several variables are involved, the results can be different depending on the main variable:
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Apart(1 / (x^2 - y^2), x)
	 * -1 / (2 y (x + y)) + 1 / (2 y (x - y))
	 * &gt;&gt; Apart(1 / (x^2 - y^2), y)
	 * 1 / (2 x (x + y)) + 1 / (2 x (x - y))
	 * </code>
	 * </pre>
	 * <p>
	 * 'Apart' is 'Listable':
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Apart({1 / (x^2 + 5x + 6)})
	 * {1/(2+x)+1/(-3-x)}
	 * </code>
	 * </pre>
	 * <p>
	 * But it does not touch other expressions:
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Sin(1 / (x ^ 2 - y ^ 2)) // Apart
	 * Sin(1/(x^2-y^2))
	 * </code>
	 * </pre>
	 */
	private static class Apart extends AbstractFunctionEvaluator {

		/**
		 * Return the denominator for the given <code>Power[...]</code> AST, by separating positive and negative powers.
		 * 
		 * @param powerAST
		 *            a power expression (a^b)
		 * @param trig
		 *            if <code>true</code> get the "trigonometric form" of the given function. Example: Csc[x] gives
		 *            Sin[x].
		 * @param splitPowerPlusExponents
		 *            split <code>Power()</code> expressions with <code>Plus()</code> exponents like
		 *            <code>a^(-x+y)</code> into numerator <code>a^y</code> and denominator <code>a^x</code>
		 * @return the numerator and denominator expression
		 */
		public static IExpr[] fractionalPartsPower(final IAST powerAST, boolean trig, boolean splitPowerPlusExponents) {
			IExpr[] parts = new IExpr[2];
			parts[0] = F.C1;

			IExpr base = powerAST.base();
			IExpr exponent = powerAST.exponent();
			if (exponent.isReal()) {
				ISignedNumber sn = (ISignedNumber) exponent;
				if (sn.isMinusOne()) {
					parts[1] = base;
					return parts;
				} else if (sn.isNegative()) {
					parts[1] = F.Power(base, sn.negate());
					return parts;
				} else {
					if (sn.isInteger() && base.isAST()) {
						// positive integer
						IAST function = (IAST) base;
						// if (function.isTimes()) {
						// IExpr[] partsArg1 = fractionalPartsTimesPower(function, true, true, trig,
						// true);
						// if (partsArg1 != null) {
						// parts[0] = F.Power(partsArg1[0], sn);
						// parts[1] = F.Power(partsArg1[1], sn);
						// return parts;
						// }
						// }
						IExpr numerForm = Numerator.getTrigForm(function, trig);
						if (numerForm.isPresent()) {
							IExpr denomForm = Denominator.getTrigForm(function, trig);
							if (denomForm.isPresent()) {
								parts[0] = F.Power(numerForm, sn);
								parts[1] = F.Power(denomForm, sn);
								return parts;
							}
						}
					}
				}
			} else if (splitPowerPlusExponents && exponent.isPlus()) {
				// base ^ (a+b+c...)
				IAST plusAST = (IAST) exponent;
				IAST[] result = plusAST.filterNIL(AbstractFunctionEvaluator::getNormalizedNegativeExpression);
				parts[1] = base.power(result[0].oneIdentity0());
				parts[0] = base.power(result[1].oneIdentity0());
				return parts;
			}
			IExpr positiveExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(exponent);
			if (positiveExpr.isPresent()) {
				parts[1] = F.Power(base, positiveExpr);
				return parts;
			}
			return null;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			IAST tempAST = Structure.threadLogicEquationOperators(arg1, ast, 1);
			if (tempAST.isPresent()) {
				return tempAST;
			}

			IAST variableList = null;
			if (ast.isAST2()) {
				variableList = Validate.checkSymbolOrSymbolList(ast, 2, engine);
				if (!variableList.isPresent()) {
					return F.NIL;
				}
			} else {
				VariablesSet eVar = new VariablesSet(arg1);
				if (eVar.isSize(0)) {
					return F.evalExpandAll(arg1, engine);
				}
				if (!eVar.isSize(1)) {
					// partial fraction only possible for univariate polynomials
					return F.evalExpandAll(arg1, engine);
				}
				variableList = eVar.getVarList();
			}

			if (variableList.size() == 2 && (arg1.isTimes() || arg1.isPower())) {
				IExpr[] parts = fractionalParts(arg1, false);
				if (parts != null) {
					IExpr variable = variableList.arg1();
					IExpr temp = partsApart(parts, variable, engine);
					if (temp.isPresent()) {
						return temp;
					}
				}
				return arg1;
			}
			return F.evalExpandAll(arg1, engine);
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <h2>Cancel</h2>
	 * 
	 * <pre>
	 * <code>Cancel(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * cancels out common factors in numerators and denominators.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Cancel(x / x ^ 2)
	 * 1/x
	 * </code>
	 * </pre>
	 * <p>
	 * 'Cancel' threads over sums:
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Cancel(x / x ^ 2 + y / y ^ 2)
	 * 1/x+1/y
	 * 
	 * &gt;&gt; Cancel(f(x) / x + x * f(x) / x ^ 2)
	 * (2*f(x))/x
	 * </code>
	 * </pre>
	 */
	private static class Cancel extends AbstractFunctionEvaluator {

		/**
		 * This predicate identifies polynomial expressions. It requires that the given expression is already expanded
		 * for <code>Plus,Power and Times</code> operations.
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
		private static IExpr[] calculatePlusIntegerGCD(IASTAppendable numeratorPlus, IInteger denominatorInt,
				IInteger gcd) {
			numeratorPlus.forEach((x, i) -> {
				if (x.isInteger()) {
					numeratorPlus.set(i, ((IInteger) x).div(gcd));
				} else if (x.isTimes() && x.first().isInteger()) {
					IASTMutable times = ((IAST) x).copy();
					times.set(1, ((IInteger) times.arg1()).div(gcd));
					numeratorPlus.set(i, times);
				} else {
					throw new WrongArgumentType(numeratorPlus, x, i, "unexpected argument");
				}
			});
			// for (int i = 1; i < numeratorPlus.size(); i++) {
			// if (numeratorPlus.get(i).isInteger()) {
			// numeratorPlus.set(i, ((IInteger) numeratorPlus.get(i)).div(gcd));
			// } else if (numeratorPlus.get(i).isTimes() && numeratorPlus.get(i).first().isInteger()) {
			// IASTMutable times = ((IAST) numeratorPlus.get(i)).copy();
			// times.set(1, ((IInteger) times.arg1()).div(gcd));
			// numeratorPlus.set(i, times);
			// } else {
			// throw new WrongArgumentType(numeratorPlus, numeratorPlus.get(i), i, "unexpected argument");
			// }
			// }
			IExpr[] result = new IExpr[3];
			result[0] = F.C1;
			result[1] = numeratorPlus;
			result[2] = denominatorInt.div(gcd);
			return result;
		}

		/**
		 * Calculate the GCD[] of the integer factors in each element of the <code>numeratorPlus</code> expression with
		 * the <code>denominatorInt</code>. After that return the result divided by the gcd value, if possible.
		 * 
		 * @param numeratorPlus
		 *            a <code>Plus[...]</code> expression as the numerator
		 * @param denominatorInt
		 *            an integer value for the denominator
		 * @return <code>null</code> if no gcd value was found
		 */
		private static IExpr[] cancelPlusIntegerGCD(IAST numeratorPlus, IInteger denominatorInt) {
			IASTAppendable plus = numeratorPlus.copyAppendable();
			IASTAppendable gcd = F.ast(F.GCD, plus.size() + 1, false);
			gcd.append(denominatorInt);
			boolean evaled = !plus.exists(x -> {
				if (x.isInteger()) {
					gcd.append(x);
				} else {
					if (x.isTimes() && x.first().isInteger()) {
						gcd.append(x.first());
					} else {
						return true;
					}
				}
				return false;
			});
			// for (int i = 1; i < plus.size(); i++) {
			// IExpr temp = plus.get(i);
			// if (temp.isInteger()) {
			// gcd.append(temp);
			// } else {
			// if (temp.isTimes() && temp.first().isInteger()) {
			// gcd.append(temp.first());
			// } else {
			// evaled = false;
			// break;
			// }
			// }
			// }
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
		 *            an <code>Times[...] or Power[...]</code> AST, where common factors should be canceled out.
		 * @return <code>F.NIL</code> is no evaluation was possible
		 * @throws JASConversionException
		 */
		public static IExpr togetherPowerTimes(IExpr powerTimesAST) throws JASConversionException {
			IExpr[] parts = fractionalParts(powerTimesAST, false);
			if (parts != null && parts[0].isPlus() && parts[1].isPlus()) {
				IAST numParts = ((IAST) parts[0]).partitionPlus(new PolynomialPredicate(), F.C0, F.C1, F.List);
				IAST denParts = ((IAST) parts[1]).partitionPlus(new PolynomialPredicate(), F.C0, F.C1, F.List);
				if (denParts.isPresent() && !denParts.arg1().isOne()) {
					IExpr[] result = cancelGCD(numParts.arg1(), denParts.arg1());
					if (result != null) {
						return F.Times(result[0], result[1], numParts.arg2(),
								F.Power(F.Times(result[2], denParts.arg2()), F.CN1));
					}
				}

			}
			return F.NIL;
		}

		public static IExpr cancelPowerTimes(IExpr powerTimesAST) throws JASConversionException {
			IExpr[] parts = fractionalParts(powerTimesAST, false);
			if (parts != null) {
				IExpr p00 = parts[0];
				IExpr p01 = F.C1;
				IExpr p10 = parts[1];
				IExpr p11 = F.C1;
				if (p00.isPlus()) {
					IAST numParts = ((IAST) p00).partitionPlus(new PolynomialPredicate(), F.C0, F.C1, F.List);
					if (numParts.isPresent() && !numParts.arg1().isOne()) {
						p00 = numParts.arg1();
						p01 = numParts.arg2();
					}
				}

				if (p10.isPlus()) {
					IAST denParts = ((IAST) p10).partitionPlus(new PolynomialPredicate(), F.C0, F.C1, F.List);
					if (denParts.isPresent() && !denParts.arg1().isOne()) {
						p10 = denParts.arg1();
						p11 = denParts.arg2();
					}
				}
				if (!p10.isOne()) {
					IExpr[] result = cancelGCD(p00, p10);
					if (result != null) {
						return F.Times(result[0], result[1], p01, F.Power(F.Times(result[2], p11), F.CN1));
					}
				}

			}
			return F.NIL;
		}

		private static IExpr[] cancelQuotientRemainder(final IExpr arg1, IExpr arg2, IExpr variable) {
			IExpr[] result = new IExpr[2];
			try {

				JASConvert<BigRational> jas = new JASConvert<BigRational>(variable, BigRational.ZERO);
				GenPolynomial<BigRational> poly1 = jas.expr2JAS(arg1, false);
				GenPolynomial<BigRational> poly2 = jas.expr2JAS(arg2, false);
				if (poly1.degree() > poly2.degree()) {
					GenPolynomial<BigRational>[] divRem = poly1.quotientRemainder(poly2);
					if (!divRem[1].isZERO()) {
						return null;
					}
					result[0] = jas.rationalPoly2Expr(divRem[0], false);
					result[1] = F.C1;
				} else {
					GenPolynomial<BigRational>[] divRem = poly2.quotientRemainder(poly1);
					if (!divRem[1].isZERO()) {
						return null;
					}
					result[0] = F.C1;
					result[1] = jas.rationalPoly2Expr(divRem[0], false);
				}
				return result;
			} catch (JASConversionException e1) {
				try {
					JASIExpr jas = new JASIExpr(variable, ExprRingFactory.CONST);
					GenPolynomial<IExpr> poly1 = jas.expr2IExprJAS(arg1);
					GenPolynomial<IExpr> poly2 = jas.expr2IExprJAS(arg2);
					if (poly1.degree() > poly2.degree()) {
						GenPolynomial<IExpr>[] divRem = poly1.quotientRemainder(poly2);
						if (!divRem[1].isZERO()) {
							return null;
						}
						result[0] = jas.exprPoly2Expr(divRem[0], variable);
						result[1] = F.C1;
					} else {
						GenPolynomial<IExpr>[] divRem = poly2.quotientRemainder(poly1);
						if (!divRem[1].isZERO()) {
							return null;
						}
						result[0] = F.C1;
						result[1] = jas.exprPoly2Expr(divRem[0]);
					}
					return result;
				} catch (JASConversionException e) {
					if (Config.SHOW_STACKTRACE) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (ast.isAST1() && arg1.isAtom()) {
				return arg1;
			}
			IExpr temp = Structure.threadPlusLogicEquationOperators(arg1, ast, 1);
			if (temp.isPresent()) {
				return temp;
			}
			temp = cancelFractionPowers(engine, arg1);
			if (temp.isPresent()) {
				return temp;
			}
			temp = cancelNIL(arg1, engine);
			if (temp.isPresent()) {
				return temp;
			}
			return arg1;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		private IExpr cancelFractionPowers(EvalEngine engine, IExpr arg1) {
			IExpr temp;
			IExpr[] parts = fractionalParts(arg1, false);
			if (parts != null && //
					((parts[0].isPower() && parts[0].exponent().isInteger()) //
							|| (parts[1].isPower() && parts[1].exponent().isInteger()))) {
				IExpr numer = parts[0];
				// use long values see: https://lgtm.com/rules/7900075/
				long numerExponent = 1;
				long denomExponent = 1;
				IExpr denom = parts[1];
				if (numer.isPower()) {
					numerExponent = numer.exponent().toIntDefault(Integer.MIN_VALUE);
					numer = numer.base();
				}
				if (denom.isPower()) {
					denomExponent = denom.exponent().toIntDefault(Integer.MIN_VALUE);
					denom = denom.base();
				}
				if (numerExponent > 0 && denomExponent > 0) {
					temp = cancelNIL(F.Times(numer, F.Power(denom, -1)), engine);
					if (temp.isPresent()) {
						if (numerExponent > denomExponent) {
							return F.Times(F.Power(temp, numerExponent - denomExponent),
									F.Power(numer, numerExponent - denomExponent));
						} else if (numerExponent < denomExponent) {
							return F.Times(F.Power(temp, denomExponent - numerExponent),
									F.Power(denom, -1 * (denomExponent - numerExponent)));
						}
						return F.Power(temp, numerExponent);
					}
				}
			}
			return F.NIL;
		}

		private IExpr cancelNIL(IExpr arg1, EvalEngine engine) {
			try {
				if (arg1.isTimes() || arg1.isPower()) {
					IExpr result = togetherPowerTimes(arg1);
					if (result.isPresent()) {
						return result;
					}
				}
				IExpr expandedArg1 = F.evalExpandAll(arg1, engine);

				if (expandedArg1.isPlus()) {
					return ((IAST) expandedArg1).mapThread(F.Cancel(null), 1);
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
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * <code>Collect(expr, variable)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * collect subexpressions in <code>expr</code> which belong to the same <code>variable</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>Collect(expr, variable, head)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * collect subexpressions in <code>expr</code> which belong to the same <code>variable</code> and apply
	 * <code>head</code> on these subexpressions.
	 * </p>
	 * </blockquote>
	 * <p>
	 * Collect additive terms of an expression.
	 * </p>
	 * <p>
	 * This function collects additive terms of an expression with respect to a list of expression up to powers with
	 * rational exponents. By the term symbol here are meant arbitrary expressions, which can contain powers, products,
	 * sums etc. In other words symbol is a pattern which will be searched for in the expression's terms.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Collect(a*x^2 + b*x^2 + a*x - b*x + c, x)
	 * c+(a-b)*x+(a+b)*x^2
	 * 
	 * &gt;&gt; Collect(a*Exp(2*x) + b*Exp(2*x), Exp(2*x))
	 * (a+b)*E^(2*x)
	 * 
	 * &gt;&gt; Collect(x^2 + y*x^2 + x*y + y + a*y, {x, y})
	 * (1+a)*y+x*y+x^2*(1+y)
	 * </code>
	 * </pre>
	 */
	private static class Collect extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 3 && ast.size() <= 4) {
				try {
					IExpr arg1 = ast.arg1();
					IAST temp = Structure.threadLogicEquationOperators(arg1, ast, 1);
					if (temp.isPresent()) {
						return temp;
					}
					IExpr arg3Head = null;
					arg1 = engine.evaluate(arg1);
					arg1 = F.expandAll(arg1, true, true);
					final IExpr arg2 = engine.evalPattern(ast.arg2());
					if (!arg2.isList()) {
						if (ast.isAST3()) {
							arg3Head = engine.evaluate(ast.arg3());
						}
						return collectSingleVariable(arg1, arg2, null, 1, arg3Head, engine);
					}
					IAST list = (IAST) arg2;
					if (list.size() > 1) {
						if (ast.isAST3()) {
							arg3Head = engine.evaluate(ast.arg3());
						}
						return collectSingleVariable(arg1, list.arg1(), (IAST) arg2, 2, arg3Head, engine);
					}
					return arg1;
				} catch (Exception e) {
					if (Config.SHOW_STACKTRACE) {
						e.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

		/**
		 * Collect terms in <code>expr</code> containing the same power expressions of <code>x</code>.
		 * 
		 * @param expr
		 * @param x
		 *            the current variable from the list of variables which should be collected
		 * @param listOfVariables
		 *            list of variables which should be collected or <code>null</code> if no list is available
		 * @param listPosition
		 *            position of the next variable in the list after <code>x</code> which should be collected
		 *            recursively
		 * @param head
		 *            the head which should be applied to each coefficient or <code>null</code> if no head should be
		 *            applied
		 * @param engine
		 *            the evaluation engine
		 * @return
		 */
		private IExpr collectSingleVariable(IExpr expr, IExpr x, final IAST listOfVariables, final int listPosition,
				IExpr head, EvalEngine engine) {
			if (expr.isAST()) {
				Map<IExpr, IASTAppendable> map = new HashMap<IExpr, IASTAppendable>();
				IAST poly = (IAST) expr;
				IASTAppendable rest = F.PlusAlloc(poly.size());

				// IPatternMatcher matcher = new PatternMatcherEvalEngine(x, engine);
				final IPatternMatcher matcher = engine.evalPatternMatcher(x);
				collectToMap(poly, matcher, map, rest);
				if (listOfVariables != null && listPosition < listOfVariables.size()) {
					// collect next pattern in sub-expressions
					IASTAppendable result = F.PlusAlloc(map.size() + 1);
					if (rest.size() > 1) {
						result.append(collectSingleVariable(rest.oneIdentity0(), listOfVariables.get(listPosition),
								listOfVariables, listPosition + 1, head, engine));
					}
					for (Entry<IExpr, IASTAppendable> entry : map.entrySet()) {
						IExpr temp = collectSingleVariable(entry.getValue().oneIdentity0(),
								listOfVariables.get(listPosition), listOfVariables, listPosition + 1, head, engine);
						result.append(F.Times(entry.getKey(), temp));
					}
					return result;
				}

				if (head != null) {
					IASTMutable simplifyAST = (IASTMutable) F.unaryAST1(head, null);
					IExpr coefficient;
					rest.forEach((arg, i) -> {
						simplifyAST.set(1, arg);
						rest.set(i, engine.evaluate(simplifyAST));
					});
					for (Map.Entry<IExpr, IASTAppendable> entry : map.entrySet()) {
						simplifyAST.set(1, entry.getValue());
						coefficient = engine.evaluate(simplifyAST);
						if (coefficient.isPlus()) {
							rest.append(F.Times(entry.getKey()).appendOneIdentity((IAST) coefficient));
						} else {
							rest.append(entry.getKey().times(coefficient));
						}
					}
				} else {
					IAST coefficient;
					for (IExpr key : map.keySet()) {
						coefficient = map.get(key);
						IASTAppendable times = F.TimesAlloc(2);
						times.append(key);
						times.appendOneIdentity(coefficient);
						rest.append(times);
					}
				}
				return rest.oneIdentity0();
			}
			return expr;
		}

		public void collectToMap(IExpr expr, IPatternMatcher matcher, Map<IExpr, IASTAppendable> map,
				IASTAppendable rest) {
			if (expr.isFree(matcher, false)) {
				rest.append(expr);
				return;
			} else if (matcher.test(expr)) {
				addPowerFactor(expr, F.C1, map);
				return;
			} else if (isPowerMatched(expr, matcher)) {
				addPowerFactor(expr, F.C1, map);
				return;
			} else if (expr.isPlus()) {
				IAST plusAST = (IAST) expr;
				IASTAppendable clone = plusAST.copyAppendable();
				int i = 1;
				while (i < clone.size()) {
					if (collectToMapPlus(clone.get(i), matcher, map)) {
						clone.remove(i);
					} else {
						i++;
					}
				}
				if (clone.size() > 1) {
					rest.appendOneIdentity(clone);
				}
				return;
			} else if (expr.isTimes()) {
				IAST timesAST = (IAST) expr;
				if (timesAST.exists((x, i) -> {
					if (matcher.test(x) || isPowerMatched(x, matcher)) {
						IASTAppendable clone = timesAST.copyAppendable();
						clone.remove(i);
						addOneIdentityPowerFactor(x, clone, map);
						return true;
					}
					return false;
				}, 1)) {
					return;
				}
				rest.append(expr);
				return;
			}
			rest.append(expr);
			return;
		}

		public boolean collectToMapPlus(IExpr expr, IPatternMatcher matcher, Map<IExpr, IASTAppendable> map) {
			if (expr.isFree(matcher, false)) {
				return false;
			} else if (matcher.test(expr)) {
				addPowerFactor(expr, F.C1, map);
				return true;
			} else if (isPowerMatched(expr, matcher)) {
				addPowerFactor(expr, F.C1, map);
				return true;
			} else if (expr.isTimes()) {
				IAST timesAST = (IAST) expr;
				return timesAST.exists((x, i) -> {
					if (matcher.test(x) || isPowerMatched(x, matcher)) {
						IAST clone = timesAST.splice(i);
						addOneIdentityPowerFactor(x, clone, map);
						return true;
					}
					return false;
				}, 1);
			}

			return false;
		}

		public void addOneIdentityPowerFactor(IExpr key, IAST subAST, Map<IExpr, IASTAppendable> map) {
			IASTAppendable resultList = map.get(key);
			if (resultList == null) {
				resultList = F.PlusAlloc(8);
				map.put(key, resultList);
			}
			resultList.appendOneIdentity(subAST);
		}

		public void addPowerFactor(IExpr key, IExpr value, Map<IExpr, IASTAppendable> map) {
			IASTAppendable resultList = map.get(key);
			if (resultList == null) {
				resultList = F.PlusAlloc(8);
				map.put(key, resultList);
			}
			resultList.append(value);
		}

		public boolean isPowerMatched(IExpr poly, IPatternMatcher matcher) {
			return poly.isPower() && poly.exponent().isNumber() && matcher.test(poly.base());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <h2>Denominator</h2>
	 * 
	 * <pre>
	 * <code>Denominator(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the denominator in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Denominator(a / b)
	 * b
	 * &gt;&gt; Denominator(2 / 3)
	 * 3
	 * &gt;&gt; Denominator(a + b)
	 * 1
	 * </code>
	 * </pre>
	 */
	private static class Denominator extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			boolean numericMode = engine.isNumericMode();
			try {
				engine.setNumericMode(false);
				boolean trig = false;
				if (ast.isAST2()) {
					final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine, true);
					if (options.isInvalidPosition()) {
						return IOFunctions.printMessage(F.Denominator, "nonopt",
								F.List(ast.arg2(), F.ZZ(options.getInvalidPosition() - 1), ast), engine);
					}
					IExpr option = options.getOption(F.Trig);
					if (option.isTrue()) {
						trig = true;
					}
				}

				IExpr expr = engine.evaluate(ast.arg1());
				if (expr.isRational()) {
					return ((IRational) expr).denominator();
				}
				IExpr[] parts = fractionalParts(expr, trig);
				if (parts == null) {
					return F.C1;
				}
				return parts[1];
			} finally

			{
				engine.setNumericMode(numericMode);
			}
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Trig, F.False));
		}

		/**
		 * Get the &quot;denominator form&quot; of the given function. Example: <code>Csc[x]</code> gives
		 * <code>Sin[x]</code>.
		 * 
		 * @param function
		 *            the function which should be transformed to &quot;denominator form&quot; determine the denominator
		 *            by splitting up functions like <code>Tan[],Cot[], Csc[],...</code>
		 * @param trig
		 * @return <code>F.NIL</code> if <code>trig</code> is false or no form is found; may return <code>1</code> if no
		 *         denominator form is available (Example Cos[]).
		 */
		public static IExpr getTrigForm(IAST function, boolean trig) {
			if (trig) {
				if (function.isAST1()) {
					for (int i = 0; i < F.DENOMINATOR_NUMERATOR_SYMBOLS.length; i++) {
						ISymbol sym = F.DENOMINATOR_NUMERATOR_SYMBOLS[i];
						if (function.head().equals(sym)) {
							IExpr result = F.DENOMINATOR_TRIG_TRUE_EXPRS[i];
							if (result.isSymbol()) {
								return F.unaryAST1(result, function.arg1());
							}
							return result;
						}
					}
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Distribute(f(x1, x2, x3,...))
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * distributes <code>f</code> over <code>Plus</code> appearing in any of the <code>xi</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Distributive_property">Wikipedia - Distributive property</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Distribute((a+b)*(x+y+z))
	 * a*x+a*y+a*z+b*x+b*y+B*z
	 * </pre>
	 */
	private final static class Distribute extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IAST temp = engine.evalArgs(ast, ISymbol.NOATTRIBUTE).orElse(ast);
			IExpr arg1 = temp.arg1();
			IExpr head = F.Plus;
			if (temp.size() >= 3) {
				head = temp.arg2();
			}
			if (temp.isAST3()) {
				if (!arg1.head().equals(temp.arg3())) {
					return arg1;
				}
			}

			if (arg1.isAST()) {
				IASTAppendable resultCollector;
				if (temp.size() >= 5) {
					resultCollector = F.ast(temp.arg4());
				} else {
					resultCollector = F.ast(head);
				}
				IASTAppendable stepResult;
				if (temp.size() >= 6) {
					stepResult = F.ast(temp.arg5());
				} else {
					stepResult = F.ast(arg1.head());
				}
				distributePosition(resultCollector, stepResult, head, (IAST) arg1, 1);
				return resultCollector;
			}
			return arg1;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_5;
		}

		private void distributePosition(IASTAppendable resultCollector, IASTAppendable stepResult, IExpr head,
				IAST arg1, int position) {
			if (arg1.size() == position) {
				resultCollector.append(stepResult);
				return;
			}
			if (arg1.size() < position) {
				return;
			}
			if (arg1.get(position).isAST(head)) {
				IAST temp = (IAST) arg1.get(position);
				temp.forEach(x -> {
					IASTAppendable res2 = stepResult.copyAppendable();
					res2.append(x);
					distributePosition(resultCollector, res2, head, arg1, position + 1);
				});
			} else {
				IASTAppendable res2 = stepResult;
				res2.append(arg1.get(position));
				distributePosition(resultCollector, res2, head, arg1, position + 1);
			}

		}
	}

	/**
	 * <pre>
	 * Expand(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * expands out positive rational powers and products of sums in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Expand((x + y) ^ 3)
	 * x^3+3*x^2*y+3*x*y^2+y^3
	 * 
	 * &gt;&gt; Expand((a + b) (a + c + d))  
	 * a^2+a*b+a*c+b*c+a*d+b*d 
	 * 
	 * &gt;&gt; Expand((a + b) (a + c + d) (e + f) + e a a)  
	 * 2*a^2*e+a*b*e+a*c*e+b*c*e+a*d*e+b*d*e+a^2*f+a*b*f+a*c*f+b*c*f+a*d*f+b*d*f 
	 * 
	 * &gt;&gt; Expand((a + b) ^ 2 * (c + d))  
	 * a^2*c+2*a*b*c+b^2*c+a^2*d+2*a*b*d+b^2*d 
	 * 
	 * &gt;&gt; Expand((x + y) ^ 2 + x y) 
	 * x^2+3*x*y+y^2  
	 * 
	 * &gt;&gt; Expand(((a + b) (c + d)) ^ 2 + b (1 + a))  
	 * a^2*c^2+2*a*b*c^2+b^2*c^2+2*a^2*c*d+4*a*b*c*d+2*b^2*c*d+a^2*d^2+2*a*b*d^2+b^2*d^2+b(1+a)
	 * </pre>
	 * <p>
	 * <code>Expand</code> expands out rational powers by expanding the <code>Floor()</code> part of the rational powers
	 * number:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Expand((x + 3)^(5/2)+(x + 1)^(3/2)) Sqrt(1+x)+x*Sqrt(1+x)+9*Sqrt(3+x)+6*x*Sqrt(3+x)+x^2*Sqrt(3+x)
	 * </pre>
	 * <p>
	 * <code>Expand</code> expands items in lists and rules:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Expand({4 (x + y), 2 (x + y) -&gt; 4 (x + y)})  
	 * {4*x+4*y,2*(x+y)-&gt;4*(x+y)}
	 * </pre>
	 * <p>
	 * <code>Expand</code> does not change any other expression.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Expand(Sin(x*(1 + y)))  
	 * Sin(x*(1+y)) 
	 * 
	 * &gt;&gt; a*(b*(c+d)+e) // Expand  
	 * a*b*c+a*b*d+a*e 
	 * 
	 * &gt;&gt; (y^2)^(1/2)/(2x+2y)//Expand  
	 * Sqrt(y^2)/(2*x+2*y) 
	 * 
	 * &gt;&gt; 2(3+2x)^2/(5+x^2+3x)^3 // Expand  
	 * 18/(5+3*x+x^2)^3+(24*x)/(5+3*x+x^2)^3+(8*x^2)/(5+3*x+x^2)^3
	 * </pre>
	 */
	private static class Expand extends AbstractFunctionEvaluator {

		private static class Expander {

			boolean expandNegativePowers;

			boolean distributePlus;
			/**
			 * Pattern which may be <code>null</code>
			 */
			IExpr pattern;

			public Expander(IExpr pattern, boolean expandNegativePowers, boolean distributePlus) {
				this.pattern = pattern;
				this.expandNegativePowers = expandNegativePowers;
				this.distributePlus = distributePlus;
			}

			/**
			 * Check if the given expression doesn't contain the pattern.
			 * 
			 * @param expression
			 * @return
			 */
			public boolean isPatternFree(IExpr expression) {
				return (pattern != null && expression.isFree(pattern, false));
			}

			/**
			 * 
			 * @param ast
			 * @param evalParts
			 *            evaluate the determined numerator and denominator parts
			 * @return
			 */
			private IExpr expandAST(final IAST ast, boolean evalParts) {
				if (isPatternFree(ast)) {
					return F.NIL;
				}
				if (ast.isExpanded() && expandNegativePowers && !distributePlus) {
					return F.NIL;
				}
				if (ast.isPower()) {
					return expandPowerNull(ast);
				} else if (ast.isTimes()) {
					// (a+b)*(c+d)...

					IExpr[] temp = fractionalPartsTimesPower(ast, false, false, false, evalParts, true, true);
					IExpr tempExpr;
					if (temp == null) {
						return expandTimes(ast);
					}
					if (temp[0].isOne()) {
						if (temp[1].isTimes()) {
							tempExpr = expandTimes((IAST) temp[1]);
							if (tempExpr.isPresent()) {
								return F.Power(tempExpr, F.CN1);
							}
							addExpanded(ast);
							return F.NIL;
						}
						if (temp[1].isPower() || temp[1].isPlus()) {
							IExpr denom = expandAST((IAST) temp[1], evalParts);
							if (denom.isPresent()) {
								return F.Power(denom, F.CN1);
							}
						}
						addExpanded(ast);
						return F.NIL;
					}

					if (temp[1].isOne()) {
						return expandTimes(ast);
					}

					boolean evaled = false;
					if (temp[0].isTimes()) {
						tempExpr = expandTimes((IAST) temp[0]);
						if (tempExpr.isPresent()) {
							temp[0] = tempExpr;
							evaled = true;
						}
					}
					if (expandNegativePowers) {
						if (temp[1].isTimes()) {
							tempExpr = expandTimes((IAST) temp[1]);
							if (tempExpr.isPresent()) {
								temp[1] = tempExpr;
								evaled = true;
							}
						} else {
							if (temp[1].isPower() || temp[1].isPlus()) {
								IExpr denom = expandAST((IAST) temp[1], evalParts);
								if (denom.isPresent()) {
									temp[1] = denom;
									evaled = true;
								}
							}
						}

					}
					IExpr powerAST = F.Power(temp[1], F.CN1);
					if (distributePlus && temp[0].isPlus()) {
						IAST mappedAST = ((IAST) temp[0]).mapThread(F.Times(null, powerAST), 1);
						IExpr flattened = flattenOneIdentity(mappedAST, F.C0);// EvalAttributes.flatten(mappedAST).orElse(mappedAST);
						return addExpanded(flattened);
					}
					if (evaled) {
						return addExpanded(binaryFlatTimes(temp[0], powerAST));
						// return addExpanded(TimesOp.times(temp[0], powerAST));
					}
					addExpanded(ast);
					return F.NIL;
				} else if (ast.isPlus()) {
					return expandPlus(ast);
				}
				addExpanded(ast);
				return F.NIL;
			}

			private IExpr addExpanded(@Nonnull IExpr expr) {
				if (expandNegativePowers && !distributePlus && expr.isAST()) {
					((IAST) expr).addEvalFlags(IAST.IS_EXPANDED);
				}
				return expr;
			}

			/**
			 * 
			 * @param ast
			 * @return <code>F.NIL</code> if no evaluation is possible
			 */
			public IExpr expandPlus(final IAST ast) {
				IASTAppendable result = F.NIL;
				for (int i = 1; i < ast.size(); i++) {
					final IExpr arg = ast.get(i);
					if (arg.isAST()) {
						IExpr temp = expand((IAST) arg, pattern, expandNegativePowers, false, true);
						if (temp.isPresent()) {
							if (!result.isPresent()) {
								result = ast.copyUntil(ast.size(), i);
							}
							result.append(temp);
							continue;
						}
					}
					result.ifAppendable(r -> r.append(arg));
				}
				if (result.isPresent()) {
					// return result;
					return flattenOneIdentity(result, F.C0);
					// return PlusOp.plus(result);
				}
				addExpanded(ast);
				return F.NIL;
			}

			private static IExpr flattenOneIdentity(IAST result, IExpr defaultValue) {
				return EvalAttributes.flattenDeep(result).orElse(result).oneIdentity(defaultValue);
			}

			/**
			 * Expand <code>(a+b)^i</code> with <code>i</code> an integer number in the range Integer.MIN_VALUE to
			 * Integer.MAX_VALUE.
			 * 
			 * @param powerAST
			 * @return
			 */
			public IExpr expandPowerNull(final IAST powerAST) {
				IExpr base = powerAST.arg1();
				if ((base.isPlus())) {
					IExpr exponent = powerAST.arg2();
					if (exponent.isFraction()) {
						IFraction fraction = (IFraction) exponent;
						if (fraction.isPositive()) {
							INumber floorPart = fraction.floorFraction().normalize();
							if (!floorPart.isZero()) {
								IFraction fractionalPart = fraction.fractionalPart();
								return expandAST(F.Times(F.Power(base, fractionalPart), F.Power(base, floorPart)),
										true);
							}
						}
					}

					int exp = exponent.toIntDefault(Integer.MIN_VALUE);
					if (exp == Integer.MIN_VALUE) {
						addExpanded(powerAST);
						return F.NIL;
					}
					IAST plusAST = (IAST) base;
					if (exp < 0) {
						if (expandNegativePowers) {
							exp *= (-1);
							return F.Power(expandPower(plusAST, exp), F.CN1);
						}
						addExpanded(powerAST);
						return F.NIL;
					}
					return expandPower(plusAST, exp);

				}
				addExpanded(powerAST);
				return F.NIL;
			}

			/**
			 * Expand a polynomial power with the multinomial theorem. See
			 * <a href= "http://en.wikipedia.org/wiki/Multinomial_theorem">Wikipedia - Multinomial theorem</a>
			 * 
			 * @param plusAST
			 * @param n
			 *            <code>n &ge; 0</code>
			 * @return
			 */
			private IExpr expandPower(final IAST plusAST, final int n) {
				if (n == 1) {
					IExpr temp = expandPlus(plusAST);
					if (temp.isPresent()) {
						return temp;
					}
					addExpanded(plusAST);
					return plusAST;
				}
				if (n == 0) {
					return F.C1;
				}

				int k = plusAST.argSize();
				long numberOfTerms = LongMath.binomial(n + k - 1, n);
				if (numberOfTerms > Integer.MAX_VALUE) {
					throw new ArithmeticException("");
				}
				final IASTAppendable expandedResult = F.ast(F.Plus, (int) numberOfTerms, false);
				Expand.NumberPartititon part = new Expand.NumberPartititon(plusAST, n, expandedResult);
				part.partition();
				return flattenOneIdentity(expandedResult, F.C0);
				// return PlusOp.plus(expandedResult);
			}

			private IExpr expandTimes(final IAST timesAST) {
				IExpr result = timesAST.arg1();

				IExpr temp;
				boolean evaled = false;
				if (result.isPower()) {
					temp = expandPowerNull((IAST) result);
					if (temp.isPresent()) {
						result = temp;
						evaled = true;
					}

				} else if (result.isPlus()) {
					temp = expandPlus((IAST) result);
					if (temp.isPresent()) {
						result = temp;
						evaled = true;
					}

				}

				for (int i = 2; i < timesAST.size(); i++) {
					temp = timesAST.get(i);
					if (temp.isPower()) {
						temp = expandPowerNull((IAST) temp);
						if (!temp.isPresent()) {
							temp = timesAST.get(i);
						} else {
							evaled = true;
						}
					} else if (temp.isPlus()) {
						temp = expandPlus((IAST) temp);
						if (!temp.isPresent()) {
							temp = timesAST.get(i);
						} else {
							evaled = true;
						}
					}
					result = expandTimesBinary(result, temp);
				}
				if (evaled == false && timesAST.equals(result)) {
					addExpanded(timesAST);
					return F.NIL;
				}
				return result;
			}

			private IExpr expandTimesBinary(final IExpr arg1, final IExpr arg2) {
				if (arg1.isPlus()) {
					if (!arg2.isPlus()) {
						return expandExprTimesPlus(arg2, (IAST) arg1);
					}
					// assure Plus(...)
					final IAST ast1 = arg2.isPlus() ? (IAST) arg2 : F.Plus(arg2);
					return expandPlusTimesPlus((IAST) arg1, ast1);
				}
				if (arg2.isPlus()) {
					return expandExprTimesPlus(arg1, (IAST) arg2);
				}
				if (arg1.equals(arg2)) {
					return F.Power(arg1, F.C2);
				}
				return binaryFlatTimes(arg1, arg2);
			}

			/**
			 * <code>(a+b)*(c+d) -> a*c+a*d+b*c+b*d</code>
			 * 
			 * @param plusAST0
			 * @param plusAST1
			 * @return
			 */
			private IExpr expandPlusTimesPlus(final IAST plusAST0, final IAST plusAST1) {
				long numberOfTerms = (long) (plusAST0.argSize()) * (long) (plusAST1.argSize());
				if (numberOfTerms > Integer.MAX_VALUE) {
					throw new ArithmeticException("");
				}
				final IASTAppendable result = F.ast(F.Plus, (int) numberOfTerms, false);
				plusAST0.forEach(x -> {
					plusAST1.forEach(y -> {
						// evaluate to flatten out Times() exprs
						evalAndExpandAST(x, y, result);
					});
				});
				return EvalEngine.get().evaluate(result);
				// return flattenOneIdentity(result, F.C0);
			}

			/**
			 * <code>expr*(a+b+c) -> expr*a+expr*b+expr*c</code>
			 * 
			 * @param expr1
			 * @param plusAST
			 * @return
			 */
			private IExpr expandExprTimesPlus(final IExpr expr1, final IAST plusAST) {
				final IASTAppendable result = F.ast(F.Plus, plusAST.argSize(), false);
				plusAST.forEach(x -> {
					// evaluate to flatten out Times() exprs
					evalAndExpandAST(expr1, x, result);
				});
				return EvalEngine.get().evaluate(result);
				// return flattenOneIdentity(result, F.C0);
			}

			/**
			 * Evaluate <code>expr1 * expr2</code> and expand the resulting expression, if it's an <code>IAST</code>.
			 * After that add the resulting expression to the <code>PlusOp</code>
			 * 
			 * @param expr1
			 * @param expr2
			 * @param result
			 */
			public void evalAndExpandAST(IExpr expr1, IExpr expr2, final IASTAppendable result) {
				IASTAppendable timesAST = binaryFlatTimes(expr1, expr2);
				appendPlus(result, timesAST); // expandAST(timesAST, true).orElse(timesAST));
			}

		}

		private static class NumberPartititon {
			IASTAppendable expandedResult;
			int m;
			int n;
			int[] parts;
			IAST precalculatedPowerASTs;

			public NumberPartititon(IAST plusAST, int n, IASTAppendable expandedResult) {

				this.expandedResult = expandedResult;
				this.n = n;
				this.m = plusAST.argSize();
				this.parts = new int[m];
				// precalculate all Power[] ASTs:
				IASTAppendable temp = F.ListAlloc(plusAST.size());
				temp.appendArgs(plusAST);
				this.precalculatedPowerASTs = temp;
			}

			private void addFactor(int[] j) {
				final Permutations.KPermutationsIterable perm = new Permutations.KPermutationsIterable(j, m, m);
				IInteger multinomial = NumberTheory.multinomial(j, n);
				IExpr temp;
				for (int[] indices : perm) {
					final IASTAppendable timesAST = F.TimesAlloc(m + 1);
					if (!multinomial.isOne()) {
						timesAST.append(multinomial);
					}
					for (int k = 0; k < m; k++) {
						if (indices[k] != 0) {
							temp = precalculatedPowerASTs.get(k + 1);
							if (indices[k] == 1) {
								timesAST.append(temp);
							} else {
								if (temp.isTimes()) {
									IAST ast = (IAST) temp;
									final int ki = k;
									timesAST.appendArgs(ast.size(), i -> F.Power(ast.get(i), F.ZZ(indices[ki])));
								} else {
									timesAST.append(F.Power(temp, F.ZZ(indices[k])));
								}
							}

						}
					}
					timesAST.setEvalFlags(IAST.IS_SORTED);
					expandedResult.append(timesAST.oneIdentity0());
				}
			}

			public void partition() {
				partition(n, n, 0);
			}

			private void partition(int n, int max, int currentIndex) {
				if (n == 0) {
					addFactor(parts);
					return;
				}
				if (currentIndex >= m) {
					return;
				}
				int old;
				old = parts[currentIndex];
				int min = Math.min(max, n);

				for (int i = min; i >= 1; i--) {
					parts[currentIndex] = i;
					partition(n - i, i, currentIndex + 1);
				}
				parts[currentIndex] = old;
			}
		}

		private static IASTAppendable binaryFlatTimes(IExpr expr1, IExpr expr2) {
			int size = expr1.isTimes() ? expr1.size() : 1;
			size += expr2.isTimes() ? expr2.size() : 1;
			IASTAppendable timesAST = F.TimesAlloc(size);
			if (expr1.isTimes()) {
				timesAST.appendAll((IAST) expr1, 1, expr1.size());
			} else {
				timesAST.append(expr1);
			}
			if (expr2.isTimes()) {
				timesAST.appendAll((IAST) expr2, 1, expr2.size());
			} else {
				timesAST.append(expr2);
			}
			return timesAST;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				if (arg1.isList()) {
					return arg1.mapThread(F.ListAlloc(arg1.size()), ast, 1);
				}
				IExpr patt = null;
				if (ast.size() > 2) {
					patt = ast.arg2();
				}
				return expand(arg1, patt, false, true, true).orElse(arg1);
			}

			return ast.arg1();
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	/**
	 * <h2>ExpandAll</h2>
	 * 
	 * <pre>
	 * <code>ExpandAll(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * expands out all positive integer powers and products of sums in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; ExpandAll((a + b) ^ 2 / (c + d)^2)
	 * (a^2+2*a*b+b^2)/(c^2+2*c*d+d^2)
	 * </code>
	 * </pre>
	 * <p>
	 * <code>ExpandAll</code> descends into sub expressions
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; ExpandAll((a + Sin(x*(1 + y)))^2)
	 * a^2+Sin(x+x*y)^2+2*a*Sin(x+x*y) 
	 * </code>
	 * </pre>
	 * <p>
	 * <code>ExpandAll</code> also expands heads
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; ExpandAll(((1 + x)(1 + y))[x])
	 * (1+x+y+x*y)[x]
	 * </code>
	 * </pre>
	 */
	private static class ExpandAll extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			IExpr patt = null;
			if (ast.size() > 2) {
				patt = ast.arg2();
			}
			if (arg1.isAST()) {
				return expandAll((IAST) arg1, patt, true, true, engine).orElse(arg1);
			}
			return arg1;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		private static IExpr setAllExpanded(IExpr expr, boolean expandNegativePowers, boolean distributePlus) {
			if (expr != null && expandNegativePowers && !distributePlus && expr.isAST()) {
				((IAST) expr).addEvalFlags(IAST.IS_ALL_EXPANDED);
			}
			return expr;
		}

	}

	/**
	 * <h2>Factor</h2>
	 * 
	 * <pre>
	 * <code>Factor(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * factors the polynomial expression <code>expr</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Factor(1+2*x+x^2, x)
	 * (1+x)^2
	 * </code>
	 * </pre>
	 * 
	 * <pre>
	 * <code>``` 
	 * &gt;&gt; Factor(x^4-1, GaussianIntegers-&gt;True)
	 * (x-1)*(x+1)*(x-I)*(x+I)
	 * </code>
	 * </pre>
	 */
	private static class Factor extends AbstractFunctionEvaluator {

		@Override
		public IAST options() {
			return F.List(F.Rule(F.GaussianIntegers, F.False), F.Rule(F.Modulus, F.C0));
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				return list.mapThread(F.ListAlloc(list.size()), ast, 1);
			}

			IExpr result = F.REMEMBER_AST_CACHE.getIfPresent(ast);
			if (result != null) {
				return result;
			}
			VariablesSet eVar = new VariablesSet(ast.arg1());
			List<IExpr> varList = eVar.getVarList().copyTo();

			IExpr expr = ast.arg1();
			if (ast.isAST1() && !ast.arg1().isTimes() && !ast.arg1().isPower()) {
				expr = F.Together.of(engine, expr);
				if (expr.isAST()) {
					IExpr[] parts = Algebra.getNumeratorDenominator((IAST) expr, engine);
					if (!parts[1].isOne()) {
						try {
							IExpr numerator = factorExpr(F.Factor(parts[0]), parts[0], eVar, false, engine);
							IExpr denomimator = factorExpr(F.Factor(parts[1]), parts[1], eVar, false, engine);
							IExpr temp = F.Divide(numerator, denomimator);
							F.REMEMBER_AST_CACHE.put(ast, temp);
							return temp;
						} catch (JASConversionException e) {
							if (Config.DEBUG) {
								e.printStackTrace();
							}
							return expr;
						}
					}
				}
			}

			try {
				if (ast.isAST2()) {
					IExpr temp = factorWithOption(ast, expr, varList, false, engine);
					if (temp.isPresent()) {
						return temp;
					}
				}
				IExpr temp = factorExpr(ast, expr, eVar, false, engine);
				F.REMEMBER_AST_CACHE.put(ast, temp);
				return temp;
			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
			return expr;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		public IExpr factorExpr(final IAST ast, IExpr expr, VariablesSet eVar, boolean factorSquareFree,
				EvalEngine engine) {
			if (expr.isAST()) {
				IExpr temp;
				// if (expr.isPower()&&expr.base().isPlus()) {
				//// System.out.println(ast.toString());
				// temp = factorExpr(ast, expr.base(), varList);
				// temp = F.Power(temp, expr.exponent());
				// } else
				if (expr.isPower()) {
					IExpr p = factorExpr((IAST) expr, expr.base(), eVar, factorSquareFree, engine);
					if (!p.equals(expr.base())) {
						return F.Power(p, expr.exponent());
					}
					return expr;
				} else if (expr.isTimes()) {
					// System.out.println(ast.toString());
					temp = ((IAST) expr).map(x -> {
						if (x.isPlus()) {
							return factorExpr(ast, x, eVar, factorSquareFree, engine);
						}
						if (x.isPower() && x.base().isPlus()) {
							IExpr p = factorExpr(ast, x.base(), eVar, factorSquareFree, engine);
							if (!p.equals(x.base())) {
								return F.Power(p, x.exponent());
							}
						}
						return F.NIL;
					}, 1);
					return temp;
				} else {
					// System.out.println("leafCount " + expr.leafCount());
					return factor((IAST) expr, eVar, factorSquareFree, engine);
				}
			}
			return expr;
		}

		private static IExpr factor(IAST expr, VariablesSet eVar, boolean factorSquareFree, EvalEngine engine)
				throws JASConversionException {
			if (Config.MAX_FACTOR_LEAFCOUNT > 0 && expr.leafCount() > Config.MAX_FACTOR_LEAFCOUNT) {
				return expr;
			}
			// use TermOrderByName.INVLEX here!
			// See https://github.com/kredel/java-algebra-system/issues/8
			Object[] objects = null;
			JASConvert<BigRational> jas = new JASConvert<BigRational>(eVar.getArrayList(), BigRational.ZERO,
					TermOrderByName.INVLEX);
			try {
				GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
				if (polyRat.length() <= 1) {
					return expr;
				}
				objects = jas.factorTerms(polyRat);
			} catch (JASConversionException e) {
				// return F.NIL;
				return factorWithPolynomialHomogenization(expr, eVar, engine);
			}

			if (objects != null) {

				SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
				try {
					GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
					FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory
							.getImplementation(edu.jas.arith.BigInteger.ONE);
					if (factorSquareFree) {
						map = factorAbstract.squarefreeFactors(poly);// factors(poly);
					} else {
						// System.out.println("Variable: " + varList.toString() + " -- " + expr.fullFormString());
						// System.out.println(poly);
						map = factorAbstract.factors(poly);
					}
				} catch (RuntimeException rex) {
					// System.out.println("Factor failed: " + expr.toString());
					if (Config.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
					return expr;
				}
				IASTAppendable result = F.TimesAlloc(map.size() + 1);
				java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
				java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
				IRational f = F.C1;
				if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
					f = F.fraction(gcd, lcm).normalize();
				}
				for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
					if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
						continue;
					}
					IExpr base = jas.integerPoly2Expr(entry.getKey());
					if (entry.getValue() == 1L) {
						if (f.isMinusOne() && base.isPlus()) {
							base = ((IAST) base).map(x -> x.negate(), 1);
							f = F.C1;
						}
						result.append(base);
					} else {
						result.append(F.Power(base, F.ZZ(entry.getValue())));
					}
				}
				if (!f.isOne()) {
					result.append(f);
				}
				// System.out.println("Factor: " + expr.toString() + " ==> " + result.toString());
				return engine.evaluate(result);
			}
			return F.NIL;
		}

		private static IExpr factorWithPolynomialHomogenization(IAST expr, VariablesSet eVar, EvalEngine engine) {
			boolean gaussianIntegers = !expr.isFree(x -> x.isComplex() || x.isComplexNumeric(), false);

			PolynomialHomogenization substitutions = new PolynomialHomogenization(eVar.getVarList(), engine);
			IExpr subsPolynomial = substitutions.replaceForward(expr);
			if (substitutions.size() == 0) {
				return factorComplex(expr, eVar.getArrayList(), F.Times, gaussianIntegers, engine);
			}
			// System.out.println(subsPolynomial.toString());
			if (subsPolynomial.isAST()) {
				eVar.addAll(substitutions.substitutedVariablesSet());
				IExpr factorization = factorComplex(subsPolynomial, eVar.getArrayList(), F.Times, gaussianIntegers,
						engine);
				// IExpr factorization = factor((IAST) subsPolynomial, eVar, factorSquareFree);
				if (factorization.isPresent()) {
					return substitutions.replaceBackward(factorization);
				}
			}
			return expr;
		}

		/**
		 * Factor the <code>expr</code> with the option given in <code>ast</code>.
		 * 
		 * @param ast
		 * @param expr
		 * @param varList
		 * @param factorSquareFree
		 * @return
		 * @throws JASConversionException
		 */
		public static IExpr factorWithOption(final IAST ast, IExpr expr, List<IExpr> varList, boolean factorSquareFree,
				final EvalEngine engine) throws JASConversionException {
			final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
			IExpr option = options.getOption(F.Modulus);
			if (option.isInteger() && !option.isZero()) {
				return factorModulus(expr, varList, factorSquareFree, option);
			}
			if (!factorSquareFree) {
				option = options.getOption(F.Extension);
				if (option.isImaginaryUnit()) {
					// Exptension->I is like gaussian integers
					return factorComplex(expr, varList, F.Times, false, true, engine);
				}

				option = options.getOption(F.GaussianIntegers);
				if (option.isPresent()) {
					if (option.isTrue()) {
						return factorComplex(expr, varList, F.Times, false, true, engine);
					}
				}
			}
			return F.NIL; // no evaluation
		}

	}

	/**
	 * <h2>FactorSquareFree</h2>
	 * 
	 * <pre>
	 * <code>FactorSquareFree(polynomial)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * factor the polynomial expression <code>polynomial</code> square free.
	 * </p>
	 * </blockquote>
	 */
	private static class FactorSquareFree extends Factor {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			VariablesSet eVar = new VariablesSet(ast.arg1());
			IExpr result = F.REMEMBER_AST_CACHE.getIfPresent(ast);
			if (result != null) {
				return result;
			}
			// if (!eVar.isSize(1)) {
			// throw new WrongArgumentType(ast, ast.arg1(), 1,
			// "Factorization only implemented for univariate polynomials");
			// }
			try {
				IExpr expr = F.evalExpandAll(ast.arg1(), engine);
				// ASTRange r = new ASTRange(eVar.getVarList(), 1);
				// List<IExpr> varList = r;
				List<IExpr> varList = eVar.getVarList().copyTo();

				if (ast.isAST2()) {
					return factorWithOption(ast, expr, varList, true, engine);
				}
				if (expr.isAST()) {
					IExpr temp = factorExpr((IAST) expr, (IAST) expr, eVar, true, engine);
					F.REMEMBER_AST_CACHE.put(ast, temp);
					return temp;
				}
				return expr;

			} catch (JASConversionException jce) {
				// toInt() conversion failed
				if (Config.DEBUG) {
					jce.printStackTrace();
				}
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	/**
	 * <h2>FactorSquareFreeList</h2>
	 * 
	 * <pre>
	 * <code>FactorSquareFreeList(polynomial)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * get the square free factors of the polynomial expression <code>polynomial</code>.
	 * </p>
	 * </blockquote>
	 */
	private static class FactorSquareFreeList extends Factor {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			VariablesSet eVar = new VariablesSet(ast.arg1());
			// if (!eVar.isSize(1)) {
			// throw new WrongArgumentType(ast, ast.arg1(), 1,
			// "Factorization only implemented for univariate polynomials");
			// }
			try {
				IExpr expr = F.evalExpandAll(ast.arg1(), engine);
				// ASTRange r = new ASTRange(eVar.getVarList(), 1);
				// List<IExpr> varList = r;
				List<IExpr> varList = eVar.getVarList().copyTo();

				// if (ast.isAST2()) {
				// return factorWithOption(ast, expr, varList, true);
				// }
				return factorList(expr, varList, true);

			} catch (JASConversionException jce) {
				// toInt() conversion failed
				if (Config.DEBUG) {
					jce.printStackTrace();
				}
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		private static IExpr factorList(IExpr expr, List<IExpr> varList, boolean factorSquareFree)
				throws JASConversionException {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
			Object[] objects = jas.factorTerms(polyRat);
			java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
			java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
			SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
			try {
				GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
				FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory
						.getImplementation(edu.jas.arith.BigInteger.ONE);

				if (factorSquareFree) {
					map = factorAbstract.squarefreeFactors(poly);// factors(poly);
				} else {
					map = factorAbstract.factors(poly);
				}
			} catch (RuntimeException rex) {
				// JAS may throw RuntimeExceptions
				return F.List(expr);
			}
			IASTAppendable result = F.ListAlloc(map.size() + 1);
			if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
				result.append(F.List(F.fraction(gcd, lcm), F.C1));
			}
			for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
				if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
					continue;
				}
				result.append(F.List(jas.integerPoly2Expr(entry.getKey()), F.ZZ(entry.getValue())));
			}
			return result;
		}
	}

	/**
	 * <h2>FactorTerms</h2>
	 * 
	 * <pre>
	 * <code>FactorTerms(poly)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * pulls out any overall numerical factor in <code>poly</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; FactorTerms(3+3/4*x^3+12/17*x^2, x)
	 * 3/68*(17*x^3+16*x^2+68)
	 * </code>
	 * </pre>
	 */
	private static class FactorTerms extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr temp = Structure.threadListLogicEquationOperators(ast.arg1(), ast, 1);
			if (temp.isPresent()) {
				return temp;
			}
			VariablesSet eVar = null;
			IAST variableList = F.NIL;
			if (ast.isAST2()) {
				if (ast.arg2().isSymbol()) {
					ISymbol variable = (ISymbol) ast.arg2();
					variableList = F.List(variable);
				} else if (ast.arg2().isList()) {
					variableList = (IAST) ast.arg2();
				} else {
					return F.NIL;
				}
			} else {
				if (ast.isAST1()) {
					IExpr expr = F.evalExpandAll(ast.arg1(), engine);
					if (expr.isPlus()) {
						temp = factorTermsPlus((IAST) expr, engine);
						if (temp.isPresent()) {
							return temp;
						}
					}
					eVar = new VariablesSet(ast.arg1());
					if (!eVar.isSize(1)) {
						// FactorTerms only possible for univariate polynomials
						if (eVar.isSize(0)) {
							return ast.arg1();
						}
						return F.NIL;
					}
					variableList = eVar.getVarList();
				}
			}
			if (!variableList.isPresent() || variableList.size() != 2) {
				// FactorTerms only possible for univariate polynomials
				return F.NIL;
			}
			List<IExpr> varList = variableList.copyTo();
			IExpr expr = F.evalExpandAll(ast.arg1(), engine);
			try {
				JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
				GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
				Object[] objects = jas.factorTerms(poly);
				java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
				java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
				if (lcm.equals(java.math.BigInteger.ZERO)) {
					// no changes
					return expr;
				}
				GenPolynomial<edu.jas.arith.BigInteger> iPoly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
				IASTAppendable result = F.TimesAlloc(2);
				result.append(F.fraction(gcd, lcm));
				result.append(jas.integerPoly2Expr(iPoly));
				return result;
			} catch (JASConversionException e1) {
				// try {
				// if (variableList.isAST1()) {
				// IAST list = PolynomialFunctions.rootsOfExprPolynomial(expr, variableList, true);
				// if (list.isList()) {
				// IExpr x = variableList.arg1();
				// IASTAppendable result = F.TimesAlloc(list.size());
				// list.forEach(arg -> result.append(F.Plus(x, arg)));
				// // for (int i = 1; i < list.size(); i++) {
				// // result.append(F.Plus(x, list.get(i)));
				// // }
				// return result;
				// }
				// }
				// } catch (ClassCastException e2) {
				// if (Config.SHOW_STACKTRACE) {
				// e2.printStackTrace();
				// }
				// } catch (JASConversionException e2) {
				// if (Config.SHOW_STACKTRACE) {
				// e2.printStackTrace();
				// }
				// }

			}
			return ast.arg1();
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		/**
		 * Factor out a rational number which may be a factor in every sub-expression of <code>plus</code>.
		 * 
		 * @param plus
		 * @param engine
		 * @return <code>F.NIL</code> if the factor couldn't be found
		 */
		private static IExpr factorTermsPlus(IAST plus, EvalEngine engine) {
			IExpr temp;
			IRational gcd1 = null;
			if (plus.arg1().isRational()) {
				gcd1 = (IRational) plus.arg1();
			} else if (plus.arg1().isTimes() && plus.arg1().first().isRational()) {
				gcd1 = (IRational) plus.arg1().first();
			}
			if (gcd1 == null) {
				return F.NIL;
			}
			for (int i = 2; i < plus.size(); i++) {
				IRational gcd2 = null;
				if (plus.get(i).isRational()) {
					gcd2 = (IRational) plus.get(i);
				} else if (plus.get(i).isTimes() && plus.get(i).first().isRational()) {
					gcd2 = (IRational) plus.get(i).first();
				}
				if (gcd2 == null) {
					return F.NIL;
				}
				temp = engine.evaluate(F.GCD(gcd1, gcd2));
				if (temp.isRational() && !temp.isOne()) {
					gcd1 = (IRational) temp;
				} else {
					return F.NIL;
				}
			}
			if (gcd1.isMinusOne()) {
				return F.NIL;
			}
			return engine.evaluate(F.Times(gcd1, F.Expand(F.Times(gcd1.inverse(), plus))));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}
	}

	/**
	 * <pre>
	 * FullSimplify(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * works like <code>Simplify</code> but additionally tries some <code>FunctionExpand</code> rule transformations to
	 * simplify <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * FullSimplify(expr, option1, option2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * full simplifies <code>expr</code> with some additional options set
	 * </p>
	 * </blockquote>
	 * <ul>
	 * <li>Assumptions - use assumptions to simplify the expression</li>
	 * <li>ComplexFunction - use this function to determine the &ldquo;weight&rdquo; of an expression.</li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))
	 * True
	 * </pre>
	 * 
	 * <h3>Related terms</h3>
	 * <p>
	 * <a href="Simplify.md">Simplify</a>
	 * </p>
	 */
	private static class FullSimplify extends Simplify {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return super.evaluate(ast, engine);
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public boolean isFullSimplifyMode() {
			return true;
		}
	}

	/**
	 * <h2>Numerator</h2>
	 * 
	 * <pre>
	 * <code>Numerator(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the numerator in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Numerator(a / b)
	 * a
	 * &gt;&gt; Numerator(2 / 3)
	 * 2
	 * &gt;&gt; Numerator(a + b)
	 * a + b
	 * </code>
	 * </pre>
	 */
	private static class Numerator extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			boolean numericMode = engine.isNumericMode();
			try {
				engine.setNumericMode(false);
				boolean trig = false;
				if (ast.isAST2()) {
					final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine, true);
					if (options.isInvalidPosition()) {
						return IOFunctions.printMessage(F.Numerator, "nonopt",
								F.List(ast.arg2(), F.ZZ(options.getInvalidPosition() - 1), ast), engine);
					}
					IExpr option = options.getOption(F.Trig);
					if (option.isTrue()) {
						trig = true;
					}
				}

				IExpr arg = engine.evaluate(ast.arg1());
				if (arg.isRational()) {
					return ((IRational) arg).numerator();
				}
				IExpr[] parts = fractionalParts(arg, trig);
				if (parts == null) {
					return arg;
				}
				return parts[0];
			} finally {
				engine.setNumericMode(numericMode);
			}
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Trig, F.False));
		}

		/**
		 * Get the &quot;numerator form&quot; of the given function. Example: <code>Csc[x]</code> gives
		 * <code>Sin[x]</code>.
		 * 
		 * @param function
		 *            the function which should be transformed to &quot;denominator form&quot; determine the denominator
		 *            by splitting up functions like <code>Tan[9,Cot[], Csc[],...</code>
		 * @param trig
		 * @return
		 */
		public static IExpr getTrigForm(IAST function, boolean trig) {
			if (trig) {
				if (function.isAST1()) {
					for (int i = 0; i < F.DENOMINATOR_NUMERATOR_SYMBOLS.length; i++) {
						ISymbol sym = F.DENOMINATOR_NUMERATOR_SYMBOLS[i];
						if (function.head().equals(sym)) {
							IExpr result = F.NUMERATOR_TRIG_TRUE_EXPRS[i];
							if (result.isSymbol()) {
								return F.unaryAST1(result, function.arg1());
							}
							return result;
						}
					}
				}
			}
			return F.NIL;
		}
	}

	/**
	 * <h2>PolynomialExtendedGCD</h2>
	 * 
	 * <pre>
	 * <code>PolynomialExtendedGCD(p, q, x)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the extended GCD ('greatest common divisor') of the univariate polynomials <code>p</code> and
	 * <code>q</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>PolynomialExtendedGCD(p, q, x, Modulus -&gt; prime)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the extended GCD ('greatest common divisor') of the univariate polynomials <code>p</code> and
	 * <code>q</code> modulus the <code>prime</code> integer.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href=
	 * "https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm#Polynomial_extended_Euclidean_algorithm">Wikipedia:
	 * Polynomial extended Euclidean algorithm</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; PolynomialExtendedGCD(x^8 + x^4 + x^3 + x + 1, x^6 + x^4 + x + 1, x, Modulus-&gt;2)
	 * {1,{1+x^2+x^3+x^4+x^5,x+x^3+x^6+x^7}}
	 * </code>
	 * </pre>
	 */
	private static class PolynomialExtendedGCD extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr x = ast.arg3();// Validate.checkSymbolType(ast, 3);
			IExpr expr1 = F.evalExpandAll(ast.arg1(), engine);
			IExpr expr2 = F.evalExpandAll(ast.arg2(), engine);
			VariablesSet eVar = new VariablesSet();
			eVar.add(x);

			// ASTRange r = new ASTRange(eVar.getVarList(), 1);
			if (ast.size() == 5) {
				// List<IExpr> varList = r;
				List<IExpr> varList = eVar.getVarList().copyTo();
				final OptionArgs options = new OptionArgs(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption(F.Modulus);
				if (option.isInteger() && !option.isZero()) {
					try {
						// found "Modulus" option => use ModIntegerRing
						ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
						JASModInteger jas = new JASModInteger(varList, modIntegerRing);
						GenPolynomial<ModLong> poly1 = jas.expr2JAS(expr1);
						GenPolynomial<ModLong> poly2 = jas.expr2JAS(expr2);
						GenPolynomial<ModLong>[] result = poly1.egcd(poly2);
						IASTAppendable list = F.ListAlloc(2);
						list.append(jas.modLongPoly2Expr(result[0]));
						IASTAppendable subList = F.ListAlloc(2);
						subList.append(jas.modLongPoly2Expr(result[1]));
						subList.append(jas.modLongPoly2Expr(result[2]));
						list.append(subList);
						return list;
					} catch (JASConversionException e) {
						if (Config.DEBUG) {
							e.printStackTrace();
						}
					}
					return F.NIL;
				}
			}

			try {
				List<IExpr> varList = eVar.getVarList().copyTo();
				JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
				GenPolynomial<BigRational> poly1 = jas.expr2JAS(expr1, false);
				GenPolynomial<BigRational> poly2 = jas.expr2JAS(expr2, false);
				GenPolynomial<BigRational>[] result = poly1.egcd(poly2);
				IASTAppendable list = F.ListAlloc(2);
				list.append(jas.rationalPoly2Expr(result[0], true));
				IASTAppendable subList = F.ListAlloc(2);
				subList.append(jas.rationalPoly2Expr(result[1], true));
				subList.append(jas.rationalPoly2Expr(result[2], true));
				list.append(subList);
				return list;
			} catch (JASConversionException e0) {
				try {
					ExprPolynomialRing ring = new ExprPolynomialRing(eVar.getVarList());
					ExprPolynomial poly1 = ring.create(expr1);
					ExprPolynomial poly2 = ring.create(expr2);
					ExprPolynomial[] result = poly1.egcd(poly2);
					if (result != null) {
						IASTAppendable list = F.ListAlloc(2);
						list.append(result[0].getExpr());
						IASTAppendable subList = F.ListAlloc(2);
						subList.append(F.Together.of(engine, result[1].getExpr()));
						subList.append(F.Together.of(engine, result[2].getExpr()));
						list.append(subList);
						return list;
					}
					return F.NIL;
				} catch (RuntimeException rex) {
					if (Config.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
				}
				if (!expr1.isPolynomial(eVar.getVarList())) {
					if (!expr2.isPolynomial(eVar.getVarList())) {
						IASTAppendable list = F.ListAlloc(2);
						list.append(expr2);
						IASTAppendable subList = F.ListAlloc(2);
						subList.append(F.C0);
						subList.append(F.C1);
						list.append(subList);
						return list;
					}
					if (expr2.isFree(eVar.getVarList())) {
						IASTAppendable list = F.ListAlloc(2);
						list.append(F.C1);
						IASTAppendable subList = F.ListAlloc(2);
						subList.append(F.C0);
						subList.append(F.Power(expr2, F.CN1));
						list.append(subList);
						return list;
					}
				}
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_3_4;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Modulus, F.C0));
		}
	}

	/**
	 * <h2>PolynomialGCD</h2>
	 * 
	 * <pre>
	 * <code>PolynomialGCD(p, q)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the GCD ('greatest common divisor') of the polynomials <code>p</code> and <code>q</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>PolynomialGCD(p, q, Modulus -&gt; prime)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the GCD ('greatest common divisor') of the polynomials <code>p</code> and <code>q</code> modulus the
	 * <code>prime</code> integer.
	 * </p>
	 * </blockquote>
	 */
	private static class PolynomialGCD extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			if (ast.isAST0()) {
				return F.NIL;
			}
			if (ast.isAST1()) {
				IExpr arg1 = ast.arg1();
				if (arg1.isNegativeResult()) {
					return arg1.negate();
				}
				return arg1;
			}
			VariablesSet eVar = new VariablesSet();
			eVar.addVarList(ast, 1);

			IExpr expr = F.evalExpandAll(ast.arg1(), engine);
			if (ast.size() > 3 && ast.last().isRuleAST()) {
				return gcdWithOption(ast, expr, eVar, engine);
			}
			try {
				// ASTRange r = new ASTRange(eVar.getVarList(), 1);
				List<IExpr> varList = eVar.getVarList().copyTo();
				JASConvert<BigInteger> jas = new JASConvert<BigInteger>(varList, BigInteger.ZERO);
				GenPolynomial<BigInteger> poly = jas.expr2JAS(expr, false);
				GenPolynomial<BigInteger> temp;
				GreatestCommonDivisorAbstract<BigInteger> factory = GCDFactory.getImplementation(BigInteger.ZERO);
				for (int i = 2; i < ast.size(); i++) {
					expr = F.evalExpandAll(ast.get(i), engine);
					temp = jas.expr2JAS(expr, false);
					poly = factory.gcd(poly, temp);
				}
				return jas.integerPoly2Expr(poly.monic());
			} catch (JASConversionException e) {
				try {
					if (eVar.size() == 0) {
						return F.NIL;
					}
					IAST vars = eVar.getVarList();
					expr = F.evalExpandAll(ast.arg1(), engine);

					ExprPolynomialRing ring = new ExprPolynomialRing(vars);
					ExprPolynomial p1 = ring.create(expr);
					ExprPolynomial p2;
					for (int i = 2; i < ast.size(); i++) {
						expr = F.evalExpandAll(ast.get(i), engine);
						p2 = ring.create(expr);
						p1 = p1.gcd(p2);
					}
					return p1.getExpr();

					// ExprPolynomialRing ring = new ExprPolynomialRing(vars);
					// ExprPolynomial pol1 = ring.create(expr);
					// ExprPolynomial pol2;
					// // ASTRange r = new ASTRange(eVar.getVarList(), 1);
					// List<IExpr> varList = eVar.getVarList().copyTo();
					// JASIExpr jas = new JASIExpr(varList, true);
					// GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
					// GenPolynomial<IExpr> p2;
					//
					// GreatestCommonDivisor<IExpr> factory = GCDFactory.getImplementation(ExprRingFactory.CONST);
					// for (int i = 2; i < ast.size(); i++) {
					// expr = F.evalExpandAll(ast.get(i), engine);
					// p2 = jas.expr2IExprJAS(expr);
					// p1 = factory.gcd(p1, p2);
					// }
					// return jas.exprPoly2Expr(p1);
				} catch (RuntimeException rex) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			IAST list = ast.setAtCopy(0, F.List);
			IExpr[] result = InternalFindCommonFactorPlus.findCommonFactors(list, false);
			if (result != null) {
				return result[0];
			}
			return F.C1;
		}

		private IExpr gcdWithOption(final IAST ast, IExpr expr, VariablesSet eVar, final EvalEngine engine) {
			final OptionArgs options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
			IExpr option = options.getOption(F.Modulus);
			if (option.isInteger() && !option.isZero()) {
				return modulusGCD(ast, expr, eVar, option);
			}
			return F.NIL;
		}

		private IExpr modulusGCD(final IAST ast, IExpr expr, VariablesSet eVar, IExpr option) {
			try {
				// found "Modulus" option => use ModIntegerRing
				// ASTRange r = new ASTRange(eVar.getVarList(), 1);
				// ModIntegerRing modIntegerRing =
				// JASConvert.option2ModIntegerRing((ISignedNumber) option);
				// JASConvert<ModInteger> jas = new
				// JASConvert<ModInteger>(r.toList(), modIntegerRing);
				ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
				JASModInteger jas = new JASModInteger(eVar.getArrayList(), modIntegerRing);
				GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
				GenPolynomial<ModLong> temp;
				GreatestCommonDivisorAbstract<ModLong> factory = GCDFactory.getImplementation(modIntegerRing);

				for (int i = 2; i < ast.argSize(); i++) {
					eVar = new VariablesSet(ast.get(i));
					if (!eVar.isSize(1)) {
						// gcd only possible for univariate polynomials
						return F.NIL;
					}
					expr = F.evalExpandAll(ast.get(i));
					temp = jas.expr2JAS(expr);
					poly = factory.gcd(poly, temp);
				}
				return Algebra.factorModulus(jas, modIntegerRing, poly, false);
			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// newSymbol.setAttributes(ISymbol.HOLDALL);
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Modulus, F.C0));
		}
	}

	/**
	 * <h2>PolynomialLCM</h2>
	 * 
	 * <pre>
	 * <code>PolynomialLCM(p, q)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the LCM ('least common multiple') of the polynomials <code>p</code> and <code>q</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>PolynomialLCM(p, q, Modulus -&gt; prime)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the LCM ('least common multiple') of the polynomials <code>p</code> and <code>q</code> modulus the
	 * <code>prime</code> integer.
	 * </p>
	 * </blockquote>
	 */
	private static class PolynomialLCM extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.NIL;
			}
			if (ast.isAST1()) {
				IExpr arg1 = ast.arg1();
				if (arg1.isNegativeResult()) {
					return arg1.negate();
				}
				return arg1;
			}

			VariablesSet eVar = new VariablesSet();
			eVar.addVarList(ast, 1);

			// ASTRange r = new ASTRange(eVar.getVarList(), 1);
			IExpr expr = F.evalExpandAll(ast.arg1(), engine);
			if (ast.size() > 3) {
				final OptionArgs options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
				IExpr option = options.getOption(F.Modulus);
				if (option.isInteger() && !option.isZero()) {
					try {
						// found "Modulus" option => use ModIntegerRing
						List<IExpr> varList = eVar.getVarList().copyTo();
						ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
						JASModInteger jas = new JASModInteger(varList, modIntegerRing);
						GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
						GenPolynomial<ModLong> temp;
						GreatestCommonDivisorAbstract<ModLong> factory = GCDFactory.getImplementation(modIntegerRing);
						for (int i = 2; i < ast.argSize(); i++) {
							expr = F.evalExpandAll(ast.get(i), engine);
							temp = jas.expr2JAS(expr);
							poly = factory.lcm(poly, temp);
						}

						return Algebra.factorModulus(jas, modIntegerRing, poly.monic(), false);
						// return jas.modLongPoly2Expr(poly.monic());
					} catch (JASConversionException e) {
						try {
							if (eVar.size() == 0) {
								return F.NIL;
							}
							expr = F.evalExpandAll(ast.arg1(), engine);
							IAST vars = eVar.getVarList();
							ExprPolynomialRing ring = new ExprPolynomialRing(vars);
							ExprPolynomial pol1 = ring.create(expr);
							// ASTRange r = new ASTRange(eVar.getVarList(), 1);
							List<IExpr> varList = eVar.getVarList().copyTo();
							JASIExpr jas = new JASIExpr(varList, true);
							GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
							GenPolynomial<IExpr> p2;

							GreatestCommonDivisor<IExpr> factory = GCDFactory.getImplementation(ExprRingFactory.CONST);
							for (int i = 2; i < ast.size(); i++) {
								expr = F.evalExpandAll(ast.get(i), engine);
								p2 = jas.expr2IExprJAS(expr);
								p1 = factory.lcm(p1, p2);
							}
							return jas.exprPoly2Expr(p1);
						} catch (RuntimeException rex) {
							if (Config.DEBUG) {
								e.printStackTrace();
							}
						}
					}
					return F.NIL;
				}
			}
			try {
				List<IExpr> varList = eVar.getVarList().copyTo();
				JASConvert<BigInteger> jas = new JASConvert<BigInteger>(varList, BigInteger.ZERO);
				GenPolynomial<BigInteger> poly = jas.expr2JAS(expr, false);
				GenPolynomial<BigInteger> gcd;
				boolean evaled = false;
				GenPolynomial<BigInteger> temp;
				GreatestCommonDivisorAbstract<BigInteger> factory = GCDFactory.getImplementation(BigInteger.ZERO);
				for (int i = 2; i < ast.size(); i++) {
					expr = F.evalExpandAll(ast.get(i), engine);
					temp = jas.expr2JAS(expr, false);
					if (!evaled) {
						gcd = factory.gcd(poly, temp);
						if (!gcd.isONE()) {
							evaled = true;
						}
					}
					poly = factory.lcm(poly, temp);
				}
				if (evaled) {
					return jas.integerPoly2Expr(poly.monic());
				}
			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
				IAST list = ast.setAtCopy(0, F.List);
				IExpr[] result = InternalFindCommonFactorPlus.findCommonFactors(list, true);
				if (result != null) {
					return F.Times(result[0], ((IAST) result[1]).setAtCopy(0, F.Times));
				}
			}
			return ast.setAtCopy(0, F.Times);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// newSymbol.setAttributes(ISymbol.HOLDALL);
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Modulus, F.C0));
		}
	}

	/**
	 * <h2>PolynomialQ</h2>
	 * 
	 * <pre>
	 * <code>PolynomialQ(p, x)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return <code>True</code> if <code>p</code> is a polynomial for the variable <code>x</code>. Return
	 * <code>False</code> in all other cases.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>PolynomialQ(p, {x, y, ...})
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return <code>True</code> if <code>p</code> is a polynomial for the variables <code>x, y, ...</code> defined in
	 * the list. Return <code>False</code> in all other cases.
	 * </p>
	 * </blockquote>
	 */
	private static class PolynomialQ extends AbstractCoreFunctionEvaluator implements BiPredicate<IExpr, IExpr> {

		/**
		 * Returns <code>True</code> if the given expression is a polynomial object; <code>False</code> otherwise
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST2()) {
				IExpr cached = F.REMEMBER_AST_CACHE.getIfPresent(ast);
				if (cached != null) {
					return cached;
				}
				IExpr arg1 = engine.evaluate(ast.arg1());
				IExpr arg2 = engine.evaluate(ast.arg2());

				IAST variablesList = F.NIL;
				if (arg2.isList()) {
					variablesList = (IAST) arg2;
				} else {
					variablesList = F.List(arg2);
				}
				IAST subst = substituteVariablesInPolynomial(arg1, variablesList, "§PolynomialQ");
				IExpr result = F.bool(subst.arg1().isPolynomial((IAST) subst.arg2()));
				F.REMEMBER_AST_CACHE.put(ast, result);
				return result;
			}
			if (ast.isAST1()) {
				return F.True;
			}
			return F.False;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public boolean test(final IExpr firstArg, final IExpr secondArg) {
			return firstArg.isPolynomial(secondArg.orNewList());
		}
	}

	/**
	 * <h2>PolynomialQuotient</h2>
	 * 
	 * <pre>
	 * <code>PolynomialQuotient(p, q, x)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the polynomial quotient of the polynomials <code>p</code> and <code>q</code> for the variable
	 * <code>x</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>PolynomialQuotient(p, q, x, Modulus -&gt; prime)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the polynomial quotient of the polynomials <code>p</code> and <code>q</code> for the variable
	 * <code>x</code> modulus the <code>prime</code> integer.
	 * </p>
	 * </blockquote>
	 */
	private static class PolynomialQuotient extends PolynomialQuotientRemainder {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			if (ast.size() == 4 || ast.size() == 5) {
				IExpr variable = ast.arg3();
				IExpr arg1 = F.evalExpandAll(ast.arg1(), engine);
				IExpr arg2 = F.evalExpandAll(ast.arg2(), engine);

				if (ast.size() == 5) {
					final OptionArgs options = new OptionArgs(ast.topHead(), ast, 4, engine);
					IExpr option = options.getOption(F.Modulus);
					if (option.isInteger() && !option.isZero()) {
						IExpr[] result = quotientRemainderModInteger(arg1, arg2, variable, option);
						if (result == null) {
							return F.NIL;
						}
						return result[0];
					}
					return F.NIL;
				}
				IExpr[] result = quotientRemainder(arg1, arg2, variable);
				if (result == null) {
					return F.NIL;
				}
				return result[0];
			}
			return F.NIL;
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Modulus, F.C0));
		}

	}

	/**
	 * <h2>PolynomialQuotientRemainder</h2>
	 * 
	 * <pre>
	 * <code>PolynomialQuotientRemainder(p, q, x)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns a list with the polynomial quotient and remainder of the polynomials <code>p</code> and <code>q</code>
	 * for the variable <code>x</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>PolynomialQuotientRemainder(p, q, x, Modulus -&gt; prime)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns list with the polynomial quotient and remainder of the polynomials <code>p</code> and <code>q</code> for
	 * the variable <code>x</code> modulus the <code>prime</code> integer.
	 * </p>
	 * </blockquote>
	 */
	private static class PolynomialQuotientRemainder extends AbstractFunctionEvaluator {

		public static IExpr[] quotientRemainder(final IExpr arg1, IExpr arg2, IExpr variable) {
			if (arg1.isFree(variable) && //
					arg2.isFree(variable)) {
				return new IExpr[] { //
						F.Divide(arg1, arg2), //
						F.C0 };
			}
			try {
				JASConvert<BigRational> jas = new JASConvert<BigRational>(variable, BigRational.ZERO);
				GenPolynomial<BigRational> poly1 = jas.expr2JAS(arg1, false);
				GenPolynomial<BigRational> poly2 = jas.expr2JAS(arg2, false);
				GenPolynomial<BigRational>[] divRem = poly1.quotientRemainder(poly2);
				return new IExpr[] { //
						jas.rationalPoly2Expr(divRem[0], false), //
						jas.rationalPoly2Expr(divRem[1], false) };
			} catch (JASConversionException e1) {
				try {
					ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
					ExprPolynomial poly1 = ring.create(arg1);
					ExprPolynomial poly2 = ring.create(arg2);
					ExprPolynomial[] divRem = poly1.quotientRemainder(poly2);
					if (divRem == null) {
						return null;
					}
					return new IExpr[] { //
							divRem[0].getExpr(), //
							divRem[1].getExpr() };
				} catch (LimitException le) {
					throw le;
				} catch (RuntimeException rex) {
					if (Config.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr temp = F.REMEMBER_AST_CACHE.getIfPresent(ast);
			if (temp != null) {
				return temp;
			}
			IExpr variable = ast.arg3();
			IExpr arg1 = F.evalExpandAll(ast.arg1(), engine);
			IExpr arg2 = F.evalExpandAll(ast.arg2(), engine);

			IExpr result = F.NIL;
			if (ast.size() == 5) {
				final OptionArgs options = new OptionArgs(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption(F.Modulus);
				if (option.isInteger() && !option.isZero()) {
					IExpr[] quotientRemainderModInteger = quotientRemainderModInteger(arg1, arg2, variable, option);
					if (quotientRemainderModInteger != null) {
						result = F.List(quotientRemainderModInteger[0], quotientRemainderModInteger[1]);
					}
				}
				F.REMEMBER_AST_CACHE.put(ast, result);
				return result;
			}
			IExpr[] quotientRemainder = quotientRemainder(arg1, arg2, variable);
			if (quotientRemainder != null) {
				result = F.List(quotientRemainder[0], quotientRemainder[1]);
			}
			F.REMEMBER_AST_CACHE.put(ast, result);
			return result;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_3_4;
		}

		public IExpr[] quotientRemainderModInteger(IExpr arg1, IExpr arg2, IExpr variable, IExpr option) {
			try {
				// found "Modulus" option => use ModIntegerRing
				ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
				JASModInteger jas = new JASModInteger(variable, modIntegerRing);
				GenPolynomial<ModLong> poly1 = jas.expr2JAS(arg1);
				GenPolynomial<ModLong> poly2 = jas.expr2JAS(arg2);
				GenPolynomial<ModLong>[] divRem = poly1.quotientRemainder(poly2);
				IExpr[] result = new IExpr[2];
				result[0] = jas.modLongPoly2Expr(divRem[0]);
				result[1] = jas.modLongPoly2Expr(divRem[1]);
				return result;
			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Modulus, F.C0));
		}
	}

	/**
	 * <h2>PolynomialQuotient</h2>
	 * 
	 * <pre>
	 * <code>PolynomialQuotient(p, q, x)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the polynomial remainder of the polynomials <code>p</code> and <code>q</code> for the variable
	 * <code>x</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>PolynomialQuotient(p, q, x, Modulus -&gt; prime)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the polynomial remainder of the polynomials <code>p</code> and <code>q</code> for the variable
	 * <code>x</code> modulus the <code>prime</code> integer.
	 * </p>
	 * </blockquote>
	 */
	private static class PolynomialRemainder extends PolynomialQuotientRemainder {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr variable = ast.arg3();
			IExpr arg1 = F.evalExpandAll(ast.arg1(), engine);
			IExpr arg2 = F.evalExpandAll(ast.arg2(), engine);

			if (ast.size() == 5) {
				final OptionArgs options = new OptionArgs(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption(F.Modulus);
				if (option.isInteger() && !option.isZero()) {
					IExpr[] result = quotientRemainderModInteger(arg1, arg2, variable, option);
					if (result == null) {
						return F.NIL;
					}
					return result[1];
				}
				return F.NIL;
			}
			IExpr[] result = quotientRemainder(arg1, arg2, variable);
			if (result == null) {
				return F.NIL;
			}
			return result[1];
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_3_4;
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Modulus, F.C0));
		}
	}

	/**
	 * <h2>PowerExpand</h2>
	 * 
	 * <pre>
	 * <code>PowerExpand(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * expands out powers of the form <code>(x^y)^z</code> and <code>(x*y)^z</code> in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; PowerExpand((a ^ b) ^ c)
	 * a^(b*c)
	 * 
	 * &gt;&gt; PowerExpand((a * b) ^ c)
	 * a^c*b^c
	 * </code>
	 * </pre>
	 * <p>
	 * <code>PowerExpand</code> is not correct without certain assumptions:
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; PowerExpand((x ^ 2) ^ (1/2))
	 * x
	 * </code>
	 * </pre>
	 */
	private static class PowerExpand extends AbstractFunctionEvaluator {

		private static class PowerExpandVisitor extends VisitorExpr {
			final boolean assumptions;

			public PowerExpandVisitor(boolean assumptions) {
				super();
				this.assumptions = assumptions;
			}

			/** {@inheritDoc} */
			@Override
			public IExpr visit2(IExpr head, IExpr arg1) {
				boolean evaled = false;
				IExpr x1 = arg1;
				IExpr result = arg1.accept(this);
				if (result.isPresent()) {
					evaled = true;
					x1 = result;
				}
				if (head.equals(Log)) {
					if (x1.isPower()) {
						// Log[x_ ^ y_] :> y * Log(x)
						IAST logResult = Times(x1.exponent(), powerExpand(Log(x1.base()), assumptions));
						if (assumptions) {
							IAST floorResult = Floor(Divide(Subtract(Pi, Im(logResult)), Times(C2, Pi)));
							IAST timesResult = Times(C2, I, Pi, floorResult);
							return Plus(logResult, timesResult);
						}
						return logResult;
					}
					if (x1.isTimes()) {
						IAST timesAST = (IAST) x1;
						// Log[x_ * y_ * z_] :> Log(x)+Log(y)+Log(z)
						IAST logResult = timesAST.setAtCopy(0, F.Plus);
						logResult = logResult.mapThread(F.Log(F.Null), 1);
						return powerExpand(logResult, assumptions);
					}
				}
				if (evaled) {
					return F.unaryAST1(head, x1);
				}
				return F.NIL;
			}

			/** {@inheritDoc} */
			@Override
			public IExpr visit3(IExpr head, IExpr arg1, IExpr arg2) {
				boolean evaled = false;
				IExpr x1 = arg1;
				IExpr result = arg1.accept(this);
				if (result.isPresent()) {
					evaled = true;
					x1 = result;
				}
				IExpr x2 = arg2;
				result = arg2.accept(this);
				if (result.isPresent()) {
					evaled = true;
					x2 = result;
				}
				if (head.equals(Power)) {
					if (x1.isTimes()) {
						// Power[x_ * y_, z_] :> x^z * y^z
						IAST timesAST = (IAST) x1;
						IASTMutable timesResult = timesAST.mapThread(Power(Null, x2), 1);
						if (assumptions) {
							IASTAppendable plusResult = F.PlusAlloc(timesAST.size() + 1);
							plusResult.append(C1D2);
							plusResult.appendArgs(timesAST.size(),
									i -> Negate(Divide(Arg(timesAST.get(i)), Times(C2, Pi))));
							IAST expResult = Power(E, Times(C2, I, Pi, x2, Floor(plusResult)));
							if (!(timesResult instanceof IASTAppendable)) {
								timesResult = timesResult.copyAppendable();
							}
							((IASTAppendable) timesResult).append(expResult);
							return timesResult;
						}
						return timesResult;
					}
					if (x1.isPower()) {
						return power(x1, x2);
					}
				}
				if (evaled) {
					return F.binaryAST2(head, x1, x2);
				}
				return F.NIL;
			}

			private IExpr power(IExpr x1, IExpr z) {
				// Power[x_ ^ y_, z_] :> x ^(y*z)
				IExpr base = x1.base();
				IExpr exponent = x1.exponent();
				IAST powerResult = Power(base, Times(exponent, z));
				if (assumptions) {
					IAST floorResult = Floor(Divide(Subtract(Pi, Im(Times(exponent, Log(base)))), Times(C2, Pi)));
					IAST expResult = Power(E, Times(C2, I, Pi, z, floorResult));
					IAST timesResult = Times(powerResult, expResult);
					return timesResult;
				}
				return powerResult;
			}
		}

		/** {@inheritDoc} */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isAST()) {
				boolean assumptions = false;
				if (ast.isAST2()) {
					final OptionArgs options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
					IExpr option = options.getOption(Assumptions);
					if (option.isTrue()) {
						// found "Assumptions -> True"
						assumptions = true;
					}
				}

				return powerExpand((IAST) ast.arg1(), assumptions);

			}
			return ast.arg1();
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		public static IExpr powerExpand(final IAST ast, boolean assumptions) {
			return ast.accept(new PowerExpandVisitor(assumptions)).orElse(ast);
		}

		/** {@inheritDoc} */
		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Assumptions, F.Automatic));
		}
	}

	private static class ToRadicals extends AbstractFunctionEvaluator {

		/**
		 * Root of a polynomial: <code>a + b*Slot1</code>.
		 * 
		 * @param a
		 *            coefficient a of the polynomial
		 * @param b
		 *            coefficient b of the polynomial
		 * @param nthRoot
		 *            <code>1 <= nthRoot <= 3</code> otherwise return F.NIL;
		 * @return
		 */
		private static IExpr root1(IExpr a, IExpr b, int nthRoot) {
			if (nthRoot != 1) {
				return F.NIL;
			}
			return Times(F.CN1, a, Power(b, -1));
		}

		/**
		 * Root of a polynomial: <code>a + b*Slot1 + c*Slot1^2</code>.
		 * 
		 * @param a
		 *            coefficient a of the polynomial
		 * @param b
		 *            coefficient b of the polynomial
		 * @param c
		 *            coefficient c of the polynomial
		 * @param nthRoot
		 *            <code>1 <= nthRoot <= 3</code> otherwise return F.NIL;
		 * @return
		 */
		private static IExpr root2(IExpr a, IExpr b, IExpr c, int nthRoot) {
			if (nthRoot < 1 || nthRoot > 3) {
				return F.NIL;
			}
			IExpr k = F.ZZ(nthRoot);
			return Plus(Times(C1D2, Power(F.CN1, k), F.Sqrt(Times(Plus(F.Sqr(b), Times(F.CN4, a, c)), Power(c, -2)))),
					Times(F.CN1D2, b, Power(c, -1)));
		}

		/**
		 * Root of a polynomial: <code>a + b*Slot1 + c*Slot1^2 + d*Slot1^3</code>.
		 * 
		 * @param a
		 *            coefficient a of the polynomial
		 * @param b
		 *            coefficient b of the polynomial
		 * @param c
		 *            coefficient c of the polynomial
		 * @param d
		 *            coefficient d of the polynomial
		 * @param nthRoot
		 *            <code>1 <= nthRoot <= 3</code> otherwise return F.NIL;
		 * @return
		 */
		private static IExpr root3(IExpr a, IExpr b, IExpr c, IExpr d, int nthRoot) {
			if (nthRoot < 1 || nthRoot > 3) {
				return F.NIL;
			}
			IExpr k = F.ZZ(nthRoot);

			// r = 3*b*d - c^2
			IExpr r = Plus(Negate(F.Sqr(c)), Times(F.C3, b, d));
			// q = 9*b*c*d - 2*c^3 - 27*a*d^2
			IExpr q = Plus(Times(F.CN2, Power(c, 3)), Times(F.C9, b, c, d), Times(F.ZZ(-27), a, F.Sqr(d)));
			// p = (q + Sqrt(q^2 + 4 r^3))^(1/3)
			IExpr p = Power(Plus(q, F.Sqrt(Plus(F.Sqr(q), Times(F.C4, Power(r, 3))))), F.C1D3);
			// -(c/(3*d)) + (E^((2*I*Pi*(k - 1))/3)*p)/(3*2^(1/3)*d) -
			// (2^(1/3)*r)/(E^((2*I*Pi*(k - 1))/3)*(3*p*d))
			return Plus(Times(F.CN1D3, c, Power(d, -1)),
					Times(F.CN1D3, Power(F.E, Times(F.CC(0L, 1L, -2L, 3L), Plus(F.CN1, k), F.Pi)), Power(p, -1), r,
							Power(C2, F.C1D3), Power(d, -1)),
					Times(F.C1D3, Power(C2, F.CN1D3), Power(F.E, Times(F.CC(0L, 1L, 2L, 3L), Plus(F.CN1, k), F.Pi)),
							Power(d, -1), p));
		}

		/**
		 * Root of a polynomial <code>a + b*Slot1 + c*Slot1^2 + d*Slot1^3 + e*Slot1^4</code>
		 * 
		 * @param a
		 * @param b
		 * @param c
		 * @param d
		 * @param e
		 * @param nthRoot
		 *            <code>1 <= nthRoot <= 4</code> otherwise return F.NIL;
		 * @return
		 */
		private static IExpr root4(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e, int nthRoot) {
			if (nthRoot < 1 || nthRoot > 4) {
				return F.NIL;
			}
			IExpr k = F.ZZ(nthRoot);

			// t = Sqrt(-4*(c^2 - 3*b*d + 12*a*e)^3 + (2*c^3 - 9*c*(b*d + 8*a*e) + 27*(a*d^2
			// + b^2*e))^2)
			IExpr t = F.Sqrt(Plus(Times(F.CN4, Power(Plus(F.Sqr(c), Times(F.CN3, b, d), Times(F.ZZ(12), a, e)), 3)),
					F.Sqr(Plus(Times(F.CN9, c, Plus(Times(b, d), Times(F.C8, a, e))),
							Times(F.ZZ(27), Plus(Times(a, F.Sqr(d)), Times(F.Sqr(b), e))), Times(C2, Power(c, 3))))));
			// s = (t + 2*c^3 - 9*c*(b*d + 8*a*e) + 27*(a*d^2 + b^2*e))^(1/3)
			IExpr s = Power(Plus(Times(C2, Power(c, 3)), t, Times(F.CN9, c, Plus(Times(b, d), Times(F.C8, a, e))),
					Times(F.ZZ(27), Plus(Times(a, F.Sqr(d)), Times(F.Sqr(b), e)))), F.C1D3);

			// eps1 = (1/2)*Sqrt((2^(1/3)*(c^2 - 3*b*d + 12*a*e))/ (3*s*e) + (3*d^2 +
			// 2*2^(2/3)*s*e - 8*c*e)/ (12 e^2))
			IExpr eps1 = Times(C1D2,
					F.Sqrt(Plus(
							Times(F.QQ(1L, 12L),
									Plus(Times(F.C3, F.Sqr(d)), Times(F.CN8, c, e),
											Times(C2, e, s, Power(C2, F.QQ(2L, 3L)))),
									Power(e, -2)),
							Times(F.C1D3, Plus(F.Sqr(c), Times(F.CN3, b, d), Times(F.ZZ(12), a, e)), Power(C2, F.C1D3),
									Power(e, -1), Power(s, -1)))));

			// u = -((2^(1/3)*s^2 + 2*c^2 - 6*b*d + 24*a*e)/ (2^(2/3)*s*e)) + 8*eps1^2
			IExpr u = Plus(Times(F.C8, F.Sqr(eps1)),
					Times(F.CN1,
							Plus(Times(C2, F.Sqr(c)), Times(F.CN6, b, d), Times(F.ZZ(24), a, e),
									Times(Power(C2, F.C1D3), F.Sqr(s))),
							Power(C2, F.QQ(-2L, 3L)), Power(e, -1), Power(s, -1)));

			// v = (d^3 - 4*c*d*e + 8*b*e^2)/ (8*e^3*eps1)
			IExpr v = Times(F.QQ(1L, 8L), Plus(Power(d, 3), Times(F.CN4, c, d, e), Times(F.C8, b, F.Sqr(e))),
					Power(e, -3), Power(eps1, -1));

			// eps2 = (1/2)*Sqrt(u + v)
			IExpr eps2 = Times(C1D2, F.Sqrt(Plus(u, v)));

			// eps3 = (1/2)*Sqrt(u - v)

			IExpr eps3 = Times(C1D2, F.Sqrt(Plus(u, Negate(v))));

			// -(d/(4*e)) + (2*Floor((k - 1)/2) - 1)*eps1 + (-1)^k*(1 - UnitStep(k -
			// 3))*eps2 - (-1)^k*(UnitStep(2 - k)
			// - 1)*eps3
			return Plus(Times(eps1, Plus(F.CN1, Times(C2, Floor(Times(C1D2, Plus(F.CN1, k)))))),
					Times(eps2, Plus(F.C1, Negate(F.UnitStep(Plus(F.CN3, k)))), Power(F.CN1, k)),
					Times(eps3, Plus(F.CN1, F.UnitStep(Plus(C2, Negate(k)))), Power(F.CN1, Plus(F.C1, k))),
					Times(F.CN1D4, d, Power(e, -1)));

		}

		private static class ToRadicalsVisitor extends VisitorExpr {
			IAST replacement;

			private ToRadicalsVisitor(IAST replacement) {
				this.replacement = replacement;
			}

			// D[a_+b_+c_,x_] -> D[a,x]+D[b,x]+D[c,x]
			// return listArg1.mapThread(F.D(F.Null, x), 1);
			@Override
			public IExpr visit(IASTMutable ast) {
				if (!ast.isAST(F.Root)) {
					IAST copied = replacement.setAtCopy(1, null);
					return ast.mapThread(copied, 1);
				}
				return F.NIL;
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 2) {
				IExpr arg1 = ast.arg1();
				IExpr temp = Structure.threadLogicEquationOperators(arg1, ast, 1);
				if (temp.isPresent()) {
					return temp;
				}
				if (arg1.isAST()) {
					ToRadicalsVisitor visitor = new ToRadicalsVisitor(ast);
					temp = ((IAST) arg1).accept(visitor);
					if (temp.isPresent()) {
						return temp;
					}
					temp = rootToRadicals((IAST) arg1, 4);
					if (temp.isPresent()) {
						return temp;
					}
				}
				return arg1;
			}
			return F.NIL;
		}

		private static IExpr rootToRadicals(final IAST ast, int maxDegree) {
			if (ast.size() == 3 && ast.arg2().isInteger()) {
				IExpr expr = ast.arg1();
				if (expr.isFunction()) {
					expr = expr.first();
					try {
						int k = ast.arg2().toIntDefault(Integer.MIN_VALUE);
						if (k < 0) {
							return F.NIL;
						}
						final IAST variables = F.List(F.Slot1);
						ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables);
						ExprPolynomial polynomial = ring.create(expr, false, true, false);

						long varDegree = polynomial.degree(0);
						if (polynomial.isConstant()) {
							return F.CEmptyList;
						}
						IExpr a;
						IExpr b;
						IExpr c;
						IExpr d;
						IExpr e;
						if (varDegree >= 1 && varDegree <= maxDegree) {
							a = C0;
							b = C0;
							c = C0;
							d = C0;
							e = C0;
							for (ExprMonomial monomial : polynomial) {
								IExpr coeff = monomial.coefficient();
								long lExp = monomial.exponent().getVal(0);
								if (lExp == 4) {
									e = coeff;
								} else if (lExp == 3) {
									d = coeff;
								} else if (lExp == 2) {
									c = coeff;
								} else if (lExp == 1) {
									b = coeff;
								} else if (lExp == 0) {
									a = coeff;
								} else {
									throw new ArithmeticException("Root::Unexpected exponent value: " + lExp);
								}
							}
							if (varDegree == 1) {
								return root1(a, b, k);
							} else if (varDegree == 2) {
								return root2(a, b, c, k);
							} else if (varDegree == 3) {
								return root3(a, b, c, d, k);
							} else if (varDegree == 4) {
								return root4(a, b, c, d, e, k);
							}
						}
					} catch (JASConversionException e2) {
						if (Config.SHOW_STACKTRACE) {
							e2.printStackTrace();
						}
					}
				}
			}
			return F.NIL;
		}

	}

	private static class Root extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			return ToRadicals.rootToRadicals(ast, 2);
		}

	}

	/**
	 * <pre>
	 * Simplify(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * simplifies <code>expr</code>
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Simplify(expr, option1, option2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * simplify <code>expr</code> with some additional options set
	 * </p>
	 * </blockquote>
	 * <ul>
	 * <li>Assumptions - use assumptions to simplify the expression</li>
	 * <li>ComplexFunction - use this function to determine the &ldquo;weight&rdquo; of an expression.</li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Simplify(1/2*(2*x+2))
	 * x+1
	 * 
	 * &gt;&gt; Simplify(2*Sin(x)^2 + 2*Cos(x)^2)
	 * 2
	 * 
	 * &gt;&gt; Simplify(x)
	 * x
	 * 
	 * &gt;&gt; Simplify(f(x))
	 * f(x)
	 * 
	 * &gt;&gt; Simplify(a*x^2+b*x^2)
	 * (a+b)*x^2
	 * </pre>
	 * <p>
	 * Simplify with an assumption:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Simplify(Sqrt(x^2), Assumptions -&gt; x&gt;0)
	 * x
	 * </pre>
	 * <p>
	 * For <code>Assumptions</code> you can define the assumption directly as second argument:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Simplify(Sqrt(x^2), x&gt;0)
	 * x
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt; Simplify(Abs(x), x&lt;0)
	 * Abs(x)
	 * </pre>
	 * <p>
	 * With this &ldquo;complexity function&rdquo; the <code>Abs</code> expression gets a &ldquo;heavier weight&rdquo;.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; complexity(x_) := 2*Count(x, _Abs, {0, 10}) + LeafCount(x)
	 * 
	 * &gt;&gt; Simplify(Abs(x), x&lt;0, ComplexityFunction-&gt;complexity)
	 * -x
	 * </pre>
	 * 
	 * <h3>Related terms</h3>
	 * <p>
	 * <a href="FullSimplify.md">FullSimplify</a>
	 * </p>
	 */
	private static class Simplify extends AbstractFunctionEvaluator {
		private static HashedOrderlessMatcherPlus PLUS_ORDERLESS_MATCHER = new HashedOrderlessMatcherPlus();
		static {
			// Cosh(x)+Sinh(x) -> Exp(x)
			PLUS_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRules(//
					F.Cosh(x_), //
					F.Sinh(x_), //
					F.Exp(x), //
					null, //
					true));
		}

		private static class SimplifiedResult {
			IExpr result;
			long minCounter;

			public SimplifiedResult(IExpr result, long minCounter) {
				this.result = result;
				this.minCounter = minCounter;
			}

			public boolean checkLessEqual(IExpr expr, long counter) {
				if (counter <= this.minCounter) {
					this.minCounter = counter;
					this.result = expr;
					return true;
				}
				return false;
			}

			public boolean checkLess(IExpr expr, long counter) {
				if (counter < this.minCounter) {
					this.minCounter = counter;
					this.result = expr;
					return true;
				}
				return false;
			}
		}

		private static class IsBasicExpressionVisitor extends AbstractVisitorBoolean {
			public IsBasicExpressionVisitor() {
				super();
			}

			@Override
			public boolean visit(IAST ast) {
				if (ast.isTimes() || ast.isPlus()) {
					// check the arguments
					return ast.forAll(x -> x.accept(this));
				}
				if (ast.isPower() && (ast.exponent().isInteger())) {
					// check the arguments
					return ast.base().accept(this);
				}
				return false;
			}

			@Override
			public boolean visit(IComplex element) {
				return true;
			}

			@Override
			public boolean visit(IComplexNum element) {
				return true;
			}

			@Override
			public boolean visit(IFraction element) {
				return true;
			}

			@Override
			public boolean visit(IInteger element) {
				return true;
			}

			@Override
			public boolean visit(INum element) {
				return true;
			}

			@Override
			public boolean visit(ISymbol symbol) {
				return true;
			}
		}

		private static class SimplifyVisitor extends VisitorExpr {
			final IsBasicExpressionVisitor isBasicAST = new IsBasicExpressionVisitor();
			/**
			 * This function is used to determine the “weight” of an expression. For example by counting the leafs of an
			 * expression with the <code>IExpr#leafCountSimplify()</code> method.
			 */
			final Function<IExpr, Long> fComplexityFunction;

			/**
			 * If <code>true</code> we are in full simplify mode (i.e. function FullSimplify)
			 */
			final boolean fFullSimplify;

			/**
			 * The current evlaution engine
			 */
			final EvalEngine fEngine;

			public SimplifyVisitor(Function<IExpr, Long> complexityFunction, EvalEngine engine, boolean fullSimplify) {
				super();
				fEngine = engine;
				fComplexityFunction = complexityFunction;
				fFullSimplify = fullSimplify;
			}

			private IExpr tryExpandTransformation(IAST plusAST, IExpr test) {
				IExpr result = F.NIL;
				long minCounter = fComplexityFunction.apply(plusAST);
				IExpr temp;
				long count;

				try {
					temp = F.evalExpand(test);
					count = fComplexityFunction.apply(temp);
					if (count < minCounter) {
						result = temp;
					}
				} catch (RuntimeException rex) {
					//
				}

				return result;
			}

			private IExpr tryTransformations(IExpr expr) {
				if (!expr.isAST()) {
					return F.NIL;
				}
				// try ExpandAll, Together, Apart, Factor to reduce the expression
				// long minCounter = fComplexityFunction.apply(expr);
				SimplifiedResult sResult = new SimplifiedResult(F.NIL, fComplexityFunction.apply(expr));
				IExpr temp;
				// long count;
				long expandAllCounter = 0;
				if (expr.isTimes()) {
					temp = tryTimesLog((IAST) expr);
					if (temp.isPresent()) {
						sResult.checkLessEqual(temp, fComplexityFunction.apply(temp));
					}
				} else if (expr.isPlus()) {
					temp = FactorTerms.factorTermsPlus((IAST) expr, EvalEngine.get());
					if (temp.isPresent()) {
						sResult.checkLessEqual(temp, fComplexityFunction.apply(temp));
					}

					IExpr[] commonFactors = InternalFindCommonFactorPlus.findCommonFactors((IAST) expr, true);
					if (commonFactors != null) {
						temp = fEngine.evaluate(F.Times(commonFactors[0], commonFactors[1]));
						sResult.checkLessEqual(temp, fComplexityFunction.apply(temp));
					}

					if (sResult.result.isPlus()) {
						temp = tryPlusLog((IAST) sResult.result);
					} else {
						temp = tryPlusLog((IAST) expr);
					}
					if (temp.isPresent()) {
						temp = fEngine.evaluate(temp);
						sResult.checkLessEqual(temp, fComplexityFunction.apply(temp));
					}
					// } else if (expr.isExp() && expr.second().isTimes()) {
					// IAST times = (IAST) expr.second();
					// IExpr i = Times.of(times, F.CNI, F.Power(F.Pi, F.CN1));
					// if (i.isRational()) {
					// IRational rat = (IRational) i;
					// if (rat.isGT(F.C1) || rat.isLE(F.CN1)) {
					// IInteger t = rat.trunc();
					// t = t.add(t.mod(F.C2));
					// // exp(I*(i - t)*Pi)
					// return F.Exp.of(F.Times(F.CI, F.Pi, F.Subtract(i, t)));
					// } else {
					// IRational t1 = rat.multiply(F.C6).normalize();
					// IRational t2 = rat.multiply(F.C4).normalize();
					// if (t1.isInteger() || t2.isInteger()) {
					// // Cos(- I*times) + I*Sin(- I*times)
					// return F.Plus.of(F.Cos(F.Times(F.CNI, times)),
					// F.Times(F.CI, F.Sin(F.Times(F.CNI, times))));
					// }
					// }
					// }
				}

				if (sResult.result.isAST()) {
					expr = sResult.result;
				}

				try {
					temp = F.evalExpandAll(expr);
					expandAllCounter = fComplexityFunction.apply(temp);
					sResult.checkLess(temp, expandAllCounter);
				} catch (RuntimeException rex) {
					//
				}

				if (sResult.result.isAST()) {
					expr = sResult.result;
				}

				if (((IAST) expr).hasTrigonometricFunction()) {

					try {
						temp = F.eval(F.TrigExpand(expr));
						sResult.checkLess(temp, fComplexityFunction.apply(temp));
					} catch (WrongArgumentType wat) {
						//
					}

					try {
						temp = F.eval(F.TrigToExp(expr));
						if (!sResult.checkLess(temp, fComplexityFunction.apply(temp))) {
							if (fFullSimplify) {
								temp = F.eval(F.Factor(temp));
								sResult.checkLess(temp, fComplexityFunction.apply(temp));
							}
						}
					} catch (WrongArgumentType wat) {
						//
					}

					try {
						temp = F.eval(F.TrigReduce(expr));
						sResult.checkLess(temp, fComplexityFunction.apply(temp));
					} catch (WrongArgumentType wat) {
						//
					}

				}

				try {
					temp = F.eval(F.ExpToTrig(expr));
					sResult.checkLess(temp, fComplexityFunction.apply(temp));
				} catch (WrongArgumentType wat) {
					//
				}

				try {
					IExpr together = expr;
					if (sResult.minCounter < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
						together = F.eval(F.Together(expr));
						sResult.checkLess(together, fComplexityFunction.apply(together));
					}

					if (fFullSimplify) {
						if (together.isTimes()) {
							IExpr[] parts = Algebra.getNumeratorDenominator((IAST) together, EvalEngine.get());
							IExpr numerator = parts[0];
							IExpr denominator = parts[1];

							if (!numerator.isOne() && //
									!denominator.isOne()) {
								tryPolynomialQuotientRemainder(numerator, denominator, sResult);
							}

						}
					}

				} catch (WrongArgumentType wat) {
					//
				}

				try {
					// System.out.println(expr.toString());
					// TODO: Factor is not fast enough for large expressions!
					// Maybe restricting factoring to smaller expressions is necessary here
					temp = F.NIL;
					if (fFullSimplify && expandAllCounter < 50) {// Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
						temp = F.eval(F.Factor(expr));
						sResult.checkLess(temp, fComplexityFunction.apply(temp));
					}
					// if (fFullSimplify
					// && (minCounter >= Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT || !temp.equals(expr))) {
					// if (expandAllCounter < (Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT / 2) && !fFullSimplify) {
					// temp = F.eval(F.Factor(expr));
					// count = fComplexityFunction.apply(temp);
					// if (count < minCounter) {
					// minCounter = count;
					// result = temp;
					// }
					// } else
					if (expandAllCounter < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
						temp = F.eval(F.FactorSquareFree(expr));
						sResult.checkLess(temp, fComplexityFunction.apply(temp));
					}

				} catch (WrongArgumentType wat) {
					//
				}

				try {
					if (sResult.minCounter < Config.MAX_SIMPLIFY_APART_LEAFCOUNT) {
						temp = F.eval(F.Apart(expr));
						sResult.checkLess(temp, fComplexityFunction.apply(temp));
					}
				} catch (WrongArgumentType wat) {
					//
				}
				return sResult.result;
			}

			/**
			 * Try <code>F.PolynomialQuotientRemainder(numerator, denominator, variable)</code> for differnt variables
			 * and numerator, denominator combinations.
			 * 
			 * @param numerator
			 * @param denominator
			 * @param sResult
			 */
			private void tryPolynomialQuotientRemainder(IExpr numerator, IExpr denominator, SimplifiedResult sResult) {
				IExpr temp;
				VariablesSet variables = new VariablesSet(numerator);
				variables.addVarList(denominator);
				List<IExpr> vars = variables.getArrayList();
				boolean evaled = false;
				for (int i = 0; i < vars.size(); i++) {
					temp = EvalEngine.get()
							.evaluate(F.PolynomialQuotientRemainder(numerator, denominator, vars.get(i)));
					if (temp.isAST(F.List, 3) && //
							temp.second().isZero()) {
						// the remainder is 0 here:
						IExpr arg1 = temp.first();
						if (sResult.checkLess(arg1, fComplexityFunction.apply(arg1))) {
							evaled = true;
							break;
						}
					}
				}
				if (!evaled) {
					for (int i = 0; i < vars.size(); i++) {
						temp = EvalEngine.get()
								.evaluate(F.PolynomialQuotientRemainder(denominator, numerator, vars.get(i)));
						if (temp.isAST(F.List, 3) && //
								temp.second().isZero()) {
							// the remainder is 0 here:
							IExpr arg1 = temp.first().reciprocal();
							if (sResult.checkLess(arg1, fComplexityFunction.apply(arg1))) {
								break;
							}
						}
					}
				}
			}

			@Override
			public IExpr visit(IASTMutable ast) {
				SimplifiedResult sResult = new SimplifiedResult(F.NIL, fComplexityFunction.apply(ast));

				IExpr temp = visitAST(ast);
				if (temp.isPresent()) {
					temp = fEngine.evaluate(temp);
					if (sResult.checkLessEqual(temp, fComplexityFunction.apply(temp))) {
						if (temp.isAST()) {
							ast = (IASTMutable) temp;
							// result = temp;
						} else {
							return temp;
						}
					}
					// long count = fComplexityFunction.apply(temp);
					// if (count <= minCounter[0]) {
					// minCounter[0] = count;
					// if (temp.isAST()) {
					// ast = (IASTMutable) temp;
					// result = temp;
					// } else {
					// return temp;
					// }
					// }
				}

				if (ast.isPlus()) {
					IASTAppendable basicPlus = F.PlusAlloc(ast.size());
					IASTAppendable restPlus = F.PlusAlloc(ast.size());
					ast.forEach(x -> {
						if (x.accept(isBasicAST)) {
							basicPlus.append(x);
						} else {
							restPlus.append(x);
						}
					});
					if (basicPlus.size() > 1) {
						temp = tryTransformations(basicPlus.oneIdentity0());
						if (temp.isPresent()) {
							if (restPlus.isAST0()) {
								return temp;
							}
							return F.Plus(temp, restPlus);
						}
					}

					temp = tryTransformations(ast);
					if (temp.isPresent()) {
						return temp;
					}

					if (fFullSimplify) {
						HashedOrderlessMatcher hashRuleMap = PLUS_ORDERLESS_MATCHER;
						if (hashRuleMap != null) {
							ast.setEvalFlags(ast.getEvalFlags() ^ IAST.IS_HASH_EVALED);
							temp = hashRuleMap.evaluateRepeated(ast, fEngine);
							if (temp.isPresent()) {
								return fEngine.evaluate(temp);
							}
						}
						functionExpand(ast, sResult);
					}

					return sResult.result;
				} else if (ast.isTimes()) {

					final IExpr denominator = F.Denominator.of(ast);
					if (!denominator.isNumber()) {
						final IExpr numerator = F.Numerator(ast);
						if (numerator.isTimes() || denominator.isTimes()) {
							IExpr numer = F.evalExpandAll(numerator);
							IExpr denom = F.evalExpandAll(denominator);
							if (F.PossibleZeroQ.ofQ(F.Subtract(numer, denom))) {
								return F.C1;
							}
						}
						// if (denominator.isTimes() || numerator.isTimes()) {
						// temp = fEngine.evaluate(F.Divide(numerator, denominator));
						//
						// long count = fComplexityFunction.apply(temp);
						// if (count <= minCounter[0]) {
						// minCounter[0] = count;
						// if (temp.isAST()) {
						// ast = (IASTMutable) temp;
						// result = temp;
						// } else {
						// return temp;
						// }
						// }
						//
						// }
					}

					temp = reduceNumberFactor(ast);
					if (temp.isPresent()) {

						sResult.result = temp;
						sResult.minCounter = fComplexityFunction.apply(temp);
					}

					IASTAppendable newTimes = F.NIL;
					for (int i = 1; i < ast.size(); i++) {
						temp = ast.get(i);
						if (temp.isPowerReciprocal() && temp.base().isPlus() && temp.base().size() == 3) {
							// example 1/(5+Sqrt(17)) => 1/8*(5-Sqrt(17))
							IAST plus1 = (IAST) temp.base();
							IAST plus2 = plus1.setAtCopy(2, plus1.arg2().negate());
							// example (5+Sqrt(17)) * (5-Sqrt(17))
							IExpr expr = F.eval(F.Expand(F.Times(plus1, plus2)));
							if (expr.isNumber() && !expr.isZero()) {
								IASTMutable powerSimplified = F.Times(expr.inverse(), plus2);
								if (newTimes.isPresent()) {
									newTimes.set(i, powerSimplified);
								} else {
									newTimes = ast.setAtClone(i, powerSimplified);
								}
							}
						}
					}
					if (newTimes.isPresent()) {
						sResult.result = ast;
						try {
							temp = F.eval(F.Expand(newTimes));
							if (sResult.checkLess(temp, fComplexityFunction.apply(temp))) {
								if (temp.isAtom()) {
									return temp;
								}
							}
						} catch (RuntimeException rex) {
							//
						}
					}

					// temp = tryTransformations(result);
					// return temp.orElse(result);

					temp = tryTransformations(sResult.result.orElse(ast));
					if (temp.isPresent()) {
						sResult.result = temp;
					}
					temp = sResult.result.orElse(ast);
					sResult.minCounter = fComplexityFunction.apply(temp);
					functionExpand(temp, sResult);// minCounter[0], result);
				} else if (ast.isPowerReciprocal() && ast.base().isPlus() && ast.base().size() == 3) {
					// example 1/(5+Sqrt(17)) => 1/8*(5-Sqrt(17))
					IAST plus1 = (IAST) ast.base();
					IAST plus2 = plus1.setAtCopy(2, plus1.arg2().negate());
					// example (5+Sqrt(17)) * (5-Sqrt(17))
					IExpr expr = F.eval(F.Expand(F.Times(plus1, plus2)));
					if (expr.isNumber() && !expr.isZero()) {
						IExpr powerSimplified = F.Times.of(expr.inverse(), plus2);
						if (sResult.checkLess(powerSimplified, fComplexityFunction.apply(powerSimplified))) {
							return powerSimplified;
						}
					}
					// } else {
					// temp = tryTransformations(ast);
					// if (temp.isPresent()) {
					// long count = fComplexityFunction.apply(temp);
					// if (count < minCounter[0]) {
					// minCounter[0] = count;
					// result = temp;
					// }
					// }
				}

				temp = F.evalExpandAll(ast);
				sResult.checkLess(temp, fComplexityFunction.apply(temp));

				functionExpand(ast, sResult);
				return sResult.result;
			}

			/**
			 * Simplify <code>Log(x)+Log(y)+p*Log(z)</code> if x, y, z are real numbers and p is an integer number
			 * 
			 * @param plusAST
			 * @return
			 */
			private static IExpr tryPlusLog(IAST plusAST) {
				if (plusAST.size() > 2) {
					IASTAppendable logPlus = F.PlusAlloc(plusAST.size());
					IExpr a1 = F.NIL;
					boolean evaled = false;
					for (int i = 1; i < plusAST.size(); i++) {
						IExpr a2 = plusAST.get(i);
						IExpr arg = F.NIL;
						if (a2.isAST(F.Times, 3) && a2.first().isInteger() && //
								a2.second().isLog() && a2.second().first().isReal()) {
							arg = F.Power.of(a2.second().first(), a2.first());
						} else if (a2.isLog() && a2.first().isReal()) {
							arg = a2.first();
						}
						if (arg.isReal()) {
							if (a1.isPresent()) {
								a1 = a1.multiply(arg);
								evaled = true;
							} else {
								a1 = arg;
							}
							continue;
						}
						logPlus.append(a2);
					}
					if (evaled) {
						if (logPlus.size() == 1) {
							return Log.of(a1);
						} else {
							logPlus.append(Log(a1));
							return logPlus;
						}
					}
				}
				return F.NIL;
			}

			private static IExpr tryTimesLog(IAST timesAST) {
				if (timesAST.size() > 2 && timesAST.first().isInteger() && !timesAST.first().isMinusOne()) {

					for (int i = 2; i < timesAST.size(); i++) {
						IExpr temp = timesAST.get(i);
						if (temp.isLog() && temp.first().isReal()) {
							IAST result = timesAST.splice(i, 1, F.Log(F.Power.of(temp.first(), timesAST.first())));
							return result.splice(1).oneIdentity0();
						}

					}
				}
				return F.NIL;
			}

			private void functionExpand(IExpr expr, SimplifiedResult sResult) {// long minCounter, IExpr result) {
				if (fFullSimplify) {
					try {
						expr = F.eval(F.FunctionExpand(expr));
						sResult.checkLess(expr, fComplexityFunction.apply(expr));
					} catch (RuntimeException rex) {
						//
					}
				} else {
					if (expr.isLog()) {
						try {
							expr = F.eval(F.FunctionExpand(expr));
							sResult.checkLessEqual(expr, fComplexityFunction.apply(expr));
						} catch (RuntimeException rex) {
							//
						}
					}
				}
			}

			private IExpr reduceNumberFactor(IASTMutable timesAST) {
				IExpr temp;
				IASTAppendable basicTimes = F.TimesAlloc(timesAST.size());
				IASTAppendable restTimes = F.TimesAlloc(timesAST.size());
				INumber number = null;
				IExpr arg1 = timesAST.arg1();

				if (arg1.isNumber()) {
					if (!arg1.isZero()) {
						number = (INumber) arg1;
					}
				} else if (arg1.isPlus()) {// && arg1.first().isNumber()) {
					long minCounter = fComplexityFunction.apply(arg1);
					IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1.first(), F.CI);
					if (imPart.isPresent()) {
						IExpr negativeAST = fEngine.evaluate(F.Distribute(F.Times(F.CI, arg1)));
						long count = fComplexityFunction.apply(negativeAST);
						if (count <= minCounter) {
							return fEngine
									.evaluate(F.Times(negativeAST, F.Distribute(F.Times(F.CNI, timesAST.rest()))));
						}
					} else {
						IExpr negativeAST = fEngine.evaluate(F.Distribute(F.Times(F.CN1, arg1)));
						long count = fComplexityFunction.apply(negativeAST);
						if (count <= minCounter) {
							IASTAppendable result = F.TimesAlloc(timesAST.size());
							result.append(F.CN1);
							result.append(negativeAST);
							result.appendAll(timesAST, 2, timesAST.size());
							return result;
						}
					}
				}
				IExpr reduced = F.NIL;
				for (int i = 1; i < timesAST.size(); i++) {
					temp = timesAST.get(i);
					if (temp.accept(isBasicAST)) {
						if (i != 1 && number != null) {
							if (temp.isPlus()) {
								// <number> * Plus[.....]
								reduced = tryExpand(timesAST, (IAST) temp, number, i, false);
							} else if (temp.isPowerReciprocal() && temp.base().isPlus()) {
								// <number> * Power[Plus[...], -1 ]
								reduced = tryExpand(timesAST, (IAST) temp.base(), number.inverse(), i, true);
							}
							if (reduced.isPresent()) {
								return reduced;
							}
						}
						basicTimes.append(temp);
					} else {
						restTimes.append(temp);
					}
				}

				if (basicTimes.size() > 1) {
					temp = tryTransformations(basicTimes.oneIdentity0());
					if (temp.isPresent()) {
						if (restTimes.isAST0()) {
							return temp;
						}
						return F.Times(temp, restTimes);
					}
				}
				return F.NIL;
			}

			private IExpr tryExpand(IAST timesAST, IAST plusAST, IExpr arg1, int i, boolean isPowerReciprocal) {
				IExpr expandedAst = tryExpandTransformation((IAST) plusAST, F.Times(arg1, plusAST));
				if (expandedAst.isPresent()) {
					IASTAppendable result = F.TimesAlloc(timesAST.size());
					// ast.range(2, ast.size()).toList(result.args());
					result.appendAll(timesAST, 2, timesAST.size());
					if (isPowerReciprocal) {
						result.set(i - 1, F.Power(expandedAst, F.CN1));
					} else {
						result.set(i - 1, expandedAst);
					}
					return result;
				}
				return F.NIL;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isAtom() && ast.isAST1()) {
				return arg1;
			}
			if (arg1.isAST()) {
				IAST list1 = (IAST) arg1;
				int headID = list1.headID();
				switch (headID) {
				case ID.List:
					return list1.mapThread(ast, 1);
				case ID.Equal:
				case ID.Unequal:
				case ID.Greater:
				case ID.GreaterEqual:
				case ID.Less:
				case ID.LessEqual:
					if (list1.size() == 3 && !list1.arg2().isZero()) {
						IExpr sub = ast.topHead().of(F.Subtract(list1.arg1(), list1.arg2()));
						return F.binaryAST2(list1.head(), sub, F.C0);
					}
					break;
				}

			}

			// note: this should also cache FullSimplify calls
			IExpr result = F.REMEMBER_AST_CACHE.getIfPresent(ast);
			if (result != null) {
				return result;
			}
			IExpr assumptionExpr = F.NIL;
			IExpr complexityFunctionHead = F.NIL;

			if (ast.size() > 2) {
				IExpr arg2 = ast.arg2();

				if (!arg2.isRule()) {
					assumptionExpr = arg2;
				}
				final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
				IExpr option = options.getOption(F.Assumptions);
				if (option.isPresent() && !option.equals(F.$Assumptions)) {
					assumptionExpr = option;
				}
				complexityFunctionHead = options.getOptionAutomatic(F.ComplexityFunction);
			}

			IAssumptions oldAssumptions = engine.getAssumptions();
			try {
				Function<IExpr, Long> complexityFunction = createComplexityFunction(complexityFunctionHead, engine);
				long minCounter = complexityFunction.apply(arg1);
				result = arg1;
				long count = 0L;
				if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {

					IAssumptions assumptions = oldAssumptions;
					if (oldAssumptions == null) {
						assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
					} else {
						assumptions = oldAssumptions.addAssumption((IAST) assumptionExpr);
					}
					if (assumptions != null) {
						engine.setAssumptions(assumptions);
						arg1 = AssumptionFunctions.refineAssumptions(arg1, assumptions, engine);
						count = complexityFunction.apply(arg1);
						if (count < minCounter) {
							minCounter = count;
							result = arg1;
						}
					}

				}

				IExpr temp = arg1.replaceAll(F.List(//
						F.Rule(F.GoldenAngle, //
								F.Times(F.Subtract(F.C3, F.CSqrt5), F.Pi)), //
						F.Rule(F.GoldenRatio, //
								F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5))), //
						F.Rule(F.Degree, //
								F.Divide(F.Pi, F.ZZ(180))) //
				));
				if (temp.isPresent()) {
					arg1 = temp;
				}

				temp = simplifyStep(arg1, complexityFunction, minCounter, result, engine);
				F.REMEMBER_AST_CACHE.put(ast, temp);
				return temp;

			} catch (ArithmeticException e) {
				//
			} finally {
				engine.setAssumptions(oldAssumptions);
			}

			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_INFINITY;
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.Assumptions, F.$Assumptions), F.Rule(F.ComplexityFunction, F.Automatic));
		}

		private IExpr simplifyStep(IExpr arg1, Function<IExpr, Long> complexityFunction, long minCounter, IExpr result,
				EvalEngine engine) {
			long count;
			IExpr temp;
			temp = arg1.accept(new SimplifyVisitor(complexityFunction, engine, isFullSimplifyMode()));
			while (temp.isPresent()) {
				count = complexityFunction.apply(temp);
				if (count == minCounter) {
					return temp;
				}
				if (count < minCounter) {
					minCounter = count;
					result = temp;
					temp = result.accept(new SimplifyVisitor(complexityFunction, engine, isFullSimplifyMode()));
				} else {
					return result;
				}
			}
			return result;
		}

		public boolean isFullSimplifyMode() {
			return false;
		}

		/**
		 * Creata the complexity function which determines the &quot;more simplified&quot; expression.
		 * 
		 * @param complexityFunctionHead
		 * @param engine
		 * @return
		 */
		private static Function<IExpr, Long> createComplexityFunction(IExpr complexityFunctionHead, EvalEngine engine) {
			Function<IExpr, Long> complexityFunction = x -> x.leafCountSimplify();
			if (complexityFunctionHead.isPresent()) {
				final IExpr head = complexityFunctionHead;
				complexityFunction = x -> {
					IExpr temp = engine.evaluate(F.unaryAST1(head, x));
					if (temp.isInteger() && !temp.isNegative()) {
						return ((IInteger) temp).toLong();
					}
					return Long.MAX_VALUE;
				};
			}
			return complexityFunction;
		}

	}

	/**
	 * <h2>Together</h2>
	 * 
	 * <pre>
	 * <code>Together(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * writes sums of fractions in <code>expr</code> together.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt;&gt; Together(a/b+x/y)
	 * (a*y+b*x)*b^(-1)*y^(-1)
	 * 
	 * &gt;&gt; Together(a / c + b / c)
	 * (a+b)/c
	 * </code>
	 * </pre>
	 * <p>
	 * <code>Together</code> operates on lists:
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Together({x / (y+1) + x / (y+1)^2})
	 * {x (2 + y) / (1 + y) ^ 2}
	 * </code>
	 * </pre>
	 * <p>
	 * But it does not touch other functions:
	 * </p>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Together(f(a / c + b / c))
	 * f(a/c+b/c)
	 * &gt;&gt; f(x)/x+f(x)/x^2//Together
	 * f(x)/x^2+f(x)/x
	 * </code>
	 * </pre>
	 */
	private static class Together extends AbstractFunctionEvaluator {

		private static IBuiltInSymbol reduceConstantTerm = F.localFunction("reduceConstantTerm", (c) -> {
			if (c.isNumber()) {
				return F.List(c, F.C1);
			}
			if (c.isTimes() && c.first().isNumber()) {
				return F.List(c.first(), c.rest().oneIdentity1());
			}
			return F.List(F.C1, c);
		});

		private static IExpr reduceFactorConstant(IExpr p, EvalEngine engine) {

			if (!engine.isNumericMode() && p.isPlus() && !engine.isTogetherMode()) {
				IExpr e = p;
				// ((reduceConstantTerm /@ (List @@ e)) // Transpose)[[1]]
				IExpr cTerms = F.Transpose
						.of(engine, F.Map(F.Function(F.unaryAST1(reduceConstantTerm, F.Slot1)), F.Apply(F.List, e)))
						.first();
				// GCD @@ cTerms
				IExpr c = F.Apply.of(engine, F.GCD, cTerms);
				if (cTerms.last().isNegative()) {
					c = c.negate();
				}
				IExpr gcd;
				if (!c.isFree(x -> x.isNumeric(), false)) {
					gcd = engine.evaluate(F.Rationalize(c));
					gcd = engine.evalN(gcd);
				} else {
					gcd = engine.evaluate(c);
				}
				if (gcd.isFree(F.GCD)) {
					return F.Times(gcd, F.Distribute.of(engine, F.Divide(e, gcd)));
				}
			}

			return p;
		}

		/**
		 * Calls <code>Together</code> for each argument of the <code>ast</code>.
		 * 
		 * @param ast
		 * @return <code>F.NIL</code> if the <code>ast</code> couldn't be evaluated.
		 */
		private static IASTMutable togetherForEach(final IAST ast, EvalEngine engine) {
			IASTMutable result = F.NIL;
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isAST()) {
					IExpr temp = togetherNull((IAST) ast.get(i), engine);
					if (temp.isPresent()) {
						if (!result.isPresent()) {
							result = ast.copy();
						}
						result.set(i, temp);
					}
				}
			}
			return result;
		}

		/**
		 * Do a <code>ExpandAll(ast)</code> and call <code>togetherAST</code> afterwards with the result..
		 * 
		 * @param ast
		 * @return <code>F.NIL</code> couldn't be transformed by <code>ExpandAll(()</code> od <code>togetherAST()</code>
		 */
		private static IExpr togetherNull(IAST ast, EvalEngine engine) {
			boolean evaled = false;
			IExpr temp = expandAll(ast, null, true, false, engine);
			if (!temp.isPresent()) {
				temp = ast;
			} else {
				evaled = true;
			}
			if (temp.isAST()) {
				IExpr result = togetherPlusTimesPower((IAST) temp, engine);
				if (result.isPresent()) {
					return engine.evaluate(result);
				}
			}
			if (evaled) {
				return temp;
			}
			return F.NIL;
		}

		/**
		 * 
		 * @param plusAST
		 *            a <code>Plus[...]</code> expression
		 * @return <code>null</code> couldn't be transformed by <code>ExpandAll(()</code> od <code>togetherAST()</code>
		 */
		private static IExpr togetherPlus(IAST plusAST) {
			if (plusAST.size() <= 2) {
				return F.NIL;
			}
			IASTAppendable numerator = F.ast(F.Plus, plusAST.size(), false);
			IASTAppendable denominator = F.ast(F.Times, plusAST.size(), false);
			boolean[] evaled = new boolean[1];
			plusAST.forEach((x, i) -> {
				// IExpr[] fractionalParts = fractionalPartsRational(x);
				// if (fractionalParts != null) {
				// numerator.append(i, fractionalParts[0]);
				// IExpr temp = fractionalParts[1];
				// if (!temp.isOne()) {
				// evaled[0] = true;
				// }
				// denominator.append(i, temp);
				// } else {
				// numerator.append(i, x);
				// denominator.append(i, F.C1);
				// }
				if (x.isFraction()) {
					numerator.append(i, ((IFraction) x).numerator());
					denominator.append(i, ((IFraction) x).denominator());
				} else if (x.isComplex()) {
					IRational re = ((IComplex) x).getRealPart();
					IRational im = ((IComplex) x).getImaginaryPart();
					if (re.isFraction() || im.isFraction()) {
						numerator.append(i, re.numerator().times(im.denominator())
								.add(im.numerator().times(re.denominator()).times(F.CI)));
						denominator.append(i, re.denominator().times(im.denominator()));
					} else {
						numerator.append(i, x);
						denominator.append(i, F.C1);
					}
				} else {
					IExpr[] fractionalParts = fractionalParts(x, false);
					if (fractionalParts != null) {
						numerator.append(i, fractionalParts[0]);
						IExpr temp = fractionalParts[1];
						if (!temp.isOne()) {
							evaled[0] = true;
						}
						denominator.append(i, temp);
					} else {
						numerator.append(i, x);
						denominator.append(i, F.C1);
					}
				}
			});
			if (!evaled[0]) {
				return F.NIL;
			}
			numerator.forEach((x, i) -> {
				IASTAppendable ni = F.TimesAlloc(plusAST.argSize());
				ni.append(x);
				for (int j = 1; j < plusAST.size(); j++) {
					if (i == j) {
						continue;
					}
					IExpr temp = denominator.get(j);
					if (!temp.isOne()) {
						ni.append(temp);
					}
				}
				numerator.set(i, ni.oneIdentity1());
			});
			int i = 1;
			while (denominator.size() > i) {
				if (denominator.get(i).isOne()) {
					denominator.remove(i);
					continue;
				}
				i++;
			}
			if (denominator.isAST0()) {
				return F.NIL;
			}

			IExpr exprNumerator = F.evalExpand(numerator.oneIdentity0());
			IExpr denom = F.eval(denominator.oneIdentity1());
			IExpr exprDenominator = F.evalExpand(denom);
			if (exprNumerator.isZero()) {
				if (exprDenominator.isZero()) {
					// let the standard evaluation handle the division by zero
					// 0^0
					return F.Times(exprNumerator, F.Power(exprDenominator, F.CN1));
				}
				return F.C0;
			}
			if (!exprDenominator.isOne()) {
				try {
					IExpr[] result = cancelGCD(exprNumerator, exprDenominator);
					if (result != null) {
						IExpr pInv = result[2].inverse();
						if (result[0].isOne()) {
							return F.Times(pInv, result[1]);
						}
						return F.Times(result[0], result[1], pInv);
					}
					return F.Times(exprNumerator, F.Power(denom, -1));
				} catch (JASConversionException jce) {
					if (Config.DEBUG) {
						jce.printStackTrace();
					}
				}
				return F.Times(exprNumerator, F.Power(denom, F.CN1));
			}
			return exprNumerator;
		}

		private static IExpr togetherPlusTimesPower(final IAST ast, EvalEngine engine) {
			if (ast.isPlus()) {
				IAST result = togetherForEach(ast, engine);
				if (result.isPresent()) {
					return togetherPlus(result).orElse(result);
				}
				return togetherPlus(ast);
			} else if (ast.isTimes() || ast.isPower()) {
				try {
					IASTMutable result = F.NIL;
					if (ast.isTimes()) {
						result = togetherForEach(ast, engine);
					} else {
						// Power
						result = togetherPower(ast, result, engine);
					}
					if (result.isPresent()) {
						IExpr temp = engine.evaluate(result);
						if (temp.isTimes() || temp.isPower()) {
							return Cancel.togetherPowerTimes(temp).orElse(temp);
						}
						return temp;
					}
					return Cancel.togetherPowerTimes(ast);
				} catch (JASConversionException jce) {
					if (Config.DEBUG) {
						jce.printStackTrace();
					}
				}
			}

			return F.NIL;
		}

		private static IASTMutable togetherPower(final IAST ast, IASTMutable result, EvalEngine engine) {
			if (ast.arg1().isAST()) {
				IExpr temp = togetherNull((IAST) ast.arg1(), engine);
				if (temp.isPresent()) {
					if (!result.isPresent()) {
						result = ast.copy();
					}
					if (ast.arg2().isNegative() && temp.isTimes()) {
						IExpr[] fractionalParts = fractionalPartsRational(temp);
						if (fractionalParts != null) {
							result.set(1, F.Divide(fractionalParts[1], fractionalParts[0]));
							result.set(2, ast.arg2().negate());
						} else {
							result.set(1, temp);
						}
					} else {
						result.set(1, temp);
					}
				}
			}
			return result;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			IAST list = Structure.threadLogicEquationOperators(arg1, ast, 1);
			if (list.isPresent()) {
				return list;
			}
			if (arg1.isAST()) {
				return togetherExpr((IAST) arg1, engine);
			}
			return arg1;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		private static IExpr togetherExpr(IExpr arg1, EvalEngine engine) {
			if (arg1.isPlusTimesPower()) {
				if (arg1.isPower()) {
					if (arg1.base().isAtom() && arg1.exponent().isAtom()) {
						return arg1;
					}
					if (!arg1.exponent().isMinusOne()) {
						if (arg1.base().isPlusTimesPower()) {
							if (arg1.exponent().isNegative()) {
								return F.Power(togetherExpr(arg1.base().inverse(), engine), arg1.exponent().negate());
							}
							return F.Power(togetherExpr(arg1.base(), engine), arg1.exponent());
						}
					}
				} else if (arg1.isTimes()) {
					if (arg1.first().isAtom()) {
						IExpr times = ((IAST) arg1).splice(1).oneIdentity0();
						if (times.isPower()) {
							return F.Times(arg1.first(), togetherExpr(times, engine));
						}
					}
					// } else if (arg1.isPlus()) {
					// IExpr[] result = InternalFindCommonFactorPlus.findCommonFactors((IAST) arg1, true);
					// if (result != null && !result[0].isOne()) {
					// IExpr temp = togetherNull((IAST) result[1], engine).orElse(result[1]);
					// if (temp.isPresent()) {
					// temp = engine.evaluate(F.Times(result[0], reduceFactorConstant(temp, engine)));
					// }
					// if (temp.isTimes() || temp.isPower()) {
					// return F.Cancel(temp);
					// }
					// return temp;
					// }
				}
				IExpr temp = togetherNull((IAST) arg1, engine).orElse(arg1);
				if (temp.isPresent()) {
					return reduceFactorConstant(temp, engine);
				}

			}
			return reduceFactorConstant(arg1, engine);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <h2>Variables</h2>
	 * 
	 * <pre>
	 * <code>Variables[expr]
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives a list of the variables that appear in the polynomial <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Variables(a x^2 + b x + c)
	 * {a,b,c,x}
	 * 
	 * &gt;&gt; Variables({a + b x, c y^2 + x/2})
	 * {a,b,c,x,y}
	 * 
	 * &gt;&gt; Variables(x + Sin(y))
	 * {x,Sin(y)}
	 * </code>
	 * </pre>
	 */
	private static class Variables extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return VariablesSet.getVariables(ast.arg1());
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private static boolean appendPlus(IASTAppendable ast, IExpr expr) {
		if (ast.head().equals(F.Plus) && expr.head().equals(F.Plus)) {
			return ast.appendArgs((IAST) expr);
		}
		return ast.append(expr);
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
	 * for the given expressions <code>numeratorPolynomial</code> and <code>denominatorPolynomial</code>.
	 * 
	 * 
	 * @param numerator
	 *            an expression which should be converted to JAS polynomial (using substitutions)
	 * @param denominator
	 *            a expression which could be converted to JAS polynomial (using substitutions)
	 * @return <code>null</code> if the expressions couldn't be converted to JAS polynomials or gcd equals 1
	 * @throws JASConversionException
	 */
	public static IExpr[] cancelGCD(final IExpr numerator, final IExpr denominator) throws JASConversionException {
		try {
			if (denominator.isInteger() && numerator.isPlus()) {
				IExpr[] result = Cancel.cancelPlusIntegerGCD((IAST) numerator, (IInteger) denominator);
				if (result != null) {
					return result;
				}
			}

			VariablesSet eVar = new VariablesSet(numerator);
			eVar.addVarList(denominator);
			if (eVar.size() == 0) {
				return null;
			}

			IAST vars = eVar.getVarList();
			PolynomialHomogenization substitutions = new PolynomialHomogenization(vars, EvalEngine.get());
			IExpr[] subst = substitutions.replaceForward(numerator, denominator);
			IExpr numeratorPolynomial = subst[0];
			IExpr denominatorPolynomial = subst[1];
			if (substitutions.size() > 0) {
				eVar.clear();
				eVar.addAll(substitutions.substitutedVariablesSet());
				vars = eVar.getVarList();
			}
			try {
				ExprPolynomialRing ring = new ExprPolynomialRing(vars);
				ExprPolynomial pol1 = ring.create(numeratorPolynomial);
				ExprPolynomial pol2 = ring.create(denominatorPolynomial);
				List<IExpr> varList = eVar.getVarList().copyTo();
				JASIExpr jas = new JASIExpr(varList, true);
				GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
				GenPolynomial<IExpr> p2 = jas.expr2IExprJAS(pol2);

				GreatestCommonDivisor<IExpr> engine;
				engine = GCDFactory.getImplementation(ExprRingFactory.CONST);
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
				result[0] = substitutions.replaceBackward(result[0]);
				result[1] = substitutions.replaceBackward(result[1]);
				result[2] = substitutions.replaceBackward(result[2]);
				return result;
			} catch (RuntimeException rex) {

			}
			List<IExpr> varList = eVar.getVarList().copyTo();
			ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
			JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(varList, cfac);
			GenPolynomial<Complex<BigRational>> p1 = jas.expr2JAS(numeratorPolynomial, false);
			GenPolynomial<Complex<BigRational>> p2 = jas.expr2JAS(denominatorPolynomial, false);
			GreatestCommonDivisor<Complex<BigRational>> engine;
			engine = GCDFactory.getImplementation(cfac);
			GenPolynomial<Complex<BigRational>> gcd;
			// if (numeratorPolynomial.isSymbol()||denominatorPolynomial.isSymbol() ) {
			// gcd = jas.expr2IExprJAS(F.C1);
			// }else {
			gcd = engine.gcd(p1, p2);
			// }
			IExpr[] result = new IExpr[3];
			if (gcd.isONE()) {
				result[0] = jas.complexPoly2Expr(gcd);
				result[1] = jas.complexPoly2Expr(p1);
				result[2] = jas.complexPoly2Expr(p2);
			} else {
				result[0] = F.C1;
				result[1] = F.eval(jas.complexPoly2Expr(p1.divide(gcd)));
				result[2] = F.eval(jas.complexPoly2Expr(p2.divide(gcd)));
			}
			result[0] = substitutions.replaceBackward(result[0]);
			result[1] = substitutions.replaceBackward(result[1]);
			result[2] = substitutions.replaceBackward(result[2]);
			return result;
		} catch (RuntimeException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Expand the given <code>ast</code> expression.
	 * 
	 * @param ast
	 * @param patt
	 * @param distributePlus
	 *            TODO
	 * @param evalParts
	 *            evaluate the determined numerator and denominator parts
	 * @return <code>F.NIL</code> if the expression couldn't be expanded.
	 */
	public static IExpr expand(final IAST ast, IExpr patt, boolean expandNegativePowers, boolean distributePlus,
			boolean evalParts) {
		Expand.Expander expander = new Expand.Expander(patt, expandNegativePowers, distributePlus);
		return expander.expandAST(ast, evalParts);
	}

	/**
	 * Expand the given <code>ast</code> expression.
	 * 
	 * @param ast
	 * @param patt
	 * @param expandNegativePowers
	 * @param distributePlus
	 * 
	 * @return <code>F.NIL</code> if the expression couldn't be expanded.
	 */
	public static IExpr expandAll(final IAST ast, IExpr patt, boolean expandNegativePowers, boolean distributePlus,
			EvalEngine engine) {
		if (patt != null && ast.isFree(patt, true)) {
			return F.NIL;
		}
		IAST localAST = ast;
		IAST tempAST = F.NIL;
		if (localAST.isEvalFlagOff(IAST.IS_SORTED)) {
			tempAST = engine.evalFlatOrderlessAttributesRecursive(localAST);
			if (tempAST.isPresent()) {
				localAST = tempAST;
			}
		}
		if (localAST.isAllExpanded() && expandNegativePowers && !distributePlus) {
			if (localAST != ast) {
				return localAST;
			}
			return F.NIL;
		}
		IASTAppendable[] result = new IASTAppendable[1];
		result[0] = F.NIL;
		IExpr temp = F.NIL;

		int localASTSize = localAST.size();
		IExpr head = localAST.head();
		if (head.isAST()) {
			temp = expandAll((IAST) head, patt, expandNegativePowers, distributePlus, engine);
			temp.ifPresent(x -> result[0] = F.ast(x, localASTSize, false));
		}
		final IAST localASTFinal = localAST;
		localAST.forEach((x, i) -> {
			if (x.isAST()) {
				IExpr t = expandAll((IAST) x, patt, expandNegativePowers, distributePlus, engine);
				if (t.isPresent()) {
					if (!result[0].isPresent()) {
						int size = localASTSize;
						if (t.isAST()) {
							size += ((IAST) t).size();
						}
						result[0] = F.ast(head, size, false);
						result[0].appendArgs(localASTFinal, i);
					}
					appendPlus(result[0], t);
					return;
				}
			}
			result[0].ifAppendable(r -> r.append(x));
		});

		if (!result[0].isPresent()) {
			temp = expand(localAST, patt, expandNegativePowers, distributePlus, true);
			if (temp.isPresent()) {
				ExpandAll.setAllExpanded(temp, expandNegativePowers, distributePlus);
				return temp;
			} else {
				if (localAST != ast) {
					ExpandAll.setAllExpanded(localAST, expandNegativePowers, distributePlus);
					return localAST;
				}
			}
			ExpandAll.setAllExpanded(ast, expandNegativePowers, distributePlus);
			return F.NIL;
		}
		temp = expand(result[0], patt, expandNegativePowers, distributePlus, true);
		if (temp.isPresent()) {
			return ExpandAll.setAllExpanded(temp, expandNegativePowers, distributePlus);
		}
		return ExpandAll.setAllExpanded(result[0], expandNegativePowers, distributePlus);
	}

	/**
	 * Factor the <code>expr</code> in the domain of GaussianIntegers.
	 * 
	 * @param expr
	 *            the (polynomial) expression which should be factored
	 * @param varList
	 *            the list of variables
	 * @param head
	 *            the head of the factorization result AST (typically <code>F.Times</code> or <code>F.List</code>)
	 * @param gaussianIntegers
	 *            if <code>true</code> use Gaussian integers
	 * @param engine
	 *            the evaluation engine
	 * @return
	 * @throws JASConversionException
	 */
	public static IExpr factorComplex(IExpr expr, List<IExpr> varList, ISymbol head, boolean gaussianIntegers,
			EvalEngine engine) {
		return factorComplex(expr, varList, head, false, gaussianIntegers, engine);
	}

	/**
	 * Factor the <code>expr</code> in the domain of GaussianIntegers.
	 * 
	 * @param expr
	 *            the (polynomial) expression which should be factored
	 * @param varList
	 *            the list of variables
	 * @param head
	 *            the head of the factorization result AST (typically <code>F.Times</code> or <code>F.List</code>)
	 * @param numeric2Rational
	 *            transform numerical values to symbolic rational numbers
	 * @param gaussianIntegers
	 *            if <code>true</code> use Gaussian integers
	 * @param engine
	 * @return
	 * @throws JASConversionException
	 */
	private static IExpr factorComplex(IExpr expr, List<IExpr> varList, ISymbol head, boolean numeric2Rational,
			boolean gaussianIntegers, EvalEngine engine) {
		try {
			if (gaussianIntegers) {
				ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
				JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(varList, cfac);
				GenPolynomial<Complex<BigRational>> polyRat = jas.expr2JAS(expr, numeric2Rational);
				return engine.evaluate(factorComplex((IAST) expr, polyRat, jas, head, cfac));
			} else {
				JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
				GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numeric2Rational);
				return factorRational(polyRat, jas, head);
				// IExpr temp = SymjaRings.FactorOverQ((IAST) expr, false);
				// if (temp != null && temp.isPresent()) {
				// return temp;
				// }
			}
		} catch (RuntimeException rex) {
			if (Config.SHOW_STACKTRACE) {
				rex.printStackTrace();
			}
		}
		return expr;
	}

	/**
	 * 
	 * @param polyRat
	 *            the rational polynomial which should be factored
	 * @param jas
	 * @param head
	 *            the head of the factorization result AST (typically <code>F.Times</code> or <code>F.List</code>)
	 * @return
	 */
	// public static IAST factorRational(IExpr expr, GenPolynomial<BigRational> polyRat, JASConvert<BigRational> jas,
	// ISymbol head) {
	// Factorization<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
	// SortedMap<GenPolynomial<BigRational>, Long> map = factorAbstract.factors(polyRat);
	//
	// IASTAppendable result = F.ast(head, map.size(), false);
	// for (SortedMap.Entry<GenPolynomial<BigRational>, Long> entry : map.entrySet()) {
	// if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
	// continue;
	// }
	// IExpr key = jas.rationalPoly2Expr(entry.getKey(), false) ;
	// result.append(F.Power(key, F.ZZ(entry.getValue())));
	// }
	// return result;
	//
	//
	//// // boolean noGCDLCM) {
	//// TermOrder termOrder = TermOrderByName.INVLEX;
	//// final String[] vars = polyRat.ring.getVars();
	//// Object[] objects = JASConvert.rationalFromRationalCoefficientsFactor(
	//// new GenPolynomialRing<BigRational>(BigRational.ZERO, vars, termOrder), polyRat);
	//// // java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
	//// // java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
	//// GenPolynomial<BigRational> poly = (GenPolynomial<BigRational>) objects[2];
	////
	//// ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
	//// GenPolynomialRing<Complex<BigRational>> cpfac = new GenPolynomialRing<Complex<BigRational>>(cfac, vars.length,
	//// termOrder);
	//// GenPolynomial<Complex<BigRational>> complexPolynomial = PolyUtil.complexFromAny(cpfac, poly);
	////
	//// // IASTAppendable result = F.ast(head);
	//// // if (!noGCDLCM) {
	//// // if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
	//// // result.append(F.fraction(gcd, lcm));
	//// // }
	//// // }
	//// return factorComplex((IAST) expr, complexPolynomial, jas, head, cfac);
	// }

	/**
	 * 
	 * @param polynomial
	 *            the complex-rational polynomial which should be factored
	 * @param jas
	 * @param head
	 *            the head of the factorization result AST (typically <code>F.Times</code> or <code>F.List</code>)
	 * @param cfac
	 * @return
	 */
	private static IAST factorComplex(IAST expr, GenPolynomial<Complex<BigRational>> polynomial,
			JASConvert<? extends RingElem<?>> jas, ISymbol head, ComplexRing<BigRational> cfac) {
		FactorComplex<BigRational> factorAbstract = new FactorComplex<BigRational>(cfac);
		SortedMap<GenPolynomial<Complex<BigRational>>, Long> map = factorAbstract.factors(polynomial);

		IASTAppendable result = F.ast(head, map.size(), false);
		for (SortedMap.Entry<GenPolynomial<Complex<BigRational>>, Long> entry : map.entrySet()) {
			if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
				continue;
			}
			IExpr key = jas.complexPoly2Expr(entry.getKey());
			if (entry.getValue().equals(1L) && map.size() <= 2 && (key.equals(F.CNI) || key.equals(F.CI))) {
				// hack: factoring -I and I out of an expression should give no new factorized expression
				return expr;
			}
			result.append(F.Power(jas.complexPoly2Expr(entry.getKey()), F.ZZ(entry.getValue())));
		}
		return result;
	}

	private static IAST factorModulus(IExpr expr, List<IExpr> varList, boolean factorSquareFree, IExpr option)
			throws JASConversionException {
		try {
			// found "Modulus" option => use ModIntegerRing
			ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
			JASModInteger jas = new JASModInteger(varList, modIntegerRing);
			GenPolynomial<ModLong> poly = jas.expr2JAS(expr);

			return factorModulus(jas, modIntegerRing, poly, factorSquareFree);
		} catch (ArithmeticException ae) {
			// toInt() conversion failed
			if (Config.DEBUG) {
				ae.printStackTrace();
			}
		}
		return F.NIL;
	}

	/**
	 * 
	 * @param jas
	 * @param modIntegerRing
	 * @param poly
	 * @param factorSquareFree
	 * @return <code>F.NIL</code> if evaluation is impossible.
	 */
	public static IAST factorModulus(JASModInteger jas, ModLongRing modIntegerRing, GenPolynomial<ModLong> poly,
			boolean factorSquareFree) {
		SortedMap<GenPolynomial<ModLong>, Long> map;
		try {
			FactorAbstract<ModLong> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
			if (factorSquareFree) {
				map = factorAbstract.squarefreeFactors(poly);
			} else {
				map = factorAbstract.factors(poly);
			}
		} catch (RuntimeException rex) {
			// JAS may throw RuntimeExceptions
			return F.NIL;
		}
		IASTAppendable result = F.TimesAlloc(map.size());
		for (SortedMap.Entry<GenPolynomial<ModLong>, Long> entry : map.entrySet()) {
			GenPolynomial<ModLong> singleFactor = entry.getKey();
			Long val = entry.getValue();
			result.append(F.Power(jas.modLongPoly2Expr(singleFactor), F.ZZ(val)));
		}
		return result;
	}

	public static IAST factorRational(GenPolynomial<BigRational> polyRat, JASConvert<BigRational> jas, ISymbol head) {
		Object[] objects = jas.factorTerms(polyRat);
		GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
		FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory
				.getImplementation(edu.jas.arith.BigInteger.ONE);
		SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
		map = factorAbstract.factors(poly);
		IASTAppendable result = F.ast(head, map.size() + 1, false);
		java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
		java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
		if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
			result.append(F.fraction(gcd, lcm));
		}
		for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
			if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
				continue;
			}
			if (entry.getValue() == 1L) {
				result.append(jas.integerPoly2Expr(entry.getKey()));
			} else {
				result.append(F.Power(jas.integerPoly2Expr(entry.getKey()), F.ZZ(entry.getValue())));
			}
		}
		return result;
	}

	/**
	 * Split the expression into numerator and denominator parts, by calling the <code>Numerator[]</code> and
	 * <code>Denominator[]</code> functions
	 * 
	 * @param ast
	 * @return an array with the numerator, denominator and the evaluated <code>Together[expr]</code>.
	 */
	public static IExpr[] getNumeratorDenominator(IAST ast, EvalEngine engine) {
		IExpr[] result = new IExpr[3];
		result[2] = together(ast, engine);
		// split expr into numerator and denominator
		result[1] = engine.evaluate(F.Denominator(result[2]));
		if (!result[1].isOne()) {
			// search roots for the numerator expression
			result[0] = engine.evaluate(F.Numerator(result[2]));
		} else {
			result[0] = ast; // result[2];
		}
		return result;
	}

	/**
	 * Return the numerator and denominator for the given <code>Times[...]</code> or <code>Power[a, b]</code> AST, by
	 * separating positive and negative powers.
	 * 
	 * @param timesPower
	 *            a Times[] or Power[] expression (a*b*c....) or a^b
	 * @param splitNumeratorOne
	 *            split a fractional number into numerator and denominator, only if the numerator is 1, if
	 *            <code>true</code>, ignore <code>splitFractionalNumbers</code> parameter.
	 * @param splitFractionalNumbers
	 *            split a fractional number into numerator and denominator
	 * @param trig
	 *            try to find a trigonometric numerator/denominator form (Example: <code>Csc[x]</code> gives
	 *            <code>1 / Sin[x]</code>)
	 * @param evalParts
	 *            evaluate the determined numerator and denominator parts
	 * @param negateNumerDenom
	 *            negate numerator and denominator, if they are both negative
	 * @param splitPowerPlusExponents
	 *            split <code>Power()</code> expressions with <code>Plus()</code> exponents like <code>a^(-x+y)</code>
	 *            into numerator <code>a^y</code> and denominator <code>a^x</code>
	 * @return the numerator and denominator expression and an optional fractional number (maybe <code>null</code>), if
	 *         splitNumeratorOne is <code>true</code>.
	 */
	public static IExpr[] fractionalPartsTimesPower(final IAST timesPower, boolean splitNumeratorOne,
			boolean splitFractionalNumbers, boolean trig, boolean evalParts, boolean negateNumerDenom,
			boolean splitPowerPlusExponents) {
		if (timesPower.isPower()) {
			IExpr[] parts = Apart.fractionalPartsPower(timesPower, trig, splitPowerPlusExponents);
			if (parts != null) {
				return parts;
			}
			return null;
		}

		IAST timesAST = timesPower;
		IExpr[] result = new IExpr[3];
		result[2] = null;
		IASTAppendable numerator = F.TimesAlloc(timesAST.size());
		IASTAppendable denominator = F.TimesAlloc(timesAST.size());

		IExpr arg;
		IAST argAST;
		boolean evaled = false;
		boolean splitFractionEvaled = false;
		for (int i = 1; i < timesAST.size(); i++) {
			arg = timesAST.get(i);
			if (arg.isAST()) {
				argAST = (IAST) arg;
				if (trig && argAST.isAST1()) {
					IExpr numerForm = Numerator.getTrigForm(argAST, trig);
					if (numerForm.isPresent()) {
						IExpr denomForm = Denominator.getTrigForm(argAST, trig);
						if (denomForm.isPresent()) {
							if (!numerForm.isOne()) {
								numerator.append(numerForm);
							}
							if (!denomForm.isOne()) {
								denominator.append(denomForm);
							}
							evaled = true;
							continue;
						}
					}
				} else if (arg.isPower()) {
					IExpr[] parts = Apart.fractionalPartsPower((IAST) arg, trig, splitPowerPlusExponents);
					if (parts != null) {
						if (!parts[0].isOne()) {
							numerator.append(parts[0]);
						}
						if (!parts[1].isOne()) {
							denominator.append(parts[1]);
						}
						evaled = true;
						continue;
					}
				}
			} else if (i == 1 && arg.isFraction()) {
				if (splitNumeratorOne) {
					IFraction fr = (IFraction) arg;
					if (fr.numerator().isOne()) {
						denominator.append(fr.denominator());
						splitFractionEvaled = true;
						continue;
					}
					if (fr.numerator().isMinusOne()) {
						numerator.append(fr.numerator());
						denominator.append(fr.denominator());
						splitFractionEvaled = true;
						continue;
					}
					result[2] = fr;
					continue;
				} else if (splitFractionalNumbers) {
					IFraction fr = (IFraction) arg;
					if (!fr.numerator().isOne()) {
						numerator.append(fr.numerator());
					}
					denominator.append(fr.denominator());
					evaled = true;
					continue;
				}
			}
			numerator.append(arg);
		}
		if (evaled) {
			if (evalParts) {
				result[0] = F.eval(numerator);
				result[1] = F.eval(denominator);
			} else {
				result[0] = numerator.oneIdentity1();
				result[1] = denominator.oneIdentity1();
			}
			if (negateNumerDenom && result[0].isNegative() && result[1].isPlus() && ((IAST) result[1]).isAST2()) {
				// negate numerator and denominator:
				result[0] = result[0].negate();
				result[1] = result[1].negate();
			}
			return result;
		}
		if (splitFractionEvaled) {
			result[0] = numerator.oneIdentity1();
			if (!result[0].isTimes() && !result[0].isPlus()) {
				result[1] = denominator.oneIdentity1();
				return result;
			}
			if (result[0].isTimes() && ((IAST) result[0]).isAST2() && ((IAST) result[0]).arg1().isMinusOne()) {
				result[1] = denominator.oneIdentity1();
				return result;
			}
		}
		return null;
	}

	/**
	 * Split the expression into numerator and denominator parts, by separating positive and negative powers.
	 * 
	 * @param arg
	 * @param trig
	 *            determine the denominator by splitting up functions like <code>Tan[],Cot[], Csc[],...</code>
	 * @return the numerator and denominator expression or <code>null</code> if no denominator was found.
	 */
	public static IExpr[] fractionalParts(final IExpr arg, boolean trig) {
		IExpr[] parts = null;
		if (arg.isAST()) {
			IAST ast = (IAST) arg;
			if (arg.isTimes()) {
				parts = fractionalPartsTimesPower(ast, false, true, trig, true, true, true);
			} else if (arg.isPower()) {
				parts = Apart.fractionalPartsPower(ast, trig, true);
			} else {
				IExpr numerForm = Numerator.getTrigForm(ast, trig);
				if (numerForm.isPresent()) {
					IExpr denomForm = Denominator.getTrigForm(ast, trig);
					if (denomForm.isPresent()) {
						parts = new IExpr[2];
						parts[0] = numerForm;
						parts[1] = denomForm;
						return parts;
					}
				}
			}
		}
		return parts;
	}

	// public static IExpr partialFractionDecomposition(IExprPartialFractionGenerator pf, IExpr[] parts, IExpr variable)
	// {
	// try {
	// IAST variableList = F.List(variable);
	// IExpr exprNumerator = F.evalExpandAll(parts[0]);
	// IExpr exprDenominator = F.evalExpandAll(parts[1]);
	// // ASTRange r = new ASTRange(variableList, 1);
	// // List<IExpr> varList = r;
	// List<IExpr> varList = variableList.copyTo();
	//
	// String[] varListStr = new String[1];
	// varListStr[0] = variableList.arg1().toString();
	// // JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
	// // GenPolynomial<BigRational> numerator = jas.expr2JAS(exprNumerator, false);
	// // GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);
	// JASIExpr jas = new JASIExpr(varList, ExprRingFactory.CONST);
	// GenPolynomial<IExpr> numerator = jas.expr2IExprJAS(exprNumerator);
	// GenPolynomial<IExpr> denominator = jas.expr2IExprJAS(exprDenominator);
	//
	// // get factors
	// FactorAbstract<IExpr> factorAbstract = FactorFactory.getImplementation(ExprRingFactory.CONST);
	// SortedMap<GenPolynomial<IExpr>, Long> sfactors = factorAbstract.baseFactors(denominator);
	//
	// List<GenPolynomial<IExpr>> D = new ArrayList<GenPolynomial<IExpr>>(sfactors.keySet());
	//
	// SquarefreeAbstract<IExpr> sqf = SquarefreeFactory.getImplementation(ExprRingFactory.CONST);
	// List<List<GenPolynomial<IExpr>>> Ai = sqf.basePartialFraction(numerator, sfactors);
	// // returns [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) =
	// // A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).
	//
	// if (Ai.size() > 0) {
	// // IAST result = F.Plus();
	// pf.allocPlus(Ai.size() * 2);
	// pf.setJAS(jas);
	// if (!Ai.get(0).get(0).isZERO()) {
	// pf.addNonFractionalPart(Ai.get(0).get(0));
	// }
	// for (int i = 1; i < Ai.size(); i++) {
	// List<GenPolynomial<IExpr>> list = Ai.get(i);
	// int j = 0;
	// for (GenPolynomial<IExpr> genPolynomial : list) {
	// if (!genPolynomial.isZERO()) {
	// GenPolynomial<IExpr> Di_1 = D.get(i - 1);
	// pf.addSinglePartialFraction(genPolynomial, Di_1, j);
	// }
	// j++;
	// }
	//
	// }
	// return pf.getResult();
	// }
	// } catch (JASConversionException e) {
	// if (Config.DEBUG) {
	// e.printStackTrace();
	// }
	// }
	// return F.NIL;
	// }

	/**
	 * If possible returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition of the
	 * numerator and denominator parts.
	 * 
	 * @param parts
	 *            numerator and denominator parts
	 * @param variable
	 * @param engine
	 * @return
	 */
	public static IExpr partsApart(IExpr[] parts, IExpr variable, EvalEngine engine) {
		IExpr temp = partialFractionDecompositionRational(new PartialFractionGenerator(), parts, variable);
		if (temp.isPresent()) {
			return temp;
		}
		temp = F.Factor.of(parts[1]);
		if (temp.isTimes()) {
			return partialFractionDecomposition(parts[0], temp, variable, 0, engine);
		}
		return F.NIL;
	}

	/**
	 * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition of the numerator
	 * and denominator parts.
	 * 
	 * @param pf
	 *            partial fraction generator
	 * @param parts
	 * @param variable
	 *            a variable
	 * @return <code>F.NIL</code> if the partial fraction decomposition wasn't constructed
	 */
	public static IExpr partialFractionDecompositionRational(IPartialFractionGenerator pf, IExpr[] parts,
			IExpr variable) {
		return partialFractionDecompositionRational(pf, parts, F.List(variable));
	}

	/**
	 * Create a (recursive) partial fraction decomposition of the expression <code>numerator / Times( ... )</code> for
	 * the given <code>variable</code>
	 * 
	 * @param numerator
	 *            the numerator of the fraction expression
	 * @param denominatorTimes
	 *            the <codeTimes( ... )</code> expression of the denominator of the fraction expression
	 * @param variable
	 * @param count
	 *            the recursion level
	 * @param engine
	 * @return the partial fraction decomposition is possible
	 */
	public static IExpr partialFractionDecomposition(IExpr numerator, IExpr denominatorTimes, IExpr variable, int count,
			EvalEngine engine) {
		if (!denominatorTimes.isTimes()) {
			return F.Times.of(engine, numerator, F.Power(denominatorTimes, -1));
		}

		// denominator is Times() here:
		IExpr first = denominatorTimes.first();
		IExpr rest = denominatorTimes.rest().oneIdentity1();
		if (first.isFree(variable)) {
			return F.Times.of(engine, F.Power(first, -1),
					partialFractionDecomposition(numerator, rest, variable, count + 1, engine));
		} else {
			IExpr v1 = F.Expand.of(engine, first);
			IExpr v2 = F.Expand.of(engine, rest);
			IExpr peGCD = F.PolynomialExtendedGCD.of(engine, v1, v2, variable);
			if (peGCD.isList() && peGCD.second().isList()) {
				IAST s = (IAST) peGCD.second();
				IExpr A = s.arg1();
				IExpr B = s.arg2();
				// IExpr u1 = F.PolynomialRemainder.of(engine, F.Expand(F.Times(B, numerator)), v1, variable);
				// IExpr u2 = F.PolynomialRemainder.of(engine, F.Expand(F.Times(A, numerator)), v2, variable);
				// return F.Plus.of(engine, F.Times(u1, F.Power(first, -1)),
				// partialFractionDecomposition(u2, rest, variable, count + 1, engine));

				IExpr u1 = F.PolynomialRemainder.ofNIL(engine, F.Expand(F.Times(B, numerator)), v1, variable);
				if (u1.isPresent()) {
					IExpr u2 = F.PolynomialRemainder.ofNIL(engine, F.Expand(F.Times(A, numerator)), v2, variable);
					if (u2.isPresent()) {
						return F.Plus.of(engine, F.Times(u1, F.Power(first, -1)),
								partialFractionDecomposition(u2, rest, variable, count + 1, engine));
					}
				}
				if (count == 0) {
					return F.NIL;
				}
				return F.Times.of(engine, numerator, F.Power(denominatorTimes, -1));
			}
		}
		if (count == 0) {
			return F.NIL;
		}
		return F.Times.of(engine, numerator, F.Power(denominatorTimes, -1));
	}

	/**
	 * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition of the numerator
	 * and denominator parts.
	 * 
	 * @param pf
	 *            partial fraction generator
	 * @param parts
	 * @param variableList
	 *            a list of variable
	 * @return <code>F.NIL</code> if the partial fraction decomposition wasn't constructed
	 */
	public static IExpr partialFractionDecompositionRational(IPartialFractionGenerator pf, IExpr[] parts,
			IAST variableList) {
		try {
			IExpr exprNumerator = F.evalExpandAll(parts[0]);
			IExpr exprDenominator = F.evalExpandAll(parts[1]);
			// ASTRange r = new ASTRange(variableList, 1);
			// List<IExpr> varList = r;
			List<IExpr> varList = variableList.copyTo();

			String[] varListStr = new String[1];
			varListStr[0] = variableList.arg1().toString();
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> numerator = jas.expr2JAS(exprNumerator, false);
			GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);

			// get factors
			FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ZERO);
			SortedMap<GenPolynomial<BigRational>, Long> sfactors = factorAbstract.baseFactors(denominator);

			List<GenPolynomial<BigRational>> D = new ArrayList<GenPolynomial<BigRational>>(sfactors.keySet());

			SquarefreeAbstract<BigRational> sqf = SquarefreeFactory.getImplementation(BigRational.ZERO);
			List<List<GenPolynomial<BigRational>>> Ai = sqf.basePartialFraction(numerator, sfactors);
			// returns [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) =
			// A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).

			if (Ai.size() > 0) {
				// IAST result = F.Plus();
				pf.allocPlus(Ai.size() * 2);
				pf.setJAS(jas);
				if (!Ai.get(0).get(0).isZERO()) {
					pf.addNonFractionalPart(Ai.get(0).get(0));
				}
				for (int i = 1; i < Ai.size(); i++) {
					List<GenPolynomial<BigRational>> list = Ai.get(i);
					int j = 0;
					for (GenPolynomial<BigRational> genPolynomial : list) {
						if (!genPolynomial.isZERO()) {
							GenPolynomial<BigRational> Di_1 = D.get(i - 1);
							pf.addSinglePartialFraction(genPolynomial, Di_1, j);
						}
						j++;
					}

				}
				return pf.getResult();
			}
		} catch (JASConversionException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		} catch (RuntimeException e) {
			// JAS may throw RuntimeExceptions
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return F.NIL;
	}

	/**
	 * Split the expression into numerator and denominator parts, by separating positive and negative powers.
	 * 
	 * @param arg
	 * @return the numerator and denominator expression
	 */
	public static IExpr[] fractionalPartsRational(final IExpr arg) {
		if (arg.isFraction()) {
			IFraction fr = (IFraction) arg;
			IExpr[] parts = new IExpr[2];
			parts[0] = fr.numerator();
			parts[1] = fr.denominator();
			return parts;
		} else if (arg.isComplex()) {
			IRational re = ((IComplex) arg).getRealPart();
			IRational im = ((IComplex) arg).getImaginaryPart();
			if (re.isFraction() || im.isFraction()) {
				IExpr[] parts = new IExpr[2];
				parts[0] = re.numerator().times(im.denominator())
						.add(im.numerator().times(re.denominator()).times(F.CI));
				parts[1] = re.denominator().times(im.denominator());
				return parts;
			}
			return null;
		}
		return fractionalParts(arg, false);
	}

	public static IExpr together(IAST ast, EvalEngine engine) {
		IExpr result = Together.togetherExpr(ast, engine);
		if (result.isPresent()) {
			return engine.evaluate(result);
		}
		return ast;
		// IExpr temp = expandAll(ast, null, true, false, engine);
		// if (!temp.isPresent()) {
		// temp = ast;
		// }
		// if (temp.isAST()) {
		// IExpr result = Together.togetherPlusTimesPower((IAST) temp, engine);
		// if (result.isPresent()) {
		// return engine.evaluate(result);
		// }
		// }
		// return temp;
	}

	/**
	 * If AST structures are available in the variableList create dummy variables and replace these expressions in
	 * polyExpr.
	 * 
	 * @param polyExpr
	 * @param variablesList
	 *            a list of variables, which aren't necessarily symbols
	 * @param dummyStr
	 * @return <code>F.List(polyExpr, substitutedVariableList)</code>
	 */
	public static IAST substituteVariablesInPolynomial(IExpr polyExpr, IAST variablesList, String dummyStr) {
		IASTAppendable substitutedVariableList = F.ListAlloc(variablesList.size());
		for (int i = 1; i < variablesList.size(); i++) {
			IExpr listArg = variablesList.get(i);
			if (listArg.isAST() && !listArg.isPower()) {
				ISymbol dummy = F.Dummy(dummyStr + i);
				polyExpr = F.subst(polyExpr, F.Rule(listArg, dummy));
				substitutedVariableList.append(dummy);
			} else {
				substitutedVariableList.append(listArg);
			}
		}
		return F.List(polyExpr, substitutedVariableList);
	}

	public static void initialize() {
		Initializer.init();
	}

	private Algebra() {

	}

}
