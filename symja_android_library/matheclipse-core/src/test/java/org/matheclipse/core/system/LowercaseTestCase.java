package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatMath;
import org.apfloat.OverflowException;
import org.junit.Ignore;
import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.ast.ASTNode;

/** Tests built-in functions */
public class LowercaseTestCase extends ExprEvaluatorTestCase {

  @Test
  public void test001() {
    // syntax error in relaxed mode
    // check("Sin[x]", "");
    check("1.2345678*^5", //
        "123456.8");
    check("1.2345678*^6", //
        "1.23457*10^6");
    check("1.2345678*^7", //
        "1.23457*10^7");
    check("1.23456789*^8", //
        "1.23457*10^8");
    // github #66
    Double d = Double.parseDouble("1231231236123216361256312631627.12312312");
    checkNumeric("1231231236123216361256312631627.12312312", //
        d.toString());
    checkNumeric("N(1231231236123216361256312631627.12312312,50)", //
        "1.2312312361232163E30");

    check("f[[1,2]]", //
        "f[[1,2]]");
    check("-cos(x)", //
        "-Cos(x)");
    check("expand((a+b)^3)", //
        "a^3+3*a^2*b+3*a*b^2+b^3");
    check("expand((a+b)^8)", //
        "a^8+8*a^7*b+28*a^6*b^2+56*a^5*b^3+70*a^4*b^4+56*a^3*b^5+28*a^2*b^6+8*a*b^7+b^8");
    check("expand((a+b+c)^3)", //
        "a^3+3*a^2*b+3*a*b^2+b^3+3*a^2*c+6*a*b*c+3*b^2*c+3*a*c^2+3*b*c^2+c^3");

    check("f(g(x),{x,-1.5`,1.5`},{y,-1.5`,1.5`})", //
        "f(g(x),{x,-1.5,1.5},{y,-1.5,1.5})");
  }

  @Test
  public void testTildeOperator() {
    check("a~b~c", //
        "b(a,c)");
    check("a~b~c~d~e", //
        "d(b(a,c),e)");
  }

  @Test
  public void testAbort() {
    check("Print(\"a\"); Abort(); Print(\"b\")", //
        "$Aborted");
  }

  @Test
  public void testAbs() {
    check("Abs((0.5)^z)", //
        "0.5^Re(z)");
    check("Abs(2^z*Log(2*z))", //
        "2^Re(z)*Abs(Log(2*z))");
    check("Abs(I^(2*Pi))", //
        "1");
    check("Abs(Undefined)", //
        "Undefined");
    check("Abs(Indeterminate)", //
        "Indeterminate");
    // Integer.MIN_VALUE
    check("Abs(-2147483648)", //
        "2147483648");
    // Long.MIN_VALUE
    check("Abs(-9223372036854775808)", //
        "9223372036854775808");
    // Integer.MIN_VALUE
    check("Abs(11/-2147483648 )", //
        "11/2147483648");
    // Long.MIN_VALUE
    check("Abs(-9223372036854775808/11)", //
        "9223372036854775808/11");

    check("Abs(x*Sign(x))", //
        "Abs(x*Sign(x))");
    check("Abs(Abs(x))", //
        "Abs(x)");
    check("Abs(E-Pi)", //
        "-E+Pi");
    check("Abs(x^2)", //
        "Abs(x)^2");
    check("Abs(1/2*x)", //
        "Abs(x)/2");
    check("Abs(1/2*E^(\\[ImaginaryJ]*\\[Pi]/4))", //
        "1/2");
    check("Abs(-x^(a + b))", //
        "Abs(x^(a+b))");
    check("Abs(-x)", //
        "Abs(x)");
    check("Abs(Conjugate(z))", //
        "Abs(z)");
    check("Abs(3*a*b*c)", //
        "3*Abs(a*b*c)");
    // check("Abs(x^(-3))", "1/Abs(x)^3");

    check("Abs((1+I)/Sqrt(2))", //
        "1");
    check("Abs(0)", //
        "0");
    check("Abs(10/3)", //
        "10/3");
    check("Abs(-10/3)", //
        "10/3");
    check("Abs(Indeterminate)", //
        "Indeterminate");
    check("Abs(Infinity)", //
        "Infinity");
    check("Abs(-1*Infinity)", //
        "Infinity");
    check("Abs(ComplexInfinity)", //
        "Infinity");
    check("Abs(I*Infinity)", //
        "Infinity");
    check("Abs(Sqrt(Pi))", //
        "Sqrt(Pi)");
    check("Abs(-3*Sqrt(Pi))", //
        "3*Sqrt(Pi)");

    check("Abs(E)", //
        "E");
  }

  @Test
  public void testAbsArg() {
    check("AbsArg(z)", //
        "{Abs(z),Arg(z)}");
    check("AbsArg(-2*z)", //
        "{2*Abs(z),Arg(-z)}");
    check("AbsArg(2*z)", //
        "{2*Abs(z),Arg(z)}");
    check("AbsArg({a, {b, c}})", //
        "{{Abs(a),Arg(a)},{{Abs(b),Arg(b)},{Abs(c),Arg(c)}}}");
    check("AbsArg({{1, -1, 0}, {0, 1}})", //
        "{{{1,0},{1,Pi},{0,0}},{{0,0},{1,0}}}");

    check("AbsArg(Gamma(-1/2))", //
        "{2*Sqrt(Pi),Pi}");

    check("AbsArg({1, I, 0})", //
        "{{1,0},{1,Pi/2},{0,0}}");
    check("AbsArg(z) /. z -> {1, I, 0}", //
        "{{1,1,0},{0,Pi/2,0}}");
  }

  @Test
  public void testAbsoluteTiming() {
    // check("AbsoluteTiming(x = 1; Pause(x); x + 3)[[1]]>1", //
    // "True");
    // check("Timing(x = 1; Pause(x); x + 3)[[1]]<1", //
    // "True");
  }

  @Test
  public void testAbsoluteCorrelation() {
    check("AbsoluteCorrelation({5, 3/4, 1}, {2, 1/2, 1})", //
        "91/24");
    check("AbsoluteCorrelation({1.5, 3, 5, 10}, {2, 1.25, 15, 8})", //
        "40.4375");
    check("AbsoluteCorrelation(N({1, 2, 5, 6}, 20), N({2, 3, 6, 8}, 20))", //
        "21.5");
    check("AbsoluteCorrelation({2 + I, 3 - 2*I, 5 + 4* I}, {I, 1 + 2*I, 10 - 5*I})", //
        "10+I*55/3");
  }

  @Test
  public void testAccumulate() {
    check("Accumulate({{a, b}, {c, d}, {e, f}})", //
        "{{a,b},{a+c,b+d},{a+c+e,b+d+f}}");
    check("Accumulate({})", //
        "{}");
    check("Accumulate({a})", //
        "{a}");
    check("Accumulate({a, b})", //
        "{a,a+b}");
    check("Accumulate({a, b, c, d})", //
        "{a,a+b,a+b+c,a+b+c+d}");
    check("Accumulate(f(a, b, c, d))", //
        "f(a,a+b,a+b+c,a+b+c+d)");
  }

  @Test
  public void testAddTo() {
    // print: AddTo: d is not a variable with a value, so its value cannot be changed.
    check("d += 7", //
        "d+=7");
    // print: AddTo: test is not a variable with a value, so its value cannot be changed.
    check("\"test\" += 7", //
        "test+=7");
    // print: Part: Part specification k[[2]] is longer than depth of object.
    check("k[[2]] += x", //
        "k[[2]]+=x");
    check("a = 10", //
        "10");
    check("a += 2", //
        "12");
    check("a", //
        "12");

    check("index={1,2,3,4,5,6,7,8,9}", //
        "{1,2,3,4,5,6,7,8,9}");
    check("index[[3]]+=y", //
        "3+y");
    check("index", //
        "{1,2,3+y,4,5,6,7,8,9}");
  }

  @Test
  public void testAlternatives() {
    // http://mathematica.stackexchange.com/a/44084
    check("f(a_Integer|b_Real) := {1,a,2,b,3}", //
        "");
    check("f(100)", //
        "{1,100,2,3}");
    check("f(100.0)", //
        "{1,2,100.0,3}");

    check("g(a_Real | b_Integer, x_) := (x*a)^b", //
        "");
    check("g(100,a)", //
        "a^100");
    check("g(100.0,a)", //
        "100.0*a");

    check("a+b+c+d/.(a|b)->t", //
        "c+d+2*t");
    check("a+b+c+d/.Except(b,(a|c))->t", //
        "b+d+2*t");
  }

  @Test
  public void testApart() {
    // see github #856
    check("Apart((4+3*Sqrt(2)+Sqrt(3)+Sqrt(6))/(2+Sqrt(2)+Sqrt(3)),x)", //
        "1+Sqrt(2)");
    // check("Factor(x^2 - y^2 )", "(x-y)*(x+y)");
    // check("Solve(x^2 - y^2==0, y)", "{{y->-x},{y->x}}");

    // TODO handle multiple variables:
    // check("Apart(1 / ((x + a) (x + b) (x + c)))", //
    // "1/(2*(x-y)*y)-1/(2*y*(x+y))");

    check("Apart(1 / (x^2 - y^2), x)", //
        "1/(2*(x-y)*y)-1/(2*y*(x+y))");

    check("Apart(1/(a*b+a*c),x)", //
        "1/(a*(b+c))");

    // TODO return 1/b - a/(b*(a + b*x))
    check("Apart((x/(a+(b*x))),x)", //
        "x/(a+b*x)");

    check("Apart((3*x-8)/((x+1)*(x-5)),x)", //
        "7/6*1/(-5+x)+11/6*1/(1+x)");

    check("Apart((a + b)^3)", //
        "a^3+3*a^2*b+3*a*b^2+b^3");

    check("Apart(1 / (x^2 - y^2))", //
        "1/(x^2-y^2)");

    check("Apart(x/(2*x + a^2))", //
        "x/(a^2+2*x)");

    check("Apart(y/(x + 2)/(x + 1),x)", //
        "y/(1+x)-y/(2+x)");

    check("Sin(1 / (x ^ 2 - y ^ 2)) // Apart", //
        "Sin(1/(x^2-y^2))");

    check("Apart(1 / (x^2 + 5*x + 6))", //
        "1/(2+x)-1/(3+x)");
    check("Apart(1 / (x^2 - y^2), x)", //
        "1/(2*(x-y)*y)-1/(2*y*(x+y))");

    check("Apart(1/((1 + x)*(5 + x)))", //
        "1/(4*(1+x))-1/(4*(5+x))");
    check("Apart(1 < (x + 1)/(x - 1) < 2)", //
        "1<1+2/(-1+x)<2");

    check("Apart(1/((1 + x)*(5 + x)))", //
        "1/(4*(1+x))-1/(4*(5+x))");
    check("Apart((x)/(x^2-1))", //
        "1/(2*(-1+x))+1/(2*(1+x))");
    check("Apart((x+3)/(x^2-3*x-40))", //
        "11/13*1/(-8+x)+2/13*1/(5+x)");
    check("Apart((10*x^2+12*x+20)/(x^3-8))", //
        "7/(-2+x)+(4+3*x)/(4+2*x+x^2)");
    check("Apart((3*x+5)*(1-2*x)^(-2))", //
        "13/2*1/(1-2*x)^2+3/2*1/(-1+2*x)");
    check("Apart((10*x^2+12*x+20)/(x^3-8))", //
        "7/(-2+x)+(4+3*x)/(4+2*x+x^2)");
    check("Apart((10*x^2-63*x+29)/((x+2)*(x+3)^5))", //
        "195/(2+x)-308/(3+x)^5-185/(3+x)^4-195/(3+x)^3-195/(3+x)^2-195/(3+x)");
  }

  @Test
  public void testAppend() {
    check("Append({1, 2, 3}, 4) ", //
        "{1,2,3,4}");
    check("Append(f(a, b), c)", //
        "f(a,b,c)");
    check("Append({a, b}, {c, d})  ", //
        "{a,b,{c,d}}");
    check("Append(a, b)", //
        "Append(a,b)");
  }

  @Test
  public void testAppendTo() {
    check("w = f({1}, {2}, {3});w[[2]]={2,a}", //
        "{2,a}");
    check("w", //
        "f({1},{2,a},{3})");

    check("w = f({1}, {2}, {3});AppendTo(w[[2]], a)", //
        "{2,a}");
    check("w", //
        "f({1},{2,a},{3})");
    check("AppendTo(Null,1)", //
        "AppendTo(Null,1)");
    check("s = {}", //
        "{}");
    check("AppendTo(s, 1)", //
        "{1}");
    check("s", //
        "{1}");

    check("y = f()", //
        "f()");
    check("AppendTo(y, x)", //
        "f(x)");
    check("y", //
        "f(x)");

    check("AppendTo({}, 1)", //
        "AppendTo({},1)");
    check("AppendTo(a, b)", //
        "AppendTo(a,b)");

    check("$l = {1, 2, 4, 9};appendto($l, 16)", //
        "{1,2,4,9,16}");
    check("$l = {1, 2, 4, 9};appendto($l, 16);$l", //
        "{1,2,4,9,16}");
  }

  @Test
  public void testAppelF1() {

    check("AppellF1(a, b1, b2, c, 0, 0) ", //
        "1");
    check("AppellF1(a, b1, b2, c, z1, 0) ", //
        "Hypergeometric2F1(a,b1,c,z1)");
    check("AppellF1(a, b1, b2, c, 0, z2) ", //
        "Hypergeometric2F1(a,b2,c,z2)");
    check("AppellF1(a, b1, b2, c, z1, 1) ", //
        "Hypergeometric2F1(a,b1,-b2+c,z1)*Hypergeometric2F1(a,b2,c,1)");
    check("AppellF1(a, b1, b2, c, z1, z1) ", //
        "Hypergeometric2F1(a,b1+b2,c,z1)");
    check("AppellF1(a, b1, b1, c, z1, -z1) ", //
        "HypergeometricPFQ({1/2+a/2,a/2,b1},{1/2+c/2,c/2},z1^2)");
    check("AppellF1(a, b1, b2, b1+b2, z1, z2) ", //
        "(1-z2)^a*Hypergeometric2F1(a,b1,b1+b2,(z1-z2)/(1-z2))");
  }

  @Test
  public void testApply() {
    check("(Count(#, Last(#))&) @@ {{1, 2},{3, 4}}", //
        "1");

    check("Apply(f)[p(x, y)]", //
        "f(x,y)");
    check("Apply(f)[aaa]", //
        "aaa");
    //
    check("Apply(f, p[x][q[y]], {1}, Heads -> True)", //
        "f(x)[f(y)]");
    check("Apply(f, p[x][q[y]], {1}, Heads -> False)", //
        "p(x)[f(y)]");
    check("Apply(f, p[x][q[y]], {1})", //
        "p(x)[f(y)]");
    check("Apply(f, 1+2+3, {1}, Heads -> True)", //
        "6");
    check("List @@@ (1+2+3)", //
        "6");

    check("f@ g@ h@ i", //
        "f(g(h(i)))");

    // github issue #40
    check("((#+##&) @@#&) /@{{1,2},{2,2,2},{3,4}}", //
        "{4,8,10}");

    check("Times @@ {1, 2, 3, 4}", //
        "24");
    check("f @@ {{a, b}, {c}, d}", //
        "f({a,b},{c},d)");
    check("apply(head, {3,4,5})", //
        "Head(3,4,5)");
    check("apply(f, a)", //
        "a");
    check("apply(f, {a, \"string\", 3}, {-1})", //
        "{a,string,3}");
    check("table(i0^j, ##) & @@ {{i0, 3}, {j, 4}}", //
        "{{1,1,1,1},{2,4,8,16},{3,9,27,81}}");

    check("apply(f, {{a, b, c}, {d, e}})", //
        "f({a,b,c},{d,e})");
    check("apply(f, {{a, b, c}, {d, e}}, {1})", //
        "{f(a,b,c),f(d,e)}");
    check("apply(f, {{a, b, c}, {d, e}}, {0, 1})", //
        "f(f(a,b,c),f(d,e))");
    // Apply down to level 2 (excluding level 0):
    check("apply(f, {{{{{a}}}}}, 2)", //
        "{f(f({{a}}))}");

    check("apply(f, {{{{{a}}}}}, {0, 2})", //
        "f(f(f({{a}})))");
    check("apply(f, {{{{{a}}}}}, Infinity)", //
        "{f(f(f(f(a))))}");
    check("apply(f, {{{{{a}}}}}, {0, Infinity})", //
        "f(f(f(f(f(a)))))");

    check("apply(f, {{{{{a}}}}}, -1)", //
        "{f(f(f(f(a))))}");
    check("apply(f, {{{{{a}}}}}, -2)", //
        "{f(f(f(f(a))))}");
    check("apply(f, {{{{{a}}}}}, -3)", //
        "{f(f(f({a})))}");

    check("apply(f, {{{{{a}}}}}, {2, -3})", //
        "{{f(f({a}))}}");
    check("apply(f, h0(h1(h2(h3(h4(a))))), {2, -3})", //
        "h0(h1(f(f(h4(a)))))");

    check("Apply(List,1+2+3)", //
        "6");
    check("f @@ {1, 2, 3}", //
        "f(1,2,3)");
    check("Plus @@ {1, 2, 3}", //
        "6");
    check("f @@ (a + b + c)", //
        "f(a,b,c)");
    check("Apply(f, {a + b, g(c, d, e * f), 3}, {1})", //
        "{f(a,b),f(c,d,e*f),3}");
    check("f @@@ {a + b, g(c, d, e * f), 3}", //
        "{f(a,b),f(c,d,e*f),3}");
    check("Apply(f, {a, b, c}, {0})", //
        "f(a,b,c)");
    check("Apply(f, {{{{{a}}}}}, {2, -3})", //
        "{{f(f({a}))}}");
    check("Apply(List, a + b * c ^ e * f(g), {0, Infinity})", //
        "{a,{b,{c,e},{g}}}");
    check("Apply(f, {a, b, c}, x+y)", //
        "Apply(f,{a,b,c},x+y)");
  }

  @Test
  public void testArcCos() {

    // see https://github.com/mtommila/apfloat/issues/18
    checkNumeric("N(ArcCos(-2),30)", //
        "3.1415926535897932384626433832+I*(-1.3169578969248167086250463473)");
    checkNumeric("N(ArcCos(2),30)", //
        "I*1.3169578969248167086250463473");

    checkNumeric("ArcCos(-2.0)", //
        "3.141592653589793+I*(-1.3169578969248166)");
    checkNumeric("ArcCos(2.0)", //
        "I*(-1.3169578969248166)");

    check("ArcCos(Cos(-1/2))", //
        "1/2");
    check("ArcCos(Cos(-1))", //
        "1");
    check("ArcCos(Cos(1))", //
        "1");
    check("ArcCos(Cos(-42))", //
        "-42+14*Pi");
    check("ArcCos(Cos(42))", //
        "-42+14*Pi");
    check("ArcCos(Cos(Pi/4))", //
        "Pi/4");
    check("ArcCos(Cos(5))", //
        "-5+2*Pi");

    check("ArcCos(0)", //
        "Pi/2");
    check("ArcCos(1)", //
        "0");
    check("Integrate(ArcCos(x), {x, -1, 1})", //
        "Pi");

    check("arccos(-11)", //
        "ArcCos(-11)");
    check("arccos(-x)", //
        "ArcCos(-x)");
    check("D(ArcCos(x),x)", //
        "-1/Sqrt(1-x^2)");
    check("diff(ArcCos(x),x)", //
        "-1/Sqrt(1-x^2)");
  }

  @Test
  public void testArcCosh() {
    check("ArcCosh(0)", //
        "I*1/2*Pi");
    checkNumeric("ArcCosh(0.0)", //
        "I*1.5707963267948966");
    checkNumeric("ArcCosh(1.4)", //
        "0.867014726490565");
    check("ArcCosh(-x)", //
        "ArcCosh(-x)");
    check("D(ArcCosh(x),x)", //
        "1/Sqrt(-1+x^2)");
    check("ArcCosh(-Infinity)", //
        "Infinity");
    check("ArcCosh(I*Infinity)", //
        "Infinity");
  }

  @Test
  public void testArcCot() {
    check("ArcCot(Cot(-1/2))", //
        "-1/2");
    check("ArcCot(Cot(-1))", //
        "-1");
    check("ArcCot(Cot(1))", //
        "1");
    check("ArcCot(Cot(-42))", //
        "-42+13*Pi");
    check("ArcCot(Cot(42))", //
        "42-13*Pi");
    check("ArcCot(Cot(Pi/4))", //
        "Pi/4");
    check("ArcCot(Cot(5))", //
        "5-2*Pi");

    check("ArcCot(0)", //
        "Pi/2");
    check("ArcCot(1)", //
        "Pi/4");

    check("arccot(complexinfinity)", //
        "0");
    check("arccot(0)", //
        "Pi/2");
    check("arccot(-11)", //
        "-ArcCot(11)");
    check("arccot(-x)", //
        "-ArcCot(x)");
    check("D(ArcCot(x),x)", //
        "-1/(1+x^2)");
  }

  @Test
  public void testArcCoth() {
    check("ArcCoth(1/Sqrt(5))", //
        "ArcCoth(1/Sqrt(5))");
    check("ArcCoth(0)", //
        "I*1/2*Pi");
    // TODO fails in bitbucket pipelines
    // check("ArcCoth(0.0)", "I*1.5707963267948966");
    // check("ArcCoth(0.5)", "0.5493061443340549+I*(-1.5707963267948966)");
    check("ArcCoth(-x)", //
        "-ArcCoth(x)");
    check("ArcCoth(-1)", //
        "-Infinity");
    check("D(ArcCoth(x),x)", //
        "1/(1-x^2)");
  }

  @Test
  public void testArcCsc() {
    check("ArcCsc(-1+I)", //
        "-I*ArcCsch(1+I)");
    check("ArcCsc(3.5)", //
        "0.289752");
    check("ArcCsc(1.0+3.5*I)", //
        "0.073021+I*(-0.261854)");
    check("ArcCsc(1)", //
        "Pi/2");
    check("ArcCsc(-1)", //
        "-Pi/2");
    check("arccsc(0)", //
        "ComplexInfinity");
    check("arccsc(-x)", //
        "-ArcCsc(x)");
    check("D(ArcCsc(x),x)", //
        "-1/(Sqrt(1-1/x^2)*x^2)");
  }

  @Test
  public void testArcCsch() {
    check("ArcCsch(-1+I)", //
        "-I*ArcCsc(1+I)");
    check("arccsch(0)", //
        "ComplexInfinity");
    checkNumeric("ArcCsch(1.0)", //
        "0.8813735870195429");
    check("ArcCsch(-Infinity)", //
        "0");

    check("arccsch(-x)", //
        "-ArcCsch(x)");
    check("diff(ArcCsch(x),x)", //
        "-1/(Sqrt(1+x^2)*Abs(x))");
  }

  @Test
  public void testArcSec() {
    check("ArcSec(3.5)", //
        "1.28104");
    check("ArcSec(1.0+3.5*I)", //
        "1.49778+I*0.261854");
    check("ArcSec(1)", //
        "0");
    check("ArcSec(-1)", //
        "Pi");
    check("ArcSec(0)", //
        "ComplexInfinity");
    check("ArcSec(-x)", //
        "ArcSec(-x)");
    check("diff(ArcSec(x),x)", //
        "1/(Sqrt(1-1/x^2)*x^2)");
  }

  @Test
  public void testArcSech() {
    check("ArcSech(0)", //
        "Infinity");
    check("ArcSech(0.0)", //
        "Indeterminate");
    check("ArcSech(1)", //
        "0");
    checkNumeric("ArcSech(0.5)", //
        "1.3169578969248166");
    check("ArcSech(-x)", //
        "ArcSech(-x)");
    check("ArcSech(-2)", //
        "I*2/3*Pi");
    check("D(ArcSech(x),x)", //
        "-1/(x*Sqrt(1-x^2))");
  }

  @Test
  public void testArcSin() {
    check("ArcSin(-1+I)", //
        "I*ArcSinh(1+I)");
    check("ArcSin({x,-3,-1/2})", //
        "{ArcSin(x),-ArcSin(3),-Pi/6}");
    check("ArcSin(Sin(-1/2))", //
        "-1/2");
    check("ArcSin(Sin(-1))", //
        "-1");
    check("ArcSin(Sin(1))", //
        "1");
    check("ArcSin(Sin(-42))", //
        "42-13*Pi");
    check("ArcSin(Sin(42))", //
        "-42+13*Pi");
    check("ArcSin(Sin(Pi/4))", //
        "Pi/4");
    check("ArcSin(Sin(5))", //
        "5-2*Pi");

    check("-3*ArcSin(x)-2*ArcCos(x)", //
        "-Pi-ArcSin(x)");
    check("-ArcSin(x)-2*ArcCos(x)", //
        "-Pi/2-ArcCos(x)");
    check("-5*ArcSin(x)-5*ArcCos(x)", //
        "-5/2*Pi");
    check("ArcSin(x)+ArcCos(x)", //
        "Pi/2");
    check("5*ArcSin(x)+5*ArcCos(x)", //
        "5/2*Pi");
    check("ArcSin(0)", //
        "0");
    check("ArcSin(1)", //
        "Pi/2");
    check("arcsin(-11)", //
        "-ArcSin(11)");
    check("arcsin(-x)", //
        "-ArcSin(x)");
    check("diff(ArcSin(x),x)", //
        "1/Sqrt(1-x^2)");
  }

  @Test
  public void testArcSinh() {
    check("ArcSinh(-1+I)", //
        "I*ArcSin(1+I)");
    check("ArcSinh(0)", //
        "0");
    check("ArcSinh(0.0)", //
        "0.0");
    checkNumeric("ArcSinh(1.0)", //
        "0.8813735870195429");
    // check("ArcSinh(-x)", "-ArcSinh(x)");
    check("diff(ArcSinh(x),x)", //
        "1/Sqrt(1+x^2)");
  }

  @Test
  public void testArcTan() {
    // github #110 avoid infinite recursion
    // check("ArcTan(Re(Sin(3+I*2)),Im(Sin(3+I*2)))", //
    // "ArcTan(Im(Sin(3+I*2))/Re(Sin(3+I*2)))");

    check("ArcTan(-1+I)", //
        "I*ArcTanh(1+I)");
    check("N(ArcTan(2, 1), 50)", //
        "0.4636476090008061162142562314612144020285370542861");
    check("N(ArcTan(2, 4), 50)", //
        "1.1071487177940905030170654601785370400700476454014");
    check("N(ArcTan(2, I*4), 50)", //
        "1.5707963267948966192313216916397514420985846996875+I*0.5493061443340548456976226184612628523237452789113");
    check("N(ArcTan(I*4, 2), 50)", //
        "I*(-0.54930614433405484569762261846126285232374527891137)");

    check("N(ArcTan(2, 1))", //
        "0.463648");
    check("N(ArcTan(2, 4))", //
        "1.10715");
    check("N(ArcTan(I*4, 2))", //
        "I*(-0.549306)");
    check("N(ArcTan(2,I*4 ))", //
        "1.5708+I*0.549306");

    check("ArcTan(y_,x^2)", //
        "ArcTan(y_,x^2)");
    check("ArcTan({}, 2)", //
        "{}");
    check("ArcTan(17, {})", //
        "{}");
    check("ArcTan({}, {})", //
        "{}");
    check("ArcTan(0, Pi)", //
        "Pi/2");
    check("ArcTan(0, -Pi/3)", //
        "-Pi/2");
    check("ArcTan(1, 0)", //
        "0");
    check("ArcTan(1/2, 1/2)", //
        "Pi/4");
    check("ArcTan(0, 1)", //
        "Pi/2");
    check("ArcTan(-1/2, 1/2)", //
        "3/4*Pi");
    check("ArcTan(-1, 0)", //
        "Pi");
    check("ArcTan(-1/2, -1/2)", //
        "-3/4*Pi");
    check("ArcTan(0, -1)", //
        "-Pi/2");
    check("ArcTan(1/2, -1/2)", //
        "-Pi/4");

    check("ArcTan(Tan(-1/2))", //
        "-1/2");
    check("ArcTan(Tan(-1))", //
        "-1");
    check("ArcTan(Tan(1))", //
        "1");
    check("ArcTan(Tan(42))", //
        "42-13*Pi");
    check("ArcTan(Tan(Pi/4))", //
        "Pi/4");
    check("ArcTan(Re(9*Cos(5)+I*9*Sin(5)),Im(9*Cos(5)+I*9*Sin(5)))", //
        "5-2*Pi");
    check("ArcTan(-9*Sqrt(2),0)", //
        "Pi");
    check("ArcTan(0.7071067811865476)", //
        "0.61548");
    check("-ArcTan(x/(2*Sqrt(2)))/(2*Sqrt(2))", //
        "-ArcTan(x/(2*Sqrt(2)))/(2*Sqrt(2))");
    check("7*ArcTan(1/2) + a+ArcTan(1/3)", //
        "a+Pi/4+6*ArcTan(1/2)");
    check("ArcTan(1/3) + ArcTan(1/7)", //
        "ArcTan(1/2)");

    check("ArcTan(a, -a)", //
        "ArcTan(a,-a)");
    check("ArcTan(-a, a)", //
        "ArcTan(-a,a)");
    check("ArcTan(a, a)", //
        "ArcTan(a,a)");
    check("2*ArcTan(x)+4*ArcCot(x)", //
        "Pi+2*ArcCot(x)");
    check("7*ArcTan(x)+3*ArcCot(x)", //
        "3/2*Pi+4*ArcTan(x)");
    check("ArcTan(x)+ArcCot(x)", //
        "Pi/2");
    check("4*ArcTan(x)+4*ArcCot(x)", //
        "2*Pi");

    // issue #180
    check("ArcTan(1,Sqrt(3))", //
        "Pi/3");

    check("ArcTan(1)", //
        "Pi/4");
    checkNumeric("ArcTan(1.0)", //
        "0.7853981633974483");
    checkNumeric("ArcTan(-1.0)", //
        "-0.7853981633974483");

    check("ArcTan(0, 0)", //
        "Indeterminate");
    check("ArcTan(1, 1)", //
        "Pi/4");
    check("ArcTan(-1, 1)", //
        "3/4*Pi");
    check("ArcTan(1, -1)", //
        "-Pi/4");
    check("ArcTan(-1, -1)", //
        "-3/4*Pi");

    check("ArcTan(17, 0)", //
        "0");
    check("arctan(Infinity,y)", //
        "0");
    check("arctan(-Infinity,y)", //
        "Pi*(-1+2*UnitStep(Re(y)))");

    check("Abs( ArcTan(ComplexInfinity) )", //
        "Indeterminate");
    check("arctan(infinity)", //
        "Pi/2");
    check("ArcTan(ComplexInfinity)", //
        "Indeterminate");
    check("arctan(1)", //
        "Pi/4");
    check("arctan(-11)", //
        "-ArcTan(11)");
    check("arctan(-x)", //
        "-ArcTan(x)");
    check("arctan(1,1)", //
        "Pi/4");
    check("arctan(-1,-1)", //
        "-3/4*Pi");
    check("arctan(0,0)", //
        "Indeterminate");
    checkNumeric("arctan(1.0,1.0)", //
        "0.7853981633974483");
    checkNumeric("N(1/4*pi)", //
        "0.7853981633974483");
    check("D(ArcTan(x),x)", //
        "1/(1+x^2)");
  }

  @Test
  public void testArcTanh() {
    check("ArcTanh(-1+I)", //
        "I*ArcTan(1+I)");
    check("ArcTanh(0)", //
        "0");
    check("ArcTanh(1)", //
        "Infinity");
    check("ArcTanh(2+I)", //
        "ArcTanh(2+I)");
    checkNumeric("ArcTanh(0.5 + 2*I)", //
        "0.09641562020299621+I*1.1265564408348223");

    check("ArcTanh(I)", //
        "I*1/4*Pi");
    check("ArcTanh(Infinity)", //
        "-I*1/2*Pi");
    check("ArcTanh(-Infinity)", //
        "I*1/2*Pi");
    check("ArcTanh(I*Infinity)", //
        "I*1/2*Pi");
    check("ArcTanh(ComplexInfinity)", //
        "Pi/2");

    check("ArcTanh(-x)", //
        "-ArcTanh(x)");
    check("D(ArcTanh(x),x)", //
        "1/(1-x^2)");
  }

  @Test
  public void testArg() {
    check("FullSimplify(Arg(-I*41*Im(z)-41*Re(z)))", //
        "Arg(-z)");
    check("FullSimplify(Arg(I*2*Im(z)+2*Re(z)))", //
        "Arg(z)");

    check("Arg(42*z)", //
        "Arg(z)");
    check("Arg(-42*z)", //
        "Arg(-z)");
    check("Arg(Interval({1, 3}))", //
        "Interval({0,0})");

    check("Arg(Interval({-7/3, -1/3}))", //
        "Interval({Pi,Pi})");

    // https://github.com/Hipparchus-Math/hipparchus/issues/185
    // System.out.println(new Complex(0.0, 0.0).getArgument());
    // System.out.println(new Complex(-0.0, 0.0).getArgument());
    // System.out.println(new Complex(-0.0, -0.0).getArgument());
    // System.out.println(new Complex(0.0, -0.0).getArgument());
    System.out.println(ApcomplexNum.valueOf(-0.0).complexArg());
    check("Arg(I-Sqrt(3))", //
        "5/6*Pi");
    check("Arg(I-Sqrt(3))", //
        "5/6*Pi");

    check("Arg(E^(-42-5*I))", //
        "-5+2*Pi");
    check("Arg(E^(7+I*3))", //
        "3");
    check("Arg(E^(I*3))", //
        "3");
    check("Arg(E^I)", //
        "1");
    check("Arg(Sqrt(z))", //
        "Arg(z)/2");
    check("Arg(-2*z)", //
        "Arg(-z)");
    check("Arg(1.3)", //
        "0");
    check("Arg(2*z)", //
        "Arg(z)");
    check("Arg(x)", //
        "Arg(x)");
    check("Arg(Infinity)", //
        "0");
    check("Arg(-Infinity)", //
        "Pi");
    check("Arg(2 + I*Pi)", //
        "ArcTan(Pi/2)");
    check("Arg(1+I*Sqrt(3))", //
        "Pi/3");
    check("Arg(I-Sqrt(3))", //
        "5/6*Pi");
    // issue #179
    check("N(Arg(1+I*Sqrt(3)))", //
        "1.0472");

    check("Arg(Pi)", //
        "0");
    check("Arg(-Pi*E)", //
        "Pi");

    check("Arg(0)", //
        "0");
    check("Arg(1)", //
        "0");
    check("Arg(-1)", //
        "Pi");
    check("Arg(I)", //
        "Pi/2");
    check("Arg(1+I)", //
        "Pi/4");
    check("Arg(-I)", //
        "-Pi/2");
    check("Arg(-2*Sqrt(Pi))", //
        "Pi");
    check("Arg(Indeterminate)", //
        "Indeterminate");
    check("Arg(0)", //
        "0");
    check("Arg(10/3)", //
        "0");
    check("Arg(-10/3)", //
        "Pi");
    check("Arg(I*Infinity)", //
        "Pi/2");
    check("Arg(-I*Infinity)", //
        "-Pi/2");
    check("Arg(ComplexInfinity)", //
        "Interval({-Pi,Pi})");

    check("Table(Arg(Exp(k*I*3*Pi/8)), {k, 8})", //
        "{3/8*Pi,3/4*Pi,-7/8*Pi,-Pi/2,-Pi/8,Pi/4,5/8*Pi,Pi}");
  }

  @Test
  public void testArgMax() {
    check("ArgMax(x*10-x^2 , x)", //
        "5");
    check("Maximize(-2*x^2 - 3*x + 5, x)", //
        "{49/8,{x->-3/4}}");
    check("ArgMax(-2*x^2 - 3*x + 5, x)", //
        "-3/4");
  }

  @Test
  public void testArgMin() {
    check("ArgMin(x*10+x^2 , x)", //
        "-5");
    check("Minimize(2*x^2 - 3*x + 5, x)", //
        "{31/8,{x->3/4}}");
    check("ArgMin(2*x^2 - 3*x + 5, x)", //
        "3/4");
  }

  @Test
  public void testArray() {
    check("Array(If(#1 == #2, x, #1*#2) &, {5, 5}) // MatrixForm", //
        "{{x,2,3,4,5},\n" //
            + " {2,x,6,8,10},\n" //
            + " {3,6,x,12,15},\n" //
            + " {4,8,12,x,20},\n" //
            + " {5,10,15,20,x}}");

    check("Array(f, 10, {0,1})", //
        "{f(0),f(1/9),f(2/9),f(1/3),f(4/9),f(5/9),f(2/3),f(7/9),f(8/9),f(1)}");
    check("Array(f, 5, {0,Pi/2})", //
        "{f(0),f(Pi/8),f(Pi/4),f(3/8*Pi),f(Pi/2)}");
    check("Array(Sin(2*#) - Cos(3*#) &,  5, {0,Pi/2})", //
        "{-1,1/Sqrt(2)-Sin(Pi/8),1+1/Sqrt(2),1/Sqrt(2)+Cos(Pi/8),0}");
    check("Array(f, -10, {0,1})", //
        "Array(f,-10,{0,1})");
    check("Array(f, 5, a)", //
        "{f(a),f(1+a),f(2+a),f(3+a),f(4+a)}");
    check("Array(f, 5, a, g)", //
        "g(f(a),f(1+a),f(2+a),f(3+a),f(4+a))");

    // TODO return 0
    // check("Array(f,0,1,Plus)", //
    // "0");
    check("m=Array(Greater, {4, 4})", //
        "{{False,False,False,False},{True,False,False,False},{True,True,False,False},{True,True,True,False}}");
    check("Boole(m)", //
        "{{0,0,0,0},{1,0,0,0},{1,1,0,0},{1,1,1,0}}");

    check("Array(11,10)", //
        "{11[1],11[2],11[3],11[4],11[5],11[6],11[7],11[8],11[9],11[10]}");
    check("Array(Cos(#1/4)&,12,-1)", //
        "{Cos(1/4),1,Cos(1/4),Cos(1/2),Cos(3/4),Cos(1),Cos(5/4),Cos(3/2),Cos(7/4),Cos(2),Cos(\n"
            + "9/4),Cos(5/2)}");
    check("Array(Cos(#1/4)&,12)", //
        "{Cos(1/4),Cos(1/2),Cos(3/4),Cos(1),Cos(5/4),Cos(3/2),Cos(7/4),Cos(2),Cos(9/4),Cos(\n"
            + "5/2),Cos(11/4),Cos(3)}");
    check("Array(Cos(#/4)&, 12, 4)", //
        "{Cos(1),Cos(5/4),Cos(3/2),Cos(7/4),Cos(2),Cos(9/4),Cos(5/2),Cos(11/4),Cos(3),Cos(\n"
            + "13/4),Cos(7/2),Cos(15/4)}");
    check("Array(Cos(#/4)&, 12, 4, Min)", //
        "Cos(13/4)");
    check("Array(f, {2, 3}, 1, Plus)", //
        "f(1,1)+f(1,2)+f(1,3)+f(2,1)+f(2,2)+f(2,3)");
    check("Array(f, {2, 3}, {1, 2, 3})", //
        "Array(f,{2,3},{1,2,3})");
    check("Array(f, 4)", //
        "{f(1),f(2),f(3),f(4)}");
    check("Array(f, 10, 0)", //
        "{f(0),f(1),f(2),f(3),f(4),f(5),f(6),f(7),f(8),f(9)}");
    check("Array(f, {2, 3})", //
        "{{f(1,1),f(1,2),f(1,3)},{f(2,1),f(2,2),f(2,3)}}");
    check("Array(f, {2, 3}, {4, 6})", //
        "{{f(4,6),f(4,7),f(4,8)},{f(5,6),f(5,7),f(5,8)}}");
    check("Array(f, {2, 3}, {0, 4})", //
        "{{f(0,4),f(0,5),f(0,6)},{f(1,4),f(1,5),f(1,6)}}");

    // TODO implement other non-integer based iterators
    // check("Array[f, 10, {0, 1}]", "");
  }

  @Test
  public void testArrayPad() {
    // display error messages: Maximum AST dimension 4294967296 exceeded
    check("ArrayPad({{1,0},{0,1}}, 2147483647)", //
        "ArrayPad(\n" //
            + "{{1,0},\n" //
            + " {0,1}},2147483647)");
    // display error messages: Maximum AST dimension 4294967296 exceeded
    check("ArrayPad(E^(I*1/3*Pi),2147483647,2*Pi)", //
        "ArrayPad(E^(I*1/3*Pi),2147483647,2*Pi)");

    check("ArrayPad({a, b, c}, 1, x)", //
        "{x,a,b,c,x}");
    check("ArrayPad({{1, 2}, {3, 4}}, {1,2})", //
        "{{0,0,0,0,0},{0,1,2,0,0},{0,3,4,0,0},{0,0,0,0,0},{0,0,0,0,0}}");
    check("ArrayPad({{1, 2}, {3, 4}}, 2)", //
        "{{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,1,2,0,0},{0,0,3,4,0,0},{0,0,0,0,0,0},{0,0,0,0,\n"
            + "0,0}}");
    check("ArrayPad({1, 2, 3}, {2,4})", //
        "{0,0,1,2,3,0,0,0,0}");
    check("ArrayPad({1, 2, 3}, 1)", //
        "{0,1,2,3,0}");
    check("ArrayPad({1, 2, 3}, 2, x)", //
        "{x,x,1,2,3,x,x}");
  }

  @Test
  public void testArrayDepth() {
    check("ArrayDepth(Array(a, {4, 5, 2}))", //
        "3");

    check("ArrayDepth({{a,b},{c,d}})", //
        "2");
    check("ArrayDepth(x)", //
        "0");
    check("ArrayDepth({{1, 2}, {3, 4}})", //
        "2");
    check("ArrayDepth({1, 2, 3, 4})", //
        "1");
    check("ArrayDepth({{a, b}, {c}})", //
        "1");
    check("ArrayDepth(f(f(a, b), f(c, d)))", //
        "2");
    check("ArrayDepth(Array(a, {4, 5, 2}))", //
        "3");
  }

  @Test
  public void testArrayFlatten() {
    check(
        "s1 = SparseArray({i_, i_} -> i, {2, 2});\ns2 = SparseArray({i_, j_} /; Abs(i-j) == 1 -> i - j, {3, 3});", //
        "");
    check("m = ArrayFlatten({{0, s1}, {-s2, 0}})", //
        "SparseArray(Number of elements: 6 Dimensions: {5,5} Default value: 0)");
    check("m // MatrixForm", //
        "{{0,0,0,1,0},\n" //
            + " {0,0,0,0,2},\n" //
            + " {0,1,0,0,0},\n" //
            + " {-1,0,1,0,0},\n" //
            + " {0,-1,0,0,0}}");

    check("s = SparseArray({i_, i_, j_, k_} -> i/(j+k-1), {3, 3, 2, 2});", //
        "");
    check("ArrayFlatten(s) // MatrixForm", //
        "{{1,1/2,0,0,0,0},\n" //
            + " {1/2,1/3,0,0,0,0},\n" //
            + " {0,0,2,1,0,0},\n" //
            + " {0,0,1,2/3,0,0},\n" //
            + " {0,0,0,0,3,3/2},\n" //
            + " {0,0,0,0,3/2,1}}");

    check("m = {{1,2},{3,4}}", //
        "{{1,2},{3,4}}");
    check("n = {{1,2},{3,4},{5,6}}", //
        "{{1,2},{3,4},{5,6}}");



    check("ArrayFlatten(Array(a, {1,2,3,4}))", //
        "{{a(1,1,1,1),a(1,1,1,2),a(1,1,1,3),a(1,1,1,4),a(1,2,1,1),a(1,2,1,2),a(1,2,1,3),a(\n"
            + "1,2,1,4)},{a(1,1,2,1),a(1,1,2,2),a(1,1,2,3),a(1,1,2,4),a(1,2,2,1),a(1,2,2,2),a(1,\n"
            + "2,2,3),a(1,2,2,4)},{a(1,1,3,1),a(1,1,3,2),a(1,1,3,3),a(1,1,3,4),a(1,2,3,1),a(1,2,\n"
            + "3,2),a(1,2,3,3),a(1,2,3,4)}}");
    check("ArrayFlatten(Array(a, {1,2,3,4})) // Dimensions", //
        "{3,8}");

    // check("ArrayFlatten(Array(a, {1,2,3,4,5}), 3)// Dimensions", //
    // "{1,2,3,4,5}");
    // check("ArrayFlatten(Array(a, {1,2,3,4,5}), 3)", //
    // "");
    // check("ArrayFlatten(Array(a, {1,2,3,4,5,6}), 3) // Dimensions", //
    // "{4,10,18}");
    check("ArrayFlatten(Array(a, {1,2,3,4,5,6})) // Dimensions", //
        "{3,8,5,6}");
    check("ArrayFlatten({{1,1,1}, {m, m,m}})", //
        "{{1,1,1,1,1,1},{1,2,1,2,1,2},{3,4,3,4,3,4}}");

    check("ArrayFlatten({{1,n,3}, {m, m,4}})", //
        "{{1,1,1,2,3},{1,1,3,4,3},{1,1,5,6,3},{1,2,1,2,4},{3,4,3,4,4}}");
    check("ArrayFlatten({{1,1,1}, {m, m,n}})", //
        "ArrayFlatten({{1,1,1},{{{1,2},{3,4}},{{1,2},{3,4}},{{1,2},{3,4},{5,6}}}})");

    check("ArrayFlatten({{0, 0, 0}, {m, m, 0}})", //
        "{{0,0,0,0,0},{1,2,1,2,0},{3,4,3,4,0}}");
    check("ArrayFlatten({{0, 0, m}, {m, m, 0}})", //
        "{{0,0,0,0,1,2},{0,0,0,0,3,4},{1,2,1,2,0,0},{3,4,3,4,0,0}}");

    // message ArrayFlatten: The array depth of the expression at position 1 of
    // {{{{1,2},{3,4}},{{1,2},{3,4}},{{1,2},{3,4}}},{{{1,2},{3,4}},{{1,2},{3,4}}}} must be at least
    // equal to the specified rank 2.
    check("ArrayFlatten({{m, m, m}, {m, m}})", //
        "ArrayFlatten({{{{1,2},{3,4}},{{1,2},{3,4}},{{1,2},{3,4}}},{{{1,2},{3,4}},{{1,2},{\n"
            + "3,4}}}})");

    check("ArrayFlatten({{m, m, m}, {m, m, m}})", //
        "{{1,2,1,2,1,2},{3,4,3,4,3,4},{1,2,1,2,1,2},{3,4,3,4,3,4}}");

  }

  @Test
  public void testArrayQ() {
    check("ArrayQ({{a, b}, {c, d}}, 2, SymbolQ)", //
        "True");
    check("ArrayQ(SparseArray({{1, 2} -> a, {2, 1} -> b}))", //
        "True");
    check("ArrayQ(SparseArray({{1, 2} -> a, {2, 1} -> b}), 1)", //
        "False");
    check("ArrayQ(SparseArray({{1, 2} -> a, {2, 1} -> b}), 2, SymbolQ)", //
        "False");
    check("ArrayQ(SparseArray({{1, 1} -> a, {1, 2} -> b}), 2, SymbolQ)", //
        "True");

    check("ArrayQ(SparseArray({1, 2}))", //
        "True");
    check("ArrayQ({ })", //
        "True");
    check("ArrayQ({1, 2, 3, 4})", //
        "True");
    check("ArrayQ({1, 2, {3}, 4})", //
        "False");
    check("ArrayQ({{1, 2}, {3}})", //
        "False");
    check("ArrayQ({{1, 2}, {3, 4}})", //
        "True");
    check("ArrayQ({1, 2, 3, 4}, 2)", //
        "False");
    check("ArrayQ({{1, 2}, {3, 4}},2)", //
        "True");
    check("ArrayQ({1, 2, 3, x}, 1, NumericQ)", //
        "False");
    check("ArrayQ({1, 2, 3, 4}, 1, NumericQ)", //
        "True");
    check("ArrayQ({{{E, 1}, {Pi, 2}}, {{Sin(1), Cos(2)}, {Sinh(1), Cosh(1)}}}, _, NumericQ)", //
        "True");
    check("ArrayQ({1, 2., E, Pi + I}, 1)", //
        "True");
    check("ArrayQ({{1,2},{3,4}},2,NumericQ)", //
        "True");
    check("ArrayQ({{a, b}, {c, d}},2,SymbolQ)", //
        "True");
  }

  @Test
  public void testArrayReshape() {
    check("ArrayReshape(SparseArray({1 -> 1, 4 -> 5}, 10), {2, 6}, x)", //
        "{{1,0,0,5,0,0},{0,0,0,0,x,x}}");
    check("ArrayReshape({}, {})", //
        "0");
    check("ArrayReshape({a,b,c}, { })", //
        "a");
    check("ArrayReshape({}, {1,2,3})", //
        "{{{0,0,0},{0,0,0}}}");
    check("ArrayReshape(Range(1000), {3, 2, 2})", //
        "{{{1,2},{3,4}},{{5,6},{7,8}},{{9,10},{11,12}}}");
    check("ArrayReshape({a, b, c, d, e, f}, {2, 3})", //
        "{{a,b,c},{d,e,f}}");
    check("ArrayReshape({a, b, c, d, e, f}, {2, 3, 1})", //
        "{{{a},{b},{c}},{{d},{e},{f}}}");
    check("ArrayReshape(Range(24), {2, 3, 4})", //
        "{{{1,2,3,4},{5,6,7,8},{9,10,11,12}},{{13,14,15,16},{17,18,19,20},{21,22,23,24}}}");
    check("ArrayReshape({a, b, c, d, e, f}, {2, 7})", //
        "{{a,b,c,d,e,f,0},{0,0,0,0,0,0,0}}");
    check("ArrayReshape({a, b, c, d, e, f}, {2, 3, 3,2}, x)", //
        "{{{{a,b},{c,d},{e,f}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}},{{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}}}");
  }

  @Test
  public void testAssuming001() {
    check("Assuming(a < 0 && b > 0, Refine(HeavisideTheta(b, b, a)))", //
        "0");

    check("$Assumptions = { x > 0 }", //
        "{x>0}");
    check("Assuming(y>0, $Assumptions)", //
        "{x>0,y>0}");

    check("Assuming(y>0, ConditionalExpression(y x^2, y>0)//Simplify)", //
        "x^2*y");
    check("Assuming(a > 0, {Refine(Sqrt(a^2)), Integrate(x^a, {x, 0, 1})})", //
        "{a,1/(1+a)}");
    check("Assuming(x>0, Simplify(Sqrt(x^2)))", //
        "x");
  }

  @Test
  public void testAssuming002() {
    check("$Assumptions = Element(m, Matrices({4, 4}, Reals, Symmetric({1, 2})))", //
        "m∈Matrices({4,4},Reals,Symmetric({1,2}))");
    check("TensorRank(m)", //
        "2");

    check("Assuming(y>0, ConditionalExpression(y x^2, y>0)//Simplify)", //
        "x^2*y");
    check("Assuming(a > 0, {Refine(Sqrt(a^2)), Integrate(x^a, {x, 0, 1})})", //
        "{a,1/(1+a)}");
    check("Assuming(x>0, Simplify(Sqrt(x^2)))", //
        "x");
  }

  @Test
  public void testAssuming003() {
    check("$Assumptions = Element(a | b | c, Vectors(3));", //
        "");
    check("{TensorRank(a), TensorDimensions(b), TensorSymmetry(c)}", //
        "{1,{3},{}}");
  }

  @Test
  public void testAtomQ() {
    check("AtomQ(<|\"a\"->1|> // Unevaluated)", //
        "False");
    check("AtomQ(<|\"a\"->1|>)", //
        "True");
    check("AtomQ(Sin(Pi))", //
        "True");
    check("AtomQ(x)", //
        "True");
    check("AtomQ(1.2)", //
        "True");
    check("AtomQ(2 + I)", //
        "True");
    check("AtomQ(2 / 3)", //
        "True");
    check("AtomQ(x + y)", //
        "False");
  }

  @Test
  public void testArithmeticGeometricMean() {
    check("N(ArithmeticGeometricMean(1 - I, 5/2 + I), 30)", //
        "1.83462883815328396810218573046+I*(-0.19146162519713708344049353505)");
    check("N(ArithmeticGeometricMean(1,2), 20)", //
        "1.4567910310469068691");
    check("N(ArithmeticGeometricMean(52,5),30)", //
        "21.8724836267417540342866215962");


    check("ArithmeticGeometricMean(1/2,42.0)", //
        "11.34094");
    check("ArithmeticGeometricMean({1.0, 2.0, 3.0, 4.0}, 42.0)", //
        "{12.874,14.88314,16.37375,17.62155}");
    check("ArithmeticGeometricMean(a, 1/a)", // orderless
        "ArithmeticGeometricMean(1/a,a)");
    check("ArithmeticGeometricMean(a, 0)", //
        "0");
    check("ArithmeticGeometricMean(0, b)", //
        "0");
    check("ArithmeticGeometricMean(d, d)", //
        "d");

    check("ArithmeticGeometricMean({1.0, 2.0, 3.0}, 5)", //
        "{2.60401,3.329,3.93624}");

    check("ArithmeticGeometricMean(1 - I, 2.5 + I)", //
        "1.83463+I*(-0.191462)");
  }

  @Test
  public void testAttributes() {
    check("Attributes(fun) = {ReadProtected, Protected}", //
        "{ReadProtected,Protected}");
    check("Attributes(Plus)", //
        "{Flat,Listable,NumericFunction,OneIdentity,Orderless,Protected}");
  }

  @Test
  public void testBeginPackage() {
    check("BeginPackage(\"test`\")", //
        "");
    check("Context( )", //
        "test`");
    check("$ContextPath", //
        "{test`,System`}");
    check("testit::usage = \"testit(x) gives x^2\"", //
        "testit(x) gives x^2");
    check("testit(x_) :=  x^2 ", //
        "");
    check("xxx", //
        "xxx");
    check("testit(12)", //
        "144");
    check("Begin(\"`Private`\")", //
        "test`Private`");
    check("End( )", //
        "test`Private`");
    check("EndPackage( )", //
        "");
    check("$ContextPath", //
        "{test`,System`,Global`}");
    check("Context( )", //
        "Global`");
    // print usage message in console
    check("?testit", //
        "");
    check("testit(12)", //
        "144");
    check("xxx", //
        "xxx");

    // file system disabled => Needs has no effect in this test
    check("BeginPackage(\"needed`\", {\"needit1`\", \"needit2`\"}) // InputForm", //
        "{Needs(\"needit1`\"),Needs(\"needit2`\")}");
  }

  @Test
  public void testBeginPackageNested() {
    check("BeginPackage(\"test`\")", //
        "");
    check("Context( )", //
        "test`");
    check("$ContextPath", //
        "{test`,System`}");
    check("testit::usage = \"testit(x) gives x^2\"", //
        "testit(x) gives x^2");
    check("testit(x_) :=  x^2 ", //
        "");
    check("Begin(\"`Private`\")", //
        "test`Private`");
    check("test2(x_) :=  x^3 ", //
        "");
    check("testit(12)", //
        "144");
    check("Context( )", //
        "test`Private`");
    check("$Context", //
        "test`Private`");
    check("$ContextPath", //
        "{test`,System`}");
    check("End( )", //
        "test`Private`");
    check("$ContextPath", //
        "{test`,System`}");
    check("Context( )", //
        "test`");
    check("EndPackage( )", //
        "");
    check("test`Private`test2(12)", //
        "1728");
    check("$ContextPath", //
        "{test`,System`,Global`}");
    check("Context( )", //
        "Global`");
    // print usage message in console
    check("?testit", //
        "");
    check("testit(12)", //
        "144");
  }

  @Test
  public void testBaseDecode() {
    check(
        "ba1 = BaseEncode(StringToByteArray(\"Man is distinguished, not only by his reason, but by this singular passion from other animals, "
            + "which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable "
            + "generation of knowledge, exceeds the short vehemence of any carnal pleasure.\")) ", //
        "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=");

    check("ba2 = BaseDecode(ba1)", //
        "ByteArray[269 Bytes]");

    check("ByteArrayToString(ba2)", //
        "Man is distinguished, not only by his reason, but by this singular passion from other animals, "
            + "which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable "
            + "generation of knowledge, exceeds the short vehemence of any carnal pleasure.");
  }

  @Test
  public void testBaseEncode() {
    check(
        "BaseEncode(ByteArray({1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20})) ", //
        "AQIDBAUGBwgJCgsMDQ4PEBESExQ=");
  }

  @Test
  public void testBaseForm() {
    check("36^^zZz", //
        "46655");
    check("BaseForm(46655, 36)", //
        "Subscript(zzz,36)");
    check("16^^abcdefff", //
        "2882400255");
    check("BaseForm(2882400255, 16)", //
        "Subscript(abcdefff,16)");
    check("37^^abcdefff", //
        "Syntax error in line: 1 - Base 37^^... is invalid. Only bases between 1 and 36 are allowed\n" //
            + "37^^abcdefff\n" + "  ^");
    check("16^^6z12xy", //
        "Syntax error in line: 1 - Number format error: 6z12xy\n" //
            + "16^^6z12xy\n" + "         ^");
  }

  @Test
  public void testBegin() {
    // Begin: is not a valid context name.
    check("Begin(\"\")", //
        "Begin()");

    check("Begin(\"mytest`\")", //
        "mytest`");
    check("Context( )", //
        "mytest`");
    check("$ContextPath", //
        "{System`,Global`}");
    check("testit::usage = \"testit(x) gives x^2\"", //
        "testit(x) gives x^2");
    check("testit(x_) :=  x^2 ", //
        "");
    check("testit(12)", //
        "144");
    check("End( )", //
        "mytest`");
    check("$ContextPath", //
        "{System`,Global`}");
    check("Context( )", //
        "Global`");
    // print usage message in console
    check("?mytest`testit", //
        "");
    check("mytest`testit(12)", //
        "144");
    check("testit(12)", //
        "testit(12)");
  }

  @Test
  public void testBellB() {
    check("BellB(1009,-9223372036854775807/9223372036854775808)", //
        "BellB(1009,-9223372036854775807/9223372036854775808)");
    check("BellB(10007)", //
        "BellB(10007)");
    check("BellB(1/2,z)", //
        "BellB(1/2,z)");

    check("BellB(10,x)", //
        "x+511*x^2+9330*x^3+34105*x^4+42525*x^5+22827*x^6+5880*x^7+750*x^8+45*x^9+x^10");
    check("BellB(0,z)", //
        "1");
    check("BellB(1,z)", //
        "z");
    check("BellB(42,0)", //
        "0");
    check("BellB(25)", //
        "4638590332229999353");
    check("BellB(n,0)", //
        "BellB(n,0)");
    check("BellB(n,1)", //
        "BellB(n)");
    check("BellB(5,x)", //
        "x+15*x^2+25*x^3+10*x^4+x^5");
    check("BellB(5,x+y^2)", //
        "x+y^2+15*(x+y^2)^2+25*(x+y^2)^3+10*(x+y^2)^4+(x+y^2)^5");
    check("Table(BellB(k), {k, 0, 14})", //
        "{1,1,2,5,15,52,203,877,4140,21147,115975,678570,4213597,27644437,190899322}");
    check("BellB(10)", //
        "115975");
    check("BellB(15)", //
        "1382958545");
    check("BellB(100)", //
        "4758539127676483365879076884138720782636366968682561146661633463755911449789244\\\n"
            + "2622672724044217756306953557882560751");
    check("BellB({1,2,3,4,5,6})", "{1,2,5,15,52,203}");
  }

  @Test
  public void testBellY() {
    // check("x + Sum(x^m/(m!*(m - 1)!) * BellY(Table({(m + k - 2)!, -(k - 1)! * c(k)}, {k, 2, m})),
    // {m, 2, 4}) ",
    // //
    // "");

    // display error messages: Polynomial degree 2147483647 exceeded
    check("BellY(2147483647,3,{x,-3,-1/2})", //
        "BellY(2147483647,3,{x,-3,-1/2})");
    // message Polynomial degree 1009 exceeded
    check("BellY(1009,19,{-1/2,-2,3})", //
        "BellY(1009,19,{-1/2,-2,3})");

    check("BellY(2,1,{1/2,0})", //
        "0");
    check("BellY(5, 2, {})", //
        "0");
    check("BellY(5, 2, {1})", //
        "0");
    check("BellY(5, 2, {1,2})", //
        "0");
    check("BellY(5, 2, {1,2,3})", //
        "60");
    check("BellY(5, 8, {1,2,3})", //
        "0");

    // https://en.wikipedia.org/wiki/Bell_polynomials
    check("BellY(6, 2, {x1, x2, x3, x4, x5})", //
        "10*x3^2+15*x2*x4+6*x1*x5");
    check("BellY(6, 3, {x1, x2, x3, x4})", //
        "15*x2^3+60*x1*x2*x3+15*x1^2*x4");

    check("BellY(4, 2, {x1, x2, x3})", //
        "3*x2^2+4*x1*x3");
    check("With({n = 7, k = 2}, BellY(n, k, Array(x, n)))", //
        "35*x(3)*x(4)+21*x(2)*x(5)+7*x(1)*x(6)");
  }

  @Test
  public void testBernoulliB() {
    check("N(BernoulliB(0) )", //
        "1.0");

    checkNumeric("Table(BernoulliB(n, z), {n, 0, 5}, {z, -2, 2,0.25}) // N", //
        "{{1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0},{-2.5,-2.25,-2.0,-1.75,-1.5,-1.25,-1.0,-0.75,-0.5,-0.25,0.0,0.25,0.5,0.75,1.0,1.25,1.5},{6.166666666666668,4.979166666666667,3.9166666666666674,2.979166666666667,2.1666666666666665,1.4791666666666665,0.9166666666666667,0.47916666666666663,0.16666666666666666,-0.02083333333333333,-0.08333333333333333,-0.02083333333333333,0.16666666666666666,0.47916666666666663,0.9166666666666667,1.4791666666666665,2.1666666666666665},{-15.0,-10.828125,-7.5,-4.921875,-3.0,-1.6406249999999998,-0.75,-0.234375,0.0,0.04687499999999999,0.0,-0.046875,0.0,0.234375,0.75,1.6406249999999998,3.0},{35.96666666666667,23.126822916666665,14.029166666666667,7.876822916666668,3.966666666666667,1.6893229166666666,0.5291666666666667,0.06432291666666666,-0.03333333333333333,0.0018229166666666665,0.02916666666666666,0.0018229166666666665,-0.03333333333333333,0.06432291666666666,0.5291666666666667,1.6893229166666666,3.966666666666667},{-85.0,-48.5009765625,-25.625000000000004,-12.202148437499998,-5.0,-1.6064453124999998,-0.3125,0.0048828125,0.0,-0.02441406249999999,0.0,0.02441406249999999,0.0,-0.0048828125,0.3125,1.6064453124999998,5.0}}");

    check("N(BernoulliB(50),50)", //
        "7500866746076964366855720.0757575757575757575757575");
    check("N(BernoulliB(50),50)//IntegerPart", //
        "7500866746076964366855720");

    // slow
    check("BernoulliB(2009,-1+Sqrt(2))", //
        "Hold(BernoulliB(2009,-1+Sqrt(2)))");
    // message: Non-negative machine-sized integer expected at position 1 in
    // BernoulliB(-2147483648,1/2).
    check("BernoulliB(-2147483648,1/2)", //
        "BernoulliB(-2147483648,1/2)");

    // message: Non-negative machine-sized integer expected at position 1 in
    // BernoulliB(-3).
    check("BernoulliB(-3)", //
        "BernoulliB(-3)");
    check("BernoulliB(3,-2)", //
        "-15");

    check("BernoulliB(4, 9)", //
        "155519/30");
    check("BernoulliB(4, -9)", //
        "242999/30");

    check("BernoulliB(18, 1/2)", //
        "-5749691557/104595456");
    check("BernoulliB(17, 1/2)", //
        "0");
    check("BernoulliB(n, 0)", //
        "BernoulliB(n)");
    check("BernoulliB(18, 1)", //
        "43867/798");

    check("BernoulliB(51)", //
        "0");
    check("BernoulliB(42)", //
        "1520097643918070802691/1806");
    check("BernoulliB(2)", //
        "1/6");
    check("Table(BernoulliB(k), {k, 0, 10})", //
        "{1,-1/2,1/6,0,-1/30,0,1/42,0,-1/30,0,5/66}");

    // http://fungrim.org/entry/555e10/
    check("Table(BernoulliB(k,x), {k, 0, 5})", //
        "{1,-1/2+x,1/6-x+x^2,x/2-3/2*x^2+x^3,-1/30+x^2-2*x^3+x^4,-x/6+5/3*x^3-5/2*x^4+x^5}");

  }



  @Test
  public void testBlankSequence() {

    check("Clear(f);f(___,y__?(#>2&)):={y}", //
        "");
    check("{f(2,3),f(1,1,1,2),f(112,1,1,3,4)}", //
        "{{3},f(1,1,1,2),{3,4}}");

    check("Clear(f);f(x__Integer)=2", //
        "2");
    check("{f(2,3),f(a,2),f(2,a),f(2)}", //
        "{2,f(a,2),f(2,a),2}");

    check("Clear(f);f(x__Real) := Plus(x)/Length({x})", //
        "");
    check("{f(1.0,4.0),f(2,2),f(1.0,a)}", //
        "{2.5,f(2,2),f(1.0,a)}");

    check("Clear(f);f(x__,y__,z__):={x,y,z}/;Print({x},{y},{z})", //
        "");
    check("f(a,b,c,d,e)", "f(a,b,c,d,e)");
    // print the possible matches
    // {a}{b}{c,d,e}
    // {a}{b,c}{d,e}
    // {a,b}{c}{d,e}
    // {a}{b,c,d}{e}
    // {a,b}{c,d}{e}
    // {a,b,c}{d}{e}
    System.out.println("-------------");
    check("g(x___,y___,z___):={x,y,z}/;Print({x},{y},{z})", //
        "");
    check("g(a,b,c,d,e)", "g(a,b,c,d,e)");
    // print the possible matches
    // {}{}{a,b,c,d,e}
    // {a}{b}{c,d,e}
    // {}{a,b,c}{d,e}
    // {a}{b,c}{d,e}
    // {a,b}{c}{d,e}
    // {a,b,c}{}{d,e}
    // {}{a,b,c,d}{e}
    // {a}{b,c,d}{e}
    // {a,b}{c,d}{e}
    // {a,b,c}{d}{e}
    // {a,b,c,d}{}{e}
    // {}{a,b,c,d,e}{}
    // {a}{b,c,d,e}{}
    // {a,b}{c,d,e}{}
    // {a,b,c}{d,e}{}
    // {a,b,c,d}{e}{}
    // {a,b,c,d,e}{}{}
  }

  @Test
  public void testBlock() {
    // Rubi rules use Block variable names in "sub-rules":
    check("Integrate(E^(E^x + x), x)", //
        "E^E^x");

    // http://oeis.org/A005132 - Recaman's sequence
    check(
        "f(s_List) := Block({a = s[[-1]], len = Length@s}, Append(s, If(a > len && !MemberQ(s, a - len), a - len, a + len))); Nest(f, {0}, 70)", //
        "{0,1,3,6,2,7,13,20,12,21,11,22,10,23,9,24,8,25,43,62,42,63,41,18,42,17,43,16,44,\n"
            + "15,45,14,46,79,113,78,114,77,39,78,38,79,37,80,36,81,35,82,34,83,33,84,32,85,31,\n"
            + "86,30,87,29,88,28,89,27,90,26,91,157,224,156,225,155}");
    check("blck=Block({i=10}, i=i+1; Return(i))", //
        "11");
    check("xm=10;Block({xm=xm}, xm=xm+1;Print(xm));xm", //
        "10");
    check("Block({testVar}, testVar=2222;testVar)", //
        "2222");
    check("testVar=1111; Block({testVar}, testVar)", //
        "1111");
    check("Block({x=y,y=z,z=3}, Print({Hold(x),Hold(y),Hold(z)});{x,y,z})", //
        "{3,3,3}");
    check("Block({x,f}, f(0)=0;f(x_):=f(x-1)+x;f(3))", //
        "6");
    check("f(3)", //
        "f(3)");

    check("m=i^2;Block({i = a}, i + m)", //
        "a+a^2");
    check("m=i^2;Module({i = a}, i + m)", //
        "a+i^2");

    check("h(x_):=Block({t}, t^2 - 1 /; (t = x - 4) > 1); h(10)", //
        "35");
    check("h(x_):=Block({t}, t^2 - 1 /; (t = x - 4) > 1); h(5)", //
        "h(5)");
  }

  @Test
  public void testBinaryDistance() {
    check("BinaryDistance(SparseArray({1, 2, 3, 4, 5}), SparseArray({1, 2, 3, 4, 5}))", //
        "1");
    check("BinaryDistance({1, 2, 3, 4, 5}, {1, 2, 3, 4, 5})", //
        "1");
    check("BinaryDistance({1, 2, 3, 4, 5}, {1, 2, 3, 4, -5})", //
        "0");
    check("BinaryDistance({1, 2, 3, 4, 5}, {1, 2, 3, 4, 5.0})", //
        "0");
  }

  @Test
  public void testBinCounts() {

    check("BinCounts({{1,0}, {0,1}},-Infinity)", //
        "BinCounts({{1,0},{0,1}},-Infinity)");

    check("BinCounts({1,2,3,4,5},{1,7,2})", //
        "{2,2,1}");
    check("BinCounts({1,2,3,4,5},{1,7,3})", //
        "{3,2}");
    check("BinCounts({1,2,3,4,5,6,7,8,9,10,11,12,13},{1,13,4})", //
        "{4,4,4}");
    check("BinCounts({2,-1,a,b},{-1,3,1})", //
        "{1,0,0,1}");
    check("BinCounts({3/4,-2},{-1,3,1})", //
        "{0,1,0,0}");
    check("BinCounts({3/4},{1,3,1})", //
        "{0,0}");
    check("BinCounts({3/4} )", //
        "{1}");

    check("BinCounts({1,2,3,4,5})", //
        "{0,1,1,1,1,1}");
    check("BinCounts({1,2,3,4,5},3)", //
        "{2,3}");
    check("BinCounts({1,2,3,4,5},4)", //
        "{3,2}");
    check("BinCounts({1,2,3,4,5},5)", //
        "{4,1}");
    check("BinCounts({1,2,3,4,5},10)", //
        "{5}");
    check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},0)", //
        "BinCounts({0.04,0.75,0.3333,0.03344,0.9999},0)");
    check("BinCounts({1,2,3,4,5},{3,3,-1})", //
        "BinCounts({1,2,3,4,5},{3,3,-1})");
    check("BinCounts({1,2,3,4,5},{3,3,1})", //
        "{}");
    check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},0.1)", //
        "{2,0,0,1,0,0,0,1,0,1}");
    check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},-0.1)", //
        "BinCounts({0.04,0.75,0.3333,0.03344,0.9999},-0.1)");
    check("BinCounts({1, 2, 3, 4, 5})", //
        "{0,1,1,1,1,1}");
    check("BinCounts({1.5, 3, a, 2.5, 1, I}, 2)", //
        "{2,2}");
    check("BinCounts({1, 3, 2, 1, 4, 5, 6, 2}, {0, 10, 1})", //
        "{0,2,2,1,1,1,1,0,0,0}");
    check("BinCounts({1, 3, 2, 1, 4, 5, 6, 2}, 2)", //
        "{2,3,2,1}");
    check("BinCounts({1.5, 3, N(3, 20), 2.5, 1, E}, 2)", //
        "{2,4}");
  }


  @Test
  public void testBitLength() {
    check("BitLength(1023)", //
        "10");
    check("BitLength(100) ", //
        "7");
    check("BitLength(-5)", //
        "3");
    check("BitLength(0)", //
        "0");
    check("BitLength(2^123-1)", //
        "123");
    check("BitLength(-(2^123-1))", //
        "123");
  }

  @Test
  public void testBrayCurtisDistance() {
    check("-1*{10.5, 10} ", //
        "{-10.5,-10}");
    check("{-1, -1} - 1 * {10.5, 10} ", //
        "{-11.5,-11}");
    check("{-1, -1} + {10.5, 10} ", //
        "{9.5,9}");


    check("BrayCurtisDistance({-1, -1}, {10.5, 10})", //
        "1.21622");
    check("BrayCurtisDistance({x,-2,3},{x,5,-3})", //
        "13/(3+2*Abs(x))");
    check("BrayCurtisDistance({-1, -1}, {10, 10})", //
        "11/9");
  }

  @Test
  public void testBreak() {
    check("n = 0", //
        "0");
    check("While(True, If(n>10, Break()); n=n+1)", //
        "");
    check("n", //
        "11");
  }

  @Test
  public void testByteArray() {
    // message ByteArray: The argument at position 1 in ByteArray(asy) should be a vector of
    // unsigned byte values or a Base64 encoded string.
    check("ba=ByteArray(\"asy\")", //
        "ByteArray(asy)");

    check("ByteArray({})", //
        "{}");
    check("ba=ByteArray(\"AQIDBAUGBwg=\")", //
        "ByteArray[8 Bytes]");
    check("Normal(ba)", //
        "{1,2,3,4,5,6,7,8}");

  }

  @Test
  public void testCancel() {

    check("Simplify(((1 + Sqrt(5))/2)^7 + (-(1 + Sqrt(5))/2)^(-7))", //
        "29");

    check("Cancel((1/8-Cos(2/7*Pi)/8) / (8-8*Cos(2/7*Pi)))", //
        "1/64");
    // check("Factor(y*(2*3*x+Pi*x))", //
    // "(6+Pi)*x*y");
    check("Cancel((x*Log(2))/(2*3*x+Pi*x))", //
        "Log(2)/(6+Pi)");
    check("Cancel((x*Log(2))/(2*3*x^2+Pi*x^5))", //
        "Log(2)/(x*(6+Pi*x^3))");
    check("Cancel((x^4*Log(2))/(2*3*x^2+Pi*x^5))", //
        "(x^2*Log(2))/(6+Pi*x^3)");
    check("Cancel((x^7*Log(2))/(2*3*x+Pi*x))", //
        "(x^6*Log(2))/(6+Pi)");
    check("Cancel((c*d+d^2*x)^2/d^3 )", //
        "(c+d*x)^2/d");
    check("Cancel((c*d+d^2*x)^2/d^2 )", //
        "(c+d*x)^2");

    // github #188
    check("Cancel(4/n^4)", //
        "4/n^4");
    check("Cancel(n^4/4)", //
        "n^4/4");
    check("Cancel(n^4/4+n^3/2+n^2/4)", //
        "n^2/4+n^3/2+n^4/4");
    check("n^4/4+n^3/2+n^2/4", //
        "n^2/4+n^3/2+n^4/4");

    // check("Cancel(((1+x)*f(x))/x^2)", //
    // "x/(-1+x)");
    // check("Cancel((x*(1+x))/(-1+x^2))", //
    // "x/(-1+x)");
    // see rubi rule 27:
    check("Cancel(d/(c*d+d^2*x))", //
        "1/(c+d*x)");
    check("Cancel((c*d+d^2*x)/d)", //
        "c+d*x");
    check("Cancel((c*d+d^2*x)^(-1)/(d^2)^(-1)  )", //
        "d/(c+d*x)");
    check("Cancel((c*d+d^2*x) /(d^2))", //
        "(c+d*x)/d");
    check("Cancel((c*d+d^2*x)^(-2)/(d^2)^(-1)  )", //
        "1/(c+d*x)^2");
    check("Cancel((c*d+d^2*x)^2/d^2 )", //
        "(c+d*x)^2");

    check("Cancel(x / x ^ 2)", //
        "1/x");
    check("Cancel(x / x ^ 2 + y / y ^ 2)", //
        "1/x+1/y");
    check("Cancel(f(x) / x + x * f(x) / x ^ 2)", //
        "(2*f(x))/x");

    check("Cancel((x - a)/(x^2 - a^2) == 0 && (x^2 - 2*x + 1)/(x - 1) >= 0)", //
        "1/(a+x)==0&&x>=1");
    check("9+3*x+x^2", "9+3*x+x^2");
    check("(9+3*x+x^2)*(3+x)^(-1)", //
        "(9+3*x+x^2)/(3+x)");
    check("1+(9+3*x+x^2)*(3+x)^(-1)+x+(x+y)^(-1)", //
        "1+x+(9+3*x+x^2)/(3+x)+1/(x+y)");

    check("Cancel(x / x ^ 2)", //
        "1/x");
    check("Cancel(f(x) / x + x * f(x) / x ^ 2)", //
        "(2*f(x))/x");
    check("Cancel(x / x ^ 2 + y / y ^ 2)", //
        "1/x+1/y");
    check("Cancel((x^2 - 1)/(x - 1))", //
        "1+x");
    check("Cancel((x - y)/(x^2 - y^2) + (x^3 - 27)/(x^2 - 9) + (x^3 + 1)/(x^2 - x + 1))", //
        "1+x+(9+3*x+x^2)/(3+x)-1/(-x-y)");
    check("cancel((x - 1)/(x^2 - 1) + (x - 2)/(x^2 - 4))", //
        "1/(1+x)+1/(2+x)");
    check("together((x - 1)/(x^2 - 1) + (x - 2)/(x^2 - 4))", //
        "(3+2*x)/((1+x)*(2+x))");
    check("Cancel(x/(1+x^3))", //
        "x/(1+x^3)");
    check("Together((x - 1)/(x^2 - 1) + (x - 2)/(x^2 - 4))", //
        "(3+2*x)/((1+x)*(2+x))");
  }

  @Test
  public void testCarlsonRC() {
    check("CarlsonRC(4,7) // FunctionExpand", //
        "ArcCos(2/Sqrt(7))/Sqrt(3)");
    check("CarlsonRC(3,-7) // FunctionExpand", //
        "CarlsonRC(3,-7)");
    check("CarlsonRC(10,7) // FunctionExpand", //
        "ArcCosh(Sqrt(10/7))/Sqrt(3)");

    // TODO apfloat complex numbers
    // check(
    // "N(CarlsonRC(Exp(I*Pi/7), Exp(I*Pi/3)),30)", //
    // "0.918779490030764719521186118777-I*0.414652997036584002880355293518");
    check("N( Exp(I*Pi/7) ,30)", //
        "0.900968867902419126236102319507+I*0.433883739117558120475768332848");
    check("N( Exp(I*Pi/3) ,30)", //
        "0.5+I*0.866025403784438646763723170752");
    check("CarlsonRC(4., 5)", //
        "0.463648");
    check("CarlsonRC(3, 1.234567890123456789012345)", //
        "0.762626353743812501642456");
    check("CarlsonRC(3, 1.2345678901234567890123456789012345)", //
        "0.76262635374381250164245591358368878");
    check("CarlsonRC(y,y)", //
        "Piecewise({{ComplexInfinity,Re(y)<=0&&Im(y)==0}},1/Sqrt(y))");
    check("CarlsonRC(42,42)", //
        "1/Sqrt(42)");
    check("CarlsonRC(4., 5)", //
        "0.463648");
    check("CarlsonRC(Exp(I*Pi/7.0), Exp(I*Pi/3.0))", //
        "0.918779+I*(-0.414653)");
  }

  @Test
  public void testCarlsonRD() {
    check("CarlsonRD(2., 3.,5.0)", //
        "0.133211");
    check("CarlsonRD(1.0+I, 1-2*I, 5.0)", //
        "0.168723+I*0.0201344");
  }

  @Test
  public void testCarlsonRF() {
    check("CarlsonRF(x,x,x)", //
        "1/Sqrt(x)");
    check("CarlsonRF(1.,2.,3.)", //
        "0.726946");
    check("CarlsonRF(I, 1-2*I, 3.0+I)", //
        "0.791737+I*(-0.0471835)");
  }

  @Test
  public void testCarlsonRG() {
    check("CarlsonRG(x,x,y)", //
        "1/2*(Sqrt(y)+x*CarlsonRF(x,x,y))");
    check("CarlsonRG(3.,5.,11.0)", //
        "2.48078");
    check("CarlsonRG(I, 1-2*I, 3.0+I)", //
        "1.18073+I*0.0126244");
  }

  @Test
  public void testCarlsonRJ() {
    check("CarlsonRJ(2., 3., 5., 7.2)", //
        "0.104459");
    check("CarlsonRJ(0.7-0.2*I, 4, 2.0+I/3, 0.7*I)", //
        "0.533311+I*(-0.52701)");
  }

  @Test
  public void testCarmichaelLambda() {
    check("CarmichaelLambda(-n)", //
        "CarmichaelLambda(n)");
    check("CarmichaelLambda(0)", //
        "0");
    check("CarmichaelLambda(1)", //
        "1");
    check("CarmichaelLambda(2)", //
        "1");
    check("CarmichaelLambda(10)", //
        "4");
    check("CarmichaelLambda(15)", //
        "4");
    check("CarmichaelLambda(11)", //
        "10");
    check("CarmichaelLambda(35)", //
        "12");
    check("CarmichaelLambda(50)", //
        "20");
    check("Table(CarmichaelLambda(-k), {k, 12})", //
        "{1,1,2,2,4,2,6,2,6,4,10,2}");
    check("Table(CarmichaelLambda(10^k), {k, 0, 10})", //
        "{1,4,20,100,500,5000,50000,500000,5000000,50000000,500000000}");
  }

  @Test
  public void testCases() {
    // check(
    // " t : {__Integer} :> t^2", //
    // "(t:{__Integer}):>t^2");
    check("Cases({(c*x+d)^(1/3)}, " //
        + "y:(a_. x + b_.)^n_ /;  FreeQ({a,b}, x) && Head(n) == Rational :> {y, a*x + b, Numerator(n), Denominator(n), a, b}, {0, Infinity})", //
        "{{(d+c*x)^(1/3),d+c*x,1,3,c,d}}");

    check("Cases({b, 6, \\[Pi]}, _Symbol, Heads->True)", //
        "{List,b,Pi}");
    check("Cases({b, 6, \\[Pi]}, _Symbol)", //
        "{b,Pi}");
    check("Cases({x,-1,1,1},SparseArray({{1,0},{0,2}}))", //
        "{}");
    check("Cases({{1, 2, 3}, a, {4, 5}}, t : {__Integer} :> t^2)", //
        "{{1,4,9},{16,25}}");
    check("Cases({1,2,3,a}, _?NumberQ)", //
        "{1,2,3}");
    check("Cases({-2,7,-1.2,0,-5-3*I}, _?Negative)", //
        "{-2,-1.2}");

    check("Cases({{a, a}, {b, a}, {a, b, c}, {b, b}, {c, a}, {b, b, b}}, {__, a | b} | {c, __})", //
        "{{a,a},{b,a},{b,b},{c,a},{b,b,b}}");
    check("Cases({{2,1,0},{3,2,0},{2,0,0},{3,3,1}}, {_, 1 | 2, _})", //
        "{{2,1,0},{3,2,0}}");
    check("Cases({{a, a}, {b, a}, {a, b, c}, {b, b}, {c, a}, {b, b, b}}, {a|b, _})", //
        "{{a,a},{b,a},{b,b}}");
    check("Cases({a, 1, 2.5, \"string\"}, _Integer|_Real)", //
        "{1,2.5}");
    check("Cases(_Complex)[{1, 2*I, 3, 4-I, 5}]", //
        "{I*2,4-I}");
    check("Cases(1, 2)", //
        "{}");
    check("Cases(f(1, 2), 2)", //
        "{2}");
    check("Cases(f(f(1, 2), f(2)), 2)", //
        "{}");
    check("Cases(f(f(1, 2), f(2)), 2, 2)", //
        "{2,2}");
    check("Cases(f(f(1, 2), f(2), 2), 2, Infinity)", //
        "{2,2,2}");
    check("Cases({1, f(2), f(3, 3, 3), 4, f(5, 5)}, f(x__) :> Plus(x))", //
        "{2,9,10}");
    check("Cases({1, f(2), f(3, 3, 3), 4, f(5, 5)}, f(x__) -> Plus(x))", //
        "{2,3,3,3,5,5}");

    check("Cases(_Complex)[{1, 2*I, 3, 4-I, 5}]", //
        "{I*2,4-I}");
    check("Cases({x, a, b, x, c}, Except(x))", //
        "{a,b,c}");
    check("Cases({a, 0, b, 1, c, 2, 3}, Except(1, _Integer))", //
        "{0,2,3}");
    check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer)", //
        "{1,1,2,3,9}");
    check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -1)", //
        "{1,1,2,3,8,9,10}");
    check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -2)", //
        "{}");
    check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, {0,4})", //
        "{1,1,2,3,8,9,10}");

    check("Cases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x)", //
        "{a,10}");
    check("Cases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x, -2)", //
        "{a,8,10}");

    check("Cases({3, -4, 5, -2}, x_ /; x < 0)", //
        "{-4,-2}");
    check("Cases({3, 4, x, x^2, x^3}, x^_)", //
        "{x^2,x^3}");
    check("Cases({3, 4, x, x^2, x^3}, x^n_ -> n)", //
        "{2,3}");
    check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {_, _})", //
        "{{1,2},{5,4},{3,3}}");
    check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {a_, b_} -> a + b)", //
        "{3,9,6}");
    check("Cases(Sqrt(Range(100)), _Integer, {1}, 3)", //
        "{1,2,3}");
  }

  @Test
  public void testCatch() {
    check("Catch(f(Catch(Throw(a, u), u)), v)", //
        "f(a)");
    check("Catch(f(Catch(Throw(a, u), v)), u)", //
        "a");
    check("Catch(f(Catch(Throw(a, u), u)), v, f)", //
        "f(a)");
    check("Catch(f(Catch(Throw(a, u), v)), u, f)", //
        "f(a,u)");
    check("Catch(Scan(If(IntegerQ(#1),Null,Throw(False))&,{2,3});True)", //
        "True");
    check("Catch(Scan(If(IntegerQ(#1),Null,Throw(False))&,{b+a});True)", //
        "False");
    check("Catch(Scan(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", //
        "6");
    check("Catch(a; b; Throw(c); d; e)", //
        "c");
    check("$f(x_) := If(x > 10, Throw(overflow), x!);Catch($f(2) + $f(11))", //
        "Overflow");
    check("$f(x_) := If(x > 10, Throw(overflow), x!);Catch($f(2) + $f(3))", //
        "8");
    check("catch(do(If(i0! > 10^10, throw(i0)), {i0, 100}))", //
        "14");
    check("Catch(If(# < 0, Throw(#)) & /@ {1, 2, 0, -1, 5, 6})", //
        "-1");
    check("Catch(a^2 + b^2 + c^2 /. b :> Throw(bbb))", //
        "bbb");
    check("Catch({Catch({a, Throw(b), c}), d, e})", //
        "{b,d,e}");
    check("Catch(Throw /@ {a, b, c})", //
        "a");
    check("$f(x_) := (If(x < 0, Throw(\"negative\")); Sqrt(x));Catch(Sum($f(i0), {i0, 5, -5, -1}))", //
        "negative");
    // check("$lst={0,v1,n1};\n" +
    // " Catch(\n" +
    // " {Map(Function($lst=False;\n" +
    // " If($lst===False,Throw(False),$lst((1)))),\n" +
    // " u),$lst((2)),$lst((3))})","");
  }

  @Test
  public void testCatalan() {
    checkNumeric("N(Catalan)", //
        "0.915965594177219");
    check("N(Catalan,50)", //
        "0.91596559417721901505460351493238411077414937428167");
  }



  @Test
  public void testCatenate() {
    check("Catenate({{1, 2, 3}, {4, 5}})", //
        "{1,2,3,4,5}");
    check("Catenate({{1,2,3},{a,b,c},{4,5,6}})", //
        "{1,2,3,a,b,c,4,5,6}");
    check("Catenate({{1, 2}, <|a -> 1, b -> 2|>})", //
        "{1,2,1,2}");
    check("Catenate({<|a -> 1, b -> 2|>, <|c -> 3, a -> 5|>})", //
        "{1,2,3,5}");
  }


  @Test
  public void testCeiling() {
    check("Ceiling(Quantity(8.5, \"Meters\"))", //
        "9[Meters]");
    check("Ceiling(DirectedInfinity(0))", //
        "ComplexInfinity");
    check("Ceiling(DirectedInfinity((1/2-I*1/2)*Sqrt(2)))", //
        "DirectedInfinity((1/2-I*1/2)*Sqrt(2))");
    check("Ceiling(-9/4)", //
        "-2");
    check("Ceiling(1/3)", //
        "1");
    check("Ceiling(-1/3)", //
        "0");
    check("Ceiling(1.2)", //
        "2");
    check("Ceiling(3/2)", //
        "2");
    check("Ceiling(1.3 + 0.7*I)", //
        "2+I");
    check("Ceiling(2.6, 0.5)", //
        "3.0");
    check("Ceiling(10.4, -1) ", //
        "10");
    check("Ceiling(-10.4, -1) ", //
        "-11");

    check("Ceiling(1.5)", //
        "2");
    check("Ceiling(1.5 + 2.7*I)", //
        "2+I*3");
  }

  @Test
  public void testCharacterRange() {
    check("CharacterRange(50, 50)", //
        "{2}");
    check("CharacterRange(\" \", \"~\")", //
        "{ ,!,\",#,$,%,&,',(,),*,+,,,-,.,/,0,1,2,3,4,5,6,7,8,9,:,;,<,=,>,?,@,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,[,\\,],^,_,`,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,{,|,},~}");
    check("CharacterRange(1000, 1020)", //
        "{Ϩ,ϩ,Ϫ,ϫ,Ϭ,ϭ,Ϯ,ϯ,ϰ,ϱ,ϲ,ϳ,ϴ,ϵ,϶,Ϸ,ϸ,Ϲ,Ϻ,ϻ,ϼ}");
    check("CharacterRange(\"a\", \"z\")", //
        "{a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}");
    check("CharacterRange(\"0\", \"9\")", //
        "{0,1,2,3,4,5,6,7,8,9}");
  }

  @Test
  public void testCharacters() {
    check("Partition(Characters(\"this is a string\"), 3, 1) // InputForm", //
        "{{\"t\",\"h\",\"i\"},{\"h\",\"i\",\"s\"},{\"i\",\"s\",\" \"},{\"s\",\" \",\"i\"},{\" \",\"i\",\"s\"},{\"i\",\"s\",\" \"},{\"s\",\" \",\"a\"},{\" \",\"a\",\" \"},{\"a\",\" \",\"s\"},{\" \",\"s\",\"t\"},{\"s\",\"t\",\"r\"},{\"t\",\"r\",\"i\"},{\"r\",\"i\",\"n\"},{\"i\",\"n\",\"g\"}}");
  }

  @Test
  public void testCheck() {
    check("Check(0^(-42), failure)", //
        "failure");

    check("Check(2^(3), failure)", //
        "8");
    check("Check(0^(-42), failure)", //
        "failure");
    check("Check(0^0, failure)", //
        "failure");
  }

  @Test
  public void testCheckAbort() {
    check("CheckAbort(Abort(); -1, 41) + 1", //
        "42");
    check("CheckAbort(abc; -1, 41) + 1", //
        "0");
  }

  @Test
  public void testChessboardDistance() {
    check("ChessboardDistance({-1, -1.5}, {1, 1})", //
        "2.5");
    check("ChessboardDistance({-1, -1}, {1, 1})", //
        "2");
  }

  @Test
  public void testChineseRemainder() {

    check("mydata = {931074546, 117172357, 482333642, 199386034, 394354985};", //
        "");
    check("mykeys={104881749667,21415628519,50887356077,224339216447,52131541939};", //
        "");
    check("ChineseRemainder(mydata,mykeys)", //
        "193166521325770200917536987616405491738358926101878755");

    check("ChineseRemainder({-1/2,-2,3},{-1/2,-2,3})", //
        "ChineseRemainder({-1/2,-2,3},{-1/2,-2,3})");
    check("ChineseRemainder({1,-15}, {284407855036305,47})", //
        "8532235651089151");
    check("ChineseRemainder({-2,-17}, {284407855036305,47})", //
        "9669867071234368");
    check("ChineseRemainder({2,17}, {284407855036305,47})", //
        "3697302115471967");
    check("ChineseRemainder({2123, 7213},{11,13})", //
        "11");
    // wikipedia example
    check("ChineseRemainder({0,3,4},{3,4,5})", //
        "39");

    check("ChineseRemainder({23},{17})", //
        "6");
    check("ChineseRemainder({91},{25})", //
        "16");
    check("ChineseRemainder({913},{25})", //
        "13");
    check("ChineseRemainder({3,4},{4,5})", //
        "19");

    check("ChineseRemainder({1, 2}, {6, 10})", //
        "ChineseRemainder({1,2},{6,10})");
  }

  @Test
  public void testChop() {
    check("-4.898587196589413*^-16*I ", //
        "I*(-0.0000000000000004898587196589413)");
    check("Chop(  1. - 4.898587196589413*^-16*I)", //
        "1");
    check(
        "Chop(f( {-1. + 1.2246467991473532*^-16*I, 1. - 2.4492935982947064*^-16*I, -1. + 3.6739403974420594*^-16*I, 1. - 4.898587196589413*^-16*I} ))", //
        "f({-1,1,-1,1})");
    check("Chop(abc)", //
        "abc");
    check("Chop(Sin(3/7))", //
        "Sin(3/7)");
    check("Chop(Sin(1/1000000000000000))", //
        "Sin(1/1000000000000000)");

    check("{{2.02.77556*10^-16, -3.88578*10^-16, -5.55112*10^-16}}=={{0,0,0}}", //
        "True");
    check("Chop(x)", //
        "x");
    check("Chop(0.00000000001)", //
        "0");
    check("Chop(18.35051+I*1.32213*10^-11)", //
        "18.35051");

    check("Exp(N(Range(4)*Pi*I))", //
        "{-1.0,1.0+I*(-2.44929*10^-16),-1.0+I*3.67394*10^-16,1.0+I*(-4.89859*10^-16)}");
    check("Chop(Exp(N(Range(4)*Pi*I)))", //
        "{-1.0,1.0,-1.0,1.0}");

    check("x=N(Pi);r=Rationalize(x,10^-12)", //
        "4272943/1360120");
    check("Chop(x-r)==0", //
        "True");
    check("Chop(x-r,10^-14)==0", //
        "False");

    check("Chop(10.^-12+2*I)", //
        "I*2.0");
    check("Chop(2. + 10.^-12*I)", //
        "2.0");
  }

  @Test
  public void testClear() {
    check("x=100;y=42", //
        "42");
    check("x", //
        "100");
    check("y", //
        "42");
    check("Clear(x,y)", //
        "");
    check("x", //
        "x");
    check("y", //
        "y");
  }

  @Test
  public void testClearAll() {
    check("x=100;y=42", //
        "42");
    check("x", //
        "100");
    check("y", //
        "42");
    check("ClearAll(x,y)", //
        "");
    check("x", //
        "x");
    check("y", //
        "y");
  }

  @Test
  public void testClearAttributes() {
    // print error message "SetAttributes: test is not a known attribute."
    check("SetAttributes(f, {Orderless, Test})", //
        "");
    // print error message "ClearAttributes: test is not a known attribute."
    check("ClearAttributes(f, {Orderless, Test})", //
        "ClearAttributes(f,{Orderless,test})");

    check("SetAttributes(f, {Orderless, Flat})", //
        "");
    check("Attributes(f)", //
        "{Flat,Orderless}");
    check("ClearAttributes(f, Flat)", //
        "");
    check("Attributes(f)", //
        "{Orderless}");
    check("ClearAttributes(f, Flat)", //
        "");
    check("Attributes(f)", //
        "{Orderless}");
  }

  @Test
  public void testClebschGordan() {
    check("ClebschGordan({j, m}, {j1, m1}, {j2, m2})", //
        "(-1)^(j-j1+m2)*Sqrt(1+2*j2)*ThreeJSymbol({j,m},{j1,m1},{j2,-m2})");
    // https://en.wikipedia.org/wiki/Table_of_Clebsch%E2%80%93Gordan_coefficients
    check("ClebschGordan({3/2, -3/2}, {3/2, 3/2}, {1, 0})", //
        "3/2*1/Sqrt(5)");


    // print message "is not physical
    check("ClebschGordan({2, 1}, {2, 4}, {4, 2})", //
        "0");

    check("ClebschGordan({5, 0}, {4, 0}, {1, 0})", //
        "Sqrt(5/33)");
    check("ClebschGordan({1/2, -1/2}, {1/2, -1/2}, {1, -1})", //
        "1");
    check("ClebschGordan({1/2, -1/2}, {1, 0}, {1/2, -1/2})", //
        "-1/Sqrt(3)");

    check("With({j1 = 3, j2 = 2},\n" //
        + "  Table(Sum(\n" //
        + "    If(Abs(m1 + m2) > j || Abs(m1 + m2) > ji, 0, \n" //
        + "     ClebschGordan({j1, m1}, {j2, m2}, {j, \n" //
        + "        m1 + m2}) ClebschGordan({j1, m1}, {j2, m2}, {ji, \n" //
        + "        m1 + m2})), {m1, -j1, j1}, {m2, -j2, j2}), {j, Abs(j1 - j2), \n" //
        + "    j1 + j2}, {ji, Abs(j1 - j2), j1 + j2})) // MatrixForm", //
        "{{3,0,0,0,0},\n" //
            + " {0,5,0,0,0},\n" //
            + " {0,0,7,0,0},\n" //
            + " {0,0,0,9,0},\n" //
            + " {0,0,0,0,11}}");
  }

  @Test
  public void testClip() {
    check("Clip(7.5,{-7.0,7.0})", //
        "7.0");
    check("Table(Clip(n,{-2,2}),{n,{-E,-1/17,1/7,2/3,5/2,Pi}})", //
        "{-2,-1/17,1/7,2/3,2,2}");
    check("Table(Clip(n,{-1/2,1/2}),{n,{-Infinity,-E,-1/17,1/7,2/3,5/2,Pi,Infinity}})", //
        "{-1/2,-1/2,-1/17,1/7,1/2,1/2,1/2,1/2}");
    check("Clip({-Infinity,1,2,{7},2.00001})", //
        "{-1,1,1,{1},1}");
    check("Clip(Infinity)", //
        "1");
    check("Clip(0)", //
        "0");
    check("Clip({1,2,{7},a})", //
        "Clip({1,2,{7},a})");
    check("Clip(Tan(E),{-1/2,1/2})", //
        "Tan(E)");
    check("Clip(Tan(2*E),{-1/2,1/2})", //
        "-1/2");
    check("Clip(Tan(-2*E),{-1/2,1/2})", //
        "1/2");

    check("Clip(Tan(E), {-1/2,1/2}, {a,b})", //
        "Tan(E)");
    check("Clip(Tan(2*E), {-1/2,1/2}, {a,b})", //
        "a");
    check("Clip(Tan(-2*E), {-1/2,1/2}, {a,b})", //
        "b");

    check("Clip(x)", //
        "Clip(x)");
    check("Clip(1)", //
        "1");
    check("Clip(-1)", //
        "-1");
    check("Clip(Sin(Pi/7))", //
        "Sin(Pi/7)");
    check("Clip(Tan(E))", //
        "Tan(E)");
    check("Clip(Tan(2*E))", //
        "-1");
    check("Clip(Tan(-2*E))", //
        "1");
    check("Clip({-2, 0, 2})", //
        "{-1,0,1}");

  }

  @Test
  public void testCoefficient() {
    check("Coefficient(x+y+z, x+y)", //
        "1");

    // https://oeis.org/A236191
    check("Coefficient(Series((x + x^2 + 2*x^3 + x^4 - x^5)/(1 + 4*x^3 - x^6), {x, 0, 38}), x^10)", //
        "-55");
    check("Coefficient(ComplexInfinity,{x,y,z})", //
        "{0,0,0}");
    check("Coefficient(ComplexInfinity,x)", //
        "0");
    check("Coefficient(7*y^w, y, w)", //
        "7");
    check("Coefficient(7*y^(3*w), y, 3*w)", //
        "7");
    check("Coefficient(c*x^(-2)+a+b*x,x,-2)", //
        "c");

    // TODO
    // check("Coefficient(2*x*y+5*x^3,x^3,0)", //
    // "0");
    check("Coefficient(x*y,z,0)", //
        "x*y");
    check("Coefficient(2*x*y+5*x^3,2*x)", //
        "y");
    check("Coefficient((2*x)^7*y+5*x^3,x^3)", //
        "5");
    check("Coefficient(2*x *y+5*x^3,x^3)", //
        "5");

    check("Coefficient(Cos(x*y), Cos(x*y))", //
        "1");
    check("Coefficient(2*x^2,x^2)", //
        "2");
    check("Coefficient(2*x^4,x^2)", //
        "0");
    check("Coefficient(2*x^2,x^3)", //
        "0");
    check("Coefficient((1+2*x)/Sqrt(3),x,1)", //
        "2/Sqrt(3)");
    check("g = (x + 3)^5;Coefficient(g, x, #) & /@ Range(0, Exponent(g, x))", //
        "{243,405,270,90,15,1}");
    // http://oeis.org/A133314
    check("b(0) = 1; " //
        + "b(n_) := b(n)=-Sum(Binomial(n, j)*a(j)*b(n-j), {j, 1, n}); crow(0) = {1}; " //
        + "crow(n_) := Coefficient(b(n), #)& /@ (Times @@ (a /@ #)&) /@ IntegerPartitions(n); " //
        + "Table(crow(n), {n, 0, 8}) // Flatten", //
        "{1,-1,-1,2,-1,6,-6,-1,8,6,-36,24,-1,10,20,-60,-90,240,-120,-1,12,30,-90,20,-360,\n"
            + "480,-90,1080,-1800,720,-1,14,42,-126,70,-630,840,-420,-630,5040,-4200,2520,-\n"
            + "12600,15120,-5040,-1,16,56,-168,112,-1008,1344,70,-1680,-1260,10080,-8400,-1680,\n"
            + "6720,20160,-67200,40320,2520,-50400,151200,-141120,40320}");
    check("Coefficient(a+b*x,x,0)", //
        "a");
    check("Coefficient(a+b*x,x,1)", //
        "b");
    check("Coefficient(a*b*x,x,1)", //
        "a*b");
    check("Coefficient(x*y,y,Exponent(x*y,y))", //
        "x");
    check("Coefficient(-3*a(1)*(2*a(1)^2-a(2))+3*a(1)*a(2)-a(3),a(1)*a(2))", //
        "6");

    check("Coefficient(SeriesData(x, 0, {1, 1, 0, 1, 1, 0, 1, 1}, 0, 9, 1), x, 4)", //
        "1");
    check("Coefficient(x^2*y^2 + 3*x + 4*y+y^w, y, 0)", //
        "3*x");
    check("Coefficient(x^2*y^2 + 3*x + 4*y+3/(y^4), x, 0)", //
        "3/y^4+4*y");
    check("Coefficient(x^2*y^2 + 3*x + 4*y+Sin(y), x, 0)", //
        "4*y+Sin(y)");
    check("Coefficient(x^2*y^2 + 3*x + 4*y+Sin(y), y, 0)", //
        "3*x+Sin(y)");
    check("Coefficient(x^2*y^2 + 3*x + 4*y+Sin(y)^3, x, 0)", //
        "4*y+Sin(y)^3");
    check("Coefficient(x^2*y^2 + 3*x + 4*y+Sin(y)^3, y, 0)", //
        "3*x+Sin(y)^3");

    check("poly=(c*x-2*y+z)^7", //
        "(c*x-2*y+z)^7");
    check("Coefficient(poly, x^2*y*z^4)", //
        "-210*c^2");

    check("coeff=CoefficientList(poly,{x,y,z})", //
        "{{{0,0,0,0,0,0,0,1},{0,0,0,0,0,0,-14,0},{0,0,0,0,0,84,0,0},{0,0,0,0,-280,0,0,0},{\n"
            + "0,0,0,560,0,0,0,0},{0,0,-672,0,0,0,0,0},{0,448,0,0,0,0,0,0},{-128,0,0,0,0,0,0,0}},{{\n"
            + "0,0,0,0,0,0,7*c,0},{0,0,0,0,0,-84*c,0,0},{0,0,0,0,420*c,0,0,0},{0,0,0,-1120*c,0,\n"
            + "0,0,0},{0,0,1680*c,0,0,0,0,0},{0,-1344*c,0,0,0,0,0,0},{448*c,0,0,0,0,0,0,0},{0,0,\n"
            + "0,0,0,0,0,0}},{{0,0,0,0,0,21*c^2,0,0},{0,0,0,0,-210*c^2,0,0,0},{0,0,0,840*c^2,0,\n"
            + "0,0,0},{0,0,-1680*c^2,0,0,0,0,0},{0,1680*c^2,0,0,0,0,0,0},{-672*c^2,0,0,0,0,0,0,\n"
            + "0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{0,0,0,0,35*c^3,0,0,0},{0,0,0,-280*c^3,\n"
            + "0,0,0,0},{0,0,840*c^3,0,0,0,0,0},{0,-1120*c^3,0,0,0,0,0,0},{560*c^3,0,0,0,0,0,0,\n"
            + "0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{0,0,0,35*c^4,0,0,0,0},{\n"
            + "0,0,-210*c^4,0,0,0,0,0},{0,420*c^4,0,0,0,0,0,0},{-280*c^4,0,0,0,0,0,0,0},{0,0,0,\n"
            + "0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{0,0,21*c^5,0,\n"
            + "0,0,0,0},{0,-84*c^5,0,0,0,0,0,0},{84*c^5,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,\n"
            + "0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{0,7*c^6,0,0,\n"
            + "0,0,0,0},{-14*c^6,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,\n"
            + "0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{c^7,0,0,0,0,0,0,\n"
            + "0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,\n"
            + "0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}}}");
    check("Part(coeff, 3,2,5)", //
        "-210*c^2");

    check("Coefficient(poly, x^5)", //
        "84*c^5*y^2-84*c^5*y*z+21*c^5*z^2");
    check("Coefficient(poly, x, 5)", //
        "84*c^5*y^2-84*c^5*y*z+21*c^5*z^2");
    check("coeff[[6]]", //
        "{{0,0,21*c^5,0,0,0,0,0},{0,-84*c^5,0,0,0,0,0,0},{84*c^5,0,0,0,0,0,0,0},{0,0,0,0,\n"
            + "0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}}");
    check("Part(coeff, 6,1,3)", //
        "21*c^5");
    check("Part(coeff, 6,2,2)", //
        "-84*c^5");
    check("Part(coeff, 6,3,1)", //
        "84*c^5");
    // check("Apply(Plus,((Coefficient(x*(b+a),x,#1)*x^#1)&))", "");
    check("Coefficient(x*y,y,1)", //
        "x");
    check("Coefficient(Sin(x*y),y,1)", //
        "0");
    check("Coefficient(x*y,y,Exponent(x*y,y))", //
        "x");

    check("Coefficient(Sin(a)^3*#1 + b*y + c, #1)", //
        "Sin(a)^3");
    check("Coefficient((#1 + 2)^2 + (#1 + 3)^3, #1, 0)", //
        "31");
    check("Coefficient(42*#1^2+y^3*#1^2+(#1 + 2)^2*(#1 + 2)^2,#1,2)", //
        "66+y^3");
    check("Coefficient(#1,#1,1)", //
        "1");
    check("Coefficient(#1^2,#1,2)", //
        "1");

    check("Coefficient(Null,x,0)", //
        "");
    check("Coefficient(Null,x)", //
        "0");

    check("Coefficient(Sin(x^2),x^2)", //
        "0");

    check("Coefficient(Sin(x^2)^2,Sin(x^2),2)", //
        "1");
    check("Coefficient(2*Sin(x^2)^3,Sin(x^2),3)", //
        "2");
    check("Coefficient(f(x)+2*Sin(x^2)^3,Sin(x^2),3)", //
        "2");
    check("Coefficient(f(x^2)+2*f(x^2)^3,f(x^2),3)", //
        "2");
    check("ExpandAll((x + y)*(x + 2*y)*(3*x + 4*y + 5))", //
        "5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
    check("Coefficient(Sin(x^2),Sin(x^2))", //
        "1");
    check("Coefficient(x*(b+a),x,1)*x^1", //
        "(a+b)*x");
    check("Coefficient((x + 1)^3, x, 2)", //
        "3");
    check("Coefficient(a*x + b*y + c, x)", //
        "a");
    check("Coefficient(Sin(a)^3*x + b*y + c, x)", //
        "Sin(a)^3");
    check("Coefficient(Sin(a*x)^3*x + b*y + c, x)", //
        "Sin(a*x)^3");
    check("Coefficient((x + 2)^2 + (x + 3)^3, x, 0)", //
        "31");
    check("Coefficient(v,x,1)", //
        "0");
    check("Coefficient(42,x,0)", //
        "42");
    check("Coefficient(42*a,x,0)", //
        "42*a");
    check("Coefficient(x,x,1)", //
        "1");
    check("Coefficient(x^2,x,2)", //
        "1");
    check("Coefficient(42*x^2+y^3*x^2+(x + 2)^2*(x + 2)^2,x,2)", //
        "66+y^3");
    check("Coefficient(2*x*a,x,1)", //
        "2*a");
    check("Coefficient(2*x*a,x,2)", //
        "0");
    check("Coefficient(2*x*a,x,3)", //
        "0");
    check("Coefficient(2*x*a,x,4)", //
        "0");
    check("Coefficient(2*x^2*a+x,x,1)", //
        "1");
    check("Coefficient(2*x^2*a,x,2)", //
        "2*a");
    check("Coefficient(2*x^3*a,x,3)", //
        "2*a");
    check("Coefficient(2*x^4*a,x,4)", //
        "2*a");
    check("Coefficient(0,x,0)", //
        "0");

    // allow multinomials
    check("ExpandAll((x + y)^4)", //
        "x^4+4*x^3*y+6*x^2*y^2+4*x*y^3+y^4");
    check("Coefficient((x + y)^4, x*y^3)", //
        "4");
    check("Coefficient((x + y)^4,  y^4)", //
        "1");
    check("Coefficient((x + y)^4,  y,4)", //
        "1");

    check("Expand((x + y)*(x + 2*y)*(3*x + 4*y + 5))", //
        "5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
    check("ExpandAll((x + y)*(x + 2*y)*(3*x + 4*y + 5))", //
        "5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
    check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x^2*y)", //
        "13");
    check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x*y^2)", //
        "18");
    check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x*y)", //
        "15");
    check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), y^3)", //
        "8");
  }

  @Test
  public void testCoefficientList() {

    check("CoefficientList(Series(2*x, {x, 0, 9}), x)", //
        "{0,2}");
    check("Series(2*x, {x, 0, 9})", //
        "2*x+O(x)^10");

    check("CoefficientList((x + y)^3, z)", //
        "{(x+y)^3}");
    check("CoefficientList((x - 2 y + 3 z)^3, {x, y, z})", //
        "{{{0,0,0,27},{0,0,-54,0},{0,36,0,0},{-8,0,0,0}},{{0,0,27,0},{0,-36,0,0},{12,0,0,\n" //
            + "0},{0,0,0,0}},{{0,9,0,0},{-6,0,0,0},{0,0,0,0},{0,0,0,0}},{{1,0,0,0},{0,0,0,0},{0,\n" //
            + "0,0,0},{0,0,0,0}}}");

    check("CoefficientList(Series(Log(1-x), {x, 0, 9}),x)", //
        "{0,-1,-1/2,-1/3,-1/4,-1/5,-1/6,-1/7,-1/8,-1/9}");


    // check("1.0 * 10.0* x^9", //
    // "10.0*x^9");
    check("Expand((1.0 + x)^10)", //
        "1.0+10.0*x+45.0*x^2+120.0*x^3+210.0*x^4+252.0*x^5+210.0*x^6+120.0*x^7+45.0*x^8+10.0*x^\n"//
            + "9+x^10");
    check("CoefficientList((1.0 + x)^10 , x)", //
        "{1.0,10.0,45.0,120.0,210.0,252.0,210.0,120.0,45.0,10.0,1}");

    check("CoefficientList(Series(-(x/(-1 + x + x^2)), {x, 0, 20}), x)", //
        "{0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765}");

    // https://oeis.org/A236191
    check("CoefficientList(Series((x + x^2 + 2*x^3 + x^4 - x^5)/(1 + 4*x^3 - x^6), {x, 0, 38}), x)", //
        "{0,1,1,2,-3,-5,-8,13,21,34,-55,-89,-144,233,377,610,-987,-1597,-2584,4181,6765,\n" //
            + "10946,-17711,-28657,-46368,75025,121393,196418,-317811,-514229,-832040,1346269,\n" //
            + "2178309,3524578,-5702887,-9227465,-14930352,24157817,39088169}");

    check("CoefficientList(ComplexInfinity,x)", //
        "{ComplexInfinity}");
    check("CoefficientList(ComplexInfinity, {x,y,z} )", //
        "{{{ComplexInfinity}}}");

    check("CoefficientList(7*y^w, y )", //
        "{7*y^w}");
    check("CoefficientList(2*x*y+5*x^3,2*x)", //
        "{5*x^3+2*x*y}");
    check("CoefficientList((2*x)^7*y+5*x^3,x^3)", //
        "{128*x^7*y,5}");
    check("CoefficientList(2*x *y+5*x^3,x^3)", //
        "{2*x*y,5}");

    check("CoefficientList(Cos(x*y), Cos(x*y))", //
        "{0,1}");
    // http://oeis.org/A000045 - Fibonacci numbers
    check("CoefficientList(Series(-(x/(-1 + x + x^2)), {x, 0, 20}), x)", //
        "{0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765}");

    check("CoefficientList(x^2*y^2 + 3*x + 4*y+y^w, {x, y})", //
        "{{y^w,4,0},{3,0,0},{0,0,1}}");
    check("CoefficientList(x^2*y^2 + 3*x + 4*y+3/(y^4), {x, y})", //
        "{{3/y^4,4,0},{3,0,0},{0,0,1}}");
    check("CoefficientList(x^2*y^2 + 3*x + 4*y+Sin(y), {x, y})", //
        "{{Sin(y),4,0},{3,0,0},{0,0,1}}");
    check("CoefficientList(x^2*y^2 + 3*x + 4*y+Sin(y)^3, {x, y})", //
        "{{Sin(y)^3,4,0},{3,0,0},{0,0,1}}");
    check("CoefficientList(0, {x})", //
        "{}");
    check("CoefficientList(0, {x, y})", //
        "{}");
    check("CoefficientList(x^2*y^2 + 3*x + 4*y + 7*y^5, {x, y})", //
        "{{0,4,0,0,0,7},{3,0,0,0,0,0},{0,0,1,0,0,0}}");
    check("CoefficientList(x^2*y^2 + 3*x + 4*y + 7*y^5, {x, y, z})", //
        "{{{0},{4},{0},{0},{0},{7}},{{3},{0},{0},{0},{0},{0}},{{0},{0},{1},{0},{0},{0}}}");
    check("CoefficientList(x^2*y^2 + 3*x + 4*y + 7*y^5+z, {x, y, z})", //
        "{{{0,1},{4,0},{0,0},{0,0},{0,0},{7,0}},{{3,0},{0,0},{0,0},{0,0},{0,0},{0,0}},{{0,\n"
            + "0},{0,0},{1,0},{0,0},{0,0},{0,0}}}");

    check("CoefficientList(x^2*y^2 + 3*x + 4*y, {x, y})", //
        "{{0,4,0},{3,0,0},{0,0,1}}");
    check("CoefficientList(x^2*y^2 + 3*x + 4*y, {x, y, z})", //
        "{{{0},{4},{0}},{{3},{0},{0}},{{0},{0},{1}}}");

    check("CoefficientList(a+b*x, x)", //
        "{a,b}");
    check("CoefficientList(a+b*x+c*x^2, x)", //
        "{a,b,c}");
    check("CoefficientList(a+c*x^2, x)", //
        "{a,0,c}");
    check("CoefficientList(0, x)", //
        "{}");
    check("CoefficientList((x+3)^5, x)", //
        "{243,405,270,90,15,1}");
    check("CoefficientList(1 + 6*x - x^4, x)", //
        "{1,6,0,0,-1}");
    check("CoefficientList((1 + x)^10 , x)", //
        "{1,10,45,120,210,252,210,120,45,10,1}");
    check("CoefficientList(a*42*x^3+12*b*x+c*4, x)", //
        "{4*c,12*b,0,42*a}");
  }

  @Test
  public void testCoefficientRules() {
    check("CoefficientRules(x^3+3*x^2*y+3*x*y^2+y^3, {x,y})", //
        "{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");
    check("CoefficientRules(x^3+3*x^2*y+3*x*y^2+y^3)", //
        "{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");

    check("CoefficientRules(7*y^w, {y,z})", //
        "{{0,0}->7*y^w}");
    check("CoefficientRules(7*y^(3*w), y )", //
        "{{0}->7*y^(3*w)}");
    check("CoefficientRules(c*x^2+a+b*x,x)", //
        "{{2}->c,{1}->b,{0}->a}");
    check("CoefficientRules(c*x^(-2)+a+b*x,x)", //
        "{{0}->a+c/x^2+b*x}");

    check("CoefficientRules(SeriesData(x, 0, {1, 1, 0, 1, 1, 0, 1, 1}, 0, 9, 1))", //
        "{{0}->1+x+x^3+x^4+x^6+x^7+O(x)^9}");
    check("CoefficientRules((x + y)^3)", //
        "{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");
    check("CoefficientRules( a*x*y^2 + b*x^2*z, {x, y, z}, DegreeReverseLexicographic)", //
        "{{1,2,0}->a,{2,0,1}->b}");

    check("CoefficientRules((x + y)^3)", "{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");
    // check("CoefficientRules(x^2 y^2 + x^3, {x, y})", "{x^3,x^2*y^2}");
    // check("CoefficientRules(x^2 y^2 + x^3, {x,
    // y},\"DegreeLexicographic\")", "{x^2*y^2,x^3}");
    check("CoefficientRules((x + 1)^5, x, Modulus -> 2)", //
        "{{5}->1,{4}->1,{1}->1,{0}->1}");

    check(
        "CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z})", //
        "{{5,4,2}->-10,{2,5,3}->7,{2,1,5}->-10,{1,5,4}->-7,{1,4,3}->6,{1,3,3}->6,{1,2,1}->\n"
            + "3,{0,4,1}->1,{0,2,1}->-7,{0,0,5}->2}");

    check(
        "CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeLexicographic)", //
        "{{0,0,5}->2,{0,2,1}->-7,{0,4,1}->1,{1,2,1}->3,{1,3,3}->6,{1,4,3}->6,{1,5,4}->-7,{\n"
            + "2,1,5}->-10,{2,5,3}->7,{5,4,2}->-10}");
    check(
        "CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, DegreeLexicographic)", //
        "{{5,4,2}->-10,{2,5,3}->7,{1,5,4}->-7,{2,1,5}->-10,{1,4,3}->6,{1,3,3}->6,{0,4,1}->\n"
            + "1,{0,0,5}->2,{1,2,1}->3,{0,2,1}->-7}");
    check(
        "CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeDegreeReverseLexicographic)", //
        "{{0,2,1}->-7,{1,2,1}->3,{0,4,1}->1,{0,0,5}->2,{1,3,3}->6,{1,4,3}->6,{2,1,5}->-10,{\n"
            + "2,5,3}->7,{1,5,4}->-7,{5,4,2}->-10}");
    check(
        "CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, DegreeReverseLexicographic)", //
        "{{5,4,2}->-10,{2,5,3}->7,{1,5,4}->-7,{1,4,3}->6,{2,1,5}->-10,{1,3,3}->6,{0,4,1}->\n"
            + "1,{0,0,5}->2,{1,2,1}->3,{0,2,1}->-7}");
    check(
        "CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeDegreeLexicographic)", //
        "{{0,2,1}->-7,{1,2,1}->3,{0,4,1}->1,{0,0,5}->2,{1,3,3}->6,{2,1,5}->-10,{1,4,3}->6,{\n"
            + "2,5,3}->7,{1,5,4}->-7,{5,4,2}->-10}");
  }

  @Test
  public void testCollect() {
    check("Collect((1 + a + x)^4, x)", //
        "1+4*a+6*a^2+4*a^3+a^4+(4+12*a+12*a^2+4*a^3)*x+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4");
    check("Collect((1 + a + x)^4, x, Factor)", //
        "(1+a)^4+4*(1+a)^3*x+6*(1+a)^2*x^2+4*(1+a)*x^3+x^4");

    check("Collect(a*x*Log(x)+ b*(x*Log(x)), x*Log(x))", //
        "(a+b)*x*Log(x)");
    check("Collect(e+f*x, {})", //
        "e+f*x");
    check("Collect(e+f*x, {x})", //
        "e+f*x");
    check("Collect(e+f*x, x)", //
        "e+f*x");
    check("Collect((1 + a + x)^4, x)", //
        "1+4*a+6*a^2+4*a^3+a^4+(4+12*a+12*a^2+4*a^3)*x+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4");
    check("Collect((1 + a + x)^4, x, Simplify)", //
        "(1+a)^4+4*(1+a)^3*x+6*(1+a)^2*x^2+4*(1+a)*x^3+x^4");
    check("Collect(x^2 + y*x^2 + x*y + y + a*y, {x, y})", //
        "(1+a)*y+x*y+x^2*(1+y)");
    check("Collect(a*x^2 + b*x^2 + a*x - b*x + c, x)", //
        "c+(a-b)*x+(a+b)*x^2");
    check("Collect(a*Exp(2*x) + b*Exp(2*x), Exp(2*x))", //
        "(a+b)*E^(2*x)");
    check("a*Exp(2*x) + b*Exp(2*x)", //
        "a*E^(2*x)+b*E^(2*x)");
    // check("Collect(D(f(Sqrt(x^2 + 1)), {x, 3}), Derivative(_)[f][_],
    // Together)", "");
    check("x*(4*a^3+12*a^2+12*a+4)+x^4+(4*a+4)*x^3+(6*a^2+12*a+6)*x^2+a^4+4*a^3+6*a^2+4*a+1", //
        "1+4*a+6*a^2+4*a^3+a^4+(4+12*a+12*a^2+4*a^3)*x+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4");
    check("x+x^4", //
        "x+x^4");
    check("Collect(a, x)", //
        "a");
    check("Collect(a*y, {x,y})", //
        "a*y");
    check("Collect(42*a, {x,y})", //
        "42*a");
    check("Collect(a*Sqrt(x) + Sqrt(x) + x^(2/3) - c*x + 3*x - 2*b*x^(2/3) + 5, x)", //
        "5+(1+a)*Sqrt(x)+(1-2*b)*x^(2/3)+(3-c)*x");
    check("Collect(3*b*x + x, x)", //
        "(1+3*b)*x");
    check("Collect(a*x^4 + b*x^4 + 2*a^2*x - 3*b*x + x - 7, x)", "-7+(1+2*a^2-3*b)*x+(a+b)*x^4");
    check("Collect((1 + a + x)^4, x)",
        "1+4*a+6*a^2+4*a^3+a^4+(4+12*a+12*a^2+4*a^3)*x+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4");


    check("Collect(a*x + b*y + c*x, x)", //
        "(a+c)*x+b*y");
    check("Collect((x + y + z + 1)^4, {x, y})", //
        "1+x^4+y^4+4*z+y^3*(4+4*z)+x^3*(4+4*y+4*z)+6*z^2+y^2*(6+12*z+6*z^2)+x^2*(6+6*y^2+\n"
            + "12*z+y*(12+12*z)+6*z^2)+4*z^3+y*(4+12*z+12*z^2+4*z^3)+x*(4+4*y^3+12*z+y^2*(12+12*z)+\n"
            + "12*z^2+y*(12+24*z+12*z^2)+4*z^3)+z^4");
  }

  @Test
  public void testCommonest() {
    // https://en.wikipedia.org/wiki/Mode_(statistics)
    check("Commonest({1, 3, 6, 6, 6, 6, 7, 7, 12, 12, 17})", //
        "{6}");
    // bimodal
    check("Commonest({1, 1, 2, 4, 4})", //
        "{1,4}");

    check("Commonest({b, a, c, 2, a, b, 1, 2}, 4)", //
        "{b,a,2,c}");
    check("Commonest({b, a, c, 2, a, b, 1, 2})", //
        "{b,a,2}");
    check("Commonest({1, 2, 2, 3, 3, 3, 4})", //
        "{3}");
    // print warning: Commonest: The requested number of elements 6 is greater than the number of
    // distinct elements 5. Only 5 elements will be returned.
    check("Commonest({b, a, c, 2, a, b, 1, 2}, 6)", //
        "{b,a,2,c,1}");
  }

  @Test
  public void testComplement() {
    check("Complement({2, -2, 1, 3}, {2, 1, -2, -1},SameTest->(Abs(#1)==Abs(#2) &))", //
        "{3}");
    check(
        "Complement({1.1, 3.4, .5, 7.6, 7.1, 1.9}, {1.2, 3.3, 1.3},SameTest->(Floor(#1)==Floor(#2)&))", //
        "{0.5,7.1}");

    check(
        "Complement({{1, 2}, {3}, {4, 5, 6}, {9, 5}}, {{2, 1}, {8, 4, 3}},SameTest->(Total(#1)==Total(#2) &))", //
        "{{9,5}}");

    check("Complement(#,#)", //
        "Slot()");
    check("Complement(#,#1*#2)", //
        "Complement(#1,#1*#2)");
    check("Complement(#2,#1)", //
        "#2");

    check("Complement({},{})", "{}");
    check("Complement({1,2,3},{2,3,4})", "{1}");
    check("Complement({2,3,4},{1,2,3})", "{4}");
    check("Complement({1},{2})", "{1}");
    check("Complement({1,2,2,4,6},{2,3,4,5})", "{1,6}");

    check("Complement({3, 2, 7, 5, 2, 2, 3, 4, 5, 6, 1}, {2, 3}, {4, 6, 27, 23})", //
        "{1,5,7}");
    check("Complement({1,2,3},{2,3,4})", //
        "{1}");
    check("Complement({2,3,4},{1,2,3})", //
        "{4}");
  }

  @Test
  public void testComplex() {
    // github #267
    check("1/(I*2*Pi*5000*25*10^-6)+10", //
        "10+(-I*4)/Pi");

    check("2.0000000000072014`*^-144+I*2.1 // FullForm", //
        "Complex(2.0000000000072015`*^-144,2.1`)");
    check("Complex(0,1)*2", //
        "I*2");
    // see Outputform conversions
    check("Complex(a, I)", //
        "a+I*I");
    check("a*((- 1/3 )*I)", //
        "-I*1/3*a");
    check("Head(2 + 3*I)", //
        "Complex");
    check("Complex(1, 2/3)", //
        "1+I*2/3");
    check("Abs(Complex(3, 4))", //
        "5");
    check("-2 / 3 - I", //
        "-2/3-I");
    check("Complex(10, 0)", //
        "10");
    check("0. + I", //
        "I*1.0");
    check("1 + 0*I", //
        "1");
    check("Head(1 + 0*I)", //
        "Integer");
    check("Complex(0.0, 0.0)", //
        "0.0");
    check("0.*I", //
        "0.0");
    check("0. + 0.*I", //
        "0.0");
    check("1. + 0.*I", //
        "1.0");
    check("0. + 1.*I", //
        "I*1.0");
    check("Complex(1, Complex(0, 1))", //
        "0");
    check("Complex(1, Complex(1, 0))", //
        "1+I");
    check("Complex(1, Complex(1, 1))", //
        "I");
    check("3/4+6/7", //
        "45/28");
    check("Complex(3/4,-(6/7)*I)", //
        "45/28");
  }

  @Test
  public void testComplexInfinity() {
    check("1 / ComplexInfinity", //
        "0");
    check("ComplexInfinity + ComplexInfinity", //
        "Indeterminate");
    check("ComplexInfinity * Infinity", //
        "ComplexInfinity");
    check("FullForm(ComplexInfinity)", //
        "DirectedInfinity()");
  }

  @Test
  public void testComposeList() {
    check("ComposeList({f,g,h}, x)", //
        "{x,f(x),g(f(x)),h(g(f(x)))}");
    check("ComposeList({1 - # &, 1/# &}[[{2, 2, 1, 2, 2, 1}]], x)", //
        "{x,1/x,x,1-x,1/(1-x),1-x,x}");
    check("ComposeList({f, g}[[{1, 2, 1, 1, 2}]], x)", //
        "{x,f(x),g(f(x)),f(g(f(x))),f(f(g(f(x)))),g(f(f(g(f(x)))))}");
    check("ComposeList({a, b, c, d}, x)", //
        "{x,a(x),b(a(x)),c(b(a(x))),d(c(b(a(x))))}");
  }

  @Test
  public void testComposition() {
    check("Composition(u, v, w)[x, y]", //
        "u(v(w(x,y)))");
    check("Composition(1 + #^# &, a*# &, #/(# + 1) &)[x]", //
        "1+((a*x)/(1+x))^((a*x)/(1+x))");
    check("Composition(f, g, h) @@ {x, y, z}", //
        "f(g(h(x,y,z)))");
  }

  @Test
  public void testCompositeQ() {
    check("CompositeQ({0,-1,-2,-4,-5,-6,-7,-8,-9,-10,-11,-12,-13,-14})", //
        "{False,False,False,True,False,True,False,True,True,True,False,True,False,True}");
    check("Select(Range(100), CompositeQ)", //
        "{4,6,8,9,10,12,14,15,16,18,20,21,22,24,25,26,27,28,30,32,33,34,35,36,38,39,40,42,\n" //
            + "44,45,46,48,49,50,51,52,54,55,56,57,58,60,62,63,64,65,66,68,69,70,72,74,75,76,77,\n" //
            + "78,80,81,82,84,85,86,87,88,90,91,92,93,94,95,96,98,99,100}"); //

    check("CompositeQ(Range(20))", //
        "{False,False,False,True,False,True,False,True,True,True,False,True,False,True,True,True,False,True,False,True}");
    check("CompositeQ(10^300+1)", //
        "True");
  }

  @Test
  public void testCompoundExpression() {
    check("1; 2; 3;", //
        "");
    check("1; 2; 3", //
        "3");
    check("a=100", //
        "100");
    check("a=100;", //
        "");
    check("a", //
        "100");
    check("Catch($a = 2; Throw($a); $a = 5)", //
        "2");
  }

  @Test
  public void testCondition() {
    check("Cases({z(1, 1), z(-1, 1), z(-2, 2)}, z(x_ /; x < 0, y_))", //
        "{z(-1,1),z(-2,2)}");

    check("q(i_,j_):=q(i,j)=q(i-1,j)+q(i,j-1);q(i_,j_)/;i<0||j<0=0;q(0,0)=1", //
        "1");
    check("Definition(q)", //
        "q(0,0)=1\n" //
            + "\n" //
            + "q(i_,j_)/;i<0||j<0=0\n" //
            + "\n" //
            + "q(i_,j_):=q(i,j)=q(-1 + i,j) + q(i,-1 + j)");
    check("q(5,5)", //
        "252");

    check("Condition(Condition(cond(x_),x>1),x<2):=Denominator(x)", //
        "");
    check("cond(3/2)", //
        "2");
    check("cond(0)", //
        "cond(0)");

    check("x /; x > 0", //
        "x/;x>0");
    check("x /; (10==10)", //
        "x/;10==10");

    check("fac(n_ /; n > 0) := n!", //
        "");
    check("fac(3)+fac(-4)", //
        "6+fac(-4)");

    check("Cases({3, -4, 5, -2}, x_ /; x < 0)", //
        "{-4,-2}");
    check("Cases({z(1, 1), z(-1, 1), z(-2, 2)}, z(x_ /; x < 0, y_))", //
        "{z(-1,1),z(-2,2)}");
    check("{1 + a, 2 + a, -3 + a} /. (x_ /; x < 0) + a -> p(x)", //
        "{1+a,2+a,p(-3)}");

    check("{6, -7, 3, 2, -1, -2} /. x_ /; x < 0 -> w", //
        "{6,w,3,2,w,w}");
    check("f(3) /. f(x_) /; x>0 -> t", //
        "t");
    check("f(-3) /. f(x_) /; x>0 -> t", //
        "f(-3)");
    check("f(x_) := p(x) /; x>0", //
        "");
    check("f(3)", //
        "p(3)");
    check("f(-3)", //
        "f(-3)");

    check("Clear(f);f(x_) := Module({u}, u^2 /; ((u = x - 1) > 0))", //
        "");
    check("f(0)", //
        "f(0)");
    check("f(6)", //
        "25");
    check("g(x_) := Module({a}, a = Prime(10^x); (FactorInteger(a + 1)) /; a < 10^6)", //
        "");
    check("g(4)", //
        "{{2,1},{3,1},{5,1},{3491,1}}");
    check("g(5)", //
        "g(5)");

    check("Cases({{a, b}, {1, 2, 3}, {{d, 6}, {d, 10}}}, {x_,y_} /; ! ListQ(x) && ! ListQ(y))", //
        "{{a,b}}");
    check("Cases({{a, b}, {1, 2, 3}, {{d, 6}, {d, 10}}}, {_, _}?VectorQ)", //
        "{{a,b}}");
  }

  @Test
  public void testConditionNested() {
    // nested(x_,y_)/; x>0 /; x<y:= x/y
    // nested(x_,y_)/; x<0 := 0
    // nested(x_,y_):=y/x
    check("nested(x_,y_)/; x>0 /; x<y:= x/y", //
        "");
    check("nested(3,4)", //
        "3/4");
    check("nested(-3,4)", //
        "nested(-3,4)");
    check("nested(x_,y_)/; x<0 := 0", //
        "");
    check("nested(3,4)", //
        "3/4");
    check("nested(-3,4)", //
        "0");
    check("nested(x_,y_) := y/x", //
        "");
    check("nested(3,4)", //
        "3/4");
    check("nested(-3,4)", //
        "0");
    check("nested(4,3)", //
        "3/4");
  }

  @Test
  public void testConditionalExpression() {
    check("Refine(Sin(2*Pi*C(1)),Element(C(1),Integers))", //
        "0");
    check("Refine(Cos(2*Pi*C(1)),Element(C(1),Integers))", //
        "1");
    check(
        "cd=Sin(x) /. {{x -> ConditionalExpression(2*Pi*C(1), Element(C(1), Integers))}, \n"
            + "  {x -> ConditionalExpression(Pi + 2*Pi*C(1), Element(C(1), Integers))}}  ", //
        "{ConditionalExpression(Sin(2*Pi*C(1)),C(1)∈Integers),ConditionalExpression(-Sin(\n" //
            + "2*Pi*C(1)),C(1)∈Integers)}");
    check("ref=Refine(cd)", //
        "{ConditionalExpression(0,C(1)∈Integers),ConditionalExpression(0,C(1)∈Integers)}");
    check("PossibleZeroQ(ref)", //
        "{True,True}");
    check("ConditionalExpression(x, x > 0)^2 == 1 && ConditionalExpression(y, y < 0) > -1", //
        "ConditionalExpression(x^2==1&&y>-1,x>0&&y<0)");
    check("{ConditionalExpression(x^2, x > 0) == 1, ConditionalExpression(x^a, a > 0) < 2}", //
        "{ConditionalExpression(x^2==1,x>0),ConditionalExpression(x^a<2,a>0)}");

    check("Sin(ConditionalExpression(x,x>0))", //
        "ConditionalExpression(Sin(x),x>0)");
    check("Sin(ConditionalExpression(x, x > 0)) +  3*ConditionalExpression(Cos(x), x < 1)^2", //
        "ConditionalExpression(3*Cos(x)^2+Sin(x),x<1&&x>0)");

    check("ConditionalExpression(a,True)", //
        "a");
    check("ConditionalExpression(a,False)", //
        "Undefined");
  }

  @Test
  public void testConjugate() {
    check("Conjugate(Cosh(Im(x)))", //
        "Cosh(Im(x))");

    check("Conjugate(Quantity(2,\"m\"))", //
        "2[m]");
    check("Conjugate(Quantity(a,\"m\"))", //
        "Conjugate(a)[m]");
    check("Conjugate(3*E^(4*I))", //
        "3/E^(I*4)");
    check("Conjugate(Sin(Pi+I))", //
        "I*Sinh(1)");
    check("Conjugate(3 + 4*I)", //
        "3-I*4");
    check("Conjugate(3)", //
        "3");
    check("Conjugate(a + b * I)", //
        "Conjugate(a)-I*Conjugate(b)");
    check("Conjugate(a * b * I)", //
        "-I*Conjugate(a*b)");
    check("Conjugate({{1, 2 + I*4, a + I*b}, {I}})", //
        "{{1,2-I*4,Conjugate(a)-I*Conjugate(b)},{-I}}");
    check("{Conjugate(Pi), Conjugate(E)}", //
        "{Pi,E}");
    check("Conjugate(1.5 + 2.5*I)", //
        "1.5+I*(-2.5)");

    check("Conjugate(1-I)", //
        "1+I");
    check("Conjugate(1+I)", //
        "1-I");
    check("Conjugate(Conjugate(x))", //
        "x");
    check("Conjugate(3*a*z)", //
        "3*Conjugate(a*z)");
    check("Conjugate(E^z)", //
        "E^Conjugate(z)");
    check("Conjugate(Pi)", //
        "Pi");
    check("Conjugate(0)", //
        "0");
    check("Conjugate(I)", //
        "-I");
    check("Conjugate(Indeterminate)", //
        "Indeterminate");
    check("Conjugate(Infinity)", //
        "Infinity");
    check("Conjugate(-Infinity)", //
        "-Infinity");
    check("Conjugate(ComplexInfinity)", //
        "ComplexInfinity");
    check("Conjugate(Transpose({{1,2+I,3},{4,5-I,6},{7,8,9}}))", //
        "{{1,4,7},{2-I,5+I,8},{3,6,9}}");
    check("Conjugate(Zeta(x))", //
        "Zeta(Conjugate(x))");
    check("Conjugate(Zeta(11,7))", //
        "Zeta(11,7)");

    check("Conjugate(Erf(x))", //
        "Erf(Conjugate(x))");
  }

  @Test
  public void testConstant() {
    check("Attributes(E)", //
        "{Constant,Protected}");
    check("Solve(x + E == 0, E) ", //
        "Solve(E+x==0,E)");
  }

  @Test
  public void testConstantArray() {
    // message Maximum AST dimension 2147483647 exceeded
    check("ConstantArray(2,2147483647)", //
        "ConstantArray(2,2147483647)");
    check("ConstantArray({},{})", //
        "{}");
    check("ConstantArray(a, 3)", //
        "{a,a,a}");
    check("ConstantArray(a, {2, 3})", //
        "{{a,a,a},{a,a,a}}");
    check("ConstantArray(c, 10)", //
        "{c,c,c,c,c,c,c,c,c,c}");
    check("ConstantArray(c, {3, 4})", //
        "{{c,c,c,c},{c,c,c,c},{c,c,c,c}}");
  }

  @Test
  public void testContainsAny() {
    check("ContainsAny({b, a, b}, {a, b, c})", //
        "True");
    check("ContainsAny({d,f,e}, {a, b, c})", //
        "False");
    check("ContainsAny({ }, {a, b, c})", //
        "False");

    check("ContainsAny(1, {1,2,3})", //
        "ContainsAny(1,{1,2,3})");
    check("ContainsAny({1,2,3}, 4)", //
        "ContainsAny({1,2,3},4)");

    check("ContainsAny({1.0,2.0}, {1,2,3})", //
        "False");
    check("ContainsAny({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "True");
  }

  @Test
  public void testContainsAll() {
    check("ContainsAll({b,a,b,c}, {a, b})", //
        "True");
    check("ContainsAll({b,a,b,c}, {a, b, d})", //
        "False");
    check("ContainsAll({b, a, d}, {a, b, c})", //
        "False");
    check("ContainsAll({ }, {a, b, c})", //
        "False");
    check("ContainsAll({a, b, c},{ })", //
        "True");

    check("ContainsAll(1, {1,2,3})", //
        "ContainsAll(1,{1,2,3})");
    check("ContainsAll({1,2,3}, 4)", //
        "ContainsAll({1,2,3},4)");

    check("ContainsAll({1.0,2.0}, {1,2,3})", //
        "False");
    check("ContainsAll({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "False");

    check("ContainsAll({1,2,3}, {1.0,2.0})", //
        "False");
    check("ContainsAll({1,2,3}, {1.0,2.0}, SameTest->Equal)", //
        "True");
  }

  @Test
  public void testContainsOnly() {
    check("ContainsOnly(<|a -> x, b -> y|>, {x, y, z})", //
        "True");
    check("ContainsOnly(<|a -> x, b -> y|>, <|1 -> x, 2 -> y, 3 -> z|>)", //
        "True");
    check("ContainsOnly({b, a, a}, {a, b, c})", //
        "True");
    check("ContainsOnly({b, a, d}, {a, b, c})", //
        "False");
    check("ContainsOnly({ }, {a, b, c})", //
        "True");

    check("ContainsOnly(1, {1,2,3})", //
        "ContainsOnly(1,{1,2,3})");
    check("ContainsOnly({1,2,3}, 4)", //
        "ContainsOnly({1,2,3},4)");

    check("ContainsOnly({1.0,2.0}, {1,2,3})", //
        "False");
    check("ContainsOnly({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "True");
  }

  @Test
  public void testContainsExactly() {
    check("ContainsExactly({a, b, c},{ })", //
        "False");

    check("ContainsExactly({b,a,b,c}, {a, b,c})", //
        "True");
    check("ContainsExactly({b,a,d,d}, {a, b,c})", //
        "False");
    check("ContainsExactly({b, a, d}, {a, b, c})", //
        "False");
    check("ContainsExactly({ }, {a, b, c})", //
        "False");
    check("ContainsExactly({a, b, c},{ })", //
        "False");

    check("ContainsExactly(1, {1,2,3})", //
        "ContainsExactly(1,{1,2,3})");
    check("ContainsExactly({1,2,3}, 4)", //
        "ContainsExactly({1,2,3},4)");

    check("ContainsExactly({1.0,2.0}, {1,2,3})", //
        "False");
    check("ContainsExactly({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "False");

    check("ContainsExactly({1,2,3}, {1.0,2.0})", //
        "False");
    check("ContainsExactly({1,2,1,2}, {1.0,2.0}, SameTest->Equal)", //
        "True");
  }

  @Test
  public void testContainsNone() {
    check("ContainsNone({d,f,e}, {a, b, c})", //
        "True");
    check("ContainsNone({b, a, b}, {a, b, c})", //
        "False");
    check("ContainsNone({ }, {a, b, c})", //
        "True");

    check("ContainsNone(1, {1,2,3})", //
        "ContainsNone(1,{1,2,3})");
    check("ContainsNone({1,2,3}, 4)", //
        "ContainsNone({1,2,3},4)");

    check("ContainsNone({1.0,2.0}, {1,2,3})", //
        "True");
    check("ContainsNone({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "False");
  }

  @Test
  public void testContext() {

    check("Context(a)", //
        "Global`");
    check("Context()", //
        "Global`");
    check("Context(Context)", //
        "System`");
    check("{x, Global`x}", //
        "{x,x}");
    check("a`b`x", //
        "a`b`x");
  }

  @Test
  public void test$Context() {
    check("$Context", //
        "Global`");
  }

  @Test
  public void test$ContextPath() {
    check("$ContextPath", //
        "{System`,Global`}");
  }

  @Test
  public void test$MachineEpsilon() {
    // use the non-formatted output engine
    check("$MachineEpsilon", //
        "2.22045*10^-16");
    check("a1=(1.0+$MachineEpsilon)", //
        "1.0");
    check("a2=(1.0+$MachineEpsilon/2)", //
        "1.0");
    check("{a1-1.0, a2-1.0}", //
        "{2.22045*10^-16,0.0}");

    // use the non-formatted engine
    checkNumeric("$MachineEpsilon", //
        "2.220446049250313E-16");
    checkNumeric("a1=(1.0+$MachineEpsilon)", //
        "1.0000000000000002");
    checkNumeric("a2=(1.0+$MachineEpsilon/2)", //
        "1.0");
    checkNumeric("{a1-1.0, a2-1.0}", //
        "{2.220446049250313E-16,0.0}");
  }

  @Test
  public void test$MachinePrecision() {
    check("$MachinePrecision", //
        "16");
    check("N(Sqrt(2), $MachinePrecision)", //
        "1.414213562373095");
    // compare shorter string result to work under unix and windows:
    String evalStr = evalString("N(Sqrt(2), $MachinePrecision+1)");
    String resultStr = "1.414213562373095";
    assertEquals(evalStr.substring(0, evalStr.length()), resultStr);
  }

  @Test
  public void test$MaxMachineNumber() {
    check("$MaxMachineNumber// FullForm", //
        "1.7976931348623157`*^308");
  }

  @Test
  public void test$MinMachineNumber() {
    check("2.2250738585072014`*^-308 // FullForm", //
        "2.2250738585072014`*^-308");
    check("$MinMachineNumber// FullForm", //
        "2.2250738585072014`*^-308");
  }

  @Test
  public void test$Version() {
    check("$Version", //
        Config.getVersion());
  }

  @Test
  public void testContinue() {
    check("For(i=1, i<=8, i=i+1, If(Mod(i,2) == 0, Continue()); Print(i))", //
        "");
  }

  @Test
  public void testContinuedFraction() {
    // ContinuedFraction: Warning: ContinuedFraction terminated before 20 terms.
    check("ContinuedFraction(1/2, 20)", //
        "{0,2}");

    // print message: ContinuedFraction: Positive integer (less than 2147483647) expected at
    // position 2 in
    // ContinuedFraction(Pi,-20).
    check("ContinuedFraction(9/22)", //
        "{0,2,2,4}");
    check("ContinuedFraction(9.0/22)", //
        "{0,2,2,3,1}");
    check("ContinuedFraction(-19/10)", //
        "{-1,-1,-9}");
    check("ContinuedFraction(7283752929681393 / 4096)", //
        "{1778259992597,1,272,15}");
    check("ContinuedFraction(30000000000000/53*Pi,10)", //
        "{1778259992597,1,260,3,1,1,4,1,1,1}");

    check("ContinuedFraction(-7283752929681393 / 4096)", //
        "{-1778259992597,-1,-272,-15}");
    check("ContinuedFraction(-30000000000000/53*Pi,10)", //
        "{-1778259992597,-1,-260,-3,-1,-1,-4,-1,-1,-1}");

    check("ContinuedFraction(2476979795053773 / 4503599627370496)", //
        "{0,1,1,4,2,56294995342130,1,3}");


    check("N(30000000000000/53*Pi)", //
        "1.77826*10^12");

    check("ContinuedFraction((2-2*Sqrt(3))/11,20)", //
        "{0,-7,-1,-1,-18,-1,-1,-9,-76,-9,-1,-1,-18,-1,-1,-9,-76,-9,-1,-1}");
    check("ContinuedFraction((2-2*Sqrt(3))/11 )", //
        "{0,-7,{-1,-1,-18,-1,-1,-9,-76,-9}}");
    check("ContinuedFraction(-5*Sqrt(11))", //
        "{-16,{-1,-1,-2,-1,-1,-32}}");
    check("ContinuedFraction(-Sqrt(70))", // <
        "{-8,{-2,-1,-2,-1,-2,-16}}");
    check("ContinuedFraction(4*Sqrt(48))", //
        "{27,{1,2,2,13,2,2,1,54}}");
    check("ContinuedFraction(Sqrt(70))", //
        "{8,{2,1,2,1,2,16}}");
    check("ContinuedFraction(5*Sqrt(11))", //
        "{16,{1,1,2,1,1,32}}");
    check("ContinuedFraction((3+Sqrt(2))/7)", //
        "{0,1,1,1,{2}}");
    check("ContinuedFraction(Sqrt(13))", //
        "{3,{1,1,1,1,6}}");
    check("ContinuedFraction((1 + 2*Sqrt(3))/5)", //
        "{0,1,{8,3,34,3}}");
    check("ContinuedFraction(Sqrt(70))", //
        "{8,{2,1,2,1,2,16}}");

    // START ContinuedFraction -> FromContinuedFraction
    check("ContinuedFraction((7*Sqrt(2) + 1)/11)", //
        "{0,1,{108,2,4,4,4,2}}");
    check("period=FromContinuedFraction({108,2,4,4,4,2,t})", //
        "108+1/(2+1/(4+1/(4+1/(4+1/(2+1/t)))))");
    check("period // Together", //
        "(17460+39041*t)/(161+360*t)");
    check("y=Solve(period-t==0,t) [[2,1,2]]", //
        "1/720*(38880+27720*Sqrt(2))");
    // TODO
    check("FromContinuedFraction({0,1,x}) /. x->y  // Simplify", //
        "1/11*(1+7*Sqrt(2))");
    // END ContinuedFraction -> FromContinuedFraction

    check("ContinuedFraction(Pi, 10)", //
        "{3,7,15,1,292,1,1,1,2,1}");
    check("ContinuedFraction(-Pi, 10)", //
        "{-3,-7,-15,-1,-292,-1,-1,-1,-2,-1}");

    check("ContinuedFraction(Sqrt(-1))", //
        "ContinuedFraction(I)");
    check("ContinuedFraction(Sqrt(1))", //
        "{1}");
    check("ContinuedFraction(Pi,-20)", //
        "ContinuedFraction(Pi,-20)");
    check("ContinuedFraction(Pi,a)", //
        "ContinuedFraction(Pi,a)");

    // github #127
    check("ContinuedFraction( (10+(2*Sqrt(10)))/(Sqrt(5)+Sqrt(2))+8/(1-Sqrt(5)))", //
        "{-2}");
    check("(10+(2*Sqrt(10)))/(Sqrt(5)+Sqrt(2))+8/(1-Sqrt(5)) // N", //
        "-2.0");

    check("ContinuedFraction(E,100)", //
        "{2,1,2,1,1,4,1,1,6,1,1,8,1,1,10,1,1,12,1,1,14,1,1,16,1,1,18,1,1,20,1,1,22,1,1,24,\n" //
            + "1,1,26,1,1,28,1,1,30,1,1,32,1,1,34,1,1,36,1,1,38,1,1,40,1,1,42,1,1,44,1,1,46,1,1,\n" //
            + "48,1,1,50,1,1,52,1,1,54,1,1,56,1,1,58,1,1,60,1,1,62,1,1,64,1,1,66,1}");


    check("ContinuedFraction(E,101)", //
        "{2,1,2,1,1,4,1,1,6,1,1,8,1,1,10,1,1,12,1,1,14,1,1,16,1,1,18,1,1,20,1,1,22,1,1,24,\n" //
            + "1,1,26,1,1,28,1,1,30,1,1,32,1,1,34,1,1,36,1,1,38,1,1,40,1,1,42,1,1,44,1,1,46,1,1,\n" //
            + "48,1,1,50,1,1,52,1,1,54,1,1,56,1,1,58,1,1,60,1,1,62,1,1,64,1,1,66,1,1}");
    check("ContinuedFraction(Sqrt(0))", //
        "{0}");
    check("ContinuedFraction(Sqrt(2))", //
        "{1,{2}}");
    check("ContinuedFraction(Sqrt(3))", //
        "{1,{1,2}}");
    check("ContinuedFraction(Sqrt(4729494))", //
        "{2174,{1,2,1,5,2,25,3,1,1,1,1,1,1,15,1,2,16,1,2,1,1,8,6,1,21,1,1,3,1,1,1,2,2,6,1,\n" //
            + "1,5,1,17,1,1,47,3,1,1,6,1,1,3,47,1,1,17,1,5,1,1,6,2,2,1,1,1,3,1,1,21,1,6,8,1,1,2,\n" //
            + "1,16,2,1,15,1,1,1,1,1,1,3,25,2,5,1,2,1,4348}}");
    check("ContinuedFraction(Sqrt(919))", //
        "{30,{3,5,1,2,1,2,1,1,1,2,3,1,19,2,3,1,1,4,9,1,7,1,3,6,2,11,1,1,1,29,1,1,1,11,2,6,\n" //
            + "3,1,7,1,9,4,1,1,3,2,19,1,3,2,1,1,1,2,1,2,1,5,3,60}}");
    check("ContinuedFraction(Sqrt(13))", //
        "{3,{1,1,1,1,6}}");

    check("ContinuedFraction(0.753)", //
        "{0,1,3,20,1,1,2,1,1}");
    check("ContinuedFraction(0.55)", //
        "{0,1,1,4,2}");
    check("ContinuedFraction(-0.753)", //
        "{0,-1,-3,-20,-1,-1,-2,-1,-1}");
    check("ContinuedFraction(-0.55)", //
        "{0,-1,-1,-4,-2}");
    check("ContinuedFraction(Pi,30)", //
        "{3,7,15,1,292,1,1,1,2,1,3,1,14,2,1,1,2,2,2,2,1,84,2,1,1,15,3,13,1,4}");
    check("ContinuedFraction(47/17)", //
        "{2,1,3,4}");
    check("ContinuedFraction(Sqrt(13))", //
        "{3,{1,1,1,1,6}}");
    check("ContinuedFraction(Sqrt(13),20)", //
        "{3,1,1,1,1,6,1,1,1,1,6,1,1,1,1,6,1,1,1,1}");
    check("ContinuedFraction(Sqrt(13),1)", //
        "{3}");
    check("ContinuedFraction(Sqrt(13),4)", //
        "{3,1,1,1}");
  }

  @Test
  public void testConvergents() {
    // TODO
    // check(
    // "Convergents({1,Quantity(1.2,\"m\")})", //
    // "");
    // check(
    // "Convergents({1,Quantity(1.2,\"m\"),3,a})", //
    // "");
    check("Convergents({1,2,3,a})", //
        "{1,3/2,10/7,(3+10*a)/(2+7*a)}");
    check("Convergents({{1,0},{0,1},0})", //
        "Convergents({{1,0},{0,1},0})");
    check("Convergents({2,3,4,5})", //
        "{2,7/3,30/13,157/68}");
    check("Convergents({1,1,1,1,1})", //
        "{1,2,3/2,5/3,8/5}");
    check("Convergents({a,b,c,d})", //
        "{a,(1+a*b)/b,(a+c+a*b*c)/(1+b*c),(1+a*b+a*d+c*d+a*b*c*d)/(b+d+b*c*d)}");
  }

  @Test
  public void testConvexHullMesh() {
    check(
        "ConvexHullMesh({{0.0, 0.0, 0.0}, {1.0, 0.5, 0.0}, {2.0, 0.0, 0.0}, "
            + "{0.5, 0.5, 0.5}, {0.0, 0.0, 2.0}, {0.1, 0.2, 0.3}, {0.0, 2.0, 0.0}})", //
        "{{0.0,0.0,0.0},{2.0,0.0,0.0},{0.0,0.0,2.0},{0.0,2.0,0.0}}");

    check("ConvexHullMesh({{0., 0.}, {1., 0.}, {0.5, 0.1}, {4.5,1.1}, {2., 3.}})", //
        "{{0.0,0.0},{1.0,0.0},{4.5,1.1},{2.0,3.0}}");
  }

  @Test
  public void testCoprimeQ() {
    check("CoprimeQ(7,3)", //
        "True");
    check("CoprimeQ(7)", //
        "False");
    check("CoprimeQ( )", //
        "False");
    check("CoprimeQ(8,9,11)", //
        "True");
    check("CoprimeQ({1, 2, 3, 4, 5}, 6)", //
        "{True,False,False,False,True}");
    check("CoprimeQ(2, 3, 5)", //
        "True");
  }

  @Test
  public void testCos() {
    check("Cos(11/8*Pi)", //
        "-Sin(Pi/8)");

    // {-(1/2), (1/4)*(1 - Sqrt(5)), -Sin(Pi/30), Sin(Pi/30), (1/4)*(-1 + Sqrt(5)),
    // 1/2, Sin((7*Pi)/30), (1/4)*(1 + Sqrt(5)), Cos((2*Pi)/15), Cos(Pi/15), 1,
    // Cos(Pi/15), Cos((2*Pi)/15), (1/4)*(1 + Sqrt(5)), Sin((7*Pi)/30), 1/2,
    // (1/4)*(-1 + Sqrt(5)), Sin(Pi/30), -Sin(Pi/30), (1/4)*(1 - Sqrt(5)), -(1/2)}
    check("Table(Cos(i/15*Pi),{i,-10,10})", //
        "{-1/2,1/4*(1-Sqrt(5)),-Cos(7/15*Pi),Cos(7/15*Pi),1/4*(-1+Sqrt(5)),1/2,Cos(4/15*Pi),\n"
            + "1/4*(1+Sqrt(5)),Cos(2/15*Pi),Cos(Pi/15),1,Cos(Pi/15),Cos(2/15*Pi),1/4*(1+Sqrt(5)),Cos(\n"
            + "4/15*Pi),1/2,1/4*(-1+Sqrt(5)),Cos(7/15*Pi),-Cos(7/15*Pi),1/4*(1-Sqrt(5)),-1/2}");

    // {Sin(a - Pi/6), Sin(a - Pi/10), Sin(a - Pi/30), Sin(a + Pi/30),
    // Sin(a + Pi/10), Sin(a + Pi/6), Sin(a + (7*Pi)/30), Cos(a - Pi/5),
    // Cos(a - (2*Pi)/15), Cos(a - Pi/15), Cos(a), Cos(a + Pi/15),
    // Cos(a + (2*Pi)/15), Cos(a + Pi/5), -Sin(a - (7*Pi)/30), -Sin(a - Pi/6),
    // -Sin(a - Pi/10), -Sin(a - Pi/30), -Sin(a + Pi/30), -Sin(a + Pi/10),
    // -Sin(a + Pi/6)}
    check("Table(Cos(a+i/15*Pi),{i,-10,10})", //
        "{-Cos(a+Pi/3),-Cos(a+2/5*Pi),-Cos(a+7/15*Pi),Sin(a+Pi/30),Sin(a+Pi/10),Sin(a+Pi/\n"
            + "6),Sin(a+7/30*Pi),Sin(a+3/10*Pi),Sin(a+11/30*Pi),Sin(a+13/30*Pi),Cos(a),Cos(a+Pi/\n"
            + "15),Cos(a+2/15*Pi),Cos(a+Pi/5),Cos(a+4/15*Pi),Cos(a+Pi/3),Cos(a+2/5*Pi),Cos(a+7/\n"
            + "15*Pi),-Sin(a+Pi/30),-Sin(a+Pi/10),-Sin(a+Pi/6)}");

    check("Cos(1.20000000000000000000000)", //
        "0.362357754476673577638373");

    // print argx message
    check("Cos( )", //
        "Cos()");
    check("Cos(a,b)", //
        "Cos(a,b)");

    check("Cos(I*a+I*b*x)/b", //
        "Cosh(a+b*x)/b");
    // TODO return Cosh((1 + 2*I)*a - 3*I*b*x)
    check("Cos((-2+I)*a+3*b*x)", //
        "Cosh((1+I*2)*a-I*3*b*x)");

    check("Cos(-2*a+3*b*x)", //
        "Cos(2*a-3*b*x)");
    check("Cos(-2*a-3*b*x)", //
        "Cos(2*a+3*b*x)");
    check("Refine(Cos(x+k*Pi), Element(k, Integers))", //
        "(-1)^k*Cos(x)");
    check("Cos(5/8*Pi+2*x)", //
        "-Sin(Pi/8+2*x)");
    check("Cos(3/4*Pi+2*x)", //
        "-Sin(Pi/4+2*x)");
    check("Cos(Pi/4 - r/2 - s + x)", //
        "Sin(Pi/4+r/2+s-x)");
    check("Cos(I*x)-I*Sin(I*x)", //
        "Cosh(x)+Sinh(x)");
    check("Cos(3/4*Pi+x)", //
        "-Sin(Pi/4+x)"); // -Cos(Pi/4-x)
    check("Cos(-3/4*Pi+x)", //
        "-Cos(Pi/4+x)");
    check("Cos(e - Pi/2 + f*x)", //
        "Sin(e+f*x)");
    check("Cos(-1/2*E+z)", //
        "Cos(E/2-z)");
    check("Cos(-Pi/2+z)", //
        "Sin(z)");
    check("Cos(e-Pi/2+f*x)", //
        "Sin(e+f*x)");
    check("Cos(e-3/2*Pi+f*x)", //
        "-Sin(e+f*x)");
    check("Cos(e-1+f*x)", //
        "Cos(1-e-f*x)");
    check("Cos(I*a+I*b*x)/b", //
        "Cosh(a+b*x)/b");
    check("Cos(ArcSin(x))", //
        "Sqrt(1-x^2)");
    check("Cos(ArcCos(x))", //
        "x");
    check("Cos(ArcTan(x))", //
        "1/Sqrt(1+x^2)");
    check("Cos(ArcCot(x))", //
        "1/Sqrt(1+1/x^2)");
    check("Cos(ArcCsc(x))", //
        "Sqrt(1-1/x^2)");
    check("Cos(ArcSec(x))", //
        "1/x");
    check("Cos(0)", //
        "1");
    check("Cos(3*Pi)", //
        "-1");
    check("Cos(1.5*Pi)", //
        "-1.83697*10^-16");

    check("Cos(z+1/2*Pi)", //
        "-Sin(z)");
    check("Cos(Pi)", //
        "-1");
    check("Cos(z+Pi)", //
        "-Cos(z)");
    check("Cos(z+42*Pi)", //
        "Cos(z)");
    check("Cos(x+y+z+43*Pi)", //
        "-Cos(x+y+z)");
    check("Cos(z+42*a*Pi)", //
        "Cos(42*a*Pi+z)");

    // this rule was moved to FunctionExpand
    check("Cos(Sqrt(x^2))", //
        "Cos(Sqrt(x^2))");
  }

  @Test
  public void testCosh() {
    check("Cosh(10*Pi*I)", //
        "1");
    check("Cosh(43*Pi*I)", //
        "-1");
    check("Cosh(17/2*Pi*I)", //
        "0");

    check("Refine(Cosh(x+I*k*Pi), Element(k, Integers))", //
        "(-1)^k*Cosh(x)");
    check("Refine(Cosh(x-I*k*Pi), Element(k, Integers))", //
        "(-1)^k*Cosh(x)");
    check("Refine(Cosh(x+4*I*k*Pi), Element(k, Integers))", //
        "Cosh(x)");
    check("Cosh(Pi*I+x)", //
        "-Cosh(x)");
    check("Cosh(10*Pi*I+x)", //
        "Cosh(x)");
    check("Cosh(43*Pi*I+x)", //
        "-Cosh(x)");
    check("Cosh(0)", //
        "1");
    check("Cosh(1/6*Pi*I)", //
        "Sqrt(3)/2");
    check("Cosh(Infinity)", //
        "Infinity");
    check("Cosh(ComplexInfinity)", //
        "Indeterminate");
  }

  @Test
  public void testCreateDirectory() {
    // Config.FILESYSTEM_ENABLED = true;
    // check("CreateDirectory()", //
    // "C:\\Users\\dev\\AppData\\Local\\Temp\\1539799359607-0");
  }

  @Test
  public void testCanberraDistance() {
    check("CanberraDistance({0,0},{0,0})", //
        "0");
    check("CanberraDistance(SparseArray({11,1,19,2}),SparseArray({5,7,1,23}))", //
        "573/200");
    check("CanberraDistance( {11,1,19,2} , {5,7,1,23} )", //
        "573/200");
    check("CanberraDistance({-1, -1.0}, {1, 1})", //
        "2.0");
    check("CanberraDistance({-1, -1}, {1, 1})", //
        "2");
  }

  @Test
  public void testCorrelation() {
    check("Correlation({a,b},{c,d})", //
        "((a-b)*(Conjugate(c)-Conjugate(d)))/(Sqrt((a-b)*(Conjugate(a)-Conjugate(b)))*Sqrt((c-d)*(Conjugate(c)-Conjugate(d))))");


    check("Correlation({1.5, 3, 5, 10}, {2, 1.25, 15, 8})", //
        "0.475976");
    check("N(Correlation({5.0, 3/4, 1}, {2, 1/2, 1}))", //
        "0.960769");
    check("Correlation({a,b},{c,d})", //
        "((a-b)*(Conjugate(c)-Conjugate(d)))/(Sqrt((a-b)*(Conjugate(a)-Conjugate(b)))*Sqrt((c-d)*(Conjugate(c)-Conjugate(d))))");
    check(
        "Correlation({10, 8, 13, 9, 11, 14, 6, 4, 12, 7, 5}, {8.04, 6.95, 7.58, 8.81, 8.33, 9.96, 7.24, 4.26,10.84, 4.82, 5.68})", //
        "0.816421");

    check("Correlation({5, 3/4, 1}, {2, 1/2, 1})", //
        "2*Sqrt(3/13)");

    check("Correlation({\n" + //
        "{60323,83.0,234289,2356,1590,107608,1947},\n" + //
        "{61122,88.5,259426,2325,1456,108632,1948},\n" + //
        "{60171,88.2,258054,3682,1616,109773,1949},\n" + //
        "{61187,89.5,284599,3351,1650,110929,1950},\n" + //
        "{63221,96.2,328975,2099,3099,112075,1951},\n" + //
        "{63639,98.1,346999,1932,3594,113270,1952},\n" + //
        "{64989,99.0,365385,1870,3547,115094,1953},\n" + //
        "{63761,100.0,363112,3578,3350,116219,1954},\n" + //
        "{66019,101.2,397469,2904,3048,117388,1955},\n" + //
        "{67857,104.6,419180,2822,2857,118734,1956},\n" + //
        "{68169,108.4,442769,2936,2798,120445,1957},\n" + //
        "{66513,110.8,444546,4681,2637,121950,1958},\n" + //
        "{68655,112.6,482704,3813,2552,123366,1959},\n" + //
        "{69564,114.2,502601,3931,2514,125368,1960},\n" + //
        "{69331,115.7,518173,4806,2572,127852,1961},\n" + //
        "{70551,116.9,554894,4007,2827,130081,1962}})", //
        "{{1.0,0.970899,0.983552,0.502498,0.457307,0.960391,0.971329},\n" + //
            " {0.970899,1.0,0.991589,0.620633,0.464744,0.979163,0.991149},\n" + //
            " {0.983552,0.991589,1.0,0.604261,0.446437,0.99109,0.995273},\n" + //
            " {0.502498,0.620633,0.604261,1.0,-0.177421,0.686552,0.668257},\n" + //
            " {0.457307,0.464744,0.446437,-0.177421,1.0,0.364416,0.417245},\n" + //
            " {0.960391,0.979163,0.99109,0.686552,0.364416,1.0,0.993953},\n" + //
            " {0.971329,0.991149,0.995273,0.668257,0.417245,0.993953,1.0}}");
    // check("Correlation(RandomReal(1, 10^4), RandomReal(1, 10^4))", //
    // "0.000430087");
  }

  @Test
  public void testCorrelationDistance() {
    check("CorrelationDistance({2,2},{5,10})", //
        "0");
    check("N(CorrelationDistance({2,2},{5,10}))", //
        "0.0");
    check("N(CorrelationDistance({4,5},{4,5}))", //
        "0.0");
    check("CorrelationDistance({4,5},{4,5})", //
        "0");

    check("CorrelationDistance({1, 2, 3}, {3,5,10})", //
        "1-7/2*1/Sqrt(13)");
    check("N(CorrelationDistance({1, 2, 3}, {3,5,10}))", //
        "0.0292747");

    check("CorrelationDistance({a,b}, {x,y})", //
        "1-((a+1/2*(-a-b))*(Conjugate(x)+1/2*(-Conjugate(x)-Conjugate(y)))+(1/2*(-a-b)+b)*(\n"//
            + "1/2*(-Conjugate(x)-Conjugate(y))+Conjugate(y)))/Sqrt((Abs(a+1/2*(-a-b))^2+Abs(1/\n"//
            + "2*(-a-b)+b)^2)*(Abs(x+1/2*(-x-y))^2+Abs(1/2*(-x-y)+y)^2))");//
    check("CorrelationDistance({a,b,c}, {x,y,z})", //
        "1-((a+1/3*(-a-b-c))*(Conjugate(x)+1/3*(-Conjugate(x)-Conjugate(y)-Conjugate(z)))+(b+\n"//
            + "1/3*(-a-b-c))*(Conjugate(y)+1/3*(-Conjugate(x)-Conjugate(y)-Conjugate(z)))+(1/3*(-a-b-c)+c)*(\n"//
            + "1/3*(-Conjugate(x)-Conjugate(y)-Conjugate(z))+Conjugate(z)))/Sqrt((Abs(a+1/3*(-a-b-c))^\n"//
            + "2+Abs(b+1/3*(-a-b-c))^2+Abs(1/3*(-a-b-c)+c)^2)*(Abs(x+1/3*(-x-y-z))^2+Abs(y+1/3*(-x-y-z))^\n"//
            + "2+Abs(1/3*(-x-y-z)+z)^2))");//
    check("CorrelationDistance(N({1, 5, 2, 3, 10}, 50), N({4, 15, 20, 5, 5}, 50))", //
        "1.2106635188398813533035274347761676650148587810028");
    // check("CorrelationDistance(RandomReal(5, 100), RandomReal(5, 100))", //
    // "1.0697");
  }

  @Test
  public void testCosineDistance() {
    check("CosineDistance(Sin(Pi/111) ,Cos(E))", //
        "2");
    check("CosineDistance(17,47)", //
        "0");
    check("CosineDistance(2/3,-3/7)", //
        "2");
    check("CosineDistance(2/3*I,-3/11)", //
        "1+I");
    check("CosineDistance(-Sin(Pi^2)+I , Cos(E))", //
        "1+(I-Sin(Pi^2))/Sqrt(1+Sin(Pi^2)^2)");


    // CosineDistance: The arguments {a,b} and {x,y,z} do not have compatible dimensions.
    check("CosineDistance({a, b}, {x, y, z})", //
        "CosineDistance({a,b},{x,y,z})");


    check("CosineDistance({1, 0}, {x, y})", //
        "1-Conjugate(x)/Sqrt(Abs(x)^2+Abs(y)^2)");
    check("CosineDistance({a, b, c}, {x, y, z})", //
        "1-(a*Conjugate(x)+b*Conjugate(y)+c*Conjugate(z))/Sqrt((Abs(a)^2+Abs(b)^2+Abs(c)^\n" //
            + "2)*(Abs(x)^2+Abs(y)^2+Abs(z)^2))");

    check("CosineDistance({7.0, 9}, {71, 89})", //
        "0.0000759646");
    check("N(CosineDistance({7, 9}, {71, 89}))", //
        "0.0000759646");
    check("CosineDistance({a, b}, {c, d})", //
        "1-(a*Conjugate(c)+b*Conjugate(d))/Sqrt((Abs(a)^2+Abs(b)^2)*(Abs(c)^2+Abs(d)^2))");//

  }

  @Test
  public void testCot() {
    // check("Cot(4/15*Pi)", //
    // "Tan((7*Pi)/30)");
    check("Cot(0.0)", //
        "ComplexInfinity");
    check("Cot(0)", //
        "ComplexInfinity");
    // check("Cot(z-Pi/3)", //
    // "-Tan(Pi/6+z)");
    check("Cot(e-Pi/2+f*x)", //
        "-Tan(e+f*x)");
    check("Cot(e+Pi/2+f*x)", //
        "-Tan(e+f*x)");
    check("Cot(ComplexInfinity)", //
        "Indeterminate");
    check("Cot(Indeterminate)", //
        "Indeterminate");
    check("Sin(x)*Cot(x)", //
        "Cos(x)");
    // check("Sin(x)^2*Cot(x)^2", "Cos(x)^2");
    // check("Sin(x)^2*Cot(x)^4", "Cos(x)^2*Cot(x)^2");
    // check("Sin(x)^4*Cot(x)^2", "Cos(x)^2*Sin(x)^2");

    check("Cot(ArcSin(x))", //
        "Sqrt(1-x^2)/x");
    check("Cot(ArcCos(x))", //
        "x/Sqrt(1-x^2)");
    check("Cot(ArcTan(x))", //
        "1/x");
    check("Cot(ArcCot(x))", //
        "x");
    check("Cot(ArcCsc(x))", //
        "Sqrt(1-1/x^2)*x");
    check("Cot(ArcSec(x))", //
        "1/(Sqrt(1-1/x^2)*x)");

    check("Cot(Pi/4)", //
        "1");
    check("Cot(0)", //
        "ComplexInfinity");
    check("Cot(1.)", //
        "0.642093");

    check("Cot(z+1/2*Pi)", //
        "-Tan(z)");
    check("Cot(Pi)", //
        "ComplexInfinity");
    check("Cot(z+Pi)", //
        "Cot(z)");
    check("Cot(z+42*Pi)", //
        "Cot(z)");
    check("Cot(x+y+z+43*Pi)", //
        "Cot(x+y+z)");
    check("Cot(z+42*a*Pi)", //
        "Cot(42*a*Pi+z)");
  }

  @Test
  public void testCoth() {
    check("Coth(0.)", //
        "ComplexInfinity");
    check("Coth(0)", //
        "ComplexInfinity");

  }

  @Test
  public void testCount() {
    check("Count(2,2)", //
        "0");
    check("SeedRandom(12345);Count(RandomReal(1, {100}), u_ /; u > 0.5)", //
        "46");
    check("Count(Sin(x) + Cos(x) + Sin(x)^2, Sin, -1 )", //
        "0");
    check("Count(Sin(x) + Cos(x) + Sin(x)^2, Sin, -1, Heads -> True)", //
        "2");
    check("Count(a)[{{a, a}, {a, a, a}, a}]", //
        "1");
    check("Count(a)[{{a, a}, {a, a, a}, a}, {2}]", //
        "Count(a)[{{a,a},{a,a,a},a},{2}]");

    check("Count({1,List},List)", //
        "1");
    check("Count({1,List},Last({1,List}))", //
        "1");

    check("Count(<|a -> 1, b -> 2, c -> 2|>, 2)", //
        "2");
    check("Count({3, 7, 10, 7, 5, 3, 7, 10}, 3)", //
        "2");
    check("Count({{a, a}, {a, a, a}, a}, a, {2})", //
        "5");
    check("count({a, b, a, a, b, c, b}, b)", //
        "3");
    check("count({a, b, f(g(b,a)), a, b, c, b}, b)", //
        "3");
    check("count({a, b, f(g(b,a)), a, b, c, b}, b, infinity)", //
        "4");
    check("count({a, b, f(g(b,a)), a, b, c, b}, b, {1,2})", //
        "3");
    check("count({a, b, f(g(b,a)), a, b, c, b}, b, {1,3})", //
        "4");
    check("count({a, b, f(g(b,a)), a, b, c, b}, b, 3)", //
        "4");
    check("count({a, b, f(g(b,a)), a, b, c, b}, b, {3})", //
        "1");
    check("count({a, b, f(g(b,a)), a, b, c, b}, b, {-1})", //
        "4");
    check("count({3, 4, x, x^2, x^3}, x^_)", //
        "2");
  }

  @Test
  public void testCounts() {
    check("Counts({a, a, b, c, a, a, b, c, c, a, a})", //
        "<|a->6,b->2,c->3|>");
  }

  @Test
  public void testCountDistinct() {
    check("CountDistinct({3, 7, 10, 7, 5, 3, 7, 10})", //
        "4");
    check("CountDistinct({{a, a}, {a, a, a}, a, a})", //
        "3");

    check("CountDistinct({a, b, b, c, a})", //
        "3");
    check("CountDistinct(<|a -> 1, b -> 2, c -> 2|>)", //
        "2");
  }

  @Test
  public void testCovariance() {

    check("Covariance({{0.25,0.33,0.45}})", //
        "Covariance(\n" //
            + "{{0.25,0.33,0.45}})");
    check("Covariance({{0.25,0.13,0.45,0.02},{0.25,0.36,0.45,0.02}})", //
        "{{0.0,0.0,0.0,0.0},\n" //
            + " {0.0,0.02645,0.0,0.0},\n" //
            + " {0.0,0.0,0.0,0.0},\n" //
            + " {0.0,0.0,0.0,0.0}}");
    check("Covariance({{0.25,0.33,0.45},{0.25,0.33,0.45}})", //
        "{{0.0,0.0,0.0},\n" //
            + " {0.0,0.0,0.0},\n" //
            + " {0.0,0.0,0.0}}");
    check("Covariance(I,x^2)", //
        "Covariance(I,x^2)");
    check(
        "Covariance({10, 8, 13, 9, 11, 14, 6, 4, 12, 7, 5}, {8.04, 6.95, 7.58, 8.81, 8.33, 9.96, 7.24, 4.26, 10.84, 4.82, 5.68})", //
        "5.501");
    check("Covariance({0.2, 0.3, 0.1}, {0.3, 0.3, -0.2})", //
        "0.025");
    check("Covariance({a, b, c,d,e}, {x, y, z,v,w})", //
        "1/20*(-(a+b+c-4*d+e)*Conjugate(v)-(a+b+c+d-4*e)*Conjugate(w)-(-4*a+b+c+d+e)*Conjugate(x)-(a-\n"
            + "4*b+c+d+e)*Conjugate(y)-(a+b-4*c+d+e)*Conjugate(z))");
    check("Covariance({a, b, c}, {x, y, z})", //
        "1/6*(-(-2*a+b+c)*Conjugate(x)-(a-2*b+c)*Conjugate(y)-(a+b-2*c)*Conjugate(z))");
    check("Covariance({a, b}, {x, y})", //
        "1/2*(a-b)*(Conjugate(x)-Conjugate(y))");
    checkNumeric("Covariance({1.5, 3, 5, 10}, {2, 1.25, 15, 8})", //
        "11.260416666666666");
  }

  @Test
  public void testCsc() {

    // check("Csc(8/15*Pi)", //
    // "Sec(Pi/30)");
    check("Csc(z+Pi)", //
        "-Csc(z)");

    check("Csc(-3/4*Pi+x)", //
        "-Csc(Pi/4+x)");

    check("Csc(5/7*Pi+x)", //
        "Sec(3/14*Pi+x)");
    check("Csc(3/4*Pi+x)", //
        "Sec(Pi/4+x)");

    check("Csc(e - Pi/2 + f*x)", //
        "-Sec(e+f*x)");
    check("Csc(x)^m*Cot(x)", //
        "Cos(x)*Csc(x)^(1+m)");
    check("Csc(x)^m*Cot(x)^3", //
        "Cos(x)^3*Csc(x)^(3+m)");
    check("Csc(3.5)", //
        "-2.85076");
    check("Csc(1.0+3.5*I)", //
        "0.0508282+I*(-0.0325769)");
    check("Csc(0)", //
        "ComplexInfinity");
    check("Csc(1)", //
        "Csc(1)");
    check("Csc(1.)", //
        "1.1884");
    check("Csc(2/5*Pi)", //
        "Sqrt(2-2/Sqrt(5))");
    check("Csc(23/12*Pi)", //
        "-2*Sqrt(2+Sqrt(3))");
    check("Csc(z+1/2*Pi)", //
        "Sec(z)");
    check("Csc(Pi)", //
        "ComplexInfinity");
    check("Csc(z+Pi)", //
        "-Csc(z)");
    check("Csc(z+42*Pi)", //
        "Csc(z)");
    check("Csc(x+y+z+43*Pi)", //
        "-Csc(x+y+z)");
    check("Csc(z+42*a*Pi)", //
        "Csc(42*a*Pi+z)");

    check("Csc(ArcSin(x))", //
        "1/x");
    check("Csc(ArcCos(x))", //
        "1/Sqrt(1-x^2)");
    check("Csc(ArcTan(x))", //
        "Sqrt(1+x^2)/x");
    check("Csc(ArcCot(x))", //
        "Sqrt(1+x^2)");
    check("Csc(ArcCsc(x))", //
        "x");
    check("Csc(ArcSec(x))", //
        "1/Sqrt(1-1/x^2)");
  }

  @Test
  public void testCsch() {
    // gitbub #173
    check("Csch(Log(5/3))", //
        "15/8");
    check("Refine(Csch(x+I*k*Pi), Element(k, Integers))", //
        "(-1)^k*Csch(x)");
    check("Refine(Csch(x-I*k*Pi), Element(k, Integers))", //
        "(-1)^k*Csch(x)");
    check("Refine(Csch(x-42*I*k*Pi), Element(k, Integers))", //
        "Csch(x)");
    check("Csch(x)^m*Coth(x)^2", //
        "Cosh(x)^2*Csch(x)^(2+m)");
    check("Csch(0)", //
        "ComplexInfinity");
    check("Csch(-x)", //
        "-Csch(x)");
    checkNumeric("Csch(1.8)", //
        "0.3398846914154937");
    check("D(Csch(x),x)", //
        "-Coth(x)*Csch(x)");
  }

  @Test
  public void testCubeRoot() {
    check("N(CubeRoot(18), 100)", //
        "2.620741394208896607141661280441996270239427645723631725102773805728699819196042108828455825989073597");
    check("CubeRoot(-64)", //
        "-4");

    check("(-8)^(-4/3)", //
        "(-1)^(2/3)/16");
    check("CubeRoot(((-8)^(-4)))", //
        "1/16");
    // github #158
    check("(-1)^(1/3)  //N", //
        "0.5+I*0.866025");
    check("CubeRoot(-1)  //N", //
        "-1.0");
    // message - CubeRoot: The parameter 3+I*4 should be real-valued.
    check("CubeRoot(3 + 4*I)", //
        "CubeRoot(3+I*4)");
    check("CubeRoot(16)", //
        "2*2^(1/3)");
    check("CubeRoot(-5)", //
        "-5^(1/3)");
    check("CubeRoot(-510000)", //
        "-10*510^(1/3)");
    check("CubeRoot(-5.1)", //
        "-1.7213");
    check("CubeRoot(b)", //
        "Surd(b,3)");
    check("CubeRoot(-0.5)", //
        "-0.793701");

    check("CubeRoot({-3, -2, -1, 0, 1, 2, 3})", //
        "{-3^(1/3),-2^(1/3),-1,0,1,2^(1/3),3^(1/3)}");
    check("CubeRoot(-2.)", //
        "-1.25992");
  }

  @Test
  public void testCurl() {
    check("Curl({y, -x}, {x, y})", //
        "-2");

    check("Curl({y, -x, 2 z}, {x, y, z})", //
        "{0,0,-2}");
    check("Curl(SparseArray({{1}->y,{2}->-x,{3}->-z},Automatic,0),{x,y,z})", //
        "{0,0,-2}");
    check("Curl({f(x, y, z), g(x, y, z), h(x, y, z)}, {x, y, z})", //
        "{-Derivative(0,0,1)[g][x,y,z]+Derivative(0,1,0)[h][x,y,z],Derivative(0,0,1)[f][x,y,z]-Derivative(\n"
            + "1,0,0)[h][x,y,z],-Derivative(0,1,0)[f][x,y,z]+Derivative(1,0,0)[g][x,y,z]}");
  }

  @Test
  public void testCyclotomic() {
    // check(
    // "Cyclotomic(10009,-9223372036854775808/9223372036854775807)", //
    // "");
    check("Cyclotomic(101,-3/4)", //
        "918250311005358176390006343336822827639729834135611094824501/\n"
            + "1606938044258990275541962092341162602522202993782792835301376");
    check("Cyclotomic(10, z)", //
        "1-z+z^2-z^3+z^4");
    check("Cyclotomic(101,-Infinity)", //
        "Indeterminate");
    // message Cyclotomic: Polynomial degree 2147483647 exceeded
    check("Cyclotomic(2147483647,3/4)", //
        "Cyclotomic(2147483647,3/4)");

    // https://en.wikipedia.org/wiki/Cyclotomic_polynomial
    check("Cyclotomic(0,x)", //
        "1");
    check("Cyclotomic(1,x)", //
        "-1+x");
    check("Cyclotomic(5,x)", //
        "1+x+x^2+x^3+x^4");
    check("Cyclotomic(10,x)", //
        "1-x+x^2-x^3+x^4");
    check("Cyclotomic(25,x)", //
        "1+x^5+x^10+x^15+x^20");
    check("Cyclotomic(32,x)", //
        "1+x^16");
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      check("Cyclotomic(94,x)", //
          "1-x+x^2-x^3+x^4-x^5+x^6-x^7+x^8-x^9+x^10-x^11+x^12-x^13+x^14-x^15+x^16-x^17+x^18-x^\n"
              + "19+x^20-x^21+x^22-x^23+x^24-x^25+x^26-x^27+x^28-x^29+x^30-x^31+x^32-x^33+x^34-x^\n"
              + "35+x^36-x^37+x^38-x^39+x^40-x^41+x^42-x^43+x^44-x^45+x^46");
      // The case of the 105-th cyclotomic polynomial is interesting because 105 is the lowest
      // integer that is the
      // product of three distinct odd prime numbers and this polynomial is the first one that has a
      // coefficient other than 1, 0, or −1:
      check("Cyclotomic(105, x)", //
          "1+x+x^2-x^5-x^6-2*x^7-x^8-x^9+x^12+x^13+x^14+x^15+x^16+x^17-x^20-x^22-x^24-x^26-x^\n"
              + "28+x^31+x^32+x^33+x^34+x^35+x^36-x^39-x^40-2*x^41-x^42-x^43+x^46+x^47+x^48");
    }
  }

  @Test
  public void testD() {
    // check("D(a*3^x,{x,n})", //
    // "a*E^x");
    // TODO
    check("D(a*Exp(x),{x,n})", //
        "a*E^x");
    check("D(a*f(x),{x,10})", //
        "a*Derivative(10)[f][x]");
    check("D(a*Exp(x),{x,3})", //
        "a*E^x");
    check("D(Tan(x),{x,3})", //
        "2*Sec(x)^4+4*Sec(x)^2*Tan(x)^2");
    check("D(Cot(x),{x,5})", //
        "-16*Cot(x)^4*Csc(x)^2-88*Cot(x)^2*Csc(x)^4-16*Csc(x)^6");

    check("D((2*Sqrt(2)*(x^3))/(x^4),x)", //
        "(-2*Sqrt(2))/x^2");
    check("D(z^n*E^(z),{z,n})", //
        "E^z*n!*Hypergeometric1F1(-n,1,-z)");
    check("D(z^n*E^(-z),{z,n})", //
        "(n!*Hypergeometric1F1(-n,1,z))/E^z");

    check("D(Hypergeometric0F1(a,x), x)", //
        "Hypergeometric0F1(1+a,x)/a");
    check("D(Hypergeometric1F1(a,b,x), x)", //
        "(a*Hypergeometric1F1(1+a,1+b,x))/b");
    check("D(Hypergeometric2F1(a,b,c,f(x)), x)", //
        "(a*b*Hypergeometric2F1(1+a,1+b,1+c,f(x))*f'(x))/c");
    check("D(Hypergeometric2F1Regularized(a,b,c,f(x)), x)", //
        "a*b*Hypergeometric2F1Regularized(1+a,1+b,1+c,f(x))*f'(x)");
    check("D(HypergeometricPFQ({}, {}, x), x)", //
        "E^x");
    check("D(HypergeometricPFQ({a}, {}, x), x)", //
        "a/(1-x)^(1+a)");
    check("D(HypergeometricPFQ({a,b}, {u,v,w}, x), x)", //
        "(a*b*HypergeometricPFQ({1+a,1+b},{1+u,1+v,1+w},x))/(u*v*w)");
    check("D(HypergeometricPFQ({a, b,c,d,e,f}, {u,v,w,y,z}, x), x)", //
        "(a*b*c*d*e*f*HypergeometricPFQ({1+a,1+b,1+c,1+d,1+e,1+f},{1+u,1+v,1+w,1+y,1+z},x))/(u*v*w*y*z)");

    check("D(HarmonicNumber(Sin(Cos(x)), y), x)", //
        "y*Cos(Cos(x))*Sin(x)*(HarmonicNumber(Sin(Cos(x)),1+y)-Zeta(1+y))");
    check("D(Sin(f(x)),{x,3})", //
        "-Cos(f(x))*f'(x)^3-3*Sin(f(x))*f'(x)*f''(x)+Cos(f(x))*Derivative(3)[f][x]");
    check("D(Sin(#), {#,-1+n})", //
        "D(Sin(#1),{#1,-1+n})");
    check("D(f(x), {x, n})", //
        "D(f(x),{x,n})");

    check("D(PolyLog(n,z),z)", //
        "PolyLog(-1+n,z)/z");
    check("D(PolyLog(n,p,z),z)", //
        "PolyLog(-1+n,p,z)/z");

    check("D(Polygamma(x),{x,n})", //
        "PolyGamma(n,x)");

    check("D(Sin(x)==x^2==y*x^4,x )", //
        "Cos(x)==2*x==4*x^3*y");

    // message - D: D called with 0 arguments; 1 or more arguments are expected.
    check("D( )", //
        "D()");

    // github #302 - special case - only 1 argument
    check("D(f)", //
        "f");
    // github #302 test case
    check("Integrate(D(x),x)", //
        "x^2/2");
    // github #302 test case
    check("Integrate(D(y)*D(x),x,y)", //
        "1/4*x^2*y^2");
    // github #302 test case
    check("Integrate(D(x),x)*Integrate(D(y),y)", //
        "1/4*x^2*y^2");

    check("D(StruveH(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveH(-1+n,x)-StruveH(1+n,x))");

    check("D(Surd(x,3),x)", //
        "1/(3*Surd(x,3)^2)");
    check("D(Surd(x,-3),x)", //
        "-1/(3*x*Surd(x,3))");
    check("D(Surd(x,4),x)", //
        "1/(4*Surd(x,4)^3)");
    check("D(Surd(x,-4),x)", //
        "-1/(4*Surd(x,4)^5)");
    check("D(Surd(x,5),x)", //
        "1/(5*Surd(x,5)^4)");
    check("D(Surd(x,-5),x)", //
        "-1/(5*x*Surd(x,5))");
    check("D(Sin(#), {#,-1+n})", //
        "D(Sin(#1),{#1,-1+n})");
    check("D(f(x)+ g(x)+h(x), {x, n})", //
        "D(f(x),{x,n})+D(g(x),{x,n})+D(h(x),{x,n})");
    check("D(Sin(x)+ Cos(y), {x, n})", //
        "Piecewise({{Cos(y),n==0}},0)+Sin(1/2*n*Pi+x)");
    check("D(Sin(x),{x,0.5})", //
        "D(Sin(x),{x,0.5})");
    check("D(Sin(x),{x,f(a)})", //
        "D(Sin(x),{x,f(a)})");
    check("D(Sin(x),{x,f(a)+I})", //
        "D(Sin(x),{x,I+f(a)})");
    check("D(x^a, {x,n})", //
        "x^(a-n)*FactorialPower(a,n)");
    check("D(x^a, {x,3})", //
        "((-2+a)*(-1+a)*a)/x^(3-a)");
    check("D(a^x, {x,2})", //
        "a^x*Log(a)^2");
    check("D(Sin(x),{x,n})", //
        "Sin(1/2*n*Pi+x)");
    check("D(InverseFunction(f)[x],x)", //
        "1/f'(InverseFunction(f)[x])");

    // message - D: 2*x is not a valid variable.
    check("D(2*x, 2*x)", //
        "D(2*x,2*x)");

    check("D(E^f(x),x)", //
        "E^f(x)*f'(x)");

    check("D(AiryAi(Sqrt(x)),x)", //
        "AiryAiPrime(Sqrt(x))/(2*Sqrt(x))");
    check("D(AiryAiPrime(Sqrt(x)),x)", //
        "AiryAi(Sqrt(x))/2");

    check("D(Piecewise({{x^2, x < 0}, {x, x > 0}}),x)", //
        "Piecewise({{2*x,x<0},{1,x>0}},0)");
    check("D(Piecewise({{(x^2 - 1)/(x - 1), x != 1}},2),x)", //
        "Piecewise({{(2*x)/(-1+x)+(1-x^2)/(1-x)^2,x!=1}},0)");

    // TODO simplify result to 1
    check("D(Piecewise({{(x^2 - 1)/(x - 1), x < 1 || x > 1}},2),x)", //
        "Piecewise({{(2*x)/(-1+x)+(1-x^2)/(1-x)^2,x<1||x>1}},0)");

    check("D(Factorial(b*x),x)", //
        "b*Gamma(1+b*x)*PolyGamma(0,1+b*x)");
    check("D(E^(E^x + x),x)", //
        "(1+E^x)*E^(E^x+x)");
    check("D((2*x*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^2),x)", //
        "(-4*e*(Sqrt(d)*Sqrt(-e)+e*x)*x^2)/(d+e*x^2)^2+(2*e*x)/(d+e*x^2)+(2*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^\n"
            + "2)");
    check("D(ArcTan(x,y),x)", //
        "-y/(x^2+y^2)");
    check("D(ArcTan(x,y),y)", //
        "x/(x^2+y^2)");
    check("D(ArcTan(x,x),x)", //
        "0");
    check("D(Cosh(b*x),x)", //
        "b*Sinh(b*x)");
    check("D(Sinh(x),x)", //
        "Cosh(x)");
    check("D(Sinc(x),x)", //
        "Cos(x)/x-Sin(x)/x^2");
    check("D(SinIntegral(x),x)", //
        "Sinc(x)");
    check("D(SinhIntegral(x),x)", //
        "Sinh(x)/x");
    check("D(CoshIntegral(x),x)", //
        "Cosh(x)/x");
    check("D(Cosh(b*x)*CoshIntegral(b*x),x)", //
        "Cosh(b*x)^2/x+b*CoshIntegral(b*x)*Sinh(b*x)");

    // gradient
    check("D(f(x, y), {{x, y}})", //
        "{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]}");
    // hessian matrix
    check("D(f(x, y), {{x, y}}, {{x, y}})", //
        "{{Derivative(2,0)[f][x,y],Derivative(1,1)[f][x,y]},{Derivative(1,1)[f][x,y],Derivative(\n"
            + "0,2)[f][x,y]}}");
    // generalization of Hessian matrix for n > 2
    check("D(f(x, y, z), {{x, y, z}, 3})", //
        "{{{Derivative(3,0,0)[f][x,y,z],Derivative(2,1,0)[f][x,y,z],Derivative(2,0,1)[f][x,y,z]},{Derivative(\n"
            + "2,1,0)[f][x,y,z],Derivative(1,2,0)[f][x,y,z],Derivative(1,1,1)[f][x,y,z]},{Derivative(\n"
            + "2,0,1)[f][x,y,z],Derivative(1,1,1)[f][x,y,z],Derivative(1,0,2)[f][x,y,z]}},{{Derivative(\n"
            + "2,1,0)[f][x,y,z],Derivative(1,2,0)[f][x,y,z],Derivative(1,1,1)[f][x,y,z]},{Derivative(\n"
            + "1,2,0)[f][x,y,z],Derivative(0,3,0)[f][x,y,z],Derivative(0,2,1)[f][x,y,z]},{Derivative(\n"
            + "1,1,1)[f][x,y,z],Derivative(0,2,1)[f][x,y,z],Derivative(0,1,2)[f][x,y,z]}},{{Derivative(\n"
            + "2,0,1)[f][x,y,z],Derivative(1,1,1)[f][x,y,z],Derivative(1,0,2)[f][x,y,z]},{Derivative(\n"
            + "1,1,1)[f][x,y,z],Derivative(0,2,1)[f][x,y,z],Derivative(0,1,2)[f][x,y,z]},{Derivative(\n"
            + "1,0,2)[f][x,y,z],Derivative(0,1,2)[f][x,y,z],Derivative(0,0,3)[f][x,y,z]}}}");

    check("Refine(D(Abs(x),x), Element(x, Reals))", //
        "x/Abs(x)");

    check("D(HarmonicNumber(x), x)", //
        "Pi^2/6-HarmonicNumber(x,2)");

    check("D(ArcCsc(x),{x,2})", //
        "(-1+2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2))");
    check("D(ArcSec(x),{x,2})", //
        "(1-2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2))");

    check("D(x*f(x)*f'(x), x)", //
        "f(x)*f'(x)+x*f'(x)^2+x*f(x)*f''(x)");
    check("D(f(x), x)", //
        "f'(x)");
    check("Sin'(2)", //
        "Cos(2)");
    check("D(Sin(x) + Cos(2*x), {x, 2}) /. x -> 0", //
        "-4");

    check("D(Sin(t), {t, 1})", //
        "Cos(t)");
    check("D(Derivative(0,1,0)[f][x,x*y,z+x^2],x)", //
        "2*x*Derivative(0,1,1)[f][x,x*y,x^2+z]+y*Derivative(0,2,0)[f][x,x*y,x^2+z]+Derivative(\n"
            + "1,1,0)[f][x,x*y,x^2+z]");

    check("D(x^3 + x^2, x)", //
        "2*x+3*x^2");
    check("D(x^3 + x^2, {x, 2})", //
        "2+6*x");
    check("D(Sin(Cos(x)), x)", //
        "-Cos(Cos(x))*Sin(x)");
    check("D(Sin(x), {x, 2})", //
        "-Sin(x)");
    check("D(Cos(t), {t, 2})", //
        "-Cos(t)");
    check("D(y, x)", //
        "0");
    check("D(x, x)", //
        "1");
    check("D(f(x), x)", //
        "f'(x)");

    check("D(f(x, x), x)", //
        "Derivative(0,1)[f][x,x]+Derivative(1,0)[f][x,x]");
    // chain rule
    check("D(f(2*x+1, 2*y, x+y), x)", //
        "Derivative(0,0,1)[f][1+2*x,2*y,x+y]+2*Derivative(1,0,0)[f][1+2*x,2*y,x+y]");
    check("D(f(x^2, x, 2*y), {x,2}, y) // Expand", //
        "2*Derivative(0,2,1)[f][x^2,x,2*y]+4*Derivative(1,0,1)[f][x^2,x,2*y]+8*x*Derivative(\n"
            + "1,1,1)[f][x^2,x,2*y]+8*x^2*Derivative(2,0,1)[f][x^2,x,2*y]");

    check("D(x ^ 3 * Cos(y), {{x, y}})", //
        "{3*x^2*Cos(y),-x^3*Sin(y)}");
    check("D(Sin(x) * Cos(y), {{x,y}, 2})", //
        "{{-Cos(y)*Sin(x),-Cos(x)*Sin(y)},{-Cos(x)*Sin(y),-Cos(y)*Sin(x)}}");
    check("D(2/3*Cos(x) - 1/3*x*Cos(x)*Sin(x) ^ 2,x)//Expand  ", //
        "-2/3*Sin(x)-2/3*x*Cos(x)^2*Sin(x)-1/3*Cos(x)*Sin(x)^2+1/3*x*Sin(x)^3");
    check("D(f(#1), {#1,2})", //
        "f''(#1)");
    check("D((#1&)*(t),{t,4})", //
        "0");
    // TODO allow Attributes(f) = {HoldAll}
    check("Attributes(f) = {HoldAll}; Apart(f''(x + x))", //
        "f''(2*x)");
    check("Attributes(f) = {}; Apart(f''(x + x)) ", //
        "f''(2*x)");

    check("D({#^2}, #)", //
        "{2*#1}");

    // Koepf Seite 40-43
    check("D(Sum(k*x^k, {k,0,10}),x)", //
        "1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9");
    check("D((x^2+3)*(3*x+2),x)", //
        "2*x*(2+3*x)+3*(3+x^2)");
    check("D(Sin(x^2),x)", //
        "2*x*Cos(x^2)");
    check("D((1+x^2)^Sin(x),x)", //
        "(1+x^2)^Sin(x)*(Cos(x)*Log(1+x^2)+(2*x*Sin(x))/(1+x^2))");
    check("D(Exp(x),x)", //
        "E^x");
    check("D((x^2+3)/(3*x+2),x)", //
        "(-3*(3+x^2))/(2+3*x)^2+(2*x)/(2+3*x)");

    // others -----
    check("D(InverseErf(x),x)", //
        "1/2*E^InverseErf(x)^2*Sqrt(Pi)");

    check("D(f(Sqrt(x^2 + 1)), {x, 3})", //
        "(3*x^3*f'(Sqrt(1+x^2)))/(1+x^2)^(5/2)+(-3*x*f'(Sqrt(1+x^2)))/(1+x^2)^(3/2)+(-3*x^\n"
            + "3*f''(Sqrt(1+x^2)))/(1+x^2)^2+(3*x*f''(Sqrt(1+x^2)))/(1+x^2)+(x^3*Derivative(3)[f][Sqrt(\n"
            + "1+x^2)])/(1+x^2)^(3/2)");

    check("f(x_) := x^5 + 6*x^3", //
        "");
    check("D(f(x), x)", //
        "18*x^2+5*x^4");
    check("f'(x)", //
        "18*x^2+5*x^4");
    check("D(f(x), x) /. x->5", //
        "3575");
    check("D(f(x), {x, 3}) /. x -> -1", //
        "96");

    check("D(x^2 * E^(5*y), x)", //
        "2*E^(5*y)*x");
    check("D(x^2 * E^(5*y), y)", //
        "5*E^(5*y)*x^2");
    check("D(x^2 * E^(5*y), {x,2}, {y,3})", //
        "250*E^(5*y)");

    check("D(Sin(g(x)) + g''(x), x)", //
        "Cos(g(x))*g'(x)+Derivative(3)[g][x]");

    check("D(Subscript(x, 1)^2 + Sin(Subscript(x, 1)*Subscript(x, 2)), Subscript(x, 1))", //
        "2*Subscript(x,1)+Cos(Subscript(x,1)*Subscript(x,2))*Subscript(x,2)");

    check("D({3*t^2, 4*t, Sin(t)}, t)", //
        "{6*t,4,Cos(t)}");
    check("D({x^n, {Exp(x), Log(x)}, {Sin(x), Cos(x), Tan(x)}}, x)", //
        "{n/x^(1-n),{E^x,1/x},{Cos(x),-Sin(x),Sec(x)^2}}");
    check("D(x^2 + 5*y^3, {{x, y}})", //
        "{2*x,15*y^2}");
    check("D(x^2 + 5*y^3, {{x, y}, 2})", //
        "{{2,0},{0,30*y}}");
    check("D((x^2+5*y^3+z^4)/E^w,{{x,y}})", //
        "{(2*x)/E^w,(15*y^2)/E^w}");
    check("D(E^(-w)*(x^2 + 5*y^3 + z^4), {{{x, y}, {z, w}}})", //
        "{{(2*x)/E^w,(15*y^2)/E^w},{(4*z^3)/E^w,-(x^2+5*y^3+z^4)/E^w}}");
    check("D(ExpIntegralEi(b*x),x)", //
        "E^(b*x)/x");

    check("D(StruveH(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveH(-1+n,x)-StruveH(1+n,x))");
    check("D(StruveH(x,y),x)", //
        "Derivative(1,0)[StruveH][x,y]");
    check("D(StruveL(n,x),x)", //
        "1/2*(x^n/(2^n*Sqrt(Pi)*Gamma(3/2+n))+StruveL(-1+n,x)+StruveL(1+n,x))");
    check("D(StruveL(x,y),x)", //
        "Derivative(1,0)[StruveL][x,y]");

    check("D(JacobiAmplitude(x,y),x)", //
        "JacobiDN(x,y)");
    check("D(JacobiAmplitude(x,y),y)", //
        "((x*(-1+y)+EllipticE(JacobiAmplitude(x,y),y))*JacobiDN(x,y)-y*JacobiCN(x,y)*JacobiSN(x,y))/(\n"
            + //
            "2*(-1+y)*y)");

    check("D(CatalanNumber(x),x)", //
        "CatalanNumber(x)*(Log(4)+PolyGamma(0,1/2+x)-PolyGamma(0,2+x))");
    check("D(Sin(x^5)/x^10,x)", //
        "(5*Cos(x^5))/x^6+(-10*Sin(x^5))/x^11");
    check("D(AppellF1(a,b1,b2,c,f1(x),f2(x)),x)", //
        "(a*b1*AppellF1(1+a,1+b1,b2,1+c,f1(x),f2(x))*f1'(x))/c+(a*b2*AppellF1(1+a,b1,1+b2,\n" //
            + "1+c,f1(x),f2(x))*f2'(x))/c");
  }

  @Test
  public void testDefault() {
    check("Default(Times,2)", //
        "1");
    check("Default(Times)", //
        "1");
    check("Default(test) := 1", //
        "1");
    check("Default(test) = 1", //
        "1");
    check("test(x_., y_.) = {x, y}", //
        "{x,y}");
    check("test(a)", //
        "{a,1}");
    check("test( )", //
        "{1,1}");

    check("Default(Plus)", //
        "0");
    check("Default(Power)", //
        "Default(Power)");
    check("Default(Power, 2)", //
        "1");
  }

  @Test
  public void testDefaultF() {
    check("Default(f)=0", //
        "0");
    check("f(x_., y_.) = {x, y}", //
        "{x,y}");
    check("f(a)", //
        "{a,0}");
    check("f()", //
        "{0,0}");
    check("Default(f, 1) = 1; Default(f, 2) = 2;", //
        "");
    check("Replace(f(), f(x_., y_.) :> {x, y})", //
        "{1,2}");

  }

  @Test
  public void testDefer() {
    // check("Defer(3*2)", "3*2");
    check("Defer(6/8)==6/8", //
        "6/8==3/4");
  }

  @Test
  @Ignore("Definition based on HashMap, which will change it's order if new symbols are defined")
  public void testDefinition() {
    check("Definition(ArcSinh)", //
        "Attributes(ArcSinh)={Listable,NumericFunction,Protected}\n" //
            + "\n" //
            + "ArcSinh(I/Sqrt(2))=I*1/4*Pi\n" //
            + "\n" //
            + "ArcSinh(Undefined)=Undefined\n" //
            + "\n" //
            + "ArcSinh(Infinity)=Infinity\n" //
            + "\n" //
            + "ArcSinh(I*Infinity)=Infinity\n" //
            + "\n" //
            + "ArcSinh(I)=I*1/2*Pi\n" //
            + "\n" //
            + "ArcSinh(0)=0\n" //
            + "\n" //
            + "ArcSinh(I*1/2)=I*1/6*Pi\n" //
            + "\n" //
            + "ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi\n" //
            + "\n" //
            + "ArcSinh(ComplexInfinity)=ComplexInfinity");

    check("a := 42", //
        "");
    check("Definition(a)", //
        "a:=42");
    check("a", //
        "42");
    check("a = 24", //
        "24");
    check("Definition(a)", //
        "a=24");
    check("a", //
        "24");

    check("g(abc_):={abc}", //
        "");
    check("g(abc_):={abc}", //
        "");
    check("Definition(g)", //
        "g(abc_):={abc}");

    check("SetAttributes(f,Listable)", //
        "");
    check("f(x_):={x}", //
        "");
    check("Definition(f)", //
        "Attributes(f)={Listable}\n"//
            + "\n"//
            + "f(x_):={x}");
  }

  @Test
  public void testDegree() {
    check("\\[Pi]", //
        "Pi");
    check("\\[Degree]", //
        "Pi/180");
    check("\u00B0", //
        "Pi/180");
    check("Sin(30*\\[Pi])", //
        "0");
    check("Sin(30*\\[Degree])", //
        "1/2");
    check("Sin(30\\[Degree])", //
        "1/2");
    check("Sin(30*Degree)", //
        "1/2");
    check("Degree == Pi / 180", //
        "True");
    check("Cos(Degree(x))", //
        "Cos(Degree(x))");
    checkNumeric("N(Degree)", //
        "0.017453292519943295");

    check("Round(Pi/Degree^2)", //
        "10313");
    check("Pi/4 < 60*Degree < Pi", //
        "True");
    check("FullSimplify(Pi/Degree)", //
        "180");
  }

  @Test
  public void testDelete() {
    // Delete: Part {3,4} of {1,0} does not exist.
    check("Delete({{1,2},{3,4}})[{1,0}]", //
        "Delete({{1,2},{3,4}})[{1,0}]");
    check("Delete({1,0})[{{1,2},{3,4}}]", //
        "{1,2,{3,4}}");

    check("Delete(<|a -> b, \"a\" -> c|>,0)", //
        "Identity(b,c)");
    check("Delete(<|a -> b, \"a\" -> c|>, \"a\")", //
        "<|a->b|>");

    check("Delete({{1,2},{3,4}},{1,0})", //
        "{1,2,{3,4}}");
    check("Delete({{1,2},{3,4}},{1,2})", //
        "{{1},{3,4}}");
    check("Delete({{a,b},{c,d}},{{1,2},{2,1},{2,1}})", //
        "{{a},{d}}");

    check("var=b", //
        "b");
    check("Delete({a, b, var, d}, -2)", //
        "{a,b,d}");

    check("Delete(a+b+c,0)", //
        "Identity(a,b,c)");
    check("Delete({ }, 0)", //
        "Identity()");
    check("Delete({1, 2}, 0)", //
        "Identity(1,2)");
    // Delete: Part {5} of {a,b,c,d} does not exist.
    check("Delete({a, b, c, d}, 5)", //
        "Delete({a,b,c,d},5)");
    // Delete: Part {1,n} of {a,b,c,d} does not exist.
    check("Delete({a, b, c, d}, {1, n})", //
        "Delete({a,b,c,d},{1,n})");
    check("Delete({a, b, c, d}, 3)", //
        "{a,b,d}");
    check("Delete({a, b, c, d}, -2)", //
        "{a,b,d}");


    check("Delete({{1,2},{3,4}},{{1,2},{2,1}})", //
        "{{1},{4}}");
    check("Delete({{1,2},{3,4}},{{1,2},{2,1},{2,1}})", //
        "{{1},{4}}");

    // test operator form
    check("Delete(pos)", //
        "Delete(pos)");
    check("Delete(pos)[x]", //
        "Delete(pos)[x]");
    check("Delete(2)[{1,2,3,4}]", //
        "{1,3,4}");

    check("Delete({a, {x, y}, b, c}, {2, 0})", //
        "{a,x,y,b,c}");
    check("Delete(f[a, b, u + v, c], {3, 0})", //
        "f(a,b,u,v,c)");
    check("Delete({a, b, c}, 0)", //
        "Identity(a,b,c)");
    check("Delete(h(a, b), {})", //
        "h(a,b)");
    check("Delete(h(a, b), {{}})", //
        "Identity()");

  }

  @Test
  public void testDeleteCases() {
    check("DeleteCases(Sqrt(Range(10)),_Integer,{1},Infinity)", //
        "{Sqrt(2),Sqrt(3),Sqrt(5),Sqrt(6),Sqrt(7),2*Sqrt(2),Sqrt(10)}");
    check("DeleteCases(Sqrt(Range(10)),_Integer,{1},-1)", //
        "DeleteCases(Sqrt(Range(10)),_Integer,{1},-1)");
    check("DeleteCases({a, 1, 2.5, \"string\"}, _Integer|_Real)", //
        "{a,string}");
    check("DeleteCases({a, b, 1, c, 2, 3}, _Symbol)", //
        "{1,2,3}");
    check("Sqrt(Range(10))", //
        "{1,Sqrt(2),Sqrt(3),2,Sqrt(5),Sqrt(6),Sqrt(7),2*Sqrt(2),3,Sqrt(10)}");

    check("DeleteCases(Sqrt(Range(10)), _Integer, {1}, 3)", //
        "{Sqrt(2),Sqrt(3),Sqrt(5),Sqrt(6),Sqrt(7),2*Sqrt(2),Sqrt(10)}");

    check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer)", //
        "{f(a),y,f(8),f(10)}");
    check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -1)", //
        "{f(a),y,f(),f()}");
    check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -2)", //
        "{1,1,f(a),2,3,y,f(8),9,f(10)}");
    check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, {0,4})", //
        "{f(a),y,f(),f()}");

    check("DeleteCases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x)", //
        "{1,1,f(a),2,3,y,g(c,f(8)),9,f(10)}");
    check("DeleteCases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x, -2)", //
        "{1,1,f(a),2,3,y,g(c,f(8)),9,f(10)}");

    check("DeleteCases({1, 1, x, 2, 3, y, 9, y}, _Integer)", //
        "{x,y,y}");

    //
    // check("Cases({3, -4, 5, -2}, x_ /; x < 0)", "{-4,-2}");
    // check("Cases({3, 4, x, x^2, x^3}, x^_)", "{x^2,x^3}");
    // check("Cases({3, 4, x, x^2, x^3}, x^n_ -> n)", "{2,3}");
    // check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {_, _})",
    // "{{1,2},{5,4},{3,3}}");
    // check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {a_, b_} -> a
    // + b)", "{3,9,6}");

  }

  @Test
  public void testDeleteDuplicates() {
    check("DeleteDuplicates({1, 7, 8, 4, 3, 4, 1, 9, 9, 2, 1})", //
        "{1,7,8,4,3,9,2}");
    check("DeleteDuplicates({3,2,1,2,3,4}, Less)", //
        "{3,2,1}");
    check("DeleteDuplicates({3,2,1,2,3,4}, Greater)", //
        "{3,3,4}");
    check("DeleteDuplicates({})", //
        "{}");

    check(
        "l = {{0, 0, 0, 1, 0}, {1, 0, 1, 0, 1}, {1, 1, 1, 0, 0}, {0, 0, 0, 0, 1}, {1, 1, 1, 0, 1}};", //
        "");
    check("DeleteDuplicates(l, Total(#1) == Total(#2) &)", //
        "{{0,0,0,1,0},{1,0,1,0,1},{1,1,1,0,1}}");
  }

  @Test
  public void testDeleteDuplicatesBy() {
    check("DeleteDuplicatesBy(<|a -> 1, b -> -1, c -> 2, d -> -2|>, Abs)", //
        "<|a->1,c->2|>");
    check("DeleteDuplicatesBy({{a, y}, {b, y}, {c, z}, {d, y}}, Last)", //
        "{{a,y},{c,z}}");
    check("DeleteDuplicatesBy(Last) @ {{a, y}, {b, y}, {c, z}, {d, y}}", //
        "{{a,y},{c,z}}");
  }

  @Test
  public void testDeleteMissing() {
    check("DeleteMissing({a, b, Missing(), c, d, Missing()})", //
        "{a,b,c,d}");
    check("DeleteMissing(<| a -> x, b -> y, c -> Missing() |>)", //
        "<|a->x,b->y|>");
  }


  @Test
  public void testDateObject() {
    // Current date
    // check("DateObject()", //
    // "2020-02-01T00:00");

    check("DateObject({2016,8,1})", //
        "2016-08-01T00:00");

    check("d=DateObject({2018,8,8});t=TimeObject({13,15});DateObject(d,t)", //
        "2018-08-08T13:15");
  }

  @Test
  public void testDateString() {
    check("DateString(3155673600)", //
        "Sat 01 Jan 2000 00:00:00");
    // check(
    // "DateString( )", //
    // "Thu 26 Aug 2021 22:15:13");
  }

  @Test
  public void testDateValue() {
    check("DateValue(DateObject({2016,8,1}), {\"Year\",\"Month\",\"Day\"})", //
        "{2016,8,1}");
    check("DateValue(DateObject({2016,8,1}), {\"Year\",\"MonthName\",\"DayName\"})", //
        "{2016,August,Monday}");
    check("DateValue(DateObject({2016,8,1}), {\"Year\",\"MonthNameShort\",\"DayNameShort\"})", //
        "{2016,Aug,Mon}");
    // Current values
    // check("DateValue(\"Second\")", //
    // "20");
    // check("DateValue(\"Minute\")", //
    // "36");
    // check("DateValue(\"Hour\")", //
    // "19");
    // check("DateValue(\"MonthNameInitial\")", //
    // "A");
    // check("DateValue(\"MonthNameShort\")", //
    // "Apr");
    // check("DateValue(\"MonthName\")", //
    // "April");
    // check("DateValue({\"Year\",\"Month\",\"Day\"})", //
    // "{2020,4,18}");
  }

  @Test
  public void testDecrement() {
    check("a = 5", //
        "5");
    check("a--", //
        "5");
    check("a", //
        "4");

    check("index = {1,2,3,4,5,6}", //
        "{1,2,3,4,5,6}");
    check("index[[2]]--", //
        "2");
    check("index", //
        "{1,1,3,4,5,6}");
  }

  @Test
  public void testGoldbachList() {
    check("GoldbachList(64)", //
        "{{3,61},{5,59},{11,53},{17,47},{23,41}}");
    check("GoldbachList(64,2)", //
        "{{3,61},{5,59}}");
    check("GoldbachList(1000000000,1)", //
        "{{71,999999929}}");
    check("GoldbachList(3325581707333960528,1)", //
        "{{9781,3325581707333950747}}");

  }

  @Test
  public void testDedekindNumber() {
    check("Table(DedekindNumber(i), {i,0,11})",
        "{2,3,6,20,168,7581,7828354,2414682040998,56130437228687557907788,\n" //
            + "286386577668298411128469151667598498812366,DedekindNumber(10),DedekindNumber(11)}");
  }

  @Test
  public void testRamseyNumber() {
    // https://en.wikipedia.org/wiki/Ramsey%27s_theorem#Known_values
    check("Table(RamseyNumber(1,j), {j,1,20})", //
        "{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}");
    check("Table(RamseyNumber(2,j), {j,1,20})", //
        "{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}");
    check("Table(RamseyNumber(i,j), {i,1,4},{j,i,10})", //
        "{{1,1,1,1,1,1,1,1,1,1},{2,3,4,5,6,7,8,9,10},{6,9,14,18,23,28,36,RamseyNumber(3,\n" //
            + "10)},{18,25,RamseyNumber(4,6),RamseyNumber(4,7),RamseyNumber(4,8),RamseyNumber(4,\n" //
            + "9),RamseyNumber(4,10)}}");
  }

  @Test
  public void testDenominator() {
    check("Denominator(ConditionalExpression(a/13,Element(C1,Integers)))", //
        "1");

    check("Denominator(-1/p^(1-n))", //
        "p");
    check("Numerator(-1/p^(1-n))", //
        "-p^n");
    check("Denominator( a*x^n*y^- m*Exp(a - b - 2 c + 3 d) )", //
        "E^(b+2*c)*y^m");
    check("Denominator(a^-b/x)", //
        "a^b*x");

    // github #151
    check("N(Denominator(Pi/E))", //
        "2.71828");

    // test undefined option message in stderr:
    check("Denominator(x, y)", //
        "Denominator(x,y)");

    check("Denominator(a/(b*c))", //
        "b*c");
    check("Denominator(a^2*b)", //
        "1");
    check("Denominator(a^2*b^-2*c^-3)", //
        "b^2*c^3");
    check("Denominator(a^2*b^-a*c^-d)", //
        "b^a*c^d");

    check("Denominator(Csc(x))", //
        "1");
    check("Denominator(Csc(x), Trig->True)", //
        "Sin(x)");
    check("Denominator(Csc(x)^4)", //
        "1");
    check("Denominator(Csc(x)^4, Trig->True)", //
        "Sin(x)^4");
    check("Denominator(42*Csc(x))", //
        "1");
    check("Denominator(42*Csc(x), Trig->True)", //
        "Sin(x)");
    check("Denominator(42*Csc(x)^3)", //
        "1");
    check("Denominator(42*Csc(x)^3, Trig->True)", //
        "Sin(x)^3");
    check("Denominator(E^(-x)*x^(1/2))", //
        "E^x");

    check("Denominator(Sec(x))", //
        "1");
    check("Denominator(Tan(x))", //
        "1");
    check("Denominator(Tan(x), Trig->True)", //
        "Cos(x)");

    check("Denominator(a / b)", //
        "b");
    check("Denominator(2 / 3)", //
        "3");
    check("Denominator(a + b)", //
        "1");
  }

  @Test
  public void testDepth() {
    check("Depth(f(a, b)[c])", //
        "2");
    check("Depth(f(a, b)[c], Heads->True)", //
        "3");

    check("Depth(x)", //
        "1");
    check("Depth(g(a))", //
        "2");
    check("Depth({{{a}, b}})", //
        "4");
    check("Depth(x + y)", //
        "2");

    check("Depth({{{{x}}}})", //
        "5");
    check("Depth(1 + 2*I)", //
        "1");
    check("Depth(f(a, b)[c])", //
        "2");
  }

  @Test
  public void testCapitalDifferentialD() {
    check("\"start \\[CapitalDifferentialD] 123 end\"", //
        "startⅅ 123 end");
  }

  @Test
  public void testDerivative() {
    check("Derivative(0,1,0,0)[Multinomial]", //
        "(-HarmonicNumber(#2)+HarmonicNumber(#1+#2+#3+#4))*Multinomial(#1,#2,#3,#4)&");
    check("Derivative(1,1,0,0)[Multinomial]", //
        "Derivative(1,1,0,0)[Multinomial]");


    check("Derivative(1)[I+2]", //
        "0&");
    check("Derivative(1)[Log10]", //
        "1/(Log(10)*#1)&");
    check("Derivative(1)[Log2]", //
        "1/(Log(2)*#1)&");
    check("Derivative(1)[Exp]", //
        "E^#1&");
    check("Derivative(1)[Abs]", //
        "Derivative(1)[Abs]");
    check("Derivative(1)[RealAbs]", //
        "#1/RealAbs(#1)&");
    check("Derivative(3)[ArcCos]", //
        "-(1+2*#1^2)/(1-#1^2)^(5/2)&");
    check("Derivative(0,0,0,n)[Hypergeometric2F1Regularized]", //
        "Hypergeometric2F1Regularized(n+#1,n+#2,n+#3,#4)*Pochhammer(#1,n)*Pochhammer(#2,n)&");
    check("Derivative(0,0,0,Sin(7))[Hypergeometric2F1Regularized]", //
        "Derivative(0,0,0,Sin(7))[Hypergeometric2F1Regularized]");
    check("Derivative(0,0,0,3)[Hypergeometric2F1Regularized]", //
        "Hypergeometric2F1Regularized(3+#1,3+#2,3+#3,#4)*Pochhammer(#1,3)*Pochhammer(#2,3)&");
    check("Derivative(0,0,0)[Sequence()]", //
        "Derivative(0,0,0)[]");

    check("Derivative(1,0)[StruveH][#1,#2]", //
        "Derivative(1,0)[StruveH][#1,#2]");

    check("f1(x_) := Sin(x)", //
        "");

    check("Derivative(n)[f1][x]", //
        "Sin(1/2*n*Pi+x)");
    check("f2(x_, y_) := Cos(x)*Sin(y)", //
        "");
    check("Derivative(1,0)[f2][x, y]", //
        "-Sin(x)*Sin(y)");

    check("D(f(x), x)", //
        "f'(x)");
    check("D(f(x, x), x)", //
        "Derivative(0,1)[f][x,x]+Derivative(1,0)[f][x,x]");
    check("Derivative(2147483647)[x[[-1,1,1]]]", //
        "Derivative(2147483647)[x[[-1,1,1]]]");
    check("Derivative(10)[Sin]", //
        "-Sin(#1)&");
    check("Derivative(10)[Sin][x]", //
        "-Sin(x)");
    check("Derivative(n)[Sin]", //
        "Sin(1/2*n*Pi+#1)&");

    check("Derivative(I+a)[Sin]", //
        "Derivative(I+a)[Sin]");
    check("f''(k^(1/3) x) - f(k^(1/3) x) == 0 /. x -> (x k^(-1/3))", //
        "-f(x)+f''(x)==0");
    check("h(x_):= 4 x / (x ^ 2 + 3*x + 5)", //
        "");
    check("extremes=Solve(h'(x)==0,x)", //
        "{{x->-Sqrt(5)},{x->Sqrt(5)}}");
    check("h''(x) /.extremes // N", //
        "{1.65086,-0.064079}");

    check("h(x_):=Sin(x)+x^2", //
        "");
    check("h'(x)", //
        "2*x+Cos(x)");
    check("h'(0.5)", //
        "1.87758");
    check("h''(x)", //
        "2-Sin(x)");

    check("h(x_):=x*Cos(x)", //
        "");
    check("h'", //
        "Cos(#1)-Sin(#1)*#1&");
    check("h''", //
        "-2*Sin(#1)-Cos(#1)*#1&");

    check("y''", //
        "Derivative(2)[y]");

    check("Derivative(1)[Sin]", //
        "Cos(#1)&");

    check("Derivative(1)[Haversine]", //
        "Sin(#1)/2&");
    check("Derivative(1)[InverseHaversine]", //
        "1/Sqrt((1-#1)*#1)&");
    check("Derivative(0)[#1^2&]", //
        "#1^2&");
    check("Derivative(1)[#1^2&]", //
        "2*#1&");
    check("Derivative(1)[3*# ^ 2+5*# ^ 3&] ", //
        "6*#1+15*#1^2&");
    check("Derivative(1)[# ^ 3&] ", //
        "3*#1^2&");
    check("Derivative(2)[# ^ 3&] ", //
        "6*#1&");
    check("Derivative(1)[E ^ #&] ", //
        "E^#1&");
    check("Derivative(1)[Sin]", //
        "Cos(#1)&");
    check("Derivative(3)[Sin]", //
        "-Cos(#1)&");
    check("Sin'(x)", //
        "Cos(x)");
    check("(# ^ 4&)''", //
        "12*#1^2&");
    // check("f'(x) // FullForm", "Derivative(1)[f][x]");
    // TODO
    // check("Derivative(1)[#2 Sin(#1)+Cos(#2)&]", "Cos(#1)*#2&");
    // check("Derivative(1,2)[#2^3 Sin(#1)+Cos(#2)&]", "6*Cos(#1)*#2&");
    // TODO Deriving with respect to an unknown parameter yields 0
    // check("Derivative(1,2,1)[#2^3*Sin(#1)+Cos(#2)&]", "");
    check("Derivative(0,0,0)[a+b+c]", //
        "a+b+c");
    // TODO You can calculate the derivative of custom functions
    // check("f(x_) := x ^ 2", "");
    // check("f'(x)", "");
    check("Derivative(2, 1)[h]", //
        "Derivative(2,1)[h]");
    check("Derivative(2, 0, 1, 0)[k(g)]", //
        "Derivative(2,0,1,0)[k(g)]");

    // parser tests
    check("Hold(f'') // FullForm ", //
        "Hold(Derivative(2)[f])");
    check("Hold(f ' ') // FullForm ", //
        "Hold(Derivative(2)[f])");
    check("Hold(f '' '') // FullForm ", //
        "Hold(Derivative(4)[f])");
    check("Hold(Derivative(x)[4] ') // FullForm ", //
        "Hold(Derivative(1)[Derivative(x)[4]])");

    check("D(f(a,b),b)", //
        "Derivative(0,1)[f][a,b]");
    check("D(f(a,b),x)", //
        "0");
    check("g(u0_,u1_):=D(f(u0,u1),u1);g(a,b)", //
        "Derivative(0,1)[f][a,b]");
    check("Derivative(1)[ArcCoth]", //
        "1/(1-#1^2)&");
    check("y''", //
        "Derivative(2)[y]");
    check("y''(x)", //
        "y''(x)");
    check("y''''(x)", //
        "Derivative(4)[y][x]");

    check("x*x^a", //
        "x^(1+a)");
    check("x/x^(1-x)", //
        "x^x");
    check("Derivative(0,1)[BesselJ][a, x]", //
        "1/2*(BesselJ(-1+a,x)-BesselJ(1+a,x))");
    check("Derivative(1,0)[Power][x, 4]", //
        "4*x^3");
    check("Derivative(1,0)[Power][x, y]", //
        "y/x^(1-y)");
    check("Derivative(1,1)[Power][x, 4]", //
        "x^3+4*x^3*Log(x)");
    check("Derivative(1,1)[Power][x, y]", //
        "x^(-1+y)+(y*Log(x))/x^(1-y)");
    check("Derivative(0,1)[Power][a, x]", //
        "a^x*Log(a)");
    check("Derivative(1,1)[Power][a, x]", //
        "a^(-1+x)+(x*Log(a))/a^(1-x)");
    check("Derivative(1,1)[Power][x, x]", //
        "x^(-1+x)+x^x*Log(x)");

    check("Hold((-1)*Sin(#)&[x])", //
        "Hold(-Sin(#1)&[x])");
    check("Hold(Derivative(1)[Cos][x])", //
        "Hold(Cos'(x))");
    check("Derivative(1)[Cos][x]", //
        "-Sin(x)");
    check("Derivative(1)[Sin][x]", //
        "Cos(x)");
    check("Derivative(4)[Cos][x]", //
        "Cos(x)");
    check("Derivative(1)[Tan]", //
        "Sec(#1)^2&");
    check("Derivative(2)[Tan]", //
        "2*Sec(#1)^2*Tan(#1)&");
    check("Derivative(4)[Log][x]", //
        "-6/x^4");
    check("Derivative(2)[ArcSin][x]", //
        "x/(1-x^2)^(3/2)");

    check("Derivative(1)[2]", //
        "0&");
    check("Derivative(1)[2][x,y,z]", //
        "0");
    check("Derivative(10)[2][x,y,z]", //
        "0");
    check("Derivative(10,9,8)[2][a,b,c]", //
        "0");
    check("Derivative(1)[Cos[3]][z]", //
        "Cos(3)'[z]");
    check("y = x^2 + 1; x = 1", //
        "1");
    check("y'", //
        "0&");

    check("Derivative(1)[CatalanNumber]", //
        "CatalanNumber(#1)*(Log(4)+PolyGamma(1/2+#1)-PolyGamma(2+#1))&");
  }


  @Test
  public void testDiceDissimilarity() {
    check("DiceDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
        "3/7");
    check("DiceDissimilarity({True, False, True}, {True, True, False})", //
        "1/2");
    check("DiceDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
        "0");
    check("DiceDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
        "1");
  }

  @Test
  public void testDifferenceDelta() {
    check("DifferenceDelta(Cosh(a*i+b),{i,2,h})", //
        "Cosh(b+a*i)-2*Cosh(b+a*(h+i))+Cosh(b+a*(2*h+i))");
    check("DifferenceDelta(Sin(a*i+b),{i,5})", //
        "-Sin(b+a*i)+5*Sin(b+a*(1+i))-10*Sin(b+a*(2+i))+10*Sin(b+a*(3+i))-5*Sin(b+a*(4+i))+Sin(b+a*(\n"
            + "5+i))");
    check("DifferenceDelta(b(a),{a,2,c})", //
        "b(a)-2*b(a+c)+b(a+2*c)");
    check("DifferenceDelta(b(a),{a,3,c})", //
        "-b(a)+3*b(a+c)-3*b(a+2*c)+b(a+3*c)");
    check("DifferenceDelta(b(a),{a,5,c})", //
        "-b(a)+5*b(a+c)-10*b(a+2*c)+10*b(a+3*c)-5*b(a+4*c)+b(a+5*c)");
    check("DifferenceDelta(b(a),{a,1,c})", //
        "-b(a)+b(a+c)");
    check("DifferenceDelta(b(a),a)", //
        "-b(a)+b(1+a)");
  }

  @Test
  public void testDifferences() {
    // TODO: two args function
    // check(
    // "Differences({a, b, c, d},3)", //
    // "{-a+3*b-3*c+d}");

    check("Differences({ })", //
        "{}");
    check("Differences({a})", //
        "{}");
    check("Differences({a,b})", //
        "{-a+b}");
    check("Differences({a, b, c, d} )", //
        "{-a+b,-b+c,-c+d}");

    check("Differences({a,b,c})", //
        "{-a+b,-b+c}");
    check("Differences(SparseArray(10 -> 1, 21))", //
        "{0,0,0,0,0,0,0,0,1,-1,0,0,0,0,0,0,0,0,0,0}");
    check("t=Table(n^5 + 2*n - 1, {n, 8})", //
        "{2,35,248,1031,3134,7787,16820,32783}");
    check("FixedPointList(Differences, %)", //
        "{{2,35,248,1031,3134,7787,16820,32783},{33,213,783,2103,4653,9033,15963},{180,\n"
            + "570,1320,2550,4380,6930},{390,750,1230,1830,2550},{360,480,600,720},{120,120,120},{\n"
            + "0,0},{0},{},{}}");
    check("NestList(Differences, Normal(SparseArray(10 -> 1, 21)), 11)", //
        "{{0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,1,-1,0,0,0,0,0,0,0,\n"
            + "0,0,0},{0,0,0,0,0,0,0,1,-2,1,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,1,-3,3,-1,0,0,0,0,0,\n"
            + "0,0,0},{0,0,0,0,0,1,-4,6,-4,1,0,0,0,0,0,0,0},{0,0,0,0,1,-5,10,-10,5,-1,0,0,0,0,0,\n"
            + "0},{0,0,0,1,-6,15,-20,15,-6,1,0,0,0,0,0},{0,0,1,-7,21,-35,35,-21,7,-1,0,0,0,0},{\n"
            + "0,1,-8,28,-56,70,-56,28,-8,1,0,0,0},{1,-9,36,-84,126,-126,84,-36,9,-1,0,0},{-10,\n"
            + "45,-120,210,-252,210,-120,45,-10,1,0},{55,-165,330,-462,462,-330,165,-55,11,-1}}");
  }

  @Test
  public void testDigitCount() {

    // message Maximum AST dimension 2147483647 exceeded
    check("DigitCount({2,5,3},2147483647)", //
        "DigitCount({2,5,3},2147483647)");
    check("DigitCount(1, 17)", //
        "{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}");

    check("DigitCount(4, 3)", //
        "{2,0,0}");
    check("DigitCount(6, 5)", //
        "{2,0,0,0,0}");
    check("DigitCount(7, 5)", //
        "{1,1,0,0,0}");
    check("DigitCount(10, 5)", //
        "{0,1,0,0,1}");
    check("DigitCount(-4, 3)", //
        "{2,0,0}");
    check("DigitCount(-6, 5)", //
        "{2,0,0,0,0}");
    check("DigitCount(-7, 5)", //
        "{1,1,0,0,0}");
    check("DigitCount(-10, 5)", //
        "{0,1,0,0,1}");

    check("DigitCount(0, 2)", //
        "{0,1}");
    check("DigitCount(0)", //
        "{0,0,0,0,0,0,0,0,0,1}");
    check("DigitCount(0, 12)", //
        "{0,0,0,0,0,0,0,0,0,0,0,1}");
    check("DigitCount(145, 12)", //
        "{2,0,0,0,0,0,0,0,0,0,0,1}");
    // https://oeis.org/A098949
    check("Select(Range(1000), " //
        + "DigitCount( # )[[1]] == 0 && DigitCount( # )[[3]] == 0 && DigitCount( # )[[5]] == 0 && DigitCount( # )[[7]] == 0 && DigitCount( # )[[9]] >0 &)", //
        "{9,29,49,69,89,90,92,94,96,98,99,209,229,249,269,289,290,292,294,296,298,299,409,\n" + //
            "429,449,469,489,490,492,494,496,498,499,609,629,649,669,689,690,692,694,696,698,\n" + //
            "699,809,829,849,869,889,890,892,894,896,898,899,900,902,904,906,908,909,920,922,\n" + //
            "924,926,928,929,940,942,944,946,948,949,960,962,964,966,968,969,980,982,984,986,\n" + //
            "988,989,990,992,994,996,998,999}");
    check("DigitCount(2147)", //
        "{1,1,0,1,0,0,1,0,0,0}");
    check("DigitCount(2147, 2, 1)", //
        "5");
    check("DigitCount(2147, 16)", //
        "{0,0,1,0,0,1,0,1,0,0,0,0,0,0,0,0}");
    check("DigitCount(100!)", //
        "{15,19,10,10,14,19,7,14,20,30}");
    check("DigitCount(-100!)", //
        "{15,19,10,10,14,19,7,14,20,30}");
    check("DigitCount(242442422, 3, {1, 2})", //
        "{6,7}");
    check("DigitCount(-242442422, 3, {1, 2})", //
        "{6,7}");
    check("Table(1 - Mod(DigitCount(n - 1, 2, 1), 2), {n, 25})", //
        "{1,0,0,1,0,1,1,0,0,1,1,0,1,0,0,1,0,1,1,0,1,0,0,1,1}");
    check("Table(2^DigitCount(t, 2, 1), {t, 0, 10})", //
        "{1,2,2,4,2,4,4,8,2,4,4}");
  }

  @Test
  public void testDigitQ() {
    check("DigitQ(\"1234\")", //
        "True");
    check("DigitQ(\".\")", //
        "False");
  }


  @Test
  public void testDiscreteDelta() {
    check("DiscreteDelta(0, 0, 0.0)", //
        "1");
    check("DiscreteDelta(0)", //
        "1");
    check("DiscreteDelta(42)", //
        "0");
    check("DiscreteDelta(-1)", //
        "0");
    check("DiscreteDelta(-42)", //
        "0");
    check("DiscreteDelta({1.6, 1.6000000000000000000000000})", //
        "DiscreteDelta({1.6,1.6})");
    check("DiscreteDelta(1.6, 1.6000000000000000000000000)", //
        "0");
    check("DiscreteDelta(1, 2, 3)", //
        "0");
  }

  @Test
  public void testDiracDelta() {
    check("DiracDelta(1+I)", //
        "DiracDelta(1+I)");
    check("DiracDelta(x)", //
        "DiracDelta(x)");
    check("DiracDelta(-x)", //
        "DiracDelta(x)");
    check("DiracDelta(-x, -y)", //
        "DiracDelta(x,y)");
    check("DiracDelta(x, y, z)", //
        "DiracDelta(x,y,z)");
    check("DiracDelta(-x, 0.5, z)", //
        "0");
    check("DiracDelta(0)", //
        "DiracDelta(0)");
    check("DiracDelta(-1)", //
        "0");
    check("DiracDelta(-42)", //
        "0");
    check("DiracDelta({1.6, 1.6000000000000000000000000})", //
        "{0,0}");
    check("DiracDelta({-1, 0, 1})", //
        "{0,DiracDelta(0),0}");
    check("DiracDelta(1, 2, 3)", //
        "0");
  }

  @Test
  public void testDirectedInfinity() {
    check("DirectedInfinity({0,0,0})", //
        "{ComplexInfinity,ComplexInfinity,ComplexInfinity}");

    check("DirectedInfinity(a*b^(-3)*c^2)", //
        "DirectedInfinity((Sign(a)*Sign(c)^2)/Sign(b)^3)");
    check("DirectedInfinity(a*b*c^(-1)*z ^(-2))", //
        "DirectedInfinity(Sign(a*b)/(Sign(c)*Sign(z)^2))");

    check("DirectedInfinity(a/b)", //
        "DirectedInfinity(Sign(a)/Sign(b))");
    check("DirectedInfinity(a)", //
        "DirectedInfinity(a)");


    // message: Infinity: Indeterminate expression 0*DirectedInfinity(a) encountered.
    check("DirectedInfinity(a)*0", //
        "Indeterminate");

    check("DirectedInfinity(1) + DirectedInfinity(-1)", //
        "Indeterminate");
    check("DirectedInfinity(-2000)", //
        "-Infinity");
    check("DirectedInfinity(2001)", //
        "Infinity");
    check("Table(DirectedInfinity(i), {i, {1, -1, I, -I}})", //
        "{Infinity,-Infinity,I*Infinity,-I*Infinity}");

    check("DirectedInfinity(1 + I)^ -1", //
        "0");
    check("1/DirectedInfinity(1 + I)", //
        "0");
    check("DirectedInfinity(1 + I)", //
        "DirectedInfinity((1+I)/Sqrt(2))");
    check("DirectedInfinity(1+I)+DirectedInfinity(2+I)", //
        "DirectedInfinity((1+I)/Sqrt(2))+DirectedInfinity((2+I)/Sqrt(5))");

    check("DirectedInfinity(Sqrt(3))", //
        "Infinity");

    check("DirectedInfinity(1)", //
        "Infinity");
    check("DirectedInfinity()", //
        "ComplexInfinity");

    check("DirectedInfinity(Indeterminate)", //
        "ComplexInfinity");
    check("ComplexInfinity+b", //
        "ComplexInfinity");
    // Power()
    check("0^(-1)", //
        "ComplexInfinity");
    check("{Exp(Infinity), Exp(-Infinity)}", //
        "{Infinity,0}");
    check("1^Infinity", //
        "Indeterminate");
    check("1^(-Infinity)", //
        "Indeterminate");
    check("1^ComplexInfinity", //
        "Indeterminate");

    // Times()
    check("DirectedInfinity(x)*DirectedInfinity(y)", //
        "DirectedInfinity(x*y)");
    check("Table(DirectedInfinity(i), {i, {1, -1, I, -I}})", //
        "{Infinity,-Infinity,I*Infinity,-I*Infinity}");
    check("(1 + I)*Infinity", "DirectedInfinity((1+I)/Sqrt(2))");
    check("{DirectedInfinity(), DirectedInfinity(Indeterminate)}", //
        "{ComplexInfinity,ComplexInfinity}");
    check("Infinity/Infinity", //
        "Indeterminate");
    check("3*DirectedInfinity(z)", //
        "DirectedInfinity(z)");
    check("I*DirectedInfinity(z)", //
        "DirectedInfinity(I*z)");

    // Plus()

    check("1+1", //
        "2");
    check("1+Infinity", //
        "Infinity");
    check("1-Infinity", //
        "-Infinity");
    check("Infinity+Infinity", //
        "Infinity");
    check("-Infinity-Infinity", //
        "-Infinity");
    check("Infinity-Infinity", //
        "Indeterminate");
    check("1+Indeterminate", //
        "Indeterminate");
    check("0+ComplexInfinity", //
        "ComplexInfinity");
    check("ComplexInfinity+ComplexInfinity", //
        "Indeterminate");
    check("ComplexInfinity+Indeterminate", //
        "Indeterminate");
    check("1+ComplexInfinity", //
        "ComplexInfinity");
    check("DirectedInfinity(x) + DirectedInfinity(y)", //
        "DirectedInfinity(x)+DirectedInfinity(y)");
    check("DirectedInfinity(x) + DirectedInfinity(y) /. {x -> 1, y -> -1}", //
        "Indeterminate");
  }

  @Test
  public void testDirichletBeta() {
    check("DirichletBeta(-1)", //
        "0");
    check("DirichletBeta(0)", //
        "1/2");
    check("DirichletBeta(1)", //
        "Pi/4");
    check("DirichletBeta(1.3-I)", //
        "0.898582+I*(-0.135951)");
    check("Table(DirichletBeta(x), {x, -4, 4}) // N", //
        "{2.5,0.0,-0.5,0.0,0.5,0.785398,0.915966,0.968946,0.988945}");
  }

  @Test
  public void testDirichletEta() {
    check("DirichletEta(1)", //
        "Log(2)");
    check("DirichletEta(1.0)", //
        "0.693147");
    check("Table(DirichletEta(x), {x, -4, 4}) // N", //
        "{0.0,-0.125,0.0,0.25,0.5,0.693147,0.822467,0.901543,0.947033}");
  }

  @Test
  public void testDiscriminant() {
    // github #122
    check("Discriminant((2*x^5)-(19*x^4)+(58*x^3)-(67*x^2)+(56*x)-48,x)", //
        "0");

    check("Discriminant(x^10 - 5*x^7 - 3*x + 9, x)", //
        "177945374758153510836");

    check("Resultant(f+g*x+h*x^2,g+2*h*x, x)", //
        "-g^2*h+4*f*h^2");
    // print message Discriminant: the function: Discriminant(Sqrt(x),x) has wrong argument Sqrt(x)
    // at position:1:
    // Polynomial expected!
    check("Discriminant(x^(1/2), x)", "Discriminant(Sqrt(x),x)");

    check("Discriminant(f+g*x+h*x^2, x)", //
        "g^2-4*f*h");
    check("Discriminant(a*x^2 + b*x + c, x)", //
        "b^2-4*a*c");
    check("Discriminant(x^10 - 5*x^7 - 3*x + 9, x)", //
        "177945374758153510836");
    check("Discriminant(a*x^3 + b*x^2 + c*x + g, x)", //
        "b^2*c^2-4*a*c^3-4*b^3*g+18*a*b*c*g-27*a^2*g^2");
    check("Discriminant(a*x^4 + b*x^3 + c*x^2 + d*x + e, x)", //
        "b^2*c^2*d^2-4*a*c^3*d^2-4*b^3*d^3+18*a*b*c*d^3-27*a^2*d^4-4*b^2*c^3*e+16*a*c^4*e+\n"
            + "18*b^3*c*d*e-80*a*b*c^2*d*e-6*a*b^2*d^2*e+144*a^2*c*d^2*e-27*b^4*e^2+144*a*b^2*c*e^\n"
            + "2-128*a^2*c^2*e^2-192*a^2*b*d*e^2+256*a^3*e^3");
  }

  @Test
  public void testDisjointQ() {
    check("DisjointQ(f(d,f,e), f(a, b, c))", //
        "True");
    check("DisjointQ(f(b, a, b), f(a, b, c))", //
        "False");

    // same as ContainsNone
    check("DisjointQ({d,f,e}, {a, b, c})", //
        "True");
    check("DisjointQ({b, a, b}, {a, b, c})", //
        "False");
    check("DisjointQ({ }, {a, b, c})", //
        "True");

    check("DisjointQ(1, {1,2,3})", //
        "DisjointQ(1,{1,2,3})");
    check("DisjointQ({1,2,3}, 4)", //
        "DisjointQ({1,2,3},4)");

    check("DisjointQ({1.0,2.0}, {1,2,3})", //
        "True");
    check("DisjointQ({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "False");
  }

  @Test
  public void testDispatch() {
    check("emptyRul=Dispatch({ })", //
        "Dispatch({})");
    check("{\"a\", b, \"c\", d, e} /. emptyRul", //
        "{a,b,c,d,e}");
    check("rul=Dispatch({\"a\" -> 1, \"b\" -> 2, \"c\" -> 3})", //
        "Dispatch({a->1,b->2,c->3})");
    check("{\"a\", b, \"c\", d, e} /. rul", //
        "{1,b,3,d,e}");
    check("rul // Normal // InputForm", //
        "{\"a\"->1,\"b\"->2,\"c\"->3}");

    check("rul = {a -> b, b -> c, c -> a, d -> e, e -> d};", //
        "");
    check("dis= Dispatch(rul);", //
        "");
    check("{a, b, c, d, e} /. rul", //
        "{b,c,a,e,d}");
    check("{a, b, c, d, e} /. dis", //
        "{b,c,a,e,d}");

    check("Atomq(dis)", //
        "True");
    check("Normal(dis)", //
        "{a->b,b->c,c->a,d->e,e->d}");

    check("dis= Dispatch({1 -> a, 2 -> b, c_?NumericQ -> Infinity})", //
        "Dispatch({1->a,2->b,c_?NumericQ->Infinity})");
    check("{1, 2, 3, Pi, Sin[x], Cos[3]} /. dis", //
        "{a,b,Infinity,Infinity,Sin(x),Infinity}");

    check("dis= Dispatch(<|a -> b, c -> d|>);", //
        "");
    check("{a, b, c, d, e} /. dis", //
        "{b,b,d,d,e}");
  }

  @Test
  public void testDistribute() {
    // check(
    // "Distribute(x^10 - 1)", //
    // "-1+x^10");
    // check("Distribute(Factor(x^10 - 1))", //
    // "-1+x^10");

    check("Distribute((a + b).(x + y + z))", //
        "a.x+a.y+a.z+b.x+b.y+b.z");
    check("Distribute(f(a + b, c + d + e))", //
        "f(a,c)+f(a,d)+f(a,e)+f(b,c)+f(b,d)+f(b,e)");
    check("Distribute(f(g(a, b), g(c, d, e)), g)", //
        "g(f(a,c),f(a,d),f(a,e),f(b,c),f(b,d),f(b,e))");
    check("Distribute((a + b + c) (u + v))", //
        "a*u+b*u+c*u+a*v+b*v+c*v");
    check("Distribute((a + b + c) (u + v), Plus)", //
        "a*u+b*u+c*u+a*v+b*v+c*v");
    check("Distribute((a + b + c)*(u + v), Plus, Times)", //
        "a*u+b*u+c*u+a*v+b*v+c*v");
    check("Distribute((a + b + c)^(u + v), Plus, Times)", //
        "(a+b+c)^(u+v)");
    check("Distribute({{a, b}, {x, y, z}, {s, t}}, List)", //
        "{{a,x,s},{a,x,t},{a,y,s},{a,y,t},{a,z,s},{a,z,t},{b,x,s},{b,x,t},{b,y,s},{b,y,t},{b,z,s},{b,z,t}}");
    check("Distribute((x*y*z)^n0, Times)", //
        "x^n0*y^n0*z^n0");
    check("Distribute(And(Or(a, b, c), Or(u, v)), Or, And)", //
        "(a&&u)||(a&&v)||(b&&u)||(b&&v)||(c&&u)||(c&&v)");
    check("Distribute((a + b).(x + y + z))", //
        "a.x+a.y+a.z+b.x+b.y+b.z");
    check("Distribute(f(g(a, b), g(c, d, e)), g, f, gp, fp)", //
        "gp(fp(a,c),fp(a,d),fp(a,e),fp(b,c),fp(b,d),fp(b,e))");
    check("Distribute(Factor(x^10 - 1))", //
        "-1+x^10");
    check("Distribute(Factor(x^6 - 1), Plus, Times, List, Times)", //
        "{-1,-x,-x^2,x,x^2,x^3,-x^2,-x^3,-x^4,-x,-x^2,-x^3,x^2,x^3,x^4,-x^3,-x^4,-x^5,x,x^\n"
            + "2,x^3,-x^2,-x^3,-x^4,x^3,x^4,x^5,x^2,x^3,x^4,-x^3,-x^4,-x^5,x^4,x^5,x^6}");
    check("Distribute((x*y*z)^n, Times)", //
        "x^n*y^n*z^n");

    // possibly not intended:
    check("Distribute((a + b)*(a + b))", //
        "a^2+b^2");
  }

  @Test
  public void testDivide() {
    check("Divide(a,b) // FullForm", //
        "Times(a, Power(b, -1))");
    check("1/2/3/5", //
        "1/30");
    check("30 / 5", //
        "6");
    check("1 / 8", //
        "1/8");
    check("Pi / 4", //
        "Pi/4");
    check("Pi / 4.0", //
        "0.785398");
    check("N(1 / 8)", //
        "0.125");
    check("a / b / c", //
        "a/(b*c)");
    check("a / (b / c)", //
        "(a*c)/b");
    check("a / b / (c / (d / e))", //
        "(a*d)/(b*c*e)");
    check("a / (b ^ 2 * c ^ 3 / e)", //
        "(a*e)/(b^2*c^3)");
    check("1 / 4.0", //
        "0.25");
    check("10 / 3 // FullForm", //
        "Rational(10,3)");
    check("a / b // FullForm", //
        "Times(a, Power(b, -1))");
  }

  @Test
  public void testDivideBy() {
    check("a = 10", //
        "10");
    check("a /= 2", //
        "5");
    check("a", //
        "5");

    check("index={1,2,3,4,5,6,7,8,9}", //
        "{1,2,3,4,5,6,7,8,9}");
    check("index[[3]]/=y", //
        "3/y");
    check("index", //
        "{1,2,3/y,4,5,6,7,8,9}");
  }

  @Test
  public void testDivisible() {
    check("Divisible(2*Pi, Pi/2)", //
        "True");
    check("Divisible(42,7)", //
        "True");
    check("Divisible(10,3)", //
        "False");
    check("Divisible(2^100-1,3)", //
        "True");
    check("Divisible({200, 201, 202, 203}, 3)", //
        "{False,True,False,False}");
    check("Divisible(3/4, 1/4)", //
        "True");
    check("Divisible(3 + I, 2 - I)", //
        "True");
  }

  @Test
  public void testDivisors() {
    check("Length(Divisors(Factorial(18)))", //
        "14688");
    check("Divisors(5)", //
        "{1,5}");
    check("Divisors(101)", //
        "{1,101}");
    check("Divisors(-2147483648)", //
        "{1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,\n"
            + "262144,524288,1048576,2097152,4194304,8388608,16777216,33554432,67108864,\n"
            + "134217728,268435456,536870912,1073741824,2147483648}");
    check(
        "a178752(n_):=Sum((1/GCD(n, k))*2^s *EulerPhi(GCD(n, k)/s), {k, 0, n-1}, {s, Divisors(GCD(n, k))})", //
        "");
    check("Table(a178752(n),{n,30})", //
        "{2,5,8,13,16,28,32,56,80,136,208,400,656,1232,2240,4192,7744,14728,27632,52664,\n"
            + "99968,190984,364768,699760,1342256,2582120,4971248,9588880,18512848,35795104}");
    check("Divisors(990)", //
        "{1,2,3,5,6,9,10,11,15,18,22,30,33,45,55,66,90,99,110,165,198,330,495,990}");
    check("Divisors(341550071728321)", //
        "{1,10670053,32010157,341550071728321}");
    check("Divisors(2010)", //
        "{1,2,3,5,6,10,15,30,67,134,201,335,402,670,1005,2010}");
    check("Divisors(0)", //
        "Divisors(0)");
    check("Divisors(1)", //
        "{1}");
    check("Divisors(6)", //
        "{1,2,3,6}");
    check("Divisors(-2)", //
        "{1,2}");
    check("Divisors(-6)", //
        "{1,2,3,6}");
    check("Divisors(24)", //
        "{1,2,3,4,6,8,12,24}");
    check("Divisors(1729)", //
        "{1,7,13,19,91,133,247,1729}");
    check("FactorInteger(1729)", //
        "{{7,1},{13,1},{19,1}}");

    check("Divisors({605,871,824})", //
        "{{1,5,11,55,121,605},{1,13,67,871},{1,2,4,8,103,206,412,824}}");
  }

  @Test
  public void testDivisorSigma() {
    check("DivisorSigma(17,20)", //
        "13107300000780119453382");
    check("DivisorSigma(0,12)", //
        "6");
    check("DivisorSigma(1,12)", //
        "28");
    check("DivisorSigma(1,20)", //
        "42");
    check("DivisorSigma(2,20)", //
        "546");
    check("DivisorSigma(2, {1, 2, 3, 4, 5})", //
        "{1,5,10,21,26}");
    check("DivisorSigma(k,10)", //
        "1+2^k+5^k+10^k");
    check("DivisorSigma(4,15)", //
        "51332");
  }

  @Test
  public void testDivisorSum() {
    // https://oeis.org/A002791
    check("a(n_) := DivisorSum(n, #^2 &, # < 5 &) + 4 * DivisorSum(n, # &, # > 4 &); Array(a,70)", //
        "{1,5,10,21,21,38,29,53,46,65,45,102,53,89,90,117,69,146,77,161,122,137,93,230,\n" + //
            "121,161,154,217,117,278,125,245,186,209,189,354,149,233,218,353,165,374,173,329,\n" + //
            "306,281,189,486,225,365,282,385,213,470,285,473,314,353,237,662,245,377,410,501,\n" + //
            "333,566,269,497,378,569}");

    check("DivisorSum(11, # &, PrimeQ)", //
        "11");
    check("DivisorSum(1000, #^2 &)", //
        "1383460");
    check("DivisorSum(20, # &, # < 5 &)", //
        "7");
    check("DivisorSum(20, # &)", //
        "42");
    check("DivisorSum(30, # &)", //
        "72");
    check("DivisorSum(20, f)", //
        "f(1)+f(2)+f(4)+f(5)+f(10)+f(20)");
    check("DivisorSum({2, 5, 10}, #^2 &)", //
        "{5,26,130}");
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      check("DivisorSum(10^50 + 1, # &)", //
          "101023927335579679355031598994389503718340251571328");
    }
  }

  @Test
  public void testDo() {
    check("Do(marray(i,10-i+1)=1, {i,1,10});marray(1,10)", //
        "1");
    check("Do(Print(i0),{})", //
        "Do(Print(i0),{})");

    check("g(x_) := (Do(If(x < 0, Return(0)), {i, {2, 1, 0, -1}}); x)", //
        "");
    check("g(-1)", //
        "-1");

    // http://oeis.org/A005132 - Recaman's sequence
    check(
        "a = {1}; Do( If( a[ [ -1 ] ] - n > 0 && Position( a, a[ [ -1 ] ] - n ) == {}, a = Append( a, a[ [ -1 ] ] - n ), a = Append( a, a[ [ -1 ] ] + n ) ), {n, 2, 70} ); a", //
        "{1,3,6,2,7,13,20,12,21,11,22,10,23,9,24,8,25,43,62,42,63,41,18,42,17,43,16,44,15,\n"
            + "45,14,46,79,113,78,114,77,39,78,38,79,37,80,36,81,35,82,34,83,33,84,32,85,31,86,\n"
            + "30,87,29,88,28,89,27,90,26,91,157,224,156,225,155}");
    check("Do(Print(i), {i, 2, 4})", //
        "");
    check("Do(Print({i, j}), {i,1,2}, {j,3,5})", //
        "");
    check("Do(If(i > 10, Break(), If(Mod(i, 2) == 0, Continue()); Print(i)), {i, 5, 20})", "");
    check("Do(Print(\"hi\"),{1+1})", "");

    check("reap(do(if(primeQ(2^n0 - 1), sow(n0)), {n0, 100}))[[2, 1]]", //
        "{2,3,5,7,13,17,19,31,61,89}");
    check("$t = x; Do($t = 1/(1 + $t), {5}); $t", //
        "1/(1+1/(1+1/(1+1/(1+1/(1+x)))))");
    check("Nest(1/(1 + #) &, x, 5)", //
        "1/(1+1/(1+1/(1+1/(1+1/(1+x)))))");
  }

  @Test
  public void testDownValues() {
    check("f(1)=3", //
        "3");
    check("f(x_):=x^3", //
        "");
    check("DownValues(f)", //
        "{HoldPattern(f(1)):>3,HoldPattern(f(x_)):>x^3}");
  }

  @Test
  public void testDrop() {
    // message Drop: Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position
    // 2 in x.
    check("Drop({1,2,3}, {3, x})", //
        "Drop({1,2,3},{3,x})");

    check("Drop(SparseArray(Range(1000)), {3, -4})", //
        "{1,2,998,999,1000}");
    check("Drop({a,b,c,d},1317624576693539401)", //
        "Drop({a,b,c,d},1317624576693539401)");
    check("Drop(<|1 -> a, 2 -> b, 3 -> c, 4 -> d|>, {2, -1})", //
        "<|1->a|>");
    check("Drop(<|1 -> a, 2 -> b, 3 -> c|>, {2})", //
        "<|1->a,3->c|>");
    check("Drop({}, 1)", //
        "Drop({},1)");
    check("Drop({a, b, c, d}, 3)", //
        "{d}");
    check("Drop({a, b, c, d}, -2)", //
        "{a,b}");
    check("Drop({a, b, c, d, e}, {2, -2})", //
        "{a,e}");

    check("A = Table(i*10 + j, {i, 4}, {j, 4})", //
        "{{11,12,13,14},{21,22,23,24},{31,32,33,34},{41,42,43,44}}");
    check("Drop(A, {2, 3}, {2, 3})", //
        "{{11,14},{41,44}}");
    check("Drop(Range(10), {-2, -6, -3})", //
        "{1,2,3,4,5,7,8,10}");
    check("Drop(Range(10), {10, 1, -3})", //
        "{2,3,5,6,8,9}");
    check("Drop(Range(6), {-5, -2, -2}) ", //
        "Drop({1,2,3,4,5,6},{-5,-2,-2})");
    check("Drop(Range(6), {0, 3, 1}) ", //
        "Drop({1,2,3,4,5,6},{0,3,1})");
    check("Drop(Range(6), {1, 3, 1}) ", //
        "{4,5,6}");

    check("Drop({a, b, c, d, e, f}, 2)", //
        "{c,d,e,f}");
    check("Drop({a, b, c, d, e, f}, -3)", //
        "{a,b,c}");
    check("Drop({a, b, c, d, e, f}, {2, 4})", //
        "{a,e,f}");
    check("Drop({{11, 12, 13}, {21, 22, 23}, {31, 32, 33}}, 1, 2)", //
        "{{23},{33}}");
    check("Drop({{11, 12, 13}, {21, 22, 23}, a, {31, 32, 33}}, 1, 2)", //
        "Drop({{11,12,13},{21,22,23},a,{31,32,33}},1,2)");
  }

  @Test
  public void testDrop0() {
    check("Drop({2,3}, 0)", //
        "{2,3}");
    check("Drop({}, 0)", //
        "{}");
  }

  // @Test
  // public void testDSolve() {
  // check("DSolve({(2*y(x)-x^2)+(2*x-y(x)^2)*y'(x)==0},y(x), x)",
  // "{{y(x)->1/(x^2-C(1))}}");
  // check("DSolve({y'(x)==2*x*y(x)^2},y(x), x)", "{{y(x)->1/(-x^2-C(1))}}");
  // check("DSolve({(2*y(x)-x^2)+(2*x-y(x)^2)*y'(x)==0},y(x), x)",
  // "{{y(x)->1/(x^2-C(1))}}");
  // check("DSolve({(6*x*y(x)+y(x)^2)*y'(x)==-2*x-3*y[x]^2},y(x), x)",
  // "{{y(x)->E^x}}");
  // }

  @Test
  public void testDSolve() {
    check("DSolve(y'(t)==t+y(t), y, t)", //
        "{{y->Function({t},-1-t+E^t*C(1))}}");

    check("DSolve(y'(x)==2*x*y(x)^2,Null,x)", //
        "DSolve(y'(x)==2*x*y(x)^2,Null,x)");
    check("DSolve({},y,t)", //
        "DSolve({},y,t)");

    check("DSolve(y'(t)==y(t), y, t)", //
        "{{y->Function({t},E^t*C(1))}}");

    check("DSolve(y'(x)==2*x*y(x)^2, y, x)", //
        "{{y->Function({x},1/(-x^2-C(1)))}}");
    check("DSolve(y'(x)==2*x*y(x)^2, y(x), x)", //
        "{{y(x)->1/(-x^2-C(1))}}");
    check("DSolve({y'(x)==2*x*y(x)^2},y(x), x)", //
        "{{y(x)->1/(-x^2-C(1))}}");

    check("DSolve(D(f(x, y), x) == D(f(x, y), y), f, {x, y})", //
        "DSolve(Derivative(1,0)[f][x,y]==Derivative(0,1)[f][x,y],f,{x,y})");

    // check("DSolve({y'(x)==y(x),y(0)==1},y(x), x)", "{{y(x)->E^x}}");
    check("DSolve({y'(x)==y(x)+2,y(0)==1},y(x), x)", "{{y(x)->-2+3*E^x}}");

    check("DSolve({y(0)==0,y'(x) + y(x) == a*Sin(x)}, y(x), x)", //
        "{{y(x)->a/(2*E^x)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");
    check("DSolve({y'(x) + y(x) == a*Sin(x),y(0)==0}, y(x), x)", //
        "{{y(x)->a/(2*E^x)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");

    check("DSolve(y'(x) + y(x) == a*Sin(x), y(x), x)", //
        "{{y(x)->C(1)/E^x-1/2*a*Cos(x)+1/2*a*Sin(x)}}");

    check("DSolve(y'(x)-x ==0, y(x), x)", //
        "{{y(x)->x^2/2+C(1)}}");
    check("DSolve(y'(x)+k*y(x) ==0, y(x), x)", //
        "{{y(x)->C(1)/E^(k*x)}}");

    check("DSolve(y'(x)-3/x*y(x)-7==0, y(x), x)", //
        "{{y(x)->-7/2*x+x^3*C(1)}}");
    check("DSolve(y'(x)== 0, y(x), x)", //
        "{{y(x)->C(1)}}");
    check("DSolve(y'(x) + y(x)*Tan(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*Cos(x)}}");
    check("DSolve(y'(x) + y(x)*Cos(x) == 0, y(x), x)", //
        "{{y(x)->C(1)/E^Sin(x)}}");
    check("DSolve(y'(x) == 3*y(x), y(x), x)", //
        "{{y(x)->E^(3*x)*C(1)}}");
    check("DSolve(y'(x) + 2*y(x)/(1-x^2) == 0, y(x), x)", //
        "{{y(x)->C(1)/E^(2*ArcTanh(x))}}");
    check("DSolve(y'(x) == -y(x), y(x), x)", //
        "{{y(x)->C(1)/E^x}}");
    check("DSolve(y'(x) == y(x)+a*Cos(x), y(x), x)", //
        "{{y(x)->E^x*C(1)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");
    // not implemented yet
    check("DSolve(y'(x) == -3*y(x)^2, y(x), x)", //
        "{{y(x)->1/(3*x-C(1))}}");
    check("DSolve({y'(x) == -3*y(x)^2, y(0)==2}, y(x), x)", //
        "{{y(x)->1/(1/2+3*x)}}");
  }

  @Test
  public void testDuplicateFreeQ() {
    check("DuplicateFreeQ({1, 7, 8, 4, 3, 9, 2})", //
        "True");
    check("DuplicateFreeQ({1, 7, 8, 4, 3, 4, 1, 9, 9, 2})", //
        "False");

    check(
        "l = {{0, 0, 0, 1, 0}, {1, 0, 1, 0, 1}, {1, 1, 1, 0, 0}, {0, 0, 0, 0, 1}, {1, 1, 1, 0, 1}};", //
        "");
    check("DuplicateFreeQ(l, Total(#1) == Total(#2) &)", //
        "False");
  }

  @Test
  public void testEasterSunday() {
    check("EasterSunday(2000)", //
        "{2000,4,23}");
    check("EasterSunday(2030)", //
        "{2030,4,21}");
  }

  @Test
  public void testEcho() {
    check("{Echo(f(x,y)), Print(g(a,b))}", //
        // >> f(x,y)
        // >> g(a,b)
        "{f(x,y),Null}");
    check("Echo(2*2)+Echo(3^4)", //
        // >> 4
        // >> 81
        "85");
    check("expr1; x = Echo(1 + 1, \"sum: \"); expr2", //
        // >> sum: 2
        "expr2");
    check("expr1; x = Echo(f(x,g(y,z)), \"leaf count: \",LeafCount); expr2", //
        // >> leaf count: 5
        "expr2");
    check("x = Echo[Unevaluated[1 + 1]]; x^2", //
        // >> 1+1
        "4");
  }

  @Test
  public void testEchoFunction() {
    check("{EchoFunction()[f(x,y)], Print(g(a,b))}", //
        // >> f(x,y)
        // >> g(a,b)
        "{f(x,y),Null}");
    check("EchoFunction()[2*2]+EchoFunction()[3^4]", //
        // >> 4
        // >> 81
        "85");
    check("EchoFunction(\"test: \", f)[expr]", //
        // >> test: f(expr)
        "expr");
    check("EchoFunction(f)[expr]", //
        // >> f(expr)
        "expr");
  }

  @Test
  public void testEffectiveInterest() {
    // TODO
    // check("EffectiveInterest({{0, .05}, {3, .065}, {5, .07}, {6, .085}}, 1/12)", //
    // "{{0,0.0511619},{3,0.0669719},{5,0.0722901},{6,0.0883909}}");
    check("EffectiveInterest(a,b)", //
        "-1+(1+a*b)^(1/b)");

    check("EffectiveInterest({.05, .065, .07, .085}, 1/12)", //
        "{0.0511619,0.0669719,0.0722901,0.0883909}");
    check("EffectiveInterest(SparseArray({.05, .065, .07, .085}), 1/12)", //
        "{0.0511619,0.0669719,0.0722901,0.0883909}");

    check("EffectiveInterest({.05, .065, .07, .085}, 1/12)", //
        "{0.0511619,0.0669719,0.0722901,0.0883909}");
    check("EffectiveInterest(.1, 0)", //
        "0.105171");
    check("EffectiveInterest(.06, 3)", //
        "0.0567218");

    check("EffectiveInterest({a,b,c,d})", //
        "-1+((1+a)*(1+b)*(1+c)*(1+d))^(1/4)");
    check("EffectiveInterest(SparseArray({a,b,c,d}))", //
        "-1+((1+a)*(1+b)*(1+c)*(1+d))^(1/4)");

    check("FindRoot(EffectiveInterest(r, 1/4) == .05, {r, .05})", //
        "{r->0.0490889}");
    check("Solve(EffectiveInterest(nr, 1/f) == eff, nr) // Quiet", //
        "{{nr->(-1+(1+eff)^(1/f))*f}}");
  }

  @Test
  public void testElement() {
    check("Element(N(1,50), Integers)", //
        "1∈Integers");

    check("Element(Sqrt(2), #) & /@ {Complexes, Algebraics, Reals, Rationals, Integers, Primes}", //
        "{True,Sqrt(2)∈Algebraics,True,Sqrt(2)∈Rationals,False,False}");
    check("Element(ComplexInfinity, Complexes)", //
        "False");
    check("{x} \\[Element] Reals", //
        "x∈Reals");
    check("{ } \\[Element] Reals", //
        "True");
    check("{x, y, I} \\[Element] Reals", //
        "False");
    check("Element(Undefined, Reals)", //
        "Undefined");
    check("Element(ComplexInfinity, Reals)", //
        "False");
    check("Element(Infinity, Reals)", //
        "False");
    check("Element(-Infinity, Reals)", //
        "False");
    check("Element(a | b | c, Reals)", //
        "(a|b|c)∈Reals");
    check("Element(a | 2 | c, Reals)", //
        "(a|c)∈Reals");
    check("Element(pi, reals)", //
        "True");
    check("Element(sin, reals)", //
        "Sin∈Reals");
    // check("Element(Sqrt(2), #) & /@ {Complexes, Algebraics, Reals, Rationals, Integers,
    // Primes}",//
    // "");
    check("Element(E, Algebraics)", //
        "False");
    check("Element(Pi, Algebraics)", //
        "False");
    check("Element(ComplexInfinity, Algebraics)", //
        "False");
    check("Element(I, Algebraics)", //
        "True");
  }

  @Test
  public void testElementData() {
    // TODO
    // check("Length(ElementData(All))", "118");

    check("ElementData(74)", //
        "Tungsten");
    check("ElementData(\"He\", \"AbsoluteBoilingPoint\")", //
        "4.22");
    check("ElementData(\"Carbon\", \"IonizationEnergies\")", //
        "{1086.5,2352.6,4620.5,6222.7,37831,47277.0}");
    check("ElementData(16, \"ElectronConfigurationString\")", //
        "[Ne] 3s2 3p4");
    check("ElementData(73, \"ElectronConfiguration\")", //
        "{{2},{2,6},{2,6,10},{2,6,10,14},{2,6,3},{2}}");

    check("ElementData(\"He\", \"ElectroNegativity\")", //
        "Missing(NotApplicable)");
    check("ElementData(\"Tc\", \"SpecificHeat\")", //
        "Missing(NotAvailable)");
    check("ElementData(\"Properties\")", //
        "{StandardName,AtomicNumber,Abbreviation,AbsoluteBoilingPoint,AbsoluteMeltingPoint,AtomicRadius,AtomicWeight,Block,BoilingPoint,BrinellHardness,BulkModulus,CovalentRadius,CrustAbundance,Density,DiscoveryYear,ElectroNegativity,ElectronAffinity,ElectronConfiguration,ElectronConfigurationString,ElectronShellConfiguration,FusionHeat,Group,IonizationEnergies,LiquidDensity,MeltingPoint,MohsHardness,Name,Period,PoissonRatio,Series,ShearModulus,SpecificHeat,ThermalConductivity,VanDerWaalsRadius,VaporizationHeat,VickersHardness,YoungModulus}");

    check("ElementData(6)", "Carbon");
    check("ElementData(\"Carbon\", \"Name\")", //
        "carbon");
    check("ElementData(79, \"Abbreviation\")", //
        "Au");
    check("ElementData(\"Au\", \"StandardName\")", //
        "Gold");
    check("ElementData(\"Gold\", \"AtomicNumber\")", //
        "79");
    check("ElementData(\"Carbon\", \"AtomicNumber\")", //
        "6");
    check("ElementData(\"He\", \"AtomicNumber\")", //
        "2");

    check("ElementData(\"Chlorine\", \"BoilingPoint\")", //
        "-34.04");
    check("ElementData(\"C\", \"AtomicWeight\")", //
        "12.01");
    check("ElementData(117, \"AtomicWeight\")", //
        "294");

    // check("ElementData(\"Pd\", \"AtomicRadius\")", "140");
    check("ElementData(\"Pd\", \"VanDerWaalsRadius\")", //
        "163");
    // check("ElementData(\"Pd\", \"CovalentRadius\")", "131");
    check("ElementData(\"Pd\", \"IonizationEnergies\")", //
        "{804.4,1870,3177}");

    check("ElementData(\"Pd\", \"ElectronAffinity\")", //
        "54.24");
    check("ElementData(\"Pd\", \"ThermalConductivity\")", //
        "71.8");
    check("ElementData(\"Pd\", \"YoungModulus\")", //
        "121");
    check("ElementData(\"Pd\", \"PoissonRatio\")", //
        "0.39");
    check("ElementData(\"Pd\", \"BulkModulus\")", //
        "180");
    check("ElementData(\"Pd\", \"ShearModulus\")", //
        "44");
    check("ElementData(\"Pd\", \"ElectronConfiguration\")", //
        "{{2},{2,6},{2,6,10},{2,6,10}}");
    check("ElementData(\"Pd\", \"ElectronConfigurationString\")", //
        "[Kr] 4d10");
    check("ElementData(\"Pd\", \"ElectronShellConfiguration\")", //
        "{2,8,18,18}");

    // check("ElementData(\"Helium\", \"MeltingPoint\")",
    // "Missing(NotApplicable)");
    // check("ElementData(\"Tungsten\", \"ThermalConductivity\")", "173");

  }

  @Test
  public void testEliminate() {
    check("Eliminate({x^2 + y^2 + z^2 == 1, x - y + z == 2, x^3 - y^2 == z + 1}, {y, z})",
        "-4*x+2*x^2-4*z+2*x*z+2*z^2==-3&&4*x-x^2+x^3+3*z-2*x*z-z^2==5");

    // print: Eliminate: y>2 is not a well-formed equation.
    check("Eliminate({x==y,y>2},{x})", //
        "Eliminate({x==y,y>2},{x})");

    // TODO
    // check("Eliminate({a0*x^p+a1*x^q==0},x)", //
    // "(-a1)*x^q == a0*x^p");

    check("Eliminate({(a*x + b)/(c*x + d)==y},x)", //
        "True");
    check("Eliminate({x == 2 + y, y == z}, y)", //
        "x-z==2");
    check("Eliminate({x == 2 + y, y == z}, {y,v})", //
        "x-z==2");
    check("Eliminate({2*x + 3*y + 4*z == 1, 9*x + 8*y + 7*z == 2}, z)", //
        "11/2*x+11/4*y==1/4");

    check("Eliminate({x == 2 + y^3, y^2 == z}, y)", //
        "x-z^(3/2)==2");

    // use evaluation step: Cos(ArcSin(y)) => Sqrt(1-y^2)
    check("Eliminate({Sin(x)==y, Cos(x) == z}, x)", //
        "Sqrt(1-y^2)-z==0");
    check("Eliminate({a^x==y, b^(2*x) == z}, x)", //
        "b^((2*Log(y))/Log(a))-z==0");
  }

  @Test
  public void testEllipticE() {
    // TODO implement second kind arbitrary precision
    checkNumeric("N(EllipticE(2/3,2),50)", //
        "EllipticE(0.66666666666666666666666666666666666666666666666666,2)");


    // https://github.com/Hipparchus-Math/hipparchus/issues/148
    check("EllipticE(3 + 2.5*I, 2.3 - 1.5*I)", //
        "3.05969+I*11.16503");
    check("EllipticE(I-2.0,0.5)", //
        "-1.47153+I*0.714415");
    check("EllipticE(2,0.999999)", //
        "1.09071");
    check("EllipticE(-Pi/2,0.5)", //
        "-1.35064");

    check("Table(EllipticE(x,0.5), {x,-2.0, 2.0, 1/4})", //
        "{-1.6629,-1.47803,-1.30054,-1.12005,-0.92733,-0.717351,-0.489911,-0.248708,0.0," + //
            "0.248708,0.489911,0.717351,0.92733,1.12005,1.30054,1.47803,1.6629}");
    check("Table(EllipticE(I+x,0.5), {x,-2.0, 2.0, 1/4})", //
        "{-1.47153+I*0.714415,-1.34791+I*0.585355,-1.37918+I*0.53464,-1.29305+I*0.657158,-1.12766+I*0.789102,"
            + //
            "-0.901309+I*0.911354,-0.627973+I*1.00908,-0.322266+I*1.07186,I*1.09348,0.322266+I*1.07186,0.627973+I*1.00908,"
            + //
            "0.901309+I*0.911354,1.12766+I*0.789102,1.29305+I*0.657158,1.37918+I*0.53464,1.34791+I*0.585355,1.47153+I*0.714415}");


    checkNumeric("EllipticE(0.4)", //
        "1.3993921388974322");
    checkNumeric("EllipticE(3 + 2.5*I)", //
        "1.1997488433342685+I*(-1.3570974376009501)");
    checkNumeric("N(EllipticE(1/3),50)", //
        "1.4303152571722197239225284145322837302944509074088");
    checkNumeric("EllipticE(0.1234567890000000000)", //
        "1.521130747104144722");
    checkNumeric("EllipticE(0.1234567890000000000000000000000)", //
        "1.521130747104144722956858486312");

    check("EllipticE(0, m)", //
        "0");
    check("EllipticE(z,0)", //
        "z");
    check("EllipticE(1/2)", //
        "(Pi^2+2*Gamma(3/4)^4)/(4*Sqrt(Pi)*Gamma(3/4)^2)");
    check("EllipticE(-1)", //
        "(Pi^2+2*Gamma(3/4)^4)/(2*Sqrt(2*Pi)*Gamma(3/4)^2)");
    check("EllipticE(Pi/2, m)", //
        "EllipticE(m)");
    check("EllipticE(5/4,1)", //
        "Sin(5/4)");
    check("EllipticE(0.4)", //
        "1.39939");
    // needs formula for complex numbers
    // check("Table(EllipticE(x ), {x,-2.0, 2.0, 1/4})", "");

  }

  @Test
  public void testEllipticF() {
    check("EllipticF(0.3,0.8)", //
        "0.303652");

    // https://github.com/paulmasson/math/issues/13
    check("EllipticF(-1.5708,1.5708)", //
        "-1.58877+I*1.39865");

    // see github #109
    check("EllipticF(3,1/2)//N", //
        "3.56632");
    check("EllipticF(17/2*Pi, m)", //
        "17*EllipticK(m)");
    check("EllipticF(Pi/2, m)", //
        "EllipticK(m)");
    check("EllipticF(a+42*Pi,m)", //
        "EllipticF(a,m)+84*EllipticK(m)");
    check("EllipticF(-z,m)", //
        "-EllipticF(z,m)");
    check("EllipticF(0, m)", //
        "0");
    check("EllipticF(z,0)", //
        "z");
    check("EllipticF(5/4, 1)", //
        "Log(Sec(5/4)+Tan(5/4))");
    check("EllipticF(3, 1)", //
        "ComplexInfinity");
    check("EllipticF(Pi, 1)", //
        "ComplexInfinity");
    check("Table(EllipticF(x,0.5), {x,-2.0, 2.0, 1/4})", //
        "{-2.44438,-2.10618,-1.75404,-1.4077,-1.08322,-0.785382,-0.510467,-0.251304,0.0,0.251304,0.510467,0.785382,1.08322,1.4077,1.75404,2.10618,2.44438}");
    check("Table(EllipticF(I+x,0.5), {x,-2.0, 2.0, 1/4})", //
        "{-2.77616+I*1.25663,-2.53768+I*1.51416,-1.26198+I*1.69875,-1.03395+I*1.34928,-0.804237+I*1.16274,-0.591003+I*1.04644,"
            + //
            "-0.388636+I*0.973573,-0.192779+I*0.933104,I*0.920097,0.192779+I*0.933104,0.388636+I*0.973573,0.591003+I*1.04644,0.804237+I*1.16274,"
            + //
            "1.03395+I*1.34928,1.26198+I*1.69875,2.53768+I*1.51416,2.77616+I*1.25663}");
  }

  @Test
  public void testEllipticK() {

    check("N(EllipticK(8/10), 50)", //
        "2.2572053268208536550832560045233873972354192817399");

    check("EllipticK(0.999999999999999990000000000000000)", //
        "20.9582676515692789828830607366566");
    // reducing the accuracy gives ComplexInfinity in MMA:
    check("EllipticK(0.99999999999999999)", //
        "20.958267651569278");

    checkNumeric("EllipticK(2.5+I)", //
        "1.1551450606569331+I*0.9528453714670536");

    check("EllipticK(0.5)", //
        "1.85407");
    check("EllipticK(-1.0+I)", //
        "1.26549+I*0.162237");
    check("Table(EllipticK(x), {x,-1.0, 1.0, 1/4})", //
        "{1.31103,1.35906,1.41574,1.48441,1.5708,1.68575,1.85407,2.15652,ComplexInfinity}");
    check("Table(EllipticK(x+I), {x,-1.0, 1.0, 1/4})", //
        "{1.26549+I*0.162237,1.30064+I*0.18478,1.33866+I*0.213052,1.37925+I*0.249038,1.42127+I*0.29538,1.46203+I*0.355241,1.49611+I*0.431362,1.51493+I*0.523542,1.50924+I*0.625146}");
  }

  @Test
  public void testEllipticPi() {
    check("EllipticPi(0.4, 0.6)", //
        "2.59092");
    check("EllipticPi(0,-1,1.5708)", //
        "-1.58877+I*0.451013");
    // github #172
    check("EllipticPi(I,1+(-1)*1,0.3)", //
        "0.0");
    check("Table(EllipticPi(x+I,0.5), {x,-2.0, 2.0, 1/4})", //
        "{0.978856+I*0.171427,1.01788+I*0.193752,1.06089+I*0.221026,1.10832+I*0.254803,1.16047+I*0.297252," //
            + "1.21733+I*0.351426,1.27808+I*0.421604,1.34015+I*0.513624,1.39738+I*0.63483,1.437+I*0.79243," //
            + "1.43661+I*0.987709,1.36724+I*1.20422,1.21283+I*1.39928,0.996036+I*1.52334,0.768937+I*1.55932," //
            + "0.57283+I*1.52838,0.420978+I*1.46215}");

    // TODO improve see discussion: https://github.com/paulmasson/math/issues/6
    // check(
    // "EllipticPi(2.0, 0.5)", //
    // "-0.313545+I*(-1.8138)");

    check("EllipticPi(0,z,m)", //
        "EllipticF(z,m)");
    check("EllipticPi(n,Pi/2,n)", //
        "EllipticE(n)/(1-n)");
    check("EllipticPi(n,Pi/2,x)", //
        "EllipticPi(n,x)");
    check("EllipticPi(n,0)", //
        "Pi/(2*Sqrt(1-n))");
    check("EllipticPi(n,1)", //
        "Infinity/Sign(1-n)");
    check("EllipticPi(n,n)", //
        "EllipticE(n)/(1-n)");
    check("EllipticPi(0.4,0.6)", //
        "2.59092");
    check("EllipticPi(1/3, Pi/5, 0.3)", //
        "0.668735");
    // TODO improve see discussion: https://github.com/paulmasson/math/issues/6
    // check(
    // "Table(EllipticPi(x,0.5), {x,-2.0, 2.0, 1/4})", //
    //
    // "{1.0227,1.07184,1.12843,1.19454,1.27313,1.36859,1.48785,1.64253,1.85407,2.16762,2.70129,3.93061,ComplexInfinity,-0.592756+I*(-4.05578),-0.45672+I*(-2.7207),-0.371748+I*(-2.14612),-0.313545+I*(-1.8138)}");
  }

  @Test
  public void testEllipticTheta() {
    check("EllipticTheta(3,0.4,E^(Pi*I*1/3))", //
        "EllipticTheta(3,0.4,0.5+I*0.866025)");
    check("EllipticTheta(1,0,x)", //
        "0");
    check("EllipticTheta(1,x,0)", //
        "0");
    check("EllipticTheta(2,x,0)", //
        "0");
    check("EllipticTheta(3,x,0)", //
        "1");
    check("EllipticTheta(4,x,0)", //
        "1");
    check("EllipticTheta(1,Pi,1/2)", //
        "0");
    check("EllipticTheta({1, 2, 3, 4}, z, q)", //
        "{EllipticTheta(1,z,q),EllipticTheta(2,z,q),EllipticTheta(3,z,q),EllipticTheta(4,z,q)}");

    check("EllipticTheta(3, 0.4+I, 0.5 )", //
        "2.89461+I*(-6.54061)");
    check("EllipticTheta(1, 2., 1/3)", //
        "1.42788");
    check("EllipticTheta(2, 5.0, 0.5)", //
        "0.183328");
    check("EllipticTheta(3, 0.4, 0.5)", //
        "1.69015");
    check("EllipticTheta(3, 1/4, 1/3) // N", //
        "1.5984");
    check("EllipticTheta(1, 0, 0) // N", //
        "0.0");

    check("Table(EllipticTheta(4, x,0.5), {x,-2.0, 2.0, 1/4})", //
        "{1.63213,2.03256,2.1136,1.83522,1.33069,0.806366," //
            + "0.411527,0.189666,0.121124,0.189666,0.411527,0.806366," //
            + "1.33069,1.83522,2.1136,2.03256,1.63213}");

    check("Table(EllipticTheta(4, x), {x,-0.9, 0.9, 1/4})", //
        "{5.46055,2.70051,1.85173,1.30101,0.8002,0.329855,0.0396032,2.24177*10^-6}");
    check("Table(EllipticTheta(4, x*I), {x,-0.9, 0.9, 1/4})", //
        "{2.73027+I*2.73027,1.35904+I*1.34147,1.0512+I*0.800524,1.00101+I*0.3,1.0002+I*(-0.2),1.03001+I*(-0.700158),1.25976+I*(-1.22016),2.19833+I*(-2.19833)}");

    // TODO: improve for case "EllipticTheta: Unsupported elliptic nome"
    check("EllipticTheta(3, 0.4+I, 0.5+I)", //
        "EllipticTheta(3,0.4+I*1.0,0.5+I*1.0)");
  }

  @Test
  public void testEntropy() {
    // Shannon entropy
    check("Entropy({a, b, b})", //
        "2/3*Log(3/2)+Log(3)/3");
    check("Entropy({a, b, b,c,c,c,d})", //
        "3/7*Log(7/3)+2/7*Log(7/2)+2/7*Log(7)");

    check("Entropy(b,{a,c,c})", //
        "2/3*Log(3/2)/Log(b)+Log(3)/(3*Log(b))");
  }



  @Test
  public void testEuclideanDistance() {
    check("EuclideanDistance({-1, -1}, {1.0, 1})", //
        "2.82843");
    check("EuclideanDistance({a, b, c}, {x, y, z})", //
        "Sqrt(Abs(a-x)^2+Abs(b-y)^2+Abs(c-z)^2)");
    check("EuclideanDistance({-1, -1}, {1, 1})", //
        "2*Sqrt(2)");
    check("EuclideanDistance({a, b}, {c, d})", //
        "Sqrt(Abs(a-c)^2+Abs(b-d)^2)");
  }

  @Test
  public void testEulerE() {
    check("EulerE(n,1/2)", //
        "EulerE(n)/2^n");
    check("Table(EulerE(n,z), {n, 0, 15})", //
        "{1,-1/2+z,-z+z^2,1/4-3/2*z^2+z^3,z-2*z^3+z^4,-1/2+5/2*z^2-5/2*z^4+z^5,-3*z+5*z^3-\n" //
            + "3*z^5+z^6,17/8-21/2*z^2+35/4*z^4-7/2*z^6+z^7,17*z-28*z^3+14*z^5-4*z^7+z^8,-31/2+\n" //
            + "153/2*z^2-63*z^4+21*z^6-9/2*z^8+z^9,-155*z+255*z^3-126*z^5+30*z^7-5*z^9+z^10,691/\n" //
            + "4-1705/2*z^2+2805/4*z^4-231*z^6+165/4*z^8-11/2*z^10+z^11,2073*z-3410*z^3+1683*z^\n" //
            + "5-396*z^7+55*z^9-6*z^11+z^12,-5461/2+26949/2*z^2-22165/2*z^4+7293/2*z^6-1287/2*z^\n" //
            + "8+143/2*z^10-13/2*z^12+z^13,-38227*z+62881*z^3-31031*z^5+7293*z^7-1001*z^9+91*z^\n" //
            + "11-7*z^13+z^14,929569/16-573405/2*z^2+943215/4*z^4-155155/2*z^6+109395/8*z^8-\n" //
            + "3003/2*z^10+455/4*z^12-15/2*z^14+z^15}");
    check("EulerE(10,z)", //
        "-155*z+255*z^3-126*z^5+30*z^7-5*z^9+z^10");
    check("Table(EulerE(k), {k, 0, 15})", //
        "{1,0,-1,0,5,0,-61,0,1385,0,-50521,0,2702765,0,-199360981,0}");
  }

  @Test
  public void testEulerGamma() {
    check("N(EulerGamma,100)", //
        "0.5772156649015328606065120900824024310421593359399235988057672348848677267776646709369470632917467495");
    check("N(EulerGamma)", //
        "0.577216");
  }

  @Test
  public void testEulerPhi() {
    check("Refine(EulerPhi(p^n),Element(p, Primes)&&Element(n, Integers))", //
        "-1/p^(1-n)+p^n");

    check("EulerPhi(-a)", //
        "EulerPhi(a)");
    check("Table(EulerPhi(k), {k, 0, 20})", //
        "{0,1,1,2,2,4,2,6,4,6,4,10,4,12,6,8,8,16,6,18,8}");
    check("Table(EulerPhi(-k), {k, 0, 20})", //
        "{0,1,1,2,2,4,2,6,4,6,4,10,4,12,6,8,8,16,6,18,8}");
    check("EulerPhi(50!)", //
        "4218559200885839042679312107816703841788854953574400000000000000");
    check("Table(EulerPhi(10^k), {k, 0, 10})", //
        "{1,4,40,400,4000,40000,400000,4000000,40000000,400000000,4000000000}");
  }

  @Test
  public void testEvaluate() {
    check("Evaluate(1+1)", //
        "2");
    check("{f(2+2, 1+1, -1+2), f(Evaluate(2+2),Evaluate(1+1),-1+2,Evaluate(-1+2))}", //
        "{f(4,2,1),f(4,2,1,1)}");
    check(
        "SetAttributes(hr,HoldRest); {hr(2+2, 1+1, -1+2), hr(2+2,Evaluate(1+1),-1+2,Evaluate(-1+2))}", //
        "{hr(4,1+1,-1+2),hr(4,2,-1+2,1)}");
    check("SetAttributes(hf,HoldFirst); {hf(1+1), hf(Evaluate(1+1))}", //
        "{hf(1+1),hf(2)}");
    check("cheb = ChebyshevT(5, x);Function(x, Evaluate(cheb))", //
        "Function(x,5*x-20*x^3+16*x^5)");
    check("Function(x, Evaluate(cheb))[10]", //
        "1580050");
    check("Hold(Evaluate(1+1),2+2)", //
        "Hold(2,2+2)");
    check("Evaluate(a,b)", //
        "Identity(a,b)");
    check("x=Plus; {Attributes(x), Attributes(Evaluate(x))}", //
        "{{},{Flat,Listable,NumericFunction,OneIdentity,Orderless,Protected}}");
  }

  @Test
  public void testExactNumberQ() {
    check("ExactNumberQ(10)", //
        "True");
    check("ExactNumberQ(4.0)", //
        "False");
    check("ExactNumberQ(n)", //
        "False");
    check("ExactNumberQ(1+I)", //
        "True");
    check("ExactNumberQ(1 + 1. * I)", //
        "False");
  }

  @Test
  public void testExcept001() {
    check("Cases({x, a, b, x, c}, Except(x))", //
        "{a,b,c}");
    check("Cases({a, 0, b, 1, c, 2, 3}, Except(1, _Integer))", //
        "{0,2,3}");

    check("Cases({1, 0, 2, 0, 3}, (0|2))", //
        "{0,2,0}");
    check("Cases({1, 0, 2, 0, 3}, Except(0))", //
        "{1,2,3}");
    check("Cases({a, b, 0, 1, 2, x, y}, Except(_Integer))", //
        "{a,b,x,y}");
    check("Cases({a, b, 0, 1, 2, x, y}, Except(0, _Integer))", //
        "{1,2}");
    check("Cases({1, 1, -5, EulerGamma, r, I, 0, Pi, 1/2}, Except(_Integer))", //
        "{EulerGamma,r,I,Pi,1/2}");
  }

  @Test
  public void testExcept002() {
    // https://github.com/mathics/Mathics/issues/1405
    check("f(Except(_(___), x_?NumericQ)) := g(x)", //
        "");
    check("f(f(x_)) := x", //
        "");

    check("f(2)", //
        "g(2)");
    check("f(Pi)", //
        "g(Pi)");
    check("f(2*Pi)", //
        "f(2*Pi)");
    check("f(a)", //
        "f(a)");
    check("f(f(a))", //
        "a");
  }

  @Test
  public void testExp() {
    check("Sin(Pi/5)", //
        "Sqrt(5/8-Sqrt(5)/8)");
    check("Sinh(1/5*Pi*I)", //
        "I*Sqrt(5/8-Sqrt(5)/8)");
    check("ExpToTrig(Exp(I*Pi/5))", //
        "1/4+Sqrt(5)/4+I*Sqrt(5/8-Sqrt(5)/8)");

    check("Exp(Interval({-1, Log(2)}))", //
        "Interval({1/E,2})");
    check("Table(Exp(I*n*Pi/2), {n, 0, 4})", //
        "{1,I,-1,-I,1}");
    check("Exp(9/2*I*Pi)", //
        "I");

    check("\\[ExponentialE]", //
        "E");
    check("Exp(10.*^20)", //
        "Overflow()");

    check("Exp(x*Log(n))", //
        "n^x");
    check("Exp(42+Log(a)+Log(b))", //
        "a*b*E^42");
    check("Exp(1)", //
        "E");
    checkNumeric("Exp(10.0)", //
        "22026.465794806718");
    check("Exp(x) //FullForm", //
        "Power(E, x)");

    check("Exp(a+b)", //
        "E^(a+b)");
    check("E^(I*Pi)", //
        "-1");
    check("E^(2*I*Pi)", //
        "1");
    check("E^(2*I*Pi*3)", //
        "1");
    check("E^(5*I*Pi)", //
        "-1");
    check("E^Infinity", //
        "Infinity");
    check("E^(-Infinity)", //
        "0");
    check("E^(I*Infinity)", //
        "Indeterminate");
    check("E^(-I*Infinity)", //
        "Indeterminate");
    check("E^ComplexInfinity", //
        "Indeterminate");
    check("Conjugate(E^z)", //
        "E^Conjugate(z)");
  }

  @Test
  public void testExpApfloat() {
    check("e=N(E,50)", //
        "2.7182818284590452353602874713526624977572470936999");
    // Discussion https://github.com/mtommila/apfloat/discussions/51
    check("Power(e,1/2)", //
        "1.6487212707001281468486507878141635716537761007101");
    check("N(Power(e,1/2),50)", //
        "1.6487212707001281468486507878141635716537761007101");
  }

  @Test
  public void testExpand() {
    check("Expand((a(1)+a(2))*(x(1)+x(2))^2, x(_))", //
        "(a(1)+a(2))*x(1)^2+2*(a(1)+a(2))*x(1)*x(2)+(a(1)+a(2))*x(2)^2");

    check("Expand((a + b)^2*(1 + x)^2, x)", //
        "(a+b)^2+2*(a+b)^2*x+(a+b)^2*x^2");
    check("Expand((1 + x)^2 + (2 + x)^2, 1 + x)", //
        "1+2*x+x^2+(2+x)^2");

    // performance test
    // check("Expand((x + y + z + w)^15 * ((x + y + z + w)^15+w));", //
    // "?");
    // check("test = (x + y + z + w)^15;Length(Expand(test*(test+w)))", //
    // "6272");

    // check(
    //
    // "Expand((1+x+x^2)*(1-x+x^3-x^4+x^6-x^7+x^9-x^10+x^12-x^13+x^15-x^16+x^18-x^19+x^21-x^22+x^\n"
    // +
    // "24-x^25+x^27-x^28+x^30-x^31+x^33-x^34+x^36-x^37+x^39-x^40+x^42-x^43+x^45-x^46+x^\n"
    // +
    // "48-x^49+x^51-x^52+x^54-x^55+x^57-x^58+x^60-x^61+x^63-x^64+x^66-x^67+x^69-x^70+x^\n"
    // +
    // "72-x^73+x^75-x^76+x^78-x^79+x^81-x^82+x^84-x^85+x^87-x^88+x^90-x^91+x^93-x^94+x^\n"
    // +
    // "96-x^97+x^99-x^100+x^102-x^103+x^105-x^106+x^108-x^109+x^111-x^112+x^114-x^115+x^\n"
    // + "117-x^118+x^120-x^121+x^123-x^124+x^126-x^127+x^129-x^130+x^132-x^133+x^135-x^\n"
    // + "136+x^138-x^139+x^141-x^142+x^144-x^145+x^147-x^148+x^150-x^151+x^153-x^154+x^\n"
    // + "156-x^157+x^159-x^160+x^162-x^163+x^165-x^166+x^168-x^169+x^171-x^172+x^174-x^\n"
    // + "175+x^177-x^178+x^180-x^181+x^183-x^184+x^186-x^187+x^189-x^190+x^192-x^193+x^\n"
    // + "195-x^196+x^198-x^199+x^201-x^202+x^204-x^205+x^207-x^208+x^210-x^211+x^213-x^\n"
    // + "214+x^216-x^217+x^219-x^220+x^222-x^223+x^225-x^226+x^228-x^229+x^231-x^232+x^\n"
    // + "234-x^235+x^237-x^238+x^240-x^241+x^243-x^244+x^246-x^247+x^249-x^250+x^252-x^\n"
    // + "253+x^255-x^256+x^258-x^259+x^261-x^262+x^264-x^265+x^267-x^268+x^270-x^271+x^\n"
    // + "273-x^274+x^276-x^277+x^279-x^280+x^282-x^283+x^285-x^286+x^288-x^289+x^291-x^\n"
    // + "292+x^294-x^295+x^297-x^298+x^300-x^301+x^303-x^304+x^306-x^307+x^309-x^310+x^\n"
    // + "312-x^313+x^315-x^316+x^318-x^319+x^321-x^322+x^324-x^325+x^327-x^328+x^330-x^\n"
    // + "331+x^333-x^334+x^336-x^337+x^339-x^340+x^342-x^343+x^345-x^346+x^348-x^349+x^\n"
    // + "351-x^352+x^354-x^355+x^357-x^358+x^360-x^361+x^363-x^364+x^366-x^367+x^369-x^\n"
    // + "370+x^372-x^373+x^375-x^376+x^378-x^379+x^381-x^382+x^384-x^385+x^387-x^388+x^\n"
    // + "390-x^391+x^393-x^394+x^396-x^397+x^399-x^400+x^402-x^403+x^405-x^406+x^408-x^\n"
    // + "409+x^411-x^412+x^414-x^415+x^417-x^418+x^420-x^421+x^423-x^424+x^426-x^427+x^\n"
    // + "429-x^430+x^432-x^433+x^435-x^436+x^438-x^439+x^441-x^442+x^444-x^445+x^447-x^\n"
    // + "448+x^450-x^451+x^453-x^454+x^456-x^457+x^459-x^460+x^462-x^463+x^465-x^466+x^\n"
    // + "468-x^469+x^471-x^472+x^474-x^475+x^477-x^478+x^480-x^481+x^483-x^484+x^486-x^\n"
    // + "487+x^489-x^490+x^492-x^493+x^495-x^496+x^498-x^499+x^501-x^502+x^504-x^505+x^\n"
    // + "507-x^508+x^510-x^511+x^513-x^514+x^516-x^517+x^519-x^520+x^522-x^523+x^525-x^\n"
    // + "526+x^528-x^529+x^531-x^532+x^534-x^535+x^537-x^538+x^540-x^541+x^543-x^544+x^\n"
    // + "546-x^547+x^549-x^550+x^552-x^553+x^555-x^556+x^558-x^559+x^561-x^562+x^564-x^\n"
    // + "565+x^567-x^568+x^570-x^571+x^573-x^574+x^576-x^577+x^579-x^580+x^582-x^583+x^\n"
    // + "585-x^586+x^588-x^589+x^591-x^592+x^594-x^595+x^597-x^598+x^600-x^601+x^603-x^\n"
    // + "604+x^606-x^607+x^609-x^610+x^612-x^613+x^615-x^616+x^618-x^619+x^621-x^622+x^\n"
    // + "624-x^625+x^627-x^628+x^630-x^631+x^633-x^634+x^636-x^637+x^639-x^640+x^642-x^\n"
    // + "643+x^645-x^646+x^648-x^649+x^651-x^652+x^654-x^655+x^657-x^658+x^660-x^661+x^\n"
    // + "663-x^664+x^666-x^667+x^669-x^670+x^672-x^673+x^675-x^676+x^678-x^679+x^681-x^\n"
    // + "682+x^684-x^685+x^687-x^688+x^690-x^691+x^693-x^694+x^696-x^697+x^699-x^700+x^\n"
    // + "702-x^703+x^705-x^706+x^708-x^709+x^711-x^712+x^714-x^715+x^717-x^718+x^720-x^\n"
    // + "721+x^723-x^724+x^726-x^727+x^729-x^730+x^732-x^733+x^735-x^736+x^738-x^739+x^\n"
    // + "741-x^742+x^744-x^745+x^747-x^748+x^750-x^751+x^753-x^754+x^756-x^757+x^759-x^\n"
    // + "760+x^762-x^763+x^765-x^766+x^768-x^769+x^771-x^772+x^774-x^775+x^777-x^778+x^\n"
    // + "780-x^781+x^783-x^784+x^786-x^787+x^789-x^790+x^792-x^793+x^795-x^796+x^798-x^\n"
    // + "799+x^801-x^802+x^804-x^805+x^807-x^808+x^810-x^811+x^813-x^814+x^816-x^817+x^\n"
    // + "819-x^820+x^822-x^823+x^825-x^826+x^828-x^829+x^831-x^832+x^834-x^835+x^837-x^\n"
    // + "838+x^840-x^841+x^843-x^844+x^846-x^847+x^849-x^850+x^852-x^853+x^855-x^856+x^\n"
    // + "858-x^859+x^861-x^862+x^864-x^865+x^867-x^868+x^870-x^871+x^873-x^874+x^876-x^\n"
    // + "877+x^879-x^880+x^882-x^883+x^885-x^886+x^888-x^889+x^891-x^892+x^894-x^895+x^\n"
    // + "897-x^898+x^900-x^901+x^903-x^904+x^906-x^907+x^909-x^910+x^912-x^913+x^915-x^\n"
    // + "916+x^918-x^919+x^921-x^922+x^924-x^925+x^927-x^928+x^930-x^931+x^933-x^934+x^\n"
    // + "936-x^937+x^939-x^940+x^942-x^943+x^945-x^946+x^948-x^949+x^951-x^952+x^954-x^\n"
    // + "955+x^957-x^958+x^960-x^961+x^963-x^964+x^966-x^967+x^969-x^970+x^972-x^973+x^\n"
    // + "975-x^976+x^978-x^979+x^981-x^982+x^984-x^985+x^987-x^988+x^990-x^991+x^993-x^\n"
    // +
    // "994+x^996-x^997+x^999-x^1000+x^1002-x^1003+x^1005-x^1006+x^1008-x^1009+x^1011-x^\n"
    // + "1012+x^1014-x^1015+x^1017-x^1018+x^1020-x^1021+x^1023-x^1024+x^1026-x^1027+x^\n"
    // + "1029-x^1030+x^1032-x^1033+x^1035-x^1036+x^1038-x^1039+x^1041-x^1042+x^1044-x^\n"
    // + "1045+x^1047-x^1048+x^1050-x^1051+x^1053-x^1054+x^1056-x^1057+x^1059-x^1060+x^\n"
    // + "1062-x^1063+x^1065-x^1066+x^1068-x^1069+x^1071-x^1072+x^1074-x^1075+x^1077-x^\n"
    // + "1078+x^1080-x^1081+x^1083-x^1084+x^1086-x^1087+x^1089-x^1090+x^1092-x^1093+x^\n"
    // + "1095-x^1096+x^1098-x^1099+x^1101-x^1102+x^1104-x^1105+x^1107-x^1108+x^1110-x^\n"
    // + "1111+x^1113-x^1114+x^1116-x^1117+x^1119-x^1120+x^1122-x^1123+x^1125-x^1126+x^\n"
    // + "1128-x^1129+x^1131-x^1132+x^1134-x^1135+x^1137-x^1138+x^1140-x^1141+x^1143-x^\n"
    // + "1144+x^1146-x^1147+x^1149-x^1150+x^1152-x^1153+x^1155-x^1156+x^1158-x^1159+x^\n"
    // + "1161-x^1162+x^1164-x^1165+x^1167-x^1168+x^1170-x^1171+x^1173-x^1174+x^1176-x^\n"
    // + "1177+x^1179-x^1180+x^1182-x^1183+x^1185-x^1186+x^1188-x^1189+x^1191-x^1192+x^\n"
    // + "1194-x^1195+x^1197-x^1198+x^1200-x^1201+x^1203-x^1204+x^1206-x^1207+x^1209-x^\n"
    // + "1210+x^1212-x^1213+x^1215-x^1216+x^1218-x^1219+x^1221-x^1222+x^1224-x^1225+x^\n"
    // + "1227-x^1228+x^1229-x^1231+x^1232-x^1234+x^1235-x^1237+x^1238-x^1240+x^1241-x^\n"
    // + "1243+x^1244-x^1246+x^1247-x^1249+x^1250-x^1252+x^1253-x^1255+x^1256-x^1258+x^\n"
    // + "1259-x^1261+x^1262-x^1264+x^1265-x^1267+x^1268-x^1270+x^1271-x^1273+x^1274-x^\n"
    // + "1276+x^1277-x^1279+x^1280-x^1282+x^1283-x^1285+x^1286-x^1288+x^1289-x^1291+x^\n"
    // + "1292-x^1294+x^1295-x^1297+x^1298-x^1300+x^1301-x^1303+x^1304-x^1306+x^1307-x^\n"
    // + "1309+x^1310-x^1312+x^1313-x^1315+x^1316-x^1318+x^1319-x^1321+x^1322-x^1324+x^\n"
    // + "1325-x^1327+x^1328-x^1330+x^1331-x^1333+x^1334-x^1336+x^1337-x^1339+x^1340-x^\n"
    // + "1342+x^1343-x^1345+x^1346-x^1348+x^1349-x^1351+x^1352-x^1354+x^1355-x^1357+x^\n"
    // + "1358-x^1360+x^1361-x^1363+x^1364-x^1366+x^1367-x^1369+x^1370-x^1372+x^1373-x^\n"
    // + "1375+x^1376-x^1378+x^1379-x^1381+x^1382-x^1384+x^1385-x^1387+x^1388-x^1390+x^\n"
    // + "1391-x^1393+x^1394-x^1396+x^1397-x^1399+x^1400-x^1402+x^1403-x^1405+x^1406-x^\n"
    // + "1408+x^1409-x^1411+x^1412-x^1414+x^1415-x^1417+x^1418-x^1420+x^1421-x^1423+x^\n"
    // + "1424-x^1426+x^1427-x^1429+x^1430-x^1432+x^1433-x^1435+x^1436-x^1438+x^1439-x^\n"
    // + "1441+x^1442-x^1444+x^1445-x^1447+x^1448-x^1450+x^1451-x^1453+x^1454-x^1456+x^\n"
    // + "1457-x^1459+x^1460-x^1462+x^1463-x^1465+x^1466-x^1468+x^1469-x^1471+x^1472-x^\n"
    // + "1474+x^1475-x^1477+x^1478-x^1480+x^1481-x^1483+x^1484-x^1486+x^1487-x^1489+x^\n"
    // + "1490-x^1492+x^1493-x^1495+x^1496-x^1498+x^1499-x^1501+x^1502-x^1504+x^1505-x^\n"
    // + "1507+x^1508-x^1510+x^1511-x^1513+x^1514-x^1516+x^1517-x^1519+x^1520-x^1522+x^\n"
    // + "1523-x^1525+x^1526-x^1528+x^1529-x^1531+x^1532-x^1534+x^1535-x^1537+x^1538-x^\n"
    // + "1540+x^1541-x^1543+x^1544-x^1546+x^1547-x^1549+x^1550-x^1552+x^1553-x^1555+x^\n"
    // + "1556-x^1558+x^1559-x^1561+x^1562-x^1564+x^1565-x^1567+x^1568-x^1570+x^1571-x^\n"
    // + "1573+x^1574-x^1576+x^1577-x^1579+x^1580-x^1582+x^1583-x^1585+x^1586-x^1588+x^\n"
    // + "1589-x^1591+x^1592-x^1594+x^1595-x^1597+x^1598-x^1600+x^1601-x^1603+x^1604-x^\n"
    // + "1606+x^1607-x^1609+x^1610-x^1612+x^1613-x^1615+x^1616-x^1618+x^1619-x^1621+x^\n"
    // + "1622-x^1624+x^1625-x^1627+x^1628-x^1630+x^1631-x^1633+x^1634-x^1636+x^1637-x^\n"
    // + "1639+x^1640-x^1642+x^1643-x^1645+x^1646-x^1648+x^1649-x^1651+x^1652-x^1654+x^\n"
    // + "1655-x^1657+x^1658-x^1660+x^1661-x^1663+x^1664-x^1666+x^1667-x^1669+x^1670-x^\n"
    // + "1672+x^1673-x^1675+x^1676-x^1678+x^1679-x^1681+x^1682-x^1684+x^1685-x^1687+x^\n"
    // + "1688-x^1690+x^1691-x^1693+x^1694-x^1696+x^1697-x^1699+x^1700-x^1702+x^1703-x^\n"
    // + "1705+x^1706-x^1708+x^1709-x^1711+x^1712-x^1714+x^1715-x^1717+x^1718-x^1720+x^\n"
    // + "1721-x^1723+x^1724-x^1726+x^1727-x^1729+x^1730-x^1732+x^1733-x^1735+x^1736-x^\n"
    // + "1738+x^1739-x^1741+x^1742-x^1744+x^1745-x^1747+x^1748-x^1750+x^1751-x^1753+x^\n"
    // + "1754-x^1756+x^1757-x^1759+x^1760-x^1762+x^1763-x^1765+x^1766-x^1768+x^1769-x^\n"
    // + "1771+x^1772-x^1774+x^1775-x^1777+x^1778-x^1780+x^1781-x^1783+x^1784-x^1786+x^\n"
    // + "1787-x^1789+x^1790-x^1792+x^1793-x^1795+x^1796-x^1798+x^1799-x^1801+x^1802-x^\n"
    // + "1804+x^1805-x^1807+x^1808-x^1810+x^1811-x^1813+x^1814-x^1816+x^1817-x^1819+x^\n"
    // + "1820-x^1822+x^1823-x^1825+x^1826-x^1828+x^1829-x^1831+x^1832-x^1834+x^1835-x^\n"
    // + "1837+x^1838-x^1840+x^1841-x^1843+x^1844-x^1846+x^1847-x^1849+x^1850-x^1852+x^\n"
    // + "1853-x^1855+x^1856-x^1858+x^1859-x^1861+x^1862-x^1864+x^1865-x^1867+x^1868-x^\n"
    // + "1870+x^1871-x^1873+x^1874-x^1876+x^1877-x^1879+x^1880-x^1882+x^1883-x^1885+x^\n"
    // + "1886-x^1888+x^1889-x^1891+x^1892-x^1894+x^1895-x^1897+x^1898-x^1900+x^1901-x^\n"
    // + "1903+x^1904-x^1906+x^1907-x^1909+x^1910-x^1912+x^1913-x^1915+x^1916-x^1918+x^\n"
    // + "1919-x^1921+x^1922-x^1924+x^1925-x^1927+x^1928-x^1930+x^1931-x^1933+x^1934-x^\n"
    // + "1936+x^1937-x^1939+x^1940-x^1942+x^1943-x^1945+x^1946-x^1948+x^1949-x^1951+x^\n"
    // + "1952-x^1954+x^1955-x^1957+x^1958-x^1960+x^1961-x^1963+x^1964-x^1966+x^1967-x^\n"
    // + "1969+x^1970-x^1972+x^1973-x^1975+x^1976-x^1978+x^1979-x^1981+x^1982-x^1984+x^\n"
    // + "1985-x^1987+x^1988-x^1990+x^1991-x^1993+x^1994-x^1996+x^1997-x^1999+x^2000-x^\n"
    // + "2002+x^2003-x^2005+x^2006-x^2008+x^2009-x^2011+x^2012-x^2014+x^2015-x^2017+x^\n"
    // + "2018-x^2020+x^2021-x^2023+x^2024-x^2026+x^2027-x^2029+x^2030-x^2032+x^2033-x^\n"
    // + "2035+x^2036-x^2038+x^2039-x^2041+x^2042-x^2044+x^2045-x^2047+x^2048-x^2050+x^\n"
    // + "2051-x^2053+x^2054-x^2056+x^2057-x^2059+x^2060-x^2062+x^2063-x^2065+x^2066-x^\n"
    // + "2068+x^2069-x^2071+x^2072-x^2074+x^2075-x^2077+x^2078-x^2080+x^2081-x^2083+x^\n"
    // + "2084-x^2086+x^2087-x^2089+x^2090-x^2092+x^2093-x^2095+x^2096-x^2098+x^2099-x^\n"
    // + "2101+x^2102-x^2104+x^2105-x^2107+x^2108-x^2110+x^2111-x^2113+x^2114-x^2116+x^\n"
    // + "2117-x^2119+x^2120-x^2122+x^2123-x^2125+x^2126-x^2128+x^2129-x^2131+x^2132-x^\n"
    // + "2134+x^2135-x^2137+x^2138-x^2140+x^2141-x^2143+x^2144-x^2146+x^2147-x^2149+x^\n"
    // + "2150-x^2152+x^2153-x^2155+x^2156-x^2158+x^2159-x^2161+x^2162-x^2164+x^2165-x^\n"
    // + "2167+x^2168-x^2170+x^2171-x^2173+x^2174-x^2176+x^2177-x^2179+x^2180-x^2182+x^\n"
    // + "2183-x^2185+x^2186-x^2188+x^2189-x^2191+x^2192-x^2194+x^2195-x^2197+x^2198-x^\n"
    // + "2200+x^2201-x^2203+x^2204-x^2206+x^2207-x^2209+x^2210-x^2212+x^2213-x^2215+x^\n"
    // + "2216-x^2218+x^2219-x^2221+x^2222-x^2224+x^2225-x^2227+x^2228-x^2230+x^2231-x^\n"
    // + "2233+x^2234-x^2236+x^2237-x^2239+x^2240-x^2242+x^2243-x^2245+x^2246-x^2248+x^\n"
    // + "2249-x^2251+x^2252-x^2254+x^2255-x^2257+x^2258-x^2260+x^2261-x^2263+x^2264-x^\n"
    // + "2266+x^2267-x^2269+x^2270-x^2272+x^2273-x^2275+x^2276-x^2278+x^2279-x^2281+x^\n"
    // + "2282-x^2284+x^2285-x^2287+x^2288-x^2290+x^2291-x^2293+x^2294-x^2296+x^2297-x^\n"
    // + "2299+x^2300-x^2302+x^2303-x^2305+x^2306-x^2308+x^2309-x^2311+x^2312-x^2314+x^\n"
    // + "2315-x^2317+x^2318-x^2320+x^2321-x^2323+x^2324-x^2326+x^2327-x^2329+x^2330-x^\n"
    // + "2332+x^2333-x^2335+x^2336-x^2338+x^2339-x^2341+x^2342-x^2344+x^2345-x^2347+x^\n"
    // + "2348-x^2350+x^2351-x^2353+x^2354-x^2356+x^2357-x^2359+x^2360-x^2362+x^2363-x^\n"
    // + "2365+x^2366-x^2368+x^2369-x^2371+x^2372-x^2374+x^2375-x^2377+x^2378-x^2380+x^\n"
    // + "2381-x^2383+x^2384-x^2386+x^2387-x^2389+x^2390-x^2392+x^2393-x^2395+x^2396-x^\n"
    // + "2398+x^2399-x^2401+x^2402-x^2404+x^2405-x^2407+x^2408-x^2410+x^2411-x^2413+x^\n"
    // + "2414-x^2416+x^2417-x^2419+x^2420-x^2422+x^2423-x^2425+x^2426-x^2428+x^2429-x^\n"
    // + "2431+x^2432-x^2434+x^2435-x^2437+x^2438-x^2440+x^2441-x^2443+x^2444-x^2446+x^\n"
    // + "2447-x^2449+x^2450-x^2452+x^2453-x^2455+x^2456))", //
    // "1+x^1229+x^2458");
    check("Expand((x + 3)^(5/2)+(x + 1)^(3/2))", //
        "Sqrt(1+x)+x*Sqrt(1+x)+9*Sqrt(3+x)+6*x*Sqrt(3+x)+x^2*Sqrt(3+x)");
    check("Expand((x + 1)^(5/2))", //
        "Sqrt(1+x)+2*x*Sqrt(1+x)+x^2*Sqrt(1+x)");
    check("Expand((x + 1)^(-5/2))", //
        "1/(1+x)^(5/2)");

    check("Expand((x + y) ^ 3) ", //
        "x^3+3*x^2*y+3*x*y^2+y^3");
    check("Expand((a + b)*(a + c + d))", //
        "a^2+a*b+a*c+b*c+a*d+b*d");
    check("Expand((a + b)*(a + c + d)*(e + f) + e*a*a)", //
        "2*a^2*e+a*b*e+a*c*e+b*c*e+a*d*e+b*d*e+a^2*f+a*b*f+a*c*f+b*c*f+a*d*f+b*d*f");
    check("Expand((a + b) ^ 2 * (c + d))", //
        "a^2*c+2*a*b*c+b^2*c+a^2*d+2*a*b*d+b^2*d");
    check("Expand((x + y) ^ 2 + x*y)", //
        "x^2+3*x*y+y^2");
    check("Expand(((a + b)*(c + d)) ^ 2 + b (1 + a))", //
        "a^2*c^2+2*a*b*c^2+b^2*c^2+2*a^2*c*d+4*a*b*c*d+2*b^2*c*d+a^2*d^2+2*a*b*d^2+b^2*d^\n"
            + "2+b(1+a)");
    // TODO return {4 x + 4 y, 2 x + 2 y -> 4 x + 4 y}
    check("Expand({4*(x + y), 2*(x + y) -> 4*(x + y)})", //
        "{4*x+4*y,2*(x+y)->4*(x+y)}");
    check("Expand(Sin(x*(1 + y)))", //
        "Sin(x*(1+y))");
    check("a*(b*(c+d)+e) // Expand ", //
        "a*b*c+a*b*d+a*e");
    check("(y^2)^(1/2)/(2*x+2*y)//Expand", //
        "Sqrt(y^2)/(2*x+2*y)");

    check("2*(3+2*x)^2/(5+x^2+3*x)^3 // Expand ", //
        "18/(5+3*x+x^2)^3+(24*x)/(5+3*x+x^2)^3+(8*x^2)/(5+3*x+x^2)^3");

    check("Expand({x*(1+x)})", //
        "{x+x^2}");
    check("Expand((-g^2+4*f*h)*h)", //
        "-g^2*h+4*f*h^2");
    check("expand((1 + x)^10)", //
        "1+10*x+45*x^2+120*x^3+210*x^4+252*x^5+210*x^6+120*x^7+45*x^8+10*x^9+x^10");
    check("expand((1 + x + y)*(2 - x)^3)", //
        "8-4*x-6*x^2+5*x^3-x^4+8*y-12*x*y+6*x^2*y-x^3*y");
    check("expand((x + y)/z)", //
        "x/z+y/z");
    check("expand((x^s + y^s)^4)", //
        "x^(4*s)+4*x^(3*s)*y^s+6*x^(2*s)*y^(2*s)+4*x^s*y^(3*s)+y^(4*s)");

    check("Expand((1 + x)*(2 + x)*(3 + x))", //
        "6+11*x+6*x^2+x^3");
    check("Distribute((1 + x)*(2 + x)*(3 + x))", //
        "6+11*x+6*x^2+x^3");

    check("expand(2*(x + y)^2*Sin(x))", //
        "2*x^2*Sin(x)+4*x*y*Sin(x)+2*y^2*Sin(x)");
    check("expand(4*(a+b)*(c+d)*(f+g)^(-2))", //
        "(4*a*c)/(f+g)^2+(4*b*c)/(f+g)^2+(4*a*d)/(f+g)^2+(4*b*d)/(f+g)^2");
  }

  @Test
  public void testExpandAll() {
    // IExpr[] temp=
    // Apart.getFractionalPartsTimes(F.Times(F.Plus(F.c,F.b),F.Power(F.a,F.CN1),F.b),
    // true);
    // issue#122
    // check("ExpandAll(( ( ( X3 - X1$c) * ( ( X1 + ( ( X4$c * X3 ) + X5$c))
    // + X3$b)) * ( ( X3 - X1 ) + ( X3$c + X5 ))))",
    // "");
    check("ExpandAll(a+f(Log((1+x)^3)))", //
        "a+f(Log(1+3*x+3*x^2+x^3))");
    check("ExpandAll(Sum(9*x,{x,x,2*x}))", //
        "27/2*x+27/2*x^2");
    // github #113 - endless recursion
    check("ExpandAll(Sum(9*x,{x,x,x}))", //
        "9*x");
    // github #111 - loss of precision if you expand the expression
    check("t=ExpandAll((Pi*E-9)^13)", //
        "-2541865828329+3671583974253*E*Pi-2447722649502*E^2*Pi^2+997220338686*E^3*Pi^3-\n"
            + "277005649635*E^4*Pi^4+55401129927*E^5*Pi^5-8207574804*E^6*Pi^6+911952756*E^7*Pi^\n"
            + "7-75996063*E^8*Pi^8+4691115*E^9*Pi^9-208494*E^10*Pi^10+6318*E^11*Pi^11-117*E^12*Pi^\n"
            + "12+E^13*Pi^13");
    check("N(t)", //
        "-0.0236816");
    // shorten the result because of failing bitbucket pipeline
    check("N(t, 30)", //
        "-0.0000416<<SHORT>>", 10);

    check("N((Pi*E-9)^13)", //
        "-0.0000416019");
    check(
        "ExpandAll(( ( ( X3 - X1_c) * ( ( X1 + ( ( X4_c * X3 ) + X5_c)) + X3_b)) * ( ( X3 - X1 ) + ( X3_c + X5 ))))", //
        "-x1^2*x3+x1*x3^2+x1*x3*x5+x1^2*x1_c-x1*x3*x1_c-x1*x5*x1_c-x1*x3*x3_b+x3^2*x3_b+x3*x5*x3_b+x1*x1_c*x3_b-x3*x1_c*x3_b-x5*x1_c*x3_b+x1*x3*x3_c-x1*x1_c*x3_c+x3*x3_b*x3_c-x1_c*x3_b*x3_c-x1*x3^\n"
            + "2*x4_c+x3^3*x4_c+x3^2*x5*x4_c+x1*x3*x1_c*x4_c-x3^2*x1_c*x4_c-x3*x5*x1_c*x4_c+x3^\n"
            + "2*x3_c*x4_c-x3*x1_c*x3_c*x4_c-x1*x3*x5_c+x3^2*x5_c+x3*x5*x5_c+x1*x1_c*x5_c-x3*x1_c*x5_c-x5*x1_c*x5_c+x3*x3_c*x5_c-x1_c*x3_c*x5_c");

    check("ExpandAll(1/(1 + x)^3 + Sin((1 + x)^3))", //
        "1/(1+3*x+3*x^2+x^3)+Sin(1+3*x+3*x^2+x^3)");
    check("Expand(1/(1 + x)^3 + Sin((1 + x)^3))", //
        "1/(1+x)^3+Sin((1+x)^3)");

    check("ExpandAll(2*x*(x^2-x+1)^(-1))", //
        "(2*x)/(1-x+x^2)");
    check("ExpandAll((2+x)*(x^2-x+1)^(-1))", //
        "2/(1-x+x^2)+x/(1-x+x^2)");
    check("ExpandAll(2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1))", //
        "(10*x^3)/(2+3*x^2)+(-8*x^4)/(2+3*x^2)+(4*x^6)/(2+3*x^2)");
    check("ExpandAll((b+c)*((b+c)*(a)^(-1)+1))", //
        "b+b^2/a+c+(2*b*c)/a+c^2/a");
    check("ExpandAll((-2*x^3+4*x-5)*((-2*x^3+4*x-5)*(a)^(-1)-2*x))", //
        "25/a+10*x+(-40*x)/a-8*x^2+(16*x^2)/a+(20*x^3)/a+4*x^4+(-16*x^4)/a+(4*x^6)/a");
    check("ExpandAll((-(-2*x^3+4*x-5)*(-(-2*x^3+4*x-5)*(3*x^2+2)^(-1)-2*x)*(3*x^2+2)^(-1)+x^2-2))", //
        "-2+x^2+(-10*x)/(2+3*x^2)+(8*x^2)/(2+3*x^2)+(-4*x^4)/(2+3*x^2)+25/(4+12*x^2+9*x^4)+(-\n"
            + "40*x)/(4+12*x^2+9*x^4)+(16*x^2)/(4+12*x^2+9*x^4)+(20*x^3)/(4+12*x^2+9*x^4)+(-16*x^\n"
            + "4)/(4+12*x^2+9*x^4)+(4*x^6)/(4+12*x^2+9*x^4)");
    check("ExpandAll(Sqrt((1 + x)^2))", //
        "Sqrt(1+2*x+x^2)");

    // TODO return a ^ 2 / (c ^ 2 + 2 c d + d ^ 2) + 2 a b / (c ^ 2 + 2 c d
    // + d ^ 2) + b ^ 2 / (c ^ 2 + 2 c d + d ^ 2)
    check("ExpandAll((a + b) ^ 2 / (c + d)^2)", //
        "a^2/(c^2+2*c*d+d^2)+(2*a*b)/(c^2+2*c*d+d^2)+b^2/(c^2+2*c*d+d^2)");
    check("ExpandAll((a + Sin(x*(1 + y)))^2)", //
        "a^2+2*a*Sin(x+x*y)+Sin(x+x*y)^2");
    check("ExpandAll(((1 + x)*(1 + y))[x])", //
        "(1+x+y+x*y)[x]");
  }

  @Test
  public void testExponent() {
    check("Exponent(1 + x^(-2) + a*x^(-42), x, List)", //
        "{-42,-2,0}");
    check("Exponent(c*x^(-2)+a+b*x,x,-2)", //
        "-2[-2,0,1]");
    check("Exponent(7*y^w, y, List)", //
        "{w}");

    check("Exponent((2*x )^7*y+5*x^3,x^3)", //
        "7/3");
    check("Exponent(2*x *y+5*x^3,x^3)", //
        "1");
    check("Exponent(2*x*y+5*x^3,2*x)", //
        "1");
    check("Exponent(Cos(x*y), Cos(x*y))", //
        "1");
    check("Exponent(x^3,x^2)", //
        "3/2");
    check("Exponent(x^a,x^2)", //
        "a/2");
    check("Exponent(x^a,x^(2/3))", //
        "3/2*a");
    check("Exponent(2*x^a,x^(2/3))", //
        "3/2*a");
    check("Exponent(2*x^a,f(x))", //
        "0");

    check("Exponent((1+2*x)/Sqrt(3),x,List)", //
        "{0,1}");
    check("Exponent(Together((1+2*x)/Sqrt(3)),x,List)", //
        "{0,1}");
    check("Exponent((1+2*x)/Sqrt(3),x,List)", //
        "{0,1}");
    check("Exponent(a+b*x,x,List)", //
        "{0,1}");
    check("Exponent(SeriesData(x, 0, {1, 1, 0, 1, 1, 0, 1, 1}, 0, 9, 1), x)", //
        "7");
    check("Exponent(x*y,y,List)", //
        "{1}");
    check("Exponent(Sin(x*y),y)", //
        "0");

    check("Exponent(f(x^2),x)", //
        "0");
    check("Exponent(f(x^2),x,List)", //
        "{0}");
    check("Exponent(x*(b+a),x)", //
        "1");
    check("Exponent(x*(b+a),{a,b,x})", //
        "{1,1,1}");
    check("Exponent(x*(b+a),x,List)", //
        "{1}");
    check("Exponent(0, x)", //
        "-Infinity");
    check("Exponent(2, x)", //
        "0");
    check("Exponent(2*x, x)", //
        "1");
    check("Exponent(x, x)", //
        "1");
    check("Exponent(x^3, x)", //
        "3");
    check("Exponent(a*x^(-1), x)", //
        "-1");
    check("Exponent(x^(-3), x)", //
        "-3");
    check("Exponent(x^(-3)+x^(-2), x)", //
        "-2");
    check("Exponent(x+42, x)", //
        "1");
    check("Exponent(1 + x^2 + a*x^3, x)", //
        "3");
    check("Exponent((x^2 + 1)^3 + 1, x)", //
        "6");
    check("Exponent(x^(n0 + 1) + 2*Sqrt(x) + 1, x)", //
        "Max(1/2,1+n0)");
    check("Exponent((x^2 + 1)^3 - 1, x, Min)", //
        "2");
    check("Exponent((x^2 + 1)^3 + 1, x)", //
        "6");
    check("Exponent(1 + x^2 + a*x^3, x, List)", //
        "{0,2,3}");
    check("Exponent((a+b)/c, c)", //
        "-1");
    check("Exponent(a/c+b/c, c)", //
        "-1");
  }

  @Test
  public void testExpToTrig() {
    check("TrigToExp(Sin(x))", //
        "(I*1/2)/E^(I*x)-I*1/2*E^(I*x)");
    check("ExpToTrig((I*1/2)/E^(I*x))", //
        "I*1/2*Cos(x)+Sin(x)/2");
    check("ExpToTrig(TrigToExp(Sin(x)))", //
        "Sin(x)");

    check("ExpToTrig((-1)^(1/3))", //
        "1/2+I*1/2*Sqrt(3)");
    check("ExpToTrig(Sqrt(I))", //
        "(1+I)/Sqrt(2)");

    check("Log(-17)", //
        "I*Pi+Log(17)");
    check("Cosh(a+b) // TrigExpand", //
        "Cosh(a)*Cosh(b)+Sinh(a)*Sinh(b)");

    check("ExpToTrig((-17)^a)", //
        "(Cos(a*Pi)+I*Sin(a*Pi))*(Cosh(a*Log(17))+Sinh(a*Log(17)))");
    check("ExpToTrig((-17/19)^a)", //
        "(Cos(a*Pi)+I*Sin(a*Pi))*(Cosh(a*Log(19/17))-Sinh(a*Log(19/17)))");
    check("ExpToTrig(I^a)", //
        "Cos(1/2*a*Pi)+I*Sin(1/2*a*Pi)");
    check("ExpToTrig(Exp(x) - Exp(-x))", //
        "2*Sinh(x)");
    check("ExpToTrig(Exp(x) - Exp(-x))", //
        "2*Sinh(x)");

    check("ExpToTrig(Exp(I*x) == -1)", //
        "Cos(x)+I*Sin(x)==-1");
    check("ExpToTrig(Exp(x)-Exp(-x))", //
        "2*Sinh(x)");
    check("ExpToTrig(E^(I*x))", //
        "Cos(x)+I*Sin(x)");
    check("ExpToTrig(E^(c*x))", //
        "Cosh(c*x)+Sinh(c*x)");
  }

  @Test
  public void testExtendedGCD() {
    check("ExtendedGCD(2,3)", //
        "{1,{-1,1}}");
    check("ExtendedGCD(550,420,3515)", //
        "{5,{-4563,5967,1}}");
    check("ExtendedGCD(6,15,30)", //
        "{3,{-2,1,0}}");
    check("ExtendedGCD(3,{5,15})", //
        "{{1,{2,-1}},{3,{1,0}}}");
    check("ExtendedGCD(6,21)", //
        "{3,{-3,1}}");
    check("GCD(6,21)", //
        "3");

    check("ExtendedGCD(10, 15)", //
        "{5,{-1,1}}");
    check("ExtendedGCD(10, 15, 7)", //
        "{1,{-3,3,-2}}");

    check("$numbers = {10, 20, 14};", //
        "");
    check("{$gcd, $factors} = ExtendedGCD(Sequence @@ $numbers)", //
        "{2,{3,0,-2}}");
    check("Plus @@ ($numbers * $factors)", //
        "2");
  }



  @Test
  public void testExtract() {
    // Extract: Position specification {{rr},{3}} in Extract(a+b+c,{{rr},{3}}) is not applicable.
    check("Extract(a+b+c,{{rr},{3}})", //
        "Extract(a+b+c,{{rr},{3}})");

    check("Extract(a+b+c,{{2},{3}},Hold)", //
        "{Hold(b),Hold(c)}");

    check("Extract(<|test->f(a,b,c)|>,{Key(test),2})", //
        "b");
    check("Extract(a+b+c,{})", //
        "{}");
    check("Extract(a+b+c,{{}})", //
        "{a+b+c}");
    check("Extract(2)[Infinity]", //
        "Extract(2)[Infinity]");
    check("Extract(2)[{a, b, c, d}]", //
        "b");
    check("Extract(a+b+c,2)", //
        "b");
    check("Extract(a+b+c,0)", //
        "Plus");
    check("Extract(a+b+c,-4)", //
        "Extract(a+b+c,-4)");
    check("Extract(a+b+c, {2})", //
        "b");
    check("Extract(a+b+c,{{2},{3}})", //
        "{b,c}");
    check("Extract({{a, b}, {c, d}}, {{1}, {2, 2}})", //
        "{{a,b},d}");
  }

  @Test
  public void testFactor() {
    // TODO message is printed to many times
    // print message "rvalue" Increment: y is not a variable with a value, so its value cannot be
    // changed.
    check("Factor(a*x+b*x+a*y++b*y)", //
        "a*x+b*x+a*b*y*y++");

    // for (int i = 0; i < 2_000_000; i++) {
    // check(
    // "Factor(a*Cosh(x) + I*b*Cosh(x) - I*a*Sinh(x) + b*Sinh(x))", //
    // " (-I*a+b)*(I*Cosh(x)+Sinh(x))");
    // }

    // flaky test - depends on random generator:
    // check("Factor(2*y^6 - x*y^3 - 3*x^2)", //
    // "(x+y^3)*(-3*x+2*y^3)");


    // doc examples
    check("Factor({x + x^2, 2*x + 2*y + 2})", //
        "{x*(1+x),2*(1+x+y)}");
    check("Factor(x*a == x*b+x*c)", //
        "a*x==(b+c)*x");
    check("Factor(x^3 + 3*x^2*y + 3*x*y^2 + y^3) ", //
        "(x+y)^3");

    check("a == d*b + d*c // Factor", //
        "a==(b+c)*d");
    // Use heuristic?
    check("Factor(x^34 + x^17 + 1)", //
        "(1+x+x^2)*(1-x+x^3-x^4+x^6-x^7+x^9-x^10+x^12-x^13+x^15-x^16+x^17-x^19+x^20-x^22+x^\n"
            + "23-x^25+x^26-x^28+x^29-x^31+x^32)");

    System.out.println();

    if (Config.EXPENSIVE_JUNIT_TESTS) {
      // "Computer Algebra - Concepts and Techniques" p.200 E.Lamagna
      check("Factor(x^2458 + x^1229 + 1)", //
          "(1+x+x^2)*(1-x+x^3-x^4+x^6-x^7+x^9-x^10+x^12-x^13+x^15-x^16+x^18-x^19+x^21-x^22+x^\n"
              + "24-x^25+x^27-x^28+x^30-x^31+x^33-x^34+x^36-x^37+x^39-x^40+x^42-x^43+x^45-x^46+x^\n"
              + "48-x^49+x^51-x^52+x^54-x^55+x^57-x^58+x^60-x^61+x^63-x^64+x^66-x^67+x^69-x^70+x^\n"
              + "72-x^73+x^75-x^76+x^78-x^79+x^81-x^82+x^84-x^85+x^87-x^88+x^90-x^91+x^93-x^94+x^\n"
              + "96-x^97+x^99-x^100+x^102-x^103+x^105-x^106+x^108-x^109+x^111-x^112+x^114-x^115+x^\n"
              + "117-x^118+x^120-x^121+x^123-x^124+x^126-x^127+x^129-x^130+x^132-x^133+x^135-x^\n"
              + "136+x^138-x^139+x^141-x^142+x^144-x^145+x^147-x^148+x^150-x^151+x^153-x^154+x^\n"
              + "156-x^157+x^159-x^160+x^162-x^163+x^165-x^166+x^168-x^169+x^171-x^172+x^174-x^\n"
              + "175+x^177-x^178+x^180-x^181+x^183-x^184+x^186-x^187+x^189-x^190+x^192-x^193+x^\n"
              + "195-x^196+x^198-x^199+x^201-x^202+x^204-x^205+x^207-x^208+x^210-x^211+x^213-x^\n"
              + "214+x^216-x^217+x^219-x^220+x^222-x^223+x^225-x^226+x^228-x^229+x^231-x^232+x^\n"
              + "234-x^235+x^237-x^238+x^240-x^241+x^243-x^244+x^246-x^247+x^249-x^250+x^252-x^\n"
              + "253+x^255-x^256+x^258-x^259+x^261-x^262+x^264-x^265+x^267-x^268+x^270-x^271+x^\n"
              + "273-x^274+x^276-x^277+x^279-x^280+x^282-x^283+x^285-x^286+x^288-x^289+x^291-x^\n"
              + "292+x^294-x^295+x^297-x^298+x^300-x^301+x^303-x^304+x^306-x^307+x^309-x^310+x^\n"
              + "312-x^313+x^315-x^316+x^318-x^319+x^321-x^322+x^324-x^325+x^327-x^328+x^330-x^\n"
              + "331+x^333-x^334+x^336-x^337+x^339-x^340+x^342-x^343+x^345-x^346+x^348-x^349+x^\n"
              + "351-x^352+x^354-x^355+x^357-x^358+x^360-x^361+x^363-x^364+x^366-x^367+x^369-x^\n"
              + "370+x^372-x^373+x^375-x^376+x^378-x^379+x^381-x^382+x^384-x^385+x^387-x^388+x^\n"
              + "390-x^391+x^393-x^394+x^396-x^397+x^399-x^400+x^402-x^403+x^405-x^406+x^408-x^\n"
              + "409+x^411-x^412+x^414-x^415+x^417-x^418+x^420-x^421+x^423-x^424+x^426-x^427+x^\n"
              + "429-x^430+x^432-x^433+x^435-x^436+x^438-x^439+x^441-x^442+x^444-x^445+x^447-x^\n"
              + "448+x^450-x^451+x^453-x^454+x^456-x^457+x^459-x^460+x^462-x^463+x^465-x^466+x^\n"
              + "468-x^469+x^471-x^472+x^474-x^475+x^477-x^478+x^480-x^481+x^483-x^484+x^486-x^\n"
              + "487+x^489-x^490+x^492-x^493+x^495-x^496+x^498-x^499+x^501-x^502+x^504-x^505+x^\n"
              + "507-x^508+x^510-x^511+x^513-x^514+x^516-x^517+x^519-x^520+x^522-x^523+x^525-x^\n"
              + "526+x^528-x^529+x^531-x^532+x^534-x^535+x^537-x^538+x^540-x^541+x^543-x^544+x^\n"
              + "546-x^547+x^549-x^550+x^552-x^553+x^555-x^556+x^558-x^559+x^561-x^562+x^564-x^\n"
              + "565+x^567-x^568+x^570-x^571+x^573-x^574+x^576-x^577+x^579-x^580+x^582-x^583+x^\n"
              + "585-x^586+x^588-x^589+x^591-x^592+x^594-x^595+x^597-x^598+x^600-x^601+x^603-x^\n"
              + "604+x^606-x^607+x^609-x^610+x^612-x^613+x^615-x^616+x^618-x^619+x^621-x^622+x^\n"
              + "624-x^625+x^627-x^628+x^630-x^631+x^633-x^634+x^636-x^637+x^639-x^640+x^642-x^\n"
              + "643+x^645-x^646+x^648-x^649+x^651-x^652+x^654-x^655+x^657-x^658+x^660-x^661+x^\n"
              + "663-x^664+x^666-x^667+x^669-x^670+x^672-x^673+x^675-x^676+x^678-x^679+x^681-x^\n"
              + "682+x^684-x^685+x^687-x^688+x^690-x^691+x^693-x^694+x^696-x^697+x^699-x^700+x^\n"
              + "702-x^703+x^705-x^706+x^708-x^709+x^711-x^712+x^714-x^715+x^717-x^718+x^720-x^\n"
              + "721+x^723-x^724+x^726-x^727+x^729-x^730+x^732-x^733+x^735-x^736+x^738-x^739+x^\n"
              + "741-x^742+x^744-x^745+x^747-x^748+x^750-x^751+x^753-x^754+x^756-x^757+x^759-x^\n"
              + "760+x^762-x^763+x^765-x^766+x^768-x^769+x^771-x^772+x^774-x^775+x^777-x^778+x^\n"
              + "780-x^781+x^783-x^784+x^786-x^787+x^789-x^790+x^792-x^793+x^795-x^796+x^798-x^\n"
              + "799+x^801-x^802+x^804-x^805+x^807-x^808+x^810-x^811+x^813-x^814+x^816-x^817+x^\n"
              + "819-x^820+x^822-x^823+x^825-x^826+x^828-x^829+x^831-x^832+x^834-x^835+x^837-x^\n"
              + "838+x^840-x^841+x^843-x^844+x^846-x^847+x^849-x^850+x^852-x^853+x^855-x^856+x^\n"
              + "858-x^859+x^861-x^862+x^864-x^865+x^867-x^868+x^870-x^871+x^873-x^874+x^876-x^\n"
              + "877+x^879-x^880+x^882-x^883+x^885-x^886+x^888-x^889+x^891-x^892+x^894-x^895+x^\n"
              + "897-x^898+x^900-x^901+x^903-x^904+x^906-x^907+x^909-x^910+x^912-x^913+x^915-x^\n"
              + "916+x^918-x^919+x^921-x^922+x^924-x^925+x^927-x^928+x^930-x^931+x^933-x^934+x^\n"
              + "936-x^937+x^939-x^940+x^942-x^943+x^945-x^946+x^948-x^949+x^951-x^952+x^954-x^\n"
              + "955+x^957-x^958+x^960-x^961+x^963-x^964+x^966-x^967+x^969-x^970+x^972-x^973+x^\n"
              + "975-x^976+x^978-x^979+x^981-x^982+x^984-x^985+x^987-x^988+x^990-x^991+x^993-x^\n"
              + "994+x^996-x^997+x^999-x^1000+x^1002-x^1003+x^1005-x^1006+x^1008-x^1009+x^1011-x^\n"
              + "1012+x^1014-x^1015+x^1017-x^1018+x^1020-x^1021+x^1023-x^1024+x^1026-x^1027+x^\n"
              + "1029-x^1030+x^1032-x^1033+x^1035-x^1036+x^1038-x^1039+x^1041-x^1042+x^1044-x^\n"
              + "1045+x^1047-x^1048+x^1050-x^1051+x^1053-x^1054+x^1056-x^1057+x^1059-x^1060+x^\n"
              + "1062-x^1063+x^1065-x^1066+x^1068-x^1069+x^1071-x^1072+x^1074-x^1075+x^1077-x^\n"
              + "1078+x^1080-x^1081+x^1083-x^1084+x^1086-x^1087+x^1089-x^1090+x^1092-x^1093+x^\n"
              + "1095-x^1096+x^1098-x^1099+x^1101-x^1102+x^1104-x^1105+x^1107-x^1108+x^1110-x^\n"
              + "1111+x^1113-x^1114+x^1116-x^1117+x^1119-x^1120+x^1122-x^1123+x^1125-x^1126+x^\n"
              + "1128-x^1129+x^1131-x^1132+x^1134-x^1135+x^1137-x^1138+x^1140-x^1141+x^1143-x^\n"
              + "1144+x^1146-x^1147+x^1149-x^1150+x^1152-x^1153+x^1155-x^1156+x^1158-x^1159+x^\n"
              + "1161-x^1162+x^1164-x^1165+x^1167-x^1168+x^1170-x^1171+x^1173-x^1174+x^1176-x^\n"
              + "1177+x^1179-x^1180+x^1182-x^1183+x^1185-x^1186+x^1188-x^1189+x^1191-x^1192+x^\n"
              + "1194-x^1195+x^1197-x^1198+x^1200-x^1201+x^1203-x^1204+x^1206-x^1207+x^1209-x^\n"
              + "1210+x^1212-x^1213+x^1215-x^1216+x^1218-x^1219+x^1221-x^1222+x^1224-x^1225+x^\n"
              + "1227-x^1228+x^1229-x^1231+x^1232-x^1234+x^1235-x^1237+x^1238-x^1240+x^1241-x^\n"
              + "1243+x^1244-x^1246+x^1247-x^1249+x^1250-x^1252+x^1253-x^1255+x^1256-x^1258+x^\n"
              + "1259-x^1261+x^1262-x^1264+x^1265-x^1267+x^1268-x^1270+x^1271-x^1273+x^1274-x^\n"
              + "1276+x^1277-x^1279+x^1280-x^1282+x^1283-x^1285+x^1286-x^1288+x^1289-x^1291+x^\n"
              + "1292-x^1294+x^1295-x^1297+x^1298-x^1300+x^1301-x^1303+x^1304-x^1306+x^1307-x^\n"
              + "1309+x^1310-x^1312+x^1313-x^1315+x^1316-x^1318+x^1319-x^1321+x^1322-x^1324+x^\n"
              + "1325-x^1327+x^1328-x^1330+x^1331-x^1333+x^1334-x^1336+x^1337-x^1339+x^1340-x^\n"
              + "1342+x^1343-x^1345+x^1346-x^1348+x^1349-x^1351+x^1352-x^1354+x^1355-x^1357+x^\n"
              + "1358-x^1360+x^1361-x^1363+x^1364-x^1366+x^1367-x^1369+x^1370-x^1372+x^1373-x^\n"
              + "1375+x^1376-x^1378+x^1379-x^1381+x^1382-x^1384+x^1385-x^1387+x^1388-x^1390+x^\n"
              + "1391-x^1393+x^1394-x^1396+x^1397-x^1399+x^1400-x^1402+x^1403-x^1405+x^1406-x^\n"
              + "1408+x^1409-x^1411+x^1412-x^1414+x^1415-x^1417+x^1418-x^1420+x^1421-x^1423+x^\n"
              + "1424-x^1426+x^1427-x^1429+x^1430-x^1432+x^1433-x^1435+x^1436-x^1438+x^1439-x^\n"
              + "1441+x^1442-x^1444+x^1445-x^1447+x^1448-x^1450+x^1451-x^1453+x^1454-x^1456+x^\n"
              + "1457-x^1459+x^1460-x^1462+x^1463-x^1465+x^1466-x^1468+x^1469-x^1471+x^1472-x^\n"
              + "1474+x^1475-x^1477+x^1478-x^1480+x^1481-x^1483+x^1484-x^1486+x^1487-x^1489+x^\n"
              + "1490-x^1492+x^1493-x^1495+x^1496-x^1498+x^1499-x^1501+x^1502-x^1504+x^1505-x^\n"
              + "1507+x^1508-x^1510+x^1511-x^1513+x^1514-x^1516+x^1517-x^1519+x^1520-x^1522+x^\n"
              + "1523-x^1525+x^1526-x^1528+x^1529-x^1531+x^1532-x^1534+x^1535-x^1537+x^1538-x^\n"
              + "1540+x^1541-x^1543+x^1544-x^1546+x^1547-x^1549+x^1550-x^1552+x^1553-x^1555+x^\n"
              + "1556-x^1558+x^1559-x^1561+x^1562-x^1564+x^1565-x^1567+x^1568-x^1570+x^1571-x^\n"
              + "1573+x^1574-x^1576+x^1577-x^1579+x^1580-x^1582+x^1583-x^1585+x^1586-x^1588+x^\n"
              + "1589-x^1591+x^1592-x^1594+x^1595-x^1597+x^1598-x^1600+x^1601-x^1603+x^1604-x^\n"
              + "1606+x^1607-x^1609+x^1610-x^1612+x^1613-x^1615+x^1616-x^1618+x^1619-x^1621+x^\n"
              + "1622-x^1624+x^1625-x^1627+x^1628-x^1630+x^1631-x^1633+x^1634-x^1636+x^1637-x^\n"
              + "1639+x^1640-x^1642+x^1643-x^1645+x^1646-x^1648+x^1649-x^1651+x^1652-x^1654+x^\n"
              + "1655-x^1657+x^1658-x^1660+x^1661-x^1663+x^1664-x^1666+x^1667-x^1669+x^1670-x^\n"
              + "1672+x^1673-x^1675+x^1676-x^1678+x^1679-x^1681+x^1682-x^1684+x^1685-x^1687+x^\n"
              + "1688-x^1690+x^1691-x^1693+x^1694-x^1696+x^1697-x^1699+x^1700-x^1702+x^1703-x^\n"
              + "1705+x^1706-x^1708+x^1709-x^1711+x^1712-x^1714+x^1715-x^1717+x^1718-x^1720+x^\n"
              + "1721-x^1723+x^1724-x^1726+x^1727-x^1729+x^1730-x^1732+x^1733-x^1735+x^1736-x^\n"
              + "1738+x^1739-x^1741+x^1742-x^1744+x^1745-x^1747+x^1748-x^1750+x^1751-x^1753+x^\n"
              + "1754-x^1756+x^1757-x^1759+x^1760-x^1762+x^1763-x^1765+x^1766-x^1768+x^1769-x^\n"
              + "1771+x^1772-x^1774+x^1775-x^1777+x^1778-x^1780+x^1781-x^1783+x^1784-x^1786+x^\n"
              + "1787-x^1789+x^1790-x^1792+x^1793-x^1795+x^1796-x^1798+x^1799-x^1801+x^1802-x^\n"
              + "1804+x^1805-x^1807+x^1808-x^1810+x^1811-x^1813+x^1814-x^1816+x^1817-x^1819+x^\n"
              + "1820-x^1822+x^1823-x^1825+x^1826-x^1828+x^1829-x^1831+x^1832-x^1834+x^1835-x^\n"
              + "1837+x^1838-x^1840+x^1841-x^1843+x^1844-x^1846+x^1847-x^1849+x^1850-x^1852+x^\n"
              + "1853-x^1855+x^1856-x^1858+x^1859-x^1861+x^1862-x^1864+x^1865-x^1867+x^1868-x^\n"
              + "1870+x^1871-x^1873+x^1874-x^1876+x^1877-x^1879+x^1880-x^1882+x^1883-x^1885+x^\n"
              + "1886-x^1888+x^1889-x^1891+x^1892-x^1894+x^1895-x^1897+x^1898-x^1900+x^1901-x^\n"
              + "1903+x^1904-x^1906+x^1907-x^1909+x^1910-x^1912+x^1913-x^1915+x^1916-x^1918+x^\n"
              + "1919-x^1921+x^1922-x^1924+x^1925-x^1927+x^1928-x^1930+x^1931-x^1933+x^1934-x^\n"
              + "1936+x^1937-x^1939+x^1940-x^1942+x^1943-x^1945+x^1946-x^1948+x^1949-x^1951+x^\n"
              + "1952-x^1954+x^1955-x^1957+x^1958-x^1960+x^1961-x^1963+x^1964-x^1966+x^1967-x^\n"
              + "1969+x^1970-x^1972+x^1973-x^1975+x^1976-x^1978+x^1979-x^1981+x^1982-x^1984+x^\n"
              + "1985-x^1987+x^1988-x^1990+x^1991-x^1993+x^1994-x^1996+x^1997-x^1999+x^2000-x^\n"
              + "2002+x^2003-x^2005+x^2006-x^2008+x^2009-x^2011+x^2012-x^2014+x^2015-x^2017+x^\n"
              + "2018-x^2020+x^2021-x^2023+x^2024-x^2026+x^2027-x^2029+x^2030-x^2032+x^2033-x^\n"
              + "2035+x^2036-x^2038+x^2039-x^2041+x^2042-x^2044+x^2045-x^2047+x^2048-x^2050+x^\n"
              + "2051-x^2053+x^2054-x^2056+x^2057-x^2059+x^2060-x^2062+x^2063-x^2065+x^2066-x^\n"
              + "2068+x^2069-x^2071+x^2072-x^2074+x^2075-x^2077+x^2078-x^2080+x^2081-x^2083+x^\n"
              + "2084-x^2086+x^2087-x^2089+x^2090-x^2092+x^2093-x^2095+x^2096-x^2098+x^2099-x^\n"
              + "2101+x^2102-x^2104+x^2105-x^2107+x^2108-x^2110+x^2111-x^2113+x^2114-x^2116+x^\n"
              + "2117-x^2119+x^2120-x^2122+x^2123-x^2125+x^2126-x^2128+x^2129-x^2131+x^2132-x^\n"
              + "2134+x^2135-x^2137+x^2138-x^2140+x^2141-x^2143+x^2144-x^2146+x^2147-x^2149+x^\n"
              + "2150-x^2152+x^2153-x^2155+x^2156-x^2158+x^2159-x^2161+x^2162-x^2164+x^2165-x^\n"
              + "2167+x^2168-x^2170+x^2171-x^2173+x^2174-x^2176+x^2177-x^2179+x^2180-x^2182+x^\n"
              + "2183-x^2185+x^2186-x^2188+x^2189-x^2191+x^2192-x^2194+x^2195-x^2197+x^2198-x^\n"
              + "2200+x^2201-x^2203+x^2204-x^2206+x^2207-x^2209+x^2210-x^2212+x^2213-x^2215+x^\n"
              + "2216-x^2218+x^2219-x^2221+x^2222-x^2224+x^2225-x^2227+x^2228-x^2230+x^2231-x^\n"
              + "2233+x^2234-x^2236+x^2237-x^2239+x^2240-x^2242+x^2243-x^2245+x^2246-x^2248+x^\n"
              + "2249-x^2251+x^2252-x^2254+x^2255-x^2257+x^2258-x^2260+x^2261-x^2263+x^2264-x^\n"
              + "2266+x^2267-x^2269+x^2270-x^2272+x^2273-x^2275+x^2276-x^2278+x^2279-x^2281+x^\n"
              + "2282-x^2284+x^2285-x^2287+x^2288-x^2290+x^2291-x^2293+x^2294-x^2296+x^2297-x^\n"
              + "2299+x^2300-x^2302+x^2303-x^2305+x^2306-x^2308+x^2309-x^2311+x^2312-x^2314+x^\n"
              + "2315-x^2317+x^2318-x^2320+x^2321-x^2323+x^2324-x^2326+x^2327-x^2329+x^2330-x^\n"
              + "2332+x^2333-x^2335+x^2336-x^2338+x^2339-x^2341+x^2342-x^2344+x^2345-x^2347+x^\n"
              + "2348-x^2350+x^2351-x^2353+x^2354-x^2356+x^2357-x^2359+x^2360-x^2362+x^2363-x^\n"
              + "2365+x^2366-x^2368+x^2369-x^2371+x^2372-x^2374+x^2375-x^2377+x^2378-x^2380+x^\n"
              + "2381-x^2383+x^2384-x^2386+x^2387-x^2389+x^2390-x^2392+x^2393-x^2395+x^2396-x^\n"
              + "2398+x^2399-x^2401+x^2402-x^2404+x^2405-x^2407+x^2408-x^2410+x^2411-x^2413+x^\n"
              + "2414-x^2416+x^2417-x^2419+x^2420-x^2422+x^2423-x^2425+x^2426-x^2428+x^2429-x^\n"
              + "2431+x^2432-x^2434+x^2435-x^2437+x^2438-x^2440+x^2441-x^2443+x^2444-x^2446+x^\n"
              + "2447-x^2449+x^2450-x^2452+x^2453-x^2455+x^2456)");
    }

    check("Factor(x^34+x^17+1)", //
        "(1+x+x^2)*(1-x+x^3-x^4+x^6-x^7+x^9-x^10+x^12-x^13+x^15-x^16+x^17-x^19+x^20-x^22+x^\n"
            + "23-x^25+x^26-x^28+x^29-x^31+x^32)");
    check("Factor(I,GaussianIntegers->True)", //
        "I");
    // https://github.com/kredel/java-algebra-system/issues/12
    // for (int i = 0; i < 100000; i++) {
    // System.out.println(i);
    // String[] vars = new String[] { "a", "c", "d", "e", "x" };
    // GenPolynomialRing<edu.jas.arith.BigInteger> fac;
    // fac = new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ZERO,
    // vars.length,
    // new TermOrder(TermOrder.INVLEX), vars);
    //
    // GenPolynomial<edu.jas.arith.BigInteger> poly = fac.parse("a*d*e + c*d^2*x + a*e^2*x +
    // c*d*e*x^2");
    // System.out.println("A: " + poly.toString());
    // FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory
    // .getImplementation(edu.jas.arith.BigInteger.ZERO);
    // SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map = factorAbstract.factors(poly);
    // }
    // System.out.println();
    check("Factor(1+x^2, Extension->I)", //
        "(-I+x)*(I+x)");

    check("Factor(x^(-6)+1)", //
        "((1+x^2)*(1-x^2+x^4))/x^6");
    System.out.println();
    System.out.print('.');
    check("Factor(x+2*Sqrt(x)+1)", //
        "(1+Sqrt(x))^2");
    // check("Factor(E^x+E^(2*x))", //
    // "E^x*(1+E^x)");
    System.out.print('.');
    check("Factor(r^2+k*q*Q*r^4-E*r^6)", //
        "r^2*(1+k*q*Q*r^2-E*r^4)");
    System.out.print('.');
    check("Factor(x+2*Sqrt(x)+1)", //
        "(1+Sqrt(x))^2");
    System.out.print('.');
    check("Factor((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))", //
        "((a*e+c*d*x)*(d+e*x))^(3/2)");
    System.out.print('.');
    check("Factor(Cos(x)-I*Sin(x) )", //
        "Cos(x)-I*Sin(x)");
    System.out.print('.');
    check("Factor((Cos(x)-I*Sin(x))/(I*Cos(x)-Sin(x)))", //
        "(Cos(x)-I*Sin(x))/(I*Cos(x)-Sin(x))");
    System.out.print('.');
    check("Factor(12 * x^2 -75 * y^2)", //
        "3*(2*x-5*y)*(2*x+5*y)");

    // example from paper
    // https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
    System.out.print('.');
    check("Factor(4^(2*x+1)*5^(x-2)-6^(1-x))", //
        "(2^(1-x)*(-75+2^(1+5*x)*15^x))/(25*3^x)");
    System.out.print('.');
    check("Factor(E^x+E^(2*x))", //
        "E^x*(1+E^x)");

    // example from paper
    // https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
    System.out.print('.');
    check("Factor(Log(2,x)+4*Log(x,2)-5)", //
        "((-4*Log(2)+Log(x))*(-Log(2)+Log(x)))/(Log(2)*Log(x))");
    // TODO reduce negative signs
    // ((Log(2) - Log(x))*(4*Log(2) - Log(x)))/(Log(2)*Log(x))
    System.out.print('.');
    check("Factor( (4*a^2-5*a*b+b^2)/(a*b) )", //
        "((-4*a+b)*(-a+b))/(a*b)");

    // example from paper
    System.out.print('.');
    check("Factor(3*Tan(3*x)-Tan(x)+2)", //
        "2-Tan(x)+3*Tan(3*x)");
    System.out.print('.');
    check("TrigToExp(3*Tan(3*x)-Tan(x)+2)", //
        "2+(-I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))+(I*3*(E^(-I*3*x)-E^(I*3*x)))/(E^(-\n"
            + "I*3*x)+E^(I*3*x))");
    System.out.print('.');
    check(
        "Factor(2+(-I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))+(I*3*(E^(-I*3*x)-E^(I*3*x)))/(E^(-I*3*x)+E^(I*3*x)))", //
        "((2-I*2)*(I+(-1/2-I*1/2)*E^(I*2*x)+E^(I*4*x)))/((-1-I*E^(I*x)+E^(I*2*x))*(-1+I*E^(I*x)+E^(\n"
            + "I*2*x)))");

    // example from paper
    System.out.print('.');
    check("Factor(3*Sech(x)^2+4*Tanh(x)+1)", //
        "1+3*Sech(x)^2+4*Tanh(x)");
    System.out.print('.');
    check("TrigToExp(3*Sech(x)^2+4*Tanh(x)+1)", //
        "1+12/(E^(-x)+E^x)^2+4*(-1/(E^x*(E^(-x)+E^x))+E^x/(E^(-x)+E^x))");

    // example from paper
    System.out.print('.');
    check("Factor(Log(x+1)+Log(x-1)-3)", //
        "-3+Log(-1+x)+Log(1+x)");
    System.out.print('.');
    check("TrigToExp(Log(x+1)+Log(x-1)-3)", //
        "-3+Log(-1+x)+Log(1+x)");
    check("-3+Log(1+x)+Log(-1+x)", //
        "-3+Log(-1+x)+Log(1+x)");

    // example from paper
    System.out.print('.');
    check("Factor(E^(3*x)-4*E^x+3*E^(-x))", //
        "((-1+E^x)*(1+E^x)*(-3+E^(2*x)))/E^x");

    // example from paper
    System.out.print('.');
    check("Factor(Cosh(x)-3*Sinh(y))", //
        "Cosh(x)-3*Sinh(y)");

    // 1/(E^x*2) + E^x/2 + 3/(E^y*2) - (3*E^y)/2
    System.out.print('.');
    check("TrigToExp(Cosh(x)-3*Sinh(y))", //
        "1/(2*E^x)+E^x/2-3*(-1/(2*E^y)+E^y/2)");
    System.out.print('.');
    check("TrigToExp(Cosh(x))", //
        "1/(2*E^x)+E^x/2");
    System.out.print('.');
    check("TrigToExp(Sinh(x))", //
        "-1/(2*E^x)+E^x/2");

    // example from paper
    System.out.print('.');
    check("Factor(2*Sinh(x)+6*Cosh(y)-5)", //
        "-5+6*Cosh(y)+2*Sinh(x)");
    System.out.print('.');
    check("TrigToExp(2*Sinh(x)+6*Cosh(y)-5)", //
        "-5+2*(-1/(2*E^x)+E^x/2)+6*(1/(2*E^y)+E^y/2)");

    // example from paper
    System.out.print('.');
    check("TrigToExp(Cos(x) + Cos(3*x) + Cos(5*x))", //
        "1/(2*E^(I*5*x))+1/(2*E^(I*3*x))+1/(2*E^(I*x))+E^(I*x)/2+E^(I*3*x)/2+E^(I*5*x)/2");
    //
    // // TODO determine more factors
    System.out.print('.');
    check("Factor(1/(2*E^(I*5*x))+1/(2*E^(I*3*x))+1/(2*E^(I*x))+E^(I*x)/2+E^(I*3*x)/2+E^(I*5*x)/2)", //
        "((-I+E^(I*x))*(I+E^(I*x))*(1-E^(I*x)+E^(I*2*x))*(-1-I*E^(I*x)+E^(I*2*x))*(-1+I*E^(I*x)+E^(\n"
            + "I*2*x))*(1+E^(I*x)+E^(I*2*x)))/(2*E^(I*5*x))");
    // ((1/2)*(1 + E^(2*I*x))*(1 - E^(I*x) + E^(2*I*x))*(1 + E^(I*x) + E^(2*I*x))*
    // (1 - E^(2*I*x) + E^(4*I*x)))/E^(5*I*x)
    System.out.print('.');
    check("Factor(TrigToExp(Cos(x) + Cos(3*x) + Cos(5*x)))", //
        "((-I+E^(I*x))*(I+E^(I*x))*(1-E^(I*x)+E^(I*2*x))*(-1-I*E^(I*x)+E^(I*2*x))*(-1+I*E^(I*x)+E^(\n"
            + "I*2*x))*(1+E^(I*x)+E^(I*2*x)))/(2*E^(I*5*x))");

    System.out.print('.'); // (a+I*b)*(Cosh(x)-I*Sinh(x))
    check("Factor(a*Cosh(x) + I*b*Cosh(x) - I*a*Sinh(x) + b*Sinh(x))", //
        "(-I*a+b)*(I*Cosh(x)+Sinh(x))");
    System.out.print('.');
    check("Factor(a*b+(4+4*x+x^2)^2)", //
        "16+a*b+32*x+24*x^2+8*x^3+x^4");

    // github #121
    System.out.print('.');
    check("Factor(x^(12)-y^(12), GaussianIntegers->True)", //
        "(x-y)*(-I*x+y)*(I*x+y)*(x+y)*(x^2+x*y+y^2)*(x^2-x*y+y^2)*(-x^2-I*x*y+y^2)*(-x^2+I*x*y+y^\n"
            + //
            "2)");
    System.out.print('.');
    check("Factor(x^(2)+y^(2), GaussianIntegers->True)", //
        "(-I*x+y)*(I*x+y)");
    System.out.print('.');
    check("Factor(Sin(x), GaussianIntegers->True)", //
        "Sin(x)");

    System.out.print('.');
    check("Factor(1+x^2, GaussianIntegers->True)", //
        "(-I+x)*(I+x)");
    System.out.print('.');
    check("Factor(1+x^2, Extension->I)", //
        "(-I+x)*(I+x)");
    check("Factor(x^(2)+y^(2), GaussianIntegers->False)", //
        "x^2+y^2");

    // Homogenization example from
    // https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
    System.out.print('.');
    check("Factor(E^(3*x)-4*E^x+3*E^(-x))", //
        "((-1+E^x)*(1+E^x)*(-3+E^(2*x)))/E^x");
    System.out.print('.');
    check("Factor(E^x+E^(2*x))", //
        "E^x*(1+E^x)");
    System.out.print('.');
    check("Factor(Sin(x))", //
        "Sin(x)");

    // TODO https://github.com/kredel/java-algebra-system/issues/8
    System.out.print('.');
    check("Factor(a*c+(b*c+a*d)*x+b*d*x^2)", //
        "(a+b*x)*(c+d*x)");

    System.out.print('.');
    check("Factor(b*c*n-a*d*n)", //
        "(b*c-a*d)*n");
    System.out.print('.');
    check("Factor(a*b*(4+4*x+x^2)^2)", //
        "a*b*(2+x)^4");

    System.out.print('.');
    check("Factor(x^2 - y^2)", //
        "(x-y)*(x+y)");
    System.out.print('.');
    check("Factor(1 / (x^2+2*x+1) + 1 / (x^4+2*x^2+1))", //
        "(2+2*x+3*x^2+x^4)/((1+x)^2*(1+x^2)^2)");

    System.out.print('.');
    check("Factor({x+x^2})", //
        "{x*(1+x)}");
    System.out.print('.');

    if (Config.EXPENSIVE_JUNIT_TESTS) {
      check("Factor(x^259+1)", //
          "(1+x)*(1-x+x^2-x^3+x^4-x^5+x^6)*(1-x+x^2-x^3+x^4-x^5+x^6-x^7+x^8-x^9+x^10-x^11+x^\n"
              + "12-x^13+x^14-x^15+x^16-x^17+x^18-x^19+x^20-x^21+x^22-x^23+x^24-x^25+x^26-x^27+x^\n"
              + "28-x^29+x^30-x^31+x^32-x^33+x^34-x^35+x^36)*(1+x-x^7-x^8+x^14+x^15-x^21-x^22+x^\n"
              + "28+x^29-x^35-x^36-x^37-x^38+x^42+x^43+x^44+x^45-x^49-x^50-x^51-x^52+x^56+x^57+x^\n"
              + "58+x^59-x^63-x^64-x^65-x^66+x^70+x^71+x^72+x^73+x^74+x^75-x^77-x^78-x^79-x^80-x^\n"
              + "81-x^82+x^84+x^85+x^86+x^87+x^88+x^89-x^91-x^92-x^93-x^94-x^95-x^96+x^98+x^99+x^\n"
              + "100+x^101+x^102+x^103-x^105-x^106-x^107-x^108-x^109-x^110-x^111+x^113+x^114+x^\n"
              + "115+x^116+x^117+x^118-x^120-x^121-x^122-x^123-x^124-x^125+x^127+x^128+x^129+x^\n"
              + "130+x^131+x^132-x^134-x^135-x^136-x^137-x^138-x^139+x^141+x^142+x^143+x^144+x^\n"
              + "145+x^146-x^150-x^151-x^152-x^153+x^157+x^158+x^159+x^160-x^164-x^165-x^166-x^\n"
              + "167+x^171+x^172+x^173+x^174-x^178-x^179-x^180-x^181+x^187+x^188-x^194-x^195+x^\n"
              + "201+x^202-x^208-x^209+x^215+x^216)");
      System.out.print('.');
      check("Factor(x^258-1)", //
          "(-1+x)*(1+x)*(1-x+x^2)*(1+x+x^2)*(1-x+x^2-x^3+x^4-x^5+x^6-x^7+x^8-x^9+x^10-x^11+x^\n"
              + "12-x^13+x^14-x^15+x^16-x^17+x^18-x^19+x^20-x^21+x^22-x^23+x^24-x^25+x^26-x^27+x^\n"
              + "28-x^29+x^30-x^31+x^32-x^33+x^34-x^35+x^36-x^37+x^38-x^39+x^40-x^41+x^42)*(1+x+x^\n"
              + "2+x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+x^11+x^12+x^13+x^14+x^15+x^16+x^17+x^18+x^19+x^\n"
              + "20+x^21+x^22+x^23+x^24+x^25+x^26+x^27+x^28+x^29+x^30+x^31+x^32+x^33+x^34+x^35+x^\n"
              + "36+x^37+x^38+x^39+x^40+x^41+x^42)*(1-x+x^3-x^4+x^6-x^7+x^9-x^10+x^12-x^13+x^15-x^\n"
              + "16+x^18-x^19+x^21-x^22+x^24-x^25+x^27-x^28+x^30-x^31+x^33-x^34+x^36-x^37+x^39-x^\n"
              + "40+x^42-x^44+x^45-x^47+x^48-x^50+x^51-x^53+x^54-x^56+x^57-x^59+x^60-x^62+x^63-x^\n"
              + "65+x^66-x^68+x^69-x^71+x^72-x^74+x^75-x^77+x^78-x^80+x^81-x^83+x^84)*(1+x-x^3-x^\n"
              + "4+x^6+x^7-x^9-x^10+x^12+x^13-x^15-x^16+x^18+x^19-x^21-x^22+x^24+x^25-x^27-x^28+x^\n"
              + "30+x^31-x^33-x^34+x^36+x^37-x^39-x^40+x^42-x^44-x^45+x^47+x^48-x^50-x^51+x^53+x^\n"
              + "54-x^56-x^57+x^59+x^60-x^62-x^63+x^65+x^66-x^68-x^69+x^71+x^72-x^74-x^75+x^77+x^\n"
              + "78-x^80-x^81+x^83+x^84)");
    }
    System.out.print('.');
    check("Factor(4*x^2+3, Extension->I)", //
        "4*(3/4+x^2)");
    System.out.print('.');
    check("Factor(3/4*x^2+9/16, Extension->I)", //
        "3/4*(3/4+x^2)");

    System.out.print('.');
    check("Factor(x^10 - 1, Modulus -> 2)", //
        "(1+x)^2*(1+x+x^2+x^3+x^4)^2");

    System.out.print('.');
    check("factor(-1+x^16)", //
        "(-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8)");
    System.out.print('.');
    check("factor((-3)*x^3 +10*x^2-11*x+4)", //
        "(1-x)^2*(4-3*x)");
    System.out.print('.');
    check("factor(x^2-a^2)", //
        "(-a+x)*(a+x)");

    // is sometimes slow, if it calls
    // FactorAbstract#factorsSquarefreeKronecker()
    System.out.print('.');
    check("factor(2*x^3*y - 2*a^2*x*y - 3*a^2*x^2 + 3*a^4)", //
        "(-a+x)*(a+x)*(-3*a^2+2*x*y)");
    System.out.print('.');
    check("expand((x+a)*(-x+a)*(-2*x*y+3*a^2))", //
        "3*a^4-3*a^2*x^2-2*a^2*x*y+2*x^3*y");
  }

  @Test
  public void testFactorialPower() {

    check("N(FactorialPower(1/3, 7, 3), 50)", //
        "131965.39551897576588934613625971650663008687700045");
    checkNumeric("FactorialPower(1 + I, I, 3.)", //
        "0.61613306455158+I*1.0098175021356186");

    check("FactorialPower(-5, 3, 0.5)", //
        "-165.0");

    check("FactorialPower(x,n,0)", //
        "x^n");
    check("FactorialPower(2-I, 2)", //
        "1-I*3");

    check("Gamma(2.0+1.0)/Gamma(2.0-0.9+1.0)", //
        "1.91116");
    check("FactorialPower(2, 0.9)", //
        "1.91116");
    check("FactorialPower(1,3)", //
        "0");
    check("FactorialPower(19,1317624576693539401)", //
        "0");

    check("FactorialPower(-1,1317624576693539401,1+Sqrt(2))", //
        "FactorialPower(-1,1317624576693539401,1+Sqrt(2))");
    check("FactorialPower(E^(I*1/3*Pi),1317624576693539401)", //
        "FactorialPower(1/2+I*1/2*Sqrt(3),1317624576693539401)");

    check("FactorialPower(3, 2)", //
        "6");
    check("FactorialPower(2, 2)", //
        "2");
    check("FactorialPower(1, 2)", //
        "0");
    check("FactorialPower(0, 2)", //
        "0");
    check("FactorialPower(9, 1)", //
        "9");
    check("FactorialPower(1, 1)", //
        "1");
    check("FactorialPower(100, 0)", //
        "1");
    check("FactorialPower(100, 3)", //
        "970200");
    check("FactorialPower(-1, 2)", //
        "2");
    check("FactorialPower(-1, 3)", //
        "-6");
    check("FactorialPower(-2, 2)", //
        "6");
    check("FactorialPower(-3, 2)", //
        "12");
    check("FactorialPower(100, 3)", //
        "970200");
    check("FactorialPower(-100, 3)", //
        "-1030200");
    check("FactorialPower(0.5, 1)", //
        "0.5");
    check("FactorialPower(0.5, 2)", //
        "-0.25");
    check("FactorialPower(0.1, 3)", //
        "0.171");
    check("FactorialPower(8, 2, 2)", //
        "48");
    check("FactorialPower(1, 2, 2)", //
        "-1");
    check("FactorialPower(-0.5, 3, 2)", //
        "-5.625");
    check("FactorialPower(0.5, 2, 3)", //
        "-1.25");
    check("FactorialPower(10, 3, 5)", //
        "0");
    check("FactorialPower(0.5, 3, 0.5)", //
        "0.0");
    check("FactorialPower(0.5, 3, 0.1)", //
        "0.06");
    check("FactorialPower(-5, 3, 0.5)", //
        "-165.0");
    check("FactorialPower(2-I, 1)", //
        "2-I");
    check("FactorialPower(1-5I, 0)", //
        "1");
    check("FactorialPower(2-I, 2)", //
        "1-I*3");
    check("FactorialPower(2-I, 2, 2)", //
        "-1-I*2");
    check("FactorialPower(2-I, 2, 0)", //
        "3-I*4");
    check("FactorialPower(2-I, 2, -1)", //
        "5-I*5");
    check("FactorialPower(1+I, 3, -2)", //
        "6+I*22");
    check("FactorialPower(5+6I, 2, -1.5)", //
        "-3.5+I*69.0");
  }


  @Test
  public void testFactorSquareFree() {
    check("a == d b + d c // FactorSquareFree", //
        "a==(b+c)*d");
    check("FactorSquareFree((b*c*x^2*Log(F))/(e^2-b^2*c^2*Log(F)^2))", //
        "(b*c*x^2*Log(F))/(e^2-b^2*c^2*Log(F)^2)");
    check("FactorSquareFree(x^2147483647)", //
        "x^2147483647");
    check("FactorSquareFree(x^2)", //
        "x^2");
    check("FactorSquareFree(c^(1/4)*g^(1/4)*(5+2*m+3*n)^(1/4)*x)", //
        "c^(1/4)*g^(1/4)*(5+2*m+3*n)^(1/4)*x");
    check("p = Expand((x + 1)^2 (x + 2)^2 (x + 3)^3)", //
        "108+432*x+711*x^2+625*x^3+318*x^4+94*x^5+15*x^6+x^7");
    check("FactorSquareFree(p)", //
        "(3+x)^3*(2+3*x+x^2)^2");
  }

  @Test
  public void testFactorSquareFreeList() {
    // bug endless loop ?
    check("FactorSquareFreeList(x^2147483647)", //
        "{{x,2147483647}}");
    check("FactorSquareFreeList(42)", //
        "{{42,1}}");
    check("FactorSquareFreeList(x)", //
        "{{1,1},{x,1}}");
    check("FactorSquareFreeList(x^5 - x^3 - x^2 + 1)", //
        "{{-1+x,2},{1+2*x+2*x^2+x^3,1}}");
    check(
        "FactorSquareFreeList(x^8 + 11*x^7 + 43*x^6 + 59*x^5 - 35*x^4 - 151*x^3 - 63*x^2 + 81*x + 54)", //
        "{{2+x,1},{3+x,3},{-1+x^2,2}}");
    check("FactorSquareFreeList((-3)*x^3 +10*x^2-11*x+4)", //
        "{{-1,1},{-1+x,2},{-4+3*x,1}}");
  }

  @Test
  public void testFactorTerms() {
    check("FactorTerms(-41*Im(z)-82*Re(z))", //
        "-41*(Im(z)+2*Re(z))");
    check("FactorTerms(Cosh(E^(2*x)*Sin(2*y)))", //
        "Cosh(E^(2*x)*Sin(2*y))");

    // check("Expand((3 + 2 x)^2*(x + 2 y)^2)",//
    // "9*x^2+12*x^3+4*x^4+36*x*y+48*x^2*y+16*x^3*y+36*y^2+48*x*y^2+16*x^2*y^2");
    // check("FactorTerms(9*x^2+12*x^3+4*x^4+36*x*y+48*x^2*y+16*x^3*y+36*y^2+48*x*y^2+16*x^2*y^2,
    // y)",//
    // "");

    // TODO create simpler form
    // check("FactorTerms(2*a*x^2*y + 2*x^2*y + 4*a*x^2 + 4*x^2 + 4*a^2*y^2 + 4*a*y^2 + 8*a^2*y +
    // 2*a*y - 6*y -
    // 12*a- 12, x)", //
    // "");

    check("FactorTerms(4*Im(z)^2+4*Re(z)^2)", //
        "4*(Im(z)^2+Re(z)^2)");
    check("FactorTerms(100*Log(2))", //
        "100*Log(2)");
    check("FactorTerms(-136+40*Sqrt(17))", //
        "8*(-17+5*Sqrt(17))");

    check("FactorTerms(x^2 - y^2, x)", //
        "x^2-y^2");
    check("factorterms(3 + 6*x + 3*x^2)", //
        "3*(1+2*x+x^2)");
  }

  @Test
  public void testFactorTermsList() {
    check("FactorTermsList(6*a^2 + 9*x^2 + 12*b^2)", //
        "{3,2*a^2+4*b^2+3*x^2}");
    check("FactorTermsList(3 + 3*a + 6*a*x + 6*x + 12*a*x^2 + 12*x^2, x)", //
        "{3,1+a+2*x+2*a*x+4*x^2+4*a*x^2}");

    // FactorTermsList: {g(x,y)} is not a polynomial.
    check("FactorTermsList({g(x,y)})", //
        "FactorTermsList({g(x,y)})");
    check("FactorTermsList(g(x,y))", //
        "{1,g(x,y)}");
    check("FactorTermsList(100*Log(2))", //
        "{100,Log(2)}");
    check("FactorTermsList(I*Log(2))", //
        "{I,Log(2)}");
    check("FactorTermsList(x^2 - y^2 )", //
        "{1,x^2-y^2}");
    check("FactorTermsList(3 + 6*x + 3*x^2)", //
        "{3,1+2*x+x^2}");
    check("FactorTermsList(5*x^2 - 5)", //
        "{5,-1+x^2}");
    check("FactorTermsList(-136+40*Sqrt(17))", //
        "{8,-17+5*Sqrt(17)}");
    check("FactorTermsList(3+3/4*x^3+12/17*x^2)", //
        "{3/68,68+16*x^2+17*x^3}");

  }

  @Test
  public void testFibonacci() {
    checkNumeric("Fibonacci(5.8, 3)", //
        "283.4827308329499");
    // check("Fibonacci(10007,Quantity(1.2,\"m\"))", //
    // "Fibonacci(10007,1.2[m])");
    // Fibonacci(11,{13,2,3,a})
    check("Fibonacci(11,{13,2,3,a})", //
        "{145336221161,5741,141481,1+15*a^2+35*a^4+28*a^6+9*a^8+a^10}");
    // message Polynomial degree 151 exceeded
    check("Fibonacci(151,11/9223372036854775807)", //
        "Fibonacci(151,11/9223372036854775807)");

    // big integer limit: Fibonacci: BigInteger bit length 363981 exceeded.
    check("Fibonacci(2147483647)", //
        "Fibonacci(2147483647)");

    check("Fibonacci(0.2114411444411100011, 5)", //
        "0.1598551917369153725");
    check("Fibonacci(5.8)", //
        "7.26639");
    check("Fibonacci(1+I/2)//N", //
        "1.3763+I*0.00716945");
    check("Fibonacci(-10, x)", //
        "-5*x-20*x^3-21*x^5-8*x^7-x^9");
    check("Fibonacci(11, x)", //
        "1+15*x^2+35*x^4+28*x^6+9*x^8+x^10");
    check("Fibonacci(50, x)", //
        "25*x+2600*x^3+80730*x^5+1184040*x^7+10015005*x^9+54627300*x^11+206253075*x^13+\n"
            + "565722720*x^15+1166803110*x^17+1855967520*x^19+2319959400*x^21+2310789600*x^23+\n"
            + "1852482996*x^25+1203322288*x^27+635745396*x^29+273438880*x^31+95548245*x^33+\n"
            + "26978328*x^35+6096454*x^37+1086008*x^39+148995*x^41+15180*x^43+1081*x^45+48*x^47+x^\n"
            + "49");
    check("Table(Fibonacci(n, x), {n, 5})", //
        "{1,x,1+x^2,2*x+x^3,1+3*x^2+x^4}");

    check("Table(Fibonacci(n), {n, 45})", //
        "{1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765,10946,17711,\n"
            + "28657,46368,75025,121393,196418,317811,514229,832040,1346269,2178309,3524578,\n"
            + "5702887,9227465,14930352,24157817,39088169,63245986,102334155,165580141,\n"
            + "267914296,433494437,701408733,1134903170}");
    // check("Fibonacci(10000)", "0");
    check("Fibonacci(0)", //
        "0");
    check("Fibonacci(1)", //
        "1");
    check("Fibonacci(10)", //
        "55");
    check("Fibonacci(-51)", //
        "20365011074");
    check("Fibonacci(-52)", //
        "-32951280099");
    check("Fibonacci(200)", //
        "280571172992510140037611932413038677189525");
    check("Table(Fibonacci(-n), {n, 10})", //
        "{1,-1,2,-3,5,-8,13,-21,34,-55}");
    check("Fibonacci(1000)", //
        "4346655768693745643568852767504062580256466051737178040248172908953655541794905\\\n"
            + "1890403879840079255169295922593080322634775209689623239873322471161642996440906\\\n"
            + "533187938298969649928516003704476137795166849228875");
  }

  @Test
  public void testFileFormat() {
    check("FileFormat(\"example/picture.jpg\")", //
        "JPEG");
    check("FileFormat(\"example/picture.test\")", //
        "None");
  }

  @Test
  public void testFilterRules() {
    check("FilterRules({a -> 1, b -> 2, c -> 3}, {b, a})", //
        "{a->1,b->2}");
    check("Options(f) = {a -> 1, b -> 2}", //
        "{a->1,b->2}");
    check("FilterRules({b -> 3, MaxIterations -> 5}, Options(f))", //
        "{b->3}");
  }

  @Test
  public void testFindClusters() {
    check("FindClusters(DirectedInfinity({-1,-2,3}),Times())", //
        "FindClusters({-Infinity,-Infinity,Infinity},1)");
    // check(
    // "FindClusters({{2, 3}, {5, 10}, {4, 5}, {2, 2}}, DistanceFunction -> CorrelationDistance)",
    // //
    // "{{{2.0,3.0},{4.0,5.0},{2.0,2.0}},{{5.0,10.0}},{}}");

    // Test data generator http://people.cs.nctu.edu.tw/~rsliang/dbscan/testdatagen.html
    // check("FindClusters({{83.08303244924173,58.83387754182331},{45.05445510940626,23.469642649637535},{14.96417921432294,69.0264096390456},{73.53189604333602,34.896145021310076},{73.28498173551634,33.96860806993209},{73.45828098873608,33.92584423092194},{73.9657889183145,35.73191006924026},{74.0074097183533,36.81735596177168},{73.41247541410848,34.27314856695011},{73.9156256353017,36.83206791547127},{74.81499205809087,37.15682749846019},{74.03144880081527,37.57399178552441},{74.51870941207744,38.674258946906775},{74.50754595105536,35.58903978415765},{74.51322752749547,36.030572259100154},{59.27900996617973,46.41091720294207},{59.73744793841615,46.20015558367595},{58.81134076672606,45.71150126331486},{58.52225539437495,47.416083617601544},{58.218626647023484,47.36228902172297},{60.27139669447206,46.606106348801404},{60.894962462363765,46.976924697402865},{62.29048673878424,47.66970563563518},{61.03857608977705,46.212924720020965}},
    // 2.0, \n" // +
    // "5, Method->\"DBSCAN\", DistanceFunction->EuclideanDistance)",//
    // "{{{73.5319,34.89615},{73.28498,33.96861},{73.45828,33.92584},{73.96579,35.73191},{74.00741,36.81736},{73.41248,34.27315},{73.91563,36.83207},{74.50755,35.58904},{74.51323,36.03057},{74.81499,37.15683},{74.03145,37.57399},{74.51871,38.67426}},{{59.27901,46.41092},{59.73745,46.20016},{58.81134,45.7115},{58.52226,47.41608},{58.21863,47.36229},{60.2714,46.60611},{60.89496,46.97692},{61.03858,46.21292},{62.29049,47.66971}}}");
    // check("FindClusters({{ 83.08303244924173, 58.83387754182331 },\n" + //
    // "{ 45.05445510940626, 23.469642649637535 },\n" + //
    // "{ 14.96417921432294, 69.0264096390456 },\n" + //
    // "{ 73.53189604333602, 34.896145021310076 },\n" + //
    // "{ 73.28498173551634, 33.96860806993209 },\n" + //
    // "{ 73.45828098873608, 33.92584423092194 },\n" + //
    // "{ 73.9657889183145, 35.73191006924026 },\n" + //
    // "{ 74.0074097183533, 36.81735596177168 },\n" + //
    // "{ 73.41247541410848, 34.27314856695011 },\n" + //
    // "{ 73.9156256353017, 36.83206791547127 },\n" + //
    // "{ 74.81499205809087, 37.15682749846019 },\n" + //
    // "{ 74.03144880081527, 37.57399178552441 },\n" + //
    // "{ 74.51870941207744, 38.674258946906775 },\n" + //
    // "{ 74.50754595105536, 35.58903978415765 },\n" + //
    // "{ 74.51322752749547, 36.030572259100154 },\n" + //
    // "{ 59.27900996617973, 46.41091720294207 },\n" + //
    // "{ 59.73744793841615, 46.20015558367595 },\n" + //
    // "{ 58.81134076672606, 45.71150126331486 },\n" + //
    // "{ 58.52225539437495, 47.416083617601544 },\n" + //
    // "{ 58.218626647023484, 47.36228902172297 },\n" + //
    // "{ 60.27139669447206, 46.606106348801404 },\n" + //
    // "{ 60.894962462363765, 46.976924697402865 },\n" + //
    // "{ 62.29048673878424, 47.66970563563518 },\n" + //
    // "{ 61.03857608977705, 46.212924720020965 },\n" + //
    // "{ 60.16916214139201, 45.18193661351688 },\n" + //
    // "{ 59.90036905976012, 47.555364347063005 },\n" + //
    // "{ 62.33003634144552, 47.83941489877179 },\n" + //
    // "{ 57.86035536718555, 47.31117930193432 },\n" + //
    // "{ 58.13715479685925, 48.985960494028404 },\n" + //
    // "{ 56.131923963548616, 46.8508904252667 },\n" + //
    // "{ 55.976329887053, 47.46384037658572 },\n" + //
    // "{ 56.23245975235477, 47.940035191131756 },\n" + //
    // "{ 58.51687048212625, 46.622885352699086 },\n" + //
    // "{ 57.85411081905477, 45.95394361577928 },\n" + //
    // "{ 56.445776311447844, 45.162093662656844 },\n" + //
    // "{ 57.36691949656233, 47.50097194337286 },\n" + //
    // "{ 58.243626387557015, 46.114052729681134 },\n" + //
    // "{ 56.27224595635198, 44.799080066150054 },\n" + //
    // "{ 57.606924816500396, 46.94291057763621 },\n" + //
    // "{ 30.18714230041951, 13.877149710431695 },\n" + //
    // "{ 30.449448810657486, 13.490778346545994 },\n" + //
    // "{ 30.295018390286714, 13.264889000216499 },\n" + //
    // "{ 30.160201832884923, 11.89278262341395 },\n" + //
    // "{ 31.341509791789576, 15.282655921997502 },\n" + //
    // "{ 31.68601630325429, 14.756873246748 },\n" + //
    // "{ 29.325963742565364, 12.097849250072613 },\n" + //
    // "{ 29.54820742388256, 13.613295356975868 },\n" + //
    // "{ 28.79359608888626, 10.36352064087987 },\n" + //
    // "{ 31.01284597092308, 12.788479208014905 },\n" + //
    // "{ 27.58509216737002, 11.47570110601373 },\n" + //
    // "{ 28.593799561727792, 10.780998203903437 },\n" + //
    // "{ 31.356105766724795, 15.080316198524088 },\n" + //
    // "{ 31.25948503636755, 13.674329151166603 },\n" + //
    // "{ 32.31590076372959, 14.95261758659035 },\n" + //
    // "{ 30.460413702763617, 15.88402809202671 },\n" + //
    // "{ 32.56178203062154, 14.586076852632686 },\n" + //
    // "{ 32.76138648530468, 16.239837325178087 },\n" + //
    // "{ 30.1829453331884, 14.709592407103628 },\n" + //
    // "{ 29.55088173528202, 15.0651247180067 },\n" + //
    // "{ 29.004155302187428, 14.089665298582986 },\n" + //
    // "{ 29.339624439831823, 13.29096065578051 },\n" + //
    // "{ 30.997460327576846, 14.551914158277214 },\n" + //
    // "{ 30.66784126125276, 16.269703107886016 }},2.0,5,Method->\"DBSCAN\",
    // DistanceFunction->EuclideanDistance)",
    // //
    // "{{{73.5319,34.89615},{73.28498,33.96861},{73.45828,33.92584},{73.96579,35.73191},{74.00741,36.81736},{73.41248,34.27315},{73.91563,36.83207},{74.50755,35.58904},{74.51323,36.03057},{74.81499,37.15683},{74.03145,37.57399},{74.51871,38.67426}},"//
    // +
    // "{{59.27901,46.41092},{59.73745,46.20016},{58.81134,45.7115},{58.52226,47.41608},{58.21863,47.36229},{60.2714,46.60611},{60.89496,46.97692},{61.03858,46.21292},{60.16916,45.18194},{59.90037,47.55536},{57.86036,47.31118},{58.51687,46.62289},{57.85411,45.95394},{58.24363,46.11405},{57.60692,46.94291},{58.13715,48.98596},{57.36692,47.50097},{62.29049,47.66971},{62.33004,47.83941},{56.13192,46.85089},{55.97633,47.46384},{56.23246,47.94004},{56.44578,45.16209},{56.27225,44.79908}},"
    // +
    // "{{30.18714,13.87715},{30.44945,13.49078},{30.29502,13.26489},{30.1602,11.89278},{31.34151,15.28266},{31.68602,14.75687},{29.32596,12.09785},{29.54821,13.6133},{31.01285,12.78848},{31.35611,15.08032},{31.25949,13.67433},{30.18295,14.70959},{29.55088,15.06512},{29.00416,14.08967},{29.33962,13.29096},{30.99746,14.55191},{28.5938,10.781},{32.3159,14.95262},{30.46041,15.88403},{32.56178,14.58608},{32.76139,16.23984},{30.66784,16.2697},{28.7936,10.36352},{27.58509,11.4757}}}");

    // check("FindClusters({{2.5, 3.1}, {5.9, 3.4}, {10, 15}, {2.2, 1.5}, {100, 7.5}})", //
    // "{{{2.5,3.1},{5.9,3.4},{2.2,1.5}},{{100.0,7.5}},{{10.0,15.0}}}");

    // check("FindClusters({1, 2, 3, 4, 5, 6, 7, 8, 9})", //
    // "{{1.0,2.0,3.0},{6.0,7.0,8.0,9.0},{4.0,5.0}}");

    // check("FindClusters({1, 2, 10, 12, 3, 1, 13, 25},4)", //
    // "{{1.0,2.0,3.0,1.0},{12.0,13.0},{25.0},{10.0}}");
  }

  @Test
  public void testFindFit() {
    check("FindFit({1.2214,1.49182,1.82212,2.22554,2.71828,3.32012,4.0552},a+b*x+c*x^2,{a,b,c},x)", //
        "{a->1.09428,b->0.0986352,c->0.0459481}");
    // https://stackoverflow.com/a/51696587/24819
    check(
        "FindFit({ {1.3,0.5}, {2.8,0.9}, {5.0,2.6}, {10.2,7.1}, {16.5,12.3}, {21.3,15.3},{ 31.8,20.4}, {52.2,24.4}}, " //
            + "d+((a-d)/ (1+(x/c)^ b)),  {{a, 1}, {b,2}, {c,20}, {d,20}}, x)", //
        "{a->0.174321,b->1.75938,c->19.69032,d->28.83068}");
    // initial guess [1.0, 1.0, 1.0] gives bad result:
    check("FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {a,w,f}, t)", //
        "{a->0.599211,w->1.51494,f->3.80421}");
    // initial guess [2.0, 1.0, 1.0] gives better result:
    check(
        "FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, {w,1}, {f,1}}, t)", //
        "{a->3.0,w->3.0,f->1.0}");
    // initial guess [2.0, 1.0, 1.0] with 1.0 by default:
    check(
        "FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, w, f}, t)", //
        "{a->3.0,w->3.0,f->1.0}");

    check("FindFit({{1,1},{2,4},{3,9},{4,16}}, " //
        + "a+b*x+c*x^2, {a, b, c}, x)", //
        "{a->-6.10623*10^-16,b->7.21645*10^-16,c->1.0}");
    check("FindFit({{15.2,8.9},{31.1,9.9},{38.6,10.3},{52.2,10.7},{75.4,11.4}}, " //
        + "a*Log(b*x), {a, b}, x)", //
        "{a->1.54503,b->20.28258}");
    check("FindFit(Table(Prime(x), {x, 20}), a*x*Log(b + c*x), {a, b, c}, x)", //
        "{a->1.42076,b->1.65558,c->0.534644}");
    check("FindFit({{1.0, 12.}, {1.9, 10.}, {2.6, 8.2}, {3.4, 6.9}, {5.0, 5.9}}, " //
        + "a*Exp(-k*t), {a, k}, t)", //
        "{a->14.38886,k->0.198208}");

    // initial guess [0, 0, 0] doesn't work
    check("FindFit(Table(Prime(x), {x, 20}), a*x*Log(b + c*x), {{a,0},{b,0},{c,0}}, x)", //
        "FindFit({2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71},a*x*Log(b+c*x),{{a,\n"
            + "0},{b,0},{c,0}},x)");
  }

  @Test
  public void testFindLinearRecurrence() {

    check("FindLinearRecurrence({{1,0},Sqrt(2)/2})", //
        "FindLinearRecurrence({{1,0},1/Sqrt(2)})");

    check("FindLinearRecurrence({1, 2, 4, 7, 28, 128, 582, 2745, 13021, 61699, 292521, 1387138})", //
        "{5,-2,6,-11}");
    check("FindLinearRecurrence(Table(2^k, {k, 10}))", //
        "{2}");
    check("FindLinearRecurrence(Table(Fibonacci(k)*Fibonacci(k-1)^2, {k, 10}))", //
        "{3,6,-3,-1}");

    check("data = LinearRecurrence({4, 3, 2, 1}, {1, 2, 3, 4}, 10)", //
        "{1,2,3,4,30,140,661,3128,14805,70066}");
    check("FindLinearRecurrence(data)", //
        "{4,3,2,1}");

  }

  @Test
  public void testFindMaximum() {
    check("FindMaximum(x^3-12*x^2+45*x+8, {x,4} )", //
        "{62.0,{x->3.0}}");

    // print message: The Function value False is not a real number at {x}={2.0}.
    check("FindMaximum(False,{x, 2})", //
        "FindMaximum(False,{x,2})");

    check("FindMaximum(-Abs(x + 1) - Abs(x + 1.01) - Abs(y + 1),{x, y})", //
        "{-0.01,{x->-1.00719,y->-1.0}}");

    check("FindMaximum(x*Cos(x), {x,2.0} )", //
        "{0.561096,{x->0.860334}}");
    check("FindMaximum(Sin(x)*Sin(2*y), {{x, 2}, {y, 2}})", //
        "{1.0,{x->4.71239,y->2.35619}}");

    check("FindMaximum(Sin(x), {x, 0.5})", //
        "{1.0,{x->1.5708}}");
    check(
        "FindMaximum(Sin(x)*Sin(2*y), {{x, 2}, {y, 2}}, MaxIterations->1000, Method -> \"ConjugateGradient\")", //
        "{1.0,{x->-1.5708,y->-0.785398}}");
  }

  @Test
  public void testFindMinimum() {
    check("FindMinimum(x^3-12*x^2+45*x+8, {x,4} )", //
        "{58.0,{x->5.0}}");
    check("FindMinimum(x^3-12*x^2+45*x+8, {x,1} )", //
        "FindMinimum(-12*x^2+x^3+45*x+8,{x,1})");

    check(
        "FindMinimum({x+y,3*x+2*y >= 7 , x >= 0 , y >= 0}, {x, y},Method -> \"SequentialQuadratic\")", //
        "{2.33333,{x->2.33333,y->-8.32917*10^-11}}");
    check(
        "FindMinimum({x+y,3*x+2*y >= 7 && x >= 0 && y >= 0}, {x, y},Method -> \"SequentialQuadratic\")", //
        "{2.33333,{x->2.33333,y->-8.32917*10^-11}}");
    // TODO Less and Greater are not allowed at the moment
    // message: FindMinimum: Constraints in `1` are not all 'equality' or 'less
    // equal' or 'greater equal' linear constraints. Constraints with Unequal(!=) are not supported.
    check(
        "FindMinimum({x+y,3*x+2*y > 7 && x > 0 && y > 0}, {x, y},Method -> \"SequentialQuadratic\")", //
        "FindMinimum({x+y,3*x+2*y>7&&x>0&&y>0},{x,y},Method->SequentialQuadratic)");

    // check("FindMinimum({Sin(x)*Sin(2*y),x^2 + y^2 < 3}, {{x, 2}, {y, 2}})", //
    // "");
    check("FindMinimum(Abs(x + 1) + Abs(x + 1.01) + Abs(y + 1),{x, y},MaxIterations->1000)", //
        "{0.01,{x->-1.00719,y->-1.0}}");
    check(
        "FindMinimum(Abs(x + 1) + Abs(x + 1.01) + Abs(y+1), {x, y}, Method -> \"ConjugateGradient\")", //
        "{0.01,{x->-1.01,y->-1.0}}");

    check("FindMinimum(Sin(x), {x,1} )", //
        "{-1.0,{x->-1.5708}}");
    check("FindMinimum(x*Cos(x), {x,5.0} )", //
        "{-3.28837,{x->3.42562}}");
    check("FindMinimum(x*Cos(x), {x,10.0} )", //
        "{-9.47729,{x->9.52933}}");

    check("FindMinimum(Sin(x)*Sin(2*y), {{x, 2}, {y, 2}})", //
        "{-1.0,{x->1.5708,y->2.35619}}");


    check("FindMinimum(Sin(x), {x, 0.5})", //
        "{-1.0,{x->-1.5708}}");
    check("FindMinimum(Sin(x), {x,1}, Method -> \"ConjugateGradient\")", //
        "{-1.0,{x->-7.85398}}");
    check("FindMinimum(x*Cos(x), {x,5.0}, Method -> \"ConjugateGradient\")", //
        "{-3.28837,{x->3.42562}}");
    check("FindMinimum(x*Cos(x), {x,10.0}, Method -> \"ConjugateGradient\")", //
        "{-9.47729,{x->9.52934}}");

    check("FindMinimum(Sin(x)*Sin(2*y), {{x, 2}, {y, 2}}, Method -> \"ConjugateGradient\")", //
        "{-1.0,{x->1.5708,y->2.35619}}");

  }
  // https://github.com/Hipparchus-Math/hipparchus/issues/40
  // @Test
  // public void testBisection() {
  // BisectionSolver solver = new BisectionSolver();
  // System.out.println(solver.solve(100, x->Math.cos(x)+2, 0.0, 5.0));
  // }

  @Test
  public void testFindRoot() {
    INumber times = F.CN1.times(F.num("1.8444E-19"));
    assertEquals(times.toString(), "-1.8444*10^-19");

    // print message: FindRoot: Search specification x should be a list with 1 to 3 elements.
    check("FindRoot(x^2.5 + x^0.5-100,x)", //
        "FindRoot(-100+Sqrt(x)+x^2.5,x)");
    check("FindRoot(x^2.5 + x^0.5-100,{x,1.0})", //
        "{x->6.24602}");

    // ???
    check("FindRoot({2400^4 - tw^4 -1.77*^-6*qw == 0,\n" //
        + "        tw^2 - t1^2 +2454*(tw - t1) - 0.2319 *qw ==0,\n" //
        + "        t1^4 - t2^4 +4.601*^6*(t1^2 - t2^2) + 1.129*^10*(t1 - t2) - 1.6229*^8 * qw == 0,\n" //
        + "        t2^2 - t3^2 + 2454 *(t2 -t3) -0.2319 *qw ==0,\n" //
        + "        tw -t3 -0.0549*qs == 0,\n" //
        + "        t3^4 - tb^4 -4.141*^5*(qs + qw) == 0,\n" //
        + "        tb - 320 -8.164*^-8*(qs +qw) == 0}, {{tw,2400+0.000015*I},{t1,-230-240*I},{t2,-241-230*I},{t3,4},{tb,1},{qw,1},{qs,1}})", //
        "{tw->2400.0,t1->2385.213,t2->708.4835,t3->680.6299,tb->320.0402,qw->461596.8,qs->31318.22}");
    check("{2400^4 - tw^4 -1.77*^-6*qw,\n" //
        + "        tw^2 - t1^2 +2454*(tw - t1) - 0.2319 *qw,\n" //
        + "        t1^4 - t2^4 +4.601*^6*(t1^2 - t2^2) + 1.129*^10*(t1 - t2) - 1.6229*^8 * qw,\n" //
        + "        t2^2 - t3^2 + 2454 *(t2 -t3) -0.2319 *qw,\n" //
        + "        tw -t3 -0.0549*qs,\n" //
        + "        t3^4 - tb^4 -4.141*^5*(qs + qw),\n" //
        + "        tb - 320 -8.164*^-8*(qs +qw)} /. {tw->2400.0,t1->2385.213,t2->708.4835,t3->680.6299,tb->320.0402,qw->461596.8,qs->31318.22}", //
        "{-0.816406,1.94471,-2.61528*10^7,0.245478,-0.000178,-35512.4,-0.0000415822}");

    // automatic: AccuracyGoal->6
    checkNumeric("10-x /. FindRoot(Sin(x - 10) - x + 10, {x, 0})", //
        "1.498271155142561E-6");
    checkNumeric("10-x /. FindRoot(Sin(x - 10) - x + 10, {x, 0},AccuracyGoal->4)", //
        "1.9439351756744827E-4");
    // Message FindRoot: interval does not bracket a root: f(NaN) = NaN, f(NaN) = NaN.
    checkNumeric("10-x /. FindRoot(Sin(x - 10) - x + 10, {x, 0},AccuracyGoal->8)", //
        "10-x/.FindRoot(10-x-Sin(10-x),{x,0},AccuracyGoal->8)");
    // Message FindRoot: Value of option AccuracyGoal->test is not Automatic or a machine-sized
    // integer.
    checkNumeric("10-x /. FindRoot(Sin(x - 10) - x + 10, {x, 0},AccuracyGoal->test)", //
        "10-x/.FindRoot(10-x-Sin(10-x),{x,0},AccuracyGoal->test)");

    // univariate cases
    check("FindRoot(sin(x)==x^2, {x, -6.6, 3.99999999})", //
        "{x->-1.84448*10^-19}");
    check("v1:= E^x - 3*x ;v2:={x, 2};", //
        "");
    check("FindRoot(v1,v2)", //
        "{x->1.51213}");
    check("{v1,v2,x}", //
        "{E^x-3*x,{x,2},x}");
    check("FindRoot(Surd(x, 2) == 1.5, {x, 1})", //
        "{x->2.25}");
    check("Exp(1.243624090168953 * E - 16) - 1", //
        "-0.999997");

    checkNumeric("FindRoot(30*x/0.000002==30, {x, 0, 5}, Method->brent)", //
        "{x->2.0E-6}");
    // github issue #60
    // Message FindRoot: Method->brent is only applicable for univariate real functions and requires
    // two real starting values that bracket the root.
    check("FindRoot(cos(x) + 2, {x, 0, 5}, Method->brent)", //
        "FindRoot(2+Cos(x),{x,0,5},Method->brent)");
    // FindRoot: Method->bisection is only applicable for univariate real functions and requires two
    // real starting values that bracket the root.
    check("FindRoot(cos(x) + 2, {x, 0, 5}, Method->bisection)", //
        "FindRoot(2+Cos(x),{x,0,5},Method->bisection)");
    check("FindRoot(cos(x) + 2, {x, 0, 5}, Method->newton)", //
        "FindRoot(2+Cos(x),{x,0,5},Method->newton)");

    // github issue #43
    check("findroot(abs(x-1)-2x-3==0, {x, -10, 10})", //
        "{x->-0.666667}");

    // github issue #103
    check("FindRoot(2^x==0,{x,-100, 100}, Method->Brent)", //
        "FindRoot(2^x==0,{x,-100,100},Method->brent)");
    // FindRoot: interval does not bracket a root: f(-1) = 0.5, f(100) =
    // 1,267,650,600,228,229,400,000,000,000,000
    check("FindRoot(2^x==0,{x,-1,100}, Method->Brent)", //
        "FindRoot(2^x==0,{x,-1,100},Method->brent)");

    checkNumeric("N(2^(-100))", //
        "7.888609052210118E-31");

    check("FindRoot(Exp(x)-1 == 0,{x,-50,100}, Method->Muller)", //
        "{x->1.24362*10^-16}");
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      // implicit times operator '*' allowed
      check("Exp(1.243624090168953 * E - 16) - 1", //
          "-0.999997");
      check("Exp(1.243624090168953E-16) - 1", //
          "-0.999997");
    } else {
      check("Exp(1.243624090168953E-16) - 1", //
          "0.0");
    }

    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10})", //
        "{x->3.4341896575482007}");
    checkNumeric("$K=10000;\n" + "$g=0.0;\n" + "$n=10*12;\n" + "$Z=12;\n" + "$AA=0.0526;\n"
        + "$R=100;\n" + "$d=0.00;\n" + "$vn=0;\n" + "$EAj=0;\n" + "$zj=0;\n" + "$sz=1;\n"
        + "FindRoot((($K*(1+p-$g)^($n/$Z))/(1+$AA))+(Sum((($R*(1+$d)^(Floor(i0/$Z)))/(1+$AA))*(1+p-$g)^(($n-i0-$vn)/$Z),{i0,0,$n-1}))+(Sum(($EAj*(1+p-$g)^(($n-$zj)/$Z))/(1+$AA),{j,1,$sz})) - 30199, {p, 0, 0.1})", //
        "{p->0.04999709393822403}");
    checkNumeric(
        "$K=10000;\n" + "$g=0.0;\n" + "$n=10*12;\n" + "$Z=12;\n" + "$AA=0.0526;\n" + "$res=15474;\n"
            + "FindRoot((($K*(1+p-$g)^($n/$Z))/(1+$AA)) - $res, {p, 0, 0.1})", //
        "{p->0.049993464334866594}");

    checkNumeric("Exp(3.4341896)", //
        "31.00627489594444");
    checkNumeric("Pi^3.0", //
        "31.006276680299816");
    // default to Newton method
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10})", //
        "{x->3.4341896575482007}");
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Method->Brent)", //
        "{x->3.434189629596888}");

    // only a start value is given:
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,3}, Method->Newton)", //
        "{x->3.4341896575482007}");

    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Method->Bisection)", //
        "{x->3.434189647436142}");
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Method->Brent)", //
        "{x->3.434189629596888}");
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Muller)", //
        "{x->3.4341896575483015}");
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Ridders)", //
        "{x->3.4341896575482007}");
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Secant)", //
        "{x->3.4341896575036097}");
    // FindRoot: maximal count (100) exceeded
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Method->RegulaFalsi, MaxIterations->100)", //
        "FindRoot(E^x==Pi^3,{x,1,10},Method->regulafalsi,MaxIterations->100)");
    // FindRoot: convergence failed
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Method->RegulaFalsi, MaxIterations->32000)", //
        "FindRoot(E^x==Pi^3,{x,1,10},Method->regulafalsi,MaxIterations->32000)");
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Illinois)", //
        "{x->3.4341896915055257}");
    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Pegasus)", //
        "{x->3.4341896575481976}");

    checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Brent)", //
        "{x->3.434189629596888}");
    check("FindRoot(Sin(x),{x,-0.5,0.5}, Secant)", //
        "{x->0.0}");

    check("FindRoot(Cos(x)==x,{x,0})", //
        "{x->0.739085}");
    check("FindRoot(Sin(x),{x,3})", //
        "{x->3.14159}");
    check("FindRoot(Sin(x),{x,6})", //
        "{x->6.28319}");
    check("FindRoot(Sin(x)==2,{x,I})", //
        "{x->1.5708+I*1.31696}");
    // TODO
    // check("FindRoot(Zeta(1/2 + I*t), {t, 12})", //
    // " ");
    // TODO solve linear equation
    // check("FindRoot({{1, 2}, {3, 4}} . x == {5, 6}, {x, {1, 1}})", //
    // " ");

  }

  @Test
  public void testFindSequenceFunction() {
    check(
        "FindSequenceFunction({0,1,2,9,44,265,1854,14833,133496,1334961,14684570,176214841,2290792932,32071101049,481066515734,7697064251745,130850092279664},n)", //
        "Subfactorial(n)");

    check("FindSequenceFunction({1, 1, 2, 2, 4, 2, 6, 4},n)", //
        "EulerPhi(n)");
    check("FindSequenceFunction({1, 1, 2, 2, 4, 2, 6, 2},n)", //
        "CarmichaelLambda(n)");

    check(
        "FindSequenceFunction({-3, -2, -1, -1, 0, 0, 1, 1, 1, 1, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5},n)", //
        "-3+PrimePi(n)");
    check("FindSequenceFunction({0, 1, 2, 2, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8},n)", //
        "PrimePi(n)");

    check("FindSequenceFunction({5, 6, 8, 10, 14, 16, 20, 22},n)", //
        "3+Prime(n)");
    check("FindSequenceFunction({2, 3, 5, 7, 11, 13, 17, 19},n)", //
        "Prime(n)");

    check("FindSequenceFunction({1, 2, 3, 5, 7, 11},n)", //
        "PartitionsP(n)");
    check("FindSequenceFunction({1, 1, 2, 2, 3, 4, 5, 6},n)", //
        "PartitionsQ(n)");
    check("FindSequenceFunction({1, 3, 4, 7, 11, 18, 29, 47, 76, 123, 199, 322},n)", //
        "LucasL(n)");
    check("FindSequenceFunction({1, 2, 5, 15, 52},n)", //
        "BellB(n)");
    check("FindSequenceFunction({1, 3, 6, 10, 15, 21},n)", //
        "1/2*n*(1+n)");
    check("FindSequenceFunction({18, 19, 22, 31, 59} ,n)", //
        "17+CatalanNumber(n)");
    check("FindSequenceFunction({1, 2, 5, 14, 42, 132, 429, 1430} ,n)", //
        "CatalanNumber(n)");
    check("FindSequenceFunction({1*3, 2*3, 6*3, 24*3, 120*3, 720*3, 5040*3} ,n)", //
        "3*n!");
    check("FindSequenceFunction({1, 2, 6, 24, 120}, n)", //
        "n!");
    check("FindSequenceFunction({1, 2, 3, 8, 15, 48, 105} , n)", //
        "n!!");
    check("FindSequenceFunction({1, 1, 2, 3, 5, 8, 13}, n)", //
        "Fibonacci(n)");
    check("FindSequenceFunction({6,18,54,162},n)", //
        "2*3^n");
    check("FindSequenceFunction({3,9,27,81},n)", //
        "3^n");
    check("FindSequenceFunction({7,9,11,13,15})", //
        "5+2*#1&");
    check("FindSequenceFunction({1,2,3,4,5})", //
        "#1&");
    check("FindSequenceFunction({1,2,3,4,5},n)", //
        "n");

  }

  @Test
  public void testFindRootMultivariate() {
    // https://en.wikipedia.org/wiki/Newton%27s_method#Example
    check("FindRoot({5*x1^2+x1*x2^2+Sin(2*x2)^2==2, Exp(2*x1-x2)+4*x2==3},{{x1, 1.0},{x2, 1.0}})", //
        "{x1->0.567297,x2->-0.309442}");

    // other multivariate cases
    check("FindRoot({2*x1+x2==E^(-x1), -x1+2*x2==E^(-x2)},{{x1, 0.0},{x2, 1.0}})", //
        "{x1->0.197594,x2->0.425514}");
    check(
        "FindRoot({Exp(-Exp(-(x1+x2)))-x2*(1+x1^2), x1*Cos(x2)+x2*Sin(x1)-0.5},{{x1, 0.0},{x2,0.0}})", //
        "{x1->0.353247,x2->0.606082}");

    check("FindRoot({Exp(x - 2) == y, y^2 == x}, {{x, 1}, {y, 1}})", //
        "{x->0.019026,y->0.137935}");
    check("FindRoot({y == E^x, x + y == 2}, {{x, 1}, {y, 1}})", //
        "{x->0.442854,y->1.55715}");
    check("FindRoot({Sin(x + y), Cos(x - y), x^2 + y^2 - z}, {{x, 1}, {y, 0}, {z, 0}})", //
        "{x->0.785398,y->-0.785398,z->1.2337}");
    check("FindRoot({Sin(x)==Cos(y), x+y==1}, {{x, 1}, {y, 1}})", //
        "{x->-1.85619,y->2.85619}");
  }

  @Test
  public void testFirst() {
    check("First(<||>)", //
        "First(<||>)");
    check("First(<|1 :> a, 2 -> b, 3 :> c|>)", //
        "a");
    check("First(Infinity)", //
        "1");
    check("First(ComplexInfinity)", //
        "First(ComplexInfinity)");
    check("First({a, b, c})", //
        "a");
    check("First(a + b + c)", //
        "a");
    check("First(a)", //
        "First(a)");
    check("First(a, b)", //
        "b");
    check("First({}, b)", //
        "b");
    check("First({a,b}, x)", //
        "a");
  }

  @Test
  public void testFirstCase() {
    check("FirstCase({a, 5, \\[Pi]}, _Symbol, Heads -> True)", //
        "List");

    check("FirstCase({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, f(x_) :> x)", //
        "a");
    check("FirstCase({a, b, c, 5, 6, 7}, _Integer)", //
        "5");
    check("FirstCase({1, 1, f(a), 2, 3, y, f[8], 9, f[10]}, Except(_Integer))", //
        "f(a)");
    check("FirstCase(<|1 -> \"a\", 2 -> \"b\", 3 -> c, 4 -> d|>, _Symbol)", //
        "c");
    check("FirstCase(<|1 -> 5, 2 -> <|3 -> 1, a -> b|>|>, _Symbol, Missing(), Infinity)", //
        "b");
    check("FirstCase({a, 5, \\[Pi]}, _Symbol, Heads -> True)", //
        "List");
    check("FirstCase({1, b -> Automatic, c -> 3},  _ -> Automatic )", //
        "Automatic");
    check("FirstCase({1, b -> Automatic, c -> 3}, HoldPattern(_ -> Automatic))", //
        "b->Automatic");
  }

  @Test
  public void testFirstPosition() {
    check("FirstPosition(x^2 + y^2, Power, Heads->False)", //
        "Missing(NotFound)");

    check("FirstPosition({a, b, a, a, b, c, b}, b)", //
        "{2}");
    check("FirstPosition({{a, a, b}, {b, a, a}, {a, b, a}}, b)", //
        "{1,3}");
    check("FirstPosition({1 + x^2, 5, x^4, a + (1 + x^2)^2}, x^_)", //
        "{1,2}");
    check(
        "FirstPosition(<|{1 -> 1 + x^2, 2 -> <|\"a\" -> x^2|>, 3 -> x^4, 4 -> a + (1 + x^2)^2}|>, x^_)", //
        "{Key(1),2}");
    check("FirstPosition(<|\"a\" -> 1, \"b\" -> 2, \"c\" -> 3, \"d\" -> 4|>, _Integer?PrimeQ)", //
        "{Key(b)}");
    check("FirstPosition({1, 2, 3}, _?StringQ, \"NoStrings\")", //
        "NoStrings");
    check("FirstPosition(x^2 + y^2, Power)", //
        "{1,0}");
    check("FirstPosition(Range(-1, 1, 0.05), 0.1)", //
        "Missing(NotFound)");
    check("FirstPosition(Range(-1, 1, 0.05), n_ /; n == 0.1)", //
        "{23}");
  }

  @Test
  public void testFit() {
    check("Fit({{-Pi,4}, {-Pi/2,0}, {0,1}, {Pi/2,-1}, {Pi,-4}}, {Sin(x/2), Sin(x), Sin(2*x)}, x)", //
        "-4.0*Sin(x/2)+2.32843*Sin(x)+Sin(2*x)");
    // TODO needs improvement
    check(
        "Fit({{-Pi,4}, {-Pi/2,0}, {0,1}, {Pi/2,-1}, {Pi,-4}}, {Sin(x/2), Sin(x), Sin(2*x), Sin(3*x)}, x)//Chop", //
        "83.5143*Sin(x/2)-186.4314*Sin(x)+3.57304*10^17*Sin(2*x)-83.12067*Sin(3*x)");

    check("Fit({1.2214,1.49182,1.82212,2.22554,2.71828,3.32012,4.0552},{2},x)", //
        "2.40778");
    check("Fit({1.2214,1.49182,1.82212,2.22554,2.71828,3.32012,4.0552},{2,x},x)", //
        "0.542903+0.46622*x");
    check("Fit({1.2214,1.49182,1.82212,2.22554,2.71828,3.32012,4.0552},{1,x,x^2},x)", //
        "1.09428+0.0986352*x+0.0459481*x^2");

    check("Fit({2,3,5,7,11,13},3,x)", //
        "6.83333");
    check("Fit({{1,1},{2,4},{3,9},{4,16}},2,x)", //
        "7.5");
    check("Fit({1,4,9,16},2,x)", //
        "7.5");
    check("Fit({1,4,9,16},{x^2},x)", //
        "x^2");

    check("Fit({x,-3,-1/2},2147483647,ComplexInfinity)", //
        "Fit({x,-3,-1/2},2147483647,ComplexInfinity)");
    check("Fit({1->0},1,x)", //
        "Fit({1->0},1,x)");
    check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, {1,x}, x)", //
        "0.186441+0.694915*x");
    check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, 1, x)", //
        "1.75");
    check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, {1,x,x^2}, x)", //
        "0.678392-0.266332*x+0.190955*x^2");
    check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, {1,x,x^2,x^4,x^5}, x)", //
        "1.0-1.96768*x+x^2-0.035533*x^4+0.00321489*x^5");

    check("Fit({{1,1},{2,4},{3,9},{4,16}},{1,x,x^2},x) // Chop", //
        "x^2");

  }

  @Test
  public void testFiveNum() {
    // example from https://en.wikipedia.org/wiki/Five-number_summary
    check("FiveNum({0, 0, 1, 2, 63, 61, 27, 13})", //
        "{0,1/2,15/2,44,63}");
    check("FiveNum({20,12,16,32,27,65,44,45,22,18})", //
        "{12,18,49/2,44,65}");
    check("FiveNum(SparseArray({20,12,16,32,27,65,44,45,22,18}))", //
        "{12,18,49/2,44,65}");
  }

  @Test
  public void testFixedPoint() {
    check("FixedPoint((# + 2/# )/2 &, 1`20, SameTest -> (Abs(#1 - #2) < 1*^-10 &))", //
        "1.4142135623730950488");
    check("FixedPoint((# + 2/# )/2 &, 1, SameTest -> (Abs(#1 - #2) < 1*^-10 &))", //
        "886731088897/627013566048");
    check("FixedPoint((# + 2/#)/2 &, 1, SameTest -> (Equal(N(#1), N(#2)) &))", //
        "1572584048032918633353217/1111984844349868137938112");

    // $IterationLimit: Iteration limit of 500 exceeded for FixedPoint(-1/2,14).
    check("FixedPoint(-1/2,14)", //
        "Hold(FixedPoint(-1/2,14))");

    check("FixedPoint(Cos, 1.0)", //
        "0.739085");
    check("FixedPoint(#+1 &, 1, 20)", //
        "21");
    check("FixedPoint(f, x, 0)", //
        "x");
    check("FixedPoint(f, x, -1)", //
        "FixedPoint(f,x,-1)");
    checkNumeric("FixedPoint(Cos, 1.0, Infinity)", //
        "0.7390851332151607");

    checkNumeric("FixedPoint((# + 2/#)/2 &, 1.)", //
        "1.414213562373095");
    check("FixedPoint(1 + Floor(#/2) &, 1000)", //
        "2");
    check("21!=0", //
        "True");
    check("{28, 21} /. {a_, b_}  -> {b, Mod(a, b)}", //
        "{21,7}");
    check("{28, 21} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", //
        "{21,7}");
    check("{21, 7} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", //
        "{7,0}");
    check("{7, 0} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", //
        "{7,0}");
    check("FixedPoint(# /. {a_, b_} /; b != 0 -> {b, Mod(a, b)} &, {28, 21})", //
        "{7,0}");
  }

  @Test
  public void testFixedPointList() {
    check("FixedPointList((# + 2/# )/2 &, 1`20, SameTest -> (Abs(#1 - #2) < 1*^-10 &))", //
        "{1,1.5,1.4166666666666666666,1.4142156862745098039,1.4142135623746899106,1.4142135623730950488}");

    check("FixedPointList(Function((# + 3/#)/2), 1.)", //
        "{1.0,2.0,1.75,1.73214,1.73205,1.73205,1.73205}");
    // message $IterationLimit: Iteration limit of 500 exceeded for FixedPointList(-1+I,{x,-2,3}).
    check("FixedPointList(-1+I,{x,-2,3})", //
        "Hold(FixedPointList(-1+I,{x,-2,3}))");
    check("FixedPointList(x_,Null)", //
        "Hold(FixedPointList(x_,Null))");

    // recursion or iteration limit exceeded
    check("FixedPointList(x^2,1.5707963267948966)", //
        "Hold(FixedPointList(x^2,1.5708))");

    // message: $IterationLimit: Iteration limit of 500 exceeded for FixedPointList(-1/2,14).
    check("FixedPointList(-1/2,14)", //
        "Hold(FixedPointList(-1/2,14))");
    check("FixedPointList(Cos, 1.0, 4)", //
        "{1.0,0.540302,0.857553,0.65429,0.79348}");
    checkNumeric("newton(n_) := FixedPointList(.5*(# + n/#) &, 1.);  newton(9)", //
        "{1.0,5.0,3.4,3.023529411764706,3.00009155413138,3.000000001396984,3.0,3.0}");

    // Get the "hailstone" sequence of a number:
    check("collatz(1) := 1", //
        "");
    check("collatz(x_ ? EvenQ) := x / 2", //
        "");
    check("collatz(x_) := 3*x + 1", //
        "");
    check("FixedPointList(collatz, 14)", //
        "{14,7,22,11,34,17,52,26,13,40,20,10,5,16,8,4,2,1,1}");

    check("FixedPointList(f, x, 0)", //
        "{x}");
    check("FixedPointList(f, x, -1) ", //
        "FixedPointList(f,x,-1)");
    check("Last(FixedPointList(Cos, 1.0, Infinity))", //
        "0.739085");
  }

  @Test
  public void testFlat() {
    System.out.println("");
    // see test https://github.com/mathics/Mathics/issues/747
    check("SetAttributes(eqv, Flat);eqv(p, q, q, p) /. eqv(x_, y_) :> {x, y}", //
        "{eqv(p),eqv(q,q,p)}");
    System.out.print(".");
    check("SetAttributes(f, Flat)", //
        "");
    System.out.print(".");
    check("f(a, b, c) /. f(a, b) -> d", //
        "f(d,c)");
    System.out.print(".");
    check("f(a, f(b, c))", //
        "f(a,b,c)");
    System.out.print(".");
    check("SetAttributes({u, v}, Flat)", //
        "");
    System.out.print(".");
    check("u(x_) := {x}", //
        "");
    System.out.print(".");
    check("u()", //
        "u()");
    // `Flat` is taken into account in pattern matching
    check("u(a)", //
        "{a}");
    System.out.print(".");
    // stack overflow?
    // check("u(a, b)", "Iteration limit of 500 exceeded.");
    // check("u(a, b, c)", "Iteration limit of 500 exceeded.");

    check("v(x_) := x   ", //
        "");
    System.out.print(".");
    check("v()", //
        "v()");
    System.out.print(".");
    check("v(a)", //
        "a");
    System.out.print(".");
    // iteration limit exceeded
    check("v(a, b)", //
        "Hold(v(a,b))");
    System.out.print(".");
    // Iteration limit
    check("v(a, b, c)", //
        "Hold(v(a,b,c))");
  }

  @Test
  public void testOnlyFlat() {
    // see test https://github.com/mathics/Mathics/issues/1485
    check("SetAttributes(fl, Flat)", //
        "");
    check("fl(fl(a, b), c)", //
        "fl(a,b,c)");
    check("fl(x_, x_) := fl(x)", //
        "");
    check("fl(b, b, b, c, c)", //
        "fl(b,c)");
    check("fl(a, a, a, b, b, b, c, c)", //
        "fl(a,b,c)");
  }

  @Test
  public void testFlatOrderlessOneIdentity() {
    check("pm(y_,x_+y_+z_):={x,y,z}", //
        "");
    check("pm(g,e+f+g+h+k)", //
        "{e,g,f+h+k}");

    // see github issue 89

    check("SetAttributes(oi, {OneIdentity})", //
        "");
    check("oi(p, q, r) /. {oi(x_,y_) :> {x,y}}", //
        "oi(p,q,r)");
    check("First@( oi(p, q, r) /. {oi(x_,y_) :> {x,y}} )", //
        "p");
    check("Rest@( oi(p, q, r) /. {oi(x_,y_) :> {x,y}} )", "oi(q,r)");

    check("SetAttributes(fo, {Flat, Orderless})", "");
    check("fo(p, q, r) /. {fo(x_,y_) :> {x,y}}", //
        "{p,fo(q,r)}");
    check("First@( fo(p, q, r) /. {fo(x_,y_) :> {x,y}} )", //
        "p");
    check("Rest@( fo(p, q, r) /. {fo(x_,y_) :> {x,y}} )", "{fo(q,r)}");

    check("Flatten(Table(Union(Sort/@Permutations({Flat,Orderless,OneIdentity}, {i})), {i,3}), 1)", //
        //
        "{{Flat}," //
            + "{OneIdentity}," //
            + "{Orderless}," //
            + "{Flat,OneIdentity}," //
            + "{Flat,Orderless}," //
            + "{OneIdentity,Orderless}," //
            + "{Flat,OneIdentity,Orderless}}");

    // Check when substitution is defined BEFORE setting attributes.
    check("Table(Module({ e = (eqv(p,q,r) /. {eqv(x_,y_) :> {x,y}}) }, ClearAll(eqv); " //
        + "SetAttributes(eqv,j); " //
        + "{j, First@e, Rest@e})," //
        + " {j, Flatten(Table(Union(Sort/@Permutations({Flat,Orderless,OneIdentity}, {i})), {i,3}), 1)})", //
        //
        "{{{Flat},p,eqv(q,r)}," //
            + "{{OneIdentity},eqv(p),{eqv(q,r)}}," //
            + "{{Orderless},p,eqv(q,r)}," //
            + "{{Flat,OneIdentity},p,eqv(q,r)}," //
            + "{{Flat,Orderless},p,{eqv(q,r)}}," //
            + "{{OneIdentity,Orderless},p,{eqv(q,r)}}," //
            + "{{Flat,OneIdentity,Orderless},p,eqv(q,r)}}");

    // Check when substitution is defined AFTER setting attributes.

    check("Table(Module({e},ClearAll(eqv);" //
        + "SetAttributes(eqv,j);" //
        + "e=(eqv(p,q,r)/.{eqv(x_,y_):>{x,y}});{j,First@e,Rest@e})," //
        + "{j,Flatten(Table(Union(Sort/@Permutations({Flat,Orderless,OneIdentity},{i})),{i,3}),1)})", //
        //
        "{{{Flat},eqv(p),{eqv(q,r)}}," //
            + "{{OneIdentity},p,eqv(q,r)}," //
            + "{{Orderless},p,eqv(q,r)}," //
            + "{{Flat,OneIdentity},p,{eqv(q,r)}}," //
            + "{{Flat,Orderless},p,{eqv(q,r)}}," //
            + "{{OneIdentity,Orderless},p,eqv(q,r)}," //
            + "{{Flat,OneIdentity,Orderless},p,{eqv(q,r)}}}");
  }

  @Test
  public void testFlatten() {

    // message Flatten: Level specification value greater equal 0 expected instead of -Infinity.
    check("Flatten(f(g(u, v), f(x, y)), -Infinity, f)", //
        "Flatten(f(g(u,v),f(x,y)),-Infinity,f)");



    check("Flatten(RandomReal(1, {3, 5, 7}), {{2, 3}, {1}})", //
        "Flatten(RandomReal(1,{3,5,7}),{{2,3},{1}})");

    check("Flatten(3.14)", //
        "Flatten(3.14)");
    // https://oeis.org/A049615
    check(
        "Table(Length(Select(Flatten(Table({x, y}, {x, 0, n - k}, {y, 0, k}), 1), GCD @@ # > 1 &)), {n, 0, 11}, {k, 0, n}) // Flatten", //
        "{0,0,0,1,0,1,2,1,1,2,3,2,3,2,3,4,3,4,4,3,4,5,4,6,6,6,4,5,6,5,7,8,8,7,5,6,7,6,9,9,\n"
            + "11,9,9,6,7,8,7,10,12,12,12,12,10,7,8,9,8,12,13,16,14,16,13,12,8,9,10,9,13,15,17,\n"
            + "18,18,17,15,13,9,10}");
    check("Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}})", //
        "{a,b,c,d,e,f,g,h}");
    check("Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}}, 1)", //
        "{a,b,c,{d},e,f,{g,h}}");
    check("Flatten(f(f(x, y), z))", //
        "f(x,y,z)");
    check("Flatten({0, {1}, {{2, -2}}, {{{3}, {-3}}}, {{{{4}}}}}, 0)", //
        "{0,{1},{{2,-2}},{{{3},{-3}}},{{{{4}}}}}");
    check("Flatten(f(g(u, v), f(x, y)), Infinity, g)", //
        "f(u,v,f(x,y))");
    check("Flatten(f(g(u, v), f(x, y)), Infinity, f)", //
        "f(g(u,v),x,y)");

    check("m = {{{1, 2}, {3}}, {{4}, {5, 6}}}", //
        "{{{1,2},{3}},{{4},{5,6}}}");
    // check("Flatten(m, {2})", "");
    // check("Flatten(m, {{2}})", "");
    // check("Flatten(m, {{2}, {1}})", "");
    // check("Flatten(m, {{2}, {1}, {3}})", "");
    // check("m = {{{1, 2}, {3}}, {{4}, {5, 6}}}", "");
    // check("m = {{{1, 2}, {3}}, {{4}, {5, 6}}}", "");
  }

  @Test
  public void testFlattenAt() {

    check("FlattenAt({a, {b, {c}}, d, {e}}, {{2},{4},{-3}})", //
        "{a,b,{c},d,e}");
    check("FlattenAt({a, {b, {c}}, d, {e}}, {{2},{4}})", //
        "{a,b,{c},d,e}");
    check("FlattenAt({a, {b, {c}}, d, {e}}, {{2, 2},{4},{2,2}})", //
        "{a,{b,c},d,e}");
    check("FlattenAt({a, {b, {c}}, d, {e}}, {{2, 2}, {4}})", //
        "{a,{b,c},d,e}");
    check("FlattenAt(2)[{a, {b, c}, {d, e}, {f}}]", //
        "{a,b,c,{d,e},{f}}");
    check("FlattenAt({a, {b, c}, {d, e}, {f}}, 2)", //
        "{a,b,c,{d,e},{f}}");
    check("FlattenAt({a, g(b,c), {d, e}, {f}}, 2)", //
        "{a,b,c,{d,e},{f}}");
    check("FlattenAt(f(a, g(b,c), {d, e}, {f}), -2)", //
        "f(a,g(b,c),d,e,{f})");
    check("FlattenAt(f(a, g(b,c), {d, e}, {f}), 4)", //
        "f(a,g(b,c),{d,e},f)");
    check("Table(FlattenAt(f({a}, {b}, {c}, {d}), i), {i, 4})", //
        "{f(a,{b},{c},{d}),f({a},b,{c},{d}),f({a},{b},c,{d}),f({a},{b},{c},d)}");

    check("FlattenAt(f(g(1, 2), g(3, 4), g(5)), {2})", //
        "f(g(1,2),3,4,g(5))");

    check("FlattenAt(f[a, g[b, c], d, {e}], {{2}, {4}})", //
        "f(a,b,c,d,e)");
    check("MapAt(Delete(0), f(a, g(b, c), d, {e}), {{2}, {4}})", //
        "f(a,b,c,d,e)");

    check("FlattenAt({1, {{2}, {3}}, 4}, {2})", //
        "{1,{2},{3},4}");
    check("MapAt(Flatten, {1, {{2}, {3}}, 4}, {2})", //
        "{1,{2,3},4}");

  }

  @Test
  public void testFloor() {
    check("Floor(Quantity(8.5, \"Meters\"))", //
        "8[Meters]");
    check("Floor(DirectedInfinity(0))", //
        "ComplexInfinity");
    check("Floor(DirectedInfinity((1/2-I*1/2)*Sqrt(2)))", //
        "DirectedInfinity((1/2-I*1/2)*Sqrt(2))");
    check("Floor(-Infinity)", //
        "-Infinity");
    check("Floor(Infinity)", //
        "Infinity");
    check("Floor(-9/4)", //
        "-3");
    check("Floor(1/3)", //
        "0");
    check("Floor(-1/3)", //
        "-1");
    check("Floor(10.4)", //
        "10");
    check("Floor(10/3)", //
        "3");
    check("Floor(10)", //
        "10");
    check("Floor(21, 2)", //
        "20");
    check("Floor(2.6, 0.5)", //
        "2.5");
    check("Floor(-10.4)", //
        "-11");
    check("Floor(1.5 + 2.7*I)", //
        "1+I*2");
    check("Floor(10.4, -1)", //
        "11");
    check("Floor(-10.4, -1) ", //
        "-10");

    check("Floor(1.5)", //
        "1");
    check("Floor(1.5 + 2.7*I)", //
        "1+I*2");
  }

  @Test
  public void testFold() {
    // message: Fold: Fold called with 5 arguments; 1 or 3 arguments are expected.
    check("Fold(f)[{a, b, c, d},x,y,z]", //
        "Fold(f)[{a,b,c,d},x,y,z]");
    check("Fold(f, {})", //
        "Fold(f,{})");
    check("Fold(test, t1, {a, b, c, d})", //
        "test(test(test(test(t1,a),b),c),d)");
    check("Fold(f, x, {a, b, c, d})", //
        "f(f(f(f(x,a),b),c),d)");
    check("Fold(f)[x,{a, b, c, d}]", //
        "f(f(f(f(x,a),b),c),d)");
    check("Fold(List, x, {a, b, c, d})", //
        "{{{{x,a},b},c},d}");
    check("Fold(Times, 1, {a, b, c, d})", //
        "a*b*c*d");
    check("Fold(#1^#2 &, x, {a, b, c, d})", //
        "(((x^a)^b)^c)^d");
    check("Catch(Fold(If(# > 10^6, Throw(#), #^2 + #1) &, 2, Range(6)))", //
        "3263442");
    check("Fold(g(#2, #1) &, x, {a, b, c, d})", //
        "g(d,g(c,g(b,g(a,x))))");
    check("Fold(x *#1 + #2 &, 0, {a, b, c, d, e})", //
        "e+x*(d+x*(c+x*(b+a*x)))");
    check("Fold(f)[{a, b, c, d}]", //
        "f(f(f(a,b),c),d)");
    check("Fold(Cross, {{1, -1, 1}, {0, 1, 1}, {1, 1, -1}})", //
        "{0,-1,-1}");
  }

  @Test
  public void testFoldList() {
    check("FoldList(f, e1, h(e2, e3, e4))", //
        "h(e1,f(e1,e2),f(f(e1,e2),e3),f(f(f(e1,e2),e3),e4))");

    // message: FoldList: FoldList called with 5 arguments; 1 or 3 arguments are expected.
    check("FoldList(f)[{a, b, c, d},x,y,z]", //
        "FoldList(f)[{a,b,c,d},x,y,z]");

    // A002110 Primorial numbers: product of first n primes.
    // https://oeis.org/A002110
    check("FoldList(Times, 1, Prime(Range(20)))", //
        "{1,2,6,30,210,2310,30030,510510,9699690,223092870,6469693230,200560490130,\n"
            + "7420738134810,304250263527210,13082761331670030,614889782588491410,\n"
            + "32589158477190044730,1922760350154212639070,117288381359406970983270,\n"
            + "7858321551080267055879090,557940830126698960967415390}");

    check("FoldList(f, {})", //
        "{}");
    check("FoldList(f,h,{})", //
        "{h}");
    check("FoldList(f, g(a))", //
        "g(a)");
    check("FoldList(f, {a, b, c, d})", //
        "{a,f(a,b),f(f(a,b),c),f(f(f(a,b),c),d)}");
    check("FoldList(Plus,{1,2,3})", //
        "{1,3,6}");

    check("foldlist(f, x, h())", //
        "h(x)");
    check("foldlist(f, x, h(c))", //
        "h(x,f(x,c))");
    check("foldlist(#^2 + #1 &, 2, range(6))", //
        "{2,6,42,1806,3263442,10650056950806,113423713055421844361000442}");
    check("foldlist(f, x, {a, b, c, d})", //
        "{x,f(x,a),f(f(x,a),b),f(f(f(x,a),b),c),f(f(f(f(x,a),b),c),d)}");
    // check("FoldList(Times, 1, Array(Prime, 10))", "");
    check("foldlist(1/(#2 + #1) &, x, reverse({a, b, c}))", //
        "{x,1/(c+x),1/(b+1/(c+x)),1/(a+1/(b+1/(c+x)))}");
    check("FoldList(f)[{a, b, c, d}]", //
        "{a,f(a,b),f(f(a,b),c),f(f(f(a,b),c),d)}");
  }

  @Test
  public void testFor() {
    check("For(n = 1, n < 1000, n++, If(PrimeQ(n) && (n > 7), Return())); n ", //
        "11");
    check("n := 1; For(i=1, i<=10, i=i+1, n = n * i);n", //
        "3628800");
    check("n==10!", //
        "True");
    check("n := 1;For(i=1, i<=10, i=i+1, If(i > 5, Return(i)); n = n * i)", //
        "6");
    check("n", //
        "120");

    check("For($i = 0, $i < 4, $i++, Print($i))", //
        "");
    check("For($i = 0, $i < 4, $i++)", //
        "");
    check("$i = 0;For($j = 0, $i < 4, $i++, Print($i));$i", //
        "4");
    check("$i = 0;For($j = 0, $i < 4, $i++);$i", //
        "4");
    check("$i = 0;For($j = 0, $i < 4, $i++)", //
        "");
    check("For($ = 1, $i < 1000, $i++, If($i > 10, Break())); $i", //
        "11");
    check(
        "For($t = 1; $k = 1, $k <= 5, $k++, $t *= $k; Print($t); If($k < 2, Continue()); $t += 2)", //
        "");
  }

  @Test
  public void testFractionalPart() {
    check("FractionalPart(Quantity(-1.1, \"Meters\"))", //
        "-0.1[Meters]");
    check("FractionalPart(235/47 + 53/10*I)", //
        "I*3/10");
    check("FractionalPart(235/47 + 5.3*I)", //
        "I*0.3");
    check("FractionalPart(ArcCosh(7/17))", //
        "-I+ArcCosh(7/17)");
    check("FractionalPart(ArcSinh(7/17))", //
        "ArcSinh(7/17)");

    check("FractionalPart(-Infinity)", //
        "Interval({-1,0})");
    check("FractionalPart(test)", //
        "FractionalPart(test)");
    check("FractionalPart({-2.4, -2.5, -3.0})", //
        "{-0.4,-0.5,0.0}");
    check("FractionalPart(14/32)", //
        "7/16");
    check("FractionalPart(4/(1 + 3 I))", //
        "2/5-I*1/5");
    check("FractionalPart(Pi^20)", //
        "-8769956796+Pi^20");
    check("FractionalPart(I*Infinity)", //
        "I*Interval({0,1})");
    check("FractionalPart(-I*Infinity)", //
        "-I*Interval({0,1})");
    check("FractionalPart(ComplexInfinity)", //
        "Interval({0,1})");

    check("FractionalPart(2.4+3.1*I)", //
        "0.4+I*0.1");
    check("FractionalPart(-5/3-(7/3)*I)", //
        "-2/3-I*1/3");

    check("FractionalPart(Cos(Pi/2))", //
        "0");
    check("FractionalPart(Sin(7/17))", //
        "Sin(7/17)");
    check("FractionalPart(Sin(-7/17))", //
        "-Sin(7/17)");

    check("FractionalPart(-Pi)", //
        "3-Pi");
    check("FractionalPart(GoldenRatio)", //
        "-1+GoldenRatio");
    check("FractionalPart(-9/4)", //
        "-1/4");
    check("FractionalPart(-9/4)+IntegerPart(-9/4)", //
        "-9/4");
    check("FractionalPart(-2.25)+IntegerPart(-2.25)", //
        "-2.25");
    check("FractionalPart(9/4)+IntegerPart(9/4)", //
        "9/4");
    check("FractionalPart(2.25)+IntegerPart(2.25)", //
        "2.25");
    check("FractionalPart(-2.25)+IntegerPart(-2.25)", //
        "-2.25");
    check("FractionalPart(0)+IntegerPart(0)", //
        "0");
    check("FractionalPart(0.0)+IntegerPart(0.0)", //
        "0.0");
    check("FractionalPart(1)+IntegerPart(1)", //
        "1");
    check("FractionalPart(1.0)+IntegerPart(1.0)", //
        "1.0");
    check("FractionalPart(-1)+IntegerPart(-1)", //
        "-1");
    check("FractionalPart(-1.0)+IntegerPart(-1.0)", //
        "-1.0");
    checkNumeric("FractionalPart(2.4)", //
        "0.3999999999999999");
    checkNumeric("FractionalPart(-2.4)", //
        "-0.3999999999999999");
    checkNumeric("FractionalPart({-2.4, -2.5, -3.0})", //
        "{-0.3999999999999999,-0.5,0.0}");
  }

  @Test
  public void testFreeQ() {
    // see notes for MemberQ
    check("FreeQ(a*x*Log(x), x*Log(x))", //
        "False");
    check("FreeQ(a*x*Log(x)+ b*(x*Log(x)),  x*Log(x))", //
        "False");
    check("FreeQ(Sin(x*y),Sin)", //
        "False");
    check("s=Sin;FreeQ(Sin(x*y),s)", //
        "False");
    check("FreeQ(x_+y_+z_)[a+b]", //
        "True");
    check("FreeQ(a + b + c, a + c)", //
        "False");
  }



  @Test
  public void testFrobeniusNumber() {
    // message FrobeniusNumber: The first argument {0,0,0,0} of FrobeniusNumber should be a
    // non-empty list of positive integers.
    check("FrobeniusNumber({0,0,0,0})", //
        "FrobeniusNumber({0,0,0,0})");

    check("FrobeniusNumber({1,-2,3})", //
        "FrobeniusNumber({1,-2,3})");
    check("FrobeniusNumber({ })", //
        "FrobeniusNumber({})");
    check("FrobeniusNumber({1->0})", //
        "FrobeniusNumber({1->0})");
    check("FrobeniusNumber({1000, 1476, 3764, 4864, 4871, 7773})", //
        "47350");
    check("FrobeniusNumber({12,16,20,27})", //
        "89");
    check("Table(FrobeniusNumber({i, i + 1}), {i, 15})", //
        "{-1,1,5,11,19,29,41,55,71,89,109,131,155,181,209}");
  }

  @Test
  public void testFrobeniusSolve() {
    // iter limit
    check("FrobeniusSolve({1,5,10,25},1317624576693539401)", //
        "Hold(FrobeniusSolve({1,5,10,25},1317624576693539401))");
    check("FrobeniusSolve({-1/2,-2,3},89)", //
        "FrobeniusSolve({-1/2,-2,3},89)");

    check("FrobeniusSolve({-1,-2,3}, 47349, 3)", //
        "FrobeniusSolve({-1,-2,3},47349,3)");
    check("FrobeniusSolve(Null, Null)", //
        "FrobeniusSolve(Null,Null)");
    check("FrobeniusSolve({1000, 1476, 3764, 4864, 4871, 7773}, 47349)", //
        "{{5,2,4,2,3,0},{6,1,0,4,1,2},{7,5,1,3,3,0},{15,9,3,0,0,1},{17,12,0,1,0,1}}");
    check("FrobeniusSolve({1000, 1476, 3764, 4864, 4871, 7773}, 47350)", //
        "{}");
    check("FrobeniusSolve({1000, 1476, 3764, 4864, 4871, 7773}, 47351)", //
        "{{1,2,3,3,2,1},{3,5,0,4,2,1},{9,3,1,0,3,2},{12,13,3,0,1,0},{14,16,0,1,1,0},{32,2,\n"
            + "2,0,1,0}}");
    check("FrobeniusSolve({2, 3, 4}, 29)", //
        "{{0,3,5},{0,7,2},{1,1,6},{1,5,3},{1,9,0},{2,3,4},{2,7,1},{3,1,5},{3,5,2},{4,3,3},{\n"
            + "4,7,0},{5,1,4},{5,5,1},{6,3,2},{7,1,3},{7,5,0},{8,3,1},{9,1,2},{10,3,0},{11,1,1},{\n"
            + "13,1,0}}");
    check("frobeniussolve({ 12, 16, 20, 27},123 )", //
        "{{0,1,4,1},{0,6,0,1},{1,4,1,1},{2,2,2,1},{3,0,3,1},{4,3,0,1},{5,1,1,1},{8,0,0,1}}");
    check("frobeniussolve({ 12, 16, 20, 27},89 )", //
        "{}");
    check("frobeniussolve({1, 5, 10, 25}, 42)", //
        "{{2,0,4,0},{2,1,1,1},{2,2,3,0},{2,3,0,1},{2,4,2,0},{2,6,1,0},{2,8,0,0},{7,0,1,1},{\n"
            + "7,1,3,0},{7,2,0,1},{7,3,2,0},{7,5,1,0},{7,7,0,0},{12,0,3,0},{12,1,0,1},{12,2,2,0},{\n"
            + "12,4,1,0},{12,6,0,0},{17,0,0,1},{17,1,2,0},{17,3,1,0},{17,5,0,0},{22,0,2,0},{22,\n"
            + "2,1,0},{22,4,0,0},{27,1,1,0},{27,3,0,0},{32,0,1,0},{32,2,0,0},{37,1,0,0},{42,0,0,\n"
            + "0}}");

    check("FrobeniusSolve({12, 16, 20, 27}, 123, 2)", //
        "{{0,1,4,1},{0,6,0,1}}");
  }

  @Test
  public void testFromCharacterCode() {
    check("FromCharacterCode({97,45,51})", //
        "a-3");
    // undefined negative codes
    check("FromCharacterCode(-42)", //
        "FromCharacterCode(-42)");
    check("FromCharacterCode({65,-10,67})", //
        "FromCharacterCode({65,-10,67})");
    check("FromCharacterCode({65,66,67,68,32,97,98,99,100})", //
        "ABCD abcd");
    check("ToCharacterCode(\"ABCD abcd\")", //
        "{65,66,67,68,32,97,98,99,100}");
  }

  @Test
  public void testFromContinuedFraction() {
    // check(
    // "Sqrt(63)/3", //
    // "Sqrt(7)");
    check("Sqrt(20541813482020954028041088271392963168222371159)/4727827637485585077281", //
        "Sqrt(919)");
    check("FromContinuedFraction({1, 4, 2, {3, 1}})", //
        "1/238*(287+Sqrt(21))");
    // check(
    //
    // "FromContinuedFraction({30,{3,5,1,2,1,2,1,1,1,2,3,1,19,2,3,1,1,4,9,1,7,1,3,6,2,11,1,1,1,29,1,1,1,11,2,6,3,1,7,1,9,4,1,1,3,2,19,1,3,2,1,1,1,2,1,2,1,5,3,60}})
    // ", //
    // "Sqrt(919)");

    check("FromContinuedFraction({1, 2, 3, 4, 5})", //
        "225/157");
    check("FromContinuedFraction({-2, 1, 9, 7, 1, 2})", //
        "-256/233");
    check("FromContinuedFraction({2, 1, 2, 1, 1, 4, 1, 1, 6, 1, 1, 8}) // N", //
        "2.71828");
    check("FromContinuedFraction({{1}}) ", //
        "1/2*(1+Sqrt(5))");

    check("FromContinuedFraction({0,-7,{-1,-1,-18,-1,-1,-9,-76,-9}})", //
        "2/11*(1-Sqrt(3))");
    check("FromContinuedFraction({-16,{-1,-1,-2,-1,-1,-32}})", //
        "-5*Sqrt(11)");
    check("FromContinuedFraction({-8,{-2,-1,-2,-1,-2,-16}})", //
        "-Sqrt(70)");

    check("FromContinuedFraction({a/b,{1,2,13,2,2,1,54}})", //
        "-15617/578+Sqrt(256224053)/578+a/b");
    check("FromContinuedFraction({27,{1,2,2,13,2,2,1,54}})", //
        "16*Sqrt(3)");
    check("FromContinuedFraction({8,{2,1,2,1,2,16}})", //
        "Sqrt(70)");
    check("FromContinuedFraction({16,{1,1,2,1,1,32}})", //
        "5*Sqrt(11)");
    check("FromContinuedFraction({0,1,1,1,{2}})", //
        "1/7*(3+Sqrt(2))");
    check("FromContinuedFraction({3,{1,1,1,1,6}})", //
        "Sqrt(13)");
    check("FromContinuedFraction({0,1,{8,3,34,3}})", //
        "1/5*(1+2*Sqrt(3))");
    check("FromContinuedFraction({8,{2,1,2,1,2,16}})", //
        "Sqrt(70)");
    check("FromContinuedFraction({0,1,{108,2,4,4,4,2}})", //
        "1/11*(1+7*Sqrt(2))");

    check("FromContinuedFraction({1,1,1,1,1})", //
        "8/5");
    check("FromContinuedFraction({2,3,4,5})", //
        "157/68");
    check("ContinuedFraction(157/68)", //
        "{2,3,4,5}");
  }

  @Test
  public void testFromDigits() {

    // https://oeis.org/A023391
    check("NestList(FromDigits(IntegerDigits(#, 8), 9) &, 8, 50)", //
        "{8,9,10,11,12,13,14,15,16,18,20,22,24,27,30,33,37,41,46,51,57,64,81,100,121,145,\n"
            + "181,221,275,345,433,541,761,1036,1471,2014,2787,3927,5533,8537,13555,21441,34102,\n"
            + "60891,103386,185033,329032,651411,1286139,2551404,5654254}");

    check("FromDigits({0})", //
        "0");
    check("FromDigits({1,2,3})", //
        "123");
    check("FromDigits({1,1,1,1,0,1,1}, 2)", //
        "123");
    check("FromDigits /@ IntegerDigits(Range(-10, 10))", //
        "{10,9,8,7,6,5,4,3,2,1,0,1,2,3,4,5,6,7,8,9,10}");
    check("FromDigits(\"123\")", //
        "123");
    check("FromDigits(\"1111011\", 2)", //
        "123");
    check("FromDigits(\"0\")", //
        "0");
    check("FromDigits(\"789ABC\")", //
        "790122");
    check("FromDigits(\"789ABC\", 16)", //
        "7903932");
    check("FromDigits(\"789abc\")", //
        "790122");
    check("FromDigits(\"789abc\", 16)", //
        "7903932");
    check("FromDigits(\"1A3C\")", //
        "2042");
  }

  @Test
  public void testFromRomanNumeral() {
    check("FromRomanNumeral(\"MDCLXVI\")", //
        "1666");
    // TODO add message for invalid roman number
    check("FromRomanNumeral(\"MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\")", //
        "36000");
    // zero as special case represented by 'N'
    check("FromRomanNumeral(\"N\")", //
        "0");
    check("FromRomanNumeral(\"MCMXXXVII\")", //
        "1937");
    check("FromRomanNumeral(\"MMMDCCXLVIII\")", //
        "3748");
    check(
        "FromRomanNumeral({\"ci\", \"cii\", \"ciii\", \"civ\", \"cv\", \"cvi\", \"cvii\", \"cviii\"})", //
        "{101,102,103,104,105,106,107,108}");
  }

  @Test
  @Ignore("Definition based on HashMap, which will change it's order if new symbols are defined")
  public void testFullDefinition() {
    check("FullDefinition(ArcSinh)", //
        "Attributes(ArcSinh)={Listable,NumericFunction,Protected}\n" //
            + "\n" //
            + "ArcSinh(I/Sqrt(2))=I*1/4*Pi\n" //
            + "\n" //
            + "ArcSinh(Undefined)=Undefined\n" //
            + "\n" //
            + "ArcSinh(Infinity)=Infinity\n" //
            + "\n" //
            + "ArcSinh(I*Infinity)=Infinity\n" //
            + "\n" //
            + "ArcSinh(I)=I*1/2*Pi\n" //
            + "\n" //
            + "ArcSinh(0)=0\n" //
            + "\n" //
            + "ArcSinh(I*1/2)=I*1/6*Pi\n" //
            + "\n" //
            + "ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi\n" //
            + "\n" //
            + "ArcSinh(ComplexInfinity)=ComplexInfinity");

    check("a(x_):=b(x,y);b[u_,v_]:={{u,v},a}", //
        "");
    check("a(test)", //
        "{{test,y},a}");
    check("FullDefinition(a)", //
        "a(x_):=b(x,y)\n" //
            + "\n" //
            + "b(u_,v_):={{u,v},a}");
  }

  @Test
  public void testFullForm() {
    check("N( 1/2+8 ,12)//FullForm", //
        "8.5`");
    check("N( Pi/13^101 ,30)//FullForm", //
        "9.74700726352830962565070727338`30*^-113");
    check("N(Sin(Pi/7),30)//FullForm", //
        "4.33883739117558120475768332848`30*^-1");
    check("Association(A->1,B->2,C->3,D->4) // FullForm", //
        "Association(Rule(A, 1), Rule(B, 2), Rule(C, 3), Rule(D, 4))");
    check("( _. ) // FullForm", //
        "Optional(Blank())");
    check("Optional(Blank())", //
        "_.");
    check("( _:2 ) // FullForm", //
        "Optional(Blank(), 2)");
    check("Optional(Blank(),2)", //
        "_:2");
    check("( x_. ) // FullForm", //
        "Optional(Pattern(x, Blank()))");
    check("Optional(Pattern(x, Blank()))", //
        "x_.");
    check("( x_:2 ) // FullForm", //
        "Optional(Pattern(x, Blank()), 2)");
    check("Optional(Pattern(x,Blank()),2)", //
        "x_:2");

    check("Hold(Function(#[[1]]*#[[2]])[SignOfFactor(Map(NormalizeSumFactors,u))]) // FullForm", //
        "Hold(Function(Times(Part(Slot(1), 1), Part(Slot(1), 2)))[signoffactor(Map(normalizesumfactors, u))])");
    check("FullForm(a:=b)", "Null");
  }

  @Test
  public void testFullSimplify() {
    check("FullSimplify(Mod(Mod(a, c) + Mod(b*q, c) + f(x), c))", //
        "Mod(a+b*q+f(x),c)");

    check("FullSimplify(a*Conjugate(a))", //
        "Abs(a)^2");
    check("FullSimplify( 3*E^(-x)+7*E^x )", //
        "10*Cosh(x)+4*Sinh(x)");
    check("Simplify( 3*E^(-x)+7*E^x )", //
        "3/E^x+7*E^x");


    // https://github.com/axkr/symja_android_library/issues/856
    check("FullSimplify((4+3*Sqrt(2)+Sqrt(3)+Sqrt(6))/(2+Sqrt(2)+Sqrt(3)))", //
        "1+Sqrt(2)");

    check("FullSimplify((Cosh(x) - 1)/(Cosh(x) + Sinh(x))-(1 - Exp(-x))^2/2)", //
        "0");
    check("FullSimplify(Sign(z)*Abs(z))", //
        "z");
    check("FullSimplify( Sqrt(9-4*Sqrt(5)))", //
        "-2+Sqrt(5)");

    check("FullSimplify(Gamma(z) / Gamma(z-2))", //
        "2-3*z+z^2");
    check("FullSimplify( Sqrt(-9+4*Sqrt(5)))", //
        "I*(-2+Sqrt(5))");


    // MMA factorizes this to (-2 + x)*(-1 + x) ? Although the ComplexityFunction returns 9 for both
    // expressions
    check("2-3*x+x^2 // FullSimplify", //
        "2-3*x+x^2");
    // MMA doesn't factorize this
    check("f(2-3*Sin(x)+Sin(x)^2) // FullSimplify", //
        "f(2-3*Sin(x)+Sin(x)^2)");

    check("FullSimplify(x^2,ComplexInfinity)", //
        "x^2");
    // see Logarithms#test0128() Rubi rule 2447 ==> -(Sqrt(-(e/d))/(2*e))
    check(
        "FullSimplify( (d-2*d*Sqrt(-e/d)*x-e*x^2)/(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2) )", //
        "1/(2*d*Sqrt(-e/d))");
    check(
        "PolynomialQuotientRemainder((d-2*d*Sqrt(-e/d)*x-e*x^2),(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2),x)",
        //
        "{1/(2*d*Sqrt(-e/d)),0}");

    // check("FullSimplify((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / "
    //
    // + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) +
    // (2*(d*Sqrt(-(e/d)) + e*x))/(d +
    // e*x^2)))", //
    // "(d-2*d*Sqrt(-e/d)*x-e*x^2)/(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2)");
    check("FullSimplify((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / " //
        + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) +   (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))", //
        "-Sqrt(-e/d)/(2*e)");

    check(
        "FullSimplify(Numerator((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / " //
            + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) +   (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2))))", //
        "1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2)");
    check(
        "FullSimplify(Denominator((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / "
            //
            + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) + (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2))))", //
        "(d*(2*d*Sqrt(-e/d)+4*e*x-2*e*Sqrt(-e/d)*x^2))/(d+e*x^2)");
    check("Together( 1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2) )", //
        "(d-2*d*Sqrt(-e/d)*x-e*x^2)/(d+e*x^2)");
    check("Together( (d*(2*d*Sqrt(-e/d)+4*e*x-2*e*Sqrt(-e/d)*x^2))/(d+e*x^2) )", //
        "(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2)/(d+e*x^2)");

    // #github #152
    check("FullSimplify(Sqrt(-9-4*Sqrt(5)))", //
        "I*(2+Sqrt(5))");
    // check("FullSimplify( Sqrt(9-4*Sqrt(5)))", //
    // "-2+Sqrt(5)");

    check("FullSimplify(Sqrt(9+4*Sqrt(5)))", //
        "2+Sqrt(5)");
    check("FullSimplify(-Sqrt(9-4*Sqrt(5))+Sqrt(9+4*Sqrt(5)))", //
        "4");
    check("-Sqrt(9-4*Sqrt(5))+Sqrt(9+4*Sqrt(5.0))", //
        "4.0");
    check("FullSimplify(f(x*y,-Sqrt(9-4*Sqrt(5))+Sqrt(9+4*Sqrt(5)),Sqrt(-9-4*Sqrt(5))))", //
        "f(x*y,4,I*(2+Sqrt(5)))");

    check("FullSimplify(Sqrt(2) + Sqrt(3) - Sqrt(5 + 2*Sqrt(6)))", //
        "0");
    check("FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
        "True");

    // check("Factor((d^2+2*d*e*x^2+e^2*x^4))",//
    // "(d+e*x^2)^2");
    // check("Together(D( 1-(2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2),x))",//
    // "(-2*d^2*Sqrt(-e/d)-4*d*e*x+2*d*e*Sqrt(-e/d)*x^2)/(d^2+2*d*e*x^2+e^2*x^4)");
    check("FullSimplify(D( 1-(2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2),x))", //
        "(-2*d*Sqrt(-e/d)-4*e*x+2*e*Sqrt(-e/d)*x^2)/(d+2*e*x^2)");

    check("p = Expand((x + 1)^2 (x + 2)^2 (x + 3)^3)", //
        "108+432*x+711*x^2+625*x^3+318*x^4+94*x^5+15*x^6+x^7");
    check("FullSimplify(p)", //
        "(1+x)^2*(2+x)^2*(3+x)^3");
    check("FullSimplify(Cosh(x)/(b*Cosh(x)+c*Sinh(x)))", //
        "1/(b+c*Tanh(x))");
    check("FullSimplify((b*Cosh(x)+c*Sinh(x))/Cosh(x))", //
        "b+c*Tanh(x)");
    check("Simplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
        "-ChebyshevT(n,x)+Cos(n*ArcCos(x))==0");
    // FullSimplify uses FunctionExpand and can test the equation:
    check("FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
        "True");
    check("FullSimplify(Cosh(x)+Sinh(x))", //
        "E^x");
  }

  @Test
  public void testFullSimplifyIssue856() {
    // github issue #856
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) * (Sqrt(2) + 4))", //
        "7*Sqrt(6)");
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) * (-Sqrt(2) - 4) )", //
        "-7*Sqrt(6)");
    check("FullSimplify( (2 *Sqrt(6) - Sqrt(3)) / (Sqrt(2) - 4) )", //
        "-Sqrt(3/2)");
  }

  @Test
  public void testFunction() {
    EvalEngine.resetModuleCounter4JUnit();
    // check("(p + #) & /. p -> q", //
    // "q+#1&");
    // check("fufufu=Function({x},Function({y},Function({z},x+y+z)))", //
    // "Function({x},Function({y},Function({z},x+y+z)))");

    // message: Parameter specification f(x) in Function(f(x),g(a)) should be a symbol or a list of
    // symbols.
    check("Function(f(x), g(a))", //
        "Function(f(x),g(a))");

    // Function: Function called with 4 arguments; between 1 and 3 arguments are expected.
    check("Function(a,b,c,d)", //
        "Function(a,b,c,d)");
    check("g = (#1 + #2) &[3,4]", //
        "7");

    check("(4*# + (2*# &)[a] &)[b]", //
        "2*a+4*b");
    check("fufufu=Function({x},Function({y},Function({z},x+y+z)))", //
        "Function({x},Function({y},Function({z},x+y+z)))");
    check("fufufu(a)", //
        "Function({y$1},Function({z$1},a+y$1+z$1))");
    check("fufufu(y)", //
        "Function({y$2},Function({z$2},y+y$2+z$2))");
    check("fufufu(y)[z]", //
        "Function({z$3$4},y+z+z$3$4)");
    check("fufufu(y)[x][z]", //
        "x+y+z");

    check("Function({x, y}, x^2 + y^3)[a, b]", //
        "a^2+b^3");
    check("f(x, ##, y, ##) &(a, b, c, d)", //
        "f(x,a,b,c,d,y,a,b,c,d)");
    check("f(x, ##2, y, ##3) &(a, b, c, d)", //
        "f(x,b,c,d,y,c,d)");
    check("If(# > 5, #, False) &(2)", //
        "False");
    check("{##} &(a, b, c)", //
        "{a,b,c}");
    check("{##2} &(a, b, c)", //
        "{b,c}");
    check("Table(a(i0, j), ##) & @@ {{i0, 3}, {j, 2}}", //
        "{{a(1,1),a(1,2)},{a(2,1),a(2,2)},{a(3,1),a(3,2)}}");
    check("Map(Function(-#),-z+w)", //
        "-w+z");
    check("f(#1) &(x, y, z)", //
        "f(x)");
    check("17 & /@ {1, 2, 3}", //
        "{17,17,17}");
    check("(p + #) & /. p -> q", //
        "q+#1&");
    check("FullForm(x -> y &)", //
        "Function(Rule(x, y))");
    check("FullForm(x -> (y &))", //
        "Rule(x, Function(y))");
    check("FullForm(Mod(#, 5) == 1 &)", //
        "Function(Equal(Mod(Slot(1), 5), 1))");
    check("FullForm(a == b && c == d &)", //
        "Function(And(Equal(a, b), Equal(c, d)))");
    check("FullForm(Mod(#, 3) == 1 && Mod(#, 5) == 1 &)", //
        "Function(And(Equal(Mod(Slot(1), 3), 1), Equal(Mod(Slot(1), 5), 1)))");

    check("({#1,Plus(##2)}&) @@@(Range/@Range(2,3))", //
        "{{1,2},{1,5}}");
    check("(#[[1]]+#[[2]]&) /@{{1,2},{3,4,5},{6,7}}", //
        "{3,7,13}");
    check("((#+##&) @@#&) /@{{1,2},{2,2,2},{3,4}}", //
        "{4,8,10}");

    check("Function({x,y},x y)[2,3]", //
        "6");
    check("Function(x,2 x)[5]", //
        "10");
    check("(Function@@{{x},x==2})[2]", //
        "True");

    // new lambda operator |->
    check("Array(x |-> 1+x^2, 10)", //
        "{2,5,10,17,26,37,50,65,82,101}");
    check("(x |-> {#1, x}) /@ #2 &[n0, {n1, n2, n3, n4, n5}]", //
        "{{n0,n1},{n0,n2},{n0,n3},{n0,n4},{n0,n5}}");

    check("Array(x \\[Function] 1+x^2, 10)", //
        "{2,5,10,17,26,37,50,65,82,101}");
    check("(x \\[Function] {#1, x}) /@ #2 &[n0, {n1, n2, n3, n4, n5}]", //
        "{{n0,n1},{n0,n2},{n0,n3},{n0,n4},{n0,n5}}");

    check("h := Function({x}, Hold(1+x))", //
        "");
    check("h(1 + 1)", //
        "Hold(1+2)");

    check("h := Function({x}, Hold(1+x), HoldAll)", //
        "");
    check("h(1 + 1)", //
        "Hold(1+1+1)");

  }

  @Test
  public void testFunctionDomain() {
    check("FunctionDomain(ArcCoth(2+3*x)*ArcSec(7+2/3*x), x)", //
        "x<=-12||(-9<=x&&x<-1)||x>-1/3");
    check("FunctionDomain(ArcSin(2+3*x), x)", //
        "-1<=x&&x<=-1/3");
    check("FunctionDomain(Sqrt(2+3*x), x)", //
        "x>=-2/3");
    check("FunctionDomain(Sqrt(-2-3*x), x)", //
        "x<=-2/3");
    check("FunctionDomain((x^2+x+1)/(-7+x^2), x)", //
        "x<-Sqrt(7)||(-Sqrt(7)<x&&x<Sqrt(7))||x>Sqrt(7)");
    check("FunctionDomain((x^2+x+1)/x, x)", //
        "x<0||x>0");
    check("FunctionDomain(1/(x^2), x)", //
        "x<0||x>0");
    check("FunctionDomain(Sqrt(-3-x), x)", //
        "x<=-3");
    check("FunctionDomain(Sqrt(x-3), x)", //
        "x>=3");
    check("FunctionDomain(Sqrt(x+3), x)", //
        "x>=-3");
    check("FunctionDomain((x+3)^-3, x)", //
        "x<-3||x>-3");
    check("FunctionDomain((x+3)^3, x)", //
        "True");
    check("FunctionDomain((2+x)^(-3), x)", //
        "x<-2||x>-2");
    check("FunctionDomain(Gamma(-3*x+2), x)", //
        "2-3*x∉Integers&&x<2/3");
    check("FunctionDomain(ArcCosh(x-1), x)", //
        "x>=2");
    check("FunctionDomain(Tan(3+x^2), x)", //
        "1/2+(3+x^2)/Pi∉Integers");
    check("FunctionDomain(Log(x-1), x)", //
        "x>1");

    check("FunctionDomain(ArcCos(x)*Log(x), x)", //
        "0<x&&x<=1");
    check("FunctionDomain(ArcSec(2+x), x)", //
        "x<=-3||x>=-1");
    check("FunctionDomain(Tan(3+x), x)", //
        "1/2+(3+x)/Pi∉Integers");
    check("FunctionDomain(x/(x^4 - 1), x)", //
        "x<-1||(-1<x&&x<1)||x>1");
    check("FunctionDomain(x/(x^4 - 1)+Tan(2+x), x)", //
        "(1/2+(2+x)/Pi∉Integers&&x<-1)||(1/2+(2+x)/Pi∉Integers&&-1<x&&x<1)||(1/2+(2+x)/Pi∉Integers&&x>\n"
            + "1)");
    // message FunctionDomain: Unable to find the domain with the available methods.
    check("FunctionDomain(Log(Tan(x) - 1), x)", //
        "FunctionDomain(Log(-1+Tan(x)),x)");
  }

  @Test
  public void testFunctionPeriod() {
    check("FunctionPeriod(E^Sin(x), x)", //
        "2*Pi");
    check("FunctionPeriod(Sin(x), x)", //
        "2*Pi");
    // TODO implement for multiple variables
    check("FunctionPeriod(Cos(x) + Sin(y), {x, y})", //
        "FunctionPeriod(Cos(x)+Sin(y),{x,y})");
  }

  @Test
  public void testFunctionRange() {
    check("FunctionRange(Sin(x)*Cos(x),x,y)", //
        "-1<=y<=1");
    // TODO
    // check("FunctionRange(Sqrt(x^2 - 1)/x, x, y)", //
    // "");

    // check(
    // "FunctionRange(E^x, x, y)", //
    // "y>0");

    check("FunctionRange(LogIntegral(a), a, b)", //
        "True");

    check("FunctionRange(x^2-1, x, y)", //
        "y>=-1");
    check("FunctionRange(x/(1 + x^2), x, y)", //
        "-1/2<=y<=1/2");

    check("FunctionRange(1/(1 + x^2), x, y)", //
        "0<=y<=1");
    check("FunctionRange(Sqrt(Sin(2*x)),x,y)", //
        "0<=y<=1");

    check("FunctionRange(Sin(x),x,y)", //
        "-1<=y<=1");
    check("FunctionRange(Cos(x),x,y)", //
        "-1<=y<=1");
  }

  @Test
  public void testFunctionURL() {
    assertEquals(ID.LINE_NUMBER_OF_JAVA_CLASS.length, ID.ZTransform + 1);
    checkRegex("FunctionURL(NIntegrate)", //
        "^.+L?\\d$"
    // "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NIntegrate.java#L771"
    );
  }


  @Test
  public void testGather() {
    check("Gather({1, 7, 3, 7, 2, 3, 9})", //
        "{{1},{7,7},{3,3},{2},{9}}");
    check("Gather({1/3, 2/6, 1/9})", //
        "{{1/3,1/3},{1/9}}");

    check("Gather({{a, 1}, {b, 1}, {a, 2}, {d, 1}, {b, 3}}, (First(#1) == First(#2)) &)", //
        "{{{a,1},{a,2}},{{b,1},{b,3}},{{d,1}}}");
    check("Gather({1,2,3,2,3,4,5,6,2,3})", //
        "{{1},{2,2,2},{3,3,3},{4},{5},{6}}");
    check("Gather(Range(0, 3, 1/3), Floor(#1) == Floor(#2) &)", //
        "{{0,1/3,2/3},{1,4/3,5/3},{2,7/3,8/3},{3}}");
  }

  @Test
  public void testGlaisher() {
    check("Glaisher // N", //
        "1.28243");
    check("N(Glaisher,50)", //
        "1.282427129100622636875342568869791727767688927325");
  }

  @Test
  public void testGatherBy() {
    check("GatherBy({{1, 2}, {2, 1}, {3, 5}, {5, 1}, {2, 2, 2}}, {})", //
        "{{{1,2}},{{2,1}},{{3,5}},{{5,1}},{{2,2,2}}}");
    check("GatherBy({{1, 3}, {2, 2}, {1, 1}}, Total)", //
        "{{{1,3},{2,2}},{{1,1}}}");
    check("GatherBy({\"xy\", \"abc\", \"ab\"}, StringLength)", //
        "{{xy,ab},{abc}}");
    check("GatherBy({{2, 0}, {1, 5}, {1, 0}}, Last)", //
        "{{{2,0},{1,0}},{{1,5}}}");
    check("GatherBy({{1, 2}, {2, 1}, {3, 5}, {5, 1}, {2, 2, 2}}, {Total, Length})", //
        "{{{{1,2},{2,1}}},{{{3,5}}},{{{5,1}},{{2,2,2}}}}");

    check("rr=Range(10); GatherBy(rr, OddQ)", // x = Range[5000]; First[Timing[GatherBy[x, OddQ]]]
        "{{1,3,5,7,9},{2,4,6,8,10}}");
    check("GatherBy({{a,10},{b,5},{a,7},{b,3},{b,10}}, First)", //
        "{{{a,10},{a,7}},{{b,5},{b,3},{b,10}}}");
    check("Tuples({{a, b}, {1, 2}, {x, y}})", //
        "{{a,1,x},{a,1,y},{a,2,x},{a,2,y},{b,1,x},{b,1,y},{b,2,x},{b,2,y}}");
    check(
        "GatherBy({{a,1,x},{a,1,y},{a,2,x},{a,2,y},{b,1,x},{b,1,y},{b,2,x},{b,2,y}}, {First,Last})", //
        "{{{{a,1,x},{a,2,x}},{{a,1,y},{a,2,y}}},{{{b,1,x},{b,2,x}},{{b,1,y},{b,2,y}}}}");
  }



  @Test
  public void testGCD() {
    // Long.MIN_VALUE = -9223372036854775808
    check("-9223372036854775808/2", //
        "-4611686018427387904");
    check("-9223372036854775808/7", //
        "-9223372036854775808/7");
    check("GCD(-9223372036854775808,-9223372036854775808)", //
        "9223372036854775808");
    // Integer.MIN_VALUE = -2147483648
    check("-2147483648/2", //
        "-1073741824");
    check("GCD(-2147483648,-2147483648)", //
        "2147483648");

    check("GCD(0,b)", //
        "GCD(0,b)");
    check("GCD(a,a)", //
        "GCD(a,a)");
    check("GCD(a,b)", //
        "GCD(a,b)");
    check("GCD(3/7,12/22)", //
        "3/77");
    check("GCD(5+3*I, 2-8*I)", //
        "1+I");
    check("GCD(5+3*I, 2+8*I)", //
        "5+I*3");
    check("GCD(1+5*I, 3+2*I)", //
        "3+I*2");
    check("GCD(5+I, 2+3*I)", //
        "2+I*3");
    check("GCD(1,I)", //
        "1");
    check("GCD(I,I)", //
        "1");
    check("GCD(-I,-I)", //
        "1");
    check("GCD(-1/2, 5)", //
        "1/2");
    check("GCD(0,Cos(b*x)[[2]])", //
        "GCD(0,Cos(b*x)[[2]])");
    check("GCD(0, CoshIntegral(b*x))", //
        "GCD(0,CoshIntegral(b*x))");
    check("GCD(x,x)", //
        "GCD(x,x)");
    check("GCD(-2147483648)", //
        "2147483648");
    check("GCD(-2147483648, -2147483648/2)", //
        "1073741824");
    check("GCD(I)", //
        "1");
    check("GCD(-I)", //
        "1");

    check("GCD()", //
        "0");
    check("GCD(10)", //
        "10");
    check("GCD(2, 3, 5)", //
        "1");
    check("GCD(1/3, 2/5, 3/7)", //
        "1/105");
    check("GCD(-3, 9)", //
        "3");
    check("GCD(b, a)", //
        "GCD(a,b)");

    check("GCD(20, 30)", //
        "10");
    check("GCD(-20, 30)", //
        "10");
    check("GCD(20, -30)", //
        "10");
    check("GCD(-20, -30)", //
        "10");
    check("GCD(10, y)", //
        "GCD(10,y)");
    check("GCD(4, {10, 11, 12, 13, 14})", //
        "{2,1,4,1,2}");
  }

  @Test
  public void testGeometricMean() {
    checkNumeric("GeometricMean(13.261197054679151) // N", //
        "GeometricMean(13.261197054679151)");
    checkNumeric("GeometricMean({1, 2.0, 3, 4})", //
        "2.213363839400643");
    check("GeometricMean({Pi,E,2})", //
        "(2*E*Pi)^(1/3)");
    check("GeometricMean({1, 2, 3, 4,5, 6, 7})", //
        "2^(4/7)*3^(2/7)*35^(1/7)");

    check("GeometricMean({})", //
        "GeometricMean({})");
    check("GeometricMean({2, 6, 5, 15, 10, 1})", //
        "3^(1/3)*Sqrt(10)");
    check("GeometricMean({{5, 10}, {2, 1}, {4, 3}, {12, 15}})", //
        "{2*30^(1/4),2^(1/4)*Sqrt(15)}");
    checkNumeric("GeometricMean(N({2, 6, 5, 15, 10, 1}))", //
        "4.56079359657056");
  }

  @Test
  public void testGeoDistance() {
    // distance between Oslo and Berlin
    check("GeoDistance({59.914, 10.752}, {52.523, 13.412})", //
        "521.4299[mi]");
    check("UnitConvert(GeoDistance({59.914, 10.752}, {52.523, 13.412}),\"km\")", //
        "839.1601[km]");

    check("GeoDistance({37, -109}, {40.113, -88.261})", //
        "1140.843[mi]");
    check("GeoDistance({30, 40}, {-40, 120})", //
        "7031.637[mi]");
  }

  @Test
  public void testGet() {
    if (Config.FILESYSTEM_ENABLED) {
      // message: Get: Cannot open noopen-file-test.m.
      check("Get(\"noopen-file-test.m\")", //
          "True");

      String pathToVectorAnalysis =
          LowercaseTestCase.class.getResource("/symja/VectorAnalysis.m").getFile();
      // remove 'file:/'
      // pathToVectorAnalysis = pathToVectorAnalysis.substring(6);
      System.out.println(pathToVectorAnalysis);
      // PatternMatching.getFile(pathToVectorAnalysis, engine)
      evalString("Get(\"" + pathToVectorAnalysis + "\")");
      check("DotProduct({a,b,c},{d,e,f}, Spherical)", //
          "a*d*Cos(b)*Cos(e)+a*d*Cos(c)*Cos(f)*Sin(b)*Sin(e)+a*d*Sin(b)*Sin(c)*Sin(e)*Sin(f)");
      // check("Information(Sin)", "");
      // check("Information(DotProduct)", "");
    }
  }

  @Test
  public void testGoldenAngle() {
    // check("IntegerPart[GoldenAngle^100]", //
    // "");
    check("NumericQ(GoldenAngle)", //
        "True");
    check("N(GoldenAngle)", //
        "2.39996");
    check("Attributes(GoldenAngle)", //
        "{Constant,Protected}");
    check("FunctionExpand(GoldenAngle)", //
        "(3-Sqrt(5))*Pi");
  }

  @Test
  public void testGoldenRatio() {
    check("N(GoldenRatio)", //
        "1.61803");
    check("Log(GoldenRatio)", //
        "ArcCsch(2)");
  }



  @Test
  public void testGroebnerBasis() {
    check("GroebnerBasis({x*y-2*y, 2*y^2-x^2}, {y, x})", //
        "{-2*x^2+x^3,-2*y+x*y,-x^2+2*y^2}");
    check("GroebnerBasis({x*y-2*y, 2*y^2-x^2}, {x, y})", //
        "{-2*y+y^3,-2*y+x*y,x^2-2*y^2}");
    check(
        "GroebnerBasis({-5*x^2+y*z-x-1, 2*x+3*x*y+y^2,x-3*y+x*z-2*z^2},{x,y,z}, MonomialOrder ->DegreeReverseLexicographic)", //
        "{x-3*y+x*z-2*z^2,2*x+3*x*y+y^2,1+x+5*x^2-y*z,-1+27*y+5*y^2-z-29*y*z+18*z^2+y*z^2-\n"
            + "20*z^3,6-156*y-20*y^2+6*z+174*y*z+y^2*z-104*z^2+120*z^3,180-20*x-4185*y-559*y^2+\n"
            + "15*y^3+162*z+4680*y*z-2808*z^2+3240*z^3,4026-20*x-106386*y-17140*y^2+4086*z+\n"
            + "114129*y*z-70866*z^2+78768*z^3+1560*z^4}");
    check("GroebnerBasis({x^2 - 2*y^2, x*y - 3}, {x, y})", //
        "{-9+2*y^4,3*x-2*y^3}");
    check("GroebnerBasis({x + y, x^2 - 1, y^2 - 2*x}, {x, y})", //
        "{1}");
    check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x*y - z + 2, z^2 - 2*x + 3*y}, {x, y, z})", //
        "{1024-832*z-215*z^2+156*z^3-25*z^4+24*z^5+13*z^6+z^8,-11552+2560*y+2197*z+2764*z^\n"
            + "2+443*z^3+728*z^4+169*z^5+32*z^6+13*z^7,-34656+5120*x+6591*z+5732*z^2+1329*z^3+\n"
            + "2184*z^4+507*z^5+96*z^6+39*z^7}");
    check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x*y - z + 2}, {x, y, z})", //
        "{4-y^2+y^4-4*z+z^2+y^2*z^2,-2*x-y+y^3+x*z+y*z^2,2+x*y-z,-1+x^2+y^2+z^2}");
    check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x*y - z + 2, z^2 - 3 + x,x - y^2 + 1}, {x, y, z})", //
        "{1}");
  }

  @Test
  public void testGudermannian() {
    check("N(Gudermannian(4/3), 50)", //
        "1.0553273957593967167251201219876788679534384811898");
    check("N(Gudermannian(I*Pi+5), 50)", //
        "1.584272016863742216280221584803507365335847712497");
    check("Gudermannian(6/4*Pi*I)", //
        "-I*Infinity");
    check("Gudermannian(3.75)", //
        "1.52377");
    check("Gudermannian(1.111111)", //
        "0.934757");
    check("Gudermannian(100.0)", //
        "1.5708");
    check("Gudermannian(I*Pi+5.0)", //
        "1.58427");
  }

  @Test
  public void testHarmonicMean() {
    checkNumeric("(1.01646-I*1.0)^(-1)", //
        "0.4999333728092652+I*0.49183772387429436");
    checkNumeric("HarmonicMean({-3.1415,2.987,-1,1})", //
        "242.94266666666653");
    checkNumeric("HarmonicMean({-3.1415,2.987,I,1})", //
        "1.9997333373775112+I*1.9673414730736105");
    check("HarmonicMean({-3.1415,2.987,-1,0,1})", //
        "0");
    check("HarmonicMean({{-3.1415,2.987,-1,0,1}})", //
        "{-3.1415,2.987,-1,0,1}");
    check("HarmonicMean({1, 2, 3, 4,5, 6, 7})", //
        "980/363");
    check("HarmonicMean({a,b,c,d})", //
        "4/(1/a+1/b+1/c+1/d)");
    check("HarmonicMean({{1, 2}, {5, 10}, {5, 2}, {4, 8}})", //
        "{80/33,160/49}");
  }



  @Test
  public void testHaversine() {
    check("N(Haversine(1/7), 50)", //
        "0.0050933697766924586521369314932831270629836495502145");
    check("N(Haversine(798+I), 50)", //
        "-0.27105513255154801719412701505423122282395758411135+I*0.02083546725316235662066240547292017854082982767312");
    checkNumeric("Haversine(1.5)", //
        "0.46463139916614854");
    checkNumeric("Haversine(0.5 + 2 * I)", //
        "-1.1508186664570472+I*0.8694047522371582");
    checkNumeric("Haversine(0.5)", //
        "0.06120871905481362");
    checkNumeric("Haversine(1.5+I)", //
        "0.44542339697277356+I*0.5861286494553962");
    check("Haversine(Pi/3)", //
        "1/4");
    check("Haversine(90*Degree)", //
        "1/2");
    check("Haversine({0, Pi/4, Pi/3, Pi/2})", //
        "{0,1/2*(1-1/Sqrt(2)),1/4,1/2}");
  }

  @Test
  public void testHead() {
    check("Head(f(a, b))", //
        "f");
    check("Head(a + b + c)", //
        "Plus");
    check("Head(a / b)", //
        "Times");
    check("Head(45)", //
        "Integer");
    check("Head(x)", //
        "Symbol");
    check("Head(f(x)[y][z])", //
        "f(x)[y]");
    check("Head({3, 4, 5})", //
        "List");
    check("FixedPoint(Head, f(x)[y][z])", //
        "Symbol");
    check("FixedPoint(Head, {3, 4, 5})", //
        "Symbol");

    check("Head(a * b)", //
        "Times");
    check("Head(6)", //
        "Integer");
    check("Head(x)", //
        "Symbol");
  }

  @Test
  public void testHeavisideLambda() {
    // TODO implement for multiple args
    check("HeavisideLambda(1/4, 1/3, 1/8)", //
        "HeavisideLambda(1/8,1/4,1/3)");
    check("HeavisideLambda(0.8,0.6)", //
        "HeavisideLambda(0.6,0.8)");


    check("Table(HeavisideLambda(n), {n, -1, 1, 1/3})", //
        "{0,1/3,2/3,1,2/3,1/3,0}");
    check("FunctionExpand(HeavisideLambda(x))", //
        "-HeavisideTheta(-1+x)+x*HeavisideTheta(-1+x)-2*x*HeavisideTheta(x)+HeavisideTheta(\n" //
            + "1+x)+x*HeavisideTheta(1+x)");
    check("HeavisideLambda(D(Sin(x),x))", //
        "HeavisideLambda(D(Sin(x),x))");
    check("Derivative(1)[HeavisideLambda]", //
        "-HeavisidePi(1/2-#1)+HeavisidePi(1/2+#1)&");
    check("HeavisideLambda(x)", //
        "HeavisideLambda(x)");
    check("D(HeavisideLambda(x), x)", //
        "-HeavisidePi(1/2-x)+HeavisidePi(1/2+x)");
    check("HeavisideLambda(1/2)", //
        "1/2");
    check("HeavisideLambda(1)", //
        "0");
    check("HeavisideLambda(-1)", //
        "0");
    check("HeavisideLambda(-1/3)", //
        "2/3");
    check("HeavisideLambda({1.6, 0.330000000000000000000000})", //
        "{0,0.67}");
    check("HeavisideLambda({-1/4, 1/2, 1})", //
        "{3/4,1/2,0}");

  }

  @Test
  public void testHeavisidePi() {
    check("HeavisidePi(D(Sin(x),x))", //
        "HeavisidePi(Cos(x))");
    check("Derivative(1)[HeavisidePi]", //
        "-2*DiracDelta(-1+2*#1)+2*DiracDelta(1+2*#1)&");
    check("HeavisidePi(x)", //
        "HeavisidePi(x)");
    check("D(HeavisideTheta(x), x)", //
        "DiracDelta(x)");
    check("HeavisidePi(1/2)", //
        "HeavisidePi(1/2)");
    check("HeavisidePi(42)", //
        "0");
    check("HeavisidePi(-1)", //
        "0");
    check("HeavisidePi(-42)", //
        "0");
    check("HeavisidePi({1.6, 0.330000000000000000000000})", //
        "{0,1}");
    check("HeavisidePi({-1/4, 1/2, 1})", //
        "{1,HeavisidePi(1/2),0}");
    check("HeavisidePi(1/4, 1/3, 1/8)", //
        "1");
    check("HeavisidePi(-2, -1, 1, 2)", //
        "0");
  }

  @Test
  public void testHeavisideTheta() {
    check("HeavisideTheta(D(Sin(x),x))", //
        "HeavisideTheta(Cos(x))");
    check("Derivative(1)[HeavisideTheta]", //
        "DiracDelta(#1)&");
    check("HeavisideTheta(x)", //
        "HeavisideTheta(x)");
    check("D(HeavisideTheta(x), x)", //
        "DiracDelta(x)");
    check("HeavisideTheta(0)", //
        "HeavisideTheta(0)");
    check("HeavisideTheta(42)", //
        "1");
    check("HeavisideTheta(-1)", //
        "0");
    check("HeavisideTheta(-42)", //
        "0");
    check("HeavisideTheta({1.6, 1.6000000000000000000000000})", //
        "{1,1}");
    check("HeavisideTheta({-1, 0, 1})", //
        "{0,HeavisideTheta(0),1}");
    check("HeavisideTheta(1, 2, 3)", //
        "1");
    check("HeavisideTheta(-2, -1, 1, 2)", //
        "0");
  }

  @Test
  public void testHold() {
    check("Hold(1<2<3!=a<2==3) // FullForm", //
        "Hold(Inequality(1, Less, 2, Less, 3, Unequal, a, Less, 2, Equal, 3))");
    check("Hold(3*2)", //
        "Hold(3*2)");
    check("Hold(2+2)", //
        "Hold(2+2)");
    check("lst = Hold(1 + 2, 2*3*4*5, 1/0, Quit())", //
        "Hold(1+2,2*3*4*5,1/0,Quit())");
    check("Apply(List, Map(Hold, lst))", //
        "{Hold(1+2),Hold(2*3*4*5),Hold(1/0),Hold(Quit())}");
    check("expr = Hold({1 + 2, g(3 + 4, 2*3), f(1 + g(2 + 3))})", //
        "Hold({1+2,g(3+4,2*3),f(1+g(2+3))})");
    check("pos = Position(expr, _Plus)", //
        "{{1,1},{1,2,1},{1,3,1,2,1},{1,3,1}}");
    check("val = Extract(expr, pos)", //
        "{3,7,5,1+g(5)}");
    check("ReplacePart(expr, Thread(pos -> val))", //
        "Hold({3,g(7,2*3),f(1+g(5))})");
    check("Hold(6/8)==6/8", //
        "Hold(6/8)==3/4");
  }

  @Test
  public void testHoldComplete() {

    check("HoldComplete(Evaluate(a+a),2+2,Sequence(a,b))", //
        "HoldComplete(Evaluate(a+a),2+2,Sequence(a,b))");
    check("g /: HoldComplete(g(x_)) := x", //
        "");
    check("HoldComplete(g(1))", //
        "HoldComplete(g(1))");
    check("HoldComplete(f(1 + 2)) /. f(x_) :> g(x)", //
        "HoldComplete(g(1+2))");
    check("ReleaseHold(HoldComplete(Sequence(1, 2)))", //
        "Identity(1,2)");

    check("g /: Hold(g(x_)) := x", //
        "");
    check("Hold(g(1))", //
        "1");
  }

  @Test
  public void testHoldAllComplete() {
    check("ClearAll(fump); " //
        + "SetAttributes(fump, HoldAllComplete);" //
        + "fump(e_) := (Print(ToString(Unevaluated(e)) <> \" ~~>\\n\" <> ToString(e)); e); " //
        + "fump((42 + 3)*6)", //
        "270");
  }

  @Test
  public void testHoldForm() {
    check("HoldForm(3*2)", //
        "3*2");
    check("HoldForm(6/8)==6/8", //
        "6/8==3/4");
  }

  @Test
  public void testHoldPattern() {

    check("a + b /. HoldPattern(_ + _) -> 0", //
        "0");

    check("MatchQ(And(x, y, z), Times(p__))", //
        "True");

    check("HoldPattern( 1/(sq(a_)*sq(b_)) ) //FullForm", //
        "HoldPattern(Power(Times(sq(Pattern(a, Blank())), sq(Pattern(b, Blank()))), -1))");
    check(" 1/(sq(a_)*sq(b_)) //FullForm", //
        "Times(Power(sq(Pattern(a, Blank())), -1), Power(sq(Pattern(b, Blank())), -1))");

    check("HoldPattern( Sqrt(2*Pi*x_) )", //
        "HoldPattern(Sqrt(2*Pi*x_))");
    check("MatchQ(And(x, y, z), p__)", //
        "True");
    // because of OneIdentity attribute for Times
    check("MatchQ(And(x, y, z), Times(p__))", //
        "True");
    check("Times(p__)===And(p__)", //
        "True");
    check("MatchQ(And(x, y, z), HoldPattern(Times(p__)))", //
        "False");
    check("HoldPattern(Times(p__))===HoldPattern(And(p__))", //
        "False");
    check("And(x, y, z)/.HoldPattern(And(a__)) ->List(a)", //
        "{x,y,z}");
    check("And(x, y, z)/.And->List", //
        "{x,y,z}");
    check("And(x, y, z)/.And(a_,b___)->List(a,b)", //
        "{x,y,z}");

    check("a + b /. HoldPattern(_ + _) -> 0", //
        "0");
    check("MatchQ(Log(a, b), HoldPattern(Log(_)/Log(_)))", //
        "True");
    check("Cases({a -> b, c -> d}, HoldPattern(a -> _))", //
        "{a->b}");
  }

  @Test
  public void testHorner() {
    check("Horner(1/Sqrt(5))", //
        "1/Sqrt(5)");
  }

  @Test
  public void testHornerForm() {
    check("HornerForm(x^(1/3) + x + x^(3/2))", //
        "x^(1/3)+(1+Sqrt(x))*x");
    check("HornerForm(1/Sqrt(5))", //
        "1/Sqrt(5)");
    check("HornerForm(#2)", //
        "#2");
    check("Horner(Infinity)", //
        "Infinity");

    check("HornerForm(11*x^3 - 4*x^2 + 7*x + 2)", //
        "2+x*(7+x*(-4+11*x))");
    check("HornerForm(a+b*x+c*x^2,x)", //
        "a+x*(b+c*x)");
  }

  @Test
  public void testHurwitzLerchPhi() {
    check("HurwitzLerchPhi(-1,1,1)", //
        "Log(2)");
    check("HurwitzLerchPhi(-1,-1,1)", //
        "1/4");
    check("HurwitzLerchPhi(2,1,1)", //
        "-I*1/2*Pi");


    check("Table(HurwitzLerchPhi(z , 1, 1), {z, -1, 2})", //
        "{Log(2),1,Infinity,-I*1/2*Pi}");
  }

  @Test
  public void testHurwitzZeta() {
    checkNumeric("N(HurwitzZeta(1/3, 8/7), 50)", //
        "-1.1389367444490991746548674334535727810961919460755");
    checkNumeric("HurwitzZeta(2.3000000000000000000000000, 48)", //
        "0.0050854686158964511510171449");

    // https://github.com/mtommila/apfloat/issues/34
    // checkNumeric("HurwitzZeta(-9223372036854775808/11,-0.8+I*1.2)", //
    // "Overflow()");
    checkNumeric("HurwitzZeta(2.2,3.1)", //
        "0.26067453797192913");


    check("HurwitzZeta(3,-4)", //
        "ComplexInfinity");
    checkNumeric("HurwitzZeta(-2.0,0.5+I*0.5)", //
        "I*0.08333333333333333");
    check("HurwitzZeta(7,5.0)", //
        "0.0000184949");

    checkNumeric("HurwitzZeta(2147483647,3.141592653589793)", //
        "0.0");
    check("HurwitzZeta(1.5708,1317624576693539401)", //
        "7.95681*10^-11");
    check("HurwitzZeta(3,0.2)", //
        "125.739");
    check("HurwitzZeta(.51, .87)", //
        "-1.32016");
    check("Table(HurwitzZeta(x, 0.5+I*0.5), {x,-2.0,2,0.25})", //
        "{I*0.0833333,0.0371452+I*0.0888619,0.080604+I*0.0797892,0.125994+I*0.0515943,"//
            + "0.166667,0.192966+I*(-0.0787252),0.190832+I*(-0.187445),0.138749+I*(-0.327822),"//
            + "I*(-0.5),-0.300858+I*(-0.702294),-0.972875+I*(-0.930936),-3.02493+I*(-1.17988),"//
            + "ComplexInfinity,4.69929+I*(-1.70242),2.46351+I*(-1.95199),1.49152+I*(-2.17411),"//
            + "0.783802+I*(-2.35189)}");
    check("Table(HurwitzZeta(x, 0.5), {x,-2.0,2,0.25})", //
        "{0.0,0.00695768,0.0164748,0.0283452,0.0416667,0.0541783,0.0608885,0.0509849,0.0," //
            + "-0.153878,-0.604899,-2.34624,ComplexInfinity,6.33397,4.77654,4.63811,4.9348}");

    check("D(HurwitzZeta(s, x), x)", //
        "-s*HurwitzZeta(1+s,x)");
    check("HurwitzZeta(20,a) // FunctionExpand", //
        "PolyGamma(19,a)/121645100408832000");

    // http://fungrim.org/entry/6e69fc/
    check("HurwitzZeta(6,11)", //
        "-52107472322919827957/51219253009612800000+Pi^6/945");
    check("HurwitzZeta(-4,42)", //
        "-24607093");

    // http://fungrim.org/entry/af23f7/
    check("HurwitzZeta(s,1)", //
        "Zeta(s)");
    // http://fungrim.org/entry/b721b4/
    check("HurwitzZeta(s,2)", //
        "-1+Zeta(s)");
    // http://fungrim.org/entry/af7d3d/
    check("HurwitzZeta(s,1/2)", //
        "(-1+2^s)*Zeta(s)");
    check("HurwitzZeta(3,1/2)", //
        "7*Zeta(3)");
    // http://fungrim.org/entry/951f86/
    check("HurwitzZeta(2,3/4)", //
        "-8*Catalan+Pi^2");

    // http://fungrim.org/entry/7dab87/
    check("HurwitzZeta(-9, 0)", //
        "-1/132");
    check("HurwitzZeta(-10, 0)", //
        "0");
    check("HurwitzZeta(-11, 0)", //
        "691/32760");

    // http://fungrim.org/entry/532f31/
    check("HurwitzZeta(1,a)", //
        "ComplexInfinity");
    // http://fungrim.org/entry/d99808/
    check("HurwitzZeta(0,a)", //
        "1/2-a");
  }

  @Test
  public void testHyperfactorial() {
    check("Hyperfactorial(-1/2)", //
        "Glaisher^(3/2)/(2^(1/24)*E^(1/8))");
    check("Hyperfactorial(10)", //
        "215779412229418562091680268288000000000000000");
    check("Hyperfactorial(-7)", //
        "-4031078400000");
    check("Hyperfactorial(-8)", //
        "3319766398771200000");
  }

  @Test
  public void testHypergeometric0F1() {
    check("N(Hypergeometric0F1(1, -2), 50)", //
        "-0.19654809527046820004079337208793223132588978731089");
    check("Hypergeometric0F1(1, -2.00000000000000000000000000000)", //
        "-0.1965480952704682000407933720879");

    check("Hypergeometric0F1(b, 0)", //
        "1");
    check("Hypergeometric0F1(b, Infinity)", //
        "ComplexInfinity");

    check("Hypergeometric0F1(1/2, z)", //
        "Cosh(2*Sqrt(z))");
    check("Hypergeometric0F1(1/2, -a)", //
        "Cos(2*Sqrt(a))");
    check("Hypergeometric0F1(3/2, z)", //
        "Sinh(2*Sqrt(z))/(2*Sqrt(z))");
    check("Hypergeometric0F1(3/2, -a)", //
        "Sin(2*Sqrt(a))/(2*Sqrt(a))");

    check("Hypergeometric0F1({1, 2, 3}, 1.5)", //
        "{3.16559,1.96279,1.60374}");
    check("Hypergeometric0F1(1,-2.0)", //
        "-0.196548");
    checkNumeric("Hypergeometric0F1(1,1.5)", //
        "3.1655890675997247");
  }

  @Test
  public void testHypergeometric0F1Regularized() {
    checkNumeric("Hypergeometric0F1Regularized(0., E)", //
        "8.522277545659726");
    check("Hypergeometric0F1Regularized(b, 0)", //
        "1/Gamma(b)");
    check("N(Hypergeometric0F1Regularized(0, -48), 50)", //
        "-0.75356407978144308380327864211537660841009027837077");
  }

  @Test
  public void testHypergeometric1F1() {
    checkNumeric("Hypergeometric1F1(1,1/2,z)", //
        "1+E^z*Sqrt(Pi)*Sqrt(z)*Erf(Sqrt(z))");
    checkNumeric("Hypergeometric1F1(1,{2,3,4},5.0)", //
        "{29.482631820515323,11.393052728206127,6.235831636923677}");
    check("Hypergeometric1F1(3,b,z)", //
        "1/2*(-1+b)*(4-b+z+(2-b)*(3-b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))+2*(3-b)*E^z*z^(\n"
            + "2-b)*(Gamma(-1+b)-Gamma(-1+b,z))+E^z*z^(3-b)*(Gamma(-1+b)-Gamma(-1+b,z)))");
    check("Hypergeometric1F1(a,a+1,z)", //
        "(a*(Gamma(a,0)-Gamma(a,-z)))/(-z)^a");
    check("Hypergeometric1F1(1,a+1,z)", //
        "(a*E^z*(Gamma(a)-Gamma(a,z)))/z^a");
    check("Hypergeometric1F1(a-1, a, z)", //
        "(-1+a)*(-z)^(1-a)*(Gamma(-1+a,0)-Gamma(-1+a,-z))");
    check("Hypergeometric1F1(a, a - 1, z)", //
        "(E^z*(-1+a+z))/(-1+a)");
    check("Hypergeometric1F1({0,0,0},a,{{0,0},{0,0},0})", //
        "{{1,1},{1,1},1}");
    checkNumeric("Hypergeometric1F1(-0.5, 1.0 / 3.0, -1)", //
        "2.269314995817225");
    // assertThat(Maja.hypergeo1F1(-0.5, 1.0 / 3.0, -1)).isEqualTo(2.269314995817403);
    check("N(Hypergeometric1F1(10, 1/3, -1), 50)", //
        "1.0856469662771144181060999200053894821341819655655");
    check("Hypergeometric1F1(10, 1/3, -1.000000000000000000000000000000000000)", //
        "1.0856469662771144181060999200053894821");

    check("Hypergeometric1F1(-2,-1-a,-a)", //
        "1+(2*a)/(-1-a)+a/(1+a)");
    check("Hypergeometric1F1(-3,b,z)", //
        "1+(-3*z)/b+(3*z^2)/(b*(1+b))-z^3/(b*(1+b)*(2+b))");
    check("Hypergeometric1F1(-2,b,z)", //
        "1+(-2*z)/b+z^2/(b*(1+b))");
    // slow
    // check("Hypergeometric1F1(-9223372036854775808/11,{2,3,4},0.5)", //
    // "{Hypergeometric1F1(-8.38488*10^17,2.0,0.5),Hypergeometric1F1(-8.38488*10^17,3.0,0.5),Hypergeometric1F1(-8.38488*10^17,4.0,0.5)}");
    // TODO check wrong
    check("Hypergeometric1F1(3,Quantity(1.2,\"m\"),-1+I)", //
        "Hypergeometric1F1(3,1.2[m],-1+I)");
    check("Hypergeometric1F1(2 + I, {2,3,4}, 0.5)", //
        "{1.61833+I*0.379258,1.391+I*0.228543,1.28402+I*0.161061}");

    check("Hypergeometric1F1(1,b,z)", //
        "(-1+b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))");
    check("Hypergeometric1F1(2,b,z)", //
        "(-1+b)*(1+(2-b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))+E^z*z^(2-b)*(Gamma(-1+b)-Gamma(-\n"
            + "1+b,z)))");
    check("Hypergeometric1F1(-2,-1,0)", //
        "1");
    check("Hypergeometric1F1(-2,-1,z)", //
        "ComplexInfinity");
    check("Hypergeometric1F1(-2,-7,z)", //
        "1+2/7*z+z^2/42");
    check("Hypergeometric1F1(-2,0,z)", //
        "ComplexInfinity");
    check("Hypergeometric1F1(a,a,z)", //
        "E^z");
    check("Hypergeometric1F1(0,1,z)", //
        "1");
    check("Hypergeometric1F1(a,1,z)  // FunctionExpand", //
        "LaguerreL(-a,z)");
    check("Hypergeometric1F1(3,1,z)", //
        "Hypergeometric1F1(3,1,z)");
    check("Hypergeometric1F1(-1,b,z)", //
        "1-z/b");
    check("Hypergeometric1F1(-1,2,3.0)", //
        "-0.5");
    check("Hypergeometric1F1(1,2,3.0)", //
        "6.36185");

  }

  @Test
  public void testHypergeometric1F1Regularized() {
    // TODO interrupt long running calculations
    // check("Hypergeometric1F1Regularized(-9223372036854775808/11,-0.8,-11)", //
    // "");

    check("N(Hypergeometric1F1Regularized(2,-4,2), 50)", //
        "1891.5983613262464581709894299072020001741607860612");
    check("N(Hypergeometric1F1Regularized(1, 0, 1), 30)", //
        "2.71828182845904523536028747135");

    checkNumeric("Hypergeometric1F1Regularized(7, 23, 0.5)", //
        "1.0370581075059291E-21");
    check("Hypergeometric1F1Regularized(a, b, 0)", //
        "1/Gamma(b)");
    check("Hypergeometric1F1Regularized(a, a, z)", //
        "E^z/Gamma(a)");
  }

  @Test
  public void testHypergeometric2F1() {
    // https://dlmf.nist.gov/15.4
    check("Hypergeometric2F1(a,b,1/2*a+1/2*b+1/2, 1/2)", //
        "(Sqrt(Pi)*Gamma(1/2+a/2+b/2))/(Gamma(1/2+a/2)*Gamma(1/2+b/2))");
    check("Hypergeometric2F1(1,a,a+1,-1)", //
        "1/2*a*(PolyGamma(0,1/2+a/2)-PolyGamma(0,a/2))");
    check("Hypergeometric2F1(a,b,a-b+1, -1)", //
        "(Sqrt(Pi)*Gamma(1+a-b))/(2^a*Gamma(1/2+a/2)*Gamma(1+a/2-b))");
    check("Hypergeometric2F1(a+1,b,a,z)", //
        "(-a+a*z-b*z)/(a*(1-z)^b*(-1+z))");
    check("Hypergeometric2F1(a,1-a,1/2,z)", //
        "Cos((-1+2*(1-a))*ArcSin(Sqrt(z)))/Sqrt(1-z)");
    check("Hypergeometric2F1(1-a,a,1/2,z)", //
        "Cos((-1+2*(1-a))*ArcSin(Sqrt(z)))/Sqrt(1-z)");
    check("Hypergeometric2F1(a,-a,1/2,z)", //
        "Cos(2*a*ArcSin(Sqrt(z)))");
    check("Hypergeometric2F1(1/2,1,3/2,3)", //
        "ArcTanh(3)/3");
    check("Hypergeometric2F1(1/2,1,3/2,t^2)", //
        "ArcTanh(t)/t");
    check("Hypergeometric2F1(a, a + 1/2, 2*a, z)", //
        "(1+Sqrt(1-z))^(1-2*a)/(2^(1-2*a)*Sqrt(1-z))");

    // https://github.com/mtommila/apfloat/issues/29
    checkNumeric("Hypergeometric2F1(-3.0, -1, -2, 1.0)", //
        "-0.5");
    // https://github.com/paulmasson/math/issues/10 - uses ThrowException
    check("Hypergeometric2F1(0.5,0.333,0.666,1)", //
        "Hypergeometric2F1(0.333,0.5,0.666,1.0)");
    checkNumeric("Hypergeometric2F1(-0.5, 1.0 / 3.0, 4.0 / 3.0, -1)", //
        "1.1114479705325755");
    checkNumeric("Hypergeometric2F1(-0.5, 1.0 / 3.0, -4.0 / 3.0, 0)", //
        "1.0");

    checkNumeric("Hypergeometric2F1(-0.5, 1.0 / 4.0, -4.0 / 3.0, -3)", //
        "0.6919237698061459");
    checkNumeric("Hypergeometric2F1(-0.5, 1.0 / 4.0, -4.0 / 3.0,-1.5)", //
        "0.8274559676019725");
    checkNumeric("Hypergeometric2F1(0.5, 1.0 / 3.0, 4.0 / 3.0, 1)", //
        "1.4021821053254544");
    checkNumeric("Hypergeometric2F1(-5, 1.0 / 3.0, 4.0 / 3.0, 1)", //
        "0.5006868131868132");
    checkNumeric("Hypergeometric2F1(-0.5, 3.0, 1.0, 0.2)", //
        "0.66383268082025");
    checkNumeric("Hypergeometric2F1(2, 2.0, 3.0, 0.95)", //
        "35.46652127744268");
    checkNumeric("Hypergeometric2F1(2, 2.0, 3.01, 0.95)", //
        "34.8101340764061");
    checkNumeric("Hypergeometric2F1(20, 2.0, 3.01, 0.95)", //
        "5.513090059384318E23");
    checkNumeric("Hypergeometric2F1(0.123, 2.0, 3.01, 0.95)", //
        "1.166719619920925");
    checkNumeric("Hypergeometric2F1(-1, 2.0, 3.01, 0.95)", //
        "0.3687707641196013");

    checkNumeric("N(Hypergeometric2F1(-23/10, 1/2 - 1/8 + 23/10 + 1, 1/8 + 1, (12 + 1)/2),50)", //
        "316.8942332151300302909232080800472001326090876729+I*436.16750112351808402055619284015549992820985322775");
    checkNumeric("Hypergeometric2F1(-2.3, 1/2 - 1/8 + 2.3 + 1, 1/8 + 1, (12 + 1)/2)", //
        "316.89423321513+I*436.1675011235181");

    check("D( Hypergeometric2F1(a,b,c,x), {x,-4})", //
        "D(Hypergeometric2F1(a,b,c,x),{x,-4})");

    check("N(Hypergeometric2F1(1/2, 1/3, 2, 1), 50)", //
        "1.1595952669639283657699920515700208819451652634397");
    check("Hypergeometric2F1(1/2, 1/3, 2, 1.000000000000000000000000000000000)", //
        "1.15959526696392836576999205157002");

    // iteration limit of exceeded.
    // https://github.com/paulmasson/math/issues/29#issuecomment-1120230707
    check("Hypergeometric2F1(1, 0.5, 1.5, -0.9999999999999976)", //
        "0.785398163397448652");


    check("Hypergeometric2F1(n,n,2*n+1,1)", //
        "Gamma(1+2*n)/Gamma(1+n)^2");
    check("Hypergeometric2F1(2/3,3/7,10, 1)", //
        "(362880*Gamma(187/21))/(Gamma(28/3)*Gamma(67/7))");

    // message Plus: m^2 and m are incompatible units
    check("Hypergeometric2F1(-5,Quantity(1.2,\"m\"),c,1)", //
        "(-28.8[m]+72.0[m^2]+-60.48[m^3]+20.736[m^4]+-2.48832[m^5]+24*c+-120.0[m]*c+151.2[m^2]*c+-69.12[m^3]*c+10.368[m^4]*c+\n" //
            + "50*c^2+-126.0[m]*c^2+86.4[m^2]*c^2+-17.28[m^3]*c^2+35*c^3+-48.0[m]*c^3+14.4[m^2]*c^\n" //
            + "3+10*c^4+-6.0[m]*c^4+c^5)/(c*(1+c)*(2+c)*(3+c)*(4+c))");

    // check("Hypergeometric2F1(1317624576693539401,0.333,-3/2,-0.5)", //
    // "Hypergeometric2F1(0.333,1.31762*10^18,-1.5,-0.5)");
    check("Hypergeometric2F1(-1,b,c,1)", //
        "(-b+c)/c");
    check("Hypergeometric2F1(-2,b,c,1)", //
        "(-b+b^2+c-2*b*c+c^2)/(c*(1+c))");
    check("Hypergeometric2F1(3,0,-1,x)", //
        "1");
    check("Hypergeometric2F1(a,3/2,1/2,x)", //
        "(1-x+2*a*x)/(1-x)^(1+a)");
    check("Hypergeometric2F1(3,1,-1,x)", //
        "ComplexInfinity");
    check("Hypergeometric2F1(1/2,I,-10,x)", //
        "ComplexInfinity");
    check("D( Hypergeometric2F1(a,b,c,x), {x,-4})", //
        "D(Hypergeometric2F1(a,b,c,x),{x,-4})");
    check("D( Hypergeometric2F1(a,b,c,x), {x,n})", //
        "(Hypergeometric2F1(a+n,b+n,c+n,x)*Pochhammer(a,n)*Pochhammer(b,n))/Pochhammer(c,n)");
    check("D( Hypergeometric2F1(a,b,c,x), {x,4})", //
        "(a*(1+a)*(2+a)*(3+a)*b*(1+b)*(2+b)*(3+b)*Hypergeometric2F1(4+a,4+b,4+c,x))/(c*(1+c)*(\n" + //
            "2+c)*(3+c))");
    check("D( Hypergeometric2F1(a,b,c,x), x)", //
        "(a*b*Hypergeometric2F1(1+a,1+b,1+c,x))/c");

    // check("Hypergeometric2F1(-3, 1, 2, z)", //
    // "(1-(1-z)^4)/(4*z)");

    check("Hypergeometric2F1(2,1-I,2-I,I*E^(I*x))", //
        "Hypergeometric2F1(1-I,2,2-I,I*E^(I*x))");
    check("Hypergeometric2F1(3/2, 2, 5/2, z^n) ", //
        "3/2*(-z^(n/2)+ArcTanh(z^(n/2))-z^n*ArcTanh(z^(n/2)))/(z^(3/2*n)*(-1+z^n))");
    check("Hypergeometric2F1(3/2, 2, 5/2, -z) ", //
        "(-I*3/2*(I*Sqrt(z)-I*ArcTan(Sqrt(z))-I*z*ArcTan(Sqrt(z))))/((-1-z)*z^(3/2))");
    check("Hypergeometric2F1(3/2, 2, 5/2, z) ", //
        "3/2*(-Sqrt(z)+ArcTanh(Sqrt(z))-z*ArcTanh(Sqrt(z)))/((-1+z)*z^(3/2))");

    // CatalanNumber:
    check("Hypergeometric2F1(-9, -10, 2, 1)", //
        "16796");

    // TODO currently unsupported
    check("Hypergeometric2F1(0.5,0.333,1,1.5708)", //
        "1.12923+I*(-0.568083)");

    check("Hypergeometric2F1(1, b, 2, z)", //
        "-(-1+(1-z)^b+z)/((-1+b)*(1-z)^b*z)");
    check("Hypergeometric2F1(a, b, a, z)", //
        "(1-z)^(-b)");
    check("Hypergeometric2F1(a, b, b-1, z)", //
        "Hypergeometric2F1(a,b,-1+b,z)");
    check("Hypergeometric2F1(a, b, b, z)", //
        "(1-z)^(-a)");
    // check("Hypergeometric2F1(a, b, b+1, z)", //
    // "(b*Beta(z,b,1-a))/z^b");

    check("Hypergeometric2F1(-5, b, c, 1)", //
        "(-24*b+50*b^2-35*b^3+10*b^4-b^5+24*c-100*b*c+105*b^2*c-40*b^3*c+5*b^4*c+50*c^2-\n"
            + "105*b*c^2+60*b^2*c^2-10*b^3*c^2+35*c^3-40*b*c^3+10*b^2*c^3+10*c^4-5*b*c^4+c^5)/(c*(\n"
            + "1+c)*(2+c)*(3+c)*(4+c))");
    check("Hypergeometric2F1(-n, b, c, 1)", //
        "Hypergeometric2F1(b,-n,c,1)");



    check("Hypergeometric2F1(2 + I, -I, 3/4, 0.5-0.5*I)", //
        "-0.972167+I*(-0.181659)");

    // Hypergeometric2F1(1 - n, -n, 2, 1) == CatalanNumber(n)
    check("Hypergeometric2F1(-3, -4, 2, 1)==CatalanNumber(4)", //
        "True");

    check("Hypergeometric2F1(1,2,3/2,x^2/9)", //
        "3/2*(1/3*Sqrt(1-x^2/9)*Sqrt(x^2)+ArcSin(Sqrt(x^2)/3))/((1-x^2/9)^(3/2)*Sqrt(x^2))");

    check("Hypergeometric2F1(-2,b,c,1)", //
        "(-b+b^2+c-2*b*c+c^2)/(c*(1+c))");

    check("Hypergeometric2F1(0.5,0.333,0.666,0.5)", //
        "1.18566");
    checkNumeric("Hypergeometric2F1(0.5,Sin(Pi),0.666,-0.5)", //
        "1.0");
    checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,-0.5)", //
        "0.9026782488379916");
    checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,0.75)", //
        "1.3975732184289733");
    checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,-0.75)", //
        "0.8677508558430819");
  }

  @Test
  public void testHypergeometric2F1Regularized() {
    check("Hypergeometric2F1Regularized(I, -I, .5 + I, 5)", //
        "-0.91139+I*(-1.76606)");

    check("Hypergeometric2F1Regularized(-1, -1, 0, .5)", //
        "0.5");
    check("N(Hypergeometric2F1Regularized(1/3, 1, 3, -7), 50)", //
        "0.35510204081632653061224489795918367346938775510204");
    check("N(Hypergeometric2F1Regularized(1/3, 1, 0, -7), 25)", //
        "-0.1458333333333333333333333");


    checkNumeric("Hypergeometric2F1Regularized(1, 2, -3, 4.5)", //
        "26.768438320767704");
    check("Hypergeometric2F1Regularized(1, 1/3, -1, -0.3000000025555555555522220000)", //
        "0.0216866352303372915924565340457");

    check("Hypergeometric2F1Regularized(7, 2, -0.3, .5)", //
        "17490.25");

    check("Hypergeometric2F1Regularized(a,b,b,x)", //
        "1/((1-x)^a*Gamma(a))");
    check("Hypergeometric2F1Regularized(a,b,c,0)", //
        "1/Gamma(c)");
  }

  @Test
  public void testHypergeometricPFQ() {
    check("HypergeometricPFQ({}, {}, z)", //
        "E^z");
    check("HypergeometricPFQ(ConstantArray(1,1), ConstantArray(2,0), z)", //
        "1/(1-z)");
    check("HypergeometricPFQ(ConstantArray(1,2), ConstantArray(2,1), z)", //
        "-Log(1-z)/z");
    check("HypergeometricPFQ(ConstantArray(1,3), ConstantArray(2,2), z)", //
        "PolyLog(2,z)/z");
    check("HypergeometricPFQ({1,1,1,1,1,1}, {2,2,2,2,2}, z)", //
        "PolyLog(5,z)/z");
    check("HypergeometricPFQ(ConstantArray(1,42), ConstantArray(2,41), z)", //
        "PolyLog(41,z)/z");

    check("HypergeometricPFQ({1, 1}, {1/2, 1}, z)", //
        "1+E^z*Sqrt(Pi)*Sqrt(z)*Erf(Sqrt(z))");
    check("HypergeometricPFQ({1, 1}, {1/2, 1}, z)", //
        "1+E^z*Sqrt(Pi)*Sqrt(z)*Erf(Sqrt(z))");
    check("HypergeometricPFQ({0,a1,a2,a3,a4},{b1,b2,b3},z)", //
        "1");

    // message HypergeometricPFQ: general hypergeometric argument currently restricted.
    check("HypergeometricPFQ({1, 1, 1}, {3/2, 3/2, 3/2}, 10.0)", //
        "HypergeometricPFQ({1,1,1},{3/2,3/2,3/2},10.0)");
    check("HypergeometricPFQ({1, 1}, {3, 3, 3}, 2.)", //
        "HypergeometricPFQ({1,1},{3,3,3},2.0)");

    check("HypergeometricPFQ({I, I, I}, {2, 2 , 2}, -1.0*I)", //
        "0.870032+I*(-0.00484538)");
    check("HypergeometricPFQ({1, 2, 3, 4}, {5, 6, 7}, {0.1, 0.3, 0.5})", //
        "{1.01164,1.03627,1.06296}");

    check("HypergeometricPFQ({1/2, b}, {3/2, b + 1}, z)", //
        "(b*(Sqrt(Pi)*Sqrt(1/z)*Erfi(Sqrt(z))+(-Gamma(b)+Gamma(b,-z))/(-z)^b))/(-1+2*b)");
    check("HypergeometricPFQ({a,b}, {c,d}, 0)", //
        "1");
    check("HypergeometricPFQ({a, b}, {c, b}, z)", //
        "HypergeometricPFQ({a},{c},z)");
  }

  @Test
  public void testHypergeometricU() {
    // TODO throws hypergeometric function pole
    // https://github.com/mtommila/apfloat/issues/36
    check("HypergeometricU(3.0, 1.0, 0.0)", //
        "HypergeometricU(3.0,1.0,0.0)");
    check("HypergeometricU(3, 2, 1.0)", //
        "0.105479");
    check("N(HypergeometricU(3, 2, 1),50)", //
        "0.10547895651520888848838225094608093588873320977117");


    check("D(HypergeometricU(a,b,x), x)", //
        "-a*HypergeometricU(1+a,1+b,x)");
    check("HypergeometricU(2, b, z)", //
        "(-1+E^z*(2-b+z)*z^(1-b)*Gamma(-1+b,z))/(2-b)");
    check("HypergeometricU(a, a+3, z)", //
        "(1+(a*(1+a))/z^2+(2*a)/z)/z^a");
    check("HypergeometricU(3, 2.5, 1.0)", //
        "0.173724");
    check("HypergeometricU(3, 2.5, 0.0)", //
        "ComplexInfinity");
    check("Table( HypergeometricU(3, 2.5, x), {x,-2.0,2,0.25})", //
        "{0.19001+I*(-0.148415),0.27603+I*(-0.141362),0.39355+I*(-0.107638),0.553199+I*(-0.0227102)," //
            + "0.76904+I*0.163012,1.05965+I*0.56395,1.44956+I*1.52035,1.97105+I*4.83136,ComplexInfinity," //
            + "2.45436,0.688641,0.312167,0.173724,0.1086,0.0732253,0.0520871,0.0385635}");
    check("Table( HypergeometricU(3, 1.0, x), {x,-2.0,2,0.25})", //
        "{0.0852414+I*0.212584,0.0312283+I*0.264433,-0.0527303+I*0.306681,-0.171748+I*0.323467,-0.325706+I*0.288932,-0.500123+I*0.162311,-0.642219+I*(-0.119092),-0.575265+I*(-0.649898),HypergeometricU(3.0,1.0,0.0),0.214115,0.105593,0.0644474,0.0436079,0.0314298,0.0236577,0.0183874,0.0146502}");
  }

  @Test
  public void testI() {
    check("I*0.0//FullForm", //
        "Complex(0.0`,0.0`)");
    check("I*1.0//FullForm", //
        "Complex(0.0`,1.0`)");
    check("(3+I)*(3-I)", //
        "10");
  }

  @Test
  public void testIdentity() {
    check("Composition(Through, {Identity, Sqrt}) /@ {0, 1.0, 2.0, 3.0, 4.0}", //
        "{{0,0},{1.0,1.0},{2.0,1.41421},{3.0,1.73205},{4.0,2.0}}");
    check("Identity'", //
        "1&");
    check("D(Identity(Sin(x)),x)", //
        "Cos(x)");
    check("Composition(f,g,Identity,h,Identity,g)", //
        "f@*g@*h@*g");
    check("Identity(0)", //
        "0");
  }

  @Test
  public void testIf() {
    check("If(FreeQ(a+b*x,x),1,a+b*x)", //
        "a+b*x");
    check("If(1 == k, itstrue, itsfalse)", //
        "If(1==k,itstrue,itsfalse)");
    check("If(1<2, a, b)", //
        "a");
    check("If(1<2, a)", //
        "a");
    check("If(False, a) //FullForm", //
        "Null");
    check("If(a>b,true)", //
        "If(a>b,True)");
    check("If(a>b,1,0)", //
        "If(a>b,1,0)");
    check("If(a>b,1,0, Indeterminate)", //
        "Indeterminate");
    check("If(TrueQ(a>b),1,0)", //
        "0");
    check("If(TrueQ(a>b),1) // FullForm", //
        "Null");
  }

  @Test
  public void testIn001() {
    check("a=1.0", //
        "1.0");
    check("a=a/2+1/a", //
        "1.5");
    check("In(2)", //
        "1.41667");
    check("In(2)", //
        "1.41422");
    check("Definition(Out)", //
        "Attributes(Out)={Listable,NHoldFirst,Protected}\n" //
            + "\n" //
            + "Out(1)=1.0`\n" //
            + "\n" //
            + "Out(2)=1.5`\n" //
            + "\n" //
            + "Out(3)=1.4166666666666665`\n" //
            + "\n" //
            + "Out(4)=1.4142156862745097`");
    check("Definition(In)", //
        "Attributes(In)={Listable,NHoldFirst,Protected}\n" //
            + "\n" //
            + "In(1):=a=1.0`\n" //
            + "\n" //
            + "In(2):=a=a/2 + 1/a\n" //
            + "\n" //
            + "In(3):=In(2)\n" //
            + "\n" //
            + "In(4):=In(2)\n" //
            + "\n" //
            + "In(5):=Definition(Out)");
  }

  @Test
  public void testIn002() {
    check("a=1.0", //
        "1.0");
    check("b=3/2", //
        "3/2");
    check("a=a/2+b/a", //
        "2.0");
    check("In(3)", //
        "1.75");
    check("Do(In(3), {3})", //
        "");
    check("a-Sqrt(3)", //
        "2.22045*10^-16");
    check("Definition(In)", //
        "Attributes(In)={Listable,NHoldFirst,Protected}\n" //
            + "\n" //
            + "In(1):=a=1.0`\n" //
            + "\n" //
            + "In(2):=b=3/2\n" //
            + "\n" //
            + "In(3):=a=a/2 + b/a\n" //
            + "\n" //
            + "In(4):=In(3)\n" //
            + "\n" //
            + "In(5):=Do(In(3),{3})\n" //
            + "\n" //
            + "In(6):=-Sqrt(3) + a");
  }

  @Test
  public void testIm() {
    check("Im(I*Arg(z) + Log(2) + (1/2)*Log(Im(z)^2 + Re(z)^2))", //
        "Arg(z)+Im(Log(Im(z)^2+Re(z)^2))/2");
    check("Im(I*Re(z)+x+y+Im(t)*I)", //
        "Im(t+x+y)+Re(z)");
    check("Im(Quantity(2,\"m\"))", //
        "0[m]");
    check("Im(Quantity(a,\"m\"))", //
        "Im(a)[m]");
    check("Im(I*x+y)", //
        "Im(y)+Re(x)");
    check("Im(I*x)", //
        "Re(x)");
    check("Im(I*Pi/4)", //
        "Pi/4");
    check("Im(E^(I*Pi/4))", //
        "1/Sqrt(2)");
    check("Im(E^(I*3))", //
        "Sin(3)");
    check("Im(Sin(42)*Cos(43))", //
        "0");
    check("Im(Sin(42))", //
        "0");
    check("Im(I*9*Sin(5))", //
        "9*Sin(5)");
    check("Im(3*(-2)^(3/4))", //
        "3*2^(1/4)");
    check("Im(3*2^(3/4))", //
        "0");

    check("Im(0)", //
        "0");
    check("Im(I)", //
        "1");
    check("Im(Indeterminate)", //
        "Indeterminate");
    check("Im(Infinity)", //
        "0");
    check("Im(-Infinity)", //
        "0");
    check("Im(ComplexInfinity)", //
        "Indeterminate");
  }

  @Test
  public void testImportString() {
    check("ImportString(\"SGVsbG8gd29ybGQ=\", \"Base64\")", // ??
        "Hello world");

    check(
        "ImportString( \"[\\\"Association\\\",[\\\"Rule\\\",\\\"'x'\\\",\\\"1\\\"],[\\\"Rule\\\",\\\"'y'\\\",\\\"2\\\"],[\\\"Rule\\\",\\\"'z'\\\",\\\"3\\\"]]\", \"ExpressionJSON\") // InputForm", //
        "<|\"x\"->1,\"y\"->2,\"z\"->3|>");
    check(
        "ImportString(\"[\\\"Graphics3D\\\", [\\\"Line\\\",[\\\"List\\\", [\\\"List\\\",1.0,1.0,-1.0], [\\\"List\\\",2.0,2.0,1.0], [\\\"List\\\",3.0,3.0,-1.0], [\\\"List\\\",4.0,4.0,1.0]] ] ]\", \"ExpressionJSON\")", //
        "Graphics3D(Line({{1.0,1.0,-1.0},{2.0,2.0,1.0},{3.0,3.0,-1.0},{4.0,4.0,1.0}}))");

    check(
        "ImportString(\"[ \\\"Quantity\\\", 2.45, \\\"'Meters'\\\"]\", \"ExpressionJSON\") // FullForm", //
        "Quantity(2.45`, \"Meters\")");
    check("ImportString(\"\\\"'abc'\\\"\", \"ExpressionJSON\") // FullForm", //
        "\"abc\"");
    check("ImportString(\"[\\\"List\\\", 1, 2, 3 ]\", \"ExpressionJSON\")", //
        "{1,2,3}");


    check("ImportString(\"{\\\"id\\\":1,\\\"text\\\":\\\"ñía\\\"}\", \"JSON\") // InputForm", //
        "{\"id\"->1,\"text\"->\"ñía\"}");
    check("ImportString(\"{\\\"id\\\":1,\\\"text\\\":\\\"ñía\\\"}\", \"RawJSON\") // InputForm", //
        "<|\"id\"->1,\"text\"->\"ñía\"|>");

    check("ImportString(\"[1,2,3]\", \"JSON\") // InputForm", //
        "{1,2,3}");
    check("ImportString(\"[1,2,3]\", \"RawJSON\") // InputForm", //
        "{1,2,3}");
    check("ImportString(\"{\\\"x\\\":1, \\\"y\\\":2, \\\"z\\\":3}\", \"JSON\") // InputForm", //
        "{\"x\"->1,\"y\"->2,\"z\"->3}");
    check("ImportString(\"{\\\"x\\\":1, \\\"y\\\":2, \\\"z\\\":3}\", \"RawJSON\") // InputForm", //
        "<|\"x\"->1,\"y\"->2,\"z\"->3|>");


    check("ImportString(\"3,4,6\\na,b,c\", \"Table\")", //
        "{{3,4,6},{a,b,c}}");
    check("ImportString(\"3,4,6\\na,b,c\", \"Text\") // InputForm", //
        "\"3,4,6\n" + "a,b,c\"");
    check("ImportString(\"a+a+a\", \"String\")", //
        "3*a");
  }

  @Test
  public void testIncrement() {
    check("a = 2", //
        "2");
    check("a++", //
        "2");
    check("a", //
        "3");
    check("++++a+++++2//Hold//FullForm", //
        "Hold(Plus(PreIncrement(PreIncrement(Increment(Increment(a)))), 2))");

    check("index = {1,2,3,4,5,6}", //
        "{1,2,3,4,5,6}");
    check("index[[2]]++", //
        "2");
    check("index", //
        "{1,3,3,4,5,6}");
  }

  @Test
  public void testIndeterminate() {
    check("Infinity-Infinity", //
        "Indeterminate");
    check("ComplexInfinity+ComplexInfinity", //
        "Indeterminate");
    check("Indeterminate+1", //
        "Indeterminate");
    check("0*Indeterminate", //
        "Indeterminate");
    check("0*ComplexInfinity", //
        "Indeterminate");
    check("Tan(Indeterminate)", //
        "Indeterminate");
    check("{And(True, Indeterminate), And(False, Indeterminate)}", //
        "{Indeterminate,False}");
    check("Indeterminate==Indeterminate", //
        "Indeterminate==Indeterminate");
    check("Indeterminate===Indeterminate", //
        "True");
    check("Indeterminate!=Indeterminate", //
        "Indeterminate!=Indeterminate");
    check("{Re(Indeterminate), Im(Indeterminate)}", //
        "{Indeterminate,Indeterminate}");
    check("NumberQ(Indeterminate)", //
        "False");
    check("{1,2,3}*Indeterminate", //
        "{Indeterminate,Indeterminate,Indeterminate}");
    check("{1,2,3}+Indeterminate", //
        "{Indeterminate,Indeterminate,Indeterminate}");

    check("Integrate(Indeterminate,x)", //
        "Indeterminate");
    check("D(Indeterminate,x)", //
        "Indeterminate");
    check("DirectedInfinity(Indeterminate)", //
        "ComplexInfinity");
  }

  @Test
  public void testInexactNumberQ() {
    check("InexactNumberQ(a)", //
        "False");
    check("InexactNumberQ(3.0)", //
        "True");
    check("InexactNumberQ(2/3)", //
        "False");
    check("InexactNumberQ(4.0+I)", //
        "True");
  }

  @Test
  public void testInfinity() {
    // 1/2*(2+3*l+l^2-4*(-Infinity)-2*l*(-Infinity)+(-Infinity)^2)
    check("-2*l*(-Infinity)", //
        "Infinity*l");
    check("(-Infinity)^2", //
        "Infinity");
    check("1/2*(2+3*l+l^2-4*(-Infinity)-2*l*(-Infinity)+(-Infinity)^2)", //
        "1/2*(Infinity+3*l+Infinity*l+l^2)");

    check("1 / Infinity", //
        "0");
    check("Infinity + 100", //
        "Infinity");
    check("Sum(1/x^2, {x, 1, Infinity})", //
        "Pi^2/6");
    check("FullForm(Infinity)", //
        "DirectedInfinity(1)");
    check("(2 + 3.5*I) / Infinity", //
        "0.0");
    check("Infinity + Infinity", //
        "Infinity");
    check("Infinity / Infinity", //
        "Indeterminate");
  }

  @Test
  public void testInformation() {
    // print documentation in console
    check("Information(Sin)", //
        "");
  }

  @Test
  public void testInner() {
    check("Inner({{1,0},{0,1},Indeterminate},{{1,0},{0,1},SparseArray({0,0})},{-1/2,{1,2,3,a},3})", //
        "Inner({{1,0},{0,1},Indeterminate},{{1,0},{0,1},SparseArray(Number of elements: 0 Dimensions: {2} Default value: 0)},{-\n"
            + "1/2,{1,2,3,a},3})");
    check("Inner(f,{{}},{5},g)", //
        "Inner(f,{{}},{5},g)");
    check("Inner(f,{{1,0},{0,1},1/Sqrt(5)},{x,-3,3/4},g)", //
        "g(f({1,0},x),f({0,1},-3),f(1/Sqrt(5),3/4))");
    check("Inner(I,{{1,0},{0,1},1/Sqrt(5)},{x,-3,3/4},DirectedInfinity(-3.141592653589793))", //
        "-Infinity[I[{1,0},x],I[{0,1},-3],I[1/Sqrt(5),3/4]]");

    check("Inner(Times,{},{ })", //
        "0");
    check("Inner(f,{},{ })", //
        "0");
    check("Inner(Times,{},{x,y})", //
        "Inner(Times,{},{x,y})");
    check("Inner(Times, {a, b}, {x, y}, Plus)", //
        "a*x+b*y");
    check("Inner(Times, {a, b}, {x, y})", //
        "a*x+b*y");
    check("Inner(Power, {a, b, c}, {x, y, z}, Times)", //
        "a^x*b^y*c^z");
    check("Inner(f, {a, b}, {x, y}, g)", //
        "g(f(a,x),f(b,y))");
    check("Inner(f, {{a, b}, {c, d}}, {x, y}, g)", //
        "{g(f(a,x),f(b,y)),g(f(c,x),f(d,y))}");
    check("Inner(f, {{a, b}, {c, d}}, {{u, v}, {w, x}}, g)", //
        "{{g(f(a,u),f(b,w)),g(f(a,v),f(b,x))},{g(f(c,u),f(d,w)),g(f(c,v),f(d,x))}}");
    check("Inner(f, {x, y}, {{a, b}, {c, d}}, g)", //
        "{g(f(x,a),f(y,c)),g(f(x,b),f(y,d))}");
    check("Inner(s, f(1), f(2), t)", //
        "t(s(1,2))");
    check("Inner(And, {{False, False}, {False, True}}, {{True, False}, {True, True}}, Or)", //
        "{{False,False},{True,True}}");
    check("Inner(f, {{{a, b}}, {{x, y}}}, {{1}, {2}}, g)", //
        "{{{g(f(a,1),f(b,2))}},{{g(f(x,1),f(y,2))}}}");
  }

  @Test
  public void testInputForm() {
    check("InputForm(Sin(0))", //
        "0");
    check("\"a string\" // InputForm", //
        "\"a string\"");
  }

  @Test
  public void testInsert() {
    check("Insert(<|\"z\"->t|>, {x->y,a->b}, 1)", //
        "<|x->y,a->b,z->t|>");
    check("Insert(<|\"z\"->t|>, {x->y,a->b},\"z\")", //
        "<|x->y,a->b,z->t|>");
    check("Insert(<||>, {x->y,a->b}, 1)", //
        "<|x->y,a->b|>");
    check("Insert(<||>, x->y, 1)", //
        "<|x->y|>");
    // Insert: The argument x is not a rule or a list of rules.
    check("Insert(<||>, x, 1)", //
        "<||>");
    // Insert: Part {3} of <||> does not exist.
    check("Insert(<||>, x->y, 3)", //
        "Insert(<||>,x->y,3)");
    check("Insert({a, b, c, d, e}, x, 3)", //
        "{a,b,x,c,d,e}");

    check("Insert({a, b, c, d, e}, x, -2)", //
        "{a,b,c,d,x,e}");
    check("Insert({a, b, c, d, e}, x, {{1}, {3}, {-1}})", //
        "{x,a,b,x,c,d,e,x}");

    check("Insert(<|a -> 1, b -> 2|>, c -> 3, Key(a))", //
        "<|c->3,a->1,b->2|>");
    check("Insert(x,3)[{a, b, c, d, e}]", //
        "{a,b,x,c,d,e}");


    // test operator form
    check("Insert(e, pos)", //
        "Insert(e,pos)");
    check("Insert(e, pos)[x]", //
        "Insert(e,pos)[x]");
    check("Insert(2, -1)[{a, b, c, d, e}]", //
        "{a,b,c,d,e,2}");
    check("Insert(2, -2)[{a, b, c, d, e}]", //
        "{a,b,c,d,2,e}");


    check("Insert(1 + x^(a + b) + y^(c + d),zzz, {3,2,3})", //
        "1+x^(a+b)+y^(c+d+zzz)");

    check("Insert(h(a,b),x, { })", //
        "h(a,b)");
    check("Insert(h(a,b),x, {{}})", //
        "Insert(h(a,b),x,{{}})");
  }

  @Test
  public void testInteger() {
    check("123456789012345678901234567890", //
        "123456789012345678901234567890");
    check("Head(5)", //
        "Integer");
    check("{a, b} = {2^10000, 2^10000 + 1}; {a == b, a < b, a <= b}", //
        "{False,True,True}");
  }

  @Test
  public void testIntegerDigits() {
    // message: IntegerDigits: Base 1 is not an integer greater than 1.
    check("IntegerDigits(11,1)", //
        "IntegerDigits(11,1)");
    // message: IntegerDigits: Base greater than 36 currently not supported in IntegerDigits.
    check("IntegerDigits(11,37)", //
        "IntegerDigits(11,37)");

    check("IntegerDigits(25, 8)", //
        "{3,1}");
    check("IntegerDigits(11,2,3)", //
        "{0,1,1}");

    check("IntegerDigits({123,456,789}, 2, 10)", //
        "{{0,0,0,1,1,1,1,0,1,1},{0,1,1,1,0,0,1,0,0,0},{1,1,0,0,0,1,0,1,0,1}}");

    // Long.MAX_VALUE == 9_223_372_036_854_775_807L
    check("IntegerDigits(9223372036854775807,2)", //
        "{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,\n" //
            + "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}");
    // Long.MAX_VALUE == 9_223_372_036_854_775_807L + 1L
    check("IntegerDigits(9223372036854775808,2)", //
        "{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,\n" //
            + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}");

    check("IntegerDigits(11,2,3)", //
        "{0,1,1}");
    check("IntegerDigits(11,2,2)", //
        "{1,1}");

    // https://oeis.org/A018900 - Sum of two distinct powers of 2
    check("Select(Range(1000), (Count(IntegerDigits(#, 2), 1)==2)&)", //
        "{3,5,6,9,10,12,17,18,20,24,33,34,36,40,48,65,66,68,72,80,96,129,130,132,136,144,\n"
            + "160,192,257,258,260,264,272,288,320,384,513,514,516,520,528,544,576,640,768}");
    check("IntegerDigits(0)", //
        "{0}");
    check("IntegerDigits(123)", //
        "{1,2,3}");
    check("IntegerDigits(-123)", //
        "{1,2,3}");
    check("IntegerDigits(123, 2)", //
        "{1,1,1,1,0,1,1}");
    check("IntegerDigits(123, 2)", //
        "{1,1,1,1,0,1,1}");
    check("IntegerDigits(123, 2, 10)", //
        "{0,0,0,1,1,1,1,0,1,1}");
    check("IntegerDigits({123,456,789}, 2, 10)", //
        "{{0,0,0,1,1,1,1,0,1,1},{0,1,1,1,0,0,1,0,0,0},{1,1,0,0,0,1,0,1,0,1}}");
    check("IntegerDigits(123, -2)", //
        "IntegerDigits(123,-2)");
    check("IntegerDigits(58127, 2)", //
        "{1,1,1,0,0,0,1,1,0,0,0,0,1,1,1,1}");
    check("IntegerDigits(58127, 16)", //
        "{14,3,0,15}");

    check("IntegerDigits({6,7,2}, 2)", //
        "{{1,1,0},{1,1,1},{1,0}}");
    check("IntegerDigits(7, {2,3,4})", //
        "{{1,1,1},{2,1},{1,3}}");
    check("IntegerDigits(Range(0,7), 2)", //
        "{{0},{1},{1,0},{1,1},{1,0,0},{1,0,1},{1,1,0},{1,1,1}}");
    check("IntegerDigits(Range(0,7), 2, 3)", //
        "{{0,0,0},{0,0,1},{0,1,0},{0,1,1},{1,0,0},{1,0,1},{1,1,0},{1,1,1}}");
    check("IntegerDigits(6345354,10,4)", //
        "{5,3,5,4}");

    check("Table(If(FreeQ(IntegerDigits(n-1, 3), 1), 1, 0), {n, 27})", //
        "{1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,1}");
    // TODO max radix is 36 at the moment
    check("Table(FromDigits(IntegerDigits(2, b), 1/b)/b, {b,2,36})", //
        "{1/4,2/3,1/2,2/5,1/3,2/7,1/4,2/9,1/5,2/11,1/6,2/13,1/7,2/15,1/8,2/17,1/9,2/19,1/\n" //
            + "10,2/21,1/11,2/23,1/12,2/25,1/13,2/27,1/14,2/29,1/15,2/31,1/16,2/33,1/17,2/35,1/\n" //
            + "18}");
  }

  @Test
  public void testIntegerExponent() {
    check("IntegerExponent(1230000)", //
        "4");
    check("IntegerExponent(2^10+2^7, 2)", //
        "7");
    check("IntegerExponent(0, 2)", //
        "Infinity");
    check("IntegerExponent(100,100)", //
        "1");
    check("Table(IntegerExponent(n!), {n, 50})", //
        "{0,0,0,0,1,1,1,1,1,2,2,2,2,2,3,3,3,3,3,4,4,4,4,4,6,6,6,6,6,7,7,7,7,7,8,8,8,8,8,9,\n"
            + "9,9,9,9,10,10,10,10,10,12}");
    check("IntegerExponent(2524,2)", //
        "2");
    check("IntegerExponent(-510000)", //
        "4");

    check("IntegerExponent(16, 2)", //
        "4");
    check("IntegerExponent(-510000)", //
        "4");
    check("IntegerExponent(10, b)", //
        "IntegerExponent(10,b)");
  }

  @Test
  public void testIntegerLength() {
    check("IntegerLength(8,1)", //
        "IntegerLength(8,1)");
    check("IntegerLength(123456)", //
        "6");
    check("IntegerLength(10^10000)", //
        "10001");
    check("IntegerLength(-10^1000)", //
        "1001");
    check("IntegerLength(8, 2)", //
        "4");
    check("IntegerLength /@ (10 ^ Range(100)) == Range(2, 101)", //
        "True");
    check("IntegerLength(3, -2)", //
        "IntegerLength(3,-2)");
    check("IntegerLength(0)", //
        "1");
    check("IntegerLength /@ (10 ^ Range(100) - 1) == Range(1, 100)", //
        "True");
  }

  @Test
  public void testIntegerPart() {
    check("IntegerPart(Quantity(-1.1, \"Meters\"))", //
        "-1[Meters]");
    check("IntegerPart(Pi^20)", //
        "8769956796");
    check("IntegerPart(2^128-1)", //
        "340282366920938463463374607431768211455");
    check("IntegerPart(Infinity)", //
        "Infinity");
    check("IntegerPart(-Infinity)", //
        "-Infinity");
    check("IntegerPart(I*Infinity)", //
        "I*Infinity");
    check("IntegerPart(-I*Infinity)", //
        "-I*Infinity");

    check("IntegerPart(Pi)", //
        "3");
    check("IntegerPart(-Pi)", //
        "-3");
    check("IntegerPart(IntegerPart(Pi))", //
        "3");

    check("IntegerPart(-9/4)", //
        "-2");
    check("IntegerPart(2.4)", //
        "2");
    check("IntegerPart(-2.4)", //
        "-2");
    check("IntegerPart({-2.4, -2.5, -3.0})", //
        "{-2,-2,-3}");
  }

  @Test
  public void testInterpolation() {
    checkNumeric("Interpolation(Table({x, Exp(4/(1+x^2))}, {x, 0, 3, 0.5}), 2.5)", //
        "1.7362439627994641");
    checkNumeric("ipf=Interpolation(Table({x, Exp(4/(1+x^2))}, {x, 0, 3, 0.5})); ipf(2.5) ", //
        "1.7362439627994641");
    checkNumeric("Exp(4/(1 + 2.5^2))", //
        "1.7362439627994641");
    // print message: InterpolatingFunction: Input value {10} lies outside the range of data in the
    // interpolating
    // function. Extrapolation will be used.
    check("ipf=Interpolation({{0, 0}, {0.1, .3}, {0.5, .6}, {1, -.2}, {2, 3}}); ipf(10) ", //
        "6311.0");

    check("ipf= Interpolation({{0, 0}, {0.1, .3}, {0.5, .6}, {1, -.2}, {2, 3}}); ipf(3/4) ", //
        "0.26864");
    check("ipf=Interpolation({{0, 0}, {0.1, .3}, {0.5, .6}, {1, -.2}, {2, 3}}); ipf(0.75) ", //
        "0.26864");
    check("ipf=Interpolation({{0,17},{1,3},{2,5},{3,4},{4,3},{5,0},{6,23}}); ipf(19/4) ", //
        "-19/32");
    check("ipf=Interpolation({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}}); ipf(5/2) ", //
        "59/16");
    check("Interpolation({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}})", //
        "InterpolatingFunction[Piecewise({{InterpolatingPolynomial({{0,0},{1,1},{2,3},{3,4}},#1),#1<2},{InterpolatingPolynomial({{\n"
            + "1,1},{2,3},{3,4},{4,3}},#1),2<=#1&&#1<3},{InterpolatingPolynomial({{2,3},{3,4},{\n"
            + "4,3},{5,0}},#1),#1>=3}})&]");
    check(
        "ipf=Interpolation({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}});{ipf(2.5),ipf(3.0),ipf(3.5)}", //
        "{3.6875,4.0,3.75}");
  }

  @Test
  public void testInterpolatingFunction() {
    check("ipf=Interpolation({{0, 0}, {0.1, .3}, {0.5, .6}, {1, -.2}, {2, 3}}); ipf(0.75) ", //
        "0.26864");
    check("ipf=Interpolation({{0,17},{1,3},{2,5},{3,4},{4,3},{5,0},{6,23}}); ipf(19/4) ", //
        "-19/32");
    check("ipf=Interpolation({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}}); ipf(5/2) ", //
        "59/16");
    check("Interpolation({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}})", //
        "InterpolatingFunction[Piecewise({{InterpolatingPolynomial({{0,0},{1,1},{2,3},{3,4}},#1),#1<2},{InterpolatingPolynomial({{\n"
            + "1,1},{2,3},{3,4},{4,3}},#1),2<=#1&&#1<3},{InterpolatingPolynomial({{2,3},{3,4},{\n"
            + "4,3},{5,0}},#1),#1>=3}})&]");
    check(
        "ipf=Interpolation({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}});{ipf(2.5),ipf(3.0),ipf(3.5)}", //
        "{3.6875,4.0,3.75}");
  }

  @Test
  public void testInterpolatingPolynomial() {
    check("InterpolatingPolynomial( {{0.1,0.3}, {0.5,0.6},  {1,-0.2},  {2.0,3.0}},x)", //
        "0.3+(0.75+(-2.61111+3.05848*(-1+x))*(-0.5+x))*(-0.1+x)");
    check("InterpolatingPolynomial( {{0.1,0.3}, {0.5,0.6},  {1,-0.2},  {2.0,3.0}},0.75)", //
        "0.238944");
    check("InterpolatingPolynomial( {{0.0,0.0},{0.1,0.3}, {0.5,0.6},  {1,-0.2},  {2.0,3.0}},0.75)", //
        "0.26864");
    check("InterpolatingPolynomial({1.0,4.0},x)", //
        "1.0+3.0*(-1+x)");
    check("InterpolatingPolynomial({1,4},x)", //
        "1+3*(-1+x)");
    check("InterpolatingPolynomial({1,4,9},x)", //
        "1+(-1+x)*(1+x)");
    check("InterpolatingPolynomial({1,4,9,16},x)", //
        "1+(-1+x)*(1+x)");
    check("InterpolatingPolynomial({1,2},x)", //
        "x");

    check("InterpolatingPolynomial({{-1, 4}, {0, 2}, {1, 6}}, x)", //
        "4+(1+x)*(-2+3*x)");
    check("Expand((3*x-2)*(x+1)+4)", //
        "2+x+3*x^2");

    check("InterpolatingPolynomial({{0, 1}, {a, 0}, {b, 0}, {c, 0}}, x)", //
        "1+x*(-1/a+(1/(a*b)+(b-x)/(a*b*c))*(-a+x))");

    check("InterpolatingPolynomial({1,2,3,5,8,5},x)", //
        "1+(1+(1/6+(-1/24+1/20*(5-x))*(-4+x))*(-3+x)*(-2+x))*(-1+x)");

    check("((x-1)*((x-3)*(x-2)*((x-4)*(-1/20*x+5/24)+1/6)+1)+1) /. x -> Range(6)", //
        "{1,2,3,5,8,5}");
  }

  @Test
  public void testInterquartileRange() {
    // https://en.wikipedia.org/wiki/Interquartile_range
    check("InterquartileRange({7,7,31,31,47,75,87,115,116,119,119,155,177})", //
        "88");
    check("InterquartileRange({1, 3, 4, 2, 5, 6})", //
        "3");

    check("InterquartileRange(BernoulliDistribution(x))", //
        "Piecewise({{1,1/4<x&&x<=3/4}},0)");
    check("InterquartileRange(CauchyDistribution(x,y))", //
        "2*y");
    check("InterquartileRange(ErlangDistribution(x,y))", //
        "-InverseGammaRegularized(x,0,1/4)/y+InverseGammaRegularized(x,0,3/4)/y");
    check("InterquartileRange(ExponentialDistribution(x))", //
        "Log(3)/x");
    check("InterquartileRange(FrechetDistribution(x,y))", //
        "y/Log(4/3)^(1/x)-y/Log(4)^(1/x)");
    check("InterquartileRange(GammaDistribution(x,y))", //
        "-y*InverseGammaRegularized(x,0,1/4)+y*InverseGammaRegularized(x,0,3/4)");
    check("InterquartileRange(GumbelDistribution( ))", //
        "Log(Log(4)/Log(4/3))");
    check("InterquartileRange(GumbelDistribution(x,y))", //
        "y*Log(Log(4)/Log(4/3))");
    check("InterquartileRange(LogNormalDistribution(x,y))", //
        "-E^(x-Sqrt(2)*y*InverseErfc(1/2))+E^(x-Sqrt(2)*y*InverseErfc(3/2))");
    check("InterquartileRange(NakagamiDistribution(x,y))", //
        "-Sqrt((y*InverseGammaRegularized(x,0,1/4))/x)+Sqrt((y*InverseGammaRegularized(x,\n"
            + "0,3/4))/x)");
    check("InterquartileRange(NormalDistribution(x,y))", //
        "2*Sqrt(2)*y*InverseErfc(1/2)");
    check("InterquartileRange(StudentTDistribution(x))", //
        "2*Sqrt(x)*Sqrt(-1+1/InverseBetaRegularized(1/2,x/2,1/2))");
    check("InterquartileRange(WeibullDistribution(x,y))", //
        "-y*Log(4/3)^(1/x)+y*Log(4)^(1/x)");
  }

  @Test
  public void testIntersectingQ() {
    check("IntersectingQ({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "True");

    check("IntersectingQ(f(b, a, b,d), f(c,c,c,d))", //
        "True");
    check("IntersectingQ(f(b, a, b,d), f(e,g))", //
        "False");

    // same as ContainsAny
    check("IntersectingQ({b, a, b}, {a, b, c})", //
        "True");
    check("IntersectingQ({b, a, b,d}, {c,c,c,d})", //
        "True");
    check("IntersectingQ({d,f,e}, {a, b, c})", //
        "False");
    check("IntersectingQ({ }, {a, b, c})", //
        "False");

    check("IntersectingQ(1, {1,2,3})", //
        "IntersectingQ(1,{1,2,3})");
    check("IntersectingQ({1,2,3}, 4)", //
        "IntersectingQ({1,2,3},4)");

    check("IntersectingQ({1.0,2.0}, {1,2,3})", //
        "False");
    check("IntersectingQ({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "True");
  }

  @Test
  public void testInterrupt() {
    check("Print(test1); Interrupt(); Print(test2)", //
        "$Aborted");
  }



  @Test
  public void testInverseCDF() {
    // https://github.com/axkr/symja_android_library/issues/147
    check("InverseCDF(StudentTDistribution(24), 0.95)", //
        "1.71088");

    check("InverseCDF(BetaDistribution(2,3), 0.1)", //
        "0.142559");
    check("InverseCDF(BetaDistribution(2,3), 0.9)", //
        "0.679539");
    check("InverseCDF(CauchyDistribution(a, b), x)", //
        "ConditionalExpression(Piecewise({{a+b*Tan(Pi*(-1/2+x)),0<x<1},{-Infinity,x<=0}},Infinity),\n"
            + "0<=x<=1)");
    check("InverseCDF(ChiSquareDistribution(3), 0.1)", //
        "0.584374");
    check("InverseCDF(ChiSquareDistribution(3), 0.9)", //
        "6.25139");
    check("InverseCDF(ExponentialDistribution(3), 0.1)", //
        "0.0351202");
    check("InverseCDF(ExponentialDistribution(3), 0.9)", //
        "0.767528");
    check("InverseCDF(FRatioDistribution(2,3), 0.1)", //
        "0.109149");
    check("InverseCDF(FRatioDistribution(2,3), 0.9)", //
        "5.46238");
    check("InverseCDF(GammaDistribution(2,3), 0.1)", //
        "1.59543");
    check("InverseCDF(GammaDistribution(2,3), 0.9)", //
        "11.66916");
    check("InverseCDF(GumbelDistribution(2,3), 0.1)", //
        "-4.7511");
    check("InverseCDF(GompertzMakehamDistribution(2,3), 0.1)", //
        "0.0172588");
    check("InverseCDF(GumbelDistribution(2,3), 0.9)", //
        "4.5021");
    check("InverseCDF(LogNormalDistribution(2,3), 0.1)", //
        "0.15808");
    check("InverseCDF(LogNormalDistribution(2,3), 0.9)", //
        "345.3833");
    check("InverseCDF(NakagamiDistribution(2,3), 0.1)", //
        "0.89315");
    check("InverseCDF(NakagamiDistribution(2,3), 0.9)", //
        "2.41549");
    check("InverseCDF(NormalDistribution(2,3), 0.1)", //
        "-1.84465");
    check("InverseCDF(NormalDistribution(2,3), 0.9)", //
        "5.84465");
    check("InverseCDF(UniformDistribution({0,1}), 0.1)", //
        "0.1");
    check("InverseCDF(UniformDistribution({0,1}), 0.9)", //
        "0.9");
    check("InverseCDF(WeibullDistribution(2,3), 0.1)", //
        "0.973779");
    check("InverseCDF(WeibullDistribution(2,3), 0.9)", //
        "4.55228");

    check("InverseCDF(GammaDistribution(a,b,g,d))", //
        "ConditionalExpression(Piecewise({{d+b*InverseGammaRegularized(a,0,#1)^(1/g),0<#1<\n"
            + "1},{d,#1<=0}},Infinity),0<=#1<=1)&");
    check("InverseCDF(GompertzMakehamDistribution(m,n),k)", //
        "ConditionalExpression(Piecewise({{Log(1-Log(1-k)/n)/m,0<k<1},{0,k<=0}},Infinity),\n" + //
            "0<=k<=1)");
    check("InverseCDF(NormalDistribution(0,1))", //
        "ConditionalExpression(-Sqrt(2)*InverseErfc(2*#1),0<=#1<=1)&");
    check("InverseCDF(NormalDistribution( ))", //
        "ConditionalExpression(-Sqrt(2)*InverseErfc(2*#1),0<=#1<=1)&");
    check("InverseCDF(NormalDistribution( ), p)", //
        "ConditionalExpression(-Sqrt(2)*InverseErfc(2*p),0<=p<=1)");
    check("InverseCDF(NormalDistribution(n,m))", //
        "ConditionalExpression(n-Sqrt(2)*m*InverseErfc(2*#1),0<=#1<=1)&");
    check("InverseCDF(NormalDistribution(n,m), p)", //
        "ConditionalExpression(n-Sqrt(2)*m*InverseErfc(2*p),0<=p<=1)");

    check("InverseCDF(NormalDistribution(0, 1), {x, y})", //
        "{ConditionalExpression(-Sqrt(2)*InverseErfc(2*x),0<=x<=1),ConditionalExpression(-Sqrt(\n"
            + "2)*InverseErfc(2*y),0<=y<=1)}");
    check("InverseCDF(StudentTDistribution(n))", //
        "ConditionalExpression(Piecewise({{-Sqrt(n)*Sqrt(-1+1/InverseBetaRegularized(2*#1,n/\n"
            + "2,1/2)),0<#1<1/2},{0,#1==1/2},{Sqrt(n)*Sqrt(-1+1/InverseBetaRegularized(2*(1-#1),n/\n"
            + "2,1/2)),1/2<#1<1},{-Infinity,#1<=0}},Infinity),0<=#1<=1)&");
  }



  @Test
  public void testInverseFunction() {
    check("InverseFunction(Log2)", //
        "2^#1&");
    check("InverseFunction(Log10)", //
        "10^#1&");
    check("InverseFunction(Erfi)", //
        "-I*InverseErf(I*#1)&");

    check("InverseFunction(Power,1,2)", //
        "#1^(1/#2)&");
    check("InverseFunction(Power,2,2)", //
        "Log");
    check("InverseFunction(Power,2,2)[x^y]", //
        "Log(x^y)");
    check("InverseFunction(Power,2,2)[x,y]", //
        "Log(y)/Log(x)");


    check("InverseFunction(2^# &)", //
        "Log(#1)/Log(2)&");

    check("InverseFunction(Tanh)", //
        "ArcTanh");
    check("InverseFunction(Tanh(2*#)&)", //
        "ArcTanh(#1)/2&");
    check("InverseFunction(Tanh(#/2)&)", //
        "2*ArcTanh(#1)&");
    check("InverseFunction(Tanh(#+1)&)", //
        "-1+ArcTanh(#1)&");

    check("InverseFunction(ArcTan,1,2)", //
        "(I*(1+Cos(2*#1)+I*Sin(2*#1)+#2))/(-1+Cos(2*#1)+I*Sin(2*#1))&");
    check("InverseFunction(ArcTan,2,2)", //
        "(-I*(-1+Cos(2*#2)+I*Sin(2*#2))*#1)/(1+Cos(2*#2)+I*Sin(2*#2))&");

    check("Solve((a0*x^p+a1*x^q)==0,x)", //
        "{{x->E^((-I*Pi+Log(a0)-Log(a1))/(-p+q))}}");
    check("Solve(c*x*a^x==12,x)", //
        "{{x->ProductLog((12*Log(a))/c)/Log(a)}}");

    check("InverseFunction(#*a^# &)", //
        "ProductLog(#1*Log(a))/Log(a)&");
    check("InverseFunction(c*#*a^# &)", //
        "ProductLog((#1*Log(a))/c)/Log(a)&");

    check("InverseFunction(#^p &)", //
        "#1^(1/p)&");
    check("InverseFunction(Composition(f, g, h))[x]", //
        "InverseFunction(h)[InverseFunction(g)[InverseFunction(f)[x]]]");
    check("Sin @* InverseFunction(Tan)", //
        "Sin@*ArcTan");

    check("D(InverseFunction(Sin)[x],x)", //
        "1/Sqrt(1-x^2)");
    check("D(InverseFunction(f)[x],x)", //
        "1/f'(InverseFunction(f)[x])");
    check("D(InverseFunction(f(g))[x],x)", //
        "1/f(g)'[InverseFunction(f(g))[x]]");
    check("InverseFunction((a*#)/(c*#) &)", //
        "InverseFunction((a*#1)/(c*#1)&)");
    check("InverseFunction((a*#)/(c*# + d) &)", //
        "(#1*d)/(a*(1+(-#1*c)/a))&");
    check("InverseFunction((a*# + b)/(c*# + d) &)", //
        "(-b+#1*d)/(a-#1*c)&");
    check("InverseFunction((a * # + b)&)", //
        "(#1-b)/a&");
    check("InverseFunction(Abs)", //
        "-#1&");
    check("InverseFunction(Sin)", //
        "ArcSin");

    check("f = 2 # & @*Cases(_Symbol)", //
        "(2*#1&)@*Cases(_Symbol)");
    check("f({a, \"b\", c, d, \"e\", 5})", //
        "{2*a,2*c,2*d}");
  }

  @Test
  public void testInverseGudermannian() {
    check("N(InverseGudermannian(4/3), 50)", //
        "2.126176090782269391936236271172156185450325122338");
    check("N(InverseGudermannian(4/3-2/3*I), 50)", //
        "1.0709631602353002334250501606064505306184199829854+I*(-1.253841344203559414899366482437511230389585771795)");
    check("InverseGudermannian(3.75)", //
        "-0.649839+I*3.14159");
    check("InverseGudermannian(1.111111)", //
        "1.45253");
    check("InverseGudermannian(100.0)", //
        "-0.55783");
    check("InverseGudermannian(I*Pi+5.0)", //
        "-0.0829127+I*1.54624");

    check("InverseGudermannian({1.5, 3.75, 5.5, 7.25})", //
        "{3.34068,-0.649839+I*3.14159,-0.878248,1.1663}");
  }

  @Test
  public void testInverseHaversine() {

    check("N(InverseHaversine(1/7), 50)", //
        "0.77519337331036130720409371118247342579981743402299");
    check("N(InverseHaversine(798+I), 50)", //
        "3.1403387355052352207114821042916462562377389087353+I*8.067776883665043131189388013520931297785865736445");
    checkNumeric("InverseHaversine(0.5)", //
        "1.5707963267948968");
    checkNumeric("InverseHaversine(1 + 2.5 * I)", //
        "1.764589463349828+I*2.3309746530493123");
    check("InverseHaversine(1/4)", //
        "Pi/3");
    checkNumeric("InverseHaversine(0.7)", //
        "1.9823131728623846");
    // Java double machine precision
    // check("ArcSin(1.3038404810405)",
    // "1.5707963267948966+I*(-0.7610396837317912)");
    // apfloat/apcomplex precision

    // TODO use ExprParser#getReal() if apfloat problems are fixed
    // check("ArcSin(1.3038404810405297)",
    // "1.5707963267948966+I*(-7.610396837318266e-1)");

    checkNumeric("N(ArcSin(-2),30)", //
        "-1.57079632679489661923132169163+I*1.3169578969248167086250463473");
    checkNumeric("N(ArcSin(2),30)", //
        "1.5707963267948966192313216916+I*(-1.3169578969248167086250463473)");

    checkNumeric("ArcSin(-2.0)", //
        "-1.5707963267948966+I*1.3169578969248164");
    checkNumeric("ArcSin(2.0)", //
        "1.5707963267948966+I*(-1.3169578969248166)");
    checkNumeric("ArcSin(1.3038404810405297)", //
        "1.570796326794896+I*(-0.7610396837318266)");
    checkNumeric("InverseHaversine(1.7)", //
        "3.141592653589793+I*(-1.5220793674636532)");
  }

  @Test
  public void testJaccardDissimilarity() {
    check("JaccardDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
        "3/5");
    check("JaccardDissimilarity({True, False, True}, {True, True, False})", //
        "2/3");
    check("JaccardDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
        "0");
    check("JaccardDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
        "1");
  }

  @Test
  public void testJacobiSymbol() {
    check("JacobiSymbol(12345, 331)", //
        "-1");
    check("JacobiSymbol(98, 331)", //
        "-1");
    check("JacobiSymbol(10^10+1, Prime(1000))", //
        "1");
    check("JacobiSymbol(10^11+1, Prime(2000))", //
        "-1");
    check("JacobiSymbol(10, 5)", //
        "0");
    check("Table(f(n, m), {n, 0, 10}, {m, 1, n, 2})", //
        "{{},{f(1,1)},{f(2,1)},{f(3,1),f(3,3)},{f(4,1),f(4,3)},{f(5,1),f(5,3),f(5,5)},{f(\n"
            + "6,1),f(6,3),f(6,5)},{f(7,1),f(7,3),f(7,5),f(7,7)},{f(8,1),f(8,3),f(8,5),f(8,7)},{f(\n"
            + "9,1),f(9,3),f(9,5),f(9,7),f(9,9)},{f(10,1),f(10,3),f(10,5),f(10,7),f(10,9)}}");
    check("Table(JacobiSymbol(n, m), {n, 0, 10}, {m, 1, n, 2})", //
        "{{},{1},{1},{1,0},{1,1},{1,-1,0},{1,0,1},{1,1,-1,0},{1,-1,-1,1},{1,0,1,1,0},{1,1,\n"
            + "0,-1,1}}");
    check("JacobiSymbol(1001, 9907)", //
        "-1");
    check("JacobiSymbol({2, 3, 5, 7, 11}, 3)", //
        "{-1,0,-1,1,-1}");
    check("JacobiSymbol(3, {1, 3, 5, 7})", //
        "{1,0,-1,-1}");
    check("JacobiSymbol(7, 6)", //
        "1");
    // check("JacobiSymbol(n, 1)", "n");
    check("JacobiSymbol(-3, {1, 3, 5, 7})", //
        "{1,0,-1,1}");
  }

  @Test
  public void testJavaForm() {
    EvalEngine.resetModuleCounter4JUnit();
    check("JavaForm(Sqrt(x), Complex)", //
        "(x).sqrt()");
    check("JavaForm(1/(x+y), Complex)", //
        "(x.add(y)).reciprocal()");
    check("JavaForm(1/Sqrt(x), Complex)", //
        "(x).reciprocal().sqrt()");
    check("JavaForm(x^(-1/2), Complex)", //
        "(x).reciprocal().sqrt()");

    // check("JavaForm((x+1)^2+(x+1)^3+x*y*10*I, Complex)", //
    // "Complex f1(Complex x, Complex y) {\n" //
    // + "Complex v1 = Complex.valueOf(1.0).add(x);\n"
    // + "return
    // (v1).pow(Complex.valueOf(2.0)).add((v1).pow(Complex.valueOf(3.0)).add(Complex.valueOf(0.0,
    // 10.0).multiply(x.multiply(y))));\n"
    // + "}\n" + "");
    // check("JavaForm((x+1)^2+(x+1)^3, Float)", //
    // "double f2(double x) {\n" //
    // + "double v1 = 1+x;\n" + "return Math.pow(v1,2)+Math.pow(v1,3);\n" + "}\n" + "");
    check("JavaForm(f(123456789123456789))", //
        "$(f,ZZ(123456789123456789L))");

    check("JavaForm(E^3-Cos(Pi^2/x), Prefix->True)", //
        "F.Subtract(F.Exp(F.C3),F.Cos(F.Times(F.Sqr(F.Pi),F.Power(F.x,F.CN1))))");
    check("JavaForm(E^3-Cos(Pi^2/x), Float->True)", //
        "20.085536923187668-Math.cos(9.869604401089358/x)");
    check("JavaForm(E^3-Cos(Pi^2/x), Float)", //
        "20.085536923187668-Math.cos(9.869604401089358/x)");

    check("JavaForm(Hold(D(sin(x)*cos(x),x)), prefix->True)", //
        "F.D(F.Times(F.Sin(F.x),F.Cos(F.x)),F.x)");
    check("JavaForm(Hold(D(sin(x)*cos(x),x)))", //
        "D(Times(Sin(x),Cos(x)),x)");
    check("JavaForm(D(sin(x)*cos(x),x), prefix->True)", //
        "F.Subtract(F.Sqr(F.Cos(F.x)),F.Sqr(F.Sin(F.x)))");
    check("JavaForm(D(sin(x)*cos(x),x))", //
        "Subtract(Sqr(Cos(x)),Sqr(Sin(x)))");
    check("JavaForm(I/2*E^((-I)*x)-I/2*E^(I*x), Prefix->True)", //
        "F.Plus(F.Times(F.CC(0L,1L,1L,2L),F.Exp(F.Times(F.CNI,F.x))),F.Times(F.CC(0L,1L,-1L,2L),F.Exp(F.Times(F.CI,F.x))))");
    check("JavaForm(I/2*E^((-I)*x)-I/2*E^(I*x))", //
        "Plus(Times(CC(0L,1L,1L,2L),Exp(Times(CNI,x))),Times(CC(0L,1L,-1L,2L),Exp(Times(CI,x))))");
    check("JavaForm(a+b+x^2+I+7+3/4+x+y, Prefix->True)", //
        "F.Plus(F.CC(31L,4L,1L,1L),F.a,F.b,F.x,F.Sqr(F.x),F.y)");
    check("JavaForm(a+b+x^2+I+7+3/4+x+y)", //
        "Plus(CC(31L,4L,1L,1L),a,b,x,Sqr(x),y)");

    check("JavaForm((x+y)^-1, Prefix->True)", //
        "F.Power(F.Plus(F.x,F.y),F.CN1)");
    check("JavaForm((x+y)^-1, Float)", //
        "1.0/(x+y)");
  }

  @Test
  public void testJSForm() {
    EvalEngine.resetModuleCounter4JUnit();
    // check("JSForm(Ramp(x))", //
    // "((x>=0) ? x : ( 0 ))");


    check("JSForm( {Identity( x ),Log( y )} )", //
        "[x,Math.log(y)]");
    check("JSForm(Sign(x)*Abs(x)^(1/3))", //
        "Math.cbrt(Math.abs(x))*Math.sign(x)");
    check("JSForm(Clip(x))", //
        "\n" //
            + " (function() {\n" //
            + "if (x<-1) { return -1;}\n" //
            + "if (x>1) { return 1;}\n" //
            + " return x;})()\n");
    check("JSForm(Clip(x, {-2, 4}))", //
        "\n" + " (function() {\n" + "if (x<-2) { return -2;}\n" + "if (x>4) { return 4;}\n"
            + " return x;})()\n" + "");

    check("JSForm(E^3-Cos(Pi^2/x))", //
        "20.085536923187668-Math.cos(9.869604401089358/x)");

    check("Piecewise({{x, 0 < x < 1}, {x^3, 1 < x < 2}}) // JSForm", //
        "\n" //
            + " (function() {\n" //
            + "if (0<x && x<1) { return x;}\n" //
            + "if (1<x && x<2) { return Math.pow(x,3);}\n" //
            + " return 0;})()\n" //
            + "");
    check("JSForm(4*EllipticE(x)+KleinInvariantJ(t)^3, \"Mathcell\")", //
        "add(mul(4,ellipticE(x)),pow(kleinJ(t),3))");
    check("JSForm(Cot(x))", //
        "(1/Math.tan(x))");
    check("JSForm(ArcCot(x))", //
        "((Math.PI/2.0)-Math.atan(x))");
    check("JSForm( Piecewise({{x^2, x < 0}, {x, x >= 0&&x<1},{Cos(x-1), x >= 1}}) )", //
        "\n" + " (function() {\n" + "if (x<0) { return Math.pow(x,2);}\n"
            + "if (x>=0&&x<1) { return x;}\n" + "if (x>=1) { return Math.cos(1-x);}\n"
            + " return 0;})()\n" + "");
    check("JSForm(ConditionalExpression(Log(1- q), 0 <=q<=1))", //
        "((0<=q && q<=1) ? (Math.log(1-q)) : ( Number.NaN ))");
    check("JSForm(x < 10 && y > 1)", //
        "x<10&&y>1");
    check("JSForm(a<b)", //
        "a<b");
    check("JSForm(a+b)", //
        "a+b");
    check("JSForm(E^3-Cos(Pi^2/x) )", //
        "20.085536923187668-Math.cos(9.869604401089358/x)");
    // JSXGraph.org syntax
    EvalEngine.resetModuleCounter4JUnit();
    // check(
    // "JSForm(Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}, PlotRange->{-1,2}), {a,0,10}))",
    // //
    // "var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,showCopyright:false,boundingbox:[-0.8641592653589794,2.7,7.147344572538565,-1.7]});\n"
    // + "board.suspendUpdate();\n"
    // + "var a =
    // board.create('slider',[[-0.0630088815692249,2.2600000000000002],[6.346194188748811,2.2600000000000002],[0,0,10]],{name:'a'});\n"
    // + "\n"
    // + "function $f1(x) { try { return [mul(cos(add(1,mul(a.Value(),x))),sin(x))];} catch(e) {
    // return Number.NaN;} }\n"
    // + "board.create('functiongraph',[$f1, 0, (6.283185307179586)],{strokecolor:'#5e81b5'});\n"
    // + "\n" + "\n" + "board.unsuspendUpdate();\n" + "");

    // Mathcell syntax / generate TeX for MathJAX
    check("JSForm(Manipulate(Factor(x^n + 1), {n, 1, 5, 1}))", //
        "var parent = document.currentScript.parentNode;\n" //
            + "var id = generateId();\n" //
            + "parent.id = id;\n" //
            + "MathCell( id, [ { type: 'slider', min: 1.0, max: 5.0, step: 1.0, name: 'n', label: 'n' }\n" //
            + " ] );\n" //
            + "\n" //
            + "parent.update = function( id ) {\n" //
            + "\n" //
            + "var n = getVariable(id, 'n');\n" //
            + "\n" //
            + "\n" //
            + "var expressions = [ '1 + x',\n" //
            + "'1 + {x}^{2}',\n" //
            + "'\\\\\\\\left( 1 + x\\\\\\\\right)  \\\\\\\\cdot \\\\\\\\left( 1 - x + {x}^{2}\\\\\\\\right) ',\n" //
            + "'1 + {x}^{4}',\n" //
            + "'\\\\\\\\left( 1 + x\\\\\\\\right)  \\\\\\\\cdot \\\\\\\\left( 1 - x + {x}^{2} - {x}^{3} + {x}^{4}\\\\\\\\right) ' ];\n" //
            + "\n" //
            + "  var data = '\\\\\\\\[' + expressions[Math.trunc((n-1.0)/1.0)] + '\\\\\\\\]';\n" //
            + "\n" //
            + "  data = data.replace( /\\\\\\\\/g, '&#92;' );\n" //
            + "\n" //
            + "  var config = {type: 'text', center: true };\n" //
            + "\n" //
            + "  evaluate( id, data, config );\n" //
            + "\n" //
            + "  MathJax.Hub.Queue( [ 'Typeset', MathJax.Hub, id ] );\n" //
            + "\n" //
            + "}\n" //
            + "parent.update( id );\n" //
            + "");

    // JSXGraph.org syntax
    // @Ignore Deactivate, because of change to Graphics output
    // check("JSForm(ListPlot(Prime(Range(25))))", //
    // "var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-1.85,102.3,27.85,-3.3]});\n"
    // + "board.suspendUpdate();\n" + "\n"
    // + "board.create('point', [function() {return 1;},function() {return 2;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + //
    // "board.create('point', [function() {return 2;},function() {return 3;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 3;},function() {return 5;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 4;},function() {return 7;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 5;},function() {return 11;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 6;},function() {return 13;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 7;},function() {return 17;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + //
    // "board.create('point', [function() {return 8;},function() {return 19;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 9;},function() {return 23;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 10;},function() {return 29;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 11;},function() {return 31;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 12;},function() {return 37;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 13;},function() {return 41;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 14;},function() {return 43;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 15;},function() {return 47;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 16;},function() {return 53;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 17;},function() {return 59;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 18;},function() {return 61;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 19;},function() {return 67;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 20;},function() {return 71;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 21;},function() {return 73;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 22;},function() {return 79;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 23;},function() {return 83;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 24;},function() {return 89;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "board.create('point', [function() {return 25;},function() {return 97;}], {color:'#5e81b5'
    // ,name:'', face:'o', size: 2 } );\n"
    // + "\n" + "\n" + "board.unsuspendUpdate();\n" + "");
    check("JSForm((x+y)^-1)", //
        "1.0/(x+y)");
  }

  @Test
  public void testJoin() {
    check("Join(<|a->0,b:>1|>,SparseArray({0,0}))", //
        "Join(<|a->0,b:>1|>,SparseArray(Number of elements: 0 Dimensions: {2} Default value: 0))");

    check("s=SparseArray({{1,0,1},{1,2,0}})", //
        "SparseArray(Number of elements: 4 Dimensions: {2,3} Default value: 0)");
    check("Join(s,{{1,0,1,3},{1,2,0,4}})", //
        "{{1,0,1},{1,2,0},{1,0,1,3},{1,2,0,4}}");
    check("Join(s,s)// MatrixForm", //
        "{{1,0,1},\n" + //
            " {1,2,0},\n" + //
            " {1,0,1},\n" + //
            " {1,2,0}}");
    check("Join(s,s,s) // MatrixForm", //
        "{{1,0,1},\n" + //
            " {1,2,0},\n" + //
            " {1,0,1},\n" + //
            " {1,2,0},\n" + //
            " {1,0,1},\n" + //
            " {1,2,0}}");

    check("Join(<|A->1,B->2,C->3,D->4|>,<|A->1,B->2,C->3,D->4|>)", //
        "<|A->1,B->2,C->3,D->4|>");
    check("Join(<|a -> b|>, <|c -> d, a -> e|>)", //
        "<|a->e,c->d|>");

    // http://oeis.org/A001597 - Perfect powers: m^k where m > 0 and k >= 2. //
    check("Join({1}, Select(Range(1770), GCD@@FactorInteger(#)[[All, 2]]>1&))", //
        "{1,4,8,9,16,25,27,32,36,49,64,81,100,121,125,128,144,169,196,216,225,243,256,289,\n"
            + "324,343,361,400,441,484,512,529,576,625,676,729,784,841,900,961,1000,1024,1089,\n"
            + "1156,1225,1296,1331,1369,1444,1521,1600,1681,1728,1764}");

    check("Join({a, b}, {c, d, e})", //
        "{a,b,c,d,e}");
    check("Join({{a, b}, {c, d}}, {{1, 2}, {3, 4}})", //
        "{{a,b},{c,d},{1,2},{3,4}}");
    check("Join(a + b, c + d, e + f)", //
        "a+b+c+d+e+f");
    check("Join(a + b, c * d)", //
        "Join(a+b,c*d)");
    check("Join(x + y, z)", //
        "Join(x+y,z)");
    check("Join(x + y, y * z, a)", //
        "Join(x+y,y*z,a)");
    check("Join(x, y + z, y * z)", //
        "Join(x,y+z,y*z)");

    check("Join(x, y)", //
        "Join(x,y)");
    check("Join({a,b}, {x,y,z})", //
        "{a,b,x,y,z}");
    check("Join({{a, b}, {x, y}}, {{1, 2}, {3, 4}})", //
        "{{a,b},{x,y},{1,2},{3,4}}");
  }

  @Test
  public void testKhinchin() {
    check("Khinchin // N", //
        "2.68545");
    check("N(Khinchin,50)", //
        "2.6854520010653064453097148354817956938203822939944");
  }

  @Test
  public void testKleinInvariantJ() {
    //
    check("KleinInvariantJ(-1.5707963267948966)", //
        "KleinInvariantJ(-1.5707963267948966)");
    check("KleinInvariantJ( (1 + I*3*Sqrt(3))/2 )", //
        "-64000/9");
    check("KleinInvariantJ( (1 + I*Sqrt(163))/2 )", //
        "-151931373056000");
    check("KleinInvariantJ(I*Infinity)", //
        "Infinity");
    check("KleinInvariantJ(E^(2*Pi*(I/3)))", //
        "0");
    check("KleinInvariantJ(I)", //
        "1");
    check("KleinInvariantJ(I-42)", //
        "1");
    check("KleinInvariantJ(I+42)", //
        "1");
    check("KleinInvariantJ(1 + 2.0*I) // Chop", //
        "166.375");
    check("Table(KleinInvariantJ(x+I)//Chop, {x,-2.0, 2.0, 1/4})", //
        "{1.0,0.387321+I*(-0.100372),-0.051849,0.387321+I*0.100372,1.0,0.387321+I*(-0.100372),-0.051849," //
            + "0.387321+I*0.100372,1.0,0.387321+I*(-0.100372),-0.051849,0.387321+I*0.100372,1.0," //
            + "0.387321+I*(-0.100372),-0.051849,0.387321+I*0.100372,1.0}");
  }

  @Test
  public void testKronekerSymbol() {

    check("KroneckerSymbol(2,3)", //
        "-1");
    check("Table(KroneckerSymbol(n, m), {n, 0, 5}, {m, 0, 5})", //
        "{{0,1,0,0,0,0},{1,1,1,1,1,1},{0,1,0,-1,0,-1},{0,1,-1,0,1,-1},{0,1,0,1,0,1},{0,1,-\n" //
            + "1,-1,1,0}}");
    check("Table(KroneckerSymbol(n, m), {n, -5, -1}, {m, -5, -1})", //
        "{{0,-1,-1,1,-1},{-1,0,1,0,-1},{1,-1,0,1,-1},{1,0,-1,0,-1},{-1,-1,1,-1,-1}}");
    check("KroneckerSymbol(10,13)", //
        "1");
    check("KroneckerSymbol(10^11 + 1, Prime(2000))", //
        "-1");
    check("KroneckerSymbol({2, 3, 5, 7, 11}, 6)", //
        "{0,0,1,1,1}");

  }

  @Test
  public void testValues() {
    check("Values(<|a :> 1 + 1, b -> Nothing|>, Hold)", //
        "{Hold(1+1),Hold(Nothing)}");

    check("Values(<|a -> x, b -> y|>)", //
        "{x,y}");
    check("Values({a -> x, b -> y})", //
        "{x,y}");
    check("Values({<|a -> x, b -> y|>, {c -> z, {}}})", //
        "{{x,y},{z,{}}}");
    check("Values({c -> z, b -> y, a -> x})", //
        "{z,y,x}");
    check("Values(a->x)", //
        "x");
    check("Values({a -> x, a -> y, {a -> z, <|b -> t|>, <||>, {}}})", //
        "{x,y,{z,{t},{},{}}}");
    check("Values({a -> x, a -> y, <|a -> z, {b -> t}, <||>, {}|>})", //
        "{x,y,{z,t}}");
    check("Values(<|a -> x, a -> y, <|a -> z, <|b -> t|>, <||>, {}|>|>)", //
        "{z,t}");
    check("Values(<|a -> x, a -> y, {a -> z, {b -> t}, <||>, {}}|>)", //
        "{z,t}");
    check("Values({a -> x, <|a -> y, b|>})", //
        "{x,Values(Association(a->y,b))}");

    check("Values(k:>v,f)", //
        "f(v)");
    check("Values(<|ahey->avalue, bkey->bvalue, ckey->cvalue|>)", //
        "{avalue,bvalue,cvalue}");
    check("Values(<|a :> 1 + 1, b -> Nothing|>, Hold)", //
        "{Hold(1+1),Hold(Nothing)}");
    check("Values( <|a -> 4, b -> 2, c -> 1, d -> 5|> )", //
        "{4,2,1,5}");
    check("Values({ahey->avalue, bkey->bvalue, ckey->cvalue})", //
        "{avalue,bvalue,cvalue}");
    check("Values({<|a -> 1, b -> 2|>, {w -> 3, {}}})", //
        "{{1,2},{3,{}}}");
  }

  @Test
  public void testKolmogorovSmirnovTest() {
    check("data1 = {\n"
        + "            0.53236606, -1.36750258, -1.47239199, -0.12517888, -1.24040594, 1.90357309,\n"
        + "            -0.54429527, 2.22084140, -1.17209146, -0.68824211, -1.75068914, 0.48505896,\n"
        + "            2.75342248, -0.90675303, -1.05971929, 0.49922388, -1.23214498, 0.79284888,\n"
        + "            0.85309580, 0.17903487, 0.39894754, -0.52744720, 0.08516943, -1.93817962,\n"
        + "            0.25042913, -0.56311389, -1.08608388, 0.11912253, 2.87961007, -0.72674865,\n"
        + "            1.11510699, 0.39970074, 0.50060532, -0.82531807, 0.14715616, -0.96133601,\n"
        + "            -0.95699473, -0.71471097, -0.50443258, 0.31690224, 0.04325009, 0.85316056,\n"
        + "            0.83602606, 1.46678847, 0.46891827, 0.69968175, 0.97864326, 0.66985742,\n"
        + "-0.20922486, -0.15265994}", //
        "{0.532366,-1.3675,-1.47239,-0.125179,-1.24041,1.90357,-0.544295,2.22084,-1.17209<<SHORT>>", //
        80);
    check("KolmogorovSmirnovTest(SparseArray(data1))", //
        "0.744855");
    check("KolmogorovSmirnovTest(data1)", //
        "0.744855");
    check("KolmogorovSmirnovTest(data1, NormalDistribution(), \"TestData\")", //
        "{0.0930213,0.744855}");

    check("data2 = {\n"
        + "            0.95791391, 0.16203847, 0.56622013, 0.39252941, 0.99126354, 0.65639108,\n"
        + "            0.07903248, 0.84124582, 0.76718719, 0.80756577, 0.12263981, 0.84733360,\n"
        + "            0.85190907, 0.77896244, 0.84915723, 0.78225903, 0.95788055, 0.01849366,\n"
        + "            0.21000365, 0.97951772, 0.60078520, 0.80534223, 0.77144013, 0.28495121,\n"
        + "0.41300867, 0.51547517, 0.78775718, 0.07564151, 0.82871088, 0.83988694}", //
        "{0.957914,0.162038,0.56622,0.392529,0.991264,0.656391,0.0790325,0.841246,0.76718<<SHORT>>", //
        80);
    check("KolmogorovSmirnovTest(data1, data2)", //
        "0.000438682");
    check("KolmogorovSmirnovTest(data1, data2, \"TestData\")", //
        "{0.46,0.000438682}");
  }

  @Test
  public void testKroneckerDelta() {
    // TODO
    // check("KroneckerDelta(0,z0)*KroneckerDelta(1,z0)", //
    // "0");
    check("KroneckerDelta(2 - I, 2. - I)", //
        "1");
    check("KroneckerDelta(n,0)", //
        "KroneckerDelta(0,n)");

    check("KroneckerDelta( )", //
        "1");
    check("KroneckerDelta(0)", //
        "1");
    check("KroneckerDelta(1)", //
        "0");
    check("KroneckerDelta(42)", //
        "0");
    check("KroneckerDelta(42, 42.0, 42)", //
        "1");
    check("KroneckerDelta(0,1)", //
        "0");
    check("KroneckerDelta(2,2)", //
        "1");
    check("KroneckerDelta(2,2.0)", //
        "1");

    check("KroneckerDelta(1,1,1,2)", //
        "0");

    check("Table(KroneckerDelta(n), {n, -2, 2})", //
        "{0,0,1,0,0}");
    check("Array(KroneckerDelta, {3, 3})", //
        "{{1,0,0},{0,1,0},{0,0,1}}");
    check("Table((KroneckerDelta(i - j + 1) + KroneckerDelta(i - j + 2))*i*j^2, {i, 5}, {j, 5})", //
        "{{0,4,9,0,0},{0,0,18,32,0},{0,0,0,48,75},{0,0,0,0,100},{0,0,0,0,0}}");
  }

  @Test
  public void testLambertW() {
    check("LambertW(x)", //
        "ProductLog(x)");
  }



  @Test
  public void testInverseLaplaceTransform() {
    // check(
    // "InverseLaplaceTransform(1/(s^3+4*s^2+s),s,t)", //
    // "");
    check("InverseLaplaceTransform(1/(s^3+2*s^2+s),s,t)", //
        "1-1/E^t-t/E^t");
    check("InverseLaplaceTransform(f(x)*s,s,t)", //
        "f(x)*DiracDelta'(t)");
    check("InverseLaplaceTransform(f(x),s,t)", //
        "DiracDelta(t)*f(x)");
    check("InverseLaplaceTransform(1/s,s,t)", //
        "1");
    check("InverseLaplaceTransform(1/s^5,s,t)", //
        "t^4/24");
    check("InverseLaplaceTransform(1/(s^2 +a^2),s,t)", //
        "Sin(a*t)/a");
    check("InverseLaplaceTransform(s/(s^2 +a^2),s,t)", //
        "Cos(a*t)");
    check("InverseLaplaceTransform(1/(1+s),s,t)", //
        "E^(-t)");
    check("InverseLaplaceTransform(1/(s^2-4),s,t)", //
        "(-1+E^(4*t))/(4*E^(2*t))");
    // test partial fraction decomposition:
    check("InverseLaplaceTransform(Together(3/(s-1)+(2*s)/(s^2+4)),s,t)", //
        "3*E^t+2*Cos(2*t)");
    check("InverseLaplaceTransform(3/(s-1)+(2*s)/(s^2+4),s,t)", //
        "3*E^t+2*Cos(2*t)");
  }

  @Test
  public void testInverseWeierstrassP() {
    // check("InverseWeierstrassP(2.0,{1,2})", //
    // "-0.715096");
    // check("Table[InverseWeierstrassP[x, {1, 2}], {x, 2.0, 6.0}]",
    // "");
  }

  @Test
  public void testInverseLaplaceTransformNumeric() {
    // check("InverseLaplaceTransform(Erf(s)/Sqrt(s), s, 2.3)", //
    // "");

    check("InverseLaplaceTransform(s/(s + 2)^2, s, 2.3)", //
        "-0.0362221");

  }

  @Test
  public void testLaplaceTransform() {
    check("LaplaceTransform(t*UnitStep(a*t),t,s)", //
        "LaplaceTransform(t*UnitStep(a*t),t,s)");
    check("LaplaceTransform(UnitStep(a*t),t,s)", //
        "Which(Sign(a)==1,1/s,Sign(a)==-1,0,True,0)");
    check("LaplaceTransform(UnitStep(42*t),t,s)", //
        "1/s");
    // numerical calculation only supported for unary functions
    check("LaplaceTransform(1/(x + y + 1), {x, y}, {5.4, 4.5})", //
        "LaplaceTransform(1/(1+x+y),{x,y},{5.4,4.5})");

    check("LaplaceTransform(Erf(t)/Sqrt(t), t, 14.2)", //
        "0.0185749");
    check("LaplaceTransform(Erf(t), t, 14.2)", //
        "0.00554208");

    check("LaplaceTransform(Tanh(t),t,s)", //
        "1/2*(-2/s-PolyGamma(0,s/4)+PolyGamma(0,1/4*(2+s)))");
    check("LaplaceTransform(E^2,t,-3+s)", //
        "E^2/(-3+s)");
    check("LaplaceTransform(c*t^2, t, s)", //
        "(2*c)/s^3");
    check("LaplaceTransform((t^3+t^4)*t^2, t, s)", //
        "720/s^7+120/s^6");
    check("LaplaceTransform(t^2*Exp(2+3*t), t, s)", //
        "(2*E^2)/(-3+s)^3");
    check("LaplaceTransform(Exp(2+3*t)/t, t, s)", //
        "E^2*LaplaceTransform(1/t,t,-3+s)");

    check("LaplaceTransform(y'(t),t,s)", //
        "s*LaplaceTransform(y(t),t,s)-y(0)");
    check("LaplaceTransform(y''(t),t,s)", //
        "s^2*LaplaceTransform(y(t),t,s)-s*y(0)-y'(0)");

    check("LaplaceTransform(t, t, t)", //
        "LaplaceTransform(t,t,t)");
    check("LaplaceTransform(t, t, s)", //
        "1/s^2");
    check("LaplaceTransform(t, s, t)", //
        "1");
    check("LaplaceTransform(s, t, t)", //
        "LaplaceTransform(s,t,t)");
    check("LaplaceTransform(E^(-t), t, s)", //
        "1/(1+s)");
    check("LaplaceTransform(t^4*Sin(t), t, s)", //
        "(384*s^4)/(1+s^2)^5+(-288*s^2)/(1+s^2)^4+24/(1+s^2)^3");
    check("LaplaceTransform(t^(1/2), t, s)", //
        "Sqrt(Pi)/(2*s^(3/2))");
    check("LaplaceTransform(t^(1/3), t, s)", //
        "Gamma(4/3)/s^(4/3)");
    check("LaplaceTransform(t^a, t, s)", //
        "Gamma(1+a)/s^(1+a)");
    // issue #941
    check("LaplaceTransform(Cos(t), t, s)", //
        "s/(1+s^2)");
    check("LaplaceTransform(Cos(a*b*t), t, s)", //
        "s/(a^2*b^2+s^2)");
    check("LaplaceTransform(Sin(t), t, s)", //
        "1/(1+s^2)");
    check("LaplaceTransform(Sin(a*b*t), t, s)", //
        "(a*b)/(a^2*b^2+s^2)");

    check("LaplaceTransform(Sin(t), t, t)", //
        "LaplaceTransform(Sin(t),t,t)");
    check("LaplaceTransform(Sinh(t), t, s)", //
        "c/(-1+s^2)");
    check("LaplaceTransform(Cosh(t), t, s)", //
        "s/(-1+s^2)");
    check("LaplaceTransform(Log(t), t, s)", //
        "-(EulerGamma+Log(s))/s");
    check("LaplaceTransform(Log(t)^2, t, s)", //
        "(6*EulerGamma^2+Pi^2+6*Log(s)*(2*EulerGamma+Log(s)))/(6*s)");
    check("LaplaceTransform(Erf(t), t, s)", //
        "(E^(s^2/4)*Erfc(s/2))/s");
    check("LaplaceTransform(Erf(t^(1/2)), t, s)", //
        "1/(s*Sqrt(1+s))");

    check("LaplaceTransform(Sin(t)*Exp(t), t, s)", //
        "1/(1+(1-s)^2)");
  }

  @Test
  public void testLast() {
    check("Last(SparseArray({1,2,3,4})) // Normal ", //
        "4");
    check("Last(SparseArray({{1,2},{3,4}})) // Normal ", //
        "{3,4}");
    check("Last(<||>)", //
        "Last(<||>)");
    check("Last({})", //
        "Last({})");
    check("Last({{1,2},{1,2},{1,2},{1,2}})", //
        "{1,2}");
    check("Last(<|1 :> a, 2 -> b, 3 :> c|>)", //
        "c");
    check("Last({}, x)", //
        "x");
    check("Last({a, b, c})", //
        "c");
    check("Last(a + b + c)", //
        "c");
    check("Last(a)", //
        "Last(a)");
  }

  @Test
  public void testLCM() {
    check("LCM(4+I, 1+16*I)", //
        "65+I*12");
    check("QuotientRemainder(1+16*I,1)", //
        "{1+I*16,0}");
    check("QuotientRemainder(5,3-I)", //
        "{2,-1+I*2}");
    check("LCM(5,3-I)", //
        "5+I*5");
    check("LCM(5,2+I)", //
        "5");
    check("LCM(-4 + 5*I, 2 + 3*I)", //
        "23+I*2");
    check("LCM(-5-I, -2-I)", //
        "9+I*7");
    check("LCM(-3,7)", //
        "21");
    check("LCM(0,b)", //
        "0");
    check("LCM(a,a)", //
        "LCM(a,a)");
    check("LCM(a,b)", //
        "LCM(a,b)");
    check("LCM(1/2,2)", //
        "2");
    check("LCM(2/3,4/9)", //
        "4/3");
    check("LCM(1/3,2/5,3/7)", //
        "6");
    check("LCM(2^32 / 3, 2^34 / 7)", //
        "17179869184");
    check("GCD(4+I, 1+16*I)", //
        "1");
    check("LCM(2^32 +I, 1+ 2^34 * I)", //
        "73786976294838206465+I*12884901888");
    check("LCM(5+I, 2+3*I)", //
        "5+I");
    check("LCM(6+ 9*I, 2 + 3*I)", //
        "6+I*9");
    check("LCM(3/7,12/22)", //
        "6");
    check("LCM(-3/7,12/22)", //
        "6");
    check("LCM(3/7,-12/22)", //
        "6");
    check("LCM(-3/7,-12/22)", //
        "6");
    check("LCM(-1/2)", //
        "1/2");

    // System.out.println(Integer.MIN_VALUE); =>-2147483648
    check("LCM(-2147483648)", //
        "2147483648");
    check("LCM(I)", //
        "1");
    check("LCM(-I)", //
        "1");
    check("LCM(-2)", //
        "2");
    check("LCM(10)", //
        "10");
    check("LCM(-2147483648, -2147483648/2)", //
        "2147483648");
    check("LCM(2, 3, 5)", //
        "30");
    check("LCM(-3, 7)", //
        "21");
    check("LCM(4)", //
        "4");
    check("LCM(2, {3, 5, 7})", //
        "{6,10,14}");
    check("LCM(0,0)", //
        "0");
    check("LCM(0,10)", //
        "0");
    check("LCM(10,0)", //
        "0");
    check("LCM(a)", //
        "LCM(a)");
    check("LCM(a,-a)", //
        "LCM(-a,a)");
    check("LCM(15, 20)", //
        "60");
    check("LCM(20, 30, 40, 50)", //
        "600");
    check("LCM(-36,45)", //
        "180");
    check("LCM(36,-45)", //
        "180");
    check("LCM(-36,-45)", //
        "180");
    check("LCM(2,3,4,5)", //
        "60");
    check("Sum(LCM(3, k), {k, 100})", //
        "11784");
    // check("LCM(1/3, 2/5, 3/7)", "");
  }

  @Test
  public void testLeafCount() {
    check("LeafCount(1 + x + y^a)", //
        "6");
    check("LeafCount(f(x, y))", //
        "3");
    check("LeafCount({1 / 3, 1 + I})", //
        "7");
    check("LeafCount(Sqrt(2))", //
        "5");
    check("LeafCount(100!)", //
        "1");
    check("LeafCount(f(1, 2)[x, y])", //
        "5");
    check("LeafCount(10+I)", //
        "3");
  }



  @Test
  public void testLength() {
    check("Length(\"test\")", //
        "0");
    check("Length(<||>)", //
        "0");
    check("Length(<|a->c,b->d|>)", //
        "2");
    check("Length({{1,2},{1,2},{1,2},{1,2}}[[2]])", //
        "2");
    check("Length(aa)", //
        "0");
    check("Length(Sqrt(aa))", //
        "2");
    check("Length({1, 2, 3})", //
        "3");
    check("Length(Exp(x))", //
        "2");
    check("FullForm(Exp(x))", //
        "Power(E, x)");
    check("Length(a)", //
        "0");
    check("Length(1/3)", //
        "0");
    check("FullForm(1/3)", //
        "Rational(1,3)");

    check("Length(a + b + c + d)", //
        "4");
    check("Length(3 + I)", //
        "0");
    check("Map(Length, {{a, b}, {a, b, c}, {x}})", //
        "{2,3,1}");
  }

  @Test
  public void testLengthWhile() {
    check("LengthWhile({1, 1, 2, 3, 5, 8, 13, 21}, # < 10 &)", //
        "6");
    check("LengthWhile({10, 1, 2, 3, 5, 8, 13, 21}, # < 10 &)", //
        "0");
    check("LengthWhile({1, 2, 3, 10, 5, 8, 42, 11}, # < 10 &)", //
        "3");
    check("LengthWhile({a, Pi, 3, 2, {1, 2, 3}, {a, b}, 10}, Head(#1) =!= List &)", //
        "4");
    check("LengthWhile({E, 8, a, b, 20, 1.4}, If(NumericQ(#1), True, #1) &)", //
        "2");
    check("LengthWhile(h(1, 1, 2, 3, a, 8, 13, b, c, d), IntegerQ)", //
        "4");
  }

  @Test
  public void testLerchPhi() {
    check("LerchPhi(-1,1,0)", //
        "-Log(2)");
    check("LerchPhi(-1,2,1/2)", //
        "4*Catalan");
    check("LerchPhi(1,2,1)", //
        "Pi^2/6");
    check("LerchPhi(2,1,0)", //
        "-I*Pi");
    check("LerchPhi(1/2-1/2*I, 2, 1)", //
        "(1+I)*PolyLog(2,1/2-I*1/2)");
    check("LerchPhi(1/2-1/2*I, 2, 1) // FunctionExpand", //
        "(1+I)*(-I*Catalan+Pi^2/48-(I*1/4*Pi-Log(2)/2)^2/2)");

    check("LerchPhi(0,1,0)", //
        "0");
    check("LerchPhi(1,1,a)", //
        "LerchPhi(1,1,a)");
    check("LerchPhi(1,1,-42/3+7*I)", //
        "Infinity");
    check("LerchPhi(-1,s,1)", //
        "(1-2^(1-s))*Zeta(s)");
    check("LerchPhi(z,1,1)", //
        "-Log(1-z)/z");
    check("LerchPhi(z,s,0)", //
        "PolyLog(s,z)");
    check("LerchPhi(z,s,1)", //
        "PolyLog(s,z)/z");
    check("LerchPhi(0,1,a)", //
        "1/Sqrt(a^2)");
    check("LerchPhi(0,s,a)", //
        "(a^2)^(-s/2)");
    check("LerchPhi(-1,s,a)", //
        "Zeta(s,a/2)/2^s-Zeta(s,1/2*(1+a))/2^s");
    check("LerchPhi(z,0,a)", //
        "1/(1-z)");
    // check("LerchPhi(1,1,2)", //
    // "Infinity");
    check("LerchPhi(z,s,-2)", //
        "z^2*(1/(2^s*z^2)+1/z+PolyLog(s,z))");
    check("LerchPhi(-1,1,0)", //
        "-Log(2)");
  }

  @Test
  public void testEqualTo() {
    check("Select({0, 0., 0.1, 0.001}, EqualTo(0))", //
        "{0,0.0}");
  }

  @Test
  public void testUnequalTo() {
    check("Select({0, 0., 0.1, 0.001}, UnequalTo(0))", //
        "{0.1,0.001}");
  }

  @Test
  public void testGreaterThan() {
    check("GreaterThan(42)[2]", //
        "False");
    check("GreaterThan(y)[x]", //
        "x>y");
  }

  @Test
  public void testGreaterEqualThan() {
    check("GreaterEqualThan(42)[2]", //
        "False");
    check("GreaterEqualThan(y)[x]", //
        "x>=y");

    check(" Select({0, 5, 10, 15}, GreaterEqualThan(10))", //
        "{10,15}");
    check(" Select({0, 5, 10, 15}, GreaterEqual(#,10)&)", //
        "{10,15}");
  }

  @Test
  public void testLessThan() {
    check("LessThan(42)[2]", //
        "True");
    check("LessThan(y)[x]", //
        "x<y");
  }

  @Test
  public void testLessEqualThan() {
    check("LessEqualThan(42)[2]", //
        "True");
    check("LessEqualThan(y)[x]", //
        "x<=y");
  }

  @Test
  public void testLetterCounts() {
    check("LetterCounts(\"The quick brown fox jumps over the lazy dog\") // InputForm", //
        "<|\"T\"->1,\" \"->8,\"a\"->1,\"b\"->1,\"c\"->1,\"d\"->1,\"e\"->3,\"f\"->1,\"g\"->1,\"h\"->2,\"i\"->1,\"j\"->1,\"k\"->1,\"l\"->1,\"m\"->1,\"n\"->1,\"o\"->4,\"p\"->1,\"q\"->1,\"r\"->2,\"s\"->1,\"t\"->1,\"u\"->2,\"v\"->1,\"w\"->1,\"x\"->1,\"y\"->1,\"z\"->1|>");
  }

  @Test
  public void testLetterQ() {
    check("LetterQ(\"a\")", //
        "True");
    check("LetterQ(\"2\")", //
        "False");
    check("LetterQ(\"äü\")", //
        "True");
  }

  @Test
  public void testLevel() {
    check("Level(x, -1)", //
        "{}");
    check("Level(x, 0)", //
        "{}");
    check("Level(x, 1)", //
        "{}");
    check("demo=x+Cos(Pi/3*Sqrt(2+1/x))+Sin(Pi/6*Sqrt(x))", //
        "x+Cos(1/3*Pi*Sqrt(2+1/x))+Sin(1/6*Pi*Sqrt(x))");
    check("Level(demo, 1)", //
        "{x,Cos(1/3*Pi*Sqrt(2+1/x)),Sin(1/6*Pi*Sqrt(x))}");
    check("Level(demo, 2)", //
        "{x,1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x))}");
    check("Level(demo, {2})", //
        "{1/3*Pi*Sqrt(2+1/x),1/6*Pi*Sqrt(x)}");
    check("Level(demo, {25})", //
        "{}");
    check("Level(demo, {2,4})", //
        "{1/3,Pi,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x)}");
    check("Level(demo, {0,4})", //
        "{x,1/3,Pi,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),1/6,Pi,x,\n"
            + "1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x)),x+Cos(1/3*Pi*Sqrt(2+1/x))+Sin(1/6*Pi*Sqrt(x))}");
    check("Level(demo, {-1})", //
        "{x,1/3,Pi,2,x,-1,1/2,1/6,Pi,x,1/2}");
    check("Level(demo, {-4})", //
        "{Sqrt(2+1/x),Sin(1/6*Pi*Sqrt(x))}");
    check("Level(demo, {-25})", //
        "{}");
    check("Level(demo, -4)", //
        "{Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),Sin(1/6*Pi*Sqrt(x))}");
    check("Level(demo, -1)", //
        "{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),\n"
            + "1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x))}");
    check("Level(demo, Infinity)", //
        "{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),\n"
            + "1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x))}");
    check("Level(demo, {1, -3})", //
        "{2+1/x,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),1/6*Pi*Sqrt(x),Sin(\n"
            + "1/6*Pi*Sqrt(x))}");
    check("Level(demo, {-4, 2})", //
        "{x,1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x))}");
    check("Level(demo, {0, -1})", //
        "{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),\n"
            + "1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x)),x+Cos(1/3*Pi*Sqrt(2+1/x))+Sin(\n"
            + "1/6*Pi*Sqrt(x))}");
    check("Level(demo, {-Infinity, Infinity})", //
        "{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),\n"
            + "1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x)),x+Cos(1/3*Pi*Sqrt(2+1/x))+Sin(\n"
            + "1/6*Pi*Sqrt(x))}");
    check("Level(demo, {3, -6})", //
        "{}");
    check("Level(demo, {-3, -1})", //
        "{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x)}");

    check("Level(a + f(x, y^n), {-1})", //
        "{a,x,y,n}");
    check("Level(a + b ^ 3 * f(2*x ^ 2), {-1}, g)", //
        "g(a,b,3,2,x,2)");
    check("Level(a + b ^ 3 * f(2*x ^ 2), {-1})", //
        "{a,b,3,2,x,2}");
    check("Level({{{{a}}}}, 3)", //
        "{{a},{{a}},{{{a}}}}");
    check("Level({{{{a}}}}, -4)", //
        "{{{{a}}}}");
    check("Level({{{{a}}}}, -5)", //
        "{}");
    check("Level(h0(h1(h2(h3(a)))), {0, -1})", //
        "{a,h3(a),h2(h3(a)),h1(h2(h3(a))),h0(h1(h2(h3(a))))}");
    check("Level({{{{a}}}}, 3, Heads -> True)", //
        "{List,List,List,{a},{{a}},{{{a}}}}");
    check("Level(x^2 + y^3, 3, Heads -> True)", //
        "{Plus,Power,x,2,x^2,Power,y,3,y^3}");
    check("Level(a ^ 2 + 2 * b, {-1}, Heads -> True)", //
        "{Plus,Power,a,2,Times,2,b}");
    check("Level(f(g(h))[x], {-1}, Heads -> True)", //
        "{f,g,h,x}");
    // TODO
    // check("Level(f(g(h))[x], {-2, -1}, Heads -> True)",
    // "{f,g,h,g(h),x,f(g(h))[x]}");

    check("Level(a + f(x, y^n), {-1})", //
        "{a,x,y,n}");
    check("Level(a + f(x, y^n0), 2)", //
        "{a,x,y^n0,f(x,y^n0)}");
    check("Level(a + f(x, y^n0), {0, Infinity})", //
        "{a,x,y,n0,y^n0,f(x,y^n0),a+f(x,y^n0)}");
    check("Level({{{{a}}}}, 1)", //
        "{{{{a}}}}");
    check("Level({{{{a}}}}, 2)", //
        "{{{a}},{{{a}}}}");
    check("Level({{{{a}}}}, 3)", //
        "{{a},{{a}},{{{a}}}}");
    check("Level({{{{a}}}}, 4)", //
        "{a,{a},{{a}},{{{a}}}}");
    check("Level({{{{a}}}}, 5)", //
        "{a,{a},{{a}},{{{a}}}}");
    check("Level({{{{a}}}}, -1)", //
        "{a,{a},{{a}},{{{a}}}}");
    check("Level({{{{a}}}}, -2)", //
        "{{a},{{a}},{{{a}}}}");
    check("Level({{{{a}}}}, -3)", //
        "{{{a}},{{{a}}}}");
    check("Level({{{{a}}}}, -4)", //
        "{{{{a}}}}");
    check("Level({{{{a}}}}, -5)", //
        "{}");
    check("Level({{{{a}}}}, {2, 3})", //
        "{{a},{{a}}}");
    check("Level({{{{a}}}}, {0, -1})", //
        "{a,{a},{{a}},{{{a}}},{{{{a}}}}}");
    check("Level(h0(h1(h2(h3(a)))), {0, -1})", //
        "{a,h3(a),h2(h3(a)),h1(h2(h3(a))),h0(h1(h2(h3(a))))}");
    check("Level({{{{a}}}}, 3, Heads -> True)", //
        "{List,List,List,{a},{{a}},{{{a}}}}");
    check("Level(x^2 + y^3, 3, Heads -> True)", //
        "{Plus,Power,x,2,x^2,Power,y,3,y^3}");
    check("Level(h1(h2(h3(x))), -1)", //
        "{x,h3(x),h2(h3(x))}");
    check("Level(h1(h2(h3(x))), {0, -1})", //
        "{x,h3(x),h2(h3(x)),h1(h2(h3(x)))}");

    check("Level(f(f(g(a), a), a, h(a), f), 2)", //
        "{g(a),a,f(g(a),a),a,a,h(a),f}");
    check("Level(f(f(g(a), a), a, h(a), f), {2})", //
        "{g(a),a,a}");
    check("Level(f(f(g(a), a), a, h(a), f), {-1})", //
        "{a,a,a,a,f}");
    check("Level(f(f(g(a), a), a, h(a), f), {-2})", //
        "{g(a),h(a)}");



    check("x = F(G(a, K(d)), H(b, L(e)), J(c, M(P(f, g))))", //
        "F(G(a,K(d)),H(b,L(e)),J(c,M(P(f,g))))");
    check("Table(Level(x, {k}), {k, 0, 5})", //
        "{{F(G(a,K(d)),H(b,L(e)),J(c,M(P(f,g))))},"//
            + "{G(a,K(d)),H(b,L(e)),J(c,M(P(f,g)))},"//
            + "{a,K(d),b,L(e),c,M(P(f,g))},"//
            + "{d,e,P(f,g)},"//
            + "{f,g},"//
            + "{}}");
    check("Table(Level(x, {-k}), {k, 1, 5})", //
        "{{a,d,b,e,c,f,g},"//
            + "{K(d),L(e),P(f,g)},"//
            + "{G(a,K(d)),H(b,L(e)),M(P(f,g))},"//
            + "{J(c,M(P(f,g)))}," //
            + "{F(G(a,K(d)),H(b,L(e)),J(c,M(P(f,g))))}}");
  }

  @Test
  public void testLevelQ() {
    check("LevelQ(2)", //
        "True");
    check("LevelQ({2, 4})", //
        "True");
    check("LevelQ(Infinity)", //
        "True");
    check("LevelQ(a + b)", //
        "False");
  }

  // @Test
  // public void testJacobianMatrix() {
  // check("JacobianMatrix({Rr, Ttheta, Zz}, Cylindrical)", "");
  // }

  @Test
  public void testLimit() {
    // TODO
    // check("Limit(((1/Abs(n)))^(1/n),n->Infinity)", //
    // "1");
    check("Limit(a^(1/n),n->Infinity)", //
        "1");
    check("Limit((1+1/n)^n,n->Infinity)", //
        "E");
    check("Limit(Sin(x)/x,x->0)", //
        "1");
    check("Limit(Sin(x)/x,x->Infinity)", //
        "0");

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


    // issue #931
    check("Limit((-1/E^x+E^x)/(E^x+E^(-x)),x -> Infinity)", //
        "1");
    check("Limit((-1+Sqrt(x))/Sqrt(-1+x), x -> Infinity)", //
        "1");

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
    check("Limit(f(a)^x,x->-Infinity)", //
        "Limit(f(a)^x,x->-Infinity)");

    check("Limit(Tan(9/7)^x,x->Infinity)", //
        "Infinity");
    check("Limit(Sin(1/7)^x,x->Infinity)", //
        "0");
    check("Limit(a^x,x->Infinity)", //
        "ConditionalExpression(Infinity,Log(a)>0)");
    check("Limit((a+b+2)^x,x->Infinity)", //
        "ConditionalExpression(Infinity,Log(2+a+b)>0)");
    check("Limit((a+f[b]+2)^x,x->Infinity)", //
        "Limit((2+a+f(b))^x,x->Infinity)");
    check("Limit(f(a)^x,x->Infinity)", //
        "Limit(f(a)^x,x->Infinity)");
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
    // TODO github #175
    // check("Limit(((a^(1/x)+b^(1/x))/2)^x, x->Infinity)", //
    // "Sqr(a)*Sqrt(b)");
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
    // gitlab #107
    check("Limit(x^2-1/x-2, x->0)", //
        "Indeterminate");

    check("Limit((x^2) /(3*x), x->Infinity)", //
        "Infinity");
    check("Limit(x^(-2/3),x->0 , Direction->-1)", //
        "Infinity");
    check("Limit(x^(-2/3),x->0 , Direction->1)", //
        "Limit(1/x^(2/3),x->0,Direction->1)");
    check("Limit(x^(-2/3),x->0)", //
        "Indeterminate");

    check("Limit(x^(-16/7),x->0 , Direction->-1)", //
        "Infinity");
    check("Limit(x^(-16/7),x->0 , Direction->1)", //
        "Limit(1/x^(16/7),x->0,Direction->1)");
    check("Limit(x^(-16/7),x->0)", //
        "Indeterminate");

    check("Limit(x^(-37/4),x->0 , Direction->-1)", //
        "Infinity");
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
  public void testLinearModelFit() {

    check("LinearModelFit({-0.8, -0.2, 0.8, 2.2}, x, x)", //
        "-2.0+x");
    // print message: LinearModelFit: The first argument is not a vector or matrix.
    check("LinearModelFit({{0, 1, 2}, {1, 0}, {3, 2}, {5, 4}},x,x)", //
        "LinearModelFit({{0,1,2},{1,0},{3,2},{5,4}},x,x)");
    // print message: LinearModelFit: Design matrix different from variable currently not supported
    // in LinearModelFit.
    check("LinearModelFit(0^ {{1,0}, {0,1}},{{1,2,3,a}},RegularExpression(1))", //
        "LinearModelFit({{0,Indeterminate},{Indeterminate,0}},{{1,2,3,a}},RegularExpression(\n"
            + "1))");

    check("LinearModelFit({ { 1, 3 }, { 2, 5 }, { 3, 7 }, { 4, 14 }, { 5, 11 } },x,x)", //
        "0.5+2.5*x");
    check("LinearModelFit({{0, 1}, {1, 0}, {3, 2}, {5, 4}},x,x)", //
        "0.186441+0.694915*x");
  }

  @Test
  public void testLinearRecurrence() {
    check("LinearRecurrence({1, 1}, {1, 1}, {5, 5})", //
        "{5}");
    check("LinearRecurrence({0, 1, 1}, {1, 1, 1}, 0)", //
        "LinearRecurrence({0,1,1},{1,1,1},0)");
    check("LinearRecurrence({0, 1, 1}, {1, 1, 1}, 1)", //
        "{1}");
    check("LinearRecurrence({0, 1, 1}, {1, 1, 1}, 2)", //
        "{1,1}");
    check("LinearRecurrence({0, 1, 1}, {1, 1, 1}, 3)", //
        "{1,1,1}");
    check("LinearRecurrence({0, 1, 1}, {1, 1, 1}, 10)", //
        "{1,1,1,2,2,3,4,5,7,9}");

    check("$IterationLimit=20000;LinearRecurrence({3, 2, 1, 4}, {1, -36, 9, 80}, 499) // Last", //
        "4392405770581123238293018208945275262282481636368897269404822983707775767943275\\\n" //
            + "2397074324032453155999783186143730463033184880119799313454266330175795103523924\\\n" //
            + "8744360339410536645316069379253837818025841264555121500544037499343216200306170\\\n" //
            + "3398323176113333063330229192911709396080150894");

    check("$IterationLimit=500", //
        "500");
    // message Maximum AST dimension 20511 exceeded
    check("LinearRecurrence({x,-3,-1/2},{{1,0},{0,1},0},101)", //
        "LinearRecurrence({x,-3,-1/2},{{1,0},{0,1},0},101)");
    // iter limit
    check("LinearRecurrence({-1,-2,3},{x,-3,-1/2},1009)", //
        "Hold(LinearRecurrence({-1,-2,3},{x,-3,-1/2},1009))");
    check("LinearRecurrence({a, b}, {1, 1}, 5)", //
        "{1,1,a+b,a^2+b+a*b,a^3+2*a*b+a^2*b+b^2}");
    check("LinearRecurrence({-3, 1}, {7, 2}, 10)", //
        "{7,2,1,-1,4,-13,43,-142,469,-1549}");
    // Fibonacci sequence
    check("LinearRecurrence({1, 1}, {1, 1}, 10)", //
        "{1,1,2,3,5,8,13,21,34,55}");
    check("LinearRecurrence({1, 1}, {1, 1}, {8})", //
        "21");

    check("LinearRecurrence({a,b}, {c,d}, 5)", //
        "{c,d,b*c+a*d,a*b*c+a^2*d+b*d,a^2*b*c+b^2*c+a^3*d+2*a*b*d}");
    check("LinearRecurrence({1, 1}, {{1, 2}, {2, 1}}, 10)", //
        "{{1,2},{2,1},{3,3},{5,4},{8,7},{13,11},{21,18},{34,29},{55,47},{89,76}}");

    // A001608 Perrin sequence
    // https://oeis.org/A001608
    check("LinearRecurrence({0, 1, 1}, {3, 0, 2}, 50)", //
        "{3,0,2,3,2,5,5,7,10,12,17,22,29,39,51,68,90,119,158,209,277,367,486,644,853,1130,\n"
            + "1497,1983,2627,3480,4610,6107,8090,10717,14197,18807,24914,33004,43721,57918,\n"
            + "76725,101639,134643,178364,236282,313007,414646,549289,727653,963935}");

    // A016064 Shortest legs of Heronian triangles (sides are consecutive integers, area is an
    // integer).
    // https://oeis.org/A016064
    check("LinearRecurrence({5, -5, 1}, {1, 3, 13}, 26)", //
        "{1,3,13,51,193,723,2701,10083,37633,140451,524173,1956243,7300801,27246963,\n"
            + "101687053,379501251,1416317953,5285770563,19726764301,73621286643,274758382273,\n"
            + "1025412242451,3826890587533,14282150107683,53301709843201,198924689265123}");

    // A251599 Centers of rows of the triangular array formed by the natural numbers.
    // https://oeis.org/A251599
    check("LinearRecurrence({1, 0, 2, -2, 0, -1, 1}, {1, 2, 3, 5, 8, 9, 13}, 60)", //
        "{1,2,3,5,8,9,13,18,19,25,32,33,41,50,51,61,72,73,85,98,99,113,128,129,145,162,\n"
            + "163,181,200,201,221,242,243,265,288,289,313,338,339,365,392,393,421,450,451,481,\n"
            + "512,513,545,578,579,613,648,649,685,722,723,761,800,801}");

    // A050250 Number of nonzero palindromes less than 10^n.
    // https://oeis.org/A050250
    check("LinearRecurrence({1, 10, -10}, {9, 18, 108}, 30)", //
        "{9,18,108,198,1098,1998,10998,19998,109998,199998,1099998,1999998,10999998,\n"
            + "19999998,109999998,199999998,1099999998,1999999998,10999999998,19999999998,\n"
            + "109999999998,199999999998,1099999999998,1999999999998,10999999999998,\n"
            + "19999999999998,109999999999998,199999999999998,1099999999999998,1999999999999998}");
  }

  @Test
  public void testLiouvilleLambda() {
    check("LiouvilleLambda(3^5)", //
        "-1");
    check("LiouvilleLambda(2*3^5)", //
        "1");
    check("LiouvilleLambda(50!)", //
        "1");
    check("LiouvilleLambda({1,2,3,4,5,6,20})", //
        "{1,-1,-1,1,-1,1,-1}");
  }

  @Test
  public void testList() {
    check("{a,b,c}(x) // FullForm", //
        "List(a, b, c)[x]");
    // prints error
    check("{1,2}+{4,5,6}", //
        "{1,2}+{4,5,6}");
  }

  @Test
  public void testListable() {
    check("SetAttributes(f, Listable)", //
        "");
    check("f({1, 2, 3}, {4, 5, 6})", //
        "{f(1,4),f(2,5),f(3,6)}");
    check("f({1, 2, 3}, 4)", //
        "{f(1,4),f(2,4),f(3,4)}");
    check("{{1, 2}, {3, 4}} + {5, 6}", //
        "{{6,7},{9,10}}");
  }

  @Test
  public void testListConvolve() {
    check("ListConvolve({x, y}, {a, b, c, d, e, f})", //
        "{b*x+a*y,c*x+b*y,d*x+c*y,e*x+d*y,f*x+e*y}");

    check("Reverse({{u, v}, {w, x}})", //
        "{{w,x},{u,v}}");

    check("ListConvolve({{u, v}, {w, x}}, {{a,b,c,p}, {d,e,f,q}, {g, h, i,r}})", //
        "{{e*u+d*v+b*w+a*x,f*u+e*v+c*w+b*x,q*u+f*v+p*w+c*x},{h*u+g*v+e*w+d*x,i*u+h*v+f*w+e*x,r*u+i*v+q*w+f*x}}");
  }

  @Test
  public void testListCorrelate() {
    check("ListCorrelate({x, y}, {a, b, c, d, e, f})", //
        "{a*x+b*y,b*x+c*y,c*x+d*y,d*x+e*y,e*x+f*y}");

    check("ListCorrelate({{u, v}, {w, x}}, {{a,b,c,p}, {d,e,f,q}, {g, h, i,r}})", //
        "{{a*u+b*v+d*w+e*x,b*u+c*v+e*w+f*x,c*u+p*v+f*w+q*x}," //
            + "{d*u+e*v+g*w+h*x,e*u+f*v+h*w+i*x,f*u+q*v+i*w+r*x}}");

    check("ListCorrelate({{1, 0, 1}, {0, -4, 0}, {1, 0, 1}}, Array(Subscript(a, #) &, {4, 4}))", //
        "{{2*Subscript(a,1)-4*Subscript(a,2)+2*Subscript(a,3),2*Subscript(a,1)-4*Subscript(a,\n"//
            + "2)+2*Subscript(a,3)},{2*Subscript(a,2)-4*Subscript(a,3)+2*Subscript(a,4),2*Subscript(a,\n"//
            + "2)-4*Subscript(a,3)+2*Subscript(a,4)}}");

    check("ListCorrelate({{1, 1}, {1, 1}}, {{a, b, c}, {d, e, f}, {g, h, i}})", //
        "{{a+b+d+e,b+c+e+f},{d+e+g+h,e+f+h+i}}");

    check("ListCorrelate(N(Differences(Range(10)^2)), N(1/Range(20)))", //
        "{20.82897,16.07103,13.44037,11.65061,10.3224,9.28594,8.44948,7.75767,7.17457,6.67562,6.24334,5.86489}");
  }

  @Test
  public void testListQ() {
    check("ListQ({1, 2, 3})", //
        "True");
    check("ListQ({{1, 2}, {3, 4}})", //
        "True");
    check("ListQ(x)", //
        "False");
  }

  // @Test
  // public void testListLinePlot() {
  // checkNumeric("ListLinePlot({{}, {{1., 1.}}, {{1., 2.}}, {}})", //
  // "ListLinePlot({{},{{1.0,1.0}},{{1.0,2.0}},{}})");
  // }

  @Test
  public void testLog() {
    checkNumeric("Log(-1.5)", //
        "0.4054651081081644+I*3.141592653589793");
    checkNumeric("N(Log(-3/2),30)", //
        "0.40546510810816438197801311546+I*3.14159265358979323846264338327");
    // same as in MMA
    checkNumeric("Log(0.0)", //
        "Indeterminate");
    checkNumeric("N(Log(0),30)", //
        "-Infinity");


    checkNumeric("Sum(i^(-1)*(-1)^(i - 1)*(x-1)^i, {i, 1, Infinity})", //
        "Log(x)");
    checkNumeric("Log(Exp(1.4))", //
        "1.4");
    checkNumeric("Log(-1.4)", //
        "0.3364722366212129+I*3.141592653589793");

    check("N(Log({2, E, 10}, 5/2),50)", //
        "{1.3219280948873623478703194294893901758648313930245," //
            + "0.91629073187415506518352721176801107145010121990826," //
            + "0.39794000867203760957252221055101394646362023707578}");

    check("Log((-1)^(1/8))", //
        "I*1/8*Pi");
    // check("Log(-(-1)^(1/8))", //
    // "-I*1/3*Pi");
    check("Log(1/2-I*1/2*Sqrt(3))", //
        "-I*1/3*Pi");

    check("Log(Overflow())", //
        "Overflow()");
    check("Log(Underflow())", //
        "Underflow()");
    check("Log(1000, 10)", //
        "1/3");
    check("Log(9) / Log(27)", //
        "Log(9)/Log(27)");
    check("Log(27) / Log(9)", //
        "Log(27)/Log(9)");

    check("Log(Interval({1/3, E}))", //
        "Interval({-Log(3),1})");
    check("Log(Interval({0, 3}))", //
        "Interval({-Infinity,Log(3)})");
    check("Log(Interval({-1, 3}))", //
        "Log(Interval({-1,3}))");
    // github #134
    check("Log(10,1)", //
        "0");
    check("Log(0,0)", //
        "Indeterminate");

    check("Log(E^(7+13*I))", //
        "7+I*13-I*4*Pi");
    check("Log(E^(27*I))", //
        "I*27-I*8*Pi");
    check("Log(10*E) // Simplify", //
        "1+Log(10)");
    check("Log(10*E*x*y) // FunctionExpand", //
        "1+Log(10)+Log(x*y)");

    check("Log( )", //
        "Log()");

    check("Log(2/3)", //
        "-Log(3/2)");
    check("Log(3/2)", //
        "Log(3/2)");
    check("Log(-3/2)", //
        "I*Pi+Log(3/2)");
    check("Log(-2/3)", //
        "I*Pi-Log(3/2)");
    check("Log(0, 0)", ///
        "Indeterminate");
    check("Log(0, x)", ///
        "0");
    check("Log(2, 0)", //
        "-Infinity");
    check("Log(3/4, 0)", //
        "Infinity");
    check("Log(-2, 0)", //
        "(-Infinity)/(I*Pi+Log(2))");

    check("Exp(Log(x))", //
        "x");
    check("Refine(Log(Exp(x)),Element(x, Reals))", //
        "x");
    check("Log({0, 1, E, E * E, E ^ 3, E ^ x})", //
        "{-Infinity,0,1,2,3,Log(E^x)}");
    check("Log(0.)", //
        "Indeterminate");
    check("Log(1000) / Log(10)", //
        "3");
    check("Log(1.4)", //
        "0.336472");
    checkNumeric("Log(Exp(1.4))", //
        "1.4");

    check("Log(-1)", //
        "I*Pi");
    check("Log(-2)", //
        "I*Pi+Log(2)");
    // test alias
    check("Ln(E)", //
        "1");
    check("ln(-E)", //
        "1+I*Pi");

    check("Log(a, b)", //
        "Log(b)/Log(a)");
    check("Log(Pi^E)", //
        "E*Log(Pi)");
    check("Log(E^10)", //
        "10");
    check("Log(E)", //
        "1");
    check("Log(-E)", //
        "1+I*Pi");
    check("D(Log(a, x),x)", //
        "1/(x*Log(a))");
    checkNumeric("Log(1000.)", //
        "6.907755278982137");
    checkNumeric("Log(2.5 + I)", //
        "0.9905007344332917+I*0.3805063771123649");
    checkNumeric("Log({2.1, 3.1, 4.1})", //
        "{0.7419373447293773,1.1314021114911006,1.410986973710262}");
    check("Log(2, 16)", //
        "4");
    check("Log(10, 1000)", //
        "3");
    check("Log(10, 10)", //
        "1");
    check("Log(0)", //
        "-Infinity");
    check("Log(1)", //
        "0");
    check("Log(-x)", //
        "Log(-x)");
    check("Log(-1)", //
        "I*Pi");
    check("Log(I)", //
        "I*1/2*Pi");
    check("Log(-I)", //
        "-I*1/2*Pi");
    check("Log(GoldenRatio)", //
        "ArcCsch(2)");
    check("Log(Infinity)", //
        "Infinity");
    check("Log(-Infinity)", //
        "Infinity");

    check("Log(I*Infinity)", //
        "Infinity");
    check("Log(-I*Infinity)", //
        "Infinity");
    check("Log(ComplexInfinity)", //
        "Infinity");

    check("Table(Im(Log(x + I*y)), {x, -2, 2}, {y, -2, 2}) //N ", //
        "{{-2.35619,-2.67795,3.14159,2.67795,2.35619}," //
            + "{-2.03444,-2.35619,3.14159,2.35619,2.03444}," //
            + "{-1.5708,-1.5708,0.0,1.5708,1.5708}," //
            + "{-1.10715,-0.785398,0.0,0.785398,1.10715}," //
            + "{-0.785398,-0.463648,0.0,0.463648,0.785398}}");
  }

  @Test
  public void testLog10() {
    checkNumeric("10*Log10(1000)", //
        "30");
    checkNumeric("Log10(Interval({1/3, 2}))", //
        "Interval({-Log(3)/Log(10),Log(2)/Log(10)})");
    check("N(Log10(45), 100)", //
        "1.65321251377534367937631691178573759163206784691928318834930381948335011629289773659900945917704643");
    checkNumeric("N(Log10(4+I))", //
        "0.6152244606891369+I*0.10639288158003271");

    check("Log10(Log10(Log(Log(Log(Log10(Log10(a)))))))", //
        "Log(Log(Log(Log(Log(Log(Log(a)/Log(10))/Log(10)))))/Log(10))/Log(10)");
    check("Log10(Log10(Log(Log(Log(Log10(Log10( )))))))", //
        "Log(Log(Log(Log(Log(Log(Log10())/Log(10)))))/Log(10))/Log(10)");
    check("Log10(1000)", //
        "3");
    checkNumeric("Log10({2., 5.})", //
        "{0.30102999566398114,0.6989700043360187}");
    check("Log10(E ^ 3)", //
        "3/Log(10)");

    check("Log10(x)", //
        "Log(x)/Log(10)");

    check("Log10(ComplexInfinity)", //
        "Infinity");
    check("Table(Log10(n), {n, {0, 1, 10, 100}})", //
        "{-Infinity,0,1,2}");
    check("Table(Log10(1/n), {n, {1, 10, 100}})", //
        "{0,-1,-2}");
  }

  @Test
  public void testLog2() {

    check("Log2(Interval({1/3, 2}))", //
        "Interval({-Log(3)/Log(2),1})");
    check("N(Log2(45), 100)", //
        "5.491853096329674710777797317385023193384460208409542732966261704898131232197342340894700693195546639");
    checkNumeric("N(Log2(2 + I))", //
        "1.1609640474436813+I*0.6689021062254881");

    check("Log(2, 0)", //
        "-Infinity");
    check("Log2(0)", //
        "-Infinity");

    check("Log2(4 ^ 8)", //
        "16");
    checkNumeric("Log2(5.6)", //
        "2.485426827170242");
    checkNumeric("Log2(-5.6)", //
        "2.485426827170242+I*4.532360141827194");
    check("Log2(E ^ 2) ", //
        "2/Log(2)");
    check("Log2(x)", //
        "Log(x)/Log(2)");
    check("Table(Log2(n), {n, {1, 2, 4, 8}})", //
        "{0,1,2,3}");
    check("Table(Log2(1/n), {n, {1, 2, 4, 8}})", //
        "{0,-1,-2,-3}");
  }



  @Test
  public void testLogisticSigmoid() {
    checkNumeric("LogisticSigmoid(0.8)", //
        "0.6899744811276125");
    checkNumeric("N(LogisticSigmoid(1/9),50)", //
        "0.52774923505451317879089698533394369096217714446845");
    checkNumeric("LogisticSigmoid(1.0000000000000000000000)", //
        "0.73105857863000487925115");
    checkNumeric("N(LogisticSigmoid(1 + 9 I))", //
        "1.4298207928844138+I*0.32606870958127954");

    check("LogisticSigmoid(I*Pi)", //
        "LogisticSigmoid(I*Pi)");
    check("D(LogisticSigmoid(x),x)", //
        "(1-LogisticSigmoid(x))*LogisticSigmoid(x)");
    check("LogisticSigmoid(Infinity)", //
        "1");
    check("LogisticSigmoid(-Infinity)", //
        "0");
    check("LogisticSigmoid(0)", //
        "1/2");

    checkNumeric("LogisticSigmoid(0.5)", //
        "0.6224593312018546");
    checkNumeric("LogisticSigmoid(0.5 + 2.3*I)", //
        "1.0647505893884985+I*0.8081774171575825");
    checkNumeric("LogisticSigmoid({-0.2, 0.1, 0.3})", //
        "{0.4501660026875221,0.52497918747894,0.5744425168116589}");
    checkNumeric("LogisticSigmoid(0.5 + 2.3*I)", //
        "1.0647505893884985+I*0.8081774171575825");
  }

  @Test
  public void testLucasL() {
    check("LucasL(11,{x,0,0,<|a->0,b:>1|>})", //
        "{11*x+55*x^3+77*x^5+44*x^7+11*x^9+x^11,0,0,<|a->0,b:>LucasL(11,1)|>}");
    // check("LucasL(19,<|s1->0,s2:>1|>)", //
    // "<|s1->0,s2:>LucasL(19,1)|>");

    // slow
    // message Polynomial degree 101 exceeded
    check("LucasL(151,1/1317624576693539401)", //
        "LucasL(151,1/1317624576693539401)");
    check("LucasL(Quantity(1.2,\"m\"),2.718281828459045)", //
        "LucasL(1.2[m],2.71828)");

    check("LucasL(1+I/2)//N", //
        "0.0653384+I*0.755095");
    check("LucasL(1,0)", //
        "0");
    check("LucasL(0,0)", //
        "2");
    check("LucasL(-1, x)", //
        "-x");
    check("LucasL(-10, x)", //
        "2+25*x^2+50*x^4+35*x^6+10*x^8+x^10");
    check("LucasL(-11, x)", //
        "-11*x-55*x^3-77*x^5-44*x^7-11*x^9-x^11");
    check("LucasL(50, x)", //
        "2+625*x^2+32500*x^4+672750*x^6+7400250*x^8+50075025*x^10+227613750*x^12+\n"
            + "736618125*x^14+1767883500*x^16+3241119750*x^18+4639918800*x^20+5272635000*x^22+\n"
            + "4814145000*x^24+3562467300*x^26+2148789800*x^28+1059575660*x^30+427248250*x^32+\n"
            + "140512125*x^34+37469900*x^36+8021650*x^38+1357510*x^40+177375*x^42+17250*x^44+\n"
            + "1175*x^46+50*x^48+x^50");
    check("Table(LucasL(n, x), {n, 5})", //
        "{x,2+x^2,3*x+x^3,2+4*x^2+x^4,5*x+5*x^3+x^5}");

    check("LucasL(-11)", //
        "-199");
    check("LucasL(-12)", //
        "322");
    check("Table(LucasL(n), {n, 20})", //
        "{1,3,4,7,11,18,29,47,76,123,199,322,521,843,1364,2207,3571,5778,9349,15127}");
    check("LucasL(1000)", //
        "9719417773590817520798198207932647373779787915534568508272808108477251881844481\\\n"
            + "5269080619149045968297679578305403209347401163036907660573971740862463751801641\\\n"
            + "201490284097309096322681531675707666695323797578127");
  }

  @Test
  public void testMachineNumberQ() {
    check("MachineNumberQ(3.14159265358979324)", //
        "False");
    check("MachineNumberQ(2.71828182845904524 + 3.14159265358979324*I)", //
        "False");
    check("MachineNumberQ(1.5 + 3.14159265358979324*I)", //
        "False");
    check("MachineNumberQ(1.5 + 2.3*I)", //
        "True");
    check("MachineNumberQ(1.5 + 5 *I)", //
        "True");
  }

  @Test
  public void testMangoldtLambda() {
    check("MangoldtLambda(3^5)", //
        "Log(3)");
    check("MangoldtLambda({1,2,3,4,5,6,7,8,9})", //
        "{0,Log(2),Log(3),Log(2),Log(5),0,Log(7),Log(2),Log(3)}");

    check("{MangoldtLambda(Prime(10^5)^10), MangoldtLambda(2*Prime(10^5)^10)}", //
        "{Log(1299709),0}");
  }

  @Test
  public void testManhattanDistance() {
    check("ManhattanDistance({-1, -1}, {1.0, 1})", //
        "4.0");
    check("ManhattanDistance({-1, -1}, {1, 1})", //
        "4");
  }

  @Test
  public void testMantissaExponent() {
    // message - MantissaExponent: The value (3.0+I*2.0) is not a real number.
    check("MantissaExponent(3+2*I, 2)", //
        "MantissaExponent(3+I*2,2)");

    check("MantissaExponent(12345.6789)", //
        "{0.123457,5}");
    checkNumeric("MantissaExponent(12345.6789)", //
        "{0.12345678900000001,5}");

    check("MantissaExponent(125.24)", //
        "{0.12524,3}");
    check("MantissaExponent(125., 2)", //
        "{0.976562,7}");
    check("MantissaExponent(10, b)", //
        "MantissaExponent(10,b)");
    check("MantissaExponent(E, Pi)", //
        "{E/Pi,1}");
    check("MantissaExponent(Pi, Pi)", //
        "{1/Pi,2}");
    check("MantissaExponent(5/2 + 3, Pi)", //
        "{11/2*1/Pi^2,2}");
    check("MantissaExponent(17, E)", //
        "{17/E^3,3}");
    check("MantissaExponent(17.0, E)", //
        "{0.84638,3}");
    check("MantissaExponent(Exp(Pi), 2)", //
        "{E^Pi/32,5}");
    check("MantissaExponent(0.0000124)", //
        "{0.124,-4}");
    check("MantissaExponent(0.0000124, 2)", //
        "{0.812646,-16}");
    check("MantissaExponent(0)", //
        "{0,0}");

    check("MantissaExponent(N(Pi, 21))", //
        "{0.314159265358979323846,1}");
    check("MantissaExponent(N(Pi, 20))", //
        "{0.31415926535897932384,1}");
    check("MantissaExponent(3.4*10^30)", //
        "{0.34,31}");
  }

  @Test
  public void testMap() {
    // Map: Options expected (instead of y_) beyond position Map(1/Sqrt(5),{},I,y_) in `3`. An
    // option must be a rule or a list of rules.
    check("Map(1/Sqrt(5),ByteArray[{}],I,y_)", //
        "Map(1/Sqrt(5),{},I,y_)");

    check("s=SparseArray({1 -> 1, 2 -> 2, 100 -> 100})", //
        "SparseArray(Number of elements: 3 Dimensions: {100} Default value: 0)");
    check("t=Map(f,s)", //
        "SparseArray(Number of elements: 3 Dimensions: {100} Default value: f(0))");
    check("ArrayRules(t)", //
        "{{1}->f(1),{2}->f(2),{100}->f(100),{_}->f(0)}");
    check("t[[2]]", //
        "f(2)");

    check("Map(#2,1/Sqrt(5),2,Heads->True)", //
        "#2[Power][#2[5],#2[-1/2]]");
    check("Map(f, {{{{a}}}}, -2)", //
        "{f({f({f({a})})})}");

    check("Map(List,Join({1,2,3},4-{1,2,3}))", //
        "{{1},{2},{3},{3},{2},{1}}");
    check("Map(f, {{{{{a}}}}}, 2)", //
        "{f({f({{{a}}})})}");
    check("Map(f, {{{{{a}}}}}, {2})", //
        "{{f({{{a}}})}}");
    check("Map(f, {{{{{a}}}}}, {0,2})", //
        "f({f({f({{{a}}})})})");
    check("Map(f, {{{{{a}}}}}, Infinity)", //
        "{f({f({f({f({f(a)})})})})}");
    check("Map(f, {{{{{a}}}}}, {0, Infinity})", //
        "f({f({f({f({f({f(a)})})})})})");
    check("Map(f, {{{{{a}}}}}, 3)", //
        "{f({f({f({{a}})})})}");
    check("Map(f, {{{{{a}}}}}, Infinity)", //
        "{f({f({f({f({f(a)})})})})}");
    check("Map(f, {{{{{a}}}}}, {0, Infinity})", //
        "f({f({f({f({f({f(a)})})})})})");

    check("Map(f, {{{{{a}}}}}, {2, -3})", //
        "{{f({f({{a}})})}}");
    check("Map(f, h0(h1(h2(h3(h4(a))))), {2, -3})", //
        "h0(h1(f(h2(f(h3(h4(a)))))))");
    check("Map(f, {{{{a}}}}, 2, Heads -> True)", //
        "f(List)[f(f(List)[f({{a}})])]");

    check("Map(f, {a, b, c})", //
        "{f(a),f(b),f(c)}");
    check("Map(f, {a, b, c}, Heads -> True)", //
        "f(List)[f(a),f(b),f(c)]");

    check("f /@ {1, 2, 3}", //
        "{f(1),f(2),f(3)}");
    check("#^2& /@ {1, 2, 3, 4}", //
        "{1,4,9,16}");
    check("Map(f, {{a, b}, {c, d, e}}, {2})", //
        "{{f(a),f(b)},{f(c),f(d),f(e)}}");
    check("Map(f, a + b + c, Heads->True)", //
        "f(Plus)[f(a),f(b),f(c)]");
    check("Map(f, expr, a+b, Heads->True)", //
        "Map(f,expr,a+b,Heads->True)");
    check("Map(f, {{{{a}}}}, -1)", //
        "{f({f({f({f(a)})})})}");
    check("Map(f, {{{{a}}}}, -2)", //
        "{f({f({f({a})})})}");
    check("Map(f, {{{{a}}}}, -3)", //
        "{f({f({{a}})})}");
    check("Map(f, {{{{a}}}}, -4)", //
        "{f({{{a}}})}");
    check("Map(f, {{{{a}}}}, -5)", //
        "{{{{a}}}}");
    check("Map(f, {{{{a}}}}, -6)", //
        "{{{{a}}}}");

    check("Map(Print, {a, b, c})", //
        "{Null,Null,Null}");

    check("Map(x, {a, {{b, c}, d, {e, {f, g}}}}, {-1})", //
        "{x(a),{{x(b),x(c)},x(d),{x(e),{x(f),x(g)}}}}");
    check("Map(x, {a, {{b, c}, d, {e, {f, g}}}}, {-2})", //
        "{a,{x({b,c}),d,{e,x({f,g})}}}");
  }

  @Test
  public void testMapApply() {
    check("MapApply(h, {{a, b}, {c, d}})", //
        "{h(a,b),h(c,d)}");
    check("h@@@{{a,b},{c,d}}", //
        "{h(a,b),h(c,d)}");
    check("MapApply(h)[{{a, b}, {c}, {d, e}}]", //
        "{h(a,b),h(c),h(d,e)}");
    check("MapApply(h, p(x)[q(y)], Heads -> True)", //
        "h(x)[h(y)]");
  }

  @Test
  public void testMapAt() {
    // MapAt: Part {5} of {a,b,c,d} does not exist.
    check("MapAt(f, {a, b, c, d}, 5)", //
        "MapAt(f,{a,b,c,d},5)");
    // MapAt: Part {1,5} of {{a,b,c,d}} does not exist.
    check("MapAt(f, {{a, b, c, d}},{1,5})", //
        "MapAt(f,{{a,b,c,d}},{1,5})");

    check("MapAt(f, {a, b, c, d}, -5)", //
        "f(List)[a,b,c,d]");
    // MapAt: Part {-6} of {a,b,c,d} does not exist.
    check("MapAt(f, {a, b, c, d}, -6)", //
        "MapAt(f,{a,b,c,d},-6)");
    // MapAt: Part {1,1} of {a,b,c,d} does not exist.
    check("MapAt(f, {a, b, c, d}, {1, 1})", //
        "MapAt(f,{a,b,c,d},{1,1})");

    check("MapAt(0&, {{0, 1}, {1, 0}}, {2, 1})", //
        "{{0,1},{0,0}}");
    check("MapAt(0&, {{0, 1}, {1, 0}}, {{2}, {1}})", //
        "{0,0}");

    check("MapAt(f,{{a, b}, {c, d}},{{2, 1},{1,2}})", //
        "{{a,f(b)},{f(c),d}}");
    check("MapAt(f, {a, b, c, d},{{3},{3}})", //
        "{a,b,f(f(c)),d}");
    check("MapAt(f, {{a, b, c}, {d, e}}, {All, 2})", //
        "{{a,f(b),c},{d,f(e)}}");
    check("MapAt(f, <|\"a\" -> 1, \"b\" -> 2, \"c\" -> 3, \"d\" -> 4, \"e\" -> 5|>, Key(\"b\"))", //
        "<|a->1,b->f(2),c->3,d->4,e->5|>");
    check("MapAt(f, <|\"a\" -> 1, \"b\" -> 2, \"c\" -> 3, \"d\" -> 4, \"e\" -> 5|>, \"b\")", //
        "<|a->1,b->f(2),c->3,d->4,e->5|>");
    check("MapAt(f, <|\"a\" -> 1, \"b\" -> 2, \"c\" -> 3, \"d\" -> 4, \"e\" -> 5|>, 3)", //
        "<|a->1,b->2,c->f(3),d->4,e->5|>");
    check("MapAt(f, <|\"a\" -> 1, \"b\" -> 2, \"c\" -> 3, \"d\" -> 4, \"e\" -> 5|>, -3)", //
        "<|a->1,b->2,c->f(3),d->4,e->5|>");
    check("MapAt(f, <|\"a\" -> 1, \"b\" -> {2, 3}, \"c\" -> 4, \"d\" -> 5|>, {\"b\", 1})", //
        "<|a->1,b->{f(2),3},c->4,d->5|>");
    check("MapAt(f, {{a, b, c}, {d, e}}, {2, 1})", //
        "{{a,b,c},{f(d),e}}");
    check("MapAt(f, <|\"a\" -> 1, \"b\" -> 2, \"c\" -> 3, \"d\" -> 4|>, {{\"a\"}, {\"b\"}})", //
        "<|a->f(1),b->f(2),c->3,d->4|>");
    check("MapAt(f, {a, b, c, d}, {{1}, {4}})", //
        "{f(a),b,c,f(d)}");
    check("MapAt(f,3)[x]", //
        "MapAt(f,3)[x]");
    check("MapAt(f,3)[{a, b, c, d}]", //
        "{a,b,f(c),d}");
    check("MapAt(f, a+b+c+d, 2)", //
        "a+c+d+f(b)");
    check("MapAt(f, {a, b, c, d}, 0)", //
        "f(List)[a,b,c,d]");

    check("MapAt(f, {a, b, c, d}, -2)", //
        "{a,b,f(c),d}");
    check("MapAt(f, {a, b, c, d}, -5)", //
        "f(List)[a,b,c,d]");
    check("MapAt(f, {a, b, c, d}, -4)", //
        "{f(a),b,c,d}");

    // print message:
    check("MapAt(f, {a, b, c, d}, -6)", //
        "MapAt(f,{a,b,c,d},-6)");
    check("MapAt(f, {a, b, c, d, e}, -6)", //
        "f(List)[a,b,c,d,e]");
    check("MapAt(f, {a, b, c, d}, 2)", //
        "{a,f(b),c,d}");
    check("sparr=SparseArray({1 -> 1, 2 -> 2, 10 -> 10});", //
        "");
    check("MapAt(f, sparr, {{1}, {5}, {10}})", //
        "{f(1),2,0,0,f(0),0,0,0,0,f(10)}");


    check("MapAt(f, h(a, b), {})", //
        "h(a,b)");
    check("MapAt(f, h(a, b), {{}})", //
        "f(h(a,b))");
  }

  @Test
  public void testReplaceAt() {
    // ReplaceAt: Cannot take positions 2 through 6 in {a,a,a,a,a}.
    check("ReplaceAt({a, a, a, a, a}, a -> xx, 2 ;; 6)", //
        "ReplaceAt({a,a,a,a,a},a->xx,2;;6)");
    check("ReplaceAt({a, a, a, a, a}, a -> xx, 2 ;; 5)", //
        "{a,xx,xx,xx,xx}");
    check("ReplaceAt({a, a, a, a, a}, a -> xx, 2 ;; 4)", //
        "{a,xx,xx,xx,a}");
    check("ReplaceAt({a, a, a, a, a}, a -> xx, 2 ;; 4 ;; 3)", //
        "{a,xx,a,a,a}");
    check("ReplaceAt({a, a, a, a, a}, a -> xx, -2 ;; -4)", //
        "{a,a,a,a,a}");
    check("ReplaceAt({a, a, a, a, a}, a -> xx, -4 ;; -1)", //
        "{a,a,a,a,a}");
    // operator form
    check("ReplaceAt(x -> y, 2)[{a, x, y, z}]", //
        "{a,y,y,z}");

    check("ReplaceAt({a, a, a, a}, a -> xx, 2)", //
        "{a,xx,a,a}");
    check("ReplaceAt({a, a, a, a}, a -> xx, {{1}, {4}})", //
        "{xx,a,a,xx}");
    check("ReplaceAt({{a, a}, {a, a}}, a -> xx, {2, 1})", //
        "{{a,a},{xx,a}}");
    check("ReplaceAt({a, a, a, a}, a -> xx, -2)", //
        "{a,a,xx,a}");
    check("ReplaceAt({1, 2, 3, 4}, x_ :> 2*x - 1, {{2}, {4}})", //
        "{1,3,3,7}");
    check("ReplaceAt({a, b, c, d}, {a->xx, _->yy}, {{1}, {2}, {4}})", //
        "{xx,yy,c,yy}");
    check("ReplaceAt({{a, a}, {a, a}}, a -> xx, {All, 2})", //
        "{{a,xx},{a,xx}}");
    check("ReplaceAt(<|\"a\" -> a, \"b\" -> a|>, a -> xx, Key(\"a\"))", //
        "<|a->xx,b->a|>");
    check("ReplaceAt(<|\"a\" -> {a, a}, \"b\" -> {a, a}|>, a -> xx, {Key(\"a\"), All})", //
        "<|a->{xx,xx},b->{a,a}|>");

    check("ReplaceAt({{a, b}, {c, d}, e}, x_:>f(x), 2)", //
        "{{a,b},f({c,d}),e}");
    check("ReplaceAt({{a, b}, {c, d}, e}, x_ :> f(x), {{1, 2}, {2, 2}, {3}})", //
        "{{a,f(b)},{c,f(d)},f(e)}");

    check("ReplaceAt(a + b + c + d, _ -> x, 2)", //
        "a+c+d+x");
    check("ReplaceAt(x^2 + y^2, _->z, {{1, 1}, {2, 1}})", //
        "2*z^2");
    check("ReplaceAt(<|3 -> \"a\", 2 -> \"b\", 1 -> \"c\"|>, _->xx , {{1}, {Key(1)}})", //
        "<|3->xx,2->b,1->xx|>");
    check("ReplaceAt({a, b, c}, _ -> f, 0)", //
        "f(a,b,c)");

    check("sparr = SparseArray({1 -> 1, 2 -> 2, 10 -> 10})", //
        "SparseArray(Number of elements: 3 Dimensions: {10} Default value: 0)");
    check("ReplaceAt(sparr, _ -> xx, {{2}, {3}})", //
        "{1,xx,xx,0,0,0,0,0,0,10}");

  }

  @Test
  public void testMapIndexed() {
    // Associations are not supported
    check("MapIndexed(f, <|\"a\" -> 1, a -> 2, 1 -> 1|>)", //
        "MapIndexed(f,<|a->1,a->2,1->1|>)");

    check("MapIndexed(f, a(b(c, d, e), l(g(h, j), k)), {-Infinity, Infinity})", //
        "f(a(f(b(f(c,{1,1}),f(d,{1,2}),f(e,{1,3})),{1}),f(l(f(g(f(h,{2,1,1}),f(j,{2,1,2})),{\n"
            + "2,1}),f(k,{2,2})),{2})),{})");
    // f[a[f[b[f[c, {1, 1}], f[d, {1, 2}], f[e, {1, 3}]], {1}], f[l[f[g[f[h, {2, 1, 1}], f[j, {2, 1,
    // 2}]], {2, 1}], f[k, {2, 2}]], {2}]], {}]
    //
    // a(f(b(f(c,{1,1}),f(d,{1,2}),f(e,{1,3})),{1}),f(l(f(g(f(h,{2,1,1}),f(j,{2,1,2})),{
    // 2,1}),f(k,{2,2})),{2}))

    check("MapIndexed(f, a(b(c, d, e), l(g(h, j), k)), Infinity)", //
        "a(f(b(f(c,{1,1}),f(d,{1,2}),f(e,{1,3})),{1}),f(l(f(g(f(h,{2,1,1}),f(j,{2,1,2})),{\n"
            + "2,1}),f(k,{2,2})),{2}))");
    check("MapIndexed(f, a(b(c, d, e), l(g(h, j), k)), {-2, Infinity})", //
        "a(f(b(f(c,{1,1}),f(d,{1,2}),f(e,{1,3})),{1}),l(f(g(f(h,{2,1,1}),f(j,{2,1,2})),{2,\n"
            + "1}),f(k,{2,2})))");
    check("MapIndexed(f, a(b(c, d, e), l(g(h, j), k)), {2, -2})", //
        "a(b(c,d,e),l(f(g(h,j),{2,1}),k))");
    check("MapIndexed(f, a(b(c, d, e), l(g(h, j), k)), 2)", //
        "a(f(b(f(c,{1,1}),f(d,{1,2}),f(e,{1,3})),{1}),f(l(f(g(h,j),{2,1}),f(k,{2,2})),{2}))");
    check("MapIndexed(f, a(b(c, d, e), l(g(h, j), k)), -2)", //
        "a(f(b(c,d,e),{1}),f(l(f(g(h,j),{2,1}),k),{2}))");

    check("MapIndexed(f, SparseArray(3 -> a, 5))", //
        "{f(0,{1}),f(0,{2}),f(a,{3}),f(0,{4}),f(0,{5})}");

    check("MapIndexed(f)[x]", //
        "x");
    check("MapIndexed(f, {{{{a, b}}}}, -3)", //
        "{f({f({{a,b}},{1,1})},{1})}");
    check("MapIndexed(f, {{{{a, b},{c, d}}}}, -2)", //
        "{f({f({f({a,b},{1,1,1}),f({c,d},{1,1,2})},{1,1})},{1})}");
    check("MapIndexed(f, {{{{a}}}}, -5)", //
        "{{{{a}}}}");
    check("MapIndexed(f, {{{{a}}}}, -4)", //
        "{f({{{a}}},{1})}");
    check("MapIndexed(f, {{{{a}}}}, -3)", //
        "{f({f({{a}},{1,1})},{1})}");
    check("MapIndexed(f, {{{{a}}}}, -2)", //
        "{f({f({f({a},{1,1,1})},{1,1})},{1})}");
    check("MapIndexed(f, {{{{a}}}}, -1)", //
        "{f({f({f({f(a,{1,1,1,1})},{1,1,1})},{1,1})},{1})}");

    check("MapIndexed(f, {{a}, {c}}, {2})", //
        "{{f(a,{1,1})},{f(c,{2,1})}}");

    check("MapIndexed(f, {a, b})", //
        "{f(a,{1}),f(b,{2})}");
    check("MapIndexed(f, {{a, b}, {c, d, e}})", //
        "{f({a,b},{1}),f({c,d,e},{2})}");
    check("MapIndexed(f, {}, 1)", //
        "{}");
    check("MapIndexed(f, {a, b}, 1)", //
        "{f(a,{1}),f(b,{2})}");
    check("MapIndexed(f, {a, b}, 0)", //
        "{a,b}");
    check("MapIndexed(f, {}, 0)", //
        "{}");
    check("MapIndexed(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 2)", //
        "{f({f({{a,b},{c,d}},{1,1})},{1}),f({f({{u,v},{s,t}},{2,1})},{2})}");
    check("MapIndexed(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 3)", //
        "{f({f({f({a,b},{1,1,1}),f({c,d},{1,1,2})},{1,1})},{1}),f({f({f({u,v},{2,1,1}),f({s,t},{\n"
            + "2,1,2})},{2,1})},{2})}");
    check("MapIndexed(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 4)", //
        "{f({f({f({f(a,{1,1,1,1}),f(b,{1,1,1,2})},{1,1,1}),f({f(c,{1,1,2,1}),f(d,{1,1,2,2})},{\n"
            + "1,1,2})},{1,1})},{1}),f({f({f({f(u,{2,1,1,1}),f(v,{2,1,1,2})},{2,1,1}),f({f(s,{2,\n"
            + "1,2,1}),f(t,{2,1,2,2})},{2,1,2})},{2,1})},{2})}");
    check("MapIndexed(f, {{{a, b}, {c, d}}, {{u, v}, {s, t}}}, 2)", //
        "{f({f({a,b},{1,1}),f({c,d},{1,2})},{1}),f({f({u,v},{2,1}),f({s,t},{2,2})},{2})}");
    check("MapIndexed(f, {{a, b, c}, {x, y, z}})", //
        "{f({a,b,c},{1}),f({x,y,z},{2})}");
    check("MapIndexed(First(#2) + f(#1) &, {a, b, c, d})", //
        "{1+f(a),2+f(b),3+f(c),4+f(d)}");
    check("MapIndexed(f, {{a, b}, {c, d, e}}, {1})", //
        "{f({a,b},{1}),f({c,d,e},{2})}");
    check("MapIndexed(f, {{a, b}, {c, d, e}}, {2})", //
        "{{f(a,{1,1}),f(b,{1,2})},{f(c,{2,1}),f(d,{2,2}),f(e,{2,3})}}");
    check("MapIndexed(f, {{a, b}, {c, d, e}}, {3})", //
        "{{a,b},{c,d,e}}");
  }

  @Test
  public void testMapThread() {
    check("MapThread(f, {{{a, b}, {c, d}}}, 3)", //
        "MapThread(f,{{{a,b},{c,d}}},3)");

    check("MapThread(f)[ {{a, b, c}, {x, y, z}}]", //
        "{f(a,x),f(b,y),f(c,z)}");
    check("MapThread(f, {}, 1)", //
        "{}");
    check("MapThread(f, {a, b}, 1)", //
        "MapThread(f,{a,b},1)");
    check("MapThread(f, {a, b}, 0)", //
        "f(a,b)");
    check("MapThread(f, {}, 0)", //
        "f()");
    check("MapThread(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 2)", //
        "{{f({a,b},{u,v}),f({c,d},{s,t})}}");
    check("MapThread(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 3)", //
        "{{{f(a,u),f(b,v)},{f(c,s),f(d,t)}}}");
    check("MapThread(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 4)", //
        "MapThread(f,{{{{a,b},{c,d}}},{{{u,v},{s,t}}}},4)");
    check("MapThread(f, {{{a, b}, {c, d}}, {{u, v}, {s, t}}}, 2)", //
        "{{f(a,u),f(b,v)},{f(c,s),f(d,t)}}");
    check("MapThread(f, {{a, b, c}, {x, y, z}})", //
        "{f(a,x),f(b,y),f(c,z)}");

    check("MapThread(f, {{a, b, c}, {1, 2, 3}})", //
        "{f(a,1),f(b,2),f(c,3)}");
  }

  @Test
  public void testMatchingDissimilarity() {
    check("MatchingDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})", //
        "4/7");
    check("MatchingDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
        "3/5");
    check("MatchingDissimilarity({True, False, True}, {True, True, False})", //
        "2/3");
    check("MatchingDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
        "0");
    check("MatchingDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
        "1");
  }

  @Test
  public void testMatchQ() {

    check("MatchQ(<|a -> 1|>, <|key_ -> val_|>)", //
        "True");
    check("MatchQ(22/7, _Rational)", //
        "True");
    check("MatchQ(42, _Complex)", //
        "False");
    check("MatchQ(42, _Rational)", //
        "False");
    check("MatchQ(1.1+2.3*I, _Complex)", //
        "True");

    check("MatchQ(_Integer)[123]", //
        "True");

    check("MatchQ({1, 2, 3}[[]], _List)", //
        "True");
    check("MatchQ({f(a, b), f(a, c), f(a, d)}, {f(_, x_) ..})", //
        "False");
    check("MatchQ({f(a, b), f(a, c), f(a, d)}, {f(x_, _) ..})", //
        "True");

    check("MatchQ({\"U\",\"V\"}, {_String ..})", //
        "True");

    check("MatchQ({a,b,c}, {___Symbol})", //
        "True");
    check("MatchQ({a,2,c}, {___Symbol})", //
        "False");
    check("MatchQ({a,b,c}, _)", //
        "True");
    check("MatchQ({a,b,c}, {_})", //
        "False");
    check("MatchQ({a,b,c}, __)", //
        "True");
    check("MatchQ({a,b,c}, x__)", //
        "True");
    check("MatchQ({ }, __)", //
        "True");
    check("MatchQ(Plus(a,b,c), Plus(__))", //
        "True");
    check("MatchQ({4,6,8}, x_/;Length(x)>4)", //
        "False");
    // Print error message
    check("MatchQ({4,6,8}, {x___}/;Length(x)>4)", //
        "False");
    check("MatchQ({4,6,8}, {x___}/;Plus(x)>10)", //
        "True");
    check("MatchQ({1,2,3}, _List)", //
        "True");
    check("MatchQ({1,2,3}, _?NumberQ)", //
        "False");
    check("MatchQ({1,2,3}, {__?NumberQ})", //
        "True");

    check(
        "MatchQ({x*(Sqrt(2*Pi*x)/(x!))^(1/x),x->Infinity}, {x_*(Sqrt(2*Pi*x_)/(x_!))^(1/x_), x_->Infinity})", //
        "True");

    // TODO
    check("MatchQ({a}, {a, ___})", //
        "True");
    check("MatchQ(2*x, c1_Integer*a_Symbol)", //
        "True");
    check("MatchQ(a + b, x_Symbol + x_Symbol)", //
        "False");
    check("MatchQ({2^a, a}, {2^x_Symbol, x_Symbol})", //
        "True");
    check("MatchQ({2^a, b}, {2^x_Symbol, x_Symbol})", //
        "False");

    check("MatchQ({a, b}, {a, __})", //
        "True");
    check("MatchQ({a}, {a, __})", //
        "False");

    check("MatchQ(1,_Integer)", //
        "True");
    check("MatchQ(_Symbol, _Symbol)", //
        "False");
    check("MatchQ(_Symbol, _Blank)", //
        "True");
    check("MatchQ(_Symbol, test_Blank)", //
        "True");
    check("MatchQ(name_Symbol, test_Pattern)", //
        "True");
    check("MatchQ(_Symbol, s)", //
        "False");
    check("MatchQ(1.5,_Integer)", //
        "False");
    check("MatchQ(1.5,_Real)", //
        "True");

    check("MatchQ(f(2*I), f(Complex(i_Integer, r_Integer)) )", //
        "True");
    check("MatchQ(g(1/2), g(Rational(n_Integer, d_Integer)) )", //
        "True");

    check("MatchQ(Simplify(1 + 1/GoldenRatio - GoldenRatio), 0)", //
        "True");

    check("MatchQ(Sin(Cos(x)), HoldPattern(F_(G_(v_))) /; F==Sin&&G==Cos&&v==x )", //
        "True");
    check("MatchQ(Sin(x*y), HoldPattern(F_(G_(v_))) /; Print(F,G,v) )", //
        "False");

    check("MatchQ(Sin(Cos(x)), HoldPattern(F_(G_(v_))))", //
        "True");

    check("MatchQ(Sin(3*y),Sin(u_*v_) /; IntegerQ(u))", //
        "True");
    check("MatchQ(123, _Integer)", //
        "True");
    check("MatchQ(123, _Real)", //
        "False");

    check("MatchQ((-1-1*#^2-3*#)&, (a_.+c_.*#^2+b_.* #)&)", //
        "True");
    check("MatchQ(#-1*#^2, b_.* #+c_.*#^2)", //
        "True");

    check("MatchQ(_Integer)[123]", //
        "True");
    check("MatchQ(22/7, _Rational)", //
        "True");
    check("MatchQ(6/3, _Rational)", //
        "False");

    check("MatchQ(22/7, _Rational)", //
        "True");
    check("MatchQ(b*x,a_.+x^n_.*b_./;FreeQ({a,b,n},x))", //
        "True");
    check("MatchQ(x,a_.+x^n_.*b_./;FreeQ({a,b,n},x))", //
        "True");
  }

  @Test
  public void testMatchQRubi001() {
    check("MatchQ[1 + 2*x, (a_. + b_.*x_Symbol)]", //
        "True");
    check("MatchQ[0 + 2*x, (a_. + b_.*x_Symbol)]", //
        "True");
    check("MatchQ[1 + x, (a_.+x_Symbol)]", //
        "True");
  }

  @Test
  public void testMatchQRubi002() {
    check("a/.Optional(c1_?NumberQ)*a_->{{c1},{a}}", //
        "{{1},{a}}");
    check("Default(g)=0", //
        "0");
    check("g(a_., b_.):={a,b}", //
        "");
    check("g(x) ", //
        "{x,0}");
    check("f( a_. + b_. ):={a,b}", //
        "");
    check("f( x ) ", //
        "{x,0}");
    check("MatchQ(2+x, ( a_. + b_. ) )", //
        "True");
    check("MatchQ(x, ( a_. + b_. ) )", //
        "True");
  }

  @Test
  public void testMathMLForm() {
    check("MathMLForm(Sqrt(3)-I)", //
        "<?xml version=\"1.0\"?>\n" //
            + "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n" //
            + "<math mode=\"display\">\n" //
            + "<mrow><msqrt><mn>3</mn></msqrt><mo>-</mo><mrow><mi>&#x2148;</mi></mrow></mrow></math>");
    check("MathMLForm(a/2+Tan(x)/4-Tan(x)^2/3+12)", //
        "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
            + "<math mode=\"display\">\n"
            + "<mrow><mn>12</mn><mo>-</mo><mfrac><mrow><msup><mrow><mi>tan</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow><mn>2</mn></msup></mrow><mn>3</mn></mfrac><mo>+</mo><mfrac><mrow><mi>tan</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow><mn>4</mn></mfrac><mo>+</mo><mfrac><mi>a</mi><mn>2</mn></mfrac></mrow></math>");
    check("MathMLForm( Surd(a,-3)  )", //
        "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
            + "<math mode=\"display\">\n"
            + "<mfrac><mn>1</mn><mroot><mi>a</mi><mn>3</mn></mroot></mfrac></math>");
    check("MathMLForm( Surd(a,3)  )", //
        "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
            + "<math mode=\"display\">\n" + "<mroot><mi>a</mi><mn>3</mn></mroot></math>");

    check("MathMLForm( f(#,#3)&  )", //
        "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
            + "<math mode=\"display\">\n"
            + "<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>#1</mi><mo>,</mo><mi>#3</mi></mrow><mo>)</mo></mrow></mrow><mo>&amp;</mo></mrow></math>");
    check("MathMLForm(D(sin(x)*cos(x),x))", "<?xml version=\"1.0\"?>\n"
        + "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
        + "<math mode=\"display\">\n"
        + "<mfrac><mrow><mo>&#x2202;</mo><mrow><mrow><mi>sin</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>cos</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow></mrow></mrow><mrow><mo>&#x2202;</mo><mi>x</mi></mrow></mfrac></math>");
  }

  @Test
  public void testMax() {

    check("Max(Sequence())", //
        "-Infinity");
    check("Refine(Max(Infinity,x), x>0)", //
        "Infinity");
    check("Max(Interval({1,2}))", //
        "2");
    check("Refine(Max(Infinity,x,y), x>0&&y>0)", //
        "Infinity");
    check("Refine(Max(Infinity,x,y), x>0)", //
        "Max(Infinity,y)");
    check("Refine(Max(x,Infinity), x>0)", //
        "Infinity");
    check("Refine(Max(x,y,Infinity), x>0&&y>0)", //
        "Infinity");

    check("Refine(Max(-Infinity,x), x>0)", //
        "x");
    check("Refine(Max(-Infinity,x,y), x>0&&y>0)", //
        "Max(x,y)");
    check("Refine(Max(x,-Infinity), x>0)", //
        "x");
    check("Refine(Max(x,y,-Infinity), x>0&&y>0)", //
        "Max(x,y)");
    check("Refine(Max(x,y,-Infinity), x>0)", //
        "Max(x,y)");

    check("Max(4, -8, 1)", //
        "4");
    check("Max({1,2},3,{-3,3.5,-Infinity},{{1/2}})", //
        "3.5");
    check("Max(x, y)", //
        "Max(x,y)");
    check("Max(5, x, -3, y, 40)", //
        "Max(40,x,y)");
    check("Max()", //
        "-Infinity");
    check("Max(x)", "x");
    check("Max(Abs(x), Abs(y))", //
        "Max(Abs(x),Abs(y))");
  }

  @Test
  public void testMaxFilter() {
    check("MaxFilter({a,b,c}, 1)", //
        "{Max(a,b),Max(a,b,c),Max(b,c)}");

    check("MaxFilter({1, 2, 3, 2, 1}, 1)", //
        "{2,3,3,3,2}");
    check("MaxFilter({0, 3, 8, 2}, 1)", //
        "{3,8,8,8}");
    check("MaxFilter({a,b,c}, 1)", //
        "{Max(a,b),Max(a,b,c),Max(b,c)}");
  }

  @Test
  public void testMaximize() {
    // print message - Maximize: The maximum is not attained at any point satisfying the
    // constraints.
    check("Maximize(1/x, x)", //
        "{}");

    check("Maximize(-x^4-7*x^3+2*x^2 - 42,x)", //
        "{-42-7/512*(-21-Sqrt(505))^3+(21+Sqrt(505))^2/32-(21+Sqrt(505))^4/4096,{x->1/8*(-\n"
            + "21-Sqrt(505))}}");
    check("Maximize(x^4+7*Tan(x)-2*x^2 + 42, x)", //
        "Maximize(42-2*x^2+x^4+7*Tan(x),x)");
    check("Maximize(x^4+7*x^3-2*x^2 + 42, x)", //
        "{Infinity,{x->-Infinity}}");
    check("Maximize(-2*x^2 - 3*x + 5, x)", //
        "{49/8,{x->-3/4}}");
  }

  @Test
  public void testMean() {
    check("Mean(Array(Subscript(a, ##) &, {2, 2}))", //
        "{1/2*(Subscript(a,1,1)+Subscript(a,2,1)),1/2*(Subscript(a,1,2)+Subscript(a,2,2))}");
    check("Mean(Array(Subscript(a, ##) &, {2, 2, 2}))", //
        "{{1/2*(Subscript(a,1,1,1)+Subscript(a,2,1,1)),1/2*(Subscript(a,1,1,2)+Subscript(a,\n"
            + "2,1,2))},{1/2*(Subscript(a,1,2,1)+Subscript(a,2,2,1)),1/2*(Subscript(a,1,2,2)+Subscript(a,\n"
            + "2,2,2))}}");
    check("Mean({26, 64, 36})", //
        "42");
    check("Mean({1, 1, 2, 3, 5, 8})", //
        "10/3");
    check("Mean({a, b})", //
        "1/2*(a+b)");

    check("Mean({{a, u}, {b, v}, {c, w}})", //
        "{1/3*(a+b+c),1/3*(u+v+w)}");
    check("Mean({1.21, 3.4, 2.15, 4, 1.55})", //
        "2.462");
    check("Mean({a,b,c,d})", //
        "1/4*(a+b+c+d)");

    check("Mean(BetaDistribution(a,b))", //
        "a/(a+b)");
    check("Mean(BernoulliDistribution(p))", //
        "p");
    check("Mean(BinomialDistribution(n, m))", //
        "m*n");
    check("Mean(ExponentialDistribution(n))", "1/n");
    check("Mean(PoissonDistribution(p))", //
        "p");
    check("Mean(BinomialDistribution(n, p))", //
        "n*p");
    check("Mean(DiscreteUniformDistribution({l, r}))", //
        "1/2*(l+r)");
    check("Mean(ErlangDistribution(n, m))", //
        "n/m");
    check("Mean(LogNormalDistribution(m,s))", //
        "E^(m+s^2/2)");
    check("Mean(NakagamiDistribution(n, m))", //
        "(Sqrt(m)*Pochhammer(n,1/2))/Sqrt(n)");
    check("Mean(NormalDistribution(n, p))", //
        "n");
    check("Mean(FrechetDistribution(n, m))", //
        "Piecewise({{m*Gamma(1-1/n),1<n}},Infinity)");
    check("Mean(GammaDistribution(n, m))", //
        "m*n");
    check("Mean(GeometricDistribution(n))", //
        "-1+1/n");
    check("Mean(GumbelDistribution(n, m))", //
        "-EulerGamma*m+n");
    check("Mean(HypergeometricDistribution(n, ns, nt))", //
        "(n*ns)/nt");
    check("Mean(StudentTDistribution(4))", //
        "0");
    check("Mean(StudentTDistribution(n))", //
        "Piecewise({{0,n>1}},Indeterminate)");
    check("Mean(WeibullDistribution(n, m))", //
        "m*Gamma(1+1/n)");
  }

  @Test
  public void testMeanFilter() {
    check("MeanFilter({-3, 3, 6, 0, 0, 3, -3, -9}, 2)", //
        "{2,3/2,6/5,12/5,6/5,-9/5,-9/4,-3}");
    check("MeanFilter({1, 2, 3, 2, 1}, 1)", //
        "{3/2,2,7/3,2,3/2}");
    check("MeanFilter({0, 3, 8, 2}, 1)", //
        "{3/2,11/3,13/3,5}");
    check("MeanFilter({a,b,c}, 1)", //
        "{1/2*(a+b),1/3*(a+b+c),1/2*(b+c)}");
  }

  @Test
  public void testMeanDeviation() {
    // Config.MAX_AST_SIZE=Integer.MAX_VALUE;
    // check(
    // "MeanDeviation(RandomReal(1, 10^4))", //
    // "0.243758");
    check("MeanDeviation(SparseArray({{1, 2}, {4, 8}, {5, 3}, {2, 15}}))", //
        "{3/2,9/2}");
    check("MeanDeviation(1+(-1)*1)", //
        "MeanDeviation(0)");
    check("MeanDeviation({a, b, c})", //
        "1/3*(Abs(a+1/3*(-a-b-c))+Abs(b+1/3*(-a-b-c))+Abs(1/3*(-a-b-c)+c))");
    check("MeanDeviation({{1, 2}, {4, 8}, {5, 3}, {2, 15}})", //
        "{3/2,9/2}");
    check("MeanDeviation({1, 2, 3, 7})", //
        "15/8");
    check("MeanDeviation({Pi, E, 2})//Together", //
        "2/9*(-4+E+Pi)");
  }

  @Test
  public void testMedian() {
    check("Median(WeightedData({8, 3, 5,4}, " + //
        "{0.15, 0.09, 0.12,0.10}))", //
        "5");
    check("Median(WeightedData({3, 4,5,8}, " + //
        "{0.09, 0.10,0.12,0.15 }))", //
        "5");

    check("Median(WeightedData({8, 3, 5, 4, 9, 0, 4, 2, 2, 3}, " + //
        "{0.15, 0.09, 0.12, 0.10, 0.16, 0., 0.11, 0.08, 0.08, 0.09}))", //
        "4");
    check("Median(WeightedData({a,b,c,g}, {d,e,f,h}))", //
        "b*Boole(d/(d+e+f+h)<1/2<=d/(d+e+f+h)+e/(d+e+f+h))+c*Boole(d/(d+e+f+h)+e/(d+e+f+h)<1/\n"
            + "2<=d/(d+e+f+h)+e/(d+e+f+h)+f/(d+e+f+h))+g*Boole(d/(d+e+f+h)+e/(d+e+f+h)+f/(d+e+f+h)<\n"
            + "1/2<=d/(d+e+f+h)+e/(d+e+f+h)+f/(d+e+f+h)+h/(d+e+f+h))+a*Boole(1/2<=d/(d+e+f+h))");
    check("Median(WeightedData({a,b,c}, {d,e,f}))", //
        "b*Boole(d/(d+e+f)<1/2<=d/(d+e+f)+e/(d+e+f))+c*Boole(d/(d+e+f)+e/(d+e+f)<1/2<=d/(d+e+f)+e/(d+e+f)+f/(d+e+f))+a*Boole(\n"
            + //
            "1/2<=d/(d+e+f))");
    check("Median(WeightedData({a,b}, {d,e}))", //
        "b*Boole(d/(d+e)<1/2<=d/(d+e)+e/(d+e))+a*Boole(1/2<=d/(d+e))");

    check("Median({{100, 1, 10, 50}, {-1, 1, -2, 2}})", //
        "{99/2,1,4,26}");
    check("Median({26, 64, 36})", //
        "36");
    check("Median({-11, 38, 501, 1183})", //
        "539/2");
    check("Median({{100, 1, 10, 50}, {-1, 1, -2, 2}})", //
        "{99/2,1,4,26}");

    check("Median({1,2,3,4,5,6,7.0})", //
        "4.0");
    check("Median({1,2,3,4,5,6,7.0,8})", //
        "4.5");
    check("Median({1,2,3,4,5,6,7})", //
        "4");

    check("Median(BernoulliDistribution(p))", //
        "Piecewise({{1,p>1/2}},0)");
    check("Median(BetaDistribution(a,b))", //
        "InverseBetaRegularized(1/2,a,b)");
    check("Median(BinomialDistribution(n, m))", //
        "Median(BinomialDistribution(n,m))");
    check("Median(ExponentialDistribution(n))", //
        "Log(2)/n");
    check("Median(PoissonDistribution(p))", //
        "Median(PoissonDistribution(p))");
    check("Median(DiscreteUniformDistribution({l, r}))", //
        "-1+l+Max(1,Ceiling(1/2*(1-l+r)))");
    check("Median(UniformDistribution({l, r}))", //
        "1/2*(l+r)");
    check("Median(ErlangDistribution(n, m))", //
        "InverseGammaRegularized(n,0,1/2)/m");
    check("Median(LogNormalDistribution(m,s))", //
        "E^m");
    check("Median(NakagamiDistribution(n, m))", //
        "Sqrt((m*InverseGammaRegularized(n,0,1/2))/n)");
    check("Median(NormalDistribution())", //
        "0");
    check("Median(NormalDistribution(n, p))", //
        "n");
    check("Median(FrechetDistribution(n, m))", //
        "m/Log(2)^(1/n)");
    check("Median(GammaDistribution(n, m))", //
        "m*InverseGammaRegularized(n,0,1/2)");
    check("Median(GammaDistribution(a,b,g,d))", //
        "d+b*InverseGammaRegularized(a,1/2)^(1/g)");
    check("Median(GeometricDistribution(n))", //
        "Median(GeometricDistribution(n))");
    check("Median(GumbelDistribution( ))", //
        "Log(Log(2))");
    check("Median(GumbelDistribution(n, m))", //
        "n+m*Log(Log(2))");
    check("Median(HypergeometricDistribution(n, ns, nt))", //
        "Median(HypergeometricDistribution(n,ns,nt))");
    check("Median(StudentTDistribution(4))", //
        "0");
    check("Median(StudentTDistribution(n))", //
        "0");
    check("Median(StudentTDistribution(m,s,v))", //
        "m");
    check("Median(WeibullDistribution(a, b))", //
        "b*Log(2)^(1/a)");
    check("Median(WeibullDistribution(a, b, m))", //
        "m+b*Log(2)^(1/a)");
  }

  @Test
  public void testMedianFilter() {
    // Wikipedia example with "shrinking the window near the boundaries"
    check("MedianFilter({2,3,80,6}, 1)", //
        "{5/2,3,6,43}");

    check("MedianFilter({1, 2, 3, 2, 1}, 1)", //
        "{3/2,2,2,2,3/2}");
    check("MedianFilter({0, 3, 8, 2}, 1)", //
        "{3/2,3,3,5}");
    check("MedianFilter({a,b,c}, 1)", //
        "{1/2*(a+b),b,1/2*(b+c)}");
  }

  @Test
  public void testMeijerG() {
    checkNumeric("MeijerG({{ }, {}}, {{0}, {}}, 0)", //
        "1");
    checkNumeric("MeijerG({{ }, {a2}}, {{0}, {b2}}, 0)", //
        "1/(Gamma(a2)*Gamma(1-b2))");
    checkNumeric("MeijerG({{1}, {a2}}, {{b1}, {}}, 0)", //
        "Gamma(b1)*Hypergeometric1F1Regularized(b1,a2,ComplexInfinity)");
    checkNumeric("MeijerG({{1}, {a2}}, {{}, {b2}}, 0)", //
        "Hypergeometric1F1Regularized(b2,a2,ComplexInfinity)/Gamma(1-b2)");
    checkNumeric("MeijerG({{1}, {}}, {{}, {}}, 0)", //
        "Infinity");
    checkNumeric("MeijerG({{0}, {}}, {{}, {}}, 0)", //
        "-Infinity");
    checkNumeric("MeijerG({{a1}, {}}, {{0}, {}}, 0)", //
        "Gamma(1-a1)");
    checkNumeric("MeijerG({{1/2}, {}}, {{0}, {0}}, 0)", //
        "Sqrt(Pi)");
    checkNumeric("MeijerG({{}, {}}, {{b1}, {}},z)", //
        "z^b1/E^z");
    checkNumeric("MeijerG({{1/2}, {1/2}}, {{}, {}}, 0)//N", //
        "ComplexInfinity");
    checkNumeric("MeijerG({{1}, {2}}, {{2.3}, {4}}, 3)", //
        "0.0");
    check("MeijerG({{1, 1}, {}}, {{1}, {0}}, z)", //
        "Log(1+z)");
    // TODO
    checkNumeric("N(MeijerG({{}, {2}}, {{1/2, 3/2}, {}}, 3), 50)", //
        "MeijerG({{},{2}},{{1/2,3/2},{}},3)");

    checkNumeric("MeijerG({{}, {0.33}}, {{Pi}, {E}}, {-0.5,0.5})", //
        "{0.01650236063766311+I*0.00786651270222696,-0.34952526547708834}");

    check("MeijerG({{}, {a2}}, {{b1}, {b2}}, z)", //
        "(z^b1*Hypergeometric1F1Regularized(1-a2+b1,1+b1-b2,z))/Gamma(a2-b1)");
    check("MeijerG({{a1}, {}}, {{b1}, {b2}}, z)", //
        "z^b1*Gamma(1-a1+b1)*Hypergeometric1F1Regularized(1-a1+b1,1+b1-b2,-z)");
    check("MeijerG({{a1}, {a2}}, {{b1}, {}}, z)", //
        "(Gamma(1-a1+b1)*Hypergeometric1F1Regularized(1-a1+b1,1-a1+a2,-1/z))/z^(1-a1)");
    check("MeijerG({{a1}, {a2}}, {{}, {b2}}, z)", //
        "Hypergeometric1F1Regularized(1-a1+b2,1-a1+a2,1/z)/(z^(1-a1)*Gamma(a1-b2))");
    check("MeijerG({{}, {}}, {{b1}, {-b1}}, z)", //
        "BesselJ(2*b1,2*Sqrt(z))");
    check("MeijerG({{a1}, {a2}}, {{}, {}}, z)", //
        "BesselJ(-a1+a2,2/Sqrt(z))/z^(1-a1-(-a1+a2)/2)");
    check("MeijerG({{a}, {}}, {{}, {b}}, 2)", //
        "2^b/Gamma(a-b)");
    check("MeijerG({{a}, {}}, {{}, {}}, z) ", //
        "1/(E^(1/z)*z^(1-a))");
    // print message
    check("MeijerG({{}, {}}, {{}, {}}, z) ", //
        "MeijerG({{},{}},{{},{}},z)");
  }

  @Test
  public void testMemberQ() {
    check("MemberQ({Sin, Cos, Tan, Cot, Sec, Csc}, If(AtomQ(Cos),Cos,Head(Cos)))", //
        "True");
    check("MemberQ(x,x)", //
        "False");
    check("MemberQ({{x^2, y^2}}, x^_)", //
        "False");
    check("MemberQ({{x^2, y^2}}, x^_, 2)", //
        "True");
    check("MemberQ({{1, 1, 3, 0}, {2, 1, 2, 2}}, 0)", //
        "False");
    check("MemberQ({{1, 1, 3, 0}, {2, 1, 2, 2}}, 0, 2)", //
        "True");
    check("MemberQ({a, b, c}, b)", //
        "True");
    check("MemberQ({a, b, c}, d)", //
        "False");
    check("MemberQ({\"a\", b, f(x)}, _?NumericQ)", //
        "False");
    check("MemberQ({\"a\", 42, f(x)}, _?NumericQ)", //
        "True");
    check("MemberQ(_List)[{{}}]", //
        "True");

    check("MemberQ(x^_)[{x^2, y^2, x^3}]", //
        "True");
    check("MemberQ({1, 3, 4, 1, 2}, 2)", //
        "True");
    check("MemberQ({x^2, y^2, x^3}, x^_)", //
        "True");
    check("MemberQ(a + b + f(c), f)", //
        "False");
    check("MemberQ(a + b + f(c), f, Heads->True)", //
        "False");
    check("MemberQ(a + b + c, a + c)", //
        "False");
  }

  @Test
  public void testMersennePrimeExponent() {
    check("Table(MersennePrimeExponent(i), {i,20})",
        "{2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423}");
    check("MersennePrimeExponent(1)", //
        "2");
    check("MersennePrimeExponent(21)", //
        "9689");
    check("MersennePrimeExponent(22)", //
        "9941");
    check("MersennePrimeExponent(23)", //
        "11213");
    check("MersennePrimeExponent(44)", //
        "32582657");
    check("MersennePrimeExponent(45)", //
        "37156667");
    check("MersennePrimeExponent(47)", //
        "43112609");
    check("MersennePrimeExponent(52)", //
        "136279841");
  }

  @Test
  public void testMersennePrimeExponentQ() {
    check("Select(Range(10000), MersennePrimeExponentQ)",
        "{2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423,9689,\n"
            + "9941}");
  }

  @Test
  public void testMessage() {
    check("Message(f::argx, 1, 2)", //
        "f: 1 called with 2 arguments; 1 argument is expected.");
    check("a::b:=\"Hello world\"", //
        "");
    check("Message(a::b)", //
        "a: Hello world");
    check("a::c:=\"Hello `1`, Mr 00`2`!\"", //
        "");
    check("Message(a::c, \"you\", 3 + 4)", //
        "a: Hello you, Mr 007!");

    check("f::failure=\"`1` called with wrong argument; `2`, `3`.\"", //
        "`1` called with wrong argument; `2`, `3`.");
    check("Message(f::failure, f, x, y)", //
        "f: f called with wrong argument; x, y.");
    check("Message(Rule::argr, Rule, 2)", //
        "Rule: Rule called with 1 argument; 2 arguments are expected.");
  }

  @Test
  public void testMessages() {
    check("Messages(\"fa\")", //
        "Messages(fa)");
    check("a::hello:=\"Hello world\"; a::james:=\"Hello `1`, Mr 00`2`!\"", //
        "");
    check("Messages(a)", //
        "{HoldPattern(a::hello):>Hello world,HoldPattern(a::james):>Hello `1`, Mr 00`2`!}");
  }

  @Test
  public void testMessageName() {
    // print "ValueQ - test whether a symbol can be considered to have a value"
    check("?ValueQ", //
        "");
    check("FullForm(a::b)", //
        "MessageName(a, b)");
    check("FullForm(a::\"b\")", //
        "MessageName(a, \"b\")");

    // Set[MessageName(f,"usage"),"A usage message")
    check("f::usage=\"A usage message\"", //
        "A usage message");
    // print "A usage message" on the console. Evaluation returns Null (i.e. no output)
    check("Information(f)", //
        "");
    check("Information(Sin)", //
        "");
    check("Information(Sin, LongForm->False)", //
        "");
    // print "A usage message" on the console. Evaluation returns Null (i.e. no output)
    check("?f", //
        "");
    check("??Sin;??Cos", //
        "");
  }

  @Test
  public void testMin() {
    check("Min(Sequence())", //
        "Infinity");
    check("Min(Interval({1,2}))", //
        "1");

    check("Refine(Min(-Infinity,x), x>0)", //
        "-Infinity");
    check("Refine(Min(-Infinity,x,y), x>0&&y>0)", //
        "-Infinity");
    check("Refine(Min(-Infinity,x,y), x>0)", //
        "Min(-Infinity,y)");
    check("Refine(Min(x,-Infinity), x>0)", //
        "-Infinity");
    check("Refine(Min(x,y,-Infinity), x>0&&y>0)", //
        "-Infinity");

    check("Refine(Min(Infinity,x), x>0)", //
        "x");
    check("Refine(Min(Infinity,x,y), x>0&&y>0)", //
        "Min(x,y)");
    check("Refine(Min(x,Infinity), x>0)", //
        "x");
    check("Refine(Min(x,y,Infinity), x>0&&y>0)", //
        "Min(x,y)");
    check("Refine(Min(x,y,Infinity), x>0&&y>0)", //
        "Min(x,y)");

    check("Refine(Infinity<x, x>0)", //
        "False");

    check("Min(5, x, -3, y, 40)", //
        "Min(-3,x,y)");
    check("Min(4, -8, 1)", //
        "-8");
    check("Min({1,2},3,{-3,3.5,-Infinity},{{1/2}})", //
        "-Infinity");
    check("Min(x, y)", //
        "Min(x,y)");
    check("Min(5, x, -3, y, 40)", //
        "Min(-3,x,y)");
    check("Min()", //
        "Infinity");
    check("Min(x)", //
        "x");
    check("Min(Abs(x), Abs(y))", //
        "Min(Abs(x),Abs(y))");
  }

  @Test
  public void testMinMax() {
    check("MinMax({1, 2, 3, 4})", //
        "{1,4}");
    check("MinMax({1, 2, 3, 4},.2)", //
        "{0.8,4.2}");
    check("MinMax({1, 2, 3, 4}, Scaled(.2))", //
        "{0.4,4.6}");
    check("MinMax({Pi, 1.3, E, Sqrt(10)})", //
        "{1.3,Sqrt(10)}");
    check("MinMax({Pi, 1.3, E, Sqrt(10)}, 1)", //
        "{0.3,1+Sqrt(10)}");
    check("MinMax({Pi, 1.3, E, Sqrt(10)}, Scaled(1/4))", //
        "{0.834431,3.62785}");
    check("MinMax({Pi, 1.3, E, Sqrt(10)}, {0, 1})", //
        "{1.3,1+Sqrt(10)}");
    check("Max({{1, 2}, {a, b}, {3, 2}})", //
        "Max(3,a,b)");
    check("MinMax({{1, 2}, {a, b}, {3, 2}})", //
        "{Min(1,a,b),Max(3,a,b)}");

    check("MinMax({ })", //
        "{Infinity,-Infinity}");
    check("MinMax({3, 1, 2, 5, 4})", //
        "{1,5}");
    check("Quantile({3, 1, 2, 5, 4}, {0, 1})", //
        "{1,5}");
  }

  @Test
  public void testMinFilter() {
    check("MinFilter({1, 2, 3, 2, 1}, 1)", //
        "{1,1,2,1,1}");
    check("MinFilter({0, 3, 8, 2}, 1)", //
        "{0,0,2,2}");
    check("MinFilter({a,b,c}, 1)", //
        "{Min(a,b),Min(a,b,c),Min(b,c)}");
  }

  @Test
  public void testMinimize() {
    // check("Minimize(Sin(x),x)", //
    // "");

    // print message - Minimize: The minimum is not attained at any point satisfying the
    // constraints.
    check("Minimize(1/x, x)", //
        "{}");


    check("Minimize(x^2+4*x+4, {x})", //
        "{0,{x->-2}}");

    check("Minimize(x^4+7*x^3-2*x^2 + 42, x)", //
        "{42+7/512*(-21-Sqrt(505))^3-(21+Sqrt(505))^2/32+(21+Sqrt(505))^4/4096,{x->1/8*(-\n"
            + "21-Sqrt(505))}}");
    check("Minimize(2*x^2 - 3*x + 5, x)", //
        "{31/8,{x->3/4}}");
  }

  @Test
  public void testMinus() {
    check("Minus(a)", //
        "-a");
    check("-a //FullForm", //
        "Times(-1, a)");
    check("-(x - 2/3)", //
        "2/3-x");
    check("-Range(10)", //
        "{-1,-2,-3,-4,-5,-6,-7,-8,-9,-10}");
  }

  @Test
  public void testMissingQ() {
    check("MissingQ(Missing(\"Test message\"))", //
        "True");
  }

  @Test
  public void testMod() {
    check("Mod(Infinity, 1+I)", //
        "Indeterminate");
    check("Mod(Infinity, 3)", //
        "Indeterminate");
    check("Mod(-1,I-1)", //
        "-1");
    check("{a, b, c}[[Mod(Range(10), 3, 1)]]", //
        "{a,b,c,a,b,c,a,b,c,a}");
    check("Mod(Range(10), 3, I)", //
        "{1,-1,0,1,-1,0,1,-1,0,1}");
    check("Mod({1, 2, 3, 4, 5, 6, 7}, 3)", //
        "{1,2,0,1,2,0,1}");
    check("Mod({1, 2, 3, 4, 5, 6, 7}, -3, 1)", //
        "{1,-1,0,1,-1,0,1}");
    check("Mod({1, 2, 3, 4, 5, 6, 7}, -3, 4)", //
        "{4,2,3,4,2,3,4}");
    check("Mod({1, 2, 3, 4, 5, 6, 7}, -3, -2)", //
        "{-2,-4,-3,-2,-4,-3,-2}");
    check("Mod({-3, -2, -1, 0, 1, 2, 3}, -3)", //
        "{0,-2,-1,0,-2,-1,0}");

    check("Mod(7,2,3)", //
        "3");
    check("Mod(I,2,3)", //
        "4+I");
    check("Mod(5-Pi/2,Pi)", //
        "5-3/2*Pi");
    check("Mod(Sqrt(-113), 2)", //
        "-I*10+I*Sqrt(113)");
    check("Mod(Exp(Pi), 2)", //
        "-22+E^Pi");
    check("Mod(42,Pi)", //
        "42-13*Pi");
    check("Mod(-42,Pi)", //
        "-42+14*Pi");
    check("Mod(-10,3)", //
        "2");
    check("Mod(10,3)", //
        "1");
    check("Mod(10,-3)", //
        "-2");
    check("Mod(-10,-3)", //
        "-1");

    check("Mod(-23,7)", //
        "5");
    check("Mod(23,7)", //
        "2");
    check("Mod(23,-7)", //
        "-5");
    check("Mod(-23,-7)", //
        "-2");

    check("Mod(14, 6)", //
        "2");
    check("Mod(-3,4)", //
        "1");
    check("Mod(-3,-4)", //
        "-3");
    check("Mod(2,-4)", //
        "-2");
    check("Mod(3,-4)", //
        "-1");
    check("Mod(5,0)", //
        "Indeterminate");
  }


  @Test
  public void testModularInverse() {
    // message ModularInverse: 3 is not invertible modulo 9.
    check("ModularInverse(3, 9)", //
        "ModularInverse(3,9)");

    check("ModularInverse(2, 3)", //
        "2");
    check("ModularInverse(-3, 5)", //
        "3");
    check("ModularInverse(3, 11)", //
        "4");
    check("ModularInverse(-3, 11)", //
        "7");
    check("ModularInverse(3, -11)", //
        "-7");
    check("ModularInverse(-3, -5)", //
        "-2");
    check("ModularInverse(3, -1)", //
        "0");
    // Config.MAX_BIT_LENGTH = Integer.MAX_VALUE;
    // check("ModularInverse(10^100000, NextPrime(1000))", //
    // "942");

  }

  @Test
  public void testModule() {
    // message: Module: Duplicate local variable x found in local variable specification {x=a,x=b}.
    check("Module({x = a, x=b}, x^2)", //
        "Module({x=a,x=b},x^2)");
    EvalEngine.resetModuleCounter4JUnit();
    // check("num=Sin(3*I);Module({v=N(num)},If(PossibleZeroQ(Re(v)),Im(v)>0,Re(v)>0))",
    // "True");
    // check("Module({x=5}, Hold(x))", "Hold(x$1)");

    check("xm=10;Module({xm=xm}, xm=xm+1;xm);xm", //
        "10");
    check("xm=10;Module({t=xm}, xm=xm+1;t)", //
        "10");
    check("xm=10;Module({t=xm}, xm=xm+1;t);xm", //
        "11");
    EvalEngine.resetModuleCounter4JUnit();
    check("{Module({a}, Block({a}, a)),Module({a}, Block({}, a))}", //
        "{a$1,a$2}");
    check("t === Module({t}, t)", //
        "False");
    check("$g(x_) := Module({v=x},int(v,x)/;v=!=x);$g(f(x))", //
        "$g(f(x))");
    check("$g(x_) := Module({v=x},int1(v,x)/;v===x);$g(f(x))", //
        "int1(f(x),f(x))");
    check("$h(x_) := Module({$u}, $u^2 /; (($u = x - 1) > 0));$h(6)", //
        "25");
    checkNumeric(
        "$f(x0_) :=\n" + " Module({x = x0},\n" + "  While(x > 0, x = Log(x));\n" + "  x\n"
            + "  );$f(2.0)", //
        "-0.36651292058166435");

    check(
        "$fib(n_) :=\n" + " Module({$f},\n" + "  $f(1) = $f(2) = 1;\n"
            + "  $f(i0_) := $f(i0) = $f(i0 - 1) + $f(i0 - 2);\n" + "  $f(n)\n" + "  );$fib(5)", //
        "5");

    check(
        "$gcd(m0_, n0_) :=\n" + " Module({m = m0, n = n0},\n"
            + "  While(n != 0, {m, n} = {n, Mod(m, n)});\n" + "  m\n" + "  );$gcd(18, 21)", //
        "3");
    EvalEngine.resetModuleCounter4JUnit();
    check("{Module({x}, x), Module({x}, x)}", //
        "{x$1,x$2}");
    check("Module({e = Expand((1 + x)^5)}, Function(x, e))", //
        "Function(x$3,e$3)");
    EvalEngine.resetModuleCounter4JUnit();
    check("Module({a,b}, Block({c}, c+a))", //
        "a$1+c");

    if (Config.SERVER_MODE == false) {
      check("f(x0_) :=\n" + " Module({x = x0},\n" + "  While(x > 0, x = Log(x));\n" + "  x\n"
          + "  );f(2.0)", "-0.366513");

      check("fib(n_) :=\n" + " Module({f},\n" + "  f(1) = f(2) = 1;\n"
          + "  f(i_) := f(i) = f(i - 1) + f(i - 2);\n" + "  f(n)\n" + "  );fib(5)", "5");

      check("myGCD(m0_, n0_) :=\n" + " Module({m = m0, n = n0},\n"
          + "  While(n != 0, {m, n} = {n, Mod(m, n)});\n" + "  m\n" + "  );myGCD(18, 21)", "3");
    }

    check("xm=10;Module({xm=xm}, xm=xm+1;Print(xm));xm", //
        "10");
    check("Module({var1=2*2,var2=var1}, {var1,var2})", //
        "{4,var1}");
    check("Module({x=y,y=z,z=3}, Print({Hold(x),Hold(y),Hold(z)});{x,y,z})", //
        "{y,z,3}");
    check("Module({x,f}, f(0)=0;f(x_):=f(x-1)+x;f(3))", //
        "6");

    EvalEngine.resetModuleCounter4JUnit();
    check("Module({x},Function(y,x+y))", //
        "Function(y$1,x$1+y$1)");
    // check("Module({y},Function(y,x+y))",//
    // "x$22$23+y");

    check("Module({x}, g2(x_)=Integrate(Sqrt(1+z^2),{z,0,x}));Table(g2(i),{i,3})", //
        "{1/Sqrt(2)+ArcSinh(1)/2,Sqrt(5)+ArcSinh(2)/2,3*Sqrt(5/2)+ArcSinh(3)/2}");

    check("v=Null;Module({w=v},Catch(Scan(Function(If(False ,Throw(False))),u); w))", //
        "");

    check("t=42; Length(Expand((1 + t)^3)) ", //
        "0");
    check("t=42;Module({t}, Length(Expand((1 + t)^3)))", //
        "4");
    check("g(u_):= Module({t = u}, t += t/(1 + u)); g(a)", //
        "a+a/(1+a)");
    check("t=17;Module({t = 6, u = t}, u^2)", //
        "289");
    check("t=17;h(x_):=Module({t}, t^2 - 1 /; (t = x - 4) > 1); h(10)", //
        "35");

    check("f(n_) :=  Module({p = Range(n),i,x,t},\n"
        + "            Do(x = RandomInteger({1,i}); t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,\n"
        + "               {i,n,2,-1}\n" + "            );\n" + "               p\n"
        + "          )\n", "");
    check("MatchQ(f(4),{_Integer..})", //
        "True");
    check("Length(f(6))", //
        "6");
  }

  @Test
  public void testMoebiusMu() {
    check("MoebiusMu(-30)", //
        "-1");
    check("FactorInteger(30)", //
        "{{2,1},{3,1},{5,1}}");
    check("MoebiusMu(30)", //
        "-1");
    check("Table(MoebiusMu(k), {k, 0,50})", //
        "{0,1,-1,-1,0,-1,1,-1,0,0,1,-1,0,-1,1,1,0,-1,0,-1,0,1,1,-1,0,0,1,0,0,-1,-1,-1,0,1,\n"
            + "1,1,0,-1,1,1,0,-1,-1,-1,0,0,1,-1,0,0,0}");
    check("MoebiusMu({1000,10000})", //
        "{0,0}");
    check("MoebiusMu(-a)", //
        "MoebiusMu(a)");
    check("MoebiusMu(47)", //
        "-1");
    check("MoebiusMu(51)", //
        "1");
    check("MoebiusMu(17291)", //
        "-1");
    check("MoebiusMu({2, 4, 7, 9})", //
        "{-1,0,-1,0}");
    check("MoebiusMu(-100)", //
        "0");
    check("Table(MoebiusMu(k), {k, 12})", //
        "{1,-1,-1,0,-1,1,-1,0,0,1,-1,0}");
    check("Table(MoebiusMu(-k), {k, 12})", //
        "{1,-1,-1,0,-1,1,-1,0,0,1,-1,0}");
    check("FactorInteger(183245)", //
        "{{5,1},{67,1},{547,1}}");
    check("MoebiusMu(183245)", //
        "-1");
    check("MoebiusMu(210)", //
        "1");
    check("MoebiusMu(192)", //
        "0");
  }

  @Test
  public void testMonomialList() {
    check("MonomialList(7*y^w, {y,z})", //
        "{7*y^w}");
    check("MonomialList(7*y^(3*w), y )", //
        "{7*y^(3*w)}");
    check("MonomialList(c*x^2+a+b*x,x)", //
        "{c*x^2,b*x,a}");
    check("MonomialList(c*x^(-2)+a+b^2*x,b)", //
        "{b^2*x,a+c/x^2}");
    check("MonomialList(c*x^(-2)+a+b*x,x)", //
        "{a+c/x^2+b*x}");

    check("MonomialList((x + y)^3)", //
        "{x^3,3*x^2*y,3*x*y^2,y^3}");
    check("MonomialList(x^2*y^2 + x^3, {x, y})", //
        "{x^3,x^2*y^2}");
    check("MonomialList(x^2*y^2 + x^3, {x, y},DegreeLexicographic)", //
        "{x^2*y^2,x^3}");
    check("MonomialList((x + 1)^5, x, Modulus -> 2)", //
        "{x^5,x^4,x,1}");

    check(
        "MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z})", //
        "{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-10*x^2*y*z^5,-7*x*y^5*z^4,6*x*y^4*z^3,6*x*y^3*z^\n"
            + "3,3*x*y^2*z,y^4*z,-7*y^2*z,2*z^5}");

    check(
        "MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeLexicographic)", //
        "{2*z^5,-7*y^2*z,y^4*z,3*x*y^2*z,6*x*y^3*z^3,6*x*y^4*z^3,-7*x*y^5*z^4,-10*x^2*y*z^\n"
            + "5,7*x^2*y^5*z^3,-10*x^5*y^4*z^2}");
    check(
        "MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, DegreeLexicographic)", //
        "{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-7*x*y^5*z^4,-10*x^2*y*z^5,6*x*y^4*z^3,6*x*y^3*z^\n"
            + "3,y^4*z,2*z^5,3*x*y^2*z,-7*y^2*z}");
    check(
        "MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y *z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeDegreeReverseLexicographic)", //
        "{-7*y^2*z,3*x*y^2*z,y^4*z,2*z^5,6*x*y^3*z^3,6*x*y^4*z^3,-10*x^2*y*z^5,7*x^2*y^5*z^\n"
            + "3,-7*x*y^5*z^4,-10*x^5*y^4*z^2}");
    check(
        "MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, DegreeReverseLexicographic)", //
        "{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-7*x*y^5*z^4,6*x*y^4*z^3,-10*x^2*y*z^5,6*x*y^3*z^\n"
            + "3,y^4*z,2*z^5,3*x*y^2*z,-7*y^2*z}");
    check(
        "MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeDegreeLexicographic)", //
        "{-7*y^2*z,3*x*y^2*z,y^4*z,2*z^5,6*x*y^3*z^3,-10*x^2*y*z^5,6*x*y^4*z^3,7*x^2*y^5*z^\n"
            + "3,-7*x*y^5*z^4,-10*x^5*y^4*z^2}");
  }

  @Test
  public void testMost() {
    // TODO
    check("Most(SparseArray({{1,2},{3,4}}))", //
        "Most(SparseArray(Number of elements: 4 Dimensions: {2,2} Default value: 0))");

    check("Most(<|1 :> a, 2 -> b, 3 :> c|>)", //
        "<|1:>a,2->b|>");
    check("Most({a})", //
        "{}");
    check("Most({a, b, c})", //
        "{a,b}");
    check("Most(a + b + c)", //
        "a+b");
    check("Most(Plus(d))", //
        "Most(d)");
    check("Most(a)", //
        "Most(a)");
  }



  @Test
  public void testMultiplicativeOrder() {
    // https://oeis.org/A023394
    check("Select(Prime(Range(500)), IntegerQ(Log(2, MultiplicativeOrder(2, # )))&) ", //
        "{3,5,17,257,641}");

    check("Select(Range(2,200), MultiplicativeOrder(10, # )== # - 1 &)", //
        "{7,17,19,23,29,47,59,61,97,109,113,131,149,167,179,181,193}");
    check("MultiplicativeOrder(-1,0)", //
        "MultiplicativeOrder(-1,0)");
    check("MultiplicativeOrder(7, 108)", //
        "18");
    check("MultiplicativeOrder(10^100 + 1, Prime(1000))", //
        "3959");
    check("MultiplicativeOrder(-5, 7)", //
        "3");
    check("Select(Range(43), MultiplicativeOrder(#, 43) == EulerPhi(43) &)", //
        "{3,5,12,18,19,20,26,28,29,30,33,34}");
  }

  @Test
  public void testNApfloat() {
    ApfloatContext ac = ApfloatContext.getContext();
    // messages
    // General: Overflow occurred in computation.
    // General: Overflow occurred in computation.
    // General: Overflow occurred in computation.
    // General: Overflow occurred in computation.
    // ApfloatContext MaxMemoryBlockSize: 786432
    check("IntegerPart(N(3, 1000)^N(3, 3000)^N(3, 3000)^N(3, 3000)^N(3, 3000));", //
        "");
    ac = ApfloatContext.getContext();

    // System.out.println("ApfloatContext MaxMemoryBlockSize: " + ac.getMaxMemoryBlockSize());
  }

  @Test
  public void testApfloatStorage() {
    try {
      Apfloat apfloat = new Apfloat("9".repeat(1_000_000) + "." + "9".repeat(1_000_000));
      apfloat = ApfloatMath.pow(apfloat, new Apfloat("91212312" + ".1231236"));
      ApfloatNum apfloatNum = ApfloatNum.valueOf(apfloat);
      IInteger integerPart = apfloatNum.integerPart();
      System.out.println(integerPart.bitLength());
      fail("Should be fail");
    } catch (OverflowException e) {
      // e.printStackTrace();
    }
  }

  @Test
  public void testDeterminePrecision() {
    ApfloatNum zero = ApfloatNum.valueOf(new Apfloat(new BigDecimal(BigInteger.ZERO), 30));
    ApfloatNum num = ApfloatNum.valueOf("1.7", 30);

    IASTMutable times = F.Times(num, zero);
    assertEquals(times.determinePrecision(true), 30);
  }

  @Test
  public void testN() {
    check("N((-1)^(1/180), 50)", //
        "0.99984769515639123915701155881391485169274031058318+I*0.01745240643728351281941897851631619247225272030713");

    // issue #942
    check("Tan(Pi/2) // N", //
        "ComplexInfinity");
    // issue #937
    check("(x==-157079632679/100000000000) // N", //
        "x==-1.5708");
    check("ConditionalExpression(x==-157079632679/100000000000*C(1),C(1)∈Integers) // N", //
        "ConditionalExpression(x==-1.5708*C(1),C(1)∈Integers)");
    check("f(1/3)//N", //
        "f(0.333333)");
    check("f(1/2*x)//N", //
        "f(0.5*x)");
    check("N({Labeled(4/3,\"test\")})", //
        "{Labeled(1.33333,test)}");
    check("N(Labeled(4/3,\"test\"))", //
        "Labeled(1.33333,test)");
    check("N(1/7,2)", //
        "0.14");
    check("N(1/7, 3)", //
        "0.143");
    check("N(8 + 1*^28, 40) // InputForm", //
        "1.0000000000000000000000000008`40*^28");
    check("N(8 + 1*^28, 40)", //
        "10000000000000000000000000008");
    // 1.0000000000000000000000000008`40.*^28
    check("N(8 + 1*^-28, 40)", //
        "8.0000000000000000000000000001");
    // github #207
    check("N(((2)/(3))*(4-3*Sqrt(2)), 100)", //
        "-0.1617604580795234309367107817527294904726770840872294796866928093147982902575474110341084019886164788");
    check("N(Sqrt(2)^(-1), 100)", //
        "0.7071067811865475244008443621048490392848359376884740365883398689953662392310535194251937671638207863");
    check("N(Sin(2)^(-1), 100)", //
        "1.099750170294616466756697397026312896658764443149845708742554443062569126995445980876791442481219894");

    check("N(Sqrt(2)/2,2147483647)", //
        "N(Sqrt(2)/2,2147483647)");
    check("N(Null)", //
        "");
    // test precision 30
    check("2/3 + Pi + 5.5`30", //
        "9.30825932025645990512931004994");
    check("2/3 + Pi + 5.5``30", //
        "9.30825932025645990512931004994");

    // imaginary part is zero
    check("I^I //N", //
        "0.20788");
    check("I^(3*I)//N", //
        "0.00898329");
    check("I^(2+3*I)//N", //
        "-0.00898329");
    // TODO don't switch to numeric mode for Sqrt(10)
    check("(0.25)^x", //
        "0.25^x");

    // github #151
    check("N(Integrate(Sin(x*Pi/3), {x, 1, 2}))", //
        "0.95493");
    check("expr=Integrate(Sin(x*Pi/3), {x, 1, 2}); N(expr)", //
        "0.95493");

    // TODO don't switch to numeric mode for Sqrt(10)
    check("Sqrt(10)*(0.25)^x", //
        "0.25^x*Sqrt(10)");

    check("{1, 2} /. x_Integer -> N(x)", //
        "{1,2}");
    check("{1, 2} /. x_Integer :> N(x)", //
        "{1.0,2.0}");
    // check("N(Pi)", "3.141592653589793");
    // check("N(Pi, 50)", "3.1415926535897932384626433832795028841971693993751");
    // check("N(1/7)", "0.14285714285714285");
    // check("N(1/7, 20)", "1.4285714285714285714e-1");
  }

  @Test
  public void testNIssue1065() {
    check("m = {{-2,-2,4},{-1,-3,7},{2,4,6}};", //
        "");
    check("s= Transpose(Eigenvectors(m))", //
        "{{1/38*(-29+5*Sqrt(61)),1/38*(-29-5*Sqrt(61)),-2},{1/38*(-33+7*Sqrt(61)),1/38*(-\n"
            + "33-7*Sqrt(61)),1},{1,1,0}}");
    check("j = {{1+Sqrt(61),0,0},{0,1-Sqrt(61),0},{0,0,-1}};", //
        "");
    check("s1 = Inverse(s);", //
        "");
    check("s.j.s1", //
        "{{-2,-2,4},\n" //
            + " {-1,-3,7},\n" //
            + " {2,4,6}}");
    check(
        "(529944115939767556068257065958983640547328-67852391143724744330116623333929624535040*Sqrt(61))/((203833064-26073320*Sqrt(61))*(-650282536985455220412264441675776+83260147110523554988992408780800*Sqrt(61))) // Together", //
        "-2");
    try {
      Config.USE_EXTENDED_PRECISION_IN_N = true;
      check("-650282536985455220412264441675776 + 83260147110523554988992408780800 * Sqrt(61) // N", //
          "4.51742*10^20");
      // correct result by using apfloat precision evaluation in N()
      check("s.j.s1 // N", //
          "{{-2.0,-2.0,4.0},{-1.0,-3.0,7.0},{2.0,4.0,6.0}}");
    } finally {
      Config.USE_EXTENDED_PRECISION_IN_N = false;
    }

    check("-650282536985455220412264441675776 + 83260147110523554988992408780800 * Sqrt(61) // N", //
        "4.51657*10^20");
    // returns wrong result because of precision loss:
    check("s.j.s1 // N", //
        "{{-2.0,-2.0,4.0},{-1.0,-3.0,7.0},{2.0,4.0,6.0}}");

  }

  @Test
  public void testNameQ() {
    check("NameQ(\"foo\")", //
        "False");
    check("NameQ(\"plot\")", //
        "True");
    check("NameQ(\"ArcTan\")", //
        "True");
    check("NameQ(\"ArcTan2\")", //
        "False");
  }

  @Test
  public void testNames() {
    check("foo`f = 42", //
        "42");
    check("Names(\"foo`*\")", //
        "{foo`f}");
    check("sysnames = Names(\"System`*\");", //
        "");
    check("Select(sysnames, MemberQ(Attributes(#), OneIdentity) &) // InputForm", //
        "{\"And\",\"Composition\",\"Dot\",\"GCD\",\"Intersection\",\"Join\",\"KroneckerProduct\",\"Max\",\"Min\",\"Or\",\"Plus\",\"Power\",\"RightComposition\",\"StringExpression\",\"StringJoin\",\"TensorProduct\",\"Times\",\"Union\",\"Xor\"}");

    check("Names(\"System`\" ~~ _ ~~ _) // InputForm", //
        "{\"Do\",\"Dt\",\"If\",\"Im\",\"In\",\"ND\",\"On\",\"Or\",\"Pi\",\"Re\",\"Tr\"}");
    check("Names(RegularExpression(\"System`...\")) // InputForm", //
        "{\"Abs\",\"All\",\"And\",\"Arg\",\"CDF\",\"Cos\",\"Cot\",\"Csc\",\"Det\",\"Div\",\"Dot\",\"End\",\"Erf\",\"Exp\"," //
            + "\"Fit\",\"For\",\"GCD\",\"Get\",\"Hue\",\"Key\",\"LCM\",\"Log\",\"Map\",\"Max\",\"Min\",\"Mod\",\"Nor\",\"Not\"," //
            + "\"Now\",\"Off\",\"Out\",\"PDF\",\"Put\",\"Red\",\"Row\",\"Sec\",\"Set\",\"Sin\",\"Sow\",\"Sum\",\"Tan\",\"Top\"," //
            + "\"Xor\"}");

    check("Names(\"Int*\" )", //
        "{Integer,IntegerDigits,IntegerExponent,IntegerLength,IntegerName,IntegerPart,IntegerPartitions,IntegerQ,Integers,Integrate,InterpolatingFunction,InterpolatingPolynomial,Interpolation,InterpolationOrder,InterquartileRange,Interrupt,IntersectingQ,Intersection,Interval,IntervalComplement,IntervalData,IntervalIntersection,IntervalMemberQ,IntervalUnion}");
    check("Names(\"Integer*\" )", //
        "{Integer,IntegerDigits,IntegerExponent,IntegerLength,IntegerName,IntegerPart,IntegerPartitions,IntegerQ,Integers}");
    check("Names(\"IntegerPart\" )", //
        "{IntegerPart}");
  }

  @Test
  public void testNand() {
    check("Nand( )", //
        "False");
    check("Nand(a)", //
        "!a");
    check("Nand(2+2)", //
        "!4");
    check("Nand(x,y,z)", //
        "Nand(x,y,z)");
    check("Nand(x,True,z)", //
        "Nand(x,z)");
    check("Nand(x,False,z)", //
        "True");
    check("Nand(True,False)", //
        "True");
    check("Nand(False, True)", //
        "True");
    check("Nand(Print(1); False, Print(2); True)", //
        "True");
    check("Nand(Print(1); True, Print(2); True)", //
        "False");
  }

  @Test
  public void testND() {
    check("ND(Exp(x), x, 1)", //
        "2.71828");
    check("ND(Cos(x)^3, {x,2}, 0)", //
        "-3.0");
    check("ND(Cos(x)^3, {x,2}, 1)", //
        "1.82226");
    check("ND(BesselY(10.0,x), x, 1)", //
        "1.2094*10^9");
  }

  @Test
  public void testNDSolve() {

    // check("model = NDSolve({x'(t) == -y(t) - x(t)^2, y'(t) == 2*x(t) - y(t)^3, x(0) == y(0) ==
    // 1}, {x, y}, {t,
    // 20});", //
    // "");
    // check("ListPlot(Table({Evaluate(x(t) /.model),Evaluate(y(t) /.model)}, {t, 20}) )", //
    // "");

    check("model=NDSolve({ y(x)*Cos(x + y(x))== (y'(x)), y(0)==1}, y, {x, 0, 30});", //
        "");
    // check("Evaluate(y(t) /.model)", //
    // "");
    // check("ListPlot(Table({t,Evaluate(y(t) /.model)}, {t, 0, 30}) )", //
    // "");

    // checkNumeric("NDSolve({ y(x)*Cos(x + y(x))== (y'(x)), y(0)==1}, y, {x, 0, 30})", //
    // "InterpolatingFunction(\n"
    // + "{{0.0,1.0486539247435627},\n" + " {0.1,1.0854808036771377},\n" + "
    // {0.2,1.1096485558561129},\n"
    // + " {0.30000000000000004,1.1212578599391958},\n" + " {0.4,1.1211098516670583},\n"
    // + " {0.5,1.110440971080707},\n" + " {0.6,1.0906930188004873},\n" + "
    // {0.7,1.0633460402333474},\n"
    // + " {0.7999999999999999,1.0298136072918775},\n" + "
    // {0.8999999999999999,0.991387315006244},\n"
    // + " {0.9999999999999999,0.9492149070968016},\n" + "
    // {1.0999999999999999,0.9042988579035411},\n"
    // + " {1.2,0.857505923891753},\n" + " {1.3,0.8095815052617051},\n"
    // + " {1.4000000000000001,0.7611651420556339},\n" + "
    // {1.5000000000000002,0.7128051392128717},\n"
    // + " {1.6000000000000003,0.6649713621384138},\n" + "
    // {1.7000000000000004,0.6180658664467911},\n"
    // + " {1.8000000000000005,0.5724313779772732},\n" + "
    // {1.9000000000000006,0.5283578293741383},\n"
    // + " {2.0000000000000004,0.48608725600029157},\n" + "
    // {2.1000000000000005,0.4458173974209415},\n"
    // + " {2.2000000000000006,0.4077043633624414},\n" + "
    // {2.3000000000000007,0.37186471548866024},\n"
    // + " {2.400000000000001,0.3383772924103512},\n" + " {2.500000000000001,0.3072850658079258},\n"
    // + " {2.600000000000001,0.2785972606942865},\n" + " {2.700000000000001,0.2522919043406724},\n"
    // + " {2.800000000000001,0.22831889029854097},\n" + "
    // {2.9000000000000012,0.20660356283726058},\n"
    // + " {3.0000000000000013,0.18705075124931517},\n" + "
    // {3.1000000000000014,0.1695491213425783},\n"
    // + " {3.2000000000000015,0.15397566999707005},\n" + "
    // {3.3000000000000016,0.14020017181526293},\n"
    // + " {3.4000000000000017,0.1280893946838693},\n" + "
    // {3.5000000000000018,0.11751092979804098},\n"
    // + " {3.600000000000002,0.10833652492796243},\n" + "
    // {3.700000000000002,0.10044485973079281},\n"
    // + " {3.800000000000002,0.09372375133539376},\n" + "
    // {3.900000000000002,0.08807182139220295},\n"
    // + " {4.000000000000002,0.0833996886046539},\n" + "
    // {4.100000000000001,0.07963077199182245},\n"
    // + " {4.200000000000001,0.07670180014927495},\n" + "
    // {4.300000000000001,0.07456312212288695},\n"
    // + " {4.4,0.07317890821085377},\n" + " {4.5,0.07252731596246077},\n" + "
    // {4.6,0.0726006791932994},\n"
    // + " {4.699999999999999,0.0734057565099308},\n" + "
    // {4.799999999999999,0.07496405020983263},\n"
    // + " {4.899999999999999,0.07731217510263196},\n" + "
    // {4.999999999999998,0.08050221749315989},\n"
    // + " {5.099999999999998,0.084601974272093},\n" + "
    // {5.1999999999999975,0.08969489746733304},\n"
    // + " {5.299999999999997,0.0958794879078716},\n" + "
    // {5.399999999999997,0.10326778201284252},\n"
    // + " {5.4999999999999964,0.11198246184539966},\n" + "
    // {5.599999999999996,0.12215200271817592},\n"
    // + " {5.699999999999996,0.13390318126361409},\n" + "
    // {5.799999999999995,0.14735024731166288},\n"
    // + " {5.899999999999995,0.16258018854901138},\n" + "
    // {5.999999999999995,0.17963388536831357},\n"
    // + " {6.099999999999994,0.19848366765525352},\n" + "
    // {6.199999999999994,0.21900890659080618},\n"
    // + " {6.299999999999994,0.24097273961477084},\n" + "
    // {6.399999999999993,0.2640045482014827},\n"
    // + " {6.499999999999993,0.2875938184944301},\n" + " {6.5999999999999925,0.311100756322707},\n"
    // + " {6.699999999999992,0.33378687199758955},\n" + "
    // {6.799999999999992,0.35486468555987505},\n"
    // + " {6.8999999999999915,0.37356070170896716},\n" + "
    // {6.999999999999991,0.38918166156437567},\n"
    // + " {7.099999999999991,0.401172598399326},\n" + " {7.19999999999999,0.4091571693969712},\n"
    // + " {7.29999999999999,0.4129553215557175},\n" + " {7.39999999999999,0.41257870289475035},\n"
    // + " {7.499999999999989,0.4082084528387488},\n" + "
    // {7.599999999999989,0.40016206914955754},\n"
    // + " {7.699999999999989,0.38885599961989714},\n" + "
    // {7.799999999999988,0.37476918269614945},\n"
    // + " {7.899999999999988,0.3584108421644447},\n" + "
    // {7.999999999999988,0.34029407839355436},\n"
    // + " {8.099999999999987,0.3209155086689302},\n" + "
    // {8.199999999999987,0.30074044115694204},\n"
    // + " {8.299999999999986,0.2801927248235081},\n" + " {8.399999999999986,0.2596483599261049},\n"
    // + " {8.499999999999986,0.23943205150206048},\n" + "
    // {8.599999999999985,0.21981604663931104},\n"
    // + " {8.699999999999985,0.20102075567302183},\n" + "
    // {8.799999999999985,0.18321678667386834},\n"
    // + " {8.899999999999984,0.1665281105852},\n" + " {8.999999999999984,0.15103612290345214},\n"
    // + " {9.099999999999984,0.136784386073114},\n" + " {9.199999999999983,0.12378383728408018},\n"
    // + " {9.299999999999983,0.11201824175272579},\n" + "
    // {9.399999999999983,0.10144967223258593},\n"
    // + " {9.499999999999982,0.09202380805440191},\n" + "
    // {9.599999999999982,0.08367487371122105},\n"
    // + " {9.699999999999982,0.07633007619745596},\n" + "
    // {9.799999999999981,0.06991344748031084},\n"
    // + " {9.89999999999998,0.06434904783208746},\n" + " {9.99999999999998,0.05956353167221671},\n"
    // + " {10.09999999999998,0.055488115733649444},\n" + "
    // {10.19999999999998,0.052060017293655024},\n"
    // + " {10.29999999999998,0.0492234472683428},\n" + "
    // {10.399999999999979,0.04693025003153322},\n"
    // + " {10.499999999999979,0.04514028068683145},\n" + "
    // {10.599999999999978,0.043821603367067995},\n"
    // + " {10.699999999999978,0.042950583025463014},\n" + "
    // {10.799999999999978,0.04251192976987961},\n"
    // + " {10.899999999999977,0.04249874014945849},\n" + "
    // {10.999999999999977,0.042912564384982844},\n"
    // + " {11.099999999999977,0.043763512208115654},\n" + "
    // {11.199999999999976,0.045070392103282626},\n"
    // + " {11.299999999999976,0.046860858299487156},\n" + "
    // {11.399999999999975,0.049171515562746765},\n"
    // + " {11.499999999999975,0.052047902387254485},\n" + "
    // {11.599999999999975,0.05554423757203267},\n"
    // + " {11.699999999999974,0.059722773229538936},\n" + "
    // {11.799999999999974,0.0646525504791017},\n"
    // + " {11.899999999999974,0.07040730672504374},\n" + "
    // {11.999999999999973,0.0770622441521745},\n"
    // + " {12.099999999999973,0.0846893525976165},\n" + "
    // {12.199999999999973,0.09335100838707118},\n"
    // + " {12.299999999999972,0.10309167339440412},\n" + "
    // {12.399999999999972,0.11392772849437467},\n"
    // + " {12.499999999999972,0.1258358199938911},\n" + "
    // {12.599999999999971,0.138740580936534},\n"
    // + " {12.69999999999997,0.15250316894309834},\n" + "
    // {12.79999999999997,0.16691262572026747},\n"
    // + " {12.89999999999997,0.18168242049828567},\n" + "
    // {12.99999999999997,0.19645445463107747},\n"
    // + " {13.09999999999997,0.2108120803325641},\n" + "
    // {13.199999999999969,0.22430228748955305},\n"
    // + " {13.299999999999969,0.23646536721975198},\n" + "
    // {13.399999999999968,0.24686856012190703},\n"
    // + " {13.499999999999968,0.25513903798118154},\n" + "
    // {13.599999999999968,0.26099149103034053},\n"
    // + " {13.699999999999967,0.26424665962609567},\n" + "
    // {13.799999999999967,0.26483900018855316},\n"
    // + " {13.899999999999967,0.26281370586093794},\n" + "
    // {13.999999999999966,0.2583149394045044},\n"
    // + " {14.099999999999966,0.2515680519994532},\n" + "
    // {14.199999999999966,0.24285873137419975},\n"
    // + " {14.299999999999965,0.2325116382150581},\n" + "
    // {14.399999999999965,0.220870422513877},\n"
    // + " {14.499999999999964,0.20828029902084735},\n" + "
    // {14.599999999999964,0.19507375354460899},\n"
    // + " {14.699999999999964,0.1815595106786453},\n" + "
    // {14.799999999999963,0.16801461631765874},\n"
    // + " {14.899999999999963,0.1546793400273539},\n" + "
    // {14.999999999999963,0.14175453929296386},\n"
    // + " {15.099999999999962,0.12940111187045478},\n" + "
    // {15.199999999999962,0.11774116741799541},\n"
    // + " {15.299999999999962,0.10686056081330114},\n" + "
    // {15.399999999999961,0.09681244213729613},\n"
    // + " {15.499999999999961,0.08762149329437179},\n" + "
    // {15.59999999999996,0.07928854218346183},\n"
    // + " {15.69999999999996,0.07179527544171443},\n" + "
    // {15.79999999999996,0.06510881121842255},\n"
    // + " {15.89999999999996,0.05918594276300758},\n" + "
    // {15.99999999999996,0.053976918199585305},\n"
    // + " {16.09999999999996,0.049428676909066306},\n" + "
    // {16.19999999999996,0.04548751365173981},\n"
    // + " {16.29999999999996,0.042101184130222284},\n" + "
    // {16.399999999999963,0.03922049785649547},\n"
    // + " {16.499999999999964,0.03680046534374525},\n" + "
    // {16.599999999999966,0.03480107758305633},\n"
    // + " {16.699999999999967,0.0331877982037784},\n" + "
    // {16.79999999999997,0.03193184479148035},\n"
    // + " {16.89999999999997,0.03101032767125873},\n" + "
    // {16.99999999999997,0.030406303862182932},\n"
    // + " {17.099999999999973,0.03010879219599716},\n" + "
    // {17.199999999999974,0.030112783566147657},\n"
    // + " {17.299999999999976,0.030419268228300628},\n" + "
    // {17.399999999999977,0.03103528987978064},\n"
    // + " {17.49999999999998,0.031974023436000365},\n" + "
    // {17.59999999999998,0.03325485929623045},\n"
    // + " {17.69999999999998,0.034903460631464545},\n" + "
    // {17.799999999999983,0.036951741050493675},\n"
    // + " {17.899999999999984,0.03943768737279855},\n" + "
    // {17.999999999999986,0.042404926190700305},\n"
    // + " {18.099999999999987,0.04590190449334592},\n" + "
    // {18.19999999999999,0.049980526549024254},\n"
    // + " {18.29999999999999,0.054694066601254296},\n" + "
    // {18.39999999999999,0.06009416801910799},\n"
    // + " {18.499999999999993,0.0662267563624869},\n" + "
    // {18.599999999999994,0.07312675184871771},\n"
    // + " {18.699999999999996,0.08081158301761861},\n" + "
    // {18.799999999999997,0.0892736922498442},\n"
    // + " {18.9,0.09847248889531121},\n" + " {19.0,0.10832652953813567},\n" + "
    // {19.1,0.11870703751609235},\n"
    // + " {19.200000000000003,0.12943412780911714},\n" + "
    // {19.300000000000004,0.1402771628385532},\n"
    // + " {19.400000000000006,0.15096041573160082},\n" + "
    // {19.500000000000007,0.16117459959331792},\n"
    // + " {19.60000000000001,0.1705938815306118},\n" + "
    // {19.70000000000001,0.17889692028539034},\n"
    // + " {19.80000000000001,0.18578953051456235},\n" + "
    // {19.900000000000013,0.19102607653255335},\n"
    // + " {20.000000000000014,0.19442681148735713},\n" + "
    // {20.100000000000016,0.19588908551204787},\n"
    // + " {20.200000000000017,0.19539143760805033},\n" + "
    // {20.30000000000002,0.19299074958941248},\n"
    // + " {20.40000000000002,0.188813589734067},\n" + " {20.50000000000002,0.18304344050912236},\n"
    // + " {20.600000000000023,0.1759056646182722},\n" + "
    // {20.700000000000024,0.1676519017849422},\n"
    // + " {20.800000000000026,0.15854523818283722},\n" + "
    // {20.900000000000027,0.14884707766919583},\n"
    // + " {21.00000000000003,0.13880625822789994},\n" + "
    // {21.10000000000003,0.12865064403209947},\n"
    // + " {21.20000000000003,0.11858119433695793},\n" + "
    // {21.300000000000033,0.10876835547392645},\n"
    // + " {21.400000000000034,0.09935052412493195},\n" + "
    // {21.500000000000036,0.09043427196672443},\n"
    // + " {21.600000000000037,0.08209599140343246},\n" + "
    // {21.70000000000004,0.07438461231024102},\n"
    // + " {21.80000000000004,0.06732504714496039},\n" + "
    // {21.90000000000004,0.0609220448460617},\n"
    // + " {22.000000000000043,0.05516417096178315},\n" + "
    // {22.100000000000044,0.05002767965231217},\n"
    // + " {22.200000000000045,0.045480098453626217},\n" + "
    // {22.300000000000047,0.04148340402665843},\n"
    // + " {22.40000000000005,0.03799672158222671},\n" + "
    // {22.50000000000005,0.03497852815326329},\n"
    // + " {22.60000000000005,0.032388377671356924},\n" + "
    // {22.700000000000053,0.030188192781695093},\n"
    // + " {22.800000000000054,0.02834318482532842},\n" + "
    // {22.900000000000055,0.026822470808221865},\n"
    // + " {23.000000000000057,0.025599456420398434},\n" + "
    // {23.10000000000006,0.02465204936600566},\n"
    // + " {23.20000000000006,0.023962759329861603},\n" + "
    // {23.30000000000006,0.023518731365109482},\n"
    // + " {23.400000000000063,0.023311749398506023},\n" + "
    // {23.500000000000064,0.02333823650818959},\n"
    // + " {23.600000000000065,0.02359926881772662},\n" + "
    // {23.700000000000067,0.024100610123988526},\n"
    // + " {23.800000000000068,0.02485276434553134},\n" + "
    // {23.90000000000007,0.025871031998059613},\n"
    // + " {24.00000000000007,0.027175544570715412},\n" + "
    // {24.100000000000072,0.02879123634710425},\n"
    // + " {24.200000000000074,0.030747696564390598},\n" + "
    // {24.300000000000075,0.03307882595701917},\n"
    // + " {24.400000000000077,0.035822201576675955},\n" + "
    // {24.500000000000078,0.03901803437743281},\n"
    // + " {24.60000000000008,0.04270758911304822},\n" + "
    // {24.70000000000008,0.046930931420902475},\n"
    // + " {24.800000000000082,0.051723880711205865},\n" + "
    // {24.900000000000084,0.05711408982254241},\n"
    // + " {25.000000000000085,0.06311625424637678},\n" + "
    // {25.100000000000087,0.06972658382886275},\n"
    // + " {25.200000000000088,0.07691685011526753},\n" + "
    // {25.30000000000009,0.0846285415836176},\n"
    // + " {25.40000000000009,0.09276788599437955},\n" + "
    // {25.500000000000092,0.10120268026019423},\n"
    // + " {25.600000000000094,0.10976193177974276},\n" + "
    // {25.700000000000095,0.11823918766143336},\n"
    // + " {25.800000000000097,0.12640006310087093},\n" + "
    // {25.900000000000098,0.13399389013035107},\n"
    // + " {26.0000000000001,0.14076868583650626},\n" + " {26.1000000000001,0.14648794906949023},\n"
    // + " {26.200000000000102,0.1509473266388399},\n" + "
    // {26.300000000000104,0.15398909090144997},\n"
    // + " {26.400000000000105,0.15551268333713533},\n" + "
    // {26.500000000000107,0.15548022163602201},\n"
    // + " {26.600000000000108,0.15391666739307647},\n" + "
    // {26.70000000000011,0.15090510975131846},\n"
    // + " {26.80000000000011,0.14657818306953294},\n" + "
    // {26.900000000000112,0.14110693216447398},\n"
    // + " {27.000000000000114,0.13468847749049695},\n" + "
    // {27.100000000000115,0.1275336799610958},\n"
    // + " {27.200000000000117,0.11985574493622606},\n" + "
    // {27.300000000000118,0.11186041177815408},\n"
    // + " {27.40000000000012,0.1037381005484644},\n" + "
    // {27.50000000000012,0.09565815735831991},\n"
    // + " {27.600000000000122,0.08776516222812088},\n" + "
    // {27.700000000000124,0.08017713485435946},\n"
    // + " {27.800000000000125,0.07298538709313364},\n" + "
    // {27.900000000000126,0.06625571890495106},\n"
    // + " {28.000000000000128,0.06003063117527128},\n" + "
    // {28.10000000000013,0.0543322298578955},\n"
    // + " {28.20000000000013,0.04916551743387415},\n" + "
    // {28.300000000000132,0.04452180553916086},\n"
    // + " {28.400000000000134,0.04038203184386874},\n" + "
    // {28.500000000000135,0.03671981933966998},\n"
    // + " {28.600000000000136,0.03350417158494679},\n" + "
    // {28.700000000000138,0.03070174835662014},\n"
    // + " {28.80000000000014,0.02827870903992453},\n" + "
    // {28.90000000000014,0.026202144002981702},\n"
    // + " {29.000000000000142,0.02444113673212715},\n" + "
    // {29.100000000000144,0.0229675124577755},\n"
    // + " {29.200000000000145,0.02175633398892615},\n" + "
    // {29.300000000000146,0.02078620446123},\n"
    // + " {29.400000000000148,0.02003943163828367},\n" + "
    // {29.50000000000015,0.019502100971413434},\n"
    // + " {29.60000000000015,0.019164096103692605},\n" + "
    // {29.700000000000152,0.01901909674905609},\n"
    // + " {29.800000000000153,0.019064575332356216},\n" + "
    // {29.900000000000155,0.019301805546317868}})");

    // 10, 28, 8/3 as constants for the Lorenz equations
    // https://socialinnovationsimulation.com/2013/07/19/tutorial-differential-equations-2/
    check(
        "model=NDSolve({x'(t) == 10*(y(t) - x(t)), \n"
            + " y'(t) == x(t)*(28 - z(t)) - y(t), z'(t) == x(t)*y(t) - 8/3*z(t),\n"
            + " x(0)== 0, y(0) == 1, z(0) == 0}, {x, y, z}, {t, 0, 20});", //
        "");
  }

  @Test
  public void testNegative() {
    check("Negative(Infinity)", //
        "False");
    check("Negative(-Infinity)", //
        "True");
    check("Negative(-9/4)", //
        "True");
    check("Negative(0.1+I)", //
        "False");
    check("Negative(0)", //
        "False");
    check("Negative(-3)", //
        "True");
    check("Negative(10/7)", //
        "False");
    check("Negative(1+2*I)", //
        "False");
    check("Negative(a + b)", //
        "Negative(a+b)");
    check("Negative(-E)", //
        "True");
    check("Negative(Sin({11, 14}))", //
        "{True,False}");
  }

  @Test
  public void testNearest() {
    check("Nearest({1, 2, 3, 5, 7, 11, 13, 17, 19}, 9)", //
        "{7,11}");
    check("Nearest({{1.5, .6}, {2, 0}, {1.25, 1.25}}, {0, 0}, " //
        + "DistanceFunction -> (3*Abs(#1[[1]]-#2[[1]]) + 2*Abs(#1[[2]]-#2[[2]]) &))", //
        "{{1.5,0.6}}");
    check("dist({u_, v_}, {x_, y_}) := 3*Abs(u-x) + 2*Abs(v-y)", //
        "");
    check("Nearest({{1.5, .6}, {2, 0}, {1.25, 1.25}}, {0, 0}, DistanceFunction -> dist)", //
        "{{1.5,0.6}}");
    check(
        "Nearest({{1.5, .6}, {2, 0}, {1.25, 1.25}}, {0, 0}, DistanceFunction -> ManhattanDistance)", //
        "{{2,0}}");
    check(
        "Nearest({{1.5, .6}, {2, 0}, {1.25, 1.25}}, {0, 0}, DistanceFunction -> ChessboardDistance)", //
        "{{1.25,1.25}}");


    check("Nearest({1, 2, 4, 8, 16, 32}, 20)", //
        "{16}");
    check("Nearest({1, 2, 4, 8, 16, 24, 32}, 20)", //
        "{16,24}");
  }

  @Test
  public void testNearestTo() {
    check("NearestTo(9)[{1, 2, 3, 5, 7, 11, 13, 17, 19}]", //
        "{7,11}");
    check(
        "NearestTo({0, 0}, DistanceFunction->ManhattanDistance) [{{1.5, .6}, {2, 0}, {1.25, 1.25}}]", //
        "{{2,0}}");
    check(
        "NearestTo({0, 0}, DistanceFunction->ChessboardDistance) [{{1.5, .6}, {2, 0}, {1.25, 1.25}}]", //
        "{{1.25,1.25}}");
    check("NearestTo({0, 0}) [{{1.5, .6}, {2, 0}, {1.25, 1.25}}]", //
        "{{1.5,0.6}}");

    check("NearestTo(20)[{1, 2, 4, 8, 16, 32}]", //
        "{16}");
    // TODO improve Nearest
    check("NearestTo(20,3)[{1, 2, 4, 8, 16, 32}]", //
        "Nearest({1,2,4,8,16,32},20,3,DistanceFunction->Automatic)");
  }

  @Test
  public void testNeeds() {
    check("Needs({-1/2,{{1}},3})", //
        "Needs({-1/2,{{1}},3})");
  }

  @Test
  public void testNest() {
    // iteration limit exceeded
    check("Nest(-I,Null,2147483647)", //
        "Hold(Nest(-I,Null,2147483647))");

    check("Nest(f, x, 3)", //
        "f(f(f(x)))");
    check("Nest((1+#) ^ 2 &, x, 2)", //
        "(1+(1+x)^2)^2");

    check("Nest(f, x, 3)", //
        "f(f(f(x)))");
    check("Nest((1 + #)^2 &, 1, 3)", //
        "676");
    check("Nest((1 + #)^2 &, x, 5)", //
        "(1+(1+(1+(1+(1+x)^2)^2)^2)^2)^2");
    check("Nest(Sqrt, 100.0, 4)", //
        "1.33352");
  }

  @Test
  public void testNestList() {
    check("NestList(4*#*(1 - #) &, 1/3, 5)", //
        "{1/3,8/9,32/81,6272/6561,7250432/43046721,1038154236987392/1853020188851841}");
    check("Length(NestList(#2,{},50))", //
        "51");
    check("NestList(#2,{},3)", //
        "{{},#2[{}],#2[#2[{}]],#2[#2[#2[{}]]]}");
    check("NestList(f, x, 4)", //
        "{x,f(x),f(f(x)),f(f(f(x))),f(f(f(f(x))))}");
    check("NestList(2*# &, 1, 8)", //
        "{1,2,4,8,16,32,64,128,256}");
    check("NestList(Cos, 1.0, 10)", //
        "{1.0,0.540302,0.857553,0.65429,0.79348,0.701369,0.76396,0.722102,0.750418,0.731404,0.744237}");
    check("NestList((1 + #)^2 &, x, 3)", //
        "{x,(1+x)^2,(1+(1+x)^2)^2,(1+(1+(1+x)^2)^2)^2}");
    check("seq=NestList(3/2 (# + Mod(#, 2)) &, 1, 499);Take(seq, 10)", //
        "{1,3,6,9,15,24,36,54,81,123}");
  }

  @Test
  public void testNestWhile() {
    check("NestWhile(#^2 &, 2, # < 256 &)", //
        "256");
    check("NestWhile(#+1 &, 1, # + #4 < 10 &, 4)", //
        "7");
    check("NestWhile(#+1 &, 1, True &, 1, 4)", //
        "5");
    check("NestWhile(#+1 &, 1, True &, 1, 4,5)", //
        "10");
    check("NestWhile(#+1 &, 1, False &, 1, 4,5)", //
        "6");


    check("NestWhile(Floor(#/2) &, 10, (Print({##}); UnsameQ(##)) &, All)", //
        "0");
    check("NestWhile(Floor(#/2) &, 10, UnsameQ, 2)", //
        "0");
    check("NestWhile(x[[1]],-2147483648,EvenQ(#1)&)", //
        "x[[1]][-2147483648]");
    check("NestWhile(#/2&, 10000, IntegerQ)", //
        "625/2");
    check("NestWhile(#/2 &, 123456, EvenQ)", //
        "1929");
    check("NestWhile(Log, 100, # > 0 &)", //
        "Log(Log(Log(Log(100))))");
  }

  @Test
  public void testNestWhileList() {
    check("NestWhileList(Function((#+3/#)/2), 1., Function(# =!= #2), 2)", //
        "{1.0,2.0,1.75,1.73214,1.73205,1.73205,1.73205}");
    // from fuzz testing:
    check("NestWhileList(10007,{ByteArray()},True&,-0.8)", //
        "NestWhileList(10007,{ByteArray()},True&,-0.8)");

    check("NestWhileList(f,g,127,0,5)", //
        "{g}");
    check("NestWhileList(f,g(1,2,3),{Sqrt(2)/2},42,7)", //
        "{g(1,2,3),f(g(1,2,3)),f(f(g(1,2,3))),f(f(f(g(1,2,3)))),f(f(f(f(g(1,2,3))))),f(f(f(f(f(g(\n"
            + "1,2,3)))))),f(f(f(f(f(f(g(1,2,3))))))),f(f(f(f(f(f(f(g(1,2,3))))))))}");
    check("NestWhileList(f,g,False&,9,7)", //
        "{g,f(g),f(f(g)),f(f(f(g))),f(f(f(f(g)))),f(f(f(f(f(g))))),f(f(f(f(f(f(g)))))),f(f(f(f(f(f(f(g)))))))}");
    check("NestWhileList(f,g,False&,8,8)", //
        "{g,f(g),f(f(g)),f(f(f(g))),f(f(f(f(g)))),f(f(f(f(f(g))))),f(f(f(f(f(f(g)))))),f(f(f(f(f(f(f(g)))))))}");
    check("NestWhileList(f,g,False&,7,8)", //
        "{g,f(g),f(f(g)),f(f(f(g))),f(f(f(f(g)))),f(f(f(f(f(g))))),f(f(f(f(f(f(g))))))}");
    check("NestWhileList(#+1 &, 1, # + #4 < 10 &, 4)", //
        "{1,2,3,4,5,6,7}");
    check("NestWhileList(#+1 &, 1, True &, 1, 4, 5)", //
        "{1,2,3,4,5,6,7,8,9,10}");
    check("NestWhileList(#+1 &, 1, False &, 1, 4, 5)", //
        "{1,2,3,4,5,6}");
    check("NestWhileList(Floor(#/2) &, 20, # > 1 &,1,Infinity,1)", //
        "{20,10,5,2,1,0}");
    check("NestWhileList(Floor(#/2) &, 20, # > 1 &,1,Infinity)", //
        "{20,10,5,2,1}");

    check("NestWhileList(Floor(#/2) &, 20, # > 1 &,1,Infinity,-1)", //
        "{20,10,5,2}");

    check("NestWhileList(#+1 &, 1, True &, 1, 4)", //
        "{1,2,3,4,5}");

    check("NestWhileList(#^2 &, 2, # < 256 &)", //
        "{2,4,16,256}");

    // TODO improve error handling
    check("NestWhileList(x[[1]],-2147483648,EvenQ(#1)&)", //
        "{-2147483648,x[[1]][-2147483648]}");

    check("NestWhileList(Floor(#/2) &, 10, (Print({##}); UnsameQ(##)) &, All)", //
        "{10,5,2,1,0,0}");
    check("NestWhileList(Floor(#/2) &, 10, UnsameQ, 2)", //
        "{10,5,2,1,0,0}");
    check("NestWhileList(#/2&, 10000, IntegerQ)", //
        "{10000,5000,2500,1250,625,625/2}");
    check("NestWhileList(#^2 &, 2, # < 256 &)", //
        "{2,4,16,256}");
    check("NestWhileList(#/2 &, 123456, EvenQ)", //
        "{123456,61728,30864,15432,7716,3858,1929}");
    check("NestWhileList(Log, 100, # > 0 &)", //
        "{100,Log(100),Log(Log(100)),Log(Log(Log(100))),Log(Log(Log(Log(100))))}");
  }

  @Test
  public void testNextPrime() {
    // print: iteration limit
    check("NextPrime(13,2147483647)", //
        "Hold(NextPrime(13,2147483647))");

    // print NextPrime: Non-negative integer expected.
    check("NextPrime(-10000)", //
        "NextPrime(-10000)");
    // NextPrime: Positive integer (less equal 2147483647) expected at position 2 in
    // NextPrime(10000,-3).
    check("NextPrime(10000, -3)", //
        "NextPrime(10000,-3)");

    check("NextPrime(10000)", //
        "10007");
    check("NextPrime(10000, 3)", //
        "10037");
    // TODO
    // check("NextPrime(100, -5)", "73");
    // check("NextPrime(10, -5)", "-2");
    // check("NextPrime(5.5, 100)", "563");
  }

  @Test
  public void testNHoldAll() {
    check("N(f(2, 3))", //
        "f(2.0,3.0)");
    check("SetAttributes(f, NHoldAll)", "");
    check("N(f(2, 3))", //
        "f(2,3)");
  }



  @Test
  public void testNonCommutativeMultiply() {
    check("{0 ** a, 1 ** a}", //
        "{0**a,1**a}");
    check("{a*b == b*a, a ** b == b ** a}", //
        "{True,a**b==b**a}");
    check("a ** (b ** c) == (a ** b) ** c", //
        "True");
    check("NonCommutativeMultiply(a)", //
        "NonCommutativeMultiply(a)");
  }

  @Test
  public void testNoneTrue() {
    check("NoneTrue({1, 3, 5}, EvenQ)", //
        "True");
    check("NoneTrue({1, 4, 5}, EvenQ)", //
        "False");
    check("NoneTrue({}, EvenQ)", //
        "True");

    check("NoneTrue({1, 2, 3, 4, 5, 6}, EvenQ)", //
        "False");
    check("NoneTrue({1, 3, 5, 7}, EvenQ)", //
        "True");
    check("NoneTrue({12, 16, x, 14, y}, # < 10 &)", //
        "Nor(x<10,y<10)");
    check("NoneTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", //
        "True");
    check("NoneTrue(f(1, 7, 3), OddQ)", //
        "False");
  }

  @Test
  public void testNonNegative() {
    check("NonNegative(-Infinity)", //
        "False");

    check("NonNegative(Infinity)", //
        "True");
    check("NonNegative(-Infinity)", //
        "False");
    check("NonNegative(-9/4)", //
        "False");
    check("NonNegative(0.1+I)", //
        "False");
    check("NonNegative(I)", //
        "False");
    check("{Positive(0), NonNegative(0)}", //
        "{False,True}");
    check("NonNegative({1.6, 3/4, Pi, 0, -5, 1 + I, Sin(10^5)})", //
        "{True,True,True,True,False,False,True}");
    check("NonNegative({x, Sin(y)})", //
        "{NonNegative(x),NonNegative(Sin(y))}");
  }

  @Test
  public void testNonPositive() {
    check("NonPositive(-9/4)", //
        "True");
    check("NonPositive(-0.1+I)", //
        "False");
    check("NonPositive(I)", //
        "False");
    check("{Negative(0), NonPositive(0)}", //
        "{False,True}");
    check("NonPositive({1.6, 3/4, Pi, 0, -5, 1 + I, Sin(10^5)})", //
        "{False,False,False,True,True,False,False}");
  }

  @Test
  public void testNor() {
    check("Nor( )", //
        "True");
    check("Nor(2+2)", //
        "!4");
    check("Nor(True,False)", //
        "False");
    check("Nor(x,y,z)", //
        "Nor(x,y,z)");
    check("Nor(x,True,z)", //
        "False");
    check("Nor(x,False,z)", //
        "Nor(x,z)");
    check("BooleanConvert(Nor(p, q, r))", //
        "!p&&!q&&!r");
    check("BooleanConvert(! Nor(p, q, r))", //
        "p||q||r");
  }

  @Test
  public void testNorm() {

    // message Power: BigInteger bit length 76493 exceeded
    check("Norm({10,100,200},10007)", //
        "Norm({10,100,200},10007)", //
        60);

    // message: Norm: The first Norm argument should be a scalar, vector or matrix.))
    check("Norm({})", //
        "Norm({})");
    check("Norm({{}})", //
        "Norm({{}})");
    check("mat={{1.1284111012048381, 6.059642563882402, 4.016005969894351},\n" + //
        "{6.953004075736082, 2.0349603837230656, 1.9793505188774905},\n" + //
        "{7.9963143348211325, 0.18947057304877646, 3.1653764788092467}};", //
        "");
    check("Norm(mat)", //
        "11.93914");
    check("Norm(mat,\"Frobenius\")", //
        "13.58383567975505");
    check("Norm({a,b,c}, \"Frobenius\")", //
        "Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)");

    check("Norm(0)", //
        "0");
    check("Norm({x, y}, 0)", //
        "Norm({x,y},0)");
    check("Norm({x, y}, 0.5)", //
        "Norm({x,y},0.5)");
    check("Norm({})", //
        "Norm({})");
    check("Norm({1, 2, 3, 4}, 2)", //
        "Sqrt(30)");
    check("Norm({10, 100, 200}, 1)", //
        "310");
    check("Norm({0,0,a,0,0})", //
        "Abs(a)");
    check("Norm({a,b,c})", //
        "Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)");

    check("Norm(SparseArray({x, y, z}), Infinity)", //
        "Max(Abs(x),Abs(y),Abs(z))");
    check("Norm({x, y, z}, Infinity)", //
        "Max(Abs(x),Abs(y),Abs(z))");
    check("Norm({x, y, z})", //
        "Sqrt(Abs(x)^2+Abs(y)^2+Abs(z)^2)");
    check("Norm({x, y, z}, p)", //
        "(Abs(x)^p+Abs(y)^p+Abs(z)^p)^(1/p)");

    check("Norm(-2+I)", //
        "Sqrt(5)");
    check("Norm(SparseArray({1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1}))", //
        "Sqrt(5)");
    check("Norm({1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1})", //
        "Sqrt(5)");
    check("Norm(N({1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1}))", //
        "2.23607");

    check("Norm({1, 2, 3, 4}, 2)", //
        "Sqrt(30)");
    check("Norm({10, 100, 200}, 1)", //
        "310");
    check("Norm({a, b, c})", //
        "Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)");
    check("Norm({-100, 2, 3, 4}, Infinity)", //
        "100");
    check("Norm(1 + I)", //
        "Sqrt(2)");
    check("Norm({1, {2, 3}}) ", //
        "Norm({1,{2,3}})");
    check("Norm({x, y})", //
        "Sqrt(Abs(x)^2+Abs(y)^2)");
    check("Norm({x, y}, 0)", //
        "Norm({x,y},0)");
    check("Norm({x, y}, 0.5)", //
        "Norm({x,y},0.5)");
    check("Norm({})", //
        "Norm({})");
  }

  @Test
  public void testNormal() {
    check("s=Series(Exp(x), {x, 0, 5})", //
        "1+x+x^2/2+x^3/6+x^4/24+x^5/120+O(x)^6");
    check("Normal(s)", //
        "1+x+x^2/2+x^3/6+x^4/24+x^5/120");
    check("Normal(Pi)", //
        "Pi");
    check("Normal( ConditionalExpression( 1, Element(a,Reals)&&b>0&&n>0 ) + z^3 )", //
        "1+z^3");
    check("Normal( ConditionalExpression( 1, Element(a,Reals)&&b>0&&n>0 ) + z^3, ByteArray)", //
        "ConditionalExpression(1+z^3,a∈Reals&&b>0&&n>0)");
    check(
        "Normal( ConditionalExpression( 1, Element(a,Reals)&&b>0&&n>0 ) + z^3, {ConditionalExpression})", //
        "1+z^3");
  }

  @Test
  public void testNormalize() {
    // message Normalize: The first argument is not a number or a vector, or the second argument is
    // not a norm function that always returns a non-negative real number for any numeric argument.
    check("Normalize({{1, 2}, {4, 5}})", //
        "Normalize({{1,2},{4,5}})");
    // set the name of the norm to get a result
    check("Normalize({{1, 2}, {4, 5}}, Norm)", //
        "{{0.147758,0.295516},{0.591031,0.738789}}");

    check(
        "Normalize({0.9999999999999999, -0.12609662354252538+I*0.3870962681438435, 0.3692814336284687+I*0.0968290630091466})", //
        "{0.8732080940136610352,-0.1101085923051267364+I*0.33801559450568667,0.3224595368133474565+I*0.0845519215553456008}");

    check("Normalize({0})", //
        "{0}");
    check("Normalize(0)", //
        "0");
    check("Normalize({1,5,1})", //
        "{1/(3*Sqrt(3)),5/3*1/Sqrt(3),1/(3*Sqrt(3))}");
    check("Normalize({x,y})", //
        "{x/Sqrt(Abs(x)^2+Abs(y)^2),y/Sqrt(Abs(x)^2+Abs(y)^2)}");
    check("Normalize({x,y}, f)", //
        "{x/f({x,y}),y/f({x,y})}");
    check("Normalize({1, 2*I, 3, 4*I, 5, 6*I})", //
        "{1/Sqrt(91),(I*2)/Sqrt(91),3/Sqrt(91),(I*4)/Sqrt(91),5/Sqrt(91),(I*6)/Sqrt(91)}");
    check("Normalize(N({1, 2*I, 3, 4*I, 5, 6*I}))", //
        "{0.104828,I*0.209657,0.314485,I*0.419314,0.524142,I*0.628971}");

    check("Normalize(1 + x + x^2, Integrate(#^2, {x, -1, 1}) &)", //
        "5/22*(1+x+x^2)");
    check("Normalize(1 + x + x^2)", //
        "Normalize(1+x+x^2)");
    check("Normalize({1, 1, 1, 1})", //
        "{1/2,1/2,1/2,1/2}");
    check("Normalize(1 + I)", //
        "(1+I)/Sqrt(2)");
    check("Normalize(0)", //
        "0");
    check("Normalize({0})", //
        "{0}");
    check("Normalize({})", //
        "{}");
  }

  @Test
  public void testNot() {
    check("!True", //
        "False");
    check("!False", //
        "True");
    check("!b", //
        "!b");
    check("Not(Not(x))", //
        "x");
    check("Not(a<b)", //
        "a>=b");
    check("Not(a<=b)", //
        "a>b");
    check("Not(a>b)", //
        "a<=b");
    check("Not(a>=b)", //
        "a<b");
    check("Not(a==b)", //
        "a!=b");
    check("Not(x>1)", //
        "x<=1");
    check("!Exists(x, f(x))", //
        "ForAll(x,!f(x))");
    check("!ForAll(x, f(x))", //
        "Exists(x,!f(x))");
  }

  @Test
  public void testNotElement() {
    check("NotElement(Pi, Integers)", //
        "True");
    check("NotElement(a, Reals)", //
        "a∉Reals");
    // TODO improve Rationals and Algebraics domains
    check("NotElement(Sqrt(2), #)& /@ {Complexes, Algebraics, Reals, Rationals, Integers, Primes}", //
        "{False,Sqrt(2)∉Algebraics,False,Sqrt(2)∉Rationals,True,True}");
  }

  @Test
  public void testNothing() {
    check("Nothing(a,b,c)", //
        "Nothing");
    check("{a, Nothing, Nothing, d}", //
        "{a,d}");
    check("bar(a, Nothing, baz(Nothing), c)", //
        "bar(a,Nothing,baz(Nothing),c)");
    check("{1, 2, Nothing, 4, 5, Nothing}", //
        "{1,2,4,5}");
    check("ReplacePart({a, b, c, d, e, f, g}, {1 -> Nothing, 3 -> Nothing})", //
        "{b,d,e,f,g}");
  }

  @Test
  public void testNumberDigit() {
    check("NumberDigit(Pi, -1, 2)", //
        "0");
    check("NumberDigit(Pi, -2, 2)", //
        "0");
    check("NumberDigit(Pi, -3, 2)", //
        "1");

    check("Range(2, -4, -1)", //
        "{2,1,0,-1,-2,-3,-4}");

    check("NumberDigit(Pi, Range(2, -4, -1), 2)", //
        "{0,1,1,0,0,1,0}");
    check("NumberDigit({Pi, E, 99352, 11/7}, {4, 0, -2})", //
        "{{0,3,4},{0,2,1},{9,2,0},{0,1,7}}");
    check("NumberDigit(Pi, Range(2, -2, -1))", //
        "{0,0,3,1,4}");
    check("NumberDigit(Pi, Range(2, -2, -1),16)", //
        "{0,0,3,2,4}");
    check("NumberDigit(123456, 2)", //
        "4");
    check("RealDigits(123456)", //
        "{{1,2,3,4,5,6},6}");
    check("NumberDigit(12.3456, -1)", //
        "3");
  }

  @Test
  public void testNumberQ() {
    check("NumberQ(3,4)", //
        "NumberQ(3,4)");
    check("NumberQ(3+I)", //
        "True");
    check("NumberQ(5!)", //
        "True");
    check("NumberQ(Pi)", //
        "False");
  }

  @Test
  public void testNumericalOrder() {
    check("NumericalOrder(Sqrt(2)+5, E+Pi)", //
        "-1");
    check("NumericalOrder(E+Pi,Sqrt(2)+5)", //
        "1");
    check("NumericalOrder(6,Pi)", //
        "-1");
    check("NumericalOrder(Exp(I), Sin(1))", //
        "1");
    check("NumericalOrder(E^I,(-10)*I+Cos(1))", //
        "1");
    check("NumericalOrder(-Infinity, GoldenRatio)", //
        "1");
    check("NumericalOrder(Infinity, 0)", //
        "-1");
    check("NumericalOrder(3/2, 1)", //
        "-1");
    check("NumericalOrder(3/2, 1.5)", //
        "0");

    check("NumericalOrder(Infinity,4)", //
        "-1");
    check("NumericalOrder(-Infinity,3/4)", //
        "1");

    check("NumericalOrder(Pi, E^2)", //
        "1");
    check("Order(Pi, E^2)", //
        "-1");
    // TODO this is canonical ordered
    check("NumericalOrder(Quantity(1, \"Meters\"), Quantity(3, \"Feet\"))", //
        "-1");
    check("NumericalOrder(Quantity(3, \"Feet\"), Quantity(1, \"Meters\"))", //
        "1");

    check("lst = {1, Pi, E, Infinity, I*Sqrt(-2), -Infinity}", //
        "{1,Pi,E,Infinity,-Sqrt(2),-Infinity}");
    check("ord = Ordering(lst, All, NumericalOrder)", //
        "{6,5,1,3,2,4}");
    check("lst[[ord]]", //
        "{-Infinity,-Sqrt(2),1,E,Pi,Infinity}");
  }

  @Test
  public void testNumericalSort() {
    check("NumericalSort({1,2,3,Infinity,-Infinity,E,Pi,GoldenRatio,Degree})", //
        "{-Infinity,Pi/180,1,GoldenRatio,2,E,3,Pi,Infinity}");

    check(
        "NumericalSort({ Infinity, Sqrt(2), -1, 0, -Infinity, Quantity(1, \"Meters\"),  Quantity(3, \"Feet\")})", //
        "{-Infinity,-1,0,Sqrt(2),Infinity,3[Feet],1[Meters]}");

    check("NumericalSort({1, Pi, E, Infinity, -Sqrt[2], -Infinity})", //
        "{-Infinity,-Sqrt(2),1,E,Pi,Infinity}");
    // TODO unequals MMA sort
    check("Sort({1, Pi, E, Infinity, -Sqrt[2], -Infinity})", //
        "{1,-Infinity,Infinity,-Sqrt(2),E,Pi}");

  }

  @Test
  public void testNumericQ() {
    // NumericQ calls expr.isNumericFunction( false )
    check("NumericQ({1,2,3})", //
        "False");

    check("NumericQ(1+GoldenAngle)", //
        "True");
    check("NumericQ(GoldenRatio)", //
        "True");
    check("NumericQ(Sqrt(3))", //
        "True");
    check("NumericQ(Glaisher)", //
        "True");
    check("1<Glaisher<2<E<3", //
        "True");
    check("NumericQ(Sqrt(sin(2)))", //
        "True");
    check("NumericQ(E+Pi)", //
        "True");
    check("NumericQ(Pi)", //
        "True");
    check("NumericQ(3+I)", //
        "True");
    check("NumberQ(5!)", //
        "True");
    check("NumberQ(Pi)", //
        "False");

    check("SetAttributes(f, NumericFunction)", //
        "");
    check("NumericQ(f(Pi))", //
        "True");

    check("expr=BesselJ(2, Sin(Exp(Log(3)+4))+x)", //
        "BesselJ(2,x+Sin(3*E^4))");
    check("TableForm(Map({#, NumericQ(#)} &, Level(expr, {0, Infinity})))", //
        "                       2   True \n" //
            + "                       x  False \n" //
            + "                       3   True \n" //
            + "                       E   True \n" //
            + "                       4   True \n" //
            + "                     E^4   True \n" //
            + "                   3*E^4   True \n" //
            + "              Sin(3*E^4)   True \n" //
            + "            x+Sin(3*E^4)  False \n" //
            + " BesselJ(2,x+Sin(3*E^4))  False ");
  }

  @Test
  public void testNumerator() {
    check("Numerator(ConditionalExpression(a/13,Element(C1,Integers)))", //
        "ConditionalExpression(a/13,c1∈Integers)");

    check("Numerator( a*x^n*y^- m*Exp(a - b - 2 c + 3 d) )", //
        "a*E^(a+3*d)*x^n");
    check("Numerator(a^-b/x)", //
        "1");

    check("N( Cos(Pi)/Pi )", //
        "-0.31831");
    // see github #151
    check("N(Numerator(Cos(Pi)/Pi))", //
        "-1.0");

    check("Numerator(a / b)", //
        "a");
    check("Numerator(a^2*b)", //
        "a^2*b");
    check("Numerator(a^2*b^-2)", //
        "a^2");
    check("Numerator(a^2*b^-a*c)", //
        "a^2*c");

    check("Numerator(Csc(x))", //
        "Csc(x)");
    check("Numerator(Csc(x), Trig->True)", //
        "1");
    check("Numerator(Csc(x)^4)", //
        "Csc(x)^4");
    check("Numerator(Csc(x)^4, Trig->True)", //
        "1");
    check("Numerator(42*Csc(x))", //
        "42*Csc(x)");
    check("Numerator(42*Csc(x), Trig->True)", //
        "42");
    check("Numerator(42*Csc(x)^3)", //
        "42*Csc(x)^3");
    check("Numerator(42*Csc(x)^3, Trig->True)", //
        "42");
    check("Numerator(E^(-x)*x^(1/2))", //
        "Sqrt(x)");

    check("Numerator(Sec(x))", //
        "Sec(x)");
    check("Numerator(Sec(x), Trig->True)", //
        "1");
    check("Numerator(Tan(x))", //
        "Tan(x)");
    check("Numerator(Tan(x), Trig->True)", //
        "Sin(x)");

    check("Numerator(2 / 3)", //
        "2");
    check("Numerator(a + b)", //
        "a+b");
  }

  @Test
  public void testOddQ() {
    check("OddQ(1/(b-a*c)[[2]])", //
        "False");
    check("OddQ((1/(b-a*c))[[2]])//Trace", //
        "{{(1/(b-a*c))[[2]],-1},True}");
    check("OddQ({1,3}) && OddQ({5,7})", //
        "{True,True}&&{True,True}");
    check("OddQ(2+4*I, GaussianIntegers->True)", //
        "False");
    check("OddQ(2-3*I, GaussianIntegers->True)", //
        "True");
    check("OddQ(1+4*I, GaussianIntegers->True)", //
        "True");
    check("OddQ(1+I, GaussianIntegers->True)", //
        "False");
    check("OddQ(4*I, GaussianIntegers->True)", //
        "False");
    check("OddQ(6, GaussianIntegers->True)", //
        "False");
    check("OddQ(I, GaussianIntegers->True)", //
        "True");
    check("OddQ(3, GaussianIntegers->True)", //
        "True");
  }

  @Test
  public void testEvenQ() {
    check("EvenQ(2+4*I, GaussianIntegers->True)", //
        "True");
    check("EvenQ(2-3*I, GaussianIntegers->True)", //
        "False");
    check("EvenQ(1+4*I, GaussianIntegers->True)", //
        "False");
    check("EvenQ(1+I, GaussianIntegers->True)", //
        "False");
    check("EvenQ(4*I, GaussianIntegers->True)", //
        "True");
    check("EvenQ(6, GaussianIntegers->True)", //
        "True");
    check("EvenQ(I, GaussianIntegers->True)", //
        "False");
    check("EvenQ(3, GaussianIntegers->True)", //
        "False");
  }

  @Test
  public void testOneIdentity() {
    check("SetAttributes(f, OneIdentity)", //
        "");
    // with a default argument, the pattern does match:
    check("a /. f(x_:0, u_) -> {u}", //
        "{a}");
    // without a default argument, the pattern does not match:
    check("a /. f(u_) -> {u}", //
        "a");
    // OneIdentity does not affect evaluation:
    check("f(a)", //
        "f(a)");
  }

  @Test
  public void testOneIdentityOrderless() {

    // github issue 89

    check(
        "Module[{e}, ClearAll[eqv]; SetAttributes[eqv, {Flat}]; eqv[p, q, r] /. {eqv[x_, y_] :> {x, y}}]", //
        "{eqv(p),eqv(q,r)}");

    check(
        "Module[{e}, ClearAll[eqv]; SetAttributes[eqv, {Flat, OneIdentity}]; eqv[p, q, r] /. {eqv[x_, y_] :> {x, y}}]", //
        "{p,eqv(q,r)}");

    // See discussion here:
    // https://mathematica.stackexchange.com/questions/183322/subtle-order-of-evaluation-issues-when-pattern-matching-with-attributes
    check(
        "Module[{e}, ClearAll[eqv]; SetAttributes[eqv, {Flat, Orderless}]; eqv[p, q, r] /. {eqv[x_, y_] :> {x, y}}]", //
        "{p,eqv(q,r)}");

    check(
        "Module[{e}, ClearAll[eqv]; SetAttributes[eqv, {Flat, OneIdentity, Orderless}]; eqv[p, q, r] /. {eqv[x_, y_] :> {x, y}}]", //
        "{p,eqv(q,r)}");

    check("SetAttributes(f,{Orderless,OneIdentity})", //
        "");
    check("f(p, q) /. {f(x_,y_) :> {x,y}}", //
        "{p,q}");
    check("f(q,f(p,r)) /. {f(x_,y_) :> {x,y}}", //
        "{q,f(p,r)}");
  }

  @Test
  public void testOperate() {
    check("Operate(a, (a*c)[f(x)])", //
        "a(a*c)[f(x)]");
    check("Through(Operate(a, (a*c)[f(x)]))", //
        "a((a*c)[f(x)])");
    check("Through(Operate(p, f(x)))", //
        "p(f(x))");
    check("Composition(p, f)[x]", //
        "p(f(x))");
    check("Operate(g &, f(a, b, c))", //
        "g(a,b,c)");
    check("Operate(p, f(a, b))", //
        "p(f)[a,b]");
    check("Operate(p, f(a, b), 1)", //
        "p(f)[a,b]");
    check("Operate(p, f(a)[b][c], 0)", //
        "p(f(a)[b][c])");
    check("Operate(p, f(a)[b][c])", //
        "p(f(a)[b])[c]");
    check("Operate(p, f(a)[b][c], 1)", //
        "p(f(a)[b])[c]");
    check("Operate(p, f(a)[b][c], 2)", //
        "p(f(a))[b][c]");
    check("Operate(p, f(a)[b][c], 3)", //
        "p(f)[a][b][c]");
    check("Operate(p, f(a)[b][c], 4)", //
        "f(a)[b][c]");
    check("Operate(p, f, 0)", //
        "p(f)");
    check("Operate(p, f, -1)", //
        "Operate(p,f,-1)");

    check("Operate(p, f)", //
        "f");
    check("Operate(p, f, 0)", //
        "p(f)");
    check("Operate(p, f(a)[b][c],0)", //
        "p(f(a)[b][c])");
    check("Operate(p, f(a)[b][c])", //
        "p(f(a)[b])[c]");
    check("Operate(p, f(a)[b][c],1)", //
        "p(f(a)[b])[c]");
    check("Operate(p, f(a)[b][c],2)", //
        "p(f(a))[b][c]");
    check("Operate(p, f(a)[b][c],3)", //
        "p(f)[a][b][c]");
    check("Operate(p, f(a)[b][c],4)", //
        "f(a)[b][c]");
    check("Operate(p, f(x, y))", //
        "p(f)[x,y]");
  }

  @Test
  public void testDP() {
    check("DP(k_,w_):=Sum(w^d/d, {d, Divisors(k)})", //
        "");
    check("DP(6,w)", //
        "w+w^2/2+w^3/3+w^6/6");
  }

  @Test
  public void testOptimizeExpression() {
    check("OptimizeExpression( (x+1)^2+(x+1)^3)", //
        "{v1^2+v1^3,{v1->1+x}}");
    check("OptimizeExpression(Sqrt(Sin(x)))", //
        "{Sqrt(Sin(x)),{}}");
    check("OptimizeExpression(Sqrt(Sin(x)+5)*Sqrt(Sin(x)+4))", //
        "{Sqrt(4+v1)*Sqrt(5+v1),{v1->Sin(x)}}");
    check("OptimizeExpression(Sqrt(Sin(x+1) + 5 + Cos(y))*Sqrt(Sin(x+1) + 4 + Cos(y)))", //
        "{Sqrt(4+v1+v2)*Sqrt(5+v1+v2),{v2->Cos(y),v1->Sin(1+x)}}");
    check("OptimizeExpression((x-y)*(z-y) + sqrt((x-y)*(z-y)))", //
        "{Sqrt(v1)+v1,{v1->(x-y)*(-y+z)}}");

    check("OptimizeExpression(#1+1+(#1+1)*(#1+1)&)", //
        "{v1*v1+v1&,{v1->1+#1}}");
    check("OptimizeExpression(f(x))", //
        "{f(x),{}}");
    check(
        "OptimizeExpression(-3*a - 2*a^3 + 4*Sqrt(1 + a^2)*(5 - 9*Log(2)) + \n"
            + " 4*a^2*Sqrt(1 + a^2)*(5 - 9*Log(2)) + \n"
            + " 12*(1 + a^2)^(3/2)*Log(1 + Sqrt(1 + 1/a^2)) - \n"
            + " 6*(4*(Sqrt(1 + a^2) - a*(2 + a^2 - a*Sqrt(1 + a^2)))*Log(a) + a*Log(1 + a^2)))", //
        //
        "{-3*a-2*a^3+4*v1*v2+4*v1*v2*v4+12*v3^(3/2)*Log(1+Sqrt(1+1/a^2))-6*(4*(v1-a*(2-a*v1+v4))*Log(a)+a*Log(v3))," //
            + "{v4->a^\n" //
            + "2," //
            + "v3->1+v4," //
            + "v2->5-9*Log(2)," //
            + "v1->Sqrt(1+v4)}}");

    check(
        "OptimizeExpression((3 + 3*a^2 + Sqrt(5 + 6*a + 5*a^2) + a*(4 + Sqrt(5 + 6*a + 5*a^2)))/6)", //
        "{1/6*(3+v1+a*(4+v1)+3*v2),{v2->a^2,v1->Sqrt(5+6*a+5*v2)}}");

    check("OptimizeExpression( Sin(x) + Cos(Sin(x)))", //
        "{v1+Cos(v1),{v1->Sin(x)}}");

    check("ReplaceRepeated@@OptimizeExpression( Sin(x) + Cos(Sin(x)))", //
        "Cos(Sin(x))+Sin(x)");
  }

  @Test
  public void testReplaceRepeated() {
    check("ReplaceRepeated(x, x -> x + 1, MaxIterations -> 4)", //
        "4+x");
    // example from https://en.wikipedia.org/wiki/Wolfram_Language
    check("sortRule := {x___,y_,z_,k___} /; y>z -> {x,z,y,k}", //
        "");
    check("{ 9, 5, 3, 1, 2, 4 } //. sortRule", //
        "{1,2,3,4,5,9}");

    check("f(g(x),y)//.{f(x_,y_):>k(g(x),g(y)),g(g(x_)):>g(x)}", //
        "k(g(x),g(y))");
    check("x//.x -> 1", //
        "1");

    check("a+b+c //. c->d", //
        "a+b+d");
    check("logrules = {Log(x_ * y_) :> Log(x) + Log(y), Log(x_^y_) :> y * Log(x)};", //
        "");
    check("Log(a * (b * c) ^ d ^ e * f) //. logrules", //
        "Log(a)+d^e*(Log(b)+Log(c))+Log(f)");

    // `ReplaceAll` just performs a single replacement:
    check("Log(a * (b * c) ^ d ^ e * f) /. logrules", //
        "Log(a)+Log((b*c)^d^e*f)");

    check("{f(f(x)), f(x), g(f(x)), f(g(f(x)))} //. f(x_) -> x", //
        "{x,x,g(x),g(x)}");
    check("ReplaceRepeated(f(x_) -> x)[{f(f(x)), f(g(f(x)))}]", //
        "{x,g(x)}");
    check("Log(Sqrt(a*(b*c^d)^e)) //. logrules", //
        "1/2*(Log(a)+e*(Log(b)+d*Log(c)))");
    check("Log(Sqrt(a(b*c^d)^e)) /. logrules", //
        "1/2*e*Log(a(b*c^d))");

    check("ReplaceRepeated(1/6*(3+3*v1+v2+a*(4+v2)), {v1->a^2, v2->Sqrt(5+6*a+5*v1)})", //
        "1/6*(3+3*a^2+Sqrt(5+6*a+5*a^2)+a*(4+Sqrt(5+6*a+5*a^2)))");
    check("ReplaceRepeated(1/6*(3+3*v1+v2+a*(4+v2)), {v2->Sqrt(5+6*a+5*v1), v1->a^2})", //
        "1/6*(3+3*a^2+Sqrt(5+6*a+5*a^2)+a*(4+Sqrt(5+6*a+5*a^2)))");


    check("x //. {a -> 42, b -> -1}", //
        "x");
    check("x //.  {{x -> 42}, {b -> -1}}", //
        "{42,x}");
  }

  @Test
  public void testOr() {
    check("Or(z, z)", //
        "z||z");
    check("Or(a, z, z)", //
        "a||z||z");
    check("Attributes(Or)", //
        "{Flat,HoldAll,OneIdentity,Protected}");
    check("Or(p, p, p) /. Or(a_, b_) :> {a, b}", //
        "{p,p||p}");
    check("Or(p, p, p) /. Or(a_., b_.) :> {a, b}", //
        "{p,p||p}");
    check("Or(p, p, p)", //
        "p||p||p");
    check("Or(p, q) === Or(q, p)", //
        "False");

    check("False || True", //
        "True");
    check("a || False || b", //
        "a||b");
    check("Or( )", //
        "False");
    check("Or(2+2)", "4");
    check("FullForm( Or(x, Or(y, z)) )", //
        "Or(x, y, z)");
    check("Or(x, False, z)", //
        "x||z");
    check("Or(x, True, z)", //
        "True");
  }

  @Test
  public void testOrder() {
    // Order compares expressions structurally
    check("Order(6, Pi)", //
        "1");

    check("Order(3,4)", //
        "1");
    check("Order(4,3)", //
        "-1");
    check("Order(6,Pi)", //
        "1");
    check("Order(6,N(Pi))", //
        "-1");
  }

  @Test
  public void testOrdering() {
    check("Ordering({1,3,4,2,5,9,6})", //
        "{1,4,2,3,5,7,6}");
    check("Ordering({1,3,4,2,5,9,6},All,Greater)", //
        "{6,7,5,3,2,4,1}");

    check("Ordering({2, 6, 1, 9, 1, 2, 3}, -1)", //
        "{4}");
    check("Ordering({2, 6, 1, 9, 1, 2, 3}, -3)", //
        "{4,2,7}");

    check("Ordering({c,a,b})", //
        "{2,3,1}");
    check("Ordering(f(3, 1, 2))", //
        "{2,3,1}");
    check("Ordering({2, 6, 1, 9, 1, 2, 3},4)", //
        "{3,5,1,6}");
    check("Ordering({2, 6, 1, 9, 1, 2, 3},All,Greater)", //
        "{4,2,7,1,6,3,5}");

    check("l={2, 6, 1, 9, 1, 2, 3}", //
        "{2,6,1,9,1,2,3}");
    check("l[[ Ordering(l) ]]", //
        "{1,1,2,2,3,6,9}");

    check("Ordering({2, 6, 1, 9, 1, 2, 3}, -20)", //
        "{4,2,7,6,1,5,3}");
    check("Ordering({2, 6, 1, 9, 1, 2, 3}, 20)", //
        "{3,5,1,6,7,2,4}");
  }

  @Test
  public void testOrderedQ() {
    check("OrderedQ({{1,2,3}, {1,4}})", //
        "False");
    check("OrderedQ(<|2 -> c, 7 -> b, 3 -> a|>)", //
        "False");
    check("OrderedQ(<|2 -> c, 7 -> b, 3 -> a|>, Greater)", //
        "True");
    check("OrderedQ(<|2 -> a, 7 -> b, 3 -> c|>)", //
        "True");
    check("OrderedQ(<|1 -> 10, 2 -> 5, 3 -> 0|>)", //
        "False");
    check("OrderedQ({{a, 3}, {d, 2}, {c, 1}}, #1[[2]] < #2[[2]] &)", //
        "False");
    check("OrderedQ({{a, 3}, {d, 2}, {c, 1}}, #1[[2]] > #2[[2]] &)", //
        "True");
    check("OrderedQ({1, Sqrt(2), 2, E, 3, Pi})", //
        "False");
    check("OrderedQ({1, Sqrt(2), 2, E, 3, Pi}, Less)", //
        "True");
    check("OrderedQ({a, b})", //
        "True");
    check("OrderedQ({b, a})", //
        "False");

    check("OrderedQ({x^2, 4+6*x})", //
        "False");

    check("OrderedQ({x^2,x^3})", //
        "True");

    check("OrderedQ({x,x^6.0 })", //
        "True");
    check("OrderedQ({4.0*x,33.0*x^6.0 })", //
        "True");
    check("OrderedQ({x^3,4+4*a })", //
        "False");

    check("OrderedQ({x^2, 6*x})", //
        "False");
    check("OrderedQ({6*x,x^2})", //
        "True");
    check("OrderedQ({a,a})", //
        "True");
    check("OrderedQ({x, y, x + y})", //
        "True");



    check("p(_?NumberQ _Symbol):= -1;\n"//
        + "p(_Symbol,_?NumberQ):= 1;\n" //
        + "p(_Symbol,_Symbol):= 0;\n" //
        + "p(x_?NumberQ,y_?NumberQ):= x <= y;", //
        "");
    check("OrderedQ({t,e,s,t,3,7,11,13}, p)", //
        "True");

  }

  @Test
  public void testOrderless() {
    check("SetAttributes(to, Orderless)", //
        "");
    check("to(b_.*x_^3, x_) := {b,x}", //
        "");

    check("to(x, x)", //
        "to(x,x)");
    check("to(a*x, x)", //
        "to(x,a*x)");
    check("to(x^3, x)", //
        "{1,x}");
    check("to(a*x^3, x)", //
        "{a,x}");
    check("to(a*x*z, x)", //
        "to(x,a*x*z)");
    check("to(a*x^3*z, x)", //
        "{a*z,x}");

    check("to(b_.*x_^n_., x_) := {b,n,x}", //
        "");

    check("to(x, x)", //
        "{1,1,x}");
    check("to(a*x, x)", //
        "{a,1,x}");
    check("to(x^2, x)", //
        "{1,2,x}");
    check("to(a*x^2, x)", //
        "{a,2,x}");
    check("to(a*x*z, x)", //
        "{a*z,1,x}");
    check("to(a*x^2*z, x)", //
        "{a*z,2,x}");
    // see https://github.com/mathics/Mathics/issues/747
    check("SetAttributes(ordl,{OneIdentity,Orderless});ordl(p,p,p)/.ordl(p_.,p_.):>p", //
        "ordl(p,p,p)");

    check("SetAttributes(f, Orderless)", //
        "");
    check("f(c, a, b, a + b, 3, 1.0)", //
        "f(1.0,3,a,b,a+b,c)");
    check("f(a, b) == f(b, a)", //
        "True");
    check("SetAttributes(f, Flat)", //
        "");
    check("Attributes(f)", //
        "{Flat,Orderless}");
    check("f(a, b, c) /. f(a, c) -> d", //
        "f(b,d)");

    check("SetAttributes(f1, Orderless)", //
        "");
    check("f1(x_Symbol, y_Integer, z_Real) := {x,y,z}", //
        "");
    check("{f1(x,3.0,2),f1(3.0,x,2),f1(2,3.0,x)}", //
        "{{x,2,3.0},{x,2,3.0},{x,2,3.0}}");
  }

  @Test
  public void testOut() {
    check("Expand((x + y)^2)", //
        "x^2+2*x*y+y^2");
    check("N(Pi, 30)", //
        "3.14159265358979323846264338327");
    check("Out( )", //
        "3.14159265358979323846264338327");
    check("Out(-3)", //
        "x^2+2*x*y+y^2");
    check("Out(-5)", //
        "Out(-5)");
    check("Out(1)", //
        "x^2+2*x*y+y^2");
    check("Out(2)", //
        "3.14159265358979323846264338327");
  }


  @Test
  public void testUnderflow() {
    check("5*Underflow() // N", //
        "0.0");
    check("5*Underflow() ", //
        "Underflow()");
    check("Underflow() // N", //
        "0.0");

    check("1-Underflow()", //
        "1+Underflow()");
    check("1-Underflow() // N", //
        "1.0");

    check("Sin(7)+x+Underflow() // N", //
        "0.656987+x");
    check("Sin(7)+x+Underflow()", //
        "x+Sin(7)+Underflow()");
  }

  @Test
  public void testOverflow() {
    check("5*Overflow() // N", //
        "Overflow()");
    check("5*Overflow() ", //
        "Overflow()");
    check("Overflow() // N", //
        "Overflow()");

    check("Sin(7)+x+Overflow() // N", //
        "x+Overflow()");
    check("Sin(7)+x+Overflow()", //
        "x+Overflow()");
  }

  @Test
  public void testOwnValues() {
    check("a=42", //
        "42");
    check("OwnValues(a)", //
        "{HoldPattern(a):>42}");

    check("a=21", //
        "21");
    // TODO
    check("Hold(a) /. OwnValues(a)", //
        "Hold(21)");
  }

  @Test
  public void testPadLeft() {
    // TODO
    // check("PadLeft({1, 2, 3}, 10, {a, b, c}, 2)", //
    // "{b, c, a, b, c, 1, 2, 3, a, b}");
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      check("PadLeft(x^2,1009)", //
          "Recursion depth of 256 exceeded during evaluation of Null.");
    }
    // https://oeis.org/A196023
    check(
        "Select(Table(FromDigits@Join(Flatten@IntegerDigits@PadLeft({666}, 2, n), Reverse@IntegerDigits(n)), {n, 397}), PrimeQ)", //
        "{16661,76667,3166613,3466643,7466647,7666667,145666541,148666841,152666251,\n" + //
            "155666551,169666961,176666671,181666181,304666403,305666503,307666703,308666803,\n" + //
            "329666923,347666743,349666943,373666373,374666473,383666383,391666193,397666793}");
    check("PadLeft({1, 2, 3}, 5)", //
        "{0,0,1,2,3}");
    check("PadLeft(x(a, b, c), 5) ", //
        "x(0,0,a,b,c)");
    check("PadLeft({1, 2, 3}, 2)", //
        "{2,3}");
    check("PadLeft({1, 2, 3}, 1)", //
        "{3}");

    check("PadLeft({{a, b, d}, {c}}, {3,5})", //
        "{{0,0,0,0,0},{0,0,a,b,d},{0,0,0,0,c}}");
    check("PadLeft(f(g(a, b, d, e, f), {c}), {3,4},1)", //
        "f(f(1,1,1,1),g(b,d,e,f),{1,1,1,c})");
    check("PadLeft({{a, b, d, e, f}, {c}}, {3,4})", //
        "{{0,0,0,0},{b,d,e,f},{0,0,0,c}}");
    check("PadLeft({{a, b, d, e}, {c}}, {3,4})", //
        "{{0,0,0,0},{a,b,d,e},{0,0,0,c}}");
    check("PadLeft({{a, b, d}, {c}}, {3,4})", //
        "{{0,0,0,0},{0,a,b,d},{0,0,0,c}}");
    check("PadLeft({{a, b}, {c}}, {3,4})", //
        "{{0,0,0,0},{0,0,a,b},{0,0,0,c}}");
    check("PadLeft({{a, b}, c}, {3,4})", //
        "PadLeft({{a,b},c},{3,4})");

    check("PadLeft({1, 2, 3}, 5)", //
        "{0,0,1,2,3}");
    check("PadLeft(x(a, b, c), 5) ", //
        "x(0,0,a,b,c)");
    check("PadLeft({1, 2, 3}, 2)", //
        "{2,3}");
    check("PadLeft({1, 2, 3}, 1)", //
        "{3}");
    check("PadLeft({{}, {1, 2}, {1, 2, 3}})", //
        "{{0,0,0},{0,1,2},{1,2,3}}");

    check("PadLeft({a, b, c}, 10)", //
        "{0,0,0,0,0,0,0,a,b,c}");
    check("PadLeft({a, b, c}, 10, {x, y, z})", //
        "{z,x,y,z,x,y,z,a,b,c}");
    check("PadLeft({a, b, c}, 9, {x, y, z})", //
        "{x,y,z,x,y,z,a,b,c}");
    check("PadLeft({a, b, c}, 8, {x, y, z})", //
        "{y,z,x,y,z,a,b,c}");
    check("PadLeft({a, b, c}, 10, 42)", //
        "{42,42,42,42,42,42,42,a,b,c}");
  }

  @Test
  public void testPadRight() {
    check("PadRight(Slot(<|s1-><|a->0,b:>1|>,s2:><|a->0,b:>1|>|>),{1},{1,2,3,a}+1)", //
        "Slot(Association(s1->Association(a->0,b:>1),s2:>Association(a->0,b:>1)))");
    check("PadRight(Slot(<|s1-><|a->0,b:>1|>,s2:><|a->0,b:>1|>|>),{1,11,1},{1,2,3,a}+1)", //
        "Slot(Association(Rule(s1),RuleDelayed(s2),Slot({2,3,4,1+a}),Slot({2,3,4,1+a}),Slot({\n"
            + "2,3,4,1+a}),Slot({2,3,4,1+a}),Slot({2,3,4,1+a}),Slot({2,3,4,1+a}),Slot({2,3,4,1+a}),Slot({\n"
            + "2,3,4,1+a}),Slot({2,3,4,1+a})))");
    check("With({r = Map(Fibonacci, Range(2, 14))}, " + //
        "Position(#, {1, 0, 1})[[All, 1]] &@ Table(If(Length@ # < 3, {}, Take(#, -3)) &@ IntegerDigits@ Total@ Map(FromDigits@ PadRight({1}, Flatten@ #) &@ Reverse@ Position(r, #) &, Abs@ Differences@ NestWhileList(Function(k, k - SelectFirst(Reverse@ r, # < k &)), n + 1, # > 1 &)), {n, 373}))", //
        "{4,12,17,25,33,38,46,51,59,67,72,80,88,93,101,106,114,122,127,135,140,148,156,\n" + //
            "161,169,177,182,190,195,203,211,216,224,232,237,245,250,258,266,271,279,284,292,\n" + //
            "300,305,313,321,326,334,339,347,355,360,368,373}");
    check("PadRight({1, 2, 3}, 5)", //
        "{1,2,3,0,0}");
    check("PadRight(x(a, b, c), 5) ", //
        "x(a,b,c,0,0)");
    check("PadRight({1, 2, 3}, 2)", //
        "{1,2}");
    check("PadRight({1, 2, 3}, 1)", //
        "{1}");

    check("PadRight(f(g(a, b, d, e, f), {c}), {3,4},1)", //
        "f(g(a,b,d,e),{c,1,1,1},f(1,1,1,1))");
    check("PadRight({{a, b, d, e, f}, {c}}, {3,4})", //
        "{{a,b,d,e},{c,0,0,0},{0,0,0,0}}");
    check("PadRight({{a, b, d, e}, {c}}, {3,4})", //
        "{{a,b,d,e},{c,0,0,0},{0,0,0,0}}");
    check("PadRight({{a, b, d}, {c}}, {3,4})", //
        "{{a,b,d,0},{c,0,0,0},{0,0,0,0}}");
    check("PadRight({{a, b}, {c}}, {3,4})", //
        "{{a,b,0,0},{c,0,0,0},{0,0,0,0}}");
    check("PadRight({{a, b}, c}, {3,4})", //
        "PadRight({{a,b},c},{3,4})");

    check("PadRight({{}, {1, 2}, {1, 2, 3}})", //
        "{{0,0,0},{1,2,0},{1,2,3}}");

    check("PadRight({a, b, c}, 10)", //
        "{a,b,c,0,0,0,0,0,0,0}");
    check("PadRight({a, b, c}, 10, {x, y, z})", //
        "{a,b,c,x,y,z,x,y,z,x}");
    check("PadRight({a, b, c}, 9, {x, y, z})", //
        "{a,b,c,x,y,z,x,y,z}");
    check("PadRight({a, b, c}, 8, {x, y, z})", //
        "{a,b,c,x,y,z,x,y}");
    check("PadRight({a, b, c}, 10, 42)", //
        "{a,b,c,42,42,42,42,42,42,42}");
    check("PadRight({{0}},{{1,0},{0,1}})", //
        "PadRight({{0}},{{1,0},{0,1}})");
  }

  @Test
  public void testParenthesis() {
    check("x+Parenthesis(a+b+c)", //
        "x+(a+b+c)");
    check("TeXForm(x+Parenthesis(a+b+c))", //
        "x + (a + b + c)");
    check("MathMLForm(x+Parenthesis(a+b+c))", //
        "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
            + "<math mode=\"display\">\n"
            + "<mrow><mrow><mo>(</mo><mrow><mi>c</mi><mo>+</mo><mi>b</mi><mo>+</mo><mi>a</mi></mrow><mo>)</mo></mrow><mo>+</mo><mi>x</mi></mrow></math>");
  }

  @Test
  public void testParametricPlot() {

    // wrong parameter call
    check("ParametricPlot(17,Heads->False,-1009,True)", //
        "ParametricPlot(17,Heads->False,-1009,True)");

  }

  @Test
  public void testParserFixedPoint() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("{28, 21} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}");
      assertEquals(obj.toString(),
          "ReplaceAll(List(28, 21), Rule(Condition(List(a_, b_), Unequal(b, 0)), List(b, Mod(a, b))))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  @Test
  public void testPartition() {
    check("Partition({-1/2,-2,3},3,2147483647)", //
        "{{-1/2,-2,3}}");
    check("Partition(f(),1)", //
        "f()");
    check("Partition(f(x),1)", //
        "f(f(x))");
    check("Partition({a,b,c,d,e,f},3,0)", //
        "Partition({a,b,c,d,e,f},3,0)");
    check("Partition({a, b, c, d, e, f}, -1)", //
        "Partition({a,b,c,d,e,f},-1)");
    check("Partition({a, b, c, d, e, f}, 2)", //
        "{{a,b},{c,d},{e,f}}");
    check("Partition({a, b, c, d, e, f}, 3, 1)", //
        "{{a,b,c},{b,c,d},{c,d,e},{d,e,f}}");
    check("Partition({a, b, c, d, e}, 2)", //
        "{{a,b},{c,d}}");
  }

  @Test
  public void testPartitionsP() {
    // iteration limit
    check("PartitionsP(10001)", //
        "Hold(PartitionsP(10001))");

    check("PartitionsP(101)", //
        "214481126");
    check("PartitionsP(0)", //
        "1");
    check("PartitionsP(-5)", //
        "0");
    check("Table(PartitionsP(k), {k, 0, 12})", //
        "{1,1,2,3,5,7,11,15,22,30,42,56,77}");
    check("PartitionsP(5)", //
        "7");
    check("PartitionsP(6)", //
        "11");
    check("PartitionsP(9)", //
        "30");
    check("PartitionsP(50)", //
        "204226");
    check("PartitionsP(100)", //
        "190569292");
    check("PartitionsP(200)", //
        "3972999029388");
    check("PartitionsP(300)", //
        "9253082936723602");
    // check("PartitionsP(1000)", "24061467864032622473692149727991");
  }

  @Test
  public void testPartitionsQ() {
    check("PartitionsQ(0)", //
        "1");
    check("PartitionsQ(-5)", //
        "0");
    check("PartitionsQ(3)", //
        "2");
    check("PartitionsQ(5)", //
        "3");
    check("PartitionsQ(6)", //
        "4");
    check("PartitionsQ(10)", //
        "10");
    check("PartitionsQ(15)", //
        "27");
    check("PartitionsQ(16)", //
        "32");
    check("PartitionsQ(17)", //
        "38");
    check("PartitionsQ(18)", //
        "46");
    check("PartitionsQ(20)", //
        "64");
    check("PartitionsQ(50)", //
        "3658");
    // upper limit to avid stack overflow
    check("PartitionsQ(201)", //
        "PartitionsQ(201)");
  }

  @Test
  public void testPerfectNumber() {
    check("PerfectNumber(2^101)", //
        "PerfectNumber(2535301200456458802993406410752)");
    // test Listable attribute
    check("PerfectNumber(Range(3))", //
        "{6,28,496}");
    check("Table(PerfectNumber(i), {i,5})", //
        "{6,28,496,8128,33550336}");
    check("PerfectNumber(1)", //
        "6");
    check("PerfectNumber(2)", //
        "28");
    check("PerfectNumber(3)", //
        "496");
    check("PerfectNumber(6)", //
        "8589869056");
    check("PerfectNumber(7)", //
        "137438691328");
    check("PerfectNumber(8)", //
        "2305843008139952128");

    // big integer results:
    check("PerfectNumber(9)", //
        "2658455991569831744654692615953842176");
    check("PerfectNumber(10)", //
        "191561942608236107294793378084303638130997321548169216");

    check("PerfectNumber(48)", //
        "PerfectNumber(48)");
  }

  @Test
  public void testPerfectNumberQ() {
    check("PerfectNumberQ(6)", //
        "True");
    check("PerfectNumberQ(1)", //
        "False");
    check("PerfectNumberQ(35)", //
        "False");
    check("Select(Range(1000), PerfectNumberQ)", //
        "{6,28,496}");
  }

  @Test
  public void testPatternAndRules() {
    check("a + 2 + b + c + x * y /. n_Integer + s__Symbol + r_ -> {n, s, r}", //
        "{2,a,b+c+x*y}");

    check(
        "f((-1)^i_*x_^(2*i_+1)/(2*i_+1)!, {i_Symbol,0,Infinity}) := Sin(x) /;  FreeQ(x,i); f((-1)^i*x^(2*i+1)/(2*i+1)!, {i,0,Infinity})", //
        "Sin(x)");
    check("f(a_.*x_^j_.,x_):={a,x,j}; f(a*x*z,x)", //
        "{a*z,x,1}");
    check("f(a_.*x_^j_.,x_):={a,x,j}; f(a*b*x^3,x)", //
        "{a*b,x,3}");
    check("f(a_.*x_^j_.,x_):={a,x,j}; f(a*b*x^3*y*z,x)", //
        "{a*b*y*z,x,3}");

    check("a + b + c /. a + b -> t", //
        "c+t");
    check("a + 2 + b + c + x * y /. n_Integer + s__Symbol + r_ -> {n, s, r}", //
        "{2,a,b+c+x*y}");
    check("f(a, b, c, d) /. f(first_, rest___) -> {first, {rest}}", //
        "{a,{b,c,d}}");
    check("f(4) /. f(x_?(# > 0&)) -> x ^ 2", //
        "16");
    check("f(4) /. f(x_) /; x > 0 -> x ^ 2", //
        "16");
    check("f(a, b, c, d) /. f(start__, end__) -> {{start}, {end}}", //
        "{{a},{b,c,d}}");
    check("f(a) /. f(x_, y_:3) -> {x, y}", //
        "{a,3}");
    // check("f(y, a->3) /. f(x_, OptionsPattern({a->2, b->5})) -> {x,
    // OptionValue(a), OptionValue(b)}", "");
  }

  @Test
  public void testPattern() {
    // TODO parse this as Optional(Pattern(a,b),Pattern(c,d))
    check("a:b:c:d", //
        "Optional(a:b,(c:d))");

    // Pattern: First element in Pattern(3,3) is not a valid pattern name.
    check("Pattern(3,3)", //
        "3:3");

    check("me:f(___,_Plus,___):={Unevaluated(me),Plus}", //
        "");
    check("f(a,b,c,d+e,f,g)", //
        "{Unevaluated(f(a,b,c,d+e,f,g)),Plus}");
    check("x:{___List} //FullForm", //
        "Pattern(x, List(BlankNullSequence(List)))");
    check("me:CircleTimes(___,_Plus,___) //FullForm", //
        "Pattern(me, CircleTimes(BlankNullSequence(), Blank(Plus), BlankNullSequence()))");

    check("x:{{_,_}...}  // FullForm", //
        "Pattern(x, List(RepeatedNull(List(Blank(), Blank()))))");

    check("x y_ : z", //
        "x*y_:z");
    check("x y : z", //
        "x*(y:z)");
    // check(
    // "a:b:c:d", //
    // "Optional(Pattern(a,b),Pattern(c,d))");

    check("Options(f) = { a  -> 1, b  -> 2 }", //
        "{a->1,b->2}");
    check("f(x_, opts : OptionsPattern()) := {x, Automatic, opts}", //
        "");
    check("f(a)", //
        "{a,Automatic}");
    check("f(a,c -> Automatic)", //
        "{a,Automatic,c->Automatic}");
  }

  @Test
  public void testPatternSequence() {
    check("integersQ(__Integer) = True", //
        "True");
    check("integersQ(__) = False", //
        "False");
    check("integersQ(1,2,3)", //
        "True");
    check("integersQ(1,2,a)", //
        "False");
  }

  @Test
  public void testPatternOrder() {
    // see https://mathematica.stackexchange.com/questions/8619
    check("PatternOrder(x_, 1)", //
        "-1");
    // check("PatternOrder(g(_, _List), g(_, {___}))", //
    // "-1");
    // check("PatternOrder(g({}, _List), g(_, _List))", //
    // "1");
    check("PatternOrder(g({}, _List), g(_, {___}))", //
        "-1");
    check("PatternOrder(g(a), g(_))", //
        "1");
    check("PatternOrder(a,b)", //
        "1");
    check("PatternOrder(g(a), g(b))", //
        "1");
    // check("PatternOrder(__, (_) ..)", //
    // "-1");
  }

  @Test
  public void testPatternTest() {
    // warning message: Pattern p_ appears on the right-hand-side of condition p_/;EvenQ(p_).
    check("{1, 2, 3, 4, 5} /. (p_ /; EvenQ(p_)) :> 0", //
        "{1,2,3,4,5}");
    check("{1, 2, 3, 4, 5} /. (p_ /; EvenQ(p)) :> 0", //
        "{1,0,3,0,5}");

    check("MatchQ({1,8,Pi},{__?Positive})", //
        "True");

    check("$j(x_, y_:1, z_:2) := jp(x, y, z); $j(a,b)", //
        "jp(a,b,2)");
    check("$j(x_, y_:1, z_:2) := jp(x, y, z); $j(a)", //
        "jp(a,1,2)");
    check("$f(x_:2):={x};$f()", //
        "{2}");
    check("$f(x_:2):={x};$f(a)", //
        "{a}");

    check("MatchQ(3, _Integer?(#>0&))", //
        "True");
    check("MatchQ(-3, _Integer?(#>0&))", //
        "False");

    check("Cases({1,2,3,5,x,y,4},_?NumberQ)", //
        "{1,2,3,5,4}");
    check("MatchQ({1,8,Pi},{__?Positive})", //
        "True");
    check("MatchQ({1,I,0},{__?Positive})", //
        "False");

    check("f(x_?NumericQ):= NIntegrate(Sin(t^3), {t, 0, x})", //
        "");
    check("f(2)", //
        "0.451948");
    check("f((1+Sqrt(2))/5)", //
        "0.0135768");
    check("f(a)", //
        "f(a)");

    check("{3,-5,2,7,-6,3} /. _?Negative:>0", //
        "{3,0,2,7,0,3}");

    check("Cases(Range(0,350),_?(Divisible(#,7)&&Divisible(#,5)&))", //
        "{0,35,70,105,140,175,210,245,280,315,350}");

    check("$f(n_?NonNegative, p_?PrimeQ):=n^p; $f(2,3)", //
        "8");
    check("$f(n_?NonNegative, p_?PrimeQ):=n^p; $f(2,4)", //
        "$f(2,4)");

    check("MatchQ({{a,b},{c,d}},{_,_}?MatrixQ)", //
        "True");
    check("MatchQ({a,b},{_,_}?MatrixQ)", //
        "False");

    check("Cases({{a,b},{1,2,3},{{d,6},{d,10}}}, {_,_}?VectorQ)", //
        "{{a,b}}");
    check("Cases({{a,b},{1,2,3},{{d,6},{d,10}}}, {x_,y_}/;!ListQ(x)&&!ListQ(y))", //
        "{{a,b}}");
  }

  @Test
  public void testPermutations() {
    // TODO
    // check("Permutations({1, 2, 1} )", //
    // "");

    // TODO
    // check(
    // "\"ABCA\" // Characters // Permutations ", //
    //
    // "{{A,A,B,C},{A,A,C,B},{A,B,A,C},{A,B,C,A},{A,C,A,B},{A,C,B,A},{B,A,A,C},{B,A,C,A},{B,C,A,A},{C,A,A,B},{C,A,B,A},{C,B,A,A}}");
    check("Permutations(x^2,{3})", //
        "{}");
    check("Permutations(x^2,{2})", //
        "{x^2,2^x}");
    check("Permutations(x^2,{1})", //
        "{x,2}");
    check("Permutations(x^2,{0})", //
        "{{}}");
    check("Permutations(x^2,{-1})", //
        "Permutations(x^2,{-1})");
    check("Permutations({1, 2, 3}, 2)", //
        "{{},{1},{2},{3},{1,2},{1,3},{2,1},{2,3},{3,1},{3,2}}");
    check("Permutations({1, 2, 3}, {2})", //
        "{{1,2},{1,3},{2,1},{2,3},{3,1},{3,2}}");
    check("Permutations({a,b,c})", //
        "{{a,b,c},{a,c,b},{b,a,c},{b,c,a},{c,a,b},{c,b,a}}");
    check("Permutations({a,b,c}, {2})", //
        "{{a,b},{a,c},{b,a},{b,c},{c,a},{c,b}}");

    check("Permutations({a},{0})", //
        "{{}}");
    check("Permutations({a,b,c,d},{3})", //
        "{{a,b,c},{a,b,d},{a,c,b},{a,c,d},{a,d,b},{a,d,c},{b,a,c},{b,a,d},{b,c,a},{b,c,d},{b,d,a},{b,d,c},{c,a,b},{c,a,d},{c,b,a},{c,b,d},{c,d,a},{c,d,b},{d,a,b},{d,a,c},{d,b,a},{d,b,c},{d,c,a},{d,c,b}}");
    check("Permutations({a,a,b})", //
        "{{a,a,b},{a,b,a},{b,a,a}}");
    check("Permutations({a,a,b,b})", //
        "{{a,a,b,b},{a,b,a,b},{a,b,b,a},{b,a,a,b},{b,a,b,a},{b,b,a,a}}");
    check("Permutations({a,a,b,b},{3})", //
        "{{a,a,b},{a,b,a},{a,b,b},{b,a,a},{b,a,b},{b,b,a}}");
  }

  @Test
  public void testPi() {
    check("N(E, 10)", //
        "2.718281828");
    check("N(Pi, 10)", //
        "3.141592654");
    check("N(Pi, 11)", //
        "3.1415926536");
    check("N(Pi, 12)", //
        "3.14159265359");
    check("N(Pi, 13)", //
        "3.14159265359");
    check("N(Pi, 14)", //
        "3.1415926535898");
    check("N(Pi, 15)", //
        "3.14159265358979");
    check("N(Pi, 16)", //
        "3.141592653589793");
    check("N(Pi, 17)", //
        "3.1415926535897932");
    check("N(Pi, 18)", //
        "3.14159265358979323");
    check("N(Pi, 19)", //
        "3.141592653589793238");
    check("N(Pi, 20)", //
        "3.1415926535897932384");
  }

  @Test
  public void testPick() {
    check("Pick({a, b, c}, {False, True, False})", //
        "{b}");
    check("Pick(f(g(1, 2), h(3, 4)), {{True, False}, {False, True}})", //
        "f(g(1),h(4))");
    check("Pick({a, b, c, d, e}, {1, 2, 3.5, 4, 5.5}, _Integer)", //
        "{a,b,d}");

    // check(
    // "Pick(<|s1-><|a->0,b:>1|>,s2:><|a->0,b:>1|>|>,(1+Sqrt(5))/2,5)", //
    // "{RuleDelayed(Association(0))}");
    check("Pick({{1, 2}, {2, 3}, {5, 6}}, {{1}, {2, 3}, {{3, 4}, {4, 5}}}, {1} | 2 | {4, 5})", //
        "{{1,2},{2},{6}}");
    // message: Pick: Expressions {{1,2},{2,3},{5,6}} and {{1},{2,3},{{3,4},{4,5}}} have
    // incompatible shapes.
    check("Pick({{1, 2}, {2, 3}, {5, 6}}, {{1}, {2, 3}, {{3, 4}, {4, 5}}}, {4, 5})", //
        "Pick({{1,2},{2,3},{5,6}},{{1},{2,3},{{3,4},{4,5}}},{4,5})");

    check("Pick({2, {3, 4}}, {3, {4, 5}}, 5)", //
        "{{4}}");
    check("Pick({2, {3, 4}}, {3, {4, 5}}, {4, 5})", //
        "{{3,4}}");

    check("data = SparseArray({{1, 3} -> 2, {2, 2} -> 3, {3, 3} -> 5})", //
        "SparseArray(Number of elements: 3 Dimensions: {3,3} Default value: 0)");
    check("Pick(data, IdentityMatrix(3), 1)", //
        "{{0},{3},{5}}");

    check("sel = SparseArray({1 -> True, 100 -> True, _ -> False}, 100)", //
        "SparseArray(Number of elements: 2 Dimensions: {100} Default value: False)");
    check("Pick(Range(100), sel)", //
        "{1,100}");
    check("Pick(Characters(\"ABC\"), {True, False, True})", //
        "{A,C}");
    check("Pick({a, b, c, d, e}, {1, 0, 1, 0, 0}, Except(1, _Integer))", //
        "{b,d,e}");
    check("Pick({a, b, c, d, e}, {1, 0, 1, 0, 0}, Except(1))", //
        "{a,b,c,d,e}");
    check("Pick({1, 2, 3}, False)", //
        "Identity()");
    check("Pick(x, {1, 2, 3}, _?NumericQ)", //
        "Identity()");
    check("Pick(x, {1, 2, 3}, _List)", //
        "x");
    check("Pick({a, b, c, d, e}, {1, 0, 1, 0, 0}, 1)", //
        "{a,c}");
    check("Pick({{a, b, c}, {d, e, f}}, {{1, 0, 0}, {0, 1, 1}}, 1)", //
        "{{a},{e,f}}");
    check("Pick({a, b, c, d}, {True, False, False, True})", //
        "{a,d}");
    check("Pick({a, b, c, d}, {1, 2, 3, 4}, 2 | 4)", //
        "{b,d}");

    check("Pick(f(1, 2, 3, 4, 5, 6), {1, 0, 1, 0, 1, 1}, 1)", //
        "f(1,3,5,6)");
  }

  @Test
  public void testPiecewise() {
    // message Piecewise: The first argument x of Piecewise is not a list of pairs.
    check("Piecewise(x)", //
        "Piecewise(x)");
    check("Piecewise({{(-1)^(1+n)/n,n>=1},{0,n==0}},0)", //
        "Piecewise({{(-1)^(1+n)/n,n>=1}},0)");
    check("Piecewise({{x^2, x < 0}, {x, x > 0}})", //
        "Piecewise({{x^2,x<0},{x,x>0}},0)");
    check("Piecewise({{-Log(x),x<=(0+(-1)*0)*1/1}},Log(x))", //
        "Piecewise({{-Log(x),x<=0}},Log(x))");
    check("Piecewise({{x^2, x < 0}, {x, x > 0}}) /. x->3", //
        "3");
    check("Piecewise({{(a^0*Log(a)^n)/n!,n>=0}},0)", //
        "Piecewise({{Log(a)^n/n!,n>=0}},0)");
    check("Piecewise({})", //
        "0");
    check("Piecewise({},0)", "0");
    check("Piecewise({{0, x <= 0}}, 1)", //
        "Piecewise({{0,x<=0}},1)");
    check("Piecewise({{1, False}})", //
        "0");
    check("Piecewise({{0 ^ 0, False}}, -1)", //
        "-1");

    check(
        "$pw = Piecewise({{Sin(x)/x, x < 0}, {1, x == 0}}, -x^2/100 + 1); $pw /. {{x -> -5}, {x -> 0}, {x -> 5}}", //
        "{Sin(5)/5,1,3/4}");
    check("Piecewise({{e1, True}, {e2, d2}, {e3, d3}}, e0)", //
        "e1");
    check("Piecewise({{e1, d1}, {e2, d2}, {e3, True}, {e4, d4}, {e5, d5}}, e0)", //
        "Piecewise({{e1,d1},{e2,d2}},e3)");
    check("Piecewise({{e1, d1}, {e2, d2}, {e3, d2 && d3}, {e4, d4}}, e0)", //
        "Piecewise({{e1,d1},{e2,d2},{e3,d2&&d3},{e4,d4}},e0)");
    check("Piecewise({{e1, d1}, {e2, d2}, {e3, False}, {e4, d4}, {e5, d5}}, e0)", //
        "Piecewise({{e1,d1},{e2,d2},{e4,d4},{e5,d5}},e0)");
  }

  @Test
  public void testPiecewiseExpand() {
    check("PiecewiseExpand(KroneckerDelta(x, y,z,u,v))", //
        "Piecewise({{1,u-v==0&&v-x==0&&x-y==0&&y-z==0}},0)");

    // PiecewiseExpand
    check("PiecewiseExpand(f(x) + Piecewise({{g(x),x>=0}},0) + z(x) )", //
        "Piecewise({{f(x)+g(x)+z(x),x>=0}},f(x)+z(x))");
    check("PiecewiseExpand(f(x) + Piecewise({{g(x),x>=0}},0) )", //
        "Piecewise({{f(x)+g(x),x>=0}},f(x))");

    check("PiecewiseExpand(f(x) * Piecewise({{g(x),x>=0}},0) * z(x) )", //
        "Piecewise({{f(x)*g(x)*z(x),x>=0}},0)");
    check("PiecewiseExpand(f(x) * Piecewise({{g(x),x>=0}},0) )", //
        "Piecewise({{f(x)*g(x),x>=0}},0)");
    check("PiecewiseExpand(f(x)*Boole(x))", //
        "Piecewise({{f(x),x}},0)");
    check("PiecewiseExpand(KroneckerDelta(x))", //
        "Piecewise({{1,x==0}},0)");
    check("PiecewiseExpand(DiscreteDelta(x))", //
        "Piecewise({{1,x==0}},0)");
    check("PiecewiseExpand(KroneckerDelta(x, y))", //
        "Piecewise({{1,x-y==0}},0)");
    check("PiecewiseExpand(DiscreteDelta(x, y))", //
        "Piecewise({{1,x==0&&y==0}},0)");
    check("PiecewiseExpand(KroneckerDelta(x, y,z,u,v))", //
        "Piecewise({{1,u-v==0&&v-x==0&&x-y==0&&y-z==0}},0)");
    check("PiecewiseExpand(DiscreteDelta(x, y,z,u,v))", //
        "Piecewise({{1,u==0&&v==0&&x==0&&y==0&&z==0}},0)");
    check("PiecewiseExpand(Abs(x), Reals)", //
        "Piecewise({{-x,x<0}},x)");
    check("PiecewiseExpand(Arg(x), Reals)", //
        "Piecewise({{Pi,x<0}},0)");
    check("PiecewiseExpand(Sign(x), Reals)", //
        "Piecewise({{-1,x<0},{1,x>0}},0)");
    check("PiecewiseExpand(Abs(x))", //
        "Abs(x)");
    check("PiecewiseExpand(Clip(x))", //
        "Piecewise({{-1,x<-1},{1,x>1}},x)");
    check("PiecewiseExpand(Clip(x,{-7,5}))", //
        "Piecewise({{-7,x<-7},{5,x>5}},x)");
    check("PiecewiseExpand(If(x, y, z))", //
        "Piecewise({{y,x}},z)");

    check("PiecewiseExpand(UnitStep(x, y, z))", //
        "Piecewise({{1,x>=0&&y>=0&&z>=0}},0)");

    check("PiecewiseExpand(Ramp(x))", //
        "Piecewise({{x,x>=0}},0)");
  }

  @Test
  public void testPlus() {
    check("(1*Plus+0)[10,20]", //
        "30");

    check("-2*Cosh(x)^2+5*Sinh(x)^2", //
        "-2+3*Sinh(x)^2");
    check("a+3*Cos(x)^2-2*Cosh(x)^2+11*Sin(x)^2+5*Sinh(x)^2", //
        "1+a+8*Sin(x)^2+3*Sinh(x)^2");

    check("Cosh(Log(3)/2)^2-Sinh(Log(3)/2)^2", //
        "1");
    check("-Cosh(x)^2-Sinh(x)^2", //
        "-Cosh(x)^2-Sinh(x)^2");
    check("Cosh(x)^2+Sinh(x)^2", //
        "Cosh(x)^2+Sinh(x)^2");
    check("Cosh(x)^2-Sinh(x)^2", //
        "1");
    check("7*Cosh(x)^2-2*Sinh(x)^2", //
        "2+5*Cosh(x)^2");
    check("2*Cosh(x)^2-5*Sinh(x)^2", //
        "2-3*Sinh(x)^2");

    check("-Cosh(x)^2+Sinh(x)^2", //
        "-1");
    check("-7*Cosh(x)^2+2*Sinh(x)^2", //
        "-2-5*Cosh(x)^2");

    check("x+1/(3!*E)-Infinity+1/(5!*E)+1/(6!*E)", //
        "-Infinity+x");
    check("Refine(Infinity+x, x>0)", //
        "Infinity");

    check("1+I", //
        "1+I");
    check("Infinity - Infinity", //
        "Indeterminate");
    check("-I+y+Interval({c,d})", //
        "-I+y+Interval({c,d})");
    check("-I+<|x->y|>+Interval({c,d})", //
        "<|x->-I+y+Interval({c,d})|>");
    check("Sin(0.1851851851851852*Cos(7/11)*x)", //
        "Sin(0.148937*x)");

    check("1-{0,1,2}", //
        "{1,0,-1}");
    check("-Infinity+0.0", //
        "-Infinity");
    check("Infinity+0.0", //
        "Infinity");
    check("-Infinity+Sin(-2)", //
        "-Infinity");
    check("-Infinity+Log(2)", //
        "-Infinity");
    check("Infinity+Sin(-2)", //
        "Infinity");
    check("Infinity+Log(2)", //
        "Infinity");

    check("{1,2}+{4,5,6}", //
        "{1,2}+{4,5,6}");
    // check("2+4/3*2^b/c", //
    // "2+2^(2+b)/(3*c)");
    check("Refine(Infinity+x, x>0)", //
        "Infinity");

    // String s = System.getProperty("os.name");
    // if (s.contains("Windows")) {
    check("N(Pi, 30) + N(E, 30)", //
        "5.85987448204883847382293085463");
    check("N(Pi, 30) + N(E, 30) // Precision", //
        "30");
    check("N(Pi, 30) + I", //
        "3.14159265358979323846264338327+I*1");
    check("N(Pi, 30) + E", //
        "5.85987448204883847382293085463");
    // }
    check("1 + 2", //
        "3");
    check("a + b + a", //
        "2*a+b");
    check("a + a + 3 * a", //
        "5*a");
    check("a + b + 4.5 + a + b + a + 2 + 1.5*b", //
        "6.5+3*a+3.5*b");
    check("Plus @@ {2, 4, 6}", //
        "12");
    check("Plus @@ Range(1000)", //
        "500500");
    check("a /. n_. + x_ :> {n, x}", //
        "{0,a}");
    check("-2*a - 2*b", //
        "-2*a-2*b");
    check("1 - I * Sqrt(3)", //
        "1-I*Sqrt(3)");
    check("Head(3 + 2*I)", //
        "Complex");

    check("Interval({1,6})+Interval({0,2})", //
        "Interval({1,8})");
    check("Interval({a,b})+z", //
        "z+Interval({a,b})");
    check("(Interval({-1,1})+1/2)^2 - 1/4", //
        "Interval({-1/4,2})");
    check("f+Interval({a,b})+Interval({c,d})", //
        "f+Interval({a+c,b+d})");
    check("Interval({a,b})+Interval({c,d})", //
        "Interval({a+c,b+d})");
    check("1+Interval({2,3})", //
        "Interval({3,4})");
    check("Plus()", //
        "0");
  }

  @Test
  public void testPlusMinus() {
    check("\\[PlusMinus] x", //
        "±x");
    check("x \\[PlusMinus] y", //
        "x±y");
  }



  @Test
  public void testPolyLog() {
    // check("PolyLog(10007,-1.5707963267948966)", //
    // "");
    check("PolyLog(-7.0, I)", //
        "136.0");
    check("PolyLog(1, 3, z)", //
        "Pi^4/90-1/6*Log(1-z)^3*Log(z)-1/2*Log(1-z)^2*PolyLog(2,1-z)+Log(1-z)*PolyLog(3,1-z)-PolyLog(\n" //
            + "4,1-z)");
    check("PolyLog(1, 4, z)", //
        "1/24*Log(1-z)^4*Log(z)+1/6*Log(1-z)^3*PolyLog(2,1-z)-1/2*Log(1-z)^2*PolyLog(3,1-z)+Log(\n" //
            + "1-z)*PolyLog(4,1-z)-PolyLog(5,1-z)+Zeta(5)");
    check("PolyLog(-42,Infinity)", //
        "Indeterminate");
    check("PolyLog(0,Infinity)", //
        "Indeterminate");
    check("PolyLog(2,Infinity)", //
        "-Infinity");
    check("PolyLog(42,-Infinity)", //
        "-Infinity");

    checkNumeric("PolyLog(1, 0.333333)", //
        "0.4054646081082894");


    checkNumeric("PolyLog(2, 0.9)", //
        "1.2997147230049588");
    checkNumeric("PolyLog(0, 5.0)", //
        "-1.25");

    checkNumeric("N(PolyLog(1, 1/3), 50)", //
        "0.40546510810816438197801311546434913657199042346249");
    checkNumeric("PolyLog(2, 0.300000000000000000)", //
        "0.326129510075476069");
    checkNumeric("PolyLog(0.2 + I, 0.5 - I)", //
        "-0.08985258966284129+I*(-0.5958648241210646)");

    // issue #929
    // message Infinite or NaN number in z1 calculation.
    checkNumeric("(PolyLog(2,E^(I*1/270*Pi^2)))/Pi // N", //
        "0.5054280619497805+I*0.0501372676125785");
    checkNumeric("PolyLog(2,0.9993319736282411 + 0.03654595031305655*I)", //
        "1.5878490863395573+I*0.1575108716027421");

    check("PolyLog(2,z) + PolyLog(2,1-z)", //
        "Pi^2/6-Log(1-z)*Log(z)");
    check("PolyLog(2,Sin(7)) + PolyLog(2,1-Sin(7))", //
        "Pi^2/6-Log(1-Sin(7))*Log(Sin(7))");

    check("PolyLog(2,E^(4*I*Pi))", //
        "Pi^2/6");
    check("PolyLog(2,E^(42*I*Pi))", //
        "Pi^2/6");
    check("PolyLog(2,E^(41*I*Pi))", //
        "-Pi^2/12");



    // TODO https://github.com/mtommila/apfloat/issues/34
    // check("PolyLog(2147483647,-3.1415)", //
    // " ");
    check("PolyLog(2147483647,1)", //
        "Zeta(2147483647)");

    check("PolyLog(n,1,z)", //
        "PolyLog(1+n,z)");
    check("PolyLog(0, 1, z)", //
        "-Log(1-z)");

    check("PolyLog(-4,z)", //
        "(z+11*z^2+11*z^3+z^4)/(1-z)^5");
    check("PolyLog(-2147483648,2.718281828459045)", //
        "PolyLog(-2.14748*10^9,2.71828)");

    check("PolyLog(2,0)", //
        "0");
    check("PolyLog(2,-1)", //
        "-Pi^2/12");
    check("PolyLog(2,1)", //
        "Pi^2/6");
    check("PolyLog(2,1/2)", //
        "Pi^2/12-Log(2)^2/2");
    check("PolyLog(2,2)", //
        "Pi^2/4-I*Pi*Log(2)");
    check("PolyLog(2,I)", //
        "I*Catalan-Pi^2/48");
    check("PolyLog(2,-I)", //
        "-I*Catalan-Pi^2/48");
    check("PolyLog(2,1-I)", //
        "-I*Catalan+Pi^2/16-I*1/4*Pi*Log(2)");
    check("PolyLog(2,1+I)", //
        "I*Catalan+Pi^2/16+I*1/4*Pi*Log(2)");
    check("PolyLog(3,1)", //
        "Zeta(3)");
    check("PolyLog(f(x),-1)", //
        "(-1+2^(1-f(x)))*Zeta(f(x))");
    check("PolyLog(0,f(x))", //
        "f(x)/(1-f(x))");
    check("PolyLog(1,f(x))", //
        "-Log(1-f(x))");
    check("PolyLog(-1,f(x))", //
        "f(x)/(1-f(x))^2");
    check("PolyLog(-2,f(x))", //
        "(f(x)+f(x)^2)/(1-f(x))^3");
    check("PolyLog(-3,f(x))", //
        "(f(x)+4*f(x)^2+f(x)^3)/(1-f(x))^4");

    check("Table(PolyLog(n, 1), {n, 1, 4})", //
        "{Infinity,Pi^2/6,Zeta(3),Pi^4/90}");
    check("Table(PolyLog(n, z),  {n, -2, 1})", //
        "{(z+z^2)/(1-z)^3,z/(1-z)^2,z/(1-z),-Log(1-z)}");
    check("PolyLog(n,-1)", //
        "(-1+2^(1-n))*Zeta(n)");
    checkNumeric("PolyLog(1,1)", //
        "Infinity");
    checkNumeric("PolyLog(1.0,1.0)", //
        "Infinity");
    checkNumeric("Table(PolyLog(1.0,z), {z,-2.0,2.0,0.1})", //
        "{-1.0986122886681098,-1.0647107369924282,-1.0296194171811581,-0.9932517730102833,-0.9555114450274362," //
            + "-0.9162907318741549,-0.8754687373538997,-0.8329091229351038,-0.7884573603642698,-0.7419373447293769," //
            + "-0.6931471805599448,-0.6418538861723944,-0.5877866649021186,-0.5306282510621699,-0.4700036292457351," //
            + "-0.40546510810816394,-0.33647223662121256,-0.26236426446749056,-0.18232155679395404,-0.09531017980432434," //
            + "0.0,0.10536051565782702,0.22314355131421054,0.3566749439387334,0.5108256237659918,0.6931471805599466,0.9162907318741567,1.2039728043259381,1.6094379124341034,2.3025850929940517," //
            + "Indeterminate,2.3025850929940384+I*(-3.141592653589793),1.609437912434096+I*(-3.141592653589793),1.2039728043259328+I*(-3.141592653589793),0.9162907318741526+I*(-3.141592653589793),0.6931471805599431+I*(-3.141592653589793),0.5108256237659887+I*(-3.141592653589793),0.35667494393873056+I*(-3.141592653589793),0.22314355131420804+I*(-3.141592653589793),0.10536051565782467+I*(-3.141592653589793),-1.332267629550187E-15+I*(-3.141592653589793)}");
    checkNumeric("Table(N(PolyLog(1,z),20), {z,-2,2,1/10})", //
        "{-1.0986122886681096913,-1.0647107369924283431,-1.0296194171811582399,-0.99325177301028339016,-0.95551144502743636145,-0.91629073187415506518,-0.87546873735389993562,-0.83290912293510400678,-0.78845736036427016946,-0.74193734472937731248,Indeterminate,-0.64185388617239477599,-0.58778666490211900818,-0.53062825106217039623,-0.47000362924573555365,-0.40546510810816438197,-0.3364722366212129305,-0.26236426446749105203,-0.18232155679395462621,-0.095310179804324860043,"
            + "0,0.10536051565782630122,0.22314355131420975576,0.35667494393873237891,0.5108256237659906832,0.69314718055994530941,0.91629073187415506518,1.2039728043259359926,1.6094379124341003746,2.302585092994045684,"
            + "Infinity,2.302585092994045684+I*(-3.1415926535897932384),1.6094379124341003746+I*(-3.1415926535897932384),1.2039728043259359926+I*(-3.1415926535897932384),0.9162907318741550651+I*(-3.1415926535897932384),0.6931471805599453094+I*(-3.1415926535897932384),0.5108256237659906832+I*(-3.1415926535897932384),0.3566749439387323789+I*(-3.1415926535897932384),0.2231435513142097557+I*(-3.1415926535897932384),0.1053605156578263012+I*(-3.1415926535897932384),I*(-3.1415926535897932384)}");

  }

  @Test
  public void testPolynomialExtendedGCD() {
    check("PolynomialExtendedGCD(e*x^2 + d, ( -2*d*e^2*Sqrt(-e/d) )*x + 2*d*e^2, x )", //
        "{-1/Sqrt(-e/d)+x,{0,-1/(2*d*e^2*Sqrt(-e/d))}}");

    check("PolynomialExtendedGCD(e*x^2+d,-2*d*e^2*Sqrt(-e/d)*x+2*d*e^2,z)", //
        "{1,{0,1/(2*d*e^2-2*d*e^2*Sqrt(-e/d)*x)}}");
    check("PolynomialExtendedGCD(e*x^2+d,-2*d*e^2*Sqrt(-e/d)*x+2*d*e^2,Infinity)", //
        "PolynomialExtendedGCD(e*x^2+d,-2*d*e^2*Sqrt(-e/d)*x+2*d*e^2,Infinity)");
    check("PolynomialExtendedGCD({},3*x,f(x,y))", //
        "PolynomialExtendedGCD({},3*x,f(x,y))");
    check("PolynomialExtendedGCD(e*x^2+d,-2*d*e^2*Sqrt(-e/d)*x+2*d*e^2,0)", //
        "PolynomialExtendedGCD(e*x^2+d,-2*d*e^2*Sqrt(-e/d)*x+2*d*e^2,0)");
    check("PolynomialExtendedGCD(Infinity,3*x,f(x,y))", //
        "PolynomialExtendedGCD(Infinity,3*x,f(x,y))");
    check("PolynomialExtendedGCD(2*x^2+3,3*x,f(x,y))", //
        "{1,{0,1/(3*x)}}");
    check("PolynomialExtendedGCD(a[x],b[x],x)", //
        "{b(x),{0,1}}");
    check("PolynomialExtendedGCD(a[x],b,x)", //
        "{1,{0,1/b}}");
    check("PolynomialExtendedGCD(a,b,x)", //
        "{1,{0,1/b}}");

    // TODO make result consistent with PolynomiaGCD
    check("PolynomialExtendedGCD(e*x^2 + d, ( -2*d*e^2*Sqrt(-e/d) )*x + 2*d*e^2, x )", //
        "{-1/Sqrt(-e/d)+x,{0,-1/(2*d*e^2*Sqrt(-e/d))}}");

    // Wikipedia: finite field GF(28) - p = x8 + x4 + x3 + x + 1, and a = x6 + x4 +
    // x + 1
    check("PolynomialExtendedGCD(x^8 + x^4 + x^3 + x + 1, x^6 + x^4 + x + 1, x, Modulus->2)", //
        "{1,{1+x^2+x^3+x^4+x^5,x+x^3+x^6+x^7}}");

    // check("PolynomialExtendedGCD((x - a)*(b*x - c)^2, (x - a)*(x^2 -
    // b*c), x)", "");
    check("PolynomialExtendedGCD((x - 1)*(x - 2)^2, (x - 1)*(x^2 - 3), x)", //
        "{-1+x,{7+4*x,9-4*x}}");

    check("PolynomialExtendedGCD((x - 1)^2*(x - 2)^2, (x - 1)*(x^2 - 3), x)", //
        "{-1+x,{1/2*(19+11*x),1/2*(-26+36*x-11*x^2)}}");
    check("PolynomialExtendedGCD((x - 1)^2*(x - 2)^2, (x - 1)*(x^2 - 3), x,  Modulus -> 2)", //
        "{1+x^2,{1,1+x}}");

    check("PolynomialExtendedGCD(a*x^2 + b*x + c, x - r, x)", //
        "{1,{1/(c+b*r+a*r^2),(-b-a*r-a*x)/(c+b*r+a*r^2)}}");
  }

  @Test
  public void testPolynomialGCD() {
    // TODO https://github.com/kredel/java-algebra-system/issues/15
    check("PolynomialGCD(-1/2,x^2-5*x+(-1)*6)", //
        "1/2");

    check("PolynomialGCD(3*x+9,-3.14159)", //
        "PolynomialGCD(9+3*x,-3.14159)");

    check("PolynomialGCD({},0.5,x^4+(-1)*1,x^5+(-1)*1,x^6+(-1)*1,x^7+(-1)*1)", //
        "{}");
    check("PolynomialGCD(f(x),f(x)*x^2)", //
        "f(x)");

    // wikipedia example https://en.wikipedia.org/wiki/Polynomial_greatest_common_divisor
    check("PolynomialGCD(x^2 + 7*x + 6, x^2-5*x-6)", //
        "1+x");

    check("PolynomialGCD(a,b )", //
        "1");
    check("PolynomialGCD(e*x^2 + d, ( -2*d*e^2*Sqrt(-e/d) )*x + 2*d*e^2 )", //
        "1");

    // TODO difference to MMA handle Extension->Automatic correctly
    check("PolynomialGCD(x^2 - 2, x - Sqrt(2))", //
        "-Sqrt(2)+x");

    // check("PolynomialGCD(I*2,12)", "2");
    check("PolynomialGCD(a+b*x,c+d*x)", //
        "1");
    check("PolynomialGCD()", //
        "PolynomialGCD()");
    check("PolynomialGCD(x)", //
        "x");
    check("PolynomialGCD(-12)", //
        "12");
    check("PolynomialGCD(x,x)", //
        "x");

    check("PolynomialGCD((x + 1)^3, x^3 + x, Modulus -> 2)", //
        "(1+x)^2");
    check("PolynomialGCD((x - a)*(b*x - c)^2, (x - a)*(x^2 - b*c))", //
        "-a+x");
    check("PolynomialGCD((1 + x)^2*(2 + x)*(4 + x), (1 + x)*(2 + x)*(3 + x))", //
        "2+3*x+x^2");
    check("PolynomialGCD(x^4 - 4, x^4 + 4*x^2 + 4)", //
        "2+x^2");

    check("PolynomialGCD(x^2 + 2*x*y + y^2, x^3 + y^3)", //
        "x+y");
    check("PolynomialGCD(x^2 - 1, x^3 - 1, x^4 - 1, x^5 - 1, x^6 - 1, x^7 - 1)", //
        "-1+x");

    check("PolynomialGCD(x^2 - 4, x^2 + 4*x + 4)", //
        "2+x");
    check("PolynomialGCD(3*x + 9, 6*x^3 - 3*x + 12)", //
        "3");
  }

  @Test
  public void testPolynomialLCM() {
    // TODO https://github.com/kredel/java-algebra-system/issues/15
    check("PolynomialLCM(x^2+7*x+6,-1/2)", //
        "-6-7*x-x^2");
    check("PolynomialGCD(1/2,2)", //
        "1/2");
    check("PolynomialLCM(1/2,2)", //
        "2");
    check("PolynomialLCM({},0.5,x^4+(-1)*1,x^5+(-1)*1,x^6+(-1)*1,x^7+(-1)*1)", //
        "{}");
    check("PolynomialLCM(f(x)*y^3,f(x)*x^2)", //
        "x^2*y^3*f(x)");
    // wikipedia example https://en.wikipedia.org/wiki/Polynomial_greatest_common_divisor
    check("PolynomialLCM(x^2 + 7*x + 6, x^2-5*x-6)", //
        "-36-36*x+x^2+x^3");

    check("PolynomialLCM(a+b*x,c+d*x)", //
        "(a+b*x)*(c+d*x)");
    check("PolynomialLCM()", //
        "PolynomialLCM()");
    check("PolynomialLCM(x)", //
        "x");
    check("PolynomialLCM(x,x)", //
        "x");
    check("PolynomialLCM(-12)", //
        "12");

    check(
        "Expand((-1+x)*(1+x)*(1+x^2)*(1-x+x^2)*(1+x+x^2)*(1+x+x^2+x^3+x^4)*(1+x+x^2+x^3+x^4+x^5+x^6))", //
        "-1-2*x-4*x^2-6*x^3-8*x^4-9*x^5-9*x^6-7*x^7-4*x^8+4*x^10+7*x^11+9*x^12+9*x^13+8*x^\n"
            + "14+6*x^15+4*x^16+2*x^17+x^18");

    check("PolynomialLCM((1 + x)^2*(2 + x)*(4 + x), (1 + x)*(2 + x)*(3 + x))", //
        "24+74*x+85*x^2+45*x^3+11*x^4+x^5");
    check("Expand((1+x)^2*(2+x)*(3+x)*(4+x))", //
        "24+74*x+85*x^2+45*x^3+11*x^4+x^5");

    check("PolynomialLCM(x^2 + 2*x*y + y^2, x^3 + y^3)", //
        "x^4+x^3*y+x*y^3+y^4");
    check("Expand((x+y)*(x^3+y^3))", //
        "x^4+x^3*y+x*y^3+y^4");

    check("PolynomialLCM(x^2 - 1, x^3 - 1, x^4 - 1, x^5 - 1, x^6 - 1, x^7 - 1)", //
        "-1-2*x-4*x^2-6*x^3-8*x^4-9*x^5-9*x^6-7*x^7-4*x^8+4*x^10+7*x^11+9*x^12+9*x^13+8*x^\n"
            + "14+6*x^15+4*x^16+2*x^17+x^18");
  }

  @Test
  public void testPolynomialQ() {
    check("PolynomialQ(x^(1/2) + 6*Sin(x))", //
        "True");
    check("Variables(x^(1/2) + 6*Sin(x))", //
        "{x,Sin(x)}");
    check("PolynomialQ(1/x(1)^2, x(1))", //
        "False");
    check("PolynomialQ(1/x(1)^2)", //
        "False");
    check("PolynomialQ(x(1)^2)", //
        "True");
    check("PolynomialQ(y(1)^3/6+y(3)/3 + y(1)*y(2)/2)", //
        "True");
    check("PolynomialQ(x, 1234)", //
        "True");

    // message: General: x+y is not a valid variable.
    check("PolynomialQ(x, x+y)", //
        "PolynomialQ(x,x+y)");

    check("PolynomialQ(7*y^w, y )", //
        "False");
    check("PolynomialQ(7*y^(3*w), y )", //
        "False");
    check("PolynomialQ(c*x^(-2)+a+b*x,x)", //
        "False");

    check("PolynomialQ(x^2 + 7*x + 6)", //
        "True");
    check("PolynomialQ(x^(1/2) + 6*Sin(x), {})", //
        "True");

    check("PolynomialQ(Cos(x*y), Cos(x*y))", //
        "True");
    check("PolynomialQ(x^3,x^2)", //
        "False");
    check("PolynomialQ(2*x^3,x^2)", //
        "False");

    check("PolynomialQ(2*x,x^2)", //
        "False");
    check("PolynomialQ(x,x^2)", //
        "False");
    check("PolynomialQ(x^2,x^2)", //
        "True");

    check("PolynomialQ(3*a,x)", //
        "True");
    check("PolynomialQ(x^2*y^3+34*x^2+7-Sin(z^3)*x^34, {x,y})", //
        "True");
    check("PolynomialQ(x^2*y^3+34*x^2+7-Sin(z^3)*x^34, {x,y,z})", //
        "False");
    check("PolynomialQ(E^f(x),x)", //
        "False");
    check("PolynomialQ(E^f(y),x)", //
        "True");
    check("PolynomialQ(Tan(x),x)", //
        "False");
    check("PolynomialQ(f(x), x)", //
        "False");
    check("PolynomialQ(f(a)+f(a)^2, f(a))", //
        "True");
    check("PolynomialQ(Sin(f(a))+f(a)^2, f(a))", //
        "False");
    check("PolynomialQ(x^3 - 2*x/y + 3*x*z, x)", //
        "True");
    check("PolynomialQ(x^3 - 2*x/y + 3*x*z, y)", //
        "False");
    check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {x, y})", //
        "True");
    check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {a, b, c})", //
        "False");

    check("PolynomialQ((1+x)^3*(1-y-x)^2, x)", //
        "True");
    check("PolynomialQ(x+Sin(x), x)", //
        "False");
    check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {x, y})", //
        "True");
    check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {a, b, c})", //
        "False");

    check("PolynomialQ(f(a)+f(a)^2,f(a))", //
        "True");

    check("PolynomialQ(I*x^(3)+(x+2)^2,x)", //
        "True");
    check("PolynomialQ(I,x)", //
        "True");
    check("PolynomialQ(a,x)", //
        "True");
    check("PolynomialQ(a,{x,y,z})", //
        "True");
    check("PolynomialQ(x,x)", //
        "True");
    check("PolynomialQ(x,{x,y,z})", //
        "True");
  }

  @Test
  public void testPolynomialQuotient() {
    check("PolynomialQuotient(x^2+4*x+1,-10,x,Modulus->2)", //
        "PolynomialQuotient(1+4*x+x^2,-10,x,Modulus->2)");

    // check(
    // "PolynomialQuotientRemainder((2+x^2+x^3)/x,1-x^2,x)", //
    // "(2-x)/x");
    check("PolynomialQuotient(0,2,x,Modulus->2)", //
        "PolynomialQuotient(0,2,x,Modulus->2)");

    check("PolynomialQuotient(x^2+4*x+1,Indeterminate,x,Modulus->3)", //
        "PolynomialQuotient(1+4*x+x^2,Indeterminate,x,Modulus->3)");
    check("PolynomialQuotient(x^2,1+(-1)*1,x)", //
        "PolynomialQuotient(x^2,0,x)");
    check("PolynomialQuotient(x^2 + 7*x + 6, x^2-5*x-6, x)", //
        "1");
    check("PolynomialQuotient(a+b*x,1,x)^3", //
        "(a+b*x)^3");
    check("PolynomialQuotient(x^2, x + a,x)", //
        "-a+x");
    check("PolynomialQuotient(x^2 + x + 1, 2*x + 1, x)", //
        "1/4+x/2");
    check("PolynomialQuotient(x^2 + b*x + 1, a*x + 1, x)", //
        "-1/a^2+b/a+x/a");
    check("PolynomialQuotient(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 2)", //
        "1+x^2");
    check("PolynomialQuotient(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 3)", //
        "1+2*x");
  }

  @Test
  public void testPolynomialQuotientRemainder() {
    check("PolynomialQuotientRemainder(1+b*x+x^2,-2147483648,x)", //
        "{-1/2147483648-1/2147483648*b*x-x^2/2147483648,0}");

    check("PolynomialQuotientRemainder(x^2 + x + 1, Pi*x + 1, x)", //
        "{-1/Pi^2+1/Pi+x/Pi,1+1/Pi^2-1/Pi}");
    check("PolynomialQuotientRemainder(x^2,1+(-1)*1,x)", //
        "PolynomialQuotientRemainder(x^2,0,x)");
    // test with Integer.MIN_VALUE
    check("PolynomialQuotientRemainder(1+b*x+x^2,-2147483648,x)", //
        "{-1/2147483648-1/2147483648*b*x-x^2/2147483648,0}");
    check("PolynomialQuotientRemainder(1+b*x+x^2,-2147483646,x)", //
        "{-1/2147483646-1/2147483646*b*x-x^2/2147483646,0}");

    check("PolynomialQuotientRemainder(6+7*x+x^2,Indeterminate,x)", //
        "PolynomialQuotientRemainder(6+7*x+x^2,Indeterminate,x)");

    check("PolynomialQuotientRemainder(2*x^2+3,3*x,f(x,y))", //
        "{(3+2*x^2)/(3*x),0}");
    check("PolynomialQuotientRemainder(x^2 + 7*x + 6, x^2-5*x-6, x)", //
        "{1,12+12*x}");
    check("PolynomialQuotientRemainder[a,b,x]", //
        "{a/b,0}");
    check("PolynomialQuotientRemainder(e*x^2 + d, ( -2*d*e^2*Sqrt(-e/d) )*x + 2*d*e^2, x )", //
        "{1/(2*e^2)-x/(2*d*e*Sqrt(-e/d)),0}");
    check("PolynomialQuotientRemainder(x^2, x + a,x)", //
        "{-a+x,a^2}");
    check("PolynomialQuotientRemainder(x^2 + x + 1, 2*x + 1, x)", //
        "{1/4+x/2,3/4}");
    check("PolynomialQuotientRemainder(x^2 + b*x + 1, a*x + 1, x)", //
        "{-1/a^2+b/a+x/a,1+1/a^2-b/a}");

    check("PolynomialQuotientRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 2)", //
        "{1+x^2,0}");
    check("PolynomialQuotientRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 3)", //
        "{1+2*x,0}");
  }

  @Test
  public void testPolynomialRemainder() {

    check("PolynomialRemainder(Indeterminate,Indeterminate,Indeterminate)", //
        "PolynomialRemainder(Indeterminate,Indeterminate,Indeterminate)");
    check("PolynomialRemainder(x^2 + 7*x + 6, x^2-5*x-6, x)", //
        "12+12*x");
    check("PolynomialRemainder(1,Sin(e+f*x),x)", //
        "PolynomialRemainder(1,Sin(e+f*x),x)");
    check("PolynomialRemainder(x^2, x + a,x)", //
        "a^2");
    check("PolynomialRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 2)", //
        "0");
    check("PolynomialRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 5)", "3");
  }

  @Test
  public void testPosition() {
    // github issue #859
    check("Position(2,2)", //
        "{{}}");
    check("Position(2,_?NumberQ)", //
        "{{}}");
    check("Position(2,_?IntegerQ)", //
        "{{}}");
    check("Position(2,3)", //
        "{}");



    check("Position(_Integer)[{1.5, 2, 2.5}]", //
        "{{2}}");

    check("Position(<|1->List,2-><|\"a\"->x^2|>,3->List,4->a+(1+x^2)^2|>,x_)", //
        "{{0},{Key(1)},{Key(2),0},{Key(2),Key(a),0},{Key(2),Key(a),1},{Key(2),Key(a),2},{Key(\n" + //
            "2),Key(a)},{Key(2)},{Key(3)},{Key(4),0},{Key(4),1},{Key(4),2,0},{Key(4),2,1,0},{Key(\n"
            + //
            "4),2,1,1},{Key(4),2,1,2,0},{Key(4),2,1,2,1},{Key(4),2,1,2,2},{Key(4),2,1,2},{Key(\n" + //
            "4),2,1},{Key(4),2,2},{Key(4),2},{Key(4)}}");
    check("Position(<|1->1+x^2,2-><|\"a\"->x^2|>,3->x^4,4->a+(1+x^2)^2|>,x_)", //
        "{{0},{Key(1),0},{Key(1),1},{Key(1),2,0},{Key(1),2,1},{Key(1),2,2},{Key(1),2},{Key(\n" + //
            "1)},{Key(2),0},{Key(2),Key(a),0},{Key(2),Key(a),1},{Key(2),Key(a),2},{Key(2),Key(a)},{Key(\n"
            + //
            "2)},{Key(3),0},{Key(3),1},{Key(3),2},{Key(3)},{Key(4),0},{Key(4),1},{Key(4),2,0},{Key(\n"
            + //
            "4),2,1,0},{Key(4),2,1,1},{Key(4),2,1,2,0},{Key(4),2,1,2,1},{Key(4),2,1,2,2},{Key(\n" + //
            "4),2,1,2},{Key(4),2,1},{Key(4),2,2},{Key(4),2},{Key(4)}}");
    check(
        "Position(<|{1 -> 1 + x^2, 2 -> <|\"a\" -> x^2|>, 3 -> x^4, 4 -> a + (1 + x^2)^2}|>, x^_)", //
        "{{Key(1),2},{Key(2),Key(a)},{Key(3)},{Key(4),2,1,2}}");
    check("Position(<|\"a\" -> 1, \"b\" -> 2, \"c\" -> 3, \"d\" -> 4|>, _Integer?PrimeQ)", //
        "{{Key(b)},{Key(c)}}");
    check("Count({1, \"f\", g, \"h\", \"7\"}, _?StringQ)", //
        "3");
    check("Length(Position({1, \"f\", g, \"h\", \"7\"}, _?StringQ))", //
        "3");
    check("Position({1 + x^2, 5, x^4, a + (1 + x^2)^2}, x^_)", //
        "{{1,2},{3},{4,2,1,2}}");
    check("Extract({1 + x^2, 5, x^4, a + (1 + x^2)^2}, {{1,2},{3},{4,2,1,2}})", //
        "{x^2,x^4,x^2}");
    check("Position(x^2 + y^2, Power, Heads->False)", //
        "{}");
    check("Position(x^2 + y^2, Power)", //
        "{{1,0},{2,0}}");
    check("Position(f(g(h(x))), _, Infinity)", //
        "{{0},{1,0},{1,1,0},{1,1,1},{1,1},{1}}");
    check("Position({a, b, a, a, b, c, b, a, b}, b, 1, 2)", //
        "{{2},{5}}");
    check("Position({1 + x^2, 5, x^4, a + (1 + x^2)^2}, x^_)", //
        "{{1,2},{3},{4,2,1,2}}");
    check("Position({1 + x^2, 5, x^4, a + (1 + x^2)^2}, x^_, 2)", //
        "{{1,2},{3}}");
    check("Position({1, 2, 2, 1, 2, 3, 2}, 2)", //
        "{{2},{3},{5},{7}}");
    check("Position({1 + Sin(x), x, (Tan(x) - y)^2}, x, 3)", //
        "{{1,2,1},{2}}");
    check("Position({1 + x^2, x*y ^ 2,  4*y,  x ^ z}, x^_)", //
        "{{1,2},{4}}");
    check("Position(_Integer)[{1.5, 2, 2.5}]", //
        "{{2}}");

    check("Position({1.0, 2+3, b}, _Integer)", //
        "{{2}}");
    check("Position(_Integer)[{1.0, 2+3, b}]", //
        "{{2}}");
    check("Position(_Integer)[{1.0, 2, b}]", //
        "{{2}}");
    check("Position(_Integer)[{a, 2, b}]", //
        "{{2}}");
    check("Position({x, {x, y}, y},x,1)", //
        "{{1}}");
    check("Position({x, {x, y}, y},x,2)", //
        "{{1},{2,1}}");
    check("Position({x, {x, y}, y},x,{2})", //
        "{{2,1}}");

    check("Position(f(f(g(a), a), a, h(a), f), a, {2, Infinity})", //
        "{{1,1,1},{1,2},{3,1}}");
    check("Position(f(f(g(a), a), a, h(a), f), f, Heads->False)", //
        "{{4}}");
    check("Position(f(f(g(a), a), a, h(a), f), f, Heads->True)", //
        "{{0},{1,0},{4}}");

    check("Position({f(a), g(b), f(c)}, f(x_))", //
        "{{1},{3}}");
    check("Position(Range(-1, 1, 0.05), 0.1)", //
        "{}");
    check("Position(Range(-1, 1, 0.05), n_ /; n == 0.1)", //
        "{{23}}");
  }

  @Test
  public void testPositive() {
    check("Positive(Infinity)", //
        "True");
    check("Positive(-Infinity)", //
        "False");
    check("Positive(-9/4)", //
        "False");
    check("Positive(0.1+I)", //
        "False");
    check("Positive(1)", //
        "True");
    check("Positive(0)", //
        "False");
    check("Positive(1 + 2*I)", //
        "False");
    check("Positive(Pi)", //
        "True");
    check("Positive(x)", //
        "Positive(x)");
    check("Positive(Sin({11, 14}))", //
        "{False,True}");
  }

  @Test
  public void testPossibleZeroQ() {
    check("PossibleZeroQ((-Exp(q) - 2*Cosh(q/3))*(-2*Cosh(q/3) - Exp(-q)) - (4*Cosh(q/3)^2 - 1)^2)", //
        "True");
    check("PossibleZeroQ(-Cos(x)/(1-Cos(x))+Sin(x)^2/(1-Cos(x))^2-1/(1-Cos(x)))", //
        "True");
    check("PossibleZeroQ((x + 1) (x - 1) - x^2 + 1)", //
        "True");
    check("PossibleZeroQ(Sqrt(x^2)-x,Assumptions -> Re(x)>0)", //
        "True");
    check("PossibleZeroQ(Sqrt(x^2)-x)", //
        "False");

    // check(
    // "(-99.12580575458303)*I", //
    // "I*(-99.12581)");
    // check(
    //
    // "-Cos[95.78967105119972+I*(-99.12580575458303)]/8+1/2*Cos[Pi/4+(95.78967105119972+I*(-99.12580575458303))/2]^3*Sin[Pi/4+(95.78967105119972+I*(-99.12580575458303))/2]+1/8*Cos[95.78967105119972+I*(-99.12580575458303)]*Sin[95.78967105119972+I*(-99.12580575458303)]",
    // //
    // "");
    // check(
    //
    // "-Cos[95.78967105119972+I*(-99.12580575458303)]/8+1/2*Cos[Pi/4+(95.78967105119972+I*(-99.12580575458303))/2]^3*Sin[Pi/4+(95.78967105119972+I*(-99.12580575458303))/2]",
    // //
    // "-2.27086*10^83+I*(-3.92378*10^84)");
    // check(
    //
    // "+1/8*Cos[95.78967105119972+I*(-99.12580575458303)]*Sin[95.78967105119972+I*(-99.12580575458303)]",
    // //
    // "2.27086*10^83+I*3.92378*10^84");
    // check(
    //
    // "PossibleZeroQ(((-Cosh(a+b*x)+Sinh(a+b*x))*(-b*Cosh(a+b*x)+b*Sinh(a+b*x)))/b+(-Cosh(a+b*x)+Sinh(a+b*x))/(Cosh(a+b*x)+Sinh(a+b*x)))",
    // //
    // "True");
    //
    check("-2.27086*10^83+I*(-3.92378*10^84)+2.27086*10^83+I*3.92378*10^84", //
        "0.0");

    // check(
    // "PossibleZeroQ(-Cos(x)/8+1/2*Cos(Pi/4+x/2)^3*Sin(Pi/4+x/2)+1/8*Cos(x)*Sin(x))", //
    // "True");
    check("PossibleZeroQ(E^(I*Pi/4)-(-1)^(1/4))", //
        "True");

    check("E^(I*Pi/4)  - (-1)^(1/4)", //
        "-(-1)^(1/4)+(1+I)/Sqrt(2)");
    check("PossibleZeroQ(E^(I*Pi/4)-(-1)^(1/4))", //
        "True");
    check("PossibleZeroQ(Erf(Log(4)+2*Log(Sin(Pi/8)))-Erf(Log(2-Sqrt(2))))", //
        "True");
    check("PossibleZeroQ(x*E^(I*Pi/4)  - x*(-1)^(1/4))", //
        "True");

    check("PossibleZeroQ(2^(2*I) - 2^(-2*I) - 2*I*Sin(Log(4)))", //
        "True");
    check("PossibleZeroQ(E^Pi - Pi^E)", //
        "False");
    check("PossibleZeroQ((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)", //
        "True");
    check("PossibleZeroQ(E^(I*Pi/4) - (-1)^(1/4))", //
        "True");
    check("PossibleZeroQ((x + 1)*(x - 1) - x^2 + 1)", //
        "True");
    check("PossibleZeroQ(1/x + 1/y - (x + y)/(x*y))", //
        "True");
    check("PossibleZeroQ(Sqrt(x^2) - x)", //
        "False");
  }

  @Test
  public void testPower() {
    check("1^Infinity", //
        "Indeterminate");
    check("(-1)^Infinity", //
        "Indeterminate");
    check("Power(-Infinity, 43)", //
        "-Infinity");
    check("Sqrt(-Infinity)", //
        "I*Infinity");
    check("Power(-Infinity, 4/3) // FullForm", //
        "DirectedInfinity(Times(-1, Power(-1, Rational(1,3))))");
    check("Power(-Infinity, 4/3)", //
        "DirectedInfinity(-(-1)^(1/3))");

    check("Power(5,2,3) // FullForm", //
        "390625");
    check("Power(Power(5,2),3) // FullForm", //
        "15625");
    // check(
    // "22610175337329362245620630780795110213748908671958118609721101133417890222511288803123590513070561131892027191395369936042326256168653778870546242363369977629202359754270184318045654379854509946171340376686223573580224107124150403623923011308302977397847548580593006903821021518558458596423345897023275437136962636706163753880297496880232736919213061706006731771987945729565465391441847486930782906711101531982421875^(1/500)
    // //N", //
    // "");
    check("(2*x*y)^n", //
        "2^n*(x*y)^n");
    check("Sqrt(-d) // FullForm", //
        "Power(Times(-1, d), Rational(1,2))");
    check("(-27)^(1/3) // FullForm", //
        "Times(3, Power(-1, Rational(1,3)))");
    check("(-27/Pi)^(2/3) // FullForm", //
        "Times(9, Power(Times(-1, Power(Pi, -1)), Rational(2,3)))");

    check("(-Pi/27)^(1/3) // FullForm", //
        "Times(Rational(1,3), Power(Times(-1, Pi), Rational(1,3)))");

    check("(Pi/27)^(1/3) // FullForm", //
        "Times(Rational(1,3), Power(Pi, Rational(1,3)))");
    check("(27/Pi)^(1/3) // FullForm", //
        "Times(3, Power(Pi, Rational(-1,3)))");
    check("Sqrt(Pi/4) // FullForm", //
        "Times(Rational(1,2), Power(Pi, Rational(1,2)))");
    check("Sqrt(4/Pi) // FullForm", //
        "Times(2, Power(Pi, Rational(-1,2)))");
    check("Sqrt(Pi/2) // FullForm", //
        "Power(Times(Rational(1,2), Pi), Rational(1,2))");
    check("Sqrt(2/Pi) // FullForm", //
        "Power(Times(2, Power(Pi, -1)), Rational(1,2))");

    check("1/(-7) // FullForm", //
        "Rational(-1,7)");
    // TODO improve output - if base is Sqrt avoid parnethesis
    check("x^Sqrt(y)^a", //
        "x^(Sqrt(y))^a");

    check("a^(1/Log(a)^4)", //
        "E^(1/Log(a)^3)");
    check("a^(Log(a)^7)", //
        "E^Log(a)^8");
    check("a^(3*b/Log(a)^3)", //
        "E^((3*b)/Log(a)^2)");
    check("a^(b/Log(a))", //
        "E^b");
    check("a^(1/Log(a))", //
        "E");


    check("1/Overflow()", //
        "Underflow()");
    check("1/Underflow()", //
        "Overflow()");
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      check(
          "(-9223372036854775807/9223372036854775808-I*9223372036854775808/9223372036854775807)^10007", //
          "BigInteger bit length 229469 exceeded");
    }
    check("Power(p,q,2,65)", //
        "p^q^36893488147419103232");
    check("Power(q,2,65)", //
        "q^36893488147419103232");
    check("3^2^65", //
        "3^36893488147419103232");
    check("x^(-2)^(-3)", //
        "1/x^(1/8)");
    check("(Sqrt[a])^(1/4)", //
        "a^(1/8)");
    check("(Sqrt[a])^(-1/4)", //
        "1/a^(1/8)");
    check("z^i / i^n //FullForm", //
        "Times(Power(i, Times(-1, n)), Power(z, i))");
    check("Sqrt(-7 + 24*I)", //
        "3+I*4");

    // message BigInteger bit length 254105 exceeded
    check("(-8/27*(-2/3)^(2/5))^2147483647", //
        "(-8/27*(-2/3)^(2/5))^2147483647");
    // SLOW in factorSmallPrimes()
    // message BigInteger bit length 258048 exceeded
    check("(85070591730234615764386559452539518976/121)^(13479/14641)", //
        "(85070591730234615764386559452539518976/121)^(13479/14641)");

    check("(I*1/2)^x^2", //
        "(I*1/2)^x^2");
    check("Sqrt(Sqrt(3/5)/2) // FullForm", //
        "Times(Power(Rational(3,5), Rational(1,4)), Power(2, Rational(-1,2)))");
    check("Sqrt(Sqrt(3/5)) // FullForm", //
        "Power(Rational(3,5), Rational(1,4))");

    check("(-1062961)^(2/3)", //
        "1031*(-1)^(2/3)*1031^(1/3)");

    // TODO
    // check("Power({{1,0},{0,1}},{{0}},{x,5,-3},1/2,{x,-2,3})", //
    // "{{1,0^{{0}}^{x^2^(-x),625,(-3)^(1/8)}},{0^{{0}}^{x^2^(-x),625,(-3)^(1/8)},1}}");
    check("Power(<|x->y|>,5,-1/2)", //
        "<|x->y^(1/Sqrt(5))|>");
    check("Power(<|x->y|>,5)", //
        "<|x->y^5|>");

    check("(-Infinity)^(-3/5)", //
        "0");
    check("1/Sqrt(-Infinity)", //
        "0");
    check("0^I", //
        "Indeterminate");
    check("0^(-I)", //
        "Indeterminate");
    // Test Config.MAX_BIT_COUNT = Short.MAX_VALUE;
    // check("(3/7)^7625597484987", //
    // "Maximum AST size 45891 exceeded");
    // check("3^3^3^3^3^3^3", //
    // "Maximum AST size 52046 exceeded");
    // check("(3/7)^762559748498700000000000", //
    // "Maximum AST size 45891 exceeded");

    check("(((3/7)^3)^3)^3", //
        "7625597484987/65712362363534280139543");
    check("((((3/7)^3)^3)^3)^3", //
        "443426488243037769948249630619149892803/283753509180010707824461062763116716606126555757084586223347181136007");
    // check("(((((3/7)^3)^3)^3)^3)^3", //
    // "8718964248596095820291107058586077169696407240473175008552521943799096709372343\\\n" +
    // "9943475549906831683116791055225665627/2284671285987374648044782166659234642669413233343555899898341285496111418662257\\\n"
    // +
    // "4870902442510049863025667206258127311451949520409822391138243055993672121915936\\\n" +
    // "570990365106665813437806284123385754752042992343");

    check("((3/7)^3)^3", //
        "19683/40353607");

    check("Power((-x)^(1/2), 2)", //
        "-x");
    check("Power((-x)^(1/3), 3)", //
        "-x");
    check("( (-11)^(1/3))^3", //
        "-11");
    check("(-(-89)^(1/3))^3-89", //
        "0");
    check("Power(a,b,c,d) // FullForm", //
        "Power(a, Power(b, Power(c, d)))");
    check("(I)^(-0.5)", //
        "0.707107+I*(-0.707107)");
    check("(I)^(-1/2)", //
        "-(-1)^(3/4)");
    check("Sqrt(-1)*(-1)^(1/10)", //
        "(-1)^(3/5)");

    check("(-1)^(2/3)", //
        "(-1)^(2/3)");
    check("1/2*Sqrt(2)", //
        "1/Sqrt(2)");
    check("I*1/2*Sqrt(2)", //
        "I/Sqrt(2)");

    check("E^(I*2/3*Pi)", //
        "-1/2+I*1/2*Sqrt(3)");
    check("E^((-11/6)*I*Pi)", //
        "I*1/2+Sqrt(3)/2");
    check("E^((3/4)*I*Pi)", //
        "(-1+I)/Sqrt(2)");
    check("E^((7/6)*I*Pi)", //
        "-I*1/2-Sqrt(3)/2");

    // check("TimeConstrained(1^3^3^3^3, 10)", //
    // "$Aborted");
    // check("TimeConstrained(1^3^3^3, 10)", //
    // "1");
    check("0^(-1)", //
        "ComplexInfinity");

    check("Refine(Exp(I*k*Pi),Element(k,Integers))", //
        "(-1)^k");

    check("Exp(2*I*43*Pi)", //
        "1");
    check("Exp(I*43*Pi)", //
        "-1");
    check("E^(I/2*Pi)", //
        "I");
    check("(I)^(-1)", //
        "-I");
    check("(I)^(-1+k)", //
        "I^(-1+k)");
    check("2^(2/3)*(-5+3*Sqrt[3])^(2/3)", //
        "(2*(-5+3*Sqrt(3)))^(2/3)");
    check("(-1095912791)^(2/3)", //
        "1062961*(-1)^(2/3)");
    check("(-1062961)^(2/3)", //
        "1031*(-1)^(2/3)*1031^(1/3)");
    check("(-27)^(2/3)", //
        "9*(-1)^(2/3)");

    check("(-1)^(1/6)*9178829416159^(1/6)", //
        "(-9178829416159)^(1/6)");
    check("(-(50!)/30!)^(1/6)", //
        "24*(-9178829416159)^(1/6)*Sqrt(2)*5^(5/6)*7^(2/3)*33^(1/3)");
    check("(-27)^(2/3)", //
        "9*(-1)^(2/3)");
    check("(-(50!))^(1/6)", //
        "604800*(-2756205443)^(1/6)*2^(5/6)*Sqrt(13)*33^(2/3)*52003^(1/3)");
    check("604800^6* -621447116887301398870058090208==(-(50!)) ", //
        "True");
    check("(-2)^(-11/4)", //
        "-(-1)^(1/4)/(4*2^(3/4))");
    check("(-2)^(11/4)", //
        "4*(-2)^(3/4)");
    check("(-2)^(4/3)", //
        "-2*(-2)^(1/3)");
    check("1/(-8/27*(-2/3)^(2/5))", //
        "27/8*(-1)^(3/5)*(3/2)^(2/5)");

    check("(-2/3)^(5/2)", //
        "I*4/9*Sqrt(2/3)");

    check("(-2/3)^(17/5)", //
        "-8/27*(-2/3)^(2/5)");
    check("(-2/3)^(-17/5)", //
        "27/8*(-1)^(3/5)*(3/2)^(2/5)");
    check("(-1)^(8/5)", //
        "-(-1)^(3/5)");
    check("(-1)^(12/5)", //
        "(-1)^(2/5)");
    check("(-1)^(5/4)", //
        "-(-1)^(1/4)");
    check("(-1)^(7/4)", //
        "-(-1)^(3/4)");
    check("(-2/3)^(5/4)", //
        "-2/3*(-2/3)^(1/4)");
    check("I^(5/2) - Sqrt(I^5)", //
        "-2*(-1)^(1/4)");

    // github #114
    check("Sqrt(1/(2*Surd(-Cos(9/20*Pi),3)))", //
        "Sqrt(-1/(2*Cos(9/20*Pi)^(1/3)))");
    check("0^(13+n)/a", //
        "0^(13+n)/a");
    check(" 2^(2*I) - 2^(-2*I) - 2*I*Sin(Log(4)) ", //
        "0");
    check("2*Sqrt(2)", //
        "2*Sqrt(2)");
    check("-2*Sqrt(2)", //
        "-2*Sqrt(2)");

    check("(2/3)^(-m)", //
        "(3/2)^m");
    check("(3/2)^(-m)", //
        "(3/2)^(-m)");

    check("(-2/3)*(3/2)^m", //
        "-(2/3)^(1-m)");
    check("(2/3)*(3/2)^m", //
        "(2/3)^(1-m)");
    check("(-2/3)*(x)/(3/2)^m", //
        "-x/(3/2)^(1+m)");
    check("(-2/3)*(x)*(3/2)^m", //
        "-(2/3)^(1-m)*x");
    check("(2/3)*(x)/(3/2)^m", //
        "x/(3/2)^(1+m)");
    check("(2/3)*(x)*(3/2)^m", //
        "(2/3)^(1-m)*x");

    check("(2/3)^(1+m)*x", //
        "(2/3)^(1+m)*x");

    check("Sqrt(Pi/b)", //
        "Sqrt(1/b)*Sqrt(Pi)");
    check("(2*x*y)^n", //
        "2^n*(x*y)^n");
    check("(0.3333*x*y)^n", //
        "0.3333^n*(x*y)^n");
    check("(a*3/4*E*x*y)^n", //
        "(3/4*E)^n*(a*x*y)^n");
    check("(a*42*(-Pi)*x*y)^n", //
        "(42*Pi)^n*(-a*x*y)^n");
    check("(-Infinity)^(42.0)", //
        "Infinity");
    check("(-Infinity)^(43.0)", //
        "-Infinity");
    check("(-Infinity)^(-42.0)", //
        "0");
    check("(-Infinity)^(-43.0)", //
        "0");
    check("(-5/6)^(1/2)", //
        "I*Sqrt(5/6)");
    check("(9/4)^(-3/8)", //
        "(2/3)^(3/4)");
    check("4^(-3/8)", //
        "1/2^(3/4)");
    check("5103^(1/3)", //
        "9*7^(1/3)");
    check("4^(3/8)", //
        "2^(3/4)");

    check("(-42)^Infinity", //
        "ComplexInfinity");
    check("(-1)^Infinity", //
        "Indeterminate");
    check("(-1/2)^Infinity", //
        "0");
    check("(1/4)^Infinity", //
        "0");
    check("(1)^Infinity", //
        "Indeterminate");
    check("(42)^Infinity", //
        "Infinity");

    check("(-42)^(-Infinity)", //
        "0");
    check("(-1)^(-Infinity)", //
        "Indeterminate");
    check("(-1/2)^(-Infinity)", //
        "ComplexInfinity");
    check("(1/4)^(-Infinity)", //
        "Infinity");
    check("(1)^(-Infinity)", //
        "Indeterminate");
    check("(42)^(-Infinity)", //
        "0");

    check("64^(2/3)", //
        "16");
    check("81^(3/4)", //
        "27");
    check("Sqrt(63/5)", //
        "3*Sqrt(7/5)");
    check("Sqrt(9/2)", //
        "3/Sqrt(2)");
    check("Sqrt(1/2)", //
        "1/Sqrt(2)");
    check("1/Sqrt(2)-Sqrt(1/2)", //
        "0");
    check("(2/3)^(-3/4)", //
        "(3/2)^(3/4)");
    check("(y*1/z)^(-1.0)", //
        "z/y");
    check("0^(3+I*4)", //
        "0");
    check("4 ^ (1/2)", //
        "2");
    check("4 ^ (1/3)", //
        "2^(2/3)");
    check("3^123", //
        "48519278097689642681155855396759336072749841943521979872827");
    check("(y ^ 2) ^ (1/2)", //
        "Sqrt(y^2)");
    check("(y ^ 2) ^ 3", //
        "y^6");
    check("4.0 ^ (1/3)", //
        "1.5874");
    check("a /. x_ ^ n_. :> {x, n}", //
        "{a,1}");
    check("(1.5 + 1.0*I) ^ 3.5", //
        "-3.68294+I*6.95139");
    check("(1.5 + 1.0*I) ^ (3.5 + 1.5*I)", //
        "-3.19182+I*0.645659");
    check("1/0", //
        "ComplexInfinity");
    check("0 ^ -2", //
        "ComplexInfinity");
    check("0 ^ (-1/2)", //
        "ComplexInfinity");
    check("0 ^ -Pi", //
        "ComplexInfinity");
    check("0 ^ I", //
        "Indeterminate");
    check("0 ^ (2*I*E)", //
        "Indeterminate");
    check("0 ^ - (Pi + 2*E*I)", //
        "ComplexInfinity");
    check("0^0", //
        "Indeterminate");
    check("Sqrt(-3+2.*I)", //
        "0.550251+I*1.81735");
    check("Sqrt(-3+2*I)", //
        "Sqrt(-3+I*2)");
    check("(3/2+1/2*I)^2", //
        "2+I*3/2");
    check("I ^ I", //
        "I^I");
    check("2 ^ 2.0", //
        "4.0");
    check("Pi ^ 4.", //
        "97.40909");
    check("a^b", //
        "a^b");

    check("54^(1/3)", //
        "3*2^(1/3)");
    check("Exp(y + Log(x))", //
        "E^y*x");
    check("E^(2*(y+Log(x)))", //
        "E^(2*y)*x^2");
    // don't change see issue #137
    check("2^(3+x)", //
        "2^(3+x)");

    check("I^(1/3)", //
        "(-1)^(1/6)");
    check("I^(1/4)", //
        "(-1)^(1/8)");
    check("I^(1/8)", //
        "(-1)^(1/16)");
    check("I^(3/8)", //
        "(-1)^(3/16)");
    check("I^(3/5)", //
        "(-1)^(3/10)");
    check("(-I)^(1/2)", //
        "-(-1)^(3/4)");
    check("(-I)^(1/3)", //
        "-(-1)^(5/6)");
    check("(-I)^(3/5)", //
        "-(-1)^(7/10)");
    check("(-I)^(21/32)", //
        "-(-1)^(43/64)");
    check("(-I)^(32/21)", //
        "-(-1)^(5/21)");
    check("(-I)^(1/4)", //
        "-(-1)^(7/8)");
    check("(-I)^(1/5)", //
        "-(-1)^(9/10)");
    check("(-I)^(1/6)", //
        "-(-1)^(11/12)");
    check("(-I)^(1/24)", //
        "-(-1)^(47/48)");
    check("(-I)^(64/7)", //
        "-(-1)^(3/7)");
    check("(-I)^(71/7)", //
        "(-1)^(13/14)");
    check("27^(1/3)", //
        "3");
    check("5103^(1/3)", //
        "9*7^(1/3)");
    check("5103^(1/2)", //
        "27*Sqrt(7)");
    check("Sqrt(75/4)", //
        "5/2*Sqrt(3)");

    check("0^(-1/2)", //
        "ComplexInfinity");

    check("E^(x+2*Pi*I)", //
        "E^x");
    check("E^(x+11*Pi*I)", //
        "-E^x");
    check("E^(x+Sin(a)+2*Pi*I)", //
        "E^(x+Sin(a))");

    check("(-9/5)*(3)^(-1/2)", //
        "-3/5*Sqrt(3)");
    check("(-1/9)*3^(1/2)", //
        "-1/(3*Sqrt(3))");
    check("3^(1/2)/9", //
        "1/(3*Sqrt(3))");
    check("0^0", //
        "Indeterminate");
    check("27^(1/3)", //
        "3");
    check("(-27)^(1/3)", //
        "3*(-1)^(1/3)");
    check("(-5)^(1/2)", //
        "I*Sqrt(5)");
    check("(-5)^(-1/2)", //
        "-I/Sqrt(5)");
    check("(-(2/3))^(-1/2)", //
        "-I*Sqrt(3/2)");
    check("FullForm(a^b^c)", //
        "Power(a, Power(b, c))");
    check("FullForm((a^b)^c)", //
        "Power(Power(a, b), c)");
    check("(a*b)^3", //
        "a^3*b^3");
    check("(a*b)^(1/2)", //
        "Sqrt(a*b)");
    check("FullForm((a^b)^3)", //
        "Power(a, Times(3, b))");
    check("{2,3,4,5}^3", //
        "{8,27,64,125}");
    check("N(29^(1/3))", //
        "3.07232");
    check("50!^(1/6)", //
        "604800*2^(5/6)*Sqrt(13)*33^(2/3)*52003^(1/3)*2756205443^(1/6)");
    check("(z^(1/3))^3", //
        "z");
    check("(z^3)^(1/3)", //
        "(z^3)^(1/3)");
    check("Sqrt(x^2)", //
        "Sqrt(x^2)");

    check("E^Log(x)", //
        "x");
    check("E^(y+Log(x))", //
        "E^y*x");
    check("E^(y+Log(x)-z)", //
        "E^(y-z)*x");
    check("E^(y-Log(x)-z)", //
        "E^(y-z)/x");
    check("E^(y+Log(x)-a*Log(v)*b*Log(u)-z)", //
        "E^(y-z-a*b*Log(u)*Log(v))*x");
    check("E^(y-Log(x)+Log(y)-a*Log(v)*b*Log(u)-z)", //
        "(E^(y-z-a*b*Log(u)*Log(v))*y)/x");
    check("Sqrt(1/a)", //
        "Sqrt(1/a)");
  }

  @Test
  public void testPowerExpand() {

    check("PowerExpand(Log(4*(Im(z)^2+ Re(z)^2)))", //
        "2*Log(2)+Log(Im(z)^2+Re(z)^2)");
    check("PowerExpand(Log(24/10)+Log(2))", //
        "3*Log(2)+Log(3)-Log(5)");
    check("PowerExpand(Log(24/10)+Log(2)+Log(x/y))", //
        "3*Log(2)+Log(3)-Log(5)+Log(x)-Log(y)");
    check("PowerExpand(Log(-13/350))", //
        "I*Pi-Log(2)-2*Log(5)-Log(7)+Log(13)");
    check("PowerExpand(Log(24/175))", //
        "3*Log(2)+Log(3)-2*Log(5)-Log(7)");
    check("PowerExpand(Log(12))", //
        "2*Log(2)+Log(3)");
    check("PowerExpand(Log(-12))", //
        "I*Pi+2*Log(2)+Log(3)");
    check("PowerExpand(ProductLog(y*Exp(y)))", //
        "y");
    check("PowerExpand(Log(x/y))", //
        "Log(x)-Log(y)");
    check("PowerExpand((x*y*z)^n)", //
        "x^n*y^n*z^n");
    check("PowerExpand(Log(x*y))", //
        "Log(x)+Log(y)");
    check("PowerExpand(Log(x^k))", //
        "k*Log(x)");
    check("PowerExpand(Sqrt(-a))", //
        "I*Sqrt(a)");
    check("PowerExpand(Sqrt(a^2))", //
        "a");
    // check("PowerExpand(Sqrt(a/b))", "Sqrt(a)*Sqrt(1/b)");

    check("PowerExpand((a ^ b) ^ c)", //
        "a^(b*c)");
    check("PowerExpand((a * b) ^ c)", //
        "a^c*b^c");
    check("PowerExpand((x ^ 2) ^ (1/2))", //
        "x");

    check("PowerExpand(Log(a*b*c, d))", //
        "Log(d)/(Log(a)+Log(b)+Log(c))");
    check("PowerExpand(Log(a*b*c))", //
        "Log(a)+Log(b)+Log(c)");
    check("PowerExpand(Log(a*b^c,d))", //
        "Log(d)/(Log(a)+c*Log(b))");
    check("PowerExpand(Log(a*b^c))", //
        "Log(a)+c*Log(b)");
    check("PowerExpand(Log(a/b))", //
        "Log(a)-Log(b)");
    check("-2^(1/2)*3^(1/2)", //
        "-Sqrt(6)");
    check("Sqrt(x*y)", //
        "Sqrt(x*y)");
    check("{Sqrt(x*y), Sqrt(x)*Sqrt(y)} /. {x -> -2, y -> -3}", //
        "{Sqrt(6),-Sqrt(6)}");
    check("PowerExpand((a^b)^(1/2))", //
        "a^(b/2)");
    check("Powerexpand((a*b)^(1/2))", //
        "Sqrt(a)*Sqrt(b)");
    check("Powerexpand(Log((a^b)^c))", //
        "b*c*Log(a)");
    check("Powerexpand({y*(a^b)^g, x+(a*b)^42,Log(a^b)})", //
        "{a^(b*g)*y,a^42*b^42+x,b*Log(a)}");
    check("Powerexpand(Sqrt(x^2))", //
        "x");
    check("Powerexpand(Log(1/z))", //
        "-Log(z)");
    check("Powerexpand(2-Log(1/z^3))", //
        "2+3*Log(z)");
    check("Powerexpand(Log(z^a))", //
        "a*Log(z)");
    check("Powerexpand(Sqrt(a* b) + Sqrt(c*d))", //
        "Sqrt(a)*Sqrt(b)+Sqrt(c)*Sqrt(d)");
    check("PowerExpand(Sqrt(x*y))", //
        "Sqrt(x)*Sqrt(y)");

    check("PowerExpand(Log(z^a), Assumptions->True)", //
        "I*2*Pi*Floor((Pi-Im(a*Log(z)))/(2*Pi))+a*Log(z)");

    check("PowerExpand((E^x)^(y), Assumptions->True)", //
        "E^(x*y+I*2*Pi*y*Floor((Pi-Im(x))/(2*Pi)))");
    // "E^(x*y)*E^(I*2*Pi*y*Floor(1/2*(-Im(x)+Pi)*Pi^(-1)))");

    check("PowerExpand((x*y)^(1/2), Assumptions->True)", //
        "E^(I*Pi*Floor(1/2-Arg(x)/(2*Pi)-Arg(y)/(2*Pi)))*Sqrt(x)*Sqrt(y)");
    check("PowerExpand((a*b*c)^(1/3), Assumptions->True)", //
        "a^(1/3)*b^(1/3)*c^(1/3)*E^(I*2/3*Pi*Floor(1/2-Arg(a)/(2*Pi)-Arg(b)/(2*Pi)-Arg(c)/(\n"
            + "2*Pi)))");
  }

  @Test
  public void testPowerMod() {
    // 2 is a primitive root for 13
    check("PowerMod(2, Range(12), 13)", //
        "{2,4,8,3,6,12,11,9,5,10,7,1}");
    // 3 is not a primitive root for 13
    check("PowerMod(3, Range(12), 13)", //
        "{3,9,1,3,9,1,3,9,1,3,9,1}");

    // check("PowerMod(6, 1/2, 10)", "1");

    check("PowerMod(2, 10, 3)", //
        "1");
    // similar to Java modInverse()
    check("PowerMod(3, -1, 7)", //
        "5");
    // prints warning
    check("PowerMod(0, -1, 2)", //
        "PowerMod(0,-1,2)");
    // prints warning
    check("PowerMod(5, 2, 0)", //
        "PowerMod(5,2,0)");

    check("PowerMod(2, 10^9, 18)", //
        "16");
    check("PowerMod(2, {10, 11, 12, 13, 14}, 5)", //
        "{4,3,1,2,4}");
    check("PowerMod(147198853397, -1, 73599183960)", //
        "43827926933");

    check("PowerMod(2, 10000000, 3)", //
        "1");
    check("PowerMod(3, -2, 10)", //
        "9");
    check("PowerMod(0, -1, 2)", //
        "PowerMod(0,-1,2)");
    check("PowerMod(5, 2, 0)", //
        "PowerMod(5,2,0)");
  }

  @Test
  public void testPreDecrement() {
    check("--5", //
        "--5");
    check("a = 2", //
        "2");
    check("--a", //
        "1");
    check("a", //
        "1");

    check("index = {1,2,3,4,5,6}", //
        "{1,2,3,4,5,6}");
    check("--index[[2]]", //
        "1");
    check("index", //
        "{1,1,3,4,5,6}");
  }

  @Test
  public void testPreIncrement() {
    check("a = 2", //
        "2");
    check("++a", //
        "3");
    check("a", //
        "3");

    check("index = {1,2,3,4,5,6}", //
        "{1,2,3,4,5,6}");
    check("++index[[2]]", //
        "3");
    check("index", //
        "{1,3,3,4,5,6}");
  }

  @Test
  public void testPrepend() {
    check("Prepend(<|a->0,b:>1|>,<|a->0,b:>1|>)", //
        "<|a->0,b:>1|>");
    check("Prepend(<|a->0,b:>1|>,<|b:>1,a->0|>)", //
        "<|b:>1,a->0|>");
    check("$n=4;Prepend(Table(0,{$n +(-1)*1}),1)", //
        "{1,0,0,0}");
    check("Prepend(1/Sqrt(5),<|x->y|>)", //
        "<|x->y^(1/Sqrt(5))|>");
    check("Prepend(<|1 -> a, 2 -> b|>, {3 -> d, 4 -> e})", //
        "<|3->d,4->e,1->a,2->b|>");
    check("Prepend(1/Sqrt(5),<|x->y|>)", //
        "<|x->y^(1/Sqrt(5))|>");
    check("Prepend(Infinity,-I)", //
        "DirectedInfinity(-I,1)");
    check("Prepend({2, 3, 4}, 1)", //
        "{1,2,3,4}");
    check("Prepend(f(b, c), a)", //
        "f(a,b,c)");
    check("Prepend({c, d}, {a, b})", //
        "{{a,b},c,d}");
    check("Prepend(a, b)", //
        "Prepend(a,b)");

    // operator form
    check("Prepend(a)[{c, d}]", //
        "{a,c,d}");
  }

  @Test
  public void testPrependTo() {
    check("s = {1, 2, 4, 9}", //
        "{1,2,4,9}");
    check("PrependTo(s, 0)", //
        "{0,1,2,4,9}");
    check("s", //
        "{0,1,2,4,9}");

    check("y = f(a, b, c)", //
        "f(a,b,c)");
    check("PrependTo(y, x)", //
        "f(x,a,b,c)");
    check("y", //
        "f(x,a,b,c)");

    check("PrependTo({a, b}, 1)", //
        "PrependTo({a,b},1)");
    check("PrependTo(a, b)", //
        "PrependTo(a,b)");
    check("x = 1 + 2", //
        "3");
    check("PrependTo(x, {3, 4}) ", //
        "PrependTo(x,{3,4})");

    check("$l = {1, 2, 4, 9};PrependTo($l, 16)", //
        "{16,1,2,4,9}");
    check("$l = {1, 2, 4, 9};PrependTo($l, 16);$l", //
        "{16,1,2,4,9}");
  }

  @Test
  public void testPrimePi() {
    // iteration limit exceeded
    check("PrimePi(2147483647)", //
        "Hold(PrimePi(2147483647))");

    check("PrimePi(0)", //
        "0");
    check("PrimePi(3.5)", //
        "2");

    check("PrimePi(100)", //
        "25");
    check("PrimePi(-1)", "0");
    check("PrimePi(3.5)" //
        , "2");
    check("PrimePi(E)", //
        "1");

    check("PrimePi(1)", //
        "0");
    check("PrimePi(2)", //
        "1");

    // check("PrimePi(1000000)", "78498");
    check("PrimePi(10000)", //
        "1229");
    check("PrimePi(5.2)", //
        "3");

    check("PrimePi(997)", //
        "168");
    check("Prime(168)", //
        "997");
  }

  @Test
  public void testPrincipleComponents() {
    // message SparseArray: Input matrix contains an indeterminate entry.
    check("PrincipalComponents(SparseArray({{0,0},{0,0}},0) ^ (I*1/3*Pi))", //
        "PrincipalComponents(SparseArray(Number of elements: 4 Dimensions: {2,2} Default value: 0))");
    check("PrincipalComponents({{0.25,0.33,0.45,0.01}},Method->\"Correlation\")", //
        "{{0.0,0.0,0.0,0.0}}");
    check("PrincipalComponents({{0.25,0.33,0.01}} )", //
        "{{0.0,0.0,0.0}}");
    check("PrincipalComponents({{ }})", //
        "{}");
    check("PrincipalComponents({"//
        + "{90.0, 60, 90},"//
        + "{90, 90, 30},"//
        + "{60, 60, 60},"//
        + "{60, 60, 90},"//
        + "{30, 30, 30}"//
        + "})", //
        "{{34.37098,-13.66927,-10.38202},\n" //
            + " {9.98346,47.68821,1.47161},\n" //
            + " {-3.93481,-2.31599,3.89274},\n" //
            + " {14.69692,-25.24923,9.08167},\n" //
            + " {-55.11655,-6.45371,-4.06399}}");

    check("PrincipalComponents({{-0.3,1.5},{-0.3,1.5}  })", //
        "{{0.0,0.0},\n" //
            + " {0.0,0.0}}");
    check("PrincipalComponents({"//
        + "{90.0, 60, 90},"//
        + "{90, 90, 30},"//
        + "{60, 60, 60},"//
        + "{60, 60, 90},"//
        + "{30, 30, 30}"//
        + "})", //
        "{{34.37098,-13.66927,-10.38202},\n" //
            + " {9.98346,47.68821,1.47161},\n" //
            + " {-3.93481,-2.31599,3.89274},\n" //
            + " {14.69692,-25.24923,9.08167},\n" //
            + " {-55.11655,-6.45371,-4.06399}}");

    check("PrincipalComponents({{1.0, 2.0}, {2.0, 3.0}, {4.0, 10.0}})", //
        "{{3.27053,-0.285293},\n" //
            + " {1.99969,0.335165},\n"//
            + " {-5.27023,-0.0498715}}");
    check("PrincipalComponents({{1.0, 2.0}, {3.0, 5.0}, {5.0, 6.0}})", //
        "{{3.06837,0.171857},\n" //
            + " {-0.481117,-0.461488},\n" //
            + " {-2.58726,0.289631}}");

    check("PrincipalComponents({"//
        + "{90.0, 60, 90},"//
        + "{90, 90, 30},"//
        + "{60, 60, 60},"//
        + "{60, 60, 90},"//
        + "{30, 30, 30}"//
        + "}, Method -> \"Correlation\")", //
        "{{0.911826,-0.942809,0.440421},\n" //
            + " {1.38323,1.41421,-0.0309834},\n" //
            + " {-0.169031,0.0,-0.169031},\n" //
            + " {0.0666714,-0.942809,-0.404733},\n" //
            + " {-2.1927,0.471405,0.164326}}");
    check(
        "PrincipalComponents({{1., 2., -1.}, {2., 3., 2.}, {4., 10., 9.}, {4., 5., 6.}}, Method -> \"Correlation\")", //
        "{{1.81962,0.226286,0.0503109},\n"//
            + " {0.873896,-0.0527284,-0.0788127},\n"//
            + " {-1.94708,0.412467,0.00116417},\n"//
            + " {-0.746438,-0.586024,0.0273376}}");
    check("pc=PrincipalComponents( {{13.2, 200, 58, 21.2}," //
        + "{10, 263, 48, 44.5}," //
        + "{8.1, 294, 80, 31}," //
        + "{8.8, 190, 50, 19.5}," //
        + "{9, 276, 91, 40.6}," //
        + "{7.9, 204, 78, 38.7}," //
        + "{3.3, 110, 77, 11.1}," //
        + "{5.9, 238, 72, 15.8}," //
        + "{15.4, 335, 80, 31.9}," //
        + "{17.4, 211, 60, 25.8}})", //
        "{{33.21737,9.53712,-3.82769,2.93848},\n" //
            + " {-31.07601,23.87403,11.64669,-3.65609},\n" //
            + " {-62.33075,-7.21028,-3.4159,-2.71508},\n" //
            + " {43.95844,16.22527,-4.73401,-2.09407},\n" //
            + " {-46.27848,-18.29775,8.74335,0.369594},\n" //
            + " {26.1816,-9.56118,14.31321,0.292612},\n" //
            + " {122.6114,-15.97682,-2.36041,-1.13754},\n" //
            + " {-4.55805,-3.5664,-12.54463,-3.97383},\n" //
            + " {-103.2831,-4.01632,-7.32915,2.98506},\n" //
            + " {21.55756,8.99234,-0.491475,6.99086}}");
    check("Variance(pc)", //
        "{4100.023,196.3743,75.84303,12.0943}");
    check("Mean(pc) // Chop", //
        "{0,0,0,0}");
    check("Correlation(pc)", //
        "{{1.0,0.0,0.0,0.0},\n" //
            + " {0.0,1.0,0.0,0.0},\n" //
            + " {0.0,0.0,1.0,0.0},\n" //
            + " {0.0,0.0,0.0,1.0}}");

    check("PrincipalComponents( {{13.2, 200, 58, 21.2}," //
        + "{10, 263, 48, 44.5}," //
        + "{8.1, 294, 80, 31}," //
        + "{8.8, 190, 50, 19.5}," //
        + "{9, 276, 91, 40.6}," //
        + "{7.9, 204, 78, 38.7}," //
        + "{3.3, 110, 77, 11.1}," //
        + "{5.9, 238, 72, 15.8}," //
        + "{15.4, 335, 80, 31.9}," //
        + "{17.4, 211, 60, 25.8}}, Method -> \"Correlation\")", //
        "{{0.483183,1.18691,-0.415228,-0.148809},\n" //
            + " {-0.977884,1.02174,1.59196,0.250963},\n" //
            + " {-0.730655,-0.966191,-0.0518975,0.514159},\n" //
            + " {1.20901,1.07107,0.327703,0.407897},\n" //
            + " {-1.26624,-1.49626,0.107426,-0.405185},\n" //
            + " {-0.16975,-0.776364,0.700605,-0.745549},\n" //
            + " {2.74941,-0.959475,-0.138538,-0.340949},\n" //
            + " {0.967206,-0.600593,-0.31439,0.838019},\n" //
            + " {-1.9416,-0.0663725,-1.03767,0.267141},\n" //
            + " {-0.322683,1.58554,-0.769967,-0.637687}}");

    check(
        "PrincipalComponents({{1., 2., -1.}, {2., 3., 2.}, {4., 10., 9.}, {4., 5., 6.}}, Method -> \"Correlation\")", //
        "{{1.81962,0.226286,0.0503109},\n"//
            + " {0.873896,-0.0527284,-0.0788127},\n"//
            + " {-1.94708,0.412467,0.00116417},\n"//
            + " {-0.746438,-0.586024,0.0273376}}");


  }

  @Test
  public void testPrime() {
    check("Prime(10^6)", //
        "15485863");
    check("Prime(10^7)", //
        "179424673");
    // check("Prime(10^8)", "2038074743");
    // check("Prime(103000000)", "2102429869");

    // above the limit return Prime(...)
    // check("Prime(10^9)", "22801763489");
    // check("Prime(10^10)", "252097800623");
    // check("Prime(10^11)", "2760727302517");
    check("Prime(1)", //
        "2");
    check("Prime(167)", //
        "991");
  }

  @Test
  public void testPrimeOmega() {
    check("PrimeOmega(-n)", //
        "PrimeOmega(n)");
    check("PrimeOmega(0)", //
        "PrimeOmega(0)");
    check("PrimeOmega(990)", //
        "5");
    check("PrimeOmega(2010)", //
        "4");
    check("PrimeOmega(2^2)", //
        "2");
    check("PrimeOmega(3*2^2)", //
        "3");
    check("PrimeOmega(50!)", //
        "108");
    check("PrimeOmega({1,2,3,4,5,6,20})", //
        "{0,1,1,2,1,2,3}");
    check("PrimeOmega({-1,-2,-3,-4,-5,-6,-20})", //
        "{0,1,1,2,1,2,3}");
  }

  @Test
  public void testPrimePowerQ() {
    check("PrimePowerQ(0)", //
        "False");
    check("PrimePowerQ(1)", //
        "False");
    check("PrimePowerQ(-1)", //
        "False");

    check("13^9", //
        "10604499373");
    check("PrimePowerQ(10604499373)", //
        "True");
    check("PrimePowerQ(-8)", //
        "True");
    check("PrimePowerQ(9)", //
        "True");
    check("PrimePowerQ(52142)", //
        "False");
    check("PrimePowerQ(371293)", //
        "True");
    check("PrimePowerQ(1)", //
        "False");
  }

  @Test
  public void testPrimeQ() {
    check("PrimeQ({1,2,4})", //
        "{False,True,False}");
    check("PrimeQ(<|a->1,b->2,c->4|>)", //
        "<|a->False,b->True,c->False|>");
    // Gaussian primes
    // https://en.wikipedia.org/wiki/Gaussian_integer#Gaussian_primes
    check("PrimeQ(-3*I, GaussianIntegers->True)", //
        "True");
    check("PrimeQ(-3*I, GaussianIntegers->False)", //
        "False");
    check("PrimeQ(3*I, GaussianIntegers->True)", //
        "True");
    check("PrimeQ(-3, GaussianIntegers->True)", //
        "True");
    check("PrimeQ(-3)", "True");

    check("PrimeQ({0,1,2,3,4,5,6,7,8,9,10,11}, GaussianIntegers->True)", //
        "{False,False,False,True,False,False,False,True,False,False,False,True}");

    check("PrimeQ({-5-4*I,-5-4*I, -5-2*I, -5+2*I, -5+4*I, " //
        + "-4-5*I, -4-I, -4+I, -4+5*I, " //
        + "-3-2*I, -3, -3+2*I, " //
        + "-2-5*I, -2-3*I, -2-I, -2+I, -2+3*I, -2+5*I, " //
        + "-1-4*I, -1-2*I, -1-I, -1+I, -1+2*I, -1+4*I, " //
        + "-3*I, 3*I, 1-4*I, 1-2*I, 1-I, 1+I, 1+2*I, 1+4*I, 2-5*I, 2-3*I, 2-I, 2+I, 2+3*I, " //
        + "2+5*I, 3-2*I, 3, 3+2*I, 4-5*I, 4-I, 4+I, 4+5*I, 5-4*I, 5-2*I, 5+2*I, 5+4*I}, GaussianIntegers->True)", //
        "{True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True}");

    // Mersenne Prime
    // https://en.wikipedia.org/wiki/Mersenne_prime
    check("PrimeQ(131071)", //
        "True");
    check("PrimeQ(524287)", //
        "True");
    check("PrimeQ(2147483647)", //
        "True");

    check("PrimeQ(99999999999971)", //
        "True");
    check("Select(Range(100), PrimeQ)", //
        "{2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97}");
    check("PrimeQ(Range(20))", //
        "{False,True,True,False,True,False,True,False,False,False,True,False,True,False,False,False,True,False,True,False}");
  }

  @Test
  public void testPrimitiveRoot() {
    check("PrimitiveRoot(12)", //
        "PrimitiveRoot(12)");
    // check("Select(Range(100), PrimitiveRoot(#) != {} &)", //
    // "");
  }

  @Test
  public void testPrimitiveRootList() {
    check("PrimitiveRootList(-2147483648)", //
        "{}");
    check("PrimitiveRootList(14)", //
        "{3,5}");
    check("PrimitiveRootList(2*Prime(5))", //
        "{7,13,17,19}");
    check("PrimitiveRootList(9)", //
        "{2,5}");
    check("PrimitiveRootList(10)", //
        "{3,7}");
    check("PrimitiveRootList(7)", //
        "{3,5}");
    check("PrimitiveRootList(12)", //
        "{}");
    check("PrimitiveRootList(19)", //
        "{2,3,10,13,14,15}");
    check("PrimitiveRootList(43)", //
        "{3,5,12,18,19,20,26,28,29,30,33,34}");
    check("PrimitiveRootList(127)", //
        "{3,6,7,12,14,23,29,39,43,45,46,48,53,55,56,57,58,65,67,78,83,85,86,91,92,93,96,\n"
            + "97,101,106,109,110,112,114,116,118}");
  }

  @Test
  public void testPrint() {
    // in console simply print the styled text
    check("Print(Style(\"AbBbCc\", Red))", //
        "");
    check("do(print(i0);if(i0>4,Return(toobig)), {i0,1,10})", //
        "toobig");
  }

  @Test
  public void testPrintableASCIIQ() {
    check("PrintableASCIIQ(FromCharacterCode /@ Range(0, 127))", //
        "{False,False,False,False,False,False,False,False,False,False,False,False," //
            + "False,False,False,False,False,False,False,False,False,False,False,False," //
            + "False,False,False,False,False,False,False,False,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,False}");
    check("PrintableASCIIQ(\"Symja\")", //
        "True");
    check("PrintableASCIIQ({\"a\", \"b\", \"c\", \"Â¤\", \"d\", \"Ë©\"})", //
        "{True,True,True,False,True,False}");
    check("PrintableASCIIQ(\"\")", //
        "True");
    check("PrintableASCIIQ(\" \")", //
        "True");
    check("PrintableASCIIQ(\"!\")", //
        "True");
    check("PrintableASCIIQ(\"~\")", //
        "True");
    check("PrintableASCIIQ(\"\\\"\")", //
        "True");
    check("PrintableASCIIQ(FromCharacterCode(32))", //
        "True");
    check("PrintableASCIIQ(FromCharacterCode /@ Range(0, 127))", //
        "{False,False,False,False,False,False,False,False,False,False,False,False," //
            + "False,False,False,False,False,False,False,False,False,False,False,False," //
            + "False,False,False,False,False,False,False,False,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,True,True,True,True,True," //
            + "True,True,True,True,True,True,True,False}");
  }

  @Test
  public void testProductLog() {
    check("ProductLog(9/2*Sqrt(3)*Log(3))", //
        "3/2*Log(3)");
    check("ProductLog(1/2*Sqrt(7)*Log(7))", //
        "Log(7)/2");
    check("ProductLog(1/2*Sqrt(-7)*Log(-7))", //
        "ProductLog(I*1/2*Sqrt(7)*(I*Pi+Log(7)))");

    // https://docs.sympy.org/latest/modules/functions/elementary.html#sympy.functions.elementary.exponential.LambertW
    check("ProductLog(1.2)", //
        "0.635564");
    check("ProductLog(-1, 1.2)", //
        "-1.34748+I*(-4.41624)");
    // -1/E < z < 0.0
    check("ProductLog(-0.12)", //
        "-0.137718");
    check("ProductLog(-1, -0.12)", //
        "-3.32033");


    check("N(ProductLog(1/3), 100)", //
        "0.2576276530497367042829162016260977909096926475032044915339511440663191292752043724596398879341002505");
    check("Table(ProductLog(k, 2.3), {k, -2, 2})", //
        "{-1.56175+I*(-10.85265),-0.696131+I*(-4.56093),0.918224,-0.696131+I*4.56093,-1.56175+I*10.85265}");


    check("ProductLog(9/3*Sqrt(5)*Log(5))", //
        "ProductLog(3*Sqrt(5)*Log(5))");



    check("ProductLog(3*E^3)", //
        "3");
    check("ProductLog(-1, -3*E^(-3))", //
        "-3");
    check("ProductLog(2*Log(2))", //
        "Log(2)");
    check("ProductLog(-Log(2)/2)", //
        "-Log(2)");

    check("ProductLog(1/Sqrt(5)) // N", //
        "0.323582");
    check("ProductLog(-1, 0)", //
        "-Infinity");
    check("ProductLog(-1, -(Pi/2))", //
        "-I*1/2*Pi");
    check("ProductLog(-1, -(1/E))", //
        "-1");
    check("Refine(ProductLog(k, 0), k>1)", //
        "-Infinity");

    check("ProductLog(2.5 + 2*I)", //
        "1.05617+I*0.352561");

    check("z == ProductLog(z) * E ^ ProductLog(z)", //
        "True");
    check("ProductLog(0)", //
        "0");
    check("ProductLog(E)", //
        "1");

    String s = System.getProperty("os.name");
    if (s.contains("Windows")) {
      // TODO fix apfloat output for "exponential" format
      check("ProductLog(-1.5)", //
          "-0.0327837+I*1.54964");
      check("ProductLog({0.2, 0.5, 0.8})", //
          "{0.168916,0.351734,0.490068}");
      check("ProductLog(2.5 + 2*I)", //
          "1.05617+I*0.352561");
      check("N(ProductLog(4/10),50)", //
          "0.29716775067313854677972696224702134190445810155012");

      check("N(ProductLog(-1),20)", //
          "-0.3181315052047641353+I*1.3372357014306894089");
    }

    check("ProductLog(-Pi/2)", //
        "I*1/2*Pi");
    check("ProductLog(-1/E)", //
        "-1");

    check("ProductLog(Infinity)", //
        "Infinity");
    check("ProductLog(-Infinity)", //
        "Infinity");
    check("ProductLog(I*Infinity)", //
        "Infinity");
    check("ProductLog(-I*Infinity)", //
        "Infinity");
    check("ProductLog(ComplexInfinity)", //
        "Infinity");

    check("ProductLog(0,a)", //
        "ProductLog(a)");
    check("ProductLog(42,0)", //
        "-Infinity");
    check("ProductLog(-1,(-1/2)*Pi)", //
        "-I*1/2*Pi");
    check("ProductLog(-1,-E^(-1))", //
        "-1");
  }

  @Test
  public void testProjection() {
    check("Projection({},{},Dot)", //
        "{}");
    check("Projection({},{})", //
        "{}");
    check("Projection({5, 6, 7}, {1, 0, 0})", //
        "{5,0,0}");
    check("Projection({5, 6, 7}, {1, 1, 1})", //
        "{6,6,6}");
    check("Projection({5, I, 7}, {1, 1, 1})", //
        "{4+I*1/3,4+I*1/3,4+I*1/3}");
    check("Projection({x,y}, {a,b}, Dot)", //
        "{(a*(a*x+b*y))/(a^2+b^2),(b*(a*x+b*y))/(a^2+b^2)}");
    check("Projection({x,y}, {a,b})", //
        "{(a*(x*Conjugate(a)+y*Conjugate(b)))/(a*Conjugate(a)+b*Conjugate(b)),(b*(x*Conjugate(a)+y*Conjugate(b)))/(a*Conjugate(a)+b*Conjugate(b))}");
    check("ip(p1_, p2_) := Integrate(p1*p2, {x, -1, 1}); Projection(x^2, LegendreP(2, x), ip)", //
        "2/3*(-1/2+3/2*x^2)");
  }

  @Test
  public void testProtect() {
    check("Protect(test1, test2, test3)", //
        "{test1,test2,test3}");
    check("Attributes({test1,test2,test3,Plus,Times})", //
        "{{Protected},{Protected},{Protected},{Flat,Listable,NumericFunction,OneIdentity,Orderless,Protected},{Flat,Listable,NumericFunction,OneIdentity,Orderless,Protected}}");
    // message Set: Symbol test1 is Protected.
    check("test=42", //
        "42");
    // message SetDelayed: Symbol test1 is Protected.
    check("test1:=42", //
        "$Failed");
    check("test1", //
        "test1");

    check("Unprotect(test1, test2)", //
        "{test1,test2}");
    check("Unprotect(test1, test2, test3)", //
        "{test3}");
    check("test1=42", //
        "42");
    check("test1", //
        "42");
  }

  @Test
  public void testPutGet() {
    if (Config.FILESYSTEM_ENABLED) {
      check("Put(x + y, \"c:/temp/example_file1.m\"); Get(\"c:/temp/example_file1.m\")", //
          "x+y");
      check(
          "Put(x + y, 2x^2 + 4z!, Cos(x) + I Sin(x), \"c:/temp/example_file2.m\");"
              + "Get(\"c:/temp/example_file2.m\")", //
          "Cos(x)+I*Sin(x)");
      check("Put(47!, \"c:/temp/test.m\"); Get(\"c:/temp/test.m\")", //
          "258623241511168180642964355153611979969197632389120000000000");
    }
  }

  @Test
  public void testRootReduce() {
    // TODO eliminate gcd
    check("RootReduce((-35-7*Sqrt(35))^-1)", //
        "1/490*(35-7*Sqrt(35))");
    check("RootReduce((35-7*Sqrt(35))^-1)", //
        "1/490*(-35-7*Sqrt(35))");
    check("RootReduce((35+7*Sqrt(35))^-1)", //
        "1/490*(-35+7*Sqrt(35))");
    check("RootReduce((-35+7*Sqrt(35))^-1)", //
        "1/490*(35+7*Sqrt(35))");

    check("RootReduce((-35-Sqrt(35))^-1)", //
        "1/1190*(-35+Sqrt(35))");
    check("RootReduce((35-Sqrt(35))^-1)", //
        "1/1190*(35+Sqrt(35))");
    check("RootReduce((35+Sqrt(35))^-1)", //
        "1/1190*(35-Sqrt(35))");
    check("RootReduce((-35+Sqrt(35))^-1)", //
        "1/1190*(-35-Sqrt(35))");
  }

  @Test
  public void testQuadraticIrrationalQ() {
    check("QuadraticIrrationalQ(5*Sqrt(11))", //
        "True");
    check("QuadraticIrrationalQ((7*Sqrt(2) + 1)/11)", //
        "True");
    check("QuadraticIrrationalQ(42)", //
        "False");
    check("QuadraticIrrationalQ({Sqrt(2), Sqrt(3), Sqrt(4)})", //
        "{True,True,False}");
    check("QuadraticIrrationalQ(n)", //
        "False");
    check("QuadraticIrrationalQ(Sqrt(2)+Sqrt(7))", //
        "False");
  }

  @Test
  public void testQuantile() {
    check("Quantile({},1/2)", //
        "Quantile({},1/2)");
    check("Quantile({1,2,3,4,5,6,7},-1/2)", //
        "Quantile({1,2,3,4,5,6,7},-1/2)");
    check("Quantile(WeibullDistribution(2, 5), N(1/4,25))", //
        "2.68180010651325822971629");

    check("Quantile({10, 50, 10, 15, 20}, 3/4, {{1/2, 0}, {0, 1}})", //
        "55/2");
    check("Quantile(NormalDistribution(m, s))", //
        "ConditionalExpression(m-Sqrt(2)*s*InverseErfc(2*#1),0<=#1<=1)&");
    check("Quantile(NormalDistribution(m, s), q)", //
        "ConditionalExpression(m-Sqrt(2)*s*InverseErfc(2*q),0<=q<=1)");

    check("Quantile({1, 2, 3, 4, 5, 6, 7}, 1/2)", //
        "4");

    check("Quantile({1,2}, 0.5)", //
        "1");
    check("Quantile({Sqrt(2), E, Pi, Sqrt(3)}, 1/4)", //
        "Sqrt(2)");
    check("Quantile({Sqrt(2), E, Pi, Sqrt(3)}, 3/4)", //
        "E");
    check("Quantile(N({E, Pi, Sqrt(2), Sqrt(3)}), 1/4)", //
        "1.41421");
    check("Quantile({1, 2, 3, 4, 5, 6, 7}, 1/2)", //
        "4");
    check("Quantile({1, 2, 3, 4, 5, 6, 7}, 1/4)", //
        "2");
    check("Quantile({1, 2, 3, 4, 5, 6, 7}, {1/4, 3/4})", //
        "{2,6}");
    check("Quantile({1.0, 2.0, 3.0, 4.0, 5, 6, 7}, {1/4, 3/4})", //
        "{2.0,6}");

    check("Quantile({{1,2,3,4},{ E, Pi, Sqrt(2),Sqrt(3)}}, 0.75)", //
        "{E,Pi,3,4}");
    check("Quantile({{1,2},{ E, Pi, Sqrt(2),Sqrt(3)}}, 0.75)", //
        "Quantile({{1,2},{E,Pi,Sqrt(2),Sqrt(3)}},0.75)");
    // Quantile[{5, 10, 4, 25, 2, 1}, 1/5, {{1/2, 0}, {0, 1}}]
    check("Quantile({5, 10, 4, 25, 2, 1}, 1/5, {{1/2, 0}, {0, 1}})", //
        "17/10");
  }

  @Test
  public void testQuartiles() {
    check("Quartiles({{-1,-2,3}},{{0,0},{0,0}}+1)", //
        "{{-1,-1,-1},{-2,-2,-2},{3,3,3}}");

    // method 1 from Wikipedia
    check("Quartiles({6, 7, 15, 36, 39, 40, 41, 42, 43, 47, 49}, {{0, 0}, {1, 0}}) // N", //
        "{15.0,40.0,43.0}");
    // method 3 from Wikipedia
    check("Quartiles({6, 7, 15, 36, 39, 40, 41, 42, 43, 47, 49}) // N", //
        "{20.25,40.0,42.75}");

    check("Quartiles({-1, 5, 10, 4, 25, 2, 1}, {{0, 0}, {1, 0}})", //
        "{1,4,10}");
    check("Quartiles(ExponentialDistribution(x))", //
        "{Log(4/3)/x,Log(2)/x,Log(4)/x}");
    check("Quartiles({1, 3, 4, 2, 5, 6})", //
        "{2,7/2,5}");
    check("Quartiles(N({Sin(1), 2, 3, 4},30))", //
        "{1.42073549240394825332625116081,2.5,3.5}");
  }

  @Test
  public void testQuiet() {
    // message: Quiet: Quiet called with 3 arguments; 1 argument is expected.
    check("Quiet(a,b,c)", //
        "Quiet(a,b,c)");
    check("Quiet(1/0)", //
        "ComplexInfinity");
    check("1/0", //
        "ComplexInfinity");
  }

  @Test
  public void testQuotient() {
    check("Table(Quotient(x + y*I, y + x*I), {x, -3.9,3.9,0.5}, {y,  -3.9,3.9,0.5})", //
        "{{1,1,1,1,1-I,1-I,-I,-I,-I,-I,-1-I,-1-I,-1-I,-1,-1,-1},{1,1,1,1,1-I,1-I,-I,-I,-I,-I,-\n"
            + "1-I,-1-I,-1,-1,-1,-1},{1,1,1,1,1,1-I,1-I,-I,-I,-I,-1-I,-1-I,-1,-1,-1,-1},{1,1,1,\n"
            + "1,1,1,1-I,-I,-I,-I,-1-I,-1,-1,-1,-1,-1},{1+I,1+I,1,1,1,1,1-I,-I,-I,-1-I,-1,-1,-1,-\n"
            + "1,-1,-1+I},{1+I,1+I,1+I,1,1,1,1,1-I,-I,-1-I,-1,-1,-1,-1+I,-1+I,-1+I},{I,I,1+I,1+I,\n"
            + "1+I,1,1,1-I,-I,-1,-1,-1+I,-1+I,-1+I,-1+I,I},{I,I,I,I,I,1+I,1+I,1,-I,-1,-1+I,I,I,I,I,I},{I,I,I,I,I,I,I,I,\n"
            + "1,I,I,I,I,I,I,I},{I,I,I,I,-1+I,-1+I,-1,-1,-I,1,1+I,1+I,1+I,I,I,I},{-1+I,-1+I,-1+I,-\n"
            + "1+I,-1,-1,-1,-1-I,-I,1-I,1,1,1+I,1+I,1+I,1+I},{-1+I,-1+I,-1+I,-1,-1,-1,-1-I,-I,-I,\n"
            + "1-I,1,1,1,1,1+I,1+I},{-1+I,-1,-1,-1,-1,-1,-1-I,-I,-I,1-I,1-I,1,1,1,1,1},{-1,-1,-\n"
            + "1,-1,-1,-1-I,-1-I,-I,-I,-I,1-I,1,1,1,1,1},{-1,-1,-1,-1,-1,-1-I,-1-I,-I,-I,-I,1-I,\n"
            + "1-I,1,1,1,1},{-1,-1,-1,-1,-1-I,-1-I,-I,-I,-I,-I,1-I,1-I,1,1,1,1}}");
    check("Table(Quotient(x + y*I, 3.0 - I*2.0), {x, -5, 5}, {y, -5, 5})", //
        "{{-I*2,-1-I*2,-1-I,-1-I,-1-I,-1-I,-1-I,-1,-2,-2,-2},{-I*2,-I*2,-I,-1-I,-1-I,-1-I,-\n"
            + "1,-1,-1,-2,-2+I},{-I*2,-I,-I,-I,-1-I,-1,-1,-1,-1,-1,-1+I},{-I,-I,-I,-I,-I,0,-1,-\n"
            + "1,-1,-1+I,-1+I},{1-I,-I,-I,-I,0,0,0,-1,-1+I,-1+I,-1+I},{1-I,1-I,-I,0,0,0,0,0,I,-\n"
            + "1+I,-1+I},{1-I,1-I,1-I,1,0,0,0,I,I,I,-1+I},{1-I,1-I,1,1,1,0,I,I,I,I,I},{1-I,1,1,\n"
            + "1,1,1,1+I,I,I,I,I*2},{2-I,2,1,1,1,1+I,1+I,1+I,I,I*2,I*2},{2,2,2,1,1+I,1+I,1+I,1+I,\n"
            + "1+I,1+I*2,I*2}}");
    check("Quotient(4.56, 2.5)", //
        "1");
    check("Quotient(E^E^E, Pi)", //
        "1214122");
    check("Quotient(10.4 + I*8.0, 4.0 + I*5.0)", //
        "2");
    check("Quotient(Sqrt(-113), 2)", //
        "I*5");
    check("Quotient(42,Pi)", //
        "13");
    check("Quotient(13, 0)", //
        "ComplexInfinity");
    check("Quotient(-17, 7)", //
        "-3");
    check("Quotient(15, -5)", //
        "-3");
    check("Quotient(17, 5)", //
        "3");
    check("Quotient(-17, -4)", //
        "4");
    check("Quotient(-14, 7)", //
        "-2");
    check("Quotient(19, -4)", //
        "-5");
  }

  @Test
  public void testQuotientRemainder() {
    check("QuotientRemainder(-5.1*Pi,Pi)", //
        "{-6,2.82743}");
    check("QuotientRemainder(5-Pi,Pi)", //
        "{0,5-Pi}");
    check("QuotientRemainder(-1/2+I*1/2, 1/2-I*1/2)", //
        "{-1,0}");

    check("QuotientRemainder(-15/4-I*1/3.0, -1/3-2/33*I)", //
        "{11-I,-0.0227273}");
    check("QuotientRemainder(-15/4-I*1/3, -1/3-2/33*I)", //
        "{11-I,-1/44}");

    check("QuotientRemainder(-15/4-I*1/3.0, 2/33*I)", //
        "{-5+I*62,0.00757576+I*(-0.030303)}");
    check("QuotientRemainder(-15/4+I*1/3.0, 2/33*I)", //
        "{5+I*62,0.00757576+I*0.030303}");
    check("QuotientRemainder(-15/4-I*1/3, 2/33*I)", //
        "{-6+I*62,1/132+I*1/33}");
    check("QuotientRemainder(-15/4+I*1/3, 2/33*I)", //
        "{6+I*62,1/132-I*1/33}");
    check("QuotientRemainder(-15/4+I*1/3.0, 2/3)", //
        "{-6,0.25+I*0.333333}");
    check("QuotientRemainder(-15/4+I*1/3, 2/3)", //
        "{-6,1/4+I*1/3}");
    check("QuotientRemainder(26+120*I,37+226*I)", //
        "{1,-11-I*106}");
    check("QuotientRemainder(13, 0)", //
        "QuotientRemainder(13,0)");
    check("QuotientRemainder(-17, 7)", //
        "{-3,4}");
    check("QuotientRemainder(15, -5)", //
        "{-3,0}");
    check("QuotientRemainder(17, 5)", //
        "{3,2}");
    check("QuotientRemainder(-17, -4)", //
        "{4,-1}");
    check("QuotientRemainder(-14, 7)", //
        "{-2,0}");
    check("QuotientRemainder(19, -4)", //
        "{-5,-1}");
  }

  @Test
  public void testRamp() {
    check("Ramp(-1)", //
        "0");
    check("Ramp(3.7)", //
        "3.7");
    check("Ramp(Pi-E)", //
        "-E+Pi");
  }

  @Test
  public void testRandom() {
    // RandomReal: The specification Infinity is not a random distribution recognized by the
    // system.
    check("RandomReal(Infinity)", //
        "RandomReal(Infinity)");


    // set the seed to get always the same JUnit results
    check("SeedRandom[1234]; RandomReal[]", //
        "0.646582");
    check("SeedRandom[1234]; RandomReal[]", //
        "0.646582");
    check("SeedRandom[987654321]; RandomReal[]", //
        "0.30594");

    check("RandomInteger({0,2147483647})", //
        "RandomInteger({0,2147483647})");
    check("RandomReal(7,{5,4,2,3})", //
        "{{{{5.73144,0.319728,4.94061},{0.0679933,4.72926,0.116555}},{{0.773992,6.94591,2.30291},{3.52974,5.42991,1.12177}},{{3.32875,2.72358,6.15825},{0.697204,2.29928,5.89195}},{{1.10541,6.22831,2.3344},{4.18358,5.5935,0.736481}}},{{{1.67449,0.906546,6.79422},{6.84396,2.51322,5.9361}},{{5.04364,6.46733,4.64001},{1.04158,2.70263,0.215921}},{{3.81838,3.23419,6.47141},{4.44989,5.12608,3.21665}},{{5.41254,5.85401,5.62645},{1.58933,6.62786,4.3783}}},{{{1.72772,3.63391,0.568468},{2.48554,5.41832,1.25711}},{{0.335168,2.82838,6.24056},{3.03068,4.04821,1.75494}},{{2.38406,0.212825,0.430829},{4.4122,0.642352,1.1778}},{{2.16,0.679996,0.182249},{4.81619,4.29105,0.726369}}},{{{2.1253,2.19809,6.15916},{4.62626,4.83428,4.11445}},{{6.60144,5.23054,5.53491},{5.02889,3.12733,6.31147}},{{1.48409,5.48308,5.79296},{5.94312,1.32072,4.10319}},{{0.00705484,4.10168,0.503736},{0.053271,2.31095,0.590767}}},{{{2.85435,6.07331,2.33489},{4.61071,0.175366,1.12545}},{{2.63546,0.598288,4.72051},{0.151525,1.9658,5.36484}},{{1.93,6.51802,2.72058},{4.05592,5.33617,3.3918}},{{0.832009,4.04422,5.87361},{3.55061,3.27097,2.55045}}}}");
    check("RandomReal({0,4},{5,4,2,3})", //
        "{{{{2.85922,1.70673,0.8738},{3.17883,0.0462446,2.3491}},{{3.44193,2.56976,2.43017},{2.51678,1.29868,3.99964}},{{0.507159,0.57407,2.82929},{1.91924,0.616029,2.25034}},{{0.295793,2.93624,0.0917664},{2.59264,1.17732,1.65084}}},{{{0.814614,3.99736,2.44831},{1.79291,3.5879,2.87252}},{{1.25318,1.87824,1.73488},{2.97028,1.7055,0.901991}},{{1.88688,1.5425,3.43454},{0.51331,3.14207,1.21088}},{{3.0212,1.66593,2.60619},{1.80588,0.610305,3.91522}}},{{{1.78368,2.41743,0.75524},{2.33301,0.369574,2.51342}},{{0.946016,0.437311,1.69956},{0.220327,1.29092,1.58481}},{{1.44784,1.72371,0.466719},{1.94973,0.119095,2.23436}},{{2.32171,1.5136,2.46327},{0.0793879,0.410682,3.41606}}},{{{3.14867,1.21575,2.26017},{2.56774,3.86094,1.82695}},{{1.30695,3.60202,2.45262},{1.65196,2.22244,0.0431879}},{{3.84996,0.308677,2.72992},{2.99436,1.1306,3.11259}},{{0.0412127,0.699323,3.5179},{3.75446,2.68401,0.00810998}}},{{{1.99472,2.68929,0.760376},{3.07714,2.82133,2.20191}},{{3.57165,3.56024,0.449397},{3.85303,1.06455,2.09794}},{{0.155784,1.3727,0.697554},{1.08796,1.17714,0.0565625}},{{2.0965,3.52074,1.20815},{2.33334,2.26257,2.08967}}}}");

    check("RandomInteger(20,{10,3})", //
        "{{2,2,19},{8,2,1},{11,17,4},{9,8,13},{13,0,15},{14,11,13},{4,13,7},{18,19,2},{5,\n"
            + "8,0},{1,16,1}}");

    check("RandomInteger(7,{5,4,2,3})", //
        "{{{{6,2,5},{3,7,1}},{{5,3,2},{1,3,1}},{{6,4,7},{6,6,3}},{{7,1,2},{6,5,3}}},{{{3,\n"
            + "1,1},{5,6,1}},{{0,0,4},{6,6,6}},{{7,1,1},{0,0,0}},{{6,0,7},{0,0,0}}},{{{3,3,7},{\n"
            + "3,3,3}},{{6,4,5},{6,0,4}},{{6,2,4},{7,1,3}},{{7,1,4},{4,2,6}}},{{{0,3,4},{1,0,1}},{{\n"
            + "1,6,4},{0,4,2}},{{6,6,1},{2,5,2}},{{6,1,1},{5,4,6}}},{{{7,3,1},{6,6,2}},{{1,6,2},{\n"
            + "3,5,5}},{{0,7,2},{0,1,4}},{{5,6,2},{0,6,5}}}}");
    check("RandomInteger({0,4},{5,4,2,3})", //
        "{{{{0,4,2},{0,0,0}},{{3,4,3},{3,3,4}},{{3,4,3},{0,2,1}},{{0,1,0},{4,2,0}}},{{{3,\n"
            + "3,0},{0,1,1}},{{0,1,3},{3,1,4}},{{0,0,3},{0,2,1}},{{4,4,4},{2,2,4}}},{{{3,0,2},{\n"
            + "3,3,2}},{{0,0,0},{2,2,2}},{{0,2,0},{1,3,3}},{{3,1,0},{2,0,0}}},{{{3,3,3},{3,3,3}},{{\n"
            + "2,3,0},{3,0,2}},{{2,4,4},{2,0,2}},{{0,3,3},{1,4,2}}},{{{4,3,0},{3,3,4}},{{1,0,4},{\n"
            + "0,1,2}},{{0,4,4},{2,1,0}},{{1,4,4},{0,0,1}}}}");

    check("RandomInteger()", //
        "1");
    check("RandomInteger(-10)", //
        "-7");
    check("RandomInteger(100)", //
        "91");
    check("RandomReal()", //
        "0.441958");

    String expected = String.join("\n", //
        "  1.53368   2.64244  0.603094  1.93993  0.757192   2.52053    3.6458    4.3667  0.0264575  1.62091   3.42147 ", //
        "  1.95564    3.8064  0.156164  2.52057   1.43282   3.08827   2.00007   2.09918   0.794163  1.28679   3.92551 ", //
        "  1.85792   1.60386  0.440516  3.06543   4.53076  0.156222   3.41509   2.07223    1.42567  1.66608   2.34519 ", //
        " 0.244549   1.42317   4.90009  1.99318   2.84108   3.10954  0.564542   4.05133    4.90708  3.39802  0.324629 ", //
        "   4.6692  0.202169   1.07454  4.54645     2.649  0.236909  0.193603  0.724548    4.81904  3.00081   1.14547 ", //
        "  1.08643   3.06911  0.695871  1.51773   2.05006   4.41148  0.677521   4.58214  0.0509575  1.21727   3.84864 ", //
        " 0.351929   2.75283   3.47041  4.95268   2.67536   1.80884   3.48008   4.56827   0.758013  3.15025  0.997234 ");
    check("RandomReal(5, {7, 11}) // TableForm", //
        expected);

    // distributions use org.hipparchus.random.RandomDataGenerator.RandomDataGenerator() which often
    // doesn't depend on java.util.Random where we set the seed.

    // check("RandomReal(ChiSquareDistribution(5),5)", //
    // "{6.88724,17.61347,4.88216,3.42269,1.84075}");
    // check("RandomReal(WeibullDistribution(10,6),5)", //
    // "{6.04801,4.26361,5.57834,5.60149,5.86845}");
    // check("RandomReal(CauchyDistribution(10,6),5)", //
    // "{25.46486,10.17115,9.24139,6.02044,-3.21386}");
    // check("RandomReal(NormalDistribution(10,6),5)", //
    // "{15.39771,18.15348,3.84502,4.17546,13.37926}");

    // message: RandomPrime: Positive integer value expected.
    check("RandomPrime(#2,{1,1,1,1})", //
        "RandomPrime(#2,{1,1,1,1})");
    check("RandomPrime(-11)", //
        "RandomPrime(-11)");
    // message: RandomPrime: There are no primes in the specified interval.
    check("RandomPrime(1)", //
        "RandomPrime(1)");
    check("RandomPrime(2)", //
        "2");
    // check("RandomPrime(100000000000000000000000000)", //
    // "87660272303062923753002687");
    //

  }

  @Test
  public void testRandomChoice() {
    // check(
    // "RandomChoice({1, 10, 5} -> {a, b, c}, {3,3})", //
    // "{c,a,a,b,c,c,b,b,a,c,b,b,c,b,b,c,b,b,b,b}");
    // check(
    // "RandomChoice({1, 10, 5} -> {a, b, c}, 20)", //
    // "{c,a,a,b,c,c,b,b,a,c,b,b,c,b,b,c,b,b,b,b}");
    // check(
    // "RandomChoice({a,b,c}, {5,2})", //
    // "{{b,c},{c,c},{a,c},{a,c},{c,b}}");
    // check("Table(StringJoin(RandomChoice(CharacterRange(\"a\", \"z\"), 5)), {10}) // InputForm",
    // //
    // "{\"jbuhp\",\"uneaw\",\"icixu\",\"vsrsy\",\"ycxsx\",\"atfvl\",\"kivvj\",\"xjllp\",\"xtwms\",\"ixwuk\"}");

    // check("RandomChoice({1,2,3,4,5,6,7},11.0)", "{2,1,5,3,5,7,4,5,5,6,5}");
    // check("RandomChoice({1,2,3,4,5,6,7},10)", "{3,7,3,6,2,7,4,1,1,4}");
    // check("RandomChoice({False, True})", "True");
    // check("RandomChoice({1,2,3,4,5,6,7})", "3");
  }

  @Test
  public void testRandomComplex() {
    // check(
    // "RandomComplex(2+2I )", //
    // "1.07196+I*1.10905");
    // check(
    // "RandomComplex( )", //
    // "0.313565+I*0.954076");
    // check(
    // "RandomComplex({1+I, 2+2I}, {2,2,3})", //
    //
    // "{{{1.61894+I*1.62895,1.42982+I*1.64042,1.88055+I*1.24075},{1.27681+I*1.09553,1.29139+I*1.79987,1.47368+I*1.59429}},{{1.42116+I*1.54729,1.51395+I*1.05403,1.47495+I*1.7832},{1.79694+I*1.1428,1.93639+I*1.50855,1.51072+I*1.02286}}}");

    // check("RandomComplex({2 + I, 10 + 20*I}, {3, 2})", //
    // "{{2.33543+I*15.51255,6.05421+I*7.19415},{4.84163+I*3.70955,8.92918+I*16.73326},{8.78651+I*5.42401,8.82129+I*15.18506}}");
    // check("RandomComplex( )", //
    // "0.320015+I*0.506726");
    // check("RandomComplex({-2-I,5+3*I})", //
    // "0.61304+I*(-0.482746)");
  }

  @Test
  public void testRandomPermutation() {
    // check(
    // "RandomPermutation(10)", //
    // "Cycles({{1,2,7,3,8,10,5,9,4,6}})");
    // check(
    // "RandomPermutation(10,3)", //
    // "{Cycles({{1,6,4,5,7,10,9,2,3,8}}),Cycles({{1,10,9,4,2,6,3,8,7,5}}),Cycles({{1,4,\n"
    // + "2,6,8,9,5,7,10,3}})}");
  }

  @Test
  public void testRandomReal() {
    // check("RandomReal(WorkingPrecision -> 50)", //
    // "0.02914582860934237466618813817377889841321841114067");
    check("NonNegative(RandomReal(-Sqrt(2)/2)*(-1.0))", //
        "True");
    check("NonNegative(RandomReal({-Sqrt(2)/2,-1.0})*(-1.0))", //
        "True");
  }

  @Test
  public void testRandomVariate() {
    // message: RandomVariate: The first argument aa is not a valid distribution.
    check("RandomVariate(aa)", //
        "RandomVariate(aa)");
    // message: RandomVariate: The specification lognormal(0.1,1.7) is not a random distribution
    // recognized
    // by the system.
    check("RandomVariate(lognormal(0.1,1.7) )", //
        "RandomVariate(lognormal(0.1,1.7))");
    // check("RandomVariate(HypergeometricDistribution(44,18,57), 10^2)", //
    // "{16,16,14,11,14,13,14,14,13,15,13,11,14,15,15,13,15,13,14,15,15,13,16,13,14,12,\n" // +
    // "16,12,14,13,13,17,16,15,16,12,15,13,13,16,15,16,15,14,15,14,10,15,17,14,15,13,14,\n" // +
    // "13,11,15,15,15,16,15,14,12,16,13,15,16,14,12,13,15,12,13,15,12,12,10,14,16,15,12,\n" // +
    // "15,16,16,14,14,13,17,14,15,13,15,15,14,15,15,12,12,14,13,15}");

    // check("RandomVariate(GammaDistribution(0.5,0.6), {2,3,4})", //
    // "{{{0.000793278,0.0921714,0.0523716,0.100137},"//
    // + "{0.104185,0.0459276,3.79475,0.247275},"//
    // + "{0.0363476,0.843459,0.662268,0.0752151}},"//
    // //
    // + "{{0.00037008,0.125594,0.0058051,0.158089},"
    // + "{0.573566,0.128191,0.00204638,0.819725},"//
    // + "{0.407606,0.00820377,0.0115433,0.107513}}}");
    // check("RandomVariate(DiscreteUniformDistribution({1,15}), 10^1)", //
    // "{14,1,9,11,5,11,11,5,13,9}");
    // check("RandomVariate(ExponentialDistribution(1), 10^1)", //
    // "{0.304049,0.242275,0.291415,1.28545,0.567106,1.02787,3.29483,4.40819,9.03388,0.375482}");
    //
    // check("RandomVariate(BetaDistribution(0.25,0.75), 10^1)", //
    // "{0.0598983,0.109825,0.00899438,0.127621,0.000186889,0.00620042,0.213545,0.82361,0.0000629664,0.465407}");
    // check("RandomVariate(UniformDistribution({0,2}), 10^1)", //
    // "{1.95015,1.42461,0.379616,0.828009,1.29886,1.74158,0.792286,0.651039,1.32392,1.71367}");
    // check("RandomVariate(BinomialDistribution(100,0.25), 10^1)", //
    // "{28,25,30,25,25,26,29,17,33,20}");

    // check("RandomVariate(BetaDistribution(0.5,0.6), {10})", //
    // "{0.651565,0.0687826,0.53907,0.511176,0.0419515,0.946387,0.995215,0.0896617,0.00242461,0.607517}");
    // check("RandomVariate(FrechetDistribution(0.5,0.6), {10})", //
    // "{288.7521,2.9714,0.403198,2.0156,0.21531,0.0399206,0.665026,1.49444,434.9269,118.3019}");
    // check("RandomVariate(GumbelDistribution(0.5,0.6), {10})", //
    // "{0.983572,1.01258,1.02586,0.351624,0.674945,0.549278,0.173217,0.464434,-0.335133,-0.151538}");
    // check("RandomVariate(GammaDistribution(0.5,0.6), {10})", //
    // "{0.08716,0.39611,0.04844,0.03546,0.57366,0.02071,0.01487,1.65639,0.75104,0.05348}");
    // check("RandomVariate(UniformDistribution({1,3}), {2})", //
    // "{1.95941,2.69658}");
    // check("RandomVariate(NormalDistribution(), 10^1)", //
    // "");
    // check("KolmogorovSmirnovTest({-0.79675,0.3841,-0.84567,0.3421,0.46447,-0.01124,-0.33517,0.82206,1.40563,0.48811},
    // NormalDistribution())", //
    // "0.56794");
    // check("KolmogorovSmirnovTest({-0.79675,0.3841,-0.84567,0.3421,0.46447,-0.01124,-0.33517,0.82206,1.40563,0.48811})",
    // //
    // "0.99446");
    // check("KolmogorovSmirnovTest({-0.79675,0.3841,-0.84567,0.3421,0.46447,-0.01124,-0.33517,0.82206,1.40563,0.48811},"
    // + "{1.64433,-0.31318,1.27263,0.16141,0.21162,-1.01509,-0.76259,0.73259,0.2478,1.28021})", //
    // "0.7869");
    // check("RandomVariate(BinomialDistribution(100,0.25), 10^1)", //
    // "{28,25,30,25,25,26,29,17,33,20}");
    // check("RandomVariate(BernoulliDistribution(0.25), 10^1)", //
    // "{1,0,0,0,1,0,0,0,0,0}");
    // check("RandomVariate(ExponentialDistribution(5.6), 10^1)", //
    // "{0.36309,0.10609,0.14096,0.11642,0.01146,0.11286,0.05236,0.00071,0.01648,0.0303}");
    // check("RandomVariate(DiscreteUniformDistribution({50,1000}), 10^1)", //
    // "{468,989,156,353,469,91,399,304,700,137}");
    // check("RandomVariate(PoissonDistribution(2.0), 10^1)", //
    // "{1,3,5,3,2,2,2,5,1,2}");
    // check("RandomVariate(NormalDistribution(2,3), 10^1)", //
    // "{3.16579,3.4267,6.43772,4.53451,3.45249,6.51662,2.10209,-3.8462,3.87387,-4.47763}");
    // check("RandomVariate(NormalDistribution(2,3))", //
    // "1.99583");
    // check("RandomVariate(NormalDistribution())", //
    // "-0.56291");
    // check("RandomVariate(DiscreteUniformDistribution({3,7}), {2})", "{3,7}");
    // check("RandomVariate(DiscreteUniformDistribution({3,7}), {2,3})", "{{5,4,7},{5,7,3}}");
    // check("RandomVariate(DiscreteUniformDistribution({3,7}), {2,3,4})",
    // "{{{4,5,5,3},{5,4,4,6},{6,3,4,7}},{{6,6,7,3},{4,6,5,6},{7,7,6,5}}}");
    // check("RandomVariate(DiscreteUniformDistribution({3,7}), 10)", "{6,5,7,7,7,7,4,5,6,3}");
    // check("RandomVariate(DiscreteUniformDistribution({1, 5}) )", "3");
  }

  @Test
  public void testRandomSample() {
    // check("RandomSample(f(1,2,3,4,5),3)", //
    // "f(3,4,1)");
    // check("RandomSample(f(1,2,3,4,5))", //
    // "f(3,4,5,1,2)");
  }

  @Test
  public void testRange() {
    check("Range(0,10,Pi)", //
        "{0,Pi,2*Pi,3*Pi}");

    check("Range(1+Sqrt(2),-2+I*2)", //
        "Range(1+Sqrt(2),-2+I*2)");
    check("Range(I, I+3)", //
        "{I,1+I,2+I,3+I}");


    check("Range(a, b, (b - a)/4)", //
        "{a,a+1/4*(-a+b),a+1/2*(-a+b),a+3/4*(-a+b),b}");

    byte[] b0Array = new byte[] {0};
    ByteArrayExpr b0a = ByteArrayExpr.newInstance(b0Array);
    IAST range = F.Range(F.CNI, b0a, F.Quantity(F.num(1.2), F.stringx("m")));
    check(range, //
        "{}");
    check("Range(-Infinity,0.5)", //
        "Range(-Infinity,0.5)");
    check("Range(a,b,ComplexInfinity)", //
        "a");
    check("Range(a,b,-Infinity)", //
        "a");
    check("Range(a,b,Infinity)", //
        "a");

    check("Range(1,1.25,0)", //
        "Range(1,1.25,0)");
    check("Range(1,-1 )", //
        "{}");
    check("Range(1,-1,1/2)", //
        "{}");
    check("Range(1,1+1/2,0)", //
        "Range(1,3/2,0)");
    check("Range(1,1+1/2,1/2)", //
        "{1,3/2}");
    check("Range(1,1.25,0.1)", //
        "{1.0,1.1,1.2}");
    check("Range(5.0)", //
        "{1,2,3,4,5}");
    check("Range(-5.0)", //
        "{}");

    check("Range(0,10,Pi)", //
        "{0,Pi,2*Pi,3*Pi}");
    check("x * Range(-1, 1, 1/5)", //
        "{-x,-4/5*x,-3/5*x,-2/5*x,-x/5,0,x/5,2/5*x,3/5*x,4/5*x,x}");
    check("a + Range(0, 3, Pi/8)", //
        "{a,a+Pi/8,a+Pi/4,a+3/8*Pi,a+Pi/2,a+5/8*Pi,a+3/4*Pi,a+7/8*Pi}");
    check("x^Range(n, n + 10, 2)", //
        "{x^n,x^(2+n),x^(4+n),x^(6+n),x^(8+n),x^(10+n)}");
    check("Range(a, a + 12*n, 2*n)", //
        "{a,a+2*n,a+4*n,a+6*n,a+8*n,a+10*n,a+12*n}");

    check("Range(5)", //
        "{1,2,3,4,5}");
    check("Range(-3, 2)", //
        "{-3,-2,-1,0,1,2}");
    check("Range(0, 2, 1/3)", //
        "{0,1/3,2/3,1,4/3,5/3,2}");
    check("Range(1.2, 2.2, 0.15)", //
        "{1.2,1.35,1.5,1.65,1.8,1.95,2.1}");

    check("Range(0)", //
        "{}");
    check("Range(1)", //
        "{1}");
    check("Range(-1)", //
        "{}");
    check("Range(10)", //
        "{1,2,3,4,5,6,7,8,9,10}");
    check("Range(1,10,2)", //
        "{1,3,5,7,9}");
    check("Range(10,20,3)", //
        "{10,13,16,19}");
    check("Range(10,1,-1)", //
        "{10,9,8,7,6,5,4,3,2,1}");
  }

  @Test
  public void testRankedMax() {
    // message: RankedMax: Input {1,I,E} is not a vector of reals or integers.
    check("RankedMax({1, I, E}, 2)", //
        "RankedMax({1,I,E},2)");

    check("RankedMax({12,13,11}, 2)", //
        "12");
    check("RankedMax({Pi, Sqrt(3), 2.95, 3}, 3)", //
        "2.95");
    check("RankedMax({12.99, 3.775, 25.33}, 2)", //
        "12.99");

    check("RankedMax({2.5, E, 12, 15, 485}, -2)", //
        "E");
    check("RankedMax({2.5, E, 12, 15, 485}, -4)", //
        "15");
    check("RankedMax({2.5, E, 12, 15, 485}, -5)", //
        "485");
    check("RankedMax({Infinity,5, Infinity, -Infinity}, 2)", //
        "Infinity");
    check("RankedMax({Infinity,5, Infinity, -Infinity}, -1)", //
        "-Infinity");
  }

  @Test
  public void testRankedMin() {
    check(
        "RankedMin({Quantity(1, \"Kilograms\"), Quantity(2, \"kg\"), Quantity(3, \"Kilograms\")}, 2)", //
        "2[kg]");
    check("RankedMin({Quantity(1, \"kg\"), Quantity(2, \"kg\"), Quantity(3, \"kg\")}, 2)", //
        "2[kg]");
    check("RankedMin(<|a -> 1, b -> 2, c -> 3, d -> 4|>, 2)", //
        "2");

    // message: RankedMin: Input {1,I,E} is not a vector of reals or integers.
    check("RankedMin({1, I, E}, 2)", //
        "RankedMin({1,I,E},2)");

    check("RankedMin({12,13,11}, 2)", //
        "12");
    check("RankedMin({Pi, Sqrt(3), 2.95, 3}, 3)", //
        "3");
    check("RankedMin({12.99, 3.775, 25.33}, 2)", //
        "12.99");

    check("RankedMin({2.5, E, 12, 15, 485}, -2)", //
        "15");
    check("RankedMin({2.5, E, 12, 15, 485}, -4)", //
        "E");
    check("RankedMin({2.5, E, 12, 15, 485}, -5)", //
        "2.5");
  }

  @Test
  public void testRational() {
    check("1.29600*10^-17", //
        "1.296*10^-17");
    check("1.29600*^-17", //
        "1.296*10^-17");
    check("6*^3", //
        "6000");
    check("6*^-1", //
        "3/5");
    check("6*^0", //
        "6");
    check("Head(1/2)", //
        "Rational");
    check("Rational(1, 2)", //
        "1/2");
    check("-2/3", //
        "-2/3");
    check("f(22/7, 201/64, x/y) /. Rational(n_, d_) :> d/n", //
        "f(7/22,64/201,x/y)");
  }

  @Test
  public void testRationalizeIssue1065() {
    // issue 1065
    checkNumeric("Rationalize(878159.58,1*10^-12) - Rationalize(431874.32,1*10^-12)", //
        "22314263/50");
    checkNumeric("22314263/50 // N", //
        "446285.26");
  }

  @Test
  public void testRationalize() {

    // check("Rationalize(0.1234567*^2)", //
    // "1234567/100000");
    // // check("Rationalize(0.12345678*^2)", //
    // // "12.3457");
    // check("Rationalize(0.123*^100)", //
    // "1.23*10^99");
    // check("Rationalize(0.1*^-99)", //
    // "1.*10^-100");
    // check("Rationalize(0.1*^-11)", //
    // "1.*10^-12");
    // check("Rationalize(0.1*^-11)", //
    // "0");
    // check("Rationalize(0.1*^-10)", //
    // "1/100000000000");
    // check("Rationalize(0.123*^10)", //
    // "1230000000");
    // check("Rationalize(0.202898)", //
    // "101449/500000");
    // check("Rationalize(1.1/2147483647 *2.5,0)", //
    // "245850922/78256779");

    check("Rationalize(0.000000000008854187817)", //
        "8.85419*10^-12");
    check("Rationalize(N(Pi), 0)", //
        "884279719003555/281474976710656");
    check("Rationalize(0.202898)", //
        "101449/500000");


    check("Rationalize(Sin(3.0),0)", //
        "5084384125703515/36028797018963968");
    check("Rationalize(N(Pi),0)", //
        "884279719003555/281474976710656");
    check("Rationalize(x+E)", //
        "E+x");
    check("Rationalize(E)", //
        "E");
    check("Rationalize(E, 0.01)", //
        "19/7");
    check("Rationalize(x+E, 0.01)", //
        "E+x");
    check("Rationalize(x+y)", //
        "x+y");
    check("Rationalize(x+0.3333*y)", //
        "x+3333/10000*y");
    check("ArcCos(-Rationalize(0.5))", //
        "2/3*Pi");
    check("Rationalize(0.202898)", //
        "101449/500000");
    check("Rationalize(1.2 + 6.7*x)", //
        "6/5+67/10*x");
    check("Rationalize(-1.2 - 6.7*x)", //
        "-6/5-67/10*x");
    check("Rationalize(Exp(Sqrt(2)), 2^-12)", //
        "218/53");
    check("Rationalize(6.75)", //
        "27/4");
    check("Rationalize(-6.75)", //
        "-27/4");
    check("Rationalize(Pi)", //
        "Pi");
    check("Rationalize(Pi, .01)", //
        "22/7");
    check("Rationalize(Pi, .001)", //
        "333/106");
  }

  @Test
  public void testRe() {
    //
    check("Re(I*Arg(z) + Log(2) + (1/2)*Log(Im(z)^2 + Re(z)^2))", //
        "Log(2)+Re(Log(Im(z)^2+Re(z)^2))/2");
    check("Re(I*Im(z)+x+y+Im(t)*I+Re(u))", //
        "Re(u+x+y)");

    check("Re(Quantity(2,\"m\"))", //
        "2[m]");
    check("Re(Quantity(a,\"m\"))", //
        "Re(a)[m]");
    check("Re(I*Pi/4 )", //
        "0");
    check("Re(E^(I*Pi/4))", //
        "1/Sqrt(2)");
    check("Re(Sin(42)*Cos(43))", //
        "Cos(43)*Sin(42)");
    check("Re(Sin(42))", //
        "Sin(42)");
    check("Re(I*9*Sin(5))", //
        "0");
    check("Re(3+4*I)", //
        "3");
    check("Re(0.5 + 2.3*I)", //
        "0.5");
    check("Im(0.5 + 2.3*I)", //
        "2.3");
    check("Re(0)", //
        "0");
    check("Re(I)", //
        "0");
    check("Re(Indeterminate)", //
        "Indeterminate");
    check("Re(Infinity)", //
        "Infinity");
    check("Re(-Infinity)", //
        "-Infinity");
    check("Re(ComplexInfinity)", //
        "Indeterminate");
  }

  @Test
  public void testReIm() {
    check("ReIm(Exp(2*I*Pi/3))", //
        "{-1/2,Sqrt(3)/2}");
    check("ReIm((-17)^(1/4))", //
        "{17^(1/4)/Sqrt(2),17^(1/4)/Sqrt(2)}");
    check("ReIm(2*z)", //
        "{2*Re(z),2*Im(z)}");
    check("ReIm(Gamma(-1/2))", //
        "{-2*Sqrt(Pi),0}");
    check("ReIm({a, {b, c}})", //
        "{{Re(a),Im(a)},{{Re(b),Im(b)},{Re(c),Im(c)}}}");

    check("ReIm(3+4*I)", //
        "{3,4}");
    check("ReIm(0.5 + 2.3*I)", //
        "{0.5,2.3}");

    check("ReIm({I, -I, 0})", //
        "{{0,1},{0,-1},{0,0}}");
    check("ReIm(z) /. z -> {I, -I, 0}", //
        "{{0,0,0},{1,-1,0}}");

    check("ReIm(SparseArray({{1} -> I, {3} -> a, {4} -> -Pi}, {5})) // MatrixForm", //
        "{{0,1},\n"//
            + " {0,0},\n"//
            + " {Re(a),Im(a)},\n"//
            + " {-Pi,0},\n"//
            + " {0,0}}");
  }

  @Test
  public void testReal() {
    check("Head(1.5)", //
        "Real");
  }

  @Test
  public void testRealDigits() {
    // message RealDigits: Non-negative machine-sized integer expected at position -1 in 3.
    check("RealDigits(Pi,2,-1,2)", //
        "RealDigits(Pi,2,-1,2)");


    check("RealDigits(5.635, 10, 20)", //
        "{{5,6,3,5,0,0,0,0,0,0,0,0,0,0,0,0,Indeterminate,Indeterminate,Indeterminate,Indeterminate},\n" //
            + "1}");

    check("RealDigits(Pi,2,1,2)", //
        "{{0},3}");
    check("RealDigits(Pi,2,1,1)", //
        "{{1},2}");
    check("RealDigits(Pi,2,1,0)", //
        "{{1},1}");
    check("RealDigits(Pi,2,1,-1)", //
        "{{0},0}");
    check("RealDigits(Pi,2,1,-2)", //
        "{{0},-1}");
    check("RealDigits(Pi,2,1,-3)", //
        "{{1},-2}");

    check("RealDigits(Pi,2,0,2)", //
        "{{},3}");
    check("RealDigits(Pi,2,0,1)", //
        "{{},2}");
    check("RealDigits(Pi,2,0,0)", //
        "{{},1}");
    check("RealDigits(Pi,2,0,-1)", //
        "{{},0}");
    check("RealDigits(Pi,2,0,-2)", //
        "{{},-1}");
    check("RealDigits(Pi,2,0,-3)", //
        "{{},-2}");

    check("RealDigits(0)", //
        "{{0},1}");
    check("RealDigits(0,20)", //
        "{{0},1}");
    check("RealDigits(0.1, 2)", //
        "{{1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,\n" //
            + "0,1,1,0,0,1,1,0,0,1,1,0,0,1},-3}");
    check("RealDigits(Pi, 10, 1, -500)", //
        "{{2},-499}");
    check("RealDigits(Pi, 10, 1, -501)", //
        "{{9},-500}");


    check("RealDigits(Pi, 10, 20, -5)", //
        "{{9,2,6,5,3,5,8,9,7,9,3,2,3,8,4,6,2,6,4,3},-4}");
    check("RealDigits(Pi, 10, 20, 5)", //
        "{{0,0,0,0,0,3,1,4,1,5,9,2,6,5,3,5,8,9,7,9},6}");

    check("RealDigits(1.234, 2, 15)", //
        "{{1,0,0,1,1,1,0,1,1,1,1,1,0,0,1},1}");
    check("RealDigits(19/7, 10, 25)", //
        "{{2,7,1,4,2,8,5,7,1,4,2,8,5,7,1,4,2,8,5,7,1,4,2,8,5},1}");

    check("RealDigits(2,10,8,-1)", //
        "{{0,0,0,0,0,0,0,0},0}");
    check("RealDigits(2,10,8,0)", //
        "{{2,0,0,0,0,0,0,0},1}");
    check("RealDigits(423.012345678*^-11)", //
        "{{4,2,3,0,1,2,3,4,5,6,7,8,0,0,0,0},-8}");
    check("RealDigits(423.012345678*^9)", //
        "{{4,2,3,0,1,2,3,4,5,6,7,8,0,0,0,0},12}");
    check("RealDigits(1.012345678*^-11)", //
        "{{1,0,1,2,3,4,5,6,7,8,0,0,0,0,0,0},-10}");
    check("RealDigits(1.012345678*^9)", //
        "{{1,0,1,2,3,4,5,6,7,8,0,0,0,0,0,0},10}");
    check("RealDigits(1.012345678*^-42)", //
        "{{1,0,1,2,3,4,5,6,7,8,0,0,0,0,0,0},-41}");
    check("RealDigits(1.012345678*^123)", //
        "{{1,0,1,2,3,4,5,6,7,8,0,0,0,0,0,0},124}");
    check(" RealDigits(N(Pi+42 ,30))", //
        "{{4,5,1,4,1,5,9,2,6,5,3,5,8,9,7,9,3,2,3,8,4,6,2,6,4,3,3,8,3,2},2}");
    check("N(RealDigits(Pi+42),30)", //
        "{{4,5,1,4,1,5,9,2,6,5,3,5,8,9,7,9,3,2,3,8,4,6,2,6,4,3,3,8,3,2},2}");
    check("RealDigits(-42)", //
        "{{4,2},2}");
    check("RealDigits(123.33333)", //
        "{{1,2,3,3,3,3,3,3,0,0,0,0,0,0,0,0},3}");
  }

  @Test
  public void testRealDigitsRational() {
    check("RealDigits(19/7)", //
        "{{2,{7,1,4,2,8,5}},1}");
    check("RealDigits(19/7, 11)", //
        "{{2,{7,9,4}},1}");
    check("RealDigits(1/7)", //
        "{{{1,4,2,8,5,7}},0}");
    check("RealDigits(1/32)", //
        "{{3,1,2,5},-1}");
    check("RealDigits(33/32)", //
        "{{1,0,3,1,2,5},1}");
    check("RealDigits(1/11)", //
        "{{{9,0}},-1}");
    check("RealDigits(12/11)", //
        "{{1,{0,9}},1}");
    check("RealDigits(1/81)", //
        "{{{1,2,3,4,5,6,7,9,0}},-1}");
    check("RealDigits(10/13)", //
        "{{{7,6,9,2,3,0}},0}");
    check("RealDigits(1/36)", //
        "{{2,{7}},-1}");
    check("RealDigits(1/37)", //
        "{{{2,7,0}},-1}");
    check("RealDigits(1/97)", //
        "{{{1,0,3,0,9,2,7,8,3,5,0,5,1,5,4,6,3,9,1,7,5,2,5,7,7,3,1,9,5,8,7,6,2,8,8,6,5,9,7,\n" //
            + "9,3,8,1,4,4,3,2,9,8,9,6,9,0,7,2,1,6,4,9,4,8,4,5,3,6,0,8,2,4,7,4,2,2,6,8,0,4,1,2,\n" //
            + "3,7,1,1,3,4,0,2,0,6,1,8,5,5,6,7,0}},-1}");
    check("RealDigits(201/10)", //
        "{{2,0,1},2}");
    check("RealDigits(141/7)", //
        "{{2,0,{1,4,2,8,5,7}},2}");

  }

  @Test
  public void testRealValuedNumberQ() {
    // TODO
    // check("RealValuedNumberQ(Overflow())", //
    // "True");
    check("RealValuedNumberQ(10)", //
        "True");
    check("RealValuedNumberQ(4.0)", //
        "True");
    check("RealValuedNumberQ(1+I)", //
        "False");
    check("RealValuedNumberQ(0*I)", //
        "True");
    check("RealValuedNumberQ(0.0*I)", //
        "False");
    check("RealValuedNumberQ(Infinity)", //
        "False");
  }

  @Test
  public void testRealValuedNumericQ() {
    check("RealValuedNumericQ(10)", //
        "True");
    check("RealValuedNumericQ(4.0)", //
        "True");
    check("RealValuedNumericQ(1+I)", //
        "False");
    check("RealValuedNumericQ(0*I)", //
        "True");
    check("RealValuedNumericQ(0.0*I)", //
        "False");
    check("RealValuedNumericQ(Infinity)", //
        "False");

    // TODO fix Underflow(),Overflow() implementation
    check("TableForm(\n" //
        + " Table({x, RealValuedNumberQ(x), " //
        + "   RealValuedNumericQ(x)}, {x, {1, 3/2, 1.5, 1 + I, E, Sin(1), Underflow(), Overflow(), Infinity}}))", //
        "           1   True   True \n" //
            + "         3/2   True   True \n" //
            + "         1.5   True   True \n" //
            + "         1+I  False  False \n" //
            + "           E  False   True \n" //
            + "      Sin(1)  False   True \n" //
            + " Underflow()   True   True \n" //
            + "  Overflow()   True   True \n" //
            + "    Infinity  False  False ");
  }

  @Test
  public void testRealAbs() {

    check("xval = Solve(RealAbs(x) == 2, x)", //
        "{{x->-2},{x->2}}");
    check("N(RealAbs(Pi - Catalan), 50)", //
        "2.2256270594125742234080398683471187734230200250934");

    check("RealAbs(Indeterminate)", //
        "Indeterminate");
    check("RealAbs(-Infinity)", //
        "Infinity");
    check("RealAbs({1.2, 1.5, 0})", //
        "{1.2,1.5,0}");
    check("RealAbs(Interval({-1, 2}))", //
        "Interval({0,2})");
    check("D(RealAbs(x),x)", //
        "x/RealAbs(x)");
    check("Integrate(RealAbs(x),x)", //
        "1/2*x*RealAbs(x)");

    check("PiecewiseExpand(RealAbs(x))", //
        "Piecewise({{-x,x<0}},x)");
  }

  @Test
  public void testRealSign() {
    check("RealSign(1.1)", //
        "1");
    check("RealSign(RealSign(x))", //
        "RealSign(x)");

    // TODO ? return -Abs(a)+Abs(b)
    check("Integrate(RealSign(x),{x,a,b})", //
        "-RealAbs(a)+RealAbs(b)");
    check("Integrate(RealSign(x),x)", //
        "RealAbs(x)");
    check("D(RealSign(x),x)", //
        "Piecewise({{0,x!=0}},Indeterminate)");
    check("RealSign(-Infinity)", //
        "-1");
    check("RealSign({3, -5, 2 + 5 I})", //
        "{1,-1,RealSign(2+I*5)}");
    check("Sign({3, -5, 2 + 5 I})", //
        "{1,-1,(2+I*5)/Sqrt(29)}");
    check("RealSign(0``200)", //
        "0");
    check("RealSign({\n" //
        + "   {1, u},\n" + "   {v, -1}\n" + "  })", //
        "{{1,RealSign(u)},{RealSign(v),-1}}");
    check("PiecewiseExpand(RealSign(x))", //
        "Piecewise({{-1,x<0},{1,x>0}},0)");
  }

  @Test
  public void testReap() {

    // Sow with no sourrounding Reap
    // prints the numbers on console
    check("Do(Sow(i);Print(i), {i, 4})", //
        "");

    check(
        "depthFirstPreorder(expr_) := Module(\n" + "  {stack = {expr, {}}, el = expr},\n"
            + "  Reap(\n" + "    While(stack =!= {},\n" + "      {el, stack} = stack;\n"
            + "      Sow(el);\n" + "      If(Not(AtomQ(el)),\n"
            + "       Do(stack = {el[[j]], stack}, {j, Length(el), 1, -1}));\n" + "      );\n"
            + "    )[[2, 1]]\n" + "  )", //
        "");
    check("depthFirstPreorder({{1, {2, 3}}, {4, 5}})", //
        "{{{1,{2,3}},{4,5}},{1,{2,3}},1,{2,3},2,3,{4,5},4,5}");

    check("Reap(Sow(1); Sow(2); Sow(3))", //
        "{3,{{1,2,3}}}");
    check("Reap(Sow(1, {x, x}); Sow(1); Sow(2); Sow(3, x) )", //
        "{3,{{1,1,3},{1,2}}}");
    check("Reap(Sow(1, {x, x}); Sow(2, y); Sow(3, x) )", //
        "{3,{{1,1,3},{2}}}");
    check("Reap(Sow(1, {x, x}); Sow(2, y); Sow(3, x), _ )", //
        "{3,{{1,1,3},{2}}}");
    check("Reap(Sow(1, {x, x}); Sow(2, y); Sow(3, x), _, f )", //
        "{3,{f(x,{1,1,3}),f(y,{2})}}");
    check("Reap(Sow(1, {x, x}); Sow(2, y); Sow(3, x), y, f )", //
        "{3,{f(y,{2})}}");
    check("Reap(Sow(1, {x, x}); Sow(2); Sow(3, x), _, f )", //
        "{3,{f(x,{1,1,3}),f(None,{2})}}");
    check("Reap(Sow(1, {x, x}); Sow(2,y); Sow(3, x), _, Rule )", //
        "{3,{x->{1,1,3},y->{2}}}");
    check("Reap(x)", //
        "{x,{}}");
    check("Reap(Sow(a); b; Sow(c); Sow(d); e)", //
        "{e,{{a,c,d}}}");
    check("Reap(Sum(Sow(i0^2) + 1, {i0, 10}))", //
        "{395,{{1,4,9,16,25,36,49,64,81,100}}}");
  }

  @Test
  public void testReleaseHold() {
    check("f(ReleaseHold(HoldForm()))", //
        "f()");
    check("f(ReleaseHold(HoldComplete(1 + 1, Evaluate(1 + 2), Sequence(3, 4))))", //
        "f(2,3,3,4)");
    check("ReleaseHold(f(HoldForm()))", //
        "f()");
    check("ReleaseHold(f(HoldComplete(1 + 1, Evaluate(1 + 2), Sequence(3, 4))))", //
        "f(2,3,3,4)");
    check("ReleaseHold(HoldComplete(1 + 1, Evaluate(1 + 2), Sequence(3, 4)))", //
        "Identity(2,3,3,4)");
    check("x = 3;", //
        "");
    check("Hold(x)", //
        "Hold(x)");
    check("ReleaseHold(Hold(x))", //
        "3");
    check("ReleaseHold(y)", //
        "y");
    check("ReleaseHold(Hold(x)+y)", //
        "3+y");

    check("ReleaseHold /@ {Hold(1 + 2), HoldForm(2 + 3), HoldComplete(3 + 4), HoldPattern(_*_)}", //
        "{3,5,7,_^2}");
    check("ReleaseHold(f(Hold(1 + g(Hold(2 + 3)))))", //
        "f(1+g(Hold(2+3)))");
    check("ReleaseHold(f(1 + g(Hold(2 + 3))))", //
        "f(1+g(5))");
  }

  @Test
  public void testRepeated() {

    check("f(x: {{_, _} ..}) := Norm(N(x))", //
        "");
    check("f({{1,1}, {1,2}, {1,3}})", //
        "4.07914");
    check("f({{1,1,1}, {1,2}, {1,3}})", //
        "f({{1,1,1},{1,2},{1,3}})");

    check("{{}, {a}, {a, a}, {a, a, a}} /. {Repeated(a, 2)} -> x", //
        "{{},x,x,{a,a,a}}");
    check("{{}, {a}, {a, a}, {a, a, a}} /. {Repeated(a, {1})} -> x", //
        "{{},x,{a,a},{a,a,a}}");
    check("{{}, {a}, {a, a}, {a, a, a}} /. {Repeated(a, {0, 2})} -> x", //
        "{x,x,x,{a,a,a}}");
    check("{{}, {a}, {a, a}, {a, a, a}} /. {Repeated(a, {1, 2})} -> x", //
        "{{},x,x,{a,a,a}}");
    check("Repeated(a)", //
        "a..");
    check("Repeated(a)  // FullForm", //
        "Repeated(a)");

    check("{{1, 1}, {1}, {2, 1}} /. {(1) ..} -> x", //
        "{x,x,{2,1}}");

    check("MatchQ({4, 5, 6}, {Repeated(x_Integer)})", //
        "False");
    check("MatchQ({4, 4, 4}, {Repeated(x_Integer)})", //
        "True");
    check("MatchQ({4, 5, 6}, {Repeated(_Integer)})", //
        "True");
  }

  @Test
  public void testRepeatedNull() {
    check("{{}, {a}, {a, a}, {a, a, a}} /. {RepeatedNull(a, {0, 2})} -> x", //
        "{x,x,x,{a,a,a}}");
    check("RepeatedNull(a)", //
        "a...");
    check("RepeatedNull(a)  // FullForm", //
        "RepeatedNull(a)");
  }

  @Test
  public void testRefine() {
    // TODO
    // check("Refine((E)^(Pi*I*2*(1/4+x)), Element(x, Integers))", //
    // "I");
    // check("Refine(Log(x)<Exp(x), x>0)", //
    // "True");
    // check("Refine((-1)^(x+y), Element(k/2, Integers))", //
    // "(-1)^y");

    check("Refine(1-w<0,w>1)", //
        "True");
    check("Refine(Sqrt(x^2 y^2), x>0&&y<-10)", //
        "-x*y");
    check("Refine(Csc(Pi*(1/2+m)), Element(m, Integers))", //
        "I^(2*m)");
    check("Refine(Csc(Pi*(-1/2+m)), Element(m, Integers))", //
        "I^(2*(-1+m))");
    check("Refine(Csc(Pi*(1/4+m)), Element(m, Integers))", //
        "Csc((1/4+m)*Pi)");
    check("Refine(Csc(Pi*(-1/4+m)), Element(m, Integers))", //
        "Csc((-1/4+m)*Pi)");
    check("Refine(Csc(x+k*Pi), Element(k, Integers))", //
        "(-1)^k*Csc(x)");

    check("Refine(Sin(Pi*(1/2+m)), Element(m, Integers))", //
        "I^(2*m)");
    check("Refine(Sin(Pi*(-1/2+m)), Element(m, Integers))", //
        "I^(2*(-1+m))");
    check("Refine(Sin(Pi*(1/4+m)), Element(m, Integers))", //
        "Sin((1/4+m)*Pi)");
    check("Refine(Sin(Pi*(-1/4+m)), Element(m, Integers))", //
        "Sin((-1/4+m)*Pi)");
    check("Refine(Sin(x+k*Pi), Element(k, Integers))", //
        "(-1)^k*Sin(x)");

    check("Refine(Sin(k*Pi), Element(k, Integers))", //
        "0");
    check("Sin(k*Pi)", //
        "Sin(k*Pi)");
    check("Refine(Cos(x+k*Pi), Element(k, Integers))", //
        "(-1)^k*Cos(x)");

    check("Refine(Re(Log(x)),x>0)", //
        "Log(x)");
    check("Refine(Im(Log(x)),x>0)", //
        "0");
    check("Refine(Re(Log(x)),x<0)", //
        "Log(-x)");
    check("Refine(Im(Log(x)),x<0)", //
        "Pi");

    check("Refine(Sqrt(x^2),Assumptions -> Re(x)>0)", //
        "x");
    check("Refine(Sqrt(x^2),Assumptions -> Re(x)<0)", //
        "-x");
    check("Refine((x^4)^(1/4),Assumptions -> Re(x)>0)", //
        "(x^4)^(1/4)");
    check("Refine((x^3)^(1/3),Assumptions -> Re(x)>0)", //
        "(x^3)^(1/3)");

    check("Refine(Abs(a^b),Element(b,Reals))", //
        "Abs(a)^b");
    check("Refine(Abs(a^b), b<0)", //
        "Abs(a)^b");

    check("Refine(Re(2*I*Pi*C(1) + Log(2)) ,Element(C(1) ,Reals))", //
        "Log(2)");
    check("Refine(7+2*m+3*n>0)", //
        "2*m+3*n>-7");
    check("Refine(Sqrt(x^2), x>0)", //
        "x");
    check("Refine(2*Im(x)+3, x>3)", //
        "3");
    check("Refine(x<x, x>0)", //
        "False");
    check("Refine(x>=x, x>0)", //
        "True");
    check("Refine((-1)^(43*k), Element(k, Integers))", //
        "(-1)^k");
    check("Refine((-1)^(42*k), Element(k, Integers))", //
        "1");
    check("Refine((-1)^(2+k), Element(k, Integers))", //
        "(-1)^k");
    check("Refine((-1)^(43+k), Element(k, Integers))", //
        "(-1)^(1+k)");
    check("Refine((-1)^(-43+k), Element(k, Integers))", //
        "(-1)^(1+k)");
    check("Refine((-1)^(4*k), Element(k, Integers))", //
        "1");

    check("Refine(Log(-3/4), x < 0)", //
        "I*Pi-Log(4/3)");
    check("Refine(Log(x), x < 0)", //
        "I*Pi+Log(-x)");

    check("Refine(EvenQ(4*k), Element(k, Integers))", //
        "True");
    // for EvenQ result is undetermined
    check("Refine(EvenQ(3*k), Element(k, Integers))", //
        "False");

    // for OddQ we cann not determine if true/false
    check("Refine(OddQ(4*k), Element(k, Integers))", //
        "False");
    check("Refine(OddQ(3*k), Element(k, Integers))", //
        "False");

    check("Refine(a>0)", //
        "a>0");
    check("Refine(MoebiusMu(p),Element(p, Primes))", //
        "-1");

    // TODO
    // check("Refine((a^b)^c, -1<b&&b<(-1))", "a^(b*c)");
    check("Refine(Log(x)>0, x>1)", //
        "True");
    check("Refine(Log(x)<0, x<1&&x>0)", //
        "True");
    check("Refine(Log(x)<0, x<1&&x>0)", //
        "True");
    check("Refine(Log(x)<0, x<1&&x>=0)", //
        "Log(x)<0");

    check("Refine(x^4<0,x<0)", //
        "False");
    check("Refine(x^(1/2)>=0, x>=0)", //
        "Sqrt(x)>=0");
    check("Refine(x^4>=0,Element(x, Reals))", //
        "True");
    check("Refine(x^4>0,Element(x, Reals))", //
        "x^4>0");
    check("Refine(x^3>=0,Element(x, Reals))", //
        "x^3>=0");
    check("Refine(x^4<0,Element(x, Reals))", //
        "x^4<0");
    check("Refine(x^4<0,x<0)", //
        "False");
    check("Refine(-x^4<=0,Element(x, Reals))", //
        "True");
    check("Refine(-x^4<0,Element(x, Reals))", //
        "x^4>0");
    check("Refine(E^x>0,Element(x, Reals))", //
        "True");

    check("Refine(DiscreteDelta(x),x>0)", //
        "0");
    check("Refine(DiscreteDelta(x),x<-1)", //
        "0");
    check("Refine(DiracDelta(x),x>0)", //
        "0");
    check("Refine(DiracDelta(x),x<-1)", //
        "0");
    check("Refine(UnitStep(-x),x>0)", //
        "0");
    check("Refine(UnitStep(x),x>0)", //
        "1");
    check("Refine(UnitStep(y,x), x>0&&y>0)", //
        "1");

    check("Refine(Re(a+I*b), Element(a, Reals)&&Element(b, Reals))", //
        "a");

    check("(x^3)^(1/3)", //
        "(x^3)^(1/3)");
    check("Refine((x^3)^(1/3), x>=0)", //
        "x");

    check("Refine(Sqrt(x^2), Element(x, Reals))", //
        "Abs(x)");
    check("Refine(Sqrt(x^2), Assumptions -> Element(x, Reals))", //
        "Abs(x)");
    check("Refine(Sqrt(x^2), Element(x, Integers))", //
        "Abs(x)");
    check("Refine(Sqrt(x^2), x>=0)", //
        "x");
    check("Refine(Power((-x)^(1/2), 2), Element(x, Reals))", //
        "-x");

    check("Refine((x^3)^(1/3), x >= 0)", //
        "x");

    check("Refine(Abs(x), x>0)", //
        "x");
    check("Refine(Abs(x), Assumptions -> x>0)", //
        "x");
    check("Refine(Abs(x), x>1)", //
        "x");
    check("Refine(Abs(x)>=0, Element(x, Reals))", //
        "True");

    check("Refine(x>0, x>0)", //
        "True");
    check("Refine(x>=0, x>0)", //
        "True");
    check("Refine(x<0, x>0)", //
        "False");

    check("Refine(x>-1, x>0)", //
        "True");
    check("Refine(x>=-1, x>0)", //
        "True");
    check("Refine(x<-1, x>0)", //
        "False");

    check("Refine(x<0, x<0)", //
        "True");
    check("Refine(x<=0, x<0)", //
        "True");
    check("Refine(x>0, x<0)", //
        "False");

    check("Refine(x<-1, x<0)", //
        "x<-1");
    check("Refine(x<=-1, x<0)", //
        "x<=-1");
    check("Refine(x>-1, x<0)", //
        "x>-1");
    check("Refine(x>-1, x>0)", //
        "True");
    check("Refine(x>-1, x>=0)", //
        "True");

    check("Refine(Log(-4), x<0)", //
        "I*Pi+Log(4)");

    check("Refine(Floor(2*a + 1), Element(a, Integers))", //
        "1+2*a");
    check("Floor(2*a + 1)", //
        "1+Floor(2*a)");

    check("Refine(Element(x, Integers), Element(x, integers))", //
        "True");
    check("Refine(Floor(x),Element(x,Integers))", //
        "x");

    check("Refine(Arg(x), Assumptions -> x>0)", //
        "0");
    check("Refine(Arg(x), Assumptions -> x<0)", //
        "Pi");

    check("Refine(x==0)", //
        "x==0");
  }

  @Test
  public void testRest() {
    check("Rest(<|1 :> a, 2 -> b, 3 :> c|>)", //
        "<|2->b,3:>c|>");
    check("Rest(f(x))", //
        "f()");
    check("Rest(E^(b*x))", //
        "b*x");
    check("Rest(a + b + c + d)", //
        "b+c+d");
    check("Rest(f(a, b, c, d))", //
        "f(b,c,d)");
    check("NestList(Rest, {a, b, c, d, e}, 3)", //
        "{{a,b,c,d,e},{b,c,d,e},{c,d,e},{d,e}}");
    check("Rest(1/b)", //
        "-1");

    check("Rest({a, b, c})", //
        "{b,c}");
    check("Rest(a + b + c)", //
        "b+c");
    check("Rest(a)", //
        "Rest(a)");
  }

  /**
   * If this test fails try a change in <code>AbstractAST#isZERO()</code>.
   *
   * <pre>
   * public boolean AbstractAST#isZERO() {
   *     return PredicateQ.possibleZeroQ(this, EvalEngine.get());
   * }
   * </pre>
   */
  @Test
  public void testResultant() {
    // https: // codegolf.stackexchange.com/questions/261236/resultant-of-two-polynomials
    check("Resultant(1,2,x)", //
        "1");
    check("Resultant(3*x+3,x^2+2*x+1,x)", //
        "0");
    check("Resultant(x^3+3*x^2+3*x+1,x^2-1,x)", //
        "0");
    check("Resultant(x^3+2*x^2+3*x+4,5*x^2+6*x+7,x)", //
        "832");
    check("Resultant(x^3+2*x^2+3*x+4,4*x^3+3*x^2+2*x+1,x)", //
        "-2000");
    check("Resultant(x^4-4*x^3+5*x^2-2*x,x^4-4*x^3+5*x^2-2*x+1,x)", //
        "1");
    check("Resultant(x^4-4*x^3+5*x^2-2*x,x^7-12*x^6+60*x^5-160*x^4+240*x^3-192*x^2+64*x,x)", //
        "0");

    // https://math.stackexchange.com/a/542228
    check("Resultant(x^5+a*x^4+b*x^3+c*x^2+d*x+e, y-(x^2+m*x+n), x)", //
        "-e^2+d*e*m-c*e*m^2+b*e*m^3-a*e*m^4+e*m^5-d^2*n+2*c*e*n+c*d*m*n-3*b*e*m*n-b*d*m^2*n+\n" //
            + "4*a*e*m^2*n+a*d*m^3*n-5*e*m^3*n-d*m^4*n-c^2*n^2+2*b*d*n^2-2*a*e*n^2+b*c*m*n^2-3*a*d*m*n^\n" //
            + "2+5*e*m*n^2-a*c*m^2*n^2+4*d*m^2*n^2+c*m^3*n^2-b^2*n^3+2*a*c*n^3-2*d*n^3+a*b*m*n^\n" //
            + "3-3*c*m*n^3-b*m^2*n^3-a^2*n^4+2*b*n^4+a*m*n^4-n^5+d^2*y-2*c*e*y-c*d*m*y+3*b*e*m*y+b*d*m^\n" //
            + "2*y-4*a*e*m^2*y-a*d*m^3*y+5*e*m^3*y+d*m^4*y+2*c^2*n*y-4*b*d*n*y+4*a*e*n*y-2*b*c*m*n*y+\n" //
            + "6*a*d*m*n*y-10*e*m*n*y+2*a*c*m^2*n*y-8*d*m^2*n*y-2*c*m^3*n*y+3*b^2*n^2*y-6*a*c*n^\n" //
            + "2*y+6*d*n^2*y-3*a*b*m*n^2*y+9*c*m*n^2*y+3*b*m^2*n^2*y+4*a^2*n^3*y-8*b*n^3*y-4*a*m*n^\n" //
            + "3*y+5*n^4*y-c^2*y^2+2*b*d*y^2-2*a*e*y^2+b*c*m*y^2-3*a*d*m*y^2+5*e*m*y^2-a*c*m^2*y^\n" //
            + "2+4*d*m^2*y^2+c*m^3*y^2-3*b^2*n*y^2+6*a*c*n*y^2-6*d*n*y^2+3*a*b*m*n*y^2-9*c*m*n*y^\n" //
            + "2-3*b*m^2*n*y^2-6*a^2*n^2*y^2+12*b*n^2*y^2+6*a*m*n^2*y^2-10*n^3*y^2+b^2*y^3-2*a*c*y^\n" //
            + "3+2*d*y^3-a*b*m*y^3+3*c*m*y^3+b*m^2*y^3+4*a^2*n*y^3-8*b*n*y^3-4*a*m*n*y^3+10*n^2*y^\n" //
            + "3-a^2*y^4+2*b*y^4+a*m*y^4-5*n*y^4+y^5");

    check("Resultant(3/4,13,x)", //
        "1");
    check("Resultant(3/4,13,Indeterminate)", //
        "Resultant(3/4,13,Indeterminate)");
    check("Resultant(0, x^3+2*x, x)", //
        "0");
    check("Resultant(f(x), 0, x)", //
        "0");
    check("Resultant(1,a+x^2+c,x)", //
        "1");
    check("Resultant(a+x^2+c, 1, x)", //
        "1");
    // check("Resultant((x - a) (x - b), (x - c) (x - d) (x - e), x)",//
    //
    // "(a*b+(-(-a^2*b-a*b^2+a*b*c+a*b*d+a*b*e-c*d*e)*(-a-b-(-a^2*b-a*b^2+a*b*c+a*b*d+a*b*e-c*d*e)/(a^\n"
    // +
    //
    // "2+a*b+b^2-a*c-b*c-a*d-b*d+c*d-a*e-b*e+c*e+d*e)))/(a^2+a*b+b^2-a*c-b*c-a*d-b*d+c*d-a*e-b*e+c*e+d*e))*(a^\n"
    // +
    // "2+a*b+b^2-a*c-b*c-a*d-b*d+c*d-a*e-b*e+c*e+d*e)^2");
    check("PolynomialRemainder(-2+x^2-2*x*y+y^2,-5+4*x-2*x^3+2*y+3*x^2*y,y)",
        "-2+x^2+(5-4*x+2*x^3)*((5-4*x+2*x^3)/(2+3*x^2)^2+(-2*x)/(2+3*x^2))");
    check("Resultant((x-y)^2-2 , y^3-5, y)", //
        "17-60*x+12*x^2-10*x^3-6*x^4+x^6");
    check("Resultant(x^2 - 2*x + 7, x^3 - x + 5, x)", //
        "265");
    check("Resultant(x^2 + 2*x , x-c, x)", //
        "2*c+c^2");

    check("Resultant(x^2 - 4, x^2 + 4*x + 4, x)", //
        "0");
    // MMA -3807 - Sympy 3807
    check("Resultant(3*x + 9, 6*x^3 - 3*x + 12, x)", //
        "3807");

    // check("Resultant[a x^3 + b x^2 + c x + f, f x^3 + c x^2 + b x + a,
    // x]", "");
  }

  @Test
  public void testReturn() {
    check("retother:=(Return();hello);retother", //
        "");
    check("f(x_) := (If(x < 0, Return(0)); x)", //
        "");
    check("f(-1)", //
        "0");

    check("Do(If(i > 3, Return()); Print(i), {i, 10})", //
        "");

    check("g(x_) := (Do(If(x < 0, Return(0)), {i, {2, 1, 0, -1}}); x)", //
        "");
    check("g(-1)", //
        "-1");

    check("h(x_) := (If(x < 0, Return()); x)", //
        "");
    check("h(1)", //
        "1");
    check("h(-1) // FullForm", "Null");

    check("f(x_) := Return(x)", //
        "");
    check("g(y_) := Module({}, z = f(y); 2)", //
        "");
    check("g(1)", //
        "2");

    check("$a(x_):=Return(1); $b(x_):=Module({},$c=$a(y);2); $b(1)", //
        "2");
    check("$f(x_) := (If(x > 5, Return(a)); x + 3); $f(6)", //
        "a");
    check("$g(x_) := (Do( If(x > 5, Return(a)), {3}); x); $g(6)", //
        "6");
    check("$h(x_) := Catch(Do(If(x > 5, Throw(a)), {3}); x); $h(6)", //
        "a");
  }

  @Test
  public void testReverse() {
    // Reverse: Nonatomic expression expected at position 1 in Reverse(f).
    check("Reverse(f)", //
        "Reverse(f)");

    check("Reverse(f())", //
        "f()");
    check("Reverse(f(a))", //
        "f(a)");
    check("Reverse(r(1,2,3,4))", //
        "r(4,3,2,1)");
    check("Reverse(<|U->1,V->2|>)", //
        "<|V->2,U->1|>");
    check("Reverse({1, 2, 3})", //
        "{3,2,1}");
    check("Reverse(x(a,b,c))", //
        "x(c,b,a)");
    // check("Reverse({{1, 2}, {3, 4}}, 1)", "");
  }

  @Test
  public void testRGBColor() {
    check("Yellow", //
        "RGBColor(1.0,1.0,0.0)");
    check("Purple", //
        "RGBColor(0.5,0.0,0.5)");
  }

  @Test
  public void testRightComposition() {
    check("Hold[f @ g@@h] // FullForm", //
        "Hold(Apply(f(g), h))");

    check("RightComposition(f, g, h)[x, y]", //
        "h(g(f(x,y)))");
    check("f /* g /* h@x", //
        "h(g(f(x)))");
    check("(f /* g /* h)@x", //
        "h(g(f(x)))");
  }

  @Test
  public void testRiffle() {
    check("Riffle({1, 2, 3, 4, 5, 6, 7, 8, 9}, x)", //
        "{1,x,2,x,3,x,4,x,5,x,6,x,7,x,8,x,9}");
    check("Riffle({1, 2, 3, 4, 5, 6, 7, 8, 9}, {x, y})", //
        "{1,x,2,y,3,x,4,y,5,x,6,y,7,x,8,y,9}");
    check("Riffle({1}, x)", //
        "{1}");
    check("Riffle({a, b, c, d}, {x, y, z, w})", //
        "{a,x,b,y,c,z,d,w}");
  }

  @Test
  public void testRogersTanimotoDissimilarity() {
    check("RogersTanimotoDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
        "3/4");
    check("RogersTanimotoDissimilarity({True, False, True}, {True, True, False})", //
        "4/5");
    check("RogersTanimotoDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
        "0");
    check("RogersTanimotoDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
        "1");
  }

  @Test
  public void testRomanNumeral() {
    // zero as special case represented by 'N'
    check("RomanNumeral(0)", //
        "N");
    check("RomanNumeral({4000,3267,3603,1929,2575,746,666,1457,3828})", //
        "{MMMM,MMMCCLXVII,MMMDCIII,MCMXXIX,MMDLXXV,DCCXLVI,DCLXVI,MCDLVII,MMMDCCCXXVIII}");
    check("RomanNumeral({1, 2, 3, 4, 5, 10, 50, 60, 100, 250, 500, 1000, 1500, 2600})", //
        "{I,II,III,IV,V,X,L,LX,C,CCL,D,M,MD,MMDC}");
  }

  @Test
  public void testRoot() {
    check("Root(EvenQ(#1)&,1009)", //
        "Root(EvenQ(#1)&,1009)");
    check("Root((#^2 - 3*# - 1)&, 2)", //
        "3/2+Sqrt(13)/2");
    check("Root((-3*#-1)&, 1)", //
        "-1/3");
  }

  @Test
  public void testRoots() {
    check("(-EulerGamma)^(1/3)", //
        "(-EulerGamma)^(1/3)");
    check("Roots(x^3==EulerGamma,x)", //
        "x==-(-EulerGamma)^(1/3)||x==EulerGamma^(1/3)||x==(-1)^(2/3)*EulerGamma^(1/3)");
    check("Roots(a*x^2+b*x+c==0,2)", //
        "Roots(c+b*x+a*x^2==0,2)");

    // check("Roots(a*x^3+b*x^2+c^2+d, x)",
    // "{(-b/2-Sqrt(b^2-4*a*c)/2)/a,(-b/2+Sqrt(b^2-4*a*c)/2)/a}");
    check("Roots(x^2-2*x-3==0,x)", //
        "x==-1||x==3");
    check("Roots(a*x^2+b*x+c==0, x)", //
        "x==(-b-Sqrt(b^2-4*a*c))/(2*a)||x==(-b+Sqrt(b^2-4*a*c))/(2*a)");
    check("Roots(3*x^3-8*x^2+-11*x+10==0,x)", //
        "x==2/3||x==1-Sqrt(6)||x==1+Sqrt(6)");
    check("Roots(3*x^3-5*x^2+5*x-2==0,x)", //
        "x==2/3||x==1/2*(1-I*Sqrt(3))||x==1/2*(1+I*Sqrt(3))");
    check("Roots(x^3 - 5*x + 4==0,x)", //
        "x==1||x==1/2*(-1-Sqrt(17))||x==1/2*(-1+Sqrt(17))");
  }

  @Test
  public void testRotateLeft() {
    // RotateLeft: Nonatomic expression expected at position 1 in RotateLeft(f).
    check("RotateLeft(f)", //
        "RotateLeft(f)");

    check("RotateLeft(r(1,2,3,4))", //
        "r(2,3,4,1)");
    check("RotateLeft(r(1,2,3,4),2)", //
        "r(3,4,1,2)");
    check("RotateLeft({})", //
        "{}");
    check("RotateLeft({a,b,c}, 5)", //
        "{c,a,b}");
    check("RotateLeft({1, 2, 3})", //
        "{2,3,1}");
    check("RotateLeft(Range(10),3)", //
        "{4,5,6,7,8,9,10,1,2,3}");
    check("RotateLeft(x(a,b,c),2)", //
        "x(c,a,b)");

    check("RotateLeft({1,2,3,4,5},2)", //
        "{3,4,5,1,2}");
  }

  @Test
  public void testRotateRight() {
    // RotateRight: Nonatomic expression expected at position 1 in RotateRight(f).
    check("RotateRight(f)", //
        "RotateRight(f)");

    check("RotateRight(r(1,2,3,4))", //
        "r(4,1,2,3)");
    check("RotateRight(r(1,2,3,4),3)", //
        "r(2,3,4,1)");
    check("RotateRight({})", //
        "{}");
    check("RotateRight({a,b,c}, 5)", //
        "{b,c,a}");
    check("RotateRight({1, 2, 3})", //
        "{3,1,2}");
    check("RotateRight(Range(10),3)", //
        "{8,9,10,1,2,3,4,5,6,7}");
    check("RotateRight(x(a,b,c),2)", //
        "x(b,c,a)");

    check("RotateRight({1,2,3,4,5},2)", //
        "{4,5,1,2,3}");
  }

  @Test
  public void testRound() {
    check("Round(Quantity(8.5, \"Meters\"))", //
        "8[Meters]");
    check("Round(-1.235512, 0)", //
        "Indeterminate");

    // message Round: Internal precision limit reached while evaluating
    // 1/E^(9223372036854775808/11).
    check("Round(3/2,1/E^(9223372036854775808/11))", //
        "Round(3/2,1/E^(9223372036854775808/11))");

    check("Round(10+x)", //
        "Round(10+x)");
    check("Round(-10+x)", //
        "-Round(10-x)");
    check("Round(10+x,Pi)", //
        "Round(10+x,Pi)");
    check("Round(Infinity, x)", //
        "Round(Infinity,x)");
    check("Round(Infinity, Pi)", //
        "Infinity");
    check("Round(-Infinity, Pi)", //
        "-Infinity");
    check("Round(1+2*I, 2*I)", //
        "I*2");
    check("Round(10, Pi)", //
        "3*Pi");
    check("Round(-10, Pi)", //
        "-3*Pi");
    check("Round(12, Pi)", //
        "4*Pi");
    check("Round(-12, Pi)", //
        "-4*Pi");
    check("Round(5.37 - 1.3*I)", //
        "5-I");
    check("Round(DirectedInfinity(0))", //
        "ComplexInfinity");
    check("Round(DirectedInfinity((1/2-I*1/2)*Sqrt(2)))", //
        "DirectedInfinity((1/2-I*1/2)*Sqrt(2))");

    // github #145
    // TODO add tests for big (Apfloat) numbers
    // Rationalize(2.1675 => 867/400
    check("Round(Rationalize(867/400),10^(-3))", //
        "271/125");
    check("Round(Rationalize(2.1675),10^(-3))", //
        "271/125");
    check("Round(2.1675, 0.001)", //
        "2.168");
    check("Round(2.1675, 1/1000)", //
        "271/125");
    check("Round(500,10^(-3))", //
        "500");
    check("Round(500, 10)", //
        "500");

    check("Round(75.345677/7.56)", //
        "10");
    check("Round(1.234512, 0.01)", //
        "1.23");
    check("Round(-1.234512, 0.01)", //
        "-1.23");
    check("Round(1.235512, 0.01)", //
        "1.24");
    check("Round(-1.235512, 0.01)", //
        "-1.24");
    check("Round(1.234512, -0.01)", //
        "1.23");
    check("Round(-1.234512, -0.01)", //
        "-1.23");
    check("Round(1.235512, -0.01)", //
        "1.24");
    check("Round(-1.235512, -0.01)", //
        "-1.24");
    check("Round(1.234512, 1/100)", //
        "123/100");
    check("Round(-1.235512, 1/100)", //
        "-31/25");

    check("Round(-1.235512, -100)", //
        "0");
    check("Round(-1.235512, 0)", //
        "Indeterminate");
    check("Refine(2/3*Round(x), Element(x,Integers))", //
        "2/3*x");

    check("Round(226, 10)", //
        "230");
    check("Round(226, -10)", //
        "230");
    check("Round({12.5, 62.1, 68.3, 74.5, 80.7}, 5)", //
        "{10,60,70,75,80}");
    check("Round({5, 15, 25, 35, 45}, 10)", //
        "{0,20,20,40,40}");
    check("Round(75.345677/7.56)", //
        "10");
    check("Table(Round(n), {n, {-5/3, 10/3, 13/2}})", //
        "{-2,3,6}");
    check("Table(Round((GoldenRatio^k)/Sqrt(5)), {k, 15}) ==" + //
        "Table(Fibonacci(k), {k, 15})", //
        "True");
    check("Round(Infinity)", //
        "Infinity");
    check("Round(-Infinity)", //
        "-Infinity");
    check("Round(Pi-E)", //
        "0");
    check("Round(5.37-I)", //
        "5-I");
    check("Round(4/(1+I))", //
        "2-I*2");
    check("Round(3.4)", //
        "3");
    check("Round(3.5)", //
        "4");
    check("Round(3.6)", //
        "4");
    check("Round(-3.4)", //
        "-3");
    check("Round(-3.5)", //
        "-4");
    check("Round(-3.6)", //
        "-4");

    check("Round(N[0.03306158858189456, 30] * 100, 10^-2) // N", //
        "3.31");

  }

  @Test
  public void testRussellRaoDissimilarity() {
    check("RussellRaoDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
        "3/5");
    check("RussellRaoDissimilarity({True, False, True}, {True, True, False})", //
        "2/3");
    check("RussellRaoDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
        "0");
    check("RussellRaoDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
        "1");
  }

  @Test
  public void testRule() {
    check("a+b+c /. c->d", //
        "a+b+d");
    check("{x,x^2,y} /. x->3", //
        "{3,9,y}");
    // Rule called with 3 arguments; 2 arguments are expected.
    check("a /. Rule(1, 2, 3) -> t ", //
        "a");
  }

  @Test
  public void testSameQUnsameQ() {
    // mathics #146
    // check("a=N[2/9,3]; b=0.22222`5; {a,b,a==b, a===b}", //
    // "{0.222,0.22222,False,False}");
    check("SameQ( )", //
        "True");
    check("SameQ(abc)", //
        "True");
    check("UnsameQ( )", //
        "True");
    check("UnsameQ(abc)", //
        "True");
    check("a===a", //
        "True");
    check("SameQ(0.0, 0)", //
        "False");
    check("UnsameQ(0.0, 0)", //
        "True");
    check("$g(f(x))===v", //
        "False");
    check("$g(f(x))===$g(f(x))", //
        "True");
    check("$g(f(x))=!=v", //
        "True");
    check("$g(f(x))=!=$g(f(x))", //
        "False");
    check("Boole(Array(UnsameQ, {3, 3, 3}))", //
        "{{{0,0,0},{0,0,1},{0,1,0}},{{0,0,1},{0,0,0},{1,0,0}},{{0,1,0},{1,0,0},{0,0,0}}}");
  }


  @Test
  public void testSawtoothWave() {
    check("SawtoothWave(-1.444444)", //
        "0.555556");

    check("SawtoothWave({0, 5},{0.4, 1.2, 3.6})", //
        "{2.0,1.0,3.0}");
    check("SawtoothWave(0.333)", //
        "0.333");
    check("SawtoothWave(17.333)", //
        "0.333");
    check("SawtoothWave(41/42)", //
        "41/42");
    check("SawtoothWave(42/41)", //
        "1/41");
    check("N(SawtoothWave(-1/47), 50)", //
        "0.97872340425531914893617021276595744680851063829787");

    check("SawtoothWave({0.4, 1.2, 3.6})", //
        "{0.4,0.2,0.6}");

  }

  @Test
  public void testScan() {
    // TODO e1 ~ e2 ~ e3 => e2[e1, e3]
    // check("(Print@#; #0 ~Scan~ #)& @ {{1, {2, 3}}, {4, 5}}", //
    // "");

    // prints
    // 1
    // 2
    // 3
    // {2,3}
    // {1,{2,3}}
    // 4
    // 5
    // {4,5}
    // {{1,{2,3}},{4,5}}

    check("Scan(Print,<|1 -> {{a}}, 2 -> b|>, 2)", //
        "");
    check("Scan(Print,<|1 -> a, 2 -> b, 3 -> c|>)", //
        "");
    check("expr = {{1, {2, 3}}, {4, 5}}; Scan(Print, expr, {0, -1})", //
        "");

    check("Scan(Print)[{a, b, c}]", //
        "");
    check("Scan(Print, {1, 2, 3}, Heads->True)", //
        "");
    check("Scan(($u(#) = x) &, {55, 11, 77, 88});{$u(76), $u(77), $u(78)}", //
        "{$u(76),x,$u(78)}");
    check("Map(If(# > 5, #, False) &, {2, 4, 6, 8})", //
        "{False,False,6,8}");
    check("Catch(Map(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", //
        "6");
    check("Catch(Scan(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", //
        "6");
    check("Reap(Scan(\n" + "   If(# > 0, Sow(#)) &, {1, {-2, Pi}, -Sqrt(3)},Infinity))[[2, 1]]", //
        "{1,Pi,3,1/2,Sqrt(3)}");
    check("Scan(Return, {1, 2})", //
        "1");
    check(
        "Reap(Scan(Sow, -(ArcTan((1 + 2*x)/Sqrt(3))/Sqrt(3)) + (1/3)*Log(1 - x) - (1/6)*Log(1 + x + x^2), {-1}))[[2, 1]]", //
        "{-1,3,-1/2,3,-1/2,1,2,x,1/3,1,-1,x,-1/6,1,x,x,2}");
  }

  @Test
  public void testSec() {
    // check("Sec(8/15*Pi)", //
    // "-Csc(Pi/30)");
    // check("Sec(4/15*Pi)", //
    // "-Csc(1/30*Pi)");
    check("-Sec(Pi/4-x)", //
        "-Sec(Pi/4-x)");
    check("-Sec(Pi/3-x)", //
        "-Sec(Pi/3-x)");
    check("Sec(5/7*Pi+x)", //
        "-Csc(3/14*Pi+x)");
    check("Sec(3/4*Pi+x)", //
        "-Csc(Pi/4+x)");
    check("Sec(-3/4*Pi+x)", //
        "-Sec(Pi/4+x)");

    check("Sec(e - Pi/2 + f*x)", //
        "Csc(e+f*x)");
    check("Sec(e+f*x)^m*Tan(e+f*x)^2", //
        "Sec(e+f*x)^(2+m)*Sin(e+f*x)^2");
    check("Sec(e+f*x)^m*Tan(e+f*x)", //
        "Sec(e+f*x)^(1+m)*Sin(e+f*x)");

    check("Sec(Pi/2+Pi*n)", //
        "-Csc(n*Pi)");
    check("Sec(0)", //
        "1");
    check("Sec(1)", //
        "Sec(1)");
    checkNumeric("Sec(1.)", //
        "1.8508157176809255");

    check("Sec(Pi/2)", //
        "ComplexInfinity");
    check("Sec(0)", //
        "1");
    check("Sec(2/5*Pi)", //
        "1+Sqrt(5)");
    check("Sec(23/12*Pi)", //
        "-Sqrt(2)+Sqrt(6)");
    check("Sec(z+1/2*Pi)", //
        "-Csc(z)");
    check("Sec(Pi)", //
        "-1");
    check("Sec(33*Pi)", //
        "-1");
    check("Sec(z+Pi)", //
        "-Sec(z)");
    check("Sec(z+42*Pi)", //
        "Sec(z)");
    check("Sec(x+y+z+43*Pi)", //
        "-Sec(x+y+z)");
    check("Sec(z+42*a*Pi)", //
        "Sec(42*a*Pi+z)");
    check("Sec(z+4/3*Pi)", //
        "-Sec(Pi/3+z)");
    check("Sec(Sqrt(x^2))", //
        "Sec(x)");
  }

  @Test
  public void testSech() {
    // gitbub #173
    check("Sech(Log(5/3))", //
        "15/17");

    check("Refine(Sech(x+I*k*Pi), Element(k, Integers))", //
        "(-1)^k*Sech(x)");
    check("Refine(Sech(x-I*k*Pi), Element(k, Integers))", //
        "(-1)^k*Sech(x)");
    check("Refine(Sech(x-42*I*k*Pi), Element(k, Integers))", //
        "Sech(x)");
    check("Refine(Sech(x-43*I*k*Pi), Element(k, Integers))", //
        "(-1)^k*Sech(x)");

    check("Sech(x)^m*Tanh(x)^2", //
        "Sech(x)^(2+m)*Sinh(x)^2");
    check("Sech(0)", //
        "1");
    checkNumeric("Sech(1.8)", //
        "0.3218048695065878");
    check("Sech(-x)", //
        "Sech(x)");
    check("D(Sech(x),x)", //
        "-Sech(x)*Tanh(x)");
  }

  @Test
  public void testSelect() {
    check("Select({},_,1000)", //
        "{}");
    check("Select({},_)", //
        "{}");
    check("Select({},#1>2&)", //
        "{}");
    // Select: Non-negative integer or Infinity expected at position 3 in
    // Select({1,2,4,7,6,2},#1>2&,-1).
    check("Select({1,2,4,7,6,2},#1>2&,-1)", //
        "Select({1,2,4,7,6,2},#1>2&,-1)");
    check("Select({1,2,4,7,6,2},#1>2&,0)", //
        "{}");
    check("Select({-3, 0}, #>10&)", //
        "{}");
    check("Select(<|a -> 1, b -> 2, c -> 3, d -> 4|>, # > 6 &)", //
        "<||>");
    check("Select(<|a -> 1, b -> 2, c -> 3, d -> 4|>, # > 2 &)", //
        "<|c->3,d->4|>");
    check("Select(Accumulate(Table({1,Prime(x)},{x,900,1000})), PrimeQ( #[[2]] )& )", //
        "{{1,6997},{3,21011},{7,49139},{11,77447},{87,644377},{93,691333}}");
    check("Select(# > 4 &) [{1, 2.2, 3, 4.5, 5, 6, 7.5, 8}]", //
        "{4.5,5,6,7.5,8}");
    check("Cases(_Integer)@Select(# > 4 &)@{1, 2.2, 3, 4.5, 5, 6, 7.5, 8}", //
        "{5,6,8}");
    check("Select({-3, 0, 1, 3, a}, #>0&)", //
        "{1,3}");
    check("Select(f(a, 2, 3), NumberQ)", //
        "f(2,3)");
    check("Select(a, True)", //
        "Select(a,True)");

    check("Select({1, 2, 4, 7, 6, 2}, EvenQ)", //
        "{2,4,6,2}");
    check("Select({1, 2, 4, 7, 6, 2}, # > 2 &)", //
        "{4,7,6}");
    check("Select({1, 2, 4, 7, 6, 2}, # > 2 &, 1)", //
        "{4}");
    check("Select({1, 2, 4, 7, x}, # > 2 &)", //
        "{4,7}");
    check("Select(f(1, a, 2, b, 3), IntegerQ)", //
        "f(1,2,3)");
    check("Select(Range(100), Mod(#, 3) == 1 && Mod(#, 5) == 1 &)", //
        "{1,16,31,46,61,76,91}");
    check("Select({-3, 0, 10, 3, a}, #>0&, 1)", //
        "{10}");
  }

  @Test
  public void testSelectFirst() {
    check("SelectFirst(<|1 -> \"a\", 2 -> \"b\", 3 -> c, 4 -> d|>,Head(#)==Symbol &)", //
        "c");
    check("SelectFirst({-3, 0, 1, 3, a}, #>0 &)", //
        "1");
    check("SelectFirst({-3, 0, 1, 3, a}, #>3 &)", //
        "Missing(NotFound)");
    check("SelectFirst({1, 2, 4, 7, 6, 2}, EvenQ)", //
        "2");
    check("SelectFirst({1, 2, 4, 7, 6, 2}, # > 2 &)", //
        "4");
    check("SelectFirst({1, 3, 5}, EvenQ, x)", //
        "x");
    check("SelectFirst({1, 3, 5, 6}, EvenQ, x)", //
        "6");
    check("SelectFirst(EvenQ)[{1, 2, 4, 7, 6, 2}]", //
        "2");
    check("SelectFirst({{1, y}, {2, z}, {3, x}, {4, y}, {5, x}}, MemberQ(#, x) &)", //
        "{3,x}");
    check("SelectFirst(f(1, a, 2, b, 3), IntegerQ)", //
        "1");
    check("SelectFirst({1, 2, 3}, StringQ, \"NoStrings\")", //
        "NoStrings");
  }

  @Test
  public void testSequence() {
    check("Sequence( )", //
        "Identity()");
    check("Sequence(a,b,c)", //
        "Identity(a,b,c)");
    check("{Sequence( ),a}", //
        "{a}");
    check("f(a, Sequence( ),b,c)", //
        "f(a,b,c)");
    check("{u, u, u} /. u -> Sequence(a, b, c)", //
        "{a,b,c,a,b,c,a,b,c}");
    check("f(a, Sequence(b, c), d)", //
        "f(a,b,c,d)");
    check("$u = Sequence(a, b, c)", //
        "Identity(a,b,c)");
    check("$u = Sequence(a, b, c);{$u,$u,$u}", //
        "{a,b,c,a,b,c,a,b,c}");
    check("f({{a, b}, {c, d}, {a}}) /. List -> Sequence", //
        "f(a,b,c,d,a)");
    // message Identity: Identity called with 3 arguments; 1 argument is expected.
    check("f(a, b, c) /. f(x__) -> x", //
        "Identity(a,b,c)");
    check("{a, Sequence(b), c, Identity(d)}", //
        "{a,b,c,d}");
    // print message
    check("Head(Sequence(a,b))", //
        "b(Symbol)");
    check("u->Sequence[a,b]", //
        "u->Sequence(a,b)");

    check("myOpts(func_, target_) := {o1 -> thick,  If(func === target, o2 -> 0, Sequence @@ {})}", //
        "");
    check("myOpts(x^2,x^2)", //
        "{o1->thick,o2->0}");
    check("myOpts(3*x,x^2)", //
        "{o1->thick}");

    check("f(x, Sequence(a, b), y)", //
        "f(x,a,b,y)");
    check("Attributes(Set)", //
        "{HoldFirst,Protected,SequenceHold}");
    check("a = Sequence(b, c);", //
        "");
    check("a", //
        "Identity(b,c)");
    check("lst = {1, 2, 3};", //
        "");
    check("f(Sequence @@ lst)", //
        "f(1,2,3)");
    check("Hold(a, Sequence(b, c), d)", //
        "Hold(a,b,c,d)");
    // If Sequence appears at a deeper level in Hold(), it is left unevaluated
    check("Hold({a, Sequence(b, c), d})", //
        "Hold({a,Sequence(b,c),d})");
  }

  @Test
  public void testSet() {
    check("aVar=10", //
        "10");
    // integer not allowed as header is (Protected)
    check("aVar(x_):={x}", //
        "$Failed");

    // check("A = {{1, 2}, {3, 4}}", "{{1,2},{3,4}}");
    // check("A[[;;, 2]] = {6, 7} ", "{6,7}");
    // check("A", "{{1,6},{3,7}}");
    //

    // check("B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}} ", "{{1,2,3},{4,5,6},{7,8,9}}");
    // check("B[[1;;2, 2;;-1]] = {{t, u}, {y, z}}", "{{t,u},{y,z}}");
    // check("B", "{{1,t,u},{4,y,z},{7,8,9}}");

    check("foo=barf", //
        "barf");
    check("foo(x)=1", //
        "1");
    check("barf(x)=1", //
        "1");

    check("a = 3", //
        "3");
    check("a", //
        "3");
    check("{a, b, c} = {10, 2, 3}   ", //
        "{10,2,3}");
    check("{a, b, {c, {d}}} = {1, 2, {{c1, c2}, {a}}} ", //
        "{1,2,{{c1,c2},{10}}}");
    check("d", //
        "10");
    check("a", //
        "1");
    check("x = a", //
        "1");
    check("a = 2", //
        "2");
    check("x", //
        "1");

    check("a = b = c = 2", //
        "2");
    check("a == b == c == 2", //
        "True");

    check("A = {{1, 2}, {3, 4}}", //
        "{{1,2},{3,4}}");
    check("A[[1, 2]] = 5", //
        "5");
    check("A", //
        "{{1,5},{3,4}}");
    check("A[[;;, 2]] = {6, 7} ", //
        "{6,7}");
    check("A", "{{1,6},{3,7}}");

    check("B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}} ", //
        "{{1,2,3},{4,5,6},{7,8,9}}");
    check("B[[1;;2, 2;;-1]] = {{t, u}, {y, z}}", //
        "{{t,u},{y,z}}");
    check("B", //
        "{{1,t,u},{4,y,z},{7,8,9}}");

    check("mat = {\n" //
        + "    {1, 2, 3},\n" //
        + "    {4, 5, 6},\n" //
        + "    {7, 8, 9}\n" //
        + "   };", //
        "");
    check("mat[[2]] = mat[[2]] + 10; MatrixForm(mat)", //
        "{{1,2,3},\n"//
            + " {14,15,16},\n"//
            + " {7,8,9}}");
  }

  @Test
  public void testSetAttributes() {
    check("SetAttributes({f, g}, {Flat, Orderless})", //
        "");
    check("Attributes(f)", //
        "{Flat,Orderless}");
    check("SetAttributes(h, Flat)", //
        "");
    check("Attributes(h)", //
        "{Flat}");
  }

  @Test
  public void testSetDelayed() {
    check("f(x_, nm : Association((_String -> _Integer) ..)) := {x,nm}", //
        "");
    check("f(a,<|\"c\"->3, \"d\"->4|>)", //
        "{a,<|c->3,d->4|>}");
    check("Attributes(SetDelayed)  ", //
        "{HoldAll,Protected,SequenceHold}");
    check("a = 1", //
        "1");
    check("x := a", //
        "");
    check("x", //
        "1");
    check("a = 2", //
        "2");
    check("x", //
        "2");

    check("f(x_) := p(x) /; x>0", //
        "");
    check("f(3)", //
        "p(3)");
    check("f(-3)", //
        "f(-3)");
  }

  @Test
  public void testSetDelayedOneIdentity() {
    check("SetAttributes(SUNIndex, {OneIdentity})", //
        "");
    check("SetAttributes(SUNFIndex, {OneIdentity})", //
        "");
    check(
        "SUNIndex(SUNFIndex(___)):= (Print(\"This error shouldn't be triggered here!\"); Abort())", //
        "");
    check(
        "SUNFIndex(SUNIndex(___)):= (Print(\"This error shouldn't be triggered here!\"); Abort())", //
        "");
  }

  @Test
  public void testShare() {
    check(
        "people = <|\n" + "236234 -> <|\"name\" -> \"bob\", \"age\" -> 18, \"sex\" -> \"M\"|>, \n"
            + "253456 -> <|\"name\" -> \"sue\", \"age\" -> 25, \"sex\" -> \"F\"|>, \n"
            + "323442 -> <|\"name\" -> \"ann\", \"age\" -> 18, \"sex\" -> \"F\"|>\n" + "|>", //
        "<|236234-><|name->bob,age->18,sex->M|>,253456-><|name->sue,age->25,sex->F|>,\n"
            + "323442-><|name->ann,age->18,sex->F|>|>");
    check("Share(people)", //
        "2");
    check("people", //
        "<|236234-><|name->bob,age->18,sex->M|>,253456-><|name->sue,age->25,sex->F|>,\n"
            + "323442-><|name->ann,age->18,sex->F|>|>");

    check("sh= Table(j*(x + i), {i, 5}, {j, i}) ", //
        "{{1+x},{2+x,2*(2+x)},{3+x,2*(3+x),3*(3+x)},{4+x,2*(4+x),3*(4+x),4*(4+x)},{5+x,2*(\n"
            + "5+x),3*(5+x),4*(5+x),5*(5+x)}}");
    check("Share(sh)", //
        "10");
    check("sh", //
        "{{1+x},{2+x,2*(2+x)},{3+x,2*(3+x),3*(3+x)},{4+x,2*(4+x),3*(4+x),4*(4+x)},{5+x,2*(\n"
            + "5+x),3*(5+x),4*(5+x),5*(5+x)}}");

    check("Share(Table(xi = x + i; Table(j*xi, {j, i}), {i, 5}))", //
        "0");
  }

  @Test
  public void testShort() {
    check("Short(Expand((1 + x + y)^12))", //
        "1+12*x+66*x^2+220*x^3+495*<<SHORT>>10+12*y^\n" + "11+12*x*y^11+y^12");
  }

  @Test
  public void testSignature() {
    check("Signature({1,2,3,4})", //
        "1");
    check("Signature({1,4,3,2})", //
        "-1");
    check("Signature({1,2,3,2})", //
        "0");

    check("Signature({a,b,c})", //
        "1");
    check("Signature({a,b,c,d})", //
        "1");
    check("Signature({a,c,b})", //
        "-1");
    check("Signature({a,c,b,d})", //
        "-1");
    check("Signature({a,b,b})", //
        "0");
    check("Select(Permutations({a,b,c,d}),Signature(#)==1&)", //
        "{{a,b,c,d},{a,c,d,b},{a,d,b,c},{b,a,d,c},{b,c,a,d},{b,d,c,a},{c,a,b,d},{c,b,d,a},{c,d,a,b},{d,a,c,b},{d,b,a,c},{d,c,b,a}}");
    check("Array(Signature({##})&,{3,3,3})", //
        "{{{0,0,0},{0,0,1},{0,-1,0}},{{0,0,-1},{0,0,0},{1,0,0}},{{0,1,0},{-1,0,0},{0,0,0}}}");
  }

  @Test
  public void testSign() {
    check(
        "Simplify((1/3+(1/3)*(-2)^(-1/3)*2^(-2/3)*(1+(0+1*I)*3^(1/2))+(1/6)*(-1)^(1/3)*(1+(0+-1*I)*3^(1/2)))^2)", //
        "1");

    check("Sign(Sign(z))", //
        "Sign(z)");
    check("Sign(Power(z, (-11)^(-1)))", //
        "1/Sign(z)^(1/11)");
    check("Sign(Power(z, (13)^(-1)))", //
        "Sign(z)^(1/13)");

    check("Sign(I^(2*Pi))", //
        "I^(2*Pi)");
    check("Sign(a*b^(-3)*c^2)", //
        "(Sign(a)*Sign(c)^2)/Sign(b)^3");

    // message Power: Infinite expression 1/0 encountered.
    check("Sign(1/0^(0.8+I*(-1.2)))", //
        "Indeterminate");
    check("Sign(Sign(z))", //
        "Sign(z)");
    check("Sign(Exp(z))", //
        "E^(I*Im(z))");

    check("Sign(2+I)", //
        "(2+I)/Sqrt(5)");
    check("Sign(1+I*Sqrt(3))", //
        "1/2*(1+I*Sqrt(3))");
    check("Sign(Sqrt(3)+I)", //
        "1/2*(I+Sqrt(3))");
    check("Sign(1.0+I)", //
        "0.707107+I*0.707107");

    check("Sign(Indeterminate)", //
        "Indeterminate");
    check("Sign(2.5)", //
        "1");
    check("Sign(-2.5)", //
        "-1");
    check("Sign(0.0)", //
        "0");
    check("Sign({-2, -1, 0, 1, 2})", //
        "{-1,-1,0,1,1}");
    check("Pi>E", //
        "True");
    check("Pi<E", //
        "False");
    check("Sign(1+I)", //
        "(1+I)/Sqrt(2)");

    check("Sign(E - Pi)", //
        "-1");
    check("Sign(0)", //
        "0");
    check("Sign(I)", //
        "I");
    check("Sign(-2*I)", //
        "-I");
    check("Sign(Indeterminate)", //
        "Indeterminate");
    check("Sign(Infinity)", //
        "1");
    check("Sign(-Infinity)", //
        "-1");
    check("Sign(DirectedInfinity(1+I*3))", //
        "(1+I*3)/Sqrt(10)");
    check("Sign(ComplexInfinity)", //
        "Indeterminate");
    check("Sign(I*Infinity)", //
        "I");

    check("Sign(-x)", //
        "-Sign(x)");
    check("Sign(-3*a*b*c)", //
        "-Sign(a*b*c)");
    check("Sign(1/z)", //
        "1/Sign(z)");
  }

  // @Test
  // public void testSimplify0() {
  // check("Factor(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
  // "2/((1+x)*(1/(x*(1+x))-ArcTan(Sqrt(x))/x^(3/2)))");
  // check("Simplify(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
  // "(-2*x^(3/2))/(-Sqrt(x) + (1 + x)*ArcTan(Sqrt(x)))");
  // }

  @Test
  public void testSimplify() {
    check("Simplify(a-a*b-a*c)", //
        "a*(1-b-c)");
    check("Simplify(a-a*b+a*c)", //
        "a*(1-b+c)");

    // issue #930
    check("Simplify(Sin(Pi*Cosh(45522*Csc(17/36*Pi))))", //
        "Sin(Pi*Cosh(45522*Csc(17/36*Pi)))");

    check("Simplify((1/x-1/3)/(x-3))", //
        "-1/(3*x)");

    check("Simplify(Abs(Sign(z)),z!=-1&& z!=0&&z!=1)", //
        "1");
    check("Simplify(Sign(x), x<0)", //
        "-1");

    check("Simplify(r0/.{r0->r})", //
        "r");
    check("Simplify(Log(E^n))", //
        "Log(E^n)");
    check("Simplify(Log(E^n),n>0)", //
        "n");

    check("Simplify(0^x, x==0)", //
        "Indeterminate");
    check("Simplify(0^x, x>0)", //
        "0");
    check("Simplify(0^x, x<0)", //
        "ComplexInfinity");

    check(" Simplify(y -> 1+Cot(x)^2)", //
        "y->Csc(x)^2");
    // TODO ???
    // check("Simplify(Sqrt(1+a*x)/Sqrt(1-a^2*x^2) )", //
    // "1/Sqrt(1-a*x)");
    check("Together((-574-2*Sqrt(21))/(-476))", //
        "1/238*(287+Sqrt(21))");

    check("Simplify(Abs(x)^2,Element(x,Reals))", //
        "x^2");
    check("Simplify(Abs(x)^3,Element(x,Reals))", //
        "Abs(x)^3");

    check("Simplify(1+Cot(x)^2)", //
        "Csc(x)^2");
    check("Simplify(1+Tan(x)^2)", //
        "Sec(x)^2");
    check("Simplify(E^(a1*a2*Log(f)+b+Log(g)))", //
        "E^b*f^(a1*a2)*g");
    check("Simplify(E^(a1*a2*Log(f)*Log(h)+b+Log(g)))", //
        "E^(b+a1*a2*Log(f)*Log(h))*g");
    check("Simplify(E^(a1*a2*Log(f)+Log(g)))", //
        "f^(a1*a2)*g");
    check("Simplify((-a+c)*(-a+x))", //
        "(a-c)*(a-x)");
    check("Simplify(Log(a*(a-b)*(a-c)-a^2*x+a*b*x+a*c*x-b*c*x))", //
        "Log((a-b)*(a-c)*(a-x))");
    check("Simplify((2/3-a)^(-5/4)*(2/3+a)^(-5/4))", //
        "1/(4/9-a^2)^(5/4)");
    check("Simplify((2-a)^(c)* (2+a)^(c))", //
        "(4-a^2)^c");
    // FullSimplify tries more steps (even for the same expressions as in Simplify)
    check("FullSimplify((2/3-a)^(-5/4)*(2/3+a)^(-5/4))", //
        "1/(4/9-a^2)^(5/4)");
    check("FullSimplify((2-a)^(c)* (2+a)^(c))", //
        "(4-a^2)^c");

    // https://github.com/axkr/symja_android_library/issues/142
    check("Simplify({{x+y+x*y==9},{x*y*(x+y)==20}})", //
        "{{x+y+x*y==9},{x*y*(x+y)==20}}");
    check("Simplify(-3+2*x+x^2==0)", //
        "2*x+x^2==3");
    check("Simplify({Im(Exp(I*Pi/5)* x), Im(2*x + I)}, x > 3)", //
        "{Im(E^(I*1/5*Pi)*x),1}");

    // check("Simplify(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
    // "(-2*x^(3/2))/(-Sqrt(x) + (1 + x)*ArcTan(Sqrt(x)))");
    // check("Simplify((-1/(Sqrt(x)*(1+x))+(-1-x)/(2*x^(3/2)*(1+x)))/(1+x))", //
    // "(-1 - 3*x)/(2*x^(3/2)*(1 + x)^2)");
    // check("Simplify(1/(ArcTan(Sqrt(x))/(-1/(Sqrt(x)*(1+x)^2)-1/(2*x^(3/2)*(1+x))))", //
    // "(-2*x^(3/2)*(1 + x)^2*ArcTan(Sqrt(x)))/(1 + 3*x)");
    //
    // check("Simplify((1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2))/((d+e*x^2)*((-4*e*(d*Sqrt(-e/d)+e*x)*x^2)/(d+e*x^2)^2+(2*e*x)/(d+e*x^2)+(2*(d*Sqrt(-e/d)+e*x))/(d+e*x^2))))",
    // //
    // "(-d+2*d*Sqrt(-e/d)*x+e*x^2)/(2*d*(-d*Sqrt(-e/d)-2*e*x+e*Sqrt(-e/d)*x^2))");//"-Sqrt(-(e/d))/(2*e)");
    check(
        "Simplify((x*(-Sqrt(-4+x^2)+x^2*Sqrt(-4+x^2)-4*Sqrt(-1+x^2)+x^2*Sqrt(-1+x^2)))/((4-5*x^2+x^4)*(x/Sqrt(-4+x^2)+x/Sqrt(-1+x^2))))", //
        "1");
    check("Simplify((Cos(x)-I*Sin(x))/(I*Cos(x)-Sin(x)))", //
        "-I*Cos(2*x)-Sin(2*x)"); // -I*Cos(2*x)-Sin(2*x)

    check("Expand((-I*a+b)*(I*Cosh(x)+Sinh(x)))", //
        "a*Cosh(x)+I*b*Cosh(x)-I*a*Sinh(x)+b*Sinh(x)");
    check("Factor(TrigToExp( a*Cosh(x)+I*b*Cosh(x)-I*a*Sinh(x)+b*Sinh(x) ))", //
        "((1/2+I*1/2)*(-I*a+b)*(I+E^(2*x)))/E^x");
    check("Simplify((-I*a+b)*(I*Cosh(x)+Sinh(x)))", //
        "(a+I*b)*(Cosh(x)-I*Sinh(x))");
    check("Simplify(Element(x, Reals), x>0)", //
        "True");
    check("Simplify(Sin(n*Pi), Element(n, Integers))", //
        "0");
    check("Simplify(Tan(x + Pi*n), Element(n, Integers))", //
        "Tan(x)");
    check(
        "Simplify(a/((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b)))+(b*Sinh(x))/((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b))))", //
        "(a+b*Sinh(x))/(a-I*b)");
    check("Simplify((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b)))", //
        "a-I*b");
    check("Simplify(-2*Log(2))", //
        "-Log(4)");
    check("Simplify(Log(6)-Log(2))", //
        "Log(3)");
    check("Simplify(a+Log(1/6)+Log(1/7)+z())", //
        "a-Log(42)+z()");
    check("Simplify(a+Log(1/6)+Log(1/7)+Log(3/4)+z())", //
        "a-Log(56)+z()");
    check("Simplify(a+Log(1/6)-2*Log(1/7)+7*Log(3/4)+z())", //
        "a+Log(35721/32768)+z()");
    check("Simplify(1+n/2)", //
        "1/2*(2+n)");
    check("Simplify((9-Sqrt(57))*x^2)", //
        "(9-Sqrt(57))*x^2");
    check("Simplify(-a/(-b+a*c))", //
        "a/(b-a*c)");
    check("Simplify(1/(Cos(x)+I*Sin(x))-(c+d*x)^n)", //
        "-(c+d*x)^n+Cos(x)-I*Sin(x)");
    check("Simplify(1/(Cos(x)+I*Sin(x)))", //
        "Cos(x)-I*Sin(x)");
    check("Expand((Sqrt(-d)*e+d*Sqrt(e)*Sqrt(-e/d))*(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d)))", //
        "0");
    check("Simplify((e*x^2)/(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d)))", //
        "(e*x^2)/(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d))");
    check("Simplify((-b^12*x^456)/x^12+(x^12*(a+b*x^37)^12)/x^12)", //
        "-b^12*x^444+(a+b*x^37)^12");
    check("Simplify(-Cos(x) +Sin(x)^2/(1-Cos(x)))", //
        "1");
    check("Simplify(-Cos(x)/(1-Cos(x))+Sin(x)^2/(1-Cos(x))^2-1/(1-Cos(x)))", //
        "0");

    check("Simplify(x^(5/2) - Sqrt(x^5), x>=0)", //
        "0");
    check("Simplify(-136+40*Sqrt(17))", //
        "8*(-17+5*Sqrt(17))");
    check("Simplify(Sqrt(17)/(5+Sqrt(17)))", //
        "1/8*(-17+5*Sqrt(17))");

    // check("Simplify(Cos(b*x)/(-Cos(b*x)/x^2-CosIntegral(b*x)/x^2))", //
    // "(x^2*Cos(b*x))/(-Cos(b*x)-CosIntegral(b*x))");
    check("Together(-Cos(b*x)/x^2+(-b*Sin(b*x))/x)", //
        "(-Cos(b*x)-b*x*Sin(b*x))/x^2");
    check("Simplify(-Cos(b*x)/x^2+(-b*Sin(b*x))/x)", //
        "(-Cos(b*x)-b*x*Sin(b*x))/x^2");

    check("Simplify(-(b/(2*Sqrt(c))+Sqrt(c)*x)^24+(a+b*x+c*x^2)^12)", //
        "-(b/(2*Sqrt(c))+Sqrt(c)*x)^24+(a+b*x+c*x^2)^12");
    check("Simplify(-ArcTan((1+x)/Sqrt(2))/(2*Sqrt(2)))", //
        "-ArcTan((1+x)/Sqrt(2))/(2*Sqrt(2))");
    check("Simplify(1 + 1/GoldenRatio - GoldenRatio)", //
        "0");
    // check("Simplify(-15-6*x)/(3*(1+x+x^2))", "");
    check("Simplify(Abs(x), x<0)", //
        "Abs(x)");
    check("complexity(x_) := 2*Count(x, _Abs, {0, 10}) + LeafCount(x)", //
        "");
    check("Simplify(Abs(x), x<0, ComplexityFunction->complexity)", //
        "-x");

    check("Simplify(100*Log(2))", //
        "100*Log(2)");
    check("Simplify(2*Sin(x)^2 + 2*Cos(x)^2)", //
        "2");
    check("Simplify(f(x))", //
        "f(x)");
    check("Simplify(a*x^2+b*x^2)", //
        "(a+b)*x^2");

    check("Simplify(5*x*(6*x+30))", //
        "30*x*(5+x)");
    check("Simplify(Sqrt(x^2), Assumptions -> x>0)", //
        "x");
    check("Simplify(Sqrt(x^2), x>0)", //
        "x");
    check("Together(2/(1/Tan(x) + Tan(x)))", //
        "2/(Cot(x)+Tan(x))");
    check("Together(2*Tan(x)/(1 + Tan(x)^2))", //
        "(2*Tan(x))/(1+Tan(x)^2)");
    check("Simplify(Sin(x)^2 + Cos(x)^2)", //
        "1");
    check("Simplify((x - 1)*(x + 1)*(x^2 + 1) + 1)", //
        "x^4");
    check("Simplify(3/(x + 3) + x/(x + 3))", //
        "1");

    check("Simplify(2*Tan(x)/(1 + Tan(x)^2))", //
        "Sin(2*x)");
    check(
        "Simplify((1/3+(1/3)*(-2)^(-1/3)*2^(-2/3)*(1+(0+1*I)*3^(1/2))+(1/6)*(-1)^(1/3)*(1+(0+-1*I)*3^(1/2)))^2)", //
        "1");
    check(
        "Simplify((1/3+(1/3)*(-2)^(-1/3)*2^(-2/3)*(1+(0+-1*I)*3^(1/2))+(1/6)*(-1)^(1/3)*(1+(0+1*I)*3^(1/2)))^2)", //
        "0");

    check("$Assumptions=(x>0);Simplify(0^x)", //
        "0");
  }

  @Test
  public void testSimplifyPreferences() {
    check("Simplify(Sqrt(2)^3)", //
        "2*Sqrt(2)");
    check("Simplify(2*Sqrt(2))", //
        "2*Sqrt(2)");
    check("Simplify(6+2*a)", //
        "2*(3+a)");
    check("Simplify(2*(3+a))", //
        "2*(3+a)");
  }

  @Test
  public void testSimplifyBoolean() {
    check("Simplify((x+1)&&(x+1))", //
        "1+x");
    check("Simplify(b&&b)", //
        "b");
    check("Simplify(a && b && !b )", //
        "False");
    check("Simplify((a || b) && (a || c) )", //
        "a||(b&&c)");
    check("Simplify(a || ! a && b)", //
        "a||b");
    check("Simplify( a || b || ! a || ! b)", //
        "True");
    check("Simplify(a || (a && Infinity))", //
        "a");
    check("Simplify((b || a) && (c || a))", //
        "a||(b&&c)");
    check("Simplify((a || b || c || Not((a && b && c))))", //
        "True");
    check("Simplify((a || b || c || Not((a && b && c && d))))", //
        "True");
    check("Simplify( (a || b || c || d || Not((a && b && c))))", //
        "True");
    check("Simplify((a || b || c || d || Not(a)))", //
        "True");
    check("Simplify((az || b || cz || Not((ay && b && cy && dy))))", //
        "True");
    check("Simplify( a || (a && b && c && d) )", //
        "a");
    check("Simplify(d || (a && b && c && d))", //
        "d");
    check("Simplify(d || e || (a && b && c && d) )", //
        "d||e");
    check("Simplify(d || b || (a && b && c && d))", //
        "b||d");
    check("Simplify(d || b || (a && b && c && d) || ! b)", //
        "True");
    check("Simplify(foo(d || b || (a && b && c && d) || ! b))", //
        "foo(True)");
    check("Simplify(d || e || (a && b && c))", //
        "d||e||(a&&b&&c)");
    check("Simplify(z || z)", //
        "z");
    check("Simplify(z || a || z)", //
        "a||z");
    check("Simplify(a || a && b )", //
        "a");
    check("Simplify(a || !a && b )", //
        "a||b");
    check("Simplify(a || c || ! a && b )", //
        "a||b||c");
    check("Simplify( a || c || ! a && ! c && b )", //
        "a||b||c");
    check("Simplify(a || c || ! a && ! c && ! b)", //
        "a||!b||c");
    check("Simplify(a || c || ! a && ! c && ! b && d )", //
        "a||c||(!b&&d)");
    check("Simplify( c || a || Not(b))", //
        "c||a||!b");
    check("Simplify(And(x1, a, x2, Not(Or(x3, a, x4)), x5))", //
        "False");
    check("Simplify(And(x1, a, x2, Or(x3, a, x4), x5))", //
        "a&&x1&&x2&&x5");
    check("Simplify(a&&b&&a)", //
        "a&&b");
  }

  @Test
  public void testSin() {
    check("Sin(11/8*Pi)", //
        "-Cos(Pi/8)");
    // check("Sin(4/15*Pi)", //
    // "Cos(7/30*Pi)");
    check("trigs={Sin,Cos,Tan,Cot}", //
        "{Sin,Cos,Tan,Cot}");
    check("trigs[[1]][10.0]", //
        "-0.544021");
    check("Sin[SparseArray[{{2,3},{4,5}}]]//MatrixForm", //
        "{{Sin(2),Sin(3)},\n" + " {Sin(4),Sin(5)}}");
    check("Sin(Interval({-Infinity,Infinity}))", //
        "Interval({-1,1})");
    check("Sin({})", //
        "{}");
    check("Sin(5*Pi/12)", //
        "(1+Sqrt(3))/(2*Sqrt(2))");
    // check("Sin(Quantity(90,\"Degree\"))",
    // "");
    check("Sin(-37/3*Pi+x)", //
        "-Cos(Pi/6+x)");
    check("Sin(83/7*Pi+x)", //
        "-Cos(5/14*Pi+x)");
    check("Refine(Sin(x+k*Pi), Element(k, Integers))", //
        "(-1)^k*Sin(x)");
    check("Sin(3/4*Pi+x)", //
        "Cos(Pi/4+x)");
    check("Sin(5/7*Pi+x)", //
        "Cos(3/14*Pi+x)");
    check("Sin(-3/4*Pi+x)", //
        "-Sin(Pi/4+x)");

    check("Sin({})", //
        "{}");
    check("Sin(e - Pi/2 + f*x)", //
        "-Cos(e+f*x)");
    check("Sin( -3/x+x )", //
        "-Sin(3/x-x)");
    check("Sin((-3+x^2)/x)", //
        "Sin((-3+x^2)/x)");
    check("Sin(-Pi/2+z)", //
        "-Cos(z)");
    check("Sin(e-Pi/2+f*x)", //
        "-Cos(e+f*x)");
    check("Sin(e-3/2*Pi+f*x)", //
        "Cos(e+f*x)");
    check("Sin(e-1+f*x)", //
        "-Sin(1-e-f*x)");

    check("Sin(ArcSin(x))", //
        "x");
    check("Sin(ArcCos(x))", //
        "Sqrt(1-x^2)");
    check("Sin(ArcTan(x))", //
        "x/Sqrt(1+x^2)");
    check("Sin(ArcCot(x))", //
        "1/Sqrt(1+x^2)");
    check("Sin(ArcCsc(x))", //
        "1/x");
    check("Sin(ArcSec(x))", //
        "Sqrt(1-1/x^2)");

    check("Sin(Pi/4)", //
        "1/Sqrt(2)");
    check("Sin(0)", //
        "0");
    checkNumeric("Sin(0.5)", //
        "0.479425538604203");
    check("Sin(3*Pi)", //
        "0");
    checkNumeric("Sin(1.0 + I)", //
        "1.2984575814159773+I*0.6349639147847361");

    checkNumeric("Sin(1.1*Pi)", //
        "-0.30901699437494773");
    checkNumeric("Sin({-0.5,9.1})", //
        "{-0.479425538604203,0.3190983623493521}");
    checkNumeric("Sin({{0.5,1.1},{6.4,7.5}})", //
        "{{0.479425538604203,0.8912073600614354},\n"
            + " {0.11654920485049364,0.9379999767747389}}");
    check("Sin({1,2})", //
        "{Sin(1),Sin(2)}");
    check("Sin(z+1/4*Pi)", //
        "Sin(Pi/4+z)");
    check("Sin(z+1/2*Pi)", //
        "Cos(z)");
    check("Sin(z+1/3*Pi)", //
        "Sin(Pi/3+z)");
    check("Sin(Pi)", //
        "0");
    check("Sin(z+Pi)", //
        "-Sin(z)");
    check("Sin(z+42*Pi)", //
        "Sin(z)");
    check("Sin(x+y+z+43*Pi)", //
        "-Sin(x+y+z)");
    check("Sin(z+42*a*Pi)", //
        "Sin(42*a*Pi+z)");
  }

  @Test
  public void testSinc() {
    checkNumeric("Sinc(3.5)", //
        "-0.10022377933989138");
    checkNumeric("N(Sinc(35/10),50)", //
        "-0.10022377933989138517724822858389588145285192455445");
    checkNumeric("Sinc(1 + 3.5*I)", //
        "3.413480749977026+I*(-3.009162956293193)");
    checkNumeric("N(Sinc(1+35/10*I),50)", //
        "3.4134807499770263967370518627588410722367885846662+I*(-3.0091629562931933945895646489407216870241346727697)");

    check("Sinc(-x)", //
        "Sinc(x)");
    check("Table(Sinc(n*Pi/3), {n, 0, 6})", //
        "{1,3/2*Sqrt(3)/Pi,3/4*Sqrt(3)/Pi,0,-3/8*Sqrt(3)/Pi,-3/10*Sqrt(3)/Pi,0}");
    check("Sinc(3.5)", //
        "-0.100224");
    check("Sinc(1.0+3.5*I)", //
        "3.41348+I*(-3.00916)");
    check("(2*Sqrt(2))/Pi", //
        "(2*Sqrt(2))/Pi");
    check("2+(-Sqrt(5))/8", //
        "2-Sqrt(5)/8");
    check("Sinc(0)", //
        "1");
    check("Sinc(1/6*Pi)", //
        "Pi/3");
    check("Sinc(1/4*Pi)", //
        "(2*Sqrt(2))/Pi");
    check("Sinc(1/3*Pi)", //
        "3/2*Sqrt(3)/Pi");
    check("Sinc(1/2*Pi)", //
        "2/Pi");
    check("Sinc(Pi)", //
        "0");
    check("Sinc(5/12*Pi)", //
        "3/5*(Sqrt(2)*(1+Sqrt(3)))/Pi");
    check("Sinc(Pi/5)", //
        "(5*Sqrt(5/8-Sqrt(5)/8))/Pi");
    check("Sinc(Pi/12)", //
        "(3*Sqrt(2)*(-1+Sqrt(3)))/Pi");
    check("Sinc(Pi/10)", //
        "5/2*(-1+Sqrt(5))/Pi");
    check("Sinc(2/5*Pi)", //
        "5/2*Sqrt(5/8+Sqrt(5)/8)/Pi");
    check("Sinc(3/10*Pi)", //
        "5/6*(1+Sqrt(5))/Pi");
    check("Sinc(I)", //
        "Sinh(1)");
    check("Sinc(ArcSin(x))", //
        "x/ArcSin(x)");
    check("Sinc(ArcCos(x))", //
        "Sqrt(1-x^2)/ArcCos(x)");
    check("Sinc(ArcTan(x))", //
        "x/(Sqrt(1+x^2)*ArcTan(x))");
    check("Sinc(I*Infinity)", //
        "Infinity");
    check("Sinc(ComplexInfinity)", //
        "Indeterminate");
  }

  @Test
  public void testSinh() {
    check("Sinh(Pi*I+x)", //
        "-Sinh(x)");
    check("Sinh(10*Pi*I+x)", //
        "Sinh(x)");
    check("Sinh(43*Pi*I+x)", //
        "-Sinh(x)");
    check("Sinh(0)", //
        "0");
    check("Sinh(42*I*Pi)", //
        "0");
    check("Sinh(3/2*I*Pi)", //
        "-I");
    check("Sinh(5/3*Pi*I)", //
        "-I*1/2*Sqrt(3)");

    check("Sinh(Infinity)", //
        "Infinity");
    check("Sinh(ComplexInfinity)", //
        "Indeterminate");
  }


  @Test
  public void testSixJSymbol() {
    // check("SixJSymbol({1, 2, 1}, {4,5,Pi})", //
    // "0");
    // check("SixJSymbol({1, 2, 1}, {4,5,12})", //
    // "0");

    // // TODO implement for half-integers
    // // check("SixJSymbol({1/2, 1/2, 1}, {5/2,7/2,3})", //
    // // "SixJSymbol({1/2,1/2,1},{5/2,7/2,3})");
    //
    // check("SixJSymbol({1, 1, 2}, {5,7,6})", //
    // "1/Sqrt(65)");
    // check("SixJSymbol({1, 2, 1}, {2,3,2})", //
    // "1/(5*Sqrt(21))");
    // check("SixJSymbol({1, 2, 3}, {2, 1, 2})", //
    // "1/(5*Sqrt(21))");
    // check("SixJSymbol({1, 2, 3}, {1,2,2})", //
    // "1/15");
  }

  @Test
  public void testSkewness() {
    check("Skewness(WeibullDistribution(n,m))", //
        "(2*Gamma(1+1/n)^3-3*Gamma(1+1/n)*Gamma(1+2/n)+Gamma(1+3/n))/(-Gamma(1+1/n)^2+Gamma(\n" + //
            "1+2/n))^(3/2)");
    check("Skewness(UniformDistribution())", //
        "0");
    check("Skewness(StudentTDistribution(n ))", //
        "Piecewise({{0,n>3}},Indeterminate)");
    check("Skewness(PoissonDistribution(n ))", //
        "1/Sqrt(n)");
    check("Skewness(NormalDistribution(n,m))", //
        "0");
    check("Skewness(NakagamiDistribution(n,m))", //
        "(Pochhammer(n,1/2)*(1/2-2*(n-Pochhammer(n,1/2)^2)))/(n-Pochhammer(n,1/2)^2)^(3/2)");
    check("Skewness(LogNormalDistribution(n,m))", //
        "(2+E^m^2)*Sqrt(-1+E^m^2)");
    check("Skewness(GumbelDistribution(n,m))", //
        "(-12*Sqrt(6)*Zeta(3))/Pi^3");
    check("Skewness(GeometricDistribution(n))", //
        "(2-n)/Sqrt(1-n)");
    check("Skewness(GammaDistribution(n,m))", //
        "2/Sqrt(n)");
    check("Skewness(FrechetDistribution(n,m))", //
        "Piecewise({{(Gamma(1-3/n)-3*Gamma(1-2/n)*Gamma(1-1/n)+2*Gamma(1-1/n)^3)/(Gamma(1-\n"
            + "2/n)-Gamma(1-1/n)^2)^(3/2),n>3}},Infinity)");
    check("Skewness(FRatioDistribution(n,m))", //
        "Piecewise({{(2*Sqrt(2)*Sqrt(-4+m)*(-2+m+2*n))/((-6+m)*Sqrt(n)*Sqrt(-2+m+n)),m>6}},Indeterminate)");
    check("Skewness(ExponentialDistribution(n))", //
        "2");
    check("Skewness(ErlangDistribution(n,m))", //
        "2/Sqrt(n)");
    check("Skewness(DiscreteUniformDistribution({n,m}))", //
        "0");
    check("Skewness(BinomialDistribution(n,m))", //
        "(1-2*m)/Sqrt((1-m)*m*n)");
    check("Skewness(BernoulliDistribution(a))", //
        "(1-2*a)/Sqrt((1-a)*a)");
    check("Skewness(ChiSquareDistribution(a))", //
        "2*Sqrt(2)*Sqrt(1/a)");

    check("Skewness({1.1, 1.2, 1.4, 2.1, 2.4})", //
        "0.407041");
  }

  @Test
  public void testSlot() {
    // check("x^2+x", "x+x^2");
    check("f(#1, X) &[ ]", //
        "f(#1,X)");
    check("(# &)[a, b, c]", //
        "a");
    check("f = If(#1 == 1, 1, #1*#0(#1 - 1)) &", //
        "If(#1==1,1,#1*#0[-1+#1])&");
    check("f(10)", //
        "3628800");
    check("# &[1, 2, 3]", //
        "1");
    check("#1 &[1, 2, 3]", //
        "1");
    check("g(#0) &[x]", //
        "g(g(#0)&)");

    // check("#1^2+#1", "#1^2+#1");
    // check("#1+#1^7", "#1");
    check("#", //
        "#1");
    check("#42", //
        "#42");
  }

  @Test
  public void testSlotSequence() {
    check("f(##1, X, ##2, Y, ##3, Z, ##4, W, ##5) &[a, b, c, d]", //
        "f(a,b,c,d,X,b,c,d,Y,c,d,Z,d,W)");
    check("(## &)[a, b, c]", //
        "Identity(a,b,c)");
    check("(##2 &)[a, b, c]", //
        "Identity(b,c)");
    check("(##4 &)[a, b, c]", //
        "Identity()");
    check("(##5 &)[a, b, c]", //
        "##5");
    check("(##-1 &)[a, b, c]", //
        "-1+a+b+c");
    check("(##2-7 &)[a, b, c]", //
        "-7+b+c");
    check("##", //
        "##1");
    check("##42", //
        "##42");
    check("f(x, ##, y, ##) &[a, b, c, d]", //
        "f(x,a,b,c,d,y,a,b,c,d)");
    check("f(##2) &[a, b, c, d]", //
        "f(b,c,d)");
    check("{##2} &[a, b, c]", //
        "{b,c}");
  }

  @Test
  public void testSokalSneathDissimilarity() {
    check("SokalSneathDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
        "3/4");
    check("SokalSneathDissimilarity({True, False, True}, {True, True, False})", //
        "4/5");
    check("SokalSneathDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
        "0");
    check("SokalSneathDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
        "1");
  }


  @Test
  public void testSort() {
    check("Sort({f(1,2,3), f(1,4)})", //
        "{f(1,4),f(1,2,3)}");
    check("Sort(<|a -> 4, b -> 1, c -> 3, e :> 2, d -> 2|>)", //
        "<|b->1,e:>2,d->2,c->3,a->4|>");
    check("Sort(<|a -> 4, b -> 1, c -> 3, d :> 2, e -> 2|>, Greater)", //
        "<|a->4,c->3,d:>2,e->2,b->1|>");
    check("Sort({2.1,1.1-I,2.1-I,I*E^(I*x)})", //
        "{1.1+I*(-1.0),2.1,2.1+I*(-1.0),I*E^(I*x)}");
    check("Sort({2,1-I,2-I,I*E^(I*x)})", //
        "{1-I,2-I,2,I*E^(I*x)}");
    check("Sort(StringJoin /@ Tuples({\"a\",\"A\",\"b\",\"B\"},2))", //
        "{aa,aA,Aa,AA,ab,aB,Ab,AB,ba,bA,Ba,BA,bb,bB,Bb,BB}");
    check("Sort({a,A,a,b,B})", //
        "{a,a,A,b,B}");
    check("Sort@{ 1+x^2,2+x^2,x(x),-x,x,Cos(x^2),Sin(x^2)}", //
        "{-x,x,1+x^2,2+x^2,Cos(x^2),Sin(x^2),x(x)}");
    check("Sort@{x,1+x^2,1+x^2+y^3+z^4,Cos(x^2),Sin(x^2)}", //
        "{x,1+x^2,1+x^2+y^3+z^4,Cos(x^2),Sin(x^2)}");
    check("Sort({E,a,D,d,N,b,c, Adele, enigma})", //
        "{a,adele,b,c,d,D,E,enigma,N}");
    check("Sort({d, b, c, a})", //
        "{a,b,c,d}");
    check("Sort({4, 1, 3, 2, 2}, Greater)", //
        "{4,3,2,2,1}");
    check("Sort({4, 1, 3, 2, 2}, #1 > #2 &)", //
        "{4,3,2,2,1}");
    check("Sort({{a, 2}, {c, 1}, {d, 3}}, #1[[2]] < #2[[2]] &)", //
        "{{c,1},{a,2},{d,3}}");
    check("Sort({4, 1.0, a, 3+I})", //
        "{1.0,3+I,4,a}");
  }

  @Test
  public void testReverseSort() {
    check("ReverseSort({4, 1.0, a, 3+I})", //
        "{a,4,3+I,1.0}");

    check("ReverseSort({c, b, d, a})", //
        "{d,c,b,a}");
    check("ReverseSort({-42,0,17,11,3,4,9})", //
        "{17,11,9,4,3,0,-42}");
    // TODO sort `e` before `d`
    check("ReverseSort(<|a -> 4, b -> 1, c -> 3, d -> 2, e -> 2|>)", //
        "<|a->4,c->3,d->2,e->2,b->1|>");
    check("ReverseSort({0, 11, 13, 4, 9}, Greater)", //
        "{0,4,9,11,13}");
    check("ReverseSort({c, b, d, a}, OrderedQ({#1, #2}) &)", //
        "{d,c,b,a}");

    // TODO define AlphabeticOrder
    // check("ReverseSort({\"cat\", \"fish\", \"catfish\", \"Cat\"}, AlphabeticOrder(\"English\"))",
    // //
    // "{cat,fish,catfish,Cat}");
  }

  @Test
  public void testSortBy() {
    check("SortBy({{5, 1}, {10, -1}}, Last)", //
        "{{10,-1},{5,1}}");
    check("SortBy(Total)[{{5, 1}, {10, -9}}]", //
        "{{10,-9},{5,1}}");
  }

  @Test
  public void testSow() {
    check("Reap(Do(If(GCD(num, den) == 1, Sow(num)), {den, 1, 20}, {num, 1, den-1}) )[[2, 1]] ", //
        "{1,1,2,1,3,1,2,3,4,1,5,1,2,3,4,5,6,1,3,5,7,1,2,4,5,7,8,1,3,7,9,1,2,3,4,5,6,7,8,9,\n" + //
            "10,1,5,7,11,1,2,3,4,5,6,7,8,9,10,11,12,1,3,5,9,11,13,1,2,4,7,8,11,13,14,1,3,5,7,\n" + //
            "9,11,13,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,1,5,7,11,13,17,1,2,3,4,5,6,7,8,\n" + //
            "9,10,11,12,13,14,15,16,17,18,1,3,7,9,11,13,17,19}");
    check("Reap(Sow(1, x); Sow(2, y); Sow(3, x); Sow(4, y))", //
        "{4,{{1,3},{2,4}}}");
    check("Reap(Sow(a); b; Sow(c); Sow(d); e)", //
        "{e,{{a,c,d}}}");
    check("Reap(Sum(Sow(i0^2) + 1, {i0, 10}))", //
        "{395,{{1,4,9,16,25,36,49,64,81,100}}}");
  }


  @Test
  public void testSplice() {
    check("Splice({a,b,c})", //
        "Splice({a,b,c})");
    check("Splice(12)", //
        "Splice(12)");

    check("{1,2,Splice({x,x,x}),2,4,3}", //
        "{1,2,x,x,x,2,4,3}");
    check("{a, b, c, Splice({ }), d, e}", //
        "{a,b,c,d,e}");
    check("h(a, b, c, Splice({1, 2, 3}), d, e)", //
        "h(a,b,c,Splice({1,2,3}),d,e)");
    check("{a, b, c, Splice[{1, 2, 3}], d, e}", //
        "{a,b,c,1,2,3,d,e}");

    check("{a, b, c, Splice[{1, 2, 3},h], d, e}", //
        "{a,b,c,Splice({1,2,3},h),d,e}");
    check("h(a, b, c, Splice({1, 2, 3}, h), d, e)", //
        "h(a,b,c,1,2,3,d,e)");
    check("h(a, b, c, Splice({{1,1},{2,2}}, h), d, e)", //
        "h(a,b,c,{1,1},{2,2},d,e)");

    check("f(1, 2, 3, Splice({x, y}, f | g), 4, 5)", //
        "f(1,2,3,x,y,4,5)");
    check("f(a)[1, 2, 3, Splice({x, y}, _f), 4, 5]", //
        "f(a)[1,2,3,x,y,4,5]");
  }

  @Test
  public void testSplit() {
    check("Split({x, x, x, y, x, y, y, z})", //
        "{{x,x,x},{y},{x},{y,y},{z}}");
    check("Split({x, x, x, y, x, y, y, z}, x)", //
        "{{x},{x},{x},{y},{x},{y},{y},{z}}");
    check("Split({1, 5, 6, 3, 6, 1, 6, 3, 4, 5, 4}, Less)", //
        "{{1,5,6},{3,6},{1,6},{3,4,5},{4}}");
    check("Split({1, 5, 6, 3, 6, 1, 6, 3, 4, 5, 4}, Greater)", //
        "{{1},{5},{6,3},{6,1},{6,3},{4},{5,4}}");
    check("Split({x -> a, x -> y, 2 -> a, z -> c, z -> a}, First(#1) === First(#2) &)", //
        "{{x->a,x->y},{2->a},{z->c,z->a}}");
    check("Split({})", //
        "{}");
  }

  @Test
  public void testSplitBy() {
    check("SplitBy(Range(1, 3, 1/3), Round)", //
        "{{1,4/3},{5/3,2,7/3},{8/3,3}}");
    check("SplitBy({1, 2, 1, 1.2}, {Round, Identity})", //
        "{{{1}},{{2}},{{1},{1.2}}}");
    check("Tuples({1, 2}, 3)", //
        "{{1,1,1},{1,1,2},{1,2,1},{1,2,2},{2,1,1},{2,1,2},{2,2,1},{2,2,2}}");
    check("SplitBy(Tuples({1, 2}, 3), First)", //
        "{{{1,1,1},{1,1,2},{1,2,1},{1,2,2}},{{2,1,1},{2,1,2},{2,2,1},{2,2,2}}}");
  }

  @Test
  public void testSqrt() {
    check("Sqrt(-Sqrt(3))", //
        "I*3^(1/4)");
    check("Sqrt(Sqrt(3))", //
        "3^(1/4)");
    check("Sqrt((1-a)*a)", //
        "Sqrt((1-a)*a)");
    check("(-3/4)/Sqrt(-3/4)", //
        "I*1/2*Sqrt(3)");
    check("(3/4)/Sqrt(3/4)", //
        "Sqrt(3)/2");
    check("(-3)/Sqrt(-3)", //
        "I*Sqrt(3)");
    check("3/Sqrt(3)", //
        "Sqrt(3)");
    check("Sqrt(4)", //
        "2");
    check("Sqrt(5)", //
        "Sqrt(5)");
    check("Sqrt(5) // N", //
        "2.23607");
    check("Sqrt(a)^2", //
        "a");
    check("Sqrt(-4)", //
        "I*2");
    check("I == Sqrt(-1)", //
        "True");
    // TODO use ExprParser#getReal() if apfloat problems are fixed
    // check("N(Sqrt(2), 50)",
    // "1.41421356237309504880168872420969807856967187537694");
  }

  @Test
  public void testSquareFreeQ() {
    check("SquareFreeQ(-45+28*I)", //
        "False");
    check("SquareFreeQ(6+7*I)", //
        "True");

    // message: SquareFreeQ: Currently not supported: number of variables in expression (2) unequals
    // number of user variables (1).
    check("SquareFreeQ(x*y^2, x)", //
        "SquareFreeQ(x*y^2,x)");
    check("SquareFreeQ(x*y^2, y)", //
        "SquareFreeQ(x*y^2,y)");

    check("SquareFreeQ(5/6)", //
        "True");
    check("SquareFreeQ(3/4)", //
        "False");

    check("SquareFreeQ(9)", //
        "False");

    check("SquareFreeQ(5)", //
        "True");
    check("SquareFreeQ(9)", //
        "False");
    check("SquareFreeQ(20)", //
        "False");
    check("SquareFreeQ(10)", //
        "True");
    check("SquareFreeQ(12)", //
        "False");
    check("SquareFreeQ(105)", //
        "True");
    check("SquareFreeQ(x^4-1)", //
        "True");
    check("SquareFreeQ(x^4 - 2*x^2 + 1)", //
        "False");
    check("SquareFreeQ(x^2+1)", //
        "True");
    check("SquareFreeQ(9 + 6*x + x^2)", //
        "False");
    check("SquareFreeQ(x^2 + 1, Modulus -> 2)", //
        "False");
    check("SquareFreeQ(6+6*x+x^2)", //
        "True");
  }

  @Test
  public void testSquaredEuclideanDistance() {
    check("SquaredEuclideanDistance({-7, 5.0}, {1, 1})", //
        "80.0");
    check("SquaredEuclideanDistance({-1, -1}, {1.5, 1})", //
        "10.25");
    check("SquaredEuclideanDistance({-7, 5}, {1, 1})", //
        "80");
    check("SquaredEuclideanDistance({-1, -1}, {1, 1})", //
        "8");
  }

  @Test
  public void testSpan() {
    check("OddQ^(n) && n > 0;;", //
        "(OddQ^n&&n>0);;All");
    check("Infinity[[2;;4]]", //
        "Infinity[[2;;4]]");

    check("FullForm(1;;4;;2)", //
        "Span(1, 4, 2)");

    check("{a, b, c, d, e, f, g, h}[[2 ;; -3]]", //
        "{b,c,d,e,f}");

    check("FullForm( ;; )", //
        "Span(1, All)");
    check("FullForm( ;;;;3 )", //
        "Span(1, All, 3)");
    check("FullForm( 2;;;;3 )", //
        "Span(2, All, 3)");
    check("FullForm(1;;4;;2)", //
        "Span(1, 4, 2)");
    check("FullForm(2;;-2)", //
        "Span(2, -2)");
    check("FullForm(;;3)", //
        "Span(1, 3)");
    // check("a ;; b ;; c ;; d", "(1;;d) (a;;b;;c)");

    check("{a, b, c, d, e, f, g, h}[[2 ;; -3]]", //
        "{b,c,d,e,f}");
    check("{a, b, c, d, e, f, g, h}[[2 ;; 5]]", //
        "{b,c,d,e}");
    check("{a, b, c, d, e, f, g, h}[[2 ;; All]]", //
        "{b,c,d,e,f,g,h}");
  }

  @Test
  public void testStandardize() {
    check("Standardize(N(Range(5)), Mean, 1 &)", //
        "{-2.0,-1.0,0.0,1.0,2.0}");
    check("Standardize(N(Range(5)), Median, InterquartileRange)", //
        "{-0.8,-0.4,0.0,0.4,0.8}");

    check("Standardize( Range(5) )", //
        "{-2*Sqrt(2/5),-Sqrt(2/5),0,Sqrt(2/5),2*Sqrt(2/5)}");

    check("Standardize(SparseArray({{a,b},{c,d}}))", //
        "{{(Sqrt(2)*(a+1/2*(-a-c)))/Sqrt((a-c)*(Conjugate(a)-Conjugate(c))),(Sqrt(2)*(b+1/\n" //
            + "2*(-b-d)))/Sqrt((b-d)*(Conjugate(b)-Conjugate(d)))},{(Sqrt(2)*(1/2*(-a-c)+c))/Sqrt((a-c)*(Conjugate(a)-Conjugate(c))),(Sqrt(\n"
            + "2)*(1/2*(-b-d)+d))/Sqrt((b-d)*(Conjugate(b)-Conjugate(d)))}}");
    check("Standardize({6.5, 3.8, 6.6, 5.7, 6.0, 6.4, 5.3})", //
        "{0.75705,-1.99453,0.85896,-0.0582346,0.247497,0.655139,-0.465877}");
    check("Standardize({{a,b},{c,d}})", //
        "{{(Sqrt(2)*(a+1/2*(-a-c)))/Sqrt((a-c)*(Conjugate(a)-Conjugate(c))),(Sqrt(2)*(b+1/\n" //
            + "2*(-b-d)))/Sqrt((b-d)*(Conjugate(b)-Conjugate(d)))},{(Sqrt(2)*(1/2*(-a-c)+c))/Sqrt((a-c)*(Conjugate(a)-Conjugate(c))),(Sqrt(\n"//
            + "2)*(1/2*(-b-d)+d))/Sqrt((b-d)*(Conjugate(b)-Conjugate(d)))}}");
  }


  @Test
  public void testSquaresR() {
    check("Table(SquaresR(8, n), {n, 10})", //
        "{16,112,448,1136,2016,3136,5504,9328,12112,14112}");
    // TODO
    // check("TimeConstrained(SquaresR({3},2147483647),3)", //
    // "");

    // message: $RecursionLimit: Recursion depth of 512 exceeded during evaluation of
    // SquaresR(2147483647,11).
    check("SquaresR(2147483647,11)", //
        "Hold(SquaresR(2147483647,11))");

    check("Table(SquaresR(2, n), {n, 100})", //
        "{4,4,0,4,8,0,0,4,4,8,0,0,8,0,0,4,8,4,0,8,0,0,0,0,12,8,0,0,8,0,0,4,0,8,0,4,8,0,0,\n" //
            + "8,8,0,0,0,8,0,0,0,4,12,0,8,8,0,0,0,0,8,0,0,8,0,0,4,16,0,0,8,0,0,0,4,8,8,0,0,0,0,\n" //
            + "0,8,4,8,0,0,16,0,0,0,8,8,0,0,0,0,0,0,8,4,0,12}");
    check("Sum(SquaresR(2, k), {k, 0, 20^2})", //
        "1257");
    check("sierpinski(n_) := Sum(SquaresR(2, k)/k, {k, n})-Pi * Log(n);", //
        "");
    check("N(sierpinski(10000))", //
        "2.58509");
  }

  @Test
  public void testPowersRepresentations() {
    // Message
    check("PowersRepresentations(2147483647,1,{0})", //
        "PowersRepresentations(2147483647,1,{0})");
    check("PowersRepresentations(8174, 6, 3)", //
        "{{0,0,4,10,13,17},{0,3,6,7,9,19},{0,7,10,12,12,15},{1,1,1,11,14,16},{1,3,5,12,13,\n" //
            + "16},{1,4,5,5,10,19},{1,5,6,10,10,18},{2,3,4,6,10,19},{3,3,3,4,13,18},{3,5,9,10,\n" //
            + "13,16},{4,5,6,13,13,15},{4,9,12,12,12,13},{5,9,9,13,13,13},{7,7,10,10,14,14}}");
    check("PowersRepresentations(100, 1, 2)", //
        "{{10}}");
    check("PowersRepresentations(1729, 0, 3)", //
        "{}");
    check("PowersRepresentations(1729, 2, 3)", //
        "{{1,12},{9,10}}");
    check("PowersRepresentations(102, 3, 2)", //
        "{{1,1,10},{2,7,7}}");
    check("PowersRepresentations(100, 2, 2)", //
        "{{0,10},{6,8}}");
    check("PowersRepresentations(87539319, 2, 3)", //
        "{{167,436},{228,423},{255,414}}");
    check("PowersRepresentations(6963472309248, 2, 3)", //
        "{{2421,19083},{5436,18948},{10200,18072},{13322,16630}}");
  }


  @Test
  public void testStack() {
    // print: {f,g,CompoundExpression,Print}
    check("f(g(1, Print(Stack()); 2))", //
        "f(g(1,2))");

    check("f(g(1, Print(Stack(_)); 2))", //
        "f(g(1,2))");
  }

  @Test
  public void testStackBegin() {
    // print: {Plus,Times,g,CompoundExpression,Print}
    check("1 + x + f(StackBegin(2 + x*g(Print(Stack( )); 2)))", //
        "1+x+f(2+x*g(2))");
    check("1 + x + f(StackBegin(2 + x*g(Print(Stack(_)); 2)))", //
        "1+x+f(2+x*g(2))");
  }

  @Test
  public void testStandardDeviation() {
    check("StandardDeviation({1, 2, 3})", //
        "1");
    check("StandardDeviation({7, -5, 101, 100})", //
        "Sqrt(13297)/2");
    check("StandardDeviation({a, a})", //
        "0");
    check("StandardDeviation({{1, 10}, {-1, 20}})", //
        "{Sqrt(2),5*Sqrt(2)}");
    check("StandardDeviation({1.21, 3.4, 2, 4.66, 1.5, 5.61, 7.22})", //
        "2.27183");
    check("StandardDeviation(LogNormalDistribution(0, 1))", //
        "Sqrt((-1+E)*E)");
  }

  @Test
  public void testStieltjesGamma() {
    check("StieltjesGamma(8,Indeterminate)", //
        "Indeterminate");
    check("StieltjesGamma(0)", //
        "EulerGamma");
    check("StieltjesGamma(0,a)", //
        "-PolyGamma(0,a)");
  }

  @Test
  public void testStirlingS1() {
    // message Maximum AST dimension 20834 exceeded
    check("StirlingS1(10007,11)", //
        "StirlingS1(10007,11)");
    check("StirlingS1(9,6)", //
        "-4536");
    check("StirlingS1(0,0)", //
        "1");
    check("StirlingS1(1,1)", //
        "1");
    check("StirlingS1(0,1)", //
        "0");
    check("StirlingS1(1,0)", //
        "0");
    check("StirlingS1(50,1)", //
        "-608281864034267560872252163321295376887552831379210240000000000");
    check("StirlingS1({2,4,6},2)", //
        "{1,11,274}");
    check("Table(StirlingS1(12, m), {m, 5})", //
        "{-39916800,120543840,-150917976,105258076,-45995730}");
    check(
        "Table(Sum( StirlingS1(m, l)*StirlingS2(l, n), {l, 0, Max(n, m) + 1}), {n, 0,  5}, {m, 0, 5})", //
        "{{1,0,0,0,0,0},{0,1,0,0,0,0},{0,0,1,0,0,0},{0,0,0,1,0,0},{0,0,0,0,1,0},{0,0,0,0,\n"
            + "0,1}}");
  }

  @Test
  public void testStirlingS2() {
    check("StirlingS2(1317624576693539401,4)", //
        "StirlingS2(1317624576693539401,4)");
    check("StirlingS2(-1,-3)", //
        "StirlingS2(-1,-3)");

    check("StirlingS2(6,10)", //
        "0");
    check("StirlingS2(10,6)", //
        "22827");
    check("StirlingS2(0,0)", //
        "1");
    check("StirlingS2(1,1)", //
        "1");
    check("StirlingS2(0,1)", //
        "0");
    check("StirlingS2(1,0)", //
        "0");
    check("StirlingS2(10,11)", //
        "0");
    check("Table(StirlingS2(10, m), {m, 10})", //
        "{1,511,9330,34105,42525,22827,5880,750,45,1}");
    check("StirlingS2({2, 4, 6}, 2)", //
        "{1,7,31}");
    check("StirlingS2(10,4)", //
        "34105");
    check("StirlingS2(1000, 500)", "11897164077580438091910055658742826<<SHORT>>", //
        35);
    check("StirlingS2(2000, 199)", "12783663313027805423901972026528914<<SHORT>>", //
        35);
  }

  @Test
  public void testStruveH() {
    // message "BigInteger bit length 60600 exceeded"
    check(
        "StruveH(1009,-9223372036854775807/9223372036854775808-I*9223372036854775808/9223372036854775807)", //
        "StruveH(1009,-9223372036854775807/9223372036854775808-I*9223372036854775808/\n"
            + "9223372036854775807)");
    // https://github.com/paulmasson/math/issues/9
    check("StruveH(0, 50.0)", //
        "-0.0853377");
    check("StruveH(0, 30.0)", //
        "-0.0960984");

    check("StruveH(0, 5.2)", //
        "-0.212448");
    check("StruveH(0, 4.0)", //
        "0.135015");
    check("StruveH(7/3 + I, 4.5 - I)", //
        "2.35765+I*(-1.40054)");
    check("StruveH(1,{0.5, 1.0, 1.5})", //
        "{0.0521737,0.198457,0.410288}");
    check("StruveH(1.5, 3.5)", //
        "1.13192");
    check("StruveH(I,0)", //
        "0");
    check("StruveH(-1+I,0)", //
        "Indeterminate");
    check("StruveH(-2+I,0)", //
        "ComplexInfinity");
    check("StruveH(1/2,x)", //
        "Sqrt(2/Pi)*Sqrt(1/x)*(1-Cos(x))");
    System.out.print(".");
    check("StruveH(-1/2,x)", //
        "Sqrt(2/Pi)*Sqrt(1/x)*Sin(x)");
    check("StruveH(a,-x)", //
        "(-(-x)^a*StruveH(a,x))/x^a");
    // TODO values > 30
    check("Table(StruveH(0,x), {x, 0, 30.0})", //
        "{0.0,0.568657,0.790859,0.574306,0.135015,-0.185217,-0.184555,0.063383,0.301988,0.319876,0.118744,-0.111421,-0.172534,"
            + "-0.0295133,0.172443,0.247724,0.135449,-0.0553148,-0.152291,-0.076104,0.0943937,0.20045,0.148766,-0.00835413,-0.126354,"
            + "-0.101825,0.0364942,0.158762,0.154544,0.0314077,-0.0960984}"); //
  }

  @Test
  public void testStruveL() {
    check("StruveL(0, 5.0)", //
        "27.10592");
    check("StruveL(0, 2.5)", //
        "3.01121");
    check("StruveL(1.5, 3.5)", //
        "4.41126");
    check("StruveL(0, 4.0)", //
        "11.13105");
    check("StruveL(7/3 + I, 4.5 - I)", //
        "-0.977295+I*(-10.82588)");
    check("StruveL(1,{0.5, 1.0, 1.5})", //
        "{0.0539422,0.226764,0.553857}");

    check("StruveL(I,0)", //
        "0");
    check("StruveL(-1+I,0)", //
        "Indeterminate");
    check("StruveL(-2+I,0)", //
        "ComplexInfinity");
    check("StruveL(1/2,x)", //
        "Sqrt(2/Pi)*Sqrt(1/x)*(-1+Cosh(x))");
    check("StruveL(-1/2,x)", //
        "Sqrt(2/Pi)*Sqrt(1/x)*Sinh(x)");
    check("StruveL(a,-x)", //
        "(-(-x)^a*StruveL(a,x))/x^a");
    check("StruveL(1/2, ComplexInfinity)", //
        "Indeterminate");
    check("Table(StruveL(0,x), {x, 0, 5,0.25})", //
        "{0.0,0.160263,0.327241,0.507986,0.710243,0.942845,1.21616,1.54264,1.93743,2.41923,3.01121," //
            + "3.7423,4.64869,5.77582,7.18085,8.9357,11.13105,13.88131,17.33089,21.66224,27.10592}");
  }

  @Test
  public void testSubdivide() {
    // print: $IterationLimit: Iteration limit of 500 exceeded.
    // TODO
    // check(
    // "Subdivide(3/4,Quantity(1.2,\"m\"),11)", //
    // "{3/4+0[m],0,0,0,0,0,0,0,0,0,0,1.2[m]}");

    // message: Subdivide: The number of subdivisions given in position 3 of Subdivide(a,b,-1)
    // should be a positive machine-sized integer.
    check("Subdivide(a,b,-1)", //
        "Subdivide(a,b,-1)");

    check("N@Subdivide(-5, 5, 6)", //
        "{-5.0,-3.33333,-1.66667,0.0,1.66667,3.33333,5.0}");

    // TODO result should be {{x,y,z},{a/2+x/2,a/2+y/2,a/2+z/2},a}
    check("Subdivide({x,y,z}, a, 2)", //
        "{{x,y,z},{a/2+x/2,a/2+y/2,a/2+z/2},{a,a,a}}");
    check("Subdivide(N(E,21),E^(Pi*I*1/3),4)", //
        "{2.71828182845904523536," //
            + "2.16371137134428392652+I*0.21650635094610966169,"
            + "1.60914091422952261768+I*0.43301270189221932338,"
            + "1.05457045711476130884+I*0.64951905283832898507,"
            + "0.5+I*0.866025403784438646763}");
    check("Subdivide(10,  4)", //
        "{0,5/2,5,15/2,10}");
    check("Subdivide({10,5},  4)", //
        "{0,{5/2,5/4},{5,5/2},{15/2,15/4},{10,5}}");
    check("Subdivide({10,5}, {5,15}, 4)", //
        "{{10,5},{35/4,15/2},{15/2,10},{25/4,25/2},{5,15}}");
    check("Subdivide({10,5}, 3, 4)", //
        "{{10,5},{33/4,9/2},{13/2,4},{19/4,7/2},{3,3}}");
    check("Subdivide({10,5}, {1,5,15}, 4)", //
        "Subdivide({10,5},{1,5,15},4)");

    check("Subdivide(5)", //
        "{0,1/5,2/5,3/5,4/5,1}");
    check("Subdivide(10,15,5)", //
        "{10,11,12,13,14,15}");
    check("Subdivide(10,15,4)", //
        "{10,45/4,25/2,55/4,15}");
    check("Subdivide(-1, -4, 3)", //
        "{-1,-2,-3,-4}");
    check("Subdivide(10, 5, 4)", //
        "{10,35/4,15/2,25/4,5}");
    check("Subdivide(5, 15, 4)", //
        "{5,15/2,10,25/2,15}");

    check("Subdivide(10)", //
        "{0,1/10,1/5,3/10,2/5,1/2,3/5,7/10,4/5,9/10,1}");
    check("Subdivide(10, 5)", //
        "{0,2,4,6,8,10}");
    check("Subdivide(-1,2, 5)", //
        "{-1,-2/5,1/5,4/5,7/5,2}");
    check("Subdivide(-1.,1., 8)", //
        "{-1.0,-0.75,-0.5,-0.25,0.0,0.25,0.5,0.75,1.0}");
    check("Subdivide(E,Pi, 4)", //
        "{E,3/4*E+Pi/4,E/2+Pi/2,E/4+3/4*Pi,Pi}");
    check("Subdivide(a, b, 6)", //
        "{a,5/6*a+b/6,2/3*a+b/3,a/2+b/2,a/3+2/3*b,a/6+5/6*b,b}");
    check("Subdivide(N(E,21),N(Pi,21), 4)", //
        "{2.71828182845904523536,2.82410953474173223613,2.92993724102441923691,3.03576494730710623768,3.14159265358979323846}");
  }

  @Test
  public void testSubfactorial() {
    check("Subfactorial(0)", //
        "1");
    check("Subfactorial(12)", //
        "176214841");
    check("Subfactorial(n)", //
        "Subfactorial(n)");
    check("Table(Subfactorial(n), {n, 10})", //
        "{0,1,2,9,44,265,1854,14833,133496,1334961}");

    // The only number equal to the sum of subfactorials of its digits:
    check("148349 == Total(Subfactorial({1, 4, 8, 3, 4, 9}))", //
        "True");

    checkNumeric("Table(Subfactorial(x+I*y), {x,-2,2,0.5}, {y,-2 ,2,0.5})", //
        "{{-9.227084993591351+I*226.34804545754852,-3.60341368902144+I*57.52215812828752,-1.4981325243711963+I*15.10153257973711,-0.6594078761887109+I*4.109712417378417,-0.30282511676493395+I*1.1557273497909215,-0.14180973765939986+I*0.3301098233309934,-0.06578326936162285+I*0.09168075208986826,-0.02922777135324635+I*0.022366556059606305,-0.011937189234760486+I*0.003341706779474045},{-224.51199155103046+I*33.783138994311265,-57.42494014380803+I*9.344563804698737,-15.326014503799536+I*2.5908324907783915,-4.324154363255359+I*0.6889085300780474,-1.3040986643465844+I*0.15231802765107366,-0.42007108938336646+I*0.008720205463217,-0.1410130398831403+I*(-0.02117096465428944),-0.04657979136378597+I*(-0.02005229004488879),-0.0135786899199037+I*(-0.012819770339213575)},{-73.56847961607632+I*(-207.89387547036583),-21.43112760840351+I*(-52.11703759475536),-6.541027528670962+I*(-13.603400055365915),-2.0962132960874325+I*(-3.780008479284061),-0.6971748832350662+I*(-1.1557273497909215),-0.23112475035685875+I*(-0.40101469216069335),-0.06911140099201767+I*(-0.15746402145149108),-0.013305353757292544+I*(-0.06620821308947582),0.003386332944104408+I*(-0.027216085248995023)},{179.82227376413772+I*(-103.3592419198595),42.72931577895212+I*(-29.85265017649355),10.25383974267816+I*(-9.11009437436893),2.506531446666703+I*(-2.9928544643766957),0.6520493321732921+I*(-1.0761590138255368),0.20567544196007476+I*(-0.4222752237740537),0.09167748459585957+I*(-0.1736414758197678),0.05336833074922617+I*(-0.06882683304436397),0.03242888563837901+I*(-0.0226149374019086)},{119.70390458403307+I*147.13695923215263,33.1422220977232+I*32.146691412605264,9.537292577413355+I*6.541027528670962,2.9204731413233214+I*1.0481066480437162,1.0,0.4083869224311086+I*(-0.11556237517842935),0.20067793971526335+I*(-0.06911140099201767),0.10829561065534317+I*(-0.019958030635938814),0.05629961322969803+I*0.006772665888208815},{-116.80734695765014+I*124.1674870365595,-23.414317375264265+I*32.29747973318128,-3.98317450302985+I*8.331805702916647,-0.24316150885499624+I*2.060784425443652,0.3260246660866461+I*0.4619204930872316,0.31397533286706414+I*0.09957968544377245,0.21948021811769758+I*0.048070664949747925,0.12992441494115906+I*0.0546223706227867,0.0614443176230067+I*0.055417745307511694},{-121.51383247642643+I*(-92.27084993591352),-29.95551927322514+I*(-17.566641733979523),-7.062372526694953+I*(-2.9962650487423925),-1.3659509156201723+I*(-0.41212992261794434),0.0,0.25828853366956134+I*0.08863108603712493,0.22657542244350878+I*0.1315665387232457,0.12924936558812197+I*0.14248538534707594,0.0408868387215724+I*0.11937189234760487},{73.12395363664382+I*(-115.62573105462519),13.324743536875513+I*(-27.750082827187917),2.357043948371871+I*(-6.65980957537445),0.6656499494393316+I*(-1.5977199883723756),0.4890369991299692+I*(-0.30711926036915266),0.4211731565787101+I*0.09847761824842886,0.28114966222679844+I*0.24837229727854723,0.11295306647755851+I*0.2678368873247892,-0.01866901418051335+I*0.20414781047557296},{107.92229070008487+I*58.48596508102584,25.05677734243667+I*9.799995441878664,6.0196825306469695+I*1.0698424292101683,1.8725105884160347+I*(-0.14128438742580243),1.0,0.6801411006713222+I*0.3064064389090305,0.36479822442754406+I*0.4897084998900002,0.053753944176759445+I*0.47884481907633486,-0.15510266452035695+I*0.3205174621383546}}");

    // check("Subfactorial(10000)", "Subfactorial(10000)");
  }

  @Test
  public void testSubsetQ() {
    check("SubsetQ(f(b,a,b,c), f(c, c, c))", //
        "True");
    check("SubsetQ(f(b,a,b,c), f(a, b, d))", //
        "False");

    // same as ContainsAll
    check("SubsetQ({b,a,b,c}, {a, b})", //
        "True");
    check("SubsetQ({b,a,b,c}, {c, c, c})", //
        "True");
    check("SubsetQ({b,a,b,c}, {a, b, d})", //
        "False");
    check("SubsetQ({b, a, d}, {a, b, c})", //
        "False");
    check("SubsetQ({ }, {a, b, c})", //
        "False");
    check("SubsetQ({ },{ })", //
        "True");
    check("SubsetQ({a, b, c},{ })", //
        "True");

    check("SubsetQ(1, {1,2,3})", //
        "SubsetQ(1,{1,2,3})");
    check("SubsetQ({1,2,3}, 4)", //
        "SubsetQ({1,2,3},4)");

    check("SubsetQ({1.0,2.0}, {1,2,3})", //
        "False");
    check("SubsetQ({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
        "False");

    check("SubsetQ({1,2,3}, {1.0,2.0})", //
        "False");
    check("SubsetQ({1,2,3}, {1.0,2.0}, SameTest->Equal)", //
        "True");
  }

  @Test
  public void testSubsets() {
    // check(
    // "Subsets({a,b,c})", //
    // "{{},{a},{b},{c},{a,b},{a,c},{b,c},{a,b,c}}");

    check("Subsets({},{2})", //
        "{}");
    check("Subsets(Infinity,All)", //
        "{ComplexInfinity,Infinity}");
    check("Subsets(Infinity,Infinity)", //
        "{ComplexInfinity,Infinity}");
    check("Subsets(Infinity,-Infinity)", //
        "Subsets(Infinity,-Infinity)");

    // https://oeis.org/A018900 - Sum of two distinct powers of 2
    check("Union(Total/@Subsets(2^Range(0, 10), {2}))", //
        "{3,5,6,9,10,12,17,18,20,24,33,34,36,40,48,65,66,68,72,80,96,129,130,132,136,144,\n"
            + "160,192,257,258,260,264,272,288,320,384,513,514,516,520,528,544,576,640,768,1025,\n"
            + "1026,1028,1032,1040,1056,1088,1152,1280,1536}");
    check("Subsets()", //
        "Subsets()");
    check("Subsets({})", //
        "{{}}");
    check("Subsets({a,b,c})", //
        "{{},{a},{b},{c},{a,b},{a,c},{b,c},{a,b,c}}");
    check("Subsets({a,b,c},2)", //
        "{{},{a},{b},{c},{a,b},{a,c},{b,c}}");
    check("Subsets({a,b,c},{2})", //
        "{{a,b},{a,c},{b,c}}");
    check("Subsets({a,b,c,d},{2})", //
        "{{a,b},{a,c},{a,d},{b,c},{b,d},{c,d}}");
  }

  @Test
  public void testPartialSubsets() {
    check("Subsets({a, b, c, d}, All, {1, 15, 2})", //
        "{{},{b},{d},{a,c},{b,c},{c,d},{a,b,d},{b,c,d}}");
    check("Subsets({a, b, c, d}, All, {15, 1, -2})", //
        "{{b,c,d},{a,b,d},{c,d},{b,c},{a,c},{d},{b},{}}");
    check("Subsets(f[a, b, c, d, e], {3},{3,8,3})", //
        "{f(a,b,e),f(a,d,e)}");
    check("Subsets({a, b, c, d, e}, {3},{-3,-8,-3})", //
        "Subsets({a,b,c,d,e},{3},{-3,-8,-3})");
    check("Subsets({a, b, c, d, e}, {3},{3,8,3})", //
        "{{a,b,e},{a,d,e}}");
    check("Subsets({a, b, c, d, e}, {3},{3,8})", //
        "{{a,b,e},{a,c,d},{a,c,e},{a,d,e},{b,c,d},{b,c,e}}");



    check("Subsets({a, b, c, d, e}, {3}, 3)", //
        "{{a,b,c},{a,b,d},{a,b,e}}");
    check("Subsets({a, b, c, d, e}, {3}, -4)", //
        "{{b,c,d},{b,c,e},{b,d,e},{c,d,e}}");
    check("Subsets({a, b, c, d, e}, {3}, {3})", //
        "{{a,b,e}}");
    check("Subsets({a, b, c, d, e}, {3},  {3,4})", //
        "{{a,b,e},{a,c,d}}");
    check("Subsets({a, b, c, d, e}, {3},  {-3,-4})", //
        "{}");
    check("Subsets({a, b, c, d, e}, {3},  {-4,-3})", //
        "{{b,c,d},{b,c,e}}");
    check("Subsets({a, b, c, d, e}, {3},  {-4,-1})", //
        "{{b,c,d},{b,c,e},{b,d,e},{c,d,e}}");
    check("Subsets({a, b, c, d, e}, {3},  {-4,1})", //
        "{}");
    check("Subsets({a, b, c, d, e}, {3}, {-4})", //
        "{{b,c,d}}");
    check("Subsets({a, b, c, d, e}, {3})", //
        "{{a,b,c},{a,b,d},{a,b,e},{a,c,d},{a,c,e},{a,d,e},{b,c,d},{b,c,e},{b,d,e},{c,d,e}}");
    check("Subsets(Range(10), All, {1024})", //
        "{{1,2,3,4,5,6,7,8,9,10}}");
  }

  @Test
  public void testSubtract() {
    check("a - x + z", //
        "a-x+z");
    check("5 - 3", //
        "2");
    check("a - b // FullForm", //
        "Plus(a, Times(-1, b))");
    check("a - b - c", //
        "a-b-c");
    check("a - (b - c)", //
        "a-b+c");
  }

  @Test
  public void testSubtractFrom() {
    check("a = 10", //
        "10");
    check("a -= 2", //
        "8");
    check("a", //
        "8");

    check("index={1,2,3,4,5,6,7,8,9}", //
        "{1,2,3,4,5,6,7,8,9}");
    check("index[[3]]-=y", //
        "3-y");
    check("index", //
        "{1,2,3-y,4,5,6,7,8,9}");
  }

  @Test
  public void testSurd() {
    checkNumeric("N(Surd(-2,  5),25)", //
        "-1.148698354997035006798626");

    checkNumeric("N(Surd({-3, -2, -1, 0, 1, 2, 3}, 7))", //
        "{-1.169930812758687,-1.1040895136738123,-1.0,0.0,1.0,1.1040895136738123,1.169930812758687}");


    check("Surd(EulerGamma,3)", //
        "EulerGamma^(1/3)");
    check("Surd(EulerGamma,-7)", //
        "1/EulerGamma^(1/7)");
    check("Table(E^(n*I*Pi/3),{n,{0,2,-2}})", //
        "{1,-1/2+I*1/2*Sqrt(3),-1/2-I*1/2*Sqrt(3)}");
    check("Table(Surd(EulerGamma,3)*E^(n*I*Pi/3),{n,{0,2,-2}})", //
        "{EulerGamma^(1/3),(-1/2+I*1/2*Sqrt(3))*EulerGamma^(1/3),(-1/2-I*1/2*Sqrt(3))*EulerGamma^(\n"
            + "1/3)}");
    check("Refine((-x)^(1/3),x<0)", //
        "(-x)^(1/3)");
    check("Refine(Surd(x,3),x<0)", //
        "-(-x)^(1/3)");
    check("Refine(Surd(x,-7),x<0) // FullForm", //
        "Times(-1, Power(Times(-1, x), Rational(-1,7)))");
    check("Surd(x,3)^14", //
        "x^4*Surd(x,3)^2");
    check("Surd(x,3)^(-14)", //
        "1/(x^4*Surd(x,3)^2)");
    check("Surd(x,3)^13", //
        "x^4*Surd(x,3)");
    check("Surd(x,3)^(-13)", //
        "1/(x^4*Surd(x,3))");
    check("Surd(x,5)^15", //
        "x^3");
    check("Surd(x,5)^(-15)", //
        "1/x^3");
    check("Surd(x,6)^23", //
        "Surd(x,6)^23");
    check("Surd(x,6)^(-23)", //
        "1/Surd(x,6)^23");
    check("Surd(x,7)^23", //
        "x^3*Surd(x,7)^2");
    check("Surd(x,7)^(-23)", //
        "1/(x^3*Surd(x,7)^2)");
    check("Surd(x,7)^8", //
        "x*Surd(x,7)");
    check("Surd(x,7)^11", //
        "x*Surd(x,7)^4");

    check("Surd(x,-1)", //
        "1/x");

    // print: Surd: Integer expected at position 2 in Surd(-4.5,1/3).
    check("Surd(-4.5, 1/3)", //
        "Surd(-4.5,1/3)");

    check("Surd(-4.5, 3)", //
        "-1.65096");
    check("Power(-4.5, 1/3)", //
        "0.825482+I*1.42978");

    check("Surd(-16.0,2)", //
        "Indeterminate");

    checkNumeric("Surd(-3,3)", //
        "-3^(1/3)");
    checkNumeric("N((-3)^(1/3))", //
        "0.7211247851537042+I*1.2490247664834064");
    checkNumeric("Surd(-3,3)-(-3)^(1/3)", //
        "-(-3)^(1/3)-3^(1/3)");
    checkNumeric("Surd(-3.,3)-(-3)^(1/3)", //
        "-2.1633743554611122+I*(-1.2490247664834064)");
    checkNumeric("Surd(-3,3)", //
        "-3^(1/3)");
    checkNumeric("Surd(-3.,3)", //
        "-1.4422495703074083");
    checkNumeric("N(Surd(-3,3))", //
        "-1.4422495703074083");

    checkNumeric("1/9 * 3^(4/3)", //
        "1/3^(2/3)");
    // checkNumeric("1/9 * 3^(7/4)", "1/3^(1/4)");
    checkNumeric("1/9 * 3^(3/4)", //
        "1/(3*3^(1/4))");
    checkNumeric("1/9 * 3^(-1/2)", //
        "1/(9*Sqrt(3))");
    checkNumeric("1/9 * 3^(1/2)", //
        "1/(3*Sqrt(3))");
    checkNumeric("2^(1/4)*2^(-3)", //
        "1/(4*2^(3/4))");
    checkNumeric("2^(-3)", //
        "1/8");
    checkNumeric("2^(-3/4)", //
        "1/2^(3/4)");

    // checkNumeric("Trace((2^(1/4))/8)", "");
    checkNumeric("Surd(2,4)/8-(1/(4*2^(1/4.0)))", //
        "-0.061573214438088525");
    checkNumeric("1/(4*2^(1/4.0))", //
        "0.21022410381342865");
    checkNumeric("Surd(2,4)", //
        "2^(1/4)");
    checkNumeric("Surd(2,4)/8", //
        "1/(4*2^(3/4))");

    checkNumeric("Surd(-2.,5)", //
        "-1.148698354997035");
    // checkNumeric("(-2.0)^(1/5)", "-1.148698354997035");

    check("Surd(-16.0,2)", //
        "Indeterminate");
    checkNumeric("Surd(-2.,5)", //
        "-1.148698354997035");
    check("Surd(-3,2)", //
        "Indeterminate");
    check("Surd(-3,-2)", //
        "Indeterminate");

    check("Surd(I,2)", //
        "Surd(I,2)");
    check("Surd({-3, -2, -1, 0, 1, 2, 3}, 7)", //
        "{-3^(1/7),-2^(1/7),-1,0,1,2^(1/7),3^(1/7)}");
  }

  @Test
  public void testSurvivalFunction() {
    check("SurvivalFunction(GeometricDistribution(1/3), x)", //
        "1-Piecewise({{1-(2/3)^(1+Floor(x)),x>=0}},0)");
    check("SurvivalFunction(NormalDistribution(), {0.2, 0.3})", //
        "{0.42074,0.382089}");
    check("SurvivalFunction(BetaDistribution(1/2,1/2), {{0.0, 0.0}, {0.2, 0.2}, {0.3, 0.3}})", //
        "{{1.0,1.0},{0.704833,0.704833},{0.63099,0.63099}}");
    check("SurvivalFunction(NormalDistribution(0, 1), x)", //
        "1-Erfc(-x/Sqrt(2))/2");
    check("CDF(NormalDistribution(0, 1), x)", //
        "Erfc(-x/Sqrt(2))/2");
  }

  @Test
  public void testSwitch() {
    check("Switch(2, 1, x, 2, y, 3, z)", //
        "y");
    check("Switch(5, 1, x, 2, y)", //
        "Switch(5,1,x,2,y)");
    check("Switch(5, 1, x, 2, y, _, z)", //
        "z");
    check("Switch(2, 1)", //
        "Switch(2,1)");
    check("$f(b_) := switch(b, True, 1, False, 0, _, -1);{$f(True), $f(False), $f(x)}", //
        "{1,0,-1}");

    check("f::boole = \"The value `1` is not True or False.\";", //
        "");
    check("f(b_) := Switch(b, True, 1, False, 0, _, Message(f::boole, b); 0)", //
        "");
    check("{f(True), f(False), f(x)}", //
        "{1,0,0}");


    check("t(e_) := Switch(e, _Plus, Together, _Times, Apart, _, Identity)", //
        "");
    check("e = (1 + x)/(1 - x) + x/(1 + x);t(e)", //
        "Together");
    check("t(e)[e]", //
        "(1+3*x)/((1-x)*(1+x))");

  }

  @Test
  public void testSymbol() {
    check("Head(x)", //
        "Symbol");
    check("Symbol(\"x\") + Symbol(\"x\")", //
        "2*x");
    check("i\\[CapitalGamma]j(hjgg)", //
        "iγj(hjgg)");
    check("i\\[Alpha]j=10;i\\[Alpha]j", //
        "10");
  }

  @Test
  public void testSymbolName() {
    check("Context(x)", //
        "Global`");
    check("SymbolName(x)", //
        "x");
    // TODO allow contexts
    // check("SymbolName(a`b`x)", "x");

  }

  @Test
  public void testSymbolQ() {
    check("SymbolQ(a)", //
        "True");
    check("SymbolQ(1)", //
        "False");
    check("SymbolQ(a+b)", //
        "False");
  }

  @Test
  public void testSyntaxQ() {
    check("SyntaxQ(\"Integrate(f(x),{x,0,10})\")", //
        "True");
    check("SyntaxQ(\"Integrate(f(x),{x,0,10)\")", //
        "False");
  }

  @Test
  public void testTable() {
    EvalEngine.resetModuleCounter4JUnit();

    check("Table(n, {n, 2147483647, 2147483647 , 1})", //
        "{2147483647}");
    check("Table(n, {n, 2147483643, 2147483647 , 2})", //
        "{2147483643,2147483645,2147483647}");
    check("Table(n, {n, -2147483640, -2147483648 , -1})", //
        "{-2147483640,-2147483641,-2147483642,-2147483643,-2147483644,-2147483645,-\n"
            + "2147483646,-2147483647,-2147483648}");

    check("Table(n, {n, -2147483640, -2147483655 , -1})", //
        "{-2147483640,-2147483641,-2147483642,-2147483643,-2147483644,-2147483645,-\n"
            + "2147483646,-2147483647,-2147483648,-2147483649,-2147483650,-2147483651,-\n"
            + "2147483652,-2147483653,-2147483654,-2147483655}");
    check("Table(n, {n, -2147483640, -2147483655 , -3})", //
        "{-2147483640,-2147483643,-2147483646,-2147483649,-2147483652,-2147483655}");

    check("Table(n, {n, 2147483640, 2147483650})", //
        "{2147483640,2147483641,2147483642,2147483643,2147483644,2147483645,2147483646,\n"
            + "2147483647,2147483648,2147483649,2147483650}");
    check("Table(n, {n, 2147483640, 2147483650,3})", //
        "{2147483640,2147483643,2147483646,2147483649}");

    check(
        "Module({i}, Table({{1,2},{1,2},{1,2},{1,2}}[[ i,{{1,2},{1,2},{1,2},{1,2}}[[i]] ]], {i,2}))", //
        "{{1,2},{1,2}}");
    check("Table(101,{-2})", //
        "{}");
    check("Table(z, {z,-2.0,2.0,0.1})", //
        "{-2.0,-1.9,-1.8,-1.7,-1.6,-1.5,-1.4,-1.3,-1.2,-1.1,-1.0,-0.9,-0.8,-0.7,-0.6,-0.5,-0.4,-0.3,-0.2,-0.1,"
            + "6.38378*10^-16,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0}");

    // message nliter
    check("s={x,1,10};Table(f(x),s)", //
        "Table(f(x),s)");

    check("Table(Sum(k^n, {k, 0, m}), {n, 1, 5, 1})", //
        "{1/2*m*(1+m),m/6+m^2/2+m^3/3,m^2/4+m^3/2+m^4/4,-m/30+m^3/3+m^4/2+m^5/5,-m^2/12+5/\n"
            + "12*m^4+m^5/2+m^6/6}");
    check("Table(f(x), {x, a, a+1})", //
        "{f(a),f(1+a)}");
    check("s=0;Table(s=i+s, {i, 0, 7})", //
        "{0,1,3,6,10,15,21,28}");
    check("Table(a + dx, {dx, 0, 3, Pi/8})", //
        "{a,a+Pi/8,a+Pi/4,a+3/8*Pi,a+Pi/2,a+5/8*Pi,a+3/4*Pi,a+7/8*Pi}");
    check("Table(0.0^x, {x, -1.0, 1.0, 0.25})", //
        "{ComplexInfinity,ComplexInfinity,ComplexInfinity,ComplexInfinity,Indeterminate,0.0,0.0,0.0,0.0}");
    check("Table(0^x, {x,-1, 1,1/4})", //
        "{ComplexInfinity,ComplexInfinity,ComplexInfinity,ComplexInfinity,Indeterminate,0,\n" + //
            "0,0,0}");
    check("Table(x, {4})", //
        "{x,x,x,x}");
    check("n=0", "0");
    check("Table(n= n + 1, {5})", //
        "{1,2,3,4,5}");
    check("Table(i, {i, 4})", //
        "{1,2,3,4}");
    check("Table(i, {i, 2, 5})", //
        "{2,3,4,5}");
    check("Table(i, {i, 2, 6, 2})", //
        "{2,4,6}");
    check("Table(i, {i, Pi, 2*Pi, Pi / 2})", //
        "{Pi,3/2*Pi,2*Pi}");
    check("Table(x^2, {x, {a, b, c}})", //
        "{a^2,b^2,c^2}");
    check("Table({i, j}, {i, {a, b}}, {j, 1, 2})", //
        "{{{a,1},{a,2}},{{b,1},{b,2}}}");
    check("Table(x, {x,0,1/3})", //
        "{0}");
    check("Table(x, {x, -0.2, 3.9})", //
        "{-0.2,0.8,1.8,2.8,3.8}");

    // check("Timing(Length(Table(i, {i, 1, 10000})))", "{0.159,10000}");
    check("Table(x,10)", //
        "{x,x,x,x,x,x,x,x,x,x}");
    check("Table(x,-1)", //
        "{}");
    check("Table(0,{4-1})", //
        "{0,0,0}");
    check("$a=10;Table($a^2, {$a, 10})", //
        "{1,4,9,16,25,36,49,64,81,100}");
    check("Table(f(a), {a, 0, 20, 2})", //
        "{f(0),f(2),f(4),f(6),f(8),f(10),f(12),f(14),f(16),f(18),f(20)}");
    check("Table(x, {10})", //
        "{x,x,x,x,x,x,x,x,x,x}");
    check("Table(10*a + j, {a, 4}, {j, 3})", //
        "{{11,12,13},{21,22,23},{31,32,33},{41,42,43}}");
    check("Table(f(a), {a, 10, -5, -2})", //
        "{f(10),f(8),f(6),f(4),f(2),f(0),f(-2),f(-4)}");
    check("Table(Sqrt(x), {x, {1, 4, 9, 16}})", //
        "{1,2,3,4}");
    check("Table(100*a + 10*j + k, {a, 3}, {j, 2}, {k, 4})", //
        "{{{111,112,113,114},{121,122,123,124}},{{211,212,213,214},{221,222,223,224}},{{\n"
            + "311,312,313,314},{321,322,323,324}}}");
    check("Table(j^(1/a), {a, {1, 2, 4}}, {j, {1, 4, 9}})", //
        "{{1,4,9},{1,2,3},{1,Sqrt(2),Sqrt(3)}}");
    check("Table(2^x + x, {x, a, a + 5*b, b})", //
        "{2^a+a,2^(a+b)+a+b,2^(a+2*b)+a+2*b,2^(a+3*b)+a+3*b,2^(a+4*b)+a+4*b,2^(a+5*b)+a+5*b}");
    check("Table(a, {a, Pi, 2*Pi, Pi / 2})", //
        "{Pi,3/2*Pi,2*Pi}");

    check(
        "b := 3 ; a := 1+b ; Length(Table(a*x^2,{x,-1.0,1.0605456,0.060606062},{y,-a+b^2,a+b^2,0.160606062}))", //
        "34");

    check("x=2;Table(x^2,{x,0,10,1})", //
        "{0,1,4,9,16,25,36,49,64,81,100}");
  }

  @Test
  public void testTablePerformance001() {
    check("$IterationLimit=10000; " //
        + "  data = Table(\n"//
        + "                With({r = RandomReal({0, 5}), t = RandomReal({0, 2*Pi})}, " //
        + "                       {r*Cos(t), r*Sin(t), Sin(r^2)/r^2}), " //
        + "               {10^4});", //
        "");
  }

  @Test
  public void testTableForm() {
    // check(
    // "TableForm({{a, b}, {c, d}, {e, f}}, TableHeadings -> {None, {\"c1\", \"c2\"}})", //
    // "");

    String expected = String.join("\n", //
        " 1             ", //
        " 1  2          ", //
        " 1  2  3       ", //
        " 1  2  3  4    ", //
        " 1  2  3  4  5 ");
    check("TableForm({{1},{1,2},{1,2,3},{1,2,3,4},{1,2,3,4,5}})", //
        expected);
    expected = String.join("\n", //
        " 1             ", //
        " 1  2          ", //
        " a             ", //
        " b             ", //
        " c             ", //
        " 1  2  3       ", //
        " 1  2  3  4    ", //
        " 1  2  3  4  5 ");
    check("TableForm({{1},{1,2},a,b,c,{1,2,3},{1,2,3,4},{1,2,3,4,5}})", //
        expected);
    expected = String.join("\n", //
        " a ", //
        " b ", //
        " 3 ");
    check("TableForm({a,b,3})", //
        expected);
    expected = String.join("\n", //
        " a ");
    check("TableForm(a)", //
        expected);
    expected = String.join("\n", //
        " a(1) ", //
        " a(2) ");
    check("TableForm(SparseArray(Array(a, {2})))", //
        expected);
    expected = String.join("\n", //
        " a(1,1)  a(1,2) ", //
        " a(2,1)  a(2,2) ");
    check("TableForm(SparseArray(Array(a, {2,2})))", //
        expected);
    expected = String.join("\n", //
        " a(1) ", //
        " a(2) ");
    check("TableForm(Array(a, {2}))", //
        expected);
    expected = String.join("\n", //
        " a(1,1)  a(1,2) ", //
        " a(2,1)  a(2,2) ");
    check("TableForm(Array(a, {2,2}))", //
        expected);
    // check("TableForm(RandomReal(5, {3, 4}))", //
    // "3.3966021212581032 0.37386691098759195 3.3714636398631437 4.888805919349176 \n" +
    // "3.977341548616806 0.7908229654057569 0.4052451831886883 1.455709563360077 \n" +
    // "1.0028599118204118 1.2764414981301953 4.024172644043268 1.3404548410990742");

  }

  @Test
  public void testTake() {
    check("Take({a, b, c, d},0)", //
        "{}");
    check("Take(<|1 -> a, 2 -> b, 3 -> c|>,0)", //
        "<||>");
    check("Take(SparseArray(Range(1000)), {100, 105})", //
        "{100,101,102,103,104,105}");
    check("Take(<|1 -> a, 2 -> b, 3 -> c|>, {2})", //
        "<|2->b|>");
    check("Take(<|1 -> a, 2 -> b, 3 -> c, 4 -> d|>, {2, -1})", //
        "<|2->b,3->c,4->d|>");
    check("Take(x^2,{8,2,-1})", //
        "Take(x^2,{8,2,-1})");
    check("Take({a, b, c, d}, -2)", //
        "{c,d}");
    check("Take({a, b, c, d}, 3)", //
        "{a,b,c}");
    check("Take({a, b, c, d}, -2)", //
        "{c,d}");
    check("Take({a, b, c, d, e}, {2, -2})", //
        "{b,c,d}");

    check("A = {{a, b, c}, {d, e, f}}", //
        "{{a,b,c},{d,e,f}}");
    check("Take(A, 2, 2)", //
        "{{a,b},{d,e}}");
    check("Take(A, All, {2})", //
        "{{b},{e}}");

    check("Take(Range(10), {8, 2, -1})", //
        "{8,7,6,5,4,3,2}");
    check("Take(Range(10), {-3, -7, -2})", //
        "{8,6,4}");
    check("Take(Range(6), {-5, -2, -2})", //
        "Take({1,2,3,4,5,6},{-5,-2,-2})");
    check("Take(l, {-1})", //
        "Take(l,{-1})");
    check("Take({1, 2, 3, 4, 5}, {-1, -2})", //
        "{}");
    check("Take({1, 2, 3, 4, 5}, {0, -1})", //
        "{}");
    check("Take({1, 2, 3, 4, 5}, {1, 0})", //
        "{}");
    check("Take({1, 2, 3, 4, 5}, {2, 1})", //
        "{}");
    check("Take({1, 2, 3, 4, 5}, {1, 0, 2})", //
        "{}");
    check("Take({1, 2, 3, 4, 5}, {1, 0, -1})", //
        "Take({1,2,3,4,5},{1,0,-1})");

    check("Take({a, b, c, d, e, f}, All)", //
        "{a,b,c,d,e,f}");
    check("Take({a, b, c, d, e, f}, 4)", //
        "{a,b,c,d}");
    check("Take({a, b, c, d, e, f}, -3)", //
        "{d,e,f}");
    check("Take({a, b, c, d, e, f}, {2,4})", //
        "{b,c,d}");
    check("Take({{11, 12, 13}, {21, 22, 23},{31, 32, 33}}, 2, 2)", //
        "{{11,12},{21,22}}");
    check("Take({{11, 12, 13}, {21, 22, 23},a,{31, 32, 33}}, 3, 2)", //
        "Take({{11,12,13},{21,22,23},a,{31,32,33}},3,2)");
    check("Take({a, b, c, d, e, f}, None)", //
        "{}");
    check("Take({1, 2, 3, 4, 5}, {1, -1, 2})", //
        "{1,3,5}");

  }

  @Test
  public void testTakeLargest() {
    check("TakeLargest(Prime(Range(10)), 3)", //
        "{29,23,19}");
    check("TakeLargest({1, 3, 5, None, Indeterminate, Missing()}, 2)", //
        "{5,3}");
    check("TakeLargest(<|a -> 1, b -> 2, c -> 3, d -> 4|>, 2)", //
        "<|d->4,c->3|>");
    check("TakeLargest(3) @ {1, 3, 5, 4, 2}", //
        "{5,4,3}");
  }

  @Test
  public void testTakeLargestBy() {
    // check("TakeLargestBy({-1.5+I*0.8660254037844386,-1.5+I*(-0.8660254037844386),-1.0},Abs,3)",
    // //
    // "{-1.5+I*0.8660254037844386,-1.5+I*(-0.8660254037844386),-1.0}");
    checkNumeric("Abs(254/315+(14954373125000+I*7875000*Sqrt(9477810222))^(1/3)/31500+\n" //
        + "      1215035/63*2^(1/3)/(29908746250000+I*15750000*Sqrt(9477810222))^(1/3))//N", //
        "2.3710652374514223");
    check("TakeLargestBy({254/315+(14954373125000+I*7875000*Sqrt(9477810222))^(1/3)/31500+\n" //
        + "      1215035/63*2^(1/3)/(29908746250000+I*15750000*Sqrt(9477810222))^(1/3),254/315+\n" //
        + "      1215035/63*(-1+I*Sqrt(3))/(2^(2/3)*(29908746250000+I*15750000*Sqrt(9477810222))^(\n" //
        + "      1/3))+((-1-I*Sqrt(3))*(29908746250000+I*15750000*Sqrt(9477810222))^(1/3))/(63000*\n" //
        + "      2^(1/3))},Abs,2)", //
        "{254/315+(14954373125000+I*7875000*Sqrt(9477810222))^(1/3)/31500+1215035/63*2^(1/\n"//
            + "3)/(29908746250000+I*15750000*Sqrt(9477810222))^(1/3),254/315+1215035/63*(-1+I*Sqrt(\n"//
            + "3))/(2^(2/3)*(29908746250000+I*15750000*Sqrt(9477810222))^(1/3))+((-1-I*Sqrt(3))*(\n"//
            + "29908746250000+I*15750000*Sqrt(9477810222))^(1/3))/(63000*2^(1/3))}");


    // print message
    check("TakeLargestBy({2.0383668,3.96078,4.5547},Abs, 4)", //
        "TakeLargestBy({2.03837,3.96078,4.5547},Abs,4)");
    check("TakeLargestBy({1, 3, 5, None, Indeterminate, Missing(),a,b}, Abs, 2)", //
        "TakeLargestBy({1,3,5,None,Indeterminate,Missing(),a,b},Abs,2)");


    check("TakeLargestBy({2.0383668,3.96078,4.5547},Abs, 3)", //
        "{4.5547,3.96078,2.03837}");
    check("TakeLargestBy(Prime(Range(10)),Mod(#,7)&, 3)", //
        "{13,5,19}");
    check("TakeLargestBy(Prime(Range(10)),Mod(#,7)&, 3)", //
        "{13,5,19}");
    check("TakeLargestBy({-5, -2, 4, 3, 1, 9, 2, -4}, Abs, 4)", //
        "{9,-5,4,-4}");
    check("TakeLargestBy(<|a -> \"\", b -> \"xxx\", c -> \"xx\"|>, StringLength, 2)", //
        "<|b->xxx,c->xx|>");
  }

  @Test
  public void testTakeSmallest() {
    // print message
    check("TakeSmallest({1, 3, 5, None, Indeterminate, Missing(),a,b}, 2)", //
        "TakeSmallest({1,3,5,None,Indeterminate,Missing(),a,b},2)");
    check("TakeSmallest(Prime(Range(10)), 3)", //
        "{2,3,5}");
    check("TakeSmallest({1, 3, 5, None, Indeterminate, Missing()}, 2)", //
        "{1,3}");
    check("TakeSmallest(<|a -> 1, b -> 2, c -> 3, d -> 4|>, 2)", //
        "<|a->1,b->2|>");
    check("TakeSmallest(3) @ {1, 3, 5, 4, 2}", //
        "{1,2,3}");
  }

  @Test
  public void testTakeSmallestBy() {
    check("TakeSmallestBy({254/315+(14954373125000+I*7875000*Sqrt(9477810222))^(1/3)/31500+\n" //
        + "      1215035/63*2^(1/3)/(29908746250000+I*15750000*Sqrt(9477810222))^(1/3),254/315+\n" //
        + "      1215035/63*(-1+I*Sqrt(3))/(2^(2/3)*(29908746250000+I*15750000*Sqrt(9477810222))^(\n" //
        + "      1/3))+((-1-I*Sqrt(3))*(29908746250000+I*15750000*Sqrt(9477810222))^(1/3))/(63000*\n" //
        + "      2^(1/3))},Abs,2)", //
        "{254/315+1215035/63*(-1+I*Sqrt(3))/(2^(2/3)*(29908746250000+I*15750000*Sqrt(\n"
            + "9477810222))^(1/3))+((-1-I*Sqrt(3))*(29908746250000+I*15750000*Sqrt(9477810222))^(\n"
            + "1/3))/(63000*2^(1/3)),254/315+(14954373125000+I*7875000*Sqrt(9477810222))^(1/3)/\n"
            + "31500+1215035/63*2^(1/3)/(29908746250000+I*15750000*Sqrt(9477810222))^(1/3)}");

    // print message
    check("TakeSmallestBy({1, 3, 5, None, Indeterminate, Missing(),a,b}, Abs, 2)", //
        "TakeSmallestBy({1,3,5,None,Indeterminate,Missing(),a,b},Abs,2)");
    check("TakeSmallestBy(Prime(Range(10)),Mod(#,7)&, 3)", //
        "{7,29,2}");
    check("TakeSmallestBy({-5, -2, 4, 3, 1, 9, 2, -4}, Abs, 4)", //
        "{1,-2,2,3}");
    check("TakeSmallestBy(<|a -> \"\", b -> \"xxx\", c -> \"xx\"|>, StringLength, 2)", //
        "<|a->,c->xx|>");
  }

  @Test
  public void testTakeWhile() {
    check("TakeWhile({2, 4, 6, 1, 2, 3}, EvenQ)", //
        "{2,4,6}");
    check("TakeWhile(h(1, 1, 2, 3, a, 8, 13, b, c, d), IntegerQ)", //
        "h(1,1,2,3)");

    check("TakeWhile({1, 1, 2, 3, 5, 8, 13, 21}, #1 < 10 &)", //
        "{1,1,2,3,5,8}");
    check("Take({1, 1, 2, 3, 5, 8, 13, 21}, LengthWhile({1, 1, 2, 3, 5, 8, 13, 21}, #1 < 10 &))", //
        "{1,1,2,3,5,8}");

    check("TakeWhile({1, 2, 3, 10, 5, 8, 42, 11}, # < 10 &)", //
        "{1,2,3}");

  }

  @Test
  public void testTally() {
    check("Tally({{a, b}, {w, x, y, z}, E, {w, x, y, z}, E}, Head(#1) === Head(#2) &)", //
        "{{{a,b},3},{E,2}}");
    check("Tally({a,a,b,a,c,b,a})", //
        "{{a,4},{b,2},{c,1}}");
    check("Tally({b,a,b,a,c,b,a})", //
        "{{b,3},{a,3},{c,1}}");
    check("Tally({{a, b}, {w, x, y, z}, E, {w, x, y, z}, E})", //
        "{{{a,b},1},{{w,x,y,z},2},{E,2}}");

    check("str=\"The quick brown fox jumps over the lazy dog\";", //
        "");
    check("Tally(Characters(str)) // InputForm", //
        "{{\"T\",1},{\"h\",2},{\"e\",3},{\" \",8},{\"q\",1},{\"u\",2},{\"i\",1},{\"c\",1},{\"k\",1},{\"b\",1},{\"r\",2},{\"o\",4},{\"w\",1},{\"n\",1},{\"f\",1},{\"x\",1},{\"j\",1},{\"m\",1},{\"p\",1},{\"s\",1},{\"v\",1},{\"t\",1},{\"l\",1},{\"a\",1},{\"z\",1},{\"y\",1},{\"d\",1},{\"g\",1}}");

    check("Sort(Tally({b,a,c,b,a,c,b,a}))", //
        "{{a,3},{b,3},{c,2}}");
    check("Map({#, Count({b, a, c, b, a, c, b, a}, #)} &,Union({b, a, c, b, a, c, b, a}))", //
        "{{a,3},{b,3},{c,2}}");
    check("Counts({b, a, c, b, a, c, b, a})", //
        "<|a->3,b->3,c->2|>");

    check("tb(list_, f_) := Tally(list, f(#) === f(#2) &)", //
        "");
    check("tb({1, 2, 3, 2, 1, 1}, EvenQ)", //
        "{{1,4},{2,2}}");

    // check("Tally(RandomInteger(10, 100))", //
    // "{{1,13},{7,11},{9,6},{0,12},{2,8},{8,7},{10,8},{4,6},{6,10},{3,11},{5,8}}");

  }

  @Test
  public void testTagSet() {
    // TagSet: Argument 42 at position 1 is expected to be a symbol.
    check("42 /: g(h(x)) = 3", //
        "42/:g(h(x))/:3");
    // TagSet: Cannot assign to raw object 42.
    check("zzz /: 42 = 40+2", //
        "42");
    check("x /: g(h(x)) = 3", //
        "3");
    check("f/:format(f)=0", //
        "0");
    check("f/:format(f)/:0", //
        "Syntax error in line: 1 - Operator: '/:' not created properly (no grouping defined)\n" + //
            "f/:format(f)/:0\n" + //
            "             ^");
    check("f/: Format(f) = \"TagSet test\"", //
        "TagSet test");
    check("Format(f)", //
        "TagSet test");
  }

  @Test
  public void testTagSetDelayed() {
    // TagSetDelayed: Argument 42 at position 1 is expected to be a symbol.
    check("42 /: f(a,g,z_)/;True := {z}", //
        "TagSetDelayed(42,f(a,g,z_)/;True,{z})");

    check("g /: f(a,g,z_)/;True := {z}", //
        "");
    check("f(a,g,test)", //
        "{test}");

    check("g /: f(g(x_)) := fg(x)", //
        "");
    check("{f(g(2)) , f(h(2)) }", //
        "{fg(2),f(h(2))}");
  }

  @Test
  public void testTagSetDelayed02() {
    check("TagSetDelayed(g,0,\"TagSetDelayed test\")", //
        "");
    check("g/: Format(a_,g(x)) := \"TagSetDelayed test\"", //
        "");
    // check("Definition(g)", //
    // "TagSet test");
    check("Format(f,g(x))", //
        "TagSetDelayed test");
  }

  @Test
  public void testTan() {

    // check("Tan(8/15*Pi)", //
    // "-Cot(Pi/30)");
    check("Tan(Pi / 2.0)", //
        "1.63312*10^16");
    // TODO
    // check("Tan(z-Pi/3)", //
    // "-Cot(Pi/6+z)");
    check("Tan(e-Pi/2+f*x)", //
        "-Cot(e+f*x)");
    check("Tan(e+Pi/2+f*x)", //
        "-Cot(e+f*x)");
    check("Tan(Pi/2+Pi*n)", //
        "-Cot(n*Pi)");

    check("Tan(ArcSin(x))", //
        "x/Sqrt(1-x^2)");
    check("Tan(ArcCos(x))", //
        "Sqrt(1-x^2)/x");
    check("Tan(ArcTan(x))", //
        "x");
    check("Tan(ArcCot(x))", //
        "1/x");
    check("Tan(ArcCsc(x))", //
        "1/(Sqrt(1-1/x^2)*x)");
    check("Tan(ArcSec(x))", //
        "Sqrt(1-1/x^2)*x");

    check("Tan(0)", //
        "0");
    check("Tan(Pi / 2)", //
        "ComplexInfinity");
    checkNumeric("Tan(0.5*Pi)", //
        "1.633123935319537E16");

    check("Tan(Pi/2)", //
        "ComplexInfinity");
    check("Tan(1/6*Pi)", //
        "1/Sqrt(3)");
    check("Tan(Pi)", //
        "0");
    check("Tan(z+Pi)", //
        "Tan(z)");
    check("Tan(z+42*Pi)", //
        "Tan(z)");
    check("Tan(z+42*a*Pi)", //
        "Tan(42*a*Pi+z)");
    check("Tan(z+1/2*Pi)", //
        "-Cot(z)");
    check("Tan(Pi)", //
        "0");
    check("Tan(33*Pi)", //
        "0");
    check("Tan(z+Pi)", //
        "Tan(z)");
    check("Tan(z+42*Pi)", //
        "Tan(z)");
    check("Tan(x+y+z+43*Pi)", //
        "Tan(x+y+z)");
    check("Tan(z+42*a*Pi)", //
        "Tan(42*a*Pi+z)");
    check("Tan(z+4/3*Pi)", //
        "Tan(Pi/3+z)");
  }

  @Test
  public void testTanh() {
    check("Tanh(0)", //
        "0");
  }

  @Test
  public void testTautologyQ() {
    // https://en.wikipedia.org/wiki/Tautology_(logic)
    check("TautologyQ(a || !a)", //
        "True");

    check("TautologyQ((a || b) && (! a || ! b))", //
        "False");
    check("TautologyQ((a || b) || (! a && ! b))", //
        "True");
    check("TautologyQ((a || b) && (! a || ! b), {a, b})", //
        "False");
    check("TautologyQ((a || b) || (! a && ! b), {a, b})", //
        "True");
  }

  @Test
  public void testTaylor() {
    check("Taylor(f(x),{x,a,2})", //
        "f(a)+(-a+x)*f'(a)+1/2*(-a+x)^2*f''(a)");
    check("Taylor(ArcSin(x),{x,0,10})", //
        "x+x^3/6+3/40*x^5+5/112*x^7+35/1152*x^9");
    check("Limit(ArcSin(x)/x,x->0)", //
        "1");
    check("(-0^2+1)^(-1/2)", //
        "1");
  }

  @Test
  public void testTextString() {
    check("TextString(10) // InputForm", //
        "\"10\"");
  }

  @Test
  public void testThread() {
    check("Thread(Tuples({0, 1}, 2) -> {a, b, c, d})", //
        "{{0,0}->a,{0,1}->b,{1,0}->c,{1,1}->d}");
    check("Thread(f({a, b, c},{d,e}))", //
        "f({a,b,c},{d,e})");
    check("Thread(f({}))", //
        "{}");
    check("Thread(x^2)", //
        "x^2");
    check("Thread(f(x^2))", //
        "f(x^2)");

    check("Thread(f({a, b, c}))", //
        "{f(a),f(b),f(c)}");
    check("Thread(f({a, b, c}, {x, y, z}))", //
        "{f(a,x),f(b,y),f(c,z)}");
    check("Thread(Log(x == y), Equal)", //
        "Log(x)==Log(y)");

    check("Thread(f({a, b, c}))", //
        "{f(a),f(b),f(c)}");
    check("Thread(f({a, b, c}, t))", //
        "{f(a,t),f(b,t),f(c,t)}");
    check("Thread(f(a + b + c), Plus)", //
        "f(a)+f(b)+f(c)");
    check("{a, b, c} + {d, e, f} + g", //
        "{a+d+g,b+e+g,c+f+g}");
    check("Thread(f({a, b, c}, h, {x, y, z}))", //
        "{f(a,h,x),f(b,h,y),f(c,h,z)}");
  }

  @Test
  public void testThreeJSymbol() {
    check("ThreeJSymbol({2, 0}, {6, 0}, {4, 0})", //
        "Sqrt(5/143)");

    check("ThreeJSymbol({3/2, -3/2}, {3/2, 3/2}, {1, 0})", //
        "Sqrt(3/5)/2");

    check("ThreeJSymbol({5, 0}, {4, 0}, {1, 0})", //
        "-Sqrt(5/11)/3");
    check("ThreeJSymbol({6, 0}, {4, 0}, {2, 0})", //
        "Sqrt(5/143)");
    check("ThreeJSymbol({2, 1}, {2, 2}, {4, -3})", //
        "-1/(3*Sqrt(2))");
    check("ThreeJSymbol({1/2, -1/2}, {1/2, -1/2}, {1, 1})", //
        "-1/Sqrt(3)");


    check("{j1, j2} = {3, 2};\n" //
        + "Table(Sum(\n" //
        + "  If(Abs(m1 + m2) > j || Abs(m1 + m2) > ji, 0, \n" //
        + "   Sqrt((2 j + 1) (2 ji + 1))*ThreeJSymbol({j1, m1}, {j2, \n" //
        + "      m2}, {j, -(m1 + m2)})*ThreeJSymbol({j1, m1}, {j2, \n" //
        + "      m2}, {ji, -(m1 + m2)})), {m1, -j1, j1}, {m2, -j2, j2}), {j, \n" //
        + "  Abs(j1 - j2), j1 + j2}, {ji, Abs(j1 - j2), j1 + j2})", //
        "{{3,0,0,0,0},{0,5,0,0,0},{0,0,7,0,0},{0,0,0,9,0},{0,0,0,0,11}}");
  }

  @Test
  public void testThrough() {


    check("s1 = {\"t\", \"o\", \"d\", \"a\", \"y\"}", //
        "{t,o,d,a,y}");
    check("Through({StringJoin, Length}[#])& @ s1", //
        "{today,5}");


    check("s2 = {{\"h\", \"e\", \"l\", \"l\", \"o\"}, {\"d\", \"a\", \"y\"}}", //
        "{{h,e,l,l,o},{d,a,y}}");
    check("Thread@ MapThread(# /@ s2 &, {{StringJoin, Length}})", //
        "{{hello,5},{day,3}}");
    check("{StringJoin(##), Length({##})} & @@@ s2", //
        "{{hello,5},{day,3}}");
    check("Through({StringJoin, Length}({##})) & @@@ s2", //
        "{{hello,5},{day,3}}");

    check("Through(p(f, g)[])", //
        "p(f(),g())");
    check("Through(p(f,g)[x])", //
        "p(f(x),g(x))");
    check("Through(p(f,g)[])", //
        "p(f(),g())");
    check("Through(f()[x])", //
        "f()");
    check("Through(p(f,g))", //
        "p(f,g)");

    check("Through(p(f,g)[x,y])", //
        "p(f(x,y),g(x,y))");
    check("Through(f(g)[x])", //
        "f(g(x))");
    check("NestList(Through, f(a)[b][c][d], 3)", //
        "{f(a)[b][c][d],f(a)[b][c(d)],f(a)[b(c(d))],f(a(b(c(d))))}");
    check("Through( ((D(#, x) &) + (D(#, x, x) &))[f(x)] )", //
        "f'(x)+f''(x)");
    check("Through((f*g)[x,y],Plus)", //
        "(f*g)[x,y]");
    check("Through((f+g+h)[x,y],Plus)", //
        "f(x,y)+g(x,y)+h(x,y)");
  }

  @Test
  public void testTimeConstrained() {
    if (!Config.TIMECONSTRAINED_NO_THREAD) {
      // Config.FUZZ_TESTING = true;
      try {
        check("TimeConstrained( Beta(I*1/2,1.5707963267948966,2147483647), 2)", //
            "$Aborted");

        check("TimeConstrained(Do(i^2, {i, 10000000}), 1)", //
            "$Aborted");
        check("TimeConstrained(Pause(1); t=TimeRemaining(); Print(t);t>1&&Head(t)==Real, 10)", //
            "True");
      } finally {
        // Config.FUZZ_TESTING = false;
      }
    }
  }

  @Test
  public void testTimeRemaining() {
    if (!Config.TIMECONSTRAINED_NO_THREAD) {
      //
      try {
        check("tc=TimeConstrained(TimeConstrained(TimeRemaining(), 2), 5);tc<=2.0&&tc>=1.9", //
            "True");
        check("tc=TimeConstrained(TimeConstrained(TimeRemaining(), 5), 2);tc<=2.0&&tc>=1.9", //
            "True");
        check(
            "tc=TimeConstrained(Pause(2); TimeConstrained(TimeRemaining(), 4), 5);tc<=3.0&&tc>=2.9", //
            "True");
      } finally {
        //
      }
    }
  }

  @Test
  public void testTimeObject() {
    // Current time
    // check("TimeObject()", //
    // "17:38:38");

    check("TimeObject({10,45})", //
        "10:45:00");
    check("TimeObject({11,11,11})", //
        "11:11:11");
    check("TimeObject({03,02,01})", //
        "03:02:01");
  }

  @Test
  public void testTimes() {
    check("0.0*Sin(#)", //
        "0");
    check("8.88178*10^-16 * Binomial(50.0, #)", //
        "8.88178*10^-16*Binomial(50.0,#1)");
    check("0.5^#1*(-0.5+1)^(50.0-#1)", //
        "8.88178*10^-16");
    check("0.5^27.0*(-0.5+1)^(50.0-27.0)", //
        "8.88178*10^-16");

    check("Times(Sqrt[2/Pi]*Sqrt[Pi])", //
        "Sqrt(2)");
    check("Times(I, 1/2) // FullForm", //
        "Complex(0,Rational(1,2))");
    check("Head(Times(I, 1/2))", //
        "Complex");
    // check("(Sqrt(1/(2*Sqrt(10)))*Sqrt(-2+(2+Sqrt(10))*x^2)*Sqrt((2-(2-Sqrt(10))*x^2)/(2-(2+Sqrt(\n"
    // + "10))*x^2))*EllipticF(ArcSin(x/(Sqrt(2)*Sqrt(1/(4*Sqrt(10)))*Sqrt(-2+(2+Sqrt(10))*x^\n"
    // + "2))),1/10*(5+Sqrt(10))))/(Sqrt(2)*Sqrt(-2+4*x^2+3*x^4)*Sqrt(1/(2-(2+Sqrt(10))*x^\n" +
    // "2)))", //
    // "(Sqrt(1/(2*Sqrt(10)))*Sqrt(-2+(2+Sqrt(10))*x^2)*Sqrt((2+(-2+Sqrt(10))*x^2)/(2-(2+Sqrt(\n" +
    // "10))*x^2))*EllipticF(ArcSin(x/(Sqrt(2)*Sqrt(1/(4*Sqrt(10)))*Sqrt(-2+(2+Sqrt(10))*x^\n" +
    // "2))),1/10*(5+Sqrt(10))))/(Sqrt(2)*Sqrt(-2+4*x^2+3*x^4)*Sqrt(1/(2-(2+Sqrt(10))*x^\n" +
    // "2)))");

    check("Sin(0.1851851851851852*Pi*x)", //
        "Sin(0.581776*x)");

    check("-a*(2-x)", //
        "a*(-2+x)");
    check("False*Log(x+y)", //
        "False*Log(x+y)");
    check("Csch(x)^3 * Sinh(x)^(-2)", //
        "Csch(x)^5");
    check("Csch(x)^3 * Sinh(x)^7", //
        "Sinh(x)^4");

    check("2*4^(1+p)", //
        "2^(3+2*p)");
    check("(2^(3/4)*x*7^(-15/4))", //
        "1/343*(2/7)^(3/4)*x");

    check("125*2^(2+3*b)", //
        "125*2^(2+3*b)");
    check("(1/2)*4^(1+p)", //
        "2^(1+2*p)");

    // same as in MMA
    check("-(1/3)*(2+n)", //
        "1/3*(-2-n)");
    check("-(2+n)", //
        "-2-n");
    check("-3*(2+n)", //
        "-3*(2+n)");
    check("2^(3+k)*4^(1+p)", //
        "2^(5+k+2*p)");
    check("2*2^(1+p)", //
        "2^(2+p)");
    check("(1/2)*4^(1+p)", //
        "2^(1+2*p)");
    check("-(-b*c+a*d)*n", //
        "(b*c-a*d)*n");
    check("5/7*Sqrt(7/6)", //
        "5/Sqrt(42)");
    check("(Sqrt(3)*x)/Sqrt(2)", //
        "Sqrt(3/2)*x");
    check("1/(sq(a)*sq(b))//FullForm", //
        "Times(Power(sq(a), -1), Power(sq(b), -1))");
    check("(-Infinity)/Sin(-2)", //
        "Infinity");
    check("(-Infinity)/(-2)", //
        "Infinity");
    check("(-Infinity)/Log(2)", //
        "-Infinity");
    check("(-Infinity)/2", //
        "-Infinity");

    // github #35
    check(" y^2*y^(-0.6666) ", //
        "y^1.3334");
    check(" y*y^(-0.6666) ", //
        "y^0.3334");

    check("x* y*y^(-0.6666) *z^(-1)", //
        "(x*y^0.3334)/z");
    check("5/7*Sqrt(7/6)", //
        "5/Sqrt(42)");

    check("Hold((-1)^(a) (b)) // FullForm", //
        "Hold(Times(Power(-1, a), b))");

    check("Sqrt(1/2)*(1+x)", //
        "(1+x)/Sqrt(2)");
    check("((5/21)^(2/3)*(7/6)^(2/7))  ", //
        "5^(2/3)/(2^(2/7)*3^(20/21)*7^(8/21))");
    check("x*y/(y*z)", //
        "x/z");
    check("x*y/(y^3*z)", //
        "x/(y^2*z)");
    check("x*y/(y^(2/3)*z)", //
        "(x*y^(1/3))/z");
    check("x*y/(y^(0.6666)*z) ", //
        "(x*y^0.3334)/z");
    check("N(Pi, 30) * I", //
        "I*3.14159265358979323846264338327");
    check("N(I*Pi, 30)", //
        "I*3.14159265358979323846264338327");
    check("N(Pi * E, 30)", //
        "8.53973422267356706546355086954");
    check("N(Pi, 30) * N(E, 30)", //
        "8.53973422267356706546355086954");
    check("N(Pi, 30) * E // Precision", //
        "30");
    check("N(Pi, 30) * E", //
        "8.53973422267356706546355086954");

    check("Floor(Log(7,1024))", //
        "3");
    check("10*2", //
        "20");
    check("a*a", //
        "a^2");
    check("x ^ 10 * x ^ -2", //
        "x^8");
    check("{1, 2, 3} * 4", //
        "{4,8,12}");
    check("Times @@ {1, 2, 3, 4}", //
        "24");
    check("IntegerLength(Times@@Range(100))", //
        "158");
    check("a /. n_. * x_ :> {n, x} ", //
        "{1,a}");
    check("-a*b // FullForm", //
        "Times(-1, a, b)");
    check("-(x - 2/3)", //
        "2/3-x");
    check("-(h/2) // FullForm", //
        "Times(Rational(-1,2), h)");
    check("x / x", //
        "1");
    check("2*x^2 / x^2", //
        "2");
    checkNumeric("3.*Pi", //
        "9.42477796076938");
    check("Head(3 * I) ", //
        "Complex");
    check("Head(Pi * I)", //
        "Times");
    checkNumeric("-2.123456789 * x", //
        "-2.123456789*x");
    checkNumeric("-2.123456789 * I", //
        "I*(-2.123456789)");

    // issue #137
    check("12*2^x*3^y", //
        "2^(2+x)*3^(1+y)");
    check("8*2^x", //
        "2^(3+x)");
    check("12*2^x", //
        "3*2^(2+x)");

    check("-Infinity", //
        "-Infinity");
    check("Times(I*Sqrt(2), I*Sqrt(3))", //
        "-Sqrt(6)");
    check("Sin(x)^(-2)/Tan(x)", //
        "Cot(x)*Csc(x)^2");
    check("Sin(x)/Tan(x)", //
        "Cos(x)");
    check("Sin(x)^2/Tan(x)^3", //
        "Cos(x)^2*Cot(x)");
    check("Sin(x)^3/Tan(x)^2", //
        "Cos(x)^2*Sin(x)");
    check("Sin(x)^2/Tan(x)", //
        "Cos(x)*Sin(x)");
    check("Sin(x)/Tan(x)^2", //
        "Cos(x)*Cot(x)");

    check("Sin(x)^(-2)", //
        "Csc(x)^2");
    check("Sin(x)/Tan(x)^(-2)", //
        "Sin(x)*Tan(x)^2");
    check("Sin(x)/Cos(x)", //
        "Tan(x)");
    check("Cos(x)*Tan(x)", //
        "Sin(x)");
    check("Cos(x)/Sin(x)", //
        "Cot(x)");
    check("Tan(x)/Sin(x)", //
        "Sec(x)");

    check("Times()", //
        "1");
    // OutputForm: I*Infinity is DirectedInfinity[I]
    check("I*Infinity", //
        "I*Infinity");

    check("Gamma(a)*Gamma(1-a)", //
        "Pi*Csc(a*Pi)");
    check("Gamma(a)^3*Gamma(1-a)^3", //
        "Pi^3*Csc(a*Pi)^3");
    check("Gamma(a)^3*Gamma(1-a)^2", //
        "Pi^2*Csc(a*Pi)^2*Gamma(a)");
  }

  @Test
  public void testTimesBy() {
    check("a = 10", //
        "10");
    check("a *= 2", //
        "20");
    check("a", //
        "20");

    check("index={1,2,3,4,5,6,7,8,9}", //
        "{1,2,3,4,5,6,7,8,9}");
    check("index[[3]]*=y", //
        "3*y");
    check("index", //
        "{1,2,3*y,4,5,6,7,8,9}");
  }

  @Test
  public void testTimeValue() {
    check("TimeValue(I*1/2,-I,0)", //
        "I*1/2");
    check(" TimeValue(-0.8+I*1.2,-5,1009)", //
        "ComplexInfinity");
    check("N(TimeValue((-8/10)+I*(12/10),-5,1009),20)", //
        "2.4078045838557934240*10^607+I*(-3.611706875783690136*10^607)");
    check("TimeValue(Annuity(100, 12), .01, 0)", //
        "1125.508");
    check("TimeValue(Annuity(100, 12), EffectiveInterest(.01, 0.25), 12)", //
        "1268.515");
    check("TimeValue(AnnuityDue(100, 12), 0.1, 0)", //
        "749.5061");

    check("TimeValue(Annuity(500,36,q), b, c)", //
        "(500*(-1+((1+b)^q)^(36/q)))/((-1+(1+b)^q)*((1+b)^q)^(36/q-c/q))");
    check("TimeValue(AnnuityDue(500,36,q), b, c)", //
        "(500*(-1+((1+b)^q)^(36/q))*((1+b)^q)^(1-36/q+c/q))/(-1+(1+b)^q)");
    check("TimeValue(Annuity(100, 12), 6/100, 0)", //
        "411863798761210257735000/491258904256726154641");
    check("TimeValue(a,b,c)", //
        "a*(1+b)^c");
    check("TimeValue(100, EffectiveInterest(.06, 1/12), 10)", //
        "181.9397");
    check("TimeValue(Annuity(1000, 10), .06, 0)", //
        "7360.087");
    check("TimeValue(Annuity(1000, 5), EffectiveInterest(.08, 1/4), 5)", //
        "5895.119");
    check("TimeValue(AnnuityDue(100, 12), 6/100, 0)", //
        "8237275975224205154700/9269035929372191597");
    check("TimeValue(AnnuityDue(1000, 10), .06, 0)", //
        "7801.692");
    check("TimeValue(AnnuityDue(1000, 5), EffectiveInterest(.08, 1/4), 5)", //
        "6381.066");
  }

  @Test
  public void testTogether() {
    check("Together((1/x-1/3)^3/(x-3)^2)", //
        "(3-x)/(27*x^3)");
    check("Together((1/x-1/3)/(x-3))", //
        "-1/(3*x)");
    check("Together(1/2+(1/3+I*1/2)*Sqrt(3))", //
        "1/6*(3+(2+I*3)*Sqrt(3))");
    check("Together(1/2+I*1/2*Sqrt(3))", //
        "1/2*(1+I*Sqrt(3))");
    check("Together(1/2*x+1/2*y+1/2*z)", //
        "1/2*(x+y+z)");
    check("Together(1/2*x+1/6*y+1/4*z)", //
        "1/12*(6*x+2*y+3*z)");

    check("Together( 8-8*Cos(2/7*Pi))", //
        "8*(1-Cos(2/7*Pi))");
    check("Together(6/5*c*f+6/5*b*g+2/5*c*f*m+1/5*b*g*m+2/5*c*f*n+3/5*b*g*n)", //
        "1/5*(6*c*f+6*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n)");

    check("Together(Sqrt(2)+SparseArray({{0,1},{1,0}}))", //
        "SparseArray(Number of elements: 4 Dimensions: {2,2} Default value: 0)");

    check("Together(1+(b*x)/a)", //
        "(a+b*x)/a");
    // TODO ((1+x)*f(x))/x^2
    check("f(x)/x+f(x)/x^2//Together", //
        "(f(x)+x*f(x))/x^2");
    check("together(x^2/(x^2 - 1) + x/(x^2 - 1))", //
        "x/(-1+x)");
    check("Together((2 + 2*x)/(2*Sqrt(2)))", //
        "(1+x)/Sqrt(2)");

    // check("Together(-(5*c*f+5*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n+30*c*g*x+10*c*g*m*x+15*c*g*n*x)^5/(3125*c^4*g^4*(6+2*m+3*n)^4))",
    // //
    // "");

    check("Together((x+2*Sqrt(x)+1)/(1+Sqrt(x)))", //
        "1+Sqrt(x)");
    check("Together(-a/(-(b-a*c)))", //
        "a/(b-a*c)");
    check("Together(Simplify(Together(-a/(-(b-a*c)))))", //
        "a/(b-a*c)");
    check("Together(1/2+I/3 + 3*a^(-1))", //
        "(18+(3+I*2)*a)/(6*a)");
    check("Together(1/2 + 3/a )", //
        "(6+a)/(2*a)");
    check("Together[(2 + 2*x)/(2*Sqrt[2])]", //
        "(1+x)/Sqrt(2)");

    check("Together((1+a/(c+d)+b/(c+d))/(a+b))", //
        "(a+b+c+d)/((a+b)*(c+d))");

    check("Together((a+b*x+c*x^2)^13/13)", //
        "(a+b*x+c*x^2)^13/13");
    check("Together(1/(a + b) + 1/(c + d) - a)", //
        "(a+b+c-a^2*c-a*b*c+d-a^2*d-a*b*d)/((a+b)*(c+d))");
    check("Together(1/a+1/b)", //
        "(a+b)/(a*b)");
    check("Together(1/a+1/b+c)", //
        "(a+b+a*b*c)/(a*b)");
    check("Together(1/c+d)", //
        "(1+c*d)/c");
    check("Together(1/2+a)", //
        "1/2*(1+2*a)");
    check("Together((1+a/(c+d)+b/(c+d))/(a+b))", //
        "(a+b+c+d)/((a+b)*(c+d))");
    check("Together(1/a+1/b+c+d)", //
        "(a+b+a*b*c+a*b*d)/(a*b)");
    check("Together(2*a+2*b)", //
        "2*(a+b)");

    check("Together(-(a^2-c^2)/(a*b-b*c))", //
        "-(a+c)/b");

    check("Together(1/Sqrt(1+1/x) )", //
        "Sqrt(x/(1+x))");
    check("Together(1/Sqrt(1+1/x)+(1+1/x)^(3/2))", //
        "(Sqrt((1+x)/x)+x*Sqrt((1+x)/x)+x*Sqrt(x/(1+x)))/x");

    check("Together(1/(2*Sqrt(3))+Sqrt(3)/2)", //
        "2/Sqrt(3)");
    check("Together(1/2-Sqrt(5)/2+2/(1+Sqrt(5)))", //
        "0");
    // check("Together(1/Sqrt(1+1/x) + (1+1/x)^(3/2) )", " ");

    check("Together(1+1/(1+1/x))", //
        "(1+2*x)/(1+x)");

    check("Together(a/b+c/d)", //
        "(b*c+a*d)/(b*d)");
    // TODO return {x (2 + y) / (1 + y) ^ 2}
    check("Together({x / (y+1) + x / (y+1)^2})", //
        "{(2*x+x*y)/(1+2*y+y^2)}");

    check("Together(a / c + b / c)", //
        "(a+b)/c");

    check("Together(f(a / c + b / c))", //
        "f(a/c+b/c)");

    check("f(x)/x+f(x)/x^2//Together", //
        "(f(x)+x*f(x))/x^2");

    check("Together(1 < 1/x + 1/(1 + x) < 2)", //
        "1<(1+2*x)/(x*(1+x))<2");
    check("Together(1/(1+1/(1+1/a)))", //
        "(1+a)/(1+2*a)");
    check("Together(1/(1+1/(1+1/(1+a))))", //
        "(2+a)/(3+2*a)");
    check("ExpandAll(a*b)", //
        "a*b");
    check("ExpandAll(a*b^(-1))", //
        "a/b");
    check("(a*b)^(-1)", //
        "1/(a*b)");
    check("Together(a/b + c/d)", //
        "(b*c+a*d)/(b*d)");
    check("Together((-7*a^(-1)*b+1)*(-a^(-1)*b-1)^(-1))", //
        "(a-7*b)/(-a-b)");
    check("Together(a*b^(-2)+c*d^(-3))", //
        "(b^2*c+a*d^3)/(b^2*d^3)");
    check("Together(-a*b^(-2)-c*d^(-3))", //
        "(-b^2*c-a*d^3)/(b^2*d^3)");
    check("Together((-8)*a^(-1)*(-a^(-1)*b-1)^(-1))", //
        "-8/(-a-b)");

    check(
        "Together((2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1)-4*x*(2*x^3-4*x+5)*(3*x^2+2)^(-1)+5*(2*x^3-4*x+\n"
            + "5)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)*(3*x^2+2)^(-1)+x^2-2)", //
        "(17-60*x+12*x^2-10*x^3-6*x^4+x^6)/(2+3*x^2)^2");
    check(
        "Together((4*x^6-8*x^4+10*x^3)*(3*x^2+2)^(-1)+(-8*x^4+16*x^2-20*x)*(3*x^2+2)^(-1)+(10*x^3\n"
            + "-20*x+25)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)", //
        "(25-60*x+32*x^2-10*x^3-8*x^6)/(2+3*x^2)");
    check(
        "Together((2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1)-4*x*(2*x^3-4*x+5)*(3*x^2+2)^(-1)+5*(2*x^3-4*x+\n"
            + "5)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)*(x)^(-1)-2)", //
        "(25-64*x+32*x^2-16*x^3-8*x^6)/(x*(2+3*x^2))");
    check("Together(a + c/g)", //
        "(c+a*g)/g");
    check("Together(((7*b*a^(-1)-1)*(-b*a^(-1)-1)^(-1)+7)*a^(-1))", //
        "-8/(-a-b)");

    check("ExpandAll((x^2-1)*x^2+x*(x^2-1))", //
        "-x-x^2+x^3+x^4");
    check("together(x^2/(x^2 - 1) + x/(x^2 - 1))", //
        "x/(-1+x)");
    check("Together((1/3*x-1/6)*(x^2-x+1)^(-1))", //
        "(-1+2*x)/(6*(1-x+x^2))");
    check(
        "Together((-a^2*r^2*x-a*b*r*x-a*c*x-a^2*r^3-2*a*b*r^2-a*c*r-b^2*r-b*c)*((a*r^2+b*r)^2+2*c*(a*r^2+b*r)+c^2)^(-1))", //
        "(-b-a*r-a*x)/(c+b*r+a*r^2)");
    check("Together((-8)*a^(-1)*(-a^(-1)*b-1)^(-1))", //
        "-8/(-a-b)");

    check("Together(a/b + c/d)", //
        "(b*c+a*d)/(b*d)");
    check("Together((-a^(-1)*b-1))", //
        "(-a-b)/a");
    check("((-b-a)*a^(-1))^(-1)", //
        "a/(-a-b)");
    check("Together(1/x + 1/(x + 1) + 1/(x + 2) + 1/(x + 3))", //
        "(6+22*x+18*x^2+4*x^3)/(x*(1+x)*(2+x)*(3+x))");
  }

  @Test
  public void testTogetherIssue856() {
    // github issue #856
    check("tg=Simplify(1/(Sqrt(7) - 2 *Sqrt(2)) + 1/(Sqrt(7) + 2 *Sqrt(2))) ", //
        "-2*Sqrt(7)");
    check("tg=Together(1/(-2*Sqrt(2)+Sqrt(7))+1/(2*Sqrt(2)+Sqrt(7)))  ", //
        "-2*Sqrt(7)");
    check("tg=Together(1/(Sqrt(7) - 2 *Sqrt(2)) + 1/(Sqrt(7) + 2 *Sqrt(2))) ", //
        "-2*Sqrt(7)");
  }

  @Test
  public void testToExpression() {
    // TODO print syntax error to error stream
    check("ToExpression(\"1+2}\")", //
        "$Failed");
    check("ToExpression(\"1+2\")", //
        "3");
    check("ToExpression(\"{2, 3, 1}\", InputForm, Max)", //
        "3");
    check("ToExpression(\"2 3\", InputForm)", //
        "6");

    check("ToExpression(\"\\\\begin{matrix}\n" //
        + "1 & 2 \\\\\\\\\n" //
        + " 7 & 8\n" //
        + "\\\\end{matrix}\"" //
        + ", TeXForm)", //
        "{{1,2},{7,8}}");
    check("ToExpression(\"\\\\frac{x}{\\\\sqrt{5}}\", TeXForm)", //
        "x/Sqrt(5)");
    check("ToExpression(\"1 + 2 - x \\\\times 4 \\\\div 5\", TeXForm)", //
        "3-4/5*x");
  }

  @Test
  public void testToRadicals() {
    check("ToRadicals(Root((#^2 - 3*# - 1)&, 2))", //
        "3/2+Sqrt(13)/2");
    check("ToRadicals(Root((-3*#-1)&, 1))", //
        "-1/3");
    check("ToRadicals(Sin(Root((#^7-#^2-#+a)&, 1)))", //
        "Sin(Root(-#1-#1^2+#1^7+a&,1))");
    check("ToRadicals(Root((#^7-#^2-#+a)&, 1)+Root((#^6-#^2-#+a)&, 1))", //
        "Root(-#1-#1^2+#1^6+a&,1)+Root(-#1-#1^2+#1^7+a&,1)");
    check("ToRadicals(Root((#^3-#^2-#+a)&, 1))", //
        "1/3+4/3*2^(1/3)/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+(11+Sqrt(-256+(11-27*a)^2)-\n"
            + "27*a)^(1/3)/(3*2^(1/3))");
    check("ToRadicals(Root((#^3-#^2-#+a)&, 2))", //
        "1/3+4/3*(2^(1/3)*(-1/2-I*1/2*Sqrt(3)))/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+((-\n"
            + "1/2+I*1/2*Sqrt(3))*(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3))/(3*2^(1/3))");
    check("ToRadicals(Root((#^3-#^2-#+a)&, 3))", //
        "1/3+4/3*(2^(1/3)*(-1/2+I*1/2*Sqrt(3)))/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+((-\n"
            + "1/2-I*1/2*Sqrt(3))*(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3))/(3*2^(1/3))");
  }

  @Test
  public void testToString() {
    check("ToString(Sqrt(1-1/x^2))", //
        "Sqrt(1 - 1/x^2)");
    String expected = String.join("\n", //
        "       -1+Sin(x)^2 == -Cos(x)^2 ", //
        "     1+Sin(x)^2 == 1 + Sin(x)^2 ", //
        "       -1+Cos(x)^2 == -Sin(x)^2 ", //
        "     1+Cos(x)^2 == 1 + Cos(x)^2 ", //
        "   -1+Tan(x)^2 == -1 + Tan(x)^2 ", //
        "         1+Tan(x)^2 == Sec(x)^2 ", //
        "   -1+Cot(x)^2 == -1 + Cot(x)^2 ", //
        "         1+Cot(x)^2 == Csc(x)^2 ", //
        "        -1+Sec(x)^2 == Tan(x)^2 ", //
        "     1+Sec(x)^2 == 1 + Sec(x)^2 ", //
        "        -1+Csc(x)^2 == Cot(x)^2 ", //
        "     1+Csc(x)^2 == 1 + Csc(x)^2 ", //
        " -1+Sinh(x)^2 == -1 + Sinh(x)^2 ", //
        "       1+Sinh(x)^2 == Cosh(x)^2 ", //
        "      -1+Cosh(x)^2 == Sinh(x)^2 ", //
        "   1+Cosh(x)^2 == 1 + Cosh(x)^2 ", //
        "     -1+Tanh(x)^2 == -Sech(x)^2 ", //
        "   1+Tanh(x)^2 == 1 + Tanh(x)^2 ", //
        "      -1+Coth(x)^2 == Csch(x)^2 ", //
        "   1+Coth(x)^2 == 1 + Coth(x)^2 ", //
        "     -1+Sech(x)^2 == -Tanh(x)^2 ", //
        "   1+Sech(x)^2 == 1 + Sech(x)^2 ", //
        " -1+Csch(x)^2 == -1 + Csch(x)^2 ", //
        "       1+Csch(x)^2 == Coth(x)^2 ");
    check(
        "Outer((ToString(#2) <> \"+\" <> ToString(#1) <> \"(x)^2 == \" <> ToString( Simplify( #2 + (#1(x)^2))) )&," //
            + "{Sin,Cos,Tan,Cot,Sec,Csc,Sinh,Cosh,Tanh,Coth,Sech,Csch}," //
            + "{-1,+1}) //" //
            + "Flatten // TableForm ", //
        expected);
    expected = String.join("\n", //
        "   -1-f(x)-Sin(x)^2+y+z == -1 + y + z - f(x) - Sin(x)^2 ", //
        "         1-f(x)-Sin(x)^2+y+z == y + z + Cos(x)^2 - f(x) ", //
        "   -1-f(x)-Cos(x)^2+y+z == -1 + y + z - Cos(x)^2 - f(x) ", //
        "         1-f(x)-Cos(x)^2+y+z == y + z - f(x) + Sin(x)^2 ", //
        "        -1-f(x)-Tan(x)^2+y+z == y + z - f(x) - Sec(x)^2 ", //
        "     1-f(x)-Tan(x)^2+y+z == 1 + y + z - f(x) - Tan(x)^2 ", //
        "        -1-f(x)-Cot(x)^2+y+z == y + z - Csc(x)^2 - f(x) ", //
        "     1-f(x)-Cot(x)^2+y+z == 1 + y + z - Cot(x)^2 - f(x) ", //
        "   -1-f(x)-Sec(x)^2+y+z == -1 + y + z - f(x) - Sec(x)^2 ", //
        "         1-f(x)-Sec(x)^2+y+z == y + z - f(x) - Tan(x)^2 ", //
        "   -1-f(x)-Csc(x)^2+y+z == -1 + y + z - Csc(x)^2 - f(x) ", //
        "         1-f(x)-Csc(x)^2+y+z == y + z - Cot(x)^2 - f(x) ", //
        "      -1-f(x)-Sinh(x)^2+y+z == y + z - Cosh(x)^2 - f(x) ", //
        "   1-f(x)-Sinh(x)^2+y+z == 1 + y + z - f(x) - Sinh(x)^2 ", //
        " -1-f(x)-Cosh(x)^2+y+z == -1 + y + z - Cosh(x)^2 - f(x) ", //
        "       1-f(x)-Cosh(x)^2+y+z == y + z - f(x) - Sinh(x)^2 ", //
        " -1-f(x)-Tanh(x)^2+y+z == -1 + y + z - f(x) - Tanh(x)^2 ", //
        "       1-f(x)-Tanh(x)^2+y+z == y + z - f(x) + Sech(x)^2 ", //
        " -1-f(x)-Coth(x)^2+y+z == -1 + y + z - Coth(x)^2 - f(x) ", //
        "       1-f(x)-Coth(x)^2+y+z == y + z - Csch(x)^2 - f(x) ", //
        " -1-f(x)-Sech(x)^2+y+z == -1 + y + z - f(x) - Sech(x)^2 ", //
        "       1-f(x)-Sech(x)^2+y+z == y + z - f(x) + Tanh(x)^2 ", //
        "      -1-f(x)-Csch(x)^2+y+z == y + z - Coth(x)^2 - f(x) ", //
        "   1-f(x)-Csch(x)^2+y+z == 1 + y + z - Csch(x)^2 - f(x) ");
    check(
        "Outer((ToString(#2) <> \"-f(x)-\" <> ToString(#1) <> \"(x)^2+y+z == \" <> ToString( Simplify( #2 - f(x) - (#1(x)^2)+y+z)) )&," //
            + "{Sin,Cos,Tan,Cot,Sec,Csc,Sinh,Cosh,Tanh,Coth,Sech,Csch}," //
            + "{-1,+1}) //" //
            + "Flatten // TableForm ", //
        expected);

    expected = String.join("\n", //
        "                                  Sinh(ArcSinh(x)) == x ", //
        "           Sinh(ArcCosh(x)) == Sqrt(-1 + x)*Sqrt(1 + x) ", //
        "                    Sinh(ArcTanh(x)) == x/Sqrt(1 - x^2) ", //
        "       Sinh(ArcCoth(x)) == 1/(Sqrt(-1 + x)*Sqrt(1 + x)) ", //
        "       Sinh(ArcSech(x)) == Sqrt(-1 + 1/x)*Sqrt(1 + 1/x) ", //
        "                                Sinh(ArcCsch(x)) == 1/x ", //
        "                      Cosh(ArcSinh(x)) == Sqrt(1 + x^2) ", //
        "                                  Cosh(ArcCosh(x)) == x ", //
        "                    Cosh(ArcTanh(x)) == 1/Sqrt(1 - x^2) ", //
        "                  Cosh(ArcCoth(x)) == 1/Sqrt(1 - 1/x^2) ", //
        "                                Cosh(ArcSech(x)) == 1/x ", //
        "                    Cosh(ArcCsch(x)) == Sqrt(1 + 1/x^2) ", //
        "                    Tanh(ArcSinh(x)) == x/Sqrt(1 + x^2) ", //
        "       Tanh(ArcCosh(x)) == (Sqrt(-1 + x)*Sqrt(1 + x))/x ", //
        "                                  Tanh(ArcTanh(x)) == x ", //
        "                                Tanh(ArcCoth(x)) == 1/x ", //
        "     Tanh(ArcSech(x)) == Sqrt(-1 + 1/x)*Sqrt(1 + 1/x)*x ", //
        "              Tanh(ArcCsch(x)) == 1/(Sqrt(1 + 1/x^2)*x) ", //
        "                    Coth(ArcSinh(x)) == Sqrt(1 + x^2)/x ", //
        "       Coth(ArcCosh(x)) == x/(Sqrt(-1 + x)*Sqrt(1 + x)) ", //
        "                                Coth(ArcTanh(x)) == 1/x ", //
        "                                  Coth(ArcCoth(x)) == x ", //
        " Coth(ArcSech(x)) == 1/(Sqrt(-1 + 1/x)*Sqrt(1 + 1/x)*x) ", //
        "                  Coth(ArcCsch(x)) == Sqrt(1 + 1/x^2)*x ", //
        "                    Sech(ArcSinh(x)) == 1/Sqrt(1 + x^2) ", //
        "                                Sech(ArcCosh(x)) == 1/x ", //
        "                      Sech(ArcTanh(x)) == Sqrt(1 - x^2) ", //
        "       Sech(ArcCoth(x)) == (Sqrt(-1 + x)*Sqrt(1 + x))/x ", //
        "                                  Sech(ArcSech(x)) == x ", //
        "                  Sech(ArcCsch(x)) == 1/Sqrt(1 + 1/x^2) ", //
        "                                Csch(ArcSinh(x)) == 1/x ", //
        "       Csch(ArcCosh(x)) == 1/(Sqrt(-1 + x)*Sqrt(1 + x)) ", //
        "        Csch(ArcTanh(x)) == (Sqrt(1 - x)*Sqrt(1 + x))/x ", //
        "           Csch(ArcCoth(x)) == Sqrt(-1 + x)*Sqrt(1 + x) ", //
        "  Csch(ArcSech(x)) == x/((1 + x)*Sqrt((1 - x)/(1 + x))) ", //
        "                                  Csch(ArcCsch(x)) == x ");
    check(
        "Outer((ToString(#1) <> \"(\" <> ToString(#2) <> \"(x)) == \" <> ToString(InputForm(#1(#2(x)))))&," //
            + "{Sinh,Cosh,Tanh,Coth,Sech,Csch}," //
            + "{ArcSinh, ArcCosh, ArcTanh, ArcCoth, ArcSech, ArcCsch}) //" //
            + "Flatten // TableForm ", //
        expected);

    expected = String.join("\n", //
        "                     Sin(ArcSin(x)) == x ", //
        "         Sin(ArcCos(x)) == Sqrt(1 - x^2) ", //
        "       Sin(ArcTan(x)) == x/Sqrt(1 + x^2) ", //
        "       Sin(ArcCot(x)) == 1/Sqrt(1 + x^2) ", //
        "       Sin(ArcSec(x)) == Sqrt(1 - 1/x^2) ", //
        "                   Sin(ArcCsc(x)) == 1/x ", //
        "         Cos(ArcSin(x)) == Sqrt(1 - x^2) ", //
        "                     Cos(ArcCos(x)) == x ", //
        "       Cos(ArcTan(x)) == 1/Sqrt(1 + x^2) ", //
        "     Cos(ArcCot(x)) == 1/Sqrt(1 + 1/x^2) ", //
        "                   Cos(ArcSec(x)) == 1/x ", //
        "       Cos(ArcCsc(x)) == Sqrt(1 - 1/x^2) ", //
        "       Tan(ArcSin(x)) == x/Sqrt(1 - x^2) ", //
        "       Tan(ArcCos(x)) == Sqrt(1 - x^2)/x ", //
        "                     Tan(ArcTan(x)) == x ", //
        "                   Tan(ArcCot(x)) == 1/x ", //
        "     Tan(ArcSec(x)) == Sqrt(1 - 1/x^2)*x ", //
        " Tan(ArcCsc(x)) == 1/(Sqrt(1 - 1/x^2)*x) ", //
        "       Cot(ArcSin(x)) == Sqrt(1 - x^2)/x ", //
        "       Cot(ArcCos(x)) == x/Sqrt(1 - x^2) ", //
        "                   Cot(ArcTan(x)) == 1/x ", //
        "                     Cot(ArcCot(x)) == x ", //
        " Cot(ArcSec(x)) == 1/(Sqrt(1 - 1/x^2)*x) ", //
        "     Cot(ArcCsc(x)) == Sqrt(1 - 1/x^2)*x ", //
        "       Sec(ArcSin(x)) == 1/Sqrt(1 - x^2) ", //
        "                   Sec(ArcCos(x)) == 1/x ", //
        "         Sec(ArcTan(x)) == Sqrt(1 + x^2) ", //
        "       Sec(ArcCot(x)) == Sqrt(1 + x^2)/x ", //
        "                     Sec(ArcSec(x)) == x ", //
        "     Sec(ArcCsc(x)) == 1/Sqrt(1 - 1/x^2) ", //
        "                   Csc(ArcSin(x)) == 1/x ", //
        "       Csc(ArcCos(x)) == 1/Sqrt(1 - x^2) ", //
        "       Csc(ArcTan(x)) == Sqrt(1 + x^2)/x ", //
        "         Csc(ArcCot(x)) == Sqrt(1 + x^2) ", //
        "     Csc(ArcSec(x)) == 1/Sqrt(1 - 1/x^2) ", //
        "                     Csc(ArcCsc(x)) == x ");
    check(
        "Outer((ToString(#1) <> \"(\" <> ToString(#2) <> \"(x)) == \" <> ToString(InputForm(#1(#2(x)))))&," //
            + "{Sin,Cos,Tan,Cot,Sec,Csc}," //
            + "{ArcSin, ArcCos, ArcTan, ArcCot, ArcSec, ArcCsc}) //" //
            + "Flatten // TableForm ", //
        expected);
    check("ToString(InputForm(a+\"b\"))", //
        "\"b\" + a");
    check("ToString(InputForm(d/2+f(x)))", //
        "d/2 + f(x)");
    check("ToString(FullForm(d/2))", //
        "Times(Rational(1,2), d)");
  }

  @Test
  public void testTotal() {
    // TODO only add up elements inside List, Association, SparseArray,...
    // check("t = Array(Subscript(a, ##) &, {2, 3, 4})", //
    // "{{{Subscript(a,1,1,1),Subscript(a,1,1,2),Subscript(a,1,1,3),Subscript(a,1,1,4)},{Subscript(a,\n"
    // //
    // + "1,2,1),Subscript(a,1,2,2),Subscript(a,1,2,3),Subscript(a,1,2,4)},{Subscript(a,1,\n" //
    // + "3,1),Subscript(a,1,3,2),Subscript(a,1,3,3),Subscript(a,1,3,4)}},{{Subscript(a,2,\n" //
    // + "1,1),Subscript(a,2,1,2),Subscript(a,2,1,3),Subscript(a,2,1,4)},{Subscript(a,2,2,\n" //
    // +
    // "1),Subscript(a,2,2,2),Subscript(a,2,2,3),Subscript(a,2,2,4)},{Subscript(a,2,3,1),Subscript(a,\n"
    // //
    // + "2,3,2),Subscript(a,2,3,3),Subscript(a,2,3,4)}}}");
    // check("Total(t, {-1})", //
    // "");

    check("Total({x^2, 3*x^3, 1})", //
        "1+x^2+3*x^3");

    check("Total(f(1,2,1))", //
        "4");
    check("Total(Derivative(1, 2, 1))", //
        "4");

    check("Total( {{1, 2, 3}, {4, 5}, {6}})", //
        "Total({{1,2,3},{4,5},{6}})");
    check("Total( {{1, 2, 3}, {4, 5}, {6}}, Infinity)", //
        "21");
    check("Total( {{1, 2, 3}, {4, 5}, {6}}, {-1})", //
        "{6,9,6}");

    check("Total({1, 2, 3})", //
        "6");
    check("Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}})", //
        "{12,15,18}");
    check("Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}}, 2)", //
        "45");
    check("Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}}, {2})", //
        "{6,15,24}");

    check("Total({x^2, 3*x^3, 1},{1})", //
        "1+x^2+3*x^3");
    check("Total({x^2, 3*x^3, 1})", //
        "1+x^2+3*x^3");
    check("Total({{1,2,3},{4,5,6},{7,8,9}})", //
        "{12,15,18}");
    check("Total({{1,2,3},{4,5,6},{7,8,9}},{1})", //
        "{12,15,18}");
    // total the rows
    check("Total({{1,2,3},{4,5,6},{7,8,9}},{2})", //
        "{6,15,24}");
    check("Total({{1,2,3},{4,5,6},{7,8,9}},2)", //
        "45");

  }

  @Test
  public void testTreeForm() {
    check("TreeForm(a+b)", //
        "JSFormData(var nodes = new vis.DataSet([\n" + //
            "  {id: 1, label: 'Plus', level: 0}\n" + //
            ", {id: 2, label: 'a', level: 1}\n" + //
            ", {id: 3, label: 'b', level: 1}\n" + //
            "]);\n" + //
            "var edges = new vis.DataSet([\n" + //
            "  {from: 1, to: 2 , arrows: { to: { enabled: true, type: 'arrow'}}}\n" + //
            ", {from: 1, to: 3 , arrows: { to: { enabled: true, type: 'arrow'}}}\n" + //
            "]);\n" + //
            ",treeform)");
  }

  @Test
  public void testTrace() {
    check("Trace(u = 2; Do(u = u*u, {3}); u, Times)", //
        "{{{{u*u,2*2}},{{u*u,4*4}},{{u*u,16*16}}}}");
    check("x=5;Trace(Mod((3 + x)^2, x - 1))", //
        "{{{{x,5},3+5,8},8^2,64},{{x,5},-1+5,4},Mod(64,4),0}");
    check("Trace(u = 2; Do(u = u*u, {3}); u)", //
        "{{u=2,2},{{{{u,2},{u,2},2*2,4},4},{{{u,4},{u,4},4*4,16},16},{{{u,16},{u,16},16*\n"
            + "16,256},256},Null},{u,256},256}");
  }

  @Test
  public void testTrigExpand() {
    // issue #930
    check("TrigExpand(Sinh(a+b))", //
        "Cosh(b)*Sinh(a)+Cosh(a)*Sinh(b)");
    check("TrigExpand(Sinh(a+b+c))", //
        "Cosh(b)*Cosh(c)*Sinh(a)+Cosh(a)*Cosh(c)*Sinh(b)+Cosh(a)*Cosh(b)*Sinh(c)+Sinh(a)*Sinh(b)*Sinh(c)");
    check("TrigExpand(Sinh(a+b+c+d))", //
        "Cosh(b)*Cosh(c)*Cosh(d)*Sinh(a)+Cosh(a)*Cosh(c)*Cosh(d)*Sinh(b)+Cosh(a)*Cosh(b)*Cosh(d)*Sinh(c)+Cosh(d)*Sinh(a)*Sinh(b)*Sinh(c)+Cosh(a)*Cosh(b)*Cosh(c)*Sinh(d)+Cosh(c)*Sinh(a)*Sinh(b)*Sinh(d)+Cosh(b)*Sinh(a)*Sinh(c)*Sinh(d)+Cosh(a)*Sinh(b)*Sinh(c)*Sinh(d)");
    // issue #930
    check("TrigExpand(Cosh(a+b))", //
        "Cosh(a)*Cosh(b)+Sinh(a)*Sinh(b)");
    check("TrigExpand(Cosh(a+b+c))", //
        "Cosh(a)*Cosh(b)*Cosh(c)+Cosh(c)*Sinh(a)*Sinh(b)+Cosh(b)*Sinh(a)*Sinh(c)+Cosh(a)*Sinh(b)*Sinh(c)");
    check("TrigExpand(Cosh(a+b+c+d))", //
        "Cosh(a)*Cosh(b)*Cosh(c)*Cosh(d)+Cosh(c)*Cosh(d)*Sinh(a)*Sinh(b)+Cosh(b)*Cosh(d)*Sinh(a)*Sinh(c)+Cosh(a)*Cosh(d)*Sinh(b)*Sinh(c)+Cosh(b)*Cosh(c)*Sinh(a)*Sinh(d)+Cosh(a)*Cosh(c)*Sinh(b)*Sinh(d)+Cosh(a)*Cosh(b)*Sinh(c)*Sinh(d)+Sinh(a)*Sinh(b)*Sinh(c)*Sinh(d)");
    // issue #930
    check("TrigExpand(Sin(a+b))", //
        "Cos(b)*Sin(a)+Cos(a)*Sin(b)");
    check("TrigExpand(Sin(a+b+c))", //
        "Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c)");
    check("TrigExpand(Sin(a+b+c+d))", //
        "Cos(b)*Cos(c)*Cos(d)*Sin(a)+Cos(a)*Cos(c)*Cos(d)*Sin(b)+Cos(a)*Cos(b)*Cos(d)*Sin(c)-Cos(d)*Sin(a)*Sin(b)*Sin(c)+Cos(a)*Cos(b)*Cos(c)*Sin(d)-Cos(c)*Sin(a)*Sin(b)*Sin(d)-Cos(b)*Sin(a)*Sin(c)*Sin(d)-Cos(a)*Sin(b)*Sin(c)*Sin(d)");
    // issue #930
    check("TrigExpand(Cos(a+b))", //
        "Cos(a)*Cos(b)-Sin(a)*Sin(b)");
    check("TrigExpand(Cos(a+b+c))", //
        "Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c)");
    check("TrigExpand(Cos(a+b+c+d))", //
        "Cos(a)*Cos(b)*Cos(c)*Cos(d)-Cos(c)*Cos(d)*Sin(a)*Sin(b)-Cos(b)*Cos(d)*Sin(a)*Sin(c)-Cos(a)*Cos(d)*Sin(b)*Sin(c)-Cos(b)*Cos(c)*Sin(a)*Sin(d)-Cos(a)*Cos(c)*Sin(b)*Sin(d)-Cos(a)*Cos(b)*Sin(c)*Sin(d)+Sin(a)*Sin(b)*Sin(c)*Sin(d)");


    check("TrigExpand( Csch(2*x) )", //
        "1/2*Csch(x)*Sech(x)");
    check("TrigExpand( Csch(3*x) )", //
        "Csch(x)/(-1+4*Cosh(x)^2)");
    check("TrigExpand( Csch(4*x) )", //
        "Csch(x)/(-4*Cosh(x)+8*Cosh(x)^3)");
    check("TrigExpand(Cosh(x)*Csch(2*x)*Sinh(x))", //
        "1/2");
    check("TrigExpand(Cos(x)*Csc(2*x)*Sin(x))", //
        "1/2");
    check("TrigExpand(Cot(2*x))", //
        "Cot(x)/2-Tan(x)/2");

    check("TrigExpand(Cosh(2*a))", //
        "Cosh(a)^2+Sinh(a)^2");
    check("TrigExpand(Cosh(3*a))", //
        "Cosh(a)^3+3*Cosh(a)*Sinh(a)^2");


    check("TrigExpand(Sinh(2*a))", //
        "2*Cosh(a)*Sinh(a)");
    check("TrigExpand(Sinh(3*a))", //
        "3*Cosh(a)^2*Sinh(a)+Sinh(a)^3");
    check("TrigExpand(Sinh(4*a))", //
        "4*Cosh(a)^3*Sinh(a)+4*Cosh(a)*Sinh(a)^3");

    check("TrigExpand(Sinh(a+b))", //
        "Cosh(b)*Sinh(a)+Cosh(a)*Sinh(b)");
    check("TrigExpand(Sinh(a+b+c))", //
        "Cosh(b)*Cosh(c)*Sinh(a)+Cosh(a)*Cosh(c)*Sinh(b)+Cosh(a)*Cosh(b)*Sinh(c)+Sinh(a)*Sinh(b)*Sinh(c)");

    check("TrigExpand(Coth(a+b))", //
        "(Cosh(a)*Cosh(b))/(Cosh(b)*Sinh(a)+Cosh(a)*Sinh(b))+" //
            + "(Sinh(a)*Sinh(b))/(Cosh(b)*Sinh(a)+Cosh(a)*Sinh(b))");

    check("TrigExpand(Tanh(a+b))", //
        "(Cosh(b)*Sinh(a))/(Cosh(a)*Cosh(b)+Sinh(a)*Sinh(b))+" //
            + "(Cosh(a)*Sinh(b))/(Cosh(a)*Cosh(b)+Sinh(a)*Sinh(b))");
    check("TrigExpand(Tanh(a+b+c))", //
        "(Cosh(b)*Cosh(c)*Sinh(a))/(Cosh(a)*Cosh(b)*Cosh(c)+Cosh(c)*Sinh(a)*Sinh(b)+Cosh(b)*Sinh(a)*Sinh(c)+Cosh(a)*Sinh(b)*Sinh(c))+"//
            + "(Cosh(a)*Cosh(c)*Sinh(b))/(Cosh(a)*Cosh(b)*Cosh(c)+Cosh(c)*Sinh(a)*Sinh(b)+Cosh(b)*Sinh(a)*Sinh(c)+Cosh(a)*Sinh(b)*Sinh(c))+"//
            + "(Cosh(a)*Cosh(b)*Sinh(c))/(Cosh(a)*Cosh(b)*Cosh(c)+Cosh(c)*Sinh(a)*Sinh(b)+Cosh(b)*Sinh(a)*Sinh(c)+Cosh(a)*Sinh(b)*Sinh(c))+"//
            + "(Sinh(a)*Sinh(b)*Sinh(c))/(Cosh(a)*Cosh(b)*Cosh(c)+Cosh(c)*Sinh(a)*Sinh(b)+Cosh(b)*Sinh(a)*Sinh(c)+Cosh(a)*Sinh(b)*Sinh(c))");

    check("TrigExpand(Csc(a+b+c))", //
        "1/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))");
    check("TrigExpand(Sec(a+b+c))", //
        "1/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))");
    check("TrigExpand(Cot(a+b+c))", //
        "(Cos(a)*Cos(b)*Cos(c))/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))+(-Cos(c)*Sin(a)*Sin(b))/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))+(-Cos(b)*Sin(a)*Sin(c))/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))+(-Cos(a)*Sin(b)*Sin(c))/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))");
    check("TrigExpand(Tan(a+b+c))", //
        "(Cos(b)*Cos(c)*Sin(a))/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))+(Cos(a)*Cos(c)*Sin(b))/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))+(Cos(a)*Cos(b)*Sin(c))/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))+(-Sin(a)*Sin(b)*Sin(c))/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))");

    check("TrigExpand(Csc(2*x))", //
        "1/2*Csc(x)*Sec(x)");
    check("TrigExpand(Sec(2*x))", //
        "1/(Cos(x)^2-Sin(x)^2)");
    check("TrigExpand(Cot(2*x))", //
        "Cot(x)/2-Tan(x)/2");
    check("TrigExpand(Tan(2*x))", //
        "(2*Cos(x)*Sin(x))/(Cos(x)^2-Sin(x)^2)");
    check("TrigExpand(Sin(2*x+3*y))", //
        "2*Cos(x)*Cos(y)^3*Sin(x)+3*Cos(x)^2*Cos(y)^2*Sin(y)-3*Cos(y)^2*Sin(x)^2*Sin(y)-6*Cos(x)*Cos(y)*Sin(x)*Sin(y)^\n"
            + "2-Cos(x)^2*Sin(y)^3+Sin(x)^2*Sin(y)^3");
    check("trigexpand(Sin(2*x))", //
        "2*Cos(x)*Sin(x)");
    check("trigexpand(Sin(x)*Tan(x))", //
        "Sin(x)*Tan(x)");
    check("trigexpand(Sin(x + y))", //
        "Cos(y)*Sin(x)+Cos(x)*Sin(y)");
    check("trigexpand(Cos(x + y))", //
        "Cos(x)*Cos(y)-Sin(x)*Sin(y)");
    check("trigexpand(Sin(x + y + z))", //
        "Cos(y)*Cos(z)*Sin(x)+Cos(x)*Cos(z)*Sin(y)+Cos(x)*Cos(y)*Sin(z)-Sin(x)*Sin(y)*Sin(z)");
    check("trigexpand(Cos(2*x))", //
        "Cos(x)^2-Sin(x)^2");
    check("trigexpand(Sin(5*x))", //
        "5*Cos(x)^4*Sin(x)-10*Cos(x)^2*Sin(x)^3+Sin(x)^5");
  }

  @Test
  public void testTrigReduce() {
    // check("TrigReduce(Tan(x)^2)", //
    // "(1-Cos(2*x))/(1+Cos(2*x))");
    // check("TrigReduce(Tan(x)^3)", //
    // "");

    check("TrigReduce(cos(x)^2)", //
        "1/2+Cos(2*x)/2");
    check("TrigReduce(cos(x)^2*sin(x))", //
        "Sin(x)/4+Sin(3*x)/4");
    check("TrigReduce(cos(x)^2+sin(x))", //
        "1/2+Cos(2*x)/2+Sin(x)");

    check("TrigReduce(Sin(x)*Cos(y))", //
        "1/2*(Sin(x-y)+Sin(x+y))");
    check("TrigReduce(-I*Cos(x)^2-2*Cos(x)*Sin(x)+I*Sin(x)^2)", //
        "-I*Cos(2*x)-Sin(2*x)");
    check("TrigReduce(I*Cos(x)^4+I*2*Cos(x)^2*Sin(x)^2+I*Sin(x)^4)", //
        "I");
    check("TrigReduce(Cos(a*x)*Sin(a*x)^2)", //
        "Cos(a*x)/4-Cos(3*a*x)/4");
    check("TrigReduce(-a*Cos(x)^2+a*Sin(x)^2 )", //
        "-a*Cos(2*x)");
    check("TrigReduce(Sin(x)*Tan(y))", //
        "1/2*(Cos(x-y)-Cos(x+y))*Sec(y)");
    check("TrigReduce(Sin(x)*Tan(y))", //
        "1/2*(Cos(x-y)-Cos(x+y))*Sec(y)");
    check("TrigReduce(Cos(x)*Tan(y))", //
        "1/2*Sec(y)*(-Sin(x-y)+Sin(x+y))");
    check("TrigReduce(2*Cos(x)^2)", //
        "1+Cos(2*x)");
    check("TrigReduce(2*Cos(x)*Sin(y))", //
        "-Sin(x-y)+Sin(x+y)");
    check("TrigReduce(15*Sin(12*x)^2 + 12*Sin(15*x)^2)", //
        "27/2-15/2*Cos(24*x)-6*Cos(30*x)");
    check("TrigReduce(2*Sinh(u)*Cosh(v))", //
        "Sinh(u-v)+Sinh(u+v)");
    check("TrigReduce(3*Sinh(u)*Cosh(v)*k)", //
        "3/2*k*Sinh(u-v)+3/2*k*Sinh(u+v)");
    // check("TrigReduce(2 Tan(x)*Tan(y))",
    // "(2*Cos(-y+x)-2*Cos(y+x))*(Cos(-y+x)+Cos(y+x))^(-1)");
  }

  @Test
  public void testTrigToExp() {
    check("TrigToExp(Cos(Sin(x)))", //
        "1/(2*E^(I*((I*1/2)/E^(I*x)-I*1/2*E^(I*x))))+E^(I*((I*1/2)/E^(I*x)-I*1/2*E^(I*x)))/\n" //
            + "2");
    String expected = String.join("\n", //
        "                ArcSinh(x)==Log(x+Sqrt(1+x^2)) ", //
        "       ArcCosh(x)==Log(x+Sqrt(-1+x)*Sqrt(1+x)) ", //
        "            ArcTanh(x)==-Log(1-x)/2+Log(1+x)/2 ", //
        "        ArcCoth(x)==-Log(1-1/x)/2+Log(1+1/x)/2 ", //
        " ArcSech(x)==Log(Sqrt(-1+1/x)*Sqrt(1+1/x)+1/x) ", //
        "            ArcCsch(x)==Log(Sqrt(1+1/x^2)+1/x) ");
    check("Map(# == TrigToExp(#)&," //
        + "{ArcSinh(x), ArcCosh(x), ArcTanh(x)," //
        + "ArcCoth(x), ArcSech(x), ArcCsch(x)}) // TableForm", //
        expected);
    expected = String.join("\n", //
        "           ArcSin(x)==-I*Log(I*x+Sqrt(1-x^2)) ", //
        "       ArcCos(x)==Pi/2+I*Log(I*x+Sqrt(1-x^2)) ", //
        " ArcTan(x)==I*1/2*Log(1-I*x)-I*1/2*Log(1+I*x) ", //
        " ArcCot(x)==I*1/2*Log(1-I/x)-I*1/2*Log(1+I/x) ", //
        "     ArcSec(x)==Pi/2+I*Log(Sqrt(1-1/x^2)+I/x) ", //
        "         ArcCsc(x)==-I*Log(Sqrt(1-1/x^2)+I/x) ");
    check("Map(# == TrigToExp(#)&," //
        + "{ArcSin(x), ArcCos(x), ArcTan(x)," //
        + "ArcCot(x), ArcSec(x), ArcCsc(x)}) // TableForm", //
        expected);
    check("I*y+x", //
        "x+I*y");
    check("TrigToExp(ArcTan(x, y))", //
        "-I*Log((x+I*y)/Sqrt(x^2+y^2))");
    check("TrigToExp(Sin(x) < x < Tan(x))", //
        "(I*1/2)/E^(I*x)-I*1/2*E^(I*x)<x<(I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))");
    check("a+E^x", //
        "a+E^x");
    check("TrigToExp(ArcCos(x))", //
        "Pi/2+I*Log(I*x+Sqrt(1-x^2))");
    check("TrigToExp(ArcCsc(x))", //
        "-I*Log(Sqrt(1-1/x^2)+I/x)");
    check("TrigToExp(ArcCot(x))", //
        "I*1/2*Log(1-I/x)-I*1/2*Log(1+I/x)");
    check("TrigToExp(ArcSec(x))", //
        "Pi/2+I*Log(Sqrt(1-1/x^2)+I/x)");
    check("TrigToExp(ArcSin(x))", //
        "-I*Log(I*x+Sqrt(1-x^2))");
    check("TrigToExp(ArcTan(x))", //
        "I*1/2*Log(1-I*x)-I*1/2*Log(1+I*x)");

    check("TrigToExp(ArcCosh(x))", //
        "Log(x+Sqrt(-1+x)*Sqrt(1+x))");
    check("TrigToExp(ArcCsch(x))", //
        "Log(Sqrt(1+1/x^2)+1/x)");
    check("TrigToExp(ArcCoth(x))", //
        "-Log(1-1/x)/2+Log(1+1/x)/2");
    check("TrigToExp(ArcSech(x))", //
        "Log(Sqrt(-1+1/x)*Sqrt(1+1/x)+1/x)");
    check("TrigToExp(ArcSinh(x))", //
        "Log(x+Sqrt(1+x^2))");
    check("TrigToExp(ArcTanh(x))", //
        "-Log(1-x)/2+Log(1+x)/2");
    check("TrigToExp(Cos(x))", //
        "1/(2*E^(I*x))+E^(I*x)/2");
    check("TrigToExp(Cot(x))", //
        "(-I*(E^(-I*x)+E^(I*x)))/(E^(-I*x)-E^(I*x))");
    check("TrigToExp(Csc(x))", //
        "(-I*2)/(E^(-I*x)-E^(I*x))");
    check("TrigToExp(Sec(x))", //
        "2/(E^(-I*x)+E^(I*x))");
    check("TrigToExp(Tan(x))", //
        "(I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))");
    check("TrigToExp(Cosh(x)+a)", //
        "a+1/(2*E^x)+E^x/2");
    check("TrigToExp(Csch(x)+a)", //
        "a+2/(-1/E^x+E^x)");
    check("TrigToExp(Coth(x)+a)", //
        "a+(E^(-x)+E^x)/(-1/E^x+E^x)");
    check("TrigToExp(Sech(x)+a)", //
        "a+2/(E^(-x)+E^x)");
    check("TrigToExp(Sinh(x)+a)", //
        "a-1/(2*E^x)+E^x/2");
    check("TrigToExp(Tanh(x))", //
        "-1/(E^x*(E^(-x)+E^x))+E^x/(E^(-x)+E^x)");
    check("TrigToExp(a+b)", //
        "a+b");
  }

  @Test
  public void testTrueQ() {
    check("TrueQ(True)", //
        "True");
    check("TrueQ(False)", //
        "False");
    check("TrueQ(x)", //
        "False");
  }

  @Test
  public void testTuples() {
    check("Tuples(x^2, -10)", //
        "Tuples(x^2,-10)");
    check("Tuples(x^2, 0)", //
        "{1}");
    check("Tuples({}, 2)", //
        "{}");
    check("Tuples({a, b, c}, 0)", //
        "{{}}");
    check("tuples({{a, b}, {1, 2, 3, 4}})", //
        "{{a,1},{a,2},{a,3},{a,4},{b,1},{b,2},{b,3},{b,4}}");
    check("tuples({{a, b}, {1, 2, 3, 4}, {x}})", //
        "{{a,1,x},{a,2,x},{a,3,x},{a,4,x},{b,1,x},{b,2,x},{b,3,x},{b,4,x}}");

    check("tuples({0,1},3)", //
        "{{0,0,0},{0,0,1},{0,1,0},{0,1,1},{1,0,0},{1,0,1},{1,1,0},{1,1,1}}");
    check("tuples({1,0},3)", //
        "{{1,1,1},{1,1,0},{1,0,1},{1,0,0},{0,1,1},{0,1,0},{0,0,1},{0,0,0}}");

    // The head of list need not be 'List':
    check("Tuples(f(a, b, c), 2)", //
        "{f(a,a),f(a,b),f(a,c),f(b,a),f(b,b),f(b,c),f(c,a),f(c,b),f(c,c)}");
    // However, when specifying multiple expressions, 'List' is always used:
    check("Tuples({f(a, b), g(x, y)})", "{{a,x},{a,y},{b,x},{b,y}}");
  }

  @Test
  public void testUnequal() {
    check("(E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi!=0", //
        "False");

    check("\"11\"!=11", //
        "True");
    check("a!=a", //
        "False");
    check("a!=b", //
        "a!=b");
    check("1!=1.", //
        "False");
    check("{1} != {2}", //
        "True");
    check("{1, 2} != {1, 2}", //
        "False");
    check("{a} != {a}", //
        "False");
    check("\"a\" != \"b\"", //
        "True");
    check("\"a\" != \"a\"", //
        "False");
    check("Pi != N(Pi)", //
        "False");
    check("a_ != b_", //
        "a_!=b_");
    check("a != a != a", //
        "False");
    check("a != b != a", //
        "a!=b!=a");
    check("{Unequal(), Unequal(x), Unequal(1)}", //
        "{True,True,True}");

    check("{\"a\",\"b\"}!={\"a\",\"b\"}", //
        "False");
    check("{\"a\",\"b\"}!={\"b\",\"a\"}", //
        "True");
    check("{\"a\",b}!={\"a\",c}", //
        "{a,b}!={a,c}");
    check("a!=a!=a!=a", //
        "False");
  }

  @Test
  public void testUnevaluated() {
    check("list1={{{1},{2},{1,2},{1,2,3},{},{}},{{2},{3},{}}}", //
        "{{{1},{2},{1,2},{1,2,3},{},{}},{{2},{3},{}}}");
    check("list1 //. {} :> Unevaluated(##&[])", //
        "{{{1},{2},{1,2},{1,2,3}},{{2},{3}}}");

    check("SetAttributes(symbolLength, HoldAll); " //
        + "symbolLength(s_Symbol) := StringLength(SymbolName(Unevaluated(s)))", //
        "");
    check("testSymbol = 42", //
        "42");
    check("symbolLength(testSymbol)", //
        "10");

    check("Sqrt(Unevaluated(x))", //
        "Sqrt(x)");
    check("Length(Unevaluated(5 + 6 + 7 + 8))", //
        "4");
    check("Attributes(Unevaluated)", //
        "{HoldAllComplete,Protected}");
    check("f(Unevaluated(x))", //
        "f(Unevaluated(x))");
    check("Attributes(f) = {Flat};", //
        "");
    check("f(Unevaluated(f(b, c)))", //
        "f(Unevaluated(b),Unevaluated(c))");
    check("f(a, Unevaluated(f(b, c)))", //
        "f(a,Unevaluated(b),Unevaluated(c))");
    check("g(a, Sequence(Unevaluated(b),Unevaluated(c)))", //
        "g(a,Unevaluated(b),Unevaluated(c))");
    check("g(Unevaluated( Sequence(a,b,c)))", //
        "g(Unevaluated(Sequence(a,b,c)))");

    check("Length(Unevaluated(5 + 6 + 7 + 8))", //
        "4");
    check("f(x_) := x^2", //
        "");
    check("ReplaceAll(Unevaluated(f(3)), 3 -> 1)", //
        "1");


    check("F(x___Real):=List(x)^2; a=0.4;", //
        "");
    check("F(Unevaluated(a), a, Unevaluated(a))", //
        "F(Unevaluated(a),0.4,Unevaluated(a))");
  }

  @Test
  public void testIntersection() {
    check(
        "Intersection({1.1, 3.4, .5, 7.6, 7.1, 1.9}, {1.2, 3.3, 7.7, 1.3}, SameTest -> (Floor[#1] == Floor[#2] &))", //
        "{1.9,3.4,7.6}");
    check("Intersection({2, -2, 1, 3, 1}, {2, 1, -2, -1},  SameTest -> (Abs(#1) == Abs(#2) &))", //
        "{1,2}");
    check(
        "Intersection({{1, 2}, {3}, {4, 5, 6}, {9, 6}}, {{2, 1}, {8, 4, 3}}, SameTest->(Total(#1) == Total(#2) &))", //
        "{{1,2},{4,5,6}}");

    check("Intersection(#1, #2, #1)", //
        "Slot()");
    check("Intersection(#1, #1)", //
        "#1");
    check("Intersection(#1,#1*#2)", //
        "#1⋂#1*#2");

    check("Intersection({},{})", "{}");
    check("Intersection({1},{2})", "{}");
    check("Intersection({1,2,2,4},{2,3,4,5})", "{2,4}");
    check("Intersection({2,3,4,5},{1,2,2,4})", "{2,4}");


  }

  @Test
  public void testUnion() {
    // check("Union({},{})", "{}");
    check("Union({1},{2})", "{1,2}");
    check("Union({1,2,2,4},{2,3,4,5})", "{1,2,3,4,5}");

    check("Union(#1,#1*#2)", //
        "Union(#1,#1*#2)");
    check("Union(#1, #2)", //
        "Slot(1,2)");

    // http://oeis.org/A001597 - Perfect powers: m^k where m > 0 and k >= 2.
    check("$min = 0; $max = 10^4;  " //
        + "Union@ Flatten@ Table( n^expo, {expo, Prime@ Range@ PrimePi@ Log2@ $max}, {n, Floor(1 + $min^(1/expo)), $max^(1/expo)})", //
        "{1,4,8,9,16,25,27,32,36,49,64,81,100,121,125,128,144,169,196,216,225,243,256,289,\n"
            + "324,343,361,400,441,484,512,529,576,625,676,729,784,841,900,961,1000,1024,1089,\n"
            + "1156,1225,1296,1331,1369,1444,1521,1600,1681,1728,1764,1849,1936,2025,2048,2116,\n"
            + "2187,2197,2209,2304,2401,2500,2601,2704,2744,2809,2916,3025,3125,3136,3249,3364,\n"
            + "3375,3481,3600,3721,3844,3969,4096,4225,4356,4489,4624,4761,4900,4913,5041,5184,\n"
            + "5329,5476,5625,5776,5832,5929,6084,6241,6400,6561,6724,6859,6889,7056,7225,7396,\n"
            + "7569,7744,7776,7921,8000,8100,8192,8281,8464,8649,8836,9025,9216,9261,9409,9604,\n"
            + "9801,10000}");

    check("Union({a,a,b,c})", //
        "{a,b,c}");
    check("Union({9, 0, 0, 3, 2, 3, 6, 2, 9, 8, 4, 9, 0, 2, 6, 5, 7, 4, 9, 8})", //
        "{0,2,3,4,5,6,7,8,9}");
    check("Union({a,a,b,c},{},{})", //
        "{a,b,c}");
    check("Union({a,a,b,c},{},{z,z,z,x,x,x,y,y,y})", //
        "{a,b,c,x,y,z}");

    check("Union({2, -2, 1, 3, 1}, SameTest->(Abs(#1)==Abs(#2) &))", //
        "{-2,1,3}");
    check("Union({1.1, 3.4, .5, 7.6, 7.1, 1.9}, SameTest->(Floor(#1)==Floor(#2) &))", //
        "{0.5,1.1,3.4,7.1}");
    check("Union({{1, 2}, {3}, {4, 5, 6}, {9, 6}}, SameTest->(Total(#1)==Total(#2)&))", //
        "{{3},{9,6}}");
  }

  @Test
  public void testUnique() {
    EvalEngine.resetModuleCounter4JUnit();
    check("Unique()", //
        "$1");
    check("Unique(x)", //
        "x$2");
    check("Unique(\"x\")", //
        "x3");
  }

  @Test
  public void testUnitConvert() {
    check("UnitConvert(Quantity(Pi, \"deg\"), \"rad\")", //
        "Pi^2/180[rad]");
    check("UnitConvert(Quantity(\"StandardAccelerationOfGravity\"),\"m/s^2\")", //
        "196133/20000[m*s^-2]");
    check("UnitConvert(Quantity(111, \"cm\"),\"m\" )", //
        "111/100[m]");

    check("UnitConvert(Quantity(Pi, \"rad\"), \"deg\")", //
        "180[deg]");
    check("UnitConvert(Quantity(Pi, \"grad\"), \"rad\")", //
        "Pi^2/180[rad]");
    check("UnitConvert(Quantity(Pi, \"rad\"), \"grad\")", //
        "180[grad]");
    check("UnitConvert(Quantity(200, \"g\")*Quantity(981, \"cm*s^-2\") )", //
        "981/500[kg*m*s^-2]");
    check("UnitConvert(Quantity(10^(-6), \"MOhm\") )", //
        "1[A^-2*kg*m^2*s^-3]");
    check("UnitConvert(Quantity(10^(-6), \"MOhm\"),\"Ohm\" )", //
        "1[Ohm]");
    check("UnitConvert(Quantity(1, \"nmi\"),\"km\" )", //
        "463/250[km]");
    check("UnitConvert(Quantity(360, \"mV^-1*mA*s^2\"),\"Ohm^-1*s^2\" )", //
        "360[Ohm^-1*s^2]");
    check("UnitConvert(Quantity(360, \"km*h^-1\"),\"m*s^-1\" )", //
        "100[m*s^-1]");
    check("UnitConvert(Quantity(2, \"km^2\") )", //
        "2000000[m^2]");
    check("UnitConvert(Quantity(2, \"km^2\"),\"cm^2\" )", //
        "20000000000[cm^2]");
    check("UnitConvert(Quantity(3, \"Hz^-2*N*m^-1\") )", //
        "3[kg]");
    check("UnitConvert(Quantity(3.8, \"lb\") )", //
        "1.72365[kg]");
    check("UnitConvert(Quantity(8.2, \"nmi\"), \"km\")", //
        "15.1864[km]");
  }

  @Test
  public void testUnitize() {
    // Unitize uses PossibleZeroQ
    // checkNumeric(
    // "N((E + Pi)^2 )", //
    // "34.33812894536713");
    // checkNumeric(
    // "N((E + Pi)*(E + Pi) )", //
    // "34.33812894536713");
    // checkNumeric(
    // "N(-E^2 - Pi^2 - 2*E*Pi)", //
    // "-34.33812894536714");
    // check(
    // "N((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)", //
    // "-7.10543*10^-15");
    check("Unitize(I,2)", //
        "0");
    check("Unitize(-I,1)", //
        "1");
    check("Unitize(I)", //
        "1");
    check("Unitize(-I)", //
        "1");
    check("Unitize(Infinity)", //
        "1");
    check("Unitize(-Infinity)", //
        "1");

    check("Unitize((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)", //
        "0");
    check("Unitize(2^(2*I) - 2^(-2*I) - 2*I*Sin(Log(4)))", //
        "0");
    check("Unitize(Sqrt(2) + Sqrt(3) - Sqrt(5 + 2*Sqrt(6)))", //
        "0");

    check("Unitize({0, -1})", //
        "{0,1}");
    check("Unitize(0)", //
        "0");
    check("Unitize(0.0)", //
        "0");
    check("Unitize(x)", //
        "Unitize(x)");
    check("Unitize(3/4)", //
        "1");
    check("Unitize(3*I)", //
        "1");
    check("Unitize(Pi)", //
        "1");

    check("Unitize(x)", //
        "Unitize(x)");
    check("Unitize(x,dx)", //
        "Unitize(x,dx)");
  }

  @Test
  public void testUnitStep() {

    check("Table(UnitStep(x), {x, -3, 3})", //
        "{0,0,0,1,1,1,1}");
    check("UnitStep(-Pi*E)", //
        "0");
    check("UnitStep(-Infinity)", //
        "0");
    check("UnitStep(Infinity)", //
        "1");

    check("UnitStep(0)", //
        "1");
    check("UnitStep(1)", //
        "1");
    check("UnitStep(-1)", //
        "0");

    check("UnitStep(Interval({0,42}))", //
        "Interval({1,1})");
    check("UnitStep(Interval({-3,-1}))", //
        "Interval({0,0})");
    check("UnitStep(Interval({-1,2}))", //
        "Interval({0,1})");

    check("UnitStep(42)", //
        "1");
    check("UnitStep(-1)", //
        "0");
    check("UnitStep(-42)", //
        "0");
    check("UnitStep({1.6, 1.6000000000000000000000000})", //
        "{1,1}");
    check("UnitStep({-1, 0, 1})", //
        "{0,1,1}");
    check("UnitStep(1, 2, 3)", //
        "1");
  }

  @Test
  public void testUnset() {
    check("Indeterminate=.", //
        "$Failed");
    check("0=.", //
        "$Failed");
    check("a=.", //
        "");
    check("$x=5;$x=.;$x", //
        "$x");
    check("$f(x_):=x^2;$f(x_)=.;$f(3)", //
        "$f(3)");
  }

  @Test
  public void testUpperCaseQ() {
    // print message: UpperCaseQ: String expected at position 1 in UpperCaseQ(abc).
    check("UpperCaseQ(abc)", //
        "UpperCaseQ(abc)");

    check("UpperCaseQ(\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\")", //
        "True");
    check("UpperCaseQ(\"ABCDEFGHIJKLMNopqRSTUVWXYZ\")", //
        "False");
  }

  @Test
  public void testUpValues() {
    check("u /: v(x_u) := {x}", //
        "");
    check("UpValues(u)", //
        "{HoldPattern(v(x_u)):>{x}}");
  }

  @Test
  public void testUpSet() {
    check("Part(f(x_), s1 : (_String | {_String ..}), s2 : (_String | {_String ..})) ^:= {x,s1,s2}", //
        "");
    check("f(y)[[{\"a\",\"b\"},{\"X\",\"Y\"}]]", //
        "{y,{a,b},{X,Y}}");

    check("ParameterRanges[Cartesian]^=Null;Null", //
        "");
    check("ParameterRanges[Cartesian]^=Null;Null", //
        "");
    check("$f($abc(0))^=100;$f($abc(0))", //
        "100");
  }

  @Test
  public void testUpSetDelayed() {
    check("$f($h(0)) ^= h0;$f($h(x_)) ^:= 2*$f($h(x - 1));$f($h(10))", //
        "1024*h0");

    check("a*b_ ^:= c", //
        "");
    check("2*a", //
        "c");
    check("2*a*d", //
        "c");
  }

  @Test
  public void testValueQ() {
    check("ValueQ(x)", //
        "False");
    check("x=1; ValueQ(x)", //
        "True");
    check("ValueQ(True)", //
        "False");
  }

  @Test
  public void testVariables() {
    check("Variables(FactorInteger(1232))", //
        "{}");
    check("Variables({x+y,x,z})", //
        "{x,y,z}");
    check("Variables(x + f(x)+Pi*E)", //
        "{x,f(x)}");
    check("Variables(x^0.3 + f(x)+Pi*E)", //
        "{x^0.3,f(x)}");
    check("Variables(Sin(x) + Cos(x))", //
        "{Cos(x),Sin(x)}");
    check("Variables({a + b*x, c*y^2 + x/2})", //
        "{a,b,c,x,y}");
    check("Variables((x + y)^2 + 3*z^2 - y*z + 7)", //
        "{x,y,z}");
    check("Variables((a - b)/(x + y) - 2/z)", //
        "{a,b,x,y,z}");
    check("Variables(Sqrt(x + y - z^2) + (-2*t)^(2/3))", //
        "{t,x,y,z}");
    check("Variables(y + x*z)", "{x,y,z}");

    check("Variables(a*x^2 + b*x + c)", //
        "{a,b,c,x}");
    check("Variables({a + b*x, c*y^2 + x/2})", //
        "{a,b,c,x,y}");
    check("Variables(x + Sin(y))", //
        "{x,Sin(y)}");
    check("Variables(x + Sin(10))", //
        "{x}");
    check("Variables(E^x)", //
        "{}");
    check("Variables(a^x)", //
        "{a^x}");
  }

  @Test
  public void testVariance() {
    check("Variance(BinomialDistribution(n, m))", //
        "(1-m)*m*n");
    check("Variance(BernoulliDistribution(n))", //
        "(1-n)*n");
    check("Variance(BetaDistribution(a,b))", //
        "(a*b)/((a+b)^2*(1+a+b))");
    check("Variance(DiscreteUniformDistribution({l, r}))", //
        "1/12*(-1+(1-l+r)^2)");
    check("Variance(ErlangDistribution(n, m))", //
        "n/m^2");
    check("Variance(ExponentialDistribution(n))", //
        "1/n^2");
    check("Variance(LogNormalDistribution(m,s))", //
        "(-1+E^s^2)*E^(2*m+s^2)");
    check("Variance(NakagamiDistribution(n, m))", //
        "m+(-m*Pochhammer(n,1/2)^2)/n");
    check("Variance(NormalDistribution(n, m))", //
        "m^2");
    check("Variance(FrechetDistribution(n, m))", //
        "Piecewise({{m^2*(Gamma(1-2/n)-Gamma(1-1/n)^2),n>2}},Infinity)");
    check("Variance(GammaDistribution(n, m))", //
        "m^2*n");
    check("Variance(GeometricDistribution(n))", //
        "(1-n)/n^2");
    check("Variance(GumbelDistribution(n, m))", //
        "1/6*m^2*Pi^2");
    check("Variance(GompertzMakehamDistribution(m,n))", //
        "(E^n*(6*EulerGamma^2+Pi^2-6*E^n*ExpIntegralEi(-n)^2-12*n*HypergeometricPFQ({1,1,\n" + //
            "1},{2,2,2},-n)+12*EulerGamma*Log(n)+6*Log(n)^2))/(6*m^2)");
    check("Variance(HypergeometricDistribution(n, ns, nt))", //
        "(n*ns*(1-ns/nt)*(-n+nt))/((-1+nt)*nt)");
    check("Variance(PoissonDistribution(n))", //
        "n");
    check("Variance(StudentTDistribution(4))", //
        "2");
    check("Variance(StudentTDistribution(n))", //
        "Piecewise({{n/(-2+n),n>2}},Indeterminate)");

    check("Variance(WeibullDistribution(n, m))", //
        "m^2*(-Gamma(1+1/n)^2+Gamma(1+2/n))");

    check("Variance({1, 2, 3})", //
        "1");
    check("Variance({7, -5, 101, 3})", //
        "7475/3");
    check("Variance({1 + 2*I, 3 - 10*I})", //
        "74");
    check("Variance({a, a})", //
        "0");
    check("Variance({{1, 3, 5}, {4, 10, 100}})", //
        "{9/2,49/2,9025/2}");

    check("Variance({Pi,E,3})//Together", //
        "1/3*(9-3*E+E^2-3*Pi-E*Pi+Pi^2)");
    check("Variance({a,b,c,d})", //
        "1/12*(-(-3*a+b+c+d)*Conjugate(a)-(a-3*b+c+d)*Conjugate(b)-(a+b-3*c+d)*Conjugate(c)-(a+b+c-\n"
            + "3*d)*Conjugate(d))");
    checkNumeric("Variance({1., 2., 3., 4.})", //
        "1.6666666666666667");
    checkNumeric("Variance({{5.2, 7}, {5.3, 8}, {5.4, 9}})", //
        "{0.010000000000000018,1.0}");
    checkNumeric("Variance({1.21, 3.4, 2, 4.66, 1.5, 5.61, 7.22})", //
        "5.16122380952381");
    check("Variance({1.21, 3.4, 2+3*I, 4.66-0.1*I, 1.5, 5.61, 7.22})", //
        "6.46265");
    // check("Variance(BernoulliDistribution(p))", "p*(1-p)");
    // check("Variance(BinomialDistribution(n, p))", "n*p*(1-p)");
  }

  @Test
  public void testVerbatim() {
    check("_ /. Verbatim(_)->t", //
        "t");
    check("x /. Verbatim[_]->t", //
        "x");
  }

  @Test
  public void testVertexList() {
    check("VertexList(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}))", //
        "{1,2,3,4}");
    check(
        "VertexList(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
        "{1,2,3}");
    check("VertexList(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
        "{1,2,3}");
  }

  @Test
  public void testVertexEccentricity() {
    check("VertexEccentricity({1 -> 2, 2 -> 3, 3 -> 1, 3 -> 4, 4 -> 5, 5 -> 3}, 1)", //
        "4");

    check(
        "VertexEccentricity(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
            + "{EdgeWeight->{1.6,1.4,0.62,1.9,2.1}}), 4)", //
        "2.22");
    check(
        "VertexEccentricity({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, 4)", //
        "2");
  }

  @Test
  public void testVertexQ() {
    check("VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),3)", //
        "True");
    check("VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),5)", //
        "False");
  }

  @Test
  public void testWeierstrassHalfPeriods() {
    // TODO improve see discussion: https://github.com/paulmasson/math/issues/7
    check("WeierstrassHalfPeriods({1.0, 2.0 } )", //
        "{1.30836,-0.654182+I*1.22937}");
    check("Table(WeierstrassHalfPeriods({1.0,x*I} ), {x,-2.0, 2.0, 1/4})", //
        "{{1.30139+I*0.299127,0.299127+I*1.30139},{1.32906+I*0.300747,0.300747+I*1.32906}," //
            + "{1.36146+I*0.301917,0.301917+I*1.36146},{1.40035+I*0.30217,0.30217+I*1.40035}," //
            + "{1.4486+I*0.300495,0.300495+I*1.4486},{1.5113+I*0.294424,0.294424+I*1.5113}," //
            + "{1.59835+I*0.276625,0.276625+I*1.59835},{1.72843+I*0.217942,0.217942+I*1.72843}," //
            + "{1.85407,I*1.85407},{1.72843+I*(-0.217942),-0.217942+I*1.72843},{1.59835+I*(-0.276625),-0.276625+I*1.59835}," //
            + "{1.5113+I*(-0.294424),-0.294424+I*1.5113},{1.4486+I*(-0.300495),-0.300495+I*1.4486}," //
            + "{1.40035+I*(-0.30217),-0.30217+I*1.40035},{1.36146+I*(-0.301917),-0.301917+I*1.36146}," //
            + "{1.32906+I*(-0.300747),-0.300747+I*1.32906},{1.30139+I*(-0.299127),-0.299127+I*1.30139}}"); //

    check("WeierstrassHalfPeriods({1.0, 2.0*I} )", //
        "{1.30139+I*(-0.299127),-0.299127+I*1.30139}");
  }

  @Test
  public void testWeierstrassInvariants() {
    check("WeierstrassInvariants({1.0, 2.0*I} )", //
        "{8.12422,4.44305}");
    check("Table(WeierstrassInvariants({1.0,x*I} ), {x,-2.0, 2.0, 1/4})", //
        "{{8.12422,4.44305},{8.15011,4.41322},{8.27476,4.26937},{8.87636,3.56885},{11.81705,-1.97659*10^-15},{27.07395,-22.08742},{129.9875,-284.3553},{2078.061,-18230.79},WeierstrassInvariants({1.0,0.0}),{2078.061,-18230.79},{129.9875,-284.3553},{27.07395,-22.08742},{11.81705,-1.97659*10^-15},{8.87636,3.56885},{8.27476,4.26937},{8.15011,4.41322},{8.12422,4.44305}}");
  }

  @Test
  public void testWeierstrassP() {
    check("WeierstrassP(z, {0, 0}) ", //
        "1/z^2");
    check("WeierstrassP(z, {3, 1}) ", //
        "1+3/2*Cot(Sqrt(3/2)*z)^2");
    check("WeierstrassP(5., {1, 2}) ", //
        "18.35051+I*1.14908*10^-13");
    check("WeierstrassP(12., {3, 2}) ", //
        "77.23116+I*4.34319*10^-13");
    check("WeierstrassP(1/3, {1, 2}) // N // Chop", //
        "9.00644");

    check("WeierstrassP(2.0, {1,2} )", //
        "2.65854+I*2.10942*10^-15");

    check("Table(WeierstrassP(x,{1.0,3.0} ), {x,-2.0, 2.0, 1/4})", //
        "{4.55263+I*2.88658*10^-15,1.98649+I*8.88178*10^-16,1.20805+I*2.22045*10^-16,1.00064,1.16036+I*(-3.33067*10^-16),1.84015+I*(-5.55112*10^-16),4.01922+I*4.44089*10^-16,16.00354+I*2.22045*10^-16,ComplexInfinity,16.00354+I*2.22045*10^-16,4.01922+I*4.44089*10^-16,1.84015+I*(-5.55112*10^-16),1.16036+I*(-3.33067*10^-16),1.00064,1.20805+I*2.22045*10^-16,1.98649+I*8.88178*10^-16,4.55263+I*2.88658*10^-15}");
  }

  @Test
  public void testWeierstrassPPrime() {
    check("WeierstrassPPrime(2.0, {1,2} )", //
        "8.39655+I*1.28374*10^-14");
    check("WeierstrassPPrime(5., {1, 2}) ", //
        "157.1532+I*1.4773*10^-12");
    check("WeierstrassPPrime(12., {3, 2}) ", //
        "1357.348+I*1.16055*10^-11");
    check("WeierstrassPPrime(1/3, {1, 2}) // N // Chop", //
        "-53.95606");

    check("Table(WeierstrassPPrime(x,{1.0,3.0} ), {x,-2.0, 2.0, 1/4})", //
        "{-19.23245+I*(-2.02665*10^-14),-5.13514+I*(-3.76819*10^-15),-1.68643+I*(-1.16607*10^-15),-0.0838866+I*(-5.45142*10^-16),1.44536+I*(-3.30329*10^-16),4.48151+I*2.98579*10^-16,15.89616+I*1.58114*10^-15,127.9683+I*1.1967*10^-15,ComplexInfinity,-127.9683+I*(-1.1967*10^-15),-15.89616+I*(-1.58114*10^-15),-4.48151+I*(-2.98579*10^-16),-1.44536+I*3.30329*10^-16,0.0838866+I*5.45142*10^-16,1.68643+I*1.16607*10^-15,5.13514+I*3.76819*10^-15,19.23245+I*2.02665*10^-14}"); //
    check("WeierstrassPPrime(z, {0, 0}) ", //
        "-2/z^3");
    check("WeierstrassPPrime(z, {3, 1}) ", //
        "-3*Sqrt(3/2)*Cot(Sqrt(3/2)*z)*Csc(Sqrt(3/2)*z)^2");
  }

  @Test
  public void testWhich() {
    check("n=5;Which(n == 3, x, n == 5, y)", //
        "y");
    check("f(x_) := Which(x < 0, -x, x == 0, 0, x > 0, x);f(-3)", //
        "3");
    check("Which(False, a)", //
        "");
    check("Which(False, a, x, b, True, c)", //
        "Which(x,b,True,c)");
    check("Which(a, b, c)", //
        "Which(a,b,c)");

    check("$a = 2;which($a == 1, x, $a == 2, b)", //
        "b");
    check("Which(1 < 0, a,  x == 0, b,  0 < 1, c)", //
        "Which(x==0,b,0<1,c)");
    check("$a = 2;which($a == 1, x, $a == 3, b)", //
        "");
    check("$x=-2;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", //
        "-1");
    check("$x=0;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", //
        "Indeterminate");
    check("$x=3;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", //
        "1");
  }

  @Test
  public void testWhile() {
    check("$n = 1; While($n \\[LessEqual] 4, $n++); $n", //
        "5");
    check("While(False)", //
        "");
    check("{a, b} = {27, 6}; While(b != 0, {a, b} = {b, Mod(a, b)});a", //
        "3");
    check("i = 1; While(True, If(i^2 > 100, Return(i + 1), i++))", //
        "12");
    check("$n = 1; While($n < 4, Print($n); $n++)", //
        "");
    check("$n = 1; While(++$n < 4); $n", //
        "4");
    check("$n = 1; While($n < 4, $n++); $n", //
        "4");
    check("$n = 1; While(True, If($n > 10, Break()); $n++);$n", //
        "11");
  }

  @Test
  public void testWhittakerM() {
    checkNumeric("N(WhittakerM(2, 1/2, 2*2*I))", //
        "0.3080150149255876+I*(-8.938966760794024)");

    check("N(WhittakerM(2, 1/3, 1/7), 100)", //
        "0.1656497719066782903103648280719871043760680518713012567635165215951597907345740933330557755894455857");
    check("WhittakerM(2, 1.33333333333333333333, 1)", //
        "0.576202738935656959011");
    checkNumeric("N(WhittakerM(4/5, 2 - I, 2))", //
        "3.2795757038417443+I*(-3.256018217261299)");
    checkNumeric("N(WhittakerM(I, 42, 0))", //
        "0.0");

    check("D(WhittakerM(a,b,x), x)", //
        "(1/2-a/x)*WhittakerM(a,b,x)+((1/2+a+b)*WhittakerM(1+a,b,x))/x");
    check("WhittakerM(k, -1/2, 0)", //
        "WhittakerM(k,-1/2,0)");
    check("WhittakerM(k, -1/3, 0)", //
        "0");
    check("WhittakerM(k,  -2/3, 0)", //
        "ComplexInfinity");
    check("WhittakerM(1, 1, 0)", //
        "0");
    check("WhittakerM(0, 1, 0)", //
        "0");
    check("WhittakerM(0, 0, 0)", //
        "0");
    check("WhittakerM(2, 3, 1.7)", //
        "4.07202");
    // TODO
    // check("WhittakerM(2, 0.5, 0.0)", //
    // "0");
    check("Table( WhittakerM(3, 2.5, x), {x,-2.0,2,0.25})", //
        "{-21.74625,-12.85647,-7.14488,-3.64892,-1.64872,-0.613825,-0.160503,-0.0177054,0.0,0.013789,0.0973501,0.28995,0.606531,1.04543,1.59424,2.23412,2.94304}");
  }

  @Test
  public void testWhittakerW() {
    checkNumeric("N(WhittakerW(2, 1/2, 2*2*I))", //
        "-0.6160300298511752+I*17.877933521588048");

    check("D(WhittakerW(a,b,x), x)", //
        "(1/2-a/x)*WhittakerW(a,b,x)-WhittakerW(1+a,b,x)/x");
    check("WhittakerW(0, 0, 0)", //
        "0");
    check("N(WhittakerW(3/5, 1/3, 1/7), 100)", //
        "0.3970969447006262740475282170027027703019024268551490675205428794618470776851561601237869467558528364");
    check("WhittakerW(2, 1.2222222222222222222, 1)", //
        "-0.2422894111164979908");

    checkNumeric("N(WhittakerW(1/5, 2 - I, 2))", //
        "0.3147585139185293+I*(-1.2734460466128783)");

    checkNumeric("WhittakerW(6, 4, 1.7)", //
        "1740.5462418091338");
    // TODO
    checkNumeric("WhittakerW(2, 0.5, 0.0)", //
        "WhittakerW(2.0,0.5,0.0)");
    checkNumeric("Table( WhittakerW(6, 4, x), {x,-2.0,2,0.25})", //
        "{2333.0618193174946+I*38.12532949300777,1090.9576562383038+I*73.572156765819,464.5499885712877+I*152.9453283147011,174.10978469218307+I*351.7626599831295,54.254490548428286+I*935.8127384437696,12.63276840854038+I*3129.88616177697,1.7295419807639405+I*15861.825767596809,0.06481415341111611+I*220852.59445363315,WhittakerW(6.0,4.0,0.0),339121.7767806576,37468.50169122885,11421.860441795448,5313.868067440274,3138.141359549763,2164.6812989167843,1662.3688694569848,1374.640737551975}");
  }

  @Test
  public void testWith() {
    // message: With: Duplicate local variable x found in local variable specification {x=a,x=b}.
    check("With({x = a, x=b}, x^2)", //
        "With({x=a,x=b},x^2)");
    check("Block({i = 0}, With({}, Module({j = i}, i=i+1; j)))", //
        "0");
    EvalEngine.resetModuleCounter4JUnit();

    // message: Set: Cannot unset object 2.0.
    check("With({x = 2.0}, x=3.0;Sqrt(x) + 1 )", //
        "2.41421");
    check("With({x = 2.0}, Sqrt(x) + 1)", //
        "2.41421");

    EvalEngine.resetModuleCounter4JUnit();
    check("With({x = 2 + y}, Hold(With({y = 4}, x + y)))", //
        "Hold(With({y$1=4},2+y+y$1))");
    EvalEngine.resetModuleCounter4JUnit();
    check("xm=10;With({xm=xm}, Print(xm));xm", //
        "10");
    check("u=test;With({w=Map(Function({#,x,v,flag}),u)},  w)", //
        "test");
    check("With({x=2, y=16},x^y)", //
        "65536");
    check("With({x = 7, y = a + 1}, x/y)", //
        "7/(1+a)");
    check("With({x = a}, (1 + x^2) &)", //
        "1+a^2&");
    check("Table(With({i = j}, Hold(i)), {j, 5})", //
        "{Hold(1),Hold(2),Hold(3),Hold(4),Hold(5)}");
    // check("With({e = Expand((1 + x)^5)}, Function(x, e))", //
    // "Function(x$11,1+5*x+10*x^2+10*x^3+5*x^4+x^5)");
    check("With({e = Expand((1 + x)^5)}, Function @@ {x, e})", //
        "Function(x,1+5*x+10*x^2+10*x^3+5*x^4+x^5)");

    check("x=5;With({x = x}, Hold(x))", //
        "Hold(5)");

    check("newton(f_, x0_) := With({fp = f'}, FixedPoint(# - f(#)/fp(#) &, x0))", //
        "");
    check("newton(Cos,1.33)", //
        "1.5708");

    check("newton(Cos(#)-#&,1.33)", //
        "0.739085");

    check("With({f = Function(n, If(n == 0, 1, n*f(n - 1)))}, f(10))", //
        "10*f(9)");
    // check("Timing(Do(With({x = 5}, x;), {10^5}))", "");
    // check("Timing(Do(Module({x = 5}, x;), {10^5}))", "");
    check("With({x=y,y=z,z=3}, Print({Hold(x),Hold(y),Hold(z)});{x,y,z})", //
        "{y,z,3}");

    // check("With({x=z},Module({x},x+y))", //
    // "x$24+y");

    check("f(x_) := With({q = False},  test /; q==0) /;  x==1", //
        "");
    check("f(1)", //
        "f(1)");

    check("With({x = a}, x = 5); a", //
        "5");
    check("With({t = 8}, With({t = 9}, t^2))", //
        "81");
    check("Clear(a);With({t = a}, With({u = b}, t + u))", //
        "a+b");

    check("With({tt = a}, (1 + tt^2) &)", //
        "1+a^2&");

    check("With({x:=2,y:=3},{x,3*y})", //
        "{2,9}");
    check("With({a=2},{b=a},{c=b},a+b+c)", //
        "6");
    check("With({y = Sin(1.0)}, Sum(y^i, {i, 0, 10}))", //
        "5.36323");

    check("With({t=Sin(10/2*Pi+#)}, t &)", //
        "-Sin(#1)&");
  }

  @Test
  public void testYuleDissimilarity() {
    check("YuleDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
        "2");
    check("YuleDissimilarity({True, False, True}, {True, True, False})", //
        "2");
    check("YuleDissimilarity({0, 0, 0, 0}, {0, 0, 0, 0})", //
        "0");
    check("YuleDissimilarity({0, 1, 0, 1}, {1, 0, 1, 0})", //
        "2");
  }

  @Test
  public void testZeta() {
    check("Zeta(11,1/2)", //
        "2047*Zeta(11)");

    // https://en.wikipedia.org/wiki/Particular_values_of_the_Riemann_zeta_function
    check("Zeta'(0)", //
        "-Log(2*Pi)/2");
    check("Zeta'(-1)", //
        "1/12-Log(Glaisher)");

    checkNumeric("Zeta(3,-4.0)", //
        "Zeta(3.0,-4.0)");
    checkNumeric("Zeta(2.2,3.1)", //
        "0.26067453797192913");
    check("Zeta(6)", //
        "Pi^6/945");
    check("Zeta(-1)", //
        "-1/12");
    check("Zeta(-11)", //
        "691/32760");
    check("Zeta(-42)", //
        "0");
    check("Zeta(0)", //
        "-1/2");

    check("Zeta(Infinity)", //
        "1");

    check("N(Zeta(5/4),50)", //
        "4.5951118258429433806853780396946256522810297806045");
    check("N(Zeta(3,2),50)", //
        "0.20205690315959428539973816151144999076498629234004");

    check("D(Zeta(s, x), x)", //
        "-s*Zeta(1+s,x)");
    check("Zeta(-3.0)", //
        "0.00833333");
    check("Zeta(4.0)", //
        "1.08232");
    check("Zeta(1.0+I)", //
        "0.582158+I*(-0.926849)");
    check("Zeta(-3.0+I*1.0^(-100))", //
        "0.0143825+I*0.0103497");
    check("Table(Zeta(2*x),{x,1,5,1})", //
        "{Pi^2/6,Pi^4/90,Pi^6/945,Pi^8/9450,Pi^10/93555}");
    check("Table(Zeta(x),{x,0,-20,-1})", //
        "{-1/2,-1/12,0,1/120,0,-1/252,0,1/240,0,-1/132,0,691/32760,0,-1/12,0,3617/8160,0,-\n"
            + "43867/14364,0,174611/6600,0}");
    check("Zeta(2)", //
        "Pi^2/6");
    checkNumeric("Zeta(-2.5 + I)", //
        "0.02359361058637964+I*0.0014077996058383772");
    check("Zeta(s, 0)", //
        "Zeta(s)");
    check("Zeta(s, 1/2)", //
        "(-1+2^s)*Zeta(s)");
    check("Zeta(s, -1)", //
        "1+Zeta(s)");
    check("Zeta(s, 2)", //
        "-1+Zeta(s)");
    check("Zeta(4, -12)", //
        "638942263173398977/590436101122560000+Pi^4/90");
    check("Zeta(11, -12)", //
        "Zeta(11,-12)");
    check("Zeta(-5, -12)", //
        "158938415/252");

  }
}
