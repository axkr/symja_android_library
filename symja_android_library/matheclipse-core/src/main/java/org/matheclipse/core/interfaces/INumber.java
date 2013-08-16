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
	 * @return 0 for <code>this == 0</code>; +1 for
	 *         <code>real(this) &gt; 0</code> or
	 *         <code>( real(this)==0 &amp;&amp; imaginary(this) &gt; 0 )</code>;
	 *         -1 for
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
	 * Is this number equal an integer number?
	 * @param i
	 * @return
	 */
	public boolean equalsInt(int i);
	
	public IExpr opposite();
	

	/**
	 * Returns the imaginary part of a complex number
	 * 
	 * @return real part
	 */
	public IExpr getIm();
	
	/**
	 * Returns the real part of a complex number
	 * 
	 * @return real part
	 */
	public IExpr getRe(); 
}
