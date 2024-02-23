package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for FindFormula function */
public class FindFormulaTest extends ExprEvaluatorTestCase {


  @Test
  public void testFindFormula() {
    check("tt=Table({x, N(x * Sin(x))}, {x, 0, 4, .3});", //
        "");
    check(" FindFormula(tt,x)", //
        "FindFormula(\n"
            + "{{0.0,0.0},\n" //
            + " {0.3,0.0886561},\n" //
            + " {0.6,0.338785},\n" //
            + " {0.9,0.704994},\n" //
            + " {1.2,1.11845},\n" //
            + " {1.5,1.49624},\n" //
            + " {1.8,1.75293},\n" //
            + " {2.1,1.81274},\n" //
            + " {2.4,1.62111},\n" //
            + " {2.7,1.15393},\n" //
            + " {3.0,0.42336},\n" //
            + " {3.3,-0.520561},\n" //
            + " {3.6,-1.59307},\n" //
        + " {3.9,-2.68229}},x)");

  }


  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
