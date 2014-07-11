package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Equal extends AbstractFunctionEvaluator {

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
		IAST lhsClone = lhsAST.clone();
		lhsClone.remove(1);
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
				IExpr result = simplifyCompare(ast.arg1(), ast.arg2(), F.Equal);
				if (result != null) {
					return result;
				}
			}

			int b = 0;
			boolean evaled = false;
			IAST result = ast.clone();
			int i = 2;
			while (i < result.size()) {
				b = compare(result.get(i - 1), result.get(i));
				if (b == (-1)) {
					return F.False;
				}
				if (b == 1) {
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

	/**
	 * 1->True -1 ->False 0->Otherwise
	 * 
	 * @param o0
	 * @param o1
	 * @return
	 */
	public int compare(final IExpr o0, final IExpr o1) {
		if (o0.isSame(o1)) {
			return 1;
		}

		if (o0.isConstant() && o1.isConstant()) {
			return -1;
		}

		if (o0.isNumber() && o1.isNumber()) {
			return -1;
		}

		if ((o0 instanceof StringX) && (o1 instanceof StringX)) {

			if (o0.isSymbol() || o1.isSymbol()) {
				return 0;
			}

			return 1;
		}

		return 0;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT);
	}
}