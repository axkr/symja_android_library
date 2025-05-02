package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.parser.client.ParserConfig;

public class ForceTimesOperatorTest extends ExprEvaluatorTestCase {

  @Test
  public void testFactorialMultiply() {
    boolean forceTimesOperator = ParserConfig.EXPLICIT_TIMES_OPERATOR;
    try {
      ParserConfig.EXPLICIT_TIMES_OPERATOR = true;
      check("-3!2==2108", //
          "Syntax error in line: 1 - End-of-file not reached.\n" //
              + "-3!2==2108\n" //
              + "   ^");
    } finally {
      ParserConfig.EXPLICIT_TIMES_OPERATOR = forceTimesOperator;
    }

    check("-3!2==2108", //
          "False");
    check("-3!2==-12", //
          "True");
  }
}
