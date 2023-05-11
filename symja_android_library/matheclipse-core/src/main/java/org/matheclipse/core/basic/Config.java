package org.matheclipse.core.basic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.ApfloatContext;
import org.hipparchus.util.Precision;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
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
  private static final Logger LOGGER = LogManager.getLogger();

  /** Show the stack trace, if an exception is thrown in evaluation */
  public static final boolean SHOW_STACKTRACE = false;

  /** Enable JSFiddle in JavaScript IFRAME output */
  public static boolean DISPLAY_JSFIDDLE_BUTTON = true;

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
      "\nCopyright (C) 2009 - 2023 - the Symja team.\n" //
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
   * Maximum memory block size for the {@link ApfloatContext}
   */
  public static final long MAX_APFLOAT_MEMORY_BLOCKSIZE = 1_000_000;

  /**
   * Maximum processors for the {@link ApfloatContext}
   */
  public static final int MAX_APFLOAT_PROCESSORS = 1;

  /**
   * Maximum number for the leaf count of an expression so that <code>PossibleZeroQ()</code> will
   * try a factoring. Has to be an int value greater 0.
   */
  public static final int MAX_POSSIBLE_ZERO_LEAFCOUNT = 1000;

  /**
   * Maximum number for the leaf count of an expression so that <code>Simplify()</code> will try
   * calling <code>Factor()</code>.
   */
  public static final int MAX_SIMPLIFY_FACTOR_LEAFCOUNT = 100;

  /**
   * Maximum number for the leaf count of an expression so that <code>Simplify()</code> will try
   * calling <code>Apart()</code>.
   */
  public static final int MAX_SIMPLIFY_APART_LEAFCOUNT = 100;

  /**
   * Maximum number for the leaf count of an expression so that <code>Simplify()</code> will try
   * calling <code>Together()</code>.
   */
  public static final int MAX_SIMPLIFY_TOGETHER_LEAFCOUNT = 65;

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

  /** Maximum number of loop runs in some Symja functions */
  public static long MAX_LOOP_COUNT = Long.MAX_VALUE;

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

  /** Switch debug mode on/off */
  public static final boolean DEBUG = false;

  public static boolean TRACE_REWRITE_RULE = false;

  /** Set to true if in fuzz testing mode */
  public static boolean FUZZ_TESTING = false;

  /** Set to true if the fuzzy parser should be used in the free form Symja API */
  public static boolean FUZZY_PARSER = false;

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

  /** The real which added to 1.0 gives the next double value greater than 1.0 */
  public static double MACHINE_EPSILON = Math.nextUp(1.0) - 1.0;

  /**
   * Replace <code>double</code> values in root algorithms by 0 if they are below this tolerance.
   * Assume <code>double</code> values in <code>PossibleZeroQ</code> to be 0 if they are below this
   * tolerance.
   */
  public static double DEFAULT_ROOTS_CHOP_DELTA = 1.0e-5;

  /**
   * Tolerance used in special function algorithms ported from
   * <a href="https://github.com/paulmasson/math">math.js</a> and in the JavaScript based plot
   * functions.
   */
  public static double SPECIAL_FUNCTIONS_TOLERANCE = 1.0e-10;

  /** Define the recursion limit for <code>Integrate#integrateByParts()</code> method. */
  public static int INTEGRATE_BY_PARTS_RECURSION_LIMIT = 10;

  /** Define the recursion limit for <code>Integrate#integrateByRubiRules()</code> method. */
  public static int INTEGRATE_RUBI_RULES_RECURSION_LIMIT = 100;

  /** Define the Rubi time limit for the <code>TimeConstrained()</code> function. */
  public static int INTEGRATE_RUBI_TIMELIMIT = 8;

  /** Define the recursion limit for <code>Limit#lHospitalesRule()</code> method. */
  public static int LIMIT_LHOSPITAL_RECURSION_LIMIT = 128;

  /**
   * Flag for thread usage.
   *
   * <p>
   * <b>Note:</b> introduced because Google app engine does not support threads.
   *
   * @see edu.jas.ufd.GCDFactory#getProxy(edu.jas.structure.RingFactory)
   */
  public static boolean JAS_NO_THREADS = false;

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
  public static java.util.concurrent.ThreadFactory THREAD_FACTORY = null;

  /**
   * Use <code>Num</code> objects for numeric calculations up to 16 digits precision.
   *
   * @deprecated use {@link ParserConfig#MACHINE_PRECISION}
   */
  @Deprecated
  public static final long MACHINE_PRECISION = ParserConfig.MACHINE_PRECISION;

  /** The maximum precision which could be requested from a user for numerical calculations. */
  public static long MAX_PRECISION_APFLOAT = Short.MAX_VALUE;

  /** Print trigonometric functions in lower case characters. */
  public static boolean MATHML_TRIG_LOWERCASE = true;

  /**
   * Set this parameter to false if you would like a <code>\\begin{pmatrix} ... \\end{pmatrix}
   * </code> output instead of <code>\left( \begin{array} ... \end{array} \right)</code> in TeXForm.
   */
  public static boolean MATRIX_TEXFORM = true;

  /**
   * Enable tests and functions which use the local files. Don't use <code>final</code> here because
   * of grpc interface.
   */
  public static boolean FILESYSTEM_ENABLED = false;

  public static boolean isFileSystemEnabled(EvalEngine engine) {
    return FILESYSTEM_ENABLED || engine.isFileSystemEnabled();
  }

  /** Do time consuming JUnit tests. For example for <code>FactorInteger</code> function */
  public static boolean EXPENSIVE_JUNIT_TESTS = false;

  /** Default package mode with which the EvalEngines initially can be started */
  public static boolean PACKAGE_MODE = true;

  public static Consumer<IExpr> PRINT_OUT = x -> {
  };

  /** The algorithm which should be used for the factorization of integer numbers. */
  // public static Function<IInteger, IAST> FACTOR_INTEGER = Primality::factorIInteger;
  public static IPrimality PRIME_FACTORS = new Primality();

  /** Use JavaScript libraries for the <code>Manipulate()</code> function */
  public static boolean USE_MANIPULATE_JS = true;

  /** Use visjs.org JavaScript library for visualizing graph theory objects */
  public static boolean USE_VISJS = false;

  public static final String TRACEFORM_PAGE = //
      "<!doctype html>\n" + "<html>\n" + "  <head>\n" + "       <meta charset=\"utf-8\">\n" + //
          "     <title>JSLists - Very simple nested list [Example 1]</title>\n" + "     \n"
          + "<style>\n" + "*, *:before, *:after {box-sizing: border-box;}\n"
          + "ul, ol {margin: 0; padding: 0;}\n" + "li {list-style: none; line-height: 1.6rem;}\n"
          + "\n" + "/* List styling */\n" + ".jslists{\n" + "   font-size: 1.3rem;\n"
          + "   font-family: Arial, Helvetica, sans-serif;\n" + "}\n"
          + ".jslist-ul, .jslist-ol, .jslist-li {margin-left: 12px;}        /* Unordered lists */\n"
          + ".jsl-collapsed {display: none;}\n" + ".jsl-list-closed {\n" + "    float: left;\n"
          + "   clear: both;\n" + " margin: 2px 4px 2px 0px;\n" + " width: 18px;\n"
          + "   height: 18px;\n" + "    cursor: pointer;\n"
          + "   background-image: url('data:image/svg+xml;utf8,<svg aria-hidden=\"true\" data-prefix=\"far\" data-icon=\"plus-square\" class=\"svg-inline--fa fa-plus-square fa-w-14\" role=\"img\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 448 512\"><path fill=\"currentColor\" d=\"M352 240v32c0 6.6-5.4 12-12 12h-88v88c0 6.6-5.4 12-12 12h-32c-6.6 0-12-5.4-12-12v-88h-88c-6.6 0-12-5.4-12-12v-32c0-6.6 5.4-12 12-12h88v-88c0-6.6 5.4-12 12-12h32c6.6 0 12 5.4 12 12v88h88c6.6 0 12 5.4 12 12zm96-160v352c0 26.5-21.5 48-48 48H48c-26.5 0-48-21.5-48-48V80c0-26.5 21.5-48 48-48h352c26.5 0 48 21.5 48 48zm-48 346V86c0-3.3-2.7-6-6-6H54c-3.3 0-6 2.7-6 6v340c0 3.3 2.7 6 6 6h340c3.3 0 6-2.7 6-6z\"></path></svg>');\n"
          + "    background-repeat: no-repeat;\n" + "    background-position: center; \n" + "}\n"
          + ".jsl-open {display: block;}\n"
          + ".jsl-list-open {background-image: url('data:image/svg+xml;utf8,<svg aria-hidden=\"true\" data-prefix=\"far\" data-icon=\"minus-square\" class=\"svg-inline--fa fa-minus-square fa-w-14\" role=\"img\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 448 512\"><path fill=\"currentColor\" d=\"M108 284c-6.6 0-12-5.4-12-12v-32c0-6.6 5.4-12 12-12h232c6.6 0 12 5.4 12 12v32c0 6.6-5.4 12-12 12H108zM448 80v352c0 26.5-21.5 48-48 48H48c-26.5 0-48-21.5-48-48V80c0-26.5 21.5-48 48-48h352c26.5 0 48 21.5 48 48zm-48 346V86c0-3.3-2.7-6-6-6H54c-3.3 0-6 2.7-6 6v340c0 3.3 2.7 6 6 6h340c3.3 0 6-2.7 6-6z\"></path></svg>');}\n"
          + "\n" + "            html, body {\n" + "             width: 100%;\n"
          + "               height: 100%;\n" + "                margin: 0;\n"
          + "               padding: 0;\n" + "          }\n" + "            body {\n"
          + "               font-family: Arial, Helvetica, sans-serif;\n"
          + "               font-size: 16px;\n" + "         }\n" + "            header {\n"
          + "               position: absolute;\n" + "              top: 0;\n"
          + "               left: 0;\n" + "             width: 100%;\n"
          + "               height: 90px;\n"
          + "               background-color: rgb(156, 158, 160);\n"
          + "               padding-left: 18px;\n" + "          }\n" + "           header > div {\n"
          + "               position: relative;\n" + "               display: inline-block;\n"
          + "               top: 50%;\n" + "               transform: translateY(-50%);\n"
          + "             font-size: 3.4rem;\n" + "               font-weight: 900;\n"
          + "            }\n" + "            main {\n" + "               position: absolute;\n"
          + "              top: 90px;\n" + "               height: calc(100vh - 90px);\n"
          + "          }\n" + "           main > div:nth-child(1) {\n"
          + "                padding: 16px;\n" + "           }\n"
          + "            main > div:nth-child(2) {\n" + "               padding: 18px;\n"
          + "</style>\n" + "    </head>\n" + "  <body> \n"
          + "               <ul id='traceform' class='jslists'>\n"
          + "                   <li>TraceForm\n" + "`1`\n" + //
          "                 </li>\n" + "                </ul>\n" + "            </div>\n" + " \n"
          + "       <script>\n" + "var blackCircle = '&#9679; ';\n"
          + "var openCircle = '&#9678; ';\n" + "\n" + "(function() {\n" + " \"use strict\";\n"
          + "    function define_JSLists() {\n" + "     var JSLists = {};\n" + "\n"
          + "       var JSLists_Error = function(error, alertType) {\n"
          + "           console.log(error);\n" + "      }\n" + "        var getUl = function(){\n"
          + "           return document.getElementsByTagName(\"UL\");\n" + "        };\n" + "\n"
          + "       var getOl = function(){\n"
          + "           return document.getElementsByTagName(\"OL\");\n" + "        };\n" + "\n"
          + "       var getAllLists = function(){\n"
          + "           var olLists = Array.prototype.slice.call(document.getElementsByTagName(\"UL\")),\n"
          + "               ulLists = Array.prototype.slice.call(document.getElementsByTagName(\"OL\"))\n"
          + "           var gLists = olLists.concat(ulLists);\n" + "            return gLists;\n"
          + "       }\n" + "\n" + "     JSLists.searchList = function(listId, searchTerm) {\n"
          + "           var i, j, lilNodes, liItems = document.getElementsByTagName(\"LI\");\n"
          + "           for(i=0; i<liItems.length; i++) {\n"
          + "                if(liItems[i].hasChildNodes()) {\n"
          + "                    for(j=0; j<liItems[i].childNodes.length; j++) {\n"
          + "                        if(liItems[i].childNodes[j].innerHTML == searchTerm) {\n"
          + "                           //?????\n" + "                        }\n"
          + "                    }\n" + "                }\n" + "           }\n" + "        }\n"
          + "\n" + "        JSLists.collapseAll = function(listId) {\n"
          + "           var i, ulLists = document.getElementsByTagName(\"UL\");\n"
          + "           for(i=0; i<ulLists.length; i++) {\n"
          + "               if(ulLists[i].className == \"jsl-collapsed\") {\n"
          + "                    console.log(ulLists[i].className + '\\n' + '@');\n"
          + "               }\n" + "            };\n" + "       };\n" + "\n"
          + "       JSLists.openAll = function(listId){\n"
          + "           var i, olLists = Array.prototype.slice.call(document.getElementsByTagName(\"UL\")),\n"
          + "               ulLists = Array.prototype.slice.call(document.getElementsByTagName(\"OL\"))\n"
          + "           var gLists = olLists.concat(ulLists);\n" + "\n"
          + "           for(i=1; i<gLists.length; i++) {\n"
          + "               gLists[i].setAttribute('class', 'jsl-open');\n" + "         };\n"
          + "       };\n" + "\n" + "        JSLists.padUnorderedLists = function(listId) {\n"
          + "           var i, listItems = document.getElementById(listId).getElementsByTagName(\"UL\");\n"
          + "           for(i=0; i<listItems.length; i++) {\n"
          + "               listItems[i].classList.add('jslist-ul');\n" + "         }\n"
          + "       };\n" + "\n" + "        JSLists.padOrderedLists = function(listId) {\n"
          + "           var i, listItems = document.getElementById(listId).getElementsByTagName(\"UL\");\n"
          + "           for(i=0; i<listItems.length; i++) {\n"
          + "               listItems[i].classList.add('jslist-ol');\n" + "         }\n"
          + "       };\n" + "\n" + "        JSLists.padLists = function(listId) {\n"
          + "           var i, listItems = document.getElementById(listId).getElementsByTagName(\"LI\");\n"
          + "           for(i=0; i<listItems.length; i++) {\n"
          + "               if(listItems[i].childNodes[0].className != \"jsl-collapsed-arrow\") {\n"
          + "                   listItems[i].classList.add('jslist-li');\n" + "             }\n"
          + "           }\n" + "            for(i=1; i<listItems.length; i++) {\n"
          + "               // console.log(listItems[i].childNodes.length);\n"
          + "               if(listItems[i].classList = \"jslist-li\" && listItems[i].childNodes.length < 2) {\n"
          + "                   listItems[i].innerHTML = blackCircle + listItems[i].innerHTML\n"
          + "               }\n" + "            }\n"
          + "           this.padUnorderedLists(listId);\n"
          + "           this.padOrderedLists(listId);\n" + "        };\n" + "\n"
          + "        JSLists.createTree = function(listId, bulletPoint) {\n"
          + "           document.getElementById(listId).style.display = \"none;\"\n"
          + "           var i, j, curElem, ulCount, olCount, listItems = document.getElementById(listId).getElementsByTagName('LI'); //this should be the main parent\n"
          + "           for(i=0; i<listItems.length; i++) {\n"
          + "               if(listItems[i].id.length > 0) {\n"
          + "                   curElem = document.getElementById(listItems[i].id);\n"
          + "                    ulCount = document.getElementById(listItems[i].id).getElementsByTagName(\"UL\");\n"
          + "                    if(ulCount.length > 0){\n"
          + "                        for(j=0; j<ulCount.length; j++) {\n"
          + "                            if(ulCount[j].nodeName == \"UL\") {\n"
          + "                                break;\n" + "                            }\n"
          + "                        }\n"
          + "                        ulCount[j].setAttribute('class', 'jsl-collapsed');\n"
          + "                        var tglDiv = document.createElement(\"div\");\n"
          + "                        tglDiv.setAttribute('class', 'jsl-list-closed');\n"
          + "                        tglDiv.setAttribute(\"id\", listItems[i].id + i +'_tgl');\n"
          + "                        curElem.insertBefore(tglDiv, curElem.childNodes[0]);\n" + "\n"
          + "                        document.getElementById(listItems[i].id + i +'_tgl').addEventListener('click', function(e) {\n"
          + "                            document.getElementById(e.target.id).classList.toggle('jsl-list-open');\n"
          + "                            document.getElementById(e.target.id).parentElement.lastElementChild.classList.toggle('jsl-open');\n"
          + "                            e.stopPropagation();\n"
          + "                        },true);\n" + "                    }\n"
          + "                } else {\n"
          + "                   listItems[i].setAttribute(\"id\", listId+\"tmp\"+i);\n"
          + "                   curElem = document.getElementById(listId+\"tmp\"+i);\n"
          + "                   ulCount = document.getElementById(listItems[i].id).getElementsByTagName(\"UL\");\n"
          + "\n"
          + "                   if(ulCount.length > 0) { //There is a nested UL in this LI element, now find the position of the UL\n"
          + "                       for(j=0; j<ulCount.length; j++) {\n"
          + "                           if(ulCount[j].nodeName == \"UL\") {\n"
          + "                               break; //Multiple UL's? //Set class collapseAll here\n"
          + "                           }\n" + "                        }\n"
          + "                       ulCount[j].setAttribute('class', 'jsl-collapsed');\n"
          + "                       var tglDiv = document.createElement(\"div\");\n"
          + "                       tglDiv.setAttribute('class', 'jsl-list-closed');\n"
          + "                       tglDiv.setAttribute(\"id\", listItems[i].id + i +'_tgl');\n"
          + "                       curElem.insertBefore(tglDiv, curElem.childNodes[0]);\n" + "\n"
          + "                       document.getElementById(listItems[i].id + i +'_tgl').addEventListener('click', function(e){\n"
          + "                           document.getElementById(e.target.id).classList.toggle('jsl-list-open');\n"
          + "                           document.getElementById(e.target.id).parentElement.lastElementChild.classList.toggle('jsl-open');\n"
          + "                           e.stopPropagation();\n" + "                     },true);\n"
          + "                   }\n" + "                    listItems[i].removeAttribute(\"id\");\n"
          + "               }\n" + "            }\n" + "            setTimeout(function() {\n"
          + "               document.getElementById(listId).style.display = \"block;\"\n"
          + "           }, 50); // stops FOUC!\n" + "           this.padLists(listId);\n"
          + "       };\n" + "\n"
          + "       // JSLists.applyToList = function(listId, listType, applyIcons, applyTheme, themeNumber){\n"
          + "       //Check the params here\n" + "      // does the id exist?\n"
          + "       JSLists.applyToList = function(listId, bulletPoint) {\n"
          + "            this.createTree(listId, \"UL\");\n" + "        };\n"
          + "   return JSLists;\n" + "    }\n" + "\n"
          + "   //define the JSLists library in the global namespace if it doesn't already exist\n"
          + "   if(typeof(JSLists) === 'undefined') {\n"
          + "       window.JSLists = define_JSLists();\n" + "   }else{\n"
          + "       console.log(\"JSLists already defined.\");\n" + "   }\n" + "})();       \n"
          + "       </script>\n" + "        <script> \n"
          + "           JSLists.createTree(\"traceform\");\n" + "       </script>\n"
          + "   </body>\n" + "</html>"; //

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
    try (InputStream resourceAsStream = Config.class
        .getResourceAsStream("/META-INF/maven/org.matheclipse/matheclipse-core/pom.properties")) {
      if (resourceAsStream != null) {
        Properties prop = new Properties();
        prop.load(resourceAsStream);
        return prop.getProperty("version");
      }
    } catch (IOException e) {
      LOGGER.error("Config.getVersion() failed", e);
    }
    return "0.0.0";
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

  public static final int DEFAULT_RECURSION_LIMIT = 512;

  public static final int DEFAULT_ITERATION_LIMIT = 1000;

  /** Global switch to make all symbols unprotected if set to {@link ISymbol#NOATTRIBUTE} */
  public static int BUILTIN_PROTECTED = ISymbol.PROTECTED;

  /** Global dynamic classloader */
  public static ClassLoader URL_CLASS_LOADER = null;

  public static void setScriptCommandLine(final String[] args) {
    IASTAppendable commandLine = F.ListAlloc(args.length + 1);
    String javaHome = System.getProperty("java.home");
    if (javaHome != null) {
      commandLine.append(javaHome);
    } else {
      commandLine.append("");
    }
    for (int i = 0; i < args.length; i++) {
      commandLine.append(args[i]);
    }
    LOGGER.info(commandLine);
    SCRIPT_COMMAND_LINE = commandLine;
  }
}
