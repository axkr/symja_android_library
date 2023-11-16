package org.matheclipse.discord;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.parser.client.ParserConfig;
import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class InterpreterTest {

  @Before
  public void setUp() throws Exception {
    Locale.setDefault(Locale.US);
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    ToggleFeature.COMPILE = false;
    ToggleFeature.COMPILE_PRINT = true;
    Config.JAVA_UNSAFE = true;
    Config.SHORTEN_STRING_LENGTH = 512;
    Config.USE_VISJS = true;
    Config.FILESYSTEM_ENABLED = false;
    Config.FUZZY_PARSER = true;
    Config.UNPROTECT_ALLOWED = false;
    Config.USE_MANIPULATE_JS = true;
    Config.JAS_NO_THREADS = true;
    Config.MATHML_TRIG_LOWERCASE = false;
    Config.MAX_AST_SIZE = 20000;
    Config.MAX_OUTPUT_SIZE = 10000;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.MAX_INPUT_LEAVES = 100L;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    Config.PRIME_FACTORS = new BigIntegerPrimality();
    EvalEngine.get().setPackageMode(true);
    F.initSymbols();
    SymjaBot.initFunctions();
  }

  @Test
  public void testInterpreter() throws Exception {
    Object result = SymjaBot.interpreter("ListPlot(Prime(Range(100)))", 3600);
    Assertions.assertTrue(result instanceof ImageExpr);
  }
}
