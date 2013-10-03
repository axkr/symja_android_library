package org.matheclipse.core.polynomials;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.integer;

import java.util.HashSet;
import java.util.Set;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Solve polynomial equations up to fourth degree (<code>Solve(a*x^4+b*x^3+c*x^2+d*x+e==0,x)</code>).
 * 
 * See <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a> See <a
 * href="http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots">Wikipedia - Cubic function</a> See <a
 * href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>
 * 
 * 
 * <br />
 * TODO not completly tested. Especially if a division through zero occurs.
 */
public class QuarticSolver {

	public static IAST solve(IExpr exprPoly, IExpr x) throws ArithmeticException {
		IExpr[] coefficients = new IExpr[] { F.C0, F.C0, F.C0, F.C0, F.C0 };
		if (convert2Coefficients(exprPoly, x, coefficients)) {
			return quarticSolve(coefficients[4], coefficients[3], coefficients[2], coefficients[1], coefficients[0]);
		}
		return null;
	}

	private static boolean convert2Coefficients(IExpr exprPoly, IExpr x, IExpr[] coefficients) {
		if (exprPoly instanceof IAST) {
			final IAST ast = (IAST) exprPoly;
			if (ast.isPlus()) {
				for (int i = 1; i < ast.size(); i++) {
					if (!convertTerm2Coefficients(ast.get(i), x, coefficients)) {
						return false;
					}
				}
				return true;
			} else {
				if (convertTerm2Coefficients(ast, x, coefficients)) {
					return true;
				}
			}
		} else {
			if (convertTerm2Coefficients(exprPoly, x, coefficients)) {
				return true;
			}
		}
		return false;
	}

	private static boolean convertTerm2Coefficients(final IExpr exprPoly, IExpr x, IExpr[] coefficients) {
		if (exprPoly.isFree(x, true)) {
			coefficients[0] = F.eval(F.Plus(coefficients[0], exprPoly));
			return true;
		}
		if (exprPoly instanceof IAST) {
			IAST ast = (IAST) exprPoly;
			if (ast.isTimes()) {
				int exponent = -1;
				IAST coeff = ast.clone();
				for (int i = 1; i < ast.size(); i++) {
					if (ast.get(i).isPower()) {
						final IExpr temp = ast.get(i).getAt(1);
						if (x.equals(temp)) {
							try {
								exponent = Validate.checkPowerExponent((IAST) ast.get(i));
							} catch (WrongArgumentType e) {
							}
							if (exponent < 0 || exponent > 4) {
								return false;
							}
							coeff.remove(i);
							coefficients[exponent] = F.eval(F.Plus(coefficients[exponent], coeff));
							return true;
						}
					} else if (x.equals(ast.get(i))) {
						coeff.remove(i);
						coefficients[1] = F.eval(F.Plus(coefficients[1], coeff));
						return true;
					}
				}
				return true;
			} else if (ast.isPower()) {
				final IExpr temp = ast.get(1);
				if (x.equals(temp)) {
					int exponent = -1;
					try {
						exponent = Validate.checkPowerExponent((IAST) ast);
					} catch (WrongArgumentType e) {
					}
					if (exponent < 0 || exponent > 4) {
						return false;
					}
					coefficients[exponent] = F.eval(F.Plus(coefficients[exponent], F.C1));
					return true;
				}
			}
		} else if (exprPoly instanceof ISymbol) {
			if (x.equals(exprPoly)) {
				coefficients[1] = F.eval(F.Plus(coefficients[1], F.C1));
				return true;
			}
		}
		return false;
	}

	public static IAST quarticSolveN(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e) {
		return (IAST) F.evaln(quarticSolve(a, b, c, d, e));
	}

	/**
	 * <code>Solve(a*x^4+b*x^3+c*x^2+d*x+e==0,x)</code>. See <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia -
	 * Quartic equation</a>
	 * 
	 * @param e
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static IAST quarticSolve(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e) {
		if (a.isZero()) {
			return cubicSolve(b, c, d, e);
		} else {
			if (b.isZero() && d.isZero()) {
				return biQuadraticSolve(a, c, e);
			}
			if (a.equals(e) && b.equals(d)) {
				return specialQuadraticSolve2(a, b, c);
			}
			IAST result = F.List();
			// 256*a^3*e^3 - 192*a^2*b*d*e^2 - 128*a^2*c^2*e^2 +144*a^2*c*d^2*e - 27*a^2*d^4 + 144*a*b^2*c*e^2 - 6*a*b^2*d^2*e -
			// 80*a*b*c^2*d*e + 18*a*b*c*d^3 + 16*a*c^4*e - 4*a*c^3*d^2 - 27*b^4*e^2 + 18*b^3*c*d*e - 4*b^3*d^3 - 4*b^2*c^3*e +
			// b^2*c^2*d^2
			IExpr discriminant = F.eval(Plus(Times(integer(256L), Power(a, C3), Power(e, C3)),
					Times(CN1, integer(192L), Power(a, C2), b, d, Power(e, C2)),
					Times(CN1, integer(128L), Power(a, C2), Power(c, C2), Power(e, C2)),
					Times(integer(144L), Power(a, C2), c, Power(d, C2), e), Times(CN1, integer(27L), Power(a, C2), Power(d, C4)),
					Times(integer(144L), a, Power(b, C2), c, Power(e, C2)),
					Times(CN1, integer(6L), a, Power(b, C2), Power(d, C2), e), Times(CN1, integer(80L), a, b, Power(c, C2), d, e),
					Times(integer(18L), a, b, c, Power(d, C3)), Times(integer(16L), a, Power(c, C4), e),
					Times(CN1, C4, a, Power(c, C3), Power(d, C2)), Times(CN1, integer(27L), Power(b, C4), Power(e, C2)),
					Times(integer(18L), Power(b, C3), c, d, e), Times(CN1, C4, Power(b, C3), Power(d, C3)),
					Times(CN1, C4, Power(b, C2), Power(c, C3), e), Times(Power(b, C2), Power(c, C2), Power(d, C2))));

			// 64*a^3*e - 16*a^2*c^2 + 16*a*b^2*c + 16*a^2*b*d - 3*b^4
			IExpr dd = F
					.eval(Plus(Times(integer(64L), Power(a, C3), e), Times(CN1, integer(16L), Power(a, C2), Power(c, C2)),
							Times(integer(16L), a, Power(b, C2), c), Times(integer(16L), Power(a, C2), b, d),
							Times(CN1, C3, Power(b, C4))));

			// c^2 - 3*b*d + 12*a*e
			IExpr delta0 = F.eval(Plus(Power(c, C2), Times(CN1, C3, b, d), Times(integer(12L), a, e)));
			// 2*c^3 - 9*b*c*d + 27*a*d^2 + 27*b^2*e - 72*a*c*e
			IExpr delta1 = F.eval(Plus(Times(C2, Power(c, C3)), Times(CN1, integer(9L), b, c, d),
					Times(integer(27L), a, Power(d, C2)), Times(integer(27L), Power(b, C2), e), Times(CN1, integer(72L), a, c, e)));
			// (delta1 + Sqrt[delta1^2-4*delta0^3])^(1/3)
			IExpr delta3 = F.eval(Power(Plus(delta1, Sqrt(Plus(Power(delta1, C2), Times(CN1, C4, Power(delta0, C3))))), C1D3));

			// -b/(4 a) - Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
			// a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3 2^(1/3) a) - (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
			// Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3) + delta3/(3 2^(1/3) a)])]/2
			result.add(Plus(
					Times(CN1, b, Power(Times(C4, a), CN1)),
					Times(CN1,
							C1D2,
							Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
									Times(CN1, C2, c, Power(Times(C3, a), CN1)),
									Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
					Times(CN1,
							C1D2,
							Sqrt(Plus(
									Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
									Times(CN1, C4, c, Power(Times(C3, a), CN1)),
									Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
									Times(CN1,
											Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)),
													Times(C4, b, c, Power(Power(a, C2), CN1)),
													Times(CN1, integer(8L), d, Power(a, CN1))),
											Power(Times(
													C4,
													Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
															Times(CN1, C2, c, Power(Times(C3, a), CN1)),
															Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
															Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))), CN1)))))));

			// -b/(4 a) - Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3) + delta3/(3 2^(1/3) a)]/2 + Sqrt[b^2/(2
			// a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3 2^(1/3) a) - (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
			// Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3) + delta3/(3 2^(1/3) a)])]/2
			result.add(Plus(
					Times(CN1, b, Power(Times(C4, a), CN1)),
					Times(CN1,
							C1D2,
							Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
									Times(CN1, C2, c, Power(Times(C3, a), CN1)),
									Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
					Times(C1D2,
							Sqrt(Plus(
									Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
									Times(CN1, C4, c, Power(Times(C3, a), CN1)),
									Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
									Times(CN1,
											Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)),
													Times(C4, b, c, Power(Power(a, C2), CN1)),
													Times(CN1, integer(8L), d, Power(a, CN1))),
											Power(Times(
													C4,
													Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
															Times(CN1, C2, c, Power(Times(C3, a), CN1)),
															Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
															Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))), CN1)))))));

			// -b/(4 a) + Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
			// a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3 2^(1/3) a) + (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
			// Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3) + delta3/(3 2^(1/3) a)])]/2
			result.add(Plus(
					Times(CN1, b, Power(Times(C4, a), CN1)),
					Times(C1D2,
							Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
									Times(CN1, C2, c, Power(Times(C3, a), CN1)),
									Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
					Times(CN1,
							C1D2,
							Sqrt(Plus(
									Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
									Times(CN1, C4, c, Power(Times(C3, a), CN1)),
									Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
									Times(Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)),
											Times(C4, b, c, Power(Power(a, C2), CN1)), Times(CN1, integer(8L), d, Power(a, CN1))),
											Power(Times(
													C4,
													Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
															Times(CN1, C2, c, Power(Times(C3, a), CN1)),
															Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
															Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))), CN1)))))));

			// -b/(4 a) + Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
			// a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3 2^(1/3) a) + (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
			// Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3) + delta3/(3 2^(1/3) a)])]/2
			result.add(Plus(
					Times(CN1, b, Power(Times(C4, a), CN1)),
					Times(C1D2,
							Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
									Times(CN1, C2, c, Power(Times(C3, a), CN1)),
									Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
					Times(C1D2,
							Sqrt(Plus(
									Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
									Times(CN1, C4, c, Power(Times(C3, a), CN1)),
									Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
									Times(Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)),
											Times(C4, b, c, Power(Power(a, C2), CN1)), Times(CN1, integer(8L), d, Power(a, CN1))),
											Power(Times(
													C4,
													Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
															Times(CN1, C2, c, Power(Times(C3, a), CN1)),
															Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
															Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))), CN1)))))));
			return createSet(result);
		}

	}

	/**
	 * <code>Solve(a*x^3+b*x^2+c*x+d==0,x)</code>. See <a
	 * href="http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots">Wikipedia - Cubic function</a>
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static IAST cubicSolve(IExpr a, IExpr b, IExpr c, IExpr d) {
		if (a.isZero()) {
			return quadraticSolve(b, c, d);
		} else {
			IAST result = F.List();
			// 18*a*b*c*d-4*b^3*d+b^2*c^2-4*a*c^3-27*a^2*d^2
			IExpr discriminant = F.eval(Plus(Times(integer(18L), a, b, c, d), Times(CN1, C4, Power(b, C3), d),
					Times(Power(b, C2), Power(c, C2)), Times(CN1, C4, a, Power(c, C3)),
					Times(CN1, integer(27L), Power(a, C2), Power(d, C2))));
			// b^2 - 3*a*c
			IExpr delta0 = F.eval(Plus(Power(b, C2), Times(CN1, C3, a, c)));
			// (-2)*b^3 + 9*a*b*c - 27*a^2*d
			IExpr delta1 = F.eval(Plus(Times(integer(-2L), Power(b, C3)), Times(integer(9L), a, b, c),
					Times(CN1, integer(27L), Power(a, C2), d)));
			// (delta1 + Sqrt[delta1^2-4*delta0^3])^(1/3)
			IExpr delta3 = F.eval(Power(Plus(delta1, Sqrt(Plus(Power(delta1, C2), Times(CN1, C4, Power(delta0, C3))))), C1D3));

			if (discriminant.isZero()) {
				if (delta0.isZero()) {
					// the three roots are equal
					// (-b)/(3*a)
					result.add(Times(CN1, b, Power(Times(C3, a), CN1)));
				} else {
					// the double root
					// (9*a*d-b*c)/(2*delta0)
					result.add(Times(Plus(Times(integer(9L), a, d), Times(CN1, b, c)), Power(Times(C2, delta0), CN1)));
					// and a simple root
					// (4*a*b*c-9*a^2*d-b^3)/(a*delta0)
					result.add(Times(Plus(Times(C4, a, b, c), Times(CN1, integer(9L), Power(a, C2), d), Times(CN1, Power(b, C3))),
							Power(Times(a, delta0), CN1)));
				}
			} else {
				// -(b/(3*a)) - (2^(1/3) (-delta0))/(3*a*delta3) + delta3/(3*2^(1/3)*a)
				result.add(Plus(Times(CN1, b, Power(Times(C3, a), CN1)),
						Times(CN1, Power(C2, C1D3), CN1, delta0, Power(Times(C3, a, delta3), CN1)),
						Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))));

				// -(b/(3*a)) + ((1 + I Sqrt[3]) (-delta0))/(3*2^(2/3)*a*delta3) - ((1 - I Sqrt[3]) delta3)/(6*2^(1/3)*a)
				result.add(Plus(
						Times(CN1, b, Power(Times(C3, a), CN1)),
						Times(Plus(C1, Times(CI, Sqrt(C3))), CN1, delta0,
								Power(Times(C3, Power(C2, fraction(2L, 3L)), a, delta3), CN1)),
						Times(CN1, Plus(C1, Times(CN1, CI, Sqrt(C3))), delta3, Power(Times(integer(6L), Power(C2, C1D3), a), CN1))));

				// -(b/(3*a)) + ((1 - I Sqrt[3]) (-delta0))/(3*2^(2/3)*a*delta3) - ((1 + I Sqrt[3]) delta3)/(6*2^(1/3)*a)
				result.add(Plus(
						Times(CN1, b, Power(Times(C3, a), CN1)),
						Times(Plus(C1, Times(CN1, CI, Sqrt(C3))), CN1, delta0,
								Power(Times(C3, Power(C2, fraction(2L, 3L)), a, delta3), CN1)),
						Times(CN1, Plus(C1, Times(CI, Sqrt(C3))), delta3, Power(Times(integer(6L), Power(C2, C1D3), a), CN1))));
			}
			return createSet(result);
		}
	}

	private static IAST createSet(IAST result) {
		Set<IExpr> set1 = new HashSet<IExpr>();
		for (int i = 1; i < result.size(); i++) {
			IExpr temp = F.evalExpandAll(result.get(i));
			if (!temp.equals(F.Indeterminate)) {
				set1.add(temp);
			}
		}
		result = F.List();
		for (IExpr e : set1) {
			result.add(e);
		}
		return result;
	}

	/**
	 * <code>Solve(a*x^2+b*x+c==0,x)</code>. See <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic
	 * equation</a>
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static IAST quadraticSolve(IExpr a, IExpr b, IExpr c) {
		IAST result = F.List();
		if (!a.isZero()) {
			if (c.isZero()) {
				result.add(F.C0);
				if (!b.isZero()) {
					result.add(F.Times(F.CN1, b, Power(a, CN1)));
				}
			} else {
				result.add(Times(Plus(Times(CN1, b), Sqrt(Plus(Power(b, C2), Times(CN1, C4, a, c)))), Power(Times(C2, a), CN1)));

				result.add(Times(Plus(Times(CN1, b), Times(CN1, Sqrt(Plus(Power(b, C2), Times(CN1, C4, a, c))))),
						Power(Times(C2, a), CN1)));
				return createSet(result);
			}
		} else {
			if (!b.isZero()) {
				result.add(Times(CN1, c, Power(b, CN1)));
			}
		}
		return result;
	}

	/**
	 * Solve the bi-quadratic expression. <code>Solve(a*x^4+bc*x^2+e==0,x)</code>.
	 * 
	 * See Bronstein 1.6.2.4
	 * 
	 * @param a
	 * @param c
	 * @param e
	 * @return
	 */
	public static IAST biQuadraticSolve(IExpr a, IExpr c, IExpr e) {
		IAST result = F.List();
		// Sqrt[c^2-4*a*e]
		IExpr sqrt = F.eval(Sqrt(Plus(Power(c,C2),Times(CN1,C4,a,e))));

		// (-c+sqrt)/(2*a)
		IExpr y1 = Times(Plus(Times(CN1, c), sqrt), Power(Times(C2, a), CN1));

		// -(c+sqrt)/(2*a)
		IExpr y2 = Times(CN1, Plus(c, sqrt), Power(Times(C2, a), CN1));
		result.add(Sqrt(y1));
		result.add(Times(CN1, Sqrt(y1)));
		result.add(Sqrt(y2));
		result.add(Times(CN1, Sqrt(y2)));
		return createSet(result);
	}

	/**
	 * Solve the special case quartic equation <code>Solve(a*x^4+b*x^3+c*x^2+b*x+a==0,x)</code>. See <a
	 * href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>. See Bronstein 1.6.2.4
	 * 
	 * @param a
	 * @param c
	 * @param e
	 * @return
	 */
	public static IAST specialQuadraticSolve2(IExpr a, IExpr b, IExpr c) {
		IAST result = F.List();
		// Sqrt[b^2-4*a*c+8*a^2]
		IExpr sqrt = F.eval(Sqrt(Plus(Power(b, C2), Times(CN1, C4, a, c), Times(ZZ(8L), Power(a, C2)))));

		// (-b+sqrt)/(2*a)
		IExpr y1 = Times(Plus(Times(CN1, b), sqrt), Power(Times(C2, a), CN1));
		// -(b+sqrt)/(2*a)
		IExpr y2 = Times(CN1, Plus(b, sqrt), Power(Times(C2, a), CN1));

		// (y1+Sqrt[y1^2-4])/2
		result.add(Times(C1D2, Plus(y1, Sqrt(Plus(Power(y1, C2), Times(CN1, C4))))));
		// (y1-Sqrt[y1^2-4])/2
		result.add(Times(C1D2, Plus(y1, Times(CN1, Sqrt(Plus(Power(y1, C2), Times(CN1, C4)))))));
		// (y2+Sqrt[y2^2-4])/2
		result.add(Times(C1D2, Plus(y2, Sqrt(Plus(Power(y2, C2), Times(CN1, C4))))));
		// (y2-Sqrt[y2^2-4])/2
		result.add(Times(C1D2, Plus(y2, Times(CN1, Sqrt(Plus(Power(y2, C2), Times(CN1, C4)))))));

		return createSet(result);
	}
}
