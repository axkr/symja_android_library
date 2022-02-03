package org.matheclipse.io.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.io.IOInit;
import junit.framework.TestCase;

/** Tests system.reflection classes */
public abstract class AbstractTestCase extends TestCase {
  protected ScriptEngine fScriptEngine;
  protected ScriptEngine fNumericScriptEngine;
  protected static ScriptEngineManager fScriptManager = new ScriptEngineManager();
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

  public AbstractTestCase(String name) {
    super(name);
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
  }

  public void check(String evalString, String expectedResult) {
    check(fScriptEngine, evalString, expectedResult, -1);
  }

  public void check(String evalString, String expectedResult, int resultLength) {
    check(fScriptEngine, evalString, expectedResult, resultLength);
  }

  public void call(String evalString) {
    try {
      if (evalString.length() == 0) {
        return;
      }
      fScriptEngine.eval(evalString);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  public void check(ScriptEngine scriptEngine, String evalString, String expectedResult,
      int resultLength) {
    try {
      if (evalString.length() == 0 && expectedResult.length() == 0) {
        return;
      }
      // scriptEngine.put("STEPWISE",Boolean.TRUE);
      // System.out.println(getName() + " - " + evalString);
      String evaledResult = (String) scriptEngine.eval(evalString);

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
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  public void checkRegex(String evalString, String regex) {
    checkRegex(fScriptEngine, evalString, regex);
  }

  public void checkRegex(ScriptEngine scriptEngine, String evalString, String regex) {
    try {
      String evaledResult = (String) scriptEngine.eval(evalString);

      assertEquals(true, Pattern.matches(regex, evaledResult));

    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  public String evalString(String evalString) {
    try {
      // scriptEngine.put("STEPWISE",Boolean.TRUE);
      return (String) fScriptEngine.eval(evalString);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
    return "";
  }

  public void checkNumeric(String evalString, String expectedResult) {
    check(fNumericScriptEngine, evalString, expectedResult, -1);
  }

  public void checkNumeric(String evalString, String expectedResult, int resultLength) {
    check(fNumericScriptEngine, evalString, expectedResult, resultLength);
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

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    try {
      synchronized (fScriptManager) {
        ToggleFeature.COMPILE = true;
        IOInit.init();
        Config.SHORTEN_STRING_LENGTH = 80;
        Config.MAX_AST_SIZE = 20000;
        Config.MAX_MATRIX_DIMENSION_SIZE = 100;
        Config.MAX_BIT_LENGTH = 200000;
        Config.MAX_POLYNOMIAL_DEGREE = 100;
        Config.FILESYSTEM_ENABLED = false;
        fScriptEngine = fScriptManager.getEngineByExtension("m");
        fScriptEngine.put("PRINT_STACKTRACE", Boolean.TRUE);
        fScriptEngine.put("RELAXED_SYNTAX", Boolean.TRUE);
        fScriptEngine.put("DECIMAL_FORMAT", "0.0####");

        fNumericScriptEngine = fScriptManager.getEngineByExtension("m");
        fNumericScriptEngine.put("RELAXED_SYNTAX", Boolean.TRUE);
        F.await();

        EvalEngine engine = (EvalEngine) fScriptEngine.get("EVAL_ENGINE");
        EvalEngine.set(engine);
        engine.init();
        engine.setRecursionLimit(512);
        engine.setIterationLimit(500);
        engine.setOutListDisabled(false, (short) 10);

        EvalEngine numericEngine = (EvalEngine) fScriptEngine.get("EVAL_ENGINE");
        numericEngine.init();
        numericEngine.setRecursionLimit(512);
        numericEngine.setIterationLimit(500);
        numericEngine.setOutListDisabled(false, (short) 10);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
