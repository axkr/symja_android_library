package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;
import static org.matheclipse.core.expression.F.integer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import org.hipparchus.analysis.solvers.LaguerreSolver;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEvalEngine;
import org.matheclipse.core.polynomials.ExpVectorLong;
import org.matheclipse.core.polynomials.ExprMonomial;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.ExprTermOrder;
import org.matheclipse.core.polynomials.PolynomialsUtils;
import org.matheclipse.core.polynomials.QuarticSolver;
import org.matheclipse.core.reflection.system.MonomialList;
import org.matheclipse.core.reflection.system.rules.LegendrePRules;
import org.matheclipse.core.reflection.system.rules.LegendreQRules;

import com.google.common.math.LongMath;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.root.ComplexRootsAbstract;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.InvalidBoundaryException;
import edu.jas.root.Rectangle;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;

public class PolynomialFunctions {
	static {
		F.BellY.setEvaluator(new BellY());
		F.ChebyshevT.setEvaluator(new ChebyshevT());
		F.ChebyshevU.setEvaluator(new ChebyshevU());
		F.Coefficient.setEvaluator(new Coefficient());
		F.CoefficientList.setEvaluator(new CoefficientList());
		F.CoefficientRules.setEvaluator(new CoefficientRules());
		F.Cyclotomic.setEvaluator(new Cyclotomic());
		F.Discriminant.setEvaluator(new Discriminant());
		F.Exponent.setEvaluator(new Exponent());
		F.HermiteH.setEvaluator(new HermiteH());
		F.LaguerreL.setEvaluator(new LaguerreL());
		F.LegendreP.setEvaluator(new LegendreP());
		F.LegendreQ.setEvaluator(new LegendreQ());
		F.NRoots.setEvaluator(new NRoots());
		F.Resultant.setEvaluator(new Resultant());
		F.RootIntervals.setEvaluator(new RootIntervals());
		F.Roots.setEvaluator(new Roots());
	}

	/**
	 * <pre>
	 * Coefficient(polynomial, variable, exponent)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * get the coefficient of <code>variable^exponent</code> in <code>polynomial</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Coefficient">Wikipedia - Coefficient Coefficient</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; Coefficient(10(x^2)+2(y^2)+2*x, x, 2)
	 * 10
	 * </pre>
	 */
	private static class Coefficient extends AbstractFunctionEvaluator {
		private boolean setExponent(IAST list, IExpr expr, long[] exponents, long value) {
			for (int j = 1; j < list.size(); j++) {
				if (list.get(j).equals(expr)) {
					int ix = ExpVectorLong.indexVar(expr, list);
					exponents[ix] = value;
					return true;
				}
			}
			return false;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);
			IExpr cached = F.REMEMBER_AST_CACHE.getIfPresent(ast);
			if (cached != null) {
				return cached;
			}

			IExpr arg2 = ast.arg2();
			// list of variable expressions extracted from the second argument
			IASTAppendable listOfVariables = null;
			// array of corresponding exponents for the list of variables
			long[] exponents = null;

			if (arg2.isTimes()) {
				// Times(x, y^a,...)
				IAST arg2AST = (IAST) arg2;
				VariablesSet eVar = new VariablesSet(arg2AST);
				listOfVariables = eVar.getVarList();
				exponents = new long[listOfVariables.argSize()];
				for (int i = 0; i < exponents.length; i++) {
					exponents[i] = 0L;
				}
				for (int i = 1; i < arg2AST.size(); i++) {
					long value = 1L;
					IExpr a1 = arg2AST.get(i);
					if (a1.isPower() && a1.exponent().isInteger()) {
						a1 = arg2AST.get(i).base();
						IInteger ii = (IInteger) arg2AST.get(i).exponent();
						try {
							value = ii.toLong();
						} catch (ArithmeticException ae) {
							return F.NIL;
						}
					}

					if (!setExponent(listOfVariables, a1, exponents, value)) {
						return F.NIL;
					}
				}
			} else {
				listOfVariables = F.ListAlloc();
				listOfVariables.append(arg2);
				exponents = new long[1];
				exponents[0] = 1;
			}

			try {
				long n = 1;
				if (ast.isAST3()) {
					if (ast.arg3().isNegativeInfinity()) {
						return F.C0;
					}
					n = Validate.checkLongType(ast.arg3());
					for (int i = 0; i < exponents.length; i++) {
						exponents[i] *= n;
					}
				}
				ExpVectorLong expArr = new ExpVectorLong(exponents);
				IExpr expr = F.evalExpandAll(ast.arg1(), engine).normal();
				ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, listOfVariables,
						listOfVariables.argSize());
				ExprPolynomial poly = ring.create(expr, true, false);
				IExpr temp = poly.coefficient(expArr);
				F.REMEMBER_AST_CACHE.put(ast, temp);
				return temp;
			} catch (RuntimeException ae) {
				if (Config.SHOW_STACKTRACE) {
					ae.printStackTrace();
				}
				return F.C0;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * 
	 */
	private static class CoefficientList extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr expr = F.evalExpandAll(ast.arg1(), engine).normal();
			IAST list = Validate.checkSymbolOrSymbolList(ast, 2);
			return coefficientList(expr, list);
		}

		// private static long univariateCoefficientList(IExpr polynomial, final ISymbol variable, List<IExpr>
		// resultList)
		// throws JASConversionException {
		// try {
		// ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
		// ExprPolynomial poly = ring.create(polynomial);
		// IAST list = poly.coefficientList();
		// int degree = list.size() - 2;
		// if (degree >= Short.MAX_VALUE) {
		// return degree;
		// }
		// for (int i = 0; i <= degree; i++) {
		// IExpr temp = list.get(i + 1);
		// resultList.add(temp);
		// }
		// return degree;
		// } catch (RuntimeException ex) {
		// throw new WrongArgumentType(polynomial, "Polynomial expected!");
		// }
		// }

		/**
		 * 
		 * @param polynomial
		 * @param variable
		 * @param resultList
		 *            the coefficient list of the given univariate polynomial in increasing order
		 * @param resultListDiff
		 *            the coefficient list of the derivative of the given univariate polynomial
		 * @return the degree of the univariate polynomial; if <code>degree >= Short.MAX_VALUE</code>, the result list
		 *         will be empty.
		 */
		// private static long univariateCoefficientList(IExpr polynomial, ISymbol variable, List<IExpr> resultList,
		// List<IExpr> resultListDiff) throws JASConversionException {
		// try {
		// ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
		// ExprPolynomial poly = ring.create(polynomial);
		// IAST polyExpr = poly.coefficientList();
		//
		// int degree = polyExpr.size() - 2;
		// if (degree >= Short.MAX_VALUE) {
		// return degree;
		// }
		// for (int i = 0; i <= degree; i++) {
		// IExpr temp = polyExpr.get(i + 1);
		// resultList.add(temp);
		// }
		// IAST polyDiff = poly.derivative().coefficientList();
		// int degreeDiff = polyDiff.size() - 2;
		// for (int i = 0; i <= degreeDiff; i++) {
		// IExpr temp = polyDiff.get(i + 1);
		// resultListDiff.add(temp);
		// }
		// return degree;
		// } catch (RuntimeException ex) {
		// throw new WrongArgumentType(polynomial, "Polynomial expected!");
		// }
		// }
	}

	/**
	 * Get exponent vectors and coefficients of monomials of a polynomial expression.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial<a/>
	 */
	private static class CoefficientRules extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, final EvalEngine engine) {
			Validate.checkRange(ast, 2, 5);

			IExpr expr = F.evalExpandAll(ast.arg1(), engine);
			VariablesSet eVar;
			IAST symbolList = F.List();
			List<IExpr> varList;
			if (ast.isAST1()) {
				// extract all variables from the polynomial expression
				eVar = new VariablesSet(ast.arg1());
				eVar.appendToList(symbolList);
				varList = eVar.getArrayList();
			} else {
				symbolList = Validate.checkSymbolOrSymbolList(ast, 2);
				varList = new ArrayList<IExpr>(symbolList.argSize());
				symbolList.forEach(x -> varList.add(x));
				// for (int i = 1; i < symbolList.size(); i++) {
				// varList.add(symbolList.get(i));
				// }
			}
			TermOrder termOrder = TermOrderByName.Lexicographic;
			try {
				if (ast.size() > 3) {
					if (ast.arg3() instanceof IStringX) {
						String orderStr = ast.arg3().toString();
						termOrder = Options.getMonomialOrder(orderStr, termOrder);
					}
					final Options options = new Options(ast.topHead(), ast, 2, engine);
					IExpr option = options.getOption("Modulus");
					if (option.isReal()) {
						return coefficientRulesModulus(expr, varList, termOrder, option);
					}
				}

				if (MonomialList.USE_JAS_POLYNOMIAL) {
					return coefficientRules(expr, varList, termOrder);
				} else {
					ExprPolynomialRing ring = new ExprPolynomialRing(symbolList,
							new ExprTermOrder(termOrder.getEvord()));
					ExprPolynomial poly = ring.create(expr);
					return poly.coefficientRules();
				}
			} catch (JASConversionException jce) {
				// toInt() conversion failed
				if (Config.DEBUG) {
					jce.printStackTrace();
				}
			}
			return F.NIL;
		}

		/**
		 * Get exponent vectors and coefficients of monomials of a polynomial expression.
		 * 
		 * @param polynomial
		 * @param variablesList
		 * @param termOrder
		 *            the JAS term ordering
		 * @return the list of monomials of the univariate polynomial.
		 */
		public static IAST coefficientRules(IExpr polynomial, final List<IExpr> variablesList,
				final TermOrder termOrder) throws JASConversionException {
			JASIExpr jas = new JASIExpr(variablesList, ExprRingFactory.CONST, termOrder, false);
			GenPolynomial<IExpr> polyExpr = jas.expr2IExprJAS(polynomial);
			IASTAppendable resultList = F.ListAlloc(polyExpr.length());
			for (Monomial<IExpr> monomial : polyExpr) {

				IExpr coeff = monomial.coefficient();
				ExpVector exp = monomial.exponent();
				int len = exp.length();
				IASTAppendable ruleList = F.ListAlloc(len);
				for (int i = 0; i < len; i++) {
					ruleList.append(F.integer(exp.getVal(len - i - 1)));
				}
				resultList.append(F.Rule(ruleList, coeff));
			}
			return resultList;
		}

		/**
		 * Get exponent vectors and coefficients of monomials of a polynomial expression.
		 * 
		 * @param polynomial
		 * @param variablesList
		 * @param termOrder
		 *            the JAS term ordering
		 * @param option
		 *            the &quot;Modulus&quot; option
		 * @return the list of monomials of the univariate polynomial.
		 */
		private static IAST coefficientRulesModulus(IExpr polynomial, List<IExpr> variablesList,
				final TermOrder termOrder, IExpr option) throws JASConversionException {
			try {
				// found "Modulus" option => use ModIntegerRing
				ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
				JASModInteger jas = new JASModInteger(variablesList, modIntegerRing);
				GenPolynomial<ModLong> polyExpr = jas.expr2JAS(polynomial);
				IASTAppendable resultList = F.ListAlloc(polyExpr.length());
				for (Monomial<ModLong> monomial : polyExpr) {
					ModLong coeff = monomial.coefficient();
					ExpVector exp = monomial.exponent();
					int len = exp.length();
					IASTAppendable ruleList = F.ListAlloc(len);
					for (int i = 0; i < len; i++) {
						ruleList.append(F.integer(exp.getVal(len - i - 1)));
					}
					resultList.append(F.Rule(ruleList, F.integer(coeff.getVal())));
				}
				return resultList;
			} catch (ArithmeticException ae) {
				// toInt() conversion failed
				if (Config.DEBUG) {
					ae.printStackTrace();
				}
			}
			return null;
		}

	}

	/**
	 * <pre>
	 * Cyclotomic(n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Cyclotomic polynomial <code>C_n(x)</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Cyclotomic_polynomial">Wikipedia - Cyclotomic polynomial</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Cyclotomic(25,x)
	 * 1+x^5+x^10+x^15+x^20
	 * </pre>
	 * <p>
	 * The case of the 105-th cyclotomic polynomial is interesting because 105 is the lowest integer that is the product
	 * of three distinct odd prime numbers and this polynomial is the first one that has a coefficient other than
	 * <code>1, 0</code> or <code>−1</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Cyclotomic(105, x) 
	 * 1+x+x^2-x^5-x^6-2*x^7-x^8-x^9+x^12+x^13+x^14+x^15+x^16+x^17-x^20-x^22-x^24-x^26-x^28+x^31+x^32+x^33+x^34+x^35+x^36-x^39-x^40-2*x^41-x^42-x^43+x^46+x^47+x^48
	 * </pre>
	 */
	final private static class Cyclotomic extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int n = ast.arg1().toIntDefault(-1);
			if (n >= 0) {
				return cyclotomic(n, ast.arg2());
			}
			return F.NIL;
		}

		private static IExpr cyclotomic(int n, final IExpr x) {
			switch (n) {
			case 0:
				return F.C1;
			case 1:
				return F.Plus(F.CN1, x);
			case 2:
				return F.Plus(F.C1, x);
			case 3:
				return F.Plus(F.C1, x, F.Sqr(x));
			}
			// precondition n > 2
			if (x.isZero()) {
				return F.C1;
			}
			// IASTAppendable result = F.PlusAlloc(n + 1);
			if (LongMath.isPrime(n)) {
				return F.sum(i -> x.power(i), 0, n - 1);
			}
			if ((n & 0x00000001) == 0x00000000) {
				// n is even
				int nHalf = n / 2;
				if ((nHalf & 0x00000001) == 0x00000001) {
					// nHalf is odd
					if (LongMath.isPrime(nHalf)) {
						return F.sum(i -> x.negate().power(i), 0, nHalf - 1);
					}
					return cyclotomic(nHalf, x.negate());
				}
			}
			BigInteger bigN = BigInteger.valueOf(n);
			Object[] primePower = Primality.primePower(bigN);
			if (primePower != null) {
				int p = ((BigInteger) primePower[0]).intValue();
				int pPowerm = n / p;
				return cyclotomic(p, x.power(F.ZZ(pPowerm)));
			}
			IInteger ni = F.ZZ(n);
			IAST divisorList = ni.divisors();
			// Product((1 - x^d)^MoebiusMu(n/d), {d, divisorList) // Together
			return F.Together(F.intIterator(F.Times,
					d -> F.Power(F.Plus(F.C1, F.Negate(F.Power(x, d))), F.MoebiusMu(F.Times(F.Power(d, -1), ni))),
					divisorList));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * Discriminant(poly, var)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes the discriminant of the polynomial <code>poly</code> with respect to the variable <code>var</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Discriminant">Wikipedia - Discriminant</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Discriminant(a*x^2+b*x+c,x)
	 * b^2-4*a*c
	 * </pre>
	 */
	private static class Discriminant extends AbstractFunctionEvaluator {
		// b^2-4*a*c
		private final static IExpr QUADRATIC = Plus(Power(F.b, C2), Times(CN1, Times(Times(C4, F.a), F.c)));

		// b^2*c^2-4*a*c^3-4*b^3*d-27*a^2*d^2+18*a*b*c*d
		private final static IExpr CUBIC = Plus(Plus(
				Plus(Plus(Times(Power(F.b, C2), Power(F.c, C2)), Times(CN1, Times(Times(C4, F.a), Power(F.c, C3)))),
						Times(CN1, Times(Times(C4, Power(F.b, C3)), F.d))),
				Times(CN1, Times(Times(integer(27L), Power(F.a, C2)), Power(F.d, C2)))),
				Times(Times(Times(Times(integer(18L), F.a), F.b), F.c), F.d));

		// Page 405
		// http://books.google.com/books?id=-gGzjSnNnR0C&lpg=PA402&vq=quartic&hl=de&pg=PA405#v=snippet&q=quartic&f=false

		// 256*a0^3*a4^3-27*a0^2*a3^4-27*a1^4*a4^2+16*a0*a2^3*a4
		// -4*a0*a2^3*a3^2-4*a1^2*a2^3*a4-4*a1^3*a3^3+a1^2*a2^2*a3^2
		// -192*a0^2*a1*a3*a4^2-128a0^2*a2^2*a4^2
		// +144*a0^2*a2*a3^2*a4+144*a0*a1^2*a2*a4^2-6*a0*a1^2*a3^2*a4
		// -80*a0*a1*a2^2*a3*a4+18*a0*a1*a2*a3^3+18*a1^3*a2*a3*a4

		// 256*a^3*e^3-27*a^2*d^4-27*b^4*e^2+16*a*c^3*e
		// -4*a*c^3*d^2-4*b^2*c^3*e-4*b^3*d^3+b^2*c^2*d^2
		// -192*a^2*b*d*e^2-128a^2*c^2*e^2
		// +144*a^2*c*d^2*e+144*a*b^2*c*e^2-6*a*b^2*d^2*e
		// -80*a*b*c^2*d*e+18*a*b*c*d^3+18*b^3*c*d*e
		private final static IExpr QUARTIC = Plus(
				Plus(Plus(
						Plus(Plus(
								Plus(Plus(
										Plus(Plus(
												Plus(Plus(
														Plus(Plus(
																Plus(Plus(
																		Times(Times(integer(256L), Power(F.a, C3)),
																				Power(F.e, C3)),
																		Times(CN1,
																				Times(Times(integer(27L),
																						Power(F.a, C2)),
																						Power(F.d, C4)))),
																		Times(CN1,
																				Times(Times(integer(27L),
																						Power(F.b, C4)),
																						Power(F.e, C2)))),
																Times(Times(Times(integer(16L), F.a), Power(F.c, C3)),
																		F.e)),
																Times(CN1,
																		Times(Times(Times(C4, F.a), Power(F.c, C3)),
																				Power(F.d, C2)))),
														Times(CN1,
																Times(Times(Times(C4, Power(F.b, C2)), Power(F.c, C3)),
																		F.e))),
														Times(CN1, Times(Times(C4, Power(F.b, C3)), Power(F.d, C3)))),
												Times(Times(Power(F.b, C2), Power(F.c, C2)), Power(F.d, C2))),
												Times(CN1,
														Times(Times(Times(Times(integer(192L), Power(F.a, C2)), F.b),
																F.d), Power(F.e, C2)))),
										Times(CN1,
												Times(Times(Times(integer(128L), Power(F.a, C2)), Power(F.c, C2)),
														Power(F.e, C2)))),
										Times(Times(Times(Times(integer(144L), Power(F.a, C2)), F.c), Power(F.d, C2)),
												F.e)),
								Times(Times(Times(Times(integer(144L), F.a), Power(F.b, C2)), F.c), Power(F.e, C2))),
								Times(CN1,
										Times(Times(Times(Times(integer(6L), F.a), Power(F.b, C2)), Power(F.d, C2)),
												F.e))),
						Times(CN1,
								Times(Times(Times(Times(Times(integer(80L), F.a), F.b), Power(F.c, C2)), F.d), F.e))),
						Times(Times(Times(Times(integer(18L), F.a), F.b), F.c), Power(F.d, C3))),
				Times(Times(Times(Times(integer(18L), Power(F.b, C3)), F.c), F.d), F.e));

		// 3125*a0^4*a5^4-2500*a0^3*a1*a4*a5^3-3750*a0^3*a2*a3*a5^3+2000*a0^3*a2*a4^2*a5^2+2250*a0^3*a3^2*a4*a5^2
		// -1600*a0^3*a3*a4^3*a5+256*a0^3*a4^5+2000*a0^2*a1^2*a3*a5^3-50*a0^2*a1^2*a4^2*a5^2+2250*a0^2*a1*a2^2*a5^3
		// -2050*a0^2*a1*a2*a3*a4*a5^2+160*a0^2*a1*a2*a4^3*a5-900*a0^2*a1*a3^2*a5^2+1020*a0^2*a1*a3^2*a4^2*a5
		// -192*a0^2*a1*a3*a4^4-900*a0^2*a2^3*a4*a5^2+825*a0^2*a2^2*a3^2*a5^2+560*a0^2*a2^2*a3*a4^2*a5-128*a0^2*a2^2*a4^4
		// -630*a0^2*a2*a3^3*a4*a5+144*a0^2*a2*a3^2*a4^3+108*a0^2*a3^5*a5-27*a0^2*a3^4*a4^2-1600*a0*a1^3*a2*a5^3
		// +160*a0*a1^3*a3*a4*a5^2-36*a0*a1^3*a4^3*a5+1020*a0*a1^2*a2^2*a4*a5^2+560*a0*a1^2*a2*a3^2*a5^2
		// -746*a0*a1^2*a2*a3*a4^2*a5+144*a0*a1^2*a2*a4^4+24*a0*a1^2*a3^3*a4*a5-6*a0*a1^2*a3^2*a4^3
		// +356*a0*a1*a2^2*a3^2*a4*a5-80*a0*a1*a2^2*a3*a4^3-630*a0*a1*a2^3*a3*a5^2+24*a0*a1*a2^3*a3^2*a5
		// -72*a0*a1*a2*a3^4*a5+18*a0*a1*a2*a3^3*a4^2+108*a0*a2^5*a5^2-72*a0*a2^4*a3*a4*a5+16*a0*a2^4*a4^3
		// +16*a0*a2^3*a3^3*a5-4*a0*a2^3*a3^2*a4^2+256*a1^5*a5^3-192*a1^4*a2*a4*a5^2-128*a1^4*a3^2*a5^2
		// +144*a1^4*a3*a4^2*a5-27*a1^4*a4^4+144*a1^3*a2^2*a3*a5^2-6*a1^3*a2^2*a4^2*a5-80*a1^3*a2*a3^2*a4*a5
		// +18*a1^3*a2*a3*a4^3+16*a1^3*a3^4*a5-4*a1^3*a3^3*a4^2-27*a1^2*a2^4*a5^2+18*a1^2*a2^3*a3*a4*a5
		// -4*a1^2*a2^3*a4^3-4*a1^2*a2^2*a3^3*a5+a1^2*a2^2*a3^2*a4^2

		// 3125*a^4*f^4-2500*a^3*b*e*f^3-3750*a^3*c*d*f^3+2000*a^3*c*e^2*f^2+2250*a^3*d^2*e*f^2
		// -1600*a^3*d*e^3*f+256*a^3*e^5+2000*a^2*b^2*d*f^3-50*a^2*b^2*e^2*f^2+2250*a^2*b*c^2*f^3
		// -2050*a^2*b*c*d*e*f^2+160*a^2*b*c*e^3*f-900*a^2*b*d^2*f^2+1020*a^2*b*d^2*e^2*f
		// -192*a^2*b*d*e^4-900*a^2*c^3*e*f^2+825*a^2*c^2*d^2*f^2+560*a^2*c^2*d*e^2*f-128*a^2*c^2*e^4
		// -630*a^2*c*d^3*e*f+144*a^2*c*d^2*e^3+108*a^2*d^5*f-27*a^2*d^4*e^2-1600*a*b^3*c*f^3
		// +160*a*b^3*d*e*f^2-36*a*b^3*e^3*f+1020*a*b^2*c^2*e*f^2+560*a*b^2*c*d^2*f^2
		// -746*a*b^2*c*d*e^2*f+144*a*b^2*c*e^4+24*a*b^2*d^3*e*f-6*a*b^2*d^2*e^3
		// +356*a*b*c^2*d^2*e*f-80*a*b*c^2*d*e^3-630*a*b*c^3*d*f^2+24*a*b*c^3*d^2*f
		// -72*a*b*c*d^4*f+18*a*b*c*d^3*e^2+108*a*c^5*f^2-72*a*c^4*d*e*f+16*a*c^4*e^3
		// +16*a*c^3*d^3*f-4*a*c^3*d^2*e^2+256*b^5*f^3-192*b^4*c*e*f^2-128*b^4*d^2*f^2
		// +144*b^4*d*e^2*f-27*b^4*e^4+144*b^3*c^2*d*f^2-6*b^3*c^2*e^2*f-80*b^3*c*d^2*e*f
		// +18*b^3*c*d*e^3+16*b^3*d^4*f-4*b^3*d^3*e^2-27*b^2*c^4*f^2+18*b^2*c^3*d*e*f
		// -4*b^2*c^3*e^3-4*b^2*c^2*d^3*f+b^2*c^2*d^2*e^2
		private final static IExpr QUINTIC = Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(
				Plus(Plus(Plus(
						Plus(Plus(Plus(
								Plus(Plus(
										Plus(Plus(
												Plus(Plus(
														Plus(Plus(
																Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(
																		Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(
																				Plus(Plus(Plus(Plus(Plus(Plus(Plus(
																						Plus(Plus(Times(Times(
																								integer(3125L),
																								Power(F.a, C4)),
																								Power(F.f, C4)),
																								Times(CN1, Times(Times(
																										Times(Times(
																												integer(2500L),
																												Power(F.a,
																														C3)),
																												F.b),
																										F.e),
																										Power(F.f,
																												C3)))),
																								Times(CN1, Times(Times(
																										Times(Times(
																												integer(3750L),
																												Power(F.a,
																														C3)),
																												F.c),
																										F.d),
																										Power(F.f,
																												C3)))),
																						Times(Times(Times(
																								Times(integer(2000L),
																										Power(F.a, C3)),
																								F.c),
																								Power(F.e, C2)),
																								Power(F.f, C2))),
																						Times(Times(Times(
																								Times(integer(2250L),
																										Power(F.a, C3)),
																								Power(F.d, C2)), F.e),
																								Power(F.f, C2))),
																						Times(CN1, Times(Times(Times(
																								Times(integer(1600L),
																										Power(F.a, C3)),
																								F.d), Power(F.e, C3)),
																								F.f))),
																						Times(Times(
																								integer(256L),
																								Power(F.a, C3)),
																								Power(F.e, C5))),
																						Times(Times(Times(
																								Times(integer(2000L),
																										Power(F.a, C2)),
																								Power(F.b, C2)), F.d),
																								Power(F.f, C3))),
																						Times(CN1, Times(Times(Times(
																								Times(integer(50L),
																										Power(F.a, C2)),
																								Power(F.b, C2)),
																								Power(F.e, C2)),
																								Power(F.f, C2)))),
																						Times(Times(Times(
																								Times(integer(2250L),
																										Power(F.a, C2)),
																								F.b), Power(F.c, C2)),
																								Power(F.f, C3))),
																				Times(CN1, Times(
																						Times(Times(Times(Times(
																								Times(integer(2050L),
																										Power(F.a, C2)),
																								F.b), F.c), F.d), F.e),
																						Power(F.f, C2)))),
																				Times(Times(
																						Times(Times(
																								Times(integer(160L),
																										Power(F.a, C2)),
																								F.b), F.c),
																						Power(F.e, C3)), F.f)),
																				Times(CN1, Times(
																						Times(Times(Times(integer(900L),
																								Power(F.a, C2)), F.b),
																								Power(F.d, C2)),
																						Power(F.f, C2)))),
																				Times(Times(Times(
																						Times(Times(integer(1020L),
																								Power(F.a, C2)), F.b),
																						Power(F.d, C2)),
																						Power(F.e, C2)), F.f)),
																				Times(CN1, Times(Times(
																						Times(Times(integer(192L),
																								Power(F.a, C2)), F.b),
																						F.d), Power(F.e, C4)))),
																				Times(CN1, Times(
																						Times(Times(
																								Times(integer(900L),
																										Power(F.a, C2)),
																								Power(F.c, C3)), F.e),
																						Power(F.f, C2)))),
																				Times(Times(
																						Times(Times(integer(825L),
																								Power(F.a, C2)),
																								Power(F.c, C2)),
																						Power(F.d, C2)),
																						Power(F.f, C2))),
																				Times(Times(
																						Times(Times(
																								Times(integer(560L),
																										Power(F.a, C2)),
																								Power(F.c, C2)), F.d),
																						Power(F.e, C2)), F.f)),
																				Times(CN1,
																						Times(Times(
																								Times(integer(128L),
																										Power(F.a, C2)),
																								Power(F.c, C2)),
																								Power(F.e, C4)))),
																		Times(CN1,
																				Times(Times(
																						Times(Times(
																								Times(integer(630L),
																										Power(F.a, C2)),
																								F.c), Power(F.d, C3)),
																						F.e), F.f))),
																		Times(Times(
																				Times(Times(integer(144L),
																						Power(F.a, C2)), F.c),
																				Power(F.d, C2)), Power(F.e, C3))),
																		Times(Times(
																				Times(integer(108L), Power(F.a, C2)),
																				Power(F.d, C5)), F.f)),
																		Times(CN1,
																				Times(Times(Times(integer(27L),
																						Power(F.a, C2)),
																						Power(F.d, C4)),
																						Power(F.e, C2)))),
																		Times(CN1, Times(
																				Times(Times(Times(integer(1600L), F.a),
																						Power(F.b, C3)), F.c),
																				Power(F.f, C3)))),
																		Times(Times(
																				Times(Times(Times(integer(160L), F.a),
																						Power(F.b, C3)), F.d),
																				F.e), Power(F.f, C2))),
																		Times(CN1,
																				Times(Times(
																						Times(Times(integer(36L), F.a),
																								Power(F.b, C3)),
																						Power(F.e, C3)), F.f))),
																		Times(Times(Times(
																				Times(Times(integer(1020L), F.a),
																						Power(F.b, C2)),
																				Power(F.c, C2)), F.e), Power(F.f, C2))),
																		Times(Times(
																				Times(Times(Times(integer(560L), F.a),
																						Power(F.b, C2)), F.c),
																				Power(F.d, C2)),
																				Power(F.f, C2))),
																		Times(CN1,
																				Times(Times(Times(
																						Times(Times(
																								Times(integer(746L),
																										F.a),
																								Power(F.b, C2)), F.c),
																						F.d), Power(F.e, C2)), F.f))),
																		Times(Times(Times(Times(integer(144L), F.a),
																				Power(F.b, C2)), F.c), Power(F.e, C4))),
																Times(Times(Times(
																		Times(Times(integer(24L), F.a), Power(F.b, C2)),
																		Power(F.d, C3)), F.e), F.f)),
																Times(CN1, Times(Times(
																		Times(Times(integer(6L), F.a), Power(F.b, C2)),
																		Power(F.d, C2)), Power(F.e, C3)))),
														Times(Times(Times(Times(Times(Times(integer(356L), F.a), F.b),
																Power(F.c, C2)), Power(F.d, C2)), F.e), F.f)),
														Times(CN1,
																Times(Times(Times(Times(Times(integer(80L), F.a), F.b),
																		Power(F.c, C2)), F.d), Power(F.e, C3)))),
												Times(CN1,
														Times(Times(Times(Times(Times(integer(630L), F.a), F.b),
																Power(F.c, C3)), F.d), Power(F.f, C2)))),
												Times(Times(Times(Times(Times(integer(24L), F.a), F.b), Power(F.c, C3)),
														Power(F.d, C2)), F.f)),
										Times(CN1,
												Times(Times(Times(Times(Times(integer(72L), F.a), F.b), F.c),
														Power(F.d, C4)), F.f))),
										Times(Times(Times(Times(Times(integer(18L), F.a), F.b), F.c), Power(F.d, C3)),
												Power(F.e, C2))),
								Times(Times(Times(integer(108L), F.a), Power(F.c, C5)), Power(F.f, C2))),
								Times(CN1,
										Times(Times(Times(Times(Times(integer(72L), F.a), Power(F.c, C4)), F.d), F.e),
												F.f))),
								Times(Times(Times(integer(16L), F.a), Power(F.c, C4)), Power(F.e, C3))),
						Times(Times(Times(Times(integer(16L), F.a), Power(F.c, C3)), Power(F.d, C3)), F.f)),
						Times(CN1,
								Times(Times(Times(Times(C4, F.a), Power(F.c, C3)), Power(F.d, C2)), Power(F.e, C2)))),
						Times(Times(integer(256L), Power(F.b, C5)), Power(F.f, C3))),
				Times(CN1, Times(Times(Times(Times(integer(192L), Power(F.b, C4)), F.c), F.e), Power(F.f, C2)))),
				Times(CN1, Times(Times(Times(integer(128L), Power(F.b, C4)), Power(F.d, C2)), Power(F.f, C2)))),
				Times(Times(Times(Times(integer(144L), Power(F.b, C4)), F.d), Power(F.e, C2)), F.f)),
				Times(CN1, Times(Times(integer(27L), Power(F.b, C4)), Power(F.e, C4)))),
				Times(Times(Times(Times(integer(144L), Power(F.b, C3)), Power(F.c, C2)), F.d), Power(F.f, C2))),
				Times(CN1,
						Times(Times(Times(Times(integer(6L), Power(F.b, C3)), Power(F.c, C2)), Power(F.e, C2)), F.f))),
				Times(CN1,
						Times(Times(Times(Times(Times(integer(80L), Power(F.b, C3)), F.c), Power(F.d, C2)), F.e),
								F.f))),
				Times(Times(Times(Times(integer(18L), Power(F.b, C3)), F.c), F.d), Power(F.e, C3))),
				Times(Times(Times(integer(16L), Power(F.b, C3)), Power(F.d, C4)), F.f)),
				Times(CN1, Times(Times(Times(C4, Power(F.b, C3)), Power(F.d, C3)), Power(F.e, C2)))),
				Times(CN1, Times(Times(Times(integer(27L), Power(F.b, C2)), Power(F.c, C4)), Power(F.f, C2)))),
				Times(Times(Times(Times(Times(integer(18L), Power(F.b, C2)), Power(F.c, C3)), F.d), F.e), F.f)),
				Times(CN1, Times(Times(Times(C4, Power(F.b, C2)), Power(F.c, C3)), Power(F.e, C3)))),
				Times(CN1, Times(Times(Times(Times(C4, Power(F.b, C2)), Power(F.c, C2)), Power(F.d, C3)), F.f))),
				Times(Times(Times(Power(F.b, C2), Power(F.c, C2)), Power(F.d, C2)), Power(F.e, C2)));

		private ISymbol[] vars = { F.a, F.b, F.c, F.d, F.e, F.f };

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr arg2 = ast.arg2();
			if (!arg2.isSymbol()) {
				return F.NIL;
			}
			IExpr expr = F.evalExpandAll(ast.arg1(), engine);
			try {
				ExprPolynomialRing ring = new ExprPolynomialRing(F.List(arg2));
				ExprPolynomial poly = ring.create(expr);

				long n = poly.degree();
				if (n >= 2L && n <= 5L) {
					IAST result = poly.coefficientList();
					IASTAppendable rules = F.ListAlloc(result.size());
					rules.appendArgs(result.size(), i -> F.Rule(vars[i - 1], result.get(i)));
					switch ((int) n) {
					case 2:
						return QUADRATIC.replaceAll(rules);
					case 3:
						return CUBIC.replaceAll(rules);
					case 4:
						return QUARTIC.replaceAll(rules);
					case 5:
						return QUINTIC.replaceAll(rules);
					}
				}
				IExpr fN = poly.leadingBaseCoefficient();// coefficient(n);
				ExprPolynomial polyDiff = poly.derivative();
				// see:
				// http://en.wikipedia.org/wiki/Discriminant#Discriminant_of_a_polynomial
				return F.Divide(F.Times(F.Power(F.CN1, (n * (n - 1) / 2)),
						F.Resultant(poly.getExpr(), polyDiff.getExpr(), arg2)), fN);
			} catch (RuntimeException ex) {
				throw new WrongArgumentType(ast, expr, 1, "Polynomial expected!");
			}
		}

		@Override
		public void setUp(final ISymbol symbol) {
			symbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * Exponent(polynomial, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the maximum power with which <code>x</code> appears in the expanded form of <code>polynomial</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Exponent(1+x^2+a*x^3, x)
	 * 3
	 * </pre>
	 */
	private static class Exponent extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);
			IExpr cached = F.REMEMBER_AST_CACHE.getIfPresent(ast);
			if (cached != null) {
				return cached;
			}

			final IExpr form = engine.evalPattern(ast.arg2());
			if (form.isList()) {
				return ((IAST) form).mapThread(ast, 2);
			}
			ISymbol sym = F.Max;
			if (ast.isAST3()) {
				final IExpr arg3 = engine.evaluate(ast.arg3());
				if (arg3.isSymbol()) {
					sym = (ISymbol) arg3;
				}
			}
			Set<IExpr> collector = new TreeSet<IExpr>();

			IExpr expr = F.evalExpandAll(ast.arg1(), engine).normal();
			if (expr.isZero()) {
				collector.add(F.CNInfinity);
			} else if (expr.isAST()) {
				IAST arg1 = (IAST) expr;
				// final IPatternMatcher matcher = new PatternMatcherEvalEngine(form, engine);
				final IPatternMatcher matcher = engine.evalPatternMatcher(form);
				if (arg1.isPower()) {
					if (matcher.test(arg1.base(), engine)) {
						collector.add(arg1.exponent());
					} else {
						collector.add(F.C0);
					}
				} else if (arg1.isPlus()) {
					for (int i = 1; i < arg1.size(); i++) {
						if (arg1.get(i).isAtom()) {
							if (arg1.get(i).isSymbol()) {
								if (matcher.test(arg1.get(i), engine)) {
									collector.add(F.C1);
								} else {
									collector.add(F.C0);
								}
							} else {
								collector.add(F.C0);
							}
						} else if (arg1.get(i).isPower()) {
							IAST pow = (IAST) arg1.get(i);
							if (matcher.test(pow.base(), engine)) {
								collector.add(pow.exponent());
							} else {
								collector.add(F.C0);
							}
						} else if (arg1.get(i).isTimes()) {
							timesExponent((IAST) arg1.get(i), matcher, collector, engine);
						} else {
							collector.add(F.C0);
						}
					}
				} else if (arg1.isTimes()) {
					timesExponent(arg1, matcher, collector, engine);
				}

			} else if (expr.isSymbol()) {
				// final PatternMatcher matcher = new PatternMatcherEvalEngine(form, engine);
				final IPatternMatcher matcher = engine.evalPatternMatcher(form);
				if (matcher.test(expr)) {
					collector.add(F.C1);
				} else {
					collector.add(F.C0);
				}
			} else {
				collector.add(F.C0);
			}

			if (collector.size() == 0) {
				collector.add(F.C0);
			}
			IASTAppendable result = F.ast(sym, collector.size(), false);
			result.appendAll(collector);
			// for (IExpr exponent : collector) {
			// result.append(exponent);
			// }
			F.REMEMBER_AST_CACHE.put(ast, result);
			return result;
		}

		private void timesExponent(IAST timesAST, final IPatternMatcher matcher, Set<IExpr> collector,
				EvalEngine engine) {
			boolean evaled = false;
			IExpr argi;
			for (int i = 1; i < timesAST.size(); i++) {
				argi = timesAST.get(i);
				if (argi.isPower()) {
					if (matcher.test(argi.base(), engine)) {
						collector.add(argi.exponent());
						evaled = true;
						break;
					}
				} else if (argi.isSymbol()) {
					if (matcher.test(argi, engine)) {
						collector.add(F.C1);
						evaled = true;
						break;
					}
				}
			}
			if (!evaled) {
				collector.add(F.C0);
			}
		}

	}

	/**
	 * <pre>
	 * Resultant(polynomial1, polynomial2, var)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes the resultant of the polynomials <code>polynomial1</code> and <code>polynomial2</code> with respect to
	 * the variable <code>var</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Resultant">Wikipedia - Resultant</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Resultant((x-y)^2-2 , y^3-5, y)
	 * 17-60*x+12*x^2-10*x^3-6*x^4+x^6
	 * </pre>
	 */
	private static class Resultant extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);
			// TODO allow multinomials
			IExpr arg3 = Validate.checkSymbolType(ast, 3);
			ISymbol x = (ISymbol) arg3;
			IExpr a = F.evalExpandAll(ast.arg1(), engine);
			IExpr b = F.evalExpandAll(ast.arg2(), engine);
			ExprPolynomialRing ring = new ExprPolynomialRing(F.List(x));
			try {
				// check if a is a polynomial otherwise check ArithmeticException, ClassCastException
				ring.create(a);
				// check if b is a polynomial otherwise check ArithmeticException, ClassCastException
				ring.create(b);
				return F.Together(resultant(a, b, x, engine));
			} catch (RuntimeException ex) {
				throw new WrongArgumentType(ast, b, 2, "Polynomial expected!");
			}
		}

		private IExpr resultant(IExpr a, IExpr b, ISymbol x, EvalEngine engine) {
			IExpr aExp = F.Exponent.of(engine, a, x);
			IExpr bExp = F.Exponent.of(engine, b, x);
			if (b.isFree(x)) {
				return F.Power(b, aExp);
			}
			IExpr abExp = aExp.times(bExp);
			if (F.Less.ofQ(engine, aExp, bExp)) {
				return F.Times(F.Power(F.CN1, abExp), resultant(b, a, x, engine));
			}

			IExpr r = F.PolynomialRemainder.of(engine, a, b, x);
			IExpr rExp = r;
			if (!r.isZero()) {
				rExp = F.Exponent.of(engine, r, x);
			}
			return F.Times(F.Power(F.CN1, abExp), F.Power(F.Coefficient(b, x, bExp), F.Subtract(aExp, rExp)),
					resultant(b, r, x, engine));
		}

		private IExpr jasResultant(IExpr a, IExpr b, ISymbol x, EvalEngine engine) {
			VariablesSet eVar = new VariablesSet();
			eVar.addVarList(x);

			try {
				// ASTRange r = new ASTRange(eVar.getVarList(), 1);
				List<IExpr> varList = eVar.getVarList().copyTo();
				JASConvert<edu.jas.arith.BigInteger> jas = new JASConvert<edu.jas.arith.BigInteger>(varList,
						edu.jas.arith.BigInteger.ZERO);
				GenPolynomial<edu.jas.arith.BigInteger> poly = jas.expr2JAS(a, false);
				GenPolynomial<edu.jas.arith.BigInteger> temp = jas.expr2JAS(b, false);
				GreatestCommonDivisorAbstract<edu.jas.arith.BigInteger> factory = GCDFactory
						.getImplementation(edu.jas.arith.BigInteger.ZERO);
				poly = factory.resultant(poly, temp);
				return jas.integerPoly2Expr(poly);
			} catch (JASConversionException e) {
				try {
					if (eVar.size() == 0) {
						return F.NIL;
					}
					IAST vars = eVar.getVarList();
					ExprPolynomialRing ring = new ExprPolynomialRing(vars);
					ExprPolynomial pol1 = ring.create(a);
					ExprPolynomial pol2 = ring.create(b);
					List<IExpr> varList = eVar.getVarList().copyTo();
					JASIExpr jas = new JASIExpr(varList, true);
					GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
					GenPolynomial<IExpr> p2 = jas.expr2IExprJAS(pol2);

					GreatestCommonDivisor<IExpr> factaory = GCDFactory.getImplementation(ExprRingFactory.CONST);
					p1 = factaory.resultant(p1, p2);
					return jas.exprPoly2Expr(p1);
				} catch (RuntimeException rex) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
				}

			}
			return F.NIL;
		}

		// public static IExpr resultant(IAST result, IAST resultListDiff) {
		// // create sylvester matrix
		// IAST sylvester = F.List();
		// IAST row = F.List();
		// IAST srow;
		// final int n = resultListDiff.size() - 2;
		// final int m = result.size() - 2;
		// final int n2 = m + n;
		//
		// for (int i = result.argSize(); i > 0; i--) {
		// row.add(result.get(i));
		// }
		// for (int i = 0; i < n; i++) {
		// // for each row
		// srow = F.List();
		// int j = 0;
		// while (j < n2) {
		// if (j < i) {
		// srow.add(F.C0);
		// j++;
		// } else if (i == j) {
		// for (int j2 = 1; j2 < row.size(); j2++) {
		// srow.add(row.get(j2));
		// j++;
		// }
		// } else {
		// srow.add(F.C0);
		// j++;
		// }
		// }
		// sylvester.add(srow);
		// }
		//
		// row = F.List();
		// for (int i = resultListDiff.argSize(); i > 0; i--) {
		// row.add(resultListDiff.get(i));
		// }
		// for (int i = n; i < n2; i++) {
		// // for each row
		// srow = F.List();
		// int j = 0;
		// int k = n;
		// while (j < n2) {
		// if (k < i) {
		// srow.add(F.C0);
		// j++;
		// k++;
		// } else if (i == k) {
		// for (int j2 = 1; j2 < row.size(); j2++) {
		// srow.add(row.get(j2));
		// j++;
		// k++;
		// }
		// } else {
		// srow.add(F.C0);
		// j++;
		// k++;
		// }
		// }
		// sylvester.add(srow);
		// }
		//
		// if (sylvester.isAST0()) {
		// return null;
		// }
		// // System.out.println(sylvester);
		// return F.eval(F.Det(sylvester));
		// }

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * NRoots(poly)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the numerical roots of polynomial <code>poly</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; NRoots(x^3-4*x^2+x+6)
	 * {2.9999999999999996,-1.0000000000000002,1.9999999999999998}
	 * </pre>
	 * 
	 * <h3>Related terms</h3>
	 * <p>
	 * <a href="DSolve.md">DSolve</a>, <a href="Eliminate.md">Eliminate</a>,
	 * <a href="GroebnerBasis.md">GroebnerBasis</a>, <a href="FindRoot.md">FindRoot</a>, <a href="Solve.md">Solve</a>
	 * </p>
	 */
	private static class NRoots extends AbstractFunctionEvaluator {
		/**
		 * Determine the numerical roots of a univariate polynomial
		 * 
		 * See Wikipedia entries for: <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>,
		 * <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and
		 * <a href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
		 * 
		 * @see Roots
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			IAST variables;
			if (ast.size() == 2) {
				VariablesSet eVar = new VariablesSet(ast.arg1());
				if (!eVar.isSize(1)) {
					// factor only possible for univariate polynomials
					engine.printMessage("NRoots: factorization only possible for univariate polynomials");
					return F.NIL;
				}
				variables = eVar.getVarList();
			} else {
				variables = ast.arg2().orNewList();
			}
			IExpr temp = roots(ast.arg1(), variables, engine);
			if (!temp.isList()) {
				return F.NIL;
			}
			IAST list = (IAST) temp;
			int size = list.size();
			IASTAppendable result = F.ListAlloc(size);
			return result.appendArgs(size, i -> engine.evalN(list.get(i)));
			// for (int i = 1; i < size; i++) {
			// result.append(engine.evalN(list.get(i)));
			// }
			// return result;
		}

		/**
		 * 
		 * @param coefficients
		 * @return <code>F.NIL</code> if the result couldn't be evaluated
		 */
		private static IAST rootsUp2Degree3(double[] coefficients) {
			if (coefficients.length == 0) {
				return F.NIL;
			}
			if (coefficients.length == 1) {
				return quadratic(0.0, 0.0, coefficients[0]);
			}
			if (coefficients.length == 2) {
				return quadratic(0.0, coefficients[1], coefficients[0]);
			}
			if (coefficients.length == 3) {
				return quadratic(coefficients[2], coefficients[1], coefficients[0]);
			}
			IAST result = F.NIL;
			if (coefficients.length == 4) {
				result = cubic(coefficients[3], coefficients[2], coefficients[1], coefficients[0]);
			}
			return result;
		}

		private static IAST quadratic(double a, double b, double c) {
			IASTAppendable result = F.ListAlloc(2);
			double discriminant = (b * b - (4 * a * c));
			if (F.isZero(discriminant)) {
				double bothEqual = ((-b / (2.0 * a)));
				result.append(F.num(bothEqual));
				result.append(F.num(bothEqual));
			} else if (discriminant < 0.0) {
				// two complex roots
				double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
				double realPart = (-b / (2.0 * a));
				result.append(F.complex(realPart, imaginaryPart));
				result.append(F.complex(realPart, -imaginaryPart));
			} else {
				// two real roots
				double real1 = ((-b + Math.sqrt(discriminant)) / (2.0 * a));
				double real2 = ((-b - Math.sqrt(discriminant)) / (2.0 * a));
				result.append(F.num(real1));
				result.append(F.num(real2));
			}
			return result;
		}

		/**
		 * See <a href= "http://stackoverflow.com/questions/13328676/c-solving-cubic-equations" > http
		 * ://stackoverflow.com/questions/13328676/c-solving-cubic-equations</a>
		 * 
		 * @param a
		 * @param b
		 * @param c
		 * @param d
		 */
		private static IAST cubic(double a, double b, double c, double d) {
			if (F.isZero(a)) {
				return F.NIL;
			}
			if (F.isZero(d)) {
				return F.NIL;
			}
			IASTAppendable result = F.ListAlloc(3);
			b /= a;
			c /= a;
			d /= a;

			double q = (3.0 * c - (b * b)) / 9.0;
			double r = -(27.0 * d) + b * (9.0 * c - 2.0 * (b * b));
			r /= 54.0;
			double discriminant = q * q * q + r * r;

			double term1 = (b / 3.0);
			if (discriminant > 0) {
				// one root real, two are complex
				double s = r + Math.sqrt(discriminant);
				s = ((s < 0) ? -Math.pow(-s, (1.0 / 3.0)) : Math.pow(s, (1.0 / 3.0)));
				double t = r - Math.sqrt(discriminant);
				t = ((t < 0) ? -Math.pow(-t, (1.0 / 3.0)) : Math.pow(t, (1.0 / 3.0)));
				result.append(F.num(-term1 + s + t));
				term1 += (s + t) / 2.0;
				double realPart = -term1;
				term1 = Math.sqrt(3.0) * (-t + s) / 2;
				result.append(F.complex(realPart, term1));
				result.append(F.complex(realPart, -term1));
				return result;
			}

			// The remaining options are all real
			double r13;
			if (F.isZero(discriminant)) {
				// All roots real, at least two are equal.
				r13 = ((r < 0) ? -Math.pow(-r, (1.0 / 3.0)) : Math.pow(r, (1.0 / 3.0)));
				result.append(F.num(-term1 + 2.0 * r13));
				result.append(F.num(-(r13 + term1)));
				result.append(F.num(-(r13 + term1)));
				return result;
			}

			// Only option left is that all roots are real and unequal (to get here,
			// q < 0)
			q = -q;
			double dum1 = q * q * q;
			dum1 = Math.acos(r / Math.sqrt(dum1));
			r13 = 2.0 * Math.sqrt(q);
			result.append(F.num(-term1 + r13 * Math.cos(dum1 / 3.0)));
			result.append(F.num(-term1 + r13 * Math.cos((dum1 + 2.0 * Math.PI) / 3.0)));
			result.append(F.num(-term1 + r13 * Math.cos((dum1 + 4.0 * Math.PI) / 3.0)));
			return result;
		}
	}

	/**
	 * Determine complex root intervals of a univariate polynomial
	 * 
	 */
	private static class RootIntervals extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return croots(ast.arg1(), false);
		}

		/**
		 * Complex numeric roots intervals.
		 * 
		 * @param ast
		 * @return
		 */
		public static IASTAppendable croots(final IExpr arg, boolean numeric) {

			try {
				VariablesSet eVar = new VariablesSet(arg);
				if (!eVar.isSize(1)) {
					// only possible for univariate polynomials
					return F.NIL;
				}
				IExpr expr = F.evalExpandAll(arg);
				// ASTRange r = new ASTRange(eVar.getVarList(), 1);
				// List<IExpr> varList = r;
				List<IExpr> varList = eVar.getVarList().copyTo();

				ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(new BigRational(1));
				ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

				JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(varList, cfac);
				GenPolynomial<Complex<BigRational>> poly = jas.numericExpr2JAS(expr);

				Squarefree<Complex<BigRational>> engine = SquarefreeFactory
						.<Complex<BigRational>>getImplementation(cfac);
				poly = engine.squarefreePart(poly);

				List<Rectangle<BigRational>> roots = cr.complexRoots(poly);

				BigRational len = new BigRational(1, 100000L);

				IASTAppendable resultList = F.ListAlloc(roots.size());

				if (numeric) {
					for (Rectangle<BigRational> root : roots) {
						Rectangle<BigRational> refine = cr.complexRootRefinement(root, poly, len);
						resultList.append(JASConvert.jas2Numeric(refine.getCenter(), Config.DEFAULT_ROOTS_CHOP_DELTA));
					}
				} else {
					IASTAppendable rectangleList;
					for (Rectangle<BigRational> root : roots) {
						rectangleList = F.ListAlloc(4);

						Rectangle<BigRational> refine = cr.complexRootRefinement(root, poly, len);
						rectangleList.append(JASConvert.jas2Complex(refine.getNW()));
						rectangleList.append(JASConvert.jas2Complex(refine.getSW()));
						rectangleList.append(JASConvert.jas2Complex(refine.getSE()));
						rectangleList.append(JASConvert.jas2Complex(refine.getNE()));
						resultList.append(rectangleList);
						// System.out.println("refine = " + refine);

					}
				}
				return resultList;
			} catch (InvalidBoundaryException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			} catch (JASConversionException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Roots(polynomial - equation, var)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * determine the roots of a univariate polynomial equation with respect to the variable <code>var</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Roots(3*x^3-5*x^2+5*x-2==0,x)
	 * x==2/3||x==1/2-I*1/2*Sqrt(3)||x==1/2+I*1/2*Sqrt(3)
	 * </pre>
	 */
	private static class Roots extends AbstractFunctionEvaluator {

		/**
		 * Determine the roots of a univariate polynomial
		 * 
		 * See Wikipedia entries for: <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>,
		 * <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and
		 * <a href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr arg1 = ast.arg1();
			if (arg1.isEqual()) {
				IAST equalAST = (IAST) arg1;
				if (equalAST.arg2().isZero()) {
					arg1 = equalAST.arg1();
				} else {
					arg1 = engine.evaluate(F.Subtract(equalAST.arg1(), equalAST.arg2()));
				}
			} else {
				throw new WrongArgumentType(ast, ast.arg1(), 1, "Equal() expression expected!");
			}
			VariablesSet eVar = null;
			if (ast.arg2().isList()) {
				eVar = new VariablesSet(ast.arg2());
			} else {
				eVar = new VariablesSet();
				eVar.add(ast.arg2());
			}
			if (!eVar.isSize(1)) {
				// factorization only possible for univariate polynomials
				throw new WrongArgumentType(ast, ast.arg2(), 2, "Only one variable expected");
			}
			IAST variables = eVar.getVarList();
			IExpr variable = variables.arg1();
			IAST list = roots(arg1, false, variables, engine);
			if (list.isPresent()) {
				IASTAppendable or = F.Or();
				for (int i = 1; i < list.size(); i++) {
					or.append(F.Equal(variable, list.get(i)));
				}
				return or;
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * ChebyshevT(n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Chebyshev polynomial of the first kind <code>T_n(x)</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Chebyshev_polynomials">Wikipedia - Chebyshev polynomials</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ChebyshevT(8, x)    
	 * 1-32*x^2+160*x^4-256*x^6+128*x^8
	 * </pre>
	 */
	final private static class ChebyshevT extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr n = ast.arg1();
			IExpr z = ast.arg2();
			if (engine.isNumericMode() && n.isNumber() && z.isNumber()) {
				// (n, z) => Cos(n*ArcCos(z))
				return F.Cos(F.Times(n, F.ArcCos(z)));
			}

			int degree = n.toIntDefault(Integer.MIN_VALUE);
			if (degree != Integer.MIN_VALUE) {
				if (degree < 0) {
					degree *= -1;
				}
				return PolynomialsUtils.createChebyshevPolynomial(degree, ast.arg2());
			}
			if (n.isNumEqualRational(F.C1D2) || n.isNumEqualRational(F.CN1D2)) {
				// (1/2, z) => Cos(ArcCos(z)/2)
				// (-1/2, z) => Cos(ArcCos(z)/2)
				return F.Cos(F.Times(F.C1D2, F.ArcCos(z)));
			}
			if (z.isZero()) {
				// Cos(Pi*n*(1/2))
				return F.Cos(F.Times(F.C1D2, F.Pi, n));
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * ChebyshevU(n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Chebyshev polynomial of the second kind <code>U_n(x)</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Chebyshev_polynomials">Wikipedia - Chebyshev polynomials</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ChebyshevU(8, x)    
	 * 1-40*x^2+240*x^4-448*x^6+256*x^8
	 * </pre>
	 */
	final private static class ChebyshevU extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr n = ast.arg1();
			IExpr z = ast.arg2();
			if (engine.isNumericMode() && n.isNumber() && z.isNumber()) {
				// Sin((n + 1)*ArcCos(z))/Sqrt(1 - z^2)
				return F.Times(F.Power(F.Plus(F.C1, F.Negate(F.Sqr(z))), F.CN1D2),
						F.Sin(F.Times(F.Plus(F.C1, n), F.ArcCos(z))));
			}

			int degree = n.toIntDefault(Integer.MIN_VALUE);
			if (degree != Integer.MIN_VALUE) {
				if (degree < 0) {
					if (degree == (-1)) {
						return F.C0;
					}
					if (degree == (-2)) {
						return F.CN1;
					}
					return F.NIL;
				}
				if (degree == 0) {
					return F.C1;
				}
				if (degree == 1) {
					return F.Times(F.C2, z);
				}
				// (n, z) => Sum(((-1)^k*(n - k)!*(2*z)^(n - 2*k))/(k!*(n - 2*k)!), {k, 0, Floor(n/2)})
				return F.sum(k -> F.Times(F.Power(F.CN1, k), F.Power(F.Times(F.C2, z), F.Plus(F.Times(F.CN2, k), n)),
						F.Power(F.Times(F.Factorial(k), F.Factorial(F.Plus(F.Times(F.CN2, k), n))), -1),
						F.Factorial(F.Plus(F.Negate(k), n))), 0, degree / 2);
			}

			if (n.isNumEqualRational(F.CN1D2)) {
				// (-1/2, z) => 1/(Sqrt(2)* Sqrt(1 + z))
				return F.Times(F.C1DSqrt2, F.Power(F.Plus(F.C1, z), F.CN1D2));
			}
			if (n.isNumEqualRational(F.C1D2)) {
				// (1/2, z) => (1 + 2*z)/(Sqrt(2)* Sqrt(1 + z))
				return F.Times(F.C1DSqrt2, F.Plus(F.C1, F.Times(F.C2, z)), F.Power(F.Plus(F.C1, z), F.CN1D2));
			}
			if (z.isZero()) {
				// Cos((Pi*n)/2)
				return F.Cos(F.Times(F.C1D2, n, F.Pi));
			}
			if (z.isOne()) {
				return F.Plus(F.C1, n);
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * BellY(n, k, {x1, x2, ... , xN})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the second kind of Bell polynomials (incomplete Bell polynomials).
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Bell_polynomials">Wikipedia - Bell polynomials</a></li>
	 * </ul>
	 * 
	 * <pre>
	 * &gt;&gt; BellY(6, 2, {x1, x2, x3, x4, x5})
	 * 10*x3^2+15*x2*x4+6*x1*x5
	 * </pre>
	 */
	final private static class BellY extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);
			if (ast.arg2().isInteger() && ast.arg2().isInteger()) {
				int n = ast.arg1().toIntDefault(Integer.MIN_VALUE);
				int k = ast.arg2().toIntDefault(Integer.MIN_VALUE);
				if (n < 0 || k < 0 || //
						!ast.arg3().isList() || //
						ast.arg3().isMatrix() != null) {
					return F.NIL;
				}
				if (n == 0 && k == 0) {
					return F.C1;
				}
				if (n == 0 || k == 0) {
					return F.C0;
				}
				int max = n - k + 2;
				if (max >= 0) {
					return bellY(n, k, (IAST) ast.arg3());
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * HermiteH(n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Hermite polynomial <code>H_n(x)</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Hermite_polynomials">Wikipedia - Hermite polynomials</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; HermiteH(8, x)    
	 * 1680-13440*x^2+13440*x^4-3584*x^6+256*x^8 
	 * 
	 * &gt;&gt; HermiteH(3, 1 + I)
	 * -28+I*4
	 * </pre>
	 */
	final private static class HermiteH extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
			if (degree > Integer.MIN_VALUE) {
				return PolynomialsUtils.createHermitePolynomial(degree, ast.arg2());
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * LaguerreL(n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Laguerre polynomial <code>L_n(x)</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Laguerre_polynomials">Wikipedia - Laguerre polynomials</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; LaguerreL(8, x)    
	 * 1-8*x+14*x^2-28/3*x^3+35/12*x^4-7/15*x^5+7/180*x^6-x^7/630+x^8/40320
	 * </pre>
	 */
	final private static class LaguerreL extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);
			int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
			if (degree != Integer.MIN_VALUE) {
				if (ast.size() == 4) {
					IExpr n = ast.arg1();
					IExpr l = ast.arg2();
					IExpr z = ast.arg3();
					if (n.isZero()) {
						return F.C1;
					}
					if (n.isOne()) {
						// -z + l + 1
						return F.Plus(F.C1, F.l, F.Negate(F.z));
					}

					// Recurrence relation for LaguerreL polynomials
					return F.Times(F.Power(n, -1),
							F.Plus(F.Times(F.CN1, F.Plus(F.CN1, l, n), F.LaguerreL(F.ZZ(degree - 2), l, z)),
									F.Times(F.Plus(F.C1, l, F.Times(F.C2, F.ZZ(degree - 1)), F.Negate(z)),
											F.LaguerreL(F.ZZ(degree - 1), l, z))));
				}
				if (degree == 0) {
					return F.C1;
				}
				if (degree > 0) {
					IExpr z = ast.arg2();
					return PolynomialsUtils.createLaguerrePolynomial(degree, z);
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * LegendreP(n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Legendre polynomial <code>P_n(x)</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Legendre_polynomials">Wikipedia - Legendre polynomials</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; LegendreP(4, x)    
	 * 3/8-15/4*x^2+35/8*x^4
	 * </pre>
	 */
	final private static class LegendreP extends AbstractFunctionEvaluator implements LegendrePRules {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
			if (degree > Integer.MIN_VALUE) {
				return PolynomialsUtils.createLegendrePolynomial(degree, ast.arg2());
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

	}

	/**
	 * <pre>
	 * LegendreQ(n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Legendre functions of the second kind <code>Q_n(x)</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Legendre_polynomials">Wikipedia - Legendre polynomials</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Expand(LegendreQ(4,z))   
	 * 55/24*z-35/8*z^3-3/16*Log(1-z)+15/8*z^2*Log(1-z)-35/16*z^4*Log(1-z)+3/16*Log(1+z)-15/8*z^2*Log(1+z)+35/16*z^4*Log(1+z)
	 * </pre>
	 */
	final static class LegendreQ extends AbstractFunctionEvaluator implements LegendreQRules {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

	}

	public static IExpr bellY(int n, int k, IAST symbols) {
		if (n == 0 && k == 0) {
			return F.C1;
		}
		if (n == 0 || k == 0) {
			return F.C0;
		}
		IExpr s = F.C0;
		int a = 1;
		int max = n - k + 2;
		for (int m = 1; m < max; m++) {
			if (!symbols.get(m).isZero()) {
				s = s.plus(F.Times(a, bellY(n - m, k - 1, symbols), symbols.get(m)));
			}
			a = a * (n - m) / m;
		}
		return s;
	}

	/**
	 * Get the coefficient list of a univariate polynomial.
	 * 
	 * @param polynomial
	 * @param variable
	 * @return <code>null</code> if the list couldn't be evaluated.
	 */
	private static double[] coefficients(IExpr polynomial, final ISymbol variable) throws JASConversionException {
		try {
			ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
			ExprPolynomial poly = ring.create(polynomial);
			// PolynomialOld poly = new PolynomialOld(polynomial, (ISymbol) variable);
			// if (!poly.isPolynomial()) {
			// throw new WrongArgumentType(polynomial, "Polynomial expected!");
			// }

			IAST list = poly.coefficientList();
			int degree = list.size() - 2;
			double[] result = new double[degree + 1];
			for (int i = 1; i < list.size(); i++) {
				ISignedNumber temp = list.get(i).evalReal();
				if (temp != null) {
					result[i - 1] = temp.doubleValue();
				} else {
					return null;
				}
			}
			return result;
		} catch (RuntimeException ex) {
			throw new WrongArgumentType(polynomial, "Polynomial expected!");
		}
	}

	public static IAST coefficientList(IExpr expr, IAST coefficientList) {
		try {
			ExprPolynomialRing ring = new ExprPolynomialRing(coefficientList);
			ExprPolynomial poly = ring.create(expr, true, false);
			if (poly.isZero()) {
				return F.List();
			}
			return poly.coefficientList();
		} catch (RuntimeException ex) {
			if (Config.SHOW_STACKTRACE) {
				ex.printStackTrace();
			}
			// throw new WrongArgumentType(ast, expr, 1, "Polynomial expected!");
		}
		return F.NIL;
	}

	public static IAST roots(final IExpr arg1, IAST variables, EvalEngine engine) {
		if (variables.size() != 2) {
			// factor only possible for univariate polynomials
			engine.printMessage("NRoots: factorization only possible for univariate polynomials");
			return F.NIL;
		}
		IExpr expr = evalExpandAll(arg1, engine);

		ISymbol sym = (ISymbol) variables.arg1();
		double[] coefficients = Expr2Object.toPolynomial(expr, sym);

		if (coefficients != null) {
			LaguerreSolver solver = new LaguerreSolver(Config.DEFAULT_ROOTS_CHOP_DELTA);
			org.hipparchus.complex.Complex[] roots = solver.solveAllComplex(coefficients, 0);
			return Object2Expr.convertComplex(true, roots);
		}
		IExpr denom = F.C1;
		if (expr.isAST()) {
			expr = Algebra.together((IAST) expr, engine);

			// split expr into numerator and denominator
			denom = engine.evaluate(F.Denominator(expr));
			if (!denom.isOne()) {
				// search roots for the numerator expression
				expr = engine.evaluate(F.Numerator(expr));
			}
		}
		return rootsOfVariable(expr, denom);
	}

	private static IAST rootsOfVariable(final IExpr expr, final IExpr denom) {

		IASTAppendable resultList = RootIntervals.croots(expr, true);
		if (resultList.isPresent()) {
			// IAST result = F.List();
			// if (resultList.size() > 0) {
			// result.appendArgs(resultList);
			// }
			// return result;
			return resultList;
		}
		return F.NIL;
	}

	protected static IAST roots(final IExpr arg1, boolean numericSolutions, IAST variables, EvalEngine engine) {

		IExpr expr = evalExpandAll(arg1, engine);

		IExpr denom = F.C1;
		if (expr.isAST()) {
			expr = Algebra.together((IAST) expr, engine);

			// split expr into numerator and denominator
			denom = F.Denominator.of(engine, expr);
			if (!denom.isOne()) {
				// search roots for the numerator expression
				expr = F.Numerator.of(engine, expr);
			}
		}
		return rootsOfVariable(expr, denom, variables, numericSolutions, engine);
	}

	/**
	 * <p>
	 * Given a set of polynomial coefficients, compute the roots of the polynomial. Depending on the polynomial being
	 * considered the roots may contain complex number. When complex numbers are present they will come in pairs of
	 * complex conjugates.
	 * </p>
	 * 
	 * @param coefficients
	 *            coefficients of the polynomial.
	 * @return the roots of the polynomial
	 */
	@Nonnull
	public static IAST findRoots(double... coefficients) {
		int N = coefficients.length - 1;

		// Construct the companion matrix
		RealMatrix c = new Array2DRowRealMatrix(N, N);

		double a = coefficients[N];
		for (int i = 0; i < N; i++) {
			c.setEntry(i, N - 1, -coefficients[i] / a);
		}
		for (int i = 1; i < N; i++) {
			c.setEntry(i, i - 1, 1);
		}

		try {

			EigenDecomposition ed = new EigenDecomposition(c);

			double[] realValues = ed.getRealEigenvalues();
			double[] imagValues = ed.getImagEigenvalues();

			IASTAppendable roots = F.ListAlloc(N);
			return roots.appendArgs(0, N,
					i -> F.chopExpr(F.complexNum(realValues[i], imagValues[i]), Config.DEFAULT_ROOTS_CHOP_DELTA));
			// for (int i = 0; i < N; i++) {
			// roots.append(F.chopExpr(F.complexNum(realValues[i], imagValues[i]),
			// Config.DEFAULT_ROOTS_CHOP_DELTA));
			// }
			// return roots;
		} catch (Exception ime) {
			throw new WrappedException(ime);
		}

	}

	public static IASTAppendable rootsOfExprPolynomial(final IExpr expr, IAST varList, boolean rootsOfQuartic) {
		IASTAppendable result = F.NIL;
		try {
			// try to generate a common expression polynomial
			ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
			ExprPolynomial ePoly = ring.create(expr, false, false);
			ePoly = ePoly.multiplyByMinimumNegativeExponents();
			if (ePoly.degree(0) >= Integer.MAX_VALUE) {
				return F.NIL;
			}
			if (ePoly.degree(0) >= 3) {
				result = unitPolynomial((int) ePoly.degree(0), ePoly);
				if (result.isPresent()) {
					result = QuarticSolver.createSet(result);
					return result;
				}
			}
			if (!rootsOfQuartic && ePoly.degree(0) > 2) {
				return F.NIL;
			}
			result = rootsOfQuarticPolynomial(ePoly);
			if (result.isPresent()) {
				if (expr.isNumericMode()) {
					for (int i = 1; i < result.size(); i++) {
						result.set(i, F.chopExpr(result.get(i), Config.DEFAULT_ROOTS_CHOP_DELTA));
					}
				}
				return result;
			}
		} catch (JASConversionException e2) {
			if (Config.SHOW_STACKTRACE) {
				e2.printStackTrace();
			}
		}
		return F.NIL;
	}

	/**
	 * Solve a polynomial with degree &lt;= 2.
	 * 
	 * @param expr
	 * @param varList
	 * @return <code>F.NIL</code> if no evaluation was possible.
	 */
	private static IAST rootsOfQuadraticExprPolynomial(final IExpr expr, IAST varList) {
		IASTAppendable result = F.NIL;
		try {
			// try to generate a common expression polynomial
			ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
			ExprPolynomial ePoly = ring.create(expr, false, false);
			ePoly = ePoly.multiplyByMinimumNegativeExponents();
			result = rootsOfQuadraticPolynomial(ePoly);
			if (result.isPresent() && expr.isNumericMode()) {
				for (int i = 1; i < result.size(); i++) {
					result.set(i, F.chopExpr(result.get(i), Config.DEFAULT_ROOTS_CHOP_DELTA));
				}
			}
		} catch (JASConversionException e2) {
			if (Config.SHOW_STACKTRACE) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Solve a polynomial with degree &lt;= 4.
	 * 
	 * @param polynomial
	 *            the polynomial
	 * @return <code>F.NIL</code> if no evaluation was possible.
	 */
	private static IASTAppendable rootsOfQuarticPolynomial(ExprPolynomial polynomial) {
		long varDegree = polynomial.degree(0);

		if (polynomial.isConstant()) {
			return F.ListAlloc(0);
		}

		IExpr a;
		IExpr b;
		IExpr c;
		IExpr d;
		IExpr e;
		if (varDegree <= 4) {
			// solve quartic equation:
			a = C0;
			b = C0;
			c = C0;
			d = C0;
			e = C0;
			for (ExprMonomial monomial : polynomial) {
				IExpr coeff = monomial.coefficient();
				long lExp = monomial.exponent().getVal(0);
				if (lExp == 4) {
					a = coeff;
				} else if (lExp == 3) {
					b = coeff;
				} else if (lExp == 2) {
					c = coeff;
				} else if (lExp == 1) {
					d = coeff;
				} else if (lExp == 0) {
					e = coeff;
				} else {
					return F.NIL;
				}
			}
			IASTAppendable result = QuarticSolver.quarticSolve(a, b, c, d, e);
			if (result.isPresent()) {
				return QuarticSolver.createSet(result);
			}
		}

		return F.NIL;
	}

	/**
	 * Solve polynomials of the form <code>a * x^varDegree + b == 0</code>
	 * 
	 * @param varDegree
	 * @param polynomial
	 * @return
	 */
	private static IASTAppendable unitPolynomial(int varDegree, ExprPolynomial polynomial) {
		IExpr a;
		IExpr b;
		a = C0;
		b = C0;
		for (ExprMonomial monomial : polynomial) {
			IExpr coeff = monomial.coefficient();
			long lExp = monomial.exponent().getVal(0);
			if (lExp == varDegree) {
				a = coeff;
			} else if (lExp == 0) {
				b = coeff;
			} else {
				return F.NIL;
			}
		}

		// a * x^varDegree + b
		if (!a.isOne()) {
			a = F.Power(a, F.fraction(-1, varDegree));
		}
		if (!b.isOne()) {
			b = F.Power(b, F.fraction(1, varDegree));
		}
		if ((varDegree & 0x0001) == 0x0001) {
			// odd
			IASTAppendable result = F.ListAlloc(varDegree);
			for (int i = 1; i <= varDegree; i++) {
				result.append(F.Times(F.Power(F.CN1, i - 1), F.Power(F.CN1, F.fraction(i, varDegree)), b, a));
			}
			return result;
		} else {
			// even
			IASTAppendable result = F.ListAlloc(varDegree);
			long size = varDegree / 2;
			int k = 1;
			for (int i = 1; i <= size; i++) {
				result.append(F.Times(F.CN1, F.Power(F.CN1, F.fraction(k, varDegree)), b, a));
				result.append(F.Times(F.Power(F.CN1, F.fraction(k, varDegree)), b, a));
				k += 2;
			}
			return result;
		}

	}

	/**
	 * Solve a polynomial with degree &lt;= 2.
	 * 
	 * @param polynomial
	 *            the polynomial
	 * @return <code>F.NIL</code> if no evaluation was possible.
	 */
	private static IASTAppendable rootsOfQuadraticPolynomial(ExprPolynomial polynomial) {
		long varDegree = polynomial.degree(0);

		if (polynomial.isConstant()) {
			return F.ListAlloc(1);
		}
		IExpr a;
		IExpr b;
		IExpr c;
		IExpr d;
		IExpr e;
		if (varDegree <= 2) {
			IEvalStepListener listener = EvalEngine.get().getStepListener();
			if (listener != null) {
				IASTAppendable temp = listener.rootsOfQuadraticPolynomial(polynomial);
				if (temp.isPresent()) {
					return temp;
				}
			}
			// solve quadratic equation:
			a = C0;
			b = C0;
			c = C0;
			d = C0;
			e = C0;
			for (ExprMonomial monomial : polynomial) {
				IExpr coeff = monomial.coefficient();
				long lExp = monomial.exponent().getVal(0);
				if (lExp == 4) {
					a = coeff;
				} else if (lExp == 3) {
					b = coeff;
				} else if (lExp == 2) {
					c = coeff;
				} else if (lExp == 1) {
					d = coeff;
				} else if (lExp == 0) {
					e = coeff;
				} else {
					throw new ArithmeticException("Roots::Unexpected exponent value: " + lExp);
				}
			}
			IASTAppendable result = QuarticSolver.quarticSolve(a, b, c, d, e);
			if (result.isPresent()) {
				result = QuarticSolver.createSet(result);
				return result;
			}

		}

		return F.NIL;
	}

	/**
	 * 
	 * @param expr
	 * @param denominator
	 * @param variables
	 * @param numericSolutions
	 * @param engine
	 * @return <code>F.NIL</code> if no evaluation was possible.
	 */
	public static IAST rootsOfVariable(final IExpr expr, final IExpr denominator, final IAST variables,
			boolean numericSolutions, EvalEngine engine) {
		IASTAppendable result = F.NIL;
		// ASTRange r = new ASTRange(variables, 1);
		// List<IExpr> varList = r;
		List<IExpr> varList = variables.copyTo();
		try {
			IExpr temp;
			IAST list = rootsOfQuadraticExprPolynomial(expr, variables);
			if (list.isPresent()) {
				return list;
			}
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numericSolutions);
			// if (polyRat.degree(0) <= 2) {
			result = rootsOfExprPolynomial(expr, variables, false);
			if (result.isPresent()) {
				return result;
			}
			// }
			result = F.ListAlloc(8);
			IAST factorRational = Algebra.factorRational(polyRat, jas, varList, F.List);
			for (int i = 1; i < factorRational.size(); i++) {
				temp = F.evalExpand(factorRational.get(i));
				IAST quarticResultList = QuarticSolver.solve(temp, variables.arg1());
				if (quarticResultList.isPresent()) {
					for (int j = 1; j < quarticResultList.size(); j++) {
						if (numericSolutions) {
							result.append(F.chopExpr(engine.evalN(quarticResultList.get(j)),
									Config.DEFAULT_ROOTS_CHOP_DELTA));
						} else {
							result.append(quarticResultList.get(j));
						}
					}
				} else {
					polyRat = jas.expr2JAS(temp, numericSolutions);
					IAST factorComplex = Algebra.factorComplex(polyRat, jas, varList, F.List, true);
					for (int k = 1; k < factorComplex.size(); k++) {
						temp = F.evalExpand(factorComplex.get(k));
						quarticResultList = QuarticSolver.solve(temp, variables.arg1());
						if (quarticResultList.isPresent()) {
							for (int j = 1; j < quarticResultList.size(); j++) {
								if (numericSolutions) {
									result.append(F.chopExpr(engine.evalN(quarticResultList.get(j)),
											Config.DEFAULT_ROOTS_CHOP_DELTA));
								} else {
									result.append(quarticResultList.get(j));
								}
							}
						} else {
							double[] coefficients = PolynomialFunctions.coefficients(temp, (ISymbol) variables.arg1());
							if (coefficients == null) {
								return F.NIL;
							}
							IAST resultList = findRoots(coefficients);
							// IAST resultList = RootIntervals.croots(temp,
							// true);
							if (resultList.size() > 0) {
								result.appendArgs(resultList);
							}
						}
					}
				}
			}
			result = QuarticSolver.createSet(result);
			return result;
		} catch (RuntimeException rex) {
			// JAS may throw RuntimeExceptions
			result = rootsOfExprPolynomial(expr, variables, true);
		}
		if (result.isPresent()) {
			if (!denominator.isNumber()) {
				// eliminate roots from the result list, which occur in the
				// denominator
				int i = 1;
				while (i < result.size()) {
					IExpr temp = denominator.replaceAll(F.Rule(variables.arg1(), result.get(i)));
					if (temp.isPresent() && engine.evaluate(temp).isZero()) {
						result.remove(i);
						continue;
					}
					i++;
				}
			}
			return result;
		}
		return F.NIL;
	}

	private final static PolynomialFunctions CONST = new PolynomialFunctions();

	public static PolynomialFunctions initialize() {
		return CONST;
	}

	private PolynomialFunctions() {

	}

}
