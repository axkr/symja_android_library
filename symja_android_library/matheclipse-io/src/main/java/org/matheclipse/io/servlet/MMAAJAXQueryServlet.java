package org.matheclipse.io.servlet;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.ConstantDefinitions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.io.IOInit;
import org.matheclipse.parser.client.ParserConfig;

public class MMAAJAXQueryServlet extends AJAXQueryServlet {

  private static final long serialVersionUID = 5700862133581416988L;
  public static volatile boolean INITIALIZED = false;

  @Override
  protected boolean isRelaxedSyntax() {
    return false;
  }

  @Override
  protected synchronized void initialization() {
    if (INITIALIZED) {
      return;
    }
    INITIALIZED = true;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
    ToggleFeature.COMPILE = true;
    ToggleFeature.COMPILE_PRINT = true;
    Config.UNPROTECT_ALLOWED = false;
    Config.USE_MANIPULATE_JS = true;
    // disable threads for JAS only on google appengine
    Config.JAS_NO_THREADS = false;
    Config.BUILTIN_PROTECTED = ISymbol.NOATTRIBUTE;
    Config.JAVA_UNSAFE = true;
    Config.SHORTEN_STRING_LENGTH = 1024;
    // Config.THREAD_FACTORY =
    // com.google.appengine.api.ThreadManager.currentRequestThreadFactory();
    Config.MATHML_TRIG_LOWERCASE = false;
    // Config.MAX_AST_SIZE = ((int) Short.MAX_VALUE) * 8;
    // Config.MAX_OUTPUT_SIZE = Short.MAX_VALUE;
    // Config.MAX_BIT_LENGTH = ((int) Short.MAX_VALUE) * 8;
    // Config.MAX_INPUT_LEAVES = 1000L;
    // Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    // Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.DEFAULT_ITERATION_LIMIT = 10_000;
    Config.DEFAULT_RECURSION_LIMIT = 1_024;

    EvalEngine engine = new EvalEngine(isRelaxedSyntax());
    EvalEngine.set(engine);
    // engine.setPackageMode(true);

    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
    Config.FILESYSTEM_ENABLED = true;
    F.initSymbols();
    IOInit.init();
    engine.setRecursionLimit(Config.DEFAULT_RECURSION_LIMIT);
    engine.setIterationLimit(Config.DEFAULT_ITERATION_LIMIT);

    S.Plot.setEvaluator(org.matheclipse.core.reflection.system.Plot.CONST);
    S.Plot3D.setEvaluator(org.matheclipse.core.reflection.system.Plot3D.CONST);
    // F.Show.setEvaluator(org.matheclipse.core.builtin.graphics.Show.CONST);
    // Config.JAS_NO_THREADS = true;
    // MMAAJAXQueryServlet.log.info(servlet + " initialized");
    System.out.println("Symja version " + ConstantDefinitions.VERSION + " initialized");
  }
}
