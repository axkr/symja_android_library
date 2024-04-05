package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.NotThreadSafe;
import org.apfloat.ApfloatInterruptedException;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.SyntaxError;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

/**
 * Evaluate math expressions to <code>IExpr</code> results.
 *
 * <p>
 * <b>Example 1</b>:
 *
 * <pre>
 * try {
 *   // don't distinguish between lower and upper case identifiers
 *   Config.PARSER_USE_LOWERCASE_SYMBOLS = true;
 *
 *   ExprEvaluator util = new ExprEvaluator(false, 100);
 *
 *   // Show an expression in the Java form:
 *   // Note: single character identifiers are case sensistive
 *   // (the "D()" function input must be written as upper case
 *   // character)
 *   String javaForm = util.toJavaForm("D(sin(x)*cos(x),x)");
 *   // prints: D(Times(Sin(x),Cos(x)),x)
 *   System.out.println(javaForm.toString());
 *
 *   // Use the Java form to create an expression with F.* static
 *   // methods:
 *   IAST function = D(Times(Sin(x), Cos(x)), x);
 *   IExpr result = util.eval(function);
 *   // print: Cos(x)^2-Sin(x)^2
 *   System.out.println(result.toString());
 *
 *   // Note "diff" is an alias for the "D" function
 *   result = util.eval("diff(sin(x)*cos(x),x)");
 *   // print: Cos(x)^2-Sin(x)^2
 *   System.out.println(result.toString());
 *
 *   // evaluate the last result ($ans contains "last answer")
 *   result = util.eval("$ans+cos(x)^2");
 *   // print: 2*Cos(x)^2-Sin(x)^2
 *   System.out.println(result.toString());
 *
 *   // evaluate an Integrate[] expression
 *   result = util.eval("integrate(sin(x)^5,x)");
 *   // print: 2/3*Cos(x)^3-1/5*Cos(x)^5-Cos(x)
 *   System.out.println(result.toString());
 *
 *   // set the value of a variable "a" to 10
 *   // Note: in server mode the variable name must have a preceding '$'
 *   // character
 *   result = util.eval("a=10");
 *   // print: 10
 *   System.out.println(result.toString());
 *
 *   // do a calculation with variable "a"
 *   result = util.eval("a*3+b");
 *   // print: 30+b
 *   System.out.println(result.toString());
 *
 *   // Do a calculation in "numeric mode" with the N() function
 *   // Note: single character identifiers are case sensistive
 *   // (the "N()" function input must be written as upper case
 *   // character)
 *   result = util.eval("N(sinh(5))");
 *   // print: 74.20321057778875
 *   System.out.println(result.toString());
 *
 *   // define a function with a recursive factorial function definition.
 *   // Note: fac(0) is the stop condition.
 *   result = util.eval("fac(x_Integer):=x*fac(x-1);fac(0)=1");
 *   // now calculate factorial of 10:
 *   result = util.eval("fac(10)");
 *   // print: 3628800
 *   System.out.println(result.toString());
 *
 * } catch (SyntaxError e) {
 *   // catch Symja parser errors here
 *   System.out.println(e.getMessage());
 * } catch (MathException me) {
 *   // catch Symja math errors here
 *   System.out.println(me.getMessage());
 * } catch (final Exception ex) {
 *   System.out.println(ex.getMessage());
 * } catch (final StackOverflowError soe) {
 *   System.out.println(soe.getMessage());
 * } catch (final OutOfMemoryError oome) {
 *   System.out.println(oome.getMessage());
 * }
 * </pre>
 *
 * <p>
 * <b>Example 2</b>:
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
@NotThreadSafe
public class ExprEvaluator {

  static {
    F.initSymbols();
  }

  private Map<ISymbol, IExpr> fVariableMap;
  private final List<ISymbol> fVariables;

  // Quit() function may set a new engine,so "final" is not possible here
  private EvalEngine fEngine;

  private IExpr fExpr;

  /**
   * Constructor for an <code>IExpr</code> object evaluator. By default no output history for the
   * <code>Out()</code> function is stored in the evaluation engine. <code>$ans</code> won't get
   * evaluate to the last result.
   */
  public ExprEvaluator() {
    this(true, (short) 0);
  }

  /**
   * Constructor for an <code>IExpr</code> object evaluator.if <code>outListDisabled==false</code>
   * no output history for the <code>Out()</code> function is stored in the evaluation engine.
   * <code>$ans</code> won't get evaluated to the last result.
   *
   * @param outListDisabled if <code>false</code> create a <code>
   *     LastCalculationsHistory(historyCapacity)</code>, otherwise no history of the last
   *        calculations will be saved and the <code>Out()</code> function (or <code>$ans</code>
   *        variable or the <code>%</code> operator) will be unevaluated.
   * @param historyCapacity the number of last entries of the calculations which should be stored.
   */
  public ExprEvaluator(boolean outListDisabled, short historyCapacity) {
    this(new EvalEngine(true), outListDisabled, historyCapacity);
  }

  /**
   * Constructor for an <code>IExpr</code> object evaluator. By default no output history for the
   * <code>Out()</code> function is stored in the evaluation engine. <code>$ans</code> won't get
   * evaluate to the last result.
   *
   * @parm engine
   * @param outListDisabled if <code>false</code> create a <code>
   *     LastCalculationsHistory(historyCapacity)</code>, otherwise no history of the last
   *        calculations will be saved and the <code>Out()</code> function (or <code>$ans</code>
   *        variable or the <code>%</code> operator) will be unevaluated.
   * @param historyCapacity the number of last entries of the calculations which should be stored.
   */
  public ExprEvaluator(EvalEngine engine, boolean outListDisabled, short historyCapacity) {
    this.fVariableMap = new IdentityHashMap<>();
    this.fVariables = new ArrayList<>();
    this.fEngine = engine;
    EvalEngine.set(engine);
    if (!outListDisabled) {
      engine.setOutListDisabled(outListDisabled, historyCapacity);
    }
  }

  /**
   * Clear all <b>local variables</b> defined with the <code>defineVariable()</code> method for this
   * evaluator. <b>Note:</b> global variables assigned in scripting mode can be cleared with the
   * <code>Clear(variable)</code> function.
   *
   * @see #defineVariable(ISymbol, IExpr)
   */
  public void clearVariables() {
    fVariableMap.clear();
    // pop all local variables from local variable stack
    for (int i = 0; i < fVariables.size(); i++) {
      fVariables.get(i).assignValue(null, false);
    }
  }

  /**
   * Define a given variable on the <b>local variable stack</b> without assigning a value.
   *
   * @param variable
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
   * Define a value for a given variable name on the <b>local variable stack</b>. The value is
   * evaluated before it's assigned to the local variable.
   *
   * @param variable
   * @param value
   */
  public ISymbol defineVariable(ISymbol variable, IExpr value) {
    if (value != null) {
      // this evaluation step may throw an exception
      IExpr temp = fEngine.evaluate(value);
      variable.assignValue(temp, false);
    }
    fVariables.add(variable);
    fVariableMap.put(variable, value);
    return variable;
  }

  /**
   * Define a given variable name on the <b>local variable stack</b> without assigning a value.
   *
   * @param variableName
   */
  public ISymbol defineVariable(String variableName) {
    return defineVariable(F.symbol(variableName, fEngine), null);
  }

  /**
   * Define a boolean value for a given variable name on the< b>local variable stack</b>
   *
   * @param variableName
   * @param value
   */
  public void defineVariable(String variableName, boolean value) {
    defineVariable(F.symbol(variableName, fEngine), value ? S.True : S.False);
  }

  /**
   * Define a double value for a given variable name on the< b>local variable stack</b>
   *
   * @param variableName
   * @param value
   */
  public ISymbol defineVariable(String variableName, double value) {
    return defineVariable(F.symbol(variableName, fEngine), F.num(value));
  }

  /**
   * Define a value for a given variable name on the< b>local variable stack</b>
   *
   * @param variableName
   * @param value
   */
  public ISymbol defineVariable(String variableName, IExpr value) {
    return defineVariable(F.symbol(variableName, fEngine), value);
  }

  /**
   * @return
   * @deprecated use eval()
   */
  @Deprecated
  public final IExpr evaluate() {
    return eval();
  }

  /**
   * @return
   * @deprecated use eval()
   */
  @Deprecated
  public final IExpr evaluate(final IExpr expr) {
    return eval(expr);
  }

  /**
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
   * @param expr the expression which should be evaluated
   * @return the evaluated object
   */
  public IExpr eval(final IExpr expr) {
    fExpr = expr;
    EvalEngine[] engineRef = new EvalEngine[] {fEngine};
    IExpr result = evalTopLevel(expr, engineRef);
    fEngine = engineRef[0];
    return result;
  }

  /**
   * Evaluate an expression. If evaluation is not possible return the input object.
   *
   * @param expr
   * @param engineRef
   * @return
   */
  public static IExpr evalTopLevel(final IExpr expr, EvalEngine[] engineRef) {
    // F.join();
    try {
      return evalTryCatch(expr, engineRef);
    } catch (final AbortException e) {
      return S.$Aborted;
    } catch (final FailedException e) {
      return S.$Failed;
    } catch (final SyntaxError e) { // catches parser errors
      // LOGGER.debug("syntax error", e);
      return F.stringx(e.getMessage());
    } catch (SymjaMathException sma) {
      // sma.printStackTrace();
      // LOGGER.debug("ExprEvaluator.evalTopLevel() failed", sma);
      Errors.printMessage(expr.topHead(), sma, engineRef[0]);
      return expr;
    } finally {
      // Quit may set a new engine
      engineRef[0] = EvalEngine.get();
    }
  }

  public static IExpr evalTryCatch(final IExpr expr, EvalEngine[] engineRef) {
    EvalEngine engine = engineRef[0];
    EvalEngine.set(engine);
    // engine.reset() must be done before parsing step
    IExpr preRead = S.$PreRead.assignedValue();
    IExpr temp;
    try {
      if (preRead != null && preRead.isPresent()) {
        temp = engine.evaluate(F.unaryAST1(preRead, expr));
      } else {
        temp = engine.evaluate(expr);
      }
    } catch (ReturnException rex) {
      // LOGGER.debug("ExprEvaluator.evalTryCatch() failed", rex);
      return rex.getValue();
    } catch (BreakException | ContinueException conex) {
      // LOGGER.debug("ExprEvaluator.evalTryCatch() failed", conex);
      IAST ast = F.Continue();
      if (conex instanceof BreakException) {
        ast = F.Break();
      }
      // No enclosing For, While or Do found for `1`.
      Errors.printMessage(S.Continue, "nofwd", F.list(ast), engine);
      temp = F.Hold(ast);
    } catch (ThrowException e) {
      // LOGGER.debug("ExprEvaluator.evalTryCatch() failed", e);
      // Uncaught `1` returned to top level.
      IAST ast = F.Throw(e.getValue());
      Errors.printMessage(S.Throw, "nocatch", F.list(ast), engine);
      temp = F.Hold(ast);
    } catch (IterationLimitExceeded e) {
      // LOGGER.debug("ExprEvaluator.evalTryCatch() failed", e);
      // Iteration limit of `1` exceeded.
      int iterationLimit = engine.getIterationLimit();
      Errors.printMessage(S.$IterationLimit, "itlim",
          F.list(iterationLimit < 0 ? F.CInfinity : F.ZZ(iterationLimit), expr), engine);
      temp = F.Hold(expr);
    } catch (RecursionLimitExceeded e) {
      // LOGGER.debug("ExprEvaluator.evalTryCatch() failed", e);
      // Recursion depth of `1` exceeded during evaluation of `2`.
      int recursionLimit = engine.getRecursionLimit();
      Errors.printMessage(S.$RecursionLimit, "reclim2",
          F.list(recursionLimit < 0 ? F.CInfinity : F.ZZ(recursionLimit), expr), engine);
      temp = F.Hold(expr);
    }
    if (!engine.isOutListDisabled()) {
      engine.addInOut(expr, temp);
    }
    return temp;
  }

  /**
   * Evaluate an expression and test if the result is <code>S.True</code>.
   *
   * @param expr the expression which should be evaluated
   * @return <code>true</code> if the result is <code>S.True</code> otherwise return <code>false
   *     </code>
   */
  public boolean isTrue(final IExpr expr) {
    return eval(expr).isTrue();
  }

  /**
   * Evaluate an expression and test if the result is <code>S.False</code>.
   *
   * @param expr the expression which should be evaluated
   * @return <code>true</code> if the result is <code>S.False</code> otherwise return <code>false
   *     </code>
   */
  public boolean isFalse(final IExpr expr) {
    return eval(expr).isFalse();
  }

  /**
   * Parse the given <code>expression String</code> and evaluate it to an IExpr value. The current
   * threads associated EvalEngine's internal states will be reset before parsing and evaluation.
   *
   * @param inputExpression
   * @return
   * @throws SyntaxError
   */
  public IExpr eval(final String inputExpression) {
    if (inputExpression != null) {
      EvalEngine.setReset(fEngine);
      fExpr = fEngine.parse(inputExpression);
      if (fExpr != null) {
        return eval(fExpr);
      }
      // call EvalEngine.remove() at the end of the thread if necessary
    }

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
      EvalEngine.setReset(fEngine);
      return fEngine.parse(inputExpression);
    }
    // } finally {
    // EvalEngine.remove();
    // }
    return null;
  }

  /**
   * Parse the given <code>expression String</code> and evaluate it to an IExpr value.
   *
   * @param inputExpression the Symja input expression
   * @param timeoutDuration with timeoutUnit, the maximum length of time to wait
   * @param timeUnit with timeoutDuration, the maximum length of time to wait
   * @param interruptible whether to respond to thread interruption by aborting the operation and
   *        throwing InterruptedException; if false, the operation is allowed to complete or time
   *        out, and the current thread's interrupt status is re-asserted.
   * @return
   * @throws SyntaxError
   */
  public IExpr evaluateWithTimeout(final String inputExpression, long timeoutDuration,
      TimeUnit timeUnit, boolean interruptible, EvalControlledCallable call) {
    if (inputExpression != null) {
      // F.join();
      EvalEngine.setReset(fEngine);
      try {
        fExpr = fEngine.parse(inputExpression);
        if (fExpr != null) {
          final ExecutorService executorService = Executors.newSingleThreadExecutor();
          EvalControlledCallable work = call == null ? new EvalControlledCallable(fEngine) : call;
          work.setExpr(fExpr);
          try {
            F.await();
            TimeLimiter timeLimiter = SimpleTimeLimiter.create(executorService);
            return timeLimiter.callWithTimeout(work, timeoutDuration, timeUnit);
          } catch (ApfloatInterruptedException
              | org.matheclipse.core.eval.exception.TimeoutException
              | java.util.concurrent.TimeoutException
              | com.google.common.util.concurrent.UncheckedTimeoutException e) {
            // LOGGER.debug("ExprEvaluator.evaluateWithTimeout() failed", e);
            return S.$Aborted;
          } catch (Exception e) {
            // LOGGER.debug("ExprEvaluator.evaluateWithTimeout() failed", e);
            return S.Null;
          } finally {
            work.cancel();
            MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.SECONDS);
          }
        }
      } finally {
        EvalEngine.remove();
      }
    }
    return null;
  }

  /** @deprecated use evalf(inputExpression) */
  @Deprecated
  public double evaluateDouble(final String inputExpression) {
    return evalf(inputExpression);
  }

  /**
   * Parse the given <code>inputExpression</code> String and evaluate it to a <code>double</code>
   * value if possible.
   *
   * @param inputExpression an input expression
   * @return <code>Double.NaN</code> if no <code>double</code> value could be evaluated
   * @throws SyntaxError
   */
  public double evalf(final String inputExpression) {
    if (inputExpression != null) {
      EvalEngine.setReset(fEngine);
      fExpr = fEngine.parse(inputExpression);
      if (fExpr != null) {
        IExpr temp = eval(F.N(fExpr));
        if (temp.isReal()) {
          return ((IReal) temp).doubleValue();
        }
      }
    }
    return Double.NaN;
  }

  /**
   * Evaluate a function with header <code>head</code> and arguments <code>args</code> by parsing
   * the string <code>args</code> to <code>IExpr</code> and applying the head on these arguments.
   * I.e. <code> head [ args[0], args[1], ...] </code>
   *
   * <pre>
   * IAST head = F.Function(F.Divide(F.Gamma(F.Plus(F.C1, F.Slot1)), F.Gamma(F.Plus(F.C1, F.Slot2))));
   * // eval function ( Gamma(1+#1)/Gamma(1+#2) ) & [23,20]
   * IExpr result = util.evalFunction(head, "23", "20");
   * System.out.println("Out[1]: " + result.toString());
   * </pre>
   *
   * @param head the head of the function.
   * @param args the arguments given as parsable strings
   * @return <code>F.NIL</code> if the evaluation wasn't possible
   */
  public IExpr evalFunction(IExpr head, String... args) {

    try {
      if (args != null) {
        IExpr[] exprArgs = new IExpr[args.length];

        for (int i = 0; i < args.length; i++) {
          exprArgs[i] = eval(args[i]);
        }
        IAST function = F.ast(exprArgs, head);

        return eval(function);
      }
    } catch (RuntimeException rex) {

    }
    return F.NIL;
  }

  /**
   * Evaluate an expression to a double value.
   *
   * @param expr a Symja expression
   * @return {@link Double#NaN} if no <code>double</code> value could be evaluated
   */
  public double evalf(final IExpr expr) {
    EvalEngine.setReset(fEngine);
    IExpr temp = eval(F.N(expr));
    if (temp.isReal()) {
      return ((IReal) temp).doubleValue();
    }
    return Double.NaN;
  }

  /**
   * Evaluate an expression to a {@link Complex} value.
   *
   * @param expr a Symja expression
   * @return {@link Complex#NaN} if no <code>double</code> value could be evaluated
   */
  public Complex evalComplex(final IExpr expr) {
    EvalEngine.setReset(fEngine);
    IExpr temp = eval(F.N(expr));
    if (temp.isNumber()) {
      return temp.evalfc();
    }
    return Complex.NaN;
  }

  public Complex evalComplex(final String inputExpression) {
    if (inputExpression != null) {
      EvalEngine.setReset(fEngine);
      fExpr = fEngine.parse(inputExpression);
      if (fExpr != null) {
        IExpr temp = eval(F.N(fExpr));
        if (temp.isNumber()) {
          return temp.evalfc();
        }
      }
    }
    return Complex.NaN;
  }

  /**
   * Get the evaluation engine assigned to this evaluator.
   *
   * @return
   */
  public EvalEngine getEvalEngine() {
    return fEngine;
  }

  /**
   * Returns the expression value to which the specified variableName is mapped, or {@code null} if
   * this map contains no mapping for the variableName.
   *
   * @param variableName
   * @return
   */
  public IExpr getVariable(String variableName) {
    return fVariableMap.get(F.symbol(variableName, fEngine));
  }

  /**
   * Converts the <code>inputExpression</code> string into a Java Symja expression string.
   *
   * @param inputExpression an input expression
   */
  public String toJavaForm(final String inputExpression) throws SymjaMathException {
    IExpr parsedExpression;
    if (inputExpression != null) {
      ExprParser parser = new ExprParser(fEngine);
      parsedExpression = parser.parse(inputExpression);
      return parsedExpression.internalFormString(false, 0).toString();
    }
    return "";
  }

  /**
   * Converts the inputExpression string into a Scala expression and writes the result to the given
   * <code>Writer</code>string.
   *
   * @param inputExpression an input expression
   */
  public String toScalaForm(final String inputExpression) throws SymjaMathException {
    IExpr parsedExpression;
    if (inputExpression != null) {
      ExprParser parser = new ExprParser(fEngine);
      parsedExpression = parser.parse(inputExpression);
      return parsedExpression.internalScalaString(false, 0).toString();
    }
    return "";
  }
}
