package org.matheclipse.core.examples;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.script.engine.MathScriptEngine;

public class MathScriptEngineExample {
  public static void main(String[] args) {
    Config.FILESYSTEM_ENABLED = true;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
    EvalEngine engine = new EvalEngine();
    MathScriptEngine scriptEngine =
        new MathScriptEngine(engine); // fScriptManager.getEngineByExtension("m");
    scriptEngine.put("PRINT_STACKTRACE", Boolean.TRUE);
    scriptEngine.put("RELAXED_SYNTAX", Boolean.FALSE);
    try {
      // EvalEngine.set(engine);
      // engine.init();
      // engine.setRecursionLimit(256);
      // engine.setIterationLimit(500);
      System.out.println(scriptEngine.eval("D[Sin[x],x]"));

      System.out.println(scriptEngine.eval("Det[{{1,2},{3,4}}]"));

    } catch (Exception rex) {
      rex.printStackTrace();
    }
  }
}
