package org.matheclipse.core.sympy.printing;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestJSCode extends ExprEvaluatorTestCase {

  @Test
  public void test_printmethod() {
    // assert jscode(Abs(x)) == "Math.abs(x)"
    check("JSForm(Abs(x))", //
        "Math.abs(x)");
  }

  @Test
  public void test_jscode_sqrt() {
    // assert jscode(sqrt(x)) == "Math.sqrt(x)"
    check("JSForm(Sqrt(x))", //
        "Math.sqrt(x)");
    // assert jscode(x**0.5) == "Math.sqrt(x)"
    check("JSForm(x^0.5)", //
        "Math.sqrt(x)");
    // assert jscode(x**(S.One/3)) == "Math.cbrt(x)"
    check("JSForm(x^(1/3))", //
        "Math.cbrt(x)");
  }

  @Test
  public void test_jscode_pow() {
    // g = implemented_function('g', Lambda(x, 2*x))
    // assert jscode(x**3) == "Math.pow(x, 3)"
    check("JSForm(x^3)", //
        "Math.pow(x,3)");

    // assert jscode(x**(y**3)) == "Math.pow(x, Math.pow(y, 3))"
    check("JSForm(x^(y^3))", //
        "Math.pow(x,Math.pow(y,3))");

    // assert jscode(1/(g(x)*3.5)**(x - y**x)/(x**2 + y)) == \
    // "Math.pow(3.5*2*x, -x + Math.pow(y, x))/(Math.pow(x, 2) + y)"
    // check("JSForm(x^(y^3))", //
    // "");

    // assert jscode(x**-1.0) == '1/x'
    check("JSForm(x^(-1.0))", //
        "1.0/(x)");
  }

  @Test
  public void test_jscode_constants_mathh() {
    // assert jscode(exp(1)) == "Math.E"
    check("JSForm(Exp(1))", //
        "Math.E");

    // assert jscode(pi) == "Math.PI"
    check("JSForm(Pi)", //
        "Math.PI");

    // assert jscode(oo) == "Number.POSITIVE_INFINITY"
    check("JSForm(Infinity)", //
        "Number.POSITIVE_INFINITY");

    // assert jscode(-oo) == "Number.NEGATIVE_INFINITY"
    check("JSForm(-Infinity)", //
        "Number.NEGATIVE_INFINITY");
  }

  @Test
  public void test_jscode_constants_other() {
    // assert jscode(2*GoldenRatio) == "var GoldenRatio = %s;\n2*GoldenRatio" %
    // GoldenRatio.evalf(17)
    check("JSForm(2*GoldenRatio)", //
        "2*(1.618033988749895)");
    // assert jscode(2*Catalan) == "var Catalan = %s;\n2*Catalan" % Catalan.evalf(17)
    check("JSForm(2*Catalan)", //
        "2*(0.915965594177219)");
    // assert jscode(
    // 2*EulerGamma) == "var EulerGamma = %s;\n2*EulerGamma" % EulerGamma.evalf(17)
    check("JSForm(2*EulerGamma)", //
        "2*(0.5772156649015329)");
  }

  @Test
  public void test_jscode_Rational() {
    check("JSForm(3/7)", //
        "0.42857142857142855");
    check("JSForm(18/9)", //
        "2");
    check("JSForm(3/-7)", //
        "-0.42857142857142855");
    check("JSForm(-3/-7)", //
        "0.42857142857142855");
  }

  @Test
  public void test_Relational() {
    // assert jscode(Eq(x, y)) == "x == y"
    check("JSForm(x==y)", //
        "x==y");

    // assert jscode(Ne(x, y)) == "x != y"
    check("JSForm(x!=y)", //
        "x!=y");

    // assert jscode(Le(x, y)) == "x <= y"
    check("JSForm(x<=y)", //
        "x<=y");

    // assert jscode(Lt(x, y)) == "x < y"
    check("JSForm(x<y)", //
        "x<y");

    // assert jscode(Gt(x, y)) == "x > y"
    check("JSForm(x>y)", //
        "x>y");

    // assert jscode(Ge(x, y)) == "x >= y"
    check("JSForm(x>=y)", //
        "x>=y");
  }

  @Test
  public void test_Mod() {
    // assert jscode(Mod(x, y)) == '((x % y) + y) % y'
    check("JSForm(Mod(x,y))", //
        "(((x % y) + y) % y)");
    // assert jscode(Mod(x, x + y)) == '((x % (x + y)) + (x + y)) % (x + y)'
    check("JSForm(Mod(x,x + y))", //
        "(((x % (x+y)) + (x+y)) % (x+y))");

    // p1, p2 = symbols('p1 p2', positive=True)
    ISymbol p1 = F.symbol("p1", F.Greater(F.Slot1, F.C0));
    ISymbol p2 = F.symbol("p2", F.Greater(F.Slot1, F.C0));
    // assert jscode(Mod(p1, p2)) == 'p1 % p2'
    check("JSForm(Mod(p1, p2))", //
        "(p1 % p2)");

    // assert jscode(Mod(p1, p2 + 3)) == 'p1 % (p2 + 3)'
    check("JSForm(Mod(p1, p2+3))", //
        "(p1 % (3+p2))");
    // assert jscode(Mod(-3, -7, evaluate=False)) == '(-3) % (-7)'
    check("JSForm(Hold(Mod(-3, -7)))", //
        "((-3) % (-7))");
    // assert jscode(-Mod(p1, p2)) == '-(p1 % p2)'
    check("JSForm(-Mod(p1, p2))", //
        "-(p1 % p2)");
    // assert jscode(x*Mod(p1, p2)) == 'x*(p1 % p2)'
    check("JSForm(x*Mod(p1, p2))", //
        "x*(p1 % p2)");
  }

  @Test
  public void test_jscode_Integer() {
    // assert jscode(Integer(67)) == "67"
    check("JSForm(67)", //
        "67");
    // assert jscode(Integer(-1)) == "-1"
    check("JSForm(-1)", //
        "-1");

  }

  @Test
  public void test_jscode_functions() {
    // assert jscode(sin(x) ** cos(x)) == "Math.pow(Math.sin(x), Math.cos(x))"
    check("JSForm(Sin(x)^Cos(x))", //
        "Math.pow(Math.sin(x),Math.cos(x))");

    // assert jscode(sinh(x) * cosh(x)) == "Math.sinh(x)*Math.cosh(x)"
    check("JSForm(Sinh(x)*Cosh(x))", //
        "Math.cosh(x)*Math.sinh(x)");

    // assert jscode(Max(x, y) + Min(x, y)) == "Math.max(x, y) + Math.min(x, y)"
    check("JSForm(Max(x, y) + Min(x, y))", //
        "Math.max(x,y)+Math.min(x,y)");

    // assert jscode(tanh(x)*acosh(y)) == "Math.tanh(x)*Math.acosh(y)"
    check("JSForm(Tanh(x)*ArcCosh(x))", //
        "Math.acosh(x)*Math.tanh(x)");

    // assert jscode(asin(x)-acos(y)) == "-Math.acos(y) + Math.asin(x)"
    check("JSForm(ArcSin(x)-ArcCos(x))", //
        "-Math.acos(x)+Math.asin(x)");
  }

  @Test
  public void test_jscode_exceptions() {
    // assert jscode(ceiling(x)) == "Math.ceil(x)"
    check("JSForm(Ceiling(x))", //
        "Math.ceil(x)");
    // assert jscode(Abs(x)) == "Math.abs(x)"
    check("JSForm(Abs(x))", //
        "Math.abs(x)");
  }

  @Test
  public void test_jscode_boolean() {
    // assert jscode(x & y) == "x && y"
    check("JSForm(x&&y)", //
        "x&&y");

    // assert jscode(x | y) == "x || y"
    check("JSForm(x||y)", //
        "x||y");

    // assert jscode(~x) == "!x"
    check("JSForm(!x)", //
        "!x");

    // assert jscode(x & y & z) == "x && y && z"
    check("JSForm(x&&y&&z)", //
        "x&&y&&z");

    // assert jscode(x | y | z) == "x || y || z"
    check("JSForm(x||y||z)", //
        "x||y||z");

    // assert jscode((x & y) | z) == "z || x && y"
    check("JSForm((x&&y)||z)", //
        "x&&y||z");

    // assert jscode((x | y) & z) == "z && (x || y)"
    check("JSForm((x || y) && z)", //
        "(x||y)&&z");
  }

}
