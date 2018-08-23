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
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PlusOp;
import org.matheclipse.core.eval.PowerOp;
import org.matheclipse.core.eval.TimesOp;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
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
import org.matheclipse.core.patternmatching.PatternMatcherEvalEngine;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherPlus;
import org.matheclipse.core.patternmatching.hash.HashedPatternRules;
import org.matheclipse.core.polynomials.ExprMonomial;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.IPartialFractionGenerator;
import org.matheclipse.core.polynomials.PartialFractionGenerator;
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
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorComplex;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;

public class Algebra {
	static {
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
		 *            TODO
		 * @return the numerator and denominator expression
		 */
		public static IExpr[] fractionalPartsPower(final IAST powerAST, boolean trig) {
			IExpr[] parts = new IExpr[2];
			parts[0] = F.C1;

			IExpr arg1 = powerAST.arg1();
			IExpr exponent = powerAST.arg2();
			if (exponent.isReal()) {
				ISignedNumber sn = (ISignedNumber) exponent;
				if (sn.isMinusOne()) {
					parts[1] = arg1;
					return parts;
				} else if (sn.isNegative()) {
					parts[1] = F.Power(arg1, sn.negate());
					return parts;
				} else {
					if (sn.isInteger() && arg1.isAST()) {
						// positive integer
						IAST function = (IAST) arg1;
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
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(exponent);
			if (negExpr.isPresent()) {
				parts[1] = F.Power(arg1, negExpr);
				return parts;
			}
			return null;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			final IExpr arg1 = ast.arg1();
			IAST tempAST = Structure.threadLogicEquationOperators(arg1, ast, 1);
			if (tempAST.isPresent()) {
				return tempAST;
			}

			IAST variableList = null;
			if (ast.isAST2()) {
				variableList = Validate.checkSymbolOrSymbolList(ast, 2);
			} else {
				VariablesSet eVar = new VariablesSet(arg1);
				if (eVar.isSize(0)) {
					return arg1;
				}
				if (!eVar.isSize(1)) {
					// partial fraction only possible for univariate polynomials
					return arg1;
				}
				variableList = eVar.getVarList();
			}

			if (arg1.isTimes() || arg1.isPower()) {
				IExpr[] parts = fractionalParts(arg1, false);
				if (parts != null) {
					IExpr temp = partialFractionDecompositionRational(new PartialFractionGenerator(), parts,
							variableList);
					if (temp.isPresent()) {
						return temp;
					}
					// temp = F.Factor.of(parts[1]);
					// if (temp.isTimes()) {
					// System.out.println(temp.toString());
					// }
				}
			}
			return arg1;

			// return F.NIL;
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
				if (denParts.isPresent() && !denParts.get(1).isOne()) {
					IExpr[] result = cancelGCD(numParts.get(1), denParts.get(1));
					if (result != null) {
						return F.Times(result[0], result[1], numParts.get(2),
								F.Power(F.Times(result[2], denParts.get(2)), F.CN1));
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
					if (numParts.isPresent() && !numParts.get(1).isOne()) {
						p00 = numParts.get(1);
						p01 = numParts.get(2);
					}
				}

				if (p10.isPlus()) {
					IAST denParts = ((IAST) p10).partitionPlus(new PolynomialPredicate(), F.C0, F.C1, F.List);
					if (denParts.isPresent() && !denParts.get(1).isOne()) {
						p10 = denParts.get(1);
						p11 = denParts.get(2);
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

		public static IExpr[] cancelQuotientRemainder(final IExpr arg1, IExpr arg2, IExpr variable) {
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
					result[0] = jas.rationalPoly2Expr(divRem[0]);
					result[1] = F.C1;
				} else {
					GenPolynomial<BigRational>[] divRem = poly2.quotientRemainder(poly1);
					if (!divRem[1].isZERO()) {
						return null;
					}
					result[0] = F.C1;
					result[1] = jas.rationalPoly2Expr(divRem[0]);
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
			Validate.checkSize(ast, 2);

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

		private IExpr cancelFractionPowers(EvalEngine engine, IExpr arg1) {
			IExpr temp;
			IExpr[] parts = fractionalParts(arg1, false);
			if (parts != null && //
					((parts[0].isPower() && parts[0].exponent().isInteger()) //
							|| (parts[1].isPower() && parts[1].exponent().isInteger()))) {
				IExpr numer = parts[0];
				int numerExponent = 1;
				IExpr denom = parts[1];
				int denomExponent = 1;
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
	 * Collect(expr, variable)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * collect subexpressions in <code>expr</code> which belong to the same <code>variable</code>.
	 * </p>
	 * </blockquote>
	 */
	private static class Collect extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);
			try {
				IExpr head = null;
				if (ast.isAST3()) {
					head = engine.evaluate(ast.arg3());
				}
				final IExpr arg1 = F.expandAll(ast.arg1(), true, true);
				IAST temp = Structure.threadLogicEquationOperators(arg1, ast, 1);
				if (temp.isPresent()) {
					return temp;
				}
				final IExpr arg2 = engine.evalPattern(ast.arg2());
				if (!arg2.isList()) {
					return collectSingleVariable(arg1, arg2, null, 1, head, engine);
				}
				IAST list = (IAST) arg2;
				if (list.size() > 1) {
					return collectSingleVariable(arg1, list.arg1(), (IAST) arg2, 2, head, engine);
				}
			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
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

				IPatternMatcher matcher = new PatternMatcherEvalEngine(x, engine);
				collectToMap(poly, matcher, map, rest);
				if (listOfVariables != null && listPosition < listOfVariables.size()) {
					// collect next pattern in sub-expressions
					IASTAppendable result = F.PlusAlloc(map.size() + 1);
					if (rest.size() > 1) {
						result.append(collectSingleVariable(rest.getOneIdentity(F.C0),
								listOfVariables.get(listPosition), listOfVariables, listPosition + 1, head, engine));
					}
					for (Entry<IExpr, IASTAppendable> entry : map.entrySet()) {
						IExpr temp = collectSingleVariable(entry.getValue().getOneIdentity(F.C0),
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
				return rest.getOneIdentity(F.C0);
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
						IAST clone = timesAST.removeAtClone(i);
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
	private static class Denominator extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			boolean trig = false;
			if (ast.isAST2()) {
				final Options options = new Options(ast.topHead(), ast, 2, engine);
				IExpr option = options.getOption("Trig");

				if (option.isTrue()) {
					trig = true;
				} else if (!option.isPresent()) {
					throw new WrongArgumentType(ast, ast.get(2), 2, "Option expected!");
				}
			}

			IExpr expr = ast.arg1();
			if (expr.isRational()) {
				return ((IRational) expr).denominator();
			}
			IExpr[] parts = fractionalParts(expr, trig);
			if (parts == null) {
				return F.C1;
			}
			return parts[1];
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
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
			Validate.checkRange(ast, 2, 6);

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

					IExpr[] temp = fractionalPartsTimesPower(ast, false, false, false, evalParts);
					IExpr tempExpr;
					if (temp == null) {
						return expandTimes(ast);
					}
					if (temp[0].isOne()) {
						if (temp[1].isTimes()) {
							tempExpr = expandTimes((IAST) temp[1]);
							if (tempExpr.isPresent()) {
								return PowerOp.power(tempExpr, F.CN1);
							}
							addExpanded(ast);
							return F.NIL;
						}
						if (temp[1].isPower() || temp[1].isPlus()) {
							IExpr denom = expandAST((IAST) temp[1], evalParts);
							if (denom.isPresent()) {
								return PowerOp.power(denom, F.CN1);
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
					IExpr powerAST = PowerOp.power(temp[1], F.CN1);
					if (distributePlus && temp[0].isPlus()) {
						return addExpanded(PlusOp.plus(((IAST) temp[0]).mapThread(F.Times(null, powerAST), 1)));
					}
					if (evaled) {
						return addExpanded(TimesOp.times(temp[0], powerAST));
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
					return PlusOp.plus(result);
				}
				addExpanded(ast);
				return F.NIL;
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

					try {
						int exp = Validate.checkPowerExponent(powerAST);
						IAST plusAST = (IAST) base;
						if (exp < 0) {
							if (expandNegativePowers) {
								exp *= (-1);
								return PowerOp.power(expandPower(plusAST, exp), F.CN1);
							}
							addExpanded(powerAST);
							return F.NIL;
						}
						return expandPower(plusAST, exp);

					} catch (WrongArgumentType e) {
						addExpanded(powerAST);
						return F.NIL;
					}
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
				return PlusOp.plus(expandedResult);
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
					}
					result = expandTimesBinary(result, temp);
				}
				if (evaled == false && timesAST.equals(result)) {
					addExpanded(timesAST);
					return F.NIL;
				}
				return result;
			}

			private IExpr expandTimesBinary(final IExpr expr0, final IExpr expr1) {
				if (expr0.isPlus()) {
					if (!expr1.isPlus()) {
						return expandExprTimesPlus(expr1, (IAST) expr0);
					}
					// assure Plus(...)
					final IAST ast1 = expr1.isPlus() ? (IAST) expr1 : F.Plus(expr1);
					return expandPlusTimesPlus((IAST) expr0, ast1);
				}
				if (expr1.isPlus()) {
					return expandExprTimesPlus(expr0, (IAST) expr1);
				}
				return TimesOp.times(expr0, expr1);
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
				return PlusOp.plus(result);
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
				return PlusOp.plus(result);
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
				IExpr arg = TimesOp.times(expr1, expr2);
				if (arg.isAST()) {
					appendPlus(result, expandAST((IAST) arg, true).orElse(arg));
					return;
				}
				result.append(arg);
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
				final IAST times = F.Times();
				IExpr temp;
				for (int[] indices : perm) {
					final IASTAppendable timesAST = times.copyAppendable();
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
									timesAST.appendArgs(ast.size(),
											i -> PowerOp.power(ast.get(i), F.integer(indices[ki])));
								} else {
									timesAST.append(PowerOp.power(temp, F.integer(indices[k])));
								}
							}

						}
					}
					expandedResult.append(TimesOp.times(timesAST));
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

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

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
			Validate.checkRange(ast, 2, 3);

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
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				return list.mapThread(F.ListAlloc(list.size()), ast, 1);
			}

			IExpr result = F.REMEMBER_AST_CACHE.getIfPresent(ast);
			if (result != null) {
				return result;
			}
			VariablesSet eVar = new VariablesSet(ast.arg1());

			IExpr expr = ast.arg1();
			if (ast.isAST1()) {
				expr = F.Together.of(engine, ast.arg1());
				if (expr.isAST()) {
					IExpr[] parts = Algebra.getNumeratorDenominator((IAST) expr, engine);
					if (!parts[1].isOne()) {
						return F.Divide(F.Factor(parts[0]), F.Factor(parts[1]));
					}
				}
			}

			// ASTRange r = new ASTRange(eVar.getVarList(), 1);
			try {
				// List<IExpr> varList = r;
				List<IExpr> varList = eVar.getVarList().copyTo();

				if (ast.isAST2()) {
					return factorWithOption(ast, expr, varList, false, engine);
				}
				if (expr.isAST()) {
					IExpr temp = factor((IAST) expr, varList, false);
					F.REMEMBER_AST_CACHE.put(ast, temp);
					return temp;
				}
				return expr;

			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
			return expr;
		}

		public static IExpr factor(IAST expr, List<IExpr> varList, boolean factorSquareFree)
				throws JASConversionException {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
			if (polyRat.length() <= 1) {
				return expr;
			}

			Object[] objects = jas.factorTerms(polyRat);
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
					result.append(F.Power(jas.integerPoly2Expr(entry.getKey()), F.integer(entry.getValue())));
				}
			}
			// System.out.println("Factor: " + expr.toString() + " ==> " + result.toString());
			return result.getOneIdentity(F.C0);
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
			final Options options = new Options(ast.topHead(), ast, 2, engine);
			IExpr option = options.getOption("Modulus");
			if (option.isReal()) {
				return factorModulus(expr, varList, factorSquareFree, option);
			}
			option = options.getOption("GaussianIntegers");
			if (option.isTrue()) {
				return factorComplex(expr, varList, F.Times, false, false);
			}
			option = options.getOption("Extension");
			if (option.isImaginaryUnit()) {
				return factorComplex(expr, varList, F.Times, false, false);
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
			Validate.checkRange(ast, 2, 3);

			VariablesSet eVar = new VariablesSet(ast.arg1());
			if (!eVar.isSize(1)) {
				throw new WrongArgumentType(ast, ast.arg1(), 1,
						"Factorization only implemented for univariate polynomials");
			}
			try {
				IExpr expr = F.evalExpandAll(ast.arg1(), engine);
				// ASTRange r = new ASTRange(eVar.getVarList(), 1);
				// List<IExpr> varList = r;
				List<IExpr> varList = eVar.getVarList().copyTo();

				if (ast.isAST2()) {
					return factorWithOption(ast, expr, varList, true, engine);
				}
				if (expr.isAST()) {
					return factor((IAST) expr, varList, true);
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
			Validate.checkSize(ast, 2);

			VariablesSet eVar = new VariablesSet(ast.arg1());
			if (!eVar.isSize(1)) {
				throw new WrongArgumentType(ast, ast.arg1(), 1,
						"Factorization only implemented for univariate polynomials");
			}
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
				result.append(F.List(jas.integerPoly2Expr(entry.getKey()), F.integer(entry.getValue())));
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
			Validate.checkRange(ast, 2, 3);

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
					VariablesSet eVar;
					eVar = new VariablesSet(ast.arg1());
					if (!eVar.isSize(1)) {
						// only possible for univariate polynomials
						return F.NIL;
					}
					variableList = eVar.getVarList();
				}
			}
			if (!variableList.isPresent() || variableList.size() != 2) {
				// FactorTerms only possible for univariate polynomials
				return F.NIL;
			}
			// ASTRange r = new ASTRange(variableList, 1);
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
				try {
					if (variableList.isAST1()) {
						IAST list = PolynomialFunctions.rootsOfExprPolynomial(expr, variableList, true);
						if (list.isList()) {
							IExpr x = variableList.arg1();
							IASTAppendable result = F.TimesAlloc(list.size());
							for (int i = 1; i < list.size(); i++) {
								result.append(F.Plus(x, list.get(i)));
							}
							return result;
						}
					}
				} catch (JASConversionException e2) {
					if (Config.SHOW_STACKTRACE) {
						e2.printStackTrace();
					}
				}

			}
			return ast.arg1();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT);
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
			Validate.checkRange(ast, 2, 3);
			return super.evaluate(ast, engine);
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
	private static class Numerator extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			boolean trig = false;
			if (ast.isAST2()) {
				final Options options = new Options(ast.topHead(), ast, 2, engine);
				IExpr option = options.getOption("Trig");

				if (option.isTrue()) {
					trig = true;
				} else if (!option.isPresent()) {
					throw new WrongArgumentType(ast, ast.get(2), 2, "Option expected!");
				}
			}

			IExpr arg = ast.arg1();
			if (arg.isRational()) {
				return ((IRational) arg).numerator();
			}
			IExpr[] parts = fractionalParts(arg, trig);
			if (parts == null) {
				return arg;
			}
			return parts[0];
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
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
			Validate.checkRange(ast, 4, 5);

			ISymbol x = Validate.checkSymbolType(ast, 3);
			IExpr expr1 = F.evalExpandAll(ast.arg1(), engine);
			IExpr expr2 = F.evalExpandAll(ast.arg2(), engine);
			VariablesSet eVar = new VariablesSet();
			eVar.add(x);

			// ASTRange r = new ASTRange(eVar.getVarList(), 1);
			if (ast.size() == 5) {
				// List<IExpr> varList = r;
				List<IExpr> varList = eVar.getVarList().copyTo();
				final Options options = new Options(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isReal()) {
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
				list.append(jas.rationalPoly2Expr(result[0]));
				IASTAppendable subList = F.ListAlloc(2);
				subList.append(jas.rationalPoly2Expr(result[1]));
				subList.append(jas.rationalPoly2Expr(result[2]));
				list.append(subList);
				return list;
			} catch (JASConversionException e0) {
				try {
					List<IExpr> varList = eVar.getVarList().copyTo();
					JASIExpr jas = new JASIExpr(varList, ExprRingFactory.CONST);
					GenPolynomial<IExpr> poly1 = jas.expr2IExprJAS(expr1);
					GenPolynomial<IExpr> poly2 = jas.expr2IExprJAS(expr2);
					GenPolynomial<IExpr>[] result = poly1.egcd(poly2);
					IASTAppendable list = F.ListAlloc(2);
					list.append(jas.exprPoly2Expr(result[0], x));
					IASTAppendable subList = F.ListAlloc(2);
					subList.append(F.Together.of(engine, jas.exprPoly2Expr(result[1], x)));
					subList.append(F.Together.of(engine, jas.exprPoly2Expr(result[2], x)));
					list.append(subList);
					return list;
				} catch (JASConversionException e) {
					if (Config.SHOW_STACKTRACE) {
						e.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
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
			Validate.checkRange(ast, 3);

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
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

		private IExpr gcdWithOption(final IAST ast, IExpr expr, VariablesSet eVar, final EvalEngine engine) {
			final Options options = new Options(ast.topHead(), ast, ast.argSize(), engine);
			IExpr option = options.getOption("Modulus");
			if (option.isReal()) {
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
			newSymbol.setAttributes(ISymbol.HOLDALL);
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
			Validate.checkRange(ast, 3);

			VariablesSet eVar = new VariablesSet();
			eVar.addVarList(ast, 1);

			// ASTRange r = new ASTRange(eVar.getVarList(), 1);
			IExpr expr = F.evalExpandAll(ast.arg1(), engine);
			if (ast.size() > 3) {
				final Options options = new Options(ast.topHead(), ast, ast.argSize(), engine);
				IExpr option = options.getOption("Modulus");
				if (option.isReal()) {
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
						if (Config.DEBUG) {
							e.printStackTrace();
						}
						return F.NIL;
					}
				}
			}
			try {
				List<IExpr> varList = eVar.getVarList().copyTo();
				JASConvert<BigInteger> jas = new JASConvert<BigInteger>(varList, BigInteger.ZERO);
				GenPolynomial<BigInteger> poly = jas.expr2JAS(expr, false);
				GenPolynomial<BigInteger> temp;
				GreatestCommonDivisorAbstract<BigInteger> factory = GCDFactory.getImplementation(BigInteger.ZERO);
				for (int i = 2; i < ast.size(); i++) {
					expr = F.evalExpandAll(ast.get(i), engine);
					temp = jas.expr2JAS(expr, false);
					poly = factory.lcm(poly, temp);
				}
				return jas.integerPoly2Expr(poly.monic());
			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
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
				IExpr arg1 = engine.evaluate(ast.arg1());
				IExpr arg2 = engine.evaluate(ast.arg2());
				return F.bool(arg1.isPolynomial(arg2.orNewList()));
			}
			return F.False;
			// if (ast.isAST2()) {
			// IAST temp = engine.evalArgs(ast, ISymbol.NOATTRIBUTE).orElse(ast);
			// return F.bool(temp.arg1().isPolynomial(temp.arg2().orNewList()));
			// }
			// Validate.checkSize(ast, 3);
			// return F.NIL;
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
			Validate.checkRange(ast, 4, 5);
			ISymbol variable = Validate.checkSymbolType(ast, 3);
			IExpr arg1 = F.evalExpandAll(ast.arg1(), engine);
			IExpr arg2 = F.evalExpandAll(ast.arg2(), engine);

			if (ast.size() == 5) {
				final Options options = new Options(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isReal()) {
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

			try {
				JASConvert<BigRational> jas = new JASConvert<BigRational>(variable, BigRational.ZERO);
				GenPolynomial<BigRational> poly1 = jas.expr2JAS(arg1, false);
				GenPolynomial<BigRational> poly2 = jas.expr2JAS(arg2, false);
				GenPolynomial<BigRational>[] divRem = poly1.quotientRemainder(poly2);
				IExpr[] result = new IExpr[2];
				result[0] = jas.rationalPoly2Expr(divRem[0]);
				result[1] = jas.rationalPoly2Expr(divRem[1]);
				return result;
			} catch (JASConversionException e1) {
				try {
					JASIExpr jas = new JASIExpr(variable, ExprRingFactory.CONST);
					GenPolynomial<IExpr> poly1 = jas.expr2IExprJAS(arg1);
					GenPolynomial<IExpr> poly2 = jas.expr2IExprJAS(arg2);
					GenPolynomial<IExpr>[] divRem = poly1.quotientRemainder(poly2);
					IExpr[] result = new IExpr[2];
					result[0] = jas.exprPoly2Expr(divRem[0], variable);
					result[1] = jas.exprPoly2Expr(divRem[1], variable);
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
			Validate.checkRange(ast, 4, 5);
			ISymbol variable = Validate.checkSymbolType(ast, 3);
			IExpr arg1 = F.evalExpandAll(ast.arg1(), engine);
			IExpr arg2 = F.evalExpandAll(ast.arg2(), engine);

			if (ast.size() == 5) {
				final Options options = new Options(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isReal()) {
					IExpr[] result = quotientRemainderModInteger(arg1, arg2, variable, option);
					if (result == null) {
						return F.NIL;
					}
					return F.List(result[0], result[1]);
				}
				return F.NIL;
			}
			IExpr[] result = quotientRemainder(arg1, arg2, variable);
			if (result == null) {
				return F.NIL;
			}
			return F.List(result[0], result[1]);
		}

		public IExpr[] quotientRemainderModInteger(IExpr arg1, IExpr arg2, ISymbol variable, IExpr option) {
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
			Validate.checkRange(ast, 4, 5);
			ISymbol variable = Validate.checkSymbolType(ast, 3);
			IExpr arg1 = F.evalExpandAll(ast.arg1(), engine);
			IExpr arg2 = F.evalExpandAll(ast.arg2(), engine);

			if (ast.size() == 5) {
				final Options options = new Options(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isReal()) {
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
						IAST logResult = timesAST.setAtClone(0, F.Plus);
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
			Validate.checkRange(ast, 2, 3);
			if (ast.arg1().isAST()) {
				boolean assumptions = false;
				if (ast.isAST2()) {
					final Options options = new Options(ast.topHead(), ast, ast.argSize(), engine);
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

		public static IExpr powerExpand(final IAST ast, boolean assumptions) {
			return ast.accept(new PowerExpandVisitor(assumptions)).orElse(ast);
		}

		/** {@inheritDoc} */
		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
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
			IExpr q = Plus(Times(F.CN2, Power(c, 3)), Times(F.C9, b, c, d), Times(F.ZZ(-27L), a, F.Sqr(d)));
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
			IExpr t = F.Sqrt(Plus(Times(F.CN4, Power(Plus(F.Sqr(c), Times(F.CN3, b, d), Times(F.ZZ(12L), a, e)), 3)),
					F.Sqr(Plus(Times(F.CN9, c, Plus(Times(b, d), Times(F.C8, a, e))),
							Times(F.ZZ(27L), Plus(Times(a, F.Sqr(d)), Times(F.Sqr(b), e))), Times(C2, Power(c, 3))))));
			// s = (t + 2*c^3 - 9*c*(b*d + 8*a*e) + 27*(a*d^2 + b^2*e))^(1/3)
			IExpr s = Power(Plus(Times(C2, Power(c, 3)), t, Times(F.CN9, c, Plus(Times(b, d), Times(F.C8, a, e))),
					Times(F.ZZ(27L), Plus(Times(a, F.Sqr(d)), Times(F.Sqr(b), e)))), F.C1D3);

			// eps1 = (1/2)*Sqrt((2^(1/3)*(c^2 - 3*b*d + 12*a*e))/ (3*s*e) + (3*d^2 +
			// 2*2^(2/3)*s*e - 8*c*e)/ (12 e^2))
			IExpr eps1 = Times(C1D2,
					F.Sqrt(Plus(
							Times(F.QQ(1L, 12L),
									Plus(Times(F.C3, F.Sqr(d)), Times(F.CN8, c, e),
											Times(C2, e, s, Power(C2, F.QQ(2L, 3L)))),
									Power(e, -2)),
							Times(F.C1D3, Plus(F.Sqr(c), Times(F.CN3, b, d), Times(F.ZZ(12L), a, e)), Power(C2, F.C1D3),
									Power(e, -1), Power(s, -1)))));

			// u = -((2^(1/3)*s^2 + 2*c^2 - 6*b*d + 24*a*e)/ (2^(2/3)*s*e)) + 8*eps1^2
			IExpr u = Plus(Times(F.C8, F.Sqr(eps1)),
					Times(F.CN1,
							Plus(Times(C2, F.Sqr(c)), Times(F.CN6, b, d), Times(F.ZZ(24L), a, e),
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
					IAST cloned = replacement.setAtClone(1, null);
					return ast.mapThread(cloned, 1);
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
						int k = ((IInteger) ast.arg2()).toInt();
						final IAST variables = F.List(F.Slot1);
						ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables);
						ExprPolynomial polynomial = ring.create(expr, false, true);

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
			 * If <code>true</code> we are in full simplify mode
			 */
			final boolean fFullSimplify;

			public SimplifyVisitor(Function<IExpr, Long> complexityFunction, boolean fullSimplify) {
				super();
				fComplexityFunction = complexityFunction;
				fFullSimplify = fullSimplify;
			}

			private IExpr tryExpandAllTransformation(IAST plusAST, IExpr test) {
				IExpr result = F.NIL;
				long minCounter = fComplexityFunction.apply(plusAST);
				IExpr temp;
				long count;

				try {
					temp = F.evalExpandAll(test);
					count = fComplexityFunction.apply(temp);
					if (count < minCounter) {
						result = temp;
					}
				} catch (WrongArgumentType wat) {
					//
				}

				return result;
			}

			private IExpr tryTransformations(IExpr expr) {
				IExpr result = F.NIL;
				if (expr.isAST()) {
					// try ExpandAll, Together, Apart, Factor to reduce the
					// expression
					long minCounter = fComplexityFunction.apply(expr);
					IExpr temp;
					long count;

					try {
						temp = F.evalExpandAll(expr);
						count = fComplexityFunction.apply(temp);
						if (count < minCounter) {
							minCounter = count;
							result = temp;
						}
					} catch (WrongArgumentType wat) {
						//
					}

					try {
						temp = F.eval(F.Together(expr));
						count = fComplexityFunction.apply(temp);
						if (count < minCounter) {
							minCounter = count;
							result = temp;
						}
					} catch (WrongArgumentType wat) {
						//
					}

					try {
						temp = F.eval(F.Factor(expr));
						count = fComplexityFunction.apply(temp);
						if (count < minCounter) {
							minCounter = count;
							result = temp;
						}
					} catch (WrongArgumentType wat) {
						//
					}

					try {
						temp = F.eval(F.Apart(expr));
						count = fComplexityFunction.apply(temp);
						if (count < minCounter) {
							minCounter = count;
							result = temp;
						}
					} catch (WrongArgumentType wat) {
						//
					}

				}
				return result;

			}

			@Override
			public IExpr visit(IASTMutable ast) {
				IExpr result = F.NIL;
				IExpr temp = visitAST(ast);
				if (temp.isPresent()) {
					long minCounter = fComplexityFunction.apply(ast);
					long count = fComplexityFunction.apply(temp);
					if (count < minCounter) {
						minCounter = count;
						if (temp.isAST()) {
							ast = (IASTMutable) temp;
							result = temp;
						} else {
							return temp;
						}
					}
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
						temp = tryTransformations(basicPlus.getOneIdentity(F.C0));
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
							EvalEngine engine = EvalEngine.get();
							temp = hashRuleMap.evaluateRepeated(ast, engine);
							if (temp.isPresent()) {
								return engine.evaluate(temp);
							}
						}
					}

					return result;

				} else if (ast.isTimes()) {
					IASTAppendable basicTimes = F.TimesAlloc(ast.size());
					IASTAppendable restTimes = F.TimesAlloc(ast.size());
					INumber number = null;
					if (ast.arg1().isNumber()) {
						number = (INumber) ast.arg1();
					}
					IExpr reduced;
					for (int i = 1; i < ast.size(); i++) {
						temp = ast.get(i);
						if (temp.accept(isBasicAST)) {
							if (i != 1 && number != null) {
								if (temp.isPlus()) {
									// <number> * Plus[.....]
									reduced = tryExpandAll(ast, temp, number, i);
									if (reduced.isPresent()) {
										return reduced;
									}
								} else if (temp.isPower() && temp.base().isPlus() && temp.exponent().isMinusOne()) {
									// <number> * Power[Plus[...], -1 ]
									reduced = tryExpandAll(ast, temp.base(), number.inverse(), i);
									if (reduced.isPresent()) {
										return F.Power(reduced, F.CN1);
									}
								}
							}
							basicTimes.append(temp);
						} else {
							restTimes.append(temp);
						}
					}

					if (basicTimes.size() > 1) {
						temp = tryTransformations(basicTimes.getOneIdentity(F.C0));
						if (temp.isPresent()) {
							if (restTimes.isAST0()) {
								return temp;
							}
							return F.Times(temp, restTimes);
						}
					}

					temp = tryTransformations(ast);
					return temp.orElse(result);
				}

				temp = F.evalExpandAll(ast);
				long minCounter = fComplexityFunction.apply(ast);

				long count = fComplexityFunction.apply(temp);
				if (count < minCounter) {
					minCounter = count;
					result = temp;
				}

				if (fFullSimplify) {
					try {
						temp = F.eval(F.FunctionExpand(ast));
						count = fComplexityFunction.apply(temp);
						if (count < minCounter) {
							minCounter = count;
							result = temp;
						}
					} catch (WrongArgumentType wat) {
						//
					}
				}

				return result;
			}

			private IExpr tryExpandAll(IAST ast, IExpr temp, IExpr arg1, int i) {
				IExpr expandedAst = tryExpandAllTransformation((IAST) temp, F.Times(arg1, temp));
				if (expandedAst.isPresent()) {
					IASTAppendable result = F.TimesAlloc(ast.size());
					// ast.range(2, ast.size()).toList(result.args());
					result.appendAll(ast, 2, ast.size());
					result.set(i - 1, expandedAst);
					return result;
				}
				return F.NIL;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2);

			IExpr arg1 = ast.arg1();
			IExpr assumptionExpr = F.NIL;
			IExpr complexityFunctionHead = F.NIL;

			if (ast.size() > 2) {
				IExpr arg2 = ast.arg2();

				if (!arg2.isRule()) {
					assumptionExpr = arg2;
				}
				final Options options = new Options(ast.topHead(), ast, 2, engine);
				IExpr option = options.getOption("Assumptions");
				if (option.isPresent()) {
					assumptionExpr = option;
				}
				complexityFunctionHead = options.getOption("ComplexityFunction");
			}
			if (arg1.isAtom()) {
				return arg1;
			}
			IAssumptions oldAssumptions = engine.getAssumptions();
			try {
				Function<IExpr, Long> complexityFunction = createComplexityFunction(complexityFunctionHead, engine);
				long minCounter = complexityFunction.apply(arg1);
				IExpr result = arg1;
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

				IExpr temp = arg1.replaceAll(F.List(F.Rule(F.GoldenRatio, F.Times(F.C1D2, F.Plus(F.C1, F.Sqrt(F.C5)))),
						F.Rule(F.Degree, F.Divide(F.Pi, F.ZZ(180)))));
				if (temp.isPresent()) {
					arg1 = temp;
				}

				return simplifyStep(arg1, complexityFunction, minCounter, result);

			} catch (ArithmeticException e) {
				//
			} finally {
				engine.setAssumptions(oldAssumptions);
			}
			return F.NIL;
		}

		private IExpr simplifyStep(IExpr arg1, Function<IExpr, Long> complexityFunction, long minCounter,
				IExpr result) {
			long count;
			IExpr temp;
			temp = arg1.accept(new SimplifyVisitor(complexityFunction, isFullSimplifyMode()));
			while (temp.isPresent()) {
				count = complexityFunction.apply(temp);
				if (count < minCounter) {
					minCounter = count;
					result = temp;
					temp = result.accept(new SimplifyVisitor(complexityFunction, isFullSimplifyMode()));
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
				return F.List(c.first(), c.rest());
			}
			return F.List(F.C1, c);
		});

		private static IExpr reduceFactorConstant(IExpr p, EvalEngine engine) {
			if (p.isPlus() && !engine.isTogetherMode()) {
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
				return F.Times.of(engine, c, F.Distribute(F.Divide(e, c)));
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
					return F.eval(result);
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
		 *            a <code>Plus[...]</code> expresion
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
				numerator.set(i, ni.getOneIdentity(F.C1));
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

			IExpr exprNumerator = F.evalExpand(numerator.getOneIdentity(F.C0));
			IExpr denom = F.eval(denominator.getOneIdentity(F.C1));
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
						IExpr temp = F.eval(result);
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
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			IAST list = Structure.threadLogicEquationOperators(arg1, ast, 1);
			if (list.isPresent()) {
				return list;
			}
			if (arg1.isPlusTimesPower()) {
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
			Validate.checkSize(ast, 2);

			return VariablesSet.getVariables(ast.arg1());
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
	 * @param numeratorPolynomial
	 *            a <code>BigRational</code> polynomial which could be converted to JAS polynomial
	 * @param denominatorPolynomial
	 *            a <code>BigRational</code> polynomial which could be converted to JAS polynomial
	 * @return <code>null</code> if the expressions couldn't be converted to JAS polynomials or gcd equals 1
	 * @throws JASConversionException
	 */
	public static IExpr[] cancelGCD(IExpr numeratorPolynomial, IExpr denominatorPolynomial)
			throws JASConversionException {

		try {
			if (denominatorPolynomial.isInteger() && numeratorPolynomial.isPlus()) {
				IExpr[] result = Cancel.cancelPlusIntegerGCD((IAST) numeratorPolynomial,
						(IInteger) denominatorPolynomial);
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
			// ASTRange r = new ASTRange(eVar.getVarList(), 1);
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
				if (JASIExpr.isInexactCoefficient(gcd)) {
					return null;
				}
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
		if ((localAST.getEvalFlags() & IAST.IS_SORTED) != IAST.IS_SORTED) {
			tempAST = engine.evalFlatOrderlessAttributesRecursive(localAST);
			if (tempAST.isPresent()) {
				localAST = tempAST;
			}
		}
		if (localAST.isAllExpanded()) {
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
	 * @param varList
	 * @param head
	 *            the head of the result AST
	 * @param noGCDLCM
	 * @param numeric2Rational
	 *            TODO
	 * @return
	 * @throws JASConversionException
	 */
	public static IAST factorComplex(IExpr expr, List<IExpr> varList, ISymbol head, boolean noGCDLCM,
			boolean numeric2Rational) throws JASConversionException {
		JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
		GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numeric2Rational);
		return factorComplex(polyRat, jas, varList, head, noGCDLCM);
	}

	public static IAST factorComplex(GenPolynomial<BigRational> polyRat, JASConvert<BigRational> jas,
			List<IExpr> varList, ISymbol head, boolean noGCDLCM) {
		TermOrder termOrder = TermOrderByName.Lexicographic;
		String[] vars = new String[varList.size()];
		for (int i = 0; i < varList.size(); i++) {
			vars[i] = varList.get(i).toString();
		}
		Object[] objects = JASConvert.rationalFromRationalCoefficientsFactor(
				new GenPolynomialRing<BigRational>(BigRational.ZERO, varList.size(), termOrder, vars), polyRat);
		java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
		java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
		GenPolynomial<BigRational> poly = (GenPolynomial<BigRational>) objects[2];

		ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
		GenPolynomialRing<Complex<BigRational>> cpfac = new GenPolynomialRing<Complex<BigRational>>(cfac, 1, termOrder);
		GenPolynomial<Complex<BigRational>> a = PolyUtil.complexFromAny(cpfac, poly);
		FactorComplex<BigRational> factorAbstract = new FactorComplex<BigRational>(cfac);
		SortedMap<GenPolynomial<Complex<BigRational>>, Long> map = factorAbstract.factors(a);

		IASTAppendable result = F.ast(head);
		if (!noGCDLCM) {
			if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
				result.append(F.fraction(gcd, lcm));
			}
		}
		GenPolynomial<Complex<BigRational>> temp;
		for (SortedMap.Entry<GenPolynomial<Complex<BigRational>>, Long> entry : map.entrySet()) {
			if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
				continue;
			}
			temp = entry.getKey();
			result.append(F.Power(jas.complexPoly2Expr(entry.getKey()), F.integer(entry.getValue())));
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
			result.append(F.Power(jas.modLongPoly2Expr(singleFactor), F.integer(val)));
		}
		return result;
	}

	public static IAST factorRational(GenPolynomial<BigRational> polyRat, JASConvert<BigRational> jas,
			List<IExpr> varList, ISymbol head) {
		Object[] objects = jas.factorTerms(polyRat);
		GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
		FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory
				.getImplementation(edu.jas.arith.BigInteger.ONE);
		SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
		map = factorAbstract.factors(poly);
		IASTAppendable result = F.ast(head);
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
				result.append(F.Power(jas.integerPoly2Expr(entry.getKey()), F.integer(entry.getValue())));
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
		result[1] = F.eval(F.Denominator(result[2]));
		if (!result[1].isOne()) {
			// search roots for the numerator expression
			result[0] = F.eval(F.Numerator(result[2]));
		} else {
			result[0] = result[2];
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
	 *            try to find a trigonometric numerator/denominator form (Example: Csc[x] gives 1 / Sin[x])
	 * @param evalParts
	 *            evaluate the determined numerator and denominator parts
	 * @return the numerator and denominator expression and an optional fractional number (maybe <code>null</code>), if
	 *         splitNumeratorOne is <code>true</code>.
	 */
	public static IExpr[] fractionalPartsTimesPower(final IAST timesPower, boolean splitNumeratorOne,
			boolean splitFractionalNumbers, boolean trig, boolean evalParts) {
		if (timesPower.isPower()) {
			IExpr[] parts = Apart.fractionalPartsPower(timesPower, trig);
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
								numerator.append(numerForm);// numerator.addMerge(numerForm);
							}
							if (!denomForm.isOne()) {
								denominator.append(denomForm);// denominator.addMerge(denomForm);
							}
							evaled = true;
							continue;
						}
					}
				} else if (arg.isPower()) {
					IExpr[] parts = Apart.fractionalPartsPower((IAST) arg, trig);
					if (parts != null) {
						if (!parts[0].isOne()) {
							numerator.append(parts[0]); // numerator.addMerge(parts[0]);
						}
						if (!parts[1].isOne()) {
							denominator.append(parts[1]);// denominator.addMerge(parts[1]);
						}
						evaled = true;
						continue;
					}
				}
			} else if (i == 1 && arg.isFraction()) {
				if (splitNumeratorOne) {
					IFraction fr = (IFraction) arg;
					if (fr.numerator().isOne()) {
						denominator.append(fr.denominator()); // denominator.addMerge(fr.getDenominator());
						splitFractionEvaled = true;
						continue;
					}
					if (fr.numerator().isMinusOne()) {
						numerator.append(fr.numerator()); // numerator.addMerge(fr.getNumerator());
						denominator.append(fr.denominator());// denominator.addMerge(fr.getDenominator());
						splitFractionEvaled = true;
						continue;
					}
					result[2] = fr;
					continue;
				} else if (splitFractionalNumbers) {
					IFraction fr = (IFraction) arg;
					if (!fr.numerator().isOne()) {
						numerator.append(fr.numerator()); // numerator.addMerge(fr.getNumerator());
					}
					denominator.append(fr.denominator()); // denominator.addMerge(fr.getDenominator());
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
				result[0] = numerator.getOneIdentity(F.C1);
				result[1] = denominator.getOneIdentity(F.C1);
			}
			if (result[0].isNegative() && result[1].isPlus() && ((IAST) result[1]).isAST2()) {
				// negate numerator and denominator:
				result[0] = result[0].negate();
				result[1] = result[1].negate();
			}
			return result;
		}
		if (splitFractionEvaled) {
			result[0] = numerator.getOneIdentity(F.C1);// numerator.getProduct();
			if (!result[0].isTimes() && !result[0].isPlus()) {
				result[1] = denominator.getOneIdentity(F.C1); // denominator.getProduct();
				return result;
			}
			if (result[0].isTimes() && ((IAST) result[0]).isAST2() && ((IAST) result[0]).arg1().isMinusOne()) {
				result[1] = denominator.getOneIdentity(F.C1); // denominator.getProduct();
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
				parts = fractionalPartsTimesPower(ast, false, true, trig, true);
			} else if (arg.isPower()) {
				parts = Apart.fractionalPartsPower(ast, trig);
				if (parts == null) {
					return null;
				}
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

	public static IExpr partialFractionDecompositionRational(IPartialFractionGenerator pf, IExpr[] parts,
			IExpr variable) {
		return partialFractionDecompositionRational(pf, parts, F.List(variable));
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
		}
		return fractionalParts(arg, false);
	}

	public static IExpr together(IAST ast, EvalEngine engine) {
		IExpr temp = expandAll(ast, null, true, false, engine);
		if (!temp.isPresent()) {
			temp = ast;
		}
		if (temp.isAST()) {
			IExpr result = Together.togetherPlusTimesPower((IAST) temp, engine);
			if (result.isPresent()) {
				return F.eval(result);
			}
		}
		return temp;
	}

	private final static Algebra CONST = new Algebra();

	public static Algebra initialize() {
		return CONST;
	}

	private Algebra() {

	}

}
