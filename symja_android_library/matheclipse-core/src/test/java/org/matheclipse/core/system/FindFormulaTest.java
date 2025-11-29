package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for FindFormula function */
public class FindFormulaTest extends ExprEvaluatorTestCase {


  @Test
  public void testFindFormula() {
    check("tt=Table({x, N(x * Sin(x))}, {x, 0, 4, .3});", //
        "");
    check("FindFormula(tt,x)", //
        "x*Sin(x)");

    check(
        "FindFormula({{-5.0,29.64995},{-4.5,25.47952},{-4.0,22.19164},{-3.5,18.63141},{-3.0,15.37643},{-2.5,12.7059},{-2.0,10.14162},{-1.5,7.6126},{-1.0,5.48926},{-0.5,3.66364},{0.0,2.00527},{0.5,0.809483},{1.0,-0.459001},{1.5,-1.19444},{2.0,-1.81788},{2.5,-2.23119},{3.0,-2.40271},{3.5,-2.36648},{4.0,-2.01335},{4.5,-1.29295},{5.0,-0.411884}},x)",
        "2.07213-2.99368*x+0.498972*x^2");

    check(
        "FindFormula({{0.0,3.61426},{0.2,3.54043},{0.4,4.95524},{0.6,5.57156},{0.8,5.66208},{1.0,6.77502},{1.2,6.90958},{1.4,9.26032},{1.6,10.33626},{1.8,11.88631},{2.0,14.54422},{2.2,18.05916},{2.4,21.27128},{2.6,24.77081},{2.8,27.82827},{3.0,33.56261},{3.2,38.89916},{3.4,45.24117},{3.6,54.21552},{3.8,62.60618},{4.0,73.70896}},x)", //
        "2.61452+E^(0.978613*x)+1.31512*x^2");
  }


  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
