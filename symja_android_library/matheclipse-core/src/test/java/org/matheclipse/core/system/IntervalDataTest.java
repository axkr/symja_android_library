package org.matheclipse.core.system;

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

    check("ToIntervalData(a>17||a<42,a)", //
        "IntervalData({17,Less,Less,Infinity})||IntervalData({-Infinity,Less,Less,42})");
  }

  public void testIntervalData() {
    check("IntervalData({-1/2, LessEqual, LessEqual, 1/2}, {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({-1/2,LessEqual,LessEqual,1/2},{2,Less,LessEqual,7/2})");
  }

  public void testIntervalMemberQ() {
    check("IntervalMemberQ(IntervalData({2,Less,Less, 5}), Pi)", //
        "True");
    check("IntervalMemberQ(IntervalData({2,Less,Less, Pi}), Pi)", //
        "False");
    check("IntervalMemberQ(IntervalData({2,Less,LessEqual, Pi}), Pi)", //
        "True");
  }

  public void testPlus() {
    check(
        "IntervalData({-1/2, LessEqual, LessEqual, 1/2})+IntervalData( {2, LessEqual, LessEqual, 3 + 1/2})", //
        "IntervalData({3/2,LessEqual,LessEqual,4})");
    check(
        "IntervalData({-1/2, LessEqual, LessEqual, 1/2})+IntervalData( {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({3/2,Less,LessEqual,4})");
    check("IntervalData({-1/2, LessEqual, Less, 1/2})+IntervalData( {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({3/2,Less,Less,4})");
    check("IntervalData({-1/2, Less, Less, 1/2})+IntervalData( {2, Less, Less, 3 + 1/2})", //
        "IntervalData({3/2,Less,Less,4})");
  }

  public void testTimes() {
    check(
        "IntervalData({-1/2, LessEqual, LessEqual, 1/2})*IntervalData( {2, Less, LessEqual, 3 +   1/2})", //
        "IntervalData({-7/4,LessEqual,LessEqual,7/4})");
    check(
        "IntervalData({-1/2, LessEqual, Less, 1/2})*IntervalData( {2, Less, LessEqual, 3 +   1/2})", //
        "IntervalData({-7/4,LessEqual,Less,7/4})");
    check("IntervalData({-1/2, Less, Less, 1/2})*IntervalData( {2, Less, Less, 3 +   1/2})", //
        "IntervalData({-7/4,Less,Less,7/4})");
    check("-1/2 * IntervalData( {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({-7/4,LessEqual,Less,-1})");
  }

  public void testIntervalDataIntersection() {
    check(
        "IntervalIntersection(IntervalData({-1/2, LessEqual, LessEqual, 1/2}, {2, Less, LessEqual, 3 + 1/2})," //
            + "IntervalData({0, LessEqual, LessEqual, 1}, {E, LessEqual, Less, 4}))", //
        "IntervalData({0,LessEqual,LessEqual,1/2},{E,LessEqual,LessEqual,7/2})");
  }

  public void testIntervalDataUnion() {
    check(
        "IntervalUnion(IntervalData({-1/2, LessEqual, LessEqual, 1/2}, {2, Less, LessEqual, 3 + 1/2})," //
            + "IntervalData({0, LessEqual, LessEqual, 1}, {E, LessEqual, Less, 4}))", //
        "IntervalData({-1/2,LessEqual,LessEqual,1},{2,Less,Less,4})");
  }

}
