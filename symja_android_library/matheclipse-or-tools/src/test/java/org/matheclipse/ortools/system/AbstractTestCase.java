package org.matheclipse.ortools.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.util.Locale;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

/**
 * The base class of the Symja level tests of the module: an {@link ExprEvaluator} with relaxed
 * syntax, the graph theory functions and the OR-Tools solvers installed.
 */
public abstract class AbstractTestCase {
  protected ExprEvaluator evaluator;

  public AbstractTestCase() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    setUp();
  }

  /** Evaluate <code>evalString</code> and compare its output form with the expected one. */
  public void check(String evalString, String expectedResult) {
    check(evalString, expectedResult, "");
  }

  public synchronized void check(String evalString, String expectedResult,
      String strException) {
    try {
      String evaledResult = printResult(evaluator.eval(evalString));
      assertEquals(expectedResult, evaledResult);
    } catch (SyntaxError e) {
      assertEquals(e.getMessage(), expectedResult);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(strException, e.getMessage());
    }
  }

  /** The output form of the evaluated <code>evalString</code>. */
  public String evalString(String evalString) {
    return printResult(evaluator.eval(evalString));
  }

  private static String printResult(IExpr result) {
    if (result == S.Null) {
      return "";
    }
    final StringWriter buf = new StringWriter();
    EvalEngine engine = EvalEngine.get();
    int significantFigures = engine.getSignificantFigures();
    OutputFormFactory off =
        OutputFormFactory.get(true, false, significantFigures - 1, significantFigures + 1);
    if (off.convert(buf, result)) {
      return buf.toString();
    }
    return "ScriptEngine: ERROR-IN-OUTPUTFORM";
  }

  /** The limits and switches of the engine used by all tests. */
  private static void initConfig() {
    Config.SHORTEN_STRING_LENGTH = 80;
    Config.MAX_AST_SIZE = 20000;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_POLYNOMIAL_DEGREE = 150;
    Config.FILESYSTEM_ENABLED = false;
    Config.ROUNDING_MODE = RoundingMode.HALF_EVEN;
  }

  /** The JUnit setup method */
  public void setUp() {
    initConfig();
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
    org.matheclipse.graphtheory.GraphTheoryInit.init();
    org.matheclipse.ortools.OrToolsInit.init();

    boolean relaxedSyntax = true;
    EvalEngine engine = new EvalEngine(relaxedSyntax);
    EvalEngine.set(engine);
    engine.init();
    engine.setRecursionLimit(512);
    engine.setIterationLimit(500);
    engine.setOutListDisabled(false, (short) 10);
    evaluator = new ExprEvaluator(engine, false, (short) 100);
  }
}
