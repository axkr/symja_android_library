package org.matheclipse.core.integrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.StringWriter;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalControlledCallable;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/** Tests system.reflection classes */
public abstract class AbstractRubiTestCase {

  protected ExprEvaluator fEvaluator;
  /** Timeout limit in seconds as the default value for Symja expression evaluation. */
  protected long fSeconds = 40;

  private boolean isRelaxedSyntax;

  public AbstractRubiTestCase(String name, boolean isRelaxedSyntax) {
    // System.out.println(">>>" + name);
    this.isRelaxedSyntax = isRelaxedSyntax;
    Config.SERVER_MODE = false;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = isRelaxedSyntax;
  }

  private String printResult(IExpr integral, IExpr result, String expectedResult,
      String manuallyCheckedResult) {
    // if (result.equals(F.Null)) {
    // return "";
    // }
    if (result.equals(F.$Aborted)) {
      return "TIMEOUT";
    }

    if (result.isFree(F.Integrate)) {
      if (manuallyCheckedResult != null) {
        manuallyCheckedResult = manuallyCheckedResult.trim();
        if (manuallyCheckedResult.length() > 0) {
          if (manuallyCheckedResult.equals(result.toString())) {
            // the expressions are textual equal
            return expectedResult;
          }
          ExprParser parser = new ExprParser(fEvaluator.getEvalEngine(), true);
          IExpr expr2 = parser.parse(manuallyCheckedResult);
          IExpr expected = fEvaluator.eval(expr2);
          if (result.equals(expected)) {
            // the expressions are structurally equal
            return expectedResult;
          }
        }
      }

      expectedResult = expectedResult.trim();
      if (expectedResult.length() > 0) {
        if (expectedResult.equals(result.toString())) {
          return expectedResult;
        }
        IExpr expected = fEvaluator.eval(expectedResult);
        if (result.equals(expected)) {
          // the expressions are structurally equal
          return expectedResult;
        }

        IExpr temp = fEvaluator.eval(F.Subtract(result, expected));
        // System.out.println(temp.toString());
        expected = fEvaluator.eval(F.PossibleZeroQ(temp));
        if (expected.isTrue()) {
          // the expressions are structurally equal
          return expectedResult;
        } else {
          // Form-independent correctness check: the antiderivative must differentiate back to the
          // integrand, i.e. D(result, x) - integrand == 0. This is what lets a native algorithm
          // stage produce a different-looking (but correct) antiderivative than Rubi/Mathematica.
          //
          // The two checks are OR-ed, so their order cannot change which results are accepted -
          // only what it costs to decide. The numeric check runs in milliseconds, while the
          // symbolic one calls Together/Simplify, which take tens of seconds on a big difference
          // and can hang outright (JAS' subresultant GCD swells on complex-rational coefficients,
          // and it never checks the engine deadline, so no timeout can stop it). So try the cheap
          // one first and only fall back to the symbolic route when it cannot decide.
          IExpr difference = fEvaluator.eval(F.Subtract(F.D(result, F.symbol("x")), integral));
          if (isFiniteDifferenceCorrect(result, integral, F.symbol("x"))
              || isZeroAntiderivativeCheck(difference)) {
            return expectedResult;
          } else {
            System.out.println("D(result) - integrand not provably zero:\n" + difference + "\n");
          }
        }
        // IExpr resultTogether= F.Together.of(F.ExpandAll(result));
        // IExpr expectedTogether = F.Together.of(F.ExpandAll(expected));
        // if (resultTogether.equals(expectedTogether)) {
        // // the expressions are structurally equal
        // return expectedResult;
        // }
      }
    } else if (manuallyCheckedResult != null) {
      manuallyCheckedResult = manuallyCheckedResult.trim();
      if (manuallyCheckedResult.length() > 0) {
        if (manuallyCheckedResult.equals(result.toString())) {
          // the expressions are textual equal
          return expectedResult;
        }
      }
    }
    final StringWriter buf = new StringWriter();
    OutputFormFactory.get(fEvaluator.getEvalEngine().isRelaxedSyntax()).convert(buf, result);
    return buf.toString();
  }

  /**
   * Symbolic zero test for {@code D(result, x) - integrand}: {@code PossibleZeroQ} escalated through
   * {@code Together} and {@code Simplify}. Works when Symja can differentiate and simplify cleanly.
   */
  private boolean isZeroAntiderivativeCheck(IExpr difference) {
    if (difference.isZero() || fEvaluator.eval(F.PossibleZeroQ(difference)).isTrue()) {
      return true;
    }
    IExpr together = fEvaluator.eval(F.Together(difference));
    if (together.isZero() || fEvaluator.eval(F.PossibleZeroQ(together)).isTrue()) {
      return true;
    }
    IExpr simplified = fEvaluator.eval(F.Simplify(difference));
    return simplified.isZero() || fEvaluator.eval(F.PossibleZeroQ(simplified)).isTrue();
  }

  /**
   * Numeric finite-difference correctness check: {@code (result(x0+h) - result(x0-h))/(2h)} must
   * equal the integrand at {@code x0}, for several {@code x0} and random values of every parameter.
   * This avoids the symbolic derivative entirely, so it verifies antiderivatives (e.g. {@code RootSum}
   * or special-function forms) whose symbolic {@code D} Symja cannot simplify.
   */
  private boolean isFiniteDifferenceCorrect(IExpr result, IExpr integrand, IExpr x) {
    // Symbols come from the integrand; the antiderivative introduces no new free symbols (e.g.
    // RootSum slots are bound). Note this must not use Variables(), which reports the *polynomial*
    // variables: for a trigonometric integrand it answers {a, b, Cos[c+d*x]}, which neither
    // contains x nor lets Cot[c+d*x]/Sec[c+d*x] in the antiderivative become numeric.
    IAST symbols = freeSymbols(integrand);
    if (!symbols.exists(symbol -> symbol.equals(x))) {
      return false; // nothing to differentiate against
    }
    final double h = 1.0e-5;
    int verified = 0;
    for (int trial = 0; trial < 8 && verified < 3; trial++) {
      double x0 = 0.4 + 0.37 * trial;
      try {
        IExpr resultPlus = numericAt(result, symbols, x, x0 + h, trial);
        IExpr resultMinus = numericAt(result, symbols, x, x0 - h, trial);
        IExpr integrandValue = numericAt(integrand, symbols, x, x0, trial);
        if (!resultPlus.isNumber() || !resultMinus.isNumber() || !integrandValue.isNumber()) {
          continue; // not numerically evaluable at this point; try another
        }
        IExpr derivative =
            fEvaluator.eval(F.Divide(F.Subtract(resultPlus, resultMinus), F.num(2.0 * h)));
        IExpr error = fEvaluator.eval(F.Abs(F.Subtract(derivative, integrandValue)));
        IExpr tolerance =
            fEvaluator.eval(F.Times(F.num(1.0e-3), F.Plus(F.C1, F.Abs(integrandValue))));
        if (!fEvaluator.eval(F.Less(error, tolerance)).isTrue()) {
          return false;
        }
        verified++;
      } catch (RuntimeException rex) {
        // try another point
      }
    }
    return verified >= 3;
  }

  /**
   * The free symbols of {@code expr}, sorted, as a {@code List(...)}. A symbol is free when it
   * carries neither the {@code Constant} nor the {@code NumericFunction} attribute, which excludes
   * both constants such as {@code Pi} and the heads of the expression such as {@code Cos} or
   * {@code Plus}.
   */
  private static IAST freeSymbols(IExpr expr) {
    Set<IExpr> symbols = new TreeSet<IExpr>();
    collectFreeSymbols(expr, symbols);
    IASTAppendable result = F.ListAlloc(symbols.size());
    result.appendAll(symbols);
    return result;
  }

  private static void collectFreeSymbols(IExpr expr, Set<IExpr> symbols) {
    if (expr.isSymbol()) {
      if (expr.isVariable()) {
        symbols.add(expr);
      }
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      // start at 0 to visit the head as well
      for (int i = 0; i < ast.size(); i++) {
        collectFreeSymbols(ast.get(i), symbols);
      }
    }
  }

  /** Numeric value of {@code expr} with {@code x = xValue} and every other free symbol randomised. */
  private IExpr numericAt(IExpr expr, IAST symbols, IExpr x, double xValue, int trial) {
    IASTAppendable rules = F.ListAlloc(symbols.size());
    for (int i = 1; i < symbols.size(); i++) {
      IExpr symbol = symbols.get(i);
      double value = symbol.equals(x) ? xValue : (0.7 + 0.31 * i + 0.09 * trial);
      rules.append(F.Rule(symbol, F.num(value)));
    }
    return fEvaluator.eval(F.N(F.ReplaceAll(expr, rules)));
  }

  /** Evaluates the given string-expression and returns the result in <code>OutputForm</code> */
  public String interpreter(final String inputExpression, final String expectedResult,
      String manuallyCheckedResult) {
    IExpr result;
    final StringWriter buf = new StringWriter();
    IExpr integral = fEvaluator.parse(inputExpression).first();
    try {
      if (fSeconds <= 0) {
        result = fEvaluator.eval(inputExpression);
      } else {
        EvalEngine engine = fEvaluator.getEvalEngine();
        engine.setSeconds(fSeconds);
        result = fEvaluator.evaluateWithTimeout(inputExpression, fSeconds, TimeUnit.SECONDS, true,
            new EvalControlledCallable(fEvaluator.getEvalEngine()));
      }
      if (result != null) {
        return printResult(integral, result, expectedResult, manuallyCheckedResult);
      }
    } catch (final AbortException re) {
      return printResult(integral, F.$Aborted, expectedResult, manuallyCheckedResult);
    } catch (final FailedException re) {
      return printResult(integral, F.$Failed, expectedResult, manuallyCheckedResult);
    } catch (final SyntaxError se) {
      String msg = se.getMessage();
      System.err.println(msg);
      System.err.println();
      System.err.flush();
      return "";
    } catch (final RuntimeException re) {
      Throwable me = re.getCause();
      if (me instanceof MathException) {
        Validate.printException(buf, me);
      } else {
        Validate.printException(buf, re);
      }
      System.err.println(buf.toString());
      System.err.flush();
      return "";
    } catch (final Exception e) {
      Validate.printException(buf, e);
      System.err.println(buf.toString());
      System.err.flush();
      return "";
    } catch (final OutOfMemoryError e) {
      Validate.printException(buf, e);
      System.err.println(buf.toString());
      System.err.flush();
      return "";
    } catch (final StackOverflowError e) {
      Validate.printException(buf, e);
      System.err.println(buf.toString());
      System.err.flush();
      return "";
    }
    return buf.toString();
  }

  public void check(String evalString, String expectedResult) {
    checkLength(evalString, expectedResult, null, -1);
  }

  public void checkLength(String evalString, String expectedResult, String manuallyCheckedResult,
      int resultLength) {
    try {
      if (evalString.length() == 0 && expectedResult.length() == 0) {
        return;
      }
      // scriptEngine.put("STEPWISE",Boolean.TRUE);
      String evaledResult = interpreter(evalString, expectedResult, manuallyCheckedResult);
      if (resultLength > 0 && evaledResult.length() > resultLength) {
        evaledResult = evaledResult.substring(0, resultLength) + "<<SHORT>>";
        assertEquals(expectedResult, evaledResult);
      } else {
        assertEquals(expectedResult, evaledResult);
      }
    } catch (AssertionError e) {
      // System.out.println(getName() + " - " + evalString);
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  /**
   * Tests if in the evaluation of the integral the same rule is used as is used by Mathematica Rubi
   *
   * @param evalString String of the form "Integrate(expr, x)
   * @param ruleNumberUsed The internal Rubi rule that was used to solve this problem
   * @param expectedResult The expected antiderivative that Rubi calculates
   */
  public void check(String evalString, String expectedResult, int... ruleNumberUsed) {
    check(evalString, expectedResult);
  }

  public void check(String evalString, String expectedResult, String manuallyCheckedResult,
      int... ruleNumberUsed) {
    checkLength(evalString, expectedResult, manuallyCheckedResult, -1);
  }

  @BeforeEach
  protected void setUp() {
    try {
      // super.setUp();
      Config.SHORTEN_STRING_LENGTH = 80;
      // Config.PRIME_FACTORS = new BigIntegerPrimality();
      F.await();
      // start test with fresh instance
      EvalEngine engine = new EvalEngine(isRelaxedSyntax);
      fEvaluator = new ExprEvaluator(engine, true, (short) 0);
      engine.setFileSystemEnabled(true);
      engine.setRecursionLimit(256);
      engine.setIterationLimit(500);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  protected void tearDown() throws Exception {
    EvalEngine.remove();
  }
}
