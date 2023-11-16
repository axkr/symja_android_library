package org.matheclipse.core.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public abstract class ExprEvaluatorTestCase {
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

  public ExprEvaluatorTestCase() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
  }

  public void check(String evalString, String expectedResult) {
    check(evaluator, evalString, expectedResult, -1);
  }

  public void check(String evalString, String expectedResult, int resultLength) {
    check(evaluator, evalString, expectedResult, resultLength);
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

  public void check(ExprEvaluator scriptEngine, String evalString, String expectedResult,
      int resultLength) {
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
      e.printStackTrace();
      assertEquals(e.getMessage(), expectedResult);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
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

  public void checkNumeric(String evalString, String expectedResult) {
    checkNumeric(evalString, expectedResult, -1);
  }

  public void checkNumeric(String evalString, String expectedResult, int resultLength) {
    checkNumeric(evaluatorN, evalString, expectedResult, resultLength);
  }

  public void checkNumeric(ExprEvaluator scriptEngine, String evalString, String expectedResult,
      int resultLength) {
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
    if (result.equals(S.Null)) {
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
    if (result.equals(S.Null)) {
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
  @Before
  public void setUp() {
    try {
      ToggleFeature.COMPILE = true;
      ToggleFeature.COMPILE_PRINT = true;
      Config.SHORTEN_STRING_LENGTH = 80;
      Config.MAX_AST_SIZE = 20000;
      Config.MAX_MATRIX_DIMENSION_SIZE = 100;
      Config.MAX_BIT_LENGTH = 200000;
      Config.MAX_POLYNOMIAL_DEGREE = 100;
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

  @After
  public void tearDown() throws Exception {

  }

}
