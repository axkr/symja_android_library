package org.matheclipse.core.eval;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.MethodSymbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IEvaluationEngine;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.reflection.system.Plus;
import org.matheclipse.core.reflection.system.Times;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathException;

import com.google.common.base.Predicate;

/**
 * The main evaluation algorithms for the .Symja computer algebra system
 */
public class EvalEngine implements Serializable, IEvaluationEngine {
	/**
	 * 
	 */
	private static final long serialVersionUID = 407328682800652434L;

	/**
	 * Evaluate an expression. If evaluation is not possible return the input object.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object
	 * @see EvalEngine#evalWithoutNumericReset(IExpr)
	 */
	public static final IExpr eval(final IExpr expr) {
		return instance.get().evaluate(expr);
	}

	/**
	 * Evaluate an expression for the given &quot;local variables list&quot;. If evaluation is not possible return the input object.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @param localVariablesList
	 *            a list of symbols which should be used as local variables inside the block
	 * @return the evaluated object
	 */
	public IExpr evalBlock(final IExpr expr, final IAST localVariablesList) {
		final List<ISymbol> variables = new ArrayList<ISymbol>();
		IExpr result = null;

		try {
			// remember which local variables we use:
			ISymbol blockVariableSymbol;

			for (int i = 1; i < localVariablesList.size(); i++) {
				if (localVariablesList.get(i).isSymbol()) {
					blockVariableSymbol = (ISymbol) localVariablesList.get(i);
					blockVariableSymbol.pushLocalVariable();
					variables.add(blockVariableSymbol);
				} else {
					if (localVariablesList.get(i).isAST(F.Set, 3)) {
						// lhs = rhs
						final IAST setFun = (IAST) localVariablesList.get(i);
						if (setFun.arg1().isSymbol()) {
							blockVariableSymbol = (ISymbol) setFun.arg1();
							blockVariableSymbol.pushLocalVariable();
							// this evaluation step may throw an exception
							IExpr temp = evaluate(setFun.arg2());
							blockVariableSymbol.set(temp);
							variables.add(blockVariableSymbol);
						}
					} else {
						return expr;
					}
				}
			}

			result = evaluate(expr);
			if (result != null) {
				return result;
			}
			return expr;
		} finally {
			// pop all local variables from local variable stack
			for (int i = 0; i < variables.size(); i++) {
				variables.get(i).popLocalVariable();
			}
		}
	}

	/**
	 * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return the input object. In &quot;quiet
	 * mode&quot; all warnings would be suppressed.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object
	 * @see EvalEngine#evalWithoutNumericReset(IExpr)
	 */
	public static final IExpr evalQuiet(final IExpr expr) {
		EvalEngine engine = instance.get();
		boolean quiet = engine.isQuietMode();
		try {
			engine.setQuietMode(true);
			return engine.evaluate(expr);
		} finally {
			engine.setQuietMode(quiet);
		}
	}

	/**
	 * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return <code>null</code>. In &quot;quiet
	 * mode&quot; all warnings would be suppressed.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object or <code>null</code> if no evaluation was possible
	 * @see EvalEngine#evalWithoutNumericReset(IExpr)
	 */
	public static final IExpr evalQuietNull(final IExpr expr) {
		EvalEngine engine = instance.get();
		boolean quiet = engine.isQuietMode();
		try {
			engine.setQuietMode(true);
			return engine.evaluateNull(expr);
		} finally {
			engine.setQuietMode(quiet);
		}
	}

	/**
	 * Evaluate an expression. If evaluation is not possible return <code>null</code>.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object or <code>null</code> if no evaluation was possible
	 * @see EvalEngine#evalWithoutNumericReset(IExpr)
	 */
	public static final IExpr evalNull(final IExpr expr) {
		return (instance.get()).evaluateNull(expr);
	}

	synchronized public static int getNextAnonymousCounter() {
		return ++fAnonymousCounter;
	}

	synchronized public static String getNextCounter() {
		return Integer.toString(++fAnonymousCounter);
	}

	/**
	 * Get the local variable stack for a given symbol name. If the local variable stack doesn't exist, return <code>null</code>
	 * 
	 * @param symbolName
	 * @return <code>null</code> if the stack doesn't exist
	 */
	final public static Stack<IExpr> localStack(final ISymbol symbol) {
		return get().getLocalVariableStackMap().get(symbol);
	}

	/**
	 * Get the local variable stack for a given symbol name. If the local variable stack doesn't exist, create a new one for the
	 * symbol.
	 * 
	 * @param symbolName
	 * @return
	 */
	public static Stack<IExpr> localStackCreate(final ISymbol symbol) {
		Map<ISymbol, Stack<IExpr>> localVariableStackMap = get().getLocalVariableStackMap();
		Stack<IExpr> temp = localVariableStackMap.get(symbol);
		if (temp != null) {
			return temp;
		}
		temp = new Stack<IExpr>();
		localVariableStackMap.put(symbol, temp);
		return temp;
	}

	/**
	 * Set the thread local evaluation engine instance
	 * 
	 * @return
	 */
	public static void set(final EvalEngine engine) {
		instance.set(engine);
	}

	public static IAST threadASTListArgs(final IAST ast) {
		IAST result;
		int listLength = 0;
		final int astSize = ast.size();
		for (int i = 1; i < astSize; i++) {
			if (ast.get(i).isList()) {
				if (listLength == 0) {
					listLength = ((IAST) ast.get(i)).size() - 1;
				} else {
					if (listLength != ((IAST) ast.get(i)).size() - 1) {
						ast.setEvalFlags(IAST.IS_LISTABLE_THREADED);
						return null;
					}
				}
			}
		}
		if ((listLength != 0) && ((result = EvalAttributes.threadList(ast, F.List, ast.head(), listLength)) != null)) {
			result.setEvalFlags(IAST.IS_LISTABLE_THREADED);
			return result;
		}
		ast.setEvalFlags(IAST.IS_LISTABLE_THREADED);
		return null;
	}

	/**
	 * Associate a symbol name in this ThreadLocal with the symbol created in this thread
	 * 
	 * @see ExprFactory.fSymbolMap for global symbol names
	 */
	private Map<String, ISymbol> fUserVariableMap;

	/**
	 * Associate a symbol name with a local variable stack in this thread.
	 * 
	 */
	transient private IdentityHashMap<ISymbol, Stack<IExpr>> fLocalVariableStackMap = null;

	/**
	 * If set to <code>true</code> the current thread should stop evaluation;
	 */
	transient volatile boolean fStopRequested;

	transient int fRecursionCounter;

	/**
	 * if <code>true</code> the engine evaluates in &quot;numeric&quot; mode, otherwise the engine evaluates in &quot;symbolic&quot;
	 * mode.
	 */
	transient boolean fNumericMode;

	/**
	 * The precision for numeric operations.
	 */
	transient int fNumericPrecision = 15;

	transient boolean fEvalLHSMode;

	transient String fSessionID;

	/**
	 * If <code>true</code> the engine evaluates in &quot;Trace()&quot; function mode.
	 * 
	 * @see #evalTrace(IExpr, Predicate, IAST)
	 */
	transient boolean fTraceMode;

	transient IAssumptions fAssumptions = null;

	transient IEvalStepListener fTraceStack = null;

	transient PrintStream fOutPrintStream = null;

	protected int fRecursionLimit;

	protected int fIterationLimit;

	static int fAnonymousCounter = 0;

	protected boolean fPackageMode = false;

	transient int fModuleCounter = 0;

	private boolean fRelaxedSyntax;

	/**
	 * List for results in <code>Reap[]</code> function.
	 */
	transient IAST reapList = null;

	protected Set<ISymbol> fModifiedVariablesList;

	/**
	 * The history list for the <code>Out[]</code> function.
	 * 
	 * @see org.matheclipse.core.reflection.Out
	 */
	transient protected LastCalculationsHistory fOutList = null;

	/**
	 * Contains the last result (&quot;answer&quot;) expression of this evaluation engine or <code>null</code> if no answer is
	 * stored in the evaluation engine.
	 */
	transient protected IExpr fAnswer = null;

	/**
	 * Flag for disabling the appending of expressions to the history list for the <code>Out[]</code> function.
	 * 
	 * @see org.matheclipse.core.reflection.Out
	 */
	transient private boolean fOutListDisabled = true;

	/**
	 * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages are shown during evaluation).
	 * 
	 * @see org.matheclipse.core.builtin.function.Quiet
	 */
	transient boolean fQuietMode = false;

	/**
	 * Use <code>Num</code> objects for numeric calculations up to 15 digits precision.
	 */
	public static final int DOUBLE_PRECISION = 15;

	public final static boolean DEBUG = false;

	transient private static final ThreadLocal<EvalEngine> instance = new ThreadLocal<EvalEngine>() {
		private int fID = 1;

		@Override
		public EvalEngine initialValue() {
			if (DEBUG) {
				System.out.println("ThreadLocal" + fID);
			}
			return new EvalEngine("ThreadLocal" + (fID++), 0, System.out, false, true);
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
	 * Removes the current thread's value for the EvalEngine's thread-local variable.
	 * 
	 * @see java.lang.ThreadLocal#remove()
	 */
	public static void remove() {
		instance.remove();
	}

	/**
	 * 
	 * 
	 */
	public EvalEngine() {
		this("", 0, System.out, false, true);
	}

	/**
	 * Constructor for an evaluation engine
	 * 
	 * @param relaxedSyntax
	 *            if <code>true</code>, the parser doesn't distinguish between upper and lower case identifiers
	 */
	public EvalEngine(boolean relaxedSyntax) {
		this("", 0, System.out, relaxedSyntax, true);
	}

	/**
	 * Constructor for an evaluation engine
	 * 
	 * @param relaxedSyntax
	 *            if <code>true</code>, the parser doesn't distinguidh between upper and lower case identifiers
	 * @param outListDisabled
	 *            if <code>true</code>, no output history for the <code>Out()</code> function is stored in the evaluation engine.
	 */
	public EvalEngine(boolean relaxedSyntax, boolean outListDisabled) {
		this("", 0, System.out, relaxedSyntax, outListDisabled);
	}

	/**
	 * Constructor for an evaluation engine
	 * 
	 * @param sessionID
	 *            an ID which uniquely identifies this session
	 * @param iterationLimit
	 *            the maximum allowed iteration limit (if set to zero, no limit will be checked)
	 * @param recursionLimit
	 *            the maximum allowed recursion limit (if set to zero, no limit will be checked)
	 * @param out
	 *            the output print stream
	 * @param relaxedSyntax
	 *            if <code>true</code>, the parser doesn't distinguidh between upper and lower case identifiers
	 * @param outListDisabled
	 *            if <code>true</code>, no output history for the <code>Out()</code> function is stored in the evaluation engine.
	 */
	public EvalEngine(final String sessionID, final int recursionLimit, final int iterationLimit, final PrintStream out,
			boolean relaxedSyntax, boolean outListDisabled) {
		fSessionID = sessionID;
		// fExpressionFactory = f;
		fRecursionLimit = recursionLimit;
		fIterationLimit = iterationLimit;
		fOutPrintStream = out;
		fRelaxedSyntax = relaxedSyntax;
		fOutListDisabled = outListDisabled;
		// fNamespace = fExpressionFactory.getNamespace();

		init();

		// set this EvalEngine to the thread local
		set(this);
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
	 * @param outListDisabled
	 *            if <code>true</code>, no output history for the <code>Out()</code> function is stored in the evaluation engine.
	 */
	public EvalEngine(final String sessionID, final int recursionLimit, final PrintStream out, boolean relaxedSyntax,
			boolean outListDisabled) {
		this(sessionID, recursionLimit, -1, out, relaxedSyntax, outListDisabled);
	}

	public EvalEngine(final String sessionID, final PrintStream out) {
		this(sessionID, -1, -1, out, false, true);
	}

	/**
	 * For every evaluation store the list of modified variables in an internal list.
	 * 
	 * @param arg0
	 * @return
	 */
	public boolean addModifiedVariable(ISymbol arg0) {
		return fModifiedVariablesList.add(arg0);
	}

	/**
	 * Add an expression to the <code>Out[]</code> list. To avoid memory leaks you can disable the appending of expressions to the
	 * output history.
	 * 
	 * @see #setOutListDisabled(boolean)
	 */
	public void addOut(IExpr arg0) {
		// remember the last result
		if (arg0 == null) {
			fAnswer = F.Null;
		} else {
			fAnswer = arg0;
		}
		ISymbol ans = F.$s("$ans");
		ans.putDownRule(ISymbol.RuleType.SET, true, ans, fAnswer, false);
		if (fOutListDisabled) {
			return;
		}
		fOutList.add(fAnswer);
	}

	public void addRules(IAST ruleList) {
		boolean oldPackageMode = isPackageMode();
		boolean oldTraceMode = isTraceMode();
		try {
			setPackageMode(true);
			setTraceMode(false);
			final int ruleSize = ruleList.size();
			for (int i = 1; i < ruleSize; i++) {
				if (ruleList.get(i) != null) {
					evaluate(ruleList.get(i));
				}
			}
		} finally {
			setPackageMode(oldPackageMode);
			setTraceMode(oldTraceMode);
		}
	}

	/**
	 * Set the step listener for this evaluation engine. The method also calls <code>setTraceMode(true)</code> to enable the trace
	 * mode. The caller is responsible for calling <code>setTraceMode(false)</code> if no further listening is desirable.
	 * 
	 * @param stepListener
	 *            the listener which should listen to the evaluation steps.
	 */
	public void setStepListener(IEvalStepListener stepListener) {
		setTraceMode(true);
		fTraceStack = stepListener;
	}

	private void beginTrace(Predicate<IExpr> matcher, IAST list) {
		setTraceMode(true);
		fTraceStack = new TraceStack(matcher, list);
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
	 * Evaluate the arguments of the given ast, taking the attributes HoldFirst, HoldRest into account.
	 * 
	 * @param ast
	 * @param attr
	 * @return
	 */
	private IAST evalArgs(final IAST ast, final int attr) {
		final int astSize = ast.size();
		if (astSize > 1) {
			boolean numericMode = fNumericMode;
			boolean localNumericMode = fNumericMode;
			if (!fNumericMode) {
				for (int i = 1; i < astSize; i++) {
					if (ast.get(i).isNumeric()) {
						localNumericMode = true;
						break;
					}
				}
			}

			IAST resultList = null;
			IExpr evaledExpr;
			if ((ISymbol.HOLDFIRST & attr) == ISymbol.NOATTRIBUTE) {
				// the HoldFirst attribute isn't set here
				try {
					if ((ISymbol.NHOLDFIRST & attr) == ISymbol.NHOLDFIRST) {
						fNumericMode = false;
					} else {
						fNumericMode = localNumericMode;
					}
					if ((evaledExpr = evalLoop(ast.arg1())) != null) {
						resultList = ast.setAtClone(1, evaledExpr);
						resultList.setEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
						if (astSize == 2) {
							return resultList;
						}
					}
				} finally {
					if ((ISymbol.NHOLDFIRST & attr) == ISymbol.NHOLDFIRST) {
						fNumericMode = numericMode;
					}
				}
			}
			if ((ISymbol.HOLDREST & attr) == ISymbol.NOATTRIBUTE) {
				// the HoldRest attribute isn't set here
				numericMode = fNumericMode;
				try {
					if ((ISymbol.NHOLDREST & attr) == ISymbol.NHOLDREST) {
						fNumericMode = false;
					} else {
						fNumericMode = localNumericMode;
					}
					for (int i = 2; i < astSize; i++) {
						if ((evaledExpr = evalLoop(ast.get(i))) != null) {
							if (resultList == null) {
								resultList = ast.clone();
								resultList.setEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
							}
							resultList.set(i, evaledExpr);
						}
					}
				} finally {
					if ((ISymbol.NHOLDREST & attr) == ISymbol.NHOLDREST) {
						fNumericMode = numericMode;
					}
				}
			}
			if (resultList != null) {
				return resultList;
			}
		}
		return null;
	}

	/**
	 * Evaluate an AST. The evaluation steps are controlled by the header attributes.
	 * 
	 * @param ast
	 * @return
	 */
	public IExpr evalAST(IAST ast) {
		IExpr head = ast.head();
		ISymbol symbol = null;
		if (head instanceof ISymbol) {
			symbol = (ISymbol) head;
			final IEvaluator module = symbol.getEvaluator();
			if (module instanceof ICoreFunctionEvaluator) {
				// evaluate a built-in function.
				if (fNumericMode) {
					return ((ICoreFunctionEvaluator) module).numericEval(ast);
				}
				return ((ICoreFunctionEvaluator) module).evaluate(ast);
			}
		} else {
			symbol = ast.topHead();
		}

		IExpr result = evalAttributes(symbol, ast);
		if (result != null) {
			return result;
		}
		// System.out.println(ast.toString());
		return evalRules(symbol, ast);
	}

	/**
	 * Evaluate the rules for an AST.
	 * 
	 * @param symbol
	 * @param ast
	 * @return
	 */
	public IExpr evalRules(ISymbol symbol, IAST ast) {
		IExpr result;
		for (int i = 1; i < ast.size(); i++) {
			if (!(ast.get(i) instanceof IPatternObject)) {
				final IExpr arg = ast.get(i);
				ISymbol lhsSymbol = null;
				if (arg.isSymbol()) {
					lhsSymbol = (ISymbol) arg;
				} else {
					lhsSymbol = arg.topHead();
				}
				if ((result = lhsSymbol.evalUpRule(this, ast)) != null) {
					return result;
				}
			}
		}

		return evalASTBuiltinFunction(symbol, ast);
	}

	public IExpr evalASTAttributes(IAST ast) {
		IExpr head = ast.head();
		ISymbol symbol = null;
		if (head instanceof ISymbol) {
			symbol = (ISymbol) head;
			final IEvaluator module = symbol.getEvaluator();
			if (module instanceof ICoreFunctionEvaluator) {
				// evaluate a built-in function.
				if (fNumericMode) {
					return ((ICoreFunctionEvaluator) module).numericEval(ast);
				}
				return ((ICoreFunctionEvaluator) module).evaluate(ast);
			}

		} else {
			symbol = ast.topHead();
		}

		return evalAttributes(symbol, ast);
	}

	/**
	 * Evaluate an AST. The evaluation steps are controlled by the header attributes.
	 * 
	 * @param ast
	 * @return
	 */
	public IExpr evalAttributes(ISymbol symbol, IAST ast) {
		IExpr head = ast.head();
		// ISymbol symbol = null;
		// if (head instanceof ISymbol) {
		// symbol = (ISymbol) head;
		// final IEvaluator module = symbol.getEvaluator();
		// if (module instanceof ICoreFunctionEvaluator) {
		// // evaluate a built-in function.
		// if (fNumericMode) {
		// return ((ICoreFunctionEvaluator) module).numericEval(ast);
		// }
		// return ((ICoreFunctionEvaluator) module).evaluate(ast);
		// }
		//
		// } else {
		// symbol = ast.topHead();
		// }

		final int astSize = ast.size();
		if (astSize == 2) {
			return evalASTArg1(ast);
		}

		// first evaluate the header !
		IExpr result = evalLoop(head);
		if (result != null) {
			return ast.apply(result);
		}

		if (astSize != 1) {
			final int attr = symbol.getAttributes();

			if ((result = flattenSequences(ast)) != null) {
				return result;
			}

			// ONEIDENTITY is checked in the evalASTArg1() method!

			IAST flattened = null;
			if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
				// associative symbol
				if ((flattened = EvalAttributes.flatten(ast)) != null) {
					IAST resultList = evalArgs(flattened, attr);
					if (resultList != null) {
						return resultList;
					}
					return flattened;
				}
			}

			IAST resultList = evalArgs(ast, attr);
			if (resultList != null) {
				return resultList;
			}

			if ((ISymbol.LISTABLE & attr) == ISymbol.LISTABLE
					&& !((ast.getEvalFlags() & IAST.IS_LISTABLE_THREADED) == IAST.IS_LISTABLE_THREADED)) {
				// thread over the lists
				resultList = threadASTListArgs(ast);
				if (resultList != null) {
					return resultList;
				}
			}

			if (astSize > 2 && (ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
				// commutative symbol
				EvalAttributes.sort(ast);
			}
		}

		return null;

	}

	/**
	 * Evaluate an AST with only one argument (i.e. <code>head[arg1]</code>). The evaluation steps are controlled by the header
	 * attributes.
	 * 
	 * @param ast
	 * @return
	 */
	private IExpr evalASTArg1(final IAST ast) {
		// special case ast.size() == 2
		// head == ast[0] --- arg1 == ast[1]
		IExpr result;
		if ((result = evalLoop(ast.head())) != null) {
			// set the new evaluated header !
			return ast.apply(result);
		}

		final ISymbol symbol = ast.topHead();
		final int attr = symbol.getAttributes();

		if ((result = flattenSequences(ast)) != null) {
			return result;
		}

		if ((ISymbol.ONEIDENTITY & attr) == ISymbol.ONEIDENTITY) {
			return ast.arg1();
		}

		if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
			final IExpr arg1 = ast.arg1();
			if (arg1.topHead().equals(symbol)) {
				// associative
				return arg1;
			}
		}

		if ((result = evalArgs(ast, attr)) != null) {
			return result;
		}

		if ((ISymbol.LISTABLE & attr) == ISymbol.LISTABLE) {
			final IExpr arg1 = ast.arg1();
			if (arg1.isList()) {
				// thread over the list
				if ((result = EvalAttributes.threadList(ast, F.List, ast.head(), ((IAST) arg1).size() - 1)) != null) {
					return result;
				}
			}
		}

		if (!(ast.arg1() instanceof IPatternObject)) {
			final IExpr arg1 = ast.arg1();
			ISymbol lhsSymbol = null;
			if (arg1.isSymbol()) {
				lhsSymbol = (ISymbol) arg1;
			} else {
				lhsSymbol = arg1.topHead();
			}
			if ((result = lhsSymbol.evalUpRule(this, ast)) != null) {
				return result;
			}
		}

		return evalASTBuiltinFunction(symbol, ast);
	}

	/**
	 * 
	 * @param symbol
	 * @param ast
	 * @return
	 */
	private IExpr evalASTBuiltinFunction(final ISymbol symbol, final IAST ast) {

		if (fEvalLHSMode) {
			final int attr = symbol.getAttributes();
			if ((ISymbol.HOLDALL & attr) == ISymbol.HOLDALL) {
				// check for Set or SetDelayed necessary, because of dynamic
				// evaluation then initializing rules for predefined symbols
				// (i.e. Sin,
				// Cos,...)
				if (!(symbol.equals(F.Set) || symbol.equals(F.SetDelayed) || symbol.equals(F.UpSet) || symbol
						.equals(F.UpSetDelayed))) {
					return null;
				}
			} else {
				if ((ISymbol.NUMERICFUNCTION & attr) != ISymbol.NUMERICFUNCTION) {
					return null;
				}
			}
		}

		if (!symbol.equals(F.Integrate)) {
			IExpr result;
			if ((result = symbol.evalDownRule(this, ast)) != null) {
				return result;
			}
		}

		if (symbol instanceof MethodSymbol) {
			return ((MethodSymbol) symbol).invoke(ast);
		} else {
			final IEvaluator module = symbol.getEvaluator();
			if (module instanceof IFunctionEvaluator) {
				// evaluate a built-in function.
				if (fNumericMode) {
					return ((IFunctionEvaluator) module).numericEval(ast);
				}
				return ((IFunctionEvaluator) module).evaluate(ast);
			}
		}
		return null;
	}

	/**
	 * Evaluate an object, if evaluation is not possible return <code>null</code>.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated expression or <code>null</code> is evasluation isn't possible
	 * @see EvalEngine#evalWithoutNumericReset(IExpr)
	 */
	public IExpr evalLoop(final IExpr expr) {
		if ((fRecursionLimit > 0) && (fRecursionCounter > fRecursionLimit)) {
			if (Config.DEBUG) {
				System.out.println(expr.toString());
			}
			RecursionLimitExceeded.throwIt(fRecursionLimit, expr);
		}

		try {
			fRecursionCounter++;
			if (fTraceMode) {
				fTraceStack.setUp(expr, fRecursionCounter);
			}

			IExpr temp = expr.evaluate(this);
			if (temp != null) {

				// if (temp == F.Null&&!expr.isAST(F.SetDelayed)) {
				// System.out.println(expr.toString());
				// }
				// if (expr.isAST(F.Integrate)) {
				// System.out.println("(0):" + expr.toString());
				// System.out.println("(1) --> " + temp.toString());
				// }
				if (fTraceMode) {
					fTraceStack.add(expr, temp, fRecursionCounter, 0L, "Evaluation loop");
				}
				IExpr result = temp;
				long iterationCounter = 1;
				do {
					temp = result.evaluate(this);
					if (temp != null) {
						// if (temp == F.Null&&!result.isAST(F.SetDelayed)) {
						// System.out.println(expr.toString());
						// }
						// if (result.isAST(F.Integrate)) {
						// System.out.println(result.toString());
						// System.out.println("("+iterationCounter+") --> " + temp.toString());
						// }
						if (fTraceMode) {
							fTraceStack.add(result, temp, fRecursionCounter, iterationCounter, "Evaluation loop");
						}
						result = temp;
						if (fIterationLimit >= 0 && fIterationLimit <= ++iterationCounter) {
							IterationLimitExceeded.throwIt(iterationCounter, result);
						}
					}
				} while (temp != null);
				// System.out.println("(0):" + expr.toString());
				// System.out.println("(" + iterationCounter + ") --> " + result.toString());
				return result;

			}
			return null;
		} finally {
			if (fTraceMode) {
				fTraceStack.tearDown(fRecursionCounter);
			}
			fRecursionCounter--;
		}
	}

	/**
	 * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest, Orderless to create
	 * pattern-matching expressions directly or for the left-hand-side of a <code>Set[]</code>, <code>SetDelayed[]</code>,
	 * <code>UpSet[]</code> or <code>UpSetDelayed[]</code> expression
	 * 
	 * @param ast
	 * @return <code>ast</code> if no evaluation was executed.
	 */
	public IExpr evalSetAttributes(IAST ast) {
		return evalSetAttributes(ast, false);
	}

	/**
	 * Evaluate the ast recursively, according to the attributes Flat, HoldAll, HoldFirst, HoldRest, Orderless to create
	 * pattern-matching expressions directly or for the left-hand-side of a <code>Set[]</code>, <code>SetDelayed[]</code>,
	 * <code>UpSet[]</code> or <code>UpSetDelayed[]</code> expression
	 * 
	 * @param ast
	 * @param noEvaluation
	 *            (sub-)expressions which contain no patterns should not be evaluated
	 * @return <code>ast</code> if no evaluation was executed.
	 */
	public IExpr evalSetAttributes(IAST ast, boolean noEvaluation) {
		boolean evalLHSMode = fEvalLHSMode;
		try {
			fEvalLHSMode = true;
			if ((ast.getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) != 0x0000) {
				// already flattened or sorted
				return ast;
			}
			return ast.optional(evalSetAttributesRecursive(ast, noEvaluation, false, 0));
		} finally {
			fEvalLHSMode = evalLHSMode;
		}
	}

	private IExpr evalSetAttributesRecursive(IAST ast, boolean noEvaluation, boolean evalNumericFunction, int level) {
		final ISymbol symbol = ast.topHead();
		// call so that attributes may be set in AbstractFunctionEvaluator#setUp() method
		symbol.getEvaluator();

		final int attr = symbol.getAttributes();
		IAST resultList = null;

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
			if ((ISymbol.HOLDREST & attr) == ISymbol.NOATTRIBUTE) {
				// the HoldRest attribute isn't set here
				for (int i = 2; i < astSize; i++) {
					IExpr expr = ast.get(i);
					if (expr.isAST()) {
						resultList = evalSetAttributeArg(ast, i, (IAST) ast.get(i), resultList, noEvaluation, level);
					}
				}
			}
			if (evalNumericFunction && ((ISymbol.HOLDALL & attr) == ISymbol.NOATTRIBUTE)) {
				IAST f = resultList != null ? resultList : ast;
				if (f.isNumericFunction()) {
					IExpr temp = evalLoop(f);
					if (temp != null) {
						return temp;
					}
				}
			}
		}
		if (resultList != null) {
			if (resultList.size() > 2) {
				if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
					// associative
					IAST result;
					if ((result = EvalAttributes.flatten(resultList)) != null) {
						return result.optional(evalSetOrderless(result, attr, noEvaluation, level)); 
					}
				}
				IExpr expr = evalSetOrderless(resultList, attr, noEvaluation, level);
				if (expr != null) {
					return expr;
				}
			}
			return resultList;
		}

		if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
			// associative
			IAST result;
			if ((result = EvalAttributes.flatten(ast)) != null) {
				return result.optional(evalSetOrderless(result, attr, noEvaluation, level));  
			}
		}
		return evalSetOrderless(ast, attr, noEvaluation, level);
	}

	private IAST evalSetAttributeArg(IAST ast, int i, IAST argI, IAST resultList, boolean noEvaluation, int level) {
		IExpr expr;
		expr = evalSetAttributesRecursive(argI, noEvaluation, true, level + 1);
		if (expr != null) {
			if (resultList == null) {
				resultList = ast.setAtClone(i, expr);
			} else {
				resultList.set(i, expr);
			}
		} else {
			expr = argI;
		}
		if (expr.isAST()) {
			if (expr.isAST(F.Sqrt, 2)) {
				if (resultList == null) {
					resultList = ast.setAtClone(i, PowerOp.power(((IAST) expr).arg1(), F.C1D2));
				} else {
					resultList.set(i, PowerOp.power(expr, F.C1D2));
				}
			} else if (expr.isAST(F.Exp, 2)) {
				if (resultList == null) {
					resultList = ast.setAtClone(i, PowerOp.power(F.E, ((IAST) expr).arg1()));
				} else {
					resultList.set(i, PowerOp.power(F.E, ((IAST) expr).arg1()));
				}
			}
		}
		return resultList;
	}

	private IExpr evalSetOrderless(IAST ast, final int attr, boolean noEvaluation, int level) {
		if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
			EvalAttributes.sort(ast);
			if (level > 0 && !noEvaluation && ast.isFreeOfPatterns()) {
				if (ast.isPlus()) {
					return Plus.CONST.evaluate(ast);
				}
				if (ast.isTimes()) {
					return Times.CONST.evaluate(ast);
				}
			}
		}
		if (level > 0 && !noEvaluation && ast.isFreeOfPatterns()) {
			return evaluate(ast);
		}

		return null;
	}

	public IAST evalFlatOrderlessAttributesRecursive(IAST ast) {
		final ISymbol symbol = ast.topHead();
		final int attr = symbol.getAttributes();
		// final Predicate<IExpr> isPattern = Predicates.isPattern();
		IAST resultList = null;

		if ((ISymbol.HOLDALL & attr) != ISymbol.HOLDALL) {
			final int astSize = ast.size();

			if ((ISymbol.HOLDFIRST & attr) == ISymbol.NOATTRIBUTE) {
				// the HoldFirst attribute isn't set here
				if (astSize > 1 && ast.arg1().isAST()) {
					IExpr expr = ast.arg1();
					if (ast.arg1().isAST()) {
						IAST temp = (IAST) ast.arg1();
						expr = evalFlatOrderlessAttributesRecursive(temp);
						if (expr != null) {
							resultList = ast.setAtClone(1, expr);
						} else {
							expr = ast.arg1();
						}
					}
				}
			}
			if ((ISymbol.HOLDREST & attr) == ISymbol.NOATTRIBUTE) {
				// the HoldRest attribute isn't set here
				for (int i = 2; i < astSize; i++) {
					if (ast.get(i).isAST()) {
						IAST temp = (IAST) ast.get(i);
						IExpr expr = evalFlatOrderlessAttributesRecursive(temp);
						if (expr != null) {
							if (resultList == null) {
								resultList = ast.clone();
							}
							resultList.set(i, expr);
						}
					}
				}
			}
		}
		if (resultList != null) {
			if (resultList.size() > 2) {
				if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
					// associative
					IAST result;
					if ((result = EvalAttributes.flatten(resultList)) != null) {
						resultList = result;
						if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
							EvalAttributes.sort(resultList);
						}
						return resultList;
					}
				}
				if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
					EvalAttributes.sort(resultList);
				}
			}
			return resultList;
		}

		if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
			// associative
			IAST result;
			if ((result = EvalAttributes.flatten(ast)) != null) {
				resultList = result;
				if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
					EvalAttributes.sort(ast);
				}
				return resultList;
			}
		}
		if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
			EvalAttributes.sort(ast);
		}
		return ast;
	}

	/**
	 * Evaluate the expression and return the <code>Trace[expr]</code> (i.e. all (sub-)expressions needed to calculate the result).
	 * 
	 * @param expr
	 *            the expression which should be evaluated.
	 * @param matcher
	 *            a filter which determines the expressions which should be traced, If the matcher is set to <code>null</code>, all
	 *            expressions are traced.
	 * @param list
	 *            an IAST object which will be cloned for containing the traced expressions. Typically a <code>F.List()</code> will
	 *            be used.
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
	 * <code>org.matheclipse.parser.client.math.MathException</code> occurs during evaluation, return <code>False</code>.
	 * 
	 * @param expr
	 * @return <code>true</code> if the expression could be evaluated to symbol <code>True</code> and <code>false</code> in all
	 *         other cases
	 */
	public final boolean evalTrue(final IExpr expr) {
		try {
			if (expr.isTrue()) {
				return true;
			}
			if (expr.isFalse()) {
				return false;
			}
			return evaluate(expr).isTrue();
		} catch (MathException fce) {
			if (Config.DEBUG) {
				fce.printStackTrace();
			}
			return false;
		}
	}

	/**
	 * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation reset the numeric mode to the
	 * value stored before the evaluation starts. If evaluation is not possible return the input object.
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
	 * <p>
	 * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation reset the numeric mode to the
	 * value stored before the evaluation starts. If evaluation is not possible return the input object.
	 * </p>
	 * <p>
	 * <b>Note:</b> if this method catches exception <code>org.matheclipse.parser.client.math.MathException</code>, it returns the
	 * input expression.
	 * </p>
	 * 
	 * @param expr
	 *            the object which should be evaluated
	 * @return the evaluated object
	 */
	public final IExpr evalPattern(final IExpr expr) {
		boolean numericMode = fNumericMode;
		try {
			if (expr.isFreeOfPatterns()) {
				return evalWithoutNumericReset(expr);
			}
			if (expr.isAST()) {
				return expr.optional(evalSetAttributes((IAST) expr));
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
	 * Store the current numeric mode and evaluate the expression <code>expr</code>. After evaluation reset the numeric mode to the
	 * value stored before the evaluation starts. If evaluation is not possible return the input object.
	 * </p>
	 * 
	 * @param expr
	 *            the object which should be evaluated
	 * @return an <code>IPatterMatcher</code> cretaed from the given expression.
	 */
	public final IPatternMatcher evalPatternMatcher(final IExpr expr) {
		IExpr temp = evalPattern(expr);
		return new PatternMatcher(temp);
	}

	/**
	 * Parse the given <code>expression String</code> into an IExpr and evaluate it.
	 * 
	 * @param astString
	 *            an expression in math formula notation
	 * @return
	 * @throws org.matheclipse.parser.client.SyntaxError
	 *             if a parsing error occurs
	 */
	final public IExpr evaluate(String expression) {
		return evaluate(parse(expression));
	}

	/**
	 * 
	 * Evaluate an object and reset the numeric mode to the value before the evaluation step. If evaluation is not possible return
	 * <code>null</code>.
	 * 
	 * @param expr
	 *            the object which should be evaluated
	 * @return the evaluated object or <code>null</code> if no evaluation was possible
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
	 * Evaluate an object without resetting the numeric mode after the evaluation step. If evaluation is not possible return the
	 * input object,
	 * 
	 * @param expr
	 *            the object which should be evaluated
	 * @return the evaluated object
	 * 
	 */
	@Override
	public final IExpr evalWithoutNumericReset(final IExpr expr) {
		IExpr temp = evalLoop(expr);
		return temp == null ? expr : temp;
	}

	private IAST flattenSequences(final IAST ast) {
		IAST seqResult = null;
		final int astSize = ast.size();
		for (int i = 1; i < astSize; i++) {
			if (ast.get(i).isSequence()) {
				IAST seq = (IAST) ast.get(i);
				if (seqResult == null) {
					seqResult = ast.copyUntil(i);
				}
				seqResult.addAll(seq, 1, seq.size());
			} else if (seqResult != null) {
				seqResult.add(ast.get(i));
			}
		}
		return seqResult;
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

	public int getIterationLimit() {
		return fIterationLimit;
	}

	final public Map<ISymbol, Stack<IExpr>> getLocalVariableStackMap() {
		if (fLocalVariableStackMap == null) {
			fLocalVariableStackMap = new IdentityHashMap<ISymbol, Stack<IExpr>>();
		}
		return fLocalVariableStackMap;
	}

	/**
	 * Get the list of modified variables
	 * 
	 * @return
	 */
	public Set<ISymbol> getModifiedVariables() {
		return fModifiedVariablesList;
	}

	public LastCalculationsHistory getOutList() {
		return fOutList;
	}

	public PrintStream getOutPrintStream() {
		return fOutPrintStream;
	}

	/**
	 * @return the reapList
	 */
	public IAST getReapList() {
		return reapList;
	}

	/**
	 * @return
	 */
	public int getRecursionLimit() {
		return fRecursionLimit;
	}

	public int getRecursionCounter() {
		return fRecursionCounter;
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
	 * Returns the <code>ISymbol</code> variable created in this thread to which the specified <code>symbolName</code> is mapped, or
	 * <code>null</code> if this map contains no mapping for the <code>symbolName</code>.
	 * 
	 * @param name
	 * @return
	 */
	public ISymbol getUserVariable(final String symbolName) {
		if (fUserVariableMap == null) {
			fUserVariableMap = new HashMap<String, ISymbol>();
		}
		return fUserVariableMap.get(symbolName);
	}

	/**
	 * Associates the <code>symbolName</code> key with the <code>ISymbol</code> value.
	 * 
	 * @param symbolName
	 * @param symbol
	 * @return
	 */
	public ISymbol putUserVariable(final String symbolName, final ISymbol symbol) {
		if (fUserVariableMap == null) {
			fUserVariableMap = new HashMap<String, ISymbol>();
		}
		return fUserVariableMap.put(symbolName, symbol);
	}

	/**
	 * Remove all <code>moduleVariables</code> from this evaluation engine.
	 * 
	 * @param moduleVariables
	 */
	public void removeUserVariables(final Map<ISymbol, ISymbol> moduleVariables) {
		// remove all module variables from eval engine
		if (fUserVariableMap != null) {
			for (ISymbol symbol : moduleVariables.values()) {
				fUserVariableMap.remove(symbol.toString());
			}
		}
	}

	/**
	 * Increment the module counter by 1 and return the result.
	 * 
	 * @return the module counter
	 */
	public int incModuleCounter() {
		return ++fModuleCounter;
	}

	@Override
	final public void init() {
		fRecursionCounter = 0;
		fNumericMode = false;
		fEvalLHSMode = false;
		fTraceMode = false;
		fTraceStack = null;
		// fTraceList = null;
		fStopRequested = false;
		fModifiedVariablesList = new HashSet<ISymbol>();
	}

	/**
	 * The engine evaluates the left-hand-side of a <code>Set, SetDelayed,...</code> expression.
	 * 
	 * @return
	 */
	public boolean isEvalLHSMode() {
		return fEvalLHSMode;
	}

	/**
	 * @return <code>true</code> if the EvalEngine runs in numeric mode.
	 */
	public boolean isNumericMode() {
		return fNumericMode;
	}

	/**
	 * Check if the appending of expressions to the history list for the <code>Out[]</code> function is enabled. If enabled, the
	 * special variable <code>$ans</code> returns the result from the last evluation done with this evaluation engine.
	 * 
	 * @see org.matheclipse.core.reflection.Out
	 */
	public boolean isOutListDisabled() {
		return fOutListDisabled;
	}

	public boolean isPackageMode() {
		return fPackageMode;
	}

	/**
	 * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages are showw in the evaluation).
	 * 
	 * @see org.matheclipse.core.builtin.function.Quiet
	 */
	public boolean isQuietMode() {
		return fQuietMode;
	}

	/**
	 * @return the fRelaxedSyntax
	 */
	public boolean isRelaxedSyntax() {
		return fRelaxedSyntax;
	}

	/**
	 * @return Returns the stopRequested.
	 */
	public boolean isStopRequested() {
		return fStopRequested;
	}

	/**
	 * If the trace mode is set the system writes an evaluation trace list or if additionally the <i>stop after evaluation mode</i>
	 * is set returns the first evaluated result.
	 * 
	 * @return
	 * @see org.matheclipse.core.reflection.system.Trace
	 */
	public boolean isTraceMode() {
		return fTraceMode;
	}

	/**
	 * Parse the given <code>expression String</code> into an IExpr without evaluation.
	 * 
	 * @param astString
	 *            an expression in math formula notation
	 * 
	 * @return
	 * @throws org.matheclipse.parser.client.SyntaxError
	 *             if a parsing error occurs
	 */
	final public IExpr parse(String expression) {
		final Parser parser = new Parser(fRelaxedSyntax);
		final ASTNode node = parser.parse(expression);
		return AST2Expr.CONST_LC.convert(node, this);
	}

	/**
	 * Parse the given <code>expression String</code> into an <code>ASTNode</code> without evaluation.
	 * 
	 * @param astString
	 *            an expression in math formula notation
	 * @return
	 * @throws org.matheclipse.parser.client.SyntaxError
	 *             if a parsing error occurs
	 */
	final public ASTNode parseNode(String expression) {
		if (fRelaxedSyntax) {
			final Parser parser = new Parser(fRelaxedSyntax);
			return parser.parse(expression);
		} else {
			final Parser parser = new Parser();
			return parser.parse(expression);
		}
	}

	/**
	 * Reset the numeric mode flag and the recursion counter
	 * 
	 */
	public void reset() {
		fNumericMode = false;
		fEvalLHSMode = false;
		fRecursionCounter = 0;
	}

	/**
	 * Set the assumptions for this evaluation engine
	 * 
	 * @param assumptions
	 */
	public void setAssumptions(IAssumptions assumptions) {
		this.fAssumptions = assumptions;
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
	 * @param b
	 */
	public void setNumericMode(final boolean b, int precision) {
		fNumericMode = b;
		fNumericPrecision = precision;
	}

	public int getNumericPrecision() {
		return fNumericPrecision;
	}

	/**
	 * Check if the <code>ApfloatNum</code> number type should be used instead of the <code>Num</code> type and the
	 * <code>ApcomplexxNum</code> number type should be used instead of the <code>ComplexNum</code> type for numeric evaluations.
	 * 
	 * @return <code>true</code> if the required precision is greater than <code>EvalEngine.DOUBLE_PRECISION</code>
	 * @see ApfloatNum
	 * @see ApcomplexNum
	 */
	public boolean isApfloat() {
		return fNumericPrecision > EvalEngine.DOUBLE_PRECISION;
	}

	/**
	 * Check if the <code>ApfloatNum</code> number type should be used instead of the <code>Num</code> type and the
	 * <code>ApcomplexxNum</code> number type should be used instead of the <code>ComplexNum</code> type for numeric evaluations.
	 * 
	 * @param precision
	 *            the given precision
	 * @return <code>true</code> if the given precision is greater than <code>EvalEngine.DOUBLE_PRECISION</code>
	 * @see ApfloatNum
	 * @see ApcomplexNum
	 */
	public static boolean isApfloat(int precision) {
		return precision > EvalEngine.DOUBLE_PRECISION;
	}

	public void setNumericPrecision(int precision) {
		fNumericPrecision = precision;
	}

	/**
	 * 
	 * @param outListDisabled
	 *            if <code>false</code> create a <code>LastCalculationsHistory(historyCapacity)</code>, otherwise no history of the
	 *            last calculations will be saved and the <code>Out()</code> function (or % operator) will be unevaluated.
	 * @param historyCapacity
	 *            the number of last entries of the calculations which should be stored.
	 */
	public void setOutListDisabled(boolean outListDisabled, int historyCapacity) {
		if (outListDisabled == false) {
			if (fOutList == null) {
				fOutList = new LastCalculationsHistory(100);
			}
		} else {
			fOutList = null;
		}
		this.fOutListDisabled = outListDisabled;
	}

	public void setOutPrintStream(final PrintStream outPrintStream) {
		fOutPrintStream = outPrintStream;
	}

	public void setPackageMode(boolean packageMode) {
		fPackageMode = packageMode;
	}

	/**
	 * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages are showw in the evaluation).
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
	public void setReapList(IAST reapList) {
		this.reapList = reapList;
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

	/**
	 * @param string
	 */
	public void setSessionID(final String string) {
		fSessionID = string;
	}

	/**
	 * @param stopRequested
	 *            The stopRequested to set.
	 */
	public void setStopRequested(final boolean stopRequested) {
		fStopRequested = stopRequested;
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

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (fUserVariableMap != null) {
			buf.append(fUserVariableMap.toString());
		}
		if (fLocalVariableStackMap != null) {
			buf.append(fLocalVariableStackMap.toString());
		}
		if (SystemNamespace.DEFAULT != null) {
			buf.append(SystemNamespace.DEFAULT.toString());
		}
		return buf.toString();
	}

	/**
	 * Print a message to the <code>Out</code> stream, if the engine is not in &quot;quiet mode&quot;.
	 * 
	 * @param str
	 *            the message which should be printed
	 */
	public void printMessage(String str) {
		if (!isQuietMode()) {
			PrintStream stream = getOutPrintStream();
			if (stream == null) {
				stream = System.out;
			}
			stream.println(str);
		}
	}

}