package org.matheclipse.core.eval;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.PatternMatching;
import org.matheclipse.core.builtin.Programming;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
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
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISignedNumber;
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
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.math.MathException;

import com.google.common.cache.Cache;

/**
 * The main evaluation algorithms for the .Symja computer algebra system. A single <code>EvalEngine
 * </code> is associated with the current thread through a <a
 * href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
 */
public class EvalEngine implements Serializable {

  private static final Logger logger = LogManager.getLogger(EvalEngine.class);

  private static final IStringX EVALUATION_LOOP = StringX.valueOf("EvalLoop");

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

  /** */
  private static final long serialVersionUID = 8402201556123198590L;

  public transient Cache<IAST, IExpr> rememberASTCache = null;

  public transient IdentityHashMap<Object, IExpr> rememberMap = null;

  public static final boolean DEBUG = false;

  private static AtomicLong MODULE_COUNTER = new AtomicLong();

  private static final transient ThreadLocal<EvalEngine> instance =
      new ThreadLocal<EvalEngine>() {

        @Override
        public EvalEngine initialValue() {
          return new EvalEngine("ThreadLocal", 0, System.out, true);
        }
      };

  /**
   * Get the thread local evaluation engine instance
   *
   * @return
   */
  public static EvalEngine get() {
    return instance.get();
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
    return precision > FEConfig.MACHINE_PRECISION;
  }

  /**
   * Removes the current thread's value for the EvalEngine's thread-local variable.
   *
   * @see java.lang.ThreadLocal#remove()
   */
  public static void remove() {
    instance.remove();
  }

  /**
   * Set the thread local evaluation engine instance
   *
   * @param engine the evaluation engine
   */
  public static void set(final EvalEngine engine) {
    instance.set(engine);
  }

  /**
   * Set the thread local evaluation engine instance and reset the engines states (numeric mode
   * flags, recursion counter,...).
   *
   * <p>Note: This method should be called before the parsing of a string expression.
   *
   * @param engine
   */
  public static void setReset(final EvalEngine engine) {
    instance.set(engine);
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
   * if <code>true</code> the engine evaluates in &quot;F.Together(expr)&quot; in IExpr#times()
   * method.
   */
  transient boolean fTogetherMode;

  transient boolean fEvalLHSMode;

  transient boolean fEvalRHSMode;

  /** @see Config#isFileSystemEnabled(); */
  transient boolean fFileSystemEnabled;

  transient String fSessionID;

  transient String fMessageShortcut;

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

  transient String f$Input = "";

  transient String f$InputFileName = "";

  /** The precision for numeric operations. */
  //  protected transient long fNumericPrecision;

  protected transient FixedPrecisionApfloatHelper fApfloatHelper;

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

  /**
   * The reap list object associated to the most enclosing <code>Reap()</code> statement. The even
   * indices in <code>java.util.List</code> contain the tag defined in <code>Sow()</code>. If no tag
   * is defined in <code>Sow()</code> tag <code>F.None</code> is used. The odd indices in <code>
   * java.util.List</code> contain the associated reap list for the tag.
   */
  private transient java.util.List<IExpr> fReapList = null;

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
  transient HashMap<IExpr, IExpr> fOnOffUniqueMap = null;

  /**
   * If not null, this map contains the header symbols for which the interactive trace should be
   * printed.
   */
  transient IdentityHashMap<ISymbol, ISymbol> fOnOffMap = null;

  transient ArrayDeque<IExpr> fStack;

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
   * If <code>true</code> the engine throws an error if an error message is printed during
   * evaluation.
   */
  transient boolean fThrowError = false;

  /**
   * A single <code>EvalEngine</code> is associated with the current thread through a <a
   * href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
   */
  public EvalEngine() {
    this("", 0, System.out, false);
  }

  /**
   * Constructor for an evaluation engine. A single <code>EvalEngine</code> is associated with the
   * current thread through a <a
   * href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
   *
   * @param relaxedSyntax if <code>true</code>, the parser doesn't distinguish between upper and
   *     lower case identifiers
   */
  public EvalEngine(boolean relaxedSyntax) {
    this("", 0, System.out, relaxedSyntax);
  }

  // static public int MAX_THREADS_COUNT = 10;

  /**
   * Constructor for an evaluation engine. A single <code>EvalEngine</code> is associated with the
   * current thread through a <a
   * href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
   *
   * @param sessionID an ID which uniquely identifies this session
   * @param recursionLimit the maximum allowed recursion limit (if set to zero, no limit will be
   *     checked)
   * @param iterationLimit the maximum allowed iteration limit (if set to zero, no limit will be
   *     checked)
   * @param outStream the output print stream
   * @param errorStream the print stream for error messages
   * @param relaxedSyntax if <code>true</code>, the parser doesn't distinguidh between upper and
   *     lower case identifiers
   */
  public EvalEngine(
      final String sessionID,
      final int recursionLimit,
      final int iterationLimit,
      final PrintStream outStream,
      PrintStream errorStream,
      boolean relaxedSyntax) {
    fOptionsStack = new OptionsStack();
    fSessionID = sessionID;
    // fExpressionFactory = f;
    fRecursionLimit = recursionLimit;
    fIterationLimit = iterationLimit;
    fOutPrintStream = outStream;
    if (errorStream == null) {
      fErrorPrintStream = outStream;
    } else {
      fErrorPrintStream = errorStream;
    }
    fRelaxedSyntax = relaxedSyntax;
    fOutListDisabled = true;
    // fNamespace = fExpressionFactory.getNamespace();

    init();
    // fContextPath = new ContextPath();
  }

  /**
   * Constructor for an evaluation engine. A single <code>EvalEngine</code> is associated with the
   * current thread through a <a
   * href="https://en.wikipedia.org/wiki/Thread-local_storage">ThreadLocal</a> mechanism.
   *
   * @param sessionID an ID which uniquely identifies this session
   * @param recursionLimit the maximum allowed recursion limit (if set to zero, no limit will be
   *     checked)
   * @param out the output print stream
   * @param relaxedSyntax if <code>true</code>, the parser doesn't distinguidh between upper and
   *     lower case identifiers
   */
  public EvalEngine(
      final String sessionID,
      final int recursionLimit,
      final PrintStream out,
      boolean relaxedSyntax) {
    this(sessionID, recursionLimit, 1000, out, null, relaxedSyntax);
  }

  public EvalEngine(final String sessionID, final PrintStream out) {
    this(sessionID, -1, -1, out, null, false);
  }

  /**
   * For every evaluation store the list of modified variables in an internal list.
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
   * @param inExpr TODO
   * @param outExpr
   */
  public void addInOut(IExpr inExpr, IExpr outExpr) {
    // remember the last result
    if (outExpr != null && outExpr.isPresent()) {
      fAnswer = outExpr;
    } else {
      fAnswer = S.Null;
    }
    ISymbol ans = F.symbol("$ans", Context.GLOBAL_CONTEXT_NAME, null, this);
    ans.putDownRule(IPatternMatcher.SET, true, ans, fAnswer, false);
    if (fOutListDisabled) {
      return;
    }
    fEvalHistory.addInOut(inExpr, fAnswer);
  }

  /**
   * @param op
   * @param rule may be <code>null</code>
   */
  public void addOptionsPattern(OptionsPattern op, IAST rule) {
    IdentityHashMap<ISymbol, IASTAppendable> optionsPattern = fOptionsStack.peek();
    IASTAppendable list = optionsPattern.get(op.getOptionsPatternHead());
    if (list == null) {
      list = F.ListAlloc(10);
      optionsPattern.put(op.getOptionsPatternHead(), list);
    }
    if (rule != null && rule.isRuleAST()) {
      if (rule.first().isSymbol()) {
        list.append(
            F.binaryAST2(rule.topHead(), ((ISymbol) rule.first()).getSymbolName(), rule.second()));
      } else {
        list.append(rule);
      }
    }
    IExpr defaultOptions = op.getDefaultOptions();
    if (defaultOptions.isPresent()) {
      IAST optionsList = null;
      if (defaultOptions.isSymbol()) {
        optionsList = PatternMatching.optionsList((ISymbol) defaultOptions, true);
        PatternMatching.extractRules(optionsList, list);
        // list.appendArgs(optionsList);
      } else if (defaultOptions.isList()) {
        PatternMatching.extractRules(defaultOptions, list);
        // list.appendArgs((IAST) defaultOptions);
      } else if (defaultOptions.isRuleAST()) {
        PatternMatching.extractRules(defaultOptions, list);
        // list.append(defaultOptions);
      }
    }
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
    if (fTraceStack != null) {
      IASTAppendable listOfHints = F.ast(S.List, list.length + 1, false);
      listOfHints.appendAll(list, 0, list.length);
      fTraceStack.add(inputExpr, rewrittenExpr, getRecursionCounter(), -1, listOfHints);
      IExpr evaluatedResult = evaluate(rewrittenExpr);
      listOfHints.append(evaluatedResult);
      return evaluatedResult;
    }
    return evaluate(rewrittenExpr);
  }

  /**
   * Add a single step to the currently defined trace stack.
   *
   * @param inputExpr
   * @param rewrittenExpr
   * @param listOfHints
   * @see #setStepListener(IEvalStepListener)
   */
  public void addTraceStep(IExpr inputExpr, IExpr rewrittenExpr, IAST listOfHints) {
    if (fTraceStack != null) {
      fTraceStack.add(inputExpr, rewrittenExpr, getRecursionCounter(), -1, listOfHints);
    }
  }

  /**
   * Add a single step to the currently defined trace stack.
   *
   * @param before
   * @param rewrittenExpr
   * @param listOfHints
   */
  public void addTraceStep(
      Supplier<IExpr> inputExpr, Supplier<IExpr> rewrittenExpr, IAST listOfHints) {
    if (fTraceStack != null) {
      fTraceStack.add(inputExpr.get(), rewrittenExpr.get(), getRecursionCounter(), -1, listOfHints);
    }
  }

  public void addTraceStep(Supplier<IExpr> inputExpr, IExpr rewrittenExpr, IAST listOfHints) {
    if (fTraceStack != null) {
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
    engine.rememberASTCache = null; // rememberASTCache;
    engine.rememberMap = rememberMap;
    engine.fAnswer = fAnswer;
    engine.fAssumptions = fAssumptions;
    engine.fContextPath = fContextPath.copy();
    engine.fErrorPrintStream = fErrorPrintStream;
    engine.fEvalLHSMode = fEvalLHSMode;
    engine.fEvalRHSMode = fEvalRHSMode;
    engine.fFileSystemEnabled = fFileSystemEnabled;
    engine.fIterationLimit = fIterationLimit;
    engine.fModifiedVariablesList = fModifiedVariablesList;
    engine.fNumericMode = fNumericMode;
    //    engine.fNumericPrecision = fNumericPrecision;
    engine.fApfloatHelper = new FixedPrecisionApfloatHelper(getNumericPrecision());
    engine.fSignificantFigures = fSignificantFigures;
    engine.fEvalHistory = fEvalHistory;
    engine.fOptionsStack = fOptionsStack;
    engine.fOutListDisabled = fOutListDisabled;
    engine.fOutPrintStream = fOutPrintStream;
    engine.fOnOffMap = fOnOffMap;
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
    engine.fThrowError = fThrowError;
    engine.fTogetherMode = fTogetherMode;
    engine.fTraceMode = fTraceMode;
    engine.fTraceStack = fTraceStack;
    engine.f$Input = f$Input;
    engine.f$InputFileName = f$InputFileName;
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
   * @return
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
   *     occured.
   * @param ast the original <code>ast</code> for whixh the arguments should be evaluated
   * @param evaledArg the i-th evaluated argument of <code>ast</code>, may be <code>F.NIL</code>
   * @param arg the i-th argument of <code>ast</code>
   * @param i <code>arg</code> is the i-th argument of <code>ast</code>
   * @param isNumericFunction if <code>true</code> the <code>NumericFunction</code> attribute is set
   *     for the <code>ast</code>'s head
   */
  public void evalArg(
      final IASTMutable[] result0,
      final IAST ast,
      final IExpr evaledArg,
      final IExpr arg,
      final int i,
      final boolean isNumericFunction) {
    // IExpr temp = evalLoop(arg);
    if (evaledArg.isPresent()) {
      if (!result0[0].isPresent()) {
        result0[0] = ast.copy();
        if (isNumericFunction && evaledArg.isNumericArgument()) {
          result0[0].addEvalFlags(
              (ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR) | IAST.CONTAINS_NUMERIC_ARG);
        } else {
          result0[0].addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
        }
      }
      result0[0].set(i, evaledArg);
    } else {
      if (isNumericFunction && arg.isNumericArgument()) {
        ast.addEvalFlags(ast.getEvalFlags() | IAST.CONTAINS_NUMERIC_ARG);
      }
    }
  }

  /**
   * Evaluate the arguments of the given ast, taking the attributes HoldFirst, HoldRest into
   * account.
   *
   * @param ast
   * @param attr
   * @return <code>F.NIL</code> is no evaluation was possible
   */
  public IASTMutable evalArgs(final IAST ast, final int attr) {
    final int astSize = ast.size();

    if (astSize > 1) {
      boolean numericMode = fNumericMode;
      boolean localNumericMode = fNumericMode;
      final boolean isNumericFunction = (ISymbol.NUMERICFUNCTION & attr) == ISymbol.NUMERICFUNCTION;
      boolean isNumericArgument = ast.isNumericArgument();
      if (!fNumericMode) {
        if (isNumericFunction && isNumericArgument) {
          localNumericMode = true;
        }
      }

      IASTMutable[] rlist = new IASTMutable[1];
      rlist[0] = F.NIL;
      IExpr x = ast.arg1();
      if ((ISymbol.HOLDFIRST & attr) == ISymbol.NOATTRIBUTE) {
        // the HoldFirst attribute is disabled
        try {
          if (!x.isAST(S.Unevaluated)) {
            selectNumericMode(attr, ISymbol.NHOLDFIRST, localNumericMode);
            IExpr temp = evalLoop(x);
            evalArg(rlist, ast, temp, x, 1, isNumericFunction);
            if (astSize == 2 && rlist[0].isPresent()) {
              return rlist[0];
            }
          }
        } finally {
          if ((ISymbol.NHOLDFIRST & attr) == ISymbol.NHOLDFIRST) {
            fNumericMode = numericMode;
          }
        }
      } else {
        // the HoldFirst attribute is set here
        if (!ast.isHoldAllCompleteAST()) {
          try {
            if (x.isAST(S.Evaluate)) {
              selectNumericMode(attr, ISymbol.NHOLDFIRST, localNumericMode);
              IExpr temp = evalLoop(x);
              evalArg(rlist, ast, temp, x, 1, isNumericFunction);
              if (astSize == 2 && rlist[0].isPresent()) {
                return rlist[0];
              }
            }
          } finally {
            if ((ISymbol.NHOLDFIRST & attr) == ISymbol.NHOLDFIRST) {
              fNumericMode = numericMode;
            }
          }
        }
      }

      if (astSize > 2) {
        if ((ISymbol.HOLDREST & attr) == ISymbol.NOATTRIBUTE) {
          // the HoldRest attribute is disabled
          numericMode = fNumericMode;
          try {
            selectNumericMode(attr, ISymbol.NHOLDREST, localNumericMode);
            ast.forEach(
                2,
                astSize,
                (arg, i) -> {
                  if (!arg.isUnevaluated()) {
                    IExpr temp = evalLoop(arg);
                    evalArg(rlist, ast, temp, arg, i, isNumericFunction);
                  }
                });
          } finally {
            if ((ISymbol.NHOLDREST & attr) == ISymbol.NHOLDREST) {
              fNumericMode = numericMode;
            }
          }
        } else {
          // the HoldRest attribute is set here
          if (!ast.isHoldAllCompleteAST()) {
            numericMode = fNumericMode;
            try {
              selectNumericMode(attr, ISymbol.NHOLDREST, localNumericMode);
              ast.forEach(
                  2,
                  astSize,
                  (arg, i) -> {
                    if (arg.isAST(S.Evaluate)) {
                      IExpr temp = evalLoop(arg);
                      evalArg(rlist, ast, temp, arg, i, isNumericFunction);
                    }
                  });
            } finally {
              if ((ISymbol.NHOLDREST & attr) == ISymbol.NHOLDREST) {
                fNumericMode = numericMode;
              }
            }
          }
        }
      }
      if (!isNumericArgument && ast.isNumericArgument()) {
        // one of the arguments is a numeric value
        if (!rlist[0].isPresent()) {
          return evalArgs(ast, attr);
        }
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
    final int attr = symbol.getAttributes();

    if ((attr & ISymbol.SEQUENCEHOLD) != ISymbol.SEQUENCEHOLD) {
      if ((result = F.flattenSequence(ast)).isPresent()) {
        return result;
      }
    }

    // don't test for OneIdentity here! OneIdentity will only be used in "structural
    // pattern-matching".
    // Functions like Times and PLus implement OneIdentity as extra transformation!

    if ((result = evalArgs(ast, attr)).isPresent()) {
      return result;
    }

    final IExpr arg1 = ast.arg1();
    if (ISymbol.hasFlatAttribute(attr)) {
      if (arg1.head().equals(symbol)) {
        // associative
        return arg1;
      }
      if (arg1.isUnevaluated() && arg1.first().head().equals(symbol) && arg1.first().isAST()) {
        IAST unevaluated = (IAST) arg1.first();
        return unevaluated.map(symbol, x -> F.Unevaluated(x));
      }
    }

    if ((ISymbol.LISTABLE & attr) == ISymbol.LISTABLE) {
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
          return ((IAssociation) arg1).mapThread(ast, 1);
        } else if (arg1.isSparseArray()) {
          return ((ISparseArray) arg1).mapThread(ast, 1);
        } else if (arg1.isConditionalExpression()) {
          IExpr temp = ast.extractConditionalExpression(true);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    }

    if ((ISymbol.NUMERICFUNCTION & attr) == ISymbol.NUMERICFUNCTION) {
      if (ast.arg1().isIndeterminate()) {
        return S.Indeterminate;
      }
    }

    if (!(arg1 instanceof IPatternObject) && arg1.isPresent()) {
      ISymbol lhsSymbol = arg1.isSymbol() ? (ISymbol) arg1 : arg1.topHead();
      return lhsSymbol.evalUpRules(ast, this);
    }
    return F.NIL;
  }

  /**
   * @param symbol
   * @param ast
   * @return <code>F.NIL</code> if no evaluation happened
   */
  private IExpr evalASTBuiltinFunction(final ISymbol symbol, IAST ast) {
    final int attr = symbol.getAttributes();
    if (fEvalLHSMode) {
      if ((ISymbol.HOLDALL & attr) == ISymbol.HOLDALL) {
        // check for Set or SetDelayed necessary, because of dynamic
        // evaluation then initializing rules for predefined symbols
        // (i.e. Sin, Cos,...)
        if (!(symbol.equals(S.Set)
            || symbol.equals(S.SetDelayed)
            || symbol.equals(S.UpSet)
            || symbol.equals(S.UpSetDelayed))) {
          return F.NIL;
        }
      } else {
        if ((ISymbol.NUMERICFUNCTION & attr) != ISymbol.NUMERICFUNCTION) {
          return F.NIL;
        }
      }
    }

    if (((ISymbol.DELAYED_RULE_EVALUATION & attr) == ISymbol.NOATTRIBUTE)
        && !symbol.equals(S.Integrate)) {
      IExpr result;
      if ((result = symbol.evalDownRule(this, ast)).isPresent()) {
        return result;
      }
    }

    if (symbol.isBuiltInSymbol()) {
      final IEvaluator evaluator = ((IBuiltInSymbol) symbol).getEvaluator();
      if (evaluator instanceof IFunctionEvaluator) {
        try {
          // evaluate a built-in function.
          final IFunctionEvaluator functionEvaluator = (IFunctionEvaluator) evaluator;
          ast = checkBuiltinArguments(ast, functionEvaluator, this);
          if (!ast.isPresent()) {
            return F.NIL;
          }
          if (!fNumericMode
              && ((ast.getEvalFlags() & IAST.BUILT_IN_EVALED) == IAST.BUILT_IN_EVALED)
              && fAssumptions == null) {
            return F.NIL;
          }
          IExpr result =
              fNumericMode
                  ? functionEvaluator.numericEval(ast, this)
                  : functionEvaluator.evaluate(ast, this);
          if (result.isPresent()) {
            return result;
          }

        } catch (FlowControlException fce) {
          throw fce;
        } catch (SymjaMathException ve) {
          if (ve instanceof LimitException) {
            throw ve;
          }
          if (FEConfig.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
          return printMessage(ast.topHead(), ve);
        }
        if (((ISymbol.DELAYED_RULE_EVALUATION & attr) == ISymbol.DELAYED_RULE_EVALUATION)) {
          // evaluate args especially for Sum() and Product(), although they have HOLDALL attribute
          IExpr temp = evalArgs(ast, ISymbol.NOATTRIBUTE).orElse(ast);
          return symbol.evalDownRule(this, temp);
        } else {
          if (!fNumericMode
              && fAssumptions == null
              && (((ISymbol.HOLDALLCOMPLETE | ISymbol.NHOLDALL) & attr) == ISymbol.NOATTRIBUTE)) {
            ast.addEvalFlags(IAST.BUILT_IN_EVALED);
            return F.NIL;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Check the number of arguments if requested and transform the <code>ast</code> from an
   * <i>operator form</i> to <i>normal form</i> if it is allowed.
   *
   * @param ast
   * @param functionEvaluator
   * @param engine
   * @return
   */
  public static IAST checkBuiltinArguments(
      IAST ast, final IFunctionEvaluator functionEvaluator, final EvalEngine engine) {
    int[] expected;
    if ((expected = functionEvaluator.expectedArgSize(ast)) != null) {
      if (expected.length == 2 && !ast.head().isBuiltInSymbol()) {
        return F.NIL;
      } else if (expected.length == 3 && expected[2] > 0) {
        switch (expected[2]) {
          case 1:
            if (ast.isAST1()) {
              ast = F.operatorForm1Append(ast);
              if (!ast.isPresent()) {
                return F.NIL;
              }
            }
            break;
          case 2:
            if (ast.isAST1()) {
              ast = F.operatorForm2Prepend(ast);
              if (!ast.isPresent()) {
                return F.NIL;
              }
            }
            break;
          default:
        }
      }
      if (ast.argSize() < expected[0] || ast.argSize() > expected[1]) {
        return IOFunctions.printArgMessage(ast, expected, engine);
      }
    }
    return ast;
  }

  /**
   * Evaluate an AST according to the attributes set in the header symbol. The evaluation steps are
   * controlled by the header attributes.
   *
   * @param symbol the header symbol
   * @param ast the AST which should be evaluated
   * @return <code>F.NIL</code> if no evaluation was possible
   */
  public IExpr evalAttributes(ISymbol symbol, IAST ast) {
    IASTMutable tempAST = (IASTMutable) ast;
    final int astSize = tempAST.size();
    if (astSize == 2) {
      return evalASTArg1(tempAST);
    }

    // first evaluate the header
    IExpr head = tempAST.head();
    IExpr result = head.evaluateHead(tempAST, this);
    if (result.isPresent()) {
      return result;
    }

    if (astSize != 1) {
      final int attr = symbol.getAttributes();
      IASTMutable returnResult = F.NIL;

      if ((attr & ISymbol.SEQUENCEHOLD) != ISymbol.SEQUENCEHOLD) {
        if ((result = F.flattenSequence(tempAST)).isPresent()) {
          return result;
        }
      }

      IASTMutable resultList = evalArgs(tempAST, attr);
      if (resultList.isPresent()) {
        return resultList;
      }

      // ONEIDENTITY is checked in the evalASTArg1() method!
      if (ISymbol.hasFlatAttribute(attr)) {
        // associative symbol
        IASTAppendable flattened;
        if ((flattened = EvalAttributes.flatten(tempAST)).isPresent()) {
          returnResult = flattened;
          tempAST = returnResult;
        }
      }

      result = evalTagSetPlusTimes(tempAST);
      if (result.isPresent()) {
        return result;
      }

      if ((ISymbol.LISTABLE & attr) == ISymbol.LISTABLE
          && !((tempAST.getEvalFlags() & IAST.IS_LISTABLE_THREADED) == IAST.IS_LISTABLE_THREADED)) {
        // thread over the lists
        resultList = threadASTListArgs(tempAST);
        if (resultList.isPresent()) {
          return evalArgs(resultList, ISymbol.NOATTRIBUTE).orElse(resultList);
        }
        int indx = tempAST.indexOf(x -> x.isAssociation());
        if (indx > 0) {
          return ((IAssociation) tempAST.get(indx)).mapThread(tempAST, indx);
        }
      }

      if ((ISymbol.NUMERICFUNCTION & attr) == ISymbol.NUMERICFUNCTION) {
        if (!((ISymbol.HOLDALL & attr) == ISymbol.HOLDALL)) {
          if (tempAST.exists(x -> x.isIndeterminate())) {
            return S.Indeterminate;
          }
          IExpr temp = tempAST.extractConditionalExpression(false);
          if (temp.isPresent()) {
            return temp;
          }
        }
      } else if (tempAST.isBooleanFunction() || tempAST.isComparatorFunction()) {
        IExpr temp = tempAST.extractConditionalExpression(false);
        if (temp.isPresent()) {
          return temp;
        }
      }

      if (astSize > 2 && ISymbol.hasOrderlessAttribute(attr)) {
        // commutative symbol
        EvalAttributes.sortWithFlags(tempAST);
      }
      return returnResult;
    }

    return F.NIL;
  }

  public IExpr evalBlock(final IExpr expr, final IAST localVariablesList) {
    ISymbol[] symbolList = new ISymbol[localVariablesList.size()];
    IExpr[] blockVariables = new IExpr[localVariablesList.size()];
    RulesData[] blockVariablesRulesData = new RulesData[localVariablesList.size()];
    IExpr result = F.NIL;
    try {
      Programming.rememberBlockVariables(
          localVariablesList, symbolList, blockVariables, blockVariablesRulesData, this);
      result = evaluate(expr);
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
    }
    throw new ArgumentTypeException(
        "conversion into a machine-size boolean value is not possible!");
  }

  /**
   * Evaluates <code>expr</code> numerically and return the result a Java <code>double</code> value.
   *
   * @param expr
   * @return
   * @see #evaluate(IExpr)
   */
  public final double evalDouble(final IExpr expr) throws ArgumentTypeException {
    if (expr.isReal()) {
      return ((ISignedNumber) expr).doubleValue();
    }
    if (expr.isNumericFunction(true)) {
      IExpr result = evalN(expr);
      if (result.isReal()) {
        return ((ISignedNumber) result).doubleValue();
      }
    }
    throw new ArgumentTypeException(
        "conversion into a machine-size double numeric value is not possible!");
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
    }
    if (result != Integer.MIN_VALUE) {
      return result;
    }
    throw new ArgumentTypeException(
        "conversion into a machine-size integer value is not possible!");
  }

  /**
   * Evaluates <code>expr</code> numerically and return the result a Java <code>
   * org.hipparchus.complex.Complex</code> value.
   *
   * @param expr
   * @return
   * @throws ArgumentTypeException
   */
  public final Complex evalComplex(final IExpr expr) throws ArgumentTypeException {
    if (expr.isReal()) {
      return new Complex(((ISignedNumber) expr).doubleValue());
    }
    if (expr.isNumber()) {
      return new Complex(((INumber) expr).reDoubleValue(), ((INumber) expr).imDoubleValue());
    }
    if (expr.isNumericFunction(true)) {
      IExpr result = evalN(expr);
      if (result.isReal()) {
        return new Complex(((ISignedNumber) result).doubleValue());
      }
      if (result.isNumber()) {
        return new Complex(((INumber) result).reDoubleValue(), ((INumber) result).imDoubleValue());
      }
    }
    throw new ArgumentTypeException(
        "conversion into a machine-size Complex numeric value is not possible!");
  }

  /**
   * Evaluate the Flat and Orderless attributes of the given <code>ast</code> recursively.
   *
   * @param ast
   * @return <code>F.NIL</code> if no evaluation was possible
   */
  public IAST evalFlatOrderlessAttributesRecursive(final IAST ast) {
    if (ast.isEvalFlagOn(IAST.IS_FLAT_ORDERLESS_EVALED)) {
      return F.NIL;
    }
    final ISymbol symbol = ast.topHead();
    final int attr = symbol.getAttributes();
    // final Predicate<IExpr> isPattern = Predicates.isPattern();
    IASTMutable resultList = F.NIL;

    if ((ISymbol.HOLDALL & attr) != ISymbol.HOLDALL) {
      final int astSize = ast.size();

      if ((ISymbol.HOLDFIRST & attr) == ISymbol.NOATTRIBUTE) {
        // the HoldFirst attribute isn't set here
        if (astSize > 1 && ast.arg1().isAST()) {
          IExpr expr = ast.arg1();
          if (ast.arg1().isAST()) {
            IAST temp = (IAST) ast.arg1();
            expr = evalFlatOrderlessAttributesRecursive(temp);
            if (expr.isPresent()) {
              resultList = ast.setAtCopy(1, expr);
            } else {
              expr = ast.arg1();
            }
          }
        }
      }
      if (astSize > 2) {
        if ((ISymbol.HOLDREST & attr) == ISymbol.NOATTRIBUTE) {
          // the HoldRest attribute isn't set here
          for (int i = 2; i < astSize; i++) {
            if (ast.get(i).isAST()) {
              IAST temp = (IAST) ast.get(i);
              IExpr expr = evalFlatOrderlessAttributesRecursive(temp);
              if (expr.isPresent()) {
                if (!resultList.isPresent()) {
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
        if (ISymbol.hasFlatAttribute(attr)) {
          // associative
          IASTAppendable result;
          if ((result = EvalAttributes.flattenDeep(resultList)).isPresent()) {
            resultList = result;
            if (ISymbol.hasOrderlessAttribute(attr)) {
              EvalAttributes.sortWithFlags(resultList);
            }
            resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
            return resultList;
          }
        }
        if (ISymbol.hasOrderlessAttribute(attr)) {
          EvalAttributes.sortWithFlags(resultList);
        }
      }
      resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
      return resultList;
    }

    if (ISymbol.hasFlatAttribute(attr)) {
      // associative
      IASTAppendable result;
      if ((result = EvalAttributes.flattenDeep(ast)).isPresent()) {
        resultList = result;
        if (ISymbol.hasOrderlessAttribute(attr)) {
          EvalAttributes.sortWithFlags(resultList);
        }
        resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
        return resultList;
      }
    }
    if (ISymbol.hasOrderlessAttribute(attr)) {
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
   * Set[]</code>, <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSetDelayed[]</code>
   * expression
   *
   * @param ast
   * @param noEvaluation (sub-)expressions which contain no patterns should not be evaluated
   * @param evalNumericFunction TODO
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
   * @see EvalEngine#evalWithoutNumericReset(IExpr)
   */
  public final IExpr evalLoop(final IExpr expr) {
    if (expr == null || !expr.isPresent()) {
      if (Config.FUZZ_TESTING) {
        throw new NullPointerException();
      }
      printMessage("Evaluation aborted in EvalEngine#evalLoop() because of undefined expression!");
      throw AbortException.ABORTED;
    }
    if ((fRecursionLimit > 0) && (fRecursionCounter > fRecursionLimit)) {
      if (Config.DEBUG) {
        System.out.println(expr.toString());
      }
      RecursionLimitExceeded.throwIt(fRecursionLimit, expr);
    }
    if (fStopRequested || Thread.currentThread().isInterrupted()) {
      // check before going one recursion deeper
      throw TimeoutException.TIMED_OUT;
    }
    IExpr result = expr;
    try {
      IExpr temp;
      fRecursionCounter++;
      stackPush(expr);
      if (fTraceMode) {
        if (result.isUnevaluated()) {
          return result.first();
        }
        fTraceStack.setUp(expr, fRecursionCounter);
        temp = result.evaluate(this);
        if (temp.isPresent()) {
          if (fStopRequested || Thread.currentThread().isInterrupted()) {
            throw TimeoutException.TIMED_OUT;
          }

          fTraceStack.add(expr, temp, fRecursionCounter, 0L, EVALUATION_LOOP);
          result = temp;
          long iterationCounter = 1;
          while (true) {
            if (result.isUnevaluated()) {
              return result.first();
            }
            temp = result.evaluate(this);
            if (temp.isPresent()) {
              if (fStopRequested || Thread.currentThread().isInterrupted()) {
                throw TimeoutException.TIMED_OUT;
              }
              if (FEConfig.SHOW_STACKTRACE) {
                if (temp.equals(result)) {
                  // Endless iteration detected in `1` in evaluation loop.
                  IOFunctions.printMessage(result.topHead(), "itendless", F.List(temp), this);
                  IterationLimitExceeded.throwIt(fIterationLimit, result);
                }
              }
              fTraceStack.add(result, temp, fRecursionCounter, iterationCounter, EVALUATION_LOOP);
              result = temp;
              if (fIterationLimit >= 0 && fIterationLimit <= ++iterationCounter) {
                IterationLimitExceeded.throwIt(iterationCounter, result);
              }
            } else {
              return result;
            }
          }
        }
      } else {
        if (result.isUnevaluated()) {
          return result.first();
        }
        temp = result.evaluate(this);
        if (temp.isPresent()) {
          if (fStopRequested || Thread.currentThread().isInterrupted()) {
            throw TimeoutException.TIMED_OUT;
          }
          if (fOnOffMode) {
            printOnOffTrace(expr, temp);
          }
          // if (temp.topHead().getContext().equals(Context.RUBI)) {
          // printOnOffTrace(expr, temp);
          // }

          result = temp;
          long iterationCounter = 1;
          while (true) {
            if (result.isUnevaluated()) {
              return result.first();
            }
            temp = result.evaluate(this);
            if (temp.isPresent()) {
              if (fStopRequested || Thread.currentThread().isInterrupted()) {
                throw TimeoutException.TIMED_OUT;
              }
              if (FEConfig.SHOW_STACKTRACE) {
                if (temp.equals(result)) {
                  // Endless iteration detected in `1` in evaluation loop.
                  IOFunctions.printMessage(result.topHead(), "itendless", F.List(temp), this);
                  IterationLimitExceeded.throwIt(fIterationLimit, result);
                }
              }
              if (fOnOffMode) {
                printOnOffTrace(result, temp);
              }

              // if (temp == F.Null&&!result.isAST(F.SetDelayed)) {
              // System.out.println(expr.toString());
              // }
              // if (result.isAST(F.Integrate)) {
              // System.out.println(result.toString());
              // System.out.println("("+iterationCounter+") --> " +
              // temp.toString());
              // }

              if (fIterationLimit >= 0 && fIterationLimit <= ++iterationCounter) {
                IterationLimitExceeded.throwIt(iterationCounter, temp);
              }
              result = temp;
            } else {
              return result;
            }
          }
        }
      }

      return F.NIL;
    } catch (UnsupportedOperationException uoe) {
      if (Config.FUZZ_TESTING) {
        throw new NullPointerException();
      }
      printMessage("Evaluation aborted: " + result.toString());
      throw AbortException.ABORTED;
    } finally {
      stackPop();
      if (fTraceMode) {
        fTraceStack.tearDown(fRecursionCounter, true);
      }
      fRecursionCounter--;
      if (fStopRequested) {
        throw TimeoutException.TIMED_OUT;
      }
    }
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
      if (stream == null) {
        stream = System.out;
      }
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
    IASTMutable copy = F.NIL;
    // EvalEngine engine = EvalEngine.get();
    // long precision = engine.getNumericPrecision();
    // for (int i = 1; i < ast1.size(); i++) {
    // IExpr temp = ast1.get(i);
    // if (temp instanceof ApfloatNum) {
    // if (((ApfloatNum) temp).precision() > precision) {
    // precision = ((ApfloatNum) temp).precision();
    // }
    // } else if (temp instanceof ApcomplexNum) {
    // if (((ApcomplexNum) temp).precision() > precision) {
    // precision = ((ApcomplexNum) temp).precision();
    // }
    // } else if (temp.isAST(F.Interval)) {
    // long p = IntervalSym.precision((IAST) temp);
    // if (p > precision) {
    // precision = p;
    // }
    // }
    // }
    // engine.setNumericPrecision(precision);
    for (int i = 1; i < ast1.size(); i++) {
      IExpr temp = ast1.get(i);
      if (!temp.isInexactNumber() && temp.isNumericFunction(true)) {
        temp = evalLoop(F.N(temp));
        if (temp.isPresent()) {
          if (!copy.isPresent()) {
            copy = ast1.copy();
          }
          copy.set(i, evalN(temp));
        }
      }
    }
    if (copy.isPresent()) {
      EvalAttributes.sort(copy);
    }
    return copy;
  }

  /**
   * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation
   * reset the numeric mode to the value stored before the evaluation starts. If evaluation is not
   * possible return the input object.
   *
   * <p><b>Note:</b> if this method catches exception <code>
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
  public final IPatternMatcher evalPatternMatcher(
      final IExpr patternExpression, final IExpr rightHandside) {
    return new PatternMatcherAndEvaluator(evalPattern(patternExpression), rightHandside);
  }

  /**
   * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return the
   * input object. In &quot;quiet mode&quot; all warnings would be suppressed.
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
   * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return <code>
   * F.NIL</code>. In &quot;quiet mode&quot; all warnings would be suppressed.
   *
   * @param expr the expression which should be evaluated
   * @return the evaluated object or <code>F.NUIL</code> if no evaluation was possible
   * @see EvalEngine#evalWithoutNumericReset(IExpr)
   */
  public final IExpr evalQuietNull(final IExpr expr) {
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
    if (argsAST.exists(x -> x.isAST(S.Unevaluated, 2))) {
      ast =
          argsAST.map(
              x -> {
                if (x.isUnevaluated()) {
                  return x.first();
                }
                return x;
              },
              1);
    } else {
      ast = argsAST;
    }
    IExpr temp = evalUpRules(ast);
    if (temp.isPresent()) {
      return temp;
    }

    return evalASTBuiltinFunction(symbol, ast);
  }

  public IExpr evalUpRules(IAST ast) {
    IExpr[] result = new IExpr[1];
    result[0] = F.NIL;
    if (ast.exists(
        x -> {
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

  private IASTMutable evalSetAttributeArg(
      IAST ast, int i, IAST argI, IASTMutable resultList, boolean noEvaluation, int level) {
    IExpr expr = evalSetAttributesRecursive(argI, noEvaluation, true, level + 1);
    if (expr != argI && expr.isPresent()) {
      if (resultList.isPresent()) {
        resultList.set(i, expr);
      } else {
        resultList = ast.setAtCopy(i, expr);
      }
    } else {
      expr = argI;
    }
    if (expr.isAST()) {
      if (((IAST) expr).size() == 2) {
        IExpr arg1 = ((IAST) expr).arg1();
        if (expr.isSqrt()) {
          if (resultList.isPresent()) {
            resultList.set(i, PowerOp.power(arg1, F.C1D2));
          } else {
            resultList = ast.setAtCopy(i, PowerOp.power(arg1, F.C1D2));
          }
        } else if (expr.isAST(S.Exp, 2)) {
          if (resultList.isPresent()) {
            resultList.set(i, PowerOp.power(S.E, arg1));
          } else {
            resultList = ast.setAtCopy(i, PowerOp.power(S.E, arg1));
          }
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

  private IExpr evalSetAttributesRecursive(
      IAST ast, boolean noEvaluation, boolean evalNumericFunction, int level) {
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

    if (symbol.isBuiltInSymbol()) {
      // call so that attributes may be set in
      // AbstractFunctionEvaluator#setUp() method
      ((IBuiltInSymbol) symbol).getEvaluator();
    }
    int headID = ast.headID();
    if (headID >= 0) {
      if (headID == ID.Blank
          || headID == ID.BlankSequence
          || headID == ID.BlankNullSequence
          || headID == ID.Pattern
          || headID == ID.Optional
          || headID == ID.OptionsPattern
          || headID == ID.Repeated
          || headID == ID.RepeatedNull) {
        return ((IFunctionEvaluator) ((IBuiltInSymbol) ast.head()).getEvaluator())
            .evaluate(ast, this);
      }
    }

    final int attr = symbol.getAttributes();
    IASTMutable resultList = F.NIL;

    if ((ISymbol.HOLDALL & attr) != ISymbol.HOLDALL) {
      final int astSize = ast.size();

      if ((ISymbol.HOLDFIRST & attr) == ISymbol.NOATTRIBUTE) {
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
        if ((ISymbol.HOLDREST & attr) == ISymbol.NOATTRIBUTE) {
          // the HoldRest attribute isn't set here
          for (int i = 2; i < astSize; i++) {
            IExpr expr = ast.get(i);
            if (expr.isAST()) {
              resultList =
                  evalSetAttributeArg(ast, i, (IAST) expr, resultList, noEvaluation, level);
            } else if (!(expr instanceof IPatternObject) && !noEvaluation) {
              IExpr temp = expr.evaluate(this);
              if (temp.isPresent()) {
                if (resultList.isPresent()) {
                  resultList.set(i, temp);
                } else {
                  resultList = ast.setAtCopy(i, temp);
                }
              }
            }
          }
        }
      }
      if (evalNumericFunction && ((ISymbol.HOLDALL & attr) == ISymbol.NOATTRIBUTE)) {
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
        if (ISymbol.hasFlatAttribute(attr)) {
          // associative
          IASTAppendable result;
          if ((result = EvalAttributes.flattenDeep(resultList)).isPresent()) {
            return evalSetOrderless(result, attr, noEvaluation, level);
          }
        }
        IExpr expr = evalSetOrderless(resultList, attr, noEvaluation, level);
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

    if (ISymbol.hasFlatAttribute(attr)) {
      // associative
      IASTAppendable result;
      if ((result = EvalAttributes.flattenDeep(ast)).isPresent()) {
        return evalSetOrderless(result, attr, noEvaluation, level);
      }
    }
    return evalSetOrderless(ast, attr, noEvaluation, level);
  }

  /**
   * @param ast
   * @param attr
   * @param noEvaluation
   * @param level
   * @return <code>ast</code> if no evaluation was possible
   */
  private IExpr evalSetOrderless(IAST ast, final int attr, boolean noEvaluation, int level) {
    if (ISymbol.hasOrderlessAttribute(attr)) {
      EvalAttributes.sortWithFlags((IASTMutable) ast);
      //      if (level > 0 && !noEvaluation && ast.isFreeOfPatterns()) {
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
   *     is set to <code>null</code>, all expressions are traced.
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
   * Test if <code>expr</code> could be evaluated to <code>True</code>. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>
   * False</code>.
   *
   * @param expr
   * @return <code>true</code> if the expression could be evaluated to symbol <code>True</code> and
   *     <code>false</code> in all other cases
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
      if (FEConfig.SHOW_STACKTRACE) {
        fce.printStackTrace();
      }
      return false;
    }
  }

  /**
   * Test if <code>expr</code> could be evaluated to {@link S#True}. If a <code>
   * org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return {@link
   * S#False}.
   *
   * @param expr
   * @return {@link S#True} if the expression could be evaluated to symbol <code>True</code> and
   *     {@link S#False} in all other cases
   */
  public final IBuiltInSymbol evalSymbolTrue(final IExpr expr) {
    return evalTrue(expr) ? S.True : S.False;
  }

  /**
   * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation
   * reset the numeric mode to the value stored before the evaluation starts. If evaluation is not
   * possible return the input object.
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
   * evaluation is not possible return {@link F#NIL}
   *
   * @param expr the object which should be evaluated
   * @return the evaluated object or <code>F.NIL</code> if no evaluation was possible
   */
  public final IExpr evaluateNIL(final IExpr expr) {
    boolean numericMode = fNumericMode;
    try {
      return evalLoop(expr);
    } finally {
      fNumericMode = numericMode;
    }
  }

  /**
   * @param expr
   * @return
   * @deprecated use {@link #evaluateNIL(IExpr)}
   */
  public final IExpr evaluateNull(final IExpr expr) {
    return evaluateNull(expr);
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
   *     or {@link #setNumericMode(boolean, long, int)}
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

  public final Context getContext() {
    return fContextPath.currentContext();
  }

  public String get$Input() {
    return f$Input;
  }

  public String get$InputFileName() {
    return f$InputFileName;
  }

  public void set$Input(String input) {
    f$Input = input;
  }

  public void set$InputFileName(String inputFileName) {
    f$InputFileName = inputFileName;
  }

  public ContextPath getContextPath() {
    return fContextPath;
  }

  public PrintStream getErrorPrintStream() {
    return fErrorPrintStream;
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
    return FEConfig.MACHINE_PRECISION - 1;
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

  public void popOptionsStack() {
    fOptionsStack.pop();
  }

  public Iterator<IdentityHashMap<ISymbol, IASTAppendable>> optionsStackIterator() {
    return fOptionsStack.iterator();
  }

  public PrintStream getOutPrintStream() {
    return fOutPrintStream;
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
  public ArrayDeque<IExpr> getStack() {
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
   * Increment the module counter by 1 and return the result.
   *
   * @return the module counter
   */
  public long incModuleCounter() {
    return MODULE_COUNTER.incrementAndGet();
  }

  public String uniqueName(String prefix) {
    return prefix + MODULE_COUNTER.incrementAndGet();
  }

  /**
   * Reset the module counter to <code>0</code>. Used only in unit tests. <b> Don't reset for
   * reusable EvalEngine's!</b>
   */
  public void resetModuleCounter4JUnit() {
    MODULE_COUNTER = new AtomicLong();
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
    //    fNumericPrecision = 15;
    fApfloatHelper = null;
    fSignificantFigures = 6;
    fRecursionCounter = 0;
    fNumericMode = false;
    fTogetherMode = false;
    fEvalLHSMode = false;
    fEvalRHSMode = false;
    fOnOffMode = false;
    fOnOffUnique = false;
    fOnOffUniqueMap = null;
    fOnOffMap = null;
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
    rememberASTCache = null;
    rememberMap = new IdentityHashMap<Object, IExpr>();
  }

  public ArrayDeque<IExpr> stackBegin() {
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
   * should be used instead of the <code>Num</code> type and the <code>ApcomplexxNum</code> number
   * type should be used instead of the <code>ComplexNum</code> type for numeric evaluations.
   *
   * @return <code>true</code> if the required precision is greater than <code>
   *     EvalEngine.DOUBLE_PRECISION</code>
   * @see ApfloatNum
   * @see ApcomplexNum
   * @see #isDoubleMode()
   */
  public final boolean isArbitraryMode() {
    return getNumericPrecision() > FEConfig.MACHINE_PRECISION;
  }

  /**
   * @return
   * @deprecated use {@link #isArbitraryMode()}
   */
  @Deprecated
  public final boolean isApfloatMode() {
    return isArbitraryMode();
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
   *     numbers should be used for evaluating numeric functions.
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
   * @see org.matheclipse.core.builtin.function.Quiet
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

  /** @return Returns the stopRequested. */
  public final boolean isStopRequested() {
    return fStopRequested;
  }

  public final boolean isThrowError() {
    return fThrowError;
  }

  public final boolean isTogetherMode() {
    return fTogetherMode;
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

  // public final int traceSize() {
  // return fTraceStack.size();
  // }
  //
  // public final void resetSize(int fromPosition) {
  // fTraceStack.resetSize(fromPosition);
  // }

  /**
   * Parse the given <code>expression String</code> into an IExpr without evaluation.
   *
   * @param expression an expression in math formula notation
   * @return
   * @throws org.matheclipse.parser.client.SyntaxError if a parsing error occurs
   */
  public final IExpr parse(String expression) {
    return parse(expression, FEConfig.EXPLICIT_TIMES_OPERATOR);
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
    final ExprParser parser =
        new ExprParser(
            this, ExprParserFactory.RELAXED_STYLE_FACTORY, fRelaxedSyntax, false, explicitTimes);
    return parser.parse(expression);
  }

  /**
   * Print a message to the <code>Out</code> stream, if the engine is not in &quot;quiet mode&quot;.
   *
   * @param str the message which should be printed
   * @return <code>F.NIL</code>
   */
  public IAST printMessage(String str) throws ArgumentTypeException {
    if (!isQuietMode()) {
      PrintStream stream = getErrorPrintStream();
      if (stream == null) {
        stream = System.err;
      }
      logger.warn(str);
      stream.println(str);
    }
    if (fThrowError) {
      throw new ArgumentTypeException(str);
    }
    return F.NIL;
  }

  /**
   * Print a message to the <code>Out</code> stream, if the engine is not in &quot;quiet mode&quot;.
   *
   * @param exception the RuntimeException which should be printed
   */
  public IAST printMessage(ISymbol symbol, Exception exception) {
    String message = exception.getMessage();
    if (!isQuietMode()) {
      PrintStream stream = getErrorPrintStream();
      if (stream == null) {
        stream = System.err;
      }
      if (message != null) {
        stream.println(symbol + ": " + message);
      } else {
        stream.println(symbol + ": " + exception.getClass().getSimpleName());
      }
    }
    if (fThrowError) {
      throw new ArgumentTypeException(message);
    }
    return F.NIL;
  }

  /**
   * Reset the numeric mode flags and the recursion counter.
   *
   * <p><b>Note:</b> This method should be called before the parsing of a string expression.
   */
  private void reset() {
    stackBegin();
    //    fNumericPrecision = 15;
    fApfloatHelper = null;
    fSignificantFigures = 6;
    fNumericMode = false;
    fEvalLHSMode = false;
    fEvalRHSMode = false;
    fRecursionCounter = 0;
    fTogetherMode = false;
    fTraceMode = false;
    fTraceStack = null;
    fStopRequested = false;
    fCopiedEngine = null;
    fSeconds = 0;
    fModifiedVariablesList = null;
    fMessageShortcut = null;
    rememberASTCache = null;
    fOptionsStack = new OptionsStack();

    if (fOnOffMode && fOnOffUnique) {
      fOnOffUniqueMap = new HashMap<IExpr, IExpr>();
    }
  }

  private void selectNumericMode(final int attr, final int nAttribute, boolean localNumericMode) {
    if ((nAttribute & attr) == nAttribute) {
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

  /** @param b */
  public void setNumericMode(final boolean b) {
    fNumericMode = b;
  }

  /**
   * Set the numeric mode and precision of numeric calculations.
   *
   * @param b
   * @param precision
   * @param figures significant figures which should be displayed in output forms
   */
  public void setNumericMode(final boolean b, long precision, int figures) {
    fNumericMode = b;
    //    fNumericPrecision = precision;
    setNumericPrecision(precision);
    fSignificantFigures = figures;
  }

  public void setNumericPrecision(long precision) {
    //    fNumericPrecision = precision;
    if (FEConfig.MACHINE_PRECISION > precision) {
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
   *     calculations will be saved and the <code>Out()</code> function (or % operator) will be
   *     unevaluated.
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
   *     stream.
   * @param headSymbolsMap the header symbols which should trigger an output in the trace
   * @param uniqueTrace the output is printed only once for a combination of _unevaluated_ input
   *     expression and _evaluated_ output expression.
   */
  public void setOnOffMode(
      final boolean onOffMode,
      IdentityHashMap<ISymbol, ISymbol> headSymbolsMap,
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
        IAST list = PatternMatching.optionsList(element.getKey(), true);
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

  /** @param string */
  public void setSessionID(final String string) {
    fSessionID = string;
  }

  /**
   * Set the stack which should be active for the evaluation engine.
   *
   * @param stack
   */
  public void setStack(ArrayDeque<IExpr> stack) {
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
   * Throw an <code>IllegalArgument</code> exception if an error message is printed in method <code>
   * printMessage()</code>.
   *
   * @param throwError
   */
  public void setThrowError(boolean throwError) {
    this.fThrowError = throwError;
  }

  public void setTogetherMode(boolean fTogetherMode) {
    this.fTogetherMode = fTogetherMode;
  }

  /** @param b */
  public void setTraceMode(final boolean b) {
    fTraceMode = b;
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
   * @return the resulting ast with the <code>argHead</code> threaded into each ast argument or
   *     <code>F.NIL</code>
   */
  public IASTMutable threadASTListArgs(final IASTMutable ast) {
    ISymbol[] head = new ISymbol[] {null};
    int[] listLength = new int[] {-1};
    if (ast.exists(
        x -> {
          if (x.isList()) {
            if (head[0] == null) {
              head[0] = S.List;
            }
            if (listLength[0] < 0) {
              listLength[0] = ((IAST) x).argSize();
            } else {
              if (listLength[0] != ((IAST) x).argSize()) {
                // Objects of unequal length in `1` cannot be combined.
                IOFunctions.printMessage(S.Thread, "tdlen", F.List(ast), EvalEngine.get());
                // ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
                return true;
              }
            }
          } else if (x.isSparseArray()) {
            if (head[0] == null) {
              head[0] = S.SparseArray;
            }
            ISparseArray sp = (ISparseArray) x;
            if (listLength[0] < 0) {
              listLength[0] = sp.getDimension()[0];
            } else {
              if (listLength[0] != sp.getDimension()[0]) {
                // Objects of unequal length in `1` cannot be combined.
                IOFunctions.printMessage(S.Thread, "tdlen", F.List(ast), EvalEngine.get());
                // ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
                return true;
              }
            }
          }
          return false;
        })) {
      return F.NIL;
    }
    if (listLength[0] != -1) {
      IASTMutable result = EvalAttributes.threadList(ast, head[0], ast.head(), listLength[0]);
      result.addEvalFlags(IAST.IS_LISTABLE_THREADED);
      return result;
    }
    ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
    return F.NIL;
  }

  /**
   * Get the {@link FixedPrecisionApfloatHelper} for fixed precision calculations.
   *
   * @return <code>null</code> if the apfloat helper isn't set in {@link #setNumericPrecision(long)}
   *     or {@link #setNumericMode(boolean, long, int)}
   */
  public static FixedPrecisionApfloatHelper getApfloat() {
    FixedPrecisionApfloatHelper h = get().fApfloatHelper;
    if (h == null) {
      h = new FixedPrecisionApfloatHelper(Config.MAX_PRECISION_APFLOAT - 1);
    }
    return h;
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
