package org.matheclipse.core.interfaces;

public interface ITernaryComparator {

	/**
	 * <ul>
	 * <li>Return TRUE if the comparison is <code>true</code></li>
	 * <li>Return FALSE if the comparison is <code>false</code></li>
	 * <li>Return UNDEFINED if the comparison is undetermined (i.e. could not be evaluated)</li>
	 * </ul>
	 * 
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	IExpr.COMPARE_TERNARY compareTernary(IExpr arg1, IExpr arg2);

}
