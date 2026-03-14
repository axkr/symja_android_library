package org.matheclipse.core.system;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for DSolve */
public class LimitTest extends ExprEvaluatorTestCase {

  @Test
  public void testLimitFa() {
    check("Limit(f(a)^x,x->-Infinity)", //
        "Limit(f(a)^x,x->-Infinity)");
    check("Limit((a+f[b]+2)^x,x->Infinity)", //
        "Limit((2+a+f(b))^x,x->Infinity)");
    check("Limit(f(a)^x,x->Infinity)", //
        "Limit(f(a)^x,x->Infinity)");
  }

  @Test
  public void testLimitIndeterminate() {
    // gitlab #107
    check("Limit(x^2-1/x-2, x->0)", //
        "Indeterminate");
  }
  @Test
  public void testLimitExp() {
    check("Limit(1/Exp(-x + Exp(-x)) - Exp(x), x -> Infinity)", //
        "-1");
    // issue #931
    check("Limit((-1/E^x+E^x)/(E^x+E^(-x)),x -> Infinity)", //
        "1");
    check("Limit((-1+Sqrt(x))/Sqrt(-1+x), x -> Infinity)", //
        "1");
  }

  @Test
  public void testLimit() {

    check("Limit(Sin(1/x)^3,x->0)", //
        "Indeterminate");

    check("Limit(Sin(x)/x,x->Infinity)", //
        "0");

    check("Limit(a^(1/n),n->Infinity)", //
        "1");
    check("Limit((1+1/n)^n,n->Infinity)", //
        "E");
    check("Limit(Sin(x)/x,x->0)", //
        "1");


    check("Limit(Log(x+1)/x,x->Infinity)", //
        "0");
    check("Limit((E^x-1)/x,x->0)", //
        "1");
    check("Limit(Log(a,x)/x,x->Infinity)", //
        "0");

    check("Limit(a^x*x^p,x->Infinity)", //
        "ConditionalExpression(Infinity,p>0&&Log(a)>0)");
    check("Limit(x^p,x->Infinity)", //
        "ConditionalExpression(Infinity,p>0)");
    check("Limit(a^x,x->Infinity)", //
        "ConditionalExpression(Infinity,Log(a)>0)");

    check("Limit(SinIntegral(t*(1-w)),t->Infinity, Assumptions->w>1)", //
        "-Pi/2");
    check("Limit(t*(1-w),t->Infinity, Assumptions->w>1)", //
        "-Infinity");
    // check("Limit(-SinIntegral(t*(1-w))/2+SinIntegral(t*(1+w))/2,t->Infinity)", //
    // " ");

    check("Limit(7-2*x+4*x^2,x->Infinity)", //
        "Infinity");
    check("Limit(Sqrt(7-2*x+4*x^2),x->Infinity)", //
        "Infinity");
    check("Limit(1+2*x+Sqrt(7-2*x+4*x^2),x->Infinity)", //
        "Infinity");
    check("Limit((-6+6*x)/(1+2*x+Sqrt(7-2*x+4*x^2)),x->Infinity)", //
        "3/2");
    check("Limit(1-Sqrt(7-2*x+4*x^2)+2*x,x->Infinity)", //
        "3/2");


    check("Limit(x^2*Sin(1/x)^3,x->0)", //
        "0");
    check("Limit(x*Sin(1/x)^3,x->0)", //
        "0");
    check("Limit(Sin(1/x)^3,x->0)", //
        "Indeterminate");
    check("Limit(Abs(Sin(1/x)),x->0)", //
        "Indeterminate");
    check("Limit(x*Sin(1/x),x->0)", //
        "0");

    check("Limit(x^2*Sin(1/x),x->0)", //
        "0");

    // github #230
    check("Limit((Sqrt(((t+4)*(t-2)^4))/((3*t)-6)^2),t->2) ", //
        "Sqrt(2/3)/3");
    check("Limit((((t+4)*(t-2)^4) /((3*t)-6)^4),t->2) ", //
        "2/27");
    check("Limit((((t+4)*(t-2)^4) /((3*t)-6) ),t->2) ", //
        "0");
    check("Limit(Sqrt(((t+4)*(t-2)^4)) ,t->2) ", //
        "0");

    check("Limit(Tan(9/7)^x,x->-Infinity)", //
        "0");
    check("Limit(Sin(1/7)^x,x->-Infinity)", //
        "Infinity");
    check("Limit(a^x,x->-Infinity)", //
        "ConditionalExpression(0,Log(a)>0)");

    check("Limit(Tan(9/7)^x,x->Infinity)", //
        "Infinity");
    check("Limit(Sin(1/7)^x,x->Infinity)", //
        "0");
    check("Limit(a^x,x->Infinity)", //
        "ConditionalExpression(Infinity,Log(a)>0)");
    check("Limit((a+b+2)^x,x->Infinity)", //
        "ConditionalExpression(Infinity,Log(2+a+b)>0)");
    check("Limit(Gamma(1/t),t->Infinity)", //
        "Infinity");
    check("Limit(ArcTanh(x/Sqrt(4+3*x^2)) ,x->-Infinity)", //
        "-ArcTanh(1/Sqrt(3))");
    check("Limit(ArcTanh(x/Sqrt(4+3*x^2)) ,x->Infinity)", //
        "ArcTanh(1/Sqrt(3))");
    check("Limit(x/Sqrt(4+3*x^2) ,x->Infinity)", //
        "1/Sqrt(3)");
    check("Limit(x/Sqrt(4+3*x^2) ,x->-Infinity)", //
        "-1/Sqrt(3)");
    check("Limit((-x^2+1)/(x+2),x->Infinity)", //
        "-Infinity");
    check("Limit(Exp(2*x),x->-Infinity)", //
        "0");
    check("Limit((1+1/x)^x,x->Infinity)", //
        "E");
    check("Limit((1+2/x)^x,x->Infinity)", //
        "E^2");
    check("Limit((1+1/x)^(2*x),x->Infinity)", //
        "E^2");
    check("Limit((1+a*(1/x))^(b*x),x->(-Infinity))", //
        "E^(a*b)");
    check("Limit(-2*x,x->Infinity)", //
        "-Infinity");
    check("Limit((x^2+1)/(-x^3+1),x->Infinity)", //
        "0");
    check("Limit(1/x,x->0)", //
        "Indeterminate");
    check("Limit((Sin(x)-Tan(x))/(x^3),x->0)", //
        "-1/2");

    check("Limit((1+x)^(1/x),x->0)", //
        "E");
    check("Limit((x/(k+x))^x,x->Infinity)", //
        "E^(-k)");
    check("Limit(((a+x)/(b+x))^(c+x),x->Infinity)", //
        "E^(a-b)");
    check("Limit(((a+x)/(b+x))^(c+x),x->-Infinity)", //
        "E^(a-b)");
    check("Limit(((a+x)/(x))^(c+x),x->Infinity)", //
        "E^a");
    check("Limit(x^(a/x), x->Infinity)", //
        "ConditionalExpression(1,a∈Reals)");
    check("Limit(x^(4/x), x->Infinity)", //
        "1");

    check("Limit(Erf(x/Sqrt(2)),x->Infinity,Direction->Reals)", //
        "1");
    check("Limit(Erf(x/Sqrt(2)),x->-Infinity,Direction->Reals)", //
        "-1");
    check("Limit((Cosh(t)-1)/t^2,t->0)", //
        "1/2");
    check("Limit(Gamma(1/t)*Cos(Sin(1/t)),t->0)", //
        "Indeterminate");
    check("Limit(Gamma(1/t),t->Infinity)", //
        "Infinity");
    check("Limit(Gamma(1/t),t->-Infinity)", //
        "-Infinity");
    check("Limit(Gamma(z,t),t->Infinity)", //
        "0");
    check("Limit(Gamma(z,t),t->0)", //
        "Gamma(z)");
    check("limit((1 - cos(x))/x^2, x->0)", //
        "1/2");
    check("limit((1 + 1/n)^n, n->infinity)", //
        "E");
    check("Limit((sin(x) - x)/x^3,x->0)", //
        "-1/6");

    check("Limit(Sqrt(x^2 - 1)/x, x->-Infinity)", //
        "-1");
    check("Limit(x/Sqrt(x^2 - 1), x->-Infinity)", //
        "-1");


    check("Limit((x^2) /(3*x), x->Infinity)", //
        "Infinity");
    check("Limit(x^(-2/3),x->0 , Direction->-1)", //
        "Infinity");
    // TODO
    check("Limit(x^(-2/3),x->0 , Direction->1)", //
        "Infinity");
    check("Limit(x^(-2/3),x->0)", //
        "Indeterminate");

    check("Limit(x^(-16/7),x->0 , Direction->-1)", //
        "Infinity");
    // TODO
    check("Limit(x^(-16/7),x->0 , Direction->1)", //
        "Infinity");
    check("Limit(x^(-16/7),x->0)", //
        "Indeterminate");

    check("Limit(x^(-37/4),x->0 , Direction->-1)", //
        "Infinity");
    // TODO
    check("Limit(x^(-37/4),x->0 , Direction->1)", //
        "Limit(1/x^(37/4),x->0,Direction->1)");
    check("Limit(x^(-37/4),x->0)", //
        "Indeterminate");

    check("Limit((x^2-1)/(x-1)^2, x->1)", //
        "Indeterminate");
    check("Limit((3*x^2-6)^(-1/3), x->-Infinity)", //
        "0");

    check("Limit(Cosh(x) , x->3)", //
        "Cosh(3)");
    check("Limit(x^3-4*x^2+6, x->-Infinity)", //
        "-Infinity");
    check("Limit(42, x->Infinity)", //
        "42");
    check("Limit(x^2-x^4, x->Infinity)", //
        "-Infinity");
    check("Limit((4*x^3-3*x+2)/(2*x^3+2*x-1), x->Infinity)", //
        "2");
    check("Limit((x^2-3*x+2)/(x^3+2*x-1), x->Infinity)", //
        "0");
    check("Limit(Sqrt(3*x-2), x->-Infinity) // FullForm", //
        "DirectedInfinity(Complex(0,1))");

    check("Limit((x-1)^2/(x^2-1), x->1)", //
        "0");
    check("Limit((x-1)/(x^2-1), x->1)", //
        "1/2");
    check("Limit((x^2-4)/(x-2), x->2)", //
        "4");
    check("Limit((x^3-1)/(x^2-1), x->1)", //
        "3/2");

    // github #120
    check("Limit( x*Log(x) , x->0)", //
        "0");
    check("Limit(Log(x),x->0)", //
        "-Infinity");
    check("Limit(Log(x)^2,x->0)", //
        "Infinity");
    check("Limit(2*x-2*x*Log(x)+x*Log(x)^2, x->0)", //
        "0");
    check("Limit(E^(-x)*Sqrt(x), x -> Infinity)", //
        "0");

    // adjust LimitRules.m if these 2 tests fails
    // check("FullForm(x*(Sqrt(2*Pi*x)/(x!))^(1/x) )", //
    // "Times(Power(Power(Times(2, Pi), Rational(1,2)), Power(x, -1)), x, Power(Times(Power(x,
    // Rational(1,2)),
    // Power(Factorial(x), -1)), Power(x, -1)))");
    // check("Limit(x*(Sqrt(2*Pi*x)/(x!))^(1/x), x->Infinity)", //
    // "E");
    // check("Limit(x/((x!)^(1/x)), x->Infinity)", //
    // "E");

    // github #115
    check("Limit(Sqrt(-4+2*x^2)/(4+3*x),x->Infinity)", //
        "Sqrt(2)/3");
    check("Limit((4+3*x)/Sqrt(-4+2*x^2),x->Infinity)", //
        "3/Sqrt(2)");
    check("Limit((4+3*x)^2/(-4+2*x^2),x->Infinity)", //
        "9/2");

    check("Limit(x^(13+n),x->0)", //
        "ConditionalExpression(0,n>-13)");
    // check("Limit(x^(13+n)/a,x->0)", //
    // "");

    check("Limit(E^(3*x), x->a)", //
        "E^(3*a)");

    check("Limit((1+k/x)^x, x->Infinity)", //
        "E^k");
    check("Limit((1-1/x)^x, x->Infinity)", //
        "1/E");
    // check("Limit((1 + Sinh(x))/E^x, x ->Infinity)", "Infinity*Limit(E^(-x),x->Infinity)");

    // issue #184
    check("N(Limit(tan(x),x->pi/2))", //
        "Indeterminate");

    check("Limit(Tan(x), x->Pi/2)", //
        "Indeterminate");
    check("Limit(Tan(x), x->Pi/2, Direction->1)", //
        "Infinity");
    check("Limit(Tan(x), x->Pi/2, Direction->-1)", //
        "-Infinity");
    check("Limit(Tan(x+3*Pi), x->Pi/2)", //
        "Indeterminate");
    check("Limit(Tan(x+3*Pi), x->Pi/2, Direction->1)", //
        "Infinity");
    check("Limit(Tan(x+3*Pi), x->Pi/2, Direction->-1)", //
        "-Infinity");
    check("Limit(Cot(x), x->0)", //
        "Indeterminate");
    check("Limit(Cot(x), x->0, Direction->1)", //
        "-Infinity");
    check("Limit(Cot(x), x->0, Direction->-1)", //
        "Infinity");
    check("Limit(Cot(x+Pi), x->0)", //
        "Indeterminate");
    check("Limit(Cot(x+Pi), x->0, Direction->1)", //
        "-Infinity");
    check("Limit(Cot(x+Pi), x->0, Direction->-1)", //
        "Infinity");

    check("Limit(Log(x^y), x->0)", //
        "ConditionalExpression(-Infinity,y>0)");
    check("Limit(Log(y*x, b), x->1)", //
        "Log(b)/Log(y)");
    check("Limit(Log(y*x), x->0)", //
        "-Infinity");
    check("Limit(Log(x), x->Infinity)", //
        "Infinity");
    check("Limit(Log(x), x->-Infinity)", //
        "Infinity");
    check("Limit((y*x)/Abs(x), x->0)", //
        "Indeterminate");
    check("Limit((y*x)/Abs(x), x->0, Direction->1)", //
        "-y");
    check("Limit(x/Abs(x), x->0)", //
        "Indeterminate");
    check("Limit(x/Abs(x), x->0, Direction->-1)", //
        "1");
    check("Limit(x/Abs(x), x->0, Direction->1)", //
        "-1");
    check("Limit(Log(x), x -> 0)", //
        "-Infinity");
    check("Limit(x^x, x -> 0)", //
        "1");
    check("Limit(1/x, x -> Infinity, Direction->1)", //
        "0");
    check("Limit(1/x, x -> Infinity, Direction->-1)", //
        "0");
    check("Limit(1/x, x -> 0, Direction->1)", //
        "-Infinity");
    check("Limit(1/x, x -> 0, Direction->-1)", //
        "Infinity");

    // print additional message. Messages are typically suppressed in Limit() steps.
    check("1/0", //
        "ComplexInfinity");

    // check("Limit((4 - x), x -> 4)", "0");
    check("Limit(1/(4 - x), x -> 4)", //
        "Indeterminate");
    check("Limit(1/(x - 4), x -> 4)", //
        "Indeterminate");

    check("Infinity-1", //
        "Infinity");
    check("Limit(a+b+2*x,x->-Infinity)", //
        "-Infinity");
    check("Limit(a+b+2*x,x->Infinity)", //
        "Infinity");
    check("Limit(E^(-x)*Sqrt(x), x -> Infinity)", //
        "0");
    check("Limit(Sin(x)/x,x->0)", //
        "1");
    check("Limit(x*Sin(1/x),x->Infinity)", //
        "1");

    check("Limit(-x,x->Infinity)", //
        "-Infinity");
    check("Limit((1 + x/n)^n, n -> Infinity)", //
        "E^x");
    check("Limit((x^2 - 2*x - 8)/(x - 4), x -> 4)", //
        "6");
    check("Limit((x^3-1)/(2*x^3-3*x),x->Infinity)", //
        "1/2");
    check("Limit((x^3-1)/(2*x^3+3*x),x->Infinity)", //
        "1/2");

    check("Limit((2*x^3-3*x),x->Infinity)", //
        "Infinity");
    check("Limit((2*x^3+3*x),x->Infinity)", //
        "Infinity");

    check("Limit(E^x, x->Infinity)", //
        "Infinity");
    check("Limit(E^x, x->-Infinity)", //
        "0");
    check("Limit(a^x, x->0)", //
        "1");
    check("Limit(c*(x^(-10)), x->Infinity)", //
        "0");
  }

  @Test
  public void testLimitIssue1001() {
    // github issue #1001
    check("Simplify(RealAbs(x + 2)/(x+2))", //
        "Piecewise({{-1,x<-2}},1)");
    check("Simplify(RealAbs(x - 2)/(x-2))", //
        "Piecewise({{-1,x<2}},1)");
    check("Limit(RealAbs(x + 2), x -> -2, Direction -> -1)", //
        "0");
    check("Limit(x + 2, x -> -2, Direction -> -1)", //
        "0");
    check("Limit(RealAbs(x + 2)/(x+2), x -> -2, Direction -> -1)", //
        "1");
    check("Limit(RealAbs(x + 2)/(x+2), x -> -2, Direction -> 1)", //
        "-1");
    check("Limit(RealAbs(x + 2)/(x+2), x -> -2)", //
        "Indeterminate");
  }

  @Test
  public void testLimitPiecewise001() {
    check("f(x_):=Piecewise({{2*x+3,x<5},{-x+12,x>5}});", //
        "");
    check("Limit(f[x], x -> 5)", //
        "Indeterminate");
    check("Limit(RealAbs(x + 2)/(x+2), x -> -2)", //
        "Indeterminate");


    check("Limit(f[x], x -> 5, Direction -> -1)", //
        "7");
    check("Limit(f[x], x -> 5, Direction -> 1)", //
        "13");
    check("Limit(f[x], x -> 5, Direction -> \"FromBelow\")", //
        "13");

  }

  @Test
  public void testLimitIssue175() {
    // TODO github #175
    // check("Limit(((a^(1/x)+b^(1/x))/2)^x, x->Infinity)", //
    // "Sqr(a)*Sqrt(b)");
  }

  @Test
  public void testLimitIssue536() {
    // avoid endless recursion:
    // check("Limit(Sqrt((4+x)/(4-x))-Pi/2,x->4)", //
    // "Indeterminate");
    // // TODO get -4*Pi
    // check(
    // "Limit((-4+x)*(Sqrt((4+x)/(4-x))-ArcTan(Sqrt((4+x)/(4-x)))+(-(4+x)*ArcTan(Sqrt((4+x)/(4-x))))/(4-x)),
    // x->4)", //
    // "Indeterminate");

    // Issue 536
    check("Integrate(Sqrt((4+x)/(4-x)), x) ", //
        "(-4+x)*(Sqrt((4+x)/(4-x))-ArcTan(Sqrt((4+x)/(4-x)))+(-(4+x)*ArcTan(Sqrt((4+x)/(4-x))))/(\n"//
            + "4-x))");

    check("Limit(ArcTan(Sqrt((4 + x)/(4 - x))),x->4)", //
        "Pi/2");
    check("Limit(Sqrt((4 + x)/(4 - x)),x->4,Direction->1)", //
        "Infinity");
    check("Limit(Sqrt((4 + x)/(4 - x)),x->4,Direction->-1)", //
        "I*Infinity");
    check("Limit(Sqrt((4 + x)/(4 - x)),x->4,Direction->\"FromBelow\")", //
        "Infinity");
    check("Limit(Sqrt((4 + x)/(4 - x)),x->4,Direction->\"FromAbove\")", //
        "I*Infinity");
    check("Limit(ArcTan(Sqrt((4 + x)/(4 - x))),x->4,Direction->1)", //
        "Pi/2");
    check("Limit(ArcTan(Sqrt((4 + x)/(4 - x))),x->4,Direction->-1)", //
        "Pi/2");
  }

  @Test
  public void testLimitGruntzMRV() {
    check("Limit(((1/Abs(n)))^(1/n),n->Infinity)", //
        "1");

    // Classic Gruntz test: Exp(x) grows faster than x, so the inner term approaches -Infinity
    check("Limit(Exp(x - Exp(x)), x -> Infinity)", //
        "0");

    // L'Hopital loop traps (Gruntz handles these via MRV identification)
    check("Limit((Exp(x) + x) / (Exp(x) - x), x -> Infinity)", //
        "1");
    check("Limit((Exp(x) - x) / Exp(x), x -> Infinity)", //
        "1");

    // Extreme growth races (Exp vs Polynomial)
    // TODO
    // check("Limit(Exp(x) / x^100, x -> Infinity)", //
    // "Infinity");
    // TODO
    // check("Limit(x^100 / Exp(x), x -> Infinity)", //
    // "0");

    // // Nested exponentials
    // TODO
    // check("Limit(Exp(Exp(x)) / Exp(Exp(x-1)), x -> Infinity)", //
    // "Infinity");
    check("Limit(Exp(Exp(x)) / Exp(Exp(x) + Exp(x)), x -> Infinity)", //
        "0");
    check("Limit(Exp(Exp(x) + x) / Exp(Exp(x)), x -> Infinity)", //
        "Infinity");

    // // Logarithm races (Logs grow slower than any polynomial)
    check("Limit(Log(x + 1) / Log(x), x -> Infinity)", //
        "1");
    check("Limit(Log(Log(x)) / Log(x), x -> Infinity)", //
        "0");
    // TODO
    // check("Limit(Log(x)^100 / x, x -> Infinity)", //
    // "0");

    // // Mixed growth classes
    check("Limit((Log(x) + Exp(x)) / (x + Exp(x)), x -> Infinity)", //
        "1");

    // // Sub-exponential growth
    check("Limit(Exp(Sqrt(x)) / x, x -> Infinity)", //
        "Infinity");
    // TODO
    // check("Limit(Exp(Log(x)^2) / x, x -> Infinity)", //
    // "Infinity");
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
