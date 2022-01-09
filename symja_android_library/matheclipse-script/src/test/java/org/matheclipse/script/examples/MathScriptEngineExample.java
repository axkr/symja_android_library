package org.matheclipse.script.examples;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.parser.client.ParserConfig;

public class MathScriptEngineExample {
  public static void main(String[] args) {
    ToggleFeature.COMPILE = true;
    Config.SHORTEN_STRING_LENGTH = 80;
    Config.MAX_AST_SIZE = 20000;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.FILESYSTEM_ENABLED = true;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;

    ScriptEngineManager scriptManager = new ScriptEngineManager();
    ScriptEngine scriptEngine = scriptManager.getEngineByExtension("m");
    scriptEngine.put("PRINT_STACKTRACE", Boolean.TRUE);
    scriptEngine.put("RELAXED_SYNTAX", Boolean.FALSE);

    EvalEngine engine = (EvalEngine) scriptEngine.get("EVAL_ENGINE");
    EvalEngine.set(engine);
    engine.init();
    engine.setRecursionLimit(512);
    engine.setIterationLimit(500);
    engine.setOutListDisabled(false, (short) 10);
    try {
      System.out.println(scriptEngine.eval("D[Sin[x],x]"));

      System.out.println(scriptEngine.eval("Det[{{1,2},{3,4}}]"));

    } catch (Exception rex) {
      rex.printStackTrace();
    }
  }
}
