package org.matheclipse.core.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** Tests for compiler functions */
public class IntervalDataTest extends ExprEvaluatorTestCase {

  public IntervalDataTest(String name) {
    super(name);
  }

  public void testEqual() {
    System.out.println( //
        EvalEngine.get().evaluate(//
            F.Equal(F.IntervalData(F.List(F.ZZ(2), S.Less, S.LessEqual, F.ZZ(5))), //
                F.IntervalData(F.List(F.ZZ(2), S.Less, S.LessEqual, F.ZZ(5)))))
            .isTrue() //
    );
    check("Equal(IntervalData({2,Less,Less, 5}), IntervalData({2,Less,Less, 5}))", //
        "True");
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
    check("IntervalData({1, LessEqual, LessEqual, 2}, {2, Less, LessEqual, 3})", //
        "IntervalData({1,LessEqual,LessEqual,3})");


    // print message IntervalData: The expression {-1/2,LessEqual} is not a valid interval.
    check("IntervalData({-1/2, LessEqual})", //
        "IntervalData({-1/2,LessEqual})");

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


  public void testIssue684_1() {
    EvalEngine engine = EvalEngine.get();
    IExpr iExprResult = engine
        .evaluate(F.IntervalIntersection(F.IntervalData(F.List(F.ZZ(0), F.Less, F.Less, F.num(2))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(2)))));
    IExpr iExprExpected =
        engine.evaluate(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.Less, F.ZZ(2))));
    assertTrue(engine.evaluate(F.Equal(iExprResult, iExprExpected)).isTrue());
  }

  public void testIssue684_2() {
    EvalEngine engine = EvalEngine.get();
    IExpr union = engine.evaluate(
        F.IntervalUnion(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(3)))));
    IExpr iExprResult = engine.evaluate(F.IntervalIntersection(
        F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(2))), union));
    // IExpr iExprExpected = engine.evaluate(
    // F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(1))),
    // F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(2)))));

    assertTrue(engine.evaluate(
        F.Equal(iExprResult, F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(2)))))
        .isTrue());
  }

  public void testIssue684_3() {
    EvalEngine engine = EvalEngine.get();
    IExpr union = engine.evaluate(
        F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(3)))));
    IExpr iExprResult = engine.evaluate(
        F.IntervalIntersection(union, F.IntervalData(F.List(F.ZZ(1), F.Less, F.Less, F.ZZ(2)))));
    assertTrue(iExprResult.isEmpty());
  }

  public void testIssue684_4() {
    EvalEngine engine = EvalEngine.get();
    IExpr iExprResult = engine
        .evaluate(F.IntervalIntersection(F.IntervalData(F.List(F.ZZ(0), F.Less, F.Less, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1)))));
    assertTrue(iExprResult.isEmpty());
  }

  public void testIssue684_5() {
    EvalEngine engine = EvalEngine.get();
    IExpr union = engine.evaluate(
        F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(3)))));
    IExpr iExprResult = engine.evaluate(F.IntervalIntersection(
        F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(2))), union));
    IExpr iExprExpected = engine.evaluate(
        F.IntervalUnion(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(2)))));
    assertTrue(EvalEngine.get().evaluate(F.Equal(iExprResult, iExprExpected)).isTrue());
  }

  // TODO implement IntervalComplement
  // public void testIssue684_6() {
  // EvalEngine engine = EvalEngine.get();
  // IExpr iExprResult = engine.evaluate(F.IntervalComplement(F.ToIntervalData(S.Reals),
  // F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(2)))));
  // IExpr iExprExpected = engine
  // .evaluate(F.IntervalUnion(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.ZZ(1))),
  // F.IntervalData(F.List(F.ZZ(2), F.Less, F.Less, F.CInfinity))));
  // assertTrue(EvalEngine.get().evaluate(F.Equal(iExprResult, iExprExpected)).isTrue());
  // }

  public void testIntervalDataIntersection() {
    check("IntervalIntersection(IntervalData({1, LessEqual, LessEqual, 2})," //
        + "IntervalData({0,LessEqual,LessEqual,1},{2,LessEqual,LessEqual,3}))", //
        "IntervalData({1,LessEqual,LessEqual,1},{2,LessEqual,LessEqual,2})");
    check("IntervalIntersection(IntervalData({0,LessEqual,LessEqual,1},{2,LessEqual,LessEqual,3})," //
        + "IntervalData({1, LessEqual, LessEqual, 2}))", //
        "IntervalData({1,LessEqual,LessEqual,1},{2,LessEqual,LessEqual,2})");

    check("IntervalIntersection(IntervalData({0,LessEqual,LessEqual,1},{2,LessEqual,LessEqual,3})," //
        + "IntervalData({1, Less, Less, 2}))", //
        "IntervalData()");

    check("IntervalIntersection(IntervalData({0, Less, Less, 2.0})," //
        + "IntervalData({1, LessEqual, LessEqual, 2}))", //
        "IntervalData({1,LessEqual,Less,2.0})");

    check("IntervalIntersection(IntervalData({1, LessEqual, Less, 2.0})," //
        + "IntervalData({1.0, Less, LessEqual, 2}))", //
        "IntervalData({1.0,Less,Less,2.0})");
    check("IntervalIntersection(IntervalData({0, Less, Less, 2.0})," //
        + "IntervalData({1, LessEqual, LessEqual, 2}))", //
        "IntervalData({1,LessEqual,Less,2.0})");

  }

  public void testIntervalDataUnion() {
    check(
        "IntervalUnion(IntervalData({-1/2, LessEqual, LessEqual, 1/2}, {2, Less, LessEqual, 3 + 1/2})," //
            + "IntervalData({0, LessEqual, LessEqual, 1}, {E, LessEqual, Less, 4}))", //
        "IntervalData({-1/2,LessEqual,LessEqual,1},{2,Less,Less,4})");

    check("IntervalUnion(IntervalData({1, LessEqual, Less, 2.0})," //
        + "IntervalData({1.0, Less, LessEqual, 2}))", //
        "IntervalData({1,LessEqual,LessEqual,2})");
    check("IntervalUnion(IntervalData({0, Less, Less, 2.0})," //
        + "IntervalData({1, LessEqual, LessEqual, 2}))", //
        "IntervalData({0,Less,LessEqual,2})");
  }

}
