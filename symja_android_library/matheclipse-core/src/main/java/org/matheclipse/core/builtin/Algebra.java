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
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Null;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Combinatoric.Permutations;
import org.matheclipse.core.builtin.function.Refine;
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
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
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
import org.matheclipse.core.polynomials.ExprMonomial;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.ExprTermOrder;
import org.matheclipse.core.polynomials.IPartialFractionGenerator;
import org.matheclipse.core.polynomials.PartialFractionGenerator;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
//import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.parser.client.math.ArithmeticMathException;

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
		F.Denominator.setEvaluator(new Denominator());
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
			if (exponent.isSignedNumber()) {
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
						// IExpr[] partsArg1 = fractionalPartsTimesPower(function, true, true, trig, true);
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

		/**
		 * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition of the
		 * numerator and denominator parts.
		 * 
		 * @deprecated untested at the moment
		 * @param parts
		 * @param variableList
		 * @return <code>null</code> if the partial fraction decomposition wasn't constructed
		 */
		@Deprecated
		private static IAST partialFractionDecompositionInteger(IExpr[] parts, IAST variableList) {
			try {
				IExpr exprNumerator = F.evalExpandAll(parts[0]);
				IExpr exprDenominator = F.evalExpandAll(parts[1]);
				ASTRange r = new ASTRange(variableList, 1);
				List<IExpr> varList = r;

				String[] varListStr = new String[1];
				varListStr[0] = variableList.arg1().toString();
				JASConvert<BigInteger> jas = new JASConvert<BigInteger>(varList, BigInteger.ZERO);
				GenPolynomial<BigInteger> numerator = jas.expr2JAS(exprNumerator, false);
				GenPolynomial<BigInteger> denominator = jas.expr2JAS(exprDenominator, false);

				// get factors
				FactorAbstract<BigInteger> factorAbstract = FactorFactory.getImplementation(BigInteger.ZERO);
				SortedMap<GenPolynomial<BigInteger>, Long> sfactors = factorAbstract.baseFactors(denominator);

				List<GenPolynomial<BigInteger>> D = new ArrayList<GenPolynomial<BigInteger>>(sfactors.keySet());

				SquarefreeAbstract<BigInteger> sqf = SquarefreeFactory.getImplementation(BigInteger.ZERO);
				List<List<GenPolynomial<BigInteger>>> Ai = sqf.basePartialFraction(numerator, sfactors);
				// returns [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) =
				// A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).

				if (Ai.size() > 0) {
					IAST result = F.Plus();
					IExpr temp;
					if (!Ai.get(0).get(0).isZERO()) {
						temp = F.eval(jas.integerPoly2Expr(Ai.get(0).get(0)));
						if (temp.isAST()) {
							((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
						}
						result.append(temp);
					}
					for (int i = 1; i < Ai.size(); i++) {
						List<GenPolynomial<BigInteger>> list = Ai.get(i);
						long j = 0L;
						for (GenPolynomial<BigInteger> genPolynomial : list) {
							if (!genPolynomial.isZERO()) {
								temp = F.eval(F.Times(jas.integerPoly2Expr(genPolynomial),
										F.Power(jas.integerPoly2Expr(D.get(i - 1)), F.integer(j * (-1L)))));
								if (!temp.isZero()) {
									if (temp.isAST()) {
										((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
									}
									result.append(temp);
								}
							}
							j++;
						}

					}
					return result;
				}
			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			final IExpr arg1 = ast.arg1();
			IAST temp = Structure.threadLogicEquationOperators(arg1, ast, 1);
			if (temp.isPresent()) {
				return temp;
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
					return partialFractionDecompositionRational(new PartialFractionGenerator(), parts,
							(ISymbol) variableList.arg1());
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
			IAST plus = ((IAST) numeratorPlus).clone();
			IAST gcd = F.ast(F.GCD, plus.size() + 1, false);
			gcd.append(denominatorInt);
			boolean evaled = true;
			for (int i = 1; i < plus.size(); i++) {
				IExpr temp = plus.get(i);
				if (temp.isInteger()) {
					gcd.append(temp);
				} else {
					if (temp.isTimes() && temp.getAt(1).isInteger()) {
						gcd.append(temp.getAt(1));
					} else {
						evaled = false;
						break;
					}
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
		 *            an <code>Times[...] or Power[...]</code> AST, where common factors should be canceled out.
		 * @return <code>F.NIL</code> is no evaluation was possible
		 * @throws JASConversionException
		 */
		public static IExpr cancelPowerTimes(IExpr powerTimesAST) throws JASConversionException {
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

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (ast.isAST1() && arg1.isAtom()) {
				return arg1;
			}
			IAST temp = Structure.threadPlusLogicEquationOperators(arg1, ast, 1);
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
			return arg1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
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
				return ((IRational) expr).getDenominator();
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
		public final static Expand CONST = new Expand();

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

			public IExpr expandAST(final IAST ast) {
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

					IExpr[] temp = fractionalPartsTimesPower(ast, false, false, false, true);
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
							IExpr denom = expandAST((IAST) temp[1]);
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
								IExpr denom = expandAST((IAST) temp[1]);
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
				IAST result = F.NIL;
				for (int i = 1; i < ast.size(); i++) {
					final IExpr arg = ast.get(i);
					if (arg.isAST()) {
						IExpr temp = expand((IAST) arg, pattern, expandNegativePowers, false);
						if (temp.isPresent()) {
							if (!result.isPresent()) {
								result = ast.copyUntil(ast.size(), i);
							}
							result.append(temp);
							continue;
						}
					}
					if (result.isPresent()) {
						result.append(arg);
					}
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
								return expandAST(F.Times(F.Power(base, fractionalPart), F.Power(base, floorPart)));
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

				int k = plusAST.size() - 1;
				long numberOfTerms = LongMath.binomial(n + k - 1, n);
				if (numberOfTerms > (long) Integer.MAX_VALUE) {
					throw new ArithmeticException("");
				}
				final IAST expandedResult = F.ast(F.Plus, (int) numberOfTerms, false);
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
				long numberOfTerms = (long) (plusAST0.size() - 1) * (long) (plusAST1.size() - 1);
				if (numberOfTerms > (long) Integer.MAX_VALUE) {
					throw new ArithmeticException("");
				}
				final IAST result = F.ast(F.Plus, (int) numberOfTerms, false);
				for (int i = 1; i < plusAST0.size(); i++) {
					for (int j = 1; j < plusAST1.size(); j++) {
						// evaluate to flatten out Times() exprs
						evalAndExpandAST(plusAST0.get(i), plusAST1.get(j), result);
					}
				}
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
				final IAST result = F.ast(F.Plus, plusAST.size() - 1, false);
				for (int i = 1; i < plusAST.size(); i++) {
					// evaluate to flatten out Times() exprs
					evalAndExpandAST(expr1, plusAST.get(i), result);
				}
				return PlusOp.plus(result);
			}

			/**
			 * Evaluate <code>expr1 * expr2</code> and expand the resulting expression, if it's an <code>IAST</code>.
			 * After that add the resulting expression to the <code>PlusOp</code>
			 * 
			 * @param result
			 * @param expr
			 */
			public void evalAndExpandAST(IExpr expr1, IExpr expr2, final IAST result) {
				IExpr arg = TimesOp.times(expr1, expr2);
				if (arg.isAST()) {
					result.appendPlus(expandAST((IAST) arg).orElse(arg));
					return;
				}
				result.append(arg);
			}

		}

		private static class NumberPartititon {
			IAST expandedResult;
			int m;
			int n;
			int[] parts;
			IAST precalculatedPowerASTs;

			public NumberPartititon(IAST plusAST, int n, IAST expandedResult) {

				this.expandedResult = expandedResult;
				this.n = n;
				this.m = plusAST.size() - 1;
				this.parts = new int[m];
				// precalculate all Power[] ASTs:
				this.precalculatedPowerASTs = F.ListAlloc(plusAST.size());
				for (IExpr expr : plusAST) {
					precalculatedPowerASTs.append(expr);
				}
			}

			private void addFactor(int[] j) {
				final Permutations.KPermutationsIterable perm = new Permutations.KPermutationsIterable(j, m, m);
				IInteger multinomial = NumberTheory.multinomial(j, n);
				final IAST times = F.Times();
				IExpr temp;
				for (int[] indices : perm) {
					final IAST timesAST = times.clone();
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
									for (int i = 1; i < ast.size(); i++) {
										timesAST.append(PowerOp.power(ast.get(i), F.integer(indices[k])));
									}
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
				return expand(arg1, patt, false, true).orElse(arg1);
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
		public final static ExpandAll CONST = new ExpandAll();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			IExpr patt = null;
			if (ast.size() > 2) {
				patt = ast.arg2();
			}
			if (arg1.isAST()) {
				return expandAll((IAST) arg1, patt, true, false).orElse(arg1);
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

			VariablesSet eVar = new VariablesSet(ast.arg1());

			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				return list.mapThread(F.ListAlloc(list.size()), ast, 1);
			}
			IExpr expr = ast.arg1();
			if (ast.isAST1()) {
				expr = engine.evaluate(F.Together(ast.arg1()));
				if (expr.isAST()) {
					IExpr[] parts = Algebra.getNumeratorDenominator((IAST) expr);
					if (!parts[1].isOne()) {
						return F.Divide(F.Factor(parts[0]), F.Factor(parts[1]));
					}
				}
			}

			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			try {
				List<IExpr> varList = r;

				if (ast.isAST2()) {
					return factorWithOption(ast, expr, varList, false, engine);
				}
				return factor(expr, varList, false);

			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
			return expr;
		}

		public static IExpr factor(IExpr expr, List<IExpr> varList, boolean factorSquareFree)
				throws JASConversionException {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
			if (polyRat.length() <= 1) {
				return expr;
			}

			Object[] objects = jas.factorTerms(polyRat);
			GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
			FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory
					.getImplementation(edu.jas.arith.BigInteger.ONE);
			SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
			if (factorSquareFree) {
				map = factorAbstract.squarefreeFactors(poly);// factors(poly);
			} else {
				map = factorAbstract.factors(poly);
			}
			IAST result = F.Times();
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
			return result.getOneIdentity(F.C0);
		}

		public static IExpr factorList(IExpr expr, List<IExpr> varList, boolean factorSquareFree)
				throws JASConversionException {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
			Object[] objects = jas.factorTerms(polyRat);
			java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
			java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
			GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
			FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory
					.getImplementation(edu.jas.arith.BigInteger.ONE);
			SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
			if (factorSquareFree) {
				map = factorAbstract.squarefreeFactors(poly);// factors(poly);
			} else {
				map = factorAbstract.factors(poly);
			}
			IAST result = F.ListAlloc(map.size() + 1);
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
			if (option.isSignedNumber()) {
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
				IExpr expr = F.evalExpandAll(ast.arg1());
				ASTRange r = new ASTRange(eVar.getVarList(), 1);
				List<IExpr> varList = r;

				if (ast.isAST2()) {
					return factorWithOption(ast, expr, varList, true, engine);
				}
				return factor(expr, varList, true);

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
				IExpr expr = F.evalExpandAll(ast.arg1());
				ASTRange r = new ASTRange(eVar.getVarList(), 1);
				List<IExpr> varList = r;

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
			ASTRange r = new ASTRange(variableList, 1);
			IExpr expr = F.evalExpandAll(ast.arg1());
			// IExpr variable = variableList.arg1();
			try {

				JASConvert<BigRational> jas = new JASConvert<BigRational>(r, BigRational.ZERO);
				GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
				Object[] objects = jas.factorTerms(poly);
				java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
				java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
				if (lcm.equals(java.math.BigInteger.ZERO)) {
					// no changes
					return expr;
				}
				GenPolynomial<edu.jas.arith.BigInteger> iPoly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
				IAST result = F.Times();
				result.append(F.fraction(gcd, lcm));
				result.append(jas.integerPoly2Expr(iPoly));
				return result;
			} catch (JASConversionException e) {
				// if (Config.DEBUG) {
				// e.printStackTrace();
				// }
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT);
		}
	}

	/**
	 * Try to simplify a given expression
	 * 
	 * TODO currently FullSimplify simply calls Simplify
	 * 
	 */
	private static class FullSimplify extends Simplify {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			return super.evaluate(ast, engine);
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
				return ((IRational) arg).getNumerator();
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
			IExpr expr1 = F.evalExpandAll(ast.arg1());
			IExpr expr2 = F.evalExpandAll(ast.arg2());
			VariablesSet eVar = new VariablesSet();
			eVar.add(x);

			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			if (ast.size() == 5) {
				List<IExpr> varList = r;
				final Options options = new Options(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isSignedNumber()) {
					try {
						// found "Modulus" option => use ModIntegerRing
						ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
						JASModInteger jas = new JASModInteger(varList, modIntegerRing);
						GenPolynomial<ModLong> poly1 = jas.expr2JAS(expr1);
						GenPolynomial<ModLong> poly2 = jas.expr2JAS(expr2);
						GenPolynomial<ModLong>[] result = poly1.egcd(poly2);
						IAST list = F.List();
						list.append(jas.modLongPoly2Expr(result[0]));
						IAST subList = F.List();
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
				JASConvert<BigRational> jas = new JASConvert<BigRational>(r, BigRational.ZERO);
				GenPolynomial<BigRational> poly1 = jas.expr2JAS(expr1, false);
				GenPolynomial<BigRational> poly2 = jas.expr2JAS(expr2, false);
				GenPolynomial<BigRational>[] result = poly1.egcd(poly2);
				IAST list = F.List();
				list.append(jas.rationalPoly2Expr(result[0]));
				IAST subList = F.List();
				subList.append(jas.rationalPoly2Expr(result[1]));
				subList.append(jas.rationalPoly2Expr(result[2]));
				list.append(subList);
				return list;
			} catch (JASConversionException e0) {
				try {
					JASIExpr jas = new JASIExpr(r, ExprRingFactory.CONST);
					GenPolynomial<IExpr> poly1 = jas.expr2IExprJAS(expr1);
					GenPolynomial<IExpr> poly2 = jas.expr2IExprJAS(expr2);
					GenPolynomial<IExpr>[] result = poly1.egcd(poly2);
					IAST list = F.List();
					list.append(jas.exprPoly2Expr(result[0], x));
					IAST subList = F.List();
					subList.append(F.eval(F.Together(jas.exprPoly2Expr(result[1], x))));
					subList.append(F.eval(F.Together(jas.exprPoly2Expr(result[2], x))));
					list.append(subList);
					return list;
				} catch (JASConversionException e) {
					// if (Config.DEBUG) {
					e.printStackTrace();
					// }
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

			IExpr expr = F.evalExpandAll(ast.arg1());
			if (ast.size() > 3 && ast.last().isRuleAST()) {
				return gcdWithOption(ast, expr, eVar, engine);
			}
			try {
				ASTRange r = new ASTRange(eVar.getVarList(), 1);
				JASConvert<BigInteger> jas = new JASConvert<BigInteger>(r, BigInteger.ZERO);
				GenPolynomial<BigInteger> poly = jas.expr2JAS(expr, false);
				GenPolynomial<BigInteger> temp;
				GreatestCommonDivisorAbstract<BigInteger> factory = GCDFactory.getImplementation(BigInteger.ZERO);
				for (int i = 2; i < ast.size(); i++) {
					expr = F.evalExpandAll(ast.get(i));
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
			final Options options = new Options(ast.topHead(), ast, ast.size() - 1, engine);
			IExpr option = options.getOption("Modulus");
			if (option.isSignedNumber()) {
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

				for (int i = 2; i < ast.size() - 1; i++) {
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

			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			IExpr expr = F.evalExpandAll(ast.arg1());
			if (ast.size() > 3) {
				final Options options = new Options(ast.topHead(), ast, ast.size() - 1, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isSignedNumber()) {
					try {
						// found "Modulus" option => use ModIntegerRing
						ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
						JASModInteger jas = new JASModInteger(r, modIntegerRing);
						GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
						GenPolynomial<ModLong> temp;
						GreatestCommonDivisorAbstract<ModLong> factory = GCDFactory.getImplementation(modIntegerRing);
						for (int i = 2; i < ast.size() - 1; i++) {
							expr = F.evalExpandAll(ast.get(i));
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
				JASConvert<BigInteger> jas = new JASConvert<BigInteger>(r, BigInteger.ZERO);
				GenPolynomial<BigInteger> poly = jas.expr2JAS(expr, false);
				GenPolynomial<BigInteger> temp;
				GreatestCommonDivisorAbstract<BigInteger> factory = GCDFactory.getImplementation(BigInteger.ZERO);
				for (int i = 2; i < ast.size(); i++) {
					expr = F.evalExpandAll(ast.get(i));
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
	private static class PolynomialQ extends AbstractFunctionEvaluator implements BiPredicate<IExpr, IExpr> {

		/**
		 * Returns <code>True</code> if the given expression is a polynoomial object; <code>False</code> otherwise
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IAST variables;
			if (ast.arg2().isList()) {
				variables = (IAST) ast.arg2();
			} else {
				variables = List(ast.arg2());
			}
			return F.bool(ast.arg1().isPolynomial(variables));

		}

		/**
		 * 
		 * @param polnomialExpr
		 * @param variables
		 * @param numericFunction
		 * @return
		 * @deprecated use
		 *             <code>ExprPolynomialRing ring = new ExprPolynomialRing(variables); ExprPolynomial poly = ring.create(polnomialExpr);</code>
		 *             if possible.
		 */
		private static GenPolynomial<IExpr> polynomial(final IExpr polnomialExpr, final IAST variables,
				boolean numericFunction) {
			IExpr expr = F.evalExpandAll(polnomialExpr);
			int termOrder = ExprTermOrder.INVLEX;
			ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables, variables.size() - 1,
					new ExprTermOrder(termOrder), true);
			try {
				ExprPolynomial poly = ring.create(expr);
				ASTRange r = new ASTRange(variables, 1);
				JASIExpr jas = new JASIExpr(r, numericFunction);
				return jas.expr2IExprJAS(poly);
			} catch (RuntimeException ex) {

			}
			return null;
		}

		/**
		 * 
		 * @param polnomialExpr
		 * @param symbol
		 * @param numericFunction
		 * @return
		 * @deprecated use
		 *             <code>ExprPolynomialRing ring = new ExprPolynomialRing(symbol); ExprPolynomial poly = ring.create(polnomialExpr);</code>
		 *             if possible
		 */
		private static GenPolynomial<IExpr> polynomial(final IExpr polnomialExpr, final ISymbol symbol,
				boolean numericFunction) {
			return polynomial(polnomialExpr, List(symbol), numericFunction);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

		@Override
		public boolean test(final IExpr firstArg, final IExpr secondArg) {
			IAST list;
			if (secondArg.isList()) {
				list = (IAST) secondArg;
			} else {
				list = List(secondArg);
			}
			return firstArg.isPolynomial(list);
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
			IExpr arg1 = F.evalExpandAll(ast.arg1());
			IExpr arg2 = F.evalExpandAll(ast.arg2());

			if (ast.size() == 5) {
				final Options options = new Options(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isSignedNumber()) {
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

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 4, 5);
			ISymbol variable = Validate.checkSymbolType(ast, 3);
			IExpr arg1 = F.evalExpandAll(ast.arg1());
			IExpr arg2 = F.evalExpandAll(ast.arg2());

			if (ast.size() == 5) {
				final Options options = new Options(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isSignedNumber()) {
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

		public static IExpr[] quotientRemainder(final IExpr arg1, IExpr arg2, ISymbol variable) {

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
					if (Config.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			return null;
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
			IExpr arg1 = F.evalExpandAll(ast.arg1());
			IExpr arg2 = F.evalExpandAll(ast.arg2());

			if (ast.size() == 5) {
				final Options options = new Options(ast.topHead(), ast, 4, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isSignedNumber()) {
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
						IAST powerAST = (IAST) x1;
						// Log[x_ ^ y_] :> y * Log(x)
						IAST logResult = Times(powerAST.arg2(), powerExpand(Log(powerAST.arg1()), assumptions));
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
						IAST timesResult = timesAST.mapThread(Power(Null, x2), 1);
						if (assumptions) {
							IAST plusResult = Plus(C1D2);
							for (int i = 1; i < timesAST.size(); i++) {
								plusResult.append(Negate(Divide(Arg(timesAST.get(i)), Times(C2, Pi))));
							}
							IAST expResult = Power(E, Times(C2, I, Pi, x2, Floor(plusResult)));
							timesResult.append(expResult);
							return timesResult;
						}
						return timesResult;
					}
					if (x1.isPower()) {
						// Power[x_ ^ y_, z_] :> x ^(y*z)
						IAST powerAST = (IAST) x1;
						IExpr base = powerAST.arg1();
						IExpr exponent = powerAST.arg2();
						IAST powerResult = Power(base, Times(exponent, x2));
						if (assumptions) {
							IAST floorResult = Floor(
									Divide(Subtract(Pi, Im(Times(exponent, Log(base)))), Times(C2, Pi)));
							IAST expResult = Power(E, Times(C2, I, Pi, x2, floorResult));
							IAST timesResult = Times(powerResult, expResult);
							return timesResult;
						}
						return powerResult;
					}
				}
				if (evaled) {
					return F.binaryAST2(head, x1, x2);
				}
				return F.NIL;
			}
		}

		/** {@inheritDoc} */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			if (ast.arg1().isAST()) {
				boolean assumptions = false;
				if (ast.isAST2()) {
					final Options options = new Options(ast.topHead(), ast, ast.size() - 1, engine);
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
			// -(c/(3*d)) + (E^((2*I*Pi*(k - 1))/3)*p)/(3*2^(1/3)*d) - (2^(1/3)*r)/(E^((2*I*Pi*(k - 1))/3)*(3*p*d))
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

			// t = Sqrt(-4*(c^2 - 3*b*d + 12*a*e)^3 + (2*c^3 - 9*c*(b*d + 8*a*e) + 27*(a*d^2 + b^2*e))^2)
			IExpr t = F.Sqrt(Plus(Times(F.CN4, Power(Plus(F.Sqr(c), Times(F.CN3, b, d), Times(F.ZZ(12L), a, e)), 3)),
					F.Sqr(Plus(Times(F.CN9, c, Plus(Times(b, d), Times(F.C8, a, e))),
							Times(F.ZZ(27L), Plus(Times(a, F.Sqr(d)), Times(F.Sqr(b), e))), Times(C2, Power(c, 3))))));
			// s = (t + 2*c^3 - 9*c*(b*d + 8*a*e) + 27*(a*d^2 + b^2*e))^(1/3)
			IExpr s = Power(Plus(Times(C2, Power(c, 3)), t, Times(F.CN9, c, Plus(Times(b, d), Times(F.C8, a, e))),
					Times(F.ZZ(27L), Plus(Times(a, F.Sqr(d)), Times(F.Sqr(b), e)))), F.C1D3);

			// eps1 = (1/2)*Sqrt((2^(1/3)*(c^2 - 3*b*d + 12*a*e))/ (3*s*e) + (3*d^2 + 2*2^(2/3)*s*e - 8*c*e)/ (12 e^2))
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

			// -(d/(4*e)) + (2*Floor((k - 1)/2) - 1)*eps1 + (-1)^k*(1 - UnitStep(k - 3))*eps2 - (-1)^k*(UnitStep(2 - k)
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
			public IExpr visit(IAST ast) {
				if (!ast.isAST(F.Root)) {
					IAST cloned = replacement.setAtClone(1, null);
					return ((IAST) ast).mapThread(cloned, 1);
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
					expr = ((IAST) expr).arg1();
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
	 */
	private static class Simplify extends AbstractFunctionEvaluator {

		private static class IsBasicExpressionVisitor extends AbstractVisitorBoolean {
			public IsBasicExpressionVisitor() {
				super();
			}

			@Override
			public boolean visit(IAST ast) {
				if (ast.isTimes() || ast.isPlus()) {
					// check the arguments
					for (int i = 1; i < ast.size(); i++) {
						if (!ast.get(i).accept(this)) {
							return false;
						}
					}
					return true;
				}
				if (ast.isPower() && (ast.arg2().isInteger())) {
					// check the arguments
					return ast.arg1().accept(this);
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

			public SimplifyVisitor(Function<IExpr, Long> complexityFunction) {
				super();
				fComplexityFunction = complexityFunction;
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
			public IExpr visit(IAST ast) {
				IExpr result = F.NIL;
				IExpr temp;

				temp = visitAST(ast);
				if (temp.isPresent()) {
					long minCounter = fComplexityFunction.apply(ast);
					long count = fComplexityFunction.apply(temp);
					if (count < minCounter) {
						minCounter = count;
						if (temp.isAST()) {
							ast = (IAST) temp;
							result = temp;
						} else {
							return temp;
						}
					}
				}

				if (ast.isPlus()) {
					IAST basicPlus = F.Plus();
					IAST restPlus = F.Plus();

					for (int i = 1; i < ast.size(); i++) {
						temp = ast.get(i);
						if (temp.accept(isBasicAST)) {
							basicPlus.append(temp);
						} else {
							restPlus.append(temp);
						}
					}
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
					return result;

				} else if (ast.isTimes()) {
					IAST basicTimes = F.Times();
					IAST restTimes = F.Times();
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
								} else if (temp.isPower() && ((IAST) temp).arg1().isPlus()
										&& ((IAST) temp).arg2().isMinusOne()) {
									// <number> * Power[Plus[...], -1 ]
									reduced = tryExpandAll(ast, ((IAST) temp).arg1(), number.inverse(), i);
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
					if (temp.isPresent()) {
						return temp;
					}
					return result;
				}

				temp = F.evalExpandAll(ast);
				long minCounter = fComplexityFunction.apply(ast);

				long count = fComplexityFunction.apply(temp);
				if (count < minCounter) {
					return temp;
				}
				return result;
			}

			private IExpr tryExpandAll(IAST ast, IExpr temp, IExpr arg1, int i) {
				IExpr expandedAst = tryExpandAllTransformation((IAST) temp, F.Times(arg1, temp));
				if (expandedAst.isPresent()) {
					IAST result = F.Times();
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

			try {
				Function<IExpr, Long> complexityFunction = createComplexityFunction(complexityFunctionHead, engine);
				long minCounter = complexityFunction.apply(arg1);
				IExpr result = arg1;
				long count = 0L;
				if (assumptionExpr.isPresent()) {
					IAssumptions assumptions = Refine.determineAssumptions(ast.topHead(), assumptionExpr, engine);
					if (assumptions != null) {
						arg1 = Refine.refineAssumptions(arg1, assumptions, engine);
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

				temp = arg1.accept(new SimplifyVisitor(complexityFunction));
				while (temp.isPresent()) {
					count = complexityFunction.apply(temp);
					if (count < minCounter) {
						minCounter = count;
						result = temp;
						temp = result.accept(new SimplifyVisitor(complexityFunction));
					} else {
						return result;
					}
				}
				return result;

			} catch (ArithmeticException e) {
				//
			}
			return F.NIL;
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

		/**
		 * Calls <code>Together</code> for each argument of the <code>ast</code>.
		 * 
		 * @param ast
		 * @return <code>F.NIL</code> if the <code>ast</code> couldn't be evaluated.
		 */
		private static IAST togetherForEach(final IAST ast) {
			IAST result = F.NIL;
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isAST()) {
					IExpr temp = togetherNull((IAST) ast.get(i));
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
		 * @return <code>null</code> couldn't be transformed by <code>ExpandAll(()</code> od <code>togetherAST()</code>
		 */
		private static IExpr togetherNull(IAST ast) {
			boolean evaled = false;
			IExpr temp = expandAll(ast, null, true, false);
			if (!temp.isPresent()) {
				temp = ast;
			} else {
				evaled = true;
			}
			if (temp.isAST()) {
				IExpr result = togetherPlusTimesPower((IAST) temp);
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
			IAST numerator = F.ast(F.Plus, plusAST.size(), false);
			IAST denominator = F.ast(F.Times, plusAST.size(), false);
			boolean evaled = false;
			IExpr temp;
			IExpr[] fractionalParts;
			for (int i = 1; i < plusAST.size(); i++) {
				fractionalParts = fractionalPartsRational(plusAST.get(i));
				if (fractionalParts != null) {
					numerator.append(i, fractionalParts[0]);
					temp = fractionalParts[1];
					if (!temp.isOne()) {
						evaled = true;
					}
					denominator.append(i, temp);
				} else {
					numerator.append(i, plusAST.get(i));
					denominator.append(i, F.C1);
				}
			}
			if (!evaled) {
				return F.NIL;
			}
			IAST ni;
			for (int i = 1; i < plusAST.size(); i++) {
				ni = F.TimesAlloc(plusAST.size() - 1);
				ni.append(numerator.get(i));
				for (int j = 1; j < plusAST.size(); j++) {
					if (i == j) {
						continue;
					}
					temp = denominator.get(j);
					if (!temp.isOne()) {
						ni.append(temp);
					}
				}
				numerator.set(i, ni.getOneIdentity(F.C1));
			}
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
			IExpr exprDenominator = F.evalExpand(denominator.getOneIdentity(F.C1));
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
					return F.NIL;
				} catch (JASConversionException jce) {
					if (Config.DEBUG) {
						jce.printStackTrace();
					}
				}
				return F.Times(exprNumerator, F.Power(exprDenominator, F.CN1));
			}
			return exprNumerator;
		}

		private static IExpr togetherPlusTimesPower(final IAST ast) {
			if (ast.isPlus()) {
				IAST result = togetherForEach(ast);
				if (result.isPresent()) {
					return togetherPlus(result).orElse(result);
				}
				return togetherPlus(ast);
			} else if (ast.isTimes() || ast.isPower()) {
				try {
					IAST result = F.NIL;
					if (ast.isTimes()) {
						result = togetherForEach(ast);
					} else {
						// Power
						result = togetherPower(ast, result);
					}
					if (result.isPresent()) {
						IExpr temp = F.eval(result);
						if (temp.isTimes() || temp.isPower()) {
							return Cancel.cancelPowerTimes(temp).orElse(temp);
						}
						return temp;
					}
					return Cancel.cancelPowerTimes(ast);
				} catch (JASConversionException jce) {
					if (Config.DEBUG) {
						jce.printStackTrace();
					}
				}
			}

			return F.NIL;
		}

		private static IAST togetherPower(final IAST ast, IAST result) {
			if (ast.arg1().isAST()) {
				IExpr temp = togetherNull((IAST) ast.arg1());
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
			IAST temp = Structure.threadLogicEquationOperators(arg1, ast, 1);
			if (temp.isPresent()) {
				return temp;
			}
			if (arg1.isPlusTimesPower()) {
				return togetherNull((IAST) arg1).orElse(arg1);
			}
			return arg1;
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

			// VariablesSet eVar = new VariablesSet(ast.arg1());
			return VariablesSet.getVariables(ast.arg1());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

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
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			JASIExpr jas = new JASIExpr(r, true);
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
	 * @return <code>F.NIL</code> if the expression couldn't be expanded.
	 */
	public static IExpr expand(final IAST ast, IExpr patt, boolean expandNegativePowers, boolean distributePlus) {
		Expand.Expander expander = new Expand.Expander(patt, expandNegativePowers, distributePlus);
		return expander.expandAST(ast);
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
	public static IExpr expandAll(final IAST ast, IExpr patt, boolean expandNegativePowers, boolean distributePlus) {
		if (patt != null && ast.isFree(patt, true)) {
			return F.NIL;
		}
		IAST localAST = ast;
		IAST tempAST = F.NIL;
		if ((localAST.getEvalFlags() & IAST.IS_SORTED) != IAST.IS_SORTED) {
			tempAST = EvalEngine.get().evalFlatOrderlessAttributesRecursive(localAST);
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
		IAST result = F.NIL;
		IExpr temp = F.NIL;
		int size = localAST.size();
		if (localAST.head().isAST()) {
			temp = expandAll((IAST) localAST.head(), patt, expandNegativePowers, distributePlus);
			if (temp.isPresent()) {
				result = F.ast(temp, size, false);
			}
		}
		for (int i = 1; i < localAST.size(); i++) {
			if (localAST.get(i).isAST()) {
				temp = expandAll((IAST) localAST.get(i), patt, expandNegativePowers, distributePlus);
				if (temp.isPresent()) {
					if (!result.isPresent()) {

						if (temp.isAST()) {
							size += ((IAST) temp).size();
						}
						result = F.ast(localAST.head(), size, false);
						result.appendArgs(localAST, i);
					}
					result.appendPlus(temp);
					continue;
				}
			}
			if (result.isPresent()) {
				result.append(localAST.get(i));
			}
		}
		if (!result.isPresent()) {
			temp = expand(localAST, patt, expandNegativePowers, distributePlus);
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
		temp = expand(result, patt, expandNegativePowers, distributePlus);
		if (temp.isPresent()) {
			return ExpandAll.setAllExpanded(temp, expandNegativePowers, distributePlus);
		}
		return ExpandAll.setAllExpanded(result, expandNegativePowers, distributePlus);
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
		// Object[] objects = jas.factorTerms(polyRat);
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

		IAST result = F.ast(head);
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

	public static IAST factorModulus(JASModInteger jas, ModLongRing modIntegerRing, GenPolynomial<ModLong> poly,
			boolean factorSquareFree) {
		FactorAbstract<ModLong> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
		SortedMap<GenPolynomial<ModLong>, Long> map;
		if (factorSquareFree) {
			map = factorAbstract.squarefreeFactors(poly);
		} else {
			map = factorAbstract.factors(poly);
		}
		IAST result = F.Times();
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
		IAST result = F.ast(head);
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
	public static IExpr[] getNumeratorDenominator(IAST ast) {
		IExpr[] result = new IExpr[3];
		result[2] = together(ast);
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
	 *            evaluate the determined parts
	 * @return the numerator and denominator expression and an optional fractional number (maybe <code>null</code>), if
	 *         splitNumeratorOne is <code>true</code>.
	 */
	public static IExpr[] fractionalPartsTimesPower(final IAST timesPower, boolean splitNumeratorOne,
			boolean splitFractionalNumbers, boolean trig, boolean evalParts) {
		if (timesPower.isPower()) {
			IAST powerAST = timesPower;
			IExpr[] parts = Apart.fractionalPartsPower(powerAST, trig);
			if (parts != null) {
				return parts;
			}
			return null;
		}

		IAST timesAST = timesPower;
		IExpr[] result = new IExpr[3];
		result[2] = null;
		IAST numerator = F.TimesAlloc(timesAST.size());
		IAST denominator = F.TimesAlloc(timesAST.size());

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
					if (fr.getNumerator().isOne()) {
						denominator.append(fr.getDenominator()); // denominator.addMerge(fr.getDenominator());
						splitFractionEvaled = true;
						continue;
					}
					if (fr.getNumerator().isMinusOne()) {
						numerator.append(fr.getNumerator()); // numerator.addMerge(fr.getNumerator());
						denominator.append(fr.getDenominator());// denominator.addMerge(fr.getDenominator());
						splitFractionEvaled = true;
						continue;
					}
					result[2] = fr;
					continue;
				} else if (splitFractionalNumbers) {
					IFraction fr = (IFraction) arg;
					if (!fr.getNumerator().isOne()) {
						numerator.append(fr.getNumerator()); // numerator.addMerge(fr.getNumerator());
					}
					denominator.append(fr.getDenominator()); // denominator.addMerge(fr.getDenominator());
					evaled = true;
					continue;
				}
			}
			numerator.append(arg); // numerator.addMerge(arg);
		}
		if (evaled) {
			// result[0] = numerator.getProduct();
			// result[1] = denominator.getProduct();
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
		try {
			IAST variableList = F.List(variable);
			IExpr exprNumerator = F.evalExpandAll(parts[0]);
			IExpr exprDenominator = F.evalExpandAll(parts[1]);
			ASTRange r = new ASTRange(variableList, 1);
			List<IExpr> varList = r;

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
			parts[0] = fr.getNumerator();
			parts[1] = fr.getDenominator();
			return parts;
		}
		return fractionalParts(arg, false);
	}

	public static IExpr together(IAST ast) {
		IExpr temp = expandAll(ast, null, true, false);
		if (!temp.isPresent()) {
			temp = ast;
		}
		if (temp.isAST()) {
			IExpr result = Together.togetherPlusTimesPower((IAST) temp);
			if (result.isPresent()) {
				return F.eval(result);
			}
		}
		return temp;
	}

	final static Algebra CONST = new Algebra();

	public static Algebra initialize() {
		return CONST;
	}

	private Algebra() {

	}

}
