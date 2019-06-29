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

	public int indexOf(final int ch);

	public int indexOf(final int ch, final int fromIndex);

	public int indexOf(final String str);

	public int indexOf(final String str, final int fromIndex);

	public int lastIndexOf(final int ch);

	public int lastIndexOf(final int ch, final int fromIndex);

	public int lastIndexOf(final String str);

	public int lastIndexOf(final String str, final int fromIndex);

	public String substring(final int beginIndex);

	public String substring(final int beginIndex, final int endIndex);
}
