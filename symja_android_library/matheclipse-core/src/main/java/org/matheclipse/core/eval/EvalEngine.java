package org.matheclipse.core.eval;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.Programming;
import org.matheclipse.core.eval.exception.IllegalArgument;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.integrate.rubi.UtilityFunctionCtors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.visit.ModuleReplaceAll;
import org.matheclipse.parser.client.math.MathException;

import com.google.common.cache.Cache;

/**
 * The main evaluation algorithms for the .Symja computer algebra system
 */
public class EvalEngine implements Serializable {

	// public final static Map<IBuiltInSymbol, Integer> STATISTICS = new TreeMap<IBuiltInSymbol, Integer>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 8402201556123198590L;

	static int fAnonymousCounter = 0;

	public transient Cache<IAST, IExpr> REMEMBER_AST_CACHE = null;

	public final static boolean DEBUG = false;

	transient private static final ThreadLocal<EvalEngine> instance = new ThreadLocal<EvalEngine>() {
		private int fID = 1;

		@Override
		public EvalEngine initialValue() {
			if (DEBUG) {
				System.out.println("ThreadLocal" + fID);
			}
			return new EvalEngine("ThreadLocal" + (fID++), 0, System.out, true);
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

	synchronized public static int getNextAnonymousCounter() {
		return ++fAnonymousCounter;
	}

	synchronized public static String getNextCounter() {
		return Integer.toString(++fAnonymousCounter);
	}

	/**
	 * Check if the <code>ApfloatNum</code> number type should be used instead of the <code>Num</code> type and the
	 * <code>ApcomplexxNum</code> number type should be used instead of the <code>ComplexNum</code> type for numeric
	 * evaluations.
	 * 
	 * @param precision
	 *            the given precision
	 * @return <code>true</code> if the given precision is greater than <code>EvalEngine.DOUBLE_PRECISION</code>
	 * @see ApfloatNum
	 * @see ApcomplexNum
	 */
	public static boolean isApfloat(int precision) {
		return precision > Config.MACHINE_PRECISION;
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
	 * @param engine
	 *            the evaluation engine
	 */
	public static void set(final EvalEngine engine) {
		instance.set(engine);
	}

	/**
	 * If set to <code>true</code> the current thread should stop evaluation;
	 */
	transient volatile boolean fStopRequested;

	transient int fRecursionCounter;

	transient long fSeconds;
	/**
	 * if <code>true</code> the engine evaluates in &quot;numeric&quot; mode, otherwise the engine evaluates in
	 * &quot;symbolic&quot; mode.
	 */
	transient boolean fNumericMode;

	/**
	 * if <code>true</code> the engine evaluates in &quot;F.Together(expr)&quot; in IExpr#times() method.
	 */
	transient boolean fTogetherMode;

	transient boolean fEvalLHSMode;

	/**
	 * @see Config#isFileSystemEnabled();
	 */
	transient boolean fFileSystemEnabled;

	transient String fSessionID;

	/**
	 * If <code>true</code> the engine evaluates in &quot;Trace()&quot; function mode.
	 * 
	 * @see #evalTrace(IExpr, Predicate, IAST)
	 */
	transient boolean fTraceMode;

	transient IAssumptions fAssumptions = null;

	transient IEvalStepListener fTraceStack = null;

	/**
	 * The stream for printing information messages
	 */
	transient PrintStream fOutPrintStream = null;

	/**
	 * The stream for printing error messages
	 */
	transient PrintStream fErrorPrintStream = null;

	transient Stack<ContextPath> fContextPathStack;

	transient ContextPath fContextPath;

	/**
	 * The precision for numeric operations.
	 */
	protected int fNumericPrecision;

	protected int fRecursionLimit;

	protected int fIterationLimit;

	protected boolean fPackageMode = Config.PACKAGE_MODE;

	transient int fModuleCounter = 0;

	private boolean fRelaxedSyntax;

	/**
	 * The reap list object associated to the most enclosing <code>Reap()</code> statement. The even indices in
	 * <code>java.util.List</code> contain the tag defined in <code>Sow()</code>. If no tag is defined in
	 * <code>Sow()</code> tag <code>F.None</code> is used. The odd indices in <code>java.util.List</code> contain the
	 * associated reap list for the tag.
	 */
	private transient java.util.List<IExpr> fReapList = null;

	public transient Set<ISymbol> fModifiedVariablesList;

	/**
	 * Set interactive trace mode on or off. See functions <code>On()</code> and <code>Off()</code>.
	 */
	transient private boolean fOnOffMode = false;

	/**
	 * Set interactive trace output only to unique expression (e.g. to print an evaluated combination only once).
	 */
	transient private boolean fOnOffUnique = false;

	/**
	 * If <code>fOnOffUnique==true</code> this map contains the unique expressions which occurred during evaluation
	 */
	transient HashMap<IExpr, IExpr> fOnOffUniqueMap = null;

	/**
	 * If not null, this map contains the header symbols for which the interactive trace should be printed.
	 */
	transient IdentityHashMap<ISymbol, ISymbol> fOnOffMap = null;

	/**
	 * The history list for the <code>Out[]</code> function.
	 * 
	 */
	private transient LastCalculationsHistory fOutList = null;

	/**
	 * Contains the last result (&quot;answer&quot;) expression of this evaluation engine or <code>null</code> if no
	 * answer is stored in the evaluation engine.
	 */
	private transient IExpr fAnswer = null;

	/**
	 * Flag for disabling the appending of expressions to the history list for the <code>Out[]</code> function.
	 * 
	 * @see org.matheclipse.core.reflection.Out
	 */
	transient private boolean fOutListDisabled = true;

	/**
	 * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages are shown during
	 * evaluation).
	 * 
	 * @see org.matheclipse.core.builtin.function.Quiet
	 */
	transient boolean fQuietMode = false;

	/**
	 * If <code>true</code> the engine throws an error if an error message is printed during evaluation.
	 * 
	 */
	transient boolean fThrowError = false;

	/**
	 * 
	 * 
	 */
	public EvalEngine() {
		this("", 0, System.out, false);
	}

	/**
	 * Constructor for an evaluation engine
	 * 
	 * @param relaxedSyntax
	 *            if <code>true</code>, the parser doesn't distinguish between upper and lower case identifiers
	 */
	public EvalEngine(boolean relaxedSyntax) {
		this("", 0, System.out, relaxedSyntax);
	}

	static public int MAX_THREADS_COUNT = 10;

	/**
	 * Constructor for an evaluation engine
	 * 
	 * @param sessionID
	 *            an ID which uniquely identifies this session
	 * @param recursionLimit
	 *            the maximum allowed recursion limit (if set to zero, no limit will be checked)
	 * @param iterationLimit
	 *            the maximum allowed iteration limit (if set to zero, no limit will be checked)
	 * @param outStream
	 *            the output print stream
	 * @param errorStream
	 *            the print stream for error messages
	 * @param relaxedSyntax
	 *            if <code>true</code>, the parser doesn't distinguidh between upper and lower case identifiers
	 */
	public EvalEngine(final String sessionID, final int recursionLimit, final int iterationLimit,
			final PrintStream outStream, PrintStream errorStream, boolean relaxedSyntax) {
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
	 * Constructor for an evaluation engine
	 * 
	 * @param sessionID
	 *            an ID which uniquely identifies this session
	 * @param recursionLimit
	 *            the maximum allowed recursion limit (if set to zero, no limit will be checked)
	 * @param out
	 *            the output print stream
	 * @param relaxedSyntax
	 *            if <code>true</code>, the parser doesn't distinguidh between upper and lower case identifiers
	 */
	public EvalEngine(final String sessionID, final int recursionLimit, final PrintStream out, boolean relaxedSyntax) {
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
	 * Add an expression to the <code>Out[]</code> list. To avoid memory leaks you can disable the appending of
	 * expressions to the output history.
	 * 
	 * @param arg0
	 */
	public void addOut(IExpr arg0) {
		// remember the last result
		if (arg0 != null && arg0.isPresent()) {
			fAnswer = arg0;
		} else {
			fAnswer = F.Null;
		}
		ISymbol ans = F.symbol("$ans", Context.GLOBAL_CONTEXT_NAME, null, this);
		ans.putDownRule(IPatternMatcher.SET, true, ans, fAnswer, false);
		if (fOutListDisabled) {
			return;
		}
		fOutList.add(fAnswer);
	}

	private void beginTrace(Predicate<IExpr> matcher, IAST list) {
		setTraceMode(true);
		fTraceStack = new TraceStack(matcher, list);
	}

	/**
	 * Copy this EvalEngine into a new EvalEngine.
	 * 
	 * @return
	 */
	public EvalEngine copy() {
		EvalEngine engine = new EvalEngine();
		engine.REMEMBER_AST_CACHE = REMEMBER_AST_CACHE;
		engine.fAnswer = fAnswer;
		engine.fAssumptions = fAssumptions;
		engine.fContextPath = fContextPath.copy();
		engine.fErrorPrintStream = fErrorPrintStream;
		engine.fEvalLHSMode = fEvalLHSMode;
		engine.fFileSystemEnabled = fFileSystemEnabled;
		engine.fIterationLimit = fIterationLimit;
		engine.fModifiedVariablesList = fModifiedVariablesList;
		engine.fModuleCounter = fModuleCounter;
		engine.fNumericMode = fNumericMode;
		engine.fNumericPrecision = fNumericPrecision;
		engine.fOutList = fOutList;
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

	public void cancel() {
		fContextPath = null;
		fErrorPrintStream = null;
		fFileSystemEnabled = false;
		fIterationLimit = 1;
		fModifiedVariablesList = null;
		fOutList = null;
		fOutPrintStream = null;
		fPackageMode = false;
		fQuietMode = true;
		fReapList = null;
		fRecursionCounter = 1;
		fRecursionLimit = 1;
		fSeconds = 1;
		fSessionID = null;
		fStopRequested = true;
		fTraceMode = false;
		fTraceStack = null;
	}

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
	 * Evaluate the i-th argument of <code>ast</code>. This method may set evaluation flags in <code>ast</code> or
	 * <code>result0</code>
	 * 
	 * @param result0
	 *            store the result of the evaluation in the i-th argument of the ast in <code>result0[0]</code>.
	 *            <code>result0[0]</code> should be <code>F.NIL</code> if no evaluation occured.
	 * @param ast
	 *            the original <code>ast</code> for whixh the arguments should be evaluated
	 * @param arg
	 *            the i-th argument of <code>ast</code>
	 * @param i
	 *            <code>arg</code> is the i-th argument of <code>ast</code>
	 * @param isNumericFunction
	 *            if <code>true</code> the <code>NumericFunction</code> attribute is set for the <code>ast</code>'s head
	 */
	private void evalArg(IASTMutable[] result0, final IAST ast, IExpr arg, int i, boolean isNumericFunction) {
		IExpr temp = evalLoop(arg);
		if (temp.isPresent()) {
			if (!result0[0].isPresent()) {
				result0[0] = ast.copy();
				if (isNumericFunction && temp.isNumericArgument()) {
					result0[0]
							.addEvalFlags((ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR) | IAST.CONTAINS_NUMERIC_ARG);
				} else {
					result0[0].addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
				}
			}
			result0[0].set(i, temp);
		} else {
			if (isNumericFunction && arg.isNumericArgument()) {
				ast.addEvalFlags(ast.getEvalFlags() | IAST.CONTAINS_NUMERIC_ARG);
			}
		}
	}

	/**
	 * Evaluate the arguments of the given ast, taking the attributes HoldFirst, HoldRest into account.
	 * 
	 * @param ast
	 * @param attr
	 * @return
	 */
	public IASTMutable evalArgs(final IAST ast, final int attr) {
		final int astSize = ast.size();

		if (astSize > 1) {
			boolean numericMode = fNumericMode;
			boolean localNumericMode = fNumericMode;
			final boolean isNumericFunction = (ISymbol.NUMERICFUNCTION & attr) == ISymbol.NUMERICFUNCTION;
			boolean isNumericArgument = ast.isNumericArgument();
			if (!fNumericMode) {
				if (isNumericFunction && ast.isNumericArgument()) {
					localNumericMode = true;
				}
			}

			IASTMutable[] rlist = new IASTMutable[1];
			rlist[0] = F.NIL;
			IExpr x = ast.arg1();
			if ((ISymbol.HOLDFIRST & attr) == ISymbol.NOATTRIBUTE) {
				// the HoldFirst attribute isn't set here
				try {
					if (!x.isAST(F.Unevaluated)) {
						selectNumericMode(attr, ISymbol.NHOLDFIRST, localNumericMode);
						evalArg(rlist, ast, x, 1, isNumericFunction);
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
				try {
					if (x.isAST(F.Evaluate)) {
						selectNumericMode(attr, ISymbol.NHOLDFIRST, localNumericMode);
						evalArg(rlist, ast, x, 1, isNumericFunction);
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

			if (astSize > 2) {
				if ((ISymbol.HOLDREST & attr) == ISymbol.NOATTRIBUTE) {
					// the HoldRest attribute isn't set here
					numericMode = fNumericMode;
					try {
						selectNumericMode(attr, ISymbol.NHOLDREST, localNumericMode);
						ast.forEach(2, astSize, (arg, i) -> {
							if (!arg.isAST(F.Unevaluated)) {
								evalArg(rlist, ast, arg, i, isNumericFunction);
							}
						});
					} finally {
						if ((ISymbol.NHOLDREST & attr) == ISymbol.NHOLDREST) {
							fNumericMode = numericMode;
						}
					}
				} else {
					// the HoldRest attribute is set here
					numericMode = fNumericMode;
					try {
						selectNumericMode(attr, ISymbol.NHOLDREST, localNumericMode);
						ast.forEach(2, astSize, (arg, i) -> {
							if (arg.isAST(F.Evaluate)) {
								evalArg(rlist, ast, arg, i, isNumericFunction);
							}
						});
					} finally {
						if ((ISymbol.NHOLDREST & attr) == ISymbol.NHOLDREST) {
							fNumericMode = numericMode;
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
	 * Evaluate an AST with only one argument (i.e. <code>head[arg1]</code>). The evaluation steps are controlled by the
	 * header attributes.
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

		if ((result = flattenSequences(ast)).isPresent()) {
			return result;
		}

		// don't test for OneIdentity here! OneIdentity will only be used in "structural pattern-matching".
		// Functions like Times and PLus implement OneIdentity as extra transformation!
		// if ((ISymbol.ONEIDENTITY & attr) == ISymbol.ONEIDENTITY) {
		// return ast.arg1();
		// }

		final IExpr arg1 = ast.arg1();
		if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
			if (arg1.topHead().equals(symbol)) {
				// associative
				return arg1;
			}
		}

		if ((result = evalArgs(ast, attr)).isPresent()) {
			return result;
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
					return EvalAttributes.threadList(ast, F.List, ast.head(), ((IAST) arg1).argSize());
				}
			}
		}

		if ((ISymbol.NUMERICFUNCTION & attr) == ISymbol.NUMERICFUNCTION) {
			if (ast.arg1().isIndeterminate()) {
				return F.Indeterminate;
			}
		}

		if (!(arg1 instanceof IPatternObject)) {
			ISymbol lhsSymbol = arg1.isSymbol() ? (ISymbol) arg1 : arg1.topHead();
			return lhsSymbol.evalUpRule(this, ast);
		}
		return F.NIL;
	}

	/**
	 * 
	 * @param symbol
	 * @param ast
	 * @return <code>F.NIL</code> if no evaluation happened
	 */
	private IExpr evalASTBuiltinFunction(final ISymbol symbol, final IAST ast) {
		final int attr = symbol.getAttributes();
		if (fEvalLHSMode) {
			if ((ISymbol.HOLDALL & attr) == ISymbol.HOLDALL) {
				// check for Set or SetDelayed necessary, because of dynamic
				// evaluation then initializing rules for predefined symbols
				// (i.e. Sin, Cos,...)
				if (!(symbol.equals(F.Set) || symbol.equals(F.SetDelayed) || symbol.equals(F.UpSet)
						|| symbol.equals(F.UpSetDelayed))) {
					return F.NIL;
				}
			} else {
				if ((ISymbol.NUMERICFUNCTION & attr) != ISymbol.NUMERICFUNCTION) {
					return F.NIL;
				}
			}
		}

		if (((ISymbol.DELAYED_RULE_EVALUATION & attr) == ISymbol.NOATTRIBUTE) && !symbol.equals(F.Integrate)) {
			IExpr result;
			if ((result = symbol.evalDownRule(this, ast)).isPresent()) {
				return result;
			}
		}

		if (symbol.isBuiltInSymbol()) {
			final IEvaluator module = ((IBuiltInSymbol) symbol).getEvaluator();
			if (module instanceof IFunctionEvaluator) {
				// evaluate a built-in function.
				final IFunctionEvaluator functionEvaluator = (IFunctionEvaluator) module;
				int[] expected;
				if ((expected = functionEvaluator.expectedArgSize()) != null) {
					if (ast.argSize() < expected[0] || ast.argSize() > expected[1]) {
						return IOFunctions.printArgMessage(ast, expected, this);
					}
				}
				IExpr result = fNumericMode ? //
						functionEvaluator.numericEval(ast, this) : //
						functionEvaluator.evaluate(ast, this);
				if (result.isPresent()) {
					// if (symbol.equals(F.Simplify) || symbol.equals(F.FullSimplify)) {
					// System.out.println(ast.toString());
					// System.out.println(result.toString());
					// System.out.println();
					// }
					return result;
				}
				if (((ISymbol.DELAYED_RULE_EVALUATION & attr) == ISymbol.DELAYED_RULE_EVALUATION)) {
					return symbol.evalDownRule(this, ast);
				}
			}
		}
		return F.NIL;
	}

	/**
	 * <p>
	 * Evaluate an AST according to the attributes set in the header symbol. The evaluation steps are controlled by the
	 * header attributes.
	 * </p>
	 * 
	 * @param symbol
	 *            the header symbol
	 * @param ast
	 *            the AST which should be evaluated
	 * @return <code>F.NIL</code> if no evaluation was possible
	 */
	public IExpr evalAttributes(@Nonnull ISymbol symbol, @Nonnull IAST ast) {
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

			if ((result = flattenSequences(tempAST)).isPresent()) {
				return result;
			}

			// ONEIDENTITY is checked in the evalASTArg1() method!

			if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
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

			IASTMutable resultList = evalArgs(tempAST, attr);
			if (resultList.isPresent()) {
				returnResult = resultList;
				tempAST = returnResult;
			}

			if ((ISymbol.LISTABLE & attr) == ISymbol.LISTABLE
					&& !((tempAST.getEvalFlags() & IAST.IS_LISTABLE_THREADED) == IAST.IS_LISTABLE_THREADED)) {
				// thread over the lists
				resultList = threadASTListArgs(tempAST);
				if (resultList.isPresent()) {
					return evalArgs(resultList, ISymbol.NOATTRIBUTE).orElse(resultList);
				}
			}

			if ((ISymbol.NUMERICFUNCTION & attr) == ISymbol.NUMERICFUNCTION) {
				if (!((ISymbol.HOLDALL & attr) == ISymbol.HOLDALL)) {
					if (tempAST.exists(x -> x.isIndeterminate())) {
						return F.Indeterminate;
					}
				}
			}

			if (astSize > 2 && (ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
				// commutative symbol
				EvalAttributes.sort(tempAST);
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
			Programming.rememberBlockVariables(localVariablesList, symbolList, blockVariables, blockVariablesRulesData,
					this);
			result = evaluate(expr);
		} finally {
			if (localVariablesList.size() > 0) {
				// reset local variables to global ones
				ISymbol variableSymbol;
				for (int i = 1; i < localVariablesList.size(); i++) {
					if (localVariablesList.get(i).isSymbol()) {
						variableSymbol = symbolList[i];
						variableSymbol.assign(blockVariables[i]);
						variableSymbol.setRulesData(blockVariablesRulesData[i]);
					} else if (localVariablesList.get(i).isAST(F.Set, 3)) {
						final IAST setFun = (IAST) localVariablesList.get(i);
						if (setFun.arg1().isSymbol()) {
							variableSymbol = symbolList[i];
							variableSymbol.assign(blockVariables[i]);
							variableSymbol.setRulesData(blockVariablesRulesData[i]);
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
	 * 
	 * @param expr
	 *            the expression which should be evaluated for the given symbol
	 * @param symbol
	 *            the symbol which should be evaluated as a local variable
	 * @param localValue
	 *            the value
	 * @param quiet
	 *            if <code>true</code> evaluate in quiet mode and suppress evaluation messages
	 */
	public IExpr evalModuleDummySymbol(IExpr expr, ISymbol symbol, IExpr localValue, boolean quiet) {
		boolean quietMode = isQuietMode();
		setQuietMode(quiet);
		java.util.IdentityHashMap<ISymbol, ISymbol> blockVariables = new IdentityHashMap<ISymbol, ISymbol>();
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
				java.util.IdentityHashMap<ISymbol, IExpr> globalVariables = new IdentityHashMap<ISymbol, IExpr>();
				for (Map.Entry<ISymbol, ISymbol> entry : blockVariables.entrySet()) {
					globalVariables.put(entry.getValue(), entry.getKey());
				}
				result = F.subst(result, globalVariables);
			}
		}
	}

	/**
	 * Evaluates <code>expr</code> numerically and return the result a Java <code>double</code> value.
	 * 
	 * @param expr
	 * @return
	 * @see #evaluate(IExpr)
	 * @throws WrongArgumentType
	 */
	final public double evalDouble(final IExpr expr) {
		if (expr.isReal()) {
			return ((ISignedNumber) expr).doubleValue();
		}
		if (expr.isNumericFunction()) {
			IExpr result = evalN(expr);
			if (result.isReal()) {
				return ((ISignedNumber) result).doubleValue();
			}
		}
		throw new WrongArgumentType(expr, "Conversion into a double numeric value is not possible!");
	}

	final public Complex evalComplex(final IExpr expr) {
		if (expr.isReal()) {
			return new Complex(((ISignedNumber) expr).doubleValue());
		}
		if (expr.isNumber()) {
			return new Complex(((INumber) expr).reDoubleValue(), ((INumber) expr).imDoubleValue());
		}
		if (expr.isNumericFunction()) {
			IExpr result = evalN(expr);
			if (result.isReal()) {
				return new Complex(((ISignedNumber) result).doubleValue());
			}
			if (result.isNumber()) {
				return new Complex(((INumber) result).reDoubleValue(), ((INumber) result).imDoubleValue());
			}
		}
		throw new WrongArgumentType(expr, "Conversion into a double numeric value is not possible!");
	}

	/**
	 * Evaluate arguments with the head <code>F.Evaluate</code>, i.e. <code>f(a, ... , Evaluate(x), ...)</code>
	 * 
	 * @param ast
	 * @return
	 */
	public final IExpr evalEvaluate(IAST ast) {
		IASTMutable[] rlist = new IASTMutable[] { F.NIL };
		ast.forEach(1, ast.size(), (x, i) -> {
			if (x.isAST(F.Evaluate)) {
				evalArg(rlist, ast, x, i, false);
			}
		});
		return rlist[0];
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
				if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
					// associative
					IASTAppendable result;
					if ((result = EvalAttributes.flatten(resultList)).isPresent()) {
						resultList = result;
						if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
							EvalAttributes.sort(resultList);
						}
						resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
						return resultList;
					}
				}
				if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
					EvalAttributes.sort(resultList);
				}
			}
			resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
			return resultList;
		}

		if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
			// associative
			IASTAppendable result;
			if ((result = EvalAttributes.flatten(ast)).isPresent()) {
				resultList = result;
				if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
					EvalAttributes.sort(resultList);
				}
				resultList.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
				return resultList;
			}
		}
		if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
			if (EvalAttributes.sort((IASTMutable) ast)) {
				ast.addEvalFlags(IAST.IS_FLAT_ORDERLESS_EVALED);
				return ast;
			}
			return ast;
		}
		return F.NIL;
	}

	/**
	 * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest, Orderless to create
	 * pattern-matching expressions directly or for the left-hand-side of a <code>Set[]</code>,
	 * <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSetDelayed[]</code> expression
	 * 
	 * @param ast
	 * @return <code>ast</code> if no evaluation was executed.
	 */
	public IExpr evalHoldPattern(IAST ast) {
		return evalHoldPattern(ast, false, false);
	}

	/**
	 * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest, Orderless to create
	 * pattern-matching expressions directly or for the left-hand-side of a <code>Set[]</code>,
	 * <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSetDelayed[]</code> expression
	 * 
	 * @param ast
	 * @param noEvaluation
	 *            (sub-)expressions which contain no patterns should not be evaluated
	 * @param evalNumericFunction
	 *            TODO
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
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated expression or <code>F.NIL</code> if evaluation isn't possible
	 * @see EvalEngine#evalWithoutNumericReset(IExpr)
	 */
	public IExpr evalLoop(@Nonnull final IExpr expr) {

		if ((fRecursionLimit > 0) && (fRecursionCounter > fRecursionLimit)) {
			if (Config.DEBUG) {
				System.out.println(expr.toString());
			}
			RecursionLimitExceeded.throwIt(fRecursionLimit, expr);
		}
		try {
			IExpr result, temp;
			fRecursionCounter++;
			if (fTraceMode) {
				fTraceStack.setUp(expr, fRecursionCounter);
				temp = expr.evaluate(this);
				if (temp.isPresent()) {
					if (fStopRequested) {
						throw TimeoutException.TIMED_OUT;
					}
					fTraceStack.add(expr, temp, fRecursionCounter, 0L, "Evaluation loop");
					result = temp;
					long iterationCounter = 1;
					while (true) {
						temp = result.evaluate(this);
						if (temp.isPresent()) {
							if (fStopRequested) {
								throw TimeoutException.TIMED_OUT;
							}
							fTraceStack.add(result, temp, fRecursionCounter, iterationCounter, "Evaluation loop");
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
				temp = expr.evaluate(this);
				if (temp.isPresent()) {
					if (fStopRequested) {
						throw TimeoutException.TIMED_OUT;
					}
					if (fOnOffMode) {
						printOnOffTrace(expr, temp);
					}

					// if (temp == F.Null&&!expr.isAST(F.SetDelayed)) {
					// System.out.println(expr.toString());
					// }
					// if (expr.isAST(F.Integrate)) {
					// System.out.println("(0):" + expr.toString());
					// System.out.println("(1) --> " + temp.toString());
					// }
					result = temp;
					long iterationCounter = 1;
					while (true) {
						temp = result.evaluate(this);
						if (temp.isPresent()) {
							if (fStopRequested) {
								throw TimeoutException.TIMED_OUT;
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

							result = temp;
							if (fIterationLimit >= 0 && fIterationLimit <= ++iterationCounter) {
								IterationLimitExceeded.throwIt(iterationCounter, result);
							}
						} else {
							return result;
						}
					}
				}
			}

			return F.NIL;
		} finally {
			if (fTraceMode) {
				fTraceStack.tearDown(fRecursionCounter);
			}
			fRecursionCounter--;
		}
	}

	/**
	 * Print the trace enabled by the <code>On({head1, head2,...})</code> function.
	 * 
	 * @param unevaledExpr
	 *            the unevaluated expression
	 * @param evaledExpr
	 *            the evaluated expression
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
	final public IExpr evalN(final IExpr expr) {
		return evaluate(F.N(expr));
	}

	/**
	 * <p>
	 * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation reset the numeric
	 * mode to the value stored before the evaluation starts. If evaluation is not possible return the input object.
	 * </p>
	 * <p>
	 * <b>Note:</b> if this method catches exception <code>org.matheclipse.parser.client.math.MathException</code>, it
	 * returns the input expression.
	 * </p>
	 * 
	 * @param expr
	 *            the object which should be evaluated
	 * @return the evaluated object
	 */
	public final IExpr evalPattern(@Nonnull final IExpr expr) {
		boolean numericMode = fNumericMode;
		try {
			if (expr.isFreeOfPatterns()) {
				return evalWithoutNumericReset(expr);
			}
			if (expr.isAST()) {
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
	 * <p>
	 * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation reset the numeric
	 * mode to the value stored before the evaluation starts. If evaluation is not possible return the input object.
	 * </p>
	 * 
	 * @param patternExpression
	 *            the object which should be evaluated
	 * @return an <code>IPatterMatcher</code> created from the given expression.
	 */
	public final IPatternMatcher evalPatternMatcher(@Nonnull final IExpr patternExpression) {
		IExpr temp = evalPattern(patternExpression);
		return new PatternMatcher(temp);
	}

	/**
	 * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return the input object. In
	 * &quot;quiet mode&quot; all warnings would be suppressed.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
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
	 * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return <code>F.NIL</code>. In
	 * &quot;quiet mode&quot; all warnings would be suppressed.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object or <code>F.NUIL</code> if no evaluation was possible
	 * @see EvalEngine#evalWithoutNumericReset(IExpr)
	 */
	public final IExpr evalQuietNull(final IExpr expr) {
		boolean quiet = isQuietMode();
		try {
			setQuietMode(true);
			return evaluateNull(expr);
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
		// if (symbol instanceof BuiltInSymbol) {
		// try {
		// ((BuiltInSymbol) symbol).getEvaluator().await();
		// } catch (InterruptedException ie) {
		// printMessage("EvalEngine#evalRules( ) interrupted");
		// return F.NIL;
		// }
		// }
		IAST ast;
		if (argsAST.exists(x -> x.isAST(F.Unevaluated, 2))) {
			ast = argsAST.map(x -> {
				if (x.isAST(F.Unevaluated, 2)) {
					return ((IAST) x).arg1();
				}
				return x;
			}, 1);
		} else {
			ast = argsAST;
		}
		IExpr[] result = new IExpr[1];
		result[0] = F.NIL;
		if (ast.exists(x -> {
			if (!(x instanceof IPatternObject)) {
				result[0] = x.topHead().evalUpRule(this, ast);
				if (result[0].isPresent()) {
					return true;
				}
			}
			return false;
		})) {
			return result[0];
		}

		return evalASTBuiltinFunction(symbol, ast);
	}

	private IASTMutable evalSetAttributeArg(IAST ast, int i, IAST argI, IASTMutable resultList, boolean noEvaluation,
			int level) {
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
				} else if (expr.isAST(F.Exp, 2)) {
					if (resultList.isPresent()) {
						resultList.set(i, PowerOp.power(F.E, arg1));
					} else {
						resultList = ast.setAtCopy(i, PowerOp.power(F.E, arg1));
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest, Orderless to create
	 * pattern-matching expressions directly or for the left-hand-side of a <code>Set[]</code>,
	 * <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSetDelayed[]</code> expression
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
	 * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest, Orderless to create
	 * pattern-matching expressions directly or for the left-hand-side of a <code>Set[]</code>,
	 * <code>SetDelayed[]</code>, <code>UpSet[]</code> or <code>UpSetDelayed[]</code> expression
	 * 
	 * @param ast
	 * @param noEvaluation
	 *            (sub-)expressions which contain no patterns should not be evaluated
	 * @return <code>ast</code> if no evaluation was executed.
	 * @deprecated use evalHoldPattern
	 */
	@Deprecated
	public IExpr evalSetAttributes(IAST ast, boolean noEvaluation) {
		return evalHoldPattern(ast, noEvaluation, false);
	}

	private IExpr evalSetAttributesRecursive(IAST ast, boolean noEvaluation, boolean evalNumericFunction, int level) {
		final ISymbol symbol = ast.topHead();
		if (symbol.isBuiltInSymbol()) {
			// call so that attributes may be set in
			// AbstractFunctionEvaluator#setUp() method
			((IBuiltInSymbol) symbol).getEvaluator();
		}
		if (ast.isAST(F.Optional, 2)) {
			return ((IFunctionEvaluator) F.Optional.getEvaluator()).evaluate(ast, this);
		}

		final int attr = symbol.getAttributes();
		IASTMutable resultList = F.NIL;

		if ((ISymbol.HOLDALL & attr) != ISymbol.HOLDALL) {
			final int astSize = ast.size();

			if ((ISymbol.HOLDFIRST & attr) == ISymbol.NOATTRIBUTE) {
				// the HoldFirst attribute isn't set here
				if (astSize > 1 && ast.arg1().isAST()) {
					IExpr expr = ast.arg1();
					if (expr.isAST()) {
						resultList = evalSetAttributeArg(ast, 1, (IAST) expr, resultList, noEvaluation, level);
					}
				}
			}
			if (astSize > 2) {
				if ((ISymbol.HOLDREST & attr) == ISymbol.NOATTRIBUTE) {
					// the HoldRest attribute isn't set here
					for (int i = 2; i < astSize; i++) {
						IExpr expr = ast.get(i);
						if (expr.isAST()) {
							resultList = evalSetAttributeArg(ast, i, (IAST) expr, resultList, noEvaluation, level);
						}
					}
				}
			}
			if (evalNumericFunction && ((ISymbol.HOLDALL & attr) == ISymbol.NOATTRIBUTE)) {
				IAST f = resultList.orElse(ast);
				if (f.isNumericFunction()) {
					IExpr temp = evalLoop(f);
					if (temp.isPresent()) {
						return temp;
					}
				}
			}
		}
		if (resultList.isPresent()) {
			if (resultList.size() > 2) {
				if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
					// associative
					IASTAppendable result;
					if ((result = EvalAttributes.flatten(resultList)).isPresent()) {
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

		if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
			// associative
			IASTAppendable result;
			if ((result = EvalAttributes.flatten(ast)).isPresent()) {
				return evalSetOrderless(result, attr, noEvaluation, level);
			}
		}
		return evalSetOrderless(ast, attr, noEvaluation, level);
	}

	/**
	 * 
	 * @param ast
	 * @param attr
	 * @param noEvaluation
	 * @param level
	 * @return <code>ast</code> if no evaluation was possible
	 */
	private IExpr evalSetOrderless(IAST ast, final int attr, boolean noEvaluation, int level) {
		if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
			EvalAttributes.sort((IASTMutable) ast);
			if (level > 0 && !noEvaluation && ast.isFreeOfPatterns()) {
				if (ast.isPlus()) {
					return Arithmetic.CONST_PLUS.evaluate(ast, this);
				}
				if (ast.isTimes()) {
					return Arithmetic.CONST_TIMES.evaluate(ast, this);
				}
			}
		}
		if (level > 0 && !noEvaluation && ast.isFreeOfPatterns()) {
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
			return UtilityFunctionCtors.evalRubiDistPlus(ast);
		} else if (ast.isTimes()) {
			return UtilityFunctionCtors.evalRubiDistTimes(ast);
		}
		return F.NIL;
	}

	/**
	 * Evaluate the expression and return the <code>Trace[expr]</code> (i.e. all (sub-)expressions needed to calculate
	 * the result).
	 * 
	 * @param expr
	 *            the expression which should be evaluated.
	 * @param matcher
	 *            a filter which determines the expressions which should be traced, If the matcher is set to
	 *            <code>null</code>, all expressions are traced.
	 * @param list
	 *            an IAST object which will be cloned for containing the traced expressions. Typically a
	 *            <code>F.List()</code> will be used.
	 * @return
	 */
	public final IAST evalTrace(final IExpr expr, Predicate<IExpr> matcher, IAST list) {
		IAST traceList = F.List();
		try {
			beginTrace(matcher, list);
			evaluate(expr);
		} finally {
			traceList = endTrace();
		}
		return traceList;
	}

	/**
	 * Test if <code>expr</code> could be evaluated to <code>True</code>. If a
	 * <code>org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return
	 * <code>False</code>.
	 * 
	 * @param expr
	 * @return <code>true</code> if the expression could be evaluated to symbol <code>True</code> and <code>false</code>
	 *         in all other cases
	 */
	public final boolean evalTrue(final IExpr expr) {
		if (expr.isTrue()) {
			return true;
		}
		if (expr.isFalse()) {
			return false;
		}
		try {
			return evaluate(expr).isTrue();
		} catch (MathException fce) {
			if (Config.SHOW_STACKTRACE) {
				fce.printStackTrace();
			}
			return false;
		}
	}

	/**
	 * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation reset the numeric
	 * mode to the value stored before the evaluation starts. If evaluation is not possible return the input object.
	 * 
	 * @param expr
	 *            the object which should be evaluated
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
	 * @param expression
	 *            an expression in math formula notation
	 * @return
	 * @throws org.matheclipse.parser.client.SyntaxError
	 *             if a parsing error occurs
	 */
	final public IExpr evaluate(String expression) {
		return evaluate(parse(expression));
	}

	/**
	 * Parse the given <code>expression String</code> into an IExpr and evaluate it.
	 * 
	 * @param expression
	 *            an expression in math formula notation
	 * @param explicitTimes
	 *            if <code>true</code> require times operator &quot;*&quot;
	 * @return
	 * @throws org.matheclipse.parser.client.SyntaxError
	 *             if a parsing error occurs
	 */
	final public IExpr evaluate(String expression, boolean explicitTimes) {
		return evaluate(parse(expression, explicitTimes));
	}

	/**
	 * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation reset the numeric
	 * mode to the value stored before the evaluation starts. If evaluation is not possible return the input object.
	 * 
	 * @param expr
	 *            the object which should be evaluated
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
	 * 
	 * Evaluate an object and reset the numeric mode to the value before the evaluation step. If evaluation is not
	 * possible return <code>F.NIL</code>.
	 * 
	 * @param expr
	 *            the object which should be evaluated
	 * @return the evaluated object or <code>F.NIL</code> if no evaluation was possible
	 */
	public final IExpr evaluateNull(final IExpr expr) {
		boolean numericMode = fNumericMode;
		try {
			return evalLoop(expr);
		} finally {
			fNumericMode = numericMode;
		}
	}

	/**
	 * Evaluate an object without resetting the numeric mode after the evaluation step. If evaluation is not possible
	 * return the input object,
	 * 
	 * @param expr
	 *            the object which should be evaluated
	 * @return the evaluated object
	 * 
	 */
	public final IExpr evalWithoutNumericReset(final IExpr expr) {
		return evalLoop(expr).orElse(expr);
	}

	/**
	 * Iterate over the arguments of <code>ast</code> and flatten the arguments of <code>Sequence(...)</code>
	 * expressions.
	 * 
	 * @param ast
	 *            an AST which may contain <code>Sequence(...)</code> expressions.
	 * @return
	 */
	private IAST flattenSequences(final IAST ast) {
		IASTAppendable[] seqResult = new IASTAppendable[] { F.NIL };

		ast.forEach((x, i) -> {
			if (x.isSequence()) {
				IAST seq = (IAST) x;
				if (!seqResult[0].isPresent()) {
					seqResult[0] = F.ast(ast.head(), ast.size() + seq.size(), false);
					seqResult[0].appendArgs(ast, i);
				}
				seqResult[0].appendArgs(seq);
			} else if (x.equals(F.Nothing)) {
				if (!seqResult[0].isPresent()) {
					seqResult[0] = F.ast(ast.head(), ast.size() - 1, false);
					seqResult[0].appendArgs(ast, i);
				}
			} else if (seqResult[0].isPresent()) {
				seqResult[0].append(x);
			}
		});
		return seqResult[0];
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

	public ContextPath getContextPath() {
		return fContextPath;
	}

	public PrintStream getErrorPrintStream() {
		return fErrorPrintStream;
	}

	public int getIterationLimit() {
		return fIterationLimit;
	}

	/**
	 * Get the list of modified variables
	 * 
	 * @return <code>null</code> if the set is not defined
	 */
	public Set<ISymbol> getModifiedVariables() {
		return fModifiedVariablesList;
	}

	public int getNumericPrecision() {
		return fNumericPrecision;
	}

	public LastCalculationsHistory getOutList() {
		return fOutList;
	}

	public PrintStream getOutPrintStream() {
		return fOutPrintStream;
	}

	/**
	 * Get the reap list object associated to the most enclosing <code>Reap()</code> statement. The even indices in
	 * <code>java.util.List</code> contain the tag defined in <code>Sow()</code>. If no tag is defined in
	 * <code>Sow()</code> tag <code>F.None</code> is used. The odd indices in <code>java.util.List</code> contain the
	 * associated reap list for the tag.
	 * 
	 * @return the reapList
	 */
	public java.util.List<IExpr> getReapList() {
		return fReapList;
	}

	public int getRecursionCounter() {
		return fRecursionCounter;
	}

	/**
	 * @return
	 */
	public int getRecursionLimit() {
		return fRecursionLimit;
	}

	public long getSeconds() {
		return fSeconds;
	}

	/**
	 * @return
	 */
	public String getSessionID() {
		return fSessionID;
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
	public int incModuleCounter() {
		return ++fModuleCounter;
	}

	/**
	 * Increment the recursion counter by 1 and return the result.
	 * 
	 * @return
	 */
	public int incRecursionCounter() {
		return ++fRecursionCounter;
	}

	/**
	 * Initialize this <code>EvalEngine</code>
	 */
	final public void init() {
		fNumericPrecision = 15;
		fRecursionCounter = 0;
		fNumericMode = false;
		fTogetherMode = false;
		fEvalLHSMode = false;
		fOnOffMode = false;
		fOnOffUnique = false;
		fOnOffUniqueMap = null;
		fOnOffMap = null;
		fTraceMode = false;
		fTraceStack = null;
		fStopRequested = false;
		fSeconds = 0;
		fModifiedVariablesList = null;
		fContextPathStack = new Stack<ContextPath>();
		fContextPath = ContextPath.initialContext();
		REMEMBER_AST_CACHE = null;
	}

	/**
	 * Check if the <code>ApfloatNum</code> number type should be used instead of the <code>Num</code> type and the
	 * <code>ApcomplexxNum</code> number type should be used instead of the <code>ComplexNum</code> type for numeric
	 * evaluations.
	 * 
	 * @return <code>true</code> if the required precision is greater than <code>EvalEngine.DOUBLE_PRECISION</code>
	 * @see ApfloatNum
	 * @see ApcomplexNum
	 */
	public final boolean isApfloat() {
		return fNumericPrecision > Config.MACHINE_PRECISION;
	}

	/**
	 * The engine evaluates the left-hand-side of a <code>Set, SetDelayed,...</code> expression.
	 * 
	 * @return
	 */
	public final boolean isEvalLHSMode() {
		return fEvalLHSMode;
	}

	public final boolean isFileSystemEnabled() {
		return fFileSystemEnabled;
	}

	/**
	 * @return <code>true</code> if the EvalEngine runs in numeric mode.
	 */
	public final boolean isNumericMode() {
		return fNumericMode;
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
	 * Check if the appending of expressions to the history list for the <code>Out[]</code> function is enabled. If
	 * enabled, the special variable <code>$ans</code> returns the result from the last evluation done with this
	 * evaluation engine.
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
	 * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages are shown in the
	 * evaluation).
	 * 
	 * @return
	 * @see org.matheclipse.core.builtin.function.Quiet
	 */
	public final boolean isQuietMode() {
		return fQuietMode;
	}

	/**
	 * @return the fRelaxedSyntax
	 */
	public final boolean isRelaxedSyntax() {
		return fRelaxedSyntax;
	}

	/**
	 * @return Returns the stopRequested.
	 */
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
	 * If the trace mode is set the system writes an evaluation trace list or if additionally the <i>stop after
	 * evaluation mode</i> is set returns the first evaluated result.
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
	 * @param expression
	 *            an expression in math formula notation
	 * 
	 * @return
	 * @throws org.matheclipse.parser.client.SyntaxError
	 *             if a parsing error occurs
	 */
	final public IExpr parse(String expression) {
		return parse(expression, Config.EXPLICIT_TIMES_OPERATOR);
		// final ExprParser parser = new ExprParser(this, fRelaxedSyntax);
		// return parser.parse(expression);
	}

	/**
	 * Parse the given <code>expression String</code> into an IExpr without evaluation.
	 * 
	 * @param expression
	 *            an expression in math formula notation
	 * @param explicitTimes
	 *            if <code>true</code> require times operator &quot;*&quot;
	 * @return
	 * @throws org.matheclipse.parser.client.SyntaxError
	 *             if a parsing error occurs
	 */

	final public IExpr parse(String expression, boolean explicitTimes) {
		final ExprParser parser = new ExprParser(this, ExprParserFactory.RELAXED_STYLE_FACTORY, fRelaxedSyntax, false,
				explicitTimes);
		return parser.parse(expression);
	}

	/**
	 * Print a message to the <code>Out</code> stream, if the engine is not in &quot;quiet mode&quot;.
	 * 
	 * @param str
	 *            the message which should be printed
	 */
	public IAST printMessage(String str) {
		if (!isQuietMode()) {
			PrintStream stream = getErrorPrintStream();
			if (stream == null) {
				stream = System.err;
			}
			stream.println(str);
		}
		if (fThrowError) {
			throw new IllegalArgument(str);
		}
		return F.NIL;
	}

	/**
	 * Reset the numeric mode flag and the recursion counter
	 * 
	 */
	public void reset() {
		fNumericPrecision = 15;
		fNumericMode = false;
		fEvalLHSMode = false;
		fRecursionCounter = 0;
		fTogetherMode = false;
		fEvalLHSMode = false;
		fTraceMode = false;
		fTraceStack = null;
		fStopRequested = false;
		fSeconds = 0;
		fModifiedVariablesList = null;
		REMEMBER_AST_CACHE = null;

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

	/**
	 * @param b
	 */
	public void setNumericMode(final boolean b) {
		fNumericMode = b;
	}

	/**
	 * Set the numeric mode and precision of numeric calculations.
	 * 
	 * @param b
	 * @param precision
	 */
	public void setNumericMode(final boolean b, int precision) {
		fNumericMode = b;
		fNumericPrecision = precision;
	}

	public void setNumericPrecision(int precision) {
		fNumericPrecision = precision;
	}

	/**
	 * 
	 * @param outListDisabled
	 *            if <code>false</code> create a <code>LastCalculationsHistory(historyCapacity)</code>, otherwise no
	 *            history of the last calculations will be saved and the <code>Out()</code> function (or % operator)
	 *            will be unevaluated.
	 * @param historyCapacity
	 *            the number of last entries of the calculations which should be stored.
	 */
	public void setOutListDisabled(boolean outListDisabled, int historyCapacity) {
		if (outListDisabled == false) {
			if (fOutList == null) {
				fOutList = new LastCalculationsHistory(historyCapacity);
			}
		} else {
			fOutList = null;
		}
		this.fOutListDisabled = outListDisabled;
	}

	/**
	 * Set the mode for the <code>On()</code> or <code>Off()</code> function
	 * 
	 * @param onOffMode
	 *            if <code>true</code> every evaluation step will be printd to the defined out stream.
	 * @param headSymbolsMap
	 *            the header symbols which should trigger an output in the trace
	 * @param uniqueTrace
	 *            the output is printed only once for a combination of _unevaluated_ input expression and _evaluated_
	 *            output expression.
	 */
	public void setOnOffMode(final boolean onOffMode, IdentityHashMap<ISymbol, ISymbol> headSymbolsMap,
			boolean uniqueTrace) {
		fOnOffMode = onOffMode;
		fOnOffMap = headSymbolsMap;
		fOnOffUnique = uniqueTrace;
		if (uniqueTrace) {
			fOnOffUniqueMap = new HashMap<IExpr, IExpr>();
		}
	}

	public void setOutListDisabled(LastCalculationsHistory outList) {
		this.fOutList = outList;
		this.fOutListDisabled = false;
	}

	public void setOutPrintStream(final PrintStream outPrintStream) {
		fOutPrintStream = outPrintStream;
	}

	public void setPackageMode(boolean packageMode) {
		fPackageMode = packageMode;
	}

	/**
	 * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages are showw in the
	 * evaluation).
	 * 
	 * @param quietMode
	 */
	public void setQuietMode(boolean quietMode) {
		this.fQuietMode = quietMode;
	}

	/**
	 * @param reapList
	 *            the reapList to set
	 */
	public void setReapList(java.util.List<IExpr> reapList) {
		this.fReapList = reapList;
	}

	/**
	 * @param i
	 */
	public void setRecursionLimit(final int i) {
		fRecursionLimit = i;
	}

	/**
	 * @param fRelaxedSyntax
	 *            the fRelaxedSyntax to set
	 */
	public void setRelaxedSyntax(boolean fRelaxedSyntax) {
		this.fRelaxedSyntax = fRelaxedSyntax;
	}

	public void setSeconds(long fSeconds) {
		this.fSeconds = fSeconds;
	}

	/**
	 * @param string
	 */
	public void setSessionID(final String string) {
		fSessionID = string;
	}

	/**
	 * Set the step listener for this evaluation engine. The method also calls <code>setTraceMode(true)</code> to enable
	 * the trace mode. The caller is responsible for calling <code>setTraceMode(false)</code> if no further listening is
	 * desirable.
	 * 
	 * @param stepListener
	 *            the listener which should listen to the evaluation steps.
	 */
	public void setStepListener(IEvalStepListener stepListener) {
		setTraceMode(true);
		fTraceStack = stepListener;
	}

	/**
	 * @param stopRequested
	 *            The stopRequested to set.
	 */
	public void setStopRequested(final boolean stopRequested) {
		fStopRequested = stopRequested;
	}

	/**
	 * Throw an <code>IllegalArgument</code> exception if an error message is printed in method
	 * <code>printMessage()</code>.
	 * 
	 * @param throwError
	 */
	public void setThrowError(boolean throwError) {
		this.fThrowError = throwError;
	}

	public void setTogetherMode(boolean fTogetherMode) {
		this.fTogetherMode = fTogetherMode;
	}

	/**
	 * @param b
	 */
	public void setTraceMode(final boolean b) {
		fTraceMode = b;
	}

	/**
	 * The size of the <code>Out[]</code> list
	 * 
	 * @return
	 */
	public int sizeOut() {
		return fOutList.size();
	}

	public void stopRequest() {
		fStopRequested = true;
	}

	/**
	 * Thread through all lists in the arguments of the IAST (i.e. the ast's head has the attribute
	 * <code>ISymbol.LISTABLE</code>) example: <code>Sin[{2,x,Pi}] ==> {Sin[2],Sin[x],Sin[Pi]}</code>
	 * 
	 * @param ast
	 * @return the resulting ast with the <code>argHead</code> threaded into each ast argument or <code>F.NIL</code>
	 */
	public IASTMutable threadASTListArgs(final IASTMutable ast) {

		int[] listLength = new int[] { -1 };
		if (ast.exists(x -> {
			if (x.isList()) {
				if (listLength[0] < 0) {
					listLength[0] = ((IAST) x).argSize();
				} else {
					if (listLength[0] != ((IAST) x).argSize()) {
						printMessage("Lists of unequal lengths cannot be combined: " + ast.toString());
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
			IASTAppendable result = EvalAttributes.threadList(ast, F.List, ast.head(), listLength[0]);
			result.addEvalFlags(IAST.IS_LISTABLE_THREADED);
			return result;
		}
		ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
		return F.NIL;
	}

}