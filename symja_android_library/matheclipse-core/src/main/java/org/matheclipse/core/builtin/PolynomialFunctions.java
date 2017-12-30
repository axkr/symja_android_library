package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.integer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEvalEngine;
import org.matheclipse.core.polynomials.ExpVectorLong;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.ExprTermOrder;
import org.matheclipse.core.reflection.system.MonomialList;

import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

public class PolynomialFunctions {
	static {
		F.Coefficient.setEvaluator(new Coefficient());
		F.CoefficientList.setEvaluator(new CoefficientList());
		F.CoefficientRules.setEvaluator(new CoefficientRules());
		F.Discriminant.setEvaluator(new Discriminant());
		F.Exponent.setEvaluator(new Exponent());
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
				exponents = new long[listOfVariables.size() - 1];
				for (int i = 0; i < exponents.length; i++) {
					exponents[i] = 0L;
				}
				for (int i = 1; i < arg2AST.size(); i++) {
					long value = 1L;
					IExpr a1 = arg2AST.get(i);
					if (a1.isPower() && a1.getAt(2).isInteger()) {
						a1 = arg2AST.get(i).getAt(1);
						IInteger ii = (IInteger) arg2AST.get(i).getAt(2);
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
				listOfVariables = F.List();
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
				IExpr expr = F.evalExpandAll(ast.arg1(), engine);
				ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, listOfVariables,
						listOfVariables.size() - 1);
				ExprPolynomial poly = ring.create(expr, true, true);
				return poly.coefficient(expArr);
			} catch (RuntimeException ae) {
				if (Config.DEBUG) {
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
			IExpr expr = F.evalExpandAll(ast.arg1(), engine);
			ISymbol arg2 = Validate.checkSymbolType(ast, 2);
			try {
				ExprPolynomialRing ring = new ExprPolynomialRing(F.List(arg2));
				ExprPolynomial poly = ring.create(expr);
				if (poly.isZero()) {
					return F.List();
				}
				return poly.coefficientList();
			} catch (RuntimeException ex) {
				throw new WrongArgumentType(ast, expr, 1, "Polynomial expected!");
			}
		}

		public static long univariateCoefficientList(IExpr polynomial, final ISymbol variable, List<IExpr> resultList)
				throws JASConversionException {
			try {
				ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
				ExprPolynomial poly = ring.create(polynomial);
				// PolynomialOld poly = new PolynomialOld(polynomial, (ISymbol) variable);
				// if (!poly.isPolynomial()) {
				// throw new WrongArgumentType(polynomial, "Polynomial expected!");
				// }
				IAST list = poly.coefficientList();
				int degree = list.size() - 2;
				if (degree >= Short.MAX_VALUE) {
					return degree;
				}
				for (int i = 0; i <= degree; i++) {
					IExpr temp = list.get(i + 1);
					resultList.add(temp);
				}
				return degree;
			} catch (RuntimeException ex) {
				throw new WrongArgumentType(polynomial, "Polynomial expected!");
			}
		}

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
		public static long univariateCoefficientList(IExpr polynomial, ISymbol variable, List<IExpr> resultList,
				List<IExpr> resultListDiff) throws JASConversionException {
			try {
				ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
				ExprPolynomial poly = ring.create(polynomial);
				// PolynomialOld poly = new PolynomialOld(polynomial, (ISymbol) variable);
				// if (!poly.isPolynomial()) {
				// throw new WrongArgumentType(polynomial, "Polynomial expected!");
				// }
				IAST polyExpr = poly.coefficientList();

				int degree = polyExpr.size() - 2;
				if (degree >= Short.MAX_VALUE) {
					return degree;
				}
				for (int i = 0; i <= degree; i++) {
					IExpr temp = polyExpr.get(i + 1);
					resultList.add(temp);
				}
				IAST polyDiff = poly.derivative().coefficientList();
				int degreeDiff = polyDiff.size() - 2;
				for (int i = 0; i <= degreeDiff; i++) {
					IExpr temp = polyDiff.get(i + 1);
					resultListDiff.add(temp);
				}
				return degree;
			} catch (RuntimeException ex) {
				throw new WrongArgumentType(polynomial, "Polynomial expected!");
			}
		}
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
				varList = new ArrayList<IExpr>(symbolList.size() - 1);
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
					if (option.isSignedNumber()) {
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
		 * @param variable
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
		 * @param variable
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

			IExpr expr = F.evalExpandAll(ast.arg1(), engine);
			if (expr.isZero()) {
				collector.add(F.CNInfinity);
			} else if (expr.isAST()) {
				IAST arg1 = (IAST) expr;
				final IPatternMatcher matcher = new PatternMatcherEvalEngine(form, engine);

				if (arg1.isPower()) {
					if (matcher.test(arg1.arg1(), engine)) {
						collector.add(arg1.arg2());
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
							if (matcher.test(pow.arg1(), engine)) {
								collector.add(pow.arg2());
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
				final PatternMatcher matcher = new PatternMatcherEvalEngine(form, engine);
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
			return result;
		}

		private void timesExponent(IAST timesAST, final IPatternMatcher matcher, Set<IExpr> collector,
				EvalEngine engine) {
			boolean evaled = false;
			IExpr argi;
			for (int i = 1; i < timesAST.size(); i++) {
				argi = timesAST.get(i);
				if (argi.isPower()) {
					IAST pow = (IAST) argi;
					if (matcher.test(pow.arg1(), engine)) {
						collector.add(pow.arg2());
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
	 * Get the coefficient list of a univariate polynomial.
	 * 
	 * @param polynomial
	 * @param variable
	 * @return <code>null</code> if the list couldn't be evaluated.
	 */
	public static double[] coefficientList(IExpr polynomial, final ISymbol variable) throws JASConversionException {
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
				ISignedNumber temp = list.get(i).evalSignedNumber();
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

	private final static PolynomialFunctions CONST = new PolynomialFunctions();

	public static PolynomialFunctions initialize() {
		return CONST;
	}

	private PolynomialFunctions() {

	}

}
