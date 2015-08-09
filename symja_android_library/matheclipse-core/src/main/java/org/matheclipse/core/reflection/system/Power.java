package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.fraction;

import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumberUtil;
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
import org.matheclipse.core.reflection.system.rules.PowerRules;

public class Power extends AbstractArg2 implements INumeric, PowerRules {
	/**
	 * Constructor for the singleton
	 */
	public final static Power CONST = new Power();

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public Power() {
	}

	@Override
	public IExpr e2ApcomplexArg(final ApcomplexNum ac0, final ApcomplexNum ac1) {
		return ac0.pow(ac1);
	}

	@Override
	public IExpr e2DblComArg(final IComplexNum c0, final IComplexNum c1) {
		return c0.pow(c1);
	}

	@Override
	public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
		return null;
	}

	@Override
	public IExpr e2ApfloatArg(final ApfloatNum af0, final ApfloatNum af1) {
		return af0.pow(af1);
	}

	@Override
	public IExpr e2DblArg(final INum d0, final INum d1) {
		if (d1.isMinusOne()) {
			return d0.inverse();
		}
		if (d1.isNumIntValue()) {
			return d0.pow(d1);
		}
		if (d0.isNegative()) {
			return F.complexNum(d0.doubleValue()).pow(F.complexNum(d1.doubleValue()));
		}
		return d0.pow(d1);
	}

	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		if (i0.isZero()) {
			// all other cases see e2ObjArg
			return null;
		}

		try {
			long n = i1.toLong();
			return i0.power(n);
		} catch (ArithmeticException ae) {

		}
		return null;
	}

	@Override
	public IExpr e2ObjArg(final IExpr arg1, final IExpr arg2) {
		if (arg1.equals(F.Indeterminate) || arg2.equals(F.Indeterminate)) {
			return F.Indeterminate;
		}

		if (arg2.isDirectedInfinity()) {
			if (arg2.isComplexInfinity()) {
				return F.Indeterminate;
			}

			if (arg1.isOne() || arg1.isMinusOne() || arg1.equals(F.CI) || arg1.equals(F.CNI)) {
				return F.Indeterminate;
			}
			IAST directedInfinity = (IAST) arg2;
			if (arg1.isZero()) {
				if (directedInfinity.isInfinity()) {
					// 0 ^ Inf
					return F.C0;
				}
				if (directedInfinity.isNegativeInfinity()) {
					// 0 ^ (-Inf)
					return F.CComplexInfinity;
				}
				return F.Indeterminate;
			}
			if (arg1.isInfinity()) {
				if (directedInfinity.isInfinity()) {
					// Inf ^ Inf
					return F.CComplexInfinity;
				}
				if (directedInfinity.isNegativeInfinity()) {
					// Inf ^ (-Inf)
					return F.C0;
				}
				return F.Indeterminate;
			}
			if (arg1.isNegativeInfinity()) {
				if (directedInfinity.isInfinity()) {
					// (-Inf) ^ Inf
					return F.CComplexInfinity;
				}
				if (directedInfinity.isNegativeInfinity()) {
					// (-Inf) ^ (-Inf)
					return F.C0;
				}
				return F.Indeterminate;
			}
			if (arg1.isComplexInfinity()) {
				if (directedInfinity.isInfinity()) {
					// ComplexInfinity ^ Inf
					return F.CComplexInfinity;
				}
				if (directedInfinity.isNegativeInfinity()) {
					// ComplexInfinity ^ (-Inf)
					return F.C0;
				}
				return F.Indeterminate;
			}
			if (arg1.isDirectedInfinity()) {
				if (directedInfinity.isInfinity()) {
					return F.CComplexInfinity;
				}
				if (directedInfinity.isNegativeInfinity()) {
					return F.C0;
				}
				return F.Indeterminate;
			}

			if (arg1.isNumber()) {
				IExpr temp = e2NumberDirectedInfinity((INumber) arg1, directedInfinity);
				if (temp != null) {
					return temp;
				}
			} else {
				IExpr a1 = F.evaln(arg1);
				if (a1.isNumber()) {
					IExpr temp = e2NumberDirectedInfinity((INumber) a1, directedInfinity);
					if (temp != null) {
						return temp;
					}
				}
			}
		}
		if (arg1.isDirectedInfinity()) {
			if (arg2.isZero()) {
				return F.Indeterminate;
			}
			if (arg1.isComplexInfinity()) {
				if (arg2.isSignedNumber()) {
					if (arg2.isNegative()) {
						return F.C0;
					}
					return F.CComplexInfinity;
				}
				return F.Indeterminate;
			}
			if (arg2.isOne()) {
				return arg1;
			}
		}
		if (arg1.isZero()) {
			EvalEngine ee = EvalEngine.get();
			if (arg2.isZero()) {
				// 0^0
				// TODO add a real log message
				// throw new DivisionByZero("0^0");
				ee.printMessage("Infinite expression 0^0");
				return F.Indeterminate;
			}

			IExpr a = F.eval(F.Re(arg2));
			if (a.isSignedNumber()) {
				if (((ISignedNumber) a).isNegative()) {
					ee.printMessage("Infinite expression 0^(negative number)");
					return F.CComplexInfinity;
				}
				return F.C0;
			}

			return null;
		}

		if (arg2.isZero()) {
			if (arg1.isInfinity() || arg1.isNegativeInfinity()) {
				return F.Indeterminate;
			}
			return F.C1;
		}

		if (arg2.isOne()) {
			return arg1;
		}

		if (arg1.isOne()) {
			return F.C1;
		}

		if (arg2.isSignedNumber()) {
			ISignedNumber is1 = (ISignedNumber) arg2;
			if (arg1.isInfinity()) {
				if (is1.isNegative()) {
					return F.C0;
				} else {
					return F.CInfinity;
				}
			} else if (arg1.isPower() && is1.isNumIntValue() && is1.isPositive()) {
				IAST a0 = (IAST) arg1;
				if (a0.arg2().isNumIntValue() && a0.arg2().isPositive()) {
					// (x*n)^m => x ^(n*m)
					return Power(a0.arg1(), is1.times(a0.arg2()));
				}
			} else if (arg1.isNegativeInfinity() && arg2.isInteger()) {
				IInteger ii = (IInteger) arg2;
				if (ii.isNegative()) {
					return F.C0;
				} else {
					if (ii.isOdd()) {
						return F.CNInfinity;
					} else {
						return F.CInfinity;
					}
				}
			}
			if (arg2.isMinusOne() || arg2.isInteger()) {
				if (arg1.isNumber()) {
					if (arg2.isMinusOne()) {
						return ((INumber) arg1).inverse();
					}
					try {
						long n = ((IInteger) arg2).toLong();
						return ((INumber) arg1).power(n);
					} catch (ArithmeticException ae) {

					}
				} else {
					IExpr o1negExpr = null;
					if (arg2.isInteger() && ((IInteger) arg2).isEven()) {
						o1negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1, true);
					} else {
						o1negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1, false);
					}
					if (o1negExpr != null) {
						if (arg2.isMinusOne()) {
							return Times(CN1, Power(o1negExpr, CN1));
						} else {
							IInteger ii = (IInteger) arg2;
							if (ii.isEven()) {
								return Power(o1negExpr, arg2);
							}
						}
					}
				}
			}
		}

		if (arg1.isSignedNumber() && ((ISignedNumber) arg1).isNegative() && arg2.equals(F.C1D2)) {
			// extract I for sqrt
			return F.Times(F.CI, F.Power(F.Negate(arg1), arg2));
		}

		if (arg1.isAST()) {
			IAST astArg1 = (IAST) arg1;
			if (astArg1.isTimes()) {
				if (arg2.isInteger()) {
					// (a * b * c)^n => a^n * b^n * c^n
					boolean doMap = false;
					if (arg2.isPositive()) {
						doMap = true;
					} else {
						for (int i = 1; i < astArg1.size(); i++) {
							if (astArg1.get(i).isNumber()) {
								doMap = true;
								break;
							} else if (astArg1.get(i).isPower()) {
								doMap = true;
								break;
							}
						}
					}
					if (doMap) {
						return astArg1.mapAt(Power(null, arg2), 1);
					}
				}
				if (arg2.isNumber()) {
					final IAST f0 = astArg1;

					if ((f0.size() > 1) && (f0.arg1().isNumber())) {
						return Times(Power(f0.arg1(), arg2), Power(F.ast(f0, F.Times, true, 2, f0.size()), arg2));
					}
				}
			} else if (astArg1.isPower()) {
				if (astArg1.arg2().isSignedNumber() && arg2.isSignedNumber()) {
					IExpr temp = astArg1.arg2().times(arg2);
					if (temp.isOne()) {
						if (astArg1.arg1().isNonNegativeResult()) {
							return astArg1.arg1();
						}
						if (astArg1.arg1().isRealResult()) {
							return F.Abs(astArg1.arg1());
						}
					}
				}
				if (arg2.isInteger()) {
					// (a ^ b )^n => a ^ (b * n)
					if (astArg1.arg2().isNumber()) {
						return F.Power(astArg1.arg1(), arg2.times(astArg1.arg2()));
					}
					return F.Power(astArg1.arg1(), F.Times(arg2, astArg1.arg2()));
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param arg1
	 *            a number
	 * @param arg2
	 *            must be a <code>DirectedInfinity[...]</code> expression
	 * @return
	 */
	private IExpr e2NumberDirectedInfinity(final INumber arg1, final IAST arg2) {
		int comp = arg1.compareAbsValueToOne();
		switch (comp) {
		case 1:
			// Abs(arg1) > 1
			if (arg2.isInfinity()) {
				// arg1 ^ Inf
				if (arg1.isSignedNumber() && arg1.isPositive()) {
					return F.CInfinity;
				}
				// complex or negative numbers
				return F.CComplexInfinity;
			}
			if (arg2.isNegativeInfinity()) {
				// arg1 ^ (-Inf)
				return F.C0;
			}
			break;
		case -1:
			// Abs(arg1) < 1
			if (arg2.isInfinity()) {
				// arg1 ^ Inf
				return F.C0;
			}
			if (arg2.isNegativeInfinity()) {
				// arg1 ^ (-Inf)
				if (arg1.isSignedNumber() && arg1.isPositive()) {
					return F.CInfinity;
				}
				// complex or negative numbers
				return F.CComplexInfinity;
			}
			break;
		}
		return null;
	}

	@Override
	public IExpr e2FraArg(IFraction f0, IFraction f1) {
		if (f0.getNumerator().isZero()) {
			return F.C0;
		}

		if (f1.getNumerator().isZero()) {
			return F.C1;
		}

		if (f1.equals(F.C1D2)) {
			if (f0.isNegative()) {
				return F.Times(F.CI, F.Power(f0.negate(), f1));
			}
		}

		if (f1.equals(F.CN1D2)) {
			if (f0.isNegative()) {
				return F.Times(F.CNI, F.Power(f0.negate().inverse(), f1.negate()));
			}
		}

		if (!f1.getDenominator().isOne()) {
			IInteger a;
			IInteger b;
			IFraction f0Temp = f0;
			if (f0.sign() < 0) {
				f0Temp = (IFraction) f0Temp.negate();
			}
			if (f1.isNegative()) {
				a = f0Temp.getDenominator();
				b = f0Temp.getNumerator();
			} else {
				a = f0Temp.getNumerator();
				b = f0Temp.getDenominator();
			}

			// example: (-27)^(2/3) or 8^(1/3)
			if (!f1.getNumerator().isOne()) {
				try {
					int exp = f1.getNumerator().toInt();
					if (exp < 0) {
						exp *= (-1);
					}
					a = a.pow(exp);
					b = b.pow(exp);
				} catch (ArithmeticException e) {
					return null;
				}
			}

			final IInteger root = f1.getDenominator();

			IInteger[] new_numer = calculateRoot(a, root);
			IInteger[] new_denom = calculateRoot(b, root);
			final IFraction new_root = F.fraction(C1, root);

			if (new_numer != null) {
				if (new_denom != null) {
					IRational p0 = null;
					if (new_denom[1].isOne()) {
						p0 = new_numer[1];
					} else {
						p0 = fraction(new_numer[1], new_denom[1]);
					}
					if (f0.sign() < 0) {
						return Times(fraction(new_numer[0], new_denom[0]), Power(p0.negate(), new_root));
					}
					return Times(fraction(new_numer[0], new_denom[0]), Power(p0, new_root));
				} else {
					if (a.isOne()) {
						return null;
					}
					IRational p0 = null;
					if (b.isOne()) {
						p0 = new_numer[1];
					} else {
						p0 = fraction(new_numer[1], b);
					}
					if (f0.sign() < 0) {
						return Times(new_numer[0], Power(p0.negate(), new_root));
					}
					return Times(new_numer[0], Power(p0, new_root));
				}
			} else {
				if (new_denom != null) {
					if (b.isOne()) {
						return null;
					}
					IRational p0 = null;
					if (new_denom[1].isOne()) {
						p0 = a;
					} else {
						p0 = F.fraction(a, new_denom[1]);
					}
					if (f0.sign() < 0) {
						return Times(fraction(C1, new_denom[0]), Power(p0.negate(), new_root));
					}
					return Times(fraction(C1, new_denom[0]), Power(p0, new_root));
				}
			}

			return null;
		}
		// now f1 denominator == 1
		int iNumer;
		try {
			iNumer = f1.getNumerator().toInt();
		} catch (ArithmeticException iob) {
			return null;
		}
		return f0.pow(iNumer);
	}

	/**
	 * Split this integer into the nth-root (with prime factors less equal 1021) and the &quot;rest factor&quot;
	 * 
	 * @return <code>{nth-root, rest factor}</code> or <code>null</code> if the root is not available
	 */
	private IInteger[] calculateRoot(IInteger a, IInteger root) {
		try {
			int n = root.toInt();
			if (n > 0) {
				if (a.isOne()) {
					return null;
				}
				if (a.isMinusOne()) {
					return null;
				}
				IInteger[] result = a.nthRootSplit(n);
				if (result[1].equals(a)) {
					// no roots found
					return null;
				}
				return result;
			}
		} catch (ArithmeticException e) {

		}
		return null;
	}

	@Override
	public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
		if (c0.isZero()) {
			return F.C0;
		}

		if (i1.isZero()) {
			return F.C1;
		}

		return c0.pow(i1.getBigNumerator().intValue());
	}

	@Override
	public IExpr eComFraArg(final IComplex c0, final IFraction i1) {
		if (i1.equals(F.C1D2) && c0.getRealPart().equals(BigFraction.ZERO)) {
			// square root of pure imaginary number
			BigFraction im = c0.getImaginaryPart();
			boolean negative = false;
			im = im.divide(BigInteger.valueOf(2L));
			if (NumberUtil.isNegative(im)) {
				im = im.negate();
				negative = true;
			}
			if (NumberUtil.isPerfectSquare(im)) {
				IExpr temp = F.Sqrt(F.fraction(im));
				if (negative) {
					// Sqrt(im.negate()) - I * Sqrt(im);
					return F.Plus(temp, F.Times(F.CNI, temp));
				}
				// Sqrt(im.negate()) + I * Sqrt(im);
				return F.Plus(temp, F.Times(F.CI, temp));
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.ONEIDENTITY | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 2) {
			throw new UnsupportedOperationException();
		}
		return Math.pow(stack[top - 1], stack[top]);
	}
}
