package org.matheclipse.io.form.tex;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.tex.TeXParser;
import org.matheclipse.core.interfaces.IExpr;
import junit.framework.TestCase;

/** Tests LaTeX import (parsing) function */
public class TeXConverterTestCase extends TestCase {

  TeXParser texConverter;

  public TeXConverterTestCase(String name) {
    super(name);
  }

  public void testTeX001() {
    check(
        "x^2 + (a+b)x + ab", //
        "a*b+(a+b)*x+x^2");
    check(
        "(2x^3 - x + z)", //
        "-x+2*x^3+z");
    check(
        "(2x^3 - 3*x + 4.5z)", //
        "-3*x+2*x^3+4.5*z");
    check(
        "x^{3}", //
        "x^3");
    check(
        "\\frac{x}{\\sqrt{5}}", //
        "x/Sqrt(5)");
  }

  public void testTeX002() {
    check(
        "1+y*z", //
        "1+y*z");
  }

  public void testTeX003() {
    check(
        "(1+y)*z", //
        "(1+y)*z");
  }

  public void testTeX004() {
    check(
        "a = bq + r", //
        "a==b*q+r");
    check(
        "f(x)=(x+a)(x+b)", //
        "f(x)==(a+x)*(b+x)");
    check(
        "(x+a)(x+b)=f(x)", //
        "(a+x)*(b+x)==f(x)");

    check(
        "x=\\frac{1+y}{1+2*z^2}", //
        "x==(1+y)/(1+2*z^2)");
  }

  public void testTeX005() {
    check(
        "f(x,1+y)", //
        "f(x,1+y)");
  }

  public void testTeX006() {
    check(
        "\\sin 30 ^ { \\circ }", //
        "Sin(30*Degree)");
  }

  public void testTeX007() {
    check(
        "\\sin  \\frac { \\pi } { 2 }", //
        "Sin(Pi*1/2)");
    check(
        "f(  \\frac { \\pi } { 2 } )", //
        "f(Pi*1/2)");
    // this will print an error message in the console
    // \operatorname isn't supported:
    check(
        "\\operatorname{ sin } \\frac { \\pi } { 2 }", //
        "$Aborted");
  }

  public void testTeX008() {
    check(
        "1 + 2 - 3 \\times 4 \\div 5", //
        "1+2-(3*4)/5");
  }

  public void testTeX009() {
    check(
        "x ^ { 2 } + 1 = 2", //
        "1+x^2==2");
  }

  public void testTeX010() {
    check(
        "\\left ( 1+2 \\right )^{2}", //
        "(1+2)^2");
  }

  public void testTeX011() {
    check(
        "\\int_{a}^{b} \\frac{dt}{t}", //
        "Integrate(1/t,{t,a,b})");
    check(
        "\\int f(xz) \\, dxz", //
        "Integrate(f(xz),xz)");
    check(
        "\\int f(x) \\, dx", //
        "Integrate(f(x),x)");
    check(
        "\\int_0^\\infty a dx", //
        "Integrate(a,{x,0,Infinity})");
    check(
        "\\int_0^\\infty e^{-x^2} dx=\\frac{\\sqrt{\\pi}}{2}", //
        "Integrate(e^(-x^2),{x,0,Infinity})==Sqrt(Pi)*1/2");
  }

  public void testTeX012() {
    check(
        "\\sin x", //
        "Sin(x)");
  }

  public void testTeX013() {
    check(
        "\\exp x", //
        "Exp(x)");
  }

  public void testTeX014() {
    check(
        "\\sum_{i = 1}^{n} i", //
        "Sum(i,{i,1,n})");
    check(
        "\\sum_{n=1}^{\\infty} 2^{-n} = 1", //
        "Sum(2^(-n),{n,1,Infinity})==1");
  }

  public void testTeX015() {
    check(
        "\\prod_{i=a}^{b} f(i)", //
        "Product(f(i),{i,a,b})");
  }

  public void testTeX016() {
    check(
        "\\frac{x}{\\sqrt{0.75}}", //
        "x/Sqrt(0.75)");
  }

  public void testTeX017() {
    check(
        "a\\leq b \\le c", //
        "a<=b<=c");
  }

  public void testTeX018() {
    check(
        "a\\geq b \\ge c", //
        "a>=b>=c");
  }

  public void testTeX019() {
    check(
        "a < b < c", //
        "a<b<c");
  }

  public void testTeX020() {
    check(
        "a > b > c", //
        "a>b>c");
  }

  public void testTeX021() {
    check(
        "((a \\lor \\lnot b) \\land (c\\wedge a))", //
        "(a||!b)&&c&&a");
  }

  public void testTeX022() {
    // Rightarrow
    check(
        "((a \\lor \\neg b) \\land (c\\Rightarrow a))", //
        "(a||!b)&&(c⇒a)");

    // vs rightarrow
    check(
        "((a \\lor \\neg b) \\land (c\\rightarrow a))", //
        "(a||!b)&&(c->a)");
  }

  public void testTeX023() {
    check(
        "a \\to b", //
        "a->b");
  }

  public void testTeX024() {
    check(
        "a \\Leftrightarrow b", //
        "a⇔b");
    check(
        "a \\equiv b", //
        "a⇔b");
  }

  public void testTeX025() {
    check(
        "a!", //
        "a!");
  }

  public void testTeX026() {
    check(
        "\\cosh ^{-1}(a)", //
        "ArcCosh(a)");
    check(
        "\\tan (a)", //
        "Tan(a)");
  }

  public void testTeX027() {
    check(
        "f'(x)", //
        "f'(x)");
  }

  public void testTeX028() {
    check(
        "\\arccos(x)", //
        "ArcCos(x)");
  }

  public void testTeX029() {
    check(
        "\\ln(x)", //
        "Log(x)");
  }

  public void testTeX030() {
    check(
        "a\\in \\mathbb{B}", //
        "a∈Booleans");
    check(
        "a\\in \\mathbb{C}", //
        "a∈Complexes");
    check(
        "a\\in \\mathbb{P}", //
        "a∈Primes");
    check(
        "a\\in \\mathbb{Q}", //
        "a∈Rationals");
    check(
        "a\\in \\mathbb{Z}", //
        "a∈Integers");
    check(
        "a\\in \\mathbb{R}", //
        "a∈Reals");
  }

  public void testTeX031() {
    // D(x^2, x)
    check(
        "\\frac{d}{dx} x^{2}", //
        "D(x^2,x)");
    checkEval(
        "\\frac{d}{dx} x^{2}", //
        "2*x");
  }

  public void testTeX032() {
    check(
        "L' = {L}{\\sqrt{1-\\frac{v^2}{c^2}}}", //
        "Derivative(1)[L]==Sqrt(1-v^2/c^2)*L");
  }

  public void testTeX033() {
    check(
        "-\\nabla \\times e", //
        "-e*∇");
    check(
        "\\nabla \\times B - 4\\pi j", //
        "B*∇-4*j*Pi");
  }

  public void testTeX034() {
    check(
        "\\lim_{x\\to 0}{\\frac{E^x-1}{2x}}", //
        "Limit((E^x+(-1)*1)/(2*x),x->0)");
    checkEval(
        "\\lim_{x\\to 0}{\\frac{E^x-1}{2x}}", //
        "1/2");

    // only "upper case" E is interpreted as euler's constant
    check(
        "\\lim_{x\\to 0}{\\frac{e^x-1}{2x}}", //
        "Limit((e^x+(-1)*1)/(2*x),x->0)");
    checkEval(
        "\\lim_{x\\to 0}{\\frac{e^x-1}{2x}}", //
        "Log(e)/2");
  }

  public void check(String strEval, String strResult) {
    IExpr expr = texConverter.toExpression(strEval);
    assertEquals(expr.toString(), strResult);
  }

  public void checkEval(String strEval, String strResult) {
    EvalEngine engine = EvalEngine.get();
    EvalEngine.setReset(engine);
    IExpr expr = texConverter.toExpression(strEval);
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
      texConverter = new TeXParser(EvalEngine.get());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
