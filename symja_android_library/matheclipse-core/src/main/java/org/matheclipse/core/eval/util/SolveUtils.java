package org.matheclipse.core.eval.util;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class SolveUtils {

	/**
	 * <code>result[0]</code> is the list of expressions <code>== 0</code> . <code>result[1]</code>are the
	 * <code>Unequal, Less, LessEqual, Greater, GreaterEqual</code> expressions. If <code>result[2].isPresent()</code>
	 * return the entry as solution.
	 * 
	 * @param list
	 * @return
	 */
	public static IAST[] filterSolveLists(IAST list) {
		IASTAppendable[] result = new IASTAppendable[3];
		result[0] = F.ListAlloc(list.size());
		result[1] = F.ListAlloc(list.size());
		result[2] = F.NIL;
		int i = 1;
		while (i < list.size()) {
			IExpr arg = list.get(i);
			if (arg.isTrue()) {
			} else if (arg.isFalse()) {
				// no solution possible
				result[2] = F.List();
				return result;
			} else if (arg.isEqual()) {
				// arg must be Equal(_, 0)
				result[0].append(arg.getAt(1));
			} else {
				result[1].append(arg);
			}
			i++;
		}
		if (result[0].isEmpty() && result[1].isEmpty()) {
			result[2] = F.List(F.List());
			return result;
		}
		return result;
	}

}
