package org.matheclipse.core.system;

import org.junit.Test;

public class IntervalTest extends ExprEvaluatorTestCase {

  @Test
  public void testIntersection() {
    check("Intersection({a,a,b,c})", //
        "{a,b,c}");
    check("Intersection({a,a,b,c},{b,a})", //
        "{a,b}");
    check(" Intersection({a, b, c}, {a}, {b})", //
        "{}");
    check(" Intersection({}, {a, b, c}, {a,b}, {b,c})", //
        "{}");

    check("Intersection(Quantity(1.2,\"m\"))", //
        "Intersection(1.2[m])");
  }

  @Test
  public void testInterval() {
    check("Sqrt(Interval({1.0,2.0}))", //
        "Interval({1.0,1.41421})");
    check("Sqrt(Interval({-1,1}))", //
        "Sqrt(Interval({-1,1}))");
    check("Interval({1,2})+Interval({3,4})", //
        "Interval({4,6})");
    check("Interval({1,2})*Interval({3,4})", //
        "Interval({3,8})");
    check("Interval({1,2})/Interval({3,4})", //
        "Interval({1/4,2/3})");
    check("Interval({1.0,2.0})^2", //
        "Interval({1.0,4.0})");
    check("Interval({0.1,10.0})^(3/4)", //
        "Interval({0.177828,5.62341})");
    check("Interval({0.1,10.0})^(4/3)", //
        "Interval({0.0464159,21.54435})");
    check("Interval({0.1,10.0})^0.75", //
        "Interval({0.177828,5.62341})");
    check("Interval({0.1,10.0})^1.33333", //
        "Interval({0.0464162,21.54418})");

    check("Interval({-2,5})", //
        "Interval({-2,5})");
    check("Interval({-2,5})^2", //
        "Interval({0,25})");
    check("1/Interval({-2,5})", //
        "Interval({-Infinity,-1/2},{1/5,Infinity})");
    check("Abs(Interval({-Infinity,-1/2},{1/5,Infinity}))", //
        "Interval({1/5,Infinity})");
    check("Solve(3*x+2==Interval({-2,5}),x)", //
        "{{x->Interval({-4/3,1})}}");
    check("Limit(Sin(1/x)+1/2*Cos(x), x->Infinity)", //
        "Interval({-1/2,1/2})");
    check("Limit(2*Sin(1/x)+1/2*Cos(x), x->0)", //
        "Indeterminate");
    check("Limit(Sin(1/x), x->0)", //
        "Indeterminate");

    check("Interval(0.0``40)", //
        "Interval({0,0})");
    check("Interval(N(Pi,60))+Pi", //
        "Interval({6.28318530717958647692528676655900576839433879875021164194987,6.28318530717958647692528676655900576839433879875021164194989})");
    check("Interval(1.0``40)", //
        "Interval({0.999999999999999999999999999999999999999,1.000000000000000000000000000000000000001})");

    check("1/Interval({-Infinity,Infinity})", //
        "Interval({-Infinity,Infinity})");
    check("1/Interval(-Infinity,Infinity)", //
        "Interval({0,0})");
    check("1/Interval(-Infinity,-1/2)", //
        "Interval({-2,-2},{0,0})");
    check("1/Interval(-Infinity,-2)", //
        "Interval({-1/2,-1/2},{0,0})");
    check("1/Interval(1/3,Infinity)", //
        "Interval({0,0},{3,3})");
    check("1/Interval(2,Infinity)", //
        "Interval({0,0},{1/2,1/2})");

    check("1/Interval({-Infinity,Infinity})", //
        "Interval({-Infinity,Infinity})");
    check("1/Interval({-Infinity,-1/2})", //
        "Interval({-2,0})");
    check("1/Interval({-Infinity,-2})", //
        "Interval({-1/2,0})");
    check("1/Interval({1/3,Infinity})", //
        "Interval({0,3})");
    check("1/Interval({2,Infinity})", //
        "Interval({0,1/2})");
    check("1/Interval({3/7,Infinity})", //
        "Interval({0,7/3})");

    // TODO return Interval({-1,1}) for -Infinity, Infinity
    check("Limit(Sin(1/x), x -> 0)", //
        "Indeterminate");

    check("Max(Interval({3, 5},{11, 37/2}))", //
        "37/2");
    check("Min(Interval({3, 5},{11, 37/2}))", //
        "3");
    check("1/Interval({-2, 5})", //
        "Interval({-Infinity,-1/2},{1/5,Infinity})");
    check("Abs(1/Interval({-2, 5}))", //
        "Interval({1/5,Infinity})");
    check("Solve(3*x + 2 == Interval({-2, 5}), x)", //
        "{{x->Interval({-4/3,1})}}");

    check("Interval({-1,1})", //
        "Interval({-1,1})");
    check("Cos(Interval({0, Pi}))", //
        "Interval({-1,1})");

    check("Interval(0.)", //
        "Interval({0.0,0.0})");
    check("Interval(0.1)-0.1", //
        "Interval({-1.38778*10^-17,1.38778*10^-17})");
    check("Interval(100.)-100.0", //
        "Interval({-1.42109*10^-14,1.42109*10^-14})");
    check("Sin(Interval(N(Pi)))", //
        "Interval({-3.21625*10^-16,5.66554*10^-16})");

    check("Sign(Interval({-Infinity, Infinity}))", //
        "Interval({-1,1})");
    check("Sign(Interval({-43, -42}))", //
        "-1");
    check("Sign(Interval({0,0}))", //
        "0");
    check("Sign(Interval({17,2^12}))", //
        "1");
    check("Sign(Interval({0,-14}))", //
        "Interval({-1,0})");

    check("Conjugate(Interval({-Infinity, Infinity}))", //
        "Interval({-Infinity,Infinity})");

    check("Re(Interval({-43, -42}))", //
        "Interval({-43,-42})");
    check("Re(Interval({3/4, 13/3}))", //
        "Interval({3/4,13/3})");
    check("Re(Interval({-2, Infinity}))", //
        "Interval({-2,Infinity})");
    check("Re(Interval({1, Infinity}))", //
        "Interval({1,Infinity})");
    check("Re(Interval({-Infinity, Infinity}))", //
        "Interval({-Infinity,Infinity})");

    check("Im(Interval({-43, -42}))", //
        "0");
    check("Im(Interval({-Infinity, Infinity}))", //
        "0");

    check("IntegerPart(Interval({-Infinity,Infinity}))", //
        "Interval({-Infinity,Infinity})");

    check("Floor(Interval({-1/3, 3/4}))", //
        "Interval({-1,0})");
    check("Floor(Interval({-Infinity, 3/4}))", //
        "Interval({-Infinity,0})");

    check("Abs(Interval({-43, -42}))", //
        "Interval({42,43})");
    check("Abs(Interval({3/4, 13/3}))", //
        "Interval({3/4,13/3})");
    check("Abs(Interval({-2, Infinity}))", //
        "Interval({0,Infinity})");
    check("Abs(Interval({1, Infinity}))", //
        "Interval({1,Infinity})");
    check("Abs(Interval({-Infinity, Infinity}))", //
        "Interval({0,Infinity})");

    check("Cosh(Interval({-1, 1}))", //
        "Interval({1,Cosh(1)})");
    check("Cosh(Interval({0, Log(3)}))", //
        "Interval({1,5/3})");

    check("Coth(Interval({-1, 1}))", //
        "Interval({-Infinity,-Coth(1)},{Coth(1),Infinity})");
    check("Coth(Interval({1.0, 2.0}))", //
        "Interval({1.03731,1.31304})");
    check("Coth(Interval({-1.0, 2.0}))", //
        "Interval({-Infinity,-1.31304},{1.03731,Infinity})");

    check("Csch(Interval({-1, 1}))", //
        "Interval({-Infinity,-Csch(1)},{Csch(1),Infinity})");
    check("Csch(Interval({1.0, 2.0}))", //
        "Interval({0.275721,0.850918})");
    check("Csch(Interval({-1.0, 2.0}))", //
        "Interval({-Infinity,-0.850918},{0.275721,Infinity})");

    check("Sech(Interval({-1, 1}))", //
        "Interval({Sech(1),1})");
    check("Sech(Interval({1.0, 2.0}))", //
        "Interval({0.265802,0.648054})");
    check("Sech(Interval({-1.0, 2.0}))", //
        "Interval({0.265802,1})");

    check("Sinh(Interval({-1, 1}))", //
        "Interval({-Sinh(1),Sinh(1)})");
    check("Sinh(Interval({0, Log(3)}))", //
        "Interval({0,4/3})");

    check("Tanh(Interval({-1, 1}))", //
        "Interval({-Tanh(1),Tanh(1)})");
    check("Tanh(Interval({-1.0, 2.0}))", //
        "Interval({-0.761594,0.964028})");

    check("ArcCot(Interval({-1, Infinity}))", //
        "Interval({-Pi/2,-Pi/4},{0,Pi/2})");
    check("ArcCot(Interval({1-Sqrt(2), 1+Sqrt(2)}))", //
        "Interval({-Pi/2,-3/8*Pi},{Pi/8,Pi/2})");
    check("ArcCot(Interval({1, Infinity}))", //
        "Interval({0,Pi/4})");
    check("ArcCot(Interval({-Pi,-1}))", //
        "Interval({-Pi/4,-ArcCot(Pi)})");

    check("ArcTan(Interval({-1, 1-Sqrt(2)}))", //
        "Interval({-Pi/4,-Pi/8})");

    check("ArcSin(Interval({-1/2,1/2}))", //
        "Interval({-Pi/6,Pi/6})");
    check("ArcSin(Interval({-1,1}))", //
        "Interval({-Pi/2,Pi/2})");

    check("ArcCos(Interval({-1/2,1/2}))", //
        "Interval({Pi/3,2/3*Pi})");
    check("ArcCos(Interval({-1,1}))", //
        "Interval({0,Pi})");

    check("ArcCosh(Interval({-1/2,1/2}))", //
        "ArcCosh(Interval({-1/2,1/2}))");
    check("ArcCosh(Interval({2,42}))", //
        "Interval({ArcCosh(2),ArcCosh(42)})");

    check("ArcSinh(Interval({-1, Infinity}))", //
        "Interval({-ArcSinh(1),Infinity})");
    check("ArcSinh(Interval({1-Sqrt(2), 1+Sqrt(2)}))", //
        "Interval({ArcSinh(1-Sqrt(2)),ArcSinh(1+Sqrt(2))})");
    check("ArcSinh(Interval({1, Infinity}))", //
        "Interval({ArcSinh(1),Infinity})");
    check("ArcSinh(Interval({-Pi,-1}))", //
        "Interval({-ArcSinh(Pi),-ArcSinh(1)})");

    check("ArcTanh(Interval({-1/2,1/2}))", //
        "Interval({-ArcTanh(1/2),ArcTanh(1/2)})");
    check("ArcTanh(Interval({-1,1}))", //
        "Interval({-Infinity,Infinity})");
    check("Csc(Interval({Pi/4,3*Pi/5}))", //
        "Interval({1,Sqrt(2)})");
    check("Csc(Interval({-Pi/4,2*Pi/3}))", //
        "Interval({-Infinity,-Sqrt(2)},{1,Infinity})");
    check("Csc(Interval({4,7}))", //
        "Interval({-Infinity,-1},{Csc(7),Infinity})");
    check("Csc(Interval({4,8}))", //
        "Interval({-Infinity,-1},{1,Infinity})");
    check("Csc(Interval({2,3}))", //
        "Interval({Csc(2),Csc(3)})");
    check("Csc(Interval({Pi/4,5*Pi/4}))", //
        "Interval({-Infinity,-Sqrt(2)},{1,Infinity})");
    check("Csc(Interval({Pi/4,5*Pi/2}))", //
        "Interval({-Infinity,-1},{1,Infinity})");

    check("Cot(Interval({3*Pi/4,6*Pi/5}))", //
        "Interval({-Infinity,-1},{Sqrt(1+2/Sqrt(5)),Infinity})");
    check("Cot(Interval({Pi/4,3*Pi/4}))", //
        "Interval({-1,1})");
    check("Cot(Interval({-Pi/4,2*Pi/3}))", //
        "Interval({-Infinity,-1},{-1/Sqrt(3),Infinity})");
    check("Cot(Interval({4,7}))", //
        "Interval({-Infinity,Cot(4)},{Cot(7),Infinity})");
    check("Cot(Interval({4,8}))", //
        "Interval({-Infinity,Infinity})");
    check("Cot(Interval({2,3}))", //
        "Interval({Cot(3),Cot(2)})");
    check("Cot(Interval({Pi/4,5*Pi/4}))", //
        "Interval({-Infinity,Infinity})");
    check("Cot(Interval({Pi/4,5*Pi/2}))", //
        "Interval({-Infinity,Infinity})");

    check("Sec(Interval({Pi/4,3*Pi/5}))", //
        "Interval({-Infinity,4/(1-Sqrt(5))},{Sqrt(2),Infinity})");
    check("Sec(Interval({-Pi/4,2*Pi/3}))", //
        "Interval({-Infinity,-2},{1,Infinity})");
    check("Sec(Interval({4,7}))", //
        "Interval({-Infinity,Sec(4)},{1,Infinity})");
    check("Sec(Interval({4,8}))", //
        "Interval({-Infinity,Sec(4)},{1,Infinity})");
    check("Sec(Interval({2,3}))", //
        "Interval({Sec(2),Sec(3)})");
    check("Sec(Interval({Pi/4,5*Pi/4}))", //
        "Interval({-Infinity,-1},{Sqrt(2),Infinity})");
    check("Sec(Interval({Pi/4,5*Pi/2}))", //
        "Interval({-Infinity,-1},{1,Infinity})");

    check("Tan(Interval({Pi/4,3*Pi/5}))", //
        "Interval({-Infinity,-Sqrt(5+2*Sqrt(5))},{1,Infinity})");
    check("Tan(Interval({-Pi/4,2*Pi/3}))", //
        "Interval({-Infinity,-Sqrt(3)},{-1,Infinity})");
    check("Tan(Interval({4,7}))", //
        "Interval({-Infinity,Tan(7)},{Tan(4),Infinity})");
    check("Tan(Interval({4,8}))", //
        "Interval({-Infinity,Infinity})");
    check("Tan(Interval({2,3}))", //
        "Interval({Tan(2),Tan(3)})");
    check("Tan(Interval({Pi/4,5*Pi/4}))", //
        "Interval({-Infinity,Infinity})");
    check("Tan(Interval({Pi/4,5*Pi/2}))", //
        "Interval({-Infinity,Infinity})");

    check("Cos(Interval({0, Pi}))", //
        "Interval({-1,1})");
    check("Cos(Interval({0, 2*Pi}))", //
        "Interval({-1,1})");
    check("Cos(Interval({Pi/2, (3/2)*Pi}))", //
        "Interval({-1,0})");
    check("Cos(Interval({(3/4)*Pi, (7/4)*Pi}))", //
        "Interval({-1,1/Sqrt(2)})");
    check("Cos(Interval({2, 8}))", //
        "Interval({-1,1})");
    check("Cos(Interval({2, 7}))", //
        "Interval({-1,1})");
    check("Cos(Interval({2, 10}))", //
        "Interval({-1,1})");

    check("Cos(Interval({3, 4}))", //
        "Interval({-1,Cos(4)})");
    check("Cos(Interval({3, 5}))", //
        "Interval({-1,Cos(5)})");
    check("Cos(Interval({4, 7}))", //
        "Interval({Cos(4),1})");
    // https://en.wikipedia.org/wiki/Interval_arithmetic
    check("Sin(Interval({0, Pi}))", //
        "Interval({0,1})");
    check("Sin(Interval({0, 2*Pi}))", //
        "Interval({-1,1})");
    check("Sin(Interval({Pi/2, (3/2)*Pi}))", //
        "Interval({-1,1})");
    check("Sin(Interval({(3/4)*Pi, (7/4)*Pi}))", //
        "Interval({-1,1/Sqrt(2)})");
    check("Sin(Interval({2, 8}))", //
        "Interval({-1,1})");
    check("Sin(Interval({2, 7}))", //
        "Interval({-1,Sin(2)})");
    check("Sin(Interval({2, 10}))", //
        "Interval({-1,1})");

    check("Sin(Interval({3, 4}))", //
        "Interval({Sin(4),Sin(3)})");
    check("Sin(Interval({3, 5}))", //
        "Interval({-1,Sin(3)})");
    check("Sin(Interval({4, 7}))", //
        "Interval({-1,Sin(7)})");

    // independent intervals otherwise result should be Interval({-1/4,2})
    check("test = Interval({-1, 1}); test^2+test", //
        "Interval({-1,2})");

    check("Sin(Interval({2.5, 5.5}))", //
        "Interval({-1,0.598472})");

    check("Interval({6,9},{12,14})*Interval({5,8},{11,13})", //
        "Interval({30,117},{132,182})");
    check("3/4*Interval({5,8},{11,13})", //
        "Interval({15/4,6},{33/4,39/4})");

    check("Interval({6,9},{12,14})+Interval({5,8},{11,13})", //
        "Interval({11,22},{23,27})");
    check("3/4+Interval({5,8},{11,13})", //
        "Interval({23/4,35/4},{47/4,55/4})");

    check("Log(Interval({11,Infinity},{7,4}))", "Interval({Log(4),Log(7)},{Log(11),Infinity})");
    check("Log(Interval({3,Infinity},{-7,-4}))", "Log(Interval({-7,-4},{3,Infinity}))");
    check("Interval({3,Infinity},{-7,-4})", "Interval({-7,-4},{3,Infinity})");

    check("(0)^Interval({2,4},{-42,43})", "Indeterminate");

    check("(0)^Interval({2,4},{-42,43})", "Indeterminate");
    check("(0)^Interval({2,4},{42,43})", "Interval({0,0})");

    check("Interval({-7,11},{27,31},{1,17})", //
        "Interval({-7,17},{27,31})");
    check("Interval({-7,11},{9,13},{1,17})", //
        "Interval({-7,17})");
    check("Interval({7,11},{9,13},{1,17})", //
        "Interval({1,17})");
    check("Interval({7,11},{9,13})", //
        "Interval({7,13})");
    check("Interval({7,11},{9,10})", //
        "Interval({7,11})");

    check("(1/2)^Interval({-3, 4},{42, 43})", //
        "Interval({1/8796093022208,1/4398046511104},{1/16,8})");

    check("E^Interval({3, 4},{42, 43})", //
        "Interval({E^3,E^4},{E^42,E^43})");
    check("(-Pi)^Interval({-3, 4},{42, 43})", //
        "(-1)^Interval({-3,4},{42,43})*Interval({1/Pi^3,Pi^4},{Pi^42,Pi^43})");

    check("Interval({-Infinity,Infinity})^2", //
        "Interval({0,Infinity})");

    check("Interval({-2, 5})^(-2)", //
        "Interval({1/25,Infinity})");
    check("Interval({0, 0})^(-1)", //
        "Interval({-Infinity,Infinity})");
    check("Interval({-2, 0})^(-1)", //
        "Interval({-Infinity,-1/2})");
    check("Interval({-2, 1})^(-1)", //
        "Interval({-Infinity,-1/2},{1,Infinity})");
    check("Interval({-2, 5})^(-1)", //
        "Interval({-Infinity,-1/2},{1/5,Infinity})");

    check("Interval({-2, 5})^2", //
        "Interval({0,25})");
    check("Interval({-7, 5})^2", //
        "Interval({0,49})");
    check("Interval({-2, 5})^2", //
        "Interval({0,25})");
    check("Interval({2, 5})^2", //
        "Interval({4,25})");
    check("Interval({-2, 5})^3", //
        "Interval({-8,125})");
    check("Interval({-10, -5})^2", //
        "Interval({25,100})");

    check("Interval(42,43,44)", //
        "Interval({42,42},{43,43},{44,44})");

    check("Interval({3,-1})", //
        "Interval({-1,3})");
    check("Interval({-1,1})/Infinity", //
        "0");
    check("Interval({1,1})", //
        "Interval({1,1})");

    check("Interval({1.5, 6}) * Interval({0.1, 2.7})", //
        "Interval({0.15,16.2})");
    check("Interval({1, 6}) * Interval({0, 2})", //
        "Interval({0,12})");
    check("Interval({1, 6}) + Interval({0, 2})", //
        "Interval({1,8})");
    check("Pi>3", //
        "True");
    check("3>Pi", //
        "False");
    check("Pi<3", //
        "False");
    check("3<Pi", //
        "True");
    check("Pi>=3", //
        "True");
    check("3>=Pi", //
        "False");
    check("Pi<=3", //
        "False");
    check("3<=Pi", //
        "True");

    check("Interval({5,8})>2", //
        "True");
    check("Interval({3,4})>Pi", //
        "Interval({3,4})>Pi");
    check("Interval({1,2})>Pi", //
        "False");
    check("Interval({5,8})<2", //
        "False");
    check("Interval({3,4})<Pi", //
        "Interval({3,4})<Pi");
    check("Interval({1,2})<Pi", //
        "True");
    check("Interval({5,8})>=2", //
        "True");
    check("Interval({3,4})>=Pi", //
        "Interval({3,4})>=Pi");
    check("Interval({1,2})>=Pi", //
        "False");
    check("Interval({5,8})<=2", //
        "False");
    check("Interval({3,4})<=Pi", //
        "Interval({3,4})<=Pi");
    check("Interval({1,2})<=Pi", //
        "True");

    check("Interval({5,8})>Interval({1,2})", //
        "True");
    check("Interval({3,4})>Interval({Pi,5})", //
        "Interval({3,4})>Interval({Pi,5})");
    check("Interval({1,2})>Interval({Pi,5})", //
        "False");
    check("Interval({5,8})<Interval({1,2})", //
        "False");
    check("Interval({3,4})<Interval({Pi,5})", //
        "Interval({3,4})<Interval({Pi,5})");
    check("Interval({1,2})<Interval({Pi,5})", //
        "True");

    check("Limit(Sin(x),x->Infinity)", //
        "Interval({-1,1})");
    check("Limit(Sin(x),x->-Infinity)", //
        "Interval({-1,1})");
    check("Limit(Sin(1/x),x->0)", //
        "Indeterminate");
    check("Max(Interval({2,4}))", //
        "4");
    check("Min(Interval({2,4}))", //
        "2");
    check("Max(Interval({-43/3,4}))", //
        "4");
    check("Min(Interval({-43/3,-4}))", //
        "-43/3");
    check("u=Interval({-1,1});u+u^2", //
        "Interval({-1,2})");
  }

  @Test
  public void testIntervalMemberQ() {
    check("IntervalMemberQ(Interval({4,6}), 2*E)", //
        "True");
    check("IntervalMemberQ(Interval({4,6}), 5.5)", //
        "True");
    check("IntervalMemberQ(Interval({4,10}), Interval({2*Pi, 3*Pi}))", //
        "True");
    check("IntervalMemberQ(Interval({4,10}), Interval({2*Pi, 4*Pi}))", //
        "False");
    check("IntervalMemberQ(Interval({4,6}), Interval({4, 6}))", //
        "True");
    check("IntervalMemberQ(Interval({3,7}), Interval({4, 6}))", //
        "True");
  }

  @Test
  public void testIntervalMemberQFloatingPoint() {
    // only floating-point values in intervals define a (small) extended interval
    check("IntervalMemberQ(Interval(1), Interval(1.0))", //
        "False");
    check("IntervalMemberQ(Interval(1.0), Interval(1))", //
        "True");
  }

  @Test
  public void testIntervalIntersection() {
    check("IntervalIntersection(Interval({-2, 3}), Interval({1, 4}))", //
        "Interval({1,3})");
    check("IntervalIntersection(Interval({-2, 0}), Interval({1, 4}))", //
        "Interval()");
    check("IntervalIntersection(Interval({-2, Pi}), Interval({E, 4}))", //
        "Interval({E,Pi})");
    check(
        "IntervalIntersection(Interval({1, 2}, {3, 4}, {5, 7}, {8, 8.5}), Interval({1.5, 3.5}, {4.1, 6}, {9, 10}))", //
        "Interval({1.5,2},{3,3.5},{5,6})");
  }

  @Test
  public void testIntervalUnion() {
    check("IntervalUnion(Interval({-2, 3}), Interval({1, 4}))", //
        "Interval({-2,4})");
    check("IntervalUnion(Interval({-2, 0}), Interval({1, 4}))", //
        "Interval({-2,0},{1,4})");
    check("IntervalUnion(Interval({-2, Pi}), Interval({E, 4}))", //
        "Interval({-2,4})");
    check("IntervalUnion(Interval({-2, E}), Interval({Pi, 4}))", //
        "Interval({-2,E},{Pi,4})");
    check(
        "IntervalUnion(Interval({1, 2}, {3, 4}, {5, 7}, {8, 8.5}), Interval({1.5, 3.5}, {4.1, 6}, {9, 10}))", //
        "Interval({1,4},{4.1,7},{8,8.5},{9,10})");
  }
}
