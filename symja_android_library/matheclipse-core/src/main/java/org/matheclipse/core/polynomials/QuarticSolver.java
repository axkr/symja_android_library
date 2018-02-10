package org.matheclipse.core.polynomials;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D4;
import static org.matheclipse.core.expression.F.CN3;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.integer;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Solve polynomial equations up to fourth degree ( <code>Solve(a*x^4+b*x^3+c*x^2+d*x+e==0,x)</code>).
 * 
 * See <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a> See
 * <a href= "http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots"> Wikipedia - Cubic function</a> See
 * <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>
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
		return F.NIL;
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
				IASTAppendable coeff = ast.copyAppendable();
				for (int i = 1; i < ast.size(); i++) {
					IExpr arg = ast.get(i);
					if (arg.isPower()) {
						final IExpr base = arg.base();
						if (x.equals(base)) {
							try {
								exponent = Validate.checkPowerExponent((IAST) arg);
							} catch (WrongArgumentType e) {
							}
							if (exponent < 0 || exponent > 4) {
								return false;
							}
							coeff.remove(i);
							coefficients[exponent] = F.eval(F.Plus(coefficients[exponent], coeff));
							return true;
						}
					} else if (x.equals(arg)) {
						coeff.remove(i);
						coefficients[1] = F.eval(F.Plus(coefficients[1], coeff));
						return true;
					}
				}
				return true;
			} else if (ast.isPower()) {
				final IExpr temp = ast.arg1();
				if (x.equals(temp)) {
					int exponent = -1;
					try {
						exponent = Validate.checkPowerExponent(ast);
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

	@Nonnull
	public static IAST quarticSolveN(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e) {
		return (IAST) F.evaln(quarticSolve(a, b, c, d, e));
	}

	/**
	 * <code>Solve(a*x^4+b*x^3+c*x^2+d*x+e==0,x)</code>. See
	 * <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>
	 * 
	 * @param e
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	@Nonnull
	public static IASTAppendable quarticSolve(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e) {
		if (a.isZero()) {
			return cubicSolve(b, c, d, e, null);
		} else {
			if (e.isZero()) {
				return cubicSolve(a, b, c, d, C0);
			}
			if (b.isZero() && d.isZero()) {
				return biQuadraticSolve(a, c, e, null);
			}
			if (a.equals(e) && b.equals(d)) {
				return quasiSymmetricQuarticSolve(a, b, c);
			}

			// -3*b^2/(8*a^2) + c/a
			IExpr alpha = F.eval(
					Plus(Times(CN3, Power(b, C2), Power(Times(ZZ(8L), Power(a, C2)), CN1)), Times(c, Power(a, CN1))));
			// b^3/(8*a^3) - b*c/(2*a^2) + d/a
			IExpr beta = F.eval(Plus(Times(Power(b, C3), Power(Times(ZZ(8L), Power(a, C3)), CN1)),
					Times(CN1, b, c, Power(Times(C2, Power(a, C2)), CN1)), Times(d, Power(a, CN1))));
			// -3*b^4/(256*a^4) + b^2*c/(16*a^3) - b*d/(4*a^2) + e/a
			IExpr gamma = F.eval(Plus(Times(CN3, Power(b, C4), Power(Times(ZZ(256L), Power(a, C4)), CN1)),
					Times(Power(b, C2), c, Power(Times(ZZ(16L), Power(a, C3)), CN1)),
					Times(CN1, b, d, Power(Times(C4, Power(a, C2)), CN1)), Times(e, Power(a, CN1))));
			if (beta.isZero()) {
				// -1/4 * b/a
				return biQuadraticSolve(C1, alpha, gamma, Times(CN1D4, b, Power(a, CN1)));
			}

			// return depressedQuarticSolve(a, b, alpha, beta, gamma);
			IASTAppendable result = F.ListAlloc(6);

			// 256*a^3*e^3 - 192*a^2*b*d*e^2 - 128*a^2*c^2*e^2 +144*a^2*c*d^2*e
			// - 27*a^2*d^4 + 144*a*b^2*c*e^2 - 6*a*b^2*d^2*e -
			// 80*a*b*c^2*d*e + 18*a*b*c*d^3 + 16*a*c^4*e - 4*a*c^3*d^2 -
			// 27*b^4*e^2 + 18*b^3*c*d*e - 4*b^3*d^3 - 4*b^2*c^3*e +
			// b^2*c^2*d^2

			// IExpr discriminant = F.eval(Plus(Times(integer(256L), Power(a,
			// C3), Power(e, C3)),
			// Times(CN1, integer(192L), Power(a, C2), b, d, Power(e, C2)),
			// Times(CN1, integer(128L), Power(a, C2), Power(c, C2), Power(e,
			// C2)),
			// Times(integer(144L), Power(a, C2), c, Power(d, C2), e),
			// Times(CN1, integer(27L), Power(a, C2), Power(d, C4)),
			// Times(integer(144L), a, Power(b, C2), c, Power(e, C2)),
			// Times(CN1, integer(6L), a, Power(b, C2), Power(d, C2), e),
			// Times(CN1, integer(80L), a, b, Power(c, C2), d, e),
			// Times(integer(18L), a, b, c, Power(d, C3)), Times(integer(16L),
			// a, Power(c, C4), e),
			// Times(CN1, C4, a, Power(c, C3), Power(d, C2)), Times(CN1,
			// integer(27L), Power(b, C4), Power(e, C2)),
			// Times(integer(18L), Power(b, C3), c, d, e), Times(CN1, C4,
			// Power(b, C3), Power(d, C3)),
			// Times(CN1, C4, Power(b, C2), Power(c, C3), e), Times(Power(b,
			// C2), Power(c, C2), Power(d, C2))));

			// 64*a^3*e - 16*a^2*c^2 + 16*a*b^2*c + 16*a^2*b*d - 3*b^4

			// IExpr dd = F
			// .eval(Plus(Times(integer(64L), Power(a, C3), e), Times(CN1,
			// integer(16L), Power(a, C2), Power(c, C2)),
			// Times(integer(16L), a, Power(b, C2), c), Times(integer(16L),
			// Power(a, C2), b, d),
			// Times(CN1, C3, Power(b, C4))));

			// c^2 - 3*b*d + 12*a*e
			IExpr delta0 = F.eval(Plus(Power(c, C2), Times(CN1, C3, b, d), Times(integer(12L), a, e)));
			// 2*c^3 - 9*b*c*d + 27*a*d^2 + 27*b^2*e - 72*a*c*e
			IExpr delta1 = F.eval(Plus(Times(C2, Power(c, C3)), Times(CN1, integer(9L), b, c, d),
					Times(integer(27L), a, Power(d, C2)), Times(integer(27L), Power(b, C2), e),
					Times(CN1, integer(72L), a, c, e)));
			// (delta1 + Sqrt[delta1^2-4*delta0^3])^(1/3)
			IExpr delta3 = F
					.eval(Power(Plus(delta1, Sqrt(Plus(Power(delta1, C2), Times(CN1, C4, Power(delta0, C3))))), C1D3));

			// -b/(4 a) - Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3
			// a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
			// a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3
			// 2^(1/3) a) - (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
			// Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3)
			// + delta3/(3 2^(1/3) a)])]/2
			result.append(Plus(Times(CN1, b, Power(Times(C4, a), CN1)),
					Times(CN1, C1D2,
							Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
									Times(CN1, C2, c, Power(Times(C3, a), CN1)),
									Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
					Times(CN1, C1D2, Sqrt(Plus(Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
							Times(CN1, C4, c, Power(Times(C3, a), CN1)),
							Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
							Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
							Times(CN1,
									Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)), Times(C4, b, c,
											Power(Power(a, C2), CN1)), Times(CN1, integer(8L), d, Power(a, CN1))),
									Power(Times(C4,
											Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
													Times(CN1, C2, c, Power(Times(C3, a), CN1)),
													Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
													Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
											CN1)))))));

			// -b/(4 a) - Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3
			// a delta3) + delta3/(3 2^(1/3) a)]/2 + Sqrt[b^2/(2
			// a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3
			// 2^(1/3) a) - (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
			// Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3)
			// + delta3/(3 2^(1/3) a)])]/2
			result.append(Plus(Times(CN1, b, Power(Times(C4, a), CN1)),
					Times(CN1, C1D2,
							Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
									Times(CN1, C2, c, Power(Times(C3, a), CN1)),
									Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
					Times(C1D2, Sqrt(Plus(Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
							Times(CN1, C4, c, Power(Times(C3, a), CN1)),
							Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
							Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
							Times(CN1,
									Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)), Times(C4, b, c,
											Power(Power(a, C2), CN1)), Times(CN1, integer(8L), d, Power(a, CN1))),
									Power(Times(C4,
											Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
													Times(CN1, C2, c, Power(Times(C3, a), CN1)),
													Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
													Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
											CN1)))))));

			// -b/(4 a) + Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3
			// a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
			// a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3
			// 2^(1/3) a) + (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
			// Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3)
			// + delta3/(3 2^(1/3) a)])]/2
			result.append(Plus(Times(CN1, b, Power(Times(C4, a), CN1)),
					Times(C1D2,
							Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
									Times(CN1, C2, c, Power(Times(C3, a), CN1)),
									Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
					Times(CN1, C1D2, Sqrt(Plus(Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
							Times(CN1, C4, c, Power(Times(C3, a), CN1)),
							Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
							Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)), Times(
									Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)), Times(C4, b, c,
											Power(Power(a, C2), CN1)), Times(CN1, integer(8L), d, Power(a, CN1))),
									Power(Times(C4,
											Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
													Times(CN1, C2, c, Power(Times(C3, a), CN1)),
													Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
													Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
											CN1)))))));

			// -b/(4 a) + Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3
			// a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
			// a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3
			// 2^(1/3) a) + (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
			// Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3)
			// + delta3/(3 2^(1/3) a)])]/2
			result.append(Plus(Times(CN1, b, Power(Times(C4, a), CN1)),
					Times(C1D2,
							Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
									Times(CN1, C2, c, Power(Times(C3, a), CN1)),
									Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
									Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
					Times(C1D2, Sqrt(Plus(Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
							Times(CN1, C4, c, Power(Times(C3, a), CN1)),
							Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
							Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)), Times(
									Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)), Times(C4, b, c,
											Power(Power(a, C2), CN1)), Times(CN1, integer(8L), d, Power(a, CN1))),
									Power(Times(C4,
											Sqrt(Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
													Times(CN1, C2, c, Power(Times(C3, a), CN1)),
													Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
													Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
											CN1)))))));
			return createSet(result);
		}

	}

	/**
	 * <code>Solve(a*x^4+b*x^3+c*x^2+d*x+e==0,x)</code>. See
	 * <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>
	 * 
	 * @param e
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static IAST depressedQuarticSolve(IExpr A, IExpr B, IExpr a, IExpr b, IExpr c) {
		IASTAppendable result = F.ListAlloc(5);
		// -(a^2/12+c)
		IExpr P = F.eval(Times(CN1, Plus(Times(QQ(1L, 12L), Power(a, C2)), c)));
		// -a^3/108 + (a*c)/3 - (b^2)/8
		IExpr Q = F.eval(Plus(Times(QQ(-1L, 108L), Power(a, C3)), Times(C1D3, a, c), Times(QQ(-1L, 8L), Power(b, C2))));

		IExpr y;
		if (P.isZero()) {
			// -5/6*a - Q^(1/3)
			y = F.eval(Plus(Times(QQ(-5L, 6L), a), Times(CN1, Power(Q, C1D3))));
		} else {
			// (-Q/2 + Sqrt[Q^2/4 + P^3/27])^(1/3)
			IExpr U = F.eval(Power(
					Plus(Times(C1D2, CN1, Q), Sqrt(Plus(Times(C1D4, Power(Q, C2)), Times(QQ(1L, 27L), Power(P, C3))))),
					C1D3));
			// -5/6*a - U - P/(3*U)
			y = Plus(Times(QQ(-5L, 6L), a), Times(CN1, U), Times(CN1, P, Power(Times(C3, U), CN1)));
		}
		// Sqrt[a+2*y]
		IExpr w = Sqrt(Plus(a, Times(C2, y)));
		// b/(2*w)
		// IExpr z = Times(b, Power(Times(C2, w), CN1));

		// -B/(4*A) + 1/2 * (w + Sqrt[-(a+2*y)-2*(a+b/w)])
		result.append(Plus(Times(CN1, B, Power(Times(C4, A), CN1)), Times(C1D2, Plus(w,
				Sqrt(Plus(Times(CN1, Plus(a, Times(C2, y))), Times(CN1, C2, Plus(a, Times(b, Power(w, CN1))))))))));
		// -B/(4*A) + 1/2 * (w - Sqrt[-(a+2*y)-2*(a+b/w)])
		result.append(Plus(Times(CN1, B, Power(Times(C4, A), CN1)), Times(C1D2, Plus(w, Times(CN1,
				Sqrt(Plus(Times(CN1, Plus(a, Times(C2, y))), Times(CN1, C2, Plus(a, Times(b, Power(w, CN1)))))))))));
		// -B/(4*A) + 1/2 * (-w + Sqrt[-(a+2*y)-2*(a-b/w)])
		result.append(Plus(Times(CN1, B, Power(Times(C4, A), CN1)), Times(C1D2, Plus(Times(CN1, w), Sqrt(
				Plus(Times(CN1, Plus(a, Times(C2, y))), Times(CN1, C2, Plus(a, Times(CN1, b, Power(w, CN1))))))))));
		// -B/(4*A) + 1/2 * (-w - Sqrt[-(a+2*y)-2*(a-b/w)])
		result.append(Plus(Times(CN1, B, Power(Times(C4, A), CN1)), Times(C1D2, Plus(Times(CN1, w), Times(CN1, Sqrt(
				Plus(Times(CN1, Plus(a, Times(C2, y))), Times(CN1, C2, Plus(a, Times(CN1, b, Power(w, CN1)))))))))));
		//
		return createSet(result);
	}

	/**
	 * <code>Solve(a*x^3+b*x^2+c*x+d==0,x)</code>. See
	 * <a href= "http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots"> Wikipedia - Cubic function</a>
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param additionalSulution
	 *            TODO
	 * @return
	 */
	public static IASTAppendable cubicSolve(IExpr a, IExpr b, IExpr c, IExpr d, IExpr additionalSulution) {
		if (a.isZero()) {
			return quadraticSolve(b, c, d, additionalSulution, null);
		} else {
			if (d.isZero()) {
				return quadraticSolve(a, b, c, additionalSulution, C0);
			}
			IASTAppendable result = F.ListAlloc(4);
			if (additionalSulution != null) {
				result.append(additionalSulution);
			}
			// 18*a*b*c*d-4*b^3*d+b^2*c^2-4*a*c^3-27*a^2*d^2
			IExpr discriminant = F.eval(Plus(Times(integer(18L), a, b, c, d), Times(CN4, Power(b, C3), d),
					Times(Power(b, C2), Power(c, C2)), Times(CN4, a, Power(c, C3)),
					Times(integer(-27L), Power(a, C2), Power(d, C2))));
			// b^2 - 3*a*c
			IExpr delta0 = F.eval(Plus(Power(b, C2), Times(CN1, C3, a, c)));
			// (-2)*b^3 + 9*a*b*c - 27*a^2*d
			IExpr delta1 = F.eval(Plus(Times(integer(-2L), Power(b, C3)), Times(integer(9L), a, b, c),
					Times(CN1, integer(27L), Power(a, C2), d)));
			// (delta1 + Sqrt[delta1^2-4*delta0^3])^(1/3)
			IExpr argDelta3 = F.eval(Plus(delta1, Sqrt(Plus(Power(delta1, C2), Times(CN1, C4, Power(delta0, C3))))));
			IExpr delta3 = F.eval(Power(argDelta3, C1D3));

			// IExpr C = F.eval(Times(integer(-27L), a.power(C2), discriminant));
			if (discriminant.isZero()) {
				if (delta0.isZero()) {
					// the three roots are equal
					// (-b)/(3*a)
					result.append(Times(CN1, b, Power(Times(C3, a), CN1)));
				} else {
					// the double root
					// (9*a*d-b*c)/(2*delta0)
					result.append(
							Times(Plus(Times(integer(9L), a, d), Times(CN1, b, c)), Power(Times(C2, delta0), CN1)));
					// and a simple root
					// (4*a*b*c-9*a^2*d-b^3)/(a*delta0)
					result.append(Times(Plus(Times(C4, a, b, c), Times(CN1, integer(9L), Power(a, C2), d),
							Times(CN1, Power(b, C3))), Power(Times(a, delta0), CN1)));
				}
			} else {
				// -(b/(3*a)) - (2^(1/3) (-delta0))/(3*a*delta3) +
				// delta3/(3*2^(1/3)*a)
				result.append(Plus(b.negate().times(C3.times(a).power(CN1)),
						Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
						Times(Power(argDelta3.timesDistributed(C1D2), C1D3), C3.times(a).power(CN1))));

				// -(b/(3*a)) + ((1 + I Sqrt[3]) (-delta0))/(3*2^(2/3)*a*delta3)
				// - ((1 - I Sqrt[3]) delta3)/(6*2^(1/3)*a)
				result.append(Plus(Times(CN1, b, Power(Times(C3, a), CN1)),
						Times(Plus(C1, Times(CI, Sqrt(C3))), CN1, delta0,
								Power(Times(C3, Power(C2, fraction(2L, 3L)), a, delta3), CN1)),
						Times(CN1, Plus(C1, Times(CN1, CI, Sqrt(C3))), delta3,
								Power(Times(integer(6L), Power(C2, C1D3), a), CN1))));

				// -(b/(3*a)) + ((1 - I Sqrt[3]) (-delta0))/(3*2^(2/3)*a*delta3)
				// - ((1 + I Sqrt[3]) delta3)/(6*2^(1/3)*a)
				result.append(Plus(Times(CN1, b, Power(Times(C3, a), CN1)),
						Times(Plus(C1, Times(CN1, CI, Sqrt(C3))), CN1, delta0,
								Power(Times(C3, Power(C2, fraction(2L, 3L)), a, delta3), CN1)),
						Times(CN1, Plus(C1, Times(CI, Sqrt(C3))), delta3,
								Power(Times(integer(6L), Power(C2, C1D3), a), CN1))));
			}
			return createSet(result);
		}
	}

	public static IASTAppendable createSet(IASTAppendable result) {
		Set<IExpr> set1 = new TreeSet<IExpr>();
		for (int i = 1; i < result.size(); i++) {
			IExpr temp = result.get(i);
			if (temp.isPlus() || temp.isTimes() || temp.isPower()) {
				temp = F.evalExpandAll(temp);// org.matheclipse.core.reflection.system.PowerExpand.powerExpand((IAST)
												// temp, false);
			}
			if (temp.isAtom() && !temp.isIndeterminate()) {
				set1.add(temp);
				continue;
			}
			temp = F.eval(temp);
			if (temp.isAtom() && !temp.isIndeterminate()) {
				set1.add(temp);
				continue;
			}
			temp = F.evalExpandAll(temp);
			if (!temp.isIndeterminate()) {
				set1.add(temp);
			}
		}
		result = F.ListAlloc(set1.size());
		for (IExpr e : set1) {
			result.append(e);
		}
		return result;
	}

	/**
	 * <code>Solve(a*x^2+b*x+c==0,x)</code>. See <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia -
	 * Quadratic equation</a>
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param solution1
	 *            TODO
	 * @param solution2
	 *            TODO
	 * @return
	 */
	public static IASTAppendable quadraticSolve(IExpr a, IExpr b, IExpr c, IExpr solution1, IExpr solution2) {
		IASTAppendable result = F.ListAlloc(5);
		if (solution1 != null) {
			result.append(solution1);
		}
		if (solution2 != null) {
			result.append(solution2);
		}
		if (!a.isZero()) {
			if (c.isZero()) {
				result.append(F.C0);
				if (!b.isZero()) {
					result.append(F.Times(F.CN1, b, Power(a, -1L)));
				}
			} else {
				if (b.isZero()) {
					IExpr discriminant = F.evalExpand(a.times(c).negate());
					discriminant = discriminant.sqrt();
					result.append(Times(discriminant, Power(a, -1L)));
					result.append(Times(discriminant.negate(), Power(a, -1L)));
				} else {
					IExpr discriminant = F.evalExpand(Plus(F.Sqr(b), a.times(c).times(F.C4).negate()));
					discriminant = discriminant.sqrt();
					result.append(Times(Plus(b.negate(), discriminant), Power(a.times(F.C2), -1L)));
					result.append(Times(Plus(b.negate(), discriminant.negate()), Power(a.times(F.C2), -1L)));
				}
				return createSet(result);
			}
		} else {
			if (!b.isZero()) {
				result.append(Times(CN1, c, Power(b, -1L)));
			}
		}
		return createSet(result);
	}

	/**
	 * Solve the bi-quadratic expression. <code>Solve(a*x^4+bc*x^2+e==0,x)</code>.
	 * 
	 * See Bronstein 1.6.2.4
	 * 
	 * @param a
	 * @param c
	 * @param e
	 * @param subtrahend
	 *            TODO
	 * @return
	 */
	public static IASTAppendable biQuadraticSolve(IExpr a, IExpr c, IExpr e, IExpr sum) {
		IASTAppendable result = F.ListAlloc(4);
		// Sqrt[c^2-4*a*e]
		IExpr sqrt = F.eval(Sqrt(Plus(Power(c, C2), Times(CN1, C4, a, e))));

		// (-c+sqrt)/(2*a)
		IExpr y1 = Times(Plus(Times(CN1, c), sqrt), Power(Times(C2, a), CN1));

		// -(c+sqrt)/(2*a)
		IExpr y2 = Times(CN1, Plus(c, sqrt), Power(Times(C2, a), CN1));
		if (sum == null) {
			result.append(Sqrt(y1));
			result.append(Times(CN1, Sqrt(y1)));
			result.append(Sqrt(y2));
			result.append(Times(CN1, Sqrt(y2)));
		} else {
			result.append(Plus(sum, Sqrt(y1)));
			result.append(Plus(sum, Times(CN1, Sqrt(y1))));
			result.append(Plus(sum, Sqrt(y2)));
			result.append(Plus(sum, Times(CN1, Sqrt(y2))));
		}
		return createSet(result);
	}

	/**
	 * Solve the special case of a "Quasi-symmetric equations" <code>Solve(a*x^4+b*x^3+c*x^2+b*x+a==0,x)</code>. See
	 * <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>. See Bronstein 1.6.2.4
	 * 
	 * @param a
	 * @param c
	 * @param e
	 * @return
	 */
	public static IASTAppendable quasiSymmetricQuarticSolve(IExpr a, IExpr b, IExpr c) {
		IASTAppendable result = F.ListAlloc(4);
		// Sqrt[b^2-4*a*c+8*a^2]
		IExpr sqrt = F.eval(Sqrt(Plus(Power(b, C2), Times(CN1, C4, a, c), Times(ZZ(8L), Power(a, C2)))));

		// (-b+sqrt)/(2*a)
		IExpr y1 = Times(Plus(Times(CN1, b), sqrt), Power(Times(C2, a), CN1));
		// -(b+sqrt)/(2*a)
		IExpr y2 = Times(CN1, Plus(b, sqrt), Power(Times(C2, a), CN1));

		// (y1+Sqrt[y1^2-4])/2
		result.append(Times(C1D2, Plus(y1, Sqrt(Plus(Power(y1, C2), Times(CN1, C4))))));
		// (y1-Sqrt[y1^2-4])/2
		result.append(Times(C1D2, Plus(y1, Times(CN1, Sqrt(Plus(Power(y1, C2), Times(CN1, C4)))))));
		// (y2+Sqrt[y2^2-4])/2
		result.append(Times(C1D2, Plus(y2, Sqrt(Plus(Power(y2, C2), Times(CN1, C4))))));
		// (y2-Sqrt[y2^2-4])/2
		result.append(Times(C1D2, Plus(y2, Times(CN1, Sqrt(Plus(Power(y2, C2), Times(CN1, C4)))))));

		return createSet(result);
	}
}
