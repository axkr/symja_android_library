package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITernaryComparator;

/**
 * <code>==</code> operator implementation.
 * 
 */
public class Equal extends AbstractFunctionEvaluator implements ITernaryComparator {

	public final static Equal CONST = new Equal();

	public Equal() {
		// default ctor
	}

	/**
	 * Create the result for a <code>simplifyCompare()</code> step.
	 * 
	 * @param lhsAST
	 * @param rhs
	 * @param originalHead
	 * @return
	 */
	private IExpr createComparatorResult(IAST lhsAST, IExpr rhs, ISymbol originalHead) {
		IAST lhsClone = lhsAST.removeAtClone(1);
		return F.binary(originalHead, lhsClone, rhs);
	}

	/**
	 * Try to simplify a comparator expression. Example: <code>3*x > 6</code>
	 * wll be simplified to <code>x> 2</code>.
	 * 
	 * @param a1
	 *            left-hand-side of the comparator expression
	 * @param a2
	 *            right-hand-side of the comparator expression
	 * @param originalHead
	 *            symbol for which the simplification was started
	 * @return the simplified comparator expression or <code>null</code> if no
	 *         simplification was found
	 */
	protected IExpr simplifyCompare(IExpr a1, IExpr a2, ISymbol originalHead) {
		IExpr lhs, rhs;
		if (a2.isNumber()) {
			lhs = a1;
			rhs = a2;
		} else if (a1.isNumber()) {
			lhs = a2;
			rhs = a1;
		} else {
			return F.NIL;
		}
		if (lhs.isAST()) {
			IAST lhsAST = (IAST) lhs;
			if (lhsAST.isTimes()) {
				if (lhsAST.arg1().isNumber()) {
					INumber sn = (INumber) lhsAST.arg1();
					rhs = F.eval(F.Divide(rhs, sn));
					return createComparatorResult(lhsAST, rhs, originalHead);
				}
			} else if (lhsAST.isPlus() && lhsAST.arg1().isNumber()) {
				INumber sn = (INumber) lhsAST.arg1();
				rhs = F.eval(F.Subtract(rhs, sn));
				return createComparatorResult(lhsAST, rhs, originalHead);
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() > 1) {
			IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDEFINED;
			if (ast.isAST2()) {
				return equalNull(ast.arg1(), ast.arg2());
			}
			boolean evaled = false;
			IAST result = ast.clone();
			int i = 2;
			IExpr arg1 = F.expandAll(result.get(1), true, true);
			while (i < result.size()) {
				IExpr arg2 = F.expandAll(result.get(i), true, true);
				b = prepareCompare(arg1, arg2);
				if (b == IExpr.COMPARE_TERNARY.FALSE) {
					return F.False;
				}
				if (b == IExpr.COMPARE_TERNARY.TRUE) {
					evaled = true;
					result.remove(i - 1);
				} else {
					result.set(i - 1, arg1);
					i++;
					arg1 = arg2;
				}

			}
			if (evaled) {
				if (result.isAST1()) {
					return F.True;
				}
				return result;
			}

		}
		return F.NIL;
	}

	/**
	 * Compare if the first and second argument are equal.
	 * 
	 * @param a1
	 *            first argument
	 * @param a2
	 *            second argument
	 * @return <code>F.NIL</code> or the simplified expression, if equality
	 *         couldn't be determined.
	 */
	public static IExpr equalNull(final IExpr a1, final IExpr a2) {
		IExpr.COMPARE_TERNARY b;
		IExpr arg1 = F.expandAll(a1, true, true);
		IExpr arg2 = F.expandAll(a2, true, true);

		b = CONST.prepareCompare(arg1, arg2);
		if (b == IExpr.COMPARE_TERNARY.FALSE) {
			return F.False;
		}
		if (b == IExpr.COMPARE_TERNARY.TRUE) {
			return F.True;
		}

		return CONST.simplifyCompare(arg1, arg2, F.Equal);
	}

	public static IExpr equals(final IAST ast) {
		return equalNull(ast.arg1(), ast.arg2()).orElse(ast);
	}

	/**
	 * 
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public IExpr.COMPARE_TERNARY prepareCompare(final IExpr arg1, final IExpr arg2) {
		if (arg1.isIndeterminate() || arg2.isIndeterminate()) {
			return IExpr.COMPARE_TERNARY.FALSE;
		}
		if (arg1.isList() && arg2.isList()) {
			IAST list1 = (IAST) arg1;
			IAST list2 = (IAST) arg2;
			int size1 = list1.size();
			if (size1 != list2.size()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}
			IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.TRUE;
			for (int i = 1; i < size1; i++) {
				b = prepareCompare(list1.get(i), list2.get(i));
				if (b == IExpr.COMPARE_TERNARY.FALSE) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
				if (b == IExpr.COMPARE_TERNARY.TRUE) {
				} else {
					return IExpr.COMPARE_TERNARY.UNDEFINED;
				}
			}
		}
		IExpr a0 = arg1;
		IExpr a1 = arg2;
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
	public IExpr.COMPARE_TERNARY compareTernary(final IExpr o0, final IExpr o1) {
		if (o0.isSame(o1)) {
			return IExpr.COMPARE_TERNARY.TRUE;
		}

		if (o0.isConstant() && o1.isConstant()) {
			return IExpr.COMPARE_TERNARY.FALSE;
		}

		if (o0.isNumber() && o1.isNumber()) {
			return IExpr.COMPARE_TERNARY.FALSE;
		}

		if ((o0 instanceof StringX) && (o1 instanceof StringX)) {
			return IExpr.COMPARE_TERNARY.FALSE;
		}

		IExpr difference = F.eval(F.Subtract(o0, o1));
		if (difference.isNumber()) {
			if (difference.isZero()) {
				return IExpr.COMPARE_TERNARY.TRUE;
			}
			return IExpr.COMPARE_TERNARY.FALSE;
		}
		if (difference.isConstant()) {
			return IExpr.COMPARE_TERNARY.FALSE;
		}

		return IExpr.COMPARE_TERNARY.UNDEFINED;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.FLAT);
	}
}