package org.matheclipse.core.interfaces;

/**
 * 
 * Implemented by all number interfaces
 * 
 */
public interface INumber extends IExpr {
	/**
	 * Gets the signum value of a complex number
	 * 
	 * @return 0 for <code>this == 0</code>; +1 for <code>real(this) &gt; 0</code> or
	 *         <code>( real(this)==0 &amp;&amp; imaginary(this) &gt; 0 )</code>; -1 for
	 *         <code>real(this) &lt; 0 || ( real(this) == 0 &amp;&amp; imaginary(this) &lt; 0 )
	 */
	public int complexSign();

	/**
	 * Get the absolute value for a given number
	 * 
	 * @return
	 */
	public IExpr eabs();

	/**
	 * Compare the absolute value of this number with <code>1</code> and return
	 * <ul>
	 * <li><code>1</code>, if the absolute value is greater than 1</li>
	 * <li><code>0</code>, if the absolute value equals 1</li>
	 * <li><code>-1</code>, if the absolute value is less than 1</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int compareAbsValueToOne();

	/**
	 * Is this number equal an integer number?
	 * 
	 * @param i
	 * @return
	 */
	public boolean equalsInt(int i);

	public INumber opposite();

	/**
	 * Returns the imaginary part of a complex number
	 * 
	 * @return real part
	 */
	public ISignedNumber getIm();

	/**
	 * Returns the real part of a complex number
	 * 
	 * @return real part
	 */
	public ISignedNumber getRe();
}
