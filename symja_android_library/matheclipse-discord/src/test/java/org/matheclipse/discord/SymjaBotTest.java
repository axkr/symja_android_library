package org.matheclipse.discord;

import java.util.Locale;
import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.parser.client.ParserConfig;
import junit.framework.TestCase;

public class SymjaBotTest extends TestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
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
  public void testInlineInterpreter001() throws Exception {
    String result = SymjaBot.inlineInterpreter("Test differentiation of `Cos` with\n" //
        + "```mma\n" //
        + "D[Cos[x],x]\n" //
        + "```\n" //
        + "and integration of `Cos` with\n" //
        + "```mma\n" //
        + "Integrate[Cos[x],x]\n" //
        + "```"); //
    assertEquals("Test differentiation of `Cos` with\n" //
        + "Input:\n" //
        + "```mma\n" //
        + "D[Cos[x],x]\n" //
        + "```\n" //
        + "Output:\n" //
        + "```mma\n" //
        + "-Sin(x)\n" //
        + "```\n" //
        + "\n" //
        + "and integration of `Cos` with\n" //
        + "Input:\n" //
        + "```mma\n" //
        + "Integrate[Cos[x],x]\n" //
        + "```\n" //
        + "Output:\n" //
        + "```mma\n" //
        + "Sin(x)\n" //
        + "```\n" //
        + "", result);
  }

  @Test
  public void testInlineInterpreter002() throws Exception {
    String result = SymjaBot.inlineInterpreter("`TeXForm` determines the TeX formula\n" //
        + "```mma\n" //
        + "TeXForm(Expand((x + y)^3))\n" //
        + "```\n" //
        + "which could be rendered with other discord bots."); //
    assertEquals("`TeXForm` determines the TeX formula\n" //
        + "Input:\n" //
        + "```mma\n" //
        + "TeXForm(Expand((x + y)^3))\n" //
        + "```\n" //
        + "Output:\n" //
        + "```LaTeX\n" //
        + "{x}^{3}+3 \\cdot {x}^{2} \\cdot y+3 \\cdot x \\cdot {y}^{2} + {y}^{3}\n" //
        + "```\n" //
        + "\n" //
        + "which could be rendered with other discord bots.", result);
  }

}
