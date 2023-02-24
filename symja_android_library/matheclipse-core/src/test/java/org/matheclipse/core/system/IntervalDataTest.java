package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for compiler functions */
public class IntervalDataTest extends ExprEvaluatorTestCase {

  public IntervalDataTest(String name) {
    super(name);
  }

  public void testToIntervalData() {
    check("ToIntervalData(Reals)", //
        "IntervalData({-Infinity,Less,Less,Infinity})");
    check("ToIntervalData(a<42,a)", //
        "IntervalData({-Infinity,Less,Less,42})");

  }

  public void testIntervalData() {
    check("IntervalData({-1/2, LessEqual, LessEqual, 1/2}, {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({-1/2,LessEqual,LessEqual,1/2},{2,Less,LessEqual,7/2})");
  }

  public void testPlus() {
    check(
        "IntervalData({-1/2, LessEqual, LessEqual, 1/2})+IntervalData( {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({3/2,Less,LessEqual,4})");
  }

  public void testTimes() {
    check(
        "IntervalData({-1/2, LessEqual, LessEqual, 1/2})*IntervalData( {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({-7/4,Less,LessEqual,7/4})");
    check("-1/2 * IntervalData( {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({-7/4,Less,LessEqual,-1})");
  }


  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
