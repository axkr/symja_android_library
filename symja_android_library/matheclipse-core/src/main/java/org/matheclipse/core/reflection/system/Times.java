package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.y;

import org.matheclipse.core.builtin.function.DirectedInfinity;
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
		return f0.multiply(f1);
	}

	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		return i0.multiply(i1);
	}

	@Override
	public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
		IExpr temp = null;
		if (o0.equals(F.Indeterminate) || o1.equals(F.Indeterminate)) {
			return F.Indeterminate;
		}

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
			return F.Power(o0, F.C2);
		}

		if (o0.isDirectedInfinity()) {
			temp = eInfinity((IAST) o0, o1);
		} else if (o1.isDirectedInfinity()) {
			temp = eInfinity((IAST) o1, o0);
		}
		if (temp != null) {
			return temp;
		}

		if (o0.isSymbol()) {
			final ISymbol s0 = (ISymbol) o0;
			if (o1.isPower()) {
				final IAST f1 = (IAST) o1;
				if (f1.arg2().isNumber()) {
					if (s0.equals(f1.arg1())) {
						// x*x^(b) => x ^ (1+b)
						return Power(s0, Plus(F.C1, f1.arg2()));
					}
				}
			}
		}

		if (o0.isPower()) {
			final IAST f0 = (IAST) o0;
			if (f0.arg2().isNumber()) {
				if (f0.equalsAt(1, o1)) {
					return Power(o1, Plus(F.C1, f0.arg2()));
				}

				if (o1.isPower()) {
					final IAST f1 = (IAST) o1;

					if (f1.arg2().isNumber()) {
						if (f0.equalsAt(1, f1.arg1())) {
							// x^(a)*x^(b) => x ^(a+b)
							return Power(f0.arg1(), Plus(f0.arg2(), f1.arg2()));
						}
						if (f0.equalsAt(2, f1.arg2()) && f0.arg1().isPositive() && f1.arg1().isPositive()
								&& f0.arg1().isSignedNumber() && f1.arg1().isSignedNumber()) {
							// a^(c)*b^(c) => (a*b) ^c
							return Power(Times(f0.arg1(), f1.arg1()), f0.arg2());
						}
					}
				}
			}
		}

		if (o1.isPower() && (((IAST) o1).arg2().isInteger())) {
			final IAST f1 = (IAST) o1;

			if (f1.equalsAt(1, o0)) {
				return Power(o0, Plus(F.C1, f1.arg2()));
			}
		}

		if (o1.isPlus()) {
			final IAST f1 = (IAST) o1;
//			if (o0.isMinusOne()) {
//				return f1.mapAt(F.Times(o0, null), 2);
//			}
			if (o0.isInteger() && o1.isPlus() && (((IAST) o1).size() == 3) && (((IAST) o1).arg1().isNumericFunction())) {
				// Note: this doesn't work for Together() function, if we allow o0 to be a fractional number
				return f1.mapAt(F.Times(o0, null), 2);
			}
		}
		return null;
	}

	public static IExpr eInfinity(IAST inf, IExpr o1) {
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
		if (inf.size() == 2) {
			if (o1.isNumber() || o1.isSymbol()) {
				if (inf.size() == 2) {
					return DirectedInfinity.timesInf(inf, o1);
					// return F.eval(F.DirectedInfinity(F.Times(inf.arg1(), F.Divide(o1, o1.abs()))));
				}

			}
			if (o1.isDirectedInfinity() && ((IAST) o1).size() == 2) {
				return F.eval(F.DirectedInfinity(F.Times(inf.arg1(), ((IAST) o1).arg1())));
			}
		}
		// if (inf.equals(F.CInfinity)) {
		// if (o1.equals(F.CInfinity)) {
		// return F.CInfinity;
		// } else if (o1.equals(F.CNInfinity)) {
		// return F.CNInfinity;
		// } else if (o1.isSignedNumber()) {
		// if (((ISignedNumber) o1).isNegative()) {
		// return F.CNInfinity;
		// } else {
		// return F.CInfinity;
		// }
		// } else if (o1.isComplex()) {
		// return F.$(F.DirectedInfinity, o1);
		// }
		// } else if (inf.equals(F.CNInfinity)) {
		// if (o1.equals(F.CInfinity)) {
		// return F.CNInfinity;
		// } else if (o1.equals(F.CNInfinity)) {
		// return F.CInfinity;
		// } else if (o1.isSignedNumber()) {
		// if (((ISignedNumber) o1).isNegative()) {
		// return F.CInfinity;
		// } else {
		// return F.CNInfinity;
		// }
		// }
		// }
		return null;
	}

	@Override
	public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
		return c0.multiply(F.complex(i1, F.C0));
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() > 2) {
			IAST temp = evaluateHashs(ast);
			if (temp != null) {
				return temp;
			}
		}
		if (ast.size() == 3) {
			if ((ast.arg1().isNumeric() || ast.arg1().isOne() || ast.arg1().isMinusOne()) && ast.arg2().isPlus()) {
				// distribute the number over the sum:
				final IAST arg2 = (IAST) ast.arg2();
				return arg2.mapAt(F.Times(ast.arg1(), null), 2);
			}
			return binaryOperator(ast.arg1(), ast.arg2());
		}

		if (ast.size() > 3) {
			final ISymbol sym = ast.topHead();
			final IAST result = F.ast(sym);
			IExpr tres;
			IExpr temp = ast.arg1();
			boolean evaled = false;
			int i = 2;

			while (i < ast.size()) {

				tres = binaryOperator(temp, ast.get(i));

				if (tres == null) {

					for (int j = i + 1; j < ast.size(); j++) {
						tres = binaryOperator(temp, ast.get(j));

						if (tres != null) {
							evaled = true;
							temp = tres;

							ast.remove(j);

							break;
						}
					}

					if (tres == null) {
						result.add(temp);
						if (i == ast.size() - 1) {
							result.add(ast.get(i));
						} else {
							temp = ast.get(i);
						}
						i++;
					}

				} else {
					evaled = true;
					temp = tres;

					if (i == (ast.size() - 1)) {
						result.add(temp);
					}

					i++;
				}
			}

			if (evaled) {
				if ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY && result.size() > 1) {
					return result.getOneIdentity(F.C0);
				}

				return result;
			}
		}

		return null;
	}

	public static IExpr evalTimesNumbers(IAST ast) {
		IAST result = F.Times();
		IExpr num;
		if (!ast.arg1().isNumber()) {
			return null;
		}
		if (!ast.arg2().isNumber()) {
			return null;
		}
		num = ast.arg1().times(ast.arg2());
		result.add(num);
		for (int i = 3; i < ast.size(); i++) {
			if (num.isNumber() && ast.get(i).isNumber()) {
				num = num.times(ast.get(i));
			} else {
				result.addAll(ast, i, ast.size());
				result.set(1, num);
				return result;
			}
		}
		return num;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		// ORDERLESS_MATCHER.setUpHashRule("Log[x_]", "Log[y_]^(-1)", Log.getFunction());
		ORDERLESS_MATCHER.defineHashRule(Log($p(x)), Power(Log($p(y)), CN1),
				org.matheclipse.core.reflection.system.Log.getFunction());

		super.setUp(symbol);
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		double result = 1;
		for (int i = top - size + 1; i < top + 1; i++) {
			result *= stack[i];
		}
		return result;
	}
}
