package org.matheclipse.parser.client;


/**
 * Frontend configuration
 *
 */
public class FEConfig {

	/**
	 * Show the stack trace, if an exception is thrown in evaluation
	 * 
	 */
	public final static boolean SHOW_STACKTRACE = false;
	
	/**
	 * If <code>true</code> the parser doesn't distinguish between lower- or uppercase symbols (i.e. constants, function
	 * names,...), with the exception of symbols with only one character (i.e. the variable &quot;i&quot; is different
	 * from the imaginary unit &quot;I&quot;)
	 */
	public static boolean PARSER_USE_LOWERCASE_SYMBOLS = true;
	/**
	 * If <code>true</code> the parser doesn't allow "square brackets" instead of "parentheses" for enclosing function
	 * arguments in relaxed mode. The syntax <code>f[x, y, ...]</code> isn't allowed then. Always use
	 * <code>f(x, y, ...)</code>.
	 * 
	 */
	public final static boolean PARSER_USE_STRICT_SYNTAX = false;

	/**
	 * <p>
	 * If <code>true</code> the <code>*</code> operator must be written for a <code>Times()</code> expression. I.e. you
	 * cannot write <code>2(b+c)</code> anymore, but have to write <code>2*(b+c)</code> to get
	 * <code>Times(2, Plus(b, c))</code>.
	 * </p>
	 * <p>
	 * You also enable <a href="https://en.wikipedia.org/wiki/Scientific_notation#E-notation">scientific E-notation</a>.
	 * I.e. <code>1E-2</code> is converted to a double value <code>0.01</code> for floating point numbers and not parsed
	 * as <code>Plus(-2, E)</code> anymore.
	 * </p>
	 * <p>
	 * You also enable integer literal input with a prefix, similar to
	 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Java integer literals</a>
	 * <ul>
	 * <li><code>0b</code> or <code>0B</code> for binary numbers</li>
	 * <li><code>0x</code> or <code>0X</code> for hexadecimal numbers</li>
	 * <li><code>0o</code> or <code>0O</code> for octal numbers</li>
	 * </ul>
	 * </p>
	 */
	public static boolean EXPLICIT_TIMES_OPERATOR = false;

	/**
	 * <p>
	 * If <code>true</code> the implicit <code>*</code> operator has a higher precedence than all other operators. I.e.
	 * <code>1/2Pi</code> is parsed as <code>Power(Times(2, Pi), -1)</code>. If <code>false</code> the implicit
	 * <code>*</code> operator has a normal precedence as in all other cases. I.e. <code>1/2Pi</code> is parsed as
	 * <code>Times(Rational(1,2), Pi)</code>.
	 * </p>
	 */
	public static boolean DOMINANT_IMPLICIT_TIMES = false;

}
