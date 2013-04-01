package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IExpr;

public interface ITernaryComparator<T extends IExpr> {
	public static enum COMPARE_RESULT {
		TRUE, FALSE, UNDEFINED
	};
	/**
	 * <ul>
	 * <li>Return TRUE if the comparison is <code>true</code> </li>
	 * <li>Return FALSE if the comparison is <code>false</code> </li>
	 * <li>Return UNDEFINED if the comparison is undetermined (i.e. could not be
	 * evaluated)</li>
	 * </ul>
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	COMPARE_RESULT compare(T o1, T o2);

}
