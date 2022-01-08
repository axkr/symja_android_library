package org.matheclipse.core.rubi.step02;

import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalControlledCallable;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;
import junit.framework.TestCase;

/** Tests system.reflection classes */
public abstract class AbstractRubiTestCase extends TestCase {

  protected ExprEvaluator fEvaluator;
  /** Timeout limit in seconds as the default value for Symja expression evaluation. */
  protected long fSeconds = 30;

  private boolean isRelaxedSyntax;

  public AbstractRubiTestCase(String name, boolean isRelaxedSyntax) {
    super(name);
    // System.out.println(">>>" + name);
    this.isRelaxedSyntax = isRelaxedSyntax;
    Config.SERVER_MODE = false;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = isRelaxedSyntax;
  }

  private String printResult(IExpr result, String expectedResult, String manuallyCheckedResult) {
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

          IExpr expected = fEvaluator.eval(manuallyCheckedResult);
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

        expected = fEvaluator.eval(F.PossibleZeroQ(F.Subtract(result, expected)));
        if (expected.isTrue()) {
          // the expressions are structurally equal
          return expectedResult;
        }
        // IExpr resultTogether= F.Together.of(F.ExpandAll(result));
        // IExpr expectedTogether = F.Together.of(F.ExpandAll(expected));
        // if (resultTogether.equals(expectedTogether)) {
        // // the expressions are structurally equal
        // return expectedResult;
        // }
      }
    }
    final StringWriter buf = new StringWriter();
    OutputFormFactory.get(fEvaluator.getEvalEngine().isRelaxedSyntax()).convert(buf, result);
    return buf.toString();
  }

  /** Evaluates the given string-expression and returns the result in <code>OutputForm</code> */
  public String interpreter(
      final String inputExpression, final String expectedResult, String manuallyCheckedResult) {
    IExpr result;
    final StringWriter buf = new StringWriter();
    try {
      if (fSeconds <= 0) {
        result = fEvaluator.eval(inputExpression);
      } else {
        EvalEngine engine = fEvaluator.getEvalEngine();
        engine.setSeconds(fSeconds);
        result =
            fEvaluator.evaluateWithTimeout(
                inputExpression,
                fSeconds,
                TimeUnit.SECONDS,
                true,
                new EvalControlledCallable(fEvaluator.getEvalEngine()));
      }
      if (result != null) {
        return printResult(result, expectedResult, manuallyCheckedResult);
      }
    } catch (final AbortException re) {
      return printResult(F.$Aborted, expectedResult, manuallyCheckedResult);
    } catch (final FailedException re) {
      return printResult(F.$Failed, expectedResult, manuallyCheckedResult);
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

  public void checkLength(
      String evalString, String expectedResult, String manuallyCheckedResult, int resultLength) {
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
      System.out.println(getName() + " - " + evalString);
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

  public void check(
      String evalString,
      String expectedResult,
      String manuallyCheckedResult,
      int... ruleNumberUsed) {
    checkLength(evalString, expectedResult, manuallyCheckedResult, -1);
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    try {
      super.setUp();
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

  @Override
  protected void tearDown() throws Exception {
    EvalEngine.remove();
    super.tearDown();
  }
}
