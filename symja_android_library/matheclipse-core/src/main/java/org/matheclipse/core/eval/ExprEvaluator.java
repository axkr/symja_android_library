package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

/**
 * <p>
 * Evaluate math expressions to <code>IExpr</code> results.
 * </p>
 * 
 * <p>
 * <b>Example 1</b>:
 * </p>
 * 
 * <pre>
 * try {
 * 	// don't distinguish between lower and upper case identifiers
 * 	Config.PARSER_USE_LOWERCASE_SYMBOLS = true;
 * 
 * 	ExprEvaluator util = new ExprEvaluator(false, 100);
 * 
 * 	// Show an expression in the Java form:
 * 	// Note: single character identifiers are case sensistive
 * 	// (the "D()" function input must be written as upper case
 * 	// character)
 * 	String javaForm = util.toJavaForm("D(sin(x)*cos(x),x)");
 * 	// prints: D(Times(Sin(x),Cos(x)),x)
 * 	System.out.println(javaForm.toString());
 * 
 * 	// Use the Java form to create an expression with F.* static
 * 	// methods:
 * 	IAST function = D(Times(Sin(x), Cos(x)), x);
 * 	IExpr result = util.eval(function);
 * 	// print: Cos(x)^2-Sin(x)^2
 * 	System.out.println(result.toString());
 * 
 * 	// Note "diff" is an alias for the "D" function
 * 	result = util.eval("diff(sin(x)*cos(x),x)");
 * 	// print: Cos(x)^2-Sin(x)^2
 * 	System.out.println(result.toString());
 * 
 * 	// evaluate the last result ($ans contains "last answer")
 * 	result = util.eval("$ans+cos(x)^2");
 * 	// print: 2*Cos(x)^2-Sin(x)^2
 * 	System.out.println(result.toString());
 * 
 * 	// evaluate an Integrate[] expression
 * 	result = util.eval("integrate(sin(x)^5,x)");
 * 	// print: 2/3*Cos(x)^3-1/5*Cos(x)^5-Cos(x)
 * 	System.out.println(result.toString());
 * 
 * 	// set the value of a variable "a" to 10
 * 	// Note: in server mode the variable name must have a preceding '$'
 * 	// character
 * 	result = util.eval("a=10");
 * 	// print: 10
 * 	System.out.println(result.toString());
 * 
 * 	// do a calculation with variable "a"
 * 	result = util.eval("a*3+b");
 * 	// print: 30+b
 * 	System.out.println(result.toString());
 * 
 * 	// Do a calculation in "numeric mode" with the N() function
 * 	// Note: single character identifiers are case sensistive
 * 	// (the "N()" function input must be written as upper case
 * 	// character)
 * 	result = util.eval("N(sinh(5))");
 * 	// print: 74.20321057778875
 * 	System.out.println(result.toString());
 * 
 * 	// define a function with a recursive factorial function definition.
 * 	// Note: fac(0) is the stop condition.
 * 	result = util.eval("fac(x_IntegerQ):=x*fac(x-1);fac(0)=1");
 * 	// now calculate factorial of 10:
 * 	result = util.eval("fac(10)");
 * 	// print: 3628800
 * 	System.out.println(result.toString());
 * 
 * } catch (SyntaxError e) {
 * 	// catch Symja parser errors here
 * 	System.out.println(e.getMessage());
 * } catch (MathException me) {
 * 	// catch Symja math errors here
 * 	System.out.println(me.getMessage());
 * } catch (final Exception ex) {
 * 	System.out.println(ex.getMessage());
 * } catch (final StackOverflowError soe) {
 * 	System.out.println(soe.getMessage());
 * } catch (final OutOfMemoryError oome) {
 * 	System.out.println(oome.getMessage());
 * }
 * </pre>
 * 
 * <p>
 * <b>Example 2</b>:
 * </p>
 * 
 * <pre>
 * EvalEngine engine = new EvalEngine(true);
 * ExprEvaluator eval = new ExprEvaluator(engine, true, 20);
 * 
 * String str = "sin(x)";
 * IExpr e = eval.eval(str);
 * int i = 100;
 * eval.defineVariable("x", (double) i);
 * double result = e.evalDouble();
 * assertEquals(-0.5063656411097588, result, 0E-10);
 * </pre>
 */
public class ExprEvaluator {
	static {
		F.initSymbols(null, null, true);
	}

	private Map<ISymbol, IExpr> fVariableMap;
	private final List<ISymbol> fVariables;

	private final EvalEngine engine;

	private IExpr fExpr;

	/**
	 * Constructor for an <code>IExpr</code> object evaluator. By default no output history for the <code>Out()</code>
	 * function is stored in the evaluation engine. <code>$ans</code> won't get evaluate to the last result.
	 * 
	 */
	public ExprEvaluator() {
		this(true, 0);
	}

	/**
	 * Constructor for an <code>IExpr</code> object evaluator.if <code>outListDisabled==false</code> no output history
	 * for the <code>Out()</code> function is stored in the evaluation engine. <code>$ans</code> won't get evaluated to
	 * the last result.
	 * 
	 * @param outListDisabled
	 *            if <code>false</code> create a <code>LastCalculationsHistory(historyCapacity)</code>, otherwise no
	 *            history of the last calculations will be saved and the <code>Out()</code> function (or
	 *            <code>$ans</code> variable or the <code>%</code> operator) will be unevaluated.
	 * @param historyCapacity
	 *            the number of last entries of the calculations which should be stored.
	 */
	public ExprEvaluator(boolean outListDisabled, int historyCapacity) {
		this(new EvalEngine(true), outListDisabled, historyCapacity);
	}

	/**
	 * Constructor for an <code>IExpr</code> object evaluator. By default no output history for the <code>Out()</code>
	 * function is stored in the evaluation engine. <code>$ans</code> won't get evaluate to the last result.
	 * 
	 * @parm engine
	 * @param outListDisabled
	 *            if <code>false</code> create a <code>LastCalculationsHistory(historyCapacity)</code>, otherwise no
	 *            history of the last calculations will be saved and the <code>Out()</code> function (or
	 *            <code>$ans</code> variable or the <code>%</code> operator) will be unevaluated.
	 * @param historyCapacity
	 *            the number of last entries of the calculations which should be stored.
	 */
	public ExprEvaluator(EvalEngine engine, boolean outListDisabled, int historyCapacity) {
		this.fVariableMap = new IdentityHashMap<>();
		this.fVariables = new ArrayList<>();
		this.engine = engine;
		EvalEngine.set(engine);
		if (!outListDisabled) {
			engine.setOutListDisabled(outListDisabled, historyCapacity);
		}
	}

	/**
	 * Clear all <b>local variables</b> defined with the <code>defineVariable()</code> method for this evaluator.
	 * <b>Note:</b> global variables assigned in scripting mode can be cleared with the <code>Clear(variable)</code>
	 * function.
	 * 
	 * @see #defineVariable(ISymbol, IExpr)
	 */
	public void clearVariables() {
		fVariableMap.clear();
		// pop all local variables from local variable stack
		for (int i = 0; i < fVariables.size(); i++) {
			fVariables.get(i).popLocalVariable();
		}
	}

	/**
	 * Define a given variable on the <b>local variable stack</b> without assigning a value.
	 * 
	 * @param variable
	 * @param value
	 */
	public ISymbol defineVariable(ISymbol variable) {
		return defineVariable(variable, null);
	}

	/**
	 * Define a double value for a given variable name on the <b>local variable stack</b> .
	 * 
	 * @param variable
	 * @param value
	 */
	public ISymbol defineVariable(ISymbol variable, double value) {
		return defineVariable(variable, F.num(value));
	}

	/**
	 * Define a value for a given variable name on the <b>local variable stack</b>. The value is evaluated before it's
	 * assigned to the local variable.
	 * 
	 * @param variable
	 * @param value
	 */
	public ISymbol defineVariable(ISymbol variable, IExpr value) {
		variable.pushLocalVariable();
		if (value != null) {
			// this evaluation step may throw an exception
			IExpr temp = engine.evaluate(value);
			variable.set(temp);
		}
		fVariables.add(variable);
		fVariableMap.put(variable, value);
		return variable;
	}

	/**
	 * Define a given variable name on the <b>local variable stack</b> without assigning a value.
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName) {
		return defineVariable(F.symbol(variableName, engine), null);
	}

	/**
	 * Define a boolean value for a given variable name on the< b>local variable stack</b>
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName, boolean value) {
		defineVariable(F.symbol(variableName, engine), value ? F.True : F.False);
	}

	/**
	 * Define a double value for a given variable name on the< b>local variable stack</b>
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName, double value) {
		return defineVariable(F.symbol(variableName, engine), F.num(value));
	}

	/**
	 * Define a value for a given variable name on the< b>local variable stack</b>
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName, IExpr value) {
		return defineVariable(F.symbol(variableName, engine), value);
	}

	/**
	 * 
	 * @return
	 * @deprecated use eval()
	 */
	@Deprecated
	public final IExpr evaluate() {
		return eval();
	}

	/**
	 * 
	 * @return
	 * @deprecated use eval()
	 */
	@Deprecated
	public final IExpr evaluate(final IExpr expr) {
		return eval(expr);
	}

	/**
	 * 
	 * @return
	 * @deprecated use eval()
	 */
	@Deprecated
	public final IExpr evaluate(final String inputExpression) {
		return eval(inputExpression);
	}

	/**
	 * Reevaluate the last <code>expression</code> (possibly after a new variable assignment).
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	public IExpr eval() {
		if (fExpr == null) {
			throw new SyntaxError(0, 0, 0, " ", "No parser input defined", 1);
		}
		return eval(fExpr);
	}

	/**
	 * Evaluate an expression. If evaluation is not possible return the input object.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object
	 */
	public IExpr eval(final IExpr expr) {
		fExpr = expr;
		// F.join();
		EvalEngine.set(engine);
		engine.reset();
		IExpr temp = engine.evaluate(expr);
		if (!engine.isOutListDisabled()) {
			engine.addOut(temp);
		}
		return temp;
	}

	/**
	 * Evaluate an expression and test if the result is <code>F.True</code>.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return <code>true</code> if the result is <code>F.True</code> otherwise return <code>false</code>
	 */
	public boolean isTrue(final IExpr expr) {
		return eval(expr).isTrue();
	}

	/**
	 * Evaluate an expression and test if the result is <code>F.False</code>.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return <code>true</code> if the result is <code>F.False</code> otherwise return <code>false</code>
	 */
	public boolean isFalse(final IExpr expr) {
		return eval(expr).isFalse();
	}

	/**
	 * Parse the given <code>expression String</code> and evaluate it to an IExpr value
	 * 
	 * @param inputExpression
	 * @return
	 * @throws SyntaxError
	 */
	public IExpr eval(final String inputExpression) {
		// try {
		if (inputExpression != null) {
			EvalEngine.set(engine);
			engine.reset();
			fExpr = engine.parse(inputExpression);
			if (fExpr != null) {
				return eval(fExpr);
			}
		}
		// } finally {
		// EvalEngine.remove();
		// }
		return null;
	}

	/**
	 * Parse the given <code>expression String</code> without evaluation
	 * 
	 * @param inputExpression
	 * @return
	 * @throws SyntaxError
	 */
	public IExpr parse(final String inputExpression) {
		// try {
		if (inputExpression != null) {
			EvalEngine.set(engine);
			engine.reset();
			return engine.parse(inputExpression);
		}
		// } finally {
		// EvalEngine.remove();
		// }
		return null;
	}

	/**
	 * <p>
	 * Parse the given <code>expression String</code> and evaluate it to an IExpr value.
	 * </p>
	 * 
	 * @param inputExpression
	 *            the Symja input expression
	 * @param timeoutDuration
	 *            with timeoutUnit, the maximum length of time to wait
	 * @param timeUnit
	 *            with timeoutDuration, the maximum length of time to wait
	 * @param interruptible
	 *            whether to respond to thread interruption by aborting the operation and throwing InterruptedException;
	 *            if false, the operation is allowed to complete or time out, and the current thread's interrupt status
	 *            is re-asserted.
	 * @return
	 * @throws SyntaxError
	 */
	public IExpr evaluateWithTimeout(final String inputExpression, long timeoutDuration, TimeUnit timeUnit,
			boolean interruptible, EvalCallable call) {
		if (inputExpression != null) {
			// F.join();
			EvalEngine.set(engine);
			engine.reset();
			fExpr = engine.parse(inputExpression);
			if (fExpr != null) {
				try {
					F.await();
					TimeLimiter timeLimiter = SimpleTimeLimiter.create(Executors.newSingleThreadExecutor());
					EvalCallable work = call == null ? new EvalCallable(engine) : call;
					work.setExpr(fExpr);
					return timeLimiter.callWithTimeout(work, timeoutDuration, timeUnit);
				} catch (InterruptedException e) {
					return F.$Aborted;
					// } catch (java.util.concurrent.TimeoutException e) {
					// return F.$Aborted;
				} catch (java.util.concurrent.TimeoutException e) {
					Throwable t = e.getCause();
					if (t instanceof RuntimeException) {
						throw (RuntimeException) t;
					}
					return F.$Aborted;
				} catch (com.google.common.util.concurrent.UncheckedTimeoutException e) {
					Throwable t = e.getCause();
					if (t instanceof RuntimeException) {
						throw (RuntimeException) t;
					}
					return F.$Aborted;
				} catch (Exception e) {
					if (Config.SHOW_STACKTRACE) {
						e.printStackTrace();
					}
					return F.Null;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @deprecated use evalf(inputExpression)
	 */
	@Deprecated
	public double evaluateDouble(final String inputExpression) {
		return evalf(inputExpression);
	}

	/**
	 * Parse the given <code>inputExpression</code> String and evaluate it to a <code>double</code> value if possible.
	 * 
	 * @param inputExpression
	 *            an input expression
	 * @return <code>Double.NaN</code> if no <code>double</code> value could be evaluated
	 * @throws SyntaxError
	 */
	public double evalf(final String inputExpression) {
		if (inputExpression != null) {
			EvalEngine.set(engine);
			engine.reset();
			fExpr = engine.parse(inputExpression);
			if (fExpr != null) {
				IExpr temp = eval(F.N(fExpr));
				if (temp.isReal()) {
					return ((ISignedNumber) temp).doubleValue();
				}
			}
		}
		return Double.NaN;
	}

	/**
	 * Evaluate an expression to a double value.
	 * 
	 * @param expr
	 *            a Symja expression
	 * @return <code>Double.NaN</code> if no <code>double</code> value could be evaluated
	 */
	public double evalf(final IExpr expr) {
		EvalEngine.set(engine);
		engine.reset();
		IExpr temp = eval(F.N(expr));
		if (temp.isReal()) {
			return ((ISignedNumber) temp).doubleValue();
		}
		return Double.NaN;
	}

	/**
	 * Get the evaluation engine assigned to this evaluator.
	 * 
	 * @return
	 */
	public EvalEngine getEvalEngine() {
		return engine;
	}

	/**
	 * Returns the expression value to which the specified variableName is mapped, or {@code null} if this map contains
	 * no mapping for the variableName.
	 * 
	 * @param variableName
	 * @return
	 */
	public IExpr getVariable(String variableName) {
		return fVariableMap.get(F.symbol(variableName, engine));
	}

	/**
	 * Converts the <code>inputExpression</code> string into a Java Symja expression string.
	 * 
	 * @param inputExpression
	 *            an input expression
	 */
	public String toJavaForm(final String inputExpression) throws MathException {
		IExpr parsedExpression;
		if (inputExpression != null) {
			ExprParser parser = new ExprParser(engine);
			parsedExpression = parser.parse(inputExpression);
			return parsedExpression.internalFormString(false, 0);
		}
		return "";
	}

	/**
	 * Converts the inputExpression string into a Scala expression and writes the result to the given
	 * <code>Writer</code>string.
	 * 
	 * @param inputExpression
	 *            an input expression
	 */
	public String toScalaForm(final String inputExpression) throws MathException {
		IExpr parsedExpression;
		if (inputExpression != null) {
			ExprParser parser = new ExprParser(engine);
			parsedExpression = parser.parse(inputExpression);
			return parsedExpression.internalScalaString(false, 0);
		}
		return "";
	}
}
