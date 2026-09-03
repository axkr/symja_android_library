package org.matheclipse.compile.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.eval.steps.LocaleMap;
import org.matheclipse.core.eval.steps.RuleDescription;
import org.matheclipse.core.eval.steps.TraceStackSteps;
import org.matheclipse.core.eval.steps.output.JSONStepsTemplate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public abstract class AbstractTestCase {
  protected ExprEvaluator evaluator;
  protected ExprEvaluator evaluatorN;
  public static boolean FUZZ_HARVESTER = false;
  public static BufferedWriter fuzzBuffer = null;

  static {
    if (FUZZ_HARVESTER) {
      File file = new File("./data/harvest.sym");
      try {
        fuzzBuffer = new BufferedWriter(new FileWriter(file));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public AbstractTestCase() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    setUp();
  }

  public void check(String evalString, String expectedResult) {
    check(evaluator, evalString, expectedResult, "", -1);
  }

  public void check(String evalString, String expectedResult, String strException) {
    check(evaluator, evalString, expectedResult, strException, -1);
  }

  public void check(String evalString, String expectedResult, int resultLength) {
    check(evaluator, evalString, expectedResult, "", resultLength);
  }

  public void call(String evalString) {
    try {
      if (evalString.length() == 0) {
        return;
      }
      EvalEngine.get().evaluate(evalString);
      // fScriptEngine.eval(evalString);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  public synchronized void check(ExprEvaluator scriptEngine, String evalString,
      String expectedResult, String strException, int resultLength) {
    try {
      if (evalString.length() == 0 && expectedResult.length() == 0) {
        return;
      }
      // scriptEngine.put("STEPWISE",Boolean.TRUE);
      // System.out.println(getName() + " - " + evalString);

      String evaledResult = printResult(scriptEngine.eval(evalString));
      if (resultLength > 0 && evaledResult.length() > resultLength) {
        evaledResult = evaledResult.substring(0, resultLength) + "<<SHORT>>";
        assertEquals(expectedResult, evaledResult);
      } else {
        if (FUZZ_HARVESTER) {
          fuzzBuffer.append(evalString);
          fuzzBuffer.append("\n\n\n");
        }
        assertEquals(expectedResult, evaledResult);
      }
    } catch (SyntaxError e) {
      System.err.println(e.getMessage());
      // e.printStackTrace();
      assertEquals(e.getMessage(), expectedResult);
    } catch (Exception e) {
      e.printStackTrace();
      String message = e.getMessage();
      assertEquals(strException, message);
    }
  }

  public void checkRegex(String evalString, String regex) {
    checkRegex(evaluator, evalString, regex);
  }

  public void checkRegex(ExprEvaluator scriptEngine, String evalString, String regex) {
    try {
      String evaledResult = printResult(scriptEngine.eval(evalString));

      assertEquals(true, Pattern.matches(regex, evaledResult));

    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  /**
   * Checks the steps of evaluating a mathematical expression in JSON format.
   *
   * @param input the input mathematical expression as a string
   * @param filter a predicate used to filter the symbols that should be included in the JSON output
   * @param expected the expected JSON string representing the steps of evaluation
   */
  protected void checkJSON(String input, Predicate<ISymbol> filter, String expected) {
    try {
      // disable Out[] history
      ExprEvaluator util = new ExprEvaluator(true, (short) -1);
      EvalEngine engine = util.getEvalEngine();
      TraceStackSteps stepListener = new TraceStackSteps();
      EvalEngine.get().setStepListener(stepListener);
      System.out.println("\n" + input);
      IExpr expr = engine.parse(input);
      if (expr != null) {

        // this eval call doesn't reset the EvalEngine
        IExpr result = util.eval(expr);
        // disable math-steps tracing during JSON output generation
        engine.setStepListener(null);
        RuleDescription desc = LocaleMap.get("en");

        OutputFormFactory outputFormFactory = OutputFormFactory.get(true);
        outputFormFactory.setIgnoreNewLine(true);
        String actual = outputFormFactory.toString(result);

        System.out.println("\nResult: " + actual);
        assertEquals(expected, actual);

        JSONStepsTemplate templateSteps = stepListener.createJSONSteps(filter, desc);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
          String output = ow.writeValueAsString(templateSteps);
          System.out.println(output);
          // F.openJSONOnDesktop(output);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    } finally {
      EvalEngine.get().setStepListener(null);
    }
  }

  public String evalString(String evalString) {
    try {
      // scriptEngine.put("STEPWISE",Boolean.TRUE);
      return printResult(evaluator.eval(evalString));
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
    return "";
  }

  /**
   * Default relative tolerance for the value comparison in {@link #checkNumeric(String, String)}.
   *
   * <p>
   * The last digits of a <code>double</code> result depend on the CPU: aarch64 (Apple silicon)
   * contracts a multiply and an add into a single fused multiply-add where x86-64 rounds twice, and
   * the platform's math library rounds the transcendental functions differently. The same
   * computation therefore prints with a different last digit on the two architectures, e.g.
   * <code>-0.2193839343955203</code> against <code>-0.2193839343955202</code>, which a comparison
   * of the printed strings reports as a failure on one architecture and not on the other. Across
   * the whole test suite the observed spread is below <code>5*10^-15</code> relative, so this bound
   * leaves more than two orders of magnitude of head room while still catching a regression in the
   * 12th significant digit.
   */
  public static final double DEFAULT_NUMERIC_RELATIVE_TOLERANCE = 1.0e-12;

  public void checkNumeric(String evalString, String expectedResult) {
    checkNumeric(evalString, expectedResult, -1);
  }

  public void checkNumeric(String evalString, String expectedResult, int resultLength) {
    checkNumeric(evaluatorN, evalString, expectedResult, resultLength,
        DEFAULT_NUMERIC_RELATIVE_TOLERANCE);
  }

  /**
   * Like {@link #checkNumeric(String, String)}, but with an explicit relative tolerance. Use this
   * for iterative algorithms - root finders, optimizers, numerical quadrature - whose result is
   * accurate to fewer digits than a single arithmetic operation, and which therefore diverge
   * further than {@link #DEFAULT_NUMERIC_RELATIVE_TOLERANCE} between architectures.
   *
   * @param relativeTolerance the largest relative difference which still counts as equal
   */
  public void checkNumeric(String evalString, String expectedResult, double relativeTolerance) {
    checkNumeric(evaluatorN, evalString, expectedResult, -1, relativeTolerance);
  }

  public void checkNumeric(ExprEvaluator scriptEngine, String evalString, String expectedResult,
      int resultLength) {
    checkNumeric(scriptEngine, evalString, expectedResult, resultLength,
        DEFAULT_NUMERIC_RELATIVE_TOLERANCE);
  }

  public void checkNumeric(ExprEvaluator scriptEngine, String evalString, String expectedResult,
      int resultLength, double relativeTolerance) {
    try {
      if (evalString.length() == 0 && expectedResult.length() == 0) {
        return;
      }
      // scriptEngine.put("STEPWISE",Boolean.TRUE);
      // System.out.println(getName() + " - " + evalString);

      String evaledResult = printResultNumeric(scriptEngine.eval(evalString));
      if (resultLength > 0 && evaledResult.length() > resultLength) {
        evaledResult = evaledResult.substring(0, resultLength) + "<<SHORT>>";
        assertEquals(expectedResult, evaledResult);
      } else {
        if (FUZZ_HARVESTER) {
          fuzzBuffer.append(evalString);
          fuzzBuffer.append("\n\n\n");
        }
        if (expectedResult.equals(evaledResult)
            || equalsNumeric(scriptEngine, expectedResult, evaledResult, relativeTolerance)) {
          return;
        }
        // not equal within the tolerance - report the two strings, so that the failure message
        // shows where they differ
        assertEquals(expectedResult, evaledResult);
      }
    } catch (SyntaxError e) {
      e.printStackTrace();
      assertEquals(e.getMessage(), expectedResult);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  /**
   * Parse both results and compare them, accepting numbers which differ by no more than the
   * tolerance. Returns <code>false</code> if either string cannot be parsed, so that a truncated or
   * non-expression result falls back to the plain string comparison.
   */
  public static boolean equalsNumeric(ExprEvaluator scriptEngine, String expectedResult,
      String evaledResult, double relativeTolerance) {
    try {
      return equalsNumeric(scriptEngine.parse(expectedResult), scriptEngine.parse(evaledResult),
          relativeTolerance);
    } catch (RuntimeException rex) {
      return false;
    }
  }

  /**
   * Compare two parsed results. Two subexpressions which both evaluate to a number are compared by
   * value, everything else has to match structurally. Comparing whole subexpressions rather than
   * the individual digits is what lets <code>3.19744*10^-14</code> and <code>3.73035*10^-14</code>
   * - printed as a mantissa times a power of ten - be recognized as the same value.
   */
  private static boolean equalsNumeric(IExpr expected, IExpr evaled, double relativeTolerance) {
    if (expected.equals(evaled)) {
      return true;
    }
    INumber expectedNumber = expected.evalNumber();
    INumber evaledNumber = evaled.evalNumber();
    if (expectedNumber != null && evaledNumber != null) {
      return closeTo(expectedNumber, evaledNumber, relativeTolerance);
    }
    if (expected.isAST() && evaled.isAST()) {
      IAST expectedAST = (IAST) expected;
      IAST evaledAST = (IAST) evaled;
      if (expectedAST.size() != evaledAST.size()) {
        return false;
      }
      // index 0 is the head
      for (int i = 0; i < expectedAST.size(); i++) {
        if (!equalsNumeric(expectedAST.get(i), evaledAST.get(i), relativeTolerance)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * <code>true</code> if the distance between the two numbers is within the relative tolerance.
   *
   * <p>
   * A complex number is compared as a whole, <code>|z1 - z2| &lt;= tolerance * max(|z1|,|z2|)
   * </code>, rather than one component at a time. That is what makes the imaginary residue of a
   * real result acceptable: in <code>-1.54308-I*3.19744*10^-14</code> against
   * <code>-1.54308-I*3.73035*10^-14</code> the imaginary parts share not one significant digit, but
   * they are judged against the modulus <code>1.54308</code> of the number they belong to, against
   * which they are a rounding residue. Note the flip side - a genuinely tiny number standing on its
   * own, such as a probability of <code>6.2*10^-16</code> in a distribution tail, keeps being
   * compared to its own magnitude and so still has to match to full precision. An absolute
   * tolerance large enough to accept the residue above would have made those comparisons vacuous.
   *
   * <p>
   * <code>NaN</code> and the infinities are only equal to themselves.
   */
  private static boolean closeTo(INumber expected, INumber evaled, double relativeTolerance) {
    double expectedRe = expected.reDoubleValue();
    double expectedIm = expected.imDoubleValue();
    double evaledRe = evaled.reDoubleValue();
    double evaledIm = evaled.imDoubleValue();
    if (Double.compare(expectedRe, evaledRe) == 0 && Double.compare(expectedIm, evaledIm) == 0) {
      return true;
    }
    if (!Double.isFinite(expectedRe) || !Double.isFinite(expectedIm) || !Double.isFinite(evaledRe)
        || !Double.isFinite(evaledIm)) {
      return false;
    }
    double difference = Math.hypot(expectedRe - evaledRe, expectedIm - evaledIm);
    double magnitude = Math.max(Math.hypot(expectedRe, expectedIm), Math.hypot(evaledRe, evaledIm));
    return difference <= relativeTolerance * magnitude;
  }

  /**
   * Evaluate <code>evalString</code> and compare the result against <code>expected</code> with an
   * <b>absolute</b> tolerance.
   *
   * <p>
   * This complements {@link #checkNumeric(String, String, double)}, which compares the two
   * <i>printed</i> expressions with a relative tolerance. Use <code>checkApprox</code> when the
   * expected value is a machine number the test computes in Java - <code>Math.exp(0.5)</code>,
   * <code>-Math.acos(31.0 / 40.0)</code> - rather than a literal the algorithm is expected to
   * reproduce digit for digit. Writing such a value as a string would force the test to encode the
   * exact result of the current implementation, so that a harmless change of step size or solver
   * tolerance breaks it even though the answer got no worse.
   *
   * <p>
   * The tolerance is absolute because the callers of this method know the size of the answer they
   * expect: a solution which oscillates through zero, such as <code>Cos</code> sampled near
   * <code>Pi/2</code>, has no useful relative accuracy there, and demanding one would make the
   * comparison unsatisfiable at the crossing while leaving it vacuous at the peaks.
   *
   * @param evalString the expression to evaluate
   * @param expected the expected value of the evaluated expression
   * @param absoluteTolerance the largest absolute difference which still counts as equal
   */
  public void checkApprox(String evalString, double expected, double absoluteTolerance) {
    checkApprox(evaluatorN, evalString, expected, absoluteTolerance);
  }

  /**
   * Like {@link #checkApprox(String, double, double)}, but evaluated by the given script engine.
   *
   * @param scriptEngine the engine which evaluates <code>evalString</code>
   * @param evalString the expression to evaluate
   * @param expected the expected value of the evaluated expression
   * @param absoluteTolerance the largest absolute difference which still counts as equal
   */
  public void checkApprox(ExprEvaluator scriptEngine, String evalString, double expected,
      double absoluteTolerance) {
    IExpr result;
    try {
      result = scriptEngine.eval(evalString);
    } catch (SyntaxError e) {
      fail(evalString + " - " + e.getMessage());
      return;
    } catch (Exception e) {
      e.printStackTrace();
      fail(evalString + " - threw " + e);
      return;
    }
    double evaledValue = result.evalfNaN();
    if (Double.isNaN(evaledValue) && !Double.isNaN(expected)) {
      // report the expression itself - an unevaluated result, a symbol or a message is far more
      // useful here than "expected <1.6487> but was <NaN>"
      fail(evalString + " - expected " + expected + " but did not evaluate to a machine number: "
          + printResultNumeric(result));
      return;
    }
    double difference = Math.abs(evaledValue - expected);
    // negated, so that a NaN difference fails rather than silently passing
    if (!(difference <= absoluteTolerance)) {
      fail(evalString + " - expected " + expected + " but was " + evaledValue + " (difference "
          + difference + " exceeds tolerance " + absoluteTolerance + ")");
    }
  }

  public void check(IAST ast, String strResult) {
    check(EvalEngine.get(), true, ast, strResult);
  }

  public void check(EvalEngine engine, boolean configMode, IAST ast, String strResult) {
    boolean mode = Config.SERVER_MODE;
    try {
      StringWriter buf = new StringWriter();

      Config.SERVER_MODE = configMode;
      if (Config.SERVER_MODE) {
        IAST inExpr = ast;
        TimeConstrainedEvaluator utility =
            new TimeConstrainedEvaluator(engine, false, Config.FOREVER);
        utility.constrainedEval(buf, inExpr);
      } else {
        if (ast != null) {
          OutputFormFactory off = OutputFormFactory.get();
          off.setIgnoreNewLine(true);
          off.convert(buf, ast);
        }
      }

      assertEquals(strResult, buf.toString());
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    } finally {
      Config.SERVER_MODE = mode;
    }
  }

  private String printResult(IExpr result) {
    return printResult(result, true);
  }

  private String printResult(IExpr result, boolean relaxedSyntax) {
    if (result == S.Null) {
      return "";
    }
    final StringWriter buf = new StringWriter();
    EvalEngine engine = EvalEngine.get();
    OutputFormFactory off;

    int significantFigures = engine.getSignificantFigures();
    off =
        OutputFormFactory.get(relaxedSyntax, false, significantFigures - 1, significantFigures + 1);

    if (off.convert(buf, result)) {
      // print the result in the console
      return buf.toString();
    }
    if (Config.FUZZ_TESTING) {
      throw new NullPointerException();
    }
    return "ScriptEngine: ERROR-IN-OUTPUTFORM";
  }

  private String printResultNumeric(IExpr result) {
    return printResultNumeric(result, true);
  }

  private String printResultNumeric(IExpr result, boolean relaxedSyntax) {
    if (result == S.Null) {
      return "";
    }
    final StringWriter buf = new StringWriter();
    OutputFormFactory off = OutputFormFactory.get(relaxedSyntax);

    if (off.convert(buf, result)) {
      // print the result in the console
      return buf.toString();
    }
    if (Config.FUZZ_TESTING) {
      throw new NullPointerException();
    }
    return "ScriptEngine: ERROR-IN-OUTPUTFORM";
  }

  /** The JUnit setup method */
  public void setUp() {
    try {
      ToggleFeature.COMPILE = true;
      ToggleFeature.COMPILE_PRINT = true;
      Config.SHORTEN_STRING_LENGTH = 80;
      Config.MAX_AST_SIZE = 20000;
      Config.MAX_MATRIX_DIMENSION_SIZE = 100;
      Config.MAX_BIT_LENGTH = 200000;
      Config.MAX_POLYNOMIAL_DEGREE = 150;
      Config.MAX_PRECISION_APFLOAT = 512;
      Config.MAX_POLYNOMIAL_DEGREE_LAGUERRE_SOLVER = 500;
      Config.FILESYSTEM_ENABLED = false;
      Config.ROUNDING_MODE = RoundingMode.HALF_EVEN;
      // fScriptEngine = fScriptManager.getEngineByExtension("m");
      // fScriptEngine.put("PRINT_STACKTRACE", Boolean.TRUE);
      // fScriptEngine.put("RELAXED_SYNTAX", Boolean.TRUE);
      // fScriptEngine.put("DECIMAL_FORMAT", "0.0####");
      //
      // fNumericScriptEngine = fScriptManager.getEngineByExtension("m");
      // fNumericScriptEngine.put("RELAXED_SYNTAX", Boolean.TRUE);
      F.await();
      org.matheclipse.compile.CompileInit.init();

      boolean relaxedSyntax = true;
      EvalEngine engine = new EvalEngine(relaxedSyntax);
      EvalEngine.set(engine);
      engine.init();
      engine.setRecursionLimit(512);
      engine.setIterationLimit(500);
      engine.setOutListDisabled(false, (short) 10);

      evaluator = new ExprEvaluator(engine, false, (short) 100);
      evaluatorN = new ExprEvaluator(engine, false, (short) 100);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
