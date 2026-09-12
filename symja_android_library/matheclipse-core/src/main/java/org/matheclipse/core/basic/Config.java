package org.matheclipse.core.basic;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.hipparchus.util.Precision;
import org.matheclipse.core.builtin.FunctionDefinitions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.BuiltinFunctionCalls;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.IPrimality;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;
import org.matheclipse.parser.trie.TrieSequencerIntArray;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/** General configuration settings. */
public class Config {

  public static BooleanSupplier BUILTIN_FUNCTION_INIT =
      () -> FunctionDefinitions.builtinFunctionInitializer();

  /** CheerpJ (cheerpj.com) environment? */
  public static boolean CHEEPRJ = false;

  /** Show the stack trace, if an exception is thrown in evaluation */
  public static boolean SHOW_STACKTRACE = false;

  /** Disable JMX calls (package java.lang.management) */
  public static boolean DISABLE_JMX = false;

  /** Enable JSFiddle in JavaScript IFRAME output */
  public static boolean DISPLAY_JSFIDDLE_BUTTON = true;

  /** Indicate that the class java.awt.Desktop is available */
  public static boolean JAVA_AWT_DESKTOP_AVAILABLE = true;

  /** Symja ASCII Art String */
  public static final String SYMJA = //
      "     _______.____    ____ .___  ___.        __       ___      \n"
          + "    /       |\\   \\  /   / |   \\/   |       |  |     /   \\     \n"
          + "   |   (----` \\   \\/   /  |  \\  /  |       |  |    /  ^  \\    \n"
          + "    \\   \\      \\_    _/   |  |\\/|  | .--.  |  |   /  /_\\  \\   \n"
          + ".----)   |       |  |     |  |  |  | |  `--'  |  /  _____  \\  \n"
          + "|_______/        |__|     |__|  |__|  \\______/  /__/     \\__\\ \n"
          + "                                                              \n";

  /** Copyright message */
  public static final String COPYRIGHT = //
      "\nCopyright (C) 2009 - 2025 - the Symja team.\n" //
          + "This program comes with ABSOLUTELY NO WARRANTY.\n" //
          + "Distributed under the GNU Public License.\n" //
          + "See the file license.txt\n\n";

  /**
   * "UTF-8" is used as default character encoding
   */
  public static String SYSTEM_CHARACTER_ENCODING = "UTF-8";

  /**
   * <p>
   * A global expression cache which compares keys with <code>==</code> object identity instead of
   * <code>equals()</code>. The keys and values are weak references.
   * 
   * @see #getExprCache()
   */
  private static volatile Cache<IExpr, Object> EXPR_CACHE = null;

  private static final int MAX_EXPR_CACHE_SIZE = 10000;

  /**
   * Maximum number for the leaf count of an expression so that <code>Factor()</code> will try a
   * factoring. Has to be an int value greater 0.
   */
  public static final int MAX_FACTOR_LEAFCOUNT = 1000;

  public static int MAX_GRAPH_VERTICES_SIZE = 100;

  /**
   * Maximum number of variables allowed for generating the minterm DNF in
   * {@link S#BooleanCountingFunction}. The minterm enumeration is exponential (<code>2^n</code>),
   * so this guards against excessive memory/CPU usage.
   */
  public static int MAX_BOOLEAN_COUNTING_FUNCTION_VARIABLES = 16;

  /**
   * Maximum memory block size for the {@link ApfloatContext}
   */
  public static final long MAX_APFLOAT_MEMORY_BLOCKSIZE = 1_000_000;

  /**
   * Maximum processors for the {@link ApfloatContext}
   */
  public static final int MAX_APFLOAT_PROCESSORS = 1;

  /**
   * Maximum number for the leaf count of the numerator or denominator of a rational expression so
   * that {@link S#Together} and {@link S#Cancel} will try to cancel a polynomial GCD.
   *
   * <p>
   * The GCD is computed by the JAS subresultant PRS algorithm over a ring of arbitrary
   * {@link IExpr} coefficients. That algorithm is prone to intermediate coefficient swell: on
   * complex rational coefficients the intermediate numerators and denominators can grow without
   * practical bound, so a GCD of a large input may not finish in any reasonable time. It cannot be
   * cancelled either, because JAS only tests the interrupted-flag when it constructs a polynomial
   * and a single coefficient operation on multi-thousand-digit numbers already runs for seconds, so
   * neither {@link EvalEngine#setSeconds(long)} nor a thread interrupt bounds it.
   *
   * <p>
   * Above this limit the GCD is skipped and the expression is left uncancelled, which is a correct
   * (only less reduced) result. Has to be an int value greater 0.
   */
  public static final int MAX_CANCEL_GCD_LEAFCOUNT = 4000;

  /**
   * Maximum number for the leaf count of an expression so that {@link S#PossibleZeroQ} > will try a
   * factoring. Has to be an int value greater 0.
   */
  public static final int MAX_POSSIBLE_ZERO_LEAFCOUNT = 1000;

  /**
   * Maximum number for the leaf count of an expression so that {@link S#Simplify} will try calling
   * {@link S#Factor}.
   */
  public static final int MAX_SIMPLIFY_FACTOR_LEAFCOUNT = 100;

  /**
   * Maximum number for the leaf count of an expression so that {@link S#Simplify} will try calling
   * {@link S#Apart}.
   */
  public static final int MAX_SIMPLIFY_APART_LEAFCOUNT = 100;

  /**
   * Maximum number for the leaf count of an expression so that {@link S#Simplify} will try calling
   * {@link S#Together}.
   */
  public static final int MAX_SIMPLIFY_TOGETHER_LEAFCOUNT = 65;

  /**
   * Maximum number of the exponent if a {@link S#Plus} expression is in the base of the power so
   * that {@link S#Simplify} will try calling {@link S#Expand}.
   */
  public static final int MAX_SIMPLIFY_EXPAND_PLUS_EXPONENT = 10;

  /** Maximum number of parsed input leaves of an expression */
  public static long MAX_INPUT_LEAVES = Long.MAX_VALUE;

  /** Maximum output size in characters for an output form (i.e. TeXForm, MathMLForm,...I) */
  public static int MAX_OUTPUT_SIZE = Integer.MAX_VALUE;

  /** Maximum size in characters for a single output line */
  public static int MAX_OUTPUT_LINE = 80;

  /** Maximum number of elements which could be allocated for an AST */
  public static int MAX_AST_SIZE = Integer.MAX_VALUE;

  /** Maximum number of row or column dimension allowed if creating a new matrix or vector */
  public static int MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;

  /** Maximum number of elements which could be allocated for a BigInteger number */
  public static int MAX_BIT_LENGTH = Integer.MAX_VALUE;

  /** Maximum degree of a polynomial generating function */
  public static int MAX_POLYNOMIAL_DEGREE = Integer.MAX_VALUE;

  /** Maximum degree of a polynomial for Laguerre solver */
  public static int MAX_POLYNOMIAL_DEGREE_LAGUERRE_SOLVER = Integer.MAX_VALUE;

  /** Maximum number of loop runs in some Symja functions */
  public static long MAX_LOOP_COUNT = Long.MAX_VALUE;

  /** Maximum number of (partition) combinations to test in pattern matching */
  public static long MAX_PATTERN_MATCHING_COMBINATIONS = 1800;

  /**
   * The minimum capacity of elements needed in an {@link IAST} to switch to persistence list
   * implementation with structural sharing
   */
  public static final int MIN_LIMIT_PERSISTENT_LIST = 32;

  /**
   * <p>
   * Get the global expression cache which compares keys with <code>==</code> object identity
   * instead of <code>equals()</code>. The keys and values are weak references.
   * 
   */
  public static Cache<IExpr, Object> getExprCache() {
    if (EXPR_CACHE == null) {
      EXPR_CACHE = CacheBuilder.newBuilder().maximumSize(MAX_EXPR_CACHE_SIZE).weakKeys()
          .weakValues().build();
    }
    return EXPR_CACHE;
  }

  /**
   * COMPILER switch - set this boolean variable to <code>true</code>, if you would force a direct
   * plot frame creation from the Plot[], Plot3D[] and ParametricPlot[] functions
   *
   * <p>
   * On the server this switch should be set to <code>false</code>
   */
  public static boolean SWING_PLOT_FRAME = false;

  /**
   * The time in milliseconds an evaluation thread should run.<br>
   * 0 =&gt; forever
   */
  public static final long FOREVER = 0L;

  /**
   * The time in milliseconds an evaluation thread should sleep until <code>Thread#stop()</code>
   * will be called.
   */
  public static final long TIME_CONSTRAINED_SLEEP_MILLISECONDS = 500;

  /**
   * The time in seconds a server request can evaluate an expression.
   */
  public static long SERVER_REQUEST_TIMEOUT_SECONDS = 60;

  /** Switch debug mode on/off */
  public static final boolean DEBUG = false;

  /**
   * Minimum number of pattern down-rules a symbol needs before a
   * {@link org.matheclipse.core.patternmatching.ruleindex.RuleFeatureIndex} is built for it. Below
   * this size a linear scan is cheaper than analyzing the expression which should be rewritten.
   * Set to {@link Integer#MAX_VALUE} to switch the index off.
   */
  public static int RULE_INDEX_MIN_RULES =
      Integer.getInteger("symja.ruleIndexMinRules", 16).intValue();

  /**
   * Set to <code>true</code> to check every rule index dispatch against a full linear scan. The
   * linear scan determines the result, the index is only verified to contain the rule which fired.
   * Mismatches are reported on <code>System.err</code> and counted in
   * {@link org.matheclipse.core.patternmatching.ruleindex.RuleIndexValidation}. Slow - for testing
   * only.
   */
  public static boolean RULE_INDEX_VALIDATE = false;

  /** Set to <code>true</code> to collect rule dispatch counters. */
  public static boolean RULE_DISPATCH_STATISTICS = false;

  /**
   * Set to <code>true</code> to collect the counters in
   * {@link org.matheclipse.core.expression.AstAllocationStats}: how appendable argument lists are
   * created, how often they outgrow their initial capacity, and from which call sites. Every
   * recorded event walks the stack to find its site, so this is slow - for a census run, not for
   * production.
   */
  public static boolean AST_ALLOCATION_STATISTICS =
      Boolean.getBoolean("symja.astAlloc.stats");

  // The wall-clock evaluation budgets are configured with -Dsymja.timeScale=<factor> or
  // -Dsymja.machineProfile=fast|normal|slow|auto, see MachineProfile.

  /**
   * Substitute the right-hand-side of a rewrite rule with a precompiled
   * {@link org.matheclipse.core.patternmatching.SubstitutionPlan} instead of walking the whole
   * expression with a generic visitor.
   * <p>
   * Set <code>-Dsymja.substitutionPlan=false</code> to fall back to
   * {@link org.matheclipse.core.patternmatching.IPatternMap#substituteSymbols(org.matheclipse.core.interfaces.IExpr, org.matheclipse.core.interfaces.IExpr)}
   * everywhere. Both paths produce the same result, so this only trades speed for a smaller amount
   * of machinery on the hot path.
   */
  public static boolean SUBSTITUTION_PLAN =
      !"false".equalsIgnoreCase(System.getProperty("symja.substitutionPlan"));

  /**
   * Set to <code>true</code> to check every planned substitution against the generic one. The
   * generic result is the one which is returned, so an incorrect plan cannot change a result while
   * this mode is enabled; differences are counted in
   * {@link org.matheclipse.core.patternmatching.ruleindex.SubstitutionPlanStats} and reported on
   * <code>System.err</code>. Slow - for testing only.
   */
  public static boolean SUBSTITUTION_PLAN_VALIDATE =
      Boolean.getBoolean("symja.substitutionPlanValidate");

  /**
   * Set to <code>true</code> to collect counters for the
   * {@link org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher} dispatch of
   * <code>Plus(...)</code> and <code>Times(...)</code> in
   * {@link org.matheclipse.core.patternmatching.ruleindex.OrderlessHashStats}. Slow - for
   * measurement only.
   */
  public static boolean ORDERLESS_HASH_STATISTICS =
      Boolean.getBoolean("symja.orderlessHashStatistics");

  /**
   * Set to <code>true</code> to prefilter the argument pairs of the
   * {@link org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher} with an
   * {@link org.matheclipse.core.patternmatching.ruleindex.OrderlessPairIndex}.
   */
  public static boolean ORDERLESS_PAIR_INDEX =
      !"false".equals(System.getProperty("symja.orderlessPairIndex"));

  /**
   * Set to <code>true</code> to check every dispatch of the
   * {@link org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher} against a full pair
   * scan. The scan determines the result, the {@link
   * org.matheclipse.core.patternmatching.ruleindex.OrderlessPairIndex} is only verified to keep
   * every pair which is rewritten. Mismatches are reported on <code>System.err</code> and counted
   * in {@link org.matheclipse.core.patternmatching.ruleindex.OrderlessIndexValidation}. Slow - for
   * testing only.
   */
  public static boolean ORDERLESS_PAIR_INDEX_VALIDATE = false;

  /** Set to true if in fuzz testing mode */
  public static boolean FUZZ_TESTING = false;

  /** Set to <code>true</code> if the fuzzy parser should be used in the free form Symja API */
  public static boolean FUZZY_PARSER = false;

  /** Set to <code>true</code> if the parser should be used to map the user input exactly */
  public static boolean USER_STEPS_PARSER = false;

  /**
   * Show the console output, if an expression has a head symbol with attribute <code>
   * ISymbol.CONSOLE_OUTPUT</code>.
   */
  // public final static boolean SHOW_CONSOLE = false;

  /**
   * Shorten an output string to a maximum length of <code>SHORTEN_STRING_LENGTH</code> characters.
   * Print &lt;&lt;SHORT&gt;&gt; as substitute of the middle of the expression if necessary.
   */
  public static int SHORTEN_STRING_LENGTH = 80;

  /** Show the pattern-matching evaluation steps in the console output. */
  public static final boolean SHOW_PATTERN_EVAL_STEPS = false;

  public static final Set<ISymbol> SHOW_PATTERN_SYMBOL_STEPS = new HashSet<ISymbol>();

  /**
   * Contains a list of strings. If executed with the
   * <a href="https://github.com/axkr/symja_android_library/wiki/Console-apps">console apps</a> the
   * executable is the first string followed by the argument strings. If not executed with a console
   * app it returns the empty list.
   */
  public static IAST SCRIPT_COMMAND_LINE = null;
  /**
   * Used to serialize the internal Rubi rules or the <code>
   * org.matheclipse.core.reflection.system.rules</code> classes to a file.
   */
  public static boolean SERIALIZE_SYMBOLS = false;

  /**
   * If set to true the <code>Integrate</code> initialization Rules will be read from ressource
   * <code>/ser/integrate.ser</code>
   */
  // public static boolean LOAD_SERIALIZED_RULES = false;

  /**
   * <code>true</code> if the engine is started by a servlet<br>
   * In <i>server mode</i> the user can only assign values to variables with prefix '$' <br>
   * <br>
   * SERVER_MODE should be set to <code>true</code> in the initialization of a servlet
   */
  public static boolean SERVER_MODE = false;

  /**
   * <code>true</code> if it's allowed to delete the <code>Protected</code> attribute from a symbol.
   * In <i>server mode</i> this flag should be set to <code>false</code>. <br>
   */
  public static boolean UNPROTECT_ALLOWED = true;

  /** See <a href="http://en.wikipedia.org/wiki/Machine_epsilon">Wikipedia: Machine epsilon</a> */
  public static double DOUBLE_EPSILON = Precision.EPSILON;

  /**
   * The double tolerance used for comparisons. For example the {@link Num#isZero()} or
   * {@link ComplexNum#isZero()} methods use this parameter.
   */
  public static double DOUBLE_TOLERANCE = DOUBLE_EPSILON * 10d;

  /**
   * The double tolerance used to determine if a value is printed as 0.0 in output formatting. For
   * example {@link OutputFormFactory}
   */
  public static double ZERO_IN_OUTPUT_FORMAT = 1.0e-100;

  /**
   * The default value of {@link OutputFormFactory}#fIgnoreNewLine,
   * <p>
   * This option allow to control the default value of OutputFormFactory#fIgnoreNewLine, which
   * obtains from OutputFormFactory#get* methods
   */
  public static boolean DEFAULT_OUTPUT_FORM_IGNORE_NEW_LINE = false;

  /** The real which added to 1.0 gives the next double value greater than 1.0 */
  public static double MACHINE_EPSILON = Math.nextUp(1.0) - 1.0;

  /**
   * Number of trailing digits which are <b>not</b> required to agree when two
   * {@link org.apfloat.Apfloat} values are compared for fuzzy equality (see
   * {@code F#isFuzzyEquals(org.apfloat.Apfloat, org.apfloat.Apfloat)}). Two values count as equal
   * if they agree in at least {@code precision - APFLOAT_ZERO_GUARD_DIGITS} significant digits.
   * <p>
   * A larger value yields a looser (more permissive) tolerance.
   */
  public static int APFLOAT_ZERO_GUARD_DIGITS = 5;

  /**
   * Replace <code>double</code> values in root algorithms by 0 if they are below this tolerance.
   * Assume <code>double</code> values in <code>PossibleZeroQ</code> to be 0 if they are below this
   * tolerance.
   */
  public static double DEFAULT_ROOTS_CHOP_DELTA = 1.0e-5;

  /**
   * Epsilon criteria for final AV=VD check in Eigen decompositions.
   */
  public static double DEFAULT_EPSILON_AV_VD_CHECK = 1.0e-4;

  /**
   * Tolerance used in special function algorithms ported from
   * <a href="https://github.com/paulmasson/math">math.js</a> and in the JavaScript based plot
   * functions.
   */
  public static double SPECIAL_FUNCTIONS_TOLERANCE = 1.0e-10;

  public static double DEFAULT_EQUALS_TOLERANCE = 1.0e-14;

  /**
   * Return {@link S#True} for {@link Apfloat} integers in <code>Element(apfloat, Integers)</code>
   */
  public static boolean ACCEPT_NUMERIC_INTEGER_IN_INTEGERS_DOMAIN = false;

  /** Define the recursion limit for <code>Integrate#integrateByParts()</code> method. */
  public static int INTEGRATE_BY_PARTS_RECURSION_LIMIT = 10;

  /** Define the recursion limit for <code>Integrate#integrateByRubiRules()</code> method. */
  public static int INTEGRATE_RUBI_RULES_RECURSION_LIMIT = 100;

  /**
   * How long one <code>TimeConstrained()</code> <i>inside</i> a Rubi rule may take, in seconds:
   * the Rubi <code>§$timelimit</code> variable.
   *
   * <p>
   * This is independent of {@link #INTEGRATE_RUBI_TIMELIMIT_MILLIS}, which is the total wall-clock
   * budget the <code>Integrate()</code> watchdog grants to one top-level run of the Rubi rules.
   *
   * <p>
   * The value is the limit on the machine the rules were tuned on; the limit which is used is
   * {@link MachineProfile#seconds(long)} of it. It is bound to the Rubi symbol
   * <code>§$timelimit</code> when the rules are loaded, on the first call of
   * <code>Integrate()</code>, so a factor which is set programmatically after that does not reach
   * it - see {@link MachineProfile#setScale(double)}.
   */
  public static int INTEGRATE_RUBI_RULE_TIMELIMIT_SECONDS = 8;

  /**
   * Maximum number of entries in the per-engine LRU cache which memoizes the results of the Rubi
   * integration rules for <code>Integrate()</code> (including "the rules have no match" results).
   */
  public static int INTEGRATE_RUBI_CACHE_SIZE = 500;

  /**
   * Master flag which enables the fast algorithmic integration cascade (rational function
   * integration, radical substitution, ...) which is tried before the Rubi rules in
   * <code>Integrate()</code>.
   *
   * <p>
   * Each stage additionally has its own <code>INTEGRATE_ALGORITHM_*</code> kill-switch (below). A
   * stage participates in the <i>Automatic</i> cascade only when it is both enabled and explicitly
   * wired into the cascade in <code>Integrate.java</code>. Stages that are ported and unit-tested
   * but not yet trusted to change production output forms are intentionally left un-wired: their
   * flag stays on so they remain reachable via <code>Integrate[f, x, Method -&gt; "..."]</code> and
   * their direct tests, and they are wired into the cascade one at a time as each passes its
   * differentiate-back corpus. Flags for stages that are not yet ported are placeholders and
   * default to <code>false</code>.
   */
  public static boolean INTEGRATE_ALGORITHMS = true;

  /**
   * Enable the rational function integration stage (Hermite/Horowitz-Ostrogradsky reduction plus
   * Lazard-Rioboo-Trager logarithmic part) in <code>Integrate()</code>.
   */
  public static boolean INTEGRATE_ALGORITHM_RATIONAL = true;

  /** Enable the CRC-style integral table lookup stage in <code>Integrate()</code>. */
  public static boolean INTEGRATE_ALGORITHM_TABLE = true;

  /** Enable the linear radical substitution stage in <code>Integrate()</code>. */
  public static boolean INTEGRATE_ALGORITHM_RADICAL_SUBSTITUTION = true;

  /** Enable the quadratic radical (Euler substitution) stage in <code>Integrate()</code>. */
  public static boolean INTEGRATE_ALGORITHM_QUADRATIC_RADICALS = false;

  /** Enable the linear-ratio radical substitution stage in <code>Integrate()</code>. */
  public static boolean INTEGRATE_ALGORITHM_LINEAR_RATIO_RADICALS = false;

  /** Enable the Chebyshev binomial-differential stage in <code>Integrate()</code>. */
  public static boolean INTEGRATE_ALGORITHM_CHEBYCHEV = true;

  /** Enable the Goursat pseudo-elliptic reduction stage in <code>Integrate()</code>. */
  public static boolean INTEGRATE_ALGORITHM_GOURSAT = false;

  /**
   * Enable the differentiation-under-the-integral-sign stage for definite integrals in
   * <code>Integrate()</code>.
   */
  public static boolean INTEGRATE_ALGORITHM_DIFF_UNDER_INT = true;

  /**
   * Enable the Weierstrass/Jeffrey substitution stage for rational trig in
   * <code>Integrate()</code>.
   */
  public static boolean INTEGRATE_ALGORITHM_WEIERSTRASS = true;

  /** Enable the derivative-divides (Geddes) heuristic stage in <code>Integrate()</code>. */
  public static boolean INTEGRATE_ALGORITHM_DERIVATIVE_DIVIDES = true;

  /**
   * Enable the conjugate rationalization stage (clear a single square root out of the denominator)
   * in <code>Integrate()</code>.
   */
  public static boolean INTEGRATE_ALGORITHM_SURD_RATIONALIZATION = true;

  /**
   * Enable the primitive-monomial (Log tower) Risch stage in <code>Integrate()</code>: partial
   * fractions in the monomial plus the logarithmic-derivative test for its simple poles.
   */
  public static boolean INTEGRATE_ALGORITHM_PRIMITIVE_TOWER = true;

  /** Enable the Risch-Norman (parallel Risch) stage in <code>Integrate()</code>. */
  public static boolean INTEGRATE_ALGORITHM_RISCH_NORMAN = true;

  /** Enable the recursive transcendental Risch stage in <code>Integrate()</code> (long-term). */
  public static boolean INTEGRATE_ALGORITHM_RISCH_TRANSCENDENTAL = true;

  /**
   * Time budget in milliseconds for the Rubi rules inside <code>Integrate()</code>. When the rules
   * exceed it, they are interrupted and the native post-Rubi stages get their turn instead of the
   * whole evaluation running into the caller's deadline. <code>0</code> disables the budget (the
   * rules then run until they finish or the evaluation is aborted). Not to be confused with
   * {@link #INTEGRATE_RUBI_RULE_TIMELIMIT_SECONDS}, the <b>seconds</b> limit for a single
   * <code>TimeConstrained()</code> call inside the Rubi rules themselves.
   *
   * <p>
   * When the evaluation has a deadline of its own, the budget is additionally capped at
   * {@link #INTEGRATE_RUBI_TIMELIMIT_SHARE} of the remaining time, so the native stages always keep
   * a slice. The budget is best-effort: it interrupts the evaluation thread, and code that does not
   * check for interruption (notably JAS) only notices when control returns to the evaluation loop.
   */
  // Raised 30s -> 45s (2026-09-02). Refusing to hand a bare RootSum to the rules (see
  // Integrate.evaluate) makes them retry other rules after each refusal, and every retry re-runs
  // the native cascade. Integrate(Log(x^2+Sqrt(1-x^2)),x) needs 30-45s of rule time under that,
  // and at 30s the watchdog cut it off and the answer was lost - testIntegrateRationalizeSurdDenominator
  // went red not on a wrong answer but on a missing one. Measured at 45s: matheclipse-core is
  // fully green (4336/0) and the independent Rubi corpus improves 44 -> 39 failures.
  // The value is the budget on the machine it was measured on; the budget which is used is
  // MachineProfile.millis() of it.
  public static long INTEGRATE_RUBI_TIMELIMIT_MILLIS = 45000L;

  /** Fraction of the remaining evaluation time the Rubi rules may use, see above. */
  public static double INTEGRATE_RUBI_TIMELIMIT_SHARE = 0.75;

  /**
   * Time limit in milliseconds for the rational integration stage. It bounds the one part of the
   * stage that can be slow: expanding a {@link org.matheclipse.core.integrate.RationalIntegration
   * RootSum} over a solvable cubic or quartic into explicit radicals (only in EMIT mode).
   * Everything else - degree 1/2 factors, the inert degree &gt;= 5 RootSum - returns in
   * milliseconds and never approaches the limit. An expansion that overruns is dropped and the
   * integral falls through.
   *
   * <p>
   * The value is the budget on the machine it was measured on, scaled by {@link MachineProfile}.
   */
  public static long INTEGRATE_RATIONAL_TIMELIMIT_MILLIS = 3000L;

  /**
   * Time limit in milliseconds for the Risch-Norman stage, on the machine it was measured on and
   * scaled by {@link MachineProfile}.
   */
  public static long INTEGRATE_RISCH_NORMAN_TIMELIMIT_MILLIS = 2000L;

  /** Maximum recursion depth for the derivative-divides heuristic. */
  public static int INTEGRATE_DERIVATIVE_DIVIDES_RECURSION_LIMIT = 3;

  /** Define the recursion limit for <code>Limit#lHospitalesRule()</code> method. */
  public static int LIMIT_LHOSPITAL_RECURSION_LIMIT = 20;

  /**
   * Flag for thread usage.
   *
   * <p>
   * <b>Note:</b> introduced because Google app engine does not support threads.
   *
   * @see edu.jas.ufd.GCDFactory#getProxy(edu.jas.structure.RingFactory)
   */
  public static boolean JAS_NO_THREADS = false;

  /**
   * Use sparse interpolation, Zippel's algorithm, for the polynomial greatest common divisor.
   *
   * <p>
   * Affects {@link org.matheclipse.core.expression.S#PolynomialGCD},
   * {@link org.matheclipse.core.expression.S#PolynomialLCM} and everything else which reaches a JAS
   * gcd - {@code Factor}, {@code Together}, {@code Cancel}, {@code Apart}. The sparse algorithm
   * wins on polynomials in many variables whose gcd has few terms and loses its extra bookkeeping
   * on the dense low variable count problems which are the common case, which is why it is off by
   * default.
   *
   * <p>
   * <b>Note:</b> the sparse algorithm verifies its result by division and falls back to a dense one
   * whenever that fails, so this flag changes the running time and never the result.
   *
   * @see edu.jas.kern.JASConfig#USE_SPARSE_GCD
   */
  public static boolean JAS_GCD_SPARSE = true;

  /** Use of <code>java.misc.Unsafe</code> is allowed if <code>true</code>. */
  public static boolean JAVA_UNSAFE = false;

  /**
   * Flag for thread usage in TimeConstrained function.
   *
   * <p>
   * <b>Note:</b> introduced because Google app engine does not support threads.
   */
  public static boolean TIMECONSTRAINED_NO_THREAD = false;

  /**
   * An object that creates new threads on demand. Using thread factories removes hardwiring of
   * calls to new Thread, enabling applications to use special thread subclasses, priorities, etc.
   *
   * <p>
   * For example <code>com.google.appengine.api.ThreadManager.currentRequestThreadFactory()
   * </code> can be used on Google appengine.
   */
  public static java.util.concurrent.ThreadFactory THREAD_FACTORY =
      // Java 21: Thread.ofVirtual().factory();
      Executors.defaultThreadFactory();

  /**
   * Use <code>Num</code> objects for numeric calculations up to 16 digits precision.
   *
   * @deprecated use {@link ParserConfig#MACHINE_PRECISION}
   */
  @Deprecated
  public static final long MACHINE_PRECISION = ParserConfig.MACHINE_PRECISION;

  /** The maximum precision which could be requested from a user for numerical calculations. */
  public static long MAX_PRECISION_APFLOAT = 256;

  /**
   * If <code>true</code> the {@link S#N} function truncates a requested precision which is greater
   * than {@link #MAX_PRECISION_APFLOAT} down to {@link #MAX_PRECISION_APFLOAT}, instead of printing
   * the <code>N::precgt</code> message and leaving the expression unevaluated.
   */
  public static boolean TRUNCATE_PRECISION_IN_N = false;

  /** The extended precision in {@link S#N} function if exact numbers extend double precision. */
  public static boolean USE_EXTENDED_PRECISION_IN_N = false;

  /** Print trigonometric functions in lower case characters. */
  public static boolean MATHML_TRIG_LOWERCASE = true;

  /**
   * Set this parameter to false if you would like a <code>\\begin{pmatrix} ... \\end{pmatrix}
   * </code> output instead of <code>\left( \begin{array} ... \end{array} \right)</code> in TeXForm.
   */
  public static boolean MATRIX_TEXFORM = true;

  /**
   * Enable tests and functions which use the local files. Don't use <code>final</code> here. Set
   * this switch to <code>true</code> to leave the sandbox mode.
   */
  public static boolean FILESYSTEM_ENABLED = false;

  public static boolean isFileSystemEnabled(EvalEngine engine) {
    return FILESYSTEM_ENABLED || engine.isFileSystemEnabled();
  }

  /** Do time consuming JUnit tests. For example for <code>FactorInteger</code> function */
  public static boolean EXPENSIVE_JUNIT_TESTS = false;

  /** Default package mode with which the EvalEngines initially can be started */
  public static boolean PACKAGE_MODE = true;

  /**
   * Let {@link org.matheclipse.core.generic.UnaryNumerical} compile the sampled function to JVM
   * bytecode when an {@link org.matheclipse.core.compile.IExprCompiler} is installed, i.e. when the
   * <code>matheclipse-compile</code> module is on the classpath.
   *
   * <p>
   * A numerical integration samples its integrand ten-thousands of times, so this is where
   * compiling pays. It is <b>off by default</b> on purpose: the compiled and the interpreted path
   * differ in real ways at the edges - <code>Abs</code> of a complex intermediate,
   * <code>Indeterminate</code> versus <code>NaN</code>, integer overflow - and the compiled path is
   * only ever a fast path, never the authority. <code>UnaryNumerical</code> falls back to
   * interpreted evaluation whenever the compiled code produces no real value, and stops using it
   * for that instance once the two disagree.
   *
   * <p>
   * Flip this only with numbers in hand; see COMPILE_MODULE_PLAN.md section 4.2.
   */
  public static boolean COMPILE_NUMERIC_FUNCTIONS = false;

  public static Consumer<IExpr> PRINT_OUT = x -> {
  };

  public final static boolean PROFILE_MODE = false;

  public static HashMap<BuiltinFunctionCalls, BuiltinFunctionCalls> PRINT_PROFILE =
      PROFILE_MODE ? new HashMap<BuiltinFunctionCalls, BuiltinFunctionCalls>() : null;

  /** The algorithm which should be used for the factorization of integer numbers. */
  // public static Function<IInteger, IAST> FACTOR_INTEGER = Primality::factorIInteger;
  public static IPrimality PRIME_FACTORS = new Primality();


  /** Use visjs.org JavaScript library for visualizing graph theory objects */
  public static boolean USE_VISJS = false;

  /** HTML template for the <a href="https://visjs.org/">VIS-network</a> */
  public static final String VISJS_PAGE = //
      "<html>\n" + //
          "<head>\n" + //
          "<meta charset=\"utf-8\">\n" + //
          "<head>\n" + //
          "  <title>Graph network</title>\n" + //
          "\n" + //
          "  <script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js\"></script>\n"
          + //
          "  <style type=\"text/css\">\n" + //
          "    #mynetwork {\n" + //
          "      width: 600px;\n" + //
          "      height: 400px;\n" + //
          "      border: 1px solid lightgray;\n" + //
          "    }\n" + //
          "  </style>\n" + //
          "</head>\n" + //
          "<body>\n" + //
          "<div id=\"vis\"></div>\n" + //
          "\n" + //
          "<script type=\"text/javascript\">\n" + //
          "`1`\n" + //
          "  // create a network\n" + //
          "  var container = document.getElementById('vis');\n" + //
          "  var data = {\n" + //
          "    nodes: nodes,\n" + //
          "    edges: edges\n" + //
          "  };\n" + //
          "`2`\n" + //
          // " var options = {};\n" + //
          "  var network = new vis.Network(container, data, options);\n" + //
          "</script>\n" + //
          "\n" + //
          "\n" + //
          "</body>\n" + //
          "</html>"; //

  public static final String SVG_PAGE = //
      "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-0.333333 -0.333333 350.666667 350.666667\" width=\"350.6666666px\" height=\"350.6666666px\">\n"
          + "`1`\n" + "</svg>"; //

  /** HTML template */
  public static final String HTML_PAGE = //
      "<html>\n" + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>HTML</title>\n" + "</head>\n"
          + "<body>\n" + "`1`\n" + "</body>\n" + "</html>"; //

  public static final double DEFAULT_CHOP_DELTA = 1.0e-10;

  /**
   * Used to parse Rubi files. See <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - Symbolic
   * Integration Rules</a>
   */
  public static boolean RUBI_CONVERT_SYMBOLS = false;

  public static String getVersion() {
    try (InputStream resourceAsStream = Config.class.getResourceAsStream("/version.properties")) {
      if (resourceAsStream != null) {
        Properties prop = new Properties();
        prop.load(resourceAsStream);
        return prop.getProperty("version");
      }
    } catch (IOException e) {
      // don't use a Logger in Config startup methods. Print the message to console instead:
      System.out.println("Config.getVersion() failed: " + e.getMessage());
    }
    return null;
  }

  /** A trie builder for mapping int[] sequences to IExpr. */
  public static final TrieBuilder<int[], IExpr, ArrayList<IExpr>> TRIE_INT2EXPR_BUILDER =
      new TrieBuilder<int[], IExpr, ArrayList<IExpr>>(TrieSequencerIntArray.INSTANCE,
          TrieMatch.EXACT, () -> new ArrayList<IExpr>(), (IExpr) null, false);

  /** A trie builder for mapping strings to IExpr. */
  public static final TrieBuilder<String, IExpr, ArrayList<IExpr>> TRIE_STRING2EXPR_BUILDER =
      TrieBuilder.create();

  /** A trie builder for mapping strings to ISymbol. */
  public static final TrieBuilder<String, ISymbol, ArrayList<ISymbol>> TRIE_STRING2SYMBOL_BUILDER =
      TrieBuilder.create();

  /** A trie builder for mapping strings to IStringX. */
  public static final TrieBuilder<String, IStringX, ArrayList<IStringX>> TRIE_STRING2STRINGX_BUILDER =
      TrieBuilder.create();

  /** A trie builder for mapping strings to IPattern. */
  public static final TrieBuilder<String, IPattern, ArrayList<IPattern>> TRIE_STRING2PATTERN_BUILDER =
      TrieBuilder.create();

  /** A trie builder for mapping strings to IPatternSequence. */
  public static final TrieBuilder<String, IPatternSequence, ArrayList<IPatternSequence>> TRIE_STRING2PATTERNSEQUENCE_BUILDER =
      TrieBuilder.create();

  public static int DEFAULT_RECURSION_LIMIT = 512;

  public static int DEFAULT_ITERATION_LIMIT = 1000;

  /** Global switch to make all symbols unprotected if set to {@link ISymbol#NOATTRIBUTE} */
  public static int BUILTIN_PROTECTED = ISymbol.PROTECTED;

  /**
   * Allow to control rounding mode used in Symja, dependent libraries may not allow to change
   * rounding mode
   */
  public static RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

  /**
   * The default invalid integer value, defined as {@link Integer#MIN_VALUE}.
   * 
   * @see IExpr#toIntDefault()
   * @see F#isPresent(int)
   * @see F#isNotPresent(int)
   */
  public static final int INVALID_INT = Integer.MIN_VALUE;

  /**
   * The default invalid long value, defined as {@link Long#MIN_VALUE}.
   * 
   * @see IExpr#toLongDefault()
   * @see F#isPresent(long)
   * @see F#isNotPresent(long)
   */
  public static final long INVALID_LONG = Long.MIN_VALUE;

  /** Global dynamic classloader */
  public static ClassLoader URL_CLASS_LOADER = null;

  /**
   * Set <code>$ScriptCommandLine</code> from a raw argument vector.
   *
   * <p>
   * The list used to start with <code>java.home</code>, which does not belong there:
   * <code>$ScriptCommandLine</code> starts with the name of the script. Prefer
   * {@link #setScriptCommandLine(String, java.util.List)}, which builds the documented form
   * from the script name and the arguments meant for it, rather than from everything on the
   * command line.
   */
  public static void setScriptCommandLine(final String[] args) {
    IASTAppendable commandLine = F.ListAlloc(args.length);
    for (int i = 0; i < args.length; i++) {
      commandLine.append(args[i]);
    }
    SCRIPT_COMMAND_LINE = commandLine;
  }

  /**
   * Set <code>$ScriptCommandLine</code> to the documented form: the name of the script
   * followed by the arguments passed to it. The options that started the interpreter are not
   * part of it, so a script sees the same list however it was launched - through
   * <code>-file</code> or through a <code>#!</code> line.
   *
   * @param scriptName the script being run, or <code>null</code> when no script is running,
   *        which makes <code>$ScriptCommandLine</code> the empty list
   * @param arguments the arguments meant for the script
   */
  public static void setScriptCommandLine(final String scriptName,
      final java.util.List<String> arguments) {
    if (scriptName == null) {
      SCRIPT_COMMAND_LINE = F.CEmptyList;
      return;
    }
    IASTAppendable commandLine = F.ListAlloc(arguments.size() + 1);
    commandLine.append(scriptName);
    for (String argument : arguments) {
      commandLine.append(argument);
    }
    SCRIPT_COMMAND_LINE = commandLine;
  }

  /**
   * Contains the executable followed by the arguments the process was started with, the way
   * <code>$CommandLine</code> is defined in the Wolfram Language. <code>null</code> when Symja was
   * not started from a console app, in which case <code>$CommandLine</code> is the empty list.
   *
   * <p>
   * Unlike {@link #SCRIPT_COMMAND_LINE} the first element is the interpreter, not the script, so
   * <code>First[$CommandLine]</code> can be used to start another copy of it.
   */
  public static IAST COMMAND_LINE = null;

  /**
   * The command that starts another copy of this interpreter, as a list of process arguments. For a
   * native binary this is the binary itself; on a JVM it is the java executable, the class path and
   * the main class, because the binary name alone would not be runnable.
   */
  public static java.util.List<String> RELAUNCH_COMMAND = null;

  /**
   * <code>true</code> while a script is run the way <code>wolframscript</code> runs one, which is
   * what <code>-file</code>, <code>-script</code>, a <code>#!</code> line, a program on stdin and
   * <code>-wstp</code> all do.
   *
   * <p>
   * In that mode <code>$VersionNumber</code> reports the Wolfram Language version Symja is
   * compatible with rather than Symja's own, because scripts gate features on it - a script that
   * reads <code>If[$VersionNumber &lt; 14.1, Exit[0]]</code> would otherwise refuse to run at all.
   * <code>$Version</code> always names Symja, so a script that wants to know what it is really
   * talking to can still find out.
   */
  public static boolean WOLFRAMSCRIPT_COMPAT = false;

  /**
   * The Wolfram Language version reported by <code>$VersionNumber</code> in
   * {@link #WOLFRAMSCRIPT_COMPAT} mode.
   */
  public static final double WOLFRAM_LANGUAGE_VERSION = 14.1;

  /**
   * <code>true</code> when Symja owns the process it runs in, so that <code>Exit[]</code> and
   * <code>Quit[]</code> can end it with an exit code. Embedded in another application they must not
   * do that, and reset the evaluation engine instead.
   */
  public static boolean PROCESS_MODE = false;

  /**
   * Enable the built-ins that reach outside the process: TCP sockets, external processes and the
   * links between two kernels. Separate from {@link #FILESYSTEM_ENABLED} because reading a file and
   * opening a port are different things to allow, and an embedded Symja usually wants neither.
   */
  public static boolean OS_ACCESS_ENABLED = false;

  public static boolean isOSAccessEnabled(EvalEngine engine) {
    return OS_ACCESS_ENABLED;
  }

  // load version string from MAVEN
  public static String VERSION = "?";

  /**
   * How long one run of {@link #calibrationWorkload()} takes, in nanoseconds, on the machine the
   * wall-clock budgets were tuned on.
   *
   * <p>
   * This is the whole of what "the baseline machine" means. An embedder which would rather
   * calibrate against its own reference machine measures that machine once, with
   * {@link #measureCalibrationWorkload()}, and assigns the result here before asking for a
   * calibration. A value of zero or less switches the calibration off: it then answers
   * <code>1.0</code>, which is the same as not calibrating at all.
   *
   * <p>
   * Measured 2026-09-06 on an Apple M5 with a Java 26 virtual machine: the first calibration of a
   * freshly started machine took between 9.9 and 10.3 milliseconds, and the value below is the
   * middle of that. A machine which needs twice as long is given twice the budget everywhere.
   */
  public static long TIME_SCALE_REFERENCE_NANOS = 10_100_000L;

  /** Narrowest and widest factor a measurement is allowed to produce. */
  private static final double MIN_CALIBRATED_SCALE = 0.25;

  private static final double MAX_CALIBRATED_SCALE = 20.0;

  /** How often the workload runs before the measurement starts, to let it be compiled. */
  private static final int CALIBRATION_WARMUP_RUNS = 3;

  /** How often the workload is measured; the fastest run is the one which counts. */
  private static final int CALIBRATION_MEASURED_RUNS = 12;

  /** The result of the one calibration this virtual machine performs, or <code>null</code>. */
  private static volatile Double calibratedTimeScale = null;

  /**
   * Measures this machine and answers how much longer it needs than the machine the wall-clock
   * evaluation budgets were tuned on.
   *
   * <p>
   * The answer is what {@link MachineProfile} multiplies every budget by: <code>1.0</code> for a
   * machine as fast as the baseline, <code>3.0</code> for one which needs three times as long. It
   * is clamped to a sane range and rounded to two decimals, so that two calibrations of the same
   * machine agree with each other, and it is measured only once - the runs afterwards answer from
   * the remembered value and cost nothing.
   *
   * <p>
   * A measurement is a weaker statement than it looks. It describes the machine as it was during
   * those few hundred milliseconds, which on a shared build host or a laptop which is thermally
   * throttled is not the machine the evaluation will run on. That is why this is never consulted
   * unless it is asked for, by <code>-Dsymja.machineProfile=auto</code> or by
   * {@link #autoCalibrateTimeScale()}.
   *
   * <p>
   * The measurement itself does not touch the evaluation engine - it cannot, since the budgets are
   * read long before an engine exists - so it is arithmetic on big integers and traffic through a
   * hash table, which is where Symja spends most of its time in any case.
   *
   * @return the factor for this machine, or <code>1.0</code> if
   *         {@link #TIME_SCALE_REFERENCE_NANOS} says the baseline is unknown
   */
  public static double calibrateTimeScale() {
    Double remembered = calibratedTimeScale;
    if (remembered != null) {
      return remembered.doubleValue();
    }
    synchronized (Config.class) {
      remembered = calibratedTimeScale;
      if (remembered == null) {
        double factor = 1.0;
        long reference = TIME_SCALE_REFERENCE_NANOS;
        if (reference > 0L) {
          double measured = (double) measureCalibrationWorkload() / (double) reference;
          if (measured < MIN_CALIBRATED_SCALE) {
            measured = MIN_CALIBRATED_SCALE;
          } else if (measured > MAX_CALIBRATED_SCALE) {
            measured = MAX_CALIBRATED_SCALE;
          }
          factor = Math.round(measured * 100.0) / 100.0;
        }
        remembered = Double.valueOf(factor);
        calibratedTimeScale = remembered;
      }
    }
    return remembered.doubleValue();
  }

  /**
   * Measures this machine with {@link #calibrateTimeScale()} and uses the result for every
   * wall-clock evaluation budget from now on.
   *
   * <p>
   * Call it before the first <code>Integrate()</code>. The limit which the Rubi rules use
   * internally is bound to a symbol when those rules are loaded, and a factor which arrives after
   * that reaches every budget except that one.
   *
   * @return the factor which was applied
   */
  public static double autoCalibrateTimeScale() {
    double factor = calibrateTimeScale();
    MachineProfile.setScale(factor);
    return factor;
  }

  /**
   * How long one run of {@link #calibrationWorkload()} takes on this machine, in nanoseconds.
   *
   * <p>
   * The workload runs a few times unmeasured first, so that what is timed is compiled code rather
   * than the interpreter, and the fastest of the timed runs is the answer: the shortest run is the
   * one which was least interrupted by the rest of the machine, and taking an average would report
   * the interruptions instead of the speed.
   *
   * <p>
   * Use this to measure a new baseline machine, and put the result in
   * {@link #TIME_SCALE_REFERENCE_NANOS}.
   */
  public static long measureCalibrationWorkload() {
    long sink = 0L;
    for (int i = 0; i < CALIBRATION_WARMUP_RUNS; i++) {
      sink += calibrationWorkload();
    }
    long fastest = Long.MAX_VALUE;
    for (int i = 0; i < CALIBRATION_MEASURED_RUNS; i++) {
      long start = System.nanoTime();
      sink += calibrationWorkload();
      long elapsed = System.nanoTime() - start;
      if (elapsed < fastest) {
        fastest = elapsed;
      }
    }
    // consume the accumulated value, so that the work cannot be compiled away as unused
    if (sink == Long.MIN_VALUE) {
      System.err.println("Config.measureCalibrationWorkload: " + sink);
    }
    return fastest;
  }

  /**
   * A fixed amount of the kind of work Symja does, whose duration says how fast this machine is.
   *
   * <p>
   * Two kinds, in the proportion they tend to appear in: arithmetic on big integers, which is what
   * the polynomial and number theory code spends its time on, and building small objects and
   * looking them up in a hash table, which is what pattern matching does. Neither part depends on
   * anything which has to be initialized first, and the whole of it is deterministic, so the same
   * machine measures the same on every run.
   *
   * <p>
   * The sizes are chosen so that one run takes roughly ten milliseconds on the baseline machine.
   * Shorter than that and the measurement says more about how far the compiler has got than about
   * the machine; much longer and asking for a calibration becomes something one notices.
   *
   * @return a value which depends on everything the method computed, so that none of it can be
   *         optimized away
   */
  private static long calibrationWorkload() {
    java.math.BigInteger accumulator = java.math.BigInteger.ONE;
    for (int i = 2; i < 4200; i++) {
      accumulator = accumulator.multiply(java.math.BigInteger.valueOf(i));
    }
    java.math.BigInteger modulus = java.math.BigInteger.valueOf(1000003L);
    java.math.BigInteger residue = accumulator.mod(modulus);
    java.math.BigInteger power = residue.modPow(java.math.BigInteger.valueOf(65537L), modulus);
    long sink = power.longValue() + accumulator.bitLength();

    HashMap<String, long[]> table = new HashMap<String, long[]>();
    for (int i = 0; i < 600000; i++) {
      String key = "sym" + (i % 16384);
      long[] counter = table.get(key);
      if (counter == null) {
        counter = new long[] {0L};
        table.put(key, counter);
      }
      counter[0] += i;
    }
    for (long[] counter : table.values()) {
      sink += counter[0];
    }
    return sink;
  }
}
