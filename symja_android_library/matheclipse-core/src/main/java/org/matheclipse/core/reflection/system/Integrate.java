package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryEval;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules0;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules1;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules10;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules11;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules12;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules13;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules14;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules15;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules16;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules2;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules3;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules4;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules5;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules6;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules7;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules8;
import org.matheclipse.core.integrate.rubi.IndefiniteIntegrationRules9;
import org.matheclipse.core.integrate.rubi.UtilityFunctions;
import org.matheclipse.core.integrate.rubi.UtilityFunctions0;
import org.matheclipse.core.integrate.rubi.UtilityFunctions1;
import org.matheclipse.core.integrate.rubi.UtilityFunctions2;
import org.matheclipse.core.integrate.rubi.UtilityFunctions3;
import org.matheclipse.core.integrate.rubi.UtilityFunctions4;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;

/**
 * Integration of a function. See <a
 * href="http://en.wikipedia.org/wiki/Integral">Integral</a>
 */
public class Integrate extends AbstractFunctionEvaluator {
	/**
	 * Constructor for the singleton
	 */
	public final static Integrate CONST = new Integrate();

	public Integrate() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() < 3) {
			return null;
		}
		IExpr fx = ast.get(1);
		if (ast.size() > 3) {
			// reduce arguments by folding Integrate[fxy, x, y] to Integrate[
			// Integrate[fxy, y], x] ...
			return ast.range(2).foldRight(new BinaryEval(F.Integrate), fx);
		}

		if (ast.get(1).isAST()) {
			fx = F.evalExpandAll(ast.get(1));
			if (fx.isPlus()) {
				// Integrate[a_+b_+...,x_] -> Integrate[a,x]+Integrate[b,x]+...
				return ((IAST) fx).map(Functors.replace1st(F.Integrate(F.Null, ast.get(2))));
			}
		}

		if (ast.get(2).isList()) {
			IAST xList = (IAST) ast.get(2);
			if (xList.isVector() == 3) {
				// Integrate[f[x], {x,a,b}]
				IAST clone = ast.clone();
				clone.set(2, xList.get(1));
				IExpr temp = F.eval(clone);
				if (temp.isFree(F.Integrate, true)) {
					// F(b)-F(a)
					IExpr Fb = F.eval(F.subst(temp, F.Rule(xList.get(1), xList.get(3))));
					IExpr Fa = F.eval(F.subst(temp, F.Rule(xList.get(1), xList.get(2))));
					if (!Fb.isFree(F.DirectedInfinity, true) || !Fb.isFree(F.Indeterminate, true)) {
						PrintStream stream = EvalEngine.get().getOutPrintStream();
						if (stream == null) {
							stream = System.out;
						}
						stream.println("Not integrable: " + temp + " for " + xList.get(1) + " = " + xList.get(3));
						return null;
					}
					if (!Fa.isFree(F.DirectedInfinity, true) || !Fa.isFree(F.Indeterminate, true)) {
						PrintStream stream = EvalEngine.get().getOutPrintStream();
						if (stream == null) {
							stream = System.out;
						}
						stream.println("Not integrable: " + temp + " for " + xList.get(1) + " = " + xList.get(2));
						return null;
					}
					return F.Subtract(Fb, Fa);
				}
			}
		}

		if (ast.get(2).isSymbol()) {
			final ISymbol symbol = (ISymbol) ast.get(2);
			if (fx.isNumber()) {
				// Integrate[x_NumberQ,y_Symbol] -> x*y
				return Times(fx, symbol);
			}
			if (fx.isFree(symbol, false)) {
				// Integrate[x_,y_Symbol] -> x*y /; FreeQ[x,y]
				return Times(fx, symbol);
			}
			if (fx.equals(symbol)) {
				// Integrate[x_,x_Symbol] -> x^2 / 2
				return Times(F.C1D2, Power(fx, F.C2));
			}
			if (fx.isAST()) {
				final IAST arg1 = (IAST) fx;

				if (arg1.isPower() && symbol.equals(arg1.get(1)) && arg1.get(2).isInteger()) {
					IInteger i = (IInteger) arg1.get(2);
					if (i.isGreaterThan(F.C1)) {
						// Integrate[x_ ^ i_IntegerQ, x_Symbol] -> 1/(i+1) * x ^
						// (i+1)
						i = i.add(F.C1);
						return F.Times(F.Power(i, F.CN1), F.Power(symbol, i));
					}
				}

				if (arg1.isTimes()) {
					// Integrate[a_*y_,x_Symbol] -> a*Integrate[y,x] /;
					// FreeQ[a,x]
					IAST filterCollector = F.Times();
					IAST restCollector = F.Times();
					arg1.filter(filterCollector, restCollector, new Predicate<IExpr>() {
						@Override
						public boolean apply(IExpr input) {
							return input.isFree(symbol, true);
						}
					});
					if (filterCollector.size() > 1) {
						if (restCollector.size() > 1) {
							if (restCollector.size() == 2) {
								filterCollector.add(F.Integrate(restCollector.get(1), symbol));
							} else {
								filterCollector.add(F.Integrate(restCollector, symbol));
							}
						}
						return filterCollector;
					}
				}

				if (!ast.get(1).equals(fx)) {
					IAST clon = ast.clone();
					clon.set(1, fx);
					return clon;
				}

				final IExpr header = arg1.head();
				if (arg1.size() >= 3) {
					if (header == F.Times || header == F.Power) {
						if (!arg1.isEvalFlagOn(IAST.IS_DECOMPOSED_PARTIAL_FRACTION) && ast.get(2).isSymbol()) {
							IExpr[] parts = Apart.getFractionalParts(arg1);
							if (parts != null) {
								IAST apartPlus = integrateByPartialFractions(parts, symbol);
								if (apartPlus != null && apartPlus.size() > 1) {
									if (apartPlus.size() == 2) {
										if (ast.equals(apartPlus.get(1))) {
											return null;
										}
										return apartPlus.get(1);
									}
									if (ast.equals(apartPlus)) {
										return null;
									}
									return apartPlus;
								}
								// if (arg1.isTimes()) {
								// IExpr result =
								// integratePolynomialByParts(arg1, symbol);
								// if (result != null) {
								// return result;
								// }
								// }
							}
						}
					}
				}

				// EvalEngine engine= EvalEngine.get();
				// engine.setIterationLimit(8);
				return F.Integrate.evalDownRule(EvalEngine.get(), ast);
			}
		}

		return null;
	}

	/**
	 * Check if the polynomial has maximum degree 2 in 1 variable and return the
	 * coefficients.
	 * 
	 * @param poly
	 * @return <code>null</code> if the polynomials degree > 2 and number of
	 *         variables <> 1
	 */
	public static boolean isQuadratic(GenPolynomial<BigRational> poly, BigRational[] result) {
		if (poly.degree() <= 2 && poly.numberOfVariables() == 1) {
			result[0] = BigRational.ZERO;
			result[1] = BigRational.ZERO;
			result[2] = BigRational.ZERO;
			for (Monomial<BigRational> monomial : poly) {
				BigRational coeff = monomial.coefficient();
				ExpVector exp = monomial.exponent();
				for (int i = 0; i < exp.length(); i++) {
					result[(int) exp.getVal(i)] = coeff;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns an AST with head <code>Plus</code>, which contains the partial
	 * fraction decomposition of the numerator and denominator parts.
	 * 
	 * @param parts
	 * @param variableList
	 * @return <code>null</code> if the partial fraction decomposition wasn't
	 *         constructed
	 */
	private static IAST integrateByPartialFractions(IExpr[] parts, ISymbol x) {
		try {
			IAST variableList = F.List(x);
			IExpr exprNumerator = F.evalExpandAll(parts[0]);
			IExpr exprDenominator = F.evalExpandAll(parts[1]);
			ASTRange r = new ASTRange(variableList, 1);
			List<IExpr> varList = r.toList();

			String[] varListStr = new String[1];
			varListStr[0] = variableList.get(1).toString();
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> numerator = jas.expr2JAS(exprNumerator);
			GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator);
			
			// get factors
			FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ZERO);
			SortedMap<GenPolynomial<BigRational>, Long> sfactors = factorAbstract.baseFactors(denominator);

			List<GenPolynomial<BigRational>> D = new ArrayList<GenPolynomial<BigRational>>(sfactors.keySet());

			SquarefreeAbstract<BigRational> sqf = SquarefreeFactory.getImplementation(BigRational.ZERO);
			List<List<GenPolynomial<BigRational>>> Ai = sqf.basePartialFraction(numerator, sfactors);
			// returns [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) =
			// A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).

			if (Ai.size() > 0) {
				BigRational[] numer = new BigRational[3];
				BigRational[] denom = new BigRational[3];
				IAST result = F.Plus();
				IExpr temp;
				if (!Ai.get(0).get(0).isZERO()) {
					temp = F.eval(jas.poly2Expr(Ai.get(0).get(0)));
					if (temp.isAST()) {
						((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
					}
					result.add(F.Integrate(temp, x));
				}
				for (int i = 1; i < Ai.size(); i++) {
					List<GenPolynomial<BigRational>> list = Ai.get(i);
					long j = 0L;
					for (GenPolynomial<BigRational> genPolynomial : list) {
						if (!genPolynomial.isZERO()) {
							boolean isDegreeLE2 = D.get(i - 1).degree() <= 2;
							if (isDegreeLE2 && j == 1L) {
								if (genPolynomial.isONE()) {
									isQuadratic(D.get(i - 1), denom);
									IFraction a = F.fraction(denom[2].numerator(), denom[2].denominator());
									IFraction b = F.fraction(denom[1].numerator(), denom[1].denominator());
									IFraction c = F.fraction(denom[0].numerator(), denom[0].denominator());
									if (a.isZero()) {
										// JavaForm[Log[b*x+c]/b]
										result.add(Times(Log(Plus(c, Times(b, x))), Power(b, CN1)));
									} else {
										// compute b^2-4*a*c from
										// (a*x^2+b*x+c)
										BigRational cmp = denom[1].multiply(denom[1]).subtract(
												BigRational.valueOf(4L).multiply(denom[2]).multiply(denom[0]));
										int cmpTo = cmp.compareTo(BigRational.ZERO);
										// (2*a*x+b)
										IExpr ax2Plusb = F.Plus(F.Times(F.C2, a, x), b);
										if (cmpTo == 0) {
											// (-2) / (2*a*x+b)
											result.add(F.Times(F.integer(-2L), F.Power(ax2Plusb, F.CN1)));
										} else if (cmpTo > 0) {
											// (b^2-4ac)^(1/2)
											temp = F.eval(F.Power(F.Subtract(F.Sqr(b), F.Times(F.C4, a, c)), F.C1D2));
											result.add(F.Times(F.Power(temp, F.CN1), F.Log(F.Times(F.Subtract(ax2Plusb, temp),
													Power(F.Plus(ax2Plusb, temp), F.CN1)))));
										} else {
											// (4ac-b^2)^(1/2)
											temp = F.eval(F.Power(F.Subtract(F.Times(F.C4, a, c), F.Sqr(b)), F.CN1D2));
											result.add(F.Times(F.C2, temp, F.ArcTan(Times(ax2Plusb, temp))));
										}
									}
								} else {
									isQuadratic(genPolynomial, numer);
									IFraction A = F.fraction(numer[1].numerator(), numer[1].denominator());
									IFraction B = F.fraction(numer[0].numerator(), numer[0].denominator());
									isQuadratic(D.get(i - 1), denom);
									IFraction p = F.fraction(denom[1].numerator(), denom[1].denominator());
									IFraction q = F.fraction(denom[0].numerator(), denom[0].denominator());
									if (A.isZero()) {
										// JavaForm[B*Log[p*x+q]/p]
										temp = Times(B, Log(Plus(q, Times(p, x))), Power(p, CN1));
									} else {
										// JavaForm[A/2*Log[x^2+p*x+q]+(2*B-A*p)/(4*q-p^2)^(1/2)*ArcTan[(2*x+p)/(4*q-p^2)^(1/2)]]
										temp = Plus(
												Times(C1D2, A, Log(Plus(q, Times(p, x), Power(x, C2)))),
												Times(ArcTan(Times(Plus(p, Times(C2, x)),
														Power(Plus(Times(CN1, Power(p, C2)), Times(C4, q)), CN1D2))),
														Plus(Times(C2, B), Times(CN1, A, p)),
														Power(Plus(Times(CN1, Power(p, C2)), Times(C4, q)), CN1D2)));
									}
									result.add(F.eval(temp));
								}
							} else if (isDegreeLE2 && j > 1L) {
								isQuadratic(genPolynomial, numer);
								IFraction A = F.fraction(numer[1].numerator(), numer[1].denominator());
								IFraction B = F.fraction(numer[0].numerator(), numer[0].denominator());
								isQuadratic(D.get(i - 1), denom);
								IFraction a = F.fraction(denom[2].numerator(), denom[2].denominator());
								IFraction b = F.fraction(denom[1].numerator(), denom[1].denominator());
								IFraction c = F.fraction(denom[0].numerator(), denom[0].denominator());
								IInteger k = F.integer(j);
								if (A.isZero()) {
									// JavaForm[B*((2*a*x+b)/((k-1)*(4*a*c-b^2)*(a*x^2+b*x+c)^(k-1))+
									// (4*k*a-6*a)/((k-1)*(4*a*c-b^2))*Integrate[(a*x^2+b*x+c)^(-k+1),x])]
									temp = Times(
											B,
											Plus(Times(
													Integrate(
															Power(Plus(c, Times(b, x), Times(a, Power(x, C2))),
																	Plus(C1, Times(CN1, k))), x),
													Plus(Times(F.integer(-6L), a), Times(C4, a, k)), Power(Plus(CN1, k), CN1),
													Power(Plus(Times(CN1, Power(b, C2)), Times(C4, a, c)), CN1)),
													Times(Plus(b, Times(C2, a, x)),
															Power(Plus(CN1, k), CN1),
															Power(Plus(Times(CN1, Power(b, C2)), Times(C4, a, c)), CN1),
															Power(Plus(c, Times(b, x), Times(a, Power(x, C2))),
																	Times(CN1, Plus(CN1, k))))));
								} else {
									// JavaForm[(-A)/(2*a*(k-1)*(a*x^2+b*x+c)^(k-1))+(B-A*b/(2*a))*Integrate[(a*x^2+b*x+c)^(-k),x]]
									temp = Plus(
											Times(Integrate(Power(Plus(c, Times(b, x), Times(a, Power(x, C2))), Times(CN1, k)), x),
													Plus(B, Times(CN1D2, A, Power(a, CN1), b))),
											Times(CN1D2, A, Power(a, CN1), Power(Plus(CN1, k), CN1),
													Power(Plus(c, Times(b, x), Times(a, Power(x, C2))), Times(CN1, Plus(CN1, k)))));
								}
								result.add(F.eval(temp));
							} else {
								temp = F.eval(F.Times(jas.poly2Expr(genPolynomial),
										F.Power(jas.poly2Expr(D.get(i - 1)), F.integer(j * (-1L)))));
								if (!temp.equals(F.C0)) {
									if (temp.isAST()) {
										((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
									}
									result.add(F.Integrate(temp, x));
								}
							}
						}
						j++;
					}

				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (JASConversionException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * See <a
	 * href="http://en.wikipedia.org/wiki/Integration_by_parts">Wikipedia-
	 * Integration by parts</a>
	 * 
	 * @param f
	 * @param g
	 * @param symbol
	 * @return
	 */
	private static IExpr integratePolynomialByParts(final IAST arg1, ISymbol symbol) {
		IAST fTimes = F.Times();
		IAST gTimes = F.Times();
		collectPolynomialTerms(arg1, symbol, fTimes, gTimes);
		IExpr f = fTimes;
		IExpr g = gTimes;
		if (fTimes.size() == 1) {
			return null;
		} else if (fTimes.size() == 2) {
			// OneIdentity
			f = fTimes.get(1);
		}
		if (gTimes.size() == 1) {
			return null;
		} else if (gTimes.size() == 2) {
			// OneIdentity
			g = gTimes.get(1);
		}
		return integrateByParts(f, g, symbol);
	}

	/**
	 * See <a
	 * href="http://en.wikipedia.org/wiki/Integration_by_parts">Wikipedia-
	 * Integration by parts</a>
	 * 
	 * @param f
	 * @param g
	 * @param symbol
	 * @return
	 */
	private static IExpr integrateByParts(IExpr f, IExpr g, ISymbol symbol) {
		EvalEngine engine = EvalEngine.get();
		int limit = engine.getRecursionLimit();
		try {
			if (limit <= 0) {
				// set recursion limit
				engine.setRecursionLimit(128);
			}
			IExpr fIntegrated = F.eval(F.Integrate(f, symbol));
			if (!fIntegrated.isFree(Integrate, true)) {
				return null;
			}
			IExpr gDerived = F.eval(F.D(g, symbol));
			return F.eval(F.Plus(F.Times(fIntegrated, g), F.Times(F.CN1, F.Integrate(F.Times(fIntegrated, gDerived), symbol))));
		} catch (RecursionLimitExceeded rle) {
			engine.setRecursionLimit(limit);
		} finally {
			engine.setRecursionLimit(limit);
		}
		return null;
	}

	/**
	 * Collect all found polynomial terms into <code>fTimes</code> and the rest
	 * into <code>gTimes</code>.
	 * 
	 * @param timesAST
	 *            an AST representing a <code>Times[...]</code> expression.
	 * @param symbol
	 * @param fTimes
	 * @param gTimes
	 */
	private static void collectPolynomialTerms(final IAST timesAST, ISymbol symbol, IAST fTimes, IAST gTimes) {
		IExpr temp;
		for (int i = 1; i < timesAST.size(); i++) {
			temp = timesAST.get(i);
			if (temp.isFree(symbol, true)) {
				fTimes.add(temp);
				continue;
			} else if (temp.equals(symbol)) {
				fTimes.add(temp);
				continue;
			} else if (PolynomialQ.polynomialQ(temp, List(symbol))) {
				fTimes.add(temp);
				continue;
			}
			gTimes.add(temp);
		}
	}

	@Override
	/**
	 * Get the rules defined for Integrate function. These rules are loaded, if the Integrate function is used the first time.
	 * 
	 * @see AbstractFunctionEvaluator#setUp(ISymbol)()
	 */
	public IAST getRuleAST() {
		// TODO Integrate[] is currently not working properly in all cases!
		// long start = System.currentTimeMillis();

		IAST ast = F.ast(F.List, 10000, false);
		ast.addAll(IndefiniteIntegrationRules0.RULES);
		ast.addAll(IndefiniteIntegrationRules1.RULES);
		ast.addAll(IndefiniteIntegrationRules2.RULES);
		ast.addAll(IndefiniteIntegrationRules3.RULES);
		ast.addAll(IndefiniteIntegrationRules4.RULES);
		ast.addAll(IndefiniteIntegrationRules5.RULES);
		ast.addAll(IndefiniteIntegrationRules6.RULES);
		ast.addAll(IndefiniteIntegrationRules7.RULES);
		ast.addAll(IndefiniteIntegrationRules8.RULES);
		ast.addAll(IndefiniteIntegrationRules9.RULES);
		ast.addAll(IndefiniteIntegrationRules10.RULES);
		ast.addAll(IndefiniteIntegrationRules11.RULES);
		ast.addAll(IndefiniteIntegrationRules12.RULES);
		ast.addAll(IndefiniteIntegrationRules13.RULES);
		ast.addAll(IndefiniteIntegrationRules14.RULES);
		ast.addAll(IndefiniteIntegrationRules15.RULES);
		ast.addAll(IndefiniteIntegrationRules16.RULES);
		UtilityFunctions.init();
		// if (Config.SHOW_STACKTRACE) {
		// long end = System.currentTimeMillis();
		// System.out.println(end - start);
		// }
		return ast;

	}

	/**
	 * Get the rules defined for Integrate utility functions. These rules are
	 * loaded on system startup.
	 * 
	 * @see AbstractFunctionEvaluator#setUp(ISymbol)()
	 */
	public static IAST getUtilityFunctionsRuleAST() {
		IAST ast = F.ast(F.List, 10000, false);
		ast.addAll(UtilityFunctions0.RULES);
		ast.addAll(UtilityFunctions1.RULES);
		ast.addAll(UtilityFunctions2.RULES);
		ast.addAll(UtilityFunctions3.RULES);
		ast.addAll(UtilityFunctions4.RULES);
		return ast;

	}

}