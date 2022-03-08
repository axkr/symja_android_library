package org.matheclipse.script.examples;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;

public class MathScriptEngineExample {
  public static void main(String[] args) {
    ToggleFeature.COMPILE = true;
    ToggleFeature.COMPILE_PRINT = true;
    Config.SHORTEN_STRING_LENGTH = 80;
    Config.MAX_AST_SIZE = 20000;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.FILESYSTEM_ENABLED = true;

    ScriptEngineManager scriptManager = new ScriptEngineManager();
    ScriptEngine scriptEngine = scriptManager.getEngineByExtension("m");
    scriptEngine.put("PRINT_STACKTRACE", Boolean.TRUE);

    EvalEngine engine = (EvalEngine) scriptEngine.get("EVAL_ENGINE");
    EvalEngine.set(engine);

    try {
      engine.setRecursionLimit(512);
      engine.setIterationLimit(500);
      // the last 10 evaluations are memorized now
      engine.setOutListDisabled(false, (short) 10);

      System.out.println(scriptEngine.eval("D(sin(x),x)"));
      System.out.println(scriptEngine.eval("apart[(x)/(x^2-1)] == 1/(2*(-1+x))+1/(2*(1+x))"));
      System.out.println(scriptEngine.eval("Det[{{1,2},{3,4}}]"));

      System.out.println(scriptEngine.eval("f=10!"));
      System.out.println(scriptEngine.eval("f+f"));
      // the last 10 evaluations are memorized
      System.out.println(scriptEngine.eval("%+5"));

      // the following example will only work, if Maven module 'matheclipse-io' is on the classpath:
      String testIO = "SemanticImportString(\"Products,Sales,Market_Share\n" //
          + "a,5500,3\n" + "b,12200,4\n" + "c,60000,33\n" + "\")";
      System.out.println(scriptEngine.eval(testIO));
    } catch (ScriptException e) {
      e.printStackTrace();
    }

  }
}
