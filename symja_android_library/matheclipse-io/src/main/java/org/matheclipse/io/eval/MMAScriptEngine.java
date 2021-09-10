package org.matheclipse.io.eval;

import javax.script.ScriptEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.io.IOInit;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.script.engine.MathScriptEngineFactory;

public class MMAScriptEngine {
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    try {
      // ScriptEngineManager manager = new ScriptEngineManager();
      FEConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
      MathScriptEngineFactory f = new MathScriptEngineFactory();
      ScriptEngine engine = f.getScriptEngine();
      IOInit.init();

      Object result = engine.eval("Integrate[Tan[x]^5,x]");
      System.out.println("Result: " + result);
    } catch (Exception ex) {
      LOGGER.catching(ex);
    }
  }
}
