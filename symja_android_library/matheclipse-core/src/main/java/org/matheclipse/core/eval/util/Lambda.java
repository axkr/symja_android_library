package org.matheclipse.core.eval.util;

import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.expression.F;
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

	private static IExpr remove(IAST ast, Predicate<IExpr> predicate) {
		IAST result = F.NIL;
		int size = ast.size();
		int j = 0;
		for (int i = 0; i < size; i++) {
			IExpr temp = ast.get(i);
			if (predicate.test(temp)) {
				if (result == null) {
					result = ast.removeAtClone(i);
					continue;
				}
				result.remove(j);
				continue;
			}

			j++;
		}

		return result;
	}

	public static IExpr removeStop(IAST ast, Predicate<IExpr> predicate, Function<IExpr, IExpr> function) {
		IAST result = F.NIL;
		int size = ast.size();
		int j = 1;
		for (int i = 1; i < size; i++) {
			IExpr temp = ast.get(i);
			IExpr t = function.apply(temp);
			if (t != null) {
				return t;
			}
			if (predicate.test(temp)) {
				if (result == null) {
					result = ast.removeAtClone(i);
					continue;
				}
				result.remove(j);
				continue;
			}

			j++;
		}

		return result;
	}

	private static IExpr testMap(IAST ast, Predicate<IExpr> predicate, Function<IExpr, IExpr> function) {
		IAST result = F.NIL;
		int size = ast.size();
		for (int i = 0; i < size; i++) {
			IExpr temp = ast.get(i);
			if (predicate.test(temp)) {
				if (result == null) {
					result = ast.clone();
				}
				result.set(i, function.apply(temp));
				continue;
			}
		}

		return result;
	}

	private static IExpr testMap2(IAST list, Predicate<IExpr> predicate, Function<IExpr, IExpr> function1,
			Function<IExpr, IExpr> function2) {
		IAST result = F.NIL;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			IExpr temp = list.get(i);
			if (predicate.test(temp)) {
				if (result == null) {
					result = list.clone();
					for (int j = 0; j < i; j++) {
						result.set(j, function2.apply(temp));
					}
				}
				result.set(i, function1.apply(temp));
				continue;
			}
			if (result != null) {
				result.set(i, function2.apply(temp));
			}
		}

		return result;
	}
}
