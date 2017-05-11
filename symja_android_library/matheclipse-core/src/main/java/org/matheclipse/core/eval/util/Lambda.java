package org.matheclipse.core.eval.util;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorReplaceArgs;
import org.matheclipse.core.visit.VisitorReplaceSlots;

public class Lambda {
	private Lambda() {
	}

	/**
	 * <p>
	 * Replace all occurrences of Slot[&lt;index&gt;] expressions with the expression at the appropriate
	 * <code>index</code> in the given <code>slotsList</code>.
	 * </p>
	 * <p>
	 * <b>Note:</b> If a slot value is <code>null</code> the Slot will not be substituted.
	 * </p>
	 * 
	 * @param expr
	 * @param slotsList
	 *            the values for the slots.
	 * @return <code>F.NIL</code> if no substitution occurred.
	 */
	public static IExpr replaceSlots(IExpr expr, final IAST slotsList) {
		return expr.accept(new VisitorReplaceSlots(slotsList));
	}

	public static IExpr replaceSlotsOrElse(IExpr expr, final IAST slotsList, IExpr elseExpr) {
		IExpr temp = expr.accept(new VisitorReplaceSlots(slotsList));
		return temp.isPresent() ? temp : elseExpr;
	}

	/**
	 * <p>
	 * Replace all occurrences of the expressions in the given list with the appropriate <code>Slot(index)</code>.
	 * </p>
	 * 
	 * @param expr
	 * @param exprsList
	 *            the values for the slots.
	 * @return <code>F.NIL</code> if no substitution occurred.
	 */
	public static IExpr replaceArgs(IExpr expr, final IAST exprsList) {
		return expr.accept(new VisitorReplaceArgs(exprsList));
	}

}
