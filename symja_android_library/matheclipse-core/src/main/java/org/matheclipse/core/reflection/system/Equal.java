package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.generic.ITernaryComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>==</code> operator implementation.
 * 
 */
public class Equal extends AbstractFunctionEvaluator implements ITernaryComparator<IExpr> {

	public Equal() {
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
		IAST lhsClone = lhsAST.removeAt(1);
		return F.binary(originalHead, lhsClone, rhs);
	}

	/**
	 * Try to simplify a comparator expression. Example: <code>3*x > 6</code> wll be simplified to <code>x> 2</code>.
	 * 
	 * @param a1
	 *            left-hand-side of the comparator expression
	 * @param a2
	 *            right-hand-side of the comparator expression
	 * @param originalHead
	 *            symbol for which the simplification was started
	 * @return the simplified comparator expression or <code>null</code> if no simplification was found
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
			return null;
		}
		if (lhs.isAST()) {
			IAST lhsAST = (IAST) lhs;
			if (lhsAST.isTimes()) {
				if (lhsAST.arg1().isNumber()) {
					INumber sn = (INumber) lhsAST.arg1();
					rhs = F.eval(F.Divide(rhs, sn));
					return createComparatorResult(lhsAST, rhs, originalHead);
				}
			} else if (lhsAST.isPlus()) {
				if (lhsAST.arg1().isNumber()) {
					INumber sn = (INumber) lhsAST.arg1();
					rhs = F.eval(F.Subtract(rhs, sn));
					return createComparatorResult(lhsAST, rhs, originalHead);
				}
			}
		}
		return null;
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() > 1) {
			if (ast.size() == 3) {
				IExpr arg1 = ast.arg1();
				IExpr arg2 = ast.arg2();
				IExpr temp1 = F.evalExpandAll(arg1);
				IExpr temp2 = F.evalExpandAll(arg2);
				IExpr difference = F.eval(F.Subtract(temp1, temp2));
				if (difference.isNumber()) {
					if (difference.isZero()) {
						return F.True;
					}
					return F.False;
				}
				if (difference.isConstant()) {
					return F.False;
				}
				IExpr result = simplifyCompare(arg1, arg2, F.Equal);
				if (result != null) {
					return result;
				}
			}

			COMPARE_RESULT b = COMPARE_RESULT.UNDEFINED;
			boolean evaled = false;
			IAST result = ast.clone();
			int i = 2;
			while (i < result.size()) {
				b = compare(result.get(i - 1), result.get(i));
				if (b == COMPARE_RESULT.FALSE) {
					return F.False;
				}
				if (b == COMPARE_RESULT.TRUE) {
					evaled = true;
					result.remove(i - 1);
				} else {
					i++;
				}
			}
			if (evaled) {
				if (result.size() == 2) {
					return F.True;
				}
				return result;
			}

		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public COMPARE_RESULT compare(final IExpr o0, final IExpr o1) {
		if (o0.isSame(o1)) {
			return COMPARE_RESULT.TRUE;
		}

		if (o0.isConstant() && o1.isConstant()) {
			return COMPARE_RESULT.FALSE;
		}

		if (o0.isNumber() && o1.isNumber()) {
			return COMPARE_RESULT.FALSE;
		}

		if ((o0 instanceof StringX) && (o1 instanceof StringX)) {

			if (o0.isSymbol() || o1.isSymbol()) {
				return COMPARE_RESULT.UNDEFINED;
			}

			return COMPARE_RESULT.TRUE;
		}

		return COMPARE_RESULT.UNDEFINED;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT);
	}
}