package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITernaryComparator;

/**
 * <code>&gt;</code> operator implementation.
 * 
 */
public class Greater extends AbstractFunctionEvaluator implements ITernaryComparator {
	public final static Greater CONST = new Greater();

	public Greater() {
		// default ctor
	}

	/**
	 * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code>
	 * will be simplified to <code>x &gt; 2</code>.
	 * 
	 * @param a1
	 *            left-hand-side of the comparator expression
	 * @param a2
	 *            right-hand-side of the comparator expression
	 * @return the simplified comparator expression or <code>null</code> if no
	 *         simplification was found
	 */
	protected IAST simplifyCompare(IExpr a1, IExpr a2) {
		return simplifyCompare(a1, a2, F.Greater, F.Less);
	}

	/**
	 * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code>
	 * wll be simplified to <code>x &gt; 2</code>.
	 * 
	 * @param a1
	 *            left-hand-side of the comparator expression
	 * @param a2
	 *            right-hand-side of the comparator expression
	 * @param originalHead
	 *            symbol of the comparator operator for which the simplification
	 *            was started
	 * @param oppositeHead
	 *            opposite of the symbol of the comparator operator for which
	 *            the comparison was started
	 * @return the simplified comparator expression or <code>F.NIL</code> if no
	 *         simplification was found
	 */
	final protected IAST simplifyCompare(IExpr a1, IExpr a2, ISymbol originalHead, ISymbol oppositeHead) {
		IExpr lhs;
		IExpr rhs;
		boolean useOppositeHeader = false;
		if (a2.isNumericFunction()) {
			lhs = a1;
			rhs = a2;
		} else if (a1.isNumericFunction()) {
			lhs = a2;
			rhs = a1;
			useOppositeHeader = true;
		} else {
			lhs = F.eval(F.Subtract(a1, a2));
			rhs = F.C0;
		}
		if (lhs.isAST()) {
			IAST lhsAST = (IAST) lhs;
			if (lhsAST.isTimes()) {
				IAST[] result = lhsAST.filter(Predicates.isNumericFunction());
				if (result[0].size() > 1) {
					IExpr temp = result[0].getOneIdentity(F.C0);
					if (temp.isNegative()) {
						useOppositeHeader = !useOppositeHeader;
					}
					rhs = rhs.divide(temp);
					return createComparatorResult(result[1].getOneIdentity(F.C0), rhs, useOppositeHeader, originalHead,
							oppositeHead);
				}
			} else if (lhsAST.isPlus()) {
				IAST[] result = lhsAST.filter(Predicates.isNumericFunction());
				if (result[0].size() > 1) {
					rhs = rhs.subtract(result[0].getOneIdentity(F.C0));
					return createComparatorResult(result[1].getOneIdentity(F.C0), rhs, useOppositeHeader, originalHead,
							oppositeHead);
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Create the result for a <code>simplifyCompare()</code> step.
	 * 
	 * @param lhs
	 *            left-hand-side of the comparator expression
	 * @param rhs
	 *            right-hand-side of the comparator expression
	 * @param useOppositeHeader
	 *            use the opposite header to create the result
	 * @param originalHead
	 *            symbol of the comparator operator for which the simplification
	 *            was started
	 * @param oppositeHead
	 *            opposite of the symbol of the comparator operator for which
	 *            the comparison was started
	 * @return
	 */
	private IAST createComparatorResult(IExpr lhs, IExpr rhs, boolean useOppositeHeader, ISymbol originalHead,
			ISymbol oppositeHead) {
		return F.binary(useOppositeHeader ? oppositeHead : originalHead, lhs, rhs);
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		if (ast.isAST2()) {
			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			IExpr result = simplifyCompare(arg1, arg2);
			if (result.isPresent()) {
				// the result may return an AST with an "opposite header" (i.e.
				// Less instead of Greater)
				return result;
			}
			if (arg2.isSignedNumber()) {
				// this part is used in other comparator operations like Less,
				// GreaterEqual,...
				IExpr temp = checkAssumptions(arg1, arg2);
				if (temp.isPresent()) {
					return temp;
				}
			}
		}
		IExpr.COMPARE_TERNARY b;
		boolean evaled = false;
		IAST result = ast.clone();
		IExpr.COMPARE_TERNARY[] cResult = new IExpr.COMPARE_TERNARY[ast.size()];
		cResult[0] = IExpr.COMPARE_TERNARY.TRUE;
		for (int i = 1; i < ast.size() - 1; i++) {
			b = prepareCompare(result.get(i), result.get(i + 1));
			if (b == IExpr.COMPARE_TERNARY.FALSE) {
				return F.False;
			}
			if (b == IExpr.COMPARE_TERNARY.TRUE) {
				evaled = true;
			}
			cResult[i] = b;
		}
		cResult[ast.size() - 1] = IExpr.COMPARE_TERNARY.TRUE;
		if (!evaled) {
			// expression doesn't change
			return F.NIL;
		}
		int i = 2;
		evaled = false;
		for (int j = 1; j < ast.size(); j++) {
			if (cResult[j - 1] == IExpr.COMPARE_TERNARY.TRUE && cResult[j] == IExpr.COMPARE_TERNARY.TRUE) {
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

		return F.NIL;
	}

	/**
	 * Check assumptions for the comparison operator. Will be overridden in
	 * <code>GreaterEqual, Less, LessEqual</code>.
	 * 
	 * @param arg1
	 *            the left-hand-side of the comparison
	 * @param arg2
	 *            the right-hand-side of the comparison
	 * @return
	 */
	protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
		if (arg2.isNegative()) {
			// arg1 > "negative number"
			if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
				return F.True;
			}
		} else if (arg2.isZero()) {
			// arg1 > 0
			if (arg1.isPositiveResult()) {
				return F.True;
			}
			if (arg1.isNegativeResult()) {
				return F.False;
			}
		} else {
			// arg1 > "positive number" > 0
			if (arg1.isNegativeResult()) {
				return F.False;
			}
		}
		return F.NIL;
	}

	public IExpr.COMPARE_TERNARY prepareCompare(final IExpr o0, final IExpr o1) {
		IExpr a0 = o0;
		IExpr a1 = o1;
		if (!a0.isSignedNumber() && a0.isNumericFunction()) {
			a0 = F.evaln(a0);
		} else if (a1.isNumeric() && a0.isRational()) {
			a0 = F.evaln(a0);
		}
		if (!a1.isSignedNumber() && a1.isNumericFunction()) {
			a1 = F.evaln(a1);
		} else if (a0.isNumeric() && a1.isRational()) {
			a1 = F.evaln(a1);
		}

		return compareTernary(a0, a1);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
		// don't compare strings
		if (a0.isSignedNumber()) {
			if (a1.isSignedNumber()) {
				return a1.isLTOrdered(a0) ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
			} else if (a1.isInfinity()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			} else if (a1.isNegativeInfinity()) {
				return IExpr.COMPARE_TERNARY.TRUE;
			}
		} else if (a1.isSignedNumber()) {
			if (a0.isInfinity()) {
				return IExpr.COMPARE_TERNARY.TRUE;
			} else if (a0.isNegativeInfinity()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}
		} else if (a0.isInfinity() && a1.isNegativeInfinity()) {
			return IExpr.COMPARE_TERNARY.TRUE;
		} else if (a0.isNegativeInfinity() && a1.isInfinity()) {
			return IExpr.COMPARE_TERNARY.FALSE;
		}
		if (a0.equals(a1)) {
			return IExpr.COMPARE_TERNARY.FALSE;
		}
		return IExpr.COMPARE_TERNARY.UNDEFINED;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT);
	}
}
