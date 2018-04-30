package org.matheclipse.core.basic;

import java.util.HashSet;
import java.util.Set;

import org.hipparchus.util.Precision;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * General configuration settings.
 */
public class Config {

	/**
	 * A global expression cache which compares keys with <code>==</code> object identity instead of
	 * <code>equals()</code>. The keys and values are weak references.
	 */
	public static Cache<IExpr, Object> EXPR_CACHE;

	private static final int MAX_EXPR_CACHE_SIZE = 10000;

	static {
		EXPR_CACHE = CacheBuilder.newBuilder().maximumSize(MAX_EXPR_CACHE_SIZE).weakKeys().weakValues().build();
	}
	/**
	 * COMPILER switch - set this boolean variable to <code>true</code>, if you would force a direct plot frame creation
	 * from the Plot[], Plot3D[] and ParametricPlot[] functions
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
	public final static boolean SHOW_STACKTRACE = false;

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
	 * Used to parse Rubi files. See <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - Symbolic Integration Rules</a>
	 */
	public static boolean RUBI_CONVERT_SYMBOLS = false;

	/**
	 * Used to serialize the internal Rubi rules or the <code>org.matheclipse.core.reflection.system.rules</code>
	 * classes to a file.
	 */
	public static boolean SERIALIZE_SYMBOLS = false;

	/**
	 * If set to true the <code>Integrate</code> initialization Rules will be read from ressource
	 * <code>/ser/integrate.ser</code>
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
	 * Define the recursion limit for <code>Integrate#integrateByParts()</code> method.
	 */
	public static int INTEGRATE_BY_PARTS_RECURSION_LIMIT = 10;

	/**
	 * Define the recursion limit for <code>Limit#lHospitalesRule()</code> method.
	 */
	public static int LIMIT_LHOSPITAL_RECURSION_LIMIT = 128;

	/**
	 * <p>
	 * Flag for thread usage.
	 * </p>
	 * <p>
	 * <b>Note:</b> Only introduced because Google app engine does not support threads.
	 * </p>
	 * 
	 * @see edu.jas.ufd.GCDFactory#getProxy(edu.jas.structure.RingFactory)
	 */
	public static boolean JAS_NO_THREADS = false;

	/**
	 * Use <code>Num</code> objects for numeric calculations up to 15 digits precision.
	 */
	public static final int MACHINE_PRECISION = 15;

	/**
	 * Enable tests and functions which use the local files. Don't use <code>final</code> here because of grpc
	 * interface.
	 */
	public static boolean FILESYSTEM_ENABLED = false;

	public static boolean isFileSystemEnabled(EvalEngine engine) {
		return FILESYSTEM_ENABLED || engine.isFileSystemEnabled();
	}

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
	 * <p>
	 * 
	 * </p>
	 * <p>
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
