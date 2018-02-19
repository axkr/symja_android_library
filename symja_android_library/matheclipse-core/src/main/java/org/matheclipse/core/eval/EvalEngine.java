package org.matheclipse.core.eval;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.BuiltInSymbol;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IEvaluationEngine;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.math.MathException;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.duy.lambda.Consumer;
import com.duy.lambda.DoubleUnaryOperator;
import com.duy.lambda.Predicate;

import javax.annotation.Nonnull;

/**
 * The main evaluation algorithms for the .Symja computer algebra system
 */
public class EvalEngine implements Serializable, IEvaluationEngine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 407328682800652434L;

	static int fAnonymousCounter = 0;

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
	 * Get the local variable stack for a given symbol. If the local variable stack doesn't exist, create a new one for
	 * the symbol.
	 * 
	 * @param symbol
	 * @return
	 */
	// public static List<IExpr> localStackCreate(final ISymbol symbol) {
	// return get().localStackCreate(symbol);
	// }

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
	 * Associate a symbol name with a local variable stack in this thread.
	 * 
	 */
	transient private HashMap<ISymbol, Deque<IExpr>> fLocalVariableStackMap = null;

	/**
	 * If set to <code>true</code> the current thread should stop evaluation;
	 */
	transient volatile boolean fStopRequested;

	transient int fRecursionCounter;

	/**
	 * Associate a symbol name in this ThreadLocal with the symbol created in this thread
	 * 
	 * @see ExprFactory.fSymbolMap for global symbol names
	 */
	// private Map<String, ISymbol> fUserVariableMap;

	/**
	 * if <code>true</code> the engine evaluates in &quot;numeric&quot; mode, otherwise the engine evaluates in
	 * &quot;symbolic&quot; mode.
	 */
	transient boolean fNumericMode;

	/**
	 * if <code>true</code> the engine evaluates in &quot;F.Together(expr)&quot; in IExpr#times() method.
	 */
	transient boolean fTogetherMode;

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

	transient ContextPath fContextPath;

	protected int fRecursionLimit;

	protected int fIterationLimit;

	protected boolean fPackageMode = F.PACKAGE_MODE;

	transient int fModuleCounter = 0;

	private boolean fRelaxedSyntax;

	/**
	 * List for results in <code>Reap[]</code> function.
	 */
	transient IASTAppendable reapList = null;

	protected Set<ISymbol> fModifiedVariablesList;

	/**
	 * The history list for the <code>Out[]</code> function.
	 * 
	 */
	transient protected LastCalculationsHistory fOutList = null;

	/**
	 * Contains the last result (&quot;answer&quot;) expression of this evaluation engine or <code>null</code> if no
	 * answer is stored in the evaluation engine.
	 */
	transient protected IExpr fAnswer = null;

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

	/**
	 * Constructor for an evaluation engine
	 * 
	 * @param sessionID
	 *            an ID which uniquely identifies this session
	 * @param recursionLimit
	 *            the maximum allowed recursion limit (if set to zero, no limit will be checked)
	 * @param iterationLimit
	 *            the maximum allowed iteration limit (if set to zero, no limit will be checked)
	 * @param out
	 *            the output print stream
	 * @param relaxedSyntax
	 *            if <code>true</code>, the parser doesn't distinguidh between upper and lower case identifiers
	 */
	public EvalEngine(final String sessionID, final int recursionLimit, final int iterationLimit, final PrintStream out,
			boolean relaxedSyntax) {
		fSessionID = sessionID;
		// fExpressionFactory = f;
		fRecursionLimit = recursionLimit;
		fIterationLimit = iterationLimit;
		fOutPrintStream = out;
		fRelaxedSyntax = relaxedSyntax;
		fOutListDisabled = true;
		// fNamespace = fExpressionFactory.getNamespace();

		init();
		// fContextPath = new ContextPath();

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
	 */
	public EvalEngine(final String sessionID, final int recursionLimit, final PrintStream out, boolean relaxedSyntax) {
		this(sessionID, recursionLimit, 1000, out, relaxedSyntax);
	}

	public EvalEngine(final String sessionID, final PrintStream out) {
		this(sessionID, -1, -1, out, false);
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
		ISymbol ans = F.userSymbol("$ans", this);
		ans.putDownRule(ISymbol.RuleType.SET, true, ans, fAnswer, false);
		if (fOutListDisabled) {
			return;
		}
		fOutList.add(fAnswer);
	}

	public void addRules(IAST ruleList) {
		// boolean oldPackageMode = isPackageMode();
		boolean oldTraceMode = isTraceMode();
		try {
			// setPackageMode(true);
			setTraceMode(false);
			final int ruleSize = ruleList.size();
			ruleList.forEach(ruleSize, new Consumer<IExpr>() {
                @Override
                public void accept(IExpr x) {
                    if (x != null) {
                        evaluate(x);
                    }
                }
            });
			// for (int i = 1; i < ruleSize; i++) {
			// if (ruleList.get(i) != null) {
			// evaluate(ruleList.get(i));
			// }
			// }
		} finally

		{
			// setPackageMode(oldPackageMode);
			setTraceMode(oldTraceMode);
		}
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
	public IASTMutable evalArgs(final IAST ast, final int attr) {
		final int astSize = ast.size();
		if (astSize > 1) {
			boolean numericMode = fNumericMode;
			boolean localNumericMode = fNumericMode;

			if (!fNumericMode) {
				if ((ISymbol.NUMERICFUNCTION & attr) == ISymbol.NUMERICFUNCTION) {
					if (ast.hasNumericArgument()) {
						localNumericMode = true;
					}
				}
			}

			IASTMutable resultList = F.NIL;
			IExpr evaledExpr;
			if ((ISymbol.HOLDFIRST & attr) == ISymbol.NOATTRIBUTE) {
				// the HoldFirst attribute isn't set here
				try {
					if ((ISymbol.NHOLDFIRST & attr) == ISymbol.NHOLDFIRST) {
						fNumericMode = false;
					} else {
						fNumericMode = localNumericMode;
					}
					if ((evaledExpr = evalLoop(ast.arg1())).isPresent()) {
						// resultList = ast.setAtClone(1, evaledExpr);
						resultList = ast.copy();
						resultList.set(1, evaledExpr);
						resultList.addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
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
			if (astSize > 2) {
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
							if ((evaledExpr = evalLoop(ast.get(i))).isPresent()) {
								if (!resultList.isPresent()) {
									resultList = ast.copy();
									resultList.addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
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
			}
			return resultList;
		}
		return F.NIL;
	}

	/**
	 * Evaluate an AST. The evaluation steps are controlled by the header attributes.
	 * 
	 * @param ast
	 * @return <code>F.NIL</code> if no evaluation happened
	 */
	public IExpr evalAST(IAST ast) {
		final IExpr head = ast.head();
		if (ast.head().isCoreFunctionSymbol()) {
			// evaluate a core function (without no rule definitions)
			final ICoreFunctionEvaluator coreFunction = (ICoreFunctionEvaluator) ((IBuiltInSymbol) head).getEvaluator();
			return fNumericMode ? coreFunction.numericEval(ast, this) : coreFunction.evaluate(ast, this);
		}
		final ISymbol symbol = ast.topHead();
		IExpr result = evalAttributes(symbol, ast);
		if (result.isPresent()) {
			return result;
		}
		// System.out.println(ast.toString());
		return evalRules(symbol, ast);
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

		// if ((ISymbol.ONEIDENTITY & attr) == ISymbol.ONEIDENTITY) {
		// if (ast.head().isSymbol()) {
		// return ast.arg1();
		// }
		// }

		if ((ISymbol.FLAT & attr) == ISymbol.FLAT) {
			final IExpr arg1 = ast.arg1();
			if (arg1.topHead().equals(symbol)) {
				// associative
				return arg1;
			}
		}

		if ((result = evalArgs(ast, attr)).isPresent()) {
			return result;
		}

		if ((ISymbol.LISTABLE & attr) == ISymbol.LISTABLE) {
			final IExpr arg1 = ast.arg1();
			if (arg1.isRealVector() && ((IAST) arg1).size() > 1) {
				if (symbol.isBuiltInSymbol()) {
					final IEvaluator module = ((IBuiltInSymbol) symbol).getEvaluator();
					if (module instanceof DoubleUnaryOperator) {
						DoubleUnaryOperator oper = (DoubleUnaryOperator) module;
						return ASTRealVector.map((IAST) arg1, oper);
					}
				}
			} else if (arg1.isRealMatrix()) {
				if (symbol.isBuiltInSymbol()) {
					final IEvaluator module = ((IBuiltInSymbol) symbol).getEvaluator();
					if (module instanceof DoubleUnaryOperator) {
						DoubleUnaryOperator oper = (DoubleUnaryOperator) module;
						return ASTRealMatrix.map((IAST) arg1, oper);
					}
				}
			}
			if (arg1.isList()) {
				// thread over the list
				return EvalAttributes.threadList(ast, F.List, ast.head(), ((IAST) arg1).size() - 1);
			}
		}

		if ((ISymbol.NUMERICFUNCTION & attr) == ISymbol.NUMERICFUNCTION) {
			if (ast.arg1().isIndeterminate()) {
				return F.Indeterminate;
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
			if ((result = lhsSymbol.evalUpRule(this, ast)).isPresent()) {
				return result;
			}
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
				IExpr result;
				if (fNumericMode) {
					result = ((IFunctionEvaluator) module).numericEval(ast, this);
				} else {
					result = ((IFunctionEvaluator) module).evaluate(ast, this);
				}
				// if (result == null) {
				// System.out.println(ast);
				// throw new NullPointerException();
				// }
				if (result != null && result.isPresent()) {
					return result;
				}
				if (((ISymbol.DELAYED_RULE_EVALUATION & attr) == ISymbol.DELAYED_RULE_EVALUATION)) {
					if ((result = symbol.evalDownRule(this, ast)).isPresent()) {
						return result;
					}
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Evaluate an AST according to the attributes set in the header symbol. The evaluation steps are controlled by the
	 * header attributes.
	 * 
	 * @param symbol
	 *            the header symbol
	 * @param ast
	 *            the AST which should be evaluated
	 * @return
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
					if (tempAST.exists(new Predicate<IExpr>() {
						@Override
						public boolean test(IExpr x) {
							return x.isIndeterminate();
						}
					}, 1)) {
						return F.Indeterminate;
					}
					// for (int i = 1; i < tempAST.size(); i++) {
					// if (tempAST.get(i).isIndeterminate()) {
					// return F.Indeterminate;
					// }
					// }
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

	/**
	 * Evaluate an expression for the given &quot;local variables list&quot;. If evaluation is not possible return the
	 * input object.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @param localVariablesList
	 *            a list of symbols which should be used as local variables inside the block
	 * @return the evaluated object
	 */
	public IExpr evalBlock(final IExpr expr, final IAST localVariablesList) {
		final List<ISymbol> variables = new ArrayList<ISymbol>();

		try {
			// remember which local variables we use:
			ISymbol blockVariableSymbol;

			for (int i = 1; i < localVariablesList.size(); i++) {
				if (localVariablesList.get(i).isSymbol()) {
					blockVariableSymbol = (ISymbol) localVariablesList.get(i);
					// blockVariableSymbol.pushLocalVariable();
					localStackCreate(blockVariableSymbol).push(F.NIL);
					variables.add(blockVariableSymbol);
				} else {
					if (localVariablesList.get(i).isAST(F.Set, 3)) {
						// lhs = rhs
						final IAST setFun = (IAST) localVariablesList.get(i);
						if (setFun.arg1().isSymbol()) {
							blockVariableSymbol = (ISymbol) setFun.arg1();
							// blockVariableSymbol.pushLocalVariable();
							final Deque<IExpr> localVariableStack = localStackCreate(blockVariableSymbol);
							localVariableStack.push(F.NIL);
							// this evaluation step may throw an exception
							IExpr temp = evaluate(setFun.arg2());
							// blockVariableSymbol.set(temp);
							localVariableStack.remove();
							localVariableStack.push(temp);
							variables.add(blockVariableSymbol);
						}
					} else {
						return expr;
					}
				}
			}

			return evaluate(expr);
		} finally {
			// pop all local variables from local variable stack
			Consumer<ISymbol> consumer = new Consumer<ISymbol>() {
				@Override
				public void accept(ISymbol x) {
					EvalEngine.this.localStack(x).pop();
				}
			};
			for (ISymbol variable : variables) {
				consumer.accept(variable);
			}
		}
	}

	/**
	 * Evaluate an expression for a local variable.
	 * 
	 * 
	 * @param expr
	 *            the expression which should be evaluated for the given symbol
	 * @param symbol
	 *            the symbol which should be evaluated as a local variable
	 * @param localValue
	 *            the value
	 */
	public IExpr evalBlock(IExpr expr, ISymbol symbol, IExpr localValue) {
		Deque<IExpr> stack = localStackCreate(symbol);
		try {
			stack.push(localValue);
			return evaluate(expr);
		} finally {
			stack.pop();
		}
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
					fTraceStack.add(expr, temp, fRecursionCounter, 0L, "Evaluation loop");
					result = temp;
					long iterationCounter = 1;
					while (true) {
						temp = result.evaluate(this);
						if (temp.isPresent()) {
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
				return evalSetAttributes((IAST) expr).orElse(expr);
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
	 * @param expr
	 *            the object which should be evaluated
	 * @return an <code>IPatterMatcher</code> cretaed from the given expression.
	 */
	public final IPatternMatcher evalPatternMatcher(@Nonnull final IExpr expr) {
		IExpr temp = evalPattern(expr);
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
	 * @param ast
	 * @return <code>F.NIL</code> if no evaluation happened
	 */
	public IExpr evalRules(ISymbol symbol, IAST ast) {
		if (symbol instanceof BuiltInSymbol) {
			((BuiltInSymbol) symbol).getEvaluator().join();
		}
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
				if ((result = lhsSymbol.evalUpRule(this, ast)).isPresent()) {
					return result;
				}
			}
		}

		return evalASTBuiltinFunction(symbol, ast);
	}

	private IASTMutable evalSetAttributeArg(IAST ast, int i, IAST argI, IASTMutable resultList, boolean noEvaluation,
			int level) {
		IExpr expr = evalSetAttributesRecursive(argI, noEvaluation, true, level + 1);
		if (expr.isPresent()) {
			if (resultList.isPresent()) {
				resultList.set(i, expr);
			} else {
				resultList = ast.setAtCopy(i, expr);
			}
		} else {
			expr = argI;
		}
		if (expr.isAST()) {
			if (expr.isAST(F.Sqrt, 2)) {
				if (resultList.isPresent()) {
					resultList.set(i, PowerOp.power(expr, F.C1D2));
				} else {
					resultList = ast.setAtCopy(i, PowerOp.power(((IAST) expr).arg1(), F.C1D2));
				}
			} else if (expr.isAST(F.Exp, 2)) {
				if (resultList.isPresent()) {
					resultList.set(i, PowerOp.power(F.E, ((IAST) expr).arg1()));
				} else {
					resultList = ast.setAtCopy(i, PowerOp.power(F.E, ((IAST) expr).arg1()));
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
	 */
	public IExpr evalSetAttributes(IAST ast) {
		return evalSetAttributes(ast, false);
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
	 */
	public IExpr evalSetAttributes(IAST ast, boolean noEvaluation) {
		boolean evalLHSMode = fEvalLHSMode;
		try {
			fEvalLHSMode = true;
			if ((ast.getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) != 0x0000) {
				// already flattened or sorted
				return ast;
			}
			return evalSetAttributesRecursive(ast, noEvaluation, false, 0);
		} finally {
			fEvalLHSMode = evalLHSMode;
		}
	}

	private IExpr evalSetAttributesRecursive(IAST ast, boolean noEvaluation, boolean evalNumericFunction, int level) {
		if (ast.isAST(F.Literal, 2)) {
			return ast.arg1();
		}
		final ISymbol symbol = ast.topHead();
		if (symbol.isBuiltInSymbol()) {
			// call so that attributes may be set in
			// AbstractFunctionEvaluator#setUp() method
			((IBuiltInSymbol) symbol).getEvaluator();
		}
		if (ast.isAST(F.Optional, 3)) {
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
				IAST f = resultList.isPresent() ? resultList : ast;
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
			if (Config.DEBUG) {
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
	@Override
	public final IExpr evalWithoutNumericReset(final IExpr expr) {
		IExpr temp = evalLoop(expr);
		return temp.isPresent() ? temp : expr;
	}

	private IAST flattenSequences(final IAST ast) {
		IASTAppendable seqResult = F.NIL;
		final int astSize = ast.size();
		for (int i = 1; i < astSize; i++) {
			if (ast.get(i).isSequence()) {
				IAST seq = (IAST) ast.get(i);
				if (!seqResult.isPresent()) {
					seqResult = F.ast(ast.head(), astSize + seq.size(), false);
					seqResult.appendArgs(ast, i);
				}
				seqResult.appendArgs(seq);
			} else if (seqResult.isPresent()) {
				seqResult.append(ast.get(i));
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

	public Context getContext() {
		return Context.SYSTEM;
	}

	public ContextPath getContextPath() {
		return fContextPath;
	}

	public int getIterationLimit() {
		return fIterationLimit;
	}

	final public Map<ISymbol, Deque<IExpr>> getLocalVariableStackMap() {
		if (fLocalVariableStackMap == null) {
			fLocalVariableStackMap = new HashMap<ISymbol, Deque<IExpr>>();
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
	 * @return the reapList
	 */
	public IASTAppendable getReapList() {
		return reapList;
	}

	public int getRecursionCounter() {
		return fRecursionCounter;
	}
	
	public int incRecursionCounter() {
		return ++fRecursionCounter;
	}
	
	public int decRecursionCounter() {
		return --fRecursionCounter;
	}

	/**
	 * @return
	 */
	public int getRecursionLimit() {
		return fRecursionLimit;
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

	@Override
	final public void init() {
		fRecursionCounter = 0;
		fNumericMode = false;
		fTogetherMode = false;
		fEvalLHSMode = false;
		fTraceMode = false;
		fTraceStack = null;
		// fTraceList = null;
		fStopRequested = false;
		fModifiedVariablesList = new HashSet<ISymbol>();
		fContextPath = new ContextPath();
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
	public boolean isApfloat() {
		return fNumericPrecision > Config.MACHINE_PRECISION;
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
	 * Check if the appending of expressions to the history list for the <code>Out[]</code> function is enabled. If
	 * enabled, the special variable <code>$ans</code> returns the result from the last evluation done with this
	 * evaluation engine.
	 * 
	 * @return
	 */
	public boolean isOutListDisabled() {
		return fOutListDisabled;
	}

	public boolean isPackageMode() {
		return fPackageMode;
	}

	/**
	 * If <code>true</code> the engine evaluates in &quot;quiet&quot; mode (i.e. no warning messages are showw in the
	 * evaluation).
	 * 
	 * @return
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

	public boolean isTogetherMode() {
		return fTogetherMode;
	}

	/**
	 * If the trace mode is set the system writes an evaluation trace list or if additionally the <i>stop after
	 * evaluation mode</i> is set returns the first evaluated result.
	 * 
	 * @return
	 */
	public boolean isTraceMode() {
		return fTraceMode;
	}

	/**
	 * Get the local variable stack for a given symbol. If the local variable stack doesn't exist, return
	 * <code>null</code>
	 * 
	 * @param symbol
	 * @return <code>null</code> if the stack doesn't exist
	 */
	final public Deque<IExpr> localStack(final ISymbol symbol) {
		return getLocalVariableStackMap().get(symbol);
	}

	/**
	 * Get the local variable stack for a given symbol. If the local variable stack doesn't exist, create a new one for
	 * the symbol.
	 * 
	 * @param symbol
	 * @return
	 */
	public Deque<IExpr> localStackCreate(final ISymbol symbol) {
		Map<ISymbol, Deque<IExpr>> localVariableStackMap = getLocalVariableStackMap();
		Deque<IExpr> temp = localVariableStackMap.get(symbol);
		if (temp != null) {
			return temp;
		}
		temp = new ArrayDeque<IExpr>();// new ArrayList<IExpr>();
		localVariableStackMap.put(symbol, temp);
		return temp;
	}

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
		final ExprParser parser = new ExprParser(this, fRelaxedSyntax);
		return parser.parse(expression);
		// final Parser parser = new Parser(fRelaxedSyntax);
		// final ASTNode node = parser.parse(expression);
		// if (fRelaxedSyntax) {
		// return AST2Expr.CONST_LC.convert(node, this);
		// }
		// return AST2Expr.CONST.convert(node, this);
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

	public void setContextPath(ContextPath fContextPath) {
		this.fContextPath = fContextPath;
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

	public void setOutListDisabled(LastCalculationsHistory outList) {
		this.fOutList = outList;
		this.fOutListDisabled = false;
	}

	public void setOutPrintStream(final PrintStream outPrintStream) {
		fOutPrintStream = outPrintStream;
	}

	public void setPackageMode(boolean packageMode) {
		if (!packageMode) {
			F.PACKAGE_MODE = false;
		}
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
	public void setReapList(IASTAppendable reapList) {
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

	public IASTMutable threadASTListArgs(final IASTMutable ast) {

		int listLength = 0;
		final int astSize = ast.size();
		for (int i = 1; i < astSize; i++) {
			if (ast.get(i).isList()) {
				if (listLength == 0) {
					listLength = ((IAST) ast.get(i)).size() - 1;
				} else {
					if (listLength != ((IAST) ast.get(i)).size() - 1) {
						printMessage("Lists of unequal length cannot be combined: " + ast.toString());
						ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
						return F.NIL;
					}
				}
			}
		}
		if (listLength != 0) {
			IASTAppendable result = EvalAttributes.threadList(ast, F.List, ast.head(), listLength);
			result.addEvalFlags(IAST.IS_LISTABLE_THREADED);
			return result;
		}
		ast.addEvalFlags(IAST.IS_LISTABLE_THREADED);
		return F.NIL;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		if (fLocalVariableStackMap != null) {
			buf.append(fLocalVariableStackMap.toString());
		}
		return buf.toString();
	}

}