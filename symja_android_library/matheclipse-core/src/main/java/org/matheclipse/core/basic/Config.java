package org.matheclipse.core.basic;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math4.util.Precision;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * General configuration settings.
 */
public class Config {

	/**
	 * COMPILER switch - set this boolean variable to <code>true</code>, if you would force a direct plot frame creation from the
	 * Plot[], Plot3D[] and ParametricPlot[] functions
	 * 
	 * On the server this switch should be set to <code>false</code>
	 */
	public static boolean SWING_PLOT_FRAME = false;

	/**
	 * The time in milliseconds an evaluation thread should run.<br/>
	 * 0 => forever
	 * 
	 */
	public static long TIME_CONSTRAINED_MILLISECONDS = 60000L;

	/**
	 * The time in milliseconds an evaluation thread should run.<br/>
	 * 0 => forever
	 * 
	 */
	public final static long FOREVER = 0L;

	/**
	 * The time in milliseconds an evaluation thread should sleep until <code>Thread#stop()</code> will be called.
	 * 
	 */
	public final static long TIME_CONSTRAINED_SLEEP_MILLISECONDS = 500;

	/**
	 * Switch debug mode on/off
	 * 
	 */
	public final static boolean DEBUG = false;

	/**
	 * Show the stack trace, if an exception is thrown in evaluation
	 * 
	 */
	public final static boolean SHOW_STACKTRACE = true;

	/**
	 * Show the console output, if an expression has a head symbol with attribute <code>ISymbol.CONSOLE_OUTPUT</code>.
	 * 
	 */
	public final static boolean SHOW_CONSOLE = true;

	/**
	 * Show the pattern-matching evaluation steps in the console output.
	 * 
	 */
	public final static boolean SHOW_PATTERN_EVAL_STEPS = false;

	public final static Set<ISymbol> SHOW_PATTERN_SYMBOL_STEPS = new HashSet<ISymbol>();

	/**
	 * If <code>true</code> the parser doesn't distinguish between lower- or uppercase symbols (i.e. constants, function names,...)
	 */
	public static boolean PARSER_USE_LOWERCASE_SYMBOLS = true;

	/**
	 * Used to parse Rubi files. See <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - Symbolic Integration Rules</a>
	 */
	public static boolean RUBI_CONVERT_SYMBOLS = false;

	/**
	 * Used to serialize the internal Rubi rules or the <code>org.matheclipse.core.reflection.system.rules</code> classes to a file.
	 */
	public static boolean SERIALIZE_SYMBOLS = false;

	/**
	 * If set to true the <code>Integrate</code> initialization Rules will be read from ressource <code>/ser/integrate.ser</code>
	 */
	// public static boolean LOAD_SERIALIZED_RULES = false;

	/**
	 * <code>true</code> if the engine is started by a servlet<br/>
	 * In <i>server mode</i> the user can only assign values to variables with prefix '$' <br/>
	 * <br/>
	 * SERVER_MODE should be set to <code>true</code> in the initialization of a servlet
	 * 
	 */
	public static boolean SERVER_MODE = false;

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Machine_epsilon">Wikipedia: Machine epsilon</a>
	 */
	public static double DOUBLE_EPSILON = Precision.EPSILON;

	/**
	 * The double tolerance used for comparisons.
	 */
	public final static double DOUBLE_TOLERANCE = DOUBLE_EPSILON * 10d;

	/**
	 * Replace <code>double</code> values in root algorithms by 0 if they are below this tolerance.
	 */
	public final static double DEFAULT_ROOTS_CHOP_DELTA = 1.0e-5;

	/**
	 * Maximum size of the BigInteger words in <i>server mode</i>.
	 * 
	 * @see apache.harmony.math.BigInteger
	 */
	public static int BIGINTEGER_MAX_SIZE = 65536;

	/**
	 * Maximum size of the FastTable entries in <i>server mode</i>.
	 * 
	 * @see javolution.util.FastTable
	 */
	public static int FASTTABLE_MAX_SIZE = 65536;

	/**
	 * Maximum length of the StringImpl's <code>String</code> in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.expression.Expression
	 */
	public static int STRING_MAX_SIZE = 4096;

	/**
	 * Maximum size of the IntegerImpl pools in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.expression.Expression
	 */
	public static int INTEGER_MAX_POOL_SIZE = 4096;

	/**
	 * Maximum size of the FractionImpl pools in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.expression.Expression
	 */
	public static int FRACTION_MAX_POOL_SIZE = 4096;

	/**
	 * Maximum size of the ComplexImpl pools in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.expression.Expression
	 */
	public static int COMPLEX_MAX_POOL_SIZE = 4096;

	/**
	 * Maximum size of the DoubleImpl pools in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.expression.Expression
	 */
	public static int DOUBLE_MAX_POOL_SIZE = 65536;// 16384;

	/**
	 * Maximum size of the DoubleComplexImpl pools in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.expression.Expression
	 */
	public static int DOUBLECOMPLEX_MAX_POOL_SIZE = 65536;// 16384;

	/**
	 * Maximum size of the PatternImpl pools in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.expression.Expression
	 */
	public static int PATTERN_MAX_POOL_SIZE = 1024;

	/**
	 * Maximum size of the StringImpl pools in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.expression.Expression
	 */
	public static int STRING_MAX_POOL_SIZE = 1024;

	/**
	 * Maximum size of the double vector's in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.basic.Alloc
	 */
	public static int MAX_DOUBLE_VECTOR_SIZE = 65536;

	/**
	 * Maximum size of the double matrix's in <i>server mode</i>.
	 * 
	 * @see org.matheclipse.core.basic.Alloc
	 */
	public static int MAX_DOUBLE_MATRIX_SIZE = 65536;

}
