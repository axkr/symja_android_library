package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.function.NumericQ;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ITernaryComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Greater extends AbstractFunctionEvaluator implements ITernaryComparator<IExpr> {
	public final static Greater CONST = new Greater();

	public Greater() {
	}

	/**
	 * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code> wll be simplified to <code>x &gt; 2</code>.
	 * 
	 * @param a1
	 *            left-hand-side of the comparator expression
	 * @param a2
	 *            right-hand-side of the comparator expression
	 * @return the simplified comparator expression or <code>null</code> if no simplification was found
	 */
	protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
		return simplifyCompare(a1, a2, F.Greater, F.Less);
	}

	/**
	 * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code> wll be simplified to <code>x &gt; 2</code>.
	 * 
	 * @param a1
	 *            left-hand-side of the comparator expression
	 * @param a2
	 *            right-hand-side of the comparator expression
	 * @param originalHead
	 *            symbol for which the simplification was started
	 * @param oppositeHead
	 *            opposite of the symbol for which the comparison was started
	 * @return the simplified comparator expression or <code>null</code> if no simplification was found
	 */
	final protected IExpr simplifyCompare(IExpr a1, IExpr a2, ISymbol originalHead, ISymbol oppositeHead) {
		IExpr lhs;
		ISignedNumber rhs;
		boolean useOppositeHeader = false;
		if (a2.isSignedNumber()) {
			lhs = a1;
			rhs = (ISignedNumber) a2;
		} else if (a1.isSignedNumber()) {
			lhs = a2;
			rhs = (ISignedNumber) a1;
			useOppositeHeader = true;
		} else {
			lhs = F.eval(F.Subtract(a1, a2));
			rhs = F.C0;
		}
		if (lhs.isAST()) {
			IAST lhsAST = (IAST) lhs;
			if (lhsAST.isTimes()) {
				if (lhsAST.arg1().isSignedNumber()) {
					ISignedNumber sn = (ISignedNumber) lhsAST.arg1();
					if (sn.isNegative()) {
						useOppositeHeader = !useOppositeHeader;
					}
					rhs = rhs.divideBy(sn);
					return createComparatorResult(lhsAST, rhs, useOppositeHeader, originalHead, oppositeHead);
				}
			} else if (lhsAST.isPlus()) {
				if (lhsAST.arg1().isSignedNumber()) {
					ISignedNumber sn = (ISignedNumber) lhsAST.arg1();
					rhs = rhs.subtractFrom(sn);
					return createComparatorResult(lhsAST, rhs, useOppositeHeader, originalHead, oppositeHead);
				}
			}
		}
		return null;
	}

	/**
	 * Create the result for a <code>simplifyCompare()</code> step.
	 * 
	 * @param lhsAST
	 * @param rhs
	 * @param useOppositeHeader
	 * @param originalHead
	 * @param oppositeHead
	 * @return
	 */
	private IExpr createComparatorResult(IAST lhsAST, IExpr rhs, boolean useOppositeHeader, ISymbol originalHead,
			ISymbol oppositeHead) {
		IAST lhsClone = lhsAST.clone();
		lhsClone.remove(1);
		if (useOppositeHeader) {
			return F.binary(oppositeHead, lhsClone, rhs);
		} else {
			return F.binary(originalHead, lhsClone, rhs);
		}
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		if (ast.size() == 3) {
			IExpr result = simplifyCompare(ast.arg1(), ast.arg2());
			if (result != null) {
				return result;
			}
		}
		COMPARE_RESULT b = COMPARE_RESULT.UNDEFINED;
		boolean evaled = false;
		IAST result = ast.clone();
		COMPARE_RESULT[] cResult = new COMPARE_RESULT[ast.size()];
		cResult[0] = COMPARE_RESULT.TRUE;
		for (int i = 1; i < ast.size() - 1; i++) {
			b = prepareCompare(result.get(i), result.get(i + 1));
			if (b == COMPARE_RESULT.FALSE) {
				return F.False;
			}
			if (b == COMPARE_RESULT.TRUE) {
				evaled = true;
			}
			cResult[i] = b;
		}
		cResult[ast.size() - 1] = COMPARE_RESULT.TRUE;
		if (!evaled) {
			// expression doesn't change
			return null;
		}
		int i = 2;
		evaled = false;
		for (int j = 1; j < ast.size(); j++) {
			if (cResult[j - 1] == COMPARE_RESULT.TRUE && cResult[j] == COMPARE_RESULT.TRUE) {
				evaled = true;
				result.remove(i - 1);
			} else {
				i++;
			}
		}

		if (evaled) {
			if (result.size() <= 2) {
				return F.True;
			}
			return result;
		}

		return null;
	}

	public COMPARE_RESULT prepareCompare(final IExpr o0, final IExpr o1) {
		// don't compare strings
		IExpr a0 = o0;
		IExpr a1 = o1;
		if (!(a0.isSignedNumber()) && NumericQ.CONST.apply(a0)) {
			a0 = F.evaln(a0);
		}
		if (!(a1.isSignedNumber()) && NumericQ.CONST.apply(a1)) {
			a1 = F.evaln(a1);
		}
		if (a0.isNumeric() && a1.isRational()) {
			a1 = F.evaln(a1);
		}
		if (a1.isNumeric() && a0.isRational()) {
			a0 = F.evaln(a0);
		}
		return compareGreater(a0, a1);
	}

	public COMPARE_RESULT compareGreater(final IExpr a0, final IExpr a1) {
		// don't compare strings
		if (a0.isSignedNumber()) {
			if (a1.isSignedNumber()) {
				return (a1.isLTOrdered(a0)) ? COMPARE_RESULT.TRUE : COMPARE_RESULT.FALSE;
			} else if (a1.isInfinity()) {
				return COMPARE_RESULT.FALSE;
			} else if (a1.isNegativeInfinity()) {
				return COMPARE_RESULT.TRUE;
			}
		} else if (a1.isSignedNumber()) {
			if (a0.isInfinity()) {
				return COMPARE_RESULT.TRUE;
			} else if (a0.isNegativeInfinity()) {
				return COMPARE_RESULT.FALSE;
			}
		} else if (a0.isInfinity() && a1.isNegativeInfinity()) {
			return COMPARE_RESULT.TRUE;
		} else if (a0.isNegativeInfinity() && a1.isInfinity()) {
			return COMPARE_RESULT.FALSE;
		}
		return COMPARE_RESULT.UNDEFINED;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT);
	}
}
