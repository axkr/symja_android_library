package org.matheclipse.core.texparser;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.tex.TeXSliceParser;
import org.matheclipse.core.interfaces.IExpr;
import junit.framework.TestCase;

/** Tests LaTeX import (parsing) function */
public class TeXConverterTestCase extends TestCase {

  TeXSliceParser texConverter;

  public TeXConverterTestCase(String name) {
    super(name);
  }

  public void testTeX001() {
    check("x^2 + (a+b)x + ab", //
        "a*b+(a+b)*x+x^2");
    check("(2x^3 - x + z)", //
        "-x+2*x^3+z");
    check("(2x^3 - 3*x + 4.5z)", //
        "-3*x+2*x^3+4.5*z");
    check("x^{3}", //
        "x^3");
    check("\\frac{x}{\\sqrt{5}}", //
        "x/Sqrt(5)");
  }

  public void testTeX002() {
    check("1+y*z", //
        "1+y*z");
  }

  public void testTeX003() {
    check("(1+y)*z", //
        "(1+y)*z");
  }

  public void testTeX004() {
    check("a = bq + r", //
        "a==b*q+r");
    check("f(x)=(x+a)(x+b)", //
        "f(x)==(a+x)*(b+x)");
    check("(x+a)(x+b)=f(x)", //
        "(a+x)*(b+x)==f(x)");

    check("x=\\frac{1+y}{1+2*z^2}", //
        "x==(1+y)/(1+2*z^2)");
  }

  public void testTeX005() {
    check("f(x,1+y)", //
        "f(x,1+y)");
  }

  public void testTeX006() {
    check("\\sin 30 ^ { \\circ }", //
        "Degree*Sin(30)");
  }

  public void testTeX007() {
    check("\\sin  \\frac { \\pi } { 2 }", //
        "Sin(Pi*1/2)");
    check("f(  \\frac { \\pi } { 2 } )", //
        "f(Pi*1/2)");
    // this will print an error message in the console
    // \operatorname isn't supported:
    check("\\operatorname{ sin } \\frac { \\pi } { 2 }", //
        "Sin(Pi*1/2)");
  }

  public void testTeX008() {
    check("1 + 2 - 3 \\times 4 \\div 5", //
        "(-4/5)*3+1+2");
  }

  public void testTeX009() {
    check("x ^ { 2 } + 1 = 2", //
        "1+x^2==2");
  }

  public void testTeX010() {
    check("\\left ( 1+2 \\right )^{2}", //
        "(1+2)^2");
  }

  public void testTeX011() {
    check("\\int_{a}^{b} \\frac{dt}{t}", //
        "Integrate(1/t,{t,a,b})");
    check("\\int f(xz) \\, dxz", //
        "Integrate(f(x*z),xz)");
    check("\\int f(x) \\, dx", //
        "Integrate(f(x),x)");
    check("\\int_0^\\infty a dx", //
        "Integrate(a,{x,0,Infinity})");
    check("\\int_0^\\infty e^{-x^2} dx=\\frac{\\sqrt{\\pi}}{2}", //
        "Integrate(E^(-x^2),{x,0,Infinity})==Sqrt(Pi)*1/2");
    check("\\int \\sqrt{4 - \\frac{3}{5}x}dx", //
        "Integrate(Sqrt(4-3/5*x),x)");
    check("\\frac{2}{3}\\int \\sqrt{4 - \\frac{3}{5}x}dx", //
        "2/3*Integrate(Sqrt(4-3/5*x),x)");
    check("\\frac{2}{3}\\int \\sqrt{4 - \\frac{3}{5}x}dx + \\sin x", //
        "2/3*Integrate(Sqrt(4-3/5*x),x)+Sin(x)");
  }

  public void testTeX012() {
    check("\\sin x", //
        "Sin(x)");
  }

  public void testTeX013() {
    check("\\exp x", //
        "Exp(x)");
  }

  public void testTeX014() {
    check("\\sum_{n=1}^{\\infty} 2^{-n} = 1", //
        "Sum(2^(-n),{n,1,Infinity})==1");
    checkFullForm("\\sum_{n=1}^{\\infty} 2^{-n} = 1", //
        "Equal(Sum(Power(2, Times(-1, n)), List(n, 1, DirectedInfinity(1))), 1)");
    check("\\sum_{i = 1}^{n} i", //
        "Sum(i,{i,1,n})");
  }

  public void testTeX015() {
    check("\\prod_{i=a}^{b} f(i)", //
        "Product(f(i),{i,a,b})");
  }

  public void testTeX016() {
    check("\\frac{x}{\\sqrt{0.75}}", //
        "x/Sqrt(0.75)");
  }

  public void testTeX017() {
    check("a\\leq b \\le c", //
        "a<=b<=c");
  }

  public void testTeX018() {
    check("a\\geq b \\ge c", //
        "a>=b>=c");
  }

  public void testTeX019() {
    check("a < b < c", //
        "a<b<c");
  }

  public void testTeX020() {
    check("a > b > c", //
        "a>b>c");
  }

  public void testTeX021() {
    check("((a \\lor \\lnot b) \\land (c\\wedge a))", //
        "(a||!b)&&c&&a");
  }

  public void testTeX022() {
    // Rightarrow
    check("((a \\lor \\neg b) \\land (c\\Rightarrow a))", //
        "(a||!b)&&(c⇒a)");

    // vs rightarrow
    check("((a \\lor \\neg b) \\land (c\\rightarrow a))", //
        "(a||!b)&&(c->a)");
  }

  public void testTeX023() {
    check("a \\to b", //
        "a->b");
  }

  public void testTeX024() {
    check("a \\Leftrightarrow b", //
        "a⇔b");
    check("a \\equiv b", //
        "a⇔b");
  }

  public void testTeX025() {
    check("a!", //
        "a!");
  }

  public void testTeX026() {
    check("\\cosh ^{-1}(a)", //
        "ArcCosh(a)");
    check("\\tan (a)", //
        "Tan(a)");
  }

  public void testTeX027() {
    check("f'(x)", //
        "f'(x)");
  }

  public void testTeX028() {
    check("\\arccos(x)", //
        "ArcCos(x)");
  }

  public void testTeX029() {
    check("\\log_2345+3 ", //
        "3+Log(2,345)");
    check("\\ln(x)", //
        "Log(x)");
  }

  public void testTeX030() {
    check("a\\in \\mathbb{B}", //
        "a∈Booleans");
    check("a\\in \\mathbb{C}", //
        "a∈Complexes");
    check("a\\in \\mathbb{P}", //
        "a∈Primes");
    check("a\\in \\mathbb{Q}", //
        "a∈Rationals");
    check("a\\in \\mathbb{Z}", //
        "a∈Integers");
    check("a\\in \\mathbb{R}", //
        "a∈Reals");
  }

  public void testTeX031() {
    check("\\frac{d}{dx} x^{2}", //
        "(d*x^2)/(d*x)");
    checkEval("\\frac{d}{dx} x^{2}", //
        "x");
  }

  public void testTeX032() {
    check("L' = {L}{\\sqrt{1-\\frac{v^2}{c^2}}}", //
        "Derivative(1)[L]==Sqrt(1-v^2/c^2)*L");
  }

  public void testTeX033() {
    check("-\\nabla \\times e", //
        "-E*∇");
    check("\\nabla \\times B - 4\\pi j", //
        "-4*j*Pi+B*∇");
  }

  public void testTeX034() {
    check("\\lim_{x\\to 0}{\\frac{E^x-1}{2x}}", //
        "Limit((-1+E^x)/(2*x),x->0)");
    checkEval("\\lim_{x\\to 0}{\\frac{E^x-1}{2x}}", //
        "1/2");

    // only "upper case" E is interpreted as euler's constant
    check("\\lim_{x\\to 0}{\\frac{e^x-1}{2x}}", //
        "Limit((-1+E^x)/(2*x),x->0)");
    checkEval("\\lim_{x\\to 0}{\\frac{e^x-1}{2x}}", //
        "1/2");
  }

  public void testTeXIssueSinTimesCos() {
    check("\\sin a \\cos b", //
        "Cos(b)*Sin(a)");
  }

  public void testTeXIssue712a() {
    check("\\large 6666", //
        "6666");
  }

  public void testTeXIssue712b() {
    check("\\log_{22}5", //
        "Log(22,5)");
  }

  public void testTeXIssue712c() {
    check(
        "\\operatorname { lim } _ { n \\rightarrow 0 } \\frac { ( - 3 ) ^ { n } + 2.5 ^ { n } } { 1 - 5 ^ { n } }", //
        "Limit(((-3)^n+2.5^n)/(1-5^n),n->0)");
  }

  public void testTeXIssue712d() {
    check("|x|", //
        "Abs(x)");
    check("\\pi^{|xy|}+3", //
        "3+Pi^Abs(x*y)");
  }

  public void testTeXIssue712e() {
    checkFullForm("\\lim_{x \\to 3^{+}} a", //
        "Limit(a, Rule(x, 3), Rule(Direction, 1))");
    checkFullForm("\\lim_{x \\longrightarrow 3} a", //
        "Limit(a, Rule(x, 3))");
    checkFullForm("\\lim_{x \\Rightarrow 3} a", //
        "Limit(a, Rule(x, 3))");
  }

  // public void testTeXIssue712f() {
  // check(
  // " \\operatorname { det } \\left| \\begin{array} { l l } { 4 } & { 5 } \\\\ { 7 } & { 2 }
  // \\end{array} \\right|", //
  // "Limit(a,x->3,Direction->1)");
  // }

  public void testTeXTimesSinCos() {
    checkFullForm("\\cos{x} ( \\sin{x} + \\cos{x})", //
        "Cos(x(Sin(Plus(x, Cos(x)))))");
    check("f ( x ) = 2 \\cos x ( \\sin x + \\cos x)", //
        "f(x)==2*Cos(x)*(Cos(x)+Sin(x))");
    check(
        "f ( x ) = 2 \\operatorname { cos } x ( \\operatorname { sin } x + \\operatorname { cos } x)", //
        "f(x)==2*Cos(x(Sin(x+Cos(x))))");
    check("\\sin(\\cos \\theta)", //
        "Sin(Cos(θ))");
  }

  public void testTeXIssue712h() {
    checkFullForm(
        "g \\left(\\right. x \\left.\\right) = \\ln \\left(\\right. \\left|\\right. x + 1 \\right|\\right. \\left.\\right)", //
        "Equal(g(x), Log(Abs(Plus(1, x))))");
  }

  public void testTeXIssue712i() {
    checkFullForm("\\sin x", //
        "Sin(x)");
    checkFullForm("\\sqrt[3]{\\sin {x}}", //
        "Power(Sin(x), Rational(1, 3))");
  }


  public void testTeXIssue712j() {
    checkFullForm("\\operatorname { tan } ( 2 x - \\frac { \\pi } { 4 } )", //
        "Tan(Plus(Times(-1, Pi, Rational(1,4)), Times(2, x)))");
  }

  public void testTeXIssue712k() {
    checkFullForm("2 \\operatorname { cos } ^ { 2 } x - \\operatorname { cos } x - 1 = 0", //
        "Equal(Plus(-1, Plus(Times(-1, Cos(x)), Times(2, Power(Cos(x), 2)))), 0)");
  }

  public void testTeXIssue712l() {
    checkFullForm("\\operatorname{GCD}(3,5)", //
        "GCD(3, 5)");
    checkFullForm("\\operatorname{gcd}(3,5)", //
        "GCD(3, 5)");
    checkFullForm(
        " \\operatorname { log } _ { 2 } ^ { 3 } a + \\operatorname { log } _ { 2 } ^ { 3 } b + \\operatorname { log } _ { 2 } ^ { 3 } c \\leq 1", //
        "LessEqual(Plus(Power(Log(2, a), 3), Power(Log(2, b), 3), Power(Log(2, c), 3)), 1)");
  }

  public void testTeXIssue712m() {
    checkFullForm(//
        "64 ^ { \\frac { 1 } { 2 } } \\cdot 64 ^ { \\frac { 1 } { 3 } } \\cdot \\sqrt [ 6 ] { 6 }", //
        "Times(Power(6, Rational(1, 6)), Power(64, Rational(1,3)), Power(64, Rational(1,2)))");
  }

  public void testTeXIssue712n() {
    checkFullForm(//
        "\\log x", //
        "Log10(x)");
  }

  public void testTeXElementOf() {
    checkFullForm(//
        "\\beta \\in \\reals", //
        "Element(β, Reals)");
    checkFullForm(//
        "\\beta \\in \\Reals", //
        "Element(β, Reals)");
  }

  public void testTeXArray() {
    checkFullForm(//
        "\\left\\{ \\begin{array} { l } { y - x = 1814 } \\\\ { y = 9 x + 182 } \\end{array}\\right.", //
        "List(Equal(Plus(Times(-1, x), y), 1814), Equal(y, Plus(182, Times(9, x))))");
    checkFullForm(//
        " \\begin{array} { l } { y - x = 1814 } \\\\ { y = 9 x + 182 } \\end{array} ", //
        "List(Equal(Plus(Times(-1, x), y), 1814), Equal(y, Plus(182, Times(9, x))))");

    checkFullForm(//
        "\\begin{array} {ll} { 2 } & {3} \\\\ { 3 } & {4} \\end{array}", //
        "List(List(2, 3), List(3, 4))");
  }
  
  public void testTeXAngle() {
    checkFullForm(//
        "3\\angle2", //
        "FromPolarCoordinates(List(3, 2))");
  }

  public void testTeXBinomial001() {
    checkEval(//
        "_{2}^{\\\\:}C_{4}^{\\\\:}", //
        "0");
  }

  public void testTeXBinomial002() {
    checkFullForm(//
        "_{6}^{\\\\:}C_{2}^{\\\\:}", //
        "Binomial(6, 2)");
  }

  public void testTeXPermuatations() {
    checkFullForm(//
        "_{3}^{\\\\:}P_{2}^{\\\\:}", //
        "Pochhammer(2, Plus(-2, 1, 3))");
  }

  public void testTeXPercent() {
    checkFullForm(//
        "20\\% ", //
        "Times(Rational(1,100), 20)");
  }

  public void testRec01() {
      ExprEvaluator evaluator = new ExprEvaluator();
      evaluator.eval("Pol[x_, y_] := FromPolarCoordinates[{x, y}]");

      TeXSliceParser teXSliceParser = new TeXSliceParser();
      IExpr input = teXSliceParser.parse("\\operatorname{Pol}(3,2)");

      IExpr result = evaluator.eval(F.N(input));
      assertEquals(result.toString(), "{-1.2484405096414273,2.727892280477045}");
  }

  public void testDet() {
    check(
        "\\operatorname { det } \\left| \\begin{array} { l l } { 4 } & { 5 } \\\\ { 7 } & { 2 } \\end{array} \\right|", //
        "Det({{4,5},{7,2}})");
  }

  public void testUnequal() {
    check("h \\neq 0", //
        "h!=0");
  }

  public void testPlusMinus() {
    check("\\frac{ \\pm \\sqrt{5^{2} - 4 \\cdot 1 \\cdot 2}}{2\\left(1\\right)}", //
        "(±Sqrt((-1)*2*4+5^2))/(2*1)");

    check("\\frac{ - 5 \\pm \\sqrt{5^{2} - 4 \\cdot 1 \\cdot 2}}{2\\left(1\\right)}", //
        "(-5±Sqrt((-1)*2*4+5^2))/(2*1)");
  }

  public void testDegreeO() {
    check("\\sin \\left( - 330\\right)^{o}", //
        "Sin(-330)^o");
    check("\\sin^{o} \\left( - 330\\right)", //
        "Sin(-330)^o");
  }
  public void check(String strEval, String strResult) {
    IExpr expr = texConverter.parse(strEval);
    assertEquals(expr.toString(), strResult);
  }

  public void checkFullForm(String strEval, String strResult) {
    IExpr expr = texConverter.parse(strEval);
    assertEquals(expr.fullFormString(), strResult);
  }

  public void checkEval(String strEval, String strResult) {
    EvalEngine engine = EvalEngine.get();
    EvalEngine.setReset(engine);
    IExpr expr = texConverter.parse(strEval);
    expr = engine.evaluate(expr);
    assertEquals(expr.toString(), strResult);
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    try {
      // F.initSymbols();
      F.await();
      EvalEngine.get().setRelaxedSyntax(true);
      texConverter = new TeXSliceParser();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
