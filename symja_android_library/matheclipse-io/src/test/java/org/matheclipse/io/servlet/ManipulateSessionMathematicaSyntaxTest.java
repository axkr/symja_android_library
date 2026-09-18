package org.matheclipse.io.servlet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;

/**
 * {@link ManipulateSessionTest}'s example read in Wolfram Language syntax, as
 * <code>MMAServletServer</code> reads it.
 *
 * <p>
 * The built-in symbol table is keyed by name and built once per JVM, lower-cased for Symja's syntax
 * and not for this one, so this class runs in a JVM of its own - see the
 * "wolfram-language-syntax" surefire execution in the module's pom.
 */
public class ManipulateSessionMathematicaSyntaxTest {

  static {
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
  }

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  @Test
  public void testReadOutsShowTheValues() throws Exception {
    ManipulateSessionTest.checkReadOuts(
        new EvalEngine("manipulate-test", 256, System.out, false),
        "Manipulate[status = If[target >= 8, \"reached\", \"not yet\"]; target,"
            + " Row[{Control[{{target, 1, \"target\"}, 0, 10}], Style[\" \"],"
            + " Style[Dynamic[target]]}],"
            + " Style[Dynamic[status], Bold, Red], {{status, \"\", \"\"}, ControlType -> None}]");
  }
}
