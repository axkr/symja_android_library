package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class IntervalDataTest extends ExprEvaluatorTestCase {

  @Test
  public void testInterval001() {
    check("Interval({-Infinity,0})-Log(3)", //
        "Interval({-Infinity,-Log(3)})");
    check("Interval({-1,1})+Log(3)", //
        "Interval({-1+Log(3),1+Log(3)})");
  }

  @Test
  public void testIntervalData001() {
    check("IntervalData({-Infinity,Less,LessEqual,0})-Log(3)", //
        "IntervalData({-Infinity,Less,LessEqual,-Log(3)})");
    check("IntervalData({-Infinity,Less,Less,0})-Log(3)", //
        "IntervalData({-Infinity,Less,Less,-Log(3)})");
    check("1+IntervalData({-1,Less,Less,1})", //
        "IntervalData({0,Less,Less,2})");
    check("IntervalData({-1,Less,Less,1})+Log(3)", //
        "IntervalData({-1+Log(3),Less,Less,1+Log(3)})");
    check("IntervalData({0,LessEqual,LessEqual,2},Sqrt(2))", //
        "IntervalData({0,LessEqual,LessEqual,2})");
  }

  @Test
  public void testIntervalDataCos() {
    check("Cos(IntervalData({-Infinity,Less,Less,0}))", //
        "IntervalData({-1,LessEqual,LessEqual,1})");

    check("Cos(IntervalData({0,Less,Less,Pi/2}))", //
        "IntervalData({0,Less,Less,1})");
    check("Cos(IntervalData({0,LessEqual,Less,Pi/2}))", //
        "IntervalData({0,Less,LessEqual,1})");
    check("Cos(IntervalData({0,Less,LessEqual,Pi/2}))", //
        "IntervalData({0,LessEqual,Less,1})");
    check("Cos(IntervalData({0,LessEqual,LessEqual,Pi/2}))", //
        "IntervalData({0,LessEqual,LessEqual,1})");

    check("Cos(IntervalData({Pi/2,Less,Less,Pi}))", //
        "IntervalData({-1,Less,Less,0})");
    check("Cos(IntervalData({Pi/2,LessEqual,Less,Pi}))", //
        "IntervalData({-1,Less,LessEqual,0})");
    check("Cos(IntervalData({Pi/2,Less,LessEqual,Pi}))", //
        "IntervalData({-1,LessEqual,Less,0})");
    check("Cos(IntervalData({Pi/2,LessEqual,LessEqual,Pi}))", //
        "IntervalData({-1,LessEqual,LessEqual,0})");

    check("Cos(IntervalData({Pi,Less,Less,3/2*Pi}))", //
        "IntervalData({-1,Less,Less,0})");
    check("Cos(IntervalData({Pi,LessEqual,Less,3/2*Pi}))", //
        "IntervalData({-1,LessEqual,Less,0})");
    check("Cos(IntervalData({Pi,Less,LessEqual,3/2*Pi}))", //
        "IntervalData({-1,Less,LessEqual,0})");
    check("Cos(IntervalData({Pi,LessEqual,LessEqual,3/2*Pi}))", //
        "IntervalData({-1,LessEqual,LessEqual,0})");
  }

  @Test
  public void testIntervalDataSin() {
    check("Sin(IntervalData({-Infinity,Less,Less,0}))", //
        "IntervalData({-1,LessEqual,LessEqual,1})");

    check("Sin(IntervalData({0,Less,Less,Pi/2}))", //
        "IntervalData({0,Less,Less,1})");
    check("Sin(IntervalData({0,LessEqual,Less,Pi/2}))", //
        "IntervalData({0,LessEqual,Less,1})");
    check("Sin(IntervalData({0,Less,LessEqual,Pi/2}))", //
        "IntervalData({0,Less,LessEqual,1})");
    check("Sin(IntervalData({0,LessEqual,LessEqual,Pi/2}))", //
        "IntervalData({0,LessEqual,LessEqual,1})");

    check("Sin(IntervalData({Pi/2,Less,Less,Pi}))", //
        "IntervalData({0,Less,Less,1})");
    check("Sin(IntervalData({Pi/2,LessEqual,Less,Pi}))", //
        "IntervalData({0,Less,LessEqual,1})");
    check("Sin(IntervalData({Pi/2,Less,LessEqual,Pi}))", //
        "IntervalData({0,LessEqual,Less,1})");
    check("Sin(IntervalData({Pi/2,LessEqual,LessEqual,Pi}))", //
        "IntervalData({0,LessEqual,LessEqual,1})");

    check("Sin(IntervalData({Pi,Less,Less,3/2*Pi}))", //
        "IntervalData({-1,Less,Less,0})");
    check("Sin(IntervalData({Pi,LessEqual,Less,3/2*Pi}))", //
        "IntervalData({-1,Less,LessEqual,0})");
    check("Sin(IntervalData({Pi,Less,LessEqual,3/2*Pi}))", //
        "IntervalData({-1,LessEqual,Less,0})");
    check("Sin(IntervalData({Pi,LessEqual,LessEqual,3/2*Pi}))", //
        "IntervalData({-1,LessEqual,LessEqual,0})");
  }

  @Test
  public void testIntervalPlus() {
    check("Interval({2,7})*x+Interval({3,5})*x", //
        "Interval({5,12})*x");
  }

  @Test
  public void testIntervalDataPlus() {
    check("Interval({2,7})*x+IntervalData({3,LessEqual,Less,5})*x", //
        "IntervalData({5,LessEqual,Less,12})*x");
    check("IntervalData({2,Less,Less,7})*x+IntervalData({3,LessEqual,Less,5})*x", //
        "IntervalData({5,Less,Less,12})*x");
  }

  @Test
  public void testIntervalDataTimes() {
    check("Interval({2,7})*y^2*IntervalData({3,LessEqual,Less,5})*x^3", //
        "IntervalData({6,LessEqual,Less,35})*x^3*y^2");
    check("IntervalData({2,Less,Less,7})*y^2*IntervalData({3,LessEqual,Less,5})*x^3", //
        "IntervalData({6,Less,Less,35})*x^3*y^2");
  }


  @Test
  public void testIntervalPower() {
    check("Interval({-3,2})^4", //
        "Interval({0,81})");
  }

  @Test
  public void testIntervalDataArccos() {
    check("ArcCos(IntervalData({-1,LessEqual,LessEqual,1/2}))", //
        "IntervalData({Pi/3,LessEqual,LessEqual,Pi})");
  }

  @Test
  public void testIntervalDataPower() {
    check("IntervalData({-3,LessEqual,LessEqual,2})^4", //
        "IntervalData({0,LessEqual,LessEqual,81})");
  }

  @Test
  public void testEqual() {
    assertEquals(//
        EvalEngine.get().evaluate(//
            F.Equal(F.IntervalData(F.List(F.ZZ(2), S.Less, S.LessEqual, F.ZZ(5))), //
                F.IntervalData(F.List(F.ZZ(2), S.Less, S.LessEqual, F.ZZ(5)))))
            .isTrue(),
        true);
    check("Equal(IntervalData({2,Less,Less, 5}), IntervalData({2,Less,Less, 5}))", //
        "True");
  }

  @Test
  public void testToIntervalData() {
    check("ToIntervalData(Reals)", //
        "IntervalData({-Infinity,Less,Less,Infinity})");
    check("ToIntervalData(a<42,a)", //
        "IntervalData({-Infinity,Less,Less,42})");

    check("ToIntervalData(a>17||a<42,a)", //
        "IntervalData({-Infinity,Less,Less,Infinity})");
    check("ToIntervalData(a>17&&a<42,a)", //
        "IntervalData({17,Less,Less,42})");
  }

  @Test
  public void testIntervalComplement1() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(3))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(2)))));

    IExpr iExprExpected = F.IntervalData(F.List(F.ZZ(2), F.Less, F.LessEqual, F.ZZ(3)));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement2() {
    IExpr interval1 =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(3), F.LessEqual, F.LessEqual, F.ZZ(3))),
            F.IntervalData(F.List(F.ZZ(4), F.LessEqual, F.LessEqual, F.ZZ(4)))));
    System.out.println(interval1.toString());
    IExpr interval2 =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.ZZ(4), F.LessEqual, F.LessEqual, F.ZZ(4))),
            F.IntervalData(F.List(F.ZZ(3), F.LessEqual, F.LessEqual, F.ZZ(3)))));
    System.out.println(interval2.toString());
    IExpr iExprResult = F.eval(F.IntervalComplement(interval1, interval2));

    IExpr iExprExpected = F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1)));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement3() {
    IExpr interval1 =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(3), F.LessEqual, F.LessEqual, F.ZZ(3))),
            F.IntervalData(F.List(F.ZZ(4), F.LessEqual, F.LessEqual, F.ZZ(4)))));
    System.out.println(interval1);
    IExpr iExprResult = F.eval(F.IntervalComplement(interval1,
        F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(3)))));

    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.Less, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(4), F.LessEqual, F.LessEqual, F.ZZ(4)))));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement4() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(5))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(4)))));

    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.Less, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(4), F.Less, F.LessEqual, F.ZZ(5)))));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement5() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.CInfinity)),
            F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.Less, F.CInfinity))));

    IExpr iExprExpected = F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.ZZ(0)));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement6() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(3))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(3)))));

    IExpr iExprExpected = F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.Less, F.ZZ(2)));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement7() {
    IExpr iExprResult =
        F.eval(F.IntervalComplement(F.IntervalData(F.List(F.ZZ(1), F.Less, F.LessEqual, F.ZZ(3))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(3)))));

    IExpr iExprExpected = F.IntervalData(F.List(F.ZZ(1), F.Less, F.Less, F.ZZ(2)));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement8() {
    IExpr iExprResult =
        F.eval(F.IntervalComplement(F.IntervalData(F.List(F.ZZ(1), F.Less, F.LessEqual, F.ZZ(3))),
            F.IntervalData(F.List(F.ZZ(2), F.Less, F.LessEqual, F.ZZ(3)))));
    IExpr iExprExpected = F.IntervalData(F.List(F.ZZ(1), F.Less, F.LessEqual, F.ZZ(2)));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement9() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1)))));
    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.Less, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(1), F.Less, F.LessEqual, F.ZZ(2)))));
    System.out.println(iExprExpected.toString());
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement10() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.CInfinity)),
            F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(1)))));
    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.ZZ(0))),
            F.IntervalData(F.List(F.ZZ(1), F.Less, F.Less, F.CInfinity))));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement11() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.CInfinity)),
            F.IntervalData(F.List(F.ZZ(0), F.Less, F.LessEqual, F.ZZ(1)))));
    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.CNInfinity, F.Less, F.LessEqual, F.ZZ(0))),
            F.IntervalData(F.List(F.ZZ(1), F.Less, F.Less, F.CInfinity))));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement12() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.CInfinity)),
            F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.Less, F.ZZ(1)))));
    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.ZZ(0))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.Less, F.CInfinity))));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement13() {
    IExpr iExprResult = F.eval(
        F.IntervalComplement(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.CInfinity)),
            F.IntervalData(F.List(F.ZZ(0), F.Less, F.Less, F.ZZ(1)))));
    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.CNInfinity, F.Less, F.LessEqual, F.ZZ(0))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.Less, F.CInfinity))));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalComplement14() {
    IExpr interval2 =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(3)))));
    System.out.println(interval2.toString());
    IExpr iExprResult = F.eval(F.IntervalComplement(
        F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.CInfinity)), interval2));
    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.CNInfinity, F.Less, F.Less, F.ZZ(0))),
            F.IntervalData(F.List(F.ZZ(1), F.Less, F.Less, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(3), F.Less, F.Less, F.CInfinity))));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }


  @Test
  public void testIntervalDataComplement() {
    check("IntervalComplement(IntervalData({-Infinity, LessEqual, LessEqual, Infinity})," //
        + "IntervalData({-Infinity, Less, Less, Infinity}))", //
        "IntervalData()");
    check("IntervalComplement(IntervalData({-Infinity, LessEqual, LessEqual, Infinity})," //
        + "IntervalData({-Infinity, LessEqual, LessEqual, Infinity}))", //
        "IntervalData()");

    check("IntervalComplement(IntervalData({-3/4, LessEqual, LessEqual, 3/4})," //
        + "IntervalData({-3/4, LessEqual, LessEqual, 3/4}))", //
        "IntervalData()");
    check("IntervalComplement(IntervalData()," //
        + "IntervalData({-42, LessEqual, LessEqual, 42}))", //
        "IntervalData()");
    check("IntervalComplement(IntervalData({-3/4, LessEqual, LessEqual, 3/4})," //
        + "IntervalData( ))", //
        "IntervalData({-3/4,LessEqual,LessEqual,3/4})");

    check("IntervalComplement(IntervalData({-3, Less, Less, 4})," //
        + "IntervalData({-3, LessEqual, LessEqual, 1}))", //
        "IntervalData({1,Less,Less,4})");
    check("IntervalComplement(IntervalData({-3, LessEqual, Less, 4})," //
        + "IntervalData({-3, Less, LessEqual, 1}))", //
        "IntervalData({-3,LessEqual,LessEqual,-3},{1,Less,Less,4})");

    check("IntervalComplement(IntervalData({-3, LessEqual, Less, 4})," //
        + "IntervalData({-3, LessEqual, LessEqual, 1}))", //
        "IntervalData({1,Less,Less,4})");


    check("IntervalComplement(IntervalData({-4, LessEqual, Less, 3})," //
        + "IntervalData({0, LessEqual, LessEqual, 3}))", //
        "IntervalData({-4,LessEqual,Less,0})");
    check("IntervalComplement(IntervalData({-4, LessEqual, LessEqual, 3})," //
        + "IntervalData({0, LessEqual, Less, 3}))", //
        "IntervalData({-4,LessEqual,Less,0},{3,LessEqual,LessEqual,3})");

    check("IntervalComplement(IntervalData({1/2, LessEqual, LessEqual, 3})," //
        + "IntervalData({0, LessEqual, LessEqual, 1}, {E, LessEqual, Less, 4}))", //
        "IntervalData({1,Less,Less,E})");
    check("IntervalComplement(ToIntervalData(Reals)," //
        + "IntervalData({0, LessEqual, LessEqual, 1}, {E, LessEqual, Less, 4}))", //
        "IntervalData({-Infinity,Less,Less,0},{1,Less,Less,E},{4,LessEqual,Less,Infinity})");
  }



  @Test
  public void testIntervalDataNonsense() {
    check("IntervalData(True,2+I,7*Sqrt(2))", //
        "IntervalData(True,2+I,7*Sqrt(2))");
  }

  @Test
  public void testIntervalData() {
    check("Infinity > Infinity", //
        "False");
    check("-Infinity > -Infinity", //
        "False");

    // print message IntervalData: The expression {Infinity,Less,Less,Infinity} is not a valid
    // interval.
    check("IntervalData({Infinity,Less,  Less, Infinity })", //
        "IntervalData({Infinity,Less,Less,Infinity})");
    // print message IntervalData: The expression {-Infinity,Less,Less,-Infinity} is not a valid
    // interval.
    check("IntervalData({-Infinity,Less,  Less, -Infinity })", //
        "IntervalData({-Infinity,Less,Less,-Infinity})");

    check("IntervalData({1,LessEqual,  Less, -1 })", //
        "IntervalData({-1,Less,LessEqual,1})");
    check("IntervalData({0,Less,  Less, 0 })", //
        "IntervalData()");



    check("IntervalData({1, LessEqual, LessEqual, 2}, {2, Less, LessEqual, 3})", //
        "IntervalData({1,LessEqual,LessEqual,3})");


    // print message IntervalData: The expression {-1/2,LessEqual} is not a valid interval.
    check("IntervalData({-1/2, LessEqual})", //
        "IntervalData({-1/2,LessEqual})");

    check("IntervalData({-1/2, LessEqual, LessEqual, 1/2}, {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({-1/2,LessEqual,LessEqual,1/2},{2,Less,LessEqual,7/2})");
  }

  @Test
  public void testIntervalMemberQ() {
    check("IntervalMemberQ(IntervalData({2,Less,Less, 5}), Pi)", //
        "True");
    check("IntervalMemberQ(IntervalData({2,Less,Less, Pi}), Pi)", //
        "False");
    check("IntervalMemberQ(IntervalData({2,Less,LessEqual, Pi}), Pi)", //
        "True");
  }

  @Test
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

  @Test
  public void testTimes() {
    check("-1/2 * Interval( {2, 3 + 1/2})", //
        "Interval({-7/4,-1})");
    check("-1/2 * IntervalData( {2, Less, LessEqual, 3 + 1/2})", //
        "IntervalData({-7/4,LessEqual,Less,-1})");

    check("IntervalData({1,Less,Less, 6}) * IntervalData({0, Less,Less,2})", //
        "IntervalData({0,Less,Less,12})");
    check(
        "IntervalData({-1/2, LessEqual, LessEqual, 1/2})*IntervalData( {2, Less, LessEqual, 3 +   1/2})", //
        "IntervalData({-7/4,LessEqual,LessEqual,7/4})");
    check(
        "IntervalData({-1/2, LessEqual, Less, 1/2})*IntervalData( {2, Less, LessEqual, 3 +   1/2})", //
        "IntervalData({-7/4,LessEqual,Less,7/4})");
    check("IntervalData({-1/2, Less, Less, 1/2})*IntervalData( {2, Less, Less, 3 +   1/2})", //
        "IntervalData({-7/4,Less,Less,7/4})");

  }


  @Test
  public void testIssue684_1() {
    EvalEngine engine = EvalEngine.get();
    IExpr iExprResult = engine
        .evaluate(F.IntervalIntersection(F.IntervalData(F.List(F.ZZ(0), F.Less, F.Less, F.num(2))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(2)))));
    IExpr iExprExpected =
        engine.evaluate(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.Less, F.ZZ(2))));
    assertTrue(engine.evaluate(F.Equal(iExprResult, iExprExpected)).isTrue());
  }

  @Test
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

  @Test
  public void testIssue684_3() {
    EvalEngine engine = EvalEngine.get();
    IExpr union = engine.evaluate(
        F.IntervalUnion(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(3)))));
    IExpr iExprResult = engine.evaluate(
        F.IntervalIntersection(union, F.IntervalData(F.List(F.ZZ(1), F.Less, F.Less, F.ZZ(2)))));
    assertTrue(iExprResult.isEmpty());
  }

  @Test
  public void testIssue684_4() {
    EvalEngine engine = EvalEngine.get();
    IExpr iExprResult = engine
        .evaluate(F.IntervalIntersection(F.IntervalData(F.List(F.ZZ(0), F.Less, F.Less, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1)))));
    assertTrue(iExprResult.isEmpty());
  }

  @Test
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

  @Test
  public void testIssue684_7() {
    EvalEngine engine = EvalEngine.get();
    IExpr iExprResult = engine.evaluate(
        F.IntervalUnion(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(1), F.Less, F.Less, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(2), F.LessEqual, F.LessEqual, F.ZZ(2)))));
    IExpr iExprExpected =
        engine.evaluate(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(2))));
    assertTrue(EvalEngine.get().evaluate(F.Equal(iExprResult, iExprExpected)).isTrue());
  }

  @Test
  public void testIssue684_8() {
    EvalEngine engine = EvalEngine.get();
    IExpr iExprResult = engine.evaluate(
        F.IntervalUnion(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(3))),
            F.IntervalData(F.List(F.ZZ(2), F.Less, F.Less, F.ZZ(3)))));
    IExpr iExprExpected =
        engine.evaluate(F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(3))));
    assertTrue(EvalEngine.get().evaluate(F.Equal(iExprResult, iExprExpected)).isTrue());
  }

  @Test
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

  @Test
  public void testIntervalUnionIssue684() {
    IExpr iExprResult =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.ZZ(3), F.Less, F.Less, F.ZZ(4))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.Less, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(7), F.LessEqual, F.Less, F.ZZ(8))),
            F.IntervalData(F.List(F.ZZ(6), F.LessEqual, F.LessEqual, F.ZZ(9))),
            F.IntervalData(F.List(F.ZZ(2), F.Less, F.LessEqual, F.ZZ(3))),
            F.IntervalData(F.List(F.CNInfinity, F.Less, F.LessEqual, F.ZZ(-1)))));
    System.out.println(iExprResult.toString());
    IExpr iExprExpected =
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.CNInfinity, F.Less, F.LessEqual, F.ZZ(-1))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.Less, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(2), F.Less, F.Less, F.ZZ(4))),
            F.IntervalData(F.List(F.ZZ(6), F.LessEqual, F.LessEqual, F.ZZ(9)))));
    assertEquals(iExprResult.toString(), iExprExpected.toString());
  }

  @Test
  public void testIntervalDataUnion() {
    check("IntervalUnion(IntervalData({1, Less, Less, 1})," //
        + "IntervalData({2, LessEqual, LessEqual, 3}))", //
        "IntervalData({2,LessEqual,LessEqual,3})");

    check("IntervalUnion(IntervalData({1, LessEqual, LessEqual, 3})," //
        + "IntervalData({2, Less, Less, 3}))", //
        "IntervalData({1,LessEqual,LessEqual,3})");

    check("IntervalUnion(IntervalData({1, LessEqual, LessEqual, 1} )," //
        + "IntervalData({1, Less, Less, 2} ),"//
        + "IntervalData({2, LessEqual, LessEqual, 2} ))", //
        "IntervalData({1,LessEqual,LessEqual,2})");

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
