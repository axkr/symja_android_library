package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN1D3;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.integer;

import java.util.List;
import java.util.SortedMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

/**
 * Determine the roots of a univariate polynomial
 * 
 * See Wikipedia entries for: <a
 * href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation
 * </a>, <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic
 * function</a> and <a
 * href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
 */
public class Roots extends AbstractFunctionEvaluator {

	public Roots() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2) {
			return null;
		}
		return roots(ast, false);
	}

	protected static IAST roots(final IAST ast, boolean numericSolutions) {
		ExprVariables eVar = new ExprVariables(ast.get(1));
		if (!eVar.isSize(1)) {
			// factor only possible for univariate polynomials
			return null;
		}
		IExpr expr = evalExpandAll(ast.get(1));
		IAST variables = eVar.getVarList();
		IExpr denom = F.C1;
		if (expr.isAST()) {
			expr = Together.together((IAST) expr);

			// split expr into numerator and denominator
			denom = F.eval(F.Denominator(expr));
			if (!denom.isOne()) {
				// search roots for the numerator expression
				expr = F.eval(F.Numerator(expr));
			}
		}
		return rootsOfVariable(expr, denom, variables, numericSolutions);
	}

	protected static IAST rootsOfVariable(final IExpr expr, final IExpr denom, final IAST variables, boolean numericSolutions) {

		if (numericSolutions) {
			IAST result = List();
			IAST resultList = RootIntervals.croots(expr, true);
			if (resultList == null) {
				return null;
			}
			if (resultList.size() > 0) {
				result.addAll(resultList);
			}
			return result;
		}

		IAST result = null;
		ASTRange r = new ASTRange(variables, 1);
		List<IExpr> varList = r.toList();
		try {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> rPoly = jas.numericExpr2JAS(expr);

			result = rootsOfPolynomial(rPoly, jas, numericSolutions);

		} catch (JASConversionException e) {
			try {
				JASConvert<IExpr> eJas = new JASConvert<IExpr>(varList, new ExprRingFactory());
				GenPolynomial<IExpr> ePoly = eJas.expr2IExprJAS(expr);
				result = rootsOfPolynomial(ePoly);
			} catch (JASConversionException e2) {
				if (Config.SHOW_STACKTRACE) {
					e2.printStackTrace();
				}
			}
		}
		if (result != null) {
			if (!denom.isNumber()) {
				// eliminate roots from the result list, which occur in the denominator
				int i = 1;
				while (i < result.size()) {
					if (F.eval(denom.replaceAll(F.Rule(variables.get(1), result.get(i)))).isZero()) {
						result.remove(i);
						continue;
					}
					i++;
				}
			}
			return result;
		}
		return null;
	}

	private static IAST rootsOfPolynomial(GenPolynomial<IExpr> ePoly) {
		long varDegree = ePoly.degree(0);
		IAST result = List();
		if (ePoly.isConstant()) {
			return result;
		}
		IExpr a;
		IExpr b;
		IExpr c;
		if (varDegree <= 2) {
			// solve Quadratic equation: a*x^2 + b*x + c = 0
			a = C0;
			b = C0;
			c = C0;
			for (Monomial<IExpr> monomial : ePoly) {
				IExpr coeff = monomial.coefficient();
				long lExp = monomial.exponent().getVal(0);
				if (lExp == 2) {
					a = coeff;
				} else if (lExp == 1) {
					b = coeff;
				} else if (lExp == 0) {
					c = coeff;
				} else {
					throw new ArithmeticException("Roots::Unexpected exponent value: " + lExp);
				}
			}
			if (org.matheclipse.core.reflection.system.PossibleZeroQ.possibleZeroQ(a)) {
				result.add(F.Divide(c.negate(), b));
			} else {
				IAST sqrt = Sqrt(Plus(Sqr(b), Times(integer(-4), a, c)));
				IExpr rev2a = F.Divide(C1, a.multiply(C2));
				result.add(Times(rev2a, Plus(b.negate(), sqrt)));
				result.add(Times(rev2a, Plus(b.negate(), sqrt.negative())));
			}
			return result;
		}
		// else if (varDegree <= 3) {
		// // ePoly = ePoly.monic();
		// // solve Cubic equation: x^3 + a*x^2 + b*x + c = 0
		// a = C0;
		// b = C0;
		// c = C0;
		// IExpr leadingCoeff = ePoly.leadingBaseCoefficient();
		// for (Monomial<IExpr> monomial : ePoly) {
		// IExpr coeff = F.eval(monomial.coefficient());
		// long lExp = monomial.exponent().getVal(0);
		// if (lExp == 2) {
		// a = coeff;
		// if (!leadingCoeff.isOne()) {
		// a = F.eval(F.Divide(a, leadingCoeff));
		// }
		// } else if (lExp == 1) {
		// b = coeff;
		// if (!leadingCoeff.isOne()) {
		// b = F.eval(F.Divide(b, leadingCoeff));
		// }
		// } else if (lExp == 0) {
		// c = coeff;
		// if (!leadingCoeff.isOne()) {
		// c = F.eval(F.Divide(c, leadingCoeff));
		// }
		// } else if (lExp == 3) {
		// // if (!coeff.isOne()) {
		// // }
		// } else {
		// throw new ArithmeticException("Roots::Unexpected exponent value: " +
		// lExp);
		// }
		// }
		// // m = JavaForm[2*a^3 - 9*a*b + 27* c]
		// IExpr m = Plus(Times(C2, Power(a, C3)), Times(integer(-9L), a, b),
		// Times(integer(27L), c));
		// // k = JavaForm[a^2 - 3*b]
		// IExpr k = Plus(Power(a, C2), Times(integer(-3L), b));
		// // n = JavaForm[m^2 - 4*k^3]
		// IExpr n = Plus(Times(integer(-4L), Power(k, C3)), Power(m, C2));
		//
		// // omega1 = -(1/2) + 1/2 * Sqrt[3] * I
		// IExpr omega1 = Plus(CN1D2, Times(C1D2, Sqrt(C3), CI));
		// // omega2 = -(1/2) - 1/2 * Sqrt[3] * I
		// IExpr omega2 = Plus(CN1D2, Times(CN1D2, Sqrt(C3), CI));
		//
		// // t1 = (1/2 * (m + n^(1/2))) ^ (1/3)
		// IExpr temp1 = F.eval(Times(C1D2, Plus(m, Sqrt(n))));
		// IExpr t1;
		// if (temp1.isSignedNumber() && ((ISignedNumber) temp1).isNegative()) {
		// t1 = Times(CN1, Power(F.Negate(temp1), C1D3));
		// } else {
		// t1 = Power(temp1, C1D3);
		// }
		// // t2 = (1/2 * (m - n^(1/2))) ^ (1/3)
		// IExpr temp2 = F.eval(Times(C1D2, Subtract(m, Sqrt(n))));
		// IExpr t2;
		// if (temp2.isSignedNumber() && ((ISignedNumber) temp2).isNegative()) {
		// t2 = Times(CN1, Power(F.Negate(temp2), C1D3));
		// } else {
		// t2 = Power(temp2, C1D3);
		// }
		// result.add(Times(CN1D3, Plus(a, t1, t2)));
		// if (temp1.equals(temp2)) {
		// result.add(Times(CN1D3, Plus(a, Times(Plus(omega1, omega2), t1))));
		// } else {
		// result.add(Times(CN1D3, Plus(a, Times(omega2, t1), Times(omega1, t2))));
		// result.add(Times(CN1D3, Plus(a, Times(omega1, t1), Times(omega2, t2))));
		// }
		// return F.eval(result);
		// }
		return null;
	}

	private static IAST rootsOfPolynomial(GenPolynomial<BigRational> poly, JASConvert<BigRational> jas, boolean numericSolutions) {
		FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
		SortedMap<GenPolynomial<BigRational>, Long> map = factorAbstract.baseFactors(poly);
		IAST result = List();// function(Or);
		IInteger a;
		IInteger b;
		IInteger c;
		for (SortedMap.Entry<GenPolynomial<BigRational>, Long> entry : map.entrySet()) {
			GenPolynomial<BigRational> key = entry.getKey();
			GenPolynomial<edu.jas.arith.BigInteger> iPoly = (GenPolynomial<edu.jas.arith.BigInteger>) jas.factorTerms(key)[2];
			if (iPoly.isConstant()) {
				continue;
			}
			Long val = entry.getValue();
			long varDegree = iPoly.degree(0);
			if (varDegree <= 2) {
				// solve Quadratic equation: a*x^2 + b*x + c = 0
				a = C0;
				b = C0;
				c = C0;
				for (Monomial<edu.jas.arith.BigInteger> monomial : iPoly) {
					edu.jas.arith.BigInteger coeff = monomial.coefficient();
					long lExp = monomial.exponent().getVal(0);
					if (lExp == 2) {
						a = integer(coeff.getVal());
					} else if (lExp == 1) {
						b = integer(coeff.getVal());
					} else if (lExp == 0) {
						c = integer(coeff.getVal());
					} else {
						throw new ArithmeticException("Roots::Unexpected exponent value: " + lExp);
					}
				}
				if (a.equals(C0)) {
					if (!b.equals(C0)) {
						IFraction rat = fraction(c, b);
						result.add(rat.negate());
					}
				} else {
					// 1 / (2*a)
					IFraction rev2a = fraction(C1, a.multiply(C2));
					// IAST discriminant = Plus(Sqr(b), Times(integer(-4), a, c));
					IInteger discriminant = b.multiply(b).add(integer(-4).multiply(a).multiply(c));
					if (discriminant.isNegative()) {
						// 2 complex roots
						IAST sqrt = Times(CI, Sqrt(discriminant.negate()));
						result.add(Times(rev2a, Plus(b.negate(), sqrt)));
						result.add(Times(rev2a, Subtract(b.negate(), sqrt)));
					} else {
						// 2 real roots
						IAST sqrt = Sqrt(discriminant);
						result.add(Times(rev2a, Plus(b.negate(), sqrt)));
						result.add(Times(rev2a, Plus(b.negate(), sqrt.negative())));
					}
				}
//			} else if (varDegree <= 3) {
//				iPoly = iPoly.monic();
//				// solve Cubic equation: x^3 + a*x^2 + b*x + c = 0
//				a = C0;
//				b = C0;
//				c = C0;
//				for (Monomial<edu.jas.arith.BigInteger> monomial : iPoly) {
//					edu.jas.arith.BigInteger coeff = monomial.coefficient();
//					long lExp = monomial.exponent().getVal(0);
//					if (lExp == 2) {
//						a = integer(coeff.getVal());
//					} else if (lExp == 1) {
//						b = integer(coeff.getVal());
//					} else if (lExp == 0) {
//						c = integer(coeff.getVal());
//					} else if (lExp == 3) {
//						if (!coeff.equals(edu.jas.arith.BigInteger.ONE)) {
//							throw new ArithmeticException("Roots::Solution for cubic equation with leading coefficient: \"" + coeff.toString()
//									+ "\" != 1 currently not implemented: ");
//						}
//					} else {
//						throw new ArithmeticException("Roots::Unexpected exponent value: " + lExp);
//					}
//				}
//				cubicSolution(result, F.C1, a, b, c);
			} else {
				IExpr temp = Power(jas.integerPoly2Expr(iPoly), integer(val));
				if (!numericSolutions) {
					result.add(temp);
				} else {
					IAST resultList = RootIntervals.croots(temp, true);
					if (resultList.size() > 0) {
						result.addAll(resultList);
					} else {
						result.add(temp);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Calculate the roots of the cubic polynomial
	 * <code>a*x^3 + b*x^2 + c*x + d</code>. See <a href=
	 * "http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots"
	 * >Wikipedia Cubic_function - General_formula_of_roots</a>
	 * 
	 * @param result
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
//	private static void cubicSolution(IAST result, IInteger a, IInteger b, IInteger c, IInteger d) {
//		// m = 2*b^3 - 9*a*b*c + 27 * a^2 * d
//		IInteger m = C2.multiply(b.pow(3)).subtract(a.multiply(b.multiply(c.multiply(integer(9))))).add(
//				a.pow(2).multiply(d.multiply(integer(27))));
//		// k = b^2 - 3*a*c
//		IInteger k = b.pow(2).subtract(C3.multiply(a.multiply(c)));
//		// n = m^2 - 4*k^3
//		IInteger n = m.pow(2).subtract(C4.multiply(k.pow(3)));
//
//		// omega1 = -(1/2) + 1/2 * Sqrt[3] * I
//		IExpr omega1 = Plus(CN1D2, Times(C1D2, Sqrt(C3), CI));
//		// omega2 = -(1/2) - 1/2 * Sqrt[3] * I
//		IExpr omega2 = Plus(CN1D2, Times(CN1D2, Sqrt(C3), CI));
//
//		// t1 = (1/2 * (m + n^(1/2))) ^ (1/3)
//		IExpr t1 = Power(Times(C1D2, Plus(m, Sqrt(n))), C1D3);
//		// t2 = (1/2 * (m - n^(1/2))) ^ (1/3)
//		IExpr t2 = Power(Times(C1D2, Subtract(m, Sqrt(n))), C1D3);
//
//		// (-1) / (3*a)
//		IFraction n1d3ta = F.fraction(F.CN1, C3.multiply(a));
//		result.add(Times(n1d3ta, Plus(b, t1, t2)));
//		result.add(Times(n1d3ta, Plus(b, Times(omega2, t1), Times(omega1, t2))));
//		result.add(Times(n1d3ta, Plus(b, Times(omega1, t1), Times(omega2, t2))));
//	}

}