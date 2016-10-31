package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.m;
import static org.matheclipse.core.expression.F.n;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y_;

import org.matheclipse.core.builtin.function.DirectedInfinity;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArgMultiple;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.HashedOrderlessMatcher;

public class Times extends AbstractArgMultiple implements INumeric {
	/**
	 * Constructor for the singleton
	 */
	public final static Times CONST = new Times();

	private static HashedOrderlessMatcher ORDERLESS_MATCHER = new HashedOrderlessMatcher();

	@Override
	public HashedOrderlessMatcher getHashRuleMap() {
		return ORDERLESS_MATCHER;
	}

	public Times() {
	}

	@Override
	public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
		return c0.multiply(c1);
	}

	@Override
	public IExpr e2DblArg(final INum d0, final INum d1) {
		return d0.multiply(d1);
	}

	@Override
	public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
		return d0.multiply(d1);
	}

	@Override
	public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
		return f0.mul(f1);
	}

	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		return i0.multiply(i1);
	}

	@Override
	public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
		IExpr temp = F.NIL;

		if (o0.isZero()) {
			if (o1.isDirectedInfinity()) {
				return F.Indeterminate;
			}
			return F.C0;
		}

		if (o1.isZero()) {
			if (o0.isDirectedInfinity()) {
				return F.Indeterminate;
			}
			return F.C0;
		}

		if (o0.isOne()) {
			return o1;
		}

		if (o1.isOne()) {
			return o0;
		}

		if (o0.equals(o1)) {
			if (o0.isNumber()) {
				return o0.times(o0);
			}
			return o0.power(F.C2);
		}

		if (o0.isDirectedInfinity()) {
			temp = eInfinity((IAST) o0, o1);
		} else if (o1.isDirectedInfinity()) {
			temp = eInfinity((IAST) o1, o0);
		}
		if (temp.isPresent()) {
			return temp;
		}

		if (o0.isPower()) {
			// (x^a) * b
			final IAST power0 = (IAST) o0;
			IExpr power0Arg1 = power0.arg1();
			IExpr power0Arg2 = power0.arg2();
			if (power0.equalsAt(1, o1)) {
				// (x^a) * x
				if (power0Arg2.isInteger()) {
					return o1.power(power0Arg2.inc());
				} else if (!power0Arg2.isNumber()) {
					return o1.power(power0Arg2.inc());
				}
			}

			if (o1.isPower()) {
				final IAST power1 = (IAST) o1;
				IExpr power1Arg1 = power1.arg1();
				IExpr power1Arg2 = power1.arg2();
				temp = timesPowerPower(power0Arg1, power0Arg2, power1Arg1, power1Arg2);
				if (temp.isPresent()) {
					return temp;
				}
			}
		}

		if (o1.isPower()) {
			final IAST power1 = (IAST) o1;
			IExpr power1Arg1 = power1.arg1();
			IExpr power1Arg2 = power1.arg2();
			temp = timesArgPower(o0, power1Arg1, power1Arg2);
			if (temp.isPresent()) {
				return temp;
			}
		}

		if (o1.isPlus()) {
			final IAST f1 = (IAST) o1;
			// issue#128
			// if (o0.isMinusOne()) {
			// return f1.mapAt(F.Times(o0, null), 2);
			// }
			// if (o0.isInteger() && o1.isPlus() && o1.isAST2() && (((IAST)
			// o1).arg1().isNumericFunction())) {
			// // Note: this doesn't work for Together() function, if we allow
			// // o0 to be a fractional number
			// return f1.mapAt(F.Times(o0, null), 2);
			// }
		}
		if (o0.isInterval1()) {
			if (o1.isInterval1() || o1.isSignedNumber()) {
				return timesInterval(o0, o1);
			}
		}
		if (o1.isInterval1()) {
			if (o0.isInterval1() || o0.isSignedNumber()) {
				return timesInterval(o0, o1);
			}
		}
		return F.NIL;
	}

	/**
	 * Try simplifying <code>arg0 * ( power1Arg1 ^ power1Arg2 )</code>
	 * 
	 * @param arg0
	 * @param power1Arg1
	 * @param power1Arg2
	 * @return
	 */
	private IExpr timesArgPower(final IExpr arg0, IExpr power1Arg1, IExpr power1Arg2) {
		if (power1Arg1.equals(arg0)) {
			if (power1Arg2.isInteger()) {
				return arg0.power(power1Arg2.inc());
			} else if (!power1Arg2.isNumber()) {
				return arg0.power(power1Arg2.inc());
			}
			// } else if (arg0.isPlus() && power1Arg1.equals(arg0.negate())) {
			// // Issue#128
			// if (power1Arg2.isInteger()) {
			// return arg0.power(power1Arg2.inc()).negate();
			// } else if (!power1Arg2.isNumber()) {
			// return arg0.power(power1Arg2.inc()).negate();
			// }
		} else {
			if (power1Arg1.isInteger() && power1Arg2.isFraction()) {
				if (arg0.isFraction()) {
					// example: 1/9 * 3^(1/2) -> 1/3 * 3^(-1/2)

					// TODO implementation for complex numbers instead of
					// fractions
					IFraction f0 = (IFraction) arg0;
					IInteger pArg1 = (IInteger) power1Arg1;
					IFraction pArg2 = (IFraction) power1Arg2;
					if (pArg1.isPositive()) {
						if (pArg2.isPositive()) {
							IInteger denominatorF0 = (IInteger) f0.getDenominator();
							IInteger[] res = denominatorF0.divideAndRemainder(pArg1);
							if (res[1].isZero()) {
								return F.Times(F.fraction(f0.getNumerator(), res[0]), F.Power(pArg1, pArg2.negate()));
							}
						} else {
							IInteger numeratorF0 = (IInteger) f0.getNumerator();
							IInteger[] res = numeratorF0.divideAndRemainder(pArg1);
							if (res[1].isZero()) {
								return F.Times(F.fraction(res[0], f0.getDenominator()), F.Power(pArg1, pArg2.negate()));
							}
						}
					}
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Try simpplifying
	 * <code>(power0Arg1 ^ power0Arg2) * (power1Arg1 ^ power1Arg2)</code>
	 * 
	 * @param power0Arg1
	 * @param power0Arg2
	 * @param power1Arg1
	 * @param power1Arg2
	 * @return
	 */
	private IExpr timesPowerPower(IExpr power0Arg1, IExpr power0Arg2, IExpr power1Arg1, IExpr power1Arg2) {
		if (power0Arg2.isNumber()) {
			if (power1Arg2.isNumber()) {
				if (power0Arg1.equals(power1Arg1)) {
					// x^(a)*x^(b) => x ^(a+b)
					return power0Arg1.power(power0Arg2.plus(power1Arg2));
				}
				if (power0Arg2.equals(power1Arg2) && power0Arg1.isPositive() && power1Arg1.isPositive()
						&& power0Arg1.isSignedNumber() && power1Arg1.isSignedNumber()) {
					// a^(c)*b^(c) => (a*b) ^c
					return power0Arg1.times(power1Arg1).power(power0Arg2);
				}
				// if (power0Arg1.isPlus() && power1Arg1.isPlus() &&
				// power0Arg1.equals(power1Arg1.negate())) {// Issue#128
				// return
				// power0Arg1.power(power0Arg2.plus(power1Arg2)).times(CN1.power(power1Arg2));
				// }
			}
		}
		if (power0Arg1.equals(power1Arg1)) {
			// x^(a)*x^(b) => x ^(a+b)
			return power0Arg1.power(power0Arg2.plus(power1Arg2));
		}
		return F.NIL;
	}

	private IExpr timesInterval(final IExpr o0, final IExpr o1) {
		return F.Interval(F.List(
				F.Min(o0.lower().times(o1.lower()), o0.lower().times(o1.upper()), o0.upper().times(o1.lower()),
						o0.upper().times(o1.upper())),
				F.Max(o0.lower().times(o1.lower()), o0.lower().times(o1.upper()), o0.upper().times(o1.lower()),
						o0.upper().times(o1.upper()))));
	}

	private static IExpr eInfinity(IAST inf, IExpr o1) {
		if (inf.isComplexInfinity()) {
			if (o1.isZero()) {
				return F.Indeterminate;
			}
			return F.CComplexInfinity;
		}
		if (inf.isInfinity()) {
			if (o1.isInfinity()) {
				return F.CInfinity;
			}
			if (o1.isNegativeInfinity()) {
				return F.CNInfinity;
			}
			if (o1.isComplexInfinity()) {
				return F.CComplexInfinity;
			}
		}
		if (inf.isNegativeInfinity()) {
			if (o1.isInfinity()) {
				return F.CNInfinity;
			}
			if (o1.isNegativeInfinity()) {
				return F.CInfinity;
			}
			if (o1.isComplexInfinity()) {
				return F.CComplexInfinity;
			}
		}
		if (inf.isAST1()) {
			if (o1.isNumber() || o1.isSymbol()) {
				if (inf.isAST1()) {
					return DirectedInfinity.timesInf(inf, o1);
				}

			}
			if (o1.isDirectedInfinity() && o1.isAST1()) {
				return F.eval(F.DirectedInfinity(F.Times(inf.arg1(), ((IAST) o1).arg1())));
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
		return c0.multiply(F.complex(i1, F.C0));
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size == 1) {
			return F.C1;
		}
		if (size > 2) {
			IAST temp = evaluateHashs(ast);
			if (temp.isPresent()) {
				if (temp.isAST(F.Times, 2)) {
					return ((IAST) temp).arg1();
				}
				return temp;
			}
		}
		if (size == 3) {
			if ((ast.arg1().isNumeric() || ast.arg1().isOne() || ast.arg1().isMinusOne()) && ast.arg2().isPlus()) {
				// distribute the number over the sum:
				final IAST arg2 = (IAST) ast.arg2();
				return arg2.mapAt(F.Times(ast.arg1(), null), 2);
			}
			return binaryOperator(ast.arg1(), ast.arg2());
		}

		if (size > 3) {
			final ISymbol sym = ast.topHead();
			final IAST result = F.ast(sym);
			IExpr tres;
			IExpr temp = ast.arg1();
			boolean evaled = false;
			int i = 2;

			while (i < ast.size()) {

				tres = binaryOperator(temp, ast.get(i));

				if (!tres.isPresent()) {

					for (int j = i + 1; j < ast.size(); j++) {
						tres = binaryOperator(temp, ast.get(j));

						if (tres.isPresent()) {
							evaled = true;
							temp = tres;

							ast.remove(j);

							break;
						}
					}

					if (!tres.isPresent()) {
						result.append(temp);
						if (i == ast.size() - 1) {
							result.append(ast.get(i));
						} else {
							temp = ast.get(i);
						}
						i++;
					}

				} else {
					evaled = true;
					temp = tres;

					if (i == (ast.size() - 1)) {
						result.append(temp);
					}

					i++;
				}
			}

			if (evaled) {
				if (sym.hasOneIdentityAttribute() && result.size() > 1) {
					return result.getOneIdentity(F.C0);
				}

				return result;
			}
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(
				ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		// ORDERLESS_MATCHER.setUpHashRule("Log[x_]", "Log[y_]^(-1)",
		// Log.getFunction());
		ORDERLESS_MATCHER.defineHashRule(Log(x_), Power(Log(y_), CN1),
				org.matheclipse.core.reflection.system.Log.getFunction());
		// addTrigRules(F.Sin, F.Cot, F.Cos);
		// addTrigRules(F.Sin, F.Sec, F.Tan);
		// addTrigRules(F.Cos, F.Tan, F.Sin);
		// addTrigRules(F.Csc, F.Tan, F.Sec);
		// addTrigRules(F.Cos, F.Csc, F.Cot);
		ORDERLESS_MATCHER.defineHashRule(F.Sin(x_), F.Cot(x_), F.Cos(x));
		ORDERLESS_MATCHER.defineHashRule(F.Sin(x_), F.Sec(x_), F.Tan(x));
		ORDERLESS_MATCHER.defineHashRule(F.Cos(x_), F.Tan(x_), F.Sin(x));
		ORDERLESS_MATCHER.defineHashRule(F.Csc(x_), F.Tan(x_), F.Sec(x));
		ORDERLESS_MATCHER.defineHashRule(F.Cos(x_), F.Csc(x_), F.Cot(x));
		super.setUp(newSymbol);
	}

	private void addTrigRules(ISymbol head1, ISymbol head2, ISymbol resultHead) {
		IAST sinX_ = F.unaryAST1(head1, x_);
		IAST cotX_ = F.unaryAST1(head2, x_);
		IAST sinX = F.unaryAST1(head1, x);
		IAST cotX = F.unaryAST1(head2, x);
		IAST resultX = F.unaryAST1(resultHead, x);
		ORDERLESS_MATCHER.defineHashRule(sinX_, cotX_, resultX);
		ORDERLESS_MATCHER.defineHashRule(sinX_, F.Power(cotX_, $p(n, IntegerQ)),
				F.Times(F.Power(cotX, F.Subtract(n, F.C1)), resultX), F.Positive(n));
		ORDERLESS_MATCHER.defineHashRule(F.Power(sinX_, $p(m, IntegerQ)), cotX_,
				F.Times(F.Power(sinX, F.Subtract(m, F.C1)), resultX), F.Positive(m));
		ORDERLESS_MATCHER.defineHashRule(F.Power(sinX_, $p(m, IntegerQ)), F.Power(cotX_, $p(n, IntegerQ)),
				F.If(F.Greater(m, n), F.Times(F.Power(sinX, F.Subtract(m, n)), F.Power(resultX, n)),
						F.Times(F.Power(cotX, F.Subtract(n, m)), F.Power(resultX, m))),
				F.And(F.Positive(m), F.Positive(n)));
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		double result = 1;
		for (int i = top - size + 1; i < top + 1; i++) {
			result *= stack[i];
		}
		return result;
	}
}
