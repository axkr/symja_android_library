package org.matheclipse.core.eval;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import org.apache.logging.log4j.Level;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.Programming;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFastFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.AbstractAST.NILPointer;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.OptionsPattern;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.integrate.rubi.UtilityFunctionCtors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IAtomicEvaluate;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.visit.ModuleReplaceAll;
import org.matheclipse.core.visit.VisitorReplaceEvalf;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The main evaluation algorithms for the Symja computer algebra system. A single <code>EvalEngine
 * </code> is associated with the current thread through a
 * <a href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
 */
@NotThreadSafe
public class EvalEngine implements Serializable {

  // private static final Logger LOGGER = LogManager.getLogger();

  private static final IStringX EVALUATION_LOOP = StringX.valueOf("EvalLoop");

  public static class OptionsResult {
    public IAST result;
    public int argSize;
    public IExpr[] options;

    public OptionsResult(IAST ast, int argSize, IExpr[] options) {
      this.result = ast;
      this.argSize = argSize;
      this.options = options;
    }
  }
  /** Stack to manage the <code>OptionsPattern()</code> mappings for a pattern-matching rule. */
  private static class OptionsStack extends ArrayDeque<IdentityHashMap<ISymbol, IASTAppendable>> {

    private static final long serialVersionUID = 2720088062330091827L;

    public OptionsStack() {
      super();
    }

    public void push() {
      push(new IdentityHashMap<ISymbol, IASTAppendable>());
    }
  }

  /**
   * In computing, memoization or memoisation is an optimization technique used primarily to speed
   * up computer programs by storing the results of expensive function calls and returning the
   * cached result when the same inputs occur again.
   *
   * <p>
   * This cache is especially used for expensive functions like <code>FullSimplify, Factor,...
   * </code> to remember the results of the function call. It often also stores the <code>F.NIL
   * </code> result to indicate that a new evaluation of a function is unnecessary. See:
   * <a href="https://en.wikipedia.org/wiki/Memoization">Wikipedia - Memoization</a>
   */
  private transient final Cache<IAST, IExpr> globalASTCache =
      CacheBuilder.newBuilder().maximumSize(500).build();

  private transient final Cache<IExpr, Object> globalObjectCache =
      CacheBuilder.newBuilder().maximumSize(500).build();

  /** */
  private static final long serialVersionUID = 8402201556123198590L;

  /**
   * Cache for the Rubi integration rules evaluation
   */
  public transient Cache<IAST, IExpr> rubiASTCache = null;

  public transient Map<Object, IExpr> rememberMap = null;

  public static final boolean DEBUG = false;

  private static AtomicLong MODULE_COUNTER = new AtomicLong();

  private static final transient ThreadLocal<EvalEngine> INSTANCE = ThreadLocal.withInitial(
      () -> new EvalEngine("ThreadLocal", Config.DEFAULT_RECURSION_LIMIT, System.out, true));


  /**
   * Get the thread local evaluation engine instance
   *
   * @return the current {@link EvalEngine} for this thread.
   */
  public static EvalEngine get() {
    return INSTANCE.get();
  }

  /**
   * Same as {@link EvalEngine#isArbitraryMode()}, but as static method.
   * 
   * @return
   */
  public static boolean isApfloatMode() {
    return INSTANCE.get().isArbitraryMode();
  }

  /**
   * Check if the <code>ApfloatNum</code> number type should be used instead of the <code>Num</code>
   * type and the <code>ApcomplexNum</code> number type should be used instead of the <code>
   * ComplexNum</code> type for numeric evaluations.
   *
   * @param precision the given precision
   * @return <code>true</code> if the given precision is greater than <code>
   *     EvalEngine.DOUBLE_PRECISION</code>
   * @see ApfloatNum
   * @see ApcomplexNum
   */
  public static boolean isApfloat(long precision) {
    return precision > ParserConfig.MACHINE_PRECISION;
  }

  /**
   * Removes the current thread's value for the EvalEngine's thread-local variable.
   *
   * @see java.lang.ThreadLocal#remove()
   */
  public static void remove() {
    INSTANCE.remove();
  }

  /**
   * Set the thread local evaluation engine instance
   *
   * @param engine the evaluation engine
   */
  public static void set(final EvalEngine engine) {
    INSTANCE.set(engine);
  }

  /**
   * Set the thread local evaluation engine instance and reset the engines states (numeric mode
   * flags, recursion counter,...).
   *
   * <p>
   * Note: This method should be called before the parsing of a string expression.
   *
   * @param engine
   */
  public static void setReset(final EvalEngine engine) {
    INSTANCE.set(engine);
    engine.reset();
  }

  /** If set to <code>true</code> the current thread should stop evaluation; */
  transient volatile boolean fStopRequested;

  transient int fRecursionCounter;

  /**
   * The time in milliseconds the current <code>TimeConstrained</code> operation should stop. <code>
   * -1</code> is set for Infinity
   */
  transient long fTimeConstrainedMillis = -1;

  transient long fSeconds;
  /**
   * if <code>true</code> the engine evaluates in &quot;numeric&quot; mode, otherwise the engine
   * evaluates in &quot;symbolic&quot; mode.
   */
  transient boolean fNumericMode;

  /**
   * if <code>true</code> the engine evaluates &quot;F.Together(expr)&quot; in IExpr#times() method.
   */
  transient boolean fTogetherMode;

  /**
   * If <code>true</code> the engine does no simplification of &quot;negative {@link S#Plus}
   * expressions&quot; inside {@link S#Times} expressions in common subexpression determining.
   */
  transient boolean fNoSimplifyMode;

  transient boolean fEvalLHSMode;

  transient boolean fEvalRHSMode;

  /**
   * If <code>true</code> disable the automatic trig simplifying rules.
   */
  transient boolean fDisabledTrigRules;

  /** @see Config#isFileSystemEnabled() */
  transient boolean fFileSystemEnabled;


  transient String fSessionID;

  private transient String fMessageShortcut;

  private transient IdentityHashMap<IBuiltInSymbol, Integer> experimatalSymbols =
      new IdentityHashMap<IBuiltInSymbol, Integer>();

  /**
   * If <code>true</code> the engine evaluates in &quot;Trace()&quot; function mode.
   *
   * @see #evalTrace(IExpr, Predicate, IAST)
   */
  transient boolean fTraceMode;

  transient IAssumptions fAssumptions = null;

  transient IEvalStepListener fTraceStack = null;

  /** The stream for printing information messages */
  transient PrintStream fOutPrintStream = null;

  /** The stream for printing error messages */
  transient PrintStream fErrorPrintStream = null;

  transient ArrayDeque<ContextPath> fContextPathStack;

  transient ContextPath fContextPath;

  transient String f$Input = null;

  transient String f$InputFileName = null;

  /** The precision for numeric operations. */
  // protected transient long fNumericPrecision;

  protected transient FixedPrecisionApfloatHelper fApfloatHelper;

  protected transient FixedPrecisionApfloatHelper fApfloatHelperDouble;

  /** The number of significant figures in the output expression */
  protected int fSignificantFigures;

  protected int fRecursionLimit;

  protected int fIterationLimit;

  protected boolean fPackageMode = Config.PACKAGE_MODE;

  /**
   * If <code>true</code> this engine doesn't distinguish between lower and upper case identifiers,
   * with the exception of identifiers with length 1.
   */
  private boolean fRelaxedSyntax;

  private transient Random fRandom = new Random();

  private transient long fRandomSeed = 0L;

  /**
   * The reap list object associated to the most enclosing <code>Reap()</code> statement. The even
   * indices in <code>java.util.List</code> contain the tag defined in <code>Sow()</code>. If no tag
   * is defined in <code>Sow()</code> tag <code>F.None</code> is used. The odd indices in <code>
   * java.util.List</code> contain the associated reap list for the tag.
   */
  private transient List<IExpr> fReapList = null;

  public transient Set<ISymbol> fModifiedVariablesList;

  /**
   * Set interactive trace mode on or off. See functions <code>On()</code> and <code>Off()</code>.
   */
  private transient boolean fOnOffMode = false;

  /**
   * Set interactive trace output only to unique expression (e.g. to print an evaluated combination
   * only once).
   */
  private transient boolean fOnOffUnique = false;

  /**
   * If <code>fOnOffUnique==true</code> this map contains the unique expressions which occurred
   * during evaluation
   */
  transient Map<IExpr, IExpr> fOnOffUniqueMap = null;

  /**
   * If not null, this map contains the header symbols for which the interactive trace should be
   * printed.
   */
  transient Map<ISymbol, ISymbol> fOnOffMap = null;

  transient IdentityHashMap<IBuiltInSymbol, IExpr> fDollarSymbolMap = null;

  transient Deque<IExpr> fStack;

  /** The history list for the <code>Out[]</code> function. */
  private transient EvalHistory fEvalHistory = null;

  /** Contains possible options in a function call hierarchy */
  private transient OptionsStack fOptionsStack;

  /**
   * Contains the last result (&quot;answer&quot;) expression of this evaluation engine or <code>
   * null</code> if no answer is stored in the evaluation engine.
   */
  private transient IExpr fAnswer = null;

  /**
   * If <code>fCopiedEngine != null</code> the <code>fCopiedEngine</code> engine is used in function
   * <code>
   * TimeConstrained</code> to stop a thread after <code>T</code> seconds.
   */
  private transient EvalEngine fCopiedEngine = null;

  /**
   * Flag for disabling the appending of expressions to the history list for the <code>Out[]</code>
   * function.
   *
   * @see org.matheclipse.core.reflection.Out
   */
  private transient boolean fOutListDisabled = true;

  /**
   * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages
   * are shown during evaluation).
   *
   * @see org.matheclipse.core.builtin.function.Quiet
   */
  transient boolean fQuietMode = false;

  /**
   * A single <code>EvalEngine</code> is associated with the current thread through a
   * <a href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
   */
  public EvalEngine() {
    this(false);
  }

  /**
   * Constructor for an evaluation engine. A single <code>EvalEngine</code> is associated with the
   * current thread through a
   * <a href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
   *
   * @param relaxedSyntax if <code>true</code>, the parser doesn't distinguish between upper and
   *        lower case identifiers
   */
  public EvalEngine(boolean relaxedSyntax) {
    this("", Config.DEFAULT_RECURSION_LIMIT, System.out, relaxedSyntax);
  }

  /**
   * Constructor for an evaluation engine. A single <code>EvalEngine</code> is associated with the
   * current thread through a
   * <a href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
   *
   * @param sessionID an ID which uniquely identifies this session
   * @param recursionLimit the maximum allowed recursion limit (if set to zero, no limit will be
   *        checked)
   * @param iterationLimit the maximum allowed iteration limit (if set to zero, no limit will be
   *        checked)
   * @param outStream the output print stream
   * @param errorStream the print stream for error messages
   * @param relaxedSyntax if <code>true</code>, the parser doesn't distinguidh between upper and
   *        lower case identifiers
   */
  public EvalEngine(String sessionID, int recursionLimit, int iterationLimit, PrintStream outStream,
      PrintStream errorStream, boolean relaxedSyntax) {
    fRandomSeed = fRandom.nextLong();
    fRandom.setSeed(fRandomSeed);
    fSessionID = sessionID;
    fRecursionLimit = recursionLimit;
    fIterationLimit = iterationLimit;
    fOutPrintStream = outStream;
    fErrorPrintStream = errorStream == null ? outStream : errorStream;
    fRelaxedSyntax = relaxedSyntax;
    fOutListDisabled = true;
    init();
  }

  /**
   * Constructor for an evaluation engine. A single <code>EvalEngine</code> is associated with the
   * current thread through a
   * <a href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
   *
   * @param sessionID an ID which uniquely identifies this session
   * @param recursionLimit the maximum allowed recursion limit (if set to zero, no limit will be
   *        checked)
   * @param out the output print stream
   * @param relaxedSyntax if <code>true</code>, the parser doesn't distinguidh between upper and
   *        lower case identifiers
   */
  public EvalEngine(String sessionID, int recursionLimit, PrintStream out, boolean relaxedSyntax) {
    this(sessionID, recursionLimit, Config.DEFAULT_ITERATION_LIMIT, out, null, relaxedSyntax);
  }

  public EvalEngine(String sessionID, PrintStream out) {
    this(sessionID, Config.DEFAULT_RECURSION_LIMIT, out, false);
  }

  /**
   * For every evaluation, store the list of modified variables in an internal list.
   *
   * @param arg0
   * @return
   */
  public boolean addModifiedVariable(ISymbol arg0) {
    if (fModifiedVariablesList != null) {
      return fModifiedVariablesList.add(arg0);
    }
    return false;
  }

  /**
   * Add an expression to the <code>Out[]</code> list. To avoid memory leaks you can disable the
   * appending of expressions to the output history.
   *
   * @param inExpr the input expression
   * @param outExpr the output expression, which is the result of the <code>inExpr</code>
   */
  public void addInOut(IExpr inExpr, IExpr outExpr) {
    // remember the last result
    if (outExpr != null && outExpr.isPresent()) {
      fAnswer = outExpr;
    } else {
      fAnswer = S.Null;
    }
    ISymbol ans = F.symbol("$ans", Context.GLOBAL_CONTEXT_NAME, null, this);
    ans.assignValue(fAnswer, false);
    // ans.putDownRule(IPatternMatcher.SET, true, ans, fAnswer, false);
    if (fOutListDisabled) {
      return;
    }
    fEvalHistory.addInOut(inExpr, fAnswer);
  }

  /**
   * Add a single step to the currently defined trace stack and evaluate the <code>rewrittenExpr
   * </code> expression.
   *
   * @param inputExpr the input expression
   * @param rewrittenExpr the rewritten expression
   * @param list
   * @return
   */
  public IExpr addEvaluatedTraceStep(IExpr inputExpr, IExpr rewrittenExpr, IExpr... list) {
    if (fTraceMode && rewrittenExpr.isPresent()) {
      if (fTraceStack != null) {
        IASTAppendable listOfHints = F.ast(S.List, list.length + 1);
        listOfHints.appendAll(list, 0, list.length);
        fTraceStack.add(inputExpr, rewrittenExpr, getRecursionCounter(), -1, listOfHints);
        IExpr evaluatedResult = evaluate(rewrittenExpr);
        listOfHints.append(evaluatedResult);
        return evaluatedResult;
      }
      return evaluate(rewrittenExpr);
    }
    return rewrittenExpr;
  }

  /**
   * Add a single information step to the currently defined trace stack. The <code>inputExpr</code>
   * hasn't changed but an additional information was inserted.
   *
   * @param inputExpr the input expression
   * @param listOfHints this hints will be used in the eval trace listener
   * @see #setStepListener(IEvalStepListener)
   */
  public void addTraceInfoStep(IExpr inputExpr, IAST listOfHints) {
    if (fTraceMode && fTraceStack != null && inputExpr.isPresent()) {
      fTraceStack.add(inputExpr, inputExpr, getRecursionCounter(), -1, listOfHints);
    }
  }

  /**
   * Add a single step to the currently defined trace stack.
   *
   * @param inputExpr the input expression
   * @param rewrittenExpr the rewritten input expression
   * @param listOfHints this hints will be used in the eval trace listener
   * @see #setStepListener(IEvalStepListener)
   */
  public void addTraceStep(IExpr inputExpr, IExpr rewrittenExpr, IAST listOfHints) {
    if (fTraceMode && fTraceStack != null && rewrittenExpr.isPresent()) {
      fTraceStack.add(inputExpr, rewrittenExpr, getRecursionCounter(), -1, listOfHints);
    }
  }

  /**
   * Add a single step to the currently defined trace stack.
   *
   * @param inputExpr the input expression
   * @param rewrittenExpr the rewritten input expression
   * @param listOfHints list of hints parameters
   */
  public void addTraceStep(Supplier<IExpr> inputExpr, Supplier<IExpr> rewrittenExpr,
      IAST listOfHints) {
    if (fTraceMode && fTraceStack != null) {
      fTraceStack.add(inputExpr.get(), rewrittenExpr.get(), getRecursionCounter(), -1, listOfHints);
    }
  }

  public void addTraceStep(Supplier<IExpr> inputExpr, IExpr rewrittenExpr, IAST listOfHints) {
    if (fTraceMode && fTraceStack != null) {
      fTraceStack.add(inputExpr.get(), rewrittenExpr, getRecursionCounter(), -1, listOfHints);
    }
  }

  private void beginTrace(Predicate<IExpr> matcher) {
    setTraceMode(true);
    fTraceStack = new TraceStack(matcher);
  }

  /**
   * Copy this EvalEngine into a new EvalEngine. The copied engine is used in function <code>
   * TimeConstrained</code> to stop a thread after T seconds.
   *
   * @return
   */
  public synchronized EvalEngine copy() {
    EvalEngine engine = new EvalEngine();
    engine.rubiASTCache = null; // rememberASTCache;
    engine.rememberMap = rememberMap;
    engine.fAnswer = fAnswer;
    engine.fAssumptions = fAssumptions;
    engine.fContextPath = fContextPath.copy();
    engine.fErrorPrintStream = fErrorPrintStream;
    engine.fEvalLHSMode = fEvalLHSMode;
    engine.fEvalRHSMode = fEvalRHSMode;
    engine.fDisabledTrigRules = fDisabledTrigRules;
    engine.fFileSystemEnabled = fFileSystemEnabled;
    engine.fIterationLimit = fIterationLimit;
    engine.fModifiedVariablesList = fModifiedVariablesList;
    engine.fNumericMode = fNumericMode;
    // engine.fNumericPrecision = fNumericPrecision;
    engine.fApfloatHelper = new FixedPrecisionApfloatHelper(getNumericPrecision());
    engine.fApfloatHelperDouble = new FixedPrecisionApfloatHelper(ParserConfig.MACHINE_PRECISION);
    engine.fSignificantFigures = fSignificantFigures;
    engine.fEvalHistory = fEvalHistory;
    engine.fOptionsStack = fOptionsStack;
    engine.fOutListDisabled = fOutListDisabled;
    engine.fOutPrintStream = fOutPrintStream;
    engine.fOnOffMap = fOnOffMap;
    engine.fDollarSymbolMap = fDollarSymbolMap;
    engine.fOnOffMode = fOnOffMode;
    engine.fOnOffUnique = fOnOffUnique;
    engine.fOnOffUniqueMap = fOnOffUniqueMap;
    engine.fPackageMode = fPackageMode;
    engine.fQuietMode = fQuietMode;
    engine.fReapList = fReapList;
    engine.fRecursionCounter = 0;
    engine.fRecursionLimit = fRecursionLimit;
    engine.fRelaxedSyntax = fRelaxedSyntax;
    engine.fSeconds = fSeconds;
    engine.fSessionID = fSessionID;
    engine.fStopRequested = false;
    engine.fTogetherMode = fTogetherMode;
    engine.fNoSimplifyMode = fNoSimplifyMode;
    engine.fTraceMode = fTraceMode;
    engine.fTraceStack = fTraceStack;
    engine.f$Input = f$Input;
    engine.f$InputFileName = f$InputFileName;
    engine.stackBegin();
    fCopiedEngine = engine;
    return engine;
  }

  public Context begin(String contextName, Context parentContext) {
    fContextPathStack.push(fContextPath);
    fContextPath = fContextPath.copy();
    Context packageContext = fContextPath.getContext(contextName, parentContext);
    setContext(packageContext);
    return packageContext;
  }

  public Context beginPackage(String contextName) {
    fContextPathStack.push(fContextPath);
    Context packageContext = fContextPath.getContext(contextName);
    setContextPath(new ContextPath(packageContext));
    ContextPath.PACKAGES.add(contextName);
    return packageContext;
  }

  public Context end() {
    if (fContextPathStack.size() > 0) {
      ContextPath p = fContextPath;
      Context c = fContextPath.currentContext();
      fContextPath = fContextPathStack.pop();
      fContextPath.synchronize(p);
      return c;
    }
    return null;
  }

  public void endPackage() {
    if (fContextPathStack.size() > 0) {
      ContextPath p = fContextPath;
      Context c = fContextPath.currentContext();
      fContextPath = fContextPathStack.pop();
      fContextPath.synchronize(p);
      fContextPath.add(0, c);
    }
  }

  // public void cancel() {
  // fContextPath = null;
  // fErrorPrintStream = null;
  // fFileSystemEnabled = false;
  // fIterationLimit = 1;
  // fModifiedVariablesList = null;
  // fOutList = null;
  // fOutPrintStream = null;
  // fPackageMode = false;
  // fQuietMode = true;
  // fReapList = null;
  // fRecursionCounter = 1;
  // fRecursionLimit = 1;
  // fSeconds = 1;
  // fSessionID = null;
  // fStopRequested = true;
  // fTraceMode = false;
  // fTraceStack = null;
  // }

  /**
   * Decrement the recursion counter by 1 and return the result.
   *
   * @return the decremented recursion counter
   */
  public int decRecursionCounter() {
    return --fRecursionCounter;
  }

  private IAST endTrace() {
    setTraceMode(false);
    IAST ast = ((TraceStack) fTraceStack).getList();
    fTraceStack = null;
    if (ast.size() > 1) {
      return ast.getAST(1);
    }
    return ast;
  }

  /**
   * Evaluate the i-th argument of <code>ast</code>. This method may set evaluation flags in <code>
   * ast</code> or <code>result0</code>
   *
   * @param result0 store the result of the evaluation in the i-th argument of the ast in <code>
   *     result0[0]</code>. <code>result0[0]</code> should be <code>F.NIL</code> if no evaluation
   *        occured.
   * @param ast the original <code>ast</code> for whixh the arguments should be evaluated
   * @param arg the i-th argument of <code>ast</code>
   * @param i <code>arg</code> is the i-th argument of <code>ast</code>
   * @param isNumericFunction if <code>true</code> the <code>NumericFunction</code> attribute is set
   *        for the <code>ast</code>'s head
   */
  public void evalArg(final IASTMutable[] result0, final IAST ast, final IExpr arg, final int i,
      final boolean isNumericFunction) {
    final IExpr evaledArg;
    if (isNumericFunction) {
      evaledArg = evalLoop(arg);
    } else {
      boolean isNumericMode = fNumericMode;
      try {
        fNumericMode = false;
        evaledArg = evalLoop(arg);
      } finally {
        fNumericMode = isNumericMode;
      }
    }
    if (evaledArg.isPresent()) {
      if (result0[0].isNIL()) {
        result0[0] = ast.copy();
        if (isNumericFunction && evaledArg.isNumericArgument(true)) {
          result0[0].addEvalFlags(
              (ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR) | IAST.CONTAINS_NUMERIC_ARG);
        } else {
          result0[0].addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
        }
      }
      result0[0].set(i, evaledArg);
    } else {
      if (isNumericFunction && arg.isNumericArgument(false)) {
        ast.addEvalFlags(ast.getEvalFlags() | IAST.CONTAINS_NUMERIC_ARG);
      }
    }

  }

  /**
   * Evaluate the arguments of the given ast, taking the attributes <code>
   * HoldFirst, NHoldFirst, HoldRest, NHoldRest, NumericFunction</code> into account.
   *
   * @param ast
   * @param attributes
   * @param numericFunction if <code>true</code> evaluate the arguments of an {@link S#N} function
   *        expression
   * @return <code>F.NIL</code> is no evaluation was possible
   */
  public IASTMutable evalArgs(final IAST ast, final int attributes, boolean numericFunction) {
    final int astSize = ast.size();

    if (astSize > 1) {
      boolean numericMode = fNumericMode;
      boolean localNumericMode = fNumericMode;
      final boolean isNumericFunction;
      if ((ISymbol.NUMERICFUNCTION & attributes) == ISymbol.NUMERICFUNCTION) {
        isNumericFunction = true;
      } else {
        isNumericFunction = numericFunction;
      }
      final boolean isNumericArgument = ast.isNumericArgument(true);
      if (!fNumericMode) {
        if (isNumericFunction && isNumericArgument) {
          localNumericMode = true;
        }
      }

      IASTMutable[] rlist = new IASTMutable[1];
      rlist[0] = F.NIL;
      IExpr x = ast.arg1();
      if ((ISymbol.HOLDFIRST & attributes) == ISymbol.NOATTRIBUTE) {
        // the HoldFirst attribute is disabled
        try {
          if (!x.isAST(S.Unevaluated)) {
            selectNumericMode(attributes, ISymbol.NHOLDFIRST, localNumericMode);
            evalArg(rlist, ast, x, 1, isNumericFunction);
            if (astSize == 2 && rlist[0].isPresent()) {
              return rlist[0];
            }
          }
        } finally {
          if ((ISymbol.NHOLDFIRST & attributes) == ISymbol.NHOLDFIRST) {
            fNumericMode = numericMode;
          }
        }
      } else {
        // the HoldFirst attribute is set here
        if (!ast.isHoldAllCompleteAST()) {
          try {
            if (x.isAST(S.Evaluate)) {
              selectNumericMode(attributes, ISymbol.NHOLDFIRST, localNumericMode);
              evalArg(rlist, ast, x, 1, isNumericFunction);
              if (astSize == 2 && rlist[0].isPresent()) {
                return rlist[0];
              }
            }
          } finally {
            if ((ISymbol.NHOLDFIRST & attributes) == ISymbol.NHOLDFIRST) {
              fNumericMode = numericMode;
            }
          }
        }
      }

      if (astSize > 2) {
        if ((ISymbol.HOLDREST & attributes) == ISymbol.NOATTRIBUTE) {
          // the HoldRest attribute is disabled
          numericMode = fNumericMode;
          try {
            final boolean nMode = localNumericMode;
            ast.forEach(2, astSize, (arg, i) -> {
              if (!arg.isUnevaluated()) {
                selectNumericMode(attributes, ISymbol.NHOLDREST, nMode);
                evalArg(rlist, ast, arg, i, isNumericFunction);
              }
            });
          } finally {
            if ((ISymbol.NHOLDREST & attributes) == ISymbol.NHOLDREST) {
              fNumericMode = numericMode;
            }
          }
        } else {
          // the HoldRest attribute is set here
          if (!ast.isHoldAllCompleteAST()) {
            numericMode = fNumericMode;
            try {
              selectNumericMode(attributes, ISymbol.NHOLDREST, localNumericMode);
              ast.forEach(2, astSize, (arg, i) -> {
                if (arg.isAST(S.Evaluate)) {
                  evalArg(rlist, ast, arg, i, isNumericFunction);
                }
              });
            } finally {
              if ((ISymbol.NHOLDREST & attributes) == ISymbol.NHOLDREST) {
                fNumericMode = numericMode;
              }
            }
          }
        }
      }
      if (isNumericFunction //
          && !isNumericArgument //
          && rlist[0].isNIL() //
          && ast.isNumericArgument(true)) {
        // one of the arguments is a numeric value
        return evalArgs(ast, attributes, isNumericFunction);
      }
      return rlist[0];
    }
    return F.NIL;
  }

  /**
   * Evaluate an AST with only one argument (i.e. <code>head[arg1]</code>). The evaluation steps are
   * controlled by the header attributes.
   *
   * @param ast
   * @return
   */
  private IExpr evalASTArg1(final IAST ast) {
    // special case ast.isAST1()
    // head == ast[0] --- arg1 == ast[1]
    IExpr result = ast.head().evaluateHead(ast, this);
    if (result.isPresent()) {
      return result;
    }

    final ISymbol symbol = ast.topHead();
    final int attributes = symbol.getAttributes();

    if ((attributes & ISymbol.SEQUENCEHOLD) != ISymbol.SEQUENCEHOLD) {
      if ((result = F.flattenSequence(ast)).isPresent()) {
        return result;
      }
    }

    // don't test for OneIdentity here! OneIdentity will only be used in "structural
    // pattern-matching".
    // Functions like Times and PLus implement OneIdentity as extra transformation!

    final IExpr arg1 = ast.arg1();
    if ((result = evalArgs(ast, attributes, false)).isPresent()) {
      return result;
    }

    if (ISymbol.hasFlatAttribute(attributes)) {
      if (arg1.head().equals(symbol)) {
        // associative
        return arg1;
      }
      if (arg1.isUnevaluated() && arg1.first().head().equals(symbol) && arg1.first().isAST()) {
        IAST unevaluated = (IAST) arg1.first();
        return unevaluated.map(symbol, x -> F.Unevaluated(x));
      }
    }

    if ((ISymbol.LISTABLE & attributes) == ISymbol.LISTABLE) {
      if (symbol.isBuiltInSymbol()) {
        if (arg1.isRealVector() && ((IAST) arg1).size() > 1) {
          final IEvaluator module = ((IBuiltInSymbol) symbol).getEvaluator();
          if (module instanceof DoubleUnaryOperator) {
            DoubleUnaryOperator oper = (DoubleUnaryOperator) module;
            return ASTRealVector.map((IAST) arg1, oper);
          }
        } else if (arg1.isRealMatrix()) {
          final IEvaluator module = ((IBuiltInSymbol) symbol).getEvaluator();
          if (module instanceof DoubleUnaryOperator) {
            DoubleUnaryOperator oper = (DoubleUnaryOperator) module;
            return ASTRealMatrix.map((IAST) arg1, oper);
          }
        }

        if (arg1.isList()) {
          // thread over the list
          return EvalAttributes.threadList(ast, S.List, ast.head(), ((IAST) arg1).argSize());
        } else if (arg1.isAssociation()) {
          // thread over the association
          return arg1.mapThread(ast, 1);
        } else if (arg1.isSparseArray()) {
          return ((ISparseArray) arg1).mapThreadSparse(ast, 1);
        } else if (arg1.isConditionalExpression()) {
          IExpr temp = ast.extractConditionalExpression(true);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    }

    if ((ISymbol.NUMERICFUNCTION & attributes) == ISymbol.NUMERICFUNCTION) {
      if (fNumericMode //
          && arg1.isInexactNumber() //
          && ast.head().isBuiltInSymbol()) {
        IExpr temp = numericFunction(symbol, ast);
        if (temp.isPresent()) {
          return temp;
        }
      }
      if (arg1.isIndeterminate()) {
        return S.Indeterminate;
      }
      if (arg1.isUndefined()) {
        return S.Undefined;
      }
    }

    if (!(arg1 instanceof IPatternObject) && arg1.isPresent()) {
      ISymbol lhsSymbol = arg1.isSymbol() ? (ISymbol) arg1 : arg1.topHead();
      return lhsSymbol.evalUpRules(ast, this);
    }
    return F.NIL;
  }

  /**
   * All arguments in <code>ast</code> must be finite inexact numbers.
   * 
   * @param symbol
   * @param ast
   * @return
   */
  private IExpr numericFunction(final ISymbol symbol, final IAST ast) {
    IExpr result;
    final IEvaluator evaluator = ((IBuiltInSymbol) symbol).getEvaluator();
    if (evaluator instanceof IFunctionEvaluator) {
      // evaluate a built-in function.
      final IFunctionEvaluator functionEvaluator = (IFunctionEvaluator) evaluator;
      try {
        result = functionEvaluator.numericFunction(ast, this);
        if (result.isPresent()) {
          return result;
        }
      } catch (ValidateException ve) {
        ve.printStackTrace();
        return Errors.printMessage(ast.topHead(), ve, this);
      } catch (FlowControlException e) {
        throw e;
      } catch (SymjaMathException ve) {
        return Errors.printMessage(ast.topHead(), ve, this);
      }
    }
    return F.NIL;
  }

  /**
   * @param symbol
   * @param ast
   * @return <code>F.NIL</code> if no evaluation happened
   */
  private IExpr evalASTBuiltinFunction(final ISymbol symbol, final IAST ast) {
    final int attributes = symbol.getAttributes();
    if (fEvalLHSMode) {
      if ((ISymbol.HOLDALL & attributes) == ISymbol.HOLDALL) {
        // check for Set or SetDelayed necessary, because of dynamic
        // evaluation then initializing rules for predefined symbols
        // (i.e. Sin, Cos,...)
        if (!(symbol.equals(S.Set) || symbol.equals(S.SetDelayed) || symbol.equals(S.UpSet)
            || symbol.equals(S.UpSetDelayed))) {
          return F.NIL;
        }
        // } else {
        // if ((ISymbol.NUMERICFUNCTION & attributes) != ISymbol.NUMERICFUNCTION) {
        // return F.NIL;
        // }
      }
    }

    if (!symbol.equals(S.Integrate)) {
      IExpr result;
      if ((result = symbol.evalDownRule(this, ast)).isPresent()) {
        return result;
      }
    }

    if (symbol.isBuiltInSymbol()) {
      if (ast.isEvalFlagOn(IAST.BUILT_IN_EVALED) && isSymbolicMode(attributes)) {
        return F.NIL;
      }
      final IEvaluator evaluator = ((IBuiltInSymbol) symbol).getEvaluator();
      if (evaluator instanceof IFunctionEvaluator) {
        // evaluate a built-in function.
        final IFunctionEvaluator functionEvaluator = (IFunctionEvaluator) evaluator;

        OptionsResult options = checkBuiltinArguments(ast, functionEvaluator);
        if (options == null) {
          ast.functionEvaled();
          return F.NIL;
        }
        IAST newAST = options.result;
        try {
          if (evaluator instanceof AbstractFunctionOptionEvaluator) {
            AbstractFunctionOptionEvaluator optionsEvaluator =
                (AbstractFunctionOptionEvaluator) evaluator;
            IExpr result =
                optionsEvaluator.evaluate(newAST, options.argSize, options.options, this, ast);
            if (result.isPresent()) {
              return result;
            }
          } else {

            IExpr result = fNumericMode ? functionEvaluator.numericEval(newAST, this)
                : functionEvaluator.evaluate(newAST, this);
            if (result.isPresent()) {
              return result;
            }
          }
        } catch (ValidateException ve) {
          ve.printStackTrace();
          return Errors.printMessage(ast.topHead(), ve, this);
        } catch (FlowControlException e) {
          throw e;
        } catch (SymjaMathException ve) {
          return Errors.printMessage(ast.topHead(), ve, this);
        }
        // cannot generally set the result as evaluated in built-in function. Especially problems in
        // `togetherMode`
        // if (((attributes & ISymbol.NUMERICFUNCTION) == ISymbol.NUMERICFUNCTION)//
        // && ast.argSize() == 1 //
        // && ast.isNumericFunction()) {
        // ast.functionEvaled();
        // }
      } else {
        ast.functionEvaled();
      }
    }
    return F.NIL;
  }

  /**
   * The engine is in &quot;symbolic evaluation mode&quot;, no assumptions are set and the head
   * contains no &quot;HOLD....&quot; attribute.
   *
   * @param headAttributes the attributes of the built-in header function which should be evaluated
   * @return <code>true</code> if the engine is in symbolic mode evaluation
   */
  public final boolean isSymbolicMode(final int headAttributes) {
    return !fNumericMode && fAssumptions == null
        && ((ISymbol.HOLDALLCOMPLETE & headAttributes) == ISymbol.NOATTRIBUTE);
  }

  /**
   * Check the number of arguments if requested and transform the <code>ast</code> from an
   * <i>operator form</i> to <i>normal form</i> if it is allowed.
   *
   * @param ast
   * @param functionEvaluator
   * @return
   */
  public OptionsResult checkBuiltinArguments(IAST ast, final IFunctionEvaluator functionEvaluator) {
    int[] expected;
    int argSize = ast.argSize();
    OptionsResult opres = new OptionsResult(ast, argSize, null);
    if ((expected = functionEvaluator.expectedArgSize(ast)) != null) {
      if (expected.length == 2 && !ast.head().isBuiltInSymbol()) {
        return null;
      } else if (expected.length == 3 && expected[2] > 0) {
        switch (expected[2]) {
          case 1:
            if (ast.isAST1()) {
              opres.result = F.operatorForm1Append(ast);
              if (opres.result.isNIL()) {
                return null;
              }
            }
            break;
          case 2:
            if (ast.head().isAST1()) {
              opres.result = F.operatorForm2Prepend(ast, expected, this);
              if (opres.result.isNIL()) {
                return null;
              }
            }
            break;
          default:
        }
      }

      ast = opres.result;
      argSize = ast.argSize();
      if (argSize < expected[0] || argSize > expected[1]
          || (expected[1] == Integer.MAX_VALUE && expected.length == 2)) {
        if (ast.isAST1() && expected.length > 2) {
          // because an operator form is allowed do not print a message
          return null;
        }
        if (argSize < expected[0]) {
          Errors.printArgMessage(ast, expected, this);
          return null;
        }
        if (argSize > expected[0]) {
          OptionsResult temp = getOptions(functionEvaluator, opres, ast, expected);
          if (temp != null) {
            return temp;
          }
        }
        if (argSize > expected[1]) {
          Errors.printArgMessage(ast, expected, this);
          return null;
        }

      }
    }
    if (functionEvaluator instanceof AbstractFunctionOptionEvaluator
        || functionEvaluator instanceof AbstractCoreFunctionOptionEvaluator) {
      opres = getOptions(functionEvaluator, opres, ast, expected);
      if (opres != null) {
        return opres;
      }
    }

    return opres;
  }

  /**
   * Check the number of arguments and print a message to error stream if necessary.
   * 
   * @param ast
   * @param functionEvaluator
   * @return
   */
  public IAST checkBuiltinArgsSize(final IAST ast, final IFastFunctionEvaluator functionEvaluator) {
    int[] expected;
    final int argSize = ast.argSize();
    if ((expected = functionEvaluator.expectedArgSize(ast)) != null) {
      if (expected.length == 2) {
        if (argSize < expected[0] || argSize > expected[1]) {
          if (argSize < expected[0]) {
            Errors.printArgMessage(ast, expected, this);
          } else if (argSize > expected[1]) {
            Errors.printArgMessage(ast, expected, this);
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Test if the experimental message was printed at least once for a symbol.
   *
   * @param symbol
   * @return
   */
  public boolean containsExperimental(IBuiltInSymbol symbol) {
    return experimatalSymbols.containsKey(symbol);
  }

  private OptionsResult getOptions(IFunctionEvaluator optionEvaluator, OptionsResult opres,
      IAST ast, @Nonnull int[] expected) {
    IBuiltInSymbol[] optionSymbols = optionEvaluator.getOptionSymbols();
    if (optionSymbols != null) {
      opres.options = new IExpr[optionSymbols.length];
      int argSize = AbstractFunctionEvaluator.determineOptions(opres.options, ast, ast.argSize(),
          expected, optionSymbols, this);
      if (expected != null && argSize <= expected[1] && argSize >= expected[0]) {
        opres.argSize = argSize;
        return opres;
      }
    }
    return null;
  }

  /**
   * Evaluate an AST according to the attributes set in the header symbol. The evaluation steps are
   * controlled by the header attributes.
   *
   * @param symbol the header symbol
   * @param mutableAST the AST which should be evaluated. If <code>symbol</code> has attribute
   *        {@link ISymbol#ORDERLESS} the mutableAST will be modified.
   * @return <code>F.NIL</code> if no evaluation was possible
   */
  public IExpr evalAttributes(ISymbol symbol, IASTMutable mutableAST) {
    final int astSize = mutableAST.size();
    if (astSize == 2) {
      return evalASTArg1(mutableAST);
    }

    IExpr returnResult = F.NIL;
    IExpr result = mutableAST.head().evaluateHead(mutableAST, this);
    if (result.isPresent()) {
      return result;
    }

    if (astSize != 1) {
      final int attributes = symbol.getAttributes();
      if ((attributes & ISymbol.NO_EVAL_ENGINE_ATTRIBUTE) == ISymbol.NOATTRIBUTE) {
        return evalNoAttributes(mutableAST);
      }

      if ((attributes & ISymbol.SEQUENCEHOLD) != ISymbol.SEQUENCEHOLD) {
        if ((result = F.flattenSequence(mutableAST)).isPresent()) {
          return result;
        }
      }

      IASTMutable resultList = evalArgs(mutableAST, attributes, false);
      if (resultList.isPresent()) {
        return resultList;
      }

      // ONEIDENTITY is checked in the evalASTArg1() method!
      if (ISymbol.hasFlatAttribute(attributes)) {
        // associative symbol
        IASTAppendable flattened;
        if ((flattened = EvalAttributes.flatten(mutableAST)).isPresent()) {
          returnResult = flattened;
          mutableAST = flattened;
        }
      }

      result = evalTagSetPlusTimes(mutableAST);
      if (result.isPresent()) {
        return result;
      }

      if ((ISymbol.LISTABLE & attributes) == ISymbol.LISTABLE && !((mutableAST.getEvalFlags()
          & IAST.IS_LISTABLE_THREADED) == IAST.IS_LISTABLE_THREADED)) {
        // thread over the lists
        resultList = threadASTListArgs(mutableAST, S.Thread, "tdlen");
        if (resultList.isPresent()) {
          if (resultList.isAssociation()) {
            return resultList;
          }
          return evalArgs(resultList, ISymbol.NOATTRIBUTE, false).orElse(resultList);
        }
      }

      if ((ISymbol.NUMERICFUNCTION & attributes) == ISymbol.NUMERICFUNCTION) {
        if (fNumericMode //
            && mutableAST.head().isBuiltInSymbol()//
            && mutableAST.forAll(x -> x.isInexactNumber())) {
          IExpr temp = numericFunction(symbol, mutableAST);
          if (temp.isPresent()) {
            return temp;
          }
        }
        if (!((ISymbol.HOLDALL & attributes) == ISymbol.HOLDALL)) {
          if (mutableAST.exists(x -> x.isIndeterminate())) {
            return S.Indeterminate;
          }
          if (mutableAST.exists(x -> x.isUndefined())) {
            return S.Undefined;
          }
        }
      }

      if (astSize > 2 && ISymbol.hasOrderlessAttribute(attributes)) {
        // commutative symbol
        EvalAttributes.sortWithFlags(mutableAST);
      }
      IExpr temp = mutableAST.extractConditionalExpression(false);
      if (temp.isPresent()) {
        return temp;
      }
    }

    return returnResult;
  }

  /**
   * Evaluate an AST, which may have only the {@link ISymbol#PROTECTED} attribute set in the header
   * symbol. Only the evaluation steps are processed, where no attributes are set.
   * 
   * @param mutableAST the AST which should be evaluated.
   * @param attributes
   * @return <code>F.NIL</code> if no evaluation was possible
   */
  private IExpr evalNoAttributes(IASTMutable mutableAST) {
    IExpr result = F.flattenSequence(mutableAST);
    if (result.isPresent()) {
      return result;
    }
    final int astSize = mutableAST.size();
    final boolean localNumericMode = fNumericMode;
    IASTMutable[] rlist = new IASTMutable[] {F.NIL};
    mutableAST.forEach(1, astSize, (arg, i) -> {
      if (!arg.isUnevaluated()) {
        fNumericMode = localNumericMode;
        evalArg(rlist, mutableAST, arg, i, false);
      }
    });
    if (rlist[0].isPresent()) {
      return rlist[0];
    }
    return mutableAST.extractConditionalExpression(false);
  }

  public IExpr evalBlock(final Supplier<IExpr> supplier, final IAST localVariablesList) {
    ISymbol[] symbolList = new ISymbol[localVariablesList.size()];
    IExpr[] blockVariables = new IExpr[localVariablesList.size()];
    RulesData[] blockVariablesRulesData = new RulesData[localVariablesList.size()];
    IExpr result = F.NIL;
    try {
      Programming.rememberBlockVariables(localVariablesList, symbolList, blockVariables,
          blockVariablesRulesData, this);
      result = supplier.get();
    } finally {
      if (localVariablesList.size() > 0) {
        // reset local variables to global ones
        ISymbol variableSymbol;
        for (int i = 1; i < localVariablesList.size(); i++) {
          if (localVariablesList.get(i).isVariable()) {
            variableSymbol = symbolList[i];
            if (variableSymbol != null) {
              variableSymbol.assignValue(blockVariables[i], false);
              variableSymbol.setRulesData(blockVariablesRulesData[i]);
            }
          } else if (localVariablesList.get(i).isAST(S.Set, 3)) {
            final IAST setFun = (IAST) localVariablesList.get(i);
            if (setFun.arg1().isVariable()) {
              variableSymbol = symbolList[i];
              if (variableSymbol != null) {
                variableSymbol.assignValue(blockVariables[i], false);
                variableSymbol.setRulesData(blockVariablesRulesData[i]);
              }
            }
          }
        }
      }
    }
    return result;
  }

  public IExpr evalBlock(final IExpr expr, final IAST localVariablesList) {
    return evalBlock(() -> evaluate(expr), localVariablesList);
  }

  /**
   * Evaluate an expression for a local &quot;dummy&quot; variable.
   *
   * @param expr the expression which should be evaluated for the given symbol
   * @param symbol the symbol which should be evaluated as a local variable
   * @param localValue the value
   * @param quiet if <code>true</code> evaluate in quiet mode and suppress evaluation messages
   */
  public IExpr evalModuleDummySymbol(IExpr expr, ISymbol symbol, IExpr localValue, boolean quiet) {
    boolean quietMode = isQuietMode();
    setQuietMode(quiet);
    java.util.IdentityHashMap<ISymbol, ISymbol> blockVariables =
        new IdentityHashMap<ISymbol, ISymbol>();
    IExpr result = F.NIL;
    try {
      ISymbol oldSymbol = symbol;
      ISymbol newSymbol = F.Dummy(oldSymbol.toString());
      blockVariables.put(oldSymbol, newSymbol);
      IExpr temp = F.subst(evaluate(localValue), blockVariables);
      evaluate(F.Set(newSymbol, temp));
      result = expr.accept(new ModuleReplaceAll(blockVariables, this, ""));
      return evaluate(result.orElse(expr));
    } finally {
      setQuietMode(quietMode);
      if (blockVariables.size() > 0) {
        // reset local variables to global ones
        java.util.IdentityHashMap<ISymbol, IExpr> globalVariables =
            new IdentityHashMap<ISymbol, IExpr>();
        for (Map.Entry<ISymbol, ISymbol> entry : blockVariables.entrySet()) {
          globalVariables.put(entry.getValue(), entry.getKey());
        }
        result = F.subst(result, globalVariables);
      }
    }
  }

  /**
   * Evaluates <code>expr</code> numerically and return the result as a Java <code>boolean</code>
   * value.
   * 
   * @param expr
   * @return
   * @throws ArgumentTypeException
   */
  public final boolean evalBoolean(final IExpr expr) throws ArgumentTypeException {
    if (expr.equals(S.True)) {
      return true;
    }
    if (expr.equals(S.False)) {
      return false;
    }
    if (expr.isNumericFunction(true)) {
      IExpr numericResult = evalN(expr);
      if (numericResult.equals(S.True)) {
        return true;
      }
      if (numericResult.equals(S.False)) {
        return false;
      }
    } else {
      IExpr temp = evaluateNIL(expr);
      if (temp.isNumericFunction(true)) {
        IExpr numericResult = evalN(temp);
        if (numericResult.equals(S.True)) {
          return true;
        }
        if (numericResult.equals(S.False)) {
          return false;
        }
      }
    }
    throw new ArgumentTypeException(
        "conversion into a machine-size boolean value is not possible!");
  }

  public final boolean[] evalBooleanVector(final IExpr expr) {
    return expr.toBooleanVector();
  }

  public final boolean[][] evalBooleanMatrix(final IExpr expr) {
    return expr.toBooleanMatrix();
  }

  /**
   * Evaluates <code>expr</code> numerically and return the result as a Java <code>double</code>
   * value.
   *
   * @param expr
   * @return
   * @see #evaluate(IExpr)
   */
  public final double evalDouble(final IExpr expr) throws ArgumentTypeException {
    return evalDouble(expr, null, Double.NaN);
  }

  /**
   * Evaluate the expression to a Java <code>double</code> value.
   * 
   * @param expr
   * @param function maybe <code>null</code>; returns a substitution value for some expressions
   * @return
   * @throws ArgumentTypeException
   */
  public final double evalDouble(final IExpr expr, Function<IExpr, IExpr> function)
      throws ArgumentTypeException {
    return evalDouble(expr, function, Double.NaN);
  }

  public final double[] evalDoubleVector(final IExpr expr) {
    return expr.toDoubleVector();
  }

  public final double[][] evalDoubleMatrix(final IExpr expr) {
    return expr.toDoubleMatrix();
  }

  /**
   * Evaluate the expression to a Java <code>double</code> value.
   * 
   * @param expr
   * @param function maybe <code>null</code>; returns a substitution value for some expressions
   * @param defaultValue
   * @return
   */
  public final double evalDouble(IExpr expr, Function<IExpr, IExpr> function, double defaultValue) {
    if (expr.isReal()) {
      return ((IReal) expr).doubleValue();
    }
    if (expr.isInfinity()) {
      return Double.POSITIVE_INFINITY;
    }
    if (expr.isNegativeInfinity()) {
      return Double.NEGATIVE_INFINITY;
    }
    boolean quietMode = fQuietMode;
    try {
      fQuietMode = true;
      if (function != null) {
        expr = expr.accept(new VisitorReplaceEvalf(function)).orElse(expr);
      }
      IExpr result = evalN(expr);
      if (result.isReal()) {
        return ((IReal) result).doubleValue();
      }
      if (result.isInfinity()) {
        return Double.POSITIVE_INFINITY;
      }
      if (result.isNegativeInfinity()) {
        return Double.NEGATIVE_INFINITY;
      }
      if (result.isComplexNumeric()) {
        IComplexNum cc = (IComplexNum) result;
        if (F.isZero(cc.getImaginaryPart())) {
          return cc.getRealPart();
        }
      }
      if (result.isQuantity()) {
        return result.evalReal().doubleValue();
      }
      if (result.isAST(S.Labeled, 3, 4)) {
        return result.first().evalReal().doubleValue();
      }
    } finally {
      fQuietMode = quietMode;
    }
    if (Double.isNaN(defaultValue)) {
      throw new ArgumentTypeException("Expression \"" + Errors.shorten(expr)
          + "\" cannot be converted to a machine-sized double numeric value!");
    }
    return defaultValue;
  }

  public final int evalInt(final IExpr expr) throws ArgumentTypeException {
    int result = Integer.MIN_VALUE;
    if (expr.isReal()) {
      result = expr.toIntDefault();
    }
    if (expr.isNumericFunction(true)) {
      IExpr numericResult = evalN(expr);
      if (numericResult.isReal()) {
        result = numericResult.toIntDefault();
      }
    } else {
      IExpr temp = evaluateNIL(expr);
      if (temp.isNumericFunction(true)) {
        IExpr numericResult = evalN(temp);
        if (numericResult.isReal()) {
          result = numericResult.toIntDefault();
        }
      }
    }
    if (result != Integer.MIN_VALUE) {
      return result;
    }
    throw new ArgumentTypeException(
        "conversion into a machine-size integer value is not possible!");
  }

  public final int[] evalIntVector(final IExpr expr) {
    return expr.toIntVector();
  }

  public final int[][] evalIntMatrix(final IExpr expr) {
    return expr.toIntMatrix();
  }

  /**
   * Evaluates <code>expr</code> numerically and return the result as a Java <code>
   * org.hipparchus.complex.Complex</code> value.
   *
   * @param expr
   * @return
   * @throws ArgumentTypeException
   */
  public final Complex evalComplex(final IExpr expr) throws ArgumentTypeException {
    return evalComplex(expr, null);
  }

  /**
   * Evaluates <code>expr</code> numerically and return the result as a Java <code>
   * org.hipparchus.complex.Complex</code> value.
   *
   * @param expr
   * @param function maybe <code>null</code>; returns a substitution value for some expressions
   * @return
   * @throws ArgumentTypeException
   */
  public final Complex evalComplex(IExpr expr, final Function<IExpr, IExpr> function)
      throws ArgumentTypeException {
    if (expr.isReal()) {
      return new Complex(((IReal) expr).doubleValue());
    }
    if (expr.isNumber()) {
      return new Complex(((INumber) expr).reDoubleValue(), ((INumber) expr).imDoubleValue());
    }
    boolean quietMode = fQuietMode;
    try {
      fQuietMode = true;
      if (function != null) {
        expr = expr.accept(new VisitorReplaceEvalf(function)).orElse(expr);
      }
      IExpr result = evalN(expr);
      if (result.isReal()) {
        return new Complex(((IReal) result).doubleValue());
      }
      if (result.isQuantity()) {
        return new Complex(result.evalReal().doubleValue());
      }
      if (result.isNumber()) {
        return new Complex(((INumber) result).reDoubleValue(), ((INumber) result).imDoubleValue());
      }
      if (result.isAST(S.Labeled, 3, 4)) {
        return evalComplex(result.first(), function);
      }
    } finally {
      fQuietMode = quietMode;
    }
    throw new ArgumentTypeException(
        "conversion into a machine-size Complex numeric value is not possible!");
  }

  public final Complex[] evalComplexVector(final IExpr expr) {
    return expr.toComplexVector();
  }

  public final Complex[][] evalComplexMatrix(final IExpr expr) {
    return expr.toComplexMatrix();
  }

  /**
   * Evaluate the Flat and Orderless attributes of the given <code>ast</code> recursively.
   *
   * @param ast
   * @return <code>F.NIL</code> if no evaluation was possible
   */
  public IAST evalFlatOrderlessAttrsRecursive(final IAST ast) {
    if (ast.isEvalFlagOn(IAST.IS_FLAT_ORDERLESS_EVALED)) {
      return F.NIL;
    }
    final ISymbol symbol = ast.topHead();
    final int attributes = symbol.getAttributes();
    // final Predicate<IExpr> isPattern = Predicates.isPattern();
    IASTMutable resultList = F.NIL;

    if ((ISymbol.HOLDALL & attributes) != ISymbol.HOLDALL) {
      final int astSize = ast.size();

      if ((ISymbol.HOLDFIRST & attributes) == ISymbol.NOATTRIBUTE) {
        // the HoldFirst attribute isn't set here
        if (astSize > 1 && ast.arg1().isAST()) {
          IExpr expr = ast.arg1();
          if (ast.arg1().isAST()) {
            IAST temp = (IAST) ast.arg1();
            expr = evalFlatOrderlessAttrsRecursive(temp);
            if (expr.isPresent()) {
              resultList = ast.setAtCopy(1, expr);
            } else {
              expr = ast.arg1();
            }
          }
        }
      }
      if (astSize > 2) {
        if ((ISymbol.HOLDREST & attributes) == ISymbol.NOATTRIBUTE) {
          // the HoldRest attribute isn't set here
          for (int i = 2; i < astSize; i++) {
            if (ast.get(i).isAST()) {
              IAST temp = (IAST) ast.get(i);
              IExpr expr = evalFlatOrderlessAttrsRecursive(temp);
              if (expr.isPresent()) {
                if (resultList.isNIL()) {
                  resultList = ast.copy();
                }
                resultList.set(i, expr);
              }
            }
          }
        }
      }
    }
    if (resultList.isPresent()) {
      if (resultList.size() > 2) {
        if (ISymbol.hasFlatAttribute(attributes)) {
          // associative
          IASTAppendable result;
          if ((result = EvalAttributes.flattenDeep(resultList)).isPresent()) {
            resultList = result;
            if (ISymbol.hasOrderlessAttribute(attributes)) {
              EvalAttributes.sortWithFlags(resultList);
            }
            resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
            return resultList;
          }
        }
        if (ISymbol.hasOrderlessAttribute(attributes)) {
          EvalAttributes.sortWithFlags(resultList);
        }
      }
      resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
      return resultList;
    }

    if (ISymbol.hasFlatAttribute(attributes)) {
      // associative
      IASTAppendable result;
      if ((result = EvalAttributes.flattenDeep(ast)).isPresent()) {
        resultList = result;
        if (ISymbol.hasOrderlessAttribute(attributes)) {
          EvalAttributes.sortWithFlags(resultList);
        }
        resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
        return resultList;
      }
    }
    if (ISymbol.hasOrderlessAttribute(attributes)) {
      if (EvalAttributes.sortWithFlags((IASTMutable) ast)) {
        ast.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
        return ast;
      }
      return ast;
    }
    return F.NIL;
  }

  /**
   * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest,
   * Orderless to create pattern-matching expressions directly or for the left-hand-side of a <code>
   * Set[]</code>, <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSetDelayed[]</code>
   * expression
   *
   * @param ast
   * @return <code>ast</code> if no evaluation was executed.
   */
  public IExpr evalHoldPattern(IAST ast) {
    return evalHoldPattern(ast, false, false);
  }

  /**
   * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest,
   * Orderless to create pattern-matching expressions directly or for the left-hand-side of a <code>
   * Set[]</code>, <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSeted[]</code>
   * expression
   *
   * @param ast
   * @param noEvaluation (sub-)expressions which contain no patterns should not be evaluated
   * @param evalNumericFunction evaluate in numeric mode
   * @return <code>ast</code> if no evaluation was executed.
   */
  public IExpr evalHoldPattern(IAST ast, boolean noEvaluation, boolean evalNumericFunction) {
    boolean evalLHSMode = fEvalLHSMode;
    try {
      fEvalLHSMode = true;
      return evalSetAttributesRecursive(ast, noEvaluation, evalNumericFunction, 0);
    } finally {
      fEvalLHSMode = evalLHSMode;
    }
  }

  /**
   * Evaluate an object, if evaluation is not possible return <code>F.NIL</code>.
   *
   * @param expr the expression which should be evaluated
   * @return the evaluated expression or <code>F.NIL</code> if evaluation isn't possible
   * @see #evalWithoutNumericReset(IExpr)
   * @see #evaluateNIL(IExpr)
   */
  private final IExpr evalLoop(final IExpr expr) {
    if (expr instanceof IAtomicEvaluate) {
      return expr.evaluate(this);
    } else if (expr instanceof IAST) {
      final IExpr head = expr.head();
      if (head instanceof IBuiltInSymbol) {
        final IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
        if (evaluator instanceof IFastFunctionEvaluator) {
          return ((IFastFunctionEvaluator) evaluator).evaluate((IAST) expr, this);
        }
      }
    } else if (expr instanceof NILPointer || expr == null) {
      if (Config.FUZZ_TESTING) {
        throw new NullPointerException();
      }
      // `1`.
      Errors.printMessage(S.General, "error",
          F.List("Evaluation aborted in EvalEngine#evalLoop() because of undefined expression!"));
      throw AbortException.ABORTED;
    }

    if ((fRecursionLimit > 0) && (fRecursionCounter > fRecursionLimit)) {
      RecursionLimitExceeded.throwIt(fRecursionLimit, expr);
    }
    if (fStopRequested || Thread.currentThread().isInterrupted()) {
      // check before going one recursion deeper
      throw TimeoutException.TIMED_OUT;
    }

    long iterationCounter = 0;
    if (fTraceMode) {
      return evalLoopTraceMode(expr);
    } else {
      IExpr result = expr;
      try {
        fRecursionCounter++;
        stackPush(expr);
        while (true) {
          if (result.isUnevaluated()) {
            return unevaluatedArg1(result.first());
          }
          final IExpr temp = result.evaluate(this);
          if (temp.isPresent()) {
            if (fStopRequested || Thread.currentThread().isInterrupted()) {
              throw TimeoutException.TIMED_OUT;
            }
            if (Config.DEBUG && temp.equals(result)) {
              // Endless iteration detected in `1` in evaluation loop.
              Errors.printMessage(result.topHead(), "itendless", F.list(temp), this);
              IterationLimitExceeded.throwIt(fIterationLimit, result);
            }
            if (fOnOffMode) {
              printOnOffTrace(result, temp);
            }

            result = temp;
            if (++iterationCounter >= fIterationLimit && fIterationLimit >= 0) {
              IterationLimitExceeded.throwIt(iterationCounter, result);
            }

            continue;
          }
          return iterationCounter == 0 ? F.NIL : result;
        }
      } catch (UnsupportedOperationException uoe) {
        if (Config.FUZZ_TESTING) {
          throw new NullPointerException();
        }
        // `1`.
        Errors.printMessage(result.topHead(), "error", F.List(uoe.getMessage()), this);
        throw AbortException.ABORTED;
      } finally {
        stackPop();
        // fTraceStack.tearDown(iterationCounter == 0 ? F.NIL : result, fRecursionCounter, true);
        fRecursionCounter--;
        if (fStopRequested) {
          throw TimeoutException.TIMED_OUT;
        }
      }
    }


  }

  /**
   * 
   * Evaluate an object in <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Trace.md">trace
   * mode</a>, if evaluation is not possible return <code>F.NIL</code>.
   *
   * @param expr the expression which should be evaluated
   * @return the evaluated expression or <code>F.NIL</code> if evaluation isn't possible
   * @see #evalWithoutNumericReset(IExpr)
   * @see #evaluateNIL(IExpr)
   */
  private IExpr evalLoopTraceMode(final IExpr expr) {
    IExpr result = expr;

    try {
      fRecursionCounter++;
      stackPush(expr);

      if (result.isUnevaluated()) {
        return unevaluatedArg1(result.first());
      }
      fTraceStack.setUp(expr, fRecursionCounter);
      boolean isEvaled = false;
      try {
        IExpr temp = result.evaluate(this);
        if (temp.isPresent()) {
          if (fStopRequested || Thread.currentThread().isInterrupted()) {
            throw TimeoutException.TIMED_OUT;
          }

          fTraceStack.add(expr, temp, fRecursionCounter, 0L, EVALUATION_LOOP);
          result = temp;
          long iterationCounter = 1;
          while (true) {
            if (result.isUnevaluated()) {
              isEvaled = true;
              return unevaluatedArg1(result.first());
            }
            temp = result.evaluate(this);
            if (temp.isPresent()) {
              if (fStopRequested || Thread.currentThread().isInterrupted()) {
                throw TimeoutException.TIMED_OUT;
              }
              if (Config.DEBUG) {
                if (temp.equals(result)) {
                  // Endless iteration detected in `1` in evaluation loop.
                  Errors.printMessage(result.topHead(), "itendless", F.list(temp), this);
                  IterationLimitExceeded.throwIt(fIterationLimit, result);
                }
              }
              fTraceStack.add(result, temp, fRecursionCounter, iterationCounter, EVALUATION_LOOP);

              result = temp;
              if (++iterationCounter >= fIterationLimit && fIterationLimit >= 0) {
                IterationLimitExceeded.throwIt(iterationCounter, result);
              }
              continue;
            }
            isEvaled = true;
            return result;
          }
        }
      } finally {
        if (isEvaled) {
          fTraceStack.tearDown(result, fRecursionCounter, true);
        } else {
          fTraceStack.tearDown(F.NIL, fRecursionCounter, false);
        }
      }
    } catch (UnsupportedOperationException uoe) {
      if (Config.FUZZ_TESTING) {
        throw new NullPointerException();
      }
      // `1`.
      Errors.printMessage(S.General, uoe, this);
      throw AbortException.ABORTED;
    } finally {
      stackPop();
      fRecursionCounter--;
      if (fStopRequested) {
        throw TimeoutException.TIMED_OUT;
      }
    }
    return F.NIL;
  }

  /**
   * Print the trace enabled by the <code>On({head1, head2,...})</code> function.
   *
   * @param unevaledExpr the unevaluated expression
   * @param evaledExpr the evaluated expression
   */
  private void printOnOffTrace(IExpr unevaledExpr, IExpr evaledExpr) {
    boolean showExpr = true;
    if (fOnOffMap != null) {
      showExpr = fOnOffMap.containsKey(unevaledExpr.topHead());
    }
    if (showExpr) {
      if (fOnOffUniqueMap != null) {
        if (fOnOffUniqueMap.containsKey(unevaledExpr)) {
          return;
        } else {
          fOnOffUniqueMap.put(unevaledExpr, evaledExpr);
        }
      }
      PrintStream stream = getOutPrintStream();
      stream.println("  " + unevaledExpr.toString() + " --> " + evaledExpr.toString() + "\n");
    }
  }

  /**
   * Evaluates <code>expr</code> numerically.
   *
   * @param expr
   * @return
   * @see #evaluate(IExpr)
   */
  public final IExpr evalN(final IExpr expr) {
    return evaluate(F.N(expr));
  }

  public final IAST evalArgsOrderlessN(IAST ast1) {
    int numericFlag =
        isArbitraryMode() ? IAST.NUMERIC_ARBITRARY_EVALED : IAST.NUMERIC_DOUBLE_EVALED;
    if (ast1.isEvalFlagOn(numericFlag)) {
      return F.NIL;
    }

    boolean oldNumericMode = fNumericMode;
    try {
      fNumericMode = true;
      IASTMutable copy = F.NIL;
      for (int i = 1; i < ast1.size(); i++) {
        IExpr temp = ast1.get(i);
        if (!temp.isInexactNumber() && temp.isNumericFunction(true)) {
          temp = evaluateNIL(temp);
          if (temp.isPresent()) {
            if (copy.isNIL()) {
              copy = ast1.copy();
            }
            copy.set(i, temp);
          }
        }
      }
      if (copy.isPresent()) {
        EvalAttributes.sort(copy);
        copy.addEvalFlags(
            isArbitraryMode() ? IAST.NUMERIC_ARBITRARY_EVALED : IAST.NUMERIC_DOUBLE_EVALED);
      } else {
        ast1.addEvalFlags(
            isArbitraryMode() ? IAST.NUMERIC_ARBITRARY_EVALED : IAST.NUMERIC_DOUBLE_EVALED);
      }
      return copy;
    } finally {
      fNumericMode = oldNumericMode;
    }
  }

  /**
   * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation
   * reset the numeric mode to the value stored before the evaluation starts. If evaluation is not
   * possible return the input object.
   *
   * <p>
   * <b>Note:</b> if this method catches exception <code>
   * org.matheclipse.parser.client.math.MathException</code>, it returns the input expression.
   *
   * @param expr the object which should be evaluated
   * @return the evaluated object
   */
  public final IExpr evalPattern(final IExpr expr) {
    boolean numericMode = fNumericMode;
    try {
      if (expr.isFreeOfPatterns()) {
        return evalWithoutNumericReset(expr);
      }
      if (expr.isAST()) {
        if (expr.isOneIdentityAST1()) {
          if (expr.first().isAST()) {
            return evalHoldPattern((IAST) expr.first()).orElse(expr.first());
          }
          return expr.first();
        }
        return evalHoldPattern((IAST) expr).orElse(expr);
      }
      return expr;
    } catch (MathException ce) {
      return expr;
    } finally {
      fNumericMode = numericMode;
    }
  }

  /**
   * Create a pattern matcher
   *
   * @param patternExpression the object which should be transformed into a pattern matcher
   * @return an <code>IPatterMatcher</code> created from the given expression.
   */
  public final IPatternMatcher evalPatternMatcher(final IExpr patternExpression) {
    return new PatternMatcher(evalPattern(patternExpression));
  }

  /**
   * Create a pattern matcher and evaluator.
   *
   * @param patternExpression
   * @param rightHandside
   * @return
   */
  public final IPatternMatcher evalPatternMatcher(final IExpr patternExpression,
      final IExpr rightHandside) {
    return new PatternMatcherAndEvaluator(evalPattern(patternExpression), rightHandside);
  }

  /**
   * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return the
   * input object. In &quot;quiet mode&quot; all warnings will be suppressed.
   *
   * @param expr the expression which should be evaluated
   * @return the evaluated object
   * @see EvalEngine#evalWithoutNumericReset(IExpr)
   */
  public final IExpr evalQuiet(final IExpr expr) {
    boolean quiet = isQuietMode();
    try {
      setQuietMode(true);
      return evaluate(expr);
    } finally {
      setQuietMode(quiet);
    }
  }

  /**
   * Evaluate an expression in &quot;traceless mode&quot;. If evaluation is not possible return the
   * input object. In &quot;traceless mode&quot; all trace outputs will be suppressed.
   *
   * @param expr the expression which should be evaluated in traceless mode
   * @return the evaluated object
   */
  public final IExpr evalTraceless(final IExpr expr) {
    boolean quiet = fQuietMode;
    boolean numericMode = fNumericMode;
    boolean traceMode = fTraceMode;
    try {
      setTraceMode(false);
      setQuietMode(true);
      return evalWithoutNumericReset(expr);
    } finally {
      fNumericMode = numericMode;
      fTraceMode = traceMode;
      fQuietMode = quiet;
    }
  }

  /**
   * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return <code>
   * F.NIL</code>. In &quot;quiet mode&quot; all warnings would be suppressed.
   *
   * @param expr the expression which should be evaluated
   * @return the evaluated object or {@link F#NIL} if no evaluation was possible
   * @deprecated use {@link #evalQuietNIL(IExpr)}
   */
  @Deprecated
  public final IExpr evalQuietNull(final IExpr expr) {
    return evalQuietNIL(expr);
  }

  /**
   * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return <code>
   * F.NIL</code>. In &quot;quiet mode&quot; all warnings would be suppressed.
   *
   * @param expr the expression which should be evaluated
   * @return the evaluated object or {@link F#NIL} if no evaluation was possible
   * @see EvalEngine#evalWithoutNumericReset(IExpr)
   */
  public final IExpr evalQuietNIL(final IExpr expr) {
    boolean quiet = isQuietMode();
    try {
      setQuietMode(true);
      return evaluateNIL(expr);
    } finally {
      setQuietMode(quiet);
    }
  }

  /**
   * Evaluate the rules for an AST.
   *
   * @param symbol
   * @param argsAST
   * @return <code>F.NIL</code> if no evaluation happened
   */
  public IExpr evalRules(ISymbol symbol, IAST argsAST) {
    IAST ast;
    boolean[] unevaluatedFunction = new boolean[] {false};
    if (argsAST.exists(x -> x.isAST(S.Unevaluated, 2))) {
      ast = argsAST.map(x -> {
        if (x.isUnevaluated()) {
          return unevaluatedArg1(unevaluatedFunction, x.first());
        }
        return x;
      }, 1);
    } else {
      ast = argsAST;
    }
    IExpr temp = evalUpRules(ast);
    if (temp.isPresent()) {
      return temp;
    }

    temp = evalASTBuiltinFunction(symbol, ast);
    if (temp.isPresent()) {
      return temp;
    }
    if (unevaluatedFunction[0]) {
      return ast;
    }
    return F.NIL;
  }

  private static IExpr unevaluatedArg1(IExpr arg1) {
    if (arg1.head().isFunction()) {
      return F.eval(arg1);
    }
    return arg1;
  }

  private static IExpr unevaluatedArg1(boolean[] unevaluatedFunction, IExpr arg1) {
    if (arg1.head().isFunction()) {
      unevaluatedFunction[0] = true;
      return F.eval(arg1);
    }
    return arg1;
  }

  public IExpr evalUpRules(IAST ast) {
    IExpr[] result = new IExpr[1];
    result[0] = F.NIL;
    if (ast.exists(x -> {
      if (x.isSymbol()) {
        result[0] = ((ISymbol) x).evalUpRules(ast, this);
        if (result[0].isPresent()) {
          return true;
        }
      } else if (!(x instanceof IPatternObject) && x.isPresent()) {
        result[0] = x.topHead().evalUpRules(ast, this);
        if (result[0].isPresent()) {
          return true;
        }
      }
      return false;
    })) {
      return result[0];
    }
    return F.NIL;
  }

  private IASTMutable evalSetAttributeArg(IAST ast, int i, IAST argI, IASTMutable resultList,
      boolean noEvaluation, int level) {
    IExpr expr = evalSetAttributesRecursive(argI, noEvaluation, true, level + 1);
    if (expr != argI && expr.isPresent()) {
      resultList = resultList.setIf(ast, i, expr);
    } else {
      expr = argI;
    }
    if (expr.isAST()) {
      if (((IAST) expr).size() == 2) {
        IExpr arg1 = ((IAST) expr).arg1();
        if (expr.isSqrt()) {
          resultList = resultList.setIf(ast, i, S.Power.of(this, arg1, F.C1D2));
        } else if (expr.isAST(S.Exp, 2)) {
          resultList = resultList.setIf(ast, i, S.Power.of(this, S.E, arg1));
        }
      }
    }
    return resultList;
  }

  /**
   * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest,
   * Orderless to create pattern-matching expressions directly or for the left-hand-side of a <code>
   * Set[]</code>, <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSetDelayed[]</code>
   * expression
   *
   * @param ast
   * @return <code>ast</code> if no evaluation was executed.
   * @deprecated use evalHoldPattern
   */
  @Deprecated
  public IExpr evalSetAttributes(IAST ast) {
    return evalHoldPattern(ast, false, false);
  }

  /**
   * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest,
   * Orderless to create pattern-matching expressions directly or for the left-hand-side of a <code>
   * Set[]</code>, <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSetDelayed[]</code>
   * expression
   *
   * @param ast
   * @param noEvaluation (sub-)expressions which contain no patterns should not be evaluated
   * @return <code>ast</code> if no evaluation was executed.
   * @deprecated use evalHoldPattern
   */
  @Deprecated
  public IExpr evalSetAttributes(IAST ast, boolean noEvaluation) {
    return evalHoldPattern(ast, noEvaluation, false);
  }

  private IExpr evalSetAttributesRecursive(IAST ast, boolean noEvaluation,
      boolean evalNumericFunction, int level) {
    // final ISymbol symbol = ast.topHead();
    IExpr head = ast.head();
    if (!(head instanceof IPatternObject) && !noEvaluation) {
      IExpr headResult = head.evaluate(this);
      if (headResult.isPresent()) {
        ast = ast.apply(headResult);
        head = headResult;
      }
    }
    ISymbol symbol = head.topHead();
    if (head.isSymbol()) {
      symbol = (ISymbol) head;
    }

    // if (symbol.isBuiltInSymbol()) {
    // call so that attributes may be set in
    // AbstractFunctionEvaluator#setUp() method
    // ((IBuiltInSymbol) symbol).getEvaluator();
    // }
    int headID = ast.headID();
    if (headID >= 0) {
      if (headID == ID.Blank || headID == ID.BlankSequence || headID == ID.BlankNullSequence
          || headID == ID.Pattern || headID == ID.Optional || headID == ID.OptionsPattern
          || headID == ID.Repeated || headID == ID.RepeatedNull) {
        return ((IFunctionEvaluator) ((IBuiltInSymbol) ast.head()).getEvaluator()).evaluate(ast,
            this);
      }
    }

    final int attributes = symbol.getAttributes();
    IASTMutable resultList = F.NIL;

    if ((ISymbol.HOLDALL & attributes) != ISymbol.HOLDALL) {
      final int astSize = ast.size();

      if ((ISymbol.HOLDFIRST & attributes) == ISymbol.NOATTRIBUTE) {
        // the HoldFirst attribute isn't set here
        if (astSize > 1) {
          IExpr expr = ast.arg1();
          if (expr.isAST()) {
            resultList = evalSetAttributeArg(ast, 1, (IAST) expr, resultList, noEvaluation, level);
          } else if (!(expr instanceof IPatternObject) && !noEvaluation) {
            IExpr temp = expr.evaluate(this);
            if (temp.isPresent()) {
              resultList = ast.setAtCopy(1, temp);
            }
          }
        }
      }
      if (astSize > 2) {
        if ((ISymbol.HOLDREST & attributes) == ISymbol.NOATTRIBUTE) {
          // the HoldRest attribute isn't set here
          for (int i = 2; i < astSize; i++) {
            IExpr expr = ast.get(i);
            if (expr.isAST()) {
              resultList =
                  evalSetAttributeArg(ast, i, (IAST) expr, resultList, noEvaluation, level);
            } else if (!(expr instanceof IPatternObject) && !noEvaluation) {
              resultList = resultList.setIfPresent(ast, i, expr.evaluate(this));
            }
          }
        }
      }
      if (evalNumericFunction && ((ISymbol.HOLDALL & attributes) == ISymbol.NOATTRIBUTE)) {
        IAST f = resultList.orElse(ast);
        if (f.isNumericFunction(true)) {
          IExpr temp = evalLoop(f);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    }
    if (resultList.isPresent()) {
      if (resultList.size() > 2) {
        if (ISymbol.hasFlatAttribute(attributes)) {
          // associative
          IASTAppendable result;
          if ((result = EvalAttributes.flattenDeep(resultList)).isPresent()) {
            return evalSetOrderless(result, attributes, noEvaluation, level);
          }
        }
        IExpr expr = evalSetOrderless(resultList, attributes, noEvaluation, level);
        if (expr.isPresent()) {
          return expr;
        }
      }
      return resultList;
    }

    if ((ast.getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) != 0x0000) {
      // already flattened or sorted
      return ast;
    }

    if (ISymbol.hasFlatAttribute(attributes)) {
      // associative
      IASTAppendable result;
      if ((result = EvalAttributes.flattenDeep(ast)).isPresent()) {
        return evalSetOrderless(result, attributes, noEvaluation, level);
      }
    }
    return evalSetOrderless(ast, attributes, noEvaluation, level);
  }

  /**
   * @param ast
   * @param attributes
   * @param noEvaluation
   * @param level
   * @return <code>ast</code> if no evaluation was possible
   */
  private IExpr evalSetOrderless(IAST ast, final int attributes, boolean noEvaluation, int level) {
    if (ISymbol.hasOrderlessAttribute(attributes)) {
      EvalAttributes.sortWithFlags((IASTMutable) ast);
      // if (level > 0 && !noEvaluation && ast.isFreeOfPatterns()) {
      if (!noEvaluation) {
        if (ast.isPlus()) {
          return Arithmetic.CONST_PLUS.evaluate(ast, this).orElse(ast);
        }
        if (ast.isTimes()) {
          return Arithmetic.CONST_TIMES.evaluate(ast, this).orElse(ast);
        }
      }
    }
    if (level > 0 && !noEvaluation) {
      return evaluate(ast);
    }

    return ast;
  }

  /**
   * Currently only the Rubi TagSet rules for <code>Dist()</code> are implemented
   *
   * @param ast
   * @return
   */
  private IExpr evalTagSetPlusTimes(IAST ast) {
    if (ast.isPlus()) {
      return UtilityFunctionCtors.evalRubiDistPlus(ast, this);
    } else if (ast.isTimes()) {
      return UtilityFunctionCtors.evalRubiDistTimes(ast, this);
    }
    return F.NIL;
  }

  /**
   * Evaluate the expression and return the <code>Trace[expr]</code> (i.e. all (sub-)expressions
   * needed to calculate the result).
   *
   * @param expr the expression which should be evaluated.
   * @param matcher a filter which determines the expressions which should be traced, If the matcher
   *        is set to <code>null</code>, all expressions are traced.
   * @return
   */
  public final IAST evalTrace(final IExpr expr, Predicate<IExpr> matcher) {
    IAST traceList = F.List();
    try {
      beginTrace(matcher);
      evaluate(expr);
    } finally {
      traceList = endTrace();
    }
    return traceList;
  }

  /**
   * Test if <code>expr</code> can be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param expr
   * @return <code>true</code> if the expression could be evaluated to symbol <code>True</code> and
   *         <code>false</code> in all other cases
   */
  public final boolean evalTrue(final IExpr expr) {
    if (expr.isBuiltInSymbol()) {
      if (expr.isTrue()) {
        return true;
      }
      if (expr.isFalse()) {
        return false;
      }
    }
    try {
      return evaluate(expr).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>head[arg1]</code> can be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param head
   * @param arg1
   * @return
   */
  public final boolean evalTrue(final IExpr head, final IExpr arg1) {
    try {
      return evaluate(F.unaryAST1(head, arg1)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>head[arg1, arg2]</code> can be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param head
   * @param arg1
   * @param arg2
   * @return
   */
  public final boolean evalTrue(final IExpr head, final IExpr arg1, final IExpr arg2) {
    try {
      return evaluate(F.binaryAST2(head, arg1, arg2)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>Equal[arg1, arg2]</code> can be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public final boolean evalEqual(final IExpr lhs, final IExpr rhs) {
    try {
      return evaluate(F.Equal(lhs, rhs)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>Less[arg1, arg2]</code> can be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public final boolean evalLess(final IExpr lhs, final IExpr rhs) {
    try {
      return evaluate(F.Less(lhs, rhs)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>Less[arg1, arg2, arg3]</code> can be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param arg1
   * @param arg2
   * @param arg3
   * @return
   */
  public final boolean evalLess(final IExpr arg1, final IExpr arg2, final IExpr arg3) {
    try {
      return evaluate(F.ternaryAST3(S.Less, arg1, arg2, arg3)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>LessEqual[arg1, arg2]</code> can be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public final boolean evalLessEqual(final IExpr lhs, final IExpr rhs) {
    try {
      return evaluate(F.LessEqual(lhs, rhs)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>Greater[arg1, arg2]</code> can be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public final boolean evalGreater(final IExpr lhs, final IExpr rhs) {
    try {
      return evaluate(F.Greater(lhs, rhs)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>Greater[arg1, arg2,arg3]</code> can be evaluated to <code>True</code>. If a
   * <code>org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return
   * <code>False</code>.
   *
   * @param arg1
   * @param arg2
   * @param arg3
   * @return
   */
  public final boolean evalGreater(final IExpr arg1, final IExpr arg2, final IExpr arg3) {
    try {
      return evaluate(F.ternaryAST3(S.Greater, arg1, arg2, arg3)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>GreaterEqual[arg1, arg2]</code> can be evaluated to <code>True</code>. If a
   * <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public final boolean evalGreaterEqual(final IExpr lhs, final IExpr rhs) {
    try {
      return evaluate(F.GreaterEqual(lhs, rhs)).isTrue();
    } catch (MathException fce) {
      return false;
    }
  }

  /**
   * Test if <code>expr</code> could be evaluated to {@link S#True}. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return
   * {@link S#False}.
   *
   * @param expr
   * @return {@link S#True} if the expression could be evaluated to symbol <code>True</code> and
   *         {@link S#False} in all other cases
   */
  public final IBuiltInSymbol evalSymbolTrue(final IExpr expr) {
    return evalTrue(expr) ? S.True : S.False;
  }

  /**
   * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation
   * reset the numeric mode to the value stored before the evaluation starts. If evaluation is not
   * possible return the input object. If <code>expr</code> equals {@link F#NIL} the method returns
   * {@link F#NIL}
   *
   * @param expr the object which should be evaluated
   * @return the evaluated object
   */
  public final IExpr evaluate(final IExpr expr) {
    boolean numericMode = fNumericMode;
    try {
      return evalWithoutNumericReset(expr);
    } finally {
      fNumericMode = numericMode;
    }
  }

  /**
   * Parse the given <code>expression String</code> into an IExpr and evaluate it.
   *
   * @param expression an expression in math formula notation
   * @return
   * @throws org.matheclipse.parser.client.SyntaxError if a parsing error occurs
   */
  public final IExpr evaluate(String expression) {
    return evaluate(parse(expression));
  }

  /**
   * Parse the given <code>expression String</code> into an IExpr and evaluate it.
   *
   * @param expression an expression in math formula notation
   * @param explicitTimes if <code>true</code> require times operator &quot;*&quot;
   * @return
   * @throws org.matheclipse.parser.client.SyntaxError if a parsing error occurs
   */
  public final IExpr evaluate(String expression, boolean explicitTimes) {
    return evaluate(parse(expression, explicitTimes));
  }

  /**
   * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation
   * reset the numeric mode to the value stored before the evaluation starts. If evaluation is not
   * possible return the input object.
   *
   * @param expr the object which should be evaluated
   * @return the evaluated object
   */
  public final IExpr evaluateNonNumeric(final IExpr expr) {
    boolean numericMode = fNumericMode;
    try {
      fNumericMode = false;
      return evalWithoutNumericReset(expr);

    } finally {
      fNumericMode = numericMode;
    }
  }

  /**
   * Evaluate an object and reset the numeric mode to the value before the evaluation step. If
   * evaluation is not possible or <code>expr</code> equals {@link F#NIL} return {@link F#NIL}.
   *
   * @param expr the object which should be evaluated
   * @return the evaluated object or <code>F.NIL</code> if no evaluation was possible
   */
  public final IExpr evaluateNIL(final IExpr expr) {
    if (expr.isPresent()) {
      boolean numericMode = fNumericMode;
      try {
        return evalLoop(expr);
      } finally {
        fNumericMode = numericMode;
      }
    }
    return F.NIL;
  }

  /**
   * @param expr
   * @return
   * @deprecated use {@link #evaluateNIL(IExpr)}
   */
  @Deprecated
  public final IExpr evaluateNull(final IExpr expr) {
    return evaluateNIL(expr);
  }

  /**
   * Evaluate an object without resetting the numeric mode after the evaluation step. If evaluation
   * is not possible return the input object,
   *
   * @param expr the object which should be evaluated
   * @return the evaluated object
   */
  public final IExpr evalWithoutNumericReset(final IExpr expr) {
    return evalLoop(expr).orElse(expr);
  }

  /**
   * Get the {@link FixedPrecisionApfloatHelper} for fixed precision calculations.
   *
   * @return <code>null</code> if the apfloat helper isn't set in {@link #setNumericPrecision(long)}
   *         or {@link #setNumericMode(boolean, long, int)}
   */
  public FixedPrecisionApfloatHelper apfloatHelper() {
    return fApfloatHelper;
  }

  /**
   * Get the last result (&quot;answer&quot;) expression of this evaluation engine.
   *
   * @return <code>null</code> if no answer is stored in the evaluation engine.
   */
  public IExpr getAnswer() {
    return fAnswer;
  }

  /**
   * Get the currently available assumptions if possible.
   *
   * @return <code>null</code> if no assumptions are available
   */
  public IAssumptions getAssumptions() {
    return fAssumptions;
  }

  /**
   * Returns the value associated with key in the {@link EvalEngine#globalASTCache}, or null if
   * there is no cached value for key.
   * 
   * @param key
   */
  public IExpr getCache(IAST key) {
    return globalASTCache.getIfPresent(key);
  }

  /**
   * Returns the object associated with key in the {@link EvalEngine#globalObjectCache}, or null if
   * there is no cached value for key.
   * 
   * @param key
   */
  public Object getObjectCache(IExpr key) {
    return globalObjectCache.getIfPresent(key);
  }

  public final Context getContext() {
    return fContextPath.currentContext();
  }

  public String get$Input() {
    if (f$Input == null) {
      f$Input = "";
    }
    return f$Input;
  }

  public String get$InputFileName() {
    if (f$InputFileName == null) {
      f$InputFileName = "";
    }
    return f$InputFileName;
  }

  public void set$Input(String input) {
    if (input == null) {
      f$Input = "";
    } else {
      f$Input = input;
    }
  }

  public void set$InputFileName(String inputFileName) {
    if (inputFileName == null) {
      f$InputFileName = "";
    } else {
      f$InputFileName = inputFileName;
    }
  }

  public ContextPath getContextPath() {
    return fContextPath;
  }

  public PrintStream getErrorPrintStream() {
    return fErrorPrintStream != null ? fErrorPrintStream : System.err;
  }

  public int getIterationLimit() {
    if (fStopRequested) {
      throw TimeoutException.TIMED_OUT;
    }
    return fIterationLimit;
  }

  public String getMessageShortcut() {
    return fMessageShortcut;
  }

  /**
   * Get the list of modified variables
   *
   * @return <code>null</code> if the set is not defined
   */
  public Set<ISymbol> getModifiedVariables() {
    return fModifiedVariablesList;
  }

  public long getNumericPrecision() {
    if (fApfloatHelper != null) {
      return fApfloatHelper.precision();
    }
    return ParserConfig.MACHINE_PRECISION - 1;
  }

  /**
   * Get significant figures for output floating point numbers
   *
   * @return
   */
  public int getSignificantFigures() {
    return fSignificantFigures;
  }

  public EvalHistory getEvalHistory() {
    return fEvalHistory;
  }

  public OptionsStack pushOptionsStack() {
    fOptionsStack.push();
    return fOptionsStack;
  }

  /**
   * Associates value with key in the {@link EvalEngine#globalASTCache}. If the cache previously
   * contained a value associated with key, the old value is replaced by value.
   * 
   * @param key
   * @param value
   */
  public void putCache(IAST key, IExpr value) {
    globalASTCache.put(key, value);
  }

  /**
   * Associates value with key in the {@link EvalEngine#globalObjectCache}. If the cache previously
   * contained a value associated with key, the old value is replaced by value.
   * 
   * @param key
   * @param value
   */
  public void putObjectCache(IExpr key, Object value) {
    globalObjectCache.put(key, value);
  }

  public void popOptionsStack() {
    fOptionsStack.pop();
  }

  public IdentityHashMap<ISymbol, IASTAppendable> peekOptionsStack() {
    return fOptionsStack.peek();
  }

  public Iterator<IdentityHashMap<ISymbol, IASTAppendable>> optionsStackIterator() {
    return fOptionsStack.iterator();
  }

  public PrintStream getOutPrintStream() {
    return fOutPrintStream != null ? fOutPrintStream : System.out;
  }

  /**
   * Get the reap list object associated to the most enclosing <code>Reap()</code> statement. The
   * even indices in <code>java.util.List</code> contain the tag defined in <code>Sow()</code>. If
   * no tag is defined in <code>Sow()</code> tag <code>F.None</code> is used. The odd indices in
   * <code>java.util.List</code> contain the associated reap list for the tag.
   *
   * @return the reapList
   */
  public java.util.List<IExpr> getReapList() {
    return fReapList;
  }

  public int getRecursionCounter() {
    if (fStopRequested) {
      throw TimeoutException.TIMED_OUT;
    }
    return fRecursionCounter;
  }

  /** @return */
  public int getRecursionLimit() {
    if (fStopRequested) {
      throw TimeoutException.TIMED_OUT;
    }
    return fRecursionLimit;
  }

  /**
   * The remaining time in seconds for function <code>TimeRemaining</code>.
   *
   * @return <code>-1.0</code> for Infinity. The remaining time in seconds otherwise.
   */
  public double getRemainingSeconds() {
    long timeConstrained = fTimeConstrainedMillis;
    if (timeConstrained < 0) {
      return -1.0;
    }
    long timeRemaining = (timeConstrained - System.currentTimeMillis());
    if (timeRemaining < 0) {
      timeRemaining = 0;
    }
    return (timeRemaining) / 1000.0;
  }

  public long getTimeConstrainedMillis() {
    return fTimeConstrainedMillis;
  }

  public long getSeconds() {
    return fSeconds;
  }

  /** @return */
  public String getSessionID() {
    return fSessionID;
  }

  /**
   * Get the current stack of expression evaluation.
   *
   * @return
   */
  public Deque<IExpr> getStack() {
    return fStack;
  }

  /**
   * Get the defined step listener or <code>null</code> if no listener is assigned.
   *
   * @return <code>null</code> if no step listener is assigned.
   */
  public IEvalStepListener getStepListener() {
    return fTraceStack;
  }

  /**
   * Increment the {@link S#Module} variables counter by 1 and return the result.
   *
   * @return the module counter
   */
  public static long incModuleCounter() {
    return MODULE_COUNTER.incrementAndGet();
  }

  /**
   * Increment the {@link S#Module} variables counter by 1 and append it to the given prefix.
   *
   * @param prefix
   * @return
   */
  public static String uniqueName(String prefix) {
    return prefix + MODULE_COUNTER.incrementAndGet();
  }

  /**
   * Reset the {@link S#Module} or {@link S#With} variables counter to <code>0</code>. Used only in
   * unit tests.
   * <p>
   * <b>Don't reset for reusable EvalEngine's!</b>
   */
  public static void resetModuleCounter4JUnit() {
    MODULE_COUNTER = new AtomicLong();
  }

  /**
   * Increment the counter, how often the experimental message was printed.
   *
   * @param symbol
   */
  public void incExperimentalCounter(IBuiltInSymbol symbol) {
    Integer counter = experimatalSymbols.get(symbol);
    if (counter == null) {
      experimatalSymbols.put(symbol, 1);
      return;
    }
    experimatalSymbols.put(symbol, ++counter);
  }

  /**
   * Increment the recursion counter by 1 and return the result.
   *
   * @return
   */
  public int incRecursionCounter() {
    return ++fRecursionCounter;
  }

  /** Initialize this <code>EvalEngine</code> */
  public final void init() {
    stackBegin();
    fAnswer = null;
    fAssumptions = null;
    S.$Assumptions.clearValue();
    // fNumericPrecision = 15;
    fApfloatHelper = null;
    fApfloatHelperDouble = null;
    fSignificantFigures = 6;
    fRecursionCounter = 0;
    fNumericMode = false;
    fTogetherMode = false;
    fNoSimplifyMode = false;
    fEvalLHSMode = false;
    fEvalRHSMode = false;
    fDisabledTrigRules = false;
    fOnOffMode = false;
    fOnOffUnique = false;
    fOnOffUniqueMap = null;
    fOnOffMap = null;
    fDollarSymbolMap = null;
    fTraceMode = false;
    fTraceStack = null;
    fStopRequested = false;
    fCopiedEngine = null;
    fSeconds = 0;
    fModifiedVariablesList = null;
    fMessageShortcut = null;
    fContextPathStack = new ArrayDeque<ContextPath>();
    fContextPath = ContextPath.initialContext();
    f$Input = "";
    f$InputFileName = "";
    fOptionsStack = new OptionsStack();
    rubiASTCache = null;
    rememberMap = new IdentityHashMap<Object, IExpr>();
  }

  public Deque<IExpr> stackBegin() {
    fStack = new ArrayDeque<IExpr>(256);
    return fStack;
  }

  public void stackPush(IExpr expr) {
    fStack.push(expr);
  }

  public IExpr stackPop() {
    if (fStack.isEmpty()) {
      return F.NIL;
    }
    return fStack.pop();
  }

  /**
   * Check if the engine is in arbitrary precision mode and that <code>ApfloatNum</code> number type
   * should be used instead of the <code>Num</code> type and the <code>ApcomplexNum</code> number
   * type should be used instead of the <code>ComplexNum</code> type for numeric evaluations.
   *
   * @return <code>true</code> if the required precision is greater than <code>
   *     EvalEngine.DOUBLE_PRECISION</code>
   * @see ApfloatNum
   * @see ApcomplexNum
   * @see #isDoubleMode()
   */
  public final boolean isArbitraryMode() {
    return getNumericPrecision() > ParserConfig.MACHINE_PRECISION;
  }

  /**
   * The engine evaluates the left-hand-side of a <code>Set, SetDelayed,...</code> expression.
   *
   * @return
   */
  public final boolean isEvalLHSMode() {
    return fEvalLHSMode;
  }

  public final boolean isEvalRHSMode() {
    return fEvalRHSMode;
  }

  public final void setEvalRHSMode(boolean evalRHSMode) {
    fEvalRHSMode = evalRHSMode;
  }

  public final boolean isFileSystemEnabled() {
    return fFileSystemEnabled;
  }

  /** @return <code>true</code> if the EvalEngine runs in numeric mode. */
  public final boolean isNumericMode() {
    return fNumericMode;
  }

  /**
   * @return <code>true</code> if the EvalEngine runs in numeric mode and Java <code>double</code>
   *         numbers should be used for evaluating numeric functions.
   * @see #isArbitraryMode()
   */
  public final boolean isDoubleMode() {
    return fNumericMode && !isArbitraryMode();
  }

  /**
   * Check if the <code>On()</code> has enabled the interactive trace
   *
   * @return
   */
  public final boolean isOnOffMode() {
    return fOnOffMode;
  }

  /**
   * Check if the appending of expressions to the history list for the <code>Out[]</code> function
   * is enabled. If enabled, the special variable <code>$ans</code> returns the result from the last
   * evluation done with this evaluation engine.
   *
   * @return
   */
  public final boolean isOutListDisabled() {
    return fOutListDisabled;
  }

  public final boolean isPackageMode() {
    return fPackageMode;
  }

  /**
   * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages
   * are shown in the evaluation).
   *
   * @return
   */
  public final boolean isQuietMode() {
    return fQuietMode;
  }

  /**
   * If <code>true</code> this engine doesn't distinguish between lower and upper case identifiers,
   * with the exception of identifiers with length 1.
   *
   * @return the fRelaxedSyntax
   */
  public final boolean isRelaxedSyntax() {
    return fRelaxedSyntax;
  }

  public boolean isDisabledTrigRules() {
    return fDisabledTrigRules;
  }

  /** @return Returns the stopRequested. */
  public final boolean isStopRequested() {
    return fStopRequested;
  }

  /**
   * Basic arithmetic operations (especially in linear algebra functions) like for example
   * {@link IExpr#plus(IExpr)} and {@link IExpr#times(IExpr)} are tried to be simplified with a
   * wrpped {@link S#Together} command during the evaluation of the multiplication.
   *
   * @return
   * @see #setTogetherMode(boolean)
   */
  public final boolean isTogetherMode() {
    return fTogetherMode;
  }

  /**
   * If <code>true</code> the engine does no simplification of &quot;negative {@link S#Plus}
   * expressions&quot; inside {@link S#Times} expressions in common subexpression determining.
   * 
   */
  public final boolean isNoSimplifyMode() {
    return fNoSimplifyMode;
  }

  /**
   * If the trace mode is set the system writes an evaluation trace list or if additionally the
   * <i>stop after evaluation mode</i> is set returns the first evaluated result.
   *
   * @return
   */
  public final boolean isTraceMode() {
    return fTraceMode;
  }

  /**
   * Parse the given <code>expression String</code> into an IExpr without evaluation.
   *
   * @param expression an expression in math formula notation
   * @return
   * @throws org.matheclipse.parser.client.SyntaxError if a parsing error occurs
   */
  public final IExpr parse(String expression) throws SyntaxError {
    return parse(expression, ParserConfig.EXPLICIT_TIMES_OPERATOR);
  }

  /**
   * Parse the given <code>expression String</code> into an IExpr without evaluation.
   *
   * @param expression an expression in math formula notation
   * @param explicitTimes if <code>true</code> require times operator &quot;*&quot;
   * @return
   * @throws org.matheclipse.parser.client.SyntaxError if a parsing error occurs
   */
  public final IExpr parse(String expression, boolean explicitTimes) {
    final ExprParser parser = new ExprParser(this, ExprParserFactory.RELAXED_STYLE_FACTORY,
        fRelaxedSyntax, false, explicitTimes);
    return parser.parse(expression);
  }

  /**
   * Returns the level with which messages associated to this engine should be logged.
   *
   * <p>
   * Returns {@link Level#ERROR} unless this engine is in {@link #isQuietMode() quiet-mode}, then
   * {@link Level#DEBUG} is returned.
   *
   * @return the logger-level of this engine. ERROR by default and DEBUG if this engine is in
   *         quite-mode.
   */
  public Level getLogLevel() {
    return isQuietMode() ? Level.DEBUG : Level.ERROR;
  }

  /**
   * Reset the numeric mode flags and the recursion counter.
   *
   * <p>
   * <b>Note:</b> This method should be called before the parsing of a string expression.
   */
  private void reset() {
    stackBegin();
    // fNumericPrecision = 15;
    fApfloatHelper = null;
    fApfloatHelperDouble = null;
    fSignificantFigures = 6;
    fNumericMode = false;
    fEvalLHSMode = false;
    fEvalRHSMode = false;
    fDisabledTrigRules = false;
    fRecursionCounter = 0;
    fTogetherMode = false;
    fNoSimplifyMode = false;
    fTraceMode = false;
    fTraceStack = null;
    fStopRequested = false;
    fCopiedEngine = null;
    fSeconds = 0;
    fModifiedVariablesList = null;
    fMessageShortcut = null;
    rubiASTCache = null;
    fOptionsStack = new OptionsStack();

    if (fOnOffMode && fOnOffUnique) {
      fOnOffUniqueMap = new HashMap<IExpr, IExpr>();
    }
  }

  /**
   * If <code>nHoldAttribute</code> ( {@link ISymbol#NHOLDFIRST} or {@link ISymbol#NHOLDREST} ) are
   * set in attributes, set numeric mode to false, otherwise set <code>localNumericMode</code>
   *
   * @param attributes
   * @param nHoldAttribute {@link ISymbol#NHOLDFIRST} or {@link ISymbol#NHOLDREST} attribute bitmask
   * @param localNumericMode
   */
  private void selectNumericMode(final int attributes, final int nHoldAttribute,
      boolean localNumericMode) {
    if ((nHoldAttribute & attributes) == nHoldAttribute) {
      fNumericMode = false;
    } else {
      fNumericMode = localNumericMode;
    }
  }

  /**
   * Set the assumptions for this evaluation engine
   *
   * @param assumptions
   */
  public void setAssumptions(IAssumptions assumptions) {
    this.fAssumptions = assumptions;
  }

  /**
   * Set the assigned value for a '$...' {@link IBuiltInSymbol}.
   * 
   * @param dollarSymbol
   * @param value
   */
  public void setDollarValue(IBuiltInSymbol dollarSymbol, IExpr value) {
    if (fDollarSymbolMap == null) {
      fDollarSymbolMap = new IdentityHashMap<IBuiltInSymbol, IExpr>();
    }
    fDollarSymbolMap.put(dollarSymbol, value);
  }

  public void setContextPath(ContextPath contextPath) {
    this.fContextPath = contextPath;
  }

  public void setContext(Context context) {
    this.fContextPath.setCurrentContext(context);
  }

  public void setErrorPrintStream(final PrintStream errorPrintStream) {
    fErrorPrintStream = errorPrintStream;
  }

  public void setFileSystemEnabled(boolean fFileSystemEnabled) {
    this.fFileSystemEnabled = fFileSystemEnabled;
  }

  public void setIterationLimit(final int i) {
    fIterationLimit = i;
  }

  public void setMessageShortcut(final String messageShortcut) {
    fMessageShortcut = messageShortcut;
  }

  /**
   * Set the numeric mode for numeric calculations.
   *
   * @param numericMode if <code>true</code> evaluate in floating number mode
   */
  public void setNumericMode(final boolean numericMode) {
    fNumericMode = numericMode;
  }

  /**
   * Set the numeric mode and precision of numeric calculations.
   *
   * @param numericMode if <code>true</code> evaluate in floating number mode
   * @param precision
   * @param figures significant figures which should be displayed in output forms
   */
  public void setNumericMode(final boolean numericMode, long precision, int figures) {
    fNumericMode = numericMode;
    setNumericPrecision(precision);
    fSignificantFigures = figures;
  }

  public void setNumericPrecision(long precision) {
    if (ParserConfig.MACHINE_PRECISION > precision) {
      fApfloatHelper = null;
    } else {
      fApfloatHelper = new FixedPrecisionApfloatHelper(precision);
    }
  }

  public void setSignificantFigures(int figures) {
    fSignificantFigures = figures;
  }

  /**
   * @param outListDisabled if <code>false</code> create a <code>
   *     LastCalculationsHistory(historyCapacity)</code>, otherwise no history of the last
   *        calculations will be saved and the <code>Out()</code> function (or % operator) will be
   *        unevaluated.
   * @param historyCapacity the number of last entries of the calculations which should be stored.
   */
  public void setOutListDisabled(boolean outListDisabled, short historyCapacity) {
    if (outListDisabled == false) {
      if (fEvalHistory == null) {
        fEvalHistory = new EvalHistory(historyCapacity);
      }
    } else {
      fEvalHistory = null;
    }
    this.fOutListDisabled = outListDisabled;
  }

  /**
   * Set the mode for the <code>On()</code> or <code>Off()</code> function
   *
   * @param onOffMode if <code>true</code> every evaluation step will be printd to the defined out
   *        stream.
   * @param headSymbolsMap the header symbols which should trigger an output in the trace
   * @param uniqueTrace the output is printed only once for a combination of _unevaluated_ input
   *        expression and _evaluated_ output expression.
   */
  public void setOnOffMode(final boolean onOffMode, Map<ISymbol, ISymbol> headSymbolsMap,
      boolean uniqueTrace) {
    fOnOffMode = onOffMode;
    fOnOffMap = headSymbolsMap;
    fOnOffUnique = uniqueTrace;
    if (uniqueTrace) {
      fOnOffUniqueMap = new HashMap<IExpr, IExpr>();
    }
  }

  public void setOptionsPattern(ISymbol lhsHead, IPatternMap patternMap) {
    IdentityHashMap<ISymbol, IASTAppendable> optionsPattern = fOptionsStack.peek();
    boolean setHead = patternMap.setOptionsPattern(this, lhsHead);
    if (!optionsPattern.isEmpty()) {
      for (Map.Entry<ISymbol, IASTAppendable> element : optionsPattern.entrySet()) {
        ISymbol symbol = element.getKey();
        IAST list = OptionsPattern.optionsList(element.getKey(), true);
        if (list.size() > 1) {
          IASTAppendable tempList = optionsPattern.get(symbol);
          if (tempList == null) {
            tempList = F.ListAlloc(10);
            optionsPattern.put(symbol, tempList);
          }
          tempList.appendArgs(list);
        }
      }
    }
    if (setHead) {
      optionsPattern.put(S.LHS_HEAD, F.ast(lhsHead));
    }
  }

  public void setOutListDisabled(EvalHistory history) {
    this.fEvalHistory = history;
    this.fOutListDisabled = false;
  }

  public void setOutPrintStream(final PrintStream outPrintStream) {
    fOutPrintStream = outPrintStream;
  }

  public void setPackageMode(boolean packageMode) {
    fPackageMode = packageMode;
  }

  public void setPrintStreamsOf(EvalEngine engine) {
    this.fOutPrintStream = engine.fOutPrintStream;
    this.fErrorPrintStream = engine.fErrorPrintStream;
  }

  /**
   * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages
   * are showw in the evaluation).
   *
   * @param quietMode
   */
  public void setQuietMode(boolean quietMode) {
    this.fQuietMode = quietMode;
  }

  /** @param reapList the reapList to set */
  public void setReapList(java.util.List<IExpr> reapList) {
    this.fReapList = reapList;
  }

  /** @param i */
  public void setRecursionLimit(final int i) {
    fRecursionLimit = i;
  }

  /** @param fRelaxedSyntax the fRelaxedSyntax to set */
  public void setRelaxedSyntax(boolean fRelaxedSyntax) {
    this.fRelaxedSyntax = fRelaxedSyntax;
  }

  /**
   * Set the time in milliseconds then the current TimeConstrained operation should stop. <code>-1
   * </code> is set for Infinity
   *
   * @param timeConstrainedMillis
   */
  public void setTimeConstrainedMillis(final long timeConstrainedMillis) {
    fTimeConstrainedMillis = timeConstrainedMillis;
  }

  public void setSeconds(long fSeconds) {
    this.fSeconds = fSeconds;
  }

  public Random getRandom() {
    return fRandom;
  }

  public long getSeed() {
    return fRandomSeed;
  }

  public synchronized void setSeed(long seed) {
    fRandomSeed = seed;
    fRandom.setSeed(seed);
  }

  /** @param string */
  public void setSessionID(final String string) {
    fSessionID = string;
  }

  /**
   * Set the stack which should be active for the evaluation engine.
   *
   * @param stack
   */
  public void setStack(Deque<IExpr> stack) {
    fStack = stack;
  }

  /**
   * Set the step listener for this evaluation engine. The method also calls <code>
   * setTraceMode(true)</code> to enable the trace mode. The caller is responsible for calling
   * <code>setTraceMode(false)</code> if no further listening is desirable.
   *
   * @param stepListener the listener which should listen to the evaluation steps.
   */
  public void setStepListener(IEvalStepListener stepListener) {
    setTraceMode(true);
    fTraceStack = stepListener;
  }

  /** @param stopRequested The stopRequested to set. */
  public void setStopRequested(final boolean stopRequested) {
    fStopRequested = stopRequested;
    if (stopRequested && fCopiedEngine != null) {
      fCopiedEngine.setStopRequested(true);
    }
    fCopiedEngine = null;
  }

  /**
   * Basic artihmetic operations (especially in linear algebra functions) like for example
   * {@link IExpr#plus(IExpr)} and {@link IExpr#times(IExpr)} are tried to be simplified with a
   * {@link S#Together} command during the evaluation of the multiplication if the parameter is set
   * to <code>true</code>.
   *
   * @param togetherMode if <code>true</code> the evaluation will be wrapped by a {@link S#Together}
   *        function in basic multiplication
   * @see #isTogetherMode()
   */
  public void setTogetherMode(boolean togetherMode) {
    this.fTogetherMode = togetherMode;
  }

  /**
   * If set to <code>true</code> the engine does no simplification of &quot;negative {@link S#Plus}
   * expressions&quot; inside {@link S#Times} expressions in common subexpression determining.
   * 
   * @param noSimplifyMode
   */
  public void setNoSimplifyMode(boolean noSimplifyMode) {
    this.fNoSimplifyMode = noSimplifyMode;
  }

  /** @param b */
  public void setTraceMode(final boolean b) {
    this.fTraceMode = b;
  }

  /**
   * If <code>true</code> disable automatic trigonometric function simplification rules.
   * 
   * @param value
   */
  public void setDisabledTrigRules(boolean value) {
    fDisabledTrigRules = value;
  }

  /**
   * The size of the <code>Out[]</code> list
   *
   * @return
   */
  public int sizeOut() {
    return fEvalHistory.size();
  }

  public void stopRequest() {
    setStopRequested(true);
  }

  /**
   * Thread through all lists in the arguments of the IAST (i.e. the ast's head has the attribute
   * <code>ISymbol.LISTABLE</code>) example: <code>Sin[{2,x,Pi}] ==> {Sin[2],Sin[x],Sin[Pi]}</code>
   *
   * @param ast
   * @param commandHead TODO
   * @param messageShortcut TODO
   * @return the resulting ast with the <code>argHead</code> threaded into each ast argument or
   *         <code>F.NIL</code>
   */
  public IASTMutable threadASTListArgs(final IAST ast, ISymbol commandHead,
      String messageShortcut) {
    ISymbol[] refHeadType = new ISymbol[] {null};
    int[] refArgSize = new int[] {-1};
    IAssociation[] refAssociation = new IAssociation[] {null};
    if (ast.exists(x -> isValidListable(x, ast, commandHead, messageShortcut, refHeadType,
        refArgSize, refAssociation))) {
      return F.NIL;
    }
    if (refArgSize[0] != -1) {
      IASTMutable result = EvalAttributes.threadList(ast, refHeadType[0], ast.head(), refArgSize[0],
          refAssociation[0]);
      result.addEvalFlags(IAST.IS_LISTABLE_THREADED);
      return result;
    }
    ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
    return F.NIL;
  }

  private boolean isValidListable(IExpr expr, final IAST ast, ISymbol commandHead,
      String messageShortcut, ISymbol[] refHeadType, int[] refArgSize,
      IAssociation[] refAssociation) {
    if (expr.isList()) {
      if (refHeadType[0] == null) {
        refHeadType[0] = S.List;
      }
      if (refArgSize[0] < 0) {
        refArgSize[0] = ((IAST) expr).argSize();
      } else {
        if (refArgSize[0] != ((IAST) expr).argSize()) {
          // tdlen: Objects of unequal length in `1` cannot be combined.
          Errors.printMessage(commandHead, messageShortcut, F.list(ast), EvalEngine.get());
          // ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
          return true;
        }
      }
    } else if (expr.isSparseArray()) {
      if (refHeadType[0] == null) {
        refHeadType[0] = S.SparseArray;
      }
      ISparseArray sp = (ISparseArray) expr;
      int[] dimensions = sp.getDimension();
      if (dimensions.length > 0) {
        if (refArgSize[0] < 0) {
          refArgSize[0] = dimensions[0];
        } else {
          if (refArgSize[0] != dimensions[0]) {
            // Objects of unequal length in `1` cannot be combined.
            Errors.printMessage(S.Thread, "tdlen", F.list(ast), EvalEngine.get());
            // ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
            return true;
          }
        }
      }
    } else {
      if (expr.isAssociation()) {
        IAssociation association = (IAssociation) expr;
        refHeadType[0] = S.Association;
        if (refArgSize[0] < 0) {
          refArgSize[0] = association.argSize();
        }
        if (refArgSize[0] != association.argSize()) {
          // tdlen: Objects of unequal length in `1` cannot be combined.
          Errors.printMessage(commandHead, messageShortcut, F.list(ast), EvalEngine.get());
          // ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
          return true;
        }
        if (refAssociation[0] != null) {
          if (refAssociation[0].size() != association.size()) {
            // The arguments `1` and `2` in `3` are incompatible.
            Errors.printMessage(S.Association, "incmp", F.List(refAssociation[0], association, ast),
                this);
            return true;
          }
          for (int i = 1; i < association.size(); i++) {
            if (!refAssociation[0].isKey(association.getRule(i).first())) {
              // The arguments `1` and `2` in `3` are incompatible.
              Errors.printMessage(S.Association, "incmp",
                  F.List(refAssociation[0], association, ast), this);
              return true;
            }
          }
        }
        refAssociation[0] = association;
      }
    }
    return false;
  }

  /**
   * Pre-evaluate each argument of the <code>ast</code> by first (forward-) substituting all
   * variables with dummy variables, evaluating the new expression and (backward-) substituting all
   * dummy variables with the original variables.
   *
   * @param ast
   * @param offset number of the argument, there the pre-evaluation should start
   * @return
   */
  public IAST preevalForwardBackwardAST(final IAST ast, int offset) {
    IASTMutable preevaled = F.NIL;
    for (int i = offset; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isAST()) {
        arg = preevalForwardBackward((IAST) arg);
        if (arg.isPresent()) {
          if (preevaled.isNIL()) {
            preevaled = ast.copy();
          }
          preevaled.set(i, arg);
        }
      }
    }
    return preevaled.orElse(ast);
  }

  private IExpr preevalForwardBackward(final IAST arg1) {
    VariablesSet variablesSet = new VariablesSet(0, arg1);
    IAST moduleVariablesList = variablesSet.getVarList();

    java.util.List<IExpr> reapList = getReapList();
    boolean quietMode = isQuietMode();
    try {
      setQuietMode(true);
      setReapList(null);
      final java.util.IdentityHashMap<IExpr, ISymbol> variablesMap =
          new IdentityHashMap<IExpr, ISymbol>();
      final java.util.IdentityHashMap<ISymbol, IExpr> dummyVariablesMap =
          new IdentityHashMap<ISymbol, IExpr>();
      final String varAppend = uniqueName("$");
      for (int i = 1; i < moduleVariablesList.size(); i++) {
        IExpr oldSymbol = moduleVariablesList.get(i);
        ISymbol newSymbol = F.Dummy(oldSymbol.toString() + varAppend);
        variablesMap.put(oldSymbol, newSymbol);
        dummyVariablesMap.put(newSymbol, oldSymbol);
      }
      // forward substitution
      IExpr expr = arg1.replaceAll(variablesMap);
      if (expr.isPresent()) {
        IExpr temp = evaluate(expr);
        // backward substitution
        return temp.replaceAll(dummyVariablesMap).orElse(temp);
      }
    } finally {
      setQuietMode(quietMode);
      setReapList(reapList);
    }
    return F.NIL;
  }

  /**
   * Get the {@link FixedPrecisionApfloatHelper} for fixed precision calculations.
   *
   */
  public static FixedPrecisionApfloatHelper getApfloat() {
    FixedPrecisionApfloatHelper h = get().fApfloatHelper;
    if (h == null) {
      h = new FixedPrecisionApfloatHelper(Config.MAX_PRECISION_APFLOAT - 1);
    }
    return h;
  }


  /**
   * Get the {@link FixedPrecisionApfloatHelper} for calculations with
   * {@link ParserConfig#MACHINE_PRECISION}.
   * 
   * @return
   */
  public static FixedPrecisionApfloatHelper getApfloatDouble() {
    FixedPrecisionApfloatHelper h = get().fApfloatHelperDouble;
    if (h == null) {
      h = new FixedPrecisionApfloatHelper(ParserConfig.MACHINE_PRECISION + 1);
    }
    return h;
  }

  /**
   * Return the assigned value for a '$...' {@link IBuiltInSymbol}.
   * 
   * @param dollarSymbol
   * @return {@link F#NIL} if no value is assigned.
   */
  public IExpr getDollarValue(IBuiltInSymbol dollarSymbol) {
    if (fDollarSymbolMap != null) {
      IExpr expr = fDollarSymbolMap.get(dollarSymbol);
      return expr == null ? F.NIL : expr;
    }
    return F.NIL;
  }

  /**
   * Return the counter, how often the experimental message was printed.
   *
   * @param symbol
   * @return
   */
  public int getExperimentalCounter(IBuiltInSymbol symbol) {
    Integer counter = experimatalSymbols.get(symbol);
    if (counter == null) {
      return 0;
    }
    return counter;
  }

  /**
   * Set the{@link FixedPrecisionApfloatHelper} instance.
   *
   * @param helper
   */
  public static void setApfloat(final FixedPrecisionApfloatHelper helper) {
    get().fApfloatHelper = helper;
  }


}
