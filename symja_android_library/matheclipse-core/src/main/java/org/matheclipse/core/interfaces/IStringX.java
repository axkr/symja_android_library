package org.matheclipse.core.interfaces;

/**
 * (I)nterface for a (String) e(X)pression
 * 
 */
public interface IStringX extends IExpr {
	/**
	 * Test if this string equals the given character sequence.
	 * 
	 * @param cs
	 * @return
	 */
	public boolean contentEquals(final CharSequence cs);
}
